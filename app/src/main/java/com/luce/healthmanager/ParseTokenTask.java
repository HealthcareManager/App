package com.luce.healthmanager;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ParseTokenTask extends AsyncTask<String, Void, JSONObject> {
    private Context context;
    private ParseTokenCallback callback;

    public ParseTokenTask(Context context, ParseTokenCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        String token = params[0];
        Log.d("test","token at PTT is :" + token);
        JSONObject userData = null;

        try {
            //URL url = new URL("http://192.168.50.38:8080/HealthcareManager/api/auth/validate-token");
            URL url = new URL("http://10.0.2.2:8080/api/auth/validate-token");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setDoOutput(true);

            int statusCode = connection.getResponseCode();
            BufferedReader br;
            if (statusCode == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
            }

            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            userData = new JSONObject(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userData;
    }

    @Override
    protected void onPostExecute(JSONObject userData) {
        if (callback != null) {
            callback.onParseTokenCompleted(userData);
        }
    }

    public interface ParseTokenCallback {
        void onParseTokenCompleted(JSONObject userData);
    }
}
