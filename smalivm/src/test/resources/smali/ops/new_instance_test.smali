.class Lnew_instance_test;
.super Ljava/lang/Object;

.method public static newLocalInstance()V
    .locals 1

    new-instance v0, Lnew_instance_test;

    return-void
.end method

.method public static newNonLocalInstance()V
    .locals 1

    new-instance v0, Ljava/lang/Integer;

    return-void
.end method

# --- Methods below appended by Plan 9 Task 17 (the two above are the original oracle corpus;
# --- appended at the END so no existing address moves).

# `new-instance` of a class absent from the loaded set. Mirrors new_array_test.smali's
# createNonExistentArrayClass()V exactly: the type reference parses, resolution fails at execute
# time, and the guest error is a NoClassDefFoundError (measured on both interpreter tiers by
# Task 16 Step 0's Fx_NewInstanceAbsentType). The nop after the new-instance is what the
# `wasReached` negative reads: new-instance is 21c = 2 code units, so the nop sits at address 2.
.method public static newAbsentClassInstance()V
    .locals 1

    new-instance v0, Ldoes/not/exist;

    nop

    return-void
.end method

# Two UNRELATED allocations of ONE type -- adjudication 8's fixture. Under the oracle's
# type-equality aliasing (type/UninitializedInstance.java compares type only) these two registers
# hold "equal" values; under the core's id-based heap identity they are two distinct objects.
.method public static newTwoLocalInstances()V
    .locals 2

    new-instance v0, Lnew_instance_test;
    new-instance v1, Lnew_instance_test;

    return-void
.end method

# `new-instance` of an app class WITH a guest `<clinit>` -- the class-init integration proof that
# the real op (not ClassInitTest's stub) triggers initialization. Lclass_init/witness; is the
# ordinary-<clinit> fixture Task 16 built; its initializer allocates a [I, which is the observable.
.method public static newWitnessInstance()V
    .locals 1

    new-instance v0, Lclass_init/witness;

    return-void
.end method
