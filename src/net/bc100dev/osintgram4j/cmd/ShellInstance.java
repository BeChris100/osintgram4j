package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.RuntimeEnvironment;
import net.bc100dev.osintgram4j.pcl.PCLConfig;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShellInstance {

    private static int startShell(File shellExec, String... args) throws IOException, InterruptedException {
        if (shellExec == null) {
            Terminal.println(Terminal.Color.RED, "Shell has not been defined by the program", true);
            return 1;
        }

        if (!shellExec.exists()) {
            Terminal.println(Terminal.Color.RED, shellExec.getAbsolutePath() + " does not exist", true);
            return 1;
        }

        if (!shellExec.canExecute()) {
            Terminal.println(Terminal.Color.RED, String.format("%s cannot be executed by %s",
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
        Terminal.println(Terminal.Color.GREEN, "Process executed with PID " + proc.pid(), true);

        return proc.waitFor();
    }

    // Invoked manually by `Method.invoke`
    // Currently only works under Linux (Ubuntu 22.04.3 LTS)
    public static int launchCmd(String[] args, List<PCLConfig> ignore) {
        String shellEnv = System.getenv("SHELL");
        if (shellEnv == null)
            // set to linux default
            shellEnv = "/bin/sh";

        List<String> cmdArgs = new ArrayList<>();
        boolean continueArgs = false;

        for (String arg : args) {
            switch (arg) {
                case "--integrated" -> shellEnv = "/bin/sh";
                case "--bash" -> shellEnv = "/bin/bash";
                case "--zsh" -> shellEnv = "/bin/zsh";
                case "--fish" -> shellEnv = "/bin/fish";
                case "--help", "-h", "?" -> {
                    Terminal.println(Terminal.Color.BLUE, helpCmd(args), true);
                    return 0;
                }
                default -> {
                    if (arg.contains("=")) {
                        String[] opts = arg.split("=", 2);
                        opts[0] = opts[0].trim();
                        opts[1] = opts[1].trim();

                        if (opts[0].equals("--shell"))
                            shellEnv = opts[1];
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

        String[] _args = new String[cmdArgs.size()];
        for (int i = 0; i < cmdArgs.size(); i++)
            _args[i] = cmdArgs.get(i);

        try {
            return startShell(new File(shellEnv), _args);
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
        }

        return 0;
    }

    // Invoked manually by `Method.invoke`
    public static String helpCmd(String[] args) {
        return """
                The Shell (sh) command launches an instance of the Shell.
                This initially launches the default Shell that is given by the "SHELL" environment.
                                
                Options:
                --integrated / -i          Launches the default Linux Shell Instance (/bin/sh)
                --bash       / -b          Launches a Bash Instance
                --zsh        / -z          Launches a ZSH Instance
                --fish       / -f          Launches a fish (Shell) instance
                --shell=(/path/to/shell)   Launches a Shell Instance that is not given by the options list""";
    }

}
