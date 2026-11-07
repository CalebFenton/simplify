.class public Lframework_static_test;
.super Ljava/lang/Object;

.method public static encodedLiteral()I
    .locals 1
    sget v0, Ljava/lang/Integer;->MAX_VALUE:I
    return v0
.end method

.method public static primitive()I
    .locals 1
    sget v0, Lsun/net/www/MimeEntry;->UNKNOWN:I
    return v0
.end method

.method public static nullable()Ljava/lang/ClassLoader;
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

.method public static throwableReference()Ljava/lang/Object;
    .locals 1
    sget-object v0, Lorg/json/JSONObject;->NULL:Ljava/lang/Object;
    return-object v0
.end method

.method public static unsafeSystemField()Ljava/io/PrintStream;
    .locals 1
    sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
    return-object v0
.end method

.method public static unsafeAncestorField()I
    .locals 1
    sget v0, Lcom/android/server/connectivity/metrics/IpConnectivityLogClass$DefaultNetworkEvent;->NONE:I
    return v0
.end method

.method public static realInitializerError()I
    .locals 2
    :try_start
    sget v0, Lcom/google/common/Config;->KEY_BACK:I
    :try_end
    const/4 v1, 0x0
    return v1

    .catch Ljava/lang/AssertionError; {:try_start .. :try_end} :caught

    :caught
    move-exception v0
    const/4 v1, 0x1
    return v1
.end method

.method public static putOverridesProjection()Ljava/lang/Boolean;
    .locals 2
    sget-object v0, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;
    const/4 v1, 0x0
    sput-object v1, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;
    sget-object v0, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;
    return-object v0
.end method

.method public static catchesProjectionThrow()I
    .locals 2
    :try_start
    sget-object v0, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;
    :try_end
    const/4 v1, 0x0
    return v1

    .catch Ljava/lang/Throwable; {:try_start .. :try_end} :caught

    :caught
    move-exception v0
    const/4 v1, 0x1
    return v1
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

.method public static siblingProjectionMutation(I)Ljava/util/List;
    .locals 2
    if-eqz p0, :unmutated
    sget-object v0, Ljava/util/Collections;->EMPTY_LIST:Ljava/util/List;
    const-string v1, "changed"
    invoke-interface {v0, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z
    return-object v0

    :unmutated
    sget-object v0, Ljava/util/Collections;->EMPTY_LIST:Ljava/util/List;
    return-object v0
.end method
