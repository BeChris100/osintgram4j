package net.bc100dev.osintgram4j.dev;

public abstract class Device {

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
