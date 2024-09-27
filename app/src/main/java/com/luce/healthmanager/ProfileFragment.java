package com.luce.healthmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false); // 正確設置 Fragment 佈局

        // 從 SharedPreferences 讀取用戶資料
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        Log.d("test","sharedPreferences is " + sharedPreferences);
        String username = sharedPreferences.getString("username", "未登入");
        String userId = sharedPreferences.getString("userId", "");

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
        if (!username.isEmpty()) {
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
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                // 清空 SharedPreferences 中的資料
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // 清除所有保存的資料
                editor.apply(); // 應用更改

                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
