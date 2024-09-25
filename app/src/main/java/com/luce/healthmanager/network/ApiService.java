package com.luce.healthmanager.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/google-login")  // 這裡是後端的 API 路徑
    Call<Void> sendIdTokenToBackend(@Body TokenRequest tokenRequest);
}
