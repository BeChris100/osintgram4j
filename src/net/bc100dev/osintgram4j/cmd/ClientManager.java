package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.HelpPage;
import osintgram4j.api.sh.Command;
import osintgram4j.api.sh.ShellEnvironment;

import java.util.List;

public class ClientManager extends Command {

    @Override
    public int launchCmd(String[] args, List<ShellEnvironment> env) {
        if (args == null || args.length == 0) {
            Terminal.errPrintln(Terminal.TermColor.YELLOW, helpCmd(args), true);
            System.err.println();

            try {
                Thread.sleep(50);
            } catch (InterruptedException ignore) {
            }

            Terminal.errPrintln(Terminal.TermColor.RED, "clientmgr: no tool provided", true);
            return 1;
        }

        switch (args[0]) {
            case String s
                    when s.equalsIgnoreCase("help") ||
                    s.equalsIgnoreCase("--help") ||
                    s.equalsIgnoreCase("-help") ||
                    s.equals("?") -> System.out.println(helpCmd(args));
            case String s
                    when s.equalsIgnoreCase("mods") || s.equalsIgnoreCase("md") -> {
                System.out.println("Not implemented yet");
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
        tools.addArg("settings", null, "Manages the Application Settings");

        return "Application Management Tool for Osintgram4j\n\nAvailable Tools:\n" + tools.display() +
                "\nWe, the developers, are not responsible for any damages caused by inserting malicious software.\n" +
                "Proceed with caution.";
    }
}
