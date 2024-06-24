package net.bc100dev.commons.utils;

import net.bc100dev.commons.utils.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import static net.bc100dev.commons.utils.RuntimeEnvironment.getOperatingSystem;

public class Utility {

    public static byte[] base64EncodeStr(byte[] data, int times) {
        byte[] b64 = data;

        for (int i = 0; i < times; i++)
            b64 = Base64.getEncoder().encode(b64);

        return b64;
    }

    public static byte[] base64DecodeStr(byte[] data, int times) {
        try {
            byte[] b64 = data;

            for (int i = 0; i < times; i++)
                b64 = Base64.getDecoder().decode(b64);

            return b64;
        } catch (IllegalArgumentException ignore) {
            return new byte[0];
        }
    }

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
            return new File(image.getParent());
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

    public static File getBinaryPath(String name) throws IOException {
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null)
            throw new NullPointerException("PATH has been received as null");

        List<String> passedPathEntries = new ArrayList<>();
        List<String> passedPathContents = new ArrayList<>();

        String[] pathArr = pathEnv.split(File.pathSeparator);
        for (String pathEntry : pathArr) {
            if (passedPathEntries.contains(pathEntry))
                continue;

            passedPathEntries.add(pathEntry);

            File fPathEntry = new File(pathEntry);
            if (!fPathEntry.exists())
                continue;

            if (!fPathEntry.isDirectory())
                continue;

            List<String> pathContents = FileUtil.listDirectory(pathEntry, true, false);
            for (String pathContent : pathContents) {
                if (passedPathContents.contains(pathContent))
                    continue;

                passedPathContents.add(pathContent);

                File bin = new File(pathContent);
                if ((bin.exists() && bin.canExecute()) && (getOperatingSystem() == OperatingSystem.WINDOWS ?
                        bin.getName().equalsIgnoreCase(name) : bin.getName().equals(name))) {
                    return bin;
                }
            }
        }

        return null;
    }

}
