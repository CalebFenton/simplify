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

# The type reference parses and resolution fails at execution, throwing NoClassDefFoundError
# (DALVIK-SEMANTICS.md, Fx_NewInstanceAbsentType). new-instance is 21c, two code units, so the nop is at
# address 2, the fall-through a definite throw never reaches.
.method public static newAbsentClassInstance()V
    .locals 1

    new-instance v0, Ldoes/not/exist;

    nop

    return-void
.end method

# Two unrelated allocations of one type. Heap identity is the allocation id, so they are two distinct
# objects whose values never agree.
.method public static newTwoLocalInstances()V
    .locals 2

    new-instance v0, Lnew_instance_test;
    new-instance v1, Lnew_instance_test;

    return-void
.end method

# An app class with a guest <clinit>, which runs before the allocation. Lclass_init/witness;'s initializer
# allocates an int array.
.method public static newWitnessInstance()V
    .locals 1

    new-instance v0, Lclass_init/witness;

    return-void
.end method
