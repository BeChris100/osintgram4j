package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.ResourceManager;
import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.HelpPage;
import net.bc100dev.osintgram4j.MainClass;
import osintgram4j.api.Command;
import osintgram4j.commons.ShellConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class VerCmd extends Command {

    private static String version(Properties props) {
        return "v" + props.getProperty("BUILD_VERSION");
    }

    private static int versionCode(Properties props) {
        try {
            return Integer.parseInt(props.getProperty("BUILD_VERSION_CODE"));
        } catch (NumberFormatException ignore) {
            return -1;
        }
    }

    private static String name(Properties props) {
        return props.getProperty("BUILD_NAME");
    }

    private static String displayName(Properties props) {
        return props.getProperty("BUILD_DISPLAY");
    }

    private static String flavor(Properties props) {
        return props.getProperty("BUILD_FLAVOR");
    }

    private static String rFlavor(Properties props) {
        return switch (flavor(props)) {
            case "debug", "dev", "devel", "development" -> "Development";
            case "release" -> "Release";
            case "p_release" -> "Pre-Release";
            case "v_alp" -> "Alpha";
            case "v_bt" -> "Beta";
            default -> "Unknown_Flavor";
        };
    }

    private static String defaultLine(Properties props) {
        return displayName(props) + " (" + name(props) + ") " + version(props) + "-" +
                versionCode(props) + " " + flavor(props) + " (" + rFlavor(props) + ")";
    }

    @Override
    public int launchCmd(String[] args, List<ShellConfig> env) {
        try {
            Properties props = new Properties();
            ResourceManager mgr = new ResourceManager(MainClass.class, true);
            InputStream is = mgr.getResourceInputStream("res/app_ver.cfg");

            props.load(is);

            is.close();

            if (args == null) {
                Terminal.println(Terminal.TermColor.BLUE, defaultLine(props), true);
                return 0;
            }

            if (args.length == 0) {
                Terminal.println(Terminal.TermColor.BLUE, defaultLine(props), true);
                return 0;
            }

            boolean bVersion = false,
                    bVersionCode = false,
                    bName = false,
                    bDisplayName = false,
                    bFlavor = false,
                    bReFlavor = false;

            for (String arg : args) {
                switch (arg) {
                    case "-v" -> bVersion = true;
                    case "-V" -> bVersionCode = true;
                    case "-n" -> bName = true;
                    case "-N" -> bDisplayName = true;
                    case "-f" -> bFlavor = true;
                    case "-F" -> bReFlavor = true;
                }
            }

            StringBuilder str = new StringBuilder();
            if (bVersion)
                str.append(version(props)).append(" ");

            if (bVersionCode)
                str.append(versionCode(props)).append(" ");

            if (bName)
                str.append(name(props)).append(" ");

            if (bDisplayName)
                str.append(displayName(props)).append(" ");

            if (bFlavor)
                str.append(flavor(props)).append(" ");

            if (bReFlavor)
                str.append(rFlavor(props)).append(" ");

            System.out.println(str);
            return 0;
        } catch (IOException ex) {
            Terminal.errPrintln(Terminal.TermColor.RED, "An error occurred while trying to read the App Information file", true);
            return 1;
        }
    }

    @Override
    public String helpCmd(String[] args) {
        HelpPage helpPage = new HelpPage();
        helpPage.setSpaceWidth(5);
        helpPage.addArg("-v", null, "Displays Version");
        helpPage.addArg("-V", null, "Displays Version of the current Codebase");
        helpPage.addArg("-n", null, "Displays name of the Codebase");
        helpPage.addArg("-N", null, "Displays the Display name");
        helpPage.addArg("-f", null, "Displays current Flavor");
        helpPage.addArg("-F", null, "Displays current Flavor, but more readable");

        return "Displays the current Application Version and Information.\n\nOptions:\n" + helpPage.display();
    }

}
