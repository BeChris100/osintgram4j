package net.bc100dev.osintgram4j;

import net.bc100dev.commons.ApplicationException;
import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.HelpPage;
import net.bc100dev.commons.utils.io.FileUtil;
import net.bc100dev.commons.utils.io.UserIO;
import osintgram4j.api.Shell;
import osintgram4j.api.ShellException;
import osintgram4j.commons.ShellConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;

import static net.bc100dev.commons.utils.RuntimeEnvironment.*;
import static net.bc100dev.osintgram4j.Settings.securityWarnings;
import static net.bc100dev.osintgram4j.Settings.loadSettings;
import static osintgram4j.commons.AppConstants.log;
import static osintgram4j.commons.TitleBlock.DISPLAY;
import static osintgram4j.commons.TitleBlock.TITLE_BLOCK;

public class MainClass {

    private static final List<ShellConfig> configList = new ArrayList<>();

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

            log.log(Level.SEVERE, thread.getName() + ": crashed (exception not handled)", throwable);
        });

        try {
            // duplicate code from `Settings#storeLocation()` (reasoning: Settings not loaded yet)
            File logFile = switch (getOperatingSystem()) {
                case LINUX -> new File(USER_HOME.getAbsolutePath() + "/.config/net.bc100dev/osintgram4j/log.txt");
                case WINDOWS -> new File(USER_HOME.getAbsolutePath() + "\\AppData\\Local\\BC100Dev\\Osintgram4j\\log.txt");
                case MAC_OS -> new File(USER_HOME.getAbsolutePath() + "/Library/net.bc100dev/osintgram4j/log.txt");
            };

            if (!logFile.exists())
                FileUtil.createFile(logFile.getAbsolutePath(), true);

            FileHandler handler = new FileHandler(logFile.getAbsolutePath(), true);
            handler.setFormatter(new LGFMT());

            log.setUseParentHandlers(false);
            log.addHandler(handler);
            log.info("Initialized");
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private static void usage(ProcessHandle ph) {
        log.info("Show help page upon start");
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

        System.out.println("$ " + cmd + " [options] (target) (target2 ...)");
        System.out.println();
        System.out.println("options:");

        HelpPage helpPage = new HelpPage();
        helpPage.setSpaceWidth(3);
        helpPage.setStartSpaceWidth(4);
        helpPage.addArg("-h, --help", null, "Display this message and exit");
        helpPage.addArg("--append-env", "[env]", "Appends environment to the Application Shell from either CLI or File");
        helpPage.addArg("-S[suppress]", null, "Suppresses warning messages, temporarily disabling them");

        String str = helpPage.display();
        str = str.substring(0, str.length() - 1);
        System.out.println(str);

        System.out.println();
        System.out.println("where:");
        System.out.println("   > [options]       ");
        System.out.println("   > (target)        primary target; optional, can be set within the Shell");
        System.out.println("   > (target2 ...)   additional targets by the use of Sessions; optional, can be set within the Shell");

        System.out.println();
        System.out.println("Refer to the README.md and USAGE.md files on GitHub, along with the Wikis at");
        System.out.println("https://github.com/BeChris100/osintgram4j to have a better overview on using the Application Shell.");
        System.out.println("Might consider checking the \"docs.d\" folder within the repository root.");
    }

    public static void main(String[] args) throws IOException {
        init();
        loadSettings();

        log.info("Application initialized");

        if (NativeLoader.hasLibrary())
            NativeLoader.load();
        else if (!isMac())
            throw new RuntimeException("the application cannot find the native library for this application");

        if (!NativeLoader.isLoaded()) {
            if (!isMac())
                throw new RuntimeException("native library unable to load");
        }

        StringBuilder suppressStr = new StringBuilder();

        if (args.length >= 1) {
            for (String arg : args) {
                String[] opts;
                if (arg.contains("="))
                    opts = arg.split("=", 2);
                else
                    opts = new String[]{arg};

                if (opts[0].startsWith("-")) {
                    if (opts[0].equals("--append-env") || opts[0].equals("-ae")) {
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
                        String s = arg.substring(2);
                        if (s.equalsIgnoreCase("admin_checks")) {
                            if (suppressStr.toString().contains("admin_checks"))
                                break;

                            suppressStr.append("admin_checks ");
                        }
                    } else {
                        switch (arg) {
                            case "-h", "--help", "-help", "help", "?" -> {
                                usage(ProcessHandle.current());
                                System.exit(0);
                                return;
                            }
                        }
                    }
                } else
                    addTarget(arg);
            }
        }

        if (!suppressStr.toString().contains("admin_checks") || securityWarnings()) {
            try {
                if (NativeLoader.isLoaded()) {
                    if (UserIO.isAdmin()) {
                        System.out.println("Running this process with elevated privileges gives all JAR/Classpath Entries the exact same privilege level.");
                        System.out.println("This includes this application and specific mods. As a security warning: Do not run any programs with high privileges, unless you trust the Software and you know, what you are doing.");
                        System.out.println("To disable this security warning message, pass `-Sadmin_checks` or disable by changing the Settings file.");
                    }
                }
            } catch (ApplicationException ex) {
                ex.printStackTrace(System.err);
            }
        }

        String clsPS = System.getProperty("java.class.path");
        String[] clsPL = clsPS.split(File.pathSeparator);

        List<File> modFiles = new ArrayList<>();

        for (String clsPE : clsPL) {
            File clsF = new File(clsPE);
            if (clsF.isDirectory())
                continue;

            if (clsF.isFile() && clsF.getName().endsWith(".jar"))
                modFiles.add(clsF);
        }

        try {
            String suppress = suppressStr.toString();
            if (!suppress.trim().isEmpty())
                suppress = suppress.substring(0, suppress.length() - 1);

            Shell appShell = new Shell(suppress);

            if (!modFiles.isEmpty()) {
                for (File modFile : modFiles)
                    appShell.addCommandsFromJar(modFile);
            }

            if (!configList.isEmpty())
                appShell.appendConfig(configList);

            appShell.launch();
        } catch (IOException | ShellException ex) {
            ex.printStackTrace(System.err);
        }

        log.info("App stopped");
    }

    private static void addTarget(String target) {
        if (configList.isEmpty()) {
            configList.add(new ShellConfig("Session0:UserTarget", target));
            return;
        }

        int sessionIndex = 0;
        for (ShellConfig config : configList) {
            if (config.getName().equals(String.format("Session%d:UserTarget", sessionIndex)))
                sessionIndex++;
        }

        configList.add(new ShellConfig(String.format("Session%d:UserTarget", sessionIndex), target));
    }

}
