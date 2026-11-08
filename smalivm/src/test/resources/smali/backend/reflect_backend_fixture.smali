.class public Lorg/cf/smalivm/core/ReflectBackendFixture;
.super Ljava/lang/Object;

.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static native overload(I)Ljava/lang/String;
.end method

.method public static native overload(Ljava/lang/Number;)Ljava/lang/String;
.end method

.method public static native mismatchedReturn()Ljava/lang/CharSequence;
.end method

.method public static native returnNullString()Ljava/lang/String;
.end method

.method public static native identityObject(Ljava/lang/Object;)Ljava/lang/Object;
.end method

.method public static native identityIntArray([I)[I
.end method

.method public static native mutateOwned(Ljava/lang/StringBuilder;Ljava/util/List;)V
.end method

.method public static native mutateExternal()V
.end method

.method public static native returnBoolean()Z
.end method

.method public static native returnByte()B
.end method

.method public static native returnChar()C
.end method

.method public static native returnShort()S
.end method

.method public static native returnInt()I
.end method

.method public static native returnLong()J
.end method

.method public static native returnFloat()F
.end method

.method public static native returnDouble()D
.end method

.method public static native returnBoxedByte()Ljava/lang/Byte;
.end method

.method public static native returnList()Ljava/util/List;
.end method

.method public static native returnNestedThrowable()Ljava/lang/Throwable;
.end method

.method public static native appendVoid(Ljava/lang/StringBuilder;)V
.end method

.method public static native throwProvided(Ljava/lang/Throwable;)V
.end method

.method public static native throwNested()V
.end method

.method public static native throwCauseCycle()V
.end method

.method public static native mutateThenThrow(Ljava/lang/StringBuilder;)V
.end method

.method public static native throwLinkage()V
.end method

.method public static native throwFatal()V
.end method

.method public static native missingOnHost()I
.end method

.method private static native privateValue()I
.end method

.method public static native acceptString(Ljava/lang/String;)I
.end method

.method public native touch()I
.end method

.method public static guestWins()I
    .registers 1
    const/4 v0, 0x7
    return v0
.end method
