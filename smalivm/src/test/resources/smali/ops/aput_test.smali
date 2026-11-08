.class Laput_test;
.super Ljava/lang/Object;

.method public static put()V
    .locals 3

    aput v2, v0, v1 # v0[v1] = v2

    return-void
.end method

.method public static putWithCatch()V
    .locals 3

    :try_start_0
    aput v2, v0, v1 # v0[v1] = v2
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    nop

    :return
    return-void

    :catch_0
    move-exception v0
    goto :return

    return-void
.end method

# `.locals 4` is required: `aput-wide v2, v0, v1` reads the pair v2+v3, and `Frame.assign` rejects
# a `J`/`D` value whose high half falls outside the frame.
#
# `canPutWideWithFloat` would pass under `.locals 3` too: `F` is not wide (`VirtualType.isWide` is
# `J` or `D` only), despite the `-wide` mnemonic.
.method public static putWide()V
    .locals 4

    aput-wide v2, v0, v1

    return-void
.end method

.method public static putObject()V
    .locals 3

    aput-object v2, v0, v1

    return-void
.end method

.method public static putObjectAndReturnObserved(Ljava/lang/Object;[Lspine/absent;)Ljava/lang/Object;
    .locals 2

    const/4 v0, 0x0
    :try_start
    aput-object p0, p1, v0
    :try_end
    .catch Ljava/lang/ArrayStoreException; {:try_start .. :try_end} :handler

    aget-object v1, p1, v0
    return-object v1

    :handler
    move-exception v1
    aget-object v1, p1, v0
    return-object v1
.end method

# `.locals 5` because the body names v3 and v4; `Frame.assign` rejects registers outside the
# frame. Addresses: `aput-object`@0, `aput-object`@2, `return-void`@4 (format 23x is two units).
#
# `aput` is `vAA` = value, `vBB` = array, `vCC` = index, and every `aput*` here uses three distinct
# registers `v2, v0, v1`. `APutOpTest.theThreeOperandsAreNotInterchangeable` pins the order on values.
.method public static putObjects()V
    .locals 5

    aput-object v2, v0, v1
    aput-object v4, v0, v3

    return-void
.end method

.method public static putBoolean()V
    .locals 3

    aput-boolean v2, v0, v1

    return-void
.end method

.method public static putByte()V
    .locals 3

    aput-byte v2, v0, v1

    return-void
.end method

.method public static putChar()V
    .locals 3

    aput-char v2, v0, v1

    return-void
.end method

.method public static putShort()V
    .locals 3

    aput-short v2, v0, v1

    return-void
.end method

# The round trip through [Ljava/lang/Object; gives the value register a tag that is a strict
# supertype of the object's own type using only verifier-valid opcodes: aput-object stores the
# element under the array's component type, and aget-object reads it back verbatim, so v3's tag is
# Ljava/lang/Object; while the object is Laput_test;. The final aput-object into [Laput_test; must
# check the object, not the tag: JVMS aastore tests the actual class and permits the store.
.method public static putRoundTrippedInstanceIntoOwnTypeArray()V
    .locals 4

    new-instance v0, Laput_test;

    const/4 v1, 0x1
    new-array v1, v1, [Ljava/lang/Object;

    const/4 v2, 0x0
    aput-object v0, v1, v2

    aget-object v3, v1, v2

    const/4 v0, 0x1
    new-array v0, v0, [Laput_test;

    aput-object v3, v0, v2

    return-void
.end method
