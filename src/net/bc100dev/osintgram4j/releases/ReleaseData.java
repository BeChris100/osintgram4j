package net.bc100dev.osintgram4j.releases;

import java.util.List;

public class ReleaseData {

    private final String tagName, displayName;
    private final boolean draft, preRelease;
    private final List<ReleaseAsset> releaseAssets;

    protected ReleaseData(String tagName, String displayName, boolean draft, boolean preRelease, List<ReleaseAsset> releaseAssets) {
        this.tagName = tagName;
        this.displayName = displayName;
        this.draft = draft;
        this.preRelease = preRelease;
        this.releaseAssets = releaseAssets;
    }

    public String getTagName() {
        return tagName;
    }

    public String getName() {
        return displayName;
    }

    public boolean isDraft() {
        return draft;
    }

    public boolean isPreRelease() {
        return preRelease;
    }

    public List<ReleaseAsset> getReleaseAssets() {
        return releaseAssets;
    }
}
