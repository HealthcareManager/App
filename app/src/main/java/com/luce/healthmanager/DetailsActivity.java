package com.luce.healthmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    private TextView titleTextView, dataTextView;
    private ImageView iconImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        titleTextView = findViewById(R.id.subtitle);
        dataTextView = findViewById(R.id.data_text);
        iconImageView = findViewById(R.id.sleep_icon);

        // 接收來自 Intent 的數據
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String data = intent.getStringExtra("data");
        int iconResId = intent.getIntExtra("icon", 0);

        // 設置數據到頁面
        titleTextView.setText(title);
        dataTextView.setText(data);
        iconImageView.setImageResource(iconResId);
    }
}
