.class public Lsput_test;
.super Ljava/lang/Object;

.field public static myInt:I
.field public static myLong:J
.field public static myString:Ljava/lang/String;
.field public static myBoolean:Z
.field public static myByte:B
.field public static myChar:C
.field public static myShort:S

.method public static putStaticInt()V
    .locals 1
    sput v0, Lsput_test;->myInt:I
    return-void
.end method

.method public static putStaticWide()V
    .locals 2
    sput-wide v0, Lsput_test;->myLong:J
    return-void
.end method

.method public static putStaticObject()V
    .locals 1
    sput-object v0, Lsput_test;->myString:Ljava/lang/String;
    return-void
.end method

.method public static putStaticBoolean()V
    .locals 1
    sput-boolean v0, Lsput_test;->myBoolean:Z
    return-void
.end method

.method public static putStaticByte()V
    .locals 1
    sput-byte v0, Lsput_test;->myByte:B
    return-void
.end method

.method public static putStaticChar()V
    .locals 1
    sput-char v0, Lsput_test;->myChar:C
    return-void
.end method

.method public static putStaticShort()V
    .locals 1
    sput-short v0, Lsput_test;->myShort:S
    return-void
.end method

# The write-side twin of sget_test's getStaticUnknownClassField: the referenced class is not in the
# loaded set, so resolution fails before the value register is ever read. Exists for OpBuilderTest's
# build-branch pin -- buildSPut is a separate `when` row from buildSGet, so it needs its own witness.
.method public static putStaticUnknownClassField()V
    .locals 1
    sput v0, Lsome/unknown/classzzzzz;->herp:I
    return-void
.end method

# Forks on the unknown parameter and each arm writes a different constant, so per-path static storage
# joins Unknown(I) over the two returning paths -- while a session-global store would report whichever
# write ran last as a definite value on both paths. The pin named by ClassInitStore.copy's KDoc
# (core/op/SPutOpTest.oneBranchesStaticWriteIsInvisibleToTheOther).
.method public static putStaticIntBranched(I)V
    .locals 1
    if-eqz p0, :else
    const/4 v0, 0x1
    sput v0, Lsput_test;->myInt:I
    return-void
    :else
    const/4 v0, 0x2
    sput v0, Lsput_test;->myInt:I
    return-void
.end method
