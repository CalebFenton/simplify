.class public Lifield_clinit_target;
.super Ljava/lang/Object;

.field public static runs:I
.field public number:I
.field public text:Ljava/lang/String;

.method public static constructor <clinit>()V
    .locals 1
    sget v0, Lifield_clinit_target;->runs:I
    add-int/lit8 v0, v0, 0x1
    sput v0, Lifield_clinit_target;->runs:I
    return-void
.end method
