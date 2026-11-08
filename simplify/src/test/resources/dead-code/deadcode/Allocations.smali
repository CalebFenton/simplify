.class public Ldeadcode/Allocations;
.super Ljava/lang/Object;

# A method that drops a framework object first initializes that class with a kept call, so the dropped allocation
# is not itself the class-initialization trigger.

.field public static sink:Ljava/lang/Object;

.method private static nothing()V
    .locals 0
    return-void
.end method

.method public static voidCallWithNoEffect()V
    .locals 0
    invoke-static {}, Ldeadcode/Allocations;->nothing()V
    return-void
.end method

.method public static unusedInteger()Ljava/lang/Integer;
    .locals 3
    const/4 v0, 0x7
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v0
    new-instance v1, Ljava/lang/Integer;
    const/4 v2, 0x5
    invoke-direct {v1, v2}, Ljava/lang/Integer;-><init>(I)V
    return-object v0
.end method

.method public static unusedStrings()Ljava/lang/String;
    .locals 5
    const/16 v0, 0x2a
    invoke-static {v0}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;
    move-result-object v0
    new-instance v1, Ljava/lang/String;
    const/4 v2, 0x1
    new-array v2, v2, [C
    const/4 v3, 0x0
    const/16 v4, 0x61
    aput-char v4, v2, v3
    invoke-direct {v1, v2}, Ljava/lang/String;-><init>([C)V
    new-instance v1, Ljava/lang/String;
    const/4 v2, 0x2
    new-array v2, v2, [C
    fill-array-data v2, :chars
    invoke-direct {v1, v2}, Ljava/lang/String;-><init>([C)V
    return-object v0
    :chars
    .array-data 2
        0x31s
        0x30s
    .end array-data
.end method

.method public static droppedBuilderChain()Ljava/lang/String;
    .locals 4
    new-instance v0, Ljava/lang/StringBuilder;
    const-string v1, "k"
    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object v0
    new-instance v1, Ljava/lang/StringBuilder;
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V
    const-string v2, "x"
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    move-result-object v1
    const/4 v3, 0x1
    invoke-virtual {v1, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;
    move-result-object v1
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object v2
    return-object v0
.end method

.method private static newBuilder()Ljava/lang/StringBuilder;
    .locals 1
    new-instance v0, Ljava/lang/StringBuilder;
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V
    return-object v0
.end method

# The append changes a builder this method did not allocate.
.method public static appendedToAnotherBuilder()Ljava/lang/String;
    .locals 3
    invoke-static {}, Ldeadcode/Allocations;->newBuilder()Ljava/lang/StringBuilder;
    move-result-object v0
    new-instance v1, Ljava/lang/StringBuilder;
    const-string v2, "d"
    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/CharSequence;)Ljava/lang/StringBuilder;
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object v0
    return-object v0
.end method

.method public static storedToField()I
    .locals 1
    new-instance v0, Ljava/lang/Object;
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    sput-object v0, Ldeadcode/Allocations;->sink:Ljava/lang/Object;
    const/4 v0, 0x1
    return v0
.end method

.method public static storedToArray([Ljava/lang/Object;)V
    .locals 2
    new-instance v0, Ljava/lang/Object;
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    const/4 v1, 0x0
    aput-object v0, p0, v1
    return-void
.end method

.method public static passedToOpaqueCall()I
    .locals 1
    new-instance v0, Ljava/lang/Object;
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    invoke-static {v0}, Ljava/lang/System;->identityHashCode(Ljava/lang/Object;)I
    const/4 v0, 0x1
    return v0
.end method

.method public static comparedByReference(Ljava/lang/Object;)I
    .locals 1
    new-instance v0, Ljava/lang/Object;
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    if-eq v0, p0, :same
    const/4 v0, 0x1
    return v0
    :same
    const/4 v0, 0x0
    return v0
.end method

.method public static constructorWritesStatic()I
    .locals 2
    new-instance v0, Ldeadcode/StaticWriter;
    const/4 v1, 0x3
    invoke-direct {v0, v1}, Ldeadcode/StaticWriter;-><init>(I)V
    sget v0, Ldeadcode/DeadCode;->visible:I
    return v0
.end method

.method public static allocationTriggersInitializer()V
    .locals 1
    new-instance v0, Ldeadcode/NoisyInitializer;
    invoke-direct {v0}, Ldeadcode/NoisyInitializer;-><init>()V
    return-void
.end method
