package com.example.myplamjumpgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.SoundPool;

public class Player {
    private float x, y;
    private float velocityX;
    private float velocityY;
    private static final int MOVE_SPEED = 10;
    private boolean isJumping = false;
    private static final float GRAVITY = 1.5F;
    private static final float ZULI = 1.2F;
//    private static final int JUMP_FORCE = -25;
    private boolean isCharging = false;
    private long chargeStartTime = 0;
    private float jumpPower = 0;
    private float movePower = 0;
    private static final float MAX_JUMP_POWER = 30f; // 最大蓄力值
    private static final float CHARGE_RATE = 0.06f;   // 蓄力速度系数

    private static final float MOVE_RATE = 0.05f;
    private static final float MAX_MOVE_POWER = 30f; // 最大蓄力值
    private boolean isAlive = true;
    private CharacterAnimator animator;
    private boolean isMoving = false;

    public Player(int startX, int startY, Context context) {
        x = startX;
        y = startY;
        this.animator = new CharacterAnimator(context.getResources());
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

        // 动画状态机
        if (velocityY < -2) { // 上升阶段
            animator.setState(CharacterAnimator.AnimState.JUMP);
            isJumping = true;
        }
        else if (velocityY > 2 || isJumping) { // 下落阶段
            animator.setState(CharacterAnimator.AnimState.FALL);
        }else {
            animator.setState(CharacterAnimator.AnimState.IDLE);
        }


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
        Bitmap currentFrame = animator.getCurrentFrame();

        // 计算绘制坐标（考虑锚点）
        float screenX = getX() - cameraX - 20; // 水平居中
        float screenY = getY() - cameraY - currentFrame.getHeight()/2 - 10;  // 底部对齐

        // 绘制动画帧
        canvas.drawBitmap(currentFrame, screenX, screenY, null);
    }

    //   public void draw(Canvas canvas, float cameraX, float cameraY) {
  //      Paint paint = new Paint();
  //      paint.setColor(Color.BLUE);
  //      // 屏幕坐标 = 世界坐标 - 相机偏移
   //     float screenX = getX() - cameraX;
   //     float screenY = getY() - cameraY;
    //    canvas.drawRect(screenX, screenY, screenX + 100, screenY + 100, paint);
   // }
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
