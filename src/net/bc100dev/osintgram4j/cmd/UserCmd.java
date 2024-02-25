package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.utils.HelpPage;
import osintgram4j.api.Command;
import osintgram4j.commons.ShellConfig;

import java.util.List;

public class UserCmd extends Command {

    @Override
    public int launchCmd(String[] args, List<ShellConfig> shellConfigs) {
        return 0;
    }

    @Override
    public String helpCmd(String[] args) {
        HelpPage page = new HelpPage();
        if (args == null || args.length == 0) {
            page.addArg("mk", null, "Creates a new Account, prompting for Username, Password, E-Mail Address and First Name");
            page.addArg("login", null, "Logins to an existing Instagram account, establishing a session. Might prompt for a 2FA code, if needed.");
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
