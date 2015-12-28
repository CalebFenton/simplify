.class Landroid/support/v4/widget/SearchViewCompatIcs;
.super Ljava/lang/Object;
.source "SearchViewCompatIcs.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/widget/SearchViewCompatIcs$MySearchView;
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 26
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 28
    return-void
.end method

.method public static newSearchView(Landroid/content/Context;)Landroid/view/View;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 43
    new-instance v0, Landroid/support/v4/widget/SearchViewCompatIcs$MySearchView;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v4/widget/SearchViewCompatIcs$MySearchView;-><init>(Landroid/content/Context;)V

    #@5
    return-object v0
.end method

.method public static setImeOptions(Landroid/view/View;I)V
    .locals 0
    .param p0, "searchView"    # Landroid/view/View;
    .param p1, "imeOptions"    # I

    #@0
    .prologue
    .line 47
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Landroid/view/View;
    invoke-virtual {p0, p1}, Landroid/widget/SearchView;->setImeOptions(I)V

    #@5
    .line 48
    return-void
.end method

.method public static setInputType(Landroid/view/View;I)V
    .locals 0
    .param p0, "searchView"    # Landroid/view/View;
    .param p1, "inputType"    # I

    #@0
    .prologue
    .line 51
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Landroid/view/View;
    invoke-virtual {p0, p1}, Landroid/widget/SearchView;->setInputType(I)V

    #@5
    .line 52
    return-void
.end method
