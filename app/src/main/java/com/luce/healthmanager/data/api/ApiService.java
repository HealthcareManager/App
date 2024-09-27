package com.luce.healthmanager.data.api;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @POST("api/auth/google-login") // 替换为实际的后端 API 路径
    Call<Void> googleLogin(@Body Map<String, String> idToken);

    @Multipart
    @POST("upload-image/{id}")
    Call<ResponseBody> uploadImage(
            @Path("id") long userId,
            @Part MultipartBody.Part file
    );
}
