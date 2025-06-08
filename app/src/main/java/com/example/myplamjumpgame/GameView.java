package com.example.myplamjumpgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private Thread gameThread;
    private SurfaceHolder holder;
    private boolean isRunning = false;
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
    private Platform lastPlatform = null;

    private List<ScoreEffect> scoreEffects = new ArrayList<>();

    private MediaPlayer bgmPlayer;
    private Bitmap staticBackground;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
//        initBGM();
//        startBGM();
        loadBackground();
        player = new Player(startX, startY, getContext());
        platforms = new ArrayList<>();
        PlatformType type = PlatformType.NORMAL;
        platforms.add(new Platform(startX, startY + 50, 200, type)); // 初始平台

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
        updateScoreEffects();

        // 使用线性插值实现平滑移动
        cameraX = player.getX() - TARGET_X_OFFSET;
        cameraY = player.getY() - TARGET_Y_OFFSET;


        if (player.getY() > getHeight() - 250) {
            handlePlayerDeath();
//            releaseBGM();
            return;
        }

    }

    private void loadBackground() {
        // 加载全屏静态背景
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false; // 禁用自动缩放
        staticBackground = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.background,
                options
        );
    }
    private void drawBackground(Canvas canvas) {
        // 直接绘制全屏背景
        Matrix matrix = new Matrix();
        float scale = Math.max(
                (float)getWidth() / staticBackground.getWidth(),
                (float)getHeight() / staticBackground.getHeight()
        );
        matrix.setScale(scale, scale);
        matrix.postTranslate(
                (getWidth() - staticBackground.getWidth() * scale) / 2,
                (getHeight() - staticBackground.getHeight() * scale) / 2
        );
        canvas.drawBitmap(staticBackground, matrix, null);
//        canvas.drawBitmap(staticBackground, 0, 0, null);
    }
//    private void initBGM() {
//        // 初始化MediaPlayer
//        bgmPlayer = MediaPlayer.create(getContext(), R.raw.bgm);
//        bgmPlayer.setLooping(true); // 循环播放
//        bgmPlayer.setVolume(0.3f, 0.3f); // 左右声道音量（0-1）
 //   }

    // 在游戏开始时调用
//    public void startBGM() {
//        if (bgmPlayer != null && !bgmPlayer.isPlaying()) {
//            bgmPlayer.start();
//        }
//    }

    // 在游戏结束时释放资源
//    private void releaseBGM() {
//        if (bgmPlayer != null) {
//            bgmPlayer.release();
//            bgmPlayer = null;
//        }//   }

    // 添加新特效
    private void addScoreEffect(int score, int color) {
        String text = "+" + score;
        scoreEffects.add(new ScoreEffect(
                player.getX() - cameraX  + 20,
                player.getY() - cameraY,
                text,
                color
        ));
    }

    // 更新所有特效
    private void updateScoreEffects() {
        Iterator<ScoreEffect> it = scoreEffects.iterator();
        while (it.hasNext()) {
            if (!it.next().update()) {
                it.remove(); // 移除已完成特效
            }
        }
    }

    private void handleScore(int bonus) {
        score += bonus;
        int color = Color.WHITE;
        if(bonus == 1){
            color = Color.GREEN;
        } else if(bonus == 5){
            color = Color.RED;
        } else if (bonus == 10) {
            color = Color.YELLOW;
        } else if (bonus == 15) {
            color = Color.BLUE;
        } else if (bonus == 20) {
            color = Color.CYAN;
        }
        addScoreEffect(bonus, color); // 彩色特效
    }

    // 绘制所有特效
    private void drawScoreEffects(Canvas canvas) {
        for (ScoreEffect effect : scoreEffects) {
            effect.draw(canvas);
        }
    }

    private void draw() {
        if (!holder.getSurface().isValid()) return;

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(Color.WHITE); // 清空背景
        drawBackground(canvas);

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
        drawScoreEffects(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    private void controlFPS() {
        try {
            Thread.sleep(16); // 约 60 FPS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handlePlayerDeath() {
        isRunning = false;

        // 跳转到结算界面
        Intent intent = new Intent(getContext(), GameOverActivity.class);
        intent.putExtra("score", score); // 传递当前分数
        getContext().startActivity(intent);

        // 关闭游戏Activity
        ((Activity) getContext()).finish();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 按下时开始蓄力（无论按在屏幕哪个位置）
                player.startCharge();
                return true;

            case MotionEvent.ACTION_UP:
                // 松开时执行跳跃
                player.releaseJump();
                return true;
        }
        return false;
    }


    private void checkCollisions() {
        for (Platform platform : platforms) {
            if (platform.checkCollision(player)) {
                player.landOnPlatform(platform.getY());
                currentPlatform = platform; // 记录当前平台
                generatePlatforms();
                // 基础得分（每次着陆都加）
                if (!platform.equals(lastPlatform)) {
                    handleScore(1);
                    lastPlatform = platform;
                    platform.setLandedTime(System.currentTimeMillis());
                }

                // 特殊平台额外得分
                if (platform.checkBonusEarned() && !platform.isBonusGiven()) {
                    handleScore(platform.getType().bonusScore);
                    platform.markBonusGiven();
                }

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
                    platform.getY() - 100,
                    getContext()// 平台上方100像素
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
                handleScore(5);
                iterator.remove();
            }
        }
    }
    private void generatePlatforms() {
        if (currentPlatform != null && !currentPlatform.hasGenerated()){

            Random rand = new Random();
            PlatformType type = PlatformType.NORMAL;
            // 5%概率生成特殊平台
            float chance = rand.nextFloat();
            if (chance < 0.15f) {
                PlatformType[] specialTypes = {
                        PlatformType.GOLD,
                        PlatformType.DIAMOND,
                        PlatformType.RAINBOW
                };
                type = specialTypes[rand.nextInt(3)];
            }
            int rightX = 0, rightY = 0;

            // 生成候选位置
            rightX = currentPlatform.getX() + rand.nextInt(300) + 300; // 横向随机：+300~+700
            rightY = currentPlatform.getY() - (rand.nextInt(200) - 100); // 垂直随机：上移200~500像素

            // 检查是否重叠且位于屏幕内
            if (!isPlatformOverlapping(rightX, rightY, 200) ) {
                Platform newplatform = new Platform(rightX, rightY, rand.nextInt(200) + 75, type);
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
