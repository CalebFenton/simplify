# Guest callers of DirectHostCallsFixture's host methods. Every method's registers are exactly its
# parameters, so p0 is v0 and a test seeds arguments by register number.
.class Ldirect_host_calls;
.super Ljava/lang/Object;

.method public static recordReceived(Ljava/lang/Object;)V
    .registers 1
    invoke-static {p0}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->recordReceived(Ljava/lang/Object;)V
    return-void
.end method

.method public static ignore(Ljava/lang/Object;)V
    .registers 1
    invoke-static {p0}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->ignore(Ljava/lang/Object;)V
    return-void
.end method

.method public static detachChild(Ljava/lang/Object;)Ljava/lang/Object;
    .registers 1
    invoke-static {p0}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->detachChild(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object p0
    return-object p0
.end method

.method public static detachHolderChild(Ljava/lang/Object;)Ljava/lang/Object;
    .registers 1
    invoke-static {p0}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->detachHolderChild(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object p0
    return-object p0
.end method

.method public static throwHolderChild(Ljava/lang/Object;)V
    .registers 1
    invoke-static {p0}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->throwHolderChild(Ljava/lang/Object;)V
    return-void
.end method

.method public static holderChild(Ljava/lang/Object;)Ljava/lang/Object;
    .registers 1
    invoke-static {p0}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->holderChild(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object p0
    return-object p0
.end method

# p1 is a second alias of p0 that the call never sees.
.method public static appendChangedThroughAlias(Ljava/lang/StringBuilder;Ljava/lang/Object;)V
    .registers 2
    invoke-static {p0}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->appendChanged(Ljava/lang/StringBuilder;)V
    return-void
.end method

.method public static returnStored()Ljava/lang/Object;
    .registers 1
    invoke-static {}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->returnStored()Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static firstElement(Ljava/lang/Object;)Ljava/lang/Object;
    .registers 1
    invoke-static {p0}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->firstElement(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object p0
    return-object p0
.end method

.method public static newBuilder()Ljava/lang/Object;
    .registers 1
    invoke-static {}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->newBuilder()Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static appendThenThrow(Ljava/lang/StringBuilder;)V
    .registers 1
    invoke-static {p0}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->appendThenThrow(Ljava/lang/StringBuilder;)V
    return-void
.end method

.method public static identityObject(Ljava/lang/Object;)Ljava/lang/Object;
    .registers 1
    invoke-static {p0}, Lorg/cf/smalivm/core/ReflectBackendFixture;->identityObject(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object p0
    return-object p0
.end method

.method public static identityIntArray([I)[I
    .registers 1
    invoke-static {p0}, Lorg/cf/smalivm/core/ReflectBackendFixture;->identityIntArray([I)[I
    move-result-object p0
    return-object p0
.end method

.method public static throwAndRecord(Ljava/lang/Throwable;)V
    .registers 1
    invoke-static {p0}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->throwAndRecord(Ljava/lang/Throwable;)V
    return-void
.end method

.method public static setFirstThenThrow([I)V
    .registers 1
    invoke-static {p0}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->setFirstThenThrow([I)V
    return-void
.end method

.method public static newIntArray()Ljava/lang/Object;
    .registers 1
    invoke-static {}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->newIntArray()Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static storeNewIntArray([Ljava/lang/Object;)V
    .registers 1
    invoke-static {p0}, Lorg/cf/smalivm/core/DirectHostCallsFixture;->storeNewIntArray([Ljava/lang/Object;)V
    return-void
.end method
