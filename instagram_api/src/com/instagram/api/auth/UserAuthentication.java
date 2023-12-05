package com.instagram.api.auth;

import com.instagram.api.ApiNetworkException;
import net.bc100dev.commons.ApplicationRuntimeException;
import net.bc100dev.commons.utils.io.FileEncryption;
import net.bc100dev.commons.utils.io.FileUtil;
import net.bc100dev.commons.utils.io.WebIOStream;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.bc100dev.commons.utils.RuntimeEnvironment.USER_HOME;
import static net.bc100dev.commons.utils.RuntimeEnvironment.getOperatingSystem;

public class UserAuthentication {

    private final String username, password;

    private boolean authorized = false;

    protected UserAuthentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static UserAuthentication openAuthentication(String username, String password) {
        return new UserAuthentication(username, password);
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

    public void login(AuthenticationCallback callback) throws IOException {
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

        JSONObject payload = new JSONObject();
        payload.put("username", username);
        payload.put("queryParams", new JSONObject());
        payload.put("trustedDeviceRecords", new JSONObject());
        payload.put("optIntoOneTap", false);
        // Commented out code: Password encryption not implemented
        //payload.put("enc_password", mkAuthPass());
        //payload.put("password", password);

        String payloadStr = mkPayload(payload);

        System.out.println("Payload: " + payloadStr);
        System.out.println("Decoded Payload: " + URLDecoder.decode(payloadStr, StandardCharsets.UTF_8));

        headers.put("Content-Length", String.valueOf(payloadStr.length()));

        WebIOStream stream = WebIOStream.openStream("https://www.instagram.com/accounts/login/ajax/", "POST", headers, payloadStr.getBytes());
        WebIOStream.Response response = stream.getResponse();
        System.out.println("Login Result: " + response.getCode() + " - " + response.getMessage());

        byte[] buff = stream.readContents();
        stream.close();

        System.out.println("Received data:\n" + new String(buff));
    }

}
