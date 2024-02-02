package net.bc100dev.osintgram4j;

import net.bc100dev.commons.CLIParser;
import net.bc100dev.commons.ResourceManager;
import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.Utility;
import osintgram4j.api.sh.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static net.bc100dev.commons.Terminal.TermColor.*;
import static net.bc100dev.osintgram4j.TitleBlock.TITLE_BLOCK;

/**
 * The Shell class is an interactive shell (as the name says), used for
 * interacting with the Instagram Private APIs.
 * <br>
 * <br>
 * The Shell class provides an interactive Shell Instance for the Application,
 * used for interacting with the built-in Application commands, along with
 * the interaction of
 */
public class Shell {

    private static Shell instance;

    private boolean running = false, scriptWarning = false;

    private final boolean terminal = System.console() != null;
    private final String suppress;

    public static Scanner scIn;

    private static final List<ShellConfig> shellConfigList = new ArrayList<>();
    private static final List<ShellCaller> shellCallers = new ArrayList<>();

    private String PS1 = "==> ";

    // Since I already hate you all, you guys have to reset the color manually via the Script itself. No automatic resets here!
    private Terminal.TermColor termColor = null;

    /**
     * Initializes the PCL with the necessary Input and the necessary commands.
     *
     * @throws ShellException May throw an exception during initializing the commands.
     * @throws IOException    Throws an exception, when cannot read any command entries
     */
    public Shell(String suppress) throws IOException, ShellException {
        if (instance != null)
            throw new ShellException("A Shell has been already initialized. Close the previously initialized Shell first.");

        Shell.instance = this;

        Shell.scIn = new Scanner(System.in);
        this.suppress = suppress;

        addCallersFromResource(Shell.class, "/net/bc100dev/osintgram4j/res/cmd_list_d/app-core.json");
    }

    public static Shell getInstance() {
        return instance;
    }

    public void appendConfig(List<ShellConfig> configList) {
        shellConfigList.addAll(configList);
    }

    /**
     * Reads a JSON File and parses into the necessary commands and other relevant information for the Shell
     *
     * @param jsonFile The JSON file to parse
     * @throws IOException    Will throw on Input Readers
     * @throws ShellException Will throw on classes/methods that are not found
     */
    public static void addCallersFromFile(File jsonFile) throws IOException, ShellException {
        ShellCommandEntry entry = ShellCommandEntry.initialize(jsonFile);
        shellCallers.addAll(entry.getCommands());
    }

    /**
     * Reads a JSON File and parses into the necessary commands and other relevant information for the Shell
     *
     * @param correspondingClass A corresponding class to its Class Path
     * @param resourceFile       The resource file within its Class Path
     * @throws IOException    Will throw on Input Readers
     * @throws ShellException Will throw on classes/methods that are not found
     * @deprecated Cannot be used due to potential Class Path errors.
     * Use {@link Shell#addCallersFromData(String)} instead
     */
    @Deprecated
    public static void addCallersFromResource(Class<?> correspondingClass, String resourceFile) throws IOException, ShellException {
        ResourceManager mgr = new ResourceManager(correspondingClass, false);
        if (mgr.resourceExists(resourceFile))
            throw new ShellException("Resource File at \"" + resourceFile + "\" does not exist");

        InputStream is = mgr.getResourceInputStream(resourceFile);
        byte[] buff = is.readAllBytes();
        is.close();

        ShellCommandEntry e = ShellCommandEntry.initialize(new String(buff));
        shellCallers.addAll(e.getCommands());
    }

    /**
     * Reads a JSON File and parses into the necessary commands and other relevant information for the Shell
     *
     * @param jsonData The JSON Data that is being used for the appending method
     * @throws IOException    Will throw on Input Readers
     * @throws ShellException Will throw on classes/methods that are not found
     */
    public static void addCallersFromData(String jsonData) throws IOException, ShellException {
        ShellCommandEntry e = ShellCommandEntry.initialize(jsonData);
        shellCallers.addAll(e.getCommands());
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

            if (shellConfigList.isEmpty()) {
                shellConfigList.add(new ShellConfig(opt[0], opt[1]));
                return;
            }

            boolean found = false;
            for (int i = 0; i < shellConfigList.size(); i++) {
                ShellConfig cfg = shellConfigList.get(i);

                if (opt[0].equals(cfg.getName())) {
                    cfg.setValue(opt[1]);

                    shellConfigList.set(i, cfg);

                    found = true;
                }
            }

            if (!found)
                shellConfigList.add(new ShellConfig(opt[0], opt[1]));
        } else {
            if (shellConfigList.isEmpty()) {
                System.out.println("No Items assigned");
                return;
            }

            String nm = line.replaceFirst("&", "").trim();

            boolean found = false;
            for (ShellConfig cfg : shellConfigList) {
                if (nm.equals(cfg.getName())) {
                    System.out.printf("%s ==> %s\n", cfg.getName(), cfg.getValue());
                    found = true;
                }
            }

            if (!found)
                System.out.printf("%s is not defined\n", nm);
        }
    }

    private ShellExecution getExecutionLine(String line) {
        String[] lnSplits = CLIParser.translateCmdLine(line);

        if (lnSplits.length == 0)
            return null;

        String exec = lnSplits[0];
        String[] givenArgs = new String[lnSplits.length - 1];

        if (lnSplits.length > 1)
            System.arraycopy(lnSplits, 1, givenArgs, 0, lnSplits.length - 1);

        return new ShellExecution(exec, givenArgs);
    }

    /**
     * The beauty happens here: The interactive application shell.
     */
    private void cmd() {
        while (running) {
            try {
                if (terminal)
                    System.out.print(PS1);

                String ln = scIn.nextLine().trim();

                if (ln.isEmpty())
                    continue;

                if (ln.startsWith("&")) {
                    assignCfg(ln);
                    continue;
                }

                ShellExecution execution = getExecutionLine(ln);
                if (execution == null)
                    continue;

                String exec = execution.exec();
                String[] givenArgs = execution.args();

                switch (exec) {
                    case "help", "app-help", "?" -> {
                        StringTokenizer tok = new StringTokenizer(ln, " ");
                        Map<String, String> helps = new HashMap<>();

                        while (tok.hasMoreTokens()) {
                            String tokVal = tok.nextToken();

                            if (tokVal.equalsIgnoreCase("help"))
                                // We do not need any help from the "help" command
                                continue;

                            for (ShellCaller caller : shellCallers) {
                                if (caller.getCommand().equals(tokVal)) {
                                    try {
                                        if (helps.containsKey(tokVal))
                                            continue;

                                        helps.put(tokVal, caller.retrieveLongHelp());
                                    } catch (ShellException ignore) {
                                        Terminal.println(Terminal.TermColor.RED,
                                                String.format("Unknown command \"%s\"", tokVal), true);
                                    }
                                } else {
                                    for (String altCommand : caller.getAlternateCommands()) {
                                        if (altCommand.equals(tokVal)) {
                                            try {
                                                if (helps.containsKey(caller.getCommand()))
                                                    continue;

                                                helps.put(caller.getCommand(), caller.retrieveLongHelp());
                                            } catch (ShellException ignore) {
                                                Terminal.println(Terminal.TermColor.RED,
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

                            for (ShellCaller caller : shellCallers) {
                                String cmd = caller.getCommand();
                                if (cmd.length() > maxCmdLength)
                                    maxCmdLength = cmd.length();
                            }

                            maxCmdLength += 5;

                            for (ShellCaller caller : shellCallers) {
                                String cmd = caller.getCommand();
                                int spaces = maxCmdLength - cmd.length();

                                Terminal.print(CYAN, cmd + " ".repeat(spaces), true);
                                Terminal.println(Terminal.TermColor.YELLOW, caller.retrieveShortHelp(), true);
                            }
                        }
                    }
                    case "echo", "print" -> {
                        if (!suppress.contains("scripts")) {
                            if (!scriptWarning) {
                                Terminal.errPrintln(YELLOW, "Some commands are meant for Script-use only.", false);
                                Terminal.errPrintln(YELLOW, "See https://github.com/BeChris100/osintgram4j/wiki/Scripting-Guide", false);
                                Terminal.errPrintln(YELLOW, "To disable this warning for one Application Session, pass '-Sscript-uses'.", true);
                                scriptWarning = true;
                            }
                        }

                        print_ln(givenArgs);
                    }
                    case "exit", "quit", "close" -> stopShell();
                    default -> execCommand(shellConfigList, exec, givenArgs);
                }
            } catch (NoSuchElementException ignore) {
                // This exception can be ignored: can occur, when a pipe is being used in the Shell Execution Code
                // For example: 'echo help | osintgram4j'
                stopShell();
            }
        }

        scIn.close();
        System.exit(0);
    }

    private void print_ln(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            boolean lastArg = i != args.length - 1;

            if (!arg.contains("%"))
                Terminal.print(termColor, arg + (lastArg ? " " : ""), false);

            int colorIndex;

            if (arg.contains("%c_")) {
                colorIndex = arg.indexOf("%c_") + "%c_".length();
                int lastPos = arg.indexOf('%', colorIndex);

                String sb = arg.substring(colorIndex, lastPos).toLowerCase();

                switch (sb) {
                    case "reset" -> {
                        Terminal.print(RESET, null, false);
                        termColor = RESET;
                    }
                    case "black" -> {
                        Terminal.print(BLACK, null, false);
                        termColor = BLACK;
                    }
                    case "red" -> {
                        Terminal.print(RED, null, false);
                        termColor = RED;
                    }
                    case "green" -> {
                        Terminal.print(GREEN, null, false);
                        termColor = GREEN;
                    }
                    case "yellow" -> {
                        Terminal.print(YELLOW, null, false);
                        termColor = YELLOW;
                    }
                    case "blue" -> {
                        Terminal.print(BLUE, null, false);
                        termColor = BLUE;
                    }
                    case "purple" -> {
                        Terminal.print(PURPLE, null, false);
                        termColor = PURPLE;
                    }
                    case "cyan" -> {
                        Terminal.print(CYAN, null, false);
                        termColor = CYAN;
                    }
                    case "white" -> {
                        Terminal.print(WHITE, null, false);
                        termColor = WHITE;
                    }
                    default -> {
                        Terminal.print(null, null, false);
                        termColor = null;
                    }
                }

                String text = arg.substring(lastPos + 1);
                if (text.isEmpty())
                    continue;

                Terminal.print(termColor, text + (lastArg ? " " : ""), false);
            }
        }

        Terminal.println(termColor, "", true);
    }

    /**
     * Executes a command from the parsed line.
     *
     * @param env  The environment parameter
     * @param exec The command parameter
     * @param args Given PCL command parameters
     */
    private void execCommand(List<ShellConfig> env, String exec, String[] args) {
        boolean cmdFound = false;
        for (ShellCaller caller : shellCallers) {
            if (caller.getCommand().equalsIgnoreCase(exec)) {
                cmdFound = true;

                try {
                    int code = caller.execute(args, env);
                    if (code != 0) {
                        Terminal.println(Terminal.TermColor.RED,
                                caller.getCommand() + ": exit code " + code, true);
                    }

                    break;
                } catch (ShellException ex) {
                    Terminal.println(Terminal.TermColor.RED, Utility.throwableToString(ex), true);
                }
            } else {
                for (String alternate : caller.getAlternateCommands()) {
                    if (alternate.equals(exec)) {
                        cmdFound = true;

                        try {
                            int code = caller.execute(args, env);
                            if (code != 0) {
                                Terminal.println(Terminal.TermColor.RED,
                                        alternate + ": exit code " + code, true);
                            }
                        } catch (ShellException ex) {
                            Terminal.println(Terminal.TermColor.RED, Utility.throwableToString(ex), true);
                        }
                    }
                }
            }
        }

        if (!cmdFound)
            Terminal.println(Terminal.TermColor.RED, String.format("%s: command not found", exec), true);
    }

    /**
     * Launches the interactive Shell
     */
    public void launch() {
        running = true;
        cmd();
    }

    /**
     * Stops the Application Shell
     */
    public void stopShell() {
        running = false;
        Shell.instance = null;
    }

    public void runScript(ShellFile shellFile) throws IOException, ShellException {
        boolean shownHelpWarning = false;

        addCallersFromResource(Shell.class, "/net/bc100dev/osintgram4j/res/cmd_list_d/defaults.json");

        for (String inst : shellFile.getInstructions()) {
            ShellExecution execution = getExecutionLine(inst);
            if (execution == null)
                continue;

            String exec = execution.exec();
            String[] args = execution.args();

            switch (exec) {
                case "help", "app-help", "?" -> {
                    if (!shownHelpWarning) {
                        Terminal.errPrintln(Terminal.TermColor.YELLOW, "The use of the `help` command during scripts are not shown.", true);
                        shownHelpWarning = true;
                    }
                }
                case "exit", "quit", "close" -> {
                    stopShell();
                    System.exit(0);
                }
                case "echo", "print" -> print_ln(args);
                default -> execCommand(shellFile.getEnvironment(), exec, args);
            }
        }
    }

    private record ShellExecution(String exec, String[] args) {
    }

}
