.class public Lreflective_call/Initialized;
.super Ljava/lang/Object;

.field private static value:I

.method static constructor <clinit>()V
    .locals 1
    const/4 v0, 0x5
    sput v0, Lreflective_call/Initialized;->value:I
    return-void
.end method

.method public static read()I
    .locals 1
    sget v0, Lreflective_call/Initialized;->value:I
    return v0
.end method

.method public static invokeRead()Ljava/lang/Object;
    .locals 3
    const-class v0, Lreflective_call/Initialized;
    const-string v1, "read"
    const/4 v2, 0x0
    new-array v2, v2, [Ljava/lang/Class;
    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method
