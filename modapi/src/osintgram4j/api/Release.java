package osintgram4j.api;

import java.util.List;

public record Release(String versionName, int versionCode, String name, String body, boolean draft, boolean prerelease, List<AssetFile> assetFiles) {
    public record AssetFile(String name, long size, String browserDownloadUrl) {}
}
