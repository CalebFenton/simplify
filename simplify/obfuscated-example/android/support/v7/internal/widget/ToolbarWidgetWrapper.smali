.class public Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;
.super Ljava/lang/Object;
.source "ToolbarWidgetWrapper.java"

# interfaces
.implements Landroid/support/v7/internal/widget/DecorToolbar;


# static fields
.field private static final AFFECTS_LOGO_MASK:I = 0x3

.field private static final TAG:Ljava/lang/String; = "ToolbarWidgetWrapper"


# instance fields
.field private mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

.field private mCustomView:Landroid/view/View;

.field private mDefaultNavigationContentDescription:I

.field private mDefaultNavigationIcon:Landroid/graphics/drawable/Drawable;

.field private mDisplayOpts:I

.field private mHomeDescription:Ljava/lang/CharSequence;

.field private mIcon:Landroid/graphics/drawable/Drawable;

.field private mLogo:Landroid/graphics/drawable/Drawable;

.field private mMenuPrepared:Z

.field private mNavIcon:Landroid/graphics/drawable/Drawable;

.field private mNavigationMode:I

.field private mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

.field private mSubtitle:Ljava/lang/CharSequence;

.field private mTabView:Landroid/view/View;

.field private final mTintManager:Landroid/support/v7/internal/widget/TintManager;

.field private mTitle:Ljava/lang/CharSequence;

.field private mTitleSet:Z

.field private mToolbar:Landroid/support/v7/widget/Toolbar;

.field private mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;


# direct methods
.method public constructor <init>(Landroid/support/v7/widget/Toolbar;Z)V
    .locals 2
    .param p1, "toolbar"    # Landroid/support/v7/widget/Toolbar;
    .param p2, "style"    # Z

    #@0
    .prologue
    .line 88
    sget v0, Landroid/support/v7/appcompat/R$string;->abc_action_bar_up_description:I

    #@2
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_ic_ab_back_mtrl_am_alpha:I

    #@4
    invoke-direct {p0, p1, p2, v0, v1}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;-><init>(Landroid/support/v7/widget/Toolbar;ZII)V

    #@7
    .line 90
    return-void
.end method

.method public constructor <init>(Landroid/support/v7/widget/Toolbar;ZII)V
    .locals 22
    .param p1, "toolbar"    # Landroid/support/v7/widget/Toolbar;
    .param p2, "style"    # Z
    .param p3, "defaultNavigationContentDescription"    # I
    .param p4, "defaultNavigationIcon"    # I

    #@0
    .prologue
    .line 93
    invoke-direct/range {p0 .. p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 81
    const/16 v17, 0x0

    #@5
    move/from16 v0, v17

    #@7
    move-object/from16 v1, p0

    #@9
    iput v0, v1, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mNavigationMode:I

    #@b
    .line 84
    const/16 v17, 0x0

    #@d
    move/from16 v0, v17

    #@f
    move-object/from16 v1, p0

    #@11
    iput v0, v1, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDefaultNavigationContentDescription:I

    #@13
    .line 94
    move-object/from16 v0, p1

    #@15
    move-object/from16 v1, p0

    #@17
    iput-object v0, v1, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@19
    .line 95
    invoke-virtual/range {p1 .. p1}, Landroid/support/v7/widget/Toolbar;->getTitle()Ljava/lang/CharSequence;

    #@1c
    move-result-object v17

    #@1d
    move-object/from16 v0, v17

    #@1f
    move-object/from16 v1, p0

    #@21
    iput-object v0, v1, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTitle:Ljava/lang/CharSequence;

    #@23
    .line 96
    invoke-virtual/range {p1 .. p1}, Landroid/support/v7/widget/Toolbar;->getSubtitle()Ljava/lang/CharSequence;

    #@26
    move-result-object v17

    #@27
    move-object/from16 v0, v17

    #@29
    move-object/from16 v1, p0

    #@2b
    iput-object v0, v1, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSubtitle:Ljava/lang/CharSequence;

    #@2d
    .line 97
    move-object/from16 v0, p0

    #@2f
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTitle:Ljava/lang/CharSequence;

    #@31
    move-object/from16 v17, v0

    #@33
    if-eqz v17, :cond_c

    #@35
    const/16 v17, 0x1

    #@37
    :goto_0
    move/from16 v0, v17

    #@39
    move-object/from16 v1, p0

    #@3b
    iput-boolean v0, v1, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTitleSet:Z

    #@3d
    .line 99
    if-eqz p2, :cond_d

    #@3f
    .line 100
    invoke-virtual/range {p1 .. p1}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@42
    move-result-object v17

    #@43
    const/16 v18, 0x0

    #@45
    sget-object v19, Landroid/support/v7/appcompat/R$styleable;->ActionBar:[I

    #@47
    sget v20, Landroid/support/v7/appcompat/R$attr;->actionBarStyle:I

    #@49
    const/16 v21, 0x0

    #@4b
    invoke-static/range {v17 .. v21}, Landroid/support/v7/internal/widget/TintTypedArray;->obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/internal/widget/TintTypedArray;

    #@4e
    move-result-object v3

    #@4f
    .line 103
    .local v3, "a":Landroid/support/v7/internal/widget/TintTypedArray;
    sget v17, Landroid/support/v7/appcompat/R$styleable;->ActionBar_title:I

    #@51
    move/from16 v0, v17

    #@53
    invoke-virtual {v3, v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getText(I)Ljava/lang/CharSequence;

    #@56
    move-result-object v15

    #@57
    .line 104
    .local v15, "title":Ljava/lang/CharSequence;
    invoke-static {v15}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@5a
    move-result v17

    #@5b
    if-nez v17, :cond_0

    #@5d
    .line 105
    move-object/from16 v0, p0

    #@5f
    invoke-virtual {v0, v15}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setTitle(Ljava/lang/CharSequence;)V

    #@62
    .line 108
    :cond_0
    sget v17, Landroid/support/v7/appcompat/R$styleable;->ActionBar_subtitle:I

    #@64
    move/from16 v0, v17

    #@66
    invoke-virtual {v3, v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getText(I)Ljava/lang/CharSequence;

    #@69
    move-result-object v13

    #@6a
    .line 109
    .local v13, "subtitle":Ljava/lang/CharSequence;
    invoke-static {v13}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@6d
    move-result v17

    #@6e
    if-nez v17, :cond_1

    #@70
    .line 110
    move-object/from16 v0, p0

    #@72
    invoke-virtual {v0, v13}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setSubtitle(Ljava/lang/CharSequence;)V

    #@75
    .line 113
    :cond_1
    sget v17, Landroid/support/v7/appcompat/R$styleable;->ActionBar_logo:I

    #@77
    move/from16 v0, v17

    #@79
    invoke-virtual {v3, v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@7c
    move-result-object v9

    #@7d
    .line 114
    .local v9, "logo":Landroid/graphics/drawable/Drawable;
    if-eqz v9, :cond_2

    #@7f
    .line 115
    move-object/from16 v0, p0

    #@81
    invoke-virtual {v0, v9}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setLogo(Landroid/graphics/drawable/Drawable;)V

    #@84
    .line 118
    :cond_2
    sget v17, Landroid/support/v7/appcompat/R$styleable;->ActionBar_icon:I

    #@86
    move/from16 v0, v17

    #@88
    invoke-virtual {v3, v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@8b
    move-result-object v8

    #@8c
    .line 119
    .local v8, "icon":Landroid/graphics/drawable/Drawable;
    if-eqz v8, :cond_3

    #@8e
    .line 120
    move-object/from16 v0, p0

    #@90
    invoke-virtual {v0, v8}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setIcon(Landroid/graphics/drawable/Drawable;)V

    #@93
    .line 123
    :cond_3
    sget v17, Landroid/support/v7/appcompat/R$styleable;->ActionBar_homeAsUpIndicator:I

    #@95
    move/from16 v0, v17

    #@97
    invoke-virtual {v3, v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@9a
    move-result-object v11

    #@9b
    .line 124
    .local v11, "navIcon":Landroid/graphics/drawable/Drawable;
    if-eqz v11, :cond_4

    #@9d
    .line 125
    move-object/from16 v0, p0

    #@9f
    invoke-virtual {v0, v11}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setNavigationIcon(Landroid/graphics/drawable/Drawable;)V

    #@a2
    .line 128
    :cond_4
    sget v17, Landroid/support/v7/appcompat/R$styleable;->ActionBar_displayOptions:I

    #@a4
    const/16 v18, 0x0

    #@a6
    move/from16 v0, v17

    #@a8
    move/from16 v1, v18

    #@aa
    invoke-virtual {v3, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getInt(II)I

    #@ad
    move-result v17

    #@ae
    move-object/from16 v0, p0

    #@b0
    move/from16 v1, v17

    #@b2
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setDisplayOptions(I)V

    #@b5
    .line 130
    sget v17, Landroid/support/v7/appcompat/R$styleable;->ActionBar_customNavigationLayout:I

    #@b7
    const/16 v18, 0x0

    #@b9
    move/from16 v0, v17

    #@bb
    move/from16 v1, v18

    #@bd
    invoke-virtual {v3, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@c0
    move-result v6

    #@c1
    .line 132
    .local v6, "customNavId":I
    if-eqz v6, :cond_5

    #@c3
    .line 133
    move-object/from16 v0, p0

    #@c5
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@c7
    move-object/from16 v17, v0

    #@c9
    invoke-virtual/range {v17 .. v17}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@cc
    move-result-object v17

    #@cd
    invoke-static/range {v17 .. v17}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@d0
    move-result-object v17

    #@d1
    move-object/from16 v0, p0

    #@d3
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@d5
    move-object/from16 v18, v0

    #@d7
    const/16 v19, 0x0

    #@d9
    move-object/from16 v0, v17

    #@db
    move-object/from16 v1, v18

    #@dd
    move/from16 v2, v19

    #@df
    invoke-virtual {v0, v6, v1, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@e2
    move-result-object v17

    #@e3
    move-object/from16 v0, p0

    #@e5
    move-object/from16 v1, v17

    #@e7
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setCustomView(Landroid/view/View;)V

    #@ea
    .line 135
    move-object/from16 v0, p0

    #@ec
    iget v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDisplayOpts:I

    #@ee
    move/from16 v17, v0

    #@f0
    or-int/lit8 v17, v17, 0x10

    #@f2
    move-object/from16 v0, p0

    #@f4
    move/from16 v1, v17

    #@f6
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setDisplayOptions(I)V

    #@f9
    .line 138
    :cond_5
    sget v17, Landroid/support/v7/appcompat/R$styleable;->ActionBar_height:I

    #@fb
    const/16 v18, 0x0

    #@fd
    move/from16 v0, v17

    #@ff
    move/from16 v1, v18

    #@101
    invoke-virtual {v3, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getLayoutDimension(II)I

    #@104
    move-result v7

    #@105
    .line 139
    .local v7, "height":I
    if-lez v7, :cond_6

    #@107
    .line 140
    move-object/from16 v0, p0

    #@109
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@10b
    move-object/from16 v17, v0

    #@10d
    invoke-virtual/range {v17 .. v17}, Landroid/support/v7/widget/Toolbar;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@110
    move-result-object v10

    #@111
    .line 141
    .local v10, "lp":Landroid/view/ViewGroup$LayoutParams;
    iput v7, v10, Landroid/view/ViewGroup$LayoutParams;->height:I

    #@113
    .line 142
    move-object/from16 v0, p0

    #@115
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@117
    move-object/from16 v17, v0

    #@119
    move-object/from16 v0, v17

    #@11b
    invoke-virtual {v0, v10}, Landroid/support/v7/widget/Toolbar;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@11e
    .line 145
    .end local v10    # "lp":Landroid/view/ViewGroup$LayoutParams;
    :cond_6
    sget v17, Landroid/support/v7/appcompat/R$styleable;->ActionBar_contentInsetStart:I

    #@120
    const/16 v18, -0x1

    #@122
    move/from16 v0, v17

    #@124
    move/from16 v1, v18

    #@126
    invoke-virtual {v3, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelOffset(II)I

    #@129
    move-result v5

    #@12a
    .line 147
    .local v5, "contentInsetStart":I
    sget v17, Landroid/support/v7/appcompat/R$styleable;->ActionBar_contentInsetEnd:I

    #@12c
    const/16 v18, -0x1

    #@12e
    move/from16 v0, v17

    #@130
    move/from16 v1, v18

    #@132
    invoke-virtual {v3, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelOffset(II)I

    #@135
    move-result v4

    #@136
    .line 149
    .local v4, "contentInsetEnd":I
    if-gez v5, :cond_7

    #@138
    if-ltz v4, :cond_8

    #@13a
    .line 150
    :cond_7
    move-object/from16 v0, p0

    #@13c
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@13e
    move-object/from16 v17, v0

    #@140
    const/16 v18, 0x0

    #@142
    move/from16 v0, v18

    #@144
    invoke-static {v5, v0}, Ljava/lang/Math;->max(II)I

    #@147
    move-result v18

    #@148
    const/16 v19, 0x0

    #@14a
    move/from16 v0, v19

    #@14c
    invoke-static {v4, v0}, Ljava/lang/Math;->max(II)I

    #@14f
    move-result v19

    #@150
    invoke-virtual/range {v17 .. v19}, Landroid/support/v7/widget/Toolbar;->setContentInsetsRelative(II)V

    #@153
    .line 154
    :cond_8
    sget v17, Landroid/support/v7/appcompat/R$styleable;->ActionBar_titleTextStyle:I

    #@155
    const/16 v18, 0x0

    #@157
    move/from16 v0, v17

    #@159
    move/from16 v1, v18

    #@15b
    invoke-virtual {v3, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@15e
    move-result v16

    #@15f
    .line 155
    .local v16, "titleTextStyle":I
    if-eqz v16, :cond_9

    #@161
    .line 156
    move-object/from16 v0, p0

    #@163
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@165
    move-object/from16 v17, v0

    #@167
    move-object/from16 v0, p0

    #@169
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@16b
    move-object/from16 v18, v0

    #@16d
    invoke-virtual/range {v18 .. v18}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@170
    move-result-object v18

    #@171
    move-object/from16 v0, v17

    #@173
    move-object/from16 v1, v18

    #@175
    move/from16 v2, v16

    #@177
    invoke-virtual {v0, v1, v2}, Landroid/support/v7/widget/Toolbar;->setTitleTextAppearance(Landroid/content/Context;I)V

    #@17a
    .line 159
    :cond_9
    sget v17, Landroid/support/v7/appcompat/R$styleable;->ActionBar_subtitleTextStyle:I

    #@17c
    const/16 v18, 0x0

    #@17e
    move/from16 v0, v17

    #@180
    move/from16 v1, v18

    #@182
    invoke-virtual {v3, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@185
    move-result v14

    #@186
    .line 161
    .local v14, "subtitleTextStyle":I
    if-eqz v14, :cond_a

    #@188
    .line 162
    move-object/from16 v0, p0

    #@18a
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@18c
    move-object/from16 v17, v0

    #@18e
    move-object/from16 v0, p0

    #@190
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@192
    move-object/from16 v18, v0

    #@194
    invoke-virtual/range {v18 .. v18}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@197
    move-result-object v18

    #@198
    move-object/from16 v0, v17

    #@19a
    move-object/from16 v1, v18

    #@19c
    invoke-virtual {v0, v1, v14}, Landroid/support/v7/widget/Toolbar;->setSubtitleTextAppearance(Landroid/content/Context;I)V

    #@19f
    .line 165
    :cond_a
    sget v17, Landroid/support/v7/appcompat/R$styleable;->ActionBar_popupTheme:I

    #@1a1
    const/16 v18, 0x0

    #@1a3
    move/from16 v0, v17

    #@1a5
    move/from16 v1, v18

    #@1a7
    invoke-virtual {v3, v0, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@1aa
    move-result v12

    #@1ab
    .line 166
    .local v12, "popupTheme":I
    if-eqz v12, :cond_b

    #@1ad
    .line 167
    move-object/from16 v0, p0

    #@1af
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@1b1
    move-object/from16 v17, v0

    #@1b3
    move-object/from16 v0, v17

    #@1b5
    invoke-virtual {v0, v12}, Landroid/support/v7/widget/Toolbar;->setPopupTheme(I)V

    #@1b8
    .line 170
    :cond_b
    invoke-virtual {v3}, Landroid/support/v7/internal/widget/TintTypedArray;->recycle()V

    #@1bb
    .line 172
    invoke-virtual {v3}, Landroid/support/v7/internal/widget/TintTypedArray;->getTintManager()Landroid/support/v7/internal/widget/TintManager;

    #@1be
    move-result-object v17

    #@1bf
    move-object/from16 v0, v17

    #@1c1
    move-object/from16 v1, p0

    #@1c3
    iput-object v0, v1, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@1c5
    .line 179
    .end local v3    # "a":Landroid/support/v7/internal/widget/TintTypedArray;
    .end local v4    # "contentInsetEnd":I
    .end local v5    # "contentInsetStart":I
    .end local v6    # "customNavId":I
    .end local v7    # "height":I
    .end local v8    # "icon":Landroid/graphics/drawable/Drawable;
    .end local v9    # "logo":Landroid/graphics/drawable/Drawable;
    .end local v11    # "navIcon":Landroid/graphics/drawable/Drawable;
    .end local v12    # "popupTheme":I
    .end local v13    # "subtitle":Ljava/lang/CharSequence;
    .end local v14    # "subtitleTextStyle":I
    .end local v15    # "title":Ljava/lang/CharSequence;
    .end local v16    # "titleTextStyle":I
    :goto_1
    move-object/from16 v0, p0

    #@1c7
    move/from16 v1, p3

    #@1c9
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setDefaultNavigationContentDescription(I)V

    #@1cc
    .line 180
    move-object/from16 v0, p0

    #@1ce
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@1d0
    move-object/from16 v17, v0

    #@1d2
    invoke-virtual/range {v17 .. v17}, Landroid/support/v7/widget/Toolbar;->getNavigationContentDescription()Ljava/lang/CharSequence;

    #@1d5
    move-result-object v17

    #@1d6
    move-object/from16 v0, v17

    #@1d8
    move-object/from16 v1, p0

    #@1da
    iput-object v0, v1, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mHomeDescription:Ljava/lang/CharSequence;

    #@1dc
    .line 182
    move-object/from16 v0, p0

    #@1de
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@1e0
    move-object/from16 v17, v0

    #@1e2
    move-object/from16 v0, v17

    #@1e4
    move/from16 v1, p4

    #@1e6
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@1e9
    move-result-object v17

    #@1ea
    move-object/from16 v0, p0

    #@1ec
    move-object/from16 v1, v17

    #@1ee
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setDefaultNavigationIcon(Landroid/graphics/drawable/Drawable;)V

    #@1f1
    .line 184
    move-object/from16 v0, p0

    #@1f3
    iget-object v0, v0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@1f5
    move-object/from16 v17, v0

    #@1f7
    new-instance v18, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$1;

    #@1f9
    move-object/from16 v0, v18

    #@1fb
    move-object/from16 v1, p0

    #@1fd
    invoke-direct {v0, v1}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$1;-><init>(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)V

    #@200
    invoke-virtual/range {v17 .. v18}, Landroid/support/v7/widget/Toolbar;->setNavigationOnClickListener(Landroid/view/View$OnClickListener;)V

    #@203
    .line 194
    return-void

    #@204
    .line 97
    :cond_c
    const/16 v17, 0x0

    #@206
    goto/16 :goto_0

    #@208
    .line 174
    :cond_d
    invoke-direct/range {p0 .. p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->detectDisplayOptions()I

    #@20b
    move-result v17

    #@20c
    move/from16 v0, v17

    #@20e
    move-object/from16 v1, p0

    #@210
    iput v0, v1, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDisplayOpts:I

    #@212
    .line 176
    new-instance v17, Landroid/support/v7/internal/widget/TintManager;

    #@214
    invoke-virtual/range {p1 .. p1}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@217
    move-result-object v18

    #@218
    invoke-direct/range {v17 .. v18}, Landroid/support/v7/internal/widget/TintManager;-><init>(Landroid/content/Context;)V

    #@21b
    move-object/from16 v0, v17

    #@21d
    move-object/from16 v1, p0

    #@21f
    iput-object v0, v1, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@221
    goto :goto_1
.end method

.method static synthetic access$000(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)Landroid/support/v7/widget/Toolbar;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@0
    .prologue
    .line 55
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    return-object v0
.end method

.method static synthetic access$100(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@0
    .prologue
    .line 55
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTitle:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method static synthetic access$200(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)Landroid/support/v7/internal/app/WindowCallback;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@0
    .prologue
    .line 55
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@2
    return-object v0
.end method

.method static synthetic access$300(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;

    #@0
    .prologue
    .line 55
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mMenuPrepared:Z

    #@2
    return v0
.end method

.method private detectDisplayOptions()I
    .locals 2

    #@0
    .prologue
    .line 225
    const/16 v0, 0xb

    #@2
    .line 227
    .local v0, "opts":I
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@4
    invoke-virtual {v1}, Landroid/support/v7/widget/Toolbar;->getNavigationIcon()Landroid/graphics/drawable/Drawable;

    #@7
    move-result-object v1

    #@8
    if-eqz v1, :cond_0

    #@a
    .line 228
    or-int/lit8 v0, v0, 0x4

    #@c
    .line 230
    :cond_0
    return v0
.end method

.method private ensureSpinner()V
    .locals 6

    #@0
    .prologue
    const/4 v5, -0x2

    #@1
    .line 549
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@3
    if-nez v1, :cond_0

    #@5
    .line 550
    new-instance v1, Landroid/support/v7/internal/widget/SpinnerCompat;

    #@7
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->getContext()Landroid/content/Context;

    #@a
    move-result-object v2

    #@b
    const/4 v3, 0x0

    #@c
    sget v4, Landroid/support/v7/appcompat/R$attr;->actionDropDownStyle:I

    #@e
    invoke-direct {v1, v2, v3, v4}, Landroid/support/v7/internal/widget/SpinnerCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@11
    iput-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@13
    .line 551
    new-instance v0, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@15
    const v1, 0x800013

    #@18
    invoke-direct {v0, v5, v5, v1}, Landroid/support/v7/widget/Toolbar$LayoutParams;-><init>(III)V

    #@1b
    .line 553
    .local v0, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@1d
    invoke-virtual {v1, v0}, Landroid/support/v7/internal/widget/SpinnerCompat;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@20
    .line 555
    .end local v0    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_0
    return-void
.end method

.method private setTitleInt(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 283
    iput-object p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTitle:Ljava/lang/CharSequence;

    #@2
    .line 284
    iget v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDisplayOpts:I

    #@4
    and-int/lit8 v0, v0, 0x8

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 285
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@a
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/Toolbar;->setTitle(Ljava/lang/CharSequence;)V

    #@d
    .line 287
    :cond_0
    return-void
.end method

.method private updateHomeAccessibility()V
    .locals 2

    #@0
    .prologue
    .line 654
    iget v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDisplayOpts:I

    #@2
    and-int/lit8 v0, v0, 0x4

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 655
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mHomeDescription:Ljava/lang/CharSequence;

    #@8
    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@b
    move-result v0

    #@c
    if-eqz v0, :cond_1

    #@e
    .line 656
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@10
    iget v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDefaultNavigationContentDescription:I

    #@12
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->setNavigationContentDescription(I)V

    #@15
    .line 661
    :cond_0
    :goto_0
    return-void

    #@16
    .line 658
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@18
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mHomeDescription:Ljava/lang/CharSequence;

    #@1a
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->setNavigationContentDescription(Ljava/lang/CharSequence;)V

    #@1d
    goto :goto_0
.end method

.method private updateNavigationIcon()V
    .locals 2

    #@0
    .prologue
    .line 664
    iget v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDisplayOpts:I

    #@2
    and-int/lit8 v0, v0, 0x4

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 665
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@8
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mNavIcon:Landroid/graphics/drawable/Drawable;

    #@a
    if-eqz v0, :cond_1

    #@c
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mNavIcon:Landroid/graphics/drawable/Drawable;

    #@e
    :goto_0
    invoke-virtual {v1, v0}, Landroid/support/v7/widget/Toolbar;->setNavigationIcon(Landroid/graphics/drawable/Drawable;)V

    #@11
    .line 667
    :cond_0
    return-void

    #@12
    .line 665
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDefaultNavigationIcon:Landroid/graphics/drawable/Drawable;

    #@14
    goto :goto_0
.end method

.method private updateToolbarLogo()V
    .locals 2

    #@0
    .prologue
    .line 366
    const/4 v0, 0x0

    #@1
    .line 367
    .local v0, "logo":Landroid/graphics/drawable/Drawable;
    iget v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDisplayOpts:I

    #@3
    and-int/lit8 v1, v1, 0x2

    #@5
    if-eqz v1, :cond_0

    #@7
    .line 368
    iget v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDisplayOpts:I

    #@9
    and-int/lit8 v1, v1, 0x1

    #@b
    if-eqz v1, :cond_2

    #@d
    .line 369
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mLogo:Landroid/graphics/drawable/Drawable;

    #@f
    if-eqz v1, :cond_1

    #@11
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mLogo:Landroid/graphics/drawable/Drawable;

    #@13
    .line 374
    :cond_0
    :goto_0
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@15
    invoke-virtual {v1, v0}, Landroid/support/v7/widget/Toolbar;->setLogo(Landroid/graphics/drawable/Drawable;)V

    #@18
    .line 375
    return-void

    #@19
    .line 369
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mIcon:Landroid/graphics/drawable/Drawable;

    #@1b
    goto :goto_0

    #@1c
    .line 371
    :cond_2
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mIcon:Landroid/graphics/drawable/Drawable;

    #@1e
    goto :goto_0
.end method


# virtual methods
.method public animateToVisibility(I)V
    .locals 2
    .param p1, "visibility"    # I

    #@0
    .prologue
    .line 602
    const/16 v0, 0x8

    #@2
    if-ne p1, v0, :cond_1

    #@4
    .line 603
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@6
    invoke-static {v0}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@9
    move-result-object v0

    #@a
    const/4 v1, 0x0

    #@b
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->alpha(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@e
    move-result-object v0

    #@f
    new-instance v1, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$2;

    #@11
    invoke-direct {v1, p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$2;-><init>(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)V

    #@14
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@17
    .line 627
    :cond_0
    :goto_0
    return-void

    #@18
    .line 618
    :cond_1
    if-nez p1, :cond_0

    #@1a
    .line 619
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@1c
    invoke-static {v0}, Landroid/support/v4/view/ViewCompat;->animate(Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@1f
    move-result-object v0

    #@20
    const/high16 v1, 0x3f800000    # 1.0f

    #@22
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->alpha(F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@25
    move-result-object v0

    #@26
    new-instance v1, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$3;

    #@28
    invoke-direct {v1, p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper$3;-><init>(Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;)V

    #@2b
    invoke-virtual {v0, v1}, Landroid/support/v4/view/ViewPropertyAnimatorCompat;->setListener(Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;

    #@2e
    goto :goto_0
.end method

.method public canShowOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 379
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->canShowOverflowMenu()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public canSplit()Z
    .locals 1

    #@0
    .prologue
    .line 314
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public collapseActionView()V
    .locals 1

    #@0
    .prologue
    .line 255
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->collapseActionView()V

    #@5
    .line 256
    return-void
.end method

.method public dismissPopupMenus()V
    .locals 1

    #@0
    .prologue
    .line 419
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->dismissPopupMenus()V

    #@5
    .line 420
    return-void
.end method

.method public getContext()Landroid/content/Context;
    .locals 1

    #@0
    .prologue
    .line 240
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getCustomView()Landroid/view/View;
    .locals 1

    #@0
    .prologue
    .line 597
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mCustomView:Landroid/view/View;

    #@2
    return-object v0
.end method

.method public getDisplayOptions()I
    .locals 1

    #@0
    .prologue
    .line 424
    iget v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDisplayOpts:I

    #@2
    return v0
.end method

.method public getDropdownItemCount()I
    .locals 1

    #@0
    .prologue
    .line 581
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getCount()I

    #@9
    move-result v0

    #@a
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public getDropdownSelectedPosition()I
    .locals 1

    #@0
    .prologue
    .line 576
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/SpinnerCompat;->getSelectedItemPosition()I

    #@9
    move-result v0

    #@a
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public getNavigationMode()I
    .locals 1

    #@0
    .prologue
    .line 504
    iget v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mNavigationMode:I

    #@2
    return v0
.end method

.method public getSubtitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 291
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->getSubtitle()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getTitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 273
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->getTitle()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getViewGroup()Landroid/view/ViewGroup;
    .locals 1

    #@0
    .prologue
    .line 235
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    return-object v0
.end method

.method public hasEmbeddedTabs()Z
    .locals 1

    #@0
    .prologue
    .line 484
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTabView:Landroid/view/View;

    #@2
    if-eqz v0, :cond_0

    #@4
    const/4 v0, 0x1

    #@5
    :goto_0
    return v0

    #@6
    :cond_0
    const/4 v0, 0x0

    #@7
    goto :goto_0
.end method

.method public hasExpandedActionView()Z
    .locals 1

    #@0
    .prologue
    .line 250
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->hasExpandedActionView()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public hasIcon()Z
    .locals 1

    #@0
    .prologue
    .line 335
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mIcon:Landroid/graphics/drawable/Drawable;

    #@2
    if-eqz v0, :cond_0

    #@4
    const/4 v0, 0x1

    #@5
    :goto_0
    return v0

    #@6
    :cond_0
    const/4 v0, 0x0

    #@7
    goto :goto_0
.end method

.method public hasLogo()Z
    .locals 1

    #@0
    .prologue
    .line 340
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mLogo:Landroid/graphics/drawable/Drawable;

    #@2
    if-eqz v0, :cond_0

    #@4
    const/4 v0, 0x1

    #@5
    :goto_0
    return v0

    #@6
    :cond_0
    const/4 v0, 0x0

    #@7
    goto :goto_0
.end method

.method public hideOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 399
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->hideOverflowMenu()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public initIndeterminateProgress()V
    .locals 2

    #@0
    .prologue
    .line 309
    const-string v0, "ToolbarWidgetWrapper"

    #@2
    const-string v1, "Progress display unsupported"

    #@4
    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    #@7
    .line 310
    return-void
.end method

.method public initProgress()V
    .locals 2

    #@0
    .prologue
    .line 304
    const-string v0, "ToolbarWidgetWrapper"

    #@2
    const-string v1, "Progress display unsupported"

    #@4
    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    #@7
    .line 305
    return-void
.end method

.method public isOverflowMenuShowPending()Z
    .locals 1

    #@0
    .prologue
    .line 389
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->isOverflowMenuShowPending()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public isOverflowMenuShowing()Z
    .locals 1

    #@0
    .prologue
    .line 384
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->isOverflowMenuShowing()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public isSplit()Z
    .locals 1

    #@0
    .prologue
    .line 245
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public isTitleTruncated()Z
    .locals 1

    #@0
    .prologue
    .line 489
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->isTitleTruncated()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public restoreHierarchyState(Landroid/util/SparseArray;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/os/Parcelable;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 676
    .local p1, "toolbarStates":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/Toolbar;->restoreHierarchyState(Landroid/util/SparseArray;)V

    #@5
    .line 677
    return-void
.end method

.method public saveHierarchyState(Landroid/util/SparseArray;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/util/SparseArray",
            "<",
            "Landroid/os/Parcelable;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 671
    .local p1, "toolbarStates":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/Toolbar;->saveHierarchyState(Landroid/util/SparseArray;)V

    #@5
    .line 672
    return-void
.end method

.method public setCollapsible(Z)V
    .locals 1
    .param p1, "collapsible"    # Z

    #@0
    .prologue
    .line 494
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/Toolbar;->setCollapsible(Z)V

    #@5
    .line 495
    return-void
.end method

.method public setCustomView(Landroid/view/View;)V
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 586
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mCustomView:Landroid/view/View;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDisplayOpts:I

    #@6
    and-int/lit8 v0, v0, 0x10

    #@8
    if-eqz v0, :cond_0

    #@a
    .line 587
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@c
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mCustomView:Landroid/view/View;

    #@e
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->removeView(Landroid/view/View;)V

    #@11
    .line 589
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mCustomView:Landroid/view/View;

    #@13
    .line 590
    if-eqz p1, :cond_1

    #@15
    iget v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDisplayOpts:I

    #@17
    and-int/lit8 v0, v0, 0x10

    #@19
    if-eqz v0, :cond_1

    #@1b
    .line 591
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@1d
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mCustomView:Landroid/view/View;

    #@1f
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/Toolbar;->addView(Landroid/view/View;)V

    #@22
    .line 593
    :cond_1
    return-void
.end method

.method public setDefaultNavigationContentDescription(I)V
    .locals 1
    .param p1, "defaultNavigationContentDescription"    # I

    #@0
    .prologue
    .line 207
    iget v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDefaultNavigationContentDescription:I

    #@2
    if-ne p1, v0, :cond_1

    #@4
    .line 214
    :cond_0
    :goto_0
    return-void

    #@5
    .line 210
    :cond_1
    iput p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDefaultNavigationContentDescription:I

    #@7
    .line 211
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@9
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->getNavigationContentDescription()Ljava/lang/CharSequence;

    #@c
    move-result-object v0

    #@d
    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@10
    move-result v0

    #@11
    if-eqz v0, :cond_0

    #@13
    .line 212
    iget v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDefaultNavigationContentDescription:I

    #@15
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setNavigationContentDescription(I)V

    #@18
    goto :goto_0
.end method

.method public setDefaultNavigationIcon(Landroid/graphics/drawable/Drawable;)V
    .locals 1
    .param p1, "defaultNavigationIcon"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 218
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDefaultNavigationIcon:Landroid/graphics/drawable/Drawable;

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 219
    iput-object p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDefaultNavigationIcon:Landroid/graphics/drawable/Drawable;

    #@6
    .line 220
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->updateNavigationIcon()V

    #@9
    .line 222
    :cond_0
    return-void
.end method

.method public setDisplayOptions(I)V
    .locals 4
    .param p1, "newOpts"    # I

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 429
    iget v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDisplayOpts:I

    #@3
    .line 430
    .local v1, "oldOpts":I
    xor-int v0, v1, p1

    #@5
    .line 431
    .local v0, "changed":I
    iput p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDisplayOpts:I

    #@7
    .line 432
    if-eqz v0, :cond_3

    #@9
    .line 433
    and-int/lit8 v2, v0, 0x4

    #@b
    if-eqz v2, :cond_0

    #@d
    .line 434
    and-int/lit8 v2, p1, 0x4

    #@f
    if-eqz v2, :cond_4

    #@11
    .line 435
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->updateNavigationIcon()V

    #@14
    .line 436
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->updateHomeAccessibility()V

    #@17
    .line 442
    :cond_0
    :goto_0
    and-int/lit8 v2, v0, 0x3

    #@19
    if-eqz v2, :cond_1

    #@1b
    .line 443
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->updateToolbarLogo()V

    #@1e
    .line 446
    :cond_1
    and-int/lit8 v2, v0, 0x8

    #@20
    if-eqz v2, :cond_2

    #@22
    .line 447
    and-int/lit8 v2, p1, 0x8

    #@24
    if-eqz v2, :cond_5

    #@26
    .line 448
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@28
    iget-object v3, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTitle:Ljava/lang/CharSequence;

    #@2a
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/Toolbar;->setTitle(Ljava/lang/CharSequence;)V

    #@2d
    .line 449
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2f
    iget-object v3, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSubtitle:Ljava/lang/CharSequence;

    #@31
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/Toolbar;->setSubtitle(Ljava/lang/CharSequence;)V

    #@34
    .line 456
    :cond_2
    :goto_1
    and-int/lit8 v2, v0, 0x10

    #@36
    if-eqz v2, :cond_3

    #@38
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mCustomView:Landroid/view/View;

    #@3a
    if-eqz v2, :cond_3

    #@3c
    .line 457
    and-int/lit8 v2, p1, 0x10

    #@3e
    if-eqz v2, :cond_6

    #@40
    .line 458
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@42
    iget-object v3, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mCustomView:Landroid/view/View;

    #@44
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/Toolbar;->addView(Landroid/view/View;)V

    #@47
    .line 464
    :cond_3
    :goto_2
    return-void

    #@48
    .line 438
    :cond_4
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@4a
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/Toolbar;->setNavigationIcon(Landroid/graphics/drawable/Drawable;)V

    #@4d
    goto :goto_0

    #@4e
    .line 451
    :cond_5
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@50
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/Toolbar;->setTitle(Ljava/lang/CharSequence;)V

    #@53
    .line 452
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@55
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/Toolbar;->setSubtitle(Ljava/lang/CharSequence;)V

    #@58
    goto :goto_1

    #@59
    .line 460
    :cond_6
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@5b
    iget-object v3, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mCustomView:Landroid/view/View;

    #@5d
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/Toolbar;->removeView(Landroid/view/View;)V

    #@60
    goto :goto_2
.end method

.method public setDropdownParams(Landroid/widget/SpinnerAdapter;Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;)V
    .locals 1
    .param p1, "adapter"    # Landroid/widget/SpinnerAdapter;
    .param p2, "listener"    # Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;

    #@0
    .prologue
    .line 560
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->ensureSpinner()V

    #@3
    .line 561
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@5
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/SpinnerCompat;->setAdapter(Landroid/widget/SpinnerAdapter;)V

    #@8
    .line 562
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@a
    invoke-virtual {v0, p2}, Landroid/support/v7/internal/widget/SpinnerCompat;->setOnItemSelectedListener(Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemSelectedListener;)V

    #@d
    .line 563
    return-void
.end method

.method public setDropdownSelectedPosition(I)V
    .locals 2
    .param p1, "position"    # I

    #@0
    .prologue
    .line 567
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 568
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "Can\'t set dropdown selected position without an adapter"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 571
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@e
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/SpinnerCompat;->setSelection(I)V

    #@11
    .line 572
    return-void
.end method

.method public setEmbeddedTabView(Landroid/support/v7/internal/widget/ScrollingTabContainerView;)V
    .locals 5
    .param p1, "tabView"    # Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@0
    .prologue
    const/4 v4, -0x2

    #@1
    .line 468
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTabView:Landroid/view/View;

    #@3
    if-eqz v1, :cond_0

    #@5
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTabView:Landroid/view/View;

    #@7
    invoke-virtual {v1}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@a
    move-result-object v1

    #@b
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@d
    if-ne v1, v2, :cond_0

    #@f
    .line 469
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@11
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTabView:Landroid/view/View;

    #@13
    invoke-virtual {v1, v2}, Landroid/support/v7/widget/Toolbar;->removeView(Landroid/view/View;)V

    #@16
    .line 471
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTabView:Landroid/view/View;

    #@18
    .line 472
    if-eqz p1, :cond_1

    #@1a
    iget v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mNavigationMode:I

    #@1c
    const/4 v2, 0x2

    #@1d
    if-ne v1, v2, :cond_1

    #@1f
    .line 473
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@21
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTabView:Landroid/view/View;

    #@23
    const/4 v3, 0x0

    #@24
    invoke-virtual {v1, v2, v3}, Landroid/support/v7/widget/Toolbar;->addView(Landroid/view/View;I)V

    #@27
    .line 474
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTabView:Landroid/view/View;

    #@29
    invoke-virtual {v1}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@2c
    move-result-object v0

    #@2d
    check-cast v0, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@2f
    .line 475
    .local v0, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    iput v4, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->width:I

    #@31
    .line 476
    iput v4, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->height:I

    #@33
    .line 477
    const v1, 0x800053

    #@36
    iput v1, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->gravity:I

    #@38
    .line 478
    const/4 v1, 0x1

    #@39
    invoke-virtual {p1, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->setAllowCollapse(Z)V

    #@3c
    .line 480
    .end local v0    # "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    :cond_1
    return-void
.end method

.method public setHomeButtonEnabled(Z)V
    .locals 0
    .param p1, "enable"    # Z

    #@0
    .prologue
    .line 500
    return-void
.end method

.method public setIcon(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 345
    if-eqz p1, :cond_0

    #@2
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@4
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@7
    move-result-object v0

    #@8
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setIcon(Landroid/graphics/drawable/Drawable;)V

    #@b
    .line 346
    return-void

    #@c
    .line 345
    :cond_0
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method public setIcon(Landroid/graphics/drawable/Drawable;)V
    .locals 0
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 350
    iput-object p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mIcon:Landroid/graphics/drawable/Drawable;

    #@2
    .line 351
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->updateToolbarLogo()V

    #@5
    .line 352
    return-void
.end method

.method public setLogo(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 356
    if-eqz p1, :cond_0

    #@2
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@4
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@7
    move-result-object v0

    #@8
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setLogo(Landroid/graphics/drawable/Drawable;)V

    #@b
    .line 357
    return-void

    #@c
    .line 356
    :cond_0
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method public setLogo(Landroid/graphics/drawable/Drawable;)V
    .locals 0
    .param p1, "d"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 361
    iput-object p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mLogo:Landroid/graphics/drawable/Drawable;

    #@2
    .line 362
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->updateToolbarLogo()V

    #@5
    .line 363
    return-void
.end method

.method public setMenu(Landroid/view/Menu;Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V
    .locals 2
    .param p1, "menu"    # Landroid/view/Menu;
    .param p2, "cb"    # Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;

    #@0
    .prologue
    .line 409
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 410
    new-instance v0, Landroid/support/v7/widget/ActionMenuPresenter;

    #@6
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@8
    invoke-virtual {v1}, Landroid/support/v7/widget/Toolbar;->getContext()Landroid/content/Context;

    #@b
    move-result-object v1

    #@c
    invoke-direct {v0, v1}, Landroid/support/v7/widget/ActionMenuPresenter;-><init>(Landroid/content/Context;)V

    #@f
    iput-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@11
    .line 411
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@13
    sget v1, Landroid/support/v7/appcompat/R$id;->action_menu_presenter:I

    #@15
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/ActionMenuPresenter;->setId(I)V

    #@18
    .line 413
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@1a
    invoke-virtual {v0, p2}, Landroid/support/v7/widget/ActionMenuPresenter;->setCallback(Landroid/support/v7/internal/view/menu/MenuPresenter$Callback;)V

    #@1d
    .line 414
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@1f
    check-cast p1, Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@21
    .end local p1    # "menu":Landroid/view/Menu;
    iget-object v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mActionMenuPresenter:Landroid/support/v7/widget/ActionMenuPresenter;

    #@23
    invoke-virtual {v0, p1, v1}, Landroid/support/v7/widget/Toolbar;->setMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/support/v7/widget/ActionMenuPresenter;)V

    #@26
    .line 415
    return-void
.end method

.method public setMenuPrepared()V
    .locals 1

    #@0
    .prologue
    .line 404
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mMenuPrepared:Z

    #@3
    .line 405
    return-void
.end method

.method public setNavigationContentDescription(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 650
    if-nez p1, :cond_0

    #@2
    const/4 v0, 0x0

    #@3
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setNavigationContentDescription(Ljava/lang/CharSequence;)V

    #@6
    .line 651
    return-void

    #@7
    .line 650
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->getContext()Landroid/content/Context;

    #@a
    move-result-object v0

    #@b
    invoke-virtual {v0, p1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    #@e
    move-result-object v0

    #@f
    goto :goto_0
.end method

.method public setNavigationContentDescription(Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "description"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 644
    iput-object p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mHomeDescription:Ljava/lang/CharSequence;

    #@2
    .line 645
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->updateHomeAccessibility()V

    #@5
    .line 646
    return-void
.end method

.method public setNavigationIcon(I)V
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 637
    if-eqz p1, :cond_0

    #@2
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@4
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@7
    move-result-object v0

    #@8
    :goto_0
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setNavigationIcon(Landroid/graphics/drawable/Drawable;)V

    #@b
    .line 640
    return-void

    #@c
    .line 637
    :cond_0
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method public setNavigationIcon(Landroid/graphics/drawable/Drawable;)V
    .locals 0
    .param p1, "icon"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 631
    iput-object p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mNavIcon:Landroid/graphics/drawable/Drawable;

    #@2
    .line 632
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->updateNavigationIcon()V

    #@5
    .line 633
    return-void
.end method

.method public setNavigationMode(I)V
    .locals 6
    .param p1, "mode"    # I

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    const/4 v4, -0x2

    #@2
    .line 509
    iget v1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mNavigationMode:I

    #@4
    .line 510
    .local v1, "oldMode":I
    if-eq p1, v1, :cond_1

    #@6
    .line 511
    packed-switch v1, :pswitch_data_0

    #@9
    .line 524
    :cond_0
    :goto_0
    iput p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mNavigationMode:I

    #@b
    .line 526
    packed-switch p1, :pswitch_data_1

    #@e
    .line 543
    new-instance v2, Ljava/lang/IllegalArgumentException;

    #@10
    new-instance v3, Ljava/lang/StringBuilder;

    #@12
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@15
    const-string v4, "Invalid navigation mode "

    #@17
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1a
    move-result-object v3

    #@1b
    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@1e
    move-result-object v3

    #@1f
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@22
    move-result-object v3

    #@23
    invoke-direct {v2, v3}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@26
    throw v2

    #@27
    .line 513
    :pswitch_0
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@29
    if-eqz v2, :cond_0

    #@2b
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@2d
    invoke-virtual {v2}, Landroid/support/v7/internal/widget/SpinnerCompat;->getParent()Landroid/view/ViewParent;

    #@30
    move-result-object v2

    #@31
    iget-object v3, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@33
    if-ne v2, v3, :cond_0

    #@35
    .line 514
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@37
    iget-object v3, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@39
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/Toolbar;->removeView(Landroid/view/View;)V

    #@3c
    goto :goto_0

    #@3d
    .line 518
    :pswitch_1
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTabView:Landroid/view/View;

    #@3f
    if-eqz v2, :cond_0

    #@41
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTabView:Landroid/view/View;

    #@43
    invoke-virtual {v2}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@46
    move-result-object v2

    #@47
    iget-object v3, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@49
    if-ne v2, v3, :cond_0

    #@4b
    .line 519
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@4d
    iget-object v3, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTabView:Landroid/view/View;

    #@4f
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/Toolbar;->removeView(Landroid/view/View;)V

    #@52
    goto :goto_0

    #@53
    .line 530
    :pswitch_2
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->ensureSpinner()V

    #@56
    .line 531
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@58
    iget-object v3, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSpinner:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@5a
    invoke-virtual {v2, v3, v5}, Landroid/support/v7/widget/Toolbar;->addView(Landroid/view/View;I)V

    #@5d
    .line 546
    :cond_1
    :goto_1
    :pswitch_3
    return-void

    #@5e
    .line 534
    :pswitch_4
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTabView:Landroid/view/View;

    #@60
    if-eqz v2, :cond_1

    #@62
    .line 535
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@64
    iget-object v3, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTabView:Landroid/view/View;

    #@66
    invoke-virtual {v2, v3, v5}, Landroid/support/v7/widget/Toolbar;->addView(Landroid/view/View;I)V

    #@69
    .line 536
    iget-object v2, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTabView:Landroid/view/View;

    #@6b
    invoke-virtual {v2}, Landroid/view/View;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@6e
    move-result-object v0

    #@6f
    check-cast v0, Landroid/support/v7/widget/Toolbar$LayoutParams;

    #@71
    .line 537
    .local v0, "lp":Landroid/support/v7/widget/Toolbar$LayoutParams;
    iput v4, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->width:I

    #@73
    .line 538
    iput v4, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->height:I

    #@75
    .line 539
    const v2, 0x800053

    #@78
    iput v2, v0, Landroid/support/v7/widget/Toolbar$LayoutParams;->gravity:I

    #@7a
    goto :goto_1

    #@7b
    .line 511
    nop

    #@7c
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
    .end packed-switch

    #@84
    .line 526
    :pswitch_data_1
    .packed-switch 0x0
        :pswitch_3
        :pswitch_2
        :pswitch_4
    .end packed-switch
.end method

.method public setSplitToolbar(Z)V
    .locals 2
    .param p1, "split"    # Z

    #@0
    .prologue
    .line 323
    if-eqz p1, :cond_0

    #@2
    .line 324
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@4
    const-string v1, "Cannot split an android.widget.Toolbar"

    #@6
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@9
    throw v0

    #@a
    .line 326
    :cond_0
    return-void
.end method

.method public setSplitView(Landroid/view/ViewGroup;)V
    .locals 0
    .param p1, "splitView"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 319
    return-void
.end method

.method public setSplitWhenNarrow(Z)V
    .locals 0
    .param p1, "splitWhenNarrow"    # Z

    #@0
    .prologue
    .line 331
    return-void
.end method

.method public setSubtitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "subtitle"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 296
    iput-object p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mSubtitle:Ljava/lang/CharSequence;

    #@2
    .line 297
    iget v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mDisplayOpts:I

    #@4
    and-int/lit8 v0, v0, 0x8

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 298
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@a
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/Toolbar;->setSubtitle(Ljava/lang/CharSequence;)V

    #@d
    .line 300
    :cond_0
    return-void
.end method

.method public setTitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 278
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTitleSet:Z

    #@3
    .line 279
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setTitleInt(Ljava/lang/CharSequence;)V

    #@6
    .line 280
    return-void
.end method

.method public setWindowCallback(Landroid/support/v7/internal/app/WindowCallback;)V
    .locals 0
    .param p1, "cb"    # Landroid/support/v7/internal/app/WindowCallback;

    #@0
    .prologue
    .line 260
    iput-object p1, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mWindowCallback:Landroid/support/v7/internal/app/WindowCallback;

    #@2
    .line 261
    return-void
.end method

.method public setWindowTitle(Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 266
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mTitleSet:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 267
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->setTitleInt(Ljava/lang/CharSequence;)V

    #@7
    .line 269
    :cond_0
    return-void
.end method

.method public showOverflowMenu()Z
    .locals 1

    #@0
    .prologue
    .line 394
    iget-object v0, p0, Landroid/support/v7/internal/widget/ToolbarWidgetWrapper;->mToolbar:Landroid/support/v7/widget/Toolbar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/Toolbar;->showOverflowMenu()Z

    #@5
    move-result v0

    #@6
    return v0
.end method
