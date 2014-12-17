.class public final Llibcore/util/MutableFloat;
.super Ljava/lang/Object;
.source "MutableFloat.java"


# instance fields
.field public value:F


# direct methods
.method public constructor <init>(F)V
    .registers 2
    .param p1, "value"    # F

    .prologue
    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 23
    iput p1, p0, Llibcore/util/MutableFloat;->value:F

    .line 24
    return-void
.end method
