package net.bc100dev.osintgram4j.cmd.conn;

import osintgram4j.api.Command;
import osintgram4j.commons.ShellConfig;

import java.util.List;

public class ReadFollowers extends Command {

    @Override
    public int launchCmd(String[] args, List<ShellConfig> shellConfigs) {
        return 0;
    }

    @Override
    public String helpCmd(String[] args) {
        return """
                Simple Return Page""";
    }

}
