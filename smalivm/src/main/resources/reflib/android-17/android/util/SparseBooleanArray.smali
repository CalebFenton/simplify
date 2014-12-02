.class public Landroid/util/SparseBooleanArray;
.super Ljava/lang/Object;
.source "SparseBooleanArray.java"

# interfaces
.implements Ljava/lang/Cloneable;


# instance fields
.field private mKeys:[I

.field private mSize:I

.field private mValues:[Z


# direct methods
.method public constructor <init>()V
    .registers 2

    .prologue
    .line 32
    const/16 v0, 0xa

    invoke-direct {p0, v0}, Landroid/util/SparseBooleanArray;-><init>(I)V

    .line 33
    return-void
.end method

.method public constructor <init>(I)V
    .registers 3
    .param p1, "initialCapacity"    # I

    .prologue
    .line 40
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 41
    invoke-static {p1}, Lcom/android/internal/util/ArrayUtils;->idealIntArraySize(I)I

    move-result p1

    .line 43
    new-array v0, p1, [I

    iput-object v0, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    .line 44
    new-array v0, p1, [Z

    iput-object v0, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    .line 45
    const/4 v0, 0x0

    iput v0, p0, Landroid/util/SparseBooleanArray;->mSize:I

    .line 46
    return-void
.end method

.method private static binarySearch([IIII)I
    .registers 9
    .param p0, "a"    # [I
    .param p1, "start"    # I
    .param p2, "len"    # I
    .param p3, "key"    # I

    .prologue
    .line 224
    add-int v1, p1, p2

    .local v1, "high":I
    add-int/lit8 v2, p1, -0x1

    .line 226
    .local v2, "low":I
    :goto_4
    sub-int v3, v1, v2

    const/4 v4, 0x1

    if-le v3, v4, :cond_15

    .line 227
    add-int v3, v1, v2

    div-int/lit8 v0, v3, 0x2

    .line 229
    .local v0, "guess":I
    aget v3, p0, v0

    if-ge v3, p3, :cond_13

    .line 230
    move v2, v0

    goto :goto_4

    .line 232
    :cond_13
    move v1, v0

    goto :goto_4

    .line 235
    .end local v0    # "guess":I
    :cond_15
    add-int v3, p1, p2

    if-ne v1, v3, :cond_1e

    .line 236
    add-int v3, p1, p2

    xor-int/lit8 v1, v3, -0x1

    .line 240
    .end local v1    # "high":I
    :cond_1d
    :goto_1d
    return v1

    .line 237
    .restart local v1    # "high":I
    :cond_1e
    aget v3, p0, v1

    if-eq v3, p3, :cond_1d

    .line 240
    xor-int/lit8 v1, v1, -0x1

    goto :goto_1d
.end method


# virtual methods
.method public append(IZ)V
    .registers 10
    .param p1, "key"    # I
    .param p2, "value"    # Z

    .prologue
    const/4 v6, 0x0

    .line 198
    iget v4, p0, Landroid/util/SparseBooleanArray;->mSize:I

    if-eqz v4, :cond_13

    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    iget v5, p0, Landroid/util/SparseBooleanArray;->mSize:I

    add-int/lit8 v5, v5, -0x1

    aget v4, v4, v5

    if-gt p1, v4, :cond_13

    .line 199
    invoke-virtual {p0, p1, p2}, Landroid/util/SparseBooleanArray;->put(IZ)V

    .line 221
    :goto_12
    return-void

    .line 203
    :cond_13
    iget v3, p0, Landroid/util/SparseBooleanArray;->mSize:I

    .line 204
    .local v3, "pos":I
    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    array-length v4, v4

    if-lt v3, v4, :cond_38

    .line 205
    add-int/lit8 v4, v3, 0x1

    invoke-static {v4}, Lcom/android/internal/util/ArrayUtils;->idealIntArraySize(I)I

    move-result v0

    .line 207
    .local v0, "n":I
    new-array v1, v0, [I

    .line 208
    .local v1, "nkeys":[I
    new-array v2, v0, [Z

    .line 211
    .local v2, "nvalues":[Z
    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    iget-object v5, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    array-length v5, v5

    invoke-static {v4, v6, v1, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 212
    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    iget-object v5, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    array-length v5, v5

    invoke-static {v4, v6, v2, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 214
    iput-object v1, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    .line 215
    iput-object v2, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    .line 218
    .end local v0    # "n":I
    .end local v1    # "nkeys":[I
    .end local v2    # "nvalues":[Z
    :cond_38
    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    aput p1, v4, v3

    .line 219
    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    aput-boolean p2, v4, v3

    .line 220
    add-int/lit8 v4, v3, 0x1

    iput v4, p0, Landroid/util/SparseBooleanArray;->mSize:I

    goto :goto_12
.end method

.method public clear()V
    .registers 2

    .prologue
    .line 190
    const/4 v0, 0x0

    iput v0, p0, Landroid/util/SparseBooleanArray;->mSize:I

    .line 191
    return-void
.end method

.method public clone()Landroid/util/SparseBooleanArray;
    .registers 4

    .prologue
    .line 50
    const/4 v1, 0x0

    .line 52
    .local v1, "clone":Landroid/util/SparseBooleanArray;
    :try_start_1
    invoke-super {p0}, Ljava/lang/Object;->clone()Ljava/lang/Object;

    move-result-object v2

    move-object v0, v2

    check-cast v0, Landroid/util/SparseBooleanArray;

    move-object v1, v0

    .line 53
    iget-object v2, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    invoke-virtual {v2}, [I->clone()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, [I

    iput-object v2, v1, Landroid/util/SparseBooleanArray;->mKeys:[I

    .line 54
    iget-object v2, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    invoke-virtual {v2}, [Z->clone()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, [Z

    iput-object v2, v1, Landroid/util/SparseBooleanArray;->mValues:[Z
    :try_end_1d
    .catch Ljava/lang/CloneNotSupportedException; {:try_start_1 .. :try_end_1d} :catch_1e

    .line 58
    :goto_1d
    return-object v1

    .line 55
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
    .line 27
    invoke-virtual {p0}, Landroid/util/SparseBooleanArray;->clone()Landroid/util/SparseBooleanArray;

    move-result-object v0

    return-object v0
.end method

.method public delete(I)V
    .registers 8
    .param p1, "key"    # I

    .prologue
    .line 87
    iget-object v1, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    const/4 v2, 0x0

    iget v3, p0, Landroid/util/SparseBooleanArray;->mSize:I

    invoke-static {v1, v2, v3, p1}, Landroid/util/SparseBooleanArray;->binarySearch([IIII)I

    move-result v0

    .line 89
    .local v0, "i":I
    if-ltz v0, :cond_2d

    .line 90
    iget-object v1, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    add-int/lit8 v2, v0, 0x1

    iget-object v3, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    iget v4, p0, Landroid/util/SparseBooleanArray;->mSize:I

    add-int/lit8 v5, v0, 0x1

    sub-int/2addr v4, v5

    invoke-static {v1, v2, v3, v0, v4}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 91
    iget-object v1, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    add-int/lit8 v2, v0, 0x1

    iget-object v3, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    iget v4, p0, Landroid/util/SparseBooleanArray;->mSize:I

    add-int/lit8 v5, v0, 0x1

    sub-int/2addr v4, v5

    invoke-static {v1, v2, v3, v0, v4}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 92
    iget v1, p0, Landroid/util/SparseBooleanArray;->mSize:I

    add-int/lit8 v1, v1, -0x1

    iput v1, p0, Landroid/util/SparseBooleanArray;->mSize:I

    .line 94
    :cond_2d
    return-void
.end method

.method public get(I)Z
    .registers 3
    .param p1, "key"    # I

    .prologue
    .line 66
    const/4 v0, 0x0

    invoke-virtual {p0, p1, v0}, Landroid/util/SparseBooleanArray;->get(IZ)Z

    move-result v0

    return v0
.end method

.method public get(IZ)Z
    .registers 7
    .param p1, "key"    # I
    .param p2, "valueIfKeyNotFound"    # Z

    .prologue
    .line 74
    iget-object v1, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    const/4 v2, 0x0

    iget v3, p0, Landroid/util/SparseBooleanArray;->mSize:I

    invoke-static {v1, v2, v3, p1}, Landroid/util/SparseBooleanArray;->binarySearch([IIII)I

    move-result v0

    .line 76
    .local v0, "i":I
    if-gez v0, :cond_c

    .line 79
    .end local p2    # "valueIfKeyNotFound":Z
    :goto_b
    return p2

    .restart local p2    # "valueIfKeyNotFound":Z
    :cond_c
    iget-object v1, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    aget-boolean p2, v1, v0

    goto :goto_b
.end method

.method public indexOfKey(I)I
    .registers 5
    .param p1, "key"    # I

    .prologue
    .line 167
    iget-object v0, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    const/4 v1, 0x0

    iget v2, p0, Landroid/util/SparseBooleanArray;->mSize:I

    invoke-static {v0, v1, v2, p1}, Landroid/util/SparseBooleanArray;->binarySearch([IIII)I

    move-result v0

    return v0
.end method

.method public indexOfValue(Z)I
    .registers 4
    .param p1, "value"    # Z

    .prologue
    .line 179
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_1
    iget v1, p0, Landroid/util/SparseBooleanArray;->mSize:I

    if-ge v0, v1, :cond_f

    .line 180
    iget-object v1, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    aget-boolean v1, v1, v0

    if-ne v1, p1, :cond_c

    .line 183
    .end local v0    # "i":I
    :goto_b
    return v0

    .line 179
    .restart local v0    # "i":I
    :cond_c
    add-int/lit8 v0, v0, 0x1

    goto :goto_1

    .line 183
    :cond_f
    const/4 v0, -0x1

    goto :goto_b
.end method

.method public keyAt(I)I
    .registers 3
    .param p1, "index"    # I

    .prologue
    .line 149
    iget-object v0, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    aget v0, v0, p1

    return v0
.end method

.method public put(IZ)V
    .registers 11
    .param p1, "key"    # I
    .param p2, "value"    # Z

    .prologue
    const/4 v6, 0x0

    .line 102
    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    iget v5, p0, Landroid/util/SparseBooleanArray;->mSize:I

    invoke-static {v4, v6, v5, p1}, Landroid/util/SparseBooleanArray;->binarySearch([IIII)I

    move-result v0

    .line 104
    .local v0, "i":I
    if-ltz v0, :cond_10

    .line 105
    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    aput-boolean p2, v4, v0

    .line 133
    :goto_f
    return-void

    .line 107
    :cond_10
    xor-int/lit8 v0, v0, -0x1

    .line 109
    iget v4, p0, Landroid/util/SparseBooleanArray;->mSize:I

    iget-object v5, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    array-length v5, v5

    if-lt v4, v5, :cond_39

    .line 110
    iget v4, p0, Landroid/util/SparseBooleanArray;->mSize:I

    add-int/lit8 v4, v4, 0x1

    invoke-static {v4}, Lcom/android/internal/util/ArrayUtils;->idealIntArraySize(I)I

    move-result v1

    .line 112
    .local v1, "n":I
    new-array v2, v1, [I

    .line 113
    .local v2, "nkeys":[I
    new-array v3, v1, [Z

    .line 116
    .local v3, "nvalues":[Z
    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    iget-object v5, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    array-length v5, v5

    invoke-static {v4, v6, v2, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 117
    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    iget-object v5, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    array-length v5, v5

    invoke-static {v4, v6, v3, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 119
    iput-object v2, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    .line 120
    iput-object v3, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    .line 123
    .end local v1    # "n":I
    .end local v2    # "nkeys":[I
    .end local v3    # "nvalues":[Z
    :cond_39
    iget v4, p0, Landroid/util/SparseBooleanArray;->mSize:I

    sub-int/2addr v4, v0

    if-eqz v4, :cond_56

    .line 125
    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    iget-object v5, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    add-int/lit8 v6, v0, 0x1

    iget v7, p0, Landroid/util/SparseBooleanArray;->mSize:I

    sub-int/2addr v7, v0

    invoke-static {v4, v0, v5, v6, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 126
    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    iget-object v5, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    add-int/lit8 v6, v0, 0x1

    iget v7, p0, Landroid/util/SparseBooleanArray;->mSize:I

    sub-int/2addr v7, v0

    invoke-static {v4, v0, v5, v6, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 129
    :cond_56
    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mKeys:[I

    aput p1, v4, v0

    .line 130
    iget-object v4, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    aput-boolean p2, v4, v0

    .line 131
    iget v4, p0, Landroid/util/SparseBooleanArray;->mSize:I

    add-int/lit8 v4, v4, 0x1

    iput v4, p0, Landroid/util/SparseBooleanArray;->mSize:I

    goto :goto_f
.end method

.method public size()I
    .registers 2

    .prologue
    .line 140
    iget v0, p0, Landroid/util/SparseBooleanArray;->mSize:I

    return v0
.end method

.method public valueAt(I)Z
    .registers 3
    .param p1, "index"    # I

    .prologue
    .line 158
    iget-object v0, p0, Landroid/util/SparseBooleanArray;->mValues:[Z

    aget-boolean v0, v0, p1

    return v0
.end method
