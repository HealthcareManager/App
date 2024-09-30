package com.luce.healthmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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
import com.luce.healthmanager.data.api.ApiService;
import com.luce.healthmanager.data.network.ApiClient;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

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
    private ImageView userAvatar, usernameArrow, genderArrow, heightArrow, weightArrow, passwordArrow;  // 用戶頭像 ImageView
    private Uri imageUri;  // 圖片選擇的 URI
    private Button btnChooseButton, saveButton; // 選擇頭像按鈕
    private ImageButton backButton;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private TextView usernameData, emailData, genderData, heightData, weightData, birthdayData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        apiService = ApiClient.getClient().create(ApiService.class);

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
        heightArrow = findViewById(R.id.height_arrow);
        weightArrow = findViewById(R.id.weight_arrow);
        passwordArrow = findViewById(R.id.password_arrow);


        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt_token", "").trim();
        String userId = sharedPreferences.getString("userId", "");
        String username = sharedPreferences.getString("username", "用戶名稱");
        String email = sharedPreferences.getString("email", "電子郵件");
        String gender = sharedPreferences.getString("gender", "性別");
        String height = sharedPreferences.getString("height", "身高");
        String weight = sharedPreferences.getString("weight", "體重");
        String birthday = sharedPreferences.getString("dateOfBirth", "生日");
        String userImage = sharedPreferences.getString("userImage", "圖片");

        Log.d("Yuchen", userImage);

        usernameData.setText("用戶名稱：" + username);
        emailData.setText("帳號：" + email);

        if (gender.equals("MALE")) {
            gender = "男";
        } else {
            gender = "女";
        }
        genderData.setText("性別：" + gender);
        heightData.setText("身高：" + height + "公分");
        weightData.setText("體重：" + weight + "公斤");
        String formattedDate = birthday.replace("-", "年").replaceFirst("年", "年").replaceFirst("-", "月") + "日";
        birthdayData.setText("生日：" + formattedDate);

        if (!userImage.isEmpty()) {
            // 使用 Glide 載入圖片
            Glide.with(this)
                    .load(userImage)
                    .placeholder(R.drawable.chatbot)  // 加載中顯示的預設圖片
                    .error(R.drawable.chatbot)        // 加載錯誤時顯示的預設圖片
                    .circleCrop()                            // 將圖片裁剪為圓形
                    .into(userAvatar);                       // 將圖片加載到 ImageView 中
        } else {
            // 如果沒有用戶圖片，顯示預設圖片
            userAvatar.setImageResource(R.drawable.chatbot);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回到上一個 Activity
            }
        });

        // 當按下註冊按鈕時觸發圖片上傳
        saveButton.setOnClickListener(v -> {
            if (imageUri != null) {
                // 如果已經選擇並裁剪圖片，開始上傳
                uploadImageToServer(imageUri, token, userId);
            } else {
                // 如果未選擇圖片，顯示提示
                Toast.makeText(UserDataActivity.this, "請先選擇圖片", Toast.LENGTH_SHORT).show();
            }
        });

        // 設置按鈕點擊事件，選擇圖片
        btnChooseButton.setOnClickListener(v -> selectImageFromGallery());
        genderArrow.setOnClickListener(view -> showGenderPickerDialog());
        heightArrow.setOnClickListener(view -> showHeightPickerDialog());
        weightArrow.setOnClickListener(view -> showWeightPickerDialog());
        usernameArrow.setOnClickListener(view -> showUsernameDialog());
    }

    // 用戶名稱
    private void showUsernameDialog() {
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("你的名字")
                .setView(editText)
                .setPositiveButton("確定", (dialog, which) -> {
                    String inputValue = editText.getText().toString();
                    if (!inputValue.isEmpty()) {
                        usernameData.setText("用戶名稱：" + inputValue);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 體重
    private void showWeightPickerDialog() {
        final NumberPicker integerPicker = new NumberPicker(this);
        integerPicker.setMinValue(10);
        integerPicker.setMaxValue(250);
        integerPicker.setValue(60);

        final NumberPicker decimalPicker = new NumberPicker(this);
        decimalPicker.setMinValue(0);
        decimalPicker.setMaxValue(9);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        layout.addView(integerPicker);
        layout.addView(decimalPicker);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("你的體重 (公斤)")
                .setView(layout)
                .setPositiveButton("確定", (dialog, which) -> {
                    double newValue = integerPicker.getValue() + decimalPicker.getValue() * 0.1;
                    weightData.setText("體重：" + newValue + "公斤");
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 顯示修改身高
    private void showHeightPickerDialog() {
        final NumberPicker integerPicker = new NumberPicker(this);
        integerPicker.setMinValue(100);
        integerPicker.setMaxValue(250);
        integerPicker.setValue(150);

        final NumberPicker decimalPicker = new NumberPicker(this);
        decimalPicker.setMinValue(0);
        decimalPicker.setMaxValue(9);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        layout.addView(integerPicker);
        layout.addView(decimalPicker);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("你的身高 (公分)")
                .setView(layout)
                .setPositiveButton("確定", (dialog, which) -> {
                    double newValue = integerPicker.getValue() + decimalPicker.getValue() * 0.1;
                    heightData.setText("身高：" + newValue + "公分");
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 顯示性別選擇的 Dialog
    private void showGenderPickerDialog() {
        final String[] genderOptions = {"男", "女", "其他"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("選擇性別")
                .setItems(genderOptions, (dialog, which) -> {
                    // 用戶選擇性別後更新顯示
                    TextView genderData = findViewById(R.id.gender_data);
                    genderData.setText("性別：" + genderOptions[which]);
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();  // 取得選擇圖片的 URI
            if (imageUri != null) {
                startCrop(imageUri);  // 啟動裁剪流程
            }
        }

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK && data != null) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                try {
                    imageUri = resultUri;  // 使用裁剪後的圖片 URI 進行上傳
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    Bitmap circularBitmap = getCroppedBitmap(bitmap);
                    userAvatar.setImageBitmap(circularBitmap);  // 將圓形圖片設置到 ImageView
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    private void uploadImageToServer(Uri imageUri, String token, String userId) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] imageBytes = getBytesFromInputStream(inputStream);

            // 構建 RequestBody
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile);

            // 使用 Retrofit 上傳圖片
            Call<ResponseBody> call = apiService.uploadImageWithToken("Bearer " + token, userId, body); // 假設用戶 ID 為 1
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