package com.luce.healthmanager.data.repository;

import com.luce.healthmanager.data.api.ApiService;
import com.luce.healthmanager.data.network.ApiClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private ApiService apiService;

    public UserRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public void googleLogin(String idToken, final Callback<Void> callback) {
        // 创建一个 Map 来存储 idToken
        Map<String, String> body = new HashMap<>();
        body.put("idToken", idToken); // 将 idToken 添加到 Map 中

        // 调用 apiService 的 googleLogin 方法
        Call<Void> call = apiService.googleLogin(body);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // 登录成功，调用回调
                    callback.onResponse(call, response);
                } else {
                    // 处理登录失败的情况，调用回调
                    callback.onFailure(call, new Throwable("Login failed: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // 处理网络请求失败，调用回调
                callback.onFailure(call, t);
            }
        });
    }
}

