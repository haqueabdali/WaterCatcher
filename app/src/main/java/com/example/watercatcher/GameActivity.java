package com.example.watercatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private WaterDropView waterView;
    private TextView tvScore;
    private SensorManager sensorManager;
    private Sensor accel, light;
    private CountDownTimer timer;
    private boolean ended = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        waterView = findViewById(R.id.waterView);
        tvScore = findViewById(R.id.tvScore);

        int dropSize = getIntent().getIntExtra("dropSize", 10);
        int sensitivity = getIntent().getIntExtra("sensitivity", 10);
        waterView.configure(dropSize, sensitivity,
                score -> runOnUiThread(() -> tvScore.setText("Score: " + score )),
                this::winAndFinish);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        startTimer();
    }

    private void startTimer() {
        if (timer != null) timer.cancel();
        timer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                int secs = (int)(millisUntilFinished / 1000);
                tvScore.setText("Score: " + waterView.getScore() + " | Time: " + secs);
            }
            public void onFinish() {
                if (!ended) loseAndFinish();
            }
        }.start();
    }

    private void winAndFinish(int score) {
        endGame(true, score);
    }

    private void loseAndFinish() {
        endGame(false, waterView.getScore());
    }

    private void endGame(boolean win, int score) {
        if (ended) return;
        ended = true;
        if (score >= 100) { // Assuming 100 is the winning condition, adjust as needed
            Intent winIntent = new Intent(GameActivity.this, WinActivity.class);
            winIntent.putExtra("finalScore", score); // Pass the score
            startActivity(winIntent);
        } else {
            Intent loseIntent = new Intent(GameActivity.this, LoseActivity.class);
            loseIntent.putExtra("finalScore", score); // Pass the score
            startActivity(loseIntent);
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        waterView.resume();
        if (accel != null) sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
        if (light != null) sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        waterView.pause();
        sensorManager.unregisterListener(this);
        if (timer != null) timer.cancel();
    }

    @Override
    public void onSensorChanged(android.hardware.SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            waterView.setTilt(-event.values[0], event.values[1]);
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            waterView.setAmbientLight(event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
