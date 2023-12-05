package net.bc100dev.osintgram4j.cmd;

import com.instagram.api.auth.AuthenticationCallback;
import com.instagram.api.auth.UserAuthentication;
import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.Utility;
import net.bc100dev.osintgram4j.api_conn.AppConnectionStatus;
import net.bc100dev.osintgram4j.sh.Shell;
import net.bc100dev.osintgram4j.sh.ShellConfig;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Connection {

    private static AppConnectionStatus connectionStatus = null;

    private static AuthenticationCallback authCallback;

    private static void connect(String username, String password, String mfa) throws IOException {
    }

    private static int testCmd(String[] args, List<ShellConfig> shellConfigs) {
        Terminal.println(Terminal.TermColor.RED, "not implemented", true);
        return 1;
    }

    // Invoked manually by `Method.invoke`
    public static int launchCmd(String[] args, List<ShellConfig> shellConfigs) {
        if (args == null) {
            Terminal.println(Terminal.TermColor.BLUE, helpCmd(), true);
            return 1;
        }

        if (args.length == 0) {
            Terminal.println(Terminal.TermColor.BLUE, helpCmd(), true);
            return 1;
        }

        switch (args[0]) {
            case "-c", "--connect" -> {
                // Connect to the server
                String[] val = new String[3];
                for (ShellConfig config : shellConfigs) {
                    switch (config.getName().toLowerCase()) {
                        case "username", "name", "uname" -> val[0] = config.getValue();
                        case "password", "pass", "passwd" -> val[1] = config.getValue();
                        case "mfa", "auth_code", "multi",
                                "factor_auth", "factor", "code" -> val[2] = config.getValue();
                    }
                }

                if (val[0] != null && val[1] != null) {
                    try {
                        connect(val[0], val[1], val[2] != null ? val[2] : null);
                    } catch (IOException e) {
                        Terminal.errPrintln(Terminal.TermColor.RED, Utility.throwableToString(e), true);
                        return 1;
                    }
                }
            }
            case "-i", "--state", "--info" -> {
                // Get the information about the current connection
            }
            case "-d", "--disconnect" -> {
                // Disconnects from the server
            }
            case "-aS", "--assign-session" -> {
                // Assigns the current connection to a session
            }
        }

        authCallback = new AuthenticationCallback() {
            @Override
            public void onLogin(String username) {
            }

            @Override
            public void onError(int responseCode, String responseMessage, String body) {
            }

            @Override
            public void onMfaRequire(String mfaCode) {
                Scanner in = Shell.scIn;
                Terminal.print(Terminal.TermColor.YELLOW, "enter your 2FA (Two-Factor Authentication) to continue: ", false);
            }
        };

        return testCmd(args, shellConfigs);
    }

    private enum CmdAction {
        ActionConnect,

        ActionGetState,

        ActionDisconnect,

        ActionSessionAssign
    }

    // Invoked manually by `Method.invoke`
    public static String helpCmd() {
        return """
                Manages the Connection State with the Client (Osintgram) and the Backend (Instagram).
                                
                Parameters:
                connection -e [&username] [&password]  -> Establishes an Instagram Account
                connection -c (mfa)                    -> Connects to the Instagram Backend Server
                connection -i                          -> Gets the current information state
                connection -d                          -> Disconnects from the Instagram Server
                                
                Also used with these following alternate commands:
                > Manage-Connection
                > ConnectionManager""";
    }
}
