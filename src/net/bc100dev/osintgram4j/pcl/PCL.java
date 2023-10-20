package net.bc100dev.osintgram4j.pcl;

import net.bc100dev.commons.CLIParser;
import net.bc100dev.commons.Terminal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * The PCL (Process Command Line) is an interactive shell, used for
 * interacting with the Instagram Private APIs, along with the interaction
 * of the official and publicly available Instagram Graph API.
 */
public class PCL {

    private final Scanner scIn;

    private final List<PCLConfig> pclConfigList;
    private final List<PCLCaller> pclCallers;

    private String PS1 = "==> ";

    /**
     * Initializes the PCL with the necessary Input and the necessary commands.
     *
     * @throws PCLException May throw an exception during initializing the commands.
     */
    public PCL() throws PCLException {
        this.scIn = new Scanner(System.in);
        this.pclConfigList = new ArrayList<>();

        this.pclCallers = new ArrayList<>();
        pclCallers.add(new PCLCaller("connect", "Connects the User to the Instagram APIs",
                "net.bc100dev.osintgram4j.cmd.Connect"));
        pclCallers.add(new PCLCaller("sh", "Runs an interactive System shell and/or executes Shell commands",
                "net.bc100dev.osintgram4j.cmd.ShellInstance", "shell"));
        pclCallers.add(new PCLCaller("clear", "Clears the Terminal/Console Screen",
                "net.bc100dev.osintgram4j.cmd.ClsScr", "cls", "clrscr"));
        pclCallers.add(new PCLCaller("sessionctl", "Session Control and Manager",
                "net.bc100dev.osintgram4j.cmd.SessionCmd", "sessions", "session"));
    }

    /**
     * Parses and assigns/reads a specific configuration. These configurations can be
     * passed to their perspective methods to read, which can be used to remove most of
     * the arguments for their specific commands, depending on their use case.
     *
     * @param line The line to get a configuration parsed
     */
    private void assignCfg(String line) {
        if (line.contains("=")) {
            String[] opt = line.split("=", 2);
            opt[0] = opt[0].trim().replaceFirst("&", "");
            opt[1] = opt[1].trim();

            if (opt[1].contains("\""))
                opt[1] = opt[1].replaceAll("\"", "");

            if (opt[0].equals("PS1")) {
                PS1 = opt[1];
                return;
            }

            if (pclConfigList.isEmpty()) {
                pclConfigList.add(new PCLConfig(opt[0], opt[1]));
                System.out.printf("Created %s with value \"%s\"\n", opt[0], opt[1]);
                return;
            }

            boolean found = false;
            for (int i = 0; i < pclConfigList.size(); i++) {
                PCLConfig cfg = pclConfigList.get(i);

                if (opt[0].equals(cfg.name)) {
                    cfg.value = opt[1];

                    pclConfigList.set(i, cfg);

                    System.out.printf("Assigned new value as \"%s\" to %s\n", cfg.value, cfg.name);
                    found = true;
                }
            }

            if (!found) {
                pclConfigList.add(new PCLConfig(opt[0], opt[1]));
                System.out.printf("Created %s with value \"%s\"\n", opt[0], opt[1]);
            }
        } else {
            if (pclConfigList.isEmpty()) {
                System.out.println("No items assigned yet");
                return;
            }

            String nm = line.replaceFirst("&", "").trim();

            boolean found = false;
            for (PCLConfig cfg : pclConfigList) {
                if (nm.equals(cfg.name)) {
                    System.out.printf("%s ==> %s\n", cfg.name, cfg.value);
                    found = true;
                }
            }

            if (!found)
                System.out.printf("No keyword labeled %s is assigned\n", nm);
        }
    }

    /**
     * The beauty happens here: The interactive shell.
     */
    private void cmd() {
        System.out.print(PS1);
        String ln = scIn.nextLine().trim();

        if (ln.isEmpty()) {
            cmd();
            return;
        }

        if (ln.startsWith("&")) {
            assignCfg(ln);
            cmd();
            return;
        }

        if (ln.startsWith("help")) {
            StringTokenizer tok = new StringTokenizer(ln, " ");
            Map<String, String> helps = new HashMap<>();

            while (tok.hasMoreTokens()) {
                String tokVal = tok.nextToken();

                if (tokVal.equals("help"))
                    // We do not need any help from the "help" command
                    continue;

                for (PCLCaller caller : pclCallers) {
                    if (caller.getCommand().equals(tokVal)) {
                        try {
                            helps.put(tokVal, caller.retrieveLongHelp());
                        } catch (PCLException ignore) {
                            Terminal.println(Terminal.Colors.RED,
                                    String.format("Unknown command \"%s\"", tokVal), true);
                        }
                    } else {
                        for (String altCommand : caller.getAlternateCommands()) {
                            if (altCommand.equals(tokVal)) {
                                try {
                                    helps.put(tokVal, caller.retrieveLongHelp());
                                } catch (PCLException ignore) {
                                    Terminal.println(Terminal.Colors.RED,
                                            String.format("Unknown command \"%s\"", tokVal), true);
                                }
                            }
                        }
                    }
                }
            }

            if (!helps.keySet().isEmpty()) {
                List<String> cmd = new ArrayList<>(helps.keySet());

                if (cmd.size() == 1)
                    Terminal.println(Terminal.Colors.BLUE, helps.get(cmd.get(0)), true);
                else {
                    for (int i = 0; i < cmd.size(); i++) {
                        Terminal.println(Terminal.Colors.CYAN, cmd.get(i), true);
                        Terminal.println(Terminal.Colors.BLUE, helps.get(cmd.get(i)), true);

                        if (i != cmd.size() - 1)
                            System.out.println();
                    }
                }
            } else {
                for (PCLCaller caller : pclCallers) {
                    Terminal.print(Terminal.Colors.CYAN, caller.getCommand() + "\t", true);
                    Terminal.println(Terminal.Colors.YELLOW, caller.retrieveShortHelp(), true);
                }
            }

            cmd();
            return;
        }

        if (ln.equals("exit") || ln.equals("quit")) {
            scIn.close();
            System.exit(0);
            return;
        }

        String[] lnSplits = CLIParser.translateCmdLine(ln);

        if (lnSplits.length == 0) {
            Terminal.println(Terminal.Colors.RED, String.format("Syntax error with parsing line \"%s\"", ln), true);
            cmd();
            return;
        }

        String exec = lnSplits[0];
        String[] givenArgs = new String[lnSplits.length - 1];

        if (lnSplits.length > 1)
            System.arraycopy(lnSplits, 1, givenArgs, 0, lnSplits.length - 1);

        execCommand(exec, givenArgs);

        cmd();
    }

    /**
     * Executes a command from the parsed line.
     *
     * @param exec The command parameter
     * @param args Given PCL command parameters
     */
    private void execCommand(String exec, String[] args) {
        boolean cmdFound = false;
        for (PCLCaller caller : pclCallers) {
            if (caller.getCommand().equals(exec)) {
                cmdFound = true;

                try {
                    int code = caller.execute(args, pclConfigList);
                    if (code != 0) {
                        Terminal.println(Terminal.Colors.RED,
                                caller.getCommand() + ": exit code " + code, true);
                    }

                    break;
                } catch (PCLException ex) {
                    ex.printStackTrace();
                }
            } else {
                for (String alternate : caller.getAlternateCommands()) {
                    if (alternate.equals(exec)) {
                        cmdFound = true;

                        try {
                            int code = caller.execute(args, pclConfigList);
                            if (code != 0) {
                                Terminal.println(Terminal.Colors.RED,
                                        alternate + ": exit code " + code, true);
                            }
                        } catch (PCLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }

        if (!cmdFound)
            Terminal.println(Terminal.Colors.RED, String.format("%s: Command not found", exec), true);
    }

    /**
     * Launches the interactive Shell
     */
    public void launch() {
        cmd();
    }

}
