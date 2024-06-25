package com.instagram.api.user;

import com.instagram.api.Constants;
import com.instagram.api.net.IHttpMethod;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

public class User {

    private final boolean self;
    private final String id, username;

    private boolean _2faLogin;

    protected User(boolean self, String id, String username, boolean _2faLogin) {
        this.self = self;
        this.id = id;
        this.username = username;
        this._2faLogin = _2faLogin;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }

    public boolean isSelf() {
        return self;
    }

    public boolean requireTwoFactorLogin() {
        return _2faLogin;
    }

    public void pass2fa() {
        _2faLogin = true;
    }

    private void command(String endpoint, IHttpMethod method, byte[] bodyData, Map<String, String> additionalHeaders) throws IOException {
        try {
            URL url = new URI(String.format("%s/accounts/%s", Constants.URL_API, endpoint)).toURL();
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod(String.valueOf(method));
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }
    }

    /*
    async changePassword(oldPassword, newPassword) {
        const { body } = await this.client.request.send({
            url: '/api/v1/accounts/change_password/',
            method: 'POST',
            form: this.client.request.sign({
                _csrftoken: this.client.state.cookieCsrfToken,
                _uid: this.client.state.cookieUserId,
                _uuid: this.client.state.uuid,
                old_password: oldPassword,
                new_password1: newPassword,
                new_password2: newPassword,
            }),
        });
        return body;
    }

    async setPrivate() {
        return this.command('set_private');
    }
    async setPublic() {
        return this.command('set_public');
    }

    async command(command) {
        const { body } = await this.client.request.send({
            url: `/api/v1/accounts/${command}/`,
            method: 'POST',
            form: this.client.request.sign({
                _csrftoken: this.client.state.cookieCsrfToken,
                _uid: this.client.state.cookieUserId,
                _uuid: this.client.state.uuid,
            }),
        });
        return body;
    }
     */

}
