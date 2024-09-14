package com.luce.healthmanager;

import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 加載佈局文件
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 初始化圖片和用戶名稱按鈕
        ImageView profileImage = view.findViewById(R.id.profile_image);
        TextView userName = view.findViewById(R.id.user_name);

        // 點擊事件 - 點擊圖片可以更換圖片
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在這裡處理更換圖片的邏輯
                // 例如，調用圖片選擇器來讓用戶選擇新圖片
                // Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // startActivityForResult(intent, REQUEST_CODE);
            }
        });

        // 點擊事件 - 點擊用戶名稱
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在這裡處理點擊用戶名稱的邏輯
                // 例如，可以彈出一個對話框讓用戶更改名稱
            }
        });

        return view;
    }
}

