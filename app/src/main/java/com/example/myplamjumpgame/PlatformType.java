package com.example.myplamjumpgame;

import android.graphics.Color;

public enum PlatformType {
    NORMAL(0, Color.GREEN, 0),    // 普通平台+0分
    GOLD(1, Color.YELLOW, 10),     // 金色平台+10分
    DIAMOND(2, Color.CYAN, 15),    // 钻石平台+15分
    RAINBOW(3, 0xFFFF00FF, 20);    // 彩虹平台+20分 (ARGB颜色)

    public final int id;
    public final int color;
    public final int bonusScore;

    PlatformType(int id, int color, int bonusScore) {
        this.id = id;
        this.color = color;
        this.bonusScore = bonusScore;
    }
}
