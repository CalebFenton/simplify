.class public Landroid/support/v4/util/LruCache;
.super Ljava/lang/Object;
.source "LruCache.java"


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


# instance fields
.field private createCount:I

.field private evictionCount:I

.field private hitCount:I

.field private final map:Ljava/util/LinkedHashMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/LinkedHashMap",
            "<TK;TV;>;"
        }
    .end annotation
.end field

.field private maxSize:I

.field private missCount:I

.field private putCount:I

.field private size:I


# direct methods
.method public constructor <init>(I)V
    .locals 4
    .param p1, "maxSize"    # I

    #@0
    .prologue
    .line 47
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 48
    if-gtz p1, :cond_0

    #@5
    .line 49
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@7
    const-string v1, "maxSize <= 0"

    #@9
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@c
    throw v0

    #@d
    .line 51
    :cond_0
    iput p1, p0, Landroid/support/v4/util/LruCache;->maxSize:I

    #@f
    .line 52
    new-instance v0, Ljava/util/LinkedHashMap;

    #@11
    const/4 v1, 0x0

    #@12
    const/high16 v2, 0x3f400000    # 0.75f

    #@14
    const/4 v3, 0x1

    #@15
    invoke-direct {v0, v1, v2, v3}, Ljava/util/LinkedHashMap;-><init>(IFZ)V

    #@18
    iput-object v0, p0, Landroid/support/v4/util/LruCache;->map:Ljava/util/LinkedHashMap;

    #@1a
    .line 53
    return-void
.end method

.method private safeSizeOf(Ljava/lang/Object;Ljava/lang/Object;)I
    .locals 4
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TK;TV;)I"
        }
    .end annotation

    #@0
    .prologue
    .line 249
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    .local p1, "key":Ljava/lang/Object;, "TK;"
    .local p2, "value":Ljava/lang/Object;, "TV;"
    invoke-virtual {p0, p1, p2}, Landroid/support/v4/util/LruCache;->sizeOf(Ljava/lang/Object;Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    .line 250
    .local v0, "result":I
    if-gez v0, :cond_0

    #@6
    .line 251
    new-instance v1, Ljava/lang/IllegalStateException;

    #@8
    new-instance v2, Ljava/lang/StringBuilder;

    #@a
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@d
    const-string v3, "Negative size: "

    #@f
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v2

    #@13
    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@16
    move-result-object v2

    #@17
    const-string v3, "="

    #@19
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v2

    #@1d
    invoke-virtual {v2, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@20
    move-result-object v2

    #@21
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@24
    move-result-object v2

    #@25
    invoke-direct {v1, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@28
    throw v1

    #@29
    .line 253
    :cond_0
    return v0
.end method


# virtual methods
.method protected create(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TK;)TV;"
        }
    .end annotation

    #@0
    .prologue
    .line 245
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    .local p1, "key":Ljava/lang/Object;, "TK;"
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public final declared-synchronized createCount()I
    .locals 1

    #@0
    .prologue
    .line 312
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    monitor-enter p0

    #@1
    :try_start_0
    iget v0, p0, Landroid/support/v4/util/LruCache;->createCount:I
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@3
    monitor-exit p0

    #@4
    return v0

    #@5
    :catchall_0
    move-exception v0

    #@6
    monitor-exit p0

    #@7
    throw v0
.end method

.method protected entryRemoved(ZLjava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    .locals 0
    .param p1, "evicted"    # Z
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ZTK;TV;TV;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 227
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    .local p2, "key":Ljava/lang/Object;, "TK;"
    .local p3, "oldValue":Ljava/lang/Object;, "TV;"
    .local p4, "newValue":Ljava/lang/Object;, "TV;"
    return-void
.end method

.method public final evictAll()V
    .locals 1

    #@0
    .prologue
    .line 271
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    const/4 v0, -0x1

    #@1
    invoke-virtual {p0, v0}, Landroid/support/v4/util/LruCache;->trimToSize(I)V

    #@4
    .line 272
    return-void
.end method

.method public final declared-synchronized evictionCount()I
    .locals 1

    #@0
    .prologue
    .line 326
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    monitor-enter p0

    #@1
    :try_start_0
    iget v0, p0, Landroid/support/v4/util/LruCache;->evictionCount:I
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@3
    monitor-exit p0

    #@4
    return v0

    #@5
    :catchall_0
    move-exception v0

    #@6
    monitor-exit p0

    #@7
    throw v0
.end method

.method public final get(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 4
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TK;)TV;"
        }
    .end annotation

    #@0
    .prologue
    .line 78
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    .local p1, "key":Ljava/lang/Object;, "TK;"
    if-nez p1, :cond_0

    #@2
    .line 79
    new-instance v2, Ljava/lang/NullPointerException;

    #@4
    const-string v3, "key == null"

    #@6
    invoke-direct {v2, v3}, Ljava/lang/NullPointerException;-><init>(Ljava/lang/String;)V

    #@9
    throw v2

    #@a
    .line 83
    :cond_0
    monitor-enter p0

    #@b
    .line 84
    :try_start_0
    iget-object v2, p0, Landroid/support/v4/util/LruCache;->map:Ljava/util/LinkedHashMap;

    #@d
    invoke-virtual {v2, p1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@10
    move-result-object v1

    #@11
    .line 85
    .local v1, "mapValue":Ljava/lang/Object;, "TV;"
    if-eqz v1, :cond_1

    #@13
    .line 86
    iget v2, p0, Landroid/support/v4/util/LruCache;->hitCount:I

    #@15
    add-int/lit8 v2, v2, 0x1

    #@17
    iput v2, p0, Landroid/support/v4/util/LruCache;->hitCount:I

    #@19
    .line 87
    monitor-exit p0

    #@1a
    move-object v0, v1

    #@1b
    .line 121
    :goto_0
    return-object v0

    #@1c
    .line 89
    :cond_1
    iget v2, p0, Landroid/support/v4/util/LruCache;->missCount:I

    #@1e
    add-int/lit8 v2, v2, 0x1

    #@20
    iput v2, p0, Landroid/support/v4/util/LruCache;->missCount:I

    #@22
    .line 90
    monitor-exit p0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@23
    .line 99
    invoke-virtual {p0, p1}, Landroid/support/v4/util/LruCache;->create(Ljava/lang/Object;)Ljava/lang/Object;

    #@26
    move-result-object v0

    #@27
    .line 100
    .local v0, "createdValue":Ljava/lang/Object;, "TV;"
    if-nez v0, :cond_2

    #@29
    .line 101
    const/4 v0, 0x0

    #@2a
    goto :goto_0

    #@2b
    .line 90
    .end local v0    # "createdValue":Ljava/lang/Object;, "TV;"
    .end local v1    # "mapValue":Ljava/lang/Object;, "TV;"
    :catchall_0
    move-exception v2

    #@2c
    :try_start_1
    monitor-exit p0
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@2d
    throw v2

    #@2e
    .line 104
    .restart local v0    # "createdValue":Ljava/lang/Object;, "TV;"
    .restart local v1    # "mapValue":Ljava/lang/Object;, "TV;"
    :cond_2
    monitor-enter p0

    #@2f
    .line 105
    :try_start_2
    iget v2, p0, Landroid/support/v4/util/LruCache;->createCount:I

    #@31
    add-int/lit8 v2, v2, 0x1

    #@33
    iput v2, p0, Landroid/support/v4/util/LruCache;->createCount:I

    #@35
    .line 106
    iget-object v2, p0, Landroid/support/v4/util/LruCache;->map:Ljava/util/LinkedHashMap;

    #@37
    invoke-virtual {v2, p1, v0}, Ljava/util/LinkedHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@3a
    move-result-object v1

    #@3b
    .line 108
    if-eqz v1, :cond_3

    #@3d
    .line 110
    iget-object v2, p0, Landroid/support/v4/util/LruCache;->map:Ljava/util/LinkedHashMap;

    #@3f
    invoke-virtual {v2, p1, v1}, Ljava/util/LinkedHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@42
    .line 114
    :goto_1
    monitor-exit p0
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_1

    #@43
    .line 116
    if-eqz v1, :cond_4

    #@45
    .line 117
    const/4 v2, 0x0

    #@46
    invoke-virtual {p0, v2, p1, v0, v1}, Landroid/support/v4/util/LruCache;->entryRemoved(ZLjava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V

    #@49
    move-object v0, v1

    #@4a
    .line 118
    goto :goto_0

    #@4b
    .line 112
    :cond_3
    :try_start_3
    iget v2, p0, Landroid/support/v4/util/LruCache;->size:I

    #@4d
    invoke-direct {p0, p1, v0}, Landroid/support/v4/util/LruCache;->safeSizeOf(Ljava/lang/Object;Ljava/lang/Object;)I

    #@50
    move-result v3

    #@51
    add-int/2addr v2, v3

    #@52
    iput v2, p0, Landroid/support/v4/util/LruCache;->size:I

    #@54
    goto :goto_1

    #@55
    .line 114
    :catchall_1
    move-exception v2

    #@56
    monitor-exit p0
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_1

    #@57
    throw v2

    #@58
    .line 120
    :cond_4
    iget v2, p0, Landroid/support/v4/util/LruCache;->maxSize:I

    #@5a
    invoke-virtual {p0, v2}, Landroid/support/v4/util/LruCache;->trimToSize(I)V

    #@5d
    goto :goto_0
.end method

.method public final declared-synchronized hitCount()I
    .locals 1

    #@0
    .prologue
    .line 297
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    monitor-enter p0

    #@1
    :try_start_0
    iget v0, p0, Landroid/support/v4/util/LruCache;->hitCount:I
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@3
    monitor-exit p0

    #@4
    return v0

    #@5
    :catchall_0
    move-exception v0

    #@6
    monitor-exit p0

    #@7
    throw v0
.end method

.method public final declared-synchronized maxSize()I
    .locals 1

    #@0
    .prologue
    .line 289
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    monitor-enter p0

    #@1
    :try_start_0
    iget v0, p0, Landroid/support/v4/util/LruCache;->maxSize:I
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@3
    monitor-exit p0

    #@4
    return v0

    #@5
    :catchall_0
    move-exception v0

    #@6
    monitor-exit p0

    #@7
    throw v0
.end method

.method public final declared-synchronized missCount()I
    .locals 1

    #@0
    .prologue
    .line 305
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    monitor-enter p0

    #@1
    :try_start_0
    iget v0, p0, Landroid/support/v4/util/LruCache;->missCount:I
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@3
    monitor-exit p0

    #@4
    return v0

    #@5
    :catchall_0
    move-exception v0

    #@6
    monitor-exit p0

    #@7
    throw v0
.end method

.method public final put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TK;TV;)TV;"
        }
    .end annotation

    #@0
    .prologue
    .line 132
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    .local p1, "key":Ljava/lang/Object;, "TK;"
    .local p2, "value":Ljava/lang/Object;, "TV;"
    if-eqz p1, :cond_0

    #@2
    if-nez p2, :cond_1

    #@4
    .line 133
    :cond_0
    new-instance v1, Ljava/lang/NullPointerException;

    #@6
    const-string v2, "key == null || value == null"

    #@8
    invoke-direct {v1, v2}, Ljava/lang/NullPointerException;-><init>(Ljava/lang/String;)V

    #@b
    throw v1

    #@c
    .line 137
    :cond_1
    monitor-enter p0

    #@d
    .line 138
    :try_start_0
    iget v1, p0, Landroid/support/v4/util/LruCache;->putCount:I

    #@f
    add-int/lit8 v1, v1, 0x1

    #@11
    iput v1, p0, Landroid/support/v4/util/LruCache;->putCount:I

    #@13
    .line 139
    iget v1, p0, Landroid/support/v4/util/LruCache;->size:I

    #@15
    invoke-direct {p0, p1, p2}, Landroid/support/v4/util/LruCache;->safeSizeOf(Ljava/lang/Object;Ljava/lang/Object;)I

    #@18
    move-result v2

    #@19
    add-int/2addr v1, v2

    #@1a
    iput v1, p0, Landroid/support/v4/util/LruCache;->size:I

    #@1c
    .line 140
    iget-object v1, p0, Landroid/support/v4/util/LruCache;->map:Ljava/util/LinkedHashMap;

    #@1e
    invoke-virtual {v1, p1, p2}, Ljava/util/LinkedHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@21
    move-result-object v0

    #@22
    .line 141
    .local v0, "previous":Ljava/lang/Object;, "TV;"
    if-eqz v0, :cond_2

    #@24
    .line 142
    iget v1, p0, Landroid/support/v4/util/LruCache;->size:I

    #@26
    invoke-direct {p0, p1, v0}, Landroid/support/v4/util/LruCache;->safeSizeOf(Ljava/lang/Object;Ljava/lang/Object;)I

    #@29
    move-result v2

    #@2a
    sub-int/2addr v1, v2

    #@2b
    iput v1, p0, Landroid/support/v4/util/LruCache;->size:I

    #@2d
    .line 144
    :cond_2
    monitor-exit p0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@2e
    .line 146
    if-eqz v0, :cond_3

    #@30
    .line 147
    const/4 v1, 0x0

    #@31
    invoke-virtual {p0, v1, p1, v0, p2}, Landroid/support/v4/util/LruCache;->entryRemoved(ZLjava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V

    #@34
    .line 150
    :cond_3
    iget v1, p0, Landroid/support/v4/util/LruCache;->maxSize:I

    #@36
    invoke-virtual {p0, v1}, Landroid/support/v4/util/LruCache;->trimToSize(I)V

    #@39
    .line 151
    return-object v0

    #@3a
    .line 144
    .end local v0    # "previous":Ljava/lang/Object;, "TV;"
    :catchall_0
    move-exception v1

    #@3b
    :try_start_1
    monitor-exit p0
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@3c
    throw v1
.end method

.method public final declared-synchronized putCount()I
    .locals 1

    #@0
    .prologue
    .line 319
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    monitor-enter p0

    #@1
    :try_start_0
    iget v0, p0, Landroid/support/v4/util/LruCache;->putCount:I
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@3
    monitor-exit p0

    #@4
    return v0

    #@5
    :catchall_0
    move-exception v0

    #@6
    monitor-exit p0

    #@7
    throw v0
.end method

.method public final remove(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TK;)TV;"
        }
    .end annotation

    #@0
    .prologue
    .line 193
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    .local p1, "key":Ljava/lang/Object;, "TK;"
    if-nez p1, :cond_0

    #@2
    .line 194
    new-instance v1, Ljava/lang/NullPointerException;

    #@4
    const-string v2, "key == null"

    #@6
    invoke-direct {v1, v2}, Ljava/lang/NullPointerException;-><init>(Ljava/lang/String;)V

    #@9
    throw v1

    #@a
    .line 198
    :cond_0
    monitor-enter p0

    #@b
    .line 199
    :try_start_0
    iget-object v1, p0, Landroid/support/v4/util/LruCache;->map:Ljava/util/LinkedHashMap;

    #@d
    invoke-virtual {v1, p1}, Ljava/util/LinkedHashMap;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    #@10
    move-result-object v0

    #@11
    .line 200
    .local v0, "previous":Ljava/lang/Object;, "TV;"
    if-eqz v0, :cond_1

    #@13
    .line 201
    iget v1, p0, Landroid/support/v4/util/LruCache;->size:I

    #@15
    invoke-direct {p0, p1, v0}, Landroid/support/v4/util/LruCache;->safeSizeOf(Ljava/lang/Object;Ljava/lang/Object;)I

    #@18
    move-result v2

    #@19
    sub-int/2addr v1, v2

    #@1a
    iput v1, p0, Landroid/support/v4/util/LruCache;->size:I

    #@1c
    .line 203
    :cond_1
    monitor-exit p0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@1d
    .line 205
    if-eqz v0, :cond_2

    #@1f
    .line 206
    const/4 v1, 0x0

    #@20
    const/4 v2, 0x0

    #@21
    invoke-virtual {p0, v1, p1, v0, v2}, Landroid/support/v4/util/LruCache;->entryRemoved(ZLjava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V

    #@24
    .line 209
    :cond_2
    return-object v0

    #@25
    .line 203
    .end local v0    # "previous":Ljava/lang/Object;, "TV;"
    :catchall_0
    move-exception v1

    #@26
    :try_start_1
    monitor-exit p0
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@27
    throw v1
.end method

.method public resize(I)V
    .locals 2
    .param p1, "maxSize"    # I

    #@0
    .prologue
    .line 61
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    if-gtz p1, :cond_0

    #@2
    .line 62
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@4
    const-string v1, "maxSize <= 0"

    #@6
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@9
    throw v0

    #@a
    .line 65
    :cond_0
    monitor-enter p0

    #@b
    .line 66
    :try_start_0
    iput p1, p0, Landroid/support/v4/util/LruCache;->maxSize:I

    #@d
    .line 67
    monitor-exit p0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@e
    .line 68
    invoke-virtual {p0, p1}, Landroid/support/v4/util/LruCache;->trimToSize(I)V

    #@11
    .line 69
    return-void

    #@12
    .line 67
    :catchall_0
    move-exception v0

    #@13
    :try_start_1
    monitor-exit p0
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@14
    throw v0
.end method

.method public final declared-synchronized size()I
    .locals 1

    #@0
    .prologue
    .line 280
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    monitor-enter p0

    #@1
    :try_start_0
    iget v0, p0, Landroid/support/v4/util/LruCache;->size:I
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@3
    monitor-exit p0

    #@4
    return v0

    #@5
    :catchall_0
    move-exception v0

    #@6
    monitor-exit p0

    #@7
    throw v0
.end method

.method protected sizeOf(Ljava/lang/Object;Ljava/lang/Object;)I
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TK;TV;)I"
        }
    .end annotation

    #@0
    .prologue
    .line 264
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    .local p1, "key":Ljava/lang/Object;, "TK;"
    .local p2, "value":Ljava/lang/Object;, "TV;"
    const/4 v0, 0x1

    #@1
    return v0
.end method

.method public final declared-synchronized snapshot()Ljava/util/Map;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/Map",
            "<TK;TV;>;"
        }
    .end annotation

    #@0
    .prologue
    .line 334
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    monitor-enter p0

    #@1
    :try_start_0
    new-instance v0, Ljava/util/LinkedHashMap;

    #@3
    iget-object v1, p0, Landroid/support/v4/util/LruCache;->map:Ljava/util/LinkedHashMap;

    #@5
    invoke-direct {v0, v1}, Ljava/util/LinkedHashMap;-><init>(Ljava/util/Map;)V
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@8
    monitor-exit p0

    #@9
    return-object v0

    #@a
    :catchall_0
    move-exception v0

    #@b
    monitor-exit p0

    #@c
    throw v0
.end method

.method public final declared-synchronized toString()Ljava/lang/String;
    .locals 6

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    const/4 v1, 0x0

    #@1
    .line 338
    monitor-enter p0

    #@2
    :try_start_0
    iget v2, p0, Landroid/support/v4/util/LruCache;->hitCount:I

    #@4
    iget v3, p0, Landroid/support/v4/util/LruCache;->missCount:I

    #@6
    add-int v0, v2, v3

    #@8
    .line 339
    .local v0, "accesses":I
    if-eqz v0, :cond_0

    #@a
    iget v2, p0, Landroid/support/v4/util/LruCache;->hitCount:I

    #@c
    mul-int/lit8 v2, v2, 0x64

    #@e
    div-int v1, v2, v0

    #@10
    .line 340
    .local v1, "hitPercent":I
    :cond_0
    const-string v2, "LruCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]"

    #@12
    const/4 v3, 0x4

    #@13
    new-array v3, v3, [Ljava/lang/Object;

    #@15
    const/4 v4, 0x0

    #@16
    iget v5, p0, Landroid/support/v4/util/LruCache;->maxSize:I

    #@18
    invoke-static {v5}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@1b
    move-result-object v5

    #@1c
    aput-object v5, v3, v4

    #@1e
    const/4 v4, 0x1

    #@1f
    iget v5, p0, Landroid/support/v4/util/LruCache;->hitCount:I

    #@21
    invoke-static {v5}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@24
    move-result-object v5

    #@25
    aput-object v5, v3, v4

    #@27
    const/4 v4, 0x2

    #@28
    iget v5, p0, Landroid/support/v4/util/LruCache;->missCount:I

    #@2a
    invoke-static {v5}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@2d
    move-result-object v5

    #@2e
    aput-object v5, v3, v4

    #@30
    const/4 v4, 0x3

    #@31
    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@34
    move-result-object v5

    #@35
    aput-object v5, v3, v4

    #@37
    invoke-static {v2, v3}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@3a
    move-result-object v2

    #@3b
    monitor-exit p0

    #@3c
    return-object v2

    #@3d
    .line 338
    .end local v0    # "accesses":I
    .end local v1    # "hitPercent":I
    :catchall_0
    move-exception v2

    #@3e
    monitor-exit p0

    #@3f
    throw v2
.end method

.method public trimToSize(I)V
    .locals 6
    .param p1, "maxSize"    # I

    #@0
    .prologue
    .line 165
    .local p0, "this":Landroid/support/v4/util/LruCache;, "Landroid/support/v4/util/LruCache<TK;TV;>;"
    :goto_0
    monitor-enter p0

    #@1
    .line 166
    :try_start_0
    iget v3, p0, Landroid/support/v4/util/LruCache;->size:I

    #@3
    if-ltz v3, :cond_0

    #@5
    iget-object v3, p0, Landroid/support/v4/util/LruCache;->map:Ljava/util/LinkedHashMap;

    #@7
    invoke-virtual {v3}, Ljava/util/LinkedHashMap;->isEmpty()Z

    #@a
    move-result v3

    #@b
    if-eqz v3, :cond_1

    #@d
    iget v3, p0, Landroid/support/v4/util/LruCache;->size:I

    #@f
    if-eqz v3, :cond_1

    #@11
    .line 167
    :cond_0
    new-instance v3, Ljava/lang/IllegalStateException;

    #@13
    new-instance v4, Ljava/lang/StringBuilder;

    #@15
    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    #@18
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@1b
    move-result-object v5

    #@1c
    invoke-virtual {v5}, Ljava/lang/Class;->getName()Ljava/lang/String;

    #@1f
    move-result-object v5

    #@20
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@23
    move-result-object v4

    #@24
    const-string v5, ".sizeOf() is reporting inconsistent results!"

    #@26
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@29
    move-result-object v4

    #@2a
    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2d
    move-result-object v4

    #@2e
    invoke-direct {v3, v4}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@31
    throw v3

    #@32
    .line 181
    :catchall_0
    move-exception v3

    #@33
    monitor-exit p0
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@34
    throw v3

    #@35
    .line 171
    :cond_1
    :try_start_1
    iget v3, p0, Landroid/support/v4/util/LruCache;->size:I

    #@37
    if-le v3, p1, :cond_2

    #@39
    iget-object v3, p0, Landroid/support/v4/util/LruCache;->map:Ljava/util/LinkedHashMap;

    #@3b
    invoke-virtual {v3}, Ljava/util/LinkedHashMap;->isEmpty()Z

    #@3e
    move-result v3

    #@3f
    if-eqz v3, :cond_3

    #@41
    .line 172
    :cond_2
    monitor-exit p0

    #@42
    .line 185
    return-void

    #@43
    .line 175
    :cond_3
    iget-object v3, p0, Landroid/support/v4/util/LruCache;->map:Ljava/util/LinkedHashMap;

    #@45
    invoke-virtual {v3}, Ljava/util/LinkedHashMap;->entrySet()Ljava/util/Set;

    #@48
    move-result-object v3

    #@49
    invoke-interface {v3}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    #@4c
    move-result-object v3

    #@4d
    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@50
    move-result-object v1

    #@51
    check-cast v1, Ljava/util/Map$Entry;

    #@53
    .line 176
    .local v1, "toEvict":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<TK;TV;>;"
    invoke-interface {v1}, Ljava/util/Map$Entry;->getKey()Ljava/lang/Object;

    #@56
    move-result-object v0

    #@57
    .line 177
    .local v0, "key":Ljava/lang/Object;, "TK;"
    invoke-interface {v1}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    #@5a
    move-result-object v2

    #@5b
    .line 178
    .local v2, "value":Ljava/lang/Object;, "TV;"
    iget-object v3, p0, Landroid/support/v4/util/LruCache;->map:Ljava/util/LinkedHashMap;

    #@5d
    invoke-virtual {v3, v0}, Ljava/util/LinkedHashMap;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    #@60
    .line 179
    iget v3, p0, Landroid/support/v4/util/LruCache;->size:I

    #@62
    invoke-direct {p0, v0, v2}, Landroid/support/v4/util/LruCache;->safeSizeOf(Ljava/lang/Object;Ljava/lang/Object;)I

    #@65
    move-result v4

    #@66
    sub-int/2addr v3, v4

    #@67
    iput v3, p0, Landroid/support/v4/util/LruCache;->size:I

    #@69
    .line 180
    iget v3, p0, Landroid/support/v4/util/LruCache;->evictionCount:I

    #@6b
    add-int/lit8 v3, v3, 0x1

    #@6d
    iput v3, p0, Landroid/support/v4/util/LruCache;->evictionCount:I

    #@6f
    .line 181
    monitor-exit p0
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@70
    .line 183
    const/4 v3, 0x1

    #@71
    const/4 v4, 0x0

    #@72
    invoke-virtual {p0, v3, v0, v2, v4}, Landroid/support/v4/util/LruCache;->entryRemoved(ZLjava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V

    #@75
    goto :goto_0
.end method
