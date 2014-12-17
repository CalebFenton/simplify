.class public Llibcore/util/BasicLruCache;
.super Ljava/lang/Object;
.source "BasicLruCache.java"


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
.field private final map:Ljava/util/LinkedHashMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/LinkedHashMap",
            "<TK;TV;>;"
        }
    .end annotation
.end field

.field private final maxSize:I


# direct methods
.method public constructor <init>(I)V
    .registers 6
    .param p1, "maxSize"    # I

    .prologue
    .line 30
    .local p0, "this":Llibcore/util/BasicLruCache;, "Llibcore/util/BasicLruCache<TK;TV;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 31
    if-gtz p1, :cond_d

    .line 32
    new-instance v0, Ljava/lang/IllegalArgumentException;

    const-string v1, "maxSize <= 0"

    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 34
    :cond_d
    iput p1, p0, Llibcore/util/BasicLruCache;->maxSize:I

    .line 35
    new-instance v0, Ljava/util/LinkedHashMap;

    const/4 v1, 0x0

    const/high16 v2, 0x3f400000

    const/4 v3, 0x1

    invoke-direct {v0, v1, v2, v3}, Ljava/util/LinkedHashMap;-><init>(IFZ)V

    iput-object v0, p0, Llibcore/util/BasicLruCache;->map:Ljava/util/LinkedHashMap;

    .line 36
    return-void
.end method

.method private trimToSize(I)V
    .registers 6
    .param p1, "maxSize"    # I

    .prologue
    .line 81
    .local p0, "this":Llibcore/util/BasicLruCache;, "Llibcore/util/BasicLruCache<TK;TV;>;"
    :goto_0
    iget-object v3, p0, Llibcore/util/BasicLruCache;->map:Ljava/util/LinkedHashMap;

    invoke-virtual {v3}, Ljava/util/LinkedHashMap;->size()I

    move-result v3

    if-le v3, p1, :cond_1f

    .line 82
    iget-object v3, p0, Llibcore/util/BasicLruCache;->map:Ljava/util/LinkedHashMap;

    invoke-virtual {v3}, Ljava/util/LinkedHashMap;->eldest()Ljava/util/Map$Entry;

    move-result-object v1

    .line 84
    .local v1, "toEvict":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<TK;TV;>;"
    invoke-interface {v1}, Ljava/util/Map$Entry;->getKey()Ljava/lang/Object;

    move-result-object v0

    .line 85
    .local v0, "key":Ljava/lang/Object;, "TK;"
    invoke-interface {v1}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    move-result-object v2

    .line 86
    .local v2, "value":Ljava/lang/Object;, "TV;"
    iget-object v3, p0, Llibcore/util/BasicLruCache;->map:Ljava/util/LinkedHashMap;

    invoke-virtual {v3, v0}, Ljava/util/LinkedHashMap;->remove(Ljava/lang/Object;)Ljava/lang/Object;

    .line 88
    invoke-virtual {p0, v0, v2}, Llibcore/util/BasicLruCache;->entryEvicted(Ljava/lang/Object;Ljava/lang/Object;)V

    goto :goto_0

    .line 90
    .end local v0    # "key":Ljava/lang/Object;, "TK;"
    .end local v1    # "toEvict":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<TK;TV;>;"
    .end local v2    # "value":Ljava/lang/Object;, "TV;"
    :cond_1f
    return-void
.end method


# virtual methods
.method protected create(Ljava/lang/Object;)Ljava/lang/Object;
    .registers 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TK;)TV;"
        }
    .end annotation

    .prologue
    .line 104
    .local p0, "this":Llibcore/util/BasicLruCache;, "Llibcore/util/BasicLruCache<TK;TV;>;"
    .local p1, "key":Ljava/lang/Object;, "TK;"
    const/4 v0, 0x0

    return-object v0
.end method

.method protected entryEvicted(Ljava/lang/Object;Ljava/lang/Object;)V
    .registers 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TK;TV;)V"
        }
    .end annotation

    .prologue
    .line 96
    .local p0, "this":Llibcore/util/BasicLruCache;, "Llibcore/util/BasicLruCache<TK;TV;>;"
    .local p1, "key":Ljava/lang/Object;, "TK;"
    .local p2, "value":Ljava/lang/Object;, "TV;"
    return-void
.end method

.method public final declared-synchronized evictAll()V
    .registers 2

    .prologue
    .line 119
    .local p0, "this":Llibcore/util/BasicLruCache;, "Llibcore/util/BasicLruCache<TK;TV;>;"
    monitor-enter p0

    const/4 v0, 0x0

    :try_start_2
    invoke-direct {p0, v0}, Llibcore/util/BasicLruCache;->trimToSize(I)V
    :try_end_5
    .catchall {:try_start_2 .. :try_end_5} :catchall_7

    .line 120
    monitor-exit p0

    return-void

    .line 119
    :catchall_7
    move-exception v0

    monitor-exit p0

    throw v0
.end method

.method public final declared-synchronized get(Ljava/lang/Object;)Ljava/lang/Object;
    .registers 5
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TK;)TV;"
        }
    .end annotation

    .prologue
    .line 45
    .local p0, "this":Llibcore/util/BasicLruCache;, "Llibcore/util/BasicLruCache<TK;TV;>;"
    .local p1, "key":Ljava/lang/Object;, "TK;"
    monitor-enter p0

    if-nez p1, :cond_c

    .line 46
    :try_start_3
    new-instance v2, Ljava/lang/NullPointerException;

    invoke-direct {v2}, Ljava/lang/NullPointerException;-><init>()V

    throw v2
    :try_end_9
    .catchall {:try_start_3 .. :try_end_9} :catchall_9

    .line 45
    :catchall_9
    move-exception v2

    monitor-exit p0

    throw v2

    .line 49
    :cond_c
    :try_start_c
    iget-object v2, p0, Llibcore/util/BasicLruCache;->map:Ljava/util/LinkedHashMap;

    invoke-virtual {v2, p1}, Ljava/util/LinkedHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_11
    .catchall {:try_start_c .. :try_end_11} :catchall_9

    move-result-object v0

    .line 50
    .local v0, "result":Ljava/lang/Object;, "TV;"
    if-eqz v0, :cond_17

    move-object v1, v0

    .line 60
    .end local v0    # "result":Ljava/lang/Object;, "TV;"
    .local v1, "result":Ljava/lang/Object;, "TV;"
    :goto_15
    monitor-exit p0

    return-object v1

    .line 54
    .end local v1    # "result":Ljava/lang/Object;, "TV;"
    .restart local v0    # "result":Ljava/lang/Object;, "TV;"
    :cond_17
    :try_start_17
    invoke-virtual {p0, p1}, Llibcore/util/BasicLruCache;->create(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    .line 56
    if-eqz v0, :cond_27

    .line 57
    iget-object v2, p0, Llibcore/util/BasicLruCache;->map:Ljava/util/LinkedHashMap;

    invoke-virtual {v2, p1, v0}, Ljava/util/LinkedHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    .line 58
    iget v2, p0, Llibcore/util/BasicLruCache;->maxSize:I

    invoke-direct {p0, v2}, Llibcore/util/BasicLruCache;->trimToSize(I)V
    :try_end_27
    .catchall {:try_start_17 .. :try_end_27} :catchall_9

    :cond_27
    move-object v1, v0

    .line 60
    .end local v0    # "result":Ljava/lang/Object;, "TV;"
    .restart local v1    # "result":Ljava/lang/Object;, "TV;"
    goto :goto_15
.end method

.method public final declared-synchronized put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    .registers 5
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TK;TV;)TV;"
        }
    .end annotation

    .prologue
    .line 71
    .local p0, "this":Llibcore/util/BasicLruCache;, "Llibcore/util/BasicLruCache<TK;TV;>;"
    .local p1, "key":Ljava/lang/Object;, "TK;"
    .local p2, "value":Ljava/lang/Object;, "TV;"
    monitor-enter p0

    if-eqz p1, :cond_5

    if-nez p2, :cond_e

    .line 72
    :cond_5
    :try_start_5
    new-instance v1, Ljava/lang/NullPointerException;

    invoke-direct {v1}, Ljava/lang/NullPointerException;-><init>()V

    throw v1
    :try_end_b
    .catchall {:try_start_5 .. :try_end_b} :catchall_b

    .line 71
    :catchall_b
    move-exception v1

    monitor-exit p0

    throw v1

    .line 75
    :cond_e
    :try_start_e
    iget-object v1, p0, Llibcore/util/BasicLruCache;->map:Ljava/util/LinkedHashMap;

    invoke-virtual {v1, p1, p2}, Ljava/util/LinkedHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    .line 76
    .local v0, "previous":Ljava/lang/Object;, "TV;"
    iget v1, p0, Llibcore/util/BasicLruCache;->maxSize:I

    invoke-direct {p0, v1}, Llibcore/util/BasicLruCache;->trimToSize(I)V
    :try_end_19
    .catchall {:try_start_e .. :try_end_19} :catchall_b

    .line 77
    monitor-exit p0

    return-object v0
.end method

.method public final declared-synchronized snapshot()Ljava/util/Map;
    .registers 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/Map",
            "<TK;TV;>;"
        }
    .end annotation

    .prologue
    .line 112
    .local p0, "this":Llibcore/util/BasicLruCache;, "Llibcore/util/BasicLruCache<TK;TV;>;"
    monitor-enter p0

    :try_start_1
    new-instance v0, Ljava/util/LinkedHashMap;

    iget-object v1, p0, Llibcore/util/BasicLruCache;->map:Ljava/util/LinkedHashMap;

    invoke-direct {v0, v1}, Ljava/util/LinkedHashMap;-><init>(Ljava/util/Map;)V
    :try_end_8
    .catchall {:try_start_1 .. :try_end_8} :catchall_a

    monitor-exit p0

    return-object v0

    :catchall_a
    move-exception v0

    monitor-exit p0

    throw v0
.end method
