.class public final Llibcore/util/MutableChar;
.super Ljava/lang/Object;
.source "MutableChar.java"


# instance fields
.field public value:C


# direct methods
.method public constructor <init>(C)V
    .registers 2
    .param p1, "value"    # C

    .prologue
    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 23
    iput-char p1, p0, Llibcore/util/MutableChar;->value:C

    .line 24
    return-void
.end method
