package com.luce.healthmanager;

import java.time.LocalDateTime;

public class HeightWeightRecord {
    private String userId; // 用戶 ID
    private double height;  // 身高
    private double weight;  // 體重
    private String date; // 日期時間

    // 建構子
    public HeightWeightRecord(String userId, double height, double weight, String date) {
        this.userId = userId;
        this.height = height;
        this.weight = weight;
        this.date = date;
    }

    // Getters 和 Setters
    public String getUserId() {
        return userId;
    }

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public String getDate() {
        return date;
    }
}

