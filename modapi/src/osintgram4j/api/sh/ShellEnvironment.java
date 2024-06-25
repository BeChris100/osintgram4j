package osintgram4j.api.sh;

public class ShellEnvironment {

    private final String name;
    private String value;

    public ShellEnvironment(String name, String value) {
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
