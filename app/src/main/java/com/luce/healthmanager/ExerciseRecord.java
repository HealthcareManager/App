package com.luce.healthmanager;


public class ExerciseRecord {

    private String exerciseType;
    private double caloriesBurned;
    private String createdAt;  // 使用 String 來簡單表示日期時間

    // Getter 和 Setter 方法
    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}