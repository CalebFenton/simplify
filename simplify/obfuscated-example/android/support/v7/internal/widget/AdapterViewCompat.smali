.class public abstract Landroid/support/v7/internal/widget/AdapterViewCompat;
.super Landroid/view/ViewGroup;
.source "AdapterViewCompat.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/widget/AdapterViewCompat$1;,
        Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;,
        Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterDataSetObserver;,
        Landroid/support/v7/internal/widget/AdapterViewCompat$AdapterContextMenuInfo;,
        Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;,
        Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemLongClickListener;,
        Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListenerWrapper;,
        Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;
    }
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<T::",
        "Landroid/widget/Adapter;",
        ">",
        "Landroid/view/ViewGroup;"
    }
.end annotation


# static fields
.field public static final INVALID_POSITION:I = -0x1

.field public static final INVALID_ROW_ID:J = -0x8000000000000000L

.field static final ITEM_VIEW_TYPE_HEADER_OR_FOOTER:I = -0x2

.field static final ITEM_VIEW_TYPE_IGNORE:I = -0x1

.field static final SYNC_FIRST_POSITION:I = 0x1

.field static final SYNC_MAX_DURATION_MILLIS:I = 0x64

.field static final SYNC_SELECTED_POSITION:I


# instance fields
.field mBlockLayoutRequests:Z

.field mDataChanged:Z

.field private mDesiredFocusableInTouchModeState:Z

.field private mDesiredFocusableState:Z

.field private mEmptyView:Landroid/view/View;

.field mFirstPosition:I
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "scrolling"
    .end annotation
.end field

.field mInLayout:Z

.field mItemCount:I
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "list"
    .end annotation
.end field

.field private mLayoutHeight:I

.field mNeedSync:Z

.field mNextSelectedPosition:I
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "list"
    .end annotation
.end field

.field mNextSelectedRowId:J

.field mOldItemCount:I

.field mOldSelectedPosition:I

.field mOldSelectedRowId:J

.field mOnItemClickListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;

.field mOnItemLongClickListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemLongClickListener;

.field mOnItemSelectedListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;

.field mSelectedPosition:I
    .annotation runtime Landroid/view/ViewDebug$ExportedProperty;
        category = "list"
    .end annotation
.end field

.field mSelectedRowId:J

.field private mSelectionNotifier:Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/support/v7/internal/widget/AdapterViewCompat",
            "<TT;>.SelectionNotifier;"
        }
    .end annotation
.end field

.field mSpecificTop:I

.field mSyncHeight:J

.field mSyncMode:I

.field mSyncPosition:I

.field mSyncRowId:J


# direct methods
.method constructor <init>(Landroid/content/Context;)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    const/4 v1, -0x1

    #@1
    const-wide/high16 v2, -0x8000000000000000L

    #@3
    const/4 v0, 0x0

    #@4
    .line 230
    invoke-direct {p0, p1}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;)V

    #@7
    .line 69
    iput v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mFirstPosition:I

    #@9
    .line 86
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncRowId:J

    #@b
    .line 96
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNeedSync:Z

    #@d
    .line 128
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mInLayout:Z

    #@f
    .line 154
    iput v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedPosition:I

    #@11
    .line 160
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedRowId:J

    #@13
    .line 165
    iput v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedPosition:I

    #@15
    .line 171
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedRowId:J

    #@17
    .line 203
    iput v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOldSelectedPosition:I

    #@19
    .line 208
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOldSelectedRowId:J

    #@1b
    .line 227
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mBlockLayoutRequests:Z

    #@1d
    .line 231
    return-void
.end method

.method constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    const/4 v1, -0x1

    #@1
    const-wide/high16 v2, -0x8000000000000000L

    #@3
    const/4 v0, 0x0

    #@4
    .line 234
    invoke-direct {p0, p1, p2}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@7
    .line 69
    iput v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mFirstPosition:I

    #@9
    .line 86
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncRowId:J

    #@b
    .line 96
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNeedSync:Z

    #@d
    .line 128
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mInLayout:Z

    #@f
    .line 154
    iput v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedPosition:I

    #@11
    .line 160
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedRowId:J

    #@13
    .line 165
    iput v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedPosition:I

    #@15
    .line 171
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedRowId:J

    #@17
    .line 203
    iput v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOldSelectedPosition:I

    #@19
    .line 208
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOldSelectedRowId:J

    #@1b
    .line 227
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mBlockLayoutRequests:Z

    #@1d
    .line 235
    return-void
.end method

.method constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyle"    # I

    #@0
    .prologue
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    const/4 v1, -0x1

    #@1
    const-wide/high16 v2, -0x8000000000000000L

    #@3
    const/4 v0, 0x0

    #@4
    .line 238
    invoke-direct {p0, p1, p2, p3}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@7
    .line 69
    iput v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mFirstPosition:I

    #@9
    .line 86
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncRowId:J

    #@b
    .line 96
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNeedSync:Z

    #@d
    .line 128
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mInLayout:Z

    #@f
    .line 154
    iput v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedPosition:I

    #@11
    .line 160
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedRowId:J

    #@13
    .line 165
    iput v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedPosition:I

    #@15
    .line 171
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedRowId:J

    #@17
    .line 203
    iput v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOldSelectedPosition:I

    #@19
    .line 208
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOldSelectedRowId:J

    #@1b
    .line 227
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mBlockLayoutRequests:Z

    #@1d
    .line 239
    return-void
.end method

.method static synthetic access$000(Landroid/support/v7/internal/widget/AdapterViewCompat;Landroid/os/Parcelable;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/internal/widget/AdapterViewCompat;
    .param p1, "x1"    # Landroid/os/Parcelable;

    #@0
    .prologue
    .line 52
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/AdapterViewCompat;->onRestoreInstanceState(Landroid/os/Parcelable;)V

    #@3
    return-void
.end method

.method static synthetic access$100(Landroid/support/v7/internal/widget/AdapterViewCompat;)Landroid/os/Parcelable;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@0
    .prologue
    .line 52
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->onSaveInstanceState()Landroid/os/Parcelable;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method static synthetic access$200(Landroid/support/v7/internal/widget/AdapterViewCompat;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@0
    .prologue
    .line 52
    invoke-direct {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->fireOnSelected()V

    #@3
    return-void
.end method

.method private fireOnSelected()V
    .locals 6

    #@0
    .prologue
    .line 893
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOnItemSelectedListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 904
    :goto_0
    return-void

    #@5
    .line 896
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getSelectedItemPosition()I

    #@8
    move-result v3

    #@9
    .line 897
    .local v3, "selection":I
    if-ltz v3, :cond_1

    #@b
    .line 898
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getSelectedView()Landroid/view/View;

    #@e
    move-result-object v2

    #@f
    .line 899
    .local v2, "v":Landroid/view/View;
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOnItemSelectedListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;

    #@11
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@14
    move-result-object v1

    #@15
    invoke-interface {v1, v3}, Landroid/widget/Adapter;->getItemId(I)J

    #@18
    move-result-wide v4

    #@19
    move-object v1, p0

    #@1a
    invoke-interface/range {v0 .. v5}, Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;->onItemSelected(Landroid/support/v7/internal/widget/AdapterViewCompat;Landroid/view/View;IJ)V

    #@1d
    goto :goto_0

    #@1e
    .line 902
    .end local v2    # "v":Landroid/view/View;
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOnItemSelectedListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;

    #@20
    invoke-interface {v0, p0}, Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;->onNothingSelected(Landroid/support/v7/internal/widget/AdapterViewCompat;)V

    #@23
    goto :goto_0
.end method

.method private updateEmptyStatus(Z)V
    .locals 6
    .param p1, "empty"    # Z

    #@0
    .prologue
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    const/16 v2, 0x8

    #@2
    const/4 v1, 0x0

    #@3
    .line 735
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->isInFilterMode()Z

    #@6
    move-result v0

    #@7
    if-eqz v0, :cond_0

    #@9
    .line 736
    const/4 p1, 0x0

    #@a
    .line 739
    :cond_0
    if-eqz p1, :cond_3

    #@c
    .line 740
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mEmptyView:Landroid/view/View;

    #@e
    if-eqz v0, :cond_2

    #@10
    .line 741
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mEmptyView:Landroid/view/View;

    #@12
    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    #@15
    .line 742
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/AdapterViewCompat;->setVisibility(I)V

    #@18
    .line 751
    :goto_0
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mDataChanged:Z

    #@1a
    if-eqz v0, :cond_1

    #@1c
    .line 752
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getLeft()I

    #@1f
    move-result v2

    #@20
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getTop()I

    #@23
    move-result v3

    #@24
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getRight()I

    #@27
    move-result v4

    #@28
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getBottom()I

    #@2b
    move-result v5

    #@2c
    move-object v0, p0

    #@2d
    invoke-virtual/range {v0 .. v5}, Landroid/support/v7/internal/widget/AdapterViewCompat;->onLayout(ZIIII)V

    #@30
    .line 758
    :cond_1
    :goto_1
    return-void

    #@31
    .line 745
    :cond_2
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/AdapterViewCompat;->setVisibility(I)V

    #@34
    goto :goto_0

    #@35
    .line 755
    :cond_3
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mEmptyView:Landroid/view/View;

    #@37
    if-eqz v0, :cond_4

    #@39
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mEmptyView:Landroid/view/View;

    #@3b
    invoke-virtual {v0, v2}, Landroid/view/View;->setVisibility(I)V

    #@3e
    .line 756
    :cond_4
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/AdapterViewCompat;->setVisibility(I)V

    #@41
    goto :goto_1
.end method


# virtual methods
.method public addView(Landroid/view/View;)V
    .locals 2
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 463
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "addView(View) is not supported in AdapterView"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public addView(Landroid/view/View;I)V
    .locals 2
    .param p1, "child"    # Landroid/view/View;
    .param p2, "index"    # I

    #@0
    .prologue
    .line 476
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "addView(View, int) is not supported in AdapterView"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public addView(Landroid/view/View;ILandroid/view/ViewGroup$LayoutParams;)V
    .locals 2
    .param p1, "child"    # Landroid/view/View;
    .param p2, "index"    # I
    .param p3, "params"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 504
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "addView(View, int, LayoutParams) is not supported in AdapterView"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    .locals 2
    .param p1, "child"    # Landroid/view/View;
    .param p2, "params"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 489
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "addView(View, LayoutParams) is not supported in AdapterView"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method protected canAnimate()Z
    .locals 1

    #@0
    .prologue
    .line 918
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    invoke-super {p0}, Landroid/view/ViewGroup;->canAnimate()Z

    #@3
    move-result v0

    #@4
    if-eqz v0, :cond_0

    #@6
    iget v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mItemCount:I

    #@8
    if-lez v0, :cond_0

    #@a
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_0
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method checkFocus()V
    .locals 6

    #@0
    .prologue
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    const/4 v4, 0x1

    #@1
    const/4 v5, 0x0

    #@2
    .line 716
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@5
    move-result-object v0

    #@6
    .line 717
    .local v0, "adapter":Landroid/widget/Adapter;, "TT;"
    if-eqz v0, :cond_0

    #@8
    invoke-interface {v0}, Landroid/widget/Adapter;->getCount()I

    #@b
    move-result v3

    #@c
    if-nez v3, :cond_5

    #@e
    :cond_0
    move v1, v4

    #@f
    .line 718
    .local v1, "empty":Z
    :goto_0
    if-eqz v1, :cond_1

    #@11
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->isInFilterMode()Z

    #@14
    move-result v3

    #@15
    if-eqz v3, :cond_6

    #@17
    :cond_1
    move v2, v4

    #@18
    .line 722
    .local v2, "focusable":Z
    :goto_1
    if-eqz v2, :cond_7

    #@1a
    iget-boolean v3, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mDesiredFocusableInTouchModeState:Z

    #@1c
    if-eqz v3, :cond_7

    #@1e
    move v3, v4

    #@1f
    :goto_2
    invoke-super {p0, v3}, Landroid/view/ViewGroup;->setFocusableInTouchMode(Z)V

    #@22
    .line 723
    if-eqz v2, :cond_8

    #@24
    iget-boolean v3, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mDesiredFocusableState:Z

    #@26
    if-eqz v3, :cond_8

    #@28
    move v3, v4

    #@29
    :goto_3
    invoke-super {p0, v3}, Landroid/view/ViewGroup;->setFocusable(Z)V

    #@2c
    .line 724
    iget-object v3, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mEmptyView:Landroid/view/View;

    #@2e
    if-eqz v3, :cond_4

    #@30
    .line 725
    if-eqz v0, :cond_2

    #@32
    invoke-interface {v0}, Landroid/widget/Adapter;->isEmpty()Z

    #@35
    move-result v3

    #@36
    if-eqz v3, :cond_3

    #@38
    :cond_2
    move v5, v4

    #@39
    :cond_3
    invoke-direct {p0, v5}, Landroid/support/v7/internal/widget/AdapterViewCompat;->updateEmptyStatus(Z)V

    #@3c
    .line 727
    :cond_4
    return-void

    #@3d
    .end local v1    # "empty":Z
    .end local v2    # "focusable":Z
    :cond_5
    move v1, v5

    #@3e
    .line 717
    goto :goto_0

    #@3f
    .restart local v1    # "empty":Z
    :cond_6
    move v2, v5

    #@40
    .line 718
    goto :goto_1

    #@41
    .restart local v2    # "focusable":Z
    :cond_7
    move v3, v5

    #@42
    .line 722
    goto :goto_2

    #@43
    :cond_8
    move v3, v5

    #@44
    .line 723
    goto :goto_3
.end method

.method checkSelectionChanged()V
    .locals 4

    #@0
    .prologue
    .line 985
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iget v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedPosition:I

    #@2
    iget v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOldSelectedPosition:I

    #@4
    if-ne v0, v1, :cond_0

    #@6
    iget-wide v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedRowId:J

    #@8
    iget-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOldSelectedRowId:J

    #@a
    cmp-long v0, v0, v2

    #@c
    if-eqz v0, :cond_1

    #@e
    .line 986
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->selectionChanged()V

    #@11
    .line 987
    iget v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedPosition:I

    #@13
    iput v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOldSelectedPosition:I

    #@15
    .line 988
    iget-wide v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedRowId:J

    #@17
    iput-wide v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOldSelectedRowId:J

    #@19
    .line 990
    :cond_1
    return-void
.end method

.method public dispatchPopulateAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)Z
    .locals 2
    .param p1, "event"    # Landroid/view/accessibility/AccessibilityEvent;

    #@0
    .prologue
    .line 908
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getSelectedView()Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    .line 909
    .local v0, "selectedView":Landroid/view/View;
    if-eqz v0, :cond_0

    #@6
    invoke-virtual {v0}, Landroid/view/View;->getVisibility()I

    #@9
    move-result v1

    #@a
    if-nez v1, :cond_0

    #@c
    invoke-virtual {v0, p1}, Landroid/view/View;->dispatchPopulateAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)Z

    #@f
    move-result v1

    #@10
    if-eqz v1, :cond_0

    #@12
    .line 911
    const/4 v1, 0x1

    #@13
    .line 913
    :goto_0
    return v1

    #@14
    :cond_0
    const/4 v1, 0x0

    #@15
    goto :goto_0
.end method

.method protected dispatchRestoreInstanceState(Landroid/util/SparseArray;)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/os/Parcelable;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 795
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    .local p1, "container":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/AdapterViewCompat;->dispatchThawSelfOnly(Landroid/util/SparseArray;)V

    #@3
    .line 796
    return-void
.end method

.method protected dispatchSaveInstanceState(Landroid/util/SparseArray;)V
    .locals 0
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/os/Parcelable;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 787
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    .local p1, "container":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/AdapterViewCompat;->dispatchFreezeSelfOnly(Landroid/util/SparseArray;)V

    #@3
    .line 788
    return-void
.end method

.method findSyncPosition()I
    .locals 20

    #@0
    .prologue
    .line 1001
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    move-object/from16 v0, p0

    #@2
    iget v3, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mItemCount:I

    #@4
    .line 1003
    .local v3, "count":I
    if-nez v3, :cond_1

    #@6
    .line 1004
    const/4 v13, -0x1

    #@7
    .line 1076
    :cond_0
    :goto_0
    return v13

    #@8
    .line 1007
    :cond_1
    move-object/from16 v0, p0

    #@a
    iget-wide v10, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncRowId:J

    #@c
    .line 1008
    .local v10, "idToMatch":J
    move-object/from16 v0, p0

    #@e
    iget v13, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncPosition:I

    #@10
    .line 1011
    .local v13, "seed":I
    const-wide/high16 v16, -0x8000000000000000L

    #@12
    cmp-long v16, v10, v16

    #@14
    if-nez v16, :cond_2

    #@16
    .line 1012
    const/4 v13, -0x1

    #@17
    goto :goto_0

    #@18
    .line 1016
    :cond_2
    const/16 v16, 0x0

    #@1a
    move/from16 v0, v16

    #@1c
    invoke-static {v0, v13}, Ljava/lang/Math;->max(II)I

    #@1f
    move-result v13

    #@20
    .line 1017
    add-int/lit8 v16, v3, -0x1

    #@22
    move/from16 v0, v16

    #@24
    invoke-static {v0, v13}, Ljava/lang/Math;->min(II)I

    #@27
    move-result v13

    #@28
    .line 1019
    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    #@2b
    move-result-wide v16

    #@2c
    const-wide/16 v18, 0x64

    #@2e
    add-long v4, v16, v18

    #@30
    .line 1024
    .local v4, "endTime":J
    move v6, v13

    #@31
    .line 1027
    .local v6, "first":I
    move v9, v13

    #@32
    .line 1030
    .local v9, "last":I
    const/4 v12, 0x0

    #@33
    .line 1040
    .local v12, "next":Z
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@36
    move-result-object v2

    #@37
    .line 1041
    .local v2, "adapter":Landroid/widget/Adapter;, "TT;"
    if-nez v2, :cond_5

    #@39
    .line 1042
    const/4 v13, -0x1

    #@3a
    goto :goto_0

    #@3b
    .line 1060
    .local v7, "hitFirst":Z
    .local v8, "hitLast":Z
    .local v14, "rowId":J
    :cond_3
    if-nez v7, :cond_4

    #@3d
    if-eqz v12, :cond_9

    #@3f
    if-nez v8, :cond_9

    #@41
    .line 1062
    :cond_4
    add-int/lit8 v9, v9, 0x1

    #@43
    .line 1063
    move v13, v9

    #@44
    .line 1065
    const/4 v12, 0x0

    #@45
    .line 1045
    .end local v7    # "hitFirst":Z
    .end local v8    # "hitLast":Z
    .end local v14    # "rowId":J
    :cond_5
    :goto_1
    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    #@48
    move-result-wide v16

    #@49
    cmp-long v16, v16, v4

    #@4b
    if-gtz v16, :cond_6

    #@4d
    .line 1046
    invoke-interface {v2, v13}, Landroid/widget/Adapter;->getItemId(I)J

    #@50
    move-result-wide v14

    #@51
    .line 1047
    .restart local v14    # "rowId":J
    cmp-long v16, v14, v10

    #@53
    if-eqz v16, :cond_0

    #@55
    .line 1052
    add-int/lit8 v16, v3, -0x1

    #@57
    move/from16 v0, v16

    #@59
    if-ne v9, v0, :cond_7

    #@5b
    const/4 v8, 0x1

    #@5c
    .line 1053
    .restart local v8    # "hitLast":Z
    :goto_2
    if-nez v6, :cond_8

    #@5e
    const/4 v7, 0x1

    #@5f
    .line 1055
    .restart local v7    # "hitFirst":Z
    :goto_3
    if-eqz v8, :cond_3

    #@61
    if-eqz v7, :cond_3

    #@63
    .line 1076
    .end local v7    # "hitFirst":Z
    .end local v8    # "hitLast":Z
    .end local v14    # "rowId":J
    :cond_6
    const/4 v13, -0x1

    #@64
    goto :goto_0

    #@65
    .line 1052
    .restart local v14    # "rowId":J
    :cond_7
    const/4 v8, 0x0

    #@66
    goto :goto_2

    #@67
    .line 1053
    .restart local v8    # "hitLast":Z
    :cond_8
    const/4 v7, 0x0

    #@68
    goto :goto_3

    #@69
    .line 1066
    .restart local v7    # "hitFirst":Z
    :cond_9
    if-nez v8, :cond_a

    #@6b
    if-nez v12, :cond_5

    #@6d
    if-nez v7, :cond_5

    #@6f
    .line 1068
    :cond_a
    add-int/lit8 v6, v6, -0x1

    #@71
    .line 1069
    move v13, v6

    #@72
    .line 1071
    const/4 v12, 0x1

    #@73
    goto :goto_1
.end method

.method public abstract getAdapter()Landroid/widget/Adapter;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TT;"
        }
    .end annotation
.end method

.method public getCount()I
    .locals 1
    .annotation runtime Landroid/view/ViewDebug$CapturedViewProperty;
    .end annotation

    #@0
    .prologue
    .line 593
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iget v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mItemCount:I

    #@2
    return v0
.end method

.method public getEmptyView()Landroid/view/View;
    .locals 1

    #@0
    .prologue
    .line 676
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mEmptyView:Landroid/view/View;

    #@2
    return-object v0
.end method

.method public getFirstVisiblePosition()I
    .locals 1

    #@0
    .prologue
    .line 636
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iget v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mFirstPosition:I

    #@2
    return v0
.end method

.method public getItemAtPosition(I)Ljava/lang/Object;
    .locals 2
    .param p1, "position"    # I

    #@0
    .prologue
    .line 767
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@3
    move-result-object v0

    #@4
    .line 768
    .local v0, "adapter":Landroid/widget/Adapter;, "TT;"
    if-eqz v0, :cond_0

    #@6
    if-gez p1, :cond_1

    #@8
    :cond_0
    const/4 v1, 0x0

    #@9
    :goto_0
    return-object v1

    #@a
    :cond_1
    invoke-interface {v0, p1}, Landroid/widget/Adapter;->getItem(I)Ljava/lang/Object;

    #@d
    move-result-object v1

    #@e
    goto :goto_0
.end method

.method public getItemIdAtPosition(I)J
    .locals 4
    .param p1, "position"    # I

    #@0
    .prologue
    .line 772
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@3
    move-result-object v0

    #@4
    .line 773
    .local v0, "adapter":Landroid/widget/Adapter;, "TT;"
    if-eqz v0, :cond_0

    #@6
    if-gez p1, :cond_1

    #@8
    :cond_0
    const-wide/high16 v2, -0x8000000000000000L

    #@a
    :goto_0
    return-wide v2

    #@b
    :cond_1
    invoke-interface {v0, p1}, Landroid/widget/Adapter;->getItemId(I)J

    #@e
    move-result-wide v2

    #@f
    goto :goto_0
.end method

.method public getLastVisiblePosition()I
    .locals 2

    #@0
    .prologue
    .line 646
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iget v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mFirstPosition:I

    #@2
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getChildCount()I

    #@5
    move-result v1

    #@6
    add-int/2addr v0, v1

    #@7
    add-int/lit8 v0, v0, -0x1

    #@9
    return v0
.end method

.method public final getOnItemClickListener()Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;
    .locals 1

    #@0
    .prologue
    .line 292
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOnItemClickListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;

    #@2
    return-object v0
.end method

.method public final getOnItemLongClickListener()Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemLongClickListener;
    .locals 1

    #@0
    .prologue
    .line 358
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOnItemLongClickListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemLongClickListener;

    #@2
    return-object v0
.end method

.method public final getOnItemSelectedListener()Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;
    .locals 1

    #@0
    .prologue
    .line 404
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOnItemSelectedListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;

    #@2
    return-object v0
.end method

.method public getPositionForView(Landroid/view/View;)I
    .locals 7
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    const/4 v5, -0x1

    #@1
    .line 606
    move-object v3, p1

    #@2
    .line 609
    .local v3, "listItem":Landroid/view/View;
    :goto_0
    :try_start_0
    invoke-virtual {v3}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@5
    move-result-object v4

    #@6
    check-cast v4, Landroid/view/View;

    #@8
    .local v4, "v":Landroid/view/View;
    invoke-virtual {v4, p0}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z
    :try_end_0
    .catch Ljava/lang/ClassCastException; {:try_start_0 .. :try_end_0} :catch_0

    #@b
    move-result v6

    #@c
    if-nez v6, :cond_1

    #@e
    .line 610
    move-object v3, v4

    #@f
    goto :goto_0

    #@10
    .line 612
    .end local v4    # "v":Landroid/view/View;
    :catch_0
    move-exception v1

    #@11
    .line 626
    :cond_0
    :goto_1
    return v5

    #@12
    .line 618
    .restart local v4    # "v":Landroid/view/View;
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getChildCount()I

    #@15
    move-result v0

    #@16
    .line 619
    .local v0, "childCount":I
    const/4 v2, 0x0

    #@17
    .local v2, "i":I
    :goto_2
    if-ge v2, v0, :cond_0

    #@19
    .line 620
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getChildAt(I)Landroid/view/View;

    #@1c
    move-result-object v6

    #@1d
    invoke-virtual {v6, v3}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z

    #@20
    move-result v6

    #@21
    if-eqz v6, :cond_2

    #@23
    .line 621
    iget v5, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mFirstPosition:I

    #@25
    add-int/2addr v5, v2

    #@26
    goto :goto_1

    #@27
    .line 619
    :cond_2
    add-int/lit8 v2, v2, 0x1

    #@29
    goto :goto_2
.end method

.method public getSelectedItem()Ljava/lang/Object;
    .locals 3

    #@0
    .prologue
    .line 577
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@3
    move-result-object v0

    #@4
    .line 578
    .local v0, "adapter":Landroid/widget/Adapter;, "TT;"
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getSelectedItemPosition()I

    #@7
    move-result v1

    #@8
    .line 579
    .local v1, "selection":I
    if-eqz v0, :cond_0

    #@a
    invoke-interface {v0}, Landroid/widget/Adapter;->getCount()I

    #@d
    move-result v2

    #@e
    if-lez v2, :cond_0

    #@10
    if-ltz v1, :cond_0

    #@12
    .line 580
    invoke-interface {v0, v1}, Landroid/widget/Adapter;->getItem(I)Ljava/lang/Object;

    #@15
    move-result-object v2

    #@16
    .line 582
    :goto_0
    return-object v2

    #@17
    :cond_0
    const/4 v2, 0x0

    #@18
    goto :goto_0
.end method

.method public getSelectedItemId()J
    .locals 2
    .annotation runtime Landroid/view/ViewDebug$CapturedViewProperty;
    .end annotation

    #@0
    .prologue
    .line 563
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iget-wide v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedRowId:J

    #@2
    return-wide v0
.end method

.method public getSelectedItemPosition()I
    .locals 1
    .annotation runtime Landroid/view/ViewDebug$CapturedViewProperty;
    .end annotation

    #@0
    .prologue
    .line 554
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iget v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedPosition:I

    #@2
    return v0
.end method

.method public abstract getSelectedView()Landroid/view/View;
.end method

.method handleDataChanged()V
    .locals 10

    #@0
    .prologue
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    const-wide/high16 v8, -0x8000000000000000L

    #@2
    const/4 v7, 0x1

    #@3
    const/4 v6, -0x1

    #@4
    const/4 v5, 0x0

    #@5
    .line 922
    iget v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mItemCount:I

    #@7
    .line 923
    .local v0, "count":I
    const/4 v1, 0x0

    #@8
    .line 925
    .local v1, "found":Z
    if-lez v0, :cond_4

    #@a
    .line 930
    iget-boolean v4, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNeedSync:Z

    #@c
    if-eqz v4, :cond_0

    #@e
    .line 933
    iput-boolean v5, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNeedSync:Z

    #@10
    .line 937
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->findSyncPosition()I

    #@13
    move-result v2

    #@14
    .line 938
    .local v2, "newPos":I
    if-ltz v2, :cond_0

    #@16
    .line 940
    invoke-virtual {p0, v2, v7}, Landroid/support/v7/internal/widget/AdapterViewCompat;->lookForSelectablePosition(IZ)I

    #@19
    move-result v3

    #@1a
    .line 941
    .local v3, "selectablePos":I
    if-ne v3, v2, :cond_0

    #@1c
    .line 943
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/AdapterViewCompat;->setNextSelectedPositionInt(I)V

    #@1f
    .line 944
    const/4 v1, 0x1

    #@20
    .line 948
    .end local v2    # "newPos":I
    .end local v3    # "selectablePos":I
    :cond_0
    if-nez v1, :cond_4

    #@22
    .line 950
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getSelectedItemPosition()I

    #@25
    move-result v2

    #@26
    .line 953
    .restart local v2    # "newPos":I
    if-lt v2, v0, :cond_1

    #@28
    .line 954
    add-int/lit8 v2, v0, -0x1

    #@2a
    .line 956
    :cond_1
    if-gez v2, :cond_2

    #@2c
    .line 957
    const/4 v2, 0x0

    #@2d
    .line 961
    :cond_2
    invoke-virtual {p0, v2, v7}, Landroid/support/v7/internal/widget/AdapterViewCompat;->lookForSelectablePosition(IZ)I

    #@30
    move-result v3

    #@31
    .line 962
    .restart local v3    # "selectablePos":I
    if-gez v3, :cond_3

    #@33
    .line 964
    invoke-virtual {p0, v2, v5}, Landroid/support/v7/internal/widget/AdapterViewCompat;->lookForSelectablePosition(IZ)I

    #@36
    move-result v3

    #@37
    .line 966
    :cond_3
    if-ltz v3, :cond_4

    #@39
    .line 967
    invoke-virtual {p0, v3}, Landroid/support/v7/internal/widget/AdapterViewCompat;->setNextSelectedPositionInt(I)V

    #@3c
    .line 968
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->checkSelectionChanged()V

    #@3f
    .line 969
    const/4 v1, 0x1

    #@40
    .line 973
    .end local v2    # "newPos":I
    .end local v3    # "selectablePos":I
    :cond_4
    if-nez v1, :cond_5

    #@42
    .line 975
    iput v6, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedPosition:I

    #@44
    .line 976
    iput-wide v8, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedRowId:J

    #@46
    .line 977
    iput v6, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedPosition:I

    #@48
    .line 978
    iput-wide v8, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedRowId:J

    #@4a
    .line 979
    iput-boolean v5, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNeedSync:Z

    #@4c
    .line 980
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->checkSelectionChanged()V

    #@4f
    .line 982
    :cond_5
    return-void
.end method

.method isInFilterMode()Z
    .locals 1

    #@0
    .prologue
    .line 686
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method lookForSelectablePosition(IZ)I
    .locals 0
    .param p1, "position"    # I
    .param p2, "lookDown"    # Z

    #@0
    .prologue
    .line 1088
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    return p1
.end method

.method protected onDetachedFromWindow()V
    .locals 1

    #@0
    .prologue
    .line 851
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    invoke-super {p0}, Landroid/view/ViewGroup;->onDetachedFromWindow()V

    #@3
    .line 852
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectionNotifier:Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;

    #@5
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@8
    .line 853
    return-void
.end method

.method protected onLayout(ZIIII)V
    .locals 1
    .param p1, "changed"    # Z
    .param p2, "left"    # I
    .param p3, "top"    # I
    .param p4, "right"    # I
    .param p5, "bottom"    # I

    #@0
    .prologue
    .line 544
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getHeight()I

    #@3
    move-result v0

    #@4
    iput v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mLayoutHeight:I

    #@6
    .line 545
    return-void
.end method

.method public performItemClick(Landroid/view/View;IJ)Z
    .locals 7
    .param p1, "view"    # Landroid/view/View;
    .param p2, "position"    # I
    .param p3, "id"    # J

    #@0
    .prologue
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    const/4 v6, 0x1

    #@1
    const/4 v0, 0x0

    #@2
    .line 305
    iget-object v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOnItemClickListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;

    #@4
    if-eqz v1, :cond_1

    #@6
    .line 306
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->playSoundEffect(I)V

    #@9
    .line 307
    if-eqz p1, :cond_0

    #@b
    .line 308
    invoke-virtual {p1, v6}, Landroid/view/View;->sendAccessibilityEvent(I)V

    #@e
    .line 310
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOnItemClickListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;

    #@10
    move-object v1, p0

    #@11
    move-object v2, p1

    #@12
    move v3, p2

    #@13
    move-wide v4, p3

    #@14
    invoke-interface/range {v0 .. v5}, Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;->onItemClick(Landroid/support/v7/internal/widget/AdapterViewCompat;Landroid/view/View;IJ)V

    #@17
    move v0, v6

    #@18
    .line 314
    :cond_1
    return v0
.end method

.method rememberSyncState()V
    .locals 6

    #@0
    .prologue
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    const/4 v5, 0x1

    #@1
    const/4 v4, 0x0

    #@2
    .line 1121
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getChildCount()I

    #@5
    move-result v2

    #@6
    if-lez v2, :cond_1

    #@8
    .line 1122
    iput-boolean v5, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNeedSync:Z

    #@a
    .line 1123
    iget v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mLayoutHeight:I

    #@c
    int-to-long v2, v2

    #@d
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncHeight:J

    #@f
    .line 1124
    iget v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedPosition:I

    #@11
    if-ltz v2, :cond_2

    #@13
    .line 1126
    iget v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedPosition:I

    #@15
    iget v3, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mFirstPosition:I

    #@17
    sub-int/2addr v2, v3

    #@18
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getChildAt(I)Landroid/view/View;

    #@1b
    move-result-object v1

    #@1c
    .line 1127
    .local v1, "v":Landroid/view/View;
    iget-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedRowId:J

    #@1e
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncRowId:J

    #@20
    .line 1128
    iget v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedPosition:I

    #@22
    iput v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncPosition:I

    #@24
    .line 1129
    if-eqz v1, :cond_0

    #@26
    .line 1130
    invoke-virtual {v1}, Landroid/view/View;->getTop()I

    #@29
    move-result v2

    #@2a
    iput v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSpecificTop:I

    #@2c
    .line 1132
    :cond_0
    iput v4, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncMode:I

    #@2e
    .line 1149
    .end local v1    # "v":Landroid/view/View;
    :cond_1
    :goto_0
    return-void

    #@2f
    .line 1135
    :cond_2
    invoke-virtual {p0, v4}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getChildAt(I)Landroid/view/View;

    #@32
    move-result-object v1

    #@33
    .line 1136
    .restart local v1    # "v":Landroid/view/View;
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@36
    move-result-object v0

    #@37
    .line 1137
    .local v0, "adapter":Landroid/widget/Adapter;, "TT;"
    iget v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mFirstPosition:I

    #@39
    if-ltz v2, :cond_4

    #@3b
    iget v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mFirstPosition:I

    #@3d
    invoke-interface {v0}, Landroid/widget/Adapter;->getCount()I

    #@40
    move-result v3

    #@41
    if-ge v2, v3, :cond_4

    #@43
    .line 1138
    iget v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mFirstPosition:I

    #@45
    invoke-interface {v0, v2}, Landroid/widget/Adapter;->getItemId(I)J

    #@48
    move-result-wide v2

    #@49
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncRowId:J

    #@4b
    .line 1142
    :goto_1
    iget v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mFirstPosition:I

    #@4d
    iput v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncPosition:I

    #@4f
    .line 1143
    if-eqz v1, :cond_3

    #@51
    .line 1144
    invoke-virtual {v1}, Landroid/view/View;->getTop()I

    #@54
    move-result v2

    #@55
    iput v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSpecificTop:I

    #@57
    .line 1146
    :cond_3
    iput v5, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncMode:I

    #@59
    goto :goto_0

    #@5a
    .line 1140
    :cond_4
    const-wide/16 v2, -0x1

    #@5c
    iput-wide v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncRowId:J

    #@5e
    goto :goto_1
.end method

.method public removeAllViews()V
    .locals 2

    #@0
    .prologue
    .line 539
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "removeAllViews() is not supported in AdapterView"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public removeView(Landroid/view/View;)V
    .locals 2
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 517
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "removeView(View) is not supported in AdapterView"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public removeViewAt(I)V
    .locals 2
    .param p1, "index"    # I

    #@0
    .prologue
    .line 529
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "removeViewAt(int) is not supported in AdapterView"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method selectionChanged()V
    .locals 2

    #@0
    .prologue
    .line 871
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOnItemSelectedListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;

    #@2
    if-eqz v0, :cond_2

    #@4
    .line 872
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mInLayout:Z

    #@6
    if-nez v0, :cond_0

    #@8
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mBlockLayoutRequests:Z

    #@a
    if-eqz v0, :cond_4

    #@c
    .line 877
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectionNotifier:Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;

    #@e
    if-nez v0, :cond_1

    #@10
    .line 878
    new-instance v0, Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;

    #@12
    const/4 v1, 0x0

    #@13
    invoke-direct {v0, p0, v1}, Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;-><init>(Landroid/support/v7/internal/widget/AdapterViewCompat;Landroid/support/v7/internal/widget/AdapterViewCompat$1;)V

    #@16
    iput-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectionNotifier:Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;

    #@18
    .line 880
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectionNotifier:Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;

    #@1a
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->post(Ljava/lang/Runnable;)Z

    #@1d
    .line 887
    :cond_2
    :goto_0
    iget v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedPosition:I

    #@1f
    const/4 v1, -0x1

    #@20
    if-eq v0, v1, :cond_3

    #@22
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->isShown()Z

    #@25
    move-result v0

    #@26
    if-eqz v0, :cond_3

    #@28
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->isInTouchMode()Z

    #@2b
    move-result v0

    #@2c
    if-nez v0, :cond_3

    #@2e
    .line 888
    const/4 v0, 0x4

    #@2f
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->sendAccessibilityEvent(I)V

    #@32
    .line 890
    :cond_3
    return-void

    #@33
    .line 882
    :cond_4
    invoke-direct {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->fireOnSelected()V

    #@36
    goto :goto_0
.end method

.method public abstract setAdapter(Landroid/widget/Adapter;)V
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TT;)V"
        }
    .end annotation
.end method

.method public setEmptyView(Landroid/view/View;)V
    .locals 3
    .param p1, "emptyView"    # Landroid/view/View;

    #@0
    .prologue
    .line 661
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iput-object p1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mEmptyView:Landroid/view/View;

    #@2
    .line 663
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@5
    move-result-object v0

    #@6
    .line 664
    .local v0, "adapter":Landroid/widget/Adapter;, "TT;"
    if-eqz v0, :cond_0

    #@8
    invoke-interface {v0}, Landroid/widget/Adapter;->isEmpty()Z

    #@b
    move-result v2

    #@c
    if-eqz v2, :cond_1

    #@e
    :cond_0
    const/4 v1, 0x1

    #@f
    .line 665
    .local v1, "empty":Z
    :goto_0
    invoke-direct {p0, v1}, Landroid/support/v7/internal/widget/AdapterViewCompat;->updateEmptyStatus(Z)V

    #@12
    .line 666
    return-void

    #@13
    .line 664
    .end local v1    # "empty":Z
    :cond_1
    const/4 v1, 0x0

    #@14
    goto :goto_0
.end method

.method public setFocusable(Z)V
    .locals 5
    .param p1, "focusable"    # Z

    #@0
    .prologue
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    const/4 v3, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 691
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@5
    move-result-object v0

    #@6
    .line 692
    .local v0, "adapter":Landroid/widget/Adapter;, "TT;"
    if-eqz v0, :cond_0

    #@8
    invoke-interface {v0}, Landroid/widget/Adapter;->getCount()I

    #@b
    move-result v4

    #@c
    if-nez v4, :cond_3

    #@e
    :cond_0
    move v1, v3

    #@f
    .line 694
    .local v1, "empty":Z
    :goto_0
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mDesiredFocusableState:Z

    #@11
    .line 695
    if-nez p1, :cond_1

    #@13
    .line 696
    iput-boolean v2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mDesiredFocusableInTouchModeState:Z

    #@15
    .line 699
    :cond_1
    if-eqz p1, :cond_4

    #@17
    if-eqz v1, :cond_2

    #@19
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->isInFilterMode()Z

    #@1c
    move-result v4

    #@1d
    if-eqz v4, :cond_4

    #@1f
    :cond_2
    :goto_1
    invoke-super {p0, v3}, Landroid/view/ViewGroup;->setFocusable(Z)V

    #@22
    .line 700
    return-void

    #@23
    .end local v1    # "empty":Z
    :cond_3
    move v1, v2

    #@24
    .line 692
    goto :goto_0

    #@25
    .restart local v1    # "empty":Z
    :cond_4
    move v3, v2

    #@26
    .line 699
    goto :goto_1
.end method

.method public setFocusableInTouchMode(Z)V
    .locals 5
    .param p1, "focusable"    # Z

    #@0
    .prologue
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    const/4 v2, 0x0

    #@1
    const/4 v3, 0x1

    #@2
    .line 704
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@5
    move-result-object v0

    #@6
    .line 705
    .local v0, "adapter":Landroid/widget/Adapter;, "TT;"
    if-eqz v0, :cond_0

    #@8
    invoke-interface {v0}, Landroid/widget/Adapter;->getCount()I

    #@b
    move-result v4

    #@c
    if-nez v4, :cond_3

    #@e
    :cond_0
    move v1, v3

    #@f
    .line 707
    .local v1, "empty":Z
    :goto_0
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mDesiredFocusableInTouchModeState:Z

    #@11
    .line 708
    if-eqz p1, :cond_1

    #@13
    .line 709
    iput-boolean v3, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mDesiredFocusableState:Z

    #@15
    .line 712
    :cond_1
    if-eqz p1, :cond_4

    #@17
    if-eqz v1, :cond_2

    #@19
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->isInFilterMode()Z

    #@1c
    move-result v4

    #@1d
    if-eqz v4, :cond_4

    #@1f
    :cond_2
    :goto_1
    invoke-super {p0, v3}, Landroid/view/ViewGroup;->setFocusableInTouchMode(Z)V

    #@22
    .line 713
    return-void

    #@23
    .end local v1    # "empty":Z
    :cond_3
    move v1, v2

    #@24
    .line 705
    goto :goto_0

    #@25
    .restart local v1    # "empty":Z
    :cond_4
    move v3, v2

    #@26
    .line 712
    goto :goto_1
.end method

.method setNextSelectedPositionInt(I)V
    .locals 2
    .param p1, "position"    # I

    #@0
    .prologue
    .line 1106
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iput p1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedPosition:I

    #@2
    .line 1107
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getItemIdAtPosition(I)J

    #@5
    move-result-wide v0

    #@6
    iput-wide v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedRowId:J

    #@8
    .line 1109
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNeedSync:Z

    #@a
    if-eqz v0, :cond_0

    #@c
    iget v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncMode:I

    #@e
    if-nez v0, :cond_0

    #@10
    if-ltz p1, :cond_0

    #@12
    .line 1110
    iput p1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncPosition:I

    #@14
    .line 1111
    iget-wide v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mNextSelectedRowId:J

    #@16
    iput-wide v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSyncRowId:J

    #@18
    .line 1113
    :cond_0
    return-void
.end method

.method public setOnClickListener(Landroid/view/View$OnClickListener;)V
    .locals 2
    .param p1, "l"    # Landroid/view/View$OnClickListener;

    #@0
    .prologue
    .line 778
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    new-instance v0, Ljava/lang/RuntimeException;

    #@2
    const-string v1, "Don\'t call setOnClickListener for an AdapterView. You probably want setOnItemClickListener instead"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public setOnItemClickListener(Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;

    #@0
    .prologue
    .line 284
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iput-object p1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOnItemClickListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;

    #@2
    .line 285
    return-void
.end method

.method public setOnItemLongClickListener(Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemLongClickListener;)V
    .locals 1
    .param p1, "listener"    # Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemLongClickListener;

    #@0
    .prologue
    .line 347
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->isLongClickable()Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    .line 348
    const/4 v0, 0x1

    #@7
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->setLongClickable(Z)V

    #@a
    .line 350
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOnItemLongClickListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemLongClickListener;

    #@c
    .line 351
    return-void
.end method

.method public setOnItemSelectedListener(Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;

    #@0
    .prologue
    .line 400
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iput-object p1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mOnItemSelectedListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;

    #@2
    .line 401
    return-void
.end method

.method setSelectedPositionInt(I)V
    .locals 2
    .param p1, "position"    # I

    #@0
    .prologue
    .line 1096
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>;"
    iput p1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedPosition:I

    #@2
    .line 1097
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getItemIdAtPosition(I)J

    #@5
    move-result-wide v0

    #@6
    iput-wide v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mSelectedRowId:J

    #@8
    .line 1098
    return-void
.end method

.method public abstract setSelection(I)V
.end method
