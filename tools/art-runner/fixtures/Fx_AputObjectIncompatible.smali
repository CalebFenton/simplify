# The runtime `ArrayStoreException` check is over the OBJECT'S
# ACTUAL TYPE (never the register tag), and `aput-object` is the only `aput` that can
# raise it.
#
# ALSO, AND MORE IMPORTANTLY: THIS IS THE `-Xint` CANARY. It is the fixture that caught
# ART-standalone's AOT compiler being unsound, and it is why every run of this harness
# passes `-Xint`.
#
# MEASURED, 5/5 reproducible, on `art_standalone 0_git20251009-r2`:
#
#   dalvikvm  -Xint  ... ->  THREW java.lang.ArrayStoreException
#   dalvikvm (default) ... ->  OK -- the store SILENTLY SUCCEEDED, and reading the
#                              element back yielded a `java.lang.Object` out of an array
#                              whose own `getClass().getName()` is `[Ljava.lang.String;`
#
# A real device (API 30) throws in BOTH modes. This image's `dex2oat` is Android
# 10/11-vintage and demonstrably drops the assignability check, so its AOT tier is not a
# Dalvik oracle at all. `verify-xint-mandate.sh` runs THIS fixture both ways on every
# invocation and fails if that divergence ever stops reproducing -- which would mean
# either the image changed or the mandate is no longer needed, and both are things a
# human must be told about rather than have silently assumed.
#
# WHY THE FIXTURE DOES NOT STOP AT THE STORE: the trailing `aget-object` + `getClass()`
# is unreachable under `-Xint`, and that is the point. Under a mode where the check is
# missing it reaches the read and reports the impossible element type, so ONE fixture
# distinguishes "correct" from "silently wrong" instead of "correct" from "no answer".
#
# SELF-CONTAINED: allocates its own `String[]` and its own `Object`.

.class public LFx_AputObjectIncompatible;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 7

    const/4 v0, 0x1
    new-array v1, v0, [Ljava/lang/String;

    # Pin the array's REAL runtime type before the store, so the report cannot be
    # explained away as "the array was not really a String[]".
    invoke-virtual {v1}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
    move-result-object v2
    invoke-virtual {v2}, Ljava/lang/Class;->getName()Ljava/lang/String;
    move-result-object v2

    const/4 v3, 0x0
    new-instance v4, Ljava/lang/Object;
    invoke-direct {v4}, Ljava/lang/Object;-><init>()V

    # THE OPCODE UNDER TEST. Verifier-valid: `Object` into a `String[]`-typed register is
    # legal to ASSEMBLE and legal to LINK -- assignability is a runtime question.
    aput-object v4, v1, v3

    # Only reachable if the store wrongly succeeded.
    aget-object v5, v1, v3
    invoke-virtual {v5}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
    move-result-object v6
    invoke-virtual {v6}, Ljava/lang/Class;->getName()Ljava/lang/String;
    move-result-object v6

    new-instance v0, Ljava/lang/StringBuilder;
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V
    const-string v3, "NO_ASE arrayType="
    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    const-string v3, " storedElemType="
    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    invoke-virtual {v0, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object v3

    return-object v3
.end method
