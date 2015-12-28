.class final Landroid/support/v4/app/BackStackState;
.super Ljava/lang/Object;
.source "BackStackRecord.java"

# interfaces
.implements Landroid/os/Parcelable;


# static fields
.field public static final CREATOR:Landroid/os/Parcelable$Creator;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/os/Parcelable$Creator",
            "<",
            "Landroid/support/v4/app/BackStackState;",
            ">;"
        }
    .end annotation
.end field


# instance fields
.field final mBreadCrumbShortTitleRes:I

.field final mBreadCrumbShortTitleText:Ljava/lang/CharSequence;

.field final mBreadCrumbTitleRes:I

.field final mBreadCrumbTitleText:Ljava/lang/CharSequence;

.field final mIndex:I

.field final mName:Ljava/lang/String;

.field final mOps:[I

.field final mSharedElementSourceNames:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field final mSharedElementTargetNames:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field final mTransition:I

.field final mTransitionStyle:I


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 176
    new-instance v0, Landroid/support/v4/app/BackStackState$1;

    #@2
    invoke-direct {v0}, Landroid/support/v4/app/BackStackState$1;-><init>()V

    #@5
    sput-object v0, Landroid/support/v4/app/BackStackState;->CREATOR:Landroid/os/Parcelable$Creator;

    #@7
    return-void
.end method

.method public constructor <init>(Landroid/os/Parcel;)V
    .locals 1
    .param p1, "in"    # Landroid/os/Parcel;

    #@0
    .prologue
    .line 96
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 97
    invoke-virtual {p1}, Landroid/os/Parcel;->createIntArray()[I

    #@6
    move-result-object v0

    #@7
    iput-object v0, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@9
    .line 98
    invoke-virtual {p1}, Landroid/os/Parcel;->readInt()I

    #@c
    move-result v0

    #@d
    iput v0, p0, Landroid/support/v4/app/BackStackState;->mTransition:I

    #@f
    .line 99
    invoke-virtual {p1}, Landroid/os/Parcel;->readInt()I

    #@12
    move-result v0

    #@13
    iput v0, p0, Landroid/support/v4/app/BackStackState;->mTransitionStyle:I

    #@15
    .line 100
    invoke-virtual {p1}, Landroid/os/Parcel;->readString()Ljava/lang/String;

    #@18
    move-result-object v0

    #@19
    iput-object v0, p0, Landroid/support/v4/app/BackStackState;->mName:Ljava/lang/String;

    #@1b
    .line 101
    invoke-virtual {p1}, Landroid/os/Parcel;->readInt()I

    #@1e
    move-result v0

    #@1f
    iput v0, p0, Landroid/support/v4/app/BackStackState;->mIndex:I

    #@21
    .line 102
    invoke-virtual {p1}, Landroid/os/Parcel;->readInt()I

    #@24
    move-result v0

    #@25
    iput v0, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbTitleRes:I

    #@27
    .line 103
    sget-object v0, Landroid/text/TextUtils;->CHAR_SEQUENCE_CREATOR:Landroid/os/Parcelable$Creator;

    #@29
    invoke-interface {v0, p1}, Landroid/os/Parcelable$Creator;->createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object;

    #@2c
    move-result-object v0

    #@2d
    check-cast v0, Ljava/lang/CharSequence;

    #@2f
    iput-object v0, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbTitleText:Ljava/lang/CharSequence;

    #@31
    .line 104
    invoke-virtual {p1}, Landroid/os/Parcel;->readInt()I

    #@34
    move-result v0

    #@35
    iput v0, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbShortTitleRes:I

    #@37
    .line 105
    sget-object v0, Landroid/text/TextUtils;->CHAR_SEQUENCE_CREATOR:Landroid/os/Parcelable$Creator;

    #@39
    invoke-interface {v0, p1}, Landroid/os/Parcelable$Creator;->createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object;

    #@3c
    move-result-object v0

    #@3d
    check-cast v0, Ljava/lang/CharSequence;

    #@3f
    iput-object v0, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbShortTitleText:Ljava/lang/CharSequence;

    #@41
    .line 106
    invoke-virtual {p1}, Landroid/os/Parcel;->createStringArrayList()Ljava/util/ArrayList;

    #@44
    move-result-object v0

    #@45
    iput-object v0, p0, Landroid/support/v4/app/BackStackState;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@47
    .line 107
    invoke-virtual {p1}, Landroid/os/Parcel;->createStringArrayList()Ljava/util/ArrayList;

    #@4a
    move-result-object v0

    #@4b
    iput-object v0, p0, Landroid/support/v4/app/BackStackState;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@4d
    .line 108
    return-void
.end method

.method public constructor <init>(Landroid/support/v4/app/FragmentManagerImpl;Landroid/support/v4/app/BackStackRecord;)V
    .locals 8
    .param p1, "fm"    # Landroid/support/v4/app/FragmentManagerImpl;
    .param p2, "bse"    # Landroid/support/v4/app/BackStackRecord;

    #@0
    .prologue
    .line 51
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 52
    const/4 v2, 0x0

    #@4
    .line 53
    .local v2, "numRemoved":I
    iget-object v3, p2, Landroid/support/v4/app/BackStackRecord;->mHead:Landroid/support/v4/app/BackStackRecord$Op;

    #@6
    .line 54
    .local v3, "op":Landroid/support/v4/app/BackStackRecord$Op;
    :goto_0
    if-eqz v3, :cond_1

    #@8
    .line 55
    iget-object v6, v3, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@a
    if-eqz v6, :cond_0

    #@c
    iget-object v6, v3, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@e
    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    #@11
    move-result v6

    #@12
    add-int/2addr v2, v6

    #@13
    .line 56
    :cond_0
    iget-object v3, v3, Landroid/support/v4/app/BackStackRecord$Op;->next:Landroid/support/v4/app/BackStackRecord$Op;

    #@15
    goto :goto_0

    #@16
    .line 58
    :cond_1
    iget v6, p2, Landroid/support/v4/app/BackStackRecord;->mNumOp:I

    #@18
    mul-int/lit8 v6, v6, 0x7

    #@1a
    add-int/2addr v6, v2

    #@1b
    new-array v6, v6, [I

    #@1d
    iput-object v6, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@1f
    .line 60
    iget-boolean v6, p2, Landroid/support/v4/app/BackStackRecord;->mAddToBackStack:Z

    #@21
    if-nez v6, :cond_2

    #@23
    .line 61
    new-instance v6, Ljava/lang/IllegalStateException;

    #@25
    const-string v7, "Not on back stack"

    #@27
    invoke-direct {v6, v7}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@2a
    throw v6

    #@2b
    .line 64
    :cond_2
    iget-object v3, p2, Landroid/support/v4/app/BackStackRecord;->mHead:Landroid/support/v4/app/BackStackRecord$Op;

    #@2d
    .line 65
    const/4 v4, 0x0

    #@2e
    .local v4, "pos":I
    move v5, v4

    #@2f
    .line 66
    .end local v4    # "pos":I
    .local v5, "pos":I
    :goto_1
    if-eqz v3, :cond_6

    #@31
    .line 67
    iget-object v6, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@33
    add-int/lit8 v4, v5, 0x1

    #@35
    .end local v5    # "pos":I
    .restart local v4    # "pos":I
    iget v7, v3, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@37
    aput v7, v6, v5

    #@39
    .line 68
    iget-object v7, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@3b
    add-int/lit8 v5, v4, 0x1

    #@3d
    .end local v4    # "pos":I
    .restart local v5    # "pos":I
    iget-object v6, v3, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@3f
    if-eqz v6, :cond_3

    #@41
    iget-object v6, v3, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@43
    iget v6, v6, Landroid/support/v4/app/Fragment;->mIndex:I

    #@45
    :goto_2
    aput v6, v7, v4

    #@47
    .line 69
    iget-object v6, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@49
    add-int/lit8 v4, v5, 0x1

    #@4b
    .end local v5    # "pos":I
    .restart local v4    # "pos":I
    iget v7, v3, Landroid/support/v4/app/BackStackRecord$Op;->enterAnim:I

    #@4d
    aput v7, v6, v5

    #@4f
    .line 70
    iget-object v6, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@51
    add-int/lit8 v5, v4, 0x1

    #@53
    .end local v4    # "pos":I
    .restart local v5    # "pos":I
    iget v7, v3, Landroid/support/v4/app/BackStackRecord$Op;->exitAnim:I

    #@55
    aput v7, v6, v4

    #@57
    .line 71
    iget-object v6, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@59
    add-int/lit8 v4, v5, 0x1

    #@5b
    .end local v5    # "pos":I
    .restart local v4    # "pos":I
    iget v7, v3, Landroid/support/v4/app/BackStackRecord$Op;->popEnterAnim:I

    #@5d
    aput v7, v6, v5

    #@5f
    .line 72
    iget-object v6, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@61
    add-int/lit8 v5, v4, 0x1

    #@63
    .end local v4    # "pos":I
    .restart local v5    # "pos":I
    iget v7, v3, Landroid/support/v4/app/BackStackRecord$Op;->popExitAnim:I

    #@65
    aput v7, v6, v4

    #@67
    .line 73
    iget-object v6, v3, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@69
    if-eqz v6, :cond_5

    #@6b
    .line 74
    iget-object v6, v3, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@6d
    invoke-virtual {v6}, Ljava/util/ArrayList;->size()I

    #@70
    move-result v0

    #@71
    .line 75
    .local v0, "N":I
    iget-object v6, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@73
    add-int/lit8 v4, v5, 0x1

    #@75
    .end local v5    # "pos":I
    .restart local v4    # "pos":I
    aput v0, v6, v5

    #@77
    .line 76
    const/4 v1, 0x0

    #@78
    .local v1, "i":I
    move v5, v4

    #@79
    .end local v4    # "pos":I
    .restart local v5    # "pos":I
    :goto_3
    if-ge v1, v0, :cond_4

    #@7b
    .line 77
    iget-object v7, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@7d
    add-int/lit8 v4, v5, 0x1

    #@7f
    .end local v5    # "pos":I
    .restart local v4    # "pos":I
    iget-object v6, v3, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@81
    invoke-virtual {v6, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@84
    move-result-object v6

    #@85
    check-cast v6, Landroid/support/v4/app/Fragment;

    #@87
    iget v6, v6, Landroid/support/v4/app/Fragment;->mIndex:I

    #@89
    aput v6, v7, v5

    #@8b
    .line 76
    add-int/lit8 v1, v1, 0x1

    #@8d
    move v5, v4

    #@8e
    .end local v4    # "pos":I
    .restart local v5    # "pos":I
    goto :goto_3

    #@8f
    .line 68
    .end local v0    # "N":I
    .end local v1    # "i":I
    :cond_3
    const/4 v6, -0x1

    #@90
    goto :goto_2

    #@91
    .restart local v0    # "N":I
    .restart local v1    # "i":I
    :cond_4
    move v4, v5

    #@92
    .line 82
    .end local v0    # "N":I
    .end local v1    # "i":I
    .end local v5    # "pos":I
    .restart local v4    # "pos":I
    :goto_4
    iget-object v3, v3, Landroid/support/v4/app/BackStackRecord$Op;->next:Landroid/support/v4/app/BackStackRecord$Op;

    #@94
    move v5, v4

    #@95
    .end local v4    # "pos":I
    .restart local v5    # "pos":I
    goto :goto_1

    #@96
    .line 80
    :cond_5
    iget-object v6, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@98
    add-int/lit8 v4, v5, 0x1

    #@9a
    .end local v5    # "pos":I
    .restart local v4    # "pos":I
    const/4 v7, 0x0

    #@9b
    aput v7, v6, v5

    #@9d
    goto :goto_4

    #@9e
    .line 84
    .end local v4    # "pos":I
    .restart local v5    # "pos":I
    :cond_6
    iget v6, p2, Landroid/support/v4/app/BackStackRecord;->mTransition:I

    #@a0
    iput v6, p0, Landroid/support/v4/app/BackStackState;->mTransition:I

    #@a2
    .line 85
    iget v6, p2, Landroid/support/v4/app/BackStackRecord;->mTransitionStyle:I

    #@a4
    iput v6, p0, Landroid/support/v4/app/BackStackState;->mTransitionStyle:I

    #@a6
    .line 86
    iget-object v6, p2, Landroid/support/v4/app/BackStackRecord;->mName:Ljava/lang/String;

    #@a8
    iput-object v6, p0, Landroid/support/v4/app/BackStackState;->mName:Ljava/lang/String;

    #@aa
    .line 87
    iget v6, p2, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@ac
    iput v6, p0, Landroid/support/v4/app/BackStackState;->mIndex:I

    #@ae
    .line 88
    iget v6, p2, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleRes:I

    #@b0
    iput v6, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbTitleRes:I

    #@b2
    .line 89
    iget-object v6, p2, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleText:Ljava/lang/CharSequence;

    #@b4
    iput-object v6, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbTitleText:Ljava/lang/CharSequence;

    #@b6
    .line 90
    iget v6, p2, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleRes:I

    #@b8
    iput v6, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbShortTitleRes:I

    #@ba
    .line 91
    iget-object v6, p2, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleText:Ljava/lang/CharSequence;

    #@bc
    iput-object v6, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbShortTitleText:Ljava/lang/CharSequence;

    #@be
    .line 92
    iget-object v6, p2, Landroid/support/v4/app/BackStackRecord;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@c0
    iput-object v6, p0, Landroid/support/v4/app/BackStackState;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@c2
    .line 93
    iget-object v6, p2, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@c4
    iput-object v6, p0, Landroid/support/v4/app/BackStackState;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@c6
    .line 94
    return-void
.end method


# virtual methods
.method public describeContents()I
    .locals 1

    #@0
    .prologue
    .line 159
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public instantiate(Landroid/support/v4/app/FragmentManagerImpl;)Landroid/support/v4/app/BackStackRecord;
    .locals 14
    .param p1, "fm"    # Landroid/support/v4/app/FragmentManagerImpl;

    #@0
    .prologue
    const/4 v13, 0x1

    #@1
    .line 111
    new-instance v1, Landroid/support/v4/app/BackStackRecord;

    #@3
    invoke-direct {v1, p1}, Landroid/support/v4/app/BackStackRecord;-><init>(Landroid/support/v4/app/FragmentManagerImpl;)V

    #@6
    .line 112
    .local v1, "bse":Landroid/support/v4/app/BackStackRecord;
    const/4 v7, 0x0

    #@7
    .line 113
    .local v7, "pos":I
    const/4 v5, 0x0

    #@8
    .line 114
    .local v5, "num":I
    :goto_0
    iget-object v10, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@a
    array-length v10, v10

    #@b
    if-ge v7, v10, :cond_4

    #@d
    .line 115
    new-instance v6, Landroid/support/v4/app/BackStackRecord$Op;

    #@f
    invoke-direct {v6}, Landroid/support/v4/app/BackStackRecord$Op;-><init>()V

    #@12
    .line 116
    .local v6, "op":Landroid/support/v4/app/BackStackRecord$Op;
    iget-object v10, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@14
    add-int/lit8 v8, v7, 0x1

    #@16
    .end local v7    # "pos":I
    .local v8, "pos":I
    aget v10, v10, v7

    #@18
    iput v10, v6, Landroid/support/v4/app/BackStackRecord$Op;->cmd:I

    #@1a
    .line 117
    sget-boolean v10, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@1c
    if-eqz v10, :cond_0

    #@1e
    const-string v10, "FragmentManager"

    #@20
    new-instance v11, Ljava/lang/StringBuilder;

    #@22
    invoke-direct {v11}, Ljava/lang/StringBuilder;-><init>()V

    #@25
    const-string v12, "Instantiate "

    #@27
    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2a
    move-result-object v11

    #@2b
    invoke-virtual {v11, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@2e
    move-result-object v11

    #@2f
    const-string v12, " op #"

    #@31
    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@34
    move-result-object v11

    #@35
    invoke-virtual {v11, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@38
    move-result-object v11

    #@39
    const-string v12, " base fragment #"

    #@3b
    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@3e
    move-result-object v11

    #@3f
    iget-object v12, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@41
    aget v12, v12, v8

    #@43
    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@46
    move-result-object v11

    #@47
    invoke-virtual {v11}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@4a
    move-result-object v11

    #@4b
    invoke-static {v10, v11}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@4e
    .line 119
    :cond_0
    iget-object v10, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@50
    add-int/lit8 v7, v8, 0x1

    #@52
    .end local v8    # "pos":I
    .restart local v7    # "pos":I
    aget v3, v10, v8

    #@54
    .line 120
    .local v3, "findex":I
    if-ltz v3, :cond_2

    #@56
    .line 121
    iget-object v10, p1, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@58
    invoke-virtual {v10, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@5b
    move-result-object v2

    #@5c
    check-cast v2, Landroid/support/v4/app/Fragment;

    #@5e
    .line 122
    .local v2, "f":Landroid/support/v4/app/Fragment;
    iput-object v2, v6, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@60
    .line 126
    .end local v2    # "f":Landroid/support/v4/app/Fragment;
    :goto_1
    iget-object v10, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@62
    add-int/lit8 v8, v7, 0x1

    #@64
    .end local v7    # "pos":I
    .restart local v8    # "pos":I
    aget v10, v10, v7

    #@66
    iput v10, v6, Landroid/support/v4/app/BackStackRecord$Op;->enterAnim:I

    #@68
    .line 127
    iget-object v10, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@6a
    add-int/lit8 v7, v8, 0x1

    #@6c
    .end local v8    # "pos":I
    .restart local v7    # "pos":I
    aget v10, v10, v8

    #@6e
    iput v10, v6, Landroid/support/v4/app/BackStackRecord$Op;->exitAnim:I

    #@70
    .line 128
    iget-object v10, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@72
    add-int/lit8 v8, v7, 0x1

    #@74
    .end local v7    # "pos":I
    .restart local v8    # "pos":I
    aget v10, v10, v7

    #@76
    iput v10, v6, Landroid/support/v4/app/BackStackRecord$Op;->popEnterAnim:I

    #@78
    .line 129
    iget-object v10, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@7a
    add-int/lit8 v7, v8, 0x1

    #@7c
    .end local v8    # "pos":I
    .restart local v7    # "pos":I
    aget v10, v10, v8

    #@7e
    iput v10, v6, Landroid/support/v4/app/BackStackRecord$Op;->popExitAnim:I

    #@80
    .line 130
    iget-object v10, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@82
    add-int/lit8 v8, v7, 0x1

    #@84
    .end local v7    # "pos":I
    .restart local v8    # "pos":I
    aget v0, v10, v7

    #@86
    .line 131
    .local v0, "N":I
    if-lez v0, :cond_3

    #@88
    .line 132
    new-instance v10, Ljava/util/ArrayList;

    #@8a
    invoke-direct {v10, v0}, Ljava/util/ArrayList;-><init>(I)V

    #@8d
    iput-object v10, v6, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@8f
    .line 133
    const/4 v4, 0x0

    #@90
    .local v4, "i":I
    :goto_2
    if-ge v4, v0, :cond_3

    #@92
    .line 134
    sget-boolean v10, Landroid/support/v4/app/FragmentManagerImpl;->DEBUG:Z

    #@94
    if-eqz v10, :cond_1

    #@96
    const-string v10, "FragmentManager"

    #@98
    new-instance v11, Ljava/lang/StringBuilder;

    #@9a
    invoke-direct {v11}, Ljava/lang/StringBuilder;-><init>()V

    #@9d
    const-string v12, "Instantiate "

    #@9f
    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@a2
    move-result-object v11

    #@a3
    invoke-virtual {v11, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@a6
    move-result-object v11

    #@a7
    const-string v12, " set remove fragment #"

    #@a9
    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@ac
    move-result-object v11

    #@ad
    iget-object v12, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@af
    aget v12, v12, v8

    #@b1
    invoke-virtual {v11, v12}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@b4
    move-result-object v11

    #@b5
    invoke-virtual {v11}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@b8
    move-result-object v11

    #@b9
    invoke-static {v10, v11}, Landroid/util/Log;->v(Ljava/lang/String;Ljava/lang/String;)I

    #@bc
    .line 136
    :cond_1
    iget-object v10, p1, Landroid/support/v4/app/FragmentManagerImpl;->mActive:Ljava/util/ArrayList;

    #@be
    iget-object v11, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@c0
    add-int/lit8 v7, v8, 0x1

    #@c2
    .end local v8    # "pos":I
    .restart local v7    # "pos":I
    aget v11, v11, v8

    #@c4
    invoke-virtual {v10, v11}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@c7
    move-result-object v9

    #@c8
    check-cast v9, Landroid/support/v4/app/Fragment;

    #@ca
    .line 137
    .local v9, "r":Landroid/support/v4/app/Fragment;
    iget-object v10, v6, Landroid/support/v4/app/BackStackRecord$Op;->removed:Ljava/util/ArrayList;

    #@cc
    invoke-virtual {v10, v9}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@cf
    .line 133
    add-int/lit8 v4, v4, 0x1

    #@d1
    move v8, v7

    #@d2
    .end local v7    # "pos":I
    .restart local v8    # "pos":I
    goto :goto_2

    #@d3
    .line 124
    .end local v0    # "N":I
    .end local v4    # "i":I
    .end local v8    # "pos":I
    .end local v9    # "r":Landroid/support/v4/app/Fragment;
    .restart local v7    # "pos":I
    :cond_2
    const/4 v10, 0x0

    #@d4
    iput-object v10, v6, Landroid/support/v4/app/BackStackRecord$Op;->fragment:Landroid/support/v4/app/Fragment;

    #@d6
    goto :goto_1

    #@d7
    .end local v7    # "pos":I
    .restart local v0    # "N":I
    .restart local v8    # "pos":I
    :cond_3
    move v7, v8

    #@d8
    .line 140
    .end local v8    # "pos":I
    .restart local v7    # "pos":I
    invoke-virtual {v1, v6}, Landroid/support/v4/app/BackStackRecord;->addOp(Landroid/support/v4/app/BackStackRecord$Op;)V

    #@db
    .line 141
    add-int/lit8 v5, v5, 0x1

    #@dd
    .line 142
    goto/16 :goto_0

    #@df
    .line 143
    .end local v0    # "N":I
    .end local v3    # "findex":I
    .end local v6    # "op":Landroid/support/v4/app/BackStackRecord$Op;
    :cond_4
    iget v10, p0, Landroid/support/v4/app/BackStackState;->mTransition:I

    #@e1
    iput v10, v1, Landroid/support/v4/app/BackStackRecord;->mTransition:I

    #@e3
    .line 144
    iget v10, p0, Landroid/support/v4/app/BackStackState;->mTransitionStyle:I

    #@e5
    iput v10, v1, Landroid/support/v4/app/BackStackRecord;->mTransitionStyle:I

    #@e7
    .line 145
    iget-object v10, p0, Landroid/support/v4/app/BackStackState;->mName:Ljava/lang/String;

    #@e9
    iput-object v10, v1, Landroid/support/v4/app/BackStackRecord;->mName:Ljava/lang/String;

    #@eb
    .line 146
    iget v10, p0, Landroid/support/v4/app/BackStackState;->mIndex:I

    #@ed
    iput v10, v1, Landroid/support/v4/app/BackStackRecord;->mIndex:I

    #@ef
    .line 147
    iput-boolean v13, v1, Landroid/support/v4/app/BackStackRecord;->mAddToBackStack:Z

    #@f1
    .line 148
    iget v10, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbTitleRes:I

    #@f3
    iput v10, v1, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleRes:I

    #@f5
    .line 149
    iget-object v10, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbTitleText:Ljava/lang/CharSequence;

    #@f7
    iput-object v10, v1, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbTitleText:Ljava/lang/CharSequence;

    #@f9
    .line 150
    iget v10, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbShortTitleRes:I

    #@fb
    iput v10, v1, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleRes:I

    #@fd
    .line 151
    iget-object v10, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbShortTitleText:Ljava/lang/CharSequence;

    #@ff
    iput-object v10, v1, Landroid/support/v4/app/BackStackRecord;->mBreadCrumbShortTitleText:Ljava/lang/CharSequence;

    #@101
    .line 152
    iget-object v10, p0, Landroid/support/v4/app/BackStackState;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@103
    iput-object v10, v1, Landroid/support/v4/app/BackStackRecord;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@105
    .line 153
    iget-object v10, p0, Landroid/support/v4/app/BackStackState;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@107
    iput-object v10, v1, Landroid/support/v4/app/BackStackRecord;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@109
    .line 154
    invoke-virtual {v1, v13}, Landroid/support/v4/app/BackStackRecord;->bumpBackStackNesting(I)V

    #@10c
    .line 155
    return-object v1
.end method

.method public writeToParcel(Landroid/os/Parcel;I)V
    .locals 2
    .param p1, "dest"    # Landroid/os/Parcel;
    .param p2, "flags"    # I

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 163
    iget-object v0, p0, Landroid/support/v4/app/BackStackState;->mOps:[I

    #@3
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeIntArray([I)V

    #@6
    .line 164
    iget v0, p0, Landroid/support/v4/app/BackStackState;->mTransition:I

    #@8
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeInt(I)V

    #@b
    .line 165
    iget v0, p0, Landroid/support/v4/app/BackStackState;->mTransitionStyle:I

    #@d
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeInt(I)V

    #@10
    .line 166
    iget-object v0, p0, Landroid/support/v4/app/BackStackState;->mName:Ljava/lang/String;

    #@12
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeString(Ljava/lang/String;)V

    #@15
    .line 167
    iget v0, p0, Landroid/support/v4/app/BackStackState;->mIndex:I

    #@17
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeInt(I)V

    #@1a
    .line 168
    iget v0, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbTitleRes:I

    #@1c
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeInt(I)V

    #@1f
    .line 169
    iget-object v0, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbTitleText:Ljava/lang/CharSequence;

    #@21
    invoke-static {v0, p1, v1}, Landroid/text/TextUtils;->writeToParcel(Ljava/lang/CharSequence;Landroid/os/Parcel;I)V

    #@24
    .line 170
    iget v0, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbShortTitleRes:I

    #@26
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeInt(I)V

    #@29
    .line 171
    iget-object v0, p0, Landroid/support/v4/app/BackStackState;->mBreadCrumbShortTitleText:Ljava/lang/CharSequence;

    #@2b
    invoke-static {v0, p1, v1}, Landroid/text/TextUtils;->writeToParcel(Ljava/lang/CharSequence;Landroid/os/Parcel;I)V

    #@2e
    .line 172
    iget-object v0, p0, Landroid/support/v4/app/BackStackState;->mSharedElementSourceNames:Ljava/util/ArrayList;

    #@30
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeStringList(Ljava/util/List;)V

    #@33
    .line 173
    iget-object v0, p0, Landroid/support/v4/app/BackStackState;->mSharedElementTargetNames:Ljava/util/ArrayList;

    #@35
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeStringList(Ljava/util/List;)V

    #@38
    .line 174
    return-void
.end method
