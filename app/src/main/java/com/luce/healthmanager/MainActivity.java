package com.luce.healthmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null && data.toString().startsWith("com.luce.healthmanager://callback")) {
            String code = data.getQueryParameter("code");
            if (code != null) {
                // 处理返回的授权码，例如将其发送到你的后端服务器来获取 Access Token
                Log.d("LINE_AUTH", "Authorization Code: " + code);
            } else {
                String error = data.getQueryParameter("error");
                Log.e("LINE_AUTH", "Error: " + error);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 檢查是否已經有 token 儲存
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt_token", null);

        if (token != null) {
            // 使用公共的 ParseTokenTask
            new ParseTokenTask(this, new ParseTokenTask.ParseTokenCallback() {
                @Override
                public void onParseTokenCompleted(JSONObject userData) {
                    if (userData != null) {
                            Toast.makeText(MainActivity.this, "歡迎回來", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "解析 token 失败", Toast.LENGTH_SHORT).show();
                        // 跳转到登录页面
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }).execute(token);
        } else {
            // 没有 token，跳转到登录页面
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // 檢查是否要顯示 HealthFragment
        if (getIntent().getBooleanExtra("showHealthFragment", false)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HealthFragment()) // 替換為健康 Fragment
                    .commit();
        }

        // 隱藏 ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 檢查是否有意圖
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null && data.getScheme().equals("com.luce.healthmanager")) {
            // 處理重定向的邏輯
            String code = data.getQueryParameter("code");
            // 使用這個 code 繼續進行後續操作
        }

        // 初始化 FragmentManager
        fragmentManager = getSupportFragmentManager();

        // 設置健康按鈕的點擊事件來加載 HealthFragment
        ImageButton healthButton = findViewById(R.id.imageButton1);
        healthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new HealthFragment()); // 加載健康頁面的 Fragment
            }
        });

        // 設置運動按鈕的點擊事件來加載 ExerciseFragment
        ImageButton sportButton = findViewById(R.id.imageButton2);
        sportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ExerciseFragment()); // 加載運動頁面的 Fragment
            }
        });

        // 設置裝置按鈕的點擊事件來加載 DeviceFragment
        ImageButton deviceButton = findViewById(R.id.imageButton3);
        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new DeviceFragment()); // 加載裝置頁面的 Fragment
            }
        });

        // 設置個人中心按鈕的點擊事件來加載 ProfileFragment
        ImageButton profileButton = findViewById(R.id.imageButton4);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ProfileFragment()); // 加載個人中心頁面的 Fragment
            }
        });

        // 設置 AI 助理按鈕的點擊事件來跳轉到 AiAssistantActivity
        ImageButton aiAssistantButton = findViewById(R.id.imageButton5);
        aiAssistantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳轉到 AiAssistantActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    // Fragment 切換方法
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null); // 讓使用者可以返回到之前的 Fragment
        fragmentTransaction.commit();
    }
}
