.class public Lclass_init/no_clinit;
.super Ljava/lang/Object;

# ---------------------------------------------------------------------------------------
# THE COMMON CASE (Plan 9 Task 16, design decision 1): a class that declares NO `<clinit>`
# at all, so initializing it transitions Uninitialized -> Initialized with NO nested run.
#
# Its superclass chain is `Ljava/lang/Object;` alone, which is a FRAMEWORK class that also
# declares no `<clinit>` (`resources/framework/smali/android-25/java/lang/Object.smali` has
# none) -- so this fixture is also what pins the precision half of resolved decision 7: a
# framework class with nothing to skip must NOT raise the trigger to STRONG, or every
# class-init trigger in the system would, since Object is on every chain.
# ---------------------------------------------------------------------------------------

.method public static touch()V
    .registers 1
    return-void
.end method
