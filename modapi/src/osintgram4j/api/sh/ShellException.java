package osintgram4j.api.sh;

public class ShellException extends Exception {

    public ShellException(String message) {
        super(message);
    }

    public ShellException(Throwable cause) {
        super(cause);
    }

    public ShellException(String message, Throwable cause) {
        super(message, cause);
    }
}
