package net.bc100dev.osintgram4j;

import net.bc100dev.commons.ApplicationException;
import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.HelpPage;
import net.bc100dev.commons.utils.StringGenerator;
import net.bc100dev.commons.utils.io.UserIO;
import net.bc100dev.osintgram4j.sh.Shell;
import net.bc100dev.osintgram4j.sh.ShellException;
import net.bc100dev.osintgram4j.sh.ShellFile;
import osintgram4j.commons.ShellConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static net.bc100dev.commons.utils.RuntimeEnvironment.isMac;
import static net.bc100dev.commons.utils.RuntimeEnvironment.isWindows;
import static net.bc100dev.osintgram4j.Settings.app_adminSecurityWarningEnabled;
import static net.bc100dev.osintgram4j.Settings.loadSettings;
import static net.bc100dev.osintgram4j.TitleBlock.DISPLAY;
import static net.bc100dev.osintgram4j.TitleBlock.TITLE_BLOCK;

public class MainClass {

    private static void init() {
        Terminal.TermColor cRed = Terminal.TermColor.RED;

        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            throwable.printStackTrace(pw);

            Terminal.errPrintln(cRed, "While running the Application, the Main Thread crashed.", false);

            if (throwable instanceof Error)
                Terminal.errPrintln(cRed, "Java Errors cannot be caught, and can be only fixed within the code itself.", false);

            if (throwable instanceof Exception)
                Terminal.errPrintln(cRed, "For some reason, a piece of code was not surrounded within a try-catch, or a Runtime Error was encountered.", true);

            Terminal.errPrintln(null, null, true);
            Terminal.errPrintln(cRed, "Error/Exception Stacktrace:", true);
            Terminal.errPrintln(cRed, sw.toString(), true);
        });
    }

    private static void usage(ProcessHandle ph) {
        System.out.println(TITLE_BLOCK());
        System.out.println();
        System.out.println(DISPLAY());
        System.out.println();
        System.out.println("usage:");

        String cmd = "." + (isWindows() ? "\\" : "/") + "osintgram4j" + (isWindows() ? ".exe" : "");
        if (ph.info().command().isPresent()) {
            File f = new File(ph.info().command().get());
            cmd = f.getName().equals("java") ? "osintgram4j.jar" : f.getName();
        }

        System.out.println("$ " + cmd + " [options]");
        System.out.println();
        System.out.println("options:");

        HelpPage helpPage = new HelpPage();
        helpPage.setSpaceWidth(3);
        helpPage.addArg("-h, --help", null, "Display this message and exit");
        helpPage.addArg("--append-env", "[env]", "Appends environment to the Application Shell from either CLI or File");
        helpPage.addArg("-gI [count]", null, "Generates new Identifiers for a development package");
        helpPage.addArg("-S[suppress]", null, "Suppresses warning messages, temporarily disabling them");
        helpPage.display(System.out);

        System.out.println();
        System.out.println("Refer to the README.md and USAGE.md files on GitHub, along with the Wikis at");
        System.out.println("https://github.com/BeChris100/osintgram4j to have a better overview on using the Application Shell.");
    }

    private static List<String> generateIdentifiers(int count) {
        if (count <= 0) {
            System.out.println("## using count of 1");
            count = 1;
        }

        List<String> list = new ArrayList<>();
        String map = "0123456789abcdefghijklmnopqrstuvwxyz";

        for (int l = 0; l < count; l++) {
            String ident = StringGenerator.generateString(35, map);
            if (list.contains(ident))
                System.err.println("#! Duplicate entry found; continuing");

            list.add(ident);

            if (l + 1 != Integer.MAX_VALUE)
                System.out.print("## Generated " + (l + 1) + " identifiers\r");
        }

        System.out.println("## Generated " + count + " identifiers\r");

        return list;
    }

    public static void main(String[] args) {
        init();
        loadSettings();

        if (NativeLoader.hasLibrary())
            NativeLoader.load();

        if (!NativeLoader.isLoaded()) {
            if (!isMac())
                throw new RuntimeException("native library unable to load");
        }

        List<ShellConfig> configList = new ArrayList<>();
        ShellFile shellFile = null;

        StringBuilder suppressStr = new StringBuilder();

        if (args.length >= 1) {
            for (String arg : args) {
                String[] opts;
                if (arg.contains("="))
                    opts = arg.split("=", 2);
                else
                    opts = new String[]{arg};

                if (opts[0].equals("--append-env")) {
                    if (opts[1].contains("=")) {
                        String[] env = opts[1].split("=", 2);
                        env[0] = env[0].trim();
                        env[1] = env[1].trim();

                        configList.add(new ShellConfig(env[0], env[1]));
                    } else {
                        File envFile = new File(opts[1]);
                        if (!envFile.exists())
                            System.err.println("Environment file at \"" + envFile.getPath() + "\" does not exist");

                        if (!envFile.canRead())
                            System.err.println("Cannot read env file at \"" + envFile.getPath() + "\" (Permission denied)");

                        try {
                            FileInputStream fis = new FileInputStream(envFile);
                            Properties envProps = new Properties();
                            envProps.load(fis);

                            fis.close();

                            if (!envProps.isEmpty()) {
                                for (Object key : envProps.keySet())
                                    configList.add(new ShellConfig((String) key, envProps.getProperty((String) key)));
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace(System.err);
                        }
                    }
                } else if (arg.startsWith("-S")) {
                    switch (arg) {
                        case "-Sscript_uses" -> {
                            if (suppressStr.toString().contains("scripts"))
                                break;

                            suppressStr.append("scripts ");
                        }
                        case "-Sadmin_checks" -> {
                            if (suppressStr.toString().contains("admin_checks"))
                                break;

                            suppressStr.append("admin_checks ");
                        }
                    }
                } else {
                    switch (args[0]) {
                        case "-h", "--help", "-help", "?" -> {
                            usage(ProcessHandle.current());
                            System.exit(0);
                            return;
                        }
                        case "--generate-identifiers", "-gI", "--identifiers" -> {
                            if (args.length == 1) {
                                System.err.println("## Required [count] parameter.");
                                System.exit(1);
                                return;
                            }

                            int count = 1;
                            try {
                                count = Integer.parseInt(args[1]);
                            } catch (NumberFormatException ignore) {
                                System.err.println("## Invalid argument \"" + args[1] + "\": using count of 1");
                            }

                            List<String> identifierList = generateIdentifiers(count);
                            for (String identifier : identifierList)
                                System.out.println(identifier);

                            System.exit(0);
                        }
                        default -> {
                            String n = args[0];
                            File file = new File(n);
                            if (!file.exists()) {
                                configList.add(new ShellConfig("CmdArgument.UserTarget", n));
                                break;
                            }

                            if (!file.canRead()) {
                                System.err.println("\"" + file.getPath() + "\": Permission denied");
                                break;
                            }

                            try {
                                shellFile = ShellFile.open(file);
                            } catch (IOException ex) {
                                ex.printStackTrace(System.err);
                            }
                        }
                    }
                }
            }
        }

        if (!suppressStr.toString().contains("admin_checks") || app_adminSecurityWarningEnabled()) {
            try {
                if (NativeLoader.isLoaded()) {
                    if (UserIO.isAdmin()) {
                        if (isWindows())
                            System.out.println("The current process is running with elevated privileges. If you have encountered errors that may relate to not being executed without administrative privileges, you may continue, otherwise consider running processes without administrative privileges, as it may bring unexpected damages.");
                        else
                            System.out.println("The current process is running as the Root user. If you have encountered errors that may relate to not being executed without administrative privileges, you may continue, otherwise consider running processes as normal users, as it may bring unexpected damages.");

                        System.out.println("As a security warning: Do not run any programs with high privileges, unless that you trust the Software that you are about to execute and you know, what you are doing.");
                        System.out.println("To disable this warning message, either pass '-Sadmin_checks' or head over to the Settings file.");
                    }
                }
            } catch (ApplicationException ex) {
                ex.printStackTrace(System.err);
            }
        }

        try {
            String suppress = suppressStr.toString();
            if (!suppress.trim().isEmpty())
                suppress = suppress.substring(0, suppress.length() - 1);

            Shell appShell = new Shell(suppress);

            if (!configList.isEmpty())
                appShell.appendConfig(configList);

            if (shellFile != null) {
                appShell.runScript(shellFile);
                return;
            }

            appShell.launch();
        } catch (IOException | ShellException ex) {
            ex.printStackTrace(System.err);
        }
    }

}
