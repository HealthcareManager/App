package com.luce.healthmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    private ImageView vipImage;
    private String jwtToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false); // 設置 Fragment 佈局

        // 從 SharedPreferences 讀取用戶資料
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        jwtToken = sharedPreferences.getString("jwt_token", null);
        String username = sharedPreferences.getString("username", "未登入");
        String userId = sharedPreferences.getString("userId", "");
        String userImage = sharedPreferences.getString("userImage", "");
        String role = sharedPreferences.getString("role", "");

        // 設置 TextView 與 ImageView
        TextView userNameTextView = view.findViewById(R.id.user_name);
        ImageView avatar = view.findViewById(R.id.profile_image);
        LinearLayout userdata = view.findViewById(R.id.userdata);
        Button logoutButton = view.findViewById(R.id.logout_button);
        Button loginButton = view.findViewById(R.id.login_button);// 使用 view.findViewById
        LinearLayout cardprime = view.findViewById(R.id.cardprime);
        LinearLayout aboutme = view.findViewById(R.id.aboutme);
        vipImage = view.findViewById(R.id.vip_image);

        Log.d("ProfileFragment", role);

        // 檢查用戶是否已登入，控制登出按鈕顯示
        if (!userId.isEmpty()) {
            // 用戶已登入，顯示登出按鈕並隱藏登入按鈕
            logoutButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);

            if (role.equals("VIP")) {
                vipImage.setVisibility(View.VISIBLE);
                vipImage.setImageDrawable(getResources().getDrawable(R.drawable.vip));
                vipImage.setVisibility(View.VISIBLE);
            }
        } else {
            // 用戶未登入，顯示登入按鈕並隱藏登出按鈕
            logoutButton.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
            vipImage.setVisibility(View.GONE);
        }

        // 轉向關於幫助與回饋
        LinearLayout helpFeedbackCard = view.findViewById(R.id.help_feedback_card);
        helpFeedbackCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HelpActivity.class));
            }
        });

        // 點擊事件: 關於我們頁面
        aboutme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(), AboutmeActivity.class));
            }
        });

        if (jwtToken != null && !userImage.isEmpty()) {
            // 使用 Glide 加載圖片並添加 Token 驗證
            GlideUrl glideUrl = new GlideUrl(userImage, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + jwtToken) // 添加 JWT Token 驗證
                    .build());

            Log.d("Yuchen", userImage);

            // 加載圖片
            Glide.with(this)
                    .load(glideUrl) // 使用自定義的 GlideUrl 加載圖片
                    .circleCrop()
                    .placeholder(R.drawable.chatbot) // 加載中的預設圖片
                    .error(R.drawable.chatbot) // 加載失敗的預設圖片
                    .into(avatar);
        } else {
            // 沒有圖片路徑或 token，顯示預設的 drawable 圖片
            avatar.setImageResource(R.drawable.chatbot);
        }

        if (jwtToken != null && !jwtToken.isEmpty()) {
            cardprime.setOnClickListener(v -> showPaymentDialog());
        }

        // 點擊事件: 控制登入/登出操作
        if (jwtToken == null) {
            // 用戶未登入，點擊圖片或名稱跳轉到登入頁面
            View.OnClickListener loginListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                }
            };
            avatar.setOnClickListener(loginListener);
            userNameTextView.setOnClickListener(loginListener);
            userdata.setOnClickListener(null);
        } else {
            View.OnClickListener userDataListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(requireActivity(), UserDataActivity.class));
                }
            };
            // 用戶已登入，不執行點擊操作，或者開啟編輯功能
            avatar.setOnClickListener(null);
            userNameTextView.setOnClickListener(null);
            userdata.setOnClickListener(userDataListener);
        }

        // 點擊事件: 登出按鈕
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
        // 處理登入按鈕
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳轉到登入頁面
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    public void showPaymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("購買會員");

        String[] paymentOptions = {"1個月", "3個月", "6個月", "12個月"};

        String[] productNamesInEnglish = {"VIP-1 month", "VIP-3 months", "VIP-6 months", "VIP-12 months"};

        final String[] selectedOption = new String[1]; // 用於儲存用戶選擇的方案
        final String[] selectedProductNameInEnglish = new String[1];
        final int[] productPrice = new int[1]; // 用於儲存對應的價格
        final int[] amount = new int[1]; // 用於儲存對應的金額

        // 設定會員方案的價格
        final int[] prices = {100, 270, 500, 900};
        final int[] amounts = {100, 270, 500, 900};

        builder.setSingleChoiceItems(paymentOptions, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedOption[0] = paymentOptions[which]; // 儲存所選方案
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("productName", selectedOption[0]);
                editor.apply();

                selectedProductNameInEnglish[0] = productNamesInEnglish[which];
                productPrice[0] = prices[which];
                amount[0] = amounts[which];
                Log.d("PaymentDialog", "Selected option: " + selectedOption[0] + ", Price: " + productPrice[0]);
            }
        });

        // 確定按鈕
        builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("PaymentDialog", "Positive button clicked.");
                if (selectedOption[0] != null) {
//                    Toast.makeText(requireContext(), "您選擇了 " + selectedOption[0], Toast.LENGTH_SHORT).show();
                    String currency = "TWD";
                    String orderId = "order" + UUID.randomUUID().toString();
                    String packageName = "RebeccaShop";
                    String productName = selectedProductNameInEnglish[0]; // 根據選擇的方案設置產品名稱
                    int productQuantity = 1;

                    try {
                        JSONObject requestBody = new JSONObject();
                        requestBody.put("amount", amount[0]);
                        requestBody.put("currency", currency);
                        requestBody.put("orderId", orderId); // 設定訂單ID

                        JSONObject packageObject = new JSONObject();
                        packageObject.put("id", 123);
                        packageObject.put("name", packageName);
                        packageObject.put("amount", amount[0]);

                        JSONObject productObject = new JSONObject();
                        productObject.put("name", productName);
                        productObject.put("imageUrl", "");
                        productObject.put("quantity", productQuantity);
                        productObject.put("price", productPrice[0]); // 設定產品價格

                        packageObject.put("products", new JSONArray().put(productObject));
                        requestBody.put("packages", new JSONArray().put(packageObject));

                        JSONObject redirectUrls = new JSONObject();
                        redirectUrls.put("confirmUrl", "com.luce.healthmanager://callback?result=success&orderId=" + orderId);
                        redirectUrls.put("cancelUrl", "com.luce.healthmanager://callback?result=cancel");

                        requestBody.put("redirectUrls", redirectUrls);

                        Log.d("PaymentDialog", String.valueOf(requestBody));

                        // 使用 OkHttpClient 發送請求
                        OkHttpClient client = new OkHttpClient();
                        RequestBody body = RequestBody.create(requestBody.toString().getBytes(StandardCharsets.UTF_8), MediaType.parse("application/json; charset=utf-8"));
                        Request request = new Request.Builder()
                                //.url("http://10.0.2.2:8080/api/payment") // 請求URL
                                .url("https://healthcaremanager.myvnc.com:8443/HealthcareManager/api/payment") // 請求URL
                                .addHeader("Authorization", "Bearer " + jwtToken) // 添加 JWT Token 驗證
                                .post(body)
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                Log.e("PaymentError", "Request failed: " + e.getMessage());
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    final String responseData = response.body().string();
                                    Log.d("PaymentResponse", "Response: " + responseData);

                                    try {
                                        JSONObject jsonResponse = new JSONObject(responseData);
                                        if (jsonResponse.has("status") && jsonResponse.getString("status").equals("success")) {
                                            String responseBodyString = jsonResponse.getString("response");
                                            JSONObject responseBody = new JSONObject(responseBodyString);

                                            if (responseBody.has("info")) {
                                                JSONObject infoObject = responseBody.getJSONObject("info");
                                                if (infoObject.has("paymentUrl")) {
                                                    JSONObject paymentUrlObject = infoObject.getJSONObject("paymentUrl");
                                                    String paymentUrl = paymentUrlObject.optString("web", null);

                                                    if (paymentUrl != null && !paymentUrl.isEmpty()) {
                                                        // 跳轉到瀏覽器
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setData(Uri.parse(paymentUrl));
                                                        startActivity(intent);
                                                    }
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Log.e("PaymentError", "Server responded with error: " + response.message());
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(requireContext(), "請先選擇一個方案", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 取消按鈕
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    // 每次返回頁面時重新加載用戶圖片及數據
    @Override
    public void onResume() {
        super.onResume();
        refreshUserData(); // 刷新資料
    }

    // 加載用戶圖片
    private void loadUserImage(String jwtToken, String userImage, ImageView avatar) {
        if (jwtToken != null && !userImage.isEmpty()) {
            GlideUrl glideUrl = new GlideUrl(userImage, new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + jwtToken)
                    .build());

            Glide.with(this)
                    .load(glideUrl)
                    .circleCrop()
                    .placeholder(R.drawable.chatbot)
                    .error(R.drawable.chatbot)
                    .into(avatar);
        } else {
            // 沒有圖片路徑或 token，顯示預設的 drawable 圖片
            avatar.setImageResource(R.drawable.chatbot);
        }
    }

    // 刷新用戶數據
    private void refreshUserData() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String jwtToken = sharedPreferences.getString("jwt_token", null);
        String username = sharedPreferences.getString("username", "未登入");
        String userId = sharedPreferences.getString("userId", "");
        String userImage = sharedPreferences.getString("userImage", "");

        TextView userNameTextView = getView().findViewById(R.id.user_name);
        ImageView avatar = getView().findViewById(R.id.profile_image);

        // 更新用戶名和ID
        userNameTextView.setText(username);

        // 加載用戶圖片
        loadUserImage(jwtToken, userImage, avatar);
    }

    // 登出用戶並清除 SharedPreferences
    private void logoutUser() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // 清除所有保存的資料
        editor.apply(); // 應用更改

        Toast.makeText(getActivity(), "您已登出", Toast.LENGTH_SHORT).show();
        // 重新加載頁面
        getActivity().recreate();
    }
}