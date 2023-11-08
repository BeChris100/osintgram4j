package net.bc100dev.osintgram4j.cache;

import net.bc100dev.commons.ApplicationIOException;
import net.bc100dev.commons.utils.io.FileUtil;
import net.bc100dev.osintgram4j.sh.ShellException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.AccessDeniedException;
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

    public List<File> listCacheDirectories() throws IOException {
        List<File> dirs = new ArrayList<>();

        for (CacheDir cache : cacheDirList) {
            File dir = cache.cacheDir();
            if (!dir.exists())
                continue;

            if (!dir.canRead())
                throw new AccessDeniedException("Cannot access file at \"" + dir.getAbsolutePath() + "\" (Permission denied)");

            if (!dir.isDirectory())
                throw new IllegalStateException("\"" + dir.getAbsolutePath() + "\" is not a directory");

            List<String> contents = FileUtil.scanFolders(dir.getAbsolutePath(), false);
            List<String> contentsCached = new ArrayList<>();

            for (String content : contents) {
                if (contentsCached.contains(content))
                    continue;

                File fileContent = new File(content);
                if (!fileContent.canRead())
                    throw new AccessDeniedException("Cannot access file at \"" + fileContent.getAbsolutePath() + "\" (Permission denied)");

                dirs.add(fileContent);
                contentsCached.add(content);
            }
        }

        return dirs;
    }

    public List<File> listCacheFiles() throws IOException {
        List<File> files = new ArrayList<>();

        for (CacheDir cache : cacheDirList) {
            File dir = cache.cacheDir();
            if (!dir.exists())
                continue;

            if (!dir.canRead())
                throw new AccessDeniedException("Cannot access file at \"" + dir.getAbsolutePath() + "\" (Permission denied)");

            if (!dir.isDirectory())
                throw new IllegalStateException("\"" + dir.getAbsolutePath() + "\" is not a directory");

            List<String> contents = FileUtil.scanFiles(dir.getAbsolutePath(), false);
            List<String> contentsCached = new ArrayList<>();

            for (String content : contents) {
                if (contentsCached.contains(content))
                    continue;

                File fileContent = new File(content);
                if (!fileContent.canRead())
                    throw new AccessDeniedException("Cannot access file at \"" + fileContent.getAbsolutePath() + "\" (Permission denied)");

                files.add(fileContent);
                contentsCached.add(content);
            }
        }

        return files;
    }

    public byte[] read(CacheDir cacheDir, File file) throws IOException {
        if (file.isAbsolute())
            throw new ApplicationIOException("The file location cannot be absolute; received \"" + file.getAbsolutePath() + "\"");

        File f = new File(cacheDir.cacheDir().getAbsolutePath() + "/" + file.getPath());
        if (!f.exists()) {
            FileUtil.createFile(f.getAbsolutePath(), true);
            return new byte[0];
        }

        if (!f.canRead())
            throw new AccessDeniedException("Cannot read data from \"" + f.getAbsolutePath() + "\" (Permission denied)");

        return FileUtil.readBytes(f.getAbsolutePath());
    }

    public void write(CacheDir cacheDir, File file, byte[] content) throws IOException {
        if (file.isAbsolute())
            throw new ApplicationIOException("The file location cannot be absolute; received \"" + file.getAbsolutePath() + "\"");

        File f = new File(cacheDir.cacheDir().getAbsolutePath() + "/" + file.getPath());
        if (!f.exists())
            FileUtil.createFile(f.getAbsolutePath(), true);

        if (!f.canWrite())
            throw new AccessDeniedException("Cannot write data to \"" + f.getAbsolutePath() + "\" (Permission denied)");

        FileUtil.write(f.getAbsolutePath(), content);
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
