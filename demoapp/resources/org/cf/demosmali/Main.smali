.class public Lorg/cf/demosmali/Main;
.super Ljava/lang/Object;
.source "Main.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 3
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static helloWorld()V
    .locals 5

    #@0
    .prologue
    .line 6
    const/16 v2, 0x10

    #@2
    new-array v0, v2, [B

    #@4
    fill-array-data v0, :array_0

    #@7
    .line 7
    .local v0, "data":[B
    new-instance v1, Ljava/lang/String;

    #@9
    invoke-static {v0}, Lorg/cf/demosmali/Base64;->decode([B)[B

    #@c
    move-result-object v2

    #@d
    invoke-direct {v1, v2}, Ljava/lang/String;-><init>([B)V

    #@10
    .line 9
    .local v1, "output":Ljava/lang/String;
    sget-object v2, Ljava/lang/System;->out:Ljava/io/PrintStream;

    #@12
    new-instance v3, Ljava/lang/StringBuilder;

    #@14
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@17
    const-string v4, "helloWorld(): "

    #@19
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v3

    #@1d
    invoke-virtual {v3, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@20
    move-result-object v3

    #@21
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@24
    move-result-object v3

    #@25
    invoke-virtual {v2, v3}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    #@28
    .line 10
    return-void

    #@29
    .line 6
    nop

    #@2a
    :array_0
    .array-data 1
        0x61t
        0x47t
        0x56t
        0x73t
        0x62t
        0x47t
        0x38t
        0x73t
        0x64t
        0x32t
        0x39t
        0x79t
        0x62t
        0x47t
        0x51t
        0x68t
    .end array-data
.end method

.method public static main([Ljava/lang/String;)V
    .locals 3
    .param p0, "args"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 13
    invoke-static {}, Lorg/cf/demosmali/Main;->helloWorld()V

    #@3
    .line 14
    invoke-static {}, Lorg/cf/demosmali/Main;->stringBuilder()V

    #@6
    .line 15
    const/4 v2, 0x3

    #@7
    new-array v0, v2, [C

    #@9
    fill-array-data v0, :array_0

    #@c
    .line 16
    .local v0, "carr":[C
    invoke-virtual {v0}, [C->clone()Ljava/lang/Object;

    #@f
    move-result-object v1

    #@10
    check-cast v1, [C

    #@12
    .line 17
    .local v1, "cloned":[C
    sget-object v2, Ljava/lang/System;->out:Ljava/io/PrintStream;

    #@14
    invoke-virtual {v2, v1}, Ljava/io/PrintStream;->println([C)V

    #@17
    .line 18
    return-void

    #@18
    .line 15
    :array_0
    .array-data 2
        0x61s
        0x62s
        0x63s
    .end array-data
.end method

.method private parameterLogic(I)I
    .locals 4
    .param p1, "param"    # I

    #@0
    .prologue
    .line 37
    const/4 v0, 0x5

    #@1
    .line 38
    .local v0, "x":I
    const/16 v1, 0xa

    #@3
    if-ne p1, v1, :cond_0

    #@5
    .line 39
    const/16 v0, 0xa

    #@7
    .line 42
    :cond_0
    sget-object v1, Ljava/lang/System;->out:Ljava/io/PrintStream;

    #@9
    new-instance v2, Ljava/lang/StringBuilder;

    #@b
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@e
    const-string v3, "parameterLogic(), x = "

    #@10
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@13
    move-result-object v2

    #@14
    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@17
    move-result-object v2

    #@18
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1b
    move-result-object v2

    #@1c
    invoke-virtual {v1, v2}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    #@1f
    .line 44
    return v0
.end method

.method private static printParameter(I)V
    .locals 3
    .param p0, "param"    # I

    #@0
    .prologue
    .line 32
    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;

    #@2
    new-instance v1, Ljava/lang/StringBuilder;

    #@4
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@7
    const-string v2, "printParameter(), param = "

    #@9
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@c
    move-result-object v1

    #@d
    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@10
    move-result-object v1

    #@11
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@14
    move-result-object v1

    #@15
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    #@18
    .line 33
    return-void
.end method

.method public static stringBuilder()V
    .locals 4

    #@0
    .prologue
    .line 21
    new-instance v0, Ljava/lang/StringBuilder;

    #@2
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    #@5
    .line 22
    .local v0, "sb":Ljava/lang/StringBuilder;
    const-string v1, "1"

    #@7
    invoke-static {v1}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    #@a
    move-result v1

    #@b
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@e
    .line 23
    const-string v1, "2"

    #@10
    invoke-static {v1}, Ljava/lang/Long;->parseLong(Ljava/lang/String;)J

    #@13
    move-result-wide v2

    #@14
    invoke-virtual {v0, v2, v3}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

    #@17
    .line 24
    const/16 v1, 0x33

    #@19
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@1c
    .line 25
    const-string v1, "abc"

    #@1e
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@21
    .line 27
    sget-object v1, Ljava/lang/System;->out:Ljava/io/PrintStream;

    #@23
    new-instance v2, Ljava/lang/StringBuilder;

    #@25
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@28
    const-string v3, "stringBuilder(): "

    #@2a
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2d
    move-result-object v2

    #@2e
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@31
    move-result-object v3

    #@32
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@35
    move-result-object v2

    #@36
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@39
    move-result-object v2

    #@3a
    invoke-virtual {v1, v2}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    #@3d
    .line 28
    return-void
.end method
