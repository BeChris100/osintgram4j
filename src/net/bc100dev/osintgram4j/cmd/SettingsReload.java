package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.osintgram4j.pcl.PCLConfig;

import java.util.List;

public class SettingsReload {

    // Invoked manually by `Method.invoke`
    public static int launchCmd(String[] args, List<PCLConfig> pclConfigs) {
        return 0;
    }

    // Invoked manually by `Method.invoke`
    public static String helpCmd(String[] args) {
        return """
                Reloads the Settings file by reading the "AppSettings.cfg" file and
                reading them into the Application Memory Heap.""";
    }

}
