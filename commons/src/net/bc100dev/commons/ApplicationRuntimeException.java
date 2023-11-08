package net.bc100dev.commons;

public class ApplicationRuntimeException extends RuntimeException {

    public ApplicationRuntimeException() {
        super();
    }

    public ApplicationRuntimeException(String message) {
        super(message);
    }

    public ApplicationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationRuntimeException(Throwable cause) {
        super(cause);
    }
}
