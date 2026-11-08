.class public Lclass_init/const_class_probe;
.super Ljava/lang/Object;

# `const-class` does not initialize its operand. The device measurement is
# `tools/art-runner/fixtures/Fx_ConstClassDoesNotInitialize.smali`, silent against its loud
# control `Fx_StaticFieldDoesInitialize.smali`. This also keeps `ConstOp`'s declared `NONE` true.
#
# The operand `Lclass_init/witness;` has a real guest `<clinit>`, so a regression shows as an
# initializer run. The `Class` is returned so the run also proves the operand resolved instead
# of degrading to `UnsupportedOp` (a `Partial` run).

.method public static resolvesWitnessClass()Ljava/lang/Class;
    .registers 1
    const-class v0, Lclass_init/witness;
    return-object v0
.end method
