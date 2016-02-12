.class Linvoke_reflected_test;
.super Ljava/lang/Object;

.method public static invokeLongValueOf()V
  .locals 2

  invoke-static {v0}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;

  return-void
.end method

.method public static invokeStringBuilderAppendWithLong()V
  .locals 3

  invoke-virtual {v0, v1, v2}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

  return-void
.end method

.method public static initStringWithByteArray()V
  .locals 1

  invoke-virtual {v0, v1}, Ljava/lang/String;-><init>([B)V

  return-void
.end method
