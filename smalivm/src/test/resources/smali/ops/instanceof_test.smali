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


# A primitive type operand. [BC]: "this will always result in 0 being stored if C refers to a
# primitive type." That spec row beats the unknown-source row, so the test drives this with both a
# known and an unknown source. ART's verifier rejects the class; obfuscated input is in scope.
.method public static instanceOfPrimitiveInt()V
    .locals 2

    instance-of v1, v0, I

    return-void
.end method

# `Ljava/io/Serializable;`, one of the two interfaces every array implements (JLS §4.10.3), so an
# `int[]` answers 1. See `ClassIndex.isInstanceOf`.
.method public static instanceOfSerializable()V
    .locals 2

    instance-of v1, v0, Ljava/io/Serializable;

    return-void
.end method

# A type operand this input does not ship. [ART] resolves the type operand before reading the
# register, so an unresolvable operand throws `NoClassDefFoundError` even for a null register, the
# opposite of the [JVMS] ordering (measured, `Fx_InstanceOfAbsentType.smali`). The source register is
# left unset so a test can seed it; `InstanceOfOpTest.anAbsentTargetTypeThrowsNoClassDefFoundErrorEvenOnANullSource`
# covers the execute half and `OpBuilderTest.anInstanceOfAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal` the decode half.
.method public static instanceOfAbsentClass()V
    .locals 2

    instance-of v1, v0, Lobf/absent;

    return-void
.end method
