.class public Lhost_exception_boundary;
.super Ljava/lang/Object;

.method public static rethrow(Ljava/lang/Throwable;)V
    .registers 1
    throw p0
.end method

.method public static identityRoundTrip()I
    .registers 3
  :host_start
    invoke-static {}, Lorg/cf/smalivm/core/ReflectBackendFixture;->throwNested()V
  :host_end
    const/4 v2, 0x0
    return v2
  :host_caught
    move-exception v0
  :rethrow_start
    invoke-static {v0}, Lhost_exception_boundary;->rethrow(Ljava/lang/Throwable;)V
  :rethrow_end
    const/4 v2, 0x0
    return v2
  :rethrow_caught
    move-exception v1
    if-ne v0, v1, :bad
    const/4 v2, 0x1
    return v2
  :bad
    const/4 v2, 0x0
    return v2
    .catchall {:host_start .. :host_end} :host_caught
    .catchall {:rethrow_start .. :rethrow_end} :rethrow_caught
.end method

.method public static identityAcrossPublicOutcomes(I)Ljava/lang/Throwable;
    .registers 2
  :host_start
    invoke-static {}, Lorg/cf/smalivm/core/ReflectBackendFixture;->throwNested()V
  :host_end
    const/4 v0, 0x0
    return-object v0
  :host_caught
    move-exception v0
    if-eqz p0, :escape
    return-object v0
  :escape
    invoke-static {v0}, Lhost_exception_boundary;->rethrow(Ljava/lang/Throwable;)V
    const/4 v0, 0x0
    return-object v0
    .catchall {:host_start .. :host_end} :host_caught
.end method

.method public static catchTarget()I
    .registers 2
  :try_start
    invoke-static {}, Lorg/cf/smalivm/core/ReflectBackendFixture;->throwNested()V
  :try_end
    const/4 v0, 0x0
    return v0
  :caught
    move-exception v1
    const/4 v0, 0x1
    return v0
    .catchall {:try_start .. :try_end} :caught
.end method

.method public static branchingInfrastructure(I)I
    .registers 2
    if-eqz p0, :host
    const/4 v0, 0x7
    return v0
  :host
    invoke-static {}, Lorg/cf/smalivm/core/ReflectBackendFixture;->throwNested()V
    const/4 v0, 0x0
    return v0
.end method

.method public static mutationSurvivesTargetThrow(Ljava/lang/StringBuilder;)Ljava/lang/String;
    .registers 2
  :try_start
    invoke-static {p0}, Lorg/cf/smalivm/core/ReflectBackendFixture;->mutateThenThrow(Ljava/lang/StringBuilder;)V
  :try_end
    const-string v0, "normal"
    return-object v0
  :caught
    move-exception v0
    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object v1
    return-object v1
    .catch Ljava/lang/IllegalStateException; {:try_start .. :try_end} :caught
.end method

.method public static fatalEscape()V
    .registers 0
    invoke-static {}, Lorg/cf/smalivm/core/ReflectBackendFixture;->throwFatal()V
    return-void
.end method

.method public static reflectionInfrastructureFailure()I
    .registers 2
  :try_start
    invoke-static {}, Lorg/cf/smalivm/core/ReflectBackendFixture;->missingOnHost()I
    move-result v0
  :try_end
    return v0
  :caught
    move-exception v1
    const/4 v0, 0x1
    return v0
    .catchall {:try_start .. :try_end} :caught
.end method

.method public static hostOnlyRuntimeFirstMatch()I
    .registers 2
  :try_start
    invoke-static {}, Lorg/cf/smalivm/core/ReflectBackendFixture;->throwNested()V
  :try_end
    const/4 v0, 0x0
    return v0
  :runtime
    move-exception v1
    const/4 v0, 0x1
    return v0
  :exception
    move-exception v1
    const/4 v0, 0x2
    return v0
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :runtime
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :exception
.end method

.method public static nonThrowableBecomesInternalError(Ljava/lang/Object;)Ljava/lang/Throwable;
    .registers 2
  :try_start
    throw p0
  :try_end
    const/4 v0, 0x0
    return-object v0
  :error
    move-exception v0
    return-object v0
    .catch Ljava/lang/Error; {:try_start .. :try_end} :error
.end method
