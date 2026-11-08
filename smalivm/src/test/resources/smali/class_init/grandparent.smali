.class public Lclass_init/grandparent;
.super Ljava/lang/Object;

# The top of a three-deep hierarchy pinning that a superclass is initialized before its subclass.
# `grandparent` <- `parent` <- `child` each declare a `<clinit>`; the test reads the order the
# three activations were created in. A reversed order would let a subclass `<clinit>` read a
# superclass static before it is set.
#
# Three levels, because two cannot distinguish "walk to the top, then come down" from "reverse
# the pair".

.method public static constructor <clinit>()V
    .registers 1
    nop
    return-void
.end method
