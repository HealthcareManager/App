package com.luce.healthmanager;

import android.content.Intent;
import android.net.Uri;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.provider.MediaStore;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput, confirmPasswordInput, emailInput, phoneInput, birthdayInput;
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
        Button btnChooseButton = findViewById(R.id.btn_choose);
        userAvatar = findViewById(R.id.userAvatar);

        registerButton = findViewById(R.id.register_button);


        // 註冊按鈕點擊事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // 設置按鈕點擊事件，選擇圖片
        btnChooseButton.setOnClickListener(v -> selectImageFromGallery());


        // 設定生日欄位點擊事件，顯示 DatePickerDialog
        birthdayInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    // 打開相冊選擇圖片
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);  // 啟動選擇圖片的意圖
    }

    // 接收並處理選擇圖片及裁剪結果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();  // 取得選擇圖片的 URI
            if (imageUri != null) {
                startCrop(imageUri);  // 啟動裁剪流程
            }
        }

        // 接收裁剪後的結果
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null) {
            final Uri resultUri = UCrop.getOutput(data);  // 裁剪後的圖片 URI
            if (resultUri != null) {
                // 使用 Glide 加載並顯示圓形圖片
                Glide.with(this)
                        .load(resultUri)
                        .circleCrop()  // 將圖片裁切成圓形
                        .into(userAvatar);  // 將圖片設置到 ImageView
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_SHORT).show();  // 顯示裁剪錯誤
            }
        }
    }

    // 啟動 uCrop 裁剪
    private void startCrop(Uri uri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));  // 裁剪後圖片的保存路徑

        // 自定義 uCrop 選項
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(80);  // 設置壓縮品質
        options.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));  // 設置工具欄顏色
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));  // 設置狀態欄顏色
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.colorAccent));  // 設置控制顏色
        options.setCircleDimmedLayer(true);  // 設置裁剪框為圓形
        options.setShowCropGrid(false);  // 隱藏裁剪網格
        options.setHideBottomControls(true);  // 隱藏底部控制工具

        // 啟動 uCrop，設置源圖片 URI 和目標裁剪圖片的 URI
        UCrop.of(uri, destinationUri)
                .withAspectRatio(1, 1)  // 設置裁剪比例 1:1
                .withMaxResultSize(500, 500)  // 設置裁剪後圖片的最大尺寸
                .withOptions(options)
                .start(this);  // 啟動裁剪活動
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


}
