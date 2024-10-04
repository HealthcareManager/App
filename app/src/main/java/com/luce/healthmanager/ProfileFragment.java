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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

public class ProfileFragment extends Fragment {

    private static final int REQUEST_UPDATE_PROFILE = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false); // 設置 Fragment 佈局

        // 從 SharedPreferences 讀取用戶資料
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("jwt_token", null);
        String username = sharedPreferences.getString("username", "未登入");
        String userId = sharedPreferences.getString("userId", "");
        String userImage = sharedPreferences.getString("userImage", "");

        Log.d("ProfileFragment", "User image URL: " + userImage);

        // 設置 TextView 與 ImageView
        TextView userNameTextView = view.findViewById(R.id.user_name);
        TextView userIdTextView = view.findViewById(R.id.user_id);
        ImageView avatar = view.findViewById(R.id.profile_image);
        Button logoutButton = view.findViewById(R.id.logout_button);
        LinearLayout userdata = view.findViewById(R.id.userdata);
        LinearLayout cardprime = view.findViewById(R.id.cardprime);
        LinearLayout aboutme = view.findViewById(R.id.aboutme);

        // 設置用戶名和 ID
        userNameTextView.setText(username);
        userIdTextView.setText("ID: " + userId);

        // 檢查用戶是否已登入，控制登出按鈕顯示
        if (!userId.isEmpty()) {
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            logoutButton.setVisibility(View.GONE);
        }

        // 加載用戶圖片
        loadUserImage(jwtToken, userImage, avatar);

        // 點擊事件: 幫助與回饋
        view.findViewById(R.id.help_feedback_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HelpActivity.class));
            }
        });

        // 點擊事件: 關於我們頁面
        aboutme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(), AboutmeActivity.class));
            }
        });

        // 點擊事件: 用戶資料頁面
        userdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), UserDataActivity.class);
                startActivityForResult(intent, REQUEST_UPDATE_PROFILE);
            }
        });

        // 點擊事件: 控制登入/登出操作
        if (jwtToken == null) {
            // 用戶未登入，點擊圖片或名稱跳轉到登入頁面
            View.OnClickListener loginListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                }
            };
            avatar.setOnClickListener(loginListener);
            userNameTextView.setOnClickListener(loginListener);
        } else {
            // 用戶已登入，不執行點擊操作，或者開啟編輯功能
            avatar.setOnClickListener(null);
            userNameTextView.setOnClickListener(null);
        }

        // 點擊事件: 登出按鈕
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        return view;
    }

    // 每次返回頁面時重新加載用戶圖片及數據
    @Override
    public void onResume() {
        super.onResume();
        refreshUserData(); // 刷新資料
    }

    // 加載用戶圖片
    private void loadUserImage(String jwtToken, String userImage, ImageView avatar) {
        if (jwtToken != null && !userImage.isEmpty()) {
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
            // 沒有圖片路徑或 token，顯示預設的 drawable 圖片
            avatar.setImageResource(R.drawable.chatbot);
        }
    }

    // 刷新用戶數據
    private void refreshUserData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("jwt_token", null);
        String username = sharedPreferences.getString("username", "未登入");
        String userId = sharedPreferences.getString("userId", "");
        String userImage = sharedPreferences.getString("userImage", "");

        TextView userNameTextView = getView().findViewById(R.id.user_name);
        TextView userIdTextView = getView().findViewById(R.id.user_id);
        ImageView avatar = getView().findViewById(R.id.profile_image);

        // 更新用戶名和ID
        userNameTextView.setText(username);
        userIdTextView.setText("ID: " + userId);

        // 加載用戶圖片
        loadUserImage(jwtToken, userImage, avatar);
    }

    // 登出用戶並清除 SharedPreferences
    private void logoutUser() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // 清除所有保存的資料
        editor.apply();  // 應用更改

        // 重新加載頁面
        getActivity().recreate();
    }
}
