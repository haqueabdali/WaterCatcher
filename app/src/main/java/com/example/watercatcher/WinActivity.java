package com.example.watercatcher;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class WinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_win);

        ImageView trophy = findViewById(R.id.winTrophy);

        // Scale animation on trophy
        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        trophy.startAnimation(scaleUp);

        // After 3 seconds, go back to MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(WinActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
