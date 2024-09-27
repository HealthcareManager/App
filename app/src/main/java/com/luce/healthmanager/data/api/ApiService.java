package com.luce.healthmanager.data.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/auth/google-login") // 替换为实际的后端 API 路径
    Call<Void> googleLogin(@Body Map<String, String> idToken);
}
