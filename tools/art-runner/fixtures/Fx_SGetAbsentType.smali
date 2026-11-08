# `sget` naming a static field on an absent class. Field resolution resolves the defining class first
# ([JVMS] 5.4.3.2). Read with the control Fx_SGetPresentType.
# Expected: THREW java.lang.NoClassDefFoundError.
.class public LFx_SGetAbsentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    sget v0, Lcom/example/DefinitelyMissing;->ABSENT_FIELD:I

    # Only reached if the field read somehow succeeded.
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v1
    return-object v1
.end method
