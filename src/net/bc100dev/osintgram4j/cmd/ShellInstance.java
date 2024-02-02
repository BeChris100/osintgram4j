package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.ApplicationIOException;
import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.RuntimeEnvironment;
import net.bc100dev.commons.utils.Utility;
import net.bc100dev.commons.utils.io.FileUtil;
import net.bc100dev.commons.utils.HelpPage;
import osintgram4j.api.Command;
import osintgram4j.api.sh.ShellConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.bc100dev.commons.utils.RuntimeEnvironment.*;

public class ShellInstance extends Command {

    private static int startShell(File shellExec, String... args) throws IOException, InterruptedException {
        if (shellExec == null) {
            Terminal.println(Terminal.TermColor.RED, "Shell has not been defined by the program", true);
            return 1;
        }

        if (!shellExec.exists()) {
            Terminal.println(Terminal.TermColor.RED, shellExec.getAbsolutePath() + " does not exist", true);
            return 1;
        }

        if (!shellExec.canExecute()) {
            Terminal.println(Terminal.TermColor.RED, String.format("%s cannot be executed by %s",
                    shellExec.getAbsolutePath(), RuntimeEnvironment.USER_NAME), true);
            return 1;
        }

        ProcessBuilder bdr = new ProcessBuilder();
        bdr.command(shellExec.getAbsolutePath());

        if (args.length != 0) {
            bdr.command("-c");
            bdr.command(args);
        }

        bdr.inheritIO();

        Process proc = bdr.start();
        Terminal.println(Terminal.TermColor.GREEN, "Process executed with PID " + proc.pid(), true);

        return proc.waitFor();
    }

    private static String findPowershell() throws IOException {
        boolean foundInPath = false;
        String location = null;
        String[] pathEnv = System.getenv("PATH").split(";");
        List<String> pathContentsCached = new ArrayList<>();

        for (String pathStr : pathEnv) {
            List<String> pathContents = FileUtil.listDirectory(pathStr, true, false);

            for (String pathContent : pathContents) {
                if (pathContentsCached.contains(pathContent))
                    continue;

                File content = new File(pathContent);
                if (content.getName().equalsIgnoreCase("powershell.exe")) {
                    foundInPath = true;
                    location = content.getAbsolutePath();
                    break;
                }

                pathContentsCached.add(pathContent);
            }

            pathContentsCached.clear();

            if (foundInPath)
                break;
        }

        if (foundInPath)
            return location;

        // assume that `powershell.exe` is not found: continue with the original PowerShell installation directory
        List<String> psRootList = FileUtil.listDirectory(System.getenv("SystemRoot") + "\\System32\\WindowsPowerShell", true, false);
        double psLatestVersion = 1.0;
        for (String psRootDir : psRootList) {
            File psRoot = new File(psRootDir);
            String version = psRoot.getName();

            if ((version.startsWith("v") || version.startsWith("V")) && version.length() > 1)
                version = version.substring(1);

            try {
                double dVersion = Double.parseDouble(version);
                if (psLatestVersion < dVersion)
                    psLatestVersion = dVersion;
            } catch (NumberFormatException ignore) {
            }
        }

        // assume that we found it now
        File psExecutable = new File(System.getenv("SystemRoot") + "\\System32\\WindowsPowerShell\\" + psLatestVersion + "\\powershell.exe");
        if (psExecutable.exists() && psExecutable.canExecute())
            return psExecutable.getAbsolutePath();

        throw new ApplicationIOException("powershell.exe not found");
    }

    private static String findShell(String name) throws IOException {
        String[] pathEnv = System.getenv("PATH").split(isWindows() ? ";" : ":");

        List<String> listCache = new ArrayList<>();
        boolean found = false;
        String shellPath = null;

        for (String pathVal : pathEnv) {
            File pathDir = new File(pathVal);

            if (!pathDir.exists())
                continue;

            if (!pathDir.canRead())
                continue;

            List<String> dirContents = FileUtil.listDirectory(pathDir.getAbsolutePath(), true, false);
            for (String dirContent : dirContents) {
                if (listCache.contains(dirContent))
                    continue;

                File file = new File(dirContent);
                if (file.getName().equalsIgnoreCase(name)) {
                    if (file.canExecute()) {
                        found = true;
                        shellPath = file.getAbsolutePath();
                        break;
                    }
                }

                listCache.add(dirContent);
            }

            listCache.clear();

            if (found)
                break;
        }

        if (!found)
            throw new IOException("Shell executable by name of \"" + name + "\" not found in the PATH environments");

        return shellPath;
    }

    // Mainly working under Ubuntu 22.04.3 LTS
    @Override
    public int launchCmd(String[] args, List<ShellConfig> ignore) {
        String shellEnv;

        if (isWindows())
            shellEnv = System.getenv("SystemRoot") + "\\system32\\cmd.exe";
        else {
            shellEnv = System.getenv("SHELL");
            if (shellEnv == null) {
                if (isLinux())
                    shellEnv = "/bin/bash";
                else if (isMac())
                    shellEnv = "/bin/zsh";
            }
        }

        List<String> cmdArgs = new ArrayList<>();
        boolean continueArgs = false;

        for (String arg : args) {
            switch (arg) {
                case "--integrated", "-i" -> {
                    if (isWindows())
                        shellEnv = "C:\\Windows\\system32\\cmd.exe";
                    else
                        shellEnv = "/bin/sh";
                }
                case "--bash", "-b" -> {
                    if (isWindows()) {
                        Terminal.errPrintln(Terminal.TermColor.RED, getOperatingSystemLabel(getOperatingSystem()) + ": not allowed", true);
                        return 1;
                    }

                    shellEnv = "/bin/bash";
                }
                case "--zsh", "-z" -> {
                    if (isWindows()) {
                        Terminal.errPrintln(Terminal.TermColor.RED, getOperatingSystemLabel(getOperatingSystem()) + ": not allowed", true);
                        return 1;
                    }

                    shellEnv = "/bin/zsh";
                }
                case "--fish", "-f" -> {
                    if (isWindows()) {
                        Terminal.errPrintln(Terminal.TermColor.RED, getOperatingSystemLabel(getOperatingSystem()) + ": not allowed", true);
                        return 1;
                    }

                    shellEnv = "/usr/bin/fish";
                }
                case "--cmd", "-c" -> {
                    if (!isWindows()) {
                        Terminal.errPrintln(Terminal.TermColor.RED, getOperatingSystemLabel(getOperatingSystem()) + ": not allowed", true);
                        return 1;
                    }

                    shellEnv = "C:\\Windows\\system32\\cmd.exe";
                }
                case "--powershell", "-p" -> {
                    if (!isWindows()) {
                        Terminal.errPrintln(Terminal.TermColor.RED, getOperatingSystemLabel(getOperatingSystem()) + ": not allowed", true);
                        return 1;
                    }

                    try {
                        shellEnv = findPowershell();
                    } catch (IOException ex) {
                        Terminal.errPrintln(Terminal.TermColor.RED, "could not find Powershell", false);
                        Terminal.errPrintln(Terminal.TermColor.RED, "stacktrace:", false);
                        Terminal.errPrintln(Terminal.TermColor.RED, Utility.throwableToString(ex), true);

                        return 1;
                    }
                }
                case "--help", "-h", "?" -> {
                    String[] _args;

                    if (args.length > 1) {
                        _args = new String[args.length - 1];
                        System.arraycopy(args, 1, _args, args.length, args.length - 1);
                    } else
                        _args = new String[0];

                    Terminal.println(Terminal.TermColor.BLUE, helpCmd(_args), true);
                    return 0;
                }
                default -> {
                    if (arg.contains("=")) {
                        String[] opts = arg.split("=", 2);
                        opts[0] = opts[0].trim();
                        opts[1] = opts[1].trim();

                        if (opts[0].equals("--shell")) {
                            if (!new File(opts[1]).isAbsolute()) {
                                try {
                                    shellEnv = findShell(opts[1]);
                                } catch (IOException ex) {
                                    Terminal.errPrintln(Terminal.TermColor.RED, "an error has occurred:", false);
                                    Terminal.errPrintln(Terminal.TermColor.RED, Utility.throwableToString(ex), true);
                                }
                            } else
                                shellEnv = opts[1];
                        }
                    }

                    if (continueArgs)
                        cmdArgs.add(arg);
                    else {
                        if (!arg.startsWith("-")) {
                            cmdArgs.add(arg);
                            continueArgs = true;
                        }
                    }
                }
            }
        }

        if (shellEnv == null) {
            Terminal.errPrintln(Terminal.TermColor.RED, "Shell Environment CMD Variable was not initialized properly.", true);
            return 1;
        }

        // validate the Shell
        File shellFile = new File(shellEnv);
        if (!shellFile.exists()) {
            Terminal.errPrintln(Terminal.TermColor.RED, String.format("Shell executable (\"%s\": \"%s\") is not found. Make sure to have it properly installed",
                    shellFile.getName(), shellFile.getAbsolutePath()), true);
            return 1;
        }

        if (!shellFile.canExecute()) {
            Terminal.errPrintln(Terminal.TermColor.RED, String.format("Shell executable (\"%s\": \"%s\") does not seem to have executable permissions.",
                    shellFile.getName(), shellFile.getAbsolutePath()), true);
            return 1;
        }

        String[] _args = new String[cmdArgs.size()];
        for (int i = 0; i < cmdArgs.size(); i++)
            _args[i] = cmdArgs.get(i);

        try {
            return startShell(new File(shellEnv), _args);
        } catch (IOException | InterruptedException ex) {
            Terminal.errPrintln(Terminal.TermColor.RED, Utility.throwableToString(ex), true);
            return 1;
        }
    }

    @Override
    public String helpCmd(String[] args) {
        String defStr = """
                The Shell (sh) command launches an instance of the Shell.
                This initially launches the default Shell that is given by the "SHELL" environment.
                                
                Options:
                """;

        HelpPage page = new HelpPage();
        page.setSpaceWidth(4);
        page.addArg("--integrated / -i", null, "Launches the old-school Shell Instance (cmd.exe on Windows; /bin/sh on Linux/macOS)");

        if (isWindows()) {
            defStr = """
                    The Shell (sh) command launches a Shell instance of the Windows command prompt.
                    By running this, you launch an instance of the System Shell, following by either
                    `cmd` or `powershell`.
                                        
                    Options:
                    """;

            page.addArg("--cmd        / -c", null, "Launches the old-style Windows Command Prompt");
            page.addArg("--powershell / -p", null, "Launches the Windows PowerShell");
        } else {
            page.addArg("--bash       / -b", null, "Launches a Bash Instance");
            page.addArg("--zsh        / -z", null, "Launches a zsh instance");
            page.addArg("--fish       / -f", null, "Launches a fish shell instance");
        }

        page.addArg("--shell=(/path/to/shell)", null, "Launches a Shell instance by the shell file path");

        return defStr + page.display();
    }

}
