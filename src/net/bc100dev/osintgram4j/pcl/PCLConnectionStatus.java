package net.bc100dev.osintgram4j.pcl;

public enum PCLConnectionStatus {

    /**
     * A successful and a verified connection
     */
    SUCCESS,

    /**
     * A connection has been established, but it is not verified
     * or
     */
    NOT_VERIFIED,

    UNKNOWN_ERROR,

    MFA_ERROR,

    NO_CONNECTION

}
