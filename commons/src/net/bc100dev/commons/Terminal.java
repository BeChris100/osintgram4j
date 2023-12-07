package net.bc100dev.commons;

public class Terminal {

    public static native int windowLines();
    public static native int windowColumns();

    public static void print(TermColor color, String msg, boolean reset) {
        if (msg != null)
            System.out.print(translateColor(color) + msg);

        if (reset)
            System.out.print(translateColor(TermColor.RESET));
    }

    public static void println(TermColor color, String msg, boolean reset) {
        if (msg != null)
            System.out.print(translateColor(color) + msg);

        if (reset)
            System.out.print(translateColor(TermColor.RESET));

        System.out.println();
    }

    public static void errPrint(TermColor color, String msg, boolean reset) {
        if (msg != null)
            System.out.print(translateColor(color) + msg);

        if (reset)
            System.out.print(translateColor(TermColor.RESET));
    }

    public static void errPrintln(TermColor color, String msg, boolean reset) {
        if (msg != null)
            System.out.print(translateColor(color) + msg);

        if (reset)
            System.out.print(translateColor(TermColor.RESET));

        System.out.println();
    }

    private static String translateColor(TermColor color) {
        if (color == null)
            return translateColor(TermColor.RESET);

        return switch (color) {
            case RESET -> "\033[0m";
            case BLACK -> "\033[0;30m";
            case RED -> "\033[0;31m";
            case GREEN -> "\033[0;32m";
            case YELLOW -> "\033[0;33m";
            case BLUE -> "\033[0;34m";
            case PURPLE -> "\033[0;35m";
            case CYAN -> "\033[0;36m";
            case WHITE -> "\033[0;37m";
        };
    }

    public static void clearTerminal() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public enum TermColor {

        RESET,
        BLACK,
        RED,
        GREEN,
        YELLOW,
        BLUE,
        PURPLE,
        CYAN,
        WHITE

    }

}
