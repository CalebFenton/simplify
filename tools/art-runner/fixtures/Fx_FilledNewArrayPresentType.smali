# WORKLIST ROW 14 CONTROL -- filled-new-array naming a PRESENT type (java.lang.String).
# The control for Fx_FilledNewArrayAbsentType: same opcode, a type that DOES resolve. A clean length
# proves filled-new-array's resolution path works and isolates the absent-type fixture's throw.
# Expected: java.lang.Integer(1).
.class public LFx_FilledNewArrayPresentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 3

    const-string v0, "x"
    filled-new-array {v0}, [Ljava/lang/String;
    move-result-object v1
    array-length v2, v1

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v0
    return-object v0
.end method
