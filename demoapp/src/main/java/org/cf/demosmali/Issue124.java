package org.cf.demosmali;

public class Issue124 {

  public static String testyTest() {
    String msg = decode(
        new byte[]{(byte) -43, (byte) -34, (byte) -4, (byte) -36, (byte) -50, (byte) -81, (byte) -41, (byte) -23, (byte) -15, (byte) -36, (byte) -23,
            (byte) -88}, decode(new byte[]{(byte) 4, (byte) 83, (byte) 81, (byte) 0, (byte) 0, (byte) 81}, "5209cf"));
    return msg;
  }

  public static byte[] encode(byte[] bytes, String key) {
    int len = bytes.length;
    int keyLen = key.length();
    for (int i = 0; i < len; i++) {
      bytes[i] = (byte) (bytes[i] ^ key.charAt(i % keyLen));
    }
    return bytes;
  }

  public static String decode(byte[] bytes, String key) {
    int len = bytes.length;
    int keyLen = key.length();
    for (int i = 0; i < len; i++) {
      bytes[i] = (byte) (bytes[i] ^ key.charAt(i % keyLen));
    }
    return new String(bytes);
  }
}
