.class public Lknown_string;
.super Ljava/lang/Object;

# v0 is an uninitialized String after new-instance and "ok" after String.<init>([B), a reflection-safe constructor.
.method public static constructString()Ljava/lang/String;
    .locals 2

    const/4 v1, 0x2
    new-array v1, v1, [B
    fill-array-data v1, :bytes
    new-instance v0, Ljava/lang/String;
    invoke-direct {v0, v1}, Ljava/lang/String;-><init>([B)V
    return-object v0

    :bytes
    .array-data 1
        0x6ft
        0x6bt
    .end array-data
.end method
