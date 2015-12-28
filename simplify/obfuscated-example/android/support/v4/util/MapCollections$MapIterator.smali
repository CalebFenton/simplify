.class final Landroid/support/v4/util/MapCollections$MapIterator;
.super Ljava/lang/Object;
.source "MapCollections.java"

# interfaces
.implements Ljava/util/Iterator;
.implements Ljava/util/Map$Entry;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/util/MapCollections;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x10
    name = "MapIterator"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Object;",
        "Ljava/util/Iterator",
        "<",
        "Ljava/util/Map$Entry",
        "<TK;TV;>;>;",
        "Ljava/util/Map$Entry",
        "<TK;TV;>;"
    }
.end annotation


# instance fields
.field mEnd:I

.field mEntryValid:Z

.field mIndex:I

.field final synthetic this$0:Landroid/support/v4/util/MapCollections;


# direct methods
.method constructor <init>(Landroid/support/v4/util/MapCollections;)V
    .locals 1

    #@0
    .prologue
    .line 76
    .local p0, "this":Landroid/support/v4/util/MapCollections$MapIterator;, "Landroid/support/v4/util/MapCollections<TK;TV;>.MapIterator;"
    iput-object p1, p0, Landroid/support/v4/util/MapCollections$MapIterator;->this$0:Landroid/support/v4/util/MapCollections;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    .line 74
    const/4 v0, 0x0

    #@6
    iput-boolean v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mEntryValid:Z

    #@8
    .line 77
    invoke-virtual {p1}, Landroid/support/v4/util/MapCollections;->colGetSize()I

    #@b
    move-result v0

    #@c
    add-int/lit8 v0, v0, -0x1

    #@e
    iput v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mEnd:I

    #@10
    .line 78
    const/4 v0, -0x1

    #@11
    iput v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@13
    .line 79
    return-void
.end method


# virtual methods
.method public final equals(Ljava/lang/Object;)Z
    .locals 6
    .param p1, "o"    # Ljava/lang/Object;

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/MapCollections$MapIterator;, "Landroid/support/v4/util/MapCollections<TK;TV;>.MapIterator;"
    const/4 v1, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 133
    iget-boolean v3, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mEntryValid:Z

    #@4
    if-nez v3, :cond_0

    #@6
    .line 134
    new-instance v1, Ljava/lang/IllegalStateException;

    #@8
    const-string v2, "This container does not support retaining Map.Entry objects"

    #@a
    invoke-direct {v1, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@d
    throw v1

    #@e
    .line 137
    :cond_0
    instance-of v3, p1, Ljava/util/Map$Entry;

    #@10
    if-nez v3, :cond_1

    #@12
    .line 141
    :goto_0
    return v2

    #@13
    :cond_1
    move-object v0, p1

    #@14
    .line 140
    check-cast v0, Ljava/util/Map$Entry;

    #@16
    .line 141
    .local v0, "e":Ljava/util/Map$Entry;, "Ljava/util/Map$Entry<**>;"
    invoke-interface {v0}, Ljava/util/Map$Entry;->getKey()Ljava/lang/Object;

    #@19
    move-result-object v3

    #@1a
    iget-object v4, p0, Landroid/support/v4/util/MapCollections$MapIterator;->this$0:Landroid/support/v4/util/MapCollections;

    #@1c
    iget v5, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@1e
    invoke-virtual {v4, v5, v2}, Landroid/support/v4/util/MapCollections;->colGetEntry(II)Ljava/lang/Object;

    #@21
    move-result-object v4

    #@22
    invoke-static {v3, v4}, Landroid/support/v4/util/ContainerHelpers;->equal(Ljava/lang/Object;Ljava/lang/Object;)Z

    #@25
    move-result v3

    #@26
    if-eqz v3, :cond_2

    #@28
    invoke-interface {v0}, Ljava/util/Map$Entry;->getValue()Ljava/lang/Object;

    #@2b
    move-result-object v3

    #@2c
    iget-object v4, p0, Landroid/support/v4/util/MapCollections$MapIterator;->this$0:Landroid/support/v4/util/MapCollections;

    #@2e
    iget v5, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@30
    invoke-virtual {v4, v5, v1}, Landroid/support/v4/util/MapCollections;->colGetEntry(II)Ljava/lang/Object;

    #@33
    move-result-object v4

    #@34
    invoke-static {v3, v4}, Landroid/support/v4/util/ContainerHelpers;->equal(Ljava/lang/Object;Ljava/lang/Object;)Z

    #@37
    move-result v3

    #@38
    if-eqz v3, :cond_2

    #@3a
    :goto_1
    move v2, v1

    #@3b
    goto :goto_0

    #@3c
    :cond_2
    move v1, v2

    #@3d
    goto :goto_1
.end method

.method public getKey()Ljava/lang/Object;
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TK;"
        }
    .end annotation

    #@0
    .prologue
    .line 106
    .local p0, "this":Landroid/support/v4/util/MapCollections$MapIterator;, "Landroid/support/v4/util/MapCollections<TK;TV;>.MapIterator;"
    iget-boolean v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mEntryValid:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 107
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "This container does not support retaining Map.Entry objects"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 110
    :cond_0
    iget-object v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->this$0:Landroid/support/v4/util/MapCollections;

    #@e
    iget v1, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@10
    const/4 v2, 0x0

    #@11
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/util/MapCollections;->colGetEntry(II)Ljava/lang/Object;

    #@14
    move-result-object v0

    #@15
    return-object v0
.end method

.method public getValue()Ljava/lang/Object;
    .locals 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TV;"
        }
    .end annotation

    #@0
    .prologue
    .line 115
    .local p0, "this":Landroid/support/v4/util/MapCollections$MapIterator;, "Landroid/support/v4/util/MapCollections<TK;TV;>.MapIterator;"
    iget-boolean v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mEntryValid:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 116
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "This container does not support retaining Map.Entry objects"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 119
    :cond_0
    iget-object v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->this$0:Landroid/support/v4/util/MapCollections;

    #@e
    iget v1, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@10
    const/4 v2, 0x1

    #@11
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/util/MapCollections;->colGetEntry(II)Ljava/lang/Object;

    #@14
    move-result-object v0

    #@15
    return-object v0
.end method

.method public hasNext()Z
    .locals 2

    #@0
    .prologue
    .line 83
    .local p0, "this":Landroid/support/v4/util/MapCollections$MapIterator;, "Landroid/support/v4/util/MapCollections<TK;TV;>.MapIterator;"
    iget v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@2
    iget v1, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mEnd:I

    #@4
    if-ge v0, v1, :cond_0

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

.method public final hashCode()I
    .locals 6

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/util/MapCollections$MapIterator;, "Landroid/support/v4/util/MapCollections<TK;TV;>.MapIterator;"
    const/4 v2, 0x0

    #@1
    .line 147
    iget-boolean v3, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mEntryValid:Z

    #@3
    if-nez v3, :cond_0

    #@5
    .line 148
    new-instance v2, Ljava/lang/IllegalStateException;

    #@7
    const-string v3, "This container does not support retaining Map.Entry objects"

    #@9
    invoke-direct {v2, v3}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@c
    throw v2

    #@d
    .line 151
    :cond_0
    iget-object v3, p0, Landroid/support/v4/util/MapCollections$MapIterator;->this$0:Landroid/support/v4/util/MapCollections;

    #@f
    iget v4, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@11
    invoke-virtual {v3, v4, v2}, Landroid/support/v4/util/MapCollections;->colGetEntry(II)Ljava/lang/Object;

    #@14
    move-result-object v0

    #@15
    .line 152
    .local v0, "key":Ljava/lang/Object;
    iget-object v3, p0, Landroid/support/v4/util/MapCollections$MapIterator;->this$0:Landroid/support/v4/util/MapCollections;

    #@17
    iget v4, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@19
    const/4 v5, 0x1

    #@1a
    invoke-virtual {v3, v4, v5}, Landroid/support/v4/util/MapCollections;->colGetEntry(II)Ljava/lang/Object;

    #@1d
    move-result-object v1

    #@1e
    .line 153
    .local v1, "value":Ljava/lang/Object;
    if-nez v0, :cond_1

    #@20
    move v3, v2

    #@21
    :goto_0
    if-nez v1, :cond_2

    #@23
    :goto_1
    xor-int/2addr v2, v3

    #@24
    return v2

    #@25
    :cond_1
    invoke-virtual {v0}, Ljava/lang/Object;->hashCode()I

    #@28
    move-result v3

    #@29
    goto :goto_0

    #@2a
    :cond_2
    invoke-virtual {v1}, Ljava/lang/Object;->hashCode()I

    #@2d
    move-result v2

    #@2e
    goto :goto_1
.end method

.method public bridge synthetic next()Ljava/lang/Object;
    .locals 1

    #@0
    .prologue
    .line 71
    .local p0, "this":Landroid/support/v4/util/MapCollections$MapIterator;, "Landroid/support/v4/util/MapCollections<TK;TV;>.MapIterator;"
    invoke-virtual {p0}, Landroid/support/v4/util/MapCollections$MapIterator;->next()Ljava/util/Map$Entry;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public next()Ljava/util/Map$Entry;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/Map$Entry",
            "<TK;TV;>;"
        }
    .end annotation

    #@0
    .prologue
    .line 88
    .local p0, "this":Landroid/support/v4/util/MapCollections$MapIterator;, "Landroid/support/v4/util/MapCollections<TK;TV;>.MapIterator;"
    iget v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@2
    add-int/lit8 v0, v0, 0x1

    #@4
    iput v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@6
    .line 89
    const/4 v0, 0x1

    #@7
    iput-boolean v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mEntryValid:Z

    #@9
    .line 90
    return-object p0
.end method

.method public remove()V
    .locals 2

    #@0
    .prologue
    .line 95
    .local p0, "this":Landroid/support/v4/util/MapCollections$MapIterator;, "Landroid/support/v4/util/MapCollections<TK;TV;>.MapIterator;"
    iget-boolean v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mEntryValid:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 96
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    invoke-direct {v0}, Ljava/lang/IllegalStateException;-><init>()V

    #@9
    throw v0

    #@a
    .line 98
    :cond_0
    iget-object v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->this$0:Landroid/support/v4/util/MapCollections;

    #@c
    iget v1, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@e
    invoke-virtual {v0, v1}, Landroid/support/v4/util/MapCollections;->colRemoveAt(I)V

    #@11
    .line 99
    iget v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@13
    add-int/lit8 v0, v0, -0x1

    #@15
    iput v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@17
    .line 100
    iget v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mEnd:I

    #@19
    add-int/lit8 v0, v0, -0x1

    #@1b
    iput v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mEnd:I

    #@1d
    .line 101
    const/4 v0, 0x0

    #@1e
    iput-boolean v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mEntryValid:Z

    #@20
    .line 102
    return-void
.end method

.method public setValue(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TV;)TV;"
        }
    .end annotation

    #@0
    .prologue
    .line 124
    .local p0, "this":Landroid/support/v4/util/MapCollections$MapIterator;, "Landroid/support/v4/util/MapCollections<TK;TV;>.MapIterator;"
    .local p1, "object":Ljava/lang/Object;, "TV;"
    iget-boolean v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mEntryValid:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 125
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "This container does not support retaining Map.Entry objects"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 128
    :cond_0
    iget-object v0, p0, Landroid/support/v4/util/MapCollections$MapIterator;->this$0:Landroid/support/v4/util/MapCollections;

    #@e
    iget v1, p0, Landroid/support/v4/util/MapCollections$MapIterator;->mIndex:I

    #@10
    invoke-virtual {v0, v1, p1}, Landroid/support/v4/util/MapCollections;->colSetValue(ILjava/lang/Object;)Ljava/lang/Object;

    #@13
    move-result-object v0

    #@14
    return-object v0
.end method

.method public final toString()Ljava/lang/String;
    .locals 2

    #@0
    .prologue
    .line 159
    .local p0, "this":Landroid/support/v4/util/MapCollections$MapIterator;, "Landroid/support/v4/util/MapCollections<TK;TV;>.MapIterator;"
    new-instance v0, Ljava/lang/StringBuilder;

    #@2
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    #@5
    invoke-virtual {p0}, Landroid/support/v4/util/MapCollections$MapIterator;->getKey()Ljava/lang/Object;

    #@8
    move-result-object v1

    #@9
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@c
    move-result-object v0

    #@d
    const-string v1, "="

    #@f
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v0

    #@13
    invoke-virtual {p0}, Landroid/support/v4/util/MapCollections$MapIterator;->getValue()Ljava/lang/Object;

    #@16
    move-result-object v1

    #@17
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@1a
    move-result-object v0

    #@1b
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1e
    move-result-object v0

    #@1f
    return-object v0
.end method
