# Obfuscated App

This is an example obfuscated Android app which shows off some of the deobfuscation features of Simplify.

## Build

```bash
./gradlew build && cp app/build/outputs/apk/release/app-release-unsigned.apk ../obfuscated-app.apk
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
