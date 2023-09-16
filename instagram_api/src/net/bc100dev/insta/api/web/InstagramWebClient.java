package net.bc100dev.insta.api.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InstagramWebClient {

    private static final String API_URL = "https://www.instagram.com/query/";
    private static final String GRAPHQL_API_URL = "https://www.instagram.com/graphql/query/";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.1.2 Safari/605.1.15";
    private static final String MOBILE_USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 11_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.0 Mobile/15E148 Safari/604.1";

    private String userAgent;
    private final List<WebClientArguments> clientArguments = new ArrayList<>();

    public InstagramWebClient(String userAgent, WebClientArguments... args) {
        this.userAgent = userAgent;

        clientArguments.addAll(Arrays.asList(args));
    }

}
