package net.bc100dev.osintgram4j.sh;

import net.bc100dev.commons.ResourceManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

public class ShellCommandEntry {

    private final int version;
    private final String label, pkgName;
    private final List<ShellCaller> callers;

    private ShellCommandEntry(int version, String label, String pkgName, List<ShellCaller> callers) {
        this.version = version;
        this.label = label;
        this.pkgName = pkgName;
        this.callers = callers;
    }

    public int getPackageVersion() {
        return version;
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

        String pkgName = obj.getString("package"),
                label = obj.getString("label");
        int version = obj.getInt("version");

        JSONArray arr = obj.getJSONArray("command_list");
        List<ShellCaller> callerList = new ArrayList<>();
        if (!arr.isEmpty()) {
            for (int i = 0; i < arr.length(); i++) {
                JSONObject cmdObj = arr.getJSONObject(i);

                if (!cmdObj.has("command"))
                    throw new ShellException(String.format("\"%s\" key at entry %d not found", "command", i + 1));

                if (!cmdObj.has("description"))
                    throw new ShellException(String.format("\"%s\" key at entry %d not found", "description", i + 1));

                if (!cmdObj.has("class"))
                    throw new ShellException(String.format("\"%s\" key at entry %d not found", "class", i + 1));

                if (!cmdObj.has("alternates"))
                    throw new ShellException(String.format("\"%s\" key at entry %d not found", "command", i + 1));

                List<String> alternatesList = new ArrayList<>();
                JSONArray altArr = cmdObj.getJSONArray("alternates");

                for (int j = 0; j < altArr.length(); j++) {
                    if (altArr.get(j) instanceof String str) {
                        if (alternatesList.contains(str))
                            continue;

                        alternatesList.add(str);
                    }
                }

                String[] alternates = new String[alternatesList.size()];
                for (int j = 0; j < alternatesList.size(); j++)
                    alternates[j] = alternatesList.get(j);

                String cmd = cmdObj.getString("command"),
                        description = cmdObj.getString("description"),
                        _class = cmdObj.getString("class");

                if (_class.startsWith("."))
                    _class = pkgName + _class;

                callerList.add(new ShellCaller(cmd, description, _class, alternates));
            }
        }

        return new ShellCommandEntry(version, label, pkgName, callerList);
    }

    public static ShellCommandEntry initialize(String resourceFile) throws IOException, ShellException {
        ResourceManager res = new ResourceManager(ShellCommandEntry.class, false);
        if (!res.resourceExists(resourceFile))
            throw new NullPointerException("Could not find resource file at \"" + resourceFile + "\"");

        InputStream is = res.getResourceInputStream(resourceFile);
        byte[] buff = is.readAllBytes();
        is.close();

        String data = new String(buff).trim();
        if (!data.startsWith("{"))
            throw new JSONException("JSON Parse (not a valid starting point)");

        if (!data.endsWith("}"))
            throw new JSONException("JSON Parse (not a valid ending point)");

        return parseJsonFile(data);
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
