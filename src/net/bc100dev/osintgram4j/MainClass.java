package net.bc100dev.osintgram4j;

import net.bc100dev.commons.ResourceManager;
import net.bc100dev.insta.api.privates.InstagramClient;
import net.bc100dev.osintgram4j.files.FileWorkerOutputType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainClass {

    private static String titleBlock() {
        try {
            ResourceManager mgr = new ResourceManager(MainClass.class, true);
            if (mgr.resourceExists("titleblock.txt")) {
                InputStream is = mgr.getResourceInputStream("titleblock.txt");
                StringBuilder str = new StringBuilder();
                int d;
                byte[] b = new byte[1024];

                while ((d = is.read(b, 0, 1024)) != -1)
                    str.append(new String(b, 0, d));

                is.close();

                return str.toString();
            } else
                return "OSINTgram v1.0";
        } catch (IOException ignore) {
            return "OSINTgram v1.0";
        }
    }

    private static void usage() {
        System.out.println(titleBlock());
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
        File output = new File("profiles/.default/worker");

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
