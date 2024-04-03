package com.instagram.api.net;

import java.util.Map;

record IQueueData(String endpoint, Map<String, String> headers, byte[] postData) {

    public boolean isEndpointValid() {
        return !(endpoint.startsWith("http://") || endpoint.startsWith("https://"));
    }

}
