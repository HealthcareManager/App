package com.luce.healthmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    // 定義 LinearLayout 變量來表示自定義的按鈕
    LinearLayout googleLoginButton, facebookLoginButton, lineLoginButton;

    private GoogleSignInClient googleSignInClient;  // Google Sign-In 客戶端
    private static final int RC_SIGN_IN = 100;  // 用於 Google Sign-In 的請求碼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 登入界面
        Button registerButton = findViewById(R.id.register_button);

        // 初始化按鈕
        googleLoginButton = findViewById(R.id.google_login_button);
        facebookLoginButton = findViewById(R.id.facebook_login_button);
        lineLoginButton = findViewById(R.id.line_login_button);

        usernameEditText = findViewById(R.id.username_input);
        passwordEditText = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        // 初始化 Google Sign-In 客戶端
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // 這裡填入你在 Google Cloud Console 中的 Web 客戶端 ID
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳轉到 RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "請輸入用戶名和密碼", Toast.LENGTH_SHORT).show();
            } else {
                new LoginTask().execute(username, password);
            }
        });

        // Google 登入按鈕點擊事件
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();  // 調用 Google 登入邏輯
            }
        });

        // Facebook 登入按鈕點擊事件
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 這裡放置 Facebook 登入邏輯
                facebookSignIn();
            }
        });

        // Line 登入按鈕點擊事件
        lineLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 這裡放置 Line 登入邏輯
            }
        });
    }

    // Google 登入邏輯
    private void googleSignIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d("test","data is " + data);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("test","task is " + task);
                Log.d("test","456");
                if (account != null) {
                    // 獲取 ID token 並傳送到後端
                    Log.d("test","account is " + account);
                    String idToken = account.getIdToken();
                    Log.d("test","idToken is " + idToken);
                    sendIdTokenToBackend(idToken);
                }
            } catch (ApiException e) {
                // 處理登入錯誤
                Log.e("GoogleSignInError", "Sign-in failed: " + e.getStatusCode(), e);
                Toast.makeText(LoginActivity.this, "Google 登入失敗", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendIdTokenToBackend(String idToken) {
        // 在這裡實作傳送 ID token 給後端進行驗證的邏輯
        // 比如可以使用 Retrofit 或其他 HTTP 客戶端來進行網路請求
        Toast.makeText(LoginActivity.this, "ID Token: " + idToken, Toast.LENGTH_SHORT).show();
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String result = null;

            try {
                //URL url = new URL("http://192.168.50.38:8080/HealthcareManager/api/auth/login");
                URL url = new URL("http://10.0.2.2:8080/api/auth/login");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String jsonInputString = "{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}";

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int statusCode = connection.getResponseCode(); // 獲取狀態碼
                BufferedReader br;
                if (statusCode == HttpURLConnection.HTTP_OK) { // 200
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                } else {
                    br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
                }

                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                result = response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);

                    // 檢查響應中是否包含token
                    if (jsonResponse.has("token")) {
                        String token = jsonResponse.getString("token");
                        Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_SHORT).show();
                        // 保存 token 到 SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("jwt_token", token);
                        editor.apply();

                        // 使用公共的 ParseTokenTask
                        new ParseTokenTask(LoginActivity.this, new ParseTokenTask.ParseTokenCallback() {
                            @Override
                            public void onParseTokenCompleted(JSONObject userData) {
                                if (userData != null) {
                                    try {
                                        String username = userData.getString("username");
                                        String userId = userData.getString("id");
                                        String email = userData.getString("email");
                                        String gender = userData.getString("gender");
                                        String height = userData.getString("height");
                                        String weight = userData.getString("weight");
                                        String dateOfBirth = userData.getString("dateOfBirth");
                                        String userImage = userData.getString("userImage");

                                        // 保存用户数据到 SharedPreferences
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
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
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.putExtra("showHealthFragment", true);
                                        startActivity(intent);
                                        finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(LoginActivity.this, "解析用戶數據出錯", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this, "解析 token 失敗", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).execute(token);

                    } else {
                        // 如果返回中不包含token，顯示錯誤訊息
                        String message = jsonResponse.getString("message");
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "解析返回訊息時出錯", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Facebook 登入處理邏輯
    private void facebookSignIn() {
        // 這裡實作 Facebook 登入邏輯
        Toast.makeText(LoginActivity.this, "Facebook 登入", Toast.LENGTH_SHORT).show();
    }

}
