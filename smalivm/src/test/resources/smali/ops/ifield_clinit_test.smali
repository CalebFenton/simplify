.class public Lifield_clinit_test;
.super Ljava/lang/Object;

# The entry class is separate so allocation, not method entry, triggers the target's <clinit>.
.method public static exerciseAllocationAndFields()V
    .locals 5
    new-instance v0, Lifield_clinit_target;
    iget v1, v0, Lifield_clinit_target;->number:I
    iget-object v2, v0, Lifield_clinit_target;->text:Ljava/lang/String;
    const/4 v3, 0x7
    iput v3, v0, Lifield_clinit_target;->number:I
    iget v4, v0, Lifield_clinit_target;->number:I
    return-void
.end method
