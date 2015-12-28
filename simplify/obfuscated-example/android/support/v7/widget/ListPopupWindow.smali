.class public Landroid/support/v7/widget/ListPopupWindow;
.super Ljava/lang/Object;
.source "ListPopupWindow.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/widget/ListPopupWindow$PopupScrollListener;,
        Landroid/support/v7/widget/ListPopupWindow$PopupTouchInterceptor;,
        Landroid/support/v7/widget/ListPopupWindow$ResizePopupRunnable;,
        Landroid/support/v7/widget/ListPopupWindow$ListSelectorHider;,
        Landroid/support/v7/widget/ListPopupWindow$PopupDataSetObserver;,
        Landroid/support/v7/widget/ListPopupWindow$DropDownListView;,
        Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;
    }
.end annotation


# static fields
.field private static final DEBUG:Z = false

.field private static final EXPAND_LIST_TIMEOUT:I = 0xfa

.field public static final INPUT_METHOD_FROM_FOCUSABLE:I = 0x0

.field public static final INPUT_METHOD_NEEDED:I = 0x1

.field public static final INPUT_METHOD_NOT_NEEDED:I = 0x2

.field public static final MATCH_PARENT:I = -0x1

.field public static final POSITION_PROMPT_ABOVE:I = 0x0

.field public static final POSITION_PROMPT_BELOW:I = 0x1

.field private static final TAG:Ljava/lang/String; = "ListPopupWindow"

.field public static final WRAP_CONTENT:I = -0x2

.field private static sClipToWindowEnabledMethod:Ljava/lang/reflect/Method;


# instance fields
.field private mAdapter:Landroid/widget/ListAdapter;

.field private mContext:Landroid/content/Context;

.field private mDropDownAlwaysVisible:Z

.field private mDropDownAnchorView:Landroid/view/View;

.field private mDropDownGravity:I

.field private mDropDownHeight:I

.field private mDropDownHorizontalOffset:I

.field private mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

.field private mDropDownListHighlight:Landroid/graphics/drawable/Drawable;

.field private mDropDownVerticalOffset:I

.field private mDropDownVerticalOffsetSet:Z

.field private mDropDownWidth:I

.field private mForceIgnoreOutsideTouch:Z

.field private mHandler:Landroid/os/Handler;

.field private final mHideSelector:Landroid/support/v7/widget/ListPopupWindow$ListSelectorHider;

.field private mItemClickListener:Landroid/widget/AdapterView$OnItemClickListener;

.field private mItemSelectedListener:Landroid/widget/AdapterView$OnItemSelectedListener;

.field private mLayoutDirection:I

.field mListItemExpandMaximum:I

.field private mModal:Z

.field private mObserver:Landroid/database/DataSetObserver;

.field private mPopup:Landroid/widget/PopupWindow;

.field private mPromptPosition:I

.field private mPromptView:Landroid/view/View;

.field private final mResizePopupRunnable:Landroid/support/v7/widget/ListPopupWindow$ResizePopupRunnable;

.field private final mScrollListener:Landroid/support/v7/widget/ListPopupWindow$PopupScrollListener;

.field private mShowDropDownRunnable:Ljava/lang/Runnable;

.field private mTempRect:Landroid/graphics/Rect;

.field private final mTouchInterceptor:Landroid/support/v7/widget/ListPopupWindow$PopupTouchInterceptor;


# direct methods
.method static constructor <clinit>()V
    .locals 6

    #@0
    .prologue
    .line 80
    :try_start_0
    const-class v1, Landroid/widget/PopupWindow;

    #@2
    const-string v2, "setClipToScreenEnabled"

    #@4
    const/4 v3, 0x1

    #@5
    new-array v3, v3, [Ljava/lang/Class;

    #@7
    const/4 v4, 0x0

    #@8
    sget-object v5, Ljava/lang/Boolean;->TYPE:Ljava/lang/Class;

    #@a
    aput-object v5, v3, v4

    #@c
    invoke-virtual {v1, v2, v3}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@f
    move-result-object v1

    #@10
    sput-object v1, Landroid/support/v7/widget/ListPopupWindow;->sClipToWindowEnabledMethod:Ljava/lang/reflect/Method;
    :try_end_0
    .catch Ljava/lang/NoSuchMethodException; {:try_start_0 .. :try_end_0} :catch_0

    #@12
    .line 85
    .local v0, "e":Ljava/lang/NoSuchMethodException;
    :goto_0
    return-void

    #@13
    .line 82
    .end local v0    # "e":Ljava/lang/NoSuchMethodException;
    :catch_0
    move-exception v0

    #@14
    .line 83
    .restart local v0    # "e":Ljava/lang/NoSuchMethodException;
    const-string v1, "ListPopupWindow"

    #@16
    const-string v2, "Could not find method setClipToScreenEnabled() on PopupWindow. Oh well."

    #@18
    invoke-static {v1, v2}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    #@1b
    goto :goto_0
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 192
    const/4 v0, 0x0

    #@1
    sget v1, Landroid/support/v7/appcompat/R$attr;->listPopupWindowStyle:I

    #@3
    invoke-direct {p0, p1, v0, v1}, Landroid/support/v7/widget/ListPopupWindow;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@6
    .line 193
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 203
    sget v0, Landroid/support/v7/appcompat/R$attr;->listPopupWindowStyle:I

    #@2
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/widget/ListPopupWindow;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@5
    .line 204
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyleAttr"    # I

    #@0
    .prologue
    .line 215
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, p2, p3, v0}, Landroid/support/v7/widget/ListPopupWindow;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;II)V

    #@4
    .line 216
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;II)V
    .locals 6
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyleAttr"    # I
    .param p4, "defStyleRes"    # I

    #@0
    .prologue
    const/4 v5, 0x1

    #@1
    const/4 v2, -0x2

    #@2
    const/4 v4, 0x0

    #@3
    const/4 v3, 0x0

    #@4
    .line 227
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@7
    .line 92
    iput v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHeight:I

    #@9
    .line 93
    iput v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@b
    .line 98
    iput v3, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownGravity:I

    #@d
    .line 100
    iput-boolean v3, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownAlwaysVisible:Z

    #@f
    .line 101
    iput-boolean v3, p0, Landroid/support/v7/widget/ListPopupWindow;->mForceIgnoreOutsideTouch:Z

    #@11
    .line 102
    const v2, 0x7fffffff

    #@14
    iput v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mListItemExpandMaximum:I

    #@16
    .line 105
    iput v3, p0, Landroid/support/v7/widget/ListPopupWindow;->mPromptPosition:I

    #@18
    .line 116
    new-instance v2, Landroid/support/v7/widget/ListPopupWindow$ResizePopupRunnable;

    #@1a
    invoke-direct {v2, p0, v4}, Landroid/support/v7/widget/ListPopupWindow$ResizePopupRunnable;-><init>(Landroid/support/v7/widget/ListPopupWindow;Landroid/support/v7/widget/ListPopupWindow$1;)V

    #@1d
    iput-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mResizePopupRunnable:Landroid/support/v7/widget/ListPopupWindow$ResizePopupRunnable;

    #@1f
    .line 117
    new-instance v2, Landroid/support/v7/widget/ListPopupWindow$PopupTouchInterceptor;

    #@21
    invoke-direct {v2, p0, v4}, Landroid/support/v7/widget/ListPopupWindow$PopupTouchInterceptor;-><init>(Landroid/support/v7/widget/ListPopupWindow;Landroid/support/v7/widget/ListPopupWindow$1;)V

    #@24
    iput-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mTouchInterceptor:Landroid/support/v7/widget/ListPopupWindow$PopupTouchInterceptor;

    #@26
    .line 118
    new-instance v2, Landroid/support/v7/widget/ListPopupWindow$PopupScrollListener;

    #@28
    invoke-direct {v2, p0, v4}, Landroid/support/v7/widget/ListPopupWindow$PopupScrollListener;-><init>(Landroid/support/v7/widget/ListPopupWindow;Landroid/support/v7/widget/ListPopupWindow$1;)V

    #@2b
    iput-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mScrollListener:Landroid/support/v7/widget/ListPopupWindow$PopupScrollListener;

    #@2d
    .line 119
    new-instance v2, Landroid/support/v7/widget/ListPopupWindow$ListSelectorHider;

    #@2f
    invoke-direct {v2, p0, v4}, Landroid/support/v7/widget/ListPopupWindow$ListSelectorHider;-><init>(Landroid/support/v7/widget/ListPopupWindow;Landroid/support/v7/widget/ListPopupWindow$1;)V

    #@32
    iput-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mHideSelector:Landroid/support/v7/widget/ListPopupWindow$ListSelectorHider;

    #@34
    .line 122
    new-instance v2, Landroid/os/Handler;

    #@36
    invoke-direct {v2}, Landroid/os/Handler;-><init>()V

    #@39
    iput-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mHandler:Landroid/os/Handler;

    #@3b
    .line 124
    new-instance v2, Landroid/graphics/Rect;

    #@3d
    invoke-direct {v2}, Landroid/graphics/Rect;-><init>()V

    #@40
    iput-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mTempRect:Landroid/graphics/Rect;

    #@42
    .line 228
    iput-object p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mContext:Landroid/content/Context;

    #@44
    .line 230
    sget-object v2, Landroid/support/v7/appcompat/R$styleable;->ListPopupWindow:[I

    #@46
    invoke-virtual {p1, p2, v2, p3, p4}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;

    #@49
    move-result-object v0

    #@4a
    .line 232
    .local v0, "a":Landroid/content/res/TypedArray;
    sget v2, Landroid/support/v7/appcompat/R$styleable;->ListPopupWindow_android_dropDownHorizontalOffset:I

    #@4c
    invoke-virtual {v0, v2, v3}, Landroid/content/res/TypedArray;->getDimensionPixelOffset(II)I

    #@4f
    move-result v2

    #@50
    iput v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHorizontalOffset:I

    #@52
    .line 234
    sget v2, Landroid/support/v7/appcompat/R$styleable;->ListPopupWindow_android_dropDownVerticalOffset:I

    #@54
    invoke-virtual {v0, v2, v3}, Landroid/content/res/TypedArray;->getDimensionPixelOffset(II)I

    #@57
    move-result v2

    #@58
    iput v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownVerticalOffset:I

    #@5a
    .line 236
    iget v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownVerticalOffset:I

    #@5c
    if-eqz v2, :cond_0

    #@5e
    .line 237
    iput-boolean v5, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownVerticalOffsetSet:Z

    #@60
    .line 239
    :cond_0
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@63
    .line 241
    new-instance v2, Landroid/support/v7/internal/widget/AppCompatPopupWindow;

    #@65
    invoke-direct {v2, p1, p2, p3}, Landroid/support/v7/internal/widget/AppCompatPopupWindow;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@68
    iput-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@6a
    .line 242
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@6c
    invoke-virtual {v2, v5}, Landroid/widget/PopupWindow;->setInputMethodMode(I)V

    #@6f
    .line 245
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mContext:Landroid/content/Context;

    #@71
    invoke-virtual {v2}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@74
    move-result-object v2

    #@75
    invoke-virtual {v2}, Landroid/content/res/Resources;->getConfiguration()Landroid/content/res/Configuration;

    #@78
    move-result-object v2

    #@79
    iget-object v1, v2, Landroid/content/res/Configuration;->locale:Ljava/util/Locale;

    #@7b
    .line 246
    .local v1, "locale":Ljava/util/Locale;
    invoke-static {v1}, Landroid/support/v4/text/TextUtilsCompat;->getLayoutDirectionFromLocale(Ljava/util/Locale;)I

    #@7e
    move-result v2

    #@7f
    iput v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mLayoutDirection:I

    #@81
    .line 247
    return-void
.end method

.method static synthetic access$1100(Landroid/support/v7/widget/ListPopupWindow;)Landroid/widget/PopupWindow;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ListPopupWindow;

    #@0
    .prologue
    .line 65
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2
    return-object v0
.end method

.method static synthetic access$1200(Landroid/support/v7/widget/ListPopupWindow;)Landroid/support/v7/widget/ListPopupWindow$ResizePopupRunnable;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ListPopupWindow;

    #@0
    .prologue
    .line 65
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mResizePopupRunnable:Landroid/support/v7/widget/ListPopupWindow$ResizePopupRunnable;

    #@2
    return-object v0
.end method

.method static synthetic access$1300(Landroid/support/v7/widget/ListPopupWindow;)Landroid/os/Handler;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ListPopupWindow;

    #@0
    .prologue
    .line 65
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mHandler:Landroid/os/Handler;

    #@2
    return-object v0
.end method

.method static synthetic access$600(Landroid/support/v7/widget/ListPopupWindow;)Landroid/support/v7/widget/ListPopupWindow$DropDownListView;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ListPopupWindow;

    #@0
    .prologue
    .line 65
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@2
    return-object v0
.end method

.method private buildDropDown()I
    .locals 21

    #@0
    .prologue
    .line 1049
    const/16 v17, 0x0

    #@2
    .line 1051
    .local v17, "otherHeights":I
    move-object/from16 v0, p0

    #@4
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@6
    if-nez v1, :cond_7

    #@8
    .line 1052
    move-object/from16 v0, p0

    #@a
    iget-object v8, v0, Landroid/support/v7/widget/ListPopupWindow;->mContext:Landroid/content/Context;

    #@c
    .line 1060
    .local v8, "context":Landroid/content/Context;
    new-instance v1, Landroid/support/v7/widget/ListPopupWindow$2;

    #@e
    move-object/from16 v0, p0

    #@10
    invoke-direct {v1, v0}, Landroid/support/v7/widget/ListPopupWindow$2;-><init>(Landroid/support/v7/widget/ListPopupWindow;)V

    #@13
    move-object/from16 v0, p0

    #@15
    iput-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mShowDropDownRunnable:Ljava/lang/Runnable;

    #@17
    .line 1070
    new-instance v3, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@19
    move-object/from16 v0, p0

    #@1b
    iget-boolean v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mModal:Z

    #@1d
    if-nez v1, :cond_6

    #@1f
    const/4 v1, 0x1

    #@20
    :goto_0
    invoke-direct {v3, v8, v1}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;-><init>(Landroid/content/Context;Z)V

    #@23
    move-object/from16 v0, p0

    #@25
    iput-object v3, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@27
    .line 1071
    move-object/from16 v0, p0

    #@29
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownListHighlight:Landroid/graphics/drawable/Drawable;

    #@2b
    if-eqz v1, :cond_0

    #@2d
    .line 1072
    move-object/from16 v0, p0

    #@2f
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@31
    move-object/from16 v0, p0

    #@33
    iget-object v3, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownListHighlight:Landroid/graphics/drawable/Drawable;

    #@35
    invoke-virtual {v1, v3}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setSelector(Landroid/graphics/drawable/Drawable;)V

    #@38
    .line 1074
    :cond_0
    move-object/from16 v0, p0

    #@3a
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@3c
    move-object/from16 v0, p0

    #@3e
    iget-object v3, v0, Landroid/support/v7/widget/ListPopupWindow;->mAdapter:Landroid/widget/ListAdapter;

    #@40
    invoke-virtual {v1, v3}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setAdapter(Landroid/widget/ListAdapter;)V

    #@43
    .line 1075
    move-object/from16 v0, p0

    #@45
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@47
    move-object/from16 v0, p0

    #@49
    iget-object v3, v0, Landroid/support/v7/widget/ListPopupWindow;->mItemClickListener:Landroid/widget/AdapterView$OnItemClickListener;

    #@4b
    invoke-virtual {v1, v3}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setOnItemClickListener(Landroid/widget/AdapterView$OnItemClickListener;)V

    #@4e
    .line 1076
    move-object/from16 v0, p0

    #@50
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@52
    const/4 v3, 0x1

    #@53
    invoke-virtual {v1, v3}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setFocusable(Z)V

    #@56
    .line 1077
    move-object/from16 v0, p0

    #@58
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@5a
    const/4 v3, 0x1

    #@5b
    invoke-virtual {v1, v3}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setFocusableInTouchMode(Z)V

    #@5e
    .line 1078
    move-object/from16 v0, p0

    #@60
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@62
    new-instance v3, Landroid/support/v7/widget/ListPopupWindow$3;

    #@64
    move-object/from16 v0, p0

    #@66
    invoke-direct {v3, v0}, Landroid/support/v7/widget/ListPopupWindow$3;-><init>(Landroid/support/v7/widget/ListPopupWindow;)V

    #@69
    invoke-virtual {v1, v3}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setOnItemSelectedListener(Landroid/widget/AdapterView$OnItemSelectedListener;)V

    #@6c
    .line 1094
    move-object/from16 v0, p0

    #@6e
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@70
    move-object/from16 v0, p0

    #@72
    iget-object v3, v0, Landroid/support/v7/widget/ListPopupWindow;->mScrollListener:Landroid/support/v7/widget/ListPopupWindow$PopupScrollListener;

    #@74
    invoke-virtual {v1, v3}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setOnScrollListener(Landroid/widget/AbsListView$OnScrollListener;)V

    #@77
    .line 1096
    move-object/from16 v0, p0

    #@79
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mItemSelectedListener:Landroid/widget/AdapterView$OnItemSelectedListener;

    #@7b
    if-eqz v1, :cond_1

    #@7d
    .line 1097
    move-object/from16 v0, p0

    #@7f
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@81
    move-object/from16 v0, p0

    #@83
    iget-object v3, v0, Landroid/support/v7/widget/ListPopupWindow;->mItemSelectedListener:Landroid/widget/AdapterView$OnItemSelectedListener;

    #@85
    invoke-virtual {v1, v3}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setOnItemSelectedListener(Landroid/widget/AdapterView$OnItemSelectedListener;)V

    #@88
    .line 1100
    :cond_1
    move-object/from16 v0, p0

    #@8a
    iget-object v9, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@8c
    .line 1102
    .local v9, "dropDownView":Landroid/view/ViewGroup;
    move-object/from16 v0, p0

    #@8e
    iget-object v13, v0, Landroid/support/v7/widget/ListPopupWindow;->mPromptView:Landroid/view/View;

    #@90
    .line 1103
    .local v13, "hintView":Landroid/view/View;
    if-eqz v13, :cond_2

    #@92
    .line 1106
    new-instance v11, Landroid/widget/LinearLayout;

    #@94
    invoke-direct {v11, v8}, Landroid/widget/LinearLayout;-><init>(Landroid/content/Context;)V

    #@97
    .line 1107
    .local v11, "hintContainer":Landroid/widget/LinearLayout;
    const/4 v1, 0x1

    #@98
    invoke-virtual {v11, v1}, Landroid/widget/LinearLayout;->setOrientation(I)V

    #@9b
    .line 1109
    new-instance v12, Landroid/widget/LinearLayout$LayoutParams;

    #@9d
    const/4 v1, -0x1

    #@9e
    const/4 v3, 0x0

    #@9f
    const/high16 v4, 0x3f800000    # 1.0f

    #@a1
    invoke-direct {v12, v1, v3, v4}, Landroid/widget/LinearLayout$LayoutParams;-><init>(IIF)V

    #@a4
    .line 1113
    .local v12, "hintParams":Landroid/widget/LinearLayout$LayoutParams;
    move-object/from16 v0, p0

    #@a6
    iget v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mPromptPosition:I

    #@a8
    packed-switch v1, :pswitch_data_0

    #@ab
    .line 1125
    const-string v1, "ListPopupWindow"

    #@ad
    new-instance v3, Ljava/lang/StringBuilder;

    #@af
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@b2
    const-string v4, "Invalid hint position "

    #@b4
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@b7
    move-result-object v3

    #@b8
    move-object/from16 v0, p0

    #@ba
    iget v4, v0, Landroid/support/v7/widget/ListPopupWindow;->mPromptPosition:I

    #@bc
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@bf
    move-result-object v3

    #@c0
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@c3
    move-result-object v3

    #@c4
    invoke-static {v1, v3}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@c7
    .line 1131
    :goto_1
    move-object/from16 v0, p0

    #@c9
    iget v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@cb
    const/high16 v3, -0x80000000

    #@cd
    invoke-static {v1, v3}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@d0
    move-result v20

    #@d1
    .line 1132
    .local v20, "widthSpec":I
    const/4 v10, 0x0

    #@d2
    .line 1133
    .local v10, "heightSpec":I
    move/from16 v0, v20

    #@d4
    invoke-virtual {v13, v0, v10}, Landroid/view/View;->measure(II)V

    #@d7
    .line 1135
    invoke-virtual {v13}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@da
    move-result-object v12

    #@db
    .end local v12    # "hintParams":Landroid/widget/LinearLayout$LayoutParams;
    check-cast v12, Landroid/widget/LinearLayout$LayoutParams;

    #@dd
    .line 1136
    .restart local v12    # "hintParams":Landroid/widget/LinearLayout$LayoutParams;
    invoke-virtual {v13}, Landroid/view/View;->getMeasuredHeight()I

    #@e0
    move-result v1

    #@e1
    iget v3, v12, Landroid/widget/LinearLayout$LayoutParams;->topMargin:I

    #@e3
    add-int/2addr v1, v3

    #@e4
    iget v3, v12, Landroid/widget/LinearLayout$LayoutParams;->bottomMargin:I

    #@e6
    add-int v17, v1, v3

    #@e8
    .line 1139
    move-object v9, v11

    #@e9
    .line 1142
    .end local v10    # "heightSpec":I
    .end local v11    # "hintContainer":Landroid/widget/LinearLayout;
    .end local v12    # "hintParams":Landroid/widget/LinearLayout$LayoutParams;
    .end local v20    # "widthSpec":I
    :cond_2
    move-object/from16 v0, p0

    #@eb
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@ed
    invoke-virtual {v1, v9}, Landroid/widget/PopupWindow;->setContentView(Landroid/view/View;)V

    #@f0
    .line 1156
    .end local v8    # "context":Landroid/content/Context;
    .end local v13    # "hintView":Landroid/view/View;
    :cond_3
    :goto_2
    const/16 v18, 0x0

    #@f2
    .line 1157
    .local v18, "padding":I
    move-object/from16 v0, p0

    #@f4
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@f6
    invoke-virtual {v1}, Landroid/widget/PopupWindow;->getBackground()Landroid/graphics/drawable/Drawable;

    #@f9
    move-result-object v7

    #@fa
    .line 1158
    .local v7, "background":Landroid/graphics/drawable/Drawable;
    if-eqz v7, :cond_8

    #@fc
    .line 1159
    move-object/from16 v0, p0

    #@fe
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mTempRect:Landroid/graphics/Rect;

    #@100
    invoke-virtual {v7, v1}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@103
    .line 1160
    move-object/from16 v0, p0

    #@105
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mTempRect:Landroid/graphics/Rect;

    #@107
    iget v1, v1, Landroid/graphics/Rect;->top:I

    #@109
    move-object/from16 v0, p0

    #@10b
    iget-object v3, v0, Landroid/support/v7/widget/ListPopupWindow;->mTempRect:Landroid/graphics/Rect;

    #@10d
    iget v3, v3, Landroid/graphics/Rect;->bottom:I

    #@10f
    add-int v18, v1, v3

    #@111
    .line 1164
    move-object/from16 v0, p0

    #@113
    iget-boolean v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownVerticalOffsetSet:Z

    #@115
    if-nez v1, :cond_4

    #@117
    .line 1165
    move-object/from16 v0, p0

    #@119
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mTempRect:Landroid/graphics/Rect;

    #@11b
    iget v1, v1, Landroid/graphics/Rect;->top:I

    #@11d
    neg-int v1, v1

    #@11e
    move-object/from16 v0, p0

    #@120
    iput v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownVerticalOffset:I

    #@122
    .line 1172
    :cond_4
    :goto_3
    move-object/from16 v0, p0

    #@124
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@126
    invoke-virtual {v1}, Landroid/widget/PopupWindow;->getInputMethodMode()I

    #@129
    move-result v1

    #@12a
    const/4 v3, 0x2

    #@12b
    if-ne v1, v3, :cond_9

    #@12d
    const/4 v14, 0x1

    #@12e
    .line 1174
    .local v14, "ignoreBottomDecorations":Z
    :goto_4
    move-object/from16 v0, p0

    #@130
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@132
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/ListPopupWindow;->getAnchorView()Landroid/view/View;

    #@135
    move-result-object v3

    #@136
    move-object/from16 v0, p0

    #@138
    iget v4, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownVerticalOffset:I

    #@13a
    invoke-virtual {v1, v3, v4}, Landroid/widget/PopupWindow;->getMaxAvailableHeight(Landroid/view/View;I)I

    #@13d
    move-result v16

    #@13e
    .line 1177
    .local v16, "maxHeight":I
    move-object/from16 v0, p0

    #@140
    iget-boolean v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownAlwaysVisible:Z

    #@142
    if-nez v1, :cond_5

    #@144
    move-object/from16 v0, p0

    #@146
    iget v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHeight:I

    #@148
    const/4 v3, -0x1

    #@149
    if-ne v1, v3, :cond_a

    #@14b
    .line 1178
    :cond_5
    add-int v1, v16, v18

    #@14d
    .line 1206
    :goto_5
    return v1

    #@14e
    .line 1070
    .end local v7    # "background":Landroid/graphics/drawable/Drawable;
    .end local v9    # "dropDownView":Landroid/view/ViewGroup;
    .end local v14    # "ignoreBottomDecorations":Z
    .end local v16    # "maxHeight":I
    .end local v18    # "padding":I
    .restart local v8    # "context":Landroid/content/Context;
    :cond_6
    const/4 v1, 0x0

    #@14f
    goto/16 :goto_0

    #@151
    .line 1115
    .restart local v9    # "dropDownView":Landroid/view/ViewGroup;
    .restart local v11    # "hintContainer":Landroid/widget/LinearLayout;
    .restart local v12    # "hintParams":Landroid/widget/LinearLayout$LayoutParams;
    .restart local v13    # "hintView":Landroid/view/View;
    :pswitch_0
    invoke-virtual {v11, v9, v12}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@154
    .line 1116
    invoke-virtual {v11, v13}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    #@157
    goto/16 :goto_1

    #@159
    .line 1120
    :pswitch_1
    invoke-virtual {v11, v13}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;)V

    #@15c
    .line 1121
    invoke-virtual {v11, v9, v12}, Landroid/widget/LinearLayout;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@15f
    goto/16 :goto_1

    #@161
    .line 1144
    .end local v8    # "context":Landroid/content/Context;
    .end local v9    # "dropDownView":Landroid/view/ViewGroup;
    .end local v11    # "hintContainer":Landroid/widget/LinearLayout;
    .end local v12    # "hintParams":Landroid/widget/LinearLayout$LayoutParams;
    .end local v13    # "hintView":Landroid/view/View;
    :cond_7
    move-object/from16 v0, p0

    #@163
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@165
    invoke-virtual {v1}, Landroid/widget/PopupWindow;->getContentView()Landroid/view/View;

    #@168
    move-result-object v9

    #@169
    check-cast v9, Landroid/view/ViewGroup;

    #@16b
    .line 1145
    .restart local v9    # "dropDownView":Landroid/view/ViewGroup;
    move-object/from16 v0, p0

    #@16d
    iget-object v0, v0, Landroid/support/v7/widget/ListPopupWindow;->mPromptView:Landroid/view/View;

    #@16f
    move-object/from16 v19, v0

    #@171
    .line 1146
    .local v19, "view":Landroid/view/View;
    if-eqz v19, :cond_3

    #@173
    .line 1147
    invoke-virtual/range {v19 .. v19}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@176
    move-result-object v12

    #@177
    check-cast v12, Landroid/widget/LinearLayout$LayoutParams;

    #@179
    .line 1149
    .restart local v12    # "hintParams":Landroid/widget/LinearLayout$LayoutParams;
    invoke-virtual/range {v19 .. v19}, Landroid/view/View;->getMeasuredHeight()I

    #@17c
    move-result v1

    #@17d
    iget v3, v12, Landroid/widget/LinearLayout$LayoutParams;->topMargin:I

    #@17f
    add-int/2addr v1, v3

    #@180
    iget v3, v12, Landroid/widget/LinearLayout$LayoutParams;->bottomMargin:I

    #@182
    add-int v17, v1, v3

    #@184
    goto/16 :goto_2

    #@186
    .line 1168
    .end local v12    # "hintParams":Landroid/widget/LinearLayout$LayoutParams;
    .end local v19    # "view":Landroid/view/View;
    .restart local v7    # "background":Landroid/graphics/drawable/Drawable;
    .restart local v18    # "padding":I
    :cond_8
    move-object/from16 v0, p0

    #@188
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mTempRect:Landroid/graphics/Rect;

    #@18a
    invoke-virtual {v1}, Landroid/graphics/Rect;->setEmpty()V

    #@18d
    goto :goto_3

    #@18e
    .line 1172
    :cond_9
    const/4 v14, 0x0

    #@18f
    goto :goto_4

    #@190
    .line 1182
    .restart local v14    # "ignoreBottomDecorations":Z
    .restart local v16    # "maxHeight":I
    :cond_a
    move-object/from16 v0, p0

    #@192
    iget v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@194
    packed-switch v1, :pswitch_data_1

    #@197
    .line 1196
    move-object/from16 v0, p0

    #@199
    iget v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@19b
    const/high16 v3, 0x40000000    # 2.0f

    #@19d
    invoke-static {v1, v3}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@1a0
    move-result v2

    #@1a1
    .line 1200
    .local v2, "childWidthSpec":I
    :goto_6
    move-object/from16 v0, p0

    #@1a3
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@1a5
    const/4 v3, 0x0

    #@1a6
    const/4 v4, -0x1

    #@1a7
    sub-int v5, v16, v17

    #@1a9
    const/4 v6, -0x1

    #@1aa
    invoke-virtual/range {v1 .. v6}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->measureHeightOfChildrenCompat(IIIII)I

    #@1ad
    move-result v15

    #@1ae
    .line 1204
    .local v15, "listContent":I
    if-lez v15, :cond_b

    #@1b0
    add-int v17, v17, v18

    #@1b2
    .line 1206
    :cond_b
    add-int v1, v15, v17

    #@1b4
    goto :goto_5

    #@1b5
    .line 1184
    .end local v2    # "childWidthSpec":I
    .end local v15    # "listContent":I
    :pswitch_2
    move-object/from16 v0, p0

    #@1b7
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mContext:Landroid/content/Context;

    #@1b9
    invoke-virtual {v1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@1bc
    move-result-object v1

    #@1bd
    invoke-virtual {v1}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@1c0
    move-result-object v1

    #@1c1
    iget v1, v1, Landroid/util/DisplayMetrics;->widthPixels:I

    #@1c3
    move-object/from16 v0, p0

    #@1c5
    iget-object v3, v0, Landroid/support/v7/widget/ListPopupWindow;->mTempRect:Landroid/graphics/Rect;

    #@1c7
    iget v3, v3, Landroid/graphics/Rect;->left:I

    #@1c9
    move-object/from16 v0, p0

    #@1cb
    iget-object v4, v0, Landroid/support/v7/widget/ListPopupWindow;->mTempRect:Landroid/graphics/Rect;

    #@1cd
    iget v4, v4, Landroid/graphics/Rect;->right:I

    #@1cf
    add-int/2addr v3, v4

    #@1d0
    sub-int/2addr v1, v3

    #@1d1
    const/high16 v3, -0x80000000

    #@1d3
    invoke-static {v1, v3}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@1d6
    move-result v2

    #@1d7
    .line 1188
    .restart local v2    # "childWidthSpec":I
    goto :goto_6

    #@1d8
    .line 1190
    .end local v2    # "childWidthSpec":I
    :pswitch_3
    move-object/from16 v0, p0

    #@1da
    iget-object v1, v0, Landroid/support/v7/widget/ListPopupWindow;->mContext:Landroid/content/Context;

    #@1dc
    invoke-virtual {v1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@1df
    move-result-object v1

    #@1e0
    invoke-virtual {v1}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@1e3
    move-result-object v1

    #@1e4
    iget v1, v1, Landroid/util/DisplayMetrics;->widthPixels:I

    #@1e6
    move-object/from16 v0, p0

    #@1e8
    iget-object v3, v0, Landroid/support/v7/widget/ListPopupWindow;->mTempRect:Landroid/graphics/Rect;

    #@1ea
    iget v3, v3, Landroid/graphics/Rect;->left:I

    #@1ec
    move-object/from16 v0, p0

    #@1ee
    iget-object v4, v0, Landroid/support/v7/widget/ListPopupWindow;->mTempRect:Landroid/graphics/Rect;

    #@1f0
    iget v4, v4, Landroid/graphics/Rect;->right:I

    #@1f2
    add-int/2addr v3, v4

    #@1f3
    sub-int/2addr v1, v3

    #@1f4
    const/high16 v3, 0x40000000    # 2.0f

    #@1f6
    invoke-static {v1, v3}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@1f9
    move-result v2

    #@1fa
    .line 1194
    .restart local v2    # "childWidthSpec":I
    goto :goto_6

    #@1fb
    .line 1113
    nop

    #@1fc
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_0
    .end packed-switch

    #@204
    .line 1182
    :pswitch_data_1
    .packed-switch -0x2
        :pswitch_2
        :pswitch_3
    .end packed-switch
.end method

.method private static isConfirmKey(I)Z
    .locals 1
    .param p0, "keyCode"    # I

    #@0
    .prologue
    .line 1765
    const/16 v0, 0x42

    #@2
    if-eq p0, v0, :cond_0

    #@4
    const/16 v0, 0x17

    #@6
    if-ne p0, v0, :cond_1

    #@8
    :cond_0
    const/4 v0, 0x1

    #@9
    :goto_0
    return v0

    #@a
    :cond_1
    const/4 v0, 0x0

    #@b
    goto :goto_0
.end method

.method private removePromptView()V
    .locals 3

    #@0
    .prologue
    .line 688
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPromptView:Landroid/view/View;

    #@2
    if-eqz v2, :cond_0

    #@4
    .line 689
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPromptView:Landroid/view/View;

    #@6
    invoke-virtual {v2}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@9
    move-result-object v1

    #@a
    .line 690
    .local v1, "parent":Landroid/view/ViewParent;
    instance-of v2, v1, Landroid/view/ViewGroup;

    #@c
    if-eqz v2, :cond_0

    #@e
    move-object v0, v1

    #@f
    .line 691
    check-cast v0, Landroid/view/ViewGroup;

    #@11
    .line 692
    .local v0, "group":Landroid/view/ViewGroup;
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPromptView:Landroid/view/View;

    #@13
    invoke-virtual {v0, v2}, Landroid/view/ViewGroup;->removeView(Landroid/view/View;)V

    #@16
    .line 695
    .end local v0    # "group":Landroid/view/ViewGroup;
    .end local v1    # "parent":Landroid/view/ViewParent;
    :cond_0
    return-void
.end method

.method private setPopupClipToScreenEnabled(Z)V
    .locals 6
    .param p1, "clip"    # Z

    #@0
    .prologue
    .line 1769
    sget-object v1, Landroid/support/v7/widget/ListPopupWindow;->sClipToWindowEnabledMethod:Ljava/lang/reflect/Method;

    #@2
    if-eqz v1, :cond_0

    #@4
    .line 1771
    :try_start_0
    sget-object v1, Landroid/support/v7/widget/ListPopupWindow;->sClipToWindowEnabledMethod:Ljava/lang/reflect/Method;

    #@6
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@8
    const/4 v3, 0x1

    #@9
    new-array v3, v3, [Ljava/lang/Object;

    #@b
    const/4 v4, 0x0

    #@c
    invoke-static {p1}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    #@f
    move-result-object v5

    #@10
    aput-object v5, v3, v4

    #@12
    invoke-virtual {v1, v2, v3}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@15
    .line 1776
    :cond_0
    :goto_0
    return-void

    #@16
    .line 1772
    :catch_0
    move-exception v0

    #@17
    .line 1773
    .local v0, "e":Ljava/lang/Exception;
    const-string v1, "ListPopupWindow"

    #@19
    const-string v2, "Could not call setClipToScreenEnabled() on PopupWindow. Oh well."

    #@1b
    invoke-static {v1, v2}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    #@1e
    goto :goto_0
.end method


# virtual methods
.method public clearListSelection()V
    .locals 2

    #@0
    .prologue
    .line 747
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@2
    .line 748
    .local v0, "list":Landroid/support/v7/widget/ListPopupWindow$DropDownListView;
    if-eqz v0, :cond_0

    #@4
    .line 750
    const/4 v1, 0x1

    #@5
    # setter for: Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mListSelectionHidden:Z
    invoke-static {v0, v1}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->access$502(Landroid/support/v7/widget/ListPopupWindow$DropDownListView;Z)Z

    #@8
    .line 752
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->requestLayout()V

    #@b
    .line 754
    :cond_0
    return-void
.end method

.method public createDragToOpenListener(Landroid/view/View;)Landroid/view/View$OnTouchListener;
    .locals 1
    .param p1, "src"    # Landroid/view/View;

    #@0
    .prologue
    .line 1033
    new-instance v0, Landroid/support/v7/widget/ListPopupWindow$1;

    #@2
    invoke-direct {v0, p0, p1}, Landroid/support/v7/widget/ListPopupWindow$1;-><init>(Landroid/support/v7/widget/ListPopupWindow;Landroid/view/View;)V

    #@5
    return-object v0
.end method

.method public dismiss()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 671
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@3
    invoke-virtual {v0}, Landroid/widget/PopupWindow;->dismiss()V

    #@6
    .line 672
    invoke-direct {p0}, Landroid/support/v7/widget/ListPopupWindow;->removePromptView()V

    #@9
    .line 673
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@b
    invoke-virtual {v0, v1}, Landroid/widget/PopupWindow;->setContentView(Landroid/view/View;)V

    #@e
    .line 674
    iput-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@10
    .line 675
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mHandler:Landroid/os/Handler;

    #@12
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mResizePopupRunnable:Landroid/support/v7/widget/ListPopupWindow$ResizePopupRunnable;

    #@14
    invoke-virtual {v0, v1}, Landroid/os/Handler;->removeCallbacks(Ljava/lang/Runnable;)V

    #@17
    .line 676
    return-void
.end method

.method public getAnchorView()Landroid/view/View;
    .locals 1

    #@0
    .prologue
    .line 425
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownAnchorView:Landroid/view/View;

    #@2
    return-object v0
.end method

.method public getAnimationStyle()I
    .locals 1

    #@0
    .prologue
    .line 416
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2
    invoke-virtual {v0}, Landroid/widget/PopupWindow;->getAnimationStyle()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getBackground()Landroid/graphics/drawable/Drawable;
    .locals 1

    #@0
    .prologue
    .line 389
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2
    invoke-virtual {v0}, Landroid/widget/PopupWindow;->getBackground()Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getHeight()I
    .locals 1

    #@0
    .prologue
    .line 521
    iget v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHeight:I

    #@2
    return v0
.end method

.method public getHorizontalOffset()I
    .locals 1

    #@0
    .prologue
    .line 442
    iget v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHorizontalOffset:I

    #@2
    return v0
.end method

.method public getInputMethodMode()I
    .locals 1

    #@0
    .prologue
    .line 719
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2
    invoke-virtual {v0}, Landroid/widget/PopupWindow;->getInputMethodMode()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getListView()Landroid/widget/ListView;
    .locals 1

    #@0
    .prologue
    .line 845
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@2
    return-object v0
.end method

.method public getPromptPosition()I
    .locals 1

    #@0
    .prologue
    .line 291
    iget v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPromptPosition:I

    #@2
    return v0
.end method

.method public getSelectedItem()Ljava/lang/Object;
    .locals 1

    #@0
    .prologue
    .line 795
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    .line 796
    const/4 v0, 0x0

    #@7
    .line 798
    :goto_0
    return-object v0

    #@8
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@a
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getSelectedItem()Ljava/lang/Object;

    #@d
    move-result-object v0

    #@e
    goto :goto_0
.end method

.method public getSelectedItemId()J
    .locals 2

    #@0
    .prologue
    .line 821
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    .line 822
    const-wide/high16 v0, -0x8000000000000000L

    #@8
    .line 824
    :goto_0
    return-wide v0

    #@9
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@b
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getSelectedItemId()J

    #@e
    move-result-wide v0

    #@f
    goto :goto_0
.end method

.method public getSelectedItemPosition()I
    .locals 1

    #@0
    .prologue
    .line 808
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    .line 809
    const/4 v0, -0x1

    #@7
    .line 811
    :goto_0
    return v0

    #@8
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@a
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getSelectedItemPosition()I

    #@d
    move-result v0

    #@e
    goto :goto_0
.end method

.method public getSelectedView()Landroid/view/View;
    .locals 1

    #@0
    .prologue
    .line 834
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    .line 835
    const/4 v0, 0x0

    #@7
    .line 837
    :goto_0
    return-object v0

    #@8
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@a
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getSelectedView()Landroid/view/View;

    #@d
    move-result-object v0

    #@e
    goto :goto_0
.end method

.method public getSoftInputMode()I
    .locals 1

    #@0
    .prologue
    .line 373
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2
    invoke-virtual {v0}, Landroid/widget/PopupWindow;->getSoftInputMode()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getVerticalOffset()I
    .locals 1

    #@0
    .prologue
    .line 458
    iget-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownVerticalOffsetSet:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 459
    const/4 v0, 0x0

    #@5
    .line 461
    :goto_0
    return v0

    #@6
    :cond_0
    iget v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownVerticalOffset:I

    #@8
    goto :goto_0
.end method

.method public getWidth()I
    .locals 1

    #@0
    .prologue
    .line 488
    iget v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@2
    return v0
.end method

.method public isDropDownAlwaysVisible()Z
    .locals 1

    #@0
    .prologue
    .line 349
    iget-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownAlwaysVisible:Z

    #@2
    return v0
.end method

.method public isInputMethodNotNeeded()Z
    .locals 2

    #@0
    .prologue
    .line 768
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2
    invoke-virtual {v0}, Landroid/widget/PopupWindow;->getInputMethodMode()I

    #@5
    move-result v0

    #@6
    const/4 v1, 0x2

    #@7
    if-ne v0, v1, :cond_0

    #@9
    const/4 v0, 0x1

    #@a
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public isModal()Z
    .locals 1

    #@0
    .prologue
    .line 314
    iget-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mModal:Z

    #@2
    return v0
.end method

.method public isShowing()Z
    .locals 1

    #@0
    .prologue
    .line 760
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2
    invoke-virtual {v0}, Landroid/widget/PopupWindow;->isShowing()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public onKeyDown(ILandroid/view/KeyEvent;)Z
    .locals 13
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    const/16 v12, 0x14

    #@2
    const/16 v11, 0x13

    #@4
    const/4 v8, 0x0

    #@5
    const/4 v7, 0x1

    #@6
    .line 870
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@9
    move-result v9

    #@a
    if-eqz v9, :cond_8

    #@c
    .line 876
    const/16 v9, 0x3e

    #@e
    if-eq p1, v9, :cond_8

    #@10
    iget-object v9, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@12
    invoke-virtual {v9}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getSelectedItemPosition()I

    #@15
    move-result v9

    #@16
    if-gez v9, :cond_0

    #@18
    invoke-static {p1}, Landroid/support/v7/widget/ListPopupWindow;->isConfirmKey(I)Z

    #@1b
    move-result v9

    #@1c
    if-nez v9, :cond_8

    #@1e
    .line 879
    :cond_0
    iget-object v9, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@20
    invoke-virtual {v9}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getSelectedItemPosition()I

    #@23
    move-result v4

    #@24
    .line 882
    .local v4, "curIndex":I
    iget-object v9, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@26
    invoke-virtual {v9}, Landroid/widget/PopupWindow;->isAboveAnchor()Z

    #@29
    move-result v9

    #@2a
    if-nez v9, :cond_4

    #@2c
    move v2, v7

    #@2d
    .line 884
    .local v2, "below":Z
    :goto_0
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mAdapter:Landroid/widget/ListAdapter;

    #@2f
    .line 887
    .local v0, "adapter":Landroid/widget/ListAdapter;
    const v5, 0x7fffffff

    #@32
    .line 888
    .local v5, "firstItem":I
    const/high16 v6, -0x80000000

    #@34
    .line 890
    .local v6, "lastItem":I
    if-eqz v0, :cond_1

    #@36
    .line 891
    invoke-interface {v0}, Landroid/widget/ListAdapter;->areAllItemsEnabled()Z

    #@39
    move-result v1

    #@3a
    .line 892
    .local v1, "allEnabled":Z
    if-eqz v1, :cond_5

    #@3c
    move v5, v8

    #@3d
    .line 894
    :goto_1
    if-eqz v1, :cond_6

    #@3f
    invoke-interface {v0}, Landroid/widget/ListAdapter;->getCount()I

    #@42
    move-result v9

    #@43
    add-int/lit8 v6, v9, -0x1

    #@45
    .line 898
    .end local v1    # "allEnabled":Z
    :cond_1
    :goto_2
    if-eqz v2, :cond_2

    #@47
    if-ne p1, v11, :cond_2

    #@49
    if-le v4, v5, :cond_3

    #@4b
    :cond_2
    if-nez v2, :cond_7

    #@4d
    if-ne p1, v12, :cond_7

    #@4f
    if-lt v4, v6, :cond_7

    #@51
    .line 902
    :cond_3
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->clearListSelection()V

    #@54
    .line 903
    iget-object v8, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@56
    invoke-virtual {v8, v7}, Landroid/widget/PopupWindow;->setInputMethodMode(I)V

    #@59
    .line 904
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->show()V

    #@5c
    .line 950
    .end local v0    # "adapter":Landroid/widget/ListAdapter;
    .end local v2    # "below":Z
    .end local v4    # "curIndex":I
    .end local v5    # "firstItem":I
    .end local v6    # "lastItem":I
    :goto_3
    :sswitch_0
    return v7

    #@5d
    .restart local v4    # "curIndex":I
    :cond_4
    move v2, v8

    #@5e
    .line 882
    goto :goto_0

    #@5f
    .line 892
    .restart local v0    # "adapter":Landroid/widget/ListAdapter;
    .restart local v1    # "allEnabled":Z
    .restart local v2    # "below":Z
    .restart local v5    # "firstItem":I
    .restart local v6    # "lastItem":I
    :cond_5
    iget-object v9, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@61
    invoke-virtual {v9, v8, v7}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->lookForSelectablePosition(IZ)I

    #@64
    move-result v5

    #@65
    goto :goto_1

    #@66
    .line 894
    :cond_6
    iget-object v9, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@68
    invoke-interface {v0}, Landroid/widget/ListAdapter;->getCount()I

    #@6b
    move-result v10

    #@6c
    add-int/lit8 v10, v10, -0x1

    #@6e
    invoke-virtual {v9, v10, v8}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->lookForSelectablePosition(IZ)I

    #@71
    move-result v6

    #@72
    goto :goto_2

    #@73
    .line 909
    .end local v1    # "allEnabled":Z
    :cond_7
    iget-object v9, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@75
    # setter for: Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mListSelectionHidden:Z
    invoke-static {v9, v8}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->access$502(Landroid/support/v7/widget/ListPopupWindow$DropDownListView;Z)Z

    #@78
    .line 912
    iget-object v9, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@7a
    invoke-virtual {v9, p1, p2}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->onKeyDown(ILandroid/view/KeyEvent;)Z

    #@7d
    move-result v3

    #@7e
    .line 915
    .local v3, "consumed":Z
    if-eqz v3, :cond_9

    #@80
    .line 918
    iget-object v9, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@82
    const/4 v10, 0x2

    #@83
    invoke-virtual {v9, v10}, Landroid/widget/PopupWindow;->setInputMethodMode(I)V

    #@86
    .line 923
    iget-object v9, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@88
    invoke-virtual {v9}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->requestFocusFromTouch()Z

    #@8b
    .line 924
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->show()V

    #@8e
    .line 926
    sparse-switch p1, :sswitch_data_0

    #@91
    .end local v0    # "adapter":Landroid/widget/ListAdapter;
    .end local v2    # "below":Z
    .end local v3    # "consumed":Z
    .end local v4    # "curIndex":I
    .end local v5    # "firstItem":I
    .end local v6    # "lastItem":I
    :cond_8
    move v7, v8

    #@92
    .line 950
    goto :goto_3

    #@93
    .line 936
    .restart local v0    # "adapter":Landroid/widget/ListAdapter;
    .restart local v2    # "below":Z
    .restart local v3    # "consumed":Z
    .restart local v4    # "curIndex":I
    .restart local v5    # "firstItem":I
    .restart local v6    # "lastItem":I
    :cond_9
    if-eqz v2, :cond_a

    #@95
    if-ne p1, v12, :cond_a

    #@97
    .line 939
    if-ne v4, v6, :cond_8

    #@99
    goto :goto_3

    #@9a
    .line 942
    :cond_a
    if-nez v2, :cond_8

    #@9c
    if-ne p1, v11, :cond_8

    #@9e
    if-ne v4, v5, :cond_8

    #@a0
    goto :goto_3

    #@a1
    .line 926
    nop

    #@a2
    :sswitch_data_0
    .sparse-switch
        0x13 -> :sswitch_0
        0x14 -> :sswitch_0
        0x17 -> :sswitch_0
        0x42 -> :sswitch_0
    .end sparse-switch
.end method

.method public onKeyPreIme(ILandroid/view/KeyEvent;)Z
    .locals 4
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    .line 988
    const/4 v3, 0x4

    #@2
    if-ne p1, v3, :cond_3

    #@4
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@7
    move-result v3

    #@8
    if-eqz v3, :cond_3

    #@a
    .line 991
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownAnchorView:Landroid/view/View;

    #@c
    .line 992
    .local v0, "anchorView":Landroid/view/View;
    invoke-virtual {p2}, Landroid/view/KeyEvent;->getAction()I

    #@f
    move-result v3

    #@10
    if-nez v3, :cond_1

    #@12
    invoke-virtual {p2}, Landroid/view/KeyEvent;->getRepeatCount()I

    #@15
    move-result v3

    #@16
    if-nez v3, :cond_1

    #@18
    .line 993
    invoke-virtual {v0}, Landroid/view/View;->getKeyDispatcherState()Landroid/view/KeyEvent$DispatcherState;

    #@1b
    move-result-object v1

    #@1c
    .line 994
    .local v1, "state":Landroid/view/KeyEvent$DispatcherState;
    if-eqz v1, :cond_0

    #@1e
    .line 995
    invoke-virtual {v1, p2, p0}, Landroid/view/KeyEvent$DispatcherState;->startTracking(Landroid/view/KeyEvent;Ljava/lang/Object;)V

    #@21
    .line 1009
    .end local v0    # "anchorView":Landroid/view/View;
    .end local v1    # "state":Landroid/view/KeyEvent$DispatcherState;
    :cond_0
    :goto_0
    return v2

    #@22
    .line 998
    .restart local v0    # "anchorView":Landroid/view/View;
    :cond_1
    invoke-virtual {p2}, Landroid/view/KeyEvent;->getAction()I

    #@25
    move-result v3

    #@26
    if-ne v3, v2, :cond_3

    #@28
    .line 999
    invoke-virtual {v0}, Landroid/view/View;->getKeyDispatcherState()Landroid/view/KeyEvent$DispatcherState;

    #@2b
    move-result-object v1

    #@2c
    .line 1000
    .restart local v1    # "state":Landroid/view/KeyEvent$DispatcherState;
    if-eqz v1, :cond_2

    #@2e
    .line 1001
    invoke-virtual {v1, p2}, Landroid/view/KeyEvent$DispatcherState;->handleUpEvent(Landroid/view/KeyEvent;)V

    #@31
    .line 1003
    :cond_2
    invoke-virtual {p2}, Landroid/view/KeyEvent;->isTracking()Z

    #@34
    move-result v3

    #@35
    if-eqz v3, :cond_3

    #@37
    invoke-virtual {p2}, Landroid/view/KeyEvent;->isCanceled()Z

    #@3a
    move-result v3

    #@3b
    if-nez v3, :cond_3

    #@3d
    .line 1004
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->dismiss()V

    #@40
    goto :goto_0

    #@41
    .line 1009
    .end local v0    # "anchorView":Landroid/view/View;
    .end local v1    # "state":Landroid/view/KeyEvent$DispatcherState;
    :cond_3
    const/4 v2, 0x0

    #@42
    goto :goto_0
.end method

.method public onKeyUp(ILandroid/view/KeyEvent;)Z
    .locals 2
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 964
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@3
    move-result v1

    #@4
    if-eqz v1, :cond_1

    #@6
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@8
    invoke-virtual {v1}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getSelectedItemPosition()I

    #@b
    move-result v1

    #@c
    if-ltz v1, :cond_1

    #@e
    .line 965
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@10
    invoke-virtual {v1, p1, p2}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->onKeyUp(ILandroid/view/KeyEvent;)Z

    #@13
    move-result v0

    #@14
    .line 966
    .local v0, "consumed":Z
    if-eqz v0, :cond_0

    #@16
    invoke-static {p1}, Landroid/support/v7/widget/ListPopupWindow;->isConfirmKey(I)Z

    #@19
    move-result v1

    #@1a
    if-eqz v1, :cond_0

    #@1c
    .line 969
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->dismiss()V

    #@1f
    .line 973
    .end local v0    # "consumed":Z
    :cond_0
    :goto_0
    return v0

    #@20
    :cond_1
    const/4 v0, 0x0

    #@21
    goto :goto_0
.end method

.method public performItemClick(I)Z
    .locals 7
    .param p1, "position"    # I

    #@0
    .prologue
    .line 779
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@3
    move-result v0

    #@4
    if-eqz v0, :cond_1

    #@6
    .line 780
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mItemClickListener:Landroid/widget/AdapterView$OnItemClickListener;

    #@8
    if-eqz v0, :cond_0

    #@a
    .line 781
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@c
    .line 782
    .local v1, "list":Landroid/support/v7/widget/ListPopupWindow$DropDownListView;
    invoke-virtual {v1}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getFirstVisiblePosition()I

    #@f
    move-result v0

    #@10
    sub-int v0, p1, v0

    #@12
    invoke-virtual {v1, v0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getChildAt(I)Landroid/view/View;

    #@15
    move-result-object v2

    #@16
    .line 783
    .local v2, "child":Landroid/view/View;
    invoke-virtual {v1}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getAdapter()Landroid/widget/ListAdapter;

    #@19
    move-result-object v6

    #@1a
    .line 784
    .local v6, "adapter":Landroid/widget/ListAdapter;
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mItemClickListener:Landroid/widget/AdapterView$OnItemClickListener;

    #@1c
    invoke-interface {v6, p1}, Landroid/widget/ListAdapter;->getItemId(I)J

    #@1f
    move-result-wide v4

    #@20
    move v3, p1

    #@21
    invoke-interface/range {v0 .. v5}, Landroid/widget/AdapterView$OnItemClickListener;->onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V

    #@24
    .line 786
    .end local v1    # "list":Landroid/support/v7/widget/ListPopupWindow$DropDownListView;
    .end local v2    # "child":Landroid/view/View;
    .end local v6    # "adapter":Landroid/widget/ListAdapter;
    :cond_0
    const/4 v0, 0x1

    #@25
    .line 788
    :goto_0
    return v0

    #@26
    :cond_1
    const/4 v0, 0x0

    #@27
    goto :goto_0
.end method

.method public postShow()V
    .locals 2

    #@0
    .prologue
    .line 576
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mHandler:Landroid/os/Handler;

    #@2
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mShowDropDownRunnable:Ljava/lang/Runnable;

    #@4
    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    #@7
    .line 577
    return-void
.end method

.method public setAdapter(Landroid/widget/ListAdapter;)V
    .locals 2
    .param p1, "adapter"    # Landroid/widget/ListAdapter;

    #@0
    .prologue
    .line 256
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mObserver:Landroid/database/DataSetObserver;

    #@2
    if-nez v0, :cond_3

    #@4
    .line 257
    new-instance v0, Landroid/support/v7/widget/ListPopupWindow$PopupDataSetObserver;

    #@6
    const/4 v1, 0x0

    #@7
    invoke-direct {v0, p0, v1}, Landroid/support/v7/widget/ListPopupWindow$PopupDataSetObserver;-><init>(Landroid/support/v7/widget/ListPopupWindow;Landroid/support/v7/widget/ListPopupWindow$1;)V

    #@a
    iput-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mObserver:Landroid/database/DataSetObserver;

    #@c
    .line 261
    :cond_0
    :goto_0
    iput-object p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mAdapter:Landroid/widget/ListAdapter;

    #@e
    .line 262
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mAdapter:Landroid/widget/ListAdapter;

    #@10
    if-eqz v0, :cond_1

    #@12
    .line 263
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mObserver:Landroid/database/DataSetObserver;

    #@14
    invoke-interface {p1, v0}, Landroid/widget/ListAdapter;->registerDataSetObserver(Landroid/database/DataSetObserver;)V

    #@17
    .line 266
    :cond_1
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@19
    if-eqz v0, :cond_2

    #@1b
    .line 267
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@1d
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mAdapter:Landroid/widget/ListAdapter;

    #@1f
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setAdapter(Landroid/widget/ListAdapter;)V

    #@22
    .line 269
    :cond_2
    return-void

    #@23
    .line 258
    :cond_3
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mAdapter:Landroid/widget/ListAdapter;

    #@25
    if-eqz v0, :cond_0

    #@27
    .line 259
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mAdapter:Landroid/widget/ListAdapter;

    #@29
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mObserver:Landroid/database/DataSetObserver;

    #@2b
    invoke-interface {v0, v1}, Landroid/widget/ListAdapter;->unregisterDataSetObserver(Landroid/database/DataSetObserver;)V

    #@2e
    goto :goto_0
.end method

.method public setAnchorView(Landroid/view/View;)V
    .locals 0
    .param p1, "anchor"    # Landroid/view/View;

    #@0
    .prologue
    .line 435
    iput-object p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownAnchorView:Landroid/view/View;

    #@2
    .line 436
    return-void
.end method

.method public setAnimationStyle(I)V
    .locals 1
    .param p1, "animationStyle"    # I

    #@0
    .prologue
    .line 407
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2
    invoke-virtual {v0, p1}, Landroid/widget/PopupWindow;->setAnimationStyle(I)V

    #@5
    .line 408
    return-void
.end method

.method public setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 398
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2
    invoke-virtual {v0, p1}, Landroid/widget/PopupWindow;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@5
    .line 399
    return-void
.end method

.method public setContentWidth(I)V
    .locals 3
    .param p1, "width"    # I

    #@0
    .prologue
    .line 508
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2
    invoke-virtual {v1}, Landroid/widget/PopupWindow;->getBackground()Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    .line 509
    .local v0, "popupBackground":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_0

    #@8
    .line 510
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mTempRect:Landroid/graphics/Rect;

    #@a
    invoke-virtual {v0, v1}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@d
    .line 511
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mTempRect:Landroid/graphics/Rect;

    #@f
    iget v1, v1, Landroid/graphics/Rect;->left:I

    #@11
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mTempRect:Landroid/graphics/Rect;

    #@13
    iget v2, v2, Landroid/graphics/Rect;->right:I

    #@15
    add-int/2addr v1, v2

    #@16
    add-int/2addr v1, p1

    #@17
    iput v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@19
    .line 515
    :goto_0
    return-void

    #@1a
    .line 513
    :cond_0
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/ListPopupWindow;->setWidth(I)V

    #@1d
    goto :goto_0
.end method

.method public setDropDownAlwaysVisible(Z)V
    .locals 0
    .param p1, "dropDownAlwaysVisible"    # Z

    #@0
    .prologue
    .line 340
    iput-boolean p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownAlwaysVisible:Z

    #@2
    .line 341
    return-void
.end method

.method public setDropDownGravity(I)V
    .locals 0
    .param p1, "gravity"    # I

    #@0
    .prologue
    .line 481
    iput p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownGravity:I

    #@2
    .line 482
    return-void
.end method

.method public setForceIgnoreOutsideTouch(Z)V
    .locals 0
    .param p1, "forceIgnoreOutsideTouch"    # Z

    #@0
    .prologue
    .line 325
    iput-boolean p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mForceIgnoreOutsideTouch:Z

    #@2
    .line 326
    return-void
.end method

.method public setHeight(I)V
    .locals 0
    .param p1, "height"    # I

    #@0
    .prologue
    .line 530
    iput p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHeight:I

    #@2
    .line 531
    return-void
.end method

.method public setHorizontalOffset(I)V
    .locals 0
    .param p1, "offset"    # I

    #@0
    .prologue
    .line 451
    iput p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHorizontalOffset:I

    #@2
    .line 452
    return-void
.end method

.method public setInputMethodMode(I)V
    .locals 1
    .param p1, "mode"    # I

    #@0
    .prologue
    .line 710
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2
    invoke-virtual {v0, p1}, Landroid/widget/PopupWindow;->setInputMethodMode(I)V

    #@5
    .line 711
    return-void
.end method

.method setListItemExpandMax(I)V
    .locals 0
    .param p1, "max"    # I

    #@0
    .prologue
    .line 855
    iput p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mListItemExpandMaximum:I

    #@2
    .line 856
    return-void
.end method

.method public setListSelector(Landroid/graphics/drawable/Drawable;)V
    .locals 0
    .param p1, "selector"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 382
    iput-object p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownListHighlight:Landroid/graphics/drawable/Drawable;

    #@2
    .line 383
    return-void
.end method

.method public setModal(Z)V
    .locals 1
    .param p1, "modal"    # Z

    #@0
    .prologue
    .line 304
    iput-boolean p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mModal:Z

    #@2
    .line 305
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@4
    invoke-virtual {v0, p1}, Landroid/widget/PopupWindow;->setFocusable(Z)V

    #@7
    .line 306
    return-void
.end method

.method public setOnDismissListener(Landroid/widget/PopupWindow$OnDismissListener;)V
    .locals 1
    .param p1, "listener"    # Landroid/widget/PopupWindow$OnDismissListener;

    #@0
    .prologue
    .line 684
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2
    invoke-virtual {v0, p1}, Landroid/widget/PopupWindow;->setOnDismissListener(Landroid/widget/PopupWindow$OnDismissListener;)V

    #@5
    .line 685
    return-void
.end method

.method public setOnItemClickListener(Landroid/widget/AdapterView$OnItemClickListener;)V
    .locals 0
    .param p1, "clickListener"    # Landroid/widget/AdapterView$OnItemClickListener;

    #@0
    .prologue
    .line 541
    iput-object p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mItemClickListener:Landroid/widget/AdapterView$OnItemClickListener;

    #@2
    .line 542
    return-void
.end method

.method public setOnItemSelectedListener(Landroid/widget/AdapterView$OnItemSelectedListener;)V
    .locals 0
    .param p1, "selectedListener"    # Landroid/widget/AdapterView$OnItemSelectedListener;

    #@0
    .prologue
    .line 552
    iput-object p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mItemSelectedListener:Landroid/widget/AdapterView$OnItemSelectedListener;

    #@2
    .line 553
    return-void
.end method

.method public setPromptPosition(I)V
    .locals 0
    .param p1, "position"    # I

    #@0
    .prologue
    .line 281
    iput p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mPromptPosition:I

    #@2
    .line 282
    return-void
.end method

.method public setPromptView(Landroid/view/View;)V
    .locals 1
    .param p1, "prompt"    # Landroid/view/View;

    #@0
    .prologue
    .line 562
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@3
    move-result v0

    #@4
    .line 563
    .local v0, "showing":Z
    if-eqz v0, :cond_0

    #@6
    .line 564
    invoke-direct {p0}, Landroid/support/v7/widget/ListPopupWindow;->removePromptView()V

    #@9
    .line 566
    :cond_0
    iput-object p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mPromptView:Landroid/view/View;

    #@b
    .line 567
    if-eqz v0, :cond_1

    #@d
    .line 568
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->show()V

    #@10
    .line 570
    :cond_1
    return-void
.end method

.method public setSelection(I)V
    .locals 3
    .param p1, "position"    # I

    #@0
    .prologue
    .line 729
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@2
    .line 730
    .local v0, "list":Landroid/support/v7/widget/ListPopupWindow$DropDownListView;
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@5
    move-result v1

    #@6
    if-eqz v1, :cond_0

    #@8
    if-eqz v0, :cond_0

    #@a
    .line 731
    const/4 v1, 0x0

    #@b
    # setter for: Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->mListSelectionHidden:Z
    invoke-static {v0, v1}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->access$502(Landroid/support/v7/widget/ListPopupWindow$DropDownListView;Z)Z

    #@e
    .line 732
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setSelection(I)V

    #@11
    .line 734
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@13
    const/16 v2, 0xb

    #@15
    if-lt v1, v2, :cond_0

    #@17
    .line 735
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->getChoiceMode()I

    #@1a
    move-result v1

    #@1b
    if-eqz v1, :cond_0

    #@1d
    .line 736
    const/4 v1, 0x1

    #@1e
    invoke-virtual {v0, p1, v1}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setItemChecked(IZ)V

    #@21
    .line 740
    :cond_0
    return-void
.end method

.method public setSoftInputMode(I)V
    .locals 1
    .param p1, "mode"    # I

    #@0
    .prologue
    .line 363
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2
    invoke-virtual {v0, p1}, Landroid/widget/PopupWindow;->setSoftInputMode(I)V

    #@5
    .line 364
    return-void
.end method

.method public setVerticalOffset(I)V
    .locals 1
    .param p1, "offset"    # I

    #@0
    .prologue
    .line 470
    iput p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownVerticalOffset:I

    #@2
    .line 471
    const/4 v0, 0x1

    #@3
    iput-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownVerticalOffsetSet:Z

    #@5
    .line 472
    return-void
.end method

.method public setWidth(I)V
    .locals 0
    .param p1, "width"    # I

    #@0
    .prologue
    .line 498
    iput p1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@2
    .line 499
    return-void
.end method

.method public show()V
    .locals 10

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    const/4 v9, -0x2

    #@2
    const/4 v1, 0x0

    #@3
    const/4 v0, -0x1

    #@4
    .line 584
    invoke-direct {p0}, Landroid/support/v7/widget/ListPopupWindow;->buildDropDown()I

    #@7
    move-result v6

    #@8
    .line 586
    .local v6, "height":I
    const/4 v4, 0x0

    #@9
    .line 587
    .local v4, "widthSpec":I
    const/4 v5, 0x0

    #@a
    .line 589
    .local v5, "heightSpec":I
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->isInputMethodNotNeeded()Z

    #@d
    move-result v7

    #@e
    .line 591
    .local v7, "noInputMethod":Z
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@10
    invoke-virtual {v2}, Landroid/widget/PopupWindow;->isShowing()Z

    #@13
    move-result v2

    #@14
    if-eqz v2, :cond_a

    #@16
    .line 592
    iget v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@18
    if-ne v2, v0, :cond_2

    #@1a
    .line 595
    const/4 v4, -0x1

    #@1b
    .line 602
    :goto_0
    iget v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHeight:I

    #@1d
    if-ne v2, v0, :cond_8

    #@1f
    .line 605
    if-eqz v7, :cond_4

    #@21
    move v5, v6

    #@22
    .line 606
    :goto_1
    if-eqz v7, :cond_6

    #@24
    .line 607
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@26
    iget v8, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@28
    if-ne v8, v0, :cond_5

    #@2a
    :goto_2
    invoke-virtual {v2, v0, v1}, Landroid/widget/PopupWindow;->setWindowLayoutMode(II)V

    #@2d
    .line 622
    :goto_3
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@2f
    iget-boolean v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mForceIgnoreOutsideTouch:Z

    #@31
    if-nez v2, :cond_0

    #@33
    iget-boolean v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownAlwaysVisible:Z

    #@35
    if-nez v2, :cond_0

    #@37
    move v1, v3

    #@38
    :cond_0
    invoke-virtual {v0, v1}, Landroid/widget/PopupWindow;->setOutsideTouchable(Z)V

    #@3b
    .line 624
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@3d
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->getAnchorView()Landroid/view/View;

    #@40
    move-result-object v1

    #@41
    iget v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHorizontalOffset:I

    #@43
    iget v3, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownVerticalOffset:I

    #@45
    invoke-virtual/range {v0 .. v5}, Landroid/widget/PopupWindow;->update(Landroid/view/View;IIII)V

    #@48
    .line 665
    :cond_1
    :goto_4
    return-void

    #@49
    .line 596
    :cond_2
    iget v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@4b
    if-ne v2, v9, :cond_3

    #@4d
    .line 597
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->getAnchorView()Landroid/view/View;

    #@50
    move-result-object v2

    #@51
    invoke-virtual {v2}, Landroid/view/View;->getWidth()I

    #@54
    move-result v4

    #@55
    goto :goto_0

    #@56
    .line 599
    :cond_3
    iget v4, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@58
    goto :goto_0

    #@59
    :cond_4
    move v5, v0

    #@5a
    .line 605
    goto :goto_1

    #@5b
    :cond_5
    move v0, v1

    #@5c
    .line 607
    goto :goto_2

    #@5d
    .line 611
    :cond_6
    iget-object v8, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@5f
    iget v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@61
    if-ne v2, v0, :cond_7

    #@63
    move v2, v0

    #@64
    :goto_5
    invoke-virtual {v8, v2, v0}, Landroid/widget/PopupWindow;->setWindowLayoutMode(II)V

    #@67
    goto :goto_3

    #@68
    :cond_7
    move v2, v1

    #@69
    goto :goto_5

    #@6a
    .line 616
    :cond_8
    iget v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHeight:I

    #@6c
    if-ne v0, v9, :cond_9

    #@6e
    .line 617
    move v5, v6

    #@6f
    goto :goto_3

    #@70
    .line 619
    :cond_9
    iget v5, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHeight:I

    #@72
    goto :goto_3

    #@73
    .line 627
    :cond_a
    iget v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@75
    if-ne v2, v0, :cond_d

    #@77
    .line 628
    const/4 v4, -0x1

    #@78
    .line 637
    :goto_6
    iget v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHeight:I

    #@7a
    if-ne v2, v0, :cond_f

    #@7c
    .line 638
    const/4 v5, -0x1

    #@7d
    .line 647
    :goto_7
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@7f
    invoke-virtual {v2, v4, v5}, Landroid/widget/PopupWindow;->setWindowLayoutMode(II)V

    #@82
    .line 648
    invoke-direct {p0, v3}, Landroid/support/v7/widget/ListPopupWindow;->setPopupClipToScreenEnabled(Z)V

    #@85
    .line 652
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@87
    iget-boolean v8, p0, Landroid/support/v7/widget/ListPopupWindow;->mForceIgnoreOutsideTouch:Z

    #@89
    if-nez v8, :cond_11

    #@8b
    iget-boolean v8, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownAlwaysVisible:Z

    #@8d
    if-nez v8, :cond_11

    #@8f
    :goto_8
    invoke-virtual {v2, v3}, Landroid/widget/PopupWindow;->setOutsideTouchable(Z)V

    #@92
    .line 653
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@94
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mTouchInterceptor:Landroid/support/v7/widget/ListPopupWindow$PopupTouchInterceptor;

    #@96
    invoke-virtual {v1, v2}, Landroid/widget/PopupWindow;->setTouchInterceptor(Landroid/view/View$OnTouchListener;)V

    #@99
    .line 654
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@9b
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->getAnchorView()Landroid/view/View;

    #@9e
    move-result-object v2

    #@9f
    iget v3, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHorizontalOffset:I

    #@a1
    iget v8, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownVerticalOffset:I

    #@a3
    iget v9, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownGravity:I

    #@a5
    invoke-static {v1, v2, v3, v8, v9}, Landroid/support/v4/widget/PopupWindowCompat;->showAsDropDown(Landroid/widget/PopupWindow;Landroid/view/View;III)V

    #@a8
    .line 656
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@aa
    invoke-virtual {v1, v0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->setSelection(I)V

    #@ad
    .line 658
    iget-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mModal:Z

    #@af
    if-eqz v0, :cond_b

    #@b1
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@b3
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->isInTouchMode()Z

    #@b6
    move-result v0

    #@b7
    if-eqz v0, :cond_c

    #@b9
    .line 659
    :cond_b
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->clearListSelection()V

    #@bc
    .line 661
    :cond_c
    iget-boolean v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mModal:Z

    #@be
    if-nez v0, :cond_1

    #@c0
    .line 662
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow;->mHandler:Landroid/os/Handler;

    #@c2
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow;->mHideSelector:Landroid/support/v7/widget/ListPopupWindow$ListSelectorHider;

    #@c4
    invoke-virtual {v0, v1}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    #@c7
    goto :goto_4

    #@c8
    .line 630
    :cond_d
    iget v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@ca
    if-ne v2, v9, :cond_e

    #@cc
    .line 631
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@ce
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow;->getAnchorView()Landroid/view/View;

    #@d1
    move-result-object v8

    #@d2
    invoke-virtual {v8}, Landroid/view/View;->getWidth()I

    #@d5
    move-result v8

    #@d6
    invoke-virtual {v2, v8}, Landroid/widget/PopupWindow;->setWidth(I)V

    #@d9
    goto :goto_6

    #@da
    .line 633
    :cond_e
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@dc
    iget v8, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownWidth:I

    #@de
    invoke-virtual {v2, v8}, Landroid/widget/PopupWindow;->setWidth(I)V

    #@e1
    goto :goto_6

    #@e2
    .line 640
    :cond_f
    iget v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHeight:I

    #@e4
    if-ne v2, v9, :cond_10

    #@e6
    .line 641
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@e8
    invoke-virtual {v2, v6}, Landroid/widget/PopupWindow;->setHeight(I)V

    #@eb
    goto :goto_7

    #@ec
    .line 643
    :cond_10
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow;->mPopup:Landroid/widget/PopupWindow;

    #@ee
    iget v8, p0, Landroid/support/v7/widget/ListPopupWindow;->mDropDownHeight:I

    #@f0
    invoke-virtual {v2, v8}, Landroid/widget/PopupWindow;->setHeight(I)V

    #@f3
    goto :goto_7

    #@f4
    :cond_11
    move v3, v1

    #@f5
    .line 652
    goto :goto_8
.end method
