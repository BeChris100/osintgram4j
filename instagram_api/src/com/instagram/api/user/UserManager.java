package com.instagram.api.user;

import com.instagram.api.Constants;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class UserManager {

    /*
    TODO: Steps on encrypting the password, and logging in
     1. Retrieve the header values (see below comment from TypeScript/JavaScript)
     2. Apply the changes
     3. Encrypt the password
     4. Log in / Create new User
     */

    /*
    'x-ig-set-www-claim': wwwClaim,
    'ig-set-authorization': auth,
    'ig-set-password-encryption-key-id': pwKeyId,
    'ig-set-password-encryption-pub-key': pwPubKey,
     */

    public static User login(String username, String password) {
        //https://www.instagram.com/api/v1/accounts/login/ajax
        //https://www.instagram.com/api/v1/accounts/login/ajax?force_classic_login

        /*
            method: 'POST',
            url: '/api/v1/accounts/login/',
            form: this.client.request.sign({
                username,
                enc_password: `#PWD_INSTAGRAM:4:${time}:${encrypted}`,
                guid: this.client.state.uuid,
                phone_id: this.client.state.phoneId,
                _csrftoken: this.client.state.cookieCsrfToken,
                device_id: this.client.state.deviceId,
                adid: this.client.state.adid,
                google_tokens: '[]',
                login_attempt_count: 0,
                country_codes: JSON.stringify([{ country_code: '1', source: 'default' }]),
                jazoest: AccountRepository.createJazoest(this.client.state.phoneId),
            })
         */

        return new User(true, username, username, false);
    }

    public static User register(String username, String password, String email, String firstName) {
        /*
        async create({ username, password, email, first_name }) {
            const { body } = await Bluebird.try(() => this.client.request.send({
                method: 'POST',
                url: '/api/v1/accounts/create/',
                form: this.client.request.sign({
                    username,
                    password,
                    email,
                    first_name,
                    guid: this.client.state.uuid,
                    device_id: this.client.state.deviceId,
                    _csrftoken: this.client.state.cookieCsrfToken,
                    force_sign_up_code: '',
                    qs_stamp: '',
                    waterfall_id: this.client.state.uuid,
                    sn_nonce: '',
                    sn_result: '',
                }),
            })).catch(errors_1.IgResponseError, error => {
                switch (error.response.body.error_type) {
                    case 'signup_block': {
                        AccountRepository.accountDebug('Signup failed');
                        throw new ig_signup_block_error_1.IgSignupBlockError(error.response);
                    }
                    default: {
                        throw error;
                    }
                }
            });
            return body;
        }
         */
        return new User(false, username, password, false);
    }

    public static User twoFactorLogin(User user, String code) {
        /*
        async twoFactorLogin(options) {
            options = (0, lodash_1.defaultsDeep)(options, {
                trustThisDevice: '1',
                verificationMethod: '1',
            });
            const { body } = await this.client.request.send({
                url: '/api/v1/accounts/two_factor_login/',
                method: 'POST',
                form: this.client.request.sign({
                    verification_code: options.verificationCode,
                    _csrftoken: this.client.state.cookieCsrfToken,
                    two_factor_identifier: options.twoFactorIdentifier,
                    username: options.username,
                    trust_this_device: options.trustThisDevice,
                    guid: this.client.state.uuid,
                    device_id: this.client.state.deviceId,
                    verification_method: options.verificationMethod,
                }),
            });
            return body;
        }
         */
        return new User(false, user.getId(), user.getUsername(), false);
    }

    public static void logout(User user) {
        /*
        async logout() {
            const { body } = await this.client.request.send({
                method: 'POST',
                url: '/api/v1/accounts/logout/',
                form: {
                    guid: this.client.state.uuid,
                    phone_id: this.client.state.phoneId,
                    _csrftoken: this.client.state.cookieCsrfToken,
                    device_id: this.client.state.deviceId,
                    _uuid: this.client.state.uuid,
                },
            });
            return body;
        }
         */
    }

    public static class PasswordEncryption {

        public static void updateHeaderValues(HttpsURLConnection conn) {
            String wwwClaim = conn.getHeaderField("x-ig-set-www-claim");
            String auth = conn.getHeaderField("ig-set-authorization");
            String pwKeyId = conn.getHeaderField("ig-set-password-encryption-key-id");
            String pwPubKey = conn.getHeaderField("ig-set-password-encryption-pub-key");

            if (wwwClaim != null)
                Constants.Privates.WWW_CLAIM = wwwClaim;

            if (auth != null)
                Constants.Privates.IG_AUTH_HEADER = auth;

            if (pwKeyId != null)
                Constants.Privates.PASS_ENC_KEY_ID = pwKeyId;

            if (pwPubKey != null)
                Constants.Privates.PASS_ENC_PUB_KEY = pwPubKey;
        }

        public static String writePassword(String encryptedPassword) {
            long timestamp = System.currentTimeMillis() / 1000L;
            return String.format("#PWD_INSTAGRAM:4:%d:%s", timestamp, encryptedPassword);
        }

        public static String toEncryptedPassword(String password) throws GeneralSecurityException {
            KeyFactory keyFact = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(Constants.Privates.PASS_ENC_PUB_KEY));
            PublicKey publicKey = keyFact.generatePublic(pubKeySpec);

            SecureRandom secRand = new SecureRandom();
            byte[] randKey = new byte[32];
            secRand.nextBytes(randKey);
            byte[] iv = new byte[12];
            secRand.nextBytes(iv);

            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] rsaEncrypted = rsaCipher.doFinal(randKey);

            Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmPs = new GCMParameterSpec(128, iv);
            aesCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(randKey, "AES"), gcmPs);

            long time = System.currentTimeMillis() / 1000L;
            aesCipher.updateAAD(String.valueOf(time).getBytes());

            byte[] aesEncrypted = aesCipher.doFinal(password.getBytes());
            byte[] sizeBuffer = ByteBuffer.allocate(2).putShort((short) rsaEncrypted.length).array();
            byte[] authTag = aesCipher.getIV();

            int totalSize = 1 + Constants.Privates.PASS_ENC_KEY_ID.getBytes().length + iv.length +
                    sizeBuffer.length + rsaEncrypted.length + authTag.length + aesEncrypted.length;

            ByteBuffer bb = ByteBuffer.allocate(totalSize);
            bb.put((byte) 1);
            bb.put(Constants.Privates.PASS_ENC_KEY_ID.getBytes());
            bb.put(iv);
            bb.put(sizeBuffer);
            bb.put(rsaEncrypted);
            bb.put(authTag);
            bb.put(aesEncrypted);

            return Base64.getEncoder().encodeToString(bb.array());
        }

    }

}
