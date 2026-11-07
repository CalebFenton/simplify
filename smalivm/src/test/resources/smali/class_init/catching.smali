.class public Lclass_init/catching;
.super Ljava/lang/Object;

# ---------------------------------------------------------------------------------------
# A `<clinit>` THAT CATCHES ITS OWN EXCEPTION (Plan 9 Task 16). This is the fixture that
# pins WHEN the `Failed` mark is applied, which is a phase-ordering question rather than a
# lifecycle one.
#
# `ExecutionSession.commit` phase 1 copies every successor's frame from the node's post-op
# frame; phase 2 does the terminal writes. So a `Failed` mark made where the escape is
# DECIDED (`escapeEnding`, during planning) would land in the frame of every HANDLER
# successor of the same throw -- i.e. on paths where the initializer catches its exception
# and goes on to `return-void`, completing initialization. Those paths would then read the
# class as `Failed` and skip the rest of its initialization forever. Wrong in the worst
# direction, on a run that still says `Complete` for that path.
#
# The `nop` at @0 is a STUB SITE: `ClassInitTest` replaces it with a throw of an
# UNRESOLVABLE value, which is the one shape where a caught frame and an escaping frame both
# exist for one op -- handler search cannot tell whether the clause covers the throw, so it
# explores every covering handler AND the escape (`ExecutionSession.planThrow`). The
# fixture's own `throw` could not serve: `const/4` + `throw` is a DEFINITE
# NullPointerException, and a definite throw with a covering handler produces no escape at
# all.
#
# The catch is `RuntimeException` and NOT a catch-all, deliberately: a covering catch-all
# would make the escape infeasible (`escapesUnhandled`) and there would be no `Failed` mark
# to mis-place.
#
# Addresses: @0 nop (10x, 1 unit), @1 return-void, @2 move-exception (11x, 1), @3 return-void.
# ---------------------------------------------------------------------------------------

.method public static constructor <clinit>()V
    .registers 1
    :try_start
    nop
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :handler
    return-void

    :handler
    move-exception v0
    return-void
.end method
