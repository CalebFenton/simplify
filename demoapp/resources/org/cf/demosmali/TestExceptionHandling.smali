.class public Lorg/cf/demosmali/TestExceptionHandling;
.super Ljava/lang/Object;
.source "TestExceptionHandling.java"


# static fields
.field private static ONE:I


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 7
    const/4 v0, 0x1

    #@1
    sput v0, Lorg/cf/demosmali/TestExceptionHandling;->ONE:I

    #@3
    return-void
.end method

.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 3
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static catchParentButNotGrandparentException()I
    .locals 3

    #@0
    .prologue
    .line 50
    const/4 v1, 0x1

    #@1
    .line 52
    .local v1, "value":I
    :try_start_0
    sget v2, Lorg/cf/demosmali/TestExceptionHandling;->ONE:I

    #@3
    if-lez v2, :cond_0

    #@5
    .line 53
    new-instance v2, Ljava/lang/NullPointerException;

    #@7
    invoke-direct {v2}, Ljava/lang/NullPointerException;-><init>()V

    #@a
    throw v2
    :try_end_0
    .catch Ljava/lang/RuntimeException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    #@b
    .line 57
    :catch_0
    move-exception v0

    #@c
    .line 58
    .local v0, "ex":Ljava/lang/RuntimeException;
    const/4 v1, 0x3

    #@d
    .line 63
    .end local v0    # "ex":Ljava/lang/RuntimeException;
    :goto_0
    return v1

    #@e
    .line 56
    :cond_0
    const/4 v1, 0x2

    #@f
    goto :goto_0

    #@10
    .line 59
    :catch_1
    move-exception v0

    #@11
    .line 60
    .local v0, "ex":Ljava/lang/Exception;
    const/4 v1, 0x4

    #@12
    goto :goto_0
.end method

.method public static catchParentException()I
    .locals 3

    #@0
    .prologue
    .line 35
    const/4 v1, 0x1

    #@1
    .line 37
    .local v1, "value":I
    :try_start_0
    sget v2, Lorg/cf/demosmali/TestExceptionHandling;->ONE:I

    #@3
    if-lez v2, :cond_0

    #@5
    .line 38
    new-instance v2, Ljava/lang/NullPointerException;

    #@7
    invoke-direct {v2}, Ljava/lang/NullPointerException;-><init>()V

    #@a
    throw v2
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@b
    .line 42
    :catch_0
    move-exception v0

    #@c
    .line 43
    .local v0, "ex":Ljava/lang/Exception;
    const/4 v1, 0x3

    #@d
    .line 46
    .end local v0    # "ex":Ljava/lang/Exception;
    :goto_0
    return v1

    #@e
    .line 41
    :cond_0
    const/4 v1, 0x2

    #@f
    goto :goto_0
.end method

.method public static finallyWithCatchForAnotherException()I
    .locals 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    #@0
    .prologue
    .line 99
    const/4 v1, 0x1

    #@1
    .line 101
    .local v1, "value":I
    :try_start_0
    sget v2, Lorg/cf/demosmali/TestExceptionHandling;->ONE:I

    #@3
    if-lez v2, :cond_0

    #@5
    .line 102
    new-instance v2, Ljava/lang/Exception;

    #@7
    invoke-direct {v2}, Ljava/lang/Exception;-><init>()V

    #@a
    throw v2
    :try_end_0
    .catch Ljava/lang/RuntimeException; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@b
    .line 106
    :catch_0
    move-exception v0

    #@c
    .line 107
    .local v0, "ex":Ljava/lang/RuntimeException;
    const/4 v1, 0x4

    #@d
    .line 109
    const/4 v1, 0x3

    #@e
    .line 112
    .end local v0    # "ex":Ljava/lang/RuntimeException;
    :goto_0
    return v1

    #@f
    .line 105
    :cond_0
    const/4 v1, 0x2

    #@10
    .line 109
    const/4 v1, 0x3

    #@11
    .line 110
    goto :goto_0

    #@12
    .line 109
    :catchall_0
    move-exception v2

    #@13
    const/4 v1, 0x3

    #@14
    throw v2
.end method

.method public static finallyWithHandledException()I
    .locals 3

    #@0
    .prologue
    .line 82
    const/4 v1, 0x1

    #@1
    .line 84
    .local v1, "value":I
    :try_start_0
    sget v2, Lorg/cf/demosmali/TestExceptionHandling;->ONE:I

    #@3
    if-lez v2, :cond_0

    #@5
    .line 85
    new-instance v2, Ljava/lang/Exception;

    #@7
    invoke-direct {v2}, Ljava/lang/Exception;-><init>()V

    #@a
    throw v2
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@b
    .line 89
    :catch_0
    move-exception v0

    #@c
    .line 90
    .local v0, "ex":Ljava/lang/Exception;
    const/4 v1, 0x4

    #@d
    .line 92
    const/4 v1, 0x3

    #@e
    .line 95
    .end local v0    # "ex":Ljava/lang/Exception;
    :goto_0
    return v1

    #@f
    .line 88
    :cond_0
    const/4 v1, 0x2

    #@10
    .line 92
    const/4 v1, 0x3

    #@11
    .line 93
    goto :goto_0

    #@12
    .line 92
    :catchall_0
    move-exception v2

    #@13
    const/4 v1, 0x3

    #@14
    throw v2
.end method

.method public static finallyWithUnhandledException()I
    .locals 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    #@0
    .prologue
    .line 67
    const/4 v0, 0x1

    #@1
    .line 69
    .local v0, "value":I
    :try_start_0
    sget v1, Lorg/cf/demosmali/TestExceptionHandling;->ONE:I

    #@3
    if-lez v1, :cond_0

    #@5
    .line 70
    new-instance v1, Ljava/lang/Exception;

    #@7
    invoke-direct {v1}, Ljava/lang/Exception;-><init>()V

    #@a
    throw v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@b
    .line 75
    :catchall_0
    move-exception v1

    #@c
    const/4 v0, 0x3

    #@d
    throw v1

    #@e
    .line 73
    :cond_0
    const/4 v0, 0x2

    #@f
    .line 75
    const/4 v0, 0x3

    #@10
    .line 78
    return v0
.end method

.method public static varargs main([Ljava/lang/String;)V
    .locals 3
    .param p0, "argv"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 10
    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;

    #@2
    new-instance v1, Ljava/lang/StringBuilder;

    #@4
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@7
    const-string v2, "val = "

    #@9
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@c
    move-result-object v1

    #@d
    invoke-static {}, Lorg/cf/demosmali/TestExceptionHandling;->catchParentButNotGrandparentException()I

    #@10
    move-result v2

    #@11
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@14
    move-result-object v1

    #@15
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@18
    move-result-object v1

    #@19
    invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    #@1c
    .line 11
    return-void
.end method

.method public static throwException()I
    .locals 3

    #@0
    .prologue
    .line 20
    const/4 v1, 0x1

    #@1
    .line 22
    .local v1, "value":I
    :try_start_0
    sget v2, Lorg/cf/demosmali/TestExceptionHandling;->ONE:I

    #@3
    if-lez v2, :cond_0

    #@5
    .line 23
    new-instance v2, Ljava/lang/Exception;

    #@7
    invoke-direct {v2}, Ljava/lang/Exception;-><init>()V

    #@a
    throw v2
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@b
    .line 27
    :catch_0
    move-exception v0

    #@c
    .line 28
    .local v0, "ex":Ljava/lang/Exception;
    const/4 v1, 0x2

    #@d
    .line 31
    .end local v0    # "ex":Ljava/lang/Exception;
    :goto_0
    return v1

    #@e
    .line 26
    :cond_0
    const/4 v1, 0x2

    #@f
    goto :goto_0
.end method


# virtual methods
.method public testCasting()V
    .locals 3

    #@0
    .prologue
    .line 14
    const/4 v2, 0x5

    #@1
    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@4
    move-result-object v0

    #@5
    .line 15
    .local v0, "o":Ljava/lang/Integer;
    check-cast v0, Ljava/lang/Integer;

    #@7
    .end local v0    # "o":Ljava/lang/Integer;
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    #@a
    move-result v1

    #@b
    .line 16
    .local v1, "x":I
    sget-object v2, Ljava/lang/System;->out:Ljava/io/PrintStream;

    #@d
    invoke-virtual {v2, v1}, Ljava/io/PrintStream;->println(I)V

    #@10
    .line 17
    return-void
.end method
