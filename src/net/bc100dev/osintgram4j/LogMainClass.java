package net.bc100dev.osintgram4j;

import net.bc100dev.commons.utils.io.FileEncryption;
import net.bc100dev.commons.utils.io.FileUtil;
import org.apache.commons.cli.*;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Scanner;

import static net.bc100dev.commons.utils.RuntimeEnvironment.USER_HOME;
import static net.bc100dev.commons.utils.RuntimeEnvironment.getOperatingSystem;

public class LogMainClass {

    private static Lookup lookup = Lookup.DEFAULT;

    private static void execute(String pass, TypeAction action, File file) {
        boolean isEncrypted = false;
        String finPass = pass;

        if (action == TypeAction.Open || action == TypeAction.Decrypt) {
            if (!file.exists()) {
                System.err.println("File \"" + file.getAbsolutePath() + "\" does not exist.");
                System.exit(1);
            }

            if (!file.canRead()) {
                System.err.println("File \"" + file.getAbsolutePath() + "\" is not readable, or cannot be opened by the current user.");
                System.exit(1);
            }

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buff = new byte[1024];
                int len;
                StringBuilder strBd = new StringBuilder();

                while ((len = fis.read(buff, 0, 1024)) > 0)
                    strBd.append(new String(buff, 0, len));

                String str = strBd.toString();
                if (!(str.contains("INFO") || str.contains("OFF") ||
                        str.contains("SEVERE") || str.contains("WARNING") || str.contains("ALL") ||
                        str.contains("CONFIG") || str.contains("FINE") || str.contains("FINER") || str.contains("FINEST"))) {
                    if (!str.contains("Osintgram4j Log Data")) {
                        if (!str.trim().isEmpty())
                            isEncrypted = true;
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }

        if (action == TypeAction.Open || action == TypeAction.Decrypt || action == TypeAction.Write_Encrypted) {
            if (pass.equalsIgnoreCase("ask")) {
                Console console = System.console();
                Scanner sc = new Scanner(System.in);

                System.out.print("Enter password: ");

                if (console != null)
                    finPass = new String(console.readPassword());
                else
                    finPass = sc.nextLine().trim();
            }
        }

        File og4jLogFile = switch (getOperatingSystem()) {
            case LINUX -> new File(USER_HOME.getAbsolutePath() + "/.config/net.bc100dev/osintgram4j/log.txt");
            case WINDOWS -> new File(USER_HOME.getAbsolutePath() + "\\AppData\\Local\\BC100Dev\\Osintgram4j\\log.txt");
            case MAC_OS -> new File(USER_HOME.getAbsolutePath() + "/Library/net.bc100dev/osintgram4j/log.txt");
        };

        File og4jNetLogFile = switch (getOperatingSystem()) {
            case LINUX -> new File(USER_HOME.getAbsolutePath() + "/.config/net.bc100dev/osintgram4j/net-log.txt");
            case WINDOWS ->
                    new File(USER_HOME.getAbsolutePath() + "\\AppData\\Local\\BC100Dev\\Osintgram4j\\net-log.txt");
            case MAC_OS -> new File(USER_HOME.getAbsolutePath() + "/Library/net.bc100dev/osintgram4j/net-log.txt");
        };

        switch (action) {
            case Decrypt -> {
                if (!isEncrypted) {
                    System.err.println("Cannot decrypt file");
                    System.err.println("Already in plain text");
                    System.exit(1);
                }

                try {
                    byte[] data = FileEncryption.toDecrypted(file, finPass);
                    File outFile = new File(file.getAbsolutePath() + ".log");

                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        fos.write(data);
                    }
                } catch (IOException | GeneralSecurityException ex) {
                    ex.printStackTrace(System.err);
                }
            }
            case Open -> {
                if (isEncrypted) {
                    try {
                        byte[] data = FileEncryption.toDecrypted(file, finPass);
                        System.out.println(new String(data));
                    } catch (IOException | GeneralSecurityException ex) {
                        System.err.println("Not able to decrypt file: " + file.getAbsolutePath());
                        ex.printStackTrace(System.err);
                    }
                } else {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        int len;
                        byte[] buff = new byte[1024];

                        while ((len = fis.read(buff, 0, 1024)) != -1)
                            System.out.println(new String(buff, 0, len));
                    } catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }
            case Write -> {
                File inFile = lookup == Lookup.DEFAULT ? og4jLogFile : og4jNetLogFile;

                try {
                    if (!file.exists())
                        FileUtil.createFile(file.getAbsolutePath(), true);
                } catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }

                try (FileInputStream fis = new FileInputStream(inFile);
                     FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buff = new byte[1024];
                    int len;

                    while ((len = fis.read(buff, 0, 1024)) != -1)
                        fos.write(buff, 0, len);
                } catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }
            case Write_Encrypted -> {
                try {
                    File outFile = new File(file.getAbsolutePath() + ".enc");
                    if (!outFile.exists())
                        FileUtil.createFile(outFile.getAbsolutePath(), true);

                    File inFile = lookup == Lookup.DEFAULT ? og4jLogFile : og4jNetLogFile;

                    byte[] encData = FileEncryption.toEncrypted(inFile, finPass);

                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        fos.write(encData);
                    }
                } catch (IOException | GeneralSecurityException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    public static void main(String[] args) {
        Options opts = new Options();

        Option passOption = new Option("p", "pass", true, "The password used for encrypting the log file (defaults to \"ask\", prompting for password)");
        passOption.setRequired(false);
        opts.addOption(passOption);

        Option logLookupOpt = new Option("l", "lookup", true, "The specific internal log file to look up (passes either \"def\" or \"net\")");
        logLookupOpt.setRequired(false);
        opts.addOption(logLookupOpt);

        Option decryptOption = new Option("d", "decrypt", false, "Decrypt a specific log file");
        decryptOption.setRequired(false);
        opts.addOption(decryptOption);

        Option openOption = new Option("o", "open", false, "Open a specific log file, and print its contents out");
        openOption.setRequired(false);
        opts.addOption(openOption);

        Option writeOption = new Option("w", "write", false, "Copies the specific log file from the Config Directory to the pointed directory");
        writeOption.setRequired(false);
        opts.addOption(writeOption);

        Option writeEncryptedOption = new Option("we", "write-encrypted", false, "Same as \"-w\", but with File Encryption. If \"-p\" is not given, it defaults to asking via prompt.");
        writeEncryptedOption.setRequired(false);
        opts.addOption(writeEncryptedOption);

        Option outputFileOpt = new Option("f", "file", true, "Given to write to a (new) file, or given to read from the file itself");
        writeEncryptedOption.setRequired(true);
        opts.addOption(outputFileOpt);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        String pass = "ask";
        TypeAction action = null;
        File outFile = null;

        try {
            cmd = parser.parse(opts, args);

            if (cmd.hasOption(passOption))
                pass = cmd.getOptionValue(passOption).trim();

            if (cmd.hasOption(decryptOption))
                action = TypeAction.Decrypt;

            if (cmd.hasOption(openOption))
                action = TypeAction.Open;

            if (cmd.hasOption(writeOption))
                action = TypeAction.Write;

            if (cmd.hasOption(writeEncryptedOption))
                action = TypeAction.Write_Encrypted;

            if (cmd.hasOption(outputFileOpt))
                outFile = new File(cmd.getOptionValue(outputFileOpt).trim());

            if (cmd.hasOption(logLookupOpt)) {
                String v = cmd.getOptionValue(logLookupOpt).toLowerCase().trim();

                if (v.isEmpty()) {
                    System.err.println("Value cannot be empty");
                    System.exit(1);
                }

                char c = v.toCharArray()[0];
                if (c == 'd')
                    lookup = Lookup.DEFAULT;
                else if (c == 'n')
                    lookup = Lookup.NETWORK;
                else
                    throw new ParseException("Invalid value \"" + v + "\"");
            }

            if (action == null)
                throw new ParseException("No action given");

            if (outFile == null)
                throw new ParseException("No file given");

            execute(pass, action, outFile);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            formatter.printHelp("og4j-logdata", opts);

            System.exit(1);
        }
    }

    enum Lookup {

        DEFAULT,

        NETWORK

    }

    enum TypeAction {

        Decrypt,

        Open,

        Write,

        Write_Encrypted

    }

}
