package net.bc100dev.osintgram4j.dev;

import org.json.JSONObject;

public class PhoneDevice extends Device {

    @Override
    public PhoneDevice loadJson(JSONObject data) {
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
