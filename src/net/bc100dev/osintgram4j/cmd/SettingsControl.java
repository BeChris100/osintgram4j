package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import osintgram4j.api.sh.Command;
import osintgram4j.commons.ShellEnvironment;

import java.util.List;

public class SettingsControl extends Command {

    // TODO: put Settings control into AppManager

    @Override
    public int launchCmd(String[] args, List<ShellEnvironment> shellConfigs) {
        Terminal.errPrintln(Terminal.TermColor.RED, "settings control: not implemented yet", true);
        Terminal.errPrintln(Terminal.TermColor.RED, "settings control: deprecated (use 'clientmgr --settings' instead)", true);
        return 1;
    }

    @Override
    public String helpCmd(String[] args) {
        return """
                Reloads the Settings file by reading the "AppSettings.cfg" file and
                reading them into the Application Memory Heap.""";
    }

}
