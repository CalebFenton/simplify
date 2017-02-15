.class Lorg/cf/test/CustomException;
.super Ljava/lang/Throwable;

.method constructor <init>()V
    .locals 2

    .prologue
    .line 1
    move-object v0, p0

    .local v0, "this":Lorg/cf/test/CustomException;
    move-object v1, v0

    invoke-direct {v1}, Ljava/lang/Throwable;-><init>()V

    return-void
.end method
