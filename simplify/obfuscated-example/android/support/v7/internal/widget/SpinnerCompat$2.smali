.class Landroid/support/v7/internal/widget/SpinnerCompat$2;
.super Ljava/lang/Object;
.source "SpinnerCompat.java"

# interfaces
.implements Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/internal/widget/SpinnerCompat;->onRestoreInstanceState(Landroid/os/Parcelable;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/widget/SpinnerCompat;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/widget/SpinnerCompat;)V
    .locals 0

    #@0
    .prologue
    .line 699
    iput-object p1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$2;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onGlobalLayout()V
    .locals 2

    #@0
    .prologue
    .line 702
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$2;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@2
    # getter for: Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;
    invoke-static {v1}, Landroid/support/v7/internal/widget/SpinnerCompat;->access$100(Landroid/support/v7/internal/widget/SpinnerCompat;)Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@5
    move-result-object v1

    #@6
    invoke-interface {v1}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->isShowing()Z

    #@9
    move-result v1

    #@a
    if-nez v1, :cond_0

    #@c
    .line 703
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$2;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@e
    # getter for: Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;
    invoke-static {v1}, Landroid/support/v7/internal/widget/SpinnerCompat;->access$100(Landroid/support/v7/internal/widget/SpinnerCompat;)Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@11
    move-result-object v1

    #@12
    invoke-interface {v1}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->show()V

    #@15
    .line 705
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$2;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@17
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/SpinnerCompat;->getViewTreeObserver()Landroid/view/ViewTreeObserver;

    #@1a
    move-result-object v0

    #@1b
    .line 706
    .local v0, "vto":Landroid/view/ViewTreeObserver;
    if-eqz v0, :cond_1

    #@1d
    .line 707
    invoke-virtual {v0, p0}, Landroid/view/ViewTreeObserver;->removeGlobalOnLayoutListener(Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;)V

    #@20
    .line 709
    :cond_1
    return-void
.end method
