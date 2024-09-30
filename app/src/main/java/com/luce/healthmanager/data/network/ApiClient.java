package com.luce.healthmanager.data.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.luce.healthmanager.AuthInterceptor;
import com.luce.healthmanager.R;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            // 獲取 SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

            // 創建 OkHttpClient 並加入 AuthInterceptor 攔截器
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(sharedPreferences))
                    .connectTimeout(30, TimeUnit.SECONDS) // 可選的設置連接超時
                    .readTimeout(30, TimeUnit.SECONDS)    // 可選的設置讀取超時
                    .build();

            // 配置 Retrofit
            retrofit = new Retrofit.Builder()
                    //.baseUrl("http://192.168.50.38:8080/HealthcareManager/") // 替換為你的 API 基礎 URL
                    .baseUrl(context.getString(R.string.api_base_url)) // 替換為你的 API 基礎 URL
                    .client(okHttpClient) // 使用自定義的 OkHttpClient
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
