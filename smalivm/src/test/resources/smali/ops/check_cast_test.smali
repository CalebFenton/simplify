.class Lcheck_cast_test;
.super Ljava/lang/Object;

.method public static castToObject()V
    .locals 1

    check-cast v0, Ljava/lang/Object;

    return-void
.end method

.method public static castToString()V
    .locals 1

    check-cast v0, Ljava/lang/String;

    return-void
.end method

# Whatever p0 throws is caught by the catch-all and cast; every Throwable is Serializable.
.method public static castCaughtThrowableToSerializable(Ljava/lang/Throwable;)V
    .locals 1

    :try_start_0
    throw p0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catch_0

    :catch_0
    move-exception v0
    check-cast v0, Ljava/io/Serializable;

    return-void
.end method

# The caught Exception is symbolic, or the NullPointerException a null p0 raises; the cast is outside the try.
.method public static castCaughtExceptionToCloneable(Ljava/lang/Exception;)V
    .locals 1

    :try_start_0
    throw p0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    :catch_0
    move-exception v0
    check-cast v0, Ljava/lang/Cloneable;

    return-void
.end method

.method public static castCaughtNullPointerExceptionToCloneable()V
    .locals 1

    new-instance v0, Ljava/lang/NullPointerException;
    invoke-direct {v0}, Ljava/lang/NullPointerException;-><init>()V

    :try_start_0
    throw v0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    :catch_0
    move-exception v0
    check-cast v0, Ljava/lang/Cloneable;

    return-void
.end method

.method public static castCaughtCloneableExceptionToCloneable()V
    .locals 1

    new-instance v0, Lcloneable_exception;
    invoke-direct {v0}, Lcloneable_exception;-><init>()V

    :try_start_0
    throw v0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    :catch_0
    move-exception v0
    check-cast v0, Ljava/lang/Cloneable;

    return-void
.end method

.method public static castToStringWithCatch()V
    .locals 2

    :try_start_0
    check-cast v0, Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v0
    goto :return

    return-void
.end method

# A TYPE OPERAND THIS INPUT DOES NOT SHIP, and the same answer `instance-of` gets. [ART] resolves the
# type operand BEFORE reading the register (`interpreter_switch_impl-inl.h:820-825` before `:829`), so
# an unresolvable operand raises `NoClassDefFoundError` even for a null register -- the opposite of the
# [JVMS] ordering. This core decides loadability at DECODE time and throws a guest
# `NoClassDefFoundError` at EXECUTE time (Plan 9 Task 15 / ROW-23, reversing Plan 8's `UnsupportedOp`
# refusal now that the type is measured: `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`,
# `Fx_CheckCastAbsentType.smali`, whose source register is null and which still throws). Pinned by
# `core/op/CheckCastOpTest.anAbsentCastTypeThrowsNoClassDefFoundErrorEvenOnANullSource` and
# `core/OpBuilderTest.aCheckCastAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal`.
.method public static castToAbsentClass()V
    .locals 1

    check-cast v0, Lobf/absent;

    return-void
.end method

# Added by Plan 8 Task 9, and the ONE difference from `castToStringWithCatch()V` above is the register
# `move-exception` targets. That method binds the caught exception into **v0 -- the register the cast is
# on** -- so it cannot observe which arm of an ambiguous cast carries the narrowed declared type: the
# handler path overwrites v0 before the join. Binding it into v1 instead leaves v0 holding exactly what
# each arm left there, which is what makes `docs/architecture/opcodes.md:172`'s SECOND half (*"their
# normal path omits target-type refinement"*) observable at all -- the oracle's `:38` exit performs no
# `assignRegister`, and `exceptions.md:247` records the whole behaviour as untested. Consumed by
# `core/op/CheckCastOpTest.theForksSuccessArmIsNarrowedAndItsThrowingArmIsNot`, which asserts the two arms
# separately at @2 and @4.
#
# Addresses, from the encoding rather than assumed: `check-cast` (21c, TWO code units) at @0, `nop` at
# @2, `return-void` at @3, `move-exception` (11x) at @4, `goto` (10t) at @5. The trailing `return-void`
# at @6 is unreachable and is kept only to mirror `castToStringWithCatch()V`.
.method public static castToStringWithCatchOnAnotherRegister()V
    .locals 2

    :try_start_0
    check-cast v0, Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v1
    goto :return

    return-void
.end method
