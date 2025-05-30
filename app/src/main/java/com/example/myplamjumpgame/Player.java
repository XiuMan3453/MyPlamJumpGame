package com.example.myplamjumpgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Player {
    private int x, y;
    private int velocityX;
    private int velocityY;
    private static final int MOVE_SPEED = 10;
    private boolean isJumping = false;
    private static final int GRAVITY = 1;
    private static final int JUMP_FORCE = -25;

    public Player(int startX, int startY) {
        x = startX;
        y = startY;
    }
    public void moveLeft() {
        velocityX = -MOVE_SPEED;
    }

    public void moveRight() {
        velocityX = MOVE_SPEED;
    }

    public void stopMoving() {
        velocityX = 0;
    }

    public void update() {
        // 水平移动
        x += velocityX;

        // 垂直移动（原逻辑）
        velocityY += GRAVITY;
        y += velocityY;

        // 地面检测（示例值，需根据实际屏幕高度调整）
        if (y > 1000) {
            y = 1000;
            velocityY = 0;
            isJumping = false;
        }
    }

    public void jump() {
        if (!isJumping) {
            velocityY = JUMP_FORCE;
            isJumping = true;
        }
    }

    public void landOnPlatform(int platformY) {
        y = platformY - 100;
        velocityY = 0;
        isJumping = false;
    }

    public void draw(Canvas canvas, float cameraX, float cameraY) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        // 屏幕坐标 = 世界坐标 - 相机偏移
        float screenX = getX() - cameraX;
        float screenY = getY() - cameraY;
        canvas.drawRect(screenX, screenY, screenX + 100, screenY + 100, paint);
    }


    public int getX() { return x; }
    public int getY() { return y; }
}
