.class public Lsget_test;
.super Ljava/lang/Object;

.field public static myInt:I
.field public static myLong:J
.field public static myString:Ljava/lang/String;
.field public static myBoolean:Z
.field public static myByte:B
.field public static myChar:C
.field public static myShort:S

.field public static final myFinalInt:I

.method public static TestStaticGetInt()V
    .locals 1
    sget v0, Lsget_test;->myInt:I
    return-void
.end method

.method public static TestStaticGetWide()V
    .locals 1
    sget-wide v0, Lsget_test;->myLong:J
    return-void
.end method

.method public static TestStaticGetObject()V
    .locals 1
    sget-object v0, Lsget_test;->myString:Ljava/lang/String;
    return-void
.end method

.method public static TestStaticGetBoolean()V
    .locals 1
    sget-boolean v0, Lsget_test;->myBoolean:Z
    return-void
.end method

.method public static TestStaticGetByte()V
    .locals 1
    sget-byte v0, Lsget_test;->myByte:B
    return-void
.end method

.method public static TestStaticGetChar()V
    .locals 1
    sget-char v0, Lsget_test;->myChar:C
    return-void
.end method

.method public static TestStaticGetShort()V
    .locals 1
    sget-short v0, Lsget_test;->myShort:S
    return-void
.end method
