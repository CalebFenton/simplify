.class Linvoke_reflected_test;
.super Ljava/lang/Object;

.method public static invokeLongValueOf()V
  .locals 2

  invoke-static {v0}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;

  return-void
.end method

.method public static invokeBooleanValueOf()V
  .locals 1

  invoke-static {v0}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

  return-void
.end method

.method public static invokeStringBuilderAppendWithLong()V
  .locals 3

  invoke-virtual {v0, v1, v2}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

  return-void
.end method

.method public static initStringWithByteArray()V
  .locals 2

  invoke-virtual {v0, v1}, Ljava/lang/String;-><init>([B)V

  return-void
.end method

.method public static addToListAndGet()V
  .locals 3

  invoke-interface {v0, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z
  invoke-interface {v0, v2}, Ljava/util/List;->get(I)Ljava/lang/Object;
  move-result-object v0

  return-void
.end method
