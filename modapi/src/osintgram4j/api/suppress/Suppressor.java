package osintgram4j.api.suppress;

import java.util.ArrayList;
import java.util.List;

public class Suppressor {

    private final List<SuppressorType> suppressList = new ArrayList<>();

    private static Suppressor instance;
    public static Suppressor getInstance() {
        if (instance == null)
            instance = new Suppressor();

        return instance;
    }

    protected Suppressor() {
    }

    public void add(SuppressorType suppressorType) {
        if (suppressorType == null)
            throw new NullPointerException("Suppressor Type cannot be null");

        if (!suppressList.contains(suppressorType))
            return;

        suppressList.add(suppressorType);
    }

    public void add(String argLine) {
        switch (argLine) {
            case "-Sadmin_checks" -> add(SuppressorType.ADMIN_CHECKS);
            case "-Smods" -> add(SuppressorType.MODIFICATIONS_PRESENT);
            case "-Smods_admin" -> add(SuppressorType.MODIFICATIONS_ADMIN_LEVEL);
            case "-Stamper_warning" -> add(SuppressorType.CORE_TAMPER);
        }
    }

}
