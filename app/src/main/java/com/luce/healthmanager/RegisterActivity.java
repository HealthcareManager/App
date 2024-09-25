package com.luce.healthmanager;

import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.net.Uri;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;


import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput, confirmPasswordInput, emailInput, phoneInput, birthdayInput;
    private OkHttpClient client = new OkHttpClient();
    private Button registerButton, googleLoginButton, facebookLoginButton;
    private static final int PICK_IMAGE_REQUEST = 1;  // 用於選擇圖片的請求碼
    private ImageView userAvatar;  // 用戶頭像 ImageView
    private Uri imageUri;  // 圖片選擇的 URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 取得所有輸入欄位的參考
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        emailInput = findViewById(R.id.email_input);
        phoneInput = findViewById(R.id.phone_input);
        birthdayInput = findViewById(R.id.birthday_input);


        registerButton = findViewById(R.id.register_button);

        // 註冊按鈕點擊事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // 設定生日欄位點擊事件，顯示 DatePickerDialog
        birthdayInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        // 獲取當前日期
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // 暫時設置應用語言為中文
        Locale locale = new Locale("zh", "CN");  // "zh" 代表中文, "CN" 代表中國大陸（簡體中文）。"TW" 代表台灣（繁體中文）。
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // 創建 DatePickerDialog 並顯示
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RegisterActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // 格式化日期並設置到 EditText 中 (yyyy年MM月dd日)
                        String formattedDate = String.format("%04d年%02d月%02d日", selectedYear, selectedMonth + 1, selectedDay);
                        birthdayInput.setText(formattedDate);
                    }
                },
                year, month, day
        );

        // 設置日期範圍
        Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.YEAR, year - 100); // 最多往前 100 年
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // 顯示日期選擇器
        datePickerDialog.show();
    }

    // 用戶註冊邏輯
    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String birthday = birthdayInput.getText().toString().trim();
        String formattedBirthday = birthday.replaceAll("年", "-").replaceAll("月", "-").replaceAll("日", "");

        // 檢查輸入欄位是否正確
        if (TextUtils.isEmpty(username)) {
            usernameInput.setError("請輸入帳號");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("請輸入密碼");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword) || !password.equals(confirmPassword)) {
            confirmPasswordInput.setError("密碼不相符");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("請輸入郵件地址");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneInput.setError("請輸入手機號碼");
            return;
        }

        if (TextUtils.isEmpty(birthday)) {
            birthdayInput.setError("請輸入生日");
            return;
        }

        // 建立 JSON 請求體
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);
            jsonBody.put("email", email);
            jsonBody.put("phoneNumber", phone);
            jsonBody.put("dateOfBirth", formattedBirthday);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 呼叫 API
        sendUserDataToServer(jsonBody.toString());
    }

    private void sendUserDataToServer(String json) {
        // 定義 MediaType
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        // 建立 RequestBody
        RequestBody body = RequestBody.create(JSON, json);

        // 建立請求
        Request request = new Request.Builder()
                .url("http://192.168.50.38:8080/HealthcareManager/api/auth/register")
                .post(body)
                .build();

        // 發送請求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 處理錯誤
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "註冊失敗", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, "註冊成功", Toast.LENGTH_SHORT).show();
                        // 跳轉到登錄頁面
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();  // 可選：結束註冊頁面
                    });
                } else {
                    // 讀取後端返回的錯誤消息
                    String errorMessage = response.body() != null ? response.body().string() : "註冊失敗，請稍後再試";
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
