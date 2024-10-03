package com.luce.healthmanager.data.api;

import com.luce.healthmanager.HeartRateData;
import com.luce.healthmanager.UserResponse;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/auth/line-callback")
    Call<UserResponse> sendAuthorizationCode(@Body Map<String, String> requestBody);

    @POST("api/auth/google-login") // 替换为实际的后端 API 路径
    Call<UserResponse> googleLogin(@Body Map<String, String> idToken);

    @POST("api/auth/facebook-login")
    Call<UserResponse> loginWithFacebook(@Body String accessToken);

    @Multipart
    @POST("api/userData/upload-image/{id}")
    Call<ResponseBody> uploadImage(
            @Path("id") String userId,
            @Part MultipartBody.Part file
    );

    @GET("api/healthData")
    Call<List<HeartRateData>> getHeartRateData();

    @PUT("api/userData/update-user-data/{id}")
    Call<ResponseBody> updateUserData(
            @Path("id") String id,
            @Body Map<String, Object> requestBody
    );

    @PUT("api/userData/update-password/{id}")
    Call<ResponseBody> updatePassword(
            @Path("id") String id,
            @Body Map<String, String> requestBody
    );

    @GET("api/userData/password/{id}")
    Call<ResponseBody> getPassword(
            @Path("id") String id
    );
}
