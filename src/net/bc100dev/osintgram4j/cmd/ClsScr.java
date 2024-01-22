package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.osintgram4j.sh.ShellConfig;

import java.util.List;

public class ClsScr {

    // Invoked manually by `Method.invoke`
    public static int launchCmd(String[] args, List<ShellConfig> shellConfigs) {
        System.out.print("\033\143");
        System.out.flush();
        return 0;
    }

    // Invoked manually by `Method.invoke`
    public static String helpCmd(String[] args) {
        return """
                Clears the Terminal Screen, just like `clear` command on Linux/macOS or `cls` on Windows.""";
    }

}
