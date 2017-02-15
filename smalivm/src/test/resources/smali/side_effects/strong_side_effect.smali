.class Lstrong_side_effect;
.super Ljava/lang/Object;

# Writes malbytes to secretFile. Any IO is strong side effect.
.method public static constructor <clinit>()V
  .locals 7
  .annotation system Ldalvik/annotation/Throws;
      value = {
          Ljava/lang/Exception;
      }
  .end annotation

  .prologue
  .line 16
  new-instance v2, Ljava/io/FileOutputStream;
  move-object v6, v2
  move-object v2, v6
  move-object v3, v6

  const-string v4, "secretFile"
  invoke-direct {v3, v4}, Ljava/io/FileOutputStream;-><init>(Ljava/lang/String;)V
  move-object v0, v2

  .line 17
  const/4 v2, 0x3
  new-array v2, v2, [B
  fill-array-data v2, :array_0
  move-object v1, v2

  .line 18
  move-object v2, v0
  move-object v3, v1
  invoke-virtual {v2, v3}, Ljava/io/OutputStream;->write([B)V

  .line 19
  return-void

  .line 17
  :array_0
  .array-data 1
      0x1t
      0x2t
      0x3t
  .end array-data
.end method
