.class Landroid/support/v4/text/ICUCompatIcs;
.super Ljava/lang/Object;
.source "ICUCompatIcs.java"


# static fields
.field private static final TAG:Ljava/lang/String; = "ICUCompatIcs"

.field private static sAddLikelySubtagsMethod:Ljava/lang/reflect/Method;

.field private static sGetScriptMethod:Ljava/lang/reflect/Method;


# direct methods
.method static constructor <clinit>()V
    .locals 6

    #@0
    .prologue
    .line 33
    :try_start_0
    const-string v2, "libcore.icu.ICU"

    #@2
    invoke-static {v2}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    #@5
    move-result-object v0

    #@6
    .line 34
    .local v0, "clazz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    if-eqz v0, :cond_0

    #@8
    .line 35
    const-string v2, "getScript"

    #@a
    const/4 v3, 0x1

    #@b
    new-array v3, v3, [Ljava/lang/Class;

    #@d
    const/4 v4, 0x0

    #@e
    const-class v5, Ljava/lang/String;

    #@10
    aput-object v5, v3, v4

    #@12
    invoke-virtual {v0, v2, v3}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@15
    move-result-object v2

    #@16
    sput-object v2, Landroid/support/v4/text/ICUCompatIcs;->sGetScriptMethod:Ljava/lang/reflect/Method;

    #@18
    .line 37
    const-string v2, "addLikelySubtags"

    #@1a
    const/4 v3, 0x1

    #@1b
    new-array v3, v3, [Ljava/lang/Class;

    #@1d
    const/4 v4, 0x0

    #@1e
    const-class v5, Ljava/lang/String;

    #@20
    aput-object v5, v3, v4

    #@22
    invoke-virtual {v0, v2, v3}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@25
    move-result-object v2

    #@26
    sput-object v2, Landroid/support/v4/text/ICUCompatIcs;->sAddLikelySubtagsMethod:Ljava/lang/reflect/Method;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@28
    .line 44
    :cond_0
    :goto_0
    return-void

    #@29
    .line 40
    :catch_0
    move-exception v1

    #@2a
    .line 42
    .local v1, "e":Ljava/lang/Exception;
    const-string v2, "ICUCompatIcs"

    #@2c
    invoke-static {v2, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/Throwable;)I

    #@2f
    goto :goto_0
.end method

.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 24
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static addLikelySubtags(Ljava/lang/String;)Ljava/lang/String;
    .locals 4
    .param p0, "locale"    # Ljava/lang/String;

    #@0
    .prologue
    .line 65
    :try_start_0
    sget-object v2, Landroid/support/v4/text/ICUCompatIcs;->sAddLikelySubtagsMethod:Ljava/lang/reflect/Method;

    #@2
    if-eqz v2, :cond_0

    #@4
    .line 66
    const/4 v2, 0x1

    #@5
    new-array v0, v2, [Ljava/lang/Object;

    #@7
    const/4 v2, 0x0

    #@8
    aput-object p0, v0, v2

    #@a
    .line 67
    .local v0, "args":[Ljava/lang/Object;
    sget-object v2, Landroid/support/v4/text/ICUCompatIcs;->sAddLikelySubtagsMethod:Ljava/lang/reflect/Method;

    #@c
    const/4 v3, 0x0

    #@d
    invoke-virtual {v2, v3, v0}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    #@10
    move-result-object v2

    #@11
    check-cast v2, Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/IllegalAccessException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/lang/reflect/InvocationTargetException; {:try_start_0 .. :try_end_0} :catch_1

    #@13
    .line 77
    .end local v0    # "args":[Ljava/lang/Object;
    :goto_0
    return-object v2

    #@14
    .line 69
    :catch_0
    move-exception v1

    #@15
    .line 71
    .local v1, "e":Ljava/lang/IllegalAccessException;
    const-string v2, "ICUCompatIcs"

    #@17
    invoke-static {v2, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/Throwable;)I

    #@1a
    .end local v1    # "e":Ljava/lang/IllegalAccessException;
    :cond_0
    :goto_1
    move-object v2, p0

    #@1b
    .line 77
    goto :goto_0

    #@1c
    .line 73
    :catch_1
    move-exception v1

    #@1d
    .line 75
    .local v1, "e":Ljava/lang/reflect/InvocationTargetException;
    const-string v2, "ICUCompatIcs"

    #@1f
    invoke-static {v2, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/Throwable;)I

    #@22
    goto :goto_1
.end method

.method public static getScript(Ljava/lang/String;)Ljava/lang/String;
    .locals 5
    .param p0, "locale"    # Ljava/lang/String;

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 48
    :try_start_0
    sget-object v2, Landroid/support/v4/text/ICUCompatIcs;->sGetScriptMethod:Ljava/lang/reflect/Method;

    #@3
    if-eqz v2, :cond_0

    #@5
    .line 49
    const/4 v2, 0x1

    #@6
    new-array v0, v2, [Ljava/lang/Object;

    #@8
    const/4 v2, 0x0

    #@9
    aput-object p0, v0, v2

    #@b
    .line 50
    .local v0, "args":[Ljava/lang/Object;
    sget-object v2, Landroid/support/v4/text/ICUCompatIcs;->sGetScriptMethod:Ljava/lang/reflect/Method;

    #@d
    const/4 v4, 0x0

    #@e
    invoke-virtual {v2, v4, v0}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    #@11
    move-result-object v2

    #@12
    check-cast v2, Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/IllegalAccessException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/lang/reflect/InvocationTargetException; {:try_start_0 .. :try_end_0} :catch_1

    #@14
    .line 60
    .end local v0    # "args":[Ljava/lang/Object;
    :goto_0
    return-object v2

    #@15
    .line 52
    :catch_0
    move-exception v1

    #@16
    .line 54
    .local v1, "e":Ljava/lang/IllegalAccessException;
    const-string v2, "ICUCompatIcs"

    #@18
    invoke-static {v2, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/Throwable;)I

    #@1b
    .end local v1    # "e":Ljava/lang/IllegalAccessException;
    :cond_0
    :goto_1
    move-object v2, v3

    #@1c
    .line 60
    goto :goto_0

    #@1d
    .line 56
    :catch_1
    move-exception v1

    #@1e
    .line 58
    .local v1, "e":Ljava/lang/reflect/InvocationTargetException;
    const-string v2, "ICUCompatIcs"

    #@20
    invoke-static {v2, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/Throwable;)I

    #@23
    goto :goto_1
.end method
