.class public Lspine/spine;
.super Ljava/lang/Object;

# const + return: the smallest possible execution
.method public static returnFive()I
    .registers 1
    const/4 v0, 0x5
    return v0
.end method

# An unknown parameter forks two paths that return the same value. The duplicate path results are distinct
# execution outcomes even though Outcome value equality says they look the same.
.method public static returnsFiveOnBothUnknownBranches(I)I
    .locals 1
    const/4 v0, 0x5
    if-eqz p0, :same
    return v0
    :same
    return v0
.end method

# The guest allocates and initializes this throwable before it escapes. The public outcome must carry its
# concrete tracked HeapRef, not reconstruct a class/message summary or degrade it to Unknown.
.method public static createsAndThrowsRuntimeException()V
    .locals 1
    new-instance v0, Ljava/lang/RuntimeException;
    invoke-direct {v0}, Ljava/lang/RuntimeException;-><init>()V
    throw v0
.end method

# One unknown branch returns normally and the other allocates and throws. Public outcomes must retain both
# path kinds without assigning semantic meaning to traversal order.
.method public static returnsOrThrows(I)I
    .locals 1
    if-eqz p0, :throw
    const/4 v0, 0x5
    return v0
  :throw
    new-instance v0, Ljava/lang/RuntimeException;
    invoke-direct {v0}, Ljava/lang/RuntimeException;-><init>()V
    throw v0
.end method

# Both unknown branches allocate the same throwable type through the same constructor. The outcomes look equal
# by type but represent two allocations and therefore require two HeapRefs.
.method public static throwsTwoEqualLookingExceptions(I)V
    .locals 1
    if-eqz p0, :second
    new-instance v0, Ljava/lang/RuntimeException;
    invoke-direct {v0}, Ljava/lang/RuntimeException;-><init>()V
    throw v0
  :second
    new-instance v0, Ljava/lang/RuntimeException;
    invoke-direct {v0}, Ljava/lang/RuntimeException;-><init>()V
    throw v0
.end method

# Three unknown paths end differently: one returns, one throws a guest exception, and one enters a configured
# host boundary that aborts. The resulting partial graph must retain both completed guest outcomes.
.method public static returnsThrowsOrCallsHost(II)I
    .locals 1
    if-eqz p0, :not_return
    const/4 v0, 0x7
    return v0
  :not_return
    if-eqz p1, :host
    new-instance v0, Ljava/lang/RuntimeException;
    invoke-direct {v0}, Ljava/lang/RuntimeException;-><init>()V
    throw v0
  :host
    invoke-static {}, Lorg/cf/smalivm/core/ReflectBackendFixture;->throwNested()V
    const/4 v0, 0x0
    return v0
.end method

# return-void: terminates with no value
.method public static returnNothing()V
    .registers 1
    const/4 v0, 0x7
    return-void
.end method

# nop sled then const: proves plain fallthrough across several addresses
.method public static nopThenConst()I
    .registers 1
    nop
    nop
    const/16 v0, 0x2a
    return v0
.end method

# goto: proves a jump transition and that the skipped address is never reached
.method public static gotoSkip()I
    .registers 1
    const/4 v0, 0x1
    goto :end
    const/4 v0, 0x2
    :end
    return v0
.end method

# move chain: v0 -> v1 -> v2
.method public static moveChain()I
    .registers 3
    const/16 v0, 0x9
    move v1, v0
    move v2, v1
    return v2
.end method

# const-wide + return-wide: proves the two-slot wide invariant end to end
.method public static returnWide()J
    .registers 2
    const-wide v0, 0x123456789L
    return-wide v0
.end method

# ---------------------------------------------------------------------------------------
# The five const forms below exist so that EVERY const opcode OpBuilder dispatches is
# decoded by a real test, not just the four the original fixtures happened to use. See
# OpBuilderTest's type-tag parity test.
#
# Editing note for whoever touches this file next: whole-method insertions anywhere in
# this file are address-safe, because smali code addresses are METHOD-LOCAL -- adding,
# removing or reordering entire methods never shifts another method's addresses. Editing
# instructions INSIDE an existing method is what shifts them, and several tests pin exact
# addresses.
# ---------------------------------------------------------------------------------------

# const-wide/16: a SIZED wide form. Dalvik has four const-wide opcodes and the engine tags
# the plain `const-wide` as D but every sized form as J, so this pins the other side of
# that split.
.method public static returnWideSixteen()J
    .registers 2
    const-wide/16 v0, 0x2a
    return-wide v0
.end method

# const-wide/32: the DANGEROUS wide form. It shares Format31i with the plain `const`, and
# Instruction31i implements NarrowLiteralInstruction, so if this opcode ever fell through
# to OpBuilder's narrow branch NOTHING would throw: the literal would be truncated to 32
# bits, tagged I, and Frame would stop reserving the pair's high half. Silent, so it needs
# a test.
.method public static returnWideThirtyTwo()J
    .registers 2
    const-wide/32 v0, 0x1b7740
    return-wide v0
.end method

# const-wide/high16: the LOUD wide form. Instruction21lh implements LongHatLiteralInstruction
# but NOT NarrowLiteralInstruction, so the same fall-through mistake throws instead of
# corrupting a value. Kept for completeness, and to document the asymmetry with /32 above.
.method public static returnWideHighSixteen()J
    .registers 2
    const-wide/high16 v0, 0x4242000000000000L
    return-wide v0
.end method

# const: the plain 32-bit narrow form (Format31i, same format as const-wide/32).
.method public static constThirtyTwo()I
    .registers 1
    const v0, 0x42424242
    return v0
.end method

# const/high16: narrow, and the literal is the hat shifted left 16 (Format21ih).
.method public static constHighSixteen()I
    .registers 1
    const/high16 v0, 0x42420000
    return v0
.end method

# const-string
.method public static returnString()Ljava/lang/String;
    .registers 1
    const-string v0, "spine"
    return-object v0
.end method

# throw an exception caught by a typed handler in the same method
.method public static throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;
    .registers 2
    :try_start
    throw v1
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :handler
    :handler
    move-exception v0
    return-object v0
.end method

# throw an exception with no handler in this method
.method public static throwUncaught(Ljava/lang/RuntimeException;)V
    .registers 2
    throw v1
.end method

# TWO typed handlers over the same range, so an unknown thrown type has more than one feasible
# handler and the fork ORDER becomes observable. The declaration order is deliberately the REVERSE
# of the handler-address order: `:later` (address 3) is declared first, `:earlier` (address 1)
# second. Without that inversion, "explored in declaration order" and "explored in ascending
# address order" would be the same sequence and neither could be pinned by a test.
#
# The two handlers also end differently on purpose: `:earlier` returns the CAUGHT value, which
# proves the thrown object reached a *forked* handler's own frame, while `:later` returns a
# constant, so the two paths are distinguishable by outcome value and not just by address.
.method public static throwTwoHandlers(Ljava/lang/Object;)Ljava/lang/Object;
    .registers 2
    :try_start
    throw v1
    :try_end
    .catch Ljava/io/IOException; {:try_start .. :try_end} :later
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :earlier
    :earlier
    move-exception v0
    return-object v0
    :later
    const-string v0, "later"
    return-object v0
.end method

# a covering catch-all alongside a typed clause. Two things need this fixture: catch-all
# PRECEDENCE for a known type that no typed clause matches, and -- the load-bearing one -- the
# catch-all ESCAPE rule for an unknown type (HandlerResolution.kt's `candidateHandlers` contract):
# a catch-all guarantees the exception is handled in this method, so `Outcome.Threw` is NOT a
# feasible path here even though the thrown type is unknown.
.method public static throwCatchAll(Ljava/lang/Object;)Ljava/lang/Object;
    .registers 2
    :try_start
    throw v1
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :typed
    .catchall {:try_start .. :try_end} :all
    :typed
    move-exception v0
    return-object v0
    :all
    const-string v0, "all"
    return-object v0
.end method

# `const/4 v0, 0x0` is the null-reference idiom, and with this build's op set it produces
# Known(0, "I") -- an INT, which can never be a throwable's runtime class. Dalvik throws a fresh
# NullPointerException here (a RuntimeException, so the handler below catches it), which means the
# handler is a feasible path even though nothing about the register's value says so. Resolving by the
# register's declared type would explore ZERO of it on a run that still reports Complete. This
# fixture exists so that case is reachable from ordinary smali, not only from a seeded frame.
.method public static throwNullConst()Ljava/lang/Object;
    .registers 1
    const/4 v0, 0x0
    :try_start
    throw v0
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :handler
    :handler
    move-exception v0
    return-object v0
.end method

# The HANDLER-REACHABILITY half of `throwNullConst` above, which one handler cannot state. Two typed
# clauses cover the same null-idiom throw and only ONE of them catches a NullPointerException, so
# "resolves definitely to the NPE handler" and "forks every covering handler" reach DIFFERENT address
# sets here and a test can tell them apart. Two deliberate choices in the layout, both of which make the
# test discriminate where an easier layout would not:
#
#  - the NON-matching clause (`Ljava/io/IOException;`) is declared first AND assembled first, so its
#    handler lands at address 2 -- which is also `nextAddress(1)`. A router that treated a handler as the
#    throw's fall-through would reach exactly the address this fixture asserts is unreached.
#  - the matching clause's handler is therefore NOT adjacent to the throw, so reaching it is evidence of
#    real try-table resolution rather than of falling through.
#
# Addresses, derived from the assembled instructions and not assumed: @0 const/4 (11n, 1 unit), @1 throw
# (11x, 1 unit), try range [1, 2), @2 const-string (21c, 2 units), @4 return-object (11x, 1 unit),
# @5 move-exception (11x, 1 unit), @6 return-object. So the two handler addresses are 2 and 5. Tests read
# both off the try-block table by catch type rather than hard-coding them, and then check these numbers.
#
# Note that the assembler emits ONE try-block table entry per `.catch` directive even though both cover
# the same range, so `methodView.tryBlocks` has TWO entries here and a test must flatten `catches` across
# them rather than reach for `tryBlocks.single()`.
.method public static throwNullConstTwoHandlers()Ljava/lang/Object;
    .registers 1
    const/4 v0, 0x0
    :try_start
    throw v0
    :try_end
    .catch Ljava/io/IOException; {:try_start .. :try_end} :not_an_npe
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :npe
    :not_an_npe
    const-string v0, "io"
    return-object v0
    :npe
    move-exception v0
    return-object v0
.end method

# The SAME method with ONE NIBBLE changed -- 0x5 instead of 0x0 -- and the point is that the outcome does
# NOT depend on that nibble. Both are a definite, message-less NullPointerException, because ART's `THROW()`
# fetches its operand with `GetVRegReference`, `ShadowFrame` keeps a PARALLEL reference array, and every
# primitive write clears the matching slot (`References()[i].Clear()` in SetVReg/SetVRegFloat/SetVRegLong/
# SetVRegDouble, shadow_frame.h:190-228; ART's own prose at :401-408: "when a primitive is stored in vX, the
# second (reference) part of the array will be null"). So a primitive-typed register reads as null whatever
# its VALUE, and 0x5 is the same case as 0x0.
#
# THIS FIXTURE'S HEADER USED TO SAY THE OPPOSITE -- that ART "reinterprets the 5 as an object pointer and
# dereferences it", making the case undefined behavior and an ambiguous fork. That reading followed `THROW()`
# without following `GetVRegReference` one level down, and it is corrected here rather than deleted so nobody
# restores it. The fixture is kept, and is now MORE valuable than under the old reading: it is the only place
# a NON-zero primitive throw is driven from real bytecode, so it is what fails if the predicate ever narrows
# back to `isDalvikNull`. Same addresses as above (handlers at 2 and 5).
.method public static throwNonZeroConstTwoHandlers()Ljava/lang/Object;
    .registers 1
    const/4 v0, 0x5
    :try_start
    throw v0
    :try_end
    .catch Ljava/io/IOException; {:try_start .. :try_end} :not_a_throwable
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :maybe
    :not_a_throwable
    const-string v0, "io"
    return-object v0
    :maybe
    move-exception v0
    return-object v0
.end method

# a catch type that is syntactically valid but NOT LOADABLE: no `Lspine/absent;` exists in the
# test smali set or the framework set, which is the normal case in a real obfuscated APK. The
# subtype question "is the thrown type a Lspine/absent;?" is therefore unanswerable, and
# ClassIndex reaches the class model unguarded, so the class model raises rather than answering.
# This fixture pins which way that resolves (see ThrowRoutingTest: ambiguity, not a host failure).
.method public static throwUnloadableCatchType(Ljava/lang/RuntimeException;)Ljava/lang/Object;
    .registers 2
    :try_start
    throw v1
    :try_end
    .catch Lspine/absent; {:try_start .. :try_end} :handler
    :handler
    move-exception v0
    return-object v0
.end method

# Former unsupported-opcode fixture. `add-int` is now supported.
# `add-int` is implemented in this plan, at which point this method becomes an ordinary supported
# one. Kept because it then makes a free differential fixture (two consts and an add, all values
# known), and because deleting it would churn the addresses nothing else depends on.
.method public static unsupportedOpcode()I
    .registers 2
    const/4 v0, 0x3
    const/4 v1, 0x4
    add-int v0, v0, v1
    return v0
.end method

# A clearly named MALFORMED-OPERAND fixture for scheduler and trust-gate tests. `filled-new-array` itself
# is supported and appears in OpBuilder.supportedOpcodes. Its `[J` operand is SPEC-forbidden because [BC]
# permits only single-word contents, so this exact instruction decodes to UnsupportedOp. This is not a
# capability ratchet and must not be registered as a supported-dialect differential fixture.
# Addresses (derived from the assembled instructions, not assumed): @0 const/4 (Format11n, 1 unit),
# @1 filled-new-array (Format35c, 3 units), @4 return (Format11x, 1 unit).
.method public static unsupportedWideFilledArray()I
    .registers 1

    const/4 v0, 0x3

    filled-new-array {v0}, [J

    return v0
.end method

# an unbounded backward goto: the ONLY way to revisit an address with the opcodes this plan
# implements, and therefore the only fixture that can actually exceed a per-address visit budget.
# Without it the address-visit limit has no honest test -- every other method here is straight-line,
# so a budget of 1 is never exceeded and an "assert Partial" would only pass if the limit tripped
# one visit too early. The label is on the nop, not on the goto: a goto with a zero offset is an
# encoding Dalvik rejects.
.method public static spinForever()V
    .registers 1
    :loop
    nop
    goto :loop
.end method

# a loop whose back-edge register (v1) is REASSIGNED every iteration: proves the Task 6 piling
# decision's soundness half, not just its visibility half. With a tight per-address budget, the
# SECOND visit to the move at :loop is limit-aborted before its own op ever runs, so its Frame is
# still the FIRST iteration's post-goto snapshot (v1 == 0x2) rather than genuine post-op state.
# v1 is the discriminating register: it changes between the executed visit's value (0x1, read by
# the move before the const below it ever runs) and the aborted revisit's stale value (0x2, read
# after that const already ran once) -- so an implementation that let the aborted node vote would
# see v1 disagree with itself and collapse a sound single-writer value to Unknown. v0 is
# deliberately NOT the proof register: nothing rewrites it between "just after visit 1's move" and
# "right before visit 2's move would run", so it coincidentally reads the same either way and a
# filtering bug there would go unnoticed.
.method public static loopRevisitChangesRegister()V
    .registers 2
    const/4 v1, 0x1
    :loop
    move v0, v1
    const/4 v1, 0x2
    goto :loop
.end method

# control flow that runs off the end of the method: a lone nop can fall through, but there is no
# next address to fall through TO. Dalvik would reject this method, but obfuscators emit malformed
# and unreachable tails, so the scheduler's containment of it needs a deterministic test rather than
# depending on some other fixture happening to be malformed.
.method public static fallsOffTheEnd()V
    .registers 1
    nop
.end method

# a KNOWN-type throw at a NON-ZERO address, inside a try block that does NOT cover address 0.
# Every other throw fixture here throws at address 0, so the session's wiring of the throw address
# into `resolveHandler` was unpinned: passing a constant 0 instead of the throwing node's own
# address resolved every one of them identically. This method discriminates -- the `nop` pushes the
# throw to address 1 and the try range is [1, 2), so resolving at 0 finds no covering block and
# reports the throw as escaping a method Dalvik provably catches it in: an unexplored feasible path
# on a run that still says Complete. The reverse shape (a try block covering 0 but not the throw)
# fabricates a caught path Dalvik never takes.
.method public static throwCaughtAfterNop(Ljava/lang/RuntimeException;)Ljava/lang/Object;
    .registers 2
    nop
    :try_start
    throw v1
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :handler
    :handler
    move-exception v0
    return-object v0
.end method

# instance method with a NON-TRAILING wide parameter: pins the two silent
# register-layout traps at once — `this` occupying p0, and a wide (J) parameter
# consuming TWO registers so every parameter after it shifts by 2, not 1.
.method public instanceWideParams(JLjava/lang/String;I)I
    .registers 8
    const/4 v0, 0x7
    return v0
.end method

# identity: returns v0 unchanged, with no opcode in between. Exists so the migration harness
# (CoreTesterTest) can drive CoreTester.test's ARRAY comparison through a real completed run without
# needing an array opcode -- new-array/aget/aput are Plan 7's, and every other array-typed return in
# this file needs one of them. v0 is read without ever being assigned by an instruction in this
# method, mirroring Laget_test;'s locals-only, externally-seeded style (`getUninitializedInt()V`):
# CoreTester seeds it before the run starts, so nothing here needs to.
.method public static identity()Ljava/lang/Object;
    .registers 1
    return-object v0
.end method
