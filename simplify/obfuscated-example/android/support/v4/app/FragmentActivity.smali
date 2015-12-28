.class public Landroid/support/v4/app/FragmentActivity;
.super Landroid/app/Activity;
.source "FragmentActivity.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;
    }
.end annotation


# static fields
.field static final FRAGMENTS_TAG:Ljava/lang/String; = "android:support:fragments"

.field private static final HONEYCOMB:I = 0xb

.field static final MSG_REALLY_STOPPED:I = 0x1

.field static final MSG_RESUME_PENDING:I = 0x2

.field private static final TAG:Ljava/lang/String; = "FragmentActivity"


# instance fields
.field mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/support/v4/util/SimpleArrayMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/support/v4/app/LoaderManagerImpl;",
            ">;"
        }
    .end annotation
.end field

.field mCheckedForLoaderManager:Z

.field final mContainer:Landroid/support/v4/app/FragmentContainer;

.field mCreated:Z

.field final mFragments:Landroid/support/v4/app/FragmentManagerImpl;

.field final mHandler:Landroid/os/Handler;

.field mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

.field mLoadersStarted:Z

.field mOptionsMenuInvalidated:Z

.field mReallyStopped:Z

.field mResumed:Z

.field mRetaining:Z

.field mStopped:Z


# direct methods
.method public constructor <init>()V
    .locals 1

    #@0
    .prologue
    .line 75
    invoke-direct {p0}, Landroid/app/Activity;-><init>()V

    #@3
    .line 86
    new-instance v0, Landroid/support/v4/app/FragmentActivity$1;

    #@5
    invoke-direct {v0, p0}, Landroid/support/v4/app/FragmentActivity$1;-><init>(Landroid/support/v4/app/FragmentActivity;)V

    #@8
    iput-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@a
    .line 105
    new-instance v0, Landroid/support/v4/app/FragmentManagerImpl;

    #@c
    invoke-direct {v0}, Landroid/support/v4/app/FragmentManagerImpl;-><init>()V

    #@f
    iput-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@11
    .line 106
    new-instance v0, Landroid/support/v4/app/FragmentActivity$2;

    #@13
    invoke-direct {v0, p0}, Landroid/support/v4/app/FragmentActivity$2;-><init>(Landroid/support/v4/app/FragmentActivity;)V

    #@16
    iput-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mContainer:Landroid/support/v4/app/FragmentContainer;

    #@18
    .line 132
    return-void
.end method

.method private dumpViewHierarchy(Ljava/lang/String;Ljava/io/PrintWriter;Landroid/view/View;)V
    .locals 5
    .param p1, "prefix"    # Ljava/lang/String;
    .param p2, "writer"    # Ljava/io/PrintWriter;
    .param p3, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 739
    invoke-virtual {p2, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@3
    .line 740
    if-nez p3, :cond_1

    #@5
    .line 741
    const-string v3, "null"

    #@7
    invoke-virtual {p2, v3}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@a
    .line 757
    :cond_0
    return-void

    #@b
    .line 744
    :cond_1
    invoke-static {p3}, Landroid/support/v4/app/FragmentActivity;->viewToString(Landroid/view/View;)Ljava/lang/String;

    #@e
    move-result-object v3

    #@f
    invoke-virtual {p2, v3}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@12
    .line 745
    instance-of v3, p3, Landroid/view/ViewGroup;

    #@14
    if-eqz v3, :cond_0

    #@16
    move-object v1, p3

    #@17
    .line 748
    check-cast v1, Landroid/view/ViewGroup;

    #@19
    .line 749
    .local v1, "grp":Landroid/view/ViewGroup;
    invoke-virtual {v1}, Landroid/view/ViewGroup;->getChildCount()I

    #@1c
    move-result v0

    #@1d
    .line 750
    .local v0, "N":I
    if-lez v0, :cond_0

    #@1f
    .line 753
    new-instance v3, Ljava/lang/StringBuilder;

    #@21
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@24
    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@27
    move-result-object v3

    #@28
    const-string v4, "  "

    #@2a
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2d
    move-result-object v3

    #@2e
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@31
    move-result-object p1

    #@32
    .line 754
    const/4 v2, 0x0

    #@33
    .local v2, "i":I
    :goto_0
    if-ge v2, v0, :cond_0

    #@35
    .line 755
    invoke-virtual {v1, v2}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    #@38
    move-result-object v3

    #@39
    invoke-direct {p0, p1, p2, v3}, Landroid/support/v4/app/FragmentActivity;->dumpViewHierarchy(Ljava/lang/String;Ljava/io/PrintWriter;Landroid/view/View;)V

    #@3c
    .line 754
    add-int/lit8 v2, v2, 0x1

    #@3e
    goto :goto_0
.end method

.method private static viewToString(Landroid/view/View;)Ljava/lang/String;
    .locals 12
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    const/16 v9, 0x56

    #@2
    const/16 v7, 0x46

    #@4
    const/16 v11, 0x2c

    #@6
    const/16 v10, 0x20

    #@8
    const/16 v8, 0x2e

    #@a
    .line 673
    new-instance v2, Ljava/lang/StringBuilder;

    #@c
    const/16 v6, 0x80

    #@e
    invoke-direct {v2, v6}, Ljava/lang/StringBuilder;-><init>(I)V

    #@11
    .line 674
    .local v2, "out":Ljava/lang/StringBuilder;
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@14
    move-result-object v6

    #@15
    invoke-virtual {v6}, Ljava/lang/Class;->getName()Ljava/lang/String;

    #@18
    move-result-object v6

    #@19
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    .line 675
    const/16 v6, 0x7b

    #@1e
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@21
    .line 676
    invoke-static {p0}, Ljava/lang/System;->identityHashCode(Ljava/lang/Object;)I

    #@24
    move-result v6

    #@25
    invoke-static {v6}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@28
    move-result-object v6

    #@29
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2c
    .line 677
    invoke-virtual {v2, v10}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@2f
    .line 678
    invoke-virtual {p0}, Landroid/view/View;->getVisibility()I

    #@32
    move-result v6

    #@33
    sparse-switch v6, :sswitch_data_0

    #@36
    .line 682
    invoke-virtual {v2, v8}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@39
    .line 684
    :goto_0
    invoke-virtual {p0}, Landroid/view/View;->isFocusable()Z

    #@3c
    move-result v6

    #@3d
    if-eqz v6, :cond_2

    #@3f
    move v6, v7

    #@40
    :goto_1
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@43
    .line 685
    invoke-virtual {p0}, Landroid/view/View;->isEnabled()Z

    #@46
    move-result v6

    #@47
    if-eqz v6, :cond_3

    #@49
    const/16 v6, 0x45

    #@4b
    :goto_2
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@4e
    .line 686
    invoke-virtual {p0}, Landroid/view/View;->willNotDraw()Z

    #@51
    move-result v6

    #@52
    if-eqz v6, :cond_4

    #@54
    move v6, v8

    #@55
    :goto_3
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@58
    .line 687
    invoke-virtual {p0}, Landroid/view/View;->isHorizontalScrollBarEnabled()Z

    #@5b
    move-result v6

    #@5c
    if-eqz v6, :cond_5

    #@5e
    const/16 v6, 0x48

    #@60
    :goto_4
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@63
    .line 688
    invoke-virtual {p0}, Landroid/view/View;->isVerticalScrollBarEnabled()Z

    #@66
    move-result v6

    #@67
    if-eqz v6, :cond_6

    #@69
    move v6, v9

    #@6a
    :goto_5
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@6d
    .line 689
    invoke-virtual {p0}, Landroid/view/View;->isClickable()Z

    #@70
    move-result v6

    #@71
    if-eqz v6, :cond_7

    #@73
    const/16 v6, 0x43

    #@75
    :goto_6
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@78
    .line 690
    invoke-virtual {p0}, Landroid/view/View;->isLongClickable()Z

    #@7b
    move-result v6

    #@7c
    if-eqz v6, :cond_8

    #@7e
    const/16 v6, 0x4c

    #@80
    :goto_7
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@83
    .line 691
    invoke-virtual {v2, v10}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@86
    .line 692
    invoke-virtual {p0}, Landroid/view/View;->isFocused()Z

    #@89
    move-result v6

    #@8a
    if-eqz v6, :cond_9

    #@8c
    :goto_8
    invoke-virtual {v2, v7}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@8f
    .line 693
    invoke-virtual {p0}, Landroid/view/View;->isSelected()Z

    #@92
    move-result v6

    #@93
    if-eqz v6, :cond_a

    #@95
    const/16 v6, 0x53

    #@97
    :goto_9
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@9a
    .line 694
    invoke-virtual {p0}, Landroid/view/View;->isPressed()Z

    #@9d
    move-result v6

    #@9e
    if-eqz v6, :cond_0

    #@a0
    const/16 v8, 0x50

    #@a2
    :cond_0
    invoke-virtual {v2, v8}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@a5
    .line 695
    invoke-virtual {v2, v10}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@a8
    .line 696
    invoke-virtual {p0}, Landroid/view/View;->getLeft()I

    #@ab
    move-result v6

    #@ac
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@af
    .line 697
    invoke-virtual {v2, v11}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@b2
    .line 698
    invoke-virtual {p0}, Landroid/view/View;->getTop()I

    #@b5
    move-result v6

    #@b6
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@b9
    .line 699
    const/16 v6, 0x2d

    #@bb
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@be
    .line 700
    invoke-virtual {p0}, Landroid/view/View;->getRight()I

    #@c1
    move-result v6

    #@c2
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@c5
    .line 701
    invoke-virtual {v2, v11}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@c8
    .line 702
    invoke-virtual {p0}, Landroid/view/View;->getBottom()I

    #@cb
    move-result v6

    #@cc
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@cf
    .line 703
    invoke-virtual {p0}, Landroid/view/View;->getId()I

    #@d2
    move-result v1

    #@d3
    .line 704
    .local v1, "id":I
    const/4 v6, -0x1

    #@d4
    if-eq v1, v6, :cond_1

    #@d6
    .line 705
    const-string v6, " #"

    #@d8
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@db
    .line 706
    invoke-static {v1}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@de
    move-result-object v6

    #@df
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@e2
    .line 707
    invoke-virtual {p0}, Landroid/view/View;->getResources()Landroid/content/res/Resources;

    #@e5
    move-result-object v4

    #@e6
    .line 708
    .local v4, "r":Landroid/content/res/Resources;
    if-eqz v1, :cond_1

    #@e8
    if-eqz v4, :cond_1

    #@ea
    .line 711
    const/high16 v6, -0x1000000

    #@ec
    and-int/2addr v6, v1

    #@ed
    sparse-switch v6, :sswitch_data_1

    #@f0
    .line 719
    :try_start_0
    invoke-virtual {v4, v1}, Landroid/content/res/Resources;->getResourcePackageName(I)Ljava/lang/String;

    #@f3
    move-result-object v3

    #@f4
    .line 722
    .local v3, "pkgname":Ljava/lang/String;
    :goto_a
    invoke-virtual {v4, v1}, Landroid/content/res/Resources;->getResourceTypeName(I)Ljava/lang/String;

    #@f7
    move-result-object v5

    #@f8
    .line 723
    .local v5, "typename":Ljava/lang/String;
    invoke-virtual {v4, v1}, Landroid/content/res/Resources;->getResourceEntryName(I)Ljava/lang/String;

    #@fb
    move-result-object v0

    #@fc
    .line 724
    .local v0, "entryname":Ljava/lang/String;
    const-string v6, " "

    #@fe
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@101
    .line 725
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@104
    .line 726
    const-string v6, ":"

    #@106
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@109
    .line 727
    invoke-virtual {v2, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@10c
    .line 728
    const-string v6, "/"

    #@10e
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@111
    .line 729
    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    :try_end_0
    .catch Landroid/content/res/Resources$NotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    #@114
    .line 734
    .end local v0    # "entryname":Ljava/lang/String;
    .end local v3    # "pkgname":Ljava/lang/String;
    .end local v4    # "r":Landroid/content/res/Resources;
    .end local v5    # "typename":Ljava/lang/String;
    :cond_1
    :goto_b
    const-string v6, "}"

    #@116
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@119
    .line 735
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@11c
    move-result-object v6

    #@11d
    return-object v6

    #@11e
    .line 679
    .end local v1    # "id":I
    :sswitch_0
    invoke-virtual {v2, v9}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@121
    goto/16 :goto_0

    #@123
    .line 680
    :sswitch_1
    const/16 v6, 0x49

    #@125
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@128
    goto/16 :goto_0

    #@12a
    .line 681
    :sswitch_2
    const/16 v6, 0x47

    #@12c
    invoke-virtual {v2, v6}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@12f
    goto/16 :goto_0

    #@131
    :cond_2
    move v6, v8

    #@132
    .line 684
    goto/16 :goto_1

    #@134
    :cond_3
    move v6, v8

    #@135
    .line 685
    goto/16 :goto_2

    #@137
    .line 686
    :cond_4
    const/16 v6, 0x44

    #@139
    goto/16 :goto_3

    #@13b
    :cond_5
    move v6, v8

    #@13c
    .line 687
    goto/16 :goto_4

    #@13e
    :cond_6
    move v6, v8

    #@13f
    .line 688
    goto/16 :goto_5

    #@141
    :cond_7
    move v6, v8

    #@142
    .line 689
    goto/16 :goto_6

    #@144
    :cond_8
    move v6, v8

    #@145
    .line 690
    goto/16 :goto_7

    #@147
    :cond_9
    move v7, v8

    #@148
    .line 692
    goto/16 :goto_8

    #@14a
    :cond_a
    move v6, v8

    #@14b
    .line 693
    goto/16 :goto_9

    #@14d
    .line 713
    .restart local v1    # "id":I
    .restart local v4    # "r":Landroid/content/res/Resources;
    :sswitch_3
    :try_start_1
    const-string v3, "app"

    #@14f
    .line 714
    .restart local v3    # "pkgname":Ljava/lang/String;
    goto :goto_a

    #@150
    .line 716
    .end local v3    # "pkgname":Ljava/lang/String;
    :sswitch_4
    const-string v3, "android"
    :try_end_1
    .catch Landroid/content/res/Resources$NotFoundException; {:try_start_1 .. :try_end_1} :catch_0

    #@152
    .line 717
    .restart local v3    # "pkgname":Ljava/lang/String;
    goto :goto_a

    #@153
    .line 730
    .end local v3    # "pkgname":Ljava/lang/String;
    :catch_0
    move-exception v6

    #@154
    goto :goto_b

    #@155
    .line 678
    nop

    #@156
    :sswitch_data_0
    .sparse-switch
        0x0 -> :sswitch_0
        0x4 -> :sswitch_1
        0x8 -> :sswitch_2
    .end sparse-switch

    #@164
    .line 711
    :sswitch_data_1
    .sparse-switch
        0x1000000 -> :sswitch_4
        0x7f000000 -> :sswitch_3
    .end sparse-switch
.end method


# virtual methods
.method doReallyStop(Z)V
    .locals 2
    .param p1, "retaining"    # Z

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 760
    iget-boolean v0, p0, Landroid/support/v4/app/FragmentActivity;->mReallyStopped:Z

    #@3
    if-nez v0, :cond_0

    #@5
    .line 761
    iput-boolean v1, p0, Landroid/support/v4/app/FragmentActivity;->mReallyStopped:Z

    #@7
    .line 762
    iput-boolean p1, p0, Landroid/support/v4/app/FragmentActivity;->mRetaining:Z

    #@9
    .line 763
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@b
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    #@e
    .line 764
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentActivity;->onReallyStop()V

    #@11
    .line 766
    :cond_0
    return-void
.end method

.method public dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V
    .locals 4
    .param p1, "prefix"    # Ljava/lang/String;
    .param p2, "fd"    # Ljava/io/FileDescriptor;
    .param p3, "writer"    # Ljava/io/PrintWriter;
    .param p4, "args"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 646
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v2, 0xb

    #@4
    if-lt v1, v2, :cond_0

    #@6
    .line 650
    :cond_0
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@9
    const-string v1, "Local FragmentActivity "

    #@b
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@e
    .line 651
    invoke-static {p0}, Ljava/lang/System;->identityHashCode(Ljava/lang/Object;)I

    #@11
    move-result v1

    #@12
    invoke-static {v1}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@15
    move-result-object v1

    #@16
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@19
    .line 652
    const-string v1, " State:"

    #@1b
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@1e
    .line 653
    new-instance v1, Ljava/lang/StringBuilder;

    #@20
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@23
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@26
    move-result-object v1

    #@27
    const-string v2, "  "

    #@29
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2c
    move-result-object v1

    #@2d
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@30
    move-result-object v0

    #@31
    .line 654
    .local v0, "innerPrefix":Ljava/lang/String;
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@34
    const-string v1, "mCreated="

    #@36
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@39
    .line 655
    iget-boolean v1, p0, Landroid/support/v4/app/FragmentActivity;->mCreated:Z

    #@3b
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->print(Z)V

    #@3e
    const-string v1, "mResumed="

    #@40
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@43
    .line 656
    iget-boolean v1, p0, Landroid/support/v4/app/FragmentActivity;->mResumed:Z

    #@45
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->print(Z)V

    #@48
    const-string v1, " mStopped="

    #@4a
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@4d
    .line 657
    iget-boolean v1, p0, Landroid/support/v4/app/FragmentActivity;->mStopped:Z

    #@4f
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->print(Z)V

    #@52
    const-string v1, " mReallyStopped="

    #@54
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@57
    .line 658
    iget-boolean v1, p0, Landroid/support/v4/app/FragmentActivity;->mReallyStopped:Z

    #@59
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->println(Z)V

    #@5c
    .line 659
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@5f
    const-string v1, "mLoadersStarted="

    #@61
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@64
    .line 660
    iget-boolean v1, p0, Landroid/support/v4/app/FragmentActivity;->mLoadersStarted:Z

    #@66
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->println(Z)V

    #@69
    .line 661
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@6b
    if-eqz v1, :cond_1

    #@6d
    .line 662
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@70
    const-string v1, "Loader Manager "

    #@72
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@75
    .line 663
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@77
    invoke-static {v1}, Ljava/lang/System;->identityHashCode(Ljava/lang/Object;)I

    #@7a
    move-result v1

    #@7b
    invoke-static {v1}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@7e
    move-result-object v1

    #@7f
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@82
    .line 664
    const-string v1, ":"

    #@84
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@87
    .line 665
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@89
    new-instance v2, Ljava/lang/StringBuilder;

    #@8b
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@8e
    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@91
    move-result-object v2

    #@92
    const-string v3, "  "

    #@94
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@97
    move-result-object v2

    #@98
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@9b
    move-result-object v2

    #@9c
    invoke-virtual {v1, v2, p2, p3, p4}, Landroid/support/v4/app/LoaderManagerImpl;->dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V

    #@9f
    .line 667
    :cond_1
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@a1
    invoke-virtual {v1, p1, p2, p3, p4}, Landroid/support/v4/app/FragmentManagerImpl;->dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V

    #@a4
    .line 668
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@a7
    const-string v1, "View Hierarchy:"

    #@a9
    invoke-virtual {p3, v1}, Ljava/io/PrintWriter;->println(Ljava/lang/String;)V

    #@ac
    .line 669
    new-instance v1, Ljava/lang/StringBuilder;

    #@ae
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@b1
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@b4
    move-result-object v1

    #@b5
    const-string v2, "  "

    #@b7
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@ba
    move-result-object v1

    #@bb
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@be
    move-result-object v1

    #@bf
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentActivity;->getWindow()Landroid/view/Window;

    #@c2
    move-result-object v2

    #@c3
    invoke-virtual {v2}, Landroid/view/Window;->getDecorView()Landroid/view/View;

    #@c6
    move-result-object v2

    #@c7
    invoke-direct {p0, v1, p3, v2}, Landroid/support/v4/app/FragmentActivity;->dumpViewHierarchy(Ljava/lang/String;Ljava/io/PrintWriter;Landroid/view/View;)V

    #@ca
    .line 670
    return-void
.end method

.method public getLastCustomNonConfigurationInstance()Ljava/lang/Object;
    .locals 2

    #@0
    .prologue
    .line 610
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentActivity;->getLastNonConfigurationInstance()Ljava/lang/Object;

    #@3
    move-result-object v0

    #@4
    check-cast v0, Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;

    #@6
    .line 612
    .local v0, "nc":Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;
    if-eqz v0, :cond_0

    #@8
    iget-object v1, v0, Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;->custom:Ljava/lang/Object;

    #@a
    :goto_0
    return-object v1

    #@b
    :cond_0
    const/4 v1, 0x0

    #@c
    goto :goto_0
.end method

.method getLoaderManager(Ljava/lang/String;ZZ)Landroid/support/v4/app/LoaderManagerImpl;
    .locals 2
    .param p1, "who"    # Ljava/lang/String;
    .param p2, "started"    # Z
    .param p3, "create"    # Z

    #@0
    .prologue
    .line 863
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@2
    if-nez v1, :cond_0

    #@4
    .line 864
    new-instance v1, Landroid/support/v4/util/SimpleArrayMap;

    #@6
    invoke-direct {v1}, Landroid/support/v4/util/SimpleArrayMap;-><init>()V

    #@9
    iput-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@b
    .line 866
    :cond_0
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@d
    invoke-virtual {v1, p1}, Landroid/support/v4/util/SimpleArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@10
    move-result-object v0

    #@11
    check-cast v0, Landroid/support/v4/app/LoaderManagerImpl;

    #@13
    .line 867
    .local v0, "lm":Landroid/support/v4/app/LoaderManagerImpl;
    if-nez v0, :cond_2

    #@15
    .line 868
    if-eqz p3, :cond_1

    #@17
    .line 869
    new-instance v0, Landroid/support/v4/app/LoaderManagerImpl;

    #@19
    .end local v0    # "lm":Landroid/support/v4/app/LoaderManagerImpl;
    invoke-direct {v0, p1, p0, p2}, Landroid/support/v4/app/LoaderManagerImpl;-><init>(Ljava/lang/String;Landroid/support/v4/app/FragmentActivity;Z)V

    #@1c
    .line 870
    .restart local v0    # "lm":Landroid/support/v4/app/LoaderManagerImpl;
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@1e
    invoke-virtual {v1, p1, v0}, Landroid/support/v4/util/SimpleArrayMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@21
    .line 875
    :cond_1
    :goto_0
    return-object v0

    #@22
    .line 873
    :cond_2
    invoke-virtual {v0, p0}, Landroid/support/v4/app/LoaderManagerImpl;->updateActivity(Landroid/support/v4/app/FragmentActivity;)V

    #@25
    goto :goto_0
.end method

.method public getSupportFragmentManager()Landroid/support/v4/app/FragmentManager;
    .locals 1

    #@0
    .prologue
    .line 805
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    return-object v0
.end method

.method public getSupportLoaderManager()Landroid/support/v4/app/LoaderManager;
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    .line 854
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@3
    if-eqz v0, :cond_0

    #@5
    .line 855
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@7
    .line 859
    :goto_0
    return-object v0

    #@8
    .line 857
    :cond_0
    iput-boolean v2, p0, Landroid/support/v4/app/FragmentActivity;->mCheckedForLoaderManager:Z

    #@a
    .line 858
    const-string v0, "(root)"

    #@c
    iget-boolean v1, p0, Landroid/support/v4/app/FragmentActivity;->mLoadersStarted:Z

    #@e
    invoke-virtual {p0, v0, v1, v2}, Landroid/support/v4/app/FragmentActivity;->getLoaderManager(Ljava/lang/String;ZZ)Landroid/support/v4/app/LoaderManagerImpl;

    #@11
    move-result-object v0

    #@12
    iput-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@14
    .line 859
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@16
    goto :goto_0
.end method

.method invalidateSupportFragment(Ljava/lang/String;)V
    .locals 2
    .param p1, "who"    # Ljava/lang/String;

    #@0
    .prologue
    .line 837
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@2
    if-eqz v1, :cond_0

    #@4
    .line 838
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@6
    invoke-virtual {v1, p1}, Landroid/support/v4/util/SimpleArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@9
    move-result-object v0

    #@a
    check-cast v0, Landroid/support/v4/app/LoaderManagerImpl;

    #@c
    .line 839
    .local v0, "lm":Landroid/support/v4/app/LoaderManagerImpl;
    if-eqz v0, :cond_0

    #@e
    iget-boolean v1, v0, Landroid/support/v4/app/LoaderManagerImpl;->mRetaining:Z

    #@10
    if-nez v1, :cond_0

    #@12
    .line 840
    invoke-virtual {v0}, Landroid/support/v4/app/LoaderManagerImpl;->doDestroy()V

    #@15
    .line 841
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@17
    invoke-virtual {v1, p1}, Landroid/support/v4/util/SimpleArrayMap;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    #@1a
    .line 844
    .end local v0    # "lm":Landroid/support/v4/app/LoaderManagerImpl;
    :cond_0
    return-void
.end method

.method protected onActivityResult(IILandroid/content/Intent;)V
    .locals 5
    .param p1, "requestCode"    # I
    .param p2, "resultCode"    # I
    .param p3, "data"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 149
    iget-object v2, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    invoke-virtual {v2}, Landroid/support/v4/app/FragmentManagerImpl;->noteStateNotSaved()V

    #@5
    .line 150
    shr-int/lit8 v1, p1, 0x10

    #@7
    .line 151
    .local v1, "index":I
    if-eqz v1, :cond_3

    #@9
    .line 152
    add-int/lit8 v1, v1, -0x1

    #@b
    .line 153
    iget-object v2, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@d
    iget-object v2, v2, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@f
    if-eqz v2, :cond_0

    #@11
    if-ltz v1, :cond_0

    #@13
    iget-object v2, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@15
    iget-object v2, v2, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@17
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@1a
    move-result v2

    #@1b
    if-lt v1, v2, :cond_1

    #@1d
    .line 154
    :cond_0
    const-string v2, "FragmentActivity"

    #@1f
    new-instance v3, Ljava/lang/StringBuilder;

    #@21
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@24
    const-string v4, "Activity result fragment index out of range: 0x"

    #@26
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@29
    move-result-object v3

    #@2a
    invoke-static {p1}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@2d
    move-result-object v4

    #@2e
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@31
    move-result-object v3

    #@32
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@35
    move-result-object v3

    #@36
    invoke-static {v2, v3}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@39
    .line 169
    :goto_0
    return-void

    #@3a
    .line 158
    :cond_1
    iget-object v2, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@3c
    iget-object v2, v2, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@3e
    invoke-virtual {v2, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@41
    move-result-object v0

    #@42
    check-cast v0, Landroid/support/v4/app/Fragment;

    #@44
    .line 159
    .local v0, "frag":Landroid/support/v4/app/Fragment;
    if-nez v0, :cond_2

    #@46
    .line 160
    const-string v2, "FragmentActivity"

    #@48
    new-instance v3, Ljava/lang/StringBuilder;

    #@4a
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@4d
    const-string v4, "Activity result no fragment exists for index: 0x"

    #@4f
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@52
    move-result-object v3

    #@53
    invoke-static {p1}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@56
    move-result-object v4

    #@57
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@5a
    move-result-object v3

    #@5b
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@5e
    move-result-object v3

    #@5f
    invoke-static {v2, v3}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@62
    goto :goto_0

    #@63
    .line 163
    :cond_2
    const v2, 0xffff

    #@66
    and-int/2addr v2, p1

    #@67
    invoke-virtual {v0, v2, p2, p3}, Landroid/support/v4/app/Fragment;->onActivityResult(IILandroid/content/Intent;)V

    #@6a
    goto :goto_0

    #@6b
    .line 168
    .end local v0    # "frag":Landroid/support/v4/app/Fragment;
    :cond_3
    invoke-super {p0, p1, p2, p3}, Landroid/app/Activity;->onActivityResult(IILandroid/content/Intent;)V

    #@6e
    goto :goto_0
.end method

.method public onAttachFragment(Landroid/support/v4/app/Fragment;)V
    .locals 0
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;

    #@0
    .prologue
    .line 798
    return-void
.end method

.method public onBackPressed()V
    .locals 1

    #@0
    .prologue
    .line 176
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->popBackStackImmediate()Z

    #@5
    move-result v0

    #@6
    if-nez v0, :cond_0

    #@8
    .line 177
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentActivity;->supportFinishAfterTransition()V

    #@b
    .line 179
    :cond_0
    return-void
.end method

.method public onConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 1
    .param p1, "newConfig"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    .line 240
    invoke-super {p0, p1}, Landroid/app/Activity;->onConfigurationChanged(Landroid/content/res/Configuration;)V

    #@3
    .line 241
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@5
    invoke-virtual {v0, p1}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchConfigurationChanged(Landroid/content/res/Configuration;)V

    #@8
    .line 242
    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .locals 5
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 249
    iget-object v3, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@3
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mContainer:Landroid/support/v4/app/FragmentContainer;

    #@5
    invoke-virtual {v3, p0, v4, v2}, Landroid/support/v4/app/FragmentManagerImpl;->attachActivity(Landroid/support/v4/app/FragmentActivity;Landroid/support/v4/app/FragmentContainer;Landroid/support/v4/app/Fragment;)V

    #@8
    .line 251
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentActivity;->getLayoutInflater()Landroid/view/LayoutInflater;

    #@b
    move-result-object v3

    #@c
    invoke-virtual {v3}, Landroid/view/LayoutInflater;->getFactory()Landroid/view/LayoutInflater$Factory;

    #@f
    move-result-object v3

    #@10
    if-nez v3, :cond_0

    #@12
    .line 252
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentActivity;->getLayoutInflater()Landroid/view/LayoutInflater;

    #@15
    move-result-object v3

    #@16
    invoke-virtual {v3, p0}, Landroid/view/LayoutInflater;->setFactory(Landroid/view/LayoutInflater$Factory;)V

    #@19
    .line 255
    :cond_0
    invoke-super {p0, p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V

    #@1c
    .line 257
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentActivity;->getLastNonConfigurationInstance()Ljava/lang/Object;

    #@1f
    move-result-object v0

    #@20
    check-cast v0, Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;

    #@22
    .line 259
    .local v0, "nc":Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;
    if-eqz v0, :cond_1

    #@24
    .line 260
    iget-object v3, v0, Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;->loaders:Landroid/support/v4/util/SimpleArrayMap;

    #@26
    iput-object v3, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@28
    .line 262
    :cond_1
    if-eqz p1, :cond_3

    #@2a
    .line 263
    const-string v3, "android:support:fragments"

    #@2c
    invoke-virtual {p1, v3}, Landroid/os/Bundle;->getParcelable(Ljava/lang/String;)Landroid/os/Parcelable;

    #@2f
    move-result-object v1

    #@30
    .line 264
    .local v1, "p":Landroid/os/Parcelable;
    iget-object v3, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@32
    if-eqz v0, :cond_2

    #@34
    iget-object v2, v0, Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;->fragments:Ljava/util/ArrayList;

    #@36
    :cond_2
    invoke-virtual {v3, v1, v2}, Landroid/support/v4/app/FragmentManagerImpl;->restoreAllState(Landroid/os/Parcelable;Ljava/util/ArrayList;)V

    #@39
    .line 266
    .end local v1    # "p":Landroid/os/Parcelable;
    :cond_3
    iget-object v2, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@3b
    invoke-virtual {v2}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchCreate()V

    #@3e
    .line 267
    return-void
.end method

.method public onCreatePanelMenu(ILandroid/view/Menu;)Z
    .locals 3
    .param p1, "featureId"    # I
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 274
    if-nez p1, :cond_1

    #@2
    .line 275
    invoke-super {p0, p1, p2}, Landroid/app/Activity;->onCreatePanelMenu(ILandroid/view/Menu;)Z

    #@5
    move-result v0

    #@6
    .line 276
    .local v0, "show":Z
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@8
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentActivity;->getMenuInflater()Landroid/view/MenuInflater;

    #@b
    move-result-object v2

    #@c
    invoke-virtual {v1, p2, v2}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchCreateOptionsMenu(Landroid/view/Menu;Landroid/view/MenuInflater;)Z

    #@f
    move-result v1

    #@10
    or-int/2addr v0, v1

    #@11
    .line 277
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@13
    const/16 v2, 0xb

    #@15
    if-lt v1, v2, :cond_0

    #@17
    .line 285
    .end local v0    # "show":Z
    :goto_0
    return v0

    #@18
    .line 283
    .restart local v0    # "show":Z
    :cond_0
    const/4 v0, 0x1

    #@19
    goto :goto_0

    #@1a
    .line 285
    .end local v0    # "show":Z
    :cond_1
    invoke-super {p0, p1, p2}, Landroid/app/Activity;->onCreatePanelMenu(ILandroid/view/Menu;)Z

    #@1d
    move-result v0

    #@1e
    goto :goto_0
.end method

.method public onCreateView(Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)Landroid/view/View;
    .locals 2
    .param p1, "name"    # Ljava/lang/String;
    .param p2, "context"    # Landroid/content/Context;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param
    .param p3, "attrs"    # Landroid/util/AttributeSet;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param

    #@0
    .prologue
    .line 293
    const-string v1, "fragment"

    #@2
    invoke-virtual {v1, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@5
    move-result v1

    #@6
    if-nez v1, :cond_1

    #@8
    .line 294
    invoke-super {p0, p1, p2, p3}, Landroid/app/Activity;->onCreateView(Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)Landroid/view/View;

    #@b
    move-result-object v0

    #@c
    .line 301
    :cond_0
    :goto_0
    return-object v0

    #@d
    .line 297
    :cond_1
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@f
    invoke-virtual {v1, p1, p2, p3}, Landroid/support/v4/app/FragmentManagerImpl;->onCreateView(Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)Landroid/view/View;

    #@12
    move-result-object v0

    #@13
    .line 298
    .local v0, "v":Landroid/view/View;
    if-nez v0, :cond_0

    #@15
    .line 299
    invoke-super {p0, p1, p2, p3}, Landroid/app/Activity;->onCreateView(Ljava/lang/String;Landroid/content/Context;Landroid/util/AttributeSet;)Landroid/view/View;

    #@18
    move-result-object v0

    #@19
    goto :goto_0
.end method

.method protected onDestroy()V
    .locals 1

    #@0
    .prologue
    .line 309
    invoke-super {p0}, Landroid/app/Activity;->onDestroy()V

    #@3
    .line 311
    const/4 v0, 0x0

    #@4
    invoke-virtual {p0, v0}, Landroid/support/v4/app/FragmentActivity;->doReallyStop(Z)V

    #@7
    .line 313
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@9
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchDestroy()V

    #@c
    .line 314
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@e
    if-eqz v0, :cond_0

    #@10
    .line 315
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@12
    invoke-virtual {v0}, Landroid/support/v4/app/LoaderManagerImpl;->doDestroy()V

    #@15
    .line 317
    :cond_0
    return-void
.end method

.method public onKeyDown(ILandroid/view/KeyEvent;)Z
    .locals 2
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 324
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/4 v1, 0x5

    #@3
    if-ge v0, v1, :cond_0

    #@5
    const/4 v0, 0x4

    #@6
    if-ne p1, v0, :cond_0

    #@8
    invoke-virtual {p2}, Landroid/view/KeyEvent;->getRepeatCount()I

    #@b
    move-result v0

    #@c
    if-nez v0, :cond_0

    #@e
    .line 329
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentActivity;->onBackPressed()V

    #@11
    .line 330
    const/4 v0, 0x1

    #@12
    .line 333
    :goto_0
    return v0

    #@13
    :cond_0
    invoke-super {p0, p1, p2}, Landroid/app/Activity;->onKeyDown(ILandroid/view/KeyEvent;)Z

    #@16
    move-result v0

    #@17
    goto :goto_0
.end method

.method public onLowMemory()V
    .locals 1

    #@0
    .prologue
    .line 341
    invoke-super {p0}, Landroid/app/Activity;->onLowMemory()V

    #@3
    .line 342
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@5
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchLowMemory()V

    #@8
    .line 343
    return-void
.end method

.method public onMenuItemSelected(ILandroid/view/MenuItem;)Z
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 350
    invoke-super {p0, p1, p2}, Landroid/app/Activity;->onMenuItemSelected(ILandroid/view/MenuItem;)Z

    #@3
    move-result v0

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 351
    const/4 v0, 0x1

    #@7
    .line 362
    :goto_0
    return v0

    #@8
    .line 354
    :cond_0
    sparse-switch p1, :sswitch_data_0

    #@b
    .line 362
    const/4 v0, 0x0

    #@c
    goto :goto_0

    #@d
    .line 356
    :sswitch_0
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@f
    invoke-virtual {v0, p2}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchOptionsItemSelected(Landroid/view/MenuItem;)Z

    #@12
    move-result v0

    #@13
    goto :goto_0

    #@14
    .line 359
    :sswitch_1
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@16
    invoke-virtual {v0, p2}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchContextItemSelected(Landroid/view/MenuItem;)Z

    #@19
    move-result v0

    #@1a
    goto :goto_0

    #@1b
    .line 354
    nop

    #@1c
    :sswitch_data_0
    .sparse-switch
        0x0 -> :sswitch_0
        0x6 -> :sswitch_1
    .end sparse-switch
.end method

.method protected onNewIntent(Landroid/content/Intent;)V
    .locals 1
    .param p1, "intent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 405
    invoke-super {p0, p1}, Landroid/app/Activity;->onNewIntent(Landroid/content/Intent;)V

    #@3
    .line 406
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@5
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->noteStateNotSaved()V

    #@8
    .line 407
    return-void
.end method

.method public onPanelClosed(ILandroid/view/Menu;)V
    .locals 1
    .param p1, "featureId"    # I
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 371
    packed-switch p1, :pswitch_data_0

    #@3
    .line 376
    :goto_0
    invoke-super {p0, p1, p2}, Landroid/app/Activity;->onPanelClosed(ILandroid/view/Menu;)V

    #@6
    .line 377
    return-void

    #@7
    .line 373
    :pswitch_0
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@9
    invoke-virtual {v0, p2}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchOptionsMenuClosed(Landroid/view/Menu;)V

    #@c
    goto :goto_0

    #@d
    .line 371
    nop

    #@e
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
    .end packed-switch
.end method

.method protected onPause()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x2

    #@1
    .line 384
    invoke-super {p0}, Landroid/app/Activity;->onPause()V

    #@4
    .line 385
    const/4 v0, 0x0

    #@5
    iput-boolean v0, p0, Landroid/support/v4/app/FragmentActivity;->mResumed:Z

    #@7
    .line 386
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@9
    invoke-virtual {v0, v1}, Landroid/os/Handler;->hasMessages(I)Z

    #@c
    move-result v0

    #@d
    if-eqz v0, :cond_0

    #@f
    .line 387
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@11
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    #@14
    .line 388
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentActivity;->onResumeFragments()V

    #@17
    .line 390
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@19
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchPause()V

    #@1c
    .line 391
    return-void
.end method

.method protected onPostResume()V
    .locals 2

    #@0
    .prologue
    .line 431
    invoke-super {p0}, Landroid/app/Activity;->onPostResume()V

    #@3
    .line 432
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@5
    const/4 v1, 0x2

    #@6
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeMessages(I)V

    #@9
    .line 433
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentActivity;->onResumeFragments()V

    #@c
    .line 434
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@e
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->execPendingActions()Z

    #@11
    .line 435
    return-void
.end method

.method protected onPrepareOptionsPanel(Landroid/view/View;Landroid/view/Menu;)Z
    .locals 1
    .param p1, "view"    # Landroid/view/View;
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 469
    const/4 v0, 0x0

    #@1
    invoke-super {p0, v0, p1, p2}, Landroid/app/Activity;->onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z

    #@4
    move-result v0

    #@5
    return v0
.end method

.method public onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z
    .locals 2
    .param p1, "featureId"    # I
    .param p2, "view"    # Landroid/view/View;
    .param p3, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 452
    if-nez p1, :cond_1

    #@2
    if-eqz p3, :cond_1

    #@4
    .line 453
    iget-boolean v1, p0, Landroid/support/v4/app/FragmentActivity;->mOptionsMenuInvalidated:Z

    #@6
    if-eqz v1, :cond_0

    #@8
    .line 454
    const/4 v1, 0x0

    #@9
    iput-boolean v1, p0, Landroid/support/v4/app/FragmentActivity;->mOptionsMenuInvalidated:Z

    #@b
    .line 455
    invoke-interface {p3}, Landroid/view/Menu;->clear()V

    #@e
    .line 456
    invoke-virtual {p0, p1, p3}, Landroid/support/v4/app/FragmentActivity;->onCreatePanelMenu(ILandroid/view/Menu;)Z

    #@11
    .line 458
    :cond_0
    invoke-virtual {p0, p2, p3}, Landroid/support/v4/app/FragmentActivity;->onPrepareOptionsPanel(Landroid/view/View;Landroid/view/Menu;)Z

    #@14
    move-result v0

    #@15
    .line 459
    .local v0, "goforit":Z
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@17
    invoke-virtual {v1, p3}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchPrepareOptionsMenu(Landroid/view/Menu;)Z

    #@1a
    move-result v1

    #@1b
    or-int/2addr v0, v1

    #@1c
    .line 462
    .end local v0    # "goforit":Z
    :goto_0
    return v0

    #@1d
    :cond_1
    invoke-super {p0, p1, p2, p3}, Landroid/app/Activity;->onPreparePanel(ILandroid/view/View;Landroid/view/Menu;)Z

    #@20
    move-result v0

    #@21
    goto :goto_0
.end method

.method onReallyStop()V
    .locals 1

    #@0
    .prologue
    .line 776
    iget-boolean v0, p0, Landroid/support/v4/app/FragmentActivity;->mLoadersStarted:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 777
    const/4 v0, 0x0

    #@5
    iput-boolean v0, p0, Landroid/support/v4/app/FragmentActivity;->mLoadersStarted:Z

    #@7
    .line 778
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@9
    if-eqz v0, :cond_0

    #@b
    .line 779
    iget-boolean v0, p0, Landroid/support/v4/app/FragmentActivity;->mRetaining:Z

    #@d
    if-nez v0, :cond_1

    #@f
    .line 780
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@11
    invoke-virtual {v0}, Landroid/support/v4/app/LoaderManagerImpl;->doStop()V

    #@14
    .line 787
    :cond_0
    :goto_0
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@16
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchReallyStop()V

    #@19
    .line 788
    return-void

    #@1a
    .line 782
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@1c
    invoke-virtual {v0}, Landroid/support/v4/app/LoaderManagerImpl;->doRetain()V

    #@1f
    goto :goto_0
.end method

.method protected onResume()V
    .locals 2

    #@0
    .prologue
    .line 420
    invoke-super {p0}, Landroid/app/Activity;->onResume()V

    #@3
    .line 421
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@5
    const/4 v1, 0x2

    #@6
    invoke-virtual {v0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    #@9
    .line 422
    const/4 v0, 0x1

    #@a
    iput-boolean v0, p0, Landroid/support/v4/app/FragmentActivity;->mResumed:Z

    #@c
    .line 423
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@e
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->execPendingActions()Z

    #@11
    .line 424
    return-void
.end method

.method protected onResumeFragments()V
    .locals 1

    #@0
    .prologue
    .line 444
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchResume()V

    #@5
    .line 445
    return-void
.end method

.method public onRetainCustomNonConfigurationInstance()Ljava/lang/Object;
    .locals 1

    #@0
    .prologue
    .line 602
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public final onRetainNonConfigurationInstance()Ljava/lang/Object;
    .locals 11

    #@0
    .prologue
    const/4 v9, 0x0

    #@1
    .line 479
    iget-boolean v8, p0, Landroid/support/v4/app/FragmentActivity;->mStopped:Z

    #@3
    if-eqz v8, :cond_0

    #@5
    .line 480
    const/4 v8, 0x1

    #@6
    invoke-virtual {p0, v8}, Landroid/support/v4/app/FragmentActivity;->doReallyStop(Z)V

    #@9
    .line 483
    :cond_0
    invoke-virtual {p0}, Landroid/support/v4/app/FragmentActivity;->onRetainCustomNonConfigurationInstance()Ljava/lang/Object;

    #@c
    move-result-object v1

    #@d
    .line 485
    .local v1, "custom":Ljava/lang/Object;
    iget-object v8, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@f
    invoke-virtual {v8}, Landroid/support/v4/app/FragmentManagerImpl;->retainNonConfig()Ljava/util/ArrayList;

    #@12
    move-result-object v2

    #@13
    .line 486
    .local v2, "fragments":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v4/app/Fragment;>;"
    const/4 v7, 0x0

    #@14
    .line 487
    .local v7, "retainLoaders":Z
    iget-object v8, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@16
    if-eqz v8, :cond_3

    #@18
    .line 490
    iget-object v8, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@1a
    invoke-virtual {v8}, Landroid/support/v4/util/SimpleArrayMap;->size()I

    #@1d
    move-result v0

    #@1e
    .line 491
    .local v0, "N":I
    new-array v5, v0, [Landroid/support/v4/app/LoaderManagerImpl;

    #@20
    .line 492
    .local v5, "loaders":[Landroid/support/v4/app/LoaderManagerImpl;
    add-int/lit8 v3, v0, -0x1

    #@22
    .local v3, "i":I
    :goto_0
    if-ltz v3, :cond_1

    #@24
    .line 493
    iget-object v8, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@26
    invoke-virtual {v8, v3}, Landroid/support/v4/util/SimpleArrayMap;->valueAt(I)Ljava/lang/Object;

    #@29
    move-result-object v8

    #@2a
    check-cast v8, Landroid/support/v4/app/LoaderManagerImpl;

    #@2c
    aput-object v8, v5, v3

    #@2e
    .line 492
    add-int/lit8 v3, v3, -0x1

    #@30
    goto :goto_0

    #@31
    .line 495
    :cond_1
    const/4 v3, 0x0

    #@32
    :goto_1
    if-ge v3, v0, :cond_3

    #@34
    .line 496
    aget-object v4, v5, v3

    #@36
    .line 497
    .local v4, "lm":Landroid/support/v4/app/LoaderManagerImpl;
    iget-boolean v8, v4, Landroid/support/v4/app/LoaderManagerImpl;->mRetaining:Z

    #@38
    if-eqz v8, :cond_2

    #@3a
    .line 498
    const/4 v7, 0x1

    #@3b
    .line 495
    :goto_2
    add-int/lit8 v3, v3, 0x1

    #@3d
    goto :goto_1

    #@3e
    .line 500
    :cond_2
    invoke-virtual {v4}, Landroid/support/v4/app/LoaderManagerImpl;->doDestroy()V

    #@41
    .line 501
    iget-object v8, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@43
    iget-object v10, v4, Landroid/support/v4/app/LoaderManagerImpl;->mWho:Ljava/lang/String;

    #@45
    invoke-virtual {v8, v10}, Landroid/support/v4/util/SimpleArrayMap;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    #@48
    goto :goto_2

    #@49
    .line 505
    .end local v0    # "N":I
    .end local v3    # "i":I
    .end local v4    # "lm":Landroid/support/v4/app/LoaderManagerImpl;
    .end local v5    # "loaders":[Landroid/support/v4/app/LoaderManagerImpl;
    :cond_3
    if-nez v2, :cond_4

    #@4b
    if-nez v7, :cond_4

    #@4d
    if-nez v1, :cond_4

    #@4f
    move-object v6, v9

    #@50
    .line 515
    :goto_3
    return-object v6

    #@51
    .line 509
    :cond_4
    new-instance v6, Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;

    #@53
    invoke-direct {v6}, Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;-><init>()V

    #@56
    .line 510
    .local v6, "nci":Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;
    iput-object v9, v6, Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;->activity:Ljava/lang/Object;

    #@58
    .line 511
    iput-object v1, v6, Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;->custom:Ljava/lang/Object;

    #@5a
    .line 512
    iput-object v9, v6, Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;->children:Landroid/support/v4/util/SimpleArrayMap;

    #@5c
    .line 513
    iput-object v2, v6, Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;->fragments:Ljava/util/ArrayList;

    #@5e
    .line 514
    iget-object v8, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@60
    iput-object v8, v6, Landroid/support/v4/app/FragmentActivity$NonConfigurationInstances;->loaders:Landroid/support/v4/util/SimpleArrayMap;

    #@62
    goto :goto_3
.end method

.method protected onSaveInstanceState(Landroid/os/Bundle;)V
    .locals 2
    .param p1, "outState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 523
    invoke-super {p0, p1}, Landroid/app/Activity;->onSaveInstanceState(Landroid/os/Bundle;)V

    #@3
    .line 524
    iget-object v1, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@5
    invoke-virtual {v1}, Landroid/support/v4/app/FragmentManagerImpl;->saveAllState()Landroid/os/Parcelable;

    #@8
    move-result-object v0

    #@9
    .line 525
    .local v0, "p":Landroid/os/Parcelable;
    if-eqz v0, :cond_0

    #@b
    .line 526
    const-string v1, "android:support:fragments"

    #@d
    invoke-virtual {p1, v1, v0}, Landroid/os/Bundle;->putParcelable(Ljava/lang/String;Landroid/os/Parcelable;)V

    #@10
    .line 528
    :cond_0
    return-void
.end method

.method protected onStart()V
    .locals 8

    #@0
    .prologue
    const/4 v7, 0x0

    #@1
    const/4 v6, 0x1

    #@2
    .line 536
    invoke-super {p0}, Landroid/app/Activity;->onStart()V

    #@5
    .line 538
    iput-boolean v7, p0, Landroid/support/v4/app/FragmentActivity;->mStopped:Z

    #@7
    .line 539
    iput-boolean v7, p0, Landroid/support/v4/app/FragmentActivity;->mReallyStopped:Z

    #@9
    .line 540
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@b
    invoke-virtual {v4, v6}, Landroid/os/Handler;->removeMessages(I)V

    #@e
    .line 542
    iget-boolean v4, p0, Landroid/support/v4/app/FragmentActivity;->mCreated:Z

    #@10
    if-nez v4, :cond_0

    #@12
    .line 543
    iput-boolean v6, p0, Landroid/support/v4/app/FragmentActivity;->mCreated:Z

    #@14
    .line 544
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@16
    invoke-virtual {v4}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchActivityCreated()V

    #@19
    .line 547
    :cond_0
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@1b
    invoke-virtual {v4}, Landroid/support/v4/app/FragmentManagerImpl;->noteStateNotSaved()V

    #@1e
    .line 548
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@20
    invoke-virtual {v4}, Landroid/support/v4/app/FragmentManagerImpl;->execPendingActions()Z

    #@23
    .line 550
    iget-boolean v4, p0, Landroid/support/v4/app/FragmentActivity;->mLoadersStarted:Z

    #@25
    if-nez v4, :cond_2

    #@27
    .line 551
    iput-boolean v6, p0, Landroid/support/v4/app/FragmentActivity;->mLoadersStarted:Z

    #@29
    .line 552
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@2b
    if-eqz v4, :cond_3

    #@2d
    .line 553
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@2f
    invoke-virtual {v4}, Landroid/support/v4/app/LoaderManagerImpl;->doStart()V

    #@32
    .line 561
    :cond_1
    :goto_0
    iput-boolean v6, p0, Landroid/support/v4/app/FragmentActivity;->mCheckedForLoaderManager:Z

    #@34
    .line 565
    :cond_2
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@36
    invoke-virtual {v4}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchStart()V

    #@39
    .line 566
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@3b
    if-eqz v4, :cond_5

    #@3d
    .line 567
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@3f
    invoke-virtual {v4}, Landroid/support/v4/util/SimpleArrayMap;->size()I

    #@42
    move-result v0

    #@43
    .line 568
    .local v0, "N":I
    new-array v3, v0, [Landroid/support/v4/app/LoaderManagerImpl;

    #@45
    .line 569
    .local v3, "loaders":[Landroid/support/v4/app/LoaderManagerImpl;
    add-int/lit8 v1, v0, -0x1

    #@47
    .local v1, "i":I
    :goto_1
    if-ltz v1, :cond_4

    #@49
    .line 570
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mAllLoaderManagers:Landroid/support/v4/util/SimpleArrayMap;

    #@4b
    invoke-virtual {v4, v1}, Landroid/support/v4/util/SimpleArrayMap;->valueAt(I)Ljava/lang/Object;

    #@4e
    move-result-object v4

    #@4f
    check-cast v4, Landroid/support/v4/app/LoaderManagerImpl;

    #@51
    aput-object v4, v3, v1

    #@53
    .line 569
    add-int/lit8 v1, v1, -0x1

    #@55
    goto :goto_1

    #@56
    .line 554
    .end local v0    # "N":I
    .end local v1    # "i":I
    .end local v3    # "loaders":[Landroid/support/v4/app/LoaderManagerImpl;
    :cond_3
    iget-boolean v4, p0, Landroid/support/v4/app/FragmentActivity;->mCheckedForLoaderManager:Z

    #@58
    if-nez v4, :cond_1

    #@5a
    .line 555
    const-string v4, "(root)"

    #@5c
    iget-boolean v5, p0, Landroid/support/v4/app/FragmentActivity;->mLoadersStarted:Z

    #@5e
    invoke-virtual {p0, v4, v5, v7}, Landroid/support/v4/app/FragmentActivity;->getLoaderManager(Ljava/lang/String;ZZ)Landroid/support/v4/app/LoaderManagerImpl;

    #@61
    move-result-object v4

    #@62
    iput-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@64
    .line 557
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@66
    if-eqz v4, :cond_1

    #@68
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@6a
    iget-boolean v4, v4, Landroid/support/v4/app/LoaderManagerImpl;->mStarted:Z

    #@6c
    if-nez v4, :cond_1

    #@6e
    .line 558
    iget-object v4, p0, Landroid/support/v4/app/FragmentActivity;->mLoaderManager:Landroid/support/v4/app/LoaderManagerImpl;

    #@70
    invoke-virtual {v4}, Landroid/support/v4/app/LoaderManagerImpl;->doStart()V

    #@73
    goto :goto_0

    #@74
    .line 572
    .restart local v0    # "N":I
    .restart local v1    # "i":I
    .restart local v3    # "loaders":[Landroid/support/v4/app/LoaderManagerImpl;
    :cond_4
    const/4 v1, 0x0

    #@75
    :goto_2
    if-ge v1, v0, :cond_5

    #@77
    .line 573
    aget-object v2, v3, v1

    #@79
    .line 574
    .local v2, "lm":Landroid/support/v4/app/LoaderManagerImpl;
    invoke-virtual {v2}, Landroid/support/v4/app/LoaderManagerImpl;->finishRetain()V

    #@7c
    .line 575
    invoke-virtual {v2}, Landroid/support/v4/app/LoaderManagerImpl;->doReportStart()V

    #@7f
    .line 572
    add-int/lit8 v1, v1, 0x1

    #@81
    goto :goto_2

    #@82
    .line 578
    .end local v0    # "N":I
    .end local v1    # "i":I
    .end local v2    # "lm":Landroid/support/v4/app/LoaderManagerImpl;
    .end local v3    # "loaders":[Landroid/support/v4/app/LoaderManagerImpl;
    :cond_5
    return-void
.end method

.method protected onStop()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 585
    invoke-super {p0}, Landroid/app/Activity;->onStop()V

    #@4
    .line 587
    iput-boolean v1, p0, Landroid/support/v4/app/FragmentActivity;->mStopped:Z

    #@6
    .line 588
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mHandler:Landroid/os/Handler;

    #@8
    invoke-virtual {v0, v1}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    #@b
    .line 590
    iget-object v0, p0, Landroid/support/v4/app/FragmentActivity;->mFragments:Landroid/support/v4/app/FragmentManagerImpl;

    #@d
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentManagerImpl;->dispatchStop()V

    #@10
    .line 591
    return-void
.end method

.method public setEnterSharedElementCallback(Landroid/support/v4/app/SharedElementCallback;)V
    .locals 0
    .param p1, "callback"    # Landroid/support/v4/app/SharedElementCallback;

    #@0
    .prologue
    .line 203
    invoke-static {p0, p1}, Landroid/support/v4/app/ActivityCompat;->setEnterSharedElementCallback(Landroid/app/Activity;Landroid/support/v4/app/SharedElementCallback;)V

    #@3
    .line 204
    return-void
.end method

.method public setExitSharedElementCallback(Landroid/support/v4/app/SharedElementCallback;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v4/app/SharedElementCallback;

    #@0
    .prologue
    .line 216
    invoke-static {p0, p1}, Landroid/support/v4/app/ActivityCompat;->setExitSharedElementCallback(Landroid/app/Activity;Landroid/support/v4/app/SharedElementCallback;)V

    #@3
    .line 217
    return-void
.end method

.method public startActivityForResult(Landroid/content/Intent;I)V
    .locals 2
    .param p1, "intent"    # Landroid/content/Intent;
    .param p2, "requestCode"    # I

    #@0
    .prologue
    .line 814
    const/4 v0, -0x1

    #@1
    if-eq p2, v0, :cond_0

    #@3
    const/high16 v0, -0x10000

    #@5
    and-int/2addr v0, p2

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 815
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@a
    const-string v1, "Can only use lower 16 bits for requestCode"

    #@c
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@f
    throw v0

    #@10
    .line 817
    :cond_0
    invoke-super {p0, p1, p2}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent;I)V

    #@13
    .line 818
    return-void
.end method

.method public startActivityFromFragment(Landroid/support/v4/app/Fragment;Landroid/content/Intent;I)V
    .locals 2
    .param p1, "fragment"    # Landroid/support/v4/app/Fragment;
    .param p2, "intent"    # Landroid/content/Intent;
    .param p3, "requestCode"    # I

    #@0
    .prologue
    const/4 v0, -0x1

    #@1
    .line 825
    if-ne p3, v0, :cond_0

    #@3
    .line 826
    invoke-super {p0, p2, v0}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent;I)V

    #@6
    .line 833
    :goto_0
    return-void

    #@7
    .line 829
    :cond_0
    const/high16 v0, -0x10000

    #@9
    and-int/2addr v0, p3

    #@a
    if-eqz v0, :cond_1

    #@c
    .line 830
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@e
    const-string v1, "Can only use lower 16 bits for requestCode"

    #@10
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@13
    throw v0

    #@14
    .line 832
    :cond_1
    iget v0, p1, Landroid/support/v4/app/Fragment;->mIndex:I

    #@16
    add-int/lit8 v0, v0, 0x1

    #@18
    shl-int/lit8 v0, v0, 0x10

    #@1a
    const v1, 0xffff

    #@1d
    and-int/2addr v1, p3

    #@1e
    add-int/2addr v0, v1

    #@1f
    invoke-super {p0, p2, v0}, Landroid/app/Activity;->startActivityForResult(Landroid/content/Intent;I)V

    #@22
    goto :goto_0
.end method

.method public supportFinishAfterTransition()V
    .locals 0

    #@0
    .prologue
    .line 191
    invoke-static {p0}, Landroid/support/v4/app/ActivityCompat;->finishAfterTransition(Landroid/app/Activity;)V

    #@3
    .line 192
    return-void
.end method

.method public supportInvalidateOptionsMenu()V
    .locals 2

    #@0
    .prologue
    .line 623
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0xb

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 626
    invoke-static {p0}, Landroid/support/v4/app/ActivityCompatHoneycomb;->invalidateOptionsMenu(Landroid/app/Activity;)V

    #@9
    .line 633
    :goto_0
    return-void

    #@a
    .line 632
    :cond_0
    const/4 v0, 0x1

    #@b
    iput-boolean v0, p0, Landroid/support/v4/app/FragmentActivity;->mOptionsMenuInvalidated:Z

    #@d
    goto :goto_0
.end method

.method public supportPostponeEnterTransition()V
    .locals 0

    #@0
    .prologue
    .line 224
    invoke-static {p0}, Landroid/support/v4/app/ActivityCompat;->postponeEnterTransition(Landroid/app/Activity;)V

    #@3
    .line 225
    return-void
.end method

.method public supportStartPostponedEnterTransition()V
    .locals 0

    #@0
    .prologue
    .line 232
    invoke-static {p0}, Landroid/support/v4/app/ActivityCompat;->startPostponedEnterTransition(Landroid/app/Activity;)V

    #@3
    .line 233
    return-void
.end method
