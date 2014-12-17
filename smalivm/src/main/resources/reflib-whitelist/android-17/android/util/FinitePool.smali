.class Landroid/util/FinitePool;
.super Ljava/lang/Object;
.source "FinitePool.java"

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


# static fields
.field private static final LOG_TAG:Ljava/lang/String; = "FinitePool"


# instance fields
.field private final mInfinite:Z

.field private final mLimit:I

.field private final mManager:Landroid/util/PoolableManager;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/util/PoolableManager",
            "<TT;>;"
        }
    .end annotation
.end field

.field private mPoolCount:I

.field private mRoot:Landroid/util/Poolable;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "TT;"
        }
    .end annotation
.end field


# direct methods
.method constructor <init>(Landroid/util/PoolableManager;)V
    .registers 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/PoolableManager",
            "<TT;>;)V"
        }
    .end annotation

    .prologue
    .line 47
    .local p0, "this":Landroid/util/FinitePool;, "Landroid/util/FinitePool<TT;>;"
    .local p1, "manager":Landroid/util/PoolableManager;, "Landroid/util/PoolableManager<TT;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 48
    iput-object p1, p0, Landroid/util/FinitePool;->mManager:Landroid/util/PoolableManager;

    .line 49
    const/4 v0, 0x0

    iput v0, p0, Landroid/util/FinitePool;->mLimit:I

    .line 50
    const/4 v0, 0x1

    iput-boolean v0, p0, Landroid/util/FinitePool;->mInfinite:Z

    .line 51
    return-void
.end method

.method constructor <init>(Landroid/util/PoolableManager;I)V
    .registers 5
    .param p2, "limit"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/PoolableManager",
            "<TT;>;I)V"
        }
    .end annotation

    .prologue
    .line 53
    .local p0, "this":Landroid/util/FinitePool;, "Landroid/util/FinitePool<TT;>;"
    .local p1, "manager":Landroid/util/PoolableManager;, "Landroid/util/PoolableManager<TT;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 54
    if-gtz p2, :cond_d

    new-instance v0, Ljava/lang/IllegalArgumentException;

    const-string v1, "The pool limit must be > 0"

    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 56
    :cond_d
    iput-object p1, p0, Landroid/util/FinitePool;->mManager:Landroid/util/PoolableManager;

    .line 57
    iput p2, p0, Landroid/util/FinitePool;->mLimit:I

    .line 58
    const/4 v0, 0x0

    iput-boolean v0, p0, Landroid/util/FinitePool;->mInfinite:Z

    .line 59
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
    .line 64
    .local p0, "this":Landroid/util/FinitePool;, "Landroid/util/FinitePool<TT;>;"
    iget-object v1, p0, Landroid/util/FinitePool;->mRoot:Landroid/util/Poolable;

    if-eqz v1, :cond_24

    .line 65
    iget-object v0, p0, Landroid/util/FinitePool;->mRoot:Landroid/util/Poolable;

    .line 66
    .local v0, "element":Landroid/util/Poolable;, "TT;"
    invoke-interface {v0}, Landroid/util/Poolable;->getNextPoolable()Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Landroid/util/Poolable;

    iput-object v1, p0, Landroid/util/FinitePool;->mRoot:Landroid/util/Poolable;

    .line 67
    iget v1, p0, Landroid/util/FinitePool;->mPoolCount:I

    add-int/lit8 v1, v1, -0x1

    iput v1, p0, Landroid/util/FinitePool;->mPoolCount:I

    .line 72
    :goto_14
    if-eqz v0, :cond_23

    .line 73
    const/4 v1, 0x0

    invoke-interface {v0, v1}, Landroid/util/Poolable;->setNextPoolable(Ljava/lang/Object;)V

    .line 74
    const/4 v1, 0x0

    invoke-interface {v0, v1}, Landroid/util/Poolable;->setPooled(Z)V

    .line 75
    iget-object v1, p0, Landroid/util/FinitePool;->mManager:Landroid/util/PoolableManager;

    invoke-interface {v1, v0}, Landroid/util/PoolableManager;->onAcquired(Landroid/util/Poolable;)V

    .line 78
    :cond_23
    return-object v0

    .line 69
    .end local v0    # "element":Landroid/util/Poolable;, "TT;"
    :cond_24
    iget-object v1, p0, Landroid/util/FinitePool;->mManager:Landroid/util/PoolableManager;

    invoke-interface {v1}, Landroid/util/PoolableManager;->newInstance()Landroid/util/Poolable;

    move-result-object v0

    .restart local v0    # "element":Landroid/util/Poolable;, "TT;"
    goto :goto_14
.end method

.method public release(Landroid/util/Poolable;)V
    .registers 5
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TT;)V"
        }
    .end annotation

    .prologue
    .line 82
    .local p0, "this":Landroid/util/FinitePool;, "Landroid/util/FinitePool<TT;>;"
    .local p1, "element":Landroid/util/Poolable;, "TT;"
    invoke-interface {p1}, Landroid/util/Poolable;->isPooled()Z

    move-result v0

    if-nez v0, :cond_27

    .line 83
    iget-boolean v0, p0, Landroid/util/FinitePool;->mInfinite:Z

    if-nez v0, :cond_10

    iget v0, p0, Landroid/util/FinitePool;->mPoolCount:I

    iget v1, p0, Landroid/util/FinitePool;->mLimit:I

    if-ge v0, v1, :cond_21

    .line 84
    :cond_10
    iget v0, p0, Landroid/util/FinitePool;->mPoolCount:I

    add-int/lit8 v0, v0, 0x1

    iput v0, p0, Landroid/util/FinitePool;->mPoolCount:I

    .line 85
    iget-object v0, p0, Landroid/util/FinitePool;->mRoot:Landroid/util/Poolable;

    invoke-interface {p1, v0}, Landroid/util/Poolable;->setNextPoolable(Ljava/lang/Object;)V

    .line 86
    const/4 v0, 0x1

    invoke-interface {p1, v0}, Landroid/util/Poolable;->setPooled(Z)V

    .line 87
    iput-object p1, p0, Landroid/util/FinitePool;->mRoot:Landroid/util/Poolable;

    .line 89
    :cond_21
    iget-object v0, p0, Landroid/util/FinitePool;->mManager:Landroid/util/PoolableManager;

    invoke-interface {v0, p1}, Landroid/util/PoolableManager;->onReleased(Landroid/util/Poolable;)V

    .line 93
    :goto_26
    return-void

    .line 91
    :cond_27
    const-string v0, "FinitePool"

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "Element is already in pool: "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    goto :goto_26
.end method
