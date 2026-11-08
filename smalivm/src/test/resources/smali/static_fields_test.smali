.class public Lstatic_fields_test;
.super Ljava/lang/Object;

.field private static alwaysZero:I
.field private static testInt1:I
.field private static testInt2:I

.field private static myList:Ljava/util/List;

# A static holding a tracked object. An array is used because it needs no `<init>` or emulation;
# the identity claims are the same as for any object.
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

# Trigger for StaticFieldAccessTest.anInitializerReadsItsOwnPreparedStaticsBeforeWritingOthers: the sget itself initializes the
# class, and the `<clinit>` reads its own statics mid-initialization.
.method public static getTestInt2()I
    .locals 1
    sget v0, Lstatic_fields_test;->testInt2:I
    return v0
.end method

# sput ordering witness: the `<clinit>` prelude runs before the triggering sput's write. In order,
# the sget reads 0x2a; a trigger-less sput would let the later sget's init reset testInt1 to 0.
.method public static putThenGetTestInt1()I
    .locals 1
    const/16 v0, 0x2a
    sput v0, Lstatic_fields_test;->testInt1:I
    sget v0, Lstatic_fields_test;->testInt1:I
    return v0
.end method

# Carrier for StaticFieldAccessTest.aStaticHoldsAReferenceSoAMutationThroughTheRegisterIsVisibleThroughTheField: a tracked array is
# stored into a static, this class's `<clinit>` runs as the sput's prelude, the array is mutated
# through the register, and the field is read back. The field holds a reference, never a copy.
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
