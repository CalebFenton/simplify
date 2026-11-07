.class Lconst_test;
.super Ljava/lang/Object;

.method public static const4()V
    .locals 1

    const/4 v0, -0x3

    return-void
.end method

.method public static const16()V
    .locals 1

    const/16 v0, 0x4242

    return-void
.end method

.method public static const16Negative()V
    .locals 1

    const/16 v0, -0xf

    return-void
.end method

.method public static const()V
    .locals 1

    const v0, 0x42424242

    return-void
.end method


.method public static constHigh16()V
    .locals 1

    const/high16 v0, 0x42420000

    return-void
.end method

.method public static constWide16()V
    .locals 2

    const-wide/16 v0, 0x4242

    return-void
.end method

.method public static constWide32()V
    .locals 2

    const-wide/32 v0, 0x1b7740

    return-void
.end method

.method public static constWide()V
    .locals 2

    const-wide v0, 0x4242424242424242L

    return-void
.end method

.method public static constWideHigh16()V
    .locals 2

    const-wide/high16 v0, 0x4242000000000000L

    return-void
.end method

.method public static constString()V
    .locals 1

    const-string v0, "When I need to identify rebels, I look for men with principles."

    return-void
.end method

.method public static constStringJumbo()V
    .locals 1

    const-string/jumbo v0, "When I need to identify JUMBO rebels, I look for JUMBO men with JUMBO principles."

    return-void
.end method

.method public static constClassLocal()V
    .locals 1

    const-class v0, Lconst_test;

    return-void
.end method

.method public static constClassRemote()V
    .locals 1

    const-class v0, Ljava/lang/Object;

    return-void
.end method

.method public static constClassUnknown()V
    .locals 1

    :try_start_0
    const-class v0, Lunknown/class;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v0
    goto :return
.end method

.method public static constClassThrowingClinit()V
    .locals 1

    const-class v0, Lorg/cf/smalivm/core/op/ThrowingClinitProbe;

    return-void
.end method

# THE THIRD OUTCOME of `OpBuilder.buildConstClass`, which is neither a resolvable class nor an absent one.
# `Ljava/lang/DexCache;` IS in the bundled framework class model (`isLoadable` says yes) and the HOST loader
# still cannot produce a `Class` for it: `SmaliClassLoader.findClass` refuses every `java.*` name
# (`dex/SmaliClassLoader.kt:43-57`) and the JVM parent it falls back to (`:32-36`) has no Android-only
# `java.*` class. That is an ENGINE limitation, not a device behaviour -- the class exists on a real device --
# so the answer is `UnsupportedOp` and NOT a guest `NoClassDefFoundError`, which would be spec §2.2's
# invented-infeasible-path defect. Measured: 506 of 2431 bundled `java.*` descriptors are in this state.
# Pinned by
# `core/OpBuilderTest.aConstClassOnAModelledButHostUnloadableClassIsUnsupportedRatherThanAResolutionFailure`.
.method public static constClassHostUnloadable()V
    .locals 1

    const-class v0, Ljava/lang/DexCache;

    return-void
.end method

# THE HANDLER-REACHABILITY TRIO (Plan 9 Task 15, ROW-23). Three methods, identical but for the ONE
# `.catch` type, around the same absent-type `const-class`. `constClassUnknown()V` above is the
# `Ljava/lang/Exception;` member of the set and is left where the corpus put it.
#
# The point is not that `NoClassDefFoundError` is "the right name". It is that an `Error` and an
# `Exception` put a guest handler on OPPOSITE sides of reachable-versus-dead, and obfuscator output is
# full of `catch (Exception)` wrappers -- so modelling this as the oracle's checked
# `ClassNotFoundException` tells a deobfuscator a block is live that a device never enters.
# Measured: `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`, cross-validated
# `[DEVICE](Tier B, nterp default, API 36)`; `tools/art-runner/fixtures/Fx_ConstClassAbsentType.smali`.
#
# Addresses, from the encoding: `const-class` (21c) is TWO code units, so it is at @0, the `nop` at @2,
# `return-void` at @3, `move-exception` (11x) at @4 and `goto` (10t) at @5.
.method public static constClassUnknownCatchingThrowable()V
    .locals 1

    :try_start_0
    const-class v0, Lunknown/class;
    :try_end_0
    .catch Ljava/lang/Throwable; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v0
    goto :return
.end method

.method public static constClassUnknownCatchingError()V
    .locals 1

    :try_start_0
    const-class v0, Lunknown/class;
    :try_end_0
    .catch Ljava/lang/Error; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v0
    goto :return
.end method

# THE CONTROL for the trio: a PRESENT type under the very same `catch (Exception)` shape. Without it,
# "the Exception handler is not reached" also passes against a build where `const-class` never runs at
# all, or where the try/catch machinery is broken -- so the absence, not the plumbing, is what is being
# measured. Mirrors `Fx_ConstClassPresentType.smali`'s role on the device side.
.method public static constClassLocalWithCatch()V
    .locals 1

    :try_start_0
    const-class v0, Lconst_test;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v0
    goto :return
.end method
