.class public Landroid/util/SparseIntArray;
.super Ljava/lang/Object;
.source "SparseIntArray.java"

# interfaces
.implements Ljava/lang/Cloneable;


# instance fields
.field private mKeys:[I

.field private mSize:I

.field private mValues:[I


# direct methods
.method public constructor <init>()V
    .registers 2

    .prologue
    .line 36
    const/16 v0, 0xa

    invoke-direct {p0, v0}, Landroid/util/SparseIntArray;-><init>(I)V

    .line 37
    return-void
.end method

.method public constructor <init>(I)V
    .registers 3
    .param p1, "initialCapacity"    # I

    .prologue
    .line 44
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 45
    invoke-static {p1}, Lcom/android/internal/util/ArrayUtils;->idealIntArraySize(I)I

    move-result p1

    .line 47
    new-array v0, p1, [I

    iput-object v0, p0, Landroid/util/SparseIntArray;->mKeys:[I

    .line 48
    new-array v0, p1, [I

    iput-object v0, p0, Landroid/util/SparseIntArray;->mValues:[I

    .line 49
    const/4 v0, 0x0

    iput v0, p0, Landroid/util/SparseIntArray;->mSize:I

    .line 50
    return-void
.end method

.method private static binarySearch([IIII)I
    .registers 9
    .param p0, "a"    # [I
    .param p1, "start"    # I
    .param p2, "len"    # I
    .param p3, "key"    # I

    .prologue
    .line 235
    add-int v1, p1, p2

    .local v1, "high":I
    add-int/lit8 v2, p1, -0x1

    .line 237
    .local v2, "low":I
    :goto_4
    sub-int v3, v1, v2

    const/4 v4, 0x1

    if-le v3, v4, :cond_15

    .line 238
    add-int v3, v1, v2

    div-int/lit8 v0, v3, 0x2

    .line 240
    .local v0, "guess":I
    aget v3, p0, v0

    if-ge v3, p3, :cond_13

    .line 241
    move v2, v0

    goto :goto_4

    .line 243
    :cond_13
    move v1, v0

    goto :goto_4

    .line 246
    .end local v0    # "guess":I
    :cond_15
    add-int v3, p1, p2

    if-ne v1, v3, :cond_1e

    .line 247
    add-int v3, p1, p2

    xor-int/lit8 v1, v3, -0x1

    .line 251
    .end local v1    # "high":I
    :cond_1d
    :goto_1d
    return v1

    .line 248
    .restart local v1    # "high":I
    :cond_1e
    aget v3, p0, v1

    if-eq v3, p3, :cond_1d

    .line 251
    xor-int/lit8 v1, v1, -0x1

    goto :goto_1d
.end method


# virtual methods
.method public append(II)V
    .registers 10
    .param p1, "key"    # I
    .param p2, "value"    # I

    .prologue
    const/4 v6, 0x0

    .line 209
    iget v4, p0, Landroid/util/SparseIntArray;->mSize:I

    if-eqz v4, :cond_13

    iget-object v4, p0, Landroid/util/SparseIntArray;->mKeys:[I

    iget v5, p0, Landroid/util/SparseIntArray;->mSize:I

    add-int/lit8 v5, v5, -0x1

    aget v4, v4, v5

    if-gt p1, v4, :cond_13

    .line 210
    invoke-virtual {p0, p1, p2}, Landroid/util/SparseIntArray;->put(II)V

    .line 232
    :goto_12
    return-void

    .line 214
    :cond_13
    iget v3, p0, Landroid/util/SparseIntArray;->mSize:I

    .line 215
    .local v3, "pos":I
    iget-object v4, p0, Landroid/util/SparseIntArray;->mKeys:[I

    array-length v4, v4

    if-lt v3, v4, :cond_38

    .line 216
    add-int/lit8 v4, v3, 0x1

    invoke-static {v4}, Lcom/android/internal/util/ArrayUtils;->idealIntArraySize(I)I

    move-result v0

    .line 218
    .local v0, "n":I
    new-array v1, v0, [I

    .line 219
    .local v1, "nkeys":[I
    new-array v2, v0, [I

    .line 222
    .local v2, "nvalues":[I
    iget-object v4, p0, Landroid/util/SparseIntArray;->mKeys:[I

    iget-object v5, p0, Landroid/util/SparseIntArray;->mKeys:[I

    array-length v5, v5

    invoke-static {v4, v6, v1, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 223
    iget-object v4, p0, Landroid/util/SparseIntArray;->mValues:[I

    iget-object v5, p0, Landroid/util/SparseIntArray;->mValues:[I

    array-length v5, v5

    invoke-static {v4, v6, v2, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 225
    iput-object v1, p0, Landroid/util/SparseIntArray;->mKeys:[I

    .line 226
    iput-object v2, p0, Landroid/util/SparseIntArray;->mValues:[I

    .line 229
    .end local v0    # "n":I
    .end local v1    # "nkeys":[I
    .end local v2    # "nvalues":[I
    :cond_38
    iget-object v4, p0, Landroid/util/SparseIntArray;->mKeys:[I

    aput p1, v4, v3

    .line 230
    iget-object v4, p0, Landroid/util/SparseIntArray;->mValues:[I

    aput p2, v4, v3

    .line 231
    add-int/lit8 v4, v3, 0x1

    iput v4, p0, Landroid/util/SparseIntArray;->mSize:I

    goto :goto_12
.end method

.method public clear()V
    .registers 2

    .prologue
    .line 201
    const/4 v0, 0x0

    iput v0, p0, Landroid/util/SparseIntArray;->mSize:I

    .line 202
    return-void
.end method

.method public clone()Landroid/util/SparseIntArray;
    .registers 4

    .prologue
    .line 54
    const/4 v1, 0x0

    .line 56
    .local v1, "clone":Landroid/util/SparseIntArray;
    :try_start_1
    invoke-super {p0}, Ljava/lang/Object;->clone()Ljava/lang/Object;

    move-result-object v2

    move-object v0, v2

    check-cast v0, Landroid/util/SparseIntArray;

    move-object v1, v0

    .line 57
    iget-object v2, p0, Landroid/util/SparseIntArray;->mKeys:[I

    invoke-virtual {v2}, [I->clone()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, [I

    iput-object v2, v1, Landroid/util/SparseIntArray;->mKeys:[I

    .line 58
    iget-object v2, p0, Landroid/util/SparseIntArray;->mValues:[I

    invoke-virtual {v2}, [I->clone()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, [I

    iput-object v2, v1, Landroid/util/SparseIntArray;->mValues:[I
    :try_end_1d
    .catch Ljava/lang/CloneNotSupportedException; {:try_start_1 .. :try_end_1d} :catch_1e

    .line 62
    :goto_1d
    return-object v1

    .line 59
    :catch_1e
    move-exception v2

    goto :goto_1d
.end method

.method public bridge synthetic clone()Ljava/lang/Object;
    .registers 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/CloneNotSupportedException;
        }
    .end annotation

    .prologue
    .line 26
    invoke-virtual {p0}, Landroid/util/SparseIntArray;->clone()Landroid/util/SparseIntArray;

    move-result-object v0

    return-object v0
.end method

.method public delete(I)V
    .registers 6
    .param p1, "key"    # I

    .prologue
    .line 91
    iget-object v1, p0, Landroid/util/SparseIntArray;->mKeys:[I

    const/4 v2, 0x0

    iget v3, p0, Landroid/util/SparseIntArray;->mSize:I

    invoke-static {v1, v2, v3, p1}, Landroid/util/SparseIntArray;->binarySearch([IIII)I

    move-result v0

    .line 93
    .local v0, "i":I
    if-ltz v0, :cond_e

    .line 94
    invoke-virtual {p0, v0}, Landroid/util/SparseIntArray;->removeAt(I)V

    .line 96
    :cond_e
    return-void
.end method

.method public get(I)I
    .registers 3
    .param p1, "key"    # I

    .prologue
    .line 70
    const/4 v0, 0x0

    invoke-virtual {p0, p1, v0}, Landroid/util/SparseIntArray;->get(II)I

    move-result v0

    return v0
.end method

.method public get(II)I
    .registers 7
    .param p1, "key"    # I
    .param p2, "valueIfKeyNotFound"    # I

    .prologue
    .line 78
    iget-object v1, p0, Landroid/util/SparseIntArray;->mKeys:[I

    const/4 v2, 0x0

    iget v3, p0, Landroid/util/SparseIntArray;->mSize:I

    invoke-static {v1, v2, v3, p1}, Landroid/util/SparseIntArray;->binarySearch([IIII)I

    move-result v0

    .line 80
    .local v0, "i":I
    if-gez v0, :cond_c

    .line 83
    .end local p2    # "valueIfKeyNotFound":I
    :goto_b
    return p2

    .restart local p2    # "valueIfKeyNotFound":I
    :cond_c
    iget-object v1, p0, Landroid/util/SparseIntArray;->mValues:[I

    aget p2, v1, v0

    goto :goto_b
.end method

.method public indexOfKey(I)I
    .registers 5
    .param p1, "key"    # I

    .prologue
    .line 178
    iget-object v0, p0, Landroid/util/SparseIntArray;->mKeys:[I

    const/4 v1, 0x0

    iget v2, p0, Landroid/util/SparseIntArray;->mSize:I

    invoke-static {v0, v1, v2, p1}, Landroid/util/SparseIntArray;->binarySearch([IIII)I

    move-result v0

    return v0
.end method

.method public indexOfValue(I)I
    .registers 4
    .param p1, "value"    # I

    .prologue
    .line 190
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_1
    iget v1, p0, Landroid/util/SparseIntArray;->mSize:I

    if-ge v0, v1, :cond_f

    .line 191
    iget-object v1, p0, Landroid/util/SparseIntArray;->mValues:[I

    aget v1, v1, v0

    if-ne v1, p1, :cond_c

    .line 194
    .end local v0    # "i":I
    :goto_b
    return v0

    .line 190
    .restart local v0    # "i":I
    :cond_c
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 194
    :cond_f
    const/4 v0, -0x1

    goto :goto_b
.end method

.method public keyAt(I)I
    .registers 3
    .param p1, "index"    # I

    .prologue
    .line 160
    iget-object v0, p0, Landroid/util/SparseIntArray;->mKeys:[I

    aget v0, v0, p1

    return v0
.end method

.method public put(II)V
    .registers 11
    .param p1, "key"    # I
    .param p2, "value"    # I

    .prologue
    const/4 v6, 0x0

    .line 113
    iget-object v4, p0, Landroid/util/SparseIntArray;->mKeys:[I

    iget v5, p0, Landroid/util/SparseIntArray;->mSize:I

    invoke-static {v4, v6, v5, p1}, Landroid/util/SparseIntArray;->binarySearch([IIII)I

    move-result v0

    .line 115
    .local v0, "i":I
    if-ltz v0, :cond_10

    .line 116
    iget-object v4, p0, Landroid/util/SparseIntArray;->mValues:[I

    aput p2, v4, v0

    .line 144
    :goto_f
    return-void

    .line 118
    :cond_10
    xor-int/lit8 v0, v0, -0x1

    .line 120
    iget v4, p0, Landroid/util/SparseIntArray;->mSize:I

    iget-object v5, p0, Landroid/util/SparseIntArray;->mKeys:[I

    array-length v5, v5

    if-lt v4, v5, :cond_39

    .line 121
    iget v4, p0, Landroid/util/SparseIntArray;->mSize:I

    add-int/lit8 v4, v4, 0x1

    invoke-static {v4}, Lcom/android/internal/util/ArrayUtils;->idealIntArraySize(I)I

    move-result v1

    .line 123
    .local v1, "n":I
    new-array v2, v1, [I

    .line 124
    .local v2, "nkeys":[I
    new-array v3, v1, [I

    .line 127
    .local v3, "nvalues":[I
    iget-object v4, p0, Landroid/util/SparseIntArray;->mKeys:[I

    iget-object v5, p0, Landroid/util/SparseIntArray;->mKeys:[I

    array-length v5, v5

    invoke-static {v4, v6, v2, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 128
    iget-object v4, p0, Landroid/util/SparseIntArray;->mValues:[I

    iget-object v5, p0, Landroid/util/SparseIntArray;->mValues:[I

    array-length v5, v5

    invoke-static {v4, v6, v3, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 130
    iput-object v2, p0, Landroid/util/SparseIntArray;->mKeys:[I

    .line 131
    iput-object v3, p0, Landroid/util/SparseIntArray;->mValues:[I

    .line 134
    .end local v1    # "n":I
    .end local v2    # "nkeys":[I
    .end local v3    # "nvalues":[I
    :cond_39
    iget v4, p0, Landroid/util/SparseIntArray;->mSize:I

    sub-int/2addr v4, v0

    if-eqz v4, :cond_56

    .line 136
    iget-object v4, p0, Landroid/util/SparseIntArray;->mKeys:[I

    iget-object v5, p0, Landroid/util/SparseIntArray;->mKeys:[I

    add-int/lit8 v6, v0, 0x1

    iget v7, p0, Landroid/util/SparseIntArray;->mSize:I

    sub-int/2addr v7, v0

    invoke-static {v4, v0, v5, v6, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 137
    iget-object v4, p0, Landroid/util/SparseIntArray;->mValues:[I

    iget-object v5, p0, Landroid/util/SparseIntArray;->mValues:[I

    add-int/lit8 v6, v0, 0x1

    iget v7, p0, Landroid/util/SparseIntArray;->mSize:I

    sub-int/2addr v7, v0

    invoke-static {v4, v0, v5, v6, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 140
    :cond_56
    iget-object v4, p0, Landroid/util/SparseIntArray;->mKeys:[I

    aput p1, v4, v0

    .line 141
    iget-object v4, p0, Landroid/util/SparseIntArray;->mValues:[I

    aput p2, v4, v0

    .line 142
    iget v4, p0, Landroid/util/SparseIntArray;->mSize:I

    add-int/lit8 v4, v4, 0x1

    iput v4, p0, Landroid/util/SparseIntArray;->mSize:I

    goto :goto_f
.end method

.method public removeAt(I)V
    .registers 7
    .param p1, "index"    # I

    .prologue
    .line 102
    iget-object v0, p0, Landroid/util/SparseIntArray;->mKeys:[I

    add-int/lit8 v1, p1, 0x1

    iget-object v2, p0, Landroid/util/SparseIntArray;->mKeys:[I

    iget v3, p0, Landroid/util/SparseIntArray;->mSize:I

    add-int/lit8 v4, p1, 0x1

    sub-int/2addr v3, v4

    invoke-static {v0, v1, v2, p1, v3}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 103
    iget-object v0, p0, Landroid/util/SparseIntArray;->mValues:[I

    add-int/lit8 v1, p1, 0x1

    iget-object v2, p0, Landroid/util/SparseIntArray;->mValues:[I

    iget v3, p0, Landroid/util/SparseIntArray;->mSize:I

    add-int/lit8 v4, p1, 0x1

    sub-int/2addr v3, v4

    invoke-static {v0, v1, v2, p1, v3}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 104
    iget v0, p0, Landroid/util/SparseIntArray;->mSize:I

    add-int/lit8 v0, v0, -0x1

    iput v0, p0, Landroid/util/SparseIntArray;->mSize:I

    .line 105
    return-void
.end method

.method public size()I
    .registers 2

    .prologue
    .line 151
    iget v0, p0, Landroid/util/SparseIntArray;->mSize:I

    return v0
.end method

.method public valueAt(I)I
    .registers 3
    .param p1, "index"    # I

    .prologue
    .line 169
    iget-object v0, p0, Landroid/util/SparseIntArray;->mValues:[I

    aget v0, v0, p1

    return v0
.end method
