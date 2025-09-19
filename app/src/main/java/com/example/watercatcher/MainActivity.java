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

    private int dropSize = 30;
    private int sensitivity = 5;
    private TextView tvHighScore;
    private int highScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure this layout name matches your XML file name from Step 1
        setContentView(R.layout.activity_main);

        tvHighScore = findViewById(R.id.tvHighScore);
        Button btnStart = findViewById(R.id.btnStart);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnSensors = findViewById(R.id.btnSensors);
        Button btnExit = findViewById(R.id.btnExit); // Find the Exit button by its ID

        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        highScore = sp.getInt("highScore", 0);
        updateHighScoreLabel();

        btnStart.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, GameActivity.class);
            i.putExtra("dropSize", dropSize);
            i.putExtra("sensitivity", sensitivity);
            startActivityForResult(i, REQ_GAME);
        });

        btnSettings.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            i.putExtra("dropSize", dropSize);
            i.putExtra("sensitivity", sensitivity);
            startActivityForResult(i, REQ_SETTINGS);
        });

        btnSensors.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SensorInfoActivity.class));
        });

        // Exit Button Logic
        btnExit.setOnClickListener(v -> {
            finishAffinity(); // Finishes this activity and all activities immediately below it
            // in the current task that have the same affinity.
            // Effectively closes the app if this is the main task.
            // System.exit(0); // For a more forceful JVM exit, if absolutely necessary.
        });
    }

    private void updateHighScoreLabel() {
        tvHighScore.setText("High Score: " + highScore);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;
        if (requestCode == REQ_SETTINGS) {
            dropSize = data.getIntExtra("dropSize", dropSize);
            sensitivity = data.getIntExtra("sensitivity", sensitivity);
        } else if (requestCode == REQ_GAME) {
            int score = data.getIntExtra("score", 0);
            boolean win = data.getBooleanExtra("win", false);
            if (score > highScore) {
                highScore = score;
                getSharedPreferences(PREFS, MODE_PRIVATE).edit().putInt("highScore", highScore).apply();
                updateHighScoreLabel();
            }
            if (win) {
                Toast.makeText(this, "🎉 You win! Score: " + score, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "⏰ Time over! Score: " + score, Toast.LENGTH_LONG).show();
            }
        }
    }
}