.class Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup$3;
.super Ljava/lang/Object;
.source "SpinnerCompat.java"

# interfaces
.implements Landroid/widget/PopupWindow$OnDismissListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->show(II)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$1:Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;

.field final synthetic val$layoutListener:Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;)V
    .locals 0

    #@0
    .prologue
    .line 1088
    iput-object p1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup$3;->this$1:Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;

    #@2
    iput-object p2, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup$3;->val$layoutListener:Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;

    #@4
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@7
    return-void
.end method


# virtual methods
.method public onDismiss()V
    .locals 2

    #@0
    .prologue
    .line 1091
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup$3;->this$1:Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;

    #@2
    iget-object v1, v1, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@4
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/SpinnerCompat;->getViewTreeObserver()Landroid/view/ViewTreeObserver;

    #@7
    move-result-object v0

    #@8
    .line 1092
    .local v0, "vto":Landroid/view/ViewTreeObserver;
    if-eqz v0, :cond_0

    #@a
    .line 1093
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup$3;->val$layoutListener:Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;

    #@c
    invoke-virtual {v0, v1}, Landroid/view/ViewTreeObserver;->removeGlobalOnLayoutListener(Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;)V

    #@f
    .line 1095
    :cond_0
    return-void
.end method
