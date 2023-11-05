package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import com.instagram.api.ConnectionStateException;
import net.bc100dev.osintgram4j.sh.ShellConfig;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.List;

import static net.bc100dev.commons.utils.RuntimeEnvironment.USER_HOME;

public class SessionCmd {

    private static void mkSessionFile() throws IOException {
        File location = new File(USER_HOME.getAbsolutePath() + "/.config/osintgram4j/sessions.json");
        FileOutputStream fos = new FileOutputStream(location);
        fos.write("[]".getBytes());
        fos.close();
    }

    private static JSONArray getSessions() throws IOException, JSONException {
        File file = new File(USER_HOME.getAbsolutePath() + "/.config/osintgram4j/sessions.json");
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
            throw new JSONException("JSON Array Error: does not start with '['");

        if (!jsonData.endsWith("]"))
            throw new JSONException("JSON Array Error: does not end with ']'");

        return new JSONArray(jsonData);
    }

    private static void addSession(String username) throws IOException, ConnectionStateException {
    }

    // Invoked manually by `Method.invoke`
    public static int launchCmd(String[] args, List<ShellConfig> pclConfigs) {
        Terminal.println(Terminal.Color.RED, "Session Manager is not implemented yet", true);
        return 1;
    }

    // Invoked manually by `Method.invoke`
    public static String helpCmd() {
        return """
                Session Manager for the Instagram Connection Status""";
    }

}
