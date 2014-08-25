.class Linvoke_virtual_test;
.super Ljava/lang/Object;

# direct methods
.method constructor <init>()V
    .locals 0

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static InvokeReturnsVoid()V
  .locals 1

  invoke-virtual {v0}, Linvoke_virtual_test;->ReturnsVoid()V

  return-void
.end method

.method public static InvokeReturnsInt()V
  .locals 1

  invoke-virtual {v0}, Linvoke_virtual_test;->ReturnsInt()I

  return-void
.end method

.method public static InvokeReturnsParameter()V
  .locals 2

  invoke-virtual {v0, v1}, Linvoke_virtual_test;->ReturnsParameter(I)I

  return-void
.end method


.method public ReturnsVoid()V
    .locals 0

    return-void
.end method

.method public ReturnsInt()I
    .locals 1

    const/4 v0, 0x7

    return v0
.end method

.method public ReturnsParameter(I)I
    .locals 1

    move v0, p1

    return v0
.end method
