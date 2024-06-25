package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.Utility;
import net.bc100dev.commons.utils.io.FileUtil;
import net.bc100dev.osintgram4j.Settings;
import osintgram4j.api.sh.Command;
import osintgram4j.api.sh.ShellEnvironment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CacheCmd extends Command {

    private static void clearCache() throws IOException {
        File relDir = new File(Settings.storeLocation().getAbsolutePath() + "/cache");

        if (!relDir.exists())
            return;

        if (!relDir.isDirectory())
            throw new IOException("\"" + relDir.getAbsolutePath() + "\" is not a directory");

        // For your understanding: dir, nameSort, removePaths
        List<String> contents = FileUtil.listDirectory(relDir.getAbsolutePath(), false, false);
        if (!contents.isEmpty()) {
            for (String content : contents)
                FileUtil.delete(content);
        }
    }

    private static void invalidateCache() throws IOException {
        File relDir = new File(Settings.storeLocation().getAbsolutePath() + "/cache");

        if (!relDir.exists())
            return;

        if (!relDir.isDirectory())
            throw new IOException("\"" + relDir.getAbsolutePath() + "\" is not a directory");

        List<String> contents = FileUtil.listDirectory(relDir.getAbsolutePath(), false, false);
        if (contents.isEmpty())
            return;

        List<File> files = new ArrayList<>();
        for (String content : contents)
            files.add(new File(content));

        for (File file : files) {
            Path path = Paths.get(file.getAbsolutePath());
            FileTime fTime = Files.getLastModifiedTime(path);

            LocalDate modified = fTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate today = LocalDate.now();

            long daysBetween = ChronoUnit.DAYS.between(modified, today);
            if (daysBetween >= 3)
                FileUtil.delete(file.getAbsolutePath());
        }
    }

    @Override
    public int launchCmd(String[] args, List<ShellEnvironment> ignore) {
        if (args == null) {
            Terminal.println(Terminal.TermColor.BLUE, helpCmd(new String[0]), true);
            return 0;
        }

        if (args.length == 0) {
            Terminal.println(Terminal.TermColor.BLUE, helpCmd(new String[0]), true);
            return 0;
        }

        switch (args[0]) {
            case "-i", "--invalidate" -> {
                try {
                    invalidateCache();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case "-w", "-c", "--wipe", "--clear" -> {
                try {
                    clearCache();
                    System.out.println("Cache cleared!");
                } catch (IOException ex) {
                    Terminal.errPrintln(Terminal.TermColor.RED, "Something went wrong with wiping cache:", false);
                    Terminal.errPrintln(Terminal.TermColor.RED, ex.getMessage(), false);
                    System.err.println();
                    Terminal.errPrintln(Terminal.TermColor.RED, "Stacktrace:", false);
                    Terminal.errPrintln(Terminal.TermColor.RED, Utility.throwableToString(ex), true);

                    return 1;
                }
            }
            case "-h", "--help" -> {
                Terminal.println(Terminal.TermColor.BLUE, helpCmd(new String[0]), true);
                return 0;
            }
        }

        return 0;
    }

    @Override
    public String helpCmd(String[] args) {
        return """
                Manages the use of Cache, and if necessary, wipes them.
                                
                Parameters:
                -i / --invalidate           Invalidates all Cache files
                -w / -c / --wipe / --clear  Wipes Cache
                                
                Also used with these following alternate commands:
                > CacheControl
                > CacheManager
                > Cache-Control
                > Cache-Manager
                > Cache
                > cache""";
    }

}
