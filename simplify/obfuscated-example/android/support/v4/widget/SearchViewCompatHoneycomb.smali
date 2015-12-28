.class Landroid/support/v4/widget/SearchViewCompatHoneycomb;
.super Ljava/lang/Object;
.source "SearchViewCompatHoneycomb.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnCloseListenerCompatBridge;,
        Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnQueryTextListenerCompatBridge;
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 30
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 37
    return-void
.end method

.method public static getQuery(Landroid/view/View;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "searchView"    # Landroid/view/View;

    #@0
    .prologue
    .line 84
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Landroid/view/View;
    invoke-virtual {p0}, Landroid/widget/SearchView;->getQuery()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static isIconified(Landroid/view/View;)Z
    .locals 1
    .param p0, "searchView"    # Landroid/view/View;

    #@0
    .prologue
    .line 100
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Landroid/view/View;
    invoke-virtual {p0}, Landroid/widget/SearchView;->isIconified()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isQueryRefinementEnabled(Landroid/view/View;)Z
    .locals 1
    .param p0, "searchView"    # Landroid/view/View;

    #@0
    .prologue
    .line 116
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Landroid/view/View;
    invoke-virtual {p0}, Landroid/widget/SearchView;->isQueryRefinementEnabled()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isSubmitButtonEnabled(Landroid/view/View;)Z
    .locals 1
    .param p0, "searchView"    # Landroid/view/View;

    #@0
    .prologue
    .line 108
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Landroid/view/View;
    invoke-virtual {p0}, Landroid/widget/SearchView;->isSubmitButtonEnabled()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static newOnCloseListener(Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnCloseListenerCompatBridge;)Ljava/lang/Object;
    .locals 1
    .param p0, "listener"    # Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnCloseListenerCompatBridge;

    #@0
    .prologue
    .line 71
    new-instance v0, Landroid/support/v4/widget/SearchViewCompatHoneycomb$2;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v4/widget/SearchViewCompatHoneycomb$2;-><init>(Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnCloseListenerCompatBridge;)V

    #@5
    return-object v0
.end method

.method public static newOnQueryTextListener(Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnQueryTextListenerCompatBridge;)Ljava/lang/Object;
    .locals 1
    .param p0, "listener"    # Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnQueryTextListenerCompatBridge;

    #@0
    .prologue
    .line 53
    new-instance v0, Landroid/support/v4/widget/SearchViewCompatHoneycomb$1;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v4/widget/SearchViewCompatHoneycomb$1;-><init>(Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnQueryTextListenerCompatBridge;)V

    #@5
    return-object v0
.end method

.method public static newSearchView(Landroid/content/Context;)Landroid/view/View;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 42
    new-instance v0, Landroid/widget/SearchView;

    #@2
    invoke-direct {v0, p0}, Landroid/widget/SearchView;-><init>(Landroid/content/Context;)V

    #@5
    return-object v0
.end method

.method public static setIconified(Landroid/view/View;Z)V
    .locals 0
    .param p0, "searchView"    # Landroid/view/View;
    .param p1, "iconify"    # Z

    #@0
    .prologue
    .line 96
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Landroid/view/View;
    invoke-virtual {p0, p1}, Landroid/widget/SearchView;->setIconified(Z)V

    #@5
    .line 97
    return-void
.end method

.method public static setMaxWidth(Landroid/view/View;I)V
    .locals 0
    .param p0, "searchView"    # Landroid/view/View;
    .param p1, "maxpixels"    # I

    #@0
    .prologue
    .line 120
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Landroid/view/View;
    invoke-virtual {p0, p1}, Landroid/widget/SearchView;->setMaxWidth(I)V

    #@5
    .line 121
    return-void
.end method

.method public static setOnCloseListener(Ljava/lang/Object;Ljava/lang/Object;)V
    .locals 0
    .param p0, "searchView"    # Ljava/lang/Object;
    .param p1, "listener"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 80
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Ljava/lang/Object;
    check-cast p1, Landroid/widget/SearchView$OnCloseListener;

    #@4
    .end local p1    # "listener":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/widget/SearchView;->setOnCloseListener(Landroid/widget/SearchView$OnCloseListener;)V

    #@7
    .line 81
    return-void
.end method

.method public static setOnQueryTextListener(Ljava/lang/Object;Ljava/lang/Object;)V
    .locals 0
    .param p0, "searchView"    # Ljava/lang/Object;
    .param p1, "listener"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 67
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Ljava/lang/Object;
    check-cast p1, Landroid/widget/SearchView$OnQueryTextListener;

    #@4
    .end local p1    # "listener":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/widget/SearchView;->setOnQueryTextListener(Landroid/widget/SearchView$OnQueryTextListener;)V

    #@7
    .line 68
    return-void
.end method

.method public static setQuery(Landroid/view/View;Ljava/lang/CharSequence;Z)V
    .locals 0
    .param p0, "searchView"    # Landroid/view/View;
    .param p1, "query"    # Ljava/lang/CharSequence;
    .param p2, "submit"    # Z

    #@0
    .prologue
    .line 88
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Landroid/view/View;
    invoke-virtual {p0, p1, p2}, Landroid/widget/SearchView;->setQuery(Ljava/lang/CharSequence;Z)V

    #@5
    .line 89
    return-void
.end method

.method public static setQueryHint(Landroid/view/View;Ljava/lang/CharSequence;)V
    .locals 0
    .param p0, "searchView"    # Landroid/view/View;
    .param p1, "hint"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 92
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Landroid/view/View;
    invoke-virtual {p0, p1}, Landroid/widget/SearchView;->setQueryHint(Ljava/lang/CharSequence;)V

    #@5
    .line 93
    return-void
.end method

.method public static setQueryRefinementEnabled(Landroid/view/View;Z)V
    .locals 0
    .param p0, "searchView"    # Landroid/view/View;
    .param p1, "enable"    # Z

    #@0
    .prologue
    .line 112
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Landroid/view/View;
    invoke-virtual {p0, p1}, Landroid/widget/SearchView;->setQueryRefinementEnabled(Z)V

    #@5
    .line 113
    return-void
.end method

.method public static setSearchableInfo(Landroid/view/View;Landroid/content/ComponentName;)V
    .locals 4
    .param p0, "searchView"    # Landroid/view/View;
    .param p1, "searchableComponent"    # Landroid/content/ComponentName;

    #@0
    .prologue
    .line 46
    move-object v1, p0

    #@1
    check-cast v1, Landroid/widget/SearchView;

    #@3
    .line 47
    .local v1, "sv":Landroid/widget/SearchView;
    invoke-virtual {v1}, Landroid/widget/SearchView;->getContext()Landroid/content/Context;

    #@6
    move-result-object v2

    #@7
    const-string v3, "search"

    #@9
    invoke-virtual {v2, v3}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@c
    move-result-object v0

    #@d
    check-cast v0, Landroid/app/SearchManager;

    #@f
    .line 49
    .local v0, "searchManager":Landroid/app/SearchManager;
    invoke-virtual {v0, p1}, Landroid/app/SearchManager;->getSearchableInfo(Landroid/content/ComponentName;)Landroid/app/SearchableInfo;

    #@12
    move-result-object v2

    #@13
    invoke-virtual {v1, v2}, Landroid/widget/SearchView;->setSearchableInfo(Landroid/app/SearchableInfo;)V

    #@16
    .line 50
    return-void
.end method

.method public static setSubmitButtonEnabled(Landroid/view/View;Z)V
    .locals 0
    .param p0, "searchView"    # Landroid/view/View;
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 104
    check-cast p0, Landroid/widget/SearchView;

    #@2
    .end local p0    # "searchView":Landroid/view/View;
    invoke-virtual {p0, p1}, Landroid/widget/SearchView;->setSubmitButtonEnabled(Z)V

    #@5
    .line 105
    return-void
.end method
