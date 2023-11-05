package net.bc100dev.osintgram4j.cache;

import net.bc100dev.osintgram4j.sh.ShellException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppCache {

    private final String username;
    private final List<CacheDir> cacheDirList;

    protected AppCache(String username, List<CacheDir> cacheDirList) {
        this.username = username;
        this.cacheDirList = cacheDirList;
    }

    public String getUsername() {
        return username;
    }

    public List<CacheDir> getTargets() {
        return cacheDirList;
    }

    public AppCache initialize(JSONObject object) throws ShellException {
        if (!object.has("ConnectedUser"))
            throw new ShellException("ConnectedUser key is not defined");

        if (!object.has("Targets"))
            throw new ShellException("Targets key is not defined");

        String un = object.getString("ConnectedUser");

        List<CacheDir> cacheList = new ArrayList<>();
        JSONArray targets = object.getJSONArray("Targets");

        if (!targets.isEmpty()) {
            for (int i = 0; i < targets.length(); i++) {
                JSONObject target = targets.getJSONObject(i);
                if (!target.has("Target"))
                    throw new ShellException("Target at " + i + " not specified");

                if (!target.has("CacheDir"))
                    throw new ShellException("CacheDir at " + i + " not specified");

                CacheDir cache = new CacheDir(target.getString("Target"), new File(target.getString("CacheDir")));
                if (cacheList.contains(cache))
                    continue;

                cacheList.add(cache);
            }
        }

        return new AppCache(un, cacheList);
    }

    public List<AppCache> initialize(JSONArray array) throws ShellException {
        if (array == null)
            throw new NullPointerException("JSON Array is null");

        if (array.isEmpty())
            return new ArrayList<>();

        List<AppCache> appCaches = new ArrayList<>();

        for (int i = 0; i < array.length(); i++)
            appCaches.add(initialize(array.getJSONObject(i)));

        return appCaches;
    }

    public static boolean existsUser(JSONArray array, String username) {
        if (array == null)
            throw new NullPointerException("JSON Array is defined as null");

        if (array.isEmpty())
            return false;

        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if (!obj.has("ConnectedUser"))
                continue;

            if (obj.getString("ConnectedUser").equals(username))
                return true;
        }

        return false;
    }

}
