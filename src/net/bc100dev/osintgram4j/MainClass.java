package net.bc100dev.osintgram4j;

import net.bc100dev.osintgram4j.sh.Shell;
import net.bc100dev.osintgram4j.sh.ShellException;

import java.io.IOException;

public class MainClass {

    private static void usage() {
        System.out.println(TitleBlock.TITLE_BLOCK());
        System.out.println();
        System.out.println("usage:");
        System.out.println("$ ./osintgram4j");
        System.out.println();
        System.out.println("Refer to the README.md and USAGE.md files on GitHub at");
        System.out.println("https://github.com/BeChris100/osintgram4j to have a better overview on using the Application Shell.");
    }

    public static void main(String[] args) {
        if (args.length >= 1) {
            for (String arg : args) {
                switch (arg) {
                    case "-h", "--help", "-help" -> {
                        usage();
                        System.exit(0);
                        return;
                    }
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
