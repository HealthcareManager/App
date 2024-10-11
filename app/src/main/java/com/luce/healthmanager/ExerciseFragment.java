package com.luce.healthmanager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
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

import org.json.JSONObject;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    private String userId;
    private String jwtToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        // 從 SharedPreferences 讀取用戶資料，在 Fragment 初始化時獲取
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");  // 取得 userId
        jwtToken = sharedPreferences.getString("jwt_token", null);  // 取得 JWT Token

        // 初始化 MapView
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);  // 地圖準備好時的回調

        // 初始化 LocationManager
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        // 初始化 View
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
                    totalDistance += calculateDistance(currentLatitude, currentLongitude);  // 計算距離
                    exerciseDistance.setText(String.format(Locale.getDefault(), "%.2f 公里", totalDistance / 1000));

                    // 更新地圖攝影機位置
                    LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 13));  // 移動到當前位置
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        // 請求位置更新前確認權限
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);  // 每 5 秒更新位置

        // 開始新運動時重置距離和消耗卡路里
        totalDistance = 0.0;
        exerciseDistance.setText(String.format(Locale.getDefault(), "%.2f 公里", totalDistance / 1000));
        caloriesBurned.setText("消耗卡路里：0");
    }

    // 暫停運動的邏輯
    private void pauseExercise() {
        handler.removeCallbacks(updateTimerRunnable);
        locationManager.removeUpdates(locationListener);

        int calories = calculateCalories(totalDistance / 1000);  // 計算卡路里消耗
        long duration = SystemClock.elapsedRealtime() - startTime;  // 計算運動總時間
        String exerciseType = isRunMode ? "跑步" : "健走";  // 設定運動類型

        caloriesBurned.setText(String.format("消耗卡路里：%d", calories));

        // 使用事先獲取的 userId 和 jwtToken 發送請求
        sendExerciseData(userId, totalDistance / 1000, calories, duration, exerciseType, jwtToken);
    }

    private void sendExerciseData(String userId, double distance, int calories, long duration, String exerciseType, String jwtToken) {
        // 構建 JSON 請求體，符合資料表結構
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("userId", userId);  // 用戶ID
            requestBody.put("caloriesBurned", calories);  // 消耗的卡路里
            requestBody.put("kilometers", distance);  // 總距離
            requestBody.put("duration", duration);  // 運動總時長
            requestBody.put("exerciseType", exerciseType);  // 運動類型

            // 使用 SimpleDateFormat 生成 ISO 8601 格式的日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String isoTime = sdf.format(new Date());  // 獲取當前時間的 ISO 格式
            requestBody.put("createdAt", isoTime);  // 使用 ISO 8601 格式的時間  // 運動結束時間
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 使用 OkHttpClient 發送請求
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(requestBody.toString(), MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/exercise")  // 後端 API 的 URL
                .addHeader("Authorization", "Bearer " + jwtToken)  // 添加 JWT Token 驗證
                .post(body)
                .build();

        // 執行請求
        new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    System.out.println("Data sent successfully");
                } else {
                    System.out.println("Error: " + response.message());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 根據運動模式計算卡路里
    private int calculateCalories(double distance) {
        return isRunMode ? (int) (distance * 120) : (int) (distance * 80);  // 假設跑步每公里 120 卡路里，健走每公里 80 卡路里
    }

    // 計算兩點間的距離
    private double calculateDistance(double currentLatitude, double currentLongitude) {
        if (isFirstLocation) {
            lastLatitude = currentLatitude;
            lastLongitude = currentLongitude;
            isFirstLocation = false;
            return 0;
        }

        // 計算兩點間的距離
        float[] results = new float[1];
        Location.distanceBetween(lastLatitude, lastLongitude, currentLatitude, currentLongitude, results);

        lastLatitude = currentLatitude;
        lastLongitude = currentLongitude;

        return results[0];
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

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
