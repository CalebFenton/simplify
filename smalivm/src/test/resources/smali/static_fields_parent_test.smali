# The declaring half of the inherited-static pair. `parentTouched` is referenced through the subclass
# (static_fields_child_test.smali), so a `sget` naming the child resolves to this class's cell and
# initializes this class: [JVMS] 5.5 initializes the declaring class, not the named one.
.class public Lstatic_fields_parent_test;
.super Ljava/lang/Object;

.field public static parentTouched:I

.method public static constructor <clinit>()V
    .locals 1
    const/4 v0, 0x1
    sput v0, Lstatic_fields_parent_test;->parentTouched:I
    return-void
.end method
