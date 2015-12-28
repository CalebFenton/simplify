.class Landroid/support/v7/widget/ListPopupWindow$1;
.super Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;
.source "ListPopupWindow.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/widget/ListPopupWindow;->createDragToOpenListener(Landroid/view/View;)Landroid/view/View$OnTouchListener;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/widget/ListPopupWindow;


# direct methods
.method constructor <init>(Landroid/support/v7/widget/ListPopupWindow;Landroid/view/View;)V
    .locals 0
    .param p2, "x0"    # Landroid/view/View;

    #@0
    .prologue
    .line 1033
    iput-object p1, p0, Landroid/support/v7/widget/ListPopupWindow$1;->this$0:Landroid/support/v7/widget/ListPopupWindow;

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
    .line 1036
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow$1;->this$0:Landroid/support/v7/widget/ListPopupWindow;

    #@2
    return-object v0
.end method
