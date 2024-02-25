package net.bc100dev.osintgram4j.cmd;

import osintgram4j.api.Command;
import osintgram4j.commons.ShellConfig;

import java.util.List;

public class EnvCmd extends Command {

    @Override
    public int launchCmd(String[] ignore, List<ShellConfig> configs) {
        if (configs.isEmpty())
            return 0;

        for (ShellConfig config : configs)
            System.out.println(config.getName() + " = " + config.getValue());

        return 0;
    }

    @Override
    public String helpCmd(String[] args) {
        return """
                Environment command""";
    }

}
