.class Linstanceof_test;
.super Ljava/lang/Object;

.method public static instanceOfObject()V
    .locals 2

    instance-of v1, v0, Ljava/lang/Object;

    return-void
.end method

.method public static instanceOfString()V
    .locals 2

    instance-of v1, v0, Ljava/lang/String;

    return-void
.end method

.method public static instanceOfStringArray()V
    .locals 2

    instance-of v1, v0, [Ljava/lang/String;

    return-void
.end method

.method public static instanceOfIntArray()V
    .locals 2

    instance-of v1, v0, [I

    return-void
.end method

.method public static instanceOfObjectArray()V
  .locals 2

  instance-of v1, v0, [Ljava/lang/Object;

  return-void
.end method

.method public static instanceOf2DObjectArray()V
  .locals 2

  instance-of v1, v0, [[Ljava/lang/Object;

  return-void
.end method

# --------------------------------------------------------------------------------------------------
# Added by Plan 8 Task 8. The six methods above are the migrated corpus and are unchanged; each of
# the three below witnesses a row of the semantics table the corpus never reached, and every one of
# them assembles (measured -- see the task report; the primitive target in particular is input ART's
# VERIFIER rejects, so "smali assembles it" was checked rather than assumed).
# --------------------------------------------------------------------------------------------------

# A PRIMITIVE TYPE OPERAND. [BC]'s note for `instance-of`, verbatim: "Since B must always be a
# reference (and not a primitive value), this will always result in 0 being stored if C refers to a
# primitive type." That row is SPEC and must beat the unknown-source row, so this fixture is driven
# with an UNKNOWN source as well as a known one -- with the rows in the other order the unknown
# source would answer Unknown(Z) where [BC] says the result is always 0.
#
# Unreachable on verified input ([ART]'s verifier rejects the class), which is exactly why it needs a
# fixture rather than a differential: obfuscated input is the expected input here.
.method public static instanceOfPrimitiveInt()V
    .locals 2

    instance-of v1, v0, I

    return-void
.end method

# `Ljava/io/Serializable;`, one of the two interfaces EVERY array implements (JLS §4.10.3). The
# oracle's `VirtualArray.instanceOf` omits both, so this is Plan 8 Task 7's registered divergence
# reaching the op level: an `int[]` here answers 1, deliberately unlike the oracle's 0.
# NEVER reconcile this toward the oracle -- see `ClassIndex.isInstanceOf`'s row 4.
.method public static instanceOfSerializable()V
    .locals 2

    instance-of v1, v0, Ljava/io/Serializable;

    return-void
.end method

# A TYPE OPERAND THIS INPUT DOES NOT SHIP. [ART] resolves the type operand BEFORE reading the
# register (`interpreter_switch_impl-inl.h:838-843` before `:847`), so an unresolvable operand throws
# `NoClassDefFoundError` even for a null register -- the opposite of the [JVMS] ordering. This core
# decides loadability at DECODE time and throws a guest `NoClassDefFoundError` at EXECUTE time
# (Plan 9 Task 15 / ROW-23, reversing Plan 8's `UnsupportedOp` refusal now that the type is measured:
# `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`, `Fx_InstanceOfAbsentType.smali`).
# The source register is left UNSET here so a test can seed it -- `Value.Top`, a definite null, or a
# real instance all have to throw, because resolution precedes every one of them. Pinned by
# `core/op/InstanceOfOpTest.anAbsentTargetTypeThrowsNoClassDefFoundErrorEvenOnANullSource` and
# `core/OpBuilderTest.anInstanceOfAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal`.
.method public static instanceOfAbsentClass()V
    .locals 2

    instance-of v1, v0, Lobf/absent;

    return-void
.end method
