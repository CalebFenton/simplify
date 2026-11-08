.class public Lframework_static_test;
.super Ljava/lang/Object;

.method public static encodedLiteral()I
    .locals 1
    sget v0, Ljava/lang/Integer;->MAX_VALUE:I
    return v0
.end method

.method public static primitive()C
    .locals 1
    sget-char v0, Ljava/io/File;->separatorChar:C
    return v0
.end method

.method public static hostAbsentClassField()Ljava/lang/ClassLoader;
    .locals 1
    sget-object v0, Ljava/lang/ClassLoader$SystemClassLoader;->loader:Ljava/lang/ClassLoader;
    return-object v0
.end method

.method public static immutableReference()Ljava/lang/Boolean;
    .locals 1
    sget-object v0, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;
    return-object v0
.end method

.method public static mutableReference()Ljava/util/List;
    .locals 1
    sget-object v0, Ljava/util/Collections;->EMPTY_LIST:Ljava/util/List;
    return-object v0
.end method

.method public static unsafeSystemField()Ljava/io/PrintStream;
    .locals 1
    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
    return-object v0
.end method

.method public static subclassField()Ljava/lang/Class;
    .locals 1
    sget-object v0, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;
    return-object v0
.end method

.method public static putOverridesProjection()Ljava/lang/Boolean;
    .locals 2
    sget-object v0, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;
    const/4 v1, 0x0
    sput-object v1, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;
    sget-object v0, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;
    return-object v0
.end method

.method public static siblingProjection(I)Ljava/util/List;
    .locals 1
    if-eqz p0, :zero
    sget-object v0, Ljava/util/Collections;->EMPTY_LIST:Ljava/util/List;
    return-object v0

    :zero
    sget-object v0, Ljava/util/Collections;->EMPTY_LIST:Ljava/util/List;
    return-object v0
.end method
