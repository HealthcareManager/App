package com.luce.healthmanager;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private SharedPreferences sharedPreferences;

    public AuthInterceptor(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = sharedPreferences.getString("jwt_token", null); // 從 SharedPreferences 獲取 token
        Log.d("test","token at AuthInterceptor is " + token);
        Request.Builder requestBuilder = chain.request().newBuilder();
        if (token != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + token); // 添加 Authorization header
        }

        return chain.proceed(requestBuilder.build());
    }
}