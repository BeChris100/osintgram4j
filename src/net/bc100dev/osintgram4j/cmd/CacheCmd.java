package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.io.FileUtil;
import net.bc100dev.osintgram4j.sh.ShellConfig;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static net.bc100dev.commons.utils.RuntimeEnvironment.*;

public class CacheCmd {

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
        List<String> contents = FileUtil.listDirectory(relDir.getAbsolutePath(), false, false, false);
        if (!contents.isEmpty()) {
            for (String content : contents)
                FileUtil.delete(content);
        }
    }

    // Invoked manually by `Method.invoke`
    public static int launchCmd(String[] args, List<ShellConfig> ignore) {
        if (args == null) {
            Terminal.println(Terminal.Color.YELLOW, helpCmd(), true);
            return 0;
        }

        if (args.length == 0) {
            Terminal.println(Terminal.Color.YELLOW, helpCmd(), true);
            return 0;
        }

        switch (args[0]) {
            case "-w", "--wipe", "--clear" -> {
                try {
                    clearCache();
                    Terminal.println(Terminal.Color.GREEN, "Cache wiped", true);
                } catch (IOException ex) {
                    Terminal.errPrintln(Terminal.Color.RED, "Something went wrong with wiping cache:", false);
                    Terminal.errPrintln(Terminal.Color.RED, ex.getMessage(), false);
                    System.err.println();
                    Terminal.errPrintln(Terminal.Color.RED, "Stacktrace:", false);

                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    ex.printStackTrace(pw);

                    Terminal.errPrintln(Terminal.Color.RED, sw.toString(), true);
                    return 1;
                }
            }
        }

        return 0;
    }

    // Invoked manually by `Method.invoke`
    public static String helpCmd() {
        return """
                Manages the use of Cache, and if necessary, wipes them.
                
                Parameters:
                -w / --wipe / --clear       Wipes Cache""";
    }

}
