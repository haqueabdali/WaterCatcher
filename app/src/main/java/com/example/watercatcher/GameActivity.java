package com.example.watercatcher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private FrameLayout gameLayout;
    private ImageView jar;
    private TextView scoreText, timerText;

    private int score = 0;
    private ArrayList<ImageView> drops = new ArrayList<>();
    private Random random = new Random();

    private CountDownTimer countDownTimer;
    private boolean gameRunning = true;

    private static final int GAME_DURATION = 10000; // 10 seconds
    private static final int WIN_SCORE = 100;

    // settings
    private int dropSize;   // from Settings
    private int dropSpeed;  // based on sensitivity

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameLayout = findViewById(R.id.gameLayout);
        jar = findViewById(R.id.jar);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);

        // 🎛 get settings from MainActivity
        dropSize = getIntent().getIntExtra("dropSize", 60);
        int sensitivity = getIntent().getIntExtra("sensitivity", 5);

        // map sensitivity → drop speed (higher = faster)
        dropSpeed = Math.max(5, sensitivity * 10);

        startGame();

        // 🟢 spawn drop at touch location
        gameLayout.setOnTouchListener((v, event) -> {
            if ((event.getAction() == MotionEvent.ACTION_DOWN ||
                    event.getAction() == MotionEvent.ACTION_MOVE) && gameRunning) {
                spawnDrop((int) event.getX(), (int) event.getY());
            }
            return true;
        });
    }

    private void startGame() {
        score = 0;
        scoreText.setText("Score: 0");
        gameRunning = true;

        // countdown ~60fps
        countDownTimer = new CountDownTimer(GAME_DURATION, 16) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                timerText.setText("Time: " + seconds + "s");
                updateDrops();
            }

            @Override
            public void onFinish() {
                gameRunning = false;
                endGame();
            }
        }.start();

        // animate jar horizontally every second
        jar.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (gameRunning) {
                    float maxX = gameLayout.getWidth() - jar.getWidth();
                    float newX = random.nextInt((int) Math.max(1, maxX));
                    jar.animate().x(newX).setDuration(800).start();
                    jar.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    // 🟢 spawn drop where touched
    private void spawnDrop(int touchX, int touchY) {
        if (gameLayout.getWidth() == 0 || gameLayout.getHeight() == 0) return;

        ImageView drop = new ImageView(this);
        drop.setImageResource(R.drawable.ic_water_drop); // make sure drawable exists
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dropSize, dropSize);
        drop.setLayoutParams(params);

        // clamp X
        int startX = touchX - (dropSize / 2);
        if (startX < 0) startX = 0;
        if (startX > gameLayout.getWidth() - dropSize) {
            startX = gameLayout.getWidth() - dropSize;
        }

        // clamp Y
        int startY = touchY - (dropSize / 2);
        if (startY < 0) startY = 0;
        if (startY > gameLayout.getHeight() - dropSize) {
            startY = gameLayout.getHeight() - dropSize;
        }

        drop.setX(startX);
        drop.setY(startY);

        gameLayout.addView(drop);
        drops.add(drop);
    }

    private void updateDrops() {
        Iterator<ImageView> iterator = drops.iterator();
        while (iterator.hasNext()) {
            ImageView drop = iterator.next();
            drop.setY(drop.getY() + dropSpeed);

            if (checkCollision(drop, jar)) {
                score++;
                scoreText.setText("Score: " + score);
                gameLayout.removeView(drop);
                iterator.remove();
            } else if (drop.getY() > gameLayout.getHeight()) {
                gameLayout.removeView(drop);
                iterator.remove();
            }
        }
    }

    private boolean checkCollision(View drop, View jar) {
        Rect dropRect = new Rect(
                (int) drop.getX(),
                (int) drop.getY(),
                (int) drop.getX() + drop.getWidth(),
                (int) drop.getY() + drop.getHeight()
        );

        Rect jarRect = new Rect(
                (int) jar.getX(),
                (int) jar.getY(),
                (int) jar.getX() + jar.getWidth(),
                (int) jar.getY() + jar.getHeight()
        );

        return Rect.intersects(dropRect, jarRect);
    }

    private void endGame() {
        Intent intent;
        boolean win = score >= WIN_SCORE;

        if (win) {
            intent = new Intent(GameActivity.this, WinActivity.class);
        } else {
            intent = new Intent(GameActivity.this, LoseActivity.class);
        }
        intent.putExtra("score", score);
        intent.putExtra("win", win);

        setResult(RESULT_OK, intent);
        startActivity(intent);
        finish();
    }
}
