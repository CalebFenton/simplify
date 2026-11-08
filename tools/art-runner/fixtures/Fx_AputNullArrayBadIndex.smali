# Ordering: NPE strictly precedes AIOOBE precedes ASE. The order rests on [JVMS] `aastore`'s
# "Otherwise" chaining with no ART or [BC] support, and ART already inverts the inherited order for
# `new-array` (see Fx_NewArrayAbsentTypeNegativeLength), so it is measured here.
#
# THIS FIXTURE'S CELL: null array AND an out-of-range index, simultaneously. If NPE wins
# the answer is `java.lang.NullPointerException`; if the bounds check somehow runs first
# it is `java.lang.ArrayIndexOutOfBoundsException`. Only one instruction executes, so the
# answer is unambiguous. `Fx_AputBadIndexIncompatibleValue` covers the AIOOBE-vs-ASE
# half.
#
# WHY THE NULL COMES OUT OF A HELPER METHOD instead of `const/4 v0, 0x0`: a zero constant
# gives the register the verifier's ZERO type, not an array type, and then the question
# of whether ART's `VerifyAPut` accepts the instruction at all contaminates the
# measurement. `nullArray()` has a DECLARED return type, so `move-result-object` types
# the register as `[Ljava/lang/String;` and the instruction is unambiguously
# verifier-valid. What is measured is then the RUNTIME's ordering, which is the question.
#
# SELF-CONTAINED: the helper builds the (null) operand and the index is a constant.

.class public LFx_AputNullArrayBadIndex;
.super Ljava/lang/Object;

.method private static nullArray()[Ljava/lang/String;
    .locals 1

    const/4 v0, 0x0
    return-object v0
.end method

.method public static run()Ljava/lang/Object;
    .locals 3

    invoke-static {}, LFx_AputNullArrayBadIndex;->nullArray()[Ljava/lang/String;
    move-result-object v0

    const/16 v1, 0x5
    const-string v2, "value"

    # THE OPCODE UNDER TEST: null array, index 5, a perfectly assignable value.
    aput-object v2, v0, v1

    const-string v2, "NO_THROW"
    return-object v2
.end method
