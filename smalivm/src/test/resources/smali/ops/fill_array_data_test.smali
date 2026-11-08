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

# An empty payload fits every array, including a zero-length one, so the bounds exception is infeasible even
# for an unknown array register. It is the only fixture that exercises FillArrayDataOp's empty-payload guard.
.method public static fillArrayDataEmptyPayload()V
    .locals 1

    fill-array-data v0, :array_0

    return-void

    :array_0
    .array-data 4
    .end array-data
.end method

# The payload label points at an ordinary instruction rather than an `.array-data` table. smali assembles it,
# and the decoder must refuse it as unsupported rather than fail on the host.
.method public static fillArrayDataWithMalformedPayloadTarget()V
    .locals 1

    fill-array-data v0, :not_a_payload

    :not_a_payload
    return-void
.end method

# fillArrayDataInt()V's five-element payload inside a Ljava/lang/Exception; handler, so an over-long payload's
# ArrayIndexOutOfBoundsException is caught. `move-exception v1` keeps the array in v0 so a test can read it
# back after the catch.
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

