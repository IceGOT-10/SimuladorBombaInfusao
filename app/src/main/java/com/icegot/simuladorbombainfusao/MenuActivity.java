package com.icegot.simuladorbombainfusao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.icegot.simuladorbombainfusao.model.Drug;
import com.icegot.simuladorbombainfusao.viewmodel.InfusionViewModel;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private InfusionViewModel viewModel;
    private List<Drug> drugList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        
        setContentView(R.layout.activity_menu);
        hideSystemUI();

        viewModel = new ViewModelProvider(this).get(InfusionViewModel.class);

        viewModel.getAllDrugs().observe(this, drugs -> {
            if (drugs != null) this.drugList = drugs;
        });

        findViewById(R.id.cardAlerts).setOnClickListener(v -> 
            Toast.makeText(this, "Configurações de Alertas em breve", Toast.LENGTH_SHORT).show());

        findViewById(R.id.cardDrugs).setOnClickListener(v -> showDrugSelectionDialog());

        findViewById(R.id.cardBack).setOnClickListener(v -> finish());
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

    private void showDrugSelectionDialog() {
        if (drugList.isEmpty()) {
            Toast.makeText(this, "Nenhum medicamento cadastrado", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] names = new String[drugList.size()];
        for (int i = 0; i < drugList.size(); i++) names[i] = drugList.get(i).getName();

        new AlertDialog.Builder(this)
                .setTitle("Selecionar Medicamento")
                .setItems(names, (dialog, which) -> {
                    Drug selected = drugList.get(which);
                    
                    // Retorna o resultado para a MainActivity
                    Intent intent = new Intent();
                    intent.putExtra("selected_drug_rate", (float) selected.getDefaultRate());
                    intent.putExtra("selected_drug_name", selected.getName());
                    setResult(RESULT_OK, intent);
                    finish();
                })
                .show();
    }
}