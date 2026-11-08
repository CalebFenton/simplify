# Simplify

Simplify is a generic Android deobfuscator. It virtually executes Dalvik code, then rewrites each method into
code that behaves identically and is easier for a human to read. Its optimizations are generic, so they work
regardless of which obfuscator produced the input.

<p align="center">
<img src="https://cloud.githubusercontent.com/assets/1356658/5331911/1e790c86-7df4-11e4-91e7-aba1d2c63b98.png" alt="Decompiled obfuscated code: many method calls, no clear meaning" height="380px" />
<img src="https://cloud.githubusercontent.com/assets/1356658/5331912/1ecc6d7c-7df4-11e4-9572-bc3d41303842.png" alt="The same code after Simplify: string literals in place of decryption calls" height="380px" />
</p>

## Quick Start

Building requires JDK 21 selected through `JAVA_HOME` and a case-sensitive filesystem. Clone with the
Android framework submodule, then build the standalone jar:

```bash
git clone --recursive https://github.com/CalebFenton/simplify.git
cd simplify
./gradlew :simplify:fatJar
```

In an existing clone, fetch the submodule with `git submodule update --init --recursive`.

Simplify the included [obfuscated example app](simplify/ObfuscatedApp/README.md):

```bash
java -jar simplify/build/libs/simplify.jar -it "org/cf/obfuscated" -et "MainActivity" simplify/obfuscated-app.apk
```

The run takes several minutes, ends with a summary of optimized, failed, and skipped methods, and writes
`obfuscated-app_simple.apk` to the current directory; `-o` chooses another path. Run
`java -jar simplify/build/libs/simplify.jar --help` for every option.

## What This Does

The project has four Gradle modules. **smalivm** is a sandboxed virtual machine for Dalvik methods. It returns
an execution graph of every possible register and class value on every path, even when some inputs are
unknown: a branch on an unknown value takes both arms. **simplify** reads those graphs and applies constant
propagation, dead code removal, unreflection, and peephole optimizations, repeating them until the method
stops changing or reaches `--max-passes`. It does not rename classes or methods. **demoapp** shows how to embed smalivm, and **sdbg** is
an interactive debugger for the same execution engine.

An optimization runs only when the analysis of a method is complete. A method whose analysis hits a limit is
reported as skipped and left unchanged. Exceptions, synchronization, volatile accesses, and writes to objects
other code can see are observable, so their operations stay. For example, save this as `example/Example.smali`:

```smali
.class public LExample;
.super Ljava/lang/Object;

.method public static add()I
    .locals 2

    const/4 v0, 0x4
    const/4 v1, 0x3
    add-int v0, v0, v1
    return v0
.end method

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

Running `java -jar simplify/build/libs/simplify.jar example -o example.dex` and disassembling `example.dex`
gives these method bodies:

```smali
.method public static add()I
    .registers 2

    const/4 v0, 0x7

    return v0
.end method

.method public static test1()I
    .registers 2

    const/4 v0, 0x1

    return v0
.end method
```

Constant propagation folds the addition and the result of `intValue()`. Dead code removal then deletes the unused
inputs of `add-int` and the `Integer` that nothing observes any more: its allocation, constructor and call change
nothing outside the object.

Simplify reads a Smali directory, a DEX file, or an APK. APK output replaces `classes.dex`, keeps every other
entry unchanged, and must be signed again before installation. An archive that also contains `classes2.dex`
is rejected with `Multi-DEX archives are not supported`.

## Command Line

`-v` raises log verbosity each time it repeats (`-v`, `-vv`). The exit status is 1 when the input is rejected,
a method fails to execute, or an optimizer edit is rejected; `--ignore-errors` skips failed methods and exits 0.
Skipped methods do not change the exit status.

If a run fails, limit it to a few classes or methods with `-it`. If a method exceeds a visit limit, raise
`--max-address-visits`, `--max-call-depth`, or `--max-method-visits`. To report a problem, run again with `-v`
or `-vv` and open an issue with the log, the full command, and the APK or DEX or its SHA-256 hash.

## Demo and Debugger

```bash
./gradlew :demoapp:fatJar :sdbg:fatJar
java -jar demoapp/build/libs/demoapp.jar demoapp/resources
java -jar sdbg/build/libs/sdbg.jar sdbg/resources 'LTest;->addNumbers()V'
```

The demo executes the Smali in `demoapp/resources` with known and unknown inputs and prints each result. See
[SDBG](sdbg/README.md) for a debugger walkthrough.

## Documentation

- [Architecture](docs/architecture/overview.md): execution engine, state, exceptions, and optimizer contracts
- [Build and distribution](docs/architecture/build-and-distribution.md): toolchain, artifacts, embedding, and CI
- [Dalvik semantics](docs/DALVIK-SEMANTICS.md): runtime evidence behind the VM's behavior
- [Dalvik Virtual Execution with SmaliVM](http://calebfenton.github.io/2016/04/30/dalvik-virtual-execution-with-smalivm/)

## Contributing

Questions, issues, and pull requests are welcome. When reporting a bug, include the APK or DEX (or its hash)
and the full command.

## License

Simplify's project-owned code is licensed under the [Apache License, Version 2.0](LICENSE). Bundled
dependencies, third-party source, and the Android framework corpus keep their own licenses; see
[THIRD_PARTY_NOTICES](THIRD_PARTY_NOTICES) and [licenses](licenses/).
