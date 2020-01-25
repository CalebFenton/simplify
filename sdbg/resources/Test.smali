.class LTest;
.super Ljava/lang/Object;


.method constructor <init>()V
  .locals 0
  .prologue
  .line 1
  invoke-direct {p0}, Ljava/lang/Object;-><init>()V
  return-void
.end method

.method public static addNumbers()V
  .locals 2
  const/4 v0, 0x2
  const/4 v1, 0x3
  add-int v0, v0, v1
  add-int v0, v0, v1
  invoke-static {v0, v1}, LTest;->addParameters(II)I
  move-result v0
  return-void
.end method

.method public static addParameters(II)I
  .locals 0
  add-int p0, p0, p1
  return p0
.end method

.method public static main([Ljava/lang/String;)V
  .locals 2

  #@0
  .prologue
  .line 3
  sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;

  #@2
  const-string v1, "hello,world!"

  #@4
  invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

  #@7
  .line 4
  return-void
.end method
