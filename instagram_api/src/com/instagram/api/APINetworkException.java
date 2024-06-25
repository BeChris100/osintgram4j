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
        super(String.format("(%d) %s", code, message));
    }

    public APINetworkException(int code, String message, Throwable cause) {
        super(String.format("(%d) %s", code, message), cause);
    }

    public APINetworkException(Throwable cause) {
        super(cause);
    }

    public static APINetworkException defineException(int code, String message) {
        return switch (code) {
            case 400 -> new BadRequestError(message);
            case 403 -> new ForbiddenError(message);
            case 404 -> new NotFoundError(message);
            case 429 -> new ThrottledError(message);
            default -> new APINetworkException(message);
        };
    }

    public static APINetworkException defineException(int code, String message, Throwable cause) {
        if (cause == null)
            return defineException(code, message);

        return switch (code) {
            case 400 -> new BadRequestError(message, cause);
            case 403 -> new ForbiddenError(message, cause);
            case 404 -> new NotFoundError(message, cause);
            case 429 -> new ThrottledError(message, cause);
            default -> new APINetworkException(message, cause);
        };
    }

    /**
     * Exception class: on 400 responses
     */
    public static class BadRequestError extends APINetworkException {
        public BadRequestError(String message) {
            super(400, message);
        }

        public BadRequestError(String message, Throwable cause) {
            super(400, message, cause);
        }

        public BadRequestError(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Exception class: on 403 responses
     */
    public static class ForbiddenError extends APINetworkException {
        public ForbiddenError(String message) {
            super(403, message);
        }

        public ForbiddenError(String message, Throwable cause) {
            super(403, message, cause);
        }

        public ForbiddenError(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Exception class: on 404 responses
     */
    public static class NotFoundError extends APINetworkException {
        public NotFoundError(String message) {
            super(404, message);
        }

        public NotFoundError(String message, Throwable cause) {
            super(404, message, cause);
        }

        public NotFoundError(Throwable cause) {
            super(cause);
        }
    }

    /**
     * Exception class: on 429 responses
     */
    public static class ThrottledError extends APINetworkException {
        public ThrottledError(String message) {
            super(429, message);
        }

        public ThrottledError(String message, Throwable cause) {
            super(429, message, cause);
        }

        public ThrottledError(Throwable cause) {
            super(cause);
        }
    }

}
