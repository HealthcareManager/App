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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

public class ExerciseFragment extends Fragment implements OnMapReadyCallback {

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

    private MapView mapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        // 初始化 MapView
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);  // 地圖準備好時的回調

        // 初始化 LocationManager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // 初始化View
        exerciseDistance = view.findViewById(R.id.exercise_distance);
        exerciseTime = view.findViewById(R.id.exercise_time);
        caloriesBurned = view.findViewById(R.id.calories_burned);
        btnRun = view.findViewById(R.id.btn_run);
        btnWalk = view.findViewById(R.id.btn_walk);
        startButton = view.findViewById(R.id.start_exercise_button);

        // 跑步按鈕點擊事件
        btnRun.setOnClickListener(v -> {
            btnRun.setBackgroundResource(R.color.colorPrimary);  // 點擊的按鈕
            btnWalk.setBackgroundResource(R.color.colorPrimaryDark);  // 恢復另一个按鈕的顏色
            isRunMode = true;
        });

        // 健走按鈕點擊事件
        btnWalk.setOnClickListener(v -> {
            btnWalk.setBackgroundResource(R.color.colorPrimary);  // 點擊的按鈕
            btnRun.setBackgroundResource(R.color.colorPrimaryDark);  // 恢復另一个按鈕的顏色
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
                handler.postDelayed(this, 1000);  // 每秒更新一次
            }
        };
        handler.post(updateTimerRunnable);

        // 初始化 LocationListener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // 計算新的邏輯並更新介面
                if (isRunning) {
                    double currentLatitude = location.getLatitude();
                    double currentLongitude = location.getLongitude();
                    totalDistance += calculateDistance(currentLatitude, currentLongitude);  // 實現距離計算邏輯
                    exerciseDistance.setText(String.format(Locale.getDefault(), "%.2f 公里", totalDistance / 1000));

                    // 更新地圖攝影機位置
                    LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));  // 實時移動到當前位置並縮放
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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);  // 每 5 秒更新一次位置

        // 開始新運動時重置距離和消耗卡路里
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
        // 調用一個方法（待制作）將數據寫入資料庫供 AI-Service 使用
    }

    // 根據運動模式計算卡路里
    private int calculateCalories(double distance) {
        return isRunMode ? (int) (distance * 120) : (int) (distance * 80);  // 假設跑步每公里 120 卡路里，健走每公里 80 卡路里
    }

    // 計算兩點間的距離
    private double calculateDistance(double currentLatitude, double currentLongitude) {
        if (isFirstLocation) {
            // 如果是第一次獲取位置，設置為初始位置
            lastLatitude = currentLatitude;
            lastLongitude = currentLongitude;
            isFirstLocation = false;
            return 0;
        }

        // 定義一個用來儲存結果的數組
        float[] results = new float[1];

        // 計算兩點間的距離，並儲存在results[0]中
        Location.distanceBetween(lastLatitude, lastLongitude, currentLatitude, currentLongitude, results);

        // 更新最后的位置
        lastLatitude = currentLatitude;
        lastLongitude = currentLongitude;

        // 返回距離，單位是公尺
        return results[0];
    }

    // onMapReady 方法初始化地圖
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // 檢查位置權限
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        // 設置地圖的默認位置
        LatLng defaultLocation = new LatLng(25.0330, 121.5654);  // 台北101
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
                }
            } else {
                Toast.makeText(getActivity(), "應用需要位置權限來跟蹤運動", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // MapView 生命周期管理
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
