package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import net.bc100dev.osintgram4j.api_conn.AppConnectionStatus;
import net.bc100dev.osintgram4j.pcl.PCLConfig;

import java.util.List;

public class Connection {

    private static AppConnectionStatus connectionStatus = null;

    // Invoked manually by `Method.invoke`
    public static int launchCmd(String[] args, List<PCLConfig> pclConfigs) {
        if (args == null) {
            Terminal.println(Terminal.Color.BLUE, helpCmd(args), true);
            return 1;
        }

        if (args.length == 0) {
            Terminal.println(Terminal.Color.BLUE, helpCmd(args), true);
            return 1;
        }

        switch (args[0]) {
            case "-e", "--establish" -> {
            }
            case "-c", "--connect" -> {
            }
            case "-i", "--state", "--info" -> {
            }
            case "-D", "--disconnect" -> {
            }
        }

        return 0;
    }

    // Invoked manually by `Method.invoke`
    public static String helpCmd(String[] args) {
        return """
                Manages the Connection State with the Client (Osintgram) and the Backend (Instagram).
                
                Parameters:
                connection -e [&username] [&password]  -> Establishes an Instagram Account
                connection -c (mfa)                    -> Connects to the Instagram Backend Server
                connection -i                          -> Gets the current information state
                connection -D                          -> Disconnects from the Instagram Server""";
    }
}
