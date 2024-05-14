package osintgram4j.api;

import net.bc100dev.commons.ResourceManager;
import org.json.JSONArray;
import org.json.JSONObject;
import osintgram4j.commons.Titles;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ReleaseManager {

    private static byte[] readDataFromUrl(String url) throws IOException {
        try {
            URL _url = new URI(url).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) _url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            String code = String.valueOf(conn.getResponseCode());
            DataInputStream dis = switch (code) {
                case String s
                        when s.startsWith("2") -> new DataInputStream(conn.getInputStream());
                case String s
                        when s.startsWith("4") ||
                        s.startsWith("5") -> new DataInputStream(conn.getErrorStream());
                default -> null;
            };

            if (dis == null)
                throw new IOException("Unexpected code: " + code);

            byte[] data = dis.readAllBytes();
            dis.close();

            conn.disconnect();
            return data;
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
    }

    private static List<Release.AssetFile> getAssetFiles(JSONArray arr) {
        List<Release.AssetFile> files = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            String name = obj.getString("name");
            long size = obj.getLong("size");
            String url = obj.getString("browser_download_url");

            Release.AssetFile file = new Release.AssetFile(name, size, url);
            if (files.contains(file))
                continue;

            files.add(file);
        }

        return files;
    }

    public static List<Release> getReleases() throws IOException {
        byte[] releasesData = readDataFromUrl("https://api.github.com/repos/BeChris100/osintgram4j/releases");

        List<Release> releases = new ArrayList<>();
        JSONArray arr = new JSONArray(new String(releasesData));

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            String versionBody = obj.getString("tag_name");
            String versionName = versionBody;
            int versionCode = 1;

            if (versionBody.contains("-")) {
                String[] spl = versionBody.split("-");
                versionName = spl[0];
                versionCode = Integer.parseInt(spl[1]);
            }

            String name = obj.getString("name");
            String body = obj.getString("body");
            boolean draft = obj.getBoolean("draft");
            boolean prerelease = obj.getBoolean("prerelease");
            List<Release.AssetFile> files = getAssetFiles(obj.getJSONArray("assets"));

            releases.add(new Release(versionName, versionCode, name, body, draft, prerelease, files));
        }

        return releases;
    }

    private static boolean isNewer(String releaseVersion, String currentVersion) {
        String[] rParts = releaseVersion.replace("v", "").split("\\.");
        String[] cParts = currentVersion.replace("v", "").split("\\.");

        int limit = Math.max(rParts.length, cParts.length);

        for (int i = 0; i < limit; i++) {
            int rPart = i < rParts.length ? Integer.parseInt(rParts[i]) : 0;
            int cPart = i < cParts.length ? Integer.parseInt(cParts[i]) : 0;

            if (rPart == cPart)
                continue;

            if (rPart > cPart)
                return true;
            else if (rPart < cPart)
                return false;
        }

        return rParts.length > cParts.length;
    }

    public static boolean hasNewerRelease(List<Release> releases) throws IOException {
        ResourceManager mgr = new ResourceManager(ReleaseManager.class, false);
        Properties props = new Properties();
        InputStream is = mgr.getResourceInputStream("/net/bc100dev/osintgram4j/res/app_ver.cfg");
        props.load(is);
        is.close();

        String currentVersion = props.getProperty("BUILD_VERSION", "v0.1").substring(1);
        int currentVersionCode = Integer.parseInt(props.getProperty("BUILD_VERSION_CODE", "1"));

        for (Release release : releases) {
            String vN = release.versionName().substring(1);
            int vC = release.versionCode();
            if (vN.equals(currentVersion) && vC > currentVersionCode)
                return true;

            return isNewer(vN, currentVersion);
        }

        return false;
    }

    public static boolean hasNewerRelease() throws IOException {
        return hasNewerRelease(getReleases());
    }

}
