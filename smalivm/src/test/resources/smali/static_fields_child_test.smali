# Plan 9 Task 18: the REFERENCING half of the inherited-static pair, and the two-initializer carrier
# for the ported TestStaticFieldAccess.canAccessFieldAfterCollapsingMultiverseTwice. Extends a class
# whose <clinit> is real, and declares its own, so one trigger on this class runs TWO nested
# activations (ancestors first -- adjudication 4's ordering).
.class public Lstatic_fields_child_test;
.super Lstatic_fields_parent_test;

.field public static childTouched:I
.field public static myArray:[Ljava/lang/String;

.method public static constructor <clinit>()V
    .locals 1
    const/4 v0, 0x1
    sput v0, Lstatic_fields_child_test;->childTouched:I
    return-void
.end method

# References the PARENT's field through the CHILD's name -- legal dex, javac never emits it, and the
# resolution walk is the only thing that can answer it. The sget initializes the parent (the declaring
# class) and must NOT initialize this class.
.method public static getParentTouchedThroughChildReference()I
    .locals 1
    sget v0, Lstatic_fields_child_test;->parentTouched:I
    return v0
.end method

# Two nested activations between the allocation and the read-back: the sput-object triggers this
# class's init, which runs the parent's <clinit> and then this class's own, each as its own
# activation, before the write lands. The mutation-after-store and field/register identity claims are
# storeThenMutateThenGetArray's (static_fields_test.smali), one initializer deeper.
.method public static storeMutateGetThroughTwoInitializers()[Ljava/lang/String;
    .locals 3
    const/4 v0, 0x1
    new-array v0, v0, [Ljava/lang/String;
    sput-object v0, Lstatic_fields_child_test;->myArray:[Ljava/lang/String;
    const/4 v1, 0x0
    const-string v2, "added item!"
    aput-object v2, v0, v1
    sget-object v0, Lstatic_fields_child_test;->myArray:[Ljava/lang/String;
    return-object v0
.end method
