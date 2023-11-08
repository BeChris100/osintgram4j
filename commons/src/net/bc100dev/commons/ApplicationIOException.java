package net.bc100dev.commons;

import java.io.IOException;

public class ApplicationIOException extends IOException {

    public ApplicationIOException() {
        super();
    }

    public ApplicationIOException(String message) {
        super(message);
    }

    public ApplicationIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationIOException(Throwable cause) {
        super(cause);
    }
}
