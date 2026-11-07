package org.cf.obfuscated;

public class WhiteNoise {

  private static final int five = 5;
  private static final int ten = 10;

  public static void messyMethod() {
    Integer i;
    i = new Integer(12345);
    i = Integer.parseInt(new String(new char[] {'1', '0'}));
    i *= Integer.valueOf(3);
    i += Integer.valueOf(24);
    i /= Integer.valueOf(3);
    i -= Integer.valueOf(8);
    realTarget();
    i = Integer.parseInt(new String(new char[] {'3', '9'}));
    byte[] noise = new byte[i];
    i = Integer.parseInt(new String(new char[] {'2', '4'}));
    i *= Integer.valueOf(4);
    i += Integer.valueOf(12);
    i /= Integer.valueOf(2);
    i -= Integer.valueOf(6);
  }

  public static int convolutedMethod() {
    return stepA1() * stepB1();
  }

  private static void realTarget() {
    System.gc();
  }

  private static int stepA1() {
    return stepA2();
  }

  private static int stepA2() {
    return ten;
  }

  private static int stepB1() {
    return stepB2();
  }

  private static int stepB2() {
    return five;
  }

}
