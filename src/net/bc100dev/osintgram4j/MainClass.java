package net.bc100dev.osintgram4j;

import net.bc100dev.osintgram4j.pcl.PCL;
import net.bc100dev.osintgram4j.pcl.PCLException;

public class MainClass {

    private static void usage() {
        System.out.println(TitleBlock.TITLE_BLOCK());
        System.out.println();
        System.out.println("usage:");
        System.out.println("$ ./osintgram4j");
        System.out.println();
        System.out.println("Refer to the README.md and USAGE.md files on GitHub at");
        System.out.println("https://github.com/BeChris100/Osintgram to have a better overview on using the PCL");
        System.out.println("(Process-Command Line).");
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
            PCL pcl = new PCL();
            pcl.launch();
        } catch (PCLException ex) {
            ex.printStackTrace(System.err);
        }
    }

}
