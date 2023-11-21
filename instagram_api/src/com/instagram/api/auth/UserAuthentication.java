package com.instagram.api.auth;

import com.instagram.api.ApiNetworkException;
import net.bc100dev.commons.utils.io.WebIOStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserAuthentication {

    private final String username, password, mfa;

    private String rolloutHash = "1";

    protected UserAuthentication(String username, String password, String mfa) {
        this.username = username;
        this.password = password;
        this.mfa = mfa;
    }

    public static UserAuthentication open(String username, String password) {
        return new UserAuthentication(username, password, null);
    }

    public static UserAuthentication open(String username, String password, String mfa) {
        return new UserAuthentication(username, password, mfa);
    }

    private void init() {
    }

    public String getMfa() {
        return mfa;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private void createRolloutHash() {
        if (rolloutHash.equals("1"))
            init();
    }

    public void login(AuthenticationCallback callback) throws IOException {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty())
            throw new ApiNetworkException("Username/Password is blank");

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("queryParams", "{}");

        createRolloutHash();

        WebIOStream stream = WebIOStream.openStream("https://www.instagram.com/accounts/login/ajax/", "POST", params);
        WebIOStream.Response response = stream.getResponse();
        System.out.println("Login Result: " + response.getCode() + " - " + response.getMessage());

        byte[] buff = stream.readContents();
        stream.close();

        System.out.println("Received data of " + new String(buff));
    }

}
