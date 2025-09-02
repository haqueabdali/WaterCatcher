package com.example.watercatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class SensorInfoActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accel, light;
    private TextView tvAccel, tvLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_info);

        tvAccel = findViewById(R.id.tvAccel);
        tvLight = findViewById(R.id.tvLight);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accel != null) sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
        if (light != null) sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(android.hardware.SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            tvAccel.setText(String.format("Accelerometer: x=%.2f y=%.2f z=%.2f", event.values[0], event.values[1], event.values[2]));
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            tvLight.setText(String.format("Light: %.2f lx", event.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
