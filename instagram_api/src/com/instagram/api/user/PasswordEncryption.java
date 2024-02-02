package com.instagram.api.user;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

class PasswordEncryption {

    public static String toEncryptedPassword(String b64PublicKey, byte passEncKeyId, String password) throws GeneralSecurityException {
        KeyFactory keyFact = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(b64PublicKey));
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

        ByteBuffer bb = ByteBuffer.allocate(1 + 1 + iv.length + sizeBuffer.length + rsaEncrypted.length + authTag.length + aesEncrypted.length);
        bb.put((byte) 1);
        bb.put(passEncKeyId);
        bb.put(iv);
        bb.put(sizeBuffer);
        bb.put(rsaEncrypted);
        bb.put(authTag);
        bb.put(aesEncrypted);

        return Base64.getEncoder().encodeToString(bb.array());
    }

}
