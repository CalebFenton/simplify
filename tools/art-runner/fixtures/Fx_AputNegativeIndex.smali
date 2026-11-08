# A negative index is `ArrayIndexOutOfBoundsException`, not a distinct exception.
#
# THIS FIXTURE'S CELL: a valid, non-null `String[1]`, an index of -1, and a perfectly assignable
# value. Only the index is wrong, so exactly one outcome is possible:
#
#   AIOOBE -> a negative index is out of range like any other. [ART] compares UNSIGNED
#             (`Array::CheckIsValidIndex`), so `index < 0` and `index >= length` are ONE check and -1
#             is not a `NegativeArraySizeException` or any other distinct exception.
#   anything else -> the unsigned-compare reading is wrong.
#
# Read with `Fx_AputBadIndexIncompatibleValue` (index 5): the two together show the bounds check does
# not care WHICH way the index leaves the range.
#
# SELF-CONTAINED: built from `new-array`, `const`, `const-string`, `aput-object` and `return-object`.
# The throw makes the `return-object` tail unreachable; the tail exists only to verify.

.class public LFx_AputNegativeIndex;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 3

    const/4 v0, 0x1
    new-array v1, v0, [Ljava/lang/String;

    const/4 v2, -0x1

    const-string v0, "value"

    # THE OPCODE UNDER TEST: index -1 into a length-1 array, storing an assignable `String`.
    aput-object v0, v1, v2

    const-string v0, "NO_THROW"
    return-object v0
.end method
