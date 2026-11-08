# Fixtures built around a projection claim (a repr shape, a value kind, or handler reachability)
# rather than around one opcode.
.class public Ldifferential/smoke;
.super Ljava/lang/Object;

.method public static returnFive()I
    .registers 1
    const/4 v0, 0x5
    return v0
.end method

.method public static nestedArray()[[I
    .registers 4
    const/4 v0, 0x1
    new-array v0, v0, [[I

    const/4 v1, 0x2
    new-array v1, v1, [I

    const/4 v2, 0x0
    const/4 v3, 0x1
    aput v3, v1, v2

    const/4 v2, 0x1
    const/4 v3, 0x2
    aput v3, v1, v2

    const/4 v2, 0x0
    aput-object v1, v0, v2

    return-object v0
.end method

.method public static newObject()Ljava/lang/Object;
    .registers 1
    new-instance v0, Ljava/lang/Object;
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    return-object v0
.end method

.method public static selfReferentialArray()[Ljava/lang/Object;
    .registers 2
    const/4 v0, 0x1
    new-array v0, v0, [Ljava/lang/Object;

    const/4 v1, 0x0
    aput-object v0, v0, v1

    return-object v0
.end method

# One inner array reachable from two sibling slots: no cycle, so the repr renders the inner array
# fully both times (`[[7], [7]]`, never `[[7], [...]]`). 7, not 0, so a lost store reads `[[0], [0]]`.
.method public static sharedInnerArray()[[I
    .registers 4
    const/4 v0, 0x2
    new-array v0, v0, [[I

    const/4 v1, 0x1
    new-array v1, v1, [I

    const/4 v2, 0x0
    const/4 v3, 0x7
    aput v3, v1, v2

    aput-object v1, v0, v2
    const/4 v2, 0x1
    aput-object v1, v0, v2

    return-object v0
.end method

# A fresh reference array element is a known null; `const/4 v0, 0x0` is Known(0, I), not a substitute.
# v2, not v0, for the destination, so a dest/array transposition is visible.
.method public static nullElementFromFreshArray()Ljava/lang/Object;
    .registers 3
    const/4 v0, 0x1
    new-array v0, v0, [Ljava/lang/Object;

    const/4 v1, 0x0
    aget-object v2, v0, v1

    return-object v2
.end method

# fill-array-data over an array the method allocates. Three elements exactly fill the length-3 array,
# so no bounds exception is feasible ([BC] fill-array-data); 1/2/3, not zeros, so a fill differs from
# a no-op.
.method public static fillArrayDataRoundTrip()[I
    .registers 1
    const/4 v0, 0x3
    new-array v0, v0, [I

    fill-array-data v0, :data

    return-object v0

    :data
    .array-data 4
        0x1
        0x2
        0x3
    .end array-data
.end method

# Array stores and fills with one unprovable operand, a method parameter. Each array has length two
# with one slot written, so a precise answer is visible per element: `[?, 0]`, not `[?, ?]`.

# Only the stored value is unprovable: the write lands on slot 0 and nothing else changes.
.method public static aputUnknownValue(I)[I
    .registers 4
    const/4 v0, 0x2
    new-array v0, v0, [I

    const/4 v1, 0x0
    aput p0, v0, v1

    return-object v0
.end method

# The array is a fresh non-null `[I` and the index is 0, so neither NPE nor a bounds exception is
# feasible and the handler is unreachable. The catch binds v2, which is unobserved; v1 is the
# observed Known(0, I).
.method public static aputUnknownValueWithCatch(I)V
    .registers 4
    const/4 v0, 0x2
    new-array v0, v0, [I

    const/4 v1, 0x0

    :try_start
    aput p0, v0, v1
    :try_end
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :catch

    :return
    return-void

    :catch
    move-exception v2

    goto :return
.end method

# The stored value is 7 and the index is unprovable: every slot is either its old value or 7, and
# a bounds exception is feasible.
.method public static aputUnknownIndex(I)[I
    .registers 4
    const/4 v0, 0x2
    new-array v0, v0, [I

    const/4 v1, 0x7
    aput v1, v0, p0

    return-object v0
.end method

# An unknown element of a `filled-new-array`: no array, index, or count operand, so nothing forks.
# `move-result-object` is required ([BC] filled-new-array).
.method public static filledNewArrayUnknownElement(I)[I
    .registers 3
    const/4 v0, 0x1

    filled-new-array {v0, p0}, [I
    move-result-object v1

    return-object v1
.end method

# `new String[]{"x", null}` as d8 emits it: v1 is `const/4 0x0`, so the reference element reads as
# null, not 0 (`docs/DALVIK-SEMANTICS.md`). The method returns void so v2 is observed without a
# return-type check.
.method public static filledNewArrayOfStringsWithNullElement()V
    .registers 3
    const-string v0, "x"

    const/4 v1, 0x0

    filled-new-array {v0, v1}, [Ljava/lang/String;
    move-result-object v2

    return-void
.end method

# Handler reachability for a synthesized NullPointerException. Exactly one of the four clauses matches,
# so the reached set does not depend on clause order.

# `const/4 v0, 0x0` is the null idiom ([BC] General design): a definite NullPointerException, so only
# `catch (Exception)` is live and nothing escapes.
.method public static throwNullIdiomFourClauses()Ljava/lang/Object;
    .registers 1
    const/4 v0, 0x0
    :try_start
    throw v0
    :try_end
    .catch Ljava/io/IOException; {:try_start .. :try_end} :not_an_npe
    .catch Ljava/lang/Error; {:try_start .. :try_end} :not_an_error
    .catch Ljava/lang/IllegalStateException; {:try_start .. :try_end} :not_an_ise
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :an_exception

    :not_an_npe
    const-string v0, "io"
    return-object v0

    :not_an_error
    const-string v0, "error"
    return-object v0

    :not_an_ise
    const-string v0, "ise"
    return-object v0

    :an_exception
    move-exception v0
    return-object v0
.end method

# The same method with 0x5: a register whose last write was primitive reads as the null reference
# (`docs/DALVIK-SEMANTICS.md`, `throw`), so the reached set and unhandled result match the 0x0 method.
.method public static throwNonZeroConstFourClauses()Ljava/lang/Object;
    .registers 1
    const/4 v0, 0x5
    :try_start
    throw v0
    :try_end
    .catch Ljava/io/IOException; {:try_start .. :try_end} :not_an_npe
    .catch Ljava/lang/Error; {:try_start .. :try_end} :not_an_error
    .catch Ljava/lang/IllegalStateException; {:try_start .. :try_end} :not_an_ise
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :an_exception

    :not_an_npe
    const-string v0, "io"
    return-object v0

    :not_an_error
    const-string v0, "error"
    return-object v0

    :not_an_ise
    const-string v0, "ise"
    return-object v0

    :an_exception
    move-exception v0
    return-object v0
.end method
