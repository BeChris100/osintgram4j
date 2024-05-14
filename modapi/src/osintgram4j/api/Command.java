package osintgram4j.api;

import osintgram4j.commons.ShellConfig;

import java.util.List;

public abstract class Command {

    private boolean asAlias = false;

    public abstract int launchCmd(String[] args, List<ShellConfig> env);

    public abstract String helpCmd(String[] args);

    public void setExecutionAsAlias(boolean asAlias) {
        this.asAlias = asAlias;
    }

    public boolean isExecutedAsAlias() {
        return asAlias;
    }
}
