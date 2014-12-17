.class public final Llibcore/util/MutableDouble;
.super Ljava/lang/Object;
.source "MutableDouble.java"


# instance fields
.field public value:D


# direct methods
.method public constructor <init>(D)V
    .registers 3
    .param p1, "value"    # D

    .prologue
    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 23
    iput-wide p1, p0, Llibcore/util/MutableDouble;->value:D

    .line 24
    return-void
.end method
