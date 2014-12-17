.class public final Llibcore/util/MutableInt;
.super Ljava/lang/Object;
.source "MutableInt.java"


# instance fields
.field public value:I


# direct methods
.method public constructor <init>(I)V
    .registers 2
    .param p1, "value"    # I

    .prologue
    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 23
    iput p1, p0, Llibcore/util/MutableInt;->value:I

    .line 24
    return-void
.end method
