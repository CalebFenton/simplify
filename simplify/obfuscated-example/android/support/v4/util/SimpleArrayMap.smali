.class public Landroid/support/v4/util/SimpleArrayMap;
.super Ljava/lang/Object;
.source "SimpleArrayMap.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<K:",
        "Ljava/lang/Object;",
        "V:",
        "Ljava/lang/Object;",
        ">",
        "Ljava/lang/Object;"
    }
.end annotation


# static fields
.field private static final BASE_SIZE:I = 0x4

.field private static final CACHE_SIZE:I = 0xa

.field private static final DEBUG:Z = false

.field private static final TAG:Ljava/lang/String; = "ArrayMap"

.field static mBaseCache:[Ljava/lang/Object;

.field static mBaseCacheSize:I

.field static mTwiceBaseCache:[Ljava/lang/Object;

.field static mTwiceBaseCacheSize:I


# instance fields
.field mArray:[Ljava/lang/Object;

.field mHashes:[I

.field mSize:I


# direct methods
.method public constructor <init>()V
    .locals 1

    #@0
    .prologue
    .line 208
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 209
    sget-object v0, Landroid/support/v4/util/ContainerHelpers;->EMPTY_INTS:[I

    #@5
    iput-object v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@7
    .line 210
    sget-object v0, Landroid/support/v4/util/ContainerHelpers;->EMPTY_OBJECTS:[Ljava/lang/Object;

    #@9
    iput-object v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@b
    .line 211
    const/4 v0, 0x0

    #@c
    iput v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@e
    .line 212
    return-void
.end method

.method public constructor <init>(I)V
    .locals 1
    .param p1, "capacity"    # I

    #@0
    .prologue
    .line 217
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 218
    if-nez p1, :cond_0

    #@5
    .line 219
    sget-object v0, Landroid/support/v4/util/ContainerHelpers;->EMPTY_INTS:[I

    #@7
    iput-object v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@9
    .line 220
    sget-object v0, Landroid/support/v4/util/ContainerHelpers;->EMPTY_OBJECTS:[Ljava/lang/Object;

    #@b
    iput-object v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@d
    .line 224
    :goto_0
    const/4 v0, 0x0

    #@e
    iput v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@10
    .line 225
    return-void

    #@11
    .line 222
    :cond_0
    invoke-direct {p0, p1}, Landroid/support/v4/util/SimpleArrayMap;->allocArrays(I)V

    #@14
    goto :goto_0
.end method

.method public constructor <init>(Landroid/support/v4/util/SimpleArrayMap;)V
    .locals 0
    .param p1, "map"    # Landroid/support/v4/util/SimpleArrayMap;

    #@0
    .prologue
    .line 231
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    invoke-direct {p0}, Landroid/support/v4/util/SimpleArrayMap;-><init>()V

    #@3
    .line 232
    if-eqz p1, :cond_0

    #@5
    .line 233
    invoke-virtual {p0, p1}, Landroid/support/v4/util/SimpleArrayMap;->putAll(Landroid/support/v4/util/SimpleArrayMap;)V

    #@8
    .line 235
    :cond_0
    return-void
.end method

.method private allocArrays(I)V
    .locals 5
    .param p1, "size"    # I

    #@0
    .prologue
    .line 138
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    const/16 v1, 0x8

    #@2
    if-ne p1, v1, :cond_2

    #@4
    .line 139
    const-class v2, Landroid/support/v4/util/ArrayMap;

    #@6
    monitor-enter v2

    #@7
    .line 140
    :try_start_0
    sget-object v1, Landroid/support/v4/util/SimpleArrayMap;->mTwiceBaseCache:[Ljava/lang/Object;

    #@9
    if-eqz v1, :cond_0

    #@b
    .line 141
    sget-object v0, Landroid/support/v4/util/SimpleArrayMap;->mTwiceBaseCache:[Ljava/lang/Object;

    #@d
    .line 142
    .local v0, "array":[Ljava/lang/Object;
    iput-object v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@f
    .line 143
    const/4 v1, 0x0

    #@10
    aget-object v1, v0, v1

    #@12
    check-cast v1, [Ljava/lang/Object;

    #@14
    check-cast v1, [Ljava/lang/Object;

    #@16
    sput-object v1, Landroid/support/v4/util/SimpleArrayMap;->mTwiceBaseCache:[Ljava/lang/Object;

    #@18
    .line 144
    const/4 v1, 0x1

    #@19
    aget-object v1, v0, v1

    #@1b
    check-cast v1, [I

    #@1d
    check-cast v1, [I

    #@1f
    iput-object v1, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@21
    .line 145
    const/4 v1, 0x0

    #@22
    const/4 v3, 0x1

    #@23
    const/4 v4, 0x0

    #@24
    aput-object v4, v0, v3

    #@26
    aput-object v4, v0, v1

    #@28
    .line 146
    sget v1, Landroid/support/v4/util/SimpleArrayMap;->mTwiceBaseCacheSize:I

    #@2a
    add-int/lit8 v1, v1, -0x1

    #@2c
    sput v1, Landroid/support/v4/util/SimpleArrayMap;->mTwiceBaseCacheSize:I

    #@2e
    .line 149
    monitor-exit v2

    #@2f
    .line 170
    .end local v0    # "array":[Ljava/lang/Object;
    :goto_0
    return-void

    #@30
    .line 151
    :cond_0
    monitor-exit v2
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@31
    .line 168
    :cond_1
    :goto_1
    new-array v1, p1, [I

    #@33
    iput-object v1, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@35
    .line 169
    shl-int/lit8 v1, p1, 0x1

    #@37
    new-array v1, v1, [Ljava/lang/Object;

    #@39
    iput-object v1, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@3b
    goto :goto_0

    #@3c
    .line 151
    :catchall_0
    move-exception v1

    #@3d
    :try_start_1
    monitor-exit v2
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@3e
    throw v1

    #@3f
    .line 152
    :cond_2
    const/4 v1, 0x4

    #@40
    if-ne p1, v1, :cond_1

    #@42
    .line 153
    const-class v2, Landroid/support/v4/util/ArrayMap;

    #@44
    monitor-enter v2

    #@45
    .line 154
    :try_start_2
    sget-object v1, Landroid/support/v4/util/SimpleArrayMap;->mBaseCache:[Ljava/lang/Object;

    #@47
    if-eqz v1, :cond_3

    #@49
    .line 155
    sget-object v0, Landroid/support/v4/util/SimpleArrayMap;->mBaseCache:[Ljava/lang/Object;

    #@4b
    .line 156
    .restart local v0    # "array":[Ljava/lang/Object;
    iput-object v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@4d
    .line 157
    const/4 v1, 0x0

    #@4e
    aget-object v1, v0, v1

    #@50
    check-cast v1, [Ljava/lang/Object;

    #@52
    check-cast v1, [Ljava/lang/Object;

    #@54
    sput-object v1, Landroid/support/v4/util/SimpleArrayMap;->mBaseCache:[Ljava/lang/Object;

    #@56
    .line 158
    const/4 v1, 0x1

    #@57
    aget-object v1, v0, v1

    #@59
    check-cast v1, [I

    #@5b
    check-cast v1, [I

    #@5d
    iput-object v1, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@5f
    .line 159
    const/4 v1, 0x0

    #@60
    const/4 v3, 0x1

    #@61
    const/4 v4, 0x0

    #@62
    aput-object v4, v0, v3

    #@64
    aput-object v4, v0, v1

    #@66
    .line 160
    sget v1, Landroid/support/v4/util/SimpleArrayMap;->mBaseCacheSize:I

    #@68
    add-int/lit8 v1, v1, -0x1

    #@6a
    sput v1, Landroid/support/v4/util/SimpleArrayMap;->mBaseCacheSize:I

    #@6c
    .line 163
    monitor-exit v2

    #@6d
    goto :goto_0

    #@6e
    .line 165
    .end local v0    # "array":[Ljava/lang/Object;
    :catchall_1
    move-exception v1

    #@6f
    monitor-exit v2
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_1

    #@70
    throw v1

    #@71
    :cond_3
    :try_start_3
    monitor-exit v2
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_1

    #@72
    goto :goto_1
.end method

.method private static freeArrays([I[Ljava/lang/Object;I)V
    .locals 5
    .param p0, "hashes"    # [I
    .param p1, "array"    # [Ljava/lang/Object;
    .param p2, "size"    # I

    #@0
    .prologue
    const/16 v3, 0xa

    #@2
    const/4 v4, 0x2

    #@3
    .line 173
    array-length v1, p0

    #@4
    const/16 v2, 0x8

    #@6
    if-ne v1, v2, :cond_3

    #@8
    .line 174
    const-class v2, Landroid/support/v4/util/ArrayMap;

    #@a
    monitor-enter v2

    #@b
    .line 175
    :try_start_0
    sget v1, Landroid/support/v4/util/SimpleArrayMap;->mTwiceBaseCacheSize:I

    #@d
    if-ge v1, v3, :cond_1

    #@f
    .line 176
    const/4 v1, 0x0

    #@10
    sget-object v3, Landroid/support/v4/util/SimpleArrayMap;->mTwiceBaseCache:[Ljava/lang/Object;

    #@12
    aput-object v3, p1, v1

    #@14
    .line 177
    const/4 v1, 0x1

    #@15
    aput-object p0, p1, v1

    #@17
    .line 178
    shl-int/lit8 v1, p2, 0x1

    #@19
    add-int/lit8 v0, v1, -0x1

    #@1b
    .local v0, "i":I
    :goto_0
    if-lt v0, v4, :cond_0

    #@1d
    .line 179
    const/4 v1, 0x0

    #@1e
    aput-object v1, p1, v0

    #@20
    .line 178
    add-int/lit8 v0, v0, -0x1

    #@22
    goto :goto_0

    #@23
    .line 181
    :cond_0
    sput-object p1, Landroid/support/v4/util/SimpleArrayMap;->mTwiceBaseCache:[Ljava/lang/Object;

    #@25
    .line 182
    sget v1, Landroid/support/v4/util/SimpleArrayMap;->mTwiceBaseCacheSize:I

    #@27
    add-int/lit8 v1, v1, 0x1

    #@29
    sput v1, Landroid/support/v4/util/SimpleArrayMap;->mTwiceBaseCacheSize:I

    #@2b
    .line 186
    .end local v0    # "i":I
    :cond_1
    monitor-exit v2

    #@2c
    .line 202
    :cond_2
    :goto_1
    return-void

    #@2d
    .line 186
    :catchall_0
    move-exception v1

    #@2e
    monitor-exit v2
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@2f
    throw v1

    #@30
    .line 187
    :cond_3
    array-length v1, p0

    #@31
    const/4 v2, 0x4

    #@32
    if-ne v1, v2, :cond_2

    #@34
    .line 188
    const-class v2, Landroid/support/v4/util/ArrayMap;

    #@36
    monitor-enter v2

    #@37
    .line 189
    :try_start_1
    sget v1, Landroid/support/v4/util/SimpleArrayMap;->mBaseCacheSize:I

    #@39
    if-ge v1, v3, :cond_5

    #@3b
    .line 190
    const/4 v1, 0x0

    #@3c
    sget-object v3, Landroid/support/v4/util/SimpleArrayMap;->mBaseCache:[Ljava/lang/Object;

    #@3e
    aput-object v3, p1, v1

    #@40
    .line 191
    const/4 v1, 0x1

    #@41
    aput-object p0, p1, v1

    #@43
    .line 192
    shl-int/lit8 v1, p2, 0x1

    #@45
    add-int/lit8 v0, v1, -0x1

    #@47
    .restart local v0    # "i":I
    :goto_2
    if-lt v0, v4, :cond_4

    #@49
    .line 193
    const/4 v1, 0x0

    #@4a
    aput-object v1, p1, v0

    #@4c
    .line 192
    add-int/lit8 v0, v0, -0x1

    #@4e
    goto :goto_2

    #@4f
    .line 195
    :cond_4
    sput-object p1, Landroid/support/v4/util/SimpleArrayMap;->mBaseCache:[Ljava/lang/Object;

    #@51
    .line 196
    sget v1, Landroid/support/v4/util/SimpleArrayMap;->mBaseCacheSize:I

    #@53
    add-int/lit8 v1, v1, 0x1

    #@55
    sput v1, Landroid/support/v4/util/SimpleArrayMap;->mBaseCacheSize:I

    #@57
    .line 200
    .end local v0    # "i":I
    :cond_5
    monitor-exit v2

    #@58
    goto :goto_1

    #@59
    :catchall_1
    move-exception v1

    #@5a
    monitor-exit v2
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    #@5b
    throw v1
.end method


# virtual methods
.method public clear()V
    .locals 3

    #@0
    .prologue
    .line 241
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    iget v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 242
    iget-object v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@6
    iget-object v1, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@8
    iget v2, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@a
    invoke-static {v0, v1, v2}, Landroid/support/v4/util/SimpleArrayMap;->freeArrays([I[Ljava/lang/Object;I)V

    #@d
    .line 243
    sget-object v0, Landroid/support/v4/util/ContainerHelpers;->EMPTY_INTS:[I

    #@f
    iput-object v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@11
    .line 244
    sget-object v0, Landroid/support/v4/util/ContainerHelpers;->EMPTY_OBJECTS:[Ljava/lang/Object;

    #@13
    iput-object v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@15
    .line 245
    const/4 v0, 0x0

    #@16
    iput v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@18
    .line 247
    :cond_0
    return-void
.end method

.method public containsKey(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "key"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 273
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    invoke-virtual {p0, p1}, Landroid/support/v4/util/SimpleArrayMap;->indexOfKey(Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    if-ltz v0, :cond_0

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

.method public containsValue(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "value"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 313
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    invoke-virtual {p0, p1}, Landroid/support/v4/util/SimpleArrayMap;->indexOfValue(Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    if-ltz v0, :cond_0

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

.method public ensureCapacity(I)V
    .locals 5
    .param p1, "minimumCapacity"    # I

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    const/4 v4, 0x0

    #@1
    .line 254
    iget-object v2, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@3
    array-length v2, v2

    #@4
    if-ge v2, p1, :cond_1

    #@6
    .line 255
    iget-object v1, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@8
    .line 256
    .local v1, "ohashes":[I
    iget-object v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@a
    .line 257
    .local v0, "oarray":[Ljava/lang/Object;
    invoke-direct {p0, p1}, Landroid/support/v4/util/SimpleArrayMap;->allocArrays(I)V

    #@d
    .line 258
    iget v2, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@f
    if-lez v2, :cond_0

    #@11
    .line 259
    iget-object v2, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@13
    iget v3, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@15
    invoke-static {v1, v4, v2, v4, v3}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@18
    .line 260
    iget-object v2, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@1a
    iget v3, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@1c
    shl-int/lit8 v3, v3, 0x1

    #@1e
    invoke-static {v0, v4, v2, v4, v3}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@21
    .line 262
    :cond_0
    iget v2, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@23
    invoke-static {v1, v0, v2}, Landroid/support/v4/util/SimpleArrayMap;->freeArrays([I[Ljava/lang/Object;I)V

    #@26
    .line 264
    .end local v0    # "oarray":[Ljava/lang/Object;
    .end local v1    # "ohashes":[I
    :cond_1
    return-void
.end method

.method public equals(Ljava/lang/Object;)Z
    .locals 10
    .param p1, "object"    # Ljava/lang/Object;

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    const/4 v6, 0x1

    #@1
    const/4 v7, 0x0

    #@2
    .line 532
    if-ne p0, p1, :cond_1

    #@4
    .line 561
    :cond_0
    :goto_0
    return v6

    #@5
    .line 535
    :cond_1
    instance-of v8, p1, Ljava/util/Map;

    #@7
    if-eqz v8, :cond_6

    #@9
    move-object v3, p1

    #@a
    .line 536
    check-cast v3, Ljava/util/Map;

    #@c
    .line 537
    .local v3, "map":Ljava/util/Map;, "Ljava/util/Map<**>;"
    invoke-virtual {p0}, Landroid/support/v4/util/SimpleArrayMap;->size()I

    #@f
    move-result v8

    #@10
    invoke-interface {v3}, Ljava/util/Map;->size()I

    #@13
    move-result v9

    #@14
    if-eq v8, v9, :cond_2

    #@16
    move v6, v7

    #@17
    .line 538
    goto :goto_0

    #@18
    .line 542
    :cond_2
    const/4 v0, 0x0

    #@19
    .local v0, "i":I
    :goto_1
    :try_start_0
    iget v8, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@1b
    if-ge v0, v8, :cond_0

    #@1d
    .line 543
    invoke-virtual {p0, v0}, Landroid/support/v4/util/SimpleArrayMap;->keyAt(I)Ljava/lang/Object;

    #@20
    move-result-object v2

    #@21
    .line 544
    .local v2, "key":Ljava/lang/Object;, "TK;"
    invoke-virtual {p0, v0}, Landroid/support/v4/util/SimpleArrayMap;->valueAt(I)Ljava/lang/Object;

    #@24
    move-result-object v4

    #@25
    .line 545
    .local v4, "mine":Ljava/lang/Object;, "TV;"
    invoke-interface {v3, v2}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@28
    move-result-object v5

    #@29
    .line 546
    .local v5, "theirs":Ljava/lang/Object;
    if-nez v4, :cond_4

    #@2b
    .line 547
    if-nez v5, :cond_3

    #@2d
    invoke-interface {v3, v2}, Ljava/util/Map;->containsKey(Ljava/lang/Object;)Z

    #@30
    move-result v8

    #@31
    if-nez v8, :cond_5

    #@33
    :cond_3
    move v6, v7

    #@34
    .line 548
    goto :goto_0

    #@35
    .line 550
    :cond_4
    invoke-virtual {v4, v5}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z
    :try_end_0
    .catch Ljava/lang/NullPointerException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/lang/ClassCastException; {:try_start_0 .. :try_end_0} :catch_1

    #@38
    move-result v8

    #@39
    if-nez v8, :cond_5

    #@3b
    move v6, v7

    #@3c
    .line 551
    goto :goto_0

    #@3d
    .line 542
    :cond_5
    add-int/lit8 v0, v0, 0x1

    #@3f
    goto :goto_1

    #@40
    .line 554
    .end local v2    # "key":Ljava/lang/Object;, "TK;"
    .end local v4    # "mine":Ljava/lang/Object;, "TV;"
    .end local v5    # "theirs":Ljava/lang/Object;
    :catch_0
    move-exception v1

    #@41
    .local v1, "ignored":Ljava/lang/NullPointerException;
    move v6, v7

    #@42
    .line 555
    goto :goto_0

    #@43
    .line 556
    .end local v1    # "ignored":Ljava/lang/NullPointerException;
    :catch_1
    move-exception v1

    #@44
    .local v1, "ignored":Ljava/lang/ClassCastException;
    move v6, v7

    #@45
    .line 557
    goto :goto_0

    #@46
    .end local v0    # "i":I
    .end local v1    # "ignored":Ljava/lang/ClassCastException;
    .end local v3    # "map":Ljava/util/Map;, "Ljava/util/Map<**>;"
    :cond_6
    move v6, v7

    #@47
    .line 561
    goto :goto_0
.end method

.method public get(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 3
    .param p1, "key"    # Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Object;",
            ")TV;"
        }
    .end annotation

    #@0
    .prologue
    .line 323
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    invoke-virtual {p0, p1}, Landroid/support/v4/util/SimpleArrayMap;->indexOfKey(Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    .line 324
    .local v0, "index":I
    if-ltz v0, :cond_0

    #@6
    iget-object v1, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@8
    shl-int/lit8 v2, v0, 0x1

    #@a
    add-int/lit8 v2, v2, 0x1

    #@c
    aget-object v1, v1, v2

    #@e
    :goto_0
    return-object v1

    #@f
    :cond_0
    const/4 v1, 0x0

    #@10
    goto :goto_0
.end method

.method public hashCode()I
    .locals 9

    #@0
    .prologue
    .line 569
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    iget-object v1, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@2
    .line 570
    .local v1, "hashes":[I
    iget-object v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@4
    .line 571
    .local v0, "array":[Ljava/lang/Object;
    const/4 v3, 0x0

    #@5
    .line 572
    .local v3, "result":I
    const/4 v2, 0x0

    #@6
    .local v2, "i":I
    const/4 v5, 0x1

    #@7
    .local v5, "v":I
    iget v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@9
    .local v4, "s":I
    :goto_0
    if-ge v2, v4, :cond_1

    #@b
    .line 573
    aget-object v6, v0, v5

    #@d
    .line 574
    .local v6, "value":Ljava/lang/Object;
    aget v8, v1, v2

    #@f
    if-nez v6, :cond_0

    #@11
    const/4 v7, 0x0

    #@12
    :goto_1
    xor-int/2addr v7, v8

    #@13
    add-int/2addr v3, v7

    #@14
    .line 572
    add-int/lit8 v2, v2, 0x1

    #@16
    add-int/lit8 v5, v5, 0x2

    #@18
    goto :goto_0

    #@19
    .line 574
    :cond_0
    invoke-virtual {v6}, Ljava/lang/Object;->hashCode()I

    #@1c
    move-result v7

    #@1d
    goto :goto_1

    #@1e
    .line 576
    .end local v6    # "value":Ljava/lang/Object;
    :cond_1
    return v3
.end method

.method indexOf(Ljava/lang/Object;I)I
    .locals 6
    .param p1, "key"    # Ljava/lang/Object;
    .param p2, "hash"    # I

    #@0
    .prologue
    .line 62
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    iget v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@2
    .line 65
    .local v0, "N":I
    if-nez v0, :cond_1

    #@4
    .line 66
    const/4 v3, -0x1

    #@5
    .line 96
    :cond_0
    :goto_0
    return v3

    #@6
    .line 69
    :cond_1
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@8
    invoke-static {v4, v0, p2}, Landroid/support/v4/util/ContainerHelpers;->binarySearch([III)I

    #@b
    move-result v3

    #@c
    .line 72
    .local v3, "index":I
    if-ltz v3, :cond_0

    #@e
    .line 77
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@10
    shl-int/lit8 v5, v3, 0x1

    #@12
    aget-object v4, v4, v5

    #@14
    invoke-virtual {p1, v4}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z

    #@17
    move-result v4

    #@18
    if-nez v4, :cond_0

    #@1a
    .line 83
    add-int/lit8 v1, v3, 0x1

    #@1c
    .local v1, "end":I
    :goto_1
    if-ge v1, v0, :cond_3

    #@1e
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@20
    aget v4, v4, v1

    #@22
    if-ne v4, p2, :cond_3

    #@24
    .line 84
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@26
    shl-int/lit8 v5, v1, 0x1

    #@28
    aget-object v4, v4, v5

    #@2a
    invoke-virtual {p1, v4}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z

    #@2d
    move-result v4

    #@2e
    if-eqz v4, :cond_2

    #@30
    move v3, v1

    #@31
    goto :goto_0

    #@32
    .line 83
    :cond_2
    add-int/lit8 v1, v1, 0x1

    #@34
    goto :goto_1

    #@35
    .line 88
    :cond_3
    add-int/lit8 v2, v3, -0x1

    #@37
    .local v2, "i":I
    :goto_2
    if-ltz v2, :cond_5

    #@39
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@3b
    aget v4, v4, v2

    #@3d
    if-ne v4, p2, :cond_5

    #@3f
    .line 89
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@41
    shl-int/lit8 v5, v2, 0x1

    #@43
    aget-object v4, v4, v5

    #@45
    invoke-virtual {p1, v4}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z

    #@48
    move-result v4

    #@49
    if-eqz v4, :cond_4

    #@4b
    move v3, v2

    #@4c
    goto :goto_0

    #@4d
    .line 88
    :cond_4
    add-int/lit8 v2, v2, -0x1

    #@4f
    goto :goto_2

    #@50
    .line 96
    :cond_5
    xor-int/lit8 v3, v1, -0x1

    #@52
    goto :goto_0
.end method

.method public indexOfKey(Ljava/lang/Object;)I
    .locals 1
    .param p1, "key"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 283
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    if-nez p1, :cond_0

    #@2
    invoke-virtual {p0}, Landroid/support/v4/util/SimpleArrayMap;->indexOfNull()I

    #@5
    move-result v0

    #@6
    :goto_0
    return v0

    #@7
    :cond_0
    invoke-virtual {p1}, Ljava/lang/Object;->hashCode()I

    #@a
    move-result v0

    #@b
    invoke-virtual {p0, p1, v0}, Landroid/support/v4/util/SimpleArrayMap;->indexOf(Ljava/lang/Object;I)I

    #@e
    move-result v0

    #@f
    goto :goto_0
.end method

.method indexOfNull()I
    .locals 6

    #@0
    .prologue
    .line 100
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    iget v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@2
    .line 103
    .local v0, "N":I
    if-nez v0, :cond_1

    #@4
    .line 104
    const/4 v3, -0x1

    #@5
    .line 134
    :cond_0
    :goto_0
    return v3

    #@6
    .line 107
    :cond_1
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@8
    const/4 v5, 0x0

    #@9
    invoke-static {v4, v0, v5}, Landroid/support/v4/util/ContainerHelpers;->binarySearch([III)I

    #@c
    move-result v3

    #@d
    .line 110
    .local v3, "index":I
    if-ltz v3, :cond_0

    #@f
    .line 115
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@11
    shl-int/lit8 v5, v3, 0x1

    #@13
    aget-object v4, v4, v5

    #@15
    if-eqz v4, :cond_0

    #@17
    .line 121
    add-int/lit8 v1, v3, 0x1

    #@19
    .local v1, "end":I
    :goto_1
    if-ge v1, v0, :cond_3

    #@1b
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@1d
    aget v4, v4, v1

    #@1f
    if-nez v4, :cond_3

    #@21
    .line 122
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@23
    shl-int/lit8 v5, v1, 0x1

    #@25
    aget-object v4, v4, v5

    #@27
    if-nez v4, :cond_2

    #@29
    move v3, v1

    #@2a
    goto :goto_0

    #@2b
    .line 121
    :cond_2
    add-int/lit8 v1, v1, 0x1

    #@2d
    goto :goto_1

    #@2e
    .line 126
    :cond_3
    add-int/lit8 v2, v3, -0x1

    #@30
    .local v2, "i":I
    :goto_2
    if-ltz v2, :cond_5

    #@32
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@34
    aget v4, v4, v2

    #@36
    if-nez v4, :cond_5

    #@38
    .line 127
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@3a
    shl-int/lit8 v5, v2, 0x1

    #@3c
    aget-object v4, v4, v5

    #@3e
    if-nez v4, :cond_4

    #@40
    move v3, v2

    #@41
    goto :goto_0

    #@42
    .line 126
    :cond_4
    add-int/lit8 v2, v2, -0x1

    #@44
    goto :goto_2

    #@45
    .line 134
    :cond_5
    xor-int/lit8 v3, v1, -0x1

    #@47
    goto :goto_0
.end method

.method indexOfValue(Ljava/lang/Object;)I
    .locals 4
    .param p1, "value"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 287
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    iget v3, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@2
    mul-int/lit8 v0, v3, 0x2

    #@4
    .line 288
    .local v0, "N":I
    iget-object v1, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@6
    .line 289
    .local v1, "array":[Ljava/lang/Object;
    if-nez p1, :cond_1

    #@8
    .line 290
    const/4 v2, 0x1

    #@9
    .local v2, "i":I
    :goto_0
    if-ge v2, v0, :cond_3

    #@b
    .line 291
    aget-object v3, v1, v2

    #@d
    if-nez v3, :cond_0

    #@f
    .line 292
    shr-int/lit8 v3, v2, 0x1

    #@11
    .line 302
    :goto_1
    return v3

    #@12
    .line 290
    :cond_0
    add-int/lit8 v2, v2, 0x2

    #@14
    goto :goto_0

    #@15
    .line 296
    .end local v2    # "i":I
    :cond_1
    const/4 v2, 0x1

    #@16
    .restart local v2    # "i":I
    :goto_2
    if-ge v2, v0, :cond_3

    #@18
    .line 297
    aget-object v3, v1, v2

    #@1a
    invoke-virtual {p1, v3}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z

    #@1d
    move-result v3

    #@1e
    if-eqz v3, :cond_2

    #@20
    .line 298
    shr-int/lit8 v3, v2, 0x1

    #@22
    goto :goto_1

    #@23
    .line 296
    :cond_2
    add-int/lit8 v2, v2, 0x2

    #@25
    goto :goto_2

    #@26
    .line 302
    :cond_3
    const/4 v3, -0x1

    #@27
    goto :goto_1
.end method

.method public isEmpty()Z
    .locals 1

    #@0
    .prologue
    .line 362
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    iget v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@2
    if-gtz v0, :cond_0

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

.method public keyAt(I)Ljava/lang/Object;
    .locals 2
    .param p1, "index"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I)TK;"
        }
    .end annotation

    #@0
    .prologue
    .line 333
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    iget-object v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@2
    shl-int/lit8 v1, p1, 0x1

    #@4
    aget-object v0, v0, v1

    #@6
    return-object v0
.end method

.method public put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    .locals 11
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TK;TV;)TV;"
        }
    .end annotation

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    .local p1, "key":Ljava/lang/Object;, "TK;"
    .local p2, "value":Ljava/lang/Object;, "TV;"
    const/16 v2, 0x8

    #@2
    const/4 v6, 0x4

    #@3
    const/4 v9, 0x0

    #@4
    .line 376
    if-nez p1, :cond_0

    #@6
    .line 377
    const/4 v0, 0x0

    #@7
    .line 378
    .local v0, "hash":I
    invoke-virtual {p0}, Landroid/support/v4/util/SimpleArrayMap;->indexOfNull()I

    #@a
    move-result v1

    #@b
    .line 383
    .local v1, "index":I
    :goto_0
    if-ltz v1, :cond_1

    #@d
    .line 384
    shl-int/lit8 v6, v1, 0x1

    #@f
    add-int/lit8 v1, v6, 0x1

    #@11
    .line 385
    iget-object v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@13
    aget-object v5, v6, v1

    #@15
    .line 386
    .local v5, "old":Ljava/lang/Object;, "TV;"
    iget-object v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@17
    aput-object p2, v6, v1

    #@19
    .line 421
    .end local v5    # "old":Ljava/lang/Object;, "TV;"
    :goto_1
    return-object v5

    #@1a
    .line 380
    .end local v0    # "hash":I
    .end local v1    # "index":I
    :cond_0
    invoke-virtual {p1}, Ljava/lang/Object;->hashCode()I

    #@1d
    move-result v0

    #@1e
    .line 381
    .restart local v0    # "hash":I
    invoke-virtual {p0, p1, v0}, Landroid/support/v4/util/SimpleArrayMap;->indexOf(Ljava/lang/Object;I)I

    #@21
    move-result v1

    #@22
    .restart local v1    # "index":I
    goto :goto_0

    #@23
    .line 390
    :cond_1
    xor-int/lit8 v1, v1, -0x1

    #@25
    .line 391
    iget v7, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@27
    iget-object v8, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@29
    array-length v8, v8

    #@2a
    if-lt v7, v8, :cond_4

    #@2c
    .line 392
    iget v7, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@2e
    if-lt v7, v2, :cond_6

    #@30
    iget v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@32
    iget v7, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@34
    shr-int/lit8 v7, v7, 0x1

    #@36
    add-int v2, v6, v7

    #@38
    .line 397
    .local v2, "n":I
    :cond_2
    :goto_2
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@3a
    .line 398
    .local v4, "ohashes":[I
    iget-object v3, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@3c
    .line 399
    .local v3, "oarray":[Ljava/lang/Object;
    invoke-direct {p0, v2}, Landroid/support/v4/util/SimpleArrayMap;->allocArrays(I)V

    #@3f
    .line 401
    iget-object v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@41
    array-length v6, v6

    #@42
    if-lez v6, :cond_3

    #@44
    .line 403
    iget-object v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@46
    array-length v7, v4

    #@47
    invoke-static {v4, v9, v6, v9, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@4a
    .line 404
    iget-object v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@4c
    array-length v7, v3

    #@4d
    invoke-static {v3, v9, v6, v9, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@50
    .line 407
    :cond_3
    iget v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@52
    invoke-static {v4, v3, v6}, Landroid/support/v4/util/SimpleArrayMap;->freeArrays([I[Ljava/lang/Object;I)V

    #@55
    .line 410
    .end local v2    # "n":I
    .end local v3    # "oarray":[Ljava/lang/Object;
    .end local v4    # "ohashes":[I
    :cond_4
    iget v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@57
    if-ge v1, v6, :cond_5

    #@59
    .line 413
    iget-object v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@5b
    iget-object v7, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@5d
    add-int/lit8 v8, v1, 0x1

    #@5f
    iget v9, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@61
    sub-int/2addr v9, v1

    #@62
    invoke-static {v6, v1, v7, v8, v9}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@65
    .line 414
    iget-object v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@67
    shl-int/lit8 v7, v1, 0x1

    #@69
    iget-object v8, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@6b
    add-int/lit8 v9, v1, 0x1

    #@6d
    shl-int/lit8 v9, v9, 0x1

    #@6f
    iget v10, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@71
    sub-int/2addr v10, v1

    #@72
    shl-int/lit8 v10, v10, 0x1

    #@74
    invoke-static {v6, v7, v8, v9, v10}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@77
    .line 417
    :cond_5
    iget-object v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@79
    aput v0, v6, v1

    #@7b
    .line 418
    iget-object v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@7d
    shl-int/lit8 v7, v1, 0x1

    #@7f
    aput-object p1, v6, v7

    #@81
    .line 419
    iget-object v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@83
    shl-int/lit8 v7, v1, 0x1

    #@85
    add-int/lit8 v7, v7, 0x1

    #@87
    aput-object p2, v6, v7

    #@89
    .line 420
    iget v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@8b
    add-int/lit8 v6, v6, 0x1

    #@8d
    iput v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@8f
    .line 421
    const/4 v5, 0x0

    #@90
    goto :goto_1

    #@91
    .line 392
    :cond_6
    iget v7, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@93
    if-ge v7, v6, :cond_2

    #@95
    move v2, v6

    #@96
    goto :goto_2
.end method

.method public putAll(Landroid/support/v4/util/SimpleArrayMap;)V
    .locals 6
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v4/util/SimpleArrayMap",
            "<+TK;+TV;>;)V"
        }
    .end annotation

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    .local p1, "array":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<+TK;+TV;>;"
    const/4 v5, 0x0

    #@1
    .line 429
    iget v0, p1, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@3
    .line 430
    .local v0, "N":I
    iget v2, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@5
    add-int/2addr v2, v0

    #@6
    invoke-virtual {p0, v2}, Landroid/support/v4/util/SimpleArrayMap;->ensureCapacity(I)V

    #@9
    .line 431
    iget v2, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@b
    if-nez v2, :cond_1

    #@d
    .line 432
    if-lez v0, :cond_0

    #@f
    .line 433
    iget-object v2, p1, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@11
    iget-object v3, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@13
    invoke-static {v2, v5, v3, v5, v0}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@16
    .line 434
    iget-object v2, p1, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@18
    iget-object v3, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@1a
    shl-int/lit8 v4, v0, 0x1

    #@1c
    invoke-static {v2, v5, v3, v5, v4}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@1f
    .line 435
    iput v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@21
    .line 442
    :cond_0
    return-void

    #@22
    .line 438
    :cond_1
    const/4 v1, 0x0

    #@23
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_0

    #@25
    .line 439
    invoke-virtual {p1, v1}, Landroid/support/v4/util/SimpleArrayMap;->keyAt(I)Ljava/lang/Object;

    #@28
    move-result-object v2

    #@29
    invoke-virtual {p1, v1}, Landroid/support/v4/util/SimpleArrayMap;->valueAt(I)Ljava/lang/Object;

    #@2c
    move-result-object v3

    #@2d
    invoke-virtual {p0, v2, v3}, Landroid/support/v4/util/SimpleArrayMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@30
    .line 438
    add-int/lit8 v1, v1, 0x1

    #@32
    goto :goto_0
.end method

.method public remove(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 2
    .param p1, "key"    # Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Object;",
            ")TV;"
        }
    .end annotation

    #@0
    .prologue
    .line 451
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    invoke-virtual {p0, p1}, Landroid/support/v4/util/SimpleArrayMap;->indexOfKey(Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    .line 452
    .local v0, "index":I
    if-ltz v0, :cond_0

    #@6
    .line 453
    invoke-virtual {p0, v0}, Landroid/support/v4/util/SimpleArrayMap;->removeAt(I)Ljava/lang/Object;

    #@9
    move-result-object v1

    #@a
    .line 456
    :goto_0
    return-object v1

    #@b
    :cond_0
    const/4 v1, 0x0

    #@c
    goto :goto_0
.end method

.method public removeAt(I)Ljava/lang/Object;
    .locals 10
    .param p1, "index"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I)TV;"
        }
    .end annotation

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    const/4 v9, 0x0

    #@1
    const/16 v0, 0x8

    #@3
    const/4 v7, 0x0

    #@4
    .line 465
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@6
    shl-int/lit8 v5, p1, 0x1

    #@8
    add-int/lit8 v5, v5, 0x1

    #@a
    aget-object v3, v4, v5

    #@c
    .line 466
    .local v3, "old":Ljava/lang/Object;
    iget v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@e
    const/4 v5, 0x1

    #@f
    if-gt v4, v5, :cond_1

    #@11
    .line 469
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@13
    iget-object v5, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@15
    iget v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@17
    invoke-static {v4, v5, v6}, Landroid/support/v4/util/SimpleArrayMap;->freeArrays([I[Ljava/lang/Object;I)V

    #@1a
    .line 470
    sget-object v4, Landroid/support/v4/util/ContainerHelpers;->EMPTY_INTS:[I

    #@1c
    iput-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@1e
    .line 471
    sget-object v4, Landroid/support/v4/util/ContainerHelpers;->EMPTY_OBJECTS:[Ljava/lang/Object;

    #@20
    iput-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@22
    .line 472
    iput v7, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@24
    .line 512
    :cond_0
    :goto_0
    return-object v3

    #@25
    .line 474
    :cond_1
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@27
    array-length v4, v4

    #@28
    if-le v4, v0, :cond_4

    #@2a
    iget v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@2c
    iget-object v5, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@2e
    array-length v5, v5

    #@2f
    div-int/lit8 v5, v5, 0x3

    #@31
    if-ge v4, v5, :cond_4

    #@33
    .line 478
    iget v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@35
    if-le v4, v0, :cond_2

    #@37
    iget v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@39
    iget v5, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@3b
    shr-int/lit8 v5, v5, 0x1

    #@3d
    add-int v0, v4, v5

    #@3f
    .line 482
    .local v0, "n":I
    :cond_2
    iget-object v2, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@41
    .line 483
    .local v2, "ohashes":[I
    iget-object v1, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@43
    .line 484
    .local v1, "oarray":[Ljava/lang/Object;
    invoke-direct {p0, v0}, Landroid/support/v4/util/SimpleArrayMap;->allocArrays(I)V

    #@46
    .line 486
    iget v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@48
    add-int/lit8 v4, v4, -0x1

    #@4a
    iput v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@4c
    .line 487
    if-lez p1, :cond_3

    #@4e
    .line 489
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@50
    invoke-static {v2, v7, v4, v7, p1}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@53
    .line 490
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@55
    shl-int/lit8 v5, p1, 0x1

    #@57
    invoke-static {v1, v7, v4, v7, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@5a
    .line 492
    :cond_3
    iget v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@5c
    if-ge p1, v4, :cond_0

    #@5e
    .line 495
    add-int/lit8 v4, p1, 0x1

    #@60
    iget-object v5, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@62
    iget v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@64
    sub-int/2addr v6, p1

    #@65
    invoke-static {v2, v4, v5, p1, v6}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@68
    .line 496
    add-int/lit8 v4, p1, 0x1

    #@6a
    shl-int/lit8 v4, v4, 0x1

    #@6c
    iget-object v5, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@6e
    shl-int/lit8 v6, p1, 0x1

    #@70
    iget v7, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@72
    sub-int/2addr v7, p1

    #@73
    shl-int/lit8 v7, v7, 0x1

    #@75
    invoke-static {v1, v4, v5, v6, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@78
    goto :goto_0

    #@79
    .line 500
    .end local v0    # "n":I
    .end local v1    # "oarray":[Ljava/lang/Object;
    .end local v2    # "ohashes":[I
    :cond_4
    iget v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@7b
    add-int/lit8 v4, v4, -0x1

    #@7d
    iput v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@7f
    .line 501
    iget v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@81
    if-ge p1, v4, :cond_5

    #@83
    .line 504
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@85
    add-int/lit8 v5, p1, 0x1

    #@87
    iget-object v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mHashes:[I

    #@89
    iget v7, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@8b
    sub-int/2addr v7, p1

    #@8c
    invoke-static {v4, v5, v6, p1, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@8f
    .line 505
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@91
    add-int/lit8 v5, p1, 0x1

    #@93
    shl-int/lit8 v5, v5, 0x1

    #@95
    iget-object v6, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@97
    shl-int/lit8 v7, p1, 0x1

    #@99
    iget v8, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@9b
    sub-int/2addr v8, p1

    #@9c
    shl-int/lit8 v8, v8, 0x1

    #@9e
    invoke-static {v4, v5, v6, v7, v8}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@a1
    .line 508
    :cond_5
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@a3
    iget v5, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@a5
    shl-int/lit8 v5, v5, 0x1

    #@a7
    aput-object v9, v4, v5

    #@a9
    .line 509
    iget-object v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@ab
    iget v5, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@ad
    shl-int/lit8 v5, v5, 0x1

    #@af
    add-int/lit8 v5, v5, 0x1

    #@b1
    aput-object v9, v4, v5

    #@b3
    goto/16 :goto_0
.end method

.method public setValueAt(ILjava/lang/Object;)Ljava/lang/Object;
    .locals 2
    .param p1, "index"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ITV;)TV;"
        }
    .end annotation

    #@0
    .prologue
    .line 352
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    .local p2, "value":Ljava/lang/Object;, "TV;"
    shl-int/lit8 v1, p1, 0x1

    #@2
    add-int/lit8 p1, v1, 0x1

    #@4
    .line 353
    iget-object v1, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@6
    aget-object v0, v1, p1

    #@8
    .line 354
    .local v0, "old":Ljava/lang/Object;, "TV;"
    iget-object v1, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@a
    aput-object p2, v1, p1

    #@c
    .line 355
    return-object v0
.end method

.method public size()I
    .locals 1

    #@0
    .prologue
    .line 519
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    iget v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@2
    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 5

    #@0
    .prologue
    .line 588
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    invoke-virtual {p0}, Landroid/support/v4/util/SimpleArrayMap;->isEmpty()Z

    #@3
    move-result v4

    #@4
    if-eqz v4, :cond_0

    #@6
    .line 589
    const-string v4, "{}"

    #@8
    .line 613
    :goto_0
    return-object v4

    #@9
    .line 592
    :cond_0
    new-instance v0, Ljava/lang/StringBuilder;

    #@b
    iget v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@d
    mul-int/lit8 v4, v4, 0x1c

    #@f
    invoke-direct {v0, v4}, Ljava/lang/StringBuilder;-><init>(I)V

    #@12
    .line 593
    .local v0, "buffer":Ljava/lang/StringBuilder;
    const/16 v4, 0x7b

    #@14
    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@17
    .line 594
    const/4 v1, 0x0

    #@18
    .local v1, "i":I
    :goto_1
    iget v4, p0, Landroid/support/v4/util/SimpleArrayMap;->mSize:I

    #@1a
    if-ge v1, v4, :cond_4

    #@1c
    .line 595
    if-lez v1, :cond_1

    #@1e
    .line 596
    const-string v4, ", "

    #@20
    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@23
    .line 598
    :cond_1
    invoke-virtual {p0, v1}, Landroid/support/v4/util/SimpleArrayMap;->keyAt(I)Ljava/lang/Object;

    #@26
    move-result-object v2

    #@27
    .line 599
    .local v2, "key":Ljava/lang/Object;
    if-eq v2, p0, :cond_2

    #@29
    .line 600
    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@2c
    .line 604
    :goto_2
    const/16 v4, 0x3d

    #@2e
    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@31
    .line 605
    invoke-virtual {p0, v1}, Landroid/support/v4/util/SimpleArrayMap;->valueAt(I)Ljava/lang/Object;

    #@34
    move-result-object v3

    #@35
    .line 606
    .local v3, "value":Ljava/lang/Object;
    if-eq v3, p0, :cond_3

    #@37
    .line 607
    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@3a
    .line 594
    :goto_3
    add-int/lit8 v1, v1, 0x1

    #@3c
    goto :goto_1

    #@3d
    .line 602
    .end local v3    # "value":Ljava/lang/Object;
    :cond_2
    const-string v4, "(this Map)"

    #@3f
    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@42
    goto :goto_2

    #@43
    .line 609
    .restart local v3    # "value":Ljava/lang/Object;
    :cond_3
    const-string v4, "(this Map)"

    #@45
    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@48
    goto :goto_3

    #@49
    .line 612
    .end local v2    # "key":Ljava/lang/Object;
    .end local v3    # "value":Ljava/lang/Object;
    :cond_4
    const/16 v4, 0x7d

    #@4b
    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@4e
    .line 613
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@51
    move-result-object v4

    #@52
    goto :goto_0
.end method

.method public valueAt(I)Ljava/lang/Object;
    .locals 2
    .param p1, "index"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I)TV;"
        }
    .end annotation

    #@0
    .prologue
    .line 342
    .local p0, "this":Landroid/support/v4/util/SimpleArrayMap;, "Landroid/support/v4/util/SimpleArrayMap<TK;TV;>;"
    iget-object v0, p0, Landroid/support/v4/util/SimpleArrayMap;->mArray:[Ljava/lang/Object;

    #@2
    shl-int/lit8 v1, p1, 0x1

    #@4
    add-int/lit8 v1, v1, 0x1

    #@6
    aget-object v0, v0, v1

    #@8
    return-object v0
.end method
