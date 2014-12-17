.class public final Llibcore/util/MutableShort;
.super Ljava/lang/Object;
.source "MutableShort.java"


# instance fields
.field public value:S


# direct methods
.method public constructor <init>(S)V
    .registers 2
    .param p1, "value"    # S

    .prologue
    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 23
    iput-short p1, p0, Llibcore/util/MutableShort;->value:S

    .line 24
    return-void
.end method
