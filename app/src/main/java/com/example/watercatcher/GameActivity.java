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

    // drop settings
    private static final int DROP_SIZE = 60;
    private static final int DROP_SPEED = 40;  // higher = faster falling

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameLayout = findViewById(R.id.gameLayout);
        jar = findViewById(R.id.jar);
        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);

        startGame();

        // 🟢 Spawn drop at touch location (X & Y)
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

        // Start countdown
        countDownTimer = new CountDownTimer(GAME_DURATION, 16) { // ~60fps
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

        // Animate jar horizontally every second (auto-move)
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

    // 🟢 Drop spawns where you touch (X & Y coordinate)
    private void spawnDrop(int touchX, int touchY) {
        if (gameLayout.getWidth() == 0 || gameLayout.getHeight() == 0) return;

        ImageView drop = new ImageView(this);
        drop.setImageResource(R.drawable.ic_water_drop); // make sure this drawable exists
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DROP_SIZE, DROP_SIZE);
        drop.setLayoutParams(params);

        // Clamp X so it doesn’t go outside screen
        int startX = touchX - (DROP_SIZE / 2);
        if (startX < 0) startX = 0;
        if (startX > gameLayout.getWidth() - DROP_SIZE) {
            startX = gameLayout.getWidth() - DROP_SIZE;
        }

        // Clamp Y so it doesn’t go outside screen
        int startY = touchY - (DROP_SIZE / 2);
        if (startY < 0) startY = 0;
        if (startY > gameLayout.getHeight() - DROP_SIZE) {
            startY = gameLayout.getHeight() - DROP_SIZE;
        }

        drop.setX(startX);
        drop.setY(startY); // 🟢 now spawns exactly at touch

        gameLayout.addView(drop);
        drops.add(drop);
    }

    private void updateDrops() {
        Iterator<ImageView> iterator = drops.iterator();
        while (iterator.hasNext()) {
            ImageView drop = iterator.next();
            drop.setY(drop.getY() + DROP_SPEED);

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
        if (score >= WIN_SCORE) {
            intent = new Intent(GameActivity.this, WinActivity.class);
        } else {
            intent = new Intent(GameActivity.this, LoseActivity.class);
        }
        intent.putExtra("finalScore", score);
        startActivity(intent);
        finish();
    }
}
