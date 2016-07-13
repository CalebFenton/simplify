.class public Lsget_test;
.super Ljava/lang/Object;

.field public static myInt:I
.field public static myIntLiteral:I = 0x42
.field public static myLong:J
.field public static myLongLiteral:J = 0xFFFFFFFFFL
.field public static myString:Ljava/lang/String;
.field public static myStringLiteral:Ljava/lang/String; = "life, what's life?"
.field public static myBoolean:Z
.field public static myBooleanLiteral:Z = true
.field public static myByte:B
.field public static myByteLiteral:B = 0xfT
.field public static myChar:C
.field public static myCharLiteral:C = 'x'
.field public static myShort:S
.field public static myShortLiteral:S = 0x100S
.field public static myDouble:D
.field public static myDoubleLiteral:D = 10000000000D
.field public static myFloat:F
.field public static myFloatLiteral:F = 1.1F

.field public static final myFinalInt:I

.method public static getStaticInt()V
    .locals 1
    sget v0, Lsget_test;->myInt:I
    return-void
.end method

.method public static getStaticIntLiteral()V
    .locals 1
    sget v0, Lsget_test;->myIntLiteral:I
    return-void
.end method

.method public static getStaticLong()V
    .locals 1
    sget-wide v0, Lsget_test;->myLong:J
    return-void
.end method

.method public static getStaticLongLiteral()V
    .locals 1
    sget-wide v0, Lsget_test;->myLongLiteral:J
    return-void
.end method

.method public static getStaticString()V
    .locals 1
    sget-object v0, Lsget_test;->myString:Ljava/lang/String;
    return-void
.end method

.method public static getStaticStringLiteral()V
    .locals 1
    sget-object v0, Lsget_test;->myStringLiteral:Ljava/lang/String;
    return-void
.end method

.method public static getStaticBoolean()V
    .locals 1
    sget-boolean v0, Lsget_test;->myBoolean:Z
    return-void
.end method

.method public static getStaticBooleanLiteral()V
    .locals 1
    sget-boolean v0, Lsget_test;->myBooleanLiteral:Z
    return-void
.end method

.method public static getStaticByte()V
    .locals 1
    sget-byte v0, Lsget_test;->myByte:B
    return-void
.end method

.method public static getStaticByteLiteral()V
    .locals 1
    sget-byte v0, Lsget_test;->myByteLiteral:B
    return-void
.end method

.method public static getStaticChar()V
    .locals 1
    sget-char v0, Lsget_test;->myChar:C
    return-void
.end method

.method public static getStaticCharLiteral()V
    .locals 1
    sget-char v0, Lsget_test;->myCharLiteral:C
    return-void
.end method

.method public static getStaticShort()V
    .locals 1
    sget-short v0, Lsget_test;->myShort:S
    return-void
.end method

.method public static getStaticShortLiteral()V
    .locals 1
    sget-short v0, Lsget_test;->myShortLiteral:S
    return-void
.end method

.method public static getStaticDouble()V
    .locals 1
    sget-wide v0, Lsget_test;->myDouble:D
    return-void
.end method

.method public static getStaticDoubleLiteral()V
    .locals 1
    sget-wide v0, Lsget_test;->myDoubleLiteral:D
    return-void
.end method

.method public static getStaticFloat()V
    .locals 1
    sget-wide v0, Lsget_test;->myFloat:F
    return-void
.end method

.method public static getStaticFloatLiteral()V
    .locals 1
    sget-wide v0, Lsget_test;->myFloatLiteral:F
    return-void
.end method

.method public static getStaticWhitelistedClassField()V
    .locals 1
    sget v0, Ljava/lang/Integer;->MAX_VALUE:I
    return-void
.end method

.method public static getStaticUnknownClassField()V
    .locals 1
    sget v0, Lsome/unknown/classzzzzz;->herp:I
    return-void
.end method
