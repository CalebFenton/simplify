.class public Lsput_test;
.super Ljava/lang/Object;

.field public static myInt:I
.field public static myLong:J
.field public static myString:Ljava/lang/String;
.field public static myBoolean:Z
.field public static myByte:B
.field public static myChar:C
.field public static myShort:S

.method public static putStaticInt()V
    .locals 1
    sput v0, Lsput_test;->myInt:I
    return-void
.end method

.method public static putStaticWide()V
    .locals 1
    sput-wide v0, Lsput_test;->myLong:J
    return-void
.end method

.method public static putStaticObject()V
    .locals 1
    sput-object v0, Lsput_test;->myString:Ljava/lang/String;
    return-void
.end method

.method public static putStaticBoolean()V
    .locals 1
    sput-boolean v0, Lsput_test;->myBoolean:Z
    return-void
.end method

.method public static putStaticByte()V
    .locals 1
    sput-byte v0, Lsput_test;->myByte:B
    return-void
.end method

.method public static putStaticChar()V
    .locals 1
    sput-char v0, Lsput_test;->myChar:C
    return-void
.end method

.method public static putStaticShort()V
    .locals 1
    sput-short v0, Lsput_test;->myShort:S
    return-void
.end method
