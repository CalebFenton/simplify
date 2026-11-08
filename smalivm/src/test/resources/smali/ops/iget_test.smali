# Receivers are built with `new-instance` and no `<init>` call. The engine does not enforce the verifier's
# uninitialized-use rule, and the uninitialized flag does not affect field storage.
.class public Liget_test;
.super Ljava/lang/Object;

.field public myInt:I
.field public myLong:J
.field public myString:Ljava/lang/String;
.field public static staticOnly:I

# A fresh object's unwritten field holds its type's default (JLS §12.5), not an unknown.
.method public static getIntFromFreshObject()I
    .locals 2
    new-instance v0, Liget_test;
    iget v1, v0, Liget_test;->myInt:I
    return v1
.end method

.method public static getLongFromFreshObject()J
    .locals 3
    new-instance v0, Liget_test;
    iget-wide v1, v0, Liget_test;->myLong:J
    return-wide v1
.end method

.method public static getStringFromFreshObject()Ljava/lang/String;
    .locals 2
    new-instance v0, Liget_test;
    iget-object v1, v0, Liget_test;->myString:Ljava/lang/String;
    return-object v1
.end method

# The round trip: what an iput stored, an iget of the same cell reads back.
.method public static putThenGetInt()I
    .locals 3
    new-instance v0, Liget_test;
    const/16 v1, 0x42
    iput v1, v0, Liget_test;->myInt:I
    iget v2, v0, Liget_test;->myInt:I
    return v2
.end method

.method public static putThenGetLong()J
    .locals 3
    new-instance v0, Liget_test;
    const-wide/16 v1, 0x2a
    iput-wide v1, v0, Liget_test;->myLong:J
    iget-wide v1, v0, Liget_test;->myLong:J
    return-wide v1
.end method

.method public static putThenGetString()Ljava/lang/String;
    .locals 3
    new-instance v0, Liget_test;
    const-string v1, "do not pray for an easy life"
    iput-object v1, v0, Liget_test;->myString:Ljava/lang/String;
    iget-object v2, v0, Liget_test;->myString:Ljava/lang/String;
    return-object v2
.end method

# A definitely-null receiver: iget at address 1, return at 3 (const/4 is 1 unit, iget is 2).
.method public static getIntFromNullReceiver()I
    .locals 2
    const/4 v0, 0x0
    iget v1, v0, Liget_test;->myInt:I
    return v1
.end method

# A non-zero primitive under the receiver register reads as null (verifier-excluded input; see
# DALVIK-SEMANTICS.md "ART has two interpreter tiers that read a register differently"). Same addresses.
.method public static getIntFromPrimitiveTaggedReceiver()I
    .locals 2
    const/4 v0, 0x5
    iget v1, v0, Liget_test;->myInt:I
    return v1
.end method

# Resolution precedes the receiver read: an absent field class on a null receiver throws the
# resolution error, never the NPE.
.method public static getFieldOfUnknownClassOnNullReceiver()I
    .locals 2
    const/4 v0, 0x0
    iget v1, v0, Lsome/unknown/classzzzzz;->someField:I
    return v1
.end method

# The class resolves and declares no such field anywhere in its chain. Receiver is a real object, so
# only the resolution can fail. new-instance @0, iget @2, return @4.
.method public static getMissingField()I
    .locals 2
    new-instance v0, Liget_test;
    iget v1, v0, Liget_test;->doesNotExistzzz:I
    return v1
.end method

# Instance opcode against a static declaration: the class exists, but instance resolution must not see it.
.method public static getStaticFieldWithInstanceOpcode()I
    .locals 2
    new-instance v0, Liget_test;
    iget v1, v0, Liget_test;->staticOnly:I
    return v1
.end method

# An unprovable receiver: the field value is unknown and the NPE is feasible, so both paths run.
.method public static getIntThroughUnknownReceiver(Liget_test;)I
    .locals 1
    iget v0, p0, Liget_test;->myInt:I
    return v0
.end method

# The normal iget result must not leak into the caught-NPE successor. v0 starts at 42. The normal
# path returns Unknown(I); the handler returns the unchanged Known(42, I).
.method public static unknownReceiverPreservesDestinationOnCaughtNpe(Liget_test;)I
    .locals 2
    const/16 v0, 0x2a
    :try_start
    iget v0, p0, Liget_test;->myInt:I
    :try_end
    return v0

    :catch_npe
    move-exception v1
    return v0

    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :catch_npe
.end method

# An inherited instance field written through the child's name and read through the parent's. Legal dex
# that javac never emits; it shares one cell only if resolution walks the superclass chain to the
# declaring class both times.
.method public static putInheritedThroughChildGetThroughParent()I
    .locals 3
    new-instance v0, Lifield_child_test;
    const/4 v1, 0x7
    iput v1, v0, Lifield_child_test;->inherited:I
    iget v2, v0, Lifield_parent_test;->inherited:I
    return v2
.end method

# Same name, different type, two classes: `shadow:I` on the parent, `shadow:J` on the child. The reference
# `Lifield_child_test;->shadow:I` must resolve past the child's `J` to the parent's `I` (fields are keyed on
# name and type), and the child's own `J` cell must stay untouched.
.method public static putShadowedIntReadsParentsCellNotChildsLong()I
    .locals 4
    new-instance v0, Lifield_child_test;
    const/4 v1, 0x7
    iput v1, v0, Lifield_child_test;->shadow:I
    iget-wide v2, v0, Lifield_child_test;->shadow:J
    iget v1, v0, Lifield_parent_test;->shadow:I
    return v1
.end method
