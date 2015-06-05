.class public Lorg/cf/obfuscated/Reflection;
.super Ljava/lang/Object;
.source "Reflection.java"


# static fields
.field private static someField:Ljava/lang/String;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    .line 9
    const-string v0, "this is some field, eh?"

    sput-object v0, Lorg/cf/obfuscated/Reflection;->someField:Ljava/lang/String;

    return-void
.end method

.method public constructor <init>()V
    .registers 1

    .prologue
    .line 7
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static main([Ljava/lang/String;)V
    .registers 1
    .param p0, "argv"    # [Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 59
    return-void
.end method

.method private static reflectSecretMethod()V
    .registers 8

    .prologue
    .line 13
    const/16 v4, 0x9

    :try_start_2
    invoke-static {v4}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    move-result-object v4

    invoke-static {v4}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v1

    .line 14
    .local v1, "klazz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const/4 v4, 0x7

    invoke-static {v4}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    move-result-object v4

    const/4 v5, 0x3

    new-array v5, v5, [Ljava/lang/Class;

    const/4 v6, 0x0

    const/16 v7, 0x8

    invoke-static {v7}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    move-result-object v7

    invoke-static {v7}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v7

    aput-object v7, v5, v6

    const/4 v6, 0x1

    const/16 v7, 0x8

    invoke-static {v7}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    move-result-object v7

    invoke-static {v7}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v7

    aput-object v7, v5, v6

    const/4 v6, 0x2

    const/16 v7, 0x8

    invoke-static {v7}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    move-result-object v7

    invoke-static {v7}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v7

    aput-object v7, v5, v6

    invoke-virtual {v1, v4, v5}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v2

    .line 21
    .local v2, "m":Ljava/lang/reflect/Method;
    const/4 v4, 0x0

    const/4 v5, 0x3

    new-array v5, v5, [Ljava/lang/Object;

    const/4 v6, 0x0

    const/16 v7, 0x24

    invoke-static {v7}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v7

    aput-object v7, v5, v6

    const/4 v6, 0x1

    const/16 v7, 0x1e

    invoke-static {v7}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v7

    aput-object v7, v5, v6

    const/4 v6, 0x2

    const v7, 0xfd9a

    invoke-static {v7}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v7

    aput-object v7, v5, v6

    invoke-virtual {v2, v4, v5}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v3

    check-cast v3, Ljava/lang/Integer;

    .line 22
    .local v3, "magic":Ljava/lang/Integer;
    sget-object v4, Ljava/lang/System;->out:Ljava/io/PrintStream;

    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "magic answer = "

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v4, v5}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
    :try_end_7b
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_7b} :catch_7c

    .line 26
    .end local v2    # "m":Ljava/lang/reflect/Method;
    .end local v3    # "magic":Ljava/lang/Integer;
    :goto_7b
    return-void

    .line 23
    :catch_7c
    move-exception v0

    .line 24
    .local v0, "e":Ljava/lang/Exception;
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V

    goto :goto_7b
.end method

.method private static reflectSecretMethod2()V
    .registers 5

    .prologue
    .line 30
    const/16 v3, 0x9

    :try_start_2
    invoke-static {v3}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    move-result-object v3

    invoke-static {v3}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v1

    .line 31
    .local v1, "klazz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const/16 v3, 0xa

    invoke-static {v3}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    move-result-object v3

    const/4 v4, 0x0

    new-array v4, v4, [Ljava/lang/Class;

    invoke-virtual {v1, v3, v4}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v2

    .line 32
    .local v2, "m":Ljava/lang/reflect/Method;
    const/4 v3, 0x0

    const/4 v4, 0x0

    new-array v4, v4, [Ljava/lang/Object;

    invoke-virtual {v2, v3, v4}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_1e
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_1e} :catch_1f

    .line 36
    .end local v2    # "m":Ljava/lang/reflect/Method;
    :goto_1e
    return-void

    .line 33
    :catch_1f
    move-exception v0

    .line 34
    .local v0, "e":Ljava/lang/Exception;
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V

    goto :goto_1e
.end method

.method private static reflectedFieldLookup()V
    .registers 4
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 47
    new-instance v1, Ljava/lang/String;

    const/16 v3, 0x9

    new-array v3, v3, [B

    fill-array-data v3, :array_20

    invoke-direct {v1, v3}, Ljava/lang/String;-><init>([B)V

    .line 48
    .local v1, "fieldName":Ljava/lang/String;
    const-class v3, Lorg/cf/obfuscated/Reflection;

    invoke-virtual {v3, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    move-result-object v0

    .line 49
    .local v0, "f":Ljava/lang/reflect/Field;
    const/4 v3, 0x0

    invoke-virtual {v0, v3}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/lang/String;

    .line 50
    .local v2, "xorKey":Ljava/lang/String;
    sget-object v3, Ljava/lang/System;->out:Ljava/io/PrintStream;

    invoke-virtual {v3, v2}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    .line 51
    return-void

    .line 47
    nop

    :array_20
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
    .registers 6
    .param p0, "x"    # Ljava/lang/Integer;
    .param p1, "y"    # Ljava/lang/Integer;
    .param p2, "z"    # Ljava/lang/Integer;

    .prologue
    .line 39
    invoke-virtual {p0}, Ljava/lang/Integer;->intValue()I

    move-result v0

    invoke-virtual {p1}, Ljava/lang/Integer;->intValue()I

    move-result v1

    invoke-virtual {p2}, Ljava/lang/Integer;->intValue()I

    move-result v2

    invoke-static {v0, v1, v2}, Lorg/cf/obfuscated/MathCrypt;->decode(III)I

    move-result v0

    return v0
.end method

.method private static secretMethod2()V
    .registers 0

    .prologue
    .line 43
    invoke-static {}, Ljava/lang/System;->gc()V

    .line 44
    return-void
.end method
