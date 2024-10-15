package com.luce.healthmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.luce.healthmanager.data.api.ApiService;
import com.luce.healthmanager.data.network.ApiClient;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private String token;
    ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordInput = findViewById(R.id.new_password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        Button resetPasswordButton = findViewById(R.id.reset_password_button);

        // 從深層連結中獲取 token
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            token = data.getQueryParameter("token");
            Log.d("ResetPasswordActivity", "Received token: " + token);
        }

        // 提交按鈕點擊事件
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                // 檢查兩次密碼是否一致
                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ResetPasswordActivity.this, "請填寫所有字段", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(ResetPasswordActivity.this, "兩次密碼不一致", Toast.LENGTH_SHORT).show();
                } else {
                    // 發送新密碼和 token 到後端進行處理
                    sendResetPassword(newPassword, token);
                }
            }
        });
    }

    // 發送新密碼到後端的方法
    private void sendResetPassword(String newPassword, String token) {
        // 發送重設密碼請求
        Call<ResponseBody> call = apiService.sendResetPassword(newPassword, token);

        // 執行請求
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 密碼重設成功
                    String message = null;
                    try {
                        message = response.body().string();  // 從響應中獲取後端返回的消息
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();

                    // 跳轉回登錄頁面
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // 請求失敗，顯示後端返回的錯誤消息
                    try {
                        String errorMessage = response.errorBody().string();  // 從錯誤響應中獲取消息
                        Toast.makeText(ResetPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ResetPasswordActivity.this, "解析錯誤，請稍後重試", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ResetPasswordError", "請求失敗", t);
                // 處理網絡錯誤
                Toast.makeText(ResetPasswordActivity.this, "網絡錯誤，請稍後重試", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

