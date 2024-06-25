package osintgram4j.commons;

import net.bc100dev.commons.ApplicationException;

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

    public static File getApplicationDirectory() throws ApplicationException {
        String k = oldPrefix + ".app_dir";
        String v = withValue(k);
        if (v != null)
            return new File(v);

        k = newPrefix + ".app_dir";
        v = withValue(k);

        if (v == null)
            throw new ApplicationException("Application directory was not initialized");

        return new File(v);
    }

    public static File getRootDirectory() throws ApplicationException {
        String k = oldPrefix + ".root_dir";
        String v = withValue(k);
        if (v != null)
            return new File(v);

        k = newPrefix + ".root_dir";
        v = withValue(k);

        if (v == null)
            throw new ApplicationException("Root directory was not initialized");

        return new File(v);
    }

    public static File getBinaryDirectory() throws ApplicationException {
        String k = oldPrefix + ".bin_dir";
        String v = withValue(k);
        if (v != null)
            return new File(v);

        k = newPrefix + ".bin_dir";
        v = withValue(k);

        if (v == null)
            throw new ApplicationException("Binary directory was not initialized");

        return new File(v);
    }

}
