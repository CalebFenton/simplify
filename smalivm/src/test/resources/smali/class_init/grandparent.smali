.class public Lclass_init/grandparent;
.super Ljava/lang/Object;

# ---------------------------------------------------------------------------------------
# THE TOP of the three-deep hierarchy that pins DIVERGENCE 4 (adjudication 4): a superclass
# is initialized BEFORE its subclass.
#
# `grandparent` <- `parent` <- `child`, each declaring its own `<clinit>`, and each
# `<clinit>` distinguishable only by its signature -- which is enough, because the claim is
# about ORDER and the test reads the order the three activations were created in.
#
# The old engine gets this backwards: it recurses over ancestors FIRST
# (`VirtualMachine.kt:457-491` -- the intent is plainly correct) and then a LIFO deque
# reverses it (`add` is `addLast`, `step()` takes `peekLast()`, `:145`). A superclass
# `<clinit>` that sets a static its subclass's `<clinit>` reads then yields a wrong
# constant, which is exactly the constant-recovery case this engine exists for.
#
# Three levels and not two, deliberately: two would be satisfied by any implementation that
# happens to visit the immediate superclass first, while three distinguishes "walk to the
# top, then come down" from "reverse the pair".
# ---------------------------------------------------------------------------------------

.method public static constructor <clinit>()V
    .registers 1
    nop
    return-void
.end method
