package net.bc100dev.osintgram4j;

import net.bc100dev.osintgram4j.sh.Shell;
import net.bc100dev.osintgram4j.sh.ShellException;

import java.io.IOException;

public class MainClass {

    private static void usage(ProcessHandle ph) {
        System.out.println(TitleBlock.TITLE_BLOCK());
        System.out.println();
        System.out.println("usage:");
        System.out.println("$ " + ph.info().command().orElse("./osintgram4j") + " [options]");
        System.out.println();
        System.out.println("options:");
        System.out.println("-h, --help          Display this message and exit");
        System.out.println();
        System.out.println("Refer to the README.md and USAGE.md files on GitHub at");
        System.out.println("https://github.com/BeChris100/osintgram4j to have a better overview on using the Application Shell.");
    }

    public static void main(String[] args) {
        System.out.println("Classpath: " + System.getProperty("java.class.path"));

        if (args.length >= 1) {
            switch (args[0]) {
                case "-h", "--help", "-help", "?" -> {
                    usage(ProcessHandle.current());
                    System.exit(0);
                    return;
                }
            }
        }

        try {
            Shell pcl = new Shell();
            pcl.launch();
        } catch (IOException | ShellException ex) {
            ex.printStackTrace(System.err);
        }
    }

}
