package net.bc100dev.osintgram4j.netx;

import net.bc100dev.commons.ApplicationException;
import net.bc100dev.commons.CLITools;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class ProxyInfo {

    private final String host;
    private final int port;
    private final ProxyConnectivityType type;

    protected ProxyInfo(String host, int port, ProxyConnectivityType type) {
        this.host = host;
        this.port = port;
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public ProxyConnectivityType getType() {
        return type;
    }

    public static List<ProxyInfo> load(String jsonData) throws ApplicationException {
        jsonData = jsonData.trim();
        if (!jsonData.startsWith("["))
            throw new JSONException("Could not load a Proxy Server list: not a valid JSON Data");

        if (!jsonData.endsWith("]"))
            throw new JSONException("Could not load a Proxy Server list: not a valid JSON Data");

        JSONArray arr = new JSONArray(jsonData);
        if (arr.isEmpty())
            throw new ApplicationException("The Proxy Server list cannot be empty");

        for (int i = 0; i < arr.length(); i++) {
            if (arr.get(i) instanceof String) {
                String lineData = arr.getString(i);
                // expects: HTTP/SOCKS Host Port

                String[] pr = CLITools.translateCmdLine(lineData);
                if (pr.length == 0)
                    throw new ApplicationException("No arguments given to the JSON Data on Index " + i);

                if (pr.length != 3)
                    throw new ApplicationException();
            }
        }

        return null;
    }

}
