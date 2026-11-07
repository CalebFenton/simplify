# Simplify

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

The project has four modules:

1. **smalivm**: Provides a virtual machine sandbox for executing Dalvik methods. After executing a method, it returns a graph containing all possible register and class values for every execution path. It works even if some values are unknown, such as file and network I/O. For example, any `if` or `switch` conditional with an unknown value results in both branches being taken.
2. **simplify**: Analyzes the execution graphs from **smalivm** and applies optimizations such as constant propagation, dead code removal, unreflection, and some peephole optimizations. These are fairly simple, but when applied together repeatedly, they'll decrypt strings, remove reflection, and greatly simplify code. It does *not* rename methods and classes.
3. **demoapp**: Contains simple, heavily commented examples for using **smalivm** in your own project. If you're building something that needs to execute Dalvik code, check it out.
4. **sdbg**: An interactive debugger for inspecting VM execution, breakpoints, and register values.

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
    --output-api-level <LEVEL>   Set output DEX API compatibility to LEVEL, default: 20
 -q,--quiet                      Be quiet
 -v,--verbose <LEVEL>            Set verbosity to LEVEL, default: 0
```

## Building

Building uses a JDK 21 toolchain; running the applications requires JDK 21 or later. Set `JAVA_HOME`
to JDK 21 for the build. The wrapper pins Gradle 9.7.0 with a verified distribution checksum, and
the Kotlin compiler is 2.4.21.

The next public release is **2.0.0-SNAPSHOT**. The Kotlin execution engine replaces the old VM API.
Embedding applications must migrate to `VmServices`, immutable method snapshots, and execution sessions.
This public release is distinct from the separate commercial product.

The DEX toolchain now uses Google's maintained Smali 3.0.10 fork. Public DEX types use
`com.android.tools.smali` rather than `org.jf`; embedding callers must update their imports.
See [build and distribution details](docs/architecture/build-and-distribution.md) for dependency,
logging, artifact, and separate historical Android-fixture boundaries.

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
./gradlew :simplify:fatJar
```

The standalone jar is `simplify/build/libs/simplify.jar`. To simplify the provided [obfuscated example app](/simplify/ObfuscatedApp):

```bash
java -jar simplify/build/libs/simplify.jar -it "org/cf/obfuscated" -et "MainActivity" simplify/obfuscated-app.apk
```

Smali directories, DEX files, and APKs are supported. APK output replaces `classes.dex`.
Other entries retain their bytes, compression methods, timestamps, and comments; the archive comment
also survives. Modified APKs must be signed again before installation.

To understand what's getting deobfuscated, check out [Obfuscated App's README](/simplify/ObfuscatedApp/README.md).

### Demo and debugger

```bash
./gradlew :demoapp:fatJar :sdbg:fatJar
java -jar demoapp/build/libs/demoapp.jar demoapp/resources
java -jar sdbg/build/libs/sdbg.jar sdbg/resources 'LTest;->addNumbers()V'
```

The Kotlin demo shows VM-local emulator registration and execution with known and unknown inputs.
Its explicit reflection policy permits only the host operations needed by the examples.
The Java files under `org.cf.demosmali` remain guest fixture sources, not the host application.

SDBG supports breakpoints, step/next/continue, register and static-field inspection, and disassembly
through the Kotlin debugger. See [SDBG usage](sdbg/README.md). Exit the console with Ctrl-D.

## Troubleshooting

If Simplify fails, try these recommendations, in order:

1. Only target a few methods or classes by using `-it` option.
2. If failure is because of maximum visits exceeded, try using higher `--max-address-visits`, `--max-call-depth`, and `--max-method-visits`.
3. Try with `-v` or `-v 2` and report the issue with the logs and a hash of the DEX or APK.
4. Try again, but do not break eye contact. Simplify can sense fear.

If Gradle selects the wrong Java installation, set `JAVA_HOME` to JDK 21 and restart your shell.
The packaged jar supplies the module opening required by host reflection. When embedding SmaliVM
directly, run Java with `--add-opens=java.base/java.lang=ALL-UNNAMED`.

## Contributing

Don't be shy. I think virtual execution and deobfuscation are fascinating problems. Anyone who's interested is automatically cool and contributions are welcome, even if it's just to fix a typo. Feel free to ask questions in the issues and submit pull requests.

### Reporting Issues

Please include a link to the APK or DEX and the full command you're using. This makes it much easier to reproduce (and thus _fix_) your issue.

If you can't share the sample, *please* include the file hash (SHA1, SHA256, etc).

## Optimization Strategies

### Constant Propagation

Constant propagation replaces proven numeric and boolean results with constants. For example:

```smali
const/4 v0, 0x4
const/4 v1, 0x3
add-int v0, v0, v1
return v0
```

The addition can become `const/4 v0, 0x7`. Dead code removal can then remove its unused inputs.
String propagation is deliberately narrower: it requires a straight-line definition originating
from `const-string`, possibly through object moves. Equal string contents do not prove equal
object identity. Arbitrary decrypted or constructed strings are not replaced with interned literals.

### Dead Code Removal

Dead code removal removes unreachable instructions and unused assignments only when complete
analysis proves the removal safe. After constant propagation, the example above reduces to:

```smali
const/4 v0, 0x7
return v0
```

Exceptions, synchronization, volatile accesses, and array writes are observable behavior.
Unused results do not make those operations removable. There is no `--remove-weak` bypass.
Partial analysis never authorizes a rewrite.

### Unreflection

One major challenge with static analysis of Java is reflection. It's just not possible to know the arguments are for reflection methods without doing careful data flow analysis. There are smart, clever ways of doing this, but smalivm does it by just executing the code. When it finds a reflected method invocation such as:

```smali
invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
```

A direct call requires more than known arguments: all executions must continue normally, with
no side effects, and the target, access, receiver, and argument shape must be proven.
The default opaque `Method.invoke` policy does not supply that proof, so those calls stay reflective.
Field lowering is limited to static fields declared by the current class.

### Peephole

Peephole optimizations simplify proven constant branches, remove exact redundant casts, and replace
eligible same-class `Class.forName` calls. String constructors are not replaced with `const-string`,
because that can change object identity.

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

Simplify's project-owned code is licensed under the [Apache License, Version 2.0](LICENSE).
There is no separate commercial-license requirement for that code.

Bundled dependencies and third-party source retain their own licenses and attribution requirements.
See [THIRD_PARTY_NOTICES](THIRD_PARTY_NOTICES) and the component texts in [licenses](licenses/).
The existing Android framework corpus has unresolved redistribution provenance; see the
[publication requirements](docs/architecture/build-and-distribution.md#ci-and-release-boundary)
before distributing release artifacts.

# Further Reading

* [Dalvik Virtual Execution with SmaliVM](http://calebfenton.github.io/2016/04/30/dalvik-virtual-execution-with-smalivm/)
* [Guillot, Yoann, and Alexandre Gazet. "Automatic Binary Deobfuscation." Journal in Computer Virology 6.3 (2010): 261-76](http://metasm.cr0.org/docs/sstic09-metasm-jcv.pdf)
* [Unicorn - The ultimate CPU emulator](http://www.unicorn-engine.org/)
* [Babak Yadegari, Saumya Debray. "Symbolic Execution of Obfuscated Code"](https://www.cs.arizona.edu/people/debray/Publications/ccs2015-symbolic.pdf)
* Success stories:
    * [Android Dynamic Class Loading with "AES/CFB/NoPadding" encryption. Take a peek before & after. Tool used: #simplify. #Android #obfuscation #classencryption #dex](https://twitter.com/enovella_/status/946899042973437952)
    * [Decrypting Malware String Encryption](https://twitter.com/caleb_fenton/status/850400605340262400)

