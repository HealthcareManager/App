package com.luce.healthmanager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class WebSocketManager {

    private static final String TAG = "WebSocketManager";
    private OkHttpClient client;
    private WebSocket webSocket;
    private WebSocketListener listener;
    private WebSocketCallback callback;

    // WebSocket 回调接口，方便 UI 层更新
    public interface WebSocketCallback {
        void onMessageReceived(String message);
    }

    // 构造函数，传入 WebSocket 回调
    public WebSocketManager(WebSocketCallback callback) {
        this.callback = callback;
    }

    // 开始 WebSocket 连接，传入 URL 和 Token
    public void startWebSocket(String serverUrl, String token) {
        client = new OkHttpClient();

        // WebSocket 请求 URL，使用 Sec-WebSocket-Protocol 传递 token
        Request request = new Request.Builder()
                .url(serverUrl) // 替换为您的 WebSocket URL
                .header("Sec-WebSocket-Protocol", token) // 使用 Subprotocol 传递 JWT Token
                .build();

        // 创建 WebSocket 监听器
        listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d(TAG, "WebSocket Opened");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Receiving message: " + text);

                // 在主线程上处理消息
                runOnUiThread(() -> {
                    // 回调传递消息
                    callback.onMessageReceived(text);
                });
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                Log.d(TAG, "Receiving bytes: " + bytes.hex());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                Log.d(TAG, "WebSocket Closing: " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Log.e(TAG, "WebSocket Failure: " + t.getMessage());
            }
        };

        // 创建 WebSocket 连接
        webSocket = client.newWebSocket(request, listener);
    }

    // 关闭 WebSocket 连接
    public void stopWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Client closed connection");
            webSocket = null;
        }
    }

    // 在主线程中执行代码
    private void runOnUiThread(Runnable action) {
        new Handler(Looper.getMainLooper()).post(action);
    }
}
