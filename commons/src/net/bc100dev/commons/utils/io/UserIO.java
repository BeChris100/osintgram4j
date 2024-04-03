package net.bc100dev.commons.utils.io;

import net.bc100dev.commons.ApplicationException;

import static net.bc100dev.commons.utils.RuntimeEnvironment.*;

public class UserIO {

    public static native int getGid() throws ApplicationException;

    public static native int getUid() throws ApplicationException;

    public static native void setUid(int id) throws ApplicationException;

    public static native void setGid(int id) throws ApplicationException;

    public static native boolean nIsAdmin() throws ApplicationException;

    public static boolean isAdmin() throws ApplicationException {
        if (isLinux())
            return getGid() == 0 && getUid() == 0;

        return nIsAdmin();
    }
    
    public static String getUsername() {
        return USER_NAME;
    }

}
