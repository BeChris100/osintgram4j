package net.bc100dev.osintgram4j.releases;

public class ReleaseAsset {

    // (String fileName, String label, String browserDownloadUrl)

    private final String fileName, label, browserDownloadUrl;

    protected ReleaseAsset(String fileName, String label, String browserDownloadUrl) {
        this.fileName = fileName;
        this.label = label;
        this.browserDownloadUrl = browserDownloadUrl;
    }

    public String getLabel() {
        return label;
    }

    public String getFileName() {
        return fileName;
    }

    public String getBrowserDownloadUrl() {
        return browserDownloadUrl;
    }
}
