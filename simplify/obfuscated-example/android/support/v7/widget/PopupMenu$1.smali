.class Landroid/support/v7/widget/PopupMenu$1;
.super Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;
.source "PopupMenu.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/widget/PopupMenu;->getDragToOpenListener()Landroid/view/View$OnTouchListener;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/widget/PopupMenu;


# direct methods
.method constructor <init>(Landroid/support/v7/widget/PopupMenu;Landroid/view/View;)V
    .locals 0
    .param p2, "x0"    # Landroid/view/View;

    #@0
    .prologue
    .line 134
    iput-object p1, p0, Landroid/support/v7/widget/PopupMenu$1;->this$0:Landroid/support/v7/widget/PopupMenu;

    #@2
    invoke-direct {p0, p2}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;-><init>(Landroid/view/View;)V

    #@5
    return-void
.end method


# virtual methods
.method public getPopup()Landroid/support/v7/widget/ListPopupWindow;
    .locals 1

    #@0
    .prologue
    .line 150
    iget-object v0, p0, Landroid/support/v7/widget/PopupMenu$1;->this$0:Landroid/support/v7/widget/PopupMenu;

    #@2
    # getter for: Landroid/support/v7/widget/PopupMenu;->mPopup:Landroid/support/v7/internal/view/menu/MenuPopupHelper;
    invoke-static {v0}, Landroid/support/v7/widget/PopupMenu;->access$000(Landroid/support/v7/widget/PopupMenu;)Landroid/support/v7/internal/view/menu/MenuPopupHelper;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuPopupHelper;->getPopup()Landroid/support/v7/widget/ListPopupWindow;

    #@9
    move-result-object v0

    #@a
    return-object v0
.end method

.method protected onForwardingStarted()Z
    .locals 1

    #@0
    .prologue
    .line 137
    iget-object v0, p0, Landroid/support/v7/widget/PopupMenu$1;->this$0:Landroid/support/v7/widget/PopupMenu;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/PopupMenu;->show()V

    #@5
    .line 138
    const/4 v0, 0x1

    #@6
    return v0
.end method

.method protected onForwardingStopped()Z
    .locals 1

    #@0
    .prologue
    .line 143
    iget-object v0, p0, Landroid/support/v7/widget/PopupMenu$1;->this$0:Landroid/support/v7/widget/PopupMenu;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/PopupMenu;->dismiss()V

    #@5
    .line 144
    const/4 v0, 0x1

    #@6
    return v0
.end method
