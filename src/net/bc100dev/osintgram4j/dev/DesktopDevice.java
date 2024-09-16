package net.bc100dev.osintgram4j.dev;

public class DesktopDevice extends Device {

    @Override
    public Type constructDeviceType() {
        return Type.DESKTOP;
    }

    @Override
    public String constructUserAgent() {
        return "";
    }
}
