package com.luce.healthmanager.network;

public class TokenRequest {
    private String idToken;

    public TokenRequest(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
}
