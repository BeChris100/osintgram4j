package net.bc100dev.commons.utils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class HelpPage {

    private int spaceWidth, startSpaceWidth;

    private final List<Item> argItemList = new ArrayList<>();

    public HelpPage() {
        this.spaceWidth = 5;
        this.startSpaceWidth = 0;
    }

    public void setSpaceWidth(int spaceWidth) {
        this.spaceWidth = spaceWidth;
    }

    public int getSpaceWidth() {
        return spaceWidth;
    }

    public void setStartSpaceWidth(int startSpaceWidth) {
        this.startSpaceWidth = startSpaceWidth;
    }

    public int getStartSpaceWidth() {
        return startSpaceWidth;
    }

    public void addArg(String arg, String assignableDesc, String description) {
        argItemList.add(new Item(arg, assignableDesc, description));
    }

    public String display() {
        StringBuilder str = new StringBuilder();
        int maxItemLength = 0;

        for (Item item : argItemList) {
            String argLine = item.arg;

            if (item.equalDesc() != null)
                argLine += "=" + item.equalDesc;

            if (maxItemLength < argLine.length())
                maxItemLength = argLine.length();
        }

        for (Item item : argItemList) {
            String argLine = item.arg;

            if (item.equalDesc() != null)
                argLine += "=" + item.equalDesc;

            if (startSpaceWidth != 0)
                str.append(" ".repeat(startSpaceWidth));

            str.append(argLine)
                    .append(" ".repeat((maxItemLength - argLine.length()) + spaceWidth))
                    .append(item.description)
                    .append("\n");
        }

        return str.toString();
    }

    public void display(PrintStream ps) {
        ps.println(display());
    }

    record Item(String arg, String equalDesc, String description) {
    }

}
