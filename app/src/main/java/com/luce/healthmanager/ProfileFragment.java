package com.luce.healthmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 將 fragment_profile.xml 加載到這個 Fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
