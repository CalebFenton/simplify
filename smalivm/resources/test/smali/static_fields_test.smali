.class public Lstatic_fields_test;
.super Ljava/lang/Object;

.field private static alwaysZero:I
.field private static testInt1:I
.field private static testInt2:I

.field private static myList:Ljava/util/List;

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
