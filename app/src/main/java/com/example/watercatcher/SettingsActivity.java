package com.example.watercatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;

public class SettingsActivity extends AppCompatActivity {

    private SeekBar seekDrop, seekSens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        seekDrop = findViewById(R.id.seekDropSize);
        seekSens = findViewById(R.id.seekSensitivity);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        int dropSize = getIntent().getIntExtra("dropSize", 30);
        int sensitivity = getIntent().getIntExtra("sensitivity", 5);

        seekDrop.setProgress(dropSize);
        seekSens.setProgress(sensitivity);

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
