.class Lmethod_reflector_test;
.super Ljava/lang/Object;


.method public static InitCharacterWithChar()V
  .locals 1

  # Can't set v0 to "new-instance" at Java level
  new-instance v0, Ljava/lang/Character;
  invoke-direct {v0, v1}, Ljava/lang/Character;-><init>(C)V

  return-void
.end method

.method public static InitBooleanWithBoolean()V
  .locals 1

  new-instance v0, Ljava/lang/Boolean;
  invoke-direct {v0, v1}, Ljava/lang/Boolean;-><init>(Z)V

  return-void
.end method

.method public static ShortValueOfShort()V
  .locals 1

  invoke-static {v0}, Ljava/lang/Short;->valueOf(S)Ljava/lang/Short;
  move-result v0

  return-void
.end method

.method public static ByteValueOfByte()V
  .locals 1

  invoke-static {v0}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;
  move-result v0

  return-void
.end method