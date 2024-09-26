package com.luce.healthmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CardDetailActivity extends AppCompatActivity {

    private ImageButton backButton;
    private Button addButton;
    private RecyclerView recyclerView;
    private DataAdapter dataAdapter;
    private List<DataItem> dataList;
    private String selectedDate = "";  // 保存選擇的日期

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        // 接收從 Intent 傳遞的卡片類型
        String cardType = getIntent().getStringExtra("CARD_TYPE");

        // 設置卡片標題
        TextView cardTitle = findViewById(R.id.card_detail_title);
        cardTitle.setText(cardType + " 詳情");

        // 初始化返回按鈕
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 返回到上一個 Activity
            }
        });

        // 初始化數據列表
        dataList = new ArrayList<>();

        // 初始化 RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataAdapter = new DataAdapter(dataList);
        recyclerView.setAdapter(dataAdapter);

        // 加載初始數據
        loadInitialData();

        // 新增按鈕
        addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDataDialog(); // 顯示輸入對話框
            }
        });
    }

    // 顯示輸入數據的對話框
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
        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(tvSelectedDate);
            }
        });

        // 設置對話框按鈕
        builder.setPositiveButton("新增", (dialog, id) -> {
            String dataInput = editData.getText().toString();

            // 檢查輸入是否為空
            if (TextUtils.isEmpty(selectedDate) || TextUtils.isEmpty(dataInput)) {
                Toast.makeText(this, "請選擇日期並輸入數據", Toast.LENGTH_SHORT).show();
                return;
            }

            // 將數據添加到列表中
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
                    // 更新選擇的日期
                    selectedMonth += 1; // 月份是從 0 開始的，所以需要加 1
                    selectedDate = selectedYear + "/" + String.format(Locale.getDefault(), "%02d", selectedMonth) + "/" + String.format(Locale.getDefault(), "%02d", selectedDay);
                    tvSelectedDate.setText(selectedDate);  // 更新日期顯示
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // 加載初始數據
    private void loadInitialData() {
        for (int i = 0; i < 10; i++) {
            String dateTime = getCurrentDateTime();
            dataList.add(new DataItem(dateTime, "數據 " + (i + 1)));
        }
        dataAdapter.notifyDataSetChanged();
    }

    // 新增數據
    private void addNewData(String date, String dataValue) {
        dataList.add(new DataItem(date, dataValue));
        dataAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(0); // 滾動到最新數據
    }

    // 獲取當前的日期和時間
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
