package net.bc100dev.osintgram4j;

import net.bc100dev.osintgram4j.sh.Shell;
import net.bc100dev.osintgram4j.sh.ShellConfig;
import net.bc100dev.osintgram4j.sh.ShellException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MainClass {

    private static final String PROGRESS_LABEL = "Launching Shell";
    private static final String PROGRESS1 = PROGRESS_LABEL + " (-)";
    private static final String PROGRESS2 = PROGRESS_LABEL + " (\\)";
    private static final String PROGRESS3 = PROGRESS_LABEL + " (|)";
    private static final String PROGRESS4 = PROGRESS_LABEL + " (/)";

    private static boolean shellLaunched = false, initProgress = false;
    private static int progressIndex = 0;

    private static String getProgressLine() {
        if (progressIndex < 1)
            progressIndex = 1;

        if (progressIndex > 4)
            progressIndex = 1;

        return switch (progressIndex) {
            case 1 -> PROGRESS1;
            case 2 -> PROGRESS2;
            case 3 -> PROGRESS3;
            case 4 -> PROGRESS4;
            default -> throw new IllegalStateException("Unexpected value: " + progressIndex);
        };
    }

    private static void usage(ProcessHandle ph) {
        System.out.println(TitleBlock.TITLE_BLOCK());
        System.out.println();
        System.out.println("usage:");
        System.out.println("$ " + ph.info().command().orElse("./osintgram4j") + " [options]");
        System.out.println();
        System.out.println("options:");
        System.out.println("-h, --help          Display this message and exit");
        System.out.println("--append-env=[env]  Appends environment to the Application Shell from either CLI or File");
        System.out.println();
        System.out.println("Refer to the README.md and USAGE.md files on GitHub at");
        System.out.println("https://github.com/BeChris100/osintgram4j to have a better overview on using the Application Shell.");
    }

    public static void main(String[] args) {
        List<ShellConfig> configList = new ArrayList<>();

        if (args.length >= 1) {
            switch (args[0]) {
                case "-h", "--help", "-help", "?" -> {
                    usage(ProcessHandle.current());
                    System.exit(0);
                    return;
                }
            }

            for (String arg : args) {
                if (arg.contains("=")) {
                    String[] opts = arg.split("=", 2);
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
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        // Launch a progress bar, in case of I/O Streams
        new Thread(() -> {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException ignore) {
            }

            if (!shellLaunched) {
                while (true) {
                    progressIndex++;

                    if (progressIndex > 4)
                        progressIndex = 1;

                    System.out.print(getProgressLine() + "\r");

                    try {
                        Thread.sleep(370);
                    } catch (InterruptedException ignore) {
                    }

                    if (shellLaunched) {
                        System.out.println();
                        break;
                    }
                }
            }
        }).start();

        try {
            Shell appShell = new Shell();

            if (!configList.isEmpty())
                appShell.appendConfig(configList);

            shellLaunched = true;
            appShell.launch();
        } catch (IOException | ShellException ex) {
            ex.printStackTrace(System.err);
            shellLaunched = true;
        }
    }

}
