.class Landroid/support/v7/internal/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;
.super Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;
.source "ActionMenuItemView.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/view/menu/ActionMenuItemView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "ActionMenuItemForwardingListener"
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/view/menu/ActionMenuItemView;


# direct methods
.method public constructor <init>(Landroid/support/v7/internal/view/menu/ActionMenuItemView;)V
    .locals 0

    #@0
    .prologue
    .line 300
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->this$0:Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@2
    .line 301
    invoke-direct {p0, p1}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;-><init>(Landroid/view/View;)V

    #@5
    .line 302
    return-void
.end method


# virtual methods
.method public getPopup()Landroid/support/v7/widget/ListPopupWindow;
    .locals 1

    #@0
    .prologue
    .line 306
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->this$0:Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@2
    # getter for: Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mPopupCallback:Landroid/support/v7/internal/view/menu/ActionMenuItemView$PopupCallback;
    invoke-static {v0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->access$000(Landroid/support/v7/internal/view/menu/ActionMenuItemView;)Landroid/support/v7/internal/view/menu/ActionMenuItemView$PopupCallback;

    #@5
    move-result-object v0

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 307
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->this$0:Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@a
    # getter for: Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mPopupCallback:Landroid/support/v7/internal/view/menu/ActionMenuItemView$PopupCallback;
    invoke-static {v0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->access$000(Landroid/support/v7/internal/view/menu/ActionMenuItemView;)Landroid/support/v7/internal/view/menu/ActionMenuItemView$PopupCallback;

    #@d
    move-result-object v0

    #@e
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView$PopupCallback;->getPopup()Landroid/support/v7/widget/ListPopupWindow;

    #@11
    move-result-object v0

    #@12
    .line 309
    :goto_0
    return-object v0

    #@13
    :cond_0
    const/4 v0, 0x0

    #@14
    goto :goto_0
.end method

.method protected onForwardingStarted()Z
    .locals 4

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 315
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->this$0:Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@3
    # getter for: Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemInvoker:Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;
    invoke-static {v2}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->access$100(Landroid/support/v7/internal/view/menu/ActionMenuItemView;)Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;

    #@6
    move-result-object v2

    #@7
    if-eqz v2, :cond_0

    #@9
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->this$0:Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@b
    # getter for: Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemInvoker:Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;
    invoke-static {v2}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->access$100(Landroid/support/v7/internal/view/menu/ActionMenuItemView;)Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;

    #@e
    move-result-object v2

    #@f
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->this$0:Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@11
    # getter for: Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-static {v3}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->access$200(Landroid/support/v7/internal/view/menu/ActionMenuItemView;)Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@14
    move-result-object v3

    #@15
    invoke-interface {v2, v3}, Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;->invokeItem(Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z

    #@18
    move-result v2

    #@19
    if-eqz v2, :cond_0

    #@1b
    .line 316
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->getPopup()Landroid/support/v7/widget/ListPopupWindow;

    #@1e
    move-result-object v0

    #@1f
    .line 317
    .local v0, "popup":Landroid/support/v7/widget/ListPopupWindow;
    if-eqz v0, :cond_0

    #@21
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@24
    move-result v2

    #@25
    if-eqz v2, :cond_0

    #@27
    const/4 v1, 0x1

    #@28
    .line 319
    .end local v0    # "popup":Landroid/support/v7/widget/ListPopupWindow;
    :cond_0
    return v1
.end method

.method protected onForwardingStopped()Z
    .locals 2

    #@0
    .prologue
    .line 324
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;->getPopup()Landroid/support/v7/widget/ListPopupWindow;

    #@3
    move-result-object v0

    #@4
    .line 325
    .local v0, "popup":Landroid/support/v7/widget/ListPopupWindow;
    if-eqz v0, :cond_0

    #@6
    .line 326
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow;->dismiss()V

    #@9
    .line 327
    const/4 v1, 0x1

    #@a
    .line 329
    :goto_0
    return v1

    #@b
    :cond_0
    const/4 v1, 0x0

    #@c
    goto :goto_0
.end method
