.class public Landroid/support/v4/app/ListFragment;
.super Landroid/support/v4/app/Fragment;
.source "ListFragment.java"


# static fields
.field static final INTERNAL_EMPTY_ID:I = 0xff0001

.field static final INTERNAL_LIST_CONTAINER_ID:I = 0xff0003

.field static final INTERNAL_PROGRESS_CONTAINER_ID:I = 0xff0002


# instance fields
.field mAdapter:Landroid/widget/ListAdapter;

.field mEmptyText:Ljava/lang/CharSequence;

.field mEmptyView:Landroid/view/View;

.field private final mHandler:Landroid/os/Handler;

.field mList:Landroid/widget/ListView;

.field mListContainer:Landroid/view/View;

.field mListShown:Z

.field private final mOnClickListener:Landroid/widget/AdapterView$OnItemClickListener;

.field mProgressContainer:Landroid/view/View;

.field private final mRequestFocus:Ljava/lang/Runnable;

.field mStandardEmptyView:Landroid/widget/TextView;


# direct methods
.method public constructor <init>()V
    .locals 1

    #@0
    .prologue
    .line 71
    invoke-direct {p0}, Landroid/support/v4/app/Fragment;-><init>()V

    #@3
    .line 47
    new-instance v0, Landroid/os/Handler;

    #@5
    invoke-direct {v0}, Landroid/os/Handler;-><init>()V

    #@8
    iput-object v0, p0, Landroid/support/v4/app/ListFragment;->mHandler:Landroid/os/Handler;

    #@a
    .line 49
    new-instance v0, Landroid/support/v4/app/ListFragment$1;

    #@c
    invoke-direct {v0, p0}, Landroid/support/v4/app/ListFragment$1;-><init>(Landroid/support/v4/app/ListFragment;)V

    #@f
    iput-object v0, p0, Landroid/support/v4/app/ListFragment;->mRequestFocus:Ljava/lang/Runnable;

    #@11
    .line 55
    new-instance v0, Landroid/support/v4/app/ListFragment$2;

    #@13
    invoke-direct {v0, p0}, Landroid/support/v4/app/ListFragment$2;-><init>(Landroid/support/v4/app/ListFragment;)V

    #@16
    iput-object v0, p0, Landroid/support/v4/app/ListFragment;->mOnClickListener:Landroid/widget/AdapterView$OnItemClickListener;

    #@18
    .line 72
    return-void
.end method

.method private ensureList()V
    .locals 6

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    .line 323
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@3
    if-eqz v3, :cond_0

    #@5
    .line 374
    :goto_0
    return-void

    #@6
    .line 326
    :cond_0
    invoke-virtual {p0}, Landroid/support/v4/app/ListFragment;->getView()Landroid/view/View;

    #@9
    move-result-object v2

    #@a
    .line 327
    .local v2, "root":Landroid/view/View;
    if-nez v2, :cond_1

    #@c
    .line 328
    new-instance v3, Ljava/lang/IllegalStateException;

    #@e
    const-string v4, "Content view not yet created"

    #@10
    invoke-direct {v3, v4}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@13
    throw v3

    #@14
    .line 330
    :cond_1
    instance-of v3, v2, Landroid/widget/ListView;

    #@16
    if-eqz v3, :cond_4

    #@18
    .line 331
    check-cast v2, Landroid/widget/ListView;

    #@1a
    .end local v2    # "root":Landroid/view/View;
    iput-object v2, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@1c
    .line 360
    :cond_2
    :goto_1
    const/4 v3, 0x1

    #@1d
    iput-boolean v3, p0, Landroid/support/v4/app/ListFragment;->mListShown:Z

    #@1f
    .line 361
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@21
    iget-object v4, p0, Landroid/support/v4/app/ListFragment;->mOnClickListener:Landroid/widget/AdapterView$OnItemClickListener;

    #@23
    invoke-virtual {v3, v4}, Landroid/widget/ListView;->setOnItemClickListener(Landroid/widget/AdapterView$OnItemClickListener;)V

    #@26
    .line 362
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mAdapter:Landroid/widget/ListAdapter;

    #@28
    if-eqz v3, :cond_9

    #@2a
    .line 363
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mAdapter:Landroid/widget/ListAdapter;

    #@2c
    .line 364
    .local v0, "adapter":Landroid/widget/ListAdapter;
    const/4 v3, 0x0

    #@2d
    iput-object v3, p0, Landroid/support/v4/app/ListFragment;->mAdapter:Landroid/widget/ListAdapter;

    #@2f
    .line 365
    invoke-virtual {p0, v0}, Landroid/support/v4/app/ListFragment;->setListAdapter(Landroid/widget/ListAdapter;)V

    #@32
    .line 373
    .end local v0    # "adapter":Landroid/widget/ListAdapter;
    :cond_3
    :goto_2
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mHandler:Landroid/os/Handler;

    #@34
    iget-object v4, p0, Landroid/support/v4/app/ListFragment;->mRequestFocus:Ljava/lang/Runnable;

    #@36
    invoke-virtual {v3, v4}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    #@39
    goto :goto_0

    #@3a
    .line 333
    .restart local v2    # "root":Landroid/view/View;
    :cond_4
    const v3, 0xff0001

    #@3d
    invoke-virtual {v2, v3}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@40
    move-result-object v3

    #@41
    check-cast v3, Landroid/widget/TextView;

    #@43
    iput-object v3, p0, Landroid/support/v4/app/ListFragment;->mStandardEmptyView:Landroid/widget/TextView;

    #@45
    .line 334
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mStandardEmptyView:Landroid/widget/TextView;

    #@47
    if-nez v3, :cond_5

    #@49
    .line 335
    const v3, 0x1020004

    #@4c
    invoke-virtual {v2, v3}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@4f
    move-result-object v3

    #@50
    iput-object v3, p0, Landroid/support/v4/app/ListFragment;->mEmptyView:Landroid/view/View;

    #@52
    .line 339
    :goto_3
    const v3, 0xff0002

    #@55
    invoke-virtual {v2, v3}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@58
    move-result-object v3

    #@59
    iput-object v3, p0, Landroid/support/v4/app/ListFragment;->mProgressContainer:Landroid/view/View;

    #@5b
    .line 340
    const v3, 0xff0003

    #@5e
    invoke-virtual {v2, v3}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@61
    move-result-object v3

    #@62
    iput-object v3, p0, Landroid/support/v4/app/ListFragment;->mListContainer:Landroid/view/View;

    #@64
    .line 341
    const v3, 0x102000a

    #@67
    invoke-virtual {v2, v3}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@6a
    move-result-object v1

    #@6b
    .line 342
    .local v1, "rawListView":Landroid/view/View;
    instance-of v3, v1, Landroid/widget/ListView;

    #@6d
    if-nez v3, :cond_7

    #@6f
    .line 343
    if-nez v1, :cond_6

    #@71
    .line 344
    new-instance v3, Ljava/lang/RuntimeException;

    #@73
    const-string v4, "Your content must have a ListView whose id attribute is \'android.R.id.list\'"

    #@75
    invoke-direct {v3, v4}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    #@78
    throw v3

    #@79
    .line 337
    .end local v1    # "rawListView":Landroid/view/View;
    :cond_5
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mStandardEmptyView:Landroid/widget/TextView;

    #@7b
    const/16 v4, 0x8

    #@7d
    invoke-virtual {v3, v4}, Landroid/widget/TextView;->setVisibility(I)V

    #@80
    goto :goto_3

    #@81
    .line 348
    .restart local v1    # "rawListView":Landroid/view/View;
    :cond_6
    new-instance v3, Ljava/lang/RuntimeException;

    #@83
    const-string v4, "Content has view with id attribute \'android.R.id.list\' that is not a ListView class"

    #@85
    invoke-direct {v3, v4}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    #@88
    throw v3

    #@89
    .line 352
    :cond_7
    check-cast v1, Landroid/widget/ListView;

    #@8b
    .end local v1    # "rawListView":Landroid/view/View;
    iput-object v1, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@8d
    .line 353
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mEmptyView:Landroid/view/View;

    #@8f
    if-eqz v3, :cond_8

    #@91
    .line 354
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@93
    iget-object v4, p0, Landroid/support/v4/app/ListFragment;->mEmptyView:Landroid/view/View;

    #@95
    invoke-virtual {v3, v4}, Landroid/widget/ListView;->setEmptyView(Landroid/view/View;)V

    #@98
    goto :goto_1

    #@99
    .line 355
    :cond_8
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mEmptyText:Ljava/lang/CharSequence;

    #@9b
    if-eqz v3, :cond_2

    #@9d
    .line 356
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mStandardEmptyView:Landroid/widget/TextView;

    #@9f
    iget-object v4, p0, Landroid/support/v4/app/ListFragment;->mEmptyText:Ljava/lang/CharSequence;

    #@a1
    invoke-virtual {v3, v4}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@a4
    .line 357
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@a6
    iget-object v4, p0, Landroid/support/v4/app/ListFragment;->mStandardEmptyView:Landroid/widget/TextView;

    #@a8
    invoke-virtual {v3, v4}, Landroid/widget/ListView;->setEmptyView(Landroid/view/View;)V

    #@ab
    goto/16 :goto_1

    #@ad
    .line 369
    .end local v2    # "root":Landroid/view/View;
    :cond_9
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mProgressContainer:Landroid/view/View;

    #@af
    if-eqz v3, :cond_3

    #@b1
    .line 370
    invoke-direct {p0, v5, v5}, Landroid/support/v4/app/ListFragment;->setListShown(ZZ)V

    #@b4
    goto/16 :goto_2
.end method

.method private setListShown(ZZ)V
    .locals 6
    .param p1, "shown"    # Z
    .param p2, "animate"    # Z

    #@0
    .prologue
    const v5, 0x10a0001

    #@3
    const/high16 v4, 0x10a0000

    #@5
    const/16 v3, 0x8

    #@7
    const/4 v2, 0x0

    #@8
    .line 280
    invoke-direct {p0}, Landroid/support/v4/app/ListFragment;->ensureList()V

    #@b
    .line 281
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mProgressContainer:Landroid/view/View;

    #@d
    if-nez v0, :cond_0

    #@f
    .line 282
    new-instance v0, Ljava/lang/IllegalStateException;

    #@11
    const-string v1, "Can\'t be used with a custom content view"

    #@13
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@16
    throw v0

    #@17
    .line 284
    :cond_0
    iget-boolean v0, p0, Landroid/support/v4/app/ListFragment;->mListShown:Z

    #@19
    if-ne v0, p1, :cond_1

    #@1b
    .line 313
    :goto_0
    return-void

    #@1c
    .line 287
    :cond_1
    iput-boolean p1, p0, Landroid/support/v4/app/ListFragment;->mListShown:Z

    #@1e
    .line 288
    if-eqz p1, :cond_3

    #@20
    .line 289
    if-eqz p2, :cond_2

    #@22
    .line 290
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mProgressContainer:Landroid/view/View;

    #@24
    invoke-virtual {p0}, Landroid/support/v4/app/ListFragment;->getActivity()Landroid/support/v4/app/FragmentActivity;

    #@27
    move-result-object v1

    #@28
    invoke-static {v1, v5}, Landroid/view/animation/AnimationUtils;->loadAnimation(Landroid/content/Context;I)Landroid/view/animation/Animation;

    #@2b
    move-result-object v1

    #@2c
    invoke-virtual {v0, v1}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    #@2f
    .line 292
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mListContainer:Landroid/view/View;

    #@31
    invoke-virtual {p0}, Landroid/support/v4/app/ListFragment;->getActivity()Landroid/support/v4/app/FragmentActivity;

    #@34
    move-result-object v1

    #@35
    invoke-static {v1, v4}, Landroid/view/animation/AnimationUtils;->loadAnimation(Landroid/content/Context;I)Landroid/view/animation/Animation;

    #@38
    move-result-object v1

    #@39
    invoke-virtual {v0, v1}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    #@3c
    .line 298
    :goto_1
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mProgressContainer:Landroid/view/View;

    #@3e
    invoke-virtual {v0, v3}, Landroid/view/View;->setVisibility(I)V

    #@41
    .line 299
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mListContainer:Landroid/view/View;

    #@43
    invoke-virtual {v0, v2}, Landroid/view/View;->setVisibility(I)V

    #@46
    goto :goto_0

    #@47
    .line 295
    :cond_2
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mProgressContainer:Landroid/view/View;

    #@49
    invoke-virtual {v0}, Landroid/view/View;->clearAnimation()V

    #@4c
    .line 296
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mListContainer:Landroid/view/View;

    #@4e
    invoke-virtual {v0}, Landroid/view/View;->clearAnimation()V

    #@51
    goto :goto_1

    #@52
    .line 301
    :cond_3
    if-eqz p2, :cond_4

    #@54
    .line 302
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mProgressContainer:Landroid/view/View;

    #@56
    invoke-virtual {p0}, Landroid/support/v4/app/ListFragment;->getActivity()Landroid/support/v4/app/FragmentActivity;

    #@59
    move-result-object v1

    #@5a
    invoke-static {v1, v4}, Landroid/view/animation/AnimationUtils;->loadAnimation(Landroid/content/Context;I)Landroid/view/animation/Animation;

    #@5d
    move-result-object v1

    #@5e
    invoke-virtual {v0, v1}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    #@61
    .line 304
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mListContainer:Landroid/view/View;

    #@63
    invoke-virtual {p0}, Landroid/support/v4/app/ListFragment;->getActivity()Landroid/support/v4/app/FragmentActivity;

    #@66
    move-result-object v1

    #@67
    invoke-static {v1, v5}, Landroid/view/animation/AnimationUtils;->loadAnimation(Landroid/content/Context;I)Landroid/view/animation/Animation;

    #@6a
    move-result-object v1

    #@6b
    invoke-virtual {v0, v1}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    #@6e
    .line 310
    :goto_2
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mProgressContainer:Landroid/view/View;

    #@70
    invoke-virtual {v0, v2}, Landroid/view/View;->setVisibility(I)V

    #@73
    .line 311
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mListContainer:Landroid/view/View;

    #@75
    invoke-virtual {v0, v3}, Landroid/view/View;->setVisibility(I)V

    #@78
    goto :goto_0

    #@79
    .line 307
    :cond_4
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mProgressContainer:Landroid/view/View;

    #@7b
    invoke-virtual {v0}, Landroid/view/View;->clearAnimation()V

    #@7e
    .line 308
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mListContainer:Landroid/view/View;

    #@80
    invoke-virtual {v0}, Landroid/view/View;->clearAnimation()V

    #@83
    goto :goto_2
.end method


# virtual methods
.method public getListAdapter()Landroid/widget/ListAdapter;
    .locals 1

    #@0
    .prologue
    .line 319
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mAdapter:Landroid/widget/ListAdapter;

    #@2
    return-object v0
.end method

.method public getListView()Landroid/widget/ListView;
    .locals 1

    #@0
    .prologue
    .line 222
    invoke-direct {p0}, Landroid/support/v4/app/ListFragment;->ensureList()V

    #@3
    .line 223
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@5
    return-object v0
.end method

.method public getSelectedItemId()J
    .locals 2

    #@0
    .prologue
    .line 214
    invoke-direct {p0}, Landroid/support/v4/app/ListFragment;->ensureList()V

    #@3
    .line 215
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@5
    invoke-virtual {v0}, Landroid/widget/ListView;->getSelectedItemId()J

    #@8
    move-result-wide v0

    #@9
    return-wide v0
.end method

.method public getSelectedItemPosition()I
    .locals 1

    #@0
    .prologue
    .line 206
    invoke-direct {p0}, Landroid/support/v4/app/ListFragment;->ensureList()V

    #@3
    .line 207
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@5
    invoke-virtual {v0}, Landroid/widget/ListView;->getSelectedItemPosition()I

    #@8
    move-result v0

    #@9
    return v0
.end method

.method public onCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;
    .locals 12
    .param p1, "inflater"    # Landroid/view/LayoutInflater;
    .param p2, "container"    # Landroid/view/ViewGroup;
    .param p3, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    const/16 v11, 0x11

    #@2
    const/4 v10, -0x2

    #@3
    const/4 v9, -0x1

    #@4
    .line 91
    invoke-virtual {p0}, Landroid/support/v4/app/ListFragment;->getActivity()Landroid/support/v4/app/FragmentActivity;

    #@7
    move-result-object v0

    #@8
    .line 93
    .local v0, "context":Landroid/content/Context;
    new-instance v5, Landroid/widget/FrameLayout;

    #@a
    invoke-direct {v5, v0}, Landroid/widget/FrameLayout;-><init>(Landroid/content/Context;)V

    #@d
    .line 97
    .local v5, "root":Landroid/widget/FrameLayout;
    new-instance v3, Landroid/widget/LinearLayout;

    #@f
    invoke-direct {v3, v0}, Landroid/widget/LinearLayout;-><init>(Landroid/content/Context;)V

    #@12
    .line 98
    .local v3, "pframe":Landroid/widget/LinearLayout;
    const v7, 0xff0002

    #@15
    invoke-virtual {v3, v7}, Landroid/widget/LinearLayout;->setId(I)V

    #@18
    .line 99
    const/4 v7, 0x1

    #@19
    invoke-virtual {v3, v7}, Landroid/widget/LinearLayout;->setOrientation(I)V

    #@1c
    .line 100
    const/16 v7, 0x8

    #@1e
    invoke-virtual {v3, v7}, Landroid/widget/LinearLayout;->setVisibility(I)V

    #@21
    .line 101
    invoke-virtual {v3, v11}, Landroid/widget/LinearLayout;->setGravity(I)V

    #@24
    .line 103
    new-instance v4, Landroid/widget/ProgressBar;

    #@26
    const/4 v7, 0x0

    #@27
    const v8, 0x101007a

    #@2a
    invoke-direct {v4, v0, v7, v8}, Landroid/widget/ProgressBar;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@2d
    .line 105
    .local v4, "progress":Landroid/widget/ProgressBar;
    new-instance v7, Landroid/widget/FrameLayout$LayoutParams;

    #@2f
    invoke-direct {v7, v10, v10}, Landroid/widget/FrameLayout$LayoutParams;-><init>(II)V

    #@32
    invoke-virtual {v3, v4, v7}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@35
    .line 108
    new-instance v7, Landroid/widget/FrameLayout$LayoutParams;

    #@37
    invoke-direct {v7, v9, v9}, Landroid/widget/FrameLayout$LayoutParams;-><init>(II)V

    #@3a
    invoke-virtual {v5, v3, v7}, Landroid/widget/FrameLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@3d
    .line 113
    new-instance v1, Landroid/widget/FrameLayout;

    #@3f
    invoke-direct {v1, v0}, Landroid/widget/FrameLayout;-><init>(Landroid/content/Context;)V

    #@42
    .line 114
    .local v1, "lframe":Landroid/widget/FrameLayout;
    const v7, 0xff0003

    #@45
    invoke-virtual {v1, v7}, Landroid/widget/FrameLayout;->setId(I)V

    #@48
    .line 116
    new-instance v6, Landroid/widget/TextView;

    #@4a
    invoke-virtual {p0}, Landroid/support/v4/app/ListFragment;->getActivity()Landroid/support/v4/app/FragmentActivity;

    #@4d
    move-result-object v7

    #@4e
    invoke-direct {v6, v7}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    #@51
    .line 117
    .local v6, "tv":Landroid/widget/TextView;
    const v7, 0xff0001

    #@54
    invoke-virtual {v6, v7}, Landroid/widget/TextView;->setId(I)V

    #@57
    .line 118
    invoke-virtual {v6, v11}, Landroid/widget/TextView;->setGravity(I)V

    #@5a
    .line 119
    new-instance v7, Landroid/widget/FrameLayout$LayoutParams;

    #@5c
    invoke-direct {v7, v9, v9}, Landroid/widget/FrameLayout$LayoutParams;-><init>(II)V

    #@5f
    invoke-virtual {v1, v6, v7}, Landroid/widget/FrameLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@62
    .line 122
    new-instance v2, Landroid/widget/ListView;

    #@64
    invoke-virtual {p0}, Landroid/support/v4/app/ListFragment;->getActivity()Landroid/support/v4/app/FragmentActivity;

    #@67
    move-result-object v7

    #@68
    invoke-direct {v2, v7}, Landroid/widget/ListView;-><init>(Landroid/content/Context;)V

    #@6b
    .line 123
    .local v2, "lv":Landroid/widget/ListView;
    const v7, 0x102000a

    #@6e
    invoke-virtual {v2, v7}, Landroid/widget/ListView;->setId(I)V

    #@71
    .line 124
    const/4 v7, 0x0

    #@72
    invoke-virtual {v2, v7}, Landroid/widget/ListView;->setDrawSelectorOnTop(Z)V

    #@75
    .line 125
    new-instance v7, Landroid/widget/FrameLayout$LayoutParams;

    #@77
    invoke-direct {v7, v9, v9}, Landroid/widget/FrameLayout$LayoutParams;-><init>(II)V

    #@7a
    invoke-virtual {v1, v2, v7}, Landroid/widget/FrameLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@7d
    .line 128
    new-instance v7, Landroid/widget/FrameLayout$LayoutParams;

    #@7f
    invoke-direct {v7, v9, v9}, Landroid/widget/FrameLayout$LayoutParams;-><init>(II)V

    #@82
    invoke-virtual {v5, v1, v7}, Landroid/widget/FrameLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@85
    .line 133
    new-instance v7, Landroid/widget/FrameLayout$LayoutParams;

    #@87
    invoke-direct {v7, v9, v9}, Landroid/widget/FrameLayout$LayoutParams;-><init>(II)V

    #@8a
    invoke-virtual {v5, v7}, Landroid/widget/FrameLayout;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@8d
    .line 136
    return-object v5
.end method

.method public onDestroyView()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 153
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mHandler:Landroid/os/Handler;

    #@3
    iget-object v1, p0, Landroid/support/v4/app/ListFragment;->mRequestFocus:Ljava/lang/Runnable;

    #@5
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeCallbacks(Ljava/lang/Runnable;)V

    #@8
    .line 154
    iput-object v2, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@a
    .line 155
    const/4 v0, 0x0

    #@b
    iput-boolean v0, p0, Landroid/support/v4/app/ListFragment;->mListShown:Z

    #@d
    .line 156
    iput-object v2, p0, Landroid/support/v4/app/ListFragment;->mListContainer:Landroid/view/View;

    #@f
    iput-object v2, p0, Landroid/support/v4/app/ListFragment;->mProgressContainer:Landroid/view/View;

    #@11
    iput-object v2, p0, Landroid/support/v4/app/ListFragment;->mEmptyView:Landroid/view/View;

    #@13
    .line 157
    iput-object v2, p0, Landroid/support/v4/app/ListFragment;->mStandardEmptyView:Landroid/widget/TextView;

    #@15
    .line 158
    invoke-super {p0}, Landroid/support/v4/app/Fragment;->onDestroyView()V

    #@18
    .line 159
    return-void
.end method

.method public onListItemClick(Landroid/widget/ListView;Landroid/view/View;IJ)V
    .locals 0
    .param p1, "l"    # Landroid/widget/ListView;
    .param p2, "v"    # Landroid/view/View;
    .param p3, "position"    # I
    .param p4, "id"    # J

    #@0
    .prologue
    .line 173
    return-void
.end method

.method public onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V
    .locals 0
    .param p1, "view"    # Landroid/view/View;
    .param p2, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 144
    invoke-super {p0, p1, p2}, Landroid/support/v4/app/Fragment;->onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V

    #@3
    .line 145
    invoke-direct {p0}, Landroid/support/v4/app/ListFragment;->ensureList()V

    #@6
    .line 146
    return-void
.end method

.method public setEmptyText(Ljava/lang/CharSequence;)V
    .locals 2
    .param p1, "text"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 232
    invoke-direct {p0}, Landroid/support/v4/app/ListFragment;->ensureList()V

    #@3
    .line 233
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mStandardEmptyView:Landroid/widget/TextView;

    #@5
    if-nez v0, :cond_0

    #@7
    .line 234
    new-instance v0, Ljava/lang/IllegalStateException;

    #@9
    const-string v1, "Can\'t be used with a custom content view"

    #@b
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@e
    throw v0

    #@f
    .line 236
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mStandardEmptyView:Landroid/widget/TextView;

    #@11
    invoke-virtual {v0, p1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@14
    .line 237
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mEmptyText:Ljava/lang/CharSequence;

    #@16
    if-nez v0, :cond_1

    #@18
    .line 238
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@1a
    iget-object v1, p0, Landroid/support/v4/app/ListFragment;->mStandardEmptyView:Landroid/widget/TextView;

    #@1c
    invoke-virtual {v0, v1}, Landroid/widget/ListView;->setEmptyView(Landroid/view/View;)V

    #@1f
    .line 240
    :cond_1
    iput-object p1, p0, Landroid/support/v4/app/ListFragment;->mEmptyText:Ljava/lang/CharSequence;

    #@21
    .line 241
    return-void
.end method

.method public setListAdapter(Landroid/widget/ListAdapter;)V
    .locals 4
    .param p1, "adapter"    # Landroid/widget/ListAdapter;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    const/4 v1, 0x1

    #@2
    .line 179
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mAdapter:Landroid/widget/ListAdapter;

    #@4
    if-eqz v3, :cond_2

    #@6
    move v0, v1

    #@7
    .line 180
    .local v0, "hadAdapter":Z
    :goto_0
    iput-object p1, p0, Landroid/support/v4/app/ListFragment;->mAdapter:Landroid/widget/ListAdapter;

    #@9
    .line 181
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@b
    if-eqz v3, :cond_1

    #@d
    .line 182
    iget-object v3, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@f
    invoke-virtual {v3, p1}, Landroid/widget/ListView;->setAdapter(Landroid/widget/ListAdapter;)V

    #@12
    .line 183
    iget-boolean v3, p0, Landroid/support/v4/app/ListFragment;->mListShown:Z

    #@14
    if-nez v3, :cond_1

    #@16
    if-nez v0, :cond_1

    #@18
    .line 186
    invoke-virtual {p0}, Landroid/support/v4/app/ListFragment;->getView()Landroid/view/View;

    #@1b
    move-result-object v3

    #@1c
    invoke-virtual {v3}, Landroid/view/View;->getWindowToken()Landroid/os/IBinder;

    #@1f
    move-result-object v3

    #@20
    if-eqz v3, :cond_0

    #@22
    move v2, v1

    #@23
    :cond_0
    invoke-direct {p0, v1, v2}, Landroid/support/v4/app/ListFragment;->setListShown(ZZ)V

    #@26
    .line 189
    :cond_1
    return-void

    #@27
    .end local v0    # "hadAdapter":Z
    :cond_2
    move v0, v2

    #@28
    .line 179
    goto :goto_0
.end method

.method public setListShown(Z)V
    .locals 1
    .param p1, "shown"    # Z

    #@0
    .prologue
    .line 258
    const/4 v0, 0x1

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v4/app/ListFragment;->setListShown(ZZ)V

    #@4
    .line 259
    return-void
.end method

.method public setListShownNoAnimation(Z)V
    .locals 1
    .param p1, "shown"    # Z

    #@0
    .prologue
    .line 266
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v4/app/ListFragment;->setListShown(ZZ)V

    #@4
    .line 267
    return-void
.end method

.method public setSelection(I)V
    .locals 1
    .param p1, "position"    # I

    #@0
    .prologue
    .line 198
    invoke-direct {p0}, Landroid/support/v4/app/ListFragment;->ensureList()V

    #@3
    .line 199
    iget-object v0, p0, Landroid/support/v4/app/ListFragment;->mList:Landroid/widget/ListView;

    #@5
    invoke-virtual {v0, p1}, Landroid/widget/ListView;->setSelection(I)V

    #@8
    .line 200
    return-void
.end method
