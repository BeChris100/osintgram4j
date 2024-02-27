package net.bc100dev.osintgram4j;

import net.bc100dev.commons.ApplicationRuntimeException;
import net.bc100dev.commons.utils.RuntimeEnvironment;

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

    private static boolean isEmpty() {
        return props.isEmpty();
    }

    // Now, this is the part, where I truly love Java.
    // Let's use the new keyword added in JDK 21: "when"
    // It takes in a boolean value, when you use a String value.
    // The String must be initialized by the `value` parameter first
    // within the `case` statement.
    private static boolean valueToBoolean(String value, boolean defValue) {
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
        try {
            FileInputStream fis = new FileInputStream("AppSettings.cfg");
            props.load(fis);
            fis.close();
        } catch (IOException ex) {
            throw new ApplicationRuntimeException(ex);
        }
    }

    public static void reloadSettings() {
        try {
            props.clear();

            FileInputStream fis = new FileInputStream("AppSettings.cfg");
            props.load(fis);
            fis.close();
        } catch (IOException ex) {
            throw new ApplicationRuntimeException(ex);
        }
    }

    public static boolean app_adminSecurityWarningEnabled() {
        if (isEmpty())
            throw new ApplicationRuntimeException("Settings are not loaded");

        return valueToBoolean(props.getProperty("App.AdminSecurityWarning"), true);
    }

    public static String app_cacheRefreshInterval() {
        if (isEmpty())
            throw new ApplicationRuntimeException("Settings are not loaded");

        return props.getProperty("App.Cache.RefreshInterval", "2d");
    }

    public static File system_storeLocation() {
        if (isEmpty())
            throw new ApplicationRuntimeException("Settings are not loaded");

        String v = null;
        switch (getOperatingSystem()) {
            case WINDOWS ->
                    v = props.getProperty("System.Windows.DefaultFileLocation", "[home]\\AppData\\Local\\BC100Dev\\osintgram4j");
            case LINUX ->
                    v = props.getProperty("System.Linux.DefaultFileLocation", "[home]/.config/BC100Dev/osintgram4j");
            case MAC_OS ->
                    v = props.getProperty("System.Mac.DefaultFileLocation", "[home]/Library/net.bc100dev/osintgram4j");
            default -> throw new ApplicationRuntimeException("Unsupported operating system");
        }

        if (v.contains("[home]"))
            v = v.replaceAll("\\[home]", USER_HOME.getAbsolutePath());

        if (v.contains("[name]"))
            v = v.replaceAll("\\[name]", USER_NAME);

        if (v.contains("[host]"))
            v = v.replaceAll("\\[host]", getHostName());

        return new File(v);
    }

    public static SystemTime time_getSystemTime() {
        LocalDateTime now = LocalDateTime.now();

        int hour24 = now.getHour();
        int hour12 = hour24 % 12;
        int minutes = now.getMinute();
        int seconds = now.getSecond();

        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();

        if (hour12 == 0)
            hour12 = 12;

        return new SystemTime(hour24, hour12, minutes, seconds, day, month, year);
    }

    public static String time_getDate() {
        SystemTime systemTime = time_getSystemTime();
        String out = dateFormat;

        if (out.contains("[day]"))
            out = out.replaceAll("\\[day]", String.format("%02d", systemTime.day));

        if (out.contains("[month]"))
            out = out.replaceAll("\\[month]", String.format("%02d", systemTime.month));

        if (out.contains("[year]"))
            out = out.replaceAll("\\[year]", String.format("%02d", systemTime.year));

        return out;
    }

    public static String time_getTime() {
        SystemTime systemTime = time_getSystemTime();
        String out = timeFormat;

        if (out.contains("[hour]"))
            out = out.replaceAll("\\[hour]", String.format("%02d", t12Format ? systemTime.hour12 : systemTime.hour24));

        if (out.contains("[minute]"))
            out = out.replaceAll("\\[minute]", String.format("%02d", systemTime.minute));

        if (out.contains("[second]"))
            out = out.replaceAll("\\[second]", String.format("%02d", systemTime.second));

        return out;
    }

    public record SystemTime(int hour24, int hour12, int minute, int second, int day, int month, int year) {
    }

}
