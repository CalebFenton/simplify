.class public Lorg/cf/obfuscated/Reflection;
.super Ljava/lang/Object;
.source "Reflection.java"


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 6
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static main([Ljava/lang/String;)V
    .registers 1
    .param p0, "argv"    # [Ljava/lang/String;

    .prologue
    .line 48
    return-void
.end method

.method private static reflectSecretMethod()V
    .registers 8

    .prologue
    .line 10
    const/16 v4, 0x9

    :try_start_2
    invoke-static {v4}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    move-result-object v4

    invoke-static {v4}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v1

    .line 11
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

    .line 18
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

    .line 19
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

    .line 23
    .end local v2    # "m":Ljava/lang/reflect/Method;
    .end local v3    # "magic":Ljava/lang/Integer;
    :goto_7b
    return-void

    .line 20
    :catch_7c
    move-exception v0

    .line 21
    .local v0, "e":Ljava/lang/Exception;
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V

    goto :goto_7b
.end method

.method private static reflectSecretMethod2()V
    .registers 5

    .prologue
    .line 27
    const/16 v3, 0x9

    :try_start_2
    invoke-static {v3}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    move-result-object v3

    invoke-static {v3}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v1

    .line 28
    .local v1, "klazz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const/16 v3, 0xa

    invoke-static {v3}, Lorg/cf/obfuscated/StringHolder;->get(I)Ljava/lang/String;

    move-result-object v3

    const/4 v4, 0x0

    new-array v4, v4, [Ljava/lang/Class;

    invoke-virtual {v1, v3, v4}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v2

    .line 29
    .local v2, "m":Ljava/lang/reflect/Method;
    const/4 v3, 0x0

    const/4 v4, 0x0

    new-array v4, v4, [Ljava/lang/Object;

    invoke-virtual {v2, v3, v4}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_1e
    .catch Ljava/lang/Exception; {:try_start_2 .. :try_end_1e} :catch_1f

    .line 33
    .end local v2    # "m":Ljava/lang/reflect/Method;
    :goto_1e
    return-void

    .line 30
    :catch_1f
    move-exception v0

    .line 31
    .local v0, "e":Ljava/lang/Exception;
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V

    goto :goto_1e
.end method

.method private static secretMethod(Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Integer;)I
    .registers 6
    .param p0, "x"    # Ljava/lang/Integer;
    .param p1, "y"    # Ljava/lang/Integer;
    .param p2, "z"    # Ljava/lang/Integer;

    .prologue
    .line 36
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
    .line 40
    invoke-static {}, Ljava/lang/System;->gc()V

    .line 41
    return-void
.end method
