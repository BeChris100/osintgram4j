package net.bc100dev.commons.utils;

import net.bc100dev.commons.utils.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.FileSystems;
import java.util.List;

public class RuntimeEnvironment {

    public static final String OS_NAME = System.getProperty("os.name");
    public static final String OS_ARCH = System.getProperty("os.arch");
    public static final String OS_VERSION = System.getProperty("os.version");
    public static final String PATH_SEPARATOR = File.pathSeparator;
    public static final File WORKING_DIRECTORY = new File(System.getProperty("user.dir"));
    public static final File USER_HOME = new File(System.getProperty("user.home"));
    public static final String USER_NAME = System.getProperty("user.name");
    public static final String HOST_NAME = System.getProperty("host.name");
    public static final String SYSTEM_LINE_SEPARATOR = System.lineSeparator();
    public static final String JAVA_VERSION = System.getProperty("java.version");
    public static final String JAVA_VENDOR_URL = System.getProperty("java.vendor.url");
    public static final String JAVA_VENDOR = System.getProperty("java.vendor");
    public static final File JAVA_HOME = new File(System.getProperty("java.home"));
    public static final String JAVA_HOME_ENV = System.getenv("JAVA_HOME");
    public static final File CLASS_PATH = new File(System.getProperty("java.class.path"));
    public static final String SYSTEM_PATH_SEPARATOR = FileSystems.getDefault().getSeparator();

    public static final String PATH_ENV = System.getenv("PATH");

    public static int countRuntimeModifications() {
        List<String> modifications = ManagementFactory.getRuntimeMXBean().getInputArguments();

        int items = 0;

        for (String modification : modifications) {
            if (modification.startsWith("-Dos.name=") ||
                    modification.startsWith("-Dos.arch=") ||
                    modification.startsWith("-Dos.version=") ||
                    modification.startsWith("-Duser.name=") ||
                    modification.startsWith("-Duser.home="))
                items++;
        }

        return items;
    }

    public static boolean osNameModified() {
        List<String> modifications = ManagementFactory.getRuntimeMXBean().getInputArguments();

        for (String mod : modifications) {
            if (mod.startsWith("-Dos.name="))
                return true;
        }

        return false;
    }

    public static boolean osArchModified() {
        List<String> modifications = ManagementFactory.getRuntimeMXBean().getInputArguments();

        for (String mod : modifications) {
            if (mod.startsWith("-Dos.arch="))
                return true;
        }

        return false;
    }

    public static boolean osVersionModified() {
        List<String> modifications = ManagementFactory.getRuntimeMXBean().getInputArguments();

        for (String mod : modifications) {
            if (mod.startsWith("-Dos.version="))
                return true;
        }

        return false;
    }

    public static boolean isLinux() {
        String os = OS_NAME.toLowerCase();
        return os.contains("nix") || os.contains("nux") || os.contains("aix");
    }

    public static boolean isWindows() {
        return OS_NAME.toLowerCase().contains("win");
    }

    public static boolean isMac() {
        return OS_NAME.toLowerCase().contains("mac");
    }

    @Deprecated(forRemoval = true)
    public static boolean isSunOs() {
        return OS_NAME.toLowerCase().contains("sunos");
    }

    public static String getHostName() {
        if (HOST_NAME != null)
            return HOST_NAME;
        else {
            if (isLinux() || isMac()) {
                try {
                    if (FileUtil.exists("/etc/hostname"))
                        return FileUtil.readFileString("/etc/hostname").trim();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }

    public static OperatingSystem getOperatingSystem() {
        if (isWindows())
            return OperatingSystem.WINDOWS;
        else if (isLinux())
            return OperatingSystem.LINUX;
        else if (isMac())
            return OperatingSystem.MAC_OS;
        else
            throw new RuntimeException("Unsupported operating system");
    }

    public static String getOperatingSystemLabel(OperatingSystem os) {
        return switch (os) {
            case WINDOWS -> "Windows";
            case MAC_OS -> "macOS";
            case LINUX -> "Linux";
        };
    }

}
