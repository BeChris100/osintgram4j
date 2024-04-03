package net.bc100dev.osintgram4j.cxx;

import net.bc100dev.commons.ApplicationException;

import java.io.IOException;
import java.util.List;

public class KbdDevice {

    public static native List<KbdDevice> listDevices() throws IOException, ApplicationException;

    private native void nOpen(KbdDevice device) throws IOException, ApplicationException;
    private native void nClose(KbdDevice device) throws IOException, ApplicationException;
    private native void nAttachListener(KbdDevice device, KeyInputListener kil) throws IOException, ApplicationException;

    protected KbdDevice(String absolutePath) {
    }

    public static KbdDevice connectById(String identifier) {
        return null;
    }

    public static KbdDevice connectByPath(String path) {
        return null;
    }

    public static KbdDevice currentDevice() {
        return null;
    }

    public void open() throws IOException, ApplicationException {
    }

    public void close() throws IOException, ApplicationException {
    }

    public void setKeyInputListener(KeyInputListener keyInputListener) {
    }

}
