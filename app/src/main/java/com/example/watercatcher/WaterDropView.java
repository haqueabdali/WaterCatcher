package com.example.watercatcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class WaterDropView extends View {

    private static class Drop {
        float x, y;
        float vx, vy;
        float r;
        Drop(float x, float y, float r) { this.x=x; this.y=y; this.r=r; }
    }

    private final Random random = new Random();
    private final List<Drop> drops = new ArrayList<>();
    private final Handler handler = new Handler();

    private float width, height;
    private int score = 0;

    private float jarY = 0f;
    private int jarDir = 1;
    private float jarSpeed = 2.0f;
    private int framesUntilSpeedChange = 0;

    private Consumer<Integer> onScore;
    private Consumer<Integer> onGameOver;

    // Jar image
    private Bitmap jarBitmap;
    private RectF jarBounds;

    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            step();
            invalidate();
            handler.postDelayed(this, 16);
        }
    };

    public WaterDropView(Context ctx) { super(ctx); init(); }
    public WaterDropView(Context ctx, AttributeSet attrs) { super(ctx, attrs); init(); }

    private void init() {
        jarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.jar);
    }

    public void configure(Consumer<Integer> onScore, Consumer<Integer> onGameOver) {
        this.onScore = onScore;
        this.onGameOver = onGameOver;
    }

    public void resume() { handler.post(ticker); }
    public void pause() { handler.removeCallbacks(ticker); }

    private void spawnDrop(float x, float y) {
        float r = 20 + random.nextFloat()*10;
        drops.add(new Drop(x, y, r));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            spawnDrop(event.getX(), event.getY());
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void step() {
        // Move jar up and down randomly
        if (framesUntilSpeedChange <= 0) {
            framesUntilSpeedChange = 60 + random.nextInt(120);
            jarSpeed = 1.5f + random.nextFloat() * 2.5f;
            if (random.nextFloat() < 0.3f) jarDir *= -1;
        } else {
            framesUntilSpeedChange--;
        }
        jarY += jarSpeed * jarDir;
        if (jarY > 60) { jarY = 60; jarDir = -1; }
        if (jarY < -60) { jarY = -60; jarDir = 1; }

        Iterator<Drop> it = drops.iterator();
        while (it.hasNext()) {
            Drop d = it.next();
            d.vy += 0.5f;  // gravity
            d.y += d.vy;

            // Check if inside jar bounds
            if (jarBounds != null && jarBounds.contains(d.x, d.y)) {
                it.remove();
                score++;
                if (onScore != null) onScore.accept(score);

                if (score >= 100 && onGameOver != null) {
                    onGameOver.accept(score);
                }
            }

            // Remove if falls below screen
            if (d.y > height) {
                it.remove();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        float jarWidth = width * 0.4f;
        float jarHeight = jarBitmap.getHeight() * (jarWidth / jarBitmap.getWidth());
        float left = (width - jarWidth)/2f;
        float top = height - jarHeight - 40;

        jarBounds = new RectF(left, top, left + jarWidth, top + jarHeight);

        // Scale bitmap to fit bounds
        jarBitmap = Bitmap.createScaledBitmap(jarBitmap, (int)jarWidth, (int)jarHeight, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw drops
        for (Drop d : drops) {
            canvas.drawCircle(d.x, d.y, d.r, getDropPaint());
        }

        // Draw jar (moves vertically)
        float offsetTop = jarBounds.top + jarY;
        RectF movedJar = new RectF(jarBounds.left, offsetTop, jarBounds.right, offsetTop + jarBounds.height());
        canvas.drawBitmap(jarBitmap, null, movedJar, null);
    }

    private android.graphics.Paint getDropPaint() {
        android.graphics.Paint p = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        p.setColor(0xFF2196F3); // Blue
        return p;
    }
}
