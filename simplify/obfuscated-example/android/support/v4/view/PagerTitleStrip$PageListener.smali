.class Landroid/support/v4/view/PagerTitleStrip$PageListener;
.super Landroid/database/DataSetObserver;
.source "PagerTitleStrip.java"

# interfaces
.implements Landroid/support/v4/view/ViewPager$OnAdapterChangeListener;
.implements Landroid/support/v4/view/ViewPager$OnPageChangeListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/PagerTitleStrip;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "PageListener"
.end annotation


# instance fields
.field private mScrollState:I

.field final synthetic this$0:Landroid/support/v4/view/PagerTitleStrip;


# direct methods
.method private constructor <init>(Landroid/support/v4/view/PagerTitleStrip;)V
    .locals 0

    #@0
    .prologue
    .line 470
    iput-object p1, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@2
    invoke-direct {p0}, Landroid/database/DataSetObserver;-><init>()V

    #@5
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v4/view/PagerTitleStrip;Landroid/support/v4/view/PagerTitleStrip$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v4/view/PagerTitleStrip;
    .param p2, "x1"    # Landroid/support/v4/view/PagerTitleStrip$1;

    #@0
    .prologue
    .line 470
    invoke-direct {p0, p1}, Landroid/support/v4/view/PagerTitleStrip$PageListener;-><init>(Landroid/support/v4/view/PagerTitleStrip;)V

    #@3
    return-void
.end method


# virtual methods
.method public onAdapterChanged(Landroid/support/v4/view/PagerAdapter;Landroid/support/v4/view/PagerAdapter;)V
    .locals 1
    .param p1, "oldAdapter"    # Landroid/support/v4/view/PagerAdapter;
    .param p2, "newAdapter"    # Landroid/support/v4/view/PagerAdapter;

    #@0
    .prologue
    .line 501
    iget-object v0, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/support/v4/view/PagerTitleStrip;->updateAdapter(Landroid/support/v4/view/PagerAdapter;Landroid/support/v4/view/PagerAdapter;)V

    #@5
    .line 502
    return-void
.end method

.method public onChanged()V
    .locals 4

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 506
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@3
    iget-object v2, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@5
    iget-object v2, v2, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@7
    invoke-virtual {v2}, Landroid/support/v4/view/ViewPager;->getCurrentItem()I

    #@a
    move-result v2

    #@b
    iget-object v3, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@d
    iget-object v3, v3, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@f
    invoke-virtual {v3}, Landroid/support/v4/view/ViewPager;->getAdapter()Landroid/support/v4/view/PagerAdapter;

    #@12
    move-result-object v3

    #@13
    invoke-virtual {v1, v2, v3}, Landroid/support/v4/view/PagerTitleStrip;->updateText(ILandroid/support/v4/view/PagerAdapter;)V

    #@16
    .line 508
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@18
    # getter for: Landroid/support/v4/view/PagerTitleStrip;->mLastKnownPositionOffset:F
    invoke-static {v1}, Landroid/support/v4/view/PagerTitleStrip;->access$100(Landroid/support/v4/view/PagerTitleStrip;)F

    #@1b
    move-result v1

    #@1c
    cmpl-float v1, v1, v0

    #@1e
    if-ltz v1, :cond_0

    #@20
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@22
    # getter for: Landroid/support/v4/view/PagerTitleStrip;->mLastKnownPositionOffset:F
    invoke-static {v1}, Landroid/support/v4/view/PagerTitleStrip;->access$100(Landroid/support/v4/view/PagerTitleStrip;)F

    #@25
    move-result v0

    #@26
    .line 509
    .local v0, "offset":F
    :cond_0
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@28
    iget-object v2, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@2a
    iget-object v2, v2, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@2c
    invoke-virtual {v2}, Landroid/support/v4/view/ViewPager;->getCurrentItem()I

    #@2f
    move-result v2

    #@30
    const/4 v3, 0x1

    #@31
    invoke-virtual {v1, v2, v0, v3}, Landroid/support/v4/view/PagerTitleStrip;->updateTextPositions(IFZ)V

    #@34
    .line 510
    return-void
.end method

.method public onPageScrollStateChanged(I)V
    .locals 0
    .param p1, "state"    # I

    #@0
    .prologue
    .line 496
    iput p1, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->mScrollState:I

    #@2
    .line 497
    return-void
.end method

.method public onPageScrolled(IFI)V
    .locals 2
    .param p1, "position"    # I
    .param p2, "positionOffset"    # F
    .param p3, "positionOffsetPixels"    # I

    #@0
    .prologue
    .line 476
    const/high16 v0, 0x3f000000    # 0.5f

    #@2
    cmpl-float v0, p2, v0

    #@4
    if-lez v0, :cond_0

    #@6
    .line 478
    add-int/lit8 p1, p1, 0x1

    #@8
    .line 480
    :cond_0
    iget-object v0, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@a
    const/4 v1, 0x0

    #@b
    invoke-virtual {v0, p1, p2, v1}, Landroid/support/v4/view/PagerTitleStrip;->updateTextPositions(IFZ)V

    #@e
    .line 481
    return-void
.end method

.method public onPageSelected(I)V
    .locals 4
    .param p1, "position"    # I

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 485
    iget v1, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->mScrollState:I

    #@3
    if-nez v1, :cond_1

    #@5
    .line 487
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@7
    iget-object v2, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@9
    iget-object v2, v2, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@b
    invoke-virtual {v2}, Landroid/support/v4/view/ViewPager;->getCurrentItem()I

    #@e
    move-result v2

    #@f
    iget-object v3, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@11
    iget-object v3, v3, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@13
    invoke-virtual {v3}, Landroid/support/v4/view/ViewPager;->getAdapter()Landroid/support/v4/view/PagerAdapter;

    #@16
    move-result-object v3

    #@17
    invoke-virtual {v1, v2, v3}, Landroid/support/v4/view/PagerTitleStrip;->updateText(ILandroid/support/v4/view/PagerAdapter;)V

    #@1a
    .line 489
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@1c
    # getter for: Landroid/support/v4/view/PagerTitleStrip;->mLastKnownPositionOffset:F
    invoke-static {v1}, Landroid/support/v4/view/PagerTitleStrip;->access$100(Landroid/support/v4/view/PagerTitleStrip;)F

    #@1f
    move-result v1

    #@20
    cmpl-float v1, v1, v0

    #@22
    if-ltz v1, :cond_0

    #@24
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@26
    # getter for: Landroid/support/v4/view/PagerTitleStrip;->mLastKnownPositionOffset:F
    invoke-static {v1}, Landroid/support/v4/view/PagerTitleStrip;->access$100(Landroid/support/v4/view/PagerTitleStrip;)F

    #@29
    move-result v0

    #@2a
    .line 490
    .local v0, "offset":F
    :cond_0
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@2c
    iget-object v2, p0, Landroid/support/v4/view/PagerTitleStrip$PageListener;->this$0:Landroid/support/v4/view/PagerTitleStrip;

    #@2e
    iget-object v2, v2, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@30
    invoke-virtual {v2}, Landroid/support/v4/view/ViewPager;->getCurrentItem()I

    #@33
    move-result v2

    #@34
    const/4 v3, 0x1

    #@35
    invoke-virtual {v1, v2, v0, v3}, Landroid/support/v4/view/PagerTitleStrip;->updateTextPositions(IFZ)V

    #@38
    .line 492
    .end local v0    # "offset":F
    :cond_1
    return-void
.end method
