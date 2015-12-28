.class Landroid/support/v7/widget/ListPopupWindow$ForwardingListener$DisallowIntercept;
.super Ljava/lang/Object;
.source "ListPopupWindow.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "DisallowIntercept"
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;


# direct methods
.method private constructor <init>(Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;)V
    .locals 0

    #@0
    .prologue
    .line 1474
    iput-object p1, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener$DisallowIntercept;->this$0:Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;Landroid/support/v7/widget/ListPopupWindow$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;
    .param p2, "x1"    # Landroid/support/v7/widget/ListPopupWindow$1;

    #@0
    .prologue
    .line 1474
    invoke-direct {p0, p1}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener$DisallowIntercept;-><init>(Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;)V

    #@3
    return-void
.end method


# virtual methods
.method public run()V
    .locals 2

    #@0
    .prologue
    .line 1477
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener$DisallowIntercept;->this$0:Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;

    #@2
    # getter for: Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mSrc:Landroid/view/View;
    invoke-static {v1}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->access$900(Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;)Landroid/view/View;

    #@5
    move-result-object v1

    #@6
    invoke-virtual {v1}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@9
    move-result-object v0

    #@a
    .line 1478
    .local v0, "parent":Landroid/view/ViewParent;
    const/4 v1, 0x1

    #@b
    invoke-interface {v0, v1}, Landroid/view/ViewParent;->requestDisallowInterceptTouchEvent(Z)V

    #@e
    .line 1479
    return-void
.end method
