.class Lfill_array_data_test;
.super Ljava/lang/Object;

.method public static fillArrayDataInt()V
    .locals 1

    fill-array-data v0, :array_0

    return-void

    :array_0
    .array-data 4
        0x1
        0x2
        0x3
        0x4
        0x5
    .end array-data
.end method

# A payload with ZERO elements. Legal, and not a degenerate curiosity: [BC]'s requirement is that the table
# "must contain no more elements than will fit in the array", which an empty table satisfies for EVERY array
# length -- including 0. So this payload provably cannot overrun anything, and the bounds exception is
# infeasible for it even when the array register is completely unknown. That is the single input which makes
# `FillArrayDataOp`'s `payloadElements.isNotEmpty()` guard load-bearing rather than decorative; without the
# guard, an unprovable array here forks an ArrayIndexOutOfBoundsException that cannot happen and tells a
# deobfuscator a dead `catch` is live.
#
# smali assembles an empty element list happily. DO NOT "tidy" this method away as an empty payload with no
# purpose -- it is the only fixture that can kill that guard. See
# `core/op/FillArrayDataOpTest.aZeroElementPayloadCannotOverrunAnyArraySoNoBoundsExceptionIsForked`.
.method public static fillArrayDataEmptyPayload()V
    .locals 1

    fill-array-data v0, :array_0

    return-void

    :array_0
    .array-data 4
    .end array-data
.end method

# A MALFORMED payload target: the "branch" offset points at an ordinary instruction rather than at an
# `.array-data` table. smali assembles it because the operand is just a label, and it is exactly the shape
# obfuscated output produces. The core must answer UNSUPPORTED_OP for it -- see
# `core/OpBuilderTest.aFillArrayDataWhoseTargetIsNotAnArrayPayloadIsUnsupportedRatherThanAHostFailure` --
# rather than let `MethodView.instructionAt`'s requireNotNull, or a failed cast, escape the decoder and
# become an indistinguishable HOST_FAILURE.
.method public static fillArrayDataWithMalformedPayloadTarget()V
    .locals 1

    fill-array-data v0, :not_a_payload

    :not_a_payload
    return-void
.end method

# The same five-element payload as fillArrayDataInt()V, wrapped in a try with a
# Ljava/lang/Exception; handler so the ArrayIndexOutOfBoundsException an over-long payload raises is
# CAUGHT rather than escaping (core/op/FillArrayDataOpTest.aPayloadLongerThanTheArrayThrowsWithoutStoringAnything
# drives it with a two-element array seed, so five elements do not fit).
#
# `.locals 2`, and `move-exception v1` rather than v0 -- DELIBERATE, and not a style choice to tidy
# away. Every other method in this file uses `.locals 1`, where `move-exception` would have to target
# v0 and would clobber the array register. v0 keeping the array is exactly what the second half of
# that test needs: it reads the array back out of v0 after the catch to prove the failed fill stored
# NOTHING. With `.locals 1` that half would be inexpressible.
.method public static fillArrayDataIntWithCatch()V
    .locals 2

    :try_start_0
    fill-array-data v0, :array_0
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    return-void

    :catch_0
    move-exception v1

    return-void

    :array_0
    .array-data 4
        0x1
        0x2
        0x3
        0x4
        0x5
    .end array-data
.end method

.method public static fillArrayDataLongWithLongs()V
    .locals 1

    fill-array-data v0, :array_0

    return-void

    :array_0
    .array-data 8
        0x1000000000L
        0x2000000000L
        0x3L
    .end array-data
.end method

.method public static fillArrayDataLongWithInts()V
    .locals 1

    fill-array-data v0, :array_0

    return-void

    :array_0
    .array-data 8
        0x10
        0x20
        0x30
    .end array-data
.end method

.method public static fillArrayDataBoolean()V
    .locals 1

    fill-array-data v0, :array_0

    return-void

    :array_0
    .array-data 1
        0x1t
        0x1t
        0x0t
        0x1t
    .end array-data
.end method

.method public static fillArrayDataBooleanTwo()V
    .locals 1

    fill-array-data v0, :array_0

    return-void

    :array_0
    .array-data 1
        0x2t
        0x1t
    .end array-data
.end method

.method public static fillArrayDataByte()V
    .locals 1

    fill-array-data v0, :array_0

    return-void

    :array_0
    .array-data 1
        0xat
        0xbt
        0xct
        0xdt
    .end array-data
.end method

.method public static fillArrayDataChar()V
    .locals 1

    fill-array-data v0, :array_0

    return-void

    :array_0
    .array-data 2
        0x61s
        0x62s
        0x63s
    .end array-data
.end method

.method public static fillArrayDataShortWithShorts()V
    .locals 1

    fill-array-data v0, :array_0

    return-void

    :array_0
    .array-data 2
        0x64s
        0xc8s
        0x5s
    .end array-data
.end method

.method public static fillArrayDataShortWithInts()V
    .locals 1

    fill-array-data v0, :array_0

    return-void

    :array_0
    .array-data 2
        0x10
        0x20
        0x30
    .end array-data
.end method

.method public static fillArrayDataFloat()V
    .locals 1

    fill-array-data v0, :array_0

    return-void

    :array_0
    .array-data 4
        0x3f800000
        0x40000000
    .end array-data
.end method

.method public static fillArrayDataDouble()V
    .locals 1

    fill-array-data v0, :array_0

    return-void

    :array_0
    .array-data 8
        0x3fb999999999999aL
        0x3ff8000000000000L
    .end array-data
.end method

