.class public Landroid/support/v7/app/ActionBar$LayoutParams;
.super Landroid/view/ViewGroup$MarginLayoutParams;
.source "ActionBar.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/app/ActionBar;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "LayoutParams"
.end annotation


# instance fields
.field public gravity:I


# direct methods
.method public constructor <init>(I)V
    .locals 2
    .param p1, "gravity"    # I

    #@0
    .prologue
    .line 1340
    const/4 v0, -0x2

    #@1
    const/4 v1, -0x1

    #@2
    invoke-direct {p0, v0, v1, p1}, Landroid/support/v7/app/ActionBar$LayoutParams;-><init>(III)V

    #@5
    .line 1341
    return-void
.end method

.method public constructor <init>(II)V
    .locals 1
    .param p1, "width"    # I
    .param p2, "height"    # I

    #@0
    .prologue
    .line 1330
    invoke-direct {p0, p1, p2}, Landroid/view/ViewGroup$MarginLayoutParams;-><init>(II)V

    #@3
    .line 1319
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/app/ActionBar$LayoutParams;->gravity:I

    #@6
    .line 1331
    const v0, 0x800013

    #@9
    iput v0, p0, Landroid/support/v7/app/ActionBar$LayoutParams;->gravity:I

    #@b
    .line 1332
    return-void
.end method

.method public constructor <init>(III)V
    .locals 1
    .param p1, "width"    # I
    .param p2, "height"    # I
    .param p3, "gravity"    # I

    #@0
    .prologue
    .line 1335
    invoke-direct {p0, p1, p2}, Landroid/view/ViewGroup$MarginLayoutParams;-><init>(II)V

    #@3
    .line 1319
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/app/ActionBar$LayoutParams;->gravity:I

    #@6
    .line 1336
    iput p3, p0, Landroid/support/v7/app/ActionBar$LayoutParams;->gravity:I

    #@8
    .line 1337
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 3
    .param p1, "c"    # Landroid/content/Context;
        .annotation build Landroid/support/annotation/NonNull;
        .end annotation
    .end param
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 1322
    invoke-direct {p0, p1, p2}, Landroid/view/ViewGroup$MarginLayoutParams;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 1319
    iput v2, p0, Landroid/support/v7/app/ActionBar$LayoutParams;->gravity:I

    #@6
    .line 1324
    sget-object v1, Landroid/support/v7/appcompat/R$styleable;->ActionBarLayout:[I

    #@8
    invoke-virtual {p1, p2, v1}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;

    #@b
    move-result-object v0

    #@c
    .line 1325
    .local v0, "a":Landroid/content/res/TypedArray;
    sget v1, Landroid/support/v7/appcompat/R$styleable;->ActionBarLayout_android_layout_gravity:I

    #@e
    invoke-virtual {v0, v1, v2}, Landroid/content/res/TypedArray;->getInt(II)I

    #@11
    move-result v1

    #@12
    iput v1, p0, Landroid/support/v7/app/ActionBar$LayoutParams;->gravity:I

    #@14
    .line 1326
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@17
    .line 1327
    return-void
.end method

.method public constructor <init>(Landroid/support/v7/app/ActionBar$LayoutParams;)V
    .locals 1
    .param p1, "source"    # Landroid/support/v7/app/ActionBar$LayoutParams;

    #@0
    .prologue
    .line 1344
    invoke-direct {p0, p1}, Landroid/view/ViewGroup$MarginLayoutParams;-><init>(Landroid/view/ViewGroup$MarginLayoutParams;)V

    #@3
    .line 1319
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/app/ActionBar$LayoutParams;->gravity:I

    #@6
    .line 1346
    iget v0, p1, Landroid/support/v7/app/ActionBar$LayoutParams;->gravity:I

    #@8
    iput v0, p0, Landroid/support/v7/app/ActionBar$LayoutParams;->gravity:I

    #@a
    .line 1347
    return-void
.end method

.method public constructor <init>(Landroid/view/ViewGroup$LayoutParams;)V
    .locals 1
    .param p1, "source"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 1350
    invoke-direct {p0, p1}, Landroid/view/ViewGroup$MarginLayoutParams;-><init>(Landroid/view/ViewGroup$LayoutParams;)V

    #@3
    .line 1319
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v7/app/ActionBar$LayoutParams;->gravity:I

    #@6
    .line 1351
    return-void
.end method
