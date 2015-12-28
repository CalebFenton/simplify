.class Landroid/support/v4/view/ViewGroupCompat$ViewGroupCompatApi21Impl;
.super Landroid/support/v4/view/ViewGroupCompat$ViewGroupCompatJellybeanMR2Impl;
.source "ViewGroupCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/ViewGroupCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "ViewGroupCompatApi21Impl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 114
    invoke-direct {p0}, Landroid/support/v4/view/ViewGroupCompat$ViewGroupCompatJellybeanMR2Impl;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public isTransitionGroup(Landroid/view/ViewGroup;)Z
    .locals 1
    .param p1, "group"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 122
    invoke-static {p1}, Landroid/support/v4/view/ViewGroupCompatApi21;->isTransitionGroup(Landroid/view/ViewGroup;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public setTransitionGroup(Landroid/view/ViewGroup;Z)V
    .locals 0
    .param p1, "group"    # Landroid/view/ViewGroup;
    .param p2, "isTransitionGroup"    # Z

    #@0
    .prologue
    .line 117
    invoke-static {p1, p2}, Landroid/support/v4/view/ViewGroupCompatApi21;->setTransitionGroup(Landroid/view/ViewGroup;Z)V

    #@3
    .line 118
    return-void
.end method
