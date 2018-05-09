# Obfuscated App

This is an example obfuscated Android app which shows off some of the deobfuscation features of Simplify.

## Build
```bash
./gradlew build && cp app/build/outputs/apk/app-release-unsigned.apk ../obfuscated-example.apk
```

## Reflection

Here's some code from the `reflectSecretMethod` from `org.cf.obfuscated.Reflection`:

```java
Class<?> klazz = Class.forName(StringHolder.get(9));
Method m = klazz.getDeclaredMethod(
  StringHolder.get(7),
  Class.forName(StringHolder.get(8)),
  Class.forName(StringHolder.get(8)),
  Class.forName(StringHolder.get(8))
);
Integer magic = (Integer) m.invoke(null, 36, 30, 64922);
System.out.println("magic answer = " + magic);
```

The `StringHolder` class contains some encrypted string literals which are decrypted at runtime. Here's what it looks like after simplification:

```java
System.out.println("magic answer = 42");
```

Booyah. All the encryption and reflection are removed.

## White Noise

Here's the `org.cf.obfuscated.WhiteNoise` class code:

```java
public class WhiteNoise
{
  private static final int five = 5;
  private static final int ten = 10;
  
  public static int convolutedMethod() {
    return stepA1() * stepB1();
  }
  
  public static void messyMethod() {
    new Integer(12345);
    Integer.valueOf(Integer.valueOf(Integer.valueOf(Integer.valueOf(Integer.parseInt(new String(new char[] { 49, 48 }))).intValue() * Integer.valueOf(3).intValue()).intValue() + Integer.valueOf(24).intValue()).intValue() / Integer.valueOf(3).intValue()).intValue();
    Integer.valueOf(8).intValue();
    realTarget();
    byte[] arrayOfByte = new byte[Integer.valueOf(Integer.parseInt(new String(new char[] { 51, 57 }))).intValue()];
    Integer.valueOf(Integer.valueOf(Integer.valueOf(Integer.valueOf(Integer.parseInt(new String(new char[] { 50, 52 }))).intValue() * Integer.valueOf(4).intValue()).intValue() + Integer.valueOf(12).intValue()).intValue() / Integer.valueOf(2).intValue()).intValue();
    Integer.valueOf(6).intValue();
  }
  
  private static void realTarget() {}
  
  private static int stepA1() {
    return stepA2();
  }
  
  private static int stepA2() {
    return 10;
  }
  
  private static int stepB1() {
    return stepB2();
  }
  
  private static int stepB2() {
    return 5;
  }
}
```

Looks pretty messy, but not that technically complicated. Here's how it looks after simplificaton:

```java
public class WhiteNoise
{
  private static final int five = 5;
  private static final int ten = 10;
  
  public static int convolutedMethod() {
    return 50;
  }
  
  public static void messyMethod() {}
  
  private static void realTarget() {}
  
  private static int stepA1() {
    return 10;
  }
  
  private static int stepA2() {
    return 10;
  }
  
  private static int stepB1() {
    return 5;
  }
  
  private static int stepB2() {
    return 5;
  }
}
```
