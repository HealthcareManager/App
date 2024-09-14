package com.luce.healthmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class ExerciseFragment extends Fragment {

    private boolean isRunning = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        // 設置運動數據
        TextView exerciseDistance = view.findViewById(R.id.exercise_distance);
        TextView exerciseStatus = view.findViewById(R.id.exercise_status);  // 單一TextView顯示運動狀態
        TextView exerciseTime = view.findViewById(R.id.exercise_time);
        TextView caloriesBurned = view.findViewById(R.id.calories_burned);

        // 初始化按鈕
        Button btnRun = view.findViewById(R.id.btn_run);
        Button btnWalk = view.findViewById(R.id.btn_walk);
        Button startButton = view.findViewById(R.id.start_exercise_button);

        // 跑步按鈕點擊事件
        btnRun.setOnClickListener(v -> {
            btnWalk.setBackgroundResource(R.color.colorPrimaryDark);  // 恢復默認背景顏色
            btnRun.setBackgroundResource(R.color.colorPrimary);  // 設定跑步按鈕背景色
            exerciseStatus.setText("跑步");

            // 更新數據，從穿戴裝置取得數據
            double distance = getWearableDeviceDistance();
            //int calories = calculateCalories(distance, "run"); // 根據運動模式計算卡路里
            exerciseDistance.setText(String.format("%.1f 公里", distance));
            caloriesBurned.setText(String.format("消耗卡路里：%d"));
        });

        // 健走按鈕點擊事件
        btnWalk.setOnClickListener(v -> {
            btnWalk.setBackgroundResource(R.color.colorPrimaryDark);  // 設定健走按鈕背景色
            btnRun.setBackgroundResource(R.color.colorPrimary);  // 恢復默認背景顏色
            exerciseStatus.setText("健走");

            // 更新數據，從穿戴裝置取得數據
            double distance = getWearableDeviceDistance();
            //int calories = calculateCalories(distance, "walk"); // 根據運動模式計算卡路里
            exerciseDistance.setText(String.format("%.1f 公里", distance));
            caloriesBurned.setText(String.format("消耗卡路里：%d"));
        });

        // 開始運動按鈕點擊事件
        startButton.setOnClickListener(v -> {
            if (isRunning) {
                // 如果已經在運動中，則暫停運動
                isRunning = false;
                startButton.setText("GO");
                pauseExercise();  // 暫停運動邏輯
            } else {
                // 如果沒有在運動，則開始運動
                isRunning = true;
                startButton.setText("STOP");
                startExercise();  // 開始運動邏輯
            }
        });

        return view;
    }


    // 假設是開始運動的邏輯
    private void startExercise() {
        // 這裡是開始運動的具體實現邏輯，可以是計時器開始、運動數據更新等
        // 例如：
        // timer.start();
    }

    // 假設是暫停運動的邏輯
    private void pauseExercise() {
        // 這裡是暫停運動的具體實現邏輯，停止計時器或暫停數據更新等
        // 例如：
        // timer.pause();
    }

    // 假設從穿戴裝置取得距離數據
    private double getWearableDeviceDistance() {
        // 在此處從小米穿戴式裝置取得實際距離數據的邏輯
        // 這裡暫時返回固定數據作為示範
        return 5.0;  // 例如：5公里
    }
}

// 根據
