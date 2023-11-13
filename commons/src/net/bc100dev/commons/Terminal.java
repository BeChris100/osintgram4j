package net.bc100dev.commons;

public class Terminal {

    public static void print(Color color, String msg, boolean reset) {
        System.out.print(translateColor(color) + msg);

        if (reset)
            System.out.print(translateColor(Color.RESET));
    }

    public static void println(Color color, String msg, boolean reset) {
        System.out.print(translateColor(color) + msg);

        if (reset)
            System.out.print(translateColor(Color.RESET));

        System.out.println();
    }

    public static void errPrint(Color color, String msg, boolean reset) {
        System.out.print(translateColor(color) + msg);

        if (reset)
            System.out.print(translateColor(Color.RESET));
    }

    public static void errPrintln(Color color, String msg, boolean reset) {
        System.out.print(translateColor(color) + msg);

        if (reset)
            System.out.print(translateColor(Color.RESET));

        System.out.println();
    }

    private static String translateColor(Color color) {
        if (color == null)
            return "";

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
            default -> translateColor(Color.RESET);
        };
    }

    public static void clearTerminal() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public enum Color {

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
