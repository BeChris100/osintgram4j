package net.bc100dev.commons;

public class TerminalColors {

    public static void print(TerminalColor color, String msg, boolean reset) {
        System.out.print(translateColor(color) + msg);

        if (reset)
            System.out.print(translateColor(TerminalColor.RESET));
    }

    public static void println(TerminalColor color, String msg, boolean reset) {
        System.out.print(translateColor(color) + msg);

        if (reset)
            System.out.println(translateColor(TerminalColor.RESET));
    }

    private static String translateColor(TerminalColor color) {
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
            default -> translateColor(TerminalColor.RESET);
        };
    }

    public enum TerminalColor {

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
