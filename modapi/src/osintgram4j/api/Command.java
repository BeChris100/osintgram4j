package osintgram4j.api;

import osintgram4j.api.sh.ShellConfig;

import java.util.List;

public abstract class Command {

    public abstract int launchCmd(String[] args, List<ShellConfig> env);

    public abstract String helpCmd(String[] args);

}
