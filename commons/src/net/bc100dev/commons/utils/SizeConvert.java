package net.bc100dev.commons.utils;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * I wrote this class, with a few methods that I found online. Since I cannot remember,
 * where exactly I found this class, I thought that at least this would be good enough.
 */
public class SizeConvert {

    public static String byteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000)
            return bytes + " B";

        CharacterIterator ci = new StringCharacterIterator("kMGTPE");

        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }

        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public static String byteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);

        if (absB < 1024)
            return bytes + " B";

        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");

        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }

        value *= Long.signum(bytes);
        return String.format("%.1f %cB", value / 1024.0, ci.current());
    }

}
