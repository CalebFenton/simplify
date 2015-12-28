.class public Landroid/support/v4/util/CircularArray;
.super Ljava/lang/Object;
.source "CircularArray.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<E:",
        "Ljava/lang/Object;",
        ">",
        "Ljava/lang/Object;"
    }
.end annotation


# instance fields
.field private mCapacityBitmask:I

.field private mElements:[Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "[TE;"
        }
    .end annotation
.end field

.field private mHead:I

.field private mTail:I


# direct methods
.method public constructor <init>()V
    .locals 1

    #@0
    .prologue
    .line 47
    .local p0, "this":Landroid/support/v4/util/CircularArray;, "Landroid/support/v4/util/CircularArray<TE;>;"
    const/16 v0, 0x8

    #@2
    invoke-direct {p0, v0}, Landroid/support/v4/util/CircularArray;-><init>(I)V

    #@5
    .line 48
    return-void
.end method

.method public constructor <init>(I)V
    .locals 3
    .param p1, "minCapacity"    # I

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/CircularArray;, "Landroid/support/v4/util/CircularArray<TE;>;"
    const/4 v2, 0x1

    #@1
    .line 55
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@4
    .line 56
    if-gtz p1, :cond_0

    #@6
    .line 57
    new-instance v1, Ljava/lang/IllegalArgumentException;

    #@8
    const-string v2, "capacity must be positive"

    #@a
    invoke-direct {v1, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@d
    throw v1

    #@e
    .line 59
    :cond_0
    move v0, p1

    #@f
    .line 62
    .local v0, "arrayCapacity":I
    invoke-static {p1}, Ljava/lang/Integer;->bitCount(I)I

    #@12
    move-result v1

    #@13
    if-eq v1, v2, :cond_1

    #@15
    .line 63
    invoke-static {p1}, Ljava/lang/Integer;->highestOneBit(I)I

    #@18
    move-result v1

    #@19
    add-int/lit8 v1, v1, 0x1

    #@1b
    shl-int v0, v2, v1

    #@1d
    .line 65
    :cond_1
    add-int/lit8 v1, v0, -0x1

    #@1f
    iput v1, p0, Landroid/support/v4/util/CircularArray;->mCapacityBitmask:I

    #@21
    .line 66
    new-array v1, v0, [Ljava/lang/Object;

    #@23
    check-cast v1, [Ljava/lang/Object;

    #@25
    iput-object v1, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@27
    .line 67
    return-void
.end method

.method private doubleCapacity()V
    .locals 7

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/CircularArray;, "Landroid/support/v4/util/CircularArray<TE;>;"
    const/4 v6, 0x0

    #@1
    .line 28
    iget-object v4, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@3
    array-length v1, v4

    #@4
    .line 29
    .local v1, "n":I
    iget v4, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@6
    sub-int v3, v1, v4

    #@8
    .line 30
    .local v3, "r":I
    shl-int/lit8 v2, v1, 0x1

    #@a
    .line 31
    .local v2, "newCapacity":I
    if-gez v2, :cond_0

    #@c
    .line 32
    new-instance v4, Ljava/lang/RuntimeException;

    #@e
    const-string v5, "Too big"

    #@10
    invoke-direct {v4, v5}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    #@13
    throw v4

    #@14
    .line 34
    :cond_0
    new-array v0, v2, [Ljava/lang/Object;

    #@16
    .line 35
    .local v0, "a":[Ljava/lang/Object;
    iget-object v4, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@18
    iget v5, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@1a
    invoke-static {v4, v5, v0, v6, v3}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@1d
    .line 36
    iget-object v4, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@1f
    iget v5, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@21
    invoke-static {v4, v6, v0, v3, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@24
    .line 37
    check-cast v0, [Ljava/lang/Object;

    #@26
    .end local v0    # "a":[Ljava/lang/Object;
    iput-object v0, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@28
    .line 38
    iput v6, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@2a
    .line 39
    iput v1, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@2c
    .line 40
    add-int/lit8 v4, v2, -0x1

    #@2e
    iput v4, p0, Landroid/support/v4/util/CircularArray;->mCapacityBitmask:I

    #@30
    .line 41
    return-void
.end method


# virtual methods
.method public final addFirst(Ljava/lang/Object;)V
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TE;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 70
    .local p0, "this":Landroid/support/v4/util/CircularArray;, "Landroid/support/v4/util/CircularArray<TE;>;"
    .local p1, "e":Ljava/lang/Object;, "TE;"
    iget v0, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@2
    add-int/lit8 v0, v0, -0x1

    #@4
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mCapacityBitmask:I

    #@6
    and-int/2addr v0, v1

    #@7
    iput v0, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@9
    .line 71
    iget-object v0, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@b
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@d
    aput-object p1, v0, v1

    #@f
    .line 72
    iget v0, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@11
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@13
    if-ne v0, v1, :cond_0

    #@15
    .line 73
    invoke-direct {p0}, Landroid/support/v4/util/CircularArray;->doubleCapacity()V

    #@18
    .line 75
    :cond_0
    return-void
.end method

.method public final addLast(Ljava/lang/Object;)V
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TE;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 78
    .local p0, "this":Landroid/support/v4/util/CircularArray;, "Landroid/support/v4/util/CircularArray<TE;>;"
    .local p1, "e":Ljava/lang/Object;, "TE;"
    iget-object v0, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@2
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@4
    aput-object p1, v0, v1

    #@6
    .line 79
    iget v0, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@8
    add-int/lit8 v0, v0, 0x1

    #@a
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mCapacityBitmask:I

    #@c
    and-int/2addr v0, v1

    #@d
    iput v0, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@f
    .line 80
    iget v0, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@11
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@13
    if-ne v0, v1, :cond_0

    #@15
    .line 81
    invoke-direct {p0}, Landroid/support/v4/util/CircularArray;->doubleCapacity()V

    #@18
    .line 83
    :cond_0
    return-void
.end method

.method public final get(I)Ljava/lang/Object;
    .locals 3
    .param p1, "i"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I)TE;"
        }
    .end annotation

    #@0
    .prologue
    .line 113
    .local p0, "this":Landroid/support/v4/util/CircularArray;, "Landroid/support/v4/util/CircularArray<TE;>;"
    if-ltz p1, :cond_0

    #@2
    invoke-virtual {p0}, Landroid/support/v4/util/CircularArray;->size()I

    #@5
    move-result v1

    #@6
    if-lt p1, v1, :cond_1

    #@8
    :cond_0
    new-instance v1, Ljava/lang/ArrayIndexOutOfBoundsException;

    #@a
    invoke-direct {v1}, Ljava/lang/ArrayIndexOutOfBoundsException;-><init>()V

    #@d
    throw v1

    #@e
    .line 114
    :cond_1
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@10
    add-int/2addr v1, p1

    #@11
    iget v2, p0, Landroid/support/v4/util/CircularArray;->mCapacityBitmask:I

    #@13
    and-int v0, v1, v2

    #@15
    .line 115
    .local v0, "p":I
    iget-object v1, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@17
    aget-object v1, v1, v0

    #@19
    return-object v1
.end method

.method public final getFirst()Ljava/lang/Object;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TE;"
        }
    .end annotation

    #@0
    .prologue
    .line 103
    .local p0, "this":Landroid/support/v4/util/CircularArray;, "Landroid/support/v4/util/CircularArray<TE;>;"
    iget v0, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@2
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@4
    if-ne v0, v1, :cond_0

    #@6
    new-instance v0, Ljava/lang/ArrayIndexOutOfBoundsException;

    #@8
    invoke-direct {v0}, Ljava/lang/ArrayIndexOutOfBoundsException;-><init>()V

    #@b
    throw v0

    #@c
    .line 104
    :cond_0
    iget-object v0, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@e
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@10
    aget-object v0, v0, v1

    #@12
    return-object v0
.end method

.method public final getLast()Ljava/lang/Object;
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TE;"
        }
    .end annotation

    #@0
    .prologue
    .line 108
    .local p0, "this":Landroid/support/v4/util/CircularArray;, "Landroid/support/v4/util/CircularArray<TE;>;"
    iget v0, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@2
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@4
    if-ne v0, v1, :cond_0

    #@6
    new-instance v0, Ljava/lang/ArrayIndexOutOfBoundsException;

    #@8
    invoke-direct {v0}, Ljava/lang/ArrayIndexOutOfBoundsException;-><init>()V

    #@b
    throw v0

    #@c
    .line 109
    :cond_0
    iget-object v0, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@e
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@10
    add-int/lit8 v1, v1, -0x1

    #@12
    iget v2, p0, Landroid/support/v4/util/CircularArray;->mCapacityBitmask:I

    #@14
    and-int/2addr v1, v2

    #@15
    aget-object v0, v0, v1

    #@17
    return-object v0
.end method

.method public final isEmpty()Z
    .locals 2

    #@0
    .prologue
    .line 123
    .local p0, "this":Landroid/support/v4/util/CircularArray;, "Landroid/support/v4/util/CircularArray<TE;>;"
    iget v0, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@2
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@4
    if-ne v0, v1, :cond_0

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

.method public final popFirst()Ljava/lang/Object;
    .locals 4
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TE;"
        }
    .end annotation

    #@0
    .prologue
    .line 86
    .local p0, "this":Landroid/support/v4/util/CircularArray;, "Landroid/support/v4/util/CircularArray<TE;>;"
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@2
    iget v2, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@4
    if-ne v1, v2, :cond_0

    #@6
    new-instance v1, Ljava/lang/ArrayIndexOutOfBoundsException;

    #@8
    invoke-direct {v1}, Ljava/lang/ArrayIndexOutOfBoundsException;-><init>()V

    #@b
    throw v1

    #@c
    .line 87
    :cond_0
    iget-object v1, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@e
    iget v2, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@10
    aget-object v0, v1, v2

    #@12
    .line 88
    .local v0, "result":Ljava/lang/Object;, "TE;"
    iget-object v1, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@14
    iget v2, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@16
    const/4 v3, 0x0

    #@17
    aput-object v3, v1, v2

    #@19
    .line 89
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@1b
    add-int/lit8 v1, v1, 0x1

    #@1d
    iget v2, p0, Landroid/support/v4/util/CircularArray;->mCapacityBitmask:I

    #@1f
    and-int/2addr v1, v2

    #@20
    iput v1, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@22
    .line 90
    return-object v0
.end method

.method public final popLast()Ljava/lang/Object;
    .locals 4
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TE;"
        }
    .end annotation

    #@0
    .prologue
    .line 94
    .local p0, "this":Landroid/support/v4/util/CircularArray;, "Landroid/support/v4/util/CircularArray<TE;>;"
    iget v2, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@2
    iget v3, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@4
    if-ne v2, v3, :cond_0

    #@6
    new-instance v2, Ljava/lang/ArrayIndexOutOfBoundsException;

    #@8
    invoke-direct {v2}, Ljava/lang/ArrayIndexOutOfBoundsException;-><init>()V

    #@b
    throw v2

    #@c
    .line 95
    :cond_0
    iget v2, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@e
    add-int/lit8 v2, v2, -0x1

    #@10
    iget v3, p0, Landroid/support/v4/util/CircularArray;->mCapacityBitmask:I

    #@12
    and-int v1, v2, v3

    #@14
    .line 96
    .local v1, "t":I
    iget-object v2, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@16
    aget-object v0, v2, v1

    #@18
    .line 97
    .local v0, "result":Ljava/lang/Object;, "TE;"
    iget-object v2, p0, Landroid/support/v4/util/CircularArray;->mElements:[Ljava/lang/Object;

    #@1a
    const/4 v3, 0x0

    #@1b
    aput-object v3, v2, v1

    #@1d
    .line 98
    iput v1, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@1f
    .line 99
    return-object v0
.end method

.method public final size()I
    .locals 2

    #@0
    .prologue
    .line 119
    .local p0, "this":Landroid/support/v4/util/CircularArray;, "Landroid/support/v4/util/CircularArray<TE;>;"
    iget v0, p0, Landroid/support/v4/util/CircularArray;->mTail:I

    #@2
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mHead:I

    #@4
    sub-int/2addr v0, v1

    #@5
    iget v1, p0, Landroid/support/v4/util/CircularArray;->mCapacityBitmask:I

    #@7
    and-int/2addr v0, v1

    #@8
    return v0
.end method
