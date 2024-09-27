package com.luce.healthmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        EditText emailInput = findViewById(R.id.email_input);
        EditText messageInput = findViewById(R.id.message_input);
        Button submitButton = findViewById(R.id.submit_button);

        // 固定的收件人
        final String fixedRecipient = "a115293001@gmail.com";

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = emailInput.getText().toString();
                String messageContent = messageInput.getText().toString();

                if (userEmail.isEmpty()) {
                    Toast.makeText(HelpActivity.this, "請輸入您的電子郵件地址", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (messageContent.isEmpty()) {
                    Toast.makeText(HelpActivity.this, "請輸入您的訊息", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 使用 Intent.ACTION_SENDTO 來發送郵件
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + fixedRecipient)); // 固定的收件人地址
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "幫助與回饋");

                // 將使用者的郵件地址和訊息加入到正文中
                String finalMessage = "寄件人: " + userEmail + "\n\n訊息內容:\n" + messageContent;
                emailIntent.putExtra(Intent.EXTRA_TEXT, finalMessage);

                // 確認有可用的郵件應用
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                } else {
                    Toast.makeText(HelpActivity.this, "找不到郵件應用", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 如果你有返回按鈕，這裡可以設置它的行為
        findViewById(R.id.back_button).setOnClickListener(view -> onBackPressed());
    }
}
