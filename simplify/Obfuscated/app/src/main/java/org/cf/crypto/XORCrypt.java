package org.cf.crypto;

public class XORCrypt {

    public static String encode(String data, String key) {
      byte[] dataBytes = data.getBytes();
      byte[] keyBytes  = key.getBytes();

      int keyPointer = 0;
      for (int i = 0; i < dataBytes.length; i++) {
        dataBytes[i] ^= keyBytes[keyPointer];
        keyPointer += dataBytes[i];
        keyPointer %= keyBytes.length;
      }

      return new String(dataBytes);
    }

    public static String decode(String data, String key) {
      byte[] dataBytes = data.getBytes();
      byte[] keyBytes  = key.getBytes();

      int keyPointer = 0;
      byte keyPointerAdd = 0;
      for (int i = 0; i < dataBytes.length; i++) {
        keyPointerAdd = dataBytes[i];
        dataBytes[i] ^= keyBytes[keyPointer];
        keyPointer += keyPointerAdd;
        keyPointer %= keyBytes.length;
      }

      return new String(dataBytes);
    }

  public static void main(String[] argv) {
//    String key = StringHolder.xorKey;
//    System.out.println("key = " + key);
//    String encoded = encode("java.lang.Integer", key);
//    System.out.println(Arrays.toString(encoded.getBytes()));
//    String decoded = decode(encoded, key);
//    System.out.println(decoded);
  }

}
