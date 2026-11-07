.class public Lstatic_fields_test;
.super Ljava/lang/Object;

.field private static alwaysZero:I
.field private static testInt1:I
.field private static testInt2:I

.field private static myList:Ljava/util/List;

# Task 18's tracked-object cell for the ported multiverse tests: myList's ArrayList needs
# new-instance + <init> + emulation (Tasks 20/Plan 11); an array is the tracked object the core can
# build today, and the identity claims are the same.
.field private static myArray:[Ljava/lang/String;

.method public static <clinit>()V
    .locals 1
    sget v0, Lstatic_fields_test;->alwaysZero:I
    sput v0, Lstatic_fields_test;->testInt1:I

    const/4 v0, 0x1
    sput v0, Lstatic_fields_test;->testInt2:I

    return-void
.end method

.method public constructor <init>()V
    .locals 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static stubMethod()V
    .locals 0
    return-void
.end method

# Plan 9 Task 18's trigger carrier for the ported TestStaticFieldAccess.canGetFieldsWhileInitializing.
# The original ran stubMethod() and relied on the harness/VM initializing the ENTRY class; the clean
# core models entry-class init as part of invocation (a callee entry is a trigger, the top-level entry
# is Plan 9 Tasks 20+), so the trigger here is the sget itself -- same <clinit>, same three values.
.method public static getTestInt2()I
    .locals 1
    sget v0, Lstatic_fields_test;->testInt2:I
    return v0
.end method

# The sput ORDERING witness (Plan 9 Task 18): the <clinit> prelude must run BEFORE the triggering
# sput's write, or preparation/<clinit> would clobber the freshly written value. With the correct
# order the sget reads 0x2a; with a trigger-less sput the later sget's init overwrites testInt1 back
# to 0 and the method returns the wrong constant.
.method public static putThenGetTestInt1()I
    .locals 1
    const/16 v0, 0x2a
    sput v0, Lstatic_fields_test;->testInt1:I
    sget v0, Lstatic_fields_test;->testInt1:I
    return v0
.end method

# Port carrier for canAccessFieldAfterCollapsingMultiverse (Plan 9 Task 18): a tracked object stored
# into a static, a nested activation (this class's own <clinit>, as the sput's prelude) run in
# between, then a mutation THROUGH THE REGISTER after the store, then the field read back. The
# original used `new-instance ArrayList` + three invokes; allocation-with-<init> and ArrayList
# emulation are Tasks 20/Plan 11, so the tracked object here is an array and the mutation is an
# aput-object -- the identity claim (the field holds a reference, never a copy) is unchanged.
.method public static storeThenMutateThenGetArray()[Ljava/lang/String;
    .locals 3
    const/4 v0, 0x1
    new-array v0, v0, [Ljava/lang/String;
    sput-object v0, Lstatic_fields_test;->myArray:[Ljava/lang/String;
    const/4 v1, 0x0
    const-string v2, "added item!"
    aput-object v2, v0, v1
    sget-object v0, Lstatic_fields_test;->myArray:[Ljava/lang/String;
    return-object v0
.end method

.method public static stubMethod2()V
    .locals 0
    invoke-static {}, Lstatic_fields_test;->stubMethod()V
    return-void
.end method

.method static callMethodWhichCallsAnotherMethodThenGetsField()V
    .locals 2

    new-instance v0, Ljava/util/ArrayList;
    sput-object v0, Lstatic_fields_test;->myList:Ljava/util/List;

    const/4 v1, 0x5
    invoke-direct {v0, v1}, Ljava/util/ArrayList;-><init>(I)V

    invoke-static {}, Lstatic_fields_test;->stubMethod2()V
    const-string v1, "added item!"
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    sget-object v0, Lstatic_fields_test;->myList:Ljava/util/List;

    return-void
.end method

.method static callMethodThenGetField()V
    .locals 2

    new-instance v0, Ljava/util/ArrayList;
    sput-object v0, Lstatic_fields_test;->myList:Ljava/util/List;

    const/4 v1, 0x5
    invoke-direct {v0, v1}, Ljava/util/ArrayList;-><init>(I)V

    invoke-static {}, Lstatic_fields_test;->stubMethod()V
    const-string v1, "added item!"
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    sget-object v0, Lstatic_fields_test;->myList:Ljava/util/List;

    return-void
.end method
