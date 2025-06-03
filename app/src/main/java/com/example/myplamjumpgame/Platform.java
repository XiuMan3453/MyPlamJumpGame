package com.example.myplamjumpgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Platform {
    private int x, y, width;
    private PlatformType type;
    private long landedTime = -1; // 记录着陆时间
    private boolean hasGenerated = false; // 新增标记
    private boolean bonusGiven = false; // 新增字段
    private boolean isPlayerOn = false;

    public void setLandedTime(long time) {
        this.landedTime = time;
        this.bonusGiven = false; // 重置奖励状态
    }

    public enum CollisionSide { TOP, BOTTOM, LEFT, RIGHT, NONE }


    public Platform(int x, int y, int width, PlatformType type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.type = type;
    }


    public void update() {
        if (isPlayerOn) {
            if (landedTime == -1) {
                landedTime = System.currentTimeMillis();
            }
        } else {
            landedTime = -1;
        }
    }

    public void draw(Canvas canvas, float cameraX, float cameraY) {
        Paint paint = new Paint();
        paint.setColor(type.color);
        float screenX = x - cameraX;
        float screenY = y - cameraY;
        canvas.drawRect(screenX, screenY, screenX + width, screenY + 50, paint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Platform platform = (Platform) o;
        return x == platform.x &&
                y == platform.y &&
                width == platform.width &&
                type == platform.type;
    }

    public boolean checkBonusEarned() {
        if (landedTime == -1) return false;
        return System.currentTimeMillis() - landedTime >= 2000; // 2秒判定
    }

    public boolean checkCollision(Player player) {
        // 玩家底部坐标
        float playerBottom = player.getY() + 100;

        // 平台顶部坐标
        float platformTop = this.y;

        // 条件1：玩家底部与平台顶部重叠
        boolean isVerticalOverlap = playerBottom >= platformTop &&
                player.getY() < platformTop;

        // 条件2：玩家左右侧与平台左右侧重叠
        boolean isHorizontalOverlap = player.getX() + 100 > this.x &&
                player.getX() < this.x + this.width;

        // 条件3：玩家正在下落（速度向下）
        boolean isFalling = player.getVelocityY() > 0;

        return isVerticalOverlap && isHorizontalOverlap && isFalling;
    }

    public int getY() { return y; }

    public int getX() { return x; }
    public int getWidth(){ return width; }
    public boolean isBonusGiven() {
        return bonusGiven;
    }

    public void markBonusGiven() {
        this.bonusGiven = true;
    }
    public boolean hasGenerated() {
        return hasGenerated;
    }
    public PlatformType getType() {
        return this.type;
    }
    public void setHasGenerated(boolean hasGenerated) {
        this.hasGenerated = hasGenerated;
    }
}
