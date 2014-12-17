.class Landroid/util/SynchronizedPool;
.super Ljava/lang/Object;
.source "SynchronizedPool.java"

# interfaces
.implements Landroid/util/Pool;


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<T::",
        "Landroid/util/Poolable",
        "<TT;>;>",
        "Ljava/lang/Object;",
        "Landroid/util/Pool",
        "<TT;>;"
    }
.end annotation


# instance fields
.field private final mLock:Ljava/lang/Object;

.field private final mPool:Landroid/util/Pool;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/util/Pool",
            "<TT;>;"
        }
    .end annotation
.end field


# direct methods
.method public constructor <init>(Landroid/util/Pool;)V
    .registers 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/Pool",
            "<TT;>;)V"
        }
    .end annotation

    .prologue
    .line 27
    .local p0, "this":Landroid/util/SynchronizedPool;, "Landroid/util/SynchronizedPool<TT;>;"
    .local p1, "pool":Landroid/util/Pool;, "Landroid/util/Pool<TT;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 28
    iput-object p1, p0, Landroid/util/SynchronizedPool;->mPool:Landroid/util/Pool;

    .line 29
    iput-object p0, p0, Landroid/util/SynchronizedPool;->mLock:Ljava/lang/Object;

    .line 30
    return-void
.end method

.method public constructor <init>(Landroid/util/Pool;Ljava/lang/Object;)V
    .registers 3
    .param p2, "lock"    # Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/Pool",
            "<TT;>;",
            "Ljava/lang/Object;",
            ")V"
        }
    .end annotation

    .prologue
    .line 32
    .local p0, "this":Landroid/util/SynchronizedPool;, "Landroid/util/SynchronizedPool<TT;>;"
    .local p1, "pool":Landroid/util/Pool;, "Landroid/util/Pool<TT;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 33
    iput-object p1, p0, Landroid/util/SynchronizedPool;->mPool:Landroid/util/Pool;

    .line 34
    iput-object p2, p0, Landroid/util/SynchronizedPool;->mLock:Ljava/lang/Object;

    .line 35
    return-void
.end method


# virtual methods
.method public acquire()Landroid/util/Poolable;
    .registers 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TT;"
        }
    .end annotation

    .prologue
    .line 38
    .local p0, "this":Landroid/util/SynchronizedPool;, "Landroid/util/SynchronizedPool<TT;>;"
    iget-object v1, p0, Landroid/util/SynchronizedPool;->mLock:Ljava/lang/Object;

    monitor-enter v1

    .line 39
    :try_start_3
    iget-object v0, p0, Landroid/util/SynchronizedPool;->mPool:Landroid/util/Pool;

    invoke-interface {v0}, Landroid/util/Pool;->acquire()Landroid/util/Poolable;

    move-result-object v0

    monitor-exit v1

    return-object v0

    .line 40
    :catchall_b
    move-exception v0

    monitor-exit v1
    :try_end_d
    .catchall {:try_start_3 .. :try_end_d} :catchall_b

    throw v0
.end method

.method public release(Landroid/util/Poolable;)V
    .registers 4
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TT;)V"
        }
    .end annotation

    .prologue
    .line 44
    .local p0, "this":Landroid/util/SynchronizedPool;, "Landroid/util/SynchronizedPool<TT;>;"
    .local p1, "element":Landroid/util/Poolable;, "TT;"
    iget-object v1, p0, Landroid/util/SynchronizedPool;->mLock:Ljava/lang/Object;

    monitor-enter v1

    .line 45
    :try_start_3
    iget-object v0, p0, Landroid/util/SynchronizedPool;->mPool:Landroid/util/Pool;

    invoke-interface {v0, p1}, Landroid/util/Pool;->release(Landroid/util/Poolable;)V

    .line 46
    monitor-exit v1

    .line 47
    return-void

    .line 46
    :catchall_a
    move-exception v0

    monitor-exit v1
    :try_end_c
    .catchall {:try_start_3 .. :try_end_c} :catchall_a

    throw v0
.end method
