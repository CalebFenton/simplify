.class Landroid/support/v4/widget/EdgeEffectCompat$EdgeEffectIcsImpl;
.super Ljava/lang/Object;
.source "EdgeEffectCompat.java"

# interfaces
.implements Landroid/support/v4/widget/EdgeEffectCompat$EdgeEffectImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/EdgeEffectCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "EdgeEffectIcsImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 90
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public draw(Ljava/lang/Object;Landroid/graphics/Canvas;)Z
    .locals 1
    .param p1, "edgeEffect"    # Ljava/lang/Object;
    .param p2, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 120
    invoke-static {p1, p2}, Landroid/support/v4/widget/EdgeEffectCompatIcs;->draw(Ljava/lang/Object;Landroid/graphics/Canvas;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public finish(Ljava/lang/Object;)V
    .locals 0
    .param p1, "edgeEffect"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 104
    invoke-static {p1}, Landroid/support/v4/widget/EdgeEffectCompatIcs;->finish(Ljava/lang/Object;)V

    #@3
    .line 105
    return-void
.end method

.method public isFinished(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "edgeEffect"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 100
    invoke-static {p1}, Landroid/support/v4/widget/EdgeEffectCompatIcs;->isFinished(Ljava/lang/Object;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public newEdgeEffect(Landroid/content/Context;)Ljava/lang/Object;
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 92
    invoke-static {p1}, Landroid/support/v4/widget/EdgeEffectCompatIcs;->newEdgeEffect(Landroid/content/Context;)Ljava/lang/Object;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public onAbsorb(Ljava/lang/Object;I)Z
    .locals 1
    .param p1, "edgeEffect"    # Ljava/lang/Object;
    .param p2, "velocity"    # I

    #@0
    .prologue
    .line 116
    invoke-static {p1, p2}, Landroid/support/v4/widget/EdgeEffectCompatIcs;->onAbsorb(Ljava/lang/Object;I)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public onPull(Ljava/lang/Object;F)Z
    .locals 1
    .param p1, "edgeEffect"    # Ljava/lang/Object;
    .param p2, "deltaDistance"    # F

    #@0
    .prologue
    .line 108
    invoke-static {p1, p2}, Landroid/support/v4/widget/EdgeEffectCompatIcs;->onPull(Ljava/lang/Object;F)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public onRelease(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "edgeEffect"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 112
    invoke-static {p1}, Landroid/support/v4/widget/EdgeEffectCompatIcs;->onRelease(Ljava/lang/Object;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public setSize(Ljava/lang/Object;II)V
    .locals 0
    .param p1, "edgeEffect"    # Ljava/lang/Object;
    .param p2, "width"    # I
    .param p3, "height"    # I

    #@0
    .prologue
    .line 96
    invoke-static {p1, p2, p3}, Landroid/support/v4/widget/EdgeEffectCompatIcs;->setSize(Ljava/lang/Object;II)V

    #@3
    .line 97
    return-void
.end method
