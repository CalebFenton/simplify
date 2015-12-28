.class Landroid/support/v4/view/ViewGroupCompat$ViewGroupCompatJellybeanMR2Impl;
.super Landroid/support/v4/view/ViewGroupCompat$ViewGroupCompatIcsImpl;
.source "ViewGroupCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/ViewGroupCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "ViewGroupCompatJellybeanMR2Impl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 102
    invoke-direct {p0}, Landroid/support/v4/view/ViewGroupCompat$ViewGroupCompatIcsImpl;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getLayoutMode(Landroid/view/ViewGroup;)I
    .locals 1
    .param p1, "group"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 105
    invoke-static {p1}, Landroid/support/v4/view/ViewGroupCompatJellybeanMR2;->getLayoutMode(Landroid/view/ViewGroup;)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public setLayoutMode(Landroid/view/ViewGroup;I)V
    .locals 0
    .param p1, "group"    # Landroid/view/ViewGroup;
    .param p2, "mode"    # I

    #@0
    .prologue
    .line 110
    invoke-static {p1, p2}, Landroid/support/v4/view/ViewGroupCompatJellybeanMR2;->setLayoutMode(Landroid/view/ViewGroup;I)V

    #@3
    .line 111
    return-void
.end method
