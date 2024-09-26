package com.luce.healthmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AiAssistantActivity extends AppCompatActivity {

    private List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private RecyclerView messageRecyclerView;
    private ImageButton backButton;
    private ImageButton sendButton;
    private EditText inputMessage;
    private OpenAIApiService openAIApiService; // 定義 Retrofit 服務接口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        // 初始化 RecyclerView 和適配器
        messageRecyclerView = findViewById(R.id.message_list);
        messageAdapter = new MessageAdapter(messageList);
        messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageRecyclerView.setAdapter(messageAdapter);

        // 初始化返回按鈕
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回到上一個 Activity
            }
        });

        // 初始化輸入框和發送按鈕
        inputMessage = findViewById(R.id.input_message);
        sendButton = findViewById(R.id.send_button);

        // 頁面載入後自動發送 AI 助理的歡迎訊息
        sendAiMessage("請問需要幫忙嗎？");

        // 初始化 Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8081/")  // 替換成實際的後端伺服器URL
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        openAIApiService = retrofit.create(OpenAIApiService.class);  // 創建 API 服務

        // 發送按鈕點擊事件
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = inputMessage.getText().toString();
                if (!userMessage.isEmpty()) {
                    // 使用者的訊息
                    messageList.add(new Message(userMessage, Message.TYPE_USER));
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    messageRecyclerView.scrollToPosition(messageList.size() - 1);
                    inputMessage.setText(""); // 清空輸入框

                    // 發送使用者的訊息到後端的 API
                    sendMessageToBackend(userMessage);
                }
            }
        });
    }

    // 向後端發送訊息
    private void sendMessageToBackend(String userMessage) {
        // 準備請求數據
        Map<String, String> question = new HashMap<>();
        question.put("question", userMessage);

        // 呼叫後端 API
        Call<Map<String, Object>> call = openAIApiService.askHealthQuestion(question);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 獲取 AI 助理的回覆
                    String aiResponse = response.body().get("answer").toString();
                    sendAiMessage(aiResponse);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                // 錯誤處理
                sendAiMessage("AI 助理無法回應，請稍後再試。");
            }
        });
    }

    // 顯示 AI 助理訊息的方法
    private void sendAiMessage(String messageContent) {
        messageList.add(new Message(messageContent, Message.TYPE_AI));
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        messageRecyclerView.scrollToPosition(messageList.size() - 1);
    }
}
