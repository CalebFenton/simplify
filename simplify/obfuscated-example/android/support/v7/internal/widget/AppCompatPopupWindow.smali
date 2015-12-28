.class public Landroid/support/v7/internal/widget/AppCompatPopupWindow;
.super Landroid/widget/PopupWindow;
.source "AppCompatPopupWindow.java"


# instance fields
.field private final mOverlapAnchor:Z


# direct methods
.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyleAttr"    # I

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 37
    invoke-direct {p0, p1, p2, p3}, Landroid/widget/PopupWindow;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 39
    sget-object v1, Landroid/support/v7/appcompat/R$styleable;->PopupWindow:[I

    #@6
    invoke-static {p1, p2, v1, p3, v2}, Landroid/support/v7/internal/widget/TintTypedArray;->obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/internal/widget/TintTypedArray;

    #@9
    move-result-object v0

    #@a
    .line 41
    .local v0, "a":Landroid/support/v7/internal/widget/TintTypedArray;
    sget v1, Landroid/support/v7/appcompat/R$styleable;->PopupWindow_overlapAnchor:I

    #@c
    invoke-virtual {v0, v1, v2}, Landroid/support/v7/internal/widget/TintTypedArray;->getBoolean(IZ)Z

    #@f
    move-result v1

    #@10
    iput-boolean v1, p0, Landroid/support/v7/internal/widget/AppCompatPopupWindow;->mOverlapAnchor:Z

    #@12
    .line 43
    sget v1, Landroid/support/v7/appcompat/R$styleable;->PopupWindow_android_popupBackground:I

    #@14
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@17
    move-result-object v1

    #@18
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/AppCompatPopupWindow;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@1b
    .line 44
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->recycle()V

    #@1e
    .line 45
    return-void
.end method


# virtual methods
.method public showAsDropDown(Landroid/view/View;II)V
    .locals 2
    .param p1, "anchor"    # Landroid/view/View;
    .param p2, "xoff"    # I
    .param p3, "yoff"    # I

    #@0
    .prologue
    .line 49
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x15

    #@4
    if-ge v0, v1, :cond_0

    #@6
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/AppCompatPopupWindow;->mOverlapAnchor:Z

    #@8
    if-eqz v0, :cond_0

    #@a
    .line 51
    invoke-virtual {p1}, Landroid/view/View;->getHeight()I

    #@d
    move-result v0

    #@e
    sub-int/2addr p3, v0

    #@f
    .line 53
    :cond_0
    invoke-super {p0, p1, p2, p3}, Landroid/widget/PopupWindow;->showAsDropDown(Landroid/view/View;II)V

    #@12
    .line 54
    return-void
.end method

.method public showAsDropDown(Landroid/view/View;III)V
    .locals 2
    .param p1, "anchor"    # Landroid/view/View;
    .param p2, "xoff"    # I
    .param p3, "yoff"    # I
    .param p4, "gravity"    # I
    .annotation build Landroid/annotation/TargetApi;
        value = 0x13
    .end annotation

    #@0
    .prologue
    .line 59
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x15

    #@4
    if-ge v0, v1, :cond_0

    #@6
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/AppCompatPopupWindow;->mOverlapAnchor:Z

    #@8
    if-eqz v0, :cond_0

    #@a
    .line 61
    invoke-virtual {p1}, Landroid/view/View;->getHeight()I

    #@d
    move-result v0

    #@e
    sub-int/2addr p3, v0

    #@f
    .line 63
    :cond_0
    invoke-super {p0, p1, p2, p3, p4}, Landroid/widget/PopupWindow;->showAsDropDown(Landroid/view/View;III)V

    #@12
    .line 64
    return-void
.end method

.method public update(Landroid/view/View;IIII)V
    .locals 2
    .param p1, "anchor"    # Landroid/view/View;
    .param p2, "xoff"    # I
    .param p3, "yoff"    # I
    .param p4, "width"    # I
    .param p5, "height"    # I

    #@0
    .prologue
    .line 68
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x15

    #@4
    if-ge v0, v1, :cond_0

    #@6
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/AppCompatPopupWindow;->mOverlapAnchor:Z

    #@8
    if-eqz v0, :cond_0

    #@a
    .line 70
    invoke-virtual {p1}, Landroid/view/View;->getHeight()I

    #@d
    move-result v0

    #@e
    sub-int/2addr p3, v0

    #@f
    .line 72
    :cond_0
    invoke-super/range {p0 .. p5}, Landroid/widget/PopupWindow;->update(Landroid/view/View;IIII)V

    #@12
    .line 73
    return-void
.end method
