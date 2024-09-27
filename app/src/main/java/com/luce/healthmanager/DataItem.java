package com.luce.healthmanager;

public class DataItem {
    private String dateTime;
    private String dataValue;

    public DataItem(String dateTime, String dataValue) {
        this.dateTime = dateTime;
        this.dataValue = dataValue;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getDataValue() {
        return dataValue;
    }
}
