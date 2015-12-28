.class public Landroid/support/v4/util/SparseArrayCompat;
.super Ljava/lang/Object;
.source "SparseArrayCompat.java"

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
    .locals 1

    #@0
    .prologue
    .line 24
    new-instance v0, Ljava/lang/Object;

    #@2
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    #@5
    sput-object v0, Landroid/support/v4/util/SparseArrayCompat;->DELETED:Ljava/lang/Object;

    #@7
    return-void
.end method

.method public constructor <init>()V
    .locals 1

    #@0
    .prologue
    .line 35
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    const/16 v0, 0xa

    #@2
    invoke-direct {p0, v0}, Landroid/support/v4/util/SparseArrayCompat;-><init>(I)V

    #@5
    .line 36
    return-void
.end method

.method public constructor <init>(I)V
    .locals 2
    .param p1, "initialCapacity"    # I

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    const/4 v1, 0x0

    #@1
    .line 45
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@4
    .line 25
    iput-boolean v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mGarbage:Z

    #@6
    .line 46
    if-nez p1, :cond_0

    #@8
    .line 47
    sget-object v0, Landroid/support/v4/util/ContainerHelpers;->EMPTY_INTS:[I

    #@a
    iput-object v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@c
    .line 48
    sget-object v0, Landroid/support/v4/util/ContainerHelpers;->EMPTY_OBJECTS:[Ljava/lang/Object;

    #@e
    iput-object v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@10
    .line 54
    :goto_0
    iput v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@12
    .line 55
    return-void

    #@13
    .line 50
    :cond_0
    invoke-static {p1}, Landroid/support/v4/util/ContainerHelpers;->idealIntArraySize(I)I

    #@16
    move-result p1

    #@17
    .line 51
    new-array v0, p1, [I

    #@19
    iput-object v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@1b
    .line 52
    new-array v0, p1, [Ljava/lang/Object;

    #@1d
    iput-object v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@1f
    goto :goto_0
.end method

.method private gc()V
    .locals 7

    #@0
    .prologue
    .line 141
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    iget v2, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@2
    .line 142
    .local v2, "n":I
    const/4 v3, 0x0

    #@3
    .line 143
    .local v3, "o":I
    iget-object v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@5
    .line 144
    .local v1, "keys":[I
    iget-object v5, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@7
    .line 146
    .local v5, "values":[Ljava/lang/Object;
    const/4 v0, 0x0

    #@8
    .local v0, "i":I
    :goto_0
    if-ge v0, v2, :cond_2

    #@a
    .line 147
    aget-object v4, v5, v0

    #@c
    .line 149
    .local v4, "val":Ljava/lang/Object;
    sget-object v6, Landroid/support/v4/util/SparseArrayCompat;->DELETED:Ljava/lang/Object;

    #@e
    if-eq v4, v6, :cond_1

    #@10
    .line 150
    if-eq v0, v3, :cond_0

    #@12
    .line 151
    aget v6, v1, v0

    #@14
    aput v6, v1, v3

    #@16
    .line 152
    aput-object v4, v5, v3

    #@18
    .line 153
    const/4 v6, 0x0

    #@19
    aput-object v6, v5, v0

    #@1b
    .line 156
    :cond_0
    add-int/lit8 v3, v3, 0x1

    #@1d
    .line 146
    :cond_1
    add-int/lit8 v0, v0, 0x1

    #@1f
    goto :goto_0

    #@20
    .line 160
    .end local v4    # "val":Ljava/lang/Object;
    :cond_2
    const/4 v6, 0x0

    #@21
    iput-boolean v6, p0, Landroid/support/v4/util/SparseArrayCompat;->mGarbage:Z

    #@23
    .line 161
    iput v3, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@25
    .line 164
    return-void
.end method


# virtual methods
.method public append(ILjava/lang/Object;)V
    .locals 7
    .param p1, "key"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ITE;)V"
        }
    .end annotation

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    .local p2, "value":Ljava/lang/Object;, "TE;"
    const/4 v6, 0x0

    #@1
    .line 325
    iget v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@3
    if-eqz v4, :cond_0

    #@5
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@7
    iget v5, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@9
    add-int/lit8 v5, v5, -0x1

    #@b
    aget v4, v4, v5

    #@d
    if-gt p1, v4, :cond_0

    #@f
    .line 326
    invoke-virtual {p0, p1, p2}, Landroid/support/v4/util/SparseArrayCompat;->put(ILjava/lang/Object;)V

    #@12
    .line 352
    :goto_0
    return-void

    #@13
    .line 330
    :cond_0
    iget-boolean v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mGarbage:Z

    #@15
    if-eqz v4, :cond_1

    #@17
    iget v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@19
    iget-object v5, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@1b
    array-length v5, v5

    #@1c
    if-lt v4, v5, :cond_1

    #@1e
    .line 331
    invoke-direct {p0}, Landroid/support/v4/util/SparseArrayCompat;->gc()V

    #@21
    .line 334
    :cond_1
    iget v3, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@23
    .line 335
    .local v3, "pos":I
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@25
    array-length v4, v4

    #@26
    if-lt v3, v4, :cond_2

    #@28
    .line 336
    add-int/lit8 v4, v3, 0x1

    #@2a
    invoke-static {v4}, Landroid/support/v4/util/ContainerHelpers;->idealIntArraySize(I)I

    #@2d
    move-result v0

    #@2e
    .line 338
    .local v0, "n":I
    new-array v1, v0, [I

    #@30
    .line 339
    .local v1, "nkeys":[I
    new-array v2, v0, [Ljava/lang/Object;

    #@32
    .line 342
    .local v2, "nvalues":[Ljava/lang/Object;
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@34
    iget-object v5, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@36
    array-length v5, v5

    #@37
    invoke-static {v4, v6, v1, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@3a
    .line 343
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@3c
    iget-object v5, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@3e
    array-length v5, v5

    #@3f
    invoke-static {v4, v6, v2, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@42
    .line 345
    iput-object v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@44
    .line 346
    iput-object v2, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@46
    .line 349
    .end local v0    # "n":I
    .end local v1    # "nkeys":[I
    .end local v2    # "nvalues":[Ljava/lang/Object;
    :cond_2
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@48
    aput p1, v4, v3

    #@4a
    .line 350
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@4c
    aput-object p2, v4, v3

    #@4e
    .line 351
    add-int/lit8 v4, v3, 0x1

    #@50
    iput v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@52
    goto :goto_0
.end method

.method public clear()V
    .locals 5

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    const/4 v4, 0x0

    #@1
    .line 309
    iget v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@3
    .line 310
    .local v1, "n":I
    iget-object v2, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@5
    .line 312
    .local v2, "values":[Ljava/lang/Object;
    const/4 v0, 0x0

    #@6
    .local v0, "i":I
    :goto_0
    if-ge v0, v1, :cond_0

    #@8
    .line 313
    const/4 v3, 0x0

    #@9
    aput-object v3, v2, v0

    #@b
    .line 312
    add-int/lit8 v0, v0, 0x1

    #@d
    goto :goto_0

    #@e
    .line 316
    :cond_0
    iput v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@10
    .line 317
    iput-boolean v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mGarbage:Z

    #@12
    .line 318
    return-void
.end method

.method public clone()Landroid/support/v4/util/SparseArrayCompat;
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Landroid/support/v4/util/SparseArrayCompat",
            "<TE;>;"
        }
    .end annotation

    #@0
    .prologue
    .line 60
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    const/4 v1, 0x0

    #@1
    .line 62
    .local v1, "clone":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    :try_start_0
    invoke-super {p0}, Ljava/lang/Object;->clone()Ljava/lang/Object;

    #@4
    move-result-object v2

    #@5
    move-object v0, v2

    #@6
    check-cast v0, Landroid/support/v4/util/SparseArrayCompat;

    #@8
    move-object v1, v0

    #@9
    .line 63
    iget-object v2, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@b
    invoke-virtual {v2}, [I->clone()Ljava/lang/Object;

    #@e
    move-result-object v2

    #@f
    check-cast v2, [I

    #@11
    iput-object v2, v1, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@13
    .line 64
    iget-object v2, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@15
    invoke-virtual {v2}, [Ljava/lang/Object;->clone()Ljava/lang/Object;

    #@18
    move-result-object v2

    #@19
    check-cast v2, [Ljava/lang/Object;

    #@1b
    iput-object v2, v1, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/CloneNotSupportedException; {:try_start_0 .. :try_end_0} :catch_0

    #@1d
    .line 68
    :goto_0
    return-object v1

    #@1e
    .line 65
    :catch_0
    move-exception v2

    #@1f
    goto :goto_0
.end method

.method public bridge synthetic clone()Ljava/lang/Object;
    .locals 1
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/CloneNotSupportedException;
        }
    .end annotation

    #@0
    .prologue
    .line 23
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    invoke-virtual {p0}, Landroid/support/v4/util/SparseArrayCompat;->clone()Landroid/support/v4/util/SparseArrayCompat;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public delete(I)V
    .locals 3
    .param p1, "key"    # I

    #@0
    .prologue
    .line 98
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    iget-object v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@2
    iget v2, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@4
    invoke-static {v1, v2, p1}, Landroid/support/v4/util/ContainerHelpers;->binarySearch([III)I

    #@7
    move-result v0

    #@8
    .line 100
    .local v0, "i":I
    if-ltz v0, :cond_0

    #@a
    .line 101
    iget-object v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@c
    aget-object v1, v1, v0

    #@e
    sget-object v2, Landroid/support/v4/util/SparseArrayCompat;->DELETED:Ljava/lang/Object;

    #@10
    if-eq v1, v2, :cond_0

    #@12
    .line 102
    iget-object v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@14
    sget-object v2, Landroid/support/v4/util/SparseArrayCompat;->DELETED:Ljava/lang/Object;

    #@16
    aput-object v2, v1, v0

    #@18
    .line 103
    const/4 v1, 0x1

    #@19
    iput-boolean v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mGarbage:Z

    #@1b
    .line 106
    :cond_0
    return-void
.end method

.method public get(I)Ljava/lang/Object;
    .locals 1
    .param p1, "key"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I)TE;"
        }
    .end annotation

    #@0
    .prologue
    .line 76
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    const/4 v0, 0x0

    #@1
    invoke-virtual {p0, p1, v0}, Landroid/support/v4/util/SparseArrayCompat;->get(ILjava/lang/Object;)Ljava/lang/Object;

    #@4
    move-result-object v0

    #@5
    return-object v0
.end method

.method public get(ILjava/lang/Object;)Ljava/lang/Object;
    .locals 3
    .param p1, "key"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ITE;)TE;"
        }
    .end annotation

    #@0
    .prologue
    .line 85
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    .local p2, "valueIfKeyNotFound":Ljava/lang/Object;, "TE;"
    iget-object v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@2
    iget v2, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@4
    invoke-static {v1, v2, p1}, Landroid/support/v4/util/ContainerHelpers;->binarySearch([III)I

    #@7
    move-result v0

    #@8
    .line 87
    .local v0, "i":I
    if-ltz v0, :cond_0

    #@a
    iget-object v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@c
    aget-object v1, v1, v0

    #@e
    sget-object v2, Landroid/support/v4/util/SparseArrayCompat;->DELETED:Ljava/lang/Object;

    #@10
    if-ne v1, v2, :cond_1

    #@12
    .line 90
    .end local p2    # "valueIfKeyNotFound":Ljava/lang/Object;, "TE;"
    :cond_0
    :goto_0
    return-object p2

    #@13
    .restart local p2    # "valueIfKeyNotFound":Ljava/lang/Object;, "TE;"
    :cond_1
    iget-object v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@15
    aget-object p2, v1, v0

    #@17
    goto :goto_0
.end method

.method public indexOfKey(I)I
    .locals 2
    .param p1, "key"    # I

    #@0
    .prologue
    .line 276
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    iget-boolean v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mGarbage:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 277
    invoke-direct {p0}, Landroid/support/v4/util/SparseArrayCompat;->gc()V

    #@7
    .line 280
    :cond_0
    iget-object v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@9
    iget v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@b
    invoke-static {v0, v1, p1}, Landroid/support/v4/util/ContainerHelpers;->binarySearch([III)I

    #@e
    move-result v0

    #@f
    return v0
.end method

.method public indexOfValue(Ljava/lang/Object;)I
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TE;)I"
        }
    .end annotation

    #@0
    .prologue
    .line 294
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    .local p1, "value":Ljava/lang/Object;, "TE;"
    iget-boolean v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mGarbage:Z

    #@2
    if-eqz v1, :cond_0

    #@4
    .line 295
    invoke-direct {p0}, Landroid/support/v4/util/SparseArrayCompat;->gc()V

    #@7
    .line 298
    :cond_0
    const/4 v0, 0x0

    #@8
    .local v0, "i":I
    :goto_0
    iget v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@a
    if-ge v0, v1, :cond_2

    #@c
    .line 299
    iget-object v1, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@e
    aget-object v1, v1, v0

    #@10
    if-ne v1, p1, :cond_1

    #@12
    .line 302
    .end local v0    # "i":I
    :goto_1
    return v0

    #@13
    .line 298
    .restart local v0    # "i":I
    :cond_1
    add-int/lit8 v0, v0, 0x1

    #@15
    goto :goto_0

    #@16
    .line 302
    :cond_2
    const/4 v0, -0x1

    #@17
    goto :goto_1
.end method

.method public keyAt(I)I
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 236
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    iget-boolean v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mGarbage:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 237
    invoke-direct {p0}, Landroid/support/v4/util/SparseArrayCompat;->gc()V

    #@7
    .line 240
    :cond_0
    iget-object v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@9
    aget v0, v0, p1

    #@b
    return v0
.end method

.method public put(ILjava/lang/Object;)V
    .locals 8
    .param p1, "key"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ITE;)V"
        }
    .end annotation

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    .local p2, "value":Ljava/lang/Object;, "TE;"
    const/4 v6, 0x0

    #@1
    .line 172
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@3
    iget v5, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@5
    invoke-static {v4, v5, p1}, Landroid/support/v4/util/ContainerHelpers;->binarySearch([III)I

    #@8
    move-result v0

    #@9
    .line 174
    .local v0, "i":I
    if-ltz v0, :cond_0

    #@b
    .line 175
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@d
    aput-object p2, v4, v0

    #@f
    .line 216
    :goto_0
    return-void

    #@10
    .line 177
    :cond_0
    xor-int/lit8 v0, v0, -0x1

    #@12
    .line 179
    iget v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@14
    if-ge v0, v4, :cond_1

    #@16
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@18
    aget-object v4, v4, v0

    #@1a
    sget-object v5, Landroid/support/v4/util/SparseArrayCompat;->DELETED:Ljava/lang/Object;

    #@1c
    if-ne v4, v5, :cond_1

    #@1e
    .line 180
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@20
    aput p1, v4, v0

    #@22
    .line 181
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@24
    aput-object p2, v4, v0

    #@26
    goto :goto_0

    #@27
    .line 185
    :cond_1
    iget-boolean v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mGarbage:Z

    #@29
    if-eqz v4, :cond_2

    #@2b
    iget v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@2d
    iget-object v5, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@2f
    array-length v5, v5

    #@30
    if-lt v4, v5, :cond_2

    #@32
    .line 186
    invoke-direct {p0}, Landroid/support/v4/util/SparseArrayCompat;->gc()V

    #@35
    .line 189
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@37
    iget v5, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@39
    invoke-static {v4, v5, p1}, Landroid/support/v4/util/ContainerHelpers;->binarySearch([III)I

    #@3c
    move-result v4

    #@3d
    xor-int/lit8 v0, v4, -0x1

    #@3f
    .line 192
    :cond_2
    iget v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@41
    iget-object v5, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@43
    array-length v5, v5

    #@44
    if-lt v4, v5, :cond_3

    #@46
    .line 193
    iget v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@48
    add-int/lit8 v4, v4, 0x1

    #@4a
    invoke-static {v4}, Landroid/support/v4/util/ContainerHelpers;->idealIntArraySize(I)I

    #@4d
    move-result v1

    #@4e
    .line 195
    .local v1, "n":I
    new-array v2, v1, [I

    #@50
    .line 196
    .local v2, "nkeys":[I
    new-array v3, v1, [Ljava/lang/Object;

    #@52
    .line 199
    .local v3, "nvalues":[Ljava/lang/Object;
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@54
    iget-object v5, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@56
    array-length v5, v5

    #@57
    invoke-static {v4, v6, v2, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@5a
    .line 200
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@5c
    iget-object v5, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@5e
    array-length v5, v5

    #@5f
    invoke-static {v4, v6, v3, v6, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@62
    .line 202
    iput-object v2, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@64
    .line 203
    iput-object v3, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@66
    .line 206
    .end local v1    # "n":I
    .end local v2    # "nkeys":[I
    .end local v3    # "nvalues":[Ljava/lang/Object;
    :cond_3
    iget v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@68
    sub-int/2addr v4, v0

    #@69
    if-eqz v4, :cond_4

    #@6b
    .line 208
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@6d
    iget-object v5, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@6f
    add-int/lit8 v6, v0, 0x1

    #@71
    iget v7, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@73
    sub-int/2addr v7, v0

    #@74
    invoke-static {v4, v0, v5, v6, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@77
    .line 209
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@79
    iget-object v5, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@7b
    add-int/lit8 v6, v0, 0x1

    #@7d
    iget v7, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@7f
    sub-int/2addr v7, v0

    #@80
    invoke-static {v4, v0, v5, v6, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@83
    .line 212
    :cond_4
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mKeys:[I

    #@85
    aput p1, v4, v0

    #@87
    .line 213
    iget-object v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@89
    aput-object p2, v4, v0

    #@8b
    .line 214
    iget v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@8d
    add-int/lit8 v4, v4, 0x1

    #@8f
    iput v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@91
    goto/16 :goto_0
.end method

.method public remove(I)V
    .locals 0
    .param p1, "key"    # I

    #@0
    .prologue
    .line 112
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    invoke-virtual {p0, p1}, Landroid/support/v4/util/SparseArrayCompat;->delete(I)V

    #@3
    .line 113
    return-void
.end method

.method public removeAt(I)V
    .locals 2
    .param p1, "index"    # I

    #@0
    .prologue
    .line 119
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    iget-object v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@2
    aget-object v0, v0, p1

    #@4
    sget-object v1, Landroid/support/v4/util/SparseArrayCompat;->DELETED:Ljava/lang/Object;

    #@6
    if-eq v0, v1, :cond_0

    #@8
    .line 120
    iget-object v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@a
    sget-object v1, Landroid/support/v4/util/SparseArrayCompat;->DELETED:Ljava/lang/Object;

    #@c
    aput-object v1, v0, p1

    #@e
    .line 121
    const/4 v0, 0x1

    #@f
    iput-boolean v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mGarbage:Z

    #@11
    .line 123
    :cond_0
    return-void
.end method

.method public removeAtRange(II)V
    .locals 4
    .param p1, "index"    # I
    .param p2, "size"    # I

    #@0
    .prologue
    .line 132
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    iget v2, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@2
    add-int v3, p1, p2

    #@4
    invoke-static {v2, v3}, Ljava/lang/Math;->min(II)I

    #@7
    move-result v0

    #@8
    .line 133
    .local v0, "end":I
    move v1, p1

    #@9
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_0

    #@b
    .line 134
    invoke-virtual {p0, v1}, Landroid/support/v4/util/SparseArrayCompat;->removeAt(I)V

    #@e
    .line 133
    add-int/lit8 v1, v1, 0x1

    #@10
    goto :goto_0

    #@11
    .line 136
    :cond_0
    return-void
.end method

.method public setValueAt(ILjava/lang/Object;)V
    .locals 1
    .param p1, "index"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ITE;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 263
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    .local p2, "value":Ljava/lang/Object;, "TE;"
    iget-boolean v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mGarbage:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 264
    invoke-direct {p0}, Landroid/support/v4/util/SparseArrayCompat;->gc()V

    #@7
    .line 267
    :cond_0
    iget-object v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@9
    aput-object p2, v0, p1

    #@b
    .line 268
    return-void
.end method

.method public size()I
    .locals 1

    #@0
    .prologue
    .line 223
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    iget-boolean v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mGarbage:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 224
    invoke-direct {p0}, Landroid/support/v4/util/SparseArrayCompat;->gc()V

    #@7
    .line 227
    :cond_0
    iget v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@9
    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 5

    #@0
    .prologue
    .line 363
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    invoke-virtual {p0}, Landroid/support/v4/util/SparseArrayCompat;->size()I

    #@3
    move-result v4

    #@4
    if-gtz v4, :cond_0

    #@6
    .line 364
    const-string v4, "{}"

    #@8
    .line 384
    :goto_0
    return-object v4

    #@9
    .line 367
    :cond_0
    new-instance v0, Ljava/lang/StringBuilder;

    #@b
    iget v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@d
    mul-int/lit8 v4, v4, 0x1c

    #@f
    invoke-direct {v0, v4}, Ljava/lang/StringBuilder;-><init>(I)V

    #@12
    .line 368
    .local v0, "buffer":Ljava/lang/StringBuilder;
    const/16 v4, 0x7b

    #@14
    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@17
    .line 369
    const/4 v1, 0x0

    #@18
    .local v1, "i":I
    :goto_1
    iget v4, p0, Landroid/support/v4/util/SparseArrayCompat;->mSize:I

    #@1a
    if-ge v1, v4, :cond_3

    #@1c
    .line 370
    if-lez v1, :cond_1

    #@1e
    .line 371
    const-string v4, ", "

    #@20
    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@23
    .line 373
    :cond_1
    invoke-virtual {p0, v1}, Landroid/support/v4/util/SparseArrayCompat;->keyAt(I)I

    #@26
    move-result v2

    #@27
    .line 374
    .local v2, "key":I
    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@2a
    .line 375
    const/16 v4, 0x3d

    #@2c
    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@2f
    .line 376
    invoke-virtual {p0, v1}, Landroid/support/v4/util/SparseArrayCompat;->valueAt(I)Ljava/lang/Object;

    #@32
    move-result-object v3

    #@33
    .line 377
    .local v3, "value":Ljava/lang/Object;
    if-eq v3, p0, :cond_2

    #@35
    .line 378
    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@38
    .line 369
    :goto_2
    add-int/lit8 v1, v1, 0x1

    #@3a
    goto :goto_1

    #@3b
    .line 380
    :cond_2
    const-string v4, "(this Map)"

    #@3d
    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@40
    goto :goto_2

    #@41
    .line 383
    .end local v2    # "key":I
    .end local v3    # "value":Ljava/lang/Object;
    :cond_3
    const/16 v4, 0x7d

    #@43
    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@46
    .line 384
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@49
    move-result-object v4

    #@4a
    goto :goto_0
.end method

.method public valueAt(I)Ljava/lang/Object;
    .locals 1
    .param p1, "index"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I)TE;"
        }
    .end annotation

    #@0
    .prologue
    .line 250
    .local p0, "this":Landroid/support/v4/util/SparseArrayCompat;, "Landroid/support/v4/util/SparseArrayCompat<TE;>;"
    iget-boolean v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mGarbage:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 251
    invoke-direct {p0}, Landroid/support/v4/util/SparseArrayCompat;->gc()V

    #@7
    .line 254
    :cond_0
    iget-object v0, p0, Landroid/support/v4/util/SparseArrayCompat;->mValues:[Ljava/lang/Object;

    #@9
    aget-object v0, v0, p1

    #@b
    return-object v0
.end method
