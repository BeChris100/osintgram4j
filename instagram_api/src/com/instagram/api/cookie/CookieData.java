package com.instagram.api.cookie;

public record CookieData(String name, String value, String domain, String expires, boolean httpOnly, int maxAge, boolean partitioned,
                         String path, boolean secure, SameSite siteValue) {

    public enum SameSite {

        Strict,

        Lax,

        None

    }

}
