.class Lweak_side_effect;
.super Ljava/lang/Object;

.field public static stringAccessedByThread:Ljava/lang/String;

# Modifying data not local to method side-effect, but this is not direct side-effect.
# Would need clever thread to watch this value.
.method public static constructor <clinit>()V
  .locals 1

  const-string v0, "nothing to see here"
  sput-object v0, Lweak_side_effect;->stringAccessedByThread:Ljava/lang/String;

  return-void
.end method
