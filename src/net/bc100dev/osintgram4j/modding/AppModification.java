package net.bc100dev.osintgram4j.modding;

import net.bc100dev.commons.utils.io.FileUtil;
import org.json.JSONObject;
import osintgram4j.api.Launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public record AppModification(File file) {

    private static JSONObject loadedJsonData = null;

    private JSONObject loadJson() throws IOException {
        if (!FileUtil.isValidZipFile(file.getAbsolutePath()))
            throw new IOException("How did you get past the zip file verification? (JAR file = \"" + file.getAbsolutePath() + "\")");

        ZipFile zip = new ZipFile(file.getAbsolutePath());
        ZipEntry o4Json = zip.getEntry("META-INF/Osintgram4j.json");
        if (o4Json == null)
            throw new IOException("Could not load \"Osintgram4j.json\" file (JAR file = '" + file.getAbsolutePath() + "')");

        InputStream is = zip.getInputStream(o4Json);
        StringBuilder bd = new StringBuilder();
        byte[] buff = new byte[1024];
        int len;

        while ((len = is.read(buff, 0, 1024)) != -1)
            bd.append(new String(buff, 0, len));

        is.close();
        zip.close();

        String str = bd.toString().trim();
        if (!((str.startsWith("{") && str.startsWith("}")) || (str.startsWith("[") && str.startsWith("]"))))
            throw new IOException("Not a JSON file");

        JSONObject obj = new JSONObject(str);
        loadedJsonData = obj;

        return obj;
    }

    public boolean isStartable() throws IOException {
        return false;
    }

    public boolean hasCommands() throws IOException {
        return false;
    }

    public void start(String[] cliArgs) throws IOException {
        if (loadedJsonData == null)
            loadJson();

        String launchClass = loadedJsonData.getJSONObject("Actions").getJSONObject("OnStart").getString("LaunchClass");

        try {
            Class<?> clazz = Class.forName(launchClass);
            Object cn = clazz.getDeclaredConstructor().newInstance();
            if (!clazz.isAssignableFrom(Launcher.class))
                throw new IOException("Launch class " + launchClass + " is not extending \"Launcher.class\"");

            Method method = clazz.getMethod("start");
        } catch (ReflectiveOperationException ex) {
            throw new IOException(ex);
        }
    }

}
