.class public Landroid/support/v4/app/Fragment;
.super Ljava/lang/Object;
.source "Fragment.java"

# interfaces
.implements Landroid/content/ComponentCallbacks;
.implements Landroid/view/View$OnCreateContextMenuListener;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/Fragment$InstantiationException;,
        Landroid/support/v4/app/Fragment$SavedState;
    }
.end annotation


# static fields
.field static final ACTIVITY_CREATED:I = 0x2

.field static final CREATED:I = 0x1

.field static final INITIALIZING:I = 0x0

.field static final RESUMED:I = 0x5

.field static final STARTED:I = 0x4

.field static final STOPPED:I = 0x3

.field static final USE_DEFAULT_TRANSITION:Ljava/lang/Object;

.field private static final sClassMap:Landroid/support/v4/util/SimpleArrayMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/support/v4/util/SimpleArrayMap",
            "<",
            "Ljava/lang/String;",
            "Ljava/lang/Class",
            "<*>;>;"
        }
    .end annotation
.end field


# instance fields
.field mActivity:Landroid/support/v4/app/FragmentActivity;

.field mAdded:Z

.field mAllowEnterTransitionOverlap:Ljava/lang/Boolean;

.field mAllowReturnTransitionOverlap:Ljava/lang/Boolean;

.field mAnimatingAway:Landroid/view/View;

.field mArguments:Landroid/os/Bundle;

.field mBackStackNesting:I

.field mCalled:Z

.field mCheckedForLoaderManager:Z

.field mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

.field mContainer:Landroid/view/ViewGroup;

.field mContainerId:I

.field mDeferStart:Z

.field mDetached:Z

.field mEnterTransition:Ljava/lang/Object;

.field mEnterTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

.field mExitTransition:Ljava/lang/Object;

.field mExitTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

.field mFragmentId:I

.field mFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

.field mFromLayout:Z

.field mHasMenu:Z

.field mHidden:Z

.field mInLayout:Z

.field mIndex:I

.field mInnerView:Landroid/view/View;

.field mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

.field mLoadersStarted:Z

.field mMenuVisible:Z

.field mNextAnim:I

.field mParentFragment:Landroid/support/v4/app/Fragment;

.field mReenterTransition:Ljava/lang/Object;

.field mRemoving:Z

.field mRestored:Z

.field mResumed:Z

.field mRetainInstance:Z

.field mRetaining:Z

.field mReturnTransition:Ljava/lang/Object;

.field mSavedFragmentState:Landroid/os/Bundle;

.field mSavedViewState:Landroid/util/SparseArray;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/util/SparseArray",
            "<",
            "Landroid/os/Parcelable;",
            ">;"
        }
    .end annotation
.end field

.field mSharedElementEnterTransition:Ljava/lang/Object;

.field mSharedElementReturnTransition:Ljava/lang/Object;

.field mState:I

.field mStateAfterAnimating:I

.field mTag:Ljava/lang/String;

.field mTarget:Landroid/support/v4/app/Fragment;

.field mTargetIndex:I

.field mTargetRequestCode:I

.field mUserVisibleHint:Z

.field mView:Landroid/view/View;

.field mWho:Ljava/lang/String;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 168
    new-instance v0, Landroid/support/v4/util/SimpleArrayMap;

    #@2
    invoke-direct {v0}, Landroid/support/v4/util/SimpleArrayMap;-><init>()V

    #@5
    sput-object v0, Landroid/support/v4/app/Fragment;->sClassMap:Landroid/support/v4/util/SimpleArrayMap;

    #@7
    .line 171
    new-instance v0, Ljava/lang/Object;

    #@9
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    #@c
    sput-object v0, Landroid/support/v4/app/Fragment;->USE_DEFAULT_TRANSITION:Ljava/lang/Object;

    #@e
    return-void
.end method

.method public constructor <init>()V
    .locals 4

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    const/4 v2, -0x1

    #@2
    const/4 v1, 0x0

    #@3
    .line 387
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@6
    .line 180
    const/4 v0, 0x0

    #@7
    iput v0, p0, Landroid/support/v4/app/Fragment;->mState:I

    #@9
    .line 196
    iput v2, p0, Landroid/support/v4/app/Fragment;->mIndex:I

    #@b
    .line 208
    iput v2, p0, Landroid/support/v4/app/Fragment;->mTargetIndex:I

    #@d
    .line 279
    iput-boolean v3, p0, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@f
    .line 301
    iput-boolean v3, p0, Landroid/support/v4/app/Fragment;->mUserVisibleHint:Z

    #@11
    .line 307
    iput-object v1, p0, Landroid/support/v4/app/Fragment;->mEnterTransition:Ljava/lang/Object;

    #@13
    .line 308
    sget-object v0, Landroid/support/v4/app/Fragment;->USE_DEFAULT_TRANSITION:Ljava/lang/Object;

    #@15
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mReturnTransition:Ljava/lang/Object;

    #@17
    .line 309
    iput-object v1, p0, Landroid/support/v4/app/Fragment;->mExitTransition:Ljava/lang/Object;

    #@19
    .line 310
    sget-object v0, Landroid/support/v4/app/Fragment;->USE_DEFAULT_TRANSITION:Ljava/lang/Object;

    #@1b
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mReenterTransition:Ljava/lang/Object;

    #@1d
    .line 311
    iput-object v1, p0, Landroid/support/v4/app/Fragment;->mSharedElementEnterTransition:Ljava/lang/Object;

    #@1f
    .line 312
    sget-object v0, Landroid/support/v4/app/Fragment;->USE_DEFAULT_TRANSITION:Ljava/lang/Object;

    #@21
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mSharedElementReturnTransition:Ljava/lang/Object;

    #@23
    .line 316
    iput-object v1, p0, Landroid/support/v4/app/Fragment;->mEnterTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@25
    .line 317
    iput-object v1, p0, Landroid/support/v4/app/Fragment;->mExitTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@27
    .line 388
    return-void
.end method

.method public static instantiate(Landroid/content/Context;Ljava/lang/String;)Landroid/support/v4/app/Fragment;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "fname"    # Ljava/lang/String;

    #@0
    .prologue
    .line 395
    const/4 v0, 0x0

    #@1
    invoke-static {p0, p1, v0}, Landroid/support/v4/app/Fragment;->instantiate(Landroid/content/Context;Ljava/lang/String;Landroid/os/Bundle;)Landroid/support/v4/app/Fragment;

    #@4
    move-result-object v0

    #@5
    return-object v0
.end method

.method public static instantiate(Landroid/content/Context;Ljava/lang/String;Landroid/os/Bundle;)Landroid/support/v4/app/Fragment;
    .locals 6
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "fname"    # Ljava/lang/String;
    .param p2, "args"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 414
    :try_start_0
    sget-object v3, Landroid/support/v4/app/Fragment;->sClassMap:Landroid/support/v4/util/SimpleArrayMap;

    #@2
    invoke-virtual {v3, p1}, Landroid/support/v4/util/SimpleArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@5
    move-result-object v0

    #@6
    check-cast v0, Ljava/lang/Class;

    #@8
    .line 415
    .local v0, "clazz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    if-nez v0, :cond_0

    #@a
    .line 417
    invoke-virtual {p0}, Landroid/content/Context;->getClassLoader()Ljava/lang/ClassLoader;

    #@d
    move-result-object v3

    #@e
    invoke-virtual {v3, p1}, Ljava/lang/ClassLoader;->loadClass(Ljava/lang/String;)Ljava/lang/Class;

    #@11
    move-result-object v0

    #@12
    .line 418
    sget-object v3, Landroid/support/v4/app/Fragment;->sClassMap:Landroid/support/v4/util/SimpleArrayMap;

    #@14
    invoke-virtual {v3, p1, v0}, Landroid/support/v4/util/SimpleArrayMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@17
    .line 420
    :cond_0
    invoke-virtual {v0}, Ljava/lang/Class;->newInstance()Ljava/lang/Object;

    #@1a
    move-result-object v2

    #@1b
    check-cast v2, Landroid/support/v4/app/Fragment;

    #@1d
    .line 421
    .local v2, "f":Landroid/support/v4/app/Fragment;
    if-eqz p2, :cond_1

    #@1f
    .line 422
    invoke-virtual {v2}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@22
    move-result-object v3

    #@23
    invoke-virtual {v3}, Ljava/lang/Class;->getClassLoader()Ljava/lang/ClassLoader;

    #@26
    move-result-object v3

    #@27
    invoke-virtual {p2, v3}, Landroid/os/Bundle;->setClassLoader(Ljava/lang/ClassLoader;)V

    #@2a
    .line 423
    iput-object p2, v2, Landroid/support/v4/app/Fragment;->mArguments:Landroid/os/Bundle;
    :try_end_0
    .catch Ljava/lang/ClassNotFoundException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/lang/InstantiationException; {:try_start_0 .. :try_end_0} :catch_1
    .catch Ljava/lang/IllegalAccessException; {:try_start_0 .. :try_end_0} :catch_2

    #@2c
    .line 425
    :cond_1
    return-object v2

    #@2d
    .line 426
    .end local v0    # "clazz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    .end local v2    # "f":Landroid/support/v4/app/Fragment;
    :catch_0
    move-exception v1

    #@2e
    .line 427
    .local v1, "e":Ljava/lang/ClassNotFoundException;
    new-instance v3, Landroid/support/v4/app/Fragment$InstantiationException;

    #@30
    new-instance v4, Ljava/lang/StringBuilder;

    #@32
    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    #@35
    const-string v5, "Unable to instantiate fragment "

    #@37
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@3a
    move-result-object v4

    #@3b
    invoke-virtual {v4, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@3e
    move-result-object v4

    #@3f
    const-string v5, ": make sure class name exists, is public, and has an"

    #@41
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@44
    move-result-object v4

    #@45
    const-string v5, " empty constructor that is public"

    #@47
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@4a
    move-result-object v4

    #@4b
    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@4e
    move-result-object v4

    #@4f
    invoke-direct {v3, v4, v1}, Landroid/support/v4/app/Fragment$InstantiationException;-><init>(Ljava/lang/String;Ljava/lang/Exception;)V

    #@52
    throw v3

    #@53
    .line 430
    .end local v1    # "e":Ljava/lang/ClassNotFoundException;
    :catch_1
    move-exception v1

    #@54
    .line 431
    .local v1, "e":Ljava/lang/InstantiationException;
    new-instance v3, Landroid/support/v4/app/Fragment$InstantiationException;

    #@56
    new-instance v4, Ljava/lang/StringBuilder;

    #@58
    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    #@5b
    const-string v5, "Unable to instantiate fragment "

    #@5d
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@60
    move-result-object v4

    #@61
    invoke-virtual {v4, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@64
    move-result-object v4

    #@65
    const-string v5, ": make sure class name exists, is public, and has an"

    #@67
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@6a
    move-result-object v4

    #@6b
    const-string v5, " empty constructor that is public"

    #@6d
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@70
    move-result-object v4

    #@71
    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@74
    move-result-object v4

    #@75
    invoke-direct {v3, v4, v1}, Landroid/support/v4/app/Fragment$InstantiationException;-><init>(Ljava/lang/String;Ljava/lang/Exception;)V

    #@78
    throw v3

    #@79
    .line 434
    .end local v1    # "e":Ljava/lang/InstantiationException;
    :catch_2
    move-exception v1

    #@7a
    .line 435
    .local v1, "e":Ljava/lang/IllegalAccessException;
    new-instance v3, Landroid/support/v4/app/Fragment$InstantiationException;

    #@7c
    new-instance v4, Ljava/lang/StringBuilder;

    #@7e
    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    #@81
    const-string v5, "Unable to instantiate fragment "

    #@83
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@86
    move-result-object v4

    #@87
    invoke-virtual {v4, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@8a
    move-result-object v4

    #@8b
    const-string v5, ": make sure class name exists, is public, and has an"

    #@8d
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@90
    move-result-object v4

    #@91
    const-string v5, " empty constructor that is public"

    #@93
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@96
    move-result-object v4

    #@97
    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@9a
    move-result-object v4

    #@9b
    invoke-direct {v3, v4, v1}, Landroid/support/v4/app/Fragment$InstantiationException;-><init>(Ljava/lang/String;Ljava/lang/Exception;)V

    #@9e
    throw v3
.end method

.method static isSupportFragmentClass(Landroid/content/Context;Ljava/lang/String;)Z
    .locals 3
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "fname"    # Ljava/lang/String;

    #@0
    .prologue
    .line 451
    :try_start_0
    sget-object v2, Landroid/support/v4/app/Fragment;->sClassMap:Landroid/support/v4/util/SimpleArrayMap;

    #@2
    invoke-virtual {v2, p1}, Landroid/support/v4/util/SimpleArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@5
    move-result-object v0

    #@6
    check-cast v0, Ljava/lang/Class;

    #@8
    .line 452
    .local v0, "clazz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    if-nez v0, :cond_0

    #@a
    .line 454
    invoke-virtual {p0}, Landroid/content/Context;->getClassLoader()Ljava/lang/ClassLoader;

    #@d
    move-result-object v2

    #@e
    invoke-virtual {v2, p1}, Ljava/lang/ClassLoader;->loadClass(Ljava/lang/String;)Ljava/lang/Class;

    #@11
    move-result-object v0

    #@12
    .line 455
    sget-object v2, Landroid/support/v4/app/Fragment;->sClassMap:Landroid/support/v4/util/SimpleArrayMap;

    #@14
    invoke-virtual {v2, p1, v0}, Landroid/support/v4/util/SimpleArrayMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@17
    .line 457
    :cond_0
    const-class v2, Landroid/support/v4/app/Fragment;

    #@19
    invoke-virtual {v2, v0}, Ljava/lang/Class;->isAssignableFrom(Ljava/lang/Class;)Z
    :try_end_0
    .catch Ljava/lang/ClassNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    #@1c
    move-result v2

    #@1d
    .line 459
    .end local v0    # "clazz":Ljava/lang/Class;, "Ljava/lang/Class<*>;"
    :goto_0
    return v2

    #@1e
    .line 458
    :catch_0
    move-exception v1

    #@1f
    .line 459
    .local v1, "e":Ljava/lang/ClassNotFoundException;
    const/4 v2, 0x0

    #@20
    goto :goto_0
.end method


# virtual methods
.method public dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V
    .locals 3
    .param p1, "prefix"    # Ljava/lang/String;
    .param p2, "fd"    # Ljava/io/FileDescriptor;
    .param p3, "writer"    # Ljava/io/PrintWriter;
    .param p4, "args"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 1654
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@3
    const-string v0, "mFragmentId=#"

    #@5
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@8
    .line 1655
    iget v0, p0, Landroid/support/v4/app/Fragment;->mFragmentId:I

    #@a
    invoke-static {v0}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@d
    move-result-object v0

    #@e
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@11
    .line 1656
    const-string v0, " mContainerId=#"

    #@13
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@16
    .line 1657
    iget v0, p0, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@18
    invoke-static {v0}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@1b
    move-result-object v0

    #@1c
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1f
    .line 1658
    const-string v0, " mTag="

    #@21
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@24
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mTag:Ljava/lang/String;

    #@26
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@29
    .line 1659
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@2c
    const-string v0, "mState="

    #@2e
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@31
    iget v0, p0, Landroid/support/v4/app/Fragment;->mState:I

    #@33
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(I)V

    #@36
    .line 1660
    const-string v0, " mIndex="

    #@38
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@3b
    iget v0, p0, Landroid/support/v4/app/Fragment;->mIndex:I

    #@3d
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(I)V

    #@40
    .line 1661
    const-string v0, " mWho="

    #@42
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@45
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mWho:Ljava/lang/String;

    #@47
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@4a
    .line 1662
    const-string v0, " mBackStackNesting="

    #@4c
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@4f
    iget v0, p0, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@51
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(I)V

    #@54
    .line 1663
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@57
    const-string v0, "mAdded="

    #@59
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@5c
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@5e
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Z)V

    #@61
    .line 1664
    const-string v0, " mRemoving="

    #@63
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@66
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mRemoving:Z

    #@68
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Z)V

    #@6b
    .line 1665
    const-string v0, " mResumed="

    #@6d
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@70
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mResumed:Z

    #@72
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Z)V

    #@75
    .line 1666
    const-string v0, " mFromLayout="

    #@77
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@7a
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mFromLayout:Z

    #@7c
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Z)V

    #@7f
    .line 1667
    const-string v0, " mInLayout="

    #@81
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@84
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mInLayout:Z

    #@86
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Z)V

    #@89
    .line 1668
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@8c
    const-string v0, "mHidden="

    #@8e
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@91
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@93
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Z)V

    #@96
    .line 1669
    const-string v0, " mDetached="

    #@98
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@9b
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mDetached:Z

    #@9d
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Z)V

    #@a0
    .line 1670
    const-string v0, " mMenuVisible="

    #@a2
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@a5
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@a7
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Z)V

    #@aa
    .line 1671
    const-string v0, " mHasMenu="

    #@ac
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@af
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@b1
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Z)V

    #@b4
    .line 1672
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@b7
    const-string v0, "mRetainInstance="

    #@b9
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@bc
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mRetainInstance:Z

    #@be
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Z)V

    #@c1
    .line 1673
    const-string v0, " mRetaining="

    #@c3
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@c6
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mRetaining:Z

    #@c8
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Z)V

    #@cb
    .line 1674
    const-string v0, " mUserVisibleHint="

    #@cd
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@d0
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mUserVisibleHint:Z

    #@d2
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Z)V

    #@d5
    .line 1675
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@d7
    if-eqz v0, :cond_0

    #@d9
    .line 1676
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@dc
    const-string v0, "mFragmentManager="

    #@de
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@e1
    .line 1677
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@e3
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@e6
    .line 1679
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@e8
    if-eqz v0, :cond_1

    #@ea
    .line 1680
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@ed
    const-string v0, "mActivity="

    #@ef
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@f2
    .line 1681
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@f4
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@f7
    .line 1683
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mParentFragment:Landroid/support/v4/app/Fragment;

    #@f9
    if-eqz v0, :cond_2

    #@fb
    .line 1684
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@fe
    const-string v0, "mParentFragment="

    #@100
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@103
    .line 1685
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mParentFragment:Landroid/support/v4/app/Fragment;

    #@105
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@108
    .line 1687
    :cond_2
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mArguments:Landroid/os/Bundle;

    #@10a
    if-eqz v0, :cond_3

    #@10c
    .line 1688
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@10f
    const-string v0, "mArguments="

    #@111
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@114
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mArguments:Landroid/os/Bundle;

    #@116
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@119
    .line 1690
    :cond_3
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@11b
    if-eqz v0, :cond_4

    #@11d
    .line 1691
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@120
    const-string v0, "mSavedFragmentState="

    #@122
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@125
    .line 1692
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@127
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@12a
    .line 1694
    :cond_4
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mSavedViewState:Landroid/util/SparseArray;

    #@12c
    if-eqz v0, :cond_5

    #@12e
    .line 1695
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@131
    const-string v0, "mSavedViewState="

    #@133
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@136
    .line 1696
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mSavedViewState:Landroid/util/SparseArray;

    #@138
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@13b
    .line 1698
    :cond_5
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@13d
    if-eqz v0, :cond_6

    #@13f
    .line 1699
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@142
    const-string v0, "mTarget="

    #@144
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@147
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@149
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/Object;)V

    #@14c
    .line 1700
    const-string v0, " mTargetRequestCode="

    #@14e
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@151
    .line 1701
    iget v0, p0, Landroid/support/v4/app/Fragment;->mTargetRequestCode:I

    #@153
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(I)V

    #@156
    .line 1703
    :cond_6
    iget v0, p0, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@158
    if-eqz v0, :cond_7

    #@15a
    .line 1704
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@15d
    const-string v0, "mNextAnim="

    #@15f
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@162
    iget v0, p0, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@164
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(I)V

    #@167
    .line 1706
    :cond_7
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mContainer:Landroid/view/ViewGroup;

    #@169
    if-eqz v0, :cond_8

    #@16b
    .line 1707
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@16e
    const-string v0, "mContainer="

    #@170
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@173
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mContainer:Landroid/view/ViewGroup;

    #@175
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@178
    .line 1709
    :cond_8
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@17a
    if-eqz v0, :cond_9

    #@17c
    .line 1710
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@17f
    const-string v0, "mView="

    #@181
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@184
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@186
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@189
    .line 1712
    :cond_9
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mInnerView:Landroid/view/View;

    #@18b
    if-eqz v0, :cond_a

    #@18d
    .line 1713
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@190
    const-string v0, "mInnerView="

    #@192
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@195
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@197
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@19a
    .line 1715
    :cond_a
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mAnimatingAway:Landroid/view/View;

    #@19c
    if-eqz v0, :cond_b

    #@19e
    .line 1716
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1a1
    const-string v0, "mAnimatingAway="

    #@1a3
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1a6
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mAnimatingAway:Landroid/view/View;

    #@1a8
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@1ab
    .line 1717
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1ae
    const-string v0, "mStateAfterAnimating="

    #@1b0
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1b3
    .line 1718
    iget v0, p0, Landroid/support/v4/app/Fragment;->mStateAfterAnimating:I

    #@1b5
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(I)V

    #@1b8
    .line 1720
    :cond_b
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@1ba
    if-eqz v0, :cond_c

    #@1bc
    .line 1721
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1bf
    const-string v0, "Loader Manager:"

    #@1c1
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@1c4
    .line 1722
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@1c6
    new-instance v1, Ljava/lang/StringBuilder;

    #@1c8
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@1cb
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1ce
    move-result-object v1

    #@1cf
    const-string v2, "  "

    #@1d1
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1d4
    move-result-object v1

    #@1d5
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1d8
    move-result-object v1

    #@1d9
    invoke-virtual {v0, v1, p2, p3, p4}, Landroid/support/v4/app/LoaderManagerImpl;->dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V

    #@1dc
    .line 1724
    :cond_c
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@1de
    if-eqz v0, :cond_d

    #@1e0
    .line 1725
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1e3
    new-instance v0, Ljava/lang/StringBuilder;

    #@1e5
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    #@1e8
    const-string v1, "Child "

    #@1ea
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1ed
    move-result-object v0

    #@1ee
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@1f0
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@1f3
    move-result-object v0

    #@1f4
    const-string v1, ":"

    #@1f6
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1f9
    move-result-object v0

    #@1fa
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1fd
    move-result-object v0

    #@1fe
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@201
    .line 1726
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@203
    new-instance v1, Ljava/lang/StringBuilder;

    #@205
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@208
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@20b
    move-result-object v1

    #@20c
    const-string v2, "  "

    #@20e
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@211
    move-result-object v1

    #@212
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@215
    move-result-object v1

    #@216
    invoke-virtual {v0, v1, p2, p3, p4}, Landroid/support/v4/app/FragmentManagerImpl;->dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V

    #@219
    .line 1728
    :cond_d
    return-void
.end method

.method public final equals(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "o"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 493
    invoke-super {p0, p1}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method findFragmentByWho(Ljava/lang/String;)Landroid/support/v4/app/Fragment;
    .locals 1
    .param p1, "who"    # Ljava/lang/String;

    #@0
    .prologue
    .line 1731
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mWho:Ljava/lang/String;

    #@2
    invoke-virtual {p1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@5
    move-result v0

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 1737
    .end local p0    # "this":Landroid/support/v4/app/Fragment;
    :goto_0
    return-object p0

    #@9
    .line 1734
    .restart local p0    # "this":Landroid/support/v4/app/Fragment;
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@b
    if-eqz v0, :cond_1

    #@d
    .line 1735
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@f
    invoke-virtual {v0, p1}, Landroid/support/v4/app/FragmentManagerImpl;->findFragmentByWho(Ljava/lang/String;)Landroid/support/v4/app/Fragment;

    #@12
    move-result-object p0

    #@13
    goto :goto_0

    #@14
    .line 1737
    :cond_1
    const/4 p0, 0x0

    #@15
    goto :goto_0
.end method

.method public final getActivity()Landroid/support/v4/app/FragmentActivity;
    .locals 1

    #@0
    .prologue
    .line 611
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@2
    return-object v0
.end method

.method public getAllowEnterTransitionOverlap()Z
    .locals 1

    #@0
    .prologue
    .line 1617
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mAllowEnterTransitionOverlap:Ljava/lang/Boolean;

    #@2
    if-nez v0, :cond_0

    #@4
    const/4 v0, 0x1

    #@5
    :goto_0
    return v0

    #@6
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mAllowEnterTransitionOverlap:Ljava/lang/Boolean;

    #@8
    invoke-virtual {v0}, Ljava/lang/Boolean;->booleanValue()Z

    #@b
    move-result v0

    #@c
    goto :goto_0
.end method

.method public getAllowReturnTransitionOverlap()Z
    .locals 1

    #@0
    .prologue
    .line 1641
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mAllowReturnTransitionOverlap:Ljava/lang/Boolean;

    #@2
    if-nez v0, :cond_0

    #@4
    const/4 v0, 0x1

    #@5
    :goto_0
    return v0

    #@6
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mAllowReturnTransitionOverlap:Ljava/lang/Boolean;

    #@8
    invoke-virtual {v0}, Ljava/lang/Boolean;->booleanValue()Z

    #@b
    move-result v0

    #@c
    goto :goto_0
.end method

.method public final getArguments()Landroid/os/Bundle;
    .locals 1

    #@0
    .prologue
    .line 558
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mArguments:Landroid/os/Bundle;

    #@2
    return-object v0
.end method

.method public final getChildFragmentManager()Landroid/support/v4/app/FragmentManager;
    .locals 2

    #@0
    .prologue
    .line 676
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 677
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->instantiateChildFragmentManager()V

    #@7
    .line 678
    iget v0, p0, Landroid/support/v4/app/Fragment;->mState:I

    #@9
    const/4 v1, 0x5

    #@a
    if-lt v0, v1, :cond_1

    #@c
    .line 679
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@e
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchResume()V

    #@11
    .line 688
    :cond_0
    :goto_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@13
    return-object v0

    #@14
    .line 680
    :cond_1
    iget v0, p0, Landroid/support/v4/app/Fragment;->mState:I

    #@16
    const/4 v1, 0x4

    #@17
    if-lt v0, v1, :cond_2

    #@19
    .line 681
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@1b
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchStart()V

    #@1e
    goto :goto_0

    #@1f
    .line 682
    :cond_2
    iget v0, p0, Landroid/support/v4/app/Fragment;->mState:I

    #@21
    const/4 v1, 0x2

    #@22
    if-lt v0, v1, :cond_3

    #@24
    .line 683
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@26
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchActivityCreated()V

    #@29
    goto :goto_0

    #@2a
    .line 684
    :cond_3
    iget v0, p0, Landroid/support/v4/app/Fragment;->mState:I

    #@2c
    const/4 v1, 0x1

    #@2d
    if-lt v0, v1, :cond_0

    #@2f
    .line 685
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@31
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchCreate()V

    #@34
    goto :goto_0
.end method

.method public getEnterTransition()Ljava/lang/Object;
    .locals 1

    #@0
    .prologue
    .line 1432
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mEnterTransition:Ljava/lang/Object;

    #@2
    return-object v0
.end method

.method public getExitTransition()Ljava/lang/Object;
    .locals 1

    #@0
    .prologue
    .line 1500
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mExitTransition:Ljava/lang/Object;

    #@2
    return-object v0
.end method

.method public final getFragmentManager()Landroid/support/v4/app/FragmentManager;
    .locals 1

    #@0
    .prologue
    .line 668
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    return-object v0
.end method

.method public final getId()I
    .locals 1

    #@0
    .prologue
    .line 529
    iget v0, p0, Landroid/support/v4/app/Fragment;->mFragmentId:I

    #@2
    return v0
.end method

.method public getLayoutInflater(Landroid/os/Bundle;)Landroid/view/LayoutInflater;
    .locals 3
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 933
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@2
    invoke-virtual {v1}, Landroid/support/v4/app/FragmentActivity;->getLayoutInflater()Landroid/view/LayoutInflater;

    #@5
    move-result-object v1

    #@6
    iget-object v2, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@8
    invoke-virtual {v1, v2}, Landroid/view/LayoutInflater;->cloneInContext(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@b
    move-result-object v0

    #@c
    .line 934
    .local v0, "result":Landroid/view/LayoutInflater;
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getChildFragmentManager()Landroid/support/v4/app/FragmentManager;

    #@f
    .line 935
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@11
    invoke-virtual {v1}, Landroid/support/v4/app/FragmentManagerImpl;->getLayoutInflaterFactory()Landroid/view/LayoutInflater$Factory;

    #@14
    move-result-object v1

    #@15
    invoke-virtual {v0, v1}, Landroid/view/LayoutInflater;->setFactory(Landroid/view/LayoutInflater$Factory;)V

    #@18
    .line 936
    return-object v0
.end method

.method public getLoaderManager()Landroid/support/v4/app/LoaderManager;
    .locals 4

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 877
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@3
    if-eqz v0, :cond_0

    #@5
    .line 878
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@7
    .line 885
    :goto_0
    return-object v0

    #@8
    .line 880
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@a
    if-nez v0, :cond_1

    #@c
    .line 881
    new-instance v0, Ljava/lang/IllegalStateException;

    #@e
    new-instance v1, Ljava/lang/StringBuilder;

    #@10
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@13
    const-string v2, "Fragment "

    #@15
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@18
    move-result-object v1

    #@19
    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v1

    #@1d
    const-string v2, " not attached to Activity"

    #@1f
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@22
    move-result-object v1

    #@23
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@26
    move-result-object v1

    #@27
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@2a
    throw v0

    #@2b
    .line 883
    :cond_1
    iput-boolean v3, p0, Landroid/support/v4/app/Fragment;->mCheckedForLoaderManager:Z

    #@2d
    .line 884
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@2f
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mWho:Ljava/lang/String;

    #@31
    iget-boolean v2, p0, Landroid/support/v4/app/Fragment;->mLoadersStarted:Z

    #@33
    invoke-virtual {v0, v1, v2, v3}, Landroid/support/v4/app/FragmentActivity;->getLoaderManager(Ljava/lang/String;ZZ)Landroid/support/v4/app/LoaderManagerImpl;

    #@36
    move-result-object v0

    #@37
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@39
    .line 885
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@3b
    goto :goto_0
.end method

.method public final getParentFragment()Landroid/support/v4/app/Fragment;
    .locals 1

    #@0
    .prologue
    .line 696
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mParentFragment:Landroid/support/v4/app/Fragment;

    #@2
    return-object v0
.end method

.method public getReenterTransition()Ljava/lang/Object;
    .locals 2

    #@0
    .prologue
    .line 1533
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mReenterTransition:Ljava/lang/Object;

    #@2
    sget-object v1, Landroid/support/v4/app/Fragment;->USE_DEFAULT_TRANSITION:Ljava/lang/Object;

    #@4
    if-ne v0, v1, :cond_0

    #@6
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getExitTransition()Ljava/lang/Object;

    #@9
    move-result-object v0

    #@a
    :goto_0
    return-object v0

    #@b
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mReenterTransition:Ljava/lang/Object;

    #@d
    goto :goto_0
.end method

.method public final getResources()Landroid/content/res/Resources;
    .locals 3

    #@0
    .prologue
    .line 618
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 619
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    new-instance v1, Ljava/lang/StringBuilder;

    #@8
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@b
    const-string v2, "Fragment "

    #@d
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@10
    move-result-object v1

    #@11
    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@14
    move-result-object v1

    #@15
    const-string v2, " not attached to Activity"

    #@17
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1a
    move-result-object v1

    #@1b
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1e
    move-result-object v1

    #@1f
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@22
    throw v0

    #@23
    .line 621
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@25
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentActivity;->getResources()Landroid/content/res/Resources;

    #@28
    move-result-object v0

    #@29
    return-object v0
.end method

.method public final getRetainInstance()Z
    .locals 1

    #@0
    .prologue
    .line 807
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mRetainInstance:Z

    #@2
    return v0
.end method

.method public getReturnTransition()Ljava/lang/Object;
    .locals 2

    #@0
    .prologue
    .line 1466
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mReturnTransition:Ljava/lang/Object;

    #@2
    sget-object v1, Landroid/support/v4/app/Fragment;->USE_DEFAULT_TRANSITION:Ljava/lang/Object;

    #@4
    if-ne v0, v1, :cond_0

    #@6
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getEnterTransition()Ljava/lang/Object;

    #@9
    move-result-object v0

    #@a
    :goto_0
    return-object v0

    #@b
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mReturnTransition:Ljava/lang/Object;

    #@d
    goto :goto_0
.end method

.method public getSharedElementEnterTransition()Ljava/lang/Object;
    .locals 1

    #@0
    .prologue
    .line 1560
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mSharedElementEnterTransition:Ljava/lang/Object;

    #@2
    return-object v0
.end method

.method public getSharedElementReturnTransition()Ljava/lang/Object;
    .locals 2

    #@0
    .prologue
    .line 1592
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mSharedElementReturnTransition:Ljava/lang/Object;

    #@2
    sget-object v1, Landroid/support/v4/app/Fragment;->USE_DEFAULT_TRANSITION:Ljava/lang/Object;

    #@4
    if-ne v0, v1, :cond_0

    #@6
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getSharedElementEnterTransition()Ljava/lang/Object;

    #@9
    move-result-object v0

    #@a
    :goto_0
    return-object v0

    #@b
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mSharedElementReturnTransition:Ljava/lang/Object;

    #@d
    goto :goto_0
.end method

.method public final getString(I)Ljava/lang/String;
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 641
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getResources()Landroid/content/res/Resources;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public final varargs getString(I[Ljava/lang/Object;)Ljava/lang/String;
    .locals 1
    .param p1, "resId"    # I
    .param p2, "formatArgs"    # [Ljava/lang/Object;

    #@0
    .prologue
    .line 654
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getResources()Landroid/content/res/Resources;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1, p2}, Landroid/content/res/Resources;->getString(I[Ljava/lang/Object;)Ljava/lang/String;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public final getTag()Ljava/lang/String;
    .locals 1

    #@0
    .prologue
    .line 536
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mTag:Ljava/lang/String;

    #@2
    return-object v0
.end method

.method public final getTargetFragment()Landroid/support/v4/app/Fragment;
    .locals 1

    #@0
    .prologue
    .line 597
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@2
    return-object v0
.end method

.method public final getTargetRequestCode()I
    .locals 1

    #@0
    .prologue
    .line 604
    iget v0, p0, Landroid/support/v4/app/Fragment;->mTargetRequestCode:I

    #@2
    return v0
.end method

.method public final getText(I)Ljava/lang/CharSequence;
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 631
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getResources()Landroid/content/res/Resources;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getText(I)Ljava/lang/CharSequence;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public getUserVisibleHint()Z
    .locals 1

    #@0
    .prologue
    .line 870
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mUserVisibleHint:Z

    #@2
    return v0
.end method

.method public getView()Landroid/view/View;
    .locals 1
    .annotation build Landroid/support/annotation/Nullable;
    .end annotation

    #@0
    .prologue
    .line 1063
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@2
    return-object v0
.end method

.method public final hasOptionsMenu()Z
    .locals 1

    #@0
    .prologue
    .line 766
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@2
    return v0
.end method

.method public final hashCode()I
    .locals 1

    #@0
    .prologue
    .line 500
    invoke-super {p0}, Ljava/lang/Object;->hashCode()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method initState()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    const/4 v1, 0x0

    #@2
    .line 1213
    const/4 v0, -0x1

    #@3
    iput v0, p0, Landroid/support/v4/app/Fragment;->mIndex:I

    #@5
    .line 1214
    iput-object v2, p0, Landroid/support/v4/app/Fragment;->mWho:Ljava/lang/String;

    #@7
    .line 1215
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@9
    .line 1216
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mRemoving:Z

    #@b
    .line 1217
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mResumed:Z

    #@d
    .line 1218
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mFromLayout:Z

    #@f
    .line 1219
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mInLayout:Z

    #@11
    .line 1220
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mRestored:Z

    #@13
    .line 1221
    iput v1, p0, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@15
    .line 1222
    iput-object v2, p0, Landroid/support/v4/app/Fragment;->mFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@17
    .line 1223
    iput-object v2, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@19
    .line 1224
    iput-object v2, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@1b
    .line 1225
    iput v1, p0, Landroid/support/v4/app/Fragment;->mFragmentId:I

    #@1d
    .line 1226
    iput v1, p0, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@1f
    .line 1227
    iput-object v2, p0, Landroid/support/v4/app/Fragment;->mTag:Ljava/lang/String;

    #@21
    .line 1228
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@23
    .line 1229
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mDetached:Z

    #@25
    .line 1230
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mRetaining:Z

    #@27
    .line 1231
    iput-object v2, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@29
    .line 1232
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mLoadersStarted:Z

    #@2b
    .line 1233
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mCheckedForLoaderManager:Z

    #@2d
    .line 1234
    return-void
.end method

.method instantiateChildFragmentManager()V
    .locals 3

    #@0
    .prologue
    .line 1741
    new-instance v0, Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    invoke-direct {v0}, Landroid/support/v4/app/FragmentManagerImpl;-><init>()V

    #@5
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@7
    .line 1742
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@9
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@b
    new-instance v2, Landroid/support/v4/app/Fragment$1;

    #@d
    invoke-direct {v2, p0}, Landroid/support/v4/app/Fragment$1;-><init>(Landroid/support/v4/app/Fragment;)V

    #@10
    invoke-virtual {v0, v1, v2, p0}, Landroid/support/v4/app/FragmentManagerImpl;->attachActivity(Landroid/support/v4/app/FragmentActivity;Landroid/support/v4/app/FragmentContainer;Landroid/support/v4/app/Fragment;)V

    #@13
    .line 1756
    return-void
.end method

.method public final isAdded()Z
    .locals 1

    #@0
    .prologue
    .line 703
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@6
    if-eqz v0, :cond_0

    #@8
    const/4 v0, 0x1

    #@9
    :goto_0
    return v0

    #@a
    :cond_0
    const/4 v0, 0x0

    #@b
    goto :goto_0
.end method

.method public final isDetached()Z
    .locals 1

    #@0
    .prologue
    .line 712
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mDetached:Z

    #@2
    return v0
.end method

.method public final isHidden()Z
    .locals 1

    #@0
    .prologue
    .line 761
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@2
    return v0
.end method

.method final isInBackStack()Z
    .locals 1

    #@0
    .prologue
    .line 486
    iget v0, p0, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@2
    if-lez v0, :cond_0

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

.method public final isInLayout()Z
    .locals 1

    #@0
    .prologue
    .line 732
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mInLayout:Z

    #@2
    return v0
.end method

.method public final isMenuVisible()Z
    .locals 1

    #@0
    .prologue
    .line 771
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@2
    return v0
.end method

.method public final isRemoving()Z
    .locals 1

    #@0
    .prologue
    .line 721
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mRemoving:Z

    #@2
    return v0
.end method

.method public final isResumed()Z
    .locals 1

    #@0
    .prologue
    .line 740
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mResumed:Z

    #@2
    return v0
.end method

.method public final isVisible()Z
    .locals 1

    #@0
    .prologue
    .line 749
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->isAdded()Z

    #@3
    move-result v0

    #@4
    if-eqz v0, :cond_0

    #@6
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->isHidden()Z

    #@9
    move-result v0

    #@a
    if-nez v0, :cond_0

    #@c
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@e
    if-eqz v0, :cond_0

    #@10
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@12
    invoke-virtual {v0}, Landroid/view/View;->getWindowToken()Landroid/os/IBinder;

    #@15
    move-result-object v0

    #@16
    if-eqz v0, :cond_0

    #@18
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@1a
    invoke-virtual {v0}, Landroid/view/View;->getVisibility()I

    #@1d
    move-result v0

    #@1e
    if-nez v0, :cond_0

    #@20
    const/4 v0, 0x1

    #@21
    :goto_0
    return v0

    #@22
    :cond_0
    const/4 v0, 0x0

    #@23
    goto :goto_0
.end method

.method public onActivityCreated(Landroid/os/Bundle;)V
    .locals 1
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;
        .annotation build Landroid/support/annotation/Nullable;
        .end annotation
    .end param

    #@0
    .prologue
    .line 1080
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 1081
    return-void
.end method

.method public onActivityResult(IILandroid/content/Intent;)V
    .locals 0
    .param p1, "requestCode"    # I
    .param p2, "resultCode"    # I
    .param p3, "data"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 925
    return-void
.end method

.method public onAttach(Landroid/app/Activity;)V
    .locals 1
    .param p1, "activity"    # Landroid/app/Activity;

    #@0
    .prologue
    .line 990
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 991
    return-void
.end method

.method public onConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 1
    .param p1, "newConfig"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    .line 1151
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 1152
    return-void
.end method

.method public onContextItemSelected(Landroid/view/MenuItem;)Z
    .locals 1
    .param p1, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 1383
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public onCreate(Landroid/os/Bundle;)V
    .locals 1
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 1015
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 1016
    return-void
.end method

.method public onCreateAnimation(IZI)Landroid/view/animation/Animation;
    .locals 1
    .param p1, "transit"    # I
    .param p2, "enter"    # Z
    .param p3, "nextAnim"    # I

    #@0
    .prologue
    .line 997
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public onCreateContextMenu(Landroid/view/ContextMenu;Landroid/view/View;Landroid/view/ContextMenu$ContextMenuInfo;)V
    .locals 1
    .param p1, "menu"    # Landroid/view/ContextMenu;
    .param p2, "v"    # Landroid/view/View;
    .param p3, "menuInfo"    # Landroid/view/ContextMenu$ContextMenuInfo;

    #@0
    .prologue
    .line 1337
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->getActivity()Landroid/support/v4/app/FragmentActivity;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1, p2, p3}, Landroid/support/v4/app/FragmentActivity;->onCreateContextMenu(Landroid/view/ContextMenu;Landroid/view/View;Landroid/view/ContextMenu$ContextMenuInfo;)V

    #@7
    .line 1338
    return-void
.end method

.method public onCreateOptionsMenu(Landroid/view/Menu;Landroid/view/MenuInflater;)V
    .locals 0
    .param p1, "menu"    # Landroid/view/Menu;
    .param p2, "inflater"    # Landroid/view/MenuInflater;

    #@0
    .prologue
    .line 1258
    return-void
.end method

.method public onCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;
    .locals 1
    .param p1, "inflater"    # Landroid/view/LayoutInflater;
    .param p2, "container"    # Landroid/view/ViewGroup;
        .annotation build Landroid/support/annotation/Nullable;
        .end annotation
    .end param
    .param p3, "savedInstanceState"    # Landroid/os/Bundle;
        .annotation build Landroid/support/annotation/Nullable;
        .end annotation
    .end param

    #@0
    .prologue
    .line 1039
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public onDestroy()V
    .locals 4

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 1194
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 1197
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCheckedForLoaderManager:Z

    #@5
    if-nez v0, :cond_0

    #@7
    .line 1198
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mCheckedForLoaderManager:Z

    #@9
    .line 1199
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@b
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mWho:Ljava/lang/String;

    #@d
    iget-boolean v2, p0, Landroid/support/v4/app/Fragment;->mLoadersStarted:Z

    #@f
    const/4 v3, 0x0

    #@10
    invoke-virtual {v0, v1, v2, v3}, Landroid/support/v4/app/FragmentActivity;->getLoaderManager(Ljava/lang/String;ZZ)Landroid/support/v4/app/LoaderManagerImpl;

    #@13
    move-result-object v0

    #@14
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@16
    .line 1201
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@18
    if-eqz v0, :cond_1

    #@1a
    .line 1202
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@1c
    invoke-virtual {v0}, Landroid/support/v4/app/LoaderManagerImpl;->doDestroy()V

    #@1f
    .line 1204
    :cond_1
    return-void
.end method

.method public onDestroyOptionsMenu()V
    .locals 0

    #@0
    .prologue
    .line 1285
    return-void
.end method

.method public onDestroyView()V
    .locals 1

    #@0
    .prologue
    .line 1186
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 1187
    return-void
.end method

.method public onDetach()V
    .locals 1

    #@0
    .prologue
    .line 1241
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 1242
    return-void
.end method

.method public onHiddenChanged(Z)V
    .locals 0
    .param p1, "hidden"    # Z

    #@0
    .prologue
    .line 782
    return-void
.end method

.method public onInflate(Landroid/app/Activity;Landroid/util/AttributeSet;Landroid/os/Bundle;)V
    .locals 1
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 982
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 983
    return-void
.end method

.method public onLowMemory()V
    .locals 1

    #@0
    .prologue
    .line 1173
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 1174
    return-void
.end method

.method public onOptionsItemSelected(Landroid/view/MenuItem;)Z
    .locals 1
    .param p1, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 1306
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public onOptionsMenuClosed(Landroid/view/Menu;)V
    .locals 0
    .param p1, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 1317
    return-void
.end method

.method public onPause()V
    .locals 1

    #@0
    .prologue
    .line 1160
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 1161
    return-void
.end method

.method public onPrepareOptionsMenu(Landroid/view/Menu;)V
    .locals 0
    .param p1, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 1275
    return-void
.end method

.method public onResume()V
    .locals 1

    #@0
    .prologue
    .line 1125
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 1126
    return-void
.end method

.method public onSaveInstanceState(Landroid/os/Bundle;)V
    .locals 0
    .param p1, "outState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 1148
    return-void
.end method

.method public onStart()V
    .locals 4

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 1104
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 1106
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mLoadersStarted:Z

    #@5
    if-nez v0, :cond_1

    #@7
    .line 1107
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mLoadersStarted:Z

    #@9
    .line 1108
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCheckedForLoaderManager:Z

    #@b
    if-nez v0, :cond_0

    #@d
    .line 1109
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mCheckedForLoaderManager:Z

    #@f
    .line 1110
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@11
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mWho:Ljava/lang/String;

    #@13
    iget-boolean v2, p0, Landroid/support/v4/app/Fragment;->mLoadersStarted:Z

    #@15
    const/4 v3, 0x0

    #@16
    invoke-virtual {v0, v1, v2, v3}, Landroid/support/v4/app/FragmentActivity;->getLoaderManager(Ljava/lang/String;ZZ)Landroid/support/v4/app/LoaderManagerImpl;

    #@19
    move-result-object v0

    #@1a
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@1c
    .line 1112
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@1e
    if-eqz v0, :cond_1

    #@20
    .line 1113
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@22
    invoke-virtual {v0}, Landroid/support/v4/app/LoaderManagerImpl;->doStart()V

    #@25
    .line 1116
    :cond_1
    return-void
.end method

.method public onStop()V
    .locals 1

    #@0
    .prologue
    .line 1169
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 1170
    return-void
.end method

.method public onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V
    .locals 0
    .param p1, "view"    # Landroid/view/View;
    .param p2, "savedInstanceState"    # Landroid/os/Bundle;
        .annotation build Landroid/support/annotation/Nullable;
        .end annotation
    .end param

    #@0
    .prologue
    .line 1053
    return-void
.end method

.method public onViewStateRestored(Landroid/os/Bundle;)V
    .locals 1
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;
        .annotation build Landroid/support/annotation/Nullable;
        .end annotation
    .end param

    #@0
    .prologue
    .line 1095
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@3
    .line 1096
    return-void
.end method

.method performActivityCreated(Landroid/os/Bundle;)V
    .locals 3
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 1790
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1791
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@6
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->noteStateNotSaved()V

    #@9
    .line 1793
    :cond_0
    const/4 v0, 0x0

    #@a
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@c
    .line 1794
    invoke-virtual {p0, p1}, Landroid/support/v4/app/Fragment;->onActivityCreated(Landroid/os/Bundle;)V

    #@f
    .line 1795
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@11
    if-nez v0, :cond_1

    #@13
    .line 1796
    new-instance v0, Landroid/support/v4/app/SuperNotCalledException;

    #@15
    new-instance v1, Ljava/lang/StringBuilder;

    #@17
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@1a
    const-string v2, "Fragment "

    #@1c
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1f
    move-result-object v1

    #@20
    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@23
    move-result-object v1

    #@24
    const-string v2, " did not call through to super.onActivityCreated()"

    #@26
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@29
    move-result-object v1

    #@2a
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2d
    move-result-object v1

    #@2e
    invoke-direct {v0, v1}, Landroid/support/v4/app/SuperNotCalledException;-><init>(Ljava/lang/String;)V

    #@31
    throw v0

    #@32
    .line 1799
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@34
    if-eqz v0, :cond_2

    #@36
    .line 1800
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@38
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchActivityCreated()V

    #@3b
    .line 1802
    :cond_2
    return-void
.end method

.method performConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 1
    .param p1, "newConfig"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    .line 1841
    invoke-virtual {p0, p1}, Landroid/support/v4/app/Fragment;->onConfigurationChanged(Landroid/content/res/Configuration;)V

    #@3
    .line 1842
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 1843
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@9
    invoke-virtual {v0, p1}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchConfigurationChanged(Landroid/content/res/Configuration;)V

    #@c
    .line 1845
    :cond_0
    return-void
.end method

.method performContextItemSelected(Landroid/view/MenuItem;)Z
    .locals 2
    .param p1, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 1908
    iget-boolean v1, p0, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@3
    if-nez v1, :cond_2

    #@5
    .line 1909
    invoke-virtual {p0, p1}, Landroid/support/v4/app/Fragment;->onContextItemSelected(Landroid/view/MenuItem;)Z

    #@8
    move-result v1

    #@9
    if-eqz v1, :cond_1

    #@b
    .line 1918
    :cond_0
    :goto_0
    return v0

    #@c
    .line 1912
    :cond_1
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@e
    if-eqz v1, :cond_2

    #@10
    .line 1913
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@12
    invoke-virtual {v1, p1}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchContextItemSelected(Landroid/view/MenuItem;)Z

    #@15
    move-result v1

    #@16
    if-nez v1, :cond_0

    #@18
    .line 1918
    :cond_2
    const/4 v0, 0x0

    #@19
    goto :goto_0
.end method

.method performCreate(Landroid/os/Bundle;)V
    .locals 4
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 1759
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    if-eqz v1, :cond_0

    #@4
    .line 1760
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@6
    invoke-virtual {v1}, Landroid/support/v4/app/FragmentManagerImpl;->noteStateNotSaved()V

    #@9
    .line 1762
    :cond_0
    const/4 v1, 0x0

    #@a
    iput-boolean v1, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@c
    .line 1763
    invoke-virtual {p0, p1}, Landroid/support/v4/app/Fragment;->onCreate(Landroid/os/Bundle;)V

    #@f
    .line 1764
    iget-boolean v1, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@11
    if-nez v1, :cond_1

    #@13
    .line 1765
    new-instance v1, Landroid/support/v4/app/SuperNotCalledException;

    #@15
    new-instance v2, Ljava/lang/StringBuilder;

    #@17
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@1a
    const-string v3, "Fragment "

    #@1c
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1f
    move-result-object v2

    #@20
    invoke-virtual {v2, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@23
    move-result-object v2

    #@24
    const-string v3, " did not call through to super.onCreate()"

    #@26
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@29
    move-result-object v2

    #@2a
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2d
    move-result-object v2

    #@2e
    invoke-direct {v1, v2}, Landroid/support/v4/app/SuperNotCalledException;-><init>(Ljava/lang/String;)V

    #@31
    throw v1

    #@32
    .line 1768
    :cond_1
    if-eqz p1, :cond_3

    #@34
    .line 1769
    const-string v1, "android:support:fragments"

    #@36
    invoke-virtual {p1, v1}, Landroid/os/Bundle;->getParcelable(Ljava/lang/String;)Landroid/os/Parcelable;

    #@39
    move-result-object v0

    #@3a
    .line 1771
    .local v0, "p":Landroid/os/Parcelable;
    if-eqz v0, :cond_3

    #@3c
    .line 1772
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@3e
    if-nez v1, :cond_2

    #@40
    .line 1773
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->instantiateChildFragmentManager()V

    #@43
    .line 1775
    :cond_2
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@45
    const/4 v2, 0x0

    #@46
    invoke-virtual {v1, v0, v2}, Landroid/support/v4/app/FragmentManagerImpl;->restoreAllState(Landroid/os/Parcelable;Ljava/util/ArrayList;)V

    #@49
    .line 1776
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@4b
    invoke-virtual {v1}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchCreate()V

    #@4e
    .line 1779
    .end local v0    # "p":Landroid/os/Parcelable;
    :cond_3
    return-void
.end method

.method performCreateOptionsMenu(Landroid/view/Menu;Landroid/view/MenuInflater;)Z
    .locals 2
    .param p1, "menu"    # Landroid/view/Menu;
    .param p2, "inflater"    # Landroid/view/MenuInflater;

    #@0
    .prologue
    .line 1864
    const/4 v0, 0x0

    #@1
    .line 1865
    .local v0, "show":Z
    iget-boolean v1, p0, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@3
    if-nez v1, :cond_1

    #@5
    .line 1866
    iget-boolean v1, p0, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@7
    if-eqz v1, :cond_0

    #@9
    iget-boolean v1, p0, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@b
    if-eqz v1, :cond_0

    #@d
    .line 1867
    const/4 v0, 0x1

    #@e
    .line 1868
    invoke-virtual {p0, p1, p2}, Landroid/support/v4/app/Fragment;->onCreateOptionsMenu(Landroid/view/Menu;Landroid/view/MenuInflater;)V

    #@11
    .line 1870
    :cond_0
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@13
    if-eqz v1, :cond_1

    #@15
    .line 1871
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@17
    invoke-virtual {v1, p1, p2}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchCreateOptionsMenu(Landroid/view/Menu;Landroid/view/MenuInflater;)Z

    #@1a
    move-result v1

    #@1b
    or-int/2addr v0, v1

    #@1c
    .line 1874
    :cond_1
    return v0
.end method

.method performCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;
    .locals 1
    .param p1, "inflater"    # Landroid/view/LayoutInflater;
    .param p2, "container"    # Landroid/view/ViewGroup;
    .param p3, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 1783
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1784
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@6
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->noteStateNotSaved()V

    #@9
    .line 1786
    :cond_0
    invoke-virtual {p0, p1, p2, p3}, Landroid/support/v4/app/Fragment;->onCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;

    #@c
    move-result-object v0

    #@d
    return-object v0
.end method

.method performDestroy()V
    .locals 3

    #@0
    .prologue
    .line 2002
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 2003
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@6
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchDestroy()V

    #@9
    .line 2005
    :cond_0
    const/4 v0, 0x0

    #@a
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@c
    .line 2006
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->onDestroy()V

    #@f
    .line 2007
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@11
    if-nez v0, :cond_1

    #@13
    .line 2008
    new-instance v0, Landroid/support/v4/app/SuperNotCalledException;

    #@15
    new-instance v1, Ljava/lang/StringBuilder;

    #@17
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@1a
    const-string v2, "Fragment "

    #@1c
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1f
    move-result-object v1

    #@20
    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@23
    move-result-object v1

    #@24
    const-string v2, " did not call through to super.onDestroy()"

    #@26
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@29
    move-result-object v1

    #@2a
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2d
    move-result-object v1

    #@2e
    invoke-direct {v0, v1}, Landroid/support/v4/app/SuperNotCalledException;-><init>(Ljava/lang/String;)V

    #@31
    throw v0

    #@32
    .line 2011
    :cond_1
    return-void
.end method

.method performDestroyView()V
    .locals 3

    #@0
    .prologue
    .line 1987
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1988
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@6
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchDestroyView()V

    #@9
    .line 1990
    :cond_0
    const/4 v0, 0x0

    #@a
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@c
    .line 1991
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->onDestroyView()V

    #@f
    .line 1992
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@11
    if-nez v0, :cond_1

    #@13
    .line 1993
    new-instance v0, Landroid/support/v4/app/SuperNotCalledException;

    #@15
    new-instance v1, Ljava/lang/StringBuilder;

    #@17
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@1a
    const-string v2, "Fragment "

    #@1c
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1f
    move-result-object v1

    #@20
    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@23
    move-result-object v1

    #@24
    const-string v2, " did not call through to super.onDestroyView()"

    #@26
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@29
    move-result-object v1

    #@2a
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2d
    move-result-object v1

    #@2e
    invoke-direct {v0, v1}, Landroid/support/v4/app/SuperNotCalledException;-><init>(Ljava/lang/String;)V

    #@31
    throw v0

    #@32
    .line 1996
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@34
    if-eqz v0, :cond_2

    #@36
    .line 1997
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@38
    invoke-virtual {v0}, Landroid/support/v4/app/LoaderManagerImpl;->doReportNextStart()V

    #@3b
    .line 1999
    :cond_2
    return-void
.end method

.method performLowMemory()V
    .locals 1

    #@0
    .prologue
    .line 1848
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->onLowMemory()V

    #@3
    .line 1849
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 1850
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@9
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchLowMemory()V

    #@c
    .line 1852
    :cond_0
    return-void
.end method

.method performOptionsItemSelected(Landroid/view/MenuItem;)Z
    .locals 2
    .param p1, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 1892
    iget-boolean v1, p0, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@3
    if-nez v1, :cond_2

    #@5
    .line 1893
    iget-boolean v1, p0, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@7
    if-eqz v1, :cond_1

    #@9
    iget-boolean v1, p0, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@b
    if-eqz v1, :cond_1

    #@d
    .line 1894
    invoke-virtual {p0, p1}, Landroid/support/v4/app/Fragment;->onOptionsItemSelected(Landroid/view/MenuItem;)Z

    #@10
    move-result v1

    #@11
    if-eqz v1, :cond_1

    #@13
    .line 1904
    :cond_0
    :goto_0
    return v0

    #@14
    .line 1898
    :cond_1
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@16
    if-eqz v1, :cond_2

    #@18
    .line 1899
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@1a
    invoke-virtual {v1, p1}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchOptionsItemSelected(Landroid/view/MenuItem;)Z

    #@1d
    move-result v1

    #@1e
    if-nez v1, :cond_0

    #@20
    .line 1904
    :cond_2
    const/4 v0, 0x0

    #@21
    goto :goto_0
.end method

.method performOptionsMenuClosed(Landroid/view/Menu;)V
    .locals 1
    .param p1, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 1922
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@2
    if-nez v0, :cond_1

    #@4
    .line 1923
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@6
    if-eqz v0, :cond_0

    #@8
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@a
    if-eqz v0, :cond_0

    #@c
    .line 1924
    invoke-virtual {p0, p1}, Landroid/support/v4/app/Fragment;->onOptionsMenuClosed(Landroid/view/Menu;)V

    #@f
    .line 1926
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@11
    if-eqz v0, :cond_1

    #@13
    .line 1927
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@15
    invoke-virtual {v0, p1}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchOptionsMenuClosed(Landroid/view/Menu;)V

    #@18
    .line 1930
    :cond_1
    return-void
.end method

.method performPause()V
    .locals 3

    #@0
    .prologue
    .line 1943
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1944
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@6
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchPause()V

    #@9
    .line 1946
    :cond_0
    const/4 v0, 0x0

    #@a
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@c
    .line 1947
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->onPause()V

    #@f
    .line 1948
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@11
    if-nez v0, :cond_1

    #@13
    .line 1949
    new-instance v0, Landroid/support/v4/app/SuperNotCalledException;

    #@15
    new-instance v1, Ljava/lang/StringBuilder;

    #@17
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@1a
    const-string v2, "Fragment "

    #@1c
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1f
    move-result-object v1

    #@20
    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@23
    move-result-object v1

    #@24
    const-string v2, " did not call through to super.onPause()"

    #@26
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@29
    move-result-object v1

    #@2a
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2d
    move-result-object v1

    #@2e
    invoke-direct {v0, v1}, Landroid/support/v4/app/SuperNotCalledException;-><init>(Ljava/lang/String;)V

    #@31
    throw v0

    #@32
    .line 1952
    :cond_1
    return-void
.end method

.method performPrepareOptionsMenu(Landroid/view/Menu;)Z
    .locals 2
    .param p1, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 1878
    const/4 v0, 0x0

    #@1
    .line 1879
    .local v0, "show":Z
    iget-boolean v1, p0, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@3
    if-nez v1, :cond_1

    #@5
    .line 1880
    iget-boolean v1, p0, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@7
    if-eqz v1, :cond_0

    #@9
    iget-boolean v1, p0, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@b
    if-eqz v1, :cond_0

    #@d
    .line 1881
    const/4 v0, 0x1

    #@e
    .line 1882
    invoke-virtual {p0, p1}, Landroid/support/v4/app/Fragment;->onPrepareOptionsMenu(Landroid/view/Menu;)V

    #@11
    .line 1884
    :cond_0
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@13
    if-eqz v1, :cond_1

    #@15
    .line 1885
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@17
    invoke-virtual {v1, p1}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchPrepareOptionsMenu(Landroid/view/Menu;)Z

    #@1a
    move-result v1

    #@1b
    or-int/2addr v0, v1

    #@1c
    .line 1888
    :cond_1
    return v0
.end method

.method performReallyStop()V
    .locals 4

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 1967
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@3
    if-eqz v0, :cond_0

    #@5
    .line 1968
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@7
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchReallyStop()V

    #@a
    .line 1970
    :cond_0
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mLoadersStarted:Z

    #@c
    if-eqz v0, :cond_2

    #@e
    .line 1971
    iput-boolean v3, p0, Landroid/support/v4/app/Fragment;->mLoadersStarted:Z

    #@10
    .line 1972
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCheckedForLoaderManager:Z

    #@12
    if-nez v0, :cond_1

    #@14
    .line 1973
    const/4 v0, 0x1

    #@15
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCheckedForLoaderManager:Z

    #@17
    .line 1974
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@19
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mWho:Ljava/lang/String;

    #@1b
    iget-boolean v2, p0, Landroid/support/v4/app/Fragment;->mLoadersStarted:Z

    #@1d
    invoke-virtual {v0, v1, v2, v3}, Landroid/support/v4/app/FragmentActivity;->getLoaderManager(Ljava/lang/String;ZZ)Landroid/support/v4/app/LoaderManagerImpl;

    #@20
    move-result-object v0

    #@21
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@23
    .line 1976
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@25
    if-eqz v0, :cond_2

    #@27
    .line 1977
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@29
    iget-boolean v0, v0, Landroid/support/v4/app/FragmentActivity;->mRetaining:Z

    #@2b
    if-nez v0, :cond_3

    #@2d
    .line 1978
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@2f
    invoke-virtual {v0}, Landroid/support/v4/app/LoaderManagerImpl;->doStop()V

    #@32
    .line 1984
    :cond_2
    :goto_0
    return-void

    #@33
    .line 1980
    :cond_3
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@35
    invoke-virtual {v0}, Landroid/support/v4/app/LoaderManagerImpl;->doRetain()V

    #@38
    goto :goto_0
.end method

.method performResume()V
    .locals 3

    #@0
    .prologue
    .line 1824
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1825
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@6
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->noteStateNotSaved()V

    #@9
    .line 1826
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@b
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->execPendingActions()Z

    #@e
    .line 1828
    :cond_0
    const/4 v0, 0x0

    #@f
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@11
    .line 1829
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->onResume()V

    #@14
    .line 1830
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@16
    if-nez v0, :cond_1

    #@18
    .line 1831
    new-instance v0, Landroid/support/v4/app/SuperNotCalledException;

    #@1a
    new-instance v1, Ljava/lang/StringBuilder;

    #@1c
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@1f
    const-string v2, "Fragment "

    #@21
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@24
    move-result-object v1

    #@25
    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@28
    move-result-object v1

    #@29
    const-string v2, " did not call through to super.onResume()"

    #@2b
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2e
    move-result-object v1

    #@2f
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@32
    move-result-object v1

    #@33
    invoke-direct {v0, v1}, Landroid/support/v4/app/SuperNotCalledException;-><init>(Ljava/lang/String;)V

    #@36
    throw v0

    #@37
    .line 1834
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@39
    if-eqz v0, :cond_2

    #@3b
    .line 1835
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@3d
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchResume()V

    #@40
    .line 1836
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@42
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->execPendingActions()Z

    #@45
    .line 1838
    :cond_2
    return-void
.end method

.method performSaveInstanceState(Landroid/os/Bundle;)V
    .locals 2
    .param p1, "outState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 1933
    invoke-virtual {p0, p1}, Landroid/support/v4/app/Fragment;->onSaveInstanceState(Landroid/os/Bundle;)V

    #@3
    .line 1934
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@5
    if-eqz v1, :cond_0

    #@7
    .line 1935
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@9
    invoke-virtual {v1}, Landroid/support/v4/app/FragmentManagerImpl;->saveAllState()Landroid/os/Parcelable;

    #@c
    move-result-object v0

    #@d
    .line 1936
    .local v0, "p":Landroid/os/Parcelable;
    if-eqz v0, :cond_0

    #@f
    .line 1937
    const-string v1, "android:support:fragments"

    #@11
    invoke-virtual {p1, v1, v0}, Landroid/os/Bundle;->putParcelable(Ljava/lang/String;Landroid/os/Parcelable;)V

    #@14
    .line 1940
    .end local v0    # "p":Landroid/os/Parcelable;
    :cond_0
    return-void
.end method

.method performStart()V
    .locals 3

    #@0
    .prologue
    .line 1805
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1806
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@6
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->noteStateNotSaved()V

    #@9
    .line 1807
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@b
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->execPendingActions()Z

    #@e
    .line 1809
    :cond_0
    const/4 v0, 0x0

    #@f
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@11
    .line 1810
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->onStart()V

    #@14
    .line 1811
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@16
    if-nez v0, :cond_1

    #@18
    .line 1812
    new-instance v0, Landroid/support/v4/app/SuperNotCalledException;

    #@1a
    new-instance v1, Ljava/lang/StringBuilder;

    #@1c
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@1f
    const-string v2, "Fragment "

    #@21
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@24
    move-result-object v1

    #@25
    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@28
    move-result-object v1

    #@29
    const-string v2, " did not call through to super.onStart()"

    #@2b
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2e
    move-result-object v1

    #@2f
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@32
    move-result-object v1

    #@33
    invoke-direct {v0, v1}, Landroid/support/v4/app/SuperNotCalledException;-><init>(Ljava/lang/String;)V

    #@36
    throw v0

    #@37
    .line 1815
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@39
    if-eqz v0, :cond_2

    #@3b
    .line 1816
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@3d
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchStart()V

    #@40
    .line 1818
    :cond_2
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@42
    if-eqz v0, :cond_3

    #@44
    .line 1819
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@46
    invoke-virtual {v0}, Landroid/support/v4/app/LoaderManagerImpl;->doReportStart()V

    #@49
    .line 1821
    :cond_3
    return-void
.end method

.method performStop()V
    .locals 3

    #@0
    .prologue
    .line 1955
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1956
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@6
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchStop()V

    #@9
    .line 1958
    :cond_0
    const/4 v0, 0x0

    #@a
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@c
    .line 1959
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->onStop()V

    #@f
    .line 1960
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@11
    if-nez v0, :cond_1

    #@13
    .line 1961
    new-instance v0, Landroid/support/v4/app/SuperNotCalledException;

    #@15
    new-instance v1, Ljava/lang/StringBuilder;

    #@17
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@1a
    const-string v2, "Fragment "

    #@1c
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1f
    move-result-object v1

    #@20
    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@23
    move-result-object v1

    #@24
    const-string v2, " did not call through to super.onStop()"

    #@26
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@29
    move-result-object v1

    #@2a
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2d
    move-result-object v1

    #@2e
    invoke-direct {v0, v1}, Landroid/support/v4/app/SuperNotCalledException;-><init>(Ljava/lang/String;)V

    #@31
    throw v0

    #@32
    .line 1964
    :cond_1
    return-void
.end method

.method public registerForContextMenu(Landroid/view/View;)V
    .locals 0
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 1351
    invoke-virtual {p1, p0}, Landroid/view/View;->setOnCreateContextMenuListener(Landroid/view/View$OnCreateContextMenuListener;)V

    #@3
    .line 1352
    return-void
.end method

.method final restoreViewState(Landroid/os/Bundle;)V
    .locals 3
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 464
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mSavedViewState:Landroid/util/SparseArray;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 465
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mInnerView:Landroid/view/View;

    #@6
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mSavedViewState:Landroid/util/SparseArray;

    #@8
    invoke-virtual {v0, v1}, Landroid/view/View;->restoreHierarchyState(Landroid/util/SparseArray;)V

    #@b
    .line 466
    const/4 v0, 0x0

    #@c
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mSavedViewState:Landroid/util/SparseArray;

    #@e
    .line 468
    :cond_0
    const/4 v0, 0x0

    #@f
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@11
    .line 469
    invoke-virtual {p0, p1}, Landroid/support/v4/app/Fragment;->onViewStateRestored(Landroid/os/Bundle;)V

    #@14
    .line 470
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@16
    if-nez v0, :cond_1

    #@18
    .line 471
    new-instance v0, Landroid/support/v4/app/SuperNotCalledException;

    #@1a
    new-instance v1, Ljava/lang/StringBuilder;

    #@1c
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@1f
    const-string v2, "Fragment "

    #@21
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@24
    move-result-object v1

    #@25
    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@28
    move-result-object v1

    #@29
    const-string v2, " did not call through to super.onViewStateRestored()"

    #@2b
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2e
    move-result-object v1

    #@2f
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@32
    move-result-object v1

    #@33
    invoke-direct {v0, v1}, Landroid/support/v4/app/SuperNotCalledException;-><init>(Ljava/lang/String;)V

    #@36
    throw v0

    #@37
    .line 474
    :cond_1
    return-void
.end method

.method public setAllowEnterTransitionOverlap(Z)V
    .locals 1
    .param p1, "allow"    # Z

    #@0
    .prologue
    .line 1605
    invoke-static {p1}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    #@3
    move-result-object v0

    #@4
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mAllowEnterTransitionOverlap:Ljava/lang/Boolean;

    #@6
    .line 1606
    return-void
.end method

.method public setAllowReturnTransitionOverlap(Z)V
    .locals 1
    .param p1, "allow"    # Z

    #@0
    .prologue
    .line 1629
    invoke-static {p1}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    #@3
    move-result-object v0

    #@4
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mAllowReturnTransitionOverlap:Ljava/lang/Boolean;

    #@6
    .line 1630
    return-void
.end method

.method public setArguments(Landroid/os/Bundle;)V
    .locals 2
    .param p1, "args"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 547
    iget v0, p0, Landroid/support/v4/app/Fragment;->mIndex:I

    #@2
    if-ltz v0, :cond_0

    #@4
    .line 548
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "Fragment already active"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 550
    :cond_0
    iput-object p1, p0, Landroid/support/v4/app/Fragment;->mArguments:Landroid/os/Bundle;

    #@e
    .line 551
    return-void
.end method

.method public setEnterSharedElementCallback(Landroid/support/v4/app/SharedElementCallback;)V
    .locals 0
    .param p1, "callback"    # Landroid/support/v4/app/SharedElementCallback;

    #@0
    .prologue
    .line 1394
    iput-object p1, p0, Landroid/support/v4/app/Fragment;->mEnterTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@2
    .line 1395
    return-void
.end method

.method public setEnterTransition(Ljava/lang/Object;)V
    .locals 0
    .param p1, "transition"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 1419
    iput-object p1, p0, Landroid/support/v4/app/Fragment;->mEnterTransition:Ljava/lang/Object;

    #@2
    .line 1420
    return-void
.end method

.method public setExitSharedElementCallback(Landroid/support/v4/app/SharedElementCallback;)V
    .locals 0
    .param p1, "callback"    # Landroid/support/v4/app/SharedElementCallback;

    #@0
    .prologue
    .line 1405
    iput-object p1, p0, Landroid/support/v4/app/Fragment;->mExitTransitionCallback:Landroid/support/v4/app/SharedElementCallback;

    #@2
    .line 1406
    return-void
.end method

.method public setExitTransition(Ljava/lang/Object;)V
    .locals 0
    .param p1, "transition"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 1484
    iput-object p1, p0, Landroid/support/v4/app/Fragment;->mExitTransition:Ljava/lang/Object;

    #@2
    .line 1485
    return-void
.end method

.method public setHasOptionsMenu(Z)V
    .locals 1
    .param p1, "hasMenu"    # Z

    #@0
    .prologue
    .line 818
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 819
    iput-boolean p1, p0, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@6
    .line 820
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->isAdded()Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->isHidden()Z

    #@f
    move-result v0

    #@10
    if-nez v0, :cond_0

    #@12
    .line 821
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@14
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentActivity;->supportInvalidateOptionsMenu()V

    #@17
    .line 824
    :cond_0
    return-void
.end method

.method final setIndex(ILandroid/support/v4/app/Fragment;)V
    .locals 2
    .param p1, "index"    # I
    .param p2, "parent"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 477
    iput p1, p0, Landroid/support/v4/app/Fragment;->mIndex:I

    #@2
    .line 478
    if-eqz p2, :cond_0

    #@4
    .line 479
    new-instance v0, Ljava/lang/StringBuilder;

    #@6
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    #@9
    iget-object v1, p2, Landroid/support/v4/app/Fragment;->mWho:Ljava/lang/String;

    #@b
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@e
    move-result-object v0

    #@f
    const-string v1, ":"

    #@11
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@14
    move-result-object v0

    #@15
    iget v1, p0, Landroid/support/v4/app/Fragment;->mIndex:I

    #@17
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@1a
    move-result-object v0

    #@1b
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1e
    move-result-object v0

    #@1f
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mWho:Ljava/lang/String;

    #@21
    .line 483
    :goto_0
    return-void

    #@22
    .line 481
    :cond_0
    new-instance v0, Ljava/lang/StringBuilder;

    #@24
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    #@27
    const-string v1, "android:fragment:"

    #@29
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2c
    move-result-object v0

    #@2d
    iget v1, p0, Landroid/support/v4/app/Fragment;->mIndex:I

    #@2f
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@32
    move-result-object v0

    #@33
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@36
    move-result-object v0

    #@37
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mWho:Ljava/lang/String;

    #@39
    goto :goto_0
.end method

.method public setInitialSavedState(Landroid/support/v4/app/Fragment$SavedState;)V
    .locals 2
    .param p1, "state"    # Landroid/support/v4/app/Fragment$SavedState;

    #@0
    .prologue
    .line 570
    iget v0, p0, Landroid/support/v4/app/Fragment;->mIndex:I

    #@2
    if-ltz v0, :cond_0

    #@4
    .line 571
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "Fragment already active"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 573
    :cond_0
    if-eqz p1, :cond_1

    #@e
    iget-object v0, p1, Landroid/support/v4/app/Fragment$SavedState;->mState:Landroid/os/Bundle;

    #@10
    if-eqz v0, :cond_1

    #@12
    iget-object v0, p1, Landroid/support/v4/app/Fragment$SavedState;->mState:Landroid/os/Bundle;

    #@14
    :goto_0
    iput-object v0, p0, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@16
    .line 575
    return-void

    #@17
    .line 573
    :cond_1
    const/4 v0, 0x0

    #@18
    goto :goto_0
.end method

.method public setMenuVisibility(Z)V
    .locals 1
    .param p1, "menuVisible"    # Z

    #@0
    .prologue
    .line 836
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 837
    iput-boolean p1, p0, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@6
    .line 838
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@8
    if-eqz v0, :cond_0

    #@a
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->isAdded()Z

    #@d
    move-result v0

    #@e
    if-eqz v0, :cond_0

    #@10
    invoke-virtual {p0}, Landroid/support/v4/app/Fragment;->isHidden()Z

    #@13
    move-result v0

    #@14
    if-nez v0, :cond_0

    #@16
    .line 839
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@18
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentActivity;->supportInvalidateOptionsMenu()V

    #@1b
    .line 842
    :cond_0
    return-void
.end method

.method public setReenterTransition(Ljava/lang/Object;)V
    .locals 0
    .param p1, "transition"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 1517
    iput-object p1, p0, Landroid/support/v4/app/Fragment;->mReenterTransition:Ljava/lang/Object;

    #@2
    .line 1518
    return-void
.end method

.method public setRetainInstance(Z)V
    .locals 2
    .param p1, "retain"    # Z

    #@0
    .prologue
    .line 799
    if-eqz p1, :cond_0

    #@2
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mParentFragment:Landroid/support/v4/app/Fragment;

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 800
    new-instance v0, Ljava/lang/IllegalStateException;

    #@8
    const-string v1, "Can\'t retain fragements that are nested in other fragments"

    #@a
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@d
    throw v0

    #@e
    .line 803
    :cond_0
    iput-boolean p1, p0, Landroid/support/v4/app/Fragment;->mRetainInstance:Z

    #@10
    .line 804
    return-void
.end method

.method public setReturnTransition(Ljava/lang/Object;)V
    .locals 0
    .param p1, "transition"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 1450
    iput-object p1, p0, Landroid/support/v4/app/Fragment;->mReturnTransition:Ljava/lang/Object;

    #@2
    .line 1451
    return-void
.end method

.method public setSharedElementEnterTransition(Ljava/lang/Object;)V
    .locals 0
    .param p1, "transition"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 1547
    iput-object p1, p0, Landroid/support/v4/app/Fragment;->mSharedElementEnterTransition:Ljava/lang/Object;

    #@2
    .line 1548
    return-void
.end method

.method public setSharedElementReturnTransition(Ljava/lang/Object;)V
    .locals 0
    .param p1, "transition"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 1576
    iput-object p1, p0, Landroid/support/v4/app/Fragment;->mSharedElementReturnTransition:Ljava/lang/Object;

    #@2
    .line 1577
    return-void
.end method

.method public setTargetFragment(Landroid/support/v4/app/Fragment;I)V
    .locals 0
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;
    .param p2, "requestCode"    # I

    #@0
    .prologue
    .line 589
    iput-object p1, p0, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@2
    .line 590
    iput p2, p0, Landroid/support/v4/app/Fragment;->mTargetRequestCode:I

    #@4
    .line 591
    return-void
.end method

.method public setUserVisibleHint(Z)V
    .locals 2
    .param p1, "isVisibleToUser"    # Z

    #@0
    .prologue
    .line 858
    iget-boolean v0, p0, Landroid/support/v4/app/Fragment;->mUserVisibleHint:Z

    #@2
    if-nez v0, :cond_0

    #@4
    if-eqz p1, :cond_0

    #@6
    iget v0, p0, Landroid/support/v4/app/Fragment;->mState:I

    #@8
    const/4 v1, 0x4

    #@9
    if-ge v0, v1, :cond_0

    #@b
    .line 859
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@d
    invoke-virtual {v0, p0}, Landroid/support/v4/app/FragmentManagerImpl;->performPendingDeferredStart(Landroid/support/v4/app/Fragment;)V

    #@10
    .line 861
    :cond_0
    iput-boolean p1, p0, Landroid/support/v4/app/Fragment;->mUserVisibleHint:Z

    #@12
    .line 862
    if-nez p1, :cond_1

    #@14
    const/4 v0, 0x1

    #@15
    :goto_0
    iput-boolean v0, p0, Landroid/support/v4/app/Fragment;->mDeferStart:Z

    #@17
    .line 863
    return-void

    #@18
    .line 862
    :cond_1
    const/4 v0, 0x0

    #@19
    goto :goto_0
.end method

.method public startActivity(Landroid/content/Intent;)V
    .locals 3
    .param p1, "intent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 893
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 894
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    new-instance v1, Ljava/lang/StringBuilder;

    #@8
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@b
    const-string v2, "Fragment "

    #@d
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@10
    move-result-object v1

    #@11
    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@14
    move-result-object v1

    #@15
    const-string v2, " not attached to Activity"

    #@17
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1a
    move-result-object v1

    #@1b
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1e
    move-result-object v1

    #@1f
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@22
    throw v0

    #@23
    .line 896
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@25
    const/4 v1, -0x1

    #@26
    invoke-virtual {v0, p0, p1, v1}, Landroid/support/v4/app/FragmentActivity;->startActivityFromFragment(Landroid/support/v4/app/Fragment;Landroid/content/Intent;I)V

    #@29
    .line 897
    return-void
.end method

.method public startActivityForResult(Landroid/content/Intent;I)V
    .locals 3
    .param p1, "intent"    # Landroid/content/Intent;
    .param p2, "requestCode"    # I

    #@0
    .prologue
    .line 904
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 905
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    new-instance v1, Ljava/lang/StringBuilder;

    #@8
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@b
    const-string v2, "Fragment "

    #@d
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@10
    move-result-object v1

    #@11
    invoke-virtual {v1, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@14
    move-result-object v1

    #@15
    const-string v2, " not attached to Activity"

    #@17
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1a
    move-result-object v1

    #@1b
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1e
    move-result-object v1

    #@1f
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@22
    throw v0

    #@23
    .line 907
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@25
    invoke-virtual {v0, p0, p1, p2}, Landroid/support/v4/app/FragmentActivity;->startActivityFromFragment(Landroid/support/v4/app/Fragment;Landroid/content/Intent;I)V

    #@28
    .line 908
    return-void
.end method

.method public toString()Ljava/lang/String;
    .locals 2

    #@0
    .prologue
    .line 505
    new-instance v0, Ljava/lang/StringBuilder;

    #@2
    const/16 v1, 0x80

    #@4
    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(I)V

    #@7
    .line 506
    .local v0, "sb":Ljava/lang/StringBuilder;
    invoke-static {p0, v0}, Landroid/support/v4/util/DebugUtils;->buildShortClassTag(Ljava/lang/Object;Ljava/lang/StringBuilder;)V

    #@a
    .line 507
    iget v1, p0, Landroid/support/v4/app/Fragment;->mIndex:I

    #@c
    if-ltz v1, :cond_0

    #@e
    .line 508
    const-string v1, " #"

    #@10
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@13
    .line 509
    iget v1, p0, Landroid/support/v4/app/Fragment;->mIndex:I

    #@15
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@18
    .line 511
    :cond_0
    iget v1, p0, Landroid/support/v4/app/Fragment;->mFragmentId:I

    #@1a
    if-eqz v1, :cond_1

    #@1c
    .line 512
    const-string v1, " id=0x"

    #@1e
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@21
    .line 513
    iget v1, p0, Landroid/support/v4/app/Fragment;->mFragmentId:I

    #@23
    invoke-static {v1}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@26
    move-result-object v1

    #@27
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2a
    .line 515
    :cond_1
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mTag:Ljava/lang/String;

    #@2c
    if-eqz v1, :cond_2

    #@2e
    .line 516
    const-string v1, " "

    #@30
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@33
    .line 517
    iget-object v1, p0, Landroid/support/v4/app/Fragment;->mTag:Ljava/lang/String;

    #@35
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@38
    .line 519
    :cond_2
    const/16 v1, 0x7d

    #@3a
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@3d
    .line 520
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@40
    move-result-object v1

    #@41
    return-object v1
.end method

.method public unregisterForContextMenu(Landroid/view/View;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 1362
    const/4 v0, 0x0

    #@1
    invoke-virtual {p1, v0}, Landroid/view/View;->setOnCreateContextMenuListener(Landroid/view/View$OnCreateContextMenuListener;)V

    #@4
    .line 1363
    return-void
.end method
