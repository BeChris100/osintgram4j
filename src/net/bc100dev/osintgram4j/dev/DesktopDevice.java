package net.bc100dev.osintgram4j.dev;

import org.json.JSONObject;

public class DesktopDevice extends Device {

    @Override
    public DesktopDevice loadJson(JSONObject data) {
        return null;
    }

    @Override
    public Type constructDeviceType() {
        return null;
    }

    @Override
    public String constructUserAgent() {
        return "";
    }
}
