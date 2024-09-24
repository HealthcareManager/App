package com.luce.healthmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private Button registerButton;
    private ImageButton googleLoginButton;
    private ImageButton facebookLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 登入界面

        // 初始化按鈕
        registerButton = findViewById(R.id.register_button);
        googleLoginButton = findViewById(R.id.google_login_button);
        facebookLoginButton = findViewById(R.id.facebook_login_button);

        // 註冊按鈕點擊事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳轉到 RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Google 登入按鈕點擊事件
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Google 登入處理
                googleSignIn();
            }
        });

        // Facebook 登入按鈕點擊事件
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Facebook 登入處理
                facebookSignIn();
            }
        });
    }

    // Google 登入處理邏輯
    private void googleSignIn() {
        // 這裡實作 Google 登入邏輯，例如使用 GoogleSignInClient
        Toast.makeText(LoginActivity.this, "Google 登入", Toast.LENGTH_SHORT).show();
    }

    // Facebook 登入處理邏輯
    private void facebookSignIn() {
        // 這裡實作 Facebook 登入邏輯
        Toast.makeText(LoginActivity.this, "Facebook 登入", Toast.LENGTH_SHORT).show();
    }

    // 這裡可以實作一個方法，將用戶資料透過 API 送至後端伺服器
    // private void sendUserDataToServer(String username, String password, String email, String phone, String birthday) {
    //     // 實作與後端 API 的連接
    // }
}
