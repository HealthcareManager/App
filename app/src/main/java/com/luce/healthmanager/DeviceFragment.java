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

        // 同步按鈕
        Button syncButton = view.findViewById(R.id.sync_button);
        syncButton.setOnClickListener(v -> {
            // 處理同步邏輯
        });

        return view;
    }
}
