package com.luce.healthmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // 定義 LinearLayout 變量來表示自定義的按鈕
    LinearLayout googleLoginButton, facebookLoginButton, lineLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 登入界面

        // 初始化按鈕
        googleLoginButton = findViewById(R.id.google_login_button);
        facebookLoginButton = findViewById(R.id.facebook_login_button);
        lineLoginButton = findViewById(R.id.line_login_button);

        // Google 登入按鈕點擊事件
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 這裡放置 Google 登入邏輯

            }
        });

        // Facebook 登入按鈕點擊事件
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 這裡放置 Facebook 登入邏輯

            }
        });

        // Line 登入按鈕點擊事件
        lineLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 這裡放置 Line 登入邏輯

            }
        });
    }
}
