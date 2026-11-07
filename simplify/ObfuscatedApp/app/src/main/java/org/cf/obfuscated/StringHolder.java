package org.cf.obfuscated;

import android.util.Base64;

import org.cf.crypto.XORCrypt;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class StringHolder {

    public static final String xorKey = new String(new char[]{'s', 'e', 'x', 'y', ' ', 't', 'i', 'm', 'e', '!'});
    //public static final String desKey = new String(new char[]{'m', 'a', 'l', 'j', 'u', 'a', 'r', 'e', 'z'});
    //    public static final String aesKey = new String(new char[]{'m', 'a', 'l', 'j', 'u', 'a', 'r', 'e', 'z'});
    //    public static final byte[] aesSalt = new byte[]{0xd, 0xe, 0xa, 0xd, 0xb, 0xe, 0xe, 0xf};

    private static final String[] base64edStrings = new String[]{
            // "Tell me of your homeworld, Usul."
            "VGVsbCBtZSBvZiB5b3VyIGhvbWV3b3JsZCwgVXN1bC4=",
            // "What do you call the mouse shadow on the second moon?"
            "V2hhdCBkbyB5b3UgY2FsbCB0aGUgbW91c2Ugc2hhZG93IG9uIHRoZSBzZWNvbmQgbW9vbj8=",
            // Some good books...
            "U25vdyBDcmFzaA==", "TmV1cm9tYW5jZXI=", "VGhlIEhpdGNoaGlrZXIncyBHdWlkZSB0byB0aGUgR2FsYXh5", "U3RyYW5nZXIgaW4gYSBTdHJhbmdlIExhbmQ=", "RHVuZSwgbG9s",};

    private static byte[][] xoredStrings = new byte[][]{
            // "secretMethod"
            new byte[]{0, 22, 27, 83, 29, 17, 40, 0, 17, 28, 22, 16},
            // "java.lang.Integer"
            new byte[]{25, 21, 31, 12, 15, 76, 18, 11, 70, 15, 105, 79, 17, 17, 31, 28, 23},};

    private static byte[][] aesedStrings = new byte[][]{
            // "org.cf.obfuscated.Reflection"
            new byte[]{81, 50, 84, 56, 113, 47, 121, 66, 52, 113, 72, 81, 57, 113, 70, 114, 121, 106, 113, 65, 89, 65, 61, 61, 58, 77, 68, 67, 118, 116, 109, 117, 109, 88, 110, 113, 71, 90, 103, 120, 52, 113, 53, 77, 66, 99, 83, 107, 66, 67, 110, 86, 78, 57, 116, 84, 73, 49, 106, 110, 79, 86, 54, 72, 88, 68, 50, 115, 61, 58, 48, 73, 78, 81, 78, 100, 77, 54, 66, 80, 114, 82, 52, 48, 99, 114, 68, 121, 52, 113, 85, 80, 110, 99, 121, 47, 116, 56, 80, 83, 51, 100, 81, 90, 75, 72, 80, 49, 43, 85, 79, 108, 115, 61},
            // "secretMethod2"
            new byte[]{118, 53, 72, 51, 74, 51, 67, 80, 74, 81, 89, 67, 71, 115, 102, 80, 101, 90, 118, 121, 86, 119, 61, 61, 58, 69, 108, 102, 102, 119, 103, 75, 51, 70, 89, 78, 110, 89, 101, 65, 85, 86, 85, 104, 104, 74, 104, 76, 80, 43, 89, 57, 85, 83, 121, 79, 88, 65, 87, 84, 76, 51, 121, 102, 85, 47, 84, 99, 61, 58, 67, 55, 97, 98, 54, 104, 119, 55, 50, 76, 79, 121, 81, 118, 90, 87, 85, 116, 117, 110, 89, 81, 61, 61},};

    private static String[] desedStrings = new String[]{
            // "org.cf.obfuscated.Reflection"
            "7HdzAs7oaiE9rVhLtABoHU6HknlvDLInjJUnwTH17n8=",
            // "secretMethod2"
            "ij6sEnG8i3gz7P0R6Cyw3A=="};

    private static String[] dummyEncodedStrings = new String[]{
            // "org.cf.obfuscated.Reflection"
            "b3JnLmNmLm9iZnVzY2F0ZWQuUmVmbGVjdGlvbg==",
            // "secretMethod2"
            "c2VjcmV0TWV0aG9kMg=="};

    private static final Map<Integer, String> strings = cacheStrings();


    private static Map<Integer, String> cacheStrings() {
        Map<Integer, String> cache = new HashMap<>();

        int pos = 0;
        for (String string : base64edStrings) {
            byte[] data = Base64.decode(string, Base64.DEFAULT);
            String decoded = null;
            try {
                decoded = new String(data, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            cache.put(pos++, decoded);
        }

        for (byte[] encoded : xoredStrings) {
            String decoded = null;
            try {
                decoded = XORCrypt.decode(new String(encoded, "UTF-8"), xorKey);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            cache.put(pos++, decoded);
        }

        /*
         * Any non-trivial encryption relies on a lot of crypto classes which are very different
         * between Android and the JVM. This means reflection isn't going to work, and smalivm doesn't
         * support instance registers. This means only dumb crypto is supported right now.
         */

        for (String string : dummyEncodedStrings) {
            byte[] data = Base64.decode(string, Base64.DEFAULT);
            String decoded = null;
            try {
                decoded = new String(data, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            cache.put(pos++, decoded);
        }
        //        try {
        //            for (String encrypted : desedStrings) {
        //                String decoded = DES.decrypt(encrypted, desKey);
        //                cache.put(pos++, decoded);
        //            }
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }

        //        try {
        //            AesCbcWithIntegrity.SecretKeys aesSecretKey = AesCbcWithIntegrity.generateKeyFromPassword(aesKey, aesSalt);
        //            for (byte[] encryptedBytes : aesedStrings) {
        //                AesCbcWithIntegrity.CipherTextIvMac encrypted = new AesCbcWithIntegrity.CipherTextIvMac(new String(encryptedBytes));
        //                byte[] decodedBytes = AesCbcWithIntegrity.decrypt(encrypted, aesSecretKey);
        //                String decoded = new String(decodedBytes);
        //                cache.put(pos++, decoded);
        //            }
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }

        //    for ( Map.Entry<Integer, String> entry : cache.entrySet() ) {
        //      System.out.println("cache #" + entry.getKey() + " = " + entry.getValue());
        //    }

        return cache;
    }

    public static String get(int index) {
        return strings.get(index);
    }

    public static void set(int index, String newValue) {
        strings.put(index, newValue);
    }

}
