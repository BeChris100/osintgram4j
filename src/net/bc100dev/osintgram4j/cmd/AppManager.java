package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.HelpPage;
import osintgram4j.api.Command;
import osintgram4j.commons.ShellConfig;

import java.util.List;

public class AppManager extends Command {

    @Override
    public int launchCmd(String[] args, List<ShellConfig> env) {
        if (args == null || args.length == 0) {
            Terminal.errPrintln(Terminal.TermColor.YELLOW, helpCmd(args), true);
            return 1;
        }

        return 0;
    }

    @Override
    public String helpCmd(String[] args) {
        HelpPage tools = new HelpPage();
        tools.setSpaceWidth(5);
        tools.addArg("mods", null, "Manages App Modifications");

        return """
                Application Management Tool
                
                Available Tools:
                mods     Manages Modi""";
    }
}
