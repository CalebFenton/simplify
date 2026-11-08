# Obfuscated App

An example Android app obfuscated by hand to show what Simplify removes. The built app is checked in as
`simplify/obfuscated-app.apk`.

## Build

```bash
./gradlew build && cp app/build/outputs/apk/release/app-release-unsigned.apk ../obfuscated-app.apk
```

## Simplify it

From the repository root, after `./gradlew :simplify:fatJar`:

```bash
java -jar simplify/build/libs/simplify.jar -it "org/cf/obfuscated" -et "MainActivity" simplify/obfuscated-app.apk
```

The output is `obfuscated-app_simple.apk`. `ObfuscatedApplicationTest.exampleAppSimplifiesToItsReadmeOutput`
checks the two results below.

## Reflection

`org.cf.obfuscated.Reflection.reflectSecretMethod()` looks up a method by name and calls it through reflection:

```java
try {
  Class<?> klazz = Class.forName(StringHolder.get(9));
  Method m = klazz.getDeclaredMethod(
    StringHolder.get(7),
    Class.forName(StringHolder.get(8)),
    Class.forName(StringHolder.get(8)),
    Class.forName(StringHolder.get(8))
  );
  Integer magic = (Integer) m.invoke(null, 36, 30, 64922);
  System.out.println("magic answer = " + magic);
} catch (Exception e) {
  e.printStackTrace();
}
```

`StringHolder` decrypts its strings (Base64 and XOR) into a map when its class initializes. `secretMethod`
returns `MathCrypt.decode(36, 30, 64922)`, which indexes a table of primes that `MathCrypt` computes when it
initializes. After simplification:

```java
try {
  System.out.println("magic answer = 42");
} catch (Exception e) {
  e.printStackTrace();
}
```

The decrypted strings became literals, the class lookups became class constants, the reflective call became a
direct call and then its result, and the string concatenation became its result.

`reflectSecretMethod2()` stays reflective: its target calls `System.gc()`, and Simplify lowers a reflective call
only when the target has no effect, because a direct call reports a target's exception differently.

## White Noise

`org.cf.obfuscated.WhiteNoise`:

```java
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

  private static int stepA1() { return stepA2(); }
  private static int stepA2() { return ten; }
  private static int stepB1() { return stepB2(); }
  private static int stepB2() { return five; }
}
```

After simplification, only the call with an effect is left in `messyMethod`, and every `step` method returns its
constant:

```java
public static void messyMethod() {
  realTarget();
}

public static int convolutedMethod() {
  return 50;
}

private static void realTarget() {
  System.gc();
}

private static int stepA1() { return 10; }
private static int stepA2() { return 10; }
private static int stepB1() { return 5; }
private static int stepB2() { return 5; }
```
