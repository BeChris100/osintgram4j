package com.instagram.api;

import java.io.IOException;

public class ApiNetworkException extends IOException {

    public ApiNetworkException(String message) {
        super(message);
    }

    public ApiNetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiNetworkException(Throwable cause) {
        super(cause);
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

    /**
     * Exception class: on any other code responses
     */
    public static class RequestError extends IOException {
        public RequestError(String message) {
            super(message);
        }

        public RequestError(String message, Throwable cause) {
            super(message, cause);
        }

        public RequestError(Throwable cause) {
            super(cause);
        }
    }

}
