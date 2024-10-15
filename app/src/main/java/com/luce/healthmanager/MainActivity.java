package com.luce.healthmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private WebSocketManager webSocketManager;
    String webSocketUrl = "wss://healthcaremanager.myvnc.com:8443/HealthcareManager/membership-status";


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleCallback(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleCallback(getIntent());
    }

    private void handleCallback(Intent intent) {
        Uri data = intent.getData();
        Log.d("LINE_PAY", "Callback URL: " + data);

        if (data != null && data.toString().startsWith("com.luce.healthmanager://callback")) {
            String orderId = data.getQueryParameter("orderId");
            String result = data.getQueryParameter("result");

            if (result != null && result.equals("success") && orderId != null) {
                Log.d("LINE_PAY", "Order ID: " + orderId);

                // Show payment success notification
                runOnUiThread(() -> {

                    SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                    boolean hasShownToast = sharedPreferences.getBoolean("hasShownToast", false);
                    if (!hasShownToast) {
                        // 顯示 Toast
                        Toast.makeText(MainActivity.this, "Payment Successful", Toast.LENGTH_LONG).show();

                        // 導航到 ProfileFragment
                        replaceFragment(new ProfileFragment());

                        // 更新角色為 VIP
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String newRole = "VIP";
                        editor.putString("role", newRole);

                        // 設置 hasShownToast 為 true，防止再次顯示
                        editor.putBoolean("hasShownToast", true);
                        editor.apply();
                    }
                });

                // Send payment data to backend
                sendPaymentDataToBackend(orderId);
            } else {
                Log.e("LINE_PAY", "Payment failed, please try again");

                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Payment failed, please try again", Toast.LENGTH_LONG).show();
                });
            }
        }
    }

    private void sendPaymentDataToBackend(String orderId) {
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);
        String jwtToken = sharedPreferences.getString("jwt_token", null); // Define and retrieve jwtToken here
        Log.d("PaymentUpdate", "User ID: " + userId); // 確認用戶 ID

        if (userId != null) {
            try {
                JSONObject requestBody = new JSONObject();
                requestBody.put("orderId", orderId);
                requestBody.put("userId", userId); // 確保這裡包含 userId
                requestBody.put("status", "SUCCESS"); // Set status based on actual situation

                // Log to see the request body
                Log.d("PaymentUpdate", "Sending request with body: " + requestBody.toString());

                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(requestBody.toString(), MediaType.parse("application/json"));

                Request request = new Request.Builder()
                        .url("https://healthcaremanager.myvnc.com:8443/HealthcareManager/api/updatePaymentStatus")
                        .addHeader("Authorization", "Bearer " + jwtToken) // Add JWT Token authentication
                        .put(body) // Use PUT method
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Log.d("PaymentUpdate", "Successfully updated user payment status");
                        } else {
                            Log.e("PaymentUpdate", "Unable to update user payment status, response code: " + response.code());
                            Log.e("PaymentUpdate", "Response content: " + response.body().string());
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("PaymentUpdate", "Unable to find user ID");
        }
    }



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 加载 activity_login.xml 布局文件

        // 檢查是否已經有 token 儲存
        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt_token", null);
        Log.d("test","sharedPreferences is " + sharedPreferences);
        Log.d("test","token is " + token);

        if (token != null) {
            // 使用公共的 ParseTokenTask
            new ParseTokenTask(this, new ParseTokenTask.ParseTokenCallback() {
                @Override
                public void onParseTokenCompleted(JSONObject userData) {
                    if (userData != null) {
                        Toast.makeText(MainActivity.this, "歡迎回來", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "解析 token 失败", Toast.LENGTH_SHORT).show();
                        // 跳转到登录页面
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }).execute(token);
        } else {
            // 没有 token，跳转到登录页面
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
        }

        // 初始化 WebSocketManager
        webSocketManager = new WebSocketManager(new WebSocketManager.WebSocketCallback() {
            @Override
            public void onMessageReceived(String message) {
                Log.d("WebSocket at MAINACTIVITY", "Message received: " + message);

                // 处理收到的消息，并根据会员状态更新 UI
                handleWebSocketMessage(message);
            }
        });

        // 启动 WebSocket 连接，传入 URL 和 Token
        if (token != null && !token.isEmpty()) {
            webSocketManager.startWebSocket(webSocketUrl, token);
        } else {
            Log.e("WebSocket", "JWT Token is null or empty. Cannot create WebSocket connection.");
        }

        // 檢查是否要顯示 HealthFragment
        if (getIntent().getBooleanExtra("showHealthFragment", false)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HealthFragment()) // 替換為健康 Fragment
                    .commit();
        }

        // 隱藏 ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // 檢查是否有意圖
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null && data.getScheme().equals("com.luce.healthmanager")) {
            // 處理重定向的邏輯
            String code = data.getQueryParameter("code");
            // 使用這個 code 繼續進行後續操作
        }

        // 初始化 FragmentManager
        fragmentManager = getSupportFragmentManager();

        // 如果首次打開應用且沒有其他 Fragment，加載 HealthFragment
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new HealthFragment()) // 加載健康頁面的 Fragment
                    .commit();
        }

        // 設置健康按鈕的點擊事件來加載 HealthFragment
        ImageButton healthButton = findViewById(R.id.imageButton1);
        healthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new HealthFragment()); // 加載健康頁面的 Fragment
            }
        });

        // 設置運動按鈕的點擊事件來加載 ExerciseFragment
        ImageButton sportButton = findViewById(R.id.imageButton2);
        sportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ExerciseFragment()); // 加載運動頁面的 Fragment
            }
        });

        // 設置裝置按鈕的點擊事件來加載 DeviceFragment
        ImageButton deviceButton = findViewById(R.id.imageButton3);
        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new DeviceFragment()); // 加載裝置頁面的 Fragment
            }
        });

        // 設置個人中心按鈕的點擊事件來加載 ProfileFragment
        ImageButton profileButton = findViewById(R.id.imageButton4);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new ProfileFragment()); // 加載個人中心頁面的 Fragment
            }
        });

        // 設置 AI 助理按鈕的點擊事件來跳轉到 AiAssistantActivity
        ImageButton aiAssistantButton = findViewById(R.id.imageButton5);
        aiAssistantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳轉到 AiAssistantActivity
                Intent intent = new Intent(MainActivity.this, AiAssistantActivity.class);
                startActivity(intent);
            }
        });
    }

    // Fragment 切換方法
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null); // 讓使用者可以返回到之前的 Fragment
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭 WebSocket 连接
        webSocketManager.stopWebSocket();
    }

    // 处理 WebSocket 收到的消息
    private void handleWebSocketMessage(String message) {
        try {
            // 假设服务器返回 JSON 格式的消息
            JSONObject jsonMessage = new JSONObject(message);
            String membershipStatus = jsonMessage.getString("membershipStatus");
            Log.d("123","membershipStatus is" + membershipStatus);

//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, new ProfileFragment(), "ProfileFragment")
//                    .commit();
//            Log.d("FragmentCheck", "ProfileFragment 正在添加...");
//            Fragment fragment = fragmentManager.findFragmentByTag("ProfileFragment");
//
//            if (fragment == null) {
//                Log.d("FragmentCheck", "ProfileFragment not found");
//            } else if (fragment instanceof ProfileFragment) {
//                ProfileFragment profileFragment = (ProfileFragment) fragment;

                if ("USER".equals(membershipStatus)) {
                    Log.d("MembershipCheck", "會員狀態是 USER");

                    // 获取 SharedPreferences 实例
                    SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // 更新 role 值为 "USER"
                    editor.putString("role", "USER");
                    editor.apply();  // 提交更改

                    // 隐藏黄冠图标并提示会员已过期
                    runOnUiThread(() -> {
                        //profileFragment.updateCrownIconVisibility(false);
                        Toast.makeText(MainActivity.this, "您的會員已過期", Toast.LENGTH_SHORT).show();
                    });

                } else {
                    Log.d("MembershipCheck", "會員狀態不是 USER");

                    // 显示黄冠图标
                    //runOnUiThread(() -> profileFragment.updateCrownIconVisibility(true));

                    // 获取 SharedPreferences 实例
                    SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // 更新 role 值为其他值，例如 "VIP" 或其他状态
                    editor.putString("role", "VIP");  // 假设会员没有过期时状态为 VIP
                    editor.apply();  // 提交更改
                }
//            } else {
//                Log.d("FragmentCheck", "找到的 Fragment 不是 ProfileFragment 实例");
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}