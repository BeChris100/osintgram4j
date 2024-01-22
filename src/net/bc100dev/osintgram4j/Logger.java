package net.bc100dev.osintgram4j;

import net.bc100dev.commons.ApplicationException;
import net.bc100dev.commons.ApplicationRuntimeException;
import net.bc100dev.commons.utils.Utility;
import net.bc100dev.commons.utils.io.FileUtil;
import net.bc100dev.osintgram4j.settings.TimeFormats;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Properties;

public class Logger {

    private static FileOutputStream fos;

    public static void open(File file) {
        try {
            if (file == null)
                throw new NullPointerException("File Pointer cannot be null");

            if (!file.exists())
                FileUtil.createFile(file.getAbsolutePath(), true);

            if (!file.canWrite())
                throw new AccessDeniedException("The log file at \"" + file.getAbsolutePath() + "\" needs to be writable (Permission denied)");

            Logger.fos = new FileOutputStream(file);
        } catch (IOException e) {
            throw new ApplicationRuntimeException(e);
        }
    }

    public static void writeLogEntry(LogType type, String tag, String message) {
        if (fos == null)
            throw new NullPointerException("File not opened, or an error encountered, while opening the File");


    }

    public static void info(Class<?> tag, String message) {
        writeLogEntry(LogType.INFO, tag.getName(), message);
    }

    public static void error(Class<?> tag, String message) {
        writeLogEntry(LogType.ERROR, tag.getName(), message);
    }

    public static void warn(Class<?> tag, String message) {
        writeLogEntry(LogType.WARN, tag.getName(), message);
    }

    public static void debug(Class<?> tag, String message) {
        writeLogEntry(LogType.DEBUG, tag.getName(), message);
    }

    public static void verbose(Class<?> tag, String message) {
        writeLogEntry(LogType.VERBOSE, tag.getName(), message);
    }

    public static void info(String tag, String message) {
        writeLogEntry(LogType.INFO, tag, message);
    }

    public static void error(String tag, String message) {
        writeLogEntry(LogType.ERROR, tag, message);
    }

    public static void warn(String tag, String message) {
        writeLogEntry(LogType.WARN, tag, message);
    }

    public static void debug(String tag, String message) {
        writeLogEntry(LogType.DEBUG, tag, message);
    }

    public static void verbose(String tag, String message) {
        writeLogEntry(LogType.VERBOSE, tag, message);
    }

    public static class LogSettings {

        public static boolean enabled = true;

        public static boolean infoLoggable = true, errorLoggable = true,
                warnLoggable = true, debugLoggable = true, verboseLoggable = true;

        public static int maxFileLogs = 15;

        public static String fileNameFormat = "LogFile-[date]-[time].txt",
                entryFormat = "[type] [date]-[time] ([tag]): [message]";

        public static void loadSettings(Properties props) throws ApplicationException {
            if (props == null)
                throw new NullPointerException("Properties Pointer is null");

            if (!props.containsKey("Logging.Enabled"))
                throw new ApplicationException("\"Logging.Enabled\" parameter in the settings is missing");

            if (!props.containsKey("Logging.Levels"))
                throw new ApplicationException("\"Logging.Levels\" parameter in the settings is missing");

            if (!props.containsKey("Logging.FileNamingScheme"))
                throw new ApplicationException("\"Logging.FileNamingScheme\" parameter in the settings is missing");

            if (!props.containsKey("Logging.MaxFileLogs"))
                throw new ApplicationException("\"Logging.MaxFileLogs\" parameter in the settings is missing");

            enabled = Utility.convertToBoolean(props.getProperty("Logging.Enabled"), true);
            fileNameFormat = props.getProperty("Logging.FileNamingScheme");
            entryFormat = props.getProperty("Logging.EntryFormat");

            String lvlValue = props.getProperty("Logging.Levels");
            if (lvlValue.equalsIgnoreCase("none"))
                enabled = false;
            else {
                String[] splits;
                if (lvlValue.contains(","))
                    splits = lvlValue.split(",");
                else if (lvlValue.contains(":"))
                    splits = lvlValue.split(":");
                else if (lvlValue.contains(";"))
                    splits = lvlValue.split(";");
                else if (lvlValue.contains("|"))
                    splits = lvlValue.split("\\|");
                else
                    splits = new String[]{lvlValue};

                if (splits.length == 0)
                    return;

                boolean tglInfo = false,
                        tglErr = false,
                        tglWarn = false,
                        tglDbg = false,
                        tglVrb = false;

                for (String spl : splits) {
                    switch (spl) {
                        case "inf", "info" -> {
                            infoLoggable = true;
                            tglInfo = true;
                        }
                        case "err", "error" -> {
                            errorLoggable = true;
                            tglErr = true;
                        }
                        case "wrn", "warn", "warning", "warnings" -> {
                            warnLoggable = true;
                            tglWarn = true;
                        }
                        case "dbg", "debug", "debugs" -> {
                            debugLoggable = true;
                            tglDbg = true;
                        }
                        case "vrb", "verb", "verbose" -> {
                            verboseLoggable = true;
                            tglVrb = true;
                        }
                    }
                }

                if (!tglInfo)
                    infoLoggable = false;

                if (!tglErr)
                    errorLoggable = false;

                if (!tglWarn)
                    warnLoggable = false;

                if (!tglDbg)
                    debugLoggable = false;

                if (!tglVrb)
                    verboseLoggable = false;
            }

            try {
                long lV = Long.parseLong(props.getProperty("Logging.MaxFileLogs"));
                if (lV > Integer.MAX_VALUE)
                    throw new ApplicationException("\"Logging.MaxFileLogs\": above the integer maximum limit");

                maxFileLogs = (int) lV;
            } catch (NumberFormatException ignore) {
            }
        }
    }

    public enum LogType {
        INFO,
        ERROR,
        WARN,
        DEBUG,
        VERBOSE
    }

}
