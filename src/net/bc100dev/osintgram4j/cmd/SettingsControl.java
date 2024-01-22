package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.osintgram4j.sh.ShellConfig;

import java.util.List;

public class SettingsControl {

    // Invoked manually by `Method.invoke`
    public static int launchCmd(String[] args, List<ShellConfig> shellConfigs) {
        return 0;
    }

    // Invoked manually by `Method.invoke`
    public static String helpCmd(String[] args) {
        return """
                Reloads the Settings file by reading the "AppSettings.cfg" file and
                reading them into the Application Memory Heap.""";
    }

}
