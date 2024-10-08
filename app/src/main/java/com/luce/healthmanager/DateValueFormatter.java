package com.luce.healthmanager;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DateValueFormatter extends ValueFormatter {
    private final List<String> dateList; // 用來儲存日期的列表

    public DateValueFormatter(List<String> dateList) {
        this.dateList = dateList;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        // 根據 X 軸的值返回相應的日期字符串
        int index = (int) value;
        if (index >= 0 && index < dateList.size()) {
            return dateList.get(index); // 返回對應的日期
        }
        return "";
    }
}
