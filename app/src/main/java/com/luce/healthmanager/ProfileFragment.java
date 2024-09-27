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

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false); // 正確設置 Fragment 佈局

        // 從 SharedPreferences 讀取用戶資料
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "未登入");
        String userId = sharedPreferences.getString("userId", "");
        String userImage = sharedPreferences.getString("userImage", "http://10.0.2.2:8080/images/0cd67af1-4665-4bd7-8814-183bec1cdc4a_image.jpg");

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


        // 轉向關於我們頁面
        aboutme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), AboutmeActivity.class);
                startActivity(intent);
            }
        });


        if (userImage != null && !userImage.isEmpty()) {
            Log.d("123", userImage);

            // 使用 Glide 加載圖片並處理錯誤和預設圖片
            Glide.with(this)
                    .load(userImage)  // 加載 SharedPreferences 中的圖片路徑
                    .circleCrop()      // 將圖片裁切成圓形
                    .placeholder(R.drawable.chatbot)  // 加載中的預設圖片
                    .error(R.drawable.chatbot)        // 加載失敗的預設圖片
                    .into(avatar);
        } else {
            // 沒有圖片路徑，顯示預設的 drawable 圖片
            avatar.setImageResource(R.drawable.chatbot);  // 預設圖片
        }


        // 轉向付費頁面
        cardprime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

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

        // 轉向用戶資料頁面
        userdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), UserDataActivity.class);
                startActivity(intent);
            }
        });

        // 處理登出按鈕
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 這裡你可以處理登出邏輯，像是清除使用者資料並跳轉到登入頁面
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
