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

# A type operand this input does not ship. ART resolves the type operand before reading the register,
# so an unresolvable operand raises `NoClassDefFoundError` even for a null register, unlike the [JVMS]
# ordering (device fixture `Fx_CheckCastAbsentType.smali`; DALVIK-SEMANTICS.md, Type operations).
.method public static castToAbsentClass()V
    .locals 1

    check-cast v0, Lobf/absent;

    return-void
.end method

# Differs from `castToStringWithCatch()V` only in binding the caught exception to v1 rather than v0,
# the cast register. With v0 untouched by the handler, each arm of an ambiguous cast is observable:
# the success arm is narrowed to the target type and the throwing arm is not
# (`CheckCastOpTest.theForksSuccessArmIsNarrowedAndItsThrowingArmIsNot`, at @2 and @4).
#
# Addresses: `check-cast` (21c, 2 units) at @0, `nop` at @2, `return-void` at @3, `move-exception`
# (11x) at @4, `goto` (10t) at @5. The trailing `return-void` at @6 is unreachable and mirrors
# `castToStringWithCatch()V`.
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
