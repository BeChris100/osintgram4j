package osintgram4j.api;

import osintgram4j.commons.ShellConfig;

import java.util.List;

public abstract class Command {

    public abstract int launchCmd(String[] args, List<ShellConfig> env);

    public abstract String helpCmd(String[] args);

}
