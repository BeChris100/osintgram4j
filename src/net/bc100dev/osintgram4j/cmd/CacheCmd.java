package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.io.FileUtil;
import osintgram4j.api.Command;
import osintgram4j.api.sh.ShellConfig;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static net.bc100dev.commons.utils.RuntimeEnvironment.*;

public class CacheCmd extends Command {

    private static void clearCache() throws IOException {
        File relDir;

        if (isWindows())
            relDir = new File(USER_HOME.getAbsolutePath() + "\\AppData\\Local\\bc100dev\\osintgram4j\\cache");
        else
            relDir = new File(USER_HOME.getAbsolutePath() + "/.cache/bc100dev/osintgram4j");

        if (!relDir.exists())
            return;

        if (!relDir.isDirectory())
            throw new IOException("\"" + relDir.getAbsolutePath() + "\" is not a directory");

        // For your understanding: dir, nameSort, removePaths, ignoreErrors
        List<String> contents = FileUtil.listDirectory(relDir.getAbsolutePath(), false, false);
        if (!contents.isEmpty()) {
            for (String content : contents)
                FileUtil.delete(content);
        }
    }

    @Override
    public int launchCmd(String[] args, List<ShellConfig> ignore) {
        if (args == null) {
            Terminal.println(Terminal.TermColor.BLUE, helpCmd(new String[0]), true);
            return 0;
        }

        if (args.length == 0) {
            Terminal.println(Terminal.TermColor.BLUE, helpCmd(new String[0]), true);
            return 0;
        }

        switch (args[0]) {
            case "-w", "--wipe", "--clear" -> {
                try {
                    clearCache();
                    System.out.println("Cache cleared!");
                } catch (IOException ex) {
                    Terminal.errPrintln(Terminal.TermColor.RED, "Something went wrong with wiping cache:", false);
                    Terminal.errPrintln(Terminal.TermColor.RED, ex.getMessage(), false);
                    System.err.println();
                    Terminal.errPrintln(Terminal.TermColor.RED, "Stacktrace:", false);

                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);

                    Terminal.errPrintln(Terminal.TermColor.RED, sw.toString(), true);
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
                -i / --invalidate           Invalidates all Cache 
                -w / --wipe / --clear       Wipes Cache
                
                Also used with these following alternate commands:
                > CacheControl
                > CacheManager
                > Cache-Control
                > Cache-Manager
                > Cache
                > cache""";
    }

}
