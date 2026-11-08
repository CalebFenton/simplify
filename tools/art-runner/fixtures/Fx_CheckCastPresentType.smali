# Control: Check-cast naming a PRESENT type (java.lang.String), a String source.
# The control for Fx_CheckCastAbsentType: same opcode, a type that DOES resolve, and a value that
# casts cleanly. Returning the string proves check-cast's resolution path works.
# Expected: java.lang.String(x).
.class public LFx_CheckCastPresentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 1

    const-string v0, "x"
    check-cast v0, Ljava/lang/String;

    return-object v0
.end method
