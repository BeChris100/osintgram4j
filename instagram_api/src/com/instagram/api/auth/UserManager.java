package com.instagram.api.auth;

import com.instagram.api.ApiNetworkException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManager {

    private final String username, password;

    private boolean authorized = false;

    protected UserManager(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static UserManager openAuthentication(String username, String password) {
        return new UserManager(username, password);
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private String mkPayload(JSONObject payload) {
        if (payload == null)
            return "";

        if (payload.isEmpty())
            return "";

        StringBuilder payloadStr = new StringBuilder();
        List<String> keys = payload.keySet().stream().toList();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);

            payloadStr.append(key).append("=").append(payload.get(key).toString());

            if (i != keys.size() - 1)
                payloadStr.append("&");
        }

        return URLEncoder.encode(payloadStr.toString(), StandardCharsets.UTF_8);
    }

    public void login(AuthenticationCallback callback) throws IOException, GeneralSecurityException {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty())
            throw new ApiNetworkException("Username/Password is blank");

        final String testHeads = """
                Host: www.instagram.com
                User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:120.0) Gecko/20100101 Firefox/120.0
                Accept: */*
                Accept-Language: en-US,en;q=0.7,de-DE;q=0.3
                Accept-Encoding: gzip, deflate, br
                X-CSRFToken: CFQWCwfyofKApxtVdXLPLfz0DOl07MXW
                X-Instagram-AJAX: 1010050085
                X-IG-App-ID: 936619743392459
                X-ASBD-ID: 129477
                X-IG-WWW-Claim: 0
                Content-Type: application/x-www-form-urlencoded
                X-Requested-With: XMLHttpRequest
                Origin: https://www.instagram.com
                DNT: 1
                Sec-GPC: 1
                Connection: keep-alive
                Referer: https://www.instagram.com/
                Sec-Fetch-Dest: empty
                Sec-Fetch-Mode: cors
                Sec-Fetch-Site: same-origin""";

        Map<String, String> headers = new HashMap<>();

        for (String testHead : testHeads.split("\n")) {
            String[] opt = testHead.split(":", 2);
            headers.put(opt[0].trim(), opt[1].trim());
        }

        PassEnc enc = new PassEnc("", (byte) 5);
        String passEncrypted = enc.encryptPassword("bc100gaming");
    }

    public void twoFactorLogin(TwoFactorIdentifier identifier, String code) throws IOException, GeneralSecurityException {
    }

    public void logout() throws IOException, GeneralSecurityException {
    }

}
