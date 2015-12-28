.class Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;
.super Landroid/support/v4/widget/ViewDragHelper$Callback;
.source "SlidingPaneLayout.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/SlidingPaneLayout;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "DragHelperCallback"
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v4/widget/SlidingPaneLayout;


# direct methods
.method private constructor <init>(Landroid/support/v4/widget/SlidingPaneLayout;)V
    .locals 0

    #@0
    .prologue
    .line 1296
    iput-object p1, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@2
    invoke-direct {p0}, Landroid/support/v4/widget/ViewDragHelper$Callback;-><init>()V

    #@5
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v4/widget/SlidingPaneLayout;Landroid/support/v4/widget/SlidingPaneLayout$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v4/widget/SlidingPaneLayout;
    .param p2, "x1"    # Landroid/support/v4/widget/SlidingPaneLayout$1;

    #@0
    .prologue
    .line 1296
    invoke-direct {p0, p1}, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;-><init>(Landroid/support/v4/widget/SlidingPaneLayout;)V

    #@3
    return-void
.end method


# virtual methods
.method public clampViewPositionHorizontal(Landroid/view/View;II)I
    .locals 7
    .param p1, "child"    # Landroid/view/View;
    .param p2, "left"    # I
    .param p3, "dx"    # I

    #@0
    .prologue
    .line 1362
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@2
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;
    invoke-static {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->access$400(Landroid/support/v4/widget/SlidingPaneLayout;)Landroid/view/View;

    #@5
    move-result-object v4

    #@6
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@9
    move-result-object v1

    #@a
    check-cast v1, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@c
    .line 1365
    .local v1, "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@e
    # invokes: Landroid/support/v4/widget/SlidingPaneLayout;->isLayoutRtlSupport()Z
    invoke-static {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->access$700(Landroid/support/v4/widget/SlidingPaneLayout;)Z

    #@11
    move-result v4

    #@12
    if-eqz v4, :cond_0

    #@14
    .line 1366
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@16
    invoke-virtual {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->getWidth()I

    #@19
    move-result v4

    #@1a
    iget-object v5, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@1c
    invoke-virtual {v5}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingRight()I

    #@1f
    move-result v5

    #@20
    iget v6, v1, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->rightMargin:I

    #@22
    add-int/2addr v5, v6

    #@23
    iget-object v6, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@25
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;
    invoke-static {v6}, Landroid/support/v4/widget/SlidingPaneLayout;->access$400(Landroid/support/v4/widget/SlidingPaneLayout;)Landroid/view/View;

    #@28
    move-result-object v6

    #@29
    invoke-virtual {v6}, Landroid/view/View;->getWidth()I

    #@2c
    move-result v6

    #@2d
    add-int/2addr v5, v6

    #@2e
    sub-int v3, v4, v5

    #@30
    .line 1368
    .local v3, "startBound":I
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@32
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideRange:I
    invoke-static {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->access$800(Landroid/support/v4/widget/SlidingPaneLayout;)I

    #@35
    move-result v4

    #@36
    sub-int v0, v3, v4

    #@38
    .line 1369
    .local v0, "endBound":I
    invoke-static {p2, v3}, Ljava/lang/Math;->min(II)I

    #@3b
    move-result v4

    #@3c
    invoke-static {v4, v0}, Ljava/lang/Math;->max(II)I

    #@3f
    move-result v2

    #@40
    .line 1375
    .local v2, "newLeft":I
    :goto_0
    return v2

    #@41
    .line 1371
    .end local v0    # "endBound":I
    .end local v2    # "newLeft":I
    .end local v3    # "startBound":I
    :cond_0
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@43
    invoke-virtual {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingLeft()I

    #@46
    move-result v4

    #@47
    iget v5, v1, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->leftMargin:I

    #@49
    add-int v3, v4, v5

    #@4b
    .line 1372
    .restart local v3    # "startBound":I
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@4d
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideRange:I
    invoke-static {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->access$800(Landroid/support/v4/widget/SlidingPaneLayout;)I

    #@50
    move-result v4

    #@51
    add-int v0, v3, v4

    #@53
    .line 1373
    .restart local v0    # "endBound":I
    invoke-static {p2, v3}, Ljava/lang/Math;->max(II)I

    #@56
    move-result v4

    #@57
    invoke-static {v4, v0}, Ljava/lang/Math;->min(II)I

    #@5a
    move-result v2

    #@5b
    .restart local v2    # "newLeft":I
    goto :goto_0
.end method

.method public clampViewPositionVertical(Landroid/view/View;II)I
    .locals 1
    .param p1, "child"    # Landroid/view/View;
    .param p2, "top"    # I
    .param p3, "dy"    # I

    #@0
    .prologue
    .line 1382
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
    .line 1357
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@2
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideRange:I
    invoke-static {v0}, Landroid/support/v4/widget/SlidingPaneLayout;->access$800(Landroid/support/v4/widget/SlidingPaneLayout;)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public onEdgeDragStarted(II)V
    .locals 2
    .param p1, "edgeFlags"    # I
    .param p2, "pointerId"    # I

    #@0
    .prologue
    .line 1387
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@2
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;
    invoke-static {v0}, Landroid/support/v4/widget/SlidingPaneLayout;->access$200(Landroid/support/v4/widget/SlidingPaneLayout;)Landroid/support/v4/widget/ViewDragHelper;

    #@5
    move-result-object v0

    #@6
    iget-object v1, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@8
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;
    invoke-static {v1}, Landroid/support/v4/widget/SlidingPaneLayout;->access$400(Landroid/support/v4/widget/SlidingPaneLayout;)Landroid/view/View;

    #@b
    move-result-object v1

    #@c
    invoke-virtual {v0, v1, p2}, Landroid/support/v4/widget/ViewDragHelper;->captureChildView(Landroid/view/View;I)V

    #@f
    .line 1388
    return-void
.end method

.method public onViewCaptured(Landroid/view/View;I)V
    .locals 1
    .param p1, "capturedChild"    # Landroid/view/View;
    .param p2, "activePointerId"    # I

    #@0
    .prologue
    .line 1324
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/widget/SlidingPaneLayout;->setAllChildrenVisible()V

    #@5
    .line 1325
    return-void
.end method

.method public onViewDragStateChanged(I)V
    .locals 2
    .param p1, "state"    # I

    #@0
    .prologue
    .line 1309
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@2
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;
    invoke-static {v0}, Landroid/support/v4/widget/SlidingPaneLayout;->access$200(Landroid/support/v4/widget/SlidingPaneLayout;)Landroid/support/v4/widget/ViewDragHelper;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0}, Landroid/support/v4/widget/ViewDragHelper;->getViewDragState()I

    #@9
    move-result v0

    #@a
    if-nez v0, :cond_0

    #@c
    .line 1310
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@e
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F
    invoke-static {v0}, Landroid/support/v4/widget/SlidingPaneLayout;->access$300(Landroid/support/v4/widget/SlidingPaneLayout;)F

    #@11
    move-result v0

    #@12
    const/4 v1, 0x0

    #@13
    cmpl-float v0, v0, v1

    #@15
    if-nez v0, :cond_1

    #@17
    .line 1311
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@19
    iget-object v1, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@1b
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;
    invoke-static {v1}, Landroid/support/v4/widget/SlidingPaneLayout;->access$400(Landroid/support/v4/widget/SlidingPaneLayout;)Landroid/view/View;

    #@1e
    move-result-object v1

    #@1f
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/SlidingPaneLayout;->updateObscuredViewsVisibility(Landroid/view/View;)V

    #@22
    .line 1312
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@24
    iget-object v1, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@26
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;
    invoke-static {v1}, Landroid/support/v4/widget/SlidingPaneLayout;->access$400(Landroid/support/v4/widget/SlidingPaneLayout;)Landroid/view/View;

    #@29
    move-result-object v1

    #@2a
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/SlidingPaneLayout;->dispatchOnPanelClosed(Landroid/view/View;)V

    #@2d
    .line 1313
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@2f
    const/4 v1, 0x0

    #@30
    # setter for: Landroid/support/v4/widget/SlidingPaneLayout;->mPreservedOpenState:Z
    invoke-static {v0, v1}, Landroid/support/v4/widget/SlidingPaneLayout;->access$502(Landroid/support/v4/widget/SlidingPaneLayout;Z)Z

    #@33
    .line 1319
    :cond_0
    :goto_0
    return-void

    #@34
    .line 1315
    :cond_1
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@36
    iget-object v1, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@38
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;
    invoke-static {v1}, Landroid/support/v4/widget/SlidingPaneLayout;->access$400(Landroid/support/v4/widget/SlidingPaneLayout;)Landroid/view/View;

    #@3b
    move-result-object v1

    #@3c
    invoke-virtual {v0, v1}, Landroid/support/v4/widget/SlidingPaneLayout;->dispatchOnPanelOpened(Landroid/view/View;)V

    #@3f
    .line 1316
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@41
    const/4 v1, 0x1

    #@42
    # setter for: Landroid/support/v4/widget/SlidingPaneLayout;->mPreservedOpenState:Z
    invoke-static {v0, v1}, Landroid/support/v4/widget/SlidingPaneLayout;->access$502(Landroid/support/v4/widget/SlidingPaneLayout;Z)Z

    #@45
    goto :goto_0
.end method

.method public onViewPositionChanged(Landroid/view/View;IIII)V
    .locals 1
    .param p1, "changedView"    # Landroid/view/View;
    .param p2, "left"    # I
    .param p3, "top"    # I
    .param p4, "dx"    # I
    .param p5, "dy"    # I

    #@0
    .prologue
    .line 1329
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@2
    # invokes: Landroid/support/v4/widget/SlidingPaneLayout;->onPanelDragged(I)V
    invoke-static {v0, p2}, Landroid/support/v4/widget/SlidingPaneLayout;->access$600(Landroid/support/v4/widget/SlidingPaneLayout;I)V

    #@5
    .line 1330
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@7
    invoke-virtual {v0}, Landroid/support/v4/widget/SlidingPaneLayout;->invalidate()V

    #@a
    .line 1331
    return-void
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
    .line 1335
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@6
    move-result-object v2

    #@7
    check-cast v2, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@9
    .line 1338
    .local v2, "lp":Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@b
    # invokes: Landroid/support/v4/widget/SlidingPaneLayout;->isLayoutRtlSupport()Z
    invoke-static {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->access$700(Landroid/support/v4/widget/SlidingPaneLayout;)Z

    #@e
    move-result v4

    #@f
    if-eqz v4, :cond_3

    #@11
    .line 1339
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@13
    invoke-virtual {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingRight()I

    #@16
    move-result v4

    #@17
    iget v5, v2, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->rightMargin:I

    #@19
    add-int v3, v4, v5

    #@1b
    .line 1340
    .local v3, "startToRight":I
    cmpg-float v4, p2, v6

    #@1d
    if-ltz v4, :cond_0

    #@1f
    cmpl-float v4, p2, v6

    #@21
    if-nez v4, :cond_1

    #@23
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@25
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F
    invoke-static {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->access$300(Landroid/support/v4/widget/SlidingPaneLayout;)F

    #@28
    move-result v4

    #@29
    cmpl-float v4, v4, v7

    #@2b
    if-lez v4, :cond_1

    #@2d
    .line 1341
    :cond_0
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@2f
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideRange:I
    invoke-static {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->access$800(Landroid/support/v4/widget/SlidingPaneLayout;)I

    #@32
    move-result v4

    #@33
    add-int/2addr v3, v4

    #@34
    .line 1343
    :cond_1
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@36
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideableView:Landroid/view/View;
    invoke-static {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->access$400(Landroid/support/v4/widget/SlidingPaneLayout;)Landroid/view/View;

    #@39
    move-result-object v4

    #@3a
    invoke-virtual {v4}, Landroid/view/View;->getWidth()I

    #@3d
    move-result v0

    #@3e
    .line 1344
    .local v0, "childWidth":I
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@40
    invoke-virtual {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->getWidth()I

    #@43
    move-result v4

    #@44
    sub-int/2addr v4, v3

    #@45
    sub-int v1, v4, v0

    #@47
    .line 1351
    .end local v0    # "childWidth":I
    .end local v3    # "startToRight":I
    .local v1, "left":I
    :cond_2
    :goto_0
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@49
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mDragHelper:Landroid/support/v4/widget/ViewDragHelper;
    invoke-static {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->access$200(Landroid/support/v4/widget/SlidingPaneLayout;)Landroid/support/v4/widget/ViewDragHelper;

    #@4c
    move-result-object v4

    #@4d
    invoke-virtual {p1}, Landroid/view/View;->getTop()I

    #@50
    move-result v5

    #@51
    invoke-virtual {v4, v1, v5}, Landroid/support/v4/widget/ViewDragHelper;->settleCapturedViewAt(II)Z

    #@54
    .line 1352
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@56
    invoke-virtual {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->invalidate()V

    #@59
    .line 1353
    return-void

    #@5a
    .line 1346
    .end local v1    # "left":I
    :cond_3
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@5c
    invoke-virtual {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->getPaddingLeft()I

    #@5f
    move-result v4

    #@60
    iget v5, v2, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->leftMargin:I

    #@62
    add-int v1, v4, v5

    #@64
    .line 1347
    .restart local v1    # "left":I
    cmpl-float v4, p2, v6

    #@66
    if-gtz v4, :cond_4

    #@68
    cmpl-float v4, p2, v6

    #@6a
    if-nez v4, :cond_2

    #@6c
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@6e
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideOffset:F
    invoke-static {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->access$300(Landroid/support/v4/widget/SlidingPaneLayout;)F

    #@71
    move-result v4

    #@72
    cmpl-float v4, v4, v7

    #@74
    if-lez v4, :cond_2

    #@76
    .line 1348
    :cond_4
    iget-object v4, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@78
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mSlideRange:I
    invoke-static {v4}, Landroid/support/v4/widget/SlidingPaneLayout;->access$800(Landroid/support/v4/widget/SlidingPaneLayout;)I

    #@7b
    move-result v4

    #@7c
    add-int/2addr v1, v4

    #@7d
    goto :goto_0
.end method

.method public tryCaptureView(Landroid/view/View;I)Z
    .locals 1
    .param p1, "child"    # Landroid/view/View;
    .param p2, "pointerId"    # I

    #@0
    .prologue
    .line 1300
    iget-object v0, p0, Landroid/support/v4/widget/SlidingPaneLayout$DragHelperCallback;->this$0:Landroid/support/v4/widget/SlidingPaneLayout;

    #@2
    # getter for: Landroid/support/v4/widget/SlidingPaneLayout;->mIsUnableToDrag:Z
    invoke-static {v0}, Landroid/support/v4/widget/SlidingPaneLayout;->access$100(Landroid/support/v4/widget/SlidingPaneLayout;)Z

    #@5
    move-result v0

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 1301
    const/4 v0, 0x0

    #@9
    .line 1304
    :goto_0
    return v0

    #@a
    :cond_0
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@d
    move-result-object v0

    #@e
    check-cast v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;

    #@10
    iget-boolean v0, v0, Landroid/support/v4/widget/SlidingPaneLayout$LayoutParams;->slideable:Z

    #@12
    goto :goto_0
.end method
