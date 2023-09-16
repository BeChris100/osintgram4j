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
            case RESET -> "\001B[0m";
            case RED -> "\001B[31m";
            case GREEN -> "\001B[32m";
            case YELLOW -> "\001B[33m";
            case BLUE -> "\001B[34m";
            case PURPLE -> "\001B[35m";
            case CYAN -> "\001B[36m";
            case WHITE -> "\001B[37m";
            default -> translateColor(TerminalColor.RESET);
        };
    }

    public enum TerminalColor {

        RESET, //  = "\001B[0m";
        RED, // = "\001B[31m";
        GREEN, // = "\u001B[32m";
        YELLOW, // = "\u001B[33m";
        BLUE, // = "\u001B[34m";
        PURPLE, // = "\u001B[35m";
        CYAN, // = "\u001B[36m";
        WHITE // = "\u001B[37m";

    }

}
