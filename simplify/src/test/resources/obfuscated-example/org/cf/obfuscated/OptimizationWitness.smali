.class public Lorg/cf/obfuscated/OptimizationWitness;
.super Ljava/lang/Object;

.field private static message:Ljava/lang/String; = "field value"

.method public static main([Ljava/lang/String;)V
    .locals 0
    return-void
.end method

.method private static addFour(IIII)I
    .locals 1
    add-int v0, p0, p1
    add-int v0, v0, p2
    add-int v0, v0, p3
    return v0
.end method

.method public static recoveredString()Ljava/lang/String;
    .locals 2
    new-instance v0, Ljava/lang/String;
    const/16 v1, 0xc
    new-array v1, v1, [B
    fill-array-data v1, :text
    invoke-direct {v0, v1}, Ljava/lang/String;-><init>([B)V
    return-object v0

    :text
    .array-data 1
        0x73t 0x65t 0x63t 0x72t 0x65t 0x74t
        0x4dt 0x65t 0x74t 0x68t 0x6ft 0x64t
    .end array-data
.end method

.method public static knownClass()Ljava/lang/Class;
    .locals 2
    const-string v0, "java.lang.String"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v1
    return-object v1
.end method

.method public static constantPredicate()I
    .locals 1
    const/4 v0, 0x0
    if-eq v0, v0, :end
    const/4 v0, 0x1
    :end
    return v0
.end method

.method public static invokeReflective()Ljava/lang/Object;
    .locals 8
    const-class v0, Lorg/cf/obfuscated/OptimizationWitness;
    const-string v1, "addFour"
    const/4 v2, 0x4
    new-array v2, v2, [Ljava/lang/Class;
    sget-object v3, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;
    const/4 v4, 0x0
    aput-object v3, v2, v4
    const/4 v4, 0x1
    aput-object v3, v2, v4
    const/4 v4, 0x2
    aput-object v3, v2, v4
    const/4 v4, 0x3
    aput-object v3, v2, v4
    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    const/4 v1, 0x4
    new-array v2, v1, [Ljava/lang/Object;
    const/4 v3, 0x0
    const/4 v4, 0x1
    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v4
    aput-object v4, v2, v3
    const/4 v3, 0x1
    const/4 v4, 0x2
    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v4
    aput-object v4, v2, v3
    const/4 v3, 0x2
    const/4 v4, 0x3
    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v4
    aput-object v4, v2, v3
    const/4 v3, 0x3
    const/4 v4, 0x4
    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v4
    aput-object v4, v2, v3
    const/4 v1, 0x0
    invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v7
    return-object v7
.end method

.method public static getReflectiveField()Ljava/lang/Object;
    .locals 4
    const-class v0, Lorg/cf/obfuscated/OptimizationWitness;
    const-string v1, "message"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v3
    return-object v3
.end method
