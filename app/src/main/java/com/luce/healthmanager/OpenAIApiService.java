package com.luce.healthmanager;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OpenAIApiService {
    @POST("/api/openai/ask")
    Call<Map<String, Object>> askHealthQuestion(@Body Map<String, String> question);
}

