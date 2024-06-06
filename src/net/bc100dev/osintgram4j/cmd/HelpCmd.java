package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.HelpPage;
import osintgram4j.api.sh.*;
import osintgram4j.commons.ShellConfig;

import java.util.*;

import static net.bc100dev.commons.Terminal.TermColor.*;
import static osintgram4j.commons.AppConstants.log;
import static osintgram4j.commons.Titles.TITLE_BLOCK;

public class HelpCmd extends Command {

    private boolean showAlias = false;
    private boolean showAlt = false;

    private int parseCommands(String[] cmdList) {
        Map<String, String> helps = new HashMap<>();
        Shell instance = Shell.getInstance();

        for (String tokVal : cmdList) {
            for (ShellCaller caller : instance.shellCallers) {
                if (caller.getCommand().equals(tokVal)) {
                    try {
                        if (helps.containsKey(tokVal))
                            continue;

                        helps.put(tokVal, caller.retrieveLongHelp(new String[0]));
                    } catch (ShellException ignore) {
                        log.warning("Command not found: " + tokVal);
                        Terminal.println(Terminal.TermColor.RED,
                                String.format("Unknown command \"%s\"", tokVal), true);
                        return 1;
                    }
                } else {
                    for (String altCommand : caller.getAlternateCommands()) {
                        if (altCommand.equals(tokVal)) {
                            try {
                                if (helps.containsKey(caller.getCommand()))
                                    continue;

                                helps.put(caller.getCommand(), caller.retrieveLongHelp(new String[0]));
                            } catch (ShellException ignore) {
                                log.warning("Command not found: " + tokVal);
                                Terminal.println(Terminal.TermColor.RED,
                                        String.format("Unknown command \"%s\"", tokVal), true);
                                return 1;
                            }
                        }
                    }
                }
            }
        }

        if (!helps.keySet().isEmpty()) {
            List<String> cmd = new ArrayList<>(helps.keySet());

            if (cmd.size() == 1)
                Terminal.println(Terminal.TermColor.BLUE, helps.get(cmd.getFirst()), true);
            else {
                for (int i = 0; i < cmd.size(); i++) {
                    Terminal.println(CYAN, cmd.get(i), true);
                    Terminal.println(Terminal.TermColor.BLUE, helps.get(cmd.get(i)), true);

                    if (i != cmd.size() - 1)
                        System.out.println();
                }
            }
        } else {
            Terminal.println(Terminal.TermColor.GREEN, TITLE_BLOCK(), true);
            System.out.println();

            int maxCmdLength = 0;

            for (ShellCaller caller : instance.shellCallers) {
                String cmd = caller.getCommand();
                if (cmd.length() > maxCmdLength)
                    maxCmdLength = cmd.length();
            }

            maxCmdLength += 5;

            for (ShellCaller caller : instance.shellCallers) {
                String cmd = caller.getCommand();
                int spaces = maxCmdLength - cmd.length();

                Terminal.print(CYAN, cmd + " ".repeat(spaces), true);
                Terminal.println(Terminal.TermColor.YELLOW, caller.retrieveShortHelp(), true);
            }
        }

        if (showAlias) {
            System.out.println();
            System.out.println("Aliases:");

            for (ShellAlias alias : instance.shellAliases) {
                String cmd = alias.getAliasCmd();
                ShellCaller call = alias.getCaller();

                Terminal.print(CYAN, cmd, true);
                System.out.print(" → ");
                Terminal.print(YELLOW, call.getCommand(), true);
                System.out.print(" ");

                StringBuilder str = new StringBuilder();
                for (int i = 0; i < alias.getExecutionArgs().length; i++) {
                    str.append(alias.getExecutionArgs()[i]);

                    if (i != alias.getExecutionArgs().length - 1)
                        str.append(" ");
                }
                Terminal.println(BLUE, str.toString(), true);
            }

            showAlias = false;
        }

        if (showAlt) {
            System.out.println();
            System.out.println("Alternates:");

            for (ShellCaller caller : instance.shellCallers) {
                for (String alternate : caller.getAlternateCommands()) {
                    Terminal.print(CYAN, alternate, true);
                    System.out.print(" → ");
                    Terminal.println(YELLOW, caller.getCommand(), true);
                }
            }

            showAlt = false;
        }

        return 0;
    }

    @Override
    public int launchCmd(String[] args, List<ShellConfig> env) {
        List<String> cmdList = new ArrayList<>();

        for (String arg : args) {
            if (arg.startsWith("-")) {
                switch (arg) {
                    case "-a" -> showAlias = true;
                    case "-e" -> showAlt = true;
                    case "-h", "--help" -> {
                        System.out.println("Display help information for each command");

                        HelpPage pg = new HelpPage();
                        pg.setStartSpaceWidth(5);
                        pg.setSpaceWidth(3);
                        pg.addArg("-a", null, "Show all commands, including aliases");
                        pg.addArg("-e", null, "Shows only aliases");
                        pg.addArg("-t", null, "Shows only alternate commands, in PowerShell Syntax alike");
                        pg.display(System.out);

                        return 0;
                    }
                    default -> {
                        Terminal.errPrintln(RED, "invalid option: " + arg, true);
                        return 1;
                    }
                }
            }

            cmdList.add(arg);
        }

        String[] cmdArr = new String[cmdList.size()];
        for (int i = 0; i < cmdList.size(); i++)
            cmdArr[i] = cmdList.get(i);

        return parseCommands(cmdArr);
    }

    @Override
    public String helpCmd(String[] args) {
        return "A command that displays help information about specific commands";
    }
}
