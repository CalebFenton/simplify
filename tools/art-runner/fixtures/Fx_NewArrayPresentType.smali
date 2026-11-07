# WORKLIST ROW 7 CONTROL -- new-array whose component type is PRESENT (java.lang.String).
# The control for Fx_NewArrayAbsentType: same opcode, a component that DOES resolve, so a batch in
# which "everything fails to resolve" would fail here too and be caught. A clean length proves
# new-array's component-resolution path works.
# Expected: java.lang.Integer(1).
.class public LFx_NewArrayPresentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    const/4 v1, 0x1
    new-array v0, v1, [Ljava/lang/String;
    array-length v1, v0

    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v0
    return-object v0
.end method
