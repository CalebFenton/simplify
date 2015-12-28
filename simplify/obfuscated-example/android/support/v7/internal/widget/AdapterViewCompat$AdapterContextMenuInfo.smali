.class public Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterContextMenuInfo;
.super Ljava/lang/Object;
.source "AdapterViewCompat.java"

# interfaces
.implements Landroid/view/ContextMenu$ContextMenuInfo;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/AdapterViewCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "AdapterContextMenuInfo"
.end annotation


# instance fields
.field public id:J

.field public position:I

.field public targetView:Landroid/view/View;


# direct methods
.method public constructor <init>(Landroid/view/View;IJ)V
    .locals 1
    .param p1, "targetView"    # Landroid/view/View;
    .param p2, "position"    # I
    .param p3, "id"    # J

    #@0
    .prologue
    .line 415
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 416
    iput-object p1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterContextMenuInfo;->targetView:Landroid/view/View;

    #@5
    .line 417
    iput p2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterContextMenuInfo;->position:I

    #@7
    .line 418
    iput-wide p3, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterContextMenuInfo;->id:J

    #@9
    .line 419
    return-void
.end method
