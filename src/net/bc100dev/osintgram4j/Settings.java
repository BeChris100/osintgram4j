package net.bc100dev.osintgram4j;

import net.bc100dev.commons.ApplicationRuntimeException;
import osintgram4j.commons.PackagedApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;

import static net.bc100dev.commons.utils.RuntimeEnvironment.*;

public class Settings {

    private static final Properties props = new Properties();

    public static String dateFormat = "[day]-[month]-[year]";
    public static String timeFormat = "[hour]:[minute]:[second]";
    public static boolean t12Format = false;

    // Now, this is the part, where I truly love Java.
    // Let's use the new keyword added in JDK 21: "when"
    // It takes in a boolean value, when you use a String value.
    // The String must be initialized by the `value` parameter first
    // within the `case` statement.
    private static boolean valueToBoolean(String value, boolean defValue) {
        if (value == null || value.trim().isEmpty())
            return defValue;

        return switch (value) {
            case String s
                when s.equalsIgnoreCase("true") -> true;
            case String s
                when s.equalsIgnoreCase("false") -> false;
            case String s
                when s.equalsIgnoreCase("1") -> true;
            case String s
                when s.equalsIgnoreCase("0") -> false;
            case String s
                when s.equalsIgnoreCase("yes") -> true;
            case String s
                when s.equalsIgnoreCase("no") -> false;
            case String s
                when s.equalsIgnoreCase("enabled") -> true;
            case String s
                when s.equalsIgnoreCase("disabled") -> false;
            default -> defValue;
        };
    }

    public static void loadSettings() {
        File appDir = PackagedApplication.getApplicationDirectory();
        if (appDir == null)
            throw new ApplicationRuntimeException("APPDIR (\"og4j.location.app_dir\") is not set up");

        File reqConfFile = new File(appDir.getAbsolutePath() + "/AppSettings.cfg");
        if (!reqConfFile.exists() || !reqConfFile.isFile() || !reqConfFile.canRead())
            throw new ApplicationRuntimeException("Default Application Settings file could not be initialized");

        try {
            FileInputStream fis = new FileInputStream(reqConfFile);
            props.load(fis);
            fis.close();
        } catch (IOException ex) {
            throw new ApplicationRuntimeException(ex);
        } catch (IllegalArgumentException ex) {
            throw new ApplicationRuntimeException("Property Load error", ex);
        }

        File userConfig;
        switch (getOperatingSystem()) {
            case WINDOWS -> userConfig = new File(USER_HOME.getAbsolutePath() + "\\AppData\\Local\\net.bc100dev\\osintgram4j\\AppSettings.cfg");
            case LINUX -> userConfig = new File(USER_HOME.getAbsolutePath() + "/.config/net.bc100dev/osintgram4j/AppSettings.cfg");
            case MAC_OS -> userConfig = new File(USER_HOME.getAbsolutePath() + "/Library/net.bc100dev/osintgram4j/AppSettings.cfg");
            default -> throw new ApplicationRuntimeException("Unsupported Operating System");
        }

        if (userConfig.exists()) {
            if (userConfig.canRead() && userConfig.isFile()) {
                try {
                    FileInputStream fis = new FileInputStream(userConfig);
                    props.load(fis);
                    fis.close();
                } catch (IOException ignore) {
                    // Ignore the exception (do not load, if an error occurs)
                } catch (IllegalArgumentException ex) {
                    throw new ApplicationRuntimeException("Property Load error", ex);
                }
            }
        }

        String envValue = System.getenv("OG4J_APPCONF");
        if (envValue != null) {
            String[] envPaths = envValue.split(File.pathSeparator);

            for (String envPath : envPaths) {
                File file = new File(envPath);
                if (!file.exists()) {
                    System.err.println("Could not find \"" + envPath + "\"");
                    continue;
                }

                if (!file.isFile()) {
                    System.err.println("\"" + envPath + "\" is not a file");
                    continue;
                }

                if (!file.canRead()) {
                    System.err.println("Current user cannot access \"" + envPath + "\"");
                    continue;
                }

                try {
                    FileInputStream fis = new FileInputStream(envPath);
                    props.load(fis);
                    fis.close();
                } catch (IOException ex) {
                    throw new ApplicationRuntimeException("Could not load " + envPath, ex);
                } catch (IllegalArgumentException ex) {
                    throw new ApplicationRuntimeException("Property Load error", ex);
                }
            }
        }

        File cwdFile = new File("OG4J_AppSettings.cfg");
        if (cwdFile.exists()) {
            if (cwdFile.isFile() && cwdFile.canRead()) {
                try {
                    FileInputStream fis = new FileInputStream(cwdFile);
                    props.load(fis);
                    fis.close();
                } catch (IOException ignore) {
                    // ignore the errors
                } catch (IllegalArgumentException ex) {
                    throw new ApplicationRuntimeException("Property Load error", ex);
                }
            }
        }
    }

    public static void reloadSettings() {
        props.clear();
        loadSettings();
    }

    public static File storeLocation() {
        return switch (getOperatingSystem()) {
            case LINUX -> new File(USER_HOME.getAbsolutePath() + "/.config/net.bc100dev/osintgram4j");
            case WINDOWS -> new File(USER_HOME.getAbsolutePath() + "\\AppData\\Local\\BC100Dev\\Osintgram4j");
            case MAC_OS -> new File(USER_HOME.getAbsolutePath() + "/Library/net.bc100dev/osintgram4j");
        };
    }

    public static boolean securityWarnings() {
        throwIfEmpty();

        return valueToBoolean(props.getProperty("SecurityWarnings"), true);
    }

    public static String cacheMaxInterval() {
        throwIfEmpty();

        return props.getProperty("CacheMaxInterval", "2d");
    }

    public static boolean alwaysUpdateDevices() {
        throwIfEmpty();

        return valueToBoolean(props.getProperty("DeviceRefresh.AlwaysUpdate", "true"), true);
    }

    public static long deviceUpdateInterval() {
        throwIfEmpty();

        return parseDuration(props.getProperty("DeviceRefresh.UpdateInterval", "3d"));
    }

    public static long connectionTimeout() {
        throwIfEmpty();

        return parseDuration(props.getProperty("Network.ConnectionTimeout"));
    }

    public static long readTimeout() {
        throwIfEmpty();

        return parseDuration(props.getProperty("Network.ReadTimeout"));
    }

    public static int maxNetworkTries() {
        throwIfEmpty();

        try {
            return Integer.parseInt(props.getProperty("Network.MaximumTries"));
        } catch (NumberFormatException ignore) {
            return 5;
        }
    }

    public static int maxNetworkConnections() {
        throwIfEmpty();

        try {
            return Integer.parseInt(props.getProperty("Network.MaximumConnections"));
        } catch (NumberFormatException ignore) {
            return 3;
        }
    }

    public static int igMaxConnections() {
        throwIfEmpty();

        try {
            return Integer.parseInt(props.getProperty("Instagram.Accounts.MaximumConnection"));
        } catch (NumberFormatException ignore) {
            return 1;
        }
    }

    public static int igMaxSessions() {
        throwIfEmpty();

        try {
            return Integer.parseInt(props.getProperty("Instagram.Accounts.MaximumSessions"));
        } catch (NumberFormatException ignore) {
            return 1;
        }
    }

    public static boolean alwaysWriteToFile() {
        throwIfEmpty();

        return valueToBoolean(props.getProperty("Osintgram4j.AlwaysWriteToFile", "true"), true);
    }

    public static String writeFileType() {
        throwIfEmpty();

        return switch (props.getProperty("Osintgram4j.FileType", "txt").toLowerCase()) {
            case "json" -> "json_file";
            case "xml" -> "xml_file";
            default -> "text_file";
        };
    }

    public static File writeFileLocation() {
        throwIfEmpty();

        String v = props.getProperty("Osintgram4j.WriteLocation", "standard");
        return switch (v) {
            case "cwd", "pwd" -> WORKING_DIRECTORY;
            case "standard" -> storeLocation();
            default -> new File(v);
        };
    }

    private static void throwIfEmpty() {
        if (props.isEmpty())
            throw new ApplicationRuntimeException("Settings are not loaded");
    }

    private static long parseDuration(String value) {
        String[] parts = value.split(" ");
        long totalDuration = 0;

        for (String part : parts) {
            long dv = Long.parseLong(part.substring(0, part.length() - (part.endsWith("mt") ? 2 : 1)));

            if (part.endsWith("s")) { // Seconds
                totalDuration += dv * 1000;
            } else if (part.endsWith("mt")) { // Minutes
                totalDuration += dv * 60 * 1000;
            } else if (part.endsWith("h")) { // Hours
                totalDuration += dv * 3600 * 1000;
            } else if (part.endsWith("d")) { // Days
                totalDuration += dv * 86400 * 1000;
            } else if (part.endsWith("m")) { // Months
                totalDuration += dv * 2629746 * 1000;
            } else if (part.endsWith("y")) { // Years
                totalDuration += dv * 31556952 * 1000;
            }
        }

        return totalDuration;
    }

}
