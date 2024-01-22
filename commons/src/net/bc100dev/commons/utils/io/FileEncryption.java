package net.bc100dev.commons.utils.io;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class FileEncryption {

    private static final byte[] salt = {
            (byte) 0x43, (byte) 0x76, (byte) 0x95, (byte) 0xc7,
            (byte) 0x5b, (byte) 0xd7, (byte) 0x45, (byte) 0x17
    };

    private static Cipher makeCipher(String pass, boolean doEncrypt) throws GeneralSecurityException {
        PBEKeySpec keySpec = new PBEKeySpec(pass.toCharArray(), salt, 65536, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey key = keyFactory.generateSecret(keySpec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(new byte[cipher.getBlockSize()]);

        if (doEncrypt)
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        else
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        return cipher;
    }

    public static byte[] toEncrypted(File sourceFile, String pass) throws IOException, GeneralSecurityException {
        byte[] decData;
        byte[] encData;

        if (sourceFile == null)
            throw new NullPointerException("Cannot access source file, as this is null");

        if (!sourceFile.exists())
            throw new FileNotFoundException("The source file does not exist at \"" + sourceFile.getAbsolutePath() + "\"");

        if (!sourceFile.canRead())
            throw new AccessDeniedException("Cannot read the source file at \"" + sourceFile.getAbsolutePath() + "\"");

        Cipher cipher = makeCipher(pass, true);

        FileInputStream inStream = new FileInputStream(sourceFile);

        int blockSize = 16;
        int paddedCount = blockSize - ((int) sourceFile.length() % blockSize);
        int padded = (int) sourceFile.length() + paddedCount;

        decData = new byte[padded];

        int ignore = inStream.read(decData);
        inStream.close();

        for (int i = (int) sourceFile.length(); i < padded; ++i)
            decData[i] = (byte) paddedCount;

        encData = cipher.doFinal(decData);

        return encData;
    }

    public static byte[] toDecrypted(File sourceFile, String pass) throws GeneralSecurityException, IOException {
        byte[] encData;
        byte[] decData;

        if (sourceFile == null)
            throw new NullPointerException("The source file is pointed to as null");

        if (!sourceFile.exists())
            throw new FileNotFoundException("File at \"" + sourceFile.getAbsolutePath() + "\" not found");

        if (!sourceFile.canRead())
            throw new AccessDeniedException("Cannot read \"" + sourceFile.getAbsolutePath() + "\"");

        Cipher cipher = makeCipher(pass, false);

        FileInputStream inStream = new FileInputStream(sourceFile);
        encData = new byte[(int) sourceFile.length()];
        int ignore = inStream.read(encData);
        inStream.close();

        decData = cipher.doFinal(encData);

        int padCount = (int) decData[decData.length - 1];

        if (padCount >= 1 && padCount <= 16)
            decData = Arrays.copyOfRange(decData, 0, decData.length - padCount);

        return decData;
    }

}
