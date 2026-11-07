# Plan 9 Task 19: the instance-field family's differential fixtures. EVERY method here diverges from
# the oracle BY DESIGN (adjudication 6): the oracle models instance fields not at all -- iget answers
# Unknown ignoring the receiver (opcode/IGetOp.kt:25-31), iput writes nothing and never null-checks
# (opcode/IPutOp.kt:25-33) -- so these register as IntendedDivergence entries, never as agreements.
# A MISSING entry for a method in this file is the warning sign.
.class public Ldifferential/ifields;
.super Ljava/lang/Object;

.field public value:I

# new-instance @0, const/16 @2, iput @4, iget @6, return @8. Oracle: Unknown(I). Core: Known(0x42, I).
.method public static putThenGetInt()I
    .locals 2
    new-instance v0, Ldifferential/ifields;
    const/16 v1, 0x42
    iput v1, v0, Ldifferential/ifields;->value:I
    iget v1, v0, Ldifferential/ifields;->value:I
    return v1
.end method

# new-instance @0, iget @2, return @4. A fresh object's unwritten field is a provable type default
# (JLS 12.5). Oracle: Unknown(I). Core: Known(0, I).
.method public static getDefaultInt()I
    .locals 2
    new-instance v0, Ldifferential/ifields;
    iget v1, v0, Ldifferential/ifields;->value:I
    return v1
.end method

# const/4 @0 (1 unit), iget @1 (2), return @3. A definitely-null receiver. Oracle: continues and
# returns Unknown(I), a path a device cannot take. Core: a definite guest NPE, nothing returns.
.method public static getFromNullReceiver()I
    .locals 2
    const/4 v0, 0x0
    iget v1, v0, Ldifferential/ifields;->value:I
    return v1
.end method

# const/4 @0 (1), const/16 @1 (2), iput @3 (2), return @5. Oracle: records WEAK, stores nothing,
# returns Known(0x2a, I). Core: a definite guest NPE and no store.
.method public static putIntoNullReceiver()I
    .locals 2
    const/4 v0, 0x0
    const/16 v1, 0x2a
    iput v1, v0, Ldifferential/ifields;->value:I
    return v1
.end method
