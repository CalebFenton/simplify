.class public Lbackend_side_effect_test;
.super Ljava/lang/Object;

.method public static pureLength(Ljava/lang/String;)I
    .locals 1

    invoke-static {p0}, Lorg/cf/smalivm/core/BackendCorpusHostFixture;->pureLength(Ljava/lang/String;)I
    move-result v0
    return v0
.end method

.method public static provenHostCalls()Ljava/lang/String;
    .locals 3

    new-instance v0, Ljava/lang/StringBuilder;
    const-string v1, "seed"
    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V
    const-string v2, "-value"
    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    move-result-object v0
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object v0
    return-object v0
.end method

.method public static observedMutation(Ljava/lang/StringBuilder;)Ljava/lang/StringBuilder;
    .locals 1

    move-object v0, p0
    invoke-static {p0}, Lorg/cf/smalivm/core/BackendCorpusHostFixture;->mutate(Ljava/lang/StringBuilder;)V
    return-object v0
.end method

.method public static mutationBeforeTargetThrow(Ljava/lang/StringBuilder;)Ljava/lang/String;
    .locals 1

    :try_start
    invoke-static {p0}, Lorg/cf/smalivm/core/BackendCorpusHostFixture;->mutateThenThrow(Ljava/lang/StringBuilder;)V
    :try_end
    .catch Ljava/lang/IllegalStateException; {:try_start .. :try_end} :caught

    const-string v0, "normal"
    return-object v0

    :caught
    move-exception v0
    const-string v0, "caught"
    return-object v0
.end method
