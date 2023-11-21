package com.instagram.api.net;

import java.util.Map;

public record Cookie(String domain, String path, Map<String, String> values) {
}
