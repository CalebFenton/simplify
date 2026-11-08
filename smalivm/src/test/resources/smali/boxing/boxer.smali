.class public Lboxing/boxer;
.super Ljava/lang/Object;

# Each method boxes its unknown parameter with the box class's valueOf and returns the box.
.method public static boxInt(I)Ljava/lang/Integer;
    .registers 1
    invoke-static {p0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object p0
    return-object p0
.end method

.method public static boxLong(J)Ljava/lang/Long;
    .registers 2
    invoke-static {p0, p1}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;
    move-result-object p0
    return-object p0
.end method

.method public static boxShort(S)Ljava/lang/Short;
    .registers 1
    invoke-static {p0}, Ljava/lang/Short;->valueOf(S)Ljava/lang/Short;
    move-result-object p0
    return-object p0
.end method

.method public static boxByte(B)Ljava/lang/Byte;
    .registers 1
    invoke-static {p0}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;
    move-result-object p0
    return-object p0
.end method

.method public static boxChar(C)Ljava/lang/Character;
    .registers 1
    invoke-static {p0}, Ljava/lang/Character;->valueOf(C)Ljava/lang/Character;
    move-result-object p0
    return-object p0
.end method

.method public static boxBoolean(Z)Ljava/lang/Boolean;
    .registers 1
    invoke-static {p0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;
    move-result-object p0
    return-object p0
.end method

.method public static boxFloat(F)Ljava/lang/Float;
    .registers 1
    invoke-static {p0}, Ljava/lang/Float;->valueOf(F)Ljava/lang/Float;
    move-result-object p0
    return-object p0
.end method

.method public static boxDouble(D)Ljava/lang/Double;
    .registers 2
    invoke-static {p0, p1}, Ljava/lang/Double;->valueOf(D)Ljava/lang/Double;
    move-result-object p0
    return-object p0
.end method
