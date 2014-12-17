.class public final Llibcore/util/MutableByte;
.super Ljava/lang/Object;
.source "MutableByte.java"


# instance fields
.field public value:B


# direct methods
.method public constructor <init>(B)V
    .registers 2
    .param p1, "value"    # B

    .prologue
    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 23
    iput-byte p1, p0, Llibcore/util/MutableByte;->value:B

    .line 24
    return-void
.end method
