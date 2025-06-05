package com.example.myplamjumpgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

public class Strawberry {
    private int x, y;
    private boolean isCollected = false;

    private Bitmap strawberryBitmap;
    private static final int SIZE = 50; // 草莓大小

    public Strawberry(int x, int y, Context context) {
        this.x = x;
        this.y = y;
        // 加载位图
        strawberryBitmap = BitmapFactory.decodeResource(
                context.getResources(),
                R.drawable.star
        );
    }

    public void draw(Canvas canvas, float cameraX, float cameraY) {
        if (!isCollected) {
         //   float drawX = x - cameraX - strawberryBitmap.getWidth()/2f;
         //   float drawY = y - cameraY - strawberryBitmap.getHeight()/2f;
         //   canvas.drawBitmap(strawberryBitmap, drawX, drawY, null);
            // 目标尺寸（调整为原图的50%）
            float targetWidth = strawberryBitmap.getWidth() * 0.25f;
            float targetHeight = strawberryBitmap.getHeight() * 0.25f;

            Matrix matrix = new Matrix();
            matrix.postScale(0.25f, 0.25f); // 缩放系数
            matrix.postTranslate(
                    x - cameraX - targetWidth/2,
                    y - cameraY - targetHeight/2
            );
            canvas.drawBitmap(strawberryBitmap, matrix, null);
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
