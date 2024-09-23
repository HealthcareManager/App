package com.luce.healthmanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput, confirmPasswordInput, emailInput, phoneInput, birthdayInput;
    private Button registerButton, googleLoginButton, facebookLoginButton;

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
        googleLoginButton = findViewById(R.id.google_login_button);
        facebookLoginButton = findViewById(R.id.facebook_login_button);

        // 註冊按鈕點擊事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Google 登入按鈕點擊事件
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Google 登入處理
                googleSignIn();
            }
        });

        // Facebook 登入按鈕點擊事件
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Facebook 登入處理
                facebookSignIn();
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

    // 顯示日期選擇器
    private void showDatePickerDialog() {
        // 獲取當前日期
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // 創建 DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RegisterActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // 格式化日期並設置到 EditText 中 (yyyy-MM-dd)
                        String formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        birthdayInput.setText(formattedDate);
                    }
                },
                year, month, day
        );
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

        // 將用戶資料送至後端
        // sendUserDataToServer(username, password, email, phone, birthday);

        Toast.makeText(RegisterActivity.this, "註冊成功", Toast.LENGTH_SHORT).show();
    }

    // Google 登入處理邏輯
    private void googleSignIn() {
        // 這裡實作 Google 登入邏輯，例如使用 GoogleSignInClient
        Toast.makeText(RegisterActivity.this, "Google 登入", Toast.LENGTH_SHORT).show();
    }

    // Facebook 登入處理邏輯
    private void facebookSignIn() {
        // 這裡實作 Facebook 登入邏輯
        Toast.makeText(RegisterActivity.this, "Facebook 登入", Toast.LENGTH_SHORT).show();
    }

    // 這裡可以實作一個方法，將用戶資料透過 API 送至後端伺服器
    // private void sendUserDataToServer(String username, String password, String email, String phone, String birthday) {
    //     // 實作與後端 API 的連接
    // }
}
