# Simplify

[![Build Status](https://travis-ci.org/CalebFenton/simplify.svg?branch=master)](https://travis-ci.org/CalebFenton/simplify) [![Coverage Status](https://img.shields.io/coveralls/CalebFenton/simplify.svg)](https://coveralls.io/r/CalebFenton/simplify) [![Coverity Scan Build Status](https://img.shields.io/coverity/scan/7022.svg)](https://scan.coverity.com/projects/calebfenton-simplify)

## Generic Android Deobfuscator

Simplify virtually executes an app to understand its behavior and then tries to optimize the code so that it behaves identically but is easier for a human to understand. Each optimization type is simple and generic, so it doesn't matter what the specific type of obfuscation is used.

### Before and After

The code on the left is a decompilation of an obfuscated app, and the code on the right has been deobfuscated.
<section>
<p align="center">
<img src="https://cloud.githubusercontent.com/assets/1356658/5331911/1e790c86-7df4-11e4-91e7-aba1d2c63b98.png" alt="Lots of method calls, no clear meaning" height="380px" align="center" />
<img src="https://cloud.githubusercontent.com/assets/1356658/5331912/1ecc6d7c-7df4-11e4-9572-bc3d41303842.png" alt="Wow, such literal, much meaning" height="380px" align="center" />
</p>
</section>

## Overview

There are three parts to the project: smalivm, simplify, and the demo app.

1. **smalivm**: Provides a virtual machine sandbox for executing Dalvik methods. After executing a method, it returns a graph containing all possible register and class values for every execution path. It works even if some values are unknown, such as file and network I/O. For example, any `if` or `switch` conditional with an unknown value results in both branches being taken.
2. **simplify**: Analyzes the execution graphs from **smalivm** and applies optimizations such as constant propagation, dead code removal, unreflection, and some peephole optimizations. These are fairly simple, but when applied together repeatedly, they'll decrypt strings, remove reflection, and greatly simplify code. It does *not* rename methods and classes.
3. **demoapp**: Contains simple, heavily commented examples for using **smalivm** in your own project. If you're building something that needs to execute Dalvik code, check it out.

## Usage

```
usage: java -jar simplify.jar <input> [options]
deobfuscates a dalvik executable
 -et,--exclude-types <pattern>   Exclude classes and methods which include REGEX, eg: "com/android", applied after include-types
 -h,--help                       Display this message
 -ie,--ignore-errors             Ignore errors while executing and optimizing methods. This may lead to unexpected behavior.
    --include-support            Attempt to execute and optimize classes in Android support library packages, default: false
 -it,--include-types <pattern>   Limit execution to classes and methods which include REGEX, eg: ";->targetMethod\("
    --max-address-visits <N>     Give up executing a method after visiting the same address N times, limits loops, default: 10000
    --max-call-depth <N>         Do not call methods after reaching a call depth of N, limits recursion and long method chains, default: 50
    --max-execution-time <N>     Give up executing a method after N seconds, default: 300
    --max-method-visits <N>      Give up executing a method after executing N instructions in that method, default: 1000000
    --max-passes <N>             Do not run optimizers on a method more than N times, default: 100
 -o,--output <file>              Output simplified input to FILE
    --output-api-level <LEVEL>   Set output DEX API compatibility to LEVEL, default: 15
 -q,--quiet                      Be quiet
    --remove-weak                Remove code even if there are weak side effects, default: true
 -v,--verbose <LEVEL>            Set verbosity to LEVEL, default: 0
```

## Building

Building requires the [Java Development Kit 8 (JDK)](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) to be installed.

Because this project contains submodules for Android frameworks, either clone with `--recursive`:

```bash
git clone --recursive https://github.com/CalebFenton/simplify.git
```

Or update submodules at any time with:

```bash
git submodule update --init --recursive
```

Then, to build a single jar which contains all dependencies:

```bash
./gradlew fatjar
```

The Simplify jar will be in `simplify/build/libs/`. You can test it's working by simplifying the provided [obfuscated example app](/simplify/ObfuscatedApp). Here's how you'd run it (you may need to change `simplify.jar`):

```bash
java -jar simplify/build/libs/simplify.jar -it "org/cf/obfuscated" -et "MainActivity" simplify/obfuscated-app.apk
```

To understand what's getting deobfuscated, check out [Obfuscated App's README](/simplify/ObfuscatedApp/README.md).

## Troubleshooting

If Simplify fails, try these recommendations, in order:

1. Only target a few methods or classes by using `-it` option.
2. If failure is because of maximum visits exceeded, try using higher `--max-address-visits`, `--max-call-depth`, and `--max-method-visits`.
3. Try with `-v` or `-v 2` and report the issue with the logs and a hash of the DEX or APK.
4. Try again, but do not break eye contact. Simplify can sense fear.

If building on Windows, and building fails with an error similar to:

> Could not find tools.jar. Please check that C:\Program Files\Java\jre1.8.0_151 contains a valid JDK installation.

This means Gradle is unable to find a proper JDK path. Make sure the JDK is installed, set the `JAVA_HOME` environment variable to your JDK path, and make sure to close and re-open the command prompt you use to build.

## Contributing

Don't be shy. I think virtual execution and deobfuscation are fascinating problems. Anyone who's interested is automatically cool and contributions are welcome, even if it's just to fix a typo. Feel free to ask questions in the issues and submit pull requests.

### Reporting Issues

Please include a link to the APK or DEX and the full command you're using. This makes it much easier to reproduce (and thus _fix_) your issue.

If you can't share the sample, *please* include the file hash (SHA1, SHA256, etc).

## Optimization Strategies

### Constant Propagation

If an op places a value of a type which can be turned into a constant such as a string, number, or boolean, this optimization will replace that op with the constant. For example:

```smali
const-string v0, "VGVsbCBtZSBvZiB5b3VyIGhvbWV3b3JsZCwgVXN1bC4="
invoke-static {v0}, Lmy/string/Decryptor;->decrypt(Ljava/lang/String;)Ljava/lang/String;
# Decrypts to: "Tell me of your homeworld, Usul."
move-result v0
```

In this example, an encrypted string is decrypted and placed into `v0`. Since strings are "constantizable", the `move-result v0` can be replaced with a `const-string`:

```smali
const-string v0, "VGVsbCBtZSBvZiB5b3VyIGhvbWV3b3JsZCwgVXN1bC4="
invoke-static {v0}, Lmy/string/Decryptor;->decrypt(Ljava/lang/String;)Ljava/lang/String;
const-string v0, "Tell me of your homeworld, Usul."
```

### Dead Code Removal

Code is dead if removing it cannot possibly alter the behavior of the app. The most obvious case is if the code is unreachable, e.g. `if (false) { // dead }`). If code is reachable, it may be considered dead if it doesn't affect any state outside of the method, i.e. it has no _side effect_. For example, code may not affect the return value for the method, alter any class variables, or perform any IO. This is a difficult to determine in static analysis. Luckily, smalivm doesn't have to be clever. It just stupidly executes everything it can and assumes there are side effects if it can't be sure. Consider the example from Constant Propagation:

```smali
const-string v0, "VGVsbCBtZSBvZiB5b3VyIGhvbWV3b3JsZCwgVXN1bC4="
invoke-static {v0}, Lmy/string/Decryptor;->decrypt(Ljava/lang/String;)Ljava/lang/String;
const-string v0, "Tell me of your homeworld, Usul."
```

In this code, the `invoke-static` no longer affects the return value of the method and let's assume it doesn't do anything weird like write bytes to the file system or a network socket so it has no side effects. It can simply be removed.

```smali
const-string v0, "VGVsbCBtZSBvZiB5b3VyIGhvbWV3b3JsZCwgVXN1bC4="
const-string v0, "Tell me of your homeworld, Usul."
```

Finally, the first `const-string` assigns a value to a register, but that value is never used, i.e. the assignment is dead. It can also be removed.

```smali
const-string v0, "Tell me of your homeworld, Usul."
```

Huzzah!

### Unreflection

One major challenge with static analysis of Java is reflection. It's just not possible to know the arguments are for reflection methods without doing careful data flow analysis. There are smart, clever ways of doing this, but smalivm does it by just executing the code. When it finds a reflected method invocation such as:

```smali
invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
```

It can know the values of `v0`, `v1`, and `v2`. If it's sure what the values are, it can replace the call to `Method.invoke()` with an actual non-reflected method invocation. The same applies for reflected field and class lookups.

### Peephole

For everything that doesn't fit cleanly into a particular category, there's peephole optimizations. This includes removing useless `check-cast` ops, replacing `Ljava/lang/String;-><init>` calls with `const-string`, and so on.

## Deobfuscation Example

### Before Optimization

```smali
.method public static test1()I
    .locals 2

    new-instance v0, Ljava/lang/Integer;
    const/4 v1, 0x1
    invoke-direct {v0, v1}, Ljava/lang/Integer;-><init>(I)V

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I
    move-result v0

    return v0
.end method
```

All this does is `v0 = 1`.

### After Constant Propagation

```smali
.method public static test1()I
    .locals 2

    new-instance v0, Ljava/lang/Integer;
    const/4 v1, 0x1
    invoke-direct {v0, v1}, Ljava/lang/Integer;-><init>(I)V

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I
    const/4 v0, 0x1

    return v0
.end method
```

The `move-result v0` is replaced with `const/4 v0, 0x1`. This is because there is only one possible return value for `intValue()I` and the return type can be made a constant. The arguments `v0` and `v1` are unambiguous and do not change. That is to say, there's a consensus of values for every possible execution path at `intValue()I`. Other types of values that can be turned into constants:

* numbers - `const/4`, `const/16`, etc.
* strings - `const-string`
* classes - `const-class`

### After Dead Code Removal

```smali
.method public static test1()I
    .locals 2

    const/4 v0, 0x1

    return v0
.end method
```

Because the code above `const/4 v0, 0x1` does not affect state outside of the method (no side-effects), it can be removed without changing behavior. If there was a method call that wrote something to the file system or network, it couldn't be removed because it affects state outside the method. Or if `test()I` took a mutable argument, such as a `LinkedList`, any instructions that accessed it couldn't be considered dead.

Other examples of dead code:

* unreferenced assignments - assigning registers and not using them
* unreached / unreachable instructions - `if (false) { dead_code(); }`

# License

This tool is available under a dual license: a commercial one suitable for closed source projects and a GPL license that can be used in open source software.

Depending on your needs, you must choose one of them and follow its policies. A detail of the policies and agreements for each license type are available in the [LICENSE.COMMERCIAL](LICENSE.COMMERCIAL) and [LICENSE.GPL](LICENSE.GPL) files.

# Further Reading

* [Dalvik Virtual Execution with SmaliVM](http://calebfenton.github.io/2016/04/30/dalvik-virtual-execution-with-smalivm/)
* [Guillot, Yoann, and Alexandre Gazet. "Automatic Binary Deobfuscation." Journal in Computer Virology 6.3 (2010): 261-76](http://metasm.cr0.org/docs/sstic09-metasm-jcv.pdf)
* [Unicorn - The ultimate CPU emulator](http://www.unicorn-engine.org/)
* [Babak Yadegari, Saumya Debray. "Symbolic Execution of Obfuscated Code"](https://www.cs.arizona.edu/people/debray/Publications/ccs2015-symbolic.pdf)
* Success stories:
    * [Android Dynamic Class Loading with "AES/CFB/NoPadding" encryption. Take a peek before & after. Tool used: #simplify. #Android #obfuscation #classencryption #dex](https://twitter.com/enovella_/status/946899042973437952)
    * [Decrypting Malware String Encryption](https://twitter.com/caleb_fenton/status/850400605340262400)

