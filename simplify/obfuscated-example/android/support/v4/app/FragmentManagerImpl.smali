.class final Landroid/support/v4/app/FragmentManagerImpl;
.super Landroid/support/v4/app/FragmentManager;
.source "FragmentManager.java"

# interfaces
.implements Landroid/view/LayoutInflater$Factory;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/FragmentManagerImpl$FragmentTag;
    }
.end annotation


# static fields
.field static final ACCELERATE_CUBIC:Landroid/view/animation/Interpolator;

.field static final ACCELERATE_QUINT:Landroid/view/animation/Interpolator;

.field static final ANIM_DUR:I = 0xdc

.field public static final ANIM_STYLE_CLOSE_ENTER:I = 0x3

.field public static final ANIM_STYLE_CLOSE_EXIT:I = 0x4

.field public static final ANIM_STYLE_FADE_ENTER:I = 0x5

.field public static final ANIM_STYLE_FADE_EXIT:I = 0x6

.field public static final ANIM_STYLE_OPEN_ENTER:I = 0x1

.field public static final ANIM_STYLE_OPEN_EXIT:I = 0x2

.field static DEBUG:Z = false

.field static final DECELERATE_CUBIC:Landroid/view/animation/Interpolator;

.field static final DECELERATE_QUINT:Landroid/view/animation/Interpolator;

.field static final HONEYCOMB:Z

.field static final TAG:Ljava/lang/String; = "FragmentManager"

.field static final TARGET_REQUEST_CODE_STATE_TAG:Ljava/lang/String; = "android:target_req_state"

.field static final TARGET_STATE_TAG:Ljava/lang/String; = "android:target_state"

.field static final USER_VISIBLE_HINT_TAG:Ljava/lang/String; = "android:user_visible_hint"

.field static final VIEW_STATE_TAG:Ljava/lang/String; = "android:view_state"


# instance fields
.field mActive:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;"
        }
    .end annotation
.end field

.field mActivity:Landroid/support/v4/app/FragmentActivity;

.field mAdded:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;"
        }
    .end annotation
.end field

.field mAvailBackStackIndices:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation
.end field

.field mAvailIndices:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/Integer;",
            ">;"
        }
    .end annotation
.end field

.field mBackStack:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v4/app/BackStackRecord;",
            ">;"
        }
    .end annotation
.end field

.field mBackStackChangeListeners:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v4/app/FragmentManager$OnBackStackChangedListener;",
            ">;"
        }
    .end annotation
.end field

.field mBackStackIndices:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v4/app/BackStackRecord;",
            ">;"
        }
    .end annotation
.end field

.field mContainer:Landroid/support/v4/app/FragmentContainer;

.field mCreatedMenus:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;"
        }
    .end annotation
.end field

.field mCurState:I

.field mDestroyed:Z

.field mExecCommit:Ljava/lang/Runnable;

.field mExecutingActions:Z

.field mHavePendingDeferredStart:Z

.field mNeedMenuInvalidate:Z

.field mNoTransactionsBecause:Ljava/lang/String;

.field mParent:Landroid/support/v4/app/Fragment;

.field mPendingActions:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/Runnable;",
            ">;"
        }
    .end annotation
.end field

.field mStateArray:Landroid/util/SparseArray;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/util/SparseArray",
            "<",
            "Landroid/os/Parcelable;",
            ">;"
        }
    .end annotation
.end field

.field mStateBundle:Landroid/os/Bundle;

.field mStateSaved:Z

.field mTmpActions:[Ljava/lang/Runnable;


# direct methods
.method static constructor <clinit>()V
    .locals 5

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    const/high16 v4, 0x40200000    # 2.5f

    #@3
    const/high16 v3, 0x3fc00000    # 1.5f

    #@5
    .line 410
    sput-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@7
    .line 413
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@9
    const/16 v2, 0xb

    #@b
    if-lt v1, v2, :cond_0

    #@d
    const/4 v0, 0x1

    #@e
    :cond_0
    sput-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->HONEYCOMB:Z

    #@10
    .line 746
    new-instance v0, Landroid/view/animation/DecelerateInterpolator;

    #@12
    invoke-direct {v0, v4}, Landroid/view/animation/DecelerateInterpolator;-><init>(F)V

    #@15
    sput-object v0, Landroid/support/v4/app/FragmentManagerImpl;->DECELERATE_QUINT:Landroid/view/animation/Interpolator;

    #@17
    .line 747
    new-instance v0, Landroid/view/animation/DecelerateInterpolator;

    #@19
    invoke-direct {v0, v3}, Landroid/view/animation/DecelerateInterpolator;-><init>(F)V

    #@1c
    sput-object v0, Landroid/support/v4/app/FragmentManagerImpl;->DECELERATE_CUBIC:Landroid/view/animation/Interpolator;

    #@1e
    .line 748
    new-instance v0, Landroid/view/animation/AccelerateInterpolator;

    #@20
    invoke-direct {v0, v4}, Landroid/view/animation/AccelerateInterpolator;-><init>(F)V

    #@23
    sput-object v0, Landroid/support/v4/app/FragmentManagerImpl;->ACCELERATE_QUINT:Landroid/view/animation/Interpolator;

    #@25
    .line 749
    new-instance v0, Landroid/view/animation/AccelerateInterpolator;

    #@27
    invoke-direct {v0, v3}, Landroid/view/animation/AccelerateInterpolator;-><init>(F)V

    #@2a
    sput-object v0, Landroid/support/v4/app/FragmentManagerImpl;->ACCELERATE_CUBIC:Landroid/view/animation/Interpolator;

    #@2c
    return-void
.end method

.method constructor <init>()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 409
    invoke-direct {p0}, Landroid/support/v4/app/FragmentManager;-><init>()V

    #@4
    .line 436
    const/4 v0, 0x0

    #@5
    iput v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCurState:I

    #@7
    .line 448
    iput-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateBundle:Landroid/os/Bundle;

    #@9
    .line 449
    iput-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateArray:Landroid/util/SparseArray;

    #@b
    .line 451
    new-instance v0, Landroid/support/v4/app/FragmentManagerImpl$1;

    #@d
    invoke-direct {v0, p0}, Landroid/support/v4/app/FragmentManagerImpl$1;-><init>(Landroid/support/v4/app/FragmentManagerImpl;)V

    #@10
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mExecCommit:Ljava/lang/Runnable;

    #@12
    .line 2205
    return-void
.end method

.method private checkStateLoss()V
    .locals 3

    #@0
    .prologue
    .line 1364
    iget-boolean v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateSaved:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1365
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "Can not perform this action after onSaveInstanceState"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 1368
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNoTransactionsBecause:Ljava/lang/String;

    #@e
    if-eqz v0, :cond_1

    #@10
    .line 1369
    new-instance v0, Ljava/lang/IllegalStateException;

    #@12
    new-instance v1, Ljava/lang/StringBuilder;

    #@14
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@17
    const-string v2, "Can not perform this action inside of "

    #@19
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v1

    #@1d
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNoTransactionsBecause:Ljava/lang/String;

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
    .line 1372
    :cond_1
    return-void
.end method

.method static makeFadeAnimation(Landroid/content/Context;FF)Landroid/view/animation/Animation;
    .locals 4
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "start"    # F
    .param p2, "end"    # F

    #@0
    .prologue
    .line 769
    new-instance v0, Landroid/view/animation/AlphaAnimation;

    #@2
    invoke-direct {v0, p1, p2}, Landroid/view/animation/AlphaAnimation;-><init>(FF)V

    #@5
    .line 770
    .local v0, "anim":Landroid/view/animation/AlphaAnimation;
    sget-object v1, Landroid/support/v4/app/FragmentManagerImpl;->DECELERATE_CUBIC:Landroid/view/animation/Interpolator;

    #@7
    invoke-virtual {v0, v1}, Landroid/view/animation/AlphaAnimation;->setInterpolator(Landroid/view/animation/Interpolator;)V

    #@a
    .line 771
    const-wide/16 v2, 0xdc

    #@c
    invoke-virtual {v0, v2, v3}, Landroid/view/animation/AlphaAnimation;->setDuration(J)V

    #@f
    .line 772
    return-object v0
.end method

.method static makeOpenCloseAnimation(Landroid/content/Context;FFFF)Landroid/view/animation/Animation;
    .locals 11
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "startScale"    # F
    .param p2, "endScale"    # F
    .param p3, "startAlpha"    # F
    .param p4, "endAlpha"    # F

    #@0
    .prologue
    .line 755
    new-instance v10, Landroid/view/animation/AnimationSet;

    #@2
    const/4 v1, 0x0

    #@3
    invoke-direct {v10, v1}, Landroid/view/animation/AnimationSet;-><init>(Z)V

    #@6
    .line 756
    .local v10, "set":Landroid/view/animation/AnimationSet;
    new-instance v0, Landroid/view/animation/ScaleAnimation;

    #@8
    const/4 v5, 0x1

    #@9
    const/high16 v6, 0x3f000000    # 0.5f

    #@b
    const/4 v7, 0x1

    #@c
    const/high16 v8, 0x3f000000    # 0.5f

    #@e
    move v1, p1

    #@f
    move v2, p2

    #@10
    move v3, p1

    #@11
    move v4, p2

    #@12
    invoke-direct/range {v0 .. v8}, Landroid/view/animation/ScaleAnimation;-><init>(FFFFIFIF)V

    #@15
    .line 758
    .local v0, "scale":Landroid/view/animation/ScaleAnimation;
    sget-object v1, Landroid/support/v4/app/FragmentManagerImpl;->DECELERATE_QUINT:Landroid/view/animation/Interpolator;

    #@17
    invoke-virtual {v0, v1}, Landroid/view/animation/ScaleAnimation;->setInterpolator(Landroid/view/animation/Interpolator;)V

    #@1a
    .line 759
    const-wide/16 v2, 0xdc

    #@1c
    invoke-virtual {v0, v2, v3}, Landroid/view/animation/ScaleAnimation;->setDuration(J)V

    #@1f
    .line 760
    invoke-virtual {v10, v0}, Landroid/view/animation/AnimationSet;->addAnimation(Landroid/view/animation/Animation;)V

    #@22
    .line 761
    new-instance v9, Landroid/view/animation/AlphaAnimation;

    #@24
    invoke-direct {v9, p3, p4}, Landroid/view/animation/AlphaAnimation;-><init>(FF)V

    #@27
    .line 762
    .local v9, "alpha":Landroid/view/animation/AlphaAnimation;
    sget-object v1, Landroid/support/v4/app/FragmentManagerImpl;->DECELERATE_CUBIC:Landroid/view/animation/Interpolator;

    #@29
    invoke-virtual {v9, v1}, Landroid/view/animation/AlphaAnimation;->setInterpolator(Landroid/view/animation/Interpolator;)V

    #@2c
    .line 763
    const-wide/16 v2, 0xdc

    #@2e
    invoke-virtual {v9, v2, v3}, Landroid/view/animation/AlphaAnimation;->setDuration(J)V

    #@31
    .line 764
    invoke-virtual {v10, v9}, Landroid/view/animation/AnimationSet;->addAnimation(Landroid/view/animation/Animation;)V

    #@34
    .line 765
    return-object v10
.end method

.method public static reverseTransit(I)I
    .locals 1
    .param p0, "transit"    # I

    #@0
    .prologue
    .line 2069
    const/4 v0, 0x0

    #@1
    .line 2070
    .local v0, "rev":I
    sparse-switch p0, :sswitch_data_0

    #@4
    .line 2081
    :goto_0
    return v0

    #@5
    .line 2072
    :sswitch_0
    const/16 v0, 0x2002

    #@7
    .line 2073
    goto :goto_0

    #@8
    .line 2075
    :sswitch_1
    const/16 v0, 0x1001

    #@a
    .line 2076
    goto :goto_0

    #@b
    .line 2078
    :sswitch_2
    const/16 v0, 0x1003

    #@d
    goto :goto_0

    #@e
    .line 2070
    :sswitch_data_0
    .sparse-switch
        0x1001 -> :sswitch_0
        0x1003 -> :sswitch_2
        0x2002 -> :sswitch_1
    .end sparse-switch
.end method

.method private throwException(Ljava/lang/RuntimeException;)V
    .locals 7
    .param p1, "ex"    # Ljava/lang/RuntimeException;

    #@0
    .prologue
    .line 459
    const-string v3, "FragmentManager"

    #@2
    invoke-virtual {p1}, Ljava/lang/RuntimeException;->getMessage()Ljava/lang/String;

    #@5
    move-result-object v4

    #@6
    invoke-static {v3, v4}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@9
    .line 460
    const-string v3, "FragmentManager"

    #@b
    const-string v4, "Activity state:"

    #@d
    invoke-static {v3, v4}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@10
    .line 461
    new-instance v1, Landroid/support/v4/util/LogWriter;

    #@12
    const-string v3, "FragmentManager"

    #@14
    invoke-direct {v1, v3}, Landroid/support/v4/util/LogWriter;-><init>(Ljava/lang/String;)V

    #@17
    .line 462
    .local v1, "logw":Landroid/support/v4/util/LogWriter;
    new-instance v2, Ljava/io/PrintWriter;

    #@19
    invoke-direct {v2, v1}, Ljava/io/PrintWriter;-><init>(Ljava/io/Writer;)V

    #@1c
    .line 463
    .local v2, "pw":Ljava/io/PrintWriter;
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@1e
    if-eqz v3, :cond_0

    #@20
    .line 465
    :try_start_0
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@22
    const-string v4, "  "

    #@24
    const/4 v5, 0x0

    #@25
    const/4 v6, 0x0

    #@26
    new-array v6, v6, [Ljava/lang/String;

    #@28
    invoke-virtual {v3, v4, v5, v2, v6}, Landroid/support/v4/app/FragmentActivity;->dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@2b
    .line 476
    :goto_0
    throw p1

    #@2c
    .line 466
    :catch_0
    move-exception v0

    #@2d
    .line 467
    .local v0, "e":Ljava/lang/Exception;
    const-string v3, "FragmentManager"

    #@2f
    const-string v4, "Failed dumping state"

    #@31
    invoke-static {v3, v4, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@34
    goto :goto_0

    #@35
    .line 471
    .end local v0    # "e":Ljava/lang/Exception;
    :cond_0
    :try_start_1
    const-string v3, "  "

    #@37
    const/4 v4, 0x0

    #@38
    const/4 v5, 0x0

    #@39
    new-array v5, v5, [Ljava/lang/String;

    #@3b
    invoke-virtual {p0, v3, v4, v2, v5}, Landroid/support/v4/app/FragmentManagerImpl;->dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    #@3e
    goto :goto_0

    #@3f
    .line 472
    :catch_1
    move-exception v0

    #@40
    .line 473
    .restart local v0    # "e":Ljava/lang/Exception;
    const-string v3, "FragmentManager"

    #@42
    const-string v4, "Failed dumping state"

    #@44
    invoke-static {v3, v4, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@47
    goto :goto_0
.end method

.method public static transitToStyleIndex(IZ)I
    .locals 1
    .param p0, "transit"    # I
    .param p1, "enter"    # Z

    #@0
    .prologue
    .line 2093
    const/4 v0, -0x1

    #@1
    .line 2094
    .local v0, "animAttr":I
    sparse-switch p0, :sswitch_data_0

    #@4
    .line 2105
    :goto_0
    return v0

    #@5
    .line 2096
    :sswitch_0
    if-eqz p1, :cond_0

    #@7
    const/4 v0, 0x1

    #@8
    .line 2097
    :goto_1
    goto :goto_0

    #@9
    .line 2096
    :cond_0
    const/4 v0, 0x2

    #@a
    goto :goto_1

    #@b
    .line 2099
    :sswitch_1
    if-eqz p1, :cond_1

    #@d
    const/4 v0, 0x3

    #@e
    .line 2100
    :goto_2
    goto :goto_0

    #@f
    .line 2099
    :cond_1
    const/4 v0, 0x4

    #@10
    goto :goto_2

    #@11
    .line 2102
    :sswitch_2
    if-eqz p1, :cond_2

    #@13
    const/4 v0, 0x5

    #@14
    :goto_3
    goto :goto_0

    #@15
    :cond_2
    const/4 v0, 0x6

    #@16
    goto :goto_3

    #@17
    .line 2094
    nop

    #@18
    :sswitch_data_0
    .sparse-switch
        0x1001 -> :sswitch_0
        0x1003 -> :sswitch_2
        0x2002 -> :sswitch_1
    .end sparse-switch
.end method


# virtual methods
.method addBackStackState(Landroid/support/v4/app/BackStackRecord;)V
    .locals 1
    .param p1, "state"    # Landroid/support/v4/app/BackStackRecord;

    #@0
    .prologue
    .line 1521
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 1522
    new-instance v0, Ljava/util/ArrayList;

    #@6
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@9
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@b
    .line 1524
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@d
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@10
    .line 1525
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentManagerImpl;->reportBackStackChanged()V

    #@13
    .line 1526
    return-void
.end method

.method public addFragment(Landroid/support/v4/app/Fragment;Z)V
    .locals 4
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;
    .param p2, "moveToStateNow"    # Z

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 1190
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@3
    if-nez v0, :cond_0

    #@5
    .line 1191
    new-instance v0, Ljava/util/ArrayList;

    #@7
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@a
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@c
    .line 1193
    :cond_0
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@e
    if-eqz v0, :cond_1

    #@10
    const-string v0, "FragmentManager"

    #@12
    new-instance v1, Ljava/lang/StringBuilder;

    #@14
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@17
    const-string v2, "add: "

    #@19
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v1

    #@1d
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@20
    move-result-object v1

    #@21
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@24
    move-result-object v1

    #@25
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@28
    .line 1194
    :cond_1
    invoke-virtual {p0, p1}, Landroid/support/v4/app/FragmentManagerImpl;->makeActive(Landroid/support/v4/app/Fragment;)V

    #@2b
    .line 1195
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mDetached:Z

    #@2d
    if-nez v0, :cond_4

    #@2f
    .line 1196
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@31
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    #@34
    move-result v0

    #@35
    if-eqz v0, :cond_2

    #@37
    .line 1197
    new-instance v0, Ljava/lang/IllegalStateException;

    #@39
    new-instance v1, Ljava/lang/StringBuilder;

    #@3b
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@3e
    const-string v2, "Fragment already added: "

    #@40
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@43
    move-result-object v1

    #@44
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@47
    move-result-object v1

    #@48
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@4b
    move-result-object v1

    #@4c
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@4f
    throw v0

    #@50
    .line 1199
    :cond_2
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@52
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@55
    .line 1200
    iput-boolean v3, p1, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@57
    .line 1201
    const/4 v0, 0x0

    #@58
    iput-boolean v0, p1, Landroid/support/v4/app/Fragment;->mRemoving:Z

    #@5a
    .line 1202
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@5c
    if-eqz v0, :cond_3

    #@5e
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@60
    if-eqz v0, :cond_3

    #@62
    .line 1203
    iput-boolean v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNeedMenuInvalidate:Z

    #@64
    .line 1205
    :cond_3
    if-eqz p2, :cond_4

    #@66
    .line 1206
    invoke-virtual {p0, p1}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(Landroid/support/v4/app/Fragment;)V

    #@69
    .line 1209
    :cond_4
    return-void
.end method

.method public addOnBackStackChangedListener(Landroid/support/v4/app/FragmentManager$OnBackStackChangedListener;)V
    .locals 1
    .param p1, "listener"    # Landroid/support/v4/app/FragmentManager$OnBackStackChangedListener;

    #@0
    .prologue
    .line 555
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackChangeListeners:Ljava/util/ArrayList;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 556
    new-instance v0, Ljava/util/ArrayList;

    #@6
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@9
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackChangeListeners:Ljava/util/ArrayList;

    #@b
    .line 558
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackChangeListeners:Ljava/util/ArrayList;

    #@d
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@10
    .line 559
    return-void
.end method

.method public allocBackStackIndex(Landroid/support/v4/app/BackStackRecord;)I
    .locals 5
    .param p1, "bse"    # Landroid/support/v4/app/BackStackRecord;

    #@0
    .prologue
    .line 1401
    monitor-enter p0

    #@1
    .line 1402
    :try_start_0
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailBackStackIndices:Ljava/util/ArrayList;

    #@3
    if-eqz v2, :cond_0

    #@5
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailBackStackIndices:Ljava/util/ArrayList;

    #@7
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@a
    move-result v2

    #@b
    if-gtz v2, :cond_3

    #@d
    .line 1403
    :cond_0
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@f
    if-nez v2, :cond_1

    #@11
    .line 1404
    new-instance v2, Ljava/util/ArrayList;

    #@13
    invoke-direct {v2}, Ljava/util/ArrayList;-><init>()V

    #@16
    iput-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@18
    .line 1406
    :cond_1
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@1a
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@1d
    move-result v0

    #@1e
    .line 1407
    .local v0, "index":I
    sget-boolean v2, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@20
    if-eqz v2, :cond_2

    #@22
    const-string v2, "FragmentManager"

    #@24
    new-instance v3, Ljava/lang/StringBuilder;

    #@26
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@29
    const-string v4, "Setting back stack index "

    #@2b
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2e
    move-result-object v3

    #@2f
    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@32
    move-result-object v3

    #@33
    const-string v4, " to "

    #@35
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@38
    move-result-object v3

    #@39
    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@3c
    move-result-object v3

    #@3d
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@40
    move-result-object v3

    #@41
    invoke-static {v2, v3}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@44
    .line 1408
    :cond_2
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@46
    invoke-virtual {v2, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@49
    .line 1409
    monitor-exit p0

    #@4a
    move v1, v0

    #@4b
    .line 1415
    .end local v0    # "index":I
    .local v1, "index":I
    :goto_0
    return v1

    #@4c
    .line 1412
    .end local v1    # "index":I
    :cond_3
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailBackStackIndices:Ljava/util/ArrayList;

    #@4e
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailBackStackIndices:Ljava/util/ArrayList;

    #@50
    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    #@53
    move-result v3

    #@54
    add-int/lit8 v3, v3, -0x1

    #@56
    invoke-virtual {v2, v3}, Ljava/util/ArrayList;->remove(I)Ljava/lang/Object;

    #@59
    move-result-object v2

    #@5a
    check-cast v2, Ljava/lang/Integer;

    #@5c
    invoke-virtual {v2}, Ljava/lang/Integer;->intValue()I

    #@5f
    move-result v0

    #@60
    .line 1413
    .restart local v0    # "index":I
    sget-boolean v2, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@62
    if-eqz v2, :cond_4

    #@64
    const-string v2, "FragmentManager"

    #@66
    new-instance v3, Ljava/lang/StringBuilder;

    #@68
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@6b
    const-string v4, "Adding back stack index "

    #@6d
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@70
    move-result-object v3

    #@71
    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@74
    move-result-object v3

    #@75
    const-string v4, " with "

    #@77
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@7a
    move-result-object v3

    #@7b
    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@7e
    move-result-object v3

    #@7f
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@82
    move-result-object v3

    #@83
    invoke-static {v2, v3}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@86
    .line 1414
    :cond_4
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@88
    invoke-virtual {v2, v0, p1}, Ljava/util/ArrayList;->set(ILjava/lang/Object;)Ljava/lang/Object;

    #@8b
    .line 1415
    monitor-exit p0

    #@8c
    move v1, v0

    #@8d
    .end local v0    # "index":I
    .restart local v1    # "index":I
    goto :goto_0

    #@8e
    .line 1417
    .end local v1    # "index":I
    :catchall_0
    move-exception v2

    #@8f
    monitor-exit p0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@90
    throw v2
.end method

.method public attachActivity(Landroid/support/v4/app/FragmentActivity;Landroid/support/v4/app/FragmentContainer;Landroid/support/v4/app/Fragment;)V
    .locals 2
    .param p1, "activity"    # Landroid/support/v4/app/FragmentActivity;
    .param p2, "container"    # Landroid/support/v4/app/FragmentContainer;
    .param p3, "parent"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 1900
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@2
    if-eqz v0, :cond_0

    #@4
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "Already attached"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 1901
    :cond_0
    iput-object p1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@e
    .line 1902
    iput-object p2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mContainer:Landroid/support/v4/app/FragmentContainer;

    #@10
    .line 1903
    iput-object p3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mParent:Landroid/support/v4/app/Fragment;

    #@12
    .line 1904
    return-void
.end method

.method public attachFragment(Landroid/support/v4/app/Fragment;II)V
    .locals 6
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;
    .param p2, "transition"    # I
    .param p3, "transitionStyle"    # I

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    const/4 v5, 0x0

    #@2
    .line 1286
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@4
    if-eqz v0, :cond_0

    #@6
    const-string v0, "FragmentManager"

    #@8
    new-instance v1, Ljava/lang/StringBuilder;

    #@a
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@d
    const-string v2, "attach: "

    #@f
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v1

    #@13
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@16
    move-result-object v1

    #@17
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1a
    move-result-object v1

    #@1b
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@1e
    .line 1287
    :cond_0
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mDetached:Z

    #@20
    if-eqz v0, :cond_5

    #@22
    .line 1288
    iput-boolean v5, p1, Landroid/support/v4/app/Fragment;->mDetached:Z

    #@24
    .line 1289
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@26
    if-nez v0, :cond_5

    #@28
    .line 1290
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@2a
    if-nez v0, :cond_1

    #@2c
    .line 1291
    new-instance v0, Ljava/util/ArrayList;

    #@2e
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@31
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@33
    .line 1293
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@35
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    #@38
    move-result v0

    #@39
    if-eqz v0, :cond_2

    #@3b
    .line 1294
    new-instance v0, Ljava/lang/IllegalStateException;

    #@3d
    new-instance v1, Ljava/lang/StringBuilder;

    #@3f
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@42
    const-string v2, "Fragment already added: "

    #@44
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@47
    move-result-object v1

    #@48
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@4b
    move-result-object v1

    #@4c
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@4f
    move-result-object v1

    #@50
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@53
    throw v0

    #@54
    .line 1296
    :cond_2
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@56
    if-eqz v0, :cond_3

    #@58
    const-string v0, "FragmentManager"

    #@5a
    new-instance v1, Ljava/lang/StringBuilder;

    #@5c
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@5f
    const-string v2, "add from attach: "

    #@61
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@64
    move-result-object v1

    #@65
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@68
    move-result-object v1

    #@69
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@6c
    move-result-object v1

    #@6d
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@70
    .line 1297
    :cond_3
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@72
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@75
    .line 1298
    iput-boolean v3, p1, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@77
    .line 1299
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@79
    if-eqz v0, :cond_4

    #@7b
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@7d
    if-eqz v0, :cond_4

    #@7f
    .line 1300
    iput-boolean v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNeedMenuInvalidate:Z

    #@81
    .line 1302
    :cond_4
    iget v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCurState:I

    #@83
    move-object v0, p0

    #@84
    move-object v1, p1

    #@85
    move v3, p2

    #@86
    move v4, p3

    #@87
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(Landroid/support/v4/app/Fragment;IIIZ)V

    #@8a
    .line 1305
    :cond_5
    return-void
.end method

.method public beginTransaction()Landroid/support/v4/app/FragmentTransaction;
    .locals 1

    #@0
    .prologue
    .line 481
    new-instance v0, Landroid/support/v4/app/BackStackRecord;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v4/app/BackStackRecord;-><init>(Landroid/support/v4/app/FragmentManagerImpl;)V

    #@5
    return-object v0
.end method

.method public detachFragment(Landroid/support/v4/app/Fragment;II)V
    .locals 6
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;
    .param p2, "transition"    # I
    .param p3, "transitionStyle"    # I

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    const/4 v2, 0x1

    #@2
    .line 1267
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@4
    if-eqz v0, :cond_0

    #@6
    const-string v0, "FragmentManager"

    #@8
    new-instance v1, Ljava/lang/StringBuilder;

    #@a
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@d
    const-string v3, "detach: "

    #@f
    invoke-virtual {v1, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v1

    #@13
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@16
    move-result-object v1

    #@17
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1a
    move-result-object v1

    #@1b
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@1e
    .line 1268
    :cond_0
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mDetached:Z

    #@20
    if-nez v0, :cond_4

    #@22
    .line 1269
    iput-boolean v2, p1, Landroid/support/v4/app/Fragment;->mDetached:Z

    #@24
    .line 1270
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@26
    if-eqz v0, :cond_4

    #@28
    .line 1272
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@2a
    if-eqz v0, :cond_2

    #@2c
    .line 1273
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@2e
    if-eqz v0, :cond_1

    #@30
    const-string v0, "FragmentManager"

    #@32
    new-instance v1, Ljava/lang/StringBuilder;

    #@34
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@37
    const-string v3, "remove from detach: "

    #@39
    invoke-virtual {v1, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@3c
    move-result-object v1

    #@3d
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@40
    move-result-object v1

    #@41
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@44
    move-result-object v1

    #@45
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@48
    .line 1274
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@4a
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    #@4d
    .line 1276
    :cond_2
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@4f
    if-eqz v0, :cond_3

    #@51
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@53
    if-eqz v0, :cond_3

    #@55
    .line 1277
    iput-boolean v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNeedMenuInvalidate:Z

    #@57
    .line 1279
    :cond_3
    iput-boolean v5, p1, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@59
    move-object v0, p0

    #@5a
    move-object v1, p1

    #@5b
    move v3, p2

    #@5c
    move v4, p3

    #@5d
    .line 1280
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(Landroid/support/v4/app/Fragment;IIIZ)V

    #@60
    .line 1283
    :cond_4
    return-void
.end method

.method public dispatchActivityCreated()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 1916
    iput-boolean v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateSaved:Z

    #@3
    .line 1917
    const/4 v0, 0x2

    #@4
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(IZ)V

    #@7
    .line 1918
    return-void
.end method

.method public dispatchConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 3
    .param p1, "newConfig"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    .line 1961
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@2
    if-eqz v2, :cond_1

    #@4
    .line 1962
    const/4 v1, 0x0

    #@5
    .local v1, "i":I
    :goto_0
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@7
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@a
    move-result v2

    #@b
    if-ge v1, v2, :cond_1

    #@d
    .line 1963
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@f
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@12
    move-result-object v0

    #@13
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@15
    .line 1964
    .local v0, "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_0

    #@17
    .line 1965
    invoke-virtual {v0, p1}, Landroid/support/v4/app/Fragment;->performConfigurationChanged(Landroid/content/res/Configuration;)V

    #@1a
    .line 1962
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@1c
    goto :goto_0

    #@1d
    .line 1969
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_1
    return-void
.end method

.method public dispatchContextItemSelected(Landroid/view/MenuItem;)Z
    .locals 3
    .param p1, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 2044
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@2
    if-eqz v2, :cond_1

    #@4
    .line 2045
    const/4 v1, 0x0

    #@5
    .local v1, "i":I
    :goto_0
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@7
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@a
    move-result v2

    #@b
    if-ge v1, v2, :cond_1

    #@d
    .line 2046
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@f
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@12
    move-result-object v0

    #@13
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@15
    .line 2047
    .local v0, "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_0

    #@17
    .line 2048
    invoke-virtual {v0, p1}, Landroid/support/v4/app/Fragment;->performContextItemSelected(Landroid/view/MenuItem;)Z

    #@1a
    move-result v2

    #@1b
    if-eqz v2, :cond_0

    #@1d
    .line 2049
    const/4 v2, 0x1

    #@1e
    .line 2054
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :goto_1
    return v2

    #@1f
    .line 2045
    .restart local v0    # "f":Landroid/support/v4/app/Fragment;
    .restart local v1    # "i":I
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@21
    goto :goto_0

    #@22
    .line 2054
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_1
    const/4 v2, 0x0

    #@23
    goto :goto_1
.end method

.method public dispatchCreate()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 1911
    iput-boolean v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateSaved:Z

    #@3
    .line 1912
    const/4 v0, 0x1

    #@4
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(IZ)V

    #@7
    .line 1913
    return-void
.end method

.method public dispatchCreateOptionsMenu(Landroid/view/Menu;Landroid/view/MenuInflater;)Z
    .locals 5
    .param p1, "menu"    # Landroid/view/Menu;
    .param p2, "inflater"    # Landroid/view/MenuInflater;

    #@0
    .prologue
    .line 1983
    const/4 v3, 0x0

    #@1
    .line 1984
    .local v3, "show":Z
    const/4 v2, 0x0

    #@2
    .line 1985
    .local v2, "newMenus":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v4/app/Fragment;>;"
    iget-object v4, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@4
    if-eqz v4, :cond_2

    #@6
    .line 1986
    const/4 v1, 0x0

    #@7
    .local v1, "i":I
    :goto_0
    iget-object v4, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@9
    invoke-virtual {v4}, Ljava/util/ArrayList;->size()I

    #@c
    move-result v4

    #@d
    if-ge v1, v4, :cond_2

    #@f
    .line 1987
    iget-object v4, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@11
    invoke-virtual {v4, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@14
    move-result-object v0

    #@15
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@17
    .line 1988
    .local v0, "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_1

    #@19
    .line 1989
    invoke-virtual {v0, p1, p2}, Landroid/support/v4/app/Fragment;->performCreateOptionsMenu(Landroid/view/Menu;Landroid/view/MenuInflater;)Z

    #@1c
    move-result v4

    #@1d
    if-eqz v4, :cond_1

    #@1f
    .line 1990
    const/4 v3, 0x1

    #@20
    .line 1991
    if-nez v2, :cond_0

    #@22
    .line 1992
    new-instance v2, Ljava/util/ArrayList;

    #@24
    .end local v2    # "newMenus":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v4/app/Fragment;>;"
    invoke-direct {v2}, Ljava/util/ArrayList;-><init>()V

    #@27
    .line 1994
    .restart local v2    # "newMenus":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v4/app/Fragment;>;"
    :cond_0
    invoke-virtual {v2, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@2a
    .line 1986
    :cond_1
    add-int/lit8 v1, v1, 0x1

    #@2c
    goto :goto_0

    #@2d
    .line 2000
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_2
    iget-object v4, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCreatedMenus:Ljava/util/ArrayList;

    #@2f
    if-eqz v4, :cond_5

    #@31
    .line 2001
    const/4 v1, 0x0

    #@32
    .restart local v1    # "i":I
    :goto_1
    iget-object v4, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCreatedMenus:Ljava/util/ArrayList;

    #@34
    invoke-virtual {v4}, Ljava/util/ArrayList;->size()I

    #@37
    move-result v4

    #@38
    if-ge v1, v4, :cond_5

    #@3a
    .line 2002
    iget-object v4, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCreatedMenus:Ljava/util/ArrayList;

    #@3c
    invoke-virtual {v4, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@3f
    move-result-object v0

    #@40
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@42
    .line 2003
    .restart local v0    # "f":Landroid/support/v4/app/Fragment;
    if-eqz v2, :cond_3

    #@44
    invoke-virtual {v2, v0}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    #@47
    move-result v4

    #@48
    if-nez v4, :cond_4

    #@4a
    .line 2004
    :cond_3
    invoke-virtual {v0}, Landroid/support/v4/app/Fragment;->onDestroyOptionsMenu()V

    #@4d
    .line 2001
    :cond_4
    add-int/lit8 v1, v1, 0x1

    #@4f
    goto :goto_1

    #@50
    .line 2009
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_5
    iput-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCreatedMenus:Ljava/util/ArrayList;

    #@52
    .line 2011
    return v3
.end method

.method public dispatchDestroy()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    const/4 v1, 0x0

    #@2
    .line 1952
    const/4 v0, 0x1

    #@3
    iput-boolean v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mDestroyed:Z

    #@5
    .line 1953
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentManagerImpl;->execPendingActions()Z

    #@8
    .line 1954
    invoke-virtual {p0, v2, v2}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(IZ)V

    #@b
    .line 1955
    iput-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@d
    .line 1956
    iput-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mContainer:Landroid/support/v4/app/FragmentContainer;

    #@f
    .line 1957
    iput-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mParent:Landroid/support/v4/app/Fragment;

    #@11
    .line 1958
    return-void
.end method

.method public dispatchDestroyView()V
    .locals 2

    #@0
    .prologue
    .line 1948
    const/4 v0, 0x1

    #@1
    const/4 v1, 0x0

    #@2
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(IZ)V

    #@5
    .line 1949
    return-void
.end method

.method public dispatchLowMemory()V
    .locals 3

    #@0
    .prologue
    .line 1972
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@2
    if-eqz v2, :cond_1

    #@4
    .line 1973
    const/4 v1, 0x0

    #@5
    .local v1, "i":I
    :goto_0
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@7
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@a
    move-result v2

    #@b
    if-ge v1, v2, :cond_1

    #@d
    .line 1974
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@f
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@12
    move-result-object v0

    #@13
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@15
    .line 1975
    .local v0, "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_0

    #@17
    .line 1976
    invoke-virtual {v0}, Landroid/support/v4/app/Fragment;->performLowMemory()V

    #@1a
    .line 1973
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@1c
    goto :goto_0

    #@1d
    .line 1980
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_1
    return-void
.end method

.method public dispatchOptionsItemSelected(Landroid/view/MenuItem;)Z
    .locals 3
    .param p1, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 2030
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@2
    if-eqz v2, :cond_1

    #@4
    .line 2031
    const/4 v1, 0x0

    #@5
    .local v1, "i":I
    :goto_0
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@7
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@a
    move-result v2

    #@b
    if-ge v1, v2, :cond_1

    #@d
    .line 2032
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@f
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@12
    move-result-object v0

    #@13
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@15
    .line 2033
    .local v0, "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_0

    #@17
    .line 2034
    invoke-virtual {v0, p1}, Landroid/support/v4/app/Fragment;->performOptionsItemSelected(Landroid/view/MenuItem;)Z

    #@1a
    move-result v2

    #@1b
    if-eqz v2, :cond_0

    #@1d
    .line 2035
    const/4 v2, 0x1

    #@1e
    .line 2040
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :goto_1
    return v2

    #@1f
    .line 2031
    .restart local v0    # "f":Landroid/support/v4/app/Fragment;
    .restart local v1    # "i":I
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@21
    goto :goto_0

    #@22
    .line 2040
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_1
    const/4 v2, 0x0

    #@23
    goto :goto_1
.end method

.method public dispatchOptionsMenuClosed(Landroid/view/Menu;)V
    .locals 3
    .param p1, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 2058
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@2
    if-eqz v2, :cond_1

    #@4
    .line 2059
    const/4 v1, 0x0

    #@5
    .local v1, "i":I
    :goto_0
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@7
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@a
    move-result v2

    #@b
    if-ge v1, v2, :cond_1

    #@d
    .line 2060
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@f
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@12
    move-result-object v0

    #@13
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@15
    .line 2061
    .local v0, "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_0

    #@17
    .line 2062
    invoke-virtual {v0, p1}, Landroid/support/v4/app/Fragment;->performOptionsMenuClosed(Landroid/view/Menu;)V

    #@1a
    .line 2059
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@1c
    goto :goto_0

    #@1d
    .line 2066
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_1
    return-void
.end method

.method public dispatchPause()V
    .locals 2

    #@0
    .prologue
    .line 1931
    const/4 v0, 0x4

    #@1
    const/4 v1, 0x0

    #@2
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(IZ)V

    #@5
    .line 1932
    return-void
.end method

.method public dispatchPrepareOptionsMenu(Landroid/view/Menu;)Z
    .locals 4
    .param p1, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 2015
    const/4 v2, 0x0

    #@1
    .line 2016
    .local v2, "show":Z
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@3
    if-eqz v3, :cond_1

    #@5
    .line 2017
    const/4 v1, 0x0

    #@6
    .local v1, "i":I
    :goto_0
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@8
    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    #@b
    move-result v3

    #@c
    if-ge v1, v3, :cond_1

    #@e
    .line 2018
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@10
    invoke-virtual {v3, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@13
    move-result-object v0

    #@14
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@16
    .line 2019
    .local v0, "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_0

    #@18
    .line 2020
    invoke-virtual {v0, p1}, Landroid/support/v4/app/Fragment;->performPrepareOptionsMenu(Landroid/view/Menu;)Z

    #@1b
    move-result v3

    #@1c
    if-eqz v3, :cond_0

    #@1e
    .line 2021
    const/4 v2, 0x1

    #@1f
    .line 2017
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@21
    goto :goto_0

    #@22
    .line 2026
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_1
    return v2
.end method

.method public dispatchReallyStop()V
    .locals 2

    #@0
    .prologue
    .line 1944
    const/4 v0, 0x2

    #@1
    const/4 v1, 0x0

    #@2
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(IZ)V

    #@5
    .line 1945
    return-void
.end method

.method public dispatchResume()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 1926
    iput-boolean v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateSaved:Z

    #@3
    .line 1927
    const/4 v0, 0x5

    #@4
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(IZ)V

    #@7
    .line 1928
    return-void
.end method

.method public dispatchStart()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 1921
    iput-boolean v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateSaved:Z

    #@3
    .line 1922
    const/4 v0, 0x4

    #@4
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(IZ)V

    #@7
    .line 1923
    return-void
.end method

.method public dispatchStop()V
    .locals 2

    #@0
    .prologue
    .line 1938
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateSaved:Z

    #@3
    .line 1940
    const/4 v0, 0x3

    #@4
    const/4 v1, 0x0

    #@5
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(IZ)V

    #@8
    .line 1941
    return-void
.end method

.method public dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V
    .locals 8
    .param p1, "prefix"    # Ljava/lang/String;
    .param p2, "fd"    # Ljava/io/FileDescriptor;
    .param p3, "writer"    # Ljava/io/PrintWriter;
    .param p4, "args"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 635
    new-instance v6, Ljava/lang/StringBuilder;

    #@2
    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    #@5
    invoke-virtual {v6, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@8
    move-result-object v6

    #@9
    const-string v7, "    "

    #@b
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@e
    move-result-object v6

    #@f
    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@12
    move-result-object v4

    #@13
    .line 638
    .local v4, "innerPrefix":Ljava/lang/String;
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@15
    if-eqz v6, :cond_1

    #@17
    .line 639
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@19
    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    #@1c
    move-result v0

    #@1d
    .line 640
    .local v0, "N":I
    if-lez v0, :cond_1

    #@1f
    .line 641
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@22
    const-string v6, "Active Fragments in "

    #@24
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@27
    .line 642
    invoke-static {p0}, Ljava/lang/System;->identityHashCode(Ljava/lang/Object;)I

    #@2a
    move-result v6

    #@2b
    invoke-static {v6}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@2e
    move-result-object v6

    #@2f
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@32
    .line 643
    const-string v6, ":"

    #@34
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@37
    .line 644
    const/4 v3, 0x0

    #@38
    .local v3, "i":I
    :goto_0
    if-ge v3, v0, :cond_1

    #@3a
    .line 645
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@3c
    invoke-virtual {v6, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@3f
    move-result-object v2

    #@40
    check-cast v2, Landroid/support/v4/app/Fragment;

    #@42
    .line 646
    .local v2, "f":Landroid/support/v4/app/Fragment;
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@45
    const-string v6, "  #"

    #@47
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@4a
    invoke-virtual {p3, v3}, Ljava/io/PrintWriter;->print(I)V

    #@4d
    .line 647
    const-string v6, ": "

    #@4f
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@52
    invoke-virtual {p3, v2}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@55
    .line 648
    if-eqz v2, :cond_0

    #@57
    .line 649
    invoke-virtual {v2, v4, p2, p3, p4}, Landroid/support/v4/app/Fragment;->dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V

    #@5a
    .line 644
    :cond_0
    add-int/lit8 v3, v3, 0x1

    #@5c
    goto :goto_0

    #@5d
    .line 655
    .end local v0    # "N":I
    .end local v2    # "f":Landroid/support/v4/app/Fragment;
    .end local v3    # "i":I
    :cond_1
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@5f
    if-eqz v6, :cond_2

    #@61
    .line 656
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@63
    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    #@66
    move-result v0

    #@67
    .line 657
    .restart local v0    # "N":I
    if-lez v0, :cond_2

    #@69
    .line 658
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@6c
    const-string v6, "Added Fragments:"

    #@6e
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@71
    .line 659
    const/4 v3, 0x0

    #@72
    .restart local v3    # "i":I
    :goto_1
    if-ge v3, v0, :cond_2

    #@74
    .line 660
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@76
    invoke-virtual {v6, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@79
    move-result-object v2

    #@7a
    check-cast v2, Landroid/support/v4/app/Fragment;

    #@7c
    .line 661
    .restart local v2    # "f":Landroid/support/v4/app/Fragment;
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@7f
    const-string v6, "  #"

    #@81
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@84
    invoke-virtual {p3, v3}, Ljava/io/PrintWriter;->print(I)V

    #@87
    .line 662
    const-string v6, ": "

    #@89
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@8c
    invoke-virtual {v2}, Landroid/support/v4/app/Fragment;->toString()Ljava/lang/String;

    #@8f
    move-result-object v6

    #@90
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@93
    .line 659
    add-int/lit8 v3, v3, 0x1

    #@95
    goto :goto_1

    #@96
    .line 667
    .end local v0    # "N":I
    .end local v2    # "f":Landroid/support/v4/app/Fragment;
    .end local v3    # "i":I
    :cond_2
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCreatedMenus:Ljava/util/ArrayList;

    #@98
    if-eqz v6, :cond_3

    #@9a
    .line 668
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCreatedMenus:Ljava/util/ArrayList;

    #@9c
    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    #@9f
    move-result v0

    #@a0
    .line 669
    .restart local v0    # "N":I
    if-lez v0, :cond_3

    #@a2
    .line 670
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@a5
    const-string v6, "Fragments Created Menus:"

    #@a7
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@aa
    .line 671
    const/4 v3, 0x0

    #@ab
    .restart local v3    # "i":I
    :goto_2
    if-ge v3, v0, :cond_3

    #@ad
    .line 672
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCreatedMenus:Ljava/util/ArrayList;

    #@af
    invoke-virtual {v6, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@b2
    move-result-object v2

    #@b3
    check-cast v2, Landroid/support/v4/app/Fragment;

    #@b5
    .line 673
    .restart local v2    # "f":Landroid/support/v4/app/Fragment;
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@b8
    const-string v6, "  #"

    #@ba
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@bd
    invoke-virtual {p3, v3}, Ljava/io/PrintWriter;->print(I)V

    #@c0
    .line 674
    const-string v6, ": "

    #@c2
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@c5
    invoke-virtual {v2}, Landroid/support/v4/app/Fragment;->toString()Ljava/lang/String;

    #@c8
    move-result-object v6

    #@c9
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@cc
    .line 671
    add-int/lit8 v3, v3, 0x1

    #@ce
    goto :goto_2

    #@cf
    .line 679
    .end local v0    # "N":I
    .end local v2    # "f":Landroid/support/v4/app/Fragment;
    .end local v3    # "i":I
    :cond_3
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@d1
    if-eqz v6, :cond_4

    #@d3
    .line 680
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@d5
    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    #@d8
    move-result v0

    #@d9
    .line 681
    .restart local v0    # "N":I
    if-lez v0, :cond_4

    #@db
    .line 682
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@de
    const-string v6, "Back Stack:"

    #@e0
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@e3
    .line 683
    const/4 v3, 0x0

    #@e4
    .restart local v3    # "i":I
    :goto_3
    if-ge v3, v0, :cond_4

    #@e6
    .line 684
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@e8
    invoke-virtual {v6, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@eb
    move-result-object v1

    #@ec
    check-cast v1, Landroid/support/v4/app/BackStackRecord;

    #@ee
    .line 685
    .local v1, "bs":Landroid/support/v4/app/BackStackRecord;
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@f1
    const-string v6, "  #"

    #@f3
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@f6
    invoke-virtual {p3, v3}, Ljava/io/PrintWriter;->print(I)V

    #@f9
    .line 686
    const-string v6, ": "

    #@fb
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@fe
    invoke-virtual {v1}, Landroid/support/v4/app/BackStackRecord;->toString()Ljava/lang/String;

    #@101
    move-result-object v6

    #@102
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@105
    .line 687
    invoke-virtual {v1, v4, p2, p3, p4}, Landroid/support/v4/app/BackStackRecord;->dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V

    #@108
    .line 683
    add-int/lit8 v3, v3, 0x1

    #@10a
    goto :goto_3

    #@10b
    .line 692
    .end local v0    # "N":I
    .end local v1    # "bs":Landroid/support/v4/app/BackStackRecord;
    .end local v3    # "i":I
    :cond_4
    monitor-enter p0

    #@10c
    .line 693
    :try_start_0
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@10e
    if-eqz v6, :cond_5

    #@110
    .line 694
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@112
    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    #@115
    move-result v0

    #@116
    .line 695
    .restart local v0    # "N":I
    if-lez v0, :cond_5

    #@118
    .line 696
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@11b
    const-string v6, "Back Stack Indices:"

    #@11d
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@120
    .line 697
    const/4 v3, 0x0

    #@121
    .restart local v3    # "i":I
    :goto_4
    if-ge v3, v0, :cond_5

    #@123
    .line 698
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@125
    invoke-virtual {v6, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@128
    move-result-object v1

    #@129
    check-cast v1, Landroid/support/v4/app/BackStackRecord;

    #@12b
    .line 699
    .restart local v1    # "bs":Landroid/support/v4/app/BackStackRecord;
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@12e
    const-string v6, "  #"

    #@130
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@133
    invoke-virtual {p3, v3}, Ljava/io/PrintWriter;->print(I)V

    #@136
    .line 700
    const-string v6, ": "

    #@138
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@13b
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@13e
    .line 697
    add-int/lit8 v3, v3, 0x1

    #@140
    goto :goto_4

    #@141
    .line 705
    .end local v0    # "N":I
    .end local v1    # "bs":Landroid/support/v4/app/BackStackRecord;
    .end local v3    # "i":I
    :cond_5
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailBackStackIndices:Ljava/util/ArrayList;

    #@143
    if-eqz v6, :cond_6

    #@145
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailBackStackIndices:Ljava/util/ArrayList;

    #@147
    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    #@14a
    move-result v6

    #@14b
    if-lez v6, :cond_6

    #@14d
    .line 706
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@150
    const-string v6, "mAvailBackStackIndices: "

    #@152
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@155
    .line 707
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailBackStackIndices:Ljava/util/ArrayList;

    #@157
    invoke-virtual {v6}, Ljava/util/ArrayList;->toArray()[Ljava/lang/Object;

    #@15a
    move-result-object v6

    #@15b
    invoke-static {v6}, Ljava/util/Arrays;->toString([Ljava/lang/Object;)Ljava/lang/String;

    #@15e
    move-result-object v6

    #@15f
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@162
    .line 709
    :cond_6
    monitor-exit p0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@163
    .line 711
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mPendingActions:Ljava/util/ArrayList;

    #@165
    if-eqz v6, :cond_7

    #@167
    .line 712
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mPendingActions:Ljava/util/ArrayList;

    #@169
    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    #@16c
    move-result v0

    #@16d
    .line 713
    .restart local v0    # "N":I
    if-lez v0, :cond_7

    #@16f
    .line 714
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@172
    const-string v6, "Pending Actions:"

    #@174
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@177
    .line 715
    const/4 v3, 0x0

    #@178
    .restart local v3    # "i":I
    :goto_5
    if-ge v3, v0, :cond_7

    #@17a
    .line 716
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mPendingActions:Ljava/util/ArrayList;

    #@17c
    invoke-virtual {v6, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@17f
    move-result-object v5

    #@180
    check-cast v5, Ljava/lang/Runnable;

    #@182
    .line 717
    .local v5, "r":Ljava/lang/Runnable;
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@185
    const-string v6, "  #"

    #@187
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@18a
    invoke-virtual {p3, v3}, Ljava/io/PrintWriter;->print(I)V

    #@18d
    .line 718
    const-string v6, ": "

    #@18f
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@192
    invoke-virtual {p3, v5}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@195
    .line 715
    add-int/lit8 v3, v3, 0x1

    #@197
    goto :goto_5

    #@198
    .line 709
    .end local v0    # "N":I
    .end local v3    # "i":I
    .end local v5    # "r":Ljava/lang/Runnable;
    :catchall_0
    move-exception v6

    #@199
    :try_start_1
    monitor-exit p0
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@19a
    throw v6

    #@19b
    .line 723
    :cond_7
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@19e
    const-string v6, "FragmentManager misc state:"

    #@1a0
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@1a3
    .line 724
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1a6
    const-string v6, "  mActivity="

    #@1a8
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1ab
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@1ad
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@1b0
    .line 725
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1b3
    const-string v6, "  mContainer="

    #@1b5
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1b8
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mContainer:Landroid/support/v4/app/FragmentContainer;

    #@1ba
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@1bd
    .line 726
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mParent:Landroid/support/v4/app/Fragment;

    #@1bf
    if-eqz v6, :cond_8

    #@1c1
    .line 727
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1c4
    const-string v6, "  mParent="

    #@1c6
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1c9
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mParent:Landroid/support/v4/app/Fragment;

    #@1cb
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@1ce
    .line 729
    :cond_8
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1d1
    const-string v6, "  mCurState="

    #@1d3
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1d6
    iget v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCurState:I

    #@1d8
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(I)V

    #@1db
    .line 730
    const-string v6, " mStateSaved="

    #@1dd
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1e0
    iget-boolean v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateSaved:Z

    #@1e2
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Z)V

    #@1e5
    .line 731
    const-string v6, " mDestroyed="

    #@1e7
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1ea
    iget-boolean v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mDestroyed:Z

    #@1ec
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Z)V

    #@1ef
    .line 732
    iget-boolean v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNeedMenuInvalidate:Z

    #@1f1
    if-eqz v6, :cond_9

    #@1f3
    .line 733
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1f6
    const-string v6, "  mNeedMenuInvalidate="

    #@1f8
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@1fb
    .line 734
    iget-boolean v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNeedMenuInvalidate:Z

    #@1fd
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Z)V

    #@200
    .line 736
    :cond_9
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNoTransactionsBecause:Ljava/lang/String;

    #@202
    if-eqz v6, :cond_a

    #@204
    .line 737
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@207
    const-string v6, "  mNoTransactionsBecause="

    #@209
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@20c
    .line 738
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNoTransactionsBecause:Ljava/lang/String;

    #@20e
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@211
    .line 740
    :cond_a
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@213
    if-eqz v6, :cond_b

    #@215
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@217
    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    #@21a
    move-result v6

    #@21b
    if-lez v6, :cond_b

    #@21d
    .line 741
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@220
    const-string v6, "  mAvailIndices: "

    #@222
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@225
    .line 742
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@227
    invoke-virtual {v6}, Ljava/util/ArrayList;->toArray()[Ljava/lang/Object;

    #@22a
    move-result-object v6

    #@22b
    invoke-static {v6}, Ljava/util/Arrays;->toString([Ljava/lang/Object;)Ljava/lang/String;

    #@22e
    move-result-object v6

    #@22f
    invoke-virtual {p3, v6}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@232
    .line 744
    :cond_b
    return-void
.end method

.method public enqueueAction(Ljava/lang/Runnable;Z)V
    .locals 2
    .param p1, "action"    # Ljava/lang/Runnable;
    .param p2, "allowStateLoss"    # Z

    #@0
    .prologue
    .line 1382
    if-nez p2, :cond_0

    #@2
    .line 1383
    invoke-direct {p0}, Landroid/support/v4/app/FragmentManagerImpl;->checkStateLoss()V

    #@5
    .line 1385
    :cond_0
    monitor-enter p0

    #@6
    .line 1386
    :try_start_0
    iget-boolean v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mDestroyed:Z

    #@8
    if-nez v0, :cond_1

    #@a
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@c
    if-nez v0, :cond_2

    #@e
    .line 1387
    :cond_1
    new-instance v0, Ljava/lang/IllegalStateException;

    #@10
    const-string v1, "Activity has been destroyed"

    #@12
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@15
    throw v0

    #@16
    .line 1397
    :catchall_0
    move-exception v0

    #@17
    monitor-exit p0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@18
    throw v0

    #@19
    .line 1389
    :cond_2
    :try_start_1
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mPendingActions:Ljava/util/ArrayList;

    #@1b
    if-nez v0, :cond_3

    #@1d
    .line 1390
    new-instance v0, Ljava/util/ArrayList;

    #@1f
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@22
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mPendingActions:Ljava/util/ArrayList;

    #@24
    .line 1392
    :cond_3
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mPendingActions:Ljava/util/ArrayList;

    #@26
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@29
    .line 1393
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mPendingActions:Ljava/util/ArrayList;

    #@2b
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    #@2e
    move-result v0

    #@2f
    const/4 v1, 0x1

    #@30
    if-ne v0, v1, :cond_4

    #@32
    .line 1394
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@34
    iget-object v0, v0, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@36
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mExecCommit:Ljava/lang/Runnable;

    #@38
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeCallbacks(Ljava/lang/Runnable;)V

    #@3b
    .line 1395
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@3d
    iget-object v0, v0, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@3f
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mExecCommit:Ljava/lang/Runnable;

    #@41
    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    #@44
    .line 1397
    :cond_4
    monitor-exit p0
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@45
    .line 1398
    return-void
.end method

.method public execPendingActions()Z
    .locals 8

    #@0
    .prologue
    const/4 v7, 0x0

    #@1
    .line 1460
    iget-boolean v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mExecutingActions:Z

    #@3
    if-eqz v5, :cond_0

    #@5
    .line 1461
    new-instance v5, Ljava/lang/IllegalStateException;

    #@7
    const-string v6, "Recursive entry to executePendingTransactions"

    #@9
    invoke-direct {v5, v6}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@c
    throw v5

    #@d
    .line 1464
    :cond_0
    invoke-static {}, Landroid/os/Looper;->myLooper()Landroid/os/Looper;

    #@10
    move-result-object v5

    #@11
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@13
    iget-object v6, v6, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@15
    invoke-virtual {v6}, Landroid/os/Handler;->getLooper()Landroid/os/Looper;

    #@18
    move-result-object v6

    #@19
    if-eq v5, v6, :cond_1

    #@1b
    .line 1465
    new-instance v5, Ljava/lang/IllegalStateException;

    #@1d
    const-string v6, "Must be called from main thread of process"

    #@1f
    invoke-direct {v5, v6}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@22
    throw v5

    #@23
    .line 1468
    :cond_1
    const/4 v0, 0x0

    #@24
    .line 1473
    .local v0, "didSomething":Z
    :goto_0
    monitor-enter p0

    #@25
    .line 1474
    :try_start_0
    iget-object v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mPendingActions:Ljava/util/ArrayList;

    #@27
    if-eqz v5, :cond_2

    #@29
    iget-object v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mPendingActions:Ljava/util/ArrayList;

    #@2b
    invoke-virtual {v5}, Ljava/util/ArrayList;->size()I

    #@2e
    move-result v5

    #@2f
    if-nez v5, :cond_4

    #@31
    .line 1475
    :cond_2
    monitor-exit p0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@32
    .line 1496
    iget-boolean v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mHavePendingDeferredStart:Z

    #@34
    if-eqz v5, :cond_9

    #@36
    .line 1497
    const/4 v3, 0x0

    #@37
    .line 1498
    .local v3, "loadersRunning":Z
    const/4 v2, 0x0

    #@38
    .local v2, "i":I
    :goto_1
    iget-object v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@3a
    invoke-virtual {v5}, Ljava/util/ArrayList;->size()I

    #@3d
    move-result v5

    #@3e
    if-ge v2, v5, :cond_8

    #@40
    .line 1499
    iget-object v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@42
    invoke-virtual {v5, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@45
    move-result-object v1

    #@46
    check-cast v1, Landroid/support/v4/app/Fragment;

    #@48
    .line 1500
    .local v1, "f":Landroid/support/v4/app/Fragment;
    if-eqz v1, :cond_3

    #@4a
    iget-object v5, v1, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@4c
    if-eqz v5, :cond_3

    #@4e
    .line 1501
    iget-object v5, v1, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@50
    invoke-virtual {v5}, Landroid/support/v4/app/LoaderManagerImpl;->hasRunningLoaders()Z

    #@53
    move-result v5

    #@54
    or-int/2addr v3, v5

    #@55
    .line 1498
    :cond_3
    add-int/lit8 v2, v2, 0x1

    #@57
    goto :goto_1

    #@58
    .line 1478
    .end local v1    # "f":Landroid/support/v4/app/Fragment;
    .end local v2    # "i":I
    .end local v3    # "loadersRunning":Z
    :cond_4
    :try_start_1
    iget-object v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mPendingActions:Ljava/util/ArrayList;

    #@5a
    invoke-virtual {v5}, Ljava/util/ArrayList;->size()I

    #@5d
    move-result v4

    #@5e
    .line 1479
    .local v4, "numActions":I
    iget-object v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mTmpActions:[Ljava/lang/Runnable;

    #@60
    if-eqz v5, :cond_5

    #@62
    iget-object v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mTmpActions:[Ljava/lang/Runnable;

    #@64
    array-length v5, v5

    #@65
    if-ge v5, v4, :cond_6

    #@67
    .line 1480
    :cond_5
    new-array v5, v4, [Ljava/lang/Runnable;

    #@69
    iput-object v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mTmpActions:[Ljava/lang/Runnable;

    #@6b
    .line 1482
    :cond_6
    iget-object v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mPendingActions:Ljava/util/ArrayList;

    #@6d
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mTmpActions:[Ljava/lang/Runnable;

    #@6f
    invoke-virtual {v5, v6}, Ljava/util/ArrayList;->toArray([Ljava/lang/Object;)[Ljava/lang/Object;

    #@72
    .line 1483
    iget-object v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mPendingActions:Ljava/util/ArrayList;

    #@74
    invoke-virtual {v5}, Ljava/util/ArrayList;->clear()V

    #@77
    .line 1484
    iget-object v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@79
    iget-object v5, v5, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@7b
    iget-object v6, p0, Landroid/support/v4/app/FragmentManagerImpl;->mExecCommit:Ljava/lang/Runnable;

    #@7d
    invoke-virtual {v5, v6}, Landroid/os/Handler;->removeCallbacks(Ljava/lang/Runnable;)V

    #@80
    .line 1485
    monitor-exit p0
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@81
    .line 1487
    const/4 v5, 0x1

    #@82
    iput-boolean v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mExecutingActions:Z

    #@84
    .line 1488
    const/4 v2, 0x0

    #@85
    .restart local v2    # "i":I
    :goto_2
    if-ge v2, v4, :cond_7

    #@87
    .line 1489
    iget-object v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mTmpActions:[Ljava/lang/Runnable;

    #@89
    aget-object v5, v5, v2

    #@8b
    invoke-interface {v5}, Ljava/lang/Runnable;->run()V

    #@8e
    .line 1490
    iget-object v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mTmpActions:[Ljava/lang/Runnable;

    #@90
    const/4 v6, 0x0

    #@91
    aput-object v6, v5, v2

    #@93
    .line 1488
    add-int/lit8 v2, v2, 0x1

    #@95
    goto :goto_2

    #@96
    .line 1485
    .end local v2    # "i":I
    .end local v4    # "numActions":I
    :catchall_0
    move-exception v5

    #@97
    :try_start_2
    monitor-exit p0
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    #@98
    throw v5

    #@99
    .line 1492
    .restart local v2    # "i":I
    .restart local v4    # "numActions":I
    :cond_7
    iput-boolean v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mExecutingActions:Z

    #@9b
    .line 1493
    const/4 v0, 0x1

    #@9c
    .line 1494
    goto :goto_0

    #@9d
    .line 1504
    .end local v4    # "numActions":I
    .restart local v3    # "loadersRunning":Z
    :cond_8
    if-nez v3, :cond_9

    #@9f
    .line 1505
    iput-boolean v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mHavePendingDeferredStart:Z

    #@a1
    .line 1506
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentManagerImpl;->startPendingDeferredFragments()V

    #@a4
    .line 1509
    .end local v2    # "i":I
    .end local v3    # "loadersRunning":Z
    :cond_9
    return v0
.end method

.method public executePendingTransactions()Z
    .locals 1

    #@0
    .prologue
    .line 486
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentManagerImpl;->execPendingActions()Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public findFragmentById(I)Landroid/support/v4/app/Fragment;
    .locals 3
    .param p1, "id"    # I

    #@0
    .prologue
    .line 1308
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@2
    if-eqz v2, :cond_2

    #@4
    .line 1310
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@6
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@9
    move-result v2

    #@a
    add-int/lit8 v1, v2, -0x1

    #@c
    .local v1, "i":I
    :goto_0
    if-ltz v1, :cond_2

    #@e
    .line 1311
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@10
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@13
    move-result-object v0

    #@14
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@16
    .line 1312
    .local v0, "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_1

    #@18
    iget v2, v0, Landroid/support/v4/app/Fragment;->mFragmentId:I

    #@1a
    if-ne v2, p1, :cond_1

    #@1c
    .line 1326
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_0
    :goto_1
    return-object v0

    #@1d
    .line 1310
    .restart local v0    # "f":Landroid/support/v4/app/Fragment;
    .restart local v1    # "i":I
    :cond_1
    add-int/lit8 v1, v1, -0x1

    #@1f
    goto :goto_0

    #@20
    .line 1317
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_2
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@22
    if-eqz v2, :cond_4

    #@24
    .line 1319
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@26
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@29
    move-result v2

    #@2a
    add-int/lit8 v1, v2, -0x1

    #@2c
    .restart local v1    # "i":I
    :goto_2
    if-ltz v1, :cond_4

    #@2e
    .line 1320
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@30
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@33
    move-result-object v0

    #@34
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@36
    .line 1321
    .restart local v0    # "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_3

    #@38
    iget v2, v0, Landroid/support/v4/app/Fragment;->mFragmentId:I

    #@3a
    if-eq v2, p1, :cond_0

    #@3c
    .line 1319
    :cond_3
    add-int/lit8 v1, v1, -0x1

    #@3e
    goto :goto_2

    #@3f
    .line 1326
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_4
    const/4 v0, 0x0

    #@40
    goto :goto_1
.end method

.method public findFragmentByTag(Ljava/lang/String;)Landroid/support/v4/app/Fragment;
    .locals 3
    .param p1, "tag"    # Ljava/lang/String;

    #@0
    .prologue
    .line 1330
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@2
    if-eqz v2, :cond_2

    #@4
    if-eqz p1, :cond_2

    #@6
    .line 1332
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@8
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@b
    move-result v2

    #@c
    add-int/lit8 v1, v2, -0x1

    #@e
    .local v1, "i":I
    :goto_0
    if-ltz v1, :cond_2

    #@10
    .line 1333
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@12
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@15
    move-result-object v0

    #@16
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@18
    .line 1334
    .local v0, "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_1

    #@1a
    iget-object v2, v0, Landroid/support/v4/app/Fragment;->mTag:Ljava/lang/String;

    #@1c
    invoke-virtual {p1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@1f
    move-result v2

    #@20
    if-eqz v2, :cond_1

    #@22
    .line 1348
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_0
    :goto_1
    return-object v0

    #@23
    .line 1332
    .restart local v0    # "f":Landroid/support/v4/app/Fragment;
    .restart local v1    # "i":I
    :cond_1
    add-int/lit8 v1, v1, -0x1

    #@25
    goto :goto_0

    #@26
    .line 1339
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_2
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@28
    if-eqz v2, :cond_4

    #@2a
    if-eqz p1, :cond_4

    #@2c
    .line 1341
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@2e
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@31
    move-result v2

    #@32
    add-int/lit8 v1, v2, -0x1

    #@34
    .restart local v1    # "i":I
    :goto_2
    if-ltz v1, :cond_4

    #@36
    .line 1342
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@38
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@3b
    move-result-object v0

    #@3c
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@3e
    .line 1343
    .restart local v0    # "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_3

    #@40
    iget-object v2, v0, Landroid/support/v4/app/Fragment;->mTag:Ljava/lang/String;

    #@42
    invoke-virtual {p1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@45
    move-result v2

    #@46
    if-nez v2, :cond_0

    #@48
    .line 1341
    :cond_3
    add-int/lit8 v1, v1, -0x1

    #@4a
    goto :goto_2

    #@4b
    .line 1348
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_4
    const/4 v0, 0x0

    #@4c
    goto :goto_1
.end method

.method public findFragmentByWho(Ljava/lang/String;)Landroid/support/v4/app/Fragment;
    .locals 3
    .param p1, "who"    # Ljava/lang/String;

    #@0
    .prologue
    .line 1352
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@2
    if-eqz v2, :cond_1

    #@4
    if-eqz p1, :cond_1

    #@6
    .line 1353
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@8
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@b
    move-result v2

    #@c
    add-int/lit8 v1, v2, -0x1

    #@e
    .local v1, "i":I
    :goto_0
    if-ltz v1, :cond_1

    #@10
    .line 1354
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@12
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@15
    move-result-object v0

    #@16
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@18
    .line 1355
    .local v0, "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_0

    #@1a
    invoke-virtual {v0, p1}, Landroid/support/v4/app/Fragment;->findFragmentByWho(Ljava/lang/String;)Landroid/support/v4/app/Fragment;

    #@1d
    move-result-object v0

    #@1e
    if-eqz v0, :cond_0

    #@20
    .line 1360
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :goto_1
    return-object v0

    #@21
    .line 1353
    .restart local v0    # "f":Landroid/support/v4/app/Fragment;
    .restart local v1    # "i":I
    :cond_0
    add-int/lit8 v1, v1, -0x1

    #@23
    goto :goto_0

    #@24
    .line 1360
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v1    # "i":I
    :cond_1
    const/4 v0, 0x0

    #@25
    goto :goto_1
.end method

.method public freeBackStackIndex(I)V
    .locals 3
    .param p1, "index"    # I

    #@0
    .prologue
    .line 1446
    monitor-enter p0

    #@1
    .line 1447
    :try_start_0
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@3
    const/4 v1, 0x0

    #@4
    invoke-virtual {v0, p1, v1}, Ljava/util/ArrayList;->set(ILjava/lang/Object;)Ljava/lang/Object;

    #@7
    .line 1448
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailBackStackIndices:Ljava/util/ArrayList;

    #@9
    if-nez v0, :cond_0

    #@b
    .line 1449
    new-instance v0, Ljava/util/ArrayList;

    #@d
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@10
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailBackStackIndices:Ljava/util/ArrayList;

    #@12
    .line 1451
    :cond_0
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@14
    if-eqz v0, :cond_1

    #@16
    const-string v0, "FragmentManager"

    #@18
    new-instance v1, Ljava/lang/StringBuilder;

    #@1a
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@1d
    const-string v2, "Freeing back stack index "

    #@1f
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@22
    move-result-object v1

    #@23
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@26
    move-result-object v1

    #@27
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2a
    move-result-object v1

    #@2b
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@2e
    .line 1452
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailBackStackIndices:Ljava/util/ArrayList;

    #@30
    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@33
    move-result-object v1

    #@34
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@37
    .line 1453
    monitor-exit p0

    #@38
    .line 1454
    return-void

    #@39
    .line 1453
    :catchall_0
    move-exception v0

    #@3a
    monitor-exit p0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@3b
    throw v0
.end method

.method public getBackStackEntryAt(I)Landroid/support/v4/app/FragmentManager$BackStackEntry;
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 550
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@5
    move-result-object v0

    #@6
    check-cast v0, Landroid/support/v4/app/FragmentManager$BackStackEntry;

    #@8
    return-object v0
.end method

.method public getBackStackEntryCount()I
    .locals 1

    #@0
    .prologue
    .line 545
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@6
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    #@9
    move-result v0

    #@a
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public getFragment(Landroid/os/Bundle;Ljava/lang/String;)Landroid/support/v4/app/Fragment;
    .locals 5
    .param p1, "bundle"    # Landroid/os/Bundle;
    .param p2, "key"    # Ljava/lang/String;

    #@0
    .prologue
    const/4 v2, -0x1

    #@1
    .line 579
    invoke-virtual {p1, p2, v2}, Landroid/os/Bundle;->getInt(Ljava/lang/String;I)I

    #@4
    move-result v1

    #@5
    .line 580
    .local v1, "index":I
    if-ne v1, v2, :cond_1

    #@7
    .line 581
    const/4 v0, 0x0

    #@8
    .line 592
    :cond_0
    :goto_0
    return-object v0

    #@9
    .line 583
    :cond_1
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@b
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@e
    move-result v2

    #@f
    if-lt v1, v2, :cond_2

    #@11
    .line 584
    new-instance v2, Ljava/lang/IllegalStateException;

    #@13
    new-instance v3, Ljava/lang/StringBuilder;

    #@15
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@18
    const-string v4, "Fragment no longer exists for key "

    #@1a
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1d
    move-result-object v3

    #@1e
    invoke-virtual {v3, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@21
    move-result-object v3

    #@22
    const-string v4, ": index "

    #@24
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@27
    move-result-object v3

    #@28
    invoke-virtual {v3, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@2b
    move-result-object v3

    #@2c
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2f
    move-result-object v3

    #@30
    invoke-direct {v2, v3}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@33
    invoke-direct {p0, v2}, Landroid/support/v4/app/FragmentManagerImpl;->throwException(Ljava/lang/RuntimeException;)V

    #@36
    .line 587
    :cond_2
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@38
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@3b
    move-result-object v0

    #@3c
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@3e
    .line 588
    .local v0, "f":Landroid/support/v4/app/Fragment;
    if-nez v0, :cond_0

    #@40
    .line 589
    new-instance v2, Ljava/lang/IllegalStateException;

    #@42
    new-instance v3, Ljava/lang/StringBuilder;

    #@44
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@47
    const-string v4, "Fragment no longer exists for key "

    #@49
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@4c
    move-result-object v3

    #@4d
    invoke-virtual {v3, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@50
    move-result-object v3

    #@51
    const-string v4, ": index "

    #@53
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@56
    move-result-object v3

    #@57
    invoke-virtual {v3, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@5a
    move-result-object v3

    #@5b
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@5e
    move-result-object v3

    #@5f
    invoke-direct {v2, v3}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@62
    invoke-direct {p0, v2}, Landroid/support/v4/app/FragmentManagerImpl;->throwException(Ljava/lang/RuntimeException;)V

    #@65
    goto :goto_0
.end method

.method public getFragments()Ljava/util/List;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/List",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;"
        }
    .end annotation

    #@0
    .prologue
    .line 597
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@2
    return-object v0
.end method

.method getLayoutInflaterFactory()Landroid/view/LayoutInflater$Factory;
    .locals 0

    #@0
    .prologue
    .line 2202
    return-object p0
.end method

.method public hideFragment(Landroid/support/v4/app/Fragment;II)V
    .locals 5
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;
    .param p2, "transition"    # I
    .param p3, "transitionStyle"    # I

    #@0
    .prologue
    const/4 v4, 0x1

    #@1
    .line 1229
    sget-boolean v1, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@3
    if-eqz v1, :cond_0

    #@5
    const-string v1, "FragmentManager"

    #@7
    new-instance v2, Ljava/lang/StringBuilder;

    #@9
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@c
    const-string v3, "hide: "

    #@e
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@11
    move-result-object v2

    #@12
    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@15
    move-result-object v2

    #@16
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@19
    move-result-object v2

    #@1a
    invoke-static {v1, v2}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@1d
    .line 1230
    :cond_0
    iget-boolean v1, p1, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@1f
    if-nez v1, :cond_4

    #@21
    .line 1231
    iput-boolean v4, p1, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@23
    .line 1232
    iget-object v1, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@25
    if-eqz v1, :cond_2

    #@27
    .line 1233
    const/4 v1, 0x0

    #@28
    invoke-virtual {p0, p1, p2, v1, p3}, Landroid/support/v4/app/FragmentManagerImpl;->loadAnimation(Landroid/support/v4/app/Fragment;IZI)Landroid/view/animation/Animation;

    #@2b
    move-result-object v0

    #@2c
    .line 1235
    .local v0, "anim":Landroid/view/animation/Animation;
    if-eqz v0, :cond_1

    #@2e
    .line 1236
    iget-object v1, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@30
    invoke-virtual {v1, v0}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    #@33
    .line 1238
    :cond_1
    iget-object v1, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@35
    const/16 v2, 0x8

    #@37
    invoke-virtual {v1, v2}, Landroid/view/View;->setVisibility(I)V

    #@3a
    .line 1240
    .end local v0    # "anim":Landroid/view/animation/Animation;
    :cond_2
    iget-boolean v1, p1, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@3c
    if-eqz v1, :cond_3

    #@3e
    iget-boolean v1, p1, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@40
    if-eqz v1, :cond_3

    #@42
    iget-boolean v1, p1, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@44
    if-eqz v1, :cond_3

    #@46
    .line 1241
    iput-boolean v4, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNeedMenuInvalidate:Z

    #@48
    .line 1243
    :cond_3
    invoke-virtual {p1, v4}, Landroid/support/v4/app/Fragment;->onHiddenChanged(Z)V

    #@4b
    .line 1245
    :cond_4
    return-void
.end method

.method public isDestroyed()Z
    .locals 1

    #@0
    .prologue
    .line 615
    iget-boolean v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mDestroyed:Z

    #@2
    return v0
.end method

.method loadAnimation(Landroid/support/v4/app/Fragment;IZI)Landroid/view/animation/Animation;
    .locals 9
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;
    .param p2, "transit"    # I
    .param p3, "enter"    # Z
    .param p4, "transitionStyle"    # I

    #@0
    .prologue
    const v8, 0x3f79999a    # 0.975f

    #@3
    const/4 v3, 0x0

    #@4
    const/4 v7, 0x0

    #@5
    const/high16 v6, 0x3f800000    # 1.0f

    #@7
    .line 777
    iget v4, p1, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@9
    invoke-virtual {p1, p2, p3, v4}, Landroid/support/v4/app/Fragment;->onCreateAnimation(IZI)Landroid/view/animation/Animation;

    #@c
    move-result-object v1

    #@d
    .line 779
    .local v1, "animObj":Landroid/view/animation/Animation;
    if-eqz v1, :cond_0

    #@f
    .line 831
    .end local v1    # "animObj":Landroid/view/animation/Animation;
    :goto_0
    return-object v1

    #@10
    .line 783
    .restart local v1    # "animObj":Landroid/view/animation/Animation;
    :cond_0
    iget v4, p1, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@12
    if-eqz v4, :cond_1

    #@14
    .line 784
    iget-object v4, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@16
    iget v5, p1, Landroid/support/v4/app/Fragment;->mNextAnim:I

    #@18
    invoke-static {v4, v5}, Landroid/view/animation/AnimationUtils;->loadAnimation(Landroid/content/Context;I)Landroid/view/animation/Animation;

    #@1b
    move-result-object v0

    #@1c
    .line 785
    .local v0, "anim":Landroid/view/animation/Animation;
    if-eqz v0, :cond_1

    #@1e
    move-object v1, v0

    #@1f
    .line 786
    goto :goto_0

    #@20
    .line 790
    .end local v0    # "anim":Landroid/view/animation/Animation;
    :cond_1
    if-nez p2, :cond_2

    #@22
    move-object v1, v3

    #@23
    .line 791
    goto :goto_0

    #@24
    .line 794
    :cond_2
    invoke-static {p2, p3}, Landroid/support/v4/app/FragmentManagerImpl;->transitToStyleIndex(IZ)I

    #@27
    move-result v2

    #@28
    .line 795
    .local v2, "styleIndex":I
    if-gez v2, :cond_3

    #@2a
    move-object v1, v3

    #@2b
    .line 796
    goto :goto_0

    #@2c
    .line 799
    :cond_3
    packed-switch v2, :pswitch_data_0

    #@2f
    .line 814
    if-nez p4, :cond_4

    #@31
    iget-object v4, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@33
    invoke-virtual {v4}, Landroid/support/v4/app/FragmentActivity;->getWindow()Landroid/view/Window;

    #@36
    move-result-object v4

    #@37
    if-eqz v4, :cond_4

    #@39
    .line 815
    iget-object v4, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@3b
    invoke-virtual {v4}, Landroid/support/v4/app/FragmentActivity;->getWindow()Landroid/view/Window;

    #@3e
    move-result-object v4

    #@3f
    invoke-virtual {v4}, Landroid/view/Window;->getAttributes()Landroid/view/WindowManager$LayoutParams;

    #@42
    move-result-object v4

    #@43
    iget p4, v4, Landroid/view/WindowManager$LayoutParams;->windowAnimations:I

    #@45
    .line 817
    :cond_4
    if-nez p4, :cond_5

    #@47
    move-object v1, v3

    #@48
    .line 818
    goto :goto_0

    #@49
    .line 801
    :pswitch_0
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@4b
    const/high16 v4, 0x3f900000    # 1.125f

    #@4d
    invoke-static {v3, v4, v6, v7, v6}, Landroid/support/v4/app/FragmentManagerImpl;->makeOpenCloseAnimation(Landroid/content/Context;FFFF)Landroid/view/animation/Animation;

    #@50
    move-result-object v1

    #@51
    goto :goto_0

    #@52
    .line 803
    :pswitch_1
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@54
    invoke-static {v3, v6, v8, v6, v7}, Landroid/support/v4/app/FragmentManagerImpl;->makeOpenCloseAnimation(Landroid/content/Context;FFFF)Landroid/view/animation/Animation;

    #@57
    move-result-object v1

    #@58
    goto :goto_0

    #@59
    .line 805
    :pswitch_2
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@5b
    invoke-static {v3, v8, v6, v7, v6}, Landroid/support/v4/app/FragmentManagerImpl;->makeOpenCloseAnimation(Landroid/content/Context;FFFF)Landroid/view/animation/Animation;

    #@5e
    move-result-object v1

    #@5f
    goto :goto_0

    #@60
    .line 807
    :pswitch_3
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@62
    const v4, 0x3f89999a    # 1.075f

    #@65
    invoke-static {v3, v6, v4, v6, v7}, Landroid/support/v4/app/FragmentManagerImpl;->makeOpenCloseAnimation(Landroid/content/Context;FFFF)Landroid/view/animation/Animation;

    #@68
    move-result-object v1

    #@69
    goto :goto_0

    #@6a
    .line 809
    :pswitch_4
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@6c
    invoke-static {v3, v7, v6}, Landroid/support/v4/app/FragmentManagerImpl;->makeFadeAnimation(Landroid/content/Context;FF)Landroid/view/animation/Animation;

    #@6f
    move-result-object v1

    #@70
    goto :goto_0

    #@71
    .line 811
    :pswitch_5
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@73
    invoke-static {v3, v6, v7}, Landroid/support/v4/app/FragmentManagerImpl;->makeFadeAnimation(Landroid/content/Context;FF)Landroid/view/animation/Animation;

    #@76
    move-result-object v1

    #@77
    goto :goto_0

    #@78
    :cond_5
    move-object v1, v3

    #@79
    .line 831
    goto :goto_0

    #@7a
    .line 799
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
        :pswitch_4
        :pswitch_5
    .end packed-switch
.end method

.method makeActive(Landroid/support/v4/app/Fragment;)V
    .locals 3
    .param p1, "f"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 1156
    iget v0, p1, Landroid/support/v4/app/Fragment;->mIndex:I

    #@2
    if-ltz v0, :cond_1

    #@4
    .line 1172
    :cond_0
    :goto_0
    return-void

    #@5
    .line 1160
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@7
    if-eqz v0, :cond_2

    #@9
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@b
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    #@e
    move-result v0

    #@f
    if-gtz v0, :cond_4

    #@11
    .line 1161
    :cond_2
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@13
    if-nez v0, :cond_3

    #@15
    .line 1162
    new-instance v0, Ljava/util/ArrayList;

    #@17
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@1a
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@1c
    .line 1164
    :cond_3
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@1e
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    #@21
    move-result v0

    #@22
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mParent:Landroid/support/v4/app/Fragment;

    #@24
    invoke-virtual {p1, v0, v1}, Landroid/support/v4/app/Fragment;->setIndex(ILandroid/support/v4/app/Fragment;)V

    #@27
    .line 1165
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@29
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@2c
    .line 1171
    :goto_1
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@2e
    if-eqz v0, :cond_0

    #@30
    const-string v0, "FragmentManager"

    #@32
    new-instance v1, Ljava/lang/StringBuilder;

    #@34
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@37
    const-string v2, "Allocated fragment index "

    #@39
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@3c
    move-result-object v1

    #@3d
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@40
    move-result-object v1

    #@41
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@44
    move-result-object v1

    #@45
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@48
    goto :goto_0

    #@49
    .line 1168
    :cond_4
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@4b
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@4d
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    #@50
    move-result v1

    #@51
    add-int/lit8 v1, v1, -0x1

    #@53
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->remove(I)Ljava/lang/Object;

    #@56
    move-result-object v0

    #@57
    check-cast v0, Ljava/lang/Integer;

    #@59
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    #@5c
    move-result v0

    #@5d
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mParent:Landroid/support/v4/app/Fragment;

    #@5f
    invoke-virtual {p1, v0, v1}, Landroid/support/v4/app/Fragment;->setIndex(ILandroid/support/v4/app/Fragment;)V

    #@62
    .line 1169
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@64
    iget v1, p1, Landroid/support/v4/app/Fragment;->mIndex:I

    #@66
    invoke-virtual {v0, v1, p1}, Ljava/util/ArrayList;->set(ILjava/lang/Object;)Ljava/lang/Object;

    #@69
    goto :goto_1
.end method

.method makeInactive(Landroid/support/v4/app/Fragment;)V
    .locals 3
    .param p1, "f"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 1175
    iget v0, p1, Landroid/support/v4/app/Fragment;->mIndex:I

    #@2
    if-gez v0, :cond_0

    #@4
    .line 1187
    :goto_0
    return-void

    #@5
    .line 1179
    :cond_0
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@7
    if-eqz v0, :cond_1

    #@9
    const-string v0, "FragmentManager"

    #@b
    new-instance v1, Ljava/lang/StringBuilder;

    #@d
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@10
    const-string v2, "Freeing fragment index "

    #@12
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@15
    move-result-object v1

    #@16
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@19
    move-result-object v1

    #@1a
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1d
    move-result-object v1

    #@1e
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@21
    .line 1180
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@23
    iget v1, p1, Landroid/support/v4/app/Fragment;->mIndex:I

    #@25
    const/4 v2, 0x0

    #@26
    invoke-virtual {v0, v1, v2}, Ljava/util/ArrayList;->set(ILjava/lang/Object;)Ljava/lang/Object;

    #@29
    .line 1181
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@2b
    if-nez v0, :cond_2

    #@2d
    .line 1182
    new-instance v0, Ljava/util/ArrayList;

    #@2f
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@32
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@34
    .line 1184
    :cond_2
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@36
    iget v1, p1, Landroid/support/v4/app/Fragment;->mIndex:I

    #@38
    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@3b
    move-result-object v1

    #@3c
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@3f
    .line 1185
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@41
    iget-object v1, p1, Landroid/support/v4/app/Fragment;->mWho:Ljava/lang/String;

    #@43
    invoke-virtual {v0, v1}, Landroid/support/v4/app/FragmentActivity;->invalidateSupportFragment(Ljava/lang/String;)V

    #@46
    .line 1186
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->initState()V

    #@49
    goto :goto_0
.end method

.method moveToState(IIIZ)V
    .locals 8
    .param p1, "newState"    # I
    .param p2, "transit"    # I
    .param p3, "transitStyle"    # I
    .param p4, "always"    # Z

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    .line 1112
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@3
    if-nez v0, :cond_0

    #@5
    if-eqz p1, :cond_0

    #@7
    .line 1113
    new-instance v0, Ljava/lang/IllegalStateException;

    #@9
    const-string v2, "No activity"

    #@b
    invoke-direct {v0, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@e
    throw v0

    #@f
    .line 1116
    :cond_0
    if-nez p4, :cond_2

    #@11
    iget v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCurState:I

    #@13
    if-ne v0, p1, :cond_2

    #@15
    .line 1142
    :cond_1
    :goto_0
    return-void

    #@16
    .line 1120
    :cond_2
    iput p1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCurState:I

    #@18
    .line 1121
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@1a
    if-eqz v0, :cond_1

    #@1c
    .line 1122
    const/4 v7, 0x0

    #@1d
    .line 1123
    .local v7, "loadersRunning":Z
    const/4 v6, 0x0

    #@1e
    .local v6, "i":I
    :goto_1
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@20
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    #@23
    move-result v0

    #@24
    if-ge v6, v0, :cond_4

    #@26
    .line 1124
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@28
    invoke-virtual {v0, v6}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@2b
    move-result-object v1

    #@2c
    check-cast v1, Landroid/support/v4/app/Fragment;

    #@2e
    .line 1125
    .local v1, "f":Landroid/support/v4/app/Fragment;
    if-eqz v1, :cond_3

    #@30
    move-object v0, p0

    #@31
    move v2, p1

    #@32
    move v3, p2

    #@33
    move v4, p3

    #@34
    .line 1126
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(Landroid/support/v4/app/Fragment;IIIZ)V

    #@37
    .line 1127
    iget-object v0, v1, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@39
    if-eqz v0, :cond_3

    #@3b
    .line 1128
    iget-object v0, v1, Landroid/support/v4/app/Fragment;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@3d
    invoke-virtual {v0}, Landroid/support/v4/app/LoaderManagerImpl;->hasRunningLoaders()Z

    #@40
    move-result v0

    #@41
    or-int/2addr v7, v0

    #@42
    .line 1123
    :cond_3
    add-int/lit8 v6, v6, 0x1

    #@44
    goto :goto_1

    #@45
    .line 1133
    .end local v1    # "f":Landroid/support/v4/app/Fragment;
    :cond_4
    if-nez v7, :cond_5

    #@47
    .line 1134
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentManagerImpl;->startPendingDeferredFragments()V

    #@4a
    .line 1137
    :cond_5
    iget-boolean v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNeedMenuInvalidate:Z

    #@4c
    if-eqz v0, :cond_1

    #@4e
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@50
    if-eqz v0, :cond_1

    #@52
    iget v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCurState:I

    #@54
    const/4 v2, 0x5

    #@55
    if-ne v0, v2, :cond_1

    #@57
    .line 1138
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@59
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentActivity;->supportInvalidateOptionsMenu()V

    #@5c
    .line 1139
    iput-boolean v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNeedMenuInvalidate:Z

    #@5e
    goto :goto_0
.end method

.method moveToState(IZ)V
    .locals 1
    .param p1, "newState"    # I
    .param p2, "always"    # Z

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 1108
    invoke-virtual {p0, p1, v0, v0, p2}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(IIIZ)V

    #@4
    .line 1109
    return-void
.end method

.method moveToState(Landroid/support/v4/app/Fragment;)V
    .locals 6
    .param p1, "f"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 1104
    iget v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCurState:I

    #@3
    move-object v0, p0

    #@4
    move-object v1, p1

    #@5
    move v4, v3

    #@6
    move v5, v3

    #@7
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(Landroid/support/v4/app/Fragment;IIIZ)V

    #@a
    .line 1105
    return-void
.end method

.method moveToState(Landroid/support/v4/app/Fragment;IIIZ)V
    .locals 10
    .param p1, "f"    # Landroid/support/v4/app/Fragment;
    .param p2, "newState"    # I
    .param p3, "transit"    # I
    .param p4, "transitionStyle"    # I
    .param p5, "keepActive"    # Z

    #@0
    .prologue
    .line 849
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mDetached:Z

    #@6
    if-eqz v0, :cond_1

    #@8
    :cond_0
    const/4 v0, 0x1

    #@9
    if-le p2, v0, :cond_1

    #@b
    .line 850
    const/4 p2, 0x1

    #@c
    .line 852
    :cond_1
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mRemoving:Z

    #@e
    if-eqz v0, :cond_2

    #@10
    iget v0, p1, Landroid/support/v4/app/Fragment;->mState:I

    #@12
    if-le p2, v0, :cond_2

    #@14
    .line 854
    iget p2, p1, Landroid/support/v4/app/Fragment;->mState:I

    #@16
    .line 858
    :cond_2
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mDeferStart:Z

    #@18
    if-eqz v0, :cond_3

    #@1a
    iget v0, p1, Landroid/support/v4/app/Fragment;->mState:I

    #@1c
    const/4 v1, 0x4

    #@1d
    if-ge v0, v1, :cond_3

    #@1f
    const/4 v0, 0x3

    #@20
    if-le p2, v0, :cond_3

    #@22
    .line 859
    const/4 p2, 0x3

    #@23
    .line 861
    :cond_3
    iget v0, p1, Landroid/support/v4/app/Fragment;->mState:I

    #@25
    if-ge v0, p2, :cond_1d

    #@27
    .line 865
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mFromLayout:Z

    #@29
    if-eqz v0, :cond_4

    #@2b
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mInLayout:Z

    #@2d
    if-nez v0, :cond_4

    #@2f
    .line 1101
    :goto_0
    return-void

    #@30
    .line 868
    :cond_4
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mAnimatingAway:Landroid/view/View;

    #@32
    if-eqz v0, :cond_5

    #@34
    .line 873
    const/4 v0, 0x0

    #@35
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mAnimatingAway:Landroid/view/View;

    #@37
    .line 874
    iget v2, p1, Landroid/support/v4/app/Fragment;->mStateAfterAnimating:I

    #@39
    const/4 v3, 0x0

    #@3a
    const/4 v4, 0x0

    #@3b
    const/4 v5, 0x1

    #@3c
    move-object v0, p0

    #@3d
    move-object v1, p1

    #@3e
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(Landroid/support/v4/app/Fragment;IIIZ)V

    #@41
    .line 876
    :cond_5
    iget v0, p1, Landroid/support/v4/app/Fragment;->mState:I

    #@43
    packed-switch v0, :pswitch_data_0

    #@46
    .line 1100
    :cond_6
    :goto_1
    iput p2, p1, Landroid/support/v4/app/Fragment;->mState:I

    #@48
    goto :goto_0

    #@49
    .line 878
    :pswitch_0
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@4b
    if-eqz v0, :cond_7

    #@4d
    const-string v0, "FragmentManager"

    #@4f
    new-instance v1, Ljava/lang/StringBuilder;

    #@51
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@54
    const-string v2, "moveto CREATED: "

    #@56
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@59
    move-result-object v1

    #@5a
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@5d
    move-result-object v1

    #@5e
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@61
    move-result-object v1

    #@62
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@65
    .line 879
    :cond_7
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@67
    if-eqz v0, :cond_9

    #@69
    .line 880
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@6b
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@6d
    invoke-virtual {v1}, Landroid/support/v4/app/FragmentActivity;->getClassLoader()Ljava/lang/ClassLoader;

    #@70
    move-result-object v1

    #@71
    invoke-virtual {v0, v1}, Landroid/os/Bundle;->setClassLoader(Ljava/lang/ClassLoader;)V

    #@74
    .line 881
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@76
    const-string v1, "android:view_state"

    #@78
    invoke-virtual {v0, v1}, Landroid/os/Bundle;->getSparseParcelableArray(Ljava/lang/String;)Landroid/util/SparseArray;

    #@7b
    move-result-object v0

    #@7c
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedViewState:Landroid/util/SparseArray;

    #@7e
    .line 883
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@80
    const-string v1, "android:target_state"

    #@82
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/app/FragmentManagerImpl;->getFragment(Landroid/os/Bundle;Ljava/lang/String;)Landroid/support/v4/app/Fragment;

    #@85
    move-result-object v0

    #@86
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@88
    .line 885
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@8a
    if-eqz v0, :cond_8

    #@8c
    .line 886
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@8e
    const-string v1, "android:target_req_state"

    #@90
    const/4 v2, 0x0

    #@91
    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->getInt(Ljava/lang/String;I)I

    #@94
    move-result v0

    #@95
    iput v0, p1, Landroid/support/v4/app/Fragment;->mTargetRequestCode:I

    #@97
    .line 889
    :cond_8
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@99
    const-string v1, "android:user_visible_hint"

    #@9b
    const/4 v2, 0x1

    #@9c
    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;Z)Z

    #@9f
    move-result v0

    #@a0
    iput-boolean v0, p1, Landroid/support/v4/app/Fragment;->mUserVisibleHint:Z

    #@a2
    .line 891
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mUserVisibleHint:Z

    #@a4
    if-nez v0, :cond_9

    #@a6
    .line 892
    const/4 v0, 0x1

    #@a7
    iput-boolean v0, p1, Landroid/support/v4/app/Fragment;->mDeferStart:Z

    #@a9
    .line 893
    const/4 v0, 0x3

    #@aa
    if-le p2, v0, :cond_9

    #@ac
    .line 894
    const/4 p2, 0x3

    #@ad
    .line 898
    :cond_9
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@af
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@b1
    .line 899
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mParent:Landroid/support/v4/app/Fragment;

    #@b3
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mParentFragment:Landroid/support/v4/app/Fragment;

    #@b5
    .line 900
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mParent:Landroid/support/v4/app/Fragment;

    #@b7
    if-eqz v0, :cond_a

    #@b9
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mParent:Landroid/support/v4/app/Fragment;

    #@bb
    iget-object v0, v0, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@bd
    :goto_2
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@bf
    .line 902
    const/4 v0, 0x0

    #@c0
    iput-boolean v0, p1, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@c2
    .line 903
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@c4
    invoke-virtual {p1, v0}, Landroid/support/v4/app/Fragment;->onAttach(Landroid/app/Activity;)V

    #@c7
    .line 904
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@c9
    if-nez v0, :cond_b

    #@cb
    .line 905
    new-instance v0, Landroid/support/v4/app/SuperNotCalledException;

    #@cd
    new-instance v1, Ljava/lang/StringBuilder;

    #@cf
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@d2
    const-string v2, "Fragment "

    #@d4
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@d7
    move-result-object v1

    #@d8
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@db
    move-result-object v1

    #@dc
    const-string v2, " did not call through to super.onAttach()"

    #@de
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@e1
    move-result-object v1

    #@e2
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@e5
    move-result-object v1

    #@e6
    invoke-direct {v0, v1}, Landroid/support/v4/app/SuperNotCalledException;-><init>(Ljava/lang/String;)V

    #@e9
    throw v0

    #@ea
    .line 900
    :cond_a
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@ec
    iget-object v0, v0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@ee
    goto :goto_2

    #@ef
    .line 908
    :cond_b
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mParentFragment:Landroid/support/v4/app/Fragment;

    #@f1
    if-nez v0, :cond_c

    #@f3
    .line 909
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@f5
    invoke-virtual {v0, p1}, Landroid/support/v4/app/FragmentActivity;->onAttachFragment(Landroid/support/v4/app/Fragment;)V

    #@f8
    .line 912
    :cond_c
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mRetaining:Z

    #@fa
    if-nez v0, :cond_d

    #@fc
    .line 913
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@fe
    invoke-virtual {p1, v0}, Landroid/support/v4/app/Fragment;->performCreate(Landroid/os/Bundle;)V

    #@101
    .line 915
    :cond_d
    const/4 v0, 0x0

    #@102
    iput-boolean v0, p1, Landroid/support/v4/app/Fragment;->mRetaining:Z

    #@104
    .line 916
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mFromLayout:Z

    #@106
    if-eqz v0, :cond_f

    #@108
    .line 920
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@10a
    invoke-virtual {p1, v0}, Landroid/support/v4/app/Fragment;->getLayoutInflater(Landroid/os/Bundle;)Landroid/view/LayoutInflater;

    #@10d
    move-result-object v0

    #@10e
    const/4 v1, 0x0

    #@10f
    iget-object v2, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@111
    invoke-virtual {p1, v0, v1, v2}, Landroid/support/v4/app/Fragment;->performCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;

    #@114
    move-result-object v0

    #@115
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@117
    .line 922
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@119
    if-eqz v0, :cond_1b

    #@11b
    .line 923
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@11d
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mInnerView:Landroid/view/View;

    #@11f
    .line 924
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@121
    invoke-static {v0}, Landroid/support/v4/app/NoSaveStateFrameLayout;->wrap(Landroid/view/View;)Landroid/view/ViewGroup;

    #@124
    move-result-object v0

    #@125
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@127
    .line 925
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@129
    if-eqz v0, :cond_e

    #@12b
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@12d
    const/16 v1, 0x8

    #@12f
    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    #@132
    .line 926
    :cond_e
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@134
    iget-object v1, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@136
    invoke-virtual {p1, v0, v1}, Landroid/support/v4/app/Fragment;->onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V

    #@139
    .line 932
    :cond_f
    :goto_3
    :pswitch_1
    const/4 v0, 0x1

    #@13a
    if-le p2, v0, :cond_17

    #@13c
    .line 933
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@13e
    if-eqz v0, :cond_10

    #@140
    const-string v0, "FragmentManager"

    #@142
    new-instance v1, Ljava/lang/StringBuilder;

    #@144
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@147
    const-string v2, "moveto ACTIVITY_CREATED: "

    #@149
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@14c
    move-result-object v1

    #@14d
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@150
    move-result-object v1

    #@151
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@154
    move-result-object v1

    #@155
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@158
    .line 934
    :cond_10
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mFromLayout:Z

    #@15a
    if-nez v0, :cond_15

    #@15c
    .line 935
    const/4 v7, 0x0

    #@15d
    .line 936
    .local v7, "container":Landroid/view/ViewGroup;
    iget v0, p1, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@15f
    if-eqz v0, :cond_11

    #@161
    .line 937
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mContainer:Landroid/support/v4/app/FragmentContainer;

    #@163
    iget v1, p1, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@165
    invoke-interface {v0, v1}, Landroid/support/v4/app/FragmentContainer;->findViewById(I)Landroid/view/View;

    #@168
    move-result-object v7

    #@169
    .end local v7    # "container":Landroid/view/ViewGroup;
    check-cast v7, Landroid/view/ViewGroup;

    #@16b
    .line 938
    .restart local v7    # "container":Landroid/view/ViewGroup;
    if-nez v7, :cond_11

    #@16d
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mRestored:Z

    #@16f
    if-nez v0, :cond_11

    #@171
    .line 939
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@173
    new-instance v1, Ljava/lang/StringBuilder;

    #@175
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@178
    const-string v2, "No view found for id 0x"

    #@17a
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@17d
    move-result-object v1

    #@17e
    iget v2, p1, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@180
    invoke-static {v2}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@183
    move-result-object v2

    #@184
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@187
    move-result-object v1

    #@188
    const-string v2, " ("

    #@18a
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@18d
    move-result-object v1

    #@18e
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->getResources()Landroid/content/res/Resources;

    #@191
    move-result-object v2

    #@192
    iget v3, p1, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@194
    invoke-virtual {v2, v3}, Landroid/content/res/Resources;->getResourceName(I)Ljava/lang/String;

    #@197
    move-result-object v2

    #@198
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@19b
    move-result-object v1

    #@19c
    const-string v2, ") for fragment "

    #@19e
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1a1
    move-result-object v1

    #@1a2
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@1a5
    move-result-object v1

    #@1a6
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1a9
    move-result-object v1

    #@1aa
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@1ad
    invoke-direct {p0, v0}, Landroid/support/v4/app/FragmentManagerImpl;->throwException(Ljava/lang/RuntimeException;)V

    #@1b0
    .line 946
    :cond_11
    iput-object v7, p1, Landroid/support/v4/app/Fragment;->mContainer:Landroid/view/ViewGroup;

    #@1b2
    .line 947
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@1b4
    invoke-virtual {p1, v0}, Landroid/support/v4/app/Fragment;->getLayoutInflater(Landroid/os/Bundle;)Landroid/view/LayoutInflater;

    #@1b7
    move-result-object v0

    #@1b8
    iget-object v1, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@1ba
    invoke-virtual {p1, v0, v7, v1}, Landroid/support/v4/app/Fragment;->performCreateView(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;

    #@1bd
    move-result-object v0

    #@1be
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@1c0
    .line 949
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@1c2
    if-eqz v0, :cond_1c

    #@1c4
    .line 950
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@1c6
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mInnerView:Landroid/view/View;

    #@1c8
    .line 951
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@1ca
    invoke-static {v0}, Landroid/support/v4/app/NoSaveStateFrameLayout;->wrap(Landroid/view/View;)Landroid/view/ViewGroup;

    #@1cd
    move-result-object v0

    #@1ce
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@1d0
    .line 952
    if-eqz v7, :cond_13

    #@1d2
    .line 953
    const/4 v0, 0x1

    #@1d3
    invoke-virtual {p0, p1, p3, v0, p4}, Landroid/support/v4/app/FragmentManagerImpl;->loadAnimation(Landroid/support/v4/app/Fragment;IZI)Landroid/view/animation/Animation;

    #@1d6
    move-result-object v6

    #@1d7
    .line 955
    .local v6, "anim":Landroid/view/animation/Animation;
    if-eqz v6, :cond_12

    #@1d9
    .line 956
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@1db
    invoke-virtual {v0, v6}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    #@1de
    .line 958
    :cond_12
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@1e0
    invoke-virtual {v7, v0}, Landroid/view/ViewGroup;->addView(Landroid/view/View;)V

    #@1e3
    .line 960
    .end local v6    # "anim":Landroid/view/animation/Animation;
    :cond_13
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@1e5
    if-eqz v0, :cond_14

    #@1e7
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@1e9
    const/16 v1, 0x8

    #@1eb
    invoke-virtual {v0, v1}, Landroid/view/View;->setVisibility(I)V

    #@1ee
    .line 961
    :cond_14
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@1f0
    iget-object v1, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@1f2
    invoke-virtual {p1, v0, v1}, Landroid/support/v4/app/Fragment;->onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V

    #@1f5
    .line 967
    .end local v7    # "container":Landroid/view/ViewGroup;
    :cond_15
    :goto_4
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@1f7
    invoke-virtual {p1, v0}, Landroid/support/v4/app/Fragment;->performActivityCreated(Landroid/os/Bundle;)V

    #@1fa
    .line 968
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@1fc
    if-eqz v0, :cond_16

    #@1fe
    .line 969
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@200
    invoke-virtual {p1, v0}, Landroid/support/v4/app/Fragment;->restoreViewState(Landroid/os/Bundle;)V

    #@203
    .line 971
    :cond_16
    const/4 v0, 0x0

    #@204
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@206
    .line 975
    :cond_17
    :pswitch_2
    const/4 v0, 0x3

    #@207
    if-le p2, v0, :cond_19

    #@209
    .line 976
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@20b
    if-eqz v0, :cond_18

    #@20d
    const-string v0, "FragmentManager"

    #@20f
    new-instance v1, Ljava/lang/StringBuilder;

    #@211
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@214
    const-string v2, "moveto STARTED: "

    #@216
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@219
    move-result-object v1

    #@21a
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@21d
    move-result-object v1

    #@21e
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@221
    move-result-object v1

    #@222
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@225
    .line 977
    :cond_18
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->performStart()V

    #@228
    .line 980
    :cond_19
    :pswitch_3
    const/4 v0, 0x4

    #@229
    if-le p2, v0, :cond_6

    #@22b
    .line 981
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@22d
    if-eqz v0, :cond_1a

    #@22f
    const-string v0, "FragmentManager"

    #@231
    new-instance v1, Ljava/lang/StringBuilder;

    #@233
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@236
    const-string v2, "moveto RESUMED: "

    #@238
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@23b
    move-result-object v1

    #@23c
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@23f
    move-result-object v1

    #@240
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@243
    move-result-object v1

    #@244
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@247
    .line 982
    :cond_1a
    const/4 v0, 0x1

    #@248
    iput-boolean v0, p1, Landroid/support/v4/app/Fragment;->mResumed:Z

    #@24a
    .line 983
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->performResume()V

    #@24d
    .line 984
    const/4 v0, 0x0

    #@24e
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@250
    .line 985
    const/4 v0, 0x0

    #@251
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedViewState:Landroid/util/SparseArray;

    #@253
    goto/16 :goto_1

    #@255
    .line 928
    :cond_1b
    const/4 v0, 0x0

    #@256
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mInnerView:Landroid/view/View;

    #@258
    goto/16 :goto_3

    #@25a
    .line 963
    .restart local v7    # "container":Landroid/view/ViewGroup;
    :cond_1c
    const/4 v0, 0x0

    #@25b
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mInnerView:Landroid/view/View;

    #@25d
    goto :goto_4

    #@25e
    .line 988
    .end local v7    # "container":Landroid/view/ViewGroup;
    :cond_1d
    iget v0, p1, Landroid/support/v4/app/Fragment;->mState:I

    #@260
    if-le v0, p2, :cond_6

    #@262
    .line 989
    iget v0, p1, Landroid/support/v4/app/Fragment;->mState:I

    #@264
    packed-switch v0, :pswitch_data_1

    #@267
    goto/16 :goto_1

    #@269
    .line 1052
    :cond_1e
    :goto_5
    :pswitch_4
    const/4 v0, 0x1

    #@26a
    if-ge p2, v0, :cond_6

    #@26c
    .line 1053
    iget-boolean v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mDestroyed:Z

    #@26e
    if-eqz v0, :cond_1f

    #@270
    .line 1054
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mAnimatingAway:Landroid/view/View;

    #@272
    if-eqz v0, :cond_1f

    #@274
    .line 1061
    iget-object v9, p1, Landroid/support/v4/app/Fragment;->mAnimatingAway:Landroid/view/View;

    #@276
    .line 1062
    .local v9, "v":Landroid/view/View;
    const/4 v0, 0x0

    #@277
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mAnimatingAway:Landroid/view/View;

    #@279
    .line 1063
    invoke-virtual {v9}, Landroid/view/View;->clearAnimation()V

    #@27c
    .line 1066
    .end local v9    # "v":Landroid/view/View;
    :cond_1f
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mAnimatingAway:Landroid/view/View;

    #@27e
    if-eqz v0, :cond_2b

    #@280
    .line 1071
    iput p2, p1, Landroid/support/v4/app/Fragment;->mStateAfterAnimating:I

    #@282
    .line 1072
    const/4 p2, 0x1

    #@283
    goto/16 :goto_1

    #@285
    .line 991
    :pswitch_5
    const/4 v0, 0x5

    #@286
    if-ge p2, v0, :cond_21

    #@288
    .line 992
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@28a
    if-eqz v0, :cond_20

    #@28c
    const-string v0, "FragmentManager"

    #@28e
    new-instance v1, Ljava/lang/StringBuilder;

    #@290
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@293
    const-string v2, "movefrom RESUMED: "

    #@295
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@298
    move-result-object v1

    #@299
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@29c
    move-result-object v1

    #@29d
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2a0
    move-result-object v1

    #@2a1
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@2a4
    .line 993
    :cond_20
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->performPause()V

    #@2a7
    .line 994
    const/4 v0, 0x0

    #@2a8
    iput-boolean v0, p1, Landroid/support/v4/app/Fragment;->mResumed:Z

    #@2aa
    .line 997
    :cond_21
    :pswitch_6
    const/4 v0, 0x4

    #@2ab
    if-ge p2, v0, :cond_23

    #@2ad
    .line 998
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@2af
    if-eqz v0, :cond_22

    #@2b1
    const-string v0, "FragmentManager"

    #@2b3
    new-instance v1, Ljava/lang/StringBuilder;

    #@2b5
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@2b8
    const-string v2, "movefrom STARTED: "

    #@2ba
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2bd
    move-result-object v1

    #@2be
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@2c1
    move-result-object v1

    #@2c2
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2c5
    move-result-object v1

    #@2c6
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@2c9
    .line 999
    :cond_22
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->performStop()V

    #@2cc
    .line 1002
    :cond_23
    :pswitch_7
    const/4 v0, 0x3

    #@2cd
    if-ge p2, v0, :cond_25

    #@2cf
    .line 1003
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@2d1
    if-eqz v0, :cond_24

    #@2d3
    const-string v0, "FragmentManager"

    #@2d5
    new-instance v1, Ljava/lang/StringBuilder;

    #@2d7
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@2da
    const-string v2, "movefrom STOPPED: "

    #@2dc
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2df
    move-result-object v1

    #@2e0
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@2e3
    move-result-object v1

    #@2e4
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2e7
    move-result-object v1

    #@2e8
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@2eb
    .line 1004
    :cond_24
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->performReallyStop()V

    #@2ee
    .line 1007
    :cond_25
    :pswitch_8
    const/4 v0, 0x2

    #@2ef
    if-ge p2, v0, :cond_1e

    #@2f1
    .line 1008
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@2f3
    if-eqz v0, :cond_26

    #@2f5
    const-string v0, "FragmentManager"

    #@2f7
    new-instance v1, Ljava/lang/StringBuilder;

    #@2f9
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@2fc
    const-string v2, "movefrom ACTIVITY_CREATED: "

    #@2fe
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@301
    move-result-object v1

    #@302
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@305
    move-result-object v1

    #@306
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@309
    move-result-object v1

    #@30a
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@30d
    .line 1009
    :cond_26
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@30f
    if-eqz v0, :cond_27

    #@311
    .line 1012
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@313
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentActivity;->isFinishing()Z

    #@316
    move-result v0

    #@317
    if-nez v0, :cond_27

    #@319
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedViewState:Landroid/util/SparseArray;

    #@31b
    if-nez v0, :cond_27

    #@31d
    .line 1013
    invoke-virtual {p0, p1}, Landroid/support/v4/app/FragmentManagerImpl;->saveFragmentViewState(Landroid/support/v4/app/Fragment;)V

    #@320
    .line 1016
    :cond_27
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->performDestroyView()V

    #@323
    .line 1017
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@325
    if-eqz v0, :cond_2a

    #@327
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mContainer:Landroid/view/ViewGroup;

    #@329
    if-eqz v0, :cond_2a

    #@32b
    .line 1018
    const/4 v6, 0x0

    #@32c
    .line 1019
    .restart local v6    # "anim":Landroid/view/animation/Animation;
    iget v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCurState:I

    #@32e
    if-lez v0, :cond_28

    #@330
    iget-boolean v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mDestroyed:Z

    #@332
    if-nez v0, :cond_28

    #@334
    .line 1020
    const/4 v0, 0x0

    #@335
    invoke-virtual {p0, p1, p3, v0, p4}, Landroid/support/v4/app/FragmentManagerImpl;->loadAnimation(Landroid/support/v4/app/Fragment;IZI)Landroid/view/animation/Animation;

    #@338
    move-result-object v6

    #@339
    .line 1023
    :cond_28
    if-eqz v6, :cond_29

    #@33b
    .line 1024
    move-object v8, p1

    #@33c
    .line 1025
    .local v8, "fragment":Landroid/support/v4/app/Fragment;
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@33e
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mAnimatingAway:Landroid/view/View;

    #@340
    .line 1026
    iput p2, p1, Landroid/support/v4/app/Fragment;->mStateAfterAnimating:I

    #@342
    .line 1027
    new-instance v0, Landroid/support/v4/app/FragmentManagerImpl$5;

    #@344
    invoke-direct {v0, p0, v8}, Landroid/support/v4/app/FragmentManagerImpl$5;-><init>(Landroid/support/v4/app/FragmentManagerImpl;Landroid/support/v4/app/Fragment;)V

    #@347
    invoke-virtual {v6, v0}, Landroid/view/animation/Animation;->setAnimationListener(Landroid/view/animation/Animation$AnimationListener;)V

    #@34a
    .line 1043
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@34c
    invoke-virtual {v0, v6}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    #@34f
    .line 1045
    .end local v8    # "fragment":Landroid/support/v4/app/Fragment;
    :cond_29
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mContainer:Landroid/view/ViewGroup;

    #@351
    iget-object v1, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@353
    invoke-virtual {v0, v1}, Landroid/view/ViewGroup;->removeView(Landroid/view/View;)V

    #@356
    .line 1047
    .end local v6    # "anim":Landroid/view/animation/Animation;
    :cond_2a
    const/4 v0, 0x0

    #@357
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mContainer:Landroid/view/ViewGroup;

    #@359
    .line 1048
    const/4 v0, 0x0

    #@35a
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@35c
    .line 1049
    const/4 v0, 0x0

    #@35d
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mInnerView:Landroid/view/View;

    #@35f
    goto/16 :goto_5

    #@361
    .line 1074
    :cond_2b
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@363
    if-eqz v0, :cond_2c

    #@365
    const-string v0, "FragmentManager"

    #@367
    new-instance v1, Ljava/lang/StringBuilder;

    #@369
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@36c
    const-string v2, "movefrom CREATED: "

    #@36e
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@371
    move-result-object v1

    #@372
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@375
    move-result-object v1

    #@376
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@379
    move-result-object v1

    #@37a
    invoke-static {v0, v1}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@37d
    .line 1075
    :cond_2c
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mRetaining:Z

    #@37f
    if-nez v0, :cond_2d

    #@381
    .line 1076
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->performDestroy()V

    #@384
    .line 1079
    :cond_2d
    const/4 v0, 0x0

    #@385
    iput-boolean v0, p1, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@387
    .line 1080
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->onDetach()V

    #@38a
    .line 1081
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mCalled:Z

    #@38c
    if-nez v0, :cond_2e

    #@38e
    .line 1082
    new-instance v0, Landroid/support/v4/app/SuperNotCalledException;

    #@390
    new-instance v1, Ljava/lang/StringBuilder;

    #@392
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@395
    const-string v2, "Fragment "

    #@397
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@39a
    move-result-object v1

    #@39b
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@39e
    move-result-object v1

    #@39f
    const-string v2, " did not call through to super.onDetach()"

    #@3a1
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@3a4
    move-result-object v1

    #@3a5
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@3a8
    move-result-object v1

    #@3a9
    invoke-direct {v0, v1}, Landroid/support/v4/app/SuperNotCalledException;-><init>(Ljava/lang/String;)V

    #@3ac
    throw v0

    #@3ad
    .line 1085
    :cond_2e
    if-nez p5, :cond_6

    #@3af
    .line 1086
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mRetaining:Z

    #@3b1
    if-nez v0, :cond_2f

    #@3b3
    .line 1087
    invoke-virtual {p0, p1}, Landroid/support/v4/app/FragmentManagerImpl;->makeInactive(Landroid/support/v4/app/Fragment;)V

    #@3b6
    goto/16 :goto_1

    #@3b8
    .line 1089
    :cond_2f
    const/4 v0, 0x0

    #@3b9
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@3bb
    .line 1090
    const/4 v0, 0x0

    #@3bc
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mParentFragment:Landroid/support/v4/app/Fragment;

    #@3be
    .line 1091
    const/4 v0, 0x0

    #@3bf
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@3c1
    .line 1092
    const/4 v0, 0x0

    #@3c2
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mChildFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@3c4
    goto/16 :goto_1

    #@3c6
    .line 876
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_2
        :pswitch_3
    .end packed-switch

    #@3d4
    .line 989
    :pswitch_data_1
    .packed-switch 0x1
        :pswitch_4
        :pswitch_8
        :pswitch_7
        :pswitch_6
        :pswitch_5
    .end packed-switch
.end method

.method public noteStateNotSaved()V
    .locals 1

    #@0
    .prologue
    .line 1907
    const/4 v0, 0x0

    #@1
    iput-boolean v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateSaved:Z

    #@3
    .line 1908
    return-void
.end method

.method public onCreateView(Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)Landroid/view/View;
    .locals 12
    .param p1, "name"    # Ljava/lang/String;
    .param p2, "context"    # Landroid/content/Context;
    .param p3, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    const/4 v3, 0x0

    #@2
    const/4 v5, -0x1

    #@3
    const/4 v2, 0x1

    #@4
    .line 2110
    const-string v4, "fragment"

    #@6
    invoke-virtual {v4, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@9
    move-result v4

    #@a
    if-nez v4, :cond_1

    #@c
    .line 2198
    :cond_0
    :goto_0
    return-object v0

    #@d
    .line 2114
    :cond_1
    const-string v4, "class"

    #@f
    invoke-interface {p3, v0, v4}, Landroid/util/AttributeSet;->getAttributeValue(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    #@12
    move-result-object v8

    #@13
    .line 2115
    .local v8, "fname":Ljava/lang/String;
    sget-object v4, Landroid/support/v4/app/FragmentManagerImpl$FragmentTag;->Fragment:[I

    #@15
    invoke-virtual {p2, p3, v4}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;

    #@18
    move-result-object v6

    #@19
    .line 2116
    .local v6, "a":Landroid/content/res/TypedArray;
    if-nez v8, :cond_2

    #@1b
    .line 2117
    invoke-virtual {v6, v3}, Landroid/content/res/TypedArray;->getString(I)Ljava/lang/String;

    #@1e
    move-result-object v8

    #@1f
    .line 2119
    :cond_2
    invoke-virtual {v6, v2, v5}, Landroid/content/res/TypedArray;->getResourceId(II)I

    #@22
    move-result v9

    #@23
    .line 2120
    .local v9, "id":I
    const/4 v4, 0x2

    #@24
    invoke-virtual {v6, v4}, Landroid/content/res/TypedArray;->getString(I)Ljava/lang/String;

    #@27
    move-result-object v11

    #@28
    .line 2121
    .local v11, "tag":Ljava/lang/String;
    invoke-virtual {v6}, Landroid/content/res/TypedArray;->recycle()V

    #@2b
    .line 2123
    iget-object v4, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@2d
    invoke-static {v4, v8}, Landroid/support/v4/app/Fragment;->isSupportFragmentClass(Landroid/content/Context;Ljava/lang/String;)Z

    #@30
    move-result v4

    #@31
    if-eqz v4, :cond_0

    #@33
    .line 2129
    const/4 v10, 0x0

    #@34
    .line 2130
    .local v10, "parent":Landroid/view/View;
    if-eqz v10, :cond_3

    #@36
    invoke-virtual {v10}, Landroid/view/View;->getId()I

    #@39
    move-result v7

    #@3a
    .line 2131
    .local v7, "containerId":I
    :goto_1
    if-ne v7, v5, :cond_4

    #@3c
    if-ne v9, v5, :cond_4

    #@3e
    if-nez v11, :cond_4

    #@40
    .line 2132
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@42
    new-instance v2, Ljava/lang/StringBuilder;

    #@44
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@47
    invoke-interface {p3}, Landroid/util/AttributeSet;->getPositionDescription()Ljava/lang/String;

    #@4a
    move-result-object v3

    #@4b
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@4e
    move-result-object v2

    #@4f
    const-string v3, ": Must specify unique android:id, android:tag, or have a parent with an id for "

    #@51
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@54
    move-result-object v2

    #@55
    invoke-virtual {v2, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@58
    move-result-object v2

    #@59
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@5c
    move-result-object v2

    #@5d
    invoke-direct {v0, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@60
    throw v0

    #@61
    .end local v7    # "containerId":I
    :cond_3
    move v7, v3

    #@62
    .line 2130
    goto :goto_1

    #@63
    .line 2139
    .restart local v7    # "containerId":I
    :cond_4
    if-eq v9, v5, :cond_9

    #@65
    invoke-virtual {p0, v9}, Landroid/support/v4/app/FragmentManagerImpl;->findFragmentById(I)Landroid/support/v4/app/Fragment;

    #@68
    move-result-object v1

    #@69
    .line 2140
    .local v1, "fragment":Landroid/support/v4/app/Fragment;
    :goto_2
    if-nez v1, :cond_5

    #@6b
    if-eqz v11, :cond_5

    #@6d
    .line 2141
    invoke-virtual {p0, v11}, Landroid/support/v4/app/FragmentManagerImpl;->findFragmentByTag(Ljava/lang/String;)Landroid/support/v4/app/Fragment;

    #@70
    move-result-object v1

    #@71
    .line 2143
    :cond_5
    if-nez v1, :cond_6

    #@73
    if-eq v7, v5, :cond_6

    #@75
    .line 2144
    invoke-virtual {p0, v7}, Landroid/support/v4/app/FragmentManagerImpl;->findFragmentById(I)Landroid/support/v4/app/Fragment;

    #@78
    move-result-object v1

    #@79
    .line 2147
    :cond_6
    sget-boolean v0, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@7b
    if-eqz v0, :cond_7

    #@7d
    const-string v0, "FragmentManager"

    #@7f
    new-instance v4, Ljava/lang/StringBuilder;

    #@81
    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    #@84
    const-string v5, "onCreateView: id=0x"

    #@86
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@89
    move-result-object v4

    #@8a
    invoke-static {v9}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@8d
    move-result-object v5

    #@8e
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@91
    move-result-object v4

    #@92
    const-string v5, " fname="

    #@94
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@97
    move-result-object v4

    #@98
    invoke-virtual {v4, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@9b
    move-result-object v4

    #@9c
    const-string v5, " existing="

    #@9e
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@a1
    move-result-object v4

    #@a2
    invoke-virtual {v4, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@a5
    move-result-object v4

    #@a6
    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@a9
    move-result-object v4

    #@aa
    invoke-static {v0, v4}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@ad
    .line 2150
    :cond_7
    if-nez v1, :cond_b

    #@af
    .line 2151
    invoke-static {p2, v8}, Landroid/support/v4/app/Fragment;->instantiate(Landroid/content/Context;Ljava/lang/String;)Landroid/support/v4/app/Fragment;

    #@b2
    move-result-object v1

    #@b3
    .line 2152
    iput-boolean v2, v1, Landroid/support/v4/app/Fragment;->mFromLayout:Z

    #@b5
    .line 2153
    if-eqz v9, :cond_a

    #@b7
    move v0, v9

    #@b8
    :goto_3
    iput v0, v1, Landroid/support/v4/app/Fragment;->mFragmentId:I

    #@ba
    .line 2154
    iput v7, v1, Landroid/support/v4/app/Fragment;->mContainerId:I

    #@bc
    .line 2155
    iput-object v11, v1, Landroid/support/v4/app/Fragment;->mTag:Ljava/lang/String;

    #@be
    .line 2156
    iput-boolean v2, v1, Landroid/support/v4/app/Fragment;->mInLayout:Z

    #@c0
    .line 2157
    iput-object p0, v1, Landroid/support/v4/app/Fragment;->mFragmentManager:Landroid/support/v4/app/FragmentManagerImpl;

    #@c2
    .line 2158
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@c4
    iget-object v4, v1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@c6
    invoke-virtual {v1, v0, p3, v4}, Landroid/support/v4/app/Fragment;->onInflate(Landroid/app/Activity;Landroid/util/AttributeSet;Landroid/os/Bundle;)V

    #@c9
    .line 2159
    invoke-virtual {p0, v1, v2}, Landroid/support/v4/app/FragmentManagerImpl;->addFragment(Landroid/support/v4/app/Fragment;Z)V

    #@cc
    .line 2182
    :cond_8
    :goto_4
    iget v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCurState:I

    #@ce
    if-ge v0, v2, :cond_d

    #@d0
    iget-boolean v0, v1, Landroid/support/v4/app/Fragment;->mFromLayout:Z

    #@d2
    if-eqz v0, :cond_d

    #@d4
    move-object v0, p0

    #@d5
    move v4, v3

    #@d6
    move v5, v3

    #@d7
    .line 2183
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(Landroid/support/v4/app/Fragment;IIIZ)V

    #@da
    .line 2188
    :goto_5
    iget-object v0, v1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@dc
    if-nez v0, :cond_e

    #@de
    .line 2189
    new-instance v0, Ljava/lang/IllegalStateException;

    #@e0
    new-instance v2, Ljava/lang/StringBuilder;

    #@e2
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@e5
    const-string v3, "Fragment "

    #@e7
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@ea
    move-result-object v2

    #@eb
    invoke-virtual {v2, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@ee
    move-result-object v2

    #@ef
    const-string v3, " did not create a view."

    #@f1
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@f4
    move-result-object v2

    #@f5
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@f8
    move-result-object v2

    #@f9
    invoke-direct {v0, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@fc
    throw v0

    #@fd
    .end local v1    # "fragment":Landroid/support/v4/app/Fragment;
    :cond_9
    move-object v1, v0

    #@fe
    .line 2139
    goto/16 :goto_2

    #@100
    .restart local v1    # "fragment":Landroid/support/v4/app/Fragment;
    :cond_a
    move v0, v7

    #@101
    .line 2153
    goto :goto_3

    #@102
    .line 2161
    :cond_b
    iget-boolean v0, v1, Landroid/support/v4/app/Fragment;->mInLayout:Z

    #@104
    if-eqz v0, :cond_c

    #@106
    .line 2164
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@108
    new-instance v2, Ljava/lang/StringBuilder;

    #@10a
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@10d
    invoke-interface {p3}, Landroid/util/AttributeSet;->getPositionDescription()Ljava/lang/String;

    #@110
    move-result-object v3

    #@111
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@114
    move-result-object v2

    #@115
    const-string v3, ": Duplicate id 0x"

    #@117
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@11a
    move-result-object v2

    #@11b
    invoke-static {v9}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@11e
    move-result-object v3

    #@11f
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@122
    move-result-object v2

    #@123
    const-string v3, ", tag "

    #@125
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@128
    move-result-object v2

    #@129
    invoke-virtual {v2, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12c
    move-result-object v2

    #@12d
    const-string v3, ", or parent id 0x"

    #@12f
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@132
    move-result-object v2

    #@133
    invoke-static {v7}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@136
    move-result-object v3

    #@137
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@13a
    move-result-object v2

    #@13b
    const-string v3, " with another fragment for "

    #@13d
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@140
    move-result-object v2

    #@141
    invoke-virtual {v2, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@144
    move-result-object v2

    #@145
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@148
    move-result-object v2

    #@149
    invoke-direct {v0, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@14c
    throw v0

    #@14d
    .line 2171
    :cond_c
    iput-boolean v2, v1, Landroid/support/v4/app/Fragment;->mInLayout:Z

    #@14f
    .line 2175
    iget-boolean v0, v1, Landroid/support/v4/app/Fragment;->mRetaining:Z

    #@151
    if-nez v0, :cond_8

    #@153
    .line 2176
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@155
    iget-object v4, v1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@157
    invoke-virtual {v1, v0, p3, v4}, Landroid/support/v4/app/Fragment;->onInflate(Landroid/app/Activity;Landroid/util/AttributeSet;Landroid/os/Bundle;)V

    #@15a
    goto/16 :goto_4

    #@15c
    .line 2185
    :cond_d
    invoke-virtual {p0, v1}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(Landroid/support/v4/app/Fragment;)V

    #@15f
    goto/16 :goto_5

    #@161
    .line 2192
    :cond_e
    if-eqz v9, :cond_f

    #@163
    .line 2193
    iget-object v0, v1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@165
    invoke-virtual {v0, v9}, Landroid/view/View;->setId(I)V

    #@168
    .line 2195
    :cond_f
    iget-object v0, v1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@16a
    invoke-virtual {v0}, Landroid/view/View;->getTag()Ljava/lang/Object;

    #@16d
    move-result-object v0

    #@16e
    if-nez v0, :cond_10

    #@170
    .line 2196
    iget-object v0, v1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@172
    invoke-virtual {v0, v11}, Landroid/view/View;->setTag(Ljava/lang/Object;)V

    #@175
    .line 2198
    :cond_10
    iget-object v0, v1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@177
    goto/16 :goto_0
.end method

.method public performPendingDeferredStart(Landroid/support/v4/app/Fragment;)V
    .locals 6
    .param p1, "f"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 835
    iget-boolean v0, p1, Landroid/support/v4/app/Fragment;->mDeferStart:Z

    #@3
    if-eqz v0, :cond_0

    #@5
    .line 836
    iget-boolean v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mExecutingActions:Z

    #@7
    if-eqz v0, :cond_1

    #@9
    .line 838
    const/4 v0, 0x1

    #@a
    iput-boolean v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mHavePendingDeferredStart:Z

    #@c
    .line 844
    :cond_0
    :goto_0
    return-void

    #@d
    .line 841
    :cond_1
    iput-boolean v3, p1, Landroid/support/v4/app/Fragment;->mDeferStart:Z

    #@f
    .line 842
    iget v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mCurState:I

    #@11
    move-object v0, p0

    #@12
    move-object v1, p1

    #@13
    move v4, v3

    #@14
    move v5, v3

    #@15
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(Landroid/support/v4/app/Fragment;IIIZ)V

    #@18
    goto :goto_0
.end method

.method public popBackStack()V
    .locals 2

    #@0
    .prologue
    .line 491
    new-instance v0, Landroid/support/v4/app/FragmentManagerImpl$2;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v4/app/FragmentManagerImpl$2;-><init>(Landroid/support/v4/app/FragmentManagerImpl;)V

    #@5
    const/4 v1, 0x0

    #@6
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/app/FragmentManagerImpl;->enqueueAction(Ljava/lang/Runnable;Z)V

    #@9
    .line 496
    return-void
.end method

.method public popBackStack(II)V
    .locals 3
    .param p1, "id"    # I
    .param p2, "flags"    # I

    #@0
    .prologue
    .line 523
    if-gez p1, :cond_0

    #@2
    .line 524
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@4
    new-instance v1, Ljava/lang/StringBuilder;

    #@6
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@9
    const-string v2, "Bad id: "

    #@b
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@e
    move-result-object v1

    #@f
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@12
    move-result-object v1

    #@13
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@16
    move-result-object v1

    #@17
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@1a
    throw v0

    #@1b
    .line 526
    :cond_0
    new-instance v0, Landroid/support/v4/app/FragmentManagerImpl$4;

    #@1d
    invoke-direct {v0, p0, p1, p2}, Landroid/support/v4/app/FragmentManagerImpl$4;-><init>(Landroid/support/v4/app/FragmentManagerImpl;II)V

    #@20
    const/4 v1, 0x0

    #@21
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/app/FragmentManagerImpl;->enqueueAction(Ljava/lang/Runnable;Z)V

    #@24
    .line 531
    return-void
.end method

.method public popBackStack(Ljava/lang/String;I)V
    .locals 2
    .param p1, "name"    # Ljava/lang/String;
    .param p2, "flags"    # I

    #@0
    .prologue
    .line 507
    new-instance v0, Landroid/support/v4/app/FragmentManagerImpl$3;

    #@2
    invoke-direct {v0, p0, p1, p2}, Landroid/support/v4/app/FragmentManagerImpl$3;-><init>(Landroid/support/v4/app/FragmentManagerImpl;Ljava/lang/String;I)V

    #@5
    const/4 v1, 0x0

    #@6
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/app/FragmentManagerImpl;->enqueueAction(Ljava/lang/Runnable;Z)V

    #@9
    .line 512
    return-void
.end method

.method public popBackStackImmediate()Z
    .locals 4

    #@0
    .prologue
    .line 500
    invoke-direct {p0}, Landroid/support/v4/app/FragmentManagerImpl;->checkStateLoss()V

    #@3
    .line 501
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentManagerImpl;->executePendingTransactions()Z

    #@6
    .line 502
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@8
    iget-object v0, v0, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@a
    const/4 v1, 0x0

    #@b
    const/4 v2, -0x1

    #@c
    const/4 v3, 0x0

    #@d
    invoke-virtual {p0, v0, v1, v2, v3}, Landroid/support/v4/app/FragmentManagerImpl;->popBackStackState(Landroid/os/Handler;Ljava/lang/String;II)Z

    #@10
    move-result v0

    #@11
    return v0
.end method

.method public popBackStackImmediate(II)Z
    .locals 3
    .param p1, "id"    # I
    .param p2, "flags"    # I

    #@0
    .prologue
    .line 535
    invoke-direct {p0}, Landroid/support/v4/app/FragmentManagerImpl;->checkStateLoss()V

    #@3
    .line 536
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentManagerImpl;->executePendingTransactions()Z

    #@6
    .line 537
    if-gez p1, :cond_0

    #@8
    .line 538
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@a
    new-instance v1, Ljava/lang/StringBuilder;

    #@c
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@f
    const-string v2, "Bad id: "

    #@11
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@14
    move-result-object v1

    #@15
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@18
    move-result-object v1

    #@19
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1c
    move-result-object v1

    #@1d
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@20
    throw v0

    #@21
    .line 540
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@23
    iget-object v0, v0, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@25
    const/4 v1, 0x0

    #@26
    invoke-virtual {p0, v0, v1, p1, p2}, Landroid/support/v4/app/FragmentManagerImpl;->popBackStackState(Landroid/os/Handler;Ljava/lang/String;II)Z

    #@29
    move-result v0

    #@2a
    return v0
.end method

.method public popBackStackImmediate(Ljava/lang/String;I)Z
    .locals 2
    .param p1, "name"    # Ljava/lang/String;
    .param p2, "flags"    # I

    #@0
    .prologue
    .line 516
    invoke-direct {p0}, Landroid/support/v4/app/FragmentManagerImpl;->checkStateLoss()V

    #@3
    .line 517
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentManagerImpl;->executePendingTransactions()Z

    #@6
    .line 518
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@8
    iget-object v0, v0, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@a
    const/4 v1, -0x1

    #@b
    invoke-virtual {p0, v0, p1, v1, p2}, Landroid/support/v4/app/FragmentManagerImpl;->popBackStackState(Landroid/os/Handler;Ljava/lang/String;II)Z

    #@e
    move-result v0

    #@f
    return v0
.end method

.method popBackStackState(Landroid/os/Handler;Ljava/lang/String;II)Z
    .locals 12
    .param p1, "handler"    # Landroid/os/Handler;
    .param p2, "name"    # Ljava/lang/String;
    .param p3, "id"    # I
    .param p4, "flags"    # I

    #@0
    .prologue
    .line 1529
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@2
    if-nez v9, :cond_0

    #@4
    .line 1530
    const/4 v9, 0x0

    #@5
    .line 1598
    :goto_0
    return v9

    #@6
    .line 1532
    :cond_0
    if-nez p2, :cond_2

    #@8
    if-gez p3, :cond_2

    #@a
    and-int/lit8 v9, p4, 0x1

    #@c
    if-nez v9, :cond_2

    #@e
    .line 1533
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@10
    invoke-virtual {v9}, Ljava/util/ArrayList;->size()I

    #@13
    move-result v9

    #@14
    add-int/lit8 v5, v9, -0x1

    #@16
    .line 1534
    .local v5, "last":I
    if-gez v5, :cond_1

    #@18
    .line 1535
    const/4 v9, 0x0

    #@19
    goto :goto_0

    #@1a
    .line 1537
    :cond_1
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@1c
    invoke-virtual {v9, v5}, Ljava/util/ArrayList;->remove(I)Ljava/lang/Object;

    #@1f
    move-result-object v1

    #@20
    check-cast v1, Landroid/support/v4/app/BackStackRecord;

    #@22
    .line 1538
    .local v1, "bss":Landroid/support/v4/app/BackStackRecord;
    new-instance v2, Landroid/util/SparseArray;

    #@24
    invoke-direct {v2}, Landroid/util/SparseArray;-><init>()V

    #@27
    .line 1539
    .local v2, "firstOutFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    new-instance v6, Landroid/util/SparseArray;

    #@29
    invoke-direct {v6}, Landroid/util/SparseArray;-><init>()V

    #@2c
    .line 1540
    .local v6, "lastInFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    invoke-virtual {v1, v2, v6}, Landroid/support/v4/app/BackStackRecord;->calculateBackFragments(Landroid/util/SparseArray;Landroid/util/SparseArray;)V

    #@2f
    .line 1541
    const/4 v9, 0x1

    #@30
    const/4 v10, 0x0

    #@31
    invoke-virtual {v1, v9, v10, v2, v6}, Landroid/support/v4/app/BackStackRecord;->popFromBackStack(ZLandroid/support/v4/app/BackStackRecord$TransitionState;Landroid/util/SparseArray;Landroid/util/SparseArray;)Landroid/support/v4/app/BackStackRecord$TransitionState;

    #@34
    .line 1542
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentManagerImpl;->reportBackStackChanged()V

    #@37
    .line 1598
    .end local v1    # "bss":Landroid/support/v4/app/BackStackRecord;
    .end local v5    # "last":I
    :goto_1
    const/4 v9, 0x1

    #@38
    goto :goto_0

    #@39
    .line 1544
    .end local v2    # "firstOutFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    .end local v6    # "lastInFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    :cond_2
    const/4 v4, -0x1

    #@3a
    .line 1545
    .local v4, "index":I
    if-nez p2, :cond_3

    #@3c
    if-ltz p3, :cond_a

    #@3e
    .line 1548
    :cond_3
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@40
    invoke-virtual {v9}, Ljava/util/ArrayList;->size()I

    #@43
    move-result v9

    #@44
    add-int/lit8 v4, v9, -0x1

    #@46
    .line 1549
    :goto_2
    if-ltz v4, :cond_4

    #@48
    .line 1550
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@4a
    invoke-virtual {v9, v4}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@4d
    move-result-object v1

    #@4e
    check-cast v1, Landroid/support/v4/app/BackStackRecord;

    #@50
    .line 1551
    .restart local v1    # "bss":Landroid/support/v4/app/BackStackRecord;
    if-eqz p2, :cond_5

    #@52
    invoke-virtual {v1}, Landroid/support/v4/app/BackStackRecord;->getName()Ljava/lang/String;

    #@55
    move-result-object v9

    #@56
    invoke-virtual {p2, v9}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@59
    move-result v9

    #@5a
    if-eqz v9, :cond_5

    #@5c
    .line 1559
    .end local v1    # "bss":Landroid/support/v4/app/BackStackRecord;
    :cond_4
    if-gez v4, :cond_7

    #@5e
    .line 1560
    const/4 v9, 0x0

    #@5f
    goto :goto_0

    #@60
    .line 1554
    .restart local v1    # "bss":Landroid/support/v4/app/BackStackRecord;
    :cond_5
    if-ltz p3, :cond_6

    #@62
    iget v9, v1, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@64
    if-eq p3, v9, :cond_4

    #@66
    .line 1557
    :cond_6
    add-int/lit8 v4, v4, -0x1

    #@68
    .line 1558
    goto :goto_2

    #@69
    .line 1562
    .end local v1    # "bss":Landroid/support/v4/app/BackStackRecord;
    :cond_7
    and-int/lit8 v9, p4, 0x1

    #@6b
    if-eqz v9, :cond_a

    #@6d
    .line 1563
    add-int/lit8 v4, v4, -0x1

    #@6f
    .line 1565
    :goto_3
    if-ltz v4, :cond_a

    #@71
    .line 1566
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@73
    invoke-virtual {v9, v4}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@76
    move-result-object v1

    #@77
    check-cast v1, Landroid/support/v4/app/BackStackRecord;

    #@79
    .line 1567
    .restart local v1    # "bss":Landroid/support/v4/app/BackStackRecord;
    if-eqz p2, :cond_8

    #@7b
    invoke-virtual {v1}, Landroid/support/v4/app/BackStackRecord;->getName()Ljava/lang/String;

    #@7e
    move-result-object v9

    #@7f
    invoke-virtual {p2, v9}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@82
    move-result v9

    #@83
    if-nez v9, :cond_9

    #@85
    :cond_8
    if-ltz p3, :cond_a

    #@87
    iget v9, v1, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@89
    if-ne p3, v9, :cond_a

    #@8b
    .line 1569
    :cond_9
    add-int/lit8 v4, v4, -0x1

    #@8d
    .line 1570
    goto :goto_3

    #@8e
    .line 1576
    .end local v1    # "bss":Landroid/support/v4/app/BackStackRecord;
    :cond_a
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@90
    invoke-virtual {v9}, Ljava/util/ArrayList;->size()I

    #@93
    move-result v9

    #@94
    add-int/lit8 v9, v9, -0x1

    #@96
    if-ne v4, v9, :cond_b

    #@98
    .line 1577
    const/4 v9, 0x0

    #@99
    goto/16 :goto_0

    #@9b
    .line 1579
    :cond_b
    new-instance v8, Ljava/util/ArrayList;

    #@9d
    invoke-direct {v8}, Ljava/util/ArrayList;-><init>()V

    #@a0
    .line 1581
    .local v8, "states":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v4/app/BackStackRecord;>;"
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@a2
    invoke-virtual {v9}, Ljava/util/ArrayList;->size()I

    #@a5
    move-result v9

    #@a6
    add-int/lit8 v3, v9, -0x1

    #@a8
    .local v3, "i":I
    :goto_4
    if-le v3, v4, :cond_c

    #@aa
    .line 1582
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@ac
    invoke-virtual {v9, v3}, Ljava/util/ArrayList;->remove(I)Ljava/lang/Object;

    #@af
    move-result-object v9

    #@b0
    invoke-virtual {v8, v9}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@b3
    .line 1581
    add-int/lit8 v3, v3, -0x1

    #@b5
    goto :goto_4

    #@b6
    .line 1584
    :cond_c
    invoke-virtual {v8}, Ljava/util/ArrayList;->size()I

    #@b9
    move-result v9

    #@ba
    add-int/lit8 v0, v9, -0x1

    #@bc
    .line 1585
    .local v0, "LAST":I
    new-instance v2, Landroid/util/SparseArray;

    #@be
    invoke-direct {v2}, Landroid/util/SparseArray;-><init>()V

    #@c1
    .line 1586
    .restart local v2    # "firstOutFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    new-instance v6, Landroid/util/SparseArray;

    #@c3
    invoke-direct {v6}, Landroid/util/SparseArray;-><init>()V

    #@c6
    .line 1587
    .restart local v6    # "lastInFragments":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/support/v4/app/Fragment;>;"
    const/4 v3, 0x0

    #@c7
    :goto_5
    if-gt v3, v0, :cond_d

    #@c9
    .line 1588
    invoke-virtual {v8, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@cc
    move-result-object v9

    #@cd
    check-cast v9, Landroid/support/v4/app/BackStackRecord;

    #@cf
    invoke-virtual {v9, v2, v6}, Landroid/support/v4/app/BackStackRecord;->calculateBackFragments(Landroid/util/SparseArray;Landroid/util/SparseArray;)V

    #@d2
    .line 1587
    add-int/lit8 v3, v3, 0x1

    #@d4
    goto :goto_5

    #@d5
    .line 1590
    :cond_d
    const/4 v7, 0x0

    #@d6
    .line 1591
    .local v7, "state":Landroid/support/v4/app/BackStackRecord$TransitionState;
    const/4 v3, 0x0

    #@d7
    :goto_6
    if-gt v3, v0, :cond_10

    #@d9
    .line 1592
    sget-boolean v9, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@db
    if-eqz v9, :cond_e

    #@dd
    const-string v9, "FragmentManager"

    #@df
    new-instance v10, Ljava/lang/StringBuilder;

    #@e1
    invoke-direct {v10}, Ljava/lang/StringBuilder;-><init>()V

    #@e4
    const-string v11, "Popping back stack state: "

    #@e6
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@e9
    move-result-object v10

    #@ea
    invoke-virtual {v8, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@ed
    move-result-object v11

    #@ee
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@f1
    move-result-object v10

    #@f2
    invoke-virtual {v10}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@f5
    move-result-object v10

    #@f6
    invoke-static {v9, v10}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@f9
    .line 1593
    :cond_e
    invoke-virtual {v8, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@fc
    move-result-object v9

    #@fd
    check-cast v9, Landroid/support/v4/app/BackStackRecord;

    #@ff
    if-ne v3, v0, :cond_f

    #@101
    const/4 v10, 0x1

    #@102
    :goto_7
    invoke-virtual {v9, v10, v7, v2, v6}, Landroid/support/v4/app/BackStackRecord;->popFromBackStack(ZLandroid/support/v4/app/BackStackRecord$TransitionState;Landroid/util/SparseArray;Landroid/util/SparseArray;)Landroid/support/v4/app/BackStackRecord$TransitionState;

    #@105
    move-result-object v7

    #@106
    .line 1591
    add-int/lit8 v3, v3, 0x1

    #@108
    goto :goto_6

    #@109
    .line 1593
    :cond_f
    const/4 v10, 0x0

    #@10a
    goto :goto_7

    #@10b
    .line 1596
    :cond_10
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentManagerImpl;->reportBackStackChanged()V

    #@10e
    goto/16 :goto_1
.end method

.method public putFragment(Landroid/os/Bundle;Ljava/lang/String;Landroid/support/v4/app/Fragment;)V
    .locals 3
    .param p1, "bundle"    # Landroid/os/Bundle;
    .param p2, "key"    # Ljava/lang/String;
    .param p3, "fragment"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 570
    iget v0, p3, Landroid/support/v4/app/Fragment;->mIndex:I

    #@2
    if-gez v0, :cond_0

    #@4
    .line 571
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
    invoke-virtual {v1, p3}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@14
    move-result-object v1

    #@15
    const-string v2, " is not currently in the FragmentManager"

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
    invoke-direct {p0, v0}, Landroid/support/v4/app/FragmentManagerImpl;->throwException(Ljava/lang/RuntimeException;)V

    #@25
    .line 574
    :cond_0
    iget v0, p3, Landroid/support/v4/app/Fragment;->mIndex:I

    #@27
    invoke-virtual {p1, p2, v0}, Landroid/os/Bundle;->putInt(Ljava/lang/String;I)V

    #@2a
    .line 575
    return-void
.end method

.method public removeFragment(Landroid/support/v4/app/Fragment;II)V
    .locals 7
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;
    .param p2, "transition"    # I
    .param p3, "transitionStyle"    # I

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    const/4 v5, 0x0

    #@2
    .line 1212
    sget-boolean v1, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@4
    if-eqz v1, :cond_0

    #@6
    const-string v1, "FragmentManager"

    #@8
    new-instance v2, Ljava/lang/StringBuilder;

    #@a
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@d
    const-string v3, "remove: "

    #@f
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v2

    #@13
    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@16
    move-result-object v2

    #@17
    const-string v3, " nesting="

    #@19
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v2

    #@1d
    iget v3, p1, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@1f
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@22
    move-result-object v2

    #@23
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@26
    move-result-object v2

    #@27
    invoke-static {v1, v2}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@2a
    .line 1213
    :cond_0
    invoke-virtual {p1}, Landroid/support/v4/app/Fragment;->isInBackStack()Z

    #@2d
    move-result v1

    #@2e
    if-nez v1, :cond_5

    #@30
    move v6, v0

    #@31
    .line 1214
    .local v6, "inactive":Z
    :goto_0
    iget-boolean v1, p1, Landroid/support/v4/app/Fragment;->mDetached:Z

    #@33
    if-eqz v1, :cond_1

    #@35
    if-eqz v6, :cond_4

    #@37
    .line 1215
    :cond_1
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@39
    if-eqz v1, :cond_2

    #@3b
    .line 1216
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@3d
    invoke-virtual {v1, p1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    #@40
    .line 1218
    :cond_2
    iget-boolean v1, p1, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@42
    if-eqz v1, :cond_3

    #@44
    iget-boolean v1, p1, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@46
    if-eqz v1, :cond_3

    #@48
    .line 1219
    iput-boolean v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNeedMenuInvalidate:Z

    #@4a
    .line 1221
    :cond_3
    iput-boolean v5, p1, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@4c
    .line 1222
    iput-boolean v0, p1, Landroid/support/v4/app/Fragment;->mRemoving:Z

    #@4e
    .line 1223
    if-eqz v6, :cond_6

    #@50
    move v2, v5

    #@51
    :goto_1
    move-object v0, p0

    #@52
    move-object v1, p1

    #@53
    move v3, p2

    #@54
    move v4, p3

    #@55
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/app/FragmentManagerImpl;->moveToState(Landroid/support/v4/app/Fragment;IIIZ)V

    #@58
    .line 1226
    :cond_4
    return-void

    #@59
    .end local v6    # "inactive":Z
    :cond_5
    move v6, v5

    #@5a
    .line 1213
    goto :goto_0

    #@5b
    .restart local v6    # "inactive":Z
    :cond_6
    move v2, v0

    #@5c
    .line 1223
    goto :goto_1
.end method

.method public removeOnBackStackChangedListener(Landroid/support/v4/app/FragmentManager$OnBackStackChangedListener;)V
    .locals 1
    .param p1, "listener"    # Landroid/support/v4/app/FragmentManager$OnBackStackChangedListener;

    #@0
    .prologue
    .line 563
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackChangeListeners:Ljava/util/ArrayList;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 564
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackChangeListeners:Ljava/util/ArrayList;

    #@6
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->remove(Ljava/lang/Object;)Z

    #@9
    .line 566
    :cond_0
    return-void
.end method

.method reportBackStackChanged()V
    .locals 2

    #@0
    .prologue
    .line 1513
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackChangeListeners:Ljava/util/ArrayList;

    #@2
    if-eqz v1, :cond_0

    #@4
    .line 1514
    const/4 v0, 0x0

    #@5
    .local v0, "i":I
    :goto_0
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackChangeListeners:Ljava/util/ArrayList;

    #@7
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    #@a
    move-result v1

    #@b
    if-ge v0, v1, :cond_0

    #@d
    .line 1515
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackChangeListeners:Ljava/util/ArrayList;

    #@f
    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@12
    move-result-object v1

    #@13
    check-cast v1, Landroid/support/v4/app/FragmentManager$OnBackStackChangedListener;

    #@15
    invoke-interface {v1}, Landroid/support/v4/app/FragmentManager$OnBackStackChangedListener;->onBackStackChanged()V

    #@18
    .line 1514
    add-int/lit8 v0, v0, 0x1

    #@1a
    goto :goto_0

    #@1b
    .line 1518
    .end local v0    # "i":I
    :cond_0
    return-void
.end method

.method restoreAllState(Landroid/os/Parcelable;Ljava/util/ArrayList;)V
    .locals 12
    .param p1, "state"    # Landroid/os/Parcelable;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/os/Parcelable;",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .local p2, "nonConfig":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v4/app/Fragment;>;"
    const/4 v11, 0x0

    #@1
    const/4 v10, 0x0

    #@2
    .line 1788
    if-nez p1, :cond_1

    #@4
    .line 1896
    :cond_0
    :goto_0
    return-void

    #@5
    :cond_1
    move-object v2, p1

    #@6
    .line 1789
    check-cast v2, Landroid/support/v4/app/FragmentManagerState;

    #@8
    .line 1790
    .local v2, "fms":Landroid/support/v4/app/FragmentManagerState;
    iget-object v7, v2, Landroid/support/v4/app/FragmentManagerState;->mActive:[Landroid/support/v4/app/FragmentState;

    #@a
    if-eqz v7, :cond_0

    #@c
    .line 1794
    if-eqz p2, :cond_4

    #@e
    .line 1795
    const/4 v4, 0x0

    #@f
    .local v4, "i":I
    :goto_1
    invoke-virtual {p2}, Ljava/util/ArrayList;->size()I

    #@12
    move-result v7

    #@13
    if-ge v4, v7, :cond_4

    #@15
    .line 1796
    invoke-virtual {p2, v4}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@18
    move-result-object v1

    #@19
    check-cast v1, Landroid/support/v4/app/Fragment;

    #@1b
    .line 1797
    .local v1, "f":Landroid/support/v4/app/Fragment;
    sget-boolean v7, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@1d
    if-eqz v7, :cond_2

    #@1f
    const-string v7, "FragmentManager"

    #@21
    new-instance v8, Ljava/lang/StringBuilder;

    #@23
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@26
    const-string v9, "restoreAllState: re-attaching retained "

    #@28
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2b
    move-result-object v8

    #@2c
    invoke-virtual {v8, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@2f
    move-result-object v8

    #@30
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@33
    move-result-object v8

    #@34
    invoke-static {v7, v8}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@37
    .line 1798
    :cond_2
    iget-object v7, v2, Landroid/support/v4/app/FragmentManagerState;->mActive:[Landroid/support/v4/app/FragmentState;

    #@39
    iget v8, v1, Landroid/support/v4/app/Fragment;->mIndex:I

    #@3b
    aget-object v3, v7, v8

    #@3d
    .line 1799
    .local v3, "fs":Landroid/support/v4/app/FragmentState;
    iput-object v1, v3, Landroid/support/v4/app/FragmentState;->mInstance:Landroid/support/v4/app/Fragment;

    #@3f
    .line 1800
    iput-object v10, v1, Landroid/support/v4/app/Fragment;->mSavedViewState:Landroid/util/SparseArray;

    #@41
    .line 1801
    iput v11, v1, Landroid/support/v4/app/Fragment;->mBackStackNesting:I

    #@43
    .line 1802
    iput-boolean v11, v1, Landroid/support/v4/app/Fragment;->mInLayout:Z

    #@45
    .line 1803
    iput-boolean v11, v1, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@47
    .line 1804
    iput-object v10, v1, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@49
    .line 1805
    iget-object v7, v3, Landroid/support/v4/app/FragmentState;->mSavedFragmentState:Landroid/os/Bundle;

    #@4b
    if-eqz v7, :cond_3

    #@4d
    .line 1806
    iget-object v7, v3, Landroid/support/v4/app/FragmentState;->mSavedFragmentState:Landroid/os/Bundle;

    #@4f
    iget-object v8, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@51
    invoke-virtual {v8}, Landroid/support/v4/app/FragmentActivity;->getClassLoader()Ljava/lang/ClassLoader;

    #@54
    move-result-object v8

    #@55
    invoke-virtual {v7, v8}, Landroid/os/Bundle;->setClassLoader(Ljava/lang/ClassLoader;)V

    #@58
    .line 1807
    iget-object v7, v3, Landroid/support/v4/app/FragmentState;->mSavedFragmentState:Landroid/os/Bundle;

    #@5a
    const-string v8, "android:view_state"

    #@5c
    invoke-virtual {v7, v8}, Landroid/os/Bundle;->getSparseParcelableArray(Ljava/lang/String;)Landroid/util/SparseArray;

    #@5f
    move-result-object v7

    #@60
    iput-object v7, v1, Landroid/support/v4/app/Fragment;->mSavedViewState:Landroid/util/SparseArray;

    #@62
    .line 1809
    iget-object v7, v3, Landroid/support/v4/app/FragmentState;->mSavedFragmentState:Landroid/os/Bundle;

    #@64
    iput-object v7, v1, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@66
    .line 1795
    :cond_3
    add-int/lit8 v4, v4, 0x1

    #@68
    goto :goto_1

    #@69
    .line 1816
    .end local v1    # "f":Landroid/support/v4/app/Fragment;
    .end local v3    # "fs":Landroid/support/v4/app/FragmentState;
    .end local v4    # "i":I
    :cond_4
    new-instance v7, Ljava/util/ArrayList;

    #@6b
    iget-object v8, v2, Landroid/support/v4/app/FragmentManagerState;->mActive:[Landroid/support/v4/app/FragmentState;

    #@6d
    array-length v8, v8

    #@6e
    invoke-direct {v7, v8}, Ljava/util/ArrayList;-><init>(I)V

    #@71
    iput-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@73
    .line 1817
    iget-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@75
    if-eqz v7, :cond_5

    #@77
    .line 1818
    iget-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@79
    invoke-virtual {v7}, Ljava/util/ArrayList;->clear()V

    #@7c
    .line 1820
    :cond_5
    const/4 v4, 0x0

    #@7d
    .restart local v4    # "i":I
    :goto_2
    iget-object v7, v2, Landroid/support/v4/app/FragmentManagerState;->mActive:[Landroid/support/v4/app/FragmentState;

    #@7f
    array-length v7, v7

    #@80
    if-ge v4, v7, :cond_a

    #@82
    .line 1821
    iget-object v7, v2, Landroid/support/v4/app/FragmentManagerState;->mActive:[Landroid/support/v4/app/FragmentState;

    #@84
    aget-object v3, v7, v4

    #@86
    .line 1822
    .restart local v3    # "fs":Landroid/support/v4/app/FragmentState;
    if-eqz v3, :cond_7

    #@88
    .line 1823
    iget-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@8a
    iget-object v8, p0, Landroid/support/v4/app/FragmentManagerImpl;->mParent:Landroid/support/v4/app/Fragment;

    #@8c
    invoke-virtual {v3, v7, v8}, Landroid/support/v4/app/FragmentState;->instantiate(Landroid/support/v4/app/FragmentActivity;Landroid/support/v4/app/Fragment;)Landroid/support/v4/app/Fragment;

    #@8f
    move-result-object v1

    #@90
    .line 1824
    .restart local v1    # "f":Landroid/support/v4/app/Fragment;
    sget-boolean v7, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@92
    if-eqz v7, :cond_6

    #@94
    const-string v7, "FragmentManager"

    #@96
    new-instance v8, Ljava/lang/StringBuilder;

    #@98
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@9b
    const-string v9, "restoreAllState: active #"

    #@9d
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@a0
    move-result-object v8

    #@a1
    invoke-virtual {v8, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@a4
    move-result-object v8

    #@a5
    const-string v9, ": "

    #@a7
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@aa
    move-result-object v8

    #@ab
    invoke-virtual {v8, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@ae
    move-result-object v8

    #@af
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@b2
    move-result-object v8

    #@b3
    invoke-static {v7, v8}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@b6
    .line 1825
    :cond_6
    iget-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@b8
    invoke-virtual {v7, v1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@bb
    .line 1829
    iput-object v10, v3, Landroid/support/v4/app/FragmentState;->mInstance:Landroid/support/v4/app/Fragment;

    #@bd
    .line 1820
    .end local v1    # "f":Landroid/support/v4/app/Fragment;
    :goto_3
    add-int/lit8 v4, v4, 0x1

    #@bf
    goto :goto_2

    #@c0
    .line 1831
    :cond_7
    iget-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@c2
    invoke-virtual {v7, v10}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@c5
    .line 1832
    iget-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@c7
    if-nez v7, :cond_8

    #@c9
    .line 1833
    new-instance v7, Ljava/util/ArrayList;

    #@cb
    invoke-direct {v7}, Ljava/util/ArrayList;-><init>()V

    #@ce
    iput-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@d0
    .line 1835
    :cond_8
    sget-boolean v7, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@d2
    if-eqz v7, :cond_9

    #@d4
    const-string v7, "FragmentManager"

    #@d6
    new-instance v8, Ljava/lang/StringBuilder;

    #@d8
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@db
    const-string v9, "restoreAllState: avail #"

    #@dd
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@e0
    move-result-object v8

    #@e1
    invoke-virtual {v8, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@e4
    move-result-object v8

    #@e5
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@e8
    move-result-object v8

    #@e9
    invoke-static {v7, v8}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@ec
    .line 1836
    :cond_9
    iget-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailIndices:Ljava/util/ArrayList;

    #@ee
    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@f1
    move-result-object v8

    #@f2
    invoke-virtual {v7, v8}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@f5
    goto :goto_3

    #@f6
    .line 1841
    .end local v3    # "fs":Landroid/support/v4/app/FragmentState;
    :cond_a
    if-eqz p2, :cond_d

    #@f8
    .line 1842
    const/4 v4, 0x0

    #@f9
    :goto_4
    invoke-virtual {p2}, Ljava/util/ArrayList;->size()I

    #@fc
    move-result v7

    #@fd
    if-ge v4, v7, :cond_d

    #@ff
    .line 1843
    invoke-virtual {p2, v4}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@102
    move-result-object v1

    #@103
    check-cast v1, Landroid/support/v4/app/Fragment;

    #@105
    .line 1844
    .restart local v1    # "f":Landroid/support/v4/app/Fragment;
    iget v7, v1, Landroid/support/v4/app/Fragment;->mTargetIndex:I

    #@107
    if-ltz v7, :cond_b

    #@109
    .line 1845
    iget v7, v1, Landroid/support/v4/app/Fragment;->mTargetIndex:I

    #@10b
    iget-object v8, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@10d
    invoke-virtual {v8}, Ljava/util/ArrayList;->size()I

    #@110
    move-result v8

    #@111
    if-ge v7, v8, :cond_c

    #@113
    .line 1846
    iget-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@115
    iget v8, v1, Landroid/support/v4/app/Fragment;->mTargetIndex:I

    #@117
    invoke-virtual {v7, v8}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@11a
    move-result-object v7

    #@11b
    check-cast v7, Landroid/support/v4/app/Fragment;

    #@11d
    iput-object v7, v1, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@11f
    .line 1842
    :cond_b
    :goto_5
    add-int/lit8 v4, v4, 0x1

    #@121
    goto :goto_4

    #@122
    .line 1848
    :cond_c
    const-string v7, "FragmentManager"

    #@124
    new-instance v8, Ljava/lang/StringBuilder;

    #@126
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@129
    const-string v9, "Re-attaching retained fragment "

    #@12b
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12e
    move-result-object v8

    #@12f
    invoke-virtual {v8, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@132
    move-result-object v8

    #@133
    const-string v9, " target no longer exists: "

    #@135
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@138
    move-result-object v8

    #@139
    iget v9, v1, Landroid/support/v4/app/Fragment;->mTargetIndex:I

    #@13b
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@13e
    move-result-object v8

    #@13f
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@142
    move-result-object v8

    #@143
    invoke-static {v7, v8}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@146
    .line 1850
    iput-object v10, v1, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@148
    goto :goto_5

    #@149
    .line 1857
    .end local v1    # "f":Landroid/support/v4/app/Fragment;
    :cond_d
    iget-object v7, v2, Landroid/support/v4/app/FragmentManagerState;->mAdded:[I

    #@14b
    if-eqz v7, :cond_11

    #@14d
    .line 1858
    new-instance v7, Ljava/util/ArrayList;

    #@14f
    iget-object v8, v2, Landroid/support/v4/app/FragmentManagerState;->mAdded:[I

    #@151
    array-length v8, v8

    #@152
    invoke-direct {v7, v8}, Ljava/util/ArrayList;-><init>(I)V

    #@155
    iput-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@157
    .line 1859
    const/4 v4, 0x0

    #@158
    :goto_6
    iget-object v7, v2, Landroid/support/v4/app/FragmentManagerState;->mAdded:[I

    #@15a
    array-length v7, v7

    #@15b
    if-ge v4, v7, :cond_12

    #@15d
    .line 1860
    iget-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@15f
    iget-object v8, v2, Landroid/support/v4/app/FragmentManagerState;->mAdded:[I

    #@161
    aget v8, v8, v4

    #@163
    invoke-virtual {v7, v8}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@166
    move-result-object v1

    #@167
    check-cast v1, Landroid/support/v4/app/Fragment;

    #@169
    .line 1861
    .restart local v1    # "f":Landroid/support/v4/app/Fragment;
    if-nez v1, :cond_e

    #@16b
    .line 1862
    new-instance v7, Ljava/lang/IllegalStateException;

    #@16d
    new-instance v8, Ljava/lang/StringBuilder;

    #@16f
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@172
    const-string v9, "No instantiated fragment for index #"

    #@174
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@177
    move-result-object v8

    #@178
    iget-object v9, v2, Landroid/support/v4/app/FragmentManagerState;->mAdded:[I

    #@17a
    aget v9, v9, v4

    #@17c
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@17f
    move-result-object v8

    #@180
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@183
    move-result-object v8

    #@184
    invoke-direct {v7, v8}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@187
    invoke-direct {p0, v7}, Landroid/support/v4/app/FragmentManagerImpl;->throwException(Ljava/lang/RuntimeException;)V

    #@18a
    .line 1865
    :cond_e
    const/4 v7, 0x1

    #@18b
    iput-boolean v7, v1, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@18d
    .line 1866
    sget-boolean v7, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@18f
    if-eqz v7, :cond_f

    #@191
    const-string v7, "FragmentManager"

    #@193
    new-instance v8, Ljava/lang/StringBuilder;

    #@195
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@198
    const-string v9, "restoreAllState: added #"

    #@19a
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@19d
    move-result-object v8

    #@19e
    invoke-virtual {v8, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@1a1
    move-result-object v8

    #@1a2
    const-string v9, ": "

    #@1a4
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1a7
    move-result-object v8

    #@1a8
    invoke-virtual {v8, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@1ab
    move-result-object v8

    #@1ac
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1af
    move-result-object v8

    #@1b0
    invoke-static {v7, v8}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@1b3
    .line 1867
    :cond_f
    iget-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@1b5
    invoke-virtual {v7, v1}, Ljava/util/ArrayList;->contains(Ljava/lang/Object;)Z

    #@1b8
    move-result v7

    #@1b9
    if-eqz v7, :cond_10

    #@1bb
    .line 1868
    new-instance v7, Ljava/lang/IllegalStateException;

    #@1bd
    const-string v8, "Already added!"

    #@1bf
    invoke-direct {v7, v8}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@1c2
    throw v7

    #@1c3
    .line 1870
    :cond_10
    iget-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@1c5
    invoke-virtual {v7, v1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@1c8
    .line 1859
    add-int/lit8 v4, v4, 0x1

    #@1ca
    goto :goto_6

    #@1cb
    .line 1873
    .end local v1    # "f":Landroid/support/v4/app/Fragment;
    :cond_11
    iput-object v10, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@1cd
    .line 1877
    :cond_12
    iget-object v7, v2, Landroid/support/v4/app/FragmentManagerState;->mBackStack:[Landroid/support/v4/app/BackStackState;

    #@1cf
    if-eqz v7, :cond_15

    #@1d1
    .line 1878
    new-instance v7, Ljava/util/ArrayList;

    #@1d3
    iget-object v8, v2, Landroid/support/v4/app/FragmentManagerState;->mBackStack:[Landroid/support/v4/app/BackStackState;

    #@1d5
    array-length v8, v8

    #@1d6
    invoke-direct {v7, v8}, Ljava/util/ArrayList;-><init>(I)V

    #@1d9
    iput-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@1db
    .line 1879
    const/4 v4, 0x0

    #@1dc
    :goto_7
    iget-object v7, v2, Landroid/support/v4/app/FragmentManagerState;->mBackStack:[Landroid/support/v4/app/BackStackState;

    #@1de
    array-length v7, v7

    #@1df
    if-ge v4, v7, :cond_0

    #@1e1
    .line 1880
    iget-object v7, v2, Landroid/support/v4/app/FragmentManagerState;->mBackStack:[Landroid/support/v4/app/BackStackState;

    #@1e3
    aget-object v7, v7, v4

    #@1e5
    invoke-virtual {v7, p0}, Landroid/support/v4/app/BackStackState;->instantiate(Landroid/support/v4/app/FragmentManagerImpl;)Landroid/support/v4/app/BackStackRecord;

    #@1e8
    move-result-object v0

    #@1e9
    .line 1881
    .local v0, "bse":Landroid/support/v4/app/BackStackRecord;
    sget-boolean v7, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@1eb
    if-eqz v7, :cond_13

    #@1ed
    .line 1882
    const-string v7, "FragmentManager"

    #@1ef
    new-instance v8, Ljava/lang/StringBuilder;

    #@1f1
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@1f4
    const-string v9, "restoreAllState: back stack #"

    #@1f6
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1f9
    move-result-object v8

    #@1fa
    invoke-virtual {v8, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@1fd
    move-result-object v8

    #@1fe
    const-string v9, " (index "

    #@200
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@203
    move-result-object v8

    #@204
    iget v9, v0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@206
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@209
    move-result-object v8

    #@20a
    const-string v9, "): "

    #@20c
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@20f
    move-result-object v8

    #@210
    invoke-virtual {v8, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@213
    move-result-object v8

    #@214
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@217
    move-result-object v8

    #@218
    invoke-static {v7, v8}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@21b
    .line 1884
    new-instance v5, Landroid/support/v4/util/LogWriter;

    #@21d
    const-string v7, "FragmentManager"

    #@21f
    invoke-direct {v5, v7}, Landroid/support/v4/util/LogWriter;-><init>(Ljava/lang/String;)V

    #@222
    .line 1885
    .local v5, "logw":Landroid/support/v4/util/LogWriter;
    new-instance v6, Ljava/io/PrintWriter;

    #@224
    invoke-direct {v6, v5}, Ljava/io/PrintWriter;-><init>(Ljava/io/Writer;)V

    #@227
    .line 1886
    .local v6, "pw":Ljava/io/PrintWriter;
    const-string v7, "  "

    #@229
    invoke-virtual {v0, v7, v6, v11}, Landroid/support/v4/app/BackStackRecord;->dump(Ljava/lang/String;Ljava/io/PrintWriter;Z)V

    #@22c
    .line 1888
    .end local v5    # "logw":Landroid/support/v4/util/LogWriter;
    .end local v6    # "pw":Ljava/io/PrintWriter;
    :cond_13
    iget-object v7, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@22e
    invoke-virtual {v7, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@231
    .line 1889
    iget v7, v0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@233
    if-ltz v7, :cond_14

    #@235
    .line 1890
    iget v7, v0, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@237
    invoke-virtual {p0, v7, v0}, Landroid/support/v4/app/FragmentManagerImpl;->setBackStackIndex(ILandroid/support/v4/app/BackStackRecord;)V

    #@23a
    .line 1879
    :cond_14
    add-int/lit8 v4, v4, 0x1

    #@23c
    goto :goto_7

    #@23d
    .line 1894
    .end local v0    # "bse":Landroid/support/v4/app/BackStackRecord;
    :cond_15
    iput-object v10, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@23f
    goto/16 :goto_0
.end method

.method retainNonConfig()Ljava/util/ArrayList;
    .locals 6
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v4/app/Fragment;",
            ">;"
        }
    .end annotation

    #@0
    .prologue
    .line 1602
    const/4 v1, 0x0

    #@1
    .line 1603
    .local v1, "fragments":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v4/app/Fragment;>;"
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@3
    if-eqz v3, :cond_3

    #@5
    .line 1604
    const/4 v2, 0x0

    #@6
    .local v2, "i":I
    :goto_0
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@8
    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    #@b
    move-result v3

    #@c
    if-ge v2, v3, :cond_3

    #@e
    .line 1605
    iget-object v3, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@10
    invoke-virtual {v3, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@13
    move-result-object v0

    #@14
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@16
    .line 1606
    .local v0, "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_1

    #@18
    iget-boolean v3, v0, Landroid/support/v4/app/Fragment;->mRetainInstance:Z

    #@1a
    if-eqz v3, :cond_1

    #@1c
    .line 1607
    if-nez v1, :cond_0

    #@1e
    .line 1608
    new-instance v1, Ljava/util/ArrayList;

    #@20
    .end local v1    # "fragments":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v4/app/Fragment;>;"
    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    #@23
    .line 1610
    .restart local v1    # "fragments":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v4/app/Fragment;>;"
    :cond_0
    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@26
    .line 1611
    const/4 v3, 0x1

    #@27
    iput-boolean v3, v0, Landroid/support/v4/app/Fragment;->mRetaining:Z

    #@29
    .line 1612
    iget-object v3, v0, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@2b
    if-eqz v3, :cond_2

    #@2d
    iget-object v3, v0, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@2f
    iget v3, v3, Landroid/support/v4/app/Fragment;->mIndex:I

    #@31
    :goto_1
    iput v3, v0, Landroid/support/v4/app/Fragment;->mTargetIndex:I

    #@33
    .line 1613
    sget-boolean v3, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@35
    if-eqz v3, :cond_1

    #@37
    const-string v3, "FragmentManager"

    #@39
    new-instance v4, Ljava/lang/StringBuilder;

    #@3b
    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    #@3e
    const-string v5, "retainNonConfig: keeping retained "

    #@40
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@43
    move-result-object v4

    #@44
    invoke-virtual {v4, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@47
    move-result-object v4

    #@48
    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@4b
    move-result-object v4

    #@4c
    invoke-static {v3, v4}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@4f
    .line 1604
    :cond_1
    add-int/lit8 v2, v2, 0x1

    #@51
    goto :goto_0

    #@52
    .line 1612
    :cond_2
    const/4 v3, -0x1

    #@53
    goto :goto_1

    #@54
    .line 1617
    .end local v0    # "f":Landroid/support/v4/app/Fragment;
    .end local v2    # "i":I
    :cond_3
    return-object v1
.end method

.method saveAllState()Landroid/os/Parcelable;
    .locals 12

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    .line 1672
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentManagerImpl;->execPendingActions()Z

    #@4
    .line 1674
    sget-boolean v9, Landroid/support/v4/app/FragmentManagerImpl;->HONEYCOMB:Z

    #@6
    if-eqz v9, :cond_0

    #@8
    .line 1684
    const/4 v9, 0x1

    #@9
    iput-boolean v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateSaved:Z

    #@b
    .line 1687
    :cond_0
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@d
    if-eqz v9, :cond_1

    #@f
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@11
    invoke-virtual {v9}, Ljava/util/ArrayList;->size()I

    #@14
    move-result v9

    #@15
    if-gtz v9, :cond_2

    #@17
    .line 1782
    :cond_1
    :goto_0
    return-object v5

    #@18
    .line 1692
    :cond_2
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@1a
    invoke-virtual {v9}, Ljava/util/ArrayList;->size()I

    #@1d
    move-result v0

    #@1e
    .line 1693
    .local v0, "N":I
    new-array v1, v0, [Landroid/support/v4/app/FragmentState;

    #@20
    .line 1694
    .local v1, "active":[Landroid/support/v4/app/FragmentState;
    const/4 v7, 0x0

    #@21
    .line 1695
    .local v7, "haveFragments":Z
    const/4 v8, 0x0

    #@22
    .local v8, "i":I
    :goto_1
    if-ge v8, v0, :cond_9

    #@24
    .line 1696
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@26
    invoke-virtual {v9, v8}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@29
    move-result-object v4

    #@2a
    check-cast v4, Landroid/support/v4/app/Fragment;

    #@2c
    .line 1697
    .local v4, "f":Landroid/support/v4/app/Fragment;
    if-eqz v4, :cond_7

    #@2e
    .line 1698
    iget v9, v4, Landroid/support/v4/app/Fragment;->mIndex:I

    #@30
    if-gez v9, :cond_3

    #@32
    .line 1699
    new-instance v9, Ljava/lang/IllegalStateException;

    #@34
    new-instance v10, Ljava/lang/StringBuilder;

    #@36
    invoke-direct {v10}, Ljava/lang/StringBuilder;-><init>()V

    #@39
    const-string v11, "Failure saving state: active "

    #@3b
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@3e
    move-result-object v10

    #@3f
    invoke-virtual {v10, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@42
    move-result-object v10

    #@43
    const-string v11, " has cleared index: "

    #@45
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@48
    move-result-object v10

    #@49
    iget v11, v4, Landroid/support/v4/app/Fragment;->mIndex:I

    #@4b
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@4e
    move-result-object v10

    #@4f
    invoke-virtual {v10}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@52
    move-result-object v10

    #@53
    invoke-direct {v9, v10}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@56
    invoke-direct {p0, v9}, Landroid/support/v4/app/FragmentManagerImpl;->throwException(Ljava/lang/RuntimeException;)V

    #@59
    .line 1704
    :cond_3
    const/4 v7, 0x1

    #@5a
    .line 1706
    new-instance v6, Landroid/support/v4/app/FragmentState;

    #@5c
    invoke-direct {v6, v4}, Landroid/support/v4/app/FragmentState;-><init>(Landroid/support/v4/app/Fragment;)V

    #@5f
    .line 1707
    .local v6, "fs":Landroid/support/v4/app/FragmentState;
    aput-object v6, v1, v8

    #@61
    .line 1709
    iget v9, v4, Landroid/support/v4/app/Fragment;->mState:I

    #@63
    if-lez v9, :cond_8

    #@65
    iget-object v9, v6, Landroid/support/v4/app/FragmentState;->mSavedFragmentState:Landroid/os/Bundle;

    #@67
    if-nez v9, :cond_8

    #@69
    .line 1710
    invoke-virtual {p0, v4}, Landroid/support/v4/app/FragmentManagerImpl;->saveFragmentBasicState(Landroid/support/v4/app/Fragment;)Landroid/os/Bundle;

    #@6c
    move-result-object v9

    #@6d
    iput-object v9, v6, Landroid/support/v4/app/FragmentState;->mSavedFragmentState:Landroid/os/Bundle;

    #@6f
    .line 1712
    iget-object v9, v4, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@71
    if-eqz v9, :cond_6

    #@73
    .line 1713
    iget-object v9, v4, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@75
    iget v9, v9, Landroid/support/v4/app/Fragment;->mIndex:I

    #@77
    if-gez v9, :cond_4

    #@79
    .line 1714
    new-instance v9, Ljava/lang/IllegalStateException;

    #@7b
    new-instance v10, Ljava/lang/StringBuilder;

    #@7d
    invoke-direct {v10}, Ljava/lang/StringBuilder;-><init>()V

    #@80
    const-string v11, "Failure saving state: "

    #@82
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@85
    move-result-object v10

    #@86
    invoke-virtual {v10, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@89
    move-result-object v10

    #@8a
    const-string v11, " has target not in fragment manager: "

    #@8c
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@8f
    move-result-object v10

    #@90
    iget-object v11, v4, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@92
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@95
    move-result-object v10

    #@96
    invoke-virtual {v10}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@99
    move-result-object v10

    #@9a
    invoke-direct {v9, v10}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@9d
    invoke-direct {p0, v9}, Landroid/support/v4/app/FragmentManagerImpl;->throwException(Ljava/lang/RuntimeException;)V

    #@a0
    .line 1718
    :cond_4
    iget-object v9, v6, Landroid/support/v4/app/FragmentState;->mSavedFragmentState:Landroid/os/Bundle;

    #@a2
    if-nez v9, :cond_5

    #@a4
    .line 1719
    new-instance v9, Landroid/os/Bundle;

    #@a6
    invoke-direct {v9}, Landroid/os/Bundle;-><init>()V

    #@a9
    iput-object v9, v6, Landroid/support/v4/app/FragmentState;->mSavedFragmentState:Landroid/os/Bundle;

    #@ab
    .line 1721
    :cond_5
    iget-object v9, v6, Landroid/support/v4/app/FragmentState;->mSavedFragmentState:Landroid/os/Bundle;

    #@ad
    const-string v10, "android:target_state"

    #@af
    iget-object v11, v4, Landroid/support/v4/app/Fragment;->mTarget:Landroid/support/v4/app/Fragment;

    #@b1
    invoke-virtual {p0, v9, v10, v11}, Landroid/support/v4/app/FragmentManagerImpl;->putFragment(Landroid/os/Bundle;Ljava/lang/String;Landroid/support/v4/app/Fragment;)V

    #@b4
    .line 1723
    iget v9, v4, Landroid/support/v4/app/Fragment;->mTargetRequestCode:I

    #@b6
    if-eqz v9, :cond_6

    #@b8
    .line 1724
    iget-object v9, v6, Landroid/support/v4/app/FragmentState;->mSavedFragmentState:Landroid/os/Bundle;

    #@ba
    const-string v10, "android:target_req_state"

    #@bc
    iget v11, v4, Landroid/support/v4/app/Fragment;->mTargetRequestCode:I

    #@be
    invoke-virtual {v9, v10, v11}, Landroid/os/Bundle;->putInt(Ljava/lang/String;I)V

    #@c1
    .line 1734
    :cond_6
    :goto_2
    sget-boolean v9, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@c3
    if-eqz v9, :cond_7

    #@c5
    const-string v9, "FragmentManager"

    #@c7
    new-instance v10, Ljava/lang/StringBuilder;

    #@c9
    invoke-direct {v10}, Ljava/lang/StringBuilder;-><init>()V

    #@cc
    const-string v11, "Saved state of "

    #@ce
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@d1
    move-result-object v10

    #@d2
    invoke-virtual {v10, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@d5
    move-result-object v10

    #@d6
    const-string v11, ": "

    #@d8
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@db
    move-result-object v10

    #@dc
    iget-object v11, v6, Landroid/support/v4/app/FragmentState;->mSavedFragmentState:Landroid/os/Bundle;

    #@de
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@e1
    move-result-object v10

    #@e2
    invoke-virtual {v10}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@e5
    move-result-object v10

    #@e6
    invoke-static {v9, v10}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@e9
    .line 1695
    .end local v6    # "fs":Landroid/support/v4/app/FragmentState;
    :cond_7
    add-int/lit8 v8, v8, 0x1

    #@eb
    goto/16 :goto_1

    #@ed
    .line 1731
    .restart local v6    # "fs":Landroid/support/v4/app/FragmentState;
    :cond_8
    iget-object v9, v4, Landroid/support/v4/app/Fragment;->mSavedFragmentState:Landroid/os/Bundle;

    #@ef
    iput-object v9, v6, Landroid/support/v4/app/FragmentState;->mSavedFragmentState:Landroid/os/Bundle;

    #@f1
    goto :goto_2

    #@f2
    .line 1739
    .end local v4    # "f":Landroid/support/v4/app/Fragment;
    .end local v6    # "fs":Landroid/support/v4/app/FragmentState;
    :cond_9
    if-nez v7, :cond_a

    #@f4
    .line 1740
    sget-boolean v9, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@f6
    if-eqz v9, :cond_1

    #@f8
    const-string v9, "FragmentManager"

    #@fa
    const-string v10, "saveAllState: no fragments!"

    #@fc
    invoke-static {v9, v10}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@ff
    goto/16 :goto_0

    #@101
    .line 1744
    :cond_a
    const/4 v2, 0x0

    #@102
    .line 1745
    .local v2, "added":[I
    const/4 v3, 0x0

    #@103
    .line 1748
    .local v3, "backStack":[Landroid/support/v4/app/BackStackState;
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@105
    if-eqz v9, :cond_d

    #@107
    .line 1749
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@109
    invoke-virtual {v9}, Ljava/util/ArrayList;->size()I

    #@10c
    move-result v0

    #@10d
    .line 1750
    if-lez v0, :cond_d

    #@10f
    .line 1751
    new-array v2, v0, [I

    #@111
    .line 1752
    const/4 v8, 0x0

    #@112
    :goto_3
    if-ge v8, v0, :cond_d

    #@114
    .line 1753
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@116
    invoke-virtual {v9, v8}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@119
    move-result-object v9

    #@11a
    check-cast v9, Landroid/support/v4/app/Fragment;

    #@11c
    iget v9, v9, Landroid/support/v4/app/Fragment;->mIndex:I

    #@11e
    aput v9, v2, v8

    #@120
    .line 1754
    aget v9, v2, v8

    #@122
    if-gez v9, :cond_b

    #@124
    .line 1755
    new-instance v9, Ljava/lang/IllegalStateException;

    #@126
    new-instance v10, Ljava/lang/StringBuilder;

    #@128
    invoke-direct {v10}, Ljava/lang/StringBuilder;-><init>()V

    #@12b
    const-string v11, "Failure saving state: active "

    #@12d
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@130
    move-result-object v10

    #@131
    iget-object v11, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@133
    invoke-virtual {v11, v8}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@136
    move-result-object v11

    #@137
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@13a
    move-result-object v10

    #@13b
    const-string v11, " has cleared index: "

    #@13d
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@140
    move-result-object v10

    #@141
    aget v11, v2, v8

    #@143
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@146
    move-result-object v10

    #@147
    invoke-virtual {v10}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@14a
    move-result-object v10

    #@14b
    invoke-direct {v9, v10}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@14e
    invoke-direct {p0, v9}, Landroid/support/v4/app/FragmentManagerImpl;->throwException(Ljava/lang/RuntimeException;)V

    #@151
    .line 1759
    :cond_b
    sget-boolean v9, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@153
    if-eqz v9, :cond_c

    #@155
    const-string v9, "FragmentManager"

    #@157
    new-instance v10, Ljava/lang/StringBuilder;

    #@159
    invoke-direct {v10}, Ljava/lang/StringBuilder;-><init>()V

    #@15c
    const-string v11, "saveAllState: adding fragment #"

    #@15e
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@161
    move-result-object v10

    #@162
    invoke-virtual {v10, v8}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@165
    move-result-object v10

    #@166
    const-string v11, ": "

    #@168
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@16b
    move-result-object v10

    #@16c
    iget-object v11, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAdded:Ljava/util/ArrayList;

    #@16e
    invoke-virtual {v11, v8}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@171
    move-result-object v11

    #@172
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@175
    move-result-object v10

    #@176
    invoke-virtual {v10}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@179
    move-result-object v10

    #@17a
    invoke-static {v9, v10}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@17d
    .line 1752
    :cond_c
    add-int/lit8 v8, v8, 0x1

    #@17f
    goto :goto_3

    #@180
    .line 1766
    :cond_d
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@182
    if-eqz v9, :cond_f

    #@184
    .line 1767
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@186
    invoke-virtual {v9}, Ljava/util/ArrayList;->size()I

    #@189
    move-result v0

    #@18a
    .line 1768
    if-lez v0, :cond_f

    #@18c
    .line 1769
    new-array v3, v0, [Landroid/support/v4/app/BackStackState;

    #@18e
    .line 1770
    const/4 v8, 0x0

    #@18f
    :goto_4
    if-ge v8, v0, :cond_f

    #@191
    .line 1771
    new-instance v10, Landroid/support/v4/app/BackStackState;

    #@193
    iget-object v9, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@195
    invoke-virtual {v9, v8}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@198
    move-result-object v9

    #@199
    check-cast v9, Landroid/support/v4/app/BackStackRecord;

    #@19b
    invoke-direct {v10, p0, v9}, Landroid/support/v4/app/BackStackState;-><init>(Landroid/support/v4/app/FragmentManagerImpl;Landroid/support/v4/app/BackStackRecord;)V

    #@19e
    aput-object v10, v3, v8

    #@1a0
    .line 1772
    sget-boolean v9, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@1a2
    if-eqz v9, :cond_e

    #@1a4
    const-string v9, "FragmentManager"

    #@1a6
    new-instance v10, Ljava/lang/StringBuilder;

    #@1a8
    invoke-direct {v10}, Ljava/lang/StringBuilder;-><init>()V

    #@1ab
    const-string v11, "saveAllState: adding back stack #"

    #@1ad
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1b0
    move-result-object v10

    #@1b1
    invoke-virtual {v10, v8}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@1b4
    move-result-object v10

    #@1b5
    const-string v11, ": "

    #@1b7
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1ba
    move-result-object v10

    #@1bb
    iget-object v11, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStack:Ljava/util/ArrayList;

    #@1bd
    invoke-virtual {v11, v8}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@1c0
    move-result-object v11

    #@1c1
    invoke-virtual {v10, v11}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@1c4
    move-result-object v10

    #@1c5
    invoke-virtual {v10}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1c8
    move-result-object v10

    #@1c9
    invoke-static {v9, v10}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@1cc
    .line 1770
    :cond_e
    add-int/lit8 v8, v8, 0x1

    #@1ce
    goto :goto_4

    #@1cf
    .line 1778
    :cond_f
    new-instance v5, Landroid/support/v4/app/FragmentManagerState;

    #@1d1
    invoke-direct {v5}, Landroid/support/v4/app/FragmentManagerState;-><init>()V

    #@1d4
    .line 1779
    .local v5, "fms":Landroid/support/v4/app/FragmentManagerState;
    iput-object v1, v5, Landroid/support/v4/app/FragmentManagerState;->mActive:[Landroid/support/v4/app/FragmentState;

    #@1d6
    .line 1780
    iput-object v2, v5, Landroid/support/v4/app/FragmentManagerState;->mAdded:[I

    #@1d8
    .line 1781
    iput-object v3, v5, Landroid/support/v4/app/FragmentManagerState;->mBackStack:[Landroid/support/v4/app/BackStackState;

    #@1da
    goto/16 :goto_0
.end method

.method saveFragmentBasicState(Landroid/support/v4/app/Fragment;)Landroid/os/Bundle;
    .locals 3
    .param p1, "f"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 1637
    const/4 v0, 0x0

    #@1
    .line 1639
    .local v0, "result":Landroid/os/Bundle;
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateBundle:Landroid/os/Bundle;

    #@3
    if-nez v1, :cond_0

    #@5
    .line 1640
    new-instance v1, Landroid/os/Bundle;

    #@7
    invoke-direct {v1}, Landroid/os/Bundle;-><init>()V

    #@a
    iput-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateBundle:Landroid/os/Bundle;

    #@c
    .line 1642
    :cond_0
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateBundle:Landroid/os/Bundle;

    #@e
    invoke-virtual {p1, v1}, Landroid/support/v4/app/Fragment;->performSaveInstanceState(Landroid/os/Bundle;)V

    #@11
    .line 1643
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateBundle:Landroid/os/Bundle;

    #@13
    invoke-virtual {v1}, Landroid/os/Bundle;->isEmpty()Z

    #@16
    move-result v1

    #@17
    if-nez v1, :cond_1

    #@19
    .line 1644
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateBundle:Landroid/os/Bundle;

    #@1b
    .line 1645
    const/4 v1, 0x0

    #@1c
    iput-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateBundle:Landroid/os/Bundle;

    #@1e
    .line 1648
    :cond_1
    iget-object v1, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@20
    if-eqz v1, :cond_2

    #@22
    .line 1649
    invoke-virtual {p0, p1}, Landroid/support/v4/app/FragmentManagerImpl;->saveFragmentViewState(Landroid/support/v4/app/Fragment;)V

    #@25
    .line 1651
    :cond_2
    iget-object v1, p1, Landroid/support/v4/app/Fragment;->mSavedViewState:Landroid/util/SparseArray;

    #@27
    if-eqz v1, :cond_4

    #@29
    .line 1652
    if-nez v0, :cond_3

    #@2b
    .line 1653
    new-instance v0, Landroid/os/Bundle;

    #@2d
    .end local v0    # "result":Landroid/os/Bundle;
    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    #@30
    .line 1655
    .restart local v0    # "result":Landroid/os/Bundle;
    :cond_3
    const-string v1, "android:view_state"

    #@32
    iget-object v2, p1, Landroid/support/v4/app/Fragment;->mSavedViewState:Landroid/util/SparseArray;

    #@34
    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putSparseParcelableArray(Ljava/lang/String;Landroid/util/SparseArray;)V

    #@37
    .line 1658
    :cond_4
    iget-boolean v1, p1, Landroid/support/v4/app/Fragment;->mUserVisibleHint:Z

    #@39
    if-nez v1, :cond_6

    #@3b
    .line 1659
    if-nez v0, :cond_5

    #@3d
    .line 1660
    new-instance v0, Landroid/os/Bundle;

    #@3f
    .end local v0    # "result":Landroid/os/Bundle;
    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    #@42
    .line 1663
    .restart local v0    # "result":Landroid/os/Bundle;
    :cond_5
    const-string v1, "android:user_visible_hint"

    #@44
    iget-boolean v2, p1, Landroid/support/v4/app/Fragment;->mUserVisibleHint:Z

    #@46
    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    #@49
    .line 1666
    :cond_6
    return-object v0
.end method

.method public saveFragmentInstanceState(Landroid/support/v4/app/Fragment;)Landroid/support/v4/app/Fragment$SavedState;
    .locals 5
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 602
    iget v2, p1, Landroid/support/v4/app/Fragment;->mIndex:I

    #@3
    if-gez v2, :cond_0

    #@5
    .line 603
    new-instance v2, Ljava/lang/IllegalStateException;

    #@7
    new-instance v3, Ljava/lang/StringBuilder;

    #@9
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@c
    const-string v4, "Fragment "

    #@e
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@11
    move-result-object v3

    #@12
    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@15
    move-result-object v3

    #@16
    const-string v4, " is not currently in the FragmentManager"

    #@18
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1b
    move-result-object v3

    #@1c
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1f
    move-result-object v3

    #@20
    invoke-direct {v2, v3}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@23
    invoke-direct {p0, v2}, Landroid/support/v4/app/FragmentManagerImpl;->throwException(Ljava/lang/RuntimeException;)V

    #@26
    .line 606
    :cond_0
    iget v2, p1, Landroid/support/v4/app/Fragment;->mState:I

    #@28
    if-lez v2, :cond_1

    #@2a
    .line 607
    invoke-virtual {p0, p1}, Landroid/support/v4/app/FragmentManagerImpl;->saveFragmentBasicState(Landroid/support/v4/app/Fragment;)Landroid/os/Bundle;

    #@2d
    move-result-object v0

    #@2e
    .line 608
    .local v0, "result":Landroid/os/Bundle;
    if-eqz v0, :cond_1

    #@30
    new-instance v1, Landroid/support/v4/app/Fragment$SavedState;

    #@32
    invoke-direct {v1, v0}, Landroid/support/v4/app/Fragment$SavedState;-><init>(Landroid/os/Bundle;)V

    #@35
    .line 610
    .end local v0    # "result":Landroid/os/Bundle;
    :cond_1
    return-object v1
.end method

.method saveFragmentViewState(Landroid/support/v4/app/Fragment;)V
    .locals 2
    .param p1, "f"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 1621
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mInnerView:Landroid/view/View;

    #@2
    if-nez v0, :cond_1

    #@4
    .line 1634
    :cond_0
    :goto_0
    return-void

    #@5
    .line 1624
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateArray:Landroid/util/SparseArray;

    #@7
    if-nez v0, :cond_2

    #@9
    .line 1625
    new-instance v0, Landroid/util/SparseArray;

    #@b
    invoke-direct {v0}, Landroid/util/SparseArray;-><init>()V

    #@e
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateArray:Landroid/util/SparseArray;

    #@10
    .line 1629
    :goto_1
    iget-object v0, p1, Landroid/support/v4/app/Fragment;->mInnerView:Landroid/view/View;

    #@12
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateArray:Landroid/util/SparseArray;

    #@14
    invoke-virtual {v0, v1}, Landroid/view/View;->saveHierarchyState(Landroid/util/SparseArray;)V

    #@17
    .line 1630
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateArray:Landroid/util/SparseArray;

    #@19
    invoke-virtual {v0}, Landroid/util/SparseArray;->size()I

    #@1c
    move-result v0

    #@1d
    if-lez v0, :cond_0

    #@1f
    .line 1631
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateArray:Landroid/util/SparseArray;

    #@21
    iput-object v0, p1, Landroid/support/v4/app/Fragment;->mSavedViewState:Landroid/util/SparseArray;

    #@23
    .line 1632
    const/4 v0, 0x0

    #@24
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateArray:Landroid/util/SparseArray;

    #@26
    goto :goto_0

    #@27
    .line 1627
    :cond_2
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerImpl;->mStateArray:Landroid/util/SparseArray;

    #@29
    invoke-virtual {v0}, Landroid/util/SparseArray;->clear()V

    #@2c
    goto :goto_1
.end method

.method public setBackStackIndex(ILandroid/support/v4/app/BackStackRecord;)V
    .locals 4
    .param p1, "index"    # I
    .param p2, "bse"    # Landroid/support/v4/app/BackStackRecord;

    #@0
    .prologue
    .line 1421
    monitor-enter p0

    #@1
    .line 1422
    :try_start_0
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@3
    if-nez v1, :cond_0

    #@5
    .line 1423
    new-instance v1, Ljava/util/ArrayList;

    #@7
    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    #@a
    iput-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@c
    .line 1425
    :cond_0
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@e
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    #@11
    move-result v0

    #@12
    .line 1426
    .local v0, "N":I
    if-ge p1, v0, :cond_2

    #@14
    .line 1427
    sget-boolean v1, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@16
    if-eqz v1, :cond_1

    #@18
    const-string v1, "FragmentManager"

    #@1a
    new-instance v2, Ljava/lang/StringBuilder;

    #@1c
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@1f
    const-string v3, "Setting back stack index "

    #@21
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@24
    move-result-object v2

    #@25
    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@28
    move-result-object v2

    #@29
    const-string v3, " to "

    #@2b
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2e
    move-result-object v2

    #@2f
    invoke-virtual {v2, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@32
    move-result-object v2

    #@33
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@36
    move-result-object v2

    #@37
    invoke-static {v1, v2}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@3a
    .line 1428
    :cond_1
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@3c
    invoke-virtual {v1, p1, p2}, Ljava/util/ArrayList;->set(ILjava/lang/Object;)Ljava/lang/Object;

    #@3f
    .line 1442
    :goto_0
    monitor-exit p0

    #@40
    .line 1443
    return-void

    #@41
    .line 1430
    :cond_2
    :goto_1
    if-ge v0, p1, :cond_5

    #@43
    .line 1431
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@45
    const/4 v2, 0x0

    #@46
    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@49
    .line 1432
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailBackStackIndices:Ljava/util/ArrayList;

    #@4b
    if-nez v1, :cond_3

    #@4d
    .line 1433
    new-instance v1, Ljava/util/ArrayList;

    #@4f
    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    #@52
    iput-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailBackStackIndices:Ljava/util/ArrayList;

    #@54
    .line 1435
    :cond_3
    sget-boolean v1, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@56
    if-eqz v1, :cond_4

    #@58
    const-string v1, "FragmentManager"

    #@5a
    new-instance v2, Ljava/lang/StringBuilder;

    #@5c
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@5f
    const-string v3, "Adding available back stack index "

    #@61
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@64
    move-result-object v2

    #@65
    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@68
    move-result-object v2

    #@69
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@6c
    move-result-object v2

    #@6d
    invoke-static {v1, v2}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@70
    .line 1436
    :cond_4
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mAvailBackStackIndices:Ljava/util/ArrayList;

    #@72
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@75
    move-result-object v2

    #@76
    invoke-virtual {v1, v2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@79
    .line 1437
    add-int/lit8 v0, v0, 0x1

    #@7b
    goto :goto_1

    #@7c
    .line 1439
    :cond_5
    sget-boolean v1, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@7e
    if-eqz v1, :cond_6

    #@80
    const-string v1, "FragmentManager"

    #@82
    new-instance v2, Ljava/lang/StringBuilder;

    #@84
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@87
    const-string v3, "Adding back stack index "

    #@89
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@8c
    move-result-object v2

    #@8d
    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@90
    move-result-object v2

    #@91
    const-string v3, " with "

    #@93
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@96
    move-result-object v2

    #@97
    invoke-virtual {v2, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@9a
    move-result-object v2

    #@9b
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@9e
    move-result-object v2

    #@9f
    invoke-static {v1, v2}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@a2
    .line 1440
    :cond_6
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mBackStackIndices:Ljava/util/ArrayList;

    #@a4
    invoke-virtual {v1, p2}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@a7
    goto :goto_0

    #@a8
    .line 1442
    .end local v0    # "N":I
    :catchall_0
    move-exception v1

    #@a9
    monitor-exit p0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@aa
    throw v1
.end method

.method public showFragment(Landroid/support/v4/app/Fragment;II)V
    .locals 6
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;
    .param p2, "transition"    # I
    .param p3, "transitionStyle"    # I

    #@0
    .prologue
    const/4 v5, 0x1

    #@1
    const/4 v4, 0x0

    #@2
    .line 1248
    sget-boolean v1, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@4
    if-eqz v1, :cond_0

    #@6
    const-string v1, "FragmentManager"

    #@8
    new-instance v2, Ljava/lang/StringBuilder;

    #@a
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@d
    const-string v3, "show: "

    #@f
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v2

    #@13
    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@16
    move-result-object v2

    #@17
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1a
    move-result-object v2

    #@1b
    invoke-static {v1, v2}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@1e
    .line 1249
    :cond_0
    iget-boolean v1, p1, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@20
    if-eqz v1, :cond_4

    #@22
    .line 1250
    iput-boolean v4, p1, Landroid/support/v4/app/Fragment;->mHidden:Z

    #@24
    .line 1251
    iget-object v1, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@26
    if-eqz v1, :cond_2

    #@28
    .line 1252
    invoke-virtual {p0, p1, p2, v5, p3}, Landroid/support/v4/app/FragmentManagerImpl;->loadAnimation(Landroid/support/v4/app/Fragment;IZI)Landroid/view/animation/Animation;

    #@2b
    move-result-object v0

    #@2c
    .line 1254
    .local v0, "anim":Landroid/view/animation/Animation;
    if-eqz v0, :cond_1

    #@2e
    .line 1255
    iget-object v1, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@30
    invoke-virtual {v1, v0}, Landroid/view/View;->startAnimation(Landroid/view/animation/Animation;)V

    #@33
    .line 1257
    :cond_1
    iget-object v1, p1, Landroid/support/v4/app/Fragment;->mView:Landroid/view/View;

    #@35
    invoke-virtual {v1, v4}, Landroid/view/View;->setVisibility(I)V

    #@38
    .line 1259
    .end local v0    # "anim":Landroid/view/animation/Animation;
    :cond_2
    iget-boolean v1, p1, Landroid/support/v4/app/Fragment;->mAdded:Z

    #@3a
    if-eqz v1, :cond_3

    #@3c
    iget-boolean v1, p1, Landroid/support/v4/app/Fragment;->mHasMenu:Z

    #@3e
    if-eqz v1, :cond_3

    #@40
    iget-boolean v1, p1, Landroid/support/v4/app/Fragment;->mMenuVisible:Z

    #@42
    if-eqz v1, :cond_3

    #@44
    .line 1260
    iput-boolean v5, p0, Landroid/support/v4/app/FragmentManagerImpl;->mNeedMenuInvalidate:Z

    #@46
    .line 1262
    :cond_3
    invoke-virtual {p1, v4}, Landroid/support/v4/app/Fragment;->onHiddenChanged(Z)V

    #@49
    .line 1264
    :cond_4
    return-void
.end method

.method startPendingDeferredFragments()V
    .locals 3

    #@0
    .prologue
    .line 1145
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@2
    if-nez v2, :cond_1

    #@4
    .line 1153
    :cond_0
    return-void

    #@5
    .line 1147
    :cond_1
    const/4 v1, 0x0

    #@6
    .local v1, "i":I
    :goto_0
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@8
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@b
    move-result v2

    #@c
    if-ge v1, v2, :cond_0

    #@e
    .line 1148
    iget-object v2, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@10
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@13
    move-result-object v0

    #@14
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@16
    .line 1149
    .local v0, "f":Landroid/support/v4/app/Fragment;
    if-eqz v0, :cond_2

    #@18
    .line 1150
    invoke-virtual {p0, v0}, Landroid/support/v4/app/FragmentManagerImpl;->performPendingDeferredStart(Landroid/support/v4/app/Fragment;)V

    #@1b
    .line 1147
    :cond_2
    add-int/lit8 v1, v1, 0x1

    #@1d
    goto :goto_0
.end method

.method public toString()Ljava/lang/String;
    .locals 2

    #@0
    .prologue
    .line 620
    new-instance v0, Ljava/lang/StringBuilder;

    #@2
    const/16 v1, 0x80

    #@4
    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(I)V

    #@7
    .line 621
    .local v0, "sb":Ljava/lang/StringBuilder;
    const-string v1, "FragmentManager{"

    #@9
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@c
    .line 622
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
    .line 623
    const-string v1, " in "

    #@19
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    .line 624
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mParent:Landroid/support/v4/app/Fragment;

    #@1e
    if-eqz v1, :cond_0

    #@20
    .line 625
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mParent:Landroid/support/v4/app/Fragment;

    #@22
    invoke-static {v1, v0}, Landroid/support/v4/util/DebugUtils;->buildShortClassTag(Ljava/lang/Object;Ljava/lang/StringBuilder;)V

    #@25
    .line 629
    :goto_0
    const-string v1, "}}"

    #@27
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2a
    .line 630
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2d
    move-result-object v1

    #@2e
    return-object v1

    #@2f
    .line 627
    :cond_0
    iget-object v1, p0, Landroid/support/v4/app/FragmentManagerImpl;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@31
    invoke-static {v1, v0}, Landroid/support/v4/util/DebugUtils;->buildShortClassTag(Ljava/lang/Object;Ljava/lang/StringBuilder;)V

    #@34
    goto :goto_0
.end method
