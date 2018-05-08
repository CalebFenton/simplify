package org.cf.crypto;

import android.util.Base64;

import java.security.MessageDigest;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class TrippleDES {
    private static String ALGO = "DESede/ECB/PKCS7Padding";

    public static String encrypt(String message, String secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, getSecreteKey(secretKey));
        byte[] plainTextBytes = message.getBytes("UTF-8");
        byte[] buf = cipher.doFinal(plainTextBytes);
        byte[] base64Bytes = Base64.encode(buf, Base64.DEFAULT);
        String base64EncryptedString = new String(base64Bytes);

        return base64EncryptedString;
    }

    public static String decrypt(String encryptedText, String secretKey) throws Exception {
        byte[] message = Base64.decode(encryptedText.getBytes(), Base64.DEFAULT);
        Cipher decipher = Cipher.getInstance(ALGO);
        decipher.init(Cipher.DECRYPT_MODE, getSecreteKey(secretKey));
        byte[] plainText = decipher.doFinal(message);

        return new String(plainText, "UTF-8");
    }

    public static SecretKey getSecreteKey(String secretKey) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digestOfPassword = md.digest(secretKey.getBytes("UTF-8"));
        byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
        SecretKey key = new SecretKeySpec(keyBytes, "DESede");

        return key;
    }
}
