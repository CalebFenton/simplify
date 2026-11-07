# Plan 9 Task 18: the DECLARING half of the inherited-static pair. `parentTouched` is declared here
# and referenced through the subclass (static_fields_child_test.smali), so a `sget` naming the child
# must resolve to THIS class's cell and initialize THIS class -- [JVMS] 5.5: the class initialized is
# the one that declares the field, never the one the instruction named.
.class public Lstatic_fields_parent_test;
.super Ljava/lang/Object;

.field public static parentTouched:I

.method public static constructor <clinit>()V
    .locals 1
    const/4 v0, 0x1
    sput v0, Lstatic_fields_parent_test;->parentTouched:I
    return-void
.end method
