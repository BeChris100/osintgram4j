package net.bc100dev.osintgram4j;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class HelpPage {

    private int spaceWidth;

    private final List<Item> argItemList = new ArrayList<>();

    public HelpPage() {
        this.spaceWidth = 2;
    }

    public void setSpaceWidth(int spaceWidth) {
        this.spaceWidth = spaceWidth;
    }

    public int getSpaceWidth() {
        return spaceWidth;
    }

    public void addArg(String arg, String equalValue, String description) {
        argItemList.add(new Item(arg, equalValue, description));
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
