package net.bc100dev.osintgram4j.cmd;

import osintgram4j.api.Command;
import osintgram4j.api.sh.ShellConfig;

import java.util.List;

public class SettingsControl extends Command {

    @Override
    public int launchCmd(String[] args, List<ShellConfig> shellConfigs) {
        return 0;
    }

    @Override
    public String helpCmd(String[] args) {
        return """
                Reloads the Settings file by reading the "AppSettings.cfg" file and
                reading them into the Application Memory Heap.""";
    }

}
