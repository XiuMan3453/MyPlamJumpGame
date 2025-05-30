package com.example.myplamjumpgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Platform {
    private int x, y, width;
    private boolean hasGenerated = false; // 新增标记

    public Platform(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public void draw(Canvas canvas, float cameraX, float cameraY) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        float screenX = x - cameraX;
        float screenY = y - cameraY;
        canvas.drawRect(screenX, screenY, screenX + width, screenY + 50, paint);
    }

    public boolean checkCollision(Player player) {
        // 碰撞检测逻辑
        return (player.getY() + 100 >= y) &&
                (player.getY() <= y + 50) &&
                (player.getX() + 100 >= x) &&
                (player.getX() <= x + width);
    }

    public int getY() { return y; }

    public int getX() { return x; }
    public int getWidth(){ return width; }
    public boolean hasGenerated() {
        return hasGenerated;
    }

    public void setHasGenerated(boolean hasGenerated) {
        this.hasGenerated = hasGenerated;
    }
}
