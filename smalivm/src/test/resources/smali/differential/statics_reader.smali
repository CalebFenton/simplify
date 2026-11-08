# Reads Ldifferential/statics; from another class, so the sget triggers its initialization mid-run.
.class public Ldifferential/statics_reader;
.super Ljava/lang/Object;

# The <clinit> runs before the sget and assigns 7.
.method public static readClinitAssigned()I
    .locals 1
    sget v0, Ldifferential/statics;->assignedByClinit:I
    return v0
.end method

# A dex literal the <clinit> never assigns: 0x2a, seeded at preparation.
.method public static readLiteralUntouchedByClinit()I
    .locals 1
    sget v0, Ldifferential/statics;->literalUntouchedByClinit:I
    return v0
.end method
