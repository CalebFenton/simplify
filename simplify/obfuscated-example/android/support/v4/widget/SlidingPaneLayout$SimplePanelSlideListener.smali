.class public Landroid/support/v4/widget/SlidingPaneLayout$SimplePanelSlideListener;
.super Ljava/lang/Object;
.source "SlidingPaneLayout.java"

# interfaces
.implements Landroid/support/v4/widget/SlidingPaneLayout$PanelSlideListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/SlidingPaneLayout;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "SimplePanelSlideListener"
.end annotation


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 237
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public onPanelClosed(Landroid/view/View;)V
    .locals 0
    .param p1, "panel"    # Landroid/view/View;

    #@0
    .prologue
    .line 246
    return-void
.end method

.method public onPanelOpened(Landroid/view/View;)V
    .locals 0
    .param p1, "panel"    # Landroid/view/View;

    #@0
    .prologue
    .line 243
    return-void
.end method

.method public onPanelSlide(Landroid/view/View;F)V
    .locals 0
    .param p1, "panel"    # Landroid/view/View;
    .param p2, "slideOffset"    # F

    #@0
    .prologue
    .line 240
    return-void
.end method
