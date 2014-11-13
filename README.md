Simplifier
==========

Semantics simplification and deobfuscation
------------------------------------------

Simplifier takes Smali files as input and produces a simpler and (hopefully) semantically equivalent Dalvik executable. The main use case for this is as a generic deobfuscator. There are two main parts to the project:

1. Smali Virtual Machine (smalivm) - A VM designed to handle ambiguous values and multiple possible execution paths. If a conditional has an ambiguous predicate, both positive and negative branches are followed. Ambiguity is assumed pessimistically. E.g. If a method is invoked, but cannot be executed, any mutable parameters are assumed mutated and marked as ambiguous. Heap state (method and class values) is maintained for each execution of each instruction. The result of the execution of a method is a context sensitive graph.

2. Simplify - An optimizer that understands how to examine the graph produced by smalivm. It performs constant propagation, dead code removal, and some specific peephole optimizations.

It is still in early development, so don't expect everything (or anything) to work. :D

###Trivial Optimization Example
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

The above code is an obtuse way to say `v0 = 1`. This is an obfuscation technique. SmaliVM understands how to use the Java API when it's safe, and will reflect the Integer methods.


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

Certain single assignment instructions can be replaced with constant instructions when there is consensus of the potential register value between all nodes for that particular instruction. In this example `move-result` is constantized, as well as `return` because there is only one possible value and it is not ambiguous.


###After Dead Code Removal
```
.method public static test1()I
    .locals 2

    const/4 v0, 0x1

    return v0
.end method
```

Dead code includes:

* unreferenced assignments
* method calls with no side-effects
* unreached / unreachable instructions

This example shows the first two types being removed.

###TODO
* Exception handling - Exceptions are dropped on the floor and catch blocks are never reached. This makes certain optimizations difficult or impossible. It's easy to answer the question "did this instruction throw an exception", but it gets tricky when dealing with ambiguous values. Lots of tedious work will need to be done for each opcode to consider possible exceptions. It should be fairly easy to bubble them up to the VM and enter catch blocks appropriately once raised.
