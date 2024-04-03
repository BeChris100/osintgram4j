package com.instagram.api;

import java.io.IOException;

public class APINetworkException extends IOException {

    public APINetworkException(String message) {
        super(message);
    }

    public APINetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public APINetworkException(int code, String message) {
        super(code + ": " + message);
    }

    public APINetworkException(int code, String message, Throwable cause) {
        super(code + ": " + message, cause);
    }

    public APINetworkException(Throwable cause) {
        super(cause);
    }

    public static IOException defineException(int code, String message, Throwable cause) {
        switch (code) {
            case 400 -> {
                if (cause == null)
                    return new BadRequestError(message);
                else
                    return new BadRequestError(message, cause);
            }
            case 403 -> {
                if (cause == null)
                    return new ForbiddenError(message);
                else
                    return new ForbiddenError(message, cause);
            }
            case 429 -> {
                if (cause == null)
                    return new ThrottledError(message);
                else
                    return new ThrottledError(message, cause);
            }
            default -> {
                if (cause == null)
                    return new APINetworkException(code, message);
                else
                    return new APINetworkException(code, message, cause);
            }
        }
    }

    /**
     * Exception class: on 400 responses
     */
    public static class BadRequestError extends IOException {
        public BadRequestError(String message) {
            super(message);
        }

        public BadRequestError(String message, Throwable cause) {
            super(message, cause);
        }

        public BadRequestError(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Exception class: on 403 responses
     */
    public static class ForbiddenError extends IOException {
        public ForbiddenError(String message) {
            super(message);
        }

        public ForbiddenError(String message, Throwable cause) {
            super(message, cause);
        }

        public ForbiddenError(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Exception class: on 429 responses
     */
    public static class ThrottledError extends IOException {
        public ThrottledError(String message) {
            super(message);
        }

        public ThrottledError(String message, Throwable cause) {
            super(message, cause);
        }

        public ThrottledError(Throwable cause) {
            super(cause);
        }
    }

}
