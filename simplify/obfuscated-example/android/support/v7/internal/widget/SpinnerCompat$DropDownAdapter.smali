.class Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;
.super Ljava/lang/Object;
.source "SpinnerCompat.java"

# interfaces
.implements Landroid/widget/ListAdapter;
.implements Landroid/widget/SpinnerAdapter;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/SpinnerCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "DropDownAdapter"
.end annotation


# instance fields
.field private mAdapter:Landroid/widget/SpinnerAdapter;

.field private mListAdapter:Landroid/widget/ListAdapter;


# direct methods
.method public constructor <init>(Landroid/widget/SpinnerAdapter;)V
    .locals 1
    .param p1, "adapter"    # Landroid/widget/SpinnerAdapter;

    #@0
    .prologue
    .line 762
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 763
    iput-object p1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@5
    .line 764
    instance-of v0, p1, Landroid/widget/ListAdapter;

    #@7
    if-eqz v0, :cond_0

    #@9
    .line 765
    check-cast p1, Landroid/widget/ListAdapter;

    #@b
    .end local p1    # "adapter":Landroid/widget/SpinnerAdapter;
    iput-object p1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mListAdapter:Landroid/widget/ListAdapter;

    #@d
    .line 767
    :cond_0
    return-void
.end method


# virtual methods
.method public areAllItemsEnabled()Z
    .locals 2

    #@0
    .prologue
    .line 811
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mListAdapter:Landroid/widget/ListAdapter;

    #@2
    .line 812
    .local v0, "adapter":Landroid/widget/ListAdapter;
    if-eqz v0, :cond_0

    #@4
    .line 813
    invoke-interface {v0}, Landroid/widget/ListAdapter;->areAllItemsEnabled()Z

    #@7
    move-result v1

    #@8
    .line 815
    :goto_0
    return v1

    #@9
    :cond_0
    const/4 v1, 0x1

    #@a
    goto :goto_0
.end method

.method public getCount()I
    .locals 1

    #@0
    .prologue
    .line 770
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@2
    if-nez v0, :cond_0

    #@4
    const/4 v0, 0x0

    #@5
    :goto_0
    return v0

    #@6
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@8
    invoke-interface {v0}, Landroid/widget/SpinnerAdapter;->getCount()I

    #@b
    move-result v0

    #@c
    goto :goto_0
.end method

.method public getDropDownView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 1
    .param p1, "position"    # I
    .param p2, "convertView"    # Landroid/view/View;
    .param p3, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 786
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@2
    if-nez v0, :cond_0

    #@4
    const/4 v0, 0x0

    #@5
    :goto_0
    return-object v0

    #@6
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@8
    invoke-interface {v0, p1, p2, p3}, Landroid/widget/SpinnerAdapter;->getDropDownView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;

    #@b
    move-result-object v0

    #@c
    goto :goto_0
.end method

.method public getItem(I)Ljava/lang/Object;
    .locals 1
    .param p1, "position"    # I

    #@0
    .prologue
    .line 774
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@2
    if-nez v0, :cond_0

    #@4
    const/4 v0, 0x0

    #@5
    :goto_0
    return-object v0

    #@6
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@8
    invoke-interface {v0, p1}, Landroid/widget/SpinnerAdapter;->getItem(I)Ljava/lang/Object;

    #@b
    move-result-object v0

    #@c
    goto :goto_0
.end method

.method public getItemId(I)J
    .locals 2
    .param p1, "position"    # I

    #@0
    .prologue
    .line 778
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@2
    if-nez v0, :cond_0

    #@4
    const-wide/16 v0, -0x1

    #@6
    :goto_0
    return-wide v0

    #@7
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@9
    invoke-interface {v0, p1}, Landroid/widget/SpinnerAdapter;->getItemId(I)J

    #@c
    move-result-wide v0

    #@d
    goto :goto_0
.end method

.method public getItemViewType(I)I
    .locals 1
    .param p1, "position"    # I

    #@0
    .prologue
    .line 833
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 1
    .param p1, "position"    # I
    .param p2, "convertView"    # Landroid/view/View;
    .param p3, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 782
    invoke-virtual {p0, p1, p2, p3}, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->getDropDownView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public getViewTypeCount()I
    .locals 1

    #@0
    .prologue
    .line 837
    const/4 v0, 0x1

    #@1
    return v0
.end method

.method public hasStableIds()Z
    .locals 1

    #@0
    .prologue
    .line 791
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@6
    invoke-interface {v0}, Landroid/widget/SpinnerAdapter;->hasStableIds()Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public isEmpty()Z
    .locals 1

    #@0
    .prologue
    .line 841
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->getCount()I

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method public isEnabled(I)Z
    .locals 2
    .param p1, "position"    # I

    #@0
    .prologue
    .line 824
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mListAdapter:Landroid/widget/ListAdapter;

    #@2
    .line 825
    .local v0, "adapter":Landroid/widget/ListAdapter;
    if-eqz v0, :cond_0

    #@4
    .line 826
    invoke-interface {v0, p1}, Landroid/widget/ListAdapter;->isEnabled(I)Z

    #@7
    move-result v1

    #@8
    .line 828
    :goto_0
    return v1

    #@9
    :cond_0
    const/4 v1, 0x1

    #@a
    goto :goto_0
.end method

.method public registerDataSetObserver(Landroid/database/DataSetObserver;)V
    .locals 1
    .param p1, "observer"    # Landroid/database/DataSetObserver;

    #@0
    .prologue
    .line 795
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 796
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@6
    invoke-interface {v0, p1}, Landroid/widget/SpinnerAdapter;->registerDataSetObserver(Landroid/database/DataSetObserver;)V

    #@9
    .line 798
    :cond_0
    return-void
.end method

.method public unregisterDataSetObserver(Landroid/database/DataSetObserver;)V
    .locals 1
    .param p1, "observer"    # Landroid/database/DataSetObserver;

    #@0
    .prologue
    .line 801
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 802
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@6
    invoke-interface {v0, p1}, Landroid/widget/SpinnerAdapter;->unregisterDataSetObserver(Landroid/database/DataSetObserver;)V

    #@9
    .line 804
    :cond_0
    return-void
.end method
