package net.bc100dev.osintgram4j.settings;

import net.bc100dev.commons.Terminal;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SettingsConstants {

    public static class Application {

        public static boolean ALLOW_CACHE = true;
        public static String CACHE_EXPIRATION_TIME = "3d";

    }

    public static class ColorTheme {

        public static Terminal.TermColor HELP_BLOCK = Terminal.TermColor.BLUE;
        public static Terminal.TermColor COMMAND_INPUT = Terminal.TermColor.RESET;
        public static Terminal.TermColor ERROR = Terminal.TermColor.RED;
        public static Terminal.TermColor WARNING = Terminal.TermColor.YELLOW;
        public static Terminal.TermColor VERBOSE = Terminal.TermColor.BLACK;
        public static Terminal.TermColor INFO = Terminal.TermColor.PURPLE;

    }

    public static class Logger {

        public static boolean LOGGING_ENABLED = false;
        public static final List<String> DISPLAY_LOG_LEVELS = new ArrayList<>();
        public static final List<String> WRITER_LOG_LEVELS = new ArrayList<>();
        public static File FILE_LOCATION;

        public static File getDefaultFileLocation() {
            Calendar cal = Calendar.getInstance();
            String fileName = String.format("osintgram4j-%d%d%d_%d%d%d.txt",
                    cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR),
                    cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

            return new File(fileName);
        }

    }

    public static class Network {

        public static long CONNECTION_TIMEOUT = 30000;
        public static long READ_TIMEOUT = 20000;

    }

}
