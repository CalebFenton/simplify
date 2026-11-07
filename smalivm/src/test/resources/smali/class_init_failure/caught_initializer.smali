.class public Lclass_init_failure/caught_initializer;
.super Ljava/lang/Object;

.field public static attempts:I

.method public static constructor <clinit>()V
    .registers 2
    sget v0, Lclass_init_failure/caught_initializer;->attempts:I
    add-int/lit8 v0, v0, 0x1
    sput v0, Lclass_init_failure/caught_initializer;->attempts:I
  :throw_start
    const/4 v1, 0x0
    throw v1
  :throw_end
  :caught
    move-exception v1
    return-void
    .catch Ljava/lang/NullPointerException; {:throw_start .. :throw_end} :caught
.end method
