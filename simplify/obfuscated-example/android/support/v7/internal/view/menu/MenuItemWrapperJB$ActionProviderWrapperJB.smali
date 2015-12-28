.class Landroid/support/v7/internal/view/menu/MenuItemWrapperJB$ActionProviderWrapperJB;
.super Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$ActionProviderWrapper;
.source "MenuItemWrapperJB.java"

# interfaces
.implements Landroid/view/ActionProvider$VisibilityListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/view/menu/MenuItemWrapperJB;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = "ActionProviderWrapperJB"
.end annotation


# instance fields
.field mListener:Landroid/support/v4/view/ActionProvider$VisibilityListener;

.field final synthetic this$0:Landroid/support/v7/internal/view/menu/MenuItemWrapperJB;


# direct methods
.method public constructor <init>(Landroid/support/v7/internal/view/menu/MenuItemWrapperJB;Landroid/content/Context;Landroid/view/ActionProvider;)V
    .locals 0
    .param p2, "context"    # Landroid/content/Context;
    .param p3, "inner"    # Landroid/view/ActionProvider;

    #@0
    .prologue
    .line 47
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperJB$ActionProviderWrapperJB;->this$0:Landroid/support/v7/internal/view/menu/MenuItemWrapperJB;

    #@2
    .line 48
    invoke-direct {p0, p1, p2, p3}, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS$ActionProviderWrapper;-><init>(Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;Landroid/content/Context;Landroid/view/ActionProvider;)V

    #@5
    .line 49
    return-void
.end method


# virtual methods
.method public isVisible()Z
    .locals 1

    #@0
    .prologue
    .line 63
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperJB$ActionProviderWrapperJB;->mInner:Landroid/view/ActionProvider;

    #@2
    invoke-virtual {v0}, Landroid/view/ActionProvider;->isVisible()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public onActionProviderVisibilityChanged(Z)V
    .locals 1
    .param p1, "isVisible"    # Z

    #@0
    .prologue
    .line 79
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperJB$ActionProviderWrapperJB;->mListener:Landroid/support/v4/view/ActionProvider$VisibilityListener;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 80
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperJB$ActionProviderWrapperJB;->mListener:Landroid/support/v4/view/ActionProvider$VisibilityListener;

    #@6
    invoke-interface {v0, p1}, Landroid/support/v4/view/ActionProvider$VisibilityListener;->onActionProviderVisibilityChanged(Z)V

    #@9
    .line 82
    :cond_0
    return-void
.end method

.method public onCreateActionView(Landroid/view/MenuItem;)Landroid/view/View;
    .locals 1
    .param p1, "forItem"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 53
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperJB$ActionProviderWrapperJB;->mInner:Landroid/view/ActionProvider;

    #@2
    invoke-virtual {v0, p1}, Landroid/view/ActionProvider;->onCreateActionView(Landroid/view/MenuItem;)Landroid/view/View;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public overridesItemVisibility()Z
    .locals 1

    #@0
    .prologue
    .line 58
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperJB$ActionProviderWrapperJB;->mInner:Landroid/view/ActionProvider;

    #@2
    invoke-virtual {v0}, Landroid/view/ActionProvider;->overridesItemVisibility()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public refreshVisibility()V
    .locals 1

    #@0
    .prologue
    .line 68
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperJB$ActionProviderWrapperJB;->mInner:Landroid/view/ActionProvider;

    #@2
    invoke-virtual {v0}, Landroid/view/ActionProvider;->refreshVisibility()V

    #@5
    .line 69
    return-void
.end method

.method public setVisibilityListener(Landroid/support/v4/view/ActionProvider$VisibilityListener;)V
    .locals 1
    .param p1, "listener"    # Landroid/support/v4/view/ActionProvider$VisibilityListener;

    #@0
    .prologue
    .line 73
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperJB$ActionProviderWrapperJB;->mListener:Landroid/support/v4/view/ActionProvider$VisibilityListener;

    #@2
    .line 74
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemWrapperJB$ActionProviderWrapperJB;->mInner:Landroid/view/ActionProvider;

    #@4
    if-eqz p1, :cond_0

    #@6
    .end local p0    # "this":Landroid/support/v7/internal/view/menu/MenuItemWrapperJB$ActionProviderWrapperJB;
    :goto_0
    invoke-virtual {v0, p0}, Landroid/view/ActionProvider;->setVisibilityListener(Landroid/view/ActionProvider$VisibilityListener;)V

    #@9
    .line 75
    return-void

    #@a
    .line 74
    .restart local p0    # "this":Landroid/support/v7/internal/view/menu/MenuItemWrapperJB$ActionProviderWrapperJB;
    :cond_0
    const/4 p0, 0x0

    #@b
    goto :goto_0
.end method
