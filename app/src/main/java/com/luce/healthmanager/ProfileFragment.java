package com.luce.healthmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        // 將 fragment_profile.xml 加載到這個 Fragment
//        return inflater.inflate(R.layout.fragment_profile, container, false);
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false); // 正確設置 Fragment 佈局

        // 綁定 LINE 登入按鈕
        Button lineLoginButton = view.findViewById(R.id.btnLineLogin); // 使用 view 來查找按鈕
        lineLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLineLogin(v);
            }
        });

        return view; // 返回視圖
    }

    // 當用戶點擊 LINE 登錄按鈕時觸發
    public void startLineLogin(View view) {
        Uri uri = Uri.parse("https://access.line.me/oauth2/v2.1/authorize"
                + "?response_type=code"
                + "&client_id=2006371057"
                + "&redirect_uri=com.luce.healthmanager://callback"
                + "&state=500"
                + "&scope=profile%20openid%20email");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
