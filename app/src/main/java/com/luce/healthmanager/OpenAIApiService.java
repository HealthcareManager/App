package com.luce.healthmanager;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OpenAIApiService {
    @POST("api/openai/ask/{userId}")
    Call<Map<String, Object>> askHealthQuestion(
            @Path("userId") String userId,
            @Body Map<String, String> request
    );
}

