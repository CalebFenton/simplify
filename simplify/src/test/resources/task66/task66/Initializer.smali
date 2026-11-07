.class public Ltask66/Initializer;
.super Ljava/lang/Object;

.field private static root:[I
.field private static marker:I

.method static constructor <clinit>()V
    .locals 3
    const/4 v0, 0x1
    new-array v0, v0, [I
    sput-object v0, Ltask66/Initializer;->root:[I
    const/4 v1, 0x1
    sput v1, Ltask66/Initializer;->marker:I
    return-void
.end method

.method public static pure()I
    .locals 1
    const/4 v0, 0x7
    return v0
.end method

.method public static rootArray()[I
    .locals 1
    sget-object v0, Ltask66/Initializer;->root:[I
    return-object v0
.end method

.method public static mutateRoot()V
    .locals 3
    sget-object v0, Ltask66/Initializer;->root:[I
    const/4 v1, 0x0
    const/16 v2, 0x8
    aput v2, v0, v1
    return-void
.end method

.method public static readRoot()I
    .locals 3
    sget-object v0, Ltask66/Initializer;->root:[I
    const/4 v1, 0x0
    aget v2, v0, v1
    return v2
.end method
