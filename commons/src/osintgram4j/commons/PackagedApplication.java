package osintgram4j.commons;

import java.io.File;

/**
 * This class will only import functions, if it was properly packaged
 * by the Java Packager (jpackage). Either from the Shell Script,
 * or completely manual.
 */
public class PackagedApplication {

    private static String withValue(String key) {
        return System.getProperty(key);
    }

    private static final String oldPrefix = ".app.locations";
    private static final String newPrefix = "og4j.location";

    public static File getApplicationDirectory() {
        String k = oldPrefix + ".app_dir";
        String v = withValue(k);
        if (v != null)
            return new File(v);

        k = newPrefix + ".app_dir";
        v = withValue(k);
        return v != null ? new File(v) : null;
    }

    public static File getRootDirectory() {
        String k = oldPrefix + ".root_dir";
        String v = withValue(k);
        if (v != null)
            return new File(v);

        k = newPrefix + ".root_dir";
        v = withValue(k);
        return v != null ? new File(v) : null;
    }

    public static File getBinaryDirectory() {
        String k = oldPrefix + ".bin_dir";
        String v = withValue(k);
        if (v != null)
            return new File(v);

        k = newPrefix + ".bin_dir";
        v = withValue(k);
        return v != null ? new File(v) : null;
    }

}
