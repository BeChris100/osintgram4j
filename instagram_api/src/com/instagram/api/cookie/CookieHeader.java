package com.instagram.api.cookie;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CookieHeader {

    private static String[] getCookies(String part) {
        List<String> lCookies = new ArrayList<>();
        if (part == null || part.isEmpty())
            return new String[0];

        if (part.contains(","))
            lCookies.addAll(Arrays.asList(part.split(",")));
        else
            lCookies.add(part);

        return lCookies.toArray(new String[0]);
    }

    public static List<CookieData> fromRequest(URLConnection conn) {
        if (conn == null)
            throw new NullPointerException("Connection Pointer is null");

        List<CookieData> cookies = new ArrayList<>();
        List<String> cookieHeaders = conn.getHeaderFields().get("Set-Cookie");

        if (cookieHeaders == null)
            return cookies;

        for (String cookieHeader : cookieHeaders) {
            String[] parts = cookieHeader.split(";");
            String[] sCookieValues = getCookies(parts[0].trim());

            // parse the other parts; if there are any
            boolean httpOnly = false, partitioned = false,
                    secure = false;
            String domain = null, expires = null,
                    path = null;
            CookieData.SameSite sameSite = null;
            int maxAge = 0;

            if (parts.length > 1) {
                for (int i = 1; i < parts.length; i++) {
                    String arg = parts[i].trim();
                    String[] argParts = arg.split("=", 2);

                    switch (argParts[0].toLowerCase()) {
                        case "domain" -> domain = argParts.length == 2 ? argParts[1] : null;
                        case "expires" -> expires = argParts.length == 2 ? argParts[1] : null;
                        case "httponly" -> httpOnly = true;
                        case "max-age", "maxage" -> {
                            if (argParts.length == 2) {
                                try {
                                    maxAge = Integer.parseInt(argParts[1]);
                                } catch (NumberFormatException ignore) {}
                            }
                        }
                        case "partitioned" -> partitioned = true;
                        case "path" -> path = argParts.length == 2 ? argParts[1] : null;
                        case "secure" -> secure = true;
                        case "samesite" -> {
                            if (argParts.length == 2) {
                                switch (argParts[1].toLowerCase()) {
                                    case "strict" -> sameSite = CookieData.SameSite.Strict;
                                    case "lax" -> sameSite = CookieData.SameSite.Lax;
                                    case "none" -> sameSite = CookieData.SameSite.None;
                                    default -> sameSite = null;
                                }
                            }
                        }
                    }
                }

                for (String sCookieValue : sCookieValues) {
                    String[] kv = sCookieValue.split("=");
                    cookies.add(new CookieData(kv[0].trim(), kv[1].trim(),
                            domain, expires, httpOnly, maxAge, partitioned,
                            path, secure, sameSite));
                }
            }
        }

        return cookies;
    }

    public static String constructHeader(List<CookieData> cookies) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cookies.size(); i++) {
            CookieData cookie = cookies.get(i);
            sb.append(cookie.name()).append("=").append(cookie.value());

            if (cookies.size() != i + 1)
                sb.append("; ");
        }

        return sb.toString();
    }

}
