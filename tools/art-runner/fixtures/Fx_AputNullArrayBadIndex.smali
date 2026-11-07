# WORKLIST ROW 12, FIRST HALF -- "Ordering: NPE strictly precedes AIOOBE precedes ASE".
# Class B, and named the HIGHEST-VALUE ITEM on the whole worklist.
#
# WHY IT IS THE HIGHEST-VALUE ITEM: the ordering rests entirely on [JVMS] `aastore`'s
# "Otherwise" chaining -- pure inheritance, no ART citation, no [BC] support. And
# worklist row 8 is a DOCUMENTED case of ART inverting exactly this kind of inherited
# ordering: `new-array`'s inherited rule is "resolve the component type, then check the
# size", and ART's `CheckArrayAlloc` (`entrypoint_utils-inl.h:292-322`) tests the count
# FIRST. One inherited ordering rule already turned out to be backwards, so this one is
# not entitled to the benefit of the doubt.
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
