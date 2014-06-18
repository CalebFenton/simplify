.class public final Lconstant_propigator_test;
.super Ljava/lang/Object;

.method public static TestEmitInteger()V
.end method

.method public static TestEmitByte()V
.end method

.method public static TestIntegerMovePropigation()I
    .locals 2

    const/4 v0, 0x5
    move v1, v0
    return v1
.end method

.method public static TestIntegerMethodReflection()I
    .locals 2

    new-instance v0, Ljava/lang/Integer;
    const/4 v1, 0x1
    invoke-direct {v0, v1}, Ljava/lang/Integer;-><init>(I)V
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I
    move-result v0
    return v0
.end method


.method private static GiveInteger()I
    .locals 1
    const/4 v0, 0x5
    return v0
.end method

.method private static GiveByte()B
    .locals 1
    return v0
.end method

.method private static GiveShort()S
    .locals 1
    return v0
.end method

.method private static GiveCharacter()C
    .locals 1
    return v0
.end method

.method private static GiveBoolean()Z
    .locals 1
    return v0
.end method

.method private static GiveLong()J
    .locals 2
    return v0
.end method

.method private static GiveFloat()F
    .locals 1
    return v0
.end method

.method private static GiveDouble()D
    .locals 1
    return v0
.end method

.method private static GiveString()Ljava/lant/String;
    .locals 1
    return v0
.end method

.method private static GiveClass()Ljava/lang/Class;
    .locals 1
    return v0
.end method
