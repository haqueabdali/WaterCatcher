package com.example.watercatcher;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class LoseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose);

        ImageView sadJar = findViewById(R.id.loseJar);

        // Fade animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        sadJar.startAnimation(fadeIn);

        // After 3 seconds, return to MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(LoseActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
