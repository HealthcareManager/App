package com.luce.healthmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AiAssistantActivity extends AppCompatActivity {

    private List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private RecyclerView messageRecyclerView;
    private ImageButton backButton;
    private ImageButton sendButton;
    private EditText inputMessage;

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

        // 初始化發送按鈕和輸入框
        inputMessage = findViewById(R.id.input_message);
        sendButton = findViewById(R.id.send_button);

        // 頁面載入後自動發送AI助理的歡迎訊息
        sendAiMessage("請問需要幫忙嗎？");

        // 設置發送按鈕的點擊事件
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

                    // AI 助理的回覆 (僅作示例)
                    sendAiMessage("已收到訊息");
                }
            }
        });
    }

    // AI 助理發送訊息的方法
    private void sendAiMessage(String messageContent) {
        messageList.add(new Message(messageContent, Message.TYPE_AI));
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        messageRecyclerView.scrollToPosition(messageList.size() - 1);
    }
}
