# The reading half of Ldifferential/statics; (see that file's header for why the split is
# load-bearing): a method of ANOTHER class touching the statics is what makes both engines initialize
# `Ldifferential/statics;` mid-run -- the path on which the oracle discards dex literals for a class
# with a <clinit> (adjudication 5) while running the <clinit> itself correctly.
.class public Ldifferential/statics_reader;
.super Ljava/lang/Object;

# The AGREEMENT control: the <clinit> runs as the sget's prelude on both engines and both read the 7
# it wrote. A wrong answer here is engine drift (a broken trigger), not adjudication 5.
.method public static readClinitAssigned()I
    .locals 1
    sget v0, Ldifferential/statics;->assignedByClinit:I
    return v0
.end method

# The DIVERGENCE: a dex literal the <clinit> never assigns. Oracle: Unknown ("undefined field").
# Core: Known(0x2a, I), seeded at preparation. Registered as statics-literal-untouched-by-clinit.
.method public static readLiteralUntouchedByClinit()I
    .locals 1
    sget v0, Ldifferential/statics;->literalUntouchedByClinit:I
    return v0
.end method
