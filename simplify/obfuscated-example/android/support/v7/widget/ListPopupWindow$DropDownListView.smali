.class Landroid/support/v7/widget/ListPopupWindow$DropDownListView;
.super Landroid/support/v7/internal/widget/ListViewCompat;
.source "ListPopupWindow.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/widget/ListPopupWindow;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "DropDownListView"
.end annotation


# instance fields
.field private mClickAnimation:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

.field private mDrawsInPressedState:Z

.field private mHijackFocus:Z

.field private mListSelectionHidden:Z

.field private mScrollHelper:Landroid/support/v4/widget/ListViewAutoScrollHelper;


# direct methods
.method public constructor <init>(Landroid/content/Context;Z)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "hijackFocus"    # Z

    #@0
    .prologue
    .line 1548
    const/4 v0, 0x0

    #@1
    sget v1, Landroid/support/v7/appcompat/R$attr;->dropDownListViewStyle:I

    #@3
    invoke-direct {p0, p1, v0, v1}, Landroid/support/v7/internal/widget/ListViewCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@6
    .line 1549
    iput-boolean p2, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mHijackFocus:Z

    #@8
    .line 1550
    const/4 v0, 0x0

    #@9
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setCacheColorHint(I)V

    #@c
    .line 1551
    return-void
.end method

.method static synthetic access$502(Landroid/support/v7/widget/ListPopupWindow$DropDownListView;Z)Z
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/ListPopupWindow$DropDownListView;
    .param p1, "x1"    # Z

    #@0
    .prologue
    .line 1496
    iput-boolean p1, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mListSelectionHidden:Z

    #@2
    return p1
.end method

.method private clearPressedItem()V
    .locals 1

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 1625
    iput-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mDrawsInPressedState:Z

    #@3
    .line 1626
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setPressed(Z)V

    #@6
    .line 1628
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->drawableStateChanged()V

    #@9
    .line 1630
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mClickAnimation:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@b
    if-eqz v0, :cond_0

    #@d
    .line 1631
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mClickAnimation:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@f
    invoke-virtual {v0}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->cancel()V

    #@12
    .line 1632
    const/4 v0, 0x0

    #@13
    iput-object v0, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mClickAnimation:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@15
    .line 1634
    :cond_0
    return-void
.end method

.method private clickPressedItem(Landroid/view/View;I)V
    .locals 2
    .param p1, "child"    # Landroid/view/View;
    .param p2, "position"    # I

    #@0
    .prologue
    .line 1620
    invoke-virtual {p0, p2}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getItemIdAtPosition(I)J

    #@3
    move-result-wide v0

    #@4
    .line 1621
    .local v0, "id":J
    invoke-virtual {p0, p1, p2, v0, v1}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->performItemClick(Landroid/view/View;IJ)Z

    #@7
    .line 1622
    return-void
.end method

.method private setPressedItem(Landroid/view/View;IFF)V
    .locals 1
    .param p1, "child"    # Landroid/view/View;
    .param p2, "position"    # I
    .param p3, "x"    # F
    .param p4, "y"    # F

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 1637
    iput-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mDrawsInPressedState:Z

    #@3
    .line 1641
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setPressed(Z)V

    #@6
    .line 1642
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->layoutChildren()V

    #@9
    .line 1645
    invoke-virtual {p0, p2}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setSelection(I)V

    #@c
    .line 1646
    invoke-virtual {p0, p2, p1, p3, p4}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->positionSelectorLikeTouchCompat(ILandroid/view/View;FF)V

    #@f
    .line 1651
    const/4 v0, 0x0

    #@10
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setSelectorEnabled(Z)V

    #@13
    .line 1655
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->refreshDrawableState()V

    #@16
    .line 1656
    return-void
.end method


# virtual methods
.method public hasFocus()Z
    .locals 1

    #@0
    .prologue
    .line 1696
    iget-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mHijackFocus:Z

    #@2
    if-nez v0, :cond_0

    #@4
    invoke-super {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->hasFocus()Z

    #@7
    move-result v0

    #@8
    if-eqz v0, :cond_1

    #@a
    :cond_0
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_1
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method public hasWindowFocus()Z
    .locals 1

    #@0
    .prologue
    .line 1676
    iget-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mHijackFocus:Z

    #@2
    if-nez v0, :cond_0

    #@4
    invoke-super {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->hasWindowFocus()Z

    #@7
    move-result v0

    #@8
    if-eqz v0, :cond_1

    #@a
    :cond_0
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_1
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method public isFocused()Z
    .locals 1

    #@0
    .prologue
    .line 1686
    iget-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mHijackFocus:Z

    #@2
    if-nez v0, :cond_0

    #@4
    invoke-super {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->isFocused()Z

    #@7
    move-result v0

    #@8
    if-eqz v0, :cond_1

    #@a
    :cond_0
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_1
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method public isInTouchMode()Z
    .locals 1

    #@0
    .prologue
    .line 1666
    iget-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mHijackFocus:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mListSelectionHidden:Z

    #@6
    if-nez v0, :cond_1

    #@8
    :cond_0
    invoke-super {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->isInTouchMode()Z

    #@b
    move-result v0

    #@c
    if-eqz v0, :cond_2

    #@e
    :cond_1
    const/4 v0, 0x1

    #@f
    :goto_0
    return v0

    #@10
    :cond_2
    const/4 v0, 0x0

    #@11
    goto :goto_0
.end method

.method public onForwardedEvent(Landroid/view/MotionEvent;I)Z
    .locals 11
    .param p1, "event"    # Landroid/view/MotionEvent;
    .param p2, "activePointerId"    # I

    #@0
    .prologue
    const/4 v10, 0x1

    #@1
    .line 1560
    const/4 v4, 0x1

    #@2
    .line 1561
    .local v4, "handledEvent":Z
    const/4 v3, 0x0

    #@3
    .line 1563
    .local v3, "clearPressedItem":Z
    invoke-static {p1}, Landroid/support/v4/view/MotionEventCompat;->getActionMasked(Landroid/view/MotionEvent;)I

    #@6
    move-result v0

    #@7
    .line 1564
    .local v0, "actionMasked":I
    packed-switch v0, :pswitch_data_0

    #@a
    .line 1597
    :cond_0
    :goto_0
    if-eqz v4, :cond_1

    #@c
    if-eqz v3, :cond_2

    #@e
    .line 1598
    :cond_1
    invoke-direct {p0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->clearPressedItem()V

    #@11
    .line 1602
    :cond_2
    if-eqz v4, :cond_7

    #@13
    .line 1603
    iget-object v8, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mScrollHelper:Landroid/support/v4/widget/ListViewAutoScrollHelper;

    #@15
    if-nez v8, :cond_3

    #@17
    .line 1604
    new-instance v8, Landroid/support/v4/widget/ListViewAutoScrollHelper;

    #@19
    invoke-direct {v8, p0}, Landroid/support/v4/widget/ListViewAutoScrollHelper;-><init>(Landroid/widget/ListView;)V

    #@1c
    iput-object v8, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mScrollHelper:Landroid/support/v4/widget/ListViewAutoScrollHelper;

    #@1e
    .line 1606
    :cond_3
    iget-object v8, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mScrollHelper:Landroid/support/v4/widget/ListViewAutoScrollHelper;

    #@20
    invoke-virtual {v8, v10}, Landroid/support/v4/widget/ListViewAutoScrollHelper;->setEnabled(Z)Landroid/support/v4/widget/AutoScrollHelper;

    #@23
    .line 1607
    iget-object v8, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mScrollHelper:Landroid/support/v4/widget/ListViewAutoScrollHelper;

    #@25
    invoke-virtual {v8, p0, p1}, Landroid/support/v4/widget/ListViewAutoScrollHelper;->onTouch(Landroid/view/View;Landroid/view/MotionEvent;)Z

    #@28
    .line 1612
    :cond_4
    :goto_1
    return v4

    #@29
    .line 1566
    :pswitch_0
    const/4 v4, 0x0

    #@2a
    .line 1567
    goto :goto_0

    #@2b
    .line 1569
    :pswitch_1
    const/4 v4, 0x0

    #@2c
    .line 1572
    :pswitch_2
    invoke-virtual {p1, p2}, Landroid/view/MotionEvent;->findPointerIndex(I)I

    #@2f
    move-result v1

    #@30
    .line 1573
    .local v1, "activeIndex":I
    if-gez v1, :cond_5

    #@32
    .line 1574
    const/4 v4, 0x0

    #@33
    .line 1575
    goto :goto_0

    #@34
    .line 1578
    :cond_5
    invoke-virtual {p1, v1}, Landroid/view/MotionEvent;->getX(I)F

    #@37
    move-result v8

    #@38
    float-to-int v6, v8

    #@39
    .line 1579
    .local v6, "x":I
    invoke-virtual {p1, v1}, Landroid/view/MotionEvent;->getY(I)F

    #@3c
    move-result v8

    #@3d
    float-to-int v7, v8

    #@3e
    .line 1580
    .local v7, "y":I
    invoke-virtual {p0, v6, v7}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->pointToPosition(II)I

    #@41
    move-result v5

    #@42
    .line 1581
    .local v5, "position":I
    const/4 v8, -0x1

    #@43
    if-ne v5, v8, :cond_6

    #@45
    .line 1582
    const/4 v3, 0x1

    #@46
    .line 1583
    goto :goto_0

    #@47
    .line 1586
    :cond_6
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getFirstVisiblePosition()I

    #@4a
    move-result v8

    #@4b
    sub-int v8, v5, v8

    #@4d
    invoke-virtual {p0, v8}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getChildAt(I)Landroid/view/View;

    #@50
    move-result-object v2

    #@51
    .line 1587
    .local v2, "child":Landroid/view/View;
    int-to-float v8, v6

    #@52
    int-to-float v9, v7

    #@53
    invoke-direct {p0, v2, v5, v8, v9}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setPressedItem(Landroid/view/View;IFF)V

    #@56
    .line 1588
    const/4 v4, 0x1

    #@57
    .line 1590
    if-ne v0, v10, :cond_0

    #@59
    .line 1591
    invoke-direct {p0, v2, v5}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->clickPressedItem(Landroid/view/View;I)V

    #@5c
    goto :goto_0

    #@5d
    .line 1608
    .end local v1    # "activeIndex":I
    .end local v2    # "child":Landroid/view/View;
    .end local v5    # "position":I
    .end local v6    # "x":I
    .end local v7    # "y":I
    :cond_7
    iget-object v8, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mScrollHelper:Landroid/support/v4/widget/ListViewAutoScrollHelper;

    #@5f
    if-eqz v8, :cond_4

    #@61
    .line 1609
    iget-object v8, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mScrollHelper:Landroid/support/v4/widget/ListViewAutoScrollHelper;

    #@63
    const/4 v9, 0x0

    #@64
    invoke-virtual {v8, v9}, Landroid/support/v4/widget/ListViewAutoScrollHelper;->setEnabled(Z)Landroid/support/v4/widget/AutoScrollHelper;

    #@67
    goto :goto_1

    #@68
    .line 1564
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_1
        :pswitch_2
        :pswitch_0
    .end packed-switch
.end method

.method protected touchModeDrawsInPressedStateCompat()Z
    .locals 1

    #@0
    .prologue
    .line 1660
    iget-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mDrawsInPressedState:Z

    #@2
    if-nez v0, :cond_0

    #@4
    invoke-super {p0}, Landroid/support/v7/internal/widget/ListViewCompat;->touchModeDrawsInPressedStateCompat()Z

    #@7
    move-result v0

    #@8
    if-eqz v0, :cond_1

    #@a
    :cond_0
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_1
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method
