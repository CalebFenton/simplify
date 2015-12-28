.class public Landroid/support/v7/widget/SwitchCompat;
.super Landroid/widget/CompoundButton;
.source "SwitchCompat.java"


# static fields
.field private static final CHECKED_STATE_SET:[I

.field private static final MONOSPACE:I = 0x3

.field private static final SANS:I = 0x1

.field private static final SERIF:I = 0x2

.field private static final TEXT_APPEARANCE_ATTRS:[I

.field private static final THUMB_ANIMATION_DURATION:I = 0xfa

.field private static final TOUCH_MODE_DOWN:I = 0x1

.field private static final TOUCH_MODE_DRAGGING:I = 0x2

.field private static final TOUCH_MODE_IDLE:I


# instance fields
.field private mMinFlingVelocity:I

.field private mOffLayout:Landroid/text/Layout;

.field private mOnLayout:Landroid/text/Layout;

.field private mPositionAnimator:Landroid/view/animation/Animation;

.field private mShowText:Z

.field private mSplitTrack:Z

.field private mSwitchBottom:I

.field private mSwitchHeight:I

.field private mSwitchLeft:I

.field private mSwitchMinWidth:I

.field private mSwitchPadding:I

.field private mSwitchRight:I

.field private mSwitchTop:I

.field private mSwitchTransformationMethod:Landroid/text/method/TransformationMethod;

.field private mSwitchWidth:I

.field private final mTempRect:Landroid/graphics/Rect;

.field private mTextColors:Landroid/content/res/ColorStateList;

.field private mTextOff:Ljava/lang/CharSequence;

.field private mTextOn:Ljava/lang/CharSequence;

.field private mTextPaint:Landroid/text/TextPaint;

.field private mThumbDrawable:Landroid/graphics/drawable/Drawable;

.field private mThumbPosition:F

.field private mThumbTextPadding:I

.field private mThumbWidth:I

.field private final mTintManager:Landroid/support/v7/internal/widget/TintManager;

.field private mTouchMode:I

.field private mTouchSlop:I

.field private mTouchX:F

.field private mTouchY:F

.field private mTrackDrawable:Landroid/graphics/drawable/Drawable;

.field private mVelocityTracker:Landroid/view/VelocityTracker;


# direct methods
.method static constructor <clinit>()V
    .locals 5

    #@0
    .prologue
    const/4 v4, 0x1

    #@1
    const/4 v3, 0x0

    #@2
    .line 78
    const/4 v0, 0x3

    #@3
    new-array v0, v0, [I

    #@5
    const v1, 0x1010098

    #@8
    aput v1, v0, v3

    #@a
    const v1, 0x1010095

    #@d
    aput v1, v0, v4

    #@f
    const/4 v1, 0x2

    #@10
    sget v2, Landroid/support/v7/appcompat/R$attr;->textAllCaps:I

    #@12
    aput v2, v0, v1

    #@14
    sput-object v0, Landroid/support/v7/widget/SwitchCompat;->TEXT_APPEARANCE_ATTRS:[I

    #@16
    .line 150
    new-array v0, v4, [I

    #@18
    const v1, 0x10100a0

    #@1b
    aput v1, v0, v3

    #@1d
    sput-object v0, Landroid/support/v7/widget/SwitchCompat;->CHECKED_STATE_SET:[I

    #@1f
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 160
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/widget/SwitchCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 161
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 171
    sget v0, Landroid/support/v7/appcompat/R$attr;->switchStyle:I

    #@2
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/widget/SwitchCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@5
    .line 172
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 8
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyleAttr"    # I

    #@0
    .prologue
    const/4 v7, 0x1

    #@1
    const/4 v6, 0x0

    #@2
    .line 185
    invoke-direct {p0, p1, p2, p3}, Landroid/widget/CompoundButton;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@5
    .line 103
    invoke-static {}, Landroid/view/VelocityTracker;->obtain()Landroid/view/VelocityTracker;

    #@8
    move-result-object v4

    #@9
    iput-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@b
    .line 145
    new-instance v4, Landroid/graphics/Rect;

    #@d
    invoke-direct {v4}, Landroid/graphics/Rect;-><init>()V

    #@10
    iput-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mTempRect:Landroid/graphics/Rect;

    #@12
    .line 187
    new-instance v4, Landroid/text/TextPaint;

    #@14
    invoke-direct {v4, v7}, Landroid/text/TextPaint;-><init>(I)V

    #@17
    iput-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@19
    .line 189
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getResources()Landroid/content/res/Resources;

    #@1c
    move-result-object v3

    #@1d
    .line 190
    .local v3, "res":Landroid/content/res/Resources;
    iget-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@1f
    invoke-virtual {v3}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@22
    move-result-object v5

    #@23
    iget v5, v5, Landroid/util/DisplayMetrics;->density:F

    #@25
    iput v5, v4, Landroid/text/TextPaint;->density:F

    #@27
    .line 192
    sget-object v4, Landroid/support/v7/appcompat/R$styleable;->SwitchCompat:[I

    #@29
    invoke-static {p1, p2, v4, p3, v6}, Landroid/support/v7/internal/widget/TintTypedArray;->obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/internal/widget/TintTypedArray;

    #@2c
    move-result-object v0

    #@2d
    .line 194
    .local v0, "a":Landroid/support/v7/internal/widget/TintTypedArray;
    sget v4, Landroid/support/v7/appcompat/R$styleable;->SwitchCompat_android_thumb:I

    #@2f
    invoke-virtual {v0, v4}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@32
    move-result-object v4

    #@33
    iput-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@35
    .line 195
    sget v4, Landroid/support/v7/appcompat/R$styleable;->SwitchCompat_track:I

    #@37
    invoke-virtual {v0, v4}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@3a
    move-result-object v4

    #@3b
    iput-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@3d
    .line 196
    sget v4, Landroid/support/v7/appcompat/R$styleable;->SwitchCompat_android_textOn:I

    #@3f
    invoke-virtual {v0, v4}, Landroid/support/v7/internal/widget/TintTypedArray;->getText(I)Ljava/lang/CharSequence;

    #@42
    move-result-object v4

    #@43
    iput-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mTextOn:Ljava/lang/CharSequence;

    #@45
    .line 197
    sget v4, Landroid/support/v7/appcompat/R$styleable;->SwitchCompat_android_textOff:I

    #@47
    invoke-virtual {v0, v4}, Landroid/support/v7/internal/widget/TintTypedArray;->getText(I)Ljava/lang/CharSequence;

    #@4a
    move-result-object v4

    #@4b
    iput-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mTextOff:Ljava/lang/CharSequence;

    #@4d
    .line 198
    sget v4, Landroid/support/v7/appcompat/R$styleable;->SwitchCompat_showText:I

    #@4f
    invoke-virtual {v0, v4, v7}, Landroid/support/v7/internal/widget/TintTypedArray;->getBoolean(IZ)Z

    #@52
    move-result v4

    #@53
    iput-boolean v4, p0, Landroid/support/v7/widget/SwitchCompat;->mShowText:Z

    #@55
    .line 199
    sget v4, Landroid/support/v7/appcompat/R$styleable;->SwitchCompat_thumbTextPadding:I

    #@57
    invoke-virtual {v0, v4, v6}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelSize(II)I

    #@5a
    move-result v4

    #@5b
    iput v4, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbTextPadding:I

    #@5d
    .line 201
    sget v4, Landroid/support/v7/appcompat/R$styleable;->SwitchCompat_switchMinWidth:I

    #@5f
    invoke-virtual {v0, v4, v6}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelSize(II)I

    #@62
    move-result v4

    #@63
    iput v4, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchMinWidth:I

    #@65
    .line 203
    sget v4, Landroid/support/v7/appcompat/R$styleable;->SwitchCompat_switchPadding:I

    #@67
    invoke-virtual {v0, v4, v6}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelSize(II)I

    #@6a
    move-result v4

    #@6b
    iput v4, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchPadding:I

    #@6d
    .line 205
    sget v4, Landroid/support/v7/appcompat/R$styleable;->SwitchCompat_splitTrack:I

    #@6f
    invoke-virtual {v0, v4, v6}, Landroid/support/v7/internal/widget/TintTypedArray;->getBoolean(IZ)Z

    #@72
    move-result v4

    #@73
    iput-boolean v4, p0, Landroid/support/v7/widget/SwitchCompat;->mSplitTrack:Z

    #@75
    .line 207
    sget v4, Landroid/support/v7/appcompat/R$styleable;->SwitchCompat_switchTextAppearance:I

    #@77
    invoke-virtual {v0, v4, v6}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@7a
    move-result v1

    #@7b
    .line 209
    .local v1, "appearance":I
    if-eqz v1, :cond_0

    #@7d
    .line 210
    invoke-virtual {p0, p1, v1}, Landroid/support/v7/widget/SwitchCompat;->setSwitchTextAppearance(Landroid/content/Context;I)V

    #@80
    .line 213
    :cond_0
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getTintManager()Landroid/support/v7/internal/widget/TintManager;

    #@83
    move-result-object v4

    #@84
    iput-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@86
    .line 215
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->recycle()V

    #@89
    .line 217
    invoke-static {p1}, Landroid/view/ViewConfiguration;->get(Landroid/content/Context;)Landroid/view/ViewConfiguration;

    #@8c
    move-result-object v2

    #@8d
    .line 218
    .local v2, "config":Landroid/view/ViewConfiguration;
    invoke-virtual {v2}, Landroid/view/ViewConfiguration;->getScaledTouchSlop()I

    #@90
    move-result v4

    #@91
    iput v4, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchSlop:I

    #@93
    .line 219
    invoke-virtual {v2}, Landroid/view/ViewConfiguration;->getScaledMinimumFlingVelocity()I

    #@96
    move-result v4

    #@97
    iput v4, p0, Landroid/support/v7/widget/SwitchCompat;->mMinFlingVelocity:I

    #@99
    .line 222
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->refreshDrawableState()V

    #@9c
    .line 223
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->isChecked()Z

    #@9f
    move-result v4

    #@a0
    invoke-virtual {p0, v4}, Landroid/support/v7/widget/SwitchCompat;->setChecked(Z)V

    #@a3
    .line 224
    return-void
.end method

.method static synthetic access$000(Landroid/support/v7/widget/SwitchCompat;F)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/SwitchCompat;
    .param p1, "x1"    # F

    #@0
    .prologue
    .line 71
    invoke-direct {p0, p1}, Landroid/support/v7/widget/SwitchCompat;->setThumbPosition(F)V

    #@3
    return-void
.end method

.method private animateThumbToCheckedState(Z)V
    .locals 6
    .param p1, "newCheckedState"    # Z

    #@0
    .prologue
    .line 706
    iget v1, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbPosition:F

    #@2
    .line 707
    .local v1, "startPosition":F
    if-eqz p1, :cond_0

    #@4
    const/high16 v2, 0x3f800000    # 1.0f

    #@6
    .line 708
    .local v2, "targetPosition":F
    :goto_0
    sub-float v0, v2, v1

    #@8
    .line 710
    .local v0, "diff":F
    new-instance v3, Landroid/support/v7/widget/SwitchCompat$1;

    #@a
    invoke-direct {v3, p0, v1, v0}, Landroid/support/v7/widget/SwitchCompat$1;-><init>(Landroid/support/v7/widget/SwitchCompat;FF)V

    #@d
    iput-object v3, p0, Landroid/support/v7/widget/SwitchCompat;->mPositionAnimator:Landroid/view/animation/Animation;

    #@f
    .line 716
    iget-object v3, p0, Landroid/support/v7/widget/SwitchCompat;->mPositionAnimator:Landroid/view/animation/Animation;

    #@11
    const-wide/16 v4, 0xfa

    #@13
    invoke-virtual {v3, v4, v5}, Landroid/view/animation/Animation;->setDuration(J)V

    #@16
    .line 717
    iget-object v3, p0, Landroid/support/v7/widget/SwitchCompat;->mPositionAnimator:Landroid/view/animation/Animation;

    #@18
    invoke-virtual {p0, v3}, Landroid/support/v7/widget/SwitchCompat;->startAnimation(Landroid/view/animation/Animation;)V

    #@1b
    .line 718
    return-void

    #@1c
    .line 707
    .end local v0    # "diff":F
    .end local v2    # "targetPosition":F
    :cond_0
    const/4 v2, 0x0

    #@1d
    goto :goto_0
.end method

.method private cancelPositionAnimator()V
    .locals 1

    #@0
    .prologue
    .line 721
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mPositionAnimator:Landroid/view/animation/Animation;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 722
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->clearAnimation()V

    #@7
    .line 723
    const/4 v0, 0x0

    #@8
    iput-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mPositionAnimator:Landroid/view/animation/Animation;

    #@a
    .line 725
    :cond_0
    return-void
.end method

.method private cancelSuperTouch(Landroid/view/MotionEvent;)V
    .locals 2
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 671
    invoke-static {p1}, Landroid/view/MotionEvent;->obtain(Landroid/view/MotionEvent;)Landroid/view/MotionEvent;

    #@3
    move-result-object v0

    #@4
    .line 672
    .local v0, "cancel":Landroid/view/MotionEvent;
    const/4 v1, 0x3

    #@5
    invoke-virtual {v0, v1}, Landroid/view/MotionEvent;->setAction(I)V

    #@8
    .line 673
    invoke-super {p0, v0}, Landroid/widget/CompoundButton;->onTouchEvent(Landroid/view/MotionEvent;)Z

    #@b
    .line 674
    invoke-virtual {v0}, Landroid/view/MotionEvent;->recycle()V

    #@e
    .line 675
    return-void
.end method

.method private static constrain(FFF)F
    .locals 1
    .param p0, "amount"    # F
    .param p1, "low"    # F
    .param p2, "high"    # F

    #@0
    .prologue
    .line 1060
    cmpg-float v0, p0, p1

    #@2
    if-gez v0, :cond_0

    #@4
    .end local p1    # "low":F
    :goto_0
    return p1

    #@5
    .restart local p1    # "low":F
    :cond_0
    cmpl-float v0, p0, p2

    #@7
    if-lez v0, :cond_1

    #@9
    move p1, p2

    #@a
    goto :goto_0

    #@b
    :cond_1
    move p1, p0

    #@c
    goto :goto_0
.end method

.method private getTargetCheckedState()Z
    .locals 2

    #@0
    .prologue
    .line 728
    iget v0, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbPosition:F

    #@2
    const/high16 v1, 0x3f000000    # 0.5f

    #@4
    cmpl-float v0, v0, v1

    #@6
    if-lez v0, :cond_0

    #@8
    const/4 v0, 0x1

    #@9
    :goto_0
    return v0

    #@a
    :cond_0
    const/4 v0, 0x0

    #@b
    goto :goto_0
.end method

.method private getThumbOffset()I
    .locals 3

    #@0
    .prologue
    .line 947
    invoke-static {p0}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@3
    move-result v1

    #@4
    if-eqz v1, :cond_0

    #@6
    .line 948
    const/high16 v1, 0x3f800000    # 1.0f

    #@8
    iget v2, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbPosition:F

    #@a
    sub-float v0, v1, v2

    #@c
    .line 952
    .local v0, "thumbPosition":F
    :goto_0
    invoke-direct {p0}, Landroid/support/v7/widget/SwitchCompat;->getThumbScrollRange()I

    #@f
    move-result v1

    #@10
    int-to-float v1, v1

    #@11
    mul-float/2addr v1, v0

    #@12
    const/high16 v2, 0x3f000000    # 0.5f

    #@14
    add-float/2addr v1, v2

    #@15
    float-to-int v1, v1

    #@16
    return v1

    #@17
    .line 950
    .end local v0    # "thumbPosition":F
    :cond_0
    iget v0, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbPosition:F

    #@19
    .restart local v0    # "thumbPosition":F
    goto :goto_0
.end method

.method private getThumbScrollRange()I
    .locals 3

    #@0
    .prologue
    .line 956
    iget-object v1, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    if-eqz v1, :cond_0

    #@4
    .line 957
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTempRect:Landroid/graphics/Rect;

    #@6
    .line 958
    .local v0, "padding":Landroid/graphics/Rect;
    iget-object v1, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@8
    invoke-virtual {v1, v0}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@b
    .line 959
    iget v1, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchWidth:I

    #@d
    iget v2, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbWidth:I

    #@f
    sub-int/2addr v1, v2

    #@10
    iget v2, v0, Landroid/graphics/Rect;->left:I

    #@12
    sub-int/2addr v1, v2

    #@13
    iget v2, v0, Landroid/graphics/Rect;->right:I

    #@15
    sub-int/2addr v1, v2

    #@16
    .line 961
    .end local v0    # "padding":Landroid/graphics/Rect;
    :goto_0
    return v1

    #@17
    :cond_0
    const/4 v1, 0x0

    #@18
    goto :goto_0
.end method

.method private hitThumb(FF)Z
    .locals 7
    .param p1, "x"    # F
    .param p2, "y"    # F

    #@0
    .prologue
    .line 580
    invoke-direct {p0}, Landroid/support/v7/widget/SwitchCompat;->getThumbOffset()I

    #@3
    move-result v2

    #@4
    .line 582
    .local v2, "thumbOffset":I
    iget-object v5, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@6
    iget-object v6, p0, Landroid/support/v7/widget/SwitchCompat;->mTempRect:Landroid/graphics/Rect;

    #@8
    invoke-virtual {v5, v6}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@b
    .line 583
    iget v5, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchTop:I

    #@d
    iget v6, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchSlop:I

    #@f
    sub-int v4, v5, v6

    #@11
    .line 584
    .local v4, "thumbTop":I
    iget v5, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchLeft:I

    #@13
    add-int/2addr v5, v2

    #@14
    iget v6, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchSlop:I

    #@16
    sub-int v1, v5, v6

    #@18
    .line 585
    .local v1, "thumbLeft":I
    iget v5, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbWidth:I

    #@1a
    add-int/2addr v5, v1

    #@1b
    iget-object v6, p0, Landroid/support/v7/widget/SwitchCompat;->mTempRect:Landroid/graphics/Rect;

    #@1d
    iget v6, v6, Landroid/graphics/Rect;->left:I

    #@1f
    add-int/2addr v5, v6

    #@20
    iget-object v6, p0, Landroid/support/v7/widget/SwitchCompat;->mTempRect:Landroid/graphics/Rect;

    #@22
    iget v6, v6, Landroid/graphics/Rect;->right:I

    #@24
    add-int/2addr v5, v6

    #@25
    iget v6, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchSlop:I

    #@27
    add-int v3, v5, v6

    #@29
    .line 587
    .local v3, "thumbRight":I
    iget v5, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchBottom:I

    #@2b
    iget v6, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchSlop:I

    #@2d
    add-int v0, v5, v6

    #@2f
    .line 588
    .local v0, "thumbBottom":I
    int-to-float v5, v1

    #@30
    cmpl-float v5, p1, v5

    #@32
    if-lez v5, :cond_0

    #@34
    int-to-float v5, v3

    #@35
    cmpg-float v5, p1, v5

    #@37
    if-gez v5, :cond_0

    #@39
    int-to-float v5, v4

    #@3a
    cmpl-float v5, p2, v5

    #@3c
    if-lez v5, :cond_0

    #@3e
    int-to-float v5, v0

    #@3f
    cmpg-float v5, p2, v5

    #@41
    if-gez v5, :cond_0

    #@43
    const/4 v5, 0x1

    #@44
    :goto_0
    return v5

    #@45
    :cond_0
    const/4 v5, 0x0

    #@46
    goto :goto_0
.end method

.method private makeLayout(Ljava/lang/CharSequence;)Landroid/text/Layout;
    .locals 8
    .param p1, "text"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 566
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchTransformationMethod:Landroid/text/method/TransformationMethod;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchTransformationMethod:Landroid/text/method/TransformationMethod;

    #@6
    invoke-interface {v0, p1, p0}, Landroid/text/method/TransformationMethod;->getTransformation(Ljava/lang/CharSequence;Landroid/view/View;)Ljava/lang/CharSequence;

    #@9
    move-result-object v1

    #@a
    .line 570
    .local v1, "transformed":Ljava/lang/CharSequence;
    :goto_0
    new-instance v0, Landroid/text/StaticLayout;

    #@c
    iget-object v2, p0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@e
    iget-object v3, p0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@10
    invoke-static {v1, v3}, Landroid/text/Layout;->getDesiredWidth(Ljava/lang/CharSequence;Landroid/text/TextPaint;)F

    #@13
    move-result v3

    #@14
    float-to-double v4, v3

    #@15
    invoke-static {v4, v5}, Ljava/lang/Math;->ceil(D)D

    #@18
    move-result-wide v4

    #@19
    double-to-int v3, v4

    #@1a
    sget-object v4, Landroid/text/Layout$Alignment;->ALIGN_NORMAL:Landroid/text/Layout$Alignment;

    #@1c
    const/high16 v5, 0x3f800000    # 1.0f

    #@1e
    const/4 v6, 0x0

    #@1f
    const/4 v7, 0x1

    #@20
    invoke-direct/range {v0 .. v7}, Landroid/text/StaticLayout;-><init>(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V

    #@23
    return-object v0

    #@24
    .end local v1    # "transformed":Ljava/lang/CharSequence;
    :cond_0
    move-object v1, p1

    #@25
    .line 566
    goto :goto_0
.end method

.method private setThumbPosition(F)V
    .locals 0
    .param p1, "position"    # F

    #@0
    .prologue
    .line 737
    iput p1, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbPosition:F

    #@2
    .line 738
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->invalidate()V

    #@5
    .line 739
    return-void
.end method

.method private stopDrag(Landroid/view/MotionEvent;)V
    .locals 7
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    const/4 v6, 0x0

    #@1
    const/4 v1, 0x1

    #@2
    const/4 v3, 0x0

    #@3
    .line 683
    iput v3, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchMode:I

    #@5
    .line 687
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getAction()I

    #@8
    move-result v4

    #@9
    if-ne v4, v1, :cond_1

    #@b
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->isEnabled()Z

    #@e
    move-result v4

    #@f
    if-eqz v4, :cond_1

    #@11
    move v0, v1

    #@12
    .line 689
    .local v0, "commitChange":Z
    :goto_0
    if-eqz v0, :cond_5

    #@14
    .line 690
    iget-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@16
    const/16 v5, 0x3e8

    #@18
    invoke-virtual {v4, v5}, Landroid/view/VelocityTracker;->computeCurrentVelocity(I)V

    #@1b
    .line 691
    iget-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@1d
    invoke-virtual {v4}, Landroid/view/VelocityTracker;->getXVelocity()F

    #@20
    move-result v2

    #@21
    .line 692
    .local v2, "xvel":F
    invoke-static {v2}, Ljava/lang/Math;->abs(F)F

    #@24
    move-result v4

    #@25
    iget v5, p0, Landroid/support/v7/widget/SwitchCompat;->mMinFlingVelocity:I

    #@27
    int-to-float v5, v5

    #@28
    cmpl-float v4, v4, v5

    #@2a
    if-lez v4, :cond_4

    #@2c
    .line 693
    invoke-static {p0}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@2f
    move-result v4

    #@30
    if-eqz v4, :cond_3

    #@32
    cmpg-float v4, v2, v6

    #@34
    if-gez v4, :cond_2

    #@36
    .line 701
    .end local v2    # "xvel":F
    .local v1, "newState":Z
    :cond_0
    :goto_1
    invoke-virtual {p0, v1}, Landroid/support/v7/widget/SwitchCompat;->setChecked(Z)V

    #@39
    .line 702
    invoke-direct {p0, p1}, Landroid/support/v7/widget/SwitchCompat;->cancelSuperTouch(Landroid/view/MotionEvent;)V

    #@3c
    .line 703
    return-void

    #@3d
    .end local v0    # "commitChange":Z
    .end local v1    # "newState":Z
    :cond_1
    move v0, v3

    #@3e
    .line 687
    goto :goto_0

    #@3f
    .restart local v0    # "commitChange":Z
    .restart local v2    # "xvel":F
    :cond_2
    move v1, v3

    #@40
    .line 693
    goto :goto_1

    #@41
    :cond_3
    cmpl-float v4, v2, v6

    #@43
    if-gtz v4, :cond_0

    #@45
    move v1, v3

    #@46
    goto :goto_1

    #@47
    .line 695
    :cond_4
    invoke-direct {p0}, Landroid/support/v7/widget/SwitchCompat;->getTargetCheckedState()Z

    #@4a
    move-result v1

    #@4b
    .restart local v1    # "newState":Z
    goto :goto_1

    #@4c
    .line 698
    .end local v1    # "newState":Z
    .end local v2    # "xvel":F
    :cond_5
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->isChecked()Z

    #@4f
    move-result v1

    #@50
    .restart local v1    # "newState":Z
    goto :goto_1
.end method


# virtual methods
.method public draw(Landroid/graphics/Canvas;)V
    .locals 15
    .param p1, "c"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 820
    iget-object v1, p0, Landroid/support/v7/widget/SwitchCompat;->mTempRect:Landroid/graphics/Rect;

    #@2
    .line 821
    .local v1, "padding":Landroid/graphics/Rect;
    iget v3, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchLeft:I

    #@4
    .line 822
    .local v3, "switchLeft":I
    iget v5, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchTop:I

    #@6
    .line 823
    .local v5, "switchTop":I
    iget v4, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchRight:I

    #@8
    .line 824
    .local v4, "switchRight":I
    iget v2, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchBottom:I

    #@a
    .line 826
    .local v2, "switchBottom":I
    invoke-direct {p0}, Landroid/support/v7/widget/SwitchCompat;->getThumbOffset()I

    #@d
    move-result v13

    #@e
    add-int v6, v3, v13

    #@10
    .line 829
    .local v6, "thumbInitialLeft":I
    iget-object v13, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@12
    if-eqz v13, :cond_0

    #@14
    .line 830
    iget-object v13, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@16
    invoke-virtual {v13, v1}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@19
    .line 833
    iget v13, v1, Landroid/graphics/Rect;->left:I

    #@1b
    add-int/2addr v6, v13

    #@1c
    .line 836
    move v10, v3

    #@1d
    .line 837
    .local v10, "trackLeft":I
    move v12, v5

    #@1e
    .line 838
    .local v12, "trackTop":I
    move v11, v4

    #@1f
    .line 839
    .local v11, "trackRight":I
    move v9, v2

    #@20
    .line 840
    .local v9, "trackBottom":I
    iget-object v13, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@22
    invoke-virtual {v13, v10, v12, v11, v9}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@25
    .line 844
    .end local v9    # "trackBottom":I
    .end local v10    # "trackLeft":I
    .end local v11    # "trackRight":I
    .end local v12    # "trackTop":I
    :cond_0
    iget-object v13, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@27
    if-eqz v13, :cond_1

    #@29
    .line 845
    iget-object v13, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@2b
    invoke-virtual {v13, v1}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@2e
    .line 847
    iget v13, v1, Landroid/graphics/Rect;->left:I

    #@30
    sub-int v7, v6, v13

    #@32
    .line 848
    .local v7, "thumbLeft":I
    iget v13, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbWidth:I

    #@34
    add-int/2addr v13, v6

    #@35
    iget v14, v1, Landroid/graphics/Rect;->right:I

    #@37
    add-int v8, v13, v14

    #@39
    .line 849
    .local v8, "thumbRight":I
    iget-object v13, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@3b
    invoke-virtual {v13, v7, v5, v8, v2}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@3e
    .line 851
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getBackground()Landroid/graphics/drawable/Drawable;

    #@41
    move-result-object v0

    #@42
    .line 852
    .local v0, "background":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_1

    #@44
    .line 853
    invoke-static {v0, v7, v5, v8, v2}, Landroid/support/v4/graphics/drawable/DrawableCompat;->setHotspotBounds(Landroid/graphics/drawable/Drawable;IIII)V

    #@47
    .line 859
    .end local v0    # "background":Landroid/graphics/drawable/Drawable;
    .end local v7    # "thumbLeft":I
    .end local v8    # "thumbRight":I
    :cond_1
    invoke-super/range {p0 .. p1}, Landroid/widget/CompoundButton;->draw(Landroid/graphics/Canvas;)V

    #@4a
    .line 860
    return-void
.end method

.method public drawableHotspotChanged(FF)V
    .locals 1
    .param p1, "x"    # F
    .param p2, "y"    # F

    #@0
    .prologue
    .line 993
    invoke-super {p0, p1, p2}, Landroid/widget/CompoundButton;->drawableHotspotChanged(FF)V

    #@3
    .line 995
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 996
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@9
    invoke-static {v0, p1, p2}, Landroid/support/v4/graphics/drawable/DrawableCompat;->setHotspot(Landroid/graphics/drawable/Drawable;FF)V

    #@c
    .line 999
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@e
    if-eqz v0, :cond_1

    #@10
    .line 1000
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@12
    invoke-static {v0, p1, p2}, Landroid/support/v4/graphics/drawable/DrawableCompat;->setHotspot(Landroid/graphics/drawable/Drawable;FF)V

    #@15
    .line 1002
    :cond_1
    return-void
.end method

.method protected drawableStateChanged()V
    .locals 2

    #@0
    .prologue
    .line 976
    invoke-super {p0}, Landroid/widget/CompoundButton;->drawableStateChanged()V

    #@3
    .line 978
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getDrawableState()[I

    #@6
    move-result-object v0

    #@7
    .line 980
    .local v0, "myDrawableState":[I
    iget-object v1, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@9
    if-eqz v1, :cond_0

    #@b
    .line 981
    iget-object v1, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@d
    invoke-virtual {v1, v0}, Landroid/graphics/drawable/Drawable;->setState([I)Z

    #@10
    .line 984
    :cond_0
    iget-object v1, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@12
    if-eqz v1, :cond_1

    #@14
    .line 985
    iget-object v1, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@16
    invoke-virtual {v1, v0}, Landroid/graphics/drawable/Drawable;->setState([I)Z

    #@19
    .line 988
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->invalidate()V

    #@1c
    .line 989
    return-void
.end method

.method public getCompoundPaddingLeft()I
    .locals 3

    #@0
    .prologue
    .line 917
    invoke-static {p0}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@3
    move-result v1

    #@4
    if-nez v1, :cond_1

    #@6
    .line 918
    invoke-super {p0}, Landroid/widget/CompoundButton;->getCompoundPaddingLeft()I

    #@9
    move-result v0

    #@a
    .line 924
    :cond_0
    :goto_0
    return v0

    #@b
    .line 920
    :cond_1
    invoke-super {p0}, Landroid/widget/CompoundButton;->getCompoundPaddingLeft()I

    #@e
    move-result v1

    #@f
    iget v2, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchWidth:I

    #@11
    add-int v0, v1, v2

    #@13
    .line 921
    .local v0, "padding":I
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getText()Ljava/lang/CharSequence;

    #@16
    move-result-object v1

    #@17
    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@1a
    move-result v1

    #@1b
    if-nez v1, :cond_0

    #@1d
    .line 922
    iget v1, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchPadding:I

    #@1f
    add-int/2addr v0, v1

    #@20
    goto :goto_0
.end method

.method public getCompoundPaddingRight()I
    .locals 3

    #@0
    .prologue
    .line 929
    invoke-static {p0}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@3
    move-result v1

    #@4
    if-eqz v1, :cond_1

    #@6
    .line 930
    invoke-super {p0}, Landroid/widget/CompoundButton;->getCompoundPaddingRight()I

    #@9
    move-result v0

    #@a
    .line 936
    :cond_0
    :goto_0
    return v0

    #@b
    .line 932
    :cond_1
    invoke-super {p0}, Landroid/widget/CompoundButton;->getCompoundPaddingRight()I

    #@e
    move-result v1

    #@f
    iget v2, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchWidth:I

    #@11
    add-int v0, v1, v2

    #@13
    .line 933
    .local v0, "padding":I
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getText()Ljava/lang/CharSequence;

    #@16
    move-result-object v1

    #@17
    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@1a
    move-result v1

    #@1b
    if-nez v1, :cond_0

    #@1d
    .line 934
    iget v1, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchPadding:I

    #@1f
    add-int/2addr v0, v1

    #@20
    goto :goto_0
.end method

.method public getShowText()Z
    .locals 1

    #@0
    .prologue
    .line 488
    iget-boolean v0, p0, Landroid/support/v7/widget/SwitchCompat;->mShowText:Z

    #@2
    return v0
.end method

.method public getSplitTrack()Z
    .locals 1

    #@0
    .prologue
    .line 439
    iget-boolean v0, p0, Landroid/support/v7/widget/SwitchCompat;->mSplitTrack:Z

    #@2
    return v0
.end method

.method public getSwitchMinWidth()I
    .locals 1

    #@0
    .prologue
    .line 342
    iget v0, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchMinWidth:I

    #@2
    return v0
.end method

.method public getSwitchPadding()I
    .locals 1

    #@0
    .prologue
    .line 321
    iget v0, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchPadding:I

    #@2
    return v0
.end method

.method public getTextOff()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 461
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTextOff:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public getTextOn()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 446
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTextOn:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public getThumbDrawable()Landroid/graphics/drawable/Drawable;
    .locals 1

    #@0
    .prologue
    .line 420
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    return-object v0
.end method

.method public getThumbTextPadding()I
    .locals 1

    #@0
    .prologue
    .line 361
    iget v0, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbTextPadding:I

    #@2
    return v0
.end method

.method public getTrackDrawable()Landroid/graphics/drawable/Drawable;
    .locals 1

    #@0
    .prologue
    .line 389
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    return-object v0
.end method

.method public jumpDrawablesToCurrentState()V
    .locals 2

    #@0
    .prologue
    .line 1011
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0xb

    #@4
    if-lt v0, v1, :cond_2

    #@6
    .line 1012
    invoke-super {p0}, Landroid/widget/CompoundButton;->jumpDrawablesToCurrentState()V

    #@9
    .line 1014
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@b
    if-eqz v0, :cond_0

    #@d
    .line 1015
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@f
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->jumpToCurrentState()V

    #@12
    .line 1018
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@14
    if-eqz v0, :cond_1

    #@16
    .line 1019
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@18
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->jumpToCurrentState()V

    #@1b
    .line 1022
    :cond_1
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mPositionAnimator:Landroid/view/animation/Animation;

    #@1d
    if-eqz v0, :cond_2

    #@1f
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mPositionAnimator:Landroid/view/animation/Animation;

    #@21
    invoke-virtual {v0}, Landroid/view/animation/Animation;->hasStarted()Z

    #@24
    move-result v0

    #@25
    if-eqz v0, :cond_2

    #@27
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mPositionAnimator:Landroid/view/animation/Animation;

    #@29
    invoke-virtual {v0}, Landroid/view/animation/Animation;->hasEnded()Z

    #@2c
    move-result v0

    #@2d
    if-nez v0, :cond_2

    #@2f
    .line 1024
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->clearAnimation()V

    #@32
    .line 1025
    const/4 v0, 0x0

    #@33
    iput-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mPositionAnimator:Landroid/view/animation/Animation;

    #@35
    .line 1028
    :cond_2
    return-void
.end method

.method protected onCreateDrawableState(I)[I
    .locals 2
    .param p1, "extraSpace"    # I

    #@0
    .prologue
    .line 967
    add-int/lit8 v1, p1, 0x1

    #@2
    invoke-super {p0, v1}, Landroid/widget/CompoundButton;->onCreateDrawableState(I)[I

    #@5
    move-result-object v0

    #@6
    .line 968
    .local v0, "drawableState":[I
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->isChecked()Z

    #@9
    move-result v1

    #@a
    if-eqz v1, :cond_0

    #@c
    .line 969
    sget-object v1, Landroid/support/v7/widget/SwitchCompat;->CHECKED_STATE_SET:[I

    #@e
    invoke-static {v0, v1}, Landroid/support/v7/widget/SwitchCompat;->mergeDrawableStates([I[I)[I

    #@11
    .line 971
    :cond_0
    return-object v0
.end method

.method protected onDraw(Landroid/graphics/Canvas;)V
    .locals 20
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 864
    invoke-super/range {p0 .. p1}, Landroid/widget/CompoundButton;->onDraw(Landroid/graphics/Canvas;)V

    #@3
    .line 866
    move-object/from16 v0, p0

    #@5
    iget-object v7, v0, Landroid/support/v7/widget/SwitchCompat;->mTempRect:Landroid/graphics/Rect;

    #@7
    .line 867
    .local v7, "padding":Landroid/graphics/Rect;
    move-object/from16 v0, p0

    #@9
    iget-object v0, v0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@b
    move-object/from16 v16, v0

    #@d
    .line 868
    .local v16, "trackDrawable":Landroid/graphics/drawable/Drawable;
    if-eqz v16, :cond_4

    #@f
    .line 869
    move-object/from16 v0, v16

    #@11
    invoke-virtual {v0, v7}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@14
    .line 874
    :goto_0
    move-object/from16 v0, p0

    #@16
    iget v13, v0, Landroid/support/v7/widget/SwitchCompat;->mSwitchTop:I

    #@18
    .line 875
    .local v13, "switchTop":I
    move-object/from16 v0, p0

    #@1a
    iget v9, v0, Landroid/support/v7/widget/SwitchCompat;->mSwitchBottom:I

    #@1c
    .line 876
    .local v9, "switchBottom":I
    iget v0, v7, Landroid/graphics/Rect;->top:I

    #@1e
    move/from16 v17, v0

    #@20
    add-int v11, v13, v17

    #@22
    .line 877
    .local v11, "switchInnerTop":I
    iget v0, v7, Landroid/graphics/Rect;->bottom:I

    #@24
    move/from16 v17, v0

    #@26
    sub-int v10, v9, v17

    #@28
    .line 879
    .local v10, "switchInnerBottom":I
    move-object/from16 v0, p0

    #@2a
    iget-object v14, v0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@2c
    .line 880
    .local v14, "thumbDrawable":Landroid/graphics/drawable/Drawable;
    if-eqz v16, :cond_0

    #@2e
    .line 881
    move-object/from16 v0, v16

    #@30
    move-object/from16 v1, p1

    #@32
    invoke-virtual {v0, v1}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@35
    .line 884
    :cond_0
    invoke-virtual/range {p1 .. p1}, Landroid/graphics/Canvas;->save()I

    #@38
    move-result v8

    #@39
    .line 886
    .local v8, "saveCount":I
    if-eqz v14, :cond_1

    #@3b
    .line 887
    move-object/from16 v0, p1

    #@3d
    invoke-virtual {v14, v0}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@40
    .line 890
    :cond_1
    invoke-direct/range {p0 .. p0}, Landroid/support/v7/widget/SwitchCompat;->getTargetCheckedState()Z

    #@43
    move-result v17

    #@44
    if-eqz v17, :cond_5

    #@46
    move-object/from16 v0, p0

    #@48
    iget-object v12, v0, Landroid/support/v7/widget/SwitchCompat;->mOnLayout:Landroid/text/Layout;

    #@4a
    .line 891
    .local v12, "switchText":Landroid/text/Layout;
    :goto_1
    if-eqz v12, :cond_3

    #@4c
    .line 892
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/SwitchCompat;->getDrawableState()[I

    #@4f
    move-result-object v5

    #@50
    .line 893
    .local v5, "drawableState":[I
    move-object/from16 v0, p0

    #@52
    iget-object v0, v0, Landroid/support/v7/widget/SwitchCompat;->mTextColors:Landroid/content/res/ColorStateList;

    #@54
    move-object/from16 v17, v0

    #@56
    if-eqz v17, :cond_2

    #@58
    .line 894
    move-object/from16 v0, p0

    #@5a
    iget-object v0, v0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@5c
    move-object/from16 v17, v0

    #@5e
    move-object/from16 v0, p0

    #@60
    iget-object v0, v0, Landroid/support/v7/widget/SwitchCompat;->mTextColors:Landroid/content/res/ColorStateList;

    #@62
    move-object/from16 v18, v0

    #@64
    const/16 v19, 0x0

    #@66
    move-object/from16 v0, v18

    #@68
    move/from16 v1, v19

    #@6a
    invoke-virtual {v0, v5, v1}, Landroid/content/res/ColorStateList;->getColorForState([II)I

    #@6d
    move-result v18

    #@6e
    invoke-virtual/range {v17 .. v18}, Landroid/text/TextPaint;->setColor(I)V

    #@71
    .line 896
    :cond_2
    move-object/from16 v0, p0

    #@73
    iget-object v0, v0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@75
    move-object/from16 v17, v0

    #@77
    move-object/from16 v0, v17

    #@79
    iput-object v5, v0, Landroid/text/TextPaint;->drawableState:[I

    #@7b
    .line 899
    if-eqz v14, :cond_6

    #@7d
    .line 900
    invoke-virtual {v14}, Landroid/graphics/drawable/Drawable;->getBounds()Landroid/graphics/Rect;

    #@80
    move-result-object v3

    #@81
    .line 901
    .local v3, "bounds":Landroid/graphics/Rect;
    iget v0, v3, Landroid/graphics/Rect;->left:I

    #@83
    move/from16 v17, v0

    #@85
    iget v0, v3, Landroid/graphics/Rect;->right:I

    #@87
    move/from16 v18, v0

    #@89
    add-int v4, v17, v18

    #@8b
    .line 906
    .end local v3    # "bounds":Landroid/graphics/Rect;
    .local v4, "cX":I
    :goto_2
    div-int/lit8 v17, v4, 0x2

    #@8d
    invoke-virtual {v12}, Landroid/text/Layout;->getWidth()I

    #@90
    move-result v18

    #@91
    div-int/lit8 v18, v18, 0x2

    #@93
    sub-int v6, v17, v18

    #@95
    .line 907
    .local v6, "left":I
    add-int v17, v11, v10

    #@97
    div-int/lit8 v17, v17, 0x2

    #@99
    invoke-virtual {v12}, Landroid/text/Layout;->getHeight()I

    #@9c
    move-result v18

    #@9d
    div-int/lit8 v18, v18, 0x2

    #@9f
    sub-int v15, v17, v18

    #@a1
    .line 908
    .local v15, "top":I
    int-to-float v0, v6

    #@a2
    move/from16 v17, v0

    #@a4
    int-to-float v0, v15

    #@a5
    move/from16 v18, v0

    #@a7
    move-object/from16 v0, p1

    #@a9
    move/from16 v1, v17

    #@ab
    move/from16 v2, v18

    #@ad
    invoke-virtual {v0, v1, v2}, Landroid/graphics/Canvas;->translate(FF)V

    #@b0
    .line 909
    move-object/from16 v0, p1

    #@b2
    invoke-virtual {v12, v0}, Landroid/text/Layout;->draw(Landroid/graphics/Canvas;)V

    #@b5
    .line 912
    .end local v4    # "cX":I
    .end local v5    # "drawableState":[I
    .end local v6    # "left":I
    .end local v15    # "top":I
    :cond_3
    move-object/from16 v0, p1

    #@b7
    invoke-virtual {v0, v8}, Landroid/graphics/Canvas;->restoreToCount(I)V

    #@ba
    .line 913
    return-void

    #@bb
    .line 871
    .end local v8    # "saveCount":I
    .end local v9    # "switchBottom":I
    .end local v10    # "switchInnerBottom":I
    .end local v11    # "switchInnerTop":I
    .end local v12    # "switchText":Landroid/text/Layout;
    .end local v13    # "switchTop":I
    .end local v14    # "thumbDrawable":Landroid/graphics/drawable/Drawable;
    :cond_4
    invoke-virtual {v7}, Landroid/graphics/Rect;->setEmpty()V

    #@be
    goto/16 :goto_0

    #@c0
    .line 890
    .restart local v8    # "saveCount":I
    .restart local v9    # "switchBottom":I
    .restart local v10    # "switchInnerBottom":I
    .restart local v11    # "switchInnerTop":I
    .restart local v13    # "switchTop":I
    .restart local v14    # "thumbDrawable":Landroid/graphics/drawable/Drawable;
    :cond_5
    move-object/from16 v0, p0

    #@c2
    iget-object v12, v0, Landroid/support/v7/widget/SwitchCompat;->mOffLayout:Landroid/text/Layout;

    #@c4
    goto :goto_1

    #@c5
    .line 903
    .restart local v5    # "drawableState":[I
    .restart local v12    # "switchText":Landroid/text/Layout;
    :cond_6
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/SwitchCompat;->getWidth()I

    #@c8
    move-result v4

    #@c9
    .restart local v4    # "cX":I
    goto :goto_2
.end method

.method public onInitializeAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)V
    .locals 1
    .param p1, "event"    # Landroid/view/accessibility/AccessibilityEvent;
    .annotation build Landroid/annotation/TargetApi;
        value = 0xe
    .end annotation

    #@0
    .prologue
    .line 1033
    invoke-super {p0, p1}, Landroid/widget/CompoundButton;->onInitializeAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)V

    #@3
    .line 1034
    const-class v0, Landroid/support/v7/widget/SwitchCompat;

    #@5
    invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;

    #@8
    move-result-object v0

    #@9
    invoke-virtual {p1, v0}, Landroid/view/accessibility/AccessibilityEvent;->setClassName(Ljava/lang/CharSequence;)V

    #@c
    .line 1035
    return-void
.end method

.method public onInitializeAccessibilityNodeInfo(Landroid/view/accessibility/AccessibilityNodeInfo;)V
    .locals 5
    .param p1, "info"    # Landroid/view/accessibility/AccessibilityNodeInfo;

    #@0
    .prologue
    .line 1039
    sget v3, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v4, 0xe

    #@4
    if-lt v3, v4, :cond_0

    #@6
    .line 1040
    invoke-super {p0, p1}, Landroid/widget/CompoundButton;->onInitializeAccessibilityNodeInfo(Landroid/view/accessibility/AccessibilityNodeInfo;)V

    #@9
    .line 1041
    const-class v3, Landroid/support/v7/widget/SwitchCompat;

    #@b
    invoke-virtual {v3}, Ljava/lang/Class;->getName()Ljava/lang/String;

    #@e
    move-result-object v3

    #@f
    invoke-virtual {p1, v3}, Landroid/view/accessibility/AccessibilityNodeInfo;->setClassName(Ljava/lang/CharSequence;)V

    #@12
    .line 1042
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->isChecked()Z

    #@15
    move-result v3

    #@16
    if-eqz v3, :cond_1

    #@18
    iget-object v2, p0, Landroid/support/v7/widget/SwitchCompat;->mTextOn:Ljava/lang/CharSequence;

    #@1a
    .line 1043
    .local v2, "switchText":Ljava/lang/CharSequence;
    :goto_0
    invoke-static {v2}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@1d
    move-result v3

    #@1e
    if-nez v3, :cond_0

    #@20
    .line 1044
    invoke-virtual {p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->getText()Ljava/lang/CharSequence;

    #@23
    move-result-object v1

    #@24
    .line 1045
    .local v1, "oldText":Ljava/lang/CharSequence;
    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@27
    move-result v3

    #@28
    if-eqz v3, :cond_2

    #@2a
    .line 1046
    invoke-virtual {p1, v2}, Landroid/view/accessibility/AccessibilityNodeInfo;->setText(Ljava/lang/CharSequence;)V

    #@2d
    .line 1054
    .end local v1    # "oldText":Ljava/lang/CharSequence;
    .end local v2    # "switchText":Ljava/lang/CharSequence;
    :cond_0
    :goto_1
    return-void

    #@2e
    .line 1042
    :cond_1
    iget-object v2, p0, Landroid/support/v7/widget/SwitchCompat;->mTextOff:Ljava/lang/CharSequence;

    #@30
    goto :goto_0

    #@31
    .line 1048
    .restart local v1    # "oldText":Ljava/lang/CharSequence;
    .restart local v2    # "switchText":Ljava/lang/CharSequence;
    :cond_2
    new-instance v0, Ljava/lang/StringBuilder;

    #@33
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    #@36
    .line 1049
    .local v0, "newText":Ljava/lang/StringBuilder;
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/CharSequence;)Ljava/lang/StringBuilder;

    #@39
    move-result-object v3

    #@3a
    const/16 v4, 0x20

    #@3c
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@3f
    move-result-object v3

    #@40
    invoke-virtual {v3, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/CharSequence;)Ljava/lang/StringBuilder;

    #@43
    .line 1050
    invoke-virtual {p1, v0}, Landroid/view/accessibility/AccessibilityNodeInfo;->setText(Ljava/lang/CharSequence;)V

    #@46
    goto :goto_1
.end method

.method protected onLayout(ZIIII)V
    .locals 9
    .param p1, "changed"    # Z
    .param p2, "left"    # I
    .param p3, "top"    # I
    .param p4, "right"    # I
    .param p5, "bottom"    # I

    #@0
    .prologue
    .line 765
    invoke-super/range {p0 .. p5}, Landroid/widget/CompoundButton;->onLayout(ZIIII)V

    #@3
    .line 767
    const/4 v0, 0x0

    #@4
    .line 768
    .local v0, "opticalInsetLeft":I
    const/4 v1, 0x0

    #@5
    .line 769
    .local v1, "opticalInsetRight":I
    iget-object v7, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@7
    if-eqz v7, :cond_0

    #@9
    .line 770
    iget-object v6, p0, Landroid/support/v7/widget/SwitchCompat;->mTempRect:Landroid/graphics/Rect;

    #@b
    .line 771
    .local v6, "trackPadding":Landroid/graphics/Rect;
    iget-object v7, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@d
    if-eqz v7, :cond_1

    #@f
    .line 772
    iget-object v7, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@11
    invoke-virtual {v7, v6}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@14
    .line 777
    :goto_0
    const/4 v0, 0x0

    #@15
    .line 778
    const/4 v1, 0x0

    #@16
    .line 783
    .end local v6    # "trackPadding":Landroid/graphics/Rect;
    :cond_0
    invoke-static {p0}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@19
    move-result v7

    #@1a
    if-eqz v7, :cond_2

    #@1c
    .line 784
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getPaddingLeft()I

    #@1f
    move-result v7

    #@20
    add-int/lit8 v3, v7, 0x0

    #@22
    .line 785
    .local v3, "switchLeft":I
    iget v7, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchWidth:I

    #@24
    add-int/2addr v7, v3

    #@25
    add-int/lit8 v7, v7, 0x0

    #@27
    add-int/lit8 v4, v7, 0x0

    #@29
    .line 793
    .local v4, "switchRight":I
    :goto_1
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getGravity()I

    #@2c
    move-result v7

    #@2d
    and-int/lit8 v7, v7, 0x70

    #@2f
    sparse-switch v7, :sswitch_data_0

    #@32
    .line 796
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getPaddingTop()I

    #@35
    move-result v5

    #@36
    .line 797
    .local v5, "switchTop":I
    iget v7, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchHeight:I

    #@38
    add-int v2, v5, v7

    #@3a
    .line 812
    .local v2, "switchBottom":I
    :goto_2
    iput v3, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchLeft:I

    #@3c
    .line 813
    iput v5, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchTop:I

    #@3e
    .line 814
    iput v2, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchBottom:I

    #@40
    .line 815
    iput v4, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchRight:I

    #@42
    .line 816
    return-void

    #@43
    .line 774
    .end local v2    # "switchBottom":I
    .end local v3    # "switchLeft":I
    .end local v4    # "switchRight":I
    .end local v5    # "switchTop":I
    .restart local v6    # "trackPadding":Landroid/graphics/Rect;
    :cond_1
    invoke-virtual {v6}, Landroid/graphics/Rect;->setEmpty()V

    #@46
    goto :goto_0

    #@47
    .line 787
    .end local v6    # "trackPadding":Landroid/graphics/Rect;
    :cond_2
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getWidth()I

    #@4a
    move-result v7

    #@4b
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getPaddingRight()I

    #@4e
    move-result v8

    #@4f
    sub-int/2addr v7, v8

    #@50
    add-int/lit8 v4, v7, 0x0

    #@52
    .line 788
    .restart local v4    # "switchRight":I
    iget v7, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchWidth:I

    #@54
    sub-int v7, v4, v7

    #@56
    add-int/lit8 v7, v7, 0x0

    #@58
    add-int/lit8 v3, v7, 0x0

    #@5a
    .restart local v3    # "switchLeft":I
    goto :goto_1

    #@5b
    .line 801
    :sswitch_0
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getPaddingTop()I

    #@5e
    move-result v7

    #@5f
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getHeight()I

    #@62
    move-result v8

    #@63
    add-int/2addr v7, v8

    #@64
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getPaddingBottom()I

    #@67
    move-result v8

    #@68
    sub-int/2addr v7, v8

    #@69
    div-int/lit8 v7, v7, 0x2

    #@6b
    iget v8, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchHeight:I

    #@6d
    div-int/lit8 v8, v8, 0x2

    #@6f
    sub-int v5, v7, v8

    #@71
    .line 803
    .restart local v5    # "switchTop":I
    iget v7, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchHeight:I

    #@73
    add-int v2, v5, v7

    #@75
    .line 804
    .restart local v2    # "switchBottom":I
    goto :goto_2

    #@76
    .line 807
    .end local v2    # "switchBottom":I
    .end local v5    # "switchTop":I
    :sswitch_1
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getHeight()I

    #@79
    move-result v7

    #@7a
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getPaddingBottom()I

    #@7d
    move-result v8

    #@7e
    sub-int v2, v7, v8

    #@80
    .line 808
    .restart local v2    # "switchBottom":I
    iget v7, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchHeight:I

    #@82
    sub-int v5, v2, v7

    #@84
    .restart local v5    # "switchTop":I
    goto :goto_2

    #@85
    .line 793
    nop

    #@86
    :sswitch_data_0
    .sparse-switch
        0x10 -> :sswitch_0
        0x50 -> :sswitch_1
    .end sparse-switch
.end method

.method public onMeasure(II)V
    .locals 12
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 493
    iget-boolean v10, p0, Landroid/support/v7/widget/SwitchCompat;->mShowText:Z

    #@2
    if-eqz v10, :cond_1

    #@4
    .line 494
    iget-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mOnLayout:Landroid/text/Layout;

    #@6
    if-nez v10, :cond_0

    #@8
    .line 495
    iget-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mTextOn:Ljava/lang/CharSequence;

    #@a
    invoke-direct {p0, v10}, Landroid/support/v7/widget/SwitchCompat;->makeLayout(Ljava/lang/CharSequence;)Landroid/text/Layout;

    #@d
    move-result-object v10

    #@e
    iput-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mOnLayout:Landroid/text/Layout;

    #@10
    .line 498
    :cond_0
    iget-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mOffLayout:Landroid/text/Layout;

    #@12
    if-nez v10, :cond_1

    #@14
    .line 499
    iget-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mTextOff:Ljava/lang/CharSequence;

    #@16
    invoke-direct {p0, v10}, Landroid/support/v7/widget/SwitchCompat;->makeLayout(Ljava/lang/CharSequence;)Landroid/text/Layout;

    #@19
    move-result-object v10

    #@1a
    iput-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mOffLayout:Landroid/text/Layout;

    #@1c
    .line 503
    :cond_1
    iget-object v2, p0, Landroid/support/v7/widget/SwitchCompat;->mTempRect:Landroid/graphics/Rect;

    #@1e
    .line 506
    .local v2, "padding":Landroid/graphics/Rect;
    iget-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@20
    if-eqz v10, :cond_3

    #@22
    .line 508
    iget-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@24
    invoke-virtual {v10, v2}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@27
    .line 509
    iget-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@29
    invoke-virtual {v10}, Landroid/graphics/drawable/Drawable;->getIntrinsicWidth()I

    #@2c
    move-result v10

    #@2d
    iget v11, v2, Landroid/graphics/Rect;->left:I

    #@2f
    sub-int/2addr v10, v11

    #@30
    iget v11, v2, Landroid/graphics/Rect;->right:I

    #@32
    sub-int v8, v10, v11

    #@34
    .line 510
    .local v8, "thumbWidth":I
    iget-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@36
    invoke-virtual {v10}, Landroid/graphics/drawable/Drawable;->getIntrinsicHeight()I

    #@39
    move-result v7

    #@3a
    .line 517
    .local v7, "thumbHeight":I
    :goto_0
    iget-boolean v10, p0, Landroid/support/v7/widget/SwitchCompat;->mShowText:Z

    #@3c
    if-eqz v10, :cond_4

    #@3e
    .line 518
    iget-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mOnLayout:Landroid/text/Layout;

    #@40
    invoke-virtual {v10}, Landroid/text/Layout;->getWidth()I

    #@43
    move-result v10

    #@44
    iget-object v11, p0, Landroid/support/v7/widget/SwitchCompat;->mOffLayout:Landroid/text/Layout;

    #@46
    invoke-virtual {v11}, Landroid/text/Layout;->getWidth()I

    #@49
    move-result v11

    #@4a
    invoke-static {v10, v11}, Ljava/lang/Math;->max(II)I

    #@4d
    move-result v10

    #@4e
    iget v11, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbTextPadding:I

    #@50
    mul-int/lit8 v11, v11, 0x2

    #@52
    add-int v0, v10, v11

    #@54
    .line 524
    .local v0, "maxTextWidth":I
    :goto_1
    invoke-static {v0, v8}, Ljava/lang/Math;->max(II)I

    #@57
    move-result v10

    #@58
    iput v10, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbWidth:I

    #@5a
    .line 527
    iget-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@5c
    if-eqz v10, :cond_5

    #@5e
    .line 528
    iget-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@60
    invoke-virtual {v10, v2}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@63
    .line 529
    iget-object v10, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@65
    invoke-virtual {v10}, Landroid/graphics/drawable/Drawable;->getIntrinsicHeight()I

    #@68
    move-result v9

    #@69
    .line 537
    .local v9, "trackHeight":I
    :goto_2
    iget v3, v2, Landroid/graphics/Rect;->left:I

    #@6b
    .line 538
    .local v3, "paddingLeft":I
    iget v4, v2, Landroid/graphics/Rect;->right:I

    #@6d
    .line 540
    .local v4, "paddingRight":I
    iget v10, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchMinWidth:I

    #@6f
    iget v11, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbWidth:I

    #@71
    mul-int/lit8 v11, v11, 0x2

    #@73
    add-int/2addr v11, v3

    #@74
    add-int/2addr v11, v4

    #@75
    invoke-static {v10, v11}, Ljava/lang/Math;->max(II)I

    #@78
    move-result v6

    #@79
    .line 542
    .local v6, "switchWidth":I
    invoke-static {v9, v7}, Ljava/lang/Math;->max(II)I

    #@7c
    move-result v5

    #@7d
    .line 543
    .local v5, "switchHeight":I
    iput v6, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchWidth:I

    #@7f
    .line 544
    iput v5, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchHeight:I

    #@81
    .line 546
    invoke-super {p0, p1, p2}, Landroid/widget/CompoundButton;->onMeasure(II)V

    #@84
    .line 548
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getMeasuredHeight()I

    #@87
    move-result v1

    #@88
    .line 549
    .local v1, "measuredHeight":I
    if-ge v1, v5, :cond_2

    #@8a
    .line 550
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getMeasuredWidthAndState(Landroid/view/View;)I

    #@8d
    move-result v10

    #@8e
    invoke-virtual {p0, v10, v5}, Landroid/support/v7/widget/SwitchCompat;->setMeasuredDimension(II)V

    #@91
    .line 552
    :cond_2
    return-void

    #@92
    .line 512
    .end local v0    # "maxTextWidth":I
    .end local v1    # "measuredHeight":I
    .end local v3    # "paddingLeft":I
    .end local v4    # "paddingRight":I
    .end local v5    # "switchHeight":I
    .end local v6    # "switchWidth":I
    .end local v7    # "thumbHeight":I
    .end local v8    # "thumbWidth":I
    .end local v9    # "trackHeight":I
    :cond_3
    const/4 v8, 0x0

    #@93
    .line 513
    .restart local v8    # "thumbWidth":I
    const/4 v7, 0x0

    #@94
    .restart local v7    # "thumbHeight":I
    goto :goto_0

    #@95
    .line 521
    :cond_4
    const/4 v0, 0x0

    #@96
    .restart local v0    # "maxTextWidth":I
    goto :goto_1

    #@97
    .line 531
    :cond_5
    invoke-virtual {v2}, Landroid/graphics/Rect;->setEmpty()V

    #@9a
    .line 532
    const/4 v9, 0x0

    #@9b
    .restart local v9    # "trackHeight":I
    goto :goto_2
.end method

.method public onPopulateAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)V
    .locals 2
    .param p1, "event"    # Landroid/view/accessibility/AccessibilityEvent;
    .annotation build Landroid/annotation/TargetApi;
        value = 0xe
    .end annotation

    #@0
    .prologue
    .line 557
    invoke-super {p0, p1}, Landroid/widget/CompoundButton;->onPopulateAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)V

    #@3
    .line 559
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->isChecked()Z

    #@6
    move-result v1

    #@7
    if-eqz v1, :cond_1

    #@9
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTextOn:Ljava/lang/CharSequence;

    #@b
    .line 560
    .local v0, "text":Ljava/lang/CharSequence;
    :goto_0
    if-eqz v0, :cond_0

    #@d
    .line 561
    invoke-virtual {p1}, Landroid/view/accessibility/AccessibilityEvent;->getText()Ljava/util/List;

    #@10
    move-result-object v1

    #@11
    invoke-interface {v1, v0}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    #@14
    .line 563
    :cond_0
    return-void

    #@15
    .line 559
    .end local v0    # "text":Ljava/lang/CharSequence;
    :cond_1
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTextOff:Ljava/lang/CharSequence;

    #@17
    goto :goto_0
.end method

.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 12
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    const/4 v11, 0x2

    #@1
    const/high16 v7, 0x3f800000    # 1.0f

    #@3
    const/4 v10, 0x0

    #@4
    const/4 v8, 0x1

    #@5
    .line 593
    iget-object v9, p0, Landroid/support/v7/widget/SwitchCompat;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@7
    invoke-virtual {v9, p1}, Landroid/view/VelocityTracker;->addMovement(Landroid/view/MotionEvent;)V

    #@a
    .line 594
    invoke-static {p1}, Landroid/support/v4/view/MotionEventCompat;->getActionMasked(Landroid/view/MotionEvent;)I

    #@d
    move-result v0

    #@e
    .line 595
    .local v0, "action":I
    packed-switch v0, :pswitch_data_0

    #@11
    .line 667
    :cond_0
    :goto_0
    :pswitch_0
    invoke-super {p0, p1}, Landroid/widget/CompoundButton;->onTouchEvent(Landroid/view/MotionEvent;)Z

    #@14
    move-result v7

    #@15
    :goto_1
    return v7

    #@16
    .line 597
    :pswitch_1
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@19
    move-result v5

    #@1a
    .line 598
    .local v5, "x":F
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@1d
    move-result v6

    #@1e
    .line 599
    .local v6, "y":F
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->isEnabled()Z

    #@21
    move-result v7

    #@22
    if-eqz v7, :cond_0

    #@24
    invoke-direct {p0, v5, v6}, Landroid/support/v7/widget/SwitchCompat;->hitThumb(FF)Z

    #@27
    move-result v7

    #@28
    if-eqz v7, :cond_0

    #@2a
    .line 600
    iput v8, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchMode:I

    #@2c
    .line 601
    iput v5, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchX:F

    #@2e
    .line 602
    iput v6, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchY:F

    #@30
    goto :goto_0

    #@31
    .line 608
    .end local v5    # "x":F
    .end local v6    # "y":F
    :pswitch_2
    iget v9, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchMode:I

    #@33
    packed-switch v9, :pswitch_data_1

    #@36
    goto :goto_0

    #@37
    .line 614
    :pswitch_3
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@3a
    move-result v5

    #@3b
    .line 615
    .restart local v5    # "x":F
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@3e
    move-result v6

    #@3f
    .line 616
    .restart local v6    # "y":F
    iget v7, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchX:F

    #@41
    sub-float v7, v5, v7

    #@43
    invoke-static {v7}, Ljava/lang/Math;->abs(F)F

    #@46
    move-result v7

    #@47
    iget v9, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchSlop:I

    #@49
    int-to-float v9, v9

    #@4a
    cmpl-float v7, v7, v9

    #@4c
    if-gtz v7, :cond_1

    #@4e
    iget v7, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchY:F

    #@50
    sub-float v7, v6, v7

    #@52
    invoke-static {v7}, Ljava/lang/Math;->abs(F)F

    #@55
    move-result v7

    #@56
    iget v9, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchSlop:I

    #@58
    int-to-float v9, v9

    #@59
    cmpl-float v7, v7, v9

    #@5b
    if-lez v7, :cond_0

    #@5d
    .line 618
    :cond_1
    iput v11, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchMode:I

    #@5f
    .line 619
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getParent()Landroid/view/ViewParent;

    #@62
    move-result-object v7

    #@63
    invoke-interface {v7, v8}, Landroid/view/ViewParent;->requestDisallowInterceptTouchEvent(Z)V

    #@66
    .line 620
    iput v5, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchX:F

    #@68
    .line 621
    iput v6, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchY:F

    #@6a
    move v7, v8

    #@6b
    .line 622
    goto :goto_1

    #@6c
    .line 628
    .end local v5    # "x":F
    .end local v6    # "y":F
    :pswitch_4
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@6f
    move-result v5

    #@70
    .line 629
    .restart local v5    # "x":F
    invoke-direct {p0}, Landroid/support/v7/widget/SwitchCompat;->getThumbScrollRange()I

    #@73
    move-result v4

    #@74
    .line 630
    .local v4, "thumbScrollRange":I
    iget v9, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchX:F

    #@76
    sub-float v3, v5, v9

    #@78
    .line 632
    .local v3, "thumbScrollOffset":F
    if-eqz v4, :cond_4

    #@7a
    .line 633
    int-to-float v9, v4

    #@7b
    div-float v1, v3, v9

    #@7d
    .line 639
    .local v1, "dPos":F
    :goto_2
    invoke-static {p0}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@80
    move-result v9

    #@81
    if-eqz v9, :cond_2

    #@83
    .line 640
    neg-float v1, v1

    #@84
    .line 642
    :cond_2
    iget v9, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbPosition:F

    #@86
    add-float/2addr v9, v1

    #@87
    invoke-static {v9, v10, v7}, Landroid/support/v7/widget/SwitchCompat;->constrain(FFF)F

    #@8a
    move-result v2

    #@8b
    .line 643
    .local v2, "newPos":F
    iget v7, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbPosition:F

    #@8d
    cmpl-float v7, v2, v7

    #@8f
    if-eqz v7, :cond_3

    #@91
    .line 644
    iput v5, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchX:F

    #@93
    .line 645
    invoke-direct {p0, v2}, Landroid/support/v7/widget/SwitchCompat;->setThumbPosition(F)V

    #@96
    :cond_3
    move v7, v8

    #@97
    .line 647
    goto/16 :goto_1

    #@99
    .line 637
    .end local v1    # "dPos":F
    .end local v2    # "newPos":F
    :cond_4
    cmpl-float v9, v3, v10

    #@9b
    if-lez v9, :cond_5

    #@9d
    move v1, v7

    #@9e
    .restart local v1    # "dPos":F
    :goto_3
    goto :goto_2

    #@9f
    .end local v1    # "dPos":F
    :cond_5
    const/high16 v1, -0x40800000    # -1.0f

    #@a1
    goto :goto_3

    #@a2
    .line 655
    .end local v3    # "thumbScrollOffset":F
    .end local v4    # "thumbScrollRange":I
    .end local v5    # "x":F
    :pswitch_5
    iget v7, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchMode:I

    #@a4
    if-ne v7, v11, :cond_6

    #@a6
    .line 656
    invoke-direct {p0, p1}, Landroid/support/v7/widget/SwitchCompat;->stopDrag(Landroid/view/MotionEvent;)V

    #@a9
    .line 658
    invoke-super {p0, p1}, Landroid/widget/CompoundButton;->onTouchEvent(Landroid/view/MotionEvent;)Z

    #@ac
    move v7, v8

    #@ad
    .line 659
    goto/16 :goto_1

    #@af
    .line 661
    :cond_6
    const/4 v7, 0x0

    #@b0
    iput v7, p0, Landroid/support/v7/widget/SwitchCompat;->mTouchMode:I

    #@b2
    .line 662
    iget-object v7, p0, Landroid/support/v7/widget/SwitchCompat;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@b4
    invoke-virtual {v7}, Landroid/view/VelocityTracker;->clear()V

    #@b7
    goto/16 :goto_0

    #@b9
    .line 595
    nop

    #@ba
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_5
        :pswitch_2
        :pswitch_5
    .end packed-switch

    #@c6
    .line 608
    :pswitch_data_1
    .packed-switch 0x0
        :pswitch_0
        :pswitch_3
        :pswitch_4
    .end packed-switch
.end method

.method public setChecked(Z)V
    .locals 1
    .param p1, "checked"    # Z

    #@0
    .prologue
    .line 748
    invoke-super {p0, p1}, Landroid/widget/CompoundButton;->setChecked(Z)V

    #@3
    .line 752
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->isChecked()Z

    #@6
    move-result p1

    #@7
    .line 754
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getWindowToken()Landroid/os/IBinder;

    #@a
    move-result-object v0

    #@b
    if-eqz v0, :cond_0

    #@d
    .line 755
    invoke-direct {p0, p1}, Landroid/support/v7/widget/SwitchCompat;->animateThumbToCheckedState(Z)V

    #@10
    .line 761
    :goto_0
    return-void

    #@11
    .line 758
    :cond_0
    invoke-direct {p0}, Landroid/support/v7/widget/SwitchCompat;->cancelPositionAnimator()V

    #@14
    .line 759
    if-eqz p1, :cond_1

    #@16
    const/high16 v0, 0x3f800000    # 1.0f

    #@18
    :goto_1
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SwitchCompat;->setThumbPosition(F)V

    #@1b
    goto :goto_0

    #@1c
    :cond_1
    const/4 v0, 0x0

    #@1d
    goto :goto_1
.end method

.method public setShowText(Z)V
    .locals 1
    .param p1, "showText"    # Z

    #@0
    .prologue
    .line 478
    iget-boolean v0, p0, Landroid/support/v7/widget/SwitchCompat;->mShowText:Z

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 479
    iput-boolean p1, p0, Landroid/support/v7/widget/SwitchCompat;->mShowText:Z

    #@6
    .line 480
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->requestLayout()V

    #@9
    .line 482
    :cond_0
    return-void
.end method

.method public setSplitTrack(Z)V
    .locals 0
    .param p1, "splitTrack"    # Z

    #@0
    .prologue
    .line 431
    iput-boolean p1, p0, Landroid/support/v7/widget/SwitchCompat;->mSplitTrack:Z

    #@2
    .line 432
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->invalidate()V

    #@5
    .line 433
    return-void
.end method

.method public setSwitchMinWidth(I)V
    .locals 0
    .param p1, "pixels"    # I

    #@0
    .prologue
    .line 331
    iput p1, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchMinWidth:I

    #@2
    .line 332
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->requestLayout()V

    #@5
    .line 333
    return-void
.end method

.method public setSwitchPadding(I)V
    .locals 0
    .param p1, "pixels"    # I

    #@0
    .prologue
    .line 311
    iput p1, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchPadding:I

    #@2
    .line 312
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->requestLayout()V

    #@5
    .line 313
    return-void
.end method

.method public setSwitchTextAppearance(Landroid/content/Context;I)V
    .locals 7
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "resid"    # I

    #@0
    .prologue
    const/4 v6, 0x0

    #@1
    .line 231
    sget-object v4, Landroid/support/v7/widget/SwitchCompat;->TEXT_APPEARANCE_ATTRS:[I

    #@3
    invoke-virtual {p1, p2, v4}, Landroid/content/Context;->obtainStyledAttributes(I[I)Landroid/content/res/TypedArray;

    #@6
    move-result-object v1

    #@7
    .line 236
    .local v1, "appearance":Landroid/content/res/TypedArray;
    invoke-virtual {v1, v6}, Landroid/content/res/TypedArray;->getColorStateList(I)Landroid/content/res/ColorStateList;

    #@a
    move-result-object v2

    #@b
    .line 237
    .local v2, "colors":Landroid/content/res/ColorStateList;
    if-eqz v2, :cond_1

    #@d
    .line 238
    iput-object v2, p0, Landroid/support/v7/widget/SwitchCompat;->mTextColors:Landroid/content/res/ColorStateList;

    #@f
    .line 244
    :goto_0
    const/4 v4, 0x1

    #@10
    invoke-virtual {v1, v4, v6}, Landroid/content/res/TypedArray;->getDimensionPixelSize(II)I

    #@13
    move-result v3

    #@14
    .line 245
    .local v3, "ts":I
    if-eqz v3, :cond_0

    #@16
    .line 246
    int-to-float v4, v3

    #@17
    iget-object v5, p0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@19
    invoke-virtual {v5}, Landroid/text/TextPaint;->getTextSize()F

    #@1c
    move-result v5

    #@1d
    cmpl-float v4, v4, v5

    #@1f
    if-eqz v4, :cond_0

    #@21
    .line 247
    iget-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@23
    int-to-float v5, v3

    #@24
    invoke-virtual {v4, v5}, Landroid/text/TextPaint;->setTextSize(F)V

    #@27
    .line 248
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->requestLayout()V

    #@2a
    .line 252
    :cond_0
    const/4 v4, 0x2

    #@2b
    invoke-virtual {v1, v4, v6}, Landroid/content/res/TypedArray;->getBoolean(IZ)Z

    #@2e
    move-result v0

    #@2f
    .line 253
    .local v0, "allCaps":Z
    if-eqz v0, :cond_2

    #@31
    .line 254
    new-instance v4, Landroid/support/v7/internal/text/AllCapsTransformationMethod;

    #@33
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getContext()Landroid/content/Context;

    #@36
    move-result-object v5

    #@37
    invoke-direct {v4, v5}, Landroid/support/v7/internal/text/AllCapsTransformationMethod;-><init>(Landroid/content/Context;)V

    #@3a
    iput-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchTransformationMethod:Landroid/text/method/TransformationMethod;

    #@3c
    .line 259
    :goto_1
    invoke-virtual {v1}, Landroid/content/res/TypedArray;->recycle()V

    #@3f
    .line 260
    return-void

    #@40
    .line 241
    .end local v0    # "allCaps":Z
    .end local v3    # "ts":I
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->getTextColors()Landroid/content/res/ColorStateList;

    #@43
    move-result-object v4

    #@44
    iput-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mTextColors:Landroid/content/res/ColorStateList;

    #@46
    goto :goto_0

    #@47
    .line 256
    .restart local v0    # "allCaps":Z
    .restart local v3    # "ts":I
    :cond_2
    const/4 v4, 0x0

    #@48
    iput-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mSwitchTransformationMethod:Landroid/text/method/TransformationMethod;

    #@4a
    goto :goto_1
.end method

.method public setSwitchTypeface(Landroid/graphics/Typeface;)V
    .locals 1
    .param p1, "tf"    # Landroid/graphics/Typeface;

    #@0
    .prologue
    .line 297
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@2
    invoke-virtual {v0}, Landroid/text/TextPaint;->getTypeface()Landroid/graphics/Typeface;

    #@5
    move-result-object v0

    #@6
    if-eq v0, p1, :cond_0

    #@8
    .line 298
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@a
    invoke-virtual {v0, p1}, Landroid/text/TextPaint;->setTypeface(Landroid/graphics/Typeface;)Landroid/graphics/Typeface;

    #@d
    .line 300
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->requestLayout()V

    #@10
    .line 301
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->invalidate()V

    #@13
    .line 303
    :cond_0
    return-void
.end method

.method public setSwitchTypeface(Landroid/graphics/Typeface;I)V
    .locals 6
    .param p1, "tf"    # Landroid/graphics/Typeface;
    .param p2, "style"    # I

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    const/4 v2, 0x0

    #@2
    .line 269
    if-lez p2, :cond_4

    #@4
    .line 270
    if-nez p1, :cond_1

    #@6
    .line 271
    invoke-static {p2}, Landroid/graphics/Typeface;->defaultFromStyle(I)Landroid/graphics/Typeface;

    #@9
    move-result-object p1

    #@a
    .line 276
    :goto_0
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/SwitchCompat;->setSwitchTypeface(Landroid/graphics/Typeface;)V

    #@d
    .line 278
    if-eqz p1, :cond_2

    #@f
    invoke-virtual {p1}, Landroid/graphics/Typeface;->getStyle()I

    #@12
    move-result v1

    #@13
    .line 279
    .local v1, "typefaceStyle":I
    :goto_1
    xor-int/lit8 v4, v1, -0x1

    #@15
    and-int v0, p2, v4

    #@17
    .line 280
    .local v0, "need":I
    iget-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@19
    and-int/lit8 v5, v0, 0x1

    #@1b
    if-eqz v5, :cond_0

    #@1d
    const/4 v2, 0x1

    #@1e
    :cond_0
    invoke-virtual {v4, v2}, Landroid/text/TextPaint;->setFakeBoldText(Z)V

    #@21
    .line 281
    iget-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@23
    and-int/lit8 v2, v0, 0x2

    #@25
    if-eqz v2, :cond_3

    #@27
    const/high16 v2, -0x41800000    # -0.25f

    #@29
    :goto_2
    invoke-virtual {v4, v2}, Landroid/text/TextPaint;->setTextSkewX(F)V

    #@2c
    .line 287
    .end local v0    # "need":I
    .end local v1    # "typefaceStyle":I
    :goto_3
    return-void

    #@2d
    .line 273
    :cond_1
    invoke-static {p1, p2}, Landroid/graphics/Typeface;->create(Landroid/graphics/Typeface;I)Landroid/graphics/Typeface;

    #@30
    move-result-object p1

    #@31
    goto :goto_0

    #@32
    :cond_2
    move v1, v2

    #@33
    .line 278
    goto :goto_1

    #@34
    .restart local v0    # "need":I
    .restart local v1    # "typefaceStyle":I
    :cond_3
    move v2, v3

    #@35
    .line 281
    goto :goto_2

    #@36
    .line 283
    .end local v0    # "need":I
    .end local v1    # "typefaceStyle":I
    :cond_4
    iget-object v4, p0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@38
    invoke-virtual {v4, v2}, Landroid/text/TextPaint;->setFakeBoldText(Z)V

    #@3b
    .line 284
    iget-object v2, p0, Landroid/support/v7/widget/SwitchCompat;->mTextPaint:Landroid/text/TextPaint;

    #@3d
    invoke-virtual {v2, v3}, Landroid/text/TextPaint;->setTextSkewX(F)V

    #@40
    .line 285
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/SwitchCompat;->setSwitchTypeface(Landroid/graphics/Typeface;)V

    #@43
    goto :goto_3
.end method

.method public setTextOff(Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "textOff"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 468
    iput-object p1, p0, Landroid/support/v7/widget/SwitchCompat;->mTextOff:Ljava/lang/CharSequence;

    #@2
    .line 469
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->requestLayout()V

    #@5
    .line 470
    return-void
.end method

.method public setTextOn(Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "textOn"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 453
    iput-object p1, p0, Landroid/support/v7/widget/SwitchCompat;->mTextOn:Ljava/lang/CharSequence;

    #@2
    .line 454
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->requestLayout()V

    #@5
    .line 455
    return-void
.end method

.method public setThumbDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 0
    .param p1, "thumb"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 399
    iput-object p1, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    .line 400
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->requestLayout()V

    #@5
    .line 401
    return-void
.end method

.method public setThumbResource(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 410
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/SwitchCompat;->setThumbDrawable(Landroid/graphics/drawable/Drawable;)V

    #@9
    .line 411
    return-void
.end method

.method public setThumbTextPadding(I)V
    .locals 0
    .param p1, "pixels"    # I

    #@0
    .prologue
    .line 351
    iput p1, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbTextPadding:I

    #@2
    .line 352
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->requestLayout()V

    #@5
    .line 353
    return-void
.end method

.method public setTrackDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 0
    .param p1, "track"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 370
    iput-object p1, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    .line 371
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->requestLayout()V

    #@5
    .line 372
    return-void
.end method

.method public setTrackResource(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 380
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/SwitchCompat;->setTrackDrawable(Landroid/graphics/drawable/Drawable;)V

    #@9
    .line 381
    return-void
.end method

.method public toggle()V
    .locals 1

    #@0
    .prologue
    .line 743
    invoke-virtual {p0}, Landroid/support/v7/widget/SwitchCompat;->isChecked()Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/SwitchCompat;->setChecked(Z)V

    #@a
    .line 744
    return-void

    #@b
    .line 743
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method protected verifyDrawable(Landroid/graphics/drawable/Drawable;)Z
    .locals 1
    .param p1, "who"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 1006
    invoke-super {p0, p1}, Landroid/widget/CompoundButton;->verifyDrawable(Landroid/graphics/drawable/Drawable;)Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mThumbDrawable:Landroid/graphics/drawable/Drawable;

    #@8
    if-eq p1, v0, :cond_0

    #@a
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat;->mTrackDrawable:Landroid/graphics/drawable/Drawable;

    #@c
    if-ne p1, v0, :cond_1

    #@e
    :cond_0
    const/4 v0, 0x1

    #@f
    :goto_0
    return v0

    #@10
    :cond_1
    const/4 v0, 0x0

    #@11
    goto :goto_0
.end method
