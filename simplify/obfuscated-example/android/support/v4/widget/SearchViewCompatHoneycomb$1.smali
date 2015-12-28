.class final Landroid/support/v4/widget/SearchViewCompatHoneycomb$1;
.super Ljava/lang/Object;
.source "SearchViewCompatHoneycomb.java"

# interfaces
.implements Landroid/widget/SearchView$OnQueryTextListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v4/widget/SearchViewCompatHoneycomb;->newOnQueryTextListener(Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnQueryTextListenerCompatBridge;)Ljava/lang/Object;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = null
.end annotation


# instance fields
.field final synthetic val$listener:Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnQueryTextListenerCompatBridge;


# direct methods
.method constructor <init>(Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnQueryTextListenerCompatBridge;)V
    .locals 0

    #@0
    .prologue
    .line 53
    iput-object p1, p0, Landroid/support/v4/widget/SearchViewCompatHoneycomb$1;->val$listener:Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnQueryTextListenerCompatBridge;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onQueryTextChange(Ljava/lang/String;)Z
    .locals 1
    .param p1, "newText"    # Ljava/lang/String;

    #@0
    .prologue
    .line 61
    iget-object v0, p0, Landroid/support/v4/widget/SearchViewCompatHoneycomb$1;->val$listener:Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnQueryTextListenerCompatBridge;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnQueryTextListenerCompatBridge;->onQueryTextChange(Ljava/lang/String;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public onQueryTextSubmit(Ljava/lang/String;)Z
    .locals 1
    .param p1, "query"    # Ljava/lang/String;

    #@0
    .prologue
    .line 56
    iget-object v0, p0, Landroid/support/v4/widget/SearchViewCompatHoneycomb$1;->val$listener:Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnQueryTextListenerCompatBridge;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v4/widget/SearchViewCompatHoneycomb$OnQueryTextListenerCompatBridge;->onQueryTextSubmit(Ljava/lang/String;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method
