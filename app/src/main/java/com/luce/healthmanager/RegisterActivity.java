package com.luce.healthmanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput, confirmPasswordInput, emailInput, phoneInput, birthdayInput;
    private Button registerButton;

    private OkHttpClient client = new OkHttpClient();

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
    }

    // 用戶註冊邏輯
    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String birthday = birthdayInput.getText().toString().trim();

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
            jsonBody.put("dateOfBirth", birthday);
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
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "註冊成功", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "註冊失敗", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
