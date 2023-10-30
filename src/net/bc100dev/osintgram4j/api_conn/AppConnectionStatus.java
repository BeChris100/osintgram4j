package net.bc100dev.osintgram4j.api_conn;

public class AppConnectionStatus {

    private final AppConnectionState state;
    private final int code;
    private final String message;

    protected AppConnectionStatus(AppConnectionState state, int code, String message) {
        this.state = state;
        this.code = code;
        this.message = message;
    }

    public AppConnectionState getConnectionState() {
        return state;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public AppConnectionStatus getConnectionStatus() {
        return null;
    }

}
