package com.example.myplamjumpgame;

import android.graphics.Canvas;
import android.graphics.Paint;

// ScoreEffect.java
public class ScoreEffect {
    private float x, y;       // 显示位置
    private String text;      // 显示文本（如"+10"）
    private int alpha = 255;  // 透明度
    private Paint paint;      // 绘制样式
    private float offsetY = 0; // 垂直偏移（用于上浮效果）

    public ScoreEffect(float x, float y, String text, int color) {
        this.x = x;
        this.y = y;
        this.text = text;

        this.paint = new Paint();
        paint.setColor(color);
        paint.setTextSize(60); // 文本大小
        paint.setAntiAlias(true);
    }

    public boolean update() {
        offsetY -= 2;  // 上浮速度
        alpha -= 5;    // 淡出速度
        paint.setAlpha(alpha);
        return alpha > 0; // 返回是否还需绘制
    }

    public void draw(Canvas canvas) {
        canvas.drawText(text, x, y + offsetY, paint);
    }
}
