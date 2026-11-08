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

# These move the built array into a real register with `move-result-object`; the result slot alone is
# not exposed by any ExecutionGraph query. The register-count variants cover the 1/2/4/5-register
# Instruction35c decode shapes behaviorally.
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

# The range form with exactly five registers. `A` is 8 bits for 3rc, so {v0 .. v4} is legal, and the
# rendering must follow the opcode, not the register count.
.method public static filledNewArrayRangeFive()V
    .locals 6

    filled-new-array/range {v0 .. v4}, [I
    move-result-object v5

    return-void
.end method

# Component types beyond `[I`: [BC] accepts reference types, and ART throws `InternalError` for
# single-word primitive components other than int (DALVIK-SEMANTICS.md, filled-new-array).
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

# Two decode-time refusals, both `UnsupportedOp`. `[J` is forbidden by [BC] ("no arrays of long or
# double") and fails ART verification; a non-array type operand violates "The type must be an array
# type". Pinned by OpBuilderTest.aFilledNewArrayWithNoFillableComponentTypeIsUnsupportedRatherThanAHostFailure.
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

# An absent component class: the op throws `NoClassDefFoundError` and exercises its `!loadable`
# branch. Shaped like `new_array_test.smali`'s createNonExistentArrayClass()V without the try block:
# an `Error` escapes a `catch (Exception)` anyway, and `CoreTester.testThrows` needs a handler-free
# method.
.method public static filledNewArrayOfMissingClass()V
    .locals 3

    filled-new-array {v0, v1}, [Ldoes/not/exist;
    move-result-object v2

    return-void
.end method
