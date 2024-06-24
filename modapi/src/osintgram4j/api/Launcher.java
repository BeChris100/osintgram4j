package osintgram4j.api;

public abstract class Launcher {

    public abstract void onLaunch(String[] cliArgs);

    public abstract void onExit(String reason);

}
