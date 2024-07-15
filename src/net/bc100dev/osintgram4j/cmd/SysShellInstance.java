package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.ApplicationIOException;
import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.Tools;
import net.bc100dev.commons.utils.HelpPage;
import net.bc100dev.commons.utils.Utility;
import net.bc100dev.commons.utils.io.FileUtil;
import osintgram4j.api.sh.Command;
import osintgram4j.api.sh.ShellEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static net.bc100dev.commons.utils.RuntimeEnvironment.*;
import static osintgram4j.commons.AppConstants.log_og4j;

public class SysShellInstance extends Command {

    private static int startShell(File shellExec, String... args) throws IOException, InterruptedException {
        if (shellExec == null) {
            log_og4j.warning("Shell_Error(Type = NotDefined)");
            Terminal.println(Terminal.TermColor.RED, "Shell has not been defined by the program", true);
            return 1;
        }

        if (!shellExec.exists()) {
            log_og4j.warning("Shell_Error(Type = ExecutableDoesNotExist)");
            Terminal.println(Terminal.TermColor.RED, shellExec.getAbsolutePath() + " does not exist", true);
            return 1;
        }

        if (!shellExec.canExecute()) {
            log_og4j.warning("Shell_Error(Type = FileNotExecutable)");
            Terminal.println(Terminal.TermColor.RED, String.format("%s cannot be executed by %s",
                    shellExec.getAbsolutePath(), USER_NAME), true);
            return 1;
        }

        List<String> cmd = new ArrayList<>();
        cmd.add(shellExec.getAbsolutePath());

        if (args.length != 0)
            cmd.addAll(Arrays.asList(args));

        ProcessBuilder bdr = new ProcessBuilder(cmd);
        bdr.inheritIO();

        Process proc = bdr.start();
        long pid = proc.pid();

        log_og4j.info("Shell_ProcessSpawn(PID = " + pid + "; Executable = \"" + shellExec.getName() + "\")");
        Terminal.println(Terminal.TermColor.CYAN, String.format("Process executed under PID %d as \"%s\"",
                pid, shellExec.getName()), true);

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
        log_og4j.info("Preparing Shell execution");

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
            if (continueArgs) {
                cmdArgs.add(arg);
                continue;
            }

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
                            String[] shellEnvArgs = Tools.translateCmdLine(opts[1]);
                            File shellOpt = new File(shellEnvArgs[0]);

                            if (!shellOpt.exists()) {
                                try {
                                    shellOpt = Utility.getBinaryPath(shellEnvArgs[0]);
                                    if (shellOpt == null) {
                                        Terminal.errPrintln(Terminal.TermColor.RED, "Shell not initialized: " + shellEnvArgs[0], true);
                                        return 1;
                                    }

                                    if (!shellOpt.exists()) {
                                        Terminal.errPrintln(Terminal.TermColor.RED, "Shell not found: " + shellEnvArgs[1], true);
                                        return 1;
                                    }
                                } catch (IOException ex) {
                                    Terminal.errPrintln(Terminal.TermColor.RED, "Could not find " + shellEnvArgs[0], true);
                                    return 1;
                                }
                            }

                            if (!shellOpt.isFile()) {
                                Terminal.errPrintln(Terminal.TermColor.RED, "Shell is not a executable file: " + opts[1], true);
                                return 1;
                            }

                            if (!shellOpt.canExecute()) {
                                Terminal.errPrintln(Terminal.TermColor.RED, "Shell does not have executable permission: " + opts[1], true);
                                return 1;
                            }

                            String[] additionalArgs = new String[0];

                            if (shellEnvArgs.length > 1) {
                                List<String> lAdditionalArgs = new ArrayList<>(Arrays.asList(shellEnvArgs).subList(1, shellEnvArgs.length));
                                additionalArgs = new String[lAdditionalArgs.size()];

                                for (int i = 0; i < lAdditionalArgs.size(); i++)
                                    additionalArgs[i] = lAdditionalArgs.get(i);
                            }

                            try {
                                return startShell(shellOpt, additionalArgs);
                            } catch (IOException | InterruptedException ex) {
                                ex.printStackTrace(System.err);
                                log_og4j.log(Level.WARNING, "Shell Execution failed", ex);
                                return 1;
                            }
                        }

                        continue;
                    }

                    cmdArgs.add(arg);
                    continueArgs = true;
                }
            }
        }

        if (shellEnv == null) {
            log_og4j.warning("ShellExecFail(Reason = VarNull)");
            Terminal.errPrintln(Terminal.TermColor.RED, "Shell Environment CMD Variable was not initialized properly.", true);
            return 1;
        }

        // validate the Shell
        if (!shellEnv.exists()) {
            log_og4j.warning("ShellExecFail(Reason = ShellNotFound)");
            Terminal.errPrintln(Terminal.TermColor.RED, String.format("Shell executable (\"%s\": \"%s\") is not found. Make sure to have it properly installed",
                    shellEnv.getName(), shellEnv.getAbsolutePath()), true);
            return 1;
        }

        if (!shellEnv.canExecute()) {
            log_og4j.warning("ShellExecFail(Reason = NoExecPermission)");
            Terminal.errPrintln(Terminal.TermColor.RED, String.format("Shell executable (\"%s\": \"%s\") does not seem to have executable permissions.",
                    shellEnv.getName(), shellEnv.getAbsolutePath()), true);
            return 1;
        }

        String[] _args = new String[0];
        if (!cmdArgs.isEmpty()) {
            _args = new String[2];
            _args[0] = "-c";

            StringBuilder argBd = new StringBuilder();
            for (int i = 0; i < cmdArgs.size(); i++) {
                argBd.append(cmdArgs.get(i));

                if (i != cmdArgs.size() - 1)
                    argBd.append(" ");
            }

            _args[1] = argBd.toString();
        }

        log_og4j.info("Shell execution prepared");

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
