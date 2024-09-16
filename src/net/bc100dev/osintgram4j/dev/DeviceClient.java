package net.bc100dev.osintgram4j.dev;

import org.json.JSONObject;

public class DeviceClient {

    public static DeviceClient fromJson(Platform platform, AppType appType, JSONObject clientApp) {
        return null;
    }

    public enum Platform {

        MOBILE,

        DESKTOP

    }

    public enum AppType {

        APP,

        BROWSER

    }

}
