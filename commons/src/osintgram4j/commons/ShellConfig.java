package osintgram4j.commons;

public class ShellConfig {

    private final String name;
    private String value;

    public ShellConfig(String name, String value) {
        this.name = name;
        this.value = value;
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
