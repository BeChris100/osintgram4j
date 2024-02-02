package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.ApplicationException;
import net.bc100dev.commons.Terminal;
import com.instagram.api.ConnectionStateException;
import net.bc100dev.commons.utils.io.FileUtil;
import osintgram4j.api.Command;
import osintgram4j.api.sh.ShellConfig;
import org.json.JSONArray;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.List;

import static net.bc100dev.commons.utils.RuntimeEnvironment.*;

public class SessionCmd extends Command {

    private static File getSessionLocationFile() {
        return switch (getOperatingSystem()) {
            case WINDOWS -> new File(USER_HOME.getAbsolutePath() + "\\AppData\\Local\\bc100dev\\osintgram4j\\data\\sessions.json");
            case LINUX -> new File(USER_HOME.getAbsolutePath() + "/.config/bc100dev/osintgram4j/data/sessions.json");
            case MAC_OS -> new File(USER_HOME.getAbsolutePath() + "/Library/bc100dev/osintgram4j/data/sessions.json");
        };
    }

    private static void mkSessionFile() throws IOException {
        FileUtil.createFile(getSessionLocationFile().getAbsolutePath(), true);
        FileOutputStream fos = new FileOutputStream(getSessionLocationFile());
        fos.write("[]".getBytes());
        fos.close();
    }

    private static JSONArray getSessions() throws IOException, ApplicationException {
        File file = getSessionLocationFile();
        if (!file.exists()) {
            mkSessionFile();
            return new JSONArray();
        }

        if (!file.canRead())
            throw new AccessDeniedException("Cannot access file at \"" + file.getAbsolutePath() + "\"");

        FileInputStream fis = new FileInputStream(file);
        StringBuilder str = new StringBuilder();
        byte[] buff = new byte[1024];
        int len;

        while ((len = fis.read(buff, 0, 1024)) != -1)
            str.append(new String(buff, 0, len));

        fis.close();

        String jsonData = str.toString().trim();
        if (!jsonData.startsWith("["))
            throw new ApplicationException("JSON Array Error: does not start with '['");

        if (!jsonData.endsWith("]"))
            throw new ApplicationException("JSON Array Error: does not end with ']'");

        return new JSONArray(jsonData);
    }

    private static void addSession(String username) throws IOException, ConnectionStateException {
    }

    private static int testImpl(String[] args, List<ShellConfig> shellConfigs) {
        if (args != null && args.length != 0) {
            switch (args[0]) {
                case "--encrypt", "-e" -> {
                    if (args.length == 1) {
                        Terminal.errPrintln(Terminal.TermColor.YELLOW, "To encrypt a session, run", false);
                        Terminal.errPrintln(Terminal.TermColor.YELLOW, args[0] + " session_id_1 session_id_2 ...", false);
                        Terminal.errPrintln(Terminal.TermColor.YELLOW, "where \"session_id_1\" and other labels are the corresponding Session identifiers", true);
                        return 1;
                    }
                }
                case "--use", "-u" -> {
                    // Use: username|identifier|label
                }
                case "--create", "-c" -> {
                    // Create:
                    /*
                    -u username -> Creates a new Session with the corresponding Instagram Username
                    -l label    -> Creates a new Session with a specific Label
                     */
                    // This will automatically create an identifier.
                }
                case "--list", "-l" -> {
                    // List: [null]
                }
                case "--set", "-s" -> {
                    // Set: key value
                }
            }

            return 0;
        }

        Terminal.errPrintln(Terminal.TermColor.RED,"Requires at least one argument", true);
        return 1;
    }

    @Override
    public int launchCmd(String[] args, List<ShellConfig> shellConfigs) {
        Terminal.println(Terminal.TermColor.RED, "Session Manager is not implemented yet", true);
        return testImpl(args, shellConfigs);
    }

    @Override
    public String helpCmd(String[] args) {
        return """
                Session Manager for the Instagram Connection Status""";
    }

}
