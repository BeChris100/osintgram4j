package com.instagram.api.net;

import net.bc100dev.commons.ApplicationRuntimeException;

import java.util.Map;

public class IGRequest {

    private final Map<String, String> headers;
    private final byte[] postData;
    private final String apiEndpoint;

    public IGRequest(String apiEndpoint, Map<String, String> headers) {
        this.apiEndpoint = apiEndpoint;
        this.headers = headers;
        this.postData = new byte[0];

        if (apiEndpoint.startsWith("http://") || apiEndpoint.startsWith("https://"))
            throw new ApplicationRuntimeException("The API Endpoint cannot start with a HTTP Prefix (http:// or https://)");
    }

    public IGRequest(String apiEndpoint, Map<String, String> headers, byte[] postData) {
        this.apiEndpoint = apiEndpoint;
        this.headers = headers;
        this.postData = postData;

        if (apiEndpoint.startsWith("http://") || apiEndpoint.startsWith("https://"))
            throw new ApplicationRuntimeException("The API Endpoint cannot start with a HTTP Prefix (http:// or https://)");
    }

    public void enqueue(IRequest request) {
    }

}
