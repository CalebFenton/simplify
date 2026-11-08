# Control: Instance-of naming a PRESENT type (java.lang.String), non-null source.
# The control for Fx_InstanceOfAbsentType: same opcode, a type that DOES resolve. Answering 1 proves
# instance-of's resolution path works and isolates the absent-type fixture's throw.
# Expected: java.lang.Integer(1).
.class public LFx_InstanceOfPresentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 3

    const-string v0, "x"
    instance-of v1, v0, Ljava/lang/String;

    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v2
    return-object v2
.end method
