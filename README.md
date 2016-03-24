Simplify
========

[![Build Status](https://travis-ci.org/CalebFenton/simplify.svg?branch=master)](https://travis-ci.org/CalebFenton/simplify) [![Coverage Status](https://img.shields.io/coveralls/CalebFenton/simplify.svg)](https://coveralls.io/r/CalebFenton/simplify) [![Coverity Scan Build Status](https://img.shields.io/coverity/scan/7022.svg)](https://scan.coverity.com/projects/calebfenton-simplify)

Generic Android Deobfuscator
----------------------------

Simplify uses a virtual machine to execute an app and understand what it does. Then, it applies optimizations to create code that behaves identically but is easier for a human to understand. It is a _generic_ deobfuscator because it doesn't need any special configuration or code for different types of obfuscation.

### Before and After

<section>
<p align="center">
<img src="https://cloud.githubusercontent.com/assets/1356658/5331911/1e790c86-7df4-11e4-91e7-aba1d2c63b98.png" alt="Lots of method calls, no clear meaning" height="380px" align="center" />
<img src="https://cloud.githubusercontent.com/assets/1356658/5331912/1ecc6d7c-7df4-11e4-9572-bc3d41303842.png" alt="Wow, such literal, much meaning" height="380px" align="center" />
</p>
</section>

There are three parts to the project:

1. **smalivm**: Creates a context sensitive control flow graph of a method by executing each instruction. The value of all classes and registers is recorded at every execution of every instruction. It doesn't need to know the arguments for a method to execute it as it handles unknown values. Also, it executes every possible path. For example, if an `if` could be `true` or `false` because it references an unknown value, it assumes both could happen and executes both paths.
2. **simplify**: Takes the graphs from **smalivm** and applies optimizations such as constant propagation, dead code removal, unreflection, and  specific peephole optimizations.
3. **demoapp**: Contains simple, heavily commented examples of how to use **smalivm**.


Building
--------

Because this project contains submodules, either clone with `--recursive`:

`git clone --recursive https://github.com/CalebFenton/simplify.git`

Or update submodules at any time with:

`git submodule update --init --recursive`

Then, to build a single jar:

`./gradlew fatjar`

The Simplify jar will be in `simplify/build/libs/simplify.jar`

You can test it's working with: `java -jar simplify/build/libs/simplify.jar -it 'org/cf' simplify/obfuscated-example`

Troubleshooting
---------------

Simplify is in early stages of development. If you encounter a failure, try these recommendations, in order:

1. Include only a few methods or classes with `-it`.
2. If failure is because of maximum visits exceeded, try using higher `--max-address-visits`, `--max-call-depth`, and `--max-method-visits`.
3. Try with `-v` or `-v 2` and report the issue with the logs.
4. Try again, but do not break eye contact. Simpify can sense fear.

Reporting Issues
----------------

1. If possible, include a link the APK or DEX. If you can't share the sample, please include either the SHA1 or MD5 checksum.
2. Include the full command used.
3. *Optional*: Include verbose logs.

Contributing
------------
Just submit a pull request. We can review and talk through it there.

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
* [Unicorn - The ultimate CPU emulator](http://www.unicorn-engine.org/)
* [Babak Yadegari, Saumya Debray. "Symbolic Execution of Obfuscated Code"](https://www.cs.arizona.edu/people/debray/Publications/ccs2015-symbolic.pdf)
