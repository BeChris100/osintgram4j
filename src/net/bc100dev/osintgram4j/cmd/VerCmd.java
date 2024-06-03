package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.ResourceManager;
import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.HelpPage;
import net.bc100dev.osintgram4j.MainClass;
import osintgram4j.api.sh.Command;
import osintgram4j.commons.ShellConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import static net.bc100dev.commons.utils.RuntimeEnvironment.getOperatingSystem;

public class VerCmd extends Command {

    private static String version(Properties props) {
        return props.getProperty("BUILD_VERSION");
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

    private static String displayFlavor(Properties props) {
        return props.getProperty("BUILD_DISPLAY_FLAVOR");
    }

    private static String defaultLine(Properties props) {
        return displayName(props) + " (" + name(props) + ") " + version(props) + "-" +
                versionCode(props) + " " + flavor(props) + " (" + displayFlavor(props) + ")";
    }

    private static String buildImage(Properties props) {
        return props.getProperty("BUILD_IMAGE");
    }

    private static String buildReadableImage(String imgType) {
        return switch (imgType) {
            /*
            # ghm = GitHub Master Branch Image
            # ghr = GitHub Release Image
            # lr-al = Linux Release - Arch Linux
            # lr-db = Linux Release - Debian
            # lr-pt = Linux Release - Portable
            # w64-is = Windows x64 - Installed
            # w64-pt = Windows x64 - Portable
             */
            case "ghm" -> "GitHub Master Branch Image";
            case "ghr" -> "GitHub Release Image";
            case "lr-al" -> "Arch Linux Package Release";
            case "lr-db" -> "Debian Package Release";
            case "lr-pt" -> "Linux Portable";
            case "w64-ie" -> "Windows-x64 Installed";
            case "w64-pt" -> "Windows-x64 Portable";
            default -> switch (getOperatingSystem()) {
                case WINDOWS -> "Windows Image Build";
                case LINUX -> "Linux Image Build";
                case MAC_OS -> "macOS Image Build";
            };
        };
    }

    @Override
    public int launchCmd(String[] args, List<ShellConfig> env) {
        InputStream is = null;

        try {
            Properties props = new Properties();
            ResourceManager mgr = new ResourceManager(MainClass.class, true);
            is = mgr.getResourceInputStream("res/app_ver.cfg");

            props.load(is);

            if (args == null || args.length == 0) {
                Terminal.println(Terminal.TermColor.BLUE, defaultLine(props), true);
                return 0;
            }

            StringBuilder str = new StringBuilder();

            for (String arg : args) {
                switch (arg) {
                    case "-v" -> str.append(version(props)).append(" ");
                    case "-V" -> str.append(versionCode(props)).append(" ");
                    case "-n" -> str.append(name(props)).append(" ");
                    case "-N" -> str.append(displayName(props)).append(" ");
                    case "-f" -> str.append(flavor(props)).append(" ");
                    case "-F" -> str.append(displayFlavor(props)).append(" ");
                    case "-i" -> str.append(buildImage(props)).append(" ");
                    case "-I" -> str.append(buildReadableImage(buildImage(props))).append(" ");
                    case "-h", "--help", "help" -> {
                        System.out.println(helpCmd(args));
                        return 0;
                    }
                    default -> {
                        Terminal.errPrintln(Terminal.TermColor.RED, String.format("flag \"%s\" is unknown", arg), true);
                        return 1;
                    }
                }
            }

            System.out.println(str.substring(0, str.toString().length() - 1));
            return 0;
        } catch (IOException ex) {
            Terminal.errPrintln(Terminal.TermColor.RED, "An error occurred while trying to read the App Information file", true);
            return 1;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
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
        helpPage.addArg("-i", null, "Displays the current build image");
        helpPage.addArg("-I", null, "Displays the current build image, but more readable");

        return "Displays the current Application Version and Information.\n\nOptions:\n" + helpPage.display();
    }

}
