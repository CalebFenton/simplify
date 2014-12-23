package org.cf.obfuscated;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class StringHolder {

  public static final String xorKey = new String(new char[]{'s', 'e', 'x', 'y', ' ', 't', 'i', 'm', 'e', '!'});
  public static final String desKey = new String(new char[]{'m', 'a', 'l', 'j', 'u', 'a', 'r', 'e', 'z'});

  private static final String[] base64edStrings = new String[]{
      // "Tell me of your homeworld, Usul."
      "VGVsbCBtZSBvZiB5b3VyIGhvbWV3b3JsZCwgVXN1bC4=",
      // "What do you call the mouse shadow on the second moon?"
      "V2hhdCBkbyB5b3UgY2FsbCB0aGUgbW91c2Ugc2hhZG93IG9uIHRoZSBzZWNvbmQgbW9vbj8=",
      // Some good books...
      "U25vdyBDcmFzaA==",
      "TmV1cm9tYW5jZXI=",
      "VGhlIEhpdGNoaGlrZXIncyBHdWlkZSB0byB0aGUgR2FsYXh5",
      "U3RyYW5nZXIgaW4gYSBTdHJhbmdlIExhbmQ=",
      "RHVuZSwgbG9s",
  };

  private static byte[][] xoredStrings = new byte[][]{
      // "secretMethod"
      new byte[] { 0, 22, 27, 83, 29, 17, 40, 0, 17, 28, 22, 16 },
      // "java.lang.Integer"
      new byte[] {25, 21, 31, 12, 15, 76, 18, 11, 70, 15, 105, 79, 17, 17, 31, 28, 23},
  };

  private static byte[][] desedStrings = new byte[][]{
      // "org.cf.obfuscated.Reflection"
      new byte[] {-20, 119, 115, 2, -50, -24, 106, 33, 61, -83, 88, 75, -76, 0, 104, 29, 78, -121, -110, 121, 111, 12, -78, 39, -116, -107, 39, -63, 49, -11, -18, 127},
      // "secretMethod2"
      new byte[] {-118, 62, -84, 18, 113, -68, -117, 120, 51, -20, -3, 17, -24, 44, -80, -36},
  };

  private static final Map<Integer, String> strings = cacheStrings();



  private static Map<Integer, String> cacheStrings() {
    Map<Integer, String> cache = new HashMap<Integer, String>(
        base64edStrings.length + xoredStrings.length + desedStrings.length
    );

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

    try {
      for (byte[] encrypted : desedStrings) {
        String decoded = DESCrypt.decrypt(encrypted, desKey);
        cache.put(pos++, decoded);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

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
