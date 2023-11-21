package net.bc100dev.osintgram4j.cmd;

import net.bc100dev.commons.Terminal;
import net.bc100dev.osintgram4j.HelpPage;
import net.bc100dev.osintgram4j.sh.ShellConfig;

import java.util.List;

import static net.bc100dev.commons.utils.SizeConvert.byteCountSI;

public class AppRuntime {

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

        Terminal.println(null, str, false);

        return 0;
    }

    public static int launchCmd(String[] args, List<ShellConfig> shellConfigs) {
        Runtime rn = Runtime.getRuntime();

        Terminal.println(Terminal.TermColor.YELLOW, "Note: this is the current Application runtime, not system information", true);

        if (args == null || args.length == 0)
            return defaultInvoke(rn);

        boolean bMaxMem = false,
                bTotalMem = false,
                bFreeMem = false,
                bAllocMem = false;

        boolean invokeGc = false;

        for (String arg : args) {
            switch (arg) {
                case "-gc" -> invokeGc = true;
                case "-mx" -> bMaxMem = true;
                case "-mf" -> bFreeMem = true;
                case "-mt" -> bTotalMem = true;
                case "-mc" -> bAllocMem = true;
                case "-h", "--help", "?" -> {
                    Terminal.println(Terminal.TermColor.CYAN, helpCmd(), true);
                    return 0;
                }
            }
        }

        if (bMaxMem)
            Terminal.println(null, "Maximum memory: " + maxMem(rn), false);

        if (bFreeMem)
            Terminal.println(null, "Free memory: " + freeMem(rn), false);

        if (bTotalMem)
            Terminal.println(null, "Total memory: " + totalMem(rn), false);

        if (bAllocMem)
            Terminal.println(null, "Allocated memory: " + allocMem(rn), false);

        if (invokeGc) {
            Terminal.println(Terminal.TermColor.YELLOW, "Invoking GC...", true);
            rn.gc();
        }

        return 0;
    }

    // Invoked manually by `Method.invoke`
    public static String helpCmd() {
        HelpPage helpPage = new HelpPage();
        helpPage.setSpaceWidth(5);
        helpPage.addArg("-gc", null, "Invoke the Garbage Collector");
        helpPage.addArg("-mx", null, "Displays maximum memory");
        helpPage.addArg("-mf", null, "Displays free memory");
        helpPage.addArg("-mt", null, "Displays total memory");
        helpPage.addArg("-mc", null, "Displays currently allocated memory");

        return "Displays the current Application Java Runtime.\n\nOptions:\n" + helpPage.display();
    }

}
