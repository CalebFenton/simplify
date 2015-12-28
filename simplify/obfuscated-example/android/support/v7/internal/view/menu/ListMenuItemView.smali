.class public Landroid/support/v7/internal/view/menu/ListMenuItemView;
.super Landroid/widget/LinearLayout;
.source "ListMenuItemView.java"

# interfaces
.implements Landroid/support/v7/internal/view/menu/MenuView$ItemView;


# static fields
.field private static final TAG:Ljava/lang/String; = "ListMenuItemView"


# instance fields
.field private mBackground:Landroid/graphics/drawable/Drawable;

.field private mCheckBox:Landroid/widget/CheckBox;

.field private mContext:Landroid/content/Context;

.field private mForceShowIcon:Z

.field private mIconView:Landroid/widget/ImageView;

.field private mInflater:Landroid/view/LayoutInflater;

.field private mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

.field private mMenuType:I

.field private mPreserveIconSpacing:Z

.field private mRadioButton:Landroid/widget/RadioButton;

.field private mShortcutView:Landroid/widget/TextView;

.field private mTextAppearance:I

.field private mTextAppearanceContext:Landroid/content/Context;

.field private mTitleView:Landroid/widget/TextView;


# direct methods
.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 80
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 81
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyle"    # I

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 63
    invoke-direct {p0, p1, p2}, Landroid/widget/LinearLayout;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 64
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mContext:Landroid/content/Context;

    #@6
    .line 66
    sget-object v1, Landroid/support/v7/appcompat/R$styleable;->MenuView:[I

    #@8
    invoke-virtual {p1, p2, v1, p3, v3}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;

    #@b
    move-result-object v0

    #@c
    .line 69
    .local v0, "a":Landroid/content/res/TypedArray;
    sget v1, Landroid/support/v7/appcompat/R$styleable;->MenuView_android_itemBackground:I

    #@e
    invoke-virtual {v0, v1}, Landroid/content/res/TypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@11
    move-result-object v1

    #@12
    iput-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mBackground:Landroid/graphics/drawable/Drawable;

    #@14
    .line 70
    sget v1, Landroid/support/v7/appcompat/R$styleable;->MenuView_android_itemTextAppearance:I

    #@16
    const/4 v2, -0x1

    #@17
    invoke-virtual {v0, v1, v2}, Landroid/content/res/TypedArray;->getResourceId(II)I

    #@1a
    move-result v1

    #@1b
    iput v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mTextAppearance:I

    #@1d
    .line 72
    sget v1, Landroid/support/v7/appcompat/R$styleable;->MenuView_preserveIconSpacing:I

    #@1f
    invoke-virtual {v0, v1, v3}, Landroid/content/res/TypedArray;->getBoolean(IZ)Z

    #@22
    move-result v1

    #@23
    iput-boolean v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mPreserveIconSpacing:Z

    #@25
    .line 74
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mTextAppearanceContext:Landroid/content/Context;

    #@27
    .line 76
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@2a
    .line 77
    return-void
.end method

.method private getInflater()Landroid/view/LayoutInflater;
    .locals 1

    #@0
    .prologue
    .line 276
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mInflater:Landroid/view/LayoutInflater;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 277
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mContext:Landroid/content/Context;

    #@6
    invoke-static {v0}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@9
    move-result-object v0

    #@a
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mInflater:Landroid/view/LayoutInflater;

    #@c
    .line 279
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mInflater:Landroid/view/LayoutInflater;

    #@e
    return-object v0
.end method

.method private insertCheckBox()V
    .locals 3

    #@0
    .prologue
    .line 260
    invoke-direct {p0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->getInflater()Landroid/view/LayoutInflater;

    #@3
    move-result-object v0

    #@4
    .line 261
    .local v0, "inflater":Landroid/view/LayoutInflater;
    sget v1, Landroid/support/v7/appcompat/R$layout;->abc_list_menu_item_checkbox:I

    #@6
    const/4 v2, 0x0

    #@7
    invoke-virtual {v0, v1, p0, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@a
    move-result-object v1

    #@b
    check-cast v1, Landroid/widget/CheckBox;

    #@d
    iput-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mCheckBox:Landroid/widget/CheckBox;

    #@f
    .line 264
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mCheckBox:Landroid/widget/CheckBox;

    #@11
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->addView(Landroid/view/View;)V

    #@14
    .line 265
    return-void
.end method

.method private insertIconView()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 245
    invoke-direct {p0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->getInflater()Landroid/view/LayoutInflater;

    #@4
    move-result-object v0

    #@5
    .line 246
    .local v0, "inflater":Landroid/view/LayoutInflater;
    sget v1, Landroid/support/v7/appcompat/R$layout;->abc_list_menu_item_icon:I

    #@7
    invoke-virtual {v0, v1, p0, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@a
    move-result-object v1

    #@b
    check-cast v1, Landroid/widget/ImageView;

    #@d
    iput-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mIconView:Landroid/widget/ImageView;

    #@f
    .line 248
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mIconView:Landroid/widget/ImageView;

    #@11
    invoke-virtual {p0, v1, v2}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->addView(Landroid/view/View;I)V

    #@14
    .line 249
    return-void
.end method

.method private insertRadioButton()V
    .locals 3

    #@0
    .prologue
    .line 252
    invoke-direct {p0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->getInflater()Landroid/view/LayoutInflater;

    #@3
    move-result-object v0

    #@4
    .line 253
    .local v0, "inflater":Landroid/view/LayoutInflater;
    sget v1, Landroid/support/v7/appcompat/R$layout;->abc_list_menu_item_radio:I

    #@6
    const/4 v2, 0x0

    #@7
    invoke-virtual {v0, v1, p0, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@a
    move-result-object v1

    #@b
    check-cast v1, Landroid/widget/RadioButton;

    #@d
    iput-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mRadioButton:Landroid/widget/RadioButton;

    #@f
    .line 256
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mRadioButton:Landroid/widget/RadioButton;

    #@11
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->addView(Landroid/view/View;)V

    #@14
    .line 257
    return-void
.end method


# virtual methods
.method public getItemData()Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .locals 1

    #@0
    .prologue
    .line 126
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    return-object v0
.end method

.method public initialize(Landroid/support/v7/internal/view/menu/MenuItemImpl;I)V
    .locals 2
    .param p1, "itemData"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .param p2, "menuType"    # I

    #@0
    .prologue
    .line 99
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    .line 100
    iput p2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mMenuType:I

    #@4
    .line 102
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isVisible()Z

    #@7
    move-result v0

    #@8
    if-eqz v0, :cond_0

    #@a
    const/4 v0, 0x0

    #@b
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->setVisibility(I)V

    #@e
    .line 104
    invoke-virtual {p1, p0}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getTitleForItemView(Landroid/support/v7/internal/view/menu/MenuView$ItemView;)Ljava/lang/CharSequence;

    #@11
    move-result-object v0

    #@12
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->setTitle(Ljava/lang/CharSequence;)V

    #@15
    .line 105
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isCheckable()Z

    #@18
    move-result v0

    #@19
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->setCheckable(Z)V

    #@1c
    .line 106
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->shouldShowShortcut()Z

    #@1f
    move-result v0

    #@20
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getShortcut()C

    #@23
    move-result v1

    #@24
    invoke-virtual {p0, v0, v1}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->setShortcut(ZC)V

    #@27
    .line 107
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getIcon()Landroid/graphics/drawable/Drawable;

    #@2a
    move-result-object v0

    #@2b
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->setIcon(Landroid/graphics/drawable/Drawable;)V

    #@2e
    .line 108
    invoke-virtual {p1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isEnabled()Z

    #@31
    move-result v0

    #@32
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->setEnabled(Z)V

    #@35
    .line 109
    return-void

    #@36
    .line 102
    :cond_0
    const/16 v0, 0x8

    #@38
    goto :goto_0
.end method

.method protected onFinishInflate()V
    .locals 3

    #@0
    .prologue
    .line 85
    invoke-super {p0}, Landroid/widget/LinearLayout;->onFinishInflate()V

    #@3
    .line 87
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mBackground:Landroid/graphics/drawable/Drawable;

    #@5
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@8
    .line 89
    sget v0, Landroid/support/v7/appcompat/R$id;->title:I

    #@a
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->findViewById(I)Landroid/view/View;

    #@d
    move-result-object v0

    #@e
    check-cast v0, Landroid/widget/TextView;

    #@10
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mTitleView:Landroid/widget/TextView;

    #@12
    .line 90
    iget v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mTextAppearance:I

    #@14
    const/4 v1, -0x1

    #@15
    if-eq v0, v1, :cond_0

    #@17
    .line 91
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mTitleView:Landroid/widget/TextView;

    #@19
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mTextAppearanceContext:Landroid/content/Context;

    #@1b
    iget v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mTextAppearance:I

    #@1d
    invoke-virtual {v0, v1, v2}, Landroid/widget/TextView;->setTextAppearance(Landroid/content/Context;I)V

    #@20
    .line 95
    :cond_0
    sget v0, Landroid/support/v7/appcompat/R$id;->shortcut:I

    #@22
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->findViewById(I)Landroid/view/View;

    #@25
    move-result-object v0

    #@26
    check-cast v0, Landroid/widget/TextView;

    #@28
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mShortcutView:Landroid/widget/TextView;

    #@2a
    .line 96
    return-void
.end method

.method protected onMeasure(II)V
    .locals 3
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 233
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mIconView:Landroid/widget/ImageView;

    #@2
    if-eqz v2, :cond_0

    #@4
    iget-boolean v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mPreserveIconSpacing:Z

    #@6
    if-eqz v2, :cond_0

    #@8
    .line 235
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@b
    move-result-object v1

    #@c
    .line 236
    .local v1, "lp":Landroid/view/ViewGroup$LayoutParams;
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mIconView:Landroid/widget/ImageView;

    #@e
    invoke-virtual {v2}, Landroid/widget/ImageView;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@11
    move-result-object v0

    #@12
    check-cast v0, Landroid/widget/LinearLayout$LayoutParams;

    #@14
    .line 237
    .local v0, "iconLp":Landroid/widget/LinearLayout$LayoutParams;
    iget v2, v1, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@16
    if-lez v2, :cond_0

    #@18
    iget v2, v0, Landroid/widget/LinearLayout$LayoutParams;->width:I

    #@1a
    if-gtz v2, :cond_0

    #@1c
    .line 238
    iget v2, v1, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@1e
    iput v2, v0, Landroid/widget/LinearLayout$LayoutParams;->width:I

    #@20
    .line 241
    .end local v0    # "iconLp":Landroid/widget/LinearLayout$LayoutParams;
    .end local v1    # "lp":Landroid/view/ViewGroup$LayoutParams;
    :cond_0
    invoke-super {p0, p1, p2}, Landroid/widget/LinearLayout;->onMeasure(II)V

    #@23
    .line 242
    return-void
.end method

.method public prefersCondensedTitle()Z
    .locals 1

    #@0
    .prologue
    .line 268
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public setCheckable(Z)V
    .locals 5
    .param p1, "checkable"    # Z

    #@0
    .prologue
    const/16 v3, 0x8

    #@2
    .line 130
    if-nez p1, :cond_1

    #@4
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mRadioButton:Landroid/widget/RadioButton;

    #@6
    if-nez v4, :cond_1

    #@8
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mCheckBox:Landroid/widget/CheckBox;

    #@a
    if-nez v4, :cond_1

    #@c
    .line 173
    :cond_0
    :goto_0
    return-void

    #@d
    .line 139
    :cond_1
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@f
    invoke-virtual {v4}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isExclusiveCheckable()Z

    #@12
    move-result v4

    #@13
    if-eqz v4, :cond_4

    #@15
    .line 140
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mRadioButton:Landroid/widget/RadioButton;

    #@17
    if-nez v4, :cond_2

    #@19
    .line 141
    invoke-direct {p0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->insertRadioButton()V

    #@1c
    .line 143
    :cond_2
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mRadioButton:Landroid/widget/RadioButton;

    #@1e
    .line 144
    .local v0, "compoundButton":Landroid/widget/CompoundButton;
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mCheckBox:Landroid/widget/CheckBox;

    #@20
    .line 153
    .local v2, "otherCompoundButton":Landroid/widget/CompoundButton;
    :goto_1
    if-eqz p1, :cond_7

    #@22
    .line 154
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@24
    invoke-virtual {v4}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isChecked()Z

    #@27
    move-result v4

    #@28
    invoke-virtual {v0, v4}, Landroid/widget/CompoundButton;->setChecked(Z)V

    #@2b
    .line 156
    if-eqz p1, :cond_6

    #@2d
    const/4 v1, 0x0

    #@2e
    .line 157
    .local v1, "newVisibility":I
    :goto_2
    invoke-virtual {v0}, Landroid/widget/CompoundButton;->getVisibility()I

    #@31
    move-result v4

    #@32
    if-eq v4, v1, :cond_3

    #@34
    .line 158
    invoke-virtual {v0, v1}, Landroid/widget/CompoundButton;->setVisibility(I)V

    #@37
    .line 162
    :cond_3
    if-eqz v2, :cond_0

    #@39
    invoke-virtual {v2}, Landroid/widget/CompoundButton;->getVisibility()I

    #@3c
    move-result v4

    #@3d
    if-eq v4, v3, :cond_0

    #@3f
    .line 163
    invoke-virtual {v2, v3}, Landroid/widget/CompoundButton;->setVisibility(I)V

    #@42
    goto :goto_0

    #@43
    .line 146
    .end local v0    # "compoundButton":Landroid/widget/CompoundButton;
    .end local v1    # "newVisibility":I
    .end local v2    # "otherCompoundButton":Landroid/widget/CompoundButton;
    :cond_4
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mCheckBox:Landroid/widget/CheckBox;

    #@45
    if-nez v4, :cond_5

    #@47
    .line 147
    invoke-direct {p0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->insertCheckBox()V

    #@4a
    .line 149
    :cond_5
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mCheckBox:Landroid/widget/CheckBox;

    #@4c
    .line 150
    .restart local v0    # "compoundButton":Landroid/widget/CompoundButton;
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mRadioButton:Landroid/widget/RadioButton;

    #@4e
    .restart local v2    # "otherCompoundButton":Landroid/widget/CompoundButton;
    goto :goto_1

    #@4f
    :cond_6
    move v1, v3

    #@50
    .line 156
    goto :goto_2

    #@51
    .line 166
    :cond_7
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mCheckBox:Landroid/widget/CheckBox;

    #@53
    if-eqz v4, :cond_8

    #@55
    .line 167
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mCheckBox:Landroid/widget/CheckBox;

    #@57
    invoke-virtual {v4, v3}, Landroid/widget/CheckBox;->setVisibility(I)V

    #@5a
    .line 169
    :cond_8
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mRadioButton:Landroid/widget/RadioButton;

    #@5c
    if-eqz v4, :cond_0

    #@5e
    .line 170
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mRadioButton:Landroid/widget/RadioButton;

    #@60
    invoke-virtual {v4, v3}, Landroid/widget/RadioButton;->setVisibility(I)V

    #@63
    goto :goto_0
.end method

.method public setChecked(Z)V
    .locals 2
    .param p1, "checked"    # Z

    #@0
    .prologue
    .line 178
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isExclusiveCheckable()Z

    #@5
    move-result v1

    #@6
    if-eqz v1, :cond_1

    #@8
    .line 179
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mRadioButton:Landroid/widget/RadioButton;

    #@a
    if-nez v1, :cond_0

    #@c
    .line 180
    invoke-direct {p0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->insertRadioButton()V

    #@f
    .line 182
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mRadioButton:Landroid/widget/RadioButton;

    #@11
    .line 190
    .local v0, "compoundButton":Landroid/widget/CompoundButton;
    :goto_0
    invoke-virtual {v0, p1}, Landroid/widget/CompoundButton;->setChecked(Z)V

    #@14
    .line 191
    return-void

    #@15
    .line 184
    .end local v0    # "compoundButton":Landroid/widget/CompoundButton;
    :cond_1
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mCheckBox:Landroid/widget/CheckBox;

    #@17
    if-nez v1, :cond_2

    #@19
    .line 185
    invoke-direct {p0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->insertCheckBox()V

    #@1c
    .line 187
    :cond_2
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mCheckBox:Landroid/widget/CheckBox;

    #@1e
    .restart local v0    # "compoundButton":Landroid/widget/CompoundButton;
    goto :goto_0
.end method

.method public setForceShowIcon(Z)V
    .locals 0
    .param p1, "forceShow"    # Z

    #@0
    .prologue
    .line 112
    iput-boolean p1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mForceShowIcon:Z

    #@2
    iput-boolean p1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mPreserveIconSpacing:Z

    #@4
    .line 113
    return-void
.end method

.method public setIcon(Landroid/graphics/drawable/Drawable;)V
    .locals 3
    .param p1, "icon"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 207
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@3
    invoke-virtual {v2}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->shouldShowIcon()Z

    #@6
    move-result v2

    #@7
    if-nez v2, :cond_0

    #@9
    iget-boolean v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mForceShowIcon:Z

    #@b
    if-eqz v2, :cond_2

    #@d
    :cond_0
    const/4 v0, 0x1

    #@e
    .line 208
    .local v0, "showIcon":Z
    :goto_0
    if-nez v0, :cond_3

    #@10
    iget-boolean v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mPreserveIconSpacing:Z

    #@12
    if-nez v2, :cond_3

    #@14
    .line 229
    .end local p1    # "icon":Landroid/graphics/drawable/Drawable;
    :cond_1
    :goto_1
    return-void

    #@15
    .end local v0    # "showIcon":Z
    .restart local p1    # "icon":Landroid/graphics/drawable/Drawable;
    :cond_2
    move v0, v1

    #@16
    .line 207
    goto :goto_0

    #@17
    .line 212
    .restart local v0    # "showIcon":Z
    :cond_3
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mIconView:Landroid/widget/ImageView;

    #@19
    if-nez v2, :cond_4

    #@1b
    if-nez p1, :cond_4

    #@1d
    iget-boolean v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mPreserveIconSpacing:Z

    #@1f
    if-eqz v2, :cond_1

    #@21
    .line 216
    :cond_4
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mIconView:Landroid/widget/ImageView;

    #@23
    if-nez v2, :cond_5

    #@25
    .line 217
    invoke-direct {p0}, Landroid/support/v7/internal/view/menu/ListMenuItemView;->insertIconView()V

    #@28
    .line 220
    :cond_5
    if-nez p1, :cond_6

    #@2a
    iget-boolean v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mPreserveIconSpacing:Z

    #@2c
    if-eqz v2, :cond_8

    #@2e
    .line 221
    :cond_6
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mIconView:Landroid/widget/ImageView;

    #@30
    if-eqz v0, :cond_7

    #@32
    .end local p1    # "icon":Landroid/graphics/drawable/Drawable;
    :goto_2
    invoke-virtual {v2, p1}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@35
    .line 223
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mIconView:Landroid/widget/ImageView;

    #@37
    invoke-virtual {v2}, Landroid/widget/ImageView;->getVisibility()I

    #@3a
    move-result v2

    #@3b
    if-eqz v2, :cond_1

    #@3d
    .line 224
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mIconView:Landroid/widget/ImageView;

    #@3f
    invoke-virtual {v2, v1}, Landroid/widget/ImageView;->setVisibility(I)V

    #@42
    goto :goto_1

    #@43
    .line 221
    .restart local p1    # "icon":Landroid/graphics/drawable/Drawable;
    :cond_7
    const/4 p1, 0x0

    #@44
    goto :goto_2

    #@45
    .line 227
    :cond_8
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mIconView:Landroid/widget/ImageView;

    #@47
    const/16 v2, 0x8

    #@49
    invoke-virtual {v1, v2}, Landroid/widget/ImageView;->setVisibility(I)V

    #@4c
    goto :goto_1
.end method

.method public setShortcut(ZC)V
    .locals 3
    .param p1, "showShortcut"    # Z
    .param p2, "shortcutKey"    # C

    #@0
    .prologue
    .line 194
    if-eqz p1, :cond_2

    #@2
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@4
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->shouldShowShortcut()Z

    #@7
    move-result v1

    #@8
    if-eqz v1, :cond_2

    #@a
    const/4 v0, 0x0

    #@b
    .line 197
    .local v0, "newVisibility":I
    :goto_0
    if-nez v0, :cond_0

    #@d
    .line 198
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mShortcutView:Landroid/widget/TextView;

    #@f
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mItemData:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@11
    invoke-virtual {v2}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getShortcutLabel()Ljava/lang/String;

    #@14
    move-result-object v2

    #@15
    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@18
    .line 201
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mShortcutView:Landroid/widget/TextView;

    #@1a
    invoke-virtual {v1}, Landroid/widget/TextView;->getVisibility()I

    #@1d
    move-result v1

    #@1e
    if-eq v1, v0, :cond_1

    #@20
    .line 202
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mShortcutView:Landroid/widget/TextView;

    #@22
    invoke-virtual {v1, v0}, Landroid/widget/TextView;->setVisibility(I)V

    #@25
    .line 204
    :cond_1
    return-void

    #@26
    .line 194
    .end local v0    # "newVisibility":I
    :cond_2
    const/16 v0, 0x8

    #@28
    goto :goto_0
.end method

.method public setTitle(Ljava/lang/CharSequence;)V
    .locals 2
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    const/16 v1, 0x8

    #@2
    .line 116
    if-eqz p1, :cond_1

    #@4
    .line 117
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mTitleView:Landroid/widget/TextView;

    #@6
    invoke-virtual {v0, p1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@9
    .line 119
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mTitleView:Landroid/widget/TextView;

    #@b
    invoke-virtual {v0}, Landroid/widget/TextView;->getVisibility()I

    #@e
    move-result v0

    #@f
    if-eqz v0, :cond_0

    #@11
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mTitleView:Landroid/widget/TextView;

    #@13
    const/4 v1, 0x0

    #@14
    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setVisibility(I)V

    #@17
    .line 123
    :cond_0
    :goto_0
    return-void

    #@18
    .line 121
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mTitleView:Landroid/widget/TextView;

    #@1a
    invoke-virtual {v0}, Landroid/widget/TextView;->getVisibility()I

    #@1d
    move-result v0

    #@1e
    if-eq v0, v1, :cond_0

    #@20
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mTitleView:Landroid/widget/TextView;

    #@22
    invoke-virtual {v0, v1}, Landroid/widget/TextView;->setVisibility(I)V

    #@25
    goto :goto_0
.end method

.method public showsIcon()Z
    .locals 1

    #@0
    .prologue
    .line 272
    iget-boolean v0, p0, Landroid/support/v7/internal/view/menu/ListMenuItemView;->mForceShowIcon:Z

    #@2
    return v0
.end method
