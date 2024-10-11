package com.luce.healthmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.luce.healthmanager.data.api.ApiService;
import com.luce.healthmanager.data.network.ApiClient;

import java.util.Collections;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeightDetailActivity extends AppCompatActivity {

    private RecyclerView recentWeightRecyclerView;
    private WeightAdapter weightAdapter;
    private List<HeightWeightRecord> weightDataList;
    private ImageButton backButton;
    private ApiService apiService;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_detail);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");

        // 初始化RecyclerView
        recentWeightRecyclerView = findViewById(R.id.recentWeightRecyclerView);
        recentWeightRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 載入資料
        loadWeightData();

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());
    }

    private void loadWeightData() {

        // 呼叫API取得身高體重資料
        Call<List<HeightWeightRecord>> getCall = apiService.getHeightWeightRecords(userId);

        getCall.enqueue(new Callback<List<HeightWeightRecord>>() {
            @Override
            public void onResponse(Call<List<HeightWeightRecord>> call, Response<List<HeightWeightRecord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weightDataList = response.body();
                    // 反轉列表以顯示最新的資料在最上面
                    Collections.reverse(weightDataList);
                    // 設置Adapter顯示資料
                    weightAdapter = new WeightAdapter(weightDataList);
                    recentWeightRecyclerView.setAdapter(weightAdapter);
                } else {
                    Toast.makeText(WeightDetailActivity.this, "無法取得資料", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<HeightWeightRecord>> call, Throwable t) {
                Log.e("API Error", t.getMessage());
                Toast.makeText(WeightDetailActivity.this, "資料加載失敗", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adapter類別，顯示體重資料
    public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.WeightViewHolder> {

        private List<HeightWeightRecord> weightDataList;

        public WeightAdapter(List<HeightWeightRecord> weightDataList) {
            this.weightDataList = weightDataList;
        }

        @Override
        public WeightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_weight, parent, false);
            return new WeightViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WeightViewHolder holder, int position) {
            HeightWeightRecord weightData = weightDataList.get(position);
            holder.bind(weightData);
        }

        @Override
        public int getItemCount() {
            return weightDataList.size();
        }

        public class WeightViewHolder extends RecyclerView.ViewHolder {

            private TextView weightTextView;
            private TextView dateTextView;

            public WeightViewHolder(View itemView) {
                super(itemView);
                weightTextView = itemView.findViewById(R.id.weightTextView);
                dateTextView = itemView.findViewById(R.id.dateTextView);
            }

            public void bind(HeightWeightRecord weightData) {
                dateTextView.setText(weightData.getDate());
                weightTextView.setText(String.valueOf(weightData.getWeight()) + " 公斤");
            }
        }
    }
}

