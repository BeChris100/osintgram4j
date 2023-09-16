package net.bc100dev.insta.api.privates;

import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;

public class InstagramClient {

    private static final String API_URL = "https://i.instagram.com/api/%s/";
    private static final String USER_AGENT = Constants.USER_AGENT;
    private static final String IG_SIG_KEY = Constants.IG_SIG_KEY;
    private static final String IG_CAPABILITIES = Constants.IG_CAPABILITIES;
    private static final String SIG_KEY_VERSION = Constants.SIG_KEY_VERSION;
    private static final String APPLICATION_ID = Constants.APPLICATION_ID;

    private final String username, password;
    private final CookieManager cookieManager;
    private final Random random;

    private String uuid, deviceId, sessionId, signatureKey, keyVersion, igCapabilities,
    appId, userAgent, appVersion, androidRelease, phoneManufacturer, phoneDevice,
    phoneModel, phoneDpi, phoneResolution, phoneChipset, versionCode, adId;
    private int androidVersion;

    public InstagramClient(String username, String password) {
        this.username = username;
        this.password = password;
        this.cookieManager = new CookieManager();
        this.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.uuid = generateUUID(false);
        this.deviceId = generateDeviceID(null);
        this.sessionId = generateUUID(false);
        this.signatureKey = IG_SIG_KEY;
        this.keyVersion = SIG_KEY_VERSION;
        this.igCapabilities = IG_CAPABILITIES;
        this.appId = APPLICATION_ID;
        this.userAgent = USER_AGENT;
        this.appVersion = Constants.APP_VERSION;
        this.androidRelease = Constants.ANDROID_RELEASE;
        this.androidVersion = Constants.ANDROID_VERSION;
        this.phoneManufacturer = Constants.PHONE_MANUFACTURER;
        this.phoneDevice = Constants.PHONE_DEVICE;
        this.phoneModel = Constants.PHONE_MODEL;
        this.phoneDpi = Constants.PHONE_DPI;
        this.phoneResolution = Constants.PHONE_RESOLUTION;
        this.phoneChipset = Constants.PHONE_CHIPSET;
        this.versionCode = Constants.VERSION_CODE;
        this.adId = generateAdID(null);
        this.random = new Random();
    }

    public void login() {
        // Custom implementation
    }

    private String generateUUID(boolean returnHex) {
        UUID uu = UUID.randomUUID();
        return returnHex ? uuid.replaceAll("-", "") : uuid;
    }

    private String generateDeviceID(String seed) {
        if (seed != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(seed.getBytes(StandardCharsets.UTF_8));
                byte[] digest = md.digest();
                return "android-" + Hex.encodeHexString(digest).substring(0, 16);
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
        }

        return generateUUID(true).substring(0, 16);
    }

    private String generateAdID(String seed) {
        if (seed == null)
            seed = username;

        if (seed != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                md.update(seed.getBytes(StandardCharsets.UTF_8));
                byte[] digest = md.digest();
                return generateUUID(false, Hex.encodeHexString(digest));
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
        }

        return generateUUID(false, null);
    }

    private String generateUUID(boolean returnHex, String seed) {
        if (seed != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(seed.getBytes(StandardCharsets.UTF_8));
                byte[] digest = md.digest();
                UUID uuid = UUID.nameUUIDFromBytes(digest);
                return returnHex ? uuid.toString().replace("-", "") : uuid.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return generateUUID(returnHex);
    }

    private String getCookieValue(String key, String domain) {
        // Custom Implementation of Cookie Retrieval
        return null;
    }

    private String getCsrfToken() {
        return getCookieValue("csrftoken", "");
    }

    private String getToken() {
        return getCsrfToken();
    }

    private String getAuthenticatedUserId() {
        return getCookieValue("ds_user_id", "");
    }

    private String getAuthenticatedUserName() {
        return getCookieValue("ds_user", "");
    }

    private String getPhoneID() {
        return generateUUID(false, deviceId);
    }

    private int getTimezoneOffset() {
        long now = System.currentTimeMillis();
        long utcNow = System.currentTimeMillis() - (now % 1000);
        return (int) ((now - utcNow) / 1000);
    }

    private String getRankToken() {
        String authUserId = getAuthenticatedUserId();
        if (authUserId != null)
            return authUserId + "_" + uuid;

        return null;
    }

    private Map<String, String> getAuthenticatedParams() {
        Map<String, String> params = new HashMap<>();
        params.put("_csrftoken", getCsrfToken());
        params.put("_uuid", uuid);
        params.put("_uid", getAuthenticatedUserId());

        return params;
    }

    private Map<String, String> getDefaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);
        headers.put("Connection", "close");
        headers.put("Accept", "*/*");
        headers.put("Accept-Language", "en-US");
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("X-IG-Capabilities", igCapabilities);
        headers.put("X-IG-Connection-Type", "WIFI");
        headers.put("X-IG-Connection-Speed", (random.nextInt(4000) + 1000) + "kbps");
        headers.put("X-IG-App-ID", appId);
        headers.put("X-IG-Bandwidth-Speed-KBPS", "-1.000");
        headers.put("X-IG-Bandwidth-TotalBytes-B", "0");
        headers.put("X-IG-Bandwidth-TotalTime-MS", "0");
        headers.put("X-FB-HTTP-Engine", Constants.FB_HTTP_ENGINE);

        return headers;
    }

    private String generateSignature(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(signatureKey.getBytes("ASCII"));
            md.update(data.getBytes("ASCII"));
            byte[] digest = md.digest();
            return Hex.encodeHexString(digest);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
