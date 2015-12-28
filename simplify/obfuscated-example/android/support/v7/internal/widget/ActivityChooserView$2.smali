.class Landroid/support/v7/internal/widget/ActivityChooserView$2;
.super Ljava/lang/Object;
.source "ActivityChooserView.java"

# interfaces
.implements Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/ActivityChooserView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/widget/ActivityChooserView;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/widget/ActivityChooserView;)V
    .locals 0

    #@0
    .prologue
    .line 138
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$2;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

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
    .line 141
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$2;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActivityChooserView;->isShowingPopup()Z

    #@5
    move-result v0

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 142
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$2;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@a
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActivityChooserView;->isShown()Z

    #@d
    move-result v0

    #@e
    if-nez v0, :cond_1

    #@10
    .line 143
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$2;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@12
    # invokes: Landroid/support/v7/internal/widget/ActivityChooserView;->getListPopupWindow()Landroid/support/v7/widget/ListPopupWindow;
    invoke-static {v0}, Landroid/support/v7/internal/widget/ActivityChooserView;->access$100(Landroid/support/v7/internal/widget/ActivityChooserView;)Landroid/support/v7/widget/ListPopupWindow;

    #@15
    move-result-object v0

    #@16
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow;->dismiss()V

    #@19
    .line 151
    :cond_0
    :goto_0
    return-void

    #@1a
    .line 145
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$2;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@1c
    # invokes: Landroid/support/v7/internal/widget/ActivityChooserView;->getListPopupWindow()Landroid/support/v7/widget/ListPopupWindow;
    invoke-static {v0}, Landroid/support/v7/internal/widget/ActivityChooserView;->access$100(Landroid/support/v7/internal/widget/ActivityChooserView;)Landroid/support/v7/widget/ListPopupWindow;

    #@1f
    move-result-object v0

    #@20
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow;->show()V

    #@23
    .line 146
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$2;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@25
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActivityChooserView;->mProvider:Landroid/support/v4/view/ActionProvider;

    #@27
    if-eqz v0, :cond_0

    #@29
    .line 147
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$2;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@2b
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActivityChooserView;->mProvider:Landroid/support/v4/view/ActionProvider;

    #@2d
    const/4 v1, 0x1

    #@2e
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ActionProvider;->subUiVisibilityChanged(Z)V

    #@31
    goto :goto_0
.end method
