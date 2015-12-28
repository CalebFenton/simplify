.class public Landroid/support/v4/widget/SearchViewCompatIcs$MySearchView;
.super Landroid/widget/SearchView;
.source "SearchViewCompatIcs.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/SearchViewCompatIcs;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "MySearchView"
.end annotation


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 0
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 30
    invoke-direct {p0, p1}, Landroid/widget/SearchView;-><init>(Landroid/content/Context;)V

    #@3
    .line 31
    return-void
.end method


# virtual methods
.method public onActionViewCollapsed()V
    .locals 2

    #@0
    .prologue
    .line 37
    const-string v0, ""

    #@2
    const/4 v1, 0x0

    #@3
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/widget/SearchViewCompatIcs$MySearchView;->setQuery(Ljava/lang/CharSequence;Z)V

    #@6
    .line 38
    invoke-super {p0}, Landroid/widget/SearchView;->onActionViewCollapsed()V

    #@9
    .line 39
    return-void
.end method
