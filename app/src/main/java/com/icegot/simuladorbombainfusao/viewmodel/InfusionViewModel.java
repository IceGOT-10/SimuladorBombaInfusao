package com.icegot.simuladorbombainfusao.viewmodel;

import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Locale;

public class InfusionViewModel extends ViewModel {

    private final MutableLiveData<Float> rate = new MutableLiveData<>(0.0f); // ml/h
    private final MutableLiveData<Float> vtbi = new MutableLiveData<>(0.0f); // Volume To Be Infused (ml)
    private final MutableLiveData<Float> infusedVolume = new MutableLiveData<>(0.0f); // ml
    private final MutableLiveData<Boolean> isInfusing = new MutableLiveData<>(false);
    private final MutableLiveData<String> timeRemaining = new MutableLiveData<>("00:00:00");

    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int UPDATE_INTERVAL_MS = 1000; // 1 segundo

    private final Runnable infusionRunnable = new Runnable() {
        @Override
        public void run() {
            if (Boolean.TRUE.equals(isInfusing.getValue())) {
                calculateNextStep();
                handler.postDelayed(this, UPDATE_INTERVAL_MS);
            }
        }
    };

    public LiveData<Float> getRate() { return rate; }
    public LiveData<Float> getVtbi() { return vtbi; }
    public LiveData<Float> getInfusedVolume() { return infusedVolume; }
    public LiveData<Boolean> getIsInfusing() { return isInfusing; }
    public LiveData<String> getTimeRemaining() { return timeRemaining; }

    public void setRate(float value) { rate.setValue(value); updateTimeRemaining(); }
    public void setVtbi(float value) { vtbi.setValue(value); updateTimeRemaining(); }

    public void startInfusion() {
        if (rate.getValue() != null && rate.getValue() > 0 && vtbi.getValue() != null && vtbi.getValue() > 0) {
            isInfusing.setValue(true);
            handler.post(infusionRunnable);
        }
    }

    public void stopInfusion() {
        isInfusing.setValue(false);
        handler.removeCallbacks(infusionRunnable);
    }

    private void calculateNextStep() {
        float currentInfused = infusedVolume.getValue() != null ? infusedVolume.getValue() : 0;
        float currentRate = rate.getValue() != null ? rate.getValue() : 0;
        float totalVtbi = vtbi.getValue() != null ? vtbi.getValue() : 0;

        // ml por segundo = (ml/h) / 3600
        float mlPerSecond = currentRate / 3600f;
        float nextVolume = currentInfused + mlPerSecond;

        if (nextVolume >= totalVtbi) {
            infusedVolume.setValue(totalVtbi);
            stopInfusion();
            // Aqui poderíamos disparar um evento de "Infusão Completa"
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