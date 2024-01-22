package net.bc100dev.osintgram4j.settings;

import net.bc100dev.commons.ApplicationException;
import net.bc100dev.commons.utils.Utility;

import java.time.LocalDateTime;
import java.util.Properties;

public class TimeFormats {

    public static String dateFormat = "[day]-[month]-[year]";
    public static String timeFormat = "[hour]:[minute]:[second]";
    public static boolean t12Format = false;

    public static void loadSettings(Properties props) throws ApplicationException {
        if (props == null)
            throw new NullPointerException("Properties Pointer is null");

        if (!props.containsKey("Time.DateFormat"))
            throw new ApplicationException("\"Time.DateFormat\" parameter in the settings is missing");

        if (!props.containsKey("Time.TimeFormat"))
            throw new ApplicationException("\"Time.TimeFormat\" parameter in the settings is missing");

        if (!props.containsKey("Time.T12Format"))
            throw new ApplicationException("\"Time.T12Format\" parameter in the settings is missing");

        t12Format = Utility.convertToBoolean(props.getProperty("Time.T12Format"), false);
        dateFormat = props.getProperty("Time.DateFormat");
        timeFormat = props.getProperty("Time.TimeFormat");
    }

    private static SystemTime getSystemTime() {
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

    public static String getDate() {
        SystemTime systemTime = getSystemTime();
        String out = dateFormat;

        if (out.contains("[day]"))
            out = out.replaceAll("\\[day]", String.format("%02d", systemTime.day));

        if (out.contains("[month]"))
            out = out.replaceAll("\\[month]", String.format("%02d", systemTime.month));

        if (out.contains("[year]"))
            out = out.replaceAll("\\[year]", String.format("%02d", systemTime.year));

        return out;
    }

    public static String getTime() {
        SystemTime systemTime = getSystemTime();
        String out = timeFormat;

        if (out.contains("[hour]"))
            out = out.replaceAll("\\[hour]", String.format("%02d", t12Format ? systemTime.hour12 : systemTime.hour24));

        if (out.contains("[minute]"))
            out = out.replaceAll("\\[minute]", String.format("%02d", systemTime.minute));

        if (out.contains("[second]"))
            out = out.replaceAll("\\[second]", String.format("%02d", systemTime.second));

        return out;
    }

    private record SystemTime(int hour24, int hour12, int minute, int second, int day, int month, int year) {
    }

}
