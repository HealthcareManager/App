package com.luce.healthmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class DeviceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);

        // 設置裝置名稱和電量
        TextView deviceName = view.findViewById(R.id.device_name);
        TextView batteryLevel = view.findViewById(R.id.battery_level);

        // 設置假數據或從裝置獲取真實數據
        deviceName.setText("裝置名稱: 小米手環 6");
        batteryLevel.setText("電量: 60%");

        // 同步按鈕
        Button syncButton = view.findViewById(R.id.sync_button);
        syncButton.setOnClickListener(v -> {
            // 處理同步邏輯
        });

        // 處理其他設置選項的點擊事件
        TextView heartRateSetting = view.findViewById(R.id.heart_rate_setting);
        heartRateSetting.setOnClickListener(v -> {
            // 處理心率設置點擊事件
        });

        TextView sleepSetting = view.findViewById(R.id.sleep_setting);
        sleepSetting.setOnClickListener(v -> {
            // 處理睡眠設置點擊事件
        });

        TextView standReminder = view.findViewById(R.id.stand_reminder);
        standReminder.setOnClickListener(v -> {
            // 處理站立提醒點擊事件
        });

        return view;
    }
}
