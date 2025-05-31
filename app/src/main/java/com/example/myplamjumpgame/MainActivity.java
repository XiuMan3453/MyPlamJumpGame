package com.example.myplamjumpgame;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 开始游戏按钮
        findViewById(R.id.btn_start).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, game.class);
            startActivity(intent);
        });

        // 退出游戏按钮
        findViewById(R.id.btn_exit).setOnClickListener(v -> {
            finishAffinity(); // 关闭所有Activity
            System.exit(0);   // 完全退出应用
        });
    }
}