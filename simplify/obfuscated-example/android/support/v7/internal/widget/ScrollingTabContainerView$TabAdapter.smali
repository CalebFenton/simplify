.class Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;
.super Landroid/widget/BaseAdapter;
.source "ScrollingTabContainerView.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/ScrollingTabContainerView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "TabAdapter"
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;


# direct methods
.method private constructor <init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V
    .locals 0

    #@0
    .prologue
    .line 539
    iput-object p1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@2
    invoke-direct {p0}, Landroid/widget/BaseAdapter;-><init>()V

    #@5
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v7/internal/widget/ScrollingTabContainerView;
    .param p2, "x1"    # Landroid/support/v7/internal/widget/ScrollingTabContainerView$1;

    #@0
    .prologue
    .line 539
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;-><init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V

    #@3
    return-void
.end method


# virtual methods
.method public getCount()I
    .locals 1

    #@0
    .prologue
    .line 542
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@2
    # getter for: Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;
    invoke-static {v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->access$200(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)Landroid/support/v7/widget/LinearLayoutCompat;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildCount()I

    #@9
    move-result v0

    #@a
    return v0
.end method

.method public getItem(I)Ljava/lang/Object;
    .locals 1
    .param p1, "position"    # I

    #@0
    .prologue
    .line 547
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@2
    # getter for: Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mTabLayout:Landroid/support/v7/widget/LinearLayoutCompat;
    invoke-static {v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->access$200(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)Landroid/support/v7/widget/LinearLayoutCompat;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->getChildAt(I)Landroid/view/View;

    #@9
    move-result-object v0

    #@a
    check-cast v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;

    #@c
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->getTab()Landroid/support/v7/app/ActionBar$Tab;

    #@f
    move-result-object v0

    #@10
    return-object v0
.end method

.method public getItemId(I)J
    .locals 2
    .param p1, "position"    # I

    #@0
    .prologue
    .line 552
    int-to-long v0, p1

    #@1
    return-wide v0
.end method

.method public getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 3
    .param p1, "position"    # I
    .param p2, "convertView"    # Landroid/view/View;
    .param p3, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 557
    if-nez p2, :cond_0

    #@2
    .line 558
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@4
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;->getItem(I)Ljava/lang/Object;

    #@7
    move-result-object v0

    #@8
    check-cast v0, Landroid/support/v7/app/ActionBar$Tab;

    #@a
    const/4 v2, 0x1

    #@b
    # invokes: Landroid/support/v7/internal/widget/ScrollingTabContainerView;->createTabView(Landroid/support/v7/app/ActionBar$Tab;Z)Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;
    invoke-static {v1, v0, v2}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->access$300(Landroid/support/v7/internal/widget/ScrollingTabContainerView;Landroid/support/v7/app/ActionBar$Tab;Z)Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;

    #@e
    move-result-object p2

    #@f
    .line 562
    :goto_0
    return-object p2

    #@10
    :cond_0
    move-object v0, p2

    #@11
    .line 560
    check-cast v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;

    #@13
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabAdapter;->getItem(I)Ljava/lang/Object;

    #@16
    move-result-object v1

    #@17
    check-cast v1, Landroid/support/v7/app/ActionBar$Tab;

    #@19
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->bindTab(Landroid/support/v7/app/ActionBar$Tab;)V

    #@1c
    goto :goto_0
.end method
