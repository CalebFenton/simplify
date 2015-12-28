.class Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;
.super Landroid/database/DataSetObserver;
.source "AdapterViewCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/AdapterViewCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = "AdapterDataSetObserver"
.end annotation


# instance fields
.field private mInstanceState:Landroid/os/Parcelable;

.field final synthetic this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/widget/AdapterViewCompat;)V
    .locals 1

    #@0
    .prologue
    .line 798
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>.AdapterDataSetObserver;"
    iput-object p1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@2
    invoke-direct {p0}, Landroid/database/DataSetObserver;-><init>()V

    #@5
    .line 800
    const/4 v0, 0x0

    #@6
    iput-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->mInstanceState:Landroid/os/Parcelable;

    #@8
    return-void
.end method


# virtual methods
.method public clearSavedState()V
    .locals 1

    #@0
    .prologue
    .line 845
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>.AdapterDataSetObserver;"
    const/4 v0, 0x0

    #@1
    iput-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->mInstanceState:Landroid/os/Parcelable;

    #@3
    .line 846
    return-void
.end method

.method public onChanged()V
    .locals 2

    #@0
    .prologue
    .line 804
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>.AdapterDataSetObserver;"
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@2
    const/4 v1, 0x1

    #@3
    iput-boolean v1, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mDataChanged:Z

    #@5
    .line 805
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@7
    iget-object v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@9
    iget v1, v1, Landroid/support/v7/internal/widget/AdapterViewCompat;->mItemCount:I

    #@b
    iput v1, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOldItemCount:I

    #@d
    .line 806
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@f
    iget-object v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@11
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@14
    move-result-object v1

    #@15
    invoke-interface {v1}, Landroid/widget/Adapter;->getCount()I

    #@18
    move-result v1

    #@19
    iput v1, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mItemCount:I

    #@1b
    .line 810
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@1d
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@20
    move-result-object v0

    #@21
    invoke-interface {v0}, Landroid/widget/Adapter;->hasStableIds()Z

    #@24
    move-result v0

    #@25
    if-eqz v0, :cond_0

    #@27
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->mInstanceState:Landroid/os/Parcelable;

    #@29
    if-eqz v0, :cond_0

    #@2b
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@2d
    iget v0, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOldItemCount:I

    #@2f
    if-nez v0, :cond_0

    #@31
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@33
    iget v0, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mItemCount:I

    #@35
    if-lez v0, :cond_0

    #@37
    .line 812
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@39
    iget-object v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->mInstanceState:Landroid/os/Parcelable;

    #@3b
    # invokes: Landroid/support/v7/internal/widget/AdapterViewCompat;->onRestoreInstanceState(Landroid/os/Parcelable;)V
    invoke-static {v0, v1}, Landroid/support/v7/internal/widget/AdapterViewCompat;->access$000(Landroid/support/v7/internal/widget/AdapterViewCompat;Landroid/os/Parcelable;)V

    #@3e
    .line 813
    const/4 v0, 0x0

    #@3f
    iput-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->mInstanceState:Landroid/os/Parcelable;

    #@41
    .line 817
    :goto_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@43
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->checkFocus()V

    #@46
    .line 818
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@48
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->requestLayout()V

    #@4b
    .line 819
    return-void

    #@4c
    .line 815
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@4e
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->rememberSyncState()V

    #@51
    goto :goto_0
.end method

.method public onInvalidated()V
    .locals 6

    #@0
    .prologue
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>.AdapterDataSetObserver;"
    const-wide/high16 v4, -0x8000000000000000L

    #@2
    const/4 v3, 0x0

    #@3
    const/4 v2, -0x1

    #@4
    .line 823
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@6
    const/4 v1, 0x1

    #@7
    iput-boolean v1, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mDataChanged:Z

    #@9
    .line 825
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@b
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@e
    move-result-object v0

    #@f
    invoke-interface {v0}, Landroid/widget/Adapter;->hasStableIds()Z

    #@12
    move-result v0

    #@13
    if-eqz v0, :cond_0

    #@15
    .line 828
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@17
    # invokes: Landroid/support/v7/internal/widget/AdapterViewCompat;->onSaveInstanceState()Landroid/os/Parcelable;
    invoke-static {v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->access$100(Landroid/support/v7/internal/widget/AdapterViewCompat;)Landroid/os/Parcelable;

    #@1a
    move-result-object v0

    #@1b
    iput-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->mInstanceState:Landroid/os/Parcelable;

    #@1d
    .line 832
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@1f
    iget-object v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@21
    iget v1, v1, Landroid/support/v7/internal/widget/AdapterViewCompat;->mItemCount:I

    #@23
    iput v1, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOldItemCount:I

    #@25
    .line 833
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@27
    iput v3, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mItemCount:I

    #@29
    .line 834
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@2b
    iput v2, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedPosition:I

    #@2d
    .line 835
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@2f
    iput-wide v4, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedRowId:J

    #@31
    .line 836
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@33
    iput v2, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedPosition:I

    #@35
    .line 837
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@37
    iput-wide v4, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedRowId:J

    #@39
    .line 838
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@3b
    iput-boolean v3, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNeedSync:Z

    #@3d
    .line 840
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@3f
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->checkFocus()V

    #@42
    .line 841
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@44
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->requestLayout()V

    #@47
    .line 842
    return-void
.end method
