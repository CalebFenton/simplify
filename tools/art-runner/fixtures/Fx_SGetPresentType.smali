# PLAN 9 TASK 15 STEP 4c CONTROL -- `sget` on a PRESENT class (java.lang.Integer.MAX_VALUE).
# The control for Fx_SGetAbsentType: same opcode, a defining class that DOES resolve, so a batch in
# which "everything fails to resolve" would show here as a failure too. A clean 2147483647 proves
# sget's resolution path works and isolates the absent-type fixture's throw to the absence itself.
# Expected: java.lang.Integer(2147483647).
.class public LFx_SGetPresentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    sget v0, Ljava/lang/Integer;->MAX_VALUE:I

    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v1
    return-object v1
.end method
