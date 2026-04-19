package com.icegot.simuladorbombainfusao.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.icegot.simuladorbombainfusao.dao.DrugDao;
import com.icegot.simuladorbombainfusao.database.AppDatabase;
import com.icegot.simuladorbombainfusao.model.Drug;
import java.util.List;
import java.util.Locale;

public class InfusionViewModel extends AndroidViewModel {

    private final DrugDao drugDao;
    private final LiveData<List<Drug>> allDrugs;

    private final MutableLiveData<Float> rate = new MutableLiveData<>(0.0f);
    private final MutableLiveData<Float> vtbi = new MutableLiveData<>(500.0f);
    private final MutableLiveData<Float> infusedVolume = new MutableLiveData<>(0.0f);
    private final MutableLiveData<Boolean> isInfusing = new MutableLiveData<>(false);
    private final MutableLiveData<String> timeRemaining = new MutableLiveData<>("00:00:00");
    private final MutableLiveData<Boolean> isComplete = new MutableLiveData<>(false);

    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int UPDATE_INTERVAL_MS = 1000;

    private final Runnable infusionRunnable = new Runnable() {
        @Override
        public void run() {
            if (Boolean.TRUE.equals(isInfusing.getValue())) {
                calculateNextStep();
                handler.postDelayed(this, UPDATE_INTERVAL_MS);
            }
        }
    };

    public InfusionViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        drugDao = db.drugDao();
        allDrugs = drugDao.getAllDrugs();
    }

    public LiveData<List<Drug>> getAllDrugs() { return allDrugs; }
    public LiveData<Float> getRate() { return rate; }
    public LiveData<Float> getVtbi() { return vtbi; }
    public LiveData<Float> getInfusedVolume() { return infusedVolume; }
    public LiveData<Boolean> getIsInfusing() { return isInfusing; }
    public LiveData<String> getTimeRemaining() { return timeRemaining; }
    public LiveData<Boolean> getIsComplete() { return isComplete; }

    public void setRate(float value) { rate.setValue(value); updateTimeRemaining(); }
    public void setVtbi(float value) { vtbi.setValue(value); updateTimeRemaining(); }

    public void startInfusion() {
        if (rate.getValue() != null && rate.getValue() > 0 && vtbi.getValue() != null && vtbi.getValue() > 0) {
            isInfusing.setValue(true);
            isComplete.setValue(false);
            handler.post(infusionRunnable);
        }
    }

    public void stopInfusion() {
        isInfusing.setValue(false);
        handler.removeCallbacks(infusionRunnable);
    }

    public void resetInfusion() {
        stopInfusion();
        infusedVolume.setValue(0.0f);
        isComplete.setValue(false);
        updateTimeRemaining();
    }

    private void calculateNextStep() {
        float currentInfused = infusedVolume.getValue() != null ? infusedVolume.getValue() : 0;
        float currentRate = rate.getValue() != null ? rate.getValue() : 0;
        float totalVtbi = vtbi.getValue() != null ? vtbi.getValue() : 0;

        float mlPerSecond = currentRate / 3600f;
        float nextVolume = currentInfused + mlPerSecond;

        if (nextVolume >= totalVtbi) {
            infusedVolume.setValue(totalVtbi);
            isComplete.setValue(true);
            stopInfusion();
        } else {
            infusedVolume.setValue(nextVolume);
        }
        updateTimeRemaining();
    }

    private void updateTimeRemaining() {
        float currentRate = rate.getValue() != null ? rate.getValue() : 0;
        float remainingVol = (vtbi.getValue() != null ? vtbi.getValue() : 0) - (infusedVolume.getValue() != null ? infusedVolume.getValue() : 0);

        if (currentRate <= 0 || remainingVol <= 0) {
            timeRemaining.setValue("00:00:00");
            return;
        }

        float hoursRemaining = remainingVol / currentRate;
        int totalSeconds = (int) (hoursRemaining * 3600);
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        timeRemaining.setValue(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopInfusion();
    }
}