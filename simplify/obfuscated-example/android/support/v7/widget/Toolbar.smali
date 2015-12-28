.class public Landroid/support/v7/widget/Toolbar;
.super Landroid/view/ViewGroup;
.source "Toolbar.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;,
        Landroid/support/v7/widget/Toolbar$SavedState;,
        Landroid/support/v7/widget/Toolbar$LayoutParams;,
        Landroid/support/v7/widget/Toolbar$OnMenuItemClickListener;
    }
.end annotation


# static fields
.field private static final TAG:Ljava/lang/String; = "Toolbar"


# instance fields
.field private mActionMenuPresenterCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

.field private mButtonGravity:I

.field private mCollapseButtonView:Landroid/widget/ImageButton;

.field private mCollapseDescription:Ljava/lang/CharSequence;

.field private mCollapseIcon:Landroid/graphics/drawable/Drawable;

.field private mCollapsible:Z

.field private final mContentInsets:Landroid/support/v7/internal/widget/RtlSpacingHelper;

.field private mEatingTouch:Z

.field mExpandedActionView:Landroid/view/View;

.field private mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

.field private mGravity:I

.field private mLogoView:Landroid/widget/ImageView;

.field private mMaxButtonHeight:I

.field private mMenuBuilderCallback:Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

.field private mMenuView:Landroid/support/v7/widget/ActionMenuView;

.field private final mMenuViewItemClickListener:Landroid/support/v7/widget/ActionMenuView$OnMenuItemClickListener;

.field private mMinHeight:I

.field private mNavButtonView:Landroid/widget/ImageButton;

.field private mOnMenuItemClickListener:Landroid/support/v7/widget/Toolbar$OnMenuItemClickListener;

.field private mOuterActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

.field private mPopupContext:Landroid/content/Context;

.field private mPopupTheme:I

.field private final mShowOverflowMenuRunnable:Ljava/lang/Runnable;

.field private mSubtitleText:Ljava/lang/CharSequence;

.field private mSubtitleTextAppearance:I

.field private mSubtitleTextColor:I

.field private mSubtitleTextView:Landroid/widget/TextView;

.field private final mTempMargins:[I

.field private final mTempViews:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;"
        }
    .end annotation
.end field

.field private final mTintManager:Landroid/support/v7/internal/widget/TintManager;

.field private mTitleMarginBottom:I

.field private mTitleMarginEnd:I

.field private mTitleMarginStart:I

.field private mTitleMarginTop:I

.field private mTitleText:Ljava/lang/CharSequence;

.field private mTitleTextAppearance:I

.field private mTitleTextColor:I

.field private mTitleTextView:Landroid/widget/TextView;

.field private mWrapper:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 187
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/widget/Toolbar;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 188
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 191
    sget v0, Landroid/support/v7/appcompat/R$attr;->toolbarStyle:I

    #@2
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/widget/Toolbar;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@5
    .line 192
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 21
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyleAttr"    # I

    #@0
    .prologue
    .line 195
    invoke-static/range {p1 .. p3}, Landroid/support/v7/widget/Toolbar;->themifyContext(Landroid/content/Context;Landroid/util/AttributeSet;I)Landroid/content/Context;

    #@3
    move-result-object v18

    #@4
    move-object/from16 v0, p0

    #@6
    move-object/from16 v1, v18

    #@8
    move-object/from16 v2, p2

    #@a
    move/from16 v3, p3

    #@c
    invoke-direct {v0, v1, v2, v3}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@f
    .line 139
    new-instance v18, Landroid/support/v7/internal/widget/RtlSpacingHelper;

    #@11
    invoke-direct/range {v18 .. v18}, Landroid/support/v7/internal/widget/RtlSpacingHelper;-><init>()V

    #@14
    move-object/from16 v0, v18

    #@16
    move-object/from16 v1, p0

    #@18
    iput-object v0, v1, Landroid/support/v7/widget/Toolbar;->mContentInsets:Landroid/support/v7/internal/widget/RtlSpacingHelper;

    #@1a
    .line 141
    const v18, 0x800013

    #@1d
    move/from16 v0, v18

    #@1f
    move-object/from16 v1, p0

    #@21
    iput v0, v1, Landroid/support/v7/widget/Toolbar;->mGravity:I

    #@23
    .line 152
    new-instance v18, Ljava/util/ArrayList;

    #@25
    invoke-direct/range {v18 .. v18}, Ljava/util/ArrayList;-><init>()V

    #@28
    move-object/from16 v0, v18

    #@2a
    move-object/from16 v1, p0

    #@2c
    iput-object v0, v1, Landroid/support/v7/widget/Toolbar;->mTempViews:Ljava/util/ArrayList;

    #@2e
    .line 154
    const/16 v18, 0x2

    #@30
    move/from16 v0, v18

    #@32
    new-array v0, v0, [I

    #@34
    move-object/from16 v18, v0

    #@36
    move-object/from16 v0, v18

    #@38
    move-object/from16 v1, p0

    #@3a
    iput-object v0, v1, Landroid/support/v7/widget/Toolbar;->mTempMargins:[I

    #@3c
    .line 158
    new-instance v18, Landroid/support/v7/widget/Toolbar$1;

    #@3e
    move-object/from16 v0, v18

    #@40
    move-object/from16 v1, p0

    #@42
    invoke-direct {v0, v1}, Landroid/support/v7/widget/Toolbar$1;-><init>(Landroid/support/v7/widget/Toolbar;)V

    #@45
    move-object/from16 v0, v18

    #@47
    move-object/from16 v1, p0

    #@49
    iput-object v0, v1, Landroid/support/v7/widget/Toolbar;->mMenuViewItemClickListener:Landroid/support/v7/widget/ActionMenuView$OnMenuItemClickListener;

    #@4b
    .line 178
    new-instance v18, Landroid/support/v7/widget/Toolbar$2;

    #@4d
    move-object/from16 v0, v18

    #@4f
    move-object/from16 v1, p0

    #@51
    invoke-direct {v0, v1}, Landroid/support/v7/widget/Toolbar$2;-><init>(Landroid/support/v7/widget/Toolbar;)V

    #@54
    move-object/from16 v0, v18

    #@56
    move-object/from16 v1, p0

    #@58
    iput-object v0, v1, Landroid/support/v7/widget/Toolbar;->mShowOverflowMenuRunnable:Ljava/lang/Runnable;

    #@5a
    .line 198
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@5d
    move-result-object v18

    #@5e
    sget-object v19, Landroid/support/v7/appcompat/R$styleable;->Toolbar:[I

    #@60
    const/16 v20, 0x0

    #@62
    move-object/from16 v0, v18

    #@64
    move-object/from16 v1, p2

    #@66
    move-object/from16 v2, v19

    #@68
    move/from16 v3, p3

    #@6a
    move/from16 v4, v20

    #@6c
    invoke-static {v0, v1, v2, v3, v4}, Landroid/support/v7/internal/widget/TintTypedArray;->obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/internal/widget/TintTypedArray;

    #@6f
    move-result-object v5

    #@70
    .line 201
    .local v5, "a":Landroid/support/v7/internal/widget/TintTypedArray;
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_titleTextAppearance:I

    #@72
    const/16 v19, 0x0

    #@74
    move/from16 v0, v18

    #@76
    move/from16 v1, v19

    #@78
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@7b
    move-result v18

    #@7c
    move/from16 v0, v18

    #@7e
    move-object/from16 v1, p0

    #@80
    iput v0, v1, Landroid/support/v7/widget/Toolbar;->mTitleTextAppearance:I

    #@82
    .line 202
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_subtitleTextAppearance:I

    #@84
    const/16 v19, 0x0

    #@86
    move/from16 v0, v18

    #@88
    move/from16 v1, v19

    #@8a
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@8d
    move-result v18

    #@8e
    move/from16 v0, v18

    #@90
    move-object/from16 v1, p0

    #@92
    iput v0, v1, Landroid/support/v7/widget/Toolbar;->mSubtitleTextAppearance:I

    #@94
    .line 203
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_android_gravity:I

    #@96
    move-object/from16 v0, p0

    #@98
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mGravity:I

    #@9a
    move/from16 v19, v0

    #@9c
    move/from16 v0, v18

    #@9e
    move/from16 v1, v19

    #@a0
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getInteger(II)I

    #@a3
    move-result v18

    #@a4
    move/from16 v0, v18

    #@a6
    move-object/from16 v1, p0

    #@a8
    iput v0, v1, Landroid/support/v7/widget/Toolbar;->mGravity:I

    #@aa
    .line 204
    const/16 v18, 0x30

    #@ac
    move/from16 v0, v18

    #@ae
    move-object/from16 v1, p0

    #@b0
    iput v0, v1, Landroid/support/v7/widget/Toolbar;->mButtonGravity:I

    #@b2
    .line 205
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_titleMargins:I

    #@b4
    const/16 v19, 0x0

    #@b6
    move/from16 v0, v18

    #@b8
    move/from16 v1, v19

    #@ba
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelOffset(II)I

    #@bd
    move-result v18

    #@be
    move/from16 v0, v18

    #@c0
    move-object/from16 v1, p0

    #@c2
    iput v0, v1, Landroid/support/v7/widget/Toolbar;->mTitleMarginBottom:I

    #@c4
    move/from16 v0, v18

    #@c6
    move-object/from16 v1, p0

    #@c8
    iput v0, v1, Landroid/support/v7/widget/Toolbar;->mTitleMarginTop:I

    #@ca
    move/from16 v0, v18

    #@cc
    move-object/from16 v1, p0

    #@ce
    iput v0, v1, Landroid/support/v7/widget/Toolbar;->mTitleMarginEnd:I

    #@d0
    move/from16 v0, v18

    #@d2
    move-object/from16 v1, p0

    #@d4
    iput v0, v1, Landroid/support/v7/widget/Toolbar;->mTitleMarginStart:I

    #@d6
    .line 208
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_titleMarginStart:I

    #@d8
    const/16 v19, -0x1

    #@da
    move/from16 v0, v18

    #@dc
    move/from16 v1, v19

    #@de
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelOffset(II)I

    #@e1
    move-result v12

    #@e2
    .line 209
    .local v12, "marginStart":I
    if-ltz v12, :cond_0

    #@e4
    .line 210
    move-object/from16 v0, p0

    #@e6
    iput v12, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginStart:I

    #@e8
    .line 213
    :cond_0
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_titleMarginEnd:I

    #@ea
    const/16 v19, -0x1

    #@ec
    move/from16 v0, v18

    #@ee
    move/from16 v1, v19

    #@f0
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelOffset(II)I

    #@f3
    move-result v11

    #@f4
    .line 214
    .local v11, "marginEnd":I
    if-ltz v11, :cond_1

    #@f6
    .line 215
    move-object/from16 v0, p0

    #@f8
    iput v11, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginEnd:I

    #@fa
    .line 218
    :cond_1
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_titleMarginTop:I

    #@fc
    const/16 v19, -0x1

    #@fe
    move/from16 v0, v18

    #@100
    move/from16 v1, v19

    #@102
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelOffset(II)I

    #@105
    move-result v13

    #@106
    .line 219
    .local v13, "marginTop":I
    if-ltz v13, :cond_2

    #@108
    .line 220
    move-object/from16 v0, p0

    #@10a
    iput v13, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginTop:I

    #@10c
    .line 223
    :cond_2
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_titleMarginBottom:I

    #@10e
    const/16 v19, -0x1

    #@110
    move/from16 v0, v18

    #@112
    move/from16 v1, v19

    #@114
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelOffset(II)I

    #@117
    move-result v10

    #@118
    .line 225
    .local v10, "marginBottom":I
    if-ltz v10, :cond_3

    #@11a
    .line 226
    move-object/from16 v0, p0

    #@11c
    iput v10, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginBottom:I

    #@11e
    .line 229
    :cond_3
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_maxButtonHeight:I

    #@120
    const/16 v19, -0x1

    #@122
    move/from16 v0, v18

    #@124
    move/from16 v1, v19

    #@126
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelSize(II)I

    #@129
    move-result v18

    #@12a
    move/from16 v0, v18

    #@12c
    move-object/from16 v1, p0

    #@12e
    iput v0, v1, Landroid/support/v7/widget/Toolbar;->mMaxButtonHeight:I

    #@130
    .line 231
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_contentInsetStart:I

    #@132
    const/high16 v19, -0x80000000

    #@134
    move/from16 v0, v18

    #@136
    move/from16 v1, v19

    #@138
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelOffset(II)I

    #@13b
    move-result v9

    #@13c
    .line 234
    .local v9, "contentInsetStart":I
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_contentInsetEnd:I

    #@13e
    const/high16 v19, -0x80000000

    #@140
    move/from16 v0, v18

    #@142
    move/from16 v1, v19

    #@144
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelOffset(II)I

    #@147
    move-result v6

    #@148
    .line 237
    .local v6, "contentInsetEnd":I
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_contentInsetLeft:I

    #@14a
    const/16 v19, 0x0

    #@14c
    move/from16 v0, v18

    #@14e
    move/from16 v1, v19

    #@150
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelSize(II)I

    #@153
    move-result v7

    #@154
    .line 239
    .local v7, "contentInsetLeft":I
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_contentInsetRight:I

    #@156
    const/16 v19, 0x0

    #@158
    move/from16 v0, v18

    #@15a
    move/from16 v1, v19

    #@15c
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelSize(II)I

    #@15f
    move-result v8

    #@160
    .line 242
    .local v8, "contentInsetRight":I
    move-object/from16 v0, p0

    #@162
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mContentInsets:Landroid/support/v7/internal/widget/RtlSpacingHelper;

    #@164
    move-object/from16 v18, v0

    #@166
    move-object/from16 v0, v18

    #@168
    invoke-virtual {v0, v7, v8}, Landroid/support/v7/internal/widget/RtlSpacingHelper;->setAbsolute(II)V

    #@16b
    .line 244
    const/high16 v18, -0x80000000

    #@16d
    move/from16 v0, v18

    #@16f
    if-ne v9, v0, :cond_4

    #@171
    const/high16 v18, -0x80000000

    #@173
    move/from16 v0, v18

    #@175
    if-eq v6, v0, :cond_5

    #@177
    .line 246
    :cond_4
    move-object/from16 v0, p0

    #@179
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mContentInsets:Landroid/support/v7/internal/widget/RtlSpacingHelper;

    #@17b
    move-object/from16 v18, v0

    #@17d
    move-object/from16 v0, v18

    #@17f
    invoke-virtual {v0, v9, v6}, Landroid/support/v7/internal/widget/RtlSpacingHelper;->setRelative(II)V

    #@182
    .line 249
    :cond_5
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_collapseIcon:I

    #@184
    move/from16 v0, v18

    #@186
    invoke-virtual {v5, v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@189
    move-result-object v18

    #@18a
    move-object/from16 v0, v18

    #@18c
    move-object/from16 v1, p0

    #@18e
    iput-object v0, v1, Landroid/support/v7/widget/Toolbar;->mCollapseIcon:Landroid/graphics/drawable/Drawable;

    #@190
    .line 250
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_collapseContentDescription:I

    #@192
    move/from16 v0, v18

    #@194
    invoke-virtual {v5, v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getText(I)Ljava/lang/CharSequence;

    #@197
    move-result-object v18

    #@198
    move-object/from16 v0, v18

    #@19a
    move-object/from16 v1, p0

    #@19c
    iput-object v0, v1, Landroid/support/v7/widget/Toolbar;->mCollapseDescription:Ljava/lang/CharSequence;

    #@19e
    .line 252
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_title:I

    #@1a0
    move/from16 v0, v18

    #@1a2
    invoke-virtual {v5, v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getText(I)Ljava/lang/CharSequence;

    #@1a5
    move-result-object v17

    #@1a6
    .line 253
    .local v17, "title":Ljava/lang/CharSequence;
    invoke-static/range {v17 .. v17}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@1a9
    move-result v18

    #@1aa
    if-nez v18, :cond_6

    #@1ac
    .line 254
    move-object/from16 v0, p0

    #@1ae
    move-object/from16 v1, v17

    #@1b0
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->setTitle(Ljava/lang/CharSequence;)V

    #@1b3
    .line 257
    :cond_6
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_subtitle:I

    #@1b5
    move/from16 v0, v18

    #@1b7
    invoke-virtual {v5, v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getText(I)Ljava/lang/CharSequence;

    #@1ba
    move-result-object v16

    #@1bb
    .line 258
    .local v16, "subtitle":Ljava/lang/CharSequence;
    invoke-static/range {v16 .. v16}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@1be
    move-result v18

    #@1bf
    if-nez v18, :cond_7

    #@1c1
    .line 259
    move-object/from16 v0, p0

    #@1c3
    move-object/from16 v1, v16

    #@1c5
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->setSubtitle(Ljava/lang/CharSequence;)V

    #@1c8
    .line 262
    :cond_7
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@1cb
    move-result-object v18

    #@1cc
    move-object/from16 v0, v18

    #@1ce
    move-object/from16 v1, p0

    #@1d0
    iput-object v0, v1, Landroid/support/v7/widget/Toolbar;->mPopupContext:Landroid/content/Context;

    #@1d2
    .line 263
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_popupTheme:I

    #@1d4
    const/16 v19, 0x0

    #@1d6
    move/from16 v0, v18

    #@1d8
    move/from16 v1, v19

    #@1da
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@1dd
    move-result v18

    #@1de
    move-object/from16 v0, p0

    #@1e0
    move/from16 v1, v18

    #@1e2
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->setPopupTheme(I)V

    #@1e5
    .line 265
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_navigationIcon:I

    #@1e7
    move/from16 v0, v18

    #@1e9
    invoke-virtual {v5, v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@1ec
    move-result-object v15

    #@1ed
    .line 266
    .local v15, "navIcon":Landroid/graphics/drawable/Drawable;
    if-eqz v15, :cond_8

    #@1ef
    .line 267
    move-object/from16 v0, p0

    #@1f1
    invoke-virtual {v0, v15}, Landroid/support/v7/widget/Toolbar;->setNavigationIcon(Landroid/graphics/drawable/Drawable;)V

    #@1f4
    .line 269
    :cond_8
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_navigationContentDescription:I

    #@1f6
    move/from16 v0, v18

    #@1f8
    invoke-virtual {v5, v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getText(I)Ljava/lang/CharSequence;

    #@1fb
    move-result-object v14

    #@1fc
    .line 270
    .local v14, "navDesc":Ljava/lang/CharSequence;
    invoke-static {v14}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@1ff
    move-result v18

    #@200
    if-nez v18, :cond_9

    #@202
    .line 271
    move-object/from16 v0, p0

    #@204
    invoke-virtual {v0, v14}, Landroid/support/v7/widget/Toolbar;->setNavigationContentDescription(Ljava/lang/CharSequence;)V

    #@207
    .line 275
    :cond_9
    sget v18, Landroid/support/v7/appcompat/R$styleable;->Toolbar_android_minHeight:I

    #@209
    const/16 v19, 0x0

    #@20b
    move/from16 v0, v18

    #@20d
    move/from16 v1, v19

    #@20f
    invoke-virtual {v5, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelSize(II)I

    #@212
    move-result v18

    #@213
    move/from16 v0, v18

    #@215
    move-object/from16 v1, p0

    #@217
    iput v0, v1, Landroid/support/v7/widget/Toolbar;->mMinHeight:I

    #@219
    .line 277
    invoke-virtual {v5}, Landroid/support/v7/internal/widget/TintTypedArray;->recycle()V

    #@21c
    .line 280
    invoke-virtual {v5}, Landroid/support/v7/internal/widget/TintTypedArray;->getTintManager()Landroid/support/v7/internal/widget/TintManager;

    #@21f
    move-result-object v18

    #@220
    move-object/from16 v0, v18

    #@222
    move-object/from16 v1, p0

    #@224
    iput-object v0, v1, Landroid/support/v7/widget/Toolbar;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@226
    .line 281
    return-void
.end method

.method static synthetic access$000(Landroid/support/v7/widget/Toolbar;)Landroid/support/v7/widget/Toolbar$OnMenuItemClickListener;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/Toolbar;

    #@0
    .prologue
    .line 107
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mOnMenuItemClickListener:Landroid/support/v7/widget/Toolbar$OnMenuItemClickListener;

    #@2
    return-object v0
.end method

.method static synthetic access$200(Landroid/support/v7/widget/Toolbar;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/Toolbar;

    #@0
    .prologue
    .line 107
    invoke-direct {p0}, Landroid/support/v7/widget/Toolbar;->ensureCollapseButtonView()V

    #@3
    return-void
.end method

.method static synthetic access$300(Landroid/support/v7/widget/Toolbar;)Landroid/widget/ImageButton;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/Toolbar;

    #@0
    .prologue
    .line 107
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@2
    return-object v0
.end method

.method static synthetic access$400(Landroid/support/v7/widget/Toolbar;)I
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/Toolbar;

    #@0
    .prologue
    .line 107
    iget v0, p0, Landroid/support/v7/widget/Toolbar;->mButtonGravity:I

    #@2
    return v0
.end method

.method static synthetic access$500(Landroid/support/v7/widget/Toolbar;Z)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/Toolbar;
    .param p1, "x1"    # Z

    #@0
    .prologue
    .line 107
    invoke-direct {p0, p1}, Landroid/support/v7/widget/Toolbar;->setChildVisibilityForExpandedActionView(Z)V

    #@3
    return-void
.end method

.method private addCustomViewsWithGravity(Ljava/util/List;I)V
    .locals 7
    .param p2, "gravity"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List",
            "<",
            "Landroid/view/View;",
            ">;I)V"
        }
    .end annotation

    #@0
    .prologue
    .local p1, "views":Ljava/util/List;, "Ljava/util/List<Landroid/view/View;>;"
    const/4 v4, 0x1

    #@1
    .line 1614
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@4
    move-result v6

    #@5
    if-ne v6, v4, :cond_1

    #@7
    .line 1615
    .local v4, "isRtl":Z
    :goto_0
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getChildCount()I

    #@a
    move-result v2

    #@b
    .line 1616
    .local v2, "childCount":I
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@e
    move-result v6

    #@f
    invoke-static {p2, v6}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@12
    move-result v0

    #@13
    .line 1619
    .local v0, "absGrav":I
    invoke-interface {p1}, Ljava/util/List;->clear()V

    #@16
    .line 1621
    if-eqz v4, :cond_2

    #@18
    .line 1622
    add-int/lit8 v3, v2, -0x1

    #@1a
    .local v3, "i":I
    :goto_1
    if-ltz v3, :cond_4

    #@1c
    .line 1623
    invoke-virtual {p0, v3}, Landroid/support/v7/widget/Toolbar;->getChildAt(I)Landroid/view/View;

    #@1f
    move-result-object v1

    #@20
    .line 1624
    .local v1, "child":Landroid/view/View;
    invoke-virtual {v1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@23
    move-result-object v5

    #@24
    check-cast v5, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@26
    .line 1625
    .local v5, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    iget v6, v5, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@28
    if-nez v6, :cond_0

    #@2a
    invoke-direct {p0, v1}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@2d
    move-result v6

    #@2e
    if-eqz v6, :cond_0

    #@30
    iget v6, v5, Landroid/support/v7/widget/Toolbar$LayoutParams;->gravity:I

    #@32
    invoke-direct {p0, v6}, Landroid/support/v7/widget/Toolbar;->getChildHorizontalGravity(I)I

    #@35
    move-result v6

    #@36
    if-ne v6, v0, :cond_0

    #@38
    .line 1627
    invoke-interface {p1, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    #@3b
    .line 1622
    :cond_0
    add-int/lit8 v3, v3, -0x1

    #@3d
    goto :goto_1

    #@3e
    .line 1614
    .end local v0    # "absGrav":I
    .end local v1    # "child":Landroid/view/View;
    .end local v2    # "childCount":I
    .end local v3    # "i":I
    .end local v4    # "isRtl":Z
    .end local v5    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_1
    const/4 v4, 0x0

    #@3f
    goto :goto_0

    #@40
    .line 1631
    .restart local v0    # "absGrav":I
    .restart local v2    # "childCount":I
    .restart local v4    # "isRtl":Z
    :cond_2
    const/4 v3, 0x0

    #@41
    .restart local v3    # "i":I
    :goto_2
    if-ge v3, v2, :cond_4

    #@43
    .line 1632
    invoke-virtual {p0, v3}, Landroid/support/v7/widget/Toolbar;->getChildAt(I)Landroid/view/View;

    #@46
    move-result-object v1

    #@47
    .line 1633
    .restart local v1    # "child":Landroid/view/View;
    invoke-virtual {v1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@4a
    move-result-object v5

    #@4b
    check-cast v5, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@4d
    .line 1634
    .restart local v5    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    iget v6, v5, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@4f
    if-nez v6, :cond_3

    #@51
    invoke-direct {p0, v1}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@54
    move-result v6

    #@55
    if-eqz v6, :cond_3

    #@57
    iget v6, v5, Landroid/support/v7/widget/Toolbar$LayoutParams;->gravity:I

    #@59
    invoke-direct {p0, v6}, Landroid/support/v7/widget/Toolbar;->getChildHorizontalGravity(I)I

    #@5c
    move-result v6

    #@5d
    if-ne v6, v0, :cond_3

    #@5f
    .line 1636
    invoke-interface {p1, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    #@62
    .line 1631
    :cond_3
    add-int/lit8 v3, v3, 0x1

    #@64
    goto :goto_2

    #@65
    .line 1640
    .end local v1    # "child":Landroid/view/View;
    .end local v5    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_4
    return-void
.end method

.method private addSystemView(Landroid/view/View;)V
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    #@0
    .prologue
    .line 1021
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v1

    #@4
    .line 1023
    .local v1, "vlp":Landroid/view/ViewGroup$LayoutParams;
    if-nez v1, :cond_0

    #@6
    .line 1024
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->generateDefaultLayoutParams()Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@9
    move-result-object v0

    #@a
    .line 1030
    .local v0, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :goto_0
    const/4 v2, 0x1

    #@b
    iput v2, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@d
    .line 1031
    invoke-virtual {p0, p1, v0}, Landroid/support/v7/widget/Toolbar;->addView(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V

    #@10
    .line 1032
    return-void

    #@11
    .line 1025
    .end local v0    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_0
    invoke-virtual {p0, v1}, Landroid/support/v7/widget/Toolbar;->checkLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Z

    #@14
    move-result v2

    #@15
    if-nez v2, :cond_1

    #@17
    .line 1026
    invoke-virtual {p0, v1}, Landroid/support/v7/widget/Toolbar;->generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@1a
    move-result-object v0

    #@1b
    .restart local v0    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    goto :goto_0

    #@1c
    .end local v0    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_1
    move-object v0, v1

    #@1d
    .line 1028
    check-cast v0, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@1f
    .restart local v0    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    goto :goto_0
.end method

.method private ensureCollapseButtonView()V
    .locals 5

    #@0
    .prologue
    .line 1002
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@2
    if-nez v1, :cond_0

    #@4
    .line 1003
    new-instance v1, Landroid/widget/ImageButton;

    #@6
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@9
    move-result-object v2

    #@a
    const/4 v3, 0x0

    #@b
    sget v4, Landroid/support/v7/appcompat/R$attr;->toolbarNavigationButtonStyle:I

    #@d
    invoke-direct {v1, v2, v3, v4}, Landroid/widget/ImageButton;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@10
    iput-object v1, p0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@12
    .line 1005
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@14
    iget-object v2, p0, Landroid/support/v7/widget/Toolbar;->mCollapseIcon:Landroid/graphics/drawable/Drawable;

    #@16
    invoke-virtual {v1, v2}, Landroid/widget/ImageButton;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@19
    .line 1006
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@1b
    iget-object v2, p0, Landroid/support/v7/widget/Toolbar;->mCollapseDescription:Ljava/lang/CharSequence;

    #@1d
    invoke-virtual {v1, v2}, Landroid/widget/ImageButton;->setContentDescription(Ljava/lang/CharSequence;)V

    #@20
    .line 1007
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->generateDefaultLayoutParams()Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@23
    move-result-object v0

    #@24
    .line 1008
    .local v0, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    const v1, 0x800003

    #@27
    iget v2, p0, Landroid/support/v7/widget/Toolbar;->mButtonGravity:I

    #@29
    and-int/lit8 v2, v2, 0x70

    #@2b
    or-int/2addr v1, v2

    #@2c
    iput v1, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->gravity:I

    #@2e
    .line 1009
    const/4 v1, 0x2

    #@2f
    iput v1, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@31
    .line 1010
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@33
    invoke-virtual {v1, v0}, Landroid/widget/ImageButton;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@36
    .line 1011
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@38
    new-instance v2, Landroid/support/v7/widget/Toolbar$3;

    #@3a
    invoke-direct {v2, p0}, Landroid/support/v7/widget/Toolbar$3;-><init>(Landroid/support/v7/widget/Toolbar;)V

    #@3d
    invoke-virtual {v1, v2}, Landroid/widget/ImageButton;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    #@40
    .line 1018
    .end local v0    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_0
    return-void
.end method

.method private ensureLogoView()V
    .locals 2

    #@0
    .prologue
    .line 507
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 508
    new-instance v0, Landroid/widget/ImageView;

    #@6
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@9
    move-result-object v1

    #@a
    invoke-direct {v0, v1}, Landroid/widget/ImageView;-><init>(Landroid/content/Context;)V

    #@d
    iput-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@f
    .line 510
    :cond_0
    return-void
.end method

.method private ensureMenu()V
    .locals 3

    #@0
    .prologue
    .line 822
    invoke-direct {p0}, Landroid/support/v7/widget/Toolbar;->ensureMenuView()V

    #@3
    .line 823
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@5
    invoke-virtual {v1}, Landroid/support/v7/widget/ActionMenuView;->peekMenu()Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@8
    move-result-object v1

    #@9
    if-nez v1, :cond_1

    #@b
    .line 825
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@d
    invoke-virtual {v1}, Landroid/support/v7/widget/ActionMenuView;->getMenu()Landroid/view/Menu;

    #@10
    move-result-object v0

    #@11
    check-cast v0, Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@13
    .line 826
    .local v0, "menu":Landroid/support/v7/internal/view/menu/MenuBuilder;
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@15
    if-nez v1, :cond_0

    #@17
    .line 827
    new-instance v1, Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@19
    const/4 v2, 0x0

    #@1a
    invoke-direct {v1, p0, v2}, Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;-><init>(Landroid/support/v7/widget/Toolbar;Landroid/support/v7/widget/Toolbar$1;)V

    #@1d
    iput-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@1f
    .line 829
    :cond_0
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@21
    const/4 v2, 0x1

    #@22
    invoke-virtual {v1, v2}, Landroid/support/v7/widget/ActionMenuView;->setExpandedActionViewsExclusive(Z)V

    #@25
    .line 830
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@27
    iget-object v2, p0, Landroid/support/v7/widget/Toolbar;->mPopupContext:Landroid/content/Context;

    #@29
    invoke-virtual {v0, v1, v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;Landroid/content/Context;)V

    #@2c
    .line 832
    .end local v0    # "menu":Landroid/support/v7/internal/view/menu/MenuBuilder;
    :cond_1
    return-void
.end method

.method private ensureMenuView()V
    .locals 4

    #@0
    .prologue
    .line 835
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@2
    if-nez v1, :cond_0

    #@4
    .line 836
    new-instance v1, Landroid/support/v7/widget/ActionMenuView;

    #@6
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@9
    move-result-object v2

    #@a
    invoke-direct {v1, v2}, Landroid/support/v7/widget/ActionMenuView;-><init>(Landroid/content/Context;)V

    #@d
    iput-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@f
    .line 837
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@11
    iget v2, p0, Landroid/support/v7/widget/Toolbar;->mPopupTheme:I

    #@13
    invoke-virtual {v1, v2}, Landroid/support/v7/widget/ActionMenuView;->setPopupTheme(I)V

    #@16
    .line 838
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@18
    iget-object v2, p0, Landroid/support/v7/widget/Toolbar;->mMenuViewItemClickListener:Landroid/support/v7/widget/ActionMenuView$OnMenuItemClickListener;

    #@1a
    invoke-virtual {v1, v2}, Landroid/support/v7/widget/ActionMenuView;->setOnMenuItemClickListener(Landroid/support/v7/widget/ActionMenuView$OnMenuItemClickListener;)V

    #@1d
    .line 839
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@1f
    iget-object v2, p0, Landroid/support/v7/widget/Toolbar;->mActionMenuPresenterCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@21
    iget-object v3, p0, Landroid/support/v7/widget/Toolbar;->mMenuBuilderCallback:Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

    #@23
    invoke-virtual {v1, v2, v3}, Landroid/support/v7/widget/ActionMenuView;->setMenuCallbacks(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;)V

    #@26
    .line 840
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->generateDefaultLayoutParams()Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@29
    move-result-object v0

    #@2a
    .line 841
    .local v0, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    const v1, 0x800005

    #@2d
    iget v2, p0, Landroid/support/v7/widget/Toolbar;->mButtonGravity:I

    #@2f
    and-int/lit8 v2, v2, 0x70

    #@31
    or-int/2addr v1, v2

    #@32
    iput v1, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->gravity:I

    #@34
    .line 842
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@36
    invoke-virtual {v1, v0}, Landroid/support/v7/widget/ActionMenuView;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@39
    .line 843
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@3b
    invoke-direct {p0, v1}, Landroid/support/v7/widget/Toolbar;->addSystemView(Landroid/view/View;)V

    #@3e
    .line 845
    .end local v0    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_0
    return-void
.end method

.method private ensureNavButtonView()V
    .locals 5

    #@0
    .prologue
    .line 992
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@2
    if-nez v1, :cond_0

    #@4
    .line 993
    new-instance v1, Landroid/widget/ImageButton;

    #@6
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@9
    move-result-object v2

    #@a
    const/4 v3, 0x0

    #@b
    sget v4, Landroid/support/v7/appcompat/R$attr;->toolbarNavigationButtonStyle:I

    #@d
    invoke-direct {v1, v2, v3, v4}, Landroid/widget/ImageButton;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@10
    iput-object v1, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@12
    .line 995
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->generateDefaultLayoutParams()Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@15
    move-result-object v0

    #@16
    .line 996
    .local v0, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    const v1, 0x800003

    #@19
    iget v2, p0, Landroid/support/v7/widget/Toolbar;->mButtonGravity:I

    #@1b
    and-int/lit8 v2, v2, 0x70

    #@1d
    or-int/2addr v1, v2

    #@1e
    iput v1, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->gravity:I

    #@20
    .line 997
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@22
    invoke-virtual {v1, v0}, Landroid/widget/ImageButton;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@25
    .line 999
    .end local v0    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_0
    return-void
.end method

.method private getChildHorizontalGravity(I)I
    .locals 4
    .param p1, "gravity"    # I

    #@0
    .prologue
    .line 1643
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@3
    move-result v2

    #@4
    .line 1644
    .local v2, "ld":I
    invoke-static {p1, v2}, Landroid/support/v4/view/GravityCompat;->getAbsoluteGravity(II)I

    #@7
    move-result v0

    #@8
    .line 1645
    .local v0, "absGrav":I
    and-int/lit8 v1, v0, 0x7

    #@a
    .line 1646
    .local v1, "hGrav":I
    packed-switch v1, :pswitch_data_0

    #@d
    .line 1652
    :pswitch_0
    const/4 v3, 0x1

    #@e
    if-ne v2, v3, :cond_0

    #@10
    const/4 v3, 0x5

    #@11
    :goto_0
    move v1, v3

    #@12
    .end local v1    # "hGrav":I
    :pswitch_1
    return v1

    #@13
    .restart local v1    # "hGrav":I
    :cond_0
    const/4 v3, 0x3

    #@14
    goto :goto_0

    #@15
    .line 1646
    nop

    #@16
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_1
        :pswitch_0
        :pswitch_1
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method

.method private getChildTop(Landroid/view/View;I)I
    .locals 11
    .param p1, "child"    # Landroid/view/View;
    .param p2, "alignmentHeight"    # I

    #@0
    .prologue
    const/4 v9, 0x0

    #@1
    .line 1563
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@4
    move-result-object v3

    #@5
    check-cast v3, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@7
    .line 1564
    .local v3, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredHeight()I

    #@a
    move-result v1

    #@b
    .line 1565
    .local v1, "childHeight":I
    if-lez p2, :cond_1

    #@d
    sub-int v10, v1, p2

    #@f
    div-int/lit8 v0, v10, 0x2

    #@11
    .line 1566
    .local v0, "alignmentOffset":I
    :goto_0
    iget v10, v3, Landroid/support/v7/widget/Toolbar$LayoutParams;->gravity:I

    #@13
    invoke-direct {p0, v10}, Landroid/support/v7/widget/Toolbar;->getChildVerticalGravity(I)I

    #@16
    move-result v10

    #@17
    sparse-switch v10, :sswitch_data_0

    #@1a
    .line 1576
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getPaddingTop()I

    #@1d
    move-result v5

    #@1e
    .line 1577
    .local v5, "paddingTop":I
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getPaddingBottom()I

    #@21
    move-result v4

    #@22
    .line 1578
    .local v4, "paddingBottom":I
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getHeight()I

    #@25
    move-result v2

    #@26
    .line 1579
    .local v2, "height":I
    sub-int v10, v2, v5

    #@28
    sub-int v6, v10, v4

    #@2a
    .line 1580
    .local v6, "space":I
    sub-int v10, v6, v1

    #@2c
    div-int/lit8 v7, v10, 0x2

    #@2e
    .line 1581
    .local v7, "spaceAbove":I
    iget v10, v3, Landroid/support/v7/widget/Toolbar$LayoutParams;->topMargin:I

    #@30
    if-ge v7, v10, :cond_2

    #@32
    .line 1582
    iget v7, v3, Landroid/support/v7/widget/Toolbar$LayoutParams;->topMargin:I

    #@34
    .line 1590
    :cond_0
    :goto_1
    add-int v9, v5, v7

    #@36
    .end local v2    # "height":I
    .end local v4    # "paddingBottom":I
    .end local v5    # "paddingTop":I
    .end local v6    # "space":I
    .end local v7    # "spaceAbove":I
    :goto_2
    return v9

    #@37
    .end local v0    # "alignmentOffset":I
    :cond_1
    move v0, v9

    #@38
    .line 1565
    goto :goto_0

    #@39
    .line 1568
    .restart local v0    # "alignmentOffset":I
    :sswitch_0
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getPaddingTop()I

    #@3c
    move-result v9

    #@3d
    sub-int/2addr v9, v0

    #@3e
    goto :goto_2

    #@3f
    .line 1571
    :sswitch_1
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getHeight()I

    #@42
    move-result v9

    #@43
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getPaddingBottom()I

    #@46
    move-result v10

    #@47
    sub-int/2addr v9, v10

    #@48
    sub-int/2addr v9, v1

    #@49
    iget v10, v3, Landroid/support/v7/widget/Toolbar$LayoutParams;->bottomMargin:I

    #@4b
    sub-int/2addr v9, v10

    #@4c
    sub-int/2addr v9, v0

    #@4d
    goto :goto_2

    #@4e
    .line 1584
    .restart local v2    # "height":I
    .restart local v4    # "paddingBottom":I
    .restart local v5    # "paddingTop":I
    .restart local v6    # "space":I
    .restart local v7    # "spaceAbove":I
    :cond_2
    sub-int v10, v2, v4

    #@50
    sub-int/2addr v10, v1

    #@51
    sub-int/2addr v10, v7

    #@52
    sub-int v8, v10, v5

    #@54
    .line 1586
    .local v8, "spaceBelow":I
    iget v10, v3, Landroid/support/v7/widget/Toolbar$LayoutParams;->bottomMargin:I

    #@56
    if-ge v8, v10, :cond_0

    #@58
    .line 1587
    iget v10, v3, Landroid/support/v7/widget/Toolbar$LayoutParams;->bottomMargin:I

    #@5a
    sub-int/2addr v10, v8

    #@5b
    sub-int v10, v7, v10

    #@5d
    invoke-static {v9, v10}, Ljava/lang/Math;->max(II)I

    #@60
    move-result v7

    #@61
    goto :goto_1

    #@62
    .line 1566
    :sswitch_data_0
    .sparse-switch
        0x30 -> :sswitch_0
        0x50 -> :sswitch_1
    .end sparse-switch
.end method

.method private getChildVerticalGravity(I)I
    .locals 2
    .param p1, "gravity"    # I

    #@0
    .prologue
    .line 1595
    and-int/lit8 v0, p1, 0x70

    #@2
    .line 1596
    .local v0, "vgrav":I
    sparse-switch v0, :sswitch_data_0

    #@5
    .line 1602
    iget v1, p0, Landroid/support/v7/widget/Toolbar;->mGravity:I

    #@7
    and-int/lit8 v0, v1, 0x70

    #@9
    .end local v0    # "vgrav":I
    :sswitch_0
    return v0

    #@a
    .line 1596
    :sswitch_data_0
    .sparse-switch
        0x10 -> :sswitch_0
        0x30 -> :sswitch_0
        0x50 -> :sswitch_0
    .end sparse-switch
.end method

.method private getHorizontalMargins(Landroid/view/View;)I
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    #@0
    .prologue
    .line 1661
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v0

    #@4
    check-cast v0, Landroid/view/ViewGroup$MarginLayoutParams;

    #@6
    .line 1662
    .local v0, "mlp":Landroid/view/ViewGroup$MarginLayoutParams;
    invoke-static {v0}, Landroid/support/v4/view/MarginLayoutParamsCompat;->getMarginStart(Landroid/view/ViewGroup$MarginLayoutParams;)I

    #@9
    move-result v1

    #@a
    invoke-static {v0}, Landroid/support/v4/view/MarginLayoutParamsCompat;->getMarginEnd(Landroid/view/ViewGroup$MarginLayoutParams;)I

    #@d
    move-result v2

    #@e
    add-int/2addr v1, v2

    #@f
    return v1
.end method

.method private getMenuInflater()Landroid/view/MenuInflater;
    .locals 2

    #@0
    .prologue
    .line 848
    new-instance v0, Landroid/support/v7/internal/view/SupportMenuInflater;

    #@2
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1}, Landroid/support/v7/internal/view/SupportMenuInflater;-><init>(Landroid/content/Context;)V

    #@9
    return-object v0
.end method

.method private getMinimumHeightCompat()I
    .locals 2

    #@0
    .prologue
    .line 1757
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x10

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 1759
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getMinimumHeight(Landroid/view/View;)I

    #@9
    move-result v0

    #@a
    .line 1762
    :goto_0
    return v0

    #@b
    :cond_0
    iget v0, p0, Landroid/support/v7/widget/Toolbar;->mMinHeight:I

    #@d
    goto :goto_0
.end method

.method private getVerticalMargins(Landroid/view/View;)I
    .locals 3
    .param p1, "v"    # Landroid/view/View;

    #@0
    .prologue
    .line 1667
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v0

    #@4
    check-cast v0, Landroid/view/ViewGroup$MarginLayoutParams;

    #@6
    .line 1668
    .local v0, "mlp":Landroid/view/ViewGroup$MarginLayoutParams;
    iget v1, v0, Landroid/view/ViewGroup$MarginLayoutParams;->topMargin:I

    #@8
    iget v2, v0, Landroid/view/ViewGroup$MarginLayoutParams;->bottomMargin:I

    #@a
    add-int/2addr v1, v2

    #@b
    return v1
.end method

.method private getViewListMeasuredWidth(Ljava/util/List;[I)I
    .locals 13
    .param p2, "collapsingMargins"    # [I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List",
            "<",
            "Landroid/view/View;",
            ">;[I)I"
        }
    .end annotation

    #@0
    .prologue
    .local p1, "views":Ljava/util/List;, "Ljava/util/List<Landroid/view/View;>;"
    const/4 v12, 0x0

    #@1
    .line 1518
    aget v0, p2, v12

    #@3
    .line 1519
    .local v0, "collapseLeft":I
    const/4 v11, 0x1

    #@4
    aget v1, p2, v11

    #@6
    .line 1520
    .local v1, "collapseRight":I
    const/4 v10, 0x0

    #@7
    .line 1521
    .local v10, "width":I
    invoke-interface {p1}, Ljava/util/List;->size()I

    #@a
    move-result v2

    #@b
    .line 1522
    .local v2, "count":I
    const/4 v3, 0x0

    #@c
    .local v3, "i":I
    :goto_0
    if-ge v3, v2, :cond_0

    #@e
    .line 1523
    invoke-interface {p1, v3}, Ljava/util/List;->get(I)Ljava/lang/Object;

    #@11
    move-result-object v9

    #@12
    check-cast v9, Landroid/view/View;

    #@14
    .line 1524
    .local v9, "v":Landroid/view/View;
    invoke-virtual {v9}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@17
    move-result-object v6

    #@18
    check-cast v6, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@1a
    .line 1525
    .local v6, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    iget v11, v6, Landroid/support/v7/widget/Toolbar$LayoutParams;->leftMargin:I

    #@1c
    sub-int v4, v11, v0

    #@1e
    .line 1526
    .local v4, "l":I
    iget v11, v6, Landroid/support/v7/widget/Toolbar$LayoutParams;->rightMargin:I

    #@20
    sub-int v7, v11, v1

    #@22
    .line 1527
    .local v7, "r":I
    invoke-static {v12, v4}, Ljava/lang/Math;->max(II)I

    #@25
    move-result v5

    #@26
    .line 1528
    .local v5, "leftMargin":I
    invoke-static {v12, v7}, Ljava/lang/Math;->max(II)I

    #@29
    move-result v8

    #@2a
    .line 1529
    .local v8, "rightMargin":I
    neg-int v11, v4

    #@2b
    invoke-static {v12, v11}, Ljava/lang/Math;->max(II)I

    #@2e
    move-result v0

    #@2f
    .line 1530
    neg-int v11, v7

    #@30
    invoke-static {v12, v11}, Ljava/lang/Math;->max(II)I

    #@33
    move-result v1

    #@34
    .line 1531
    invoke-virtual {v9}, Landroid/view/View;->getMeasuredWidth()I

    #@37
    move-result v11

    #@38
    add-int/2addr v11, v5

    #@39
    add-int/2addr v11, v8

    #@3a
    add-int/2addr v10, v11

    #@3b
    .line 1522
    add-int/lit8 v3, v3, 0x1

    #@3d
    goto :goto_0

    #@3e
    .line 1533
    .end local v4    # "l":I
    .end local v5    # "leftMargin":I
    .end local v6    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    .end local v7    # "r":I
    .end local v8    # "rightMargin":I
    .end local v9    # "v":Landroid/view/View;
    :cond_0
    return v10
.end method

.method private static isCustomView(Landroid/view/View;)Z
    .locals 1
    .param p0, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 1700
    invoke-virtual {p0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v0

    #@4
    check-cast v0, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@6
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

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

.method private layoutChildLeft(Landroid/view/View;I[II)I
    .locals 7
    .param p1, "child"    # Landroid/view/View;
    .param p2, "left"    # I
    .param p3, "collapsingMargins"    # [I
    .param p4, "alignmentHeight"    # I

    #@0
    .prologue
    const/4 v6, 0x0

    #@1
    .line 1538
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@4
    move-result-object v2

    #@5
    check-cast v2, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@7
    .line 1539
    .local v2, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    iget v4, v2, Landroid/support/v7/widget/Toolbar$LayoutParams;->leftMargin:I

    #@9
    aget v5, p3, v6

    #@b
    sub-int v1, v4, v5

    #@d
    .line 1540
    .local v1, "l":I
    invoke-static {v6, v1}, Ljava/lang/Math;->max(II)I

    #@10
    move-result v4

    #@11
    add-int/2addr p2, v4

    #@12
    .line 1541
    neg-int v4, v1

    #@13
    invoke-static {v6, v4}, Ljava/lang/Math;->max(II)I

    #@16
    move-result v4

    #@17
    aput v4, p3, v6

    #@19
    .line 1542
    invoke-direct {p0, p1, p4}, Landroid/support/v7/widget/Toolbar;->getChildTop(Landroid/view/View;I)I

    #@1c
    move-result v3

    #@1d
    .line 1543
    .local v3, "top":I
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredWidth()I

    #@20
    move-result v0

    #@21
    .line 1544
    .local v0, "childWidth":I
    add-int v4, p2, v0

    #@23
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredHeight()I

    #@26
    move-result v5

    #@27
    add-int/2addr v5, v3

    #@28
    invoke-virtual {p1, p2, v3, v4, v5}, Landroid/view/View;->layout(IIII)V

    #@2b
    .line 1545
    iget v4, v2, Landroid/support/v7/widget/Toolbar$LayoutParams;->rightMargin:I

    #@2d
    add-int/2addr v4, v0

    #@2e
    add-int/2addr p2, v4

    #@2f
    .line 1546
    return p2
.end method

.method private layoutChildRight(Landroid/view/View;I[II)I
    .locals 8
    .param p1, "child"    # Landroid/view/View;
    .param p2, "right"    # I
    .param p3, "collapsingMargins"    # [I
    .param p4, "alignmentHeight"    # I

    #@0
    .prologue
    const/4 v7, 0x1

    #@1
    const/4 v6, 0x0

    #@2
    .line 1551
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@5
    move-result-object v1

    #@6
    check-cast v1, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@8
    .line 1552
    .local v1, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    iget v4, v1, Landroid/support/v7/widget/Toolbar$LayoutParams;->rightMargin:I

    #@a
    aget v5, p3, v7

    #@c
    sub-int v2, v4, v5

    #@e
    .line 1553
    .local v2, "r":I
    invoke-static {v6, v2}, Ljava/lang/Math;->max(II)I

    #@11
    move-result v4

    #@12
    sub-int/2addr p2, v4

    #@13
    .line 1554
    neg-int v4, v2

    #@14
    invoke-static {v6, v4}, Ljava/lang/Math;->max(II)I

    #@17
    move-result v4

    #@18
    aput v4, p3, v7

    #@1a
    .line 1555
    invoke-direct {p0, p1, p4}, Landroid/support/v7/widget/Toolbar;->getChildTop(Landroid/view/View;I)I

    #@1d
    move-result v3

    #@1e
    .line 1556
    .local v3, "top":I
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredWidth()I

    #@21
    move-result v0

    #@22
    .line 1557
    .local v0, "childWidth":I
    sub-int v4, p2, v0

    #@24
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredHeight()I

    #@27
    move-result v5

    #@28
    add-int/2addr v5, v3

    #@29
    invoke-virtual {p1, v4, v3, p2, v5}, Landroid/view/View;->layout(IIII)V

    #@2c
    .line 1558
    iget v4, v1, Landroid/support/v7/widget/Toolbar$LayoutParams;->leftMargin:I

    #@2e
    add-int/2addr v4, v0

    #@2f
    sub-int/2addr p2, v4

    #@30
    .line 1559
    return p2
.end method

.method private measureChildCollapseMargins(Landroid/view/View;IIII[I)I
    .locals 11
    .param p1, "child"    # Landroid/view/View;
    .param p2, "parentWidthMeasureSpec"    # I
    .param p3, "widthUsed"    # I
    .param p4, "parentHeightMeasureSpec"    # I
    .param p5, "heightUsed"    # I
    .param p6, "collapsingMargins"    # [I

    #@0
    .prologue
    .line 1128
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v5

    #@4
    check-cast v5, Landroid/view/ViewGroup$MarginLayoutParams;

    #@6
    .line 1130
    .local v5, "lp":Landroid/view/ViewGroup$MarginLayoutParams;
    iget v8, v5, Landroid/view/ViewGroup$MarginLayoutParams;->leftMargin:I

    #@8
    const/4 v9, 0x0

    #@9
    aget v9, p6, v9

    #@b
    sub-int v3, v8, v9

    #@d
    .line 1131
    .local v3, "leftDiff":I
    iget v8, v5, Landroid/view/ViewGroup$MarginLayoutParams;->rightMargin:I

    #@f
    const/4 v9, 0x1

    #@10
    aget v9, p6, v9

    #@12
    sub-int v6, v8, v9

    #@14
    .line 1132
    .local v6, "rightDiff":I
    const/4 v8, 0x0

    #@15
    invoke-static {v8, v3}, Ljava/lang/Math;->max(II)I

    #@18
    move-result v4

    #@19
    .line 1133
    .local v4, "leftMargin":I
    const/4 v8, 0x0

    #@1a
    invoke-static {v8, v6}, Ljava/lang/Math;->max(II)I

    #@1d
    move-result v7

    #@1e
    .line 1134
    .local v7, "rightMargin":I
    add-int v2, v4, v7

    #@20
    .line 1135
    .local v2, "hMargins":I
    const/4 v8, 0x0

    #@21
    const/4 v9, 0x0

    #@22
    neg-int v10, v3

    #@23
    invoke-static {v9, v10}, Ljava/lang/Math;->max(II)I

    #@26
    move-result v9

    #@27
    aput v9, p6, v8

    #@29
    .line 1136
    const/4 v8, 0x1

    #@2a
    const/4 v9, 0x0

    #@2b
    neg-int v10, v6

    #@2c
    invoke-static {v9, v10}, Ljava/lang/Math;->max(II)I

    #@2f
    move-result v9

    #@30
    aput v9, p6, v8

    #@32
    .line 1138
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getPaddingLeft()I

    #@35
    move-result v8

    #@36
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getPaddingRight()I

    #@39
    move-result v9

    #@3a
    add-int/2addr v8, v9

    #@3b
    add-int/2addr v8, v2

    #@3c
    add-int/2addr v8, p3

    #@3d
    iget v9, v5, Landroid/view/ViewGroup$MarginLayoutParams;->width:I

    #@3f
    invoke-static {p2, v8, v9}, Landroid/support/v7/widget/Toolbar;->getChildMeasureSpec(III)I

    #@42
    move-result v1

    #@43
    .line 1140
    .local v1, "childWidthMeasureSpec":I
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getPaddingTop()I

    #@46
    move-result v8

    #@47
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getPaddingBottom()I

    #@4a
    move-result v9

    #@4b
    add-int/2addr v8, v9

    #@4c
    iget v9, v5, Landroid/view/ViewGroup$MarginLayoutParams;->topMargin:I

    #@4e
    add-int/2addr v8, v9

    #@4f
    iget v9, v5, Landroid/view/ViewGroup$MarginLayoutParams;->bottomMargin:I

    #@51
    add-int/2addr v8, v9

    #@52
    add-int v8, v8, p5

    #@54
    iget v9, v5, Landroid/view/ViewGroup$MarginLayoutParams;->height:I

    #@56
    invoke-static {p4, v8, v9}, Landroid/support/v7/widget/Toolbar;->getChildMeasureSpec(III)I

    #@59
    move-result v0

    #@5a
    .line 1144
    .local v0, "childHeightMeasureSpec":I
    invoke-virtual {p1, v1, v0}, Landroid/view/View;->measure(II)V

    #@5d
    .line 1145
    invoke-virtual {p1}, Landroid/view/View;->getMeasuredWidth()I

    #@60
    move-result v8

    #@61
    add-int/2addr v8, v2

    #@62
    return v8
.end method

.method private measureChildConstrained(Landroid/view/View;IIIII)V
    .locals 8
    .param p1, "child"    # Landroid/view/View;
    .param p2, "parentWidthSpec"    # I
    .param p3, "widthUsed"    # I
    .param p4, "parentHeightSpec"    # I
    .param p5, "heightUsed"    # I
    .param p6, "heightConstraint"    # I

    #@0
    .prologue
    const/high16 v7, 0x40000000    # 2.0f

    #@2
    .line 1103
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@5
    move-result-object v3

    #@6
    check-cast v3, Landroid/view/ViewGroup$MarginLayoutParams;

    #@8
    .line 1105
    .local v3, "lp":Landroid/view/ViewGroup$MarginLayoutParams;
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getPaddingLeft()I

    #@b
    move-result v5

    #@c
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getPaddingRight()I

    #@f
    move-result v6

    #@10
    add-int/2addr v5, v6

    #@11
    iget v6, v3, Landroid/view/ViewGroup$MarginLayoutParams;->leftMargin:I

    #@13
    add-int/2addr v5, v6

    #@14
    iget v6, v3, Landroid/view/ViewGroup$MarginLayoutParams;->rightMargin:I

    #@16
    add-int/2addr v5, v6

    #@17
    add-int/2addr v5, p3

    #@18
    iget v6, v3, Landroid/view/ViewGroup$MarginLayoutParams;->width:I

    #@1a
    invoke-static {p2, v5, v6}, Landroid/support/v7/widget/Toolbar;->getChildMeasureSpec(III)I

    #@1d
    move-result v2

    #@1e
    .line 1108
    .local v2, "childWidthSpec":I
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getPaddingTop()I

    #@21
    move-result v5

    #@22
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getPaddingBottom()I

    #@25
    move-result v6

    #@26
    add-int/2addr v5, v6

    #@27
    iget v6, v3, Landroid/view/ViewGroup$MarginLayoutParams;->topMargin:I

    #@29
    add-int/2addr v5, v6

    #@2a
    iget v6, v3, Landroid/view/ViewGroup$MarginLayoutParams;->bottomMargin:I

    #@2c
    add-int/2addr v5, v6

    #@2d
    add-int/2addr v5, p5

    #@2e
    iget v6, v3, Landroid/view/ViewGroup$MarginLayoutParams;->height:I

    #@30
    invoke-static {p4, v5, v6}, Landroid/support/v7/widget/Toolbar;->getChildMeasureSpec(III)I

    #@33
    move-result v1

    #@34
    .line 1112
    .local v1, "childHeightSpec":I
    invoke-static {v1}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@37
    move-result v0

    #@38
    .line 1113
    .local v0, "childHeightMode":I
    if-eq v0, v7, :cond_0

    #@3a
    if-ltz p6, :cond_0

    #@3c
    .line 1114
    if-eqz v0, :cond_1

    #@3e
    invoke-static {v1}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@41
    move-result v5

    #@42
    invoke-static {v5, p6}, Ljava/lang/Math;->min(II)I

    #@45
    move-result v4

    #@46
    .line 1117
    .local v4, "size":I
    :goto_0
    invoke-static {v4, v7}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@49
    move-result v1

    #@4a
    .line 1119
    .end local v4    # "size":I
    :cond_0
    invoke-virtual {p1, v2, v1}, Landroid/view/View;->measure(II)V

    #@4d
    .line 1120
    return-void

    #@4e
    :cond_1
    move v4, p6

    #@4f
    .line 1114
    goto :goto_0
.end method

.method private postShowOverflowMenu()V
    .locals 1

    #@0
    .prologue
    .line 1065
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mShowOverflowMenuRunnable:Ljava/lang/Runnable;

    #@2
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/Toolbar;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@5
    .line 1066
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mShowOverflowMenuRunnable:Ljava/lang/Runnable;

    #@7
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/Toolbar;->post(Ljava/lang/Runnable;)Z

    #@a
    .line 1067
    return-void
.end method

.method private setChildVisibilityForExpandedActionView(Z)V
    .locals 6
    .param p1, "expand"    # Z

    #@0
    .prologue
    .line 1712
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getChildCount()I

    #@3
    move-result v1

    #@4
    .line 1713
    .local v1, "childCount":I
    const/4 v2, 0x0

    #@5
    .local v2, "i":I
    :goto_0
    if-ge v2, v1, :cond_2

    #@7
    .line 1714
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/Toolbar;->getChildAt(I)Landroid/view/View;

    #@a
    move-result-object v0

    #@b
    .line 1715
    .local v0, "child":Landroid/view/View;
    invoke-virtual {v0}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@e
    move-result-object v3

    #@f
    check-cast v3, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@11
    .line 1716
    .local v3, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    iget v4, v3, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@13
    const/4 v5, 0x2

    #@14
    if-eq v4, v5, :cond_0

    #@16
    iget-object v4, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@18
    if-eq v0, v4, :cond_0

    #@1a
    .line 1717
    if-eqz p1, :cond_1

    #@1c
    const/16 v4, 0x8

    #@1e
    :goto_1
    invoke-virtual {v0, v4}, Landroid/view/View;->setVisibility(I)V

    #@21
    .line 1713
    :cond_0
    add-int/lit8 v2, v2, 0x1

    #@23
    goto :goto_0

    #@24
    .line 1717
    :cond_1
    const/4 v4, 0x0

    #@25
    goto :goto_1

    #@26
    .line 1720
    .end local v0    # "child":Landroid/view/View;
    .end local v3    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_2
    return-void
.end method

.method private shouldCollapse()Z
    .locals 5

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 1152
    iget-boolean v4, p0, Landroid/support/v7/widget/Toolbar;->mCollapsible:Z

    #@3
    if-nez v4, :cond_1

    #@5
    .line 1162
    :cond_0
    :goto_0
    return v3

    #@6
    .line 1154
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getChildCount()I

    #@9
    move-result v1

    #@a
    .line 1155
    .local v1, "childCount":I
    const/4 v2, 0x0

    #@b
    .local v2, "i":I
    :goto_1
    if-ge v2, v1, :cond_3

    #@d
    .line 1156
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/Toolbar;->getChildAt(I)Landroid/view/View;

    #@10
    move-result-object v0

    #@11
    .line 1157
    .local v0, "child":Landroid/view/View;
    invoke-direct {p0, v0}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@14
    move-result v4

    #@15
    if-eqz v4, :cond_2

    #@17
    invoke-virtual {v0}, Landroid/view/View;->getMeasuredWidth()I

    #@1a
    move-result v4

    #@1b
    if-lez v4, :cond_2

    #@1d
    invoke-virtual {v0}, Landroid/view/View;->getMeasuredHeight()I

    #@20
    move-result v4

    #@21
    if-gtz v4, :cond_0

    #@23
    .line 1155
    :cond_2
    add-int/lit8 v2, v2, 0x1

    #@25
    goto :goto_1

    #@26
    .line 1162
    .end local v0    # "child":Landroid/view/View;
    :cond_3
    const/4 v3, 0x1

    #@27
    goto :goto_0
.end method

.method private shouldLayout(Landroid/view/View;)Z
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 1657
    if-eqz p1, :cond_0

    #@2
    invoke-virtual {p1}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@5
    move-result-object v0

    #@6
    if-ne v0, p0, :cond_0

    #@8
    invoke-virtual {p1}, Landroid/view/View;->getVisibility()I

    #@b
    move-result v0

    #@c
    const/16 v1, 0x8

    #@e
    if-eq v0, v1, :cond_0

    #@10
    const/4 v0, 0x1

    #@11
    :goto_0
    return v0

    #@12
    :cond_0
    const/4 v0, 0x0

    #@13
    goto :goto_0
.end method

.method private static themifyContext(Landroid/content/Context;Landroid/util/AttributeSet;I)Landroid/content/Context;
    .locals 5
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "attrs"    # Landroid/util/AttributeSet;
    .param p2, "defStyleAttr"    # I

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    .line 2005
    sget-object v3, Landroid/support/v7/appcompat/R$styleable;->Toolbar:[I

    #@3
    invoke-virtual {p0, p1, v3, p2, v4}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;

    #@6
    move-result-object v0

    #@7
    .line 2007
    .local v0, "a":Landroid/content/res/TypedArray;
    sget v3, Landroid/support/v7/appcompat/R$styleable;->Toolbar_theme:I

    #@9
    invoke-virtual {v0, v3, v4}, Landroid/content/res/TypedArray;->getResourceId(II)I

    #@c
    move-result v2

    #@d
    .line 2008
    .local v2, "themeId":I
    if-eqz v2, :cond_0

    #@f
    .line 2009
    new-instance v1, Landroid/view/ContextThemeWrapper;

    #@11
    invoke-direct {v1, p0, v2}, Landroid/view/ContextThemeWrapper;-><init>(Landroid/content/Context;I)V

    #@14
    .end local p0    # "context":Landroid/content/Context;
    .local v1, "context":Landroid/content/Context;
    move-object p0, v1

    #@15
    .line 2011
    .end local v1    # "context":Landroid/content/Context;
    .restart local p0    # "context":Landroid/content/Context;
    :cond_0
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@18
    .line 2012
    return-object p0
.end method

.method private updateChildVisibilityForExpandedActionView(Landroid/view/View;)V
    .locals 3
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 1723
    invoke-virtual {p1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@3
    move-result-object v0

    #@4
    check-cast v0, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@6
    .line 1724
    .local v0, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    iget v1, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@8
    const/4 v2, 0x2

    #@9
    if-eq v1, v2, :cond_0

    #@b
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@d
    if-eq p1, v1, :cond_0

    #@f
    .line 1725
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedActionView:Landroid/view/View;

    #@11
    if-eqz v1, :cond_1

    #@13
    const/16 v1, 0x8

    #@15
    :goto_0
    invoke-virtual {p1, v1}, Landroid/view/View;->setVisibility(I)V

    #@18
    .line 1727
    :cond_0
    return-void

    #@19
    .line 1725
    :cond_1
    const/4 v1, 0x0

    #@1a
    goto :goto_0
.end method


# virtual methods
.method public canShowOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 332
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getVisibility()I

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@8
    if-eqz v0, :cond_0

    #@a
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@c
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuView;->isOverflowReserved()Z

    #@f
    move-result v0

    #@10
    if-eqz v0, :cond_0

    #@12
    const/4 v0, 0x1

    #@13
    :goto_0
    return v0

    #@14
    :cond_0
    const/4 v0, 0x0

    #@15
    goto :goto_0
.end method

.method protected checkLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Z
    .locals 1
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 1696
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->checkLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Z

    #@3
    move-result v0

    #@4
    if-eqz v0, :cond_0

    #@6
    instance-of v0, p1, Landroid/support/v7/widget/Toolbar$LayoutParams;

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

.method public collapseActionView()V
    .locals 2

    #@0
    .prologue
    .line 537
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@2
    if-nez v1, :cond_1

    #@4
    const/4 v0, 0x0

    #@5
    .line 539
    .local v0, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :goto_0
    if-eqz v0, :cond_0

    #@7
    .line 540
    invoke-virtual {v0}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->collapseActionView()Z

    #@a
    .line 542
    :cond_0
    return-void

    #@b
    .line 537
    .end local v0    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_1
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@d
    iget-object v0, v1, Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;->mCurrentExpandedItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@f
    goto :goto_0
.end method

.method public dismissPopupMenus()V
    .locals 1

    #@0
    .prologue
    .line 408
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 409
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuView;->dismissPopupMenus()V

    #@9
    .line 411
    :cond_0
    return-void
.end method

.method protected generateDefaultLayoutParams()Landroid/support/v7/widget/Toolbar$LayoutParams;
    .locals 2

    #@0
    .prologue
    const/4 v1, -0x2

    #@1
    .line 1691
    new-instance v0, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@3
    invoke-direct {v0, v1, v1}, Landroid/support/v7/widget/Toolbar$LayoutParams;-><init>(II)V

    #@6
    return-object v0
.end method

.method protected bridge synthetic generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams;
    .locals 1

    #@0
    .prologue
    .line 107
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->generateDefaultLayoutParams()Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public generateLayoutParams(Landroid/util/AttributeSet;)Landroid/support/v7/widget/Toolbar$LayoutParams;
    .locals 2
    .param p1, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 1673
    new-instance v0, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@2
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1, p1}, Landroid/support/v7/widget/Toolbar$LayoutParams;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@9
    return-object v0
.end method

.method protected generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/support/v7/widget/Toolbar$LayoutParams;
    .locals 1
    .param p1, "p"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 1678
    instance-of v0, p1, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1679
    new-instance v0, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@6
    check-cast p1, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@8
    .end local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    invoke-direct {v0, p1}, Landroid/support/v7/widget/Toolbar$LayoutParams;-><init>(Landroid/support/v7/widget/Toolbar$LayoutParams;)V

    #@b
    .line 1685
    :goto_0
    return-object v0

    #@c
    .line 1680
    .restart local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    :cond_0
    instance-of v0, p1, Landroid/support/v7/app/ActionBar$LayoutParams;

    #@e
    if-eqz v0, :cond_1

    #@10
    .line 1681
    new-instance v0, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@12
    check-cast p1, Landroid/support/v7/app/ActionBar$LayoutParams;

    #@14
    .end local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    invoke-direct {v0, p1}, Landroid/support/v7/widget/Toolbar$LayoutParams;-><init>(Landroid/support/v7/app/ActionBar$LayoutParams;)V

    #@17
    goto :goto_0

    #@18
    .line 1682
    .restart local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    :cond_1
    instance-of v0, p1, Landroid/view/ViewGroup$MarginLayoutParams;

    #@1a
    if-eqz v0, :cond_2

    #@1c
    .line 1683
    new-instance v0, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@1e
    check-cast p1, Landroid/view/ViewGroup$MarginLayoutParams;

    #@20
    .end local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    invoke-direct {v0, p1}, Landroid/support/v7/widget/Toolbar$LayoutParams;-><init>(Landroid/view/ViewGroup$MarginLayoutParams;)V

    #@23
    goto :goto_0

    #@24
    .line 1685
    .restart local p1    # "p":Landroid/view/ViewGroup$LayoutParams;
    :cond_2
    new-instance v0, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@26
    invoke-direct {v0, p1}, Landroid/support/v7/widget/Toolbar$LayoutParams;-><init>(Landroid/view/ViewGroup$LayoutParams;)V

    #@29
    goto :goto_0
.end method

.method public bridge synthetic generateLayoutParams(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams;
    .locals 1
    .param p1, "x0"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 107
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/Toolbar;->generateLayoutParams(Landroid/util/AttributeSet;)Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method protected bridge synthetic generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/view/ViewGroup$LayoutParams;
    .locals 1
    .param p1, "x0"    # Landroid/view/ViewGroup$LayoutParams;

    #@0
    .prologue
    .line 107
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/Toolbar;->generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public getContentInsetEnd()I
    .locals 1

    #@0
    .prologue
    .line 930
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mContentInsets:Landroid/support/v7/internal/widget/RtlSpacingHelper;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/RtlSpacingHelper;->getEnd()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getContentInsetLeft()I
    .locals 1

    #@0
    .prologue
    .line 969
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mContentInsets:Landroid/support/v7/internal/widget/RtlSpacingHelper;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/RtlSpacingHelper;->getLeft()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getContentInsetRight()I
    .locals 1

    #@0
    .prologue
    .line 988
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mContentInsets:Landroid/support/v7/internal/widget/RtlSpacingHelper;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/RtlSpacingHelper;->getRight()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getContentInsetStart()I
    .locals 1

    #@0
    .prologue
    .line 911
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mContentInsets:Landroid/support/v7/internal/widget/RtlSpacingHelper;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/RtlSpacingHelper;->getStart()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getLogo()Landroid/graphics/drawable/Drawable;
    .locals 1

    #@0
    .prologue
    .line 465
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@6
    invoke-virtual {v0}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    #@9
    move-result-object v0

    #@a
    :goto_0
    return-object v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public getLogoDescription()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 503
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@6
    invoke-virtual {v0}, Landroid/widget/ImageView;->getContentDescription()Ljava/lang/CharSequence;

    #@9
    move-result-object v0

    #@a
    :goto_0
    return-object v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public getMenu()Landroid/view/Menu;
    .locals 1

    #@0
    .prologue
    .line 817
    invoke-direct {p0}, Landroid/support/v7/widget/Toolbar;->ensureMenu()V

    #@3
    .line 818
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@5
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuView;->getMenu()Landroid/view/Menu;

    #@8
    move-result-object v0

    #@9
    return-object v0
.end method

.method public getNavigationContentDescription()Ljava/lang/CharSequence;
    .locals 1
    .annotation build Landroid/support/annotation/Nullable;
    .end annotation

    #@0
    .prologue
    .line 709
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@6
    invoke-virtual {v0}, Landroid/widget/ImageButton;->getContentDescription()Ljava/lang/CharSequence;

    #@9
    move-result-object v0

    #@a
    :goto_0
    return-object v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public getNavigationIcon()Landroid/graphics/drawable/Drawable;
    .locals 1
    .annotation build Landroid/support/annotation/Nullable;
    .end annotation

    #@0
    .prologue
    .line 791
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@6
    invoke-virtual {v0}, Landroid/widget/ImageButton;->getDrawable()Landroid/graphics/drawable/Drawable;

    #@9
    move-result-object v0

    #@a
    :goto_0
    return-object v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public getPopupTheme()I
    .locals 1

    #@0
    .prologue
    .line 307
    iget v0, p0, Landroid/support/v7/widget/Toolbar;->mPopupTheme:I

    #@2
    return v0
.end method

.method public getSubtitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 606
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleText:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public getTitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 550
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mTitleText:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public getWrapper()Landroid/support/v7/internal/widget/DecorToolbar;
    .locals 2

    #@0
    .prologue
    .line 1705
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mWrapper:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 1706
    new-instance v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@6
    const/4 v1, 0x1

    #@7
    invoke-direct {v0, p0, v1}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;-><init>(Landroid/support/v7/widget/Toolbar;Z)V

    #@a
    iput-object v0, p0, Landroid/support/v7/widget/Toolbar;->mWrapper:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@c
    .line 1708
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mWrapper:Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@e
    return-object v0
.end method

.method public hasExpandedActionView()Z
    .locals 1

    #@0
    .prologue
    .line 523
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@6
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;->mCurrentExpandedItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

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

.method public hideOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 365
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuView;->hideOverflowMenu()Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public inflateMenu(I)V
    .locals 2
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 860
    invoke-direct {p0}, Landroid/support/v7/widget/Toolbar;->getMenuInflater()Landroid/view/MenuInflater;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getMenu()Landroid/view/Menu;

    #@7
    move-result-object v1

    #@8
    invoke-virtual {v0, p1, v1}, Landroid/view/MenuInflater;->inflate(ILandroid/view/Menu;)V

    #@b
    .line 861
    return-void
.end method

.method public isOverflowMenuShowPending()Z
    .locals 1

    #@0
    .prologue
    .line 347
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuView;->isOverflowMenuShowPending()Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public isOverflowMenuShowing()Z
    .locals 1

    #@0
    .prologue
    .line 342
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuView;->isOverflowMenuShowing()Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public isTitleTruncated()Z
    .locals 5

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 415
    iget-object v4, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@3
    if-nez v4, :cond_1

    #@5
    .line 430
    :cond_0
    :goto_0
    return v3

    #@6
    .line 419
    :cond_1
    iget-object v4, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@8
    invoke-virtual {v4}, Landroid/widget/TextView;->getLayout()Landroid/text/Layout;

    #@b
    move-result-object v2

    #@c
    .line 420
    .local v2, "titleLayout":Landroid/text/Layout;
    if-eqz v2, :cond_0

    #@e
    .line 424
    invoke-virtual {v2}, Landroid/text/Layout;->getLineCount()I

    #@11
    move-result v1

    #@12
    .line 425
    .local v1, "lineCount":I
    const/4 v0, 0x0

    #@13
    .local v0, "i":I
    :goto_1
    if-ge v0, v1, :cond_0

    #@15
    .line 426
    invoke-virtual {v2, v0}, Landroid/text/Layout;->getEllipsisCount(I)I

    #@18
    move-result v4

    #@19
    if-lez v4, :cond_2

    #@1b
    .line 427
    const/4 v3, 0x1

    #@1c
    goto :goto_0

    #@1d
    .line 425
    :cond_2
    add-int/lit8 v0, v0, 0x1

    #@1f
    goto :goto_1
.end method

.method protected onDetachedFromWindow()V
    .locals 1

    #@0
    .prologue
    .line 1071
    invoke-super {p0}, Landroid/view/ViewGroup;->onDetachedFromWindow()V

    #@3
    .line 1072
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mShowOverflowMenuRunnable:Ljava/lang/Runnable;

    #@5
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/Toolbar;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@8
    .line 1073
    return-void
.end method

.method protected onLayout(ZIIII)V
    .locals 50
    .param p1, "changed"    # Z
    .param p2, "l"    # I
    .param p3, "t"    # I
    .param p4, "r"    # I
    .param p5, "b"    # I

    #@0
    .prologue
    .line 1303
    invoke-static/range {p0 .. p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@3
    move-result v46

    #@4
    const/16 v47, 0x1

    #@6
    move/from16 v0, v46

    #@8
    move/from16 v1, v47

    #@a
    if-ne v0, v1, :cond_e

    #@c
    const/16 v16, 0x1

    #@e
    .line 1304
    .local v16, "isRtl":Z
    :goto_0
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getWidth()I

    #@11
    move-result v45

    #@12
    .line 1305
    .local v45, "width":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getHeight()I

    #@15
    move-result v14

    #@16
    .line 1306
    .local v14, "height":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getPaddingLeft()I

    #@19
    move-result v24

    #@1a
    .line 1307
    .local v24, "paddingLeft":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getPaddingRight()I

    #@1d
    move-result v25

    #@1e
    .line 1308
    .local v25, "paddingRight":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getPaddingTop()I

    #@21
    move-result v26

    #@22
    .line 1309
    .local v26, "paddingTop":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getPaddingBottom()I

    #@25
    move-result v23

    #@26
    .line 1310
    .local v23, "paddingBottom":I
    move/from16 v20, v24

    #@28
    .line 1311
    .local v20, "left":I
    sub-int v29, v45, v25

    #@2a
    .line 1313
    .local v29, "right":I
    move-object/from16 v0, p0

    #@2c
    iget-object v12, v0, Landroid/support/v7/widget/Toolbar;->mTempMargins:[I

    #@2e
    .line 1314
    .local v12, "collapsingMargins":[I
    const/16 v46, 0x0

    #@30
    const/16 v47, 0x1

    #@32
    const/16 v48, 0x0

    #@34
    aput v48, v12, v47

    #@36
    aput v48, v12, v46

    #@38
    .line 1317
    invoke-direct/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getMinimumHeightCompat()I

    #@3b
    move-result v5

    #@3c
    .line 1319
    .local v5, "alignmentHeight":I
    move-object/from16 v0, p0

    #@3e
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@40
    move-object/from16 v46, v0

    #@42
    move-object/from16 v0, p0

    #@44
    move-object/from16 v1, v46

    #@46
    invoke-direct {v0, v1}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@49
    move-result v46

    #@4a
    if-eqz v46, :cond_0

    #@4c
    .line 1320
    if-eqz v16, :cond_f

    #@4e
    .line 1321
    move-object/from16 v0, p0

    #@50
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@52
    move-object/from16 v46, v0

    #@54
    move-object/from16 v0, p0

    #@56
    move-object/from16 v1, v46

    #@58
    move/from16 v2, v29

    #@5a
    invoke-direct {v0, v1, v2, v12, v5}, Landroid/support/v7/widget/Toolbar;->layoutChildRight(Landroid/view/View;I[II)I

    #@5d
    move-result v29

    #@5e
    .line 1329
    :cond_0
    :goto_1
    move-object/from16 v0, p0

    #@60
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@62
    move-object/from16 v46, v0

    #@64
    move-object/from16 v0, p0

    #@66
    move-object/from16 v1, v46

    #@68
    invoke-direct {v0, v1}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@6b
    move-result v46

    #@6c
    if-eqz v46, :cond_1

    #@6e
    .line 1330
    if-eqz v16, :cond_10

    #@70
    .line 1331
    move-object/from16 v0, p0

    #@72
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@74
    move-object/from16 v46, v0

    #@76
    move-object/from16 v0, p0

    #@78
    move-object/from16 v1, v46

    #@7a
    move/from16 v2, v29

    #@7c
    invoke-direct {v0, v1, v2, v12, v5}, Landroid/support/v7/widget/Toolbar;->layoutChildRight(Landroid/view/View;I[II)I

    #@7f
    move-result v29

    #@80
    .line 1339
    :cond_1
    :goto_2
    move-object/from16 v0, p0

    #@82
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@84
    move-object/from16 v46, v0

    #@86
    move-object/from16 v0, p0

    #@88
    move-object/from16 v1, v46

    #@8a
    invoke-direct {v0, v1}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@8d
    move-result v46

    #@8e
    if-eqz v46, :cond_2

    #@90
    .line 1340
    if-eqz v16, :cond_11

    #@92
    .line 1341
    move-object/from16 v0, p0

    #@94
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@96
    move-object/from16 v46, v0

    #@98
    move-object/from16 v0, p0

    #@9a
    move-object/from16 v1, v46

    #@9c
    move/from16 v2, v20

    #@9e
    invoke-direct {v0, v1, v2, v12, v5}, Landroid/support/v7/widget/Toolbar;->layoutChildLeft(Landroid/view/View;I[II)I

    #@a1
    move-result v20

    #@a2
    .line 1349
    :cond_2
    :goto_3
    const/16 v46, 0x0

    #@a4
    const/16 v47, 0x0

    #@a6
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getContentInsetLeft()I

    #@a9
    move-result v48

    #@aa
    sub-int v48, v48, v20

    #@ac
    invoke-static/range {v47 .. v48}, Ljava/lang/Math;->max(II)I

    #@af
    move-result v47

    #@b0
    aput v47, v12, v46

    #@b2
    .line 1350
    const/16 v46, 0x1

    #@b4
    const/16 v47, 0x0

    #@b6
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getContentInsetRight()I

    #@b9
    move-result v48

    #@ba
    sub-int v49, v45, v25

    #@bc
    sub-int v49, v49, v29

    #@be
    sub-int v48, v48, v49

    #@c0
    invoke-static/range {v47 .. v48}, Ljava/lang/Math;->max(II)I

    #@c3
    move-result v47

    #@c4
    aput v47, v12, v46

    #@c6
    .line 1351
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getContentInsetLeft()I

    #@c9
    move-result v46

    #@ca
    move/from16 v0, v20

    #@cc
    move/from16 v1, v46

    #@ce
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@d1
    move-result v20

    #@d2
    .line 1352
    sub-int v46, v45, v25

    #@d4
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getContentInsetRight()I

    #@d7
    move-result v47

    #@d8
    sub-int v46, v46, v47

    #@da
    move/from16 v0, v29

    #@dc
    move/from16 v1, v46

    #@de
    invoke-static {v0, v1}, Ljava/lang/Math;->min(II)I

    #@e1
    move-result v29

    #@e2
    .line 1354
    move-object/from16 v0, p0

    #@e4
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mExpandedActionView:Landroid/view/View;

    #@e6
    move-object/from16 v46, v0

    #@e8
    move-object/from16 v0, p0

    #@ea
    move-object/from16 v1, v46

    #@ec
    invoke-direct {v0, v1}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@ef
    move-result v46

    #@f0
    if-eqz v46, :cond_3

    #@f2
    .line 1355
    if-eqz v16, :cond_12

    #@f4
    .line 1356
    move-object/from16 v0, p0

    #@f6
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mExpandedActionView:Landroid/view/View;

    #@f8
    move-object/from16 v46, v0

    #@fa
    move-object/from16 v0, p0

    #@fc
    move-object/from16 v1, v46

    #@fe
    move/from16 v2, v29

    #@100
    invoke-direct {v0, v1, v2, v12, v5}, Landroid/support/v7/widget/Toolbar;->layoutChildRight(Landroid/view/View;I[II)I

    #@103
    move-result v29

    #@104
    .line 1364
    :cond_3
    :goto_4
    move-object/from16 v0, p0

    #@106
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@108
    move-object/from16 v46, v0

    #@10a
    move-object/from16 v0, p0

    #@10c
    move-object/from16 v1, v46

    #@10e
    invoke-direct {v0, v1}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@111
    move-result v46

    #@112
    if-eqz v46, :cond_4

    #@114
    .line 1365
    if-eqz v16, :cond_13

    #@116
    .line 1366
    move-object/from16 v0, p0

    #@118
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@11a
    move-object/from16 v46, v0

    #@11c
    move-object/from16 v0, p0

    #@11e
    move-object/from16 v1, v46

    #@120
    move/from16 v2, v29

    #@122
    invoke-direct {v0, v1, v2, v12, v5}, Landroid/support/v7/widget/Toolbar;->layoutChildRight(Landroid/view/View;I[II)I

    #@125
    move-result v29

    #@126
    .line 1374
    :cond_4
    :goto_5
    move-object/from16 v0, p0

    #@128
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@12a
    move-object/from16 v46, v0

    #@12c
    move-object/from16 v0, p0

    #@12e
    move-object/from16 v1, v46

    #@130
    invoke-direct {v0, v1}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@133
    move-result v18

    #@134
    .line 1375
    .local v18, "layoutTitle":Z
    move-object/from16 v0, p0

    #@136
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@138
    move-object/from16 v46, v0

    #@13a
    move-object/from16 v0, p0

    #@13c
    move-object/from16 v1, v46

    #@13e
    invoke-direct {v0, v1}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@141
    move-result v17

    #@142
    .line 1376
    .local v17, "layoutSubtitle":Z
    const/16 v39, 0x0

    #@144
    .line 1377
    .local v39, "titleHeight":I
    if-eqz v18, :cond_5

    #@146
    .line 1378
    move-object/from16 v0, p0

    #@148
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@14a
    move-object/from16 v46, v0

    #@14c
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@14f
    move-result-object v22

    #@150
    check-cast v22, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@152
    .line 1379
    .local v22, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    move-object/from16 v0, v22

    #@154
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->topMargin:I

    #@156
    move/from16 v46, v0

    #@158
    move-object/from16 v0, p0

    #@15a
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@15c
    move-object/from16 v47, v0

    #@15e
    invoke-virtual/range {v47 .. v47}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@161
    move-result v47

    #@162
    add-int v46, v46, v47

    #@164
    move-object/from16 v0, v22

    #@166
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->bottomMargin:I

    #@168
    move/from16 v47, v0

    #@16a
    add-int v46, v46, v47

    #@16c
    add-int v39, v39, v46

    #@16e
    .line 1381
    .end local v22    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_5
    if-eqz v17, :cond_6

    #@170
    .line 1382
    move-object/from16 v0, p0

    #@172
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@174
    move-object/from16 v46, v0

    #@176
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@179
    move-result-object v22

    #@17a
    check-cast v22, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@17c
    .line 1383
    .restart local v22    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    move-object/from16 v0, v22

    #@17e
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->topMargin:I

    #@180
    move/from16 v46, v0

    #@182
    move-object/from16 v0, p0

    #@184
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@186
    move-object/from16 v47, v0

    #@188
    invoke-virtual/range {v47 .. v47}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@18b
    move-result v47

    #@18c
    add-int v46, v46, v47

    #@18e
    move-object/from16 v0, v22

    #@190
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->bottomMargin:I

    #@192
    move/from16 v47, v0

    #@194
    add-int v46, v46, v47

    #@196
    add-int v39, v39, v46

    #@198
    .line 1386
    .end local v22    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_6
    if-nez v18, :cond_7

    #@19a
    if-eqz v17, :cond_d

    #@19c
    .line 1388
    :cond_7
    if-eqz v18, :cond_14

    #@19e
    move-object/from16 v0, p0

    #@1a0
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@1a2
    move-object/from16 v43, v0

    #@1a4
    .line 1389
    .local v43, "topChild":Landroid/view/View;
    :goto_6
    if-eqz v17, :cond_15

    #@1a6
    move-object/from16 v0, p0

    #@1a8
    iget-object v6, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@1aa
    .line 1390
    .local v6, "bottomChild":Landroid/view/View;
    :goto_7
    invoke-virtual/range {v43 .. v43}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@1ad
    move-result-object v44

    #@1ae
    check-cast v44, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@1b0
    .line 1391
    .local v44, "toplp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    invoke-virtual {v6}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@1b3
    move-result-object v7

    #@1b4
    check-cast v7, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@1b6
    .line 1392
    .local v7, "bottomlp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    if-eqz v18, :cond_8

    #@1b8
    move-object/from16 v0, p0

    #@1ba
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@1bc
    move-object/from16 v46, v0

    #@1be
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getMeasuredWidth()I

    #@1c1
    move-result v46

    #@1c2
    if-gtz v46, :cond_9

    #@1c4
    :cond_8
    if-eqz v17, :cond_16

    #@1c6
    move-object/from16 v0, p0

    #@1c8
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@1ca
    move-object/from16 v46, v0

    #@1cc
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getMeasuredWidth()I

    #@1cf
    move-result v46

    #@1d0
    if-lez v46, :cond_16

    #@1d2
    :cond_9
    const/16 v38, 0x1

    #@1d4
    .line 1395
    .local v38, "titleHasWidth":Z
    :goto_8
    move-object/from16 v0, p0

    #@1d6
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mGravity:I

    #@1d8
    move/from16 v46, v0

    #@1da
    and-int/lit8 v46, v46, 0x70

    #@1dc
    sparse-switch v46, :sswitch_data_0

    #@1df
    .line 1401
    sub-int v46, v14, v26

    #@1e1
    sub-int v31, v46, v23

    #@1e3
    .line 1402
    .local v31, "space":I
    sub-int v46, v31, v39

    #@1e5
    div-int/lit8 v32, v46, 0x2

    #@1e7
    .line 1403
    .local v32, "spaceAbove":I
    move-object/from16 v0, v44

    #@1e9
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->topMargin:I

    #@1eb
    move/from16 v46, v0

    #@1ed
    move-object/from16 v0, p0

    #@1ef
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginTop:I

    #@1f1
    move/from16 v47, v0

    #@1f3
    add-int v46, v46, v47

    #@1f5
    move/from16 v0, v32

    #@1f7
    move/from16 v1, v46

    #@1f9
    if-ge v0, v1, :cond_17

    #@1fb
    .line 1404
    move-object/from16 v0, v44

    #@1fd
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->topMargin:I

    #@1ff
    move/from16 v46, v0

    #@201
    move-object/from16 v0, p0

    #@203
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginTop:I

    #@205
    move/from16 v47, v0

    #@207
    add-int v32, v46, v47

    #@209
    .line 1413
    :cond_a
    :goto_9
    add-int v42, v26, v32

    #@20b
    .line 1420
    .end local v31    # "space":I
    .end local v32    # "spaceAbove":I
    .local v42, "titleTop":I
    :goto_a
    if-eqz v16, :cond_19

    #@20d
    .line 1421
    if-eqz v38, :cond_18

    #@20f
    move-object/from16 v0, p0

    #@211
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginStart:I

    #@213
    move/from16 v46, v0

    #@215
    :goto_b
    const/16 v47, 0x1

    #@217
    aget v47, v12, v47

    #@219
    sub-int v28, v46, v47

    #@21b
    .line 1422
    .local v28, "rd":I
    const/16 v46, 0x0

    #@21d
    move/from16 v0, v46

    #@21f
    move/from16 v1, v28

    #@221
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@224
    move-result v46

    #@225
    sub-int v29, v29, v46

    #@227
    .line 1423
    const/16 v46, 0x1

    #@229
    const/16 v47, 0x0

    #@22b
    move/from16 v0, v28

    #@22d
    neg-int v0, v0

    #@22e
    move/from16 v48, v0

    #@230
    invoke-static/range {v47 .. v48}, Ljava/lang/Math;->max(II)I

    #@233
    move-result v47

    #@234
    aput v47, v12, v46

    #@236
    .line 1424
    move/from16 v41, v29

    #@238
    .line 1425
    .local v41, "titleRight":I
    move/from16 v36, v29

    #@23a
    .line 1427
    .local v36, "subtitleRight":I
    if-eqz v18, :cond_b

    #@23c
    .line 1428
    move-object/from16 v0, p0

    #@23e
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@240
    move-object/from16 v46, v0

    #@242
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@245
    move-result-object v22

    #@246
    check-cast v22, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@248
    .line 1429
    .restart local v22    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    move-object/from16 v0, p0

    #@24a
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@24c
    move-object/from16 v46, v0

    #@24e
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getMeasuredWidth()I

    #@251
    move-result v46

    #@252
    sub-int v40, v41, v46

    #@254
    .line 1430
    .local v40, "titleLeft":I
    move-object/from16 v0, p0

    #@256
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@258
    move-object/from16 v46, v0

    #@25a
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@25d
    move-result v46

    #@25e
    add-int v37, v42, v46

    #@260
    .line 1431
    .local v37, "titleBottom":I
    move-object/from16 v0, p0

    #@262
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@264
    move-object/from16 v46, v0

    #@266
    move-object/from16 v0, v46

    #@268
    move/from16 v1, v40

    #@26a
    move/from16 v2, v42

    #@26c
    move/from16 v3, v41

    #@26e
    move/from16 v4, v37

    #@270
    invoke-virtual {v0, v1, v2, v3, v4}, Landroid/widget/TextView;->layout(IIII)V

    #@273
    .line 1432
    move-object/from16 v0, p0

    #@275
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginEnd:I

    #@277
    move/from16 v46, v0

    #@279
    sub-int v41, v40, v46

    #@27b
    .line 1433
    move-object/from16 v0, v22

    #@27d
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->bottomMargin:I

    #@27f
    move/from16 v46, v0

    #@281
    add-int v42, v37, v46

    #@283
    .line 1435
    .end local v22    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    .end local v37    # "titleBottom":I
    .end local v40    # "titleLeft":I
    :cond_b
    if-eqz v17, :cond_c

    #@285
    .line 1436
    move-object/from16 v0, p0

    #@287
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@289
    move-object/from16 v46, v0

    #@28b
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@28e
    move-result-object v22

    #@28f
    check-cast v22, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@291
    .line 1437
    .restart local v22    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    move-object/from16 v0, v22

    #@293
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->topMargin:I

    #@295
    move/from16 v46, v0

    #@297
    add-int v42, v42, v46

    #@299
    .line 1438
    move-object/from16 v0, p0

    #@29b
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@29d
    move-object/from16 v46, v0

    #@29f
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getMeasuredWidth()I

    #@2a2
    move-result v46

    #@2a3
    sub-int v35, v36, v46

    #@2a5
    .line 1439
    .local v35, "subtitleLeft":I
    move-object/from16 v0, p0

    #@2a7
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@2a9
    move-object/from16 v46, v0

    #@2ab
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@2ae
    move-result v46

    #@2af
    add-int v34, v42, v46

    #@2b1
    .line 1440
    .local v34, "subtitleBottom":I
    move-object/from16 v0, p0

    #@2b3
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@2b5
    move-object/from16 v46, v0

    #@2b7
    move-object/from16 v0, v46

    #@2b9
    move/from16 v1, v35

    #@2bb
    move/from16 v2, v42

    #@2bd
    move/from16 v3, v36

    #@2bf
    move/from16 v4, v34

    #@2c1
    invoke-virtual {v0, v1, v2, v3, v4}, Landroid/widget/TextView;->layout(IIII)V

    #@2c4
    .line 1441
    move-object/from16 v0, p0

    #@2c6
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginEnd:I

    #@2c8
    move/from16 v46, v0

    #@2ca
    sub-int v36, v36, v46

    #@2cc
    .line 1442
    move-object/from16 v0, v22

    #@2ce
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->bottomMargin:I

    #@2d0
    move/from16 v46, v0

    #@2d2
    add-int v42, v34, v46

    #@2d4
    .line 1444
    .end local v22    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    .end local v34    # "subtitleBottom":I
    .end local v35    # "subtitleLeft":I
    :cond_c
    if-eqz v38, :cond_d

    #@2d6
    .line 1445
    move/from16 v0, v41

    #@2d8
    move/from16 v1, v36

    #@2da
    invoke-static {v0, v1}, Ljava/lang/Math;->min(II)I

    #@2dd
    move-result v29

    #@2de
    .line 1480
    .end local v6    # "bottomChild":Landroid/view/View;
    .end local v7    # "bottomlp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    .end local v28    # "rd":I
    .end local v36    # "subtitleRight":I
    .end local v38    # "titleHasWidth":Z
    .end local v41    # "titleRight":I
    .end local v42    # "titleTop":I
    .end local v43    # "topChild":Landroid/view/View;
    .end local v44    # "toplp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_d
    :goto_c
    move-object/from16 v0, p0

    #@2e0
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTempViews:Ljava/util/ArrayList;

    #@2e2
    move-object/from16 v46, v0

    #@2e4
    const/16 v47, 0x3

    #@2e6
    move-object/from16 v0, p0

    #@2e8
    move-object/from16 v1, v46

    #@2ea
    move/from16 v2, v47

    #@2ec
    invoke-direct {v0, v1, v2}, Landroid/support/v7/widget/Toolbar;->addCustomViewsWithGravity(Ljava/util/List;I)V

    #@2ef
    .line 1481
    move-object/from16 v0, p0

    #@2f1
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTempViews:Ljava/util/ArrayList;

    #@2f3
    move-object/from16 v46, v0

    #@2f5
    invoke-virtual/range {v46 .. v46}, Ljava/util/ArrayList;->size()I

    #@2f8
    move-result v21

    #@2f9
    .line 1482
    .local v21, "leftViewsCount":I
    const/4 v15, 0x0

    #@2fa
    .local v15, "i":I
    :goto_d
    move/from16 v0, v21

    #@2fc
    if-ge v15, v0, :cond_1d

    #@2fe
    .line 1483
    move-object/from16 v0, p0

    #@300
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTempViews:Ljava/util/ArrayList;

    #@302
    move-object/from16 v46, v0

    #@304
    move-object/from16 v0, v46

    #@306
    invoke-virtual {v0, v15}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@309
    move-result-object v46

    #@30a
    check-cast v46, Landroid/view/View;

    #@30c
    move-object/from16 v0, p0

    #@30e
    move-object/from16 v1, v46

    #@310
    move/from16 v2, v20

    #@312
    invoke-direct {v0, v1, v2, v12, v5}, Landroid/support/v7/widget/Toolbar;->layoutChildLeft(Landroid/view/View;I[II)I

    #@315
    move-result v20

    #@316
    .line 1482
    add-int/lit8 v15, v15, 0x1

    #@318
    goto :goto_d

    #@319
    .line 1303
    .end local v5    # "alignmentHeight":I
    .end local v12    # "collapsingMargins":[I
    .end local v14    # "height":I
    .end local v15    # "i":I
    .end local v16    # "isRtl":Z
    .end local v17    # "layoutSubtitle":Z
    .end local v18    # "layoutTitle":Z
    .end local v20    # "left":I
    .end local v21    # "leftViewsCount":I
    .end local v23    # "paddingBottom":I
    .end local v24    # "paddingLeft":I
    .end local v25    # "paddingRight":I
    .end local v26    # "paddingTop":I
    .end local v29    # "right":I
    .end local v39    # "titleHeight":I
    .end local v45    # "width":I
    :cond_e
    const/16 v16, 0x0

    #@31b
    goto/16 :goto_0

    #@31d
    .line 1324
    .restart local v5    # "alignmentHeight":I
    .restart local v12    # "collapsingMargins":[I
    .restart local v14    # "height":I
    .restart local v16    # "isRtl":Z
    .restart local v20    # "left":I
    .restart local v23    # "paddingBottom":I
    .restart local v24    # "paddingLeft":I
    .restart local v25    # "paddingRight":I
    .restart local v26    # "paddingTop":I
    .restart local v29    # "right":I
    .restart local v45    # "width":I
    :cond_f
    move-object/from16 v0, p0

    #@31f
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@321
    move-object/from16 v46, v0

    #@323
    move-object/from16 v0, p0

    #@325
    move-object/from16 v1, v46

    #@327
    move/from16 v2, v20

    #@329
    invoke-direct {v0, v1, v2, v12, v5}, Landroid/support/v7/widget/Toolbar;->layoutChildLeft(Landroid/view/View;I[II)I

    #@32c
    move-result v20

    #@32d
    goto/16 :goto_1

    #@32f
    .line 1334
    :cond_10
    move-object/from16 v0, p0

    #@331
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@333
    move-object/from16 v46, v0

    #@335
    move-object/from16 v0, p0

    #@337
    move-object/from16 v1, v46

    #@339
    move/from16 v2, v20

    #@33b
    invoke-direct {v0, v1, v2, v12, v5}, Landroid/support/v7/widget/Toolbar;->layoutChildLeft(Landroid/view/View;I[II)I

    #@33e
    move-result v20

    #@33f
    goto/16 :goto_2

    #@341
    .line 1344
    :cond_11
    move-object/from16 v0, p0

    #@343
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@345
    move-object/from16 v46, v0

    #@347
    move-object/from16 v0, p0

    #@349
    move-object/from16 v1, v46

    #@34b
    move/from16 v2, v29

    #@34d
    invoke-direct {v0, v1, v2, v12, v5}, Landroid/support/v7/widget/Toolbar;->layoutChildRight(Landroid/view/View;I[II)I

    #@350
    move-result v29

    #@351
    goto/16 :goto_3

    #@353
    .line 1359
    :cond_12
    move-object/from16 v0, p0

    #@355
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mExpandedActionView:Landroid/view/View;

    #@357
    move-object/from16 v46, v0

    #@359
    move-object/from16 v0, p0

    #@35b
    move-object/from16 v1, v46

    #@35d
    move/from16 v2, v20

    #@35f
    invoke-direct {v0, v1, v2, v12, v5}, Landroid/support/v7/widget/Toolbar;->layoutChildLeft(Landroid/view/View;I[II)I

    #@362
    move-result v20

    #@363
    goto/16 :goto_4

    #@365
    .line 1369
    :cond_13
    move-object/from16 v0, p0

    #@367
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@369
    move-object/from16 v46, v0

    #@36b
    move-object/from16 v0, p0

    #@36d
    move-object/from16 v1, v46

    #@36f
    move/from16 v2, v20

    #@371
    invoke-direct {v0, v1, v2, v12, v5}, Landroid/support/v7/widget/Toolbar;->layoutChildLeft(Landroid/view/View;I[II)I

    #@374
    move-result v20

    #@375
    goto/16 :goto_5

    #@377
    .line 1388
    .restart local v17    # "layoutSubtitle":Z
    .restart local v18    # "layoutTitle":Z
    .restart local v39    # "titleHeight":I
    :cond_14
    move-object/from16 v0, p0

    #@379
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@37b
    move-object/from16 v43, v0

    #@37d
    goto/16 :goto_6

    #@37f
    .line 1389
    .restart local v43    # "topChild":Landroid/view/View;
    :cond_15
    move-object/from16 v0, p0

    #@381
    iget-object v6, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@383
    goto/16 :goto_7

    #@385
    .line 1392
    .restart local v6    # "bottomChild":Landroid/view/View;
    .restart local v7    # "bottomlp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    .restart local v44    # "toplp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_16
    const/16 v38, 0x0

    #@387
    goto/16 :goto_8

    #@389
    .line 1397
    .restart local v38    # "titleHasWidth":Z
    :sswitch_0
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getPaddingTop()I

    #@38c
    move-result v46

    #@38d
    move-object/from16 v0, v44

    #@38f
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->topMargin:I

    #@391
    move/from16 v47, v0

    #@393
    add-int v46, v46, v47

    #@395
    move-object/from16 v0, p0

    #@397
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginTop:I

    #@399
    move/from16 v47, v0

    #@39b
    add-int v42, v46, v47

    #@39d
    .line 1398
    .restart local v42    # "titleTop":I
    goto/16 :goto_a

    #@39f
    .line 1406
    .end local v42    # "titleTop":I
    .restart local v31    # "space":I
    .restart local v32    # "spaceAbove":I
    :cond_17
    sub-int v46, v14, v23

    #@3a1
    sub-int v46, v46, v39

    #@3a3
    sub-int v46, v46, v32

    #@3a5
    sub-int v33, v46, v26

    #@3a7
    .line 1408
    .local v33, "spaceBelow":I
    move-object/from16 v0, v44

    #@3a9
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->bottomMargin:I

    #@3ab
    move/from16 v46, v0

    #@3ad
    move-object/from16 v0, p0

    #@3af
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginBottom:I

    #@3b1
    move/from16 v47, v0

    #@3b3
    add-int v46, v46, v47

    #@3b5
    move/from16 v0, v33

    #@3b7
    move/from16 v1, v46

    #@3b9
    if-ge v0, v1, :cond_a

    #@3bb
    .line 1409
    const/16 v46, 0x0

    #@3bd
    iget v0, v7, Landroid/support/v7/widget/Toolbar$LayoutParams;->bottomMargin:I

    #@3bf
    move/from16 v47, v0

    #@3c1
    move-object/from16 v0, p0

    #@3c3
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginBottom:I

    #@3c5
    move/from16 v48, v0

    #@3c7
    add-int v47, v47, v48

    #@3c9
    sub-int v47, v47, v33

    #@3cb
    sub-int v47, v32, v47

    #@3cd
    invoke-static/range {v46 .. v47}, Ljava/lang/Math;->max(II)I

    #@3d0
    move-result v32

    #@3d1
    goto/16 :goto_9

    #@3d3
    .line 1416
    .end local v31    # "space":I
    .end local v32    # "spaceAbove":I
    .end local v33    # "spaceBelow":I
    :sswitch_1
    sub-int v46, v14, v23

    #@3d5
    iget v0, v7, Landroid/support/v7/widget/Toolbar$LayoutParams;->bottomMargin:I

    #@3d7
    move/from16 v47, v0

    #@3d9
    sub-int v46, v46, v47

    #@3db
    move-object/from16 v0, p0

    #@3dd
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginBottom:I

    #@3df
    move/from16 v47, v0

    #@3e1
    sub-int v46, v46, v47

    #@3e3
    sub-int v42, v46, v39

    #@3e5
    .restart local v42    # "titleTop":I
    goto/16 :goto_a

    #@3e7
    .line 1421
    :cond_18
    const/16 v46, 0x0

    #@3e9
    goto/16 :goto_b

    #@3eb
    .line 1448
    :cond_19
    if-eqz v38, :cond_1c

    #@3ed
    move-object/from16 v0, p0

    #@3ef
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginStart:I

    #@3f1
    move/from16 v46, v0

    #@3f3
    :goto_e
    const/16 v47, 0x0

    #@3f5
    aget v47, v12, v47

    #@3f7
    sub-int v19, v46, v47

    #@3f9
    .line 1449
    .local v19, "ld":I
    const/16 v46, 0x0

    #@3fb
    move/from16 v0, v46

    #@3fd
    move/from16 v1, v19

    #@3ff
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@402
    move-result v46

    #@403
    add-int v20, v20, v46

    #@405
    .line 1450
    const/16 v46, 0x0

    #@407
    const/16 v47, 0x0

    #@409
    move/from16 v0, v19

    #@40b
    neg-int v0, v0

    #@40c
    move/from16 v48, v0

    #@40e
    invoke-static/range {v47 .. v48}, Ljava/lang/Math;->max(II)I

    #@411
    move-result v47

    #@412
    aput v47, v12, v46

    #@414
    .line 1451
    move/from16 v40, v20

    #@416
    .line 1452
    .restart local v40    # "titleLeft":I
    move/from16 v35, v20

    #@418
    .line 1454
    .restart local v35    # "subtitleLeft":I
    if-eqz v18, :cond_1a

    #@41a
    .line 1455
    move-object/from16 v0, p0

    #@41c
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@41e
    move-object/from16 v46, v0

    #@420
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@423
    move-result-object v22

    #@424
    check-cast v22, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@426
    .line 1456
    .restart local v22    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    move-object/from16 v0, p0

    #@428
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@42a
    move-object/from16 v46, v0

    #@42c
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getMeasuredWidth()I

    #@42f
    move-result v46

    #@430
    add-int v41, v40, v46

    #@432
    .line 1457
    .restart local v41    # "titleRight":I
    move-object/from16 v0, p0

    #@434
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@436
    move-object/from16 v46, v0

    #@438
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@43b
    move-result v46

    #@43c
    add-int v37, v42, v46

    #@43e
    .line 1458
    .restart local v37    # "titleBottom":I
    move-object/from16 v0, p0

    #@440
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@442
    move-object/from16 v46, v0

    #@444
    move-object/from16 v0, v46

    #@446
    move/from16 v1, v40

    #@448
    move/from16 v2, v42

    #@44a
    move/from16 v3, v41

    #@44c
    move/from16 v4, v37

    #@44e
    invoke-virtual {v0, v1, v2, v3, v4}, Landroid/widget/TextView;->layout(IIII)V

    #@451
    .line 1459
    move-object/from16 v0, p0

    #@453
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginEnd:I

    #@455
    move/from16 v46, v0

    #@457
    add-int v40, v41, v46

    #@459
    .line 1460
    move-object/from16 v0, v22

    #@45b
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->bottomMargin:I

    #@45d
    move/from16 v46, v0

    #@45f
    add-int v42, v37, v46

    #@461
    .line 1462
    .end local v22    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    .end local v37    # "titleBottom":I
    .end local v41    # "titleRight":I
    :cond_1a
    if-eqz v17, :cond_1b

    #@463
    .line 1463
    move-object/from16 v0, p0

    #@465
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@467
    move-object/from16 v46, v0

    #@469
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@46c
    move-result-object v22

    #@46d
    check-cast v22, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@46f
    .line 1464
    .restart local v22    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    move-object/from16 v0, v22

    #@471
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->topMargin:I

    #@473
    move/from16 v46, v0

    #@475
    add-int v42, v42, v46

    #@477
    .line 1465
    move-object/from16 v0, p0

    #@479
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@47b
    move-object/from16 v46, v0

    #@47d
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getMeasuredWidth()I

    #@480
    move-result v46

    #@481
    add-int v36, v35, v46

    #@483
    .line 1466
    .restart local v36    # "subtitleRight":I
    move-object/from16 v0, p0

    #@485
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@487
    move-object/from16 v46, v0

    #@489
    invoke-virtual/range {v46 .. v46}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@48c
    move-result v46

    #@48d
    add-int v34, v42, v46

    #@48f
    .line 1467
    .restart local v34    # "subtitleBottom":I
    move-object/from16 v0, p0

    #@491
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@493
    move-object/from16 v46, v0

    #@495
    move-object/from16 v0, v46

    #@497
    move/from16 v1, v35

    #@499
    move/from16 v2, v42

    #@49b
    move/from16 v3, v36

    #@49d
    move/from16 v4, v34

    #@49f
    invoke-virtual {v0, v1, v2, v3, v4}, Landroid/widget/TextView;->layout(IIII)V

    #@4a2
    .line 1468
    move-object/from16 v0, p0

    #@4a4
    iget v0, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginEnd:I

    #@4a6
    move/from16 v46, v0

    #@4a8
    add-int v35, v36, v46

    #@4aa
    .line 1469
    move-object/from16 v0, v22

    #@4ac
    iget v0, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->bottomMargin:I

    #@4ae
    move/from16 v46, v0

    #@4b0
    add-int v42, v34, v46

    #@4b2
    .line 1471
    .end local v22    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    .end local v34    # "subtitleBottom":I
    .end local v36    # "subtitleRight":I
    :cond_1b
    if-eqz v38, :cond_d

    #@4b4
    .line 1472
    move/from16 v0, v40

    #@4b6
    move/from16 v1, v35

    #@4b8
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@4bb
    move-result v20

    #@4bc
    goto/16 :goto_c

    #@4be
    .line 1448
    .end local v19    # "ld":I
    .end local v35    # "subtitleLeft":I
    .end local v40    # "titleLeft":I
    :cond_1c
    const/16 v46, 0x0

    #@4c0
    goto/16 :goto_e

    #@4c2
    .line 1487
    .end local v6    # "bottomChild":Landroid/view/View;
    .end local v7    # "bottomlp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    .end local v38    # "titleHasWidth":Z
    .end local v42    # "titleTop":I
    .end local v43    # "topChild":Landroid/view/View;
    .end local v44    # "toplp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    .restart local v15    # "i":I
    .restart local v21    # "leftViewsCount":I
    :cond_1d
    move-object/from16 v0, p0

    #@4c4
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTempViews:Ljava/util/ArrayList;

    #@4c6
    move-object/from16 v46, v0

    #@4c8
    const/16 v47, 0x5

    #@4ca
    move-object/from16 v0, p0

    #@4cc
    move-object/from16 v1, v46

    #@4ce
    move/from16 v2, v47

    #@4d0
    invoke-direct {v0, v1, v2}, Landroid/support/v7/widget/Toolbar;->addCustomViewsWithGravity(Ljava/util/List;I)V

    #@4d3
    .line 1488
    move-object/from16 v0, p0

    #@4d5
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTempViews:Ljava/util/ArrayList;

    #@4d7
    move-object/from16 v46, v0

    #@4d9
    invoke-virtual/range {v46 .. v46}, Ljava/util/ArrayList;->size()I

    #@4dc
    move-result v30

    #@4dd
    .line 1489
    .local v30, "rightViewsCount":I
    const/4 v15, 0x0

    #@4de
    :goto_f
    move/from16 v0, v30

    #@4e0
    if-ge v15, v0, :cond_1e

    #@4e2
    .line 1490
    move-object/from16 v0, p0

    #@4e4
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTempViews:Ljava/util/ArrayList;

    #@4e6
    move-object/from16 v46, v0

    #@4e8
    move-object/from16 v0, v46

    #@4ea
    invoke-virtual {v0, v15}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@4ed
    move-result-object v46

    #@4ee
    check-cast v46, Landroid/view/View;

    #@4f0
    move-object/from16 v0, p0

    #@4f2
    move-object/from16 v1, v46

    #@4f4
    move/from16 v2, v29

    #@4f6
    invoke-direct {v0, v1, v2, v12, v5}, Landroid/support/v7/widget/Toolbar;->layoutChildRight(Landroid/view/View;I[II)I

    #@4f9
    move-result v29

    #@4fa
    .line 1489
    add-int/lit8 v15, v15, 0x1

    #@4fc
    goto :goto_f

    #@4fd
    .line 1496
    :cond_1e
    move-object/from16 v0, p0

    #@4ff
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTempViews:Ljava/util/ArrayList;

    #@501
    move-object/from16 v46, v0

    #@503
    const/16 v47, 0x1

    #@505
    move-object/from16 v0, p0

    #@507
    move-object/from16 v1, v46

    #@509
    move/from16 v2, v47

    #@50b
    invoke-direct {v0, v1, v2}, Landroid/support/v7/widget/Toolbar;->addCustomViewsWithGravity(Ljava/util/List;I)V

    #@50e
    .line 1497
    move-object/from16 v0, p0

    #@510
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTempViews:Ljava/util/ArrayList;

    #@512
    move-object/from16 v46, v0

    #@514
    move-object/from16 v0, p0

    #@516
    move-object/from16 v1, v46

    #@518
    invoke-direct {v0, v1, v12}, Landroid/support/v7/widget/Toolbar;->getViewListMeasuredWidth(Ljava/util/List;[I)I

    #@51b
    move-result v11

    #@51c
    .line 1498
    .local v11, "centerViewsWidth":I
    sub-int v46, v45, v24

    #@51e
    sub-int v46, v46, v25

    #@520
    div-int/lit8 v46, v46, 0x2

    #@522
    add-int v27, v24, v46

    #@524
    .line 1499
    .local v27, "parentCenter":I
    div-int/lit8 v13, v11, 0x2

    #@526
    .line 1500
    .local v13, "halfCenterViewsWidth":I
    sub-int v8, v27, v13

    #@528
    .line 1501
    .local v8, "centerLeft":I
    add-int v9, v8, v11

    #@52a
    .line 1502
    .local v9, "centerRight":I
    move/from16 v0, v20

    #@52c
    if-ge v8, v0, :cond_20

    #@52e
    .line 1503
    move/from16 v8, v20

    #@530
    .line 1508
    :cond_1f
    :goto_10
    move-object/from16 v0, p0

    #@532
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTempViews:Ljava/util/ArrayList;

    #@534
    move-object/from16 v46, v0

    #@536
    invoke-virtual/range {v46 .. v46}, Ljava/util/ArrayList;->size()I

    #@539
    move-result v10

    #@53a
    .line 1509
    .local v10, "centerViewsCount":I
    const/4 v15, 0x0

    #@53b
    :goto_11
    if-ge v15, v10, :cond_21

    #@53d
    .line 1510
    move-object/from16 v0, p0

    #@53f
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTempViews:Ljava/util/ArrayList;

    #@541
    move-object/from16 v46, v0

    #@543
    move-object/from16 v0, v46

    #@545
    invoke-virtual {v0, v15}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@548
    move-result-object v46

    #@549
    check-cast v46, Landroid/view/View;

    #@54b
    move-object/from16 v0, p0

    #@54d
    move-object/from16 v1, v46

    #@54f
    invoke-direct {v0, v1, v8, v12, v5}, Landroid/support/v7/widget/Toolbar;->layoutChildLeft(Landroid/view/View;I[II)I

    #@552
    move-result v8

    #@553
    .line 1509
    add-int/lit8 v15, v15, 0x1

    #@555
    goto :goto_11

    #@556
    .line 1504
    .end local v10    # "centerViewsCount":I
    :cond_20
    move/from16 v0, v29

    #@558
    if-le v9, v0, :cond_1f

    #@55a
    .line 1505
    sub-int v46, v9, v29

    #@55c
    sub-int v8, v8, v46

    #@55e
    goto :goto_10

    #@55f
    .line 1514
    .restart local v10    # "centerViewsCount":I
    :cond_21
    move-object/from16 v0, p0

    #@561
    iget-object v0, v0, Landroid/support/v7/widget/Toolbar;->mTempViews:Ljava/util/ArrayList;

    #@563
    move-object/from16 v46, v0

    #@565
    invoke-virtual/range {v46 .. v46}, Ljava/util/ArrayList;->clear()V

    #@568
    .line 1515
    return-void

    #@569
    .line 1395
    nop

    #@56a
    :sswitch_data_0
    .sparse-switch
        0x30 -> :sswitch_0
        0x50 -> :sswitch_1
    .end sparse-switch
.end method

.method protected onMeasure(II)V
    .locals 37
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 1167
    const/4 v6, 0x0

    #@1
    .line 1168
    .local v6, "width":I
    const/16 v25, 0x0

    #@3
    .line 1169
    .local v25, "height":I
    const/16 v22, 0x0

    #@5
    .line 1171
    .local v22, "childState":I
    move-object/from16 v0, p0

    #@7
    iget-object v13, v0, Landroid/support/v7/widget/Toolbar;->mTempMargins:[I

    #@9
    .line 1174
    .local v13, "collapsingMargins":[I
    invoke-static/range {p0 .. p0}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@c
    move-result v3

    #@d
    if-eqz v3, :cond_6

    #@f
    .line 1175
    const/16 v29, 0x1

    #@11
    .line 1176
    .local v29, "marginStartIndex":I
    const/16 v28, 0x0

    #@13
    .line 1184
    .local v28, "marginEndIndex":I
    :goto_0
    const/16 v33, 0x0

    #@15
    .line 1185
    .local v33, "navWidth":I
    move-object/from16 v0, p0

    #@17
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@19
    move-object/from16 v0, p0

    #@1b
    invoke-direct {v0, v3}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@1e
    move-result v3

    #@1f
    if-eqz v3, :cond_0

    #@21
    .line 1186
    move-object/from16 v0, p0

    #@23
    iget-object v4, v0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@25
    const/4 v8, 0x0

    #@26
    move-object/from16 v0, p0

    #@28
    iget v9, v0, Landroid/support/v7/widget/Toolbar;->mMaxButtonHeight:I

    #@2a
    move-object/from16 v3, p0

    #@2c
    move/from16 v5, p1

    #@2e
    move/from16 v7, p2

    #@30
    invoke-direct/range {v3 .. v9}, Landroid/support/v7/widget/Toolbar;->measureChildConstrained(Landroid/view/View;IIIII)V

    #@33
    .line 1188
    move-object/from16 v0, p0

    #@35
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@37
    invoke-virtual {v3}, Landroid/widget/ImageButton;->getMeasuredWidth()I

    #@3a
    move-result v3

    #@3b
    move-object/from16 v0, p0

    #@3d
    iget-object v5, v0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@3f
    move-object/from16 v0, p0

    #@41
    invoke-direct {v0, v5}, Landroid/support/v7/widget/Toolbar;->getHorizontalMargins(Landroid/view/View;)I

    #@44
    move-result v5

    #@45
    add-int v33, v3, v5

    #@47
    .line 1189
    move-object/from16 v0, p0

    #@49
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@4b
    invoke-virtual {v3}, Landroid/widget/ImageButton;->getMeasuredHeight()I

    #@4e
    move-result v3

    #@4f
    move-object/from16 v0, p0

    #@51
    iget-object v5, v0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@53
    move-object/from16 v0, p0

    #@55
    invoke-direct {v0, v5}, Landroid/support/v7/widget/Toolbar;->getVerticalMargins(Landroid/view/View;)I

    #@58
    move-result v5

    #@59
    add-int/2addr v3, v5

    #@5a
    move/from16 v0, v25

    #@5c
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@5f
    move-result v25

    #@60
    .line 1191
    move-object/from16 v0, p0

    #@62
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@64
    invoke-static {v3}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@67
    move-result v3

    #@68
    move/from16 v0, v22

    #@6a
    invoke-static {v0, v3}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@6d
    move-result v22

    #@6e
    .line 1195
    :cond_0
    move-object/from16 v0, p0

    #@70
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@72
    move-object/from16 v0, p0

    #@74
    invoke-direct {v0, v3}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@77
    move-result v3

    #@78
    if-eqz v3, :cond_1

    #@7a
    .line 1196
    move-object/from16 v0, p0

    #@7c
    iget-object v4, v0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@7e
    const/4 v8, 0x0

    #@7f
    move-object/from16 v0, p0

    #@81
    iget v9, v0, Landroid/support/v7/widget/Toolbar;->mMaxButtonHeight:I

    #@83
    move-object/from16 v3, p0

    #@85
    move/from16 v5, p1

    #@87
    move/from16 v7, p2

    #@89
    invoke-direct/range {v3 .. v9}, Landroid/support/v7/widget/Toolbar;->measureChildConstrained(Landroid/view/View;IIIII)V

    #@8c
    .line 1198
    move-object/from16 v0, p0

    #@8e
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@90
    invoke-virtual {v3}, Landroid/widget/ImageButton;->getMeasuredWidth()I

    #@93
    move-result v3

    #@94
    move-object/from16 v0, p0

    #@96
    iget-object v5, v0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@98
    move-object/from16 v0, p0

    #@9a
    invoke-direct {v0, v5}, Landroid/support/v7/widget/Toolbar;->getHorizontalMargins(Landroid/view/View;)I

    #@9d
    move-result v5

    #@9e
    add-int v33, v3, v5

    #@a0
    .line 1200
    move-object/from16 v0, p0

    #@a2
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@a4
    invoke-virtual {v3}, Landroid/widget/ImageButton;->getMeasuredHeight()I

    #@a7
    move-result v3

    #@a8
    move-object/from16 v0, p0

    #@aa
    iget-object v5, v0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@ac
    move-object/from16 v0, p0

    #@ae
    invoke-direct {v0, v5}, Landroid/support/v7/widget/Toolbar;->getVerticalMargins(Landroid/view/View;)I

    #@b1
    move-result v5

    #@b2
    add-int/2addr v3, v5

    #@b3
    move/from16 v0, v25

    #@b5
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@b8
    move-result v25

    #@b9
    .line 1202
    move-object/from16 v0, p0

    #@bb
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mCollapseButtonView:Landroid/widget/ImageButton;

    #@bd
    invoke-static {v3}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@c0
    move-result v3

    #@c1
    move/from16 v0, v22

    #@c3
    invoke-static {v0, v3}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@c6
    move-result v22

    #@c7
    .line 1206
    :cond_1
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getContentInsetStart()I

    #@ca
    move-result v24

    #@cb
    .line 1207
    .local v24, "contentInsetStart":I
    move/from16 v0, v24

    #@cd
    move/from16 v1, v33

    #@cf
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@d2
    move-result v3

    #@d3
    add-int/2addr v6, v3

    #@d4
    .line 1208
    const/4 v3, 0x0

    #@d5
    sub-int v5, v24, v33

    #@d7
    invoke-static {v3, v5}, Ljava/lang/Math;->max(II)I

    #@da
    move-result v3

    #@db
    aput v3, v13, v29

    #@dd
    .line 1210
    const/16 v32, 0x0

    #@df
    .line 1211
    .local v32, "menuWidth":I
    move-object/from16 v0, p0

    #@e1
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@e3
    move-object/from16 v0, p0

    #@e5
    invoke-direct {v0, v3}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@e8
    move-result v3

    #@e9
    if-eqz v3, :cond_2

    #@eb
    .line 1212
    move-object/from16 v0, p0

    #@ed
    iget-object v4, v0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@ef
    const/4 v8, 0x0

    #@f0
    move-object/from16 v0, p0

    #@f2
    iget v9, v0, Landroid/support/v7/widget/Toolbar;->mMaxButtonHeight:I

    #@f4
    move-object/from16 v3, p0

    #@f6
    move/from16 v5, p1

    #@f8
    move/from16 v7, p2

    #@fa
    invoke-direct/range {v3 .. v9}, Landroid/support/v7/widget/Toolbar;->measureChildConstrained(Landroid/view/View;IIIII)V

    #@fd
    .line 1214
    move-object/from16 v0, p0

    #@ff
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@101
    invoke-virtual {v3}, Landroid/support/v7/widget/ActionMenuView;->getMeasuredWidth()I

    #@104
    move-result v3

    #@105
    move-object/from16 v0, p0

    #@107
    iget-object v5, v0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@109
    move-object/from16 v0, p0

    #@10b
    invoke-direct {v0, v5}, Landroid/support/v7/widget/Toolbar;->getHorizontalMargins(Landroid/view/View;)I

    #@10e
    move-result v5

    #@10f
    add-int v32, v3, v5

    #@111
    .line 1215
    move-object/from16 v0, p0

    #@113
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@115
    invoke-virtual {v3}, Landroid/support/v7/widget/ActionMenuView;->getMeasuredHeight()I

    #@118
    move-result v3

    #@119
    move-object/from16 v0, p0

    #@11b
    iget-object v5, v0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@11d
    move-object/from16 v0, p0

    #@11f
    invoke-direct {v0, v5}, Landroid/support/v7/widget/Toolbar;->getVerticalMargins(Landroid/view/View;)I

    #@122
    move-result v5

    #@123
    add-int/2addr v3, v5

    #@124
    move/from16 v0, v25

    #@126
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@129
    move-result v25

    #@12a
    .line 1217
    move-object/from16 v0, p0

    #@12c
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@12e
    invoke-static {v3}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@131
    move-result v3

    #@132
    move/from16 v0, v22

    #@134
    invoke-static {v0, v3}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@137
    move-result v22

    #@138
    .line 1221
    :cond_2
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getContentInsetEnd()I

    #@13b
    move-result v23

    #@13c
    .line 1222
    .local v23, "contentInsetEnd":I
    move/from16 v0, v23

    #@13e
    move/from16 v1, v32

    #@140
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@143
    move-result v3

    #@144
    add-int/2addr v6, v3

    #@145
    .line 1223
    const/4 v3, 0x0

    #@146
    sub-int v5, v23, v32

    #@148
    invoke-static {v3, v5}, Ljava/lang/Math;->max(II)I

    #@14b
    move-result v3

    #@14c
    aput v3, v13, v28

    #@14e
    .line 1225
    move-object/from16 v0, p0

    #@150
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mExpandedActionView:Landroid/view/View;

    #@152
    move-object/from16 v0, p0

    #@154
    invoke-direct {v0, v3}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@157
    move-result v3

    #@158
    if-eqz v3, :cond_3

    #@15a
    .line 1226
    move-object/from16 v0, p0

    #@15c
    iget-object v4, v0, Landroid/support/v7/widget/Toolbar;->mExpandedActionView:Landroid/view/View;

    #@15e
    const/4 v8, 0x0

    #@15f
    move-object/from16 v3, p0

    #@161
    move/from16 v5, p1

    #@163
    move/from16 v7, p2

    #@165
    move-object v9, v13

    #@166
    invoke-direct/range {v3 .. v9}, Landroid/support/v7/widget/Toolbar;->measureChildCollapseMargins(Landroid/view/View;IIII[I)I

    #@169
    move-result v3

    #@16a
    add-int/2addr v6, v3

    #@16b
    .line 1228
    move-object/from16 v0, p0

    #@16d
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mExpandedActionView:Landroid/view/View;

    #@16f
    invoke-virtual {v3}, Landroid/view/View;->getMeasuredHeight()I

    #@172
    move-result v3

    #@173
    move-object/from16 v0, p0

    #@175
    iget-object v5, v0, Landroid/support/v7/widget/Toolbar;->mExpandedActionView:Landroid/view/View;

    #@177
    move-object/from16 v0, p0

    #@179
    invoke-direct {v0, v5}, Landroid/support/v7/widget/Toolbar;->getVerticalMargins(Landroid/view/View;)I

    #@17c
    move-result v5

    #@17d
    add-int/2addr v3, v5

    #@17e
    move/from16 v0, v25

    #@180
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@183
    move-result v25

    #@184
    .line 1230
    move-object/from16 v0, p0

    #@186
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mExpandedActionView:Landroid/view/View;

    #@188
    invoke-static {v3}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@18b
    move-result v3

    #@18c
    move/from16 v0, v22

    #@18e
    invoke-static {v0, v3}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@191
    move-result v22

    #@192
    .line 1234
    :cond_3
    move-object/from16 v0, p0

    #@194
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@196
    move-object/from16 v0, p0

    #@198
    invoke-direct {v0, v3}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@19b
    move-result v3

    #@19c
    if-eqz v3, :cond_4

    #@19e
    .line 1235
    move-object/from16 v0, p0

    #@1a0
    iget-object v4, v0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@1a2
    const/4 v8, 0x0

    #@1a3
    move-object/from16 v3, p0

    #@1a5
    move/from16 v5, p1

    #@1a7
    move/from16 v7, p2

    #@1a9
    move-object v9, v13

    #@1aa
    invoke-direct/range {v3 .. v9}, Landroid/support/v7/widget/Toolbar;->measureChildCollapseMargins(Landroid/view/View;IIII[I)I

    #@1ad
    move-result v3

    #@1ae
    add-int/2addr v6, v3

    #@1af
    .line 1237
    move-object/from16 v0, p0

    #@1b1
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@1b3
    invoke-virtual {v3}, Landroid/widget/ImageView;->getMeasuredHeight()I

    #@1b6
    move-result v3

    #@1b7
    move-object/from16 v0, p0

    #@1b9
    iget-object v5, v0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@1bb
    move-object/from16 v0, p0

    #@1bd
    invoke-direct {v0, v5}, Landroid/support/v7/widget/Toolbar;->getVerticalMargins(Landroid/view/View;)I

    #@1c0
    move-result v5

    #@1c1
    add-int/2addr v3, v5

    #@1c2
    move/from16 v0, v25

    #@1c4
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@1c7
    move-result v25

    #@1c8
    .line 1239
    move-object/from16 v0, p0

    #@1ca
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@1cc
    invoke-static {v3}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@1cf
    move-result v3

    #@1d0
    move/from16 v0, v22

    #@1d2
    invoke-static {v0, v3}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@1d5
    move-result v22

    #@1d6
    .line 1243
    :cond_4
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getChildCount()I

    #@1d9
    move-result v21

    #@1da
    .line 1244
    .local v21, "childCount":I
    const/16 v26, 0x0

    #@1dc
    .local v26, "i":I
    :goto_1
    move/from16 v0, v26

    #@1de
    move/from16 v1, v21

    #@1e0
    if-ge v0, v1, :cond_8

    #@1e2
    .line 1245
    move-object/from16 v0, p0

    #@1e4
    move/from16 v1, v26

    #@1e6
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->getChildAt(I)Landroid/view/View;

    #@1e9
    move-result-object v4

    #@1ea
    .line 1246
    .local v4, "child":Landroid/view/View;
    invoke-virtual {v4}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@1ed
    move-result-object v27

    #@1ee
    check-cast v27, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@1f0
    .line 1247
    .local v27, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    move-object/from16 v0, v27

    #@1f2
    iget v3, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->mViewType:I

    #@1f4
    if-nez v3, :cond_5

    #@1f6
    move-object/from16 v0, p0

    #@1f8
    invoke-direct {v0, v4}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@1fb
    move-result v3

    #@1fc
    if-nez v3, :cond_7

    #@1fe
    .line 1244
    :cond_5
    :goto_2
    add-int/lit8 v26, v26, 0x1

    #@200
    goto :goto_1

    #@201
    .line 1178
    .end local v4    # "child":Landroid/view/View;
    .end local v21    # "childCount":I
    .end local v23    # "contentInsetEnd":I
    .end local v24    # "contentInsetStart":I
    .end local v26    # "i":I
    .end local v27    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    .end local v28    # "marginEndIndex":I
    .end local v29    # "marginStartIndex":I
    .end local v32    # "menuWidth":I
    .end local v33    # "navWidth":I
    :cond_6
    const/16 v29, 0x0

    #@203
    .line 1179
    .restart local v29    # "marginStartIndex":I
    const/16 v28, 0x1

    #@205
    .restart local v28    # "marginEndIndex":I
    goto/16 :goto_0

    #@207
    .line 1252
    .restart local v4    # "child":Landroid/view/View;
    .restart local v21    # "childCount":I
    .restart local v23    # "contentInsetEnd":I
    .restart local v24    # "contentInsetStart":I
    .restart local v26    # "i":I
    .restart local v27    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    .restart local v32    # "menuWidth":I
    .restart local v33    # "navWidth":I
    :cond_7
    const/4 v8, 0x0

    #@208
    move-object/from16 v3, p0

    #@20a
    move/from16 v5, p1

    #@20c
    move/from16 v7, p2

    #@20e
    move-object v9, v13

    #@20f
    invoke-direct/range {v3 .. v9}, Landroid/support/v7/widget/Toolbar;->measureChildCollapseMargins(Landroid/view/View;IIII[I)I

    #@212
    move-result v3

    #@213
    add-int/2addr v6, v3

    #@214
    .line 1254
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredHeight()I

    #@217
    move-result v3

    #@218
    move-object/from16 v0, p0

    #@21a
    invoke-direct {v0, v4}, Landroid/support/v7/widget/Toolbar;->getVerticalMargins(Landroid/view/View;)I

    #@21d
    move-result v5

    #@21e
    add-int/2addr v3, v5

    #@21f
    move/from16 v0, v25

    #@221
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@224
    move-result v25

    #@225
    .line 1255
    invoke-static {v4}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@228
    move-result v3

    #@229
    move/from16 v0, v22

    #@22b
    invoke-static {v0, v3}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@22e
    move-result v22

    #@22f
    goto :goto_2

    #@230
    .line 1259
    .end local v4    # "child":Landroid/view/View;
    .end local v27    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_8
    const/16 v36, 0x0

    #@232
    .line 1260
    .local v36, "titleWidth":I
    const/16 v34, 0x0

    #@234
    .line 1261
    .local v34, "titleHeight":I
    move-object/from16 v0, p0

    #@236
    iget v3, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginTop:I

    #@238
    move-object/from16 v0, p0

    #@23a
    iget v5, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginBottom:I

    #@23c
    add-int v12, v3, v5

    #@23e
    .line 1262
    .local v12, "titleVertMargins":I
    move-object/from16 v0, p0

    #@240
    iget v3, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginStart:I

    #@242
    move-object/from16 v0, p0

    #@244
    iget v5, v0, Landroid/support/v7/widget/Toolbar;->mTitleMarginEnd:I

    #@246
    add-int v35, v3, v5

    #@248
    .line 1263
    .local v35, "titleHorizMargins":I
    move-object/from16 v0, p0

    #@24a
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@24c
    move-object/from16 v0, p0

    #@24e
    invoke-direct {v0, v3}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@251
    move-result v3

    #@252
    if-eqz v3, :cond_9

    #@254
    .line 1264
    move-object/from16 v0, p0

    #@256
    iget-object v8, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@258
    add-int v10, v6, v35

    #@25a
    move-object/from16 v7, p0

    #@25c
    move/from16 v9, p1

    #@25e
    move/from16 v11, p2

    #@260
    invoke-direct/range {v7 .. v13}, Landroid/support/v7/widget/Toolbar;->measureChildCollapseMargins(Landroid/view/View;IIII[I)I

    #@263
    move-result v36

    #@264
    .line 1267
    move-object/from16 v0, p0

    #@266
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@268
    invoke-virtual {v3}, Landroid/widget/TextView;->getMeasuredWidth()I

    #@26b
    move-result v3

    #@26c
    move-object/from16 v0, p0

    #@26e
    iget-object v5, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@270
    move-object/from16 v0, p0

    #@272
    invoke-direct {v0, v5}, Landroid/support/v7/widget/Toolbar;->getHorizontalMargins(Landroid/view/View;)I

    #@275
    move-result v5

    #@276
    add-int v36, v3, v5

    #@278
    .line 1268
    move-object/from16 v0, p0

    #@27a
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@27c
    invoke-virtual {v3}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@27f
    move-result v3

    #@280
    move-object/from16 v0, p0

    #@282
    iget-object v5, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@284
    move-object/from16 v0, p0

    #@286
    invoke-direct {v0, v5}, Landroid/support/v7/widget/Toolbar;->getVerticalMargins(Landroid/view/View;)I

    #@289
    move-result v5

    #@28a
    add-int v34, v3, v5

    #@28c
    .line 1269
    move-object/from16 v0, p0

    #@28e
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@290
    invoke-static {v3}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@293
    move-result v3

    #@294
    move/from16 v0, v22

    #@296
    invoke-static {v0, v3}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@299
    move-result v22

    #@29a
    .line 1272
    :cond_9
    move-object/from16 v0, p0

    #@29c
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@29e
    move-object/from16 v0, p0

    #@2a0
    invoke-direct {v0, v3}, Landroid/support/v7/widget/Toolbar;->shouldLayout(Landroid/view/View;)Z

    #@2a3
    move-result v3

    #@2a4
    if-eqz v3, :cond_a

    #@2a6
    .line 1273
    move-object/from16 v0, p0

    #@2a8
    iget-object v15, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@2aa
    add-int v17, v6, v35

    #@2ac
    add-int v19, v34, v12

    #@2ae
    move-object/from16 v14, p0

    #@2b0
    move/from16 v16, p1

    #@2b2
    move/from16 v18, p2

    #@2b4
    move-object/from16 v20, v13

    #@2b6
    invoke-direct/range {v14 .. v20}, Landroid/support/v7/widget/Toolbar;->measureChildCollapseMargins(Landroid/view/View;IIII[I)I

    #@2b9
    move-result v3

    #@2ba
    move/from16 v0, v36

    #@2bc
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@2bf
    move-result v36

    #@2c0
    .line 1277
    move-object/from16 v0, p0

    #@2c2
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@2c4
    invoke-virtual {v3}, Landroid/widget/TextView;->getMeasuredHeight()I

    #@2c7
    move-result v3

    #@2c8
    move-object/from16 v0, p0

    #@2ca
    iget-object v5, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@2cc
    move-object/from16 v0, p0

    #@2ce
    invoke-direct {v0, v5}, Landroid/support/v7/widget/Toolbar;->getVerticalMargins(Landroid/view/View;)I

    #@2d1
    move-result v5

    #@2d2
    add-int/2addr v3, v5

    #@2d3
    add-int v34, v34, v3

    #@2d5
    .line 1279
    move-object/from16 v0, p0

    #@2d7
    iget-object v3, v0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@2d9
    invoke-static {v3}, Landroid/support/v4/view/ViewCompat;->getMeasuredState(Landroid/view/View;)I

    #@2dc
    move-result v3

    #@2dd
    move/from16 v0, v22

    #@2df
    invoke-static {v0, v3}, Landroid/support/v7/internal/widget/ViewUtils;->combineMeasuredStates(II)I

    #@2e2
    move-result v22

    #@2e3
    .line 1283
    :cond_a
    add-int v6, v6, v36

    #@2e5
    .line 1284
    move/from16 v0, v25

    #@2e7
    move/from16 v1, v34

    #@2e9
    invoke-static {v0, v1}, Ljava/lang/Math;->max(II)I

    #@2ec
    move-result v25

    #@2ed
    .line 1288
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getPaddingLeft()I

    #@2f0
    move-result v3

    #@2f1
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getPaddingRight()I

    #@2f4
    move-result v5

    #@2f5
    add-int/2addr v3, v5

    #@2f6
    add-int/2addr v6, v3

    #@2f7
    .line 1289
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getPaddingTop()I

    #@2fa
    move-result v3

    #@2fb
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getPaddingBottom()I

    #@2fe
    move-result v5

    #@2ff
    add-int/2addr v3, v5

    #@300
    add-int v25, v25, v3

    #@302
    .line 1291
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getSuggestedMinimumWidth()I

    #@305
    move-result v3

    #@306
    invoke-static {v6, v3}, Ljava/lang/Math;->max(II)I

    #@309
    move-result v3

    #@30a
    const/high16 v5, -0x1000000

    #@30c
    and-int v5, v5, v22

    #@30e
    move/from16 v0, p1

    #@310
    invoke-static {v3, v0, v5}, Landroid/support/v4/view/ViewCompat;->resolveSizeAndState(III)I

    #@313
    move-result v31

    #@314
    .line 1294
    .local v31, "measuredWidth":I
    invoke-virtual/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->getSuggestedMinimumHeight()I

    #@317
    move-result v3

    #@318
    move/from16 v0, v25

    #@31a
    invoke-static {v0, v3}, Ljava/lang/Math;->max(II)I

    #@31d
    move-result v3

    #@31e
    shl-int/lit8 v5, v22, 0x10

    #@320
    move/from16 v0, p2

    #@322
    invoke-static {v3, v0, v5}, Landroid/support/v4/view/ViewCompat;->resolveSizeAndState(III)I

    #@325
    move-result v30

    #@326
    .line 1298
    .local v30, "measuredHeight":I
    invoke-direct/range {p0 .. p0}, Landroid/support/v7/widget/Toolbar;->shouldCollapse()Z

    #@329
    move-result v3

    #@32a
    if-eqz v3, :cond_b

    #@32c
    const/16 v30, 0x0

    #@32e
    .end local v30    # "measuredHeight":I
    :cond_b
    move-object/from16 v0, p0

    #@330
    move/from16 v1, v31

    #@332
    move/from16 v2, v30

    #@334
    invoke-virtual {v0, v1, v2}, Landroid/support/v7/widget/Toolbar;->setMeasuredDimension(II)V

    #@337
    .line 1299
    return-void
.end method

.method protected onRestoreInstanceState(Landroid/os/Parcelable;)V
    .locals 4
    .param p1, "state"    # Landroid/os/Parcelable;

    #@0
    .prologue
    .line 1048
    move-object v2, p1

    #@1
    check-cast v2, Landroid/support/v7/widget/Toolbar$SavedState;

    #@3
    .line 1049
    .local v2, "ss":Landroid/support/v7/widget/Toolbar$SavedState;
    invoke-virtual {v2}, Landroid/support/v7/widget/Toolbar$SavedState;->getSuperState()Landroid/os/Parcelable;

    #@6
    move-result-object v3

    #@7
    invoke-super {p0, v3}, Landroid/view/ViewGroup;->onRestoreInstanceState(Landroid/os/Parcelable;)V

    #@a
    .line 1051
    iget-object v3, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@c
    if-eqz v3, :cond_2

    #@e
    iget-object v3, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@10
    invoke-virtual {v3}, Landroid/support/v7/widget/ActionMenuView;->peekMenu()Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@13
    move-result-object v1

    #@14
    .line 1052
    .local v1, "menu":Landroid/view/Menu;
    :goto_0
    iget v3, v2, Landroid/support/v7/widget/Toolbar$SavedState;->expandedMenuItemId:I

    #@16
    if-eqz v3, :cond_0

    #@18
    iget-object v3, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@1a
    if-eqz v3, :cond_0

    #@1c
    if-eqz v1, :cond_0

    #@1e
    .line 1053
    iget v3, v2, Landroid/support/v7/widget/Toolbar$SavedState;->expandedMenuItemId:I

    #@20
    invoke-interface {v1, v3}, Landroid/view/Menu;->findItem(I)Landroid/view/MenuItem;

    #@23
    move-result-object v0

    #@24
    .line 1054
    .local v0, "item":Landroid/view/MenuItem;
    if-eqz v0, :cond_0

    #@26
    .line 1055
    invoke-static {v0}, Landroid/support/v4/view/MenuItemCompat;->expandActionView(Landroid/view/MenuItem;)Z

    #@29
    .line 1059
    .end local v0    # "item":Landroid/view/MenuItem;
    :cond_0
    iget-boolean v3, v2, Landroid/support/v7/widget/Toolbar$SavedState;->isOverflowOpen:Z

    #@2b
    if-eqz v3, :cond_1

    #@2d
    .line 1060
    invoke-direct {p0}, Landroid/support/v7/widget/Toolbar;->postShowOverflowMenu()V

    #@30
    .line 1062
    :cond_1
    return-void

    #@31
    .line 1051
    .end local v1    # "menu":Landroid/view/Menu;
    :cond_2
    const/4 v1, 0x0

    #@32
    goto :goto_0
.end method

.method public onRtlPropertiesChanged(I)V
    .locals 3
    .param p1, "layoutDirection"    # I

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 311
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@3
    const/16 v2, 0x11

    #@5
    if-lt v1, v2, :cond_0

    #@7
    .line 312
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->onRtlPropertiesChanged(I)V

    #@a
    .line 314
    :cond_0
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mContentInsets:Landroid/support/v7/internal/widget/RtlSpacingHelper;

    #@c
    if-ne p1, v0, :cond_1

    #@e
    :goto_0
    invoke-virtual {v1, v0}, Landroid/support/v7/internal/widget/RtlSpacingHelper;->setDirection(Z)V

    #@11
    .line 315
    return-void

    #@12
    .line 314
    :cond_1
    const/4 v0, 0x0

    #@13
    goto :goto_0
.end method

.method protected onSaveInstanceState()Landroid/os/Parcelable;
    .locals 2

    #@0
    .prologue
    .line 1036
    new-instance v0, Landroid/support/v7/widget/Toolbar$SavedState;

    #@2
    invoke-super {p0}, Landroid/view/ViewGroup;->onSaveInstanceState()Landroid/os/Parcelable;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1}, Landroid/support/v7/widget/Toolbar$SavedState;-><init>(Landroid/os/Parcelable;)V

    #@9
    .line 1038
    .local v0, "state":Landroid/support/v7/widget/Toolbar$SavedState;
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@b
    if-eqz v1, :cond_0

    #@d
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@f
    iget-object v1, v1, Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;->mCurrentExpandedItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@11
    if-eqz v1, :cond_0

    #@13
    .line 1039
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@15
    iget-object v1, v1, Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;->mCurrentExpandedItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@17
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getItemId()I

    #@1a
    move-result v1

    #@1b
    iput v1, v0, Landroid/support/v7/widget/Toolbar$SavedState;->expandedMenuItemId:I

    #@1d
    .line 1042
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->isOverflowMenuShowing()Z

    #@20
    move-result v1

    #@21
    iput-boolean v1, v0, Landroid/support/v7/widget/Toolbar$SavedState;->isOverflowOpen:Z

    #@23
    .line 1043
    return-object v0
.end method

.method public onTouchEvent(Landroid/view/MotionEvent;)Z
    .locals 5
    .param p1, "ev"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    const/4 v3, 0x1

    #@2
    .line 1082
    invoke-static {p1}, Landroid/support/v4/view/MotionEventCompat;->getActionMasked(Landroid/view/MotionEvent;)I

    #@5
    move-result v0

    #@6
    .line 1083
    .local v0, "action":I
    if-nez v0, :cond_0

    #@8
    .line 1084
    iput-boolean v4, p0, Landroid/support/v7/widget/Toolbar;->mEatingTouch:Z

    #@a
    .line 1087
    :cond_0
    iget-boolean v2, p0, Landroid/support/v7/widget/Toolbar;->mEatingTouch:Z

    #@c
    if-nez v2, :cond_1

    #@e
    .line 1088
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->onTouchEvent(Landroid/view/MotionEvent;)Z

    #@11
    move-result v1

    #@12
    .line 1089
    .local v1, "handled":Z
    if-nez v0, :cond_1

    #@14
    if-nez v1, :cond_1

    #@16
    .line 1090
    iput-boolean v3, p0, Landroid/support/v7/widget/Toolbar;->mEatingTouch:Z

    #@18
    .line 1094
    .end local v1    # "handled":Z
    :cond_1
    if-eq v0, v3, :cond_2

    #@1a
    const/4 v2, 0x3

    #@1b
    if-ne v0, v2, :cond_3

    #@1d
    .line 1095
    :cond_2
    iput-boolean v4, p0, Landroid/support/v7/widget/Toolbar;->mEatingTouch:Z

    #@1f
    .line 1098
    :cond_3
    return v3
.end method

.method public setCollapsible(Z)V
    .locals 0
    .param p1, "collapsible"    # Z

    #@0
    .prologue
    .line 1735
    iput-boolean p1, p0, Landroid/support/v7/widget/Toolbar;->mCollapsible:Z

    #@2
    .line 1736
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->requestLayout()V

    #@5
    .line 1737
    return-void
.end method

.method public setContentInsetsAbsolute(II)V
    .locals 1
    .param p1, "contentInsetLeft"    # I
    .param p2, "contentInsetRight"    # I

    #@0
    .prologue
    .line 950
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mContentInsets:Landroid/support/v7/internal/widget/RtlSpacingHelper;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/internal/widget/RtlSpacingHelper;->setAbsolute(II)V

    #@5
    .line 951
    return-void
.end method

.method public setContentInsetsRelative(II)V
    .locals 1
    .param p1, "contentInsetStart"    # I
    .param p2, "contentInsetEnd"    # I

    #@0
    .prologue
    .line 892
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mContentInsets:Landroid/support/v7/internal/widget/RtlSpacingHelper;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/support/v7/internal/widget/RtlSpacingHelper;->setRelative(II)V

    #@5
    .line 893
    return-void
.end method

.method public setLogo(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 327
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/Toolbar;->setLogo(Landroid/graphics/drawable/Drawable;)V

    #@9
    .line 328
    return-void
.end method

.method public setLogo(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "drawable"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 443
    if-eqz p1, :cond_2

    #@2
    .line 444
    invoke-direct {p0}, Landroid/support/v7/widget/Toolbar;->ensureLogoView()V

    #@5
    .line 445
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@7
    invoke-virtual {v0}, Landroid/widget/ImageView;->getParent()Landroid/view/ViewParent;

    #@a
    move-result-object v0

    #@b
    if-nez v0, :cond_0

    #@d
    .line 446
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@f
    invoke-direct {p0, v0}, Landroid/support/v7/widget/Toolbar;->addSystemView(Landroid/view/View;)V

    #@12
    .line 447
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@14
    invoke-direct {p0, v0}, Landroid/support/v7/widget/Toolbar;->updateChildVisibilityForExpandedActionView(Landroid/view/View;)V

    #@17
    .line 452
    :cond_0
    :goto_0
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@19
    if-eqz v0, :cond_1

    #@1b
    .line 453
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@1d
    invoke-virtual {v0, p1}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@20
    .line 455
    :cond_1
    return-void

    #@21
    .line 449
    :cond_2
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@23
    if-eqz v0, :cond_0

    #@25
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@27
    invoke-virtual {v0}, Landroid/widget/ImageView;->getParent()Landroid/view/ViewParent;

    #@2a
    move-result-object v0

    #@2b
    if-eqz v0, :cond_0

    #@2d
    .line 450
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@2f
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/Toolbar;->removeView(Landroid/view/View;)V

    #@32
    goto :goto_0
.end method

.method public setLogoDescription(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 477
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/content/Context;->getText(I)Ljava/lang/CharSequence;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/Toolbar;->setLogoDescription(Ljava/lang/CharSequence;)V

    #@b
    .line 478
    return-void
.end method

.method public setLogoDescription(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "description"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 489
    invoke-static {p1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    .line 490
    invoke-direct {p0}, Landroid/support/v7/widget/Toolbar;->ensureLogoView()V

    #@9
    .line 492
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@b
    if-eqz v0, :cond_1

    #@d
    .line 493
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mLogoView:Landroid/widget/ImageView;

    #@f
    invoke-virtual {v0, p1}, Landroid/widget/ImageView;->setContentDescription(Ljava/lang/CharSequence;)V

    #@12
    .line 495
    :cond_1
    return-void
.end method

.method public setMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/support/v7/widget/ActionMenuPresenter;)V
    .locals 5
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "outerPresenter"    # Landroid/support/v7/widget/ActionMenuPresenter;

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    const/4 v3, 0x1

    #@2
    .line 370
    if-nez p1, :cond_1

    #@4
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@6
    if-nez v1, :cond_1

    #@8
    .line 402
    :cond_0
    :goto_0
    return-void

    #@9
    .line 374
    :cond_1
    invoke-direct {p0}, Landroid/support/v7/widget/Toolbar;->ensureMenuView()V

    #@c
    .line 375
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@e
    invoke-virtual {v1}, Landroid/support/v7/widget/ActionMenuView;->peekMenu()Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@11
    move-result-object v0

    #@12
    .line 376
    .local v0, "oldMenu":Landroid/support/v7/internal/view/menu/MenuBuilder;
    if-eq v0, p1, :cond_0

    #@14
    .line 380
    if-eqz v0, :cond_2

    #@16
    .line 381
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mOuterActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@18
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->removeMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;)V

    #@1b
    .line 382
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@1d
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->removeMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;)V

    #@20
    .line 385
    :cond_2
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@22
    if-nez v1, :cond_3

    #@24
    .line 386
    new-instance v1, Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@26
    invoke-direct {v1, p0, v4}, Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;-><init>(Landroid/support/v7/widget/Toolbar;Landroid/support/v7/widget/Toolbar$1;)V

    #@29
    iput-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@2b
    .line 389
    :cond_3
    invoke-virtual {p2, v3}, Landroid/support/v7/widget/ActionMenuPresenter;->setExpandedActionViewsExclusive(Z)V

    #@2e
    .line 390
    if-eqz p1, :cond_4

    #@30
    .line 391
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mPopupContext:Landroid/content/Context;

    #@32
    invoke-virtual {p1, p2, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;Landroid/content/Context;)V

    #@35
    .line 392
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@37
    iget-object v2, p0, Landroid/support/v7/widget/Toolbar;->mPopupContext:Landroid/content/Context;

    #@39
    invoke-virtual {p1, v1, v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;Landroid/content/Context;)V

    #@3c
    .line 399
    :goto_1
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@3e
    iget v2, p0, Landroid/support/v7/widget/Toolbar;->mPopupTheme:I

    #@40
    invoke-virtual {v1, v2}, Landroid/support/v7/widget/ActionMenuView;->setPopupTheme(I)V

    #@43
    .line 400
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@45
    invoke-virtual {v1, p2}, Landroid/support/v7/widget/ActionMenuView;->setPresenter(Landroid/support/v7/widget/ActionMenuPresenter;)V

    #@48
    .line 401
    iput-object p2, p0, Landroid/support/v7/widget/Toolbar;->mOuterActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@4a
    goto :goto_0

    #@4b
    .line 394
    :cond_4
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mPopupContext:Landroid/content/Context;

    #@4d
    invoke-virtual {p2, v1, v4}, Landroid/support/v7/widget/ActionMenuPresenter;->initForMenu(Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@50
    .line 395
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@52
    iget-object v2, p0, Landroid/support/v7/widget/Toolbar;->mPopupContext:Landroid/content/Context;

    #@54
    invoke-virtual {v1, v2, v4}, Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;->initForMenu(Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@57
    .line 396
    invoke-virtual {p2, v3}, Landroid/support/v7/widget/ActionMenuPresenter;->updateMenuView(Z)V

    #@5a
    .line 397
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mExpandedMenuPresenter:Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;

    #@5c
    invoke-virtual {v1, v3}, Landroid/support/v7/widget/Toolbar$ExpandedActionViewMenuPresenter;->updateMenuView(Z)V

    #@5f
    goto :goto_1
.end method

.method public setMenuCallbacks(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;)V
    .locals 0
    .param p1, "pcb"    # Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;
    .param p2, "mcb"    # Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

    #@0
    .prologue
    .line 1744
    iput-object p1, p0, Landroid/support/v7/widget/Toolbar;->mActionMenuPresenterCallback:Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@2
    .line 1745
    iput-object p2, p0, Landroid/support/v7/widget/Toolbar;->mMenuBuilderCallback:Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

    #@4
    .line 1746
    return-void
.end method

.method public setMinimumHeight(I)V
    .locals 0
    .param p1, "minHeight"    # I

    #@0
    .prologue
    .line 1751
    iput p1, p0, Landroid/support/v7/widget/Toolbar;->mMinHeight:I

    #@2
    .line 1753
    invoke-super {p0, p1}, Landroid/view/ViewGroup;->setMinimumHeight(I)V

    #@5
    .line 1754
    return-void
.end method

.method public setNavigationContentDescription(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 721
    if-eqz p1, :cond_0

    #@2
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0, p1}, Landroid/content/Context;->getText(I)Ljava/lang/CharSequence;

    #@9
    move-result-object v0

    #@a
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/Toolbar;->setNavigationContentDescription(Ljava/lang/CharSequence;)V

    #@d
    .line 722
    return-void

    #@e
    .line 721
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public setNavigationContentDescription(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "description"    # Ljava/lang/CharSequence;
        .annotation build Landroid/support/annotation/Nullable;
        .end annotation
    .end param

    #@0
    .prologue
    .line 733
    invoke-static {p1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@3
    move-result v0

    #@4
    if-nez v0, :cond_0

    #@6
    .line 734
    invoke-direct {p0}, Landroid/support/v7/widget/Toolbar;->ensureNavButtonView()V

    #@9
    .line 736
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@b
    if-eqz v0, :cond_1

    #@d
    .line 737
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@f
    invoke-virtual {v0, p1}, Landroid/widget/ImageButton;->setContentDescription(Ljava/lang/CharSequence;)V

    #@12
    .line 739
    :cond_1
    return-void
.end method

.method public setNavigationIcon(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 754
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/Toolbar;->setNavigationIcon(Landroid/graphics/drawable/Drawable;)V

    #@9
    .line 755
    return-void
.end method

.method public setNavigationIcon(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "icon"    # Landroid/graphics/drawable/Drawable;
        .annotation build Landroid/support/annotation/Nullable;
        .end annotation
    .end param

    #@0
    .prologue
    .line 770
    if-eqz p1, :cond_2

    #@2
    .line 771
    invoke-direct {p0}, Landroid/support/v7/widget/Toolbar;->ensureNavButtonView()V

    #@5
    .line 772
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@7
    invoke-virtual {v0}, Landroid/widget/ImageButton;->getParent()Landroid/view/ViewParent;

    #@a
    move-result-object v0

    #@b
    if-nez v0, :cond_0

    #@d
    .line 773
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@f
    invoke-direct {p0, v0}, Landroid/support/v7/widget/Toolbar;->addSystemView(Landroid/view/View;)V

    #@12
    .line 774
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@14
    invoke-direct {p0, v0}, Landroid/support/v7/widget/Toolbar;->updateChildVisibilityForExpandedActionView(Landroid/view/View;)V

    #@17
    .line 779
    :cond_0
    :goto_0
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@19
    if-eqz v0, :cond_1

    #@1b
    .line 780
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@1d
    invoke-virtual {v0, p1}, Landroid/widget/ImageButton;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@20
    .line 782
    :cond_1
    return-void

    #@21
    .line 776
    :cond_2
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@23
    if-eqz v0, :cond_0

    #@25
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@27
    invoke-virtual {v0}, Landroid/widget/ImageButton;->getParent()Landroid/view/ViewParent;

    #@2a
    move-result-object v0

    #@2b
    if-eqz v0, :cond_0

    #@2d
    .line 777
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@2f
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/Toolbar;->removeView(Landroid/view/View;)V

    #@32
    goto :goto_0
.end method

.method public setNavigationOnClickListener(Landroid/view/View$OnClickListener;)V
    .locals 1
    .param p1, "listener"    # Landroid/view/View$OnClickListener;

    #@0
    .prologue
    .line 804
    invoke-direct {p0}, Landroid/support/v7/widget/Toolbar;->ensureNavButtonView()V

    #@3
    .line 805
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mNavButtonView:Landroid/widget/ImageButton;

    #@5
    invoke-virtual {v0, p1}, Landroid/widget/ImageButton;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    #@8
    .line 806
    return-void
.end method

.method public setOnMenuItemClickListener(Landroid/support/v7/widget/Toolbar$OnMenuItemClickListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v7/widget/Toolbar$OnMenuItemClickListener;

    #@0
    .prologue
    .line 872
    iput-object p1, p0, Landroid/support/v7/widget/Toolbar;->mOnMenuItemClickListener:Landroid/support/v7/widget/Toolbar$OnMenuItemClickListener;

    #@2
    .line 873
    return-void
.end method

.method public setPopupTheme(I)V
    .locals 2
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 291
    iget v0, p0, Landroid/support/v7/widget/Toolbar;->mPopupTheme:I

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 292
    iput p1, p0, Landroid/support/v7/widget/Toolbar;->mPopupTheme:I

    #@6
    .line 293
    if-nez p1, :cond_1

    #@8
    .line 294
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@b
    move-result-object v0

    #@c
    iput-object v0, p0, Landroid/support/v7/widget/Toolbar;->mPopupContext:Landroid/content/Context;

    #@e
    .line 299
    :cond_0
    :goto_0
    return-void

    #@f
    .line 296
    :cond_1
    new-instance v0, Landroid/view/ContextThemeWrapper;

    #@11
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@14
    move-result-object v1

    #@15
    invoke-direct {v0, v1, p1}, Landroid/view/ContextThemeWrapper;-><init>(Landroid/content/Context;I)V

    #@18
    iput-object v0, p0, Landroid/support/v7/widget/Toolbar;->mPopupContext:Landroid/content/Context;

    #@1a
    goto :goto_0
.end method

.method public setSubtitle(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 617
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/content/Context;->getText(I)Ljava/lang/CharSequence;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/Toolbar;->setSubtitle(Ljava/lang/CharSequence;)V

    #@b
    .line 618
    return-void
.end method

.method public setSubtitle(Ljava/lang/CharSequence;)V
    .locals 3
    .param p1, "subtitle"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 628
    invoke-static {p1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@3
    move-result v1

    #@4
    if-nez v1, :cond_4

    #@6
    .line 629
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@8
    if-nez v1, :cond_1

    #@a
    .line 630
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@d
    move-result-object v0

    #@e
    .line 631
    .local v0, "context":Landroid/content/Context;
    new-instance v1, Landroid/widget/TextView;

    #@10
    invoke-direct {v1, v0}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    #@13
    iput-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@15
    .line 632
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@17
    invoke-virtual {v1}, Landroid/widget/TextView;->setSingleLine()V

    #@1a
    .line 633
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@1c
    sget-object v2, Landroid/text/TextUtils$TruncateAt;->END:Landroid/text/TextUtils$TruncateAt;

    #@1e
    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setEllipsize(Landroid/text/TextUtils$TruncateAt;)V

    #@21
    .line 634
    iget v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextAppearance:I

    #@23
    if-eqz v1, :cond_0

    #@25
    .line 635
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@27
    iget v2, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextAppearance:I

    #@29
    invoke-virtual {v1, v0, v2}, Landroid/widget/TextView;->setTextAppearance(Landroid/content/Context;I)V

    #@2c
    .line 637
    :cond_0
    iget v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextColor:I

    #@2e
    if-eqz v1, :cond_1

    #@30
    .line 638
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@32
    iget v2, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextColor:I

    #@34
    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setTextColor(I)V

    #@37
    .line 641
    .end local v0    # "context":Landroid/content/Context;
    :cond_1
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@39
    invoke-virtual {v1}, Landroid/widget/TextView;->getParent()Landroid/view/ViewParent;

    #@3c
    move-result-object v1

    #@3d
    if-nez v1, :cond_2

    #@3f
    .line 642
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@41
    invoke-direct {p0, v1}, Landroid/support/v7/widget/Toolbar;->addSystemView(Landroid/view/View;)V

    #@44
    .line 643
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@46
    invoke-direct {p0, v1}, Landroid/support/v7/widget/Toolbar;->updateChildVisibilityForExpandedActionView(Landroid/view/View;)V

    #@49
    .line 648
    :cond_2
    :goto_0
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@4b
    if-eqz v1, :cond_3

    #@4d
    .line 649
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@4f
    invoke-virtual {v1, p1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@52
    .line 651
    :cond_3
    iput-object p1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleText:Ljava/lang/CharSequence;

    #@54
    .line 652
    return-void

    #@55
    .line 645
    :cond_4
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@57
    if-eqz v1, :cond_2

    #@59
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@5b
    invoke-virtual {v1}, Landroid/widget/TextView;->getParent()Landroid/view/ViewParent;

    #@5e
    move-result-object v1

    #@5f
    if-eqz v1, :cond_2

    #@61
    .line 646
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@63
    invoke-virtual {p0, v1}, Landroid/support/v7/widget/Toolbar;->removeView(Landroid/view/View;)V

    #@66
    goto :goto_0
.end method

.method public setSubtitleTextAppearance(Landroid/content/Context;I)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "resId"    # I

    #@0
    .prologue
    .line 670
    iput p2, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextAppearance:I

    #@2
    .line 671
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 672
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@8
    invoke-virtual {v0, p1, p2}, Landroid/widget/TextView;->setTextAppearance(Landroid/content/Context;I)V

    #@b
    .line 674
    :cond_0
    return-void
.end method

.method public setSubtitleTextColor(I)V
    .locals 1
    .param p1, "color"    # I

    #@0
    .prologue
    .line 694
    iput p1, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextColor:I

    #@2
    .line 695
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 696
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mSubtitleTextView:Landroid/widget/TextView;

    #@8
    invoke-virtual {v0, p1}, Landroid/widget/TextView;->setTextColor(I)V

    #@b
    .line 698
    :cond_0
    return-void
.end method

.method public setTitle(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 562
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0, p1}, Landroid/content/Context;->getText(I)Ljava/lang/CharSequence;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/Toolbar;->setTitle(Ljava/lang/CharSequence;)V

    #@b
    .line 563
    return-void
.end method

.method public setTitle(Ljava/lang/CharSequence;)V
    .locals 3
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 574
    invoke-static {p1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@3
    move-result v1

    #@4
    if-nez v1, :cond_4

    #@6
    .line 575
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@8
    if-nez v1, :cond_1

    #@a
    .line 576
    invoke-virtual {p0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@d
    move-result-object v0

    #@e
    .line 577
    .local v0, "context":Landroid/content/Context;
    new-instance v1, Landroid/widget/TextView;

    #@10
    invoke-direct {v1, v0}, Landroid/widget/TextView;-><init>(Landroid/content/Context;)V

    #@13
    iput-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@15
    .line 578
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@17
    invoke-virtual {v1}, Landroid/widget/TextView;->setSingleLine()V

    #@1a
    .line 579
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@1c
    sget-object v2, Landroid/text/TextUtils$TruncateAt;->END:Landroid/text/TextUtils$TruncateAt;

    #@1e
    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setEllipsize(Landroid/text/TextUtils$TruncateAt;)V

    #@21
    .line 580
    iget v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextAppearance:I

    #@23
    if-eqz v1, :cond_0

    #@25
    .line 581
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@27
    iget v2, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextAppearance:I

    #@29
    invoke-virtual {v1, v0, v2}, Landroid/widget/TextView;->setTextAppearance(Landroid/content/Context;I)V

    #@2c
    .line 583
    :cond_0
    iget v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextColor:I

    #@2e
    if-eqz v1, :cond_1

    #@30
    .line 584
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@32
    iget v2, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextColor:I

    #@34
    invoke-virtual {v1, v2}, Landroid/widget/TextView;->setTextColor(I)V

    #@37
    .line 587
    .end local v0    # "context":Landroid/content/Context;
    :cond_1
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@39
    invoke-virtual {v1}, Landroid/widget/TextView;->getParent()Landroid/view/ViewParent;

    #@3c
    move-result-object v1

    #@3d
    if-nez v1, :cond_2

    #@3f
    .line 588
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@41
    invoke-direct {p0, v1}, Landroid/support/v7/widget/Toolbar;->addSystemView(Landroid/view/View;)V

    #@44
    .line 589
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@46
    invoke-direct {p0, v1}, Landroid/support/v7/widget/Toolbar;->updateChildVisibilityForExpandedActionView(Landroid/view/View;)V

    #@49
    .line 594
    :cond_2
    :goto_0
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@4b
    if-eqz v1, :cond_3

    #@4d
    .line 595
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@4f
    invoke-virtual {v1, p1}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@52
    .line 597
    :cond_3
    iput-object p1, p0, Landroid/support/v7/widget/Toolbar;->mTitleText:Ljava/lang/CharSequence;

    #@54
    .line 598
    return-void

    #@55
    .line 591
    :cond_4
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@57
    if-eqz v1, :cond_2

    #@59
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@5b
    invoke-virtual {v1}, Landroid/widget/TextView;->getParent()Landroid/view/ViewParent;

    #@5e
    move-result-object v1

    #@5f
    if-eqz v1, :cond_2

    #@61
    .line 592
    iget-object v1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@63
    invoke-virtual {p0, v1}, Landroid/support/v7/widget/Toolbar;->removeView(Landroid/view/View;)V

    #@66
    goto :goto_0
.end method

.method public setTitleTextAppearance(Landroid/content/Context;I)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "resId"    # I

    #@0
    .prologue
    .line 659
    iput p2, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextAppearance:I

    #@2
    .line 660
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 661
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@8
    invoke-virtual {v0, p1, p2}, Landroid/widget/TextView;->setTextAppearance(Landroid/content/Context;I)V

    #@b
    .line 663
    :cond_0
    return-void
.end method

.method public setTitleTextColor(I)V
    .locals 1
    .param p1, "color"    # I

    #@0
    .prologue
    .line 682
    iput p1, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextColor:I

    #@2
    .line 683
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 684
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mTitleTextView:Landroid/widget/TextView;

    #@8
    invoke-virtual {v0, p1}, Landroid/widget/TextView;->setTextColor(I)V

    #@b
    .line 686
    :cond_0
    return-void
.end method

.method public showOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 356
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/Toolbar;->mMenuView:Landroid/support/v7/widget/ActionMenuView;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ActionMenuView;->showOverflowMenu()Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method
