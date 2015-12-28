.class Landroid/support/v4/widget/ScrollerCompat$ScrollerCompatImplBase;
.super Ljava/lang/Object;
.source "ScrollerCompat.java"

# interfaces
.implements Landroid/support/v4/widget/ScrollerCompat$ScrollerCompatImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/ScrollerCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "ScrollerCompatImplBase"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 61
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public abortAnimation(Ljava/lang/Object;)V
    .locals 0
    .param p1, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 119
    check-cast p1, Landroid/widget/Scroller;

    #@2
    .end local p1    # "scroller":Ljava/lang/Object;
    invoke-virtual {p1}, Landroid/widget/Scroller;->abortAnimation()V

    #@5
    .line 120
    return-void
.end method

.method public computeScrollOffset(Ljava/lang/Object;)Z
    .locals 2
    .param p1, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 90
    move-object v0, p1

    #@1
    check-cast v0, Landroid/widget/Scroller;

    #@3
    .line 91
    .local v0, "s":Landroid/widget/Scroller;
    invoke-virtual {v0}, Landroid/widget/Scroller;->computeScrollOffset()Z

    #@6
    move-result v1

    #@7
    return v1
.end method

.method public createScroller(Landroid/content/Context;Landroid/view/animation/Interpolator;)Ljava/lang/Object;
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "interpolator"    # Landroid/view/animation/Interpolator;

    #@0
    .prologue
    .line 64
    if-eqz p2, :cond_0

    #@2
    new-instance v0, Landroid/widget/Scroller;

    #@4
    invoke-direct {v0, p1, p2}, Landroid/widget/Scroller;-><init>(Landroid/content/Context;Landroid/view/animation/Interpolator;)V

    #@7
    :goto_0
    return-object v0

    #@8
    :cond_0
    new-instance v0, Landroid/widget/Scroller;

    #@a
    invoke-direct {v0, p1}, Landroid/widget/Scroller;-><init>(Landroid/content/Context;)V

    #@d
    goto :goto_0
.end method

.method public fling(Ljava/lang/Object;IIIIIIII)V
    .locals 9
    .param p1, "scroller"    # Ljava/lang/Object;
    .param p2, "startX"    # I
    .param p3, "startY"    # I
    .param p4, "velX"    # I
    .param p5, "velY"    # I
    .param p6, "minX"    # I
    .param p7, "maxX"    # I
    .param p8, "minY"    # I
    .param p9, "maxY"    # I

    #@0
    .prologue
    .line 108
    move-object v0, p1

    #@1
    check-cast v0, Landroid/widget/Scroller;

    #@3
    move v1, p2

    #@4
    move v2, p3

    #@5
    move v3, p4

    #@6
    move v4, p5

    #@7
    move v5, p6

    #@8
    move/from16 v6, p7

    #@a
    move/from16 v7, p8

    #@c
    move/from16 v8, p9

    #@e
    invoke-virtual/range {v0 .. v8}, Landroid/widget/Scroller;->fling(IIIIIIII)V

    #@11
    .line 109
    return-void
.end method

.method public fling(Ljava/lang/Object;IIIIIIIIII)V
    .locals 9
    .param p1, "scroller"    # Ljava/lang/Object;
    .param p2, "startX"    # I
    .param p3, "startY"    # I
    .param p4, "velX"    # I
    .param p5, "velY"    # I
    .param p6, "minX"    # I
    .param p7, "maxX"    # I
    .param p8, "minY"    # I
    .param p9, "maxY"    # I
    .param p10, "overX"    # I
    .param p11, "overY"    # I

    #@0
    .prologue
    .line 114
    move-object v0, p1

    #@1
    check-cast v0, Landroid/widget/Scroller;

    #@3
    move v1, p2

    #@4
    move v2, p3

    #@5
    move v3, p4

    #@6
    move v4, p5

    #@7
    move v5, p6

    #@8
    move/from16 v6, p7

    #@a
    move/from16 v7, p8

    #@c
    move/from16 v8, p9

    #@e
    invoke-virtual/range {v0 .. v8}, Landroid/widget/Scroller;->fling(IIIIIIII)V

    #@11
    .line 115
    return-void
.end method

.method public getCurrVelocity(Ljava/lang/Object;)F
    .locals 1
    .param p1, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 85
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getCurrX(Ljava/lang/Object;)I
    .locals 1
    .param p1, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 75
    check-cast p1, Landroid/widget/Scroller;

    #@2
    .end local p1    # "scroller":Ljava/lang/Object;
    invoke-virtual {p1}, Landroid/widget/Scroller;->getCurrX()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getCurrY(Ljava/lang/Object;)I
    .locals 1
    .param p1, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 80
    check-cast p1, Landroid/widget/Scroller;

    #@2
    .end local p1    # "scroller":Ljava/lang/Object;
    invoke-virtual {p1}, Landroid/widget/Scroller;->getCurrY()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getFinalX(Ljava/lang/Object;)I
    .locals 1
    .param p1, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 141
    check-cast p1, Landroid/widget/Scroller;

    #@2
    .end local p1    # "scroller":Ljava/lang/Object;
    invoke-virtual {p1}, Landroid/widget/Scroller;->getFinalX()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getFinalY(Ljava/lang/Object;)I
    .locals 1
    .param p1, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 146
    check-cast p1, Landroid/widget/Scroller;

    #@2
    .end local p1    # "scroller":Ljava/lang/Object;
    invoke-virtual {p1}, Landroid/widget/Scroller;->getFinalY()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public isFinished(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 70
    check-cast p1, Landroid/widget/Scroller;

    #@2
    .end local p1    # "scroller":Ljava/lang/Object;
    invoke-virtual {p1}, Landroid/widget/Scroller;->isFinished()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public isOverScrolled(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 136
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public notifyHorizontalEdgeReached(Ljava/lang/Object;III)V
    .locals 0
    .param p1, "scroller"    # Ljava/lang/Object;
    .param p2, "startX"    # I
    .param p3, "finalX"    # I
    .param p4, "overX"    # I

    #@0
    .prologue
    .line 126
    return-void
.end method

.method public notifyVerticalEdgeReached(Ljava/lang/Object;III)V
    .locals 0
    .param p1, "scroller"    # Ljava/lang/Object;
    .param p2, "startY"    # I
    .param p3, "finalY"    # I
    .param p4, "overY"    # I

    #@0
    .prologue
    .line 131
    return-void
.end method

.method public startScroll(Ljava/lang/Object;IIII)V
    .locals 0
    .param p1, "scroller"    # Ljava/lang/Object;
    .param p2, "startX"    # I
    .param p3, "startY"    # I
    .param p4, "dx"    # I
    .param p5, "dy"    # I

    #@0
    .prologue
    .line 96
    check-cast p1, Landroid/widget/Scroller;

    #@2
    .end local p1    # "scroller":Ljava/lang/Object;
    invoke-virtual {p1, p2, p3, p4, p5}, Landroid/widget/Scroller;->startScroll(IIII)V

    #@5
    .line 97
    return-void
.end method

.method public startScroll(Ljava/lang/Object;IIIII)V
    .locals 6
    .param p1, "scroller"    # Ljava/lang/Object;
    .param p2, "startX"    # I
    .param p3, "startY"    # I
    .param p4, "dx"    # I
    .param p5, "dy"    # I
    .param p6, "duration"    # I

    #@0
    .prologue
    .line 102
    move-object v0, p1

    #@1
    check-cast v0, Landroid/widget/Scroller;

    #@3
    move v1, p2

    #@4
    move v2, p3

    #@5
    move v3, p4

    #@6
    move v4, p5

    #@7
    move v5, p6

    #@8
    invoke-virtual/range {v0 .. v5}, Landroid/widget/Scroller;->startScroll(IIIII)V

    #@b
    .line 103
    return-void
.end method
