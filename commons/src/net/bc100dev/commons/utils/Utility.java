package net.bc100dev.commons.utils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Random;

public class Utility {

    public static File getRuntimeImage() {
        try {
            return new File(Utility.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException ignore) {
            return null;
        }
    }

    public static File getRuntimeDir() {
        try {
            File image = new File(Utility.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return new File(image.getAbsolutePath().substring(0, Utility.getLastPathSeparator(image.getAbsolutePath(), false)));
        } catch (URISyntaxException ignore) {
            return null;
        }
    }

    public static boolean convertToBoolean(String value, boolean defValue) {
        if (value == null || value.trim().isEmpty())
            return false;

        if (value.equalsIgnoreCase("Y") || value.equalsIgnoreCase("Yes") ||
                value.equalsIgnoreCase("1") || value.equalsIgnoreCase("true") ||
                value.equalsIgnoreCase("enabled") || value.equalsIgnoreCase("active"))
            return true;

        return defValue;
    }

    public static String getLineSeparator(String contents) {
        int[] counts = getLineSeparatorCounts(contents);

        if (counts[0] == counts[1])
            return "\r\n";
        else if (counts[1] >= 1 && counts[0] == 0)
            return "\n";
        else if (counts[0] >= 1 && counts[1] == 0)
            return "\r";
        else
            return System.lineSeparator();
    }

    public static int[] getLineSeparatorCounts(String contents) {
        char[] chars = contents.toCharArray();

        int[] i = new int[2];

        for (char c : chars) {
            if (c == '\r')
                i[0]++;

            if (c == '\n')
                i[1]++;
        }

        return i;
    }

    public static int getLastPathSeparator(String path, boolean toEnd) {
        if (path == null)
            return 0;

        if (path.isEmpty())
            return 0;

        if (!(path.contains("\\") || path.contains("/")))
            return 0;

        int lastSep;
        if (path.contains("\\") && path.contains("/")) {
            path = path.replaceAll("\\\\", "/");
            lastSep = path.lastIndexOf('/');
        } else if (path.contains("\\"))
            lastSep = path.lastIndexOf('\\');
        else
            lastSep = path.lastIndexOf('/');

        lastSep++;

        if (toEnd) {
            String sub = path.substring(lastSep);
            if (!sub.isEmpty())
                lastSep = path.length();
        }

        return lastSep;
    }

    public static String throwableToString(Throwable th) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        th.printStackTrace(pw);

        return sw.toString();
    }

    public static long getRandomLong(long min, long max) {
        return new Random().nextLong() % (max - min + 1) + min;
    }

    public static double getRandomDouble(double min, double max) {
        return new Random().nextDouble() * (max - min) + min;
    }

    public static float getRandomFloat(float min, float max) {
        return new Random().nextFloat() * (max - min) + min;
    }

    public static int getRandomInteger(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

}
