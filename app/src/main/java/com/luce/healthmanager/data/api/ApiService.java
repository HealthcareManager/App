package com.luce.healthmanager.data.api;

import com.luce.healthmanager.HeartRateData;
import com.luce.healthmanager.UserResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.GET;

public interface ApiService {

    @POST("api/auth/google-login") // 替换为实际的后端 API 路径
    Call<UserResponse> googleLogin(@Body Map<String, String> idToken);

    @POST("api/auth/facebook-login")
    Call<UserResponse> loginWithFacebook(@Body String accessToken);

    @Multipart
    @POST("api/auth/upload-image/{id}")
    Call<ResponseBody> uploadImageWithToken(
            @Header("Authorization") String token,  // 添加 Authorization header
            @Path("id") String userId,
            @Part MultipartBody.Part file
    );

    @GET("api/healthData")
    Call<List<HeartRateData>> getHeartRateData();

    @PUT("api/auth/update-username/{id}")
    Call<ResponseBody> updateUsername(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body Map<String, String> requestBody
    );

    @PUT("api/auth/update-password/{id}")
    Call<ResponseBody> updatePassword(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body Map<String, String> requestBody
    );

}
