Simplifier
==========

Semantics simplification and deobfuscation
------------------------------------------

The goal for this project is to analyze a given set of Smali files and modify instructions to be semantically equivalent but simpler. The two main techniques are constant propagation and dead code removal.

###Trivial Example
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

###After Constant Propagation
```
.method public static test1()I
    .locals 2

    new-instance v0, Ljava/lang/Integer;

    const/4 v1, 0x1
    invoke-direct {v0, v1}, Ljava/lang/Integer;-><init>(I)V

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I
    const/4 v0, 0x1 # move replaced with const

    # known return register value prepended with const
    const/4 v0, 0x1

    return v0
.end method
```

###After Dead Code Removal
```
.method public static test1()I
    .locals 2

    const/4 v0, 0x1

    return v0
.end method
```

The eventual goal is to have configurable options for relaxed assumptions of ambiguity. For example, tell Simplifier to assume static variables of some class won't be modified.

It is still in early development, so don't expect everything (or anything) to work. :D