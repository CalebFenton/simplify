.class public Landroid/support/v7/widget/Toolbar$LayoutParams;
.super Landroid/support/v7/app/ActionBar$LayoutParams;
.source "Toolbar.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/widget/Toolbar;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "LayoutParams"
.end annotation


# static fields
.field static final CUSTOM:I = 0x0

.field static final EXPANDED:I = 0x2

.field static final SYSTEM:I = 0x1


# instance fields
.field mViewType:I


# direct methods
.method public constructor <init>(I)V
    .locals 2
    .param p1, "gravity"    # I

    #@0
    .prologue
    .line 1812
    const/4 v0, -0x2

    #@1
    const/4 v1, -0x1

    #@2
    invoke-direct {p0, v0, v1, p1}, Landroid/support/v7/widget/Toolbar$LayoutParams;-><init>(III)V

    #@5
    .line 1813
    return-void
.end method

.method public constructor <init>(II)V
    .locals 1
    .param p1, "width"    # I
    .param p2, "height"    # I

    #@0
    .prologue
    .line 1802
    invoke-direct {p0, p1, p2}, Landroid/support/v7/app/ActionBar$LayoutParams;-><init>(II)V

    #@3
    .line 1795
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@6
    .line 1803
    const v0, 0x800013

    #@9
    iput v0, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->gravity:I

    #@b
    .line 1804
    return-void
.end method

.method public constructor <init>(III)V
    .locals 1
    .param p1, "width"    # I
    .param p2, "height"    # I
    .param p3, "gravity"    # I

    #@0
    .prologue
    .line 1807
    invoke-direct {p0, p1, p2}, Landroid/support/v7/app/ActionBar$LayoutParams;-><init>(II)V

    #@3
    .line 1795
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@6
    .line 1808
    iput p3, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->gravity:I

    #@8
    .line 1809
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "c"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 1798
    invoke-direct {p0, p1, p2}, Landroid/support/v7/app/ActionBar$LayoutParams;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@3
    .line 1795
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@6
    .line 1799
    return-void
.end method

.method public constructor <init>(Landroid/support/v7/app/ActionBar$LayoutParams;)V
    .locals 1
    .param p1, "source"    # Landroid/support/v7/app/ActionBar$LayoutParams;

    #@0
    .prologue
    .line 1822
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBar$LayoutParams;-><init>(Landroid/support/v7/app/ActionBar$LayoutParams;)V

    #@3
    .line 1795
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@6
    .line 1823
    return-void
.end method

.method public constructor <init>(Landroid/support/v7/widget/Toolbar$LayoutParams;)V
    .locals 1
    .param p1, "source"    # Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@0
    .prologue
    .line 1816
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBar$LayoutParams;-><init>(Landroid/support/v7/app/ActionBar$LayoutParams;)V

    #@3
    .line 1795
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@6
    .line 1818
    iget v0, p1, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@8
    iput v0, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@a
    .line 1819
    return-void
.end method

.method public constructor <init>(Landroid/view/ViewGroup$LayoutParams;)V
    .locals 1
    .param p1, "source"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 1833
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBar$LayoutParams;-><init>(Landroid/view/ViewGroup$LayoutParams;)V

    #@3
    .line 1795
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@6
    .line 1834
    return-void
.end method

.method public constructor <init>(Landroid/view/ViewGroup$MarginLayoutParams;)V
    .locals 1
    .param p1, "source"    # Landroid/view/ViewGroup$MarginLayoutParams;

    #@0
    .prologue
    .line 1826
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBar$LayoutParams;-><init>(Landroid/view/ViewGroup$LayoutParams;)V

    #@3
    .line 1795
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@6
    .line 1829
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/Toolbar$LayoutParams;->copyMarginsFromCompat(Landroid/view/ViewGroup$MarginLayoutParams;)V

    #@9
    .line 1830
    return-void
.end method


# virtual methods
.method copyMarginsFromCompat(Landroid/view/ViewGroup$MarginLayoutParams;)V
    .locals 1
    .param p1, "source"    # Landroid/view/ViewGroup$MarginLayoutParams;

    #@0
    .prologue
    .line 1837
    iget v0, p1, Landroid/view/ViewGroup$MarginLayoutParams;->leftMargin:I

    #@2
    iput v0, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->leftMargin:I

    #@4
    .line 1838
    iget v0, p1, Landroid/view/ViewGroup$MarginLayoutParams;->topMargin:I

    #@6
    iput v0, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->topMargin:I

    #@8
    .line 1839
    iget v0, p1, Landroid/view/ViewGroup$MarginLayoutParams;->rightMargin:I

    #@a
    iput v0, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->rightMargin:I

    #@c
    .line 1840
    iget v0, p1, Landroid/view/ViewGroup$MarginLayoutParams;->bottomMargin:I

    #@e
    iput v0, p0, Landroid/support/v7/widget/Toolbar$LayoutParams;->bottomMargin:I

    #@10
    .line 1841
    return-void
.end method
