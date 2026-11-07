.class public Lorg/cf/obfuscated/Reflection;
.super Ljava/lang/Object;
.source "Reflection.java"


# static fields
.field private static someField:Ljava/lang/String;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 9
    const-string v0, "this is some field, eh?"

    #@2
    sput-object v0, Lorg/cf/obfuscated/Reflection;->someField:Ljava/lang/String;

    #@4
    return-void
.end method

.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 7
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static main([Ljava/lang/String;)V
    .locals 0
    .param p0, "argv"    # [Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    #@0
    .prologue
    .line 59
    return-void
.end method

.method private static reflectSecretMethod()V
    .locals 8

    #@0
    .prologue
    .line 13
    const/16 v4, 0x9

    #@2
    :try_start_0
    invoke-static {v4}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    #@5
    move-result-object v4

    #@6
    invoke-static {v4}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    #@9
    move-result-object v1

    #@a
    .line 14
    .local v1, "klazz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const/4 v4, 0x7

    #@b
    invoke-static {v4}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    #@e
    move-result-object v4

    #@f
    const/4 v5, 0x3

    #@10
    new-array v5, v5, [Ljava/lang/Class;

    #@12
    const/4 v6, 0x0

    #@13
    const/16 v7, 0x8

    #@15
    invoke-static {v7}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    #@18
    move-result-object v7

    #@19
    invoke-static {v7}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    #@1c
    move-result-object v7

    #@1d
    aput-object v7, v5, v6

    #@1f
    const/4 v6, 0x1

    #@20
    const/16 v7, 0x8

    #@22
    invoke-static {v7}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    #@25
    move-result-object v7

    #@26
    invoke-static {v7}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    #@29
    move-result-object v7

    #@2a
    aput-object v7, v5, v6

    #@2c
    const/4 v6, 0x2

    #@2d
    const/16 v7, 0x8

    #@2f
    invoke-static {v7}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    #@32
    move-result-object v7

    #@33
    invoke-static {v7}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    #@36
    move-result-object v7

    #@37
    aput-object v7, v5, v6

    #@39
    invoke-virtual {v1, v4, v5}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@3c
    move-result-object v2

    #@3d
    .line 21
    .local v2, "m":Ljava/lang/reflect/Method;
    const/4 v4, 0x0

    #@3e
    const/4 v5, 0x3

    #@3f
    new-array v5, v5, [Ljava/lang/Object;

    #@41
    const/4 v6, 0x0

    #@42
    const/16 v7, 0x24

    #@44
    invoke-static {v7}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@47
    move-result-object v7

    #@48
    aput-object v7, v5, v6

    #@4a
    const/4 v6, 0x1

    #@4b
    const/16 v7, 0x1e

    #@4d
    invoke-static {v7}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@50
    move-result-object v7

    #@51
    aput-object v7, v5, v6

    #@53
    const/4 v6, 0x2

    #@54
    const v7, 0xfd9a

    #@57
    invoke-static {v7}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@5a
    move-result-object v7

    #@5b
    aput-object v7, v5, v6

    #@5d
    invoke-virtual {v2, v4, v5}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    #@60
    move-result-object v3

    #@61
    check-cast v3, Ljava/lang/Integer;

    #@63
    .line 22
    .local v3, "magic":Ljava/lang/Integer;
    sget-object v4, Ljava/lang/System;->out:Ljava/io/PrintStream;

    #@65
    new-instance v5, Ljava/lang/StringBuilder;

    #@67
    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    #@6a
    const-string v6, "magic answer = "

    #@6c
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@6f
    move-result-object v5

    #@70
    invoke-virtual {v5, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@73
    move-result-object v5

    #@74
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@77
    move-result-object v5

    #@78
    invoke-virtual {v4, v5}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@7b
    .line 26
    .end local v2    # "m":Ljava/lang/reflect/Method;
    .end local v3    # "magic":Ljava/lang/Integer;
    :goto_0
    return-void

    #@7c
    .line 23
    :catch_0
    move-exception v0

    #@7d
    .line 24
    .local v0, "e":Ljava/lang/Exception;
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V

    #@80
    goto :goto_0
.end method

.method private static reflectSecretMethod2()V
    .locals 5

    #@0
    .prologue
    .line 30
    const/16 v3, 0x9

    #@2
    :try_start_0
    invoke-static {v3}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    #@5
    move-result-object v3

    #@6
    invoke-static {v3}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    #@9
    move-result-object v1

    #@a
    .line 31
    .local v1, "klazz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const/16 v3, 0xa

    #@c
    invoke-static {v3}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    #@f
    move-result-object v3

    #@10
    const/4 v4, 0x0

    #@11
    new-array v4, v4, [Ljava/lang/Class;

    #@13
    invoke-virtual {v1, v3, v4}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@16
    move-result-object v2

    #@17
    .line 32
    .local v2, "m":Ljava/lang/reflect/Method;
    const/4 v3, 0x0

    #@18
    const/4 v4, 0x0

    #@19
    new-array v4, v4, [Ljava/lang/Object;

    #@1b
    invoke-virtual {v2, v3, v4}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@1e
    .line 36
    .end local v2    # "m":Ljava/lang/reflect/Method;
    :goto_0
    return-void

    #@1f
    .line 33
    :catch_0
    move-exception v0

    #@20
    .line 34
    .local v0, "e":Ljava/lang/Exception;
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V

    #@23
    goto :goto_0
.end method

.method private static reflectedFieldLookup()V
    .locals 4
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    #@0
    .prologue
    .line 47
    new-instance v1, Ljava/lang/String;

    #@2
    const/16 v3, 0x9

    #@4
    new-array v3, v3, [B

    #@6
    fill-array-data v3, :array_0

    #@9
    invoke-direct {v1, v3}, Ljava/lang/String;-><init>([B)V

    #@c
    .line 48
    .local v1, "fieldName":Ljava/lang/String;
    const-class v3, Lorg/cf/obfuscated/Reflection;

    #@e
    invoke-virtual {v3, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    #@11
    move-result-object v0

    #@12
    .line 49
    .local v0, "f":Ljava/lang/reflect/Field;
    const/4 v3, 0x0

    #@13
    invoke-virtual {v0, v3}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@16
    move-result-object v2

    #@17
    check-cast v2, Ljava/lang/String;

    #@19
    .line 50
    .local v2, "xorKey":Ljava/lang/String;
    sget-object v3, Ljava/lang/System;->out:Ljava/io/PrintStream;

    #@1b
    invoke-virtual {v3, v2}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    #@1e
    .line 51
    return-void

    #@1f
    .line 47
    nop

    #@20
    :array_0
    .array-data 1
        0x73t
        0x6ft
        0x6dt
        0x65t
        0x46t
        0x69t
        0x65t
        0x6ct
        0x64t
    .end array-data
.end method

.method private static secretMethod(Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;)I
    .locals 3
    .param p0, "x"    # Ljava/lang/Integer;
    .param p1, "y"    # Ljava/lang/Integer;
    .param p2, "z"    # Ljava/lang/Integer;

    #@0
    .prologue
    .line 39
    invoke-virtual {p0}, Ljava/lang/Integer;->intValue()I

    #@3
    move-result v0

    #@4
    invoke-virtual {p1}, Ljava/lang/Integer;->intValue()I

    #@7
    move-result v1

    #@8
    invoke-virtual {p2}, Ljava/lang/Integer;->intValue()I

    #@b
    move-result v2

    #@c
    invoke-static {v0, v1, v2}, Lorg/cf/obfuscated/MathCrypt;->decode(III)I

    #@f
    move-result v0

    #@10
    return v0
.end method

.method private static secretMethod2()V
    .locals 0

    #@0
    .prologue
    .line 43
    invoke-static {}, Ljava/lang/System;->gc()V

    #@3
    .line 44
    return-void
.end method
