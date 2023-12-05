package net.bc100dev.commons.utils.io;

import net.bc100dev.commons.ApplicationException;
import net.bc100dev.commons.ApplicationIOException;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class WebIOStream {

    private final HttpsURLConnection conn;
    private final DataInputStream dis;
    private final Response response;

    private static final String[] PAYLOAD_METHODS = {
            // Submits data
            "POST",

            // Update or create a new item
            "PUT",

            // Partial Updates
            "PATCH",

            // Delete an item
            "DELETE"
    };

    public static WebIOStream openStream(String urlParam, String method, Map<String, String> headers, byte[] payload) throws IOException {
        try {
            URI _u = new URI(urlParam);
            URL url = _u.toURL();

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);

            if (headers != null) {
                Set<String> set = headers.keySet();
                if (!set.isEmpty()) {
                    for (String key : set)
                        conn.setRequestProperty(key, headers.get(key));
                }
            }

            //try (OutputStream os = conn.getOutputStream()) {
            //    os.write(payload.getBytes());
            //    os.flush();
            //}

            boolean doPayload = false;
            for (String _method : PAYLOAD_METHODS) {
                if (method.equals(_method)) {
                    doPayload = true;
                    break;
                }
            }

            if (doPayload) {
                if (payload == null)
                    throw new NullPointerException("Payload cannot be null; can be empty, if server supports non-required payload requests");

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload);
                    os.flush();
                }
            }

            conn.connect();

            Response _response = new Response(conn.getResponseCode(), conn.getResponseMessage());
            InputStream is;

            if (String.valueOf(conn.getResponseCode()).startsWith("2"))
                is = conn.getInputStream();
            else if (String.valueOf(conn.getResponseCode()).startsWith("4") || String.valueOf(conn.getResponseCode()).startsWith("5"))
                is = conn.getErrorStream();
            else
                throw new IOException("Unknown code " + conn.getResponseCode() + " received");

            return new WebIOStream(_response, conn, new DataInputStream(is));
        } catch (URISyntaxException e) {
            throw new ApplicationIOException("Error in creating a URI", e);
        }
    }

    protected WebIOStream(Response response, HttpsURLConnection conn, DataInputStream dis) {
        this.conn = conn;
        this.dis = dis;
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public byte[] readContents() throws IOException {
        if (dis == null)
            return new byte[0];

        return dis.readAllBytes();
    }

    public void close() throws IOException {
        if (dis != null)
            dis.close();

        conn.disconnect();
    }

    public static class Response {

        private final int code;
        private final String message;

        protected Response(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

}
