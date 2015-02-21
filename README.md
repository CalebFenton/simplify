Simplify
========

[![Build Status](https://travis-ci.org/CalebFenton/simplify.svg?branch=master)](https://travis-ci.org/CalebFenton/simplify) [![Coverage Status](https://img.shields.io/coveralls/CalebFenton/simplify.svg)](https://coveralls.io/r/CalebFenton/simplify)

Generic Android Deobfuscator
----------------------------

Simplify uses a virtual machine to understand what an app does. Then, it applies optimizations to create code that behaves identically, but is easier for a human to understand. Specifically, it takes Smali files as input and outputs a Dex file with (hopefully) identical semantics but less complicated structure.

For example, if an app's strings are encrypted, Simplify will interpret the app in its own virtual machine to determine semantics. Then, it uses the apps own code to decrypt the strings and replaces the encrypted strings and the decryption method calls with the decrypted versions. It's a **generic** deobfuscator because Simplify doesn't need to know how the decryption works ahead of time. This technique also works well for eliminating different types of white noise, such as no-ops and useless arithmetic.

**Before / After**

<section>
<p align="center">
<img src="https://cloud.githubusercontent.com/assets/1356658/5331911/1e790c86-7df4-11e4-91e7-aba1d2c63b98.png" alt="Lots of method calls, no clear meaning" height="380px" align="center" />
<img src="https://cloud.githubusercontent.com/assets/1356658/5331912/1ecc6d7c-7df4-11e4-9572-bc3d41303842.png" alt="Wow, such literal, much meaning" height="380px" align="center" />
</p>
</section>

There are three parts to the project:

1. **Smali Virtual Machine (SmaliVM)** - A VM designed to handle ambiguous values and multiple possible execution paths. For example, if there is an `if`, and the predicate includes unknown values (user input, current time, read from a file, etc.), the VM will assume either one could happen, and takes both the `true` and `false` paths. This increases uncertainty, but ensures fidelity. SmaliVM's output is a graph that represents what the app could do. It contains every possible execution path and the register and class member values at each possible execution of every instruction.
2. **Simplify** - The optimizer. It takes the graphs from SmaliVM and applies optimizations like constant propagation, dead code removal, and  specific peephole optimizations.
3. **Demoapp** - A short and heavily commented project that shows how to get started using SmaliVM.


Building
--------

To build the jar, use `./gradlew shadowJar`


Troubleshooting
---------------

Simplify is still in early stages of development. If you encounter a failure, try these recommendations, in order:

1. Limit the target methods and classes to just the essentials with `-it`. Simplify will try and run over the entire app by default, which means more possibility for failure. Limiting to just one class or a few important methods improves chances significantly.
2. If methods fail to simplify because of errors related to maximum visits exceeded, try using higher `--max-address-visits`, `--max-call-depth`, and `--max-method-visits`.
3. Try with `-v` or `-vv` and report the issue.
4. Try again, but do not break eye contact. Simpify can sense fear.


Reporting Issues
----------------

Two main things are needed to reproduce and fix problems:

1. The APK. If it's legal and possible, please link to the APK.
2. The full command used.

Optional, but still useful, is a verbose log output around the error.


Reporting Success
-----------------

Did Simplify just save you a few hours of suffering? Send an e-mail to calebjfenton (AT) gmail [dot] com. If that's not your thing, send a pull request with bug fixes.



### Optimization Example
```
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

The above code is an obtuse way to say `v0 = 1`. This is sometimes used as an obfuscation technique.


###After Constant Propagation
```
.method public static test1()I
    .locals 2

    new-instance v0, Ljava/lang/Integer;

    const/4 v1, 0x1
    invoke-direct {v0, v1}, Ljava/lang/Integer;-><init>(I)V

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I
    # move-result replaced with const/4
    const/4 v0, 0x1

    # known return register value prepended with const
    const/4 v0, 0x1

    return v0
.end method
```

Some single assignment instructions can be replaced with constant instructions when there is consensus of the value being assigned of all the possible executions of that instruction. If the instruction is outside of a loop, there will only be one node in the graph for that instruction.
In the above example, `move-result` is constantized, so is `return`, because there is only one possible value (consensus) and it is not unknown.


###After Dead Code Removal
```
.method public static test1()I
    .locals 2

    const/4 v0, 0x1

    return v0
.end method
```

Dead code includes:

* unreferenced assignments - assigning something and never using it
* method calls with no side-effects - `Ljava/lang/Integer;->intValue()I` has no side-effects
* unreached / unreachable instructions - code inside of an `if (false)` block, none in this example

Related Works
-------------

* [Guillot, Yoann, and Alexandre Gazet. "Automatic Binary Deobfuscation." Journal in Computer Virology 6.3 (2010): 261-76](http://metasm.cr0.org/docs/sstic09-metasm-jcv.pdf)
