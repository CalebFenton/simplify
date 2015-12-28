.class public Landroid/support/v7/internal/widget/FitWindowsLinearLayout;
.super Landroid/widget/LinearLayout;
.source "FitWindowsLinearLayout.java"

# interfaces
.implements Landroid/support/v7/internal/widget/FitWindowsViewGroup;


# instance fields
.field private mListener:Landroid/support/v7/internal/widget/FitWindowsViewGroup$OnFitSystemWindowsListener;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 0
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 32
    invoke-direct {p0, p1}, Landroid/widget/LinearLayout;-><init>(Landroid/content/Context;)V

    #@3
    .line 33
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 0
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 36
    invoke-direct {p0, p1, p2}, Landroid/widget/LinearLayout;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@3
    .line 37
    return-void
.end method


# virtual methods
.method protected fitSystemWindows(Landroid/graphics/Rect;)Z
    .locals 1
    .param p1, "insets"    # Landroid/graphics/Rect;

    #@0
    .prologue
    .line 45
    iget-object v0, p0, Landroid/support/v7/internal/widget/FitWindowsLinearLayout;->mListener:Landroid/support/v7/internal/widget/FitWindowsViewGroup$OnFitSystemWindowsListener;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 46
    iget-object v0, p0, Landroid/support/v7/internal/widget/FitWindowsLinearLayout;->mListener:Landroid/support/v7/internal/widget/FitWindowsViewGroup$OnFitSystemWindowsListener;

    #@6
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/FitWindowsViewGroup$OnFitSystemWindowsListener;->onFitSystemWindows(Landroid/graphics/Rect;)V

    #@9
    .line 48
    :cond_0
    invoke-super {p0, p1}, Landroid/widget/LinearLayout;->fitSystemWindows(Landroid/graphics/Rect;)Z

    #@c
    move-result v0

    #@d
    return v0
.end method

.method public setOnFitSystemWindowsListener(Landroid/support/v7/internal/widget/FitWindowsViewGroup$OnFitSystemWindowsListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v7/internal/widget/FitWindowsViewGroup$OnFitSystemWindowsListener;

    #@0
    .prologue
    .line 40
    iput-object p1, p0, Landroid/support/v7/internal/widget/FitWindowsLinearLayout;->mListener:Landroid/support/v7/internal/widget/FitWindowsViewGroup$OnFitSystemWindowsListener;

    #@2
    .line 41
    return-void
.end method
