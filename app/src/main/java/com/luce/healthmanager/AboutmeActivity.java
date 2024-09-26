package com.luce.healthmanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AboutmeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutme);  // 綁定對應的 XML 佈局

        // 如果你有返回按鈕，這裡可以設置它的行為
        findViewById(R.id.back_button).setOnClickListener(view -> onBackPressed());
    }
}