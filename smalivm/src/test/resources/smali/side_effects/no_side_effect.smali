.class Lno_side_effect;
.super Ljava/lang/Object;

.method public static constructor <clinit>()V
  .locals 2

  const/4 v0, 0x5
  const-string v0, "nothing to see here"

  # Gradle or something is proxy'ing out:Ljava/io/PrintStream; and it can't be cloned.
  # This makes the cloner unhappy, and causes failures.
  # Tests run fine in Eclipse.
  #sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
  #const-string v1, "hello,world!"
  #invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

  return-void
.end method
