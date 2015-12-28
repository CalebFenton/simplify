.class Landroid/support/v7/internal/view/menu/BaseWrapper;
.super Ljava/lang/Object;
.source "BaseWrapper.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "<T:",
        "Ljava/lang/Object;",
        ">",
        "Ljava/lang/Object;"
    }
.end annotation


# instance fields
.field final mWrappedObject:Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "TT;"
        }
    .end annotation
.end field


# direct methods
.method constructor <init>(Ljava/lang/Object;)V
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TT;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 23
    .local p0, "this":Landroid/support/v7/internal/view/menu/BaseWrapper;, "Landroid/support/v7/internal/view/menu/BaseWrapper<TT;>;"
    .local p1, "object":Ljava/lang/Object;, "TT;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 24
    if-nez p1, :cond_0

    #@5
    .line 25
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@7
    const-string v1, "Wrapped Object can not be null."

    #@9
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@c
    throw v0

    #@d
    .line 27
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/BaseWrapper;->mWrappedObject:Ljava/lang/Object;

    #@f
    .line 28
    return-void
.end method


# virtual methods
.method public getWrappedObject()Ljava/lang/Object;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()TT;"
        }
    .end annotation

    #@0
    .prologue
    .line 31
    .local p0, "this":Landroid/support/v7/internal/view/menu/BaseWrapper;, "Landroid/support/v7/internal/view/menu/BaseWrapper<TT;>;"
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/BaseWrapper;->mWrappedObject:Ljava/lang/Object;

    #@2
    return-object v0
.end method
