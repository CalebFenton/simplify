.class public Landroid/support/v4/util/DebugUtils;
.super Ljava/lang/Object;
.source "DebugUtils.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 25
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static buildShortClassTag(Ljava/lang/Object;Ljava/lang/StringBuilder;)V
    .locals 3
    .param p0, "cls"    # Ljava/lang/Object;
    .param p1, "out"    # Ljava/lang/StringBuilder;

    #@0
    .prologue
    .line 28
    if-nez p0, :cond_0

    #@2
    .line 29
    const-string v2, "null"

    #@4
    invoke-virtual {p1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@7
    .line 43
    :goto_0
    return-void

    #@8
    .line 31
    :cond_0
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@b
    move-result-object v2

    #@c
    invoke-virtual {v2}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    #@f
    move-result-object v1

    #@10
    .line 32
    .local v1, "simpleName":Ljava/lang/String;
    if-eqz v1, :cond_1

    #@12
    invoke-virtual {v1}, Ljava/lang/String;->length()I

    #@15
    move-result v2

    #@16
    if-gtz v2, :cond_2

    #@18
    .line 33
    :cond_1
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@1b
    move-result-object v2

    #@1c
    invoke-virtual {v2}, Ljava/lang/Class;->getName()Ljava/lang/String;

    #@1f
    move-result-object v1

    #@20
    .line 34
    const/16 v2, 0x2e

    #@22
    invoke-virtual {v1, v2}, Ljava/lang/String;->lastIndexOf(I)I

    #@25
    move-result v0

    #@26
    .line 35
    .local v0, "end":I
    if-lez v0, :cond_2

    #@28
    .line 36
    add-int/lit8 v2, v0, 0x1

    #@2a
    invoke-virtual {v1, v2}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    #@2d
    move-result-object v1

    #@2e
    .line 39
    .end local v0    # "end":I
    :cond_2
    invoke-virtual {p1, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@31
    .line 40
    const/16 v2, 0x7b

    #@33
    invoke-virtual {p1, v2}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@36
    .line 41
    invoke-static {p0}, Ljava/lang/System;->identityHashCode(Ljava/lang/Object;)I

    #@39
    move-result v2

    #@3a
    invoke-static {v2}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@3d
    move-result-object v2

    #@3e
    invoke-virtual {p1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@41
    goto :goto_0
.end method
