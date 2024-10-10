package com.luce.healthmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.luce.healthmanager.data.api.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HealthFragment extends Fragment {

    // 定義 TextView 用於顯示數據
    private TextView heartRateText, oxygenText, bloodPressureText, bloodSugarText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health, container, false);

        // 初始化 TextView 來顯示數據
        heartRateText = view.findViewById(R.id.heart_data);
        oxygenText = view.findViewById(R.id.oxygen_data);
        bloodPressureText = view.findViewById(R.id.blood_data);
        bloodSugarText = view.findViewById(R.id.sugar_data);

        // 請求數據並更新 UI
        fetchHealthData();

        // 設置心律卡片的點擊事件
        LinearLayout heartCard = view.findViewById(R.id.heart_card);
        heartCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CardDetailActivity.class);
            intent.putExtra("CARD_TYPE", "心律");
            startActivity(intent);
        });

        // 設置血氧卡片的點擊事件
        LinearLayout oxygenCard = view.findViewById(R.id.oxygen_card);
        oxygenCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CardDetailActivity.class);
            intent.putExtra("CARD_TYPE", "血氧");
            startActivity(intent);
        });

        // 設置血壓卡片的點擊事件
        LinearLayout bloodCard = view.findViewById(R.id.blood_card);
        bloodCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CardDetailActivity.class);
            intent.putExtra("CARD_TYPE", "血壓");
            startActivity(intent);
        });

        // 設置血糖卡片的點擊事件
        LinearLayout sugarCard = view.findViewById(R.id.sugar_card);
        sugarCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CardDetailActivity.class);
            intent.putExtra("CARD_TYPE", "血糖");
            startActivity(intent);
        });

        // 設置體重卡片的點擊事件
        LinearLayout weightCard = view.findViewById(R.id.weight_card);
        weightCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CardDetailActivity.class);
            intent.putExtra("CARD_TYPE", "體重");
            Log.d("Yuchen", "123");
            startActivity(intent);
        });

        return view;
    }

    // 從後端獲取健康數據
    private void fetchHealthData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // 模擬器中訪問主機的地址
                .addConverterFactory(GsonConverterFactory.create())  // 使用 Gson 解析 JSON
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<UserMetricsResponse> call = apiService.getUserMetrics("1"); // 傳遞用戶ID，這裡假設為1

        call.enqueue(new Callback<UserMetricsResponse>() {
            @Override
            public void onResponse(Call<UserMetricsResponse> call, Response<UserMetricsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserMetricsResponse userMetrics = response.body();

                    // 假設我們只顯示最新的一筆數據
                    if (!userMetrics.getMetrics().isEmpty()) {
                        UserMetricsResponse.Metric latestMetric = userMetrics.getMetrics().get(0);

                        // 更新心律、血氧、血壓、血糖的 TextView
                        heartRateText.setText(String.valueOf(latestMetric.getHeartRate()));
                        oxygenText.setText(String.valueOf(latestMetric.getBloodOxygen()));
                        bloodPressureText.setText(latestMetric.getBloodPressure());
                        bloodSugarText.setText(String.valueOf(latestMetric.getBloodSugar()));
                    }
                }
            }

            @Override
            public void onFailure(Call<UserMetricsResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
