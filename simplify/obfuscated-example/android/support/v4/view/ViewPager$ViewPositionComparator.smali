.class Landroid/support/v4/view/ViewPager$ViewPositionComparator;
.super Ljava/lang/Object;
.source "ViewPager.java"

# interfaces
.implements Ljava/util/Comparator;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/ViewPager;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "ViewPositionComparator"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Object;",
        "Ljava/util/Comparator",
        "<",
        "Landroid/view/View;",
        ">;"
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 2884
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public compare(Landroid/view/View;Landroid/view/View;)I
    .locals 4
    .param p1, "lhs"    # Landroid/view/View;
    .param p2, "rhs"    # Landroid/view/View;

    #@0
    .prologue
    .line 2887
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v0

    #@4
    check-cast v0, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@6
    .line 2888
    .local v0, "llp":Landroid/support/v4/view/ViewPager$LayoutParams;
    invoke-virtual {p2}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@9
    move-result-object v1

    #@a
    check-cast v1, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@c
    .line 2889
    .local v1, "rlp":Landroid/support/v4/view/ViewPager$LayoutParams;
    iget-boolean v2, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@e
    iget-boolean v3, v1, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@10
    if-eq v2, v3, :cond_1

    #@12
    .line 2890
    iget-boolean v2, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@14
    if-eqz v2, :cond_0

    #@16
    const/4 v2, 0x1

    #@17
    .line 2892
    :goto_0
    return v2

    #@18
    .line 2890
    :cond_0
    const/4 v2, -0x1

    #@19
    goto :goto_0

    #@1a
    .line 2892
    :cond_1
    iget v2, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->position:I

    #@1c
    iget v3, v1, Landroid/support/v4/view/ViewPager$LayoutParams;->position:I

    #@1e
    sub-int/2addr v2, v3

    #@1f
    goto :goto_0
.end method

.method public bridge synthetic compare(Ljava/lang/Object;Ljava/lang/Object;)I
    .locals 1
    .param p1, "x0"    # Ljava/lang/Object;
    .param p2, "x1"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 2884
    check-cast p1, Landroid/view/View;

    #@2
    .end local p1    # "x0":Ljava/lang/Object;
    check-cast p2, Landroid/view/View;

    #@4
    .end local p2    # "x1":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2}, Landroid/support/v4/view/ViewPager$ViewPositionComparator;->compare(Landroid/view/View;Landroid/view/View;)I

    #@7
    move-result v0

    #@8
    return v0
.end method
