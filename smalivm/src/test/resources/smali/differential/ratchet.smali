# One small method per opcode family, each arranged so the family opcode is preceded only by
# const/move/return. Nothing here throws, loops, or touches an unloadable class.
.class public Ldifferential/ratchet;
.super Ljava/lang/Object;

.field private static counter:I

# A single if-eqz fork: two transitions from one instruction.
.method public static branchOnZero()I
    .registers 1
    const/4 v0, 0x0
    if-eqz v0, :taken
    const/4 v0, 0x1
    :taken
    return v0
.end method

# A wide comparison producing a narrow result: exercises the wide-pair read path.
.method public static compareWide()I
    .registers 5
    const-wide/16 v0, 0x1
    const-wide/16 v2, 0x2
    cmp-long v4, v0, v2
    return v4
.end method

# A full allocate / store / load round trip through the VM-tracked heap; v2 is both the aget
# destination and the returned value.
.method public static arrayRoundTrip()I
    .registers 3
    const/4 v0, 0x1
    new-array v0, v0, [I
    const/4 v1, 0x0
    const/4 v2, 0x7
    aput v2, v0, v1
    aget v2, v0, v1
    return v2
.end method

# No <clinit>, so the field starts at its default and the round trip is deterministic.
.method public static staticFieldRoundTrip()I
    .registers 1
    const/4 v0, 0x5
    sput v0, Ldifferential/ratchet;->counter:I
    sget v0, Ldifferential/ratchet;->counter:I
    return v0
.end method

# Calls a method in this class so the target always resolves.
.method public static invokeStatic()I
    .registers 1
    invoke-static {}, Ldifferential/ratchet;->branchOnZero()I
    move-result v0
    return v0
.end method

# Allocation followed by the <init> call on Object.
.method public static newInstance()Ljava/lang/Object;
    .registers 1
    new-instance v0, Ljava/lang/Object;
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    return-object v0
.end method

# A trivial constructor, so a `new-instance` of this class is initialized by its own <init>,
# not by a direct super call at the allocation site.
.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

# Two unrelated allocations of one type, then <init> on exactly one: heap identity, not type,
# decides which register is initialized, so v0 stays uninitialized.
.method public static initializeOneOfTwoAllocations()V
    .registers 2
    new-instance v0, Ldifferential/ratchet;
    new-instance v1, Ldifferential/ratchet;
    invoke-direct {v1}, Ldifferential/ratchet;-><init>()V
    return-void
.end method

# The same shape with Object's own <init>; both allocations share one frame.
.method public static initializeOneOfTwoObjectAllocations()V
    .registers 2
    new-instance v0, Ljava/lang/Object;
    new-instance v1, Ljava/lang/Object;
    invoke-direct {v1}, Ljava/lang/Object;-><init>()V
    return-void
.end method
