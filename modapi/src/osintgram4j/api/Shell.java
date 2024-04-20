package osintgram4j.api;

import net.bc100dev.commons.*;
import net.bc100dev.commons.utils.Utility;
import net.bc100dev.commons.utils.io.UserIO;
import osintgram4j.commons.ShellConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static net.bc100dev.commons.Terminal.TermColor.CYAN;
import static net.bc100dev.commons.utils.RuntimeEnvironment.*;
import static osintgram4j.commons.AppConstants.log;
import static osintgram4j.commons.TitleBlock.TITLE_BLOCK;

/**
 * The Shell class is an interactive shell (as the name says), used for
 * interacting with the Instagram Private APIs.
 * <br>
 * <br>
 * The Shell class provides an interactive Shell Instance for the Application,
 * used for interacting with the built-in Application commands, along with
 * the interaction of the original Osintgram commands.
 */
public class Shell {

    private static Shell instance;

    private boolean running = false, scriptWarning = false;

    private final boolean terminal = System.console() != null;
    private final String suppress;

    public Scanner kbi; // kbi, as short, is named after "KeyBoard Input"

    public final List<ShellConfig> shellConfigList = new ArrayList<>();
    private final List<ShellCaller> shellCallers = new ArrayList<>();

    private String PS1;

    // Since I already hate you all, you guys have to reset the color manually via the Script itself. No automatic resets here!
    private Terminal.TermColor termColor = null;

    /**
     * Initializes the Shell with the necessary Input and the necessary commands.
     *
     * @throws ShellException May throw an exception during initializing the commands.
     * @throws IOException    Throws an exception, when cannot read any command entries
     */
    public Shell(String suppress) throws IOException, ShellException {
        if (instance != null)
            throw new ShellException("A Shell has been already initialized. Close the previously initialized Shell first.");

        this.kbi = new Scanner(System.in);
        this.suppress = suppress;

        try {
            PS1 = String.format("[%s/%s: %s]%s ", USER_NAME, getHostName(), WORKING_DIRECTORY.getName(), UserIO.nIsAdmin() ? "#" : "$");
        } catch (ApplicationException ex) {
            ex.printStackTrace(System.err);
            PS1 = String.format("[%s/%s: %s] >> ", USER_NAME, getHostName(), WORKING_DIRECTORY.getName());
        }

        // In favor of `addCallersFromResource` deprecation:
        try {
            InputStream is = new ResourceManager(Shell.class, false).getResourceInputStream("/net/bc100dev/osintgram4j/res/cmd_list_d/app-core.json");
            byte[] buff = is.readAllBytes();
            is.close();

            addCommands(new String(buff));
        } catch (IOException | ShellException ex) {
            throw new ApplicationRuntimeException(ex);
        }

        // If everything goes well, this will be successfully initialized
        Shell.instance = this;
    }

    public static Shell getInstance() {
        return instance;
    }

    public void appendConfig(List<ShellConfig> configList) {
        shellConfigList.addAll(configList);
        log.info("added " + configList.size() + " variables");
    }

    /**
     * Reads a JSON File and parses into the necessary commands and other relevant information for the Shell
     *
     * @param jsonFile The JSON file to parse
     * @throws IOException    Will throw on Input Readers
     * @throws ShellException Will throw on classes/methods that are not found
     */
    public void addCommands(File jsonFile) throws IOException, ShellException {
        ShellCommandEntry entry = ShellCommandEntry.initialize(jsonFile);
        shellCallers.addAll(entry.getCommands());

        log.info("added " + entry.getCommands().size() + " commands");
    }

    /**
     * Reads a JSON File and parses into the necessary commands and other relevant information for the Shell
     *
     * @param jsonData The JSON Data that is being used for the appending method
     * @throws ShellException Will throw on classes/methods that are not found
     */
    public void addCommands(String jsonData) throws ShellException {
        ShellCommandEntry e = ShellCommandEntry.initialize(jsonData);
        shellCallers.addAll(e.getCommands());

        log.info("added " + e.getCommands().size() + " commands");
    }

    /**
     * Reads a Manifest JAR file and uses the "Osintgram4j-API-Commands" attribute to
     *
     * @param file The JAR file itself
     * @throws IOException    Input Reading error
     * @throws ShellException May throw, if Manifest does not exist, did not include the attribute, or went with parsing errors
     */
    public void addCommandsFromJar(File file) throws IOException, ShellException {
        if (file == null)
            throw new NullPointerException("The File instance for the method is pointed as null");

        JarFile jarFile = new JarFile(file);

        Manifest manifest = jarFile.getManifest();
        if (manifest == null) {
            log.warning("Manifest file for file \"" + file.getPath() + "\" was not found");
            return;
        }

        Attributes attributes = manifest.getMainAttributes();
        String outJarValue = attributes.getValue("Osintgram4j-API-ExposedCommands");
        if (outJarValue != null) {
            String[] paths = Tools.translateCmdLine(outJarValue);

            for (String path : paths) {
                File f = new File(path);
                if (!f.exists()) {
                    log.warning("API-ExposedCommands(\"" + f.getAbsolutePath() + "\"): not found");
                    continue;
                }

                if (!f.canRead()) {
                    log.warning("API-ExposedCommands(\"" + f.getAbsolutePath() + "\"): access denied");
                    continue;
                }

                addCommands(f);
            }
        }

        String value = attributes.getValue("Osintgram4j-API-Commands");
        if (value == null) {
            log.warning("JarFile(" + file.getPath() + "): No \"Osintgram4j-API-Commands\" attribute found; skipping JAR file");
            return;
        }

        String[] paths = Tools.translateCmdLine(value);
        if (paths.length == 0) {
            log.warning("JarFile(" + file.getPath() + "): Manifest Attribute \"Osintgram4j-API-Commands\" has no paths, returned " + paths.length);
            return;
        }

        for (String path : paths) {
            JarEntry entry = jarFile.getJarEntry(path);
            if (entry == null) {
                log.warning("JarFile(" + file.getPath() + "): Entry \"" + path + "\" in the JAR file does not exist");
                throw new IOException("Entry at \"" + path + "\" in the JAR file does not exist");
            }

            InputStream is = jarFile.getInputStream(entry);
            byte[] buff = new byte[1024];
            int len;
            StringBuilder str = new StringBuilder();

            while ((len = is.read(buff, 0, 1024)) != -1)
                str.append(new String(buff, 0, len));

            is.close();

            addCommands(str.toString());
        }
    }

    /**
     * Parses and assigns/reads a specific configuration. These configurations can be
     * passed to their perspective methods to read, which can be used to remove most of
     * the arguments for their specific commands, depending on their use case.
     *
     * @param line The line to get a configuration parsed
     */
    private void assignCfg(String line) {
        log.info("adding \"" + line + "\"");

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
        String[] lnSplits = Tools.translateCmdLine(line);

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

                String ln = kbi.nextLine().trim();

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

                                        helps.put(tokVal, caller.retrieveLongHelp(new String[0]));
                                    } catch (ShellException ignore) {
                                        log.warning("Command not found: " + tokVal);
                                        Terminal.println(Terminal.TermColor.RED,
                                                String.format("Unknown command \"%s\"", tokVal), true);
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
                    case "exit", "quit", "close" -> stopShell();
                    default -> execCommand(shellConfigList, exec, givenArgs);
                }
            } catch (NoSuchElementException ignore) {
                // This exception can be ignored: can occur, when a pipe is being used in the Shell Execution Code
                // For example: 'echo help | osintgram4j'
                stopShell();
            }
        }

        kbi.close();
        System.exit(0);
    }

    /**
     * Executes a command from the parsed line.
     *
     * @param env  The environment parameter
     * @param exec The command parameter
     * @param args Given Shell command parameters
     */
    private void execCommand(List<ShellConfig> env, String exec, String[] args) {
        log.info(String.format("CreateCommandExec(Command=\"%s\", Args=\"%s\")", exec, Arrays.toString(args)));

        boolean cmdFound = false;
        for (ShellCaller caller : shellCallers) {
            if (caller.getCommand().equalsIgnoreCase(exec)) {
                cmdFound = true;

                try {
                    log.info("CommandRun(" + exec + ", " + Arrays.toString(args) + ")");

                    int code = caller.execute(args, env);

                    log.info("CommandExecution(Code=" + code + ", Cmd=" + exec + ")");

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
                            log.info("CommandRun(" + exec + ", " + Arrays.toString(args) + ")");
                            int code = caller.execute(args, env);
                            log.info("CommandExecution(Code=" + code + ", Cmd=" + exec + ")");

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
        log.info("Starting Shell");

        running = true;
        cmd();
    }

    /**
     * Stops the Application Shell
     */
    public void stopShell() {
        log.info("Closing app");

        running = false;
        Shell.instance = null;
    }

    private record ShellExecution(String exec, String[] args) {
    }

}
