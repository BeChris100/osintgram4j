package osintgram4j.api.sh;

import net.bc100dev.commons.ApplicationException;
import net.bc100dev.commons.Terminal;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OptionSelect {

    private String title, message;

    private final List<ButtonAction> buttons = new ArrayList<>();

    private int mismatchCount = 0;
    private int mismatchMaxCount = 3;

    public OptionSelect() {
    }

    public OptionSelect(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMismatchMaxCount() {
        return mismatchMaxCount;
    }

    public void setMismatchMaxCount(int mismatchMaxCount) {
        if (mismatchMaxCount < 0)
            throw new IllegalArgumentException("Cannot set max count in negative bounds");

        if (mismatchMaxCount == 0)
            throw new IllegalArgumentException("Mismatch count cannot be zero");

        this.mismatchMaxCount = mismatchMaxCount;
    }

    public void addButton(boolean defBtn, String text, Runnable rn) {
        if (defBtn) {
            if (!buttons.isEmpty()) {
                for (int i = 0; i < buttons.size(); i++) {
                    ButtonAction btn = buttons.get(i);
                    if (btn.defButton())
                        buttons.set(i, new ButtonAction(new Button(btn.btn.text, btn.btn.rn), false));
                }
            }
        }

        buttons.add(new ButtonAction(new Button(text, rn), defBtn));
    }

    public void show() {
        if (buttons.isEmpty())
            return;

        if (title != null && !title.trim().isEmpty()) {
            System.out.println(title);
            System.out.println("-".repeat(title.length()));
            System.out.println();
        }

        if (message != null && !message.trim().isEmpty()) {
            System.out.println(message);
            System.out.println();
        }

        for (int i = 0; i < buttons.size(); i++) {
            ButtonAction btnAction = buttons.get(i);
            Button btn = btnAction.btn;

            System.out.println("( " + (i + 1) + " ) " + btn.text);
        }

        Scanner in = new Scanner(System.in);
        System.out.print("==> ");

        try {
            String v = in.nextLine();
            if (v.trim().isEmpty()) {
                if (invokeDefault())
                    return;

                throw new ApplicationException("No value specified");
            }

            int index = Integer.parseInt(v) - 1;
            if (index < 0 || index >= buttons.size()) {
                if (invokeDefault())
                    return;

                throw new ApplicationException("No such button");
            }

            buttons.get(index).btn.rn.run();
        } catch (NumberFormatException | ApplicationException ex) {
            mismatchCount++;

            if (mismatchCount == mismatchMaxCount) {
                System.err.println("You have provided the wrong value several times; expected a numeric value");
                return;
            }

            Terminal.clearTerminal();

            System.err.println(ex.getMessage());
            System.err.println();
            show();
        }
    }

    private boolean invokeDefault() {
        boolean found = false;

        for (ButtonAction btn : buttons) {
            if (btn.defButton) {
                found = true;
                btn.btn.rn.run();
            }
        }

        return found;
    }

    private record ButtonAction(Button btn, boolean defButton) {
    }

    public record Button(String text, Runnable rn) {
    }

}
