.class public Landroid/support/v4/view/ViewCompatKitKat;
.super Ljava/lang/Object;
.source "ViewCompatKitKat.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 24
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getAccessibilityLiveRegion(Landroid/view/View;)I
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 26
    invoke-virtual {p0}, Landroid/view/View;->getAccessibilityLiveRegion()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static setAccessibilityLiveRegion(Landroid/view/View;I)V
    .locals 0
    .param p0, "view"    # Landroid/view/View;
    .param p1, "mode"    # I

    #@0
    .prologue
    .line 30
    invoke-virtual {p0, p1}, Landroid/view/View;->setAccessibilityLiveRegion(I)V

    #@3
    .line 31
    return-void
.end method
