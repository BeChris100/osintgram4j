package net.bc100dev.osintgram4j.config;

import net.bc100dev.commons.utils.io.FileUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Credential {

    private final String username, password;

    protected Credential(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private static Credential fromJsonObject(String jsonObjectRoot) throws CredentialsException {
        jsonObjectRoot = jsonObjectRoot.trim();
        if (!(jsonObjectRoot.startsWith("{") && jsonObjectRoot.endsWith("}")))
            throw new CredentialsException("The given String data is not a valid JSON Object");

        JSONObject objRoot = new JSONObject(jsonObjectRoot);
        if (!objRoot.has("username"))
            throw new CredentialsException("The username key in the JSON object has not been found");

        if (!objRoot.has("password"))
            throw new CredentialsException("The password key in the JSON object has not been found");

        if (objRoot.get("username") instanceof String un && objRoot.get("password") instanceof String pw)
            return new Credential(un, pw);

        throw new CredentialsException("The username or the password value in the JSON object is not a string");
    }

    public static Credential getCredentialsFromConfig(File configFile) throws IOException, CredentialsException {
        if (configFile == null)
            throw new NullPointerException("The file is passed as null, therefore cannot access");

        if (!configFile.exists())
            throw new FileNotFoundException("The file at \"" + configFile.getAbsolutePath() + "\" does not exist");

        if (!configFile.canRead())
            throw new AccessDeniedException("The current user cannot access the file at \"" + configFile.getAbsolutePath() + "\"");

        FileInputStream fis = new FileInputStream(configFile);
        Properties props = new Properties();

        props.load(fis);
        fis.close();

        if (!props.containsKey("username"))
            throw new CredentialsException("The credential data is missing the \"username\" key");

        if (!props.containsKey("password"))
            throw new CredentialsException("The credential data is missing the \"password\" key");

        return new Credential(props.getProperty("username"), props.getProperty("password"));
    }

    public static List<Credential> getCredentialsFromJson(File jsonFile) throws IOException, CredentialsException {
        if (jsonFile == null)
            throw new NullPointerException("The file is passed as null, therefore cannot access");

        if (!jsonFile.exists())
            throw new FileNotFoundException("The file at \"" + jsonFile.getAbsolutePath() + "\" does not exist");

        if (!jsonFile.canRead())
            throw new AccessDeniedException("The current user cannot access the file at \"" + jsonFile.getAbsolutePath() + "\"");

        String jsonData = FileUtil.readFileString(jsonFile.getAbsolutePath()).trim();
        List<Credential> credentialList = new ArrayList<>();

        if (jsonData.startsWith("{") && jsonData.endsWith("}")) {
            credentialList.add(fromJsonObject(jsonData));
            return credentialList;
        } else if (jsonData.startsWith("[") && jsonData.endsWith("]")) {
            JSONArray arr = new JSONArray(jsonData);
            if (arr.isEmpty())
                throw new CredentialsException("No credentials given");

            if (arr.length() == 1) {
                if (arr.get(0) instanceof JSONObject j)
                    credentialList.add(fromJsonObject(j.toString()));
                else
                    throw new CredentialsException("The JSON object at the first JSON array index is not valid");

                return credentialList;
            }

            for (int i = 0; i < arr.length(); i++) {
                if (arr.get(i) instanceof JSONObject j)
                    credentialList.add(fromJsonObject(j.toString()));
                else
                    throw new CredentialsException("The JSON object at JSON array index " + i + " is not valid");
            }
        }

        return credentialList;
    }

}
