# CONTROL for `Fx_NewInstanceDoesInitialize`, and it is not optional -- plus the
# `new-instance` row of the resolution-failure family (Plan 9 Task 15's worklist).
#
# Two jobs, one fixture:
#
# 1. It proves `new-instance` genuinely EXECUTES and RESOLVES its operand on this runtime.
#    `Fx_NewInstanceDoesInitialize`'s allocation is bare -- the uninitialized reference
#    cannot be read -- so that fixture cannot prove its own opcode ran, and "no CLINIT_RAN"
#    would otherwise be indistinguishable from "the allocation never happened". An operand
#    that resolves against nothing makes the resolution observable.
#
# 2. It settles which throwable `new-instance` of an absent class raises, in the same shape
#    as `Fx_ConstClassAbsentType` / `Fx_CheckCastAbsentType` / `Fx_NewArrayAbsentType`.
#    Task 17 needs the answer.
#
# Read WITH `Fx_NewInstanceDoesInitialize`, which allocates a class that IS present.
.class public LFx_NewInstanceAbsentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    new-instance v0, Lcom/example/DefinitelyMissing;

    # Only reached if resolution unexpectedly succeeded.
    const-string v1, "RESOLVED"
    return-object v1
.end method
