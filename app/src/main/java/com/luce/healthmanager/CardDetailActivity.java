package com.luce.healthmanager;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
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
    private RecyclerView recyclerView; // 用來顯示數據列表的 RecyclerView
    private DataAdapter dataAdapter;   // RecyclerView 的適配器
    private List<DataItem> dataList;   // 用來保存數據項的列表
    private LineChart lineChart;       // 用來顯示數據走勢的折線圖
    private ApiService apiService;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        // 初始化折線圖變量
        lineChart = findViewById(R.id.line_chart);
        // 初始化數據列表
        dataList = new ArrayList<>();
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

        // 初始化 RecyclerView，用於顯示數據項
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataAdapter = new DataAdapter(dataList);
        recyclerView.setAdapter(dataAdapter);

        // 初始化新增按鈕，並設置點擊事件以顯示對話框
        addButton = findViewById(R.id.add_button);
        addButton.setVisibility(View.GONE);
    }


    // 初始化折線圖
    private void setupLineChart() {
        lineChart.setDrawGridBackground(false);  // 不繪製背景格子
        lineChart.getDescription().setEnabled(false);  // 不顯示描述文字

        // 設置 X 軸屬性
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  // X 軸顯示在底部
        xAxis.setDrawGridLines(false);  // 不繪製網格線
        xAxis.setGranularity(1f);  // X 軸標籤的最小間隔
        xAxis.setAvoidFirstLastClipping(true);  // 防止第一和最後的標籤被裁切

        // 設置 Y 軸屬性
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);  // 不繪製網格線
        lineChart.getAxisRight().setEnabled(false);  // 不顯示右側的 Y 軸
    }

    // 通用方法，用於獲取數據並將其展示到相應卡片中
    private void fetchHealthData(String cardType) {
        Call<UserMetricsResponse> call = apiService.getUserMetrics(String.valueOf(1));

        call.enqueue(new Callback<UserMetricsResponse>() {
            @Override
            public void onResponse(Call<UserMetricsResponse> call, Response<UserMetricsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Entry> entries = new ArrayList<>();
                    List<DataItem> dataList = new ArrayList<>();

                    // 根据 cardType 类型，解析数据并生成折线图数据
                    for (UserMetricsResponse.Metric metric : response.body().getMetrics()) {
                        switch (cardType) {
                            case "心律":
                                // 将心律数据添加到 entries 列表并显示在 RecyclerView 中
                                entries.add(new Entry(entries.size(), metric.getHeartRate()));
                                dataList.add(new DataItem(metric.getTimestamp(), String.valueOf(metric.getHeartRate())));
                                break;
                            case "血壓":
                                // 分割血压字符串（如 "120/80"），提取收缩压并添加到图表中
                                String[] bp = metric.getBloodPressure().split("/");
                                if (bp.length == 2) {  // 确保血压数据格式正确
                                    entries.add(new Entry(entries.size(), Float.parseFloat(bp[0])));  // 使用收缩压
                                }
                                dataList.add(new DataItem(metric.getTimestamp(), metric.getBloodPressure()));  // 显示完整的血压值
                                break;
                            case "血氧":
                                // 将血氧数据添加到 entries 列表并显示在 RecyclerView 中
                                entries.add(new Entry(entries.size(), (float) metric.getBloodOxygen().floatValue()));
                                dataList.add(new DataItem(metric.getTimestamp(), String.valueOf(metric.getBloodOxygen())));
                                break;
                            case "血糖":
                                // 将血糖数据添加到 entries 列表并显示在 RecyclerView 中
                                entries.add(new Entry(entries.size(), (float) metric.getBloodSugar().floatValue()));
                                dataList.add(new DataItem(metric.getTimestamp(), String.valueOf(metric.getBloodSugar())));
                                break;
                            // 可以根据需要添加其他类型的健康数据处理逻辑
                            case "身高體重":
                                addButton.setVisibility(View.VISIBLE);
                                addButton.setOnClickListener(v -> showAddDataDialog());
                                break;
                            default:
                                break;
                        }
                    }

                    // 更新折线图
                    updateLineChart(entries, cardType);
                }
            }

            @Override
            public void onFailure(Call<UserMetricsResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(CardDetailActivity.this, "获取数据失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateLineChart(List<Entry> entries, String cardType) {
        LineDataSet dataSet = new LineDataSet(entries, cardType + " 数据");
        dataSet.setLineWidth(2.5f);
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.RED);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        lineChart.setData(new LineData(dataSet));
        lineChart.invalidate(); // 刷新圖表顯示
    }

    // 顯示新增數據的對話框
    private void showAddDataDialog() {
        // 創建對話框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 動態創建一個垂直佈局
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(50, 40, 50, 10);  // 設置內邊距（可調整）

        // 身高選擇器
        TextView heightLabel = new TextView(this);
        heightLabel.setText("身高 (cm):");
        heightLabel.setPadding(0, 20, 0, 10);
        dialogLayout.addView(heightLabel);

        // 創建身高的整數選擇器
        NumberPicker heightWholePicker = new NumberPicker(this);
        heightWholePicker.setMinValue(100);  // 假設身高最小值為100cm
        heightWholePicker.setMaxValue(250);  // 假設身高最大值為250cm
        heightWholePicker.setWrapSelectorWheel(false);
        dialogLayout.addView(heightWholePicker);

        // 小數部分選擇器
        NumberPicker heightDecimalPicker = new NumberPicker(this);
        heightDecimalPicker.setMinValue(0);  // 小數部分最小值為0
        heightDecimalPicker.setMaxValue(9);  // 小數部分最大值為9
        heightDecimalPicker.setWrapSelectorWheel(true);  // 允許循環
        dialogLayout.addView(heightDecimalPicker);

        // 體重選擇器
        TextView weightLabel = new TextView(this);
        weightLabel.setText("體重 (kg):");
        weightLabel.setPadding(0, 20, 0, 10);
        dialogLayout.addView(weightLabel);

        // 創建體重的整數選擇器
        NumberPicker weightWholePicker = new NumberPicker(this);
        weightWholePicker.setMinValue(30);  // 假設體重最小值為30kg
        weightWholePicker.setMaxValue(200);  // 假設體重最大值為200kg
        weightWholePicker.setWrapSelectorWheel(false);
        dialogLayout.addView(weightWholePicker);

        // 體重小數部分選擇器
        NumberPicker weightDecimalPicker = new NumberPicker(this);
        weightDecimalPicker.setMinValue(0);  // 小數部分最小值為0
        weightDecimalPicker.setMaxValue(9);  // 小數部分最大值為9
        weightDecimalPicker.setWrapSelectorWheel(true);  // 允許循環
        dialogLayout.addView(weightDecimalPicker);

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
            dataToServer(finalHeight, finalWeight);
        });

        builder.setNegativeButton("取消", (dialog, id) -> dialog.dismiss());

        // 顯示對話框
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void dataToServer(double finalHeight, double finalWeight) {

//        Call<ResponseBody> call = apiService.updateHeightWeightRecord();
    }

    // 新增數據到數據列表中
    private void addNewData(String date, String dataValue) {
        dataList.add(new DataItem(date, dataValue)); // 添加新的數據項
        dataAdapter.notifyDataSetChanged(); // 通知適配器數據已改變
        recyclerView.scrollToPosition(dataList.size() - 1); // 滾動到最新添加的數據項
    }
}
