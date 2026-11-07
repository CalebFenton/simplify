# WORKLIST ROW 12, THIRD CELL -- "NPE strictly precedes ASE", proved DIRECTLY rather than by
# transitivity. `Fx_AputNullArrayBadIndex` pins NPE-over-AIOOBE and `Fx_AputBadIndexIncompatibleValue`
# pins AIOOBE-over-ASE, so NPE-over-ASE already follows through the chain -- but a strict three-way
# order deserves its direct edge too, and this is the one cell where a null array and an
# ASE-eligible value are present at the SAME instruction.
#
# THIS FIXTURE'S CELL: null array AND an unassignable value, one valid index between them so the
# index is not a confound. Only one instruction executes, so the answer is unambiguous:
#
#   NPE -> the null dereference is resolved before the store's assignability check ([JVMS] `aastore`
#          "Otherwise" chaining; [ART]'s `CheckAssignable` is itself guarded by `if (object != nullptr)`,
#          so a null array never reaches the type check).
#   ASE -> assignability was checked against a null array, which no runtime does.
#
# WHY THE NULL COMES OUT OF A HELPER METHOD instead of `const/4 v0, 0x0`: a zero constant gives the
# register the verifier's ZERO type, not an array type, and then whether `VerifyAPut` accepts the
# instruction at all contaminates the measurement. `nullArray()` has a DECLARED return type, so
# `move-result-object` types the register as `[Ljava/lang/String;` and the store is unambiguously
# verifier-valid -- the reasoning `Fx_AputNullArrayBadIndex` documents at length.
#
# WHY THE VALUE COMES OUT OF `new-instance` + `invoke-direct`: an `Object` stored into a `String[]` is
# verifier-valid to assemble and to link (covariance is a runtime question, worklist row 1), and it is
# the `Fx_AputObjectIncompatible` pattern for building an unassignable value in bytecode.
#
# SELF-CONTAINED: the helper builds the (null) array and the value is a fresh `java.lang.Object`.

.class public LFx_AputNullArrayIncompatibleValue;
.super Ljava/lang/Object;

.method private static nullArray()[Ljava/lang/String;
    .locals 1

    const/4 v0, 0x0
    return-object v0
.end method

.method public static run()Ljava/lang/Object;
    .locals 3

    invoke-static {}, LFx_AputNullArrayIncompatibleValue;->nullArray()[Ljava/lang/String;
    move-result-object v0

    new-instance v1, Ljava/lang/Object;
    invoke-direct {v1}, Ljava/lang/Object;-><init>()V

    const/4 v2, 0x0

    # THE OPCODE UNDER TEST: null array, valid index 0, an unassignable `Object` value.
    aput-object v1, v0, v2

    const-string v2, "NO_THROW"
    return-object v2
.end method
