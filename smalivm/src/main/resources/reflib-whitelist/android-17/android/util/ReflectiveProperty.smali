.class Landroid/util/ReflectiveProperty;
.super Landroid/util/Property;
.source "ReflectiveProperty.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<T:",
        "Ljava/lang/Object;",
        "V:",
        "Ljava/lang/Object;",
        ">",
        "Landroid/util/Property",
        "<TT;TV;>;"
    }
.end annotation


# static fields
.field private static final PREFIX_GET:Ljava/lang/String; = "get"

.field private static final PREFIX_IS:Ljava/lang/String; = "is"

.field private static final PREFIX_SET:Ljava/lang/String; = "set"


# instance fields
.field private mField:Ljava/lang/reflect/Field;

.field private mGetter:Ljava/lang/reflect/Method;

.field private mSetter:Ljava/lang/reflect/Method;


# direct methods
.method public constructor <init>(Ljava/lang/Class;Ljava/lang/Class;Ljava/lang/String;)V
    .registers 19
    .param p3, "name"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Class",
            "<TT;>;",
            "Ljava/lang/Class",
            "<TV;>;",
            "Ljava/lang/String;",
            ")V"
        }
    .end annotation

    .prologue
    .line 47
    .local p0, "this":Landroid/util/ReflectiveProperty;, "Landroid/util/ReflectiveProperty<TT;TV;>;"
    .local p1, "propertyHolder":Ljava/lang/Class;, "Ljava/lang/Class<TT;>;"
    .local p2, "valueType":Ljava/lang/Class;, "Ljava/lang/Class<TV;>;"
    move-object/from16 v0, p2

    move-object/from16 v1, p3

    invoke-direct {p0, v0, v1}, Landroid/util/Property;-><init>(Ljava/lang/Class;Ljava/lang/String;)V

    .line 48
    const/4 v12, 0x0

    move-object/from16 v0, p3

    invoke-virtual {v0, v12}, Ljava/lang/String;->charAt(I)C

    move-result v12

    invoke-static {v12}, Ljava/lang/Character;->toUpperCase(C)C

    move-result v7

    .line 49
    .local v7, "firstLetter":C
    const/4 v12, 0x1

    move-object/from16 v0, p3

    invoke-virtual {v0, v12}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    move-result-object v11

    .line 50
    .local v11, "theRest":Ljava/lang/String;
    new-instance v12, Ljava/lang/StringBuilder;

    invoke-direct {v12}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v12, v7}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    move-result-object v12

    invoke-virtual {v12, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v12

    invoke-virtual {v12}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    .line 51
    .local v2, "capitalizedName":Ljava/lang/String;
    new-instance v12, Ljava/lang/StringBuilder;

    invoke-direct {v12}, Ljava/lang/StringBuilder;-><init>()V

    const-string v13, "get"

    invoke-virtual {v12, v13}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v12

    invoke-virtual {v12, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v12

    invoke-virtual {v12}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v8

    .line 53
    .local v8, "getterName":Ljava/lang/String;
    const/4 v12, 0x0

    :try_start_3e
    check-cast v12, [Ljava/lang/Class;

    move-object/from16 v0, p1

    invoke-virtual {v0, v8, v12}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v12

    iput-object v12, p0, Landroid/util/ReflectiveProperty;->mGetter:Ljava/lang/reflect/Method;
    :try_end_48
    .catch Ljava/lang/NoSuchMethodException; {:try_start_3e .. :try_end_48} :catch_87

    .line 76
    :goto_48
    iget-object v12, p0, Landroid/util/ReflectiveProperty;->mGetter:Ljava/lang/reflect/Method;

    invoke-virtual {v12}, Ljava/lang/reflect/Method;->getReturnType()Ljava/lang/Class;

    move-result-object v9

    .line 78
    .local v9, "getterType":Ljava/lang/Class;
    move-object/from16 v0, p2

    invoke-direct {p0, v0, v9}, Landroid/util/ReflectiveProperty;->typesMatch(Ljava/lang/Class;Ljava/lang/Class;)Z

    move-result v12

    if-nez v12, :cond_10d

    .line 79
    new-instance v12, Landroid/util/NoSuchPropertyException;

    new-instance v13, Ljava/lang/StringBuilder;

    invoke-direct {v13}, Ljava/lang/StringBuilder;-><init>()V

    const-string v14, "Underlying type ("

    invoke-virtual {v13, v14}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v13

    invoke-virtual {v13, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v13

    const-string v14, ") "

    invoke-virtual {v13, v14}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v13

    const-string v14, "does not match Property type ("

    invoke-virtual {v13, v14}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v13

    move-object/from16 v0, p2

    invoke-virtual {v13, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v13

    const-string v14, ")"

    invoke-virtual {v13, v14}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v13

    invoke-virtual {v13}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v13

    invoke-direct {v12, v13}, Landroid/util/NoSuchPropertyException;-><init>(Ljava/lang/String;)V

    throw v12

    .line 54
    .end local v9    # "getterType":Ljava/lang/Class;
    :catch_87
    move-exception v3

    .line 56
    .local v3, "e":Ljava/lang/NoSuchMethodException;
    new-instance v12, Ljava/lang/StringBuilder;

    invoke-direct {v12}, Ljava/lang/StringBuilder;-><init>()V

    const-string v13, "is"

    invoke-virtual {v12, v13}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v12

    invoke-virtual {v12, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v12

    invoke-virtual {v12}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v8

    .line 58
    const/4 v12, 0x0

    :try_start_9c
    check-cast v12, [Ljava/lang/Class;

    move-object/from16 v0, p1

    invoke-virtual {v0, v8, v12}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v12

    iput-object v12, p0, Landroid/util/ReflectiveProperty;->mGetter:Ljava/lang/reflect/Method;
    :try_end_a6
    .catch Ljava/lang/NoSuchMethodException; {:try_start_9c .. :try_end_a6} :catch_a7

    goto :goto_48

    .line 59
    :catch_a7
    move-exception v4

    .line 62
    .local v4, "e1":Ljava/lang/NoSuchMethodException;
    :try_start_a8
    move-object/from16 v0, p1

    move-object/from16 v1, p3

    invoke-virtual {v0, v1}, Ljava/lang/Class;->getField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    move-result-object v12

    iput-object v12, p0, Landroid/util/ReflectiveProperty;->mField:Ljava/lang/reflect/Field;

    .line 63
    iget-object v12, p0, Landroid/util/ReflectiveProperty;->mField:Ljava/lang/reflect/Field;

    invoke-virtual {v12}, Ljava/lang/reflect/Field;->getType()Ljava/lang/Class;

    move-result-object v6

    .line 64
    .local v6, "fieldType":Ljava/lang/Class;
    move-object/from16 v0, p2

    invoke-direct {p0, v0, v6}, Landroid/util/ReflectiveProperty;->typesMatch(Ljava/lang/Class;Ljava/lang/Class;)Z

    move-result v12

    if-nez v12, :cond_12f

    .line 65
    new-instance v12, Landroid/util/NoSuchPropertyException;

    new-instance v13, Ljava/lang/StringBuilder;

    invoke-direct {v13}, Ljava/lang/StringBuilder;-><init>()V

    const-string v14, "Underlying type ("

    invoke-virtual {v13, v14}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v13

    invoke-virtual {v13, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v13

    const-string v14, ") "

    invoke-virtual {v13, v14}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v13

    const-string v14, "does not match Property type ("

    invoke-virtual {v13, v14}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v13

    move-object/from16 v0, p2

    invoke-virtual {v13, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v13

    const-string v14, ")"

    invoke-virtual {v13, v14}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v13

    invoke-virtual {v13}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v13

    invoke-direct {v12, v13}, Landroid/util/NoSuchPropertyException;-><init>(Ljava/lang/String;)V

    throw v12
    :try_end_f1
    .catch Ljava/lang/NoSuchFieldException; {:try_start_a8 .. :try_end_f1} :catch_f1

    .line 69
    .end local v6    # "fieldType":Ljava/lang/Class;
    :catch_f1
    move-exception v5

    .line 71
    .local v5, "e2":Ljava/lang/NoSuchFieldException;
    new-instance v12, Landroid/util/NoSuchPropertyException;

    new-instance v13, Ljava/lang/StringBuilder;

    invoke-direct {v13}, Ljava/lang/StringBuilder;-><init>()V

    const-string v14, "No accessor method or field found for property with name "

    invoke-virtual {v13, v14}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v13

    move-object/from16 v0, p3

    invoke-virtual {v13, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v13

    invoke-virtual {v13}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v13

    invoke-direct {v12, v13}, Landroid/util/NoSuchPropertyException;-><init>(Ljava/lang/String;)V

    throw v12

    .line 82
    .end local v3    # "e":Ljava/lang/NoSuchMethodException;
    .end local v4    # "e1":Ljava/lang/NoSuchMethodException;
    .end local v5    # "e2":Ljava/lang/NoSuchFieldException;
    .restart local v9    # "getterType":Ljava/lang/Class;
    :cond_10d
    new-instance v12, Ljava/lang/StringBuilder;

    invoke-direct {v12}, Ljava/lang/StringBuilder;-><init>()V

    const-string/jumbo v13, "set"

    invoke-virtual {v12, v13}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v12

    invoke-virtual {v12, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v12

    invoke-virtual {v12}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v10

    .line 84
    .local v10, "setterName":Ljava/lang/String;
    const/4 v12, 0x1

    :try_start_122
    new-array v12, v12, [Ljava/lang/Class;

    const/4 v13, 0x0

    aput-object v9, v12, v13

    move-object/from16 v0, p1

    invoke-virtual {v0, v10, v12}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v12

    iput-object v12, p0, Landroid/util/ReflectiveProperty;->mSetter:Ljava/lang/reflect/Method;
    :try_end_12f
    .catch Ljava/lang/NoSuchMethodException; {:try_start_122 .. :try_end_12f} :catch_130

    .line 88
    .end local v9    # "getterType":Ljava/lang/Class;
    .end local v10    # "setterName":Ljava/lang/String;
    :cond_12f
    :goto_12f
    return-void

    .line 85
    .restart local v9    # "getterType":Ljava/lang/Class;
    .restart local v10    # "setterName":Ljava/lang/String;
    :catch_130
    move-exception v12

    goto :goto_12f
.end method

.method private typesMatch(Ljava/lang/Class;Ljava/lang/Class;)Z
    .registers 6
    .param p2, "getterType"    # Ljava/lang/Class;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Class",
            "<TV;>;",
            "Ljava/lang/Class;",
            ")Z"
        }
    .end annotation

    .prologue
    .local p0, "this":Landroid/util/ReflectiveProperty;, "Landroid/util/ReflectiveProperty<TT;TV;>;"
    .local p1, "valueType":Ljava/lang/Class;, "Ljava/lang/Class<TV;>;"
    const/4 v1, 0x1

    const/4 v0, 0x0

    .line 98
    if-eq p2, p1, :cond_4c

    .line 99
    invoke-virtual {p2}, Ljava/lang/Class;->isPrimitive()Z

    move-result v2

    if-eqz v2, :cond_4b

    .line 100
    sget-object v2, Ljava/lang/Float;->TYPE:Ljava/lang/Class;

    if-ne p2, v2, :cond_12

    const-class v2, Ljava/lang/Float;

    if-eq p1, v2, :cond_4a

    :cond_12
    sget-object v2, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;

    if-ne p2, v2, :cond_1a

    const-class v2, Ljava/lang/Integer;

    if-eq p1, v2, :cond_4a

    :cond_1a
    sget-object v2, Ljava/lang/Boolean;->TYPE:Ljava/lang/Class;

    if-ne p2, v2, :cond_22

    const-class v2, Ljava/lang/Boolean;

    if-eq p1, v2, :cond_4a

    :cond_22
    sget-object v2, Ljava/lang/Long;->TYPE:Ljava/lang/Class;

    if-ne p2, v2, :cond_2a

    const-class v2, Ljava/lang/Long;

    if-eq p1, v2, :cond_4a

    :cond_2a
    sget-object v2, Ljava/lang/Double;->TYPE:Ljava/lang/Class;

    if-ne p2, v2, :cond_32

    const-class v2, Ljava/lang/Double;

    if-eq p1, v2, :cond_4a

    :cond_32
    sget-object v2, Ljava/lang/Short;->TYPE:Ljava/lang/Class;

    if-ne p2, v2, :cond_3a

    const-class v2, Ljava/lang/Short;

    if-eq p1, v2, :cond_4a

    :cond_3a
    sget-object v2, Ljava/lang/Byte;->TYPE:Ljava/lang/Class;

    if-ne p2, v2, :cond_42

    const-class v2, Ljava/lang/Byte;

    if-eq p1, v2, :cond_4a

    :cond_42
    sget-object v2, Ljava/lang/Character;->TYPE:Ljava/lang/Class;

    if-ne p2, v2, :cond_4b

    const-class v2, Ljava/lang/Character;

    if-ne p1, v2, :cond_4b

    :cond_4a
    move v0, v1

    .line 111
    :cond_4b
    :goto_4b
    return v0

    :cond_4c
    move v0, v1

    goto :goto_4b
.end method


# virtual methods
.method public get(Ljava/lang/Object;)Ljava/lang/Object;
    .registers 5
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TT;)TV;"
        }
    .end annotation

    .prologue
    .line 137
    .local p0, "this":Landroid/util/ReflectiveProperty;, "Landroid/util/ReflectiveProperty<TT;TV;>;"
    .local p1, "object":Ljava/lang/Object;, "TT;"
    iget-object v1, p0, Landroid/util/ReflectiveProperty;->mGetter:Ljava/lang/reflect/Method;

    if-eqz v1, :cond_20

    .line 139
    :try_start_4
    iget-object v2, p0, Landroid/util/ReflectiveProperty;->mGetter:Ljava/lang/reflect/Method;

    const/4 v1, 0x0

    check-cast v1, [Ljava/lang/Object;

    invoke-virtual {v2, p1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_c
    .catch Ljava/lang/IllegalAccessException; {:try_start_4 .. :try_end_c} :catch_e
    .catch Ljava/lang/reflect/InvocationTargetException; {:try_start_4 .. :try_end_c} :catch_15

    move-result-object v1

    .line 147
    :goto_d
    return-object v1

    .line 140
    :catch_e
    move-exception v0

    .line 141
    .local v0, "e":Ljava/lang/IllegalAccessException;
    new-instance v1, Ljava/lang/AssertionError;

    invoke-direct {v1}, Ljava/lang/AssertionError;-><init>()V

    throw v1

    .line 142
    .end local v0    # "e":Ljava/lang/IllegalAccessException;
    :catch_15
    move-exception v0

    .line 143
    .local v0, "e":Ljava/lang/reflect/InvocationTargetException;
    new-instance v1, Ljava/lang/RuntimeException;

    invoke-virtual {v0}, Ljava/lang/reflect/InvocationTargetException;->getCause()Ljava/lang/Throwable;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/Throwable;)V

    throw v1

    .line 145
    .end local v0    # "e":Ljava/lang/reflect/InvocationTargetException;
    :cond_20
    iget-object v1, p0, Landroid/util/ReflectiveProperty;->mField:Ljava/lang/reflect/Field;

    if-eqz v1, :cond_32

    .line 147
    :try_start_24
    iget-object v1, p0, Landroid/util/ReflectiveProperty;->mField:Ljava/lang/reflect/Field;

    invoke-virtual {v1, p1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_29
    .catch Ljava/lang/IllegalAccessException; {:try_start_24 .. :try_end_29} :catch_2b

    move-result-object v1

    goto :goto_d

    .line 148
    :catch_2b
    move-exception v0

    .line 149
    .local v0, "e":Ljava/lang/IllegalAccessException;
    new-instance v1, Ljava/lang/AssertionError;

    invoke-direct {v1}, Ljava/lang/AssertionError;-><init>()V

    throw v1

    .line 153
    .end local v0    # "e":Ljava/lang/IllegalAccessException;
    :cond_32
    new-instance v1, Ljava/lang/AssertionError;

    invoke-direct {v1}, Ljava/lang/AssertionError;-><init>()V

    throw v1
.end method

.method public isReadOnly()Z
    .registers 2

    .prologue
    .line 161
    .local p0, "this":Landroid/util/ReflectiveProperty;, "Landroid/util/ReflectiveProperty<TT;TV;>;"
    iget-object v0, p0, Landroid/util/ReflectiveProperty;->mSetter:Ljava/lang/reflect/Method;

    if-nez v0, :cond_a

    iget-object v0, p0, Landroid/util/ReflectiveProperty;->mField:Ljava/lang/reflect/Field;

    if-nez v0, :cond_a

    const/4 v0, 0x1

    :goto_9
    return v0

    :cond_a
    const/4 v0, 0x0

    goto :goto_9
.end method

.method public set(Ljava/lang/Object;Ljava/lang/Object;)V
    .registers 7
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TT;TV;)V"
        }
    .end annotation

    .prologue
    .line 116
    .local p0, "this":Landroid/util/ReflectiveProperty;, "Landroid/util/ReflectiveProperty<TT;TV;>;"
    .local p1, "object":Ljava/lang/Object;, "TT;"
    .local p2, "value":Ljava/lang/Object;, "TV;"
    iget-object v1, p0, Landroid/util/ReflectiveProperty;->mSetter:Ljava/lang/reflect/Method;

    if-eqz v1, :cond_22

    .line 118
    :try_start_4
    iget-object v1, p0, Landroid/util/ReflectiveProperty;->mSetter:Ljava/lang/reflect/Method;

    const/4 v2, 0x1

    new-array v2, v2, [Ljava/lang/Object;

    const/4 v3, 0x0

    aput-object p2, v2, v3

    invoke-virtual {v1, p1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_f
    .catch Ljava/lang/IllegalAccessException; {:try_start_4 .. :try_end_f} :catch_10
    .catch Ljava/lang/reflect/InvocationTargetException; {:try_start_4 .. :try_end_f} :catch_17

    .line 133
    :goto_f
    return-void

    .line 119
    :catch_10
    move-exception v0

    .line 120
    .local v0, "e":Ljava/lang/IllegalAccessException;
    new-instance v1, Ljava/lang/AssertionError;

    invoke-direct {v1}, Ljava/lang/AssertionError;-><init>()V

    throw v1

    .line 121
    .end local v0    # "e":Ljava/lang/IllegalAccessException;
    :catch_17
    move-exception v0

    .line 122
    .local v0, "e":Ljava/lang/reflect/InvocationTargetException;
    new-instance v1, Ljava/lang/RuntimeException;

    invoke-virtual {v0}, Ljava/lang/reflect/InvocationTargetException;->getCause()Ljava/lang/Throwable;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/Throwable;)V

    throw v1

    .line 124
    .end local v0    # "e":Ljava/lang/reflect/InvocationTargetException;
    :cond_22
    iget-object v1, p0, Landroid/util/ReflectiveProperty;->mField:Ljava/lang/reflect/Field;

    if-eqz v1, :cond_33

    .line 126
    :try_start_26
    iget-object v1, p0, Landroid/util/ReflectiveProperty;->mField:Ljava/lang/reflect/Field;

    invoke-virtual {v1, p1, p2}, Ljava/lang/reflect/Field;->set(Ljava/lang/Object;Ljava/lang/Object;)V
    :try_end_2b
    .catch Ljava/lang/IllegalAccessException; {:try_start_26 .. :try_end_2b} :catch_2c

    goto :goto_f

    .line 127
    :catch_2c
    move-exception v0

    .line 128
    .local v0, "e":Ljava/lang/IllegalAccessException;
    new-instance v1, Ljava/lang/AssertionError;

    invoke-direct {v1}, Ljava/lang/AssertionError;-><init>()V

    throw v1

    .line 131
    .end local v0    # "e":Ljava/lang/IllegalAccessException;
    :cond_33
    new-instance v1, Ljava/lang/UnsupportedOperationException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "Property "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {p0}, Landroid/util/ReflectiveProperty;->getName()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-string v3, " is read-only"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    throw v1
.end method
