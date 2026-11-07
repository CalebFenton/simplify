# `android.util.Base64` under bare `dalvikvm`. Obfuscators reach for this constantly in
# string-decryption routines, so whether it works outside an app process is a question
# that matters for emulation fidelity rather than a curiosity.
#
# Encodes the two bytes {0x68, 0x69} -- "hi" -- with NO_WRAP (flag 2).
.class public LFx_AndroidBase64;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 4

    const/4 v0, 0x2
    new-array v1, v0, [B

    const/4 v2, 0x0
    const/16 v3, 0x68
    aput-byte v3, v1, v2

    const/4 v2, 0x1
    const/16 v3, 0x69
    aput-byte v3, v1, v2

    invoke-static {v1, v0}, Landroid/util/Base64;->encodeToString([BI)Ljava/lang/String;
    move-result-object v0

    return-object v0
.end method
