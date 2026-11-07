# PLAN 9 TASK 15 / ADJUDICATION 7b -- THE ORDERING: missing METHOD vs NULL RECEIVER.
# Both conditions hold at once: the receiver is null AND the method does not exist on the (present)
# class. Whichever throw arrives names the order, and the order is observable to a guest handler --
# `catch (NullPointerException)` and `catch (NoSuchMethodError)` are on opposite sides of the
# Exception/Error split, so this decides which one a deobfuscator believes is reachable.
# The old engine could not answer it: adjudication 2 gives the new core the null-receiver NPE the
# oracle never had, and the oracle crashes the host on the missing method before executing anything,
# so the two never composed there. Left to propagate; the dispatcher reports whichever came first.
# Read WITH Fx_InvokeMissingMethod (same missing method, no receiver at all -> NoSuchMethodError).
# Expected: THREW java.lang.NoSuchMethodError if resolution precedes the null check, which is what
# the resolution-before-null-check ordering already measured for check-cast/instance-of predicts.
.class public LFx_InvokeMissingMethodNullReceiver;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    const/4 v0, 0x0
    invoke-virtual {v0}, Ljava/lang/String;->definitelyNotAMethod()Ljava/lang/Object;

    # Only reached if neither condition threw.
    move-result-object v1
    return-object v1
.end method
