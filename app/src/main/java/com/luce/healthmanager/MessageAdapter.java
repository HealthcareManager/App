package com.luce.healthmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;  // 訊息列表

    // 構造函數
    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    // 創建 ViewHolder
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    // 綁定資料到 ViewHolder
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.getType() == Message.TYPE_USER) {  // 使用者訊息
            holder.userMessage.setText(message.getContent());
            holder.userMessage.setVisibility(View.VISIBLE);
            holder.aiMessage.setVisibility(View.GONE);
        } else {  // AI 助理訊息
            holder.aiMessage.setText(message.getContent());
            holder.aiMessage.setVisibility(View.VISIBLE);
            holder.userMessage.setVisibility(View.GONE);
        }
    }

    // 訊息數量
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder 類別
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView userMessage, aiMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessage = itemView.findViewById(R.id.user_message);
            aiMessage = itemView.findViewById(R.id.ai_message);
        }
    }
}
