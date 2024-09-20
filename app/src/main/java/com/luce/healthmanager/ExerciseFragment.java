package com.luce.healthmanager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class ExerciseFragment extends Fragment {

    private boolean isRunning = false;
    private boolean isRunMode = false;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double totalDistance = 0.0;
    private long startTime = 0;
    private Handler handler = new Handler();
    private Runnable updateTimerRunnable;

    private TextView exerciseDistance;
    private TextView exerciseTime;
    private TextView caloriesBurned;
    private Button btnRun;
    private Button btnWalk;
    private Button startButton;

    private double lastLatitude = 0.0;
    private double lastLongitude = 0.0;
    private boolean isFirstLocation = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        // 在這裡初始化 LocationManager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // 初始化視圖
        exerciseDistance = view.findViewById(R.id.exercise_distance);
        exerciseTime = view.findViewById(R.id.exercise_time);
        caloriesBurned = view.findViewById(R.id.calories_burned);
        btnRun = view.findViewById(R.id.btn_run);
        btnWalk = view.findViewById(R.id.btn_walk);
        startButton = view.findViewById(R.id.start_exercise_button);

        // 跑步按鈕點擊事件
        btnRun.setOnClickListener(v -> {
            btnRun.setBackgroundResource(R.color.colorPrimary); // 選中的
            btnWalk.setBackgroundResource(R.color.colorPrimaryDark); // 恢復另一個按鈕的顏色
            isRunMode = true;
        });

        // 健走按鈕點擊事件
        btnWalk.setOnClickListener(v -> {
            btnWalk.setBackgroundResource(R.color.colorPrimary); // 選中
            btnRun.setBackgroundResource(R.color.colorPrimaryDark); // 恢復另一個按鈕的顏色
            isRunMode = false;
        });

        // 開始運動按鈕點擊事件
        startButton.setOnClickListener(v -> {
            if (isRunning) {
                isRunning = false;
                startButton.setText("GO");
                pauseExercise();
            } else {
                isRunning = true;
                startButton.setText("STOP");
                startExercise();
            }
        });

        return view;
    }

    // 開始運動的邏輯
    private void startExercise() {
        startTime = SystemClock.elapsedRealtime();
        updateTimerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = SystemClock.elapsedRealtime() - startTime;
                int minutes = (int) (elapsedTime / 1000 / 60);
                int seconds = (int) (elapsedTime / 1000 % 60);
                exerciseTime.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                handler.postDelayed(this, 1000); // 每秒更新一次
            }
        };
        handler.post(updateTimerRunnable);

        // 初始化 LocationListener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // 計算新的距離並更新界面
                if (isRunning) {
                    double currentLatitude = location.getLatitude();
                    double currentLongitude = location.getLongitude();
                    totalDistance += calculateDistance(currentLatitude, currentLongitude); // 自行實現距離計算邏輯
                    exerciseDistance.setText(String.format(Locale.getDefault(), "%.2f 公里", totalDistance / 1000));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener); // 每分鐘更新一次位置
        // 在開始新的運動時重置距離和卡路里
        totalDistance = 0.0;
        exerciseDistance.setText(String.format(Locale.getDefault(), "%.2f 公里", totalDistance / 1000));
        caloriesBurned.setText("消耗卡路里：0");

    }


    // 暫停運動的邏輯
    private void pauseExercise() {
        handler.removeCallbacks(updateTimerRunnable);
        locationManager.removeUpdates(locationListener);

        int calories = calculateCalories(totalDistance / 1000);
        caloriesBurned.setText(String.format("消耗卡路里：%d", calories));
        //調用一個方法（待製作）來將數據傳送給後端寫入資料庫供AI-Service使用，要先討論想使用什麼技術和步驟
    }

    // 根據運動模式計算卡路里
    private int calculateCalories(double distance) {
        return isRunMode ? (int) (distance * 120) : (int) (distance * 80); // 假設跑步每公里120卡路里，健走每公里80卡路里
    }


    // 計算兩個地理位置之間的距離
    private double calculateDistance(double currentLatitude, double currentLongitude) {
        if (isFirstLocation) {
            // 如果是第一次獲取位置，設置為初始位置
            lastLatitude = currentLatitude;
            lastLongitude = currentLongitude;
            isFirstLocation = false;
            return 0;
        }

        // 定義一個用來存儲結果的數組
        float[] results = new float[1];

        // 計算兩點間的距離，並存儲在 results[0] 中
        Location.distanceBetween(lastLatitude, lastLongitude, currentLatitude, currentLongitude, results);

        // 更新最後的位置
        lastLatitude = currentLatitude;
        lastLongitude = currentLongitude;

        // 返回距離，單位是米
        return results[0];
    }

}
