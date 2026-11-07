.class public Lclass_init/heir;
.super Lclass_init/grandparent;

# ---------------------------------------------------------------------------------------
# A class that declares NO `<clinit>` while its SUPERCLASS declares one -- the exact shape
# that separates a declared-only initializer lookup from the ancestor-searching one, and the
# only shape in which the oracle's defect is visible.
#
# `docs/architecture/execution-engine.md:159`: "Because `VirtualClass.getMethod()` searches
# ancestors, a class with no **declared** `<clinit>` can instead resolve an ancestor
# initializer ... Static initializers are not inherited, so this lookup conflates ordinary
# method inheritance with class initialization." So `ClassIndex.lookupMethod(heir,
# "<clinit>()V")` answers `grandparent`'s initializer, and a scheduler using it would run
# grandparent's `<clinit>` believing it had run heir's -- marking heir initialized while its
# own statics were never touched.
#
# `ClassIndex.lookupClassInitializer` is the declared-only lookup that answers correctly, and
# this fixture is what makes the difference between the two an assertion rather than a claim.
# ---------------------------------------------------------------------------------------

.method public static touch()V
    .registers 1
    return-void
.end method
