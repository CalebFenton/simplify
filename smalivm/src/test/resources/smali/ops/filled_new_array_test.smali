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

# Plans 6–7 additions. The existing two methods have no `move-result-object`, so the array they build lives
# only in the result slot -- which no ExecutionGraph query exposes, deliberately (CoreTester.test says so
# and refuses the corpus's negative-pseudo-register idiom). These move it into a real register, which is
# exactly what that guard's message prescribes, and the ASSERTED VALUES are unchanged.
#
# The register-count variants exist because the corpus's coverage of the 1/2/4/5-register Instruction35c
# decode lived entirely in mockk tests (see the adjudication note): `filledNewArray()V` is 3 registers and
# `filledNewArrayRange()V` is 6, so two of the five 35c shapes and neither boundary were covered
# behaviorally.
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

# The RANGE form with exactly five registers, which is the case the oracle's "size > 5 -> print the range
# spelling" rule gets wrong and the opcode-based rule gets right. `A` is 8 bits for 3rc, so {v0 .. v4} is
# perfectly legal and no existing fixture covers it (filledNewArrayRange()V uses six). See Step 2's toString.
.method public static filledNewArrayRangeFive()V
    .locals 6

    filled-new-array/range {v0 .. v4}, [I
    move-result-object v5

    return-void
.end method

# The two rows the first draft's "anything but [I fails DEX verification" claim wrongly excluded. [BC] says
# reference types ARE acceptable; ART does NOT implement the single-word primitive components.
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

# The two DECODE-TIME refusals, both `UnsupportedOp`. `[J` is forbidden by SPEC ([BC]: "no arrays of long
# or double") and is an ART verifier hard failure, so no device ever reaches an interpreter for it; a
# non-array type operand violates [BC]'s "The type must be an array type" and has no component to fill.
# Both exist so the guard in OpBuilder.buildFilledNewArray is not INERT -- see
# OpBuilderTest.aFilledNewArrayWithNoFillableComponentTypeIsUnsupportedRatherThanAHostFailure.
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

# NOT in the brief's fixture block, and added because without it the op's `!loadable` branch would be
# INERT -- no test could kill a mutation that deleted it, which the mutate-to-prove rule forbids. Shaped
# like `new_array_test.smali`'s createNonExistentArrayClass()V minus the try block: the throw is a
# `NoClassDefFoundError`, an `Error`, so a `catch (Exception)` clause could not catch it anyway (design
# decision 4), and a handler-free method is what `CoreTester.testThrows` is correct for.
.method public static filledNewArrayOfMissingClass()V
    .locals 3

    filled-new-array {v0, v1}, [Ldoes/not/exist;
    move-result-object v2

    return-void
.end method
