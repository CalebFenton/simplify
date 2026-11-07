.class public Lclass_init_failure/non_error;
.super Ljava/lang/Object;

.field public static value:I

.method public static constructor <clinit>()V
    .registers 2
    sget v0, Lclass_init_failure/state;->attempts:I
    add-int/lit8 v0, v0, 0x1
    sput v0, Lclass_init_failure/state;->attempts:I
    const/4 v0, 0x7
    sput v0, Lclass_init_failure/non_error;->value:I
    sput v0, Lclass_init_failure/state;->beforeFailure:I
  :throw_start
    const/4 v1, 0x0
    throw v1
  :throw_end
  :caught
    move-exception v1
    sput-object v1, Lclass_init_failure/state;->nonError:Ljava/lang/Throwable;
    throw v1
    .catch Ljava/lang/NullPointerException; {:throw_start .. :throw_end} :caught
.end method

.method public static touch()V
    .registers 0
    return-void
.end method
