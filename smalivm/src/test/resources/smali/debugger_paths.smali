.class public Ldebugger_paths;
.super Ljava/lang/Object;

# Both arms of an unknown fork call a callee whose entry forks into two returns.
.method public static forkThenCallForkingCallee(II)I
    .registers 3

    if-eqz p0, :second

    invoke-static {p1}, Ldebugger_paths;->returnsFromEitherPath(I)I
    move-result v0
    return v0

    :second
    invoke-static {p1}, Ldebugger_paths;->returnsFromEitherPath(I)I
    move-result v0
    return v0
.end method

.method public static returnsFromEitherPath(I)I
    .registers 1

    if-eqz p0, :zero
    return p0

    :zero
    return p0
.end method

# The fall-through arm runs first and decodes to UnsupportedOp (`[J` is not a legal filled-new-array
# operand); the branch-target arm stays scheduled behind it.
# Addresses: @0 if-eqz (2 units), @2 filled-new-array (3 units), @5 return, @6 const/4 (index 3), @7 return.
.method public static abortsBeforeCleanBranch(I)I
    .registers 2

    if-eqz p0, :clean

    filled-new-array {p0}, [J
    return p0

    :clean
    const/4 v0, 0x1
    return v0
.end method
