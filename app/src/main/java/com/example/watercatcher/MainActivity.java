package com.example.watercatcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS = "water_prefs";
    private static final int REQ_SETTINGS = 100;
    private static final int REQ_GAME = 200;

    private int dropSize = 60;     // default drop size
    private int sensitivity = 5;   // default sensitivity
    private int highScore = 0;

    private TextView tvHighScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHighScore = findViewById(R.id.tvHighScore);
        Button btnStart = findViewById(R.id.btnStart);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnSensors = findViewById(R.id.btnSensors);
        Button btnExit = findViewById(R.id.btnExit);

        // Load saved high score
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        highScore = sp.getInt("highScore", 0);
        updateHighScoreLabel();

        // Start Game
        btnStart.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra("dropSize", dropSize);
            i.putExtra("sensitivity", sensitivity);
            startActivityForResult(i, REQ_GAME);
        });

        // Open Settings
        btnSettings.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            i.putExtra("dropSize", dropSize);
            i.putExtra("sensitivity", sensitivity);
            startActivityForResult(i, REQ_SETTINGS);
        });

        // Open Sensors info
        btnSensors.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SensorInfoActivity.class));
        });

        // Exit App
        btnExit.setOnClickListener(v -> finishAffinity());
    }

    private void updateHighScoreLabel() {
        tvHighScore.setText("High Score: " + highScore);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == REQ_SETTINGS) {
            // Settings returned
            dropSize = data.getIntExtra("dropSize", dropSize);
            sensitivity = data.getIntExtra("sensitivity", sensitivity);

        } else if (requestCode == REQ_GAME) {
            // Game finished
            int score = data.getIntExtra("finalScore", 0);

            if (score > highScore) {
                highScore = score;
                getSharedPreferences(PREFS, MODE_PRIVATE)
                        .edit()
                        .putInt("highScore", highScore)
                        .apply();
                updateHighScoreLabel();
            }

            if (score >= 100) {
                Toast.makeText(this, "🎉 You win! Score: " + score, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "⏰ Time over! Score: " + score, Toast.LENGTH_LONG).show();
            }
        }
    }
}
