.class Lif_test;
.super Ljava/lang/Object;

.method public static ifEqual()V
    .locals 2

    if-eq v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static ifNotEqual()V
    .locals 2

    if-ne v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static ifLessThan()V
    .locals 2

    if-lt v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static ifGreaterOrEqual()V
    .locals 2

    if-ge v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static ifGreaterThan()V
    .locals 2

    if-gt v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static ifLessOrEqual()V
    .locals 2

    if-le v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static ifEqualZero()V
    .locals 1

    if-eqz v0, :end

    nop

    :end
    return-void
.end method

.method public static ifNotEqualZero()V
    .locals 1

    if-nez v0, :end

    nop

    :end
    return-void
.end method

.method public static ifLessThanZero()V
    .locals 1

    if-ltz v0, :end

    nop

    :end
    return-void
.end method

.method public static ifGreaterOrEqualZero()V
    .locals 1

    if-gez v0, :end

    nop

    :end
    return-void
.end method
.method public static ifGreaterThanZero()V
    .locals 1

    if-gtz v0, :end

    nop

    :end
    return-void
.end method

.method public static ifLessOrEqualZero()V
    .locals 1

    if-lez v0, :end

    nop

    :end
    return-void
.end method

.method public static ifEqIntToByte()V
    .locals 2

    aget-byte v2, v1, v0
    const/16 v0, 0x1

    if-eq v0, v2, :end

    nop

    :end
    return-void
.end method

.method public static ifEqByteToInt()V
    .locals 2

    aget-byte v2, v1, v0
    const/16 v0, 0x1

    if-eq v2, v0, :end

    nop

    :end
    return-void
.end method
