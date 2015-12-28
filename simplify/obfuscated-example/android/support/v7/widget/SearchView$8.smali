.class Landroid/support/v7/widget/SearchView$8;
.super Ljava/lang/Object;
.source "SearchView.java"

# interfaces
.implements Landroid/view/View$OnKeyListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/widget/SearchView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/widget/SearchView;


# direct methods
.method constructor <init>(Landroid/support/v7/widget/SearchView;)V
    .locals 0

    #@0
    .prologue
    .line 928
    iput-object p1, p0, Landroid/support/v7/widget/SearchView$8;->this$0:Landroid/support/v7/widget/SearchView;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onKey(Landroid/view/View;ILandroid/view/KeyEvent;)Z
    .locals 5
    .param p1, "v"    # Landroid/view/View;
    .param p2, "keyCode"    # I
    .param p3, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v0, 0x0

    #@2
    .line 931
    iget-object v2, p0, Landroid/support/v7/widget/SearchView$8;->this$0:Landroid/support/v7/widget/SearchView;

    #@4
    # getter for: Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;
    invoke-static {v2}, Landroid/support/v7/widget/SearchView;->access$1500(Landroid/support/v7/widget/SearchView;)Landroid/app/SearchableInfo;

    #@7
    move-result-object v2

    #@8
    if-nez v2, :cond_1

    #@a
    .line 961
    :cond_0
    :goto_0
    return v0

    #@b
    .line 942
    :cond_1
    iget-object v2, p0, Landroid/support/v7/widget/SearchView$8;->this$0:Landroid/support/v7/widget/SearchView;

    #@d
    # getter for: Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;
    invoke-static {v2}, Landroid/support/v7/widget/SearchView;->access$1300(Landroid/support/v7/widget/SearchView;)Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@10
    move-result-object v2

    #@11
    invoke-virtual {v2}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->isPopupShowing()Z

    #@14
    move-result v2

    #@15
    if-eqz v2, :cond_2

    #@17
    iget-object v2, p0, Landroid/support/v7/widget/SearchView$8;->this$0:Landroid/support/v7/widget/SearchView;

    #@19
    # getter for: Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;
    invoke-static {v2}, Landroid/support/v7/widget/SearchView;->access$1300(Landroid/support/v7/widget/SearchView;)Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@1c
    move-result-object v2

    #@1d
    invoke-virtual {v2}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getListSelection()I

    #@20
    move-result v2

    #@21
    const/4 v3, -0x1

    #@22
    if-eq v2, v3, :cond_2

    #@24
    .line 944
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$8;->this$0:Landroid/support/v7/widget/SearchView;

    #@26
    # invokes: Landroid/support/v7/widget/SearchView;->onSuggestionsKey(Landroid/view/View;ILandroid/view/KeyEvent;)Z
    invoke-static {v0, p1, p2, p3}, Landroid/support/v7/widget/SearchView;->access$1600(Landroid/support/v7/widget/SearchView;Landroid/view/View;ILandroid/view/KeyEvent;)Z

    #@29
    move-result v0

    #@2a
    goto :goto_0

    #@2b
    .line 949
    :cond_2
    iget-object v2, p0, Landroid/support/v7/widget/SearchView$8;->this$0:Landroid/support/v7/widget/SearchView;

    #@2d
    # getter for: Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;
    invoke-static {v2}, Landroid/support/v7/widget/SearchView;->access$1300(Landroid/support/v7/widget/SearchView;)Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@30
    move-result-object v2

    #@31
    # invokes: Landroid/support/v7/widget/SearchView$SearchAutoComplete;->isEmpty()Z
    invoke-static {v2}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->access$1700(Landroid/support/v7/widget/SearchView$SearchAutoComplete;)Z

    #@34
    move-result v2

    #@35
    if-nez v2, :cond_0

    #@37
    invoke-static {p3}, Landroid/support/v4/view/KeyEventCompat;->hasNoModifiers(Landroid/view/KeyEvent;)Z

    #@3a
    move-result v2

    #@3b
    if-eqz v2, :cond_0

    #@3d
    .line 950
    invoke-virtual {p3}, Landroid/view/KeyEvent;->getAction()I

    #@40
    move-result v2

    #@41
    if-ne v2, v1, :cond_0

    #@43
    .line 951
    const/16 v2, 0x42

    #@45
    if-ne p2, v2, :cond_0

    #@47
    .line 952
    invoke-virtual {p1}, Landroid/view/View;->cancelLongPress()V

    #@4a
    .line 955
    iget-object v2, p0, Landroid/support/v7/widget/SearchView$8;->this$0:Landroid/support/v7/widget/SearchView;

    #@4c
    const/4 v3, 0x0

    #@4d
    iget-object v4, p0, Landroid/support/v7/widget/SearchView$8;->this$0:Landroid/support/v7/widget/SearchView;

    #@4f
    # getter for: Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;
    invoke-static {v4}, Landroid/support/v7/widget/SearchView;->access$1300(Landroid/support/v7/widget/SearchView;)Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@52
    move-result-object v4

    #@53
    invoke-virtual {v4}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getText()Landroid/text/Editable;

    #@56
    move-result-object v4

    #@57
    invoke-virtual {v4}, Ljava/lang/Object;->toString()Ljava/lang/String;

    #@5a
    move-result-object v4

    #@5b
    # invokes: Landroid/support/v7/widget/SearchView;->launchQuerySearch(ILjava/lang/String;Ljava/lang/String;)V
    invoke-static {v2, v0, v3, v4}, Landroid/support/v7/widget/SearchView;->access$1800(Landroid/support/v7/widget/SearchView;ILjava/lang/String;Ljava/lang/String;)V

    #@5e
    move v0, v1

    #@5f
    .line 957
    goto :goto_0
.end method
