.class public Landroid/util/DebugUtils;
.super Ljava/lang/Object;
.source "DebugUtils.java"


# direct methods
.method public constructor <init>()V
    .registers 1

    .prologue
    .line 26
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static buildShortClassTag(Ljava/lang/Object;Ljava/lang/StringBuilder;)V
    .registers 5
    .param p0, "cls"    # Ljava/lang/Object;
    .param p1, "out"    # Ljava/lang/StringBuilder;

    .prologue
    .line 108
    if-nez p0, :cond_9

    .line 109
    const-string/jumbo v2, "null"

    invoke-virtual {p1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 123
    :goto_8
    return-void

    .line 111
    :cond_9
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v1

    .line 112
    .local v1, "simpleName":Ljava/lang/String;
    if-eqz v1, :cond_19

    invoke-virtual {v1}, Ljava/lang/String;->isEmpty()Z

    move-result v2

    if-eqz v2, :cond_2f

    .line 113
    :cond_19
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/Class;->getName()Ljava/lang/String;

    move-result-object v1

    .line 114
    const/16 v2, 0x2e

    invoke-virtual {v1, v2}, Ljava/lang/String;->lastIndexOf(I)I

    move-result v0

    .line 115
    .local v0, "end":I
    if-lez v0, :cond_2f

    .line 116
    add-int/lit8 v2, v0, 0x1

    invoke-virtual {v1, v2}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v1

    .line 119
    .end local v0    # "end":I
    :cond_2f
    invoke-virtual {p1, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 120
    const/16 v2, 0x7b

    invoke-virtual {p1, v2}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    .line 121
    invoke-static {p0}, Ljava/lang/System;->identityHashCode(Ljava/lang/Object;)I

    move-result v2

    invoke-static {v2}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    move-result-object v2

    invoke-virtual {p1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    goto :goto_8
.end method

.method public static isObjectSelected(Ljava/lang/Object;)Z
    .registers 15
    .param p0, "object"    # Ljava/lang/Object;

    .prologue
    const/4 v11, 0x0

    .line 66
    const/4 v4, 0x0

    .line 67
    .local v4, "match":Z
    const-string v10, "ANDROID_OBJECT_FILTER"

    invoke-static {v10}, Ljava/lang/System;->getenv(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v7

    .line 68
    .local v7, "s":Ljava/lang/String;
    if-eqz v7, :cond_a0

    invoke-virtual {v7}, Ljava/lang/String;->length()I

    move-result v10

    if-lez v10, :cond_a0

    .line 69
    const-string v10, "@"

    invoke-virtual {v7, v10}, Ljava/lang/String;->split(Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v8

    .line 71
    .local v8, "selectors":[Ljava/lang/String;
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v10

    invoke-virtual {v10}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v10

    aget-object v11, v8, v11

    invoke-virtual {v10, v11}, Ljava/lang/String;->matches(Ljava/lang/String;)Z

    move-result v10

    if-eqz v10, :cond_a0

    .line 73
    const/4 v2, 0x1

    .local v2, "i":I
    :goto_27
    array-length v10, v8

    if-ge v2, v10, :cond_a0

    .line 74
    aget-object v10, v8, v2

    const-string v11, "="

    invoke-virtual {v10, v11}, Ljava/lang/String;->split(Ljava/lang/String;)[Ljava/lang/String;

    move-result-object v5

    .line 75
    .local v5, "pair":[Ljava/lang/String;
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v3

    .line 77
    .local v3, "klass":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    const/4 v0, 0x0

    .line 78
    .local v0, "declaredMethod":Ljava/lang/reflect/Method;
    move-object v6, v3

    .line 80
    .local v6, "parent":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    :cond_38
    :try_start_38
    new-instance v10, Ljava/lang/StringBuilder;

    invoke-direct {v10}, Ljava/lang/StringBuilder;-><init>()V

    const-string v11, "get"

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v10

    const/4 v11, 0x0

    aget-object v11, v5, v11

    const/4 v12, 0x0

    const/4 v13, 0x1

    invoke-virtual {v11, v12, v13}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v11

    invoke-virtual {v11}, Ljava/lang/String;->toUpperCase()Ljava/lang/String;

    move-result-object v11

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v10

    const/4 v11, 0x0

    aget-object v11, v5, v11

    const/4 v12, 0x1

    invoke-virtual {v11, v12}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v11

    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v10

    invoke-virtual {v10}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v11

    const/4 v10, 0x0

    check-cast v10, [Ljava/lang/Class;

    invoke-virtual {v6, v11, v10}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v0

    .line 85
    invoke-virtual {v3}, Ljava/lang/Class;->getSuperclass()Ljava/lang/Class;

    move-result-object v6

    if-eqz v6, :cond_73

    if-eqz v0, :cond_38

    .line 87
    :cond_73
    if-eqz v0, :cond_8a

    .line 88
    const/4 v10, 0x0

    check-cast v10, [Ljava/lang/Object;

    invoke-virtual {v0, p0, v10}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v9

    .line 90
    .local v9, "value":Ljava/lang/Object;
    if-eqz v9, :cond_8d

    invoke-virtual {v9}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v10

    :goto_82
    const/4 v11, 0x1

    aget-object v11, v5, v11

    invoke-virtual {v10, v11}, Ljava/lang/String;->matches(Ljava/lang/String;)Z

    move-result v10

    or-int/2addr v4, v10

    .line 73
    .end local v9    # "value":Ljava/lang/Object;
    :cond_8a
    :goto_8a
    add-int/lit8 v2, v2, 0x1

    goto :goto_27

    .line 90
    .restart local v9    # "value":Ljava/lang/Object;
    :cond_8d
    const-string/jumbo v10, "null"
    :try_end_90
    .catch Ljava/lang/NoSuchMethodException; {:try_start_38 .. :try_end_90} :catch_91
    .catch Ljava/lang/IllegalAccessException; {:try_start_38 .. :try_end_90} :catch_96
    .catch Ljava/lang/reflect/InvocationTargetException; {:try_start_38 .. :try_end_90} :catch_9b

    goto :goto_82

    .line 93
    .end local v9    # "value":Ljava/lang/Object;
    :catch_91
    move-exception v1

    .line 94
    .local v1, "e":Ljava/lang/NoSuchMethodException;
    invoke-virtual {v1}, Ljava/lang/NoSuchMethodException;->printStackTrace()V

    goto :goto_8a

    .line 95
    .end local v1    # "e":Ljava/lang/NoSuchMethodException;
    :catch_96
    move-exception v1

    .line 96
    .local v1, "e":Ljava/lang/IllegalAccessException;
    invoke-virtual {v1}, Ljava/lang/IllegalAccessException;->printStackTrace()V

    goto :goto_8a

    .line 97
    .end local v1    # "e":Ljava/lang/IllegalAccessException;
    :catch_9b
    move-exception v1

    .line 98
    .local v1, "e":Ljava/lang/reflect/InvocationTargetException;
    invoke-virtual {v1}, Ljava/lang/reflect/InvocationTargetException;->printStackTrace()V

    goto :goto_8a

    .line 103
    .end local v0    # "declaredMethod":Ljava/lang/reflect/Method;
    .end local v1    # "e":Ljava/lang/reflect/InvocationTargetException;
    .end local v2    # "i":I
    .end local v3    # "klass":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .end local v5    # "pair":[Ljava/lang/String;
    .end local v6    # "parent":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .end local v8    # "selectors":[Ljava/lang/String;
    :cond_a0
    return v4
.end method
