package org.cf.obfuscated;


import java.lang.reflect.Method;

public class Reflection {

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

  public static void main(String[] argv) {
//    System.out.println("7 = " + StringHolder.get(7));
//    System.out.println("8 = " + StringHolder.get(8));
//    System.out.println("9 = " + StringHolder.get(9));
//    reflectSecretMethod();
  }

}
