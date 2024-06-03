package net.bc100dev.osintgram4j.cmd.conn;

import osintgram4j.api.sh.Command;
import osintgram4j.commons.ShellConfig;

import java.util.List;

public class ReadFollowersP extends Command {

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
