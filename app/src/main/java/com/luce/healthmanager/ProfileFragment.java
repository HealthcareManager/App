package com.luce.healthmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

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

public class ProfileFragment extends Fragment {

    private static final int REQUEST_UPDATE_PROFILE = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false); // 正確設置 Fragment 佈局

        // 從 SharedPreferences 讀取用戶資料
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        Log.d("test","sharedPreferences is " + sharedPreferences);
        // 檢查是否已登入
        String jwtToken = sharedPreferences.getString("jwt_token", null);
        String username = sharedPreferences.getString("username", "未登入");
        String userId = sharedPreferences.getString("userId", "");
        String userImage = sharedPreferences.getString("userImage", "");

        Log.d("Yuchen", userImage);
        // 更新 TextView
        TextView userNameTextView = view.findViewById(R.id.user_name);
        TextView userIdTextView = view.findViewById(R.id.user_id);

        userNameTextView.setText(username);
        userIdTextView.setText("ID: " + userId);

        ImageView avatar = view.findViewById(R.id.profile_image);
        TextView userName = view.findViewById(R.id.user_name);
        LinearLayout userdata = view.findViewById(R.id.userdata);
        Button logoutButton = view.findViewById(R.id.logout_button); // 使用 view.findViewById
        LinearLayout cardprime = view.findViewById(R.id.cardprime);
        LinearLayout aboutme = view.findViewById(R.id.aboutme);

        // 檢查用戶是否已登入
        if (!userId.isEmpty()) {
            // 用戶已登入，顯示登出按鈕
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            // 用戶未登入，隱藏登出按鈕
            logoutButton.setVisibility(View.GONE);
        }

        // 轉向關於幫助與回饋
        LinearLayout helpFeedbackCard = view.findViewById(R.id.help_feedback_card);
        helpFeedbackCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);
            }
        });

        // 轉向關於我們頁面
        aboutme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), AboutmeActivity.class);
                startActivity(intent);
            }
        });


        if (jwtToken != null && !userImage.isEmpty()) {
            // 使用 Glide 加載圖片並添加 Token 驗證
            GlideUrl glideUrl = new GlideUrl(userImage, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + jwtToken)  // 添加 JWT Token 驗證
                    .build());

            Log.d("Yuchen", userImage);

            // 加載圖片
            Glide.with(this)
                    .load(glideUrl)  // 使用自定義的 GlideUrl 加載圖片
                    .circleCrop()
                    .placeholder(R.drawable.chatbot)  // 加載中的預設圖片
                    .error(R.drawable.chatbot)        // 加載失敗的預設圖片
                    .into(avatar);
        } else {
            // 沒有圖片路徑或 token，顯示預設的 drawable 圖片
            avatar.setImageResource(R.drawable.chatbot);
        }


        cardprime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 構建請求體
                String requestBody = "{\n" +
                "    \"amount\": 100,\n" +
                "    \"currency\": \"TWD\",\n" +
                "    \"orderId\": \"order202410010001\",\n" +
                "    \"packages\": [\n" +
                "        {\n" +
                "            \"name\": \"RebeccaShop\",\n" +
                "            \"amount\": 100,\n" +
                "            \"products\": [\n" +
                "                {\n" +
                "                    \"name\": \"VIP\",\n" +
                "                    \"imageUrl\": \"\",\n" +
                "                    \"quantity\": 1,\n" +
                "                    \"price\": 100\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"redirectUrls\": {\n" +
                "        \"confirmUrl\": \"https://www.google.com.tw\",\n" +
                "        \"cancelUrl\": \"https://www.google.com.tw\"\n" +
                "    }\n" +
                "}"; // 請求體
        
                OkHttpClient client = new OkHttpClient();
        
                RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json"));
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8080/api/payment") // 在模擬器中使用
                        .post(body)
                        .build();
        
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }
        
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String responseData = response.body().string();
        
                            // 解析 JSON 響應
                            try {
                                JSONObject jsonResponse = new JSONObject(responseData);
                                if (jsonResponse.getString("status").equals("success")) {
                                    JSONObject responseBody = new JSONObject(jsonResponse.getString("response"));
                                    String paymentUrl = responseBody.getString("paymentUrl"); // 確保這裡正確解析到支付 URL
        
                                    // 跳轉到瀏覽器
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(paymentUrl));
                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });

        if (jwtToken == null ) {
            // 轉向登入頁面
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });

            // 轉向登入頁面
            userName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            // 已登入，不進行任何操作或執行其他已登入的操作
            avatar.setOnClickListener(null); // 或者可以打開用戶詳細頁面
            userName.setOnClickListener(null); // 或者可以讓用戶編輯個人資料
        }

        // 轉向用戶資料頁面
        userdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), UserDataActivity.class);
                startActivityForResult(intent, REQUEST_UPDATE_PROFILE);
            }
        });

        // 處理登出按鈕
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 這裡你可以處理登出邏輯，像是清除使用者資料並跳轉到登入頁面
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                // 清空 SharedPreferences 中的資料
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // 清除所有保存的資料
                editor.apply(); // 應用更改

                getActivity().recreate();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 每次返回頁面時重新加載用戶圖片及數據
        loadUserImage();
    }

    private void loadUserImage() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("jwt_token", null);
        String userImage = sharedPreferences.getString("userImage", "");

        ImageView avatar = getView().findViewById(R.id.profile_image);

        if (jwtToken != null && !userImage.isEmpty()) {
            // 使用 Glide 加載圖片
            GlideUrl glideUrl = new GlideUrl(userImage, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + jwtToken)
                    .build());

            Glide.with(this)
                    .load(glideUrl)
                    .circleCrop()
                    .placeholder(R.drawable.chatbot)
                    .error(R.drawable.chatbot)
                    .into(avatar);
        } else {
            // 顯示預設圖片
            avatar.setImageResource(R.drawable.chatbot);
        }
    }
}
