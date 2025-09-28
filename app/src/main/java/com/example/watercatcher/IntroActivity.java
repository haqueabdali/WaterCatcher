package com.example.watercatcher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        TextView storyText = findViewById(R.id.storyText);
        Button startButton = findViewById(R.id.startButton);

        // 🐟 Set the story text
        storyText.setText(
                "🐟 Oh no! Your little fish jumped out of her bowl and landed in a jar.\n\n" +
                        "But the jar is empty, and she needs water fast! 💧\n\n" +
                        "She’s looking at you with big, round eyes:\n" +
                        "“Please, give me water before I dry out… hurry!”"
        );

        // 🚀 Start the game when button is pressed
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, GameActivity.class);
            startActivity(intent);
            finish(); // close intro so back button won’t return here
        });
    }
}