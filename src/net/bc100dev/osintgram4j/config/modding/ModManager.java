package net.bc100dev.osintgram4j.config.modding;

import net.bc100dev.commons.ApplicationException;
import net.bc100dev.commons.ApplicationIOException;
import net.bc100dev.commons.utils.io.FileUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import osintgram4j.api.Launcher;
import osintgram4j.commons.PackagedApplication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ModManager {

    public static final List<ApplicationModification> runningMods = new ArrayList<>();
    private static final List<ModInstance> modInstances = new ArrayList<>();

    private static ApplicationModification getModData(String modFilePath) throws IOException, ApplicationException {
        ZipFile zipFile = new ZipFile(new File(modFilePath));
        ZipEntry entry = zipFile.getEntry("META-INF/Osintgram4j.json");
        InputStream is = zipFile.getInputStream(entry);

        StringBuilder strBd = new StringBuilder();
        byte[] buff = new byte[1024];
        int len;

        while ((len = is.read(buff, 0, 1024)) != -1)
            strBd.append(new String(buff, 0, len));

        is.close();

        String str = strBd.toString().trim();
        if (!(str.startsWith("{") || str.startsWith("}")))
            throw new ApplicationIOException("Invalid JSON data for mod file " + modFilePath);

        JSONObject root = new JSONObject(str);
        JSONObject meta = root.getJSONObject("ModInfo");

        ApplicationModification.ModInfo info = new ApplicationModification.ModInfo(
                meta.getString("Author"),
                meta.getString("Name"),
                meta.getString("Version")
        );

        Class<?> startClass = null;
        if (root.has("StartClass") && root.get("StartClass") instanceof String startClassStr) {
            try {
                startClass = Class.forName(startClassStr);
                Class<?> extenderClass = startClass.getSuperclass();
                if (extenderClass != Launcher.class)
                    throw new ApplicationException(String.format("Invalid Superclass, received \"%s\", should have been \"%s\"",
                            extenderClass.getName(), Launcher.class.getName()));

                Method ignoreLauncher = startClass.getDeclaredMethod("onLaunch", String[].class); // args: (String[] cliArgs)
                Method ignoreExit = startClass.getDeclaredMethod("onExit", String.class); // args: (String reason)
            } catch (ReflectiveOperationException ex) {
                throw new ApplicationException(ex);
            }
        }

        String[] commandsArr = new String[0];
        if (root.has("JsonCommandFiles") && root.get("JsonCommandFiles") instanceof JSONArray cmdArr) {
            List<String> commandsList = new ArrayList<>();
            for (int i = 0; i < cmdArr.length(); i++)
                commandsList.add(cmdArr.getString(i));

            commandsArr = new String[commandsList.size()];
            for (int i = 0; i < commandsList.size(); i++)
                commandsArr[i] = commandsList.get(i);
        }

        zipFile.close();

        return new ApplicationModification(info, new File(modFilePath), commandsArr, startClass);
    }

    public static List<ApplicationModification> listInstalledMods() throws IOException, ApplicationException {
        List<ApplicationModification> mods = new ArrayList<>();

        File appDir = PackagedApplication.getApplicationDirectory();
        File modDir = new File(appDir, "mods");
        if (!modDir.exists())
            return mods;

        List<String> sModList = FileUtil.listDirectory(modDir.getAbsolutePath(), true, false);
        for (String sMod : sModList)
            mods.add(getModData(sMod));

        return mods;
    }

    public static List<ApplicationModification> listLoadedMods() throws IOException, ApplicationException {
        List<ApplicationModification> installedMods = listInstalledMods();
        List<ApplicationModification> loadedMods = new ArrayList<>();

        String[] loadedContents = System.getProperty("java.class.path").split(File.pathSeparator);
        for (String loadedContent : loadedContents) {
            File content = new File(loadedContent);

            for (ApplicationModification mod : installedMods) {
                if (mod.getModFile().getAbsolutePath().equalsIgnoreCase(content.getAbsolutePath()))
                    loadedMods.add(mod);
            }
        }

        return loadedMods;
    }

    public static void startMod(ApplicationModification mod, String[] cliArgs) throws ApplicationException {
        try {
            Class<?> starterClass = mod.getStartClass();
            Object instance = starterClass.getDeclaredConstructor().newInstance();
            modInstances.add(new ModInstance(mod, instance));

            Method starterMethod = starterClass.getDeclaredMethod("onLaunch", String[].class);
            starterMethod.invoke(instance, (Object) cliArgs);

            runningMods.add(mod);
        } catch (ReflectiveOperationException ex) {
            throw new ApplicationException(ex);
        }
    }

    public static void stopMod(ApplicationModification mod, String reason) throws ApplicationException {
        Object findInstance = null;
        int modInstanceIndex = -1, runningModIndex = -1;

        for (int i = 0; i < modInstances.size(); i++) {
            ModInstance modInstance = modInstances.get(i);
            if (modInstance.mod == mod) {
                findInstance = modInstance;
                modInstanceIndex = i;
            }
        }

        for (int i = 0; i < runningMods.size(); i++) {
            if (runningMods.get(i) == mod)
                runningModIndex = i;
        }

        if (findInstance == null || runningModIndex == -1)
            throw new ApplicationException("Could not find instance for \"" + mod.getInfo().name() + "\" (presumably not initialized previously)");

        try {
            Class<?> starterClass = mod.getStartClass();
            Method exitMethod = starterClass.getDeclaredMethod("onExit", String.class);
            exitMethod.invoke(findInstance, reason);

            runningMods.remove(runningModIndex);
            modInstances.remove(modInstanceIndex);
        } catch (ReflectiveOperationException ex) {
            throw new ApplicationException(ex);
        }
    }

    record ModInstance(ApplicationModification mod, Object instance) {
    }

}
