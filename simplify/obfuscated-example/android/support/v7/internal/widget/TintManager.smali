.class public Landroid/support/v7/internal/widget/TintManager;
.super Ljava/lang/Object;
.source "TintManager.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;
    }
.end annotation


# static fields
.field private static final COLOR_FILTER_CACHE:Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;

.field private static final CONTAINERS_WITH_TINT_CHILDREN:[I

.field private static final DEBUG:Z

.field static final DEFAULT_MODE:Landroid/graphics/PorterDuff$Mode;

.field private static final TAG:Ljava/lang/String;

.field private static final TINT_COLOR_BACKGROUND_MULTIPLY:[I

.field private static final TINT_COLOR_CONTROL_ACTIVATED:[I

.field private static final TINT_COLOR_CONTROL_NORMAL:[I

.field private static final TINT_COLOR_CONTROL_STATE_LIST:[I


# instance fields
.field private final mContext:Landroid/content/Context;

.field private mDefaultColorStateList:Landroid/content/res/ColorStateList;

.field private final mResources:Landroid/content/res/Resources;

.field private mSwitchThumbStateList:Landroid/content/res/ColorStateList;

.field private mSwitchTrackStateList:Landroid/content/res/ColorStateList;

.field private final mTypedValue:Landroid/util/TypedValue;


# direct methods
.method static constructor <clinit>()V
    .locals 8

    #@0
    .prologue
    const/4 v7, 0x6

    #@1
    const/4 v6, 0x3

    #@2
    const/4 v5, 0x2

    #@3
    const/4 v4, 0x1

    #@4
    const/4 v3, 0x0

    #@5
    .line 37
    const-class v0, Landroid/support/v7/internal/widget/TintManager;

    #@7
    invoke-virtual {v0}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    #@a
    move-result-object v0

    #@b
    sput-object v0, Landroid/support/v7/internal/widget/TintManager;->TAG:Ljava/lang/String;

    #@d
    .line 40
    sget-object v0, Landroid/graphics/PorterDuff$Mode;->SRC_IN:Landroid/graphics/PorterDuff$Mode;

    #@f
    sput-object v0, Landroid/support/v7/internal/widget/TintManager;->DEFAULT_MODE:Landroid/graphics/PorterDuff$Mode;

    #@11
    .line 42
    new-instance v0, Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;

    #@13
    invoke-direct {v0, v7}, Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;-><init>(I)V

    #@16
    sput-object v0, Landroid/support/v7/internal/widget/TintManager;->COLOR_FILTER_CACHE:Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;

    #@18
    .line 48
    const/16 v0, 0xe

    #@1a
    new-array v0, v0, [I

    #@1c
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_ic_ab_back_mtrl_am_alpha:I

    #@1e
    aput v1, v0, v3

    #@20
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_ic_go_search_api_mtrl_alpha:I

    #@22
    aput v1, v0, v4

    #@24
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_ic_search_api_mtrl_alpha:I

    #@26
    aput v1, v0, v5

    #@28
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_ic_commit_search_api_mtrl_alpha:I

    #@2a
    aput v1, v0, v6

    #@2c
    const/4 v1, 0x4

    #@2d
    sget v2, Landroid/support/v7/appcompat/R$drawable;->abc_ic_clear_mtrl_alpha:I

    #@2f
    aput v2, v0, v1

    #@31
    const/4 v1, 0x5

    #@32
    sget v2, Landroid/support/v7/appcompat/R$drawable;->abc_ic_menu_share_mtrl_alpha:I

    #@34
    aput v2, v0, v1

    #@36
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_ic_menu_copy_mtrl_am_alpha:I

    #@38
    aput v1, v0, v7

    #@3a
    const/4 v1, 0x7

    #@3b
    sget v2, Landroid/support/v7/appcompat/R$drawable;->abc_ic_menu_cut_mtrl_alpha:I

    #@3d
    aput v2, v0, v1

    #@3f
    const/16 v1, 0x8

    #@41
    sget v2, Landroid/support/v7/appcompat/R$drawable;->abc_ic_menu_selectall_mtrl_alpha:I

    #@43
    aput v2, v0, v1

    #@45
    const/16 v1, 0x9

    #@47
    sget v2, Landroid/support/v7/appcompat/R$drawable;->abc_ic_menu_paste_mtrl_am_alpha:I

    #@49
    aput v2, v0, v1

    #@4b
    const/16 v1, 0xa

    #@4d
    sget v2, Landroid/support/v7/appcompat/R$drawable;->abc_ic_menu_moreoverflow_mtrl_alpha:I

    #@4f
    aput v2, v0, v1

    #@51
    const/16 v1, 0xb

    #@53
    sget v2, Landroid/support/v7/appcompat/R$drawable;->abc_ic_voice_search_api_mtrl_alpha:I

    #@55
    aput v2, v0, v1

    #@57
    const/16 v1, 0xc

    #@59
    sget v2, Landroid/support/v7/appcompat/R$drawable;->abc_textfield_search_default_mtrl_alpha:I

    #@5b
    aput v2, v0, v1

    #@5d
    const/16 v1, 0xd

    #@5f
    sget v2, Landroid/support/v7/appcompat/R$drawable;->abc_textfield_default_mtrl_alpha:I

    #@61
    aput v2, v0, v1

    #@63
    sput-object v0, Landroid/support/v7/internal/widget/TintManager;->TINT_COLOR_CONTROL_NORMAL:[I

    #@65
    .line 69
    new-array v0, v6, [I

    #@67
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_textfield_activated_mtrl_alpha:I

    #@69
    aput v1, v0, v3

    #@6b
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_textfield_search_activated_mtrl_alpha:I

    #@6d
    aput v1, v0, v4

    #@6f
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_cab_background_top_mtrl_alpha:I

    #@71
    aput v1, v0, v5

    #@73
    sput-object v0, Landroid/support/v7/internal/widget/TintManager;->TINT_COLOR_CONTROL_ACTIVATED:[I

    #@75
    .line 79
    new-array v0, v6, [I

    #@77
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_popup_background_mtrl_mult:I

    #@79
    aput v1, v0, v3

    #@7b
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_cab_background_internal_bg:I

    #@7d
    aput v1, v0, v4

    #@7f
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_menu_hardkey_panel_mtrl_mult:I

    #@81
    aput v1, v0, v5

    #@83
    sput-object v0, Landroid/support/v7/internal/widget/TintManager;->TINT_COLOR_BACKGROUND_MULTIPLY:[I

    #@85
    .line 89
    new-array v0, v7, [I

    #@87
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_edit_text_material:I

    #@89
    aput v1, v0, v3

    #@8b
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_tab_indicator_material:I

    #@8d
    aput v1, v0, v4

    #@8f
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_textfield_search_material:I

    #@91
    aput v1, v0, v5

    #@93
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_spinner_mtrl_am_alpha:I

    #@95
    aput v1, v0, v6

    #@97
    const/4 v1, 0x4

    #@98
    sget v2, Landroid/support/v7/appcompat/R$drawable;->abc_btn_check_material:I

    #@9a
    aput v2, v0, v1

    #@9c
    const/4 v1, 0x5

    #@9d
    sget v2, Landroid/support/v7/appcompat/R$drawable;->abc_btn_radio_material:I

    #@9f
    aput v2, v0, v1

    #@a1
    sput-object v0, Landroid/support/v7/internal/widget/TintManager;->TINT_COLOR_CONTROL_STATE_LIST:[I

    #@a3
    .line 102
    new-array v0, v4, [I

    #@a5
    sget v1, Landroid/support/v7/appcompat/R$drawable;->abc_cab_background_top_material:I

    #@a7
    aput v1, v0, v3

    #@a9
    sput-object v0, Landroid/support/v7/internal/widget/TintManager;->CONTAINERS_WITH_TINT_CHILDREN:[I

    #@ab
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 126
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 127
    iput-object p1, p0, Landroid/support/v7/internal/widget/TintManager;->mContext:Landroid/content/Context;

    #@5
    .line 128
    new-instance v0, Landroid/support/v7/internal/widget/TintResources;

    #@7
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@a
    move-result-object v1

    #@b
    invoke-direct {v0, v1, p0}, Landroid/support/v7/internal/widget/TintResources;-><init>(Landroid/content/res/Resources;Landroid/support/v7/internal/widget/TintManager;)V

    #@e
    iput-object v0, p0, Landroid/support/v7/internal/widget/TintManager;->mResources:Landroid/content/res/Resources;

    #@10
    .line 129
    new-instance v0, Landroid/util/TypedValue;

    #@12
    invoke-direct {v0}, Landroid/util/TypedValue;-><init>()V

    #@15
    iput-object v0, p0, Landroid/support/v7/internal/widget/TintManager;->mTypedValue:Landroid/util/TypedValue;

    #@17
    .line 130
    return-void
.end method

.method private static arrayContains([II)Z
    .locals 5
    .param p0, "array"    # [I
    .param p1, "value"    # I

    #@0
    .prologue
    .line 204
    move-object v0, p0

    #@1
    .local v0, "arr$":[I
    array-length v3, v0

    #@2
    .local v3, "len$":I
    const/4 v1, 0x0

    #@3
    .local v1, "i$":I
    :goto_0
    if-ge v1, v3, :cond_1

    #@5
    aget v2, v0, v1

    #@7
    .line 205
    .local v2, "id":I
    if-ne v2, p1, :cond_0

    #@9
    .line 206
    const/4 v4, 0x1

    #@a
    .line 209
    .end local v2    # "id":I
    :goto_1
    return v4

    #@b
    .line 204
    .restart local v2    # "id":I
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@d
    goto :goto_0

    #@e
    .line 209
    .end local v2    # "id":I
    :cond_1
    const/4 v4, 0x0

    #@f
    goto :goto_1
.end method

.method private getDefaultColorStateList()Landroid/content/res/ColorStateList;
    .locals 9

    #@0
    .prologue
    const/4 v6, 0x7

    #@1
    const/4 v8, 0x1

    #@2
    const/4 v7, 0x0

    #@3
    .line 221
    iget-object v5, p0, Landroid/support/v7/internal/widget/TintManager;->mDefaultColorStateList:Landroid/content/res/ColorStateList;

    #@5
    if-nez v5, :cond_0

    #@7
    .line 227
    sget v5, Landroid/support/v7/appcompat/R$attr;->colorControlNormal:I

    #@9
    invoke-virtual {p0, v5}, Landroid/support/v7/internal/widget/TintManager;->getThemeAttrColor(I)I

    #@c
    move-result v1

    #@d
    .line 228
    .local v1, "colorControlNormal":I
    sget v5, Landroid/support/v7/appcompat/R$attr;->colorControlActivated:I

    #@f
    invoke-virtual {p0, v5}, Landroid/support/v7/internal/widget/TintManager;->getThemeAttrColor(I)I

    #@12
    move-result v0

    #@13
    .line 230
    .local v0, "colorControlActivated":I
    new-array v4, v6, [[I

    #@15
    .line 231
    .local v4, "states":[[I
    new-array v2, v6, [I

    #@17
    .line 232
    .local v2, "colors":[I
    const/4 v3, 0x0

    #@18
    .line 235
    .local v3, "i":I
    new-array v5, v8, [I

    #@1a
    const v6, -0x101009e

    #@1d
    aput v6, v5, v7

    #@1f
    aput-object v5, v4, v3

    #@21
    .line 236
    sget v5, Landroid/support/v7/appcompat/R$attr;->colorControlNormal:I

    #@23
    invoke-virtual {p0, v5}, Landroid/support/v7/internal/widget/TintManager;->getDisabledThemeAttrColor(I)I

    #@26
    move-result v5

    #@27
    aput v5, v2, v3

    #@29
    .line 237
    add-int/lit8 v3, v3, 0x1

    #@2b
    .line 239
    new-array v5, v8, [I

    #@2d
    const v6, 0x101009c

    #@30
    aput v6, v5, v7

    #@32
    aput-object v5, v4, v3

    #@34
    .line 240
    aput v0, v2, v3

    #@36
    .line 241
    add-int/lit8 v3, v3, 0x1

    #@38
    .line 243
    new-array v5, v8, [I

    #@3a
    const v6, 0x10102fe

    #@3d
    aput v6, v5, v7

    #@3f
    aput-object v5, v4, v3

    #@41
    .line 244
    aput v0, v2, v3

    #@43
    .line 245
    add-int/lit8 v3, v3, 0x1

    #@45
    .line 247
    new-array v5, v8, [I

    #@47
    const v6, 0x10100a7

    #@4a
    aput v6, v5, v7

    #@4c
    aput-object v5, v4, v3

    #@4e
    .line 248
    aput v0, v2, v3

    #@50
    .line 249
    add-int/lit8 v3, v3, 0x1

    #@52
    .line 251
    new-array v5, v8, [I

    #@54
    const v6, 0x10100a0

    #@57
    aput v6, v5, v7

    #@59
    aput-object v5, v4, v3

    #@5b
    .line 252
    aput v0, v2, v3

    #@5d
    .line 253
    add-int/lit8 v3, v3, 0x1

    #@5f
    .line 255
    new-array v5, v8, [I

    #@61
    const v6, 0x10100a1

    #@64
    aput v6, v5, v7

    #@66
    aput-object v5, v4, v3

    #@68
    .line 256
    aput v0, v2, v3

    #@6a
    .line 257
    add-int/lit8 v3, v3, 0x1

    #@6c
    .line 260
    new-array v5, v7, [I

    #@6e
    aput-object v5, v4, v3

    #@70
    .line 261
    aput v1, v2, v3

    #@72
    .line 262
    add-int/lit8 v3, v3, 0x1

    #@74
    .line 264
    new-instance v5, Landroid/content/res/ColorStateList;

    #@76
    invoke-direct {v5, v4, v2}, Landroid/content/res/ColorStateList;-><init>([[I[I)V

    #@79
    iput-object v5, p0, Landroid/support/v7/internal/widget/TintManager;->mDefaultColorStateList:Landroid/content/res/ColorStateList;

    #@7b
    .line 266
    .end local v0    # "colorControlActivated":I
    .end local v1    # "colorControlNormal":I
    .end local v2    # "colors":[I
    .end local v3    # "i":I
    .end local v4    # "states":[[I
    :cond_0
    iget-object v5, p0, Landroid/support/v7/internal/widget/TintManager;->mDefaultColorStateList:Landroid/content/res/ColorStateList;

    #@7d
    return-object v5
.end method

.method public static getDrawable(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 119
    invoke-static {p1}, Landroid/support/v7/internal/widget/TintManager;->isInTintList(I)Z

    #@3
    move-result v0

    #@4
    if-eqz v0, :cond_0

    #@6
    .line 120
    new-instance v0, Landroid/support/v7/internal/widget/TintManager;

    #@8
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/TintManager;-><init>(Landroid/content/Context;)V

    #@b
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@e
    move-result-object v0

    #@f
    .line 122
    :goto_0
    return-object v0

    #@10
    :cond_0
    invoke-static {p0, p1}, Landroid/support/v4/content/ContextCompat;->getDrawable(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;

    #@13
    move-result-object v0

    #@14
    goto :goto_0
.end method

.method private getSwitchThumbColorStateList()Landroid/content/res/ColorStateList;
    .locals 7

    #@0
    .prologue
    const/4 v4, 0x3

    #@1
    const/4 v6, 0x1

    #@2
    const/4 v5, 0x0

    #@3
    .line 295
    iget-object v3, p0, Landroid/support/v7/internal/widget/TintManager;->mSwitchThumbStateList:Landroid/content/res/ColorStateList;

    #@5
    if-nez v3, :cond_0

    #@7
    .line 296
    new-array v2, v4, [[I

    #@9
    .line 297
    .local v2, "states":[[I
    new-array v0, v4, [I

    #@b
    .line 298
    .local v0, "colors":[I
    const/4 v1, 0x0

    #@c
    .line 301
    .local v1, "i":I
    new-array v3, v6, [I

    #@e
    const v4, -0x101009e

    #@11
    aput v4, v3, v5

    #@13
    aput-object v3, v2, v1

    #@15
    .line 302
    sget v3, Landroid/support/v7/appcompat/R$attr;->colorSwitchThumbNormal:I

    #@17
    invoke-virtual {p0, v3}, Landroid/support/v7/internal/widget/TintManager;->getDisabledThemeAttrColor(I)I

    #@1a
    move-result v3

    #@1b
    aput v3, v0, v1

    #@1d
    .line 303
    add-int/lit8 v1, v1, 0x1

    #@1f
    .line 305
    new-array v3, v6, [I

    #@21
    const v4, 0x10100a0

    #@24
    aput v4, v3, v5

    #@26
    aput-object v3, v2, v1

    #@28
    .line 306
    sget v3, Landroid/support/v7/appcompat/R$attr;->colorControlActivated:I

    #@2a
    invoke-virtual {p0, v3}, Landroid/support/v7/internal/widget/TintManager;->getThemeAttrColor(I)I

    #@2d
    move-result v3

    #@2e
    aput v3, v0, v1

    #@30
    .line 307
    add-int/lit8 v1, v1, 0x1

    #@32
    .line 310
    new-array v3, v5, [I

    #@34
    aput-object v3, v2, v1

    #@36
    .line 311
    sget v3, Landroid/support/v7/appcompat/R$attr;->colorSwitchThumbNormal:I

    #@38
    invoke-virtual {p0, v3}, Landroid/support/v7/internal/widget/TintManager;->getThemeAttrColor(I)I

    #@3b
    move-result v3

    #@3c
    aput v3, v0, v1

    #@3e
    .line 312
    add-int/lit8 v1, v1, 0x1

    #@40
    .line 314
    new-instance v3, Landroid/content/res/ColorStateList;

    #@42
    invoke-direct {v3, v2, v0}, Landroid/content/res/ColorStateList;-><init>([[I[I)V

    #@45
    iput-object v3, p0, Landroid/support/v7/internal/widget/TintManager;->mSwitchThumbStateList:Landroid/content/res/ColorStateList;

    #@47
    .line 316
    .end local v0    # "colors":[I
    .end local v1    # "i":I
    .end local v2    # "states":[[I
    :cond_0
    iget-object v3, p0, Landroid/support/v7/internal/widget/TintManager;->mSwitchThumbStateList:Landroid/content/res/ColorStateList;

    #@49
    return-object v3
.end method

.method private getSwitchTrackColorStateList()Landroid/content/res/ColorStateList;
    .locals 9

    #@0
    .prologue
    const v8, 0x1010030

    #@3
    const/4 v4, 0x3

    #@4
    const/4 v7, 0x1

    #@5
    const v6, 0x3e99999a    # 0.3f

    #@8
    const/4 v5, 0x0

    #@9
    .line 270
    iget-object v3, p0, Landroid/support/v7/internal/widget/TintManager;->mSwitchTrackStateList:Landroid/content/res/ColorStateList;

    #@b
    if-nez v3, :cond_0

    #@d
    .line 271
    new-array v2, v4, [[I

    #@f
    .line 272
    .local v2, "states":[[I
    new-array v0, v4, [I

    #@11
    .line 273
    .local v0, "colors":[I
    const/4 v1, 0x0

    #@12
    .line 276
    .local v1, "i":I
    new-array v3, v7, [I

    #@14
    const v4, -0x101009e

    #@17
    aput v4, v3, v5

    #@19
    aput-object v3, v2, v1

    #@1b
    .line 277
    const v3, 0x3dcccccd    # 0.1f

    #@1e
    invoke-virtual {p0, v8, v3}, Landroid/support/v7/internal/widget/TintManager;->getThemeAttrColor(IF)I

    #@21
    move-result v3

    #@22
    aput v3, v0, v1

    #@24
    .line 278
    add-int/lit8 v1, v1, 0x1

    #@26
    .line 280
    new-array v3, v7, [I

    #@28
    const v4, 0x10100a0

    #@2b
    aput v4, v3, v5

    #@2d
    aput-object v3, v2, v1

    #@2f
    .line 281
    sget v3, Landroid/support/v7/appcompat/R$attr;->colorControlActivated:I

    #@31
    invoke-virtual {p0, v3, v6}, Landroid/support/v7/internal/widget/TintManager;->getThemeAttrColor(IF)I

    #@34
    move-result v3

    #@35
    aput v3, v0, v1

    #@37
    .line 282
    add-int/lit8 v1, v1, 0x1

    #@39
    .line 285
    new-array v3, v5, [I

    #@3b
    aput-object v3, v2, v1

    #@3d
    .line 286
    invoke-virtual {p0, v8, v6}, Landroid/support/v7/internal/widget/TintManager;->getThemeAttrColor(IF)I

    #@40
    move-result v3

    #@41
    aput v3, v0, v1

    #@43
    .line 287
    add-int/lit8 v1, v1, 0x1

    #@45
    .line 289
    new-instance v3, Landroid/content/res/ColorStateList;

    #@47
    invoke-direct {v3, v2, v0}, Landroid/content/res/ColorStateList;-><init>([[I[I)V

    #@4a
    iput-object v3, p0, Landroid/support/v7/internal/widget/TintManager;->mSwitchTrackStateList:Landroid/content/res/ColorStateList;

    #@4c
    .line 291
    .end local v0    # "colors":[I
    .end local v1    # "i":I
    .end local v2    # "states":[[I
    :cond_0
    iget-object v3, p0, Landroid/support/v7/internal/widget/TintManager;->mSwitchTrackStateList:Landroid/content/res/ColorStateList;

    #@4e
    return-object v3
.end method

.method private static isInTintList(I)Z
    .locals 1
    .param p0, "drawableId"    # I

    #@0
    .prologue
    .line 213
    sget-object v0, Landroid/support/v7/internal/widget/TintManager;->TINT_COLOR_BACKGROUND_MULTIPLY:[I

    #@2
    invoke-static {v0, p0}, Landroid/support/v7/internal/widget/TintManager;->arrayContains([II)Z

    #@5
    move-result v0

    #@6
    if-nez v0, :cond_0

    #@8
    sget-object v0, Landroid/support/v7/internal/widget/TintManager;->TINT_COLOR_CONTROL_NORMAL:[I

    #@a
    invoke-static {v0, p0}, Landroid/support/v7/internal/widget/TintManager;->arrayContains([II)Z

    #@d
    move-result v0

    #@e
    if-nez v0, :cond_0

    #@10
    sget-object v0, Landroid/support/v7/internal/widget/TintManager;->TINT_COLOR_CONTROL_ACTIVATED:[I

    #@12
    invoke-static {v0, p0}, Landroid/support/v7/internal/widget/TintManager;->arrayContains([II)Z

    #@15
    move-result v0

    #@16
    if-nez v0, :cond_0

    #@18
    sget-object v0, Landroid/support/v7/internal/widget/TintManager;->TINT_COLOR_CONTROL_STATE_LIST:[I

    #@1a
    invoke-static {v0, p0}, Landroid/support/v7/internal/widget/TintManager;->arrayContains([II)Z

    #@1d
    move-result v0

    #@1e
    if-nez v0, :cond_0

    #@20
    sget-object v0, Landroid/support/v7/internal/widget/TintManager;->CONTAINERS_WITH_TINT_CHILDREN:[I

    #@22
    invoke-static {v0, p0}, Landroid/support/v7/internal/widget/TintManager;->arrayContains([II)Z

    #@25
    move-result v0

    #@26
    if-eqz v0, :cond_1

    #@28
    :cond_0
    const/4 v0, 0x1

    #@29
    :goto_0
    return v0

    #@2a
    :cond_1
    const/4 v0, 0x0

    #@2b
    goto :goto_0
.end method


# virtual methods
.method getDisabledThemeAttrColor(I)I
    .locals 5
    .param p1, "attr"    # I

    #@0
    .prologue
    .line 341
    iget-object v1, p0, Landroid/support/v7/internal/widget/TintManager;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {v1}, Landroid/content/Context;->getTheme()Landroid/content/res/Resources$Theme;

    #@5
    move-result-object v1

    #@6
    const v2, 0x1010033

    #@9
    iget-object v3, p0, Landroid/support/v7/internal/widget/TintManager;->mTypedValue:Landroid/util/TypedValue;

    #@b
    const/4 v4, 0x1

    #@c
    invoke-virtual {v1, v2, v3, v4}, Landroid/content/res/Resources$Theme;->resolveAttribute(ILandroid/util/TypedValue;Z)Z

    #@f
    .line 342
    iget-object v1, p0, Landroid/support/v7/internal/widget/TintManager;->mTypedValue:Landroid/util/TypedValue;

    #@11
    invoke-virtual {v1}, Landroid/util/TypedValue;->getFloat()F

    #@14
    move-result v0

    #@15
    .line 344
    .local v0, "disabledAlpha":F
    invoke-virtual {p0, p1, v0}, Landroid/support/v7/internal/widget/TintManager;->getThemeAttrColor(IF)I

    #@18
    move-result v1

    #@19
    return v1
.end method

.method public getDrawable(I)Landroid/graphics/drawable/Drawable;
    .locals 4
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 133
    iget-object v2, p0, Landroid/support/v7/internal/widget/TintManager;->mContext:Landroid/content/Context;

    #@2
    invoke-static {v2, p1}, Landroid/support/v4/content/ContextCompat;->getDrawable(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    .line 135
    .local v0, "drawable":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_0

    #@8
    .line 136
    sget-object v2, Landroid/support/v7/internal/widget/TintManager;->TINT_COLOR_CONTROL_STATE_LIST:[I

    #@a
    invoke-static {v2, p1}, Landroid/support/v7/internal/widget/TintManager;->arrayContains([II)Z

    #@d
    move-result v2

    #@e
    if-eqz v2, :cond_1

    #@10
    .line 137
    new-instance v1, Landroid/support/v7/internal/widget/TintDrawableWrapper;

    #@12
    invoke-direct {p0}, Landroid/support/v7/internal/widget/TintManager;->getDefaultColorStateList()Landroid/content/res/ColorStateList;

    #@15
    move-result-object v2

    #@16
    invoke-direct {v1, v0, v2}, Landroid/support/v7/internal/widget/TintDrawableWrapper;-><init>(Landroid/graphics/drawable/Drawable;Landroid/content/res/ColorStateList;)V

    #@19
    .end local v0    # "drawable":Landroid/graphics/drawable/Drawable;
    .local v1, "drawable":Landroid/graphics/drawable/Drawable;
    move-object v0, v1

    #@1a
    .line 149
    .end local v1    # "drawable":Landroid/graphics/drawable/Drawable;
    .restart local v0    # "drawable":Landroid/graphics/drawable/Drawable;
    :cond_0
    :goto_0
    return-object v0

    #@1b
    .line 138
    :cond_1
    sget v2, Landroid/support/v7/appcompat/R$drawable;->abc_switch_track_mtrl_alpha:I

    #@1d
    if-ne p1, v2, :cond_2

    #@1f
    .line 139
    new-instance v1, Landroid/support/v7/internal/widget/TintDrawableWrapper;

    #@21
    invoke-direct {p0}, Landroid/support/v7/internal/widget/TintManager;->getSwitchTrackColorStateList()Landroid/content/res/ColorStateList;

    #@24
    move-result-object v2

    #@25
    invoke-direct {v1, v0, v2}, Landroid/support/v7/internal/widget/TintDrawableWrapper;-><init>(Landroid/graphics/drawable/Drawable;Landroid/content/res/ColorStateList;)V

    #@28
    .end local v0    # "drawable":Landroid/graphics/drawable/Drawable;
    .restart local v1    # "drawable":Landroid/graphics/drawable/Drawable;
    move-object v0, v1

    #@29
    .end local v1    # "drawable":Landroid/graphics/drawable/Drawable;
    .restart local v0    # "drawable":Landroid/graphics/drawable/Drawable;
    goto :goto_0

    #@2a
    .line 140
    :cond_2
    sget v2, Landroid/support/v7/appcompat/R$drawable;->abc_switch_thumb_material:I

    #@2c
    if-ne p1, v2, :cond_3

    #@2e
    .line 141
    new-instance v1, Landroid/support/v7/internal/widget/TintDrawableWrapper;

    #@30
    invoke-direct {p0}, Landroid/support/v7/internal/widget/TintManager;->getSwitchThumbColorStateList()Landroid/content/res/ColorStateList;

    #@33
    move-result-object v2

    #@34
    sget-object v3, Landroid/graphics/PorterDuff$Mode;->MULTIPLY:Landroid/graphics/PorterDuff$Mode;

    #@36
    invoke-direct {v1, v0, v2, v3}, Landroid/support/v7/internal/widget/TintDrawableWrapper;-><init>(Landroid/graphics/drawable/Drawable;Landroid/content/res/ColorStateList;Landroid/graphics/PorterDuff$Mode;)V

    #@39
    .end local v0    # "drawable":Landroid/graphics/drawable/Drawable;
    .restart local v1    # "drawable":Landroid/graphics/drawable/Drawable;
    move-object v0, v1

    #@3a
    .end local v1    # "drawable":Landroid/graphics/drawable/Drawable;
    .restart local v0    # "drawable":Landroid/graphics/drawable/Drawable;
    goto :goto_0

    #@3b
    .line 143
    :cond_3
    sget-object v2, Landroid/support/v7/internal/widget/TintManager;->CONTAINERS_WITH_TINT_CHILDREN:[I

    #@3d
    invoke-static {v2, p1}, Landroid/support/v7/internal/widget/TintManager;->arrayContains([II)Z

    #@40
    move-result v2

    #@41
    if-eqz v2, :cond_4

    #@43
    .line 144
    iget-object v2, p0, Landroid/support/v7/internal/widget/TintManager;->mResources:Landroid/content/res/Resources;

    #@45
    invoke-virtual {v2, p1}, Landroid/content/res/Resources;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@48
    move-result-object v0

    #@49
    goto :goto_0

    #@4a
    .line 146
    :cond_4
    invoke-virtual {p0, p1, v0}, Landroid/support/v7/internal/widget/TintManager;->tintDrawable(ILandroid/graphics/drawable/Drawable;)V

    #@4d
    goto :goto_0
.end method

.method getThemeAttrColor(I)I
    .locals 3
    .param p1, "attr"    # I

    #@0
    .prologue
    .line 320
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintManager;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {v0}, Landroid/content/Context;->getTheme()Landroid/content/res/Resources$Theme;

    #@5
    move-result-object v0

    #@6
    iget-object v1, p0, Landroid/support/v7/internal/widget/TintManager;->mTypedValue:Landroid/util/TypedValue;

    #@8
    const/4 v2, 0x1

    #@9
    invoke-virtual {v0, p1, v1, v2}, Landroid/content/res/Resources$Theme;->resolveAttribute(ILandroid/util/TypedValue;Z)Z

    #@c
    move-result v0

    #@d
    if-eqz v0, :cond_1

    #@f
    .line 321
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintManager;->mTypedValue:Landroid/util/TypedValue;

    #@11
    iget v0, v0, Landroid/util/TypedValue;->type:I

    #@13
    const/16 v1, 0x10

    #@15
    if-lt v0, v1, :cond_0

    #@17
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintManager;->mTypedValue:Landroid/util/TypedValue;

    #@19
    iget v0, v0, Landroid/util/TypedValue;->type:I

    #@1b
    const/16 v1, 0x1f

    #@1d
    if-gt v0, v1, :cond_0

    #@1f
    .line 323
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintManager;->mTypedValue:Landroid/util/TypedValue;

    #@21
    iget v0, v0, Landroid/util/TypedValue;->data:I

    #@23
    .line 328
    :goto_0
    return v0

    #@24
    .line 324
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintManager;->mTypedValue:Landroid/util/TypedValue;

    #@26
    iget v0, v0, Landroid/util/TypedValue;->type:I

    #@28
    const/4 v1, 0x3

    #@29
    if-ne v0, v1, :cond_1

    #@2b
    .line 325
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintManager;->mResources:Landroid/content/res/Resources;

    #@2d
    iget-object v1, p0, Landroid/support/v7/internal/widget/TintManager;->mTypedValue:Landroid/util/TypedValue;

    #@2f
    iget v1, v1, Landroid/util/TypedValue;->resourceId:I

    #@31
    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getColor(I)I

    #@34
    move-result v0

    #@35
    goto :goto_0

    #@36
    .line 328
    :cond_1
    const/4 v0, 0x0

    #@37
    goto :goto_0
.end method

.method getThemeAttrColor(IF)I
    .locals 4
    .param p1, "attr"    # I
    .param p2, "alpha"    # F

    #@0
    .prologue
    .line 332
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/TintManager;->getThemeAttrColor(I)I

    #@3
    move-result v0

    #@4
    .line 333
    .local v0, "color":I
    invoke-static {v0}, Landroid/graphics/Color;->alpha(I)I

    #@7
    move-result v1

    #@8
    .line 336
    .local v1, "originalAlpha":I
    const v2, 0xffffff

    #@b
    and-int/2addr v2, v0

    #@c
    int-to-float v3, v1

    #@d
    mul-float/2addr v3, p2

    #@e
    invoke-static {v3}, Ljava/lang/Math;->round(F)I

    #@11
    move-result v3

    #@12
    shl-int/lit8 v3, v3, 0x18

    #@14
    or-int/2addr v2, v3

    #@15
    return v2
.end method

.method tintDrawable(ILandroid/graphics/drawable/Drawable;)V
    .locals 7
    .param p1, "resId"    # I
    .param p2, "drawable"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 153
    const/4 v5, 0x0

    #@1
    .line 154
    .local v5, "tintMode":Landroid/graphics/PorterDuff$Mode;
    const/4 v3, 0x0

    #@2
    .line 155
    .local v3, "colorAttrSet":Z
    const/4 v2, 0x0

    #@3
    .line 156
    .local v2, "colorAttr":I
    const/4 v0, -0x1

    #@4
    .line 158
    .local v0, "alpha":I
    sget-object v6, Landroid/support/v7/internal/widget/TintManager;->TINT_COLOR_CONTROL_NORMAL:[I

    #@6
    invoke-static {v6, p1}, Landroid/support/v7/internal/widget/TintManager;->arrayContains([II)Z

    #@9
    move-result v6

    #@a
    if-eqz v6, :cond_4

    #@c
    .line 159
    sget v2, Landroid/support/v7/appcompat/R$attr;->colorControlNormal:I

    #@e
    .line 160
    const/4 v3, 0x1

    #@f
    .line 174
    :cond_0
    :goto_0
    if-eqz v3, :cond_3

    #@11
    .line 175
    if-nez v5, :cond_1

    #@13
    .line 176
    sget-object v5, Landroid/support/v7/internal/widget/TintManager;->DEFAULT_MODE:Landroid/graphics/PorterDuff$Mode;

    #@15
    .line 178
    :cond_1
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/TintManager;->getThemeAttrColor(I)I

    #@18
    move-result v1

    #@19
    .line 181
    .local v1, "color":I
    sget-object v6, Landroid/support/v7/internal/widget/TintManager;->COLOR_FILTER_CACHE:Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;

    #@1b
    invoke-virtual {v6, v1, v5}, Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;->get(ILandroid/graphics/PorterDuff$Mode;)Landroid/graphics/PorterDuffColorFilter;

    #@1e
    move-result-object v4

    #@1f
    .line 183
    .local v4, "filter":Landroid/graphics/PorterDuffColorFilter;
    if-nez v4, :cond_2

    #@21
    .line 185
    new-instance v4, Landroid/graphics/PorterDuffColorFilter;

    #@23
    .end local v4    # "filter":Landroid/graphics/PorterDuffColorFilter;
    invoke-direct {v4, v1, v5}, Landroid/graphics/PorterDuffColorFilter;-><init>(ILandroid/graphics/PorterDuff$Mode;)V

    #@26
    .line 186
    .restart local v4    # "filter":Landroid/graphics/PorterDuffColorFilter;
    sget-object v6, Landroid/support/v7/internal/widget/TintManager;->COLOR_FILTER_CACHE:Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;

    #@28
    invoke-virtual {v6, v1, v5, v4}, Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;->put(ILandroid/graphics/PorterDuff$Mode;Landroid/graphics/PorterDuffColorFilter;)Landroid/graphics/PorterDuffColorFilter;

    #@2b
    .line 190
    :cond_2
    invoke-virtual {p2, v4}, Landroid/graphics/drawable/Drawable;->setColorFilter(Landroid/graphics/ColorFilter;)V

    #@2e
    .line 192
    const/4 v6, -0x1

    #@2f
    if-eq v0, v6, :cond_3

    #@31
    .line 193
    invoke-virtual {p2, v0}, Landroid/graphics/drawable/Drawable;->setAlpha(I)V

    #@34
    .line 201
    .end local v1    # "color":I
    .end local v4    # "filter":Landroid/graphics/PorterDuffColorFilter;
    :cond_3
    return-void

    #@35
    .line 161
    :cond_4
    sget-object v6, Landroid/support/v7/internal/widget/TintManager;->TINT_COLOR_CONTROL_ACTIVATED:[I

    #@37
    invoke-static {v6, p1}, Landroid/support/v7/internal/widget/TintManager;->arrayContains([II)Z

    #@3a
    move-result v6

    #@3b
    if-eqz v6, :cond_5

    #@3d
    .line 162
    sget v2, Landroid/support/v7/appcompat/R$attr;->colorControlActivated:I

    #@3f
    .line 163
    const/4 v3, 0x1

    #@40
    goto :goto_0

    #@41
    .line 164
    :cond_5
    sget-object v6, Landroid/support/v7/internal/widget/TintManager;->TINT_COLOR_BACKGROUND_MULTIPLY:[I

    #@43
    invoke-static {v6, p1}, Landroid/support/v7/internal/widget/TintManager;->arrayContains([II)Z

    #@46
    move-result v6

    #@47
    if-eqz v6, :cond_6

    #@49
    .line 165
    const v2, 0x1010031

    #@4c
    .line 166
    const/4 v3, 0x1

    #@4d
    .line 167
    sget-object v5, Landroid/graphics/PorterDuff$Mode;->MULTIPLY:Landroid/graphics/PorterDuff$Mode;

    #@4f
    goto :goto_0

    #@50
    .line 168
    :cond_6
    sget v6, Landroid/support/v7/appcompat/R$drawable;->abc_list_divider_mtrl_alpha:I

    #@52
    if-ne p1, v6, :cond_0

    #@54
    .line 169
    const v2, 0x1010030

    #@57
    .line 170
    const/4 v3, 0x1

    #@58
    .line 171
    const v6, 0x42233333    # 40.8f

    #@5b
    invoke-static {v6}, Ljava/lang/Math;->round(F)I

    #@5e
    move-result v0

    #@5f
    goto :goto_0
.end method
