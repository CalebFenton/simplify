package org.cf.obfuscated;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflection {

  private static String someField = "this is some field, eh?";

  private static void reflectSecretMethod() {
    try {
      Class<?> klazz = Class.forName(StringHolder.get(9));
      Method m = klazz.getDeclaredMethod(
          StringHolder.get(7)
          , Class.forName(StringHolder.get(8))
          , Class.forName(StringHolder.get(8))
          , Class.forName(StringHolder.get(8))
      );
      // Expected: 42
      Integer magic = (Integer) m.invoke(null, 36, 30, 64922);
      System.out.println("magic answer = " + magic);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void reflectSecretMethod2() {
    try {
      Class<?> klazz = Class.forName(StringHolder.get(9));
      Method m = klazz.getDeclaredMethod(StringHolder.get(10));
      m.invoke(null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static int secretMethod(Integer x, Integer y, Integer z) {
    return MathCrypt.decode(x, y, z);
  }

  private static void secretMethod2() {
    System.gc();
  }

  private static void reflectedFieldLookup() throws Exception {
    String fieldName = new String(new byte[] {115,111,109,101,70,105,101,108,100}); // "someField"
    Field f = Reflection.class.getDeclaredField(fieldName);
    String xorKey = (String) f.get(null);
    System.out.println(xorKey);
  }

  public static void main(String[] argv) throws Exception {
//    System.out.println("7 = " + StringHolder.get(7));
//    System.out.println("8 = " + StringHolder.get(8));
//    System.out.println("9 = " + StringHolder.get(9));
//    reflectSecretMethod();
//    reflectedFieldLookup();
  }

}
