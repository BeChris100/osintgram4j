package net.bc100dev.commons;

import net.bc100dev.commons.utils.RuntimeEnvironment;
import net.bc100dev.commons.utils.io.FileUtil;

import java.io.*;
import java.net.URL;
import java.nio.file.AccessDeniedException;

public class ResourceManager {

    private final String packageName;
    private final boolean inPkg;

    public ResourceManager(Class<?> cls, boolean inPkg) {
        this.packageName = cls.getPackageName();
        this.inPkg = inPkg;
    }

    private String getLookupPath(String resName) {
        String lookup = "";

        if (inPkg)
            lookup = "/" + packageName.replaceAll("\\.", "/") + "/";

        return lookup + resName;
    }

    public boolean resourceExists(String resName) {
        return getClass().getResource(getLookupPath(resName)) != null;
    }

    public String getPath(String resName) {
        URL resource = getClass().getResource(getLookupPath(resName));

        if (resource == null)
            return "";

        return resource.getPath();
    }

    public InputStream getResourceInputStream(String resName) {
        URL resource = getClass().getResource(getLookupPath(resName));

        if (resource == null)
            throw new NullPointerException(String.format("Cannot access \"%s\" (Not found)", getLookupPath(resName)));

        return getClass().getResourceAsStream(getLookupPath(resName));
    }

    public void copy(String resource, File outputFile) throws IOException {
        if (ResourceManager.class.getResource(getLookupPath(resource)) == null)
            throw new FileNotFoundException("Resource file \"" + resource + "\" not found");

        InputStream is = getClass().getResourceAsStream(getLookupPath(resource));

        if (is == null)
            throw new IOException("Could not open stream for resource file \"" + resource + "\"");

        if (!outputFile.exists())
            FileUtil.createFile(outputFile.getAbsolutePath(), true);

        if (!outputFile.canWrite())
            throw new AccessDeniedException("User \"" + RuntimeEnvironment.USER_NAME + "\" does not have access " +
                    "to \"" + outputFile.getPath() + "\"");

        FileOutputStream fos = new FileOutputStream(outputFile);

        int data;
        byte[] buf = new byte[2048];
        while ((data = is.read(buf, 0, 2048)) != -1)
            fos.write(buf, 0, data);

        fos.close();
        is.close();
    }

    public URL getResource(String resource) {
        if (getClass().getResource(resource) == null)
            throw new RuntimeException("Resource file \"" + resource + "\" not found in the Java Package");

        return getClass().getResource(resource);
    }

}
