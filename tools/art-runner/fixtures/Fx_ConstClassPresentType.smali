# Control: Const-class naming a PRESENT type (java.lang.String).
# The control for Fx_ConstClassAbsentType: same opcode, a type that DOES resolve on the boot
# classpath, so a batch in which "everything fails to resolve" would show here as a failure too
# and be caught. A clean return proves const-class's resolution path works and isolates the
# absent-type fixture's throw to the absence itself.
# Expected: java.lang.String(java.lang.String).
.class public LFx_ConstClassPresentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    const-class v0, Ljava/lang/String;

    invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;
    move-result-object v1
    return-object v1
.end method
