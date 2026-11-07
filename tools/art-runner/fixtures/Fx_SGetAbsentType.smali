# PLAN 9 TASK 15 STEP 4c -- `sget` naming a static field on an ABSENT class.
# Closes the gap DALVIK-SEMANTICS.md's resolution-failure family flagged: only the five RESOLVING
# opcodes (const-class, check-cast, instance-of, new-array, filled-new-array) had fixtures, so
# `sget` on an absent class was INHERITED extrapolation ([JVMS] 5.4.3.2 field resolution -> 5.4.3.1)
# carrying the sibling measurement rather than a measurement of its own.
# Field resolution requires the DEFINING CLASS to resolve first, so the failure should be the same
# NoClassDefFoundError the five siblings raise -- but "should be" is what this fixture replaces.
# Read WITH the control Fx_SGetPresentType, so "everything fails to resolve" cannot masquerade as
# this result.
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
