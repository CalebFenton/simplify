.class final Landroid/support/v4/util/MapCollections$ValuesCollection;
.super Ljava/lang/Object;
.source "MapCollections.java"

# interfaces
.implements Ljava/util/Collection;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/util/MapCollections;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x10
    name = "ValuesCollection"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Object;",
        "Ljava/util/Collection",
        "<TV;>;"
    }
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v4/util/MapCollections;


# direct methods
.method constructor <init>(Landroid/support/v4/util/MapCollections;)V
    .locals 0

    #@0
    .prologue
    .line 353
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    iput-object p1, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public add(Ljava/lang/Object;)Z
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TV;)Z"
        }
    .end annotation

    #@0
    .prologue
    .line 357
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    .local p1, "object":Ljava/lang/Object;, "TV;"
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@5
    throw v0
.end method

.method public addAll(Ljava/util/Collection;)Z
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/Collection",
            "<+TV;>;)Z"
        }
    .end annotation

    #@0
    .prologue
    .line 362
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    .local p1, "collection":Ljava/util/Collection;, "Ljava/util/Collection<+TV;>;"
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@5
    throw v0
.end method

.method public clear()V
    .locals 1

    #@0
    .prologue
    .line 367
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    iget-object v0, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/util/MapCollections;->colClear()V

    #@5
    .line 368
    return-void
.end method

.method public contains(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "object"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 372
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    iget-object v0, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/util/MapCollections;->colIndexOfValue(Ljava/lang/Object;)I

    #@5
    move-result v0

    #@6
    if-ltz v0, :cond_0

    #@8
    const/4 v0, 0x1

    #@9
    :goto_0
    return v0

    #@a
    :cond_0
    const/4 v0, 0x0

    #@b
    goto :goto_0
.end method

.method public containsAll(Ljava/util/Collection;)Z
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/Collection",
            "<*>;)Z"
        }
    .end annotation

    #@0
    .prologue
    .line 377
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    .local p1, "collection":Ljava/util/Collection;, "Ljava/util/Collection<*>;"
    invoke-interface {p1}, Ljava/util/Collection;->iterator()Ljava/util/Iterator;

    #@3
    move-result-object v0

    #@4
    .line 378
    .local v0, "it":Ljava/util/Iterator;, "Ljava/util/Iterator<*>;"
    :cond_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    #@7
    move-result v1

    #@8
    if-eqz v1, :cond_1

    #@a
    .line 379
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@d
    move-result-object v1

    #@e
    invoke-virtual {p0, v1}, Landroid/support/v4/util/MapCollections$ValuesCollection;->contains(Ljava/lang/Object;)Z

    #@11
    move-result v1

    #@12
    if-nez v1, :cond_0

    #@14
    .line 380
    const/4 v1, 0x0

    #@15
    .line 383
    :goto_0
    return v1

    #@16
    :cond_1
    const/4 v1, 0x1

    #@17
    goto :goto_0
.end method

.method public isEmpty()Z
    .locals 1

    #@0
    .prologue
    .line 388
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    iget-object v0, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/util/MapCollections;->colGetSize()I

    #@5
    move-result v0

    #@6
    if-nez v0, :cond_0

    #@8
    const/4 v0, 0x1

    #@9
    :goto_0
    return v0

    #@a
    :cond_0
    const/4 v0, 0x0

    #@b
    goto :goto_0
.end method

.method public iterator()Ljava/util/Iterator;
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/Iterator",
            "<TV;>;"
        }
    .end annotation

    #@0
    .prologue
    .line 393
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    new-instance v0, Landroid/support/v4/util/MapCollections$ArrayIterator;

    #@2
    iget-object v1, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@4
    const/4 v2, 0x1

    #@5
    invoke-direct {v0, v1, v2}, Landroid/support/v4/util/MapCollections$ArrayIterator;-><init>(Landroid/support/v4/util/MapCollections;I)V

    #@8
    return-object v0
.end method

.method public remove(Ljava/lang/Object;)Z
    .locals 2
    .param p1, "object"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 398
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    iget-object v1, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@2
    invoke-virtual {v1, p1}, Landroid/support/v4/util/MapCollections;->colIndexOfValue(Ljava/lang/Object;)I

    #@5
    move-result v0

    #@6
    .line 399
    .local v0, "index":I
    if-ltz v0, :cond_0

    #@8
    .line 400
    iget-object v1, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@a
    invoke-virtual {v1, v0}, Landroid/support/v4/util/MapCollections;->colRemoveAt(I)V

    #@d
    .line 401
    const/4 v1, 0x1

    #@e
    .line 403
    :goto_0
    return v1

    #@f
    :cond_0
    const/4 v1, 0x0

    #@10
    goto :goto_0
.end method

.method public removeAll(Ljava/util/Collection;)Z
    .locals 6
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/Collection",
            "<*>;)Z"
        }
    .end annotation

    #@0
    .prologue
    .line 408
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    .local p1, "collection":Ljava/util/Collection;, "Ljava/util/Collection<*>;"
    iget-object v4, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@2
    invoke-virtual {v4}, Landroid/support/v4/util/MapCollections;->colGetSize()I

    #@5
    move-result v0

    #@6
    .line 409
    .local v0, "N":I
    const/4 v1, 0x0

    #@7
    .line 410
    .local v1, "changed":Z
    const/4 v3, 0x0

    #@8
    .local v3, "i":I
    :goto_0
    if-ge v3, v0, :cond_1

    #@a
    .line 411
    iget-object v4, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@c
    const/4 v5, 0x1

    #@d
    invoke-virtual {v4, v3, v5}, Landroid/support/v4/util/MapCollections;->colGetEntry(II)Ljava/lang/Object;

    #@10
    move-result-object v2

    #@11
    .line 412
    .local v2, "cur":Ljava/lang/Object;
    invoke-interface {p1, v2}, Ljava/util/Collection;->contains(Ljava/lang/Object;)Z

    #@14
    move-result v4

    #@15
    if-eqz v4, :cond_0

    #@17
    .line 413
    iget-object v4, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@19
    invoke-virtual {v4, v3}, Landroid/support/v4/util/MapCollections;->colRemoveAt(I)V

    #@1c
    .line 414
    add-int/lit8 v3, v3, -0x1

    #@1e
    .line 415
    add-int/lit8 v0, v0, -0x1

    #@20
    .line 416
    const/4 v1, 0x1

    #@21
    .line 410
    :cond_0
    add-int/lit8 v3, v3, 0x1

    #@23
    goto :goto_0

    #@24
    .line 419
    .end local v2    # "cur":Ljava/lang/Object;
    :cond_1
    return v1
.end method

.method public retainAll(Ljava/util/Collection;)Z
    .locals 6
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/Collection",
            "<*>;)Z"
        }
    .end annotation

    #@0
    .prologue
    .line 424
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    .local p1, "collection":Ljava/util/Collection;, "Ljava/util/Collection<*>;"
    iget-object v4, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@2
    invoke-virtual {v4}, Landroid/support/v4/util/MapCollections;->colGetSize()I

    #@5
    move-result v0

    #@6
    .line 425
    .local v0, "N":I
    const/4 v1, 0x0

    #@7
    .line 426
    .local v1, "changed":Z
    const/4 v3, 0x0

    #@8
    .local v3, "i":I
    :goto_0
    if-ge v3, v0, :cond_1

    #@a
    .line 427
    iget-object v4, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@c
    const/4 v5, 0x1

    #@d
    invoke-virtual {v4, v3, v5}, Landroid/support/v4/util/MapCollections;->colGetEntry(II)Ljava/lang/Object;

    #@10
    move-result-object v2

    #@11
    .line 428
    .local v2, "cur":Ljava/lang/Object;
    invoke-interface {p1, v2}, Ljava/util/Collection;->contains(Ljava/lang/Object;)Z

    #@14
    move-result v4

    #@15
    if-nez v4, :cond_0

    #@17
    .line 429
    iget-object v4, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@19
    invoke-virtual {v4, v3}, Landroid/support/v4/util/MapCollections;->colRemoveAt(I)V

    #@1c
    .line 430
    add-int/lit8 v3, v3, -0x1

    #@1e
    .line 431
    add-int/lit8 v0, v0, -0x1

    #@20
    .line 432
    const/4 v1, 0x1

    #@21
    .line 426
    :cond_0
    add-int/lit8 v3, v3, 0x1

    #@23
    goto :goto_0

    #@24
    .line 435
    .end local v2    # "cur":Ljava/lang/Object;
    :cond_1
    return v1
.end method

.method public size()I
    .locals 1

    #@0
    .prologue
    .line 440
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    iget-object v0, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/util/MapCollections;->colGetSize()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public toArray()[Ljava/lang/Object;
    .locals 2

    #@0
    .prologue
    .line 445
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    iget-object v0, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@2
    const/4 v1, 0x1

    #@3
    invoke-virtual {v0, v1}, Landroid/support/v4/util/MapCollections;->toArrayHelper(I)[Ljava/lang/Object;

    #@6
    move-result-object v0

    #@7
    return-object v0
.end method

.method public toArray([Ljava/lang/Object;)[Ljava/lang/Object;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "<T:",
            "Ljava/lang/Object;",
            ">([TT;)[TT;"
        }
    .end annotation

    #@0
    .prologue
    .line 450
    .local p0, "this":Landroid/support/v4/util/MapCollections$ValuesCollection;, "Landroid/support/v4/util/MapCollections<TK;TV;>.ValuesCollection;"
    .local p1, "array":[Ljava/lang/Object;, "[TT;"
    iget-object v0, p0, Landroid/support/v4/util/MapCollections$ValuesCollection;->this$0:Landroid/support/v4/util/MapCollections;

    #@2
    const/4 v1, 0x1

    #@3
    invoke-virtual {v0, p1, v1}, Landroid/support/v4/util/MapCollections;->toArrayHelper([Ljava/lang/Object;I)[Ljava/lang/Object;

    #@6
    move-result-object v0

    #@7
    return-object v0
.end method
