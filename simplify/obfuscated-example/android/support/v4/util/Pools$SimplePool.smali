.class public Landroid/support/v4/util/Pools$SimplePool;
.super Ljava/lang/Object;
.source "Pools.java"

# interfaces
.implements Landroid/support/v4/util/Pools$Pool;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/util/Pools;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "SimplePool"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<T:",
        "Ljava/lang/Object;",
        ">",
        "Ljava/lang/Object;",
        "Landroid/support/v4/util/Pools$Pool",
        "<TT;>;"
    }
.end annotation


# instance fields
.field private final mPool:[Ljava/lang/Object;

.field private mPoolSize:I


# direct methods
.method public constructor <init>(I)V
    .locals 2
    .param p1, "maxPoolSize"    # I

    #@0
    .prologue
    .line 90
    .local p0, "this":Landroid/support/v4/util/Pools$SimplePool;, "Landroid/support/v4/util/Pools$SimplePool<TT;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 91
    if-gtz p1, :cond_0

    #@5
    .line 92
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@7
    const-string v1, "The max pool size must be > 0"

    #@9
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@c
    throw v0

    #@d
    .line 94
    :cond_0
    new-array v0, p1, [Ljava/lang/Object;

    #@f
    iput-object v0, p0, Landroid/support/v4/util/Pools$SimplePool;->mPool:[Ljava/lang/Object;

    #@11
    .line 95
    return-void
.end method

.method private isInPool(Ljava/lang/Object;)Z
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TT;)Z"
        }
    .end annotation

    #@0
    .prologue
    .line 124
    .local p0, "this":Landroid/support/v4/util/Pools$SimplePool;, "Landroid/support/v4/util/Pools$SimplePool<TT;>;"
    .local p1, "instance":Ljava/lang/Object;, "TT;"
    const/4 v0, 0x0

    #@1
    .local v0, "i":I
    :goto_0
    iget v1, p0, Landroid/support/v4/util/Pools$SimplePool;->mPoolSize:I

    #@3
    if-ge v0, v1, :cond_1

    #@5
    .line 125
    iget-object v1, p0, Landroid/support/v4/util/Pools$SimplePool;->mPool:[Ljava/lang/Object;

    #@7
    aget-object v1, v1, v0

    #@9
    if-ne v1, p1, :cond_0

    #@b
    .line 126
    const/4 v1, 0x1

    #@c
    .line 129
    :goto_1
    return v1

    #@d
    .line 124
    :cond_0
    add-int/lit8 v0, v0, 0x1

    #@f
    goto :goto_0

    #@10
    .line 129
    :cond_1
    const/4 v1, 0x0

    #@11
    goto :goto_1
.end method


# virtual methods
.method public acquire()Ljava/lang/Object;
    .locals 4
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TT;"
        }
    .end annotation

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/Pools$SimplePool;, "Landroid/support/v4/util/Pools$SimplePool<TT;>;"
    const/4 v2, 0x0

    #@1
    .line 100
    iget v3, p0, Landroid/support/v4/util/Pools$SimplePool;->mPoolSize:I

    #@3
    if-lez v3, :cond_0

    #@5
    .line 101
    iget v3, p0, Landroid/support/v4/util/Pools$SimplePool;->mPoolSize:I

    #@7
    add-int/lit8 v1, v3, -0x1

    #@9
    .line 102
    .local v1, "lastPooledIndex":I
    iget-object v3, p0, Landroid/support/v4/util/Pools$SimplePool;->mPool:[Ljava/lang/Object;

    #@b
    aget-object v0, v3, v1

    #@d
    .line 103
    .local v0, "instance":Ljava/lang/Object;, "TT;"
    iget-object v3, p0, Landroid/support/v4/util/Pools$SimplePool;->mPool:[Ljava/lang/Object;

    #@f
    aput-object v2, v3, v1

    #@11
    .line 104
    iget v2, p0, Landroid/support/v4/util/Pools$SimplePool;->mPoolSize:I

    #@13
    add-int/lit8 v2, v2, -0x1

    #@15
    iput v2, p0, Landroid/support/v4/util/Pools$SimplePool;->mPoolSize:I

    #@17
    .line 107
    .end local v0    # "instance":Ljava/lang/Object;, "TT;"
    .end local v1    # "lastPooledIndex":I
    :goto_0
    return-object v0

    #@18
    :cond_0
    move-object v0, v2

    #@19
    goto :goto_0
.end method

.method public release(Ljava/lang/Object;)Z
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TT;)Z"
        }
    .end annotation

    #@0
    .prologue
    .line 112
    .local p0, "this":Landroid/support/v4/util/Pools$SimplePool;, "Landroid/support/v4/util/Pools$SimplePool<TT;>;"
    .local p1, "instance":Ljava/lang/Object;, "TT;"
    invoke-direct {p0, p1}, Landroid/support/v4/util/Pools$SimplePool;->isInPool(Ljava/lang/Object;)Z

    #@3
    move-result v0

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 113
    new-instance v0, Ljava/lang/IllegalStateException;

    #@8
    const-string v1, "Already in the pool!"

    #@a
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@d
    throw v0

    #@e
    .line 115
    :cond_0
    iget v0, p0, Landroid/support/v4/util/Pools$SimplePool;->mPoolSize:I

    #@10
    iget-object v1, p0, Landroid/support/v4/util/Pools$SimplePool;->mPool:[Ljava/lang/Object;

    #@12
    array-length v1, v1

    #@13
    if-ge v0, v1, :cond_1

    #@15
    .line 116
    iget-object v0, p0, Landroid/support/v4/util/Pools$SimplePool;->mPool:[Ljava/lang/Object;

    #@17
    iget v1, p0, Landroid/support/v4/util/Pools$SimplePool;->mPoolSize:I

    #@19
    aput-object p1, v0, v1

    #@1b
    .line 117
    iget v0, p0, Landroid/support/v4/util/Pools$SimplePool;->mPoolSize:I

    #@1d
    add-int/lit8 v0, v0, 0x1

    #@1f
    iput v0, p0, Landroid/support/v4/util/Pools$SimplePool;->mPoolSize:I

    #@21
    .line 118
    const/4 v0, 0x1

    #@22
    .line 120
    :goto_0
    return v0

    #@23
    :cond_1
    const/4 v0, 0x0

    #@24
    goto :goto_0
.end method
