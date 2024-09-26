package com.luce.healthmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private List<DataItem> dataList;

    public DataAdapter(List<DataItem> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        // 顯示最新的數據在最上面，因此使用倒序
        DataItem dataItem = dataList.get(dataList.size() - 1 - position);
        holder.dateTimeTextView.setText(dataItem.getDateTime());
        holder.dataValueTextView.setText(dataItem.getDataValue());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        TextView dateTimeTextView;
        TextView dataValueTextView;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTimeTextView = itemView.findViewById(R.id.date_time_text_view);
            dataValueTextView = itemView.findViewById(R.id.data_value_text_view);
        }
    }
}
