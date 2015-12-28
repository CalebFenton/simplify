.class Landroid/support/v7/app/ActionBarActivityDelegateBase$4;
.super Ljava/lang/Object;
.source "ActionBarActivityDelegateBase.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/app/ActionBarActivityDelegateBase;->startSupportActionModeFromWindow(Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;


# direct methods
.method constructor <init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;)V
    .locals 0

    #@0
    .prologue
    .line 626
    iput-object p1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$4;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public run()V
    .locals 4

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 628
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$4;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@3
    iget-object v0, v0, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModePopup:Landroid/widget/PopupWindow;

    #@5
    iget-object v1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$4;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@7
    iget-object v1, v1, Landroid/support/v7/app/ActionBarActivityDelegateBase;->mActionModeView:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@9
    const/16 v2, 0x37

    #@b
    invoke-virtual {v0, v1, v2, v3, v3}, Landroid/widget/PopupWindow;->showAtLocation(Landroid/view/View;III)V

    #@e
    .line 631
    return-void
.end method
