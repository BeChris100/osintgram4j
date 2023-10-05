package net.bc100dev.osintgram4j;

import net.bc100dev.commons.utils.io.FileUtil;
import net.bc100dev.osintgram4j.files.FileWorkerOutputType;
import net.bc100dev.osintgram4j.pcl.PCL;
import net.bc100dev.osintgram4j.pcl.PCLException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;

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

    private static File checkFile(File file) throws IOException {
        if (file == null)
            return null;

        if (!file.exists())
            throw new FileNotFoundException("File at \"" + file.getAbsolutePath() + "\" not found");

        if (!file.canRead())
            throw new AccessDeniedException("Cannot read file at at \"" + file.getAbsolutePath() + "\"");

        return file;
    }

    private static File overrideUserCredentials() {
        if (FileUtil.exists(".config")) {
            try {
                List<String> listStr = FileUtil.listDirectory(".config", false, true, false);

                for (String itemStr : listStr) {
                    if (itemStr.endsWith("credentials.json"))
                        return checkFile(new File(".config/credentials.json"));

                    if (itemStr.endsWith("credentials.cfg"))
                        return checkFile(new File(".config/credentials.cfg"));

                    if (itemStr.endsWith("credentials.ini"))
                        return checkFile(new File(".config/credentials.ini"));
                }
            } catch (IOException ignore) {
                return null;
            }
        }

        return null;
    }

    public static void main(String[] args) {
        String target = null;
        FileWorkerOutputType fileType = null;
        File output = new File("profiles/.default/worker");
        boolean pclCredPrompt = true;

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

        File userCred = overrideUserCredentials();
        if (userCred != null)
            pclCredPrompt = false;

        try {
            PCL pcl = new PCL();
            if (!pclCredPrompt)
                // This is not my password, so do not try to log into my account.
                pcl.connect("bechris100", "hfIUhG6rg8ih512vhiDsLJf1µ3");

            pcl.launch();
        } catch (IOException | PCLException ex) {
            ex.printStackTrace();
        }
    }

}
