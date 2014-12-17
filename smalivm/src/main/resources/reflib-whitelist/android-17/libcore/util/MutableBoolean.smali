.class public final Llibcore/util/MutableBoolean;
.super Ljava/lang/Object;
.source "MutableBoolean.java"


# instance fields
.field public value:Z


# direct methods
.method public constructor <init>(Z)V
    .registers 2
    .param p1, "value"    # Z

    .prologue
    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 23
    iput-boolean p1, p0, Llibcore/util/MutableBoolean;->value:Z

    .line 24
    return-void
.end method
