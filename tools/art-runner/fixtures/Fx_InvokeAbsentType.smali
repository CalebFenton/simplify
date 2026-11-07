# PLAN 9 TASK 15 STEP 4c -- `invoke-static` naming a method on an ABSENT class.
# The other half of the gap DALVIK-SEMANTICS.md flagged: method resolution ([JVMS] 5.4.3.3) also
# resolves the defining class first, so an absent class behind an invoke was INHERITED extrapolation
# from the five measured resolving opcodes rather than a measurement.
# Read WITH the control Fx_InvokePresentType.
# Expected: THREW java.lang.NoClassDefFoundError.
.class public LFx_InvokeAbsentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 1

    invoke-static {}, Lcom/example/DefinitelyMissing;->absentMethod()Ljava/lang/Object;

    # Only reached if the call somehow resolved and returned.
    move-result-object v0
    return-object v0
.end method
