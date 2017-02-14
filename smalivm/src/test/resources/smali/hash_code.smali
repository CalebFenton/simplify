.class Lhash_code;
.super Ljava/lang/Object;

# Seen in com/google/gson/JsonNull;
.method public constructor <init>()V
    .locals 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public hashCode()I
    .locals 1

    const-class v0, Lhash_code;
    invoke-virtual {v0}, Ljava/lang/Object;->hashCode()I
    move-result v0
    return v0
.end method

.method public static createInstance()V
  .locals 1
  new-instance v0, Lhash_code;
  invoke-virtual {v0}, Lhash_code;-><init>()V
  return-void
.end method