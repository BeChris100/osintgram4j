package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.HelpPage;
import osintgram4j.api.Command;
import osintgram4j.commons.ShellConfig;

import java.util.List;
import java.util.Scanner;

public class UserCmd extends Command {

    private static void create(String username, String email, String password) {
        // TODO: Invoke Account Creation
    }

    private static void promptCreate() {
        Scanner in = new Scanner(System.in);
        Terminal.print(Terminal.TermColor.GREEN, "Enter your username: ", false);
        String username = in.nextLine();
    }

    @Override
    public int launchCmd(String[] args, List<ShellConfig> shellConfigs) {
        if (args == null || args.length == 0) {
            Terminal.errPrintln(Terminal.TermColor.YELLOW, helpCmd(new String[0]), true);
            return 1;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> promptCreate();
            case "login" -> {
                if (args.length == 1) {
                    String[] message = {
                            "usage:",
                            "user-manager login [username] (password) (mfa_code)",
                            "user-manager login [email] (password) (mfa_code)",
                            "user-manager login [UserConnectionID]",
                            null,
                            "where:",
                            "[argument] is required",
                            "(argument) is optional",
                            null,
                            "options:",
                            "[UserConnectionID]: The connection ID that Osintgram4j gives for each User Connection.",
                            "\tThey are reusable, but expire after 5 days of inactivity"
                    };

                    for (int i = 0; i < message.length; i++)
                        Terminal.errPrintln(Terminal.TermColor.CYAN, message[i], i != message.length - 1);

                    return 1;
                }
            }
            case "logout" -> {
                if (args.length == 1) {
                    Terminal.errPrintln(Terminal.TermColor.CYAN, "Connection ID is required", true);
                    return 1;
                }
            }
            case "mod" -> {
                if (args.length < 3) {
                    String[] message = {
                            "usage:",
                            "user-manager mod [id] [key] [value]",
                            "user-manager mod --help"
                    };
                }

                for (int i = 1; i < args.length; i++) {
                }
            }
        }

        return 0;
    }

    @Override
    public String helpCmd(String[] args) {
        HelpPage page = new HelpPage();
        if (args == null || args.length == 0) {
            page.addArg("create", null, "Creates a new Account, prompting for Username, Password, E-Mail Address and First Name");
            page.addArg("login", null, "Logins to an existing Instagram account, establishing a connection. Might prompt for a 2FA code, if needed.");
            page.addArg("logout", null, "Logs out from an existing Instagram account session");
            page.addArg("mod", null, "Modifies the Instagram User Account");

            return "User Control and Management\n\nOptions:\n" + page.display();
        }

        switch (args[0]) {
            case "login" -> {
                return """
                        User Control and Management: "login"
                        
                        Logs into an existing Instagram account. This command passes
                        either both the "username" and "password" arguments, or can be defined
                        by the Shell Environment via
                        
                        &UserControl.Authentication.Name
                        &UserControl.Authentication.Pass
                        
                        By logging into an account, either an existing Session will be started,
                        or a new Session will be created for the corresponding user.""";
            }
            case "logout" -> {
                return """
                        User Control and Management: "logout"
                        
                        Logs out of the currently running Session, or can be """;
            }
            default -> {
                return "Unknown Option \"" + args[0] + "\"";
            }
        }
    }

}
