package net.bc100dev.osintgram4j.cmd;

import osintgram4j.api.sh.Shell;
import osintgram4j.api.sh.Command;
import osintgram4j.api.sh.ShellEnvironment;

import java.util.List;

public class EnvCmd extends Command {

    @Override
    public int launchCmd(String[] args, List<ShellEnvironment> configs) {
        if (configs.isEmpty())
            return 0;

        if (args != null && args.length >= 1) {
            switch (args[0]) {
                case "-r", "--remove" -> {
                    if (args.length == 1) {
                        System.err.println("no key value provided");
                        return 1;
                    }

                    Shell shell = Shell.getInstance();

                    for (int i = 1; i < args.length; i++) {
                        int index = -1;

                        for (int I = 0; I < configs.size(); I++) {
                            ShellEnvironment config = configs.get(I);
                            if (config.getName().equals(args[i])) {
                                index = I;
                                break;
                            }
                        }

                        if (index != -1) {
                            shell.shellEnvList.remove(index);
                            System.out.println("\"" + args[i] + "\" successfully deleted");
                        }
                    }
                }
            }
        }

        for (ShellEnvironment config : configs)
            System.out.println(config.getName() + " = " + config.getValue());

        return 0;
    }

    @Override
    public String helpCmd(String[] args) {
        return """
                Environment command
                
                Options:
                -r | --remove key1 (key2 ...)      Removes one or more application environment variables""";
    }

}
