.class Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;
.super Landroid/support/v4/widget/ViewDragHelper$Callback;
.source "DrawerLayout.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/DrawerLayout;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "ViewDragCallback"
.end annotation


# instance fields
.field private final mAbsGravity:I

.field private mDragger:Landroid/support/v4/widget/ViewDragHelper;

.field private final mPeekRunnable:Ljava/lang/Runnable;

.field final synthetic this$0:Landroid/support/v4/widget/DrawerLayout;


# direct methods
.method public constructor <init>(Landroid/support/v4/widget/DrawerLayout;I)V
    .locals 1
    .param p2, "gravity"    # I

    #@0
    .prologue
    .line 1605
    iput-object p1, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@2
    invoke-direct {p0}, Landroid/support/v4/widget/ViewDragHelper$Callback;-><init>()V

    #@5
    .line 1599
    new-instance v0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback$1;

    #@7
    invoke-direct {v0, p0}, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback$1;-><init>(Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;)V

    #@a
    iput-object v0, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mPeekRunnable:Ljava/lang/Runnable;

    #@c
    .line 1606
    iput p2, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mAbsGravity:I

    #@e
    .line 1607
    return-void
.end method

.method static synthetic access$000(Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@0
    .prologue
    .line 1595
    invoke-direct {p0}, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->peekDrawer()V

    #@3
    return-void
.end method

.method private closeOtherDrawer()V
    .locals 3

    #@0
    .prologue
    const/4 v0, 0x3

    #@1
    .line 1656
    iget v2, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mAbsGravity:I

    #@3
    if-ne v2, v0, :cond_0

    #@5
    const/4 v0, 0x5

    #@6
    .line 1657
    .local v0, "otherGrav":I
    :cond_0
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@8
    invoke-virtual {v2, v0}, Landroid/support/v4/widget/DrawerLayout;->findDrawerWithGravity(I)Landroid/view/View;

    #@b
    move-result-object v1

    #@c
    .line 1658
    .local v1, "toClose":Landroid/view/View;
    if-eqz v1, :cond_1

    #@e
    .line 1659
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@10
    invoke-virtual {v2, v1}, Landroid/support/v4/widget/DrawerLayout;->closeDrawer(Landroid/view/View;)V

    #@13
    .line 1661
    :cond_1
    return-void
.end method

.method private peekDrawer()V
    .locals 9

    #@0
    .prologue
    const/4 v8, 0x3

    #@1
    const/4 v6, 0x1

    #@2
    const/4 v5, 0x0

    #@3
    .line 1690
    iget-object v7, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@5
    invoke-virtual {v7}, Landroid/support/v4/widget/ViewDragHelper;->getEdgeSize()I

    #@8
    move-result v3

    #@9
    .line 1691
    .local v3, "peekDistance":I
    iget v7, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mAbsGravity:I

    #@b
    if-ne v7, v8, :cond_4

    #@d
    move v1, v6

    #@e
    .line 1692
    .local v1, "leftEdge":Z
    :goto_0
    if-eqz v1, :cond_5

    #@10
    .line 1693
    iget-object v7, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@12
    invoke-virtual {v7, v8}, Landroid/support/v4/widget/DrawerLayout;->findDrawerWithGravity(I)Landroid/view/View;

    #@15
    move-result-object v4

    #@16
    .line 1694
    .local v4, "toCapture":Landroid/view/View;
    if-eqz v4, :cond_0

    #@18
    invoke-virtual {v4}, Landroid/view/View;->getWidth()I

    #@1b
    move-result v5

    #@1c
    neg-int v5, v5

    #@1d
    :cond_0
    add-int v0, v5, v3

    #@1f
    .line 1700
    .local v0, "childLeft":I
    :goto_1
    if-eqz v4, :cond_3

    #@21
    if-eqz v1, :cond_1

    #@23
    invoke-virtual {v4}, Landroid/view/View;->getLeft()I

    #@26
    move-result v5

    #@27
    if-lt v5, v0, :cond_2

    #@29
    :cond_1
    if-nez v1, :cond_3

    #@2b
    invoke-virtual {v4}, Landroid/view/View;->getLeft()I

    #@2e
    move-result v5

    #@2f
    if-le v5, v0, :cond_3

    #@31
    :cond_2
    iget-object v5, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@33
    invoke-virtual {v5, v4}, Landroid/support/v4/widget/DrawerLayout;->getDrawerLockMode(Landroid/view/View;)I

    #@36
    move-result v5

    #@37
    if-nez v5, :cond_3

    #@39
    .line 1703
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3c
    move-result-object v2

    #@3d
    check-cast v2, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@3f
    .line 1704
    .local v2, "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    iget-object v5, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@41
    invoke-virtual {v4}, Landroid/view/View;->getTop()I

    #@44
    move-result v7

    #@45
    invoke-virtual {v5, v4, v0, v7}, Landroid/support/v4/widget/ViewDragHelper;->smoothSlideViewTo(Landroid/view/View;II)Z

    #@48
    .line 1705
    iput-boolean v6, v2, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->isPeeking:Z

    #@4a
    .line 1706
    iget-object v5, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@4c
    invoke-virtual {v5}, Landroid/support/v4/widget/DrawerLayout;->invalidate()V

    #@4f
    .line 1708
    invoke-direct {p0}, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->closeOtherDrawer()V

    #@52
    .line 1710
    iget-object v5, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@54
    invoke-virtual {v5}, Landroid/support/v4/widget/DrawerLayout;->cancelChildViewTouch()V

    #@57
    .line 1712
    .end local v2    # "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    :cond_3
    return-void

    #@58
    .end local v0    # "childLeft":I
    .end local v1    # "leftEdge":Z
    .end local v4    # "toCapture":Landroid/view/View;
    :cond_4
    move v1, v5

    #@59
    .line 1691
    goto :goto_0

    #@5a
    .line 1696
    .restart local v1    # "leftEdge":Z
    :cond_5
    iget-object v5, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@5c
    const/4 v7, 0x5

    #@5d
    invoke-virtual {v5, v7}, Landroid/support/v4/widget/DrawerLayout;->findDrawerWithGravity(I)Landroid/view/View;

    #@60
    move-result-object v4

    #@61
    .line 1697
    .restart local v4    # "toCapture":Landroid/view/View;
    iget-object v5, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@63
    invoke-virtual {v5}, Landroid/support/v4/widget/DrawerLayout;->getWidth()I

    #@66
    move-result v5

    #@67
    sub-int v0, v5, v3

    #@69
    .restart local v0    # "childLeft":I
    goto :goto_1
.end method


# virtual methods
.method public clampViewPositionHorizontal(Landroid/view/View;II)I
    .locals 3
    .param p1, "child"    # Landroid/view/View;
    .param p2, "left"    # I
    .param p3, "dx"    # I

    #@0
    .prologue
    .line 1747
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@2
    const/4 v2, 0x3

    #@3
    invoke-virtual {v1, p1, v2}, Landroid/support/v4/widget/DrawerLayout;->checkDrawerViewAbsoluteGravity(Landroid/view/View;I)Z

    #@6
    move-result v1

    #@7
    if-eqz v1, :cond_0

    #@9
    .line 1748
    invoke-virtual {p1}, Landroid/view/View;->getWidth()I

    #@c
    move-result v1

    #@d
    neg-int v1, v1

    #@e
    const/4 v2, 0x0

    #@f
    invoke-static {p2, v2}, Ljava/lang/Math;->min(II)I

    #@12
    move-result v2

    #@13
    invoke-static {v1, v2}, Ljava/lang/Math;->max(II)I

    #@16
    move-result v1

    #@17
    .line 1751
    :goto_0
    return v1

    #@18
    .line 1750
    :cond_0
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@1a
    invoke-virtual {v1}, Landroid/support/v4/widget/DrawerLayout;->getWidth()I

    #@1d
    move-result v0

    #@1e
    .line 1751
    .local v0, "width":I
    invoke-virtual {p1}, Landroid/view/View;->getWidth()I

    #@21
    move-result v1

    #@22
    sub-int v1, v0, v1

    #@24
    invoke-static {p2, v0}, Ljava/lang/Math;->min(II)I

    #@27
    move-result v2

    #@28
    invoke-static {v1, v2}, Ljava/lang/Math;->max(II)I

    #@2b
    move-result v1

    #@2c
    goto :goto_0
.end method

.method public clampViewPositionVertical(Landroid/view/View;II)I
    .locals 1
    .param p1, "child"    # Landroid/view/View;
    .param p2, "top"    # I
    .param p3, "dy"    # I

    #@0
    .prologue
    .line 1757
    invoke-virtual {p1}, Landroid/view/View;->getTop()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getViewHorizontalDragRange(Landroid/view/View;)I
    .locals 1
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 1742
    iget-object v0, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/DrawerLayout;->isDrawerView(Landroid/view/View;)Z

    #@5
    move-result v0

    #@6
    if-eqz v0, :cond_0

    #@8
    invoke-virtual {p1}, Landroid/view/View;->getWidth()I

    #@b
    move-result v0

    #@c
    :goto_0
    return v0

    #@d
    :cond_0
    const/4 v0, 0x0

    #@e
    goto :goto_0
.end method

.method public onEdgeDragStarted(II)V
    .locals 3
    .param p1, "edgeFlags"    # I
    .param p2, "pointerId"    # I

    #@0
    .prologue
    .line 1729
    and-int/lit8 v1, p1, 0x1

    #@2
    const/4 v2, 0x1

    #@3
    if-ne v1, v2, :cond_1

    #@5
    .line 1730
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@7
    const/4 v2, 0x3

    #@8
    invoke-virtual {v1, v2}, Landroid/support/v4/widget/DrawerLayout;->findDrawerWithGravity(I)Landroid/view/View;

    #@b
    move-result-object v0

    #@c
    .line 1735
    .local v0, "toCapture":Landroid/view/View;
    :goto_0
    if-eqz v0, :cond_0

    #@e
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@10
    invoke-virtual {v1, v0}, Landroid/support/v4/widget/DrawerLayout;->getDrawerLockMode(Landroid/view/View;)I

    #@13
    move-result v1

    #@14
    if-nez v1, :cond_0

    #@16
    .line 1736
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@18
    invoke-virtual {v1, v0, p2}, Landroid/support/v4/widget/ViewDragHelper;->captureChildView(Landroid/view/View;I)V

    #@1b
    .line 1738
    :cond_0
    return-void

    #@1c
    .line 1732
    .end local v0    # "toCapture":Landroid/view/View;
    :cond_1
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@1e
    const/4 v2, 0x5

    #@1f
    invoke-virtual {v1, v2}, Landroid/support/v4/widget/DrawerLayout;->findDrawerWithGravity(I)Landroid/view/View;

    #@22
    move-result-object v0

    #@23
    .restart local v0    # "toCapture":Landroid/view/View;
    goto :goto_0
.end method

.method public onEdgeLock(I)Z
    .locals 1
    .param p1, "edgeFlags"    # I

    #@0
    .prologue
    .line 1723
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public onEdgeTouched(II)V
    .locals 4
    .param p1, "edgeFlags"    # I
    .param p2, "pointerId"    # I

    #@0
    .prologue
    .line 1684
    iget-object v0, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@2
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mPeekRunnable:Ljava/lang/Runnable;

    #@4
    const-wide/16 v2, 0xa0

    #@6
    invoke-virtual {v0, v1, v2, v3}, Landroid/support/v4/widget/DrawerLayout;->postDelayed(Ljava/lang/Runnable;J)Z

    #@9
    .line 1685
    return-void
.end method

.method public onViewCaptured(Landroid/view/View;I)V
    .locals 2
    .param p1, "capturedChild"    # Landroid/view/View;
    .param p2, "activePointerId"    # I

    #@0
    .prologue
    .line 1649
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v0

    #@4
    check-cast v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;

    #@6
    .line 1650
    .local v0, "lp":Landroid/support/v4/widget/DrawerLayout$LayoutParams;
    const/4 v1, 0x0

    #@7
    iput-boolean v1, v0, Landroid/support/v4/widget/DrawerLayout$LayoutParams;->isPeeking:Z

    #@9
    .line 1652
    invoke-direct {p0}, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->closeOtherDrawer()V

    #@c
    .line 1653
    return-void
.end method

.method public onViewDragStateChanged(I)V
    .locals 3
    .param p1, "state"    # I

    #@0
    .prologue
    .line 1627
    iget-object v0, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@2
    iget v1, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mAbsGravity:I

    #@4
    iget-object v2, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@6
    invoke-virtual {v2}, Landroid/support/v4/widget/ViewDragHelper;->getCapturedView()Landroid/view/View;

    #@9
    move-result-object v2

    #@a
    invoke-virtual {v0, v1, p1, v2}, Landroid/support/v4/widget/DrawerLayout;->updateDrawerState(IILandroid/view/View;)V

    #@d
    .line 1628
    return-void
.end method

.method public onViewPositionChanged(Landroid/view/View;IIII)V
    .locals 5
    .param p1, "changedView"    # Landroid/view/View;
    .param p2, "left"    # I
    .param p3, "top"    # I
    .param p4, "dx"    # I
    .param p5, "dy"    # I

    #@0
    .prologue
    .line 1633
    invoke-virtual {p1}, Landroid/view/View;->getWidth()I

    #@3
    move-result v0

    #@4
    .line 1636
    .local v0, "childWidth":I
    iget-object v3, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@6
    const/4 v4, 0x3

    #@7
    invoke-virtual {v3, p1, v4}, Landroid/support/v4/widget/DrawerLayout;->checkDrawerViewAbsoluteGravity(Landroid/view/View;I)Z

    #@a
    move-result v3

    #@b
    if-eqz v3, :cond_0

    #@d
    .line 1637
    add-int v3, v0, p2

    #@f
    int-to-float v3, v3

    #@10
    int-to-float v4, v0

    #@11
    div-float v1, v3, v4

    #@13
    .line 1642
    .local v1, "offset":F
    :goto_0
    iget-object v3, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@15
    invoke-virtual {v3, p1, v1}, Landroid/support/v4/widget/DrawerLayout;->setDrawerViewOffset(Landroid/view/View;F)V

    #@18
    .line 1643
    const/4 v3, 0x0

    #@19
    cmpl-float v3, v1, v3

    #@1b
    if-nez v3, :cond_1

    #@1d
    const/4 v3, 0x4

    #@1e
    :goto_1
    invoke-virtual {p1, v3}, Landroid/view/View;->setVisibility(I)V

    #@21
    .line 1644
    iget-object v3, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@23
    invoke-virtual {v3}, Landroid/support/v4/widget/DrawerLayout;->invalidate()V

    #@26
    .line 1645
    return-void

    #@27
    .line 1639
    .end local v1    # "offset":F
    :cond_0
    iget-object v3, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@29
    invoke-virtual {v3}, Landroid/support/v4/widget/DrawerLayout;->getWidth()I

    #@2c
    move-result v2

    #@2d
    .line 1640
    .local v2, "width":I
    sub-int v3, v2, p2

    #@2f
    int-to-float v3, v3

    #@30
    int-to-float v4, v0

    #@31
    div-float v1, v3, v4

    #@33
    .restart local v1    # "offset":F
    goto :goto_0

    #@34
    .line 1643
    .end local v2    # "width":I
    :cond_1
    const/4 v3, 0x0

    #@35
    goto :goto_1
.end method

.method public onViewReleased(Landroid/view/View;FF)V
    .locals 8
    .param p1, "releasedChild"    # Landroid/view/View;
    .param p2, "xvel"    # F
    .param p3, "yvel"    # F

    #@0
    .prologue
    const/high16 v7, 0x3f000000    # 0.5f

    #@2
    const/4 v6, 0x0

    #@3
    .line 1667
    iget-object v4, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@5
    invoke-virtual {v4, p1}, Landroid/support/v4/widget/DrawerLayout;->getDrawerViewOffset(Landroid/view/View;)F

    #@8
    move-result v2

    #@9
    .line 1668
    .local v2, "offset":F
    invoke-virtual {p1}, Landroid/view/View;->getWidth()I

    #@c
    move-result v0

    #@d
    .line 1671
    .local v0, "childWidth":I
    iget-object v4, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@f
    const/4 v5, 0x3

    #@10
    invoke-virtual {v4, p1, v5}, Landroid/support/v4/widget/DrawerLayout;->checkDrawerViewAbsoluteGravity(Landroid/view/View;I)Z

    #@13
    move-result v4

    #@14
    if-eqz v4, :cond_2

    #@16
    .line 1672
    cmpl-float v4, p2, v6

    #@18
    if-gtz v4, :cond_0

    #@1a
    cmpl-float v4, p2, v6

    #@1c
    if-nez v4, :cond_1

    #@1e
    cmpl-float v4, v2, v7

    #@20
    if-lez v4, :cond_1

    #@22
    :cond_0
    const/4 v1, 0x0

    #@23
    .line 1678
    .local v1, "left":I
    :goto_0
    iget-object v4, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@25
    invoke-virtual {p1}, Landroid/view/View;->getTop()I

    #@28
    move-result v5

    #@29
    invoke-virtual {v4, v1, v5}, Landroid/support/v4/widget/ViewDragHelper;->settleCapturedViewAt(II)Z

    #@2c
    .line 1679
    iget-object v4, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@2e
    invoke-virtual {v4}, Landroid/support/v4/widget/DrawerLayout;->invalidate()V

    #@31
    .line 1680
    return-void

    #@32
    .line 1672
    .end local v1    # "left":I
    :cond_1
    neg-int v1, v0

    #@33
    goto :goto_0

    #@34
    .line 1674
    :cond_2
    iget-object v4, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@36
    invoke-virtual {v4}, Landroid/support/v4/widget/DrawerLayout;->getWidth()I

    #@39
    move-result v3

    #@3a
    .line 1675
    .local v3, "width":I
    cmpg-float v4, p2, v6

    #@3c
    if-ltz v4, :cond_3

    #@3e
    cmpl-float v4, p2, v6

    #@40
    if-nez v4, :cond_4

    #@42
    cmpl-float v4, v2, v7

    #@44
    if-lez v4, :cond_4

    #@46
    :cond_3
    sub-int v1, v3, v0

    #@48
    .restart local v1    # "left":I
    :goto_1
    goto :goto_0

    #@49
    .end local v1    # "left":I
    :cond_4
    move v1, v3

    #@4a
    goto :goto_1
.end method

.method public removeCallbacks()V
    .locals 2

    #@0
    .prologue
    .line 1614
    iget-object v0, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@2
    iget-object v1, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mPeekRunnable:Ljava/lang/Runnable;

    #@4
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/DrawerLayout;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@7
    .line 1615
    return-void
.end method

.method public setDragger(Landroid/support/v4/widget/ViewDragHelper;)V
    .locals 0
    .param p1, "dragger"    # Landroid/support/v4/widget/ViewDragHelper;

    #@0
    .prologue
    .line 1610
    iput-object p1, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mDragger:Landroid/support/v4/widget/ViewDragHelper;

    #@2
    .line 1611
    return-void
.end method

.method public tryCaptureView(Landroid/view/View;I)Z
    .locals 2
    .param p1, "child"    # Landroid/view/View;
    .param p2, "pointerId"    # I

    #@0
    .prologue
    .line 1621
    iget-object v0, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/DrawerLayout;->isDrawerView(Landroid/view/View;)Z

    #@5
    move-result v0

    #@6
    if-eqz v0, :cond_0

    #@8
    iget-object v0, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@a
    iget v1, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->mAbsGravity:I

    #@c
    invoke-virtual {v0, p1, v1}, Landroid/support/v4/widget/DrawerLayout;->checkDrawerViewAbsoluteGravity(Landroid/view/View;I)Z

    #@f
    move-result v0

    #@10
    if-eqz v0, :cond_0

    #@12
    iget-object v0, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->this$0:Landroid/support/v4/widget/DrawerLayout;

    #@14
    invoke-virtual {v0, p1}, Landroid/support/v4/widget/DrawerLayout;->getDrawerLockMode(Landroid/view/View;)I

    #@17
    move-result v0

    #@18
    if-nez v0, :cond_0

    #@1a
    const/4 v0, 0x1

    #@1b
    :goto_0
    return v0

    #@1c
    :cond_0
    const/4 v0, 0x0

    #@1d
    goto :goto_0
.end method
