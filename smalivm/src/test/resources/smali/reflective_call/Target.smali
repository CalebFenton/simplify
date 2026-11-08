.class public Lreflective_call/Target;
.super Ljava/lang/Object;

.field private static touched:I

.method public constructor <init>()V
    .locals 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

# The reflective targets.

.method private static secret(Ljava/lang/Integer;Ljava/lang/Integer;)I
    .locals 2
    invoke-virtual {p0}, Ljava/lang/Integer;->intValue()I
    move-result v0
    invoke-virtual {p1}, Ljava/lang/Integer;->intValue()I
    move-result v1
    add-int/2addr v0, v1
    return v0
.end method

.method private static addInts(II)I
    .locals 1
    add-int v0, p0, p1
    return v0
.end method

.method private static widen(J)J
    .locals 0
    return-wide p0
.end method

.method private static echo(Ljava/lang/String;)Ljava/lang/String;
    .locals 0
    return-object p0
.end method

.method private static nothing()V
    .locals 0
    return-void
.end method

.method private static touch()V
    .locals 1
    const/4 v0, 0x1
    sput v0, Lreflective_call/Target;->touched:I
    return-void
.end method

.method private static fail()V
    .locals 1
    new-instance v0, Ljava/lang/IllegalStateException;
    invoke-direct {v0}, Ljava/lang/IllegalStateException;-><init>()V
    throw v0
.end method

.method static packageValue()I
    .locals 1
    const/4 v0, 0x3
    return v0
.end method

.method public value()I
    .locals 1
    const/4 v0, 0x7
    return v0
.end method

# Lookups: the declared method of this class named [p0] with parameter types [p1].

.method public static lookup(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    .locals 1
    const-class v0, Lreflective_call/Target;
    invoke-virtual {v0, p0, p1}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    return-object v0
.end method

.method public static lookupNoParameters(Ljava/lang/String;)Ljava/lang/reflect/Method;
    .locals 1
    const/4 v0, 0x0
    new-array v0, v0, [Ljava/lang/Class;
    invoke-static {p0, v0}, Lreflective_call/Target;->lookup(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    return-object v0
.end method

.method public static lookupSecret()Ljava/lang/reflect/Method;
    .locals 3
    const/4 v0, 0x2
    new-array v0, v0, [Ljava/lang/Class;
    const-class v1, Ljava/lang/Integer;
    const/4 v2, 0x0
    aput-object v1, v0, v2
    const/4 v2, 0x1
    aput-object v1, v0, v2
    const-string v1, "secret"
    invoke-static {v1, v0}, Lreflective_call/Target;->lookup(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    return-object v0
.end method

.method public static lookupAddInts()Ljava/lang/reflect/Method;
    .locals 3
    const/4 v0, 0x2
    new-array v0, v0, [Ljava/lang/Class;
    sget-object v1, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;
    const/4 v2, 0x0
    aput-object v1, v0, v2
    const/4 v2, 0x1
    aput-object v1, v0, v2
    const-string v1, "addInts"
    invoke-static {v1, v0}, Lreflective_call/Target;->lookup(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    return-object v0
.end method

# Callers.

.method public static invokeStaticBoxed()Ljava/lang/Object;
    .locals 5
    invoke-static {}, Lreflective_call/Target;->lookupSecret()Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x2
    new-array v1, v1, [Ljava/lang/Object;
    const/16 v2, 0x24
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v2
    const/4 v3, 0x0
    aput-object v2, v1, v3
    const/4 v2, 0x6
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v2
    const/4 v3, 0x1
    aput-object v2, v1, v3
    const/4 v4, 0x0
    invoke-virtual {v0, v4, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokeWithArguments([Ljava/lang/Object;)Ljava/lang/Object;
    .locals 2
    invoke-static {}, Lreflective_call/Target;->lookupSecret()Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1, p0}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokeUnboxesWideningShortAndChar()Ljava/lang/Object;
    .locals 4
    invoke-static {}, Lreflective_call/Target;->lookupAddInts()Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x2
    new-array v1, v1, [Ljava/lang/Object;
    const/16 v2, 0x28
    invoke-static {v2}, Ljava/lang/Short;->valueOf(S)Ljava/lang/Short;
    move-result-object v2
    const/4 v3, 0x0
    aput-object v2, v1, v3
    const/4 v2, 0x2
    invoke-static {v2}, Ljava/lang/Character;->valueOf(C)Ljava/lang/Character;
    move-result-object v2
    const/4 v3, 0x1
    aput-object v2, v1, v3
    const/4 v3, 0x0
    invoke-virtual {v0, v3, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokeWidensIntegerToLong()Ljava/lang/Object;
    .locals 4
    const/4 v0, 0x1
    new-array v0, v0, [Ljava/lang/Class;
    sget-object v1, Ljava/lang/Long;->TYPE:Ljava/lang/Class;
    const/4 v2, 0x0
    aput-object v1, v0, v2
    const-string v1, "widen"
    invoke-static {v1, v0}, Lreflective_call/Target;->lookup(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x1
    new-array v1, v1, [Ljava/lang/Object;
    const/4 v2, 0x5
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v2
    const/4 v3, 0x0
    aput-object v2, v1, v3
    invoke-virtual {v0, v3, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokePrimitiveWithNull()Ljava/lang/Object;
    .locals 4
    invoke-static {}, Lreflective_call/Target;->lookupAddInts()Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x2
    new-array v1, v1, [Ljava/lang/Object;
    const/4 v2, 0x1
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v2
    const/4 v3, 0x1
    aput-object v2, v1, v3
    const/4 v3, 0x0
    invoke-virtual {v0, v3, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokePrimitiveWithWrongWrapper()Ljava/lang/Object;
    .locals 4
    invoke-static {}, Lreflective_call/Target;->lookupAddInts()Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x2
    new-array v1, v1, [Ljava/lang/Object;
    const-wide/16 v2, 0x1
    invoke-static {v2, v3}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;
    move-result-object v2
    const/4 v3, 0x0
    aput-object v2, v1, v3
    const/4 v2, 0x1
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v2
    const/4 v3, 0x1
    aput-object v2, v1, v3
    const/4 v3, 0x0
    invoke-virtual {v0, v3, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokeReferenceWithWrongType()Ljava/lang/Object;
    .locals 4
    const/4 v0, 0x1
    new-array v0, v0, [Ljava/lang/Class;
    const-class v1, Ljava/lang/String;
    const/4 v2, 0x0
    aput-object v1, v0, v2
    const-string v1, "echo"
    invoke-static {v1, v0}, Lreflective_call/Target;->lookup(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x1
    new-array v1, v1, [Ljava/lang/Object;
    const/4 v2, 0x1
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v2
    const/4 v3, 0x0
    aput-object v2, v1, v3
    invoke-virtual {v0, v3, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokeReferenceWithNull()Ljava/lang/Object;
    .locals 4
    const/4 v0, 0x1
    new-array v0, v0, [Ljava/lang/Class;
    const-class v1, Ljava/lang/String;
    const/4 v2, 0x0
    aput-object v1, v0, v2
    const-string v1, "echo"
    invoke-static {v1, v0}, Lreflective_call/Target;->lookup(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x1
    new-array v1, v1, [Ljava/lang/Object;
    invoke-virtual {v0, v2, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokeWithTooFewArguments()Ljava/lang/Object;
    .locals 3
    invoke-static {}, Lreflective_call/Target;->lookupSecret()Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x1
    new-array v1, v1, [Ljava/lang/Object;
    const/4 v2, 0x0
    invoke-virtual {v0, v2, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokeWithNullArgumentArray()Ljava/lang/Object;
    .locals 2
    invoke-static {}, Lreflective_call/Target;->lookupSecret()Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static catchArgumentCountFailure()Ljava/lang/Object;
    .locals 3
    invoke-static {}, Lreflective_call/Target;->lookupSecret()Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x0
    :try_start
    invoke-virtual {v0, v1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    :try_end
    .catch Ljava/lang/IllegalArgumentException; {:try_start .. :try_end} :caught
    return-object v0
    :caught
    move-exception v0
    return-object v0
.end method

.method public static invokeVoid()Ljava/lang/Object;
    .locals 2
    const-string v0, "nothing"
    invoke-static {v0}, Lreflective_call/Target;->lookupNoParameters(Ljava/lang/String;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokeTouch()V
    .locals 2
    const-string v0, "touch"
    invoke-static {v0}, Lreflective_call/Target;->lookupNoParameters(Ljava/lang/String;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    return-void
.end method

.method public static invokeFailing()Ljava/lang/Object;
    .locals 2
    const-string v0, "fail"
    invoke-static {v0}, Lreflective_call/Target;->lookupNoParameters(Ljava/lang/String;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static catchFailingCause()Ljava/lang/Object;
    .locals 2
    const-string v0, "fail"
    invoke-static {v0}, Lreflective_call/Target;->lookupNoParameters(Ljava/lang/String;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x0
    :try_start
    invoke-virtual {v0, v1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    :try_end
    .catch Ljava/lang/reflect/InvocationTargetException; {:try_start .. :try_end} :caught
    return-object v0
    :caught
    move-exception v0
    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;
    move-result-object v0
    return-object v0
.end method

.method public static invokeVirtualOnChild()Ljava/lang/Object;
    .locals 3
    const-string v0, "value"
    invoke-static {v0}, Lreflective_call/Target;->lookupNoParameters(Ljava/lang/String;)Ljava/lang/reflect/Method;
    move-result-object v0
    new-instance v1, Lreflective_call/Child;
    invoke-direct {v1}, Lreflective_call/Child;-><init>()V
    const/4 v2, 0x0
    invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokeInstanceOnNull()Ljava/lang/Object;
    .locals 2
    const-string v0, "value"
    invoke-static {v0}, Lreflective_call/Target;->lookupNoParameters(Ljava/lang/String;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static invokeInstanceOnUnrelated()Ljava/lang/Object;
    .locals 3
    const-string v0, "value"
    invoke-static {v0}, Lreflective_call/Target;->lookupNoParameters(Ljava/lang/String;)Ljava/lang/reflect/Method;
    move-result-object v0
    const-string v1, "not a target"
    const/4 v2, 0x0
    invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method
