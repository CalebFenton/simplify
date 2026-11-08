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

# `new-instance` of a class absent from the loaded set, mirroring new_array_test.smali's
# createNonExistentArrayClass()V: resolution fails at execute time with a NoClassDefFoundError (measured
# on both interpreter tiers). new-instance is 21c = 2 code units, so the nop the `wasReached` negative
# reads sits at address 2.
.method public static newAbsentClassInstance()V
    .locals 1

    new-instance v0, Ldoes/not/exist;

    nop

    return-void
.end method

# Two unrelated allocations of one type: heap identity is the allocation id, so these are two distinct
# objects, not equal values.
.method public static newTwoLocalInstances()V
    .locals 2

    new-instance v0, Lnew_instance_test;
    new-instance v1, Lnew_instance_test;

    return-void
.end method

# `new-instance` of an app class with a guest `<clinit>`: the real op triggers initialization.
# Lclass_init/witness;'s initializer allocates a [I, which is the observable.
.method public static newWitnessInstance()V
    .locals 1

    new-instance v0, Lclass_init/witness;

    return-void
.end method
