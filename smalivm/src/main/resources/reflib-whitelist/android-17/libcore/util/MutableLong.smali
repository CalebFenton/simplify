.class public final Llibcore/util/MutableLong;
.super Ljava/lang/Object;
.source "MutableLong.java"


# instance fields
.field public value:J


# direct methods
.method public constructor <init>(J)V
    .registers 3
    .param p1, "value"    # J

    .prologue
    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 23
    iput-wide p1, p0, Llibcore/util/MutableLong;->value:J

    .line 24
    return-void
.end method
