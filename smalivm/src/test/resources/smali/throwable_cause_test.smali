.class public Lthrowable_cause_test;
.super Ljava/lang/Object;

.method public static returnedThrowable()Ljava/lang/Throwable;
    .registers 1
    invoke-static {}, Lorg/cf/smalivm/core/ReflectBackendFixture;->returnNestedThrowable()Ljava/lang/Throwable;
    move-result-object v0
    return-object v0
.end method

.method public static getCause(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    .locals 1

    invoke-virtual {p0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;
    move-result-object v0
    return-object v0
.end method

.method public static catchHostCauseAndRethrow()V
    .locals 2

    :try_start
    invoke-static {}, Lorg/cf/smalivm/core/ReflectBackendFixture;->throwNested()V
    :try_end
    .catchall {:try_start .. :try_end} :handler
    return-void

    :handler
    move-exception v0
    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;
    move-result-object v1
    throw v1
.end method

.method public static catchHostCauseCycle()Ljava/lang/Throwable;
    .locals 1

    :try_start
    invoke-static {}, Lorg/cf/smalivm/core/ReflectBackendFixture;->throwCauseCycle()V
    :try_end
    .catchall {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return-object v0

    :handler
    move-exception v0
    return-object v0
.end method

.method public static catchProvided(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    .locals 1

    :try_start
    invoke-static {p0}, Lorg/cf/smalivm/core/ReflectBackendFixture;->throwProvided(Ljava/lang/Throwable;)V
    :try_end
    .catchall {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return-object v0

    :handler
    move-exception v0
    return-object v0
.end method
