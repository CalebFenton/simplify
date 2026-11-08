.class Lbinary_math_test;
.super Ljava/lang/Object;


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

# Register layout: `long` is wide, so the lhs is the pair {v0,v1} and the rhs is read from {v2,v3}.
# An rhs at v1 would overlap the lhs pair, and seeding it would clear the lhs's low half
# (`Frame.clearWidePartner`). Addresses: div-long (23x, 2 units) @0, then @2/@3/@4.
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

# Register layout as in `divLongWithCatch()V`: `double` is wide too, so the rhs pair starts at v2.
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


# A div-int whose ambiguous divisor forks, where the fall-through target does not exist but the
# exceptional one has a real handler. Verifier-invalid on purpose: a fork keeps its buildable branch
# when the other target is malformed, as deobfuscator input requires.
#
# The handler sits before the try block and is jumped over, so the div-int is the last instruction:
# @0 goto (1 unit), @1 move-exception (1), @2 return-void (1), @3 div-int (2), and the fall-through
# target @5 does not exist. A handler after the div-int would make the fall-through buildable.
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


# Ambiguous fork decisions. Dalvik throws ArithmeticException only for a zero divisor, for both
# `div` and `rem`, so the fork depends on the divisor alone:
#   - `remIntUnknownDivisorWithCatch(I)V`: `rem-int` by an unknown divisor, so the handler is feasible.
#   - `divIntUnknownDividendWithCatch(I)V`: an unknown dividend over a known non-zero divisor, so the
#     handler is infeasible (`Integer.MIN_VALUE / -1` overflows silently).
#
# The unknown operand is a parameter, so the engine seeds it as an unknown of its declared type with
# no harness help. `move-exception` targets v1, not v0, so the observed register v0 holds the same
# type on both paths instead of joining `I` with `Ljava/lang/ArithmeticException;`.
#
# Register layout: `.registers 3` with one `I` parameter puts it at v2, leaving v0 for the operand and
# v1 for the catch. Addresses: @0 div/rem-int (23x, 2 units), @2 return-void (1), @3 move-exception
# (1), @4 goto (1), the same tail as `divIntWithCatch()V`.

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


# A `div-int` whose divisor register was written by `const-wide` 2^32, whose low 32 bits are zero.
# Dalvik reads a `div-int` operand as 32 bits, so the divisor is 0 and it throws; the divide-by-zero
# test must use the same width as the arithmetic. `const-wide` stores a `Long` raw, so the ambiguity
# comes from the real opcode rather than a hand-built `Value`.
#
# Verifier-invalid on purpose: obfuscators emit register types the verifier rejects, and the engine
# must still route the path Dalvik takes.
.method public static divIntByWideZeroLowHalf()V
    .locals 4

    const/16 v0, 0xc            # @0 -- dividend 12
    const-wide v2, 0x100000000L # @2 -- 2^32: non-zero as a long, zero as an int
    div-int v0, v0, v2          # @7
    return-void                 # @9
.end method


# Two `div-int`s over the same known-zero divisor (v0=10, v1=0, per BinaryMathOpTest's seed), each
# synthesizing its own ArithmeticException. The two land under distinct heap ids and do not
# `agreesWith` each other, as in Dalvik, where each throw allocates a fresh object
# (BinaryMathOpTest.twoSeparatelySynthesizedExceptionsStillDoNotAgree).
#
# The catch type is unloadable (`Lbinary_math_test$Unloadable;`) rather than a matching
# `Ljava/lang/ArithmeticException;`. A matching clause makes the first throw definitely caught, so it
# never becomes an `Outcome.Threw` and the test's list of escaped refs would be empty. An unloadable
# clause is ambiguous (`ThrowRoutingTest.anUnloadableCatchTypeForksTheHandlerAndTheEscape`): the first
# throw both escapes and continues into the handler, where the second, uncovered division throws and
# escapes too.
#
# Verifier-invalid: the handler code follows the try block, so a verifier reads it as the first
# division's fall-through. A known-zero divisor never falls through, so only the throw reaches it.
#
# Addresses: `div-int` (23x, 2 units) @0, `move-exception` @2, `div-int` @3. No trailing
# `return-void`: both divisions definitely throw.
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


# The literal forms with a literal that only a boundary test needs: a divisor of -1 (the one divisor for
# which `Integer.MIN_VALUE` overflows) and shift distances outside 0..31 (which the int rule masks).
.method public static divIntLit8ByMinusOne()V
    .locals 1

    div-int/lit8 v0, v0, -0x1

    return-void
.end method

.method public static remIntLit8ByMinusOne()V
    .locals 1

    rem-int/lit8 v0, v0, -0x1

    return-void
.end method

.method public static divIntLit16ByMinusOne()V
    .locals 1

    div-int/lit16 v0, v0, -0x1

    return-void
.end method

.method public static remIntLit16ByMinusOne()V
    .locals 1

    rem-int/lit16 v0, v0, -0x1

    return-void
.end method

.method public static shlIntLit8By33()V
    .locals 1

    shl-int/lit8 v0, v0, 0x21

    return-void
.end method

.method public static ushrIntLit8ByMinusOne()V
    .locals 1

    ushr-int/lit8 v0, v0, -0x1

    return-void
.end method
