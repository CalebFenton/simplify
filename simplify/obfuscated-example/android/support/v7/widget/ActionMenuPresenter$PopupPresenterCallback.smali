.class Landroid/support/v7/widget/ActionMenuPresenter$PopupPresenterCallback;
.super Ljava/lang/Object;
.source "ActionMenuPresenter.java"

# interfaces
.implements Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/widget/ActionMenuPresenter;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "PopupPresenterCallback"
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/widget/ActionMenuPresenter;


# direct methods
.method private constructor <init>(Landroid/support/v7/widget/ActionMenuPresenter;)V
    .locals 0

    #@0
    .prologue
    .line 719
    iput-object p1, p0, Landroid/support/v7/widget/ActionMenuPresenter$PopupPresenterCallback;->this$0:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/support/v7/widget/ActionMenuPresenter$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v7/widget/ActionMenuPresenter;
    .param p2, "x1"    # Landroid/support/v7/widget/ActionMenuPresenter$1;

    #@0
    .prologue
    .line 719
    invoke-direct {p0, p1}, Landroid/support/v7/widget/ActionMenuPresenter$PopupPresenterCallback;-><init>(Landroid/support/v7/widget/ActionMenuPresenter;)V

    #@3
    return-void
.end method


# virtual methods
.method public onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V
    .locals 3
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "allMenusAreClosing"    # Z

    #@0
    .prologue
    .line 732
    instance-of v1, p1, Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@2
    if-eqz v1, :cond_0

    #@4
    move-object v1, p1

    #@5
    .line 733
    check-cast v1, Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@7
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->getRootMenu()Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@a
    move-result-object v1

    #@b
    const/4 v2, 0x0

    #@c
    invoke-virtual {v1, v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->close(Z)V

    #@f
    .line 735
    :cond_0
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter$PopupPresenterCallback;->this$0:Landroid/support/v7/widget/ActionMenuPresenter;

    #@11
    invoke-virtual {v1}, Landroid/support/v7/widget/ActionMenuPresenter;->getCallback()Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@14
    move-result-object v0

    #@15
    .line 736
    .local v0, "cb":Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;
    if-eqz v0, :cond_1

    #@17
    .line 737
    invoke-interface {v0, p1, p2}, Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;->onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V

    #@1a
    .line 739
    :cond_1
    return-void
.end method

.method public onOpenSubMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;)Z
    .locals 4
    .param p1, "subMenu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 723
    if-nez p1, :cond_0

    #@3
    .line 727
    :goto_0
    return v2

    #@4
    .line 725
    :cond_0
    iget-object v3, p0, Landroid/support/v7/widget/ActionMenuPresenter$PopupPresenterCallback;->this$0:Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    move-object v1, p1

    #@7
    check-cast v1, Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@9
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->getItem()Landroid/view/MenuItem;

    #@c
    move-result-object v1

    #@d
    invoke-interface {v1}, Landroid/view/MenuItem;->getItemId()I

    #@10
    move-result v1

    #@11
    iput v1, v3, Landroid/support/v7/widget/ActionMenuPresenter;->mOpenSubMenuId:I

    #@13
    .line 726
    iget-object v1, p0, Landroid/support/v7/widget/ActionMenuPresenter$PopupPresenterCallback;->this$0:Landroid/support/v7/widget/ActionMenuPresenter;

    #@15
    invoke-virtual {v1}, Landroid/support/v7/widget/ActionMenuPresenter;->getCallback()Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@18
    move-result-object v0

    #@19
    .line 727
    .local v0, "cb":Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;
    if-eqz v0, :cond_1

    #@1b
    invoke-interface {v0, p1}, Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;->onOpenSubMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;)Z

    #@1e
    move-result v1

    #@1f
    :goto_1
    move v2, v1

    #@20
    goto :goto_0

    #@21
    :cond_1
    move v1, v2

    #@22
    goto :goto_1
.end method
