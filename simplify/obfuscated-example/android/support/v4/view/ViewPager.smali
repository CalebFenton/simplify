.class public Landroid/support/v4/view/ViewPager;
.super Landroid/view/ViewGroup;
.source "ViewPager.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/view/ViewPager$ViewPositionComparator;,
        Landroid/support/v4/view/ViewPager$LayoutParams;,
        Landroid/support/v4/view/ViewPager$PagerObserver;,
        Landroid/support/v4/view/ViewPager$MyAccessibilityDelegate;,
        Landroid/support/v4/view/ViewPager$SavedState;,
        Landroid/support/v4/view/ViewPager$Decor;,
        Landroid/support/v4/view/ViewPager$OnAdapterChangeListener;,
        Landroid/support/v4/view/ViewPager$PageTransformer;,
        Landroid/support/v4/view/ViewPager$SimpleOnPageChangeListener;,
        Landroid/support/v4/view/ViewPager$OnPageChangeListener;,
        Landroid/support/v4/view/ViewPager$ItemInfo;
    }
.end annotation


# static fields
.field private static final CLOSE_ENOUGH:I = 0x2

.field private static final COMPARATOR:Ljava/util/Comparator;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Comparator",
            "<",
            "Landroid/support/v4/view/ViewPager$ItemInfo;",
            ">;"
        }
    .end annotation
.end field

.field private static final DEBUG:Z = false

.field private static final DEFAULT_GUTTER_SIZE:I = 0x10

.field private static final DEFAULT_OFFSCREEN_PAGES:I = 0x1

.field private static final DRAW_ORDER_DEFAULT:I = 0x0

.field private static final DRAW_ORDER_FORWARD:I = 0x1

.field private static final DRAW_ORDER_REVERSE:I = 0x2

.field private static final INVALID_POINTER:I = -0x1

.field private static final LAYOUT_ATTRS:[I

.field private static final MAX_SETTLE_DURATION:I = 0x258

.field private static final MIN_DISTANCE_FOR_FLING:I = 0x19

.field private static final MIN_FLING_VELOCITY:I = 0x190

.field public static final SCROLL_STATE_DRAGGING:I = 0x1

.field public static final SCROLL_STATE_IDLE:I = 0x0

.field public static final SCROLL_STATE_SETTLING:I = 0x2

.field private static final TAG:Ljava/lang/String; = "ViewPager"

.field private static final USE_CACHE:Z

.field private static final sInterpolator:Landroid/view/animation/Interpolator;

.field private static final sPositionComparator:Landroid/support/v4/view/ViewPager$ViewPositionComparator;


# instance fields
.field private mActivePointerId:I

.field private mAdapter:Landroid/support/v4/view/PagerAdapter;

.field private mAdapterChangeListener:Landroid/support/v4/view/ViewPager$OnAdapterChangeListener;

.field private mBottomPageBounds:I

.field private mCalledSuper:Z

.field private mChildHeightMeasureSpec:I

.field private mChildWidthMeasureSpec:I

.field private mCloseEnough:I

.field private mCurItem:I

.field private mDecorChildCount:I

.field private mDefaultGutterSize:I

.field private mDrawingOrder:I

.field private mDrawingOrderedChildren:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;"
        }
    .end annotation
.end field

.field private final mEndScrollRunnable:Ljava/lang/Runnable;

.field private mExpectedAdapterCount:I

.field private mFakeDragBeginTime:J

.field private mFakeDragging:Z

.field private mFirstLayout:Z

.field private mFirstOffset:F

.field private mFlingDistance:I

.field private mGutterSize:I

.field private mIgnoreGutter:Z

.field private mInLayout:Z

.field private mInitialMotionX:F

.field private mInitialMotionY:F

.field private mInternalPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

.field private mIsBeingDragged:Z

.field private mIsUnableToDrag:Z

.field private final mItems:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v4/view/ViewPager$ItemInfo;",
            ">;"
        }
    .end annotation
.end field

.field private mLastMotionX:F

.field private mLastMotionY:F

.field private mLastOffset:F

.field private mLeftEdge:Landroid/support/v4/widget/EdgeEffectCompat;

.field private mMarginDrawable:Landroid/graphics/drawable/Drawable;

.field private mMaximumVelocity:I

.field private mMinimumVelocity:I

.field private mNeedCalculatePageOffsets:Z

.field private mObserver:Landroid/support/v4/view/ViewPager$PagerObserver;

.field private mOffscreenPageLimit:I

.field private mOnPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

.field private mPageMargin:I

.field private mPageTransformer:Landroid/support/v4/view/ViewPager$PageTransformer;

.field private mPopulatePending:Z

.field private mRestoredAdapterState:Landroid/os/Parcelable;

.field private mRestoredClassLoader:Ljava/lang/ClassLoader;

.field private mRestoredCurItem:I

.field private mRightEdge:Landroid/support/v4/widget/EdgeEffectCompat;

.field private mScrollState:I

.field private mScroller:Landroid/widget/Scroller;

.field private mScrollingCacheEnabled:Z

.field private mSetChildrenDrawingOrderEnabled:Ljava/lang/reflect/Method;

.field private final mTempItem:Landroid/support/v4/view/ViewPager$ItemInfo;

.field private final mTempRect:Landroid/graphics/Rect;

.field private mTopPageBounds:I

.field private mTouchSlop:I

.field private mVelocityTracker:Landroid/view/VelocityTracker;


# direct methods
.method static constructor <clinit>()V
    .locals 3

    #@0
    .prologue
    .line 103
    const/4 v0, 0x1

    #@1
    new-array v0, v0, [I

    #@3
    const/4 v1, 0x0

    #@4
    const v2, 0x10100b3

    #@7
    aput v2, v0, v1

    #@9
    sput-object v0, Landroid/support/v4/view/ViewPager;->LAYOUT_ATTRS:[I

    #@b
    .line 121
    new-instance v0, Landroid/support/v4/view/ViewPager$1;

    #@d
    invoke-direct {v0}, Landroid/support/v4/view/ViewPager$1;-><init>()V

    #@10
    sput-object v0, Landroid/support/v4/view/ViewPager;->COMPARATOR:Ljava/util/Comparator;

    #@12
    .line 128
    new-instance v0, Landroid/support/v4/view/ViewPager$2;

    #@14
    invoke-direct {v0}, Landroid/support/v4/view/ViewPager$2;-><init>()V

    #@17
    sput-object v0, Landroid/support/v4/view/ViewPager;->sInterpolator:Landroid/view/animation/Interpolator;

    #@19
    .line 228
    new-instance v0, Landroid/support/v4/view/ViewPager$ViewPositionComparator;

    #@1b
    invoke-direct {v0}, Landroid/support/v4/view/ViewPager$ViewPositionComparator;-><init>()V

    #@1e
    sput-object v0, Landroid/support/v4/view/ViewPager;->sPositionComparator:Landroid/support/v4/view/ViewPager$ViewPositionComparator;

    #@20
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 5
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    const/4 v3, 0x1

    #@2
    const/4 v2, 0x0

    #@3
    const/4 v1, -0x1

    #@4
    .line 349
    invoke-direct {p0, p1}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;)V

    #@7
    .line 135
    new-instance v0, Ljava/util/ArrayList;

    #@9
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@c
    iput-object v0, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@e
    .line 136
    new-instance v0, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@10
    invoke-direct {v0}, Landroid/support/v4/view/ViewPager$ItemInfo;-><init>()V

    #@13
    iput-object v0, p0, Landroid/support/v4/view/ViewPager;->mTempItem:Landroid/support/v4/view/ViewPager$ItemInfo;

    #@15
    .line 138
    new-instance v0, Landroid/graphics/Rect;

    #@17
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@1a
    iput-object v0, p0, Landroid/support/v4/view/ViewPager;->mTempRect:Landroid/graphics/Rect;

    #@1c
    .line 142
    iput v1, p0, Landroid/support/v4/view/ViewPager;->mRestoredCurItem:I

    #@1e
    .line 143
    iput-object v4, p0, Landroid/support/v4/view/ViewPager;->mRestoredAdapterState:Landroid/os/Parcelable;

    #@20
    .line 144
    iput-object v4, p0, Landroid/support/v4/view/ViewPager;->mRestoredClassLoader:Ljava/lang/ClassLoader;

    #@22
    .line 156
    const v0, -0x800001

    #@25
    iput v0, p0, Landroid/support/v4/view/ViewPager;->mFirstOffset:F

    #@27
    .line 157
    const v0, 0x7f7fffff    # Float.MAX_VALUE

    #@2a
    iput v0, p0, Landroid/support/v4/view/ViewPager;->mLastOffset:F

    #@2c
    .line 166
    iput v3, p0, Landroid/support/v4/view/ViewPager;->mOffscreenPageLimit:I

    #@2e
    .line 185
    iput v1, p0, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@30
    .line 212
    iput-boolean v3, p0, Landroid/support/v4/view/ViewPager;->mFirstLayout:Z

    #@32
    .line 213
    iput-boolean v2, p0, Landroid/support/v4/view/ViewPager;->mNeedCalculatePageOffsets:Z

    #@34
    .line 246
    new-instance v0, Landroid/support/v4/view/ViewPager$3;

    #@36
    invoke-direct {v0, p0}, Landroid/support/v4/view/ViewPager$3;-><init>(Landroid/support/v4/view/ViewPager;)V

    #@39
    iput-object v0, p0, Landroid/support/v4/view/ViewPager;->mEndScrollRunnable:Ljava/lang/Runnable;

    #@3b
    .line 253
    iput v2, p0, Landroid/support/v4/view/ViewPager;->mScrollState:I

    #@3d
    .line 350
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->initViewPager()V

    #@40
    .line 351
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 5
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    const/4 v3, 0x1

    #@2
    const/4 v2, 0x0

    #@3
    const/4 v1, -0x1

    #@4
    .line 354
    invoke-direct {p0, p1, p2}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@7
    .line 135
    new-instance v0, Ljava/util/ArrayList;

    #@9
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@c
    iput-object v0, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@e
    .line 136
    new-instance v0, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@10
    invoke-direct {v0}, Landroid/support/v4/view/ViewPager$ItemInfo;-><init>()V

    #@13
    iput-object v0, p0, Landroid/support/v4/view/ViewPager;->mTempItem:Landroid/support/v4/view/ViewPager$ItemInfo;

    #@15
    .line 138
    new-instance v0, Landroid/graphics/Rect;

    #@17
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@1a
    iput-object v0, p0, Landroid/support/v4/view/ViewPager;->mTempRect:Landroid/graphics/Rect;

    #@1c
    .line 142
    iput v1, p0, Landroid/support/v4/view/ViewPager;->mRestoredCurItem:I

    #@1e
    .line 143
    iput-object v4, p0, Landroid/support/v4/view/ViewPager;->mRestoredAdapterState:Landroid/os/Parcelable;

    #@20
    .line 144
    iput-object v4, p0, Landroid/support/v4/view/ViewPager;->mRestoredClassLoader:Ljava/lang/ClassLoader;

    #@22
    .line 156
    const v0, -0x800001

    #@25
    iput v0, p0, Landroid/support/v4/view/ViewPager;->mFirstOffset:F

    #@27
    .line 157
    const v0, 0x7f7fffff    # Float.MAX_VALUE

    #@2a
    iput v0, p0, Landroid/support/v4/view/ViewPager;->mLastOffset:F

    #@2c
    .line 166
    iput v3, p0, Landroid/support/v4/view/ViewPager;->mOffscreenPageLimit:I

    #@2e
    .line 185
    iput v1, p0, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@30
    .line 212
    iput-boolean v3, p0, Landroid/support/v4/view/ViewPager;->mFirstLayout:Z

    #@32
    .line 213
    iput-boolean v2, p0, Landroid/support/v4/view/ViewPager;->mNeedCalculatePageOffsets:Z

    #@34
    .line 246
    new-instance v0, Landroid/support/v4/view/ViewPager$3;

    #@36
    invoke-direct {v0, p0}, Landroid/support/v4/view/ViewPager$3;-><init>(Landroid/support/v4/view/ViewPager;)V

    #@39
    iput-object v0, p0, Landroid/support/v4/view/ViewPager;->mEndScrollRunnable:Ljava/lang/Runnable;

    #@3b
    .line 253
    iput v2, p0, Landroid/support/v4/view/ViewPager;->mScrollState:I

    #@3d
    .line 355
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->initViewPager()V

    #@40
    .line 356
    return-void
.end method

.method static synthetic access$000(Landroid/support/v4/view/ViewPager;I)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/view/ViewPager;
    .param p1, "x1"    # I

    #@0
    .prologue
    .line 89
    invoke-direct {p0, p1}, Landroid/support/v4/view/ViewPager;->setScrollState(I)V

    #@3
    return-void
.end method

.method static synthetic access$200(Landroid/support/v4/view/ViewPager;)Landroid/support/v4/view/PagerAdapter;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/view/ViewPager;

    #@0
    .prologue
    .line 89
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@2
    return-object v0
.end method

.method static synthetic access$300(Landroid/support/v4/view/ViewPager;)I
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/view/ViewPager;

    #@0
    .prologue
    .line 89
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@2
    return v0
.end method

.method static synthetic access$400()[I
    .locals 1

    #@0
    .prologue
    .line 89
    sget-object v0, Landroid/support/v4/view/ViewPager;->LAYOUT_ATTRS:[I

    #@2
    return-object v0
.end method

.method private calculatePageOffsets(Landroid/support/v4/view/ViewPager$ItemInfo;ILandroid/support/v4/view/ViewPager$ItemInfo;)V
    .locals 14
    .param p1, "curItem"    # Landroid/support/v4/view/ViewPager$ItemInfo;
    .param p2, "curIndex"    # I
    .param p3, "oldCurInfo"    # Landroid/support/v4/view/ViewPager$ItemInfo;

    #@0
    .prologue
    .line 1127
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@2
    invoke-virtual {v12}, Landroid/support/v4/view/PagerAdapter;->getCount()I

    #@5
    move-result v1

    #@6
    .line 1128
    .local v1, "N":I
    invoke-direct {p0}, Landroid/support/v4/view/ViewPager;->getClientWidth()I

    #@9
    move-result v11

    #@a
    .line 1129
    .local v11, "width":I
    if-lez v11, :cond_0

    #@c
    iget v12, p0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@e
    int-to-float v12, v12

    #@f
    int-to-float v13, v11

    #@10
    div-float v6, v12, v13

    #@12
    .line 1131
    .local v6, "marginOffset":F
    :goto_0
    if-eqz p3, :cond_6

    #@14
    .line 1132
    move-object/from16 v0, p3

    #@16
    iget v8, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@18
    .line 1134
    .local v8, "oldCurPosition":I
    iget v12, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@1a
    if-ge v8, v12, :cond_3

    #@1c
    .line 1135
    const/4 v5, 0x0

    #@1d
    .line 1136
    .local v5, "itemIndex":I
    const/4 v3, 0x0

    #@1e
    .line 1137
    .local v3, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    move-object/from16 v0, p3

    #@20
    iget v12, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@22
    move-object/from16 v0, p3

    #@24
    iget v13, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@26
    add-float/2addr v12, v13

    #@27
    add-float v7, v12, v6

    #@29
    .line 1138
    .local v7, "offset":F
    add-int/lit8 v9, v8, 0x1

    #@2b
    .line 1139
    .local v9, "pos":I
    :goto_1
    iget v12, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@2d
    if-gt v9, v12, :cond_6

    #@2f
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@31
    invoke-virtual {v12}, Ljava/util/ArrayList;->size()I

    #@34
    move-result v12

    #@35
    if-ge v5, v12, :cond_6

    #@37
    .line 1140
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@39
    invoke-virtual {v12, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@3c
    move-result-object v3

    #@3d
    .end local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    check-cast v3, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@3f
    .line 1141
    .restart local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :goto_2
    iget v12, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@41
    if-le v9, v12, :cond_1

    #@43
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@45
    invoke-virtual {v12}, Ljava/util/ArrayList;->size()I

    #@48
    move-result v12

    #@49
    add-int/lit8 v12, v12, -0x1

    #@4b
    if-ge v5, v12, :cond_1

    #@4d
    .line 1142
    add-int/lit8 v5, v5, 0x1

    #@4f
    .line 1143
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@51
    invoke-virtual {v12, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@54
    move-result-object v3

    #@55
    .end local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    check-cast v3, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@57
    .restart local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    goto :goto_2

    #@58
    .line 1129
    .end local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v5    # "itemIndex":I
    .end local v6    # "marginOffset":F
    .end local v7    # "offset":F
    .end local v8    # "oldCurPosition":I
    .end local v9    # "pos":I
    :cond_0
    const/4 v6, 0x0

    #@59
    goto :goto_0

    #@5a
    .line 1145
    .restart local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .restart local v5    # "itemIndex":I
    .restart local v6    # "marginOffset":F
    .restart local v7    # "offset":F
    .restart local v8    # "oldCurPosition":I
    .restart local v9    # "pos":I
    :cond_1
    :goto_3
    iget v12, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@5c
    if-ge v9, v12, :cond_2

    #@5e
    .line 1148
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@60
    invoke-virtual {v12, v9}, Landroid/support/v4/view/PagerAdapter;->getPageWidth(I)F

    #@63
    move-result v12

    #@64
    add-float/2addr v12, v6

    #@65
    add-float/2addr v7, v12

    #@66
    .line 1149
    add-int/lit8 v9, v9, 0x1

    #@68
    goto :goto_3

    #@69
    .line 1151
    :cond_2
    iput v7, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@6b
    .line 1152
    iget v12, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@6d
    add-float/2addr v12, v6

    #@6e
    add-float/2addr v7, v12

    #@6f
    .line 1139
    add-int/lit8 v9, v9, 0x1

    #@71
    goto :goto_1

    #@72
    .line 1154
    .end local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v5    # "itemIndex":I
    .end local v7    # "offset":F
    .end local v9    # "pos":I
    :cond_3
    iget v12, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@74
    if-le v8, v12, :cond_6

    #@76
    .line 1155
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@78
    invoke-virtual {v12}, Ljava/util/ArrayList;->size()I

    #@7b
    move-result v12

    #@7c
    add-int/lit8 v5, v12, -0x1

    #@7e
    .line 1156
    .restart local v5    # "itemIndex":I
    const/4 v3, 0x0

    #@7f
    .line 1157
    .restart local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    move-object/from16 v0, p3

    #@81
    iget v7, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@83
    .line 1158
    .restart local v7    # "offset":F
    add-int/lit8 v9, v8, -0x1

    #@85
    .line 1159
    .restart local v9    # "pos":I
    :goto_4
    iget v12, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@87
    if-lt v9, v12, :cond_6

    #@89
    if-ltz v5, :cond_6

    #@8b
    .line 1160
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@8d
    invoke-virtual {v12, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@90
    move-result-object v3

    #@91
    .end local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    check-cast v3, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@93
    .line 1161
    .restart local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :goto_5
    iget v12, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@95
    if-ge v9, v12, :cond_4

    #@97
    if-lez v5, :cond_4

    #@99
    .line 1162
    add-int/lit8 v5, v5, -0x1

    #@9b
    .line 1163
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@9d
    invoke-virtual {v12, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@a0
    move-result-object v3

    #@a1
    .end local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    check-cast v3, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@a3
    .restart local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    goto :goto_5

    #@a4
    .line 1165
    :cond_4
    :goto_6
    iget v12, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@a6
    if-le v9, v12, :cond_5

    #@a8
    .line 1168
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@aa
    invoke-virtual {v12, v9}, Landroid/support/v4/view/PagerAdapter;->getPageWidth(I)F

    #@ad
    move-result v12

    #@ae
    add-float/2addr v12, v6

    #@af
    sub-float/2addr v7, v12

    #@b0
    .line 1169
    add-int/lit8 v9, v9, -0x1

    #@b2
    goto :goto_6

    #@b3
    .line 1171
    :cond_5
    iget v12, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@b5
    add-float/2addr v12, v6

    #@b6
    sub-float/2addr v7, v12

    #@b7
    .line 1172
    iput v7, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@b9
    .line 1159
    add-int/lit8 v9, v9, -0x1

    #@bb
    goto :goto_4

    #@bc
    .line 1178
    .end local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v5    # "itemIndex":I
    .end local v7    # "offset":F
    .end local v8    # "oldCurPosition":I
    .end local v9    # "pos":I
    :cond_6
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@be
    invoke-virtual {v12}, Ljava/util/ArrayList;->size()I

    #@c1
    move-result v4

    #@c2
    .line 1179
    .local v4, "itemCount":I
    iget v7, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@c4
    .line 1180
    .restart local v7    # "offset":F
    iget v12, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@c6
    add-int/lit8 v9, v12, -0x1

    #@c8
    .line 1181
    .restart local v9    # "pos":I
    iget v12, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@ca
    if-nez v12, :cond_7

    #@cc
    iget v12, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@ce
    :goto_7
    iput v12, p0, Landroid/support/v4/view/ViewPager;->mFirstOffset:F

    #@d0
    .line 1182
    iget v12, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@d2
    add-int/lit8 v13, v1, -0x1

    #@d4
    if-ne v12, v13, :cond_8

    #@d6
    iget v12, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@d8
    iget v13, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@da
    add-float/2addr v12, v13

    #@db
    const/high16 v13, 0x3f800000    # 1.0f

    #@dd
    sub-float/2addr v12, v13

    #@de
    :goto_8
    iput v12, p0, Landroid/support/v4/view/ViewPager;->mLastOffset:F

    #@e0
    .line 1185
    add-int/lit8 v2, p2, -0x1

    #@e2
    .local v2, "i":I
    :goto_9
    if-ltz v2, :cond_b

    #@e4
    .line 1186
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@e6
    invoke-virtual {v12, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@e9
    move-result-object v3

    #@ea
    check-cast v3, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@ec
    .line 1187
    .restart local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :goto_a
    iget v12, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@ee
    if-le v9, v12, :cond_9

    #@f0
    .line 1188
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@f2
    add-int/lit8 v10, v9, -0x1

    #@f4
    .end local v9    # "pos":I
    .local v10, "pos":I
    invoke-virtual {v12, v9}, Landroid/support/v4/view/PagerAdapter;->getPageWidth(I)F

    #@f7
    move-result v12

    #@f8
    add-float/2addr v12, v6

    #@f9
    sub-float/2addr v7, v12

    #@fa
    move v9, v10

    #@fb
    .end local v10    # "pos":I
    .restart local v9    # "pos":I
    goto :goto_a

    #@fc
    .line 1181
    .end local v2    # "i":I
    .end local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_7
    const v12, -0x800001

    #@ff
    goto :goto_7

    #@100
    .line 1182
    :cond_8
    const v12, 0x7f7fffff    # Float.MAX_VALUE

    #@103
    goto :goto_8

    #@104
    .line 1190
    .restart local v2    # "i":I
    .restart local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_9
    iget v12, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@106
    add-float/2addr v12, v6

    #@107
    sub-float/2addr v7, v12

    #@108
    .line 1191
    iput v7, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@10a
    .line 1192
    iget v12, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@10c
    if-nez v12, :cond_a

    #@10e
    iput v7, p0, Landroid/support/v4/view/ViewPager;->mFirstOffset:F

    #@110
    .line 1185
    :cond_a
    add-int/lit8 v2, v2, -0x1

    #@112
    add-int/lit8 v9, v9, -0x1

    #@114
    goto :goto_9

    #@115
    .line 1194
    .end local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_b
    iget v12, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@117
    iget v13, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@119
    add-float/2addr v12, v13

    #@11a
    add-float v7, v12, v6

    #@11c
    .line 1195
    iget v12, p1, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@11e
    add-int/lit8 v9, v12, 0x1

    #@120
    .line 1197
    add-int/lit8 v2, p2, 0x1

    #@122
    :goto_b
    if-ge v2, v4, :cond_e

    #@124
    .line 1198
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@126
    invoke-virtual {v12, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@129
    move-result-object v3

    #@12a
    check-cast v3, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@12c
    .line 1199
    .restart local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :goto_c
    iget v12, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@12e
    if-ge v9, v12, :cond_c

    #@130
    .line 1200
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@132
    add-int/lit8 v10, v9, 0x1

    #@134
    .end local v9    # "pos":I
    .restart local v10    # "pos":I
    invoke-virtual {v12, v9}, Landroid/support/v4/view/PagerAdapter;->getPageWidth(I)F

    #@137
    move-result v12

    #@138
    add-float/2addr v12, v6

    #@139
    add-float/2addr v7, v12

    #@13a
    move v9, v10

    #@13b
    .end local v10    # "pos":I
    .restart local v9    # "pos":I
    goto :goto_c

    #@13c
    .line 1202
    :cond_c
    iget v12, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@13e
    add-int/lit8 v13, v1, -0x1

    #@140
    if-ne v12, v13, :cond_d

    #@142
    .line 1203
    iget v12, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@144
    add-float/2addr v12, v7

    #@145
    const/high16 v13, 0x3f800000    # 1.0f

    #@147
    sub-float/2addr v12, v13

    #@148
    iput v12, p0, Landroid/support/v4/view/ViewPager;->mLastOffset:F

    #@14a
    .line 1205
    :cond_d
    iput v7, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@14c
    .line 1206
    iget v12, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@14e
    add-float/2addr v12, v6

    #@14f
    add-float/2addr v7, v12

    #@150
    .line 1197
    add-int/lit8 v2, v2, 0x1

    #@152
    add-int/lit8 v9, v9, 0x1

    #@154
    goto :goto_b

    #@155
    .line 1209
    .end local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_e
    const/4 v12, 0x0

    #@156
    iput-boolean v12, p0, Landroid/support/v4/view/ViewPager;->mNeedCalculatePageOffsets:Z

    #@158
    .line 1210
    return-void
.end method

.method private completeScroll(Z)V
    .locals 10
    .param p1, "postEvents"    # Z

    #@0
    .prologue
    const/4 v7, 0x0

    #@1
    .line 1741
    iget v8, p0, Landroid/support/v4/view/ViewPager;->mScrollState:I

    #@3
    const/4 v9, 0x2

    #@4
    if-ne v8, v9, :cond_3

    #@6
    const/4 v2, 0x1

    #@7
    .line 1742
    .local v2, "needPopulate":Z
    :goto_0
    if-eqz v2, :cond_1

    #@9
    .line 1744
    invoke-direct {p0, v7}, Landroid/support/v4/view/ViewPager;->setScrollingCacheEnabled(Z)V

    #@c
    .line 1745
    iget-object v8, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@e
    invoke-virtual {v8}, Landroid/widget/Scroller;->abortAnimation()V

    #@11
    .line 1746
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@14
    move-result v3

    #@15
    .line 1747
    .local v3, "oldX":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getScrollY()I

    #@18
    move-result v4

    #@19
    .line 1748
    .local v4, "oldY":I
    iget-object v8, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@1b
    invoke-virtual {v8}, Landroid/widget/Scroller;->getCurrX()I

    #@1e
    move-result v5

    #@1f
    .line 1749
    .local v5, "x":I
    iget-object v8, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@21
    invoke-virtual {v8}, Landroid/widget/Scroller;->getCurrY()I

    #@24
    move-result v6

    #@25
    .line 1750
    .local v6, "y":I
    if-ne v3, v5, :cond_0

    #@27
    if-eq v4, v6, :cond_1

    #@29
    .line 1751
    :cond_0
    invoke-virtual {p0, v5, v6}, Landroid/support/v4/view/ViewPager;->scrollTo(II)V

    #@2c
    .line 1754
    .end local v3    # "oldX":I
    .end local v4    # "oldY":I
    .end local v5    # "x":I
    .end local v6    # "y":I
    :cond_1
    iput-boolean v7, p0, Landroid/support/v4/view/ViewPager;->mPopulatePending:Z

    #@2e
    .line 1755
    const/4 v0, 0x0

    #@2f
    .local v0, "i":I
    :goto_1
    iget-object v8, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@31
    invoke-virtual {v8}, Ljava/util/ArrayList;->size()I

    #@34
    move-result v8

    #@35
    if-ge v0, v8, :cond_4

    #@37
    .line 1756
    iget-object v8, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@39
    invoke-virtual {v8, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@3c
    move-result-object v1

    #@3d
    check-cast v1, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@3f
    .line 1757
    .local v1, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    iget-boolean v8, v1, Landroid/support/v4/view/ViewPager$ItemInfo;->scrolling:Z

    #@41
    if-eqz v8, :cond_2

    #@43
    .line 1758
    const/4 v2, 0x1

    #@44
    .line 1759
    iput-boolean v7, v1, Landroid/support/v4/view/ViewPager$ItemInfo;->scrolling:Z

    #@46
    .line 1755
    :cond_2
    add-int/lit8 v0, v0, 0x1

    #@48
    goto :goto_1

    #@49
    .end local v0    # "i":I
    .end local v1    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v2    # "needPopulate":Z
    :cond_3
    move v2, v7

    #@4a
    .line 1741
    goto :goto_0

    #@4b
    .line 1762
    .restart local v0    # "i":I
    .restart local v2    # "needPopulate":Z
    :cond_4
    if-eqz v2, :cond_5

    #@4d
    .line 1763
    if-eqz p1, :cond_6

    #@4f
    .line 1764
    iget-object v7, p0, Landroid/support/v4/view/ViewPager;->mEndScrollRunnable:Ljava/lang/Runnable;

    #@51
    invoke-static {p0, v7}, Landroid/support/v4/view/ViewCompat;->postOnAnimation(Landroid/view/View;Ljava/lang/Runnable;)V

    #@54
    .line 1769
    :cond_5
    :goto_2
    return-void

    #@55
    .line 1766
    :cond_6
    iget-object v7, p0, Landroid/support/v4/view/ViewPager;->mEndScrollRunnable:Ljava/lang/Runnable;

    #@57
    invoke-interface {v7}, Ljava/lang/Runnable;->run()V

    #@5a
    goto :goto_2
.end method

.method private determineTargetPage(IFII)I
    .locals 6
    .param p1, "currentPage"    # I
    .param p2, "pageOffset"    # F
    .param p3, "velocity"    # I
    .param p4, "deltaX"    # I

    #@0
    .prologue
    .line 2156
    invoke-static {p4}, Ljava/lang/Math;->abs(I)I

    #@3
    move-result v4

    #@4
    iget v5, p0, Landroid/support/v4/view/ViewPager;->mFlingDistance:I

    #@6
    if-le v4, v5, :cond_2

    #@8
    invoke-static {p3}, Ljava/lang/Math;->abs(I)I

    #@b
    move-result v4

    #@c
    iget v5, p0, Landroid/support/v4/view/ViewPager;->mMinimumVelocity:I

    #@e
    if-le v4, v5, :cond_2

    #@10
    .line 2157
    if-lez p3, :cond_1

    #@12
    move v2, p1

    #@13
    .line 2163
    .local v2, "targetPage":I
    :goto_0
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@15
    invoke-virtual {v4}, Ljava/util/ArrayList;->size()I

    #@18
    move-result v4

    #@19
    if-lez v4, :cond_0

    #@1b
    .line 2164
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@1d
    const/4 v5, 0x0

    #@1e
    invoke-virtual {v4, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@21
    move-result-object v0

    #@22
    check-cast v0, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@24
    .line 2165
    .local v0, "firstItem":Landroid/support/v4/view/ViewPager$ItemInfo;
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@26
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@28
    invoke-virtual {v5}, Ljava/util/ArrayList;->size()I

    #@2b
    move-result v5

    #@2c
    add-int/lit8 v5, v5, -0x1

    #@2e
    invoke-virtual {v4, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@31
    move-result-object v1

    #@32
    check-cast v1, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@34
    .line 2168
    .local v1, "lastItem":Landroid/support/v4/view/ViewPager$ItemInfo;
    iget v4, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@36
    iget v5, v1, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@38
    invoke-static {v2, v5}, Ljava/lang/Math;->min(II)I

    #@3b
    move-result v5

    #@3c
    invoke-static {v4, v5}, Ljava/lang/Math;->max(II)I

    #@3f
    move-result v2

    #@40
    .line 2171
    .end local v0    # "firstItem":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v1    # "lastItem":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_0
    return v2

    #@41
    .line 2157
    .end local v2    # "targetPage":I
    :cond_1
    add-int/lit8 v2, p1, 0x1

    #@43
    goto :goto_0

    #@44
    .line 2159
    :cond_2
    iget v4, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@46
    if-lt p1, v4, :cond_3

    #@48
    const v3, 0x3ecccccd    # 0.4f

    #@4b
    .line 2160
    .local v3, "truncator":F
    :goto_1
    int-to-float v4, p1

    #@4c
    add-float/2addr v4, p2

    #@4d
    add-float/2addr v4, v3

    #@4e
    float-to-int v2, v4

    #@4f
    .restart local v2    # "targetPage":I
    goto :goto_0

    #@50
    .line 2159
    .end local v2    # "targetPage":I
    .end local v3    # "truncator":F
    :cond_3
    const v3, 0x3f19999a    # 0.6f

    #@53
    goto :goto_1
.end method

.method private enableLayers(Z)V
    .locals 5
    .param p1, "enable"    # Z

    #@0
    .prologue
    .line 1776
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@3
    move-result v0

    #@4
    .line 1777
    .local v0, "childCount":I
    const/4 v1, 0x0

    #@5
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_1

    #@7
    .line 1778
    if-eqz p1, :cond_0

    #@9
    const/4 v2, 0x2

    #@a
    .line 1780
    .local v2, "layerType":I
    :goto_1
    invoke-virtual {p0, v1}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@d
    move-result-object v3

    #@e
    const/4 v4, 0x0

    #@f
    invoke-static {v3, v2, v4}, Landroid/support/v4/view/ViewCompat;->setLayerType(Landroid/view/View;ILandroid/graphics/Paint;)V

    #@12
    .line 1777
    add-int/lit8 v1, v1, 0x1

    #@14
    goto :goto_0

    #@15
    .line 1778
    .end local v2    # "layerType":I
    :cond_0
    const/4 v2, 0x0

    #@16
    goto :goto_1

    #@17
    .line 1782
    :cond_1
    return-void
.end method

.method private endDrag()V
    .locals 1

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 2404
    iput-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@3
    .line 2405
    iput-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsUnableToDrag:Z

    #@5
    .line 2407
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@7
    if-eqz v0, :cond_0

    #@9
    .line 2408
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@b
    invoke-virtual {v0}, Landroid/view/VelocityTracker;->recycle()V

    #@e
    .line 2409
    const/4 v0, 0x0

    #@f
    iput-object v0, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@11
    .line 2411
    :cond_0
    return-void
.end method

.method private getChildRectInPagerCoordinates(Landroid/graphics/Rect;Landroid/view/View;)Landroid/graphics/Rect;
    .locals 4
    .param p1, "outRect"    # Landroid/graphics/Rect;
    .param p2, "child"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 2585
    if-nez p1, :cond_0

    #@3
    .line 2586
    new-instance p1, Landroid/graphics/Rect;

    #@5
    .end local p1    # "outRect":Landroid/graphics/Rect;
    invoke-direct {p1}, Landroid/graphics/Rect;-><init>()V

    #@8
    .line 2588
    .restart local p1    # "outRect":Landroid/graphics/Rect;
    :cond_0
    if-nez p2, :cond_2

    #@a
    .line 2589
    invoke-virtual {p1, v2, v2, v2, v2}, Landroid/graphics/Rect;->set(IIII)V

    #@d
    .line 2607
    :cond_1
    return-object p1

    #@e
    .line 2592
    :cond_2
    invoke-virtual {p2}, Landroid/view/View;->getLeft()I

    #@11
    move-result v2

    #@12
    iput v2, p1, Landroid/graphics/Rect;->left:I

    #@14
    .line 2593
    invoke-virtual {p2}, Landroid/view/View;->getRight()I

    #@17
    move-result v2

    #@18
    iput v2, p1, Landroid/graphics/Rect;->right:I

    #@1a
    .line 2594
    invoke-virtual {p2}, Landroid/view/View;->getTop()I

    #@1d
    move-result v2

    #@1e
    iput v2, p1, Landroid/graphics/Rect;->top:I

    #@20
    .line 2595
    invoke-virtual {p2}, Landroid/view/View;->getBottom()I

    #@23
    move-result v2

    #@24
    iput v2, p1, Landroid/graphics/Rect;->bottom:I

    #@26
    .line 2597
    invoke-virtual {p2}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@29
    move-result-object v1

    #@2a
    .line 2598
    .local v1, "parent":Landroid/view/ViewParent;
    :goto_0
    instance-of v2, v1, Landroid/view/ViewGroup;

    #@2c
    if-eqz v2, :cond_1

    #@2e
    if-eq v1, p0, :cond_1

    #@30
    move-object v0, v1

    #@31
    .line 2599
    check-cast v0, Landroid/view/ViewGroup;

    #@33
    .line 2600
    .local v0, "group":Landroid/view/ViewGroup;
    iget v2, p1, Landroid/graphics/Rect;->left:I

    #@35
    invoke-virtual {v0}, Landroid/view/ViewGroup;->getLeft()I

    #@38
    move-result v3

    #@39
    add-int/2addr v2, v3

    #@3a
    iput v2, p1, Landroid/graphics/Rect;->left:I

    #@3c
    .line 2601
    iget v2, p1, Landroid/graphics/Rect;->right:I

    #@3e
    invoke-virtual {v0}, Landroid/view/ViewGroup;->getRight()I

    #@41
    move-result v3

    #@42
    add-int/2addr v2, v3

    #@43
    iput v2, p1, Landroid/graphics/Rect;->right:I

    #@45
    .line 2602
    iget v2, p1, Landroid/graphics/Rect;->top:I

    #@47
    invoke-virtual {v0}, Landroid/view/ViewGroup;->getTop()I

    #@4a
    move-result v3

    #@4b
    add-int/2addr v2, v3

    #@4c
    iput v2, p1, Landroid/graphics/Rect;->top:I

    #@4e
    .line 2603
    iget v2, p1, Landroid/graphics/Rect;->bottom:I

    #@50
    invoke-virtual {v0}, Landroid/view/ViewGroup;->getBottom()I

    #@53
    move-result v3

    #@54
    add-int/2addr v2, v3

    #@55
    iput v2, p1, Landroid/graphics/Rect;->bottom:I

    #@57
    .line 2605
    invoke-virtual {v0}, Landroid/view/ViewGroup;->getParent()Landroid/view/ViewParent;

    #@5a
    move-result-object v1

    #@5b
    .line 2606
    goto :goto_0
.end method

.method private getClientWidth()I
    .locals 2

    #@0
    .prologue
    .line 483
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getMeasuredWidth()I

    #@3
    move-result v0

    #@4
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingLeft()I

    #@7
    move-result v1

    #@8
    sub-int/2addr v0, v1

    #@9
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingRight()I

    #@c
    move-result v1

    #@d
    sub-int/2addr v0, v1

    #@e
    return v0
.end method

.method private infoForCurrentScrollPosition()Landroid/support/v4/view/ViewPager$ItemInfo;
    .locals 15

    #@0
    .prologue
    const/4 v8, 0x0

    #@1
    .line 2113
    invoke-direct {p0}, Landroid/support/v4/view/ViewPager;->getClientWidth()I

    #@4
    move-result v12

    #@5
    .line 2114
    .local v12, "width":I
    if-lez v12, :cond_5

    #@7
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@a
    move-result v13

    #@b
    int-to-float v13, v13

    #@c
    int-to-float v14, v12

    #@d
    div-float v11, v13, v14

    #@f
    .line 2115
    .local v11, "scrollOffset":F
    :goto_0
    if-lez v12, :cond_0

    #@11
    iget v13, p0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@13
    int-to-float v13, v13

    #@14
    int-to-float v14, v12

    #@15
    div-float v8, v13, v14

    #@17
    .line 2116
    .local v8, "marginOffset":F
    :cond_0
    const/4 v5, -0x1

    #@18
    .line 2117
    .local v5, "lastPos":I
    const/4 v4, 0x0

    #@19
    .line 2118
    .local v4, "lastOffset":F
    const/4 v6, 0x0

    #@1a
    .line 2119
    .local v6, "lastWidth":F
    const/4 v0, 0x1

    #@1b
    .line 2121
    .local v0, "first":Z
    const/4 v3, 0x0

    #@1c
    .line 2122
    .local v3, "lastItem":Landroid/support/v4/view/ViewPager$ItemInfo;
    const/4 v1, 0x0

    #@1d
    .local v1, "i":I
    :goto_1
    iget-object v13, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@1f
    invoke-virtual {v13}, Ljava/util/ArrayList;->size()I

    #@22
    move-result v13

    #@23
    if-ge v1, v13, :cond_4

    #@25
    .line 2123
    iget-object v13, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@27
    invoke-virtual {v13, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@2a
    move-result-object v2

    #@2b
    check-cast v2, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@2d
    .line 2125
    .local v2, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    if-nez v0, :cond_1

    #@2f
    iget v13, v2, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@31
    add-int/lit8 v14, v5, 0x1

    #@33
    if-eq v13, v14, :cond_1

    #@35
    .line 2127
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mTempItem:Landroid/support/v4/view/ViewPager$ItemInfo;

    #@37
    .line 2128
    add-float v13, v4, v6

    #@39
    add-float/2addr v13, v8

    #@3a
    iput v13, v2, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@3c
    .line 2129
    add-int/lit8 v13, v5, 0x1

    #@3e
    iput v13, v2, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@40
    .line 2130
    iget-object v13, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@42
    iget v14, v2, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@44
    invoke-virtual {v13, v14}, Landroid/support/v4/view/PagerAdapter;->getPageWidth(I)F

    #@47
    move-result v13

    #@48
    iput v13, v2, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@4a
    .line 2131
    add-int/lit8 v1, v1, -0x1

    #@4c
    .line 2133
    :cond_1
    iget v9, v2, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@4e
    .line 2135
    .local v9, "offset":F
    move v7, v9

    #@4f
    .line 2136
    .local v7, "leftBound":F
    iget v13, v2, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@51
    add-float/2addr v13, v9

    #@52
    add-float v10, v13, v8

    #@54
    .line 2137
    .local v10, "rightBound":F
    if-nez v0, :cond_2

    #@56
    cmpl-float v13, v11, v7

    #@58
    if-ltz v13, :cond_4

    #@5a
    .line 2138
    :cond_2
    cmpg-float v13, v11, v10

    #@5c
    if-ltz v13, :cond_3

    #@5e
    iget-object v13, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@60
    invoke-virtual {v13}, Ljava/util/ArrayList;->size()I

    #@63
    move-result v13

    #@64
    add-int/lit8 v13, v13, -0x1

    #@66
    if-ne v1, v13, :cond_6

    #@68
    :cond_3
    move-object v3, v2

    #@69
    .line 2151
    .end local v2    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v3    # "lastItem":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v7    # "leftBound":F
    .end local v9    # "offset":F
    .end local v10    # "rightBound":F
    :cond_4
    return-object v3

    #@6a
    .end local v0    # "first":Z
    .end local v1    # "i":I
    .end local v4    # "lastOffset":F
    .end local v5    # "lastPos":I
    .end local v6    # "lastWidth":F
    .end local v8    # "marginOffset":F
    .end local v11    # "scrollOffset":F
    :cond_5
    move v11, v8

    #@6b
    .line 2114
    goto :goto_0

    #@6c
    .line 2144
    .restart local v0    # "first":Z
    .restart local v1    # "i":I
    .restart local v2    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .restart local v3    # "lastItem":Landroid/support/v4/view/ViewPager$ItemInfo;
    .restart local v4    # "lastOffset":F
    .restart local v5    # "lastPos":I
    .restart local v6    # "lastWidth":F
    .restart local v7    # "leftBound":F
    .restart local v8    # "marginOffset":F
    .restart local v9    # "offset":F
    .restart local v10    # "rightBound":F
    .restart local v11    # "scrollOffset":F
    :cond_6
    const/4 v0, 0x0

    #@6d
    .line 2145
    iget v5, v2, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@6f
    .line 2146
    move v4, v9

    #@70
    .line 2147
    iget v6, v2, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@72
    .line 2148
    move-object v3, v2

    #@73
    .line 2122
    add-int/lit8 v1, v1, 0x1

    #@75
    goto :goto_1
.end method

.method private isGutterDrag(FF)Z
    .locals 3
    .param p1, "x"    # F
    .param p2, "dx"    # F

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 1772
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mGutterSize:I

    #@3
    int-to-float v0, v0

    #@4
    cmpg-float v0, p1, v0

    #@6
    if-gez v0, :cond_0

    #@8
    cmpl-float v0, p2, v2

    #@a
    if-gtz v0, :cond_1

    #@c
    :cond_0
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getWidth()I

    #@f
    move-result v0

    #@10
    iget v1, p0, Landroid/support/v4/view/ViewPager;->mGutterSize:I

    #@12
    sub-int/2addr v0, v1

    #@13
    int-to-float v0, v0

    #@14
    cmpl-float v0, p1, v0

    #@16
    if-lez v0, :cond_2

    #@18
    cmpg-float v0, p2, v2

    #@1a
    if-gez v0, :cond_2

    #@1c
    :cond_1
    const/4 v0, 0x1

    #@1d
    :goto_0
    return v0

    #@1e
    :cond_2
    const/4 v0, 0x0

    #@1f
    goto :goto_0
.end method

.method private onSecondaryPointerUp(Landroid/view/MotionEvent;)V
    .locals 4
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 2389
    invoke-static {p1}, Landroid/support/v4/view/MotionEventCompat;->getActionIndex(Landroid/view/MotionEvent;)I

    #@3
    move-result v2

    #@4
    .line 2390
    .local v2, "pointerIndex":I
    invoke-static {p1, v2}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@7
    move-result v1

    #@8
    .line 2391
    .local v1, "pointerId":I
    iget v3, p0, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@a
    if-ne v1, v3, :cond_0

    #@c
    .line 2394
    if-nez v2, :cond_1

    #@e
    const/4 v0, 0x1

    #@f
    .line 2395
    .local v0, "newPointerIndex":I
    :goto_0
    invoke-static {p1, v0}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@12
    move-result v3

    #@13
    iput v3, p0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@15
    .line 2396
    invoke-static {p1, v0}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@18
    move-result v3

    #@19
    iput v3, p0, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@1b
    .line 2397
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@1d
    if-eqz v3, :cond_0

    #@1f
    .line 2398
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@21
    invoke-virtual {v3}, Landroid/view/VelocityTracker;->clear()V

    #@24
    .line 2401
    .end local v0    # "newPointerIndex":I
    :cond_0
    return-void

    #@25
    .line 2394
    :cond_1
    const/4 v0, 0x0

    #@26
    goto :goto_0
.end method

.method private pageScrolled(I)Z
    .locals 10
    .param p1, "xpos"    # I

    #@0
    .prologue
    const/4 v7, 0x0

    #@1
    .line 1636
    iget-object v8, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@3
    invoke-virtual {v8}, Ljava/util/ArrayList;->size()I

    #@6
    move-result v8

    #@7
    if-nez v8, :cond_0

    #@9
    .line 1637
    iput-boolean v7, p0, Landroid/support/v4/view/ViewPager;->mCalledSuper:Z

    #@b
    .line 1638
    const/4 v8, 0x0

    #@c
    invoke-virtual {p0, v7, v8, v7}, Landroid/support/v4/view/ViewPager;->onPageScrolled(IFI)V

    #@f
    .line 1639
    iget-boolean v8, p0, Landroid/support/v4/view/ViewPager;->mCalledSuper:Z

    #@11
    if-nez v8, :cond_2

    #@13
    .line 1640
    new-instance v7, Ljava/lang/IllegalStateException;

    #@15
    const-string v8, "onPageScrolled did not call superclass implementation"

    #@17
    invoke-direct {v7, v8}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@1a
    throw v7

    #@1b
    .line 1645
    :cond_0
    invoke-direct {p0}, Landroid/support/v4/view/ViewPager;->infoForCurrentScrollPosition()Landroid/support/v4/view/ViewPager$ItemInfo;

    #@1e
    move-result-object v1

    #@1f
    .line 1646
    .local v1, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    invoke-direct {p0}, Landroid/support/v4/view/ViewPager;->getClientWidth()I

    #@22
    move-result v5

    #@23
    .line 1647
    .local v5, "width":I
    iget v8, p0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@25
    add-int v6, v5, v8

    #@27
    .line 1648
    .local v6, "widthWithMargin":I
    iget v8, p0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@29
    int-to-float v8, v8

    #@2a
    int-to-float v9, v5

    #@2b
    div-float v2, v8, v9

    #@2d
    .line 1649
    .local v2, "marginOffset":F
    iget v0, v1, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@2f
    .line 1650
    .local v0, "currentPage":I
    int-to-float v8, p1

    #@30
    int-to-float v9, v5

    #@31
    div-float/2addr v8, v9

    #@32
    iget v9, v1, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@34
    sub-float/2addr v8, v9

    #@35
    iget v9, v1, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@37
    add-float/2addr v9, v2

    #@38
    div-float v4, v8, v9

    #@3a
    .line 1652
    .local v4, "pageOffset":F
    int-to-float v8, v6

    #@3b
    mul-float/2addr v8, v4

    #@3c
    float-to-int v3, v8

    #@3d
    .line 1654
    .local v3, "offsetPixels":I
    iput-boolean v7, p0, Landroid/support/v4/view/ViewPager;->mCalledSuper:Z

    #@3f
    .line 1655
    invoke-virtual {p0, v0, v4, v3}, Landroid/support/v4/view/ViewPager;->onPageScrolled(IFI)V

    #@42
    .line 1656
    iget-boolean v7, p0, Landroid/support/v4/view/ViewPager;->mCalledSuper:Z

    #@44
    if-nez v7, :cond_1

    #@46
    .line 1657
    new-instance v7, Ljava/lang/IllegalStateException;

    #@48
    const-string v8, "onPageScrolled did not call superclass implementation"

    #@4a
    invoke-direct {v7, v8}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@4d
    throw v7

    #@4e
    .line 1660
    :cond_1
    const/4 v7, 0x1

    #@4f
    .end local v0    # "currentPage":I
    .end local v1    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v2    # "marginOffset":F
    .end local v3    # "offsetPixels":I
    .end local v4    # "pageOffset":F
    .end local v5    # "width":I
    .end local v6    # "widthWithMargin":I
    :cond_2
    return v7
.end method

.method private performDrag(F)Z
    .locals 17
    .param p1, "x"    # F

    #@0
    .prologue
    .line 2062
    const/4 v7, 0x0

    #@1
    .line 2064
    .local v7, "needsInvalidate":Z
    move-object/from16 v0, p0

    #@3
    iget v14, v0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@5
    sub-float v2, v14, p1

    #@7
    .line 2065
    .local v2, "deltaX":F
    move/from16 v0, p1

    #@9
    move-object/from16 v1, p0

    #@b
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@d
    .line 2067
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@10
    move-result v14

    #@11
    int-to-float v8, v14

    #@12
    .line 2068
    .local v8, "oldScrollX":F
    add-float v12, v8, v2

    #@14
    .line 2069
    .local v12, "scrollX":F
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getClientWidth()I

    #@17
    move-result v13

    #@18
    .line 2071
    .local v13, "width":I
    int-to-float v14, v13

    #@19
    move-object/from16 v0, p0

    #@1b
    iget v15, v0, Landroid/support/v4/view/ViewPager;->mFirstOffset:F

    #@1d
    mul-float v6, v14, v15

    #@1f
    .line 2072
    .local v6, "leftBound":F
    int-to-float v14, v13

    #@20
    move-object/from16 v0, p0

    #@22
    iget v15, v0, Landroid/support/v4/view/ViewPager;->mLastOffset:F

    #@24
    mul-float v11, v14, v15

    #@26
    .line 2073
    .local v11, "rightBound":F
    const/4 v5, 0x1

    #@27
    .line 2074
    .local v5, "leftAbsolute":Z
    const/4 v10, 0x1

    #@28
    .line 2076
    .local v10, "rightAbsolute":Z
    move-object/from16 v0, p0

    #@2a
    iget-object v14, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@2c
    const/4 v15, 0x0

    #@2d
    invoke-virtual {v14, v15}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@30
    move-result-object v3

    #@31
    check-cast v3, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@33
    .line 2077
    .local v3, "firstItem":Landroid/support/v4/view/ViewPager$ItemInfo;
    move-object/from16 v0, p0

    #@35
    iget-object v14, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@37
    move-object/from16 v0, p0

    #@39
    iget-object v15, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@3b
    invoke-virtual {v15}, Ljava/util/ArrayList;->size()I

    #@3e
    move-result v15

    #@3f
    add-int/lit8 v15, v15, -0x1

    #@41
    invoke-virtual {v14, v15}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@44
    move-result-object v4

    #@45
    check-cast v4, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@47
    .line 2078
    .local v4, "lastItem":Landroid/support/v4/view/ViewPager$ItemInfo;
    iget v14, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@49
    if-eqz v14, :cond_0

    #@4b
    .line 2079
    const/4 v5, 0x0

    #@4c
    .line 2080
    iget v14, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@4e
    int-to-float v15, v13

    #@4f
    mul-float v6, v14, v15

    #@51
    .line 2082
    :cond_0
    iget v14, v4, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@53
    move-object/from16 v0, p0

    #@55
    iget-object v15, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@57
    invoke-virtual {v15}, Landroid/support/v4/view/PagerAdapter;->getCount()I

    #@5a
    move-result v15

    #@5b
    add-int/lit8 v15, v15, -0x1

    #@5d
    if-eq v14, v15, :cond_1

    #@5f
    .line 2083
    const/4 v10, 0x0

    #@60
    .line 2084
    iget v14, v4, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@62
    int-to-float v15, v13

    #@63
    mul-float v11, v14, v15

    #@65
    .line 2087
    :cond_1
    cmpg-float v14, v12, v6

    #@67
    if-gez v14, :cond_4

    #@69
    .line 2088
    if-eqz v5, :cond_2

    #@6b
    .line 2089
    sub-float v9, v6, v12

    #@6d
    .line 2090
    .local v9, "over":F
    move-object/from16 v0, p0

    #@6f
    iget-object v14, v0, Landroid/support/v4/view/ViewPager;->mLeftEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@71
    invoke-static {v9}, Ljava/lang/Math;->abs(F)F

    #@74
    move-result v15

    #@75
    int-to-float v0, v13

    #@76
    move/from16 v16, v0

    #@78
    div-float v15, v15, v16

    #@7a
    invoke-virtual {v14, v15}, Landroid/support/v4/widget/EdgeEffectCompat;->onPull(F)Z

    #@7d
    move-result v7

    #@7e
    .line 2092
    .end local v9    # "over":F
    :cond_2
    move v12, v6

    #@7f
    .line 2101
    :cond_3
    :goto_0
    move-object/from16 v0, p0

    #@81
    iget v14, v0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@83
    float-to-int v15, v12

    #@84
    int-to-float v15, v15

    #@85
    sub-float v15, v12, v15

    #@87
    add-float/2addr v14, v15

    #@88
    move-object/from16 v0, p0

    #@8a
    iput v14, v0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@8c
    .line 2102
    float-to-int v14, v12

    #@8d
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getScrollY()I

    #@90
    move-result v15

    #@91
    move-object/from16 v0, p0

    #@93
    invoke-virtual {v0, v14, v15}, Landroid/support/v4/view/ViewPager;->scrollTo(II)V

    #@96
    .line 2103
    float-to-int v14, v12

    #@97
    move-object/from16 v0, p0

    #@99
    invoke-direct {v0, v14}, Landroid/support/v4/view/ViewPager;->pageScrolled(I)Z

    #@9c
    .line 2105
    return v7

    #@9d
    .line 2093
    :cond_4
    cmpl-float v14, v12, v11

    #@9f
    if-lez v14, :cond_3

    #@a1
    .line 2094
    if-eqz v10, :cond_5

    #@a3
    .line 2095
    sub-float v9, v12, v11

    #@a5
    .line 2096
    .restart local v9    # "over":F
    move-object/from16 v0, p0

    #@a7
    iget-object v14, v0, Landroid/support/v4/view/ViewPager;->mRightEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@a9
    invoke-static {v9}, Ljava/lang/Math;->abs(F)F

    #@ac
    move-result v15

    #@ad
    int-to-float v0, v13

    #@ae
    move/from16 v16, v0

    #@b0
    div-float v15, v15, v16

    #@b2
    invoke-virtual {v14, v15}, Landroid/support/v4/widget/EdgeEffectCompat;->onPull(F)Z

    #@b5
    move-result v7

    #@b6
    .line 2098
    .end local v9    # "over":F
    :cond_5
    move v12, v11

    #@b7
    goto :goto_0
.end method

.method private recomputeScrollPosition(IIII)V
    .locals 14
    .param p1, "width"    # I
    .param p2, "oldWidth"    # I
    .param p3, "margin"    # I
    .param p4, "oldMargin"    # I

    #@0
    .prologue
    .line 1473
    if-lez p2, :cond_1

    #@2
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@4
    invoke-virtual {v0}, Ljava/util/ArrayList;->isEmpty()Z

    #@7
    move-result v0

    #@8
    if-nez v0, :cond_1

    #@a
    .line 1474
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingLeft()I

    #@d
    move-result v0

    #@e
    sub-int v0, p1, v0

    #@10
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingRight()I

    #@13
    move-result v2

    #@14
    sub-int/2addr v0, v2

    #@15
    add-int v12, v0, p3

    #@17
    .line 1475
    .local v12, "widthWithMargin":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingLeft()I

    #@1a
    move-result v0

    #@1b
    sub-int v0, p2, v0

    #@1d
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingRight()I

    #@20
    move-result v2

    #@21
    sub-int/2addr v0, v2

    #@22
    add-int v7, v0, p4

    #@24
    .line 1477
    .local v7, "oldWidthWithMargin":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@27
    move-result v13

    #@28
    .line 1478
    .local v13, "xpos":I
    int-to-float v0, v13

    #@29
    int-to-float v2, v7

    #@2a
    div-float v8, v0, v2

    #@2c
    .line 1479
    .local v8, "pageOffset":F
    int-to-float v0, v12

    #@2d
    mul-float/2addr v0, v8

    #@2e
    float-to-int v1, v0

    #@2f
    .line 1481
    .local v1, "newOffsetPixels":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getScrollY()I

    #@32
    move-result v0

    #@33
    invoke-virtual {p0, v1, v0}, Landroid/support/v4/view/ViewPager;->scrollTo(II)V

    #@36
    .line 1482
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@38
    invoke-virtual {v0}, Landroid/widget/Scroller;->isFinished()Z

    #@3b
    move-result v0

    #@3c
    if-nez v0, :cond_0

    #@3e
    .line 1484
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@40
    invoke-virtual {v0}, Landroid/widget/Scroller;->getDuration()I

    #@43
    move-result v0

    #@44
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@46
    invoke-virtual {v2}, Landroid/widget/Scroller;->timePassed()I

    #@49
    move-result v2

    #@4a
    sub-int v5, v0, v2

    #@4c
    .line 1485
    .local v5, "newDuration":I
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@4e
    invoke-virtual {p0, v0}, Landroid/support/v4/view/ViewPager;->infoForPosition(I)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@51
    move-result-object v11

    #@52
    .line 1486
    .local v11, "targetInfo":Landroid/support/v4/view/ViewPager$ItemInfo;
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@54
    const/4 v2, 0x0

    #@55
    iget v3, v11, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@57
    int-to-float v4, p1

    #@58
    mul-float/2addr v3, v4

    #@59
    float-to-int v3, v3

    #@5a
    const/4 v4, 0x0

    #@5b
    invoke-virtual/range {v0 .. v5}, Landroid/widget/Scroller;->startScroll(IIIII)V

    #@5e
    .line 1499
    .end local v1    # "newOffsetPixels":I
    .end local v5    # "newDuration":I
    .end local v7    # "oldWidthWithMargin":I
    .end local v8    # "pageOffset":F
    .end local v11    # "targetInfo":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v12    # "widthWithMargin":I
    .end local v13    # "xpos":I
    :cond_0
    :goto_0
    return-void

    #@5f
    .line 1490
    :cond_1
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@61
    invoke-virtual {p0, v0}, Landroid/support/v4/view/ViewPager;->infoForPosition(I)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@64
    move-result-object v6

    #@65
    .line 1491
    .local v6, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    if-eqz v6, :cond_2

    #@67
    iget v0, v6, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@69
    iget v2, p0, Landroid/support/v4/view/ViewPager;->mLastOffset:F

    #@6b
    invoke-static {v0, v2}, Ljava/lang/Math;->min(FF)F

    #@6e
    move-result v9

    #@6f
    .line 1492
    .local v9, "scrollOffset":F
    :goto_1
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingLeft()I

    #@72
    move-result v0

    #@73
    sub-int v0, p1, v0

    #@75
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingRight()I

    #@78
    move-result v2

    #@79
    sub-int/2addr v0, v2

    #@7a
    int-to-float v0, v0

    #@7b
    mul-float/2addr v0, v9

    #@7c
    float-to-int v10, v0

    #@7d
    .line 1494
    .local v10, "scrollPos":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@80
    move-result v0

    #@81
    if-eq v10, v0, :cond_0

    #@83
    .line 1495
    const/4 v0, 0x0

    #@84
    invoke-direct {p0, v0}, Landroid/support/v4/view/ViewPager;->completeScroll(Z)V

    #@87
    .line 1496
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getScrollY()I

    #@8a
    move-result v0

    #@8b
    invoke-virtual {p0, v10, v0}, Landroid/support/v4/view/ViewPager;->scrollTo(II)V

    #@8e
    goto :goto_0

    #@8f
    .line 1491
    .end local v9    # "scrollOffset":F
    .end local v10    # "scrollPos":I
    :cond_2
    const/4 v9, 0x0

    #@90
    goto :goto_1
.end method

.method private removeNonDecorViews()V
    .locals 4

    #@0
    .prologue
    .line 459
    const/4 v1, 0x0

    #@1
    .local v1, "i":I
    :goto_0
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@4
    move-result v3

    #@5
    if-ge v1, v3, :cond_1

    #@7
    .line 460
    invoke-virtual {p0, v1}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@a
    move-result-object v0

    #@b
    .line 461
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@e
    move-result-object v2

    #@f
    check-cast v2, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@11
    .line 462
    .local v2, "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    iget-boolean v3, v2, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@13
    if-nez v3, :cond_0

    #@15
    .line 463
    invoke-virtual {p0, v1}, Landroid/support/v4/view/ViewPager;->removeViewAt(I)V

    #@18
    .line 464
    add-int/lit8 v1, v1, -0x1

    #@1a
    .line 459
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@1c
    goto :goto_0

    #@1d
    .line 467
    .end local v0    # "child":Landroid/view/View;
    .end local v2    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    :cond_1
    return-void
.end method

.method private requestParentDisallowInterceptTouchEvent(Z)V
    .locals 1
    .param p1, "disallowIntercept"    # Z

    #@0
    .prologue
    .line 2055
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getParent()Landroid/view/ViewParent;

    #@3
    move-result-object v0

    #@4
    .line 2056
    .local v0, "parent":Landroid/view/ViewParent;
    if-eqz v0, :cond_0

    #@6
    .line 2057
    invoke-interface {v0, p1}, Landroid/view/ViewParent;->requestDisallowInterceptTouchEvent(Z)V

    #@9
    .line 2059
    :cond_0
    return-void
.end method

.method private scrollToItem(IZIZ)V
    .locals 8
    .param p1, "item"    # I
    .param p2, "smoothScroll"    # Z
    .param p3, "velocity"    # I
    .param p4, "dispatchSelected"    # Z

    #@0
    .prologue
    const/4 v7, 0x0

    #@1
    .line 562
    invoke-virtual {p0, p1}, Landroid/support/v4/view/ViewPager;->infoForPosition(I)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@4
    move-result-object v0

    #@5
    .line 563
    .local v0, "curInfo":Landroid/support/v4/view/ViewPager$ItemInfo;
    const/4 v1, 0x0

    #@6
    .line 564
    .local v1, "destX":I
    if-eqz v0, :cond_0

    #@8
    .line 565
    invoke-direct {p0}, Landroid/support/v4/view/ViewPager;->getClientWidth()I

    #@b
    move-result v2

    #@c
    .line 566
    .local v2, "width":I
    int-to-float v3, v2

    #@d
    iget v4, p0, Landroid/support/v4/view/ViewPager;->mFirstOffset:F

    #@f
    iget v5, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@11
    iget v6, p0, Landroid/support/v4/view/ViewPager;->mLastOffset:F

    #@13
    invoke-static {v5, v6}, Ljava/lang/Math;->min(FF)F

    #@16
    move-result v5

    #@17
    invoke-static {v4, v5}, Ljava/lang/Math;->max(FF)F

    #@1a
    move-result v4

    #@1b
    mul-float/2addr v3, v4

    #@1c
    float-to-int v1, v3

    #@1d
    .line 569
    .end local v2    # "width":I
    :cond_0
    if-eqz p2, :cond_3

    #@1f
    .line 570
    invoke-virtual {p0, v1, v7, p3}, Landroid/support/v4/view/ViewPager;->smoothScrollTo(III)V

    #@22
    .line 571
    if-eqz p4, :cond_1

    #@24
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mOnPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@26
    if-eqz v3, :cond_1

    #@28
    .line 572
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mOnPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@2a
    invoke-interface {v3, p1}, Landroid/support/v4/view/ViewPager$OnPageChangeListener;->onPageSelected(I)V

    #@2d
    .line 574
    :cond_1
    if-eqz p4, :cond_2

    #@2f
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mInternalPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@31
    if-eqz v3, :cond_2

    #@33
    .line 575
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mInternalPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@35
    invoke-interface {v3, p1}, Landroid/support/v4/view/ViewPager$OnPageChangeListener;->onPageSelected(I)V

    #@38
    .line 588
    :cond_2
    :goto_0
    return-void

    #@39
    .line 578
    :cond_3
    if-eqz p4, :cond_4

    #@3b
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mOnPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@3d
    if-eqz v3, :cond_4

    #@3f
    .line 579
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mOnPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@41
    invoke-interface {v3, p1}, Landroid/support/v4/view/ViewPager$OnPageChangeListener;->onPageSelected(I)V

    #@44
    .line 581
    :cond_4
    if-eqz p4, :cond_5

    #@46
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mInternalPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@48
    if-eqz v3, :cond_5

    #@4a
    .line 582
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mInternalPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@4c
    invoke-interface {v3, p1}, Landroid/support/v4/view/ViewPager$OnPageChangeListener;->onPageSelected(I)V

    #@4f
    .line 584
    :cond_5
    invoke-direct {p0, v7}, Landroid/support/v4/view/ViewPager;->completeScroll(Z)V

    #@52
    .line 585
    invoke-virtual {p0, v1, v7}, Landroid/support/v4/view/ViewPager;->scrollTo(II)V

    #@55
    .line 586
    invoke-direct {p0, v1}, Landroid/support/v4/view/ViewPager;->pageScrolled(I)Z

    #@58
    goto :goto_0
.end method

.method private setScrollState(I)V
    .locals 1
    .param p1, "newState"    # I

    #@0
    .prologue
    .line 393
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mScrollState:I

    #@2
    if-ne v0, p1, :cond_1

    #@4
    .line 405
    :cond_0
    :goto_0
    return-void

    #@5
    .line 397
    :cond_1
    iput p1, p0, Landroid/support/v4/view/ViewPager;->mScrollState:I

    #@7
    .line 398
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mPageTransformer:Landroid/support/v4/view/ViewPager$PageTransformer;

    #@9
    if-eqz v0, :cond_2

    #@b
    .line 400
    if-eqz p1, :cond_3

    #@d
    const/4 v0, 0x1

    #@e
    :goto_1
    invoke-direct {p0, v0}, Landroid/support/v4/view/ViewPager;->enableLayers(Z)V

    #@11
    .line 402
    :cond_2
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mOnPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@13
    if-eqz v0, :cond_0

    #@15
    .line 403
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mOnPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@17
    invoke-interface {v0, p1}, Landroid/support/v4/view/ViewPager$OnPageChangeListener;->onPageScrollStateChanged(I)V

    #@1a
    goto :goto_0

    #@1b
    .line 400
    :cond_3
    const/4 v0, 0x0

    #@1c
    goto :goto_1
.end method

.method private setScrollingCacheEnabled(Z)V
    .locals 1
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 2414
    iget-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mScrollingCacheEnabled:Z

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 2415
    iput-boolean p1, p0, Landroid/support/v4/view/ViewPager;->mScrollingCacheEnabled:Z

    #@6
    .line 2426
    :cond_0
    return-void
.end method

.method private sortChildDrawingOrder()V
    .locals 5

    #@0
    .prologue
    .line 1111
    iget v3, p0, Landroid/support/v4/view/ViewPager;->mDrawingOrder:I

    #@2
    if-eqz v3, :cond_2

    #@4
    .line 1112
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mDrawingOrderedChildren:Ljava/util/ArrayList;

    #@6
    if-nez v3, :cond_0

    #@8
    .line 1113
    new-instance v3, Ljava/util/ArrayList;

    #@a
    invoke-direct {v3}, Ljava/util/ArrayList;-><init>()V

    #@d
    iput-object v3, p0, Landroid/support/v4/view/ViewPager;->mDrawingOrderedChildren:Ljava/util/ArrayList;

    #@f
    .line 1117
    :goto_0
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@12
    move-result v1

    #@13
    .line 1118
    .local v1, "childCount":I
    const/4 v2, 0x0

    #@14
    .local v2, "i":I
    :goto_1
    if-ge v2, v1, :cond_1

    #@16
    .line 1119
    invoke-virtual {p0, v2}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@19
    move-result-object v0

    #@1a
    .line 1120
    .local v0, "child":Landroid/view/View;
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mDrawingOrderedChildren:Ljava/util/ArrayList;

    #@1c
    invoke-virtual {v3, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@1f
    .line 1118
    add-int/lit8 v2, v2, 0x1

    #@21
    goto :goto_1

    #@22
    .line 1115
    .end local v0    # "child":Landroid/view/View;
    .end local v1    # "childCount":I
    .end local v2    # "i":I
    :cond_0
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mDrawingOrderedChildren:Ljava/util/ArrayList;

    #@24
    invoke-virtual {v3}, Ljava/util/ArrayList;->clear()V

    #@27
    goto :goto_0

    #@28
    .line 1122
    .restart local v1    # "childCount":I
    .restart local v2    # "i":I
    :cond_1
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mDrawingOrderedChildren:Ljava/util/ArrayList;

    #@2a
    sget-object v4, Landroid/support/v4/view/ViewPager;->sPositionComparator:Landroid/support/v4/view/ViewPager$ViewPositionComparator;

    #@2c
    invoke-static {v3, v4}, Ljava/util/Collections;->sort(Ljava/util/List;Ljava/util/Comparator;)V

    #@2f
    .line 1124
    .end local v1    # "childCount":I
    .end local v2    # "i":I
    :cond_2
    return-void
.end method


# virtual methods
.method public addFocusables(Ljava/util/ArrayList;II)V
    .locals 7
    .param p2, "direction"    # I
    .param p3, "focusableMode"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;II)V"
        }
    .end annotation

    #@0
    .prologue
    .line 2631
    .local p1, "views":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    #@3
    move-result v2

    #@4
    .line 2633
    .local v2, "focusableCount":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getDescendantFocusability()I

    #@7
    move-result v1

    #@8
    .line 2635
    .local v1, "descendantFocusability":I
    const/high16 v5, 0x60000

    #@a
    if-eq v1, v5, :cond_1

    #@c
    .line 2636
    const/4 v3, 0x0

    #@d
    .local v3, "i":I
    :goto_0
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@10
    move-result v5

    #@11
    if-ge v3, v5, :cond_1

    #@13
    .line 2637
    invoke-virtual {p0, v3}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@16
    move-result-object v0

    #@17
    .line 2638
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getVisibility()I

    #@1a
    move-result v5

    #@1b
    if-nez v5, :cond_0

    #@1d
    .line 2639
    invoke-virtual {p0, v0}, Landroid/support/v4/view/ViewPager;->infoForChild(Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@20
    move-result-object v4

    #@21
    .line 2640
    .local v4, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    if-eqz v4, :cond_0

    #@23
    iget v5, v4, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@25
    iget v6, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@27
    if-ne v5, v6, :cond_0

    #@29
    .line 2641
    invoke-virtual {v0, p1, p2, p3}, Landroid/view/View;->addFocusables(Ljava/util/ArrayList;II)V

    #@2c
    .line 2636
    .end local v4    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_0
    add-int/lit8 v3, v3, 0x1

    #@2e
    goto :goto_0

    #@2f
    .line 2651
    .end local v0    # "child":Landroid/view/View;
    .end local v3    # "i":I
    :cond_1
    const/high16 v5, 0x40000

    #@31
    if-ne v1, v5, :cond_2

    #@33
    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    #@36
    move-result v5

    #@37
    if-ne v2, v5, :cond_3

    #@39
    .line 2657
    :cond_2
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->isFocusable()Z

    #@3c
    move-result v5

    #@3d
    if-nez v5, :cond_4

    #@3f
    .line 2668
    :cond_3
    :goto_1
    return-void

    #@40
    .line 2660
    :cond_4
    and-int/lit8 v5, p3, 0x1

    #@42
    const/4 v6, 0x1

    #@43
    if-ne v5, v6, :cond_5

    #@45
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->isInTouchMode()Z

    #@48
    move-result v5

    #@49
    if-eqz v5, :cond_5

    #@4b
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->isFocusableInTouchMode()Z

    #@4e
    move-result v5

    #@4f
    if-eqz v5, :cond_3

    #@51
    .line 2664
    :cond_5
    if-eqz p1, :cond_3

    #@53
    .line 2665
    invoke-virtual {p1, p0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@56
    goto :goto_1
.end method

.method addNewItem(II)Landroid/support/v4/view/ViewPager$ItemInfo;
    .locals 2
    .param p1, "position"    # I
    .param p2, "index"    # I

    #@0
    .prologue
    .line 835
    new-instance v0, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@2
    invoke-direct {v0}, Landroid/support/v4/view/ViewPager$ItemInfo;-><init>()V

    #@5
    .line 836
    .local v0, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    iput p1, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@7
    .line 837
    iget-object v1, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@9
    invoke-virtual {v1, p0, p1}, Landroid/support/v4/view/PagerAdapter;->instantiateItem(Landroid/view/ViewGroup;I)Ljava/lang/Object;

    #@c
    move-result-object v1

    #@d
    iput-object v1, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->object:Ljava/lang/Object;

    #@f
    .line 838
    iget-object v1, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@11
    invoke-virtual {v1, p1}, Landroid/support/v4/view/PagerAdapter;->getPageWidth(I)F

    #@14
    move-result v1

    #@15
    iput v1, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@17
    .line 839
    if-ltz p2, :cond_0

    #@19
    iget-object v1, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@1b
    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I

    #@1e
    move-result v1

    #@1f
    if-lt p2, v1, :cond_1

    #@21
    .line 840
    :cond_0
    iget-object v1, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@23
    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@26
    .line 844
    :goto_0
    return-object v0

    #@27
    .line 842
    :cond_1
    iget-object v1, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@29
    invoke-virtual {v1, p2, v0}, Ljava/util/ArrayList;->add(ILjava/lang/Object;)V

    #@2c
    goto :goto_0
.end method

.method public addTouchables(Ljava/util/ArrayList;)V
    .locals 5
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 2678
    .local p1, "views":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    const/4 v1, 0x0

    #@1
    .local v1, "i":I
    :goto_0
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@4
    move-result v3

    #@5
    if-ge v1, v3, :cond_1

    #@7
    .line 2679
    invoke-virtual {p0, v1}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@a
    move-result-object v0

    #@b
    .line 2680
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getVisibility()I

    #@e
    move-result v3

    #@f
    if-nez v3, :cond_0

    #@11
    .line 2681
    invoke-virtual {p0, v0}, Landroid/support/v4/view/ViewPager;->infoForChild(Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@14
    move-result-object v2

    #@15
    .line 2682
    .local v2, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    if-eqz v2, :cond_0

    #@17
    iget v3, v2, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@19
    iget v4, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@1b
    if-ne v3, v4, :cond_0

    #@1d
    .line 2683
    invoke-virtual {v0, p1}, Landroid/view/View;->addTouchables(Ljava/util/ArrayList;)V

    #@20
    .line 2678
    .end local v2    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@22
    goto :goto_0

    #@23
    .line 2687
    .end local v0    # "child":Landroid/view/View;
    :cond_1
    return-void
.end method

.method public addView(Landroid/view/View;ILandroid/view/ViewGroup$LayoutParams;)V
    .locals 3
    .param p1, "child"    # Landroid/view/View;
    .param p2, "index"    # I
    .param p3, "params"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 1297
    invoke-virtual {p0, p3}, Landroid/support/v4/view/ViewPager;->checkLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Z

    #@3
    move-result v1

    #@4
    if-nez v1, :cond_0

    #@6
    .line 1298
    invoke-virtual {p0, p3}, Landroid/support/v4/view/ViewPager;->generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/view/ViewGroup$LayoutParams;

    #@9
    move-result-object p3

    #@a
    :cond_0
    move-object v0, p3

    #@b
    .line 1300
    check-cast v0, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@d
    .line 1301
    .local v0, "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    iget-boolean v1, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@f
    instance-of v2, p1, Landroid/support/v4/view/ViewPager$Decor;

    #@11
    or-int/2addr v1, v2

    #@12
    iput-boolean v1, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@14
    .line 1302
    iget-boolean v1, p0, Landroid/support/v4/view/ViewPager;->mInLayout:Z

    #@16
    if-eqz v1, :cond_2

    #@18
    .line 1303
    if-eqz v0, :cond_1

    #@1a
    iget-boolean v1, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@1c
    if-eqz v1, :cond_1

    #@1e
    .line 1304
    new-instance v1, Ljava/lang/IllegalStateException;

    #@20
    const-string v2, "Cannot add pager decor view during layout"

    #@22
    invoke-direct {v1, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@25
    throw v1

    #@26
    .line 1306
    :cond_1
    const/4 v1, 0x1

    #@27
    iput-boolean v1, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->needsMeasure:Z

    #@29
    .line 1307
    invoke-virtual {p0, p1, p2, p3}, Landroid/support/v4/view/ViewPager;->addViewInLayout(Landroid/view/View;ILandroid/view/ViewGroup$LayoutParams;)Z

    #@2c
    .line 1319
    :goto_0
    return-void

    #@2d
    .line 1309
    :cond_2
    invoke-super {p0, p1, p2, p3}, Landroid/view/ViewGroup;->addView(Landroid/view/View;ILandroid/view/ViewGroup$LayoutParams;)V

    #@30
    goto :goto_0
.end method

.method public arrowScroll(I)Z
    .locals 13
    .param p1, "direction"    # I

    #@0
    .prologue
    const/16 v12, 0x42

    #@2
    const/16 v11, 0x11

    #@4
    .line 2519
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->findFocus()Landroid/view/View;

    #@7
    move-result-object v1

    #@8
    .line 2520
    .local v1, "currentFocused":Landroid/view/View;
    if-ne v1, p0, :cond_3

    #@a
    .line 2521
    const/4 v1, 0x0

    #@b
    .line 2545
    :cond_0
    :goto_0
    const/4 v2, 0x0

    #@c
    .line 2547
    .local v2, "handled":Z
    invoke-static {}, Landroid/view/FocusFinder;->getInstance()Landroid/view/FocusFinder;

    #@f
    move-result-object v8

    #@10
    invoke-virtual {v8, p0, v1, p1}, Landroid/view/FocusFinder;->findNextFocus(Landroid/view/ViewGroup;Landroid/view/View;I)Landroid/view/View;

    #@13
    move-result-object v4

    #@14
    .line 2549
    .local v4, "nextFocused":Landroid/view/View;
    if-eqz v4, :cond_a

    #@16
    if-eq v4, v1, :cond_a

    #@18
    .line 2550
    if-ne p1, v11, :cond_8

    #@1a
    .line 2553
    iget-object v8, p0, Landroid/support/v4/view/ViewPager;->mTempRect:Landroid/graphics/Rect;

    #@1c
    invoke-direct {p0, v8, v4}, Landroid/support/v4/view/ViewPager;->getChildRectInPagerCoordinates(Landroid/graphics/Rect;Landroid/view/View;)Landroid/graphics/Rect;

    #@1f
    move-result-object v8

    #@20
    iget v5, v8, Landroid/graphics/Rect;->left:I

    #@22
    .line 2554
    .local v5, "nextLeft":I
    iget-object v8, p0, Landroid/support/v4/view/ViewPager;->mTempRect:Landroid/graphics/Rect;

    #@24
    invoke-direct {p0, v8, v1}, Landroid/support/v4/view/ViewPager;->getChildRectInPagerCoordinates(Landroid/graphics/Rect;Landroid/view/View;)Landroid/graphics/Rect;

    #@27
    move-result-object v8

    #@28
    iget v0, v8, Landroid/graphics/Rect;->left:I

    #@2a
    .line 2555
    .local v0, "currLeft":I
    if-eqz v1, :cond_7

    #@2c
    if-lt v5, v0, :cond_7

    #@2e
    .line 2556
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->pageLeft()Z

    #@31
    move-result v2

    #@32
    .line 2578
    .end local v0    # "currLeft":I
    .end local v5    # "nextLeft":I
    :cond_1
    :goto_1
    if-eqz v2, :cond_2

    #@34
    .line 2579
    invoke-static {p1}, Landroid/view/SoundEffectConstants;->getContantForFocusDirection(I)I

    #@37
    move-result v8

    #@38
    invoke-virtual {p0, v8}, Landroid/support/v4/view/ViewPager;->playSoundEffect(I)V

    #@3b
    .line 2581
    :cond_2
    return v2

    #@3c
    .line 2522
    .end local v2    # "handled":Z
    .end local v4    # "nextFocused":Landroid/view/View;
    :cond_3
    if-eqz v1, :cond_0

    #@3e
    .line 2523
    const/4 v3, 0x0

    #@3f
    .line 2524
    .local v3, "isChild":Z
    invoke-virtual {v1}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@42
    move-result-object v6

    #@43
    .local v6, "parent":Landroid/view/ViewParent;
    :goto_2
    instance-of v8, v6, Landroid/view/ViewGroup;

    #@45
    if-eqz v8, :cond_4

    #@47
    .line 2526
    if-ne v6, p0, :cond_5

    #@49
    .line 2527
    const/4 v3, 0x1

    #@4a
    .line 2531
    :cond_4
    if-nez v3, :cond_0

    #@4c
    .line 2533
    new-instance v7, Ljava/lang/StringBuilder;

    #@4e
    invoke-direct {v7}, Ljava/lang/StringBuilder;-><init>()V

    #@51
    .line 2534
    .local v7, "sb":Ljava/lang/StringBuilder;
    invoke-virtual {v1}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@54
    move-result-object v8

    #@55
    invoke-virtual {v8}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    #@58
    move-result-object v8

    #@59
    invoke-virtual {v7, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@5c
    .line 2535
    invoke-virtual {v1}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@5f
    move-result-object v6

    #@60
    :goto_3
    instance-of v8, v6, Landroid/view/ViewGroup;

    #@62
    if-eqz v8, :cond_6

    #@64
    .line 2537
    const-string v8, " => "

    #@66
    invoke-virtual {v7, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@69
    move-result-object v8

    #@6a
    invoke-virtual {v6}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@6d
    move-result-object v9

    #@6e
    invoke-virtual {v9}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    #@71
    move-result-object v9

    #@72
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@75
    .line 2536
    invoke-interface {v6}, Landroid/view/ViewParent;->getParent()Landroid/view/ViewParent;

    #@78
    move-result-object v6

    #@79
    goto :goto_3

    #@7a
    .line 2525
    .end local v7    # "sb":Ljava/lang/StringBuilder;
    :cond_5
    invoke-interface {v6}, Landroid/view/ViewParent;->getParent()Landroid/view/ViewParent;

    #@7d
    move-result-object v6

    #@7e
    goto :goto_2

    #@7f
    .line 2539
    .restart local v7    # "sb":Ljava/lang/StringBuilder;
    :cond_6
    const-string v8, "ViewPager"

    #@81
    new-instance v9, Ljava/lang/StringBuilder;

    #@83
    invoke-direct {v9}, Ljava/lang/StringBuilder;-><init>()V

    #@86
    const-string v10, "arrowScroll tried to find focus based on non-child current focused view "

    #@88
    invoke-virtual {v9, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@8b
    move-result-object v9

    #@8c
    invoke-virtual {v7}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@8f
    move-result-object v10

    #@90
    invoke-virtual {v9, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@93
    move-result-object v9

    #@94
    invoke-virtual {v9}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@97
    move-result-object v9

    #@98
    invoke-static {v8, v9}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@9b
    .line 2541
    const/4 v1, 0x0

    #@9c
    goto/16 :goto_0

    #@9e
    .line 2558
    .end local v3    # "isChild":Z
    .end local v6    # "parent":Landroid/view/ViewParent;
    .end local v7    # "sb":Ljava/lang/StringBuilder;
    .restart local v0    # "currLeft":I
    .restart local v2    # "handled":Z
    .restart local v4    # "nextFocused":Landroid/view/View;
    .restart local v5    # "nextLeft":I
    :cond_7
    invoke-virtual {v4}, Landroid/view/View;->requestFocus()Z

    #@a1
    move-result v2

    #@a2
    goto :goto_1

    #@a3
    .line 2560
    .end local v0    # "currLeft":I
    .end local v5    # "nextLeft":I
    :cond_8
    if-ne p1, v12, :cond_1

    #@a5
    .line 2563
    iget-object v8, p0, Landroid/support/v4/view/ViewPager;->mTempRect:Landroid/graphics/Rect;

    #@a7
    invoke-direct {p0, v8, v4}, Landroid/support/v4/view/ViewPager;->getChildRectInPagerCoordinates(Landroid/graphics/Rect;Landroid/view/View;)Landroid/graphics/Rect;

    #@aa
    move-result-object v8

    #@ab
    iget v5, v8, Landroid/graphics/Rect;->left:I

    #@ad
    .line 2564
    .restart local v5    # "nextLeft":I
    iget-object v8, p0, Landroid/support/v4/view/ViewPager;->mTempRect:Landroid/graphics/Rect;

    #@af
    invoke-direct {p0, v8, v1}, Landroid/support/v4/view/ViewPager;->getChildRectInPagerCoordinates(Landroid/graphics/Rect;Landroid/view/View;)Landroid/graphics/Rect;

    #@b2
    move-result-object v8

    #@b3
    iget v0, v8, Landroid/graphics/Rect;->left:I

    #@b5
    .line 2565
    .restart local v0    # "currLeft":I
    if-eqz v1, :cond_9

    #@b7
    if-gt v5, v0, :cond_9

    #@b9
    .line 2566
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->pageRight()Z

    #@bc
    move-result v2

    #@bd
    goto/16 :goto_1

    #@bf
    .line 2568
    :cond_9
    invoke-virtual {v4}, Landroid/view/View;->requestFocus()Z

    #@c2
    move-result v2

    #@c3
    goto/16 :goto_1

    #@c5
    .line 2571
    .end local v0    # "currLeft":I
    .end local v5    # "nextLeft":I
    :cond_a
    if-eq p1, v11, :cond_b

    #@c7
    const/4 v8, 0x1

    #@c8
    if-ne p1, v8, :cond_c

    #@ca
    .line 2573
    :cond_b
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->pageLeft()Z

    #@cd
    move-result v2

    #@ce
    goto/16 :goto_1

    #@d0
    .line 2574
    :cond_c
    if-eq p1, v12, :cond_d

    #@d2
    const/4 v8, 0x2

    #@d3
    if-ne p1, v8, :cond_1

    #@d5
    .line 2576
    :cond_d
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->pageRight()Z

    #@d8
    move-result v2

    #@d9
    goto/16 :goto_1
.end method

.method public beginFakeDrag()Z
    .locals 10

    #@0
    .prologue
    const/4 v9, 0x1

    #@1
    const/4 v4, 0x0

    #@2
    const/4 v5, 0x0

    #@3
    .line 2278
    iget-boolean v2, p0, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@5
    if-eqz v2, :cond_0

    #@7
    .line 2294
    :goto_0
    return v4

    #@8
    .line 2281
    :cond_0
    iput-boolean v9, p0, Landroid/support/v4/view/ViewPager;->mFakeDragging:Z

    #@a
    .line 2282
    invoke-direct {p0, v9}, Landroid/support/v4/view/ViewPager;->setScrollState(I)V

    #@d
    .line 2283
    iput v5, p0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@f
    iput v5, p0, Landroid/support/v4/view/ViewPager;->mInitialMotionX:F

    #@11
    .line 2284
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@13
    if-nez v2, :cond_1

    #@15
    .line 2285
    invoke-static {}, Landroid/view/VelocityTracker;->obtain()Landroid/view/VelocityTracker;

    #@18
    move-result-object v2

    #@19
    iput-object v2, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@1b
    .line 2289
    :goto_1
    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    #@1e
    move-result-wide v0

    #@1f
    .local v0, "time":J
    move-wide v2, v0

    #@20
    move v6, v5

    #@21
    move v7, v4

    #@22
    .line 2290
    invoke-static/range {v0 .. v7}, Landroid/view/MotionEvent;->obtain(JJIFFI)Landroid/view/MotionEvent;

    #@25
    move-result-object v8

    #@26
    .line 2291
    .local v8, "ev":Landroid/view/MotionEvent;
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@28
    invoke-virtual {v2, v8}, Landroid/view/VelocityTracker;->addMovement(Landroid/view/MotionEvent;)V

    #@2b
    .line 2292
    invoke-virtual {v8}, Landroid/view/MotionEvent;->recycle()V

    #@2e
    .line 2293
    iput-wide v0, p0, Landroid/support/v4/view/ViewPager;->mFakeDragBeginTime:J

    #@30
    move v4, v9

    #@31
    .line 2294
    goto :goto_0

    #@32
    .line 2287
    .end local v0    # "time":J
    .end local v8    # "ev":Landroid/view/MotionEvent;
    :cond_1
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@34
    invoke-virtual {v2}, Landroid/view/VelocityTracker;->clear()V

    #@37
    goto :goto_1
.end method

.method protected canScroll(Landroid/view/View;ZIII)Z
    .locals 11
    .param p1, "v"    # Landroid/view/View;
    .param p2, "checkV"    # Z
    .param p3, "dx"    # I
    .param p4, "x"    # I
    .param p5, "y"    # I

    #@0
    .prologue
    .line 2456
    instance-of v0, p1, Landroid/view/ViewGroup;

    #@2
    if-eqz v0, :cond_1

    #@4
    move-object v7, p1

    #@5
    .line 2457
    check-cast v7, Landroid/view/ViewGroup;

    #@7
    .line 2458
    .local v7, "group":Landroid/view/ViewGroup;
    invoke-virtual {p1}, Landroid/view/View;->getScrollX()I

    #@a
    move-result v9

    #@b
    .line 2459
    .local v9, "scrollX":I
    invoke-virtual {p1}, Landroid/view/View;->getScrollY()I

    #@e
    move-result v10

    #@f
    .line 2460
    .local v10, "scrollY":I
    invoke-virtual {v7}, Landroid/view/ViewGroup;->getChildCount()I

    #@12
    move-result v6

    #@13
    .line 2462
    .local v6, "count":I
    add-int/lit8 v8, v6, -0x1

    #@15
    .local v8, "i":I
    :goto_0
    if-ltz v8, :cond_1

    #@17
    .line 2465
    invoke-virtual {v7, v8}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    #@1a
    move-result-object v1

    #@1b
    .line 2466
    .local v1, "child":Landroid/view/View;
    add-int v0, p4, v9

    #@1d
    invoke-virtual {v1}, Landroid/view/View;->getLeft()I

    #@20
    move-result v2

    #@21
    if-lt v0, v2, :cond_0

    #@23
    add-int v0, p4, v9

    #@25
    invoke-virtual {v1}, Landroid/view/View;->getRight()I

    #@28
    move-result v2

    #@29
    if-ge v0, v2, :cond_0

    #@2b
    add-int v0, p5, v10

    #@2d
    invoke-virtual {v1}, Landroid/view/View;->getTop()I

    #@30
    move-result v2

    #@31
    if-lt v0, v2, :cond_0

    #@33
    add-int v0, p5, v10

    #@35
    invoke-virtual {v1}, Landroid/view/View;->getBottom()I

    #@38
    move-result v2

    #@39
    if-ge v0, v2, :cond_0

    #@3b
    const/4 v2, 0x1

    #@3c
    add-int v0, p4, v9

    #@3e
    invoke-virtual {v1}, Landroid/view/View;->getLeft()I

    #@41
    move-result v3

    #@42
    sub-int v4, v0, v3

    #@44
    add-int v0, p5, v10

    #@46
    invoke-virtual {v1}, Landroid/view/View;->getTop()I

    #@49
    move-result v3

    #@4a
    sub-int v5, v0, v3

    #@4c
    move-object v0, p0

    #@4d
    move v3, p3

    #@4e
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/view/ViewPager;->canScroll(Landroid/view/View;ZIII)Z

    #@51
    move-result v0

    #@52
    if-eqz v0, :cond_0

    #@54
    .line 2470
    const/4 v0, 0x1

    #@55
    .line 2475
    .end local v1    # "child":Landroid/view/View;
    .end local v6    # "count":I
    .end local v7    # "group":Landroid/view/ViewGroup;
    .end local v8    # "i":I
    .end local v9    # "scrollX":I
    .end local v10    # "scrollY":I
    :goto_1
    return v0

    #@56
    .line 2462
    .restart local v1    # "child":Landroid/view/View;
    .restart local v6    # "count":I
    .restart local v7    # "group":Landroid/view/ViewGroup;
    .restart local v8    # "i":I
    .restart local v9    # "scrollX":I
    .restart local v10    # "scrollY":I
    :cond_0
    add-int/lit8 v8, v8, -0x1

    #@58
    goto :goto_0

    #@59
    .line 2475
    .end local v1    # "child":Landroid/view/View;
    .end local v6    # "count":I
    .end local v7    # "group":Landroid/view/ViewGroup;
    .end local v8    # "i":I
    .end local v9    # "scrollX":I
    .end local v10    # "scrollY":I
    :cond_1
    if-eqz p2, :cond_2

    #@5b
    neg-int v0, p3

    #@5c
    invoke-static {p1, v0}, Landroid/support/v4/view/ViewCompat;->canScrollHorizontally(Landroid/view/View;I)Z

    #@5f
    move-result v0

    #@60
    if-eqz v0, :cond_2

    #@62
    const/4 v0, 0x1

    #@63
    goto :goto_1

    #@64
    :cond_2
    const/4 v0, 0x0

    #@65
    goto :goto_1
.end method

.method public canScrollHorizontally(I)Z
    .locals 6
    .param p1, "direction"    # I

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    const/4 v3, 0x0

    #@2
    .line 2429
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@4
    if-nez v4, :cond_1

    #@6
    .line 2440
    :cond_0
    :goto_0
    return v3

    #@7
    .line 2433
    :cond_1
    invoke-direct {p0}, Landroid/support/v4/view/ViewPager;->getClientWidth()I

    #@a
    move-result v1

    #@b
    .line 2434
    .local v1, "width":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@e
    move-result v0

    #@f
    .line 2435
    .local v0, "scrollX":I
    if-gez p1, :cond_3

    #@11
    .line 2436
    int-to-float v4, v1

    #@12
    iget v5, p0, Landroid/support/v4/view/ViewPager;->mFirstOffset:F

    #@14
    mul-float/2addr v4, v5

    #@15
    float-to-int v4, v4

    #@16
    if-le v0, v4, :cond_2

    #@18
    :goto_1
    move v3, v2

    #@19
    goto :goto_0

    #@1a
    :cond_2
    move v2, v3

    #@1b
    goto :goto_1

    #@1c
    .line 2437
    :cond_3
    if-lez p1, :cond_0

    #@1e
    .line 2438
    int-to-float v4, v1

    #@1f
    iget v5, p0, Landroid/support/v4/view/ViewPager;->mLastOffset:F

    #@21
    mul-float/2addr v4, v5

    #@22
    float-to-int v4, v4

    #@23
    if-ge v0, v4, :cond_4

    #@25
    :goto_2
    move v3, v2

    #@26
    goto :goto_0

    #@27
    :cond_4
    move v2, v3

    #@28
    goto :goto_2
.end method

.method protected checkLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Z
    .locals 1
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 2757
    instance-of v0, p1, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@2
    if-eqz v0, :cond_0

    #@4
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->checkLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Z

    #@7
    move-result v0

    #@8
    if-eqz v0, :cond_0

    #@a
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_0
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method public computeScroll()V
    .locals 5

    #@0
    .prologue
    .line 1612
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@2
    invoke-virtual {v4}, Landroid/widget/Scroller;->isFinished()Z

    #@5
    move-result v4

    #@6
    if-nez v4, :cond_2

    #@8
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@a
    invoke-virtual {v4}, Landroid/widget/Scroller;->computeScrollOffset()Z

    #@d
    move-result v4

    #@e
    if-eqz v4, :cond_2

    #@10
    .line 1613
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@13
    move-result v0

    #@14
    .line 1614
    .local v0, "oldX":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getScrollY()I

    #@17
    move-result v1

    #@18
    .line 1615
    .local v1, "oldY":I
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@1a
    invoke-virtual {v4}, Landroid/widget/Scroller;->getCurrX()I

    #@1d
    move-result v2

    #@1e
    .line 1616
    .local v2, "x":I
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@20
    invoke-virtual {v4}, Landroid/widget/Scroller;->getCurrY()I

    #@23
    move-result v3

    #@24
    .line 1618
    .local v3, "y":I
    if-ne v0, v2, :cond_0

    #@26
    if-eq v1, v3, :cond_1

    #@28
    .line 1619
    :cond_0
    invoke-virtual {p0, v2, v3}, Landroid/support/v4/view/ViewPager;->scrollTo(II)V

    #@2b
    .line 1620
    invoke-direct {p0, v2}, Landroid/support/v4/view/ViewPager;->pageScrolled(I)Z

    #@2e
    move-result v4

    #@2f
    if-nez v4, :cond_1

    #@31
    .line 1621
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@33
    invoke-virtual {v4}, Landroid/widget/Scroller;->abortAnimation()V

    #@36
    .line 1622
    const/4 v4, 0x0

    #@37
    invoke-virtual {p0, v4, v3}, Landroid/support/v4/view/ViewPager;->scrollTo(II)V

    #@3a
    .line 1627
    :cond_1
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->postInvalidateOnAnimation(Landroid/view/View;)V

    #@3d
    .line 1633
    .end local v0    # "oldX":I
    .end local v1    # "oldY":I
    .end local v2    # "x":I
    .end local v3    # "y":I
    :goto_0
    return-void

    #@3e
    .line 1632
    :cond_2
    const/4 v4, 0x1

    #@3f
    invoke-direct {p0, v4}, Landroid/support/v4/view/ViewPager;->completeScroll(Z)V

    #@42
    goto :goto_0
.end method

.method dataSetChanged()V
    .locals 15

    #@0
    .prologue
    const/4 v10, 0x1

    #@1
    const/4 v11, 0x0

    #@2
    .line 850
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@4
    invoke-virtual {v12}, Landroid/support/v4/view/PagerAdapter;->getCount()I

    #@7
    move-result v0

    #@8
    .line 851
    .local v0, "adapterCount":I
    iput v0, p0, Landroid/support/v4/view/ViewPager;->mExpectedAdapterCount:I

    #@a
    .line 852
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@c
    invoke-virtual {v12}, Ljava/util/ArrayList;->size()I

    #@f
    move-result v12

    #@10
    iget v13, p0, Landroid/support/v4/view/ViewPager;->mOffscreenPageLimit:I

    #@12
    mul-int/lit8 v13, v13, 0x2

    #@14
    add-int/lit8 v13, v13, 0x1

    #@16
    if-ge v12, v13, :cond_1

    #@18
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@1a
    invoke-virtual {v12}, Ljava/util/ArrayList;->size()I

    #@1d
    move-result v12

    #@1e
    if-ge v12, v0, :cond_1

    #@20
    move v7, v10

    #@21
    .line 854
    .local v7, "needPopulate":Z
    :goto_0
    iget v8, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@23
    .line 856
    .local v8, "newCurrItem":I
    const/4 v5, 0x0

    #@24
    .line 857
    .local v5, "isUpdating":Z
    const/4 v3, 0x0

    #@25
    .local v3, "i":I
    :goto_1
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@27
    invoke-virtual {v12}, Ljava/util/ArrayList;->size()I

    #@2a
    move-result v12

    #@2b
    if-ge v3, v12, :cond_6

    #@2d
    .line 858
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@2f
    invoke-virtual {v12, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@32
    move-result-object v4

    #@33
    check-cast v4, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@35
    .line 859
    .local v4, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@37
    iget-object v13, v4, Landroid/support/v4/view/ViewPager$ItemInfo;->object:Ljava/lang/Object;

    #@39
    invoke-virtual {v12, v13}, Landroid/support/v4/view/PagerAdapter;->getItemPosition(Ljava/lang/Object;)I

    #@3c
    move-result v9

    #@3d
    .line 861
    .local v9, "newPos":I
    const/4 v12, -0x1

    #@3e
    if-ne v9, v12, :cond_2

    #@40
    .line 857
    :cond_0
    :goto_2
    add-int/lit8 v3, v3, 0x1

    #@42
    goto :goto_1

    #@43
    .end local v3    # "i":I
    .end local v4    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v5    # "isUpdating":Z
    .end local v7    # "needPopulate":Z
    .end local v8    # "newCurrItem":I
    .end local v9    # "newPos":I
    :cond_1
    move v7, v11

    #@44
    .line 852
    goto :goto_0

    #@45
    .line 865
    .restart local v3    # "i":I
    .restart local v4    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .restart local v5    # "isUpdating":Z
    .restart local v7    # "needPopulate":Z
    .restart local v8    # "newCurrItem":I
    .restart local v9    # "newPos":I
    :cond_2
    const/4 v12, -0x2

    #@46
    if-ne v9, v12, :cond_4

    #@48
    .line 866
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@4a
    invoke-virtual {v12, v3}, Ljava/util/ArrayList;->remove(I)Ljava/lang/Object;

    #@4d
    .line 867
    add-int/lit8 v3, v3, -0x1

    #@4f
    .line 869
    if-nez v5, :cond_3

    #@51
    .line 870
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@53
    invoke-virtual {v12, p0}, Landroid/support/v4/view/PagerAdapter;->startUpdate(Landroid/view/ViewGroup;)V

    #@56
    .line 871
    const/4 v5, 0x1

    #@57
    .line 874
    :cond_3
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@59
    iget v13, v4, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@5b
    iget-object v14, v4, Landroid/support/v4/view/ViewPager$ItemInfo;->object:Ljava/lang/Object;

    #@5d
    invoke-virtual {v12, p0, v13, v14}, Landroid/support/v4/view/PagerAdapter;->destroyItem(Landroid/view/ViewGroup;ILjava/lang/Object;)V

    #@60
    .line 875
    const/4 v7, 0x1

    #@61
    .line 877
    iget v12, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@63
    iget v13, v4, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@65
    if-ne v12, v13, :cond_0

    #@67
    .line 879
    iget v12, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@69
    add-int/lit8 v13, v0, -0x1

    #@6b
    invoke-static {v12, v13}, Ljava/lang/Math;->min(II)I

    #@6e
    move-result v12

    #@6f
    invoke-static {v11, v12}, Ljava/lang/Math;->max(II)I

    #@72
    move-result v8

    #@73
    .line 880
    const/4 v7, 0x1

    #@74
    goto :goto_2

    #@75
    .line 885
    :cond_4
    iget v12, v4, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@77
    if-eq v12, v9, :cond_0

    #@79
    .line 886
    iget v12, v4, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@7b
    iget v13, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@7d
    if-ne v12, v13, :cond_5

    #@7f
    .line 888
    move v8, v9

    #@80
    .line 891
    :cond_5
    iput v9, v4, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@82
    .line 892
    const/4 v7, 0x1

    #@83
    goto :goto_2

    #@84
    .line 896
    .end local v4    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v9    # "newPos":I
    :cond_6
    if-eqz v5, :cond_7

    #@86
    .line 897
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@88
    invoke-virtual {v12, p0}, Landroid/support/v4/view/PagerAdapter;->finishUpdate(Landroid/view/ViewGroup;)V

    #@8b
    .line 900
    :cond_7
    iget-object v12, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@8d
    sget-object v13, Landroid/support/v4/view/ViewPager;->COMPARATOR:Ljava/util/Comparator;

    #@8f
    invoke-static {v12, v13}, Ljava/util/Collections;->sort(Ljava/util/List;Ljava/util/Comparator;)V

    #@92
    .line 902
    if-eqz v7, :cond_a

    #@94
    .line 904
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@97
    move-result v2

    #@98
    .line 905
    .local v2, "childCount":I
    const/4 v3, 0x0

    #@99
    :goto_3
    if-ge v3, v2, :cond_9

    #@9b
    .line 906
    invoke-virtual {p0, v3}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@9e
    move-result-object v1

    #@9f
    .line 907
    .local v1, "child":Landroid/view/View;
    invoke-virtual {v1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@a2
    move-result-object v6

    #@a3
    check-cast v6, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@a5
    .line 908
    .local v6, "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    iget-boolean v12, v6, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@a7
    if-nez v12, :cond_8

    #@a9
    .line 909
    const/4 v12, 0x0

    #@aa
    iput v12, v6, Landroid/support/v4/view/ViewPager$LayoutParams;->widthFactor:F

    #@ac
    .line 905
    :cond_8
    add-int/lit8 v3, v3, 0x1

    #@ae
    goto :goto_3

    #@af
    .line 913
    .end local v1    # "child":Landroid/view/View;
    .end local v6    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    :cond_9
    invoke-virtual {p0, v8, v11, v10}, Landroid/support/v4/view/ViewPager;->setCurrentItemInternal(IZZ)V

    #@b2
    .line 914
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->requestLayout()V

    #@b5
    .line 916
    .end local v2    # "childCount":I
    :cond_a
    return-void
.end method

.method public dispatchKeyEvent(Landroid/view/KeyEvent;)Z
    .locals 1
    .param p1, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 2481
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->dispatchKeyEvent(Landroid/view/KeyEvent;)Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    invoke-virtual {p0, p1}, Landroid/support/v4/view/ViewPager;->executeKeyEvent(Landroid/view/KeyEvent;)Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_1

    #@c
    :cond_0
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_1
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public dispatchPopulateAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)Z
    .locals 6
    .param p1, "event"    # Landroid/view/accessibility/AccessibilityEvent;

    #@0
    .prologue
    .line 2725
    invoke-virtual {p1}, Landroid/view/accessibility/AccessibilityEvent;->getEventType()I

    #@3
    move-result v4

    #@4
    const/16 v5, 0x1000

    #@6
    if-ne v4, v5, :cond_0

    #@8
    .line 2726
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->dispatchPopulateAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)Z

    #@b
    move-result v4

    #@c
    .line 2742
    :goto_0
    return v4

    #@d
    .line 2730
    :cond_0
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@10
    move-result v1

    #@11
    .line 2731
    .local v1, "childCount":I
    const/4 v2, 0x0

    #@12
    .local v2, "i":I
    :goto_1
    if-ge v2, v1, :cond_2

    #@14
    .line 2732
    invoke-virtual {p0, v2}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@17
    move-result-object v0

    #@18
    .line 2733
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getVisibility()I

    #@1b
    move-result v4

    #@1c
    if-nez v4, :cond_1

    #@1e
    .line 2734
    invoke-virtual {p0, v0}, Landroid/support/v4/view/ViewPager;->infoForChild(Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@21
    move-result-object v3

    #@22
    .line 2735
    .local v3, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    if-eqz v3, :cond_1

    #@24
    iget v4, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@26
    iget v5, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@28
    if-ne v4, v5, :cond_1

    #@2a
    invoke-virtual {v0, p1}, Landroid/view/View;->dispatchPopulateAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)Z

    #@2d
    move-result v4

    #@2e
    if-eqz v4, :cond_1

    #@30
    .line 2737
    const/4 v4, 0x1

    #@31
    goto :goto_0

    #@32
    .line 2731
    .end local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_1
    add-int/lit8 v2, v2, 0x1

    #@34
    goto :goto_1

    #@35
    .line 2742
    .end local v0    # "child":Landroid/view/View;
    :cond_2
    const/4 v4, 0x0

    #@36
    goto :goto_0
.end method

.method distanceInfluenceForSnapDuration(F)F
    .locals 4
    .param p1, "f"    # F

    #@0
    .prologue
    .line 771
    const/high16 v0, 0x3f000000    # 0.5f

    #@2
    sub-float/2addr p1, v0

    #@3
    .line 772
    float-to-double v0, p1

    #@4
    const-wide v2, 0x3fde28c7460698c7L    # 0.4712389167638204

    #@9
    mul-double/2addr v0, v2

    #@a
    double-to-float p1, v0

    #@b
    .line 773
    float-to-double v0, p1

    #@c
    invoke-static {v0, v1}, Ljava/lang/Math;->sin(D)D

    #@f
    move-result-wide v0

    #@10
    double-to-float v0, v0

    #@11
    return v0
.end method

.method public draw(Landroid/graphics/Canvas;)V
    .locals 8
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    const/4 v6, 0x1

    #@1
    .line 2176
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->draw(Landroid/graphics/Canvas;)V

    #@4
    .line 2177
    const/4 v1, 0x0

    #@5
    .line 2179
    .local v1, "needsInvalidate":Z
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getOverScrollMode(Landroid/view/View;)I

    #@8
    move-result v2

    #@9
    .line 2180
    .local v2, "overScrollMode":I
    if-eqz v2, :cond_0

    #@b
    if-ne v2, v6, :cond_4

    #@d
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@f
    if-eqz v5, :cond_4

    #@11
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@13
    invoke-virtual {v5}, Landroid/support/v4/view/PagerAdapter;->getCount()I

    #@16
    move-result v5

    #@17
    if-le v5, v6, :cond_4

    #@19
    .line 2183
    :cond_0
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mLeftEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@1b
    invoke-virtual {v5}, Landroid/support/v4/widget/EdgeEffectCompat;->isFinished()Z

    #@1e
    move-result v5

    #@1f
    if-nez v5, :cond_1

    #@21
    .line 2184
    invoke-virtual {p1}, Landroid/graphics/Canvas;->save()I

    #@24
    move-result v3

    #@25
    .line 2185
    .local v3, "restoreCount":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getHeight()I

    #@28
    move-result v5

    #@29
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingTop()I

    #@2c
    move-result v6

    #@2d
    sub-int/2addr v5, v6

    #@2e
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingBottom()I

    #@31
    move-result v6

    #@32
    sub-int v0, v5, v6

    #@34
    .line 2186
    .local v0, "height":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getWidth()I

    #@37
    move-result v4

    #@38
    .line 2188
    .local v4, "width":I
    const/high16 v5, 0x43870000    # 270.0f

    #@3a
    invoke-virtual {p1, v5}, Landroid/graphics/Canvas;->rotate(F)V

    #@3d
    .line 2189
    neg-int v5, v0

    #@3e
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingTop()I

    #@41
    move-result v6

    #@42
    add-int/2addr v5, v6

    #@43
    int-to-float v5, v5

    #@44
    iget v6, p0, Landroid/support/v4/view/ViewPager;->mFirstOffset:F

    #@46
    int-to-float v7, v4

    #@47
    mul-float/2addr v6, v7

    #@48
    invoke-virtual {p1, v5, v6}, Landroid/graphics/Canvas;->translate(FF)V

    #@4b
    .line 2190
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mLeftEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@4d
    invoke-virtual {v5, v0, v4}, Landroid/support/v4/widget/EdgeEffectCompat;->setSize(II)V

    #@50
    .line 2191
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mLeftEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@52
    invoke-virtual {v5, p1}, Landroid/support/v4/widget/EdgeEffectCompat;->draw(Landroid/graphics/Canvas;)Z

    #@55
    move-result v5

    #@56
    or-int/2addr v1, v5

    #@57
    .line 2192
    invoke-virtual {p1, v3}, Landroid/graphics/Canvas;->restoreToCount(I)V

    #@5a
    .line 2194
    .end local v0    # "height":I
    .end local v3    # "restoreCount":I
    .end local v4    # "width":I
    :cond_1
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mRightEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@5c
    invoke-virtual {v5}, Landroid/support/v4/widget/EdgeEffectCompat;->isFinished()Z

    #@5f
    move-result v5

    #@60
    if-nez v5, :cond_2

    #@62
    .line 2195
    invoke-virtual {p1}, Landroid/graphics/Canvas;->save()I

    #@65
    move-result v3

    #@66
    .line 2196
    .restart local v3    # "restoreCount":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getWidth()I

    #@69
    move-result v4

    #@6a
    .line 2197
    .restart local v4    # "width":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getHeight()I

    #@6d
    move-result v5

    #@6e
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingTop()I

    #@71
    move-result v6

    #@72
    sub-int/2addr v5, v6

    #@73
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingBottom()I

    #@76
    move-result v6

    #@77
    sub-int v0, v5, v6

    #@79
    .line 2199
    .restart local v0    # "height":I
    const/high16 v5, 0x42b40000    # 90.0f

    #@7b
    invoke-virtual {p1, v5}, Landroid/graphics/Canvas;->rotate(F)V

    #@7e
    .line 2200
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getPaddingTop()I

    #@81
    move-result v5

    #@82
    neg-int v5, v5

    #@83
    int-to-float v5, v5

    #@84
    iget v6, p0, Landroid/support/v4/view/ViewPager;->mLastOffset:F

    #@86
    const/high16 v7, 0x3f800000    # 1.0f

    #@88
    add-float/2addr v6, v7

    #@89
    neg-float v6, v6

    #@8a
    int-to-float v7, v4

    #@8b
    mul-float/2addr v6, v7

    #@8c
    invoke-virtual {p1, v5, v6}, Landroid/graphics/Canvas;->translate(FF)V

    #@8f
    .line 2201
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mRightEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@91
    invoke-virtual {v5, v0, v4}, Landroid/support/v4/widget/EdgeEffectCompat;->setSize(II)V

    #@94
    .line 2202
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mRightEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@96
    invoke-virtual {v5, p1}, Landroid/support/v4/widget/EdgeEffectCompat;->draw(Landroid/graphics/Canvas;)Z

    #@99
    move-result v5

    #@9a
    or-int/2addr v1, v5

    #@9b
    .line 2203
    invoke-virtual {p1, v3}, Landroid/graphics/Canvas;->restoreToCount(I)V

    #@9e
    .line 2210
    .end local v0    # "height":I
    .end local v3    # "restoreCount":I
    .end local v4    # "width":I
    :cond_2
    :goto_0
    if-eqz v1, :cond_3

    #@a0
    .line 2212
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->postInvalidateOnAnimation(Landroid/view/View;)V

    #@a3
    .line 2214
    :cond_3
    return-void

    #@a4
    .line 2206
    :cond_4
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mLeftEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@a6
    invoke-virtual {v5}, Landroid/support/v4/widget/EdgeEffectCompat;->finish()V

    #@a9
    .line 2207
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mRightEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@ab
    invoke-virtual {v5}, Landroid/support/v4/widget/EdgeEffectCompat;->finish()V

    #@ae
    goto :goto_0
.end method

.method protected drawableStateChanged()V
    .locals 2

    #@0
    .prologue
    .line 759
    invoke-super {p0}, Landroid/view/ViewGroup;->drawableStateChanged()V

    #@3
    .line 760
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mMarginDrawable:Landroid/graphics/drawable/Drawable;

    #@5
    .line 761
    .local v0, "d":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_0

    #@7
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable;->isStateful()Z

    #@a
    move-result v1

    #@b
    if-eqz v1, :cond_0

    #@d
    .line 762
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getDrawableState()[I

    #@10
    move-result-object v1

    #@11
    invoke-virtual {v0, v1}, Landroid/graphics/drawable/Drawable;->setState([I)Z

    #@14
    .line 764
    :cond_0
    return-void
.end method

.method public endFakeDrag()V
    .locals 12

    #@0
    .prologue
    const/4 v11, 0x1

    #@1
    .line 2304
    iget-boolean v9, p0, Landroid/support/v4/view/ViewPager;->mFakeDragging:Z

    #@3
    if-nez v9, :cond_0

    #@5
    .line 2305
    new-instance v9, Ljava/lang/IllegalStateException;

    #@7
    const-string v10, "No fake drag in progress. Call beginFakeDrag first."

    #@9
    invoke-direct {v9, v10}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@c
    throw v9

    #@d
    .line 2308
    :cond_0
    iget-object v7, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@f
    .line 2309
    .local v7, "velocityTracker":Landroid/view/VelocityTracker;
    const/16 v9, 0x3e8

    #@11
    iget v10, p0, Landroid/support/v4/view/ViewPager;->mMaximumVelocity:I

    #@13
    int-to-float v10, v10

    #@14
    invoke-virtual {v7, v9, v10}, Landroid/view/VelocityTracker;->computeCurrentVelocity(IF)V

    #@17
    .line 2310
    iget v9, p0, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@19
    invoke-static {v7, v9}, Landroid/support/v4/view/VelocityTrackerCompat;->getXVelocity(Landroid/view/VelocityTracker;I)F

    #@1c
    move-result v9

    #@1d
    float-to-int v2, v9

    #@1e
    .line 2312
    .local v2, "initialVelocity":I
    iput-boolean v11, p0, Landroid/support/v4/view/ViewPager;->mPopulatePending:Z

    #@20
    .line 2313
    invoke-direct {p0}, Landroid/support/v4/view/ViewPager;->getClientWidth()I

    #@23
    move-result v8

    #@24
    .line 2314
    .local v8, "width":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@27
    move-result v5

    #@28
    .line 2315
    .local v5, "scrollX":I
    invoke-direct {p0}, Landroid/support/v4/view/ViewPager;->infoForCurrentScrollPosition()Landroid/support/v4/view/ViewPager$ItemInfo;

    #@2b
    move-result-object v1

    #@2c
    .line 2316
    .local v1, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    iget v0, v1, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@2e
    .line 2317
    .local v0, "currentPage":I
    int-to-float v9, v5

    #@2f
    int-to-float v10, v8

    #@30
    div-float/2addr v9, v10

    #@31
    iget v10, v1, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@33
    sub-float/2addr v9, v10

    #@34
    iget v10, v1, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@36
    div-float v4, v9, v10

    #@38
    .line 2318
    .local v4, "pageOffset":F
    iget v9, p0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@3a
    iget v10, p0, Landroid/support/v4/view/ViewPager;->mInitialMotionX:F

    #@3c
    sub-float/2addr v9, v10

    #@3d
    float-to-int v6, v9

    #@3e
    .line 2319
    .local v6, "totalDelta":I
    invoke-direct {p0, v0, v4, v2, v6}, Landroid/support/v4/view/ViewPager;->determineTargetPage(IFII)I

    #@41
    move-result v3

    #@42
    .line 2321
    .local v3, "nextPage":I
    invoke-virtual {p0, v3, v11, v11, v2}, Landroid/support/v4/view/ViewPager;->setCurrentItemInternal(IZZI)V

    #@45
    .line 2322
    invoke-direct {p0}, Landroid/support/v4/view/ViewPager;->endDrag()V

    #@48
    .line 2324
    const/4 v9, 0x0

    #@49
    iput-boolean v9, p0, Landroid/support/v4/view/ViewPager;->mFakeDragging:Z

    #@4b
    .line 2325
    return-void
.end method

.method public executeKeyEvent(Landroid/view/KeyEvent;)Z
    .locals 4
    .param p1, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 2493
    const/4 v0, 0x0

    #@2
    .line 2494
    .local v0, "handled":Z
    invoke-virtual {p1}, Landroid/view/KeyEvent;->getAction()I

    #@5
    move-result v1

    #@6
    if-nez v1, :cond_0

    #@8
    .line 2495
    invoke-virtual {p1}, Landroid/view/KeyEvent;->getKeyCode()I

    #@b
    move-result v1

    #@c
    sparse-switch v1, :sswitch_data_0

    #@f
    .line 2515
    :cond_0
    :goto_0
    return v0

    #@10
    .line 2497
    :sswitch_0
    const/16 v1, 0x11

    #@12
    invoke-virtual {p0, v1}, Landroid/support/v4/view/ViewPager;->arrowScroll(I)Z

    #@15
    move-result v0

    #@16
    .line 2498
    goto :goto_0

    #@17
    .line 2500
    :sswitch_1
    const/16 v1, 0x42

    #@19
    invoke-virtual {p0, v1}, Landroid/support/v4/view/ViewPager;->arrowScroll(I)Z

    #@1c
    move-result v0

    #@1d
    .line 2501
    goto :goto_0

    #@1e
    .line 2503
    :sswitch_2
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@20
    const/16 v2, 0xb

    #@22
    if-lt v1, v2, :cond_0

    #@24
    .line 2506
    invoke-static {p1}, Landroid/support/v4/view/KeyEventCompat;->hasNoModifiers(Landroid/view/KeyEvent;)Z

    #@27
    move-result v1

    #@28
    if-eqz v1, :cond_1

    #@2a
    .line 2507
    const/4 v1, 0x2

    #@2b
    invoke-virtual {p0, v1}, Landroid/support/v4/view/ViewPager;->arrowScroll(I)Z

    #@2e
    move-result v0

    #@2f
    goto :goto_0

    #@30
    .line 2508
    :cond_1
    invoke-static {p1, v3}, Landroid/support/v4/view/KeyEventCompat;->hasModifiers(Landroid/view/KeyEvent;I)Z

    #@33
    move-result v1

    #@34
    if-eqz v1, :cond_0

    #@36
    .line 2509
    invoke-virtual {p0, v3}, Landroid/support/v4/view/ViewPager;->arrowScroll(I)Z

    #@39
    move-result v0

    #@3a
    goto :goto_0

    #@3b
    .line 2495
    nop

    #@3c
    :sswitch_data_0
    .sparse-switch
        0x15 -> :sswitch_0
        0x16 -> :sswitch_1
        0x3d -> :sswitch_2
    .end sparse-switch
.end method

.method public fakeDragBy(F)V
    .locals 18
    .param p1, "xOffset"    # F

    #@0
    .prologue
    .line 2335
    move-object/from16 v0, p0

    #@2
    iget-boolean v2, v0, Landroid/support/v4/view/ViewPager;->mFakeDragging:Z

    #@4
    if-nez v2, :cond_0

    #@6
    .line 2336
    new-instance v2, Ljava/lang/IllegalStateException;

    #@8
    const-string v3, "No fake drag in progress. Call beginFakeDrag first."

    #@a
    invoke-direct {v2, v3}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@d
    throw v2

    #@e
    .line 2339
    :cond_0
    move-object/from16 v0, p0

    #@10
    iget v2, v0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@12
    add-float v2, v2, p1

    #@14
    move-object/from16 v0, p0

    #@16
    iput v2, v0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@18
    .line 2341
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@1b
    move-result v2

    #@1c
    int-to-float v14, v2

    #@1d
    .line 2342
    .local v14, "oldScrollX":F
    sub-float v16, v14, p1

    #@1f
    .line 2343
    .local v16, "scrollX":F
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getClientWidth()I

    #@22
    move-result v17

    #@23
    .line 2345
    .local v17, "width":I
    move/from16 v0, v17

    #@25
    int-to-float v2, v0

    #@26
    move-object/from16 v0, p0

    #@28
    iget v3, v0, Landroid/support/v4/view/ViewPager;->mFirstOffset:F

    #@2a
    mul-float v13, v2, v3

    #@2c
    .line 2346
    .local v13, "leftBound":F
    move/from16 v0, v17

    #@2e
    int-to-float v2, v0

    #@2f
    move-object/from16 v0, p0

    #@31
    iget v3, v0, Landroid/support/v4/view/ViewPager;->mLastOffset:F

    #@33
    mul-float v15, v2, v3

    #@35
    .line 2348
    .local v15, "rightBound":F
    move-object/from16 v0, p0

    #@37
    iget-object v2, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@39
    const/4 v3, 0x0

    #@3a
    invoke-virtual {v2, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@3d
    move-result-object v11

    #@3e
    check-cast v11, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@40
    .line 2349
    .local v11, "firstItem":Landroid/support/v4/view/ViewPager$ItemInfo;
    move-object/from16 v0, p0

    #@42
    iget-object v2, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@44
    move-object/from16 v0, p0

    #@46
    iget-object v3, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@48
    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    #@4b
    move-result v3

    #@4c
    add-int/lit8 v3, v3, -0x1

    #@4e
    invoke-virtual {v2, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@51
    move-result-object v12

    #@52
    check-cast v12, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@54
    .line 2350
    .local v12, "lastItem":Landroid/support/v4/view/ViewPager$ItemInfo;
    iget v2, v11, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@56
    if-eqz v2, :cond_1

    #@58
    .line 2351
    iget v2, v11, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@5a
    move/from16 v0, v17

    #@5c
    int-to-float v3, v0

    #@5d
    mul-float v13, v2, v3

    #@5f
    .line 2353
    :cond_1
    iget v2, v12, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@61
    move-object/from16 v0, p0

    #@63
    iget-object v3, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@65
    invoke-virtual {v3}, Landroid/support/v4/view/PagerAdapter;->getCount()I

    #@68
    move-result v3

    #@69
    add-int/lit8 v3, v3, -0x1

    #@6b
    if-eq v2, v3, :cond_2

    #@6d
    .line 2354
    iget v2, v12, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@6f
    move/from16 v0, v17

    #@71
    int-to-float v3, v0

    #@72
    mul-float v15, v2, v3

    #@74
    .line 2357
    :cond_2
    cmpg-float v2, v16, v13

    #@76
    if-gez v2, :cond_4

    #@78
    .line 2358
    move/from16 v16, v13

    #@7a
    .line 2363
    :cond_3
    :goto_0
    move-object/from16 v0, p0

    #@7c
    iget v2, v0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@7e
    move/from16 v0, v16

    #@80
    float-to-int v3, v0

    #@81
    int-to-float v3, v3

    #@82
    sub-float v3, v16, v3

    #@84
    add-float/2addr v2, v3

    #@85
    move-object/from16 v0, p0

    #@87
    iput v2, v0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@89
    .line 2364
    move/from16 v0, v16

    #@8b
    float-to-int v2, v0

    #@8c
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getScrollY()I

    #@8f
    move-result v3

    #@90
    move-object/from16 v0, p0

    #@92
    invoke-virtual {v0, v2, v3}, Landroid/support/v4/view/ViewPager;->scrollTo(II)V

    #@95
    .line 2365
    move/from16 v0, v16

    #@97
    float-to-int v2, v0

    #@98
    move-object/from16 v0, p0

    #@9a
    invoke-direct {v0, v2}, Landroid/support/v4/view/ViewPager;->pageScrolled(I)Z

    #@9d
    .line 2368
    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    #@a0
    move-result-wide v4

    #@a1
    .line 2369
    .local v4, "time":J
    move-object/from16 v0, p0

    #@a3
    iget-wide v2, v0, Landroid/support/v4/view/ViewPager;->mFakeDragBeginTime:J

    #@a5
    const/4 v6, 0x2

    #@a6
    move-object/from16 v0, p0

    #@a8
    iget v7, v0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@aa
    const/4 v8, 0x0

    #@ab
    const/4 v9, 0x0

    #@ac
    invoke-static/range {v2 .. v9}, Landroid/view/MotionEvent;->obtain(JJIFFI)Landroid/view/MotionEvent;

    #@af
    move-result-object v10

    #@b0
    .line 2371
    .local v10, "ev":Landroid/view/MotionEvent;
    move-object/from16 v0, p0

    #@b2
    iget-object v2, v0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@b4
    invoke-virtual {v2, v10}, Landroid/view/VelocityTracker;->addMovement(Landroid/view/MotionEvent;)V

    #@b7
    .line 2372
    invoke-virtual {v10}, Landroid/view/MotionEvent;->recycle()V

    #@ba
    .line 2373
    return-void

    #@bb
    .line 2359
    .end local v4    # "time":J
    .end local v10    # "ev":Landroid/view/MotionEvent;
    :cond_4
    cmpl-float v2, v16, v15

    #@bd
    if-lez v2, :cond_3

    #@bf
    .line 2360
    move/from16 v16, v15

    #@c1
    goto :goto_0
.end method

.method protected generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams;
    .locals 1

    #@0
    .prologue
    .line 2747
    new-instance v0, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@2
    invoke-direct {v0}, Landroid/support/v4/view/ViewPager$LayoutParams;-><init>()V

    #@5
    return-object v0
.end method

.method public generateLayoutParams(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams;
    .locals 2
    .param p1, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 2762
    new-instance v0, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@2
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1, p1}, Landroid/support/v4/view/ViewPager$LayoutParams;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@9
    return-object v0
.end method

.method protected generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/view/ViewGroup$LayoutParams;
    .locals 1
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 2752
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public getAdapter()Landroid/support/v4/view/PagerAdapter;
    .locals 1

    #@0
    .prologue
    .line 475
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@2
    return-object v0
.end method

.method protected getChildDrawingOrder(II)I
    .locals 4
    .param p1, "childCount"    # I
    .param p2, "i"    # I

    #@0
    .prologue
    .line 647
    iget v2, p0, Landroid/support/v4/view/ViewPager;->mDrawingOrder:I

    #@2
    const/4 v3, 0x2

    #@3
    if-ne v2, v3, :cond_0

    #@5
    add-int/lit8 v2, p1, -0x1

    #@7
    sub-int v0, v2, p2

    #@9
    .line 648
    .local v0, "index":I
    :goto_0
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mDrawingOrderedChildren:Ljava/util/ArrayList;

    #@b
    invoke-virtual {v2, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@e
    move-result-object v2

    #@f
    check-cast v2, Landroid/view/View;

    #@11
    invoke-virtual {v2}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@14
    move-result-object v2

    #@15
    check-cast v2, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@17
    iget v1, v2, Landroid/support/v4/view/ViewPager$LayoutParams;->childIndex:I

    #@19
    .line 649
    .local v1, "result":I
    return v1

    #@1a
    .end local v0    # "index":I
    .end local v1    # "result":I
    :cond_0
    move v0, p2

    #@1b
    .line 647
    goto :goto_0
.end method

.method public getCurrentItem()I
    .locals 1

    #@0
    .prologue
    .line 510
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@2
    return v0
.end method

.method public getOffscreenPageLimit()I
    .locals 1

    #@0
    .prologue
    .line 672
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mOffscreenPageLimit:I

    #@2
    return v0
.end method

.method public getPageMargin()I
    .locals 1

    #@0
    .prologue
    .line 728
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@2
    return v0
.end method

.method infoForAnyChild(Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;
    .locals 2
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 1342
    :goto_0
    invoke-virtual {p1}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@3
    move-result-object v0

    #@4
    .local v0, "parent":Landroid/view/ViewParent;
    if-eq v0, p0, :cond_2

    #@6
    .line 1343
    if-eqz v0, :cond_0

    #@8
    instance-of v1, v0, Landroid/view/View;

    #@a
    if-nez v1, :cond_1

    #@c
    .line 1344
    :cond_0
    const/4 v1, 0x0

    #@d
    .line 1348
    :goto_1
    return-object v1

    #@e
    :cond_1
    move-object p1, v0

    #@f
    .line 1346
    check-cast p1, Landroid/view/View;

    #@11
    goto :goto_0

    #@12
    .line 1348
    :cond_2
    invoke-virtual {p0, p1}, Landroid/support/v4/view/ViewPager;->infoForChild(Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@15
    move-result-object v1

    #@16
    goto :goto_1
.end method

.method infoForChild(Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;
    .locals 4
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 1331
    const/4 v0, 0x0

    #@1
    .local v0, "i":I
    :goto_0
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@3
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@6
    move-result v2

    #@7
    if-ge v0, v2, :cond_1

    #@9
    .line 1332
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@b
    invoke-virtual {v2, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@e
    move-result-object v1

    #@f
    check-cast v1, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@11
    .line 1333
    .local v1, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@13
    iget-object v3, v1, Landroid/support/v4/view/ViewPager$ItemInfo;->object:Ljava/lang/Object;

    #@15
    invoke-virtual {v2, p1, v3}, Landroid/support/v4/view/PagerAdapter;->isViewFromObject(Landroid/view/View;Ljava/lang/Object;)Z

    #@18
    move-result v2

    #@19
    if-eqz v2, :cond_0

    #@1b
    .line 1337
    .end local v1    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :goto_1
    return-object v1

    #@1c
    .line 1331
    .restart local v1    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_0
    add-int/lit8 v0, v0, 0x1

    #@1e
    goto :goto_0

    #@1f
    .line 1337
    .end local v1    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_1
    const/4 v1, 0x0

    #@20
    goto :goto_1
.end method

.method infoForPosition(I)Landroid/support/v4/view/ViewPager$ItemInfo;
    .locals 3
    .param p1, "position"    # I

    #@0
    .prologue
    .line 1352
    const/4 v0, 0x0

    #@1
    .local v0, "i":I
    :goto_0
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@3
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@6
    move-result v2

    #@7
    if-ge v0, v2, :cond_1

    #@9
    .line 1353
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@b
    invoke-virtual {v2, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@e
    move-result-object v1

    #@f
    check-cast v1, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@11
    .line 1354
    .local v1, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    iget v2, v1, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@13
    if-ne v2, p1, :cond_0

    #@15
    .line 1358
    .end local v1    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :goto_1
    return-object v1

    #@16
    .line 1352
    .restart local v1    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_0
    add-int/lit8 v0, v0, 0x1

    #@18
    goto :goto_0

    #@19
    .line 1358
    .end local v1    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_1
    const/4 v1, 0x0

    #@1a
    goto :goto_1
.end method

.method initViewPager()V
    .locals 6

    #@0
    .prologue
    const/4 v5, 0x1

    #@1
    .line 359
    const/4 v3, 0x0

    #@2
    invoke-virtual {p0, v3}, Landroid/support/v4/view/ViewPager;->setWillNotDraw(Z)V

    #@5
    .line 360
    const/high16 v3, 0x40000

    #@7
    invoke-virtual {p0, v3}, Landroid/support/v4/view/ViewPager;->setDescendantFocusability(I)V

    #@a
    .line 361
    invoke-virtual {p0, v5}, Landroid/support/v4/view/ViewPager;->setFocusable(Z)V

    #@d
    .line 362
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getContext()Landroid/content/Context;

    #@10
    move-result-object v1

    #@11
    .line 363
    .local v1, "context":Landroid/content/Context;
    new-instance v3, Landroid/widget/Scroller;

    #@13
    sget-object v4, Landroid/support/v4/view/ViewPager;->sInterpolator:Landroid/view/animation/Interpolator;

    #@15
    invoke-direct {v3, v1, v4}, Landroid/widget/Scroller;-><init>(Landroid/content/Context;Landroid/view/animation/Interpolator;)V

    #@18
    iput-object v3, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@1a
    .line 364
    invoke-static {v1}, Landroid/view/ViewConfiguration;->get(Landroid/content/Context;)Landroid/view/ViewConfiguration;

    #@1d
    move-result-object v0

    #@1e
    .line 365
    .local v0, "configuration":Landroid/view/ViewConfiguration;
    invoke-virtual {v1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@21
    move-result-object v3

    #@22
    invoke-virtual {v3}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@25
    move-result-object v3

    #@26
    iget v2, v3, Landroid/util/DisplayMetrics;->density:F

    #@28
    .line 367
    .local v2, "density":F
    invoke-static {v0}, Landroid/support/v4/view/ViewConfigurationCompat;->getScaledPagingTouchSlop(Landroid/view/ViewConfiguration;)I

    #@2b
    move-result v3

    #@2c
    iput v3, p0, Landroid/support/v4/view/ViewPager;->mTouchSlop:I

    #@2e
    .line 368
    const/high16 v3, 0x43c80000    # 400.0f

    #@30
    mul-float/2addr v3, v2

    #@31
    float-to-int v3, v3

    #@32
    iput v3, p0, Landroid/support/v4/view/ViewPager;->mMinimumVelocity:I

    #@34
    .line 369
    invoke-virtual {v0}, Landroid/view/ViewConfiguration;->getScaledMaximumFlingVelocity()I

    #@37
    move-result v3

    #@38
    iput v3, p0, Landroid/support/v4/view/ViewPager;->mMaximumVelocity:I

    #@3a
    .line 370
    new-instance v3, Landroid/support/v4/widget/EdgeEffectCompat;

    #@3c
    invoke-direct {v3, v1}, Landroid/support/v4/widget/EdgeEffectCompat;-><init>(Landroid/content/Context;)V

    #@3f
    iput-object v3, p0, Landroid/support/v4/view/ViewPager;->mLeftEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@41
    .line 371
    new-instance v3, Landroid/support/v4/widget/EdgeEffectCompat;

    #@43
    invoke-direct {v3, v1}, Landroid/support/v4/widget/EdgeEffectCompat;-><init>(Landroid/content/Context;)V

    #@46
    iput-object v3, p0, Landroid/support/v4/view/ViewPager;->mRightEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@48
    .line 373
    const/high16 v3, 0x41c80000    # 25.0f

    #@4a
    mul-float/2addr v3, v2

    #@4b
    float-to-int v3, v3

    #@4c
    iput v3, p0, Landroid/support/v4/view/ViewPager;->mFlingDistance:I

    #@4e
    .line 374
    const/high16 v3, 0x40000000    # 2.0f

    #@50
    mul-float/2addr v3, v2

    #@51
    float-to-int v3, v3

    #@52
    iput v3, p0, Landroid/support/v4/view/ViewPager;->mCloseEnough:I

    #@54
    .line 375
    const/high16 v3, 0x41800000    # 16.0f

    #@56
    mul-float/2addr v3, v2

    #@57
    float-to-int v3, v3

    #@58
    iput v3, p0, Landroid/support/v4/view/ViewPager;->mDefaultGutterSize:I

    #@5a
    .line 377
    new-instance v3, Landroid/support/v4/view/ViewPager$MyAccessibilityDelegate;

    #@5c
    invoke-direct {v3, p0}, Landroid/support/v4/view/ViewPager$MyAccessibilityDelegate;-><init>(Landroid/support/v4/view/ViewPager;)V

    #@5f
    invoke-static {p0, v3}, Landroid/support/v4/view/ViewCompat;->setAccessibilityDelegate(Landroid/view/View;Landroid/support/v4/view/AccessibilityDelegateCompat;)V

    #@62
    .line 379
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getImportantForAccessibility(Landroid/view/View;)I

    #@65
    move-result v3

    #@66
    if-nez v3, :cond_0

    #@68
    .line 381
    invoke-static {p0, v5}, Landroid/support/v4/view/ViewCompat;->setImportantForAccessibility(Landroid/view/View;I)V

    #@6b
    .line 384
    :cond_0
    return-void
.end method

.method public isFakeDragging()Z
    .locals 1

    #@0
    .prologue
    .line 2385
    iget-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mFakeDragging:Z

    #@2
    return v0
.end method

.method protected onAttachedToWindow()V
    .locals 1

    #@0
    .prologue
    .line 1363
    invoke-super {p0}, Landroid/view/ViewGroup;->onAttachedToWindow()V

    #@3
    .line 1364
    const/4 v0, 0x1

    #@4
    iput-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mFirstLayout:Z

    #@6
    .line 1365
    return-void
.end method

.method protected onDetachedFromWindow()V
    .locals 1

    #@0
    .prologue
    .line 388
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mEndScrollRunnable:Ljava/lang/Runnable;

    #@2
    invoke-virtual {p0, v0}, Landroid/support/v4/view/ViewPager;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@5
    .line 389
    invoke-super {p0}, Landroid/view/ViewGroup;->onDetachedFromWindow()V

    #@8
    .line 390
    return-void
.end method

.method protected onDraw(Landroid/graphics/Canvas;)V
    .locals 18
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 2218
    invoke-super/range {p0 .. p1}, Landroid/view/ViewGroup;->onDraw(Landroid/graphics/Canvas;)V

    #@3
    .line 2221
    move-object/from16 v0, p0

    #@5
    iget v13, v0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@7
    if-lez v13, :cond_2

    #@9
    move-object/from16 v0, p0

    #@b
    iget-object v13, v0, Landroid/support/v4/view/ViewPager;->mMarginDrawable:Landroid/graphics/drawable/Drawable;

    #@d
    if-eqz v13, :cond_2

    #@f
    move-object/from16 v0, p0

    #@11
    iget-object v13, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@13
    invoke-virtual {v13}, Ljava/util/ArrayList;->size()I

    #@16
    move-result v13

    #@17
    if-lez v13, :cond_2

    #@19
    move-object/from16 v0, p0

    #@1b
    iget-object v13, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@1d
    if-eqz v13, :cond_2

    #@1f
    .line 2222
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@22
    move-result v10

    #@23
    .line 2223
    .local v10, "scrollX":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getWidth()I

    #@26
    move-result v11

    #@27
    .line 2225
    .local v11, "width":I
    move-object/from16 v0, p0

    #@29
    iget v13, v0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@2b
    int-to-float v13, v13

    #@2c
    int-to-float v14, v11

    #@2d
    div-float v7, v13, v14

    #@2f
    .line 2226
    .local v7, "marginOffset":F
    const/4 v5, 0x0

    #@30
    .line 2227
    .local v5, "itemIndex":I
    move-object/from16 v0, p0

    #@32
    iget-object v13, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@34
    const/4 v14, 0x0

    #@35
    invoke-virtual {v13, v14}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@38
    move-result-object v3

    #@39
    check-cast v3, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@3b
    .line 2228
    .local v3, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    iget v8, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@3d
    .line 2229
    .local v8, "offset":F
    move-object/from16 v0, p0

    #@3f
    iget-object v13, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@41
    invoke-virtual {v13}, Ljava/util/ArrayList;->size()I

    #@44
    move-result v4

    #@45
    .line 2230
    .local v4, "itemCount":I
    iget v2, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@47
    .line 2231
    .local v2, "firstPos":I
    move-object/from16 v0, p0

    #@49
    iget-object v13, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@4b
    add-int/lit8 v14, v4, -0x1

    #@4d
    invoke-virtual {v13, v14}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@50
    move-result-object v13

    #@51
    check-cast v13, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@53
    iget v6, v13, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@55
    .line 2232
    .local v6, "lastPos":I
    move v9, v2

    #@56
    .local v9, "pos":I
    :goto_0
    if-ge v9, v6, :cond_2

    #@58
    .line 2233
    :goto_1
    iget v13, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@5a
    if-le v9, v13, :cond_0

    #@5c
    if-ge v5, v4, :cond_0

    #@5e
    .line 2234
    move-object/from16 v0, p0

    #@60
    iget-object v13, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@62
    add-int/lit8 v5, v5, 0x1

    #@64
    invoke-virtual {v13, v5}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@67
    move-result-object v3

    #@68
    .end local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    check-cast v3, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@6a
    .restart local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    goto :goto_1

    #@6b
    .line 2238
    :cond_0
    iget v13, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@6d
    if-ne v9, v13, :cond_3

    #@6f
    .line 2239
    iget v13, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@71
    iget v14, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@73
    add-float/2addr v13, v14

    #@74
    int-to-float v14, v11

    #@75
    mul-float v1, v13, v14

    #@77
    .line 2240
    .local v1, "drawAt":F
    iget v13, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@79
    iget v14, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@7b
    add-float/2addr v13, v14

    #@7c
    add-float v8, v13, v7

    #@7e
    .line 2247
    :goto_2
    move-object/from16 v0, p0

    #@80
    iget v13, v0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@82
    int-to-float v13, v13

    #@83
    add-float/2addr v13, v1

    #@84
    int-to-float v14, v10

    #@85
    cmpl-float v13, v13, v14

    #@87
    if-lez v13, :cond_1

    #@89
    .line 2248
    move-object/from16 v0, p0

    #@8b
    iget-object v13, v0, Landroid/support/v4/view/ViewPager;->mMarginDrawable:Landroid/graphics/drawable/Drawable;

    #@8d
    float-to-int v14, v1

    #@8e
    move-object/from16 v0, p0

    #@90
    iget v15, v0, Landroid/support/v4/view/ViewPager;->mTopPageBounds:I

    #@92
    move-object/from16 v0, p0

    #@94
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@96
    move/from16 v16, v0

    #@98
    move/from16 v0, v16

    #@9a
    int-to-float v0, v0

    #@9b
    move/from16 v16, v0

    #@9d
    add-float v16, v16, v1

    #@9f
    const/high16 v17, 0x3f000000    # 0.5f

    #@a1
    add-float v16, v16, v17

    #@a3
    move/from16 v0, v16

    #@a5
    float-to-int v0, v0

    #@a6
    move/from16 v16, v0

    #@a8
    move-object/from16 v0, p0

    #@aa
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mBottomPageBounds:I

    #@ac
    move/from16 v17, v0

    #@ae
    invoke-virtual/range {v13 .. v17}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@b1
    .line 2250
    move-object/from16 v0, p0

    #@b3
    iget-object v13, v0, Landroid/support/v4/view/ViewPager;->mMarginDrawable:Landroid/graphics/drawable/Drawable;

    #@b5
    move-object/from16 v0, p1

    #@b7
    invoke-virtual {v13, v0}, Landroid/graphics/drawable/Drawable;->draw(Landroid/graphics/Canvas;)V

    #@ba
    .line 2253
    :cond_1
    add-int v13, v10, v11

    #@bc
    int-to-float v13, v13

    #@bd
    cmpl-float v13, v1, v13

    #@bf
    if-lez v13, :cond_4

    #@c1
    .line 2258
    .end local v1    # "drawAt":F
    .end local v2    # "firstPos":I
    .end local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v4    # "itemCount":I
    .end local v5    # "itemIndex":I
    .end local v6    # "lastPos":I
    .end local v7    # "marginOffset":F
    .end local v8    # "offset":F
    .end local v9    # "pos":I
    .end local v10    # "scrollX":I
    .end local v11    # "width":I
    :cond_2
    return-void

    #@c2
    .line 2242
    .restart local v2    # "firstPos":I
    .restart local v3    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .restart local v4    # "itemCount":I
    .restart local v5    # "itemIndex":I
    .restart local v6    # "lastPos":I
    .restart local v7    # "marginOffset":F
    .restart local v8    # "offset":F
    .restart local v9    # "pos":I
    .restart local v10    # "scrollX":I
    .restart local v11    # "width":I
    :cond_3
    move-object/from16 v0, p0

    #@c4
    iget-object v13, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@c6
    invoke-virtual {v13, v9}, Landroid/support/v4/view/PagerAdapter;->getPageWidth(I)F

    #@c9
    move-result v12

    #@ca
    .line 2243
    .local v12, "widthFactor":F
    add-float v13, v8, v12

    #@cc
    int-to-float v14, v11

    #@cd
    mul-float v1, v13, v14

    #@cf
    .line 2244
    .restart local v1    # "drawAt":F
    add-float v13, v12, v7

    #@d1
    add-float/2addr v8, v13

    #@d2
    goto :goto_2

    #@d3
    .line 2232
    .end local v12    # "widthFactor":F
    :cond_4
    add-int/lit8 v9, v9, 0x1

    #@d5
    goto :goto_0
.end method

.method public onInterceptTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 14
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 1792
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getAction()I

    #@3
    move-result v0

    #@4
    and-int/lit16 v6, v0, 0xff

    #@6
    .line 1795
    .local v6, "action":I
    const/4 v0, 0x3

    #@7
    if-eq v6, v0, :cond_0

    #@9
    const/4 v0, 0x1

    #@a
    if-ne v6, v0, :cond_2

    #@c
    .line 1798
    :cond_0
    const/4 v0, 0x0

    #@d
    iput-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@f
    .line 1799
    const/4 v0, 0x0

    #@10
    iput-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsUnableToDrag:Z

    #@12
    .line 1800
    const/4 v0, -0x1

    #@13
    iput v0, p0, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@15
    .line 1801
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@17
    if-eqz v0, :cond_1

    #@19
    .line 1802
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@1b
    invoke-virtual {v0}, Landroid/view/VelocityTracker;->recycle()V

    #@1e
    .line 1803
    const/4 v0, 0x0

    #@1f
    iput-object v0, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@21
    .line 1805
    :cond_1
    const/4 v0, 0x0

    #@22
    .line 1925
    :goto_0
    return v0

    #@23
    .line 1810
    :cond_2
    if-eqz v6, :cond_4

    #@25
    .line 1811
    iget-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@27
    if-eqz v0, :cond_3

    #@29
    .line 1813
    const/4 v0, 0x1

    #@2a
    goto :goto_0

    #@2b
    .line 1815
    :cond_3
    iget-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsUnableToDrag:Z

    #@2d
    if-eqz v0, :cond_4

    #@2f
    .line 1817
    const/4 v0, 0x0

    #@30
    goto :goto_0

    #@31
    .line 1821
    :cond_4
    sparse-switch v6, :sswitch_data_0

    #@34
    .line 1916
    :cond_5
    :goto_1
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@36
    if-nez v0, :cond_6

    #@38
    .line 1917
    invoke-static {}, Landroid/view/VelocityTracker;->obtain()Landroid/view/VelocityTracker;

    #@3b
    move-result-object v0

    #@3c
    iput-object v0, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@3e
    .line 1919
    :cond_6
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@40
    invoke-virtual {v0, p1}, Landroid/view/VelocityTracker;->addMovement(Landroid/view/MotionEvent;)V

    #@43
    .line 1925
    iget-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@45
    goto :goto_0

    #@46
    .line 1832
    :sswitch_0
    iget v7, p0, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@48
    .line 1833
    .local v7, "activePointerId":I
    const/4 v0, -0x1

    #@49
    if-eq v7, v0, :cond_5

    #@4b
    .line 1838
    invoke-static {p1, v7}, Landroid/support/v4/view/MotionEventCompat;->findPointerIndex(Landroid/view/MotionEvent;I)I

    #@4e
    move-result v9

    #@4f
    .line 1839
    .local v9, "pointerIndex":I
    invoke-static {p1, v9}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@52
    move-result v10

    #@53
    .line 1840
    .local v10, "x":F
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@55
    sub-float v8, v10, v0

    #@57
    .line 1841
    .local v8, "dx":F
    invoke-static {v8}, Ljava/lang/Math;->abs(F)F

    #@5a
    move-result v11

    #@5b
    .line 1842
    .local v11, "xDiff":F
    invoke-static {p1, v9}, Landroid/support/v4/view/MotionEventCompat;->getY(Landroid/view/MotionEvent;I)F

    #@5e
    move-result v12

    #@5f
    .line 1843
    .local v12, "y":F
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mInitialMotionY:F

    #@61
    sub-float v0, v12, v0

    #@63
    invoke-static {v0}, Ljava/lang/Math;->abs(F)F

    #@66
    move-result v13

    #@67
    .line 1846
    .local v13, "yDiff":F
    const/4 v0, 0x0

    #@68
    cmpl-float v0, v8, v0

    #@6a
    if-eqz v0, :cond_7

    #@6c
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@6e
    invoke-direct {p0, v0, v8}, Landroid/support/v4/view/ViewPager;->isGutterDrag(FF)Z

    #@71
    move-result v0

    #@72
    if-nez v0, :cond_7

    #@74
    const/4 v2, 0x0

    #@75
    float-to-int v3, v8

    #@76
    float-to-int v4, v10

    #@77
    float-to-int v5, v12

    #@78
    move-object v0, p0

    #@79
    move-object v1, p0

    #@7a
    invoke-virtual/range {v0 .. v5}, Landroid/support/v4/view/ViewPager;->canScroll(Landroid/view/View;ZIII)Z

    #@7d
    move-result v0

    #@7e
    if-eqz v0, :cond_7

    #@80
    .line 1849
    iput v10, p0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@82
    .line 1850
    iput v12, p0, Landroid/support/v4/view/ViewPager;->mLastMotionY:F

    #@84
    .line 1851
    const/4 v0, 0x1

    #@85
    iput-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsUnableToDrag:Z

    #@87
    .line 1852
    const/4 v0, 0x0

    #@88
    goto :goto_0

    #@89
    .line 1854
    :cond_7
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mTouchSlop:I

    #@8b
    int-to-float v0, v0

    #@8c
    cmpl-float v0, v11, v0

    #@8e
    if-lez v0, :cond_a

    #@90
    const/high16 v0, 0x3f000000    # 0.5f

    #@92
    mul-float/2addr v0, v11

    #@93
    cmpl-float v0, v0, v13

    #@95
    if-lez v0, :cond_a

    #@97
    .line 1856
    const/4 v0, 0x1

    #@98
    iput-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@9a
    .line 1857
    const/4 v0, 0x1

    #@9b
    invoke-direct {p0, v0}, Landroid/support/v4/view/ViewPager;->requestParentDisallowInterceptTouchEvent(Z)V

    #@9e
    .line 1858
    const/4 v0, 0x1

    #@9f
    invoke-direct {p0, v0}, Landroid/support/v4/view/ViewPager;->setScrollState(I)V

    #@a2
    .line 1859
    const/4 v0, 0x0

    #@a3
    cmpl-float v0, v8, v0

    #@a5
    if-lez v0, :cond_9

    #@a7
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mInitialMotionX:F

    #@a9
    iget v1, p0, Landroid/support/v4/view/ViewPager;->mTouchSlop:I

    #@ab
    int-to-float v1, v1

    #@ac
    add-float/2addr v0, v1

    #@ad
    :goto_2
    iput v0, p0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@af
    .line 1861
    iput v12, p0, Landroid/support/v4/view/ViewPager;->mLastMotionY:F

    #@b1
    .line 1862
    const/4 v0, 0x1

    #@b2
    invoke-direct {p0, v0}, Landroid/support/v4/view/ViewPager;->setScrollingCacheEnabled(Z)V

    #@b5
    .line 1871
    :cond_8
    :goto_3
    iget-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@b7
    if-eqz v0, :cond_5

    #@b9
    .line 1873
    invoke-direct {p0, v10}, Landroid/support/v4/view/ViewPager;->performDrag(F)Z

    #@bc
    move-result v0

    #@bd
    if-eqz v0, :cond_5

    #@bf
    .line 1874
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->postInvalidateOnAnimation(Landroid/view/View;)V

    #@c2
    goto/16 :goto_1

    #@c4
    .line 1859
    :cond_9
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mInitialMotionX:F

    #@c6
    iget v1, p0, Landroid/support/v4/view/ViewPager;->mTouchSlop:I

    #@c8
    int-to-float v1, v1

    #@c9
    sub-float/2addr v0, v1

    #@ca
    goto :goto_2

    #@cb
    .line 1863
    :cond_a
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mTouchSlop:I

    #@cd
    int-to-float v0, v0

    #@ce
    cmpl-float v0, v13, v0

    #@d0
    if-lez v0, :cond_8

    #@d2
    .line 1869
    const/4 v0, 0x1

    #@d3
    iput-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsUnableToDrag:Z

    #@d5
    goto :goto_3

    #@d6
    .line 1885
    .end local v7    # "activePointerId":I
    .end local v8    # "dx":F
    .end local v9    # "pointerIndex":I
    .end local v10    # "x":F
    .end local v11    # "xDiff":F
    .end local v12    # "y":F
    .end local v13    # "yDiff":F
    :sswitch_1
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@d9
    move-result v0

    #@da
    iput v0, p0, Landroid/support/v4/view/ViewPager;->mInitialMotionX:F

    #@dc
    iput v0, p0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@de
    .line 1886
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@e1
    move-result v0

    #@e2
    iput v0, p0, Landroid/support/v4/view/ViewPager;->mInitialMotionY:F

    #@e4
    iput v0, p0, Landroid/support/v4/view/ViewPager;->mLastMotionY:F

    #@e6
    .line 1887
    const/4 v0, 0x0

    #@e7
    invoke-static {p1, v0}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@ea
    move-result v0

    #@eb
    iput v0, p0, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@ed
    .line 1888
    const/4 v0, 0x0

    #@ee
    iput-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsUnableToDrag:Z

    #@f0
    .line 1890
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@f2
    invoke-virtual {v0}, Landroid/widget/Scroller;->computeScrollOffset()Z

    #@f5
    .line 1891
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mScrollState:I

    #@f7
    const/4 v1, 0x2

    #@f8
    if-ne v0, v1, :cond_b

    #@fa
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@fc
    invoke-virtual {v0}, Landroid/widget/Scroller;->getFinalX()I

    #@ff
    move-result v0

    #@100
    iget-object v1, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@102
    invoke-virtual {v1}, Landroid/widget/Scroller;->getCurrX()I

    #@105
    move-result v1

    #@106
    sub-int/2addr v0, v1

    #@107
    invoke-static {v0}, Ljava/lang/Math;->abs(I)I

    #@10a
    move-result v0

    #@10b
    iget v1, p0, Landroid/support/v4/view/ViewPager;->mCloseEnough:I

    #@10d
    if-le v0, v1, :cond_b

    #@10f
    .line 1894
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@111
    invoke-virtual {v0}, Landroid/widget/Scroller;->abortAnimation()V

    #@114
    .line 1895
    const/4 v0, 0x0

    #@115
    iput-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mPopulatePending:Z

    #@117
    .line 1896
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->populate()V

    #@11a
    .line 1897
    const/4 v0, 0x1

    #@11b
    iput-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@11d
    .line 1898
    const/4 v0, 0x1

    #@11e
    invoke-direct {p0, v0}, Landroid/support/v4/view/ViewPager;->requestParentDisallowInterceptTouchEvent(Z)V

    #@121
    .line 1899
    const/4 v0, 0x1

    #@122
    invoke-direct {p0, v0}, Landroid/support/v4/view/ViewPager;->setScrollState(I)V

    #@125
    goto/16 :goto_1

    #@127
    .line 1901
    :cond_b
    const/4 v0, 0x0

    #@128
    invoke-direct {p0, v0}, Landroid/support/v4/view/ViewPager;->completeScroll(Z)V

    #@12b
    .line 1902
    const/4 v0, 0x0

    #@12c
    iput-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@12e
    goto/16 :goto_1

    #@130
    .line 1912
    :sswitch_2
    invoke-direct {p0, p1}, Landroid/support/v4/view/ViewPager;->onSecondaryPointerUp(Landroid/view/MotionEvent;)V

    #@133
    goto/16 :goto_1

    #@135
    .line 1821
    nop

    #@136
    :sswitch_data_0
    .sparse-switch
        0x0 -> :sswitch_1
        0x2 -> :sswitch_0
        0x6 -> :sswitch_2
    .end sparse-switch
.end method

.method protected onLayout(ZIIII)V
    .locals 30
    .param p1, "changed"    # Z
    .param p2, "l"    # I
    .param p3, "t"    # I
    .param p4, "r"    # I
    .param p5, "b"    # I

    #@0
    .prologue
    .line 1503
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@3
    move-result v9

    #@4
    .line 1504
    .local v9, "count":I
    sub-int v24, p4, p2

    #@6
    .line 1505
    .local v24, "width":I
    sub-int v11, p5, p3

    #@8
    .line 1506
    .local v11, "height":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getPaddingLeft()I

    #@b
    move-result v19

    #@c
    .line 1507
    .local v19, "paddingLeft":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getPaddingTop()I

    #@f
    move-result v21

    #@10
    .line 1508
    .local v21, "paddingTop":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getPaddingRight()I

    #@13
    move-result v20

    #@14
    .line 1509
    .local v20, "paddingRight":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getPaddingBottom()I

    #@17
    move-result v18

    #@18
    .line 1510
    .local v18, "paddingBottom":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@1b
    move-result v22

    #@1c
    .line 1512
    .local v22, "scrollX":I
    const/4 v10, 0x0

    #@1d
    .line 1516
    .local v10, "decorCount":I
    const/4 v14, 0x0

    #@1e
    .local v14, "i":I
    :goto_0
    if-ge v14, v9, :cond_1

    #@20
    .line 1517
    move-object/from16 v0, p0

    #@22
    invoke-virtual {v0, v14}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@25
    move-result-object v5

    #@26
    .line 1518
    .local v5, "child":Landroid/view/View;
    invoke-virtual {v5}, Landroid/view/View;->getVisibility()I

    #@29
    move-result v26

    #@2a
    const/16 v27, 0x8

    #@2c
    move/from16 v0, v26

    #@2e
    move/from16 v1, v27

    #@30
    if-eq v0, v1, :cond_0

    #@32
    .line 1519
    invoke-virtual {v5}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@35
    move-result-object v17

    #@36
    check-cast v17, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@38
    .line 1520
    .local v17, "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    const/4 v6, 0x0

    #@39
    .line 1521
    .local v6, "childLeft":I
    const/4 v7, 0x0

    #@3a
    .line 1522
    .local v7, "childTop":I
    move-object/from16 v0, v17

    #@3c
    iget-boolean v0, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@3e
    move/from16 v26, v0

    #@40
    if-eqz v26, :cond_0

    #@42
    .line 1523
    move-object/from16 v0, v17

    #@44
    iget v0, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->gravity:I

    #@46
    move/from16 v26, v0

    #@48
    and-int/lit8 v13, v26, 0x7

    #@4a
    .line 1524
    .local v13, "hgrav":I
    move-object/from16 v0, v17

    #@4c
    iget v0, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->gravity:I

    #@4e
    move/from16 v26, v0

    #@50
    and-int/lit8 v23, v26, 0x70

    #@52
    .line 1525
    .local v23, "vgrav":I
    packed-switch v13, :pswitch_data_0

    #@55
    .line 1527
    :pswitch_0
    move/from16 v6, v19

    #@57
    .line 1542
    :goto_1
    sparse-switch v23, :sswitch_data_0

    #@5a
    .line 1544
    move/from16 v7, v21

    #@5c
    .line 1559
    :goto_2
    add-int v6, v6, v22

    #@5e
    .line 1560
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredWidth()I

    #@61
    move-result v26

    #@62
    add-int v26, v26, v6

    #@64
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredHeight()I

    #@67
    move-result v27

    #@68
    add-int v27, v27, v7

    #@6a
    move/from16 v0, v26

    #@6c
    move/from16 v1, v27

    #@6e
    invoke-virtual {v5, v6, v7, v0, v1}, Landroid/view/View;->layout(IIII)V

    #@71
    .line 1563
    add-int/lit8 v10, v10, 0x1

    #@73
    .line 1516
    .end local v6    # "childLeft":I
    .end local v7    # "childTop":I
    .end local v13    # "hgrav":I
    .end local v17    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    .end local v23    # "vgrav":I
    :cond_0
    add-int/lit8 v14, v14, 0x1

    #@75
    goto :goto_0

    #@76
    .line 1530
    .restart local v6    # "childLeft":I
    .restart local v7    # "childTop":I
    .restart local v13    # "hgrav":I
    .restart local v17    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    .restart local v23    # "vgrav":I
    :pswitch_1
    move/from16 v6, v19

    #@78
    .line 1531
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredWidth()I

    #@7b
    move-result v26

    #@7c
    add-int v19, v19, v26

    #@7e
    .line 1532
    goto :goto_1

    #@7f
    .line 1534
    :pswitch_2
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredWidth()I

    #@82
    move-result v26

    #@83
    sub-int v26, v24, v26

    #@85
    div-int/lit8 v26, v26, 0x2

    #@87
    move/from16 v0, v26

    #@89
    move/from16 v1, v19

    #@8b
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@8e
    move-result v6

    #@8f
    .line 1536
    goto :goto_1

    #@90
    .line 1538
    :pswitch_3
    sub-int v26, v24, v20

    #@92
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredWidth()I

    #@95
    move-result v27

    #@96
    sub-int v6, v26, v27

    #@98
    .line 1539
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredWidth()I

    #@9b
    move-result v26

    #@9c
    add-int v20, v20, v26

    #@9e
    goto :goto_1

    #@9f
    .line 1547
    :sswitch_0
    move/from16 v7, v21

    #@a1
    .line 1548
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredHeight()I

    #@a4
    move-result v26

    #@a5
    add-int v21, v21, v26

    #@a7
    .line 1549
    goto :goto_2

    #@a8
    .line 1551
    :sswitch_1
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredHeight()I

    #@ab
    move-result v26

    #@ac
    sub-int v26, v11, v26

    #@ae
    div-int/lit8 v26, v26, 0x2

    #@b0
    move/from16 v0, v26

    #@b2
    move/from16 v1, v21

    #@b4
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@b7
    move-result v7

    #@b8
    .line 1553
    goto :goto_2

    #@b9
    .line 1555
    :sswitch_2
    sub-int v26, v11, v18

    #@bb
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredHeight()I

    #@be
    move-result v27

    #@bf
    sub-int v7, v26, v27

    #@c1
    .line 1556
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredHeight()I

    #@c4
    move-result v26

    #@c5
    add-int v18, v18, v26

    #@c7
    goto :goto_2

    #@c8
    .line 1568
    .end local v5    # "child":Landroid/view/View;
    .end local v6    # "childLeft":I
    .end local v7    # "childTop":I
    .end local v13    # "hgrav":I
    .end local v17    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    .end local v23    # "vgrav":I
    :cond_1
    sub-int v26, v24, v19

    #@ca
    sub-int v8, v26, v20

    #@cc
    .line 1570
    .local v8, "childWidth":I
    const/4 v14, 0x0

    #@cd
    :goto_3
    if-ge v14, v9, :cond_4

    #@cf
    .line 1571
    move-object/from16 v0, p0

    #@d1
    invoke-virtual {v0, v14}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@d4
    move-result-object v5

    #@d5
    .line 1572
    .restart local v5    # "child":Landroid/view/View;
    invoke-virtual {v5}, Landroid/view/View;->getVisibility()I

    #@d8
    move-result v26

    #@d9
    const/16 v27, 0x8

    #@db
    move/from16 v0, v26

    #@dd
    move/from16 v1, v27

    #@df
    if-eq v0, v1, :cond_3

    #@e1
    .line 1573
    invoke-virtual {v5}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@e4
    move-result-object v17

    #@e5
    check-cast v17, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@e7
    .line 1575
    .restart local v17    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    move-object/from16 v0, v17

    #@e9
    iget-boolean v0, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@eb
    move/from16 v26, v0

    #@ed
    if-nez v26, :cond_3

    #@ef
    move-object/from16 v0, p0

    #@f1
    invoke-virtual {v0, v5}, Landroid/support/v4/view/ViewPager;->infoForChild(Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@f4
    move-result-object v15

    #@f5
    .local v15, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    if-eqz v15, :cond_3

    #@f7
    .line 1576
    int-to-float v0, v8

    #@f8
    move/from16 v26, v0

    #@fa
    iget v0, v15, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@fc
    move/from16 v27, v0

    #@fe
    mul-float v26, v26, v27

    #@100
    move/from16 v0, v26

    #@102
    float-to-int v0, v0

    #@103
    move/from16 v16, v0

    #@105
    .line 1577
    .local v16, "loff":I
    add-int v6, v19, v16

    #@107
    .line 1578
    .restart local v6    # "childLeft":I
    move/from16 v7, v21

    #@109
    .line 1579
    .restart local v7    # "childTop":I
    move-object/from16 v0, v17

    #@10b
    iget-boolean v0, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->needsMeasure:Z

    #@10d
    move/from16 v26, v0

    #@10f
    if-eqz v26, :cond_2

    #@111
    .line 1582
    const/16 v26, 0x0

    #@113
    move/from16 v0, v26

    #@115
    move-object/from16 v1, v17

    #@117
    iput-boolean v0, v1, Landroid/support/v4/view/ViewPager$LayoutParams;->needsMeasure:Z

    #@119
    .line 1583
    int-to-float v0, v8

    #@11a
    move/from16 v26, v0

    #@11c
    move-object/from16 v0, v17

    #@11e
    iget v0, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->widthFactor:F

    #@120
    move/from16 v27, v0

    #@122
    mul-float v26, v26, v27

    #@124
    move/from16 v0, v26

    #@126
    float-to-int v0, v0

    #@127
    move/from16 v26, v0

    #@129
    const/high16 v27, 0x40000000    # 2.0f

    #@12b
    invoke-static/range {v26 .. v27}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@12e
    move-result v25

    #@12f
    .line 1586
    .local v25, "widthSpec":I
    sub-int v26, v11, v21

    #@131
    sub-int v26, v26, v18

    #@133
    const/high16 v27, 0x40000000    # 2.0f

    #@135
    invoke-static/range {v26 .. v27}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@138
    move-result v12

    #@139
    .line 1589
    .local v12, "heightSpec":I
    move/from16 v0, v25

    #@13b
    invoke-virtual {v5, v0, v12}, Landroid/view/View;->measure(II)V

    #@13e
    .line 1594
    .end local v12    # "heightSpec":I
    .end local v25    # "widthSpec":I
    :cond_2
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredWidth()I

    #@141
    move-result v26

    #@142
    add-int v26, v26, v6

    #@144
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredHeight()I

    #@147
    move-result v27

    #@148
    add-int v27, v27, v7

    #@14a
    move/from16 v0, v26

    #@14c
    move/from16 v1, v27

    #@14e
    invoke-virtual {v5, v6, v7, v0, v1}, Landroid/view/View;->layout(IIII)V

    #@151
    .line 1570
    .end local v6    # "childLeft":I
    .end local v7    # "childTop":I
    .end local v15    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v16    # "loff":I
    .end local v17    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    :cond_3
    add-int/lit8 v14, v14, 0x1

    #@153
    goto/16 :goto_3

    #@155
    .line 1600
    .end local v5    # "child":Landroid/view/View;
    :cond_4
    move/from16 v0, v21

    #@157
    move-object/from16 v1, p0

    #@159
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mTopPageBounds:I

    #@15b
    .line 1601
    sub-int v26, v11, v18

    #@15d
    move/from16 v0, v26

    #@15f
    move-object/from16 v1, p0

    #@161
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mBottomPageBounds:I

    #@163
    .line 1602
    move-object/from16 v0, p0

    #@165
    iput v10, v0, Landroid/support/v4/view/ViewPager;->mDecorChildCount:I

    #@167
    .line 1604
    move-object/from16 v0, p0

    #@169
    iget-boolean v0, v0, Landroid/support/v4/view/ViewPager;->mFirstLayout:Z

    #@16b
    move/from16 v26, v0

    #@16d
    if-eqz v26, :cond_5

    #@16f
    .line 1605
    move-object/from16 v0, p0

    #@171
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@173
    move/from16 v26, v0

    #@175
    const/16 v27, 0x0

    #@177
    const/16 v28, 0x0

    #@179
    const/16 v29, 0x0

    #@17b
    move-object/from16 v0, p0

    #@17d
    move/from16 v1, v26

    #@17f
    move/from16 v2, v27

    #@181
    move/from16 v3, v28

    #@183
    move/from16 v4, v29

    #@185
    invoke-direct {v0, v1, v2, v3, v4}, Landroid/support/v4/view/ViewPager;->scrollToItem(IZIZ)V

    #@188
    .line 1607
    :cond_5
    const/16 v26, 0x0

    #@18a
    move/from16 v0, v26

    #@18c
    move-object/from16 v1, p0

    #@18e
    iput-boolean v0, v1, Landroid/support/v4/view/ViewPager;->mFirstLayout:Z

    #@190
    .line 1608
    return-void

    #@191
    .line 1525
    nop

    #@192
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_2
        :pswitch_0
        :pswitch_1
        :pswitch_0
        :pswitch_3
    .end packed-switch

    #@1a0
    .line 1542
    :sswitch_data_0
    .sparse-switch
        0x10 -> :sswitch_1
        0x30 -> :sswitch_0
        0x50 -> :sswitch_2
    .end sparse-switch
.end method

.method protected onMeasure(II)V
    .locals 23
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 1374
    const/16 v21, 0x0

    #@2
    move/from16 v0, v21

    #@4
    move/from16 v1, p1

    #@6
    invoke-static {v0, v1}, Landroid/support/v4/view/ViewPager;->getDefaultSize(II)I

    #@9
    move-result v21

    #@a
    const/16 v22, 0x0

    #@c
    move/from16 v0, v22

    #@e
    move/from16 v1, p2

    #@10
    invoke-static {v0, v1}, Landroid/support/v4/view/ViewPager;->getDefaultSize(II)I

    #@13
    move-result v22

    #@14
    move-object/from16 v0, p0

    #@16
    move/from16 v1, v21

    #@18
    move/from16 v2, v22

    #@1a
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/view/ViewPager;->setMeasuredDimension(II)V

    #@1d
    .line 1377
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getMeasuredWidth()I

    #@20
    move-result v15

    #@21
    .line 1378
    .local v15, "measuredWidth":I
    div-int/lit8 v14, v15, 0xa

    #@23
    .line 1379
    .local v14, "maxGutterSize":I
    move-object/from16 v0, p0

    #@25
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mDefaultGutterSize:I

    #@27
    move/from16 v21, v0

    #@29
    move/from16 v0, v21

    #@2b
    invoke-static {v14, v0}, Ljava/lang/Math;->min(II)I

    #@2e
    move-result v21

    #@2f
    move/from16 v0, v21

    #@31
    move-object/from16 v1, p0

    #@33
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mGutterSize:I

    #@35
    .line 1382
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getPaddingLeft()I

    #@38
    move-result v21

    #@39
    sub-int v21, v15, v21

    #@3b
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getPaddingRight()I

    #@3e
    move-result v22

    #@3f
    sub-int v5, v21, v22

    #@41
    .line 1383
    .local v5, "childWidthSize":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getMeasuredHeight()I

    #@44
    move-result v21

    #@45
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getPaddingTop()I

    #@48
    move-result v22

    #@49
    sub-int v21, v21, v22

    #@4b
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getPaddingBottom()I

    #@4e
    move-result v22

    #@4f
    sub-int v4, v21, v22

    #@51
    .line 1390
    .local v4, "childHeightSize":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@54
    move-result v16

    #@55
    .line 1391
    .local v16, "size":I
    const/4 v12, 0x0

    #@56
    .local v12, "i":I
    :goto_0
    move/from16 v0, v16

    #@58
    if-ge v12, v0, :cond_a

    #@5a
    .line 1392
    move-object/from16 v0, p0

    #@5c
    invoke-virtual {v0, v12}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@5f
    move-result-object v3

    #@60
    .line 1393
    .local v3, "child":Landroid/view/View;
    invoke-virtual {v3}, Landroid/view/View;->getVisibility()I

    #@63
    move-result v21

    #@64
    const/16 v22, 0x8

    #@66
    move/from16 v0, v21

    #@68
    move/from16 v1, v22

    #@6a
    if-eq v0, v1, :cond_5

    #@6c
    .line 1394
    invoke-virtual {v3}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@6f
    move-result-object v13

    #@70
    check-cast v13, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@72
    .line 1395
    .local v13, "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    if-eqz v13, :cond_5

    #@74
    iget-boolean v0, v13, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@76
    move/from16 v21, v0

    #@78
    if-eqz v21, :cond_5

    #@7a
    .line 1396
    iget v0, v13, Landroid/support/v4/view/ViewPager$LayoutParams;->gravity:I

    #@7c
    move/from16 v21, v0

    #@7e
    and-int/lit8 v11, v21, 0x7

    #@80
    .line 1397
    .local v11, "hgrav":I
    iget v0, v13, Landroid/support/v4/view/ViewPager$LayoutParams;->gravity:I

    #@82
    move/from16 v21, v0

    #@84
    and-int/lit8 v17, v21, 0x70

    #@86
    .line 1398
    .local v17, "vgrav":I
    const/high16 v18, -0x80000000

    #@88
    .line 1399
    .local v18, "widthMode":I
    const/high16 v8, -0x80000000

    #@8a
    .line 1400
    .local v8, "heightMode":I
    const/16 v21, 0x30

    #@8c
    move/from16 v0, v17

    #@8e
    move/from16 v1, v21

    #@90
    if-eq v0, v1, :cond_0

    #@92
    const/16 v21, 0x50

    #@94
    move/from16 v0, v17

    #@96
    move/from16 v1, v21

    #@98
    if-ne v0, v1, :cond_6

    #@9a
    :cond_0
    const/4 v7, 0x1

    #@9b
    .line 1401
    .local v7, "consumeVertical":Z
    :goto_1
    const/16 v21, 0x3

    #@9d
    move/from16 v0, v21

    #@9f
    if-eq v11, v0, :cond_1

    #@a1
    const/16 v21, 0x5

    #@a3
    move/from16 v0, v21

    #@a5
    if-ne v11, v0, :cond_7

    #@a7
    :cond_1
    const/4 v6, 0x1

    #@a8
    .line 1403
    .local v6, "consumeHorizontal":Z
    :goto_2
    if-eqz v7, :cond_8

    #@aa
    .line 1404
    const/high16 v18, 0x40000000    # 2.0f

    #@ac
    .line 1409
    :cond_2
    :goto_3
    move/from16 v19, v5

    #@ae
    .line 1410
    .local v19, "widthSize":I
    move v9, v4

    #@af
    .line 1411
    .local v9, "heightSize":I
    iget v0, v13, Landroid/support/v4/view/ViewPager$LayoutParams;->width:I

    #@b1
    move/from16 v21, v0

    #@b3
    const/16 v22, -0x2

    #@b5
    move/from16 v0, v21

    #@b7
    move/from16 v1, v22

    #@b9
    if-eq v0, v1, :cond_3

    #@bb
    .line 1412
    const/high16 v18, 0x40000000    # 2.0f

    #@bd
    .line 1413
    iget v0, v13, Landroid/support/v4/view/ViewPager$LayoutParams;->width:I

    #@bf
    move/from16 v21, v0

    #@c1
    const/16 v22, -0x1

    #@c3
    move/from16 v0, v21

    #@c5
    move/from16 v1, v22

    #@c7
    if-eq v0, v1, :cond_3

    #@c9
    .line 1414
    iget v0, v13, Landroid/support/v4/view/ViewPager$LayoutParams;->width:I

    #@cb
    move/from16 v19, v0

    #@cd
    .line 1417
    :cond_3
    iget v0, v13, Landroid/support/v4/view/ViewPager$LayoutParams;->height:I

    #@cf
    move/from16 v21, v0

    #@d1
    const/16 v22, -0x2

    #@d3
    move/from16 v0, v21

    #@d5
    move/from16 v1, v22

    #@d7
    if-eq v0, v1, :cond_4

    #@d9
    .line 1418
    const/high16 v8, 0x40000000    # 2.0f

    #@db
    .line 1419
    iget v0, v13, Landroid/support/v4/view/ViewPager$LayoutParams;->height:I

    #@dd
    move/from16 v21, v0

    #@df
    const/16 v22, -0x1

    #@e1
    move/from16 v0, v21

    #@e3
    move/from16 v1, v22

    #@e5
    if-eq v0, v1, :cond_4

    #@e7
    .line 1420
    iget v9, v13, Landroid/support/v4/view/ViewPager$LayoutParams;->height:I

    #@e9
    .line 1423
    :cond_4
    move/from16 v0, v19

    #@eb
    move/from16 v1, v18

    #@ed
    invoke-static {v0, v1}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@f0
    move-result v20

    #@f1
    .line 1424
    .local v20, "widthSpec":I
    invoke-static {v9, v8}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@f4
    move-result v10

    #@f5
    .line 1425
    .local v10, "heightSpec":I
    move/from16 v0, v20

    #@f7
    invoke-virtual {v3, v0, v10}, Landroid/view/View;->measure(II)V

    #@fa
    .line 1427
    if-eqz v7, :cond_9

    #@fc
    .line 1428
    invoke-virtual {v3}, Landroid/view/View;->getMeasuredHeight()I

    #@ff
    move-result v21

    #@100
    sub-int v4, v4, v21

    #@102
    .line 1391
    .end local v6    # "consumeHorizontal":Z
    .end local v7    # "consumeVertical":Z
    .end local v8    # "heightMode":I
    .end local v9    # "heightSize":I
    .end local v10    # "heightSpec":I
    .end local v11    # "hgrav":I
    .end local v13    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    .end local v17    # "vgrav":I
    .end local v18    # "widthMode":I
    .end local v19    # "widthSize":I
    .end local v20    # "widthSpec":I
    :cond_5
    :goto_4
    add-int/lit8 v12, v12, 0x1

    #@104
    goto/16 :goto_0

    #@106
    .line 1400
    .restart local v8    # "heightMode":I
    .restart local v11    # "hgrav":I
    .restart local v13    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    .restart local v17    # "vgrav":I
    .restart local v18    # "widthMode":I
    :cond_6
    const/4 v7, 0x0

    #@107
    goto :goto_1

    #@108
    .line 1401
    .restart local v7    # "consumeVertical":Z
    :cond_7
    const/4 v6, 0x0

    #@109
    goto :goto_2

    #@10a
    .line 1405
    .restart local v6    # "consumeHorizontal":Z
    :cond_8
    if-eqz v6, :cond_2

    #@10c
    .line 1406
    const/high16 v8, 0x40000000    # 2.0f

    #@10e
    goto :goto_3

    #@10f
    .line 1429
    .restart local v9    # "heightSize":I
    .restart local v10    # "heightSpec":I
    .restart local v19    # "widthSize":I
    .restart local v20    # "widthSpec":I
    :cond_9
    if-eqz v6, :cond_5

    #@111
    .line 1430
    invoke-virtual {v3}, Landroid/view/View;->getMeasuredWidth()I

    #@114
    move-result v21

    #@115
    sub-int v5, v5, v21

    #@117
    goto :goto_4

    #@118
    .line 1436
    .end local v3    # "child":Landroid/view/View;
    .end local v6    # "consumeHorizontal":Z
    .end local v7    # "consumeVertical":Z
    .end local v8    # "heightMode":I
    .end local v9    # "heightSize":I
    .end local v10    # "heightSpec":I
    .end local v11    # "hgrav":I
    .end local v13    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    .end local v17    # "vgrav":I
    .end local v18    # "widthMode":I
    .end local v19    # "widthSize":I
    .end local v20    # "widthSpec":I
    :cond_a
    const/high16 v21, 0x40000000    # 2.0f

    #@11a
    move/from16 v0, v21

    #@11c
    invoke-static {v5, v0}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@11f
    move-result v21

    #@120
    move/from16 v0, v21

    #@122
    move-object/from16 v1, p0

    #@124
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mChildWidthMeasureSpec:I

    #@126
    .line 1437
    const/high16 v21, 0x40000000    # 2.0f

    #@128
    move/from16 v0, v21

    #@12a
    invoke-static {v4, v0}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@12d
    move-result v21

    #@12e
    move/from16 v0, v21

    #@130
    move-object/from16 v1, p0

    #@132
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mChildHeightMeasureSpec:I

    #@134
    .line 1440
    const/16 v21, 0x1

    #@136
    move/from16 v0, v21

    #@138
    move-object/from16 v1, p0

    #@13a
    iput-boolean v0, v1, Landroid/support/v4/view/ViewPager;->mInLayout:Z

    #@13c
    .line 1441
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->populate()V

    #@13f
    .line 1442
    const/16 v21, 0x0

    #@141
    move/from16 v0, v21

    #@143
    move-object/from16 v1, p0

    #@145
    iput-boolean v0, v1, Landroid/support/v4/view/ViewPager;->mInLayout:Z

    #@147
    .line 1445
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@14a
    move-result v16

    #@14b
    .line 1446
    const/4 v12, 0x0

    #@14c
    :goto_5
    move/from16 v0, v16

    #@14e
    if-ge v12, v0, :cond_d

    #@150
    .line 1447
    move-object/from16 v0, p0

    #@152
    invoke-virtual {v0, v12}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@155
    move-result-object v3

    #@156
    .line 1448
    .restart local v3    # "child":Landroid/view/View;
    invoke-virtual {v3}, Landroid/view/View;->getVisibility()I

    #@159
    move-result v21

    #@15a
    const/16 v22, 0x8

    #@15c
    move/from16 v0, v21

    #@15e
    move/from16 v1, v22

    #@160
    if-eq v0, v1, :cond_c

    #@162
    .line 1452
    invoke-virtual {v3}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@165
    move-result-object v13

    #@166
    check-cast v13, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@168
    .line 1453
    .restart local v13    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    if-eqz v13, :cond_b

    #@16a
    iget-boolean v0, v13, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@16c
    move/from16 v21, v0

    #@16e
    if-nez v21, :cond_c

    #@170
    .line 1454
    :cond_b
    int-to-float v0, v5

    #@171
    move/from16 v21, v0

    #@173
    iget v0, v13, Landroid/support/v4/view/ViewPager$LayoutParams;->widthFactor:F

    #@175
    move/from16 v22, v0

    #@177
    mul-float v21, v21, v22

    #@179
    move/from16 v0, v21

    #@17b
    float-to-int v0, v0

    #@17c
    move/from16 v21, v0

    #@17e
    const/high16 v22, 0x40000000    # 2.0f

    #@180
    invoke-static/range {v21 .. v22}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@183
    move-result v20

    #@184
    .line 1456
    .restart local v20    # "widthSpec":I
    move-object/from16 v0, p0

    #@186
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mChildHeightMeasureSpec:I

    #@188
    move/from16 v21, v0

    #@18a
    move/from16 v0, v20

    #@18c
    move/from16 v1, v21

    #@18e
    invoke-virtual {v3, v0, v1}, Landroid/view/View;->measure(II)V

    #@191
    .line 1446
    .end local v13    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    .end local v20    # "widthSpec":I
    :cond_c
    add-int/lit8 v12, v12, 0x1

    #@193
    goto :goto_5

    #@194
    .line 1460
    .end local v3    # "child":Landroid/view/View;
    :cond_d
    return-void
.end method

.method protected onPageScrolled(IFI)V
    .locals 17
    .param p1, "position"    # I
    .param p2, "offset"    # F
    .param p3, "offsetPixels"    # I

    #@0
    .prologue
    .line 1677
    move-object/from16 v0, p0

    #@2
    iget v15, v0, Landroid/support/v4/view/ViewPager;->mDecorChildCount:I

    #@4
    if-lez v15, :cond_2

    #@6
    .line 1678
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@9
    move-result v12

    #@a
    .line 1679
    .local v12, "scrollX":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getPaddingLeft()I

    #@d
    move-result v10

    #@e
    .line 1680
    .local v10, "paddingLeft":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getPaddingRight()I

    #@11
    move-result v11

    #@12
    .line 1681
    .local v11, "paddingRight":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getWidth()I

    #@15
    move-result v14

    #@16
    .line 1682
    .local v14, "width":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@19
    move-result v4

    #@1a
    .line 1683
    .local v4, "childCount":I
    const/4 v8, 0x0

    #@1b
    .local v8, "i":I
    :goto_0
    if-ge v8, v4, :cond_2

    #@1d
    .line 1684
    move-object/from16 v0, p0

    #@1f
    invoke-virtual {v0, v8}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@22
    move-result-object v3

    #@23
    .line 1685
    .local v3, "child":Landroid/view/View;
    invoke-virtual {v3}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@26
    move-result-object v9

    #@27
    check-cast v9, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@29
    .line 1686
    .local v9, "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    iget-boolean v15, v9, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@2b
    if-nez v15, :cond_1

    #@2d
    .line 1683
    :cond_0
    :goto_1
    add-int/lit8 v8, v8, 0x1

    #@2f
    goto :goto_0

    #@30
    .line 1688
    :cond_1
    iget v15, v9, Landroid/support/v4/view/ViewPager$LayoutParams;->gravity:I

    #@32
    and-int/lit8 v7, v15, 0x7

    #@34
    .line 1689
    .local v7, "hgrav":I
    const/4 v5, 0x0

    #@35
    .line 1690
    .local v5, "childLeft":I
    packed-switch v7, :pswitch_data_0

    #@38
    .line 1692
    :pswitch_0
    move v5, v10

    #@39
    .line 1707
    :goto_2
    add-int/2addr v5, v12

    #@3a
    .line 1709
    invoke-virtual {v3}, Landroid/view/View;->getLeft()I

    #@3d
    move-result v15

    #@3e
    sub-int v6, v5, v15

    #@40
    .line 1710
    .local v6, "childOffset":I
    if-eqz v6, :cond_0

    #@42
    .line 1711
    invoke-virtual {v3, v6}, Landroid/view/View;->offsetLeftAndRight(I)V

    #@45
    goto :goto_1

    #@46
    .line 1695
    .end local v6    # "childOffset":I
    :pswitch_1
    move v5, v10

    #@47
    .line 1696
    invoke-virtual {v3}, Landroid/view/View;->getWidth()I

    #@4a
    move-result v15

    #@4b
    add-int/2addr v10, v15

    #@4c
    .line 1697
    goto :goto_2

    #@4d
    .line 1699
    :pswitch_2
    invoke-virtual {v3}, Landroid/view/View;->getMeasuredWidth()I

    #@50
    move-result v15

    #@51
    sub-int v15, v14, v15

    #@53
    div-int/lit8 v15, v15, 0x2

    #@55
    invoke-static {v15, v10}, Ljava/lang/Math;->max(II)I

    #@58
    move-result v5

    #@59
    .line 1701
    goto :goto_2

    #@5a
    .line 1703
    :pswitch_3
    sub-int v15, v14, v11

    #@5c
    invoke-virtual {v3}, Landroid/view/View;->getMeasuredWidth()I

    #@5f
    move-result v16

    #@60
    sub-int v5, v15, v16

    #@62
    .line 1704
    invoke-virtual {v3}, Landroid/view/View;->getMeasuredWidth()I

    #@65
    move-result v15

    #@66
    add-int/2addr v11, v15

    #@67
    goto :goto_2

    #@68
    .line 1716
    .end local v3    # "child":Landroid/view/View;
    .end local v4    # "childCount":I
    .end local v5    # "childLeft":I
    .end local v7    # "hgrav":I
    .end local v8    # "i":I
    .end local v9    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    .end local v10    # "paddingLeft":I
    .end local v11    # "paddingRight":I
    .end local v12    # "scrollX":I
    .end local v14    # "width":I
    :cond_2
    move-object/from16 v0, p0

    #@6a
    iget-object v15, v0, Landroid/support/v4/view/ViewPager;->mOnPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@6c
    if-eqz v15, :cond_3

    #@6e
    .line 1717
    move-object/from16 v0, p0

    #@70
    iget-object v15, v0, Landroid/support/v4/view/ViewPager;->mOnPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@72
    move/from16 v0, p1

    #@74
    move/from16 v1, p2

    #@76
    move/from16 v2, p3

    #@78
    invoke-interface {v15, v0, v1, v2}, Landroid/support/v4/view/ViewPager$OnPageChangeListener;->onPageScrolled(IFI)V

    #@7b
    .line 1719
    :cond_3
    move-object/from16 v0, p0

    #@7d
    iget-object v15, v0, Landroid/support/v4/view/ViewPager;->mInternalPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@7f
    if-eqz v15, :cond_4

    #@81
    .line 1720
    move-object/from16 v0, p0

    #@83
    iget-object v15, v0, Landroid/support/v4/view/ViewPager;->mInternalPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@85
    move/from16 v0, p1

    #@87
    move/from16 v1, p2

    #@89
    move/from16 v2, p3

    #@8b
    invoke-interface {v15, v0, v1, v2}, Landroid/support/v4/view/ViewPager$OnPageChangeListener;->onPageScrolled(IFI)V

    #@8e
    .line 1723
    :cond_4
    move-object/from16 v0, p0

    #@90
    iget-object v15, v0, Landroid/support/v4/view/ViewPager;->mPageTransformer:Landroid/support/v4/view/ViewPager$PageTransformer;

    #@92
    if-eqz v15, :cond_6

    #@94
    .line 1724
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@97
    move-result v12

    #@98
    .line 1725
    .restart local v12    # "scrollX":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@9b
    move-result v4

    #@9c
    .line 1726
    .restart local v4    # "childCount":I
    const/4 v8, 0x0

    #@9d
    .restart local v8    # "i":I
    :goto_3
    if-ge v8, v4, :cond_6

    #@9f
    .line 1727
    move-object/from16 v0, p0

    #@a1
    invoke-virtual {v0, v8}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@a4
    move-result-object v3

    #@a5
    .line 1728
    .restart local v3    # "child":Landroid/view/View;
    invoke-virtual {v3}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@a8
    move-result-object v9

    #@a9
    check-cast v9, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@ab
    .line 1730
    .restart local v9    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    iget-boolean v15, v9, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@ad
    if-eqz v15, :cond_5

    #@af
    .line 1726
    :goto_4
    add-int/lit8 v8, v8, 0x1

    #@b1
    goto :goto_3

    #@b2
    .line 1732
    :cond_5
    invoke-virtual {v3}, Landroid/view/View;->getLeft()I

    #@b5
    move-result v15

    #@b6
    sub-int/2addr v15, v12

    #@b7
    int-to-float v15, v15

    #@b8
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getClientWidth()I

    #@bb
    move-result v16

    #@bc
    move/from16 v0, v16

    #@be
    int-to-float v0, v0

    #@bf
    move/from16 v16, v0

    #@c1
    div-float v13, v15, v16

    #@c3
    .line 1733
    .local v13, "transformPos":F
    move-object/from16 v0, p0

    #@c5
    iget-object v15, v0, Landroid/support/v4/view/ViewPager;->mPageTransformer:Landroid/support/v4/view/ViewPager$PageTransformer;

    #@c7
    invoke-interface {v15, v3, v13}, Landroid/support/v4/view/ViewPager$PageTransformer;->transformPage(Landroid/view/View;F)V

    #@ca
    goto :goto_4

    #@cb
    .line 1737
    .end local v3    # "child":Landroid/view/View;
    .end local v4    # "childCount":I
    .end local v8    # "i":I
    .end local v9    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    .end local v12    # "scrollX":I
    .end local v13    # "transformPos":F
    :cond_6
    const/4 v15, 0x1

    #@cc
    move-object/from16 v0, p0

    #@ce
    iput-boolean v15, v0, Landroid/support/v4/view/ViewPager;->mCalledSuper:Z

    #@d0
    .line 1738
    return-void

    #@d1
    .line 1690
    nop

    #@d2
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_2
        :pswitch_0
        :pswitch_1
        :pswitch_0
        :pswitch_3
    .end packed-switch
.end method

.method protected onRequestFocusInDescendants(ILandroid/graphics/Rect;)Z
    .locals 9
    .param p1, "direction"    # I
    .param p2, "previouslyFocusedRect"    # Landroid/graphics/Rect;

    #@0
    .prologue
    .line 2698
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@3
    move-result v1

    #@4
    .line 2699
    .local v1, "count":I
    and-int/lit8 v7, p1, 0x2

    #@6
    if-eqz v7, :cond_0

    #@8
    .line 2700
    const/4 v6, 0x0

    #@9
    .line 2701
    .local v6, "index":I
    const/4 v5, 0x1

    #@a
    .line 2702
    .local v5, "increment":I
    move v2, v1

    #@b
    .line 2708
    .local v2, "end":I
    :goto_0
    move v3, v6

    #@c
    .local v3, "i":I
    :goto_1
    if-eq v3, v2, :cond_2

    #@e
    .line 2709
    invoke-virtual {p0, v3}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@11
    move-result-object v0

    #@12
    .line 2710
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getVisibility()I

    #@15
    move-result v7

    #@16
    if-nez v7, :cond_1

    #@18
    .line 2711
    invoke-virtual {p0, v0}, Landroid/support/v4/view/ViewPager;->infoForChild(Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@1b
    move-result-object v4

    #@1c
    .line 2712
    .local v4, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    if-eqz v4, :cond_1

    #@1e
    iget v7, v4, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@20
    iget v8, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@22
    if-ne v7, v8, :cond_1

    #@24
    .line 2713
    invoke-virtual {v0, p1, p2}, Landroid/view/View;->requestFocus(ILandroid/graphics/Rect;)Z

    #@27
    move-result v7

    #@28
    if-eqz v7, :cond_1

    #@2a
    .line 2714
    const/4 v7, 0x1

    #@2b
    .line 2719
    .end local v0    # "child":Landroid/view/View;
    .end local v4    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :goto_2
    return v7

    #@2c
    .line 2704
    .end local v2    # "end":I
    .end local v3    # "i":I
    .end local v5    # "increment":I
    .end local v6    # "index":I
    :cond_0
    add-int/lit8 v6, v1, -0x1

    #@2e
    .line 2705
    .restart local v6    # "index":I
    const/4 v5, -0x1

    #@2f
    .line 2706
    .restart local v5    # "increment":I
    const/4 v2, -0x1

    #@30
    .restart local v2    # "end":I
    goto :goto_0

    #@31
    .line 2708
    .restart local v0    # "child":Landroid/view/View;
    .restart local v3    # "i":I
    :cond_1
    add-int/2addr v3, v5

    #@32
    goto :goto_1

    #@33
    .line 2719
    .end local v0    # "child":Landroid/view/View;
    :cond_2
    const/4 v7, 0x0

    #@34
    goto :goto_2
.end method

.method public onRestoreInstanceState(Landroid/os/Parcelable;)V
    .locals 4
    .param p1, "state"    # Landroid/os/Parcelable;

    #@0
    .prologue
    .line 1277
    instance-of v1, p1, Landroid/support/v4/view/ViewPager$SavedState;

    #@2
    if-nez v1, :cond_0

    #@4
    .line 1278
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->onRestoreInstanceState(Landroid/os/Parcelable;)V

    #@7
    .line 1293
    :goto_0
    return-void

    #@8
    :cond_0
    move-object v0, p1

    #@9
    .line 1282
    check-cast v0, Landroid/support/v4/view/ViewPager$SavedState;

    #@b
    .line 1283
    .local v0, "ss":Landroid/support/v4/view/ViewPager$SavedState;
    invoke-virtual {v0}, Landroid/support/v4/view/ViewPager$SavedState;->getSuperState()Landroid/os/Parcelable;

    #@e
    move-result-object v1

    #@f
    invoke-super {p0, v1}, Landroid/view/ViewGroup;->onRestoreInstanceState(Landroid/os/Parcelable;)V

    #@12
    .line 1285
    iget-object v1, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@14
    if-eqz v1, :cond_1

    #@16
    .line 1286
    iget-object v1, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@18
    iget-object v2, v0, Landroid/support/v4/view/ViewPager$SavedState;->adapterState:Landroid/os/Parcelable;

    #@1a
    iget-object v3, v0, Landroid/support/v4/view/ViewPager$SavedState;->loader:Ljava/lang/ClassLoader;

    #@1c
    invoke-virtual {v1, v2, v3}, Landroid/support/v4/view/PagerAdapter;->restoreState(Landroid/os/Parcelable;Ljava/lang/ClassLoader;)V

    #@1f
    .line 1287
    iget v1, v0, Landroid/support/v4/view/ViewPager$SavedState;->position:I

    #@21
    const/4 v2, 0x0

    #@22
    const/4 v3, 0x1

    #@23
    invoke-virtual {p0, v1, v2, v3}, Landroid/support/v4/view/ViewPager;->setCurrentItemInternal(IZZ)V

    #@26
    goto :goto_0

    #@27
    .line 1289
    :cond_1
    iget v1, v0, Landroid/support/v4/view/ViewPager$SavedState;->position:I

    #@29
    iput v1, p0, Landroid/support/v4/view/ViewPager;->mRestoredCurItem:I

    #@2b
    .line 1290
    iget-object v1, v0, Landroid/support/v4/view/ViewPager$SavedState;->adapterState:Landroid/os/Parcelable;

    #@2d
    iput-object v1, p0, Landroid/support/v4/view/ViewPager;->mRestoredAdapterState:Landroid/os/Parcelable;

    #@2f
    .line 1291
    iget-object v1, v0, Landroid/support/v4/view/ViewPager$SavedState;->loader:Ljava/lang/ClassLoader;

    #@31
    iput-object v1, p0, Landroid/support/v4/view/ViewPager;->mRestoredClassLoader:Ljava/lang/ClassLoader;

    #@33
    goto :goto_0
.end method

.method public onSaveInstanceState()Landroid/os/Parcelable;
    .locals 3

    #@0
    .prologue
    .line 1266
    invoke-super {p0}, Landroid/view/ViewGroup;->onSaveInstanceState()Landroid/os/Parcelable;

    #@3
    move-result-object v1

    #@4
    .line 1267
    .local v1, "superState":Landroid/os/Parcelable;
    new-instance v0, Landroid/support/v4/view/ViewPager$SavedState;

    #@6
    invoke-direct {v0, v1}, Landroid/support/v4/view/ViewPager$SavedState;-><init>(Landroid/os/Parcelable;)V

    #@9
    .line 1268
    .local v0, "ss":Landroid/support/v4/view/ViewPager$SavedState;
    iget v2, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@b
    iput v2, v0, Landroid/support/v4/view/ViewPager$SavedState;->position:I

    #@d
    .line 1269
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@f
    if-eqz v2, :cond_0

    #@11
    .line 1270
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@13
    invoke-virtual {v2}, Landroid/support/v4/view/PagerAdapter;->saveState()Landroid/os/Parcelable;

    #@16
    move-result-object v2

    #@17
    iput-object v2, v0, Landroid/support/v4/view/ViewPager$SavedState;->adapterState:Landroid/os/Parcelable;

    #@19
    .line 1272
    :cond_0
    return-object v0
.end method

.method protected onSizeChanged(IIII)V
    .locals 2
    .param p1, "w"    # I
    .param p2, "h"    # I
    .param p3, "oldw"    # I
    .param p4, "oldh"    # I

    #@0
    .prologue
    .line 1464
    invoke-super {p0, p1, p2, p3, p4}, Landroid/view/ViewGroup;->onSizeChanged(IIII)V

    #@3
    .line 1467
    if-eq p1, p3, :cond_0

    #@5
    .line 1468
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@7
    iget v1, p0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@9
    invoke-direct {p0, p1, p3, v0, v1}, Landroid/support/v4/view/ViewPager;->recomputeScrollPosition(IIII)V

    #@c
    .line 1470
    :cond_0
    return-void
.end method

.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 28
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 1930
    move-object/from16 v0, p0

    #@2
    iget-boolean v0, v0, Landroid/support/v4/view/ViewPager;->mFakeDragging:Z

    #@4
    move/from16 v24, v0

    #@6
    if-eqz v24, :cond_0

    #@8
    .line 1934
    const/16 v24, 0x1

    #@a
    .line 2051
    :goto_0
    return v24

    #@b
    .line 1937
    :cond_0
    invoke-virtual/range {p1 .. p1}, Landroid/view/MotionEvent;->getAction()I

    #@e
    move-result v24

    #@f
    if-nez v24, :cond_1

    #@11
    invoke-virtual/range {p1 .. p1}, Landroid/view/MotionEvent;->getEdgeFlags()I

    #@14
    move-result v24

    #@15
    if-eqz v24, :cond_1

    #@17
    .line 1940
    const/16 v24, 0x0

    #@19
    goto :goto_0

    #@1a
    .line 1943
    :cond_1
    move-object/from16 v0, p0

    #@1c
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@1e
    move-object/from16 v24, v0

    #@20
    if-eqz v24, :cond_2

    #@22
    move-object/from16 v0, p0

    #@24
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@26
    move-object/from16 v24, v0

    #@28
    invoke-virtual/range {v24 .. v24}, Landroid/support/v4/view/PagerAdapter;->getCount()I

    #@2b
    move-result v24

    #@2c
    if-nez v24, :cond_3

    #@2e
    .line 1945
    :cond_2
    const/16 v24, 0x0

    #@30
    goto :goto_0

    #@31
    .line 1948
    :cond_3
    move-object/from16 v0, p0

    #@33
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@35
    move-object/from16 v24, v0

    #@37
    if-nez v24, :cond_4

    #@39
    .line 1949
    invoke-static {}, Landroid/view/VelocityTracker;->obtain()Landroid/view/VelocityTracker;

    #@3c
    move-result-object v24

    #@3d
    move-object/from16 v0, v24

    #@3f
    move-object/from16 v1, p0

    #@41
    iput-object v0, v1, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@43
    .line 1951
    :cond_4
    move-object/from16 v0, p0

    #@45
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@47
    move-object/from16 v24, v0

    #@49
    move-object/from16 v0, v24

    #@4b
    move-object/from16 v1, p1

    #@4d
    invoke-virtual {v0, v1}, Landroid/view/VelocityTracker;->addMovement(Landroid/view/MotionEvent;)V

    #@50
    .line 1953
    invoke-virtual/range {p1 .. p1}, Landroid/view/MotionEvent;->getAction()I

    #@53
    move-result v5

    #@54
    .line 1954
    .local v5, "action":I
    const/4 v11, 0x0

    #@55
    .line 1956
    .local v11, "needsInvalidate":Z
    and-int/lit16 v0, v5, 0xff

    #@57
    move/from16 v24, v0

    #@59
    packed-switch v24, :pswitch_data_0

    #@5c
    .line 2048
    :cond_5
    :goto_1
    :pswitch_0
    if-eqz v11, :cond_6

    #@5e
    .line 2049
    invoke-static/range {p0 .. p0}, Landroid/support/v4/view/ViewCompat;->postInvalidateOnAnimation(Landroid/view/View;)V

    #@61
    .line 2051
    :cond_6
    const/16 v24, 0x1

    #@63
    goto :goto_0

    #@64
    .line 1958
    :pswitch_1
    move-object/from16 v0, p0

    #@66
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@68
    move-object/from16 v24, v0

    #@6a
    invoke-virtual/range {v24 .. v24}, Landroid/widget/Scroller;->abortAnimation()V

    #@6d
    .line 1959
    const/16 v24, 0x0

    #@6f
    move/from16 v0, v24

    #@71
    move-object/from16 v1, p0

    #@73
    iput-boolean v0, v1, Landroid/support/v4/view/ViewPager;->mPopulatePending:Z

    #@75
    .line 1960
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->populate()V

    #@78
    .line 1963
    invoke-virtual/range {p1 .. p1}, Landroid/view/MotionEvent;->getX()F

    #@7b
    move-result v24

    #@7c
    move/from16 v0, v24

    #@7e
    move-object/from16 v1, p0

    #@80
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mInitialMotionX:F

    #@82
    move/from16 v0, v24

    #@84
    move-object/from16 v1, p0

    #@86
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@88
    .line 1964
    invoke-virtual/range {p1 .. p1}, Landroid/view/MotionEvent;->getY()F

    #@8b
    move-result v24

    #@8c
    move/from16 v0, v24

    #@8e
    move-object/from16 v1, p0

    #@90
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mInitialMotionY:F

    #@92
    move/from16 v0, v24

    #@94
    move-object/from16 v1, p0

    #@96
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mLastMotionY:F

    #@98
    .line 1965
    const/16 v24, 0x0

    #@9a
    move-object/from16 v0, p1

    #@9c
    move/from16 v1, v24

    #@9e
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@a1
    move-result v24

    #@a2
    move/from16 v0, v24

    #@a4
    move-object/from16 v1, p0

    #@a6
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@a8
    goto :goto_1

    #@a9
    .line 1969
    :pswitch_2
    move-object/from16 v0, p0

    #@ab
    iget-boolean v0, v0, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@ad
    move/from16 v24, v0

    #@af
    if-nez v24, :cond_7

    #@b1
    .line 1970
    move-object/from16 v0, p0

    #@b3
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@b5
    move/from16 v24, v0

    #@b7
    move-object/from16 v0, p1

    #@b9
    move/from16 v1, v24

    #@bb
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->findPointerIndex(Landroid/view/MotionEvent;I)I

    #@be
    move-result v15

    #@bf
    .line 1971
    .local v15, "pointerIndex":I
    move-object/from16 v0, p1

    #@c1
    invoke-static {v0, v15}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@c4
    move-result v20

    #@c5
    .line 1972
    .local v20, "x":F
    move-object/from16 v0, p0

    #@c7
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@c9
    move/from16 v24, v0

    #@cb
    sub-float v24, v20, v24

    #@cd
    invoke-static/range {v24 .. v24}, Ljava/lang/Math;->abs(F)F

    #@d0
    move-result v21

    #@d1
    .line 1973
    .local v21, "xDiff":F
    move-object/from16 v0, p1

    #@d3
    invoke-static {v0, v15}, Landroid/support/v4/view/MotionEventCompat;->getY(Landroid/view/MotionEvent;I)F

    #@d6
    move-result v22

    #@d7
    .line 1974
    .local v22, "y":F
    move-object/from16 v0, p0

    #@d9
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mLastMotionY:F

    #@db
    move/from16 v24, v0

    #@dd
    sub-float v24, v22, v24

    #@df
    invoke-static/range {v24 .. v24}, Ljava/lang/Math;->abs(F)F

    #@e2
    move-result v23

    #@e3
    .line 1976
    .local v23, "yDiff":F
    move-object/from16 v0, p0

    #@e5
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mTouchSlop:I

    #@e7
    move/from16 v24, v0

    #@e9
    move/from16 v0, v24

    #@eb
    int-to-float v0, v0

    #@ec
    move/from16 v24, v0

    #@ee
    cmpl-float v24, v21, v24

    #@f0
    if-lez v24, :cond_7

    #@f2
    cmpl-float v24, v21, v23

    #@f4
    if-lez v24, :cond_7

    #@f6
    .line 1978
    const/16 v24, 0x1

    #@f8
    move/from16 v0, v24

    #@fa
    move-object/from16 v1, p0

    #@fc
    iput-boolean v0, v1, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@fe
    .line 1979
    const/16 v24, 0x1

    #@100
    move-object/from16 v0, p0

    #@102
    move/from16 v1, v24

    #@104
    invoke-direct {v0, v1}, Landroid/support/v4/view/ViewPager;->requestParentDisallowInterceptTouchEvent(Z)V

    #@107
    .line 1980
    move-object/from16 v0, p0

    #@109
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mInitialMotionX:F

    #@10b
    move/from16 v24, v0

    #@10d
    sub-float v24, v20, v24

    #@10f
    const/16 v25, 0x0

    #@111
    cmpl-float v24, v24, v25

    #@113
    if-lez v24, :cond_8

    #@115
    move-object/from16 v0, p0

    #@117
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mInitialMotionX:F

    #@119
    move/from16 v24, v0

    #@11b
    move-object/from16 v0, p0

    #@11d
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mTouchSlop:I

    #@11f
    move/from16 v25, v0

    #@121
    move/from16 v0, v25

    #@123
    int-to-float v0, v0

    #@124
    move/from16 v25, v0

    #@126
    add-float v24, v24, v25

    #@128
    :goto_2
    move/from16 v0, v24

    #@12a
    move-object/from16 v1, p0

    #@12c
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@12e
    .line 1982
    move/from16 v0, v22

    #@130
    move-object/from16 v1, p0

    #@132
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mLastMotionY:F

    #@134
    .line 1983
    const/16 v24, 0x1

    #@136
    move-object/from16 v0, p0

    #@138
    move/from16 v1, v24

    #@13a
    invoke-direct {v0, v1}, Landroid/support/v4/view/ViewPager;->setScrollState(I)V

    #@13d
    .line 1984
    const/16 v24, 0x1

    #@13f
    move-object/from16 v0, p0

    #@141
    move/from16 v1, v24

    #@143
    invoke-direct {v0, v1}, Landroid/support/v4/view/ViewPager;->setScrollingCacheEnabled(Z)V

    #@146
    .line 1987
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getParent()Landroid/view/ViewParent;

    #@149
    move-result-object v14

    #@14a
    .line 1988
    .local v14, "parent":Landroid/view/ViewParent;
    if-eqz v14, :cond_7

    #@14c
    .line 1989
    const/16 v24, 0x1

    #@14e
    move/from16 v0, v24

    #@150
    invoke-interface {v14, v0}, Landroid/view/ViewParent;->requestDisallowInterceptTouchEvent(Z)V

    #@153
    .line 1994
    .end local v14    # "parent":Landroid/view/ViewParent;
    .end local v15    # "pointerIndex":I
    .end local v20    # "x":F
    .end local v21    # "xDiff":F
    .end local v22    # "y":F
    .end local v23    # "yDiff":F
    :cond_7
    move-object/from16 v0, p0

    #@155
    iget-boolean v0, v0, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@157
    move/from16 v24, v0

    #@159
    if-eqz v24, :cond_5

    #@15b
    .line 1996
    move-object/from16 v0, p0

    #@15d
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@15f
    move/from16 v24, v0

    #@161
    move-object/from16 v0, p1

    #@163
    move/from16 v1, v24

    #@165
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->findPointerIndex(Landroid/view/MotionEvent;I)I

    #@168
    move-result v6

    #@169
    .line 1998
    .local v6, "activePointerIndex":I
    move-object/from16 v0, p1

    #@16b
    invoke-static {v0, v6}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@16e
    move-result v20

    #@16f
    .line 1999
    .restart local v20    # "x":F
    move-object/from16 v0, p0

    #@171
    move/from16 v1, v20

    #@173
    invoke-direct {v0, v1}, Landroid/support/v4/view/ViewPager;->performDrag(F)Z

    #@176
    move-result v24

    #@177
    or-int v11, v11, v24

    #@179
    .line 2000
    goto/16 :goto_1

    #@17b
    .line 1980
    .end local v6    # "activePointerIndex":I
    .restart local v15    # "pointerIndex":I
    .restart local v21    # "xDiff":F
    .restart local v22    # "y":F
    .restart local v23    # "yDiff":F
    :cond_8
    move-object/from16 v0, p0

    #@17d
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mInitialMotionX:F

    #@17f
    move/from16 v24, v0

    #@181
    move-object/from16 v0, p0

    #@183
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mTouchSlop:I

    #@185
    move/from16 v25, v0

    #@187
    move/from16 v0, v25

    #@189
    int-to-float v0, v0

    #@18a
    move/from16 v25, v0

    #@18c
    sub-float v24, v24, v25

    #@18e
    goto :goto_2

    #@18f
    .line 2003
    .end local v15    # "pointerIndex":I
    .end local v20    # "x":F
    .end local v21    # "xDiff":F
    .end local v22    # "y":F
    .end local v23    # "yDiff":F
    :pswitch_3
    move-object/from16 v0, p0

    #@191
    iget-boolean v0, v0, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@193
    move/from16 v24, v0

    #@195
    if-eqz v24, :cond_5

    #@197
    .line 2004
    move-object/from16 v0, p0

    #@199
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mVelocityTracker:Landroid/view/VelocityTracker;

    #@19b
    move-object/from16 v18, v0

    #@19d
    .line 2005
    .local v18, "velocityTracker":Landroid/view/VelocityTracker;
    const/16 v24, 0x3e8

    #@19f
    move-object/from16 v0, p0

    #@1a1
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mMaximumVelocity:I

    #@1a3
    move/from16 v25, v0

    #@1a5
    move/from16 v0, v25

    #@1a7
    int-to-float v0, v0

    #@1a8
    move/from16 v25, v0

    #@1aa
    move-object/from16 v0, v18

    #@1ac
    move/from16 v1, v24

    #@1ae
    move/from16 v2, v25

    #@1b0
    invoke-virtual {v0, v1, v2}, Landroid/view/VelocityTracker;->computeCurrentVelocity(IF)V

    #@1b3
    .line 2006
    move-object/from16 v0, p0

    #@1b5
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@1b7
    move/from16 v24, v0

    #@1b9
    move-object/from16 v0, v18

    #@1bb
    move/from16 v1, v24

    #@1bd
    invoke-static {v0, v1}, Landroid/support/v4/view/VelocityTrackerCompat;->getXVelocity(Landroid/view/VelocityTracker;I)F

    #@1c0
    move-result v24

    #@1c1
    move/from16 v0, v24

    #@1c3
    float-to-int v10, v0

    #@1c4
    .line 2008
    .local v10, "initialVelocity":I
    const/16 v24, 0x1

    #@1c6
    move/from16 v0, v24

    #@1c8
    move-object/from16 v1, p0

    #@1ca
    iput-boolean v0, v1, Landroid/support/v4/view/ViewPager;->mPopulatePending:Z

    #@1cc
    .line 2009
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getClientWidth()I

    #@1cf
    move-result v19

    #@1d0
    .line 2010
    .local v19, "width":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@1d3
    move-result v16

    #@1d4
    .line 2011
    .local v16, "scrollX":I
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->infoForCurrentScrollPosition()Landroid/support/v4/view/ViewPager$ItemInfo;

    #@1d7
    move-result-object v8

    #@1d8
    .line 2012
    .local v8, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    iget v7, v8, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@1da
    .line 2013
    .local v7, "currentPage":I
    move/from16 v0, v16

    #@1dc
    int-to-float v0, v0

    #@1dd
    move/from16 v24, v0

    #@1df
    move/from16 v0, v19

    #@1e1
    int-to-float v0, v0

    #@1e2
    move/from16 v25, v0

    #@1e4
    div-float v24, v24, v25

    #@1e6
    iget v0, v8, Landroid/support/v4/view/ViewPager$ItemInfo;->offset:F

    #@1e8
    move/from16 v25, v0

    #@1ea
    sub-float v24, v24, v25

    #@1ec
    iget v0, v8, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@1ee
    move/from16 v25, v0

    #@1f0
    div-float v13, v24, v25

    #@1f2
    .line 2014
    .local v13, "pageOffset":F
    move-object/from16 v0, p0

    #@1f4
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@1f6
    move/from16 v24, v0

    #@1f8
    move-object/from16 v0, p1

    #@1fa
    move/from16 v1, v24

    #@1fc
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->findPointerIndex(Landroid/view/MotionEvent;I)I

    #@1ff
    move-result v6

    #@200
    .line 2016
    .restart local v6    # "activePointerIndex":I
    move-object/from16 v0, p1

    #@202
    invoke-static {v0, v6}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@205
    move-result v20

    #@206
    .line 2017
    .restart local v20    # "x":F
    move-object/from16 v0, p0

    #@208
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mInitialMotionX:F

    #@20a
    move/from16 v24, v0

    #@20c
    sub-float v24, v20, v24

    #@20e
    move/from16 v0, v24

    #@210
    float-to-int v0, v0

    #@211
    move/from16 v17, v0

    #@213
    .line 2018
    .local v17, "totalDelta":I
    move-object/from16 v0, p0

    #@215
    move/from16 v1, v17

    #@217
    invoke-direct {v0, v7, v13, v10, v1}, Landroid/support/v4/view/ViewPager;->determineTargetPage(IFII)I

    #@21a
    move-result v12

    #@21b
    .line 2020
    .local v12, "nextPage":I
    const/16 v24, 0x1

    #@21d
    const/16 v25, 0x1

    #@21f
    move-object/from16 v0, p0

    #@221
    move/from16 v1, v24

    #@223
    move/from16 v2, v25

    #@225
    invoke-virtual {v0, v12, v1, v2, v10}, Landroid/support/v4/view/ViewPager;->setCurrentItemInternal(IZZI)V

    #@228
    .line 2022
    const/16 v24, -0x1

    #@22a
    move/from16 v0, v24

    #@22c
    move-object/from16 v1, p0

    #@22e
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@230
    .line 2023
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->endDrag()V

    #@233
    .line 2024
    move-object/from16 v0, p0

    #@235
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mLeftEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@237
    move-object/from16 v24, v0

    #@239
    invoke-virtual/range {v24 .. v24}, Landroid/support/v4/widget/EdgeEffectCompat;->onRelease()Z

    #@23c
    move-result v24

    #@23d
    move-object/from16 v0, p0

    #@23f
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mRightEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@241
    move-object/from16 v25, v0

    #@243
    invoke-virtual/range {v25 .. v25}, Landroid/support/v4/widget/EdgeEffectCompat;->onRelease()Z

    #@246
    move-result v25

    #@247
    or-int v11, v24, v25

    #@249
    .line 2025
    goto/16 :goto_1

    #@24b
    .line 2028
    .end local v6    # "activePointerIndex":I
    .end local v7    # "currentPage":I
    .end local v8    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v10    # "initialVelocity":I
    .end local v12    # "nextPage":I
    .end local v13    # "pageOffset":F
    .end local v16    # "scrollX":I
    .end local v17    # "totalDelta":I
    .end local v18    # "velocityTracker":Landroid/view/VelocityTracker;
    .end local v19    # "width":I
    .end local v20    # "x":F
    :pswitch_4
    move-object/from16 v0, p0

    #@24d
    iget-boolean v0, v0, Landroid/support/v4/view/ViewPager;->mIsBeingDragged:Z

    #@24f
    move/from16 v24, v0

    #@251
    if-eqz v24, :cond_5

    #@253
    .line 2029
    move-object/from16 v0, p0

    #@255
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@257
    move/from16 v24, v0

    #@259
    const/16 v25, 0x1

    #@25b
    const/16 v26, 0x0

    #@25d
    const/16 v27, 0x0

    #@25f
    move-object/from16 v0, p0

    #@261
    move/from16 v1, v24

    #@263
    move/from16 v2, v25

    #@265
    move/from16 v3, v26

    #@267
    move/from16 v4, v27

    #@269
    invoke-direct {v0, v1, v2, v3, v4}, Landroid/support/v4/view/ViewPager;->scrollToItem(IZIZ)V

    #@26c
    .line 2030
    const/16 v24, -0x1

    #@26e
    move/from16 v0, v24

    #@270
    move-object/from16 v1, p0

    #@272
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@274
    .line 2031
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->endDrag()V

    #@277
    .line 2032
    move-object/from16 v0, p0

    #@279
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mLeftEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@27b
    move-object/from16 v24, v0

    #@27d
    invoke-virtual/range {v24 .. v24}, Landroid/support/v4/widget/EdgeEffectCompat;->onRelease()Z

    #@280
    move-result v24

    #@281
    move-object/from16 v0, p0

    #@283
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mRightEdge:Landroid/support/v4/widget/EdgeEffectCompat;

    #@285
    move-object/from16 v25, v0

    #@287
    invoke-virtual/range {v25 .. v25}, Landroid/support/v4/widget/EdgeEffectCompat;->onRelease()Z

    #@28a
    move-result v25

    #@28b
    or-int v11, v24, v25

    #@28d
    goto/16 :goto_1

    #@28f
    .line 2036
    :pswitch_5
    invoke-static/range {p1 .. p1}, Landroid/support/v4/view/MotionEventCompat;->getActionIndex(Landroid/view/MotionEvent;)I

    #@292
    move-result v9

    #@293
    .line 2037
    .local v9, "index":I
    move-object/from16 v0, p1

    #@295
    invoke-static {v0, v9}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@298
    move-result v20

    #@299
    .line 2038
    .restart local v20    # "x":F
    move/from16 v0, v20

    #@29b
    move-object/from16 v1, p0

    #@29d
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@29f
    .line 2039
    move-object/from16 v0, p1

    #@2a1
    invoke-static {v0, v9}, Landroid/support/v4/view/MotionEventCompat;->getPointerId(Landroid/view/MotionEvent;I)I

    #@2a4
    move-result v24

    #@2a5
    move/from16 v0, v24

    #@2a7
    move-object/from16 v1, p0

    #@2a9
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@2ab
    goto/16 :goto_1

    #@2ad
    .line 2043
    .end local v9    # "index":I
    .end local v20    # "x":F
    :pswitch_6
    invoke-direct/range {p0 .. p1}, Landroid/support/v4/view/ViewPager;->onSecondaryPointerUp(Landroid/view/MotionEvent;)V

    #@2b0
    .line 2044
    move-object/from16 v0, p0

    #@2b2
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mActivePointerId:I

    #@2b4
    move/from16 v24, v0

    #@2b6
    move-object/from16 v0, p1

    #@2b8
    move/from16 v1, v24

    #@2ba
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->findPointerIndex(Landroid/view/MotionEvent;I)I

    #@2bd
    move-result v24

    #@2be
    move-object/from16 v0, p1

    #@2c0
    move/from16 v1, v24

    #@2c2
    invoke-static {v0, v1}, Landroid/support/v4/view/MotionEventCompat;->getX(Landroid/view/MotionEvent;I)F

    #@2c5
    move-result v24

    #@2c6
    move/from16 v0, v24

    #@2c8
    move-object/from16 v1, p0

    #@2ca
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mLastMotionX:F

    #@2cc
    goto/16 :goto_1

    #@2ce
    .line 1956
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_3
        :pswitch_2
        :pswitch_4
        :pswitch_0
        :pswitch_5
        :pswitch_6
    .end packed-switch
.end method

.method pageLeft()Z
    .locals 2

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 2611
    iget v1, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@3
    if-lez v1, :cond_0

    #@5
    .line 2612
    iget v1, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@7
    add-int/lit8 v1, v1, -0x1

    #@9
    invoke-virtual {p0, v1, v0}, Landroid/support/v4/view/ViewPager;->setCurrentItem(IZ)V

    #@c
    .line 2615
    :goto_0
    return v0

    #@d
    :cond_0
    const/4 v0, 0x0

    #@e
    goto :goto_0
.end method

.method pageRight()Z
    .locals 3

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 2619
    iget-object v1, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@3
    if-eqz v1, :cond_0

    #@5
    iget v1, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@7
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@9
    invoke-virtual {v2}, Landroid/support/v4/view/PagerAdapter;->getCount()I

    #@c
    move-result v2

    #@d
    add-int/lit8 v2, v2, -0x1

    #@f
    if-ge v1, v2, :cond_0

    #@11
    .line 2620
    iget v1, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@13
    add-int/lit8 v1, v1, 0x1

    #@15
    invoke-virtual {p0, v1, v0}, Landroid/support/v4/view/ViewPager;->setCurrentItem(IZ)V

    #@18
    .line 2623
    :goto_0
    return v0

    #@19
    :cond_0
    const/4 v0, 0x0

    #@1a
    goto :goto_0
.end method

.method populate()V
    .locals 1

    #@0
    .prologue
    .line 919
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@2
    invoke-virtual {p0, v0}, Landroid/support/v4/view/ViewPager;->populate(I)V

    #@5
    .line 920
    return-void
.end method

.method populate(I)V
    .locals 30
    .param p1, "newCurrentItem"    # I

    #@0
    .prologue
    .line 923
    const/16 v21, 0x0

    #@2
    .line 924
    .local v21, "oldCurInfo":Landroid/support/v4/view/ViewPager$ItemInfo;
    const/4 v15, 0x2

    #@3
    .line 925
    .local v15, "focusDirection":I
    move-object/from16 v0, p0

    #@5
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@7
    move/from16 v27, v0

    #@9
    move/from16 v0, v27

    #@b
    move/from16 v1, p1

    #@d
    if-eq v0, v1, :cond_0

    #@f
    .line 926
    move-object/from16 v0, p0

    #@11
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@13
    move/from16 v27, v0

    #@15
    move/from16 v0, v27

    #@17
    move/from16 v1, p1

    #@19
    if-ge v0, v1, :cond_2

    #@1b
    const/16 v15, 0x42

    #@1d
    .line 927
    :goto_0
    move-object/from16 v0, p0

    #@1f
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@21
    move/from16 v27, v0

    #@23
    move-object/from16 v0, p0

    #@25
    move/from16 v1, v27

    #@27
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ViewPager;->infoForPosition(I)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@2a
    move-result-object v21

    #@2b
    .line 928
    move/from16 v0, p1

    #@2d
    move-object/from16 v1, p0

    #@2f
    iput v0, v1, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@31
    .line 931
    :cond_0
    move-object/from16 v0, p0

    #@33
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@35
    move-object/from16 v27, v0

    #@37
    if-nez v27, :cond_3

    #@39
    .line 932
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->sortChildDrawingOrder()V

    #@3c
    .line 1108
    :cond_1
    :goto_1
    return-void

    #@3d
    .line 926
    :cond_2
    const/16 v15, 0x11

    #@3f
    goto :goto_0

    #@40
    .line 940
    :cond_3
    move-object/from16 v0, p0

    #@42
    iget-boolean v0, v0, Landroid/support/v4/view/ViewPager;->mPopulatePending:Z

    #@44
    move/from16 v27, v0

    #@46
    if-eqz v27, :cond_4

    #@48
    .line 942
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->sortChildDrawingOrder()V

    #@4b
    goto :goto_1

    #@4c
    .line 949
    :cond_4
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getWindowToken()Landroid/os/IBinder;

    #@4f
    move-result-object v27

    #@50
    if-eqz v27, :cond_1

    #@52
    .line 953
    move-object/from16 v0, p0

    #@54
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@56
    move-object/from16 v27, v0

    #@58
    move-object/from16 v0, v27

    #@5a
    move-object/from16 v1, p0

    #@5c
    invoke-virtual {v0, v1}, Landroid/support/v4/view/PagerAdapter;->startUpdate(Landroid/view/ViewGroup;)V

    #@5f
    .line 955
    move-object/from16 v0, p0

    #@61
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mOffscreenPageLimit:I

    #@63
    move/from16 v22, v0

    #@65
    .line 956
    .local v22, "pageLimit":I
    const/16 v27, 0x0

    #@67
    move-object/from16 v0, p0

    #@69
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@6b
    move/from16 v28, v0

    #@6d
    sub-int v28, v28, v22

    #@6f
    invoke-static/range {v27 .. v28}, Ljava/lang/Math;->max(II)I

    #@72
    move-result v26

    #@73
    .line 957
    .local v26, "startPos":I
    move-object/from16 v0, p0

    #@75
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@77
    move-object/from16 v27, v0

    #@79
    invoke-virtual/range {v27 .. v27}, Landroid/support/v4/view/PagerAdapter;->getCount()I

    #@7c
    move-result v4

    #@7d
    .line 958
    .local v4, "N":I
    add-int/lit8 v27, v4, -0x1

    #@7f
    move-object/from16 v0, p0

    #@81
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@83
    move/from16 v28, v0

    #@85
    add-int v28, v28, v22

    #@87
    invoke-static/range {v27 .. v28}, Ljava/lang/Math;->min(II)I

    #@8a
    move-result v12

    #@8b
    .line 960
    .local v12, "endPos":I
    move-object/from16 v0, p0

    #@8d
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mExpectedAdapterCount:I

    #@8f
    move/from16 v27, v0

    #@91
    move/from16 v0, v27

    #@93
    if-eq v4, v0, :cond_5

    #@95
    .line 963
    :try_start_0
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getResources()Landroid/content/res/Resources;

    #@98
    move-result-object v27

    #@99
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getId()I

    #@9c
    move-result v28

    #@9d
    invoke-virtual/range {v27 .. v28}, Landroid/content/res/Resources;->getResourceName(I)Ljava/lang/String;
    :try_end_0
    .catch Landroid/content/res/Resources$NotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    #@a0
    move-result-object v24

    #@a1
    .line 967
    .local v24, "resName":Ljava/lang/String;
    :goto_2
    new-instance v27, Ljava/lang/IllegalStateException;

    #@a3
    new-instance v28, Ljava/lang/StringBuilder;

    #@a5
    invoke-direct/range {v28 .. v28}, Ljava/lang/StringBuilder;-><init>()V

    #@a8
    const-string v29, "The application\'s PagerAdapter changed the adapter\'s contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: "

    #@aa
    invoke-virtual/range {v28 .. v29}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@ad
    move-result-object v28

    #@ae
    move-object/from16 v0, p0

    #@b0
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mExpectedAdapterCount:I

    #@b2
    move/from16 v29, v0

    #@b4
    invoke-virtual/range {v28 .. v29}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@b7
    move-result-object v28

    #@b8
    const-string v29, ", found: "

    #@ba
    invoke-virtual/range {v28 .. v29}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@bd
    move-result-object v28

    #@be
    move-object/from16 v0, v28

    #@c0
    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@c3
    move-result-object v28

    #@c4
    const-string v29, " Pager id: "

    #@c6
    invoke-virtual/range {v28 .. v29}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@c9
    move-result-object v28

    #@ca
    move-object/from16 v0, v28

    #@cc
    move-object/from16 v1, v24

    #@ce
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@d1
    move-result-object v28

    #@d2
    const-string v29, " Pager class: "

    #@d4
    invoke-virtual/range {v28 .. v29}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@d7
    move-result-object v28

    #@d8
    invoke-virtual/range {p0 .. p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@db
    move-result-object v29

    #@dc
    invoke-virtual/range {v28 .. v29}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@df
    move-result-object v28

    #@e0
    const-string v29, " Problematic adapter: "

    #@e2
    invoke-virtual/range {v28 .. v29}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@e5
    move-result-object v28

    #@e6
    move-object/from16 v0, p0

    #@e8
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@ea
    move-object/from16 v29, v0

    #@ec
    invoke-virtual/range {v29 .. v29}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@ef
    move-result-object v29

    #@f0
    invoke-virtual/range {v28 .. v29}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@f3
    move-result-object v28

    #@f4
    invoke-virtual/range {v28 .. v28}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@f7
    move-result-object v28

    #@f8
    invoke-direct/range {v27 .. v28}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@fb
    throw v27

    #@fc
    .line 964
    .end local v24    # "resName":Ljava/lang/String;
    :catch_0
    move-exception v11

    #@fd
    .line 965
    .local v11, "e":Landroid/content/res/Resources$NotFoundException;
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getId()I

    #@100
    move-result v27

    #@101
    invoke-static/range {v27 .. v27}, Ljava/lang/Integer;->toHexString(I)Ljava/lang/String;

    #@104
    move-result-object v24

    #@105
    .restart local v24    # "resName":Ljava/lang/String;
    goto :goto_2

    #@106
    .line 976
    .end local v11    # "e":Landroid/content/res/Resources$NotFoundException;
    .end local v24    # "resName":Ljava/lang/String;
    :cond_5
    const/4 v8, -0x1

    #@107
    .line 977
    .local v8, "curIndex":I
    const/4 v9, 0x0

    #@108
    .line 978
    .local v9, "curItem":Landroid/support/v4/view/ViewPager$ItemInfo;
    const/4 v8, 0x0

    #@109
    :goto_3
    move-object/from16 v0, p0

    #@10b
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@10d
    move-object/from16 v27, v0

    #@10f
    invoke-virtual/range {v27 .. v27}, Ljava/util/ArrayList;->size()I

    #@112
    move-result v27

    #@113
    move/from16 v0, v27

    #@115
    if-ge v8, v0, :cond_6

    #@117
    .line 979
    move-object/from16 v0, p0

    #@119
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@11b
    move-object/from16 v27, v0

    #@11d
    move-object/from16 v0, v27

    #@11f
    invoke-virtual {v0, v8}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@122
    move-result-object v17

    #@123
    check-cast v17, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@125
    .line 980
    .local v17, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    move-object/from16 v0, v17

    #@127
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@129
    move/from16 v27, v0

    #@12b
    move-object/from16 v0, p0

    #@12d
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@12f
    move/from16 v28, v0

    #@131
    move/from16 v0, v27

    #@133
    move/from16 v1, v28

    #@135
    if-lt v0, v1, :cond_c

    #@137
    .line 981
    move-object/from16 v0, v17

    #@139
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@13b
    move/from16 v27, v0

    #@13d
    move-object/from16 v0, p0

    #@13f
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@141
    move/from16 v28, v0

    #@143
    move/from16 v0, v27

    #@145
    move/from16 v1, v28

    #@147
    if-ne v0, v1, :cond_6

    #@149
    move-object/from16 v9, v17

    #@14b
    .line 986
    .end local v17    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_6
    if-nez v9, :cond_7

    #@14d
    if-lez v4, :cond_7

    #@14f
    .line 987
    move-object/from16 v0, p0

    #@151
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@153
    move/from16 v27, v0

    #@155
    move-object/from16 v0, p0

    #@157
    move/from16 v1, v27

    #@159
    invoke-virtual {v0, v1, v8}, Landroid/support/v4/view/ViewPager;->addNewItem(II)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@15c
    move-result-object v9

    #@15d
    .line 993
    :cond_7
    if-eqz v9, :cond_a

    #@15f
    .line 994
    const/4 v13, 0x0

    #@160
    .line 995
    .local v13, "extraWidthLeft":F
    add-int/lit8 v18, v8, -0x1

    #@162
    .line 996
    .local v18, "itemIndex":I
    if-ltz v18, :cond_d

    #@164
    move-object/from16 v0, p0

    #@166
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@168
    move-object/from16 v27, v0

    #@16a
    move-object/from16 v0, v27

    #@16c
    move/from16 v1, v18

    #@16e
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@171
    move-result-object v27

    #@172
    check-cast v27, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@174
    move-object/from16 v17, v27

    #@176
    .line 997
    .restart local v17    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :goto_4
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getClientWidth()I

    #@179
    move-result v7

    #@17a
    .line 998
    .local v7, "clientWidth":I
    if-gtz v7, :cond_e

    #@17c
    const/16 v19, 0x0

    #@17e
    .line 1000
    .local v19, "leftWidthNeeded":F
    :goto_5
    move-object/from16 v0, p0

    #@180
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@182
    move/from16 v27, v0

    #@184
    add-int/lit8 v23, v27, -0x1

    #@186
    .local v23, "pos":I
    :goto_6
    if-ltz v23, :cond_8

    #@188
    .line 1001
    cmpl-float v27, v13, v19

    #@18a
    if-ltz v27, :cond_12

    #@18c
    move/from16 v0, v23

    #@18e
    move/from16 v1, v26

    #@190
    if-ge v0, v1, :cond_12

    #@192
    .line 1002
    if-nez v17, :cond_f

    #@194
    .line 1028
    :cond_8
    iget v14, v9, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@196
    .line 1029
    .local v14, "extraWidthRight":F
    add-int/lit8 v18, v8, 0x1

    #@198
    .line 1030
    const/high16 v27, 0x40000000    # 2.0f

    #@19a
    cmpg-float v27, v14, v27

    #@19c
    if-gez v27, :cond_9

    #@19e
    .line 1031
    move-object/from16 v0, p0

    #@1a0
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@1a2
    move-object/from16 v27, v0

    #@1a4
    invoke-virtual/range {v27 .. v27}, Ljava/util/ArrayList;->size()I

    #@1a7
    move-result v27

    #@1a8
    move/from16 v0, v18

    #@1aa
    move/from16 v1, v27

    #@1ac
    if-ge v0, v1, :cond_16

    #@1ae
    move-object/from16 v0, p0

    #@1b0
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@1b2
    move-object/from16 v27, v0

    #@1b4
    move-object/from16 v0, v27

    #@1b6
    move/from16 v1, v18

    #@1b8
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@1bb
    move-result-object v27

    #@1bc
    check-cast v27, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@1be
    move-object/from16 v17, v27

    #@1c0
    .line 1032
    :goto_7
    if-gtz v7, :cond_17

    #@1c2
    const/16 v25, 0x0

    #@1c4
    .line 1034
    .local v25, "rightWidthNeeded":F
    :goto_8
    move-object/from16 v0, p0

    #@1c6
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@1c8
    move/from16 v27, v0

    #@1ca
    add-int/lit8 v23, v27, 0x1

    #@1cc
    :goto_9
    move/from16 v0, v23

    #@1ce
    if-ge v0, v4, :cond_9

    #@1d0
    .line 1035
    cmpl-float v27, v14, v25

    #@1d2
    if-ltz v27, :cond_1b

    #@1d4
    move/from16 v0, v23

    #@1d6
    if-le v0, v12, :cond_1b

    #@1d8
    .line 1036
    if-nez v17, :cond_18

    #@1da
    .line 1061
    .end local v25    # "rightWidthNeeded":F
    :cond_9
    move-object/from16 v0, p0

    #@1dc
    move-object/from16 v1, v21

    #@1de
    invoke-direct {v0, v9, v8, v1}, Landroid/support/v4/view/ViewPager;->calculatePageOffsets(Landroid/support/v4/view/ViewPager$ItemInfo;ILandroid/support/v4/view/ViewPager$ItemInfo;)V

    #@1e1
    .line 1071
    .end local v7    # "clientWidth":I
    .end local v13    # "extraWidthLeft":F
    .end local v14    # "extraWidthRight":F
    .end local v17    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v18    # "itemIndex":I
    .end local v19    # "leftWidthNeeded":F
    .end local v23    # "pos":I
    :cond_a
    move-object/from16 v0, p0

    #@1e3
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@1e5
    move-object/from16 v28, v0

    #@1e7
    move-object/from16 v0, p0

    #@1e9
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@1eb
    move/from16 v29, v0

    #@1ed
    if-eqz v9, :cond_1f

    #@1ef
    iget-object v0, v9, Landroid/support/v4/view/ViewPager$ItemInfo;->object:Ljava/lang/Object;

    #@1f1
    move-object/from16 v27, v0

    #@1f3
    :goto_a
    move-object/from16 v0, v28

    #@1f5
    move-object/from16 v1, p0

    #@1f7
    move/from16 v2, v29

    #@1f9
    move-object/from16 v3, v27

    #@1fb
    invoke-virtual {v0, v1, v2, v3}, Landroid/support/v4/view/PagerAdapter;->setPrimaryItem(Landroid/view/ViewGroup;ILjava/lang/Object;)V

    #@1fe
    .line 1073
    move-object/from16 v0, p0

    #@200
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@202
    move-object/from16 v27, v0

    #@204
    move-object/from16 v0, v27

    #@206
    move-object/from16 v1, p0

    #@208
    invoke-virtual {v0, v1}, Landroid/support/v4/view/PagerAdapter;->finishUpdate(Landroid/view/ViewGroup;)V

    #@20b
    .line 1077
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@20e
    move-result v6

    #@20f
    .line 1078
    .local v6, "childCount":I
    const/16 v16, 0x0

    #@211
    .local v16, "i":I
    :goto_b
    move/from16 v0, v16

    #@213
    if-ge v0, v6, :cond_20

    #@215
    .line 1079
    move-object/from16 v0, p0

    #@217
    move/from16 v1, v16

    #@219
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@21c
    move-result-object v5

    #@21d
    .line 1080
    .local v5, "child":Landroid/view/View;
    invoke-virtual {v5}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@220
    move-result-object v20

    #@221
    check-cast v20, Landroid/support/v4/view/ViewPager$LayoutParams;

    #@223
    .line 1081
    .local v20, "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    move/from16 v0, v16

    #@225
    move-object/from16 v1, v20

    #@227
    iput v0, v1, Landroid/support/v4/view/ViewPager$LayoutParams;->childIndex:I

    #@229
    .line 1082
    move-object/from16 v0, v20

    #@22b
    iget-boolean v0, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->isDecor:Z

    #@22d
    move/from16 v27, v0

    #@22f
    if-nez v27, :cond_b

    #@231
    move-object/from16 v0, v20

    #@233
    iget v0, v0, Landroid/support/v4/view/ViewPager$LayoutParams;->widthFactor:F

    #@235
    move/from16 v27, v0

    #@237
    const/16 v28, 0x0

    #@239
    cmpl-float v27, v27, v28

    #@23b
    if-nez v27, :cond_b

    #@23d
    .line 1084
    move-object/from16 v0, p0

    #@23f
    invoke-virtual {v0, v5}, Landroid/support/v4/view/ViewPager;->infoForChild(Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@242
    move-result-object v17

    #@243
    .line 1085
    .restart local v17    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    if-eqz v17, :cond_b

    #@245
    .line 1086
    move-object/from16 v0, v17

    #@247
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@249
    move/from16 v27, v0

    #@24b
    move/from16 v0, v27

    #@24d
    move-object/from16 v1, v20

    #@24f
    iput v0, v1, Landroid/support/v4/view/ViewPager$LayoutParams;->widthFactor:F

    #@251
    .line 1087
    move-object/from16 v0, v17

    #@253
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@255
    move/from16 v27, v0

    #@257
    move/from16 v0, v27

    #@259
    move-object/from16 v1, v20

    #@25b
    iput v0, v1, Landroid/support/v4/view/ViewPager$LayoutParams;->position:I

    #@25d
    .line 1078
    .end local v17    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_b
    add-int/lit8 v16, v16, 0x1

    #@25f
    goto :goto_b

    #@260
    .line 978
    .end local v5    # "child":Landroid/view/View;
    .end local v6    # "childCount":I
    .end local v16    # "i":I
    .end local v20    # "lp":Landroid/support/v4/view/ViewPager$LayoutParams;
    .restart local v17    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_c
    add-int/lit8 v8, v8, 0x1

    #@262
    goto/16 :goto_3

    #@264
    .line 996
    .end local v17    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .restart local v13    # "extraWidthLeft":F
    .restart local v18    # "itemIndex":I
    :cond_d
    const/16 v17, 0x0

    #@266
    goto/16 :goto_4

    #@268
    .line 998
    .restart local v7    # "clientWidth":I
    .restart local v17    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_e
    const/high16 v27, 0x40000000    # 2.0f

    #@26a
    iget v0, v9, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@26c
    move/from16 v28, v0

    #@26e
    sub-float v27, v27, v28

    #@270
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getPaddingLeft()I

    #@273
    move-result v28

    #@274
    move/from16 v0, v28

    #@276
    int-to-float v0, v0

    #@277
    move/from16 v28, v0

    #@279
    int-to-float v0, v7

    #@27a
    move/from16 v29, v0

    #@27c
    div-float v28, v28, v29

    #@27e
    add-float v19, v27, v28

    #@280
    goto/16 :goto_5

    #@282
    .line 1005
    .restart local v19    # "leftWidthNeeded":F
    .restart local v23    # "pos":I
    :cond_f
    move-object/from16 v0, v17

    #@284
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@286
    move/from16 v27, v0

    #@288
    move/from16 v0, v23

    #@28a
    move/from16 v1, v27

    #@28c
    if-ne v0, v1, :cond_10

    #@28e
    move-object/from16 v0, v17

    #@290
    iget-boolean v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->scrolling:Z

    #@292
    move/from16 v27, v0

    #@294
    if-nez v27, :cond_10

    #@296
    .line 1006
    move-object/from16 v0, p0

    #@298
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@29a
    move-object/from16 v27, v0

    #@29c
    move-object/from16 v0, v27

    #@29e
    move/from16 v1, v18

    #@2a0
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->remove(I)Ljava/lang/Object;

    #@2a3
    .line 1007
    move-object/from16 v0, p0

    #@2a5
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@2a7
    move-object/from16 v27, v0

    #@2a9
    move-object/from16 v0, v17

    #@2ab
    iget-object v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->object:Ljava/lang/Object;

    #@2ad
    move-object/from16 v28, v0

    #@2af
    move-object/from16 v0, v27

    #@2b1
    move-object/from16 v1, p0

    #@2b3
    move/from16 v2, v23

    #@2b5
    move-object/from16 v3, v28

    #@2b7
    invoke-virtual {v0, v1, v2, v3}, Landroid/support/v4/view/PagerAdapter;->destroyItem(Landroid/view/ViewGroup;ILjava/lang/Object;)V

    #@2ba
    .line 1012
    add-int/lit8 v18, v18, -0x1

    #@2bc
    .line 1013
    add-int/lit8 v8, v8, -0x1

    #@2be
    .line 1014
    if-ltz v18, :cond_11

    #@2c0
    move-object/from16 v0, p0

    #@2c2
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@2c4
    move-object/from16 v27, v0

    #@2c6
    move-object/from16 v0, v27

    #@2c8
    move/from16 v1, v18

    #@2ca
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@2cd
    move-result-object v27

    #@2ce
    check-cast v27, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@2d0
    move-object/from16 v17, v27

    #@2d2
    .line 1000
    :cond_10
    :goto_c
    add-int/lit8 v23, v23, -0x1

    #@2d4
    goto/16 :goto_6

    #@2d6
    .line 1014
    :cond_11
    const/16 v17, 0x0

    #@2d8
    goto :goto_c

    #@2d9
    .line 1016
    :cond_12
    if-eqz v17, :cond_14

    #@2db
    move-object/from16 v0, v17

    #@2dd
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@2df
    move/from16 v27, v0

    #@2e1
    move/from16 v0, v23

    #@2e3
    move/from16 v1, v27

    #@2e5
    if-ne v0, v1, :cond_14

    #@2e7
    .line 1017
    move-object/from16 v0, v17

    #@2e9
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@2eb
    move/from16 v27, v0

    #@2ed
    add-float v13, v13, v27

    #@2ef
    .line 1018
    add-int/lit8 v18, v18, -0x1

    #@2f1
    .line 1019
    if-ltz v18, :cond_13

    #@2f3
    move-object/from16 v0, p0

    #@2f5
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@2f7
    move-object/from16 v27, v0

    #@2f9
    move-object/from16 v0, v27

    #@2fb
    move/from16 v1, v18

    #@2fd
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@300
    move-result-object v27

    #@301
    check-cast v27, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@303
    move-object/from16 v17, v27

    #@305
    :goto_d
    goto :goto_c

    #@306
    :cond_13
    const/16 v17, 0x0

    #@308
    goto :goto_d

    #@309
    .line 1021
    :cond_14
    add-int/lit8 v27, v18, 0x1

    #@30b
    move-object/from16 v0, p0

    #@30d
    move/from16 v1, v23

    #@30f
    move/from16 v2, v27

    #@311
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/view/ViewPager;->addNewItem(II)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@314
    move-result-object v17

    #@315
    .line 1022
    move-object/from16 v0, v17

    #@317
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@319
    move/from16 v27, v0

    #@31b
    add-float v13, v13, v27

    #@31d
    .line 1023
    add-int/lit8 v8, v8, 0x1

    #@31f
    .line 1024
    if-ltz v18, :cond_15

    #@321
    move-object/from16 v0, p0

    #@323
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@325
    move-object/from16 v27, v0

    #@327
    move-object/from16 v0, v27

    #@329
    move/from16 v1, v18

    #@32b
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@32e
    move-result-object v27

    #@32f
    check-cast v27, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@331
    move-object/from16 v17, v27

    #@333
    :goto_e
    goto :goto_c

    #@334
    :cond_15
    const/16 v17, 0x0

    #@336
    goto :goto_e

    #@337
    .line 1031
    .restart local v14    # "extraWidthRight":F
    :cond_16
    const/16 v17, 0x0

    #@339
    goto/16 :goto_7

    #@33b
    .line 1032
    :cond_17
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getPaddingRight()I

    #@33e
    move-result v27

    #@33f
    move/from16 v0, v27

    #@341
    int-to-float v0, v0

    #@342
    move/from16 v27, v0

    #@344
    int-to-float v0, v7

    #@345
    move/from16 v28, v0

    #@347
    div-float v27, v27, v28

    #@349
    const/high16 v28, 0x40000000    # 2.0f

    #@34b
    add-float v25, v27, v28

    #@34d
    goto/16 :goto_8

    #@34f
    .line 1039
    .restart local v25    # "rightWidthNeeded":F
    :cond_18
    move-object/from16 v0, v17

    #@351
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@353
    move/from16 v27, v0

    #@355
    move/from16 v0, v23

    #@357
    move/from16 v1, v27

    #@359
    if-ne v0, v1, :cond_19

    #@35b
    move-object/from16 v0, v17

    #@35d
    iget-boolean v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->scrolling:Z

    #@35f
    move/from16 v27, v0

    #@361
    if-nez v27, :cond_19

    #@363
    .line 1040
    move-object/from16 v0, p0

    #@365
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@367
    move-object/from16 v27, v0

    #@369
    move-object/from16 v0, v27

    #@36b
    move/from16 v1, v18

    #@36d
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->remove(I)Ljava/lang/Object;

    #@370
    .line 1041
    move-object/from16 v0, p0

    #@372
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@374
    move-object/from16 v27, v0

    #@376
    move-object/from16 v0, v17

    #@378
    iget-object v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->object:Ljava/lang/Object;

    #@37a
    move-object/from16 v28, v0

    #@37c
    move-object/from16 v0, v27

    #@37e
    move-object/from16 v1, p0

    #@380
    move/from16 v2, v23

    #@382
    move-object/from16 v3, v28

    #@384
    invoke-virtual {v0, v1, v2, v3}, Landroid/support/v4/view/PagerAdapter;->destroyItem(Landroid/view/ViewGroup;ILjava/lang/Object;)V

    #@387
    .line 1046
    move-object/from16 v0, p0

    #@389
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@38b
    move-object/from16 v27, v0

    #@38d
    invoke-virtual/range {v27 .. v27}, Ljava/util/ArrayList;->size()I

    #@390
    move-result v27

    #@391
    move/from16 v0, v18

    #@393
    move/from16 v1, v27

    #@395
    if-ge v0, v1, :cond_1a

    #@397
    move-object/from16 v0, p0

    #@399
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@39b
    move-object/from16 v27, v0

    #@39d
    move-object/from16 v0, v27

    #@39f
    move/from16 v1, v18

    #@3a1
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@3a4
    move-result-object v27

    #@3a5
    check-cast v27, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@3a7
    move-object/from16 v17, v27

    #@3a9
    .line 1034
    :cond_19
    :goto_f
    add-int/lit8 v23, v23, 0x1

    #@3ab
    goto/16 :goto_9

    #@3ad
    .line 1046
    :cond_1a
    const/16 v17, 0x0

    #@3af
    goto :goto_f

    #@3b0
    .line 1048
    :cond_1b
    if-eqz v17, :cond_1d

    #@3b2
    move-object/from16 v0, v17

    #@3b4
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@3b6
    move/from16 v27, v0

    #@3b8
    move/from16 v0, v23

    #@3ba
    move/from16 v1, v27

    #@3bc
    if-ne v0, v1, :cond_1d

    #@3be
    .line 1049
    move-object/from16 v0, v17

    #@3c0
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@3c2
    move/from16 v27, v0

    #@3c4
    add-float v14, v14, v27

    #@3c6
    .line 1050
    add-int/lit8 v18, v18, 0x1

    #@3c8
    .line 1051
    move-object/from16 v0, p0

    #@3ca
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@3cc
    move-object/from16 v27, v0

    #@3ce
    invoke-virtual/range {v27 .. v27}, Ljava/util/ArrayList;->size()I

    #@3d1
    move-result v27

    #@3d2
    move/from16 v0, v18

    #@3d4
    move/from16 v1, v27

    #@3d6
    if-ge v0, v1, :cond_1c

    #@3d8
    move-object/from16 v0, p0

    #@3da
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@3dc
    move-object/from16 v27, v0

    #@3de
    move-object/from16 v0, v27

    #@3e0
    move/from16 v1, v18

    #@3e2
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@3e5
    move-result-object v27

    #@3e6
    check-cast v27, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@3e8
    move-object/from16 v17, v27

    #@3ea
    :goto_10
    goto :goto_f

    #@3eb
    :cond_1c
    const/16 v17, 0x0

    #@3ed
    goto :goto_10

    #@3ee
    .line 1053
    :cond_1d
    move-object/from16 v0, p0

    #@3f0
    move/from16 v1, v23

    #@3f2
    move/from16 v2, v18

    #@3f4
    invoke-virtual {v0, v1, v2}, Landroid/support/v4/view/ViewPager;->addNewItem(II)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@3f7
    move-result-object v17

    #@3f8
    .line 1054
    add-int/lit8 v18, v18, 0x1

    #@3fa
    .line 1055
    move-object/from16 v0, v17

    #@3fc
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->widthFactor:F

    #@3fe
    move/from16 v27, v0

    #@400
    add-float v14, v14, v27

    #@402
    .line 1056
    move-object/from16 v0, p0

    #@404
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@406
    move-object/from16 v27, v0

    #@408
    invoke-virtual/range {v27 .. v27}, Ljava/util/ArrayList;->size()I

    #@40b
    move-result v27

    #@40c
    move/from16 v0, v18

    #@40e
    move/from16 v1, v27

    #@410
    if-ge v0, v1, :cond_1e

    #@412
    move-object/from16 v0, p0

    #@414
    iget-object v0, v0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@416
    move-object/from16 v27, v0

    #@418
    move-object/from16 v0, v27

    #@41a
    move/from16 v1, v18

    #@41c
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@41f
    move-result-object v27

    #@420
    check-cast v27, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@422
    move-object/from16 v17, v27

    #@424
    :goto_11
    goto :goto_f

    #@425
    :cond_1e
    const/16 v17, 0x0

    #@427
    goto :goto_11

    #@428
    .line 1071
    .end local v7    # "clientWidth":I
    .end local v13    # "extraWidthLeft":F
    .end local v14    # "extraWidthRight":F
    .end local v17    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    .end local v18    # "itemIndex":I
    .end local v19    # "leftWidthNeeded":F
    .end local v23    # "pos":I
    .end local v25    # "rightWidthNeeded":F
    :cond_1f
    const/16 v27, 0x0

    #@42a
    goto/16 :goto_a

    #@42c
    .line 1091
    .restart local v6    # "childCount":I
    .restart local v16    # "i":I
    :cond_20
    invoke-direct/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->sortChildDrawingOrder()V

    #@42f
    .line 1093
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->hasFocus()Z

    #@432
    move-result v27

    #@433
    if-eqz v27, :cond_1

    #@435
    .line 1094
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->findFocus()Landroid/view/View;

    #@438
    move-result-object v10

    #@439
    .line 1095
    .local v10, "currentFocused":Landroid/view/View;
    if-eqz v10, :cond_23

    #@43b
    move-object/from16 v0, p0

    #@43d
    invoke-virtual {v0, v10}, Landroid/support/v4/view/ViewPager;->infoForAnyChild(Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@440
    move-result-object v17

    #@441
    .line 1096
    .restart local v17    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :goto_12
    if-eqz v17, :cond_21

    #@443
    move-object/from16 v0, v17

    #@445
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@447
    move/from16 v27, v0

    #@449
    move-object/from16 v0, p0

    #@44b
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@44d
    move/from16 v28, v0

    #@44f
    move/from16 v0, v27

    #@451
    move/from16 v1, v28

    #@453
    if-eq v0, v1, :cond_1

    #@455
    .line 1097
    :cond_21
    const/16 v16, 0x0

    #@457
    :goto_13
    invoke-virtual/range {p0 .. p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@45a
    move-result v27

    #@45b
    move/from16 v0, v16

    #@45d
    move/from16 v1, v27

    #@45f
    if-ge v0, v1, :cond_1

    #@461
    .line 1098
    move-object/from16 v0, p0

    #@463
    move/from16 v1, v16

    #@465
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ViewPager;->getChildAt(I)Landroid/view/View;

    #@468
    move-result-object v5

    #@469
    .line 1099
    .restart local v5    # "child":Landroid/view/View;
    move-object/from16 v0, p0

    #@46b
    invoke-virtual {v0, v5}, Landroid/support/v4/view/ViewPager;->infoForChild(Landroid/view/View;)Landroid/support/v4/view/ViewPager$ItemInfo;

    #@46e
    move-result-object v17

    #@46f
    .line 1100
    if-eqz v17, :cond_22

    #@471
    move-object/from16 v0, v17

    #@473
    iget v0, v0, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@475
    move/from16 v27, v0

    #@477
    move-object/from16 v0, p0

    #@479
    iget v0, v0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@47b
    move/from16 v28, v0

    #@47d
    move/from16 v0, v27

    #@47f
    move/from16 v1, v28

    #@481
    if-ne v0, v1, :cond_22

    #@483
    .line 1101
    invoke-virtual {v5, v15}, Landroid/view/View;->requestFocus(I)Z

    #@486
    move-result v27

    #@487
    if-nez v27, :cond_1

    #@489
    .line 1097
    :cond_22
    add-int/lit8 v16, v16, 0x1

    #@48b
    goto :goto_13

    #@48c
    .line 1095
    .end local v5    # "child":Landroid/view/View;
    .end local v17    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_23
    const/16 v17, 0x0

    #@48e
    goto :goto_12
.end method

.method public removeView(Landroid/view/View;)V
    .locals 1
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 1323
    iget-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mInLayout:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1324
    invoke-virtual {p0, p1}, Landroid/support/v4/view/ViewPager;->removeViewInLayout(Landroid/view/View;)V

    #@7
    .line 1328
    :goto_0
    return-void

    #@8
    .line 1326
    :cond_0
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->removeView(Landroid/view/View;)V

    #@b
    goto :goto_0
.end method

.method public setAdapter(Landroid/support/v4/view/PagerAdapter;)V
    .locals 10
    .param p1, "adapter"    # Landroid/support/v4/view/PagerAdapter;

    #@0
    .prologue
    const/4 v9, 0x1

    #@1
    const/4 v8, 0x0

    #@2
    const/4 v7, 0x0

    #@3
    .line 413
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@5
    if-eqz v4, :cond_1

    #@7
    .line 414
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@9
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mObserver:Landroid/support/v4/view/ViewPager$PagerObserver;

    #@b
    invoke-virtual {v4, v5}, Landroid/support/v4/view/PagerAdapter;->unregisterDataSetObserver(Landroid/database/DataSetObserver;)V

    #@e
    .line 415
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@10
    invoke-virtual {v4, p0}, Landroid/support/v4/view/PagerAdapter;->startUpdate(Landroid/view/ViewGroup;)V

    #@13
    .line 416
    const/4 v0, 0x0

    #@14
    .local v0, "i":I
    :goto_0
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@16
    invoke-virtual {v4}, Ljava/util/ArrayList;->size()I

    #@19
    move-result v4

    #@1a
    if-ge v0, v4, :cond_0

    #@1c
    .line 417
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@1e
    invoke-virtual {v4, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@21
    move-result-object v1

    #@22
    check-cast v1, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@24
    .line 418
    .local v1, "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@26
    iget v5, v1, Landroid/support/v4/view/ViewPager$ItemInfo;->position:I

    #@28
    iget-object v6, v1, Landroid/support/v4/view/ViewPager$ItemInfo;->object:Ljava/lang/Object;

    #@2a
    invoke-virtual {v4, p0, v5, v6}, Landroid/support/v4/view/PagerAdapter;->destroyItem(Landroid/view/ViewGroup;ILjava/lang/Object;)V

    #@2d
    .line 416
    add-int/lit8 v0, v0, 0x1

    #@2f
    goto :goto_0

    #@30
    .line 420
    .end local v1    # "ii":Landroid/support/v4/view/ViewPager$ItemInfo;
    :cond_0
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@32
    invoke-virtual {v4, p0}, Landroid/support/v4/view/PagerAdapter;->finishUpdate(Landroid/view/ViewGroup;)V

    #@35
    .line 421
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@37
    invoke-virtual {v4}, Ljava/util/ArrayList;->clear()V

    #@3a
    .line 422
    invoke-direct {p0}, Landroid/support/v4/view/ViewPager;->removeNonDecorViews()V

    #@3d
    .line 423
    iput v7, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@3f
    .line 424
    invoke-virtual {p0, v7, v7}, Landroid/support/v4/view/ViewPager;->scrollTo(II)V

    #@42
    .line 427
    .end local v0    # "i":I
    :cond_1
    iget-object v2, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@44
    .line 428
    .local v2, "oldAdapter":Landroid/support/v4/view/PagerAdapter;
    iput-object p1, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@46
    .line 429
    iput v7, p0, Landroid/support/v4/view/ViewPager;->mExpectedAdapterCount:I

    #@48
    .line 431
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@4a
    if-eqz v4, :cond_3

    #@4c
    .line 432
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mObserver:Landroid/support/v4/view/ViewPager$PagerObserver;

    #@4e
    if-nez v4, :cond_2

    #@50
    .line 433
    new-instance v4, Landroid/support/v4/view/ViewPager$PagerObserver;

    #@52
    invoke-direct {v4, p0, v8}, Landroid/support/v4/view/ViewPager$PagerObserver;-><init>(Landroid/support/v4/view/ViewPager;Landroid/support/v4/view/ViewPager$1;)V

    #@55
    iput-object v4, p0, Landroid/support/v4/view/ViewPager;->mObserver:Landroid/support/v4/view/ViewPager$PagerObserver;

    #@57
    .line 435
    :cond_2
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@59
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mObserver:Landroid/support/v4/view/ViewPager$PagerObserver;

    #@5b
    invoke-virtual {v4, v5}, Landroid/support/v4/view/PagerAdapter;->registerDataSetObserver(Landroid/database/DataSetObserver;)V

    #@5e
    .line 436
    iput-boolean v7, p0, Landroid/support/v4/view/ViewPager;->mPopulatePending:Z

    #@60
    .line 437
    iget-boolean v3, p0, Landroid/support/v4/view/ViewPager;->mFirstLayout:Z

    #@62
    .line 438
    .local v3, "wasFirstLayout":Z
    iput-boolean v9, p0, Landroid/support/v4/view/ViewPager;->mFirstLayout:Z

    #@64
    .line 439
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@66
    invoke-virtual {v4}, Landroid/support/v4/view/PagerAdapter;->getCount()I

    #@69
    move-result v4

    #@6a
    iput v4, p0, Landroid/support/v4/view/ViewPager;->mExpectedAdapterCount:I

    #@6c
    .line 440
    iget v4, p0, Landroid/support/v4/view/ViewPager;->mRestoredCurItem:I

    #@6e
    if-ltz v4, :cond_5

    #@70
    .line 441
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@72
    iget-object v5, p0, Landroid/support/v4/view/ViewPager;->mRestoredAdapterState:Landroid/os/Parcelable;

    #@74
    iget-object v6, p0, Landroid/support/v4/view/ViewPager;->mRestoredClassLoader:Ljava/lang/ClassLoader;

    #@76
    invoke-virtual {v4, v5, v6}, Landroid/support/v4/view/PagerAdapter;->restoreState(Landroid/os/Parcelable;Ljava/lang/ClassLoader;)V

    #@79
    .line 442
    iget v4, p0, Landroid/support/v4/view/ViewPager;->mRestoredCurItem:I

    #@7b
    invoke-virtual {p0, v4, v7, v9}, Landroid/support/v4/view/ViewPager;->setCurrentItemInternal(IZZ)V

    #@7e
    .line 443
    const/4 v4, -0x1

    #@7f
    iput v4, p0, Landroid/support/v4/view/ViewPager;->mRestoredCurItem:I

    #@81
    .line 444
    iput-object v8, p0, Landroid/support/v4/view/ViewPager;->mRestoredAdapterState:Landroid/os/Parcelable;

    #@83
    .line 445
    iput-object v8, p0, Landroid/support/v4/view/ViewPager;->mRestoredClassLoader:Ljava/lang/ClassLoader;

    #@85
    .line 453
    .end local v3    # "wasFirstLayout":Z
    :cond_3
    :goto_1
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mAdapterChangeListener:Landroid/support/v4/view/ViewPager$OnAdapterChangeListener;

    #@87
    if-eqz v4, :cond_4

    #@89
    if-eq v2, p1, :cond_4

    #@8b
    .line 454
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mAdapterChangeListener:Landroid/support/v4/view/ViewPager$OnAdapterChangeListener;

    #@8d
    invoke-interface {v4, v2, p1}, Landroid/support/v4/view/ViewPager$OnAdapterChangeListener;->onAdapterChanged(Landroid/support/v4/view/PagerAdapter;Landroid/support/v4/view/PagerAdapter;)V

    #@90
    .line 456
    :cond_4
    return-void

    #@91
    .line 446
    .restart local v3    # "wasFirstLayout":Z
    :cond_5
    if-nez v3, :cond_6

    #@93
    .line 447
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->populate()V

    #@96
    goto :goto_1

    #@97
    .line 449
    :cond_6
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->requestLayout()V

    #@9a
    goto :goto_1
.end method

.method setChildrenDrawingOrderEnabledCompat(Z)V
    .locals 6
    .param p1, "enable"    # Z

    #@0
    .prologue
    .line 628
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/4 v2, 0x7

    #@3
    if-lt v1, v2, :cond_1

    #@5
    .line 629
    iget-object v1, p0, Landroid/support/v4/view/ViewPager;->mSetChildrenDrawingOrderEnabled:Ljava/lang/reflect/Method;

    #@7
    if-nez v1, :cond_0

    #@9
    .line 631
    :try_start_0
    const-class v1, Landroid/view/ViewGroup;

    #@b
    const-string v2, "setChildrenDrawingOrderEnabled"

    #@d
    const/4 v3, 0x1

    #@e
    new-array v3, v3, [Ljava/lang/Class;

    #@10
    const/4 v4, 0x0

    #@11
    sget-object v5, Ljava/lang/Boolean;->TYPE:Ljava/lang/Class;

    #@13
    aput-object v5, v3, v4

    #@15
    invoke-virtual {v1, v2, v3}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@18
    move-result-object v1

    #@19
    iput-object v1, p0, Landroid/support/v4/view/ViewPager;->mSetChildrenDrawingOrderEnabled:Ljava/lang/reflect/Method;
    :try_end_0
    .catch Ljava/lang/NoSuchMethodException; {:try_start_0 .. :try_end_0} :catch_0

    #@1b
    .line 638
    :cond_0
    :goto_0
    :try_start_1
    iget-object v1, p0, Landroid/support/v4/view/ViewPager;->mSetChildrenDrawingOrderEnabled:Ljava/lang/reflect/Method;

    #@1d
    const/4 v2, 0x1

    #@1e
    new-array v2, v2, [Ljava/lang/Object;

    #@20
    const/4 v3, 0x0

    #@21
    invoke-static {p1}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    #@24
    move-result-object v4

    #@25
    aput-object v4, v2, v3

    #@27
    invoke-virtual {v1, p0, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    #@2a
    .line 643
    :cond_1
    :goto_1
    return-void

    #@2b
    .line 633
    :catch_0
    move-exception v0

    #@2c
    .line 634
    .local v0, "e":Ljava/lang/NoSuchMethodException;
    const-string v1, "ViewPager"

    #@2e
    const-string v2, "Can\'t find setChildrenDrawingOrderEnabled"

    #@30
    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@33
    goto :goto_0

    #@34
    .line 639
    .end local v0    # "e":Ljava/lang/NoSuchMethodException;
    :catch_1
    move-exception v0

    #@35
    .line 640
    .local v0, "e":Ljava/lang/Exception;
    const-string v1, "ViewPager"

    #@37
    const-string v2, "Error changing children drawing order"

    #@39
    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@3c
    goto :goto_1
.end method

.method public setCurrentItem(I)V
    .locals 2
    .param p1, "item"    # I

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 494
    iput-boolean v1, p0, Landroid/support/v4/view/ViewPager;->mPopulatePending:Z

    #@3
    .line 495
    iget-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mFirstLayout:Z

    #@5
    if-nez v0, :cond_0

    #@7
    const/4 v0, 0x1

    #@8
    :goto_0
    invoke-virtual {p0, p1, v0, v1}, Landroid/support/v4/view/ViewPager;->setCurrentItemInternal(IZZ)V

    #@b
    .line 496
    return-void

    #@c
    :cond_0
    move v0, v1

    #@d
    .line 495
    goto :goto_0
.end method

.method public setCurrentItem(IZ)V
    .locals 1
    .param p1, "item"    # I
    .param p2, "smoothScroll"    # Z

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 505
    iput-boolean v0, p0, Landroid/support/v4/view/ViewPager;->mPopulatePending:Z

    #@3
    .line 506
    invoke-virtual {p0, p1, p2, v0}, Landroid/support/v4/view/ViewPager;->setCurrentItemInternal(IZZ)V

    #@6
    .line 507
    return-void
.end method

.method setCurrentItemInternal(IZZ)V
    .locals 1
    .param p1, "item"    # I
    .param p2, "smoothScroll"    # Z
    .param p3, "always"    # Z

    #@0
    .prologue
    .line 514
    const/4 v0, 0x0

    #@1
    invoke-virtual {p0, p1, p2, p3, v0}, Landroid/support/v4/view/ViewPager;->setCurrentItemInternal(IZZI)V

    #@4
    .line 515
    return-void
.end method

.method setCurrentItemInternal(IZZI)V
    .locals 5
    .param p1, "item"    # I
    .param p2, "smoothScroll"    # Z
    .param p3, "always"    # Z
    .param p4, "velocity"    # I

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    const/4 v4, 0x0

    #@2
    .line 518
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@4
    if-eqz v3, :cond_0

    #@6
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@8
    invoke-virtual {v3}, Landroid/support/v4/view/PagerAdapter;->getCount()I

    #@b
    move-result v3

    #@c
    if-gtz v3, :cond_1

    #@e
    .line 519
    :cond_0
    invoke-direct {p0, v4}, Landroid/support/v4/view/ViewPager;->setScrollingCacheEnabled(Z)V

    #@11
    .line 558
    :goto_0
    return-void

    #@12
    .line 522
    :cond_1
    if-nez p3, :cond_2

    #@14
    iget v3, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@16
    if-ne v3, p1, :cond_2

    #@18
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@1a
    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    #@1d
    move-result v3

    #@1e
    if-eqz v3, :cond_2

    #@20
    .line 523
    invoke-direct {p0, v4}, Landroid/support/v4/view/ViewPager;->setScrollingCacheEnabled(Z)V

    #@23
    goto :goto_0

    #@24
    .line 527
    :cond_2
    if-gez p1, :cond_5

    #@26
    .line 528
    const/4 p1, 0x0

    #@27
    .line 532
    :cond_3
    :goto_1
    iget v2, p0, Landroid/support/v4/view/ViewPager;->mOffscreenPageLimit:I

    #@29
    .line 533
    .local v2, "pageLimit":I
    iget v3, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@2b
    add-int/2addr v3, v2

    #@2c
    if-gt p1, v3, :cond_4

    #@2e
    iget v3, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@30
    sub-int/2addr v3, v2

    #@31
    if-ge p1, v3, :cond_6

    #@33
    .line 537
    :cond_4
    const/4 v1, 0x0

    #@34
    .local v1, "i":I
    :goto_2
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@36
    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    #@39
    move-result v3

    #@3a
    if-ge v1, v3, :cond_6

    #@3c
    .line 538
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mItems:Ljava/util/ArrayList;

    #@3e
    invoke-virtual {v3, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@41
    move-result-object v3

    #@42
    check-cast v3, Landroid/support/v4/view/ViewPager$ItemInfo;

    #@44
    iput-boolean v0, v3, Landroid/support/v4/view/ViewPager$ItemInfo;->scrolling:Z

    #@46
    .line 537
    add-int/lit8 v1, v1, 0x1

    #@48
    goto :goto_2

    #@49
    .line 529
    .end local v1    # "i":I
    .end local v2    # "pageLimit":I
    :cond_5
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@4b
    invoke-virtual {v3}, Landroid/support/v4/view/PagerAdapter;->getCount()I

    #@4e
    move-result v3

    #@4f
    if-lt p1, v3, :cond_3

    #@51
    .line 530
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@53
    invoke-virtual {v3}, Landroid/support/v4/view/PagerAdapter;->getCount()I

    #@56
    move-result v3

    #@57
    add-int/lit8 p1, v3, -0x1

    #@59
    goto :goto_1

    #@5a
    .line 541
    .restart local v2    # "pageLimit":I
    :cond_6
    iget v3, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@5c
    if-eq v3, p1, :cond_9

    #@5e
    .line 543
    .local v0, "dispatchSelected":Z
    :goto_3
    iget-boolean v3, p0, Landroid/support/v4/view/ViewPager;->mFirstLayout:Z

    #@60
    if-eqz v3, :cond_a

    #@62
    .line 546
    iput p1, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@64
    .line 547
    if-eqz v0, :cond_7

    #@66
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mOnPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@68
    if-eqz v3, :cond_7

    #@6a
    .line 548
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mOnPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@6c
    invoke-interface {v3, p1}, Landroid/support/v4/view/ViewPager$OnPageChangeListener;->onPageSelected(I)V

    #@6f
    .line 550
    :cond_7
    if-eqz v0, :cond_8

    #@71
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mInternalPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@73
    if-eqz v3, :cond_8

    #@75
    .line 551
    iget-object v3, p0, Landroid/support/v4/view/ViewPager;->mInternalPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@77
    invoke-interface {v3, p1}, Landroid/support/v4/view/ViewPager$OnPageChangeListener;->onPageSelected(I)V

    #@7a
    .line 553
    :cond_8
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->requestLayout()V

    #@7d
    goto :goto_0

    #@7e
    .end local v0    # "dispatchSelected":Z
    :cond_9
    move v0, v4

    #@7f
    .line 541
    goto :goto_3

    #@80
    .line 555
    .restart local v0    # "dispatchSelected":Z
    :cond_a
    invoke-virtual {p0, p1}, Landroid/support/v4/view/ViewPager;->populate(I)V

    #@83
    .line 556
    invoke-direct {p0, p1, p2, p4, v0}, Landroid/support/v4/view/ViewPager;->scrollToItem(IZIZ)V

    #@86
    goto :goto_0
.end method

.method setInternalPageChangeListener(Landroid/support/v4/view/ViewPager$OnPageChangeListener;)Landroid/support/v4/view/ViewPager$OnPageChangeListener;
    .locals 1
    .param p1, "listener"    # Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@0
    .prologue
    .line 659
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mInternalPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@2
    .line 660
    .local v0, "oldListener":Landroid/support/v4/view/ViewPager$OnPageChangeListener;
    iput-object p1, p0, Landroid/support/v4/view/ViewPager;->mInternalPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@4
    .line 661
    return-object v0
.end method

.method public setOffscreenPageLimit(I)V
    .locals 4
    .param p1, "limit"    # I

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 693
    if-ge p1, v3, :cond_0

    #@3
    .line 694
    const-string v0, "ViewPager"

    #@5
    new-instance v1, Ljava/lang/StringBuilder;

    #@7
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@a
    const-string v2, "Requested offscreen page limit "

    #@c
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@f
    move-result-object v1

    #@10
    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@13
    move-result-object v1

    #@14
    const-string v2, " too small; defaulting to "

    #@16
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@19
    move-result-object v1

    #@1a
    invoke-virtual {v1, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@1d
    move-result-object v1

    #@1e
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@21
    move-result-object v1

    #@22
    invoke-static {v0, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@25
    .line 696
    const/4 p1, 0x1

    #@26
    .line 698
    :cond_0
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mOffscreenPageLimit:I

    #@28
    if-eq p1, v0, :cond_1

    #@2a
    .line 699
    iput p1, p0, Landroid/support/v4/view/ViewPager;->mOffscreenPageLimit:I

    #@2c
    .line 700
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->populate()V

    #@2f
    .line 702
    :cond_1
    return-void
.end method

.method setOnAdapterChangeListener(Landroid/support/v4/view/ViewPager$OnAdapterChangeListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v4/view/ViewPager$OnAdapterChangeListener;

    #@0
    .prologue
    .line 479
    iput-object p1, p0, Landroid/support/v4/view/ViewPager;->mAdapterChangeListener:Landroid/support/v4/view/ViewPager$OnAdapterChangeListener;

    #@2
    .line 480
    return-void
.end method

.method public setOnPageChangeListener(Landroid/support/v4/view/ViewPager$OnPageChangeListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@0
    .prologue
    .line 597
    iput-object p1, p0, Landroid/support/v4/view/ViewPager;->mOnPageChangeListener:Landroid/support/v4/view/ViewPager$OnPageChangeListener;

    #@2
    .line 598
    return-void
.end method

.method public setPageMargin(I)V
    .locals 2
    .param p1, "marginPixels"    # I

    #@0
    .prologue
    .line 713
    iget v0, p0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@2
    .line 714
    .local v0, "oldMargin":I
    iput p1, p0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@4
    .line 716
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getWidth()I

    #@7
    move-result v1

    #@8
    .line 717
    .local v1, "width":I
    invoke-direct {p0, v1, v1, p1, v0}, Landroid/support/v4/view/ViewPager;->recomputeScrollPosition(IIII)V

    #@b
    .line 719
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->requestLayout()V

    #@e
    .line 720
    return-void
.end method

.method public setPageMarginDrawable(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 749
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getContext()Landroid/content/Context;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@b
    move-result-object v0

    #@c
    invoke-virtual {p0, v0}, Landroid/support/v4/view/ViewPager;->setPageMarginDrawable(Landroid/graphics/drawable/Drawable;)V

    #@f
    .line 750
    return-void
.end method

.method public setPageMarginDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 737
    iput-object p1, p0, Landroid/support/v4/view/ViewPager;->mMarginDrawable:Landroid/graphics/drawable/Drawable;

    #@2
    .line 738
    if-eqz p1, :cond_0

    #@4
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->refreshDrawableState()V

    #@7
    .line 739
    :cond_0
    if-nez p1, :cond_1

    #@9
    const/4 v0, 0x1

    #@a
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v4/view/ViewPager;->setWillNotDraw(Z)V

    #@d
    .line 740
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->invalidate()V

    #@10
    .line 741
    return-void

    #@11
    .line 739
    :cond_1
    const/4 v0, 0x0

    #@12
    goto :goto_0
.end method

.method public setPageTransformer(ZLandroid/support/v4/view/ViewPager$PageTransformer;)V
    .locals 6
    .param p1, "reverseDrawingOrder"    # Z
    .param p2, "transformer"    # Landroid/support/v4/view/ViewPager$PageTransformer;

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    const/4 v3, 0x0

    #@2
    .line 613
    sget v4, Landroid/os/Build$VERSION;->SDK_INT:I

    #@4
    const/16 v5, 0xb

    #@6
    if-lt v4, v5, :cond_1

    #@8
    .line 614
    if-eqz p2, :cond_2

    #@a
    move v0, v2

    #@b
    .line 615
    .local v0, "hasTransformer":Z
    :goto_0
    iget-object v4, p0, Landroid/support/v4/view/ViewPager;->mPageTransformer:Landroid/support/v4/view/ViewPager$PageTransformer;

    #@d
    if-eqz v4, :cond_3

    #@f
    move v4, v2

    #@10
    :goto_1
    if-eq v0, v4, :cond_4

    #@12
    move v1, v2

    #@13
    .line 616
    .local v1, "needsPopulate":Z
    :goto_2
    iput-object p2, p0, Landroid/support/v4/view/ViewPager;->mPageTransformer:Landroid/support/v4/view/ViewPager$PageTransformer;

    #@15
    .line 617
    invoke-virtual {p0, v0}, Landroid/support/v4/view/ViewPager;->setChildrenDrawingOrderEnabledCompat(Z)V

    #@18
    .line 618
    if-eqz v0, :cond_5

    #@1a
    .line 619
    if-eqz p1, :cond_0

    #@1c
    const/4 v2, 0x2

    #@1d
    :cond_0
    iput v2, p0, Landroid/support/v4/view/ViewPager;->mDrawingOrder:I

    #@1f
    .line 623
    :goto_3
    if-eqz v1, :cond_1

    #@21
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->populate()V

    #@24
    .line 625
    .end local v0    # "hasTransformer":Z
    .end local v1    # "needsPopulate":Z
    :cond_1
    return-void

    #@25
    :cond_2
    move v0, v3

    #@26
    .line 614
    goto :goto_0

    #@27
    .restart local v0    # "hasTransformer":Z
    :cond_3
    move v4, v3

    #@28
    .line 615
    goto :goto_1

    #@29
    :cond_4
    move v1, v3

    #@2a
    goto :goto_2

    #@2b
    .line 621
    .restart local v1    # "needsPopulate":Z
    :cond_5
    iput v3, p0, Landroid/support/v4/view/ViewPager;->mDrawingOrder:I

    #@2d
    goto :goto_3
.end method

.method smoothScrollTo(II)V
    .locals 1
    .param p1, "x"    # I
    .param p2, "y"    # I

    #@0
    .prologue
    .line 783
    const/4 v0, 0x0

    #@1
    invoke-virtual {p0, p1, p2, v0}, Landroid/support/v4/view/ViewPager;->smoothScrollTo(III)V

    #@4
    .line 784
    return-void
.end method

.method smoothScrollTo(III)V
    .locals 15
    .param p1, "x"    # I
    .param p2, "y"    # I
    .param p3, "velocity"    # I

    #@0
    .prologue
    .line 794
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getChildCount()I

    #@3
    move-result v1

    #@4
    if-nez v1, :cond_0

    #@6
    .line 796
    const/4 v1, 0x0

    #@7
    invoke-direct {p0, v1}, Landroid/support/v4/view/ViewPager;->setScrollingCacheEnabled(Z)V

    #@a
    .line 832
    :goto_0
    return-void

    #@b
    .line 799
    :cond_0
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getScrollX()I

    #@e
    move-result v2

    #@f
    .line 800
    .local v2, "sx":I
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->getScrollY()I

    #@12
    move-result v3

    #@13
    .line 801
    .local v3, "sy":I
    sub-int v4, p1, v2

    #@15
    .line 802
    .local v4, "dx":I
    sub-int v5, p2, v3

    #@17
    .line 803
    .local v5, "dy":I
    if-nez v4, :cond_1

    #@19
    if-nez v5, :cond_1

    #@1b
    .line 804
    const/4 v1, 0x0

    #@1c
    invoke-direct {p0, v1}, Landroid/support/v4/view/ViewPager;->completeScroll(Z)V

    #@1f
    .line 805
    invoke-virtual {p0}, Landroid/support/v4/view/ViewPager;->populate()V

    #@22
    .line 806
    const/4 v1, 0x0

    #@23
    invoke-direct {p0, v1}, Landroid/support/v4/view/ViewPager;->setScrollState(I)V

    #@26
    goto :goto_0

    #@27
    .line 810
    :cond_1
    const/4 v1, 0x1

    #@28
    invoke-direct {p0, v1}, Landroid/support/v4/view/ViewPager;->setScrollingCacheEnabled(Z)V

    #@2b
    .line 811
    const/4 v1, 0x2

    #@2c
    invoke-direct {p0, v1}, Landroid/support/v4/view/ViewPager;->setScrollState(I)V

    #@2f
    .line 813
    invoke-direct {p0}, Landroid/support/v4/view/ViewPager;->getClientWidth()I

    #@32
    move-result v12

    #@33
    .line 814
    .local v12, "width":I
    div-int/lit8 v9, v12, 0x2

    #@35
    .line 815
    .local v9, "halfWidth":I
    const/high16 v1, 0x3f800000    # 1.0f

    #@37
    const/high16 v13, 0x3f800000    # 1.0f

    #@39
    invoke-static {v4}, Ljava/lang/Math;->abs(I)I

    #@3c
    move-result v14

    #@3d
    int-to-float v14, v14

    #@3e
    mul-float/2addr v13, v14

    #@3f
    int-to-float v14, v12

    #@40
    div-float/2addr v13, v14

    #@41
    invoke-static {v1, v13}, Ljava/lang/Math;->min(FF)F

    #@44
    move-result v8

    #@45
    .line 816
    .local v8, "distanceRatio":F
    int-to-float v1, v9

    #@46
    int-to-float v13, v9

    #@47
    invoke-virtual {p0, v8}, Landroid/support/v4/view/ViewPager;->distanceInfluenceForSnapDuration(F)F

    #@4a
    move-result v14

    #@4b
    mul-float/2addr v13, v14

    #@4c
    add-float v7, v1, v13

    #@4e
    .line 819
    .local v7, "distance":F
    const/4 v6, 0x0

    #@4f
    .line 820
    .local v6, "duration":I
    invoke-static/range {p3 .. p3}, Ljava/lang/Math;->abs(I)I

    #@52
    move-result p3

    #@53
    .line 821
    if-lez p3, :cond_2

    #@55
    .line 822
    const/high16 v1, 0x447a0000    # 1000.0f

    #@57
    move/from16 v0, p3

    #@59
    int-to-float v13, v0

    #@5a
    div-float v13, v7, v13

    #@5c
    invoke-static {v13}, Ljava/lang/Math;->abs(F)F

    #@5f
    move-result v13

    #@60
    mul-float/2addr v1, v13

    #@61
    invoke-static {v1}, Ljava/lang/Math;->round(F)I

    #@64
    move-result v1

    #@65
    mul-int/lit8 v6, v1, 0x4

    #@67
    .line 828
    :goto_1
    const/16 v1, 0x258

    #@69
    invoke-static {v6, v1}, Ljava/lang/Math;->min(II)I

    #@6c
    move-result v6

    #@6d
    .line 830
    iget-object v1, p0, Landroid/support/v4/view/ViewPager;->mScroller:Landroid/widget/Scroller;

    #@6f
    invoke-virtual/range {v1 .. v6}, Landroid/widget/Scroller;->startScroll(IIIII)V

    #@72
    .line 831
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->postInvalidateOnAnimation(Landroid/view/View;)V

    #@75
    goto :goto_0

    #@76
    .line 824
    :cond_2
    int-to-float v1, v12

    #@77
    iget-object v13, p0, Landroid/support/v4/view/ViewPager;->mAdapter:Landroid/support/v4/view/PagerAdapter;

    #@79
    iget v14, p0, Landroid/support/v4/view/ViewPager;->mCurItem:I

    #@7b
    invoke-virtual {v13, v14}, Landroid/support/v4/view/PagerAdapter;->getPageWidth(I)F

    #@7e
    move-result v13

    #@7f
    mul-float v11, v1, v13

    #@81
    .line 825
    .local v11, "pageWidth":F
    invoke-static {v4}, Ljava/lang/Math;->abs(I)I

    #@84
    move-result v1

    #@85
    int-to-float v1, v1

    #@86
    iget v13, p0, Landroid/support/v4/view/ViewPager;->mPageMargin:I

    #@88
    int-to-float v13, v13

    #@89
    add-float/2addr v13, v11

    #@8a
    div-float v10, v1, v13

    #@8c
    .line 826
    .local v10, "pageDelta":F
    const/high16 v1, 0x3f800000    # 1.0f

    #@8e
    add-float/2addr v1, v10

    #@8f
    const/high16 v13, 0x42c80000    # 100.0f

    #@91
    mul-float/2addr v1, v13

    #@92
    float-to-int v6, v1

    #@93
    goto :goto_1
.end method

.method protected verifyDrawable(Landroid/graphics/drawable/Drawable;)Z
    .locals 1
    .param p1, "who"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 754
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->verifyDrawable(Landroid/graphics/drawable/Drawable;)Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    iget-object v0, p0, Landroid/support/v4/view/ViewPager;->mMarginDrawable:Landroid/graphics/drawable/Drawable;

    #@8
    if-ne p1, v0, :cond_1

    #@a
    :cond_0
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_1
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method
