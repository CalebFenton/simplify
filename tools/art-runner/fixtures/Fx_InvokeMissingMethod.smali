# PLAN 9 TASK 15 STEP 4c / ADJUDICATION 7b -- a missing METHOD on a PRESENT class.
# Adjudication 7 covers a missing CLASS. This is the fourth missing-member behaviour: the class
# resolves (java.lang.String is on the boot classpath) and the METHOD does not. The old engine
# hard-crashes the HOST here at op-build time (`opcode/InvokeOp.kt:311`), so it has no portable
# answer to port; the expected device shape is NoSuchMethodError -- a LinkageError/Error, same family
# as NoClassDefFoundError -- and this fixture is what turns that from INHERITED into measured.
# Read WITH the control Fx_InvokePresentType (same opcode, a method that exists) and with
# Fx_InvokeAbsentType (the missing-CLASS half), so "invoke is broken" cannot masquerade as either.
# Expected: THREW java.lang.NoSuchMethodError.
.class public LFx_InvokeMissingMethod;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 1

    invoke-static {}, Ljava/lang/String;->definitelyNotAMethod()Ljava/lang/Object;

    # Only reached if the call somehow resolved and returned.
    move-result-object v0
    return-object v0
.end method
