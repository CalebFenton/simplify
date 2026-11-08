.class Lfilled_new_array_test;
.super Ljava/lang/Object;

.method public static filledNewArray()V
    .locals 3

    filled-new-array {v0, v1, v2}, [I

    return-void
.end method

.method public static filledNewArrayRange()V
    .locals 6

    filled-new-array/range {v0 .. v5}, [I

    return-void
.end method

# The first two methods leave the array in the result slot, which no graph query exposes; the methods
# below move it into a register. The register-count variants cover every Instruction35c shape (1 to 5).
.method public static filledNewArrayThenMoveResult()V
    .locals 4

    filled-new-array {v0, v1, v2}, [I
    move-result-object v3

    return-void
.end method

.method public static filledNewArrayRangeThenMoveResult()V
    .locals 7

    filled-new-array/range {v0 .. v5}, [I
    move-result-object v6

    return-void
.end method

.method public static filledNewArrayOne()V
    .locals 2

    filled-new-array {v0}, [I
    move-result-object v1

    return-void
.end method

.method public static filledNewArrayTwo()V
    .locals 3

    filled-new-array {v0, v1}, [I
    move-result-object v2

    return-void
.end method

.method public static filledNewArrayFour()V
    .locals 5

    filled-new-array {v0, v1, v2, v3}, [I
    move-result-object v4

    return-void
.end method

.method public static filledNewArrayFive()V
    .locals 6

    filled-new-array {v0, v1, v2, v3, v4}, [I
    move-result-object v5

    return-void
.end method

# The range form with five registers: `A` is 8 bits for 3rc, so {v0 .. v4} is legal and renders as a range.
.method public static filledNewArrayRangeFive()V
    .locals 6

    filled-new-array/range {v0 .. v4}, [I
    move-result-object v5

    return-void
.end method

# [BC] accepts reference components; ART implements no single-word primitive component except int.
.method public static filledNewArrayOfStrings()V
    .locals 3

    filled-new-array {v0, v1}, [Ljava/lang/String;
    move-result-object v2

    return-void
.end method

.method public static filledNewArrayOfBytes()V
    .locals 3

    filled-new-array {v0, v1}, [B
    move-result-object v2

    return-void
.end method

# Both refused at decode time: `[J` is forbidden ([BC]: "no arrays of long or double"), and a non-array type
# operand has no component to fill ([BC]: "The type must be an array type").
.method public static filledNewArrayOfLongs()V
    .locals 3

    filled-new-array {v0, v1}, [J
    move-result-object v2

    return-void
.end method

.method public static filledNewArrayOfNonArrayType()V
    .locals 3

    filled-new-array {v0, v1}, I
    move-result-object v2

    return-void
.end method

# An absent component class. NoClassDefFoundError is an Error, which `catch (Exception)` cannot catch, so the
# method has no handler.
.method public static filledNewArrayOfMissingClass()V
    .locals 3

    filled-new-array {v0, v1}, [Ldoes/not/exist;
    move-result-object v2

    return-void
.end method
