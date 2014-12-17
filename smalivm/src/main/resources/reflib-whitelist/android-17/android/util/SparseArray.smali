.class public Landroid/util/SparseArray;
.super Ljava/lang/Object;
.source "SparseArray.java"

# interfaces
.implements Ljava/lang/Cloneable;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<E:",
        "Ljava/lang/Object;",
        ">",
        "Ljava/lang/Object;",
        "Ljava/lang/Cloneable;"
    }
.end annotation


# static fields
.field private static final DELETED:Ljava/lang/Object;


# instance fields
.field private mGarbage:Z

.field private mKeys:[I

.field private mSize:I

.field private mValues:[Ljava/lang/Object;


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    .line 27
    new-instance v0, Ljava/lang/Object;

    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    sput-object v0, Landroid/util/SparseArray;->DELETED:Ljava/lang/Object;

    return-void
.end method

.method public constructor <init>()V
    .registers 2

    .prologue
    .line 38
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    const/16 v0, 0xa

    invoke-direct {p0, v0}, Landroid/util/SparseArray;-><init>(I)V

    .line 39
    return-void
.end method

.method public constructor <init>(I)V
    .registers 4
    .param p1, "initialCapacity"    # I

    .prologue
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    const/4 v1, 0x0

    .line 46
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 28
    iput-boolean v1, p0, Landroid/util/SparseArray;->mGarbage:Z

    .line 47
    invoke-static {p1}, Lcom/android/internal/util/ArrayUtils;->idealIntArraySize(I)I

    move-result p1

    .line 49
    new-array v0, p1, [I

    iput-object v0, p0, Landroid/util/SparseArray;->mKeys:[I

    .line 50
    new-array v0, p1, [Ljava/lang/Object;

    iput-object v0, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    .line 51
    iput v1, p0, Landroid/util/SparseArray;->mSize:I

    .line 52
    return-void
.end method

.method private static binarySearch([IIII)I
    .registers 9
    .param p0, "a"    # [I
    .param p1, "start"    # I
    .param p2, "len"    # I
    .param p3, "key"    # I

    .prologue
    .line 337
    add-int v1, p1, p2

    .local v1, "high":I
    add-int/lit8 v2, p1, -0x1

    .line 339
    .local v2, "low":I
    :goto_4
    sub-int v3, v1, v2

    const/4 v4, 0x1

    if-le v3, v4, :cond_15

    .line 340
    add-int v3, v1, v2

    div-int/lit8 v0, v3, 0x2

    .line 342
    .local v0, "guess":I
    aget v3, p0, v0

    if-ge v3, p3, :cond_13

    .line 343
    move v2, v0

    goto :goto_4

    .line 345
    :cond_13
    move v1, v0

    goto :goto_4

    .line 348
    .end local v0    # "guess":I
    :cond_15
    add-int v3, p1, p2

    if-ne v1, v3, :cond_1e

    .line 349
    add-int v3, p1, p2

    xor-int/lit8 v1, v3, -0x1

    .line 353
    .end local v1    # "high":I
    :cond_1d
    :goto_1d
    return v1

    .line 350
    .restart local v1    # "high":I
    :cond_1e
    aget v3, p0, v1

    if-eq v3, p3, :cond_1d

    .line 353
    xor-int/lit8 v1, v1, -0x1

    goto :goto_1d
.end method

.method private gc()V
    .registers 8

    .prologue
    .line 125
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    iget v2, p0, Landroid/util/SparseArray;->mSize:I

    .line 126
    .local v2, "n":I
    const/4 v3, 0x0

    .line 127
    .local v3, "o":I
    iget-object v1, p0, Landroid/util/SparseArray;->mKeys:[I

    .line 128
    .local v1, "keys":[I
    iget-object v5, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    .line 130
    .local v5, "values":[Ljava/lang/Object;
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_8
    if-ge v0, v2, :cond_20

    .line 131
    aget-object v4, v5, v0

    .line 133
    .local v4, "val":Ljava/lang/Object;
    sget-object v6, Landroid/util/SparseArray;->DELETED:Ljava/lang/Object;

    if-eq v4, v6, :cond_1d

    .line 134
    if-eq v0, v3, :cond_1b

    .line 135
    aget v6, v1, v0

    aput v6, v1, v3

    .line 136
    aput-object v4, v5, v3

    .line 137
    const/4 v6, 0x0

    aput-object v6, v5, v0

    .line 140
    :cond_1b
    add-int/lit8 v3, v3, 0x1

    .line 130
    :cond_1d
    add-int/lit8 v0, v0, 0x1

    goto :goto_8

    .line 144
    .end local v4    # "val":Ljava/lang/Object;
    :cond_20
    const/4 v6, 0x0

    iput-boolean v6, p0, Landroid/util/SparseArray;->mGarbage:Z

    .line 145
    iput v3, p0, Landroid/util/SparseArray;->mSize:I

    .line 148
    return-void
.end method


# virtual methods
.method public append(ILjava/lang/Object;)V
    .registers 10
    .param p1, "key"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ITE;)V"
        }
    .end annotation

    .prologue
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    .local p2, "value":Ljava/lang/Object;, "TE;"
    const/4 v6, 0x0

    .line 307
    iget v4, p0, Landroid/util/SparseArray;->mSize:I

    if-eqz v4, :cond_13

    iget-object v4, p0, Landroid/util/SparseArray;->mKeys:[I

    iget v5, p0, Landroid/util/SparseArray;->mSize:I

    add-int/lit8 v5, v5, -0x1

    aget v4, v4, v5

    if-gt p1, v4, :cond_13

    .line 308
    invoke-virtual {p0, p1, p2}, Landroid/util/SparseArray;->put(ILjava/lang/Object;)V

    .line 334
    :goto_12
    return-void

    .line 312
    :cond_13
    iget-boolean v4, p0, Landroid/util/SparseArray;->mGarbage:Z

    if-eqz v4, :cond_21

    iget v4, p0, Landroid/util/SparseArray;->mSize:I

    iget-object v5, p0, Landroid/util/SparseArray;->mKeys:[I

    array-length v5, v5

    if-lt v4, v5, :cond_21

    .line 313
    invoke-direct {p0}, Landroid/util/SparseArray;->gc()V

    .line 316
    :cond_21
    iget v3, p0, Landroid/util/SparseArray;->mSize:I

    .line 317
    .local v3, "pos":I
    iget-object v4, p0, Landroid/util/SparseArray;->mKeys:[I

    array-length v4, v4

    if-lt v3, v4, :cond_46

    .line 318
    add-int/lit8 v4, v3, 0x1

    invoke-static {v4}, Lcom/android/internal/util/ArrayUtils;->idealIntArraySize(I)I

    move-result v0

    .line 320
    .local v0, "n":I
    new-array v1, v0, [I

    .line 321
    .local v1, "nkeys":[I
    new-array v2, v0, [Ljava/lang/Object;

    .line 324
    .local v2, "nvalues":[Ljava/lang/Object;
    iget-object v4, p0, Landroid/util/SparseArray;->mKeys:[I

    iget-object v5, p0, Landroid/util/SparseArray;->mKeys:[I

    array-length v5, v5

    invoke-static {v4, v6, v1, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 325
    iget-object v4, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    iget-object v5, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    array-length v5, v5

    invoke-static {v4, v6, v2, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 327
    iput-object v1, p0, Landroid/util/SparseArray;->mKeys:[I

    .line 328
    iput-object v2, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    .line 331
    .end local v0    # "n":I
    .end local v1    # "nkeys":[I
    .end local v2    # "nvalues":[Ljava/lang/Object;
    :cond_46
    iget-object v4, p0, Landroid/util/SparseArray;->mKeys:[I

    aput p1, v4, v3

    .line 332
    iget-object v4, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    aput-object p2, v4, v3

    .line 333
    add-int/lit8 v4, v3, 0x1

    iput v4, p0, Landroid/util/SparseArray;->mSize:I

    goto :goto_12
.end method

.method public clear()V
    .registers 6

    .prologue
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    const/4 v4, 0x0

    .line 291
    iget v1, p0, Landroid/util/SparseArray;->mSize:I

    .line 292
    .local v1, "n":I
    iget-object v2, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    .line 294
    .local v2, "values":[Ljava/lang/Object;
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_6
    if-ge v0, v1, :cond_e

    .line 295
    const/4 v3, 0x0

    aput-object v3, v2, v0

    .line 294
    add-int/lit8 v0, v0, 0x1

    goto :goto_6

    .line 298
    :cond_e
    iput v4, p0, Landroid/util/SparseArray;->mSize:I

    .line 299
    iput-boolean v4, p0, Landroid/util/SparseArray;->mGarbage:Z

    .line 300
    return-void
.end method

.method public clone()Landroid/util/SparseArray;
    .registers 4
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Landroid/util/SparseArray",
            "<TE;>;"
        }
    .end annotation

    .prologue
    .line 57
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    const/4 v1, 0x0

    .line 59
    .local v1, "clone":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    :try_start_1
    invoke-super {p0}, Ljava/lang/Object;->clone()Ljava/lang/Object;

    move-result-object v2

    move-object v0, v2

    check-cast v0, Landroid/util/SparseArray;

    move-object v1, v0

    .line 60
    iget-object v2, p0, Landroid/util/SparseArray;->mKeys:[I

    invoke-virtual {v2}, [I->clone()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, [I

    iput-object v2, v1, Landroid/util/SparseArray;->mKeys:[I

    .line 61
    iget-object v2, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    invoke-virtual {v2}, [Ljava/lang/Object;->clone()Ljava/lang/Object;

    move-result-object v2

    check-cast v2, [Ljava/lang/Object;

    iput-object v2, v1, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;
    :try_end_1d
    .catch Ljava/lang/CloneNotSupportedException; {:try_start_1 .. :try_end_1d} :catch_1e

    .line 65
    :goto_1d
    return-object v1

    .line 62
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
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    invoke-virtual {p0}, Landroid/util/SparseArray;->clone()Landroid/util/SparseArray;

    move-result-object v0

    return-object v0
.end method

.method public delete(I)V
    .registers 6
    .param p1, "key"    # I

    .prologue
    .line 95
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    iget-object v1, p0, Landroid/util/SparseArray;->mKeys:[I

    const/4 v2, 0x0

    iget v3, p0, Landroid/util/SparseArray;->mSize:I

    invoke-static {v1, v2, v3, p1}, Landroid/util/SparseArray;->binarySearch([IIII)I

    move-result v0

    .line 97
    .local v0, "i":I
    if-ltz v0, :cond_1c

    .line 98
    iget-object v1, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    aget-object v1, v1, v0

    sget-object v2, Landroid/util/SparseArray;->DELETED:Ljava/lang/Object;

    if-eq v1, v2, :cond_1c

    .line 99
    iget-object v1, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    sget-object v2, Landroid/util/SparseArray;->DELETED:Ljava/lang/Object;

    aput-object v2, v1, v0

    .line 100
    const/4 v1, 0x1

    iput-boolean v1, p0, Landroid/util/SparseArray;->mGarbage:Z

    .line 103
    :cond_1c
    return-void
.end method

.method public get(I)Ljava/lang/Object;
    .registers 3
    .param p1, "key"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I)TE;"
        }
    .end annotation

    .prologue
    .line 73
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    const/4 v0, 0x0

    invoke-virtual {p0, p1, v0}, Landroid/util/SparseArray;->get(ILjava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    return-object v0
.end method

.method public get(ILjava/lang/Object;)Ljava/lang/Object;
    .registers 7
    .param p1, "key"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ITE;)TE;"
        }
    .end annotation

    .prologue
    .line 82
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    .local p2, "valueIfKeyNotFound":Ljava/lang/Object;, "TE;"
    iget-object v1, p0, Landroid/util/SparseArray;->mKeys:[I

    const/4 v2, 0x0

    iget v3, p0, Landroid/util/SparseArray;->mSize:I

    invoke-static {v1, v2, v3, p1}, Landroid/util/SparseArray;->binarySearch([IIII)I

    move-result v0

    .line 84
    .local v0, "i":I
    if-ltz v0, :cond_13

    iget-object v1, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    aget-object v1, v1, v0

    sget-object v2, Landroid/util/SparseArray;->DELETED:Ljava/lang/Object;

    if-ne v1, v2, :cond_14

    .line 87
    .end local p2    # "valueIfKeyNotFound":Ljava/lang/Object;, "TE;"
    :cond_13
    :goto_13
    return-object p2

    .restart local p2    # "valueIfKeyNotFound":Ljava/lang/Object;, "TE;"
    :cond_14
    iget-object v1, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    aget-object p2, v1, v0

    goto :goto_13
.end method

.method public indexOfKey(I)I
    .registers 5
    .param p1, "key"    # I

    .prologue
    .line 260
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    iget-boolean v0, p0, Landroid/util/SparseArray;->mGarbage:Z

    if-eqz v0, :cond_7

    .line 261
    invoke-direct {p0}, Landroid/util/SparseArray;->gc()V

    .line 264
    :cond_7
    iget-object v0, p0, Landroid/util/SparseArray;->mKeys:[I

    const/4 v1, 0x0

    iget v2, p0, Landroid/util/SparseArray;->mSize:I

    invoke-static {v0, v1, v2, p1}, Landroid/util/SparseArray;->binarySearch([IIII)I

    move-result v0

    return v0
.end method

.method public indexOfValue(Ljava/lang/Object;)I
    .registers 4
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TE;)I"
        }
    .end annotation

    .prologue
    .line 276
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    .local p1, "value":Ljava/lang/Object;, "TE;"
    iget-boolean v1, p0, Landroid/util/SparseArray;->mGarbage:Z

    if-eqz v1, :cond_7

    .line 277
    invoke-direct {p0}, Landroid/util/SparseArray;->gc()V

    .line 280
    :cond_7
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_8
    iget v1, p0, Landroid/util/SparseArray;->mSize:I

    if-ge v0, v1, :cond_16

    .line 281
    iget-object v1, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    aget-object v1, v1, v0

    if-ne v1, p1, :cond_13

    .line 284
    .end local v0    # "i":I
    :goto_12
    return v0

    .line 280
    .restart local v0    # "i":I
    :cond_13
    add-int/lit8 v0, v0, 0x1

    goto :goto_8

    .line 284
    :cond_16
    const/4 v0, -0x1

    goto :goto_12
.end method

.method public keyAt(I)I
    .registers 3
    .param p1, "index"    # I

    .prologue
    .line 220
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    iget-boolean v0, p0, Landroid/util/SparseArray;->mGarbage:Z

    if-eqz v0, :cond_7

    .line 221
    invoke-direct {p0}, Landroid/util/SparseArray;->gc()V

    .line 224
    :cond_7
    iget-object v0, p0, Landroid/util/SparseArray;->mKeys:[I

    aget v0, v0, p1

    return v0
.end method

.method public put(ILjava/lang/Object;)V
    .registers 11
    .param p1, "key"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ITE;)V"
        }
    .end annotation

    .prologue
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    .local p2, "value":Ljava/lang/Object;, "TE;"
    const/4 v6, 0x0

    .line 156
    iget-object v4, p0, Landroid/util/SparseArray;->mKeys:[I

    iget v5, p0, Landroid/util/SparseArray;->mSize:I

    invoke-static {v4, v6, v5, p1}, Landroid/util/SparseArray;->binarySearch([IIII)I

    move-result v0

    .line 158
    .local v0, "i":I
    if-ltz v0, :cond_10

    .line 159
    iget-object v4, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    aput-object p2, v4, v0

    .line 200
    :goto_f
    return-void

    .line 161
    :cond_10
    xor-int/lit8 v0, v0, -0x1

    .line 163
    iget v4, p0, Landroid/util/SparseArray;->mSize:I

    if-ge v0, v4, :cond_27

    iget-object v4, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    aget-object v4, v4, v0

    sget-object v5, Landroid/util/SparseArray;->DELETED:Ljava/lang/Object;

    if-ne v4, v5, :cond_27

    .line 164
    iget-object v4, p0, Landroid/util/SparseArray;->mKeys:[I

    aput p1, v4, v0

    .line 165
    iget-object v4, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    aput-object p2, v4, v0

    goto :goto_f

    .line 169
    :cond_27
    iget-boolean v4, p0, Landroid/util/SparseArray;->mGarbage:Z

    if-eqz v4, :cond_3f

    iget v4, p0, Landroid/util/SparseArray;->mSize:I

    iget-object v5, p0, Landroid/util/SparseArray;->mKeys:[I

    array-length v5, v5

    if-lt v4, v5, :cond_3f

    .line 170
    invoke-direct {p0}, Landroid/util/SparseArray;->gc()V

    .line 173
    iget-object v4, p0, Landroid/util/SparseArray;->mKeys:[I

    iget v5, p0, Landroid/util/SparseArray;->mSize:I

    invoke-static {v4, v6, v5, p1}, Landroid/util/SparseArray;->binarySearch([IIII)I

    move-result v4

    xor-int/lit8 v0, v4, -0x1

    .line 176
    :cond_3f
    iget v4, p0, Landroid/util/SparseArray;->mSize:I

    iget-object v5, p0, Landroid/util/SparseArray;->mKeys:[I

    array-length v5, v5

    if-lt v4, v5, :cond_66

    .line 177
    iget v4, p0, Landroid/util/SparseArray;->mSize:I

    add-int/lit8 v4, v4, 0x1

    invoke-static {v4}, Lcom/android/internal/util/ArrayUtils;->idealIntArraySize(I)I

    move-result v1

    .line 179
    .local v1, "n":I
    new-array v2, v1, [I

    .line 180
    .local v2, "nkeys":[I
    new-array v3, v1, [Ljava/lang/Object;

    .line 183
    .local v3, "nvalues":[Ljava/lang/Object;
    iget-object v4, p0, Landroid/util/SparseArray;->mKeys:[I

    iget-object v5, p0, Landroid/util/SparseArray;->mKeys:[I

    array-length v5, v5

    invoke-static {v4, v6, v2, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 184
    iget-object v4, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    iget-object v5, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    array-length v5, v5

    invoke-static {v4, v6, v3, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 186
    iput-object v2, p0, Landroid/util/SparseArray;->mKeys:[I

    .line 187
    iput-object v3, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    .line 190
    .end local v1    # "n":I
    .end local v2    # "nkeys":[I
    .end local v3    # "nvalues":[Ljava/lang/Object;
    :cond_66
    iget v4, p0, Landroid/util/SparseArray;->mSize:I

    sub-int/2addr v4, v0

    if-eqz v4, :cond_83

    .line 192
    iget-object v4, p0, Landroid/util/SparseArray;->mKeys:[I

    iget-object v5, p0, Landroid/util/SparseArray;->mKeys:[I

    add-int/lit8 v6, v0, 0x1

    iget v7, p0, Landroid/util/SparseArray;->mSize:I

    sub-int/2addr v7, v0

    invoke-static {v4, v0, v5, v6, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 193
    iget-object v4, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    iget-object v5, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    add-int/lit8 v6, v0, 0x1

    iget v7, p0, Landroid/util/SparseArray;->mSize:I

    sub-int/2addr v7, v0

    invoke-static {v4, v0, v5, v6, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 196
    :cond_83
    iget-object v4, p0, Landroid/util/SparseArray;->mKeys:[I

    aput p1, v4, v0

    .line 197
    iget-object v4, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    aput-object p2, v4, v0

    .line 198
    iget v4, p0, Landroid/util/SparseArray;->mSize:I

    add-int/lit8 v4, v4, 0x1

    iput v4, p0, Landroid/util/SparseArray;->mSize:I

    goto/16 :goto_f
.end method

.method public remove(I)V
    .registers 2
    .param p1, "key"    # I

    .prologue
    .line 109
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    invoke-virtual {p0, p1}, Landroid/util/SparseArray;->delete(I)V

    .line 110
    return-void
.end method

.method public removeAt(I)V
    .registers 4
    .param p1, "index"    # I

    .prologue
    .line 116
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    iget-object v0, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    aget-object v0, v0, p1

    sget-object v1, Landroid/util/SparseArray;->DELETED:Ljava/lang/Object;

    if-eq v0, v1, :cond_11

    .line 117
    iget-object v0, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    sget-object v1, Landroid/util/SparseArray;->DELETED:Ljava/lang/Object;

    aput-object v1, v0, p1

    .line 118
    const/4 v0, 0x1

    iput-boolean v0, p0, Landroid/util/SparseArray;->mGarbage:Z

    .line 120
    :cond_11
    return-void
.end method

.method public setValueAt(ILjava/lang/Object;)V
    .registers 4
    .param p1, "index"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ITE;)V"
        }
    .end annotation

    .prologue
    .line 247
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    .local p2, "value":Ljava/lang/Object;, "TE;"
    iget-boolean v0, p0, Landroid/util/SparseArray;->mGarbage:Z

    if-eqz v0, :cond_7

    .line 248
    invoke-direct {p0}, Landroid/util/SparseArray;->gc()V

    .line 251
    :cond_7
    iget-object v0, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    aput-object p2, v0, p1

    .line 252
    return-void
.end method

.method public size()I
    .registers 2

    .prologue
    .line 207
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    iget-boolean v0, p0, Landroid/util/SparseArray;->mGarbage:Z

    if-eqz v0, :cond_7

    .line 208
    invoke-direct {p0}, Landroid/util/SparseArray;->gc()V

    .line 211
    :cond_7
    iget v0, p0, Landroid/util/SparseArray;->mSize:I

    return v0
.end method

.method public valueAt(I)Ljava/lang/Object;
    .registers 3
    .param p1, "index"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I)TE;"
        }
    .end annotation

    .prologue
    .line 234
    .local p0, "this":Landroid/util/SparseArray;, "Landroid/util/SparseArray<TE;>;"
    iget-boolean v0, p0, Landroid/util/SparseArray;->mGarbage:Z

    if-eqz v0, :cond_7

    .line 235
    invoke-direct {p0}, Landroid/util/SparseArray;->gc()V

    .line 238
    :cond_7
    iget-object v0, p0, Landroid/util/SparseArray;->mValues:[Ljava/lang/Object;

    aget-object v0, v0, p1

    return-object v0
.end method
