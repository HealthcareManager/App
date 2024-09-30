package com.luce.healthmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.luce.healthmanager.data.api.ApiService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CardDetailActivity extends AppCompatActivity {

    // UI 控件變量
    private ImageButton backButton;  // 返回按鈕
    private Button addButton;        // 新增數據按鈕
    private RecyclerView recyclerView; // 用來顯示數據列表的 RecyclerView
    private DataAdapter dataAdapter;   // RecyclerView 的適配器
    private List<DataItem> dataList;   // 用來保存數據項的列表
    private String selectedDate = "";  // 保存選擇的日期
    private LineChart lineChart;       // 用來顯示數據走勢的折線圖

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        // 初始化折線圖變量
        lineChart = findViewById(R.id.line_chart);
        // 初始化數據列表
        dataList = new ArrayList<>();
        // 設置 LineChart，初始化圖表的外觀
        setupLineChart();

        // 接收從 Intent 傳遞的卡片類型，根據類型顯示不同的數據
        String cardType = getIntent().getStringExtra("CARD_TYPE");
        TextView cardTitle = findViewById(R.id.card_detail_title);

        switch (cardType) {
            case "心律":
                cardTitle.setText("心律 詳情");
                fetchHeartRateData(); // 從後端獲取心律數據並顯示
                break;
            // 可以擴展其他類型
            case "血氧":
                cardTitle.setText("血氧 詳情");
                break;
            case "血壓":
                cardTitle.setText("血壓 詳情");
                break;
            case "血糖":
                cardTitle.setText("血糖 詳情");
                break;
            case "卡路里":
                cardTitle.setText("卡路里 詳情");
                break;
            case "身高體重":
                cardTitle.setText("身高體重 詳情");
                break;
            case "抽菸":
                cardTitle.setText("抽菸 詳情");
                break;
            case "喝酒":
                cardTitle.setText("喝酒 詳情");
                break;
            case "檳榔":
                cardTitle.setText("檳榔 詳情");
                break;
            default:
                cardTitle.setText("未知類型");
                break;

        }

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
        addButton.setOnClickListener(v -> showAddDataDialog());
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

    // 從後端獲取心律數據
    private void fetchHeartRateData() {
        // 設置 Retrofit 用於 HTTP 請求
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // 模擬器中訪問主機的地址
                .addConverterFactory(GsonConverterFactory.create())  // 使用 Gson 解析 JSON
                .build();

        // 創建 API 服務
        ApiService apiService = retrofit.create(ApiService.class);
        Call<List<HeartRateData>> call = apiService.getHeartRateData();

        // 執行 API 請求
        call.enqueue(new Callback<List<HeartRateData>>() {
            @Override
            public void onResponse(Call<List<HeartRateData>> call, Response<List<HeartRateData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 獲取返回的心律數據
                    List<HeartRateData> heartRateDataList = response.body();
                    List<Entry> entries = new ArrayList<>();

                    // 將數據轉換為折線圖所需的格式
                    for (int i = 0; i < heartRateDataList.size(); i++) {
                        HeartRateData data = heartRateDataList.get(i);
                        entries.add(new Entry(i, data.getHeartRate()));
                        // 添加數據到列表中以顯示在 RecyclerView
                        dataList.add(new DataItem(data.getTimestamp(), String.valueOf(data.getHeartRate())));
                    }

                    // 設置折線圖數據
                    LineDataSet dataSet = new LineDataSet(entries, "心律數據");
                    dataSet.setLineWidth(2.5f);
                    dataSet.setColor(Color.BLUE);
                    dataSet.setCircleColor(Color.RED);
                    dataSet.setCircleRadius(4f);
                    dataSet.setDrawValues(false);  // 不在點上顯示數值

                    lineChart.setData(new LineData(dataSet));
                    lineChart.invalidate(); // 刷新圖表顯示
                    dataAdapter.notifyDataSetChanged(); // 更新列表顯示
                }
            }

            @Override
            public void onFailure(Call<List<HeartRateData>> call, Throwable t) {
                t.printStackTrace();
                // 顯示錯誤信息
                Toast.makeText(CardDetailActivity.this, "無法獲取心律數據", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 顯示新增數據的對話框
    private void showAddDataDialog() {
        // 創建對話框的視圖
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_data, null);
        builder.setView(dialogView);

        // 初始化對話框中的控件
        Button btnSelectDate = dialogView.findViewById(R.id.btn_select_date);
        TextView tvSelectedDate = dialogView.findViewById(R.id.tv_selected_date);
        EditText editData = dialogView.findViewById(R.id.edit_data);

        // 設置日期選擇按鈕的點擊事件
        btnSelectDate.setOnClickListener(v -> showDatePickerDialog(tvSelectedDate));

        // 設置對話框按鈕
        builder.setPositiveButton("新增", (dialog, id) -> {
            String dataInput = editData.getText().toString();
            // 檢查輸入是否為空
            if (TextUtils.isEmpty(selectedDate) || TextUtils.isEmpty(dataInput)) {
                Toast.makeText(this, "請選擇日期並輸入數據", Toast.LENGTH_SHORT).show();
                return;
            }
            // 新增數據到數據列表中
            addNewData(selectedDate, dataInput);
        });

        builder.setNegativeButton("取消", (dialog, id) -> dialog.dismiss());
        // 顯示對話框
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 顯示日期選擇器
    private void showDatePickerDialog(TextView tvSelectedDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedMonth += 1; // 月份是從 0 開始的，所以需要加 1
                    selectedDate = selectedYear + "/" + String.format(Locale.getDefault(), "%02d", selectedMonth) + "/" + String.format(Locale.getDefault(), "%02d", selectedDay);
                    tvSelectedDate.setText(selectedDate);  // 更新日期顯示
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // 新增數據到數據列表中
    private void addNewData(String date, String dataValue) {
        dataList.add(new DataItem(date, dataValue)); // 添加新的數據項
        dataAdapter.notifyDataSetChanged(); // 通知適配器數據已改變
        recyclerView.scrollToPosition(dataList.size() - 1); // 滾動到最新添加的數據項
    }
}
