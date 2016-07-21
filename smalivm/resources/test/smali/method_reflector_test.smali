.class Lmethod_reflector_test;
.super Ljava/lang/Object;


.method public static initCharacterWithChar()V
  .locals 1

  # Can't set v0 to "new-instance" at Java level
  new-instance v0, Ljava/lang/Character;
  invoke-direct {v0, v1}, Ljava/lang/Character;-><init>(C)V

  return-void
.end method

.method public static initBooleanWithBoolean()V
  .locals 1

  new-instance v0, Ljava/lang/Boolean;
  invoke-direct {v0, v1}, Ljava/lang/Boolean;-><init>(Z)V

  return-void
.end method

.method public static shortValueOfShort()V
  .locals 1

  invoke-static {v0}, Ljava/lang/Short;->valueOf(S)Ljava/lang/Short;
  move-result v0

  return-void
.end method

.method public static byteValueOfByte()V
  .locals 1

  invoke-static {v0}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;
  move-result v0

  return-void
.end method

.method public static getClassMethod()V
  .locals 2

  invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
  move-result v0

  return-void
.end method

.method public static stringLength()V
  .locals 1

  :try_start_1
  invoke-virtual {v0}, Ljava/lang/String;->length()I
  :try_end_1
  .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

  nop

  :catch_1
  return-void
.end method
