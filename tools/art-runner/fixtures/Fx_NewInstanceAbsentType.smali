# `new-instance` of an absent class. It is the control for Fx_NewInstanceDoesInitialize, proving the
# opcode executes and resolves its operand here, and the new-instance row of the resolution-failure
# family alongside Fx_ConstClassAbsentType, Fx_CheckCastAbsentType and Fx_NewArrayAbsentType.
.class public LFx_NewInstanceAbsentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    new-instance v0, Lcom/example/DefinitelyMissing;

    # Only reached if resolution unexpectedly succeeded.
    const-string v1, "RESOLVED"
    return-object v1
.end method
