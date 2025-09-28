package com.example.watercatcher;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private SeekBar seekDrop, seekSens;
    private TextView txtDrop, txtSens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        seekDrop = findViewById(R.id.seekDropSize);
        seekSens = findViewById(R.id.seekSensitivity);
        txtDrop = findViewById(R.id.txtDropValue);
        txtSens = findViewById(R.id.txtSensValue);

        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        int dropSize = getIntent().getIntExtra("dropSize", 60);
        int sensitivity = getIntent().getIntExtra("sensitivity", 5);

        // Configure SeekBars
        seekDrop.setMax(200);      // up to 200px for drop size
        seekSens.setMax(20);       // sensitivity 1–20

        seekDrop.setProgress(dropSize);
        seekSens.setProgress(sensitivity);

        txtDrop.setText("Drop Size: " + dropSize + "px");
        txtSens.setText("Sensitivity: " + sensitivity);

        // Live update labels
        seekDrop.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtDrop.setText("Drop Size: " + progress + "px");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekSens.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtSens.setText("Sensitivity: " + progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnSave.setOnClickListener(v -> {
            Intent data = new Intent();
            data.putExtra("dropSize", seekDrop.getProgress());
            data.putExtra("sensitivity", seekSens.getProgress());
            setResult(RESULT_OK, data);
            finish();
        });

        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
