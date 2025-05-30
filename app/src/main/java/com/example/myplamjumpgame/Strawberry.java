package com.example.myplamjumpgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Strawberry {
    private int x, y;
    private boolean isCollected = false;
    private static final int SIZE = 50; // 草莓大小

    public Strawberry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas canvas, float cameraX, float cameraY) {
        if (!isCollected) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            // 绘制圆形草莓（实际游戏可替换为Bitmap）
            canvas.drawCircle(x - cameraX, y - cameraY, SIZE / 2, paint);
        }
    }

    public boolean checkCollision(Player player, float cameraX, float cameraY) {
        if (isCollected) return false;
        // 碰撞检测：玩家与草莓的圆形碰撞
        float playerCenterX = player.getX() + 50 - cameraX; // 玩家中心X
        float playerCenterY = player.getY() + 50 - cameraY; // 玩家中心Y
        float dx = (x - cameraX) - playerCenterX;
        float dy = (y - cameraY) - playerCenterY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        return distance < (SIZE / 2 + 50); // 玩家半径50，草莓半径25
    }

    public void collect() {
        isCollected = true;
    }
}
