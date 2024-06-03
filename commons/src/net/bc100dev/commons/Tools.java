package net.bc100dev.commons;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Tools {

    /**
     * Sorry StackOverflow that I borrowed yer code. Credits still is required, so here ya go.
     * <a href="https://stackoverflow.com/questions/3259143/split-a-string-containing-command-line-parameters-into-a-string-in-java">damn link</a>
     * <br/>
     * [code borrowed from ant.jar]
     * Crack a command line.
     * @param cmdLine the command line to process.
     * @return the command line broken into strings.
     * An empty or null toProcess parameter results in a zero sized array.
     */
    public static String[] translateCmdLine(String cmdLine) {
        if (cmdLine == null || cmdLine.trim().isEmpty()) {
            //no command? no string
            return new String[0];
        }

        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = normal;
        final StringTokenizer tok = new StringTokenizer(cmdLine, "\"' ", true);
        final ArrayList<String> result = new ArrayList<String>();
        final StringBuilder current = new StringBuilder();
        boolean lastTokenHasBeenQuoted = false;

        while (tok.hasMoreTokens()) {
            String nextTok = tok.nextToken();
            switch (state) {
                case inQuote:
                    if ("\'".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                    break;
                case inDoubleQuote:
                    if ("\"".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                    break;
                default:
                    if ("'".equals(nextTok)) {
                        state = inQuote;
                    } else if ("\"".equals(nextTok)) {
                        state = inDoubleQuote;
                    } else if (" ".equals(nextTok)) {
                        if (lastTokenHasBeenQuoted || current.length() != 0) {
                            result.add(current.toString());
                            current.setLength(0);
                        }
                    } else {
                        current.append(nextTok);
                    }
                    lastTokenHasBeenQuoted = false;
                    break;
            }
        }
        if (lastTokenHasBeenQuoted || current.length() != 0) {
            result.add(current.toString());
        }
        if (state == inQuote || state == inDoubleQuote) {
            throw new RuntimeException("unbalanced quotes in " + cmdLine);
        }
        return result.toArray(new String[result.size()]);
    }

}
