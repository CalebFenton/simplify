.class Lorg/cf/test/SimpleClass;
.super Ljava/lang/Object;

.method public static simpleLoop(ILjava/lang/Object;)V
    .locals 2

    const/4 v0, 0x0
    const/4 v1, 0x1
    const/4 v2, 0x2

    :begin
    if-gt v0, v2, :end

    nop
    add-int/2addr v0, v1
    goto :begin

    :end
    return-void
.end method
