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

            // 保存用户数据到 SharedPreferences
            editor.putString("userId", user.getId());
            editor.putString("username", user.getUsername());
            editor.putString("email", user.getEmail());
            editor.putString("gender", user.getGender());
            editor.putString("height", String.valueOf(user.getHeight()));
            editor.putString("weight", String.valueOf(user.getWeight()));
            editor.putString("dateOfBirth", String.valueOf(user.getDateOfBirth()));
            editor.putString("userImage", user.getImagelink());
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
        try {
            String username = userData.getString("username");
            String userId = userData.getString("id");
            String email = userData.getString("email");
            String gender = userData.getString("gender");
            String height = userData.getString("height");
            String weight = userData.getString("weight");
            String dateOfBirth = userData.getString("dateOfBirth");
            String userImage = userData.getString("userImage");

            // 获取 SharedPreferences
            SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // 保存用户数据到 SharedPreferences
            editor.putString("username", username);
            editor.putString("userId", userId);
            editor.putString("email", email);
            editor.putString("gender", gender);
            editor.putString("height", height);
            editor.putString("weight", weight);
            editor.putString("dateOfBirth", dateOfBirth);
            editor.putString("userImage", userImage);
            editor.apply();

            // 跳转到 MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("showHealthFragment", true);
            context.startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("test", String.valueOf(e));
            Toast.makeText(context, "保存用户数据出错", Toast.LENGTH_SHORT).show();
        }
    }

}
