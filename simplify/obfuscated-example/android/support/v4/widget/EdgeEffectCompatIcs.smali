.class Landroid/support/v4/widget/EdgeEffectCompatIcs;
.super Ljava/lang/Object;
.source "EdgeEffectCompatIcs.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 28
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static draw(Ljava/lang/Object;Landroid/graphics/Canvas;)Z
    .locals 1
    .param p0, "edgeEffect"    # Ljava/lang/Object;
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 62
    check-cast p0, Landroid/widget/EdgeEffect;

    #@2
    .end local p0    # "edgeEffect":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/widget/EdgeEffect;->draw(Landroid/graphics/Canvas;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static finish(Ljava/lang/Object;)V
    .locals 0
    .param p0, "edgeEffect"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 42
    check-cast p0, Landroid/widget/EdgeEffect;

    #@2
    .end local p0    # "edgeEffect":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/widget/EdgeEffect;->finish()V

    #@5
    .line 43
    return-void
.end method

.method public static isFinished(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "edgeEffect"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 38
    check-cast p0, Landroid/widget/EdgeEffect;

    #@2
    .end local p0    # "edgeEffect":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/widget/EdgeEffect;->isFinished()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static newEdgeEffect(Landroid/content/Context;)Ljava/lang/Object;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 30
    new-instance v0, Landroid/widget/EdgeEffect;

    #@2
    invoke-direct {v0, p0}, Landroid/widget/EdgeEffect;-><init>(Landroid/content/Context;)V

    #@5
    return-object v0
.end method

.method public static onAbsorb(Ljava/lang/Object;I)Z
    .locals 1
    .param p0, "edgeEffect"    # Ljava/lang/Object;
    .param p1, "velocity"    # I

    #@0
    .prologue
    .line 57
    check-cast p0, Landroid/widget/EdgeEffect;

    #@2
    .end local p0    # "edgeEffect":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/widget/EdgeEffect;->onAbsorb(I)V

    #@5
    .line 58
    const/4 v0, 0x1

    #@6
    return v0
.end method

.method public static onPull(Ljava/lang/Object;F)Z
    .locals 1
    .param p0, "edgeEffect"    # Ljava/lang/Object;
    .param p1, "deltaDistance"    # F

    #@0
    .prologue
    .line 46
    check-cast p0, Landroid/widget/EdgeEffect;

    #@2
    .end local p0    # "edgeEffect":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/widget/EdgeEffect;->onPull(F)V

    #@5
    .line 47
    const/4 v0, 0x1

    #@6
    return v0
.end method

.method public static onRelease(Ljava/lang/Object;)Z
    .locals 2
    .param p0, "edgeEffect"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 51
    move-object v0, p0

    #@1
    check-cast v0, Landroid/widget/EdgeEffect;

    #@3
    .line 52
    .local v0, "eff":Landroid/widget/EdgeEffect;
    invoke-virtual {v0}, Landroid/widget/EdgeEffect;->onRelease()V

    #@6
    .line 53
    invoke-virtual {v0}, Landroid/widget/EdgeEffect;->isFinished()Z

    #@9
    move-result v1

    #@a
    return v1
.end method

.method public static setSize(Ljava/lang/Object;II)V
    .locals 0
    .param p0, "edgeEffect"    # Ljava/lang/Object;
    .param p1, "width"    # I
    .param p2, "height"    # I

    #@0
    .prologue
    .line 34
    check-cast p0, Landroid/widget/EdgeEffect;

    #@2
    .end local p0    # "edgeEffect":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2}, Landroid/widget/EdgeEffect;->setSize(II)V

    #@5
    .line 35
    return-void
.end method
