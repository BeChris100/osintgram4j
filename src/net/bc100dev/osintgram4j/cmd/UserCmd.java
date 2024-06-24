package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.HelpPage;
import net.bc100dev.osintgram4j.api_conn.UserSession;
import osintgram4j.api.sh.Command;
import osintgram4j.commons.ShellEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserCmd extends Command {

    private static void create(String username, String email, String password) {
        // TODO: Invoke Account Creation
    }

    private static void logout(UserSession session) {
    }

    private static void promptCreate(List<ShellEnvironment> shellEnv) {
        // TODO:
    }

    @Override
    public int launchCmd(String[] args, List<ShellEnvironment> shellConfigs) {
        if (args == null || args.length == 0) {
            Terminal.errPrintln(Terminal.TermColor.YELLOW, helpCmd(new String[0]), true);
            return 1;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> promptCreate(shellConfigs);
            case "login" -> {
                if (args.length == 1) {
                    String[] message = {
                            "usage:",
                            "user-manager login [username|email] (password) (mfa_code)",
                            "user-manager login [UserConnectionID]",
                            null,
                            "where:",
                            "[argument] is required",
                            "(argument) is optional",
                            null,
                            "options:",
                            "[UserConnectionID]: The connection ID that Osintgram4j gives for each User Connection.",
                            "\tThey are reusable, but expire after 5 days of inactivity by default.",
                            "[username]: The username to login",
                            "[email]: The email address to login",
                            "(password): The optional field, password, to log in",
                            "(mfa_code): On accounts without 2FA enabled, this field can be left empty",
                            null,
                            "Note: Osintgram4j does not store your 2FA codes, as Osintgram4j does not have any access",
                            "to the Instagram Authentication Database, where you can easily log into your account without the use of 2FA.",
                            "Trying to access their database might result in your account being suspended, along with federal agencies",
                            "taking notice."
                    };

                    for (int i = 0; i < message.length; i++) {
                        String msg = message[i];
                        if (msg == null) {
                            Terminal.errPrintln(Terminal.TermColor.CYAN, "", i != message.length - 1);
                            continue;
                        }

                        Terminal.errPrintln(Terminal.TermColor.CYAN, msg, i != message.length - 1);
                    }

                    return 1;
                }
            }
            case "logout" -> {
                if (args.length == 1) {
                    Terminal.errPrintln(Terminal.TermColor.CYAN, "Either '-all', '-current' or '-other' is required", true);
                    Terminal.errPrintln(Terminal.TermColor.CYAN, "Run 'user-manager help login", true);
                    return 1;
                }
            }
            case "help" -> {
                if (args.length > 1) {
                    List<String> lArgs = new ArrayList<>(Arrays.asList(args).subList(1, args.length));
                    String[] sArgs = new String[lArgs.size()];

                    for (int i = 0; i < lArgs.size(); i++)
                        sArgs[i] = lArgs.get(i);

                    Terminal.println(Terminal.TermColor.CYAN, helpCmd(sArgs), true);
                }
            }
            default -> {
                Terminal.errPrintln(Terminal.TermColor.RED, "Unknown Control Command: \"" + args[0] + "\"", true);
                return 1;
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

            return "User Control and Management\n\nOptions:\n" + page.display();
        }

        StringBuilder str = new StringBuilder();
        List<String> givenArgs = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            switch (arg.toLowerCase()) {
                case "login" -> {
                    if (givenArgs.contains("login"))
                        continue;

                    givenArgs.add("login");

                    str.append("""
                        User Control and Management: "login"
                        
                        Logs into an existing Instagram account. This command passes
                        either both the "username" and "password" arguments, or can be defined
                        by the Shell Environment via
                        
                        &UserControl.Authentication.Name
                        &UserControl.Authentication.Pass
                        
                        By logging into an account, either an existing Session will be started that was already created,
                        or a new Session will be created for the corresponding user.""");

                    if (i != args.length - 1)
                        str.append("\n\n\n");
                }
                case "logout" -> {
                    if (givenArgs.contains("logout"))
                        continue;

                    givenArgs.add("logout");

                    str.append("""
                        User Control and Management: "logout"
                        
                        Does either one of the following:
                        - Logs out of all running Sessions (argument "-all")
                        - Logs out of the currently running Session (argument "-current")
                        - Logs out of the other running Sessions (argument "-other")""");

                    if (i != args.length - 1)
                        str.append("\n\n\n");
                }
                case "create" -> {
                    if (givenArgs.contains("create"))
                        continue;

                    givenArgs.add("create");

                    str.append("""
                        User Control and Management: "create"
                        
                        Creates a brand new Instagram account. This command passes
                        both the "username" and "password" arguments, or can be defined
                        by the Shell Environment via
                        
                        &UserControl.Authentication.Name
                        &UserControl.Authentication.Pass
                        
                        If these specific environments do not exist, you will be prompted to
                        enter them. Additionally, you will be either prompted to enter your
                        E-Mail address and/or phone number, or you can define them quickly via
                        
                        &UserControl.Authentication.Email
                        &UserControl.Authentication.PhNum
                        
                        Either an E-Mail address, or a phone number, is required to create a new
                        Instagram account.""");

                    if (i != args.length - 1)
                        str.append("\n\n\n");
                }
                default -> {
                    return "Unknown Option \"" + args[0] + "\"";
                }
            }
        }

        return str.toString();
    }

}
