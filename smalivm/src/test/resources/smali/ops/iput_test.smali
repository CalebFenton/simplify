# Receivers are `new-instance` allocations with no `<init>` call; the engine does not enforce the verifier's
# uninitialized-use rule.
.class public Liput_test;
.super Ljava/lang/Object;

.field public myInt:I
.field public myString:Ljava/lang/String;
.field public static staticOnly:I

# Returns the receiver so the test can follow the ref into the heap and assert the stored cell.
.method public static putInt()Liput_test;
    .locals 2
    new-instance v0, Liput_test;
    const/16 v1, 0x42
    iput v1, v0, Liput_test;->myInt:I
    return-object v0
.end method

# A definitely-null receiver: const/4 @0 (1 unit), const/16 @1 (2), iput @3 (2), return-void @5.
# The NPE is definite and the store must not happen, so the fall-through at 5 stays unreached.
.method public static putIntoNullReceiver()V
    .locals 2
    const/4 v0, 0x0
    const/16 v1, 0x2a
    iput v1, v0, Liput_test;->myInt:I
    return-void
.end method

# A non-zero primitive read as a reference receiver (verifier-excluded input). Same addresses.
.method public static putIntoPrimitiveTaggedReceiver()V
    .locals 2
    const/4 v0, 0x5
    const/16 v1, 0x2a
    iput v1, v0, Liput_test;->myInt:I
    return-void
.end method

# Resolution precedes null handling. The absent owner must produce NCDFE, never receiver NPE.
.method public static putMissingClassOnNullReceiver()V
    .locals 2
    const/4 v0, 0x0
    const/16 v1, 0x2a
    iput v1, v0, Ldoes/not/exist;->myInt:I
    return-void
.end method

# Present class and valid receiver, absent instance member: definite NoSuchFieldError and no store.
.method public static putMissingMember()V
    .locals 2
    new-instance v0, Liput_test;
    const/16 v1, 0x2a
    iput v1, v0, Liput_test;->doesNotExistzzz:I
    return-void
.end method

# Instance opcode against a static declaration: instance resolution must reject the static-only member.
.method public static putStaticFieldWithInstanceOpcode()V
    .locals 2
    new-instance v0, Liput_test;
    const/16 v1, 0x2a
    iput v1, v0, Liput_test;->staticOnly:I
    return-void
.end method

# An unprovable receiver: the write's target cannot be identified (STRONG), and the NPE is feasible
# alongside the fall-through. iput @0, return-void @2.
.method public static putThroughUnknownReceiver(Liput_test;I)V
    .locals 0
    iput p1, p0, Liput_test;->myInt:I
    return-void
.end method

# An unknown receiver must not be guessed to mean some tracked object already in the heap. The normal
# outcome reads the separate fresh object's untouched default; the NPE outcome remains feasible.
.method public static putThroughUnknownReceiverDoesNotInventTarget(Liput_test;I)I
    .locals 2
    new-instance v0, Liput_test;
    iput p1, p0, Liput_test;->myInt:I
    iget v1, v0, Liput_test;->myInt:I
    return v1
.end method

# Both outcomes return. A caught NPE must see the same untouched unrelated object as the normal path.
.method public static unknownReceiverCaughtNpeKeepsUnrelatedField(Liput_test;I)I
    .locals 3
    new-instance v0, Liput_test;
    :try_start
    iput p1, p0, Liput_test;->myInt:I
    :try_end
    iget v1, v0, Liput_test;->myInt:I
    return v1

    :catch_npe
    move-exception v2
    iget v1, v0, Liput_test;->myInt:I
    return v1

    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :catch_npe
.end method

# Verifier-excluded input: the receiver is a known, non-null host value rather than a tracked object.
# No guest exception is invented and the unmodelled target is STRONG (DALVIK-SEMANTICS.md
# "Engine choices for verifier-excluded input").
.method public static putThroughKnownNonHeapRefReceiver()I
    .locals 2
    const-string v0, "known but not tracked"
    const/16 v1, 0x2a
    iput v1, v0, Liput_test;->myInt:I
    return v1
.end method

# Verifier-excluded input: the receiver is tracked, but its object layout has no Liput_test cell. Its own
# Liget_test cell must stay at the default, and the unmodelled target is STRONG.
.method public static putThroughTrackedObjectWithoutResolvedCell()Liget_test;
    .locals 2
    new-instance v0, Liget_test;
    const/16 v1, 0x2a
    iput v1, v0, Liput_test;->myInt:I
    return-object v0
.end method

# `iput-object` reads its value register as a reference, so a primitive-tagged 5 stores null. Read back
# through iget-object.
.method public static putObjectWithPrimitiveTaggedValue()Ljava/lang/String;
    .locals 3
    new-instance v0, Liput_test;
    const/4 v1, 0x5
    iput-object v1, v0, Liput_test;->myString:Ljava/lang/String;
    iget-object v2, v0, Liput_test;->myString:Ljava/lang/String;
    return-object v2
.end method

# Returns the receiver so the test can inspect the stored cell before an iget-object masks a bad store
# by applying its own reference coercion.
.method public static putObjectWithPrimitiveTaggedValueAndReturnReceiver()Liput_test;
    .locals 2
    new-instance v0, Liput_test;
    const/4 v1, 0x5
    iput-object v1, v0, Liput_test;->myString:Ljava/lang/String;
    return-object v0
.end method

# Two arms write different constants into one object's cell; the read after the join must see them
# disagree (per-path heap isolation observed through the ops).
.method public static putIntBranched(I)I
    .locals 3
    new-instance v0, Liput_test;
    if-eqz p0, :else
    const/4 v1, 0x1
    iput v1, v0, Liput_test;->myInt:I
    goto :end
    :else
    const/4 v1, 0x2
    iput v1, v0, Liput_test;->myInt:I
    :end
    iget v2, v0, Liput_test;->myInt:I
    return v2
.end method

# Two registers, one object: a write through the alias is visible through the other register, because
# identity is the heap id and the cell lives on the object.
.method public static putThroughAliasReadThroughOriginal()I
    .locals 4
    new-instance v0, Liput_test;
    move-object v1, v0
    const/16 v2, 0x42
    iput v2, v1, Liput_test;->myInt:I
    iget v3, v0, Liput_test;->myInt:I
    return v3
.end method

# Two objects of one type have two distinct cells: identity is the heap id, never the type.
.method public static putIntoTwoObjectsKeepsCellsDistinct()V
    .locals 5
    new-instance v0, Liput_test;
    new-instance v1, Liput_test;
    const/4 v2, 0x1
    iput v2, v0, Liput_test;->myInt:I
    const/4 v2, 0x2
    iput v2, v1, Liput_test;->myInt:I
    iget v3, v0, Liput_test;->myInt:I
    iget v4, v1, Liput_test;->myInt:I
    return-void
.end method
