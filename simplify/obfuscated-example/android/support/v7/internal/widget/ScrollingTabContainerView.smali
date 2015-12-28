.class public Landroid/support/v7/internal/widget/ScrollingTabContainerView;
.super Landroid/widget/HorizontalScrollView;
.source "ScrollingTabContainerView.java"

# interfaces
.implements Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;,
        Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabClickListener;,
        Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;,
        Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;
    }
.end annotation


# static fields
.field private static final FADE_DURATION:I = 0xc8

.field private static final TAG:Ljava/lang/String; = "ScrollingTabContainerView"

.field private static final sAlphaInterpolator:Landroid/view/animation/Interpolator;


# instance fields
.field private mAllowCollapse:Z

.field private mContentHeight:I

.field mMaxTabWidth:I

.field private mSelectedTabIndex:I

.field mStackedTabMaxWidth:I

.field private mTabClickListener:Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabClickListener;

.field private mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

.field mTabSelector:Ljava/lang/Runnable;

.field private mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

.field protected final mVisAnimListener:Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;

.field protected mVisibilityAnim:Landroid/support/v4/view/ViewPropertyAnimatorCompat;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 74
    new-instance v0, Landroid/view/animation/DecelerateInterpolator;

    #@2
    invoke-direct {v0}, Landroid/view/animation/DecelerateInterpolator;-><init>()V

    #@5
    sput-object v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->sAlphaInterpolator:Landroid/view/animation/Interpolator;

    #@7
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 5
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 79
    invoke-direct {p0, p1}, Landroid/widget/HorizontalScrollView;-><init>(Landroid/content/Context;)V

    #@3
    .line 72
    new-instance v1, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;

    #@5
    invoke-direct {v1, p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;-><init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V

    #@8
    iput-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mVisAnimListener:Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;

    #@a
    .line 81
    const/4 v1, 0x0

    #@b
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setHorizontalScrollBarEnabled(Z)V

    #@e
    .line 83
    invoke-static {p1}, Landroid/support/v7/internal/view/ActionBarPolicy;->get(Landroid/content/Context;)Landroid/support/v7/internal/view/ActionBarPolicy;

    #@11
    move-result-object v0

    #@12
    .line 84
    .local v0, "abp":Landroid/support/v7/internal/view/ActionBarPolicy;
    invoke-virtual {v0}, Landroid/support/v7/internal/view/ActionBarPolicy;->getTabContainerHeight()I

    #@15
    move-result v1

    #@16
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setContentHeight(I)V

    #@19
    .line 85
    invoke-virtual {v0}, Landroid/support/v7/internal/view/ActionBarPolicy;->getStackedTabMaxWidth()I

    #@1c
    move-result v1

    #@1d
    iput v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mStackedTabMaxWidth:I

    #@1f
    .line 87
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->createTabLayout()Landroid/support/v7/widget/LinearLayoutCompat;

    #@22
    move-result-object v1

    #@23
    iput-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@25
    .line 88
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@27
    new-instance v2, Landroid/view/ViewGroup$LayoutParams;

    #@29
    const/4 v3, -0x2

    #@2a
    const/4 v4, -0x1

    #@2b
    invoke-direct {v2, v3, v4}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    #@2e
    invoke-virtual {p0, v1, v2}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@31
    .line 90
    return-void
.end method

.method static synthetic access$200(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)Landroid/support/v7/widget/LinearLayoutCompat;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@0
    .prologue
    .line 55
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@2
    return-object v0
.end method

.method static synthetic access$300(Landroid/support/v7/internal/widget/ScrollingTabContainerView;Landroid/support/v7/app/ActionBar$Tab;Z)Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ScrollingTabContainerView;
    .param p1, "x1"    # Landroid/support/v7/app/ActionBar$Tab;
    .param p2, "x2"    # Z

    #@0
    .prologue
    .line 55
    invoke-direct {p0, p1, p2}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->createTabView(Landroid/support/v7/app/ActionBar$Tab;Z)Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method private createSpinner()Landroid/support/v7/internal/widget/SpinnerCompat;
    .locals 4

    #@0
    .prologue
    .line 211
    new-instance v0, Landroid/support/v7/internal/widget/SpinnerCompat;

    #@2
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    const/4 v2, 0x0

    #@7
    sget v3, Landroid/support/v7/appcompat/R$attr;->actionDropDownStyle:I

    #@9
    invoke-direct {v0, v1, v2, v3}, Landroid/support/v7/internal/widget/SpinnerCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@c
    .line 213
    .local v0, "spinner":Landroid/support/v7/internal/widget/SpinnerCompat;
    new-instance v1, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@e
    const/4 v2, -0x2

    #@f
    const/4 v3, -0x1

    #@10
    invoke-direct {v1, v2, v3}, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;-><init>(II)V

    #@13
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/SpinnerCompat;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@16
    .line 215
    invoke-virtual {v0, p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->setOnItemClickListenerInt(Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;)V

    #@19
    .line 216
    return-object v0
.end method

.method private createTabLayout()Landroid/support/v7/widget/LinearLayoutCompat;
    .locals 4

    #@0
    .prologue
    .line 201
    new-instance v0, Landroid/support/v7/widget/LinearLayoutCompat;

    #@2
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    const/4 v2, 0x0

    #@7
    sget v3, Landroid/support/v7/appcompat/R$attr;->actionBarTabBarStyle:I

    #@9
    invoke-direct {v0, v1, v2, v3}, Landroid/support/v7/widget/LinearLayoutCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@c
    .line 203
    .local v0, "tabLayout":Landroid/support/v7/widget/LinearLayoutCompat;
    const/4 v1, 0x1

    #@d
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/LinearLayoutCompat;->setMeasureWithLargestChildEnabled(Z)V

    #@10
    .line 204
    const/16 v1, 0x11

    #@12
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/LinearLayoutCompat;->setGravity(I)V

    #@15
    .line 205
    new-instance v1, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@17
    const/4 v2, -0x2

    #@18
    const/4 v3, -0x1

    #@19
    invoke-direct {v1, v2, v3}, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;-><init>(II)V

    #@1c
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/LinearLayoutCompat;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@1f
    .line 207
    return-object v0
.end method

.method private createTabView(Landroid/support/v7/app/ActionBar$Tab;Z)Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;
    .locals 4
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;
    .param p2, "forAdapter"    # Z

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 289
    new-instance v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;

    #@3
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->getContext()Landroid/content/Context;

    #@6
    move-result-object v1

    #@7
    invoke-direct {v0, p0, v1, p1, p2}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;-><init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;Landroid/content/Context;Landroid/support/v7/app/ActionBar$Tab;Z)V

    #@a
    .line 290
    .local v0, "tabView":Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;
    if-eqz p2, :cond_0

    #@c
    .line 291
    invoke-virtual {v0, v2}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@f
    .line 292
    new-instance v1, Landroid/widget/AbsListView$LayoutParams;

    #@11
    const/4 v2, -0x1

    #@12
    iget v3, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mContentHeight:I

    #@14
    invoke-direct {v1, v2, v3}, Landroid/widget/AbsListView$LayoutParams;-><init>(II)V

    #@17
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@1a
    .line 302
    :goto_0
    return-object v0

    #@1b
    .line 295
    :cond_0
    const/4 v1, 0x1

    #@1c
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->setFocusable(Z)V

    #@1f
    .line 297
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabClickListener:Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabClickListener;

    #@21
    if-nez v1, :cond_1

    #@23
    .line 298
    new-instance v1, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabClickListener;

    #@25
    invoke-direct {v1, p0, v2}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabClickListener;-><init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;)V

    #@28
    iput-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabClickListener:Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabClickListener;

    #@2a
    .line 300
    :cond_1
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabClickListener:Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabClickListener;

    #@2c
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    #@2f
    goto :goto_0
.end method

.method private isCollapsed()Z
    .locals 1

    #@0
    .prologue
    .line 143
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getParent()Landroid/view/ViewParent;

    #@9
    move-result-object v0

    #@a
    if-ne v0, p0, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method private performCollapse()V
    .locals 5

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    .line 151
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->isCollapsed()Z

    #@4
    move-result v0

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 167
    :goto_0
    return-void

    #@8
    .line 153
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@a
    if-nez v0, :cond_1

    #@c
    .line 154
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->createSpinner()Landroid/support/v7/internal/widget/SpinnerCompat;

    #@f
    move-result-object v0

    #@10
    iput-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@12
    .line 156
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@14
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->removeView(Landroid/view/View;)V

    #@17
    .line 157
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@19
    new-instance v1, Landroid/view/ViewGroup$LayoutParams;

    #@1b
    const/4 v2, -0x2

    #@1c
    const/4 v3, -0x1

    #@1d
    invoke-direct {v1, v2, v3}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    #@20
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@23
    .line 159
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@25
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getAdapter()Landroid/widget/SpinnerAdapter;

    #@28
    move-result-object v0

    #@29
    if-nez v0, :cond_2

    #@2b
    .line 160
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@2d
    new-instance v1, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;

    #@2f
    invoke-direct {v1, p0, v4}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;-><init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;)V

    #@32
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/SpinnerCompat;->setAdapter(Landroid/widget/SpinnerAdapter;)V

    #@35
    .line 162
    :cond_2
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSelector:Ljava/lang/Runnable;

    #@37
    if-eqz v0, :cond_3

    #@39
    .line 163
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSelector:Ljava/lang/Runnable;

    #@3b
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@3e
    .line 164
    iput-object v4, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSelector:Ljava/lang/Runnable;

    #@40
    .line 166
    :cond_3
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@42
    iget v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mSelectedTabIndex:I

    #@44
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/SpinnerCompat;->setSelection(I)V

    #@47
    goto :goto_0
.end method

.method private performExpand()Z
    .locals 5

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    .line 170
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->isCollapsed()Z

    #@4
    move-result v0

    #@5
    if-nez v0, :cond_0

    #@7
    .line 176
    :goto_0
    return v4

    #@8
    .line 172
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@a
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->removeView(Landroid/view/View;)V

    #@d
    .line 173
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@f
    new-instance v1, Landroid/view/ViewGroup$LayoutParams;

    #@11
    const/4 v2, -0x2

    #@12
    const/4 v3, -0x1

    #@13
    invoke-direct {v1, v2, v3}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    #@16
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@19
    .line 175
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@1b
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getSelectedItemPosition()I

    #@1e
    move-result v0

    #@1f
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setTabSelected(I)V

    #@22
    goto :goto_0
.end method


# virtual methods
.method public addTab(Landroid/support/v7/app/ActionBar$Tab;IZ)V
    .locals 6
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;
    .param p2, "position"    # I
    .param p3, "setSelected"    # Z

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    .line 321
    invoke-direct {p0, p1, v5}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->createTabView(Landroid/support/v7/app/ActionBar$Tab;Z)Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;

    #@4
    move-result-object v0

    #@5
    .line 322
    .local v0, "tabView":Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@7
    new-instance v2, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@9
    const/4 v3, -0x1

    #@a
    const/high16 v4, 0x3f800000    # 1.0f

    #@c
    invoke-direct {v2, v5, v3, v4}, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;-><init>(IIF)V

    #@f
    invoke-virtual {v1, v0, p2, v2}, Landroid/support/v7/widget/LinearLayoutCompat;->addView(Landroid/view/View;ILandroid/view/ViewGroup$LayoutParams;)V

    #@12
    .line 324
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@14
    if-eqz v1, :cond_0

    #@16
    .line 325
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@18
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/SpinnerCompat;->getAdapter()Landroid/widget/SpinnerAdapter;

    #@1b
    move-result-object v1

    #@1c
    check-cast v1, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;

    #@1e
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;->notifyDataSetChanged()V

    #@21
    .line 327
    :cond_0
    if-eqz p3, :cond_1

    #@23
    .line 328
    const/4 v1, 0x1

    #@24
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->setSelected(Z)V

    #@27
    .line 330
    :cond_1
    iget-boolean v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mAllowCollapse:Z

    #@29
    if-eqz v1, :cond_2

    #@2b
    .line 331
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->requestLayout()V

    #@2e
    .line 333
    :cond_2
    return-void
.end method

.method public addTab(Landroid/support/v7/app/ActionBar$Tab;Z)V
    .locals 6
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;
    .param p2, "setSelected"    # Z

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    .line 306
    invoke-direct {p0, p1, v5}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->createTabView(Landroid/support/v7/app/ActionBar$Tab;Z)Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;

    #@4
    move-result-object v0

    #@5
    .line 307
    .local v0, "tabView":Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@7
    new-instance v2, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@9
    const/4 v3, -0x1

    #@a
    const/high16 v4, 0x3f800000    # 1.0f

    #@c
    invoke-direct {v2, v5, v3, v4}, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;-><init>(IIF)V

    #@f
    invoke-virtual {v1, v0, v2}, Landroid/support/v7/widget/LinearLayoutCompat;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@12
    .line 309
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@14
    if-eqz v1, :cond_0

    #@16
    .line 310
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@18
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/SpinnerCompat;->getAdapter()Landroid/widget/SpinnerAdapter;

    #@1b
    move-result-object v1

    #@1c
    check-cast v1, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;

    #@1e
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;->notifyDataSetChanged()V

    #@21
    .line 312
    :cond_0
    if-eqz p2, :cond_1

    #@23
    .line 313
    const/4 v1, 0x1

    #@24
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->setSelected(Z)V

    #@27
    .line 315
    :cond_1
    iget-boolean v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mAllowCollapse:Z

    #@29
    if-eqz v1, :cond_2

    #@2b
    .line 316
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->requestLayout()V

    #@2e
    .line 318
    :cond_2
    return-void
.end method

.method public animateToTab(I)V
    .locals 2
    .param p1, "position"    # I

    #@0
    .prologue
    .line 257
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@2
    invoke-virtual {v1, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildAt(I)Landroid/view/View;

    #@5
    move-result-object v0

    #@6
    .line 258
    .local v0, "tabView":Landroid/view/View;
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSelector:Ljava/lang/Runnable;

    #@8
    if-eqz v1, :cond_0

    #@a
    .line 259
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSelector:Ljava/lang/Runnable;

    #@c
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@f
    .line 261
    :cond_0
    new-instance v1, Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;

    #@11
    invoke-direct {v1, p0, v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;-><init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;Landroid/view/View;)V

    #@14
    iput-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSelector:Ljava/lang/Runnable;

    #@16
    .line 268
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSelector:Ljava/lang/Runnable;

    #@18
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->post(Ljava/lang/Runnable;)Z

    #@1b
    .line 269
    return-void
.end method

.method public animateToVisibility(I)V
    .locals 6
    .param p1, "visibility"    # I

    #@0
    .prologue
    const-wide/16 v4, 0xc8

    #@2
    const/4 v2, 0x0

    #@3
    .line 232
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mVisibilityAnim:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@5
    if-eqz v1, :cond_0

    #@7
    .line 233
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mVisibilityAnim:Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@9
    invoke-virtual {v1}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->cancel()V

    #@c
    .line 235
    :cond_0
    if-nez p1, :cond_2

    #@e
    .line 236
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->getVisibility()I

    #@11
    move-result v1

    #@12
    if-eqz v1, :cond_1

    #@14
    .line 237
    invoke-static {p0, v2}, Landroid/support/v4/view/ViewCompat;->setAlpha(Landroid/view/View;F)V

    #@17
    .line 240
    :cond_1
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@1a
    move-result-object v1

    #@1b
    const/high16 v2, 0x3f800000    # 1.0f

    #@1d
    invoke-virtual {v1, v2}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->alpha(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@20
    move-result-object v0

    #@21
    .line 241
    .local v0, "anim":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    invoke-virtual {v0, v4, v5}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setDuration(J)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@24
    .line 243
    sget-object v1, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->sAlphaInterpolator:Landroid/view/animation/Interpolator;

    #@26
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setInterpolator(Landroid/view/animation/Interpolator;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@29
    .line 244
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mVisAnimListener:Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;

    #@2b
    invoke-virtual {v1, v0, p1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;->withFinalVisibility(Landroid/support/v4/view/ViewPropertyAnimatorCompat;I)Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;

    #@2e
    move-result-object v1

    #@2f
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@32
    .line 245
    invoke-virtual {v0}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->start()V

    #@35
    .line 254
    :goto_0
    return-void

    #@36
    .line 247
    .end local v0    # "anim":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    :cond_2
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@39
    move-result-object v1

    #@3a
    invoke-virtual {v1, v2}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->alpha(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@3d
    move-result-object v0

    #@3e
    .line 248
    .restart local v0    # "anim":Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    invoke-virtual {v0, v4, v5}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setDuration(J)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@41
    .line 250
    sget-object v1, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->sAlphaInterpolator:Landroid/view/animation/Interpolator;

    #@43
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setInterpolator(Landroid/view/animation/Interpolator;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@46
    .line 251
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mVisAnimListener:Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;

    #@48
    invoke-virtual {v1, v0, p1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;->withFinalVisibility(Landroid/support/v4/view/ViewPropertyAnimatorCompat;I)Landroid/support/v7/internal/widget/ScrollingTabContainerView$VisibilityAnimListener;

    #@4b
    move-result-object v1

    #@4c
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@4f
    .line 252
    invoke-virtual {v0}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->start()V

    #@52
    goto :goto_0
.end method

.method public onAttachedToWindow()V
    .locals 1

    #@0
    .prologue
    .line 273
    invoke-super {p0}, Landroid/widget/HorizontalScrollView;->onAttachedToWindow()V

    #@3
    .line 274
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSelector:Ljava/lang/Runnable;

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 276
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSelector:Ljava/lang/Runnable;

    #@9
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->post(Ljava/lang/Runnable;)Z

    #@c
    .line 278
    :cond_0
    return-void
.end method

.method protected onConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 3
    .param p1, "newConfig"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    .line 220
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v2, 0x8

    #@4
    if-lt v1, v2, :cond_0

    #@6
    .line 221
    invoke-super {p0, p1}, Landroid/widget/HorizontalScrollView;->onConfigurationChanged(Landroid/content/res/Configuration;)V

    #@9
    .line 224
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->getContext()Landroid/content/Context;

    #@c
    move-result-object v1

    #@d
    invoke-static {v1}, Landroid/support/v7/internal/view/ActionBarPolicy;->get(Landroid/content/Context;)Landroid/support/v7/internal/view/ActionBarPolicy;

    #@10
    move-result-object v0

    #@11
    .line 227
    .local v0, "abp":Landroid/support/v7/internal/view/ActionBarPolicy;
    invoke-virtual {v0}, Landroid/support/v7/internal/view/ActionBarPolicy;->getTabContainerHeight()I

    #@14
    move-result v1

    #@15
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setContentHeight(I)V

    #@18
    .line 228
    invoke-virtual {v0}, Landroid/support/v7/internal/view/ActionBarPolicy;->getStackedTabMaxWidth()I

    #@1b
    move-result v1

    #@1c
    iput v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mStackedTabMaxWidth:I

    #@1e
    .line 229
    return-void
.end method

.method public onDetachedFromWindow()V
    .locals 1

    #@0
    .prologue
    .line 282
    invoke-super {p0}, Landroid/widget/HorizontalScrollView;->onDetachedFromWindow()V

    #@3
    .line 283
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSelector:Ljava/lang/Runnable;

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 284
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSelector:Ljava/lang/Runnable;

    #@9
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@c
    .line 286
    :cond_0
    return-void
.end method

.method public onItemClick(Landroid/support/v7/internal/widget/AdapterViewCompat;Landroid/view/View;IJ)V
    .locals 2
    .param p2, "view"    # Landroid/view/View;
    .param p3, "position"    # I
    .param p4, "id"    # J
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v7/internal/widget/AdapterViewCompat",
            "<*>;",
            "Landroid/view/View;",
            "IJ)V"
        }
    .end annotation

    #@0
    .prologue
    .line 367
    .local p1, "parent":Landroid/support/v7/internal/widget/AdapterViewCompat;, "Landroid/support/v7/internal/widget/AdapterViewCompat<*>;"
    move-object v0, p2

    #@1
    check-cast v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;

    #@3
    .line 368
    .local v0, "tabView":Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->getTab()Landroid/support/v7/app/ActionBar$Tab;

    #@6
    move-result-object v1

    #@7
    invoke-virtual {v1}, Landroid/support/v7/app/ActionBar$Tab;->select()V

    #@a
    .line 369
    return-void
.end method

.method public onMeasure(II)V
    .locals 11
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    const/high16 v10, 0x40000000    # 2.0f

    #@2
    const/4 v6, 0x1

    #@3
    const/4 v7, 0x0

    #@4
    .line 94
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@7
    move-result v5

    #@8
    .line 95
    .local v5, "widthMode":I
    if-ne v5, v10, :cond_2

    #@a
    move v2, v6

    #@b
    .line 96
    .local v2, "lockedExpanded":Z
    :goto_0
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setFillViewport(Z)V

    #@e
    .line 98
    iget-object v8, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@10
    invoke-virtual {v8}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildCount()I

    #@13
    move-result v1

    #@14
    .line 99
    .local v1, "childCount":I
    if-le v1, v6, :cond_4

    #@16
    if-eq v5, v10, :cond_0

    #@18
    const/high16 v8, -0x80000000

    #@1a
    if-ne v5, v8, :cond_4

    #@1c
    .line 101
    :cond_0
    const/4 v8, 0x2

    #@1d
    if-le v1, v8, :cond_3

    #@1f
    .line 102
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@22
    move-result v8

    #@23
    int-to-float v8, v8

    #@24
    const v9, 0x3ecccccd    # 0.4f

    #@27
    mul-float/2addr v8, v9

    #@28
    float-to-int v8, v8

    #@29
    iput v8, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mMaxTabWidth:I

    #@2b
    .line 106
    :goto_1
    iget v8, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mMaxTabWidth:I

    #@2d
    iget v9, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mStackedTabMaxWidth:I

    #@2f
    invoke-static {v8, v9}, Ljava/lang/Math;->min(II)I

    #@32
    move-result v8

    #@33
    iput v8, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mMaxTabWidth:I

    #@35
    .line 111
    :goto_2
    iget v8, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mContentHeight:I

    #@37
    invoke-static {v8, v10}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@3a
    move-result p2

    #@3b
    .line 113
    if-nez v2, :cond_5

    #@3d
    iget-boolean v8, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mAllowCollapse:Z

    #@3f
    if-eqz v8, :cond_5

    #@41
    move v0, v6

    #@42
    .line 115
    .local v0, "canCollapse":Z
    :goto_3
    if-eqz v0, :cond_7

    #@44
    .line 117
    iget-object v6, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@46
    invoke-virtual {v6, v7, p2}, Landroid/support/v7/widget/LinearLayoutCompat;->measure(II)V

    #@49
    .line 118
    iget-object v6, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@4b
    invoke-virtual {v6}, Landroid/support/v7/widget/LinearLayoutCompat;->getMeasuredWidth()I

    #@4e
    move-result v6

    #@4f
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@52
    move-result v7

    #@53
    if-le v6, v7, :cond_6

    #@55
    .line 119
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->performCollapse()V

    #@58
    .line 127
    :goto_4
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->getMeasuredWidth()I

    #@5b
    move-result v4

    #@5c
    .line 128
    .local v4, "oldWidth":I
    invoke-super {p0, p1, p2}, Landroid/widget/HorizontalScrollView;->onMeasure(II)V

    #@5f
    .line 129
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->getMeasuredWidth()I

    #@62
    move-result v3

    #@63
    .line 131
    .local v3, "newWidth":I
    if-eqz v2, :cond_1

    #@65
    if-eq v4, v3, :cond_1

    #@67
    .line 133
    iget v6, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mSelectedTabIndex:I

    #@69
    invoke-virtual {p0, v6}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setTabSelected(I)V

    #@6c
    .line 135
    :cond_1
    return-void

    #@6d
    .end local v0    # "canCollapse":Z
    .end local v1    # "childCount":I
    .end local v2    # "lockedExpanded":Z
    .end local v3    # "newWidth":I
    .end local v4    # "oldWidth":I
    :cond_2
    move v2, v7

    #@6e
    .line 95
    goto :goto_0

    #@6f
    .line 104
    .restart local v1    # "childCount":I
    .restart local v2    # "lockedExpanded":Z
    :cond_3
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@72
    move-result v8

    #@73
    div-int/lit8 v8, v8, 0x2

    #@75
    iput v8, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mMaxTabWidth:I

    #@77
    goto :goto_1

    #@78
    .line 108
    :cond_4
    const/4 v8, -0x1

    #@79
    iput v8, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mMaxTabWidth:I

    #@7b
    goto :goto_2

    #@7c
    :cond_5
    move v0, v7

    #@7d
    .line 113
    goto :goto_3

    #@7e
    .line 121
    .restart local v0    # "canCollapse":Z
    :cond_6
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->performExpand()Z

    #@81
    goto :goto_4

    #@82
    .line 124
    :cond_7
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->performExpand()Z

    #@85
    goto :goto_4
.end method

.method public removeAllTabs()V
    .locals 1

    #@0
    .prologue
    .line 356
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/LinearLayoutCompat;->removeAllViews()V

    #@5
    .line 357
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@7
    if-eqz v0, :cond_0

    #@9
    .line 358
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@b
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getAdapter()Landroid/widget/SpinnerAdapter;

    #@e
    move-result-object v0

    #@f
    check-cast v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;

    #@11
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;->notifyDataSetChanged()V

    #@14
    .line 360
    :cond_0
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mAllowCollapse:Z

    #@16
    if-eqz v0, :cond_1

    #@18
    .line 361
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->requestLayout()V

    #@1b
    .line 363
    :cond_1
    return-void
.end method

.method public removeTabAt(I)V
    .locals 1
    .param p1, "position"    # I

    #@0
    .prologue
    .line 346
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->removeViewAt(I)V

    #@5
    .line 347
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@7
    if-eqz v0, :cond_0

    #@9
    .line 348
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@b
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getAdapter()Landroid/widget/SpinnerAdapter;

    #@e
    move-result-object v0

    #@f
    check-cast v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;

    #@11
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;->notifyDataSetChanged()V

    #@14
    .line 350
    :cond_0
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mAllowCollapse:Z

    #@16
    if-eqz v0, :cond_1

    #@18
    .line 351
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->requestLayout()V

    #@1b
    .line 353
    :cond_1
    return-void
.end method

.method public setAllowCollapse(Z)V
    .locals 0
    .param p1, "allowCollapse"    # Z

    #@0
    .prologue
    .line 147
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mAllowCollapse:Z

    #@2
    .line 148
    return-void
.end method

.method public setContentHeight(I)V
    .locals 0
    .param p1, "contentHeight"    # I

    #@0
    .prologue
    .line 196
    iput p1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mContentHeight:I

    #@2
    .line 197
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->requestLayout()V

    #@5
    .line 198
    return-void
.end method

.method public setTabSelected(I)V
    .locals 5
    .param p1, "position"    # I

    #@0
    .prologue
    .line 180
    iput p1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mSelectedTabIndex:I

    #@2
    .line 181
    iget-object v4, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@4
    invoke-virtual {v4}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildCount()I

    #@7
    move-result v3

    #@8
    .line 182
    .local v3, "tabCount":I
    const/4 v1, 0x0

    #@9
    .local v1, "i":I
    :goto_0
    if-ge v1, v3, :cond_2

    #@b
    .line 183
    iget-object v4, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@d
    invoke-virtual {v4, v1}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildAt(I)Landroid/view/View;

    #@10
    move-result-object v0

    #@11
    .line 184
    .local v0, "child":Landroid/view/View;
    if-ne v1, p1, :cond_1

    #@13
    const/4 v2, 0x1

    #@14
    .line 185
    .local v2, "isSelected":Z
    :goto_1
    invoke-virtual {v0, v2}, Landroid/view/View;->setSelected(Z)V

    #@17
    .line 186
    if-eqz v2, :cond_0

    #@19
    .line 187
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->animateToTab(I)V

    #@1c
    .line 182
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@1e
    goto :goto_0

    #@1f
    .line 184
    .end local v2    # "isSelected":Z
    :cond_1
    const/4 v2, 0x0

    #@20
    goto :goto_1

    #@21
    .line 190
    .end local v0    # "child":Landroid/view/View;
    :cond_2
    iget-object v4, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@23
    if-eqz v4, :cond_3

    #@25
    if-ltz p1, :cond_3

    #@27
    .line 191
    iget-object v4, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@29
    invoke-virtual {v4, p1}, Landroid/support/v7/internal/widget/SpinnerCompat;->setSelection(I)V

    #@2c
    .line 193
    :cond_3
    return-void
.end method

.method public updateTab(I)V
    .locals 1
    .param p1, "position"    # I

    #@0
    .prologue
    .line 336
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildAt(I)Landroid/view/View;

    #@5
    move-result-object v0

    #@6
    check-cast v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;

    #@8
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->update()V

    #@b
    .line 337
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@d
    if-eqz v0, :cond_0

    #@f
    .line 338
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@11
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getAdapter()Landroid/widget/SpinnerAdapter;

    #@14
    move-result-object v0

    #@15
    check-cast v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;

    #@17
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;->notifyDataSetChanged()V

    #@1a
    .line 340
    :cond_0
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mAllowCollapse:Z

    #@1c
    if-eqz v0, :cond_1

    #@1e
    .line 341
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->requestLayout()V

    #@21
    .line 343
    :cond_1
    return-void
.end method
