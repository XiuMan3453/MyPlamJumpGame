package com.example.myplamjumpgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CharacterAnimator {
    enum AnimState { IDLE, JUMP, FALL }
    private AnimState currentState = AnimState.IDLE;

    private Bitmap[][] allFrames; // [状态][帧]
    private int[] frameCounts = {14, 1, 13}; // 各状态帧数
    private int currentFrame;
    private long lastFrameTime;

    public CharacterAnimator(Resources res) {
        // 初始化所有动画
        allFrames = new Bitmap[AnimState.values().length][];
        for (AnimState state : AnimState.values()) {
            loadStateFrames(res, state);
        }
    }

    private void loadStateFrames(Resources res, AnimState state) {
        int count = frameCounts[state.ordinal()];
        allFrames[state.ordinal()] = new Bitmap[count];

        for (int i = 0; i < count; i++) {
            String name = String.format("%s_%02d",
                    state.name().toLowerCase(), i+1);
            int resId = res.getIdentifier(name, "drawable", "com.example.myplamjumpgame");
            allFrames[state.ordinal()][i] = BitmapFactory.decodeResource(res, resId);
        }
    }

    public void setState(AnimState newState) {
        if (currentState != newState) {
            currentState = newState;
            currentFrame = 0; // 切换状态时重置帧
        }
    }

    public Bitmap getCurrentFrame() {
        // 按60ms/帧更新动画(约16FPS)
        if (System.currentTimeMillis() - lastFrameTime > getFrameDelay()) {
            currentFrame = (currentFrame + 1) % frameCounts[currentState.ordinal()];
            lastFrameTime = System.currentTimeMillis();
        }
        return allFrames[currentState.ordinal()][currentFrame];
    }

    // 为不同状态设置不同帧间隔
    private int getFrameDelay() {
        switch (currentState) {
            case JUMP: return 50;  // 跳跃动画更快(20FPS)
            case FALL: return 100;  // 下落动画稍慢(14FPS)
            default:   return 200; // 待机动画(10FPS)
        }
    }
}
