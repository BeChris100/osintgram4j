package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.osintgram4j.sh.ShellConfig;

import java.util.List;

public class EnvCmd {

    // Invoked manually by `Method.invoke`
    public static int launchCmd(String[] ignore, List<ShellConfig> configs) {
        if (configs.isEmpty())
            return 0;

        for (ShellConfig config : configs)
            System.out.println(config.getName() + " = " + config.getValue());

        return 0;
    }

    // Invoked manually by `Method.invoke`
    public static String helpCmd() {
        return """
                Environment command""";
    }

}
