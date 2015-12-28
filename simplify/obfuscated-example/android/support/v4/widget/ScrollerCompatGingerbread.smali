.class Landroid/support/v4/widget/ScrollerCompatGingerbread;
.super Ljava/lang/Object;
.source "ScrollerCompatGingerbread.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 24
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static abortAnimation(Ljava/lang/Object;)V
    .locals 0
    .param p0, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 67
    check-cast p0, Landroid/widget/OverScroller;

    #@2
    .end local p0    # "scroller":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/widget/OverScroller;->abortAnimation()V

    #@5
    .line 68
    return-void
.end method

.method public static computeScrollOffset(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 43
    check-cast p0, Landroid/widget/OverScroller;

    #@2
    .end local p0    # "scroller":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/widget/OverScroller;->computeScrollOffset()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static createScroller(Landroid/content/Context;Landroid/view/animation/Interpolator;)Ljava/lang/Object;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "interpolator"    # Landroid/view/animation/Interpolator;

    #@0
    .prologue
    .line 26
    if-eqz p1, :cond_0

    #@2
    new-instance v0, Landroid/widget/OverScroller;

    #@4
    invoke-direct {v0, p0, p1}, Landroid/widget/OverScroller;-><init>(Landroid/content/Context;Landroid/view/animation/Interpolator;)V

    #@7
    :goto_0
    return-object v0

    #@8
    :cond_0
    new-instance v0, Landroid/widget/OverScroller;

    #@a
    invoke-direct {v0, p0}, Landroid/widget/OverScroller;-><init>(Landroid/content/Context;)V

    #@d
    goto :goto_0
.end method

.method public static fling(Ljava/lang/Object;IIIIIIII)V
    .locals 9
    .param p0, "scroller"    # Ljava/lang/Object;
    .param p1, "startX"    # I
    .param p2, "startY"    # I
    .param p3, "velX"    # I
    .param p4, "velY"    # I
    .param p5, "minX"    # I
    .param p6, "maxX"    # I
    .param p7, "minY"    # I
    .param p8, "maxY"    # I

    #@0
    .prologue
    .line 57
    move-object v0, p0

    #@1
    check-cast v0, Landroid/widget/OverScroller;

    #@3
    move v1, p1

    #@4
    move v2, p2

    #@5
    move v3, p3

    #@6
    move v4, p4

    #@7
    move v5, p5

    #@8
    move v6, p6

    #@9
    move/from16 v7, p7

    #@b
    move/from16 v8, p8

    #@d
    invoke-virtual/range {v0 .. v8}, Landroid/widget/OverScroller;->fling(IIIIIIII)V

    #@10
    .line 58
    return-void
.end method

.method public static fling(Ljava/lang/Object;IIIIIIIIII)V
    .locals 11
    .param p0, "scroller"    # Ljava/lang/Object;
    .param p1, "startX"    # I
    .param p2, "startY"    # I
    .param p3, "velX"    # I
    .param p4, "velY"    # I
    .param p5, "minX"    # I
    .param p6, "maxX"    # I
    .param p7, "minY"    # I
    .param p8, "maxY"    # I
    .param p9, "overX"    # I
    .param p10, "overY"    # I

    #@0
    .prologue
    .line 62
    move-object v0, p0

    #@1
    check-cast v0, Landroid/widget/OverScroller;

    #@3
    move v1, p1

    #@4
    move v2, p2

    #@5
    move v3, p3

    #@6
    move v4, p4

    #@7
    move/from16 v5, p5

    #@9
    move/from16 v6, p6

    #@b
    move/from16 v7, p7

    #@d
    move/from16 v8, p8

    #@f
    move/from16 v9, p9

    #@11
    move/from16 v10, p10

    #@13
    invoke-virtual/range {v0 .. v10}, Landroid/widget/OverScroller;->fling(IIIIIIIIII)V

    #@16
    .line 64
    return-void
.end method

.method public static getCurrX(Ljava/lang/Object;)I
    .locals 1
    .param p0, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 35
    check-cast p0, Landroid/widget/OverScroller;

    #@2
    .end local p0    # "scroller":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/widget/OverScroller;->getCurrX()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getCurrY(Ljava/lang/Object;)I
    .locals 1
    .param p0, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 39
    check-cast p0, Landroid/widget/OverScroller;

    #@2
    .end local p0    # "scroller":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/widget/OverScroller;->getCurrY()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getFinalX(Ljava/lang/Object;)I
    .locals 1
    .param p0, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 84
    check-cast p0, Landroid/widget/OverScroller;

    #@2
    .end local p0    # "scroller":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/widget/OverScroller;->getFinalX()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getFinalY(Ljava/lang/Object;)I
    .locals 1
    .param p0, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 88
    check-cast p0, Landroid/widget/OverScroller;

    #@2
    .end local p0    # "scroller":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/widget/OverScroller;->getFinalY()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isFinished(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 31
    check-cast p0, Landroid/widget/OverScroller;

    #@2
    .end local p0    # "scroller":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/widget/OverScroller;->isFinished()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isOverScrolled(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 80
    check-cast p0, Landroid/widget/OverScroller;

    #@2
    .end local p0    # "scroller":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/widget/OverScroller;->isOverScrolled()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static notifyHorizontalEdgeReached(Ljava/lang/Object;III)V
    .locals 0
    .param p0, "scroller"    # Ljava/lang/Object;
    .param p1, "startX"    # I
    .param p2, "finalX"    # I
    .param p3, "overX"    # I

    #@0
    .prologue
    .line 72
    check-cast p0, Landroid/widget/OverScroller;

    #@2
    .end local p0    # "scroller":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2, p3}, Landroid/widget/OverScroller;->notifyHorizontalEdgeReached(III)V

    #@5
    .line 73
    return-void
.end method

.method public static notifyVerticalEdgeReached(Ljava/lang/Object;III)V
    .locals 0
    .param p0, "scroller"    # Ljava/lang/Object;
    .param p1, "startY"    # I
    .param p2, "finalY"    # I
    .param p3, "overY"    # I

    #@0
    .prologue
    .line 76
    check-cast p0, Landroid/widget/OverScroller;

    #@2
    .end local p0    # "scroller":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2, p3}, Landroid/widget/OverScroller;->notifyVerticalEdgeReached(III)V

    #@5
    .line 77
    return-void
.end method

.method public static startScroll(Ljava/lang/Object;IIII)V
    .locals 0
    .param p0, "scroller"    # Ljava/lang/Object;
    .param p1, "startX"    # I
    .param p2, "startY"    # I
    .param p3, "dx"    # I
    .param p4, "dy"    # I

    #@0
    .prologue
    .line 47
    check-cast p0, Landroid/widget/OverScroller;

    #@2
    .end local p0    # "scroller":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2, p3, p4}, Landroid/widget/OverScroller;->startScroll(IIII)V

    #@5
    .line 48
    return-void
.end method

.method public static startScroll(Ljava/lang/Object;IIIII)V
    .locals 6
    .param p0, "scroller"    # Ljava/lang/Object;
    .param p1, "startX"    # I
    .param p2, "startY"    # I
    .param p3, "dx"    # I
    .param p4, "dy"    # I
    .param p5, "duration"    # I

    #@0
    .prologue
    .line 52
    move-object v0, p0

    #@1
    check-cast v0, Landroid/widget/OverScroller;

    #@3
    move v1, p1

    #@4
    move v2, p2

    #@5
    move v3, p3

    #@6
    move v4, p4

    #@7
    move v5, p5

    #@8
    invoke-virtual/range {v0 .. v5}, Landroid/widget/OverScroller;->startScroll(IIIII)V

    #@b
    .line 53
    return-void
.end method
