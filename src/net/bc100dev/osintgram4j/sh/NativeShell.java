package net.bc100dev.osintgram4j.sh;

import net.bc100dev.commons.ApplicationException;
import net.bc100dev.commons.ApplicationRuntimeException;
import net.bc100dev.commons.utils.io.UserIO;

import java.util.ArrayList;
import java.util.List;

import static net.bc100dev.commons.utils.RuntimeEnvironment.*;

/**
 * This class is a native Shell layer for the compatible operating systems.
 * Being the superclass, this supports the use of In-App Shell History and Navigation.
 * Specifically, this relies on C++.
 */
public class NativeShell {

    private final List<String> commandHistory = new ArrayList<>();

    private boolean running = false;

    private String PS1;

    public NativeShell(String surpress) {
        try {
            PS1 = String.format("[%s/%s: %s]%s ", USER_NAME, getHostName(), WORKING_DIRECTORY.getName(), UserIO.nIsAdmin() ? "#" : "$");
        } catch (ApplicationException ex) {
            throw new ApplicationRuntimeException(ex);
        }
    }

    private native String inputCmd(String ps1);

    public void cmd() {
        while (running) {
            String cli = inputCmd(PS1);
        }
    }

}
