package com.instagram.api.auth;

public interface AuthenticationCallback {

    void onLogin(String username);

    void onError(int responseCode, String responseMessage, String body);

}
