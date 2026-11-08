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

# A primitive type operand. [BC]: "Since B must always be a reference (and not a primitive value), this
# will always result in 0 being stored if C refers to a primitive type." That row must beat the
# unknown-source row, so tests drive this method with an unknown source as well as a known one.
# ART's verifier rejects the class, so only unverified input reaches it.
.method public static instanceOfPrimitiveInt()V
    .locals 2

    instance-of v1, v0, I

    return-void
.end method

# `Ljava/io/Serializable;` is one of the two interfaces every array implements (JLS §4.10.3), so an `int[]`
# answers 1.
.method public static instanceOfSerializable()V
    .locals 2

    instance-of v1, v0, Ljava/io/Serializable;

    return-void
.end method

# A type operand this input does not ship. ART resolves the type operand before reading the register, so
# an unresolvable operand throws `NoClassDefFoundError` even for a null register, the opposite of the
# [JVMS] order (measured on device by `Fx_InstanceOfAbsentType.smali`). The source register is left unset
# so a test can seed it: `Value.Top`, a definite null, and a real instance all throw.
.method public static instanceOfAbsentClass()V
    .locals 2

    instance-of v1, v0, Lobf/absent;

    return-void
.end method
