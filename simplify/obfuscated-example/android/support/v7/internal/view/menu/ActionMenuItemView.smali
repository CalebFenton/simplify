.class public Landroid/support/v7/internal/view/menu/ActionMenuItemView;
.super Landroid/support/v7/internal/widget/CompatTextView;
.source "ActionMenuItemView.java"

# interfaces
.implements Landroid/support/v7/internal/view/menu/MenuView$ItemView;
.implements Landroid/support/v7/widget/ActionMenuView$ActionMenuChildView;
.implements Landroid/view/View$OnClickListener;
.implements Landroid/view/View$OnLongClickListener;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/view/menu/ActionMenuItemView$PopupCallback;,
        Landroid/support/v7/internal/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;
    }
.end annotation


# static fields
.field private static final MAX_ICON_SIZE:I = 0x20

.field private static final TAG:Ljava/lang/String; = "ActionMenuItemView"


# instance fields
.field private mAllowTextWithIcon:Z

.field private mExpandedFormat:Z

.field private mForwardingListener:Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;

.field private mIcon:Landroid/graphics/drawable/Drawable;

.field private mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

.field private mItemInvoker:Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;

.field private mMaxIconSize:I

.field private mMinWidth:I

.field private mPopupCallback:Landroid/support/v7/internal/view/menu/ActionMenuItemView$PopupCallback;

.field private mSavedPaddingLeft:I

.field private mTitle:Ljava/lang/CharSequence;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 68
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 69
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 72
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 73
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 5
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyle"    # I

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    .line 76
    invoke-direct {p0, p1, p2, p3}, Landroid/support/v7/internal/widget/CompatTextView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 77
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@7
    move-result-object v2

    #@8
    .line 78
    .local v2, "res":Landroid/content/res/Resources;
    sget v3, Landroid/support/v7/appcompat/R$bool;->abc_config_allowActionMenuItemTextWithIcon:I

    #@a
    invoke-virtual {v2, v3}, Landroid/content/res/Resources;->getBoolean(I)Z

    #@d
    move-result v3

    #@e
    iput-boolean v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mAllowTextWithIcon:Z

    #@10
    .line 80
    sget-object v3, Landroid/support/v7/appcompat/R$styleable;->ActionMenuItemView:[I

    #@12
    invoke-virtual {p1, p2, v3, p3, v4}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;

    #@15
    move-result-object v0

    #@16
    .line 82
    .local v0, "a":Landroid/content/res/TypedArray;
    sget v3, Landroid/support/v7/appcompat/R$styleable;->ActionMenuItemView_android_minWidth:I

    #@18
    invoke-virtual {v0, v3, v4}, Landroid/content/res/TypedArray;->getDimensionPixelSize(II)I

    #@1b
    move-result v3

    #@1c
    iput v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mMinWidth:I

    #@1e
    .line 84
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@21
    .line 86
    invoke-virtual {v2}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@24
    move-result-object v3

    #@25
    iget v1, v3, Landroid/util/DisplayMetrics;->density:F

    #@27
    .line 87
    .local v1, "density":F
    const/high16 v3, 0x42000000    # 32.0f

    #@29
    mul-float/2addr v3, v1

    #@2a
    const/high16 v4, 0x3f000000    # 0.5f

    #@2c
    add-float/2addr v3, v4

    #@2d
    float-to-int v3, v3

    #@2e
    iput v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mMaxIconSize:I

    #@30
    .line 89
    invoke-virtual {p0, p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    #@33
    .line 90
    invoke-virtual {p0, p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->setOnLongClickListener(Landroid/view/View$OnLongClickListener;)V

    #@36
    .line 92
    new-instance v3, Landroid/support/v7/internal/text/AllCapsTransformationMethod;

    #@38
    invoke-direct {v3, p1}, Landroid/support/v7/internal/text/AllCapsTransformationMethod;-><init>(Landroid/content/Context;)V

    #@3b
    invoke-virtual {p0, v3}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->setTransformationMethod(Landroid/text/method/TransformationMethod;)V

    #@3e
    .line 94
    const/4 v3, -0x1

    #@3f
    iput v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mSavedPaddingLeft:I

    #@41
    .line 95
    return-void
.end method

.method static synthetic access$000(Landroid/support/v7/internal/view/menu/ActionMenuItemView;)Landroid/support/v7/internal/view/menu/ActionMenuItemView$PopupCallback;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@0
    .prologue
    .line 46
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mPopupCallback:Landroid/support/v7/internal/view/menu/ActionMenuItemView$PopupCallback;

    #@2
    return-object v0
.end method

.method static synthetic access$100(Landroid/support/v7/internal/view/menu/ActionMenuItemView;)Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@0
    .prologue
    .line 46
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemInvoker:Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;

    #@2
    return-object v0
.end method

.method static synthetic access$200(Landroid/support/v7/internal/view/menu/ActionMenuItemView;)Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/view/menu/ActionMenuItemView;

    #@0
    .prologue
    .line 46
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    return-object v0
.end method

.method private updateTextButtonVisibility()V
    .locals 4

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 179
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mTitle:Ljava/lang/CharSequence;

    #@4
    invoke-static {v3}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@7
    move-result v3

    #@8
    if-nez v3, :cond_2

    #@a
    move v0, v1

    #@b
    .line 180
    .local v0, "visible":Z
    :goto_0
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mIcon:Landroid/graphics/drawable/Drawable;

    #@d
    if-eqz v3, :cond_0

    #@f
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@11
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->showsTextAsAction()Z

    #@14
    move-result v3

    #@15
    if-eqz v3, :cond_1

    #@17
    iget-boolean v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mAllowTextWithIcon:Z

    #@19
    if-nez v3, :cond_0

    #@1b
    iget-boolean v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mExpandedFormat:Z

    #@1d
    if-eqz v3, :cond_1

    #@1f
    :cond_0
    move v2, v1

    #@20
    :cond_1
    and-int/2addr v0, v2

    #@21
    .line 183
    if-eqz v0, :cond_3

    #@23
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mTitle:Ljava/lang/CharSequence;

    #@25
    :goto_1
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->setText(Ljava/lang/CharSequence;)V

    #@28
    .line 184
    return-void

    #@29
    .end local v0    # "visible":Z
    :cond_2
    move v0, v2

    #@2a
    .line 179
    goto :goto_0

    #@2b
    .line 183
    .restart local v0    # "visible":Z
    :cond_3
    const/4 v1, 0x0

    #@2c
    goto :goto_1
.end method


# virtual methods
.method public getItemData()Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .locals 1

    #@0
    .prologue
    .line 114
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    return-object v0
.end method

.method public hasText()Z
    .locals 1

    #@0
    .prologue
    .line 209
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getText()Ljava/lang/CharSequence;

    #@3
    move-result-object v0

    #@4
    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@7
    move-result v0

    #@8
    if-nez v0, :cond_0

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

.method public initialize(Landroid/support/v7/internal/view/menu/MenuItemImpl;I)V
    .locals 1
    .param p1, "itemData"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .param p2, "menuType"    # I

    #@0
    .prologue
    .line 118
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    .line 120
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getIcon()Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->setIcon(Landroid/graphics/drawable/Drawable;)V

    #@9
    .line 121
    invoke-virtual {p1, p0}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getTitleForItemView(Landroid/support/v7/internal/view/menu/MenuView$ItemView;)Ljava/lang/CharSequence;

    #@c
    move-result-object v0

    #@d
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->setTitle(Ljava/lang/CharSequence;)V

    #@10
    .line 122
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getItemId()I

    #@13
    move-result v0

    #@14
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->setId(I)V

    #@17
    .line 124
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isVisible()Z

    #@1a
    move-result v0

    #@1b
    if-eqz v0, :cond_1

    #@1d
    const/4 v0, 0x0

    #@1e
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->setVisibility(I)V

    #@21
    .line 125
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isEnabled()Z

    #@24
    move-result v0

    #@25
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->setEnabled(Z)V

    #@28
    .line 126
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->hasSubMenu()Z

    #@2b
    move-result v0

    #@2c
    if-eqz v0, :cond_0

    #@2e
    .line 127
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mForwardingListener:Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;

    #@30
    if-nez v0, :cond_0

    #@32
    .line 128
    new-instance v0, Landroid/support/v7/internal/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;

    #@34
    invoke-direct {v0, p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView$ActionMenuItemForwardingListener;-><init>(Landroid/support/v7/internal/view/menu/ActionMenuItemView;)V

    #@37
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mForwardingListener:Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;

    #@39
    .line 131
    :cond_0
    return-void

    #@3a
    .line 124
    :cond_1
    const/16 v0, 0x8

    #@3c
    goto :goto_0
.end method

.method public needsDividerAfter()Z
    .locals 1

    #@0
    .prologue
    .line 232
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->hasText()Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public needsDividerBefore()Z
    .locals 1

    #@0
    .prologue
    .line 228
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->hasText()Z

    #@3
    move-result v0

    #@4
    if-eqz v0, :cond_0

    #@6
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@8
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getIcon()Landroid/graphics/drawable/Drawable;

    #@b
    move-result-object v0

    #@c
    if-nez v0, :cond_0

    #@e
    const/4 v0, 0x1

    #@f
    :goto_0
    return v0

    #@10
    :cond_0
    const/4 v0, 0x0

    #@11
    goto :goto_0
.end method

.method public onClick(Landroid/view/View;)V
    .locals 2
    .param p1, "v"    # Landroid/view/View;

    #@0
    .prologue
    .line 144
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemInvoker:Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 145
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemInvoker:Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;

    #@6
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@8
    invoke-interface {v0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;->invokeItem(Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z

    #@b
    .line 147
    :cond_0
    return-void
.end method

.method public onConfigurationChanged(Landroid/content/res/Configuration;)V
    .locals 2
    .param p1, "newConfig"    # Landroid/content/res/Configuration;

    #@0
    .prologue
    .line 98
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x8

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 99
    invoke-super {p0, p1}, Landroid/support/v7/internal/widget/CompatTextView;->onConfigurationChanged(Landroid/content/res/Configuration;)V

    #@9
    .line 102
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getContext()Landroid/content/Context;

    #@c
    move-result-object v0

    #@d
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@10
    move-result-object v0

    #@11
    sget v1, Landroid/support/v7/appcompat/R$bool;->abc_config_allowActionMenuItemTextWithIcon:I

    #@13
    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getBoolean(I)Z

    #@16
    move-result v0

    #@17
    iput-boolean v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mAllowTextWithIcon:Z

    #@19
    .line 104
    invoke-direct {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->updateTextButtonVisibility()V

    #@1c
    .line 105
    return-void
.end method

.method public onLongClick(Landroid/view/View;)Z
    .locals 13
    .param p1, "v"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v10, 0x1

    #@1
    const/4 v9, 0x0

    #@2
    .line 237
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->hasText()Z

    #@5
    move-result v11

    #@6
    if-eqz v11, :cond_0

    #@8
    .line 265
    :goto_0
    return v9

    #@9
    .line 242
    :cond_0
    const/4 v11, 0x2

    #@a
    new-array v6, v11, [I

    #@c
    .line 243
    .local v6, "screenPos":[I
    new-instance v2, Landroid/graphics/Rect;

    #@e
    invoke-direct {v2}, Landroid/graphics/Rect;-><init>()V

    #@11
    .line 244
    .local v2, "displayFrame":Landroid/graphics/Rect;
    invoke-virtual {p0, v6}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getLocationOnScreen([I)V

    #@14
    .line 245
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getWindowVisibleDisplayFrame(Landroid/graphics/Rect;)V

    #@17
    .line 247
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getContext()Landroid/content/Context;

    #@1a
    move-result-object v1

    #@1b
    .line 248
    .local v1, "context":Landroid/content/Context;
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getWidth()I

    #@1e
    move-result v8

    #@1f
    .line 249
    .local v8, "width":I
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getHeight()I

    #@22
    move-result v3

    #@23
    .line 250
    .local v3, "height":I
    aget v11, v6, v10

    #@25
    div-int/lit8 v12, v3, 0x2

    #@27
    add-int v4, v11, v12

    #@29
    .line 251
    .local v4, "midy":I
    aget v11, v6, v9

    #@2b
    div-int/lit8 v12, v8, 0x2

    #@2d
    add-int v5, v11, v12

    #@2f
    .line 252
    .local v5, "referenceX":I
    invoke-static {p1}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@32
    move-result v11

    #@33
    if-nez v11, :cond_1

    #@35
    .line 253
    invoke-virtual {v1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@38
    move-result-object v11

    #@39
    invoke-virtual {v11}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@3c
    move-result-object v11

    #@3d
    iget v7, v11, Landroid/util/DisplayMetrics;->widthPixels:I

    #@3f
    .line 254
    .local v7, "screenWidth":I
    sub-int v5, v7, v5

    #@41
    .line 256
    .end local v7    # "screenWidth":I
    :cond_1
    iget-object v11, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@43
    invoke-virtual {v11}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getTitle()Ljava/lang/CharSequence;

    #@46
    move-result-object v11

    #@47
    invoke-static {v1, v11, v9}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    #@4a
    move-result-object v0

    #@4b
    .line 257
    .local v0, "cheatSheet":Landroid/widget/Toast;
    invoke-virtual {v2}, Landroid/graphics/Rect;->height()I

    #@4e
    move-result v11

    #@4f
    if-ge v4, v11, :cond_2

    #@51
    .line 259
    const v9, 0x800035

    #@54
    invoke-virtual {v0, v9, v5, v3}, Landroid/widget/Toast;->setGravity(III)V

    #@57
    .line 264
    :goto_1
    invoke-virtual {v0}, Landroid/widget/Toast;->show()V

    #@5a
    move v9, v10

    #@5b
    .line 265
    goto :goto_0

    #@5c
    .line 262
    :cond_2
    const/16 v11, 0x51

    #@5e
    invoke-virtual {v0, v11, v9, v3}, Landroid/widget/Toast;->setGravity(III)V

    #@61
    goto :goto_1
.end method

.method protected onMeasure(II)V
    .locals 12
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    const/high16 v11, 0x40000000    # 2.0f

    #@2
    .line 270
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->hasText()Z

    #@5
    move-result v3

    #@6
    .line 271
    .local v3, "textVisible":Z
    if-eqz v3, :cond_0

    #@8
    iget v7, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mSavedPaddingLeft:I

    #@a
    if-ltz v7, :cond_0

    #@c
    .line 272
    iget v7, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mSavedPaddingLeft:I

    #@e
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getPaddingTop()I

    #@11
    move-result v8

    #@12
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getPaddingRight()I

    #@15
    move-result v9

    #@16
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getPaddingBottom()I

    #@19
    move-result v10

    #@1a
    invoke-super {p0, v7, v8, v9, v10}, Landroid/support/v7/internal/widget/CompatTextView;->setPadding(IIII)V

    #@1d
    .line 276
    :cond_0
    invoke-super {p0, p1, p2}, Landroid/support/v7/internal/widget/CompatTextView;->onMeasure(II)V

    #@20
    .line 278
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@23
    move-result v5

    #@24
    .line 279
    .local v5, "widthMode":I
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@27
    move-result v6

    #@28
    .line 280
    .local v6, "widthSize":I
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getMeasuredWidth()I

    #@2b
    move-result v1

    #@2c
    .line 281
    .local v1, "oldMeasuredWidth":I
    const/high16 v7, -0x80000000

    #@2e
    if-ne v5, v7, :cond_3

    #@30
    iget v7, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mMinWidth:I

    #@32
    invoke-static {v6, v7}, Ljava/lang/Math;->min(II)I

    #@35
    move-result v2

    #@36
    .line 284
    .local v2, "targetWidth":I
    :goto_0
    if-eq v5, v11, :cond_1

    #@38
    iget v7, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mMinWidth:I

    #@3a
    if-lez v7, :cond_1

    #@3c
    if-ge v1, v2, :cond_1

    #@3e
    .line 286
    invoke-static {v2, v11}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@41
    move-result v7

    #@42
    invoke-super {p0, v7, p2}, Landroid/support/v7/internal/widget/CompatTextView;->onMeasure(II)V

    #@45
    .line 290
    :cond_1
    if-nez v3, :cond_2

    #@47
    iget-object v7, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mIcon:Landroid/graphics/drawable/Drawable;

    #@49
    if-eqz v7, :cond_2

    #@4b
    .line 293
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getMeasuredWidth()I

    #@4e
    move-result v4

    #@4f
    .line 294
    .local v4, "w":I
    iget-object v7, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mIcon:Landroid/graphics/drawable/Drawable;

    #@51
    invoke-virtual {v7}, Landroid/graphics/drawable/Drawable;->getBounds()Landroid/graphics/Rect;

    #@54
    move-result-object v7

    #@55
    invoke-virtual {v7}, Landroid/graphics/Rect;->width()I

    #@58
    move-result v0

    #@59
    .line 295
    .local v0, "dw":I
    sub-int v7, v4, v0

    #@5b
    div-int/lit8 v7, v7, 0x2

    #@5d
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getPaddingTop()I

    #@60
    move-result v8

    #@61
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getPaddingRight()I

    #@64
    move-result v9

    #@65
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->getPaddingBottom()I

    #@68
    move-result v10

    #@69
    invoke-super {p0, v7, v8, v9, v10}, Landroid/support/v7/internal/widget/CompatTextView;->setPadding(IIII)V

    #@6c
    .line 297
    .end local v0    # "dw":I
    .end local v4    # "w":I
    :cond_2
    return-void

    #@6d
    .line 281
    .end local v2    # "targetWidth":I
    :cond_3
    iget v2, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mMinWidth:I

    #@6f
    goto :goto_0
.end method

.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 1
    .param p1, "e"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 135
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->hasSubMenu()Z

    #@5
    move-result v0

    #@6
    if-eqz v0, :cond_0

    #@8
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mForwardingListener:Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;

    #@a
    if-eqz v0, :cond_0

    #@c
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mForwardingListener:Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;

    #@e
    invoke-virtual {v0, p0, p1}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->onTouch(Landroid/view/View;Landroid/view/MotionEvent;)Z

    #@11
    move-result v0

    #@12
    if-eqz v0, :cond_0

    #@14
    .line 137
    const/4 v0, 0x1

    #@15
    .line 139
    :goto_0
    return v0

    #@16
    :cond_0
    invoke-super {p0, p1}, Landroid/support/v7/internal/widget/CompatTextView;->onTouchEvent(Landroid/view/MotionEvent;)Z

    #@19
    move-result v0

    #@1a
    goto :goto_0
.end method

.method public prefersCondensedTitle()Z
    .locals 1

    #@0
    .prologue
    .line 158
    const/4 v0, 0x1

    #@1
    return v0
.end method

.method public setCheckable(Z)V
    .locals 0
    .param p1, "checkable"    # Z

    #@0
    .prologue
    .line 163
    return-void
.end method

.method public setChecked(Z)V
    .locals 0
    .param p1, "checked"    # Z

    #@0
    .prologue
    .line 167
    return-void
.end method

.method public setExpandedFormat(Z)V
    .locals 1
    .param p1, "expandedFormat"    # Z

    #@0
    .prologue
    .line 170
    iget-boolean v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mExpandedFormat:Z

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 171
    iput-boolean p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mExpandedFormat:Z

    #@6
    .line 172
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@8
    if-eqz v0, :cond_0

    #@a
    .line 173
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@c
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->actionFormatChanged()V

    #@f
    .line 176
    :cond_0
    return-void
.end method

.method public setIcon(Landroid/graphics/drawable/Drawable;)V
    .locals 7
    .param p1, "icon"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    const/4 v6, 0x0

    #@1
    const/4 v5, 0x0

    #@2
    .line 187
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mIcon:Landroid/graphics/drawable/Drawable;

    #@4
    .line 188
    if-eqz p1, :cond_2

    #@6
    .line 189
    invoke-virtual {p1}, Landroid/graphics/drawable/Drawable;->getIntrinsicWidth()I

    #@9
    move-result v2

    #@a
    .line 190
    .local v2, "width":I
    invoke-virtual {p1}, Landroid/graphics/drawable/Drawable;->getIntrinsicHeight()I

    #@d
    move-result v0

    #@e
    .line 191
    .local v0, "height":I
    iget v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mMaxIconSize:I

    #@10
    if-le v2, v3, :cond_0

    #@12
    .line 192
    iget v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mMaxIconSize:I

    #@14
    int-to-float v3, v3

    #@15
    int-to-float v4, v2

    #@16
    div-float v1, v3, v4

    #@18
    .line 193
    .local v1, "scale":F
    iget v2, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mMaxIconSize:I

    #@1a
    .line 194
    int-to-float v3, v0

    #@1b
    mul-float/2addr v3, v1

    #@1c
    float-to-int v0, v3

    #@1d
    .line 196
    .end local v1    # "scale":F
    :cond_0
    iget v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mMaxIconSize:I

    #@1f
    if-le v0, v3, :cond_1

    #@21
    .line 197
    iget v3, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mMaxIconSize:I

    #@23
    int-to-float v3, v3

    #@24
    int-to-float v4, v0

    #@25
    div-float v1, v3, v4

    #@27
    .line 198
    .restart local v1    # "scale":F
    iget v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mMaxIconSize:I

    #@29
    .line 199
    int-to-float v3, v2

    #@2a
    mul-float/2addr v3, v1

    #@2b
    float-to-int v2, v3

    #@2c
    .line 201
    .end local v1    # "scale":F
    :cond_1
    invoke-virtual {p1, v6, v6, v2, v0}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@2f
    .line 203
    .end local v0    # "height":I
    .end local v2    # "width":I
    :cond_2
    invoke-virtual {p0, p1, v5, v5, v5}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->setCompoundDrawables(Landroid/graphics/drawable/Drawable;Landroid/graphics/drawable/Drawable;Landroid/graphics/drawable/Drawable;Landroid/graphics/drawable/Drawable;)V

    #@32
    .line 205
    invoke-direct {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->updateTextButtonVisibility()V

    #@35
    .line 206
    return-void
.end method

.method public setItemInvoker(Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;)V
    .locals 0
    .param p1, "invoker"    # Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;

    #@0
    .prologue
    .line 150
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mItemInvoker:Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;

    #@2
    .line 151
    return-void
.end method

.method public setPadding(IIII)V
    .locals 0
    .param p1, "l"    # I
    .param p2, "t"    # I
    .param p3, "r"    # I
    .param p4, "b"    # I

    #@0
    .prologue
    .line 109
    iput p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mSavedPaddingLeft:I

    #@2
    .line 110
    invoke-super {p0, p1, p2, p3, p4}, Landroid/support/v7/internal/widget/CompatTextView;->setPadding(IIII)V

    #@5
    .line 111
    return-void
.end method

.method public setPopupCallback(Landroid/support/v7/internal/view/menu/ActionMenuItemView$PopupCallback;)V
    .locals 0
    .param p1, "popupCallback"    # Landroid/support/v7/internal/view/menu/ActionMenuItemView$PopupCallback;

    #@0
    .prologue
    .line 154
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mPopupCallback:Landroid/support/v7/internal/view/menu/ActionMenuItemView$PopupCallback;

    #@2
    .line 155
    return-void
.end method

.method public setShortcut(ZC)V
    .locals 0
    .param p1, "showShortcut"    # Z
    .param p2, "shortcutKey"    # C

    #@0
    .prologue
    .line 214
    return-void
.end method

.method public setTitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 217
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mTitle:Ljava/lang/CharSequence;

    #@2
    .line 219
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->mTitle:Ljava/lang/CharSequence;

    #@4
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->setContentDescription(Ljava/lang/CharSequence;)V

    #@7
    .line 220
    invoke-direct {p0}, Landroid/support/v7/internal/view/menu/ActionMenuItemView;->updateTextButtonVisibility()V

    #@a
    .line 221
    return-void
.end method

.method public showsIcon()Z
    .locals 1

    #@0
    .prologue
    .line 224
    const/4 v0, 0x1

    #@1
    return v0
.end method
