package com.luce.healthmanager;

public class Message {
    public static final int TYPE_USER = 0; // 使用者訊息類型
    public static final int TYPE_AI = 1;   // AI 助理訊息類型

    private String content; // 訊息內容
    private int type;       // 訊息類型

    public Message(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public boolean isFromUser() {
        return type == TYPE_USER;
    }
}
