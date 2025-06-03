package com.example.myplamjumpgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Player {
    private float x, y;
    private float velocityX;
    private float velocityY;
    private static final int MOVE_SPEED = 10;
    private boolean isJumping = false;
    private static final int GRAVITY = 1;
    private static final int ZULI = 1;
    private static final int JUMP_FORCE = -25;

    private boolean isCharging = false;
    private long chargeStartTime = 0;
    private float jumpPower = 0;

    private float movePower = 0;
    private static final float MAX_JUMP_POWER = 30f; // 最大蓄力值
    private static final float CHARGE_RATE = 0.06f;   // 蓄力速度系数

    private static final float MOVE_RATE = 0.05f;
    private static final float MAX_MOVE_POWER = 30f; // 最大蓄力值
    private boolean isAlive = true;

    public Player(int startX, int startY) {
        x = startX;
        y = startY;
    }

    public void startCharge() {
        isCharging = true;
        chargeStartTime = System.currentTimeMillis();
    }

    public void releaseJump() {
        if (isCharging) {
            // 计算蓄力时间（限制最大值）
            long chargeDuration = System.currentTimeMillis() - chargeStartTime;
            jumpPower = Math.min(chargeDuration * CHARGE_RATE, MAX_JUMP_POWER);
            movePower = Math.min(chargeDuration * MOVE_RATE, MAX_MOVE_POWER);


            // 执行跳跃（蓄力越大，跳跃初速度越大）
            velocityY = -jumpPower;
            velocityX = movePower;
            isCharging = false;
        }
    }

    public void update() {
        if (isCharging) {
            x += (float) ((Math.random() - 0.5) * 2); // 微小随机位移
        }
        // 水平移动
        velocityX = Math.max(velocityX - ZULI, velocityX);
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


    public void landOnPlatform(int platformY) {
        y = platformY - 100;
        velocityY = 0;
        velocityX = 0;
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
    public void die() {
        isAlive = false;
        velocityY = 0;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public void setX(float o) {this.x = o;}
    public float getVelocityY() {
        return velocityY;
    }
}
