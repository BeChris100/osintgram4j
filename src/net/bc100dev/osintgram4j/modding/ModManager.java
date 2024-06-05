package net.bc100dev.osintgram4j.modding;

import net.bc100dev.commons.ApplicationException;
import net.bc100dev.commons.utils.io.FileUtil;
import net.bc100dev.commons.utils.io.UserIO;
import osintgram4j.commons.PackagedApplication;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ModManager {

    private static void overwriteFile(File file) throws IOException {
        File rootDir = PackagedApplication.getApplicationDirectory();
        if (rootDir == null)
            throw new RuntimeException("Root directory not found; application is likely not packaged");

        File configFile = new File(rootDir, "lib/app/osintgram4j.cfg");
    }

    private static void execEditor() throws IOException {
    }

    public static List<AppModification> listMods() throws IOException {
        return null;
    }

    public static void installMod(File file) throws IOException {
        if (!FileUtil.isValidZipFile(file.getAbsolutePath()))
            throw new IOException("Invalid Zip file given: " + file.getAbsolutePath());

        try {
            UserIO.setUid(0);
            UserIO.setGid(0);

            overwriteFile(file);
        } catch (ApplicationException ignore) {
            // binary does not have setuid/setgid permissions
            execEditor();
        }
    }

    public static void removeMod(AppModification mod) throws IOException {
    }

}
