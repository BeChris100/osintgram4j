package net.bc100dev.osintgram4j.cmd;

import osintgram4j.api.Command;
import osintgram4j.api.sh.ShellConfig;

import java.util.List;

public class ClsScr extends Command {

    @Override
    public int launchCmd(String[] args, List<ShellConfig> shellConfigs) {
        System.out.print("\033\143");
        System.out.flush();
        return 0;
    }

    @Override
    public String helpCmd(String[] args) {
        return """
                Clears the Terminal Screen, just like `clear` command on Linux/macOS or `cls` on Windows.""";
    }

}
