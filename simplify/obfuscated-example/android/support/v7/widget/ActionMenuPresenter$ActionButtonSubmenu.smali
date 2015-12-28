.class Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;
.super Landroid/support/v7/internal/view/menu/MenuPopupHelper;
.source "ActionMenuPresenter.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/widget/ActionMenuPresenter;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "ActionButtonSubmenu"
.end annotation


# instance fields
.field private mSubMenu:Landroid/support/v7/internal/view/menu/SubMenuBuilder;

.field final synthetic this$0:Landroid/support/v7/widget/ActionMenuPresenter;


# direct methods
.method public constructor <init>(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/content/Context;Landroid/support/v7/internal/view/menu/SubMenuBuilder;)V
    .locals 11
    .param p2, "context"    # Landroid/content/Context;
    .param p3, "subMenu"    # Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@0
    .prologue
    .line 686
    iput-object p1, p0, Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;->this$0:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    .line 687
    const/4 v3, 0x0

    #@3
    const/4 v4, 0x0

    #@4
    sget v5, Landroid/support/v7/appcompat/R$attr;->actionOverflowMenuStyle:I

    #@6
    move-object v0, p0

    #@7
    move-object v1, p2

    #@8
    move-object v2, p3

    #@9
    invoke-direct/range {v0 .. v5}, Landroid/support/v7/internal/view/menu/MenuPopupHelper;-><init>(Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/view/View;ZI)V

    #@c
    .line 689
    iput-object p3, p0, Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;->mSubMenu:Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@e
    .line 691
    invoke-virtual {p3}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->getItem()Landroid/view/MenuItem;

    #@11
    move-result-object v9

    #@12
    check-cast v9, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@14
    .line 692
    .local v9, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v9}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isActionButton()Z

    #@17
    move-result v0

    #@18
    if-nez v0, :cond_0

    #@1a
    .line 694
    # getter for: Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;
    invoke-static {p1}, Landroid/support/v7/widget/ActionMenuPresenter;->access$500(Landroid/support/v7/widget/ActionMenuPresenter;)Landroid/view/View;

    #@1d
    move-result-object v0

    #@1e
    if-nez v0, :cond_2

    #@20
    # getter for: Landroid/support/v7/widget/ActionMenuPresenter;->mMenuView:Landroid/support/v7/internal/view/menu/MenuView;
    invoke-static {p1}, Landroid/support/v7/widget/ActionMenuPresenter;->access$600(Landroid/support/v7/widget/ActionMenuPresenter;)Landroid/support/v7/internal/view/menu/MenuView;

    #@23
    move-result-object v0

    #@24
    check-cast v0, Landroid/view/View;

    #@26
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;->setAnchorView(Landroid/view/View;)V

    #@29
    .line 697
    :cond_0
    iget-object v0, p1, Landroid/support/v7/widget/ActionMenuPresenter;->mPopupPresenterCallback:Landroid/support/v7/widget/ActionMenuPresenter$PopupPresenterCallback;

    #@2b
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;->setCallback(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V

    #@2e
    .line 699
    const/4 v10, 0x0

    #@2f
    .line 700
    .local v10, "preserveIconSpacing":Z
    invoke-virtual {p3}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->size()I

    #@32
    move-result v7

    #@33
    .line 701
    .local v7, "count":I
    const/4 v8, 0x0

    #@34
    .local v8, "i":I
    :goto_1
    if-ge v8, v7, :cond_1

    #@36
    .line 702
    invoke-virtual {p3, v8}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->getItem(I)Landroid/view/MenuItem;

    #@39
    move-result-object v6

    #@3a
    .line 703
    .local v6, "childItem":Landroid/view/MenuItem;
    invoke-interface {v6}, Landroid/view/MenuItem;->isVisible()Z

    #@3d
    move-result v0

    #@3e
    if-eqz v0, :cond_3

    #@40
    invoke-interface {v6}, Landroid/view/MenuItem;->getIcon()Landroid/graphics/drawable/Drawable;

    #@43
    move-result-object v0

    #@44
    if-eqz v0, :cond_3

    #@46
    .line 704
    const/4 v10, 0x1

    #@47
    .line 708
    .end local v6    # "childItem":Landroid/view/MenuItem;
    :cond_1
    invoke-virtual {p0, v10}, Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;->setForceShowIcon(Z)V

    #@4a
    .line 709
    return-void

    #@4b
    .line 694
    .end local v7    # "count":I
    .end local v8    # "i":I
    .end local v10    # "preserveIconSpacing":Z
    :cond_2
    # getter for: Landroid/support/v7/widget/ActionMenuPresenter;->mOverflowButton:Landroid/view/View;
    invoke-static {p1}, Landroid/support/v7/widget/ActionMenuPresenter;->access$500(Landroid/support/v7/widget/ActionMenuPresenter;)Landroid/view/View;

    #@4e
    move-result-object v0

    #@4f
    goto :goto_0

    #@50
    .line 701
    .restart local v6    # "childItem":Landroid/view/MenuItem;
    .restart local v7    # "count":I
    .restart local v8    # "i":I
    .restart local v10    # "preserveIconSpacing":Z
    :cond_3
    add-int/lit8 v8, v8, 0x1

    #@52
    goto :goto_1
.end method


# virtual methods
.method public onDismiss()V
    .locals 2

    #@0
    .prologue
    .line 713
    invoke-super {p0}, Landroid/support/v7/internal/view/menu/MenuPopupHelper;->onDismiss()V

    #@3
    .line 714
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;->this$0:Landroid/support/v7/widget/ActionMenuPresenter;

    #@5
    const/4 v1, 0x0

    #@6
    # setter for: Landroid/support/v7/widget/ActionMenuPresenter;->mActionButtonPopup:Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;
    invoke-static {v0, v1}, Landroid/support/v7/widget/ActionMenuPresenter;->access$702(Landroid/support/v7/widget/ActionMenuPresenter;Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;)Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;

    #@9
    .line 715
    iget-object v0, p0, Landroid/support/v7/widget/ActionMenuPresenter$ActionButtonSubmenu;->this$0:Landroid/support/v7/widget/ActionMenuPresenter;

    #@b
    const/4 v1, 0x0

    #@c
    iput v1, v0, Landroid/support/v7/widget/ActionMenuPresenter;->mOpenSubMenuId:I

    #@e
    .line 716
    return-void
.end method
