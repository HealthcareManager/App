package com.luce.healthmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Intent intent = new Intent(MainActivity.this, AiAssistantActivity.class);
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
