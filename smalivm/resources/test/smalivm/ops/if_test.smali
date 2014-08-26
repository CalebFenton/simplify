.class Lif_test;
.super Ljava/lang/Object;

.method public static IfEquals()V
    .locals 2

    if-eq v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static IfNotEquals()V
    .locals 2

    if-ne v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static IfLessThan()V
    .locals 2

    if-lt v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static IfGreaterOrEqual()V
    .locals 2

    if-ge v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static IfGreaterThan()V
    .locals 2

    if-gt v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static IfLessOrEqual()V
    .locals 2

    if-le v0, v1, :end

    nop

    :end
    return-void
.end method

.method public static IfEqualZero()V
    .locals 1

    if-eqz v0, :end

    nop

    :end
    return-void
.end method

.method public static IfNotEqualZero()V
    .locals 1

    if-nez v0, :end

    nop

    :end
    return-void
.end method

.method public static IfLessThanZero()V
    .locals 1

    if-ltz v0, :end

    nop

    :end
    return-void
.end method

.method public static IfGreaterOrEqualZero()V
    .locals 1

    if-gez v0, :end

    nop

    :end
    return-void
.end method
.method public static IfGreaterThanZero()V
    .locals 1

    if-gtz v0, :end

    nop

    :end
    return-void
.end method

.method public static IfLessOrEqualZero()V
    .locals 1

    if-lez v0, :end

    nop

    :end
    return-void
.end method

.method public static IfEqIntToByte()V
    .locals 2

    aget-byte v2, v1, v0
    const/16 v0, 0x1

    if-eq v0, v2, :end

    nop

    :end
    return-void
.end method

.method public static IfEqByteToInt()V
    .locals 2

    aget-byte v2, v1, v0
    const/16 v0, 0x1

    if-eq v2, v0, :end

    nop

    :end
    return-void
.end method
