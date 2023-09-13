package net.bc100dev.osintgram4j;

import net.bc100dev.osintgram4j.files.FileWorkerOutputType;

import java.io.File;

public class MainClass {

    private static void usage() {
        System.out.println("Osintgram4Java v1.0");
        System.out.println();
        System.out.println("usage:");
        System.out.println("$ ./osintgram4j");
        System.out.println();
        System.out.println("Refer to the README.md and USAGE.md files on GitHub at");
        System.out.println("https://github.com/BeChris100/Osintgram to have a better overview on using the PCL");
        System.out.println("(Process-Command Line).");
    }

    public static void main(String[] args) {
        String target = null;
        FileWorkerOutputType fileType = null;
        File output = new File("profiles/.default/.worker");
        boolean defNewType = false, csvWrite = false;

        if (args.length >= 1) {
            for (String arg : args) {
                if (arg.equals("-h") || arg.equals("--help")) {
                    usage();
                    System.exit(0);
                    return;
                }

                if (target != null) {
                    System.err.println("Only one target can be used. To use more than one targets,");
                    System.err.println("use the Process CLI by typing \"&target = [new_target]\".");
                    System.exit(1);
                    return;
                }

                target = arg;
            }
        }
    }

}
