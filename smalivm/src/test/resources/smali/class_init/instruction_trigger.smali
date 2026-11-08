.class public Lclass_init/instruction_trigger;
.super Ljava/lang/Object;

# Instruction triggers of class initialization. `new-instance` initializes its class before it allocates,
# so a trigger that had work to do executes twice: the class-init prelude resumes at the trigger's own
# address. Addresses are method-local: @0 new-instance (21c, 2 units), @2 const/4, @3 return.

.method public static newNoClinit()I
    .registers 2
    new-instance v1, Lclass_init/no_clinit;
    const/4 v0, 0x7
    return v0
.end method

.method public static newWitness()I
    .registers 2
    new-instance v1, Lclass_init/witness;
    const/4 v0, 0x7
    return v0
.end method

.method public static newChild()I
    .registers 2
    new-instance v1, Lclass_init/child;
    const/4 v0, 0x7
    return v0
.end method

.method public static newReentrant()I
    .registers 2
    new-instance v1, Lclass_init/reentrant;
    const/4 v0, 0x7
    return v0
.end method

# Two triggers for one class on one path: the second finds the class initialized.
.method public static newWitnessTwice()I
    .registers 2
    new-instance v1, Lclass_init/witness;
    new-instance v1, Lclass_init/witness;
    const/4 v0, 0x7
    return v0
.end method

# @0 if-eqz (21t, 2 units), @2 new-instance reached on one branch, @4 new-instance reached on both. With p0
# unknown the run forks, and each path initializes the class for itself: two `<clinit>` runs.
.method public static newWitnessOnOneBranchThenOnBoth(I)I
    .registers 3
    if-eqz p0, :skip
    new-instance v1, Lclass_init/witness;
    :skip
    new-instance v1, Lclass_init/witness;
    const/4 v0, 0x7
    return v0
.end method
