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

# `.locals 4`, raised from 3 in Plan 7 Task 8, and the extra register is REQUIRED rather than tidy.
# `aput-wide v2, v0, v1` reads a wide value out of v2, which is the pair v2+v3 -- so v3 must exist. The
# core's `Frame.assign` reserves `register + 1` as a `WideHigh` slot for any `J`/`D` value and
# `require`s `high < registerCount`, so seeding v2 with a `D` or a `J` under `.locals 3` THROWS
# (`3 < 3` is false). The oracle's map-backed `pokeRegister` tolerated it silently, which is why the
# malformed `.locals` survived this long. `.locals` is a header directive: it moves no instruction and
# renumbers nothing, so `aput-wide`@0 / `return-void`@2 and the meaning of v0..v2 are unchanged.
#
# ASYMMETRY WORTH STATING, because it looks like a bug later: `canPutWideWithFloat` passes either way.
# `F` is NOT wide -- `VirtualType.isWide` is `J` or `D` only -- even though the MNEMONIC is `-wide`, so
# a float value occupies v2 alone and reserves nothing.
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

# `.locals 5`, raised from 3 in Plan 7 Task 8. The BODY names v3 and v4, so `.locals 3` was malformed
# on its own terms -- and its only test seeds registers 3 and 4 as well. The oracle's map-backed
# `pokeRegister` tolerated writing registers the method never declared; the core's `Frame.assign`
# range-`require`s and THROWS, so this went red on arrival. `.locals` moves no instruction and
# renumbers nothing (`aput-object`@0, `aput-object`@2, `return-void`@4 -- format 23x is TWO code
# units), and for a static no-arg method v0..v2 keep meaning exactly what they meant.
#
# THREE DISTINCT REGISTERS PER INSTRUCTION, which matters beyond this method's own test: `aput` is
# `vAA` = VALUE, `vBB` = array, `vCC` = index, and every `aput*` in this file is already
# `v2, v0, v1` -- unlike `aget_test.smali`, whose corpus methods are all `v0, v0, v1` and hid a
# dest/array transposition from the entire suite. `APutOpTest.theThreeOperandsAreNotInterchangeable`
# pins the order on values rather than relying on that luck.
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

# --- Appended by Plan 9 Task 17, which landed `new-instance` and closed CARRY-FORWARD item 16
# --- (the value side of aput-object's store check is now object-over-tag).
#
# The round trip through [Ljava/lang/Object; is what makes the value register's TAG a strict
# SUPERTYPE of the tracked object's own type using only verifier-valid opcodes: aput-object into a
# [Ljava/lang/Object; stores the element under the ARRAY's component type (storedValue's rule), and
# aget-object reads the element back verbatim -- so v3's tag is Ljava/lang/Object; while the object
# behind the ref is Laput_test;. The final aput-object into a [Laput_test; is then exactly the
# shape item 16 filed: the tag says refuse (Object is not a subtype of aput_test), the object says
# permit, and JVMS aastore -- which tests the ACTUAL class -- permits.
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
