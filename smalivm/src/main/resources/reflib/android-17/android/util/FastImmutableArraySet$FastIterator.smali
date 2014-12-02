.class final Landroid/util/FastImmutableArraySet$FastIterator;
.super Ljava/lang/Object;
.source "FastImmutableArraySet.java"

# interfaces
.implements Ljava/util/Iterator;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/util/FastImmutableArraySet;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1a
    name = "FastIterator"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<T:",
        "Ljava/lang/Object;",
        ">",
        "Ljava/lang/Object;",
        "Ljava/util/Iterator",
        "<TT;>;"
    }
.end annotation


# instance fields
.field private final mContents:[Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "[TT;"
        }
    .end annotation
.end field

.field mIndex:I


# direct methods
.method public constructor <init>([Ljava/lang/Object;)V
    .registers 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "([TT;)V"
        }
    .end annotation

    .prologue
    .line 59
    .local p0, "this":Landroid/util/FastImmutableArraySet$FastIterator;, "Landroid/util/FastImmutableArraySet$FastIterator<TT;>;"
    .local p1, "contents":[Ljava/lang/Object;, "[TT;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 60
    iput-object p1, p0, Landroid/util/FastImmutableArraySet$FastIterator;->mContents:[Ljava/lang/Object;

    .line 61
    return-void
.end method


# virtual methods
.method public hasNext()Z
    .registers 3

    .prologue
    .line 65
    .local p0, "this":Landroid/util/FastImmutableArraySet$FastIterator;, "Landroid/util/FastImmutableArraySet$FastIterator<TT;>;"
    iget v0, p0, Landroid/util/FastImmutableArraySet$FastIterator;->mIndex:I

    iget-object v1, p0, Landroid/util/FastImmutableArraySet$FastIterator;->mContents:[Ljava/lang/Object;

    array-length v1, v1

    if-eq v0, v1, :cond_9

    const/4 v0, 0x1

    :goto_8
    return v0

    :cond_9
    const/4 v0, 0x0

    goto :goto_8
.end method

.method public next()Ljava/lang/Object;
    .registers 4
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TT;"
        }
    .end annotation

    .prologue
    .line 70
    .local p0, "this":Landroid/util/FastImmutableArraySet$FastIterator;, "Landroid/util/FastImmutableArraySet$FastIterator<TT;>;"
    iget-object v0, p0, Landroid/util/FastImmutableArraySet$FastIterator;->mContents:[Ljava/lang/Object;

    iget v1, p0, Landroid/util/FastImmutableArraySet$FastIterator;->mIndex:I

    add-int/lit8 v2, v1, 0x1

    iput v2, p0, Landroid/util/FastImmutableArraySet$FastIterator;->mIndex:I

    aget-object v0, v0, v1

    return-object v0
.end method

.method public remove()V
    .registers 2

    .prologue
    .line 75
    .local p0, "this":Landroid/util/FastImmutableArraySet$FastIterator;, "Landroid/util/FastImmutableArraySet$FastIterator<TT;>;"
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    throw v0
.end method
