.class Landroid/support/v7/internal/widget/SpinnerCompat;
.super Landroid/support/v7/internal/widget/AbsSpinnerCompat;
.source "SpinnerCompat.java"

# interfaces
.implements Landroid/content/DialogInterface$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;,
        Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;,
        Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;,
        Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;,
        Landroid/support/v7/internal/widget/SpinnerCompat$SavedState;
    }
.end annotation


# static fields
.field private static final MAX_ITEMS_MEASURED:I = 0xf

.field public static final MODE_DIALOG:I = 0x0

.field public static final MODE_DROPDOWN:I = 0x1

.field private static final MODE_THEME:I = -0x1

.field private static final TAG:Ljava/lang/String; = "Spinner"


# instance fields
.field private mDisableChildrenWhenDisabled:Z

.field mDropDownWidth:I

.field private mForwardingListener:Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;

.field private mGravity:I

.field private mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

.field private mTempAdapter:Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;

.field private mTempRect:Landroid/graphics/Rect;

.field private final mTintManager:Landroid/support/v7/internal/widget/TintManager;


# direct methods
.method constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 100
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/internal/widget/SpinnerCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 101
    return-void
.end method

.method constructor <init>(Landroid/content/Context;I)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "mode"    # I

    #@0
    .prologue
    .line 114
    const/4 v0, 0x0

    #@1
    sget v1, Landroid/support/v7/appcompat/R$attr;->spinnerStyle:I

    #@3
    invoke-direct {p0, p1, v0, v1, p2}, Landroid/support/v7/internal/widget/SpinnerCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;II)V

    #@6
    .line 115
    return-void
.end method

.method constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 125
    sget v0, Landroid/support/v7/appcompat/R$attr;->spinnerStyle:I

    #@2
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/internal/widget/SpinnerCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@5
    .line 126
    return-void
.end method

.method constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyle"    # I

    #@0
    .prologue
    .line 141
    const/4 v0, -0x1

    #@1
    invoke-direct {p0, p1, p2, p3, v0}, Landroid/support/v7/internal/widget/SpinnerCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;II)V

    #@4
    .line 142
    return-void
.end method

.method constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;II)V
    .locals 6
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyle"    # I
    .param p4, "mode"    # I

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    const/4 v4, 0x0

    #@2
    .line 161
    invoke-direct {p0, p1, p2, p3}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@5
    .line 89
    new-instance v2, Landroid/graphics/Rect;

    #@7
    invoke-direct {v2}, Landroid/graphics/Rect;-><init>()V

    #@a
    iput-object v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mTempRect:Landroid/graphics/Rect;

    #@c
    .line 163
    sget-object v2, Landroid/support/v7/appcompat/R$styleable;->Spinner:[I

    #@e
    invoke-static {p1, p2, v2, p3, v4}, Landroid/support/v7/internal/widget/TintTypedArray;->obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/internal/widget/TintTypedArray;

    #@11
    move-result-object v0

    #@12
    .line 167
    .local v0, "a":Landroid/support/v7/internal/widget/TintTypedArray;
    sget v2, Landroid/support/v7/appcompat/R$styleable;->Spinner_android_background:I

    #@14
    invoke-virtual {v0, v2}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@17
    move-result-object v2

    #@18
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/SpinnerCompat;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@1b
    .line 169
    const/4 v2, -0x1

    #@1c
    if-ne p4, v2, :cond_0

    #@1e
    .line 170
    sget v2, Landroid/support/v7/appcompat/R$styleable;->Spinner_spinnerMode:I

    #@20
    invoke-virtual {v0, v2, v4}, Landroid/support/v7/internal/widget/TintTypedArray;->getInt(II)I

    #@23
    move-result p4

    #@24
    .line 173
    :cond_0
    packed-switch p4, :pswitch_data_0

    #@27
    .line 207
    :goto_0
    sget v2, Landroid/support/v7/appcompat/R$styleable;->Spinner_android_gravity:I

    #@29
    const/16 v3, 0x11

    #@2b
    invoke-virtual {v0, v2, v3}, Landroid/support/v7/internal/widget/TintTypedArray;->getInt(II)I

    #@2e
    move-result v2

    #@2f
    iput v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mGravity:I

    #@31
    .line 209
    iget-object v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@33
    sget v3, Landroid/support/v7/appcompat/R$styleable;->Spinner_prompt:I

    #@35
    invoke-virtual {v0, v3}, Landroid/support/v7/internal/widget/TintTypedArray;->getString(I)Ljava/lang/String;

    #@38
    move-result-object v3

    #@39
    invoke-interface {v2, v3}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->setPromptText(Ljava/lang/CharSequence;)V

    #@3c
    .line 211
    sget v2, Landroid/support/v7/appcompat/R$styleable;->Spinner_disableChildrenWhenDisabled:I

    #@3e
    invoke-virtual {v0, v2, v4}, Landroid/support/v7/internal/widget/TintTypedArray;->getBoolean(IZ)Z

    #@41
    move-result v2

    #@42
    iput-boolean v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mDisableChildrenWhenDisabled:Z

    #@44
    .line 214
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->recycle()V

    #@47
    .line 218
    iget-object v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mTempAdapter:Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;

    #@49
    if-eqz v2, :cond_1

    #@4b
    .line 219
    iget-object v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@4d
    iget-object v3, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mTempAdapter:Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;

    #@4f
    invoke-interface {v2, v3}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->setAdapter(Landroid/widget/ListAdapter;)V

    #@52
    .line 220
    iput-object v5, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mTempAdapter:Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;

    #@54
    .line 224
    :cond_1
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getTintManager()Landroid/support/v7/internal/widget/TintManager;

    #@57
    move-result-object v2

    #@58
    iput-object v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@5a
    .line 225
    return-void

    #@5b
    .line 175
    :pswitch_0
    new-instance v2, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;

    #@5d
    invoke-direct {v2, p0, v5}, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;-><init>(Landroid/support/v7/internal/widget/SpinnerCompat;Landroid/support/v7/internal/widget/SpinnerCompat$1;)V

    #@60
    iput-object v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@62
    goto :goto_0

    #@63
    .line 180
    :pswitch_1
    new-instance v1, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;

    #@65
    invoke-direct {v1, p0, p1, p2, p3}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;-><init>(Landroid/support/v7/internal/widget/SpinnerCompat;Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@68
    .line 182
    .local v1, "popup":Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;
    sget v2, Landroid/support/v7/appcompat/R$styleable;->Spinner_android_dropDownWidth:I

    #@6a
    const/4 v3, -0x2

    #@6b
    invoke-virtual {v0, v2, v3}, Landroid/support/v7/internal/widget/TintTypedArray;->getLayoutDimension(II)I

    #@6e
    move-result v2

    #@6f
    iput v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mDropDownWidth:I

    #@71
    .line 185
    sget v2, Landroid/support/v7/appcompat/R$styleable;->Spinner_android_popupBackground:I

    #@73
    invoke-virtual {v0, v2}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@76
    move-result-object v2

    #@77
    invoke-virtual {v1, v2}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@7a
    .line 188
    iput-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@7c
    .line 189
    new-instance v2, Landroid/support/v7/internal/widget/SpinnerCompat$1;

    #@7e
    invoke-direct {v2, p0, p0, v1}, Landroid/support/v7/internal/widget/SpinnerCompat$1;-><init>(Landroid/support/v7/internal/widget/SpinnerCompat;Landroid/view/View;Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;)V

    #@81
    iput-object v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mForwardingListener:Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;

    #@83
    goto :goto_0

    #@84
    .line 173
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method

.method static synthetic access$100(Landroid/support/v7/internal/widget/SpinnerCompat;)Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/SpinnerCompat;

    #@0
    .prologue
    .line 53
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@2
    return-object v0
.end method

.method static synthetic access$400(Landroid/support/v7/internal/widget/SpinnerCompat;)Landroid/graphics/Rect;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/SpinnerCompat;

    #@0
    .prologue
    .line 53
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mTempRect:Landroid/graphics/Rect;

    #@2
    return-object v0
.end method

.method private makeView(IZ)Landroid/view/View;
    .locals 4
    .param p1, "position"    # I
    .param p2, "addChild"    # Z

    #@0
    .prologue
    .line 527
    iget-boolean v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mDataChanged:Z

    #@2
    if-nez v2, :cond_0

    #@4
    .line 528
    iget-object v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mRecycler:Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;

    #@6
    invoke-virtual {v2, p1}, Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;->get(I)Landroid/view/View;

    #@9
    move-result-object v0

    #@a
    .line 529
    .local v0, "child":Landroid/view/View;
    if-eqz v0, :cond_0

    #@c
    .line 531
    invoke-direct {p0, v0, p2}, Landroid/support/v7/internal/widget/SpinnerCompat;->setUpChild(Landroid/view/View;Z)V

    #@f
    move-object v1, v0

    #@10
    .line 543
    .end local v0    # "child":Landroid/view/View;
    .local v1, "child":Landroid/view/View;
    :goto_0
    return-object v1

    #@11
    .line 538
    .end local v1    # "child":Landroid/view/View;
    :cond_0
    iget-object v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@13
    const/4 v3, 0x0

    #@14
    invoke-interface {v2, p1, v3, p0}, Landroid/widget/SpinnerAdapter;->getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;

    #@17
    move-result-object v0

    #@18
    .line 541
    .restart local v0    # "child":Landroid/view/View;
    invoke-direct {p0, v0, p2}, Landroid/support/v7/internal/widget/SpinnerCompat;->setUpChild(Landroid/view/View;Z)V

    #@1b
    move-object v1, v0

    #@1c
    .line 543
    .end local v0    # "child":Landroid/view/View;
    .restart local v1    # "child":Landroid/view/View;
    goto :goto_0
.end method

.method private setUpChild(Landroid/view/View;Z)V
    .locals 11
    .param p1, "child"    # Landroid/view/View;
    .param p2, "addChild"    # Z

    #@0
    .prologue
    .line 556
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v6

    #@4
    .line 557
    .local v6, "lp":Landroid/view/ViewGroup$LayoutParams;
    if-nez v6, :cond_0

    #@6
    .line 558
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@9
    move-result-object v6

    #@a
    .line 561
    :cond_0
    if-eqz p2, :cond_1

    #@c
    .line 562
    const/4 v8, 0x0

    #@d
    invoke-virtual {p0, p1, v8, v6}, Landroid/support/v7/internal/widget/SpinnerCompat;->addViewInLayout(Landroid/view/View;ILandroid/view/ViewGroup$LayoutParams;)Z

    #@10
    .line 565
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->hasFocus()Z

    #@13
    move-result v8

    #@14
    invoke-virtual {p1, v8}, Landroid/view/View;->setSelected(Z)V

    #@17
    .line 566
    iget-boolean v8, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mDisableChildrenWhenDisabled:Z

    #@19
    if-eqz v8, :cond_2

    #@1b
    .line 567
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->isEnabled()Z

    #@1e
    move-result v8

    #@1f
    invoke-virtual {p1, v8}, Landroid/view/View;->setEnabled(Z)V

    #@22
    .line 571
    :cond_2
    iget v8, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mHeightMeasureSpec:I

    #@24
    iget-object v9, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@26
    iget v9, v9, Landroid/graphics/Rect;->top:I

    #@28
    iget-object v10, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@2a
    iget v10, v10, Landroid/graphics/Rect;->bottom:I

    #@2c
    add-int/2addr v9, v10

    #@2d
    iget v10, v6, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@2f
    invoke-static {v8, v9, v10}, Landroid/view/ViewGroup;->getChildMeasureSpec(III)I

    #@32
    move-result v1

    #@33
    .line 573
    .local v1, "childHeightSpec":I
    iget v8, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mWidthMeasureSpec:I

    #@35
    iget-object v9, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@37
    iget v9, v9, Landroid/graphics/Rect;->left:I

    #@39
    iget-object v10, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@3b
    iget v10, v10, Landroid/graphics/Rect;->right:I

    #@3d
    add-int/2addr v9, v10

    #@3e
    iget v10, v6, Landroid/view/ViewGroup$LayoutParams;->width:I

    #@40
    invoke-static {v8, v9, v10}, Landroid/view/ViewGroup;->getChildMeasureSpec(III)I

    #@43
    move-result v5

    #@44
    .line 577
    .local v5, "childWidthSpec":I
    invoke-virtual {p1, v5, v1}, Landroid/view/View;->measure(II)V

    #@47
    .line 583
    iget-object v8, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@49
    iget v8, v8, Landroid/graphics/Rect;->top:I

    #@4b
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getMeasuredHeight()I

    #@4e
    move-result v9

    #@4f
    iget-object v10, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@51
    iget v10, v10, Landroid/graphics/Rect;->bottom:I

    #@53
    sub-int/2addr v9, v10

    #@54
    iget-object v10, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@56
    iget v10, v10, Landroid/graphics/Rect;->top:I

    #@58
    sub-int/2addr v9, v10

    #@59
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredHeight()I

    #@5c
    move-result v10

    #@5d
    sub-int/2addr v9, v10

    #@5e
    div-int/lit8 v9, v9, 0x2

    #@60
    add-int v4, v8, v9

    #@62
    .line 586
    .local v4, "childTop":I
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredHeight()I

    #@65
    move-result v8

    #@66
    add-int v0, v4, v8

    #@68
    .line 588
    .local v0, "childBottom":I
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredWidth()I

    #@6b
    move-result v7

    #@6c
    .line 589
    .local v7, "width":I
    const/4 v2, 0x0

    #@6d
    .line 590
    .local v2, "childLeft":I
    add-int v3, v2, v7

    #@6f
    .line 592
    .local v3, "childRight":I
    invoke-virtual {p1, v2, v4, v3, v0}, Landroid/view/View;->layout(IIII)V

    #@72
    .line 593
    return-void
.end method


# virtual methods
.method public getBaseline()I
    .locals 5

    #@0
    .prologue
    const/4 v2, -0x1

    #@1
    const/4 v4, 0x0

    #@2
    .line 378
    const/4 v0, 0x0

    #@3
    .line 380
    .local v0, "child":Landroid/view/View;
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getChildCount()I

    #@6
    move-result v3

    #@7
    if-lez v3, :cond_2

    #@9
    .line 381
    invoke-virtual {p0, v4}, Landroid/support/v7/internal/widget/SpinnerCompat;->getChildAt(I)Landroid/view/View;

    #@c
    move-result-object v0

    #@d
    .line 387
    :cond_0
    :goto_0
    if-eqz v0, :cond_1

    #@f
    .line 388
    invoke-virtual {v0}, Landroid/view/View;->getBaseline()I

    #@12
    move-result v1

    #@13
    .line 389
    .local v1, "childBaseline":I
    if-ltz v1, :cond_1

    #@15
    invoke-virtual {v0}, Landroid/view/View;->getTop()I

    #@18
    move-result v2

    #@19
    add-int/2addr v2, v1

    #@1a
    .line 391
    .end local v1    # "childBaseline":I
    :cond_1
    return v2

    #@1b
    .line 382
    :cond_2
    iget-object v3, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@1d
    if-eqz v3, :cond_0

    #@1f
    iget-object v3, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@21
    invoke-interface {v3}, Landroid/widget/SpinnerAdapter;->getCount()I

    #@24
    move-result v3

    #@25
    if-lez v3, :cond_0

    #@27
    .line 383
    invoke-direct {p0, v4, v4}, Landroid/support/v7/internal/widget/SpinnerCompat;->makeView(IZ)Landroid/view/View;

    #@2a
    move-result-object v0

    #@2b
    .line 384
    iget-object v3, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mRecycler:Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;

    #@2d
    invoke-virtual {v3, v4, v0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;->put(ILandroid/view/View;)V

    #@30
    goto :goto_0
.end method

.method public getDropDownHorizontalOffset()I
    .locals 1

    #@0
    .prologue
    .line 298
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->getHorizontalOffset()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getDropDownVerticalOffset()I
    .locals 1

    #@0
    .prologue
    .line 278
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->getVerticalOffset()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getDropDownWidth()I
    .locals 1

    #@0
    .prologue
    .line 328
    iget v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mDropDownWidth:I

    #@2
    return v0
.end method

.method public getPopupBackground()Landroid/graphics/drawable/Drawable;
    .locals 1

    #@0
    .prologue
    .line 258
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->getBackground()Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getPrompt()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 635
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@2
    invoke-interface {v0}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->getHintText()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method layout(IZ)V
    .locals 10
    .param p1, "delta"    # I
    .param p2, "animate"    # Z

    #@0
    .prologue
    const/4 v9, 0x0

    #@1
    .line 461
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@3
    iget v1, v7, Landroid/graphics/Rect;->left:I

    #@5
    .line 462
    .local v1, "childrenLeft":I
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getRight()I

    #@8
    move-result v7

    #@9
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getLeft()I

    #@c
    move-result v8

    #@d
    sub-int/2addr v7, v8

    #@e
    iget-object v8, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@10
    iget v8, v8, Landroid/graphics/Rect;->left:I

    #@12
    sub-int/2addr v7, v8

    #@13
    iget-object v8, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mSpinnerPadding:Landroid/graphics/Rect;

    #@15
    iget v8, v8, Landroid/graphics/Rect;->right:I

    #@17
    sub-int v2, v7, v8

    #@19
    .line 464
    .local v2, "childrenWidth":I
    iget-boolean v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mDataChanged:Z

    #@1b
    if-eqz v7, :cond_0

    #@1d
    .line 465
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->handleDataChanged()V

    #@20
    .line 469
    :cond_0
    iget v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mItemCount:I

    #@22
    if-nez v7, :cond_1

    #@24
    .line 470
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->resetList()V

    #@27
    .line 512
    :goto_0
    return-void

    #@28
    .line 474
    :cond_1
    iget v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mNextSelectedPosition:I

    #@2a
    if-ltz v7, :cond_2

    #@2c
    .line 475
    iget v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mNextSelectedPosition:I

    #@2e
    invoke-virtual {p0, v7}, Landroid/support/v7/internal/widget/SpinnerCompat;->setSelectedPositionInt(I)V

    #@31
    .line 478
    :cond_2
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->recycleAllViews()V

    #@34
    .line 481
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->removeAllViewsInLayout()V

    #@37
    .line 484
    iget v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mSelectedPosition:I

    #@39
    iput v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mFirstPosition:I

    #@3b
    .line 485
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mAdapter:Landroid/widget/SpinnerAdapter;

    #@3d
    if-eqz v7, :cond_3

    #@3f
    .line 486
    iget v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mSelectedPosition:I

    #@41
    const/4 v8, 0x1

    #@42
    invoke-direct {p0, v7, v8}, Landroid/support/v7/internal/widget/SpinnerCompat;->makeView(IZ)Landroid/view/View;

    #@45
    move-result-object v4

    #@46
    .line 487
    .local v4, "sel":Landroid/view/View;
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredWidth()I

    #@49
    move-result v6

    #@4a
    .line 488
    .local v6, "width":I
    move v5, v1

    #@4b
    .line 489
    .local v5, "selectedOffset":I
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@4e
    move-result v3

    #@4f
    .line 490
    .local v3, "layoutDirection":I
    iget v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mGravity:I

    #@51
    invoke-static {v7, v3}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@54
    move-result v0

    #@55
    .line 491
    .local v0, "absoluteGravity":I
    and-int/lit8 v7, v0, 0x7

    #@57
    sparse-switch v7, :sswitch_data_0

    #@5a
    .line 499
    :goto_1
    invoke-virtual {v4, v5}, Landroid/view/View;->offsetLeftAndRight(I)V

    #@5d
    .line 503
    .end local v0    # "absoluteGravity":I
    .end local v3    # "layoutDirection":I
    .end local v4    # "sel":Landroid/view/View;
    .end local v5    # "selectedOffset":I
    .end local v6    # "width":I
    :cond_3
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mRecycler:Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;

    #@5f
    invoke-virtual {v7}, Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;->clear()V

    #@62
    .line 505
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->invalidate()V

    #@65
    .line 507
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->checkSelectionChanged()V

    #@68
    .line 509
    iput-boolean v9, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mDataChanged:Z

    #@6a
    .line 510
    iput-boolean v9, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mNeedSync:Z

    #@6c
    .line 511
    iget v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mSelectedPosition:I

    #@6e
    invoke-virtual {p0, v7}, Landroid/support/v7/internal/widget/SpinnerCompat;->setNextSelectedPositionInt(I)V

    #@71
    goto :goto_0

    #@72
    .line 493
    .restart local v0    # "absoluteGravity":I
    .restart local v3    # "layoutDirection":I
    .restart local v4    # "sel":Landroid/view/View;
    .restart local v5    # "selectedOffset":I
    .restart local v6    # "width":I
    :sswitch_0
    div-int/lit8 v7, v2, 0x2

    #@74
    add-int/2addr v7, v1

    #@75
    div-int/lit8 v8, v6, 0x2

    #@77
    sub-int v5, v7, v8

    #@79
    .line 494
    goto :goto_1

    #@7a
    .line 496
    :sswitch_1
    add-int v7, v1, v2

    #@7c
    sub-int v5, v7, v6

    #@7e
    goto :goto_1

    #@7f
    .line 491
    nop

    #@80
    :sswitch_data_0
    .sparse-switch
        0x1 -> :sswitch_0
        0x5 -> :sswitch_1
    .end sparse-switch
.end method

.method measureContentWidth(Landroid/widget/SpinnerAdapter;Landroid/graphics/drawable/Drawable;)I
    .locals 13
    .param p1, "adapter"    # Landroid/widget/SpinnerAdapter;
    .param p2, "background"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 639
    if-nez p1, :cond_1

    #@2
    .line 640
    const/4 v8, 0x0

    #@3
    .line 679
    :cond_0
    :goto_0
    return v8

    #@4
    .line 643
    :cond_1
    const/4 v8, 0x0

    #@5
    .line 644
    .local v8, "width":I
    const/4 v5, 0x0

    #@6
    .line 645
    .local v5, "itemView":Landroid/view/View;
    const/4 v4, 0x0

    #@7
    .line 646
    .local v4, "itemType":I
    const/4 v10, 0x0

    #@8
    const/4 v11, 0x0

    #@9
    invoke-static {v10, v11}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@c
    move-result v9

    #@d
    .line 648
    .local v9, "widthMeasureSpec":I
    const/4 v10, 0x0

    #@e
    const/4 v11, 0x0

    #@f
    invoke-static {v10, v11}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@12
    move-result v2

    #@13
    .line 653
    .local v2, "heightMeasureSpec":I
    const/4 v10, 0x0

    #@14
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getSelectedItemPosition()I

    #@17
    move-result v11

    #@18
    invoke-static {v10, v11}, Ljava/lang/Math;->max(II)I

    #@1b
    move-result v7

    #@1c
    .line 654
    .local v7, "start":I
    invoke-interface {p1}, Landroid/widget/SpinnerAdapter;->getCount()I

    #@1f
    move-result v10

    #@20
    add-int/lit8 v11, v7, 0xf

    #@22
    invoke-static {v10, v11}, Ljava/lang/Math;->min(II)I

    #@25
    move-result v1

    #@26
    .line 655
    .local v1, "end":I
    sub-int v0, v1, v7

    #@28
    .line 656
    .local v0, "count":I
    const/4 v10, 0x0

    #@29
    rsub-int/lit8 v11, v0, 0xf

    #@2b
    sub-int v11, v7, v11

    #@2d
    invoke-static {v10, v11}, Ljava/lang/Math;->max(II)I

    #@30
    move-result v7

    #@31
    .line 657
    move v3, v7

    #@32
    .local v3, "i":I
    :goto_1
    if-ge v3, v1, :cond_4

    #@34
    .line 658
    invoke-interface {p1, v3}, Landroid/widget/SpinnerAdapter;->getItemViewType(I)I

    #@37
    move-result v6

    #@38
    .line 659
    .local v6, "positionType":I
    if-eq v6, v4, :cond_2

    #@3a
    .line 660
    move v4, v6

    #@3b
    .line 661
    const/4 v5, 0x0

    #@3c
    .line 663
    :cond_2
    invoke-interface {p1, v3, v5, p0}, Landroid/widget/SpinnerAdapter;->getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;

    #@3f
    move-result-object v5

    #@40
    .line 664
    invoke-virtual {v5}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@43
    move-result-object v10

    #@44
    if-nez v10, :cond_3

    #@46
    .line 665
    new-instance v10, Landroid/view/ViewGroup$LayoutParams;

    #@48
    const/4 v11, -0x2

    #@49
    const/4 v12, -0x2

    #@4a
    invoke-direct {v10, v11, v12}, Landroid/view/ViewGroup$LayoutParams;-><init>(II)V

    #@4d
    invoke-virtual {v5, v10}, Landroid/view/View;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@50
    .line 669
    :cond_3
    invoke-virtual {v5, v9, v2}, Landroid/view/View;->measure(II)V

    #@53
    .line 670
    invoke-virtual {v5}, Landroid/view/View;->getMeasuredWidth()I

    #@56
    move-result v10

    #@57
    invoke-static {v8, v10}, Ljava/lang/Math;->max(II)I

    #@5a
    move-result v8

    #@5b
    .line 657
    add-int/lit8 v3, v3, 0x1

    #@5d
    goto :goto_1

    #@5e
    .line 674
    .end local v6    # "positionType":I
    :cond_4
    if-eqz p2, :cond_0

    #@60
    .line 675
    iget-object v10, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mTempRect:Landroid/graphics/Rect;

    #@62
    invoke-virtual {p2, v10}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@65
    .line 676
    iget-object v10, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mTempRect:Landroid/graphics/Rect;

    #@67
    iget v10, v10, Landroid/graphics/Rect;->left:I

    #@69
    iget-object v11, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mTempRect:Landroid/graphics/Rect;

    #@6b
    iget v11, v11, Landroid/graphics/Rect;->right:I

    #@6d
    add-int/2addr v10, v11

    #@6e
    add-int/2addr v8, v10

    #@6f
    goto :goto_0
.end method

.method public onClick(Landroid/content/DialogInterface;I)V
    .locals 0
    .param p1, "dialog"    # Landroid/content/DialogInterface;
    .param p2, "which"    # I

    #@0
    .prologue
    .line 611
    invoke-virtual {p0, p2}, Landroid/support/v7/internal/widget/SpinnerCompat;->setSelection(I)V

    #@3
    .line 612
    invoke-interface {p1}, Landroid/content/DialogInterface;->dismiss()V

    #@6
    .line 613
    return-void
.end method

.method protected onDetachedFromWindow()V
    .locals 1

    #@0
    .prologue
    .line 397
    invoke-super {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->onDetachedFromWindow()V

    #@3
    .line 399
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@5
    if-eqz v0, :cond_0

    #@7
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@9
    invoke-interface {v0}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->isShowing()Z

    #@c
    move-result v0

    #@d
    if-eqz v0, :cond_0

    #@f
    .line 400
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@11
    invoke-interface {v0}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->dismiss()V

    #@14
    .line 402
    :cond_0
    return-void
.end method

.method protected onLayout(ZIIII)V
    .locals 2
    .param p1, "changed"    # Z
    .param p2, "l"    # I
    .param p3, "t"    # I
    .param p4, "r"    # I
    .param p5, "b"    # I

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 447
    invoke-super/range {p0 .. p5}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->onLayout(ZIIII)V

    #@4
    .line 448
    const/4 v0, 0x1

    #@5
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mInLayout:Z

    #@7
    .line 449
    invoke-virtual {p0, v1, v1}, Landroid/support/v7/internal/widget/SpinnerCompat;->layout(IZ)V

    #@a
    .line 450
    iput-boolean v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mInLayout:Z

    #@c
    .line 451
    return-void
.end method

.method protected onMeasure(II)V
    .locals 3
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 430
    invoke-super {p0, p1, p2}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->onMeasure(II)V

    #@3
    .line 431
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@5
    if-eqz v1, :cond_0

    #@7
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@a
    move-result v1

    #@b
    const/high16 v2, -0x80000000

    #@d
    if-ne v1, v2, :cond_0

    #@f
    .line 432
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getMeasuredWidth()I

    #@12
    move-result v0

    #@13
    .line 433
    .local v0, "measuredWidth":I
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getAdapter()Landroid/widget/SpinnerAdapter;

    #@16
    move-result-object v1

    #@17
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getBackground()Landroid/graphics/drawable/Drawable;

    #@1a
    move-result-object v2

    #@1b
    invoke-virtual {p0, v1, v2}, Landroid/support/v7/internal/widget/SpinnerCompat;->measureContentWidth(Landroid/widget/SpinnerAdapter;Landroid/graphics/drawable/Drawable;)I

    #@1e
    move-result v1

    #@1f
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@22
    move-result v1

    #@23
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@26
    move-result v2

    #@27
    invoke-static {v1, v2}, Ljava/lang/Math;->min(II)I

    #@2a
    move-result v1

    #@2b
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getMeasuredHeight()I

    #@2e
    move-result v2

    #@2f
    invoke-virtual {p0, v1, v2}, Landroid/support/v7/internal/widget/SpinnerCompat;->setMeasuredDimension(II)V

    #@32
    .line 438
    .end local v0    # "measuredWidth":I
    :cond_0
    return-void
.end method

.method public onRestoreInstanceState(Landroid/os/Parcelable;)V
    .locals 4
    .param p1, "state"    # Landroid/os/Parcelable;

    #@0
    .prologue
    .line 691
    move-object v1, p1

    #@1
    check-cast v1, Landroid/support/v7/internal/widget/SpinnerCompat$SavedState;

    #@3
    .line 693
    .local v1, "ss":Landroid/support/v7/internal/widget/SpinnerCompat$SavedState;
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/SpinnerCompat$SavedState;->getSuperState()Landroid/os/Parcelable;

    #@6
    move-result-object v3

    #@7
    invoke-super {p0, v3}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->onRestoreInstanceState(Landroid/os/Parcelable;)V

    #@a
    .line 695
    iget-boolean v3, v1, Landroid/support/v7/internal/widget/SpinnerCompat$SavedState;->showDropdown:Z

    #@c
    if-eqz v3, :cond_0

    #@e
    .line 696
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getViewTreeObserver()Landroid/view/ViewTreeObserver;

    #@11
    move-result-object v2

    #@12
    .line 697
    .local v2, "vto":Landroid/view/ViewTreeObserver;
    if-eqz v2, :cond_0

    #@14
    .line 698
    new-instance v0, Landroid/support/v7/internal/widget/SpinnerCompat$2;

    #@16
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/SpinnerCompat$2;-><init>(Landroid/support/v7/internal/widget/SpinnerCompat;)V

    #@19
    .line 711
    .local v0, "listener":Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;
    invoke-virtual {v2, v0}, Landroid/view/ViewTreeObserver;->addOnGlobalLayoutListener(Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;)V

    #@1c
    .line 714
    .end local v0    # "listener":Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;
    .end local v2    # "vto":Landroid/view/ViewTreeObserver;
    :cond_0
    return-void
.end method

.method public onSaveInstanceState()Landroid/os/Parcelable;
    .locals 2

    #@0
    .prologue
    .line 684
    new-instance v0, Landroid/support/v7/internal/widget/SpinnerCompat$SavedState;

    #@2
    invoke-super {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->onSaveInstanceState()Landroid/os/Parcelable;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1}, Landroid/support/v7/internal/widget/SpinnerCompat$SavedState;-><init>(Landroid/os/Parcelable;)V

    #@9
    .line 685
    .local v0, "ss":Landroid/support/v7/internal/widget/SpinnerCompat$SavedState;
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@b
    if-eqz v1, :cond_0

    #@d
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@f
    invoke-interface {v1}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->isShowing()Z

    #@12
    move-result v1

    #@13
    if-eqz v1, :cond_0

    #@15
    const/4 v1, 0x1

    #@16
    :goto_0
    iput-boolean v1, v0, Landroid/support/v7/internal/widget/SpinnerCompat$SavedState;->showDropdown:Z

    #@18
    .line 686
    return-object v0

    #@19
    .line 685
    :cond_0
    const/4 v1, 0x0

    #@1a
    goto :goto_0
.end method

.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 1
    .param p1, "event"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 421
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mForwardingListener:Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mForwardingListener:Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;

    #@6
    invoke-virtual {v0, p0, p1}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->onTouch(Landroid/view/View;Landroid/view/MotionEvent;)Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    .line 422
    const/4 v0, 0x1

    #@d
    .line 425
    :goto_0
    return v0

    #@e
    :cond_0
    invoke-super {p0, p1}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->onTouchEvent(Landroid/view/MotionEvent;)Z

    #@11
    move-result v0

    #@12
    goto :goto_0
.end method

.method public performClick()Z
    .locals 2

    #@0
    .prologue
    .line 597
    invoke-super {p0}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->performClick()Z

    #@3
    move-result v0

    #@4
    .line 599
    .local v0, "handled":Z
    if-nez v0, :cond_0

    #@6
    .line 600
    const/4 v0, 0x1

    #@7
    .line 602
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@9
    invoke-interface {v1}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->isShowing()Z

    #@c
    move-result v1

    #@d
    if-nez v1, :cond_0

    #@f
    .line 603
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@11
    invoke-interface {v1}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->show()V

    #@14
    .line 607
    :cond_0
    return v0
.end method

.method public bridge synthetic setAdapter(Landroid/widget/Adapter;)V
    .locals 0
    .param p1, "x0"    # Landroid/widget/Adapter;

    #@0
    .prologue
    .line 53
    check-cast p1, Landroid/widget/SpinnerAdapter;

    #@2
    .end local p1    # "x0":Landroid/widget/Adapter;
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/SpinnerCompat;->setAdapter(Landroid/widget/SpinnerAdapter;)V

    #@5
    return-void
.end method

.method public setAdapter(Landroid/widget/SpinnerAdapter;)V
    .locals 3
    .param p1, "adapter"    # Landroid/widget/SpinnerAdapter;

    #@0
    .prologue
    .line 360
    invoke-super {p0, p1}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setAdapter(Landroid/widget/SpinnerAdapter;)V

    #@3
    .line 362
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mRecycler:Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;

    #@5
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/AbsSpinnerCompat$RecycleBin;->clear()V

    #@8
    .line 364
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getContext()Landroid/content/Context;

    #@b
    move-result-object v1

    #@c
    invoke-virtual {v1}, Landroid/content/Context;->getApplicationInfo()Landroid/content/pm/ApplicationInfo;

    #@f
    move-result-object v1

    #@10
    iget v0, v1, Landroid/content/pm/ApplicationInfo;->targetSdkVersion:I

    #@12
    .line 365
    .local v0, "targetSdkVersion":I
    const/16 v1, 0x15

    #@14
    if-lt v0, v1, :cond_0

    #@16
    if-eqz p1, :cond_0

    #@18
    invoke-interface {p1}, Landroid/widget/SpinnerAdapter;->getViewTypeCount()I

    #@1b
    move-result v1

    #@1c
    const/4 v2, 0x1

    #@1d
    if-eq v1, v2, :cond_0

    #@1f
    .line 367
    new-instance v1, Ljava/lang/IllegalArgumentException;

    #@21
    const-string v2, "Spinner adapter view type count must be 1"

    #@23
    invoke-direct {v1, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@26
    throw v1

    #@27
    .line 369
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@29
    if-eqz v1, :cond_1

    #@2b
    .line 370
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@2d
    new-instance v2, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;

    #@2f
    invoke-direct {v2, p1}, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;-><init>(Landroid/widget/SpinnerAdapter;)V

    #@32
    invoke-interface {v1, v2}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->setAdapter(Landroid/widget/ListAdapter;)V

    #@35
    .line 374
    :goto_0
    return-void

    #@36
    .line 372
    :cond_1
    new-instance v1, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;

    #@38
    invoke-direct {v1, p1}, Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;-><init>(Landroid/widget/SpinnerAdapter;)V

    #@3b
    iput-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mTempAdapter:Landroid/support/v7/internal/widget/SpinnerCompat$DropDownAdapter;

    #@3d
    goto :goto_0
.end method

.method public setDropDownHorizontalOffset(I)V
    .locals 1
    .param p1, "pixels"    # I

    #@0
    .prologue
    .line 288
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->setHorizontalOffset(I)V

    #@5
    .line 289
    return-void
.end method

.method public setDropDownVerticalOffset(I)V
    .locals 1
    .param p1, "pixels"    # I

    #@0
    .prologue
    .line 268
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->setVerticalOffset(I)V

    #@5
    .line 269
    return-void
.end method

.method public setDropDownWidth(I)V
    .locals 2
    .param p1, "pixels"    # I

    #@0
    .prologue
    .line 312
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@2
    instance-of v0, v0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;

    #@4
    if-nez v0, :cond_0

    #@6
    .line 313
    const-string v0, "Spinner"

    #@8
    const-string v1, "Cannot set dropdown width for MODE_DIALOG, ignoring"

    #@a
    invoke-static {v0, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@d
    .line 317
    :goto_0
    return-void

    #@e
    .line 316
    :cond_0
    iput p1, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mDropDownWidth:I

    #@10
    goto :goto_0
.end method

.method public setEnabled(Z)V
    .locals 3
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 333
    invoke-super {p0, p1}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setEnabled(Z)V

    #@3
    .line 334
    iget-boolean v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mDisableChildrenWhenDisabled:Z

    #@5
    if-eqz v2, :cond_0

    #@7
    .line 335
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getChildCount()I

    #@a
    move-result v0

    #@b
    .line 336
    .local v0, "count":I
    const/4 v1, 0x0

    #@c
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_0

    #@e
    .line 337
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/SpinnerCompat;->getChildAt(I)Landroid/view/View;

    #@11
    move-result-object v2

    #@12
    invoke-virtual {v2, p1}, Landroid/view/View;->setEnabled(Z)V

    #@15
    .line 336
    add-int/lit8 v1, v1, 0x1

    #@17
    goto :goto_0

    #@18
    .line 340
    .end local v0    # "count":I
    .end local v1    # "i":I
    :cond_0
    return-void
.end method

.method public setGravity(I)V
    .locals 1
    .param p1, "gravity"    # I

    #@0
    .prologue
    .line 349
    iget v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mGravity:I

    #@2
    if-eq v0, p1, :cond_1

    #@4
    .line 350
    and-int/lit8 v0, p1, 0x7

    #@6
    if-nez v0, :cond_0

    #@8
    .line 351
    const v0, 0x800003

    #@b
    or-int/2addr p1, v0

    #@c
    .line 353
    :cond_0
    iput p1, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mGravity:I

    #@e
    .line 354
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->requestLayout()V

    #@11
    .line 356
    :cond_1
    return-void
.end method

.method public setOnItemClickListener(Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;)V
    .locals 2
    .param p1, "l"    # Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;

    #@0
    .prologue
    .line 412
    new-instance v0, Ljava/lang/RuntimeException;

    #@2
    const-string v1, "setOnItemClickListener cannot be used with a spinner."

    #@4
    invoke-direct {v0, v1}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method setOnItemClickListenerInt(Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;)V
    .locals 0
    .param p1, "l"    # Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;

    #@0
    .prologue
    .line 416
    invoke-super {p0, p1}, Landroid/support/v7/internal/widget/AbsSpinnerCompat;->setOnItemClickListener(Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;)V

    #@3
    .line 417
    return-void
.end method

.method public setPopupBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 2
    .param p1, "background"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 234
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@2
    instance-of v0, v0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;

    #@4
    if-nez v0, :cond_0

    #@6
    .line 235
    const-string v0, "Spinner"

    #@8
    const-string v1, "setPopupBackgroundDrawable: incompatible spinner mode; ignoring..."

    #@a
    invoke-static {v0, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@d
    .line 239
    :goto_0
    return-void

    #@e
    .line 238
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@10
    check-cast v0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;

    #@12
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@15
    goto :goto_0
.end method

.method public setPopupBackgroundResource(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 248
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/SpinnerCompat;->setPopupBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@9
    .line 249
    return-void
.end method

.method public setPrompt(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "prompt"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 620
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat;->mPopup:Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;

    #@2
    invoke-interface {v0, p1}, Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;->setPromptText(Ljava/lang/CharSequence;)V

    #@5
    .line 621
    return-void
.end method

.method public setPromptId(I)V
    .locals 1
    .param p1, "promptId"    # I

    #@0
    .prologue
    .line 628
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getContext()Landroid/content/Context;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/content/Context;->getText(I)Ljava/lang/CharSequence;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/SpinnerCompat;->setPrompt(Ljava/lang/CharSequence;)V

    #@b
    .line 629
    return-void
.end method
