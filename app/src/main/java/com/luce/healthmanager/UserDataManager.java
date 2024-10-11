package com.luce.healthmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class UserDataManager {

    // 保存用户数据到 SharedPreferences 的方法
    public static void saveUserDataToPreferences(Context context, UserResponse user) {
        try {
            // 获取 SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Log.d("at UDM","userID is " + user.getId());
            // 保存用户数据到 SharedPreferences
            editor.putString("userId", user.getId());
            editor.putString("jwt_token", user.getJwtToken());
            editor.putString("username", user.getUsername());
            editor.putString("email", user.getEmail());
            editor.putString("dateOfBirth", String.valueOf(user.getDateOfBirth()));
            editor.putString("gender", user.getGender() != null ? user.getGender() : "");
            editor.putString("height", user.getHeight() != null ? String.valueOf(user.getHeight()) : "");
            editor.putString("weight", user.getWeight() != null ? String.valueOf(user.getWeight()) : "");
            editor.putString("userImage", user.getImagelink() != null ? user.getImagelink() : "");
            editor.putString("role", user.getRole());

            editor.apply();

            // 跳转到 MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("showHealthFragment", true);
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("test", String.valueOf(e));
            Toast.makeText(context, "保存用户数据出错", Toast.LENGTH_SHORT).show();
        }
    }

    public static void saveUserDataToPreferences(Context context, JSONObject userData) {

        Log.d("UserData", "Received userData: " + userData.toString());
        // 從 JSONObject 獲取用戶數據
        String username = userData.optString("username", "");
        String userId = userData.optString("id", "");
        String email = userData.optString("email", "");
        String gender = userData.optString("gender", "");
        String height = userData.optString("height", "");
        String weight = userData.optString("weight", "");
        String dateOfBirth = userData.optString("dateOfBirth", "");
        String userImage = userData.optString("userImage", "");
        String role = userData.optString("role", "");

        // 获取 SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 保存用户数据到 SharedPreferences
        editor.putString("userId", userId);
        editor.putString("username", username);
        editor.putString("email", email);
        editor.putString("gender", gender.isEmpty() ? "" : gender);
        editor.putString("height", height.isEmpty() ? "" : height);
        editor.putString("weight", weight.isEmpty() ? "" : weight);
        editor.putString("dateOfBirth", dateOfBirth.isEmpty() ? "" : dateOfBirth);
        editor.putString("userImage", userImage.isEmpty() ? "" : userImage);
        editor.putString("role", role);
        editor.apply();

        // 跳转到 MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("showHealthFragment", true);
        context.startActivity(intent);

    }

}
