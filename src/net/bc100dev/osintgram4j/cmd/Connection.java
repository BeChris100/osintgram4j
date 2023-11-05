package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import com.instagram.api.ConnectionStateException;
import net.bc100dev.osintgram4j.api_conn.AppConnectionStatus;
import net.bc100dev.osintgram4j.sh.ShellConfig;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class Connection {

    private static AppConnectionStatus connectionStatus = null;

    private static String[] usernameAndPassword(List<ShellConfig> pclConfigs) {
        String[] values = new String[2];

        values[0] = null;
        values[1] = null;

        for (ShellConfig config : pclConfigs) {
            switch (config.name) {
                case "username" -> values[0] = config.value;
                case "password" -> values[1] = config.value;
            }
        }

        return values;
    }

    private static String[] usernameAndPassword(String[] args) {
        String[] values = new String[2];
        values[0] = null;
        values[1] = null;

        if (args.length == 3) {
            values[0] = args[1];
            values[1] = args[2];
        }

        return values;
    }

    private static void connect(String username, String password) throws IOException, ConnectionStateException {
    }

    // Invoked manually by `Method.invoke`
    public static int launchCmd(String[] args, List<ShellConfig> pclConfigs) {
        if (args == null) {
            Terminal.println(Terminal.Color.BLUE, helpCmd(), true);
            return 1;
        }

        if (args.length == 0) {
            Terminal.println(Terminal.Color.BLUE, helpCmd(), true);
            return 1;
        }

        switch (args[0]) {
            case "-e", "--establish" -> {
                String[] upValues = usernameAndPassword(pclConfigs);
                if (upValues[0] == null || upValues[1] == null) {
                    Terminal.println(Terminal.Color.YELLOW, "Failed to read username and/or password out of the variables; trying arguments", true);

                    upValues = usernameAndPassword(args);
                    if (upValues[0] == null || upValues[1] == null) {
                        Terminal.errPrintln(Terminal.Color.RED, "Failed to connect: username and/or password not given", true);
                        return 1;
                    }
                }

                try {
                    connect(upValues[0], upValues[1]);
                } catch (Throwable th) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    th.printStackTrace(pw);

                    Terminal.errPrintln(Terminal.Color.RED, sw.toString(), true);
                }
            }
            case "-c", "--connect" -> {
                // Connect to the server
            }
            case "-i", "--state", "--info" -> {
                // Get the information about the current connection
            }
            case "-d", "--disconnect" -> {
                // Disconnects from the server
            }
        }

        return 0;
    }

    // Invoked manually by `Method.invoke`
    public static String helpCmd() {
        return """
                Manages the Connection State with the Client (Osintgram) and the Backend (Instagram).
                
                Parameters:
                connection -e [&username] [&password]  -> Establishes an Instagram Account
                connection -c (mfa)                    -> Connects to the Instagram Backend Server
                connection -i                          -> Gets the current information state
                connection -d                          -> Disconnects from the Instagram Server""";
    }
}
