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
    private final DataInputStream disIn, disErr;
    private final Response response;

    public static WebIOStream openStream(String urlParam, String method, Map<String, String> headers) throws IOException {
        try {
            URI _u = new URI(urlParam);
            URL url = _u.toURL();

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod(method);

            if (headers != null) {
                Set<String> set = headers.keySet();
                if (!set.isEmpty()) {
                    for (String key : set)
                        conn.setRequestProperty(key, headers.get(key));
                }
            }

            conn.connect();

            Response _response = new Response(conn.getResponseCode(), conn.getResponseMessage());
            DataInputStream disIn = null, disErr = null;

            InputStream isIn = conn.getInputStream(), isErr = conn.getErrorStream();
            if (isIn != null)
                disIn = new DataInputStream(isIn);

            if (isErr != null)
                disErr = new DataInputStream(isErr);

            return new WebIOStream(_response, conn, disIn, disErr);
        } catch (URISyntaxException e) {
            throw new ApplicationIOException("Error in creating a URI", e);
        }
    }

    public static WebIOStream openStream(String urlParam, String method, Map<String, String> headers, String pushContents) throws IOException {
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

            try (OutputStream os = conn.getOutputStream()) {
                os.write(pushContents.getBytes());
                os.flush();
            }

            conn.connect();

            Response _response = new Response(conn.getResponseCode(), conn.getResponseMessage());
            DataInputStream disIn = null, disErr = null;

            InputStream isIn = conn.getInputStream(), isErr = conn.getErrorStream();
            if (isIn != null)
                disIn = new DataInputStream(isIn);

            if (isErr != null)
                disErr = new DataInputStream(isErr);

            return new WebIOStream(_response, conn, disIn, disErr);
        } catch (URISyntaxException e) {
            throw new ApplicationIOException("Error in creating a URI", e);
        }
    }

    protected WebIOStream(Response response, HttpsURLConnection conn, DataInputStream disIn, DataInputStream disErr) {
        this.conn = conn;
        this.disIn = disIn;
        this.disErr = disErr;
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public byte[] readOutputContents() throws IOException {
        if (disIn == null)
            return new byte[0];

        return disIn.readAllBytes();
    }

    public byte[] readErrorContents() throws IOException {
        if (disErr == null)
            return new byte[0];

        return disErr.readAllBytes();
    }

    public void close() throws IOException {
        if (disIn != null)
            disIn.close();

        if (disErr != null)
            disErr.close();

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
