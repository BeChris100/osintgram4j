package net.bc100dev.osintgram4j.dev;

import org.json.JSONObject;

public abstract class Device {

    public abstract Device loadJson(JSONObject data);

    public abstract String constructUserAgent();

    public abstract Type constructDeviceType();

    public enum Type {

        DESKTOP,

        PHONE

    }

    public enum OperatingSystem {

        Windows,

        Linux,

        MacOS,

        Android,

        iOS

    }

}
