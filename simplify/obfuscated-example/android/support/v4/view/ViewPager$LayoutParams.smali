.class public Landroid/support/v4/view/ViewPager$LayoutParams;
.super Landroid/view/ViewGroup$LayoutParams;
.source "ViewPager.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/ViewPager;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "LayoutParams"
.end annotation


# instance fields
.field childIndex:I

.field public gravity:I

.field public isDecor:Z

.field needsMeasure:Z

.field position:I

.field widthFactor:F


# direct methods
.method public constructor <init>()V
    .locals 1

    #@0
    .prologue
    const/4 v0, -0x1

    #@1
    .line 2872
    invoke-direct {p0, v0, v0}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    #@4
    .line 2853
    const/4 v0, 0x0

    #@5
    iput v0, p0, Landroid/support/v4/view/ViewPager$LayoutParams;->widthFactor:F

    #@7
    .line 2873
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 2876
    invoke-direct {p0, p1, p2}, Landroid/view/ViewGroup$LayoutParams;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@3
    .line 2853
    const/4 v1, 0x0

    #@4
    iput v1, p0, Landroid/support/v4/view/ViewPager$LayoutParams;->widthFactor:F

    #@6
    .line 2878
    # getter for: Landroid/support/v4/view/ViewPager;->LAYOUT_ATTRS:[I
    invoke-static {}, Landroid/support/v4/view/ViewPager;->access$400()[I

    #@9
    move-result-object v1

    #@a
    invoke-virtual {p1, p2, v1}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;

    #@d
    move-result-object v0

    #@e
    .line 2879
    .local v0, "a":Landroid/content/res/TypedArray;
    const/4 v1, 0x0

    #@f
    const/16 v2, 0x30

    #@11
    invoke-virtual {v0, v1, v2}, Landroid/content/res/TypedArray;->getInteger(II)I

    #@14
    move-result v1

    #@15
    iput v1, p0, Landroid/support/v4/view/ViewPager$LayoutParams;->gravity:I

    #@17
    .line 2880
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@1a
    .line 2881
    return-void
.end method
