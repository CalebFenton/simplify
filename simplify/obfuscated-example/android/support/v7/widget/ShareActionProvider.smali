.class public Landroid/support/v7/widget/ShareActionProvider;
.super Landroid/support/v4/view/ActionProvider;
.source "ShareActionProvider.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/widget/ShareActionProvider$1;,
        Landroid/support/v7/widget/ShareActionProvider$ShareActivityChooserModelPolicy;,
        Landroid/support/v7/widget/ShareActionProvider$ShareMenuItemOnMenuItemClickListener;,
        Landroid/support/v7/widget/ShareActionProvider$OnShareTargetSelectedListener;
    }
.end annotation


# static fields
.field private static final DEFAULT_INITIAL_ACTIVITY_COUNT:I = 0x4

.field public static final DEFAULT_SHARE_HISTORY_FILE_NAME:Ljava/lang/String; = "share_history.xml"


# instance fields
.field private final mContext:Landroid/content/Context;

.field private mMaxShownActivityCount:I

.field private mOnChooseActivityListener:Landroid/support/v7/internal/widget/ActivityChooserModel$OnChooseActivityListener;

.field private final mOnMenuItemClickListener:Landroid/support/v7/widget/ShareActionProvider$ShareMenuItemOnMenuItemClickListener;

.field private mOnShareTargetSelectedListener:Landroid/support/v7/widget/ShareActionProvider$OnShareTargetSelectedListener;

.field private mShareHistoryFileName:Ljava/lang/String;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 157
    invoke-direct {p0, p1}, Landroid/support/v4/view/ActionProvider;-><init>(Landroid/content/Context;)V

    #@3
    .line 124
    const/4 v0, 0x4

    #@4
    iput v0, p0, Landroid/support/v7/widget/ShareActionProvider;->mMaxShownActivityCount:I

    #@6
    .line 129
    new-instance v0, Landroid/support/v7/widget/ShareActionProvider$ShareMenuItemOnMenuItemClickListener;

    #@8
    const/4 v1, 0x0

    #@9
    invoke-direct {v0, p0, v1}, Landroid/support/v7/widget/ShareActionProvider$ShareMenuItemOnMenuItemClickListener;-><init>(Landroid/support/v7/widget/ShareActionProvider;Landroid/support/v7/widget/ShareActionProvider$1;)V

    #@c
    iput-object v0, p0, Landroid/support/v7/widget/ShareActionProvider;->mOnMenuItemClickListener:Landroid/support/v7/widget/ShareActionProvider$ShareMenuItemOnMenuItemClickListener;

    #@e
    .line 145
    const-string v0, "share_history.xml"

    #@10
    iput-object v0, p0, Landroid/support/v7/widget/ShareActionProvider;->mShareHistoryFileName:Ljava/lang/String;

    #@12
    .line 158
    iput-object p1, p0, Landroid/support/v7/widget/ShareActionProvider;->mContext:Landroid/content/Context;

    #@14
    .line 159
    return-void
.end method

.method static synthetic access$100(Landroid/support/v7/widget/ShareActionProvider;)Landroid/content/Context;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ShareActionProvider;

    #@0
    .prologue
    .line 87
    iget-object v0, p0, Landroid/support/v7/widget/ShareActionProvider;->mContext:Landroid/content/Context;

    #@2
    return-object v0
.end method

.method static synthetic access$200(Landroid/support/v7/widget/ShareActionProvider;)Ljava/lang/String;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ShareActionProvider;

    #@0
    .prologue
    .line 87
    iget-object v0, p0, Landroid/support/v7/widget/ShareActionProvider;->mShareHistoryFileName:Ljava/lang/String;

    #@2
    return-object v0
.end method

.method static synthetic access$400(Landroid/support/v7/widget/ShareActionProvider;)Landroid/support/v7/widget/ShareActionProvider$OnShareTargetSelectedListener;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ShareActionProvider;

    #@0
    .prologue
    .line 87
    iget-object v0, p0, Landroid/support/v7/widget/ShareActionProvider;->mOnShareTargetSelectedListener:Landroid/support/v7/widget/ShareActionProvider$OnShareTargetSelectedListener;

    #@2
    return-object v0
.end method

.method private setActivityChooserPolicyIfNeeded()V
    .locals 3

    #@0
    .prologue
    .line 330
    iget-object v1, p0, Landroid/support/v7/widget/ShareActionProvider;->mOnShareTargetSelectedListener:Landroid/support/v7/widget/ShareActionProvider$OnShareTargetSelectedListener;

    #@2
    if-nez v1, :cond_0

    #@4
    .line 338
    :goto_0
    return-void

    #@5
    .line 333
    :cond_0
    iget-object v1, p0, Landroid/support/v7/widget/ShareActionProvider;->mOnChooseActivityListener:Landroid/support/v7/internal/widget/ActivityChooserModel$OnChooseActivityListener;

    #@7
    if-nez v1, :cond_1

    #@9
    .line 334
    new-instance v1, Landroid/support/v7/widget/ShareActionProvider$ShareActivityChooserModelPolicy;

    #@b
    const/4 v2, 0x0

    #@c
    invoke-direct {v1, p0, v2}, Landroid/support/v7/widget/ShareActionProvider$ShareActivityChooserModelPolicy;-><init>(Landroid/support/v7/widget/ShareActionProvider;Landroid/support/v7/widget/ShareActionProvider$1;)V

    #@f
    iput-object v1, p0, Landroid/support/v7/widget/ShareActionProvider;->mOnChooseActivityListener:Landroid/support/v7/internal/widget/ActivityChooserModel$OnChooseActivityListener;

    #@11
    .line 336
    :cond_1
    iget-object v1, p0, Landroid/support/v7/widget/ShareActionProvider;->mContext:Landroid/content/Context;

    #@13
    iget-object v2, p0, Landroid/support/v7/widget/ShareActionProvider;->mShareHistoryFileName:Ljava/lang/String;

    #@15
    invoke-static {v1, v2}, Landroid/support/v7/internal/widget/ActivityChooserModel;->get(Landroid/content/Context;Ljava/lang/String;)Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@18
    move-result-object v0

    #@19
    .line 337
    .local v0, "dataModel":Landroid/support/v7/internal/widget/ActivityChooserModel;
    iget-object v1, p0, Landroid/support/v7/widget/ShareActionProvider;->mOnChooseActivityListener:Landroid/support/v7/internal/widget/ActivityChooserModel$OnChooseActivityListener;

    #@1b
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ActivityChooserModel;->setOnChooseActivityListener(Landroid/support/v7/internal/widget/ActivityChooserModel$OnChooseActivityListener;)V

    #@1e
    goto :goto_0
.end method


# virtual methods
.method public hasSubMenu()Z
    .locals 1

    #@0
    .prologue
    .line 207
    const/4 v0, 0x1

    #@1
    return v0
.end method

.method public onCreateActionView()Landroid/view/View;
    .locals 7

    #@0
    .prologue
    .line 182
    iget-object v4, p0, Landroid/support/v7/widget/ShareActionProvider;->mContext:Landroid/content/Context;

    #@2
    iget-object v5, p0, Landroid/support/v7/widget/ShareActionProvider;->mShareHistoryFileName:Ljava/lang/String;

    #@4
    invoke-static {v4, v5}, Landroid/support/v7/internal/widget/ActivityChooserModel;->get(Landroid/content/Context;Ljava/lang/String;)Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@7
    move-result-object v1

    #@8
    .line 183
    .local v1, "dataModel":Landroid/support/v7/internal/widget/ActivityChooserModel;
    new-instance v0, Landroid/support/v7/internal/widget/ActivityChooserView;

    #@a
    iget-object v4, p0, Landroid/support/v7/widget/ShareActionProvider;->mContext:Landroid/content/Context;

    #@c
    invoke-direct {v0, v4}, Landroid/support/v7/internal/widget/ActivityChooserView;-><init>(Landroid/content/Context;)V

    #@f
    .line 184
    .local v0, "activityChooserView":Landroid/support/v7/internal/widget/ActivityChooserView;
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ActivityChooserView;->setActivityChooserModel(Landroid/support/v7/internal/widget/ActivityChooserModel;)V

    #@12
    .line 187
    new-instance v3, Landroid/util/TypedValue;

    #@14
    invoke-direct {v3}, Landroid/util/TypedValue;-><init>()V

    #@17
    .line 188
    .local v3, "outTypedValue":Landroid/util/TypedValue;
    iget-object v4, p0, Landroid/support/v7/widget/ShareActionProvider;->mContext:Landroid/content/Context;

    #@19
    invoke-virtual {v4}, Landroid/content/Context;->getTheme()Landroid/content/res/Resources$Theme;

    #@1c
    move-result-object v4

    #@1d
    sget v5, Landroid/support/v7/appcompat/R$attr;->actionModeShareDrawable:I

    #@1f
    const/4 v6, 0x1

    #@20
    invoke-virtual {v4, v5, v3, v6}, Landroid/content/res/Resources$Theme;->resolveAttribute(ILandroid/util/TypedValue;Z)Z

    #@23
    .line 189
    iget-object v4, p0, Landroid/support/v7/widget/ShareActionProvider;->mContext:Landroid/content/Context;

    #@25
    iget v5, v3, Landroid/util/TypedValue;->resourceId:I

    #@27
    invoke-static {v4, v5}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;

    #@2a
    move-result-object v2

    #@2b
    .line 190
    .local v2, "drawable":Landroid/graphics/drawable/Drawable;
    invoke-virtual {v0, v2}, Landroid/support/v7/internal/widget/ActivityChooserView;->setExpandActivityOverflowButtonDrawable(Landroid/graphics/drawable/Drawable;)V

    #@2e
    .line 191
    invoke-virtual {v0, p0}, Landroid/support/v7/internal/widget/ActivityChooserView;->setProvider(Landroid/support/v4/view/ActionProvider;)V

    #@31
    .line 194
    sget v4, Landroid/support/v7/appcompat/R$string;->abc_shareactionprovider_share_with_application:I

    #@33
    invoke-virtual {v0, v4}, Landroid/support/v7/internal/widget/ActivityChooserView;->setDefaultActionButtonContentDescription(I)V

    #@36
    .line 196
    sget v4, Landroid/support/v7/appcompat/R$string;->abc_shareactionprovider_share_with:I

    #@38
    invoke-virtual {v0, v4}, Landroid/support/v7/internal/widget/ActivityChooserView;->setExpandActivityOverflowButtonContentDescription(I)V

    #@3b
    .line 199
    return-object v0
.end method

.method public onPrepareSubMenu(Landroid/view/SubMenu;)V
    .locals 10
    .param p1, "subMenu"    # Landroid/view/SubMenu;

    #@0
    .prologue
    const/4 v9, 0x0

    #@1
    .line 216
    invoke-interface {p1}, Landroid/view/SubMenu;->clear()V

    #@4
    .line 218
    iget-object v7, p0, Landroid/support/v7/widget/ShareActionProvider;->mContext:Landroid/content/Context;

    #@6
    iget-object v8, p0, Landroid/support/v7/widget/ShareActionProvider;->mShareHistoryFileName:Ljava/lang/String;

    #@8
    invoke-static {v7, v8}, Landroid/support/v7/internal/widget/ActivityChooserModel;->get(Landroid/content/Context;Ljava/lang/String;)Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@b
    move-result-object v2

    #@c
    .line 219
    .local v2, "dataModel":Landroid/support/v7/internal/widget/ActivityChooserModel;
    iget-object v7, p0, Landroid/support/v7/widget/ShareActionProvider;->mContext:Landroid/content/Context;

    #@e
    invoke-virtual {v7}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    #@11
    move-result-object v6

    #@12
    .line 221
    .local v6, "packageManager":Landroid/content/pm/PackageManager;
    invoke-virtual {v2}, Landroid/support/v7/internal/widget/ActivityChooserModel;->getActivityCount()I

    #@15
    move-result v3

    #@16
    .line 222
    .local v3, "expandedActivityCount":I
    iget v7, p0, Landroid/support/v7/widget/ShareActionProvider;->mMaxShownActivityCount:I

    #@18
    invoke-static {v3, v7}, Ljava/lang/Math;->min(II)I

    #@1b
    move-result v1

    #@1c
    .line 225
    .local v1, "collapsedActivityCount":I
    const/4 v5, 0x0

    #@1d
    .local v5, "i":I
    :goto_0
    if-ge v5, v1, :cond_0

    #@1f
    .line 226
    invoke-virtual {v2, v5}, Landroid/support/v7/internal/widget/ActivityChooserModel;->getActivity(I)Landroid/content/pm/ResolveInfo;

    #@22
    move-result-object v0

    #@23
    .line 227
    .local v0, "activity":Landroid/content/pm/ResolveInfo;
    invoke-virtual {v0, v6}, Landroid/content/pm/ResolveInfo;->loadLabel(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;

    #@26
    move-result-object v7

    #@27
    invoke-interface {p1, v9, v5, v5, v7}, Landroid/view/SubMenu;->add(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    #@2a
    move-result-object v7

    #@2b
    invoke-virtual {v0, v6}, Landroid/content/pm/ResolveInfo;->loadIcon(Landroid/content/pm/PackageManager;)Landroid/graphics/drawable/Drawable;

    #@2e
    move-result-object v8

    #@2f
    invoke-interface {v7, v8}, Landroid/view/MenuItem;->setIcon(Landroid/graphics/drawable/Drawable;)Landroid/view/MenuItem;

    #@32
    move-result-object v7

    #@33
    iget-object v8, p0, Landroid/support/v7/widget/ShareActionProvider;->mOnMenuItemClickListener:Landroid/support/v7/widget/ShareActionProvider$ShareMenuItemOnMenuItemClickListener;

    #@35
    invoke-interface {v7, v8}, Landroid/view/MenuItem;->setOnMenuItemClickListener(Landroid/view/MenuItem$OnMenuItemClickListener;)Landroid/view/MenuItem;

    #@38
    .line 225
    add-int/lit8 v5, v5, 0x1

    #@3a
    goto :goto_0

    #@3b
    .line 232
    .end local v0    # "activity":Landroid/content/pm/ResolveInfo;
    :cond_0
    if-ge v1, v3, :cond_1

    #@3d
    .line 234
    iget-object v7, p0, Landroid/support/v7/widget/ShareActionProvider;->mContext:Landroid/content/Context;

    #@3f
    sget v8, Landroid/support/v7/appcompat/R$string;->abc_activity_chooser_view_see_all:I

    #@41
    invoke-virtual {v7, v8}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    #@44
    move-result-object v7

    #@45
    invoke-interface {p1, v9, v1, v1, v7}, Landroid/view/SubMenu;->addSubMenu(IIILjava/lang/CharSequence;)Landroid/view/SubMenu;

    #@48
    move-result-object v4

    #@49
    .line 237
    .local v4, "expandedSubMenu":Landroid/view/SubMenu;
    const/4 v5, 0x0

    #@4a
    :goto_1
    if-ge v5, v3, :cond_1

    #@4c
    .line 238
    invoke-virtual {v2, v5}, Landroid/support/v7/internal/widget/ActivityChooserModel;->getActivity(I)Landroid/content/pm/ResolveInfo;

    #@4f
    move-result-object v0

    #@50
    .line 239
    .restart local v0    # "activity":Landroid/content/pm/ResolveInfo;
    invoke-virtual {v0, v6}, Landroid/content/pm/ResolveInfo;->loadLabel(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;

    #@53
    move-result-object v7

    #@54
    invoke-interface {v4, v9, v5, v5, v7}, Landroid/view/SubMenu;->add(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    #@57
    move-result-object v7

    #@58
    invoke-virtual {v0, v6}, Landroid/content/pm/ResolveInfo;->loadIcon(Landroid/content/pm/PackageManager;)Landroid/graphics/drawable/Drawable;

    #@5b
    move-result-object v8

    #@5c
    invoke-interface {v7, v8}, Landroid/view/MenuItem;->setIcon(Landroid/graphics/drawable/Drawable;)Landroid/view/MenuItem;

    #@5f
    move-result-object v7

    #@60
    iget-object v8, p0, Landroid/support/v7/widget/ShareActionProvider;->mOnMenuItemClickListener:Landroid/support/v7/widget/ShareActionProvider$ShareMenuItemOnMenuItemClickListener;

    #@62
    invoke-interface {v7, v8}, Landroid/view/MenuItem;->setOnMenuItemClickListener(Landroid/view/MenuItem$OnMenuItemClickListener;)Landroid/view/MenuItem;

    #@65
    .line 237
    add-int/lit8 v5, v5, 0x1

    #@67
    goto :goto_1

    #@68
    .line 244
    .end local v0    # "activity":Landroid/content/pm/ResolveInfo;
    .end local v4    # "expandedSubMenu":Landroid/view/SubMenu;
    :cond_1
    return-void
.end method

.method public setOnShareTargetSelectedListener(Landroid/support/v7/widget/ShareActionProvider$OnShareTargetSelectedListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v7/widget/ShareActionProvider$OnShareTargetSelectedListener;

    #@0
    .prologue
    .line 172
    iput-object p1, p0, Landroid/support/v7/widget/ShareActionProvider;->mOnShareTargetSelectedListener:Landroid/support/v7/widget/ShareActionProvider$OnShareTargetSelectedListener;

    #@2
    .line 173
    invoke-direct {p0}, Landroid/support/v7/widget/ShareActionProvider;->setActivityChooserPolicyIfNeeded()V

    #@5
    .line 174
    return-void
.end method

.method public setShareHistoryFileName(Ljava/lang/String;)V
    .locals 0
    .param p1, "shareHistoryFile"    # Ljava/lang/String;

    #@0
    .prologue
    .line 278
    iput-object p1, p0, Landroid/support/v7/widget/ShareActionProvider;->mShareHistoryFileName:Ljava/lang/String;

    #@2
    .line 279
    invoke-direct {p0}, Landroid/support/v7/widget/ShareActionProvider;->setActivityChooserPolicyIfNeeded()V

    #@5
    .line 280
    return-void
.end method

.method public setShareIntent(Landroid/content/Intent;)V
    .locals 3
    .param p1, "shareIntent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 302
    iget-object v1, p0, Landroid/support/v7/widget/ShareActionProvider;->mContext:Landroid/content/Context;

    #@2
    iget-object v2, p0, Landroid/support/v7/widget/ShareActionProvider;->mShareHistoryFileName:Ljava/lang/String;

    #@4
    invoke-static {v1, v2}, Landroid/support/v7/internal/widget/ActivityChooserModel;->get(Landroid/content/Context;Ljava/lang/String;)Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@7
    move-result-object v0

    #@8
    .line 304
    .local v0, "dataModel":Landroid/support/v7/internal/widget/ActivityChooserModel;
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/ActivityChooserModel;->setIntent(Landroid/content/Intent;)V

    #@b
    .line 305
    return-void
.end method
