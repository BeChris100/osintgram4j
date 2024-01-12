package net.bc100dev.osintgram4j;

import net.bc100dev.commons.ApplicationRuntimeException;
import net.bc100dev.commons.utils.Utility;

import java.io.File;

import static net.bc100dev.commons.utils.RuntimeEnvironment.isMac;

public class NativeLoader {

    private static boolean loaded = false;

    public static boolean hasLibrary() {
        String[] paths = System.getProperty("java.library.path").split(File.pathSeparator);
        for (String path : paths) {
            File libFile = new File(path, System.mapLibraryName("osintgram4j-cxx"));

            if (libFile.exists())
                return true;
        }

        return false;
    }

    public static boolean isLoaded() {
        if (isMac())
            return false;

        return loaded;
    }

    public static void load() {
        if (!hasLibrary()) {
            if (isMac()) {
                int ran = Utility.getRandomInteger(1, 12000);
                if (ran == 2502)
                    throw new ApplicationRuntimeException("I don't have a MacBook, so no native libraries for macOS");

                throw new ApplicationRuntimeException("Native libraries are not available for macOS. Sorry!");
            }
        }

        System.loadLibrary("osintgram4j-cxx");
        loaded = true;
    }

}
