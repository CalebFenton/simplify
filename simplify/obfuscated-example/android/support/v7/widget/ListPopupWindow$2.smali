.class Landroid/support/v7/widget/ListPopupWindow$2;
.super Ljava/lang/Object;
.source "ListPopupWindow.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/widget/ListPopupWindow;->buildDropDown()I
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/widget/ListPopupWindow;


# direct methods
.method constructor <init>(Landroid/support/v7/widget/ListPopupWindow;)V
    .locals 0

    #@0
    .prologue
    .line 1060
    iput-object p1, p0, Landroid/support/v7/widget/ListPopupWindow$2;->this$0:Landroid/support/v7/widget/ListPopupWindow;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public run()V
    .locals 2

    #@0
    .prologue
    .line 1063
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow$2;->this$0:Landroid/support/v7/widget/ListPopupWindow;

    #@2
    invoke-virtual {v1}, Landroid/support/v7/widget/ListPopupWindow;->getAnchorView()Landroid/view/View;

    #@5
    move-result-object v0

    #@6
    .line 1064
    .local v0, "view":Landroid/view/View;
    if-eqz v0, :cond_0

    #@8
    invoke-virtual {v0}, Landroid/view/View;->getWindowToken()Landroid/os/IBinder;

    #@b
    move-result-object v1

    #@c
    if-eqz v1, :cond_0

    #@e
    .line 1065
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow$2;->this$0:Landroid/support/v7/widget/ListPopupWindow;

    #@10
    invoke-virtual {v1}, Landroid/support/v7/widget/ListPopupWindow;->show()V

    #@13
    .line 1067
    :cond_0
    return-void
.end method
