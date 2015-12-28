.class Landroid/support/v7/internal/view/menu/MenuItemImpl$1;
.super Ljava/lang/Object;
.source "MenuItemImpl.java"

# interfaces
.implements Landroid/support/v4/view/ActionProvider$VisibilityListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/internal/view/menu/MenuItemImpl;->setSupportActionProvider(Landroid/support/v4/view/ActionProvider;)Landroid/support/v4/internal/view/SupportMenuItem;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/view/menu/MenuItemImpl;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/view/menu/MenuItemImpl;)V
    .locals 0

    #@0
    .prologue
    .line 663
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/MenuItemImpl$1;->this$0:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onActionProviderVisibilityChanged(Z)V
    .locals 2
    .param p1, "isVisible"    # Z

    #@0
    .prologue
    .line 666
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuItemImpl$1;->this$0:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    # getter for: Landroid/support/v7/internal/view/menu/MenuItemImpl;->mMenu:Landroid/support/v7/internal/view/menu/MenuBuilder;
    invoke-static {v0}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->access$000(Landroid/support/v7/internal/view/menu/MenuItemImpl;)Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@5
    move-result-object v0

    #@6
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuItemImpl$1;->this$0:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@8
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemVisibleChanged(Landroid/support/v7/internal/view/menu/MenuItemImpl;)V

    #@b
    .line 667
    return-void
.end method
