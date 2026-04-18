package com.icegot.simuladorbombainfusao;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.icegot.simuladorbombainfusao.viewmodel.InfusionViewModel;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private InfusionViewModel viewModel;
    private TextView tvRateValue, tvInfusedValue, tvTimeRemaining;
    private Button btnStart, btnStop, btnIncrease, btnDecrease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Inicializar Componentes da UI
        initUI();

        // 2. Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(InfusionViewModel.class);

        // 3. Configurar Observadores (LCD Display)
        setupObservers();

        // 4. Configurar Cliques dos Botões
        setupClickListeners();

        // Valores Iniciais de Exemplo (Podem vir do Banco de Dados depois)
        viewModel.setRate(100.0f);
        viewModel.setVtbi(500.0f);
    }

    private void initUI() {
        tvRateValue = findViewById(R.id.tvRateValue);
        tvInfusedValue = findViewById(R.id.tvInfusedValue);
        tvTimeRemaining = findViewById(R.id.tvTimeRemaining);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
    }

    private void setupObservers() {
        // Observa a Vazão
        viewModel.getRate().observe(this, rate -> 
            tvRateValue.setText(String.format(Locale.getDefault(), "%.1f", rate)));

        // Observa o Volume Infundido
        viewModel.getInfusedVolume().observe(this, infused -> 
            tvInfusedValue.setText(String.format(Locale.getDefault(), "%.1f ml", infused)));

        // Observa o Tempo Restante
        viewModel.getTimeRemaining().observe(this, time -> 
            tvTimeRemaining.setText(time));

        // Observa se está infundindo para habilitar/desabilitar botões
        viewModel.getIsInfusing().observe(this, isInfusing -> {
            btnStart.setEnabled(!isInfusing);
            btnStop.setEnabled(isInfusing);
            btnIncrease.setEnabled(!isInfusing);
            btnDecrease.setEnabled(!isInfusing);
        });
    }

    private void setupClickListeners() {
        btnStart.setOnClickListener(v -> viewModel.startInfusion());
        
        btnStop.setOnClickListener(v -> viewModel.stopInfusion());

        btnIncrease.setOnClickListener(v -> {
            float currentRate = viewModel.getRate().getValue() != null ? viewModel.getRate().getValue() : 0;
            viewModel.setRate(currentRate + 10.0f);
        });

        btnDecrease.setOnClickListener(v -> {
            float currentRate = viewModel.getRate().getValue() != null ? viewModel.getRate().getValue() : 0;
            if (currentRate >= 10.0f) {
                viewModel.setRate(currentRate - 10.0f);
            }
        });
    }
}