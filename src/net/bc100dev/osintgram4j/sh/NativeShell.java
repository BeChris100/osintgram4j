package net.bc100dev.osintgram4j.sh;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a native Shell layer for the compatible operating systems.
 * Being the superclass, this supports the use of In-App Shell History and Navigation.
 * Specifically, this relies on C++.
 */
public class NativeShell {

    private final List<String> commandHistory = new ArrayList<>();

    public NativeShell(String surpress) {
    }

    private native String input();

}
