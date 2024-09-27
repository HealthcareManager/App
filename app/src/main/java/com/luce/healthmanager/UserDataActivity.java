package com.luce.healthmanager;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserDataActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;  // 用於選擇圖片的請求碼
    private static final int REQUEST_PERMISSION_CODE = 100;
    private ImageView userAvatar;  // 用戶頭像 ImageView
    private Uri imageUri;  // 圖片選擇的 URI
    private Button btnChooseButton, saveButton; // 選擇頭像按鈕
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        // 創建 Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/api/auth/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        btnChooseButton = findViewById(R.id.btn_choose);
        userAvatar = findViewById(R.id.userAvatar);
        saveButton = findViewById(R.id.save_button);

        // 當按下註冊按鈕時觸發圖片上傳
        saveButton.setOnClickListener(v -> {
            if (imageUri != null) {
                // 如果已經選擇並裁剪圖片，開始上傳
                uploadImageToServer(imageUri);
            } else {
                // 如果未選擇圖片，顯示提示
                Toast.makeText(UserDataActivity.this, "請先選擇圖片", Toast.LENGTH_SHORT).show();
            }
        });

        // 設置按鈕點擊事件，選擇圖片
        btnChooseButton.setOnClickListener(v -> selectImageFromGallery());
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
                Log.d("imagelink", String.valueOf(userAvatar));
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_SHORT).show();  // 顯示裁剪錯誤
            }
        }
    }

    private void uploadImageToServer(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = getBytesFromInputStream(inputStream);

            // 構建 RequestBody
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile);

            // 使用 Retrofit 上傳圖片
            Call<ResponseBody> call = apiService.uploadImage(1L, body); // 假設用戶 ID 為 1
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("Upload Response", response.toString());
                    if (response.isSuccessful()) {
                        Toast.makeText(UserDataActivity.this, "上傳成功", Toast.LENGTH_SHORT).show();
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
        options.setHideBottomControls(true);  // 隱藏底部控制工具

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