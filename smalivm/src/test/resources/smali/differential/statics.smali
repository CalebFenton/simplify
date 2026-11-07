# The static-field family's WITH-<clinit> differential class (Plan 9 Task 18) -- the shape where
# adjudication 5 genuinely bites. The oracle seeds `.field ... = literal` initializers ONLY for a
# class with NO <clinit> (VirtualMachine.kt:479-487 vs :549-555), so when THIS class is initialized
# mid-run it discards `literalUntouchedByClinit`'s 0x2a -- a constant sitting right there in the
# dex -- and reads it back as Unknown with an "undefined field" log (ExecutionState.kt:258-262). The
# clean core seeds literals for every class at JVMS 5.4.2 preparation, so the same read is
# Known(0x2a, I): registered as a NEW_CORE_MORE_PRECISE divergence, never to be reconciled.
#
# The readers live in Ldifferential/statics_reader; ON PURPOSE, and moving them here would make the
# divergence unobservable -- measured, not guessed: the oracle's spawnEntrypointState calls
# initializeClassState on the ENTRY method's own class unconditionally (VirtualMachine.kt:526-528),
# which seeds every literal, so a same-class reader sees 0x2a on BOTH engines and the sweep reads
# adjudication 5 as an agreement. The defect is specifically about a class initialized DURING a run
# (the sget trigger path), which needs the trigger to come from another class.
.class public Ldifferential/statics;
.super Ljava/lang/Object;

.field public static assignedByClinit:I
.field public static literalUntouchedByClinit:I = 0x2a

.method public static constructor <clinit>()V
    .locals 1
    const/4 v0, 0x7
    sput v0, Ldifferential/statics;->assignedByClinit:I
    return-void
.end method
