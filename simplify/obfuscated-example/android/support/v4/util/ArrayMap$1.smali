.class Landroid/support/v4/util/ArrayMap$1;
.super Landroid/support/v4/util/MapCollections;
.source "ArrayMap.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v4/util/ArrayMap;->getCollection()Landroid/support/v4/util/MapCollections;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroid/support/v4/util/MapCollections",
        "<TK;TV;>;"
    }
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v4/util/ArrayMap;


# direct methods
.method constructor <init>(Landroid/support/v4/util/ArrayMap;)V
    .locals 0

    #@0
    .prologue
    .line 73
    .local p0, "this":Landroid/support/v4/util/ArrayMap$1;, "Landroid/support/v4/util/ArrayMap.1;"
    iput-object p1, p0, Landroid/support/v4/util/ArrayMap$1;->this$0:Landroid/support/v4/util/ArrayMap;

    #@2
    invoke-direct {p0}, Landroid/support/v4/util/MapCollections;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method protected colClear()V
    .locals 1

    #@0
    .prologue
    .line 116
    .local p0, "this":Landroid/support/v4/util/ArrayMap$1;, "Landroid/support/v4/util/ArrayMap.1;"
    iget-object v0, p0, Landroid/support/v4/util/ArrayMap$1;->this$0:Landroid/support/v4/util/ArrayMap;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/util/ArrayMap;->clear()V

    #@5
    .line 117
    return-void
.end method

.method protected colGetEntry(II)Ljava/lang/Object;
    .locals 2
    .param p1, "index"    # I
    .param p2, "offset"    # I

    #@0
    .prologue
    .line 81
    .local p0, "this":Landroid/support/v4/util/ArrayMap$1;, "Landroid/support/v4/util/ArrayMap.1;"
    iget-object v0, p0, Landroid/support/v4/util/ArrayMap$1;->this$0:Landroid/support/v4/util/ArrayMap;

    #@2
    iget-object v0, v0, Landroid/support/v4/util/ArrayMap;->mArray:[Ljava/lang/Object;

    #@4
    shl-int/lit8 v1, p1, 0x1

    #@6
    add-int/2addr v1, p2

    #@7
    aget-object v0, v0, v1

    #@9
    return-object v0
.end method

.method protected colGetMap()Ljava/util/Map;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/Map",
            "<TK;TV;>;"
        }
    .end annotation

    #@0
    .prologue
    .line 96
    .local p0, "this":Landroid/support/v4/util/ArrayMap$1;, "Landroid/support/v4/util/ArrayMap.1;"
    iget-object v0, p0, Landroid/support/v4/util/ArrayMap$1;->this$0:Landroid/support/v4/util/ArrayMap;

    #@2
    return-object v0
.end method

.method protected colGetSize()I
    .locals 1

    #@0
    .prologue
    .line 76
    .local p0, "this":Landroid/support/v4/util/ArrayMap$1;, "Landroid/support/v4/util/ArrayMap.1;"
    iget-object v0, p0, Landroid/support/v4/util/ArrayMap$1;->this$0:Landroid/support/v4/util/ArrayMap;

    #@2
    iget v0, v0, Landroid/support/v4/util/ArrayMap;->mSize:I

    #@4
    return v0
.end method

.method protected colIndexOfKey(Ljava/lang/Object;)I
    .locals 1
    .param p1, "key"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 86
    .local p0, "this":Landroid/support/v4/util/ArrayMap$1;, "Landroid/support/v4/util/ArrayMap.1;"
    iget-object v0, p0, Landroid/support/v4/util/ArrayMap$1;->this$0:Landroid/support/v4/util/ArrayMap;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/util/ArrayMap;->indexOfKey(Ljava/lang/Object;)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method protected colIndexOfValue(Ljava/lang/Object;)I
    .locals 1
    .param p1, "value"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 91
    .local p0, "this":Landroid/support/v4/util/ArrayMap$1;, "Landroid/support/v4/util/ArrayMap.1;"
    iget-object v0, p0, Landroid/support/v4/util/ArrayMap$1;->this$0:Landroid/support/v4/util/ArrayMap;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/util/ArrayMap;->indexOfValue(Ljava/lang/Object;)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method protected colPut(Ljava/lang/Object;Ljava/lang/Object;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TK;TV;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 101
    .local p0, "this":Landroid/support/v4/util/ArrayMap$1;, "Landroid/support/v4/util/ArrayMap.1;"
    .local p1, "key":Ljava/lang/Object;, "TK;"
    .local p2, "value":Ljava/lang/Object;, "TV;"
    iget-object v0, p0, Landroid/support/v4/util/ArrayMap$1;->this$0:Landroid/support/v4/util/ArrayMap;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/support/v4/util/ArrayMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@5
    .line 102
    return-void
.end method

.method protected colRemoveAt(I)V
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 111
    .local p0, "this":Landroid/support/v4/util/ArrayMap$1;, "Landroid/support/v4/util/ArrayMap.1;"
    iget-object v0, p0, Landroid/support/v4/util/ArrayMap$1;->this$0:Landroid/support/v4/util/ArrayMap;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/util/ArrayMap;->removeAt(I)Ljava/lang/Object;

    #@5
    .line 112
    return-void
.end method

.method protected colSetValue(ILjava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .param p1, "index"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(ITV;)TV;"
        }
    .end annotation

    #@0
    .prologue
    .line 106
    .local p0, "this":Landroid/support/v4/util/ArrayMap$1;, "Landroid/support/v4/util/ArrayMap.1;"
    .local p2, "value":Ljava/lang/Object;, "TV;"
    iget-object v0, p0, Landroid/support/v4/util/ArrayMap$1;->this$0:Landroid/support/v4/util/ArrayMap;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/support/v4/util/ArrayMap;->setValueAt(ILjava/lang/Object;)Ljava/lang/Object;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method
