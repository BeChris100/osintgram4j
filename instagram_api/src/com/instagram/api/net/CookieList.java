package com.instagram.api.net;

import java.util.ArrayList;
import java.util.List;

public class CookieList {

    private static final List<Cookie> cookies;

    static {
        cookies = new ArrayList<>();
    }

    public static void setCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public static List<Cookie> getCookies() {
        return cookies;
    }

}
