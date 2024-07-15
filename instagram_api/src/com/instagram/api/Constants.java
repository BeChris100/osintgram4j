package com.instagram.api;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final String URL_API = "https://i.instagram.com/api/v1";

    public static final String APP_VERSION = "336.0.0.35.90"; // update to latest version

    public static final String APP_ID = "567067343352427";

    public static String LOCALE = "en_US";

    public static Map<String, String> putDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        return headers;
    }

    public static class Privates {

            /*
            'x-ig-set-www-claim': wwwClaim,
            'ig-set-authorization': auth,
            'ig-set-password-encryption-key-id': pwKeyId,
            'ig-set-password-encryption-pub-key': pwPubKey,
             */

        public static String WWW_CLAIM;
        public static String IG_AUTH_HEADER;
        public static String PASS_ENC_KEY_ID;
        public static String PASS_ENC_PUB_KEY;

    }

}
