# Type-hierarchy fixtures: reference-type joins, instance-of, check-cast, and const-class.
.class public Ldifferential/types;
.super Ljava/lang/Object;

# Two arms return `[String` and `[StringBuilder`, joining covariantly to `[Object` (JLS 4.10.3).
# The selector is a parameter so both arms are explored.
.method public static arrayArmsJoinCovariantly(I)Ljava/lang/Object;
    .registers 3

    const/4 v1, 0x1

    if-eqz v2, :other

    new-array v0, v1, [Ljava/lang/String;

    goto :end

    :other
    new-array v0, v1, [Ljava/lang/StringBuilder;

    :end
    return-object v0
.end method

# `[Integer` and `[Long` join through their common component supertype to `[Number`, the tightest
# array upper bound.
.method public static arrayArmsJoinToNumberArray(I)Ljava/lang/Object;
    .registers 3

    const/4 v1, 0x1

    if-eqz v2, :other

    new-array v0, v1, [Ljava/lang/Integer;

    goto :end

    :other
    new-array v0, v1, [Ljava/lang/Long;

    :end
    return-object v0
.end method

# An `int[]` is an instance of Cloneable (JLS 4.10.3,
# `docs/DALVIK-SEMANTICS.md#subtype-testing-entirely-inherited`), so the answer is 1.
.method public static intArrayIsCloneable()I
    .registers 2

    const/4 v1, 0x1

    new-array v0, v1, [I

    instance-of v0, v0, Ljava/lang/Cloneable;

    return v0
.end method

# instance-of on an unknown String parameter, which may be null, so the result is Unknown(Z)
# ([JVMS] instanceof). v1, not v0, is the destination and the observed register.
.method public static unknownStringIsInstanceOfString(Ljava/lang/String;)I
    .registers 3

    instance-of v1, v2, Ljava/lang/String;

    return v1
.end method

# instance-of on a register whose last write was primitive: [ART] reads it as the null reference,
# so the answer is 0.
.method public static primitiveRegisterIsInstanceOfObject()I
    .registers 2

    const/4 v1, 0x5

    instance-of v0, v1, Ljava/lang/Object;

    return v0
.end method

# check-cast on an unknown `Object` parameter: both pass and ClassCastException are feasible, so the
# handler is reachable. A `String` parameter would pass provably. The catch binds v1; v0 holds the
# observed constant 7 on every returning path. p0 is v3.
.method public static unknownCastMayThrow(Ljava/lang/Object;)I
    .registers 4

    const/4 v0, 0x7

    :try_start_0
    check-cast v3, Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    :return
    return v0

    :catch_0
    move-exception v1

    goto :return
.end method

# A known String cast to Object succeeds and the register takes the cast type. A widening cast, so
# replacing the type (as [ART] does) differs from intersecting it.
.method public static castStringToObject()Ljava/lang/Object;
    .registers 1

    const-string v0, "great maker"

    check-cast v0, Ljava/lang/Object;

    return-object v0
.end method

# const-class of a system class. The result is compared by `Class.toString()`, which is
# loader-independent.
.method public static constClassObject()Ljava/lang/Class;
    .registers 1

    const-class v0, Ljava/lang/Object;

    return-object v0
.end method

# A known String is an instance of Object, so instance-of folds to 1.
.method public static knownStringIsInstanceOfObject()I
    .registers 2

    const-string v0, "great maker"

    instance-of v0, v0, Ljava/lang/Object;

    return v0
.end method
