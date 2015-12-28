.class public Landroid/support/v4/view/PagerTitleStrip;
.super Landroid/view/ViewGroup;
.source "PagerTitleStrip.java"

# interfaces
.implements Landroid/support/v4/view/ViewPager$Decor;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/view/PagerTitleStrip$1;,
        Landroid/support/v4/view/PagerTitleStrip$PageListener;,
        Landroid/support/v4/view/PagerTitleStrip$PagerTitleStripImplIcs;,
        Landroid/support/v4/view/PagerTitleStrip$PagerTitleStripImplBase;,
        Landroid/support/v4/view/PagerTitleStrip$PagerTitleStripImpl;
    }
.end annotation


# static fields
.field private static final ATTRS:[I

.field private static final IMPL:Landroid/support/v4/view/PagerTitleStrip$PagerTitleStripImpl;

.field private static final SIDE_ALPHA:F = 0.6f

.field private static final TAG:Ljava/lang/String; = "PagerTitleStrip"

.field private static final TEXT_ATTRS:[I

.field private static final TEXT_SPACING:I = 0x10


# instance fields
.field mCurrText:Landroid/widget/TextView;

.field private mGravity:I

.field private mLastKnownCurrentPage:I

.field private mLastKnownPositionOffset:F

.field mNextText:Landroid/widget/TextView;

.field private mNonPrimaryAlpha:I

.field private final mPageListener:Landroid/support/v4/view/PagerTitleStrip$PageListener;

.field mPager:Landroid/support/v4/view/ViewPager;

.field mPrevText:Landroid/widget/TextView;

.field private mScaledTextSpacing:I

.field mTextColor:I

.field private mUpdatingPositions:Z

.field private mUpdatingText:Z

.field private mWatchingAdapter:Ljava/lang/ref/WeakReference;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/lang/ref/WeakReference",
            "<",
            "Landroid/support/v4/view/PagerAdapter;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method static constructor <clinit>()V
    .locals 3

    #@0
    .prologue
    .line 65
    const/4 v0, 0x4

    #@1
    new-array v0, v0, [I

    #@3
    fill-array-data v0, :array_0

    #@6
    sput-object v0, Landroid/support/v4/view/PagerTitleStrip;->ATTRS:[I

    #@8
    .line 72
    const/4 v0, 0x1

    #@9
    new-array v0, v0, [I

    #@b
    const/4 v1, 0x0

    #@c
    const v2, 0x101038c

    #@f
    aput v2, v0, v1

    #@11
    sput-object v0, Landroid/support/v4/view/PagerTitleStrip;->TEXT_ATTRS:[I

    #@13
    .line 100
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@15
    const/16 v1, 0xe

    #@17
    if-lt v0, v1, :cond_0

    #@19
    .line 101
    new-instance v0, Landroid/support/v4/view/PagerTitleStrip$PagerTitleStripImplIcs;

    #@1b
    invoke-direct {v0}, Landroid/support/v4/view/PagerTitleStrip$PagerTitleStripImplIcs;-><init>()V

    #@1e
    sput-object v0, Landroid/support/v4/view/PagerTitleStrip;->IMPL:Landroid/support/v4/view/PagerTitleStrip$PagerTitleStripImpl;

    #@20
    .line 105
    :goto_0
    return-void

    #@21
    .line 103
    :cond_0
    new-instance v0, Landroid/support/v4/view/PagerTitleStrip$PagerTitleStripImplBase;

    #@23
    invoke-direct {v0}, Landroid/support/v4/view/PagerTitleStrip$PagerTitleStripImplBase;-><init>()V

    #@26
    sput-object v0, Landroid/support/v4/view/PagerTitleStrip;->IMPL:Landroid/support/v4/view/PagerTitleStrip$PagerTitleStripImpl;

    #@28
    goto :goto_0

    #@29
    .line 65
    nop

    #@2a
    :array_0
    .array-data 4
        0x1010034
        0x1010095
        0x1010098
        0x10100af
    .end array-data
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 112
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v4/view/PagerTitleStrip;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 113
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 11
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    const/4 v10, 0x2

    #@1
    const/4 v9, 0x0

    #@2
    .line 116
    invoke-direct {p0, p1, p2}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@5
    .line 53
    const/4 v7, -0x1

    #@6
    iput v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mLastKnownCurrentPage:I

    #@8
    .line 54
    const/high16 v7, -0x40800000    # -1.0f

    #@a
    iput v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mLastKnownPositionOffset:F

    #@c
    .line 61
    new-instance v7, Landroid/support/v4/view/PagerTitleStrip$PageListener;

    #@e
    const/4 v8, 0x0

    #@f
    invoke-direct {v7, p0, v8}, Landroid/support/v4/view/PagerTitleStrip$PageListener;-><init>(Landroid/support/v4/view/PagerTitleStrip;Landroid/support/v4/view/PagerTitleStrip$1;)V

    #@12
    iput-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mPageListener:Landroid/support/v4/view/PagerTitleStrip$PageListener;

    #@14
    .line 118
    new-instance v7, Landroid/widget/TextView;

    #@16
    invoke-direct {v7, p1}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    #@19
    iput-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@1b
    invoke-virtual {p0, v7}, Landroid/support/v4/view/PagerTitleStrip;->addView(Landroid/view/View;)V

    #@1e
    .line 119
    new-instance v7, Landroid/widget/TextView;

    #@20
    invoke-direct {v7, p1}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    #@23
    iput-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@25
    invoke-virtual {p0, v7}, Landroid/support/v4/view/PagerTitleStrip;->addView(Landroid/view/View;)V

    #@28
    .line 120
    new-instance v7, Landroid/widget/TextView;

    #@2a
    invoke-direct {v7, p1}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    #@2d
    iput-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@2f
    invoke-virtual {p0, v7}, Landroid/support/v4/view/PagerTitleStrip;->addView(Landroid/view/View;)V

    #@32
    .line 122
    sget-object v7, Landroid/support/v4/view/PagerTitleStrip;->ATTRS:[I

    #@34
    invoke-virtual {p1, p2, v7}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;

    #@37
    move-result-object v0

    #@38
    .line 123
    .local v0, "a":Landroid/content/res/TypedArray;
    invoke-virtual {v0, v9, v9}, Landroid/content/res/TypedArray;->getResourceId(II)I

    #@3b
    move-result v4

    #@3c
    .line 124
    .local v4, "textAppearance":I
    if-eqz v4, :cond_0

    #@3e
    .line 125
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@40
    invoke-virtual {v7, p1, v4}, Landroid/widget/TextView;->setTextAppearance(Landroid/content/Context;I)V

    #@43
    .line 126
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@45
    invoke-virtual {v7, p1, v4}, Landroid/widget/TextView;->setTextAppearance(Landroid/content/Context;I)V

    #@48
    .line 127
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@4a
    invoke-virtual {v7, p1, v4}, Landroid/widget/TextView;->setTextAppearance(Landroid/content/Context;I)V

    #@4d
    .line 129
    :cond_0
    const/4 v7, 0x1

    #@4e
    invoke-virtual {v0, v7, v9}, Landroid/content/res/TypedArray;->getDimensionPixelSize(II)I

    #@51
    move-result v6

    #@52
    .line 130
    .local v6, "textSize":I
    if-eqz v6, :cond_1

    #@54
    .line 131
    int-to-float v7, v6

    #@55
    invoke-virtual {p0, v9, v7}, Landroid/support/v4/view/PagerTitleStrip;->setTextSize(IF)V

    #@58
    .line 133
    :cond_1
    invoke-virtual {v0, v10}, Landroid/content/res/TypedArray;->hasValue(I)Z

    #@5b
    move-result v7

    #@5c
    if-eqz v7, :cond_2

    #@5e
    .line 134
    invoke-virtual {v0, v10, v9}, Landroid/content/res/TypedArray;->getColor(II)I

    #@61
    move-result v5

    #@62
    .line 135
    .local v5, "textColor":I
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@64
    invoke-virtual {v7, v5}, Landroid/widget/TextView;->setTextColor(I)V

    #@67
    .line 136
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@69
    invoke-virtual {v7, v5}, Landroid/widget/TextView;->setTextColor(I)V

    #@6c
    .line 137
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@6e
    invoke-virtual {v7, v5}, Landroid/widget/TextView;->setTextColor(I)V

    #@71
    .line 139
    .end local v5    # "textColor":I
    :cond_2
    const/4 v7, 0x3

    #@72
    const/16 v8, 0x50

    #@74
    invoke-virtual {v0, v7, v8}, Landroid/content/res/TypedArray;->getInteger(II)I

    #@77
    move-result v7

    #@78
    iput v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mGravity:I

    #@7a
    .line 140
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@7d
    .line 142
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@7f
    invoke-virtual {v7}, Landroid/widget/TextView;->getTextColors()Landroid/content/res/ColorStateList;

    #@82
    move-result-object v7

    #@83
    invoke-virtual {v7}, Landroid/content/res/ColorStateList;->getDefaultColor()I

    #@86
    move-result v7

    #@87
    iput v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mTextColor:I

    #@89
    .line 143
    const v7, 0x3f19999a    # 0.6f

    #@8c
    invoke-virtual {p0, v7}, Landroid/support/v4/view/PagerTitleStrip;->setNonPrimaryAlpha(F)V

    #@8f
    .line 145
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@91
    sget-object v8, Landroid/text/TextUtils$TruncateAt;->END:Landroid/text/TextUtils$TruncateAt;

    #@93
    invoke-virtual {v7, v8}, Landroid/widget/TextView;->setEllipsize(Landroid/text/TextUtils$TruncateAt;)V

    #@96
    .line 146
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@98
    sget-object v8, Landroid/text/TextUtils$TruncateAt;->END:Landroid/text/TextUtils$TruncateAt;

    #@9a
    invoke-virtual {v7, v8}, Landroid/widget/TextView;->setEllipsize(Landroid/text/TextUtils$TruncateAt;)V

    #@9d
    .line 147
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@9f
    sget-object v8, Landroid/text/TextUtils$TruncateAt;->END:Landroid/text/TextUtils$TruncateAt;

    #@a1
    invoke-virtual {v7, v8}, Landroid/widget/TextView;->setEllipsize(Landroid/text/TextUtils$TruncateAt;)V

    #@a4
    .line 149
    const/4 v1, 0x0

    #@a5
    .line 150
    .local v1, "allCaps":Z
    if-eqz v4, :cond_3

    #@a7
    .line 151
    sget-object v7, Landroid/support/v4/view/PagerTitleStrip;->TEXT_ATTRS:[I

    #@a9
    invoke-virtual {p1, v4, v7}, Landroid/content/Context;->obtainStyledAttributes(I[I)Landroid/content/res/TypedArray;

    #@ac
    move-result-object v3

    #@ad
    .line 152
    .local v3, "ta":Landroid/content/res/TypedArray;
    invoke-virtual {v3, v9, v9}, Landroid/content/res/TypedArray;->getBoolean(IZ)Z

    #@b0
    move-result v1

    #@b1
    .line 153
    invoke-virtual {v3}, Landroid/content/res/TypedArray;->recycle()V

    #@b4
    .line 156
    .end local v3    # "ta":Landroid/content/res/TypedArray;
    :cond_3
    if-eqz v1, :cond_4

    #@b6
    .line 157
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@b8
    invoke-static {v7}, Landroid/support/v4/view/PagerTitleStrip;->setSingleLineAllCaps(Landroid/widget/TextView;)V

    #@bb
    .line 158
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@bd
    invoke-static {v7}, Landroid/support/v4/view/PagerTitleStrip;->setSingleLineAllCaps(Landroid/widget/TextView;)V

    #@c0
    .line 159
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@c2
    invoke-static {v7}, Landroid/support/v4/view/PagerTitleStrip;->setSingleLineAllCaps(Landroid/widget/TextView;)V

    #@c5
    .line 166
    :goto_0
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@c8
    move-result-object v7

    #@c9
    invoke-virtual {v7}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@cc
    move-result-object v7

    #@cd
    iget v2, v7, Landroid/util/DisplayMetrics;->density:F

    #@cf
    .line 167
    .local v2, "density":F
    const/high16 v7, 0x41800000    # 16.0f

    #@d1
    mul-float/2addr v7, v2

    #@d2
    float-to-int v7, v7

    #@d3
    iput v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mScaledTextSpacing:I

    #@d5
    .line 168
    return-void

    #@d6
    .line 161
    .end local v2    # "density":F
    :cond_4
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@d8
    invoke-virtual {v7}, Landroid/widget/TextView;->setSingleLine()V

    #@db
    .line 162
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@dd
    invoke-virtual {v7}, Landroid/widget/TextView;->setSingleLine()V

    #@e0
    .line 163
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@e2
    invoke-virtual {v7}, Landroid/widget/TextView;->setSingleLine()V

    #@e5
    goto :goto_0
.end method

.method static synthetic access$100(Landroid/support/v4/view/PagerTitleStrip;)F
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/view/PagerTitleStrip;

    #@0
    .prologue
    .line 45
    iget v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mLastKnownPositionOffset:F

    #@2
    return v0
.end method

.method private static setSingleLineAllCaps(Landroid/widget/TextView;)V
    .locals 1
    .param p0, "text"    # Landroid/widget/TextView;

    #@0
    .prologue
    .line 108
    sget-object v0, Landroid/support/v4/view/PagerTitleStrip;->IMPL:Landroid/support/v4/view/PagerTitleStrip$PagerTitleStripImpl;

    #@2
    invoke-interface {v0, p0}, Landroid/support/v4/view/PagerTitleStrip$PagerTitleStripImpl;->setSingleLineAllCaps(Landroid/widget/TextView;)V

    #@5
    .line 109
    return-void
.end method


# virtual methods
.method getMinHeight()I
    .locals 2

    #@0
    .prologue
    .line 462
    const/4 v1, 0x0

    #@1
    .line 463
    .local v1, "minHeight":I
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->getBackground()Landroid/graphics/drawable/Drawable;

    #@4
    move-result-object v0

    #@5
    .line 464
    .local v0, "bg":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_0

    #@7
    .line 465
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->getIntrinsicHeight()I

    #@a
    move-result v1

    #@b
    .line 467
    :cond_0
    return v1
.end method

.method public getTextSpacing()I
    .locals 1

    #@0
    .prologue
    .line 184
    iget v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mScaledTextSpacing:I

    #@2
    return v0
.end method

.method protected onAttachedToWindow()V
    .locals 5

    #@0
    .prologue
    .line 242
    invoke-super {p0}, Landroid/view/ViewGroup;->onAttachedToWindow()V

    #@3
    .line 244
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->getParent()Landroid/view/ViewParent;

    #@6
    move-result-object v2

    #@7
    .line 245
    .local v2, "parent":Landroid/view/ViewParent;
    instance-of v3, v2, Landroid/support/v4/view/ViewPager;

    #@9
    if-nez v3, :cond_0

    #@b
    .line 246
    new-instance v3, Ljava/lang/IllegalStateException;

    #@d
    const-string v4, "PagerTitleStrip must be a direct child of a ViewPager."

    #@f
    invoke-direct {v3, v4}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@12
    throw v3

    #@13
    :cond_0
    move-object v1, v2

    #@14
    .line 250
    check-cast v1, Landroid/support/v4/view/ViewPager;

    #@16
    .line 251
    .local v1, "pager":Landroid/support/v4/view/ViewPager;
    invoke-virtual {v1}, Landroid/support/v4/view/ViewPager;->getAdapter()Landroid/support/v4/view/PagerAdapter;

    #@19
    move-result-object v0

    #@1a
    .line 253
    .local v0, "adapter":Landroid/support/v4/view/PagerAdapter;
    iget-object v3, p0, Landroid/support/v4/view/PagerTitleStrip;->mPageListener:Landroid/support/v4/view/PagerTitleStrip$PageListener;

    #@1c
    invoke-virtual {v1, v3}, Landroid/support/v4/view/ViewPager;->setInternalPageChangeListener(Landroid/support/v4/view/ViewPager$OnPageChangeListener;)Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@1f
    .line 254
    iget-object v3, p0, Landroid/support/v4/view/PagerTitleStrip;->mPageListener:Landroid/support/v4/view/PagerTitleStrip$PageListener;

    #@21
    invoke-virtual {v1, v3}, Landroid/support/v4/view/ViewPager;->setOnAdapterChangeListener(Landroid/support/v4/view/ViewPager$OnAdapterChangeListener;)V

    #@24
    .line 255
    iput-object v1, p0, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@26
    .line 256
    iget-object v3, p0, Landroid/support/v4/view/PagerTitleStrip;->mWatchingAdapter:Ljava/lang/ref/WeakReference;

    #@28
    if-eqz v3, :cond_1

    #@2a
    iget-object v3, p0, Landroid/support/v4/view/PagerTitleStrip;->mWatchingAdapter:Ljava/lang/ref/WeakReference;

    #@2c
    invoke-virtual {v3}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    #@2f
    move-result-object v3

    #@30
    check-cast v3, Landroid/support/v4/view/PagerAdapter;

    #@32
    :goto_0
    invoke-virtual {p0, v3, v0}, Landroid/support/v4/view/PagerTitleStrip;->updateAdapter(Landroid/support/v4/view/PagerAdapter;Landroid/support/v4/view/PagerAdapter;)V

    #@35
    .line 257
    return-void

    #@36
    .line 256
    :cond_1
    const/4 v3, 0x0

    #@37
    goto :goto_0
.end method

.method protected onDetachedFromWindow()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 261
    invoke-super {p0}, Landroid/view/ViewGroup;->onDetachedFromWindow()V

    #@4
    .line 262
    iget-object v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 263
    iget-object v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@a
    invoke-virtual {v0}, Landroid/support/v4/view/ViewPager;->getAdapter()Landroid/support/v4/view/PagerAdapter;

    #@d
    move-result-object v0

    #@e
    invoke-virtual {p0, v0, v1}, Landroid/support/v4/view/PagerTitleStrip;->updateAdapter(Landroid/support/v4/view/PagerAdapter;Landroid/support/v4/view/PagerAdapter;)V

    #@11
    .line 264
    iget-object v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@13
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ViewPager;->setInternalPageChangeListener(Landroid/support/v4/view/ViewPager$OnPageChangeListener;)Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@16
    .line 265
    iget-object v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@18
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ViewPager;->setOnAdapterChangeListener(Landroid/support/v4/view/ViewPager$OnAdapterChangeListener;)V

    #@1b
    .line 266
    iput-object v1, p0, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@1d
    .line 268
    :cond_0
    return-void
.end method

.method protected onLayout(ZIIII)V
    .locals 3
    .param p1, "changed"    # Z
    .param p2, "l"    # I
    .param p3, "t"    # I
    .param p4, "r"    # I
    .param p5, "b"    # I

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 455
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@3
    if-eqz v1, :cond_1

    #@5
    .line 456
    iget v1, p0, Landroid/support/v4/view/PagerTitleStrip;->mLastKnownPositionOffset:F

    #@7
    cmpl-float v1, v1, v0

    #@9
    if-ltz v1, :cond_0

    #@b
    iget v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mLastKnownPositionOffset:F

    #@d
    .line 457
    .local v0, "offset":F
    :cond_0
    iget v1, p0, Landroid/support/v4/view/PagerTitleStrip;->mLastKnownCurrentPage:I

    #@f
    const/4 v2, 0x1

    #@10
    invoke-virtual {p0, v1, v0, v2}, Landroid/support/v4/view/PagerTitleStrip;->updateTextPositions(IFZ)V

    #@13
    .line 459
    .end local v0    # "offset":F
    :cond_1
    return-void
.end method

.method protected onMeasure(II)V
    .locals 12
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 422
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@3
    move-result v8

    #@4
    .line 423
    .local v8, "widthMode":I
    invoke-static {p2}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@7
    move-result v3

    #@8
    .line 424
    .local v3, "heightMode":I
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@b
    move-result v9

    #@c
    .line 425
    .local v9, "widthSize":I
    invoke-static {p2}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@f
    move-result v4

    #@10
    .line 427
    .local v4, "heightSize":I
    const/high16 v10, 0x40000000    # 2.0f

    #@12
    if-eq v8, v10, :cond_0

    #@14
    .line 428
    new-instance v10, Ljava/lang/IllegalStateException;

    #@16
    const-string v11, "Must measure with an exact width"

    #@18
    invoke-direct {v10, v11}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@1b
    throw v10

    #@1c
    .line 431
    :cond_0
    move v0, v4

    #@1d
    .line 432
    .local v0, "childHeight":I
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->getMinHeight()I

    #@20
    move-result v5

    #@21
    .line 433
    .local v5, "minHeight":I
    const/4 v6, 0x0

    #@22
    .line 434
    .local v6, "padding":I
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->getPaddingTop()I

    #@25
    move-result v10

    #@26
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->getPaddingBottom()I

    #@29
    move-result v11

    #@2a
    add-int v6, v10, v11

    #@2c
    .line 435
    sub-int/2addr v0, v6

    #@2d
    .line 437
    int-to-float v10, v9

    #@2e
    const v11, 0x3f4ccccd    # 0.8f

    #@31
    mul-float/2addr v10, v11

    #@32
    float-to-int v10, v10

    #@33
    const/high16 v11, -0x80000000

    #@35
    invoke-static {v10, v11}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@38
    move-result v2

    #@39
    .line 439
    .local v2, "childWidthSpec":I
    const/high16 v10, -0x80000000

    #@3b
    invoke-static {v0, v10}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@3e
    move-result v1

    #@3f
    .line 441
    .local v1, "childHeightSpec":I
    iget-object v10, p0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@41
    invoke-virtual {v10, v2, v1}, Landroid/widget/TextView;->measure(II)V

    #@44
    .line 442
    iget-object v10, p0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@46
    invoke-virtual {v10, v2, v1}, Landroid/widget/TextView;->measure(II)V

    #@49
    .line 443
    iget-object v10, p0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@4b
    invoke-virtual {v10, v2, v1}, Landroid/widget/TextView;->measure(II)V

    #@4e
    .line 445
    const/high16 v10, 0x40000000    # 2.0f

    #@50
    if-ne v3, v10, :cond_1

    #@52
    .line 446
    invoke-virtual {p0, v9, v4}, Landroid/support/v4/view/PagerTitleStrip;->setMeasuredDimension(II)V

    #@55
    .line 451
    :goto_0
    return-void

    #@56
    .line 448
    :cond_1
    iget-object v10, p0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@58
    invoke-virtual {v10}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@5b
    move-result v7

    #@5c
    .line 449
    .local v7, "textHeight":I
    add-int v10, v7, v6

    #@5e
    invoke-static {v5, v10}, Ljava/lang/Math;->max(II)I

    #@61
    move-result v10

    #@62
    invoke-virtual {p0, v9, v10}, Landroid/support/v4/view/PagerTitleStrip;->setMeasuredDimension(II)V

    #@65
    goto :goto_0
.end method

.method public requestLayout()V
    .locals 1

    #@0
    .prologue
    .line 310
    iget-boolean v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mUpdatingText:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 311
    invoke-super {p0}, Landroid/view/ViewGroup;->requestLayout()V

    #@7
    .line 313
    :cond_0
    return-void
.end method

.method public setGravity(I)V
    .locals 0
    .param p1, "gravity"    # I

    #@0
    .prologue
    .line 236
    iput p1, p0, Landroid/support/v4/view/PagerTitleStrip;->mGravity:I

    #@2
    .line 237
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->requestLayout()V

    #@5
    .line 238
    return-void
.end method

.method public setNonPrimaryAlpha(F)V
    .locals 4
    .param p1, "alpha"    # F

    #@0
    .prologue
    .line 193
    const/high16 v1, 0x437f0000    # 255.0f

    #@2
    mul-float/2addr v1, p1

    #@3
    float-to-int v1, v1

    #@4
    and-int/lit16 v1, v1, 0xff

    #@6
    iput v1, p0, Landroid/support/v4/view/PagerTitleStrip;->mNonPrimaryAlpha:I

    #@8
    .line 194
    iget v1, p0, Landroid/support/v4/view/PagerTitleStrip;->mNonPrimaryAlpha:I

    #@a
    shl-int/lit8 v1, v1, 0x18

    #@c
    iget v2, p0, Landroid/support/v4/view/PagerTitleStrip;->mTextColor:I

    #@e
    const v3, 0xffffff

    #@11
    and-int/2addr v2, v3

    #@12
    or-int v0, v1, v2

    #@14
    .line 195
    .local v0, "transparentColor":I
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@16
    invoke-virtual {v1, v0}, Landroid/widget/TextView;->setTextColor(I)V

    #@19
    .line 196
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@1b
    invoke-virtual {v1, v0}, Landroid/widget/TextView;->setTextColor(I)V

    #@1e
    .line 197
    return-void
.end method

.method public setTextColor(I)V
    .locals 4
    .param p1, "color"    # I

    #@0
    .prologue
    .line 206
    iput p1, p0, Landroid/support/v4/view/PagerTitleStrip;->mTextColor:I

    #@2
    .line 207
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@4
    invoke-virtual {v1, p1}, Landroid/widget/TextView;->setTextColor(I)V

    #@7
    .line 208
    iget v1, p0, Landroid/support/v4/view/PagerTitleStrip;->mNonPrimaryAlpha:I

    #@9
    shl-int/lit8 v1, v1, 0x18

    #@b
    iget v2, p0, Landroid/support/v4/view/PagerTitleStrip;->mTextColor:I

    #@d
    const v3, 0xffffff

    #@10
    and-int/2addr v2, v3

    #@11
    or-int v0, v1, v2

    #@13
    .line 209
    .local v0, "transparentColor":I
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@15
    invoke-virtual {v1, v0}, Landroid/widget/TextView;->setTextColor(I)V

    #@18
    .line 210
    iget-object v1, p0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@1a
    invoke-virtual {v1, v0}, Landroid/widget/TextView;->setTextColor(I)V

    #@1d
    .line 211
    return-void
.end method

.method public setTextSize(IF)V
    .locals 1
    .param p1, "unit"    # I
    .param p2, "size"    # F

    #@0
    .prologue
    .line 224
    iget-object v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/widget/TextView;->setTextSize(IF)V

    #@5
    .line 225
    iget-object v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@7
    invoke-virtual {v0, p1, p2}, Landroid/widget/TextView;->setTextSize(IF)V

    #@a
    .line 226
    iget-object v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@c
    invoke-virtual {v0, p1, p2}, Landroid/widget/TextView;->setTextSize(IF)V

    #@f
    .line 227
    return-void
.end method

.method public setTextSpacing(I)V
    .locals 0
    .param p1, "spacingPixels"    # I

    #@0
    .prologue
    .line 176
    iput p1, p0, Landroid/support/v4/view/PagerTitleStrip;->mScaledTextSpacing:I

    #@2
    .line 177
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->requestLayout()V

    #@5
    .line 178
    return-void
.end method

.method updateAdapter(Landroid/support/v4/view/PagerAdapter;Landroid/support/v4/view/PagerAdapter;)V
    .locals 1
    .param p1, "oldAdapter"    # Landroid/support/v4/view/PagerAdapter;
    .param p2, "newAdapter"    # Landroid/support/v4/view/PagerAdapter;

    #@0
    .prologue
    .line 316
    if-eqz p1, :cond_0

    #@2
    .line 317
    iget-object v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mPageListener:Landroid/support/v4/view/PagerTitleStrip$PageListener;

    #@4
    invoke-virtual {p1, v0}, Landroid/support/v4/view/PagerAdapter;->unregisterDataSetObserver(Landroid/database/DataSetObserver;)V

    #@7
    .line 318
    const/4 v0, 0x0

    #@8
    iput-object v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mWatchingAdapter:Ljava/lang/ref/WeakReference;

    #@a
    .line 320
    :cond_0
    if-eqz p2, :cond_1

    #@c
    .line 321
    iget-object v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mPageListener:Landroid/support/v4/view/PagerTitleStrip$PageListener;

    #@e
    invoke-virtual {p2, v0}, Landroid/support/v4/view/PagerAdapter;->registerDataSetObserver(Landroid/database/DataSetObserver;)V

    #@11
    .line 322
    new-instance v0, Ljava/lang/ref/WeakReference;

    #@13
    invoke-direct {v0, p2}, Ljava/lang/ref/WeakReference;-><init>(Ljava/lang/Object;)V

    #@16
    iput-object v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mWatchingAdapter:Ljava/lang/ref/WeakReference;

    #@18
    .line 324
    :cond_1
    iget-object v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@1a
    if-eqz v0, :cond_2

    #@1c
    .line 325
    const/4 v0, -0x1

    #@1d
    iput v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mLastKnownCurrentPage:I

    #@1f
    .line 326
    const/high16 v0, -0x40800000    # -1.0f

    #@21
    iput v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mLastKnownPositionOffset:F

    #@23
    .line 327
    iget-object v0, p0, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@25
    invoke-virtual {v0}, Landroid/support/v4/view/ViewPager;->getCurrentItem()I

    #@28
    move-result v0

    #@29
    invoke-virtual {p0, v0, p2}, Landroid/support/v4/view/PagerTitleStrip;->updateText(ILandroid/support/v4/view/PagerAdapter;)V

    #@2c
    .line 328
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->requestLayout()V

    #@2f
    .line 330
    :cond_2
    return-void
.end method

.method updateText(ILandroid/support/v4/view/PagerAdapter;)V
    .locals 10
    .param p1, "currentItem"    # I
    .param p2, "adapter"    # Landroid/support/v4/view/PagerAdapter;

    #@0
    .prologue
    const/4 v7, 0x1

    #@1
    const/high16 v9, -0x80000000

    #@3
    const/4 v6, 0x0

    #@4
    .line 271
    if-eqz p2, :cond_3

    #@6
    invoke-virtual {p2}, Landroid/support/v4/view/PagerAdapter;->getCount()I

    #@9
    move-result v3

    #@a
    .line 272
    .local v3, "itemCount":I
    :goto_0
    iput-boolean v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mUpdatingText:Z

    #@c
    .line 274
    const/4 v4, 0x0

    #@d
    .line 275
    .local v4, "text":Ljava/lang/CharSequence;
    if-lt p1, v7, :cond_0

    #@f
    if-eqz p2, :cond_0

    #@11
    .line 276
    add-int/lit8 v7, p1, -0x1

    #@13
    invoke-virtual {p2, v7}, Landroid/support/v4/view/PagerAdapter;->getPageTitle(I)Ljava/lang/CharSequence;

    #@16
    move-result-object v4

    #@17
    .line 278
    :cond_0
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@19
    invoke-virtual {v7, v4}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@1c
    .line 280
    iget-object v8, p0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@1e
    if-eqz p2, :cond_4

    #@20
    if-ge p1, v3, :cond_4

    #@22
    invoke-virtual {p2, p1}, Landroid/support/v4/view/PagerAdapter;->getPageTitle(I)Ljava/lang/CharSequence;

    #@25
    move-result-object v7

    #@26
    :goto_1
    invoke-virtual {v8, v7}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@29
    .line 283
    const/4 v4, 0x0

    #@2a
    .line 284
    add-int/lit8 v7, p1, 0x1

    #@2c
    if-ge v7, v3, :cond_1

    #@2e
    if-eqz p2, :cond_1

    #@30
    .line 285
    add-int/lit8 v7, p1, 0x1

    #@32
    invoke-virtual {p2, v7}, Landroid/support/v4/view/PagerAdapter;->getPageTitle(I)Ljava/lang/CharSequence;

    #@35
    move-result-object v4

    #@36
    .line 287
    :cond_1
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@38
    invoke-virtual {v7, v4}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@3b
    .line 290
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->getWidth()I

    #@3e
    move-result v7

    #@3f
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->getPaddingLeft()I

    #@42
    move-result v8

    #@43
    sub-int/2addr v7, v8

    #@44
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->getPaddingRight()I

    #@47
    move-result v8

    #@48
    sub-int v5, v7, v8

    #@4a
    .line 291
    .local v5, "width":I
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->getHeight()I

    #@4d
    move-result v7

    #@4e
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->getPaddingTop()I

    #@51
    move-result v8

    #@52
    sub-int/2addr v7, v8

    #@53
    invoke-virtual {p0}, Landroid/support/v4/view/PagerTitleStrip;->getPaddingBottom()I

    #@56
    move-result v8

    #@57
    sub-int v0, v7, v8

    #@59
    .line 292
    .local v0, "childHeight":I
    int-to-float v7, v5

    #@5a
    const v8, 0x3f4ccccd    # 0.8f

    #@5d
    mul-float/2addr v7, v8

    #@5e
    float-to-int v7, v7

    #@5f
    invoke-static {v7, v9}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@62
    move-result v2

    #@63
    .line 294
    .local v2, "childWidthSpec":I
    invoke-static {v0, v9}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@66
    move-result v1

    #@67
    .line 295
    .local v1, "childHeightSpec":I
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@69
    invoke-virtual {v7, v2, v1}, Landroid/widget/TextView;->measure(II)V

    #@6c
    .line 296
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@6e
    invoke-virtual {v7, v2, v1}, Landroid/widget/TextView;->measure(II)V

    #@71
    .line 297
    iget-object v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@73
    invoke-virtual {v7, v2, v1}, Landroid/widget/TextView;->measure(II)V

    #@76
    .line 299
    iput p1, p0, Landroid/support/v4/view/PagerTitleStrip;->mLastKnownCurrentPage:I

    #@78
    .line 301
    iget-boolean v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mUpdatingPositions:Z

    #@7a
    if-nez v7, :cond_2

    #@7c
    .line 302
    iget v7, p0, Landroid/support/v4/view/PagerTitleStrip;->mLastKnownPositionOffset:F

    #@7e
    invoke-virtual {p0, p1, v7, v6}, Landroid/support/v4/view/PagerTitleStrip;->updateTextPositions(IFZ)V

    #@81
    .line 305
    :cond_2
    iput-boolean v6, p0, Landroid/support/v4/view/PagerTitleStrip;->mUpdatingText:Z

    #@83
    .line 306
    return-void

    #@84
    .end local v0    # "childHeight":I
    .end local v1    # "childHeightSpec":I
    .end local v2    # "childWidthSpec":I
    .end local v3    # "itemCount":I
    .end local v4    # "text":Ljava/lang/CharSequence;
    .end local v5    # "width":I
    :cond_3
    move v3, v6

    #@85
    .line 271
    goto :goto_0

    #@86
    .line 280
    .restart local v3    # "itemCount":I
    .restart local v4    # "text":Ljava/lang/CharSequence;
    :cond_4
    const/4 v7, 0x0

    #@87
    goto :goto_1
.end method

.method updateTextPositions(IFZ)V
    .locals 45
    .param p1, "position"    # I
    .param p2, "positionOffset"    # F
    .param p3, "force"    # Z

    #@0
    .prologue
    .line 333
    move-object/from16 v0, p0

    #@2
    iget v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mLastKnownCurrentPage:I

    #@4
    move/from16 v42, v0

    #@6
    move/from16 v0, p1

    #@8
    move/from16 v1, v42

    #@a
    if-eq v0, v1, :cond_2

    #@c
    .line 334
    move-object/from16 v0, p0

    #@e
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mPager:Landroid/support/v4/view/ViewPager;

    #@10
    move-object/from16 v42, v0

    #@12
    invoke-virtual/range {v42 .. v42}, Landroid/support/v4/view/ViewPager;->getAdapter()Landroid/support/v4/view/PagerAdapter;

    #@15
    move-result-object v42

    #@16
    move-object/from16 v0, p0

    #@18
    move/from16 v1, p1

    #@1a
    move-object/from16 v2, v42

    #@1c
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/view/PagerTitleStrip;->updateText(ILandroid/support/v4/view/PagerAdapter;)V

    #@1f
    .line 339
    :cond_0
    const/16 v42, 0x1

    #@21
    move/from16 v0, v42

    #@23
    move-object/from16 v1, p0

    #@25
    iput-boolean v0, v1, Landroid/support/v4/view/PagerTitleStrip;->mUpdatingPositions:Z

    #@27
    .line 341
    move-object/from16 v0, p0

    #@29
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@2b
    move-object/from16 v42, v0

    #@2d
    invoke-virtual/range {v42 .. v42}, Landroid/widget/TextView;->getMeasuredWidth()I

    #@30
    move-result v36

    #@31
    .line 342
    .local v36, "prevWidth":I
    move-object/from16 v0, p0

    #@33
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@35
    move-object/from16 v42, v0

    #@37
    invoke-virtual/range {v42 .. v42}, Landroid/widget/TextView;->getMeasuredWidth()I

    #@3a
    move-result v18

    #@3b
    .line 343
    .local v18, "currWidth":I
    move-object/from16 v0, p0

    #@3d
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@3f
    move-object/from16 v42, v0

    #@41
    invoke-virtual/range {v42 .. v42}, Landroid/widget/TextView;->getMeasuredWidth()I

    #@44
    move-result v26

    #@45
    .line 344
    .local v26, "nextWidth":I
    div-int/lit8 v19, v18, 0x2

    #@47
    .line 346
    .local v19, "halfCurrWidth":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/PagerTitleStrip;->getWidth()I

    #@4a
    move-result v38

    #@4b
    .line 347
    .local v38, "stripWidth":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/PagerTitleStrip;->getHeight()I

    #@4e
    move-result v37

    #@4f
    .line 348
    .local v37, "stripHeight":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/PagerTitleStrip;->getPaddingLeft()I

    #@52
    move-result v29

    #@53
    .line 349
    .local v29, "paddingLeft":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/PagerTitleStrip;->getPaddingRight()I

    #@56
    move-result v30

    #@57
    .line 350
    .local v30, "paddingRight":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/PagerTitleStrip;->getPaddingTop()I

    #@5a
    move-result v31

    #@5b
    .line 351
    .local v31, "paddingTop":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/PagerTitleStrip;->getPaddingBottom()I

    #@5e
    move-result v28

    #@5f
    .line 352
    .local v28, "paddingBottom":I
    add-int v39, v29, v19

    #@61
    .line 353
    .local v39, "textPaddedLeft":I
    add-int v40, v30, v19

    #@63
    .line 354
    .local v40, "textPaddedRight":I
    sub-int v42, v38, v39

    #@65
    sub-int v10, v42, v40

    #@67
    .line 356
    .local v10, "contentWidth":I
    const/high16 v42, 0x3f000000    # 0.5f

    #@69
    add-float v14, p2, v42

    #@6b
    .line 357
    .local v14, "currOffset":F
    const/high16 v42, 0x3f800000    # 1.0f

    #@6d
    cmpl-float v42, v14, v42

    #@6f
    if-lez v42, :cond_1

    #@71
    .line 358
    const/high16 v42, 0x3f800000    # 1.0f

    #@73
    sub-float v14, v14, v42

    #@75
    .line 360
    :cond_1
    sub-int v42, v38, v40

    #@77
    int-to-float v0, v10

    #@78
    move/from16 v43, v0

    #@7a
    mul-float v43, v43, v14

    #@7c
    move/from16 v0, v43

    #@7e
    float-to-int v0, v0

    #@7f
    move/from16 v43, v0

    #@81
    sub-int v12, v42, v43

    #@83
    .line 361
    .local v12, "currCenter":I
    div-int/lit8 v42, v18, 0x2

    #@85
    sub-int v13, v12, v42

    #@87
    .line 362
    .local v13, "currLeft":I
    add-int v15, v13, v18

    #@89
    .line 364
    .local v15, "currRight":I
    move-object/from16 v0, p0

    #@8b
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@8d
    move-object/from16 v42, v0

    #@8f
    invoke-virtual/range {v42 .. v42}, Landroid/widget/TextView;->getBaseline()I

    #@92
    move-result v32

    #@93
    .line 365
    .local v32, "prevBaseline":I
    move-object/from16 v0, p0

    #@95
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@97
    move-object/from16 v42, v0

    #@99
    invoke-virtual/range {v42 .. v42}, Landroid/widget/TextView;->getBaseline()I

    #@9c
    move-result v11

    #@9d
    .line 366
    .local v11, "currBaseline":I
    move-object/from16 v0, p0

    #@9f
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@a1
    move-object/from16 v42, v0

    #@a3
    invoke-virtual/range {v42 .. v42}, Landroid/widget/TextView;->getBaseline()I

    #@a6
    move-result v22

    #@a7
    .line 367
    .local v22, "nextBaseline":I
    move/from16 v0, v32

    #@a9
    invoke-static {v0, v11}, Ljava/lang/Math;->max(II)I

    #@ac
    move-result v42

    #@ad
    move/from16 v0, v42

    #@af
    move/from16 v1, v22

    #@b1
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@b4
    move-result v20

    #@b5
    .line 368
    .local v20, "maxBaseline":I
    sub-int v35, v20, v32

    #@b7
    .line 369
    .local v35, "prevTopOffset":I
    sub-int v17, v20, v11

    #@b9
    .line 370
    .local v17, "currTopOffset":I
    sub-int v25, v20, v22

    #@bb
    .line 371
    .local v25, "nextTopOffset":I
    move-object/from16 v0, p0

    #@bd
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@bf
    move-object/from16 v42, v0

    #@c1
    invoke-virtual/range {v42 .. v42}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@c4
    move-result v42

    #@c5
    add-int v7, v35, v42

    #@c7
    .line 372
    .local v7, "alignedPrevHeight":I
    move-object/from16 v0, p0

    #@c9
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@cb
    move-object/from16 v42, v0

    #@cd
    invoke-virtual/range {v42 .. v42}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@d0
    move-result v42

    #@d1
    add-int v5, v17, v42

    #@d3
    .line 373
    .local v5, "alignedCurrHeight":I
    move-object/from16 v0, p0

    #@d5
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@d7
    move-object/from16 v42, v0

    #@d9
    invoke-virtual/range {v42 .. v42}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@dc
    move-result v42

    #@dd
    add-int v6, v25, v42

    #@df
    .line 374
    .local v6, "alignedNextHeight":I
    invoke-static {v7, v5}, Ljava/lang/Math;->max(II)I

    #@e2
    move-result v42

    #@e3
    move/from16 v0, v42

    #@e5
    invoke-static {v0, v6}, Ljava/lang/Math;->max(II)I

    #@e8
    move-result v21

    #@e9
    .line 377
    .local v21, "maxTextHeight":I
    move-object/from16 v0, p0

    #@eb
    iget v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mGravity:I

    #@ed
    move/from16 v42, v0

    #@ef
    and-int/lit8 v41, v42, 0x70

    #@f1
    .line 382
    .local v41, "vgrav":I
    sparse-switch v41, :sswitch_data_0

    #@f4
    .line 385
    add-int v34, v31, v35

    #@f6
    .line 386
    .local v34, "prevTop":I
    add-int v16, v31, v17

    #@f8
    .line 387
    .local v16, "currTop":I
    add-int v24, v31, v25

    #@fa
    .line 404
    .local v24, "nextTop":I
    :goto_0
    move-object/from16 v0, p0

    #@fc
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@fe
    move-object/from16 v42, v0

    #@100
    move-object/from16 v0, p0

    #@102
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mCurrText:Landroid/widget/TextView;

    #@104
    move-object/from16 v43, v0

    #@106
    invoke-virtual/range {v43 .. v43}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@109
    move-result v43

    #@10a
    add-int v43, v43, v16

    #@10c
    move-object/from16 v0, v42

    #@10e
    move/from16 v1, v16

    #@110
    move/from16 v2, v43

    #@112
    invoke-virtual {v0, v13, v1, v15, v2}, Landroid/widget/TextView;->layout(IIII)V

    #@115
    .line 407
    move-object/from16 v0, p0

    #@117
    iget v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mScaledTextSpacing:I

    #@119
    move/from16 v42, v0

    #@11b
    sub-int v42, v13, v42

    #@11d
    sub-int v42, v42, v36

    #@11f
    move/from16 v0, v29

    #@121
    move/from16 v1, v42

    #@123
    invoke-static {v0, v1}, Ljava/lang/Math;->min(II)I

    #@126
    move-result v33

    #@127
    .line 408
    .local v33, "prevLeft":I
    move-object/from16 v0, p0

    #@129
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@12b
    move-object/from16 v42, v0

    #@12d
    add-int v43, v33, v36

    #@12f
    move-object/from16 v0, p0

    #@131
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mPrevText:Landroid/widget/TextView;

    #@133
    move-object/from16 v44, v0

    #@135
    invoke-virtual/range {v44 .. v44}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@138
    move-result v44

    #@139
    add-int v44, v44, v34

    #@13b
    move-object/from16 v0, v42

    #@13d
    move/from16 v1, v33

    #@13f
    move/from16 v2, v34

    #@141
    move/from16 v3, v43

    #@143
    move/from16 v4, v44

    #@145
    invoke-virtual {v0, v1, v2, v3, v4}, Landroid/widget/TextView;->layout(IIII)V

    #@148
    .line 411
    sub-int v42, v38, v30

    #@14a
    sub-int v42, v42, v26

    #@14c
    move-object/from16 v0, p0

    #@14e
    iget v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mScaledTextSpacing:I

    #@150
    move/from16 v43, v0

    #@152
    add-int v43, v43, v15

    #@154
    invoke-static/range {v42 .. v43}, Ljava/lang/Math;->max(II)I

    #@157
    move-result v23

    #@158
    .line 413
    .local v23, "nextLeft":I
    move-object/from16 v0, p0

    #@15a
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@15c
    move-object/from16 v42, v0

    #@15e
    add-int v43, v23, v26

    #@160
    move-object/from16 v0, p0

    #@162
    iget-object v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mNextText:Landroid/widget/TextView;

    #@164
    move-object/from16 v44, v0

    #@166
    invoke-virtual/range {v44 .. v44}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@169
    move-result v44

    #@16a
    add-int v44, v44, v24

    #@16c
    move-object/from16 v0, v42

    #@16e
    move/from16 v1, v23

    #@170
    move/from16 v2, v24

    #@172
    move/from16 v3, v43

    #@174
    move/from16 v4, v44

    #@176
    invoke-virtual {v0, v1, v2, v3, v4}, Landroid/widget/TextView;->layout(IIII)V

    #@179
    .line 416
    move/from16 v0, p2

    #@17b
    move-object/from16 v1, p0

    #@17d
    iput v0, v1, Landroid/support/v4/view/PagerTitleStrip;->mLastKnownPositionOffset:F

    #@17f
    .line 417
    const/16 v42, 0x0

    #@181
    move/from16 v0, v42

    #@183
    move-object/from16 v1, p0

    #@185
    iput-boolean v0, v1, Landroid/support/v4/view/PagerTitleStrip;->mUpdatingPositions:Z

    #@187
    .line 418
    .end local v5    # "alignedCurrHeight":I
    .end local v6    # "alignedNextHeight":I
    .end local v7    # "alignedPrevHeight":I
    .end local v10    # "contentWidth":I
    .end local v11    # "currBaseline":I
    .end local v12    # "currCenter":I
    .end local v13    # "currLeft":I
    .end local v14    # "currOffset":F
    .end local v15    # "currRight":I
    .end local v16    # "currTop":I
    .end local v17    # "currTopOffset":I
    .end local v18    # "currWidth":I
    .end local v19    # "halfCurrWidth":I
    .end local v20    # "maxBaseline":I
    .end local v21    # "maxTextHeight":I
    .end local v22    # "nextBaseline":I
    .end local v23    # "nextLeft":I
    .end local v24    # "nextTop":I
    .end local v25    # "nextTopOffset":I
    .end local v26    # "nextWidth":I
    .end local v28    # "paddingBottom":I
    .end local v29    # "paddingLeft":I
    .end local v30    # "paddingRight":I
    .end local v31    # "paddingTop":I
    .end local v32    # "prevBaseline":I
    .end local v33    # "prevLeft":I
    .end local v34    # "prevTop":I
    .end local v35    # "prevTopOffset":I
    .end local v36    # "prevWidth":I
    .end local v37    # "stripHeight":I
    .end local v38    # "stripWidth":I
    .end local v39    # "textPaddedLeft":I
    .end local v40    # "textPaddedRight":I
    .end local v41    # "vgrav":I
    :goto_1
    return-void

    #@188
    .line 335
    :cond_2
    if-nez p3, :cond_0

    #@18a
    move-object/from16 v0, p0

    #@18c
    iget v0, v0, Landroid/support/v4/view/PagerTitleStrip;->mLastKnownPositionOffset:F

    #@18e
    move/from16 v42, v0

    #@190
    cmpl-float v42, p2, v42

    #@192
    if-nez v42, :cond_0

    #@194
    goto :goto_1

    #@195
    .line 390
    .restart local v5    # "alignedCurrHeight":I
    .restart local v6    # "alignedNextHeight":I
    .restart local v7    # "alignedPrevHeight":I
    .restart local v10    # "contentWidth":I
    .restart local v11    # "currBaseline":I
    .restart local v12    # "currCenter":I
    .restart local v13    # "currLeft":I
    .restart local v14    # "currOffset":F
    .restart local v15    # "currRight":I
    .restart local v17    # "currTopOffset":I
    .restart local v18    # "currWidth":I
    .restart local v19    # "halfCurrWidth":I
    .restart local v20    # "maxBaseline":I
    .restart local v21    # "maxTextHeight":I
    .restart local v22    # "nextBaseline":I
    .restart local v25    # "nextTopOffset":I
    .restart local v26    # "nextWidth":I
    .restart local v28    # "paddingBottom":I
    .restart local v29    # "paddingLeft":I
    .restart local v30    # "paddingRight":I
    .restart local v31    # "paddingTop":I
    .restart local v32    # "prevBaseline":I
    .restart local v35    # "prevTopOffset":I
    .restart local v36    # "prevWidth":I
    .restart local v37    # "stripHeight":I
    .restart local v38    # "stripWidth":I
    .restart local v39    # "textPaddedLeft":I
    .restart local v40    # "textPaddedRight":I
    .restart local v41    # "vgrav":I
    :sswitch_0
    sub-int v42, v37, v31

    #@197
    sub-int v27, v42, v28

    #@199
    .line 391
    .local v27, "paddedHeight":I
    sub-int v42, v27, v21

    #@19b
    div-int/lit8 v9, v42, 0x2

    #@19d
    .line 392
    .local v9, "centeredTop":I
    add-int v34, v9, v35

    #@19f
    .line 393
    .restart local v34    # "prevTop":I
    add-int v16, v9, v17

    #@1a1
    .line 394
    .restart local v16    # "currTop":I
    add-int v24, v9, v25

    #@1a3
    .line 395
    .restart local v24    # "nextTop":I
    goto/16 :goto_0

    #@1a5
    .line 397
    .end local v9    # "centeredTop":I
    .end local v16    # "currTop":I
    .end local v24    # "nextTop":I
    .end local v27    # "paddedHeight":I
    .end local v34    # "prevTop":I
    :sswitch_1
    sub-int v42, v37, v28

    #@1a7
    sub-int v8, v42, v21

    #@1a9
    .line 398
    .local v8, "bottomGravTop":I
    add-int v34, v8, v35

    #@1ab
    .line 399
    .restart local v34    # "prevTop":I
    add-int v16, v8, v17

    #@1ad
    .line 400
    .restart local v16    # "currTop":I
    add-int v24, v8, v25

    #@1af
    .restart local v24    # "nextTop":I
    goto/16 :goto_0

    #@1b1
    .line 382
    nop

    #@1b2
    :sswitch_data_0
    .sparse-switch
        0x10 -> :sswitch_0
        0x50 -> :sswitch_1
    .end sparse-switch
.end method
