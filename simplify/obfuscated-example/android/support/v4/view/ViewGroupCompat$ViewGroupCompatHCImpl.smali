.class Landroid/support/v4/view/ViewGroupCompat$ViewGroupCompatHCImpl;
.super Landroid/support/v4/view/ViewGroupCompat$ViewGroupCompatStubImpl;
.source "ViewGroupCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/ViewGroupCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "ViewGroupCompatHCImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 87
    invoke-direct {p0}, Landroid/support/v4/view/ViewGroupCompat$ViewGroupCompatStubImpl;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public setMotionEventSplittingEnabled(Landroid/view/ViewGroup;Z)V
    .locals 0
    .param p1, "group"    # Landroid/view/ViewGroup;
    .param p2, "split"    # Z

    #@0
    .prologue
    .line 90
    invoke-static {p1, p2}, Landroid/support/v4/view/ViewGroupCompatHC;->setMotionEventSplittingEnabled(Landroid/view/ViewGroup;Z)V

    #@3
    .line 91
    return-void
.end method
