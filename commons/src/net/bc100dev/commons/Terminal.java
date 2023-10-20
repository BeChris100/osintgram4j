package net.bc100dev.commons;

public class Terminal {

    public static void print(Colors color, String msg, boolean reset) {
        System.out.print(translateColor(color) + msg);

        if (reset)
            System.out.print(translateColor(Colors.RESET));
    }

    public static void println(Colors color, String msg, boolean reset) {
        System.out.print(translateColor(color) + msg);

        if (reset)
            System.out.println(translateColor(Colors.RESET));
    }

    private static String translateColor(Colors color) {
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
            default -> translateColor(Colors.RESET);
        };
    }

    public static void clearTerminal() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public enum Colors {

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
