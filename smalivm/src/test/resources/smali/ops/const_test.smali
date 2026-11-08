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

# One 64-bit literal written by two wide const forms on two paths that meet at the return.
.method public static wideFormsOfOneLiteral(I)J
    .locals 2

    if-eqz p0, :sized
    const-wide v0, 0x5L
    goto :done

    :sized
    const-wide/16 v0, 0x5

    :done
    return-wide v0
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

# `Ljava/lang/DexCache;` is in the bundled framework class model, but the host loader cannot produce a
# `Class` for it: `SmaliClassLoader` refuses `java.*` names and the JVM parent has no Android-only
# `java.*` class. The class exists on a device, so this is an engine limit: `UnsupportedOp`, not a
# guest `NoClassDefFoundError` that would invent an infeasible path. Pinned by
# `OpBuilderTest.aConstClassOnAModelledButHostUnloadableClassIsUnsupportedRatherThanAResolutionFailure`.
.method public static constClassHostUnloadable()V
    .locals 1

    const-class v0, Ljava/lang/DexCache;

    return-void
.end method

# Handler reachability: these methods differ only in the `.catch` type around the same absent-type
# `const-class`; `constClassUnknown()V` above is the `Ljava/lang/Exception;` case. The absent type
# raises `NoClassDefFoundError`, an `Error`, so a `catch (Exception)` wrapper is never entered on a
# device (`tools/art-runner/fixtures/Fx_ConstClassAbsentType.smali`, measured on both tiers).
#
# Addresses: `const-class` (21c, 2 units) at @0, `nop` at @2, `return-void` at @3,
# `move-exception` (11x) at @4, `goto` (10t) at @5.
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

# Control: a present type under the same `catch (Exception)` shape, so "the handler is not reached"
# cannot pass because `const-class` never ran or try/catch is broken. Mirrors
# `Fx_ConstClassPresentType.smali` on the device side.
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
