package osintgram4j.api.sh;

import net.bc100dev.commons.*;
import net.bc100dev.commons.utils.Utility;
import net.bc100dev.commons.utils.io.UserIO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static net.bc100dev.commons.Terminal.TermColor.*;
import static net.bc100dev.commons.utils.RuntimeEnvironment.*;
import static osintgram4j.commons.AppConstants.log_og4j;

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

    private boolean running = false;

    public final boolean terminal = System.console() != null;

    public Scanner kbi; // kbi, as short, is named after "KeyBoard Input"

    public final List<ShellEnvironment> shellEnvList = new ArrayList<>();
    public final List<ShellCommand> shellCallers = new ArrayList<>();
    public final List<ShellAlias> shellAliases = new ArrayList<>();

    private String PS1;

    /**
     * Initializes the Shell with the necessary Input and the necessary commands.
     *
     * @throws ShellException May throw an exception during initializing the commands.
     * @throws IOException    Throws an exception, when cannot read any command entries
     */
    public Shell() throws IOException, ShellException {
        if (instance != null)
            throw new ShellException("A Shell has been already initialized. Close the previously initialized Shell first.");

        this.kbi = new Scanner(System.in);

        try {
            PS1 = String.format("[%s/%s: %s]%s ", USER_NAME, getHostName(), WORKING_DIRECTORY.getName(), UserIO.nIsAdmin() ? "#" : "$");
        } catch (ApplicationException ex) {
            ex.printStackTrace(System.err);
            PS1 = String.format("[%s/%s: %s] >> ", USER_NAME, getHostName(), WORKING_DIRECTORY.getName());
        }

        if (!terminal)
            PS1 = "[osintgram4j]$ ";

        try {
            InputStream is = new ResourceManager(Shell.class, false).getResourceInputStream("/net/bc100dev/osintgram4j/res/cmd_list_d/app-core.json");
            byte[] buff = is.readAllBytes();
            is.close();

            addCommands(new String(buff));
        } catch (IOException | ShellException ex) {
            throw new ApplicationRuntimeException(ex);
        }

        Shell.instance = this;
    }

    public static Shell getInstance() {
        return instance;
    }

    public void appendConfig(List<ShellEnvironment> configList) {
        shellEnvList.addAll(configList);
        log_og4j.info("added " + configList.size() + " variables");
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
        shellAliases.addAll(entry.getAliases());

        log_og4j.info("added " + entry.getCommands().size() + " commands");
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
        shellAliases.addAll(e.getAliases());

        log_og4j.info("added " + e.getCommands().size() + " commands");
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
            log_og4j.warning("Manifest file for file \"" + file.getPath() + "\" was not found");
            return;
        }

        Attributes attributes = manifest.getMainAttributes();
        String value = attributes.getValue("Osintgram4j-OnLoad-Commands");

        if (value == null) {
            log_og4j.warning("JarFile(" + file.getPath() + "): No \"Osintgram4j-OnLoad-Commands\" attribute found; skipping JAR file");
            return;
        }

        String[] paths = Tools.translateCmdLine(value);
        if (paths.length == 0) {
            log_og4j.warning("JarFile(" + file.getPath() + "): Manifest Attribute \"Osintgram4j-OnLoad-Commands\" has no paths, returned " + paths.length);
            return;
        }

        for (String path : paths) {
            JarEntry entry = jarFile.getJarEntry(path);
            if (entry == null) {
                log_og4j.warning("JarFile(" + file.getPath() + "): Entry \"" + path + "\" in the JAR file does not exist");
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
        log_og4j.info("AddEnv \"" + line + "\"");

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

            if (shellEnvList.isEmpty()) {
                shellEnvList.add(new ShellEnvironment(opt[0], opt[1]));
                return;
            }

            boolean found = false;
            for (int i = 0; i < shellEnvList.size(); i++) {
                ShellEnvironment cfg = shellEnvList.get(i);

                if (opt[0].equals(cfg.getName())) {
                    cfg.setValue(opt[1]);

                    shellEnvList.set(i, cfg);

                    found = true;
                }
            }

            if (!found)
                shellEnvList.add(new ShellEnvironment(opt[0], opt[1]));
        } else {
            if (shellEnvList.isEmpty()) {
                System.out.println("No Items assigned");
                return;
            }

            String nm = line.replaceFirst("&", "").trim();

            boolean found = false;
            for (ShellEnvironment cfg : shellEnvList) {
                if (nm.equals(cfg.getName())) {
                    System.out.printf("%s ==> %s\n", cfg.getName(), cfg.getValue());
                    found = true;
                }
            }

            if (!found)
                System.out.printf("%s is not defined\n", nm);
        }
    }

    private void assignAlias(String line) {
        line = line.replaceFirst("%", "");

        String[] opts = Tools.translateCmdLine(line);
        if (opts.length == 0) {
            log_og4j.warning("AliasCreationError(Length == 0)");
            Terminal.errPrintln(RED, "no alias name given", true);
            return;
        }

        String aliasName = opts[0];
        String cmdName = null;
        List<String> execArgs = new ArrayList<>();

        if (opts.length > 1) {
            cmdName = opts[1];

            if (opts.length > 2)
                execArgs.addAll(Arrays.asList(opts).subList(2, opts.length));
        }

        if (opts.length == 1) {
            aliasName = opts[0];

            for (ShellAlias alias : shellAliases) {
                if (!aliasName.equals(alias.getAliasCmd()))
                    continue;

                Terminal.print(CYAN, aliasName + ": ", false);
                Terminal.print(BLUE, "aliased to ", false);
                Terminal.print(CYAN, alias.getCaller().getCommand(), false);
                Terminal.print(BLUE, ", with arguments \"", false);

                String[] _args = alias.getExecutionArgs();
                for (int i = 0; i < _args.length; i++) {
                    Terminal.print(CYAN, _args[i], false);

                    if (i != _args.length - 1)
                        Terminal.print(CYAN, " ", false);
                }

                Terminal.println(CYAN, "\"", true);
                break;
            }

            return;
        }

        ShellCommand execCall = null;
        for (ShellCommand caller : shellCallers) {
            if (cmdName.equals(caller.getCommand()))
                execCall = caller;
        }

        if (execCall == null) {
            Terminal.errPrintln(RED, "No command by the name of \"" + cmdName + "\" found", true);
            return;
        }

        String[] sExecArgs = new String[execArgs.size()];
        for (int i = 0; i < execArgs.size(); i++)
            sExecArgs[i] = execArgs.get(i);

        for (int i = 0; i < shellAliases.size(); i++) {
            ShellAlias alias = shellAliases.get(i);

            if (aliasName.equals(alias.getAliasCmd())) {
                ShellAlias nAlias = new ShellAlias(aliasName, execCall, sExecArgs);
                nAlias.allowPlatformSupport(getOperatingSystem(), true);
                shellAliases.set(i, nAlias);

                return;
            }
        }

        ShellAlias nAlias = new ShellAlias(aliasName, execCall, sExecArgs);
        nAlias.allowPlatformSupport(getOperatingSystem(), true);
        shellAliases.add(nAlias);

        log_og4j.info(String.format("MkAlias(\"%s\", Cmd=\"%s\", ArgsLen=\"%d\")", aliasName, cmdName, execArgs.size()));
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
                System.out.print(PS1);
                String ln = kbi.nextLine().trim();

                if (ln.isEmpty())
                    continue;

                if (ln.startsWith("&")) {
                    assignCfg(ln);
                    continue;
                }

                if (ln.startsWith("%")) {
                    assignAlias(ln);
                    continue;
                }

                ShellExecution execution = getExecutionLine(ln);
                if (execution == null)
                    continue;

                String exec = execution.exec();
                String[] givenArgs = execution.args();

                switch (exec) {
                    case "exit", "quit", "close" -> stopShell();
                    default -> execCommand(exec, givenArgs);
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
     * @param exec The command parameter
     * @param args Given Shell command parameters
     */
    private void execCommand(String exec, String[] args) {
        log_og4j.info(String.format("CreateCommandExec(Command=\"%s\", Args=\"%s\")", exec, Arrays.toString(args)));

        int rnd = Utility.getRandomInteger(0, 100000);
        if (rnd == 1983) {
            rnd = Utility.getRandomInteger(1, 5);
            switch (rnd) {
                case 1 -> log_og4j.info("They have already infested the machine");
                case 2 -> log_og4j.info("Problems have arrived. Time to wipe them.");
                case 3 -> log_og4j.info("Why did they have to add them in?");
                case 4 -> log_og4j.info("Bro had all the time to design the core, yet couldn't make the API already.");
                case 5 -> log_og4j.info("Nothing else than the alternates being within the core itself.");
            }
        }

        boolean cmdFound = false;
        for (ShellCommand caller : shellCallers) {
            if (caller.getCommand().equalsIgnoreCase(exec)) {
                cmdFound = true;

                try {
                    log_og4j.info("CommandRun(" + exec + ", " + Arrays.toString(args) + ")");

                    if (caller.isDeprecated()) {
                        log_og4j.warning("CommandDeprecation(" + exec + ")");
                        Terminal.println(YELLOW, String.format("%s: command deprecated", exec), true);
                    }

                    int code = caller.execute(args, shellEnvList);

                    log_og4j.info("CommandExecution(Code=" + code + ", Cmd=" + exec + ")");

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
                    if (alternate.equalsIgnoreCase(exec)) {
                        cmdFound = true;

                        if (caller.isDeprecated()) {
                            log_og4j.warning("CommandDeprecation(" + exec + ")");
                            Terminal.println(YELLOW, String.format("%s: command deprecated", exec), true);
                        }

                        try {
                            log_og4j.info("CommandRun(MC_Alternate; " + exec + ", " + Arrays.toString(args) + ")");
                            int code = caller.execute(args, shellEnvList);
                            log_og4j.info("CommandExecution(MC_Alternate; Code=" + code + ", Cmd=" + exec + ")");

                            if (code != 0) {
                                Terminal.println(Terminal.TermColor.RED,
                                        alternate + ": exit code " + code, true);
                            }
                        } catch (ShellException ex) {
                            Terminal.println(Terminal.TermColor.RED, Utility.throwableToString(ex), true);
                        }
                    }
                }

                if (!cmdFound) {
                    for (ShellAlias alias : shellAliases) {
                        if (alias.getAliasCmd().equals(exec)) {
                            cmdFound = true;

                            if (!alias.isPlatformSupported(getOperatingSystem())) {
                                Terminal.errPrintln(RED, String.format("%s: command not allowed (unsupported platform)", alias.getAliasCmd()), true);
                                cmdFound = false;
                            }

                            if (cmdFound) {
                                try {
                                    log_og4j.info("AliasExecute(Command=" + alias.getCaller().getCommand() + "; DefArgs=" + Arrays.toString(alias.getExecutionArgs()) + "; AdditionalArgs=" + Arrays.toString(args) + ")");
                                    int code = alias.execute(args, shellEnvList);
                                    log_og4j.info("AliasExecution(Code=" + code + ")");
                                } catch (ShellException ex) {
                                    Terminal.println(Terminal.TermColor.RED, Utility.throwableToString(ex), true);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!cmdFound) {
            log_og4j.info(String.format("CommandExecution(Command = \"%s\"; Found = false)", exec));
            Terminal.println(Terminal.TermColor.RED, String.format("%s: command not found", exec), true);
        }
    }

    /**
     * Launches the interactive Shell
     */
    public void launch() {
        log_og4j.info("Starting Shell");

        running = true;
        cmd();
    }

    /**
     * Stops the Application Shell
     */
    public void stopShell() {
        log_og4j.info("Closing app");

        running = false;
        Shell.instance = null;
    }

    private record ShellExecution(String exec, String[] args) {
    }

}
