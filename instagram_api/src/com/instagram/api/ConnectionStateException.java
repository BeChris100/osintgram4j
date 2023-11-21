package com.instagram.api;

import java.io.IOException;

public class ConnectionStateException extends IOException {

    public ConnectionStateException(String message) {
        super(message);
    }

    public ConnectionStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionStateException(Throwable cause) {
        super(cause);
    }
}
