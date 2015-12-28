.class public Landroid/support/v4/widget/ListViewAutoScrollHelper;
.super Landroid/support/v4/widget/AutoScrollHelper;
.source "ListViewAutoScrollHelper.java"


# instance fields
.field private final mTarget:Landroid/widget/ListView;


# direct methods
.method public constructor <init>(Landroid/widget/ListView;)V
    .locals 0
    .param p1, "target"    # Landroid/widget/ListView;

    #@0
    .prologue
    .line 30
    invoke-direct {p0, p1}, Landroid/support/v4/widget/AutoScrollHelper;-><init>(Landroid/view/View;)V

    #@3
    .line 32
    iput-object p1, p0, Landroid/support/v4/widget/ListViewAutoScrollHelper;->mTarget:Landroid/widget/ListView;

    #@5
    .line 33
    return-void
.end method


# virtual methods
.method public canTargetScrollHorizontally(I)Z
    .locals 1
    .param p1, "direction"    # I

    #@0
    .prologue
    .line 55
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public canTargetScrollVertically(I)Z
    .locals 10
    .param p1, "direction"    # I

    #@0
    .prologue
    const/4 v7, 0x0

    #@1
    .line 60
    iget-object v6, p0, Landroid/support/v4/widget/ListViewAutoScrollHelper;->mTarget:Landroid/widget/ListView;

    #@3
    .line 61
    .local v6, "target":Landroid/widget/ListView;
    invoke-virtual {v6}, Landroid/widget/ListView;->getCount()I

    #@6
    move-result v3

    #@7
    .line 62
    .local v3, "itemCount":I
    if-nez v3, :cond_1

    #@9
    .line 92
    :cond_0
    :goto_0
    return v7

    #@a
    .line 66
    :cond_1
    invoke-virtual {v6}, Landroid/widget/ListView;->getChildCount()I

    #@d
    move-result v0

    #@e
    .line 67
    .local v0, "childCount":I
    invoke-virtual {v6}, Landroid/widget/ListView;->getFirstVisiblePosition()I

    #@11
    move-result v1

    #@12
    .line 68
    .local v1, "firstPosition":I
    add-int v4, v1, v0

    #@14
    .line 70
    .local v4, "lastPosition":I
    if-lez p1, :cond_3

    #@16
    .line 72
    if-lt v4, v3, :cond_2

    #@18
    .line 73
    add-int/lit8 v8, v0, -0x1

    #@1a
    invoke-virtual {v6, v8}, Landroid/widget/ListView;->getChildAt(I)Landroid/view/View;

    #@1d
    move-result-object v5

    #@1e
    .line 74
    .local v5, "lastView":Landroid/view/View;
    invoke-virtual {v5}, Landroid/view/View;->getBottom()I

    #@21
    move-result v8

    #@22
    invoke-virtual {v6}, Landroid/widget/ListView;->getHeight()I

    #@25
    move-result v9

    #@26
    if-le v8, v9, :cond_0

    #@28
    .line 92
    .end local v5    # "lastView":Landroid/view/View;
    :cond_2
    const/4 v7, 0x1

    #@29
    goto :goto_0

    #@2a
    .line 78
    :cond_3
    if-gez p1, :cond_0

    #@2c
    .line 80
    if-gtz v1, :cond_2

    #@2e
    .line 81
    invoke-virtual {v6, v7}, Landroid/widget/ListView;->getChildAt(I)Landroid/view/View;

    #@31
    move-result-object v2

    #@32
    .line 82
    .local v2, "firstView":Landroid/view/View;
    invoke-virtual {v2}, Landroid/view/View;->getTop()I

    #@35
    move-result v8

    #@36
    if-ltz v8, :cond_2

    #@38
    goto :goto_0
.end method

.method public scrollTargetBy(II)V
    .locals 5
    .param p1, "deltaX"    # I
    .param p2, "deltaY"    # I

    #@0
    .prologue
    .line 37
    iget-object v3, p0, Landroid/support/v4/widget/ListViewAutoScrollHelper;->mTarget:Landroid/widget/ListView;

    #@2
    .line 38
    .local v3, "target":Landroid/widget/ListView;
    invoke-virtual {v3}, Landroid/widget/ListView;->getFirstVisiblePosition()I

    #@5
    move-result v0

    #@6
    .line 39
    .local v0, "firstPosition":I
    const/4 v4, -0x1

    #@7
    if-ne v0, v4, :cond_1

    #@9
    .line 50
    :cond_0
    :goto_0
    return-void

    #@a
    .line 43
    :cond_1
    const/4 v4, 0x0

    #@b
    invoke-virtual {v3, v4}, Landroid/widget/ListView;->getChildAt(I)Landroid/view/View;

    #@e
    move-result-object v1

    #@f
    .line 44
    .local v1, "firstView":Landroid/view/View;
    if-eqz v1, :cond_0

    #@11
    .line 48
    invoke-virtual {v1}, Landroid/view/View;->getTop()I

    #@14
    move-result v4

    #@15
    sub-int v2, v4, p2

    #@17
    .line 49
    .local v2, "newTop":I
    invoke-virtual {v3, v0, v2}, Landroid/widget/ListView;->setSelectionFromTop(II)V

    #@1a
    goto :goto_0
.end method
