package com.luce.healthmanager;

import android.os.Bundle;
import android.text.TextUtils;
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
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button resetPasswordButton;
    // 創建 Retrofit 客戶端
    ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password); // 绑定XML布局

        // 绑定视图组件
        emailInput = findViewById(R.id.email_input);
        resetPasswordButton = findViewById(R.id.reset_button);

        // 设置按钮点击事件
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotPasswordActivity.this, "請輸入電子郵件信箱", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendPasswordResetEmail(email);
            }
        });
    }

    private void sendPasswordResetEmail(String email) {

        // 創建 User 對象並設置 email
        UserResponse user = new UserResponse();
        user.setEmail(email);

        // 發送忘記密碼請求
        Call<ResponseBody> call = apiService.sendPasswordResetEmail(user);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 成功時，顯示後端返回的消息
                    String message = null;
                    try {
                        message = response.body().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                } else {
                    // 如果響應有錯誤，顯示錯誤消息
                    String errorMessage = "請求失敗，請重試";

                    // 嘗試從 response 中獲取錯誤消息
                    if (response.errorBody() != null) {
                        try {
                            // 解析錯誤消息，這裡將接收到後端返回的 "電子郵件不存在"
                            errorMessage = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ForgotPasswordError", "請求失敗", t);
                // 處理請求失敗情況
                Toast.makeText(ForgotPasswordActivity.this, "網絡錯誤，請稍後重試", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

