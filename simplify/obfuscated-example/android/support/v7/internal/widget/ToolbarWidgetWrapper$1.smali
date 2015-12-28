.class Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$1;
.super Ljava/lang/Object;
.source "ToolbarWidgetWrapper.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;-><init>(Landroid/support/v7/widget/Toolbar;ZII)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final mNavItem:Landroid/support/v7/internal/view/menu/ActionMenuItem;

.field final synthetic this$0:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)V
    .locals 7

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 184
    iput-object p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$1;->this$0:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@3
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@6
    .line 185
    new-instance v0, Landroid/support/v7/internal/view/menu/ActionMenuItem;

    #@8
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$1;->this$0:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@a
    # getter for: Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;
    invoke-static {v1}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->access$000(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)Landroid/support/v7/widget/Toolbar;

    #@d
    move-result-object v1

    #@e
    invoke-virtual {v1}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@11
    move-result-object v1

    #@12
    const v3, 0x102002c

    #@15
    iget-object v4, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$1;->this$0:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@17
    # getter for: Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTitle:Ljava/lang/CharSequence;
    invoke-static {v4}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->access$100(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)Ljava/lang/CharSequence;

    #@1a
    move-result-object v6

    #@1b
    move v4, v2

    #@1c
    move v5, v2

    #@1d
    invoke-direct/range {v0 .. v6}, Landroid/support/v7/internal/view/menu/ActionMenuItem;-><init>(Landroid/content/Context;IIIILjava/lang/CharSequence;)V

    #@20
    iput-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$1;->mNavItem:Landroid/support/v7/internal/view/menu/ActionMenuItem;

    #@22
    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    #@0
    .prologue
    .line 189
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$1;->this$0:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@2
    # getter for: Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;
    invoke-static {v0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->access$200(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)Landroid/support/v7/internal/app/WindowCallback;

    #@5
    move-result-object v0

    #@6
    if-eqz v0, :cond_0

    #@8
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$1;->this$0:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@a
    # getter for: Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mMenuPrepared:Z
    invoke-static {v0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->access$300(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)Z

    #@d
    move-result v0

    #@e
    if-eqz v0, :cond_0

    #@10
    .line 190
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$1;->this$0:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@12
    # getter for: Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;
    invoke-static {v0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->access$200(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)Landroid/support/v7/internal/app/WindowCallback;

    #@15
    move-result-object v0

    #@16
    const/4 v1, 0x0

    #@17
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$1;->mNavItem:Landroid/support/v7/internal/view/menu/ActionMenuItem;

    #@19
    invoke-interface {v0, v1, v2}, Landroid/support/v7/internal/app/WindowCallback;->onMenuItemSelected(ILandroid/view/MenuItem;)Z

    #@1c
    .line 192
    :cond_0
    return-void
.end method
