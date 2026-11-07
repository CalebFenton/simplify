.class Lbinary_math_test;
.super Ljava/lang/Object;


################ Integer ################
.method public static addInt()V
    .locals 2

    add-int v0, v0, v1

    return-void
.end method

.method public static addInt2Addr()V
    .locals 2

    add-int/2addr v0, v1

    return-void
.end method

.method public static addIntLit8()V
    .locals 1

    add-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static addIntLit16()V
    .locals 1

    add-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static mulInt()V
    .locals 2

    mul-int v0, v0, v1

    return-void
.end method

.method public static mulInt2Addr()V
    .locals 2

    mul-int/2addr v0, v1

    return-void
.end method

.method public static mulIntLit8()V
    .locals 1

    mul-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static mulIntLit16()V
    .locals 1

    mul-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static divInt()V
    .locals 2

    div-int v0, v0, v1

    return-void
.end method

.method public static divInt2Addr()V
    .locals 2

    div-int/2addr v0, v1

    return-void
.end method

.method public static divIntLit8()V
    .locals 1

    div-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static divIntLit16()V
    .locals 1

    div-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static divIntWithCatch()V
    .locals 2

    :try_start_0
    # Floating point percision literals do not throw ArithmeticException
    div-int v0, v0, v1 # @0

    :try_end_0
    .catch Ljava/lang/ArithmeticException; {:try_start_0 .. :try_end_0} :catch_0

    :return
    return-void # @2

    :catch_0
    move-exception v0 # @3

    goto :return # @4
.end method

.method public static remInt()V
    .locals 2

    rem-int v0, v0, v1

    return-void
.end method

.method public static remInt2Addr()V
    .locals 2

    rem-int/2addr v0, v1

    return-void
.end method

.method public static remIntLit8()V
    .locals 1

    rem-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static remIntLit16()V
    .locals 1

    rem-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static andInt()V
    .locals 2

    and-int v0, v0, v1

    return-void
.end method

.method public static andInt2Addr()V
    .locals 2

    and-int/2addr v0, v1

    return-void
.end method

.method public static andIntLit8()V
    .locals 1

    and-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static andIntLit16()V
    .locals 1

    and-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static orInt()V
    .locals 2

    or-int v0, v0, v1

    return-void
.end method

.method public static orInt2Addr()V
    .locals 2

    or-int/2addr v0, v1

    return-void
.end method

.method public static orIntLit8()V
    .locals 1

    or-int/lit8 v0, v0, 0xf

    return-void
.end method

.method public static orIntLit16()V
    .locals 1

    or-int/lit16 v0, v0, 0xff

    return-void
.end method

.method public static xorInt()V
    .locals 2

    xor-int v0, v0, v1

    return-void
.end method

.method public static xorInt2Addr()V
    .locals 2

    xor-int/2addr v0, v1

    return-void
.end method

.method public static xorIntLit8()V
    .locals 1

    xor-int/lit8 v0, v0, 0x3

    return-void
.end method

.method public static xorIntLit16()V
    .locals 1

    xor-int/lit16 v0, v0, 0x10

    return-void
.end method

.method public static shlInt()V
    .locals 2

    shl-int v0, v0, v1

    return-void
.end method

.method public static shlInt2Addr()V
    .locals 2

    shl-int/2addr v0, v1

    return-void
.end method

.method public static shlIntLit8()V
    .locals 1

    shl-int/lit8 v0, v0, 0x2

    return-void
.end method

.method public static shrInt()V
    .locals 2

    shr-int v0, v0, v1

    return-void
.end method

.method public static shrInt2Addr()V
    .locals 2

    shr-int/2addr v0, v1

    return-void
.end method

.method public static shrIntLit8()V
    .locals 1

    shr-int/lit8 v0, v0, 0x2

    return-void
.end method

.method public static ushrInt()V
    .locals 2

    ushr-int v0, v0, v1

    return-void
.end method

.method public static ushrInt2Addr()V
    .locals 2

    ushr-int/2addr v0, v1

    return-void
.end method

.method public static ushrIntLit8()V
    .locals 1

    ushr-int/lit8 v0, v0, 0x2

    return-void
.end method

.method public static subInt()V
    .locals 2

    sub-int v0, v0, v1

    return-void
.end method

.method public static subInt2Addr()V
    .locals 2

    sub-int/2addr v0, v1

    return-void
.end method

.method public static RSubInt()V
    .locals 2

    rsub-int v0, v0, 0xff

    return-void
.end method

.method public static RSubIntLit8()V
    .locals 1

    rsub-int/lit8 v0, v0, 0xf

    return-void
.end method




################ Long ################
.method public static addLong()V
    .locals 4

    add-long v0, v0, v2

    return-void
.end method

.method public static addLong2Addr()V
    .locals 4

    add-long/2addr v0, v2

    return-void
.end method

.method public static subLong()V
    .locals 4

    sub-long v0, v0, v2

    return-void
.end method

.method public static subLong2Addr()V
    .locals 4

    sub-long/2addr v0, v2

    return-void
.end method

.method public static mulLong()V
    .locals 4

    mul-long v0, v0, v2

    return-void
.end method

.method public static mulLong2Addr()V
    .locals 4

    mul-long/2addr v0, v2

    return-void
.end method

.method public static divLong()V
    .locals 4

    div-long v0, v0, v2

    return-void
.end method

.method public static divLong2Addr()V
    .locals 4

    div-long/2addr v0, v2

    return-void
.end method

# REGISTER LAYOUT, corrected. This was `.locals 2` with `div-long v0, v0, v1`, which is unfaithful
# whichever way it is read: `long` is wide, so the lhs pair is {v0,v1} and the rhs pair read from v1 is
# {v1,v2} -- v1 belongs to BOTH, and v2 does not exist under `.locals 2`. A `.locals` bump alone cannot
# fix it, because the encoded instruction still reads its rhs from v1: seeding v0 reserves v1 as the
# pair's high half, and seeding v1 then destroys the lhs (`Frame.clearWidePartner` nulls the low
# register of the pair whose high half is being overwritten), so the op would run on operands no test
# asked for. The rhs operand therefore moves to v2, matching every other wide method in this file
# (`divLong()V` and friends are already `v0, v0, v2` under `.locals 4`). Addresses are UNCHANGED --
# div-long is Instruction23x either way, so it is still 2 code units and the tail is still @2/@3/@4.
.method public static divLongWithCatch()V
    .locals 4

    :try_start_0
    div-long v0, v0, v2 # @0

    :try_end_0
    .catch Ljava/lang/ArithmeticException; {:try_start_0 .. :try_end_0} :catch_0

    :return
    return-void # @2

    :catch_0
    move-exception v0 # @3

    goto :return # @4
.end method

.method public static remLong()V
    .locals 4

    rem-long v0, v0, v2

    return-void
.end method

.method public static remLong2Addr()V
    .locals 4

    rem-long/2addr v0, v2

    return-void
.end method

.method public static andLong()V
    .locals 4

    and-long v0, v0, v2

    return-void
.end method

.method public static andLong2Addr()V
    .locals 4

    and-long/2addr v0, v2

    return-void
.end method

.method public static orLong()V
    .locals 4

    or-long v0, v0, v2

    return-void
.end method

.method public static orLong2Addr()V
    .locals 4

    or-long/2addr v0, v2

    return-void
.end method

.method public static xorLong()V
    .locals 4

    xor-long v0, v0, v2

    return-void
.end method

.method public static xorLong2Addr()V
    .locals 4

    xor-long/2addr v0, v2

    return-void
.end method

.method public static shlLong()V
    .locals 4

    shl-long v0, v0, v2

    return-void
.end method

.method public static shlLong2Addr()V
    .locals 4

    shl-long/2addr v0, v2

    return-void
.end method

.method public static shrLong()V
    .locals 4

    shr-long v0, v0, v2

    return-void
.end method

.method public static shrLong2Addr()V
    .locals 4

    shr-long/2addr v0, v2

    return-void
.end method

.method public static ushrLong()V
    .locals 4

    ushr-long v0, v0, v2

    return-void
.end method

.method public static ushrLong2Addr()V
    .locals 4

    ushr-long/2addr v0, v2

    return-void
.end method




################ Float ################
.method public static addFloat()V
    .locals 2

    add-float v0, v0, v1

    return-void
.end method

.method public static addFloat2Addr()V
    .locals 2

    add-float/2addr v0, v1

    return-void
.end method

.method public static subFloat()V
    .locals 2

    sub-float v0, v0, v1

    return-void
.end method

.method public static subFloat2Addr()V
    .locals 2

    sub-float/2addr v0, v1

    return-void
.end method

.method public static mulFloat()V
    .locals 2

    mul-float v0, v0, v1

    return-void
.end method

.method public static mulFloat2Addr()V
    .locals 2

    mul-float/2addr v0, v1

    return-void
.end method

.method public static divFloat()V
    .locals 2

    div-float v0, v0, v1

    return-void
.end method

.method public static divFloat2Addr()V
    .locals 2

    div-float/2addr v0, v1

    return-void
.end method

.method public static divFloatWithCatch()V
    .locals 2

    :try_start_0
    div-float v0, v0, v1 # @0

    :try_end_0
    .catch Ljava/lang/ArithmeticException; {:try_start_0 .. :try_end_0} :catch_0

    :return
    return-void # @2

    :catch_0
    move-exception v0 # @3

    goto :return # @4
.end method


.method public static remFloat()V
    .locals 2

    rem-float v0, v0, v1

    return-void
.end method

.method public static remFloat2Addr()V
    .locals 2

    rem-float/2addr v0, v1

    return-void
.end method




################ Double ################
.method public static addDouble()V
    .locals 4

    add-double v0, v0, v2

    return-void
.end method

.method public static addDouble2Addr()V
    .locals 4

    add-double/2addr v0, v2

    return-void
.end method

.method public static subDouble()V
    .locals 4

    sub-double v0, v0, v2

    return-void
.end method

.method public static subDouble2Addr()V
    .locals 4

    sub-double/2addr v0, v2

    return-void
.end method

.method public static mulDouble()V
    .locals 4

    mul-double v0, v0, v2

    return-void
.end method

.method public static mulDouble2Addr()V
    .locals 4

    mul-double/2addr v0, v2

    return-void
.end method

.method public static divDouble()V
    .locals 4

    div-double v0, v0, v2

    return-void
.end method

.method public static divDouble2Addr()V
    .locals 4

    div-double/2addr v0, v2

    return-void
.end method

# REGISTER LAYOUT, corrected -- the identical defect `divLongWithCatch()V` above carries, for the same
# reason (`double` is wide too). See that method's comment for the full derivation.
.method public static divDoubleWithCatch()V
    .locals 4

    :try_start_0
    div-double v0, v0, v2 # @0

    :try_end_0
    .catch Ljava/lang/ArithmeticException; {:try_start_0 .. :try_end_0} :catch_0

    :return
    return-void # @2

    :catch_0
    move-exception v0 # @3

    goto :return # @4
.end method

.method public static remDouble()V
    .locals 4

    rem-double v0, v0, v2

    return-void
.end method

.method public static remDouble2Addr()V
    .locals 4

    rem-double/2addr v0, v2

    return-void
.end method


################ Malformed tails ################

# A div-int whose ambiguous divisor forks, where the NORMAL fall-through target does not exist while the
# EXCEPTIONAL one has a real handler. Verifier-invalid on purpose: it is the shape that proves a fork
# does not discard its one buildable branch when the other target is malformed, which is expected input
# for a deobfuscator rather than a hypothetical.
#
# The handler sits BEFORE the try block and is jumped over, which is what makes the div-int genuinely
# the last instruction: @0 goto (1 unit), @1 move-exception (1), @2 return-void (1), @3 div-int (2), so
# the fall-through target is @5 and the method has no such address. Putting the handler AFTER the
# div-int instead -- the obvious shape -- does NOT produce this fixture's behavior: the fall-through
# would land on the `move-exception`, a perfectly buildable address, and nothing would be lost.
.method public static divIntFallsOffTheEnd()V
    .locals 2

    goto :start # @0

    :catch_0
    move-exception v0 # @1

    return-void # @2

    :start
    :try_start_0
    div-int v0, v0, v1 # @3 -- last instruction; nextAddress is @5, which does not exist
    :try_end_0
    .catch Ljava/lang/ArithmeticException; {:try_start_0 .. :try_end_0} :catch_0
.end method


################ Ambiguous fork decisions (differential witnesses) ################

# The two methods below are the differential witnesses for the ONE oracle defect at
# `opcode/BinaryMathOp.kt:64-67`, whose fork condition is `result.isUnknown && operator == DIV`. That
# reads the wrong two things, and it is wrong in BOTH directions, so it takes two fixtures to pin:
#
#   - `remIntUnknownDivisorWithCatch(I)V` -- `rem-int` by an unknown divisor. Dalvik throws
#     ArithmeticException for `rem` by zero exactly as for `div`, so the handler is FEASIBLE; the
#     oracle's DIV-only gate never schedules it and still reports the run complete.
#   - `divIntUnknownDividendWithCatch(I)V` -- `div-int` of an unknown dividend by a KNOWN non-zero
#     divisor. Only a zero divisor throws (`Integer.MIN_VALUE / -1` overflows silently), so the handler
#     is INFEASIBLE; the oracle's `result.isUnknown` gate -- true when EITHER operand is unknown --
#     schedules it anyway.
#
# WHY THE DIVISOR/DIVIDEND IS A PARAMETER RATHER THAN A SEEDED REGISTER: both engines seed a
# parameter to "unknown of its declared type" natively, so the ambiguity under test needs no help from
# the harness. Seeding it instead would mean each engine building its own unknown, and a seeding
# asymmetry would be indistinguishable from the fork divergence these fixtures exist to show. (The
# oracle cannot read an unseeded local at all: `ExecutionState.readRegister` is `peekRegister(r)!!`.)
#
# WHY `move-exception` TARGETS v1 AND NOT v0: the observed register must AGREE between the engines so
# the recorded divergence is the path set alone. `divIntWithCatch()V` above moves the caught exception
# into v0, which would make the fixture diverge on the register too -- and on the core those two paths
# join across `I` and `Ljava/lang/ArithmeticException;`, which `joinTypes` calls Incompatible, so the
# register difference would be a Top-projection artifact rather than an engine fact.
#
# Register layout, derived from the encoding rather than assumed: `.registers 3` with one `I`
# parameter puts the parameter at v2 (registerCount - parameterSize), leaving v0 for the seeded
# operand and v1 for the catch slot. Addresses: @0 div/rem-int (Instruction23x, 2 code units),
# @2 return-void (1), @3 move-exception (1), @4 goto (1) -- the same tail as `divIntWithCatch()V`.

.method public static remIntUnknownDivisorWithCatch(I)V
    .registers 3

    :try_start_0
    rem-int v0, v0, v2 # @0 -- v0 seeded, v2 the unknown parameter

    :try_end_0
    .catch Ljava/lang/ArithmeticException; {:try_start_0 .. :try_end_0} :catch_0

    :return
    return-void # @2

    :catch_0
    move-exception v1 # @3

    goto :return # @4
.end method

.method public static divIntUnknownDividendWithCatch(I)V
    .registers 3

    :try_start_0
    div-int v0, v2, v0 # @0 -- dividend v2 (unknown parameter), divisor v0 (seeded non-zero)

    :try_end_0
    .catch Ljava/lang/ArithmeticException; {:try_start_0 .. :try_end_0} :catch_0

    :return
    return-void # @2

    :catch_0
    move-exception v1 # @3

    goto :return # @4
.end method


################ Ambiguous operand widths ################

# A `div-int` whose divisor register was written by a `const-wide`: the raw is the 64-bit 2^32, whose
# LOW 32 BITS ARE ZERO. Dalvik reads a `div-int` operand as a 32-bit register, so the divisor it sees is
# 0 and it throws; reading the same raw at 64-bit width instead says "divisor 2^32, carry on". This is
# the shape that catches the divide-by-zero test being asked at a different width from the arithmetic,
# and it needs no seeded frame at all -- `const-wide` puts the value there, and `OpBuilder` tags it `D`
# with a `Long` raw (its own documented quirk), so the ambiguity is the real one Dalvik's overloaded
# const opcodes produce rather than a hand-built `Value`.
#
# Verifier-invalid on purpose, on the same grounds as `divIntFallsOffTheEnd()V` above: obfuscators emit
# register types the verifier would reject, and the engine must still route the path Dalvik takes.
.method public static divIntByWideZeroLowHalf()V
    .locals 4

    const/16 v0, 0xc            # @0 -- dividend 12
    const-wide v2, 0x100000000L # @2 -- 2^32: non-zero as a long, zero as an int
    div-int v0, v0, v2          # @7
    return-void                 # @9
.end method


################ Two independent syntheses (heap identity) ################

# `divIntTwiceByZeroWithCatch()V` -- two `div-int` instructions over the SAME known-zero divisor
# (v0=10, v1=0, per BinaryMathOpTest's seed), each synthesizing its OWN ArithmeticException. The claim
# under test (BinaryMathOpTest.twoSeparatelySynthesizedExceptionsStillDoNotAgree) is that the two
# syntheses land under DISTINCT heap ids and so do not `agreesWith` each other -- CARRY-FORWARD called
# that "avoidably imprecise"; it is correct instead, since two paths that each throw a fresh exception
# do not throw one object in Dalvik either.
#
# WHY THE CATCH TYPE IS DELIBERATELY UNLOADABLE rather than a matching `Ljava/lang/ArithmeticException;`
# clause (the shape every OTHER `*WithCatch` method in this file uses), which is the non-obvious part.
# A matching clause would make BOTH throws DEFINITELY caught (`ExecutionSession.planThrow`: a resolved
# handler means `escapes = false`), and `ThrowRoutingTest.handlerPathIsReachedAndTheThrowingPathDoesNotEscape`
# already pins that a definitely-caught throw never becomes an `Outcome.Threw` at all -- it is routed
# straight to the handler instead. Worse, with a matching clause the SECOND division could never even
# run: reaching it needs the FIRST throw to be caught (a KNOWN-zero divisor's op emits `[Throw]` alone,
# never a `Continue` -- see `BinaryMathOp.transitionsFor` -- so there is no other transition off it), and
# "caught" is exactly the case that never populates `Outcome.Threw`. A matching clause therefore caps
# this test's own `refs` list at ZERO elements: `assertEquals(0, 0)` would pass no matter what `HeapIds`
# did, which is exactly the vacuous-test failure mode the task brief warns about.
#
# An UNLOADABLE catch type (`Lbinary_math_test$Unloadable;`, a descriptor nothing in this tree defines)
# is genuinely AMBIGUOUS instead (`ThrowRoutingTest.anUnloadableCatchTypeIsAmbiguityNotAHostFailure`):
# the session cannot decide whether Dalvik's runtime class loader would consider the clause a match, so
# BOTH interpretations of the SAME throw are explored from the one node
# (`ExecutionSession.planThrow`'s `Committable` can carry a terminal AND successors at once) -- the
# escape, recorded immediately as `Outcome.Threw`, AND the handler continuation, which is what lets the
# second division run at all. The second division is left UNCOVERED by any try block, so its own throw
# is unambiguously unhandled and escapes directly -- no second unloadable clause is needed to reach a
# second `Outcome.Threw`.
#
# Verifier-invalid on the same grounds as `divIntFallsOffTheEnd()V` and `divIntByWideZeroLowHalf()V`
# above: the handler's own code (`move-exception` + the second division) sits immediately after the try
# block in the instruction stream, which a real verifier would read as an ordinary (and here, invalid)
# fall-through target for the first division. Harmless: a KNOWN-zero divisor's op never emits a
# `Continue` transition, so that fall-through is never actually explored -- the handler is reached only
# via the ambiguous throw's own successor, never by falling off the first `div-int`.
#
# Addresses: `div-int` (Instruction23x, 2 units) @0, `move-exception` (1 unit) @2, `div-int` @3 (2
# units). The method ends there with no trailing `return-void`: both divisions definitely throw, so no
# path ever falls through to one (the same "last instruction, no successor" shape
# `divIntFallsOffTheEnd()V` already establishes this tree tolerates).
.method public static divIntTwiceByZeroWithCatch()V
    .locals 4

    :try_start_0
    div-int v2, v0, v1              # @0 -- known zero, throws; the ambiguous catch forks escape + handler
    :try_end_0
    .catch Lbinary_math_test$Unloadable; {:try_start_0 .. :try_end_0} :catch_0

    :catch_0
    move-exception v3               # @2 -- reached only via the ambiguous throw's handler successor
    div-int v2, v0, v1              # @3 -- same known-zero divisor; UNCOVERED, so this one definitely escapes
.end method
