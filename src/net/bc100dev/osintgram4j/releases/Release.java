package net.bc100dev.osintgram4j.releases;

import net.bc100dev.commons.ApplicationException;
import net.bc100dev.commons.utils.io.WebIOStream;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Release {

    public static List<ReleaseData> getReleases(String user, String repo) throws IOException, ApplicationException {
        WebIOStream stream = WebIOStream.openStream("https://api.github.com/repos/" + user + "/" + repo + "/releases", "GET", null);
        byte[] buff = stream.readContents();
        stream.close();

        String jsonArrData = new String(buff).trim();
        if (!jsonArrData.startsWith("["))
            throw new ApplicationException("JSON Error (not a valid starting array character)");

        if (!jsonArrData.endsWith("]"))
            throw new ApplicationException("JSON Error (not a valid ending array character)");

        JSONArray jsonArray = new JSONArray(jsonArrData);
        if (jsonArray.isEmpty())
            return new ArrayList<>();

        List<ReleaseData> releaseDataList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            List<ReleaseAsset> assetList = new ArrayList<>();

            JSONObject objRoot = jsonArray.getJSONObject(i);
            JSONArray assetArr = objRoot.getJSONArray("assets");

            for (int j = 0; j < assetArr.length(); j++) {
                JSONObject assetObj = assetArr.getJSONObject(j);

                ReleaseAsset asset = new ReleaseAsset(
                        assetObj.getString("name"),
                        assetObj.isNull("label") ? assetObj.getString("name") : assetObj.getString("label"),
                        assetObj.getString("browser_download_url")
                );

                if (assetList.contains(asset))
                    continue;

                assetList.add(asset);
            }

            ReleaseData data = new ReleaseData(
                    objRoot.getString("tag_name"),
                    objRoot.getString("name"),
                    objRoot.getBoolean("draft"),
                    objRoot.getBoolean("prerelease"),
                    assetList
            );

            if (releaseDataList.contains(data))
                continue;

            releaseDataList.add(data);
        }

        return releaseDataList;
    }

}
