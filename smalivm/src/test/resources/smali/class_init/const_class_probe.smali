.class public Lclass_init/const_class_probe;
.super Ljava/lang/Object;

# ---------------------------------------------------------------------------------------
# `const-class` MUST NOT INITIALIZE (Plan 8's choice, kept by Plan 9 Task 16 Step 2).
#
# This is the one half of the class-init trigger question that was `[DEVICE]`-measured
# BEFORE Task 16: `tools/art-runner/fixtures/Fx_ConstClassDoesNotInitialize.smali` reads
# silent against its control `Fx_StaticFieldDoesInitialize.smali`, which prints -- one
# silent, one loud, same witness mechanism.
#
# Before this task "does not initialize" was trivially true, because nothing in the engine
# initialized anything. Now it is a claim that can REGRESS, so it gets a fixture and an
# assertion rather than a comment. It also keeps `ConstOp`'s declared `NONE` truthful, which
# is where the oracle has Correctness Defect 21 (`docs/architecture/opcodes.md:176`: effect
# `NONE` reported while a host `<clinit>` runs).
#
# The operand is `Lclass_init/witness;`, which HAS a real guest `<clinit>` -- so a
# regression would be visible as an actual initializer run rather than as a no-op.
#
# `const-class` resolves through this engine's own `SmaliClassLoader`, and the returned
# `Class` is returned so the run also proves the operand resolved rather than silently
# degrading to an `UnsupportedOp` (which would make the run `Partial` and the test would say
# so).
# ---------------------------------------------------------------------------------------

.method public static resolvesWitnessClass()Ljava/lang/Class;
    .registers 1
    const-class v0, Lclass_init/witness;
    return-object v0
.end method
