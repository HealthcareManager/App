package com.luce.healthmanager;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @Multipart
    @POST("upload-image/{id}")
    Call<ResponseBody> uploadImage(
            @Path("id") long userId,
            @Part MultipartBody.Part file
    );
}

