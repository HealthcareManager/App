package com.luce.healthmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.luce.healthmanager.data.api.ApiService;
import com.luce.healthmanager.data.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HealthFragment extends Fragment {

    // 定義 TextView 用於顯示數據
    private TextView heartRateText, oxygenText, bloodPressureText, bloodSugarText, heightData;
    private ApiService apiService;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health, container, false);

        // 初始化 TextView 來顯示數據
        heartRateText = view.findViewById(R.id.heart_data);
        oxygenText = view.findViewById(R.id.oxygen_data);
        bloodPressureText = view.findViewById(R.id.blood_data);
        bloodSugarText = view.findViewById(R.id.sugar_data);
        heightData = view.findViewById(R.id.height_data);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");

        apiService = ApiClient.getClient(getActivity()).create(ApiService.class);

        // 請求數據並更新 UI
        fetchHealthData();

        // 調用方法獲取卡路里數據
        fetchCaloriesData();


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

 
        // 設置卡路里卡片點擊事件
        LinearLayout caloriesCard = view.findViewById(R.id.calories_card);
        caloriesCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CardDetailActivity.class);
            intent.putExtra("CARD_TYPE", "卡路里");
            startActivity(intent);
        });


        return view;
    }

    // 從後端獲取健康數據
    private void fetchHealthData() {

        Call<UserMetricsResponse> call = apiService.getUserMetrics(userId); // 傳遞用戶ID

        Call<List<HeightWeightRecord>> getCall = apiService.getHeightWeightRecords(userId);

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

        getCall.enqueue(new Callback<List<HeightWeightRecord>>() {
            @Override
            public void onResponse(Call<List<HeightWeightRecord>> call, Response<List<HeightWeightRecord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HeightWeightRecord> records = response.body();

                    if (!records.isEmpty()) {
                        // 取得最新的體重記錄 (假設最新的記錄在列表的最後一個)
                        HeightWeightRecord latestRecord = records.get(records.size() - 1);
                        
                        String latestWeight = String.valueOf(latestRecord.getWeight());

                        heightData.setText(latestWeight + "公斤");
                        Log.d("LatestWeight", "最新體重: " + latestWeight);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<HeightWeightRecord>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    private void fetchCaloriesData() {
        Call<List<ExerciseRecord>> call = apiService.getExerciseRecords(userId);
    
        call.enqueue(new Callback<List<ExerciseRecord>>() {
            @Override
            public void onResponse(Call<List<ExerciseRecord>> call, Response<List<ExerciseRecord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ExerciseRecord> records = response.body();


                    if (!records.isEmpty()) {
                        // 取最新一筆紀錄顯示在卡片上
                        ExerciseRecord latestRecord = records.get(0);
                        TextView caloriesDataText = getView().findViewById(R.id.calories_data);
                        caloriesDataText.setText(latestRecord.getCaloriesBurned() + " kcal");
                    }
                } else {
                    Log.d("HealthFragment", "無法獲取卡路里數據：" + response.code());
                }
            }
    
            @Override
            public void onFailure(Call<List<ExerciseRecord>> call, Throwable t) {
                Log.e("HealthFragment", "獲取卡路里數據失敗", t);
            }
        });
    }
}
