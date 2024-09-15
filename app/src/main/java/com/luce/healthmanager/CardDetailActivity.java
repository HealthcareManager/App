package com.luce.healthmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class CardDetailActivity extends AppCompatActivity {

    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        // 接收從 Intent 傳遞的卡片類型
        String cardType = getIntent().getStringExtra("CARD_TYPE");

        // 根據卡片類型顯示對應的詳細內容
        TextView cardTitle = findViewById(R.id.card_detail_title);
        TextView cardData = findViewById(R.id.card_detail_data);  // 顯示具體的數據

        // 初始化返回按鈕
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回到上一個 Activity
            }
        });

        // 根據卡片類型設置相應的詳細資料
        switch (cardType) {
            case "sleep":
                cardTitle.setText("睡眠詳情");
                cardData.setText("這是關於睡眠的詳細資料...");
                break;
            case "heart":
                cardTitle.setText("心律詳情");
                cardData.setText("這是關於心律的詳細資料...");
                break;
            case "oxygen":
                cardTitle.setText("血氧詳情");
                cardData.setText("這是關於血氧的詳細資料...");
                break;
            case "blood":
                cardTitle.setText("血壓詳情");
                cardData.setText("這是關於血壓的詳細資料...");
                break;
            case "sugar":
                cardTitle.setText("血糖詳情");
                cardData.setText("這是關於血糖的詳細資料...");
                break;
            case "steps":
                cardTitle.setText("步數詳情");
                cardData.setText("這是關於步數的詳細資料...");
                break;
            case "calories":
                cardTitle.setText("卡路里詳情");
                cardData.setText("這是關於卡路里的詳細資料...");
                break;
            case "stand":
                cardTitle.setText("站立詳情");
                cardData.setText("這是關於站立次數的詳細資料...");
                break;
            case "pressure":
                cardTitle.setText("壓力指數詳情");
                cardData.setText("這是關於壓力指數的詳細資料...");
                break;
            case "height":
                cardTitle.setText("身高體重詳情");
                cardData.setText("這是關於身高體重的詳細資料...");
                break;
            case "smoke":
                cardTitle.setText("抽菸詳情");
                cardData.setText("這是關於抽菸的詳細資料...");
                break;
            case "beer":
                cardTitle.setText("喝酒詳情");
                cardData.setText("這是關於喝酒的詳細資料...");
                break;
            case "leaf":
                cardTitle.setText("檳榔詳情");
                cardData.setText("這是關於檳榔的詳細資料...");
                break;
            default:
                cardTitle.setText("未知卡片");
                cardData.setText("沒有找到相關的數據。");
                break;
        }
    }
}
