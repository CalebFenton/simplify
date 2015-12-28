.class final Landroid/support/v4/app/BackStackRecord;
.super Landroid/support/v4/app/FragmentTransaction;
.source "BackStackRecord.java"

# interfaces
.implements Landroid/support/v4/app/FragmentManager$BackStackEntry;
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/BackStackRecord$TransitionState;,
        Landroid/support/v4/app/BackStackRecord$Op;
    }
.end annotation


# static fields
.field static final OP_ADD:I = 0x1

.field static final OP_ATTACH:I = 0x7

.field static final OP_DETACH:I = 0x6

.field static final OP_HIDE:I = 0x4

.field static final OP_NULL:I = 0x0

.field static final OP_REMOVE:I = 0x3

.field static final OP_REPLACE:I = 0x2

.field static final OP_SHOW:I = 0x5

.field static final TAG:Ljava/lang/String; = "FragmentManager"


# instance fields
.field mAddToBackStack:Z

.field mAllowAddToBackStack:Z

.field mBreadCrumbShortTitleRes:I

.field mBreadCrumbShortTitleText:Ljava/lang/CharSequence;

.field mBreadCrumbTitleRes:I

.field mBreadCrumbTitleText:Ljava/lang/CharSequence;

.field mCommitted:Z

.field mEnterAnim:I

.field mExitAnim:I

.field mHead:Landroid/support/v4/app/BackStackRecord$Op;

.field mIndex:I

.field final mManager:Landroid/support/v4/app/FragmentManagerImpl;

.field mName:Ljava/lang/String;

.field mNumOp:I

.field mPopEnterAnim:I

.field mPopExitAnim:I

.field mSharedElementSourceNames:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field mSharedElementTargetNames:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field mTail:Landroid/support/v4/app/BackStackRecord$Op;

.field mTransition:I

.field mTransitionStyle:I


# direct methods
.method public constructor <init>(Landroid/support/v4/app/FragmentManagerImpl;)V
    .locals 1
    .param p1, "manager"    # Landroid/support/v4/app/FragmentManagerImpl;

    #@0
    .prologue
    .line 355
    invoke-direct {p0}, Landroid/support/v4/app/FragmentTransaction;-><init>()V

    #@3
    .line 228
    const/4 v0, 0x1

    #@4
    iput-boolean v0, p0, Landroid/support/v4/app/BackStackRecord;->mAllowAddToBackStack:Z

    #@6
    .line 231
    const/4 v0, -0x1

    #@7
    iput v0, p0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@9
    .line 356
    iput-object p1, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@b
    .line 357
    return-void
.end method

.method static synthetic access$000(Landroid/support/v4/app/BackStackRecord;Landroid/support/v4/app/BackStackRecord$TransitionState;ZLandroid/support/v4/app/Fragment;)Landroid/support/v4/util/ArrayMap;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/app/BackStackRecord;
    .param p1, "x1"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .param p2, "x2"    # Z
    .param p3, "x3"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 191
    invoke-direct {p0, p1, p2, p3}, Landroid/support/v4/app/BackStackRecord;->mapSharedElementsIn(Landroid/support/v4/app/BackStackRecord$TransitionState;ZLandroid/support/v4/app/Fragment;)Landroid/support/v4/util/ArrayMap;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method static synthetic access$100(Landroid/support/v4/app/BackStackRecord;Landroid/support/v4/util/ArrayMap;Landroid/support/v4/app/BackStackRecord$TransitionState;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/app/BackStackRecord;
    .param p1, "x1"    # Landroid/support/v4/util/ArrayMap;
    .param p2, "x2"    # Landroid/support/v4/app/BackStackRecord$TransitionState;

    #@0
    .prologue
    .line 191
    invoke-direct {p0, p1, p2}, Landroid/support/v4/app/BackStackRecord;->setEpicenterIn(Landroid/support/v4/util/ArrayMap;Landroid/support/v4/app/BackStackRecord$TransitionState;)V

    #@3
    return-void
.end method

.method static synthetic access$200(Landroid/support/v4/app/BackStackRecord;Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/support/v4/app/Fragment;Landroid/support/v4/app/Fragment;ZLandroid/support/v4/util/ArrayMap;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/app/BackStackRecord;
    .param p1, "x1"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .param p2, "x2"    # Landroid/support/v4/app/Fragment;
    .param p3, "x3"    # Landroid/support/v4/app/Fragment;
    .param p4, "x4"    # Z
    .param p5, "x5"    # Landroid/support/v4/util/ArrayMap;

    #@0
    .prologue
    .line 191
    invoke-direct/range {p0 .. p5}, Landroid/support/v4/app/BackStackRecord;->callSharedElementEnd(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/support/v4/app/Fragment;Landroid/support/v4/app/Fragment;ZLandroid/support/v4/util/ArrayMap;)V

    #@3
    return-void
.end method

.method static synthetic access$300(Landroid/support/v4/app/BackStackRecord;Landroid/support/v4/app/BackStackRecord$TransitionState;ILjava/lang/Object;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/app/BackStackRecord;
    .param p1, "x1"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .param p2, "x2"    # I
    .param p3, "x3"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 191
    invoke-direct {p0, p1, p2, p3}, Landroid/support/v4/app/BackStackRecord;->excludeHiddenFragments(Landroid/support/v4/app/BackStackRecord$TransitionState;ILjava/lang/Object;)V

    #@3
    return-void
.end method

.method private beginTransition(Landroid/util/SparseArray;Landroid/util/SparseArray;Z)Landroid/support/v4/app/BackStackRecord$TransitionState;
    .locals 8
    .param p3, "isBack"    # Z
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;Z)",
            "Landroid/support/v4/app/BackStackRecord$TransitionState;"
        }
    .end annotation

    #@0
    .prologue
    .line 1018
    .local p1, "firstOutFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    .local p2, "lastInFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    new-instance v2, Landroid/support/v4/app/BackStackRecord$TransitionState;

    #@2
    invoke-direct {v2, p0}, Landroid/support/v4/app/BackStackRecord$TransitionState;-><init>(Landroid/support/v4/app/BackStackRecord;)V

    #@5
    .line 1023
    .local v2, "state":Landroid/support/v4/app/BackStackRecord$TransitionState;
    new-instance v0, Landroid/view/View;

    #@7
    iget-object v3, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@9
    iget-object v3, v3, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@b
    invoke-direct {v0, v3}, Landroid/view/View;-><init>(Landroid/content/Context;)V

    #@e
    iput-object v0, v2, Landroid/support/v4/app/BackStackRecord$TransitionState;->nonExistentView:Landroid/view/View;

    #@10
    .line 1025
    const/4 v6, 0x0

    #@11
    .line 1027
    .local v6, "anyTransitionStarted":Z
    const/4 v7, 0x0

    #@12
    .local v7, "i":I
    :goto_0
    invoke-virtual {p1}, Landroid/util/SparseArray;->size()I

    #@15
    move-result v0

    #@16
    if-ge v7, v0, :cond_1

    #@18
    .line 1028
    invoke-virtual {p1, v7}, Landroid/util/SparseArray;->keyAt(I)I

    #@1b
    move-result v1

    #@1c
    .local v1, "containerId":I
    move-object v0, p0

    #@1d
    move v3, p3

    #@1e
    move-object v4, p1

    #@1f
    move-object v5, p2

    #@20
    .line 1029
    invoke-direct/range {v0 .. v5}, Landroid/support/v4/app/BackStackRecord;->configureTransitions(ILandroid/support/v4/app/BackStackRecord$TransitionState;ZLandroid/util/SparseArray;Landroid/util/SparseArray;)Z

    #@23
    move-result v0

    #@24
    if-eqz v0, :cond_0

    #@26
    .line 1031
    const/4 v6, 0x1

    #@27
    .line 1027
    :cond_0
    add-int/lit8 v7, v7, 0x1

    #@29
    goto :goto_0

    #@2a
    .line 1036
    .end local v1    # "containerId":I
    :cond_1
    const/4 v7, 0x0

    #@2b
    :goto_1
    invoke-virtual {p2}, Landroid/util/SparseArray;->size()I

    #@2e
    move-result v0

    #@2f
    if-ge v7, v0, :cond_3

    #@31
    .line 1037
    invoke-virtual {p2, v7}, Landroid/util/SparseArray;->keyAt(I)I

    #@34
    move-result v1

    #@35
    .line 1038
    .restart local v1    # "containerId":I
    invoke-virtual {p1, v1}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    #@38
    move-result-object v0

    #@39
    if-nez v0, :cond_2

    #@3b
    move-object v0, p0

    #@3c
    move v3, p3

    #@3d
    move-object v4, p1

    #@3e
    move-object v5, p2

    #@3f
    invoke-direct/range {v0 .. v5}, Landroid/support/v4/app/BackStackRecord;->configureTransitions(ILandroid/support/v4/app/BackStackRecord$TransitionState;ZLandroid/util/SparseArray;Landroid/util/SparseArray;)Z

    #@42
    move-result v0

    #@43
    if-eqz v0, :cond_2

    #@45
    .line 1041
    const/4 v6, 0x1

    #@46
    .line 1036
    :cond_2
    add-int/lit8 v7, v7, 0x1

    #@48
    goto :goto_1

    #@49
    .line 1045
    .end local v1    # "containerId":I
    :cond_3
    if-nez v6, :cond_4

    #@4b
    .line 1046
    const/4 v2, 0x0

    #@4c
    .line 1049
    :cond_4
    return-object v2
.end method

.method private calculateFragments(Landroid/util/SparseArray;Landroid/util/SparseArray;)V
    .locals 6
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 776
    .local p1, "firstOutFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    .local p2, "lastInFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    iget-object v4, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    iget-object v4, v4, Landroid/support/v4/app/FragmentManagerImpl;->mContainer:Landroid/support/v4/app/FragmentContainer;

    #@4
    invoke-interface {v4}, Landroid/support/v4/app/FragmentContainer;->hasView()Z

    #@7
    move-result v4

    #@8
    if-nez v4, :cond_1

    #@a
    .line 821
    :cond_0
    return-void

    #@b
    .line 779
    :cond_1
    iget-object v3, p0, Landroid/support/v4/app/BackStackRecord;->mHead:Landroid/support/v4/app/BackStackRecord$Op;

    #@d
    .line 780
    .local v3, "op":Landroid/support/v4/app/BackStackRecord$Op;
    :goto_0
    if-eqz v3, :cond_0

    #@f
    .line 781
    iget v4, v3, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@11
    packed-switch v4, :pswitch_data_0

    #@14
    .line 819
    :goto_1
    iget-object v3, v3, Landroid/support/v4/app/BackStackRecord$Op;->next:Landroid/support/v4/app/BackStackRecord$Op;

    #@16
    goto :goto_0

    #@17
    .line 783
    :pswitch_0
    iget-object v4, v3, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@19
    invoke-direct {p0, p2, v4}, Landroid/support/v4/app/BackStackRecord;->setLastIn(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@1c
    goto :goto_1

    #@1d
    .line 786
    :pswitch_1
    iget-object v0, v3, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@1f
    .line 787
    .local v0, "f":Landroid/support/v4/app/Fragment;
    iget-object v4, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@21
    iget-object v4, v4, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@23
    if-eqz v4, :cond_5

    #@25
    .line 788
    const/4 v1, 0x0

    #@26
    .local v1, "i":I
    :goto_2
    iget-object v4, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@28
    iget-object v4, v4, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@2a
    invoke-virtual {v4}, Ljava/util/ArrayList;->size()I

    #@2d
    move-result v4

    #@2e
    if-ge v1, v4, :cond_5

    #@30
    .line 789
    iget-object v4, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@32
    iget-object v4, v4, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@34
    invoke-virtual {v4, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@37
    move-result-object v2

    #@38
    check-cast v2, Landroid/support/v4/app/Fragment;

    #@3a
    .line 790
    .local v2, "old":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_2

    #@3c
    iget v4, v2, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@3e
    iget v5, v0, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@40
    if-ne v4, v5, :cond_3

    #@42
    .line 791
    :cond_2
    if-ne v2, v0, :cond_4

    #@44
    .line 792
    const/4 v0, 0x0

    #@45
    .line 788
    :cond_3
    :goto_3
    add-int/lit8 v1, v1, 0x1

    #@47
    goto :goto_2

    #@48
    .line 794
    :cond_4
    invoke-static {p1, v2}, Landroid/support/v4/app/BackStackRecord;->setFirstOut(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@4b
    goto :goto_3

    #@4c
    .line 799
    .end local v1    # "i":I
    .end local v2    # "old":Landroid/support/v4/app/Fragment;
    :cond_5
    invoke-direct {p0, p2, v0}, Landroid/support/v4/app/BackStackRecord;->setLastIn(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@4f
    goto :goto_1

    #@50
    .line 803
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    :pswitch_2
    iget-object v4, v3, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@52
    invoke-static {p1, v4}, Landroid/support/v4/app/BackStackRecord;->setFirstOut(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@55
    goto :goto_1

    #@56
    .line 806
    :pswitch_3
    iget-object v4, v3, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@58
    invoke-static {p1, v4}, Landroid/support/v4/app/BackStackRecord;->setFirstOut(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@5b
    goto :goto_1

    #@5c
    .line 809
    :pswitch_4
    iget-object v4, v3, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@5e
    invoke-direct {p0, p2, v4}, Landroid/support/v4/app/BackStackRecord;->setLastIn(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@61
    goto :goto_1

    #@62
    .line 812
    :pswitch_5
    iget-object v4, v3, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@64
    invoke-static {p1, v4}, Landroid/support/v4/app/BackStackRecord;->setFirstOut(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@67
    goto :goto_1

    #@68
    .line 815
    :pswitch_6
    iget-object v4, v3, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@6a
    invoke-direct {p0, p2, v4}, Landroid/support/v4/app/BackStackRecord;->setLastIn(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@6d
    goto :goto_1

    #@6e
    .line 781
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
        :pswitch_4
        :pswitch_5
        :pswitch_6
    .end packed-switch
.end method

.method private callSharedElementEnd(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/support/v4/app/Fragment;Landroid/support/v4/app/Fragment;ZLandroid/support/v4/util/ArrayMap;)V
    .locals 4
    .param p1, "state"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .param p2, "inFragment"    # Landroid/support/v4/app/Fragment;
    .param p3, "outFragment"    # Landroid/support/v4/app/Fragment;
    .param p4, "isBack"    # Z
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v4/app/BackStackRecord$TransitionState;",
            "Landroid/support/v4/app/Fragment;",
            "Landroid/support/v4/app/Fragment;",
            "Z",
            "Landroid/support/v4/util/ArrayMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 1267
    .local p5, "namedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    if-eqz p4, :cond_1

    #@2
    iget-object v1, p3, Landroid/support/v4/app/Fragment;->mEnterTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@4
    .line 1270
    .local v1, "sharedElementCallback":Landroid/support/v4/app/SharedElementCallback;
    :goto_0
    if-eqz v1, :cond_0

    #@6
    .line 1271
    new-instance v0, Ljava/util/ArrayList;

    #@8
    invoke-virtual {p5}, Landroid/support/v4/util/ArrayMap;->keySet()Ljava/util/Set;

    #@b
    move-result-object v3

    #@c
    invoke-direct {v0, v3}, Ljava/util/ArrayList;-><init>(Ljava/util/Collection;)V

    #@f
    .line 1272
    .local v0, "names":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Ljava/lang/String;>;"
    new-instance v2, Ljava/util/ArrayList;

    #@11
    invoke-virtual {p5}, Landroid/support/v4/util/ArrayMap;->values()Ljava/util/Collection;

    #@14
    move-result-object v3

    #@15
    invoke-direct {v2, v3}, Ljava/util/ArrayList;-><init>(Ljava/util/Collection;)V

    #@18
    .line 1273
    .local v2, "views":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    const/4 v3, 0x0

    #@19
    invoke-virtual {v1, v0, v2, v3}, Landroid/support/v4/app/SharedElementCallback;->onSharedElementEnd(Ljava/util/List;Ljava/util/List;Ljava/util/List;)V

    #@1c
    .line 1275
    .end local v0    # "names":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Ljava/lang/String;>;"
    .end local v2    # "views":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    :cond_0
    return-void

    #@1d
    .line 1267
    .end local v1    # "sharedElementCallback":Landroid/support/v4/app/SharedElementCallback;
    :cond_1
    iget-object v1, p2, Landroid/support/v4/app/Fragment;->mEnterTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@1f
    goto :goto_0
.end method

.method private static captureExitingViews(Ljava/lang/Object;Landroid/support/v4/app/Fragment;Ljava/util/ArrayList;Landroid/support/v4/util/ArrayMap;)Ljava/lang/Object;
    .locals 1
    .param p0, "exitTransition"    # Ljava/lang/Object;
    .param p1, "outFragment"    # Landroid/support/v4/app/Fragment;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Object;",
            "Landroid/support/v4/app/Fragment;",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;",
            "Landroid/support/v4/util/ArrayMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;)",
            "Ljava/lang/Object;"
        }
    .end annotation

    #@0
    .prologue
    .line 1080
    .local p2, "exitingViews":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    .local p3, "namedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    if-eqz p0, :cond_0

    #@2
    .line 1081
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->getView()Landroid/view/View;

    #@5
    move-result-object v0

    #@6
    invoke-static {p0, v0, p2, p3}, Landroid/support/v4/app/FragmentTransitionCompat21;->captureExitingViews(Ljava/lang/Object;Landroid/view/View;Ljava/util/ArrayList;Ljava/util/Map;)Ljava/lang/Object;

    #@9
    move-result-object p0

    #@a
    .line 1084
    :cond_0
    return-object p0
.end method

.method private configureTransitions(ILandroid/support/v4/app/BackStackRecord$TransitionState;ZLandroid/util/SparseArray;Landroid/util/SparseArray;)Z
    .locals 38
    .param p1, "containerId"    # I
    .param p2, "state"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .param p3, "isBack"    # Z
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I",
            "Landroid/support/v4/app/BackStackRecord$TransitionState;",
            "Z",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;)Z"
        }
    .end annotation

    #@0
    .prologue
    .line 1131
    .local p4, "firstOutFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    .local p5, "lastInFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    move-object/from16 v0, p0

    #@2
    iget-object v4, v0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@4
    iget-object v4, v4, Landroid/support/v4/app/FragmentManagerImpl;->mContainer:Landroid/support/v4/app/FragmentContainer;

    #@6
    move/from16 v0, p1

    #@8
    invoke-interface {v4, v0}, Landroid/support/v4/app/FragmentContainer;->findViewById(I)Landroid/view/View;

    #@b
    move-result-object v6

    #@c
    check-cast v6, Landroid/view/ViewGroup;

    #@e
    .line 1132
    .local v6, "sceneRoot":Landroid/view/ViewGroup;
    if-nez v6, :cond_0

    #@10
    .line 1133
    const/4 v4, 0x0

    #@11
    .line 1226
    :goto_0
    return v4

    #@12
    .line 1135
    :cond_0
    move-object/from16 v0, p5

    #@14
    move/from16 v1, p1

    #@16
    invoke-virtual {v0, v1}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    #@19
    move-result-object v8

    #@1a
    check-cast v8, Landroid/support/v4/app/Fragment;

    #@1c
    .line 1136
    .local v8, "inFragment":Landroid/support/v4/app/Fragment;
    move-object/from16 v0, p4

    #@1e
    move/from16 v1, p1

    #@20
    invoke-virtual {v0, v1}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    #@23
    move-result-object v9

    #@24
    check-cast v9, Landroid/support/v4/app/Fragment;

    #@26
    .line 1138
    .local v9, "outFragment":Landroid/support/v4/app/Fragment;
    move/from16 v0, p3

    #@28
    invoke-static {v8, v0}, Landroid/support/v4/app/BackStackRecord;->getEnterTransition(Landroid/support/v4/app/Fragment;Z)Ljava/lang/Object;

    #@2b
    move-result-object v12

    #@2c
    .line 1139
    .local v12, "enterTransition":Ljava/lang/Object;
    move/from16 v0, p3

    #@2e
    invoke-static {v8, v9, v0}, Landroid/support/v4/app/BackStackRecord;->getSharedElementTransition(Landroid/support/v4/app/Fragment;Landroid/support/v4/app/Fragment;Z)Ljava/lang/Object;

    #@31
    move-result-object v7

    #@32
    .line 1141
    .local v7, "sharedElementTransition":Ljava/lang/Object;
    move/from16 v0, p3

    #@34
    invoke-static {v9, v0}, Landroid/support/v4/app/BackStackRecord;->getExitTransition(Landroid/support/v4/app/Fragment;Z)Ljava/lang/Object;

    #@37
    move-result-object v25

    #@38
    .line 1142
    .local v25, "exitTransition":Ljava/lang/Object;
    if-nez v12, :cond_1

    #@3a
    if-nez v7, :cond_1

    #@3c
    if-nez v25, :cond_1

    #@3e
    .line 1144
    const/4 v4, 0x0

    #@3f
    goto :goto_0

    #@40
    .line 1146
    :cond_1
    const/16 v35, 0x0

    #@42
    .line 1147
    .local v35, "namedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    new-instance v11, Ljava/util/ArrayList;

    #@44
    invoke-direct {v11}, Ljava/util/ArrayList;-><init>()V

    #@47
    .line 1148
    .local v11, "sharedElementTargets":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    if-eqz v7, :cond_2

    #@49
    .line 1149
    move-object/from16 v0, p0

    #@4b
    move-object/from16 v1, p2

    #@4d
    move/from16 v2, p3

    #@4f
    invoke-direct {v0, v1, v9, v2}, Landroid/support/v4/app/BackStackRecord;->remapSharedElements(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/support/v4/app/Fragment;Z)Landroid/support/v4/util/ArrayMap;

    #@52
    move-result-object v35

    #@53
    .line 1150
    invoke-virtual/range {v35 .. v35}, Landroid/support/v4/util/ArrayMap;->isEmpty()Z

    #@56
    move-result v4

    #@57
    if-eqz v4, :cond_7

    #@59
    .line 1151
    move-object/from16 v0, p2

    #@5b
    iget-object v4, v0, Landroid/support/v4/app/BackStackRecord$TransitionState;->nonExistentView:Landroid/view/View;

    #@5d
    invoke-virtual {v11, v4}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@60
    .line 1157
    :goto_1
    if-eqz p3, :cond_8

    #@62
    iget-object v0, v9, Landroid/support/v4/app/Fragment;->mEnterTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@64
    move-object/from16 v33, v0

    #@66
    .line 1160
    .local v33, "callback":Landroid/support/v4/app/SharedElementCallback;
    :goto_2
    if-eqz v33, :cond_2

    #@68
    .line 1161
    new-instance v36, Ljava/util/ArrayList;

    #@6a
    invoke-virtual/range {v35 .. v35}, Landroid/support/v4/util/ArrayMap;->keySet()Ljava/util/Set;

    #@6d
    move-result-object v4

    #@6e
    move-object/from16 v0, v36

    #@70
    invoke-direct {v0, v4}, Ljava/util/ArrayList;-><init>(Ljava/util/Collection;)V

    #@73
    .line 1162
    .local v36, "names":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Ljava/lang/String;>;"
    new-instance v37, Ljava/util/ArrayList;

    #@75
    invoke-virtual/range {v35 .. v35}, Landroid/support/v4/util/ArrayMap;->values()Ljava/util/Collection;

    #@78
    move-result-object v4

    #@79
    move-object/from16 v0, v37

    #@7b
    invoke-direct {v0, v4}, Ljava/util/ArrayList;-><init>(Ljava/util/Collection;)V

    #@7e
    .line 1163
    .local v37, "views":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    const/4 v4, 0x0

    #@7f
    move-object/from16 v0, v33

    #@81
    move-object/from16 v1, v36

    #@83
    move-object/from16 v2, v37

    #@85
    invoke-virtual {v0, v1, v2, v4}, Landroid/support/v4/app/SharedElementCallback;->onSharedElementStart(Ljava/util/List;Ljava/util/List;Ljava/util/List;)V

    #@88
    .line 1167
    .end local v33    # "callback":Landroid/support/v4/app/SharedElementCallback;
    .end local v36    # "names":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Ljava/lang/String;>;"
    .end local v37    # "views":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    :cond_2
    new-instance v26, Ljava/util/ArrayList;

    #@8a
    invoke-direct/range {v26 .. v26}, Ljava/util/ArrayList;-><init>()V

    #@8d
    .line 1168
    .local v26, "exitingViews":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    move-object/from16 v0, v25

    #@8f
    move-object/from16 v1, v26

    #@91
    move-object/from16 v2, v35

    #@93
    invoke-static {v0, v9, v1, v2}, Landroid/support/v4/app/BackStackRecord;->captureExitingViews(Ljava/lang/Object;Landroid/support/v4/app/Fragment;Ljava/util/ArrayList;Landroid/support/v4/util/ArrayMap;)Ljava/lang/Object;

    #@96
    move-result-object v25

    #@97
    .line 1172
    move-object/from16 v0, p0

    #@99
    iget-object v4, v0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@9b
    if-eqz v4, :cond_4

    #@9d
    if-eqz v35, :cond_4

    #@9f
    .line 1173
    move-object/from16 v0, p0

    #@a1
    iget-object v4, v0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@a3
    const/4 v5, 0x0

    #@a4
    invoke-virtual {v4, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@a7
    move-result-object v4

    #@a8
    move-object/from16 v0, v35

    #@aa
    invoke-virtual {v0, v4}, Landroid/support/v4/util/ArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@ad
    move-result-object v34

    #@ae
    check-cast v34, Landroid/view/View;

    #@b0
    .line 1174
    .local v34, "epicenterView":Landroid/view/View;
    if-eqz v34, :cond_4

    #@b2
    .line 1175
    if-eqz v25, :cond_3

    #@b4
    .line 1176
    move-object/from16 v0, v25

    #@b6
    move-object/from16 v1, v34

    #@b8
    invoke-static {v0, v1}, Landroid/support/v4/app/FragmentTransitionCompat21;->setEpicenter(Ljava/lang/Object;Landroid/view/View;)V

    #@bb
    .line 1178
    :cond_3
    if-eqz v7, :cond_4

    #@bd
    .line 1179
    move-object/from16 v0, v34

    #@bf
    invoke-static {v7, v0}, Landroid/support/v4/app/FragmentTransitionCompat21;->setEpicenter(Ljava/lang/Object;Landroid/view/View;)V

    #@c2
    .line 1185
    .end local v34    # "epicenterView":Landroid/view/View;
    :cond_4
    new-instance v15, Landroid/support/v4/app/BackStackRecord$1;

    #@c4
    move-object/from16 v0, p0

    #@c6
    invoke-direct {v15, v0, v8}, Landroid/support/v4/app/BackStackRecord$1;-><init>(Landroid/support/v4/app/BackStackRecord;Landroid/support/v4/app/Fragment;)V

    #@c9
    .line 1193
    .local v15, "viewRetriever":Landroid/support/v4/app/FragmentTransitionCompat21$ViewRetriever;
    if-eqz v7, :cond_5

    #@cb
    move-object/from16 v4, p0

    #@cd
    move-object/from16 v5, p2

    #@cf
    move/from16 v10, p3

    #@d1
    .line 1194
    invoke-direct/range {v4 .. v11}, Landroid/support/v4/app/BackStackRecord;->prepareSharedElementTransition(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/view/View;Ljava/lang/Object;Landroid/support/v4/app/Fragment;Landroid/support/v4/app/Fragment;ZLjava/util/ArrayList;)V

    #@d4
    .line 1198
    :cond_5
    new-instance v19, Ljava/util/ArrayList;

    #@d6
    invoke-direct/range {v19 .. v19}, Ljava/util/ArrayList;-><init>()V

    #@d9
    .line 1199
    .local v19, "enteringViews":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    new-instance v20, Landroid/support/v4/util/ArrayMap;

    #@db
    invoke-direct/range {v20 .. v20}, Landroid/support/v4/util/ArrayMap;-><init>()V

    #@de
    .line 1201
    .local v20, "renamedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    if-eqz p3, :cond_9

    #@e0
    invoke-virtual {v8}, Landroid/support/v4/app/Fragment;->getAllowReturnTransitionOverlap()Z

    #@e3
    move-result v32

    #@e4
    .line 1203
    .local v32, "allowOverlap":Z
    :goto_3
    move-object/from16 v0, v25

    #@e6
    move/from16 v1, v32

    #@e8
    invoke-static {v12, v0, v7, v1}, Landroid/support/v4/app/FragmentTransitionCompat21;->mergeTransitions(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Z)Ljava/lang/Object;

    #@eb
    move-result-object v29

    #@ec
    .line 1206
    .local v29, "transition":Ljava/lang/Object;
    if-eqz v29, :cond_6

    #@ee
    .line 1207
    move-object/from16 v0, p2

    #@f0
    iget-object v0, v0, Landroid/support/v4/app/BackStackRecord$TransitionState;->nonExistentView:Landroid/view/View;

    #@f2
    move-object/from16 v16, v0

    #@f4
    move-object/from16 v0, p2

    #@f6
    iget-object v0, v0, Landroid/support/v4/app/BackStackRecord$TransitionState;->enteringEpicenterView:Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;

    #@f8
    move-object/from16 v17, v0

    #@fa
    move-object/from16 v0, p2

    #@fc
    iget-object v0, v0, Landroid/support/v4/app/BackStackRecord$TransitionState;->nameOverrides:Landroid/support/v4/util/ArrayMap;

    #@fe
    move-object/from16 v18, v0

    #@100
    move-object v13, v7

    #@101
    move-object v14, v6

    #@102
    move-object/from16 v21, v11

    #@104
    invoke-static/range {v12 .. v21}, Landroid/support/v4/app/FragmentTransitionCompat21;->addTransitionTargets(Ljava/lang/Object;Ljava/lang/Object;Landroid/view/View;Landroid/support/v4/app/FragmentTransitionCompat21$ViewRetriever;Landroid/view/View;Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;Ljava/util/Map;Ljava/util/ArrayList;Ljava/util/Map;Ljava/util/ArrayList;)V

    #@107
    .line 1211
    move-object/from16 v0, p0

    #@109
    move-object/from16 v1, p2

    #@10b
    move/from16 v2, p1

    #@10d
    move-object/from16 v3, v29

    #@10f
    invoke-direct {v0, v6, v1, v2, v3}, Landroid/support/v4/app/BackStackRecord;->excludeHiddenFragmentsAfterEnter(Landroid/view/View;Landroid/support/v4/app/BackStackRecord$TransitionState;ILjava/lang/Object;)V

    #@112
    .line 1215
    move-object/from16 v0, p2

    #@114
    iget-object v4, v0, Landroid/support/v4/app/BackStackRecord$TransitionState;->nonExistentView:Landroid/view/View;

    #@116
    const/4 v5, 0x1

    #@117
    move-object/from16 v0, v29

    #@119
    invoke-static {v0, v4, v5}, Landroid/support/v4/app/FragmentTransitionCompat21;->excludeTarget(Ljava/lang/Object;Landroid/view/View;Z)V

    #@11c
    .line 1217
    move-object/from16 v0, p0

    #@11e
    move-object/from16 v1, p2

    #@120
    move/from16 v2, p1

    #@122
    move-object/from16 v3, v29

    #@124
    invoke-direct {v0, v1, v2, v3}, Landroid/support/v4/app/BackStackRecord;->excludeHiddenFragments(Landroid/support/v4/app/BackStackRecord$TransitionState;ILjava/lang/Object;)V

    #@127
    .line 1219
    move-object/from16 v0, v29

    #@129
    invoke-static {v6, v0}, Landroid/support/v4/app/FragmentTransitionCompat21;->beginDelayedTransition(Landroid/view/ViewGroup;Ljava/lang/Object;)V

    #@12c
    .line 1221
    move-object/from16 v0, p2

    #@12e
    iget-object v0, v0, Landroid/support/v4/app/BackStackRecord$TransitionState;->nonExistentView:Landroid/view/View;

    #@130
    move-object/from16 v22, v0

    #@132
    move-object/from16 v0, p2

    #@134
    iget-object v0, v0, Landroid/support/v4/app/BackStackRecord$TransitionState;->hiddenFragmentViews:Ljava/util/ArrayList;

    #@136
    move-object/from16 v30, v0

    #@138
    move-object/from16 v21, v6

    #@13a
    move-object/from16 v23, v12

    #@13c
    move-object/from16 v24, v19

    #@13e
    move-object/from16 v27, v7

    #@140
    move-object/from16 v28, v11

    #@142
    move-object/from16 v31, v20

    #@144
    invoke-static/range {v21 .. v31}, Landroid/support/v4/app/FragmentTransitionCompat21;->cleanupTransitions(Landroid/view/View;Landroid/view/View;Ljava/lang/Object;Ljava/util/ArrayList;Ljava/lang/Object;Ljava/util/ArrayList;Ljava/lang/Object;Ljava/util/ArrayList;Ljava/lang/Object;Ljava/util/ArrayList;Ljava/util/Map;)V

    #@147
    .line 1226
    :cond_6
    if-eqz v29, :cond_a

    #@149
    const/4 v4, 0x1

    #@14a
    goto/16 :goto_0

    #@14c
    .line 1153
    .end local v15    # "viewRetriever":Landroid/support/v4/app/FragmentTransitionCompat21$ViewRetriever;
    .end local v19    # "enteringViews":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    .end local v20    # "renamedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    .end local v26    # "exitingViews":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    .end local v29    # "transition":Ljava/lang/Object;
    .end local v32    # "allowOverlap":Z
    :cond_7
    invoke-virtual/range {v35 .. v35}, Landroid/support/v4/util/ArrayMap;->values()Ljava/util/Collection;

    #@14f
    move-result-object v4

    #@150
    invoke-virtual {v11, v4}, Ljava/util/ArrayList;->addAll(Ljava/util/Collection;)Z

    #@153
    goto/16 :goto_1

    #@155
    .line 1157
    :cond_8
    iget-object v0, v8, Landroid/support/v4/app/Fragment;->mEnterTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@157
    move-object/from16 v33, v0

    #@159
    goto/16 :goto_2

    #@15b
    .line 1201
    .restart local v15    # "viewRetriever":Landroid/support/v4/app/FragmentTransitionCompat21$ViewRetriever;
    .restart local v19    # "enteringViews":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    .restart local v20    # "renamedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    .restart local v26    # "exitingViews":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    :cond_9
    invoke-virtual {v8}, Landroid/support/v4/app/Fragment;->getAllowEnterTransitionOverlap()Z

    #@15e
    move-result v32

    #@15f
    goto :goto_3

    #@160
    .line 1226
    .restart local v29    # "transition":Ljava/lang/Object;
    .restart local v32    # "allowOverlap":Z
    :cond_a
    const/4 v4, 0x0

    #@161
    goto/16 :goto_0
.end method

.method private doAddOp(ILandroid/support/v4/app/Fragment;Ljava/lang/String;I)V
    .locals 4
    .param p1, "containerViewId"    # I
    .param p2, "fragment"    # Landroid/support/v4/app/Fragment;
    .param p3, "tag"    # Ljava/lang/String;
    .param p4, "opcmd"    # I

    #@0
    .prologue
    .line 416
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    iput-object v1, p2, Landroid/support/v4/app/Fragment;->mFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@4
    .line 418
    if-eqz p3, :cond_1

    #@6
    .line 419
    iget-object v1, p2, Landroid/support/v4/app/Fragment;->mTag:Ljava/lang/String;

    #@8
    if-eqz v1, :cond_0

    #@a
    iget-object v1, p2, Landroid/support/v4/app/Fragment;->mTag:Ljava/lang/String;

    #@c
    invoke-virtual {p3, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@f
    move-result v1

    #@10
    if-nez v1, :cond_0

    #@12
    .line 420
    new-instance v1, Ljava/lang/IllegalStateException;

    #@14
    new-instance v2, Ljava/lang/StringBuilder;

    #@16
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@19
    const-string v3, "Can\'t change tag of fragment "

    #@1b
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1e
    move-result-object v2

    #@1f
    invoke-virtual {v2, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@22
    move-result-object v2

    #@23
    const-string v3, ": was "

    #@25
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@28
    move-result-object v2

    #@29
    iget-object v3, p2, Landroid/support/v4/app/Fragment;->mTag:Ljava/lang/String;

    #@2b
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2e
    move-result-object v2

    #@2f
    const-string v3, " now "

    #@31
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@34
    move-result-object v2

    #@35
    invoke-virtual {v2, p3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@38
    move-result-object v2

    #@39
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@3c
    move-result-object v2

    #@3d
    invoke-direct {v1, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@40
    throw v1

    #@41
    .line 424
    :cond_0
    iput-object p3, p2, Landroid/support/v4/app/Fragment;->mTag:Ljava/lang/String;

    #@43
    .line 427
    :cond_1
    if-eqz p1, :cond_3

    #@45
    .line 428
    iget v1, p2, Landroid/support/v4/app/Fragment;->mFragmentId:I

    #@47
    if-eqz v1, :cond_2

    #@49
    iget v1, p2, Landroid/support/v4/app/Fragment;->mFragmentId:I

    #@4b
    if-eq v1, p1, :cond_2

    #@4d
    .line 429
    new-instance v1, Ljava/lang/IllegalStateException;

    #@4f
    new-instance v2, Ljava/lang/StringBuilder;

    #@51
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@54
    const-string v3, "Can\'t change container ID of fragment "

    #@56
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@59
    move-result-object v2

    #@5a
    invoke-virtual {v2, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@5d
    move-result-object v2

    #@5e
    const-string v3, ": was "

    #@60
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@63
    move-result-object v2

    #@64
    iget v3, p2, Landroid/support/v4/app/Fragment;->mFragmentId:I

    #@66
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@69
    move-result-object v2

    #@6a
    const-string v3, " now "

    #@6c
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@6f
    move-result-object v2

    #@70
    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@73
    move-result-object v2

    #@74
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@77
    move-result-object v2

    #@78
    invoke-direct {v1, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@7b
    throw v1

    #@7c
    .line 433
    :cond_2
    iput p1, p2, Landroid/support/v4/app/Fragment;->mFragmentId:I

    #@7e
    iput p1, p2, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@80
    .line 436
    :cond_3
    new-instance v0, Landroid/support/v4/app/BackStackRecord$Op;

    #@82
    invoke-direct {v0}, Landroid/support/v4/app/BackStackRecord$Op;-><init>()V

    #@85
    .line 437
    .local v0, "op":Landroid/support/v4/app/BackStackRecord$Op;
    iput p4, v0, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@87
    .line 438
    iput-object p2, v0, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@89
    .line 439
    invoke-virtual {p0, v0}, Landroid/support/v4/app/BackStackRecord;->addOp(Landroid/support/v4/app/BackStackRecord$Op;)V

    #@8c
    .line 440
    return-void
.end method

.method private excludeHiddenFragments(Landroid/support/v4/app/BackStackRecord$TransitionState;ILjava/lang/Object;)V
    .locals 4
    .param p1, "state"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .param p2, "containerId"    # I
    .param p3, "transition"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 1375
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    iget-object v2, v2, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@4
    if-eqz v2, :cond_2

    #@6
    .line 1376
    const/4 v1, 0x0

    #@7
    .local v1, "i":I
    :goto_0
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@9
    iget-object v2, v2, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@b
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@e
    move-result v2

    #@f
    if-ge v1, v2, :cond_2

    #@11
    .line 1377
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@13
    iget-object v2, v2, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@15
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@18
    move-result-object v0

    #@19
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@1b
    .line 1378
    .local v0, "fragment":Landroid/support/v4/app/Fragment;
    iget-object v2, v0, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@1d
    if-eqz v2, :cond_0

    #@1f
    iget-object v2, v0, Landroid/support/v4/app/Fragment;->mContainer:Landroid/view/ViewGroup;

    #@21
    if-eqz v2, :cond_0

    #@23
    iget v2, v0, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@25
    if-ne v2, p2, :cond_0

    #@27
    .line 1380
    iget-boolean v2, v0, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@29
    if-eqz v2, :cond_1

    #@2b
    .line 1381
    iget-object v2, p1, Landroid/support/v4/app/BackStackRecord$TransitionState;->hiddenFragmentViews:Ljava/util/ArrayList;

    #@2d
    iget-object v3, v0, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@2f
    invoke-virtual {v2, v3}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    #@32
    move-result v2

    #@33
    if-nez v2, :cond_0

    #@35
    .line 1382
    iget-object v2, v0, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@37
    const/4 v3, 0x1

    #@38
    invoke-static {p3, v2, v3}, Landroid/support/v4/app/FragmentTransitionCompat21;->excludeTarget(Ljava/lang/Object;Landroid/view/View;Z)V

    #@3b
    .line 1384
    iget-object v2, p1, Landroid/support/v4/app/BackStackRecord$TransitionState;->hiddenFragmentViews:Ljava/util/ArrayList;

    #@3d
    iget-object v3, v0, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@3f
    invoke-virtual {v2, v3}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@42
    .line 1376
    :cond_0
    :goto_1
    add-int/lit8 v1, v1, 0x1

    #@44
    goto :goto_0

    #@45
    .line 1387
    :cond_1
    iget-object v2, v0, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@47
    const/4 v3, 0x0

    #@48
    invoke-static {p3, v2, v3}, Landroid/support/v4/app/FragmentTransitionCompat21;->excludeTarget(Ljava/lang/Object;Landroid/view/View;Z)V

    #@4b
    .line 1389
    iget-object v2, p1, Landroid/support/v4/app/BackStackRecord$TransitionState;->hiddenFragmentViews:Ljava/util/ArrayList;

    #@4d
    iget-object v3, v0, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@4f
    invoke-virtual {v2, v3}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    #@52
    goto :goto_1

    #@53
    .line 1394
    .end local v0    # "fragment":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_2
    return-void
.end method

.method private excludeHiddenFragmentsAfterEnter(Landroid/view/View;Landroid/support/v4/app/BackStackRecord$TransitionState;ILjava/lang/Object;)V
    .locals 7
    .param p1, "sceneRoot"    # Landroid/view/View;
    .param p2, "state"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .param p3, "containerId"    # I
    .param p4, "transition"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 1364
    invoke-virtual {p1}, Landroid/view/View;->getViewTreeObserver()Landroid/view/ViewTreeObserver;

    #@3
    move-result-object v6

    #@4
    new-instance v0, Landroid/support/v4/app/BackStackRecord$3;

    #@6
    move-object v1, p0

    #@7
    move-object v2, p1

    #@8
    move-object v3, p2

    #@9
    move v4, p3

    #@a
    move-object v5, p4

    #@b
    invoke-direct/range {v0 .. v5}, Landroid/support/v4/app/BackStackRecord$3;-><init>(Landroid/support/v4/app/BackStackRecord;Landroid/view/View;Landroid/support/v4/app/BackStackRecord$TransitionState;ILjava/lang/Object;)V

    #@e
    invoke-virtual {v6, v0}, Landroid/view/ViewTreeObserver;->addOnPreDrawListener(Landroid/view/ViewTreeObserver$OnPreDrawListener;)V

    #@11
    .line 1372
    return-void
.end method

.method private static getEnterTransition(Landroid/support/v4/app/Fragment;Z)Ljava/lang/Object;
    .locals 1
    .param p0, "inFragment"    # Landroid/support/v4/app/Fragment;
    .param p1, "isBack"    # Z

    #@0
    .prologue
    .line 1053
    if-nez p0, :cond_0

    #@2
    .line 1054
    const/4 v0, 0x0

    #@3
    .line 1056
    :goto_0
    return-object v0

    #@4
    :cond_0
    if-eqz p1, :cond_1

    #@6
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getReenterTransition()Ljava/lang/Object;

    #@9
    move-result-object v0

    #@a
    :goto_1
    invoke-static {v0}, Landroid/support/v4/app/FragmentTransitionCompat21;->cloneTransition(Ljava/lang/Object;)Ljava/lang/Object;

    #@d
    move-result-object v0

    #@e
    goto :goto_0

    #@f
    :cond_1
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getEnterTransition()Ljava/lang/Object;

    #@12
    move-result-object v0

    #@13
    goto :goto_1
.end method

.method private static getExitTransition(Landroid/support/v4/app/Fragment;Z)Ljava/lang/Object;
    .locals 1
    .param p0, "outFragment"    # Landroid/support/v4/app/Fragment;
    .param p1, "isBack"    # Z

    #@0
    .prologue
    .line 1061
    if-nez p0, :cond_0

    #@2
    .line 1062
    const/4 v0, 0x0

    #@3
    .line 1064
    :goto_0
    return-object v0

    #@4
    :cond_0
    if-eqz p1, :cond_1

    #@6
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getReturnTransition()Ljava/lang/Object;

    #@9
    move-result-object v0

    #@a
    :goto_1
    invoke-static {v0}, Landroid/support/v4/app/FragmentTransitionCompat21;->cloneTransition(Ljava/lang/Object;)Ljava/lang/Object;

    #@d
    move-result-object v0

    #@e
    goto :goto_0

    #@f
    :cond_1
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getExitTransition()Ljava/lang/Object;

    #@12
    move-result-object v0

    #@13
    goto :goto_1
.end method

.method private static getSharedElementTransition(Landroid/support/v4/app/Fragment;Landroid/support/v4/app/Fragment;Z)Ljava/lang/Object;
    .locals 1
    .param p0, "inFragment"    # Landroid/support/v4/app/Fragment;
    .param p1, "outFragment"    # Landroid/support/v4/app/Fragment;
    .param p2, "isBack"    # Z

    #@0
    .prologue
    .line 1070
    if-eqz p0, :cond_0

    #@2
    if-nez p1, :cond_1

    #@4
    .line 1071
    :cond_0
    const/4 v0, 0x0

    #@5
    .line 1073
    :goto_0
    return-object v0

    #@6
    :cond_1
    if-eqz p2, :cond_2

    #@8
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->getSharedElementReturnTransition()Ljava/lang/Object;

    #@b
    move-result-object v0

    #@c
    :goto_1
    invoke-static {v0}, Landroid/support/v4/app/FragmentTransitionCompat21;->cloneTransition(Ljava/lang/Object;)Ljava/lang/Object;

    #@f
    move-result-object v0

    #@10
    goto :goto_0

    #@11
    :cond_2
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getSharedElementEnterTransition()Ljava/lang/Object;

    #@14
    move-result-object v0

    #@15
    goto :goto_1
.end method

.method private mapEnteringSharedElements(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/support/v4/app/Fragment;Z)Landroid/support/v4/util/ArrayMap;
    .locals 4
    .param p1, "state"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .param p2, "inFragment"    # Landroid/support/v4/app/Fragment;
    .param p3, "isBack"    # Z
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v4/app/BackStackRecord$TransitionState;",
            "Landroid/support/v4/app/Fragment;",
            "Z)",
            "Landroid/support/v4/util/ArrayMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;"
        }
    .end annotation

    #@0
    .prologue
    .line 1346
    new-instance v0, Landroid/support/v4/util/ArrayMap;

    #@2
    invoke-direct {v0}, Landroid/support/v4/util/ArrayMap;-><init>()V

    #@5
    .line 1347
    .local v0, "namedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    invoke-virtual {p2}, Landroid/support/v4/app/Fragment;->getView()Landroid/view/View;

    #@8
    move-result-object v1

    #@9
    .line 1348
    .local v1, "root":Landroid/view/View;
    if-eqz v1, :cond_0

    #@b
    .line 1349
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@d
    if-eqz v2, :cond_0

    #@f
    .line 1350
    invoke-static {v0, v1}, Landroid/support/v4/app/FragmentTransitionCompat21;->findNamedViews(Ljava/util/Map;Landroid/view/View;)V

    #@12
    .line 1351
    if-eqz p3, :cond_1

    #@14
    .line 1352
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@16
    iget-object v3, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@18
    invoke-static {v2, v3, v0}, Landroid/support/v4/app/BackStackRecord;->remapNames(Ljava/util/ArrayList;Ljava/util/ArrayList;Landroid/support/v4/util/ArrayMap;)Landroid/support/v4/util/ArrayMap;

    #@1b
    move-result-object v0

    #@1c
    .line 1359
    :cond_0
    :goto_0
    return-object v0

    #@1d
    .line 1355
    :cond_1
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@1f
    invoke-virtual {v0, v2}, Landroid/support/v4/util/ArrayMap;->retainAll(Ljava/util/Collection;)Z

    #@22
    goto :goto_0
.end method

.method private mapSharedElementsIn(Landroid/support/v4/app/BackStackRecord$TransitionState;ZLandroid/support/v4/app/Fragment;)Landroid/support/v4/util/ArrayMap;
    .locals 4
    .param p1, "state"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .param p2, "isBack"    # Z
    .param p3, "inFragment"    # Landroid/support/v4/app/Fragment;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v4/app/BackStackRecord$TransitionState;",
            "Z",
            "Landroid/support/v4/app/Fragment;",
            ")",
            "Landroid/support/v4/util/ArrayMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;"
        }
    .end annotation

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 1291
    invoke-direct {p0, p1, p3, p2}, Landroid/support/v4/app/BackStackRecord;->mapEnteringSharedElements(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/support/v4/app/Fragment;Z)Landroid/support/v4/util/ArrayMap;

    #@4
    move-result-object v0

    #@5
    .line 1295
    .local v0, "namedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    if-eqz p2, :cond_1

    #@7
    .line 1296
    iget-object v1, p3, Landroid/support/v4/app/Fragment;->mExitTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@9
    if-eqz v1, :cond_0

    #@b
    .line 1297
    iget-object v1, p3, Landroid/support/v4/app/Fragment;->mExitTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@d
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@f
    invoke-virtual {v1, v2, v0}, Landroid/support/v4/app/SharedElementCallback;->onMapSharedElements(Ljava/util/List;Ljava/util/Map;)V

    #@12
    .line 1300
    :cond_0
    invoke-direct {p0, p1, v0, v3}, Landroid/support/v4/app/BackStackRecord;->setBackNameOverrides(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/support/v4/util/ArrayMap;Z)V

    #@15
    .line 1308
    :goto_0
    return-object v0

    #@16
    .line 1302
    :cond_1
    iget-object v1, p3, Landroid/support/v4/app/Fragment;->mEnterTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@18
    if-eqz v1, :cond_2

    #@1a
    .line 1303
    iget-object v1, p3, Landroid/support/v4/app/Fragment;->mEnterTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@1c
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@1e
    invoke-virtual {v1, v2, v0}, Landroid/support/v4/app/SharedElementCallback;->onMapSharedElements(Ljava/util/List;Ljava/util/Map;)V

    #@21
    .line 1306
    :cond_2
    invoke-direct {p0, p1, v0, v3}, Landroid/support/v4/app/BackStackRecord;->setNameOverrides(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/support/v4/util/ArrayMap;Z)V

    #@24
    goto :goto_0
.end method

.method private prepareSharedElementTransition(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/view/View;Ljava/lang/Object;Landroid/support/v4/app/Fragment;Landroid/support/v4/app/Fragment;ZLjava/util/ArrayList;)V
    .locals 10
    .param p1, "state"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .param p2, "sceneRoot"    # Landroid/view/View;
    .param p3, "sharedElementTransition"    # Ljava/lang/Object;
    .param p4, "inFragment"    # Landroid/support/v4/app/Fragment;
    .param p5, "outFragment"    # Landroid/support/v4/app/Fragment;
    .param p6, "isBack"    # Z
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v4/app/BackStackRecord$TransitionState;",
            "Landroid/view/View;",
            "Ljava/lang/Object;",
            "Landroid/support/v4/app/Fragment;",
            "Landroid/support/v4/app/Fragment;",
            "Z",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 1233
    .local p7, "sharedElementTargets":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    invoke-virtual {p2}, Landroid/view/View;->getViewTreeObserver()Landroid/view/ViewTreeObserver;

    #@3
    move-result-object v9

    #@4
    new-instance v0, Landroid/support/v4/app/BackStackRecord$2;

    #@6
    move-object v1, p0

    #@7
    move-object v2, p2

    #@8
    move-object v3, p3

    #@9
    move-object/from16 v4, p7

    #@b
    move-object v5, p1

    #@c
    move/from16 v6, p6

    #@e
    move-object v7, p4

    #@f
    move-object v8, p5

    #@10
    invoke-direct/range {v0 .. v8}, Landroid/support/v4/app/BackStackRecord$2;-><init>(Landroid/support/v4/app/BackStackRecord;Landroid/view/View;Ljava/lang/Object;Ljava/util/ArrayList;Landroid/support/v4/app/BackStackRecord$TransitionState;ZLandroid/support/v4/app/Fragment;Landroid/support/v4/app/Fragment;)V

    #@13
    invoke-virtual {v9, v0}, Landroid/view/ViewTreeObserver;->addOnPreDrawListener(Landroid/view/ViewTreeObserver$OnPreDrawListener;)V

    #@16
    .line 1263
    return-void
.end method

.method private static remapNames(Ljava/util/ArrayList;Ljava/util/ArrayList;Landroid/support/v4/util/ArrayMap;)Landroid/support/v4/util/ArrayMap;
    .locals 5
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;",
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;",
            "Landroid/support/v4/util/ArrayMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;)",
            "Landroid/support/v4/util/ArrayMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;"
        }
    .end annotation

    #@0
    .prologue
    .line 1321
    .local p0, "inMap":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Ljava/lang/String;>;"
    .local p1, "toGoInMap":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Ljava/lang/String;>;"
    .local p2, "namedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    invoke-virtual {p2}, Landroid/support/v4/util/ArrayMap;->isEmpty()Z

    #@3
    move-result v4

    #@4
    if-eqz v4, :cond_0

    #@6
    .line 1332
    .end local p2    # "namedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    :goto_0
    return-object p2

    #@7
    .line 1324
    .restart local p2    # "namedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    :cond_0
    new-instance v2, Landroid/support/v4/util/ArrayMap;

    #@9
    invoke-direct {v2}, Landroid/support/v4/util/ArrayMap;-><init>()V

    #@c
    .line 1325
    .local v2, "remappedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    #@f
    move-result v1

    #@10
    .line 1326
    .local v1, "numKeys":I
    const/4 v0, 0x0

    #@11
    .local v0, "i":I
    :goto_1
    if-ge v0, v1, :cond_2

    #@13
    .line 1327
    invoke-virtual {p0, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@16
    move-result-object v4

    #@17
    invoke-virtual {p2, v4}, Landroid/support/v4/util/ArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@1a
    move-result-object v3

    #@1b
    check-cast v3, Landroid/view/View;

    #@1d
    .line 1328
    .local v3, "view":Landroid/view/View;
    if-eqz v3, :cond_1

    #@1f
    .line 1329
    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@22
    move-result-object v4

    #@23
    invoke-virtual {v2, v4, v3}, Landroid/support/v4/util/ArrayMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@26
    .line 1326
    :cond_1
    add-int/lit8 v0, v0, 0x1

    #@28
    goto :goto_1

    #@29
    .end local v3    # "view":Landroid/view/View;
    :cond_2
    move-object p2, v2

    #@2a
    .line 1332
    goto :goto_0
.end method

.method private remapSharedElements(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/support/v4/app/Fragment;Z)Landroid/support/v4/util/ArrayMap;
    .locals 4
    .param p1, "state"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .param p2, "outFragment"    # Landroid/support/v4/app/Fragment;
    .param p3, "isBack"    # Z
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v4/app/BackStackRecord$TransitionState;",
            "Landroid/support/v4/app/Fragment;",
            "Z)",
            "Landroid/support/v4/util/ArrayMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;"
        }
    .end annotation

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 1089
    new-instance v0, Landroid/support/v4/util/ArrayMap;

    #@3
    invoke-direct {v0}, Landroid/support/v4/util/ArrayMap;-><init>()V

    #@6
    .line 1090
    .local v0, "namedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@8
    if-eqz v1, :cond_0

    #@a
    .line 1091
    invoke-virtual {p2}, Landroid/support/v4/app/Fragment;->getView()Landroid/view/View;

    #@d
    move-result-object v1

    #@e
    invoke-static {v0, v1}, Landroid/support/v4/app/FragmentTransitionCompat21;->findNamedViews(Ljava/util/Map;Landroid/view/View;)V

    #@11
    .line 1092
    if-eqz p3, :cond_2

    #@13
    .line 1093
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@15
    invoke-virtual {v0, v1}, Landroid/support/v4/util/ArrayMap;->retainAll(Ljava/util/Collection;)Z

    #@18
    .line 1100
    :cond_0
    :goto_0
    if-eqz p3, :cond_3

    #@1a
    .line 1101
    iget-object v1, p2, Landroid/support/v4/app/Fragment;->mEnterTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@1c
    if-eqz v1, :cond_1

    #@1e
    .line 1102
    iget-object v1, p2, Landroid/support/v4/app/Fragment;->mEnterTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@20
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@22
    invoke-virtual {v1, v2, v0}, Landroid/support/v4/app/SharedElementCallback;->onMapSharedElements(Ljava/util/List;Ljava/util/Map;)V

    #@25
    .line 1105
    :cond_1
    invoke-direct {p0, p1, v0, v3}, Landroid/support/v4/app/BackStackRecord;->setBackNameOverrides(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/support/v4/util/ArrayMap;Z)V

    #@28
    .line 1114
    :goto_1
    return-object v0

    #@29
    .line 1095
    :cond_2
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@2b
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@2d
    invoke-static {v1, v2, v0}, Landroid/support/v4/app/BackStackRecord;->remapNames(Ljava/util/ArrayList;Ljava/util/ArrayList;Landroid/support/v4/util/ArrayMap;)Landroid/support/v4/util/ArrayMap;

    #@30
    move-result-object v0

    #@31
    goto :goto_0

    #@32
    .line 1107
    :cond_3
    iget-object v1, p2, Landroid/support/v4/app/Fragment;->mExitTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@34
    if-eqz v1, :cond_4

    #@36
    .line 1108
    iget-object v1, p2, Landroid/support/v4/app/Fragment;->mExitTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@38
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@3a
    invoke-virtual {v1, v2, v0}, Landroid/support/v4/app/SharedElementCallback;->onMapSharedElements(Ljava/util/List;Ljava/util/Map;)V

    #@3d
    .line 1111
    :cond_4
    invoke-direct {p0, p1, v0, v3}, Landroid/support/v4/app/BackStackRecord;->setNameOverrides(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/support/v4/util/ArrayMap;Z)V

    #@40
    goto :goto_1
.end method

.method private setBackNameOverrides(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/support/v4/util/ArrayMap;Z)V
    .locals 7
    .param p1, "state"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .param p3, "isEnd"    # Z
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v4/app/BackStackRecord$TransitionState;",
            "Landroid/support/v4/util/ArrayMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;Z)V"
        }
    .end annotation

    #@0
    .prologue
    .line 1422
    .local p2, "namedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    iget-object v6, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@2
    if-nez v6, :cond_1

    #@4
    const/4 v0, 0x0

    #@5
    .line 1423
    .local v0, "count":I
    :goto_0
    const/4 v1, 0x0

    #@6
    .local v1, "i":I
    :goto_1
    if-ge v1, v0, :cond_3

    #@8
    .line 1424
    iget-object v6, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@a
    invoke-virtual {v6, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@d
    move-result-object v3

    #@e
    check-cast v3, Ljava/lang/String;

    #@10
    .line 1425
    .local v3, "source":Ljava/lang/String;
    iget-object v6, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@12
    invoke-virtual {v6, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@15
    move-result-object v2

    #@16
    check-cast v2, Ljava/lang/String;

    #@18
    .line 1426
    .local v2, "originalTarget":Ljava/lang/String;
    invoke-virtual {p2, v2}, Landroid/support/v4/util/ArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@1b
    move-result-object v5

    #@1c
    check-cast v5, Landroid/view/View;

    #@1e
    .line 1427
    .local v5, "view":Landroid/view/View;
    if-eqz v5, :cond_0

    #@20
    .line 1428
    invoke-static {v5}, Landroid/support/v4/app/FragmentTransitionCompat21;->getTransitionName(Landroid/view/View;)Ljava/lang/String;

    #@23
    move-result-object v4

    #@24
    .line 1429
    .local v4, "target":Ljava/lang/String;
    if-eqz p3, :cond_2

    #@26
    .line 1430
    iget-object v6, p1, Landroid/support/v4/app/BackStackRecord$TransitionState;->nameOverrides:Landroid/support/v4/util/ArrayMap;

    #@28
    invoke-static {v6, v3, v4}, Landroid/support/v4/app/BackStackRecord;->setNameOverride(Landroid/support/v4/util/ArrayMap;Ljava/lang/String;Ljava/lang/String;)V

    #@2b
    .line 1423
    .end local v4    # "target":Ljava/lang/String;
    :cond_0
    :goto_2
    add-int/lit8 v1, v1, 0x1

    #@2d
    goto :goto_1

    #@2e
    .line 1422
    .end local v0    # "count":I
    .end local v1    # "i":I
    .end local v2    # "originalTarget":Ljava/lang/String;
    .end local v3    # "source":Ljava/lang/String;
    .end local v5    # "view":Landroid/view/View;
    :cond_1
    iget-object v6, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@30
    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    #@33
    move-result v0

    #@34
    goto :goto_0

    #@35
    .line 1432
    .restart local v0    # "count":I
    .restart local v1    # "i":I
    .restart local v2    # "originalTarget":Ljava/lang/String;
    .restart local v3    # "source":Ljava/lang/String;
    .restart local v4    # "target":Ljava/lang/String;
    .restart local v5    # "view":Landroid/view/View;
    :cond_2
    iget-object v6, p1, Landroid/support/v4/app/BackStackRecord$TransitionState;->nameOverrides:Landroid/support/v4/util/ArrayMap;

    #@37
    invoke-static {v6, v4, v3}, Landroid/support/v4/app/BackStackRecord;->setNameOverride(Landroid/support/v4/util/ArrayMap;Ljava/lang/String;Ljava/lang/String;)V

    #@3a
    goto :goto_2

    #@3b
    .line 1436
    .end local v2    # "originalTarget":Ljava/lang/String;
    .end local v3    # "source":Ljava/lang/String;
    .end local v4    # "target":Ljava/lang/String;
    .end local v5    # "view":Landroid/view/View;
    :cond_3
    return-void
.end method

.method private setEpicenterIn(Landroid/support/v4/util/ArrayMap;Landroid/support/v4/app/BackStackRecord$TransitionState;)V
    .locals 3
    .param p2, "state"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v4/util/ArrayMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;",
            "Landroid/support/v4/app/BackStackRecord$TransitionState;",
            ")V"
        }
    .end annotation

    #@0
    .prologue
    .line 1278
    .local p1, "namedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@2
    if-eqz v1, :cond_0

    #@4
    invoke-virtual {p1}, Landroid/support/v4/util/ArrayMap;->isEmpty()Z

    #@7
    move-result v1

    #@8
    if-nez v1, :cond_0

    #@a
    .line 1280
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@c
    const/4 v2, 0x0

    #@d
    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@10
    move-result-object v1

    #@11
    invoke-virtual {p1, v1}, Landroid/support/v4/util/ArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@14
    move-result-object v0

    #@15
    check-cast v0, Landroid/view/View;

    #@17
    .line 1282
    .local v0, "epicenter":Landroid/view/View;
    if-eqz v0, :cond_0

    #@19
    .line 1283
    iget-object v1, p2, Landroid/support/v4/app/BackStackRecord$TransitionState;->enteringEpicenterView:Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;

    #@1b
    iput-object v0, v1, Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;->epicenter:Landroid/view/View;

    #@1d
    .line 1286
    .end local v0    # "epicenter":Landroid/view/View;
    :cond_0
    return-void
.end method

.method private static setFirstOut(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V
    .locals 2
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;",
            "Landroid/support/v4/app/Fragment;",
            ")V"
        }
    .end annotation

    #@0
    .prologue
    .line 747
    .local p0, "fragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    if-eqz p1, :cond_0

    #@2
    .line 748
    iget v0, p1, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@4
    .line 749
    .local v0, "containerId":I
    if-eqz v0, :cond_0

    #@6
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->isHidden()Z

    #@9
    move-result v1

    #@a
    if-nez v1, :cond_0

    #@c
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->isAdded()Z

    #@f
    move-result v1

    #@10
    if-eqz v1, :cond_0

    #@12
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->getView()Landroid/view/View;

    #@15
    move-result-object v1

    #@16
    if-eqz v1, :cond_0

    #@18
    invoke-virtual {p0, v0}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    #@1b
    move-result-object v1

    #@1c
    if-nez v1, :cond_0

    #@1e
    .line 751
    invoke-virtual {p0, v0, p1}, Landroid/util/SparseArray;->put(ILjava/lang/Object;)V

    #@21
    .line 754
    .end local v0    # "containerId":I
    :cond_0
    return-void
.end method

.method private setLastIn(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V
    .locals 1
    .param p2, "fragment"    # Landroid/support/v4/app/Fragment;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;",
            "Landroid/support/v4/app/Fragment;",
            ")V"
        }
    .end annotation

    #@0
    .prologue
    .line 757
    .local p1, "fragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    if-eqz p2, :cond_0

    #@2
    .line 758
    iget v0, p2, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@4
    .line 759
    .local v0, "containerId":I
    if-eqz v0, :cond_0

    #@6
    .line 760
    invoke-virtual {p1, v0, p2}, Landroid/util/SparseArray;->put(ILjava/lang/Object;)V

    #@9
    .line 763
    .end local v0    # "containerId":I
    :cond_0
    return-void
.end method

.method private static setNameOverride(Landroid/support/v4/util/ArrayMap;Ljava/lang/String;Ljava/lang/String;)V
    .locals 2
    .param p1, "source"    # Ljava/lang/String;
    .param p2, "target"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v4/util/ArrayMap",
            "<",
            "Ljava/lang/String;",
            "Ljava/lang/String;",
            ">;",
            "Ljava/lang/String;",
            "Ljava/lang/String;",
            ")V"
        }
    .end annotation

    #@0
    .prologue
    .line 1398
    .local p0, "overrides":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Ljava/lang/String;>;"
    if-eqz p1, :cond_0

    #@2
    if-eqz p2, :cond_0

    #@4
    invoke-virtual {p1, p2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@7
    move-result v1

    #@8
    if-nez v1, :cond_0

    #@a
    .line 1399
    const/4 v0, 0x0

    #@b
    .local v0, "index":I
    :goto_0
    invoke-virtual {p0}, Landroid/support/v4/util/ArrayMap;->size()I

    #@e
    move-result v1

    #@f
    if-ge v0, v1, :cond_2

    #@11
    .line 1400
    invoke-virtual {p0, v0}, Landroid/support/v4/util/ArrayMap;->valueAt(I)Ljava/lang/Object;

    #@14
    move-result-object v1

    #@15
    invoke-virtual {p1, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@18
    move-result v1

    #@19
    if-eqz v1, :cond_1

    #@1b
    .line 1401
    invoke-virtual {p0, v0, p2}, Landroid/support/v4/util/ArrayMap;->setValueAt(ILjava/lang/Object;)Ljava/lang/Object;

    #@1e
    .line 1407
    .end local v0    # "index":I
    :cond_0
    :goto_1
    return-void

    #@1f
    .line 1399
    .restart local v0    # "index":I
    :cond_1
    add-int/lit8 v0, v0, 0x1

    #@21
    goto :goto_0

    #@22
    .line 1405
    :cond_2
    invoke-virtual {p0, p1, p2}, Landroid/support/v4/util/ArrayMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@25
    goto :goto_1
.end method

.method private setNameOverrides(Landroid/support/v4/app/BackStackRecord$TransitionState;Landroid/support/v4/util/ArrayMap;Z)V
    .locals 5
    .param p1, "state"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .param p3, "isEnd"    # Z
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v4/app/BackStackRecord$TransitionState;",
            "Landroid/support/v4/util/ArrayMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;Z)V"
        }
    .end annotation

    #@0
    .prologue
    .line 1440
    .local p2, "namedViews":Landroid/support/v4/util/ArrayMap;, "Landroid/support/v4/util/ArrayMap<Ljava/lang/String;Landroid/view/View;>;"
    invoke-virtual {p2}, Landroid/support/v4/util/ArrayMap;->size()I

    #@3
    move-result v0

    #@4
    .line 1441
    .local v0, "count":I
    const/4 v1, 0x0

    #@5
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_1

    #@7
    .line 1442
    invoke-virtual {p2, v1}, Landroid/support/v4/util/ArrayMap;->keyAt(I)Ljava/lang/Object;

    #@a
    move-result-object v2

    #@b
    check-cast v2, Ljava/lang/String;

    #@d
    .line 1443
    .local v2, "source":Ljava/lang/String;
    invoke-virtual {p2, v1}, Landroid/support/v4/util/ArrayMap;->valueAt(I)Ljava/lang/Object;

    #@10
    move-result-object v4

    #@11
    check-cast v4, Landroid/view/View;

    #@13
    invoke-static {v4}, Landroid/support/v4/app/FragmentTransitionCompat21;->getTransitionName(Landroid/view/View;)Ljava/lang/String;

    #@16
    move-result-object v3

    #@17
    .line 1444
    .local v3, "target":Ljava/lang/String;
    if-eqz p3, :cond_0

    #@19
    .line 1445
    iget-object v4, p1, Landroid/support/v4/app/BackStackRecord$TransitionState;->nameOverrides:Landroid/support/v4/util/ArrayMap;

    #@1b
    invoke-static {v4, v2, v3}, Landroid/support/v4/app/BackStackRecord;->setNameOverride(Landroid/support/v4/util/ArrayMap;Ljava/lang/String;Ljava/lang/String;)V

    #@1e
    .line 1441
    :goto_1
    add-int/lit8 v1, v1, 0x1

    #@20
    goto :goto_0

    #@21
    .line 1447
    :cond_0
    iget-object v4, p1, Landroid/support/v4/app/BackStackRecord$TransitionState;->nameOverrides:Landroid/support/v4/util/ArrayMap;

    #@23
    invoke-static {v4, v3, v2}, Landroid/support/v4/app/BackStackRecord;->setNameOverride(Landroid/support/v4/util/ArrayMap;Ljava/lang/String;Ljava/lang/String;)V

    #@26
    goto :goto_1

    #@27
    .line 1450
    .end local v2    # "source":Ljava/lang/String;
    .end local v3    # "target":Ljava/lang/String;
    :cond_1
    return-void
.end method

.method private static setNameOverrides(Landroid/support/v4/app/BackStackRecord$TransitionState;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
    .locals 4
    .param p0, "state"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v4/app/BackStackRecord$TransitionState;",
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;",
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 1411
    .local p1, "sourceNames":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Ljava/lang/String;>;"
    .local p2, "targetNames":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Ljava/lang/String;>;"
    if-eqz p1, :cond_0

    #@2
    .line 1412
    const/4 v0, 0x0

    #@3
    .local v0, "i":I
    :goto_0
    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    #@6
    move-result v3

    #@7
    if-ge v0, v3, :cond_0

    #@9
    .line 1413
    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@c
    move-result-object v1

    #@d
    check-cast v1, Ljava/lang/String;

    #@f
    .line 1414
    .local v1, "source":Ljava/lang/String;
    invoke-virtual {p2, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@12
    move-result-object v2

    #@13
    check-cast v2, Ljava/lang/String;

    #@15
    .line 1415
    .local v2, "target":Ljava/lang/String;
    iget-object v3, p0, Landroid/support/v4/app/BackStackRecord$TransitionState;->nameOverrides:Landroid/support/v4/util/ArrayMap;

    #@17
    invoke-static {v3, v1, v2}, Landroid/support/v4/app/BackStackRecord;->setNameOverride(Landroid/support/v4/util/ArrayMap;Ljava/lang/String;Ljava/lang/String;)V

    #@1a
    .line 1412
    add-int/lit8 v0, v0, 0x1

    #@1c
    goto :goto_0

    #@1d
    .line 1418
    .end local v0    # "i":I
    .end local v1    # "source":Ljava/lang/String;
    .end local v2    # "target":Ljava/lang/String;
    :cond_0
    return-void
.end method


# virtual methods
.method public add(ILandroid/support/v4/app/Fragment;)Landroid/support/v4/app/FragmentTransaction;
    .locals 2
    .param p1, "containerViewId"    # I
    .param p2, "fragment"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 406
    const/4 v0, 0x0

    #@1
    const/4 v1, 0x1

    #@2
    invoke-direct {p0, p1, p2, v0, v1}, Landroid/support/v4/app/BackStackRecord;->doAddOp(ILandroid/support/v4/app/Fragment;Ljava/lang/String;I)V

    #@5
    .line 407
    return-object p0
.end method

.method public add(ILandroid/support/v4/app/Fragment;Ljava/lang/String;)Landroid/support/v4/app/FragmentTransaction;
    .locals 1
    .param p1, "containerViewId"    # I
    .param p2, "fragment"    # Landroid/support/v4/app/Fragment;
    .param p3, "tag"    # Ljava/lang/String;

    #@0
    .prologue
    .line 411
    const/4 v0, 0x1

    #@1
    invoke-direct {p0, p1, p2, p3, v0}, Landroid/support/v4/app/BackStackRecord;->doAddOp(ILandroid/support/v4/app/Fragment;Ljava/lang/String;I)V

    #@4
    .line 412
    return-object p0
.end method

.method public add(Landroid/support/v4/app/Fragment;Ljava/lang/String;)Landroid/support/v4/app/FragmentTransaction;
    .locals 2
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;
    .param p2, "tag"    # Ljava/lang/String;

    #@0
    .prologue
    .line 401
    const/4 v0, 0x0

    #@1
    const/4 v1, 0x1

    #@2
    invoke-direct {p0, v0, p1, p2, v1}, Landroid/support/v4/app/BackStackRecord;->doAddOp(ILandroid/support/v4/app/Fragment;Ljava/lang/String;I)V

    #@5
    .line 402
    return-object p0
.end method

.method addOp(Landroid/support/v4/app/BackStackRecord$Op;)V
    .locals 1
    .param p1, "op"    # Landroid/support/v4/app/BackStackRecord$Op;

    #@0
    .prologue
    .line 386
    iget-object v0, p0, Landroid/support/v4/app/BackStackRecord;->mHead:Landroid/support/v4/app/BackStackRecord$Op;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 387
    iput-object p1, p0, Landroid/support/v4/app/BackStackRecord;->mTail:Landroid/support/v4/app/BackStackRecord$Op;

    #@6
    iput-object p1, p0, Landroid/support/v4/app/BackStackRecord;->mHead:Landroid/support/v4/app/BackStackRecord$Op;

    #@8
    .line 393
    :goto_0
    iget v0, p0, Landroid/support/v4/app/BackStackRecord;->mEnterAnim:I

    #@a
    iput v0, p1, Landroid/support/v4/app/BackStackRecord$Op;->enterAnim:I

    #@c
    .line 394
    iget v0, p0, Landroid/support/v4/app/BackStackRecord;->mExitAnim:I

    #@e
    iput v0, p1, Landroid/support/v4/app/BackStackRecord$Op;->exitAnim:I

    #@10
    .line 395
    iget v0, p0, Landroid/support/v4/app/BackStackRecord;->mPopEnterAnim:I

    #@12
    iput v0, p1, Landroid/support/v4/app/BackStackRecord$Op;->popEnterAnim:I

    #@14
    .line 396
    iget v0, p0, Landroid/support/v4/app/BackStackRecord;->mPopExitAnim:I

    #@16
    iput v0, p1, Landroid/support/v4/app/BackStackRecord$Op;->popExitAnim:I

    #@18
    .line 397
    iget v0, p0, Landroid/support/v4/app/BackStackRecord;->mNumOp:I

    #@1a
    add-int/lit8 v0, v0, 0x1

    #@1c
    iput v0, p0, Landroid/support/v4/app/BackStackRecord;->mNumOp:I

    #@1e
    .line 398
    return-void

    #@1f
    .line 389
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/BackStackRecord;->mTail:Landroid/support/v4/app/BackStackRecord$Op;

    #@21
    iput-object v0, p1, Landroid/support/v4/app/BackStackRecord$Op;->prev:Landroid/support/v4/app/BackStackRecord$Op;

    #@23
    .line 390
    iget-object v0, p0, Landroid/support/v4/app/BackStackRecord;->mTail:Landroid/support/v4/app/BackStackRecord$Op;

    #@25
    iput-object p1, v0, Landroid/support/v4/app/BackStackRecord$Op;->next:Landroid/support/v4/app/BackStackRecord$Op;

    #@27
    .line 391
    iput-object p1, p0, Landroid/support/v4/app/BackStackRecord;->mTail:Landroid/support/v4/app/BackStackRecord$Op;

    #@29
    goto :goto_0
.end method

.method public addSharedElement(Landroid/view/View;Ljava/lang/String;)Landroid/support/v4/app/FragmentTransaction;
    .locals 3
    .param p1, "sharedElement"    # Landroid/view/View;
    .param p2, "name"    # Ljava/lang/String;

    #@0
    .prologue
    .line 520
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v2, 0x15

    #@4
    if-lt v1, v2, :cond_2

    #@6
    .line 521
    invoke-static {p1}, Landroid/support/v4/app/FragmentTransitionCompat21;->getTransitionName(Landroid/view/View;)Ljava/lang/String;

    #@9
    move-result-object v0

    #@a
    .line 522
    .local v0, "transitionName":Ljava/lang/String;
    if-nez v0, :cond_0

    #@c
    .line 523
    new-instance v1, Ljava/lang/IllegalArgumentException;

    #@e
    const-string v2, "Unique transitionNames are required for all sharedElements"

    #@10
    invoke-direct {v1, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@13
    throw v1

    #@14
    .line 526
    :cond_0
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@16
    if-nez v1, :cond_1

    #@18
    .line 527
    new-instance v1, Ljava/util/ArrayList;

    #@1a
    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    #@1d
    iput-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@1f
    .line 528
    new-instance v1, Ljava/util/ArrayList;

    #@21
    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    #@24
    iput-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@26
    .line 531
    :cond_1
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@28
    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@2b
    .line 532
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@2d
    invoke-virtual {v1, p2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@30
    .line 534
    .end local v0    # "transitionName":Ljava/lang/String;
    :cond_2
    return-object p0
.end method

.method public addToBackStack(Ljava/lang/String;)Landroid/support/v4/app/FragmentTransaction;
    .locals 2
    .param p1, "name"    # Ljava/lang/String;

    #@0
    .prologue
    .line 543
    iget-boolean v0, p0, Landroid/support/v4/app/BackStackRecord;->mAllowAddToBackStack:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 544
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "This FragmentTransaction is not allowed to be added to the back stack."

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 547
    :cond_0
    const/4 v0, 0x1

    #@d
    iput-boolean v0, p0, Landroid/support/v4/app/BackStackRecord;->mAddToBackStack:Z

    #@f
    .line 548
    iput-object p1, p0, Landroid/support/v4/app/BackStackRecord;->mName:Ljava/lang/String;

    #@11
    .line 549
    return-object p0
.end method

.method public attach(Landroid/support/v4/app/Fragment;)Landroid/support/v4/app/FragmentTransaction;
    .locals 2
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 492
    new-instance v0, Landroid/support/v4/app/BackStackRecord$Op;

    #@2
    invoke-direct {v0}, Landroid/support/v4/app/BackStackRecord$Op;-><init>()V

    #@5
    .line 493
    .local v0, "op":Landroid/support/v4/app/BackStackRecord$Op;
    const/4 v1, 0x7

    #@6
    iput v1, v0, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@8
    .line 494
    iput-object p1, v0, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@a
    .line 495
    invoke-virtual {p0, v0}, Landroid/support/v4/app/BackStackRecord;->addOp(Landroid/support/v4/app/BackStackRecord$Op;)V

    #@d
    .line 497
    return-object p0
.end method

.method bumpBackStackNesting(I)V
    .locals 6
    .param p1, "amt"    # I

    #@0
    .prologue
    .line 590
    iget-boolean v3, p0, Landroid/support/v4/app/BackStackRecord;->mAddToBackStack:Z

    #@2
    if-nez v3, :cond_1

    #@4
    .line 612
    :cond_0
    return-void

    #@5
    .line 593
    :cond_1
    sget-boolean v3, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@7
    if-eqz v3, :cond_2

    #@9
    const-string v3, "FragmentManager"

    #@b
    new-instance v4, Ljava/lang/StringBuilder;

    #@d
    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    #@10
    const-string v5, "Bump nesting in "

    #@12
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@15
    move-result-object v4

    #@16
    invoke-virtual {v4, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@19
    move-result-object v4

    #@1a
    const-string v5, " by "

    #@1c
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1f
    move-result-object v4

    #@20
    invoke-virtual {v4, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@23
    move-result-object v4

    #@24
    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@27
    move-result-object v4

    #@28
    invoke-static {v3, v4}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@2b
    .line 595
    :cond_2
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mHead:Landroid/support/v4/app/BackStackRecord$Op;

    #@2d
    .line 596
    .local v1, "op":Landroid/support/v4/app/BackStackRecord$Op;
    :goto_0
    if-eqz v1, :cond_0

    #@2f
    .line 597
    iget-object v3, v1, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@31
    if-eqz v3, :cond_3

    #@33
    .line 598
    iget-object v3, v1, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@35
    iget v4, v3, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@37
    add-int/2addr v4, p1

    #@38
    iput v4, v3, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@3a
    .line 599
    sget-boolean v3, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@3c
    if-eqz v3, :cond_3

    #@3e
    const-string v3, "FragmentManager"

    #@40
    new-instance v4, Ljava/lang/StringBuilder;

    #@42
    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    #@45
    const-string v5, "Bump nesting of "

    #@47
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@4a
    move-result-object v4

    #@4b
    iget-object v5, v1, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@4d
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@50
    move-result-object v4

    #@51
    const-string v5, " to "

    #@53
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@56
    move-result-object v4

    #@57
    iget-object v5, v1, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@59
    iget v5, v5, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@5b
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@5e
    move-result-object v4

    #@5f
    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@62
    move-result-object v4

    #@63
    invoke-static {v3, v4}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@66
    .line 602
    :cond_3
    iget-object v3, v1, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@68
    if-eqz v3, :cond_5

    #@6a
    .line 603
    iget-object v3, v1, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@6c
    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    #@6f
    move-result v3

    #@70
    add-int/lit8 v0, v3, -0x1

    #@72
    .local v0, "i":I
    :goto_1
    if-ltz v0, :cond_5

    #@74
    .line 604
    iget-object v3, v1, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@76
    invoke-virtual {v3, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@79
    move-result-object v2

    #@7a
    check-cast v2, Landroid/support/v4/app/Fragment;

    #@7c
    .line 605
    .local v2, "r":Landroid/support/v4/app/Fragment;
    iget v3, v2, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@7e
    add-int/2addr v3, p1

    #@7f
    iput v3, v2, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@81
    .line 606
    sget-boolean v3, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@83
    if-eqz v3, :cond_4

    #@85
    const-string v3, "FragmentManager"

    #@87
    new-instance v4, Ljava/lang/StringBuilder;

    #@89
    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    #@8c
    const-string v5, "Bump nesting of "

    #@8e
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@91
    move-result-object v4

    #@92
    invoke-virtual {v4, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@95
    move-result-object v4

    #@96
    const-string v5, " to "

    #@98
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@9b
    move-result-object v4

    #@9c
    iget v5, v2, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@9e
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@a1
    move-result-object v4

    #@a2
    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@a5
    move-result-object v4

    #@a6
    invoke-static {v3, v4}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@a9
    .line 603
    :cond_4
    add-int/lit8 v0, v0, -0x1

    #@ab
    goto :goto_1

    #@ac
    .line 610
    .end local v0    # "i":I
    .end local v2    # "r":Landroid/support/v4/app/Fragment;
    :cond_5
    iget-object v1, v1, Landroid/support/v4/app/BackStackRecord$Op;->next:Landroid/support/v4/app/BackStackRecord$Op;

    #@ae
    goto/16 :goto_0
.end method

.method public calculateBackFragments(Landroid/util/SparseArray;Landroid/util/SparseArray;)V
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 834
    .local p1, "firstOutFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    .local p2, "lastInFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    iget-object v2, v2, Landroid/support/v4/app/FragmentManagerImpl;->mContainer:Landroid/support/v4/app/FragmentContainer;

    #@4
    invoke-interface {v2}, Landroid/support/v4/app/FragmentContainer;->hasView()Z

    #@7
    move-result v2

    #@8
    if-nez v2, :cond_1

    #@a
    .line 870
    :cond_0
    return-void

    #@b
    .line 837
    :cond_1
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mHead:Landroid/support/v4/app/BackStackRecord$Op;

    #@d
    .line 838
    .local v1, "op":Landroid/support/v4/app/BackStackRecord$Op;
    :goto_0
    if-eqz v1, :cond_0

    #@f
    .line 839
    iget v2, v1, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@11
    packed-switch v2, :pswitch_data_0

    #@14
    .line 868
    :goto_1
    iget-object v1, v1, Landroid/support/v4/app/BackStackRecord$Op;->next:Landroid/support/v4/app/BackStackRecord$Op;

    #@16
    goto :goto_0

    #@17
    .line 841
    :pswitch_0
    iget-object v2, v1, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@19
    invoke-static {p1, v2}, Landroid/support/v4/app/BackStackRecord;->setFirstOut(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@1c
    goto :goto_1

    #@1d
    .line 844
    :pswitch_1
    iget-object v2, v1, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@1f
    if-eqz v2, :cond_2

    #@21
    .line 845
    iget-object v2, v1, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@23
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@26
    move-result v2

    #@27
    add-int/lit8 v0, v2, -0x1

    #@29
    .local v0, "i":I
    :goto_2
    if-ltz v0, :cond_2

    #@2b
    .line 846
    iget-object v2, v1, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@2d
    invoke-virtual {v2, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@30
    move-result-object v2

    #@31
    check-cast v2, Landroid/support/v4/app/Fragment;

    #@33
    invoke-direct {p0, p2, v2}, Landroid/support/v4/app/BackStackRecord;->setLastIn(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@36
    .line 845
    add-int/lit8 v0, v0, -0x1

    #@38
    goto :goto_2

    #@39
    .line 849
    .end local v0    # "i":I
    :cond_2
    iget-object v2, v1, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@3b
    invoke-static {p1, v2}, Landroid/support/v4/app/BackStackRecord;->setFirstOut(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@3e
    goto :goto_1

    #@3f
    .line 852
    :pswitch_2
    iget-object v2, v1, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@41
    invoke-direct {p0, p2, v2}, Landroid/support/v4/app/BackStackRecord;->setLastIn(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@44
    goto :goto_1

    #@45
    .line 855
    :pswitch_3
    iget-object v2, v1, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@47
    invoke-direct {p0, p2, v2}, Landroid/support/v4/app/BackStackRecord;->setLastIn(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@4a
    goto :goto_1

    #@4b
    .line 858
    :pswitch_4
    iget-object v2, v1, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@4d
    invoke-static {p1, v2}, Landroid/support/v4/app/BackStackRecord;->setFirstOut(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@50
    goto :goto_1

    #@51
    .line 861
    :pswitch_5
    iget-object v2, v1, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@53
    invoke-direct {p0, p2, v2}, Landroid/support/v4/app/BackStackRecord;->setLastIn(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@56
    goto :goto_1

    #@57
    .line 864
    :pswitch_6
    iget-object v2, v1, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@59
    invoke-static {p1, v2}, Landroid/support/v4/app/BackStackRecord;->setFirstOut(Landroid/util/SparseArray;Landroid/support/v4/app/Fragment;)V

    #@5c
    goto :goto_1

    #@5d
    .line 839
    nop

    #@5e
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
        :pswitch_4
        :pswitch_5
        :pswitch_6
    .end packed-switch
.end method

.method public commit()I
    .locals 1

    #@0
    .prologue
    .line 615
    const/4 v0, 0x0

    #@1
    invoke-virtual {p0, v0}, Landroid/support/v4/app/BackStackRecord;->commitInternal(Z)I

    #@4
    move-result v0

    #@5
    return v0
.end method

.method public commitAllowingStateLoss()I
    .locals 1

    #@0
    .prologue
    .line 619
    const/4 v0, 0x1

    #@1
    invoke-virtual {p0, v0}, Landroid/support/v4/app/BackStackRecord;->commitInternal(Z)I

    #@4
    move-result v0

    #@5
    return v0
.end method

.method commitInternal(Z)I
    .locals 6
    .param p1, "allowStateLoss"    # Z

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    .line 623
    iget-boolean v2, p0, Landroid/support/v4/app/BackStackRecord;->mCommitted:Z

    #@3
    if-eqz v2, :cond_0

    #@5
    new-instance v2, Ljava/lang/IllegalStateException;

    #@7
    const-string v3, "commit already called"

    #@9
    invoke-direct {v2, v3}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@c
    throw v2

    #@d
    .line 624
    :cond_0
    sget-boolean v2, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@f
    if-eqz v2, :cond_1

    #@11
    .line 625
    const-string v2, "FragmentManager"

    #@13
    new-instance v3, Ljava/lang/StringBuilder;

    #@15
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@18
    const-string v4, "Commit: "

    #@1a
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1d
    move-result-object v3

    #@1e
    invoke-virtual {v3, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@21
    move-result-object v3

    #@22
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@25
    move-result-object v3

    #@26
    invoke-static {v2, v3}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@29
    .line 626
    new-instance v0, Landroid/support/v4/util/LogWriter;

    #@2b
    const-string v2, "FragmentManager"

    #@2d
    invoke-direct {v0, v2}, Landroid/support/v4/util/LogWriter;-><init>(Ljava/lang/String;)V

    #@30
    .line 627
    .local v0, "logw":Landroid/support/v4/util/LogWriter;
    new-instance v1, Ljava/io/PrintWriter;

    #@32
    invoke-direct {v1, v0}, Ljava/io/PrintWriter;-><init>(Ljava/io/Writer;)V

    #@35
    .line 628
    .local v1, "pw":Ljava/io/PrintWriter;
    const-string v2, "  "

    #@37
    invoke-virtual {p0, v2, v5, v1, v5}, Landroid/support/v4/app/BackStackRecord;->dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V

    #@3a
    .line 630
    .end local v0    # "logw":Landroid/support/v4/util/LogWriter;
    .end local v1    # "pw":Ljava/io/PrintWriter;
    :cond_1
    const/4 v2, 0x1

    #@3b
    iput-boolean v2, p0, Landroid/support/v4/app/BackStackRecord;->mCommitted:Z

    #@3d
    .line 631
    iget-boolean v2, p0, Landroid/support/v4/app/BackStackRecord;->mAddToBackStack:Z

    #@3f
    if-eqz v2, :cond_2

    #@41
    .line 632
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@43
    invoke-virtual {v2, p0}, Landroid/support/v4/app/FragmentManagerImpl;->allocBackStackIndex(Landroid/support/v4/app/BackStackRecord;)I

    #@46
    move-result v2

    #@47
    iput v2, p0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@49
    .line 636
    :goto_0
    iget-object v2, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@4b
    invoke-virtual {v2, p0, p1}, Landroid/support/v4/app/FragmentManagerImpl;->enqueueAction(Ljava/lang/Runnable;Z)V

    #@4e
    .line 637
    iget v2, p0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@50
    return v2

    #@51
    .line 634
    :cond_2
    const/4 v2, -0x1

    #@52
    iput v2, p0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@54
    goto :goto_0
.end method

.method public detach(Landroid/support/v4/app/Fragment;)Landroid/support/v4/app/FragmentTransaction;
    .locals 2
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 483
    new-instance v0, Landroid/support/v4/app/BackStackRecord$Op;

    #@2
    invoke-direct {v0}, Landroid/support/v4/app/BackStackRecord$Op;-><init>()V

    #@5
    .line 484
    .local v0, "op":Landroid/support/v4/app/BackStackRecord$Op;
    const/4 v1, 0x6

    #@6
    iput v1, v0, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@8
    .line 485
    iput-object p1, v0, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@a
    .line 486
    invoke-virtual {p0, v0}, Landroid/support/v4/app/BackStackRecord;->addOp(Landroid/support/v4/app/BackStackRecord$Op;)V

    #@d
    .line 488
    return-object p0
.end method

.method public disallowAddToBackStack()Landroid/support/v4/app/FragmentTransaction;
    .locals 2

    #@0
    .prologue
    .line 557
    iget-boolean v0, p0, Landroid/support/v4/app/BackStackRecord;->mAddToBackStack:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 558
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "This transaction is already being added to the back stack"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 561
    :cond_0
    const/4 v0, 0x0

    #@d
    iput-boolean v0, p0, Landroid/support/v4/app/BackStackRecord;->mAllowAddToBackStack:Z

    #@f
    .line 562
    return-object p0
.end method

.method public dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V
    .locals 1
    .param p1, "prefix"    # Ljava/lang/String;
    .param p2, "fd"    # Ljava/io/FileDescriptor;
    .param p3, "writer"    # Ljava/io/PrintWriter;
    .param p4, "args"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 259
    const/4 v0, 0x1

    #@1
    invoke-virtual {p0, p1, p3, v0}, Landroid/support/v4/app/BackStackRecord;->dump(Ljava/lang/String;Ljava/io/PrintWriter;Z)V

    #@4
    .line 260
    return-void
.end method

.method public dump(Ljava/lang/String;Ljava/io/PrintWriter;Z)V
    .locals 7
    .param p1, "prefix"    # Ljava/lang/String;
    .param p2, "writer"    # Ljava/io/PrintWriter;
    .param p3, "full"    # Z

    #@0
    .prologue
    .line 263
    if-eqz p3, :cond_8

    #@2
    .line 264
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@5
    const-string v5, "mName="

    #@7
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@a
    iget-object v5, p0, Landroid/support/v4/app/BackStackRecord;->mName:Ljava/lang/String;

    #@c
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@f
    .line 265
    const-string v5, " mIndex="

    #@11
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@14
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@16
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(I)V

    #@19
    .line 266
    const-string v5, " mCommitted="

    #@1b
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1e
    iget-boolean v5, p0, Landroid/support/v4/app/BackStackRecord;->mCommitted:Z

    #@20
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->println(Z)V

    #@23
    .line 267
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mTransition:I

    #@25
    if-eqz v5, :cond_0

    #@27
    .line 268
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@2a
    const-string v5, "mTransition=#"

    #@2c
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@2f
    .line 269
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mTransition:I

    #@31
    invoke-static {v5}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@34
    move-result-object v5

    #@35
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@38
    .line 270
    const-string v5, " mTransitionStyle=#"

    #@3a
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@3d
    .line 271
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mTransitionStyle:I

    #@3f
    invoke-static {v5}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@42
    move-result-object v5

    #@43
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@46
    .line 273
    :cond_0
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mEnterAnim:I

    #@48
    if-nez v5, :cond_1

    #@4a
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mExitAnim:I

    #@4c
    if-eqz v5, :cond_2

    #@4e
    .line 274
    :cond_1
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@51
    const-string v5, "mEnterAnim=#"

    #@53
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@56
    .line 275
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mEnterAnim:I

    #@58
    invoke-static {v5}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@5b
    move-result-object v5

    #@5c
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@5f
    .line 276
    const-string v5, " mExitAnim=#"

    #@61
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@64
    .line 277
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mExitAnim:I

    #@66
    invoke-static {v5}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@69
    move-result-object v5

    #@6a
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@6d
    .line 279
    :cond_2
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mPopEnterAnim:I

    #@6f
    if-nez v5, :cond_3

    #@71
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mPopExitAnim:I

    #@73
    if-eqz v5, :cond_4

    #@75
    .line 280
    :cond_3
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@78
    const-string v5, "mPopEnterAnim=#"

    #@7a
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@7d
    .line 281
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mPopEnterAnim:I

    #@7f
    invoke-static {v5}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@82
    move-result-object v5

    #@83
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@86
    .line 282
    const-string v5, " mPopExitAnim=#"

    #@88
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@8b
    .line 283
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mPopExitAnim:I

    #@8d
    invoke-static {v5}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@90
    move-result-object v5

    #@91
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@94
    .line 285
    :cond_4
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleRes:I

    #@96
    if-nez v5, :cond_5

    #@98
    iget-object v5, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleText:Ljava/lang/CharSequence;

    #@9a
    if-eqz v5, :cond_6

    #@9c
    .line 286
    :cond_5
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@9f
    const-string v5, "mBreadCrumbTitleRes=#"

    #@a1
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@a4
    .line 287
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleRes:I

    #@a6
    invoke-static {v5}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@a9
    move-result-object v5

    #@aa
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@ad
    .line 288
    const-string v5, " mBreadCrumbTitleText="

    #@af
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@b2
    .line 289
    iget-object v5, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleText:Ljava/lang/CharSequence;

    #@b4
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@b7
    .line 291
    :cond_6
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleRes:I

    #@b9
    if-nez v5, :cond_7

    #@bb
    iget-object v5, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleText:Ljava/lang/CharSequence;

    #@bd
    if-eqz v5, :cond_8

    #@bf
    .line 292
    :cond_7
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@c2
    const-string v5, "mBreadCrumbShortTitleRes=#"

    #@c4
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@c7
    .line 293
    iget v5, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleRes:I

    #@c9
    invoke-static {v5}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@cc
    move-result-object v5

    #@cd
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@d0
    .line 294
    const-string v5, " mBreadCrumbShortTitleText="

    #@d2
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@d5
    .line 295
    iget-object v5, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleText:Ljava/lang/CharSequence;

    #@d7
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@da
    .line 299
    :cond_8
    iget-object v5, p0, Landroid/support/v4/app/BackStackRecord;->mHead:Landroid/support/v4/app/BackStackRecord$Op;

    #@dc
    if-eqz v5, :cond_10

    #@de
    .line 300
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@e1
    const-string v5, "Operations:"

    #@e3
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@e6
    .line 301
    new-instance v5, Ljava/lang/StringBuilder;

    #@e8
    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    #@eb
    invoke-virtual {v5, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@ee
    move-result-object v5

    #@ef
    const-string v6, "    "

    #@f1
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@f4
    move-result-object v5

    #@f5
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@f8
    move-result-object v2

    #@f9
    .line 302
    .local v2, "innerPrefix":Ljava/lang/String;
    iget-object v4, p0, Landroid/support/v4/app/BackStackRecord;->mHead:Landroid/support/v4/app/BackStackRecord$Op;

    #@fb
    .line 303
    .local v4, "op":Landroid/support/v4/app/BackStackRecord$Op;
    const/4 v3, 0x0

    #@fc
    .line 304
    .local v3, "num":I
    :goto_0
    if-eqz v4, :cond_10

    #@fe
    .line 306
    iget v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@100
    packed-switch v5, :pswitch_data_0

    #@103
    .line 315
    new-instance v5, Ljava/lang/StringBuilder;

    #@105
    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    #@108
    const-string v6, "cmd="

    #@10a
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@10d
    move-result-object v5

    #@10e
    iget v6, v4, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@110
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@113
    move-result-object v5

    #@114
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@117
    move-result-object v0

    #@118
    .line 317
    .local v0, "cmdStr":Ljava/lang/String;
    :goto_1
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@11b
    const-string v5, "  Op #"

    #@11d
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@120
    invoke-virtual {p2, v3}, Ljava/io/PrintWriter;->print(I)V

    #@123
    .line 318
    const-string v5, ": "

    #@125
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@128
    invoke-virtual {p2, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@12b
    .line 319
    const-string v5, " "

    #@12d
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@130
    iget-object v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@132
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@135
    .line 320
    if-eqz p3, :cond_c

    #@137
    .line 321
    iget v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->enterAnim:I

    #@139
    if-nez v5, :cond_9

    #@13b
    iget v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->exitAnim:I

    #@13d
    if-eqz v5, :cond_a

    #@13f
    .line 322
    :cond_9
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@142
    const-string v5, "enterAnim=#"

    #@144
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@147
    .line 323
    iget v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->enterAnim:I

    #@149
    invoke-static {v5}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@14c
    move-result-object v5

    #@14d
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@150
    .line 324
    const-string v5, " exitAnim=#"

    #@152
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@155
    .line 325
    iget v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->exitAnim:I

    #@157
    invoke-static {v5}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@15a
    move-result-object v5

    #@15b
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@15e
    .line 327
    :cond_a
    iget v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->popEnterAnim:I

    #@160
    if-nez v5, :cond_b

    #@162
    iget v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->popExitAnim:I

    #@164
    if-eqz v5, :cond_c

    #@166
    .line 328
    :cond_b
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@169
    const-string v5, "popEnterAnim=#"

    #@16b
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@16e
    .line 329
    iget v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->popEnterAnim:I

    #@170
    invoke-static {v5}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@173
    move-result-object v5

    #@174
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@177
    .line 330
    const-string v5, " popExitAnim=#"

    #@179
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@17c
    .line 331
    iget v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->popExitAnim:I

    #@17e
    invoke-static {v5}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@181
    move-result-object v5

    #@182
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@185
    .line 334
    :cond_c
    iget-object v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@187
    if-eqz v5, :cond_f

    #@189
    iget-object v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@18b
    invoke-virtual {v5}, Ljava/util/ArrayList;->size()I

    #@18e
    move-result v5

    #@18f
    if-lez v5, :cond_f

    #@191
    .line 335
    const/4 v1, 0x0

    #@192
    .local v1, "i":I
    :goto_2
    iget-object v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@194
    invoke-virtual {v5}, Ljava/util/ArrayList;->size()I

    #@197
    move-result v5

    #@198
    if-ge v1, v5, :cond_f

    #@19a
    .line 336
    invoke-virtual {p2, v2}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@19d
    .line 337
    iget-object v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@19f
    invoke-virtual {v5}, Ljava/util/ArrayList;->size()I

    #@1a2
    move-result v5

    #@1a3
    const/4 v6, 0x1

    #@1a4
    if-ne v5, v6, :cond_d

    #@1a6
    .line 338
    const-string v5, "Removed: "

    #@1a8
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1ab
    .line 346
    :goto_3
    iget-object v5, v4, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@1ad
    invoke-virtual {v5, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@1b0
    move-result-object v5

    #@1b1
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@1b4
    .line 335
    add-int/lit8 v1, v1, 0x1

    #@1b6
    goto :goto_2

    #@1b7
    .line 307
    .end local v0    # "cmdStr":Ljava/lang/String;
    .end local v1    # "i":I
    :pswitch_0
    const-string v0, "NULL"

    #@1b9
    .restart local v0    # "cmdStr":Ljava/lang/String;
    goto/16 :goto_1

    #@1bb
    .line 308
    .end local v0    # "cmdStr":Ljava/lang/String;
    :pswitch_1
    const-string v0, "ADD"

    #@1bd
    .restart local v0    # "cmdStr":Ljava/lang/String;
    goto/16 :goto_1

    #@1bf
    .line 309
    .end local v0    # "cmdStr":Ljava/lang/String;
    :pswitch_2
    const-string v0, "REPLACE"

    #@1c1
    .restart local v0    # "cmdStr":Ljava/lang/String;
    goto/16 :goto_1

    #@1c3
    .line 310
    .end local v0    # "cmdStr":Ljava/lang/String;
    :pswitch_3
    const-string v0, "REMOVE"

    #@1c5
    .restart local v0    # "cmdStr":Ljava/lang/String;
    goto/16 :goto_1

    #@1c7
    .line 311
    .end local v0    # "cmdStr":Ljava/lang/String;
    :pswitch_4
    const-string v0, "HIDE"

    #@1c9
    .restart local v0    # "cmdStr":Ljava/lang/String;
    goto/16 :goto_1

    #@1cb
    .line 312
    .end local v0    # "cmdStr":Ljava/lang/String;
    :pswitch_5
    const-string v0, "SHOW"

    #@1cd
    .restart local v0    # "cmdStr":Ljava/lang/String;
    goto/16 :goto_1

    #@1cf
    .line 313
    .end local v0    # "cmdStr":Ljava/lang/String;
    :pswitch_6
    const-string v0, "DETACH"

    #@1d1
    .restart local v0    # "cmdStr":Ljava/lang/String;
    goto/16 :goto_1

    #@1d3
    .line 314
    .end local v0    # "cmdStr":Ljava/lang/String;
    :pswitch_7
    const-string v0, "ATTACH"

    #@1d5
    .restart local v0    # "cmdStr":Ljava/lang/String;
    goto/16 :goto_1

    #@1d7
    .line 340
    .restart local v1    # "i":I
    :cond_d
    if-nez v1, :cond_e

    #@1d9
    .line 341
    const-string v5, "Removed:"

    #@1db
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@1de
    .line 343
    :cond_e
    invoke-virtual {p2, v2}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1e1
    const-string v5, "  #"

    #@1e3
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1e6
    invoke-virtual {p2, v1}, Ljava/io/PrintWriter;->print(I)V

    #@1e9
    .line 344
    const-string v5, ": "

    #@1eb
    invoke-virtual {p2, v5}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1ee
    goto :goto_3

    #@1ef
    .line 349
    .end local v1    # "i":I
    :cond_f
    iget-object v4, v4, Landroid/support/v4/app/BackStackRecord$Op;->next:Landroid/support/v4/app/BackStackRecord$Op;

    #@1f1
    .line 350
    add-int/lit8 v3, v3, 0x1

    #@1f3
    .line 351
    goto/16 :goto_0

    #@1f5
    .line 353
    .end local v0    # "cmdStr":Ljava/lang/String;
    .end local v2    # "innerPrefix":Ljava/lang/String;
    .end local v3    # "num":I
    .end local v4    # "op":Landroid/support/v4/app/BackStackRecord$Op;
    :cond_10
    return-void

    #@1f6
    .line 306
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
        :pswitch_4
        :pswitch_5
        :pswitch_6
        :pswitch_7
    .end packed-switch
.end method

.method public getBreadCrumbShortTitle()Ljava/lang/CharSequence;
    .locals 2

    #@0
    .prologue
    .line 379
    iget v0, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleRes:I

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 380
    iget-object v0, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@6
    iget-object v0, v0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@8
    iget v1, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleRes:I

    #@a
    invoke-virtual {v0, v1}, Landroid/support/v4/app/FragmentActivity;->getText(I)Ljava/lang/CharSequence;

    #@d
    move-result-object v0

    #@e
    .line 382
    :goto_0
    return-object v0

    #@f
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleText:Ljava/lang/CharSequence;

    #@11
    goto :goto_0
.end method

.method public getBreadCrumbShortTitleRes()I
    .locals 1

    #@0
    .prologue
    .line 368
    iget v0, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleRes:I

    #@2
    return v0
.end method

.method public getBreadCrumbTitle()Ljava/lang/CharSequence;
    .locals 2

    #@0
    .prologue
    .line 372
    iget v0, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleRes:I

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 373
    iget-object v0, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@6
    iget-object v0, v0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@8
    iget v1, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleRes:I

    #@a
    invoke-virtual {v0, v1}, Landroid/support/v4/app/FragmentActivity;->getText(I)Ljava/lang/CharSequence;

    #@d
    move-result-object v0

    #@e
    .line 375
    :goto_0
    return-object v0

    #@f
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleText:Ljava/lang/CharSequence;

    #@11
    goto :goto_0
.end method

.method public getBreadCrumbTitleRes()I
    .locals 1

    #@0
    .prologue
    .line 364
    iget v0, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleRes:I

    #@2
    return v0
.end method

.method public getId()I
    .locals 1

    #@0
    .prologue
    .line 360
    iget v0, p0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@2
    return v0
.end method

.method public getName()Ljava/lang/String;
    .locals 1

    #@0
    .prologue
    .line 970
    iget-object v0, p0, Landroid/support/v4/app/BackStackRecord;->mName:Ljava/lang/String;

    #@2
    return-object v0
.end method

.method public getTransition()I
    .locals 1

    #@0
    .prologue
    .line 974
    iget v0, p0, Landroid/support/v4/app/BackStackRecord;->mTransition:I

    #@2
    return v0
.end method

.method public getTransitionStyle()I
    .locals 1

    #@0
    .prologue
    .line 978
    iget v0, p0, Landroid/support/v4/app/BackStackRecord;->mTransitionStyle:I

    #@2
    return v0
.end method

.method public hide(Landroid/support/v4/app/Fragment;)Landroid/support/v4/app/FragmentTransaction;
    .locals 2
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 465
    new-instance v0, Landroid/support/v4/app/BackStackRecord$Op;

    #@2
    invoke-direct {v0}, Landroid/support/v4/app/BackStackRecord$Op;-><init>()V

    #@5
    .line 466
    .local v0, "op":Landroid/support/v4/app/BackStackRecord$Op;
    const/4 v1, 0x4

    #@6
    iput v1, v0, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@8
    .line 467
    iput-object p1, v0, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@a
    .line 468
    invoke-virtual {p0, v0}, Landroid/support/v4/app/BackStackRecord;->addOp(Landroid/support/v4/app/BackStackRecord$Op;)V

    #@d
    .line 470
    return-object p0
.end method

.method public isAddToBackStackAllowed()Z
    .locals 1

    #@0
    .prologue
    .line 553
    iget-boolean v0, p0, Landroid/support/v4/app/BackStackRecord;->mAllowAddToBackStack:Z

    #@2
    return v0
.end method

.method public isEmpty()Z
    .locals 1

    #@0
    .prologue
    .line 982
    iget v0, p0, Landroid/support/v4/app/BackStackRecord;->mNumOp:I

    #@2
    if-nez v0, :cond_0

    #@4
    const/4 v0, 0x1

    #@5
    :goto_0
    return v0

    #@6
    :cond_0
    const/4 v0, 0x0

    #@7
    goto :goto_0
.end method

.method public popFromBackStack(ZLandroid/support/v4/app/BackStackRecord$TransitionState;Landroid/util/SparseArray;Landroid/util/SparseArray;)Landroid/support/v4/app/BackStackRecord$TransitionState;
    .locals 17
    .param p1, "doStateMove"    # Z
    .param p2, "state"    # Landroid/support/v4/app/BackStackRecord$TransitionState;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(Z",
            "Landroid/support/v4/app/BackStackRecord$TransitionState;",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;)",
            "Landroid/support/v4/app/BackStackRecord$TransitionState;"
        }
    .end annotation

    #@0
    .prologue
    .line 874
    .local p3, "firstOutFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    .local p4, "lastInFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    sget-boolean v13, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@2
    if-eqz v13, :cond_0

    #@4
    .line 875
    const-string v13, "FragmentManager"

    #@6
    new-instance v14, Ljava/lang/StringBuilder;

    #@8
    invoke-direct {v14}, Ljava/lang/StringBuilder;-><init>()V

    #@b
    const-string v15, "popFromBackStack: "

    #@d
    invoke-virtual {v14, v15}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@10
    move-result-object v14

    #@11
    move-object/from16 v0, p0

    #@13
    invoke-virtual {v14, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@16
    move-result-object v14

    #@17
    invoke-virtual {v14}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1a
    move-result-object v14

    #@1b
    invoke-static {v13, v14}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@1e
    .line 876
    new-instance v5, Landroid/support/v4/util/LogWriter;

    #@20
    const-string v13, "FragmentManager"

    #@22
    invoke-direct {v5, v13}, Landroid/support/v4/util/LogWriter;-><init>(Ljava/lang/String;)V

    #@25
    .line 877
    .local v5, "logw":Landroid/support/v4/util/LogWriter;
    new-instance v10, Ljava/io/PrintWriter;

    #@27
    invoke-direct {v10, v5}, Ljava/io/PrintWriter;-><init>(Ljava/io/Writer;)V

    #@2a
    .line 878
    .local v10, "pw":Ljava/io/PrintWriter;
    const-string v13, "  "

    #@2c
    const/4 v14, 0x0

    #@2d
    const/4 v15, 0x0

    #@2e
    move-object/from16 v0, p0

    #@30
    invoke-virtual {v0, v13, v14, v10, v15}, Landroid/support/v4/app/BackStackRecord;->dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V

    #@33
    .line 881
    .end local v5    # "logw":Landroid/support/v4/util/LogWriter;
    .end local v10    # "pw":Ljava/io/PrintWriter;
    :cond_0
    if-nez p2, :cond_3

    #@35
    .line 882
    invoke-virtual/range {p3 .. p3}, Landroid/util/SparseArray;->size()I

    #@38
    move-result v13

    #@39
    if-nez v13, :cond_1

    #@3b
    invoke-virtual/range {p4 .. p4}, Landroid/util/SparseArray;->size()I

    #@3e
    move-result v13

    #@3f
    if-eqz v13, :cond_2

    #@41
    .line 883
    :cond_1
    const/4 v13, 0x1

    #@42
    move-object/from16 v0, p0

    #@44
    move-object/from16 v1, p3

    #@46
    move-object/from16 v2, p4

    #@48
    invoke-direct {v0, v1, v2, v13}, Landroid/support/v4/app/BackStackRecord;->beginTransition(Landroid/util/SparseArray;Landroid/util/SparseArray;Z)Landroid/support/v4/app/BackStackRecord$TransitionState;

    #@4b
    move-result-object p2

    #@4c
    .line 889
    :cond_2
    :goto_0
    const/4 v13, -0x1

    #@4d
    move-object/from16 v0, p0

    #@4f
    invoke-virtual {v0, v13}, Landroid/support/v4/app/BackStackRecord;->bumpBackStackNesting(I)V

    #@52
    .line 891
    if-eqz p2, :cond_4

    #@54
    const/4 v12, 0x0

    #@55
    .line 892
    .local v12, "transitionStyle":I
    :goto_1
    if-eqz p2, :cond_5

    #@57
    const/4 v11, 0x0

    #@58
    .line 893
    .local v11, "transition":I
    :goto_2
    move-object/from16 v0, p0

    #@5a
    iget-object v7, v0, Landroid/support/v4/app/BackStackRecord;->mTail:Landroid/support/v4/app/BackStackRecord$Op;

    #@5c
    .line 894
    .local v7, "op":Landroid/support/v4/app/BackStackRecord$Op;
    :goto_3
    if-eqz v7, :cond_a

    #@5e
    .line 895
    if-eqz p2, :cond_6

    #@60
    const/4 v8, 0x0

    #@61
    .line 896
    .local v8, "popEnterAnim":I
    :goto_4
    if-eqz p2, :cond_7

    #@63
    const/4 v9, 0x0

    #@64
    .line 897
    .local v9, "popExitAnim":I
    :goto_5
    iget v13, v7, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@66
    packed-switch v13, :pswitch_data_0

    #@69
    .line 949
    new-instance v13, Ljava/lang/IllegalArgumentException;

    #@6b
    new-instance v14, Ljava/lang/StringBuilder;

    #@6d
    invoke-direct {v14}, Ljava/lang/StringBuilder;-><init>()V

    #@70
    const-string v15, "Unknown cmd: "

    #@72
    invoke-virtual {v14, v15}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@75
    move-result-object v14

    #@76
    iget v15, v7, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@78
    invoke-virtual {v14, v15}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@7b
    move-result-object v14

    #@7c
    invoke-virtual {v14}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@7f
    move-result-object v14

    #@80
    invoke-direct {v13, v14}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@83
    throw v13

    #@84
    .line 885
    .end local v7    # "op":Landroid/support/v4/app/BackStackRecord$Op;
    .end local v8    # "popEnterAnim":I
    .end local v9    # "popExitAnim":I
    .end local v11    # "transition":I
    .end local v12    # "transitionStyle":I
    :cond_3
    if-nez p1, :cond_2

    #@86
    .line 886
    move-object/from16 v0, p0

    #@88
    iget-object v13, v0, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@8a
    move-object/from16 v0, p0

    #@8c
    iget-object v14, v0, Landroid/support/v4/app/BackStackRecord;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@8e
    move-object/from16 v0, p2

    #@90
    invoke-static {v0, v13, v14}, Landroid/support/v4/app/BackStackRecord;->setNameOverrides(Landroid/support/v4/app/BackStackRecord$TransitionState;Ljava/util/ArrayList;Ljava/util/ArrayList;)V

    #@93
    goto :goto_0

    #@94
    .line 891
    :cond_4
    move-object/from16 v0, p0

    #@96
    iget v12, v0, Landroid/support/v4/app/BackStackRecord;->mTransitionStyle:I

    #@98
    goto :goto_1

    #@99
    .line 892
    .restart local v12    # "transitionStyle":I
    :cond_5
    move-object/from16 v0, p0

    #@9b
    iget v11, v0, Landroid/support/v4/app/BackStackRecord;->mTransition:I

    #@9d
    goto :goto_2

    #@9e
    .line 895
    .restart local v7    # "op":Landroid/support/v4/app/BackStackRecord$Op;
    .restart local v11    # "transition":I
    :cond_6
    iget v8, v7, Landroid/support/v4/app/BackStackRecord$Op;->popEnterAnim:I

    #@a0
    goto :goto_4

    #@a1
    .line 896
    .restart local v8    # "popEnterAnim":I
    :cond_7
    iget v9, v7, Landroid/support/v4/app/BackStackRecord$Op;->popExitAnim:I

    #@a3
    goto :goto_5

    #@a4
    .line 899
    .restart local v9    # "popExitAnim":I
    :pswitch_0
    iget-object v3, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@a6
    .line 900
    .local v3, "f":Landroid/support/v4/app/Fragment;
    iput v9, v3, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@a8
    .line 901
    move-object/from16 v0, p0

    #@aa
    iget-object v13, v0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@ac
    invoke-static {v11}, Landroid/support/v4/app/FragmentManagerImpl;->reverseTransit(I)I

    #@af
    move-result v14

    #@b0
    invoke-virtual {v13, v3, v14, v12}, Landroid/support/v4/app/FragmentManagerImpl;->removeFragment(Landroid/support/v4/app/Fragment;II)V

    #@b3
    .line 953
    :cond_8
    :goto_6
    iget-object v7, v7, Landroid/support/v4/app/BackStackRecord$Op;->prev:Landroid/support/v4/app/BackStackRecord$Op;

    #@b5
    .line 954
    goto :goto_3

    #@b6
    .line 905
    .end local v3    # "f":Landroid/support/v4/app/Fragment;
    :pswitch_1
    iget-object v3, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@b8
    .line 906
    .restart local v3    # "f":Landroid/support/v4/app/Fragment;
    if-eqz v3, :cond_9

    #@ba
    .line 907
    iput v9, v3, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@bc
    .line 908
    move-object/from16 v0, p0

    #@be
    iget-object v13, v0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@c0
    invoke-static {v11}, Landroid/support/v4/app/FragmentManagerImpl;->reverseTransit(I)I

    #@c3
    move-result v14

    #@c4
    invoke-virtual {v13, v3, v14, v12}, Landroid/support/v4/app/FragmentManagerImpl;->removeFragment(Landroid/support/v4/app/Fragment;II)V

    #@c7
    .line 911
    :cond_9
    iget-object v13, v7, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@c9
    if-eqz v13, :cond_8

    #@cb
    .line 912
    const/4 v4, 0x0

    #@cc
    .local v4, "i":I
    :goto_7
    iget-object v13, v7, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@ce
    invoke-virtual {v13}, Ljava/util/ArrayList;->size()I

    #@d1
    move-result v13

    #@d2
    if-ge v4, v13, :cond_8

    #@d4
    .line 913
    iget-object v13, v7, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@d6
    invoke-virtual {v13, v4}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@d9
    move-result-object v6

    #@da
    check-cast v6, Landroid/support/v4/app/Fragment;

    #@dc
    .line 914
    .local v6, "old":Landroid/support/v4/app/Fragment;
    iput v8, v6, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@de
    .line 915
    move-object/from16 v0, p0

    #@e0
    iget-object v13, v0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@e2
    const/4 v14, 0x0

    #@e3
    invoke-virtual {v13, v6, v14}, Landroid/support/v4/app/FragmentManagerImpl;->addFragment(Landroid/support/v4/app/Fragment;Z)V

    #@e6
    .line 912
    add-int/lit8 v4, v4, 0x1

    #@e8
    goto :goto_7

    #@e9
    .line 920
    .end local v3    # "f":Landroid/support/v4/app/Fragment;
    .end local v4    # "i":I
    .end local v6    # "old":Landroid/support/v4/app/Fragment;
    :pswitch_2
    iget-object v3, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@eb
    .line 921
    .restart local v3    # "f":Landroid/support/v4/app/Fragment;
    iput v8, v3, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@ed
    .line 922
    move-object/from16 v0, p0

    #@ef
    iget-object v13, v0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@f1
    const/4 v14, 0x0

    #@f2
    invoke-virtual {v13, v3, v14}, Landroid/support/v4/app/FragmentManagerImpl;->addFragment(Landroid/support/v4/app/Fragment;Z)V

    #@f5
    goto :goto_6

    #@f6
    .line 925
    .end local v3    # "f":Landroid/support/v4/app/Fragment;
    :pswitch_3
    iget-object v3, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@f8
    .line 926
    .restart local v3    # "f":Landroid/support/v4/app/Fragment;
    iput v8, v3, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@fa
    .line 927
    move-object/from16 v0, p0

    #@fc
    iget-object v13, v0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@fe
    invoke-static {v11}, Landroid/support/v4/app/FragmentManagerImpl;->reverseTransit(I)I

    #@101
    move-result v14

    #@102
    invoke-virtual {v13, v3, v14, v12}, Landroid/support/v4/app/FragmentManagerImpl;->showFragment(Landroid/support/v4/app/Fragment;II)V

    #@105
    goto :goto_6

    #@106
    .line 931
    .end local v3    # "f":Landroid/support/v4/app/Fragment;
    :pswitch_4
    iget-object v3, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@108
    .line 932
    .restart local v3    # "f":Landroid/support/v4/app/Fragment;
    iput v9, v3, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@10a
    .line 933
    move-object/from16 v0, p0

    #@10c
    iget-object v13, v0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@10e
    invoke-static {v11}, Landroid/support/v4/app/FragmentManagerImpl;->reverseTransit(I)I

    #@111
    move-result v14

    #@112
    invoke-virtual {v13, v3, v14, v12}, Landroid/support/v4/app/FragmentManagerImpl;->hideFragment(Landroid/support/v4/app/Fragment;II)V

    #@115
    goto :goto_6

    #@116
    .line 937
    .end local v3    # "f":Landroid/support/v4/app/Fragment;
    :pswitch_5
    iget-object v3, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@118
    .line 938
    .restart local v3    # "f":Landroid/support/v4/app/Fragment;
    iput v8, v3, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@11a
    .line 939
    move-object/from16 v0, p0

    #@11c
    iget-object v13, v0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@11e
    invoke-static {v11}, Landroid/support/v4/app/FragmentManagerImpl;->reverseTransit(I)I

    #@121
    move-result v14

    #@122
    invoke-virtual {v13, v3, v14, v12}, Landroid/support/v4/app/FragmentManagerImpl;->attachFragment(Landroid/support/v4/app/Fragment;II)V

    #@125
    goto :goto_6

    #@126
    .line 943
    .end local v3    # "f":Landroid/support/v4/app/Fragment;
    :pswitch_6
    iget-object v3, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@128
    .line 944
    .restart local v3    # "f":Landroid/support/v4/app/Fragment;
    iput v8, v3, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@12a
    .line 945
    move-object/from16 v0, p0

    #@12c
    iget-object v13, v0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@12e
    invoke-static {v11}, Landroid/support/v4/app/FragmentManagerImpl;->reverseTransit(I)I

    #@131
    move-result v14

    #@132
    invoke-virtual {v13, v3, v14, v12}, Landroid/support/v4/app/FragmentManagerImpl;->detachFragment(Landroid/support/v4/app/Fragment;II)V

    #@135
    goto/16 :goto_6

    #@137
    .line 956
    .end local v3    # "f":Landroid/support/v4/app/Fragment;
    .end local v8    # "popEnterAnim":I
    .end local v9    # "popExitAnim":I
    :cond_a
    if-eqz p1, :cond_b

    #@139
    .line 957
    move-object/from16 v0, p0

    #@13b
    iget-object v13, v0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@13d
    move-object/from16 v0, p0

    #@13f
    iget-object v14, v0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@141
    iget v14, v14, Landroid/support/v4/app/FragmentManagerImpl;->mCurState:I

    #@143
    invoke-static {v11}, Landroid/support/v4/app/FragmentManagerImpl;->reverseTransit(I)I

    #@146
    move-result v15

    #@147
    const/16 v16, 0x1

    #@149
    move/from16 v0, v16

    #@14b
    invoke-virtual {v13, v14, v15, v12, v0}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(IIIZ)V

    #@14e
    .line 959
    const/16 p2, 0x0

    #@150
    .line 962
    :cond_b
    move-object/from16 v0, p0

    #@152
    iget v13, v0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@154
    if-ltz v13, :cond_c

    #@156
    .line 963
    move-object/from16 v0, p0

    #@158
    iget-object v13, v0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@15a
    move-object/from16 v0, p0

    #@15c
    iget v14, v0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@15e
    invoke-virtual {v13, v14}, Landroid/support/v4/app/FragmentManagerImpl;->freeBackStackIndex(I)V

    #@161
    .line 964
    const/4 v13, -0x1

    #@162
    move-object/from16 v0, p0

    #@164
    iput v13, v0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@166
    .line 966
    :cond_c
    return-object p2

    #@167
    .line 897
    nop

    #@168
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
        :pswitch_4
        :pswitch_5
        :pswitch_6
    .end packed-switch
.end method

.method public remove(Landroid/support/v4/app/Fragment;)Landroid/support/v4/app/FragmentTransaction;
    .locals 2
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 456
    new-instance v0, Landroid/support/v4/app/BackStackRecord$Op;

    #@2
    invoke-direct {v0}, Landroid/support/v4/app/BackStackRecord$Op;-><init>()V

    #@5
    .line 457
    .local v0, "op":Landroid/support/v4/app/BackStackRecord$Op;
    const/4 v1, 0x3

    #@6
    iput v1, v0, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@8
    .line 458
    iput-object p1, v0, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@a
    .line 459
    invoke-virtual {p0, v0}, Landroid/support/v4/app/BackStackRecord;->addOp(Landroid/support/v4/app/BackStackRecord$Op;)V

    #@d
    .line 461
    return-object p0
.end method

.method public replace(ILandroid/support/v4/app/Fragment;)Landroid/support/v4/app/FragmentTransaction;
    .locals 1
    .param p1, "containerViewId"    # I
    .param p2, "fragment"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 443
    const/4 v0, 0x0

    #@1
    invoke-virtual {p0, p1, p2, v0}, Landroid/support/v4/app/BackStackRecord;->replace(ILandroid/support/v4/app/Fragment;Ljava/lang/String;)Landroid/support/v4/app/FragmentTransaction;

    #@4
    move-result-object v0

    #@5
    return-object v0
.end method

.method public replace(ILandroid/support/v4/app/Fragment;Ljava/lang/String;)Landroid/support/v4/app/FragmentTransaction;
    .locals 2
    .param p1, "containerViewId"    # I
    .param p2, "fragment"    # Landroid/support/v4/app/Fragment;
    .param p3, "tag"    # Ljava/lang/String;

    #@0
    .prologue
    .line 447
    if-nez p1, :cond_0

    #@2
    .line 448
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@4
    const-string v1, "Must use non-zero containerViewId"

    #@6
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@9
    throw v0

    #@a
    .line 451
    :cond_0
    const/4 v0, 0x2

    #@b
    invoke-direct {p0, p1, p2, p3, v0}, Landroid/support/v4/app/BackStackRecord;->doAddOp(ILandroid/support/v4/app/Fragment;Ljava/lang/String;I)V

    #@e
    .line 452
    return-object p0
.end method

.method public run()V
    .locals 14

    #@0
    .prologue
    .line 641
    sget-boolean v11, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@2
    if-eqz v11, :cond_0

    #@4
    const-string v11, "FragmentManager"

    #@6
    new-instance v12, Ljava/lang/StringBuilder;

    #@8
    invoke-direct {v12}, Ljava/lang/StringBuilder;-><init>()V

    #@b
    const-string v13, "Run: "

    #@d
    invoke-virtual {v12, v13}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@10
    move-result-object v12

    #@11
    invoke-virtual {v12, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@14
    move-result-object v12

    #@15
    invoke-virtual {v12}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@18
    move-result-object v12

    #@19
    invoke-static {v11, v12}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@1c
    .line 643
    :cond_0
    iget-boolean v11, p0, Landroid/support/v4/app/BackStackRecord;->mAddToBackStack:Z

    #@1e
    if-eqz v11, :cond_1

    #@20
    .line 644
    iget v11, p0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@22
    if-gez v11, :cond_1

    #@24
    .line 645
    new-instance v11, Ljava/lang/IllegalStateException;

    #@26
    const-string v12, "addToBackStack() called after commit()"

    #@28
    invoke-direct {v11, v12}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@2b
    throw v11

    #@2c
    .line 649
    :cond_1
    const/4 v11, 0x1

    #@2d
    invoke-virtual {p0, v11}, Landroid/support/v4/app/BackStackRecord;->bumpBackStackNesting(I)V

    #@30
    .line 651
    const/4 v8, 0x0

    #@31
    .line 652
    .local v8, "state":Landroid/support/v4/app/BackStackRecord$TransitionState;
    const/4 v3, 0x0

    #@32
    .line 653
    .local v3, "firstOutFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    const/4 v5, 0x0

    #@33
    .line 654
    .local v5, "lastInFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    sget v11, Landroid/os/Build$VERSION;->SDK_INT:I

    #@35
    const/16 v12, 0x15

    #@37
    if-lt v11, v12, :cond_2

    #@39
    .line 655
    new-instance v3, Landroid/util/SparseArray;

    #@3b
    .end local v3    # "firstOutFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    invoke-direct {v3}, Landroid/util/SparseArray;-><init>()V

    #@3e
    .line 656
    .restart local v3    # "firstOutFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    new-instance v5, Landroid/util/SparseArray;

    #@40
    .end local v5    # "lastInFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    invoke-direct {v5}, Landroid/util/SparseArray;-><init>()V

    #@43
    .line 658
    .restart local v5    # "lastInFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    invoke-direct {p0, v3, v5}, Landroid/support/v4/app/BackStackRecord;->calculateFragments(Landroid/util/SparseArray;Landroid/util/SparseArray;)V

    #@46
    .line 660
    const/4 v11, 0x0

    #@47
    invoke-direct {p0, v3, v5, v11}, Landroid/support/v4/app/BackStackRecord;->beginTransition(Landroid/util/SparseArray;Landroid/util/SparseArray;Z)Landroid/support/v4/app/BackStackRecord$TransitionState;

    #@4a
    move-result-object v8

    #@4b
    .line 663
    :cond_2
    if-eqz v8, :cond_3

    #@4d
    const/4 v10, 0x0

    #@4e
    .line 664
    .local v10, "transitionStyle":I
    :goto_0
    if-eqz v8, :cond_4

    #@50
    const/4 v9, 0x0

    #@51
    .line 665
    .local v9, "transition":I
    :goto_1
    iget-object v7, p0, Landroid/support/v4/app/BackStackRecord;->mHead:Landroid/support/v4/app/BackStackRecord$Op;

    #@53
    .line 666
    .local v7, "op":Landroid/support/v4/app/BackStackRecord$Op;
    :goto_2
    if-eqz v7, :cond_f

    #@55
    .line 667
    if-eqz v8, :cond_5

    #@57
    const/4 v0, 0x0

    #@58
    .line 668
    .local v0, "enterAnim":I
    :goto_3
    if-eqz v8, :cond_6

    #@5a
    const/4 v1, 0x0

    #@5b
    .line 669
    .local v1, "exitAnim":I
    :goto_4
    iget v11, v7, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@5d
    packed-switch v11, :pswitch_data_0

    #@60
    .line 732
    new-instance v11, Ljava/lang/IllegalArgumentException;

    #@62
    new-instance v12, Ljava/lang/StringBuilder;

    #@64
    invoke-direct {v12}, Ljava/lang/StringBuilder;-><init>()V

    #@67
    const-string v13, "Unknown cmd: "

    #@69
    invoke-virtual {v12, v13}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@6c
    move-result-object v12

    #@6d
    iget v13, v7, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@6f
    invoke-virtual {v12, v13}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@72
    move-result-object v12

    #@73
    invoke-virtual {v12}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@76
    move-result-object v12

    #@77
    invoke-direct {v11, v12}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@7a
    throw v11

    #@7b
    .line 663
    .end local v0    # "enterAnim":I
    .end local v1    # "exitAnim":I
    .end local v7    # "op":Landroid/support/v4/app/BackStackRecord$Op;
    .end local v9    # "transition":I
    .end local v10    # "transitionStyle":I
    :cond_3
    iget v10, p0, Landroid/support/v4/app/BackStackRecord;->mTransitionStyle:I

    #@7d
    goto :goto_0

    #@7e
    .line 664
    .restart local v10    # "transitionStyle":I
    :cond_4
    iget v9, p0, Landroid/support/v4/app/BackStackRecord;->mTransition:I

    #@80
    goto :goto_1

    #@81
    .line 667
    .restart local v7    # "op":Landroid/support/v4/app/BackStackRecord$Op;
    .restart local v9    # "transition":I
    :cond_5
    iget v0, v7, Landroid/support/v4/app/BackStackRecord$Op;->enterAnim:I

    #@83
    goto :goto_3

    #@84
    .line 668
    .restart local v0    # "enterAnim":I
    :cond_6
    iget v1, v7, Landroid/support/v4/app/BackStackRecord$Op;->exitAnim:I

    #@86
    goto :goto_4

    #@87
    .line 671
    .restart local v1    # "exitAnim":I
    :pswitch_0
    iget-object v2, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@89
    .line 672
    .local v2, "f":Landroid/support/v4/app/Fragment;
    iput v0, v2, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@8b
    .line 673
    iget-object v11, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@8d
    const/4 v12, 0x0

    #@8e
    invoke-virtual {v11, v2, v12}, Landroid/support/v4/app/FragmentManagerImpl;->addFragment(Landroid/support/v4/app/Fragment;Z)V

    #@91
    .line 736
    :cond_7
    :goto_5
    iget-object v7, v7, Landroid/support/v4/app/BackStackRecord$Op;->next:Landroid/support/v4/app/BackStackRecord$Op;

    #@93
    .line 737
    goto :goto_2

    #@94
    .line 676
    .end local v2    # "f":Landroid/support/v4/app/Fragment;
    :pswitch_1
    iget-object v2, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@96
    .line 677
    .restart local v2    # "f":Landroid/support/v4/app/Fragment;
    iget-object v11, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@98
    iget-object v11, v11, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@9a
    if-eqz v11, :cond_e

    #@9c
    .line 678
    const/4 v4, 0x0

    #@9d
    .local v4, "i":I
    :goto_6
    iget-object v11, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@9f
    iget-object v11, v11, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@a1
    invoke-virtual {v11}, Ljava/util/ArrayList;->size()I

    #@a4
    move-result v11

    #@a5
    if-ge v4, v11, :cond_e

    #@a7
    .line 679
    iget-object v11, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@a9
    iget-object v11, v11, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@ab
    invoke-virtual {v11, v4}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@ae
    move-result-object v6

    #@af
    check-cast v6, Landroid/support/v4/app/Fragment;

    #@b1
    .line 680
    .local v6, "old":Landroid/support/v4/app/Fragment;
    sget-boolean v11, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@b3
    if-eqz v11, :cond_8

    #@b5
    const-string v11, "FragmentManager"

    #@b7
    new-instance v12, Ljava/lang/StringBuilder;

    #@b9
    invoke-direct {v12}, Ljava/lang/StringBuilder;-><init>()V

    #@bc
    const-string v13, "OP_REPLACE: adding="

    #@be
    invoke-virtual {v12, v13}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@c1
    move-result-object v12

    #@c2
    invoke-virtual {v12, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@c5
    move-result-object v12

    #@c6
    const-string v13, " old="

    #@c8
    invoke-virtual {v12, v13}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@cb
    move-result-object v12

    #@cc
    invoke-virtual {v12, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@cf
    move-result-object v12

    #@d0
    invoke-virtual {v12}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@d3
    move-result-object v12

    #@d4
    invoke-static {v11, v12}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@d7
    .line 682
    :cond_8
    if-eqz v2, :cond_9

    #@d9
    iget v11, v6, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@db
    iget v12, v2, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@dd
    if-ne v11, v12, :cond_a

    #@df
    .line 683
    :cond_9
    if-ne v6, v2, :cond_b

    #@e1
    .line 684
    const/4 v2, 0x0

    #@e2
    iput-object v2, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@e4
    .line 678
    :cond_a
    :goto_7
    add-int/lit8 v4, v4, 0x1

    #@e6
    goto :goto_6

    #@e7
    .line 686
    :cond_b
    iget-object v11, v7, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@e9
    if-nez v11, :cond_c

    #@eb
    .line 687
    new-instance v11, Ljava/util/ArrayList;

    #@ed
    invoke-direct {v11}, Ljava/util/ArrayList;-><init>()V

    #@f0
    iput-object v11, v7, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@f2
    .line 689
    :cond_c
    iget-object v11, v7, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@f4
    invoke-virtual {v11, v6}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@f7
    .line 690
    iput v1, v6, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@f9
    .line 691
    iget-boolean v11, p0, Landroid/support/v4/app/BackStackRecord;->mAddToBackStack:Z

    #@fb
    if-eqz v11, :cond_d

    #@fd
    .line 692
    iget v11, v6, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@ff
    add-int/lit8 v11, v11, 0x1

    #@101
    iput v11, v6, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@103
    .line 693
    sget-boolean v11, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@105
    if-eqz v11, :cond_d

    #@107
    const-string v11, "FragmentManager"

    #@109
    new-instance v12, Ljava/lang/StringBuilder;

    #@10b
    invoke-direct {v12}, Ljava/lang/StringBuilder;-><init>()V

    #@10e
    const-string v13, "Bump nesting of "

    #@110
    invoke-virtual {v12, v13}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@113
    move-result-object v12

    #@114
    invoke-virtual {v12, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@117
    move-result-object v12

    #@118
    const-string v13, " to "

    #@11a
    invoke-virtual {v12, v13}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@11d
    move-result-object v12

    #@11e
    iget v13, v6, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@120
    invoke-virtual {v12, v13}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@123
    move-result-object v12

    #@124
    invoke-virtual {v12}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@127
    move-result-object v12

    #@128
    invoke-static {v11, v12}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@12b
    .line 696
    :cond_d
    iget-object v11, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@12d
    invoke-virtual {v11, v6, v9, v10}, Landroid/support/v4/app/FragmentManagerImpl;->removeFragment(Landroid/support/v4/app/Fragment;II)V

    #@130
    goto :goto_7

    #@131
    .line 701
    .end local v4    # "i":I
    .end local v6    # "old":Landroid/support/v4/app/Fragment;
    :cond_e
    if-eqz v2, :cond_7

    #@133
    .line 702
    iput v0, v2, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@135
    .line 703
    iget-object v11, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@137
    const/4 v12, 0x0

    #@138
    invoke-virtual {v11, v2, v12}, Landroid/support/v4/app/FragmentManagerImpl;->addFragment(Landroid/support/v4/app/Fragment;Z)V

    #@13b
    goto/16 :goto_5

    #@13d
    .line 707
    .end local v2    # "f":Landroid/support/v4/app/Fragment;
    :pswitch_2
    iget-object v2, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@13f
    .line 708
    .restart local v2    # "f":Landroid/support/v4/app/Fragment;
    iput v1, v2, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@141
    .line 709
    iget-object v11, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@143
    invoke-virtual {v11, v2, v9, v10}, Landroid/support/v4/app/FragmentManagerImpl;->removeFragment(Landroid/support/v4/app/Fragment;II)V

    #@146
    goto/16 :goto_5

    #@148
    .line 712
    .end local v2    # "f":Landroid/support/v4/app/Fragment;
    :pswitch_3
    iget-object v2, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@14a
    .line 713
    .restart local v2    # "f":Landroid/support/v4/app/Fragment;
    iput v1, v2, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@14c
    .line 714
    iget-object v11, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@14e
    invoke-virtual {v11, v2, v9, v10}, Landroid/support/v4/app/FragmentManagerImpl;->hideFragment(Landroid/support/v4/app/Fragment;II)V

    #@151
    goto/16 :goto_5

    #@153
    .line 717
    .end local v2    # "f":Landroid/support/v4/app/Fragment;
    :pswitch_4
    iget-object v2, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@155
    .line 718
    .restart local v2    # "f":Landroid/support/v4/app/Fragment;
    iput v0, v2, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@157
    .line 719
    iget-object v11, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@159
    invoke-virtual {v11, v2, v9, v10}, Landroid/support/v4/app/FragmentManagerImpl;->showFragment(Landroid/support/v4/app/Fragment;II)V

    #@15c
    goto/16 :goto_5

    #@15e
    .line 722
    .end local v2    # "f":Landroid/support/v4/app/Fragment;
    :pswitch_5
    iget-object v2, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@160
    .line 723
    .restart local v2    # "f":Landroid/support/v4/app/Fragment;
    iput v1, v2, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@162
    .line 724
    iget-object v11, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@164
    invoke-virtual {v11, v2, v9, v10}, Landroid/support/v4/app/FragmentManagerImpl;->detachFragment(Landroid/support/v4/app/Fragment;II)V

    #@167
    goto/16 :goto_5

    #@169
    .line 727
    .end local v2    # "f":Landroid/support/v4/app/Fragment;
    :pswitch_6
    iget-object v2, v7, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@16b
    .line 728
    .restart local v2    # "f":Landroid/support/v4/app/Fragment;
    iput v0, v2, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@16d
    .line 729
    iget-object v11, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@16f
    invoke-virtual {v11, v2, v9, v10}, Landroid/support/v4/app/FragmentManagerImpl;->attachFragment(Landroid/support/v4/app/Fragment;II)V

    #@172
    goto/16 :goto_5

    #@174
    .line 739
    .end local v0    # "enterAnim":I
    .end local v1    # "exitAnim":I
    .end local v2    # "f":Landroid/support/v4/app/Fragment;
    :cond_f
    iget-object v11, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@176
    iget-object v12, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@178
    iget v12, v12, Landroid/support/v4/app/FragmentManagerImpl;->mCurState:I

    #@17a
    const/4 v13, 0x1

    #@17b
    invoke-virtual {v11, v12, v9, v10, v13}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(IIIZ)V

    #@17e
    .line 741
    iget-boolean v11, p0, Landroid/support/v4/app/BackStackRecord;->mAddToBackStack:Z

    #@180
    if-eqz v11, :cond_10

    #@182
    .line 742
    iget-object v11, p0, Landroid/support/v4/app/BackStackRecord;->mManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@184
    invoke-virtual {v11, p0}, Landroid/support/v4/app/FragmentManagerImpl;->addBackStackState(Landroid/support/v4/app/BackStackRecord;)V

    #@187
    .line 744
    :cond_10
    return-void

    #@188
    .line 669
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
        :pswitch_4
        :pswitch_5
        :pswitch_6
    .end packed-switch
.end method

.method public setBreadCrumbShortTitle(I)Landroid/support/v4/app/FragmentTransaction;
    .locals 1
    .param p1, "res"    # I

    #@0
    .prologue
    .line 578
    iput p1, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleRes:I

    #@2
    .line 579
    const/4 v0, 0x0

    #@3
    iput-object v0, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleText:Ljava/lang/CharSequence;

    #@5
    .line 580
    return-object p0
.end method

.method public setBreadCrumbShortTitle(Ljava/lang/CharSequence;)Landroid/support/v4/app/FragmentTransaction;
    .locals 1
    .param p1, "text"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 584
    const/4 v0, 0x0

    #@1
    iput v0, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleRes:I

    #@3
    .line 585
    iput-object p1, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleText:Ljava/lang/CharSequence;

    #@5
    .line 586
    return-object p0
.end method

.method public setBreadCrumbTitle(I)Landroid/support/v4/app/FragmentTransaction;
    .locals 1
    .param p1, "res"    # I

    #@0
    .prologue
    .line 566
    iput p1, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleRes:I

    #@2
    .line 567
    const/4 v0, 0x0

    #@3
    iput-object v0, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleText:Ljava/lang/CharSequence;

    #@5
    .line 568
    return-object p0
.end method

.method public setBreadCrumbTitle(Ljava/lang/CharSequence;)Landroid/support/v4/app/FragmentTransaction;
    .locals 1
    .param p1, "text"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 572
    const/4 v0, 0x0

    #@1
    iput v0, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleRes:I

    #@3
    .line 573
    iput-object p1, p0, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleText:Ljava/lang/CharSequence;

    #@5
    .line 574
    return-object p0
.end method

.method public setCustomAnimations(II)Landroid/support/v4/app/FragmentTransaction;
    .locals 1
    .param p1, "enter"    # I
    .param p2, "exit"    # I

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 501
    invoke-virtual {p0, p1, p2, v0, v0}, Landroid/support/v4/app/BackStackRecord;->setCustomAnimations(IIII)Landroid/support/v4/app/FragmentTransaction;

    #@4
    move-result-object v0

    #@5
    return-object v0
.end method

.method public setCustomAnimations(IIII)Landroid/support/v4/app/FragmentTransaction;
    .locals 0
    .param p1, "enter"    # I
    .param p2, "exit"    # I
    .param p3, "popEnter"    # I
    .param p4, "popExit"    # I

    #@0
    .prologue
    .line 506
    iput p1, p0, Landroid/support/v4/app/BackStackRecord;->mEnterAnim:I

    #@2
    .line 507
    iput p2, p0, Landroid/support/v4/app/BackStackRecord;->mExitAnim:I

    #@4
    .line 508
    iput p3, p0, Landroid/support/v4/app/BackStackRecord;->mPopEnterAnim:I

    #@6
    .line 509
    iput p4, p0, Landroid/support/v4/app/BackStackRecord;->mPopExitAnim:I

    #@8
    .line 510
    return-object p0
.end method

.method public setTransition(I)Landroid/support/v4/app/FragmentTransaction;
    .locals 0
    .param p1, "transition"    # I

    #@0
    .prologue
    .line 514
    iput p1, p0, Landroid/support/v4/app/BackStackRecord;->mTransition:I

    #@2
    .line 515
    return-object p0
.end method

.method public setTransitionStyle(I)Landroid/support/v4/app/FragmentTransaction;
    .locals 0
    .param p1, "styleRes"    # I

    #@0
    .prologue
    .line 538
    iput p1, p0, Landroid/support/v4/app/BackStackRecord;->mTransitionStyle:I

    #@2
    .line 539
    return-object p0
.end method

.method public show(Landroid/support/v4/app/Fragment;)Landroid/support/v4/app/FragmentTransaction;
    .locals 2
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 474
    new-instance v0, Landroid/support/v4/app/BackStackRecord$Op;

    #@2
    invoke-direct {v0}, Landroid/support/v4/app/BackStackRecord$Op;-><init>()V

    #@5
    .line 475
    .local v0, "op":Landroid/support/v4/app/BackStackRecord$Op;
    const/4 v1, 0x5

    #@6
    iput v1, v0, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@8
    .line 476
    iput-object p1, v0, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@a
    .line 477
    invoke-virtual {p0, v0}, Landroid/support/v4/app/BackStackRecord;->addOp(Landroid/support/v4/app/BackStackRecord$Op;)V

    #@d
    .line 479
    return-object p0
.end method

.method public toString()Ljava/lang/String;
    .locals 2

    #@0
    .prologue
    .line 243
    new-instance v0, Ljava/lang/StringBuilder;

    #@2
    const/16 v1, 0x80

    #@4
    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(I)V

    #@7
    .line 244
    .local v0, "sb":Ljava/lang/StringBuilder;
    const-string v1, "BackStackEntry{"

    #@9
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@c
    .line 245
    invoke-static {p0}, Ljava/lang/System;->identityHashCode(Ljava/lang/Object;)I

    #@f
    move-result v1

    #@10
    invoke-static {v1}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@13
    move-result-object v1

    #@14
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@17
    .line 246
    iget v1, p0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@19
    if-ltz v1, :cond_0

    #@1b
    .line 247
    const-string v1, " #"

    #@1d
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@20
    .line 248
    iget v1, p0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@22
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@25
    .line 250
    :cond_0
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mName:Ljava/lang/String;

    #@27
    if-eqz v1, :cond_1

    #@29
    .line 251
    const-string v1, " "

    #@2b
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2e
    .line 252
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord;->mName:Ljava/lang/String;

    #@30
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@33
    .line 254
    :cond_1
    const-string v1, "}"

    #@35
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@38
    .line 255
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@3b
    move-result-object v1

    #@3c
    return-object v1
.end method
