package com.icegot.simuladorbombainfusao;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.icegot.simuladorbombainfusao.viewmodel.InfusionViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int MENU_REQUEST_CODE = 1001;
    private InfusionViewModel viewModel;
    private EditText etRateValue, etVtbiValue;
    private TextView tvInfusedValue, tvTimeRemaining, tvClock, tvFlowArrows, tvStartStop;
    private ImageView ivStartStop;
    private View btnStartStopContainer, btnBolus, btnMenu, centralPanel;
    private ToneGenerator toneGenerator;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean arrowsVisible = true;
    private float previousRate = 52.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        
        setContentView(R.layout.activity_main);
        hideSystemUI();

        toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        initUI();
        viewModel = new ViewModelProvider(this).get(InfusionViewModel.class);
        setupObservers();
        setupClickListeners();
        startClock();
    }

    private void hideSystemUI() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private void initUI() {
        etRateValue = findViewById(R.id.etRateValue);
        etVtbiValue = findViewById(R.id.etVtbiValue);
        tvInfusedValue = findViewById(R.id.tvInfusedValue);
        tvTimeRemaining = findViewById(R.id.tvTimeRemaining);
        tvClock = findViewById(R.id.tvClock);
        tvFlowArrows = findViewById(R.id.tvFlowArrows);
        tvStartStop = findViewById(R.id.tvStartStop);
        ivStartStop = findViewById(R.id.ivStartStop);
        btnStartStopContainer = findViewById(R.id.btnStartStopContainer);
        btnBolus = findViewById(R.id.btnBolus);
        btnMenu = findViewById(R.id.btnMenu);
        centralPanel = findViewById(R.id.centralPanel);
        
        tvFlowArrows.setVisibility(View.INVISIBLE);

        etRateValue.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String input = etRateValue.getText().toString();
                if (input.isEmpty()) {
                    viewModel.setRate(0.0f);
                    etRateValue.setText("0.00");
                } else {
                    try {
                        float rate = Float.parseFloat(input);
                        viewModel.setRate(rate);
                        etRateValue.setText(String.format(Locale.US, "%.2f", rate));
                    } catch (NumberFormatException ignored) {
                        etRateValue.setText("0.00");
                    }
                }
                hideSystemUI();
            }
        });

        etVtbiValue.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String input = etVtbiValue.getText().toString();
                if (input.isEmpty()) {
                    viewModel.setVtbi(0.0f);
                    etVtbiValue.setText("0.0");
                } else {
                    try {
                        float vtbi = Float.parseFloat(input);
                        viewModel.setVtbi(vtbi);
                        etVtbiValue.setText(String.format(Locale.US, "%.1f", vtbi));
                    } catch (NumberFormatException ignored) {
                        etVtbiValue.setText("0.0");
                    }
                }
                hideSystemUI();
            }
        });

        etRateValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && etRateValue.hasFocus()) {
                    try {
                        float rate = Float.parseFloat(s.toString());
                        viewModel.setRate(rate);
                    } catch (NumberFormatException ignored) {}
                }
            }
        });

        etVtbiValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && etVtbiValue.hasFocus()) {
                    try {
                        float vtbi = Float.parseFloat(s.toString());
                        viewModel.setVtbi(vtbi);
                    } catch (NumberFormatException ignored) {}
                }
            }
        });
    }

    private void startClock() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                tvClock.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
                handler.postDelayed(this, 1000);
            }
        });
    }

    private final Runnable arrowAnimation = new Runnable() {
        @Override
        public void run() {
            arrowsVisible = !arrowsVisible;
            tvFlowArrows.setVisibility(arrowsVisible ? View.VISIBLE : View.INVISIBLE);
            handler.postDelayed(this, 500);
        }
    };

    private void setupObservers() {
        viewModel.getRate().observe(this, rate -> {
            String newVal = String.format(Locale.US, "%.2f", rate);
            if (!etRateValue.hasFocus()) {
                etRateValue.setText(newVal);
            }
        });

        viewModel.getVtbi().observe(this, vtbi -> {
            String newVal = String.format(Locale.US, "%.1f", vtbi);
            if (!etVtbiValue.hasFocus()) {
                etVtbiValue.setText(newVal);
            }
        });

        viewModel.getInfusedVolume().observe(this, infused -> 
            tvInfusedValue.setText(String.format(Locale.getDefault(), "%.1f", infused)));

        viewModel.getTimeRemaining().observe(this, time -> 
            tvTimeRemaining.setText(time));

        viewModel.getIsInfusing().observe(this, isInfusing -> {
            if (isInfusing) {
                ivStartStop.setImageResource(android.R.drawable.ic_media_pause);
                tvStartStop.setText("PARAR");
                handler.removeCallbacks(arrowAnimation);
                handler.post(arrowAnimation);
                etRateValue.setEnabled(false);
                etVtbiValue.setEnabled(false);
                centralPanel.setBackgroundResource(R.drawable.bg_gradient_light_blue);
            } else {
                ivStartStop.setImageResource(android.R.drawable.ic_media_play);
                tvStartStop.setText("INICIAR");
                handler.removeCallbacks(arrowAnimation);
                tvFlowArrows.setVisibility(View.INVISIBLE);
                etRateValue.setEnabled(true);
                etVtbiValue.setEnabled(true);
            }
        });

        viewModel.getIsComplete().observe(this, isComplete -> {
            if (isComplete) {
                centralPanel.setBackgroundColor(Color.parseColor("#FF5252")); // lifemed_alarm
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 1000);
                Toast.makeText(this, "ALERTA: Infusão Finalizada!", Toast.LENGTH_LONG).show();
            } else if (!Boolean.TRUE.equals(viewModel.getIsInfusing().getValue())) {
                centralPanel.setBackgroundResource(R.drawable.bg_gradient_light_blue);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupClickListeners() {
        btnStartStopContainer.setOnClickListener(v -> {
            Boolean isInfusing = viewModel.getIsInfusing().getValue();
            if (Boolean.TRUE.equals(isInfusing)) {
                viewModel.stopInfusion();
            } else {
                Float currentRate = viewModel.getRate().getValue();
                Float currentVtbi = viewModel.getVtbi().getValue();
                
                if (currentRate == null || currentRate <= 0) {
                    Toast.makeText(this, "Defina a vazão", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currentVtbi == null || currentVtbi <= 0) {
                    Toast.makeText(this, "Defina o VAI (VTBI)", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                viewModel.startInfusion();
            }
        });

        btnBolus.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                previousRate = viewModel.getRate().getValue() != null ? viewModel.getRate().getValue() : 0;
                viewModel.setRate(999.0f);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                viewModel.setRate(previousRate);
                return true;
            }
            return false;
        });

        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivityForResult(intent, MENU_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MENU_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            float rate = data.getFloatExtra("selected_drug_rate", 0.0f);
            String name = data.getStringExtra("selected_drug_name");
            viewModel.setRate(rate);
            viewModel.resetInfusion();
            Toast.makeText(this, "Medicamento: " + name, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (toneGenerator != null) {
            toneGenerator.release();
        }
    }
}