package com.luce.healthmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.luce.healthmanager.data.api.ApiService;
import com.luce.healthmanager.data.network.ApiClient;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDataActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int PICK_IMAGE_REQUEST = 1;  // 用於選擇圖片的請求碼
    private static final int REQUEST_PERMISSION_CODE = 100;
    private ImageView userAvatar, usernameArrow, genderArrow, passwordArrow;  // 用戶頭像 ImageView
    private Uri imageUri;  // 圖片選擇的 URI
    private Button btnChooseButton, saveButton;
    private SharedPreferences sharedPreferences;
    private TextView usernameData, emailData, genderData, heightData, weightData, birthdayData;
    private boolean updatedImage = false;
    private String userId, formattedDate;
    private ImageButton backButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        btnChooseButton = findViewById(R.id.btn_choose);
        userAvatar = findViewById(R.id.userAvatar);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back_button);
        usernameData = findViewById(R.id.username_data);
        emailData = findViewById(R.id.email_data);
        genderData = findViewById(R.id.gender_data);
        heightData = findViewById(R.id.height_data);
        weightData = findViewById(R.id.weight_data);
        birthdayData = findViewById(R.id.birthday_data);
        genderArrow = findViewById(R.id.gender_arrow);
        usernameArrow = findViewById(R.id.username_arrow);
        passwordArrow = findViewById(R.id.password_arrow);


        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt_token", null);
        userId = sharedPreferences.getString("userId", "");
        String username = sharedPreferences.getString("username", "用戶名稱");
        String email = sharedPreferences.getString("email", "電子郵件");
        String gender = sharedPreferences.getString("gender", "性別");
        String height = sharedPreferences.getString("height", "身高");
        String weight = sharedPreferences.getString("weight", "體重");
        String birthday = sharedPreferences.getString("dateOfBirth", "生日");
        String userImage = sharedPreferences.getString("userImage", "圖片");

        Log.d("Yuchen", gender);

        usernameData.setText("用戶名稱：" + username);
        emailData.setText("帳號：" + email);

        if (gender.equals("MALE")) {
            gender = "男";
        } else if (gender.equals("FEMALE")) {
            gender= "女";
        } else {
            gender = "";
        }
        genderData.setText("性別：" + gender);
        heightData.setText("身高：" + height + "公分");
        weightData.setText("體重：" + weight + "公斤");

        if (birthday.isEmpty()) {
            formattedDate = "";
        } else {
            formattedDate = birthday.replace("-", "年").replaceFirst("年", "年").replaceFirst("-", "月") + "日";
        }
        birthdayData.setText("生日：" + formattedDate);

        if (token != null && !userImage.isEmpty() && !updatedImage) {
            // 使用 Glide 加載圖片並添加 Token 驗證
            GlideUrl glideUrl = new GlideUrl(userImage, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + token)  // 添加 JWT Token 驗證
                    .build());

            // 加載圖片
            Glide.with(this)
                    .load(glideUrl)  // 使用自定義的 GlideUrl 加載圖片
                    .circleCrop()
                    .placeholder(R.drawable.chatbot)  // 加載中的預設圖片
                    .error(R.drawable.chatbot)        // 加載失敗的預設圖片
                    .into(userAvatar);
        } else {
            // 沒有圖片路徑或 token，顯示預設的 drawable 圖片
            userAvatar.setImageResource(R.drawable.chatbot);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 返回結果給上一個 Activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedImage", updatedImage);
                setResult(RESULT_OK, resultIntent);

                finish(); // 返回到上一個 Activity
            }
        });

        // 當按下註冊按鈕時觸發圖片上傳
        saveButton.setOnClickListener(v -> {
            if (imageUri != null) {
                // 如果已經選擇並裁剪圖片，開始上傳
                uploadImageToServer(imageUri, userId, token);
            }
        });

        // 設置按鈕點擊事件，選擇圖片
        btnChooseButton.setOnClickListener(v -> selectImageFromGallery());
        genderArrow.setOnClickListener(view -> showGenderPickerDialog());
        usernameArrow.setOnClickListener(view -> showUsernameDialog());
        passwordArrow.setOnClickListener(view -> showPasswordDialog());
    }

    private void showPasswordDialog() {
        // 創建一個對話框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("修改密碼");

        // 設置對話框的佈局
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // 創建 EditText 來輸入舊密碼
        final EditText oldPasswordInput = new EditText(this);
        oldPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        oldPasswordInput.setHint("舊密碼");
        layout.addView(oldPasswordInput); // 添加到佈局

        // 創建 EditText 來輸入新密碼
        final EditText newPasswordInput = new EditText(this);
        newPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPasswordInput.setHint("新密碼");
        layout.addView(newPasswordInput); // 添加到佈局

        // 創建 EditText 來確認新密碼
        final EditText confirmPasswordInput = new EditText(this);
        confirmPasswordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPasswordInput.setHint("確認新密碼");
        layout.addView(confirmPasswordInput); // 添加到佈局

        builder.setView(layout);

        // 設置按鈕的行為
        builder.setPositiveButton("確定", (dialog, which) -> {
                    String oldPassword = oldPasswordInput.getText().toString();
                    String newPassword = newPasswordInput.getText().toString();
                    String confirmPassword = confirmPasswordInput.getText().toString();

                    // 檢查舊密碼和新密碼的有效性
                    if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                        Toast.makeText(this, "請填寫所有字段", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!newPassword.equals(confirmPassword)) {
                        Toast.makeText(this, "新密碼和確認新密碼不一致", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, String> passwordUpdate = new HashMap<>();
                    passwordUpdate.put("userId", userId);
                    passwordUpdate.put("oldPassword", oldPassword);
                    passwordUpdate.put("newPassword", newPassword);

                    Call<ResponseBody> updateCall = apiService.updatePassword(passwordUpdate);
                    updateCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(UserDataActivity.this, "密碼已成功更新", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    // 獲取錯誤訊息
                                    String errorMessage = response.errorBody().string();

                                    if (errorMessage.contains("舊密碼不正確")){
                                        Toast.makeText(UserDataActivity.this, "舊密碼不正確", Toast.LENGTH_SHORT).show();
                                    } else if (errorMessage.contains("用戶不存在")){
                                        Toast.makeText(UserDataActivity.this, "用戶不存在", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.d("Yuchen", errorMessage);
                                        Toast.makeText(UserDataActivity.this, "更新失敗", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(UserDataActivity.this, "無法處理錯誤訊息", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(UserDataActivity.this, "更新請求失敗: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void updateDataToServer(Map<String, Object> updateData) {

        // 發送請求更新用戶資料
        Call<ResponseBody> call = apiService.updateUserData(userId, updateData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserDataActivity.this, "資料更新成功", Toast.LENGTH_SHORT).show();

                    // 更新 SharedPreferences 中的數據
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // 更新每個需要更新的字段
                    if (updateData.containsKey("username")) {
                        editor.putString("username", (String) updateData.get("username"));
                    }
                    if (updateData.containsKey("gender")) {
                        editor.putString("gender", (String) updateData.get("gender"));
                    }

                    editor.apply(); // 提交更改
                } else {
                    try {
                        // 如果資料更新失敗，檢查後端返回的錯誤信息
                        String errorMessage = response.errorBody().string();
                        if (errorMessage.contains("該用戶名已被使用")) {
                            // 如果是因為用戶名重複的原因，顯示相應的錯誤訊息
                            Toast.makeText(UserDataActivity.this, "該用戶名已被使用，請換一個名稱", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他錯誤
                            Toast.makeText(UserDataActivity.this, "資料更新失敗", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(UserDataActivity.this, "無法解析錯誤信息", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UserDataActivity.this, "更新請求失敗: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 用戶名稱
    private void showUsernameDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("請輸入您的名字")
                .setView(editText)
                .setPositiveButton("確定", (dialog, which) -> {
                    String newUsername = editText.getText().toString().trim();
                    if (!newUsername.isEmpty()) {
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("username", newUsername);

                        updateDataToServer(updateData);
                        usernameData.setText("用戶名稱：" + newUsername);
                    } else {
                        Toast.makeText(this, "名稱不能為空", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 顯示性別選擇的 Dialog
    private void showGenderPickerDialog() {
        final String[] genderOptions = {"男", "女", "其他"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("請選擇您的性別")
                .setItems(genderOptions, (dialog, which) -> {
                    // 用戶選擇性別後更新顯示
                    String selectedGender = genderOptions[which]; // 獲取選擇的性別
                    TextView genderTextView = findViewById(R.id.gender_data); // 找到顯示性別的 TextView
                    genderTextView.setText(selectedGender); // 更新 TextView 的顯示

                    String genderString;
                    switch (selectedGender) {
                        case "男":
                            genderString = "MALE";
                            break;
                        case "女":
                            genderString = "FEMALE";
                            break;
                        case "其他":
                        default:
                            genderString = "OTHER";
                            break;
                    }

                    // 準備更新數據
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("gender", genderString); // 將選擇的性別加入更新數據

                    updateDataToServer(updateData); // 呼叫更新方法
                    genderData.setText("性別：" + selectedGender);
                })
                .show();
    }

    // 打開相冊選擇圖片
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);  // 啟動選擇圖片的意圖
    }

    // 接收並處理選擇圖片及裁剪結果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // 開始裁剪
                    startCrop(selectedImageUri);
                }
            } else if (requestCode == UCrop.REQUEST_CROP && data != null) {
                Uri croppedImageUri = UCrop.getOutput(data);
                if (croppedImageUri != null) {
                    // 更新裁剪後的圖片 URI
                    imageUri = croppedImageUri;
                    updatePreviewImage(imageUri);
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            // 處理裁剪錯誤
            Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 更新預覽圖片
    private void updatePreviewImage(Uri imageUri) {
        Glide.with(this)
                .load(imageUri)
                .circleCrop()  // 圓形裁剪
                .placeholder(R.drawable.chatbot)  // 預設圖片
                .error(R.drawable.chatbot)        // 加載失敗圖片
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 禁用磁碟緩存
                .skipMemoryCache(true)  // 跳過內存緩存
                .into(userAvatar);  // 加載到 ImageView

        // 確保 UI 重新繪製
        userAvatar.invalidate();
    }

    private void uploadImageToServer(Uri imageUri, String userId, String token) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = getBytesFromInputStream(inputStream);

            // 構建 RequestBody
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile);


            // 使用 Retrofit 上傳圖片
            Call<ResponseBody> call = apiService.uploadImage(userId, body); // 假設用戶 ID 為 1

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("Upload Response", response.toString());
                    if (response.isSuccessful()) {
                        Toast.makeText(UserDataActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();

                        try {
                            // 從伺服器回應中獲取圖片的URL
                            String jsonResponse = response.body().string();
                            JSONObject jsonObject = new JSONObject(jsonResponse);
                            String imageUrl = jsonObject.getString("filePath");

                            Log.d("imageURL", "imageURL is " + imageUrl);

                            // 更新SharedPreferences中的userImage
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userImage", imageUrl);  // 使用後端返回的圖片路徑
                            editor.apply();

                            updatedImage = true;  // 標記圖片已更新

                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.e("Upload Error", "Response Code: " + response.code());
                        try {
                            String errorResponse = response.errorBody().string();
                            Log.e("Upload Error", errorResponse);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(UserDataActivity.this, "上傳失敗", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(UserDataActivity.this, "請求失敗: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            byteBuffer.write(data, 0, nRead);
        }
        byteBuffer.flush();
        return byteBuffer.toByteArray();
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
        options.setHideBottomControls(false);  // 隱藏底部控制工具

        // 啟動 uCrop，設置源圖片 URI 和目標裁剪圖片的 URI
        UCrop.of(uri, destinationUri)
                .withAspectRatio(1, 1)  // 設置裁剪比例 1:1
                .withMaxResultSize(500, 500)  // 設置裁剪後圖片的最大尺寸
                .withOptions(options)
                .start(this);  // 啟動裁剪活動
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 權限被授予
            } else {
                // 權限拒絕
                Toast.makeText(this, "需要讀取存儲的權限才能選擇圖片", Toast.LENGTH_SHORT).show();
            }
        }
    }

}