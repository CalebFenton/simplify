Simplifier
==========

Semantics simplification and deobfuscation
------------------------------------------

The goal for this project is to analyze a given set of Smali files and modify instructions to be semantically equivalent but simpler. The two main techniques are constant propagation and no-op removal.

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

###After No-op Removal
```
.method public static test1()I
    .locals 2

    const/4 v0, 0x1

    return v0
.end method
```

It should be possible to have configurable options for relaxed assumtions about ambiguity, e.g. assume values of static variables haven't changed since class initialization, or just for specific classes.

It is still in early development, so don't expect everything (or anything) to work. :D