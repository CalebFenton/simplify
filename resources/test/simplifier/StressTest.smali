.class public Lsimplify/StressTest;
.super Ljava/lang/Object;
.source "StressTest.java"


# static fields
.field private static stringMap:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map",
            "<",
            "Ljava/lang/Integer;",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method static constructor <clinit>()V
    .locals 4

    .prologue
    .line 10
    new-instance v0, Ljava/util/HashMap;

    move-object v3, v0

    move-object v0, v3

    move-object v1, v3

    invoke-direct {v1}, Ljava/util/HashMap;-><init>()V

    sput-object v0, Lsimplify/StressTest;->stringMap:Ljava/util/Map;

    .line 11
    sget-object v0, Lsimplify/StressTest;->stringMap:Ljava/util/Map;

    const/4 v1, 0x0

    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    const-string v2, "map value 0"

    invoke-interface {v0, v1, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    .line 12
    sget-object v0, Lsimplify/StressTest;->stringMap:Ljava/util/Map;

    const/4 v1, 0x1

    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    const-string v2, "map value 1"

    invoke-interface {v0, v1, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    .line 13
    sget-object v0, Lsimplify/StressTest;->stringMap:Ljava/util/Map;

    const/4 v1, 0x2

    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v1

    const-string v2, "map value 2"

    invoke-interface {v0, v1, v2}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    .line 14
    return-void
.end method

.method public constructor <init>()V
    .locals 2

    .prologue
    .line 16
    move-object v0, p0

    move-object v1, v0

    invoke-direct {v1}, Ljava/lang/Object;-><init>()V

    .line 18
    return-void
.end method

.method public static doTest()V
    .locals 3

    .prologue
    .line 21
    const/4 v1, 0x0

    move v0, v1

    :goto_0
    move v1, v0

    const/4 v2, 0x5

    if-ge v1, v2, :cond_0

    .line 22
    sget-object v1, Ljava/lang/System;->out:Ljava/io/PrintStream;

    move v2, v0

    invoke-static {v2}, Lsimplify/StressTest;->getMap(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    .line 21
    add-int/lit8 v0, v0, 0x1

    goto :goto_0

    .line 25
    :cond_0
    sget-object v1, Ljava/lang/System;->out:Ljava/io/PrintStream;

    const-string v2, "!1 skrow ti"

    invoke-static {v2}, Lsimplify/StressTest;->staticDecrypt(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    .line 27
    sget-object v1, Ljava/lang/System;->out:Ljava/io/PrintStream;

    const-string v2, "hs\u001fvnqjr\u001f1 "

    invoke-static {v2}, Lsimplify/StressTest;->staticDecrypt2(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    .line 28
    return-void
.end method

.method public static getMap(I)Ljava/lang/String;
    .locals 4

    .prologue
    .line 31
    move v0, p0

    move v2, v0

    const/4 v3, 0x3

    rem-int/lit8 v2, v2, 0x3

    move v1, v2

    .line 33
    sget-object v2, Lsimplify/StressTest;->stringMap:Ljava/util/Map;

    move v3, v1

    invoke-static {v3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    invoke-interface {v2, v3}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v2

    check-cast v2, Ljava/lang/String;

    move-object v0, v2

    return-object v0
.end method

.method public static staticDecrypt(Ljava/lang/String;)Ljava/lang/String;
    .locals 8

    .prologue
    .line 37
    move-object v0, p0

    new-instance v4, Ljava/lang/StringBuilder;

    move-object v7, v4

    move-object v4, v7

    move-object v5, v7

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    move-object v1, v4

    .line 38
    new-instance v4, Ljava/lang/StringBuilder;

    move-object v7, v4

    move-object v4, v7

    move-object v5, v7

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    move-object v2, v4

    .line 40
    move-object v4, v0

    invoke-virtual {v4}, Ljava/lang/String;->length()I

    move-result v4

    const/4 v5, 0x1

    add-int/lit8 v4, v4, -0x1

    move v3, v4

    :goto_0
    move v4, v3

    if-ltz v4, :cond_0

    .line 41
    move-object v4, v1

    move-object v5, v0

    move v6, v3

    invoke-virtual {v5, v6}, Ljava/lang/String;->charAt(I)C

    move-result v5

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    move-result-object v4

    .line 42
    move-object v4, v2

    move v5, v3

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v4

    .line 40
    add-int/lit8 v3, v3, -0x1

    goto :goto_0

    .line 45
    :cond_0
    sget-object v4, Ljava/lang/System;->out:Ljava/io/PrintStream;

    new-instance v5, Ljava/lang/StringBuilder;

    move-object v7, v5

    move-object v5, v7

    move-object v6, v7

    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "some numbers: "

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    move-object v6, v2

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v6

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-virtual {v4, v5}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    .line 47
    move-object v4, v1

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    move-object v0, v4

    return-object v0
.end method

.method public static staticDecrypt2(Ljava/lang/String;)Ljava/lang/String;
    .locals 8

    .prologue
    .line 51
    move-object v0, p0

    new-instance v3, Ljava/lang/StringBuilder;

    move-object v7, v3

    move-object v3, v7

    move-object v4, v7

    move-object v5, v0

    invoke-direct {v4, v5}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    move-object v1, v3

    .line 53
    const/4 v3, 0x0

    move v2, v3

    :goto_0
    move v3, v2

    move-object v4, v0

    invoke-virtual {v4}, Ljava/lang/String;->length()I

    move-result v4

    if-ge v3, v4, :cond_0

    .line 54
    move-object v3, v1

    move v4, v2

    move-object v5, v1

    move v6, v2

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->charAt(I)C

    move-result v5

    const/4 v6, 0x1

    add-int/lit8 v5, v5, 0x1

    int-to-char v5, v5

    invoke-virtual {v3, v4, v5}, Ljava/lang/StringBuilder;->setCharAt(IC)V

    .line 53
    add-int/lit8 v2, v2, 0x1

    goto :goto_0

    .line 57
    :cond_0
    move-object v3, v1

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    move-object v0, v3

    return-object v0
.end method
