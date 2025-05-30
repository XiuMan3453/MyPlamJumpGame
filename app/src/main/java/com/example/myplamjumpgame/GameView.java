package com.example.myplamjumpgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Thread gameThread;
    private SurfaceHolder holder;
    private boolean isRunning = false;
    // 按键区域定义
    private Rect leftButton = new Rect();
    private Rect rightButton = new Rect();
    private Rect jumpButton = new Rect();
    private Player player;
    private List<Platform> platforms;
    private Platform currentPlatform; // 当前玩家所在的平台
    private float cameraX = 0; // 相机水平偏移
    private float cameraY = 0; // 相机垂直偏移
    private float TARGET_X_OFFSET; // 角色在屏幕水平位置的30%（中左部）
    private float TARGET_Y_OFFSET; // 角色在屏幕垂直居中

    private List<Strawberry> strawberries = new ArrayList<>();
    private int score = 0;
    private Random rand = new Random();

    public GameView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        updateScreenDimensions();
        initGame();
    }
    private void updateScreenDimensions() {
        TARGET_X_OFFSET = getWidth() * 0.3f;
        TARGET_Y_OFFSET = getHeight() * 0.5f;
    }
    private int getScreenWidth() {
        return getWidth(); // SurfaceView 的宽度
    }

    private int getScreenHeight() {
        return getHeight(); // SurfaceView 的高度
    }

    private void initGame() {
        // 初始位置根据相机偏移计算
        int startX = (int) (TARGET_X_OFFSET);
        int startY = (int) (TARGET_Y_OFFSET);
        player = new Player(startX, startY);
        platforms = new ArrayList<>();
        platforms.add(new Platform(startX, startY + 50, 200)); // 初始平台
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        updateScreenDimensions(); // 屏幕尺寸变化时更新
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            update();       // 更新游戏状态
            draw();         // 绘制游戏画面
            controlFPS();   // 控制帧率
        }
    }

    private void update() {
        player.update();
        checkCollisions();
        checkStrawberryCollisions();

        // 使用线性插值实现平滑移动
        cameraX = player.getX() - TARGET_X_OFFSET;
        cameraY = player.getY() - TARGET_Y_OFFSET;

    }
    // 绘制按键

    private void drawButtons(Canvas canvas) {
        // 按钮区域定义（基于屏幕固定位置）
        int buttonSize = 150;
        int buttonBottom = getHeight() - 50;
        int buttonTop = buttonBottom - buttonSize;

        leftButton.set(50, buttonTop, 50 + buttonSize, buttonBottom);
        rightButton.set(250, buttonTop, 250 + buttonSize, buttonBottom);
        jumpButton.set(getWidth() - 200, buttonTop, getWidth() - 50, buttonBottom);

        // 绘制按钮（直接使用屏幕坐标，无需偏移）
        Paint buttonPaint = new Paint();
        buttonPaint.setColor(Color.argb(100, 0, 0, 255));
        canvas.drawRect(leftButton, buttonPaint);
        canvas.drawRect(rightButton, buttonPaint);
        canvas.drawRect(jumpButton, buttonPaint);

        // 绘制按钮文字
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(60);
        canvas.drawText("←", leftButton.centerX() - 20, leftButton.centerY() + 20, textPaint);
        canvas.drawText("→", rightButton.centerX() - 20, rightButton.centerY() + 20, textPaint);
        canvas.drawText("↑", jumpButton.centerX() - 20, jumpButton.centerY() + 20, textPaint);
    }

    private void draw() {
        if (!holder.getSurface().isValid()) return;

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.WHITE); // 清空背景

        // 绘制玩家和平台
        player.draw(canvas, cameraX, cameraY);
        for (Platform platform : platforms) {
            platform.draw(canvas, cameraX, cameraY);
        }
        for (Strawberry strawberry : strawberries) {
            strawberry.draw(canvas, cameraX, cameraY);
        }
        // 绘制分数
        Paint scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(80);
        canvas.drawText("分数: " + score, 50, 100, scorePaint);
        drawButtons(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    private void controlFPS() {
        try {
            Thread.sleep(16); // 约 60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float touchX = event.getX();
        float touchY = event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // 检测触摸区域
                if (leftButton.contains((int) touchX, (int) touchY)) {
                    player.moveLeft();
                } else if (rightButton.contains((int) touchX, (int) touchY)) {
                    player.moveRight();
                } else if (jumpButton.contains((int) touchX, (int) touchY)) {
                    player.jump();
                }
                return true;

            case MotionEvent.ACTION_UP:
                // 松开时停止移动
                player.stopMoving();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void checkCollisions() {
        for (Platform platform : platforms) {
            if (platform.checkCollision(player)) {
                player.landOnPlatform(platform.getY());
                currentPlatform = platform; // 记录当前平台
                generatePlatforms();
            }
        }
    }
    private boolean isPlatformOverlapping(int newX, int newY, int newWidth) {
        // 新平台的矩形范围
        Rect newRect = new Rect(newX, newY, newX + newWidth, newY + 50);

        // 遍历所有现有平台
        for (Platform platform : platforms) {
            Rect existingRect = new Rect(
                    platform.getX(),
                    platform.getY(),
                    platform.getX() + platform.getWidth(),
                    platform.getY() + 50
            );

            // 检查矩形是否相交
            if (newRect.intersect(existingRect)) {
                return true;
            }
        }
        return false;
    }

    // 在生成平台时概率生成草莓
    private void generateStrawberry(Platform platform) {
        if (rand.nextFloat() < 0.3f) { // 30%概率生成
            // 草莓位于平台左上方或右上方
            int offsetX = rand.nextBoolean() ? -50 : platform.getWidth() + 50;
            strawberries.add(new Strawberry(
                    platform.getX() + offsetX,
                    platform.getY() - 100 // 平台上方100像素
            ));
        }
    }

    // 检测草莓碰撞
    private void checkStrawberryCollisions() {
        Iterator<Strawberry> iterator = strawberries.iterator();
        while (iterator.hasNext()) {
            Strawberry strawberry = iterator.next();
            if (strawberry.checkCollision(player, cameraX, cameraY)) {
                strawberry.collect();
                score++;
                iterator.remove();
            }
        }
    }
    private void generatePlatforms() {
        if (currentPlatform != null && !currentPlatform.hasGenerated()){

            Random rand = new Random();
            int rightX = 0, rightY = 0;

            // 生成候选位置
            rightX = currentPlatform.getX() + rand.nextInt(200) + 200; // 横向随机：+300~+700
            rightY = currentPlatform.getY() - (rand.nextInt(300) - 100); // 垂直随机：上移200~500像素

            // 检查是否重叠且位于屏幕内
            if (!isPlatformOverlapping(rightX, rightY, 200) ) {
                Platform newplatform = new Platform(rightX, rightY, 200);
                platforms.add(newplatform);
                generateStrawberry(newplatform);
                currentPlatform.setHasGenerated(true);
            }
            currentPlatform = null; // 重置当前平台标记
            // 清理超出屏幕的平台


            Iterator<Platform> iterator = platforms.iterator();
            while (iterator.hasNext()) {
                Platform platform = iterator.next();
                if (platform.getX() + platform.getWidth() - cameraX < 0) { // 新增：检查是否超出左侧
                    iterator.remove();
                }
            }
        }
    }

}
