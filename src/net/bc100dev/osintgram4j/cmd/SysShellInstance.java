package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.ApplicationIOException;
import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.HelpPage;
import net.bc100dev.commons.utils.Utility;
import net.bc100dev.commons.utils.io.FileUtil;
import osintgram4j.api.sh.Command;
import osintgram4j.api.sh.ShellEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.bc100dev.commons.utils.RuntimeEnvironment.*;

public class SysShellInstance extends Command {

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
                    shellExec.getAbsolutePath(), USER_NAME), true);
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
        Terminal.println(Terminal.TermColor.GREEN, String.format("Process executed under PID %d as \"%s\"",
                proc.pid(), shellExec.getName()), true);

        return proc.waitFor();
    }

    private static File findPowershell() throws IOException {
        File binPath = Utility.getBinaryPath("powershell.exe");
        if (binPath != null)
            return binPath;

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
            return psExecutable;

        throw new ApplicationIOException("powershell.exe not found");
    }

    @Override
    public int launchCmd(String[] args, List<ShellEnvironment> ignore) {
        File shellEnv = null;

        if (isWindows())
            shellEnv = new File(System.getenv("SystemRoot") + "\\system32\\cmd.exe");
        else {
            String lShellEnv = System.getenv("SHELL");
            if (lShellEnv == null) {
                if (isLinux())
                    shellEnv = new File("/bin/bash");
                else if (isMac())
                    shellEnv = new File("/bin/zsh");
            } else
                shellEnv = new File(lShellEnv);
        }

        List<String> cmdArgs = new ArrayList<>();
        boolean continueArgs = false;

        for (String arg : args) {
            switch (arg) {
                case "--integrated", "-i" -> {
                    if (isWindows())
                        shellEnv = new File("C:\\Windows\\system32\\cmd.exe");
                    else
                        shellEnv = new File("/bin/sh");
                }
                case "--bash", "-b" -> {
                    if (isWindows()) {
                        Terminal.errPrintln(Terminal.TermColor.RED, getOperatingSystemLabel(getOperatingSystem()) + ": not allowed", true);
                        return 1;
                    }

                    shellEnv = new File("/bin/bash");
                }
                case "--zsh", "-z" -> {
                    if (isWindows()) {
                        Terminal.errPrintln(Terminal.TermColor.RED, getOperatingSystemLabel(getOperatingSystem()) + ": not allowed", true);
                        return 1;
                    }

                    shellEnv = new File("/bin/zsh");
                }
                case "--fish", "-f" -> {
                    if (isWindows()) {
                        Terminal.errPrintln(Terminal.TermColor.RED, getOperatingSystemLabel(getOperatingSystem()) + ": not allowed", true);
                        return 1;
                    }

                    shellEnv = new File("/usr/bin/fish");
                }
                case "--cmd", "-c" -> {
                    if (!isWindows()) {
                        Terminal.errPrintln(Terminal.TermColor.RED, getOperatingSystemLabel(getOperatingSystem()) + ": not allowed", true);
                        return 1;
                    }

                    shellEnv = new File("C:\\Windows\\system32\\cmd.exe");
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
                            File shellOpt = new File(opts[1]);

                            if (!shellOpt.exists()) {
                                Terminal.errPrintln(Terminal.TermColor.RED, "Shell not found: " + opts[1], true);
                                return 1;
                            }

                            if (!shellOpt.isFile()) {
                                Terminal.errPrintln(Terminal.TermColor.RED, "Shell is not a executable file: " + opts[1], true);
                                return 1;
                            }

                            if (!shellOpt.canExecute()) {
                                Terminal.errPrintln(Terminal.TermColor.RED, "Shell does not have executable permission: " + opts[1], true);
                                return 1;
                            }

                            if (shellOpt.isAbsolute())
                                shellEnv = new File(opts[1]);
                            else {
                                try {
                                    shellEnv = Utility.getBinaryPath(opts[1]);
                                } catch (IOException ex) {
                                    Terminal.errPrintln(Terminal.TermColor.RED, "an error has occurred:", false);
                                    Terminal.errPrintln(Terminal.TermColor.RED, Utility.throwableToString(ex), true);
                                    return 1;
                                }
                            }
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
        if (!shellEnv.exists()) {
            Terminal.errPrintln(Terminal.TermColor.RED, String.format("Shell executable (\"%s\": \"%s\") is not found. Make sure to have it properly installed",
                    shellEnv.getName(), shellEnv.getAbsolutePath()), true);
            return 1;
        }

        if (!shellEnv.canExecute()) {
            Terminal.errPrintln(Terminal.TermColor.RED, String.format("Shell executable (\"%s\": \"%s\") does not seem to have executable permissions.",
                    shellEnv.getName(), shellEnv.getAbsolutePath()), true);
            return 1;
        }

        String[] _args = new String[cmdArgs.size()];
        for (int i = 0; i < cmdArgs.size(); i++)
            _args[i] = cmdArgs.get(i);

        try {
            return startShell(shellEnv, _args);
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
