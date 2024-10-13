package com.luce.healthmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.luce.healthmanager.data.api.ApiService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.luce.healthmanager.data.network.ApiClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardDetailActivity extends AppCompatActivity {

    // UI 控件變量
    private ImageButton backButton;  // 返回按鈕
    private Button addButton;        // 新增數據按鈕
    private LineChart lineChart;       // 用來顯示數據走勢的折線圖
    private ApiService apiService;
    private String userId, gender;
    private LinearLayout weightCard, weightCard1;
    private TextView suggestedWeightText;
    private ImageView weightArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        gender = sharedPreferences.getString("gender", "");


        // 初始化折線圖變量
        lineChart = findViewById(R.id.line_chart);
        // 設置 LineChart，初始化圖表的外觀
        setupLineChart();

        // 接收從 Intent 傳遞的卡片類型，根據類型顯示不同的數據
        String cardType = getIntent().getStringExtra("CARD_TYPE");
        TextView cardTitle = findViewById(R.id.card_detail_title);

        cardTitle.setText(cardType + " 詳情");
        fetchHealthData(cardType); // 根據卡片類型獲取相應的數據

        // 初始化返回按鈕，並設置點擊事件
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish()); // 返回上一個 Activity

        // 初始化新增按鈕，並設置點擊事件以顯示對話框
        addButton = findViewById(R.id.add_button);
        addButton.setVisibility(View.GONE);

        weightCard = findViewById(R.id.weight_card);
        weightCard1 = findViewById(R.id.weight_card1);
        weightCard.setVisibility(View.GONE);
        weightCard1.setVisibility(View.GONE);

        weightArrow = findViewById(R.id.weight_arrow);

        suggestedWeightText = findViewById(R.id.suggested_weight_text);
    }


    // 初始化折線圖
    private void setupLineChart() {
        lineChart.setDrawGridBackground(false); // 不繪製背景格子
        lineChart.getDescription().setEnabled(false); // 不顯示描述文字

        // 設置 X 軸屬性
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X 軸顯示在底部
        xAxis.setDrawGridLines(true); // 繪製 X 軸網格線
        xAxis.setGranularity(1f); // X 軸標籤的最小間隔
        xAxis.setLabelCount(10, true); // 設置標籤數量為 10 個，並自動調整
        xAxis.setAvoidFirstLastClipping(true); // 防止第一和最後的標籤被裁切
        xAxis.setLabelRotationAngle(-45f); // X 軸標籤旋轉角度（選擇性，防止擁擠）


        // 設置 Y 軸屬性
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true); // 繪製 Y 軸網格線
        leftAxis.setAxisMinimum(50f);
        leftAxis.setAxisMaximum(130f);
        lineChart.getAxisRight().setEnabled(false); // 不顯示右側的 Y 軸
    }

    // 通用方法，用於獲取數據並將其展示到相應卡片中
    private void fetchHealthData(String cardType) {
        Call<UserMetricsResponse> call = apiService.getUserMetrics(String.valueOf(1));
        Call<List<HeightWeightRecord>> getCall = apiService.getHeightWeightRecords(userId);

        call.enqueue(new Callback<UserMetricsResponse>() {
            @Override
            public void onResponse(Call<UserMetricsResponse> call, Response<UserMetricsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Entry> entries = new ArrayList<>();
                    List<String> dates = new ArrayList<>();

                    List<UserMetricsResponse.Metric> metrics = response.body().getMetrics();

                    // 如果資料超過 10 筆，取最後的 10 筆資料
                    if (metrics.size() > 10) {
                        metrics = metrics.subList(metrics.size() - 10, metrics.size());
                    }

                    // 按照日期順序進行排序
                    metrics.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));

                    // 據 cardType 類型，解析數據
                    for (int i = 0; i < metrics.size(); i++) {
                        UserMetricsResponse.Metric metric = metrics.get(i);
                        String date = metric.getTimestamp(); // 假設 timestamp 是 MM/dd 格式的日期
                        dates.add(date); // 將日期加入日期列表

                        switch (cardType) {
                            case "心律":
                                entries.add(new Entry(i, metric.getHeartRate())); // 使用排序後的索引 i
                                break;
                            case "血壓":
                                String[] bp = metric.getBloodPressure().split("/");
                                if (bp.length == 2) {
                                    entries.add(new Entry(i, Float.parseFloat(bp[0]))); // 使用排序後的索引 i
                                }
                                break;
                            case "血氧":
                                entries.add(new Entry(i, metric.getBloodOxygen().floatValue())); // 使用排序後的索引 i
                                break;
                            case "血糖":
                                entries.add(new Entry(i, metric.getBloodSugar().floatValue())); // 使用排序後的索引 i
                                break;
                            case "體重":
                                addButton.setVisibility(View.VISIBLE);
                                addButton.setOnClickListener(v -> showAddDataDialog());
                                weightCard.setVisibility(View.VISIBLE);
                                weightCard1.setVisibility(View.VISIBLE);
                                weightArrow.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(CardDetailActivity.this, WeightDetailActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case "卡路里":
                                // 重新顯示最新的運動卡路里資料
                                fetchCaloriesData(); 
                                break;
                            default:
                                break;
                        }
                    }

                    // 特殊處理體重的情況
                    if (cardType.equals("體重")) {
                        getCall.enqueue(new Callback<List<HeightWeightRecord>>() {
                            @Override
                            public void onResponse(Call<List<HeightWeightRecord>> call, Response<List<HeightWeightRecord>> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    List<HeightWeightRecord> records = response.body();

                                    // 清除上次的數據，避免重複
                                    dates.clear();
                                    entries.clear();

                                    if (!records.isEmpty()) {
                                        HeightWeightRecord latestRecord = records.get(0);
                                        double height = latestRecord.getHeight(); // 假設身高的單位是公尺 (米)

                                        double SuggestedWeight = 0;
                                        // 計算建議體重範圍
                                        if (gender.equals("女")) {
                                            SuggestedWeight = (height - 70) * 0.6;
                                        } else {
                                            SuggestedWeight = (height - 80) * 0.7;
                                        }

                                        // 顯示建議體重範圍
                                        suggestedWeightText.setText(SuggestedWeight + "公斤");
                                    }

                                    int limit = Math.min(records.size(), 10);

                                    // 解析伺服器返回的體重資料
                                    for (int i = 0; i < limit; i++) {
                                        HeightWeightRecord record = records.get(i);
                                        String date = record.getDate();  // 使用伺服器返回的日期
                                        dates.add(date);  // 將日期加入日期列表
                                        entries.add(new Entry(i, (float) record.getWeight()));  // 轉換為 float 類型
                                    }

                                    // 更新折線圖
                                    updateLineChart(entries, "體重", dates);
                                } else {
                                    Log.d("Card", "獲取體重數據失敗，回應碼：" + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<List<HeightWeightRecord>> call, Throwable t) {
                                Log.e("Card", "請求失敗", t);
                                Toast.makeText(CardDetailActivity.this, "獲取體重數據失敗", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        // 更新其他類型的數據到折線圖
                        updateLineChart(entries, cardType, dates);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserMetricsResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(CardDetailActivity.this, "獲取數據失敗", Toast.LENGTH_SHORT).show();
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
                    List<Entry> entries = new ArrayList<>();
                    List<String> dates = new ArrayList<>();
    
                    // 只取最近的 10 筆資料
                    int limit = Math.min(records.size(), 10);
                    for (int i = 0; i < limit; i++) {
                        ExerciseRecord record = records.get(i);
                        String date = record.getDate(); // 假設日期格式為 MM/dd
                        dates.add(date);
                        entries.add(new Entry(i, (float) record.getCaloriesBurned()));
                    }
    
                    // 更新折線圖
                    updateLineChart(entries, "卡路里", dates);
                } else {
                    Toast.makeText(CardDetailActivity.this, "無法獲取卡路里數據", Toast.LENGTH_SHORT).show();
                }
            }
    
            @Override
            public void onFailure(Call<List<ExerciseRecord>> call, Throwable t) {
                Toast.makeText(CardDetailActivity.this, "卡路里數據請求失敗：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateLineChart(List<Entry> entries, String cardType, List<String> dates) {
        LineDataSet dataSet = new LineDataSet(entries, cardType);
        dataSet.setLineWidth(2f); // 設置折線寬度
        dataSet.setCircleRadius(4f); // 設置數據點圓圈半徑
        dataSet.setValueTextSize(10f); // 設置數據點文字大小

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // 設置 X 軸的日期格式化
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));

        xAxis.setLabelCount(dates.size(), true); // 設置標籤數量為日期列表的大小
        xAxis.setGranularity(1f); // 確保每個資料點都顯示

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(50f); // 設置 Y 軸的最小值，例如 50
        leftAxis.setAxisMaximum(130f); // 設置 Y 軸的最大值，例如 200
        lineChart.getAxisRight().setEnabled(false); // 不顯示右側的 Y 軸

        // 調整圖例位置
        Legend legend = lineChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM); // 圖例垂直對齊底部
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER); // 圖例水平居中
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL); // 水平排列
        legend.setDrawInside(false); // 在圖表外部繪製
        legend.setYOffset(10f); // 向下移動圖例

        lineChart.setExtraOffsets(0f, 0f, 0f, 20f); // 調整下方邊距，留出空間給圖例

        // 更新圖表
        lineChart.invalidate(); // 刷新圖表
    }


    // 顯示新增數據的對話框
    private void showAddDataDialog() {
        // 創建對話框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 動態創建一個垂直佈局
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(50, 40, 50, 10);  // 設置內邊距（可調整）

        // 身高選擇器標籤
        TextView heightLabel = new TextView(this);
        heightLabel.setText("身高 (cm):");
        heightLabel.setPadding(0, 20, 0, 10);
        dialogLayout.addView(heightLabel);

        // 創建水平佈局來包含整數和小數部分的身高選擇器
        LinearLayout heightLayout = new LinearLayout(this);
        heightLayout.setOrientation(LinearLayout.HORIZONTAL);

        // 創建身高的整數選擇器
        NumberPicker heightWholePicker = new NumberPicker(this);
        heightWholePicker.setMinValue(100);  // 假設身高最小值為100cm
        heightWholePicker.setMaxValue(250);  // 假設身高最大值為250cm
        heightWholePicker.setValue(150);
        heightWholePicker.setWrapSelectorWheel(false);
        heightLayout.addView(heightWholePicker);

        // 小數部分選擇器
        NumberPicker heightDecimalPicker = new NumberPicker(this);
        heightDecimalPicker.setMinValue(0);  // 小數部分最小值為0
        heightDecimalPicker.setMaxValue(9);  // 小數部分最大值為9
        heightDecimalPicker.setWrapSelectorWheel(true);  // 允許循環
        heightLayout.addView(heightDecimalPicker);

        dialogLayout.addView(heightLayout);

        // 體重選擇器標籤
        TextView weightLabel = new TextView(this);
        weightLabel.setText("體重 (kg):");
        weightLabel.setPadding(0, 20, 0, 10);
        dialogLayout.addView(weightLabel);

        // 創建水平佈局來包含整數和小數部分的體重選擇器
        LinearLayout weightLayout = new LinearLayout(this);
        weightLayout.setOrientation(LinearLayout.HORIZONTAL);

        // 創建體重的整數選擇器
        NumberPicker weightWholePicker = new NumberPicker(this);
        weightWholePicker.setMinValue(30);  // 假設體重最小值為30kg
        weightWholePicker.setMaxValue(200);  // 假設體重最大值為200kg
        weightWholePicker.setValue(60);
        weightWholePicker.setWrapSelectorWheel(false);
        weightLayout.addView(weightWholePicker);

        // 體重小數部分選擇器
        NumberPicker weightDecimalPicker = new NumberPicker(this);
        weightDecimalPicker.setMinValue(0);  // 小數部分最小值為0
        weightDecimalPicker.setMaxValue(9);  // 小數部分最大值為9
        weightDecimalPicker.setWrapSelectorWheel(true);  // 允許循環
        weightLayout.addView(weightDecimalPicker);

        dialogLayout.addView(weightLayout);

        // 設置對話框的內容佈局
        builder.setView(dialogLayout);

        // 設置對話框按鈕
        builder.setPositiveButton("新增", (dialog, id) -> {
            // 獲取身高和體重的整數及小數部分
            int selectedHeightWhole = heightWholePicker.getValue();
            int selectedHeightDecimal = heightDecimalPicker.getValue();
            int selectedWeightWhole = weightWholePicker.getValue();
            int selectedWeightDecimal = weightDecimalPicker.getValue();

            // 將整數和小數組合起來
            double finalHeight = selectedHeightWhole + (selectedHeightDecimal * 0.1);
            double finalWeight = selectedWeightWhole + (selectedWeightDecimal * 0.1);

            Map<String, String> userData = new HashMap<>();
            userData.put("height", String.valueOf(finalHeight));
            userData.put("weight", String.valueOf(finalWeight));

            // 在這裡處理添加邏輯，比如更新到數據列表或提交到後端
            dataToServer(userData, "體重");
        });

        builder.setNegativeButton("取消", (dialog, id) -> dialog.dismiss());

        // 顯示對話框
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void dataToServer(Map<String, String> userData, String cardType) {

        userData.put("userId", userId);

        Call<ResponseBody> updateCall = apiService.updateHeightWeightRecord(userData);

        updateCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(CardDetailActivity.this, "身高體重已添加", Toast.LENGTH_SHORT);
                    // 重新獲取數據
                    fetchHealthData(cardType);
                } else {
                    Toast.makeText(CardDetailActivity.this, "身高體重添加失敗", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CardDetailActivity.this, "更新請求失敗: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
