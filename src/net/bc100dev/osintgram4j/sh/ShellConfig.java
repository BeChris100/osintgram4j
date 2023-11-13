package net.bc100dev.osintgram4j.sh;

public class ShellConfig {

    private final String name;
    private String value;

    protected ShellConfig(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static ShellConfig create(String name, String value) {
        return new ShellConfig(name, value);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
