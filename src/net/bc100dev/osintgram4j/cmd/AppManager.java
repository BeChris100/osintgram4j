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

        switch (args[0]) {
            case String s
                    when s.equalsIgnoreCase("mods") -> {
                System.out.println("Not implemented yet");
            }
            case String s
                    when s.equalsIgnoreCase("help") -> {
                System.out.println(helpCmd(args));
            }
            case String s
                    when s.equalsIgnoreCase("settings") || s.equalsIgnoreCase("stg") -> {
                System.out.println("Not implemented yet");
            }
            default -> System.err.println("Unknown tool: " + args[0]);
        }

        return 0;
    }

    @Override
    public String helpCmd(String[] args) {
        HelpPage tools = new HelpPage();
        tools.setSpaceWidth(5);
        tools.addArg("mods", null, "Manages App Modifications");

        return "Application Management Tool for Osintgram4j\n\nAvailable Tools:\n" + tools.display() +
                "\n\nWe, the developers, are not responsible for any damages caused by inserting malicious software.\n" +
                "Proceed with caution.";
    }
}
