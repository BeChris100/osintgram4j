package net.bc100dev.insta.api;

import java.util.HashMap;
import java.util.Map;

class InstagramConstants {

    private static final String API_URL = "https://i.instagram.com/api/%s/";

    public static final String DEFAULT_API_VERSION = "v1";

    public static String getApiUrl(String version) {
        return String.format(API_URL, version);
    }

    public static Map<String, String> getHeaders(RequestUserAgent userAgent) {
        return new HashMap<>();
    }

}
