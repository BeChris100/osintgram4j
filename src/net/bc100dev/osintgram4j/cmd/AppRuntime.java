package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.utils.HelpPage;
import net.bc100dev.osintgram4j.NativeLoader;
import osintgram4j.api.sh.Command;
import osintgram4j.commons.ShellEnvironment;

import java.util.List;

import static net.bc100dev.commons.utils.RuntimeEnvironment.isMac;
import static net.bc100dev.commons.utils.SizeConvert.byteCountSI;

public class AppRuntime extends Command {

    private static native long sysTotalMemory();

    private static native long sysAvailableMemory();

    private static native long sysFreeMemory();

    private static String maxMem(Runtime runtime) {
        return byteCountSI(runtime.maxMemory());
    }

    private static String totalMem(Runtime runtime) {
        return byteCountSI(runtime.maxMemory());
    }

    private static String freeMem(Runtime runtime) {
        return byteCountSI(runtime.freeMemory());
    }

    private static String allocMem(Runtime runtime) {
        return byteCountSI(runtime.totalMemory() - runtime.freeMemory());
    }

    private static int defaultInvoke(Runtime runtime) {
        String str = "Maximum Memory: " + maxMem(runtime) + "\n" +
                "Free Memory: " + freeMem(runtime) + "\n" +
                "Total Memory: " + totalMem(runtime) + "\n" +
                "Allocated Memory: " + allocMem(runtime);

        if (NativeLoader.isLoaded()) {
            str += "\n" +
                    "System Total Memory: " + byteCountSI(sysTotalMemory()) + "\n" +
                    "System Available Memory: " + byteCountSI(sysAvailableMemory()) + "\n" +
                    "System Free Memory: " + byteCountSI(sysFreeMemory());
        }

        Terminal.println(null, str, false);

        return 0;
    }

    @Override
    public int launchCmd(String[] args, List<ShellEnvironment> shellConfigs) {
        Runtime rn = Runtime.getRuntime();

        if (args == null || args.length == 0)
            return defaultInvoke(rn);

        boolean bMaxMem = false,
                bTotalMem = false,
                bFreeMem = false,
                bAllocMem = false,
                bSysTotalMem = false,
                bSysAvailableMem = false,
                bSysFreeMem = false;

        boolean invokeGc = false;

        for (String arg : args) {
            switch (arg) {
                case "-gc" -> invokeGc = true;
                case "-mx" -> bMaxMem = true;
                case "-mf" -> bFreeMem = true;
                case "-mt" -> bTotalMem = true;
                case "-mc" -> bAllocMem = true;
                case "-m" -> {
                    bMaxMem = true;
                    bFreeMem = true;
                    bTotalMem = true;
                    bAllocMem = true;
                }
                case "-st" -> bSysTotalMem = true;
                case "-sa" -> bSysAvailableMem = true;
                case "-sf" -> bSysFreeMem = true;
                case "-s" -> {
                    bSysTotalMem = true;
                    bSysAvailableMem = true;
                    bSysFreeMem = true;
                }
                case "-h", "--help", "?" -> {
                    Terminal.println(Terminal.TermColor.CYAN, helpCmd(args), true);
                    return 0;
                }
            }
        }

        if (bTotalMem)
            Terminal.println(null, "Total memory: " + totalMem(rn), false);

        if (bMaxMem)
            Terminal.println(null, "Maximum memory: " + maxMem(rn), false);

        if (bAllocMem)
            Terminal.println(null, "Allocated memory: " + allocMem(rn), false);

        if (bFreeMem)
            Terminal.println(null, "Free memory: " + freeMem(rn), false);

        if (bSysTotalMem || bSysAvailableMem || bSysFreeMem) {
            if (NativeLoader.isLoaded()) {
                Terminal.println(Terminal.TermColor.YELLOW, "System Memory Info: some values are not 100% accurate", true);

                if (bSysTotalMem)
                    Terminal.println(null, "System Total Memory: " + byteCountSI(sysTotalMemory()), false);

                if (bSysAvailableMem)
                    Terminal.println(null, "System Available Memory: " + byteCountSI(sysAvailableMemory()), false);

                if (bSysFreeMem)
                    Terminal.println(null, "System Free Memory: " + byteCountSI(sysFreeMemory()), false);
            } else
                Terminal.errPrintln(Terminal.TermColor.RED, "Cannot retrieve System Memory Information: library not loaded", true);
        }

        if (invokeGc) {
            Terminal.println(Terminal.TermColor.YELLOW, "Invoking GC...", true);
            rn.gc();
        }

        return 0;
    }

    @Override
    public String helpCmd(String[] args) {
        HelpPage helpPage = new HelpPage();
        helpPage.setSpaceWidth(5);
        helpPage.addArg("-gc", null, "Invoke the Garbage Collector");
        helpPage.addArg("-mx", null, "Displays maximum memory");
        helpPage.addArg("-mf", null, "Displays free memory");
        helpPage.addArg("-mt", null, "Displays total memory");
        helpPage.addArg("-mc", null, "Displays currently allocated memory");
        helpPage.addArg("-m", null, "Displays the current Application Runtime Memory");

        if (!isMac()) {
            helpPage.addArg("-st", null, "Displays the System Total Memory");
            helpPage.addArg("-sa", null, "Displays the System Available Memory");
            helpPage.addArg("-sf", null, "Displays the System Free Memory (not 100% accurate)");
            helpPage.addArg("-s", null, "Displays the current System Memory state");
        }

        return "Displays the current Application Java Runtime.\n\nOptions:\n" + helpPage.display();
    }

}
