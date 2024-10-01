package com.luce.healthmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginApi;
import com.linecorp.linesdk.auth.LineLoginResult;
import com.luce.healthmanager.data.api.ApiService;
import com.luce.healthmanager.data.network.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    // 定義 LinearLayout 變量來表示自定義的按鈕
    LinearLayout googleLoginButton, facebookLoginButton, lineLoginButton;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private static final int REQUEST_CODE_LINE_LOGIN = 1001;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 登入界面

        Button registerButton = findViewById(R.id.register_button);
        // Google
        googleLoginButton = findViewById(R.id.google_login_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id)) // 使用您在 Google Cloud Console 中的客戶端 ID
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // FB
        facebookLoginButton = findViewById(R.id.facebook_login_button);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getApplication());
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        lineLoginButton = findViewById(R.id.line_login_button);

        loginButton = findViewById(R.id.login_button);
        usernameEditText = findViewById(R.id.username_input);
        passwordEditText = findViewById(R.id.password_input);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        //一般用戶註冊
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳轉到 RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //一般用戶登入
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "請輸入用戶名和密碼", Toast.LENGTH_SHORT).show();
            } else {
                //login(username, password);
                new LoginTask().execute(username, password);
            }
        });

        // Google 登入按鈕點擊事件
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 這裡放置 Google 登入邏輯
                googleSignin();
            }
        });

        // Facebook 登入按鈕點擊事件
        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("LoginActivity", "Login canceled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.e("LoginActivity", "Login error: " + exception.getMessage());
                    }
                });
            }
        });

        // Line 登入按鈕點擊事件
        lineLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 這裡放置 Line 登入邏輯
                showConsentDialog();
            }
        });
    }

    private void showConsentDialog() {
        new AlertDialog.Builder(this)
                .setTitle("同意收集電子郵件地址")
                .setMessage("為了提供您更好的服務，我們將使用您的電子郵件地址進行用戶驗證及發送通知。\n您是否同意？")
                .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户同意，继续执行 LINE 登录
                        loginWithLine();
                    }
                })
                .setNegativeButton("拒絕", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户拒绝，关闭对话框
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void loginWithLine() {
//        String redirectUriForLine = getString(R.string.redirectUriForLine);  // 你應該用你在 LINE Developers 設定的重定向URI
//        String authorizationUrl = "https://access.line.me/oauth2/v2.1/authorize?response_type=code&client_id=" +
//                getString(R.string.line_channel_id) +
//                "&redirect_uri=" + redirectUriForLine +
//                "&state=12345abcde&scope=openid%20email%20profile";
//        // 使用 CustomTabsIntent 打開 LINE OAuth 網頁
//        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//        CustomTabsIntent customTabsIntent = builder.build();
//        customTabsIntent.launchUrl(this, Uri.parse(authorizationUrl));
        try {
            LineAuthenticationParams params = new LineAuthenticationParams.Builder()
                    .scopes(Arrays.asList(Scope.PROFILE, Scope.OPENID_CONNECT, Scope.OC_EMAIL))
                    .build();

            Intent loginIntent = LineLoginApi.getLoginIntent(this, getString(R.string.line_channel_id), params);
            startActivityForResult(loginIntent, REQUEST_CODE_LINE_LOGIN);
        } catch (Exception e) {
            Log.e("LineLogin", "Error logging in with LINE: " + e.getMessage());
        }

    }

    // 在 Facebook 登入成功後的回調中進行 Firebase 認證
    private void handleFacebookAccessToken(AccessToken token) {
        // 發送訪問令牌到後端進行驗證
        String accessToken = token.getToken();
        Log.d("fb test","fb accessToken is" + accessToken);
        verifyAccessToken(accessToken);
    }

    private void googleSignin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("LoginActivity", "onActivityResult called with requestCode: " + requestCode + ", resultCode: " + resultCode);

        // 處理 Google 登入
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task); // 處理 Google 登入結果
        }

        // 處理 Line 登入
        if (requestCode == REQUEST_CODE_LINE_LOGIN) {
            LineLoginResult result = LineLoginApi.getLoginResultFromIntent(data);
            switch (result.getResponseCode()) {
                case SUCCESS:
                    // 获取 authorization code
                    String authorizationCode = result.getNonce();  // 获取到的就是 authorization code
                    Log.d("LineLogin", "Authorization Code: " + authorizationCode);

                    // 将 authorization code 发送到后端
                    sendAuthorizationCodeToBackend(authorizationCode);
                    break;

                case CANCEL:
                    Log.d("LineLogin", "LINE 登录取消");
                    Toast.makeText(this, "LINE 登录取消", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    String errorMessage = result.getErrorData().toString();
                    Log.e("LineLogin", "LINE 登录失败: " + errorMessage);
                    Toast.makeText(this, "LINE 登录失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        // 處理 Facebook 登入
        callbackManager.onActivityResult(requestCode, resultCode, data); // 將結果傳遞給 Facebook 的 CallbackManager
    }

    // Line 的
    private void sendAuthorizationCodeToBackend(String authorizationCode) {
        // 構建請求體
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("code", authorizationCode);
        Log.d("Line","Line send to backend is " + apiService.sendAuthorizationCode(requestBody));

        Call<UserResponse> call = apiService.sendAuthorizationCode(requestBody);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("LineLogin", "用戶信息：" + response.body());
                } else {
                    Toast.makeText(LoginActivity.this, "獲取用戶資料失敗", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "請求失敗：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Line的
//    private void requestUserInfoFromBackend(String accessToken) {
//        Call<UserResponse> call = apiService.loginWithLine(accessToken);
//
//        call.enqueue(new Callback<UserResponse>() {
//            @Override
//            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    // 处理并存储用户资料
//                    //handleLineLoginResponse(new Gson().toJson(response.body()));
//                    Log.d("Line test","response is " + response);
//                } else {
//                    Toast.makeText(LoginActivity.this, "獲取用戶資料失敗", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserResponse> call, Throwable t) {
//                Toast.makeText(LoginActivity.this, "請求失敗：" + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // 登入成功
            String idToken = account.getIdToken();
            // 將 ID Token 發送到後端
            sendIdTokenToServer(idToken);

        } catch (ApiException e) {
            int statusCode = e.getStatusCode();
            String errorMessage = "登入失敗: " + statusCode;
            // 登入失敗
            Toast.makeText(this, "登入失敗: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            Log.w("GoogleSignIn", "登入失敗: " + e.getStatusCode() + " - " + e.getMessage());
            Log.e("GoogleSignInError", "Sign-in failed: " + e.getStatusCode(), e);
        }
    }

    // Google的
    private void sendIdTokenToServer(String idToken) {
        // 创建包含 idToken 的请求体
        Map<String, String> idTokenMap = new HashMap<>();
        idTokenMap.put("idToken", idToken);

        Call<UserResponse> call = apiService.googleLogin(idTokenMap);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    UserResponse user = response.body();
                    if (user != null) {
                        UserDataManager.saveUserDataToPreferences(LoginActivity.this, user);
                    }
                } else {
                    // Handle error response
                    System.out.println("Login failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Handle network error or failure
                System.out.println("Network error: " + t.getMessage());
            }
        });
    }

    // FB的
    private void verifyAccessToken(String accessToken) {
        // 使用 Retrofit 來呼叫後端 API
        Call<UserResponse> call = apiService.loginWithFacebook(accessToken);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();
                    if (user != null) {
                        UserDataManager.saveUserDataToPreferences(LoginActivity.this, user);
                    }
                } else {
                    Log.e("LoginActivity", "Login failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("LoginActivity", "Error: " + t.getMessage());
            }
        });
    }

    //一般登入的
    private class LoginTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String password = params[1];
            String result = null;

            try {
                //URL url = new URL("http://10.0.2.2:8080/api/auth/login");
                URL url = new URL("http://192.168.50.38:8080/HealthcareManager/api/auth/login");
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
                        Log.d("test","token at login is " + token);
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
                                    UserDataManager.saveUserDataToPreferences(LoginActivity.this, userData);
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
//    private void login(String username, String password) {
//        UserResponse user = new UserResponse(username, password);
//        Call<UserResponse> call = apiService.Login(user);
//        call.enqueue(new Callback<UserResponse>() {
//            @Override
//            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    try {
//                        // 將 response 轉為 JSON 字符串
//                        String responseString = new Gson().toJson(response);
//                        JSONObject jsonResponse = new JSONObject(responseString);
//                        Log.d("test","response is " + responseString);
//
//                        // 檢查響應中是否包含 token
//                        if (jsonResponse.has("token")) {
//                            String token = jsonResponse.getString("token");
//                            Log.d("test", "Token at login is " + token);
//
//                            // 保存 token 到 SharedPreferences
//                            SharedPreferences sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString("jwt_token", token);
//                            editor.apply();
//
//                            // 保存用戶數據
//                            UserDataManager.saveUserDataToPreferences(LoginActivity.this, user);
//                        } else {
//                            Log.e("LoginActivity", "Token not found in the response.");
//                        }
//                    } catch (JSONException e) {
//                        Log.e("LoginActivity", "JSON Parsing error: " + e.getMessage());
//                    }
//                } else {
//                    Log.e("LoginActivity", "Login failed: " + response.message());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<UserResponse> call, Throwable t) {
//                Log.e("LoginActivity", "Error: " + t.getMessage());
//            }
//        });
//    }
}
