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
        setContentView(R.layout.activity_main);

        tvHighScore = findViewById(R.id.tvHighScore);
        Button btnStart = findViewById(R.id.btnStart);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnSensors = findViewById(R.id.btnSensors);

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
            // implicit intent: share best score
            if (score == highScore) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I scored " + score + " in WaterCatcher!");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
            }
        }
    }
}
