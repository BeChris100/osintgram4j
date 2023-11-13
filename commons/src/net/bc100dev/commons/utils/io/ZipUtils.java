package net.bc100dev.commons.utils.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static void createEmptyZip(File zipLocation) throws IOException {
        try (ZipOutputStream ignore = new ZipOutputStream(new FileOutputStream(zipLocation))) {
            // do nothing
        }
    }

}
