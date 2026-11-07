.class public LPackagedSmoke;
.super Ljava/lang/Object;

# Must execute the captured modern framework method, including its wide result.
.method public static multiply()J
    .registers 4
    const v0, 0x7fffffff
    const/4 v1, 0x2
    invoke-static {v0, v1}, Ljava/lang/Math;->multiplyFull(II)J
    move-result-wide v2
    return-wide v2
.end method
