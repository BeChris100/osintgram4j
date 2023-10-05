package net.bc100dev.osintgram4j.pcl;

import net.bc100dev.commons.CLIParser;
import net.bc100dev.commons.TerminalColors;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The PCL (Process Command Line) is an interactive shell, used for
 * interacting with the Instagram Private APIs, along with the interaction
 * of the official and publicly available Instagram Graph API.
 */
public class PCL {

    private final Scanner scIn;

    private boolean connected = false;

    private final List<PCLConfig> pclConfigList;
    private final List<PCLCaller> pclCallers;

    // Instagram Connection Streams
    private DataInputStream connectionInput, connectionError;
    private DataOutputStream connectionOutput;

    public PCL() throws PCLException {
        this.scIn = new Scanner(System.in);
        this.pclConfigList = new ArrayList<>();

        this.pclCallers = new ArrayList<>();
        pclCallers.add(new PCLCaller("connect", "Connects the User to the Instagram APIs", new String[]{
                "&username",
                "&password",
                "&mfa"
        }, "net.bc100dev.osintgram4j.cmd.Connect"));
    }

    /**
     * Checks, if we have made a connection to the Instagram API.
     *
     * @return Returns true, if the connection is successful.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Verifies, if the connection that we made to Instagram is still
     * online, and verifies its current connection.
     *
     * @return Returns true, if the connection still can be established,
     * otherwise returns false, if an Exception occurs
     * @throws IOException  Throws an IO (Input/Output) Error (Exception),
     *                      when an internal Connection fails.
     * @throws PCLException Throws a PCL Exception, when an either known
     *                      or unknown error from Instagram occurs.
     */
    public boolean verifyConnection() throws IOException, PCLException {
        return false;
    }

    private void connect0(String username, String password, String mfaCode) throws IOException, PCLException {
    }

    /**
     * Connects to the Instagram APIs, establishing a connection. To check
     * for a successful connection, use {@link PCL#isConnected()} to return
     * the connection status
     *
     * @throws IOException Input/Output Error Streams
     * @throws PCLException Process Command Line Errors, including API Connection errors etc.
     */
    public void connect(String username, String password) throws IOException, PCLException {
    }

    public void connect(String username, String password, String mfaCode) throws IOException, PCLException {
    }

    private void assignCfg(String ln) {
        if (ln.contains("=")) {
            String[] opt = ln.split("=", 2);
            opt[0] = opt[0].trim().replaceFirst("&", "");
            opt[1] = opt[1].trim();

            if (pclConfigList.isEmpty()) {
                pclConfigList.add(new PCLConfig(opt[0], opt[1]));
                System.out.printf("Created %s with value \"%s\"\n", opt[0], opt[1]);
                return;
            }

            boolean found = false;
            for (int i = 0; i < pclConfigList.size(); i++) {
                PCLConfig cfg = pclConfigList.get(i);

                if (opt[0].equals(cfg.name)) {
                    cfg.value = opt[1];

                    pclConfigList.set(i, cfg);

                    System.out.printf("Assigned new value as \"%s\" to %s\n", cfg.value, cfg.name);
                    found = true;
                }
            }

            if (!found) {
                pclConfigList.add(new PCLConfig(opt[0], opt[1]));
                System.out.printf("Created %s with value \"%s\"\n", opt[0], opt[1]);
            }
        } else {
            if (pclConfigList.isEmpty()) {
                System.out.println("No items assigned yet");
                return;
            }

            String nm = ln.replaceFirst("&", "").trim();

            boolean found = false;
            for (PCLConfig cfg : pclConfigList) {
                if (nm.equals(cfg.name)) {
                    System.out.printf("%s ==> %s\n", cfg.name, cfg.value);
                    found = true;
                }
            }

            if (!found)
                System.out.printf("No keyword labeled %s is assigned\n", nm);
        }
    }

    private void cmd() {
        System.out.print("==> ");
        String ln = scIn.nextLine().trim();

        if (ln.isEmpty()) {
            cmd();
            return;
        }

        if (ln.startsWith("&")) {
            assignCfg(ln);
            cmd();
            return;
        }

        if (ln.startsWith("help")) {
            try {
                System.out.println(pclCallers.get(0).retrieveLongHelp());
            } catch (PCLException e) {
                throw new RuntimeException(e);
            }
            cmd();
            return;
        }

        if (ln.equals("exit") || ln.equals("quit")) {
            scIn.close();
            System.exit(0);
            return;
        }

        String[] lnSplits = CLIParser.translateCmdLine(ln);

        if (lnSplits.length == 0) {
            TerminalColors.println(TerminalColors.TerminalColor.RED, String.format("Syntax error with parsing line \"%s\"", ln), true);
            cmd();
            return;
        }

        String exec = lnSplits[0];
        String[] givenArgs = new String[lnSplits.length - 1];

        if (lnSplits.length > 1)
            System.arraycopy(lnSplits, 1, givenArgs, 0, lnSplits.length - 1);

        System.out.println("Given Execution Parameter: " + exec);
        System.out.println("Given Arguments: " + Arrays.toString(givenArgs));

        cmd();
    }

    private String listToString(String[] list, String split) {
        if (list == null || list.length == 0)
            return "";

        StringBuilder str = new StringBuilder();
        for (String item : list)
            str.append("\"").append(item).append("\"").append(str);

        return str.substring(0, str.toString().length() - split.length());
    }

    public void launch() {
        cmd();
    }

}
