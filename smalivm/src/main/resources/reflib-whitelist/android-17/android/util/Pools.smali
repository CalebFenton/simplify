.class public Landroid/util/Pools;
.super Ljava/lang/Object;
.source "Pools.java"


# direct methods
.method private constructor <init>()V
    .registers 1

    .prologue
    .line 23
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 24
    return-void
.end method

.method public static finitePool(Landroid/util/PoolableManager;I)Landroid/util/Pool;
    .registers 3
    .param p1, "limit"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T::",
            "Landroid/util/Poolable",
            "<TT;>;>(",
            "Landroid/util/PoolableManager",
            "<TT;>;I)",
            "Landroid/util/Pool",
            "<TT;>;"
        }
    .end annotation

    .prologue
    .line 31
    .local p0, "manager":Landroid/util/PoolableManager;, "Landroid/util/PoolableManager<TT;>;"
    new-instance v0, Landroid/util/FinitePool;

    invoke-direct {v0, p0, p1}, Landroid/util/FinitePool;-><init>(Landroid/util/PoolableManager;I)V

    return-object v0
.end method

.method public static simplePool(Landroid/util/PoolableManager;)Landroid/util/Pool;
    .registers 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T::",
            "Landroid/util/Poolable",
            "<TT;>;>(",
            "Landroid/util/PoolableManager",
            "<TT;>;)",
            "Landroid/util/Pool",
            "<TT;>;"
        }
    .end annotation

    .prologue
    .line 27
    .local p0, "manager":Landroid/util/PoolableManager;, "Landroid/util/PoolableManager<TT;>;"
    new-instance v0, Landroid/util/FinitePool;

    invoke-direct {v0, p0}, Landroid/util/FinitePool;-><init>(Landroid/util/PoolableManager;)V

    return-object v0
.end method

.method public static synchronizedPool(Landroid/util/Pool;)Landroid/util/Pool;
    .registers 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T::",
            "Landroid/util/Poolable",
            "<TT;>;>(",
            "Landroid/util/Pool",
            "<TT;>;)",
            "Landroid/util/Pool",
            "<TT;>;"
        }
    .end annotation

    .prologue
    .line 35
    .local p0, "pool":Landroid/util/Pool;, "Landroid/util/Pool<TT;>;"
    new-instance v0, Landroid/util/SynchronizedPool;

    invoke-direct {v0, p0}, Landroid/util/SynchronizedPool;-><init>(Landroid/util/Pool;)V

    return-object v0
.end method

.method public static synchronizedPool(Landroid/util/Pool;Ljava/lang/Object;)Landroid/util/Pool;
    .registers 3
    .param p1, "lock"    # Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T::",
            "Landroid/util/Poolable",
            "<TT;>;>(",
            "Landroid/util/Pool",
            "<TT;>;",
            "Ljava/lang/Object;",
            ")",
            "Landroid/util/Pool",
            "<TT;>;"
        }
    .end annotation

    .prologue
    .line 39
    .local p0, "pool":Landroid/util/Pool;, "Landroid/util/Pool<TT;>;"
    new-instance v0, Landroid/util/SynchronizedPool;

    invoke-direct {v0, p0, p1}, Landroid/util/SynchronizedPool;-><init>(Landroid/util/Pool;Ljava/lang/Object;)V

    return-object v0
.end method
