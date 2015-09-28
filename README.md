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

1. Limit to just a few methods or classes `-it`.
2. If failure is because of maximum visits exceeded, try using higher `--max-address-visits`, `--max-call-depth`, and `--max-method-visits`.
3. Try with `-v` or `-vv` and report the issue.
4. Try again, but do not break eye contact. Simpify can sense fear.


Reporting Issues
----------------

1. If you can, link the **APK** or **DEX**.
2. The full command used.
3. *Optional*: verbose logs

Optimization Example
--------------------
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

###After Dead Code Removal
```smali
.method public static test1()I
    .locals 2

    const/4 v0, 0x1

    return v0
.end method
```

Because the code above `const/4 v0, 0x1` does not affect state outside of the method (no side-effects) it can be removed without changing behavior. If there was a method call that wrote something to the file system or network, it couldn't be removed because it affects state outside the method. Or if `test()I` took a mutable argument, such as a `LinkedList`, any instructions that accessed it couldn't be considered dead.

Some other examples of dead code:

* unreferenced assignments - assigning registers and not using them
* unreached / unreachable instructions - `if (false) { dead_code(); }`


Related Works
-------------

* [Guillot, Yoann, and Alexandre Gazet. "Automatic Binary Deobfuscation." Journal in Computer Virology 6.3 (2010): 261-76](http://metasm.cr0.org/docs/sstic09-metasm-jcv.pdf)
