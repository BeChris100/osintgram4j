package net.bc100dev.osintgram4j.config;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class SessionIdentifier {

    private final File sessionDir;
    private final UUID uuid;

    protected SessionIdentifier(File sessionDir, UUID uuid) {
        this.sessionDir = sessionDir;
        this.uuid = uuid;
    }

    public File getSessionDir() {
        return sessionDir;
    }

    public UUID getUuid() {
        return uuid;
    }

    public File doDirEncrypt() throws IOException {
        return null;
    }

    /**
     * Retrieves a Session instance by the directory name
     *
     * @param name The name of the directory
     * @return Retrieves an instance of the corresponding Session
     */
    public static SessionIdentifier getInstanceByName(String name) {
        return null;
    }

    public static SessionIdentifier getInstanceByUsername(String username) {
        return null;
    }

    public static SessionIdentifier getInstanceByUserId(String userId) {
        return null;
    }

    public static SessionIdentifier getInstanceByUuid(UUID _uuid) {
        return null;
    }

    public static SessionIdentifier createInstance(String username, String name) {
        return null;
    }

}
