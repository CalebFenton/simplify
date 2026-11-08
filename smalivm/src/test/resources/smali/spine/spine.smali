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

# The const forms below give every const opcode OpBuilder dispatches a real decode test (OpBuilderTest's
# type-tag parity test). Addresses are method-local: whole-method insertions are address-safe, editing
# instructions inside a method is not, and tests pin exact addresses.

# const-wide/16: a sized wide form. The engine tags plain `const-wide` as D and every sized form
# as J, so this pins the sized side of that split.
.method public static returnWideSixteen()J
    .registers 2
    const-wide/16 v0, 0x2a
    return-wide v0
.end method

# const-wide/32 shares Format31i with plain `const`, and Instruction31i implements
# NarrowLiteralInstruction, so a fall-through to OpBuilder's narrow branch would silently truncate the
# literal to 32 bits, tag it I, and stop reserving the pair's high half.
.method public static returnWideThirtyTwo()J
    .registers 2
    const-wide/32 v0, 0x1b7740
    return-wide v0
.end method

# const-wide/high16: Instruction21lh implements LongHatLiteralInstruction but not
# NarrowLiteralInstruction, so the same fall-through mistake throws instead of corrupting a value.
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

# Two typed handlers over the same range, so an unknown thrown type has two feasible handlers and the
# fork order is observable. Declaration order is the reverse of address order (`:later` at 3 first,
# `:earlier` at 1 second), so "declaration order" and "ascending address order" differ.
#
# `:earlier` returns the caught value, proving the thrown object reached a forked handler's frame;
# `:later` returns a constant, so the paths differ by outcome value as well as address.
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

# A covering catch-all alongside a typed clause: catch-all precedence for a known type no typed clause
# matches, and for an unknown type the catch-all guarantees the exception is handled here, so
# `Outcome.Threw` is not a feasible path.
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

# `const/4 v0, 0x0` is the null-reference idiom and produces Known(0, "I"), an int. Dalvik throws a
# fresh NullPointerException here, which the RuntimeException handler catches, so the handler is
# feasible although the register's declared type says nothing about it. This reaches that case from
# ordinary smali rather than a seeded frame.
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

# Handler reachability for the null-idiom throw: two typed clauses cover it and only one catches a
# NullPointerException, so "resolves definitely to the NPE handler" and "forks every covering handler"
# reach different addresses.
#  - the non-matching clause (`Ljava/io/IOException;`) is declared and assembled first, so its handler is
#    at address 2, which is also `nextAddress(1)`: a router treating a handler as the throw's
#    fall-through reaches the address the test asserts is unreached.
#  - the matching handler is not adjacent to the throw, so reaching it shows real try-table resolution.
#
# Addresses: @0 const/4 (11n, 1 unit), @1 throw (11x, 1 unit), try range [1, 2), @2 const-string (21c,
# 2 units), @4 return-object (11x, 1 unit), @5 move-exception (11x, 1 unit), @6 return-object. Handlers
# are at 2 and 5; tests read them off the try-block table by catch type.
#
# The assembler emits one try-block entry per `.catch` directive, so `methodView.tryBlocks` has two
# entries here; tests flatten `catches` across them.
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

# The same method with 0x5 instead of 0x0; the outcome does not depend on that nibble. Both are a
# definite, message-less NullPointerException: ART's `THROW()` reads its operand with `GetVRegReference`,
# and every primitive write clears the register's reference slot, so a primitive-typed register reads as
# null whatever its value (DALVIK-SEMANTICS.md, Coercion and register bits). This is the only non-zero
# primitive throw driven from real bytecode. Same addresses as above (handlers at 2 and 5).
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

# A catch type that is syntactically valid but not loadable: no `Lspine/absent;` exists in the test or
# framework set, the normal case in an obfuscated APK. Whether the thrown type matches is unanswerable,
# and ThrowRoutingTest pins that this is ambiguity, not a host failure.
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

.method public static addIntOfKnownConstants()I
    .registers 2
    const/4 v0, 0x3
    const/4 v1, 0x4
    add-int v0, v0, v1
    return v0
.end method

# A malformed-operand fixture for scheduler and trust-gate tests. `filled-new-array` is supported, but
# [BC] permits only single-word contents, so the `[J` operand decodes to UnsupportedOp. Do not register
# it as a supported-dialect differential fixture.
# Addresses (derived from the assembled instructions, not assumed): @0 const/4 (Format11n, 1 unit),
# @1 filled-new-array (Format35c, 3 units), @4 return (Format11x, 1 unit).
.method public static unsupportedWideFilledArray()I
    .registers 1

    const/4 v0, 0x3

    filled-new-array {v0}, [J

    return v0
.end method

# An unbounded backward goto, the fixture that exceeds a per-address visit budget. The label is on the
# nop because Dalvik rejects a goto with a zero offset.
.method public static spinForever()V
    .registers 1
    :loop
    nop
    goto :loop
.end method

# A loop whose back-edge register v1 is reassigned every iteration. With a tight per-address budget the
# second visit to the move at :loop is limit-aborted before its op runs, so its frame is a stale
# snapshot (v1 == 0x2) while the executed visit read 0x1. If the aborted node voted in consensus, v1
# would collapse from a sound single-writer value to Unknown. v0 cannot show this: nothing rewrites it
# between the two visits.
.method public static loopRevisitChangesRegister()V
    .registers 2
    const/4 v1, 0x1
    :loop
    move v0, v1
    const/4 v1, 0x2
    goto :loop
.end method

# Control flow that runs off the end of the method: the nop has no next address. Dalvik rejects this,
# but obfuscators emit malformed tails, so the scheduler's containment needs a deterministic test.
.method public static fallsOffTheEnd()V
    .registers 1
    nop
.end method

# A known-type throw at address 1, inside a try range [1, 2) that does not cover address 0. Handler
# resolution must use the throwing node's own address: resolving at 0 would report an escape from a
# method Dalvik catches it in.
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

# Instance method with a non-trailing wide parameter: `this` occupies p0, and the wide (J) parameter
# takes two registers, so every later parameter shifts by 2.
.method public instanceWideParams(JLjava/lang/String;I)I
    .registers 8
    const/4 v0, 0x7
    return v0
.end method

# identity: returns v0 unchanged with no opcode in between, so CoreTesterTest can drive
# CoreTester.test's array comparison through a completed run with no array opcode. CoreTester seeds v0
# before the run.
.method public static identity()Ljava/lang/Object;
    .registers 1
    return-object v0
.end method
