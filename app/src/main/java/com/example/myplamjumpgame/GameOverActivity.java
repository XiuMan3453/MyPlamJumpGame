package com.example.myplamjumpgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GameOverActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // 获取传递的分数
        int score = getIntent().getIntExtra("score", 0);
        TextView tvScore = findViewById(R.id.tv_score);
        tvScore.setText("scroe: " + score);

        // 重新开始按钮
        findViewById(R.id.btn_restart).setOnClickListener(v -> {
            Intent intent = new Intent(this, game.class);
            startActivity(intent);
            finish(); // 结束当前界面
        });

        // 退出游戏按钮
        findViewById(R.id.btn_exit).setOnClickListener(v -> {
            finishAffinity(); // 关闭所有Activity
        });
    }
}