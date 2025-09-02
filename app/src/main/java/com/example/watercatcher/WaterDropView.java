package com.example.watercatcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

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

    private final Paint dropPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint jarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint jarStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private final List<Drop> drops = new ArrayList<>();
    private final Handler handler = new Handler();

    private float width, height;
    private float tiltX, tiltY;
    private float gravity = 0.6f;
    private float friction = 0.995f;
    private float jarFill = 0f; // 0..1
    private float jarTarget = 0f;
    private int score = 0;
    private int baseDropSize = 30;
    private int sensitivity = 5;
    private float ambientLight = 100f;

    private float jarY = 0f;
    private float jarSpeed = 1.8f;
    private int jarDir = 1;
    private int framesUntilSpeedChange = 0;

    private Consumer<Integer> onScore;
    private Consumer<Integer> onGameOver;

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
        dropPaint.setStyle(Paint.Style.FILL);
        jarPaint.setStyle(Paint.Style.FILL);
        jarStroke.setStyle(Paint.Style.STROKE);
        jarStroke.setStrokeWidth(6f);
        jarStroke.setColor(0xFF555555);
        updateColors();
    }

    public void configure(int baseDropSize, int sensitivity, Consumer<Integer> onScore, Consumer<Integer> onGameOver) {
        this.baseDropSize = Math.max(10, baseDropSize);
        this.sensitivity = Math.max(1, sensitivity);
        this.onScore = onScore;
        this.onGameOver = onGameOver;
    }

    public void resume() { handler.post(ticker); }

    public void pause() { handler.removeCallbacks(ticker); }

    public void setTilt(float tx, float ty) {
        tiltX = tx;
        tiltY = ty;
    }

    public void setAmbientLight(float lx) {
        ambientLight = lx;
        updateColors();
    }

    public int getScore() { return score; }

    private void updateColors() {
        int alpha = 200;
        int blue = (int)Math.max(120, 255 - Math.min(ambientLight, 200));
        int color = (alpha << 24) | (50 << 16) | (150 << 8) | blue;
        dropPaint.setColor(color);
        jarPaint.setColor(0x5522AAFF);
    }

    private void spawnDrop(float x, float y) {
        float r = baseDropSize/2f + random.nextFloat()*baseDropSize/2f;
        Drop d = new Drop(x, y, r);
        d.vx = 0f;
        d.vy = 0f;
        drops.add(d);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            spawnDrop(event.getX(), event.getY());
            if (onScore != null) onScore.accept(score);
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void step() {
        // Random vertical jar movement with gentle bounces
        if (framesUntilSpeedChange <= 0) {
            framesUntilSpeedChange = 60 + random.nextInt(120);
            jarSpeed = 1.2f + random.nextFloat()*2.0f;
            if (random.nextFloat() < 0.2f) jarDir *= -1; // occasional flip
        } else {
            framesUntilSpeedChange--;
        }
        jarY += jarSpeed * jarDir;
        if (jarY > 60) { jarY = 60; jarDir = -1; }
        if (jarY < -60) { jarY = -60; jarDir = 1; }

        float gx = gravity * (tiltX / sensitivity);
        float gy = gravity * (1 + tiltY / sensitivity);

        Iterator<Drop> it = drops.iterator();
        while (it.hasNext()) {
            Drop d = it.next();
            d.vx += gx;
            d.vy += gy;
            d.x += d.vx;
            d.y += d.vy;
            d.vx *= friction;
            d.vy *= friction;

            // walls
            if (d.x < d.r) { d.x = d.r; d.vx = -d.vx*0.6f; }
            if (d.x > width-d.r) { d.x = width-d.r; d.vx = -d.vx*0.6f; }
            if (d.y < d.r) { d.y = d.r; d.vy = -d.vy*0.6f; }

            // bottom -> captured by moving jar
            float captureLine = height - 60 + jarY;
            if (d.y > captureLine) {
                it.remove();
                score += 1;
                jarTarget = Math.min(1f, jarTarget + 0.01f);
                if (onScore != null) onScore.accept(score);
            }
        }

        // smooth jar fill
        jarFill += (jarTarget - jarFill) * 0.05f;

        // Win at 100 captured drops
        if (score >= 100 && onGameOver != null) {
            onGameOver.accept(score);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);

        // draw moving jar at bottom area
        float jarWidth = width * 0.6f;
        float jarHeight = 120f;
        float left = (width - jarWidth)/2f;
        float top = height - jarHeight - 10 + jarY;
        RectF jarRect = new RectF(left, top, left + jarWidth, top + jarHeight);

        // jar fill
        float fillTop = top + jarHeight * (1 - jarFill);
        RectF fillRect = new RectF(left+8, fillTop, left + jarWidth - 8, top + jarHeight - 8);
        c.drawRoundRect(fillRect, 18, 18, jarPaint);

        // jar outline
        c.drawRoundRect(jarRect, 24, 24, jarStroke);

        // draw drops
        for (Drop d : drops) {
            c.drawCircle(d.x, d.y, d.r, dropPaint);
        }
    }
}
