package osintgram4j.api.sh;

import net.bc100dev.commons.Terminal;
import net.bc100dev.commons.Tools;
import net.bc100dev.commons.utils.OperatingSystem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import static net.bc100dev.commons.utils.RuntimeEnvironment.getOperatingSystem;

public class ShellCommandEntry {

    private final int version;
    private final String label, pkgName;
    private final List<ShellCaller> callers;
    private final List<ShellAlias> aliases;

    private ShellCommandEntry(int version, String label, String pkgName, List<ShellCaller> callers, List<ShellAlias> aliases) {
        this.version = version;
        this.label = label;
        this.pkgName = pkgName;
        this.callers = callers;
        this.aliases = aliases;
    }

    public int getPackageVersion() {
        return version;
    }

    public List<ShellAlias> getAliases() {
        return aliases;
    }

    public List<ShellCaller> getCommands() {
        return callers;
    }

    public String getLabel() {
        return label;
    }

    public String getPackageName() {
        return pkgName;
    }

    protected static ShellCommandEntry parseJsonFile(String contents) throws ShellException {
        JSONObject obj = new JSONObject(contents);
        // for now, we can ignore the versioning, but we still need that

        if (!obj.has("package"))
            throw new ShellException("\"package\" key not found");

        if (!obj.has("label"))
            throw new ShellException("\"label\" key not found");

        if (!obj.has("version"))
            throw new ShellException("\"version\" key not found");

        if (!obj.has("command_list"))
            throw new ShellException("\"command_list\" key not found");

        String pkgName, label;
        int version;

        if (obj.get("package") instanceof String s0 &&
                obj.get("label") instanceof String s1 &&
                obj.get("version") instanceof Integer i0) {
            pkgName = s0;
            label = s1;
            version = i0;
        } else
            throw new ShellException("One of the keys (\"package\", \"label\", \"version\") have invalid value types");

        if (version != 1) {
            Terminal.errPrintln(Terminal.TermColor.RED, String.format("%s: unknown version %d", label, version), true);
            return null;
        }

        JSONArray arr = obj.getJSONArray("command_list");
        List<ShellCaller> callerList = new ArrayList<>();
        List<ShellAlias> aliases = new ArrayList<>();

        if (!arr.isEmpty()) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject cmdObj = arr.getJSONObject(i);
                boolean deprecated = false;

                if (!cmdObj.has("command"))
                    throw new ShellException(String.format("\"%s\" key at entry %d not found", "command", i + 1));

                if (!cmdObj.has("description"))
                    throw new ShellException(String.format("\"%s\" key at entry %d not found", "description", i + 1));

                if (!cmdObj.has("class"))
                    throw new ShellException(String.format("\"%s\" key at entry %d not found", "class", i + 1));

                if (!cmdObj.has("alternates"))
                    throw new ShellException(String.format("\"%s\" key at entry %d not found", "command", i + 1));

                if (cmdObj.has("deprecated")) {
                    if (cmdObj.get("deprecated") instanceof Boolean b)
                        deprecated = b;
                }

                List<String> alternatesList = new ArrayList<>();
                if (cmdObj.get("alternates") instanceof JSONArray) {
                    JSONArray altArr = cmdObj.getJSONArray("alternates");

                    for (int j = 0; j < altArr.length(); j++) {
                        if (altArr.get(j) instanceof String str) {
                            if (alternatesList.contains(str))
                                continue;

                            alternatesList.add(str);
                        }
                    }
                } else
                    throw new ShellException("\"alternates\" has an invalid value data type");

                String[] alternates = new String[alternatesList.size()];
                for (int j = 0; j < alternatesList.size(); j++)
                    alternates[j] = alternatesList.get(j);

                String cmd, description, _class;
                if (cmdObj.get("cmd") instanceof String ss0 &&
                        cmdObj.get("description") instanceof String ss1 &&
                        cmdObj.get("class") instanceof String ss2) {
                    cmd = ss0;
                    description = ss1;
                    _class = ss2;
                } else
                    throw new ShellException("One of the keys (command_list: \"cmd\", \"description\", \"class\") have invalid data type");

                if (_class.startsWith("."))
                    _class = pkgName + _class;

                ShellCaller caller = new ShellCaller(deprecated, cmd, description, _class, alternates);
                callerList.add(caller);

                if (cmdObj.has("aliases")) {
                    if (cmdObj.get("aliases") instanceof JSONArray ja0) {
                        for (int j = 0; j < ja0.length(); j++) {
                            JSONObject aliasObj = ja0.getJSONObject(j);
                            if (!aliasObj.has("cmd"))
                                throw new ShellException(String.format("\"%s\" key at entry %d not found (required to execute)", "cmd", i + 1));

                            if (!aliasObj.has("args"))
                                throw new ShellException(String.format("\"%s\" key at entry %d not found (required to append to command)", "args", i + 1));

                            String aliasCmd;
                            String[] aliasArgs;

                            if (aliasObj.get("cmd") instanceof String sss0 &&
                                    aliasObj.get("args") instanceof String sss1) {
                                aliasCmd = sss0;
                                aliasArgs = Tools.translateCmdLine(sss1);
                            } else
                                throw new ShellException("One of the keys (aliases: \"cmd\", \"args\") have an invalid data type");

                            ShellAlias alias = new ShellAlias(aliasCmd, caller, aliasArgs);

                            if (aliasObj.has("depends")) {
                                if (aliasObj.get("depends") instanceof String depStr)
                                    alias.dependsOn(Tools.translateCmdLine(depStr));
                                else
                                    throw new ShellException("Key (aliases: \"depends\") have an invalid data type");
                            }

                            if (aliasObj.has("platforms")) {
                                if (aliasObj.get("platforms") instanceof String plt) {
                                    if (!plt.contains("*")) {
                                        String[] platformsArr = plt.split(",");

                                        for (String platform : platformsArr) {
                                            switch (platform.toLowerCase()) {
                                                case "linux", "lin", "nux" ->
                                                        alias.allowPlatformSupport(OperatingSystem.LINUX, true);
                                                case "win", "win32", "win64", "windows", "windows64", "windows32",
                                                     "windows_x32",
                                                     "windows_x64", "windows_32", "windows_64" ->
                                                        alias.allowPlatformSupport(OperatingSystem.WINDOWS, true);
                                                case "mac_os", "osx", "mac" ->
                                                        alias.allowPlatformSupport(OperatingSystem.MAC_OS, true);
                                            }
                                        }
                                    } else
                                        alias.allowAllPlatforms(true);
                                } else
                                    throw new ShellException("Key (aliases: \"platforms\") have an invalid data type");
                            } else
                                alias.allowAllPlatforms(true);

                            if (alias.isPlatformSupported(getOperatingSystem()))
                                aliases.add(alias);
                        }
                    }
                }
            }
        }

        return new ShellCommandEntry(version, label, pkgName, callerList, aliases);
    }

    @Deprecated
    public static ShellCommandEntry initialize(Class<?> cls, String resourceFile) throws IOException, ShellException {
        if (cls == null)
            throw new NullPointerException("The class cannot be passed as null, when accessing resource files");

        //.getResource(getLookupPath(resName)) != null;
        if (cls.getResource(resourceFile) == null)
            throw new ShellException("Resource file at \"" + resourceFile + "\" not found");

        InputStream is = cls.getResourceAsStream(resourceFile);
        if (is == null)
            throw new IOException("Could not open an InputStream for \"" + resourceFile + "\"");

        byte[] buff = is.readAllBytes();
        is.close();

        String data = new String(buff).trim();
        if (!data.startsWith("{"))
            throw new JSONException("JSON Parse (not a valid starting point)");

        if (!data.endsWith("}"))
            throw new JSONException("JSON Parse (not a valid ending point)");

        return parseJsonFile(data);
    }

    public static ShellCommandEntry initialize(String jsonData) throws ShellException {
        jsonData = jsonData.trim();

        if (!jsonData.startsWith("{"))
            throw new JSONException("JSON Parse (not a valid starting point)");

        if (!jsonData.endsWith("}"))
            throw new JSONException("JSON Parse (not a valid ending point)");

        return parseJsonFile(jsonData);
    }

    public static ShellCommandEntry initialize(File entryFile) throws IOException, ShellException {
        if (entryFile == null)
            throw new NullPointerException("Entry file is null");

        if (!entryFile.exists())
            throw new FileNotFoundException("File at \"" + entryFile.getAbsolutePath() + "\" not found");

        if (!entryFile.canRead())
            throw new AccessDeniedException("The current user cannot read the file at \"" + entryFile.getAbsolutePath() + "\"");

        FileInputStream fis = new FileInputStream(entryFile);
        byte[] buff = fis.readAllBytes();
        fis.close();

        String data = new String(buff).trim();
        if (!data.startsWith("{"))
            throw new JSONException("JSON Parse (not a valid starting point)");

        if (!data.endsWith("}"))
            throw new JSONException("JSON Parse (not a valid ending point)");

        return parseJsonFile(data);
    }

}
