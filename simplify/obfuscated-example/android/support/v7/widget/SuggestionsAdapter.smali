.class Landroid/support/v7/widget/SuggestionsAdapter;
.super Landroid/support/v4/widget/ResourceCursorAdapter;
.source "SuggestionsAdapter.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;
    }
.end annotation


# static fields
.field private static final DBG:Z = false

.field static final INVALID_INDEX:I = -0x1

.field private static final LOG_TAG:Ljava/lang/String; = "SuggestionsAdapter"

.field private static final QUERY_LIMIT:I = 0x32

.field static final REFINE_ALL:I = 0x2

.field static final REFINE_BY_ENTRY:I = 0x1

.field static final REFINE_NONE:I


# instance fields
.field private mClosed:Z

.field private final mCommitIconResId:I

.field private mFlagsCol:I

.field private mIconName1Col:I

.field private mIconName2Col:I

.field private final mOutsideDrawablesCache:Ljava/util/WeakHashMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/WeakHashMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/graphics/drawable/Drawable$ConstantState;",
            ">;"
        }
    .end annotation
.end field

.field private final mProviderContext:Landroid/content/Context;

.field private mQueryRefinement:I

.field private final mSearchManager:Landroid/app/SearchManager;

.field private final mSearchView:Landroid/support/v7/widget/SearchView;

.field private final mSearchable:Landroid/app/SearchableInfo;

.field private mText1Col:I

.field private mText2Col:I

.field private mText2UrlCol:I

.field private mUrlColor:Landroid/content/res/ColorStateList;


# direct methods
.method public constructor <init>(Landroid/content/Context;Landroid/support/v7/widget/SearchView;Landroid/app/SearchableInfo;Ljava/util/WeakHashMap;)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "searchView"    # Landroid/support/v7/widget/SearchView;
    .param p3, "searchable"    # Landroid/app/SearchableInfo;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "Landroid/support/v7/widget/SearchView;",
            "Landroid/app/SearchableInfo;",
            "Ljava/util/WeakHashMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/graphics/drawable/Drawable$ConstantState;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .local p4, "outsideDrawablesCache":Ljava/util/WeakHashMap;, "Ljava/util/WeakHashMap<Ljava/lang/String;Landroid/graphics/drawable/Drawable$ConstantState;>;"
    const/4 v3, 0x1

    #@1
    const/4 v2, -0x1

    #@2
    .line 95
    invoke-virtual {p2}, Landroid/support/v7/widget/SearchView;->getSuggestionRowLayout()I

    #@5
    move-result v0

    #@6
    const/4 v1, 0x0

    #@7
    invoke-direct {p0, p1, v0, v1, v3}, Landroid/support/v4/widget/ResourceCursorAdapter;-><init>(Landroid/content/Context;ILandroid/database/Cursor;Z)V

    #@a
    .line 74
    const/4 v0, 0x0

    #@b
    iput-boolean v0, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mClosed:Z

    #@d
    .line 75
    iput v3, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mQueryRefinement:I

    #@f
    .line 83
    iput v2, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mText1Col:I

    #@11
    .line 84
    iput v2, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mText2Col:I

    #@13
    .line 85
    iput v2, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mText2UrlCol:I

    #@15
    .line 86
    iput v2, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mIconName1Col:I

    #@17
    .line 87
    iput v2, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mIconName2Col:I

    #@19
    .line 88
    iput v2, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mFlagsCol:I

    #@1b
    .line 97
    iget-object v0, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mContext:Landroid/content/Context;

    #@1d
    const-string v1, "search"

    #@1f
    invoke-virtual {v0, v1}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@22
    move-result-object v0

    #@23
    check-cast v0, Landroid/app/SearchManager;

    #@25
    iput-object v0, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mSearchManager:Landroid/app/SearchManager;

    #@27
    .line 98
    iput-object p2, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mSearchView:Landroid/support/v7/widget/SearchView;

    #@29
    .line 99
    iput-object p3, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mSearchable:Landroid/app/SearchableInfo;

    #@2b
    .line 100
    invoke-virtual {p2}, Landroid/support/v7/widget/SearchView;->getSuggestionCommitIconResId()I

    #@2e
    move-result v0

    #@2f
    iput v0, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mCommitIconResId:I

    #@31
    .line 103
    iput-object p1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mProviderContext:Landroid/content/Context;

    #@33
    .line 105
    iput-object p4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mOutsideDrawablesCache:Ljava/util/WeakHashMap;

    #@35
    .line 106
    return-void
.end method

.method private checkIconCache(Ljava/lang/String;)Landroid/graphics/drawable/Drawable;
    .locals 2
    .param p1, "resourceUri"    # Ljava/lang/String;

    #@0
    .prologue
    .line 559
    iget-object v1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mOutsideDrawablesCache:Ljava/util/WeakHashMap;

    #@2
    invoke-virtual {v1, p1}, Ljava/util/WeakHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@5
    move-result-object v0

    #@6
    check-cast v0, Landroid/graphics/drawable/Drawable$ConstantState;

    #@8
    .line 560
    .local v0, "cached":Landroid/graphics/drawable/Drawable$ConstantState;
    if-nez v0, :cond_0

    #@a
    .line 561
    const/4 v1, 0x0

    #@b
    .line 564
    :goto_0
    return-object v1

    #@c
    :cond_0
    invoke-virtual {v0}, Landroid/graphics/drawable/Drawable$ConstantState;->newDrawable()Landroid/graphics/drawable/Drawable;

    #@f
    move-result-object v1

    #@10
    goto :goto_0
.end method

.method private formatUrl(Ljava/lang/CharSequence;)Ljava/lang/CharSequence;
    .locals 8
    .param p1, "url"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    const/4 v2, 0x0

    #@2
    .line 335
    iget-object v0, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mUrlColor:Landroid/content/res/ColorStateList;

    #@4
    if-nez v0, :cond_0

    #@6
    .line 337
    new-instance v6, Landroid/util/TypedValue;

    #@8
    invoke-direct {v6}, Landroid/util/TypedValue;-><init>()V

    #@b
    .line 338
    .local v6, "colorValue":Landroid/util/TypedValue;
    iget-object v0, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mContext:Landroid/content/Context;

    #@d
    invoke-virtual {v0}, Landroid/content/Context;->getTheme()Landroid/content/res/Resources$Theme;

    #@10
    move-result-object v0

    #@11
    sget v3, Landroid/support/v7/appcompat/R$attr;->textColorSearchUrl:I

    #@13
    const/4 v4, 0x1

    #@14
    invoke-virtual {v0, v3, v6, v4}, Landroid/content/res/Resources$Theme;->resolveAttribute(ILandroid/util/TypedValue;Z)Z

    #@17
    .line 339
    iget-object v0, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mContext:Landroid/content/Context;

    #@19
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@1c
    move-result-object v0

    #@1d
    iget v3, v6, Landroid/util/TypedValue;->resourceId:I

    #@1f
    invoke-virtual {v0, v3}, Landroid/content/res/Resources;->getColorStateList(I)Landroid/content/res/ColorStateList;

    #@22
    move-result-object v0

    #@23
    iput-object v0, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mUrlColor:Landroid/content/res/ColorStateList;

    #@25
    .line 342
    .end local v6    # "colorValue":Landroid/util/TypedValue;
    :cond_0
    new-instance v7, Landroid/text/SpannableString;

    #@27
    invoke-direct {v7, p1}, Landroid/text/SpannableString;-><init>(Ljava/lang/CharSequence;)V

    #@2a
    .line 343
    .local v7, "text":Landroid/text/SpannableString;
    new-instance v0, Landroid/text/style/TextAppearanceSpan;

    #@2c
    iget-object v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mUrlColor:Landroid/content/res/ColorStateList;

    #@2e
    move v3, v2

    #@2f
    move-object v5, v1

    #@30
    invoke-direct/range {v0 .. v5}, Landroid/text/style/TextAppearanceSpan;-><init>(Ljava/lang/String;IILandroid/content/res/ColorStateList;Landroid/content/res/ColorStateList;)V

    #@33
    invoke-interface {p1}, Ljava/lang/CharSequence;->length()I

    #@36
    move-result v1

    #@37
    const/16 v3, 0x21

    #@39
    invoke-virtual {v7, v0, v2, v1, v3}, Landroid/text/SpannableString;->setSpan(Ljava/lang/Object;III)V

    #@3c
    .line 346
    return-object v7
.end method

.method private getActivityIcon(Landroid/content/ComponentName;)Landroid/graphics/drawable/Drawable;
    .locals 10
    .param p1, "component"    # Landroid/content/ComponentName;

    #@0
    .prologue
    const/4 v6, 0x0

    #@1
    .line 623
    iget-object v7, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mContext:Landroid/content/Context;

    #@3
    invoke-virtual {v7}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    #@6
    move-result-object v5

    #@7
    .line 626
    .local v5, "pm":Landroid/content/pm/PackageManager;
    const/16 v7, 0x80

    #@9
    :try_start_0
    invoke-virtual {v5, p1, v7}, Landroid/content/pm/PackageManager;->getActivityInfo(Landroid/content/ComponentName;I)Landroid/content/pm/ActivityInfo;
    :try_end_0
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    #@c
    move-result-object v0

    #@d
    .line 631
    .local v0, "activityInfo":Landroid/content/pm/ActivityInfo;
    invoke-virtual {v0}, Landroid/content/pm/ActivityInfo;->getIconResource()I

    #@10
    move-result v3

    #@11
    .line 632
    .local v3, "iconId":I
    if-nez v3, :cond_1

    #@13
    move-object v1, v6

    #@14
    .line 640
    .end local v0    # "activityInfo":Landroid/content/pm/ActivityInfo;
    .end local v3    # "iconId":I
    :cond_0
    :goto_0
    return-object v1

    #@15
    .line 627
    :catch_0
    move-exception v2

    #@16
    .line 628
    .local v2, "ex":Landroid/content/pm/PackageManager$NameNotFoundException;
    const-string v7, "SuggestionsAdapter"

    #@18
    invoke-virtual {v2}, Landroid/content/pm/PackageManager$NameNotFoundException;->toString()Ljava/lang/String;

    #@1b
    move-result-object v8

    #@1c
    invoke-static {v7, v8}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@1f
    move-object v1, v6

    #@20
    .line 629
    goto :goto_0

    #@21
    .line 633
    .end local v2    # "ex":Landroid/content/pm/PackageManager$NameNotFoundException;
    .restart local v0    # "activityInfo":Landroid/content/pm/ActivityInfo;
    .restart local v3    # "iconId":I
    :cond_1
    invoke-virtual {p1}, Landroid/content/ComponentName;->getPackageName()Ljava/lang/String;

    #@24
    move-result-object v4

    #@25
    .line 634
    .local v4, "pkg":Ljava/lang/String;
    iget-object v7, v0, Landroid/content/pm/ActivityInfo;->applicationInfo:Landroid/content/pm/ApplicationInfo;

    #@27
    invoke-virtual {v5, v4, v3, v7}, Landroid/content/pm/PackageManager;->getDrawable(Ljava/lang/String;ILandroid/content/pm/ApplicationInfo;)Landroid/graphics/drawable/Drawable;

    #@2a
    move-result-object v1

    #@2b
    .line 635
    .local v1, "drawable":Landroid/graphics/drawable/Drawable;
    if-nez v1, :cond_0

    #@2d
    .line 636
    const-string v7, "SuggestionsAdapter"

    #@2f
    new-instance v8, Ljava/lang/StringBuilder;

    #@31
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@34
    const-string v9, "Invalid icon resource "

    #@36
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@39
    move-result-object v8

    #@3a
    invoke-virtual {v8, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@3d
    move-result-object v8

    #@3e
    const-string v9, " for "

    #@40
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@43
    move-result-object v8

    #@44
    invoke-virtual {p1}, Landroid/content/ComponentName;->flattenToShortString()Ljava/lang/String;

    #@47
    move-result-object v9

    #@48
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@4b
    move-result-object v8

    #@4c
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@4f
    move-result-object v8

    #@50
    invoke-static {v7, v8}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@53
    move-object v1, v6

    #@54
    .line 638
    goto :goto_0
.end method

.method private getActivityIconWithCache(Landroid/content/ComponentName;)Landroid/graphics/drawable/Drawable;
    .locals 6
    .param p1, "component"    # Landroid/content/ComponentName;

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    .line 601
    invoke-virtual {p1}, Landroid/content/ComponentName;->flattenToShortString()Ljava/lang/String;

    #@4
    move-result-object v1

    #@5
    .line 603
    .local v1, "componentIconKey":Ljava/lang/String;
    iget-object v5, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mOutsideDrawablesCache:Ljava/util/WeakHashMap;

    #@7
    invoke-virtual {v5, v1}, Ljava/util/WeakHashMap;->containsKey(Ljava/lang/Object;)Z

    #@a
    move-result v5

    #@b
    if-eqz v5, :cond_1

    #@d
    .line 604
    iget-object v5, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mOutsideDrawablesCache:Ljava/util/WeakHashMap;

    #@f
    invoke-virtual {v5, v1}, Ljava/util/WeakHashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@12
    move-result-object v0

    #@13
    check-cast v0, Landroid/graphics/drawable/Drawable$ConstantState;

    #@15
    .line 605
    .local v0, "cached":Landroid/graphics/drawable/Drawable$ConstantState;
    if-nez v0, :cond_0

    #@17
    .line 612
    .end local v0    # "cached":Landroid/graphics/drawable/Drawable$ConstantState;
    :goto_0
    return-object v4

    #@18
    .line 605
    .restart local v0    # "cached":Landroid/graphics/drawable/Drawable$ConstantState;
    :cond_0
    iget-object v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mProviderContext:Landroid/content/Context;

    #@1a
    invoke-virtual {v4}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@1d
    move-result-object v4

    #@1e
    invoke-virtual {v0, v4}, Landroid/graphics/drawable/Drawable$ConstantState;->newDrawable(Landroid/content/res/Resources;)Landroid/graphics/drawable/Drawable;

    #@21
    move-result-object v4

    #@22
    goto :goto_0

    #@23
    .line 608
    .end local v0    # "cached":Landroid/graphics/drawable/Drawable$ConstantState;
    :cond_1
    invoke-direct {p0, p1}, Landroid/support/v7/widget/SuggestionsAdapter;->getActivityIcon(Landroid/content/ComponentName;)Landroid/graphics/drawable/Drawable;

    #@26
    move-result-object v2

    #@27
    .line 610
    .local v2, "drawable":Landroid/graphics/drawable/Drawable;
    if-nez v2, :cond_2

    #@29
    move-object v3, v4

    #@2a
    .line 611
    .local v3, "toCache":Landroid/graphics/drawable/Drawable$ConstantState;
    :goto_1
    iget-object v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mOutsideDrawablesCache:Ljava/util/WeakHashMap;

    #@2c
    invoke-virtual {v4, v1, v3}, Ljava/util/WeakHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@2f
    move-object v4, v2

    #@30
    .line 612
    goto :goto_0

    #@31
    .line 610
    .end local v3    # "toCache":Landroid/graphics/drawable/Drawable$ConstantState;
    :cond_2
    invoke-virtual {v2}, Landroid/graphics/drawable/Drawable;->getConstantState()Landroid/graphics/drawable/Drawable$ConstantState;

    #@34
    move-result-object v3

    #@35
    goto :goto_1
.end method

.method public static getColumnString(Landroid/database/Cursor;Ljava/lang/String;)Ljava/lang/String;
    .locals 2
    .param p0, "cursor"    # Landroid/database/Cursor;
    .param p1, "columnName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 652
    invoke-interface {p0, p1}, Landroid/database/Cursor;->getColumnIndex(Ljava/lang/String;)I

    #@3
    move-result v0

    #@4
    .line 653
    .local v0, "col":I
    invoke-static {p0, v0}, Landroid/support/v7/widget/SuggestionsAdapter;->getStringOrNull(Landroid/database/Cursor;I)Ljava/lang/String;

    #@7
    move-result-object v1

    #@8
    return-object v1
.end method

.method private getDefaultIcon1(Landroid/database/Cursor;)Landroid/graphics/drawable/Drawable;
    .locals 2
    .param p1, "cursor"    # Landroid/database/Cursor;

    #@0
    .prologue
    .line 582
    iget-object v1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mSearchable:Landroid/app/SearchableInfo;

    #@2
    invoke-virtual {v1}, Landroid/app/SearchableInfo;->getSearchActivity()Landroid/content/ComponentName;

    #@5
    move-result-object v1

    #@6
    invoke-direct {p0, v1}, Landroid/support/v7/widget/SuggestionsAdapter;->getActivityIconWithCache(Landroid/content/ComponentName;)Landroid/graphics/drawable/Drawable;

    #@9
    move-result-object v0

    #@a
    .line 583
    .local v0, "drawable":Landroid/graphics/drawable/Drawable;
    if-eqz v0, :cond_0

    #@c
    .line 588
    .end local v0    # "drawable":Landroid/graphics/drawable/Drawable;
    :goto_0
    return-object v0

    #@d
    .restart local v0    # "drawable":Landroid/graphics/drawable/Drawable;
    :cond_0
    iget-object v1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mContext:Landroid/content/Context;

    #@f
    invoke-virtual {v1}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    #@12
    move-result-object v1

    #@13
    invoke-virtual {v1}, Landroid/content/pm/PackageManager;->getDefaultActivityIcon()Landroid/graphics/drawable/Drawable;

    #@16
    move-result-object v0

    #@17
    goto :goto_0
.end method

.method private getDrawable(Landroid/net/Uri;)Landroid/graphics/drawable/Drawable;
    .locals 9
    .param p1, "uri"    # Landroid/net/Uri;

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    .line 526
    :try_start_0
    invoke-virtual {p1}, Landroid/net/Uri;->getScheme()Ljava/lang/String;

    #@4
    move-result-object v2

    #@5
    .line 527
    .local v2, "scheme":Ljava/lang/String;
    const-string v4, "android.resource"

    #@7
    invoke-virtual {v4, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z
    :try_end_0
    .catch Ljava/io/FileNotFoundException; {:try_start_0 .. :try_end_0} :catch_1

    #@a
    move-result v4

    #@b
    if-eqz v4, :cond_0

    #@d
    .line 530
    :try_start_1
    invoke-virtual {p0, p1}, Landroid/support/v7/widget/SuggestionsAdapter;->getDrawableFromResourceUri(Landroid/net/Uri;)Landroid/graphics/drawable/Drawable;
    :try_end_1
    .catch Landroid/content/res/Resources$NotFoundException; {:try_start_1 .. :try_end_1} :catch_0
    .catch Ljava/io/FileNotFoundException; {:try_start_1 .. :try_end_1} :catch_1

    #@10
    move-result-object v4

    #@11
    .line 552
    .end local v2    # "scheme":Ljava/lang/String;
    :goto_0
    return-object v4

    #@12
    .line 531
    .restart local v2    # "scheme":Ljava/lang/String;
    :catch_0
    move-exception v0

    #@13
    .line 532
    .local v0, "ex":Landroid/content/res/Resources$NotFoundException;
    :try_start_2
    new-instance v4, Ljava/io/FileNotFoundException;

    #@15
    new-instance v6, Ljava/lang/StringBuilder;

    #@17
    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    #@1a
    const-string v7, "Resource does not exist: "

    #@1c
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1f
    move-result-object v6

    #@20
    invoke-virtual {v6, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@23
    move-result-object v6

    #@24
    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@27
    move-result-object v6

    #@28
    invoke-direct {v4, v6}, Ljava/io/FileNotFoundException;-><init>(Ljava/lang/String;)V

    #@2b
    throw v4
    :try_end_2
    .catch Ljava/io/FileNotFoundException; {:try_start_2 .. :try_end_2} :catch_1

    #@2c
    .line 550
    .end local v0    # "ex":Landroid/content/res/Resources$NotFoundException;
    .end local v2    # "scheme":Ljava/lang/String;
    :catch_1
    move-exception v1

    #@2d
    .line 551
    .local v1, "fnfe":Ljava/io/FileNotFoundException;
    const-string v4, "SuggestionsAdapter"

    #@2f
    new-instance v6, Ljava/lang/StringBuilder;

    #@31
    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    #@34
    const-string v7, "Icon not found: "

    #@36
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@39
    move-result-object v6

    #@3a
    invoke-virtual {v6, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@3d
    move-result-object v6

    #@3e
    const-string v7, ", "

    #@40
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@43
    move-result-object v6

    #@44
    invoke-virtual {v1}, Ljava/io/FileNotFoundException;->getMessage()Ljava/lang/String;

    #@47
    move-result-object v7

    #@48
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@4b
    move-result-object v6

    #@4c
    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@4f
    move-result-object v6

    #@50
    invoke-static {v4, v6}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@53
    move-object v4, v5

    #@54
    .line 552
    goto :goto_0

    #@55
    .line 536
    .end local v1    # "fnfe":Ljava/io/FileNotFoundException;
    .restart local v2    # "scheme":Ljava/lang/String;
    :cond_0
    :try_start_3
    iget-object v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mProviderContext:Landroid/content/Context;

    #@57
    invoke-virtual {v4}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    #@5a
    move-result-object v4

    #@5b
    invoke-virtual {v4, p1}, Landroid/content/ContentResolver;->openInputStream(Landroid/net/Uri;)Ljava/io/InputStream;

    #@5e
    move-result-object v3

    #@5f
    .line 537
    .local v3, "stream":Ljava/io/InputStream;
    if-nez v3, :cond_1

    #@61
    .line 538
    new-instance v4, Ljava/io/FileNotFoundException;

    #@63
    new-instance v6, Ljava/lang/StringBuilder;

    #@65
    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    #@68
    const-string v7, "Failed to open "

    #@6a
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@6d
    move-result-object v6

    #@6e
    invoke-virtual {v6, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@71
    move-result-object v6

    #@72
    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@75
    move-result-object v6

    #@76
    invoke-direct {v4, v6}, Ljava/io/FileNotFoundException;-><init>(Ljava/lang/String;)V

    #@79
    throw v4
    :try_end_3
    .catch Ljava/io/FileNotFoundException; {:try_start_3 .. :try_end_3} :catch_1

    #@7a
    .line 541
    :cond_1
    const/4 v4, 0x0

    #@7b
    :try_start_4
    invoke-static {v3, v4}, Landroid/graphics/drawable/Drawable;->createFromStream(Ljava/io/InputStream;Ljava/lang/String;)Landroid/graphics/drawable/Drawable;
    :try_end_4
    .catchall {:try_start_4 .. :try_end_4} :catchall_0

    #@7e
    move-result-object v4

    #@7f
    .line 544
    :try_start_5
    invoke-virtual {v3}, Ljava/io/InputStream;->close()V
    :try_end_5
    .catch Ljava/io/IOException; {:try_start_5 .. :try_end_5} :catch_2
    .catch Ljava/io/FileNotFoundException; {:try_start_5 .. :try_end_5} :catch_1

    #@82
    goto :goto_0

    #@83
    .line 545
    :catch_2
    move-exception v0

    #@84
    .line 546
    .local v0, "ex":Ljava/io/IOException;
    :try_start_6
    const-string v6, "SuggestionsAdapter"

    #@86
    new-instance v7, Ljava/lang/StringBuilder;

    #@88
    invoke-direct {v7}, Ljava/lang/StringBuilder;-><init>()V

    #@8b
    const-string v8, "Error closing icon stream for "

    #@8d
    invoke-virtual {v7, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@90
    move-result-object v7

    #@91
    invoke-virtual {v7, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@94
    move-result-object v7

    #@95
    invoke-virtual {v7}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@98
    move-result-object v7

    #@99
    invoke-static {v6, v7, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    :try_end_6
    .catch Ljava/io/FileNotFoundException; {:try_start_6 .. :try_end_6} :catch_1

    #@9c
    goto/16 :goto_0

    #@9e
    .line 543
    .end local v0    # "ex":Ljava/io/IOException;
    :catchall_0
    move-exception v4

    #@9f
    .line 544
    :try_start_7
    invoke-virtual {v3}, Ljava/io/InputStream;->close()V
    :try_end_7
    .catch Ljava/io/IOException; {:try_start_7 .. :try_end_7} :catch_3
    .catch Ljava/io/FileNotFoundException; {:try_start_7 .. :try_end_7} :catch_1

    #@a2
    .line 547
    :goto_1
    :try_start_8
    throw v4

    #@a3
    .line 545
    :catch_3
    move-exception v0

    #@a4
    .line 546
    .restart local v0    # "ex":Ljava/io/IOException;
    const-string v6, "SuggestionsAdapter"

    #@a6
    new-instance v7, Ljava/lang/StringBuilder;

    #@a8
    invoke-direct {v7}, Ljava/lang/StringBuilder;-><init>()V

    #@ab
    const-string v8, "Error closing icon stream for "

    #@ad
    invoke-virtual {v7, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@b0
    move-result-object v7

    #@b1
    invoke-virtual {v7, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@b4
    move-result-object v7

    #@b5
    invoke-virtual {v7}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@b8
    move-result-object v7

    #@b9
    invoke-static {v6, v7, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    :try_end_8
    .catch Ljava/io/FileNotFoundException; {:try_start_8 .. :try_end_8} :catch_1

    #@bc
    goto :goto_1
.end method

.method private getDrawableFromResourceValue(Ljava/lang/String;)Landroid/graphics/drawable/Drawable;
    .locals 9
    .param p1, "drawableId"    # Ljava/lang/String;

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    .line 483
    if-eqz p1, :cond_0

    #@3
    invoke-virtual {p1}, Ljava/lang/String;->length()I

    #@6
    move-result v6

    #@7
    if-eqz v6, :cond_0

    #@9
    const-string v6, "0"

    #@b
    invoke-virtual {v6, p1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@e
    move-result v6

    #@f
    if-eqz v6, :cond_2

    #@11
    :cond_0
    move-object v0, v5

    #@12
    .line 515
    :cond_1
    :goto_0
    return-object v0

    #@13
    .line 488
    :cond_2
    :try_start_0
    invoke-static {p1}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I

    #@16
    move-result v3

    #@17
    .line 490
    .local v3, "resourceId":I
    new-instance v6, Ljava/lang/StringBuilder;

    #@19
    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    #@1c
    const-string v7, "android.resource://"

    #@1e
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@21
    move-result-object v6

    #@22
    iget-object v7, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mProviderContext:Landroid/content/Context;

    #@24
    invoke-virtual {v7}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    #@27
    move-result-object v7

    #@28
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2b
    move-result-object v6

    #@2c
    const-string v7, "/"

    #@2e
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@31
    move-result-object v6

    #@32
    invoke-virtual {v6, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@35
    move-result-object v6

    #@36
    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@39
    move-result-object v1

    #@3a
    .line 493
    .local v1, "drawableUri":Ljava/lang/String;
    invoke-direct {p0, v1}, Landroid/support/v7/widget/SuggestionsAdapter;->checkIconCache(Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    #@3d
    move-result-object v0

    #@3e
    .line 494
    .local v0, "drawable":Landroid/graphics/drawable/Drawable;
    if-nez v0, :cond_1

    #@40
    .line 498
    iget-object v6, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mProviderContext:Landroid/content/Context;

    #@42
    invoke-static {v6, v3}, Landroid/support/v4/content/ContextCompat;->getDrawable(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;

    #@45
    move-result-object v0

    #@46
    .line 500
    invoke-direct {p0, v1, v0}, Landroid/support/v7/widget/SuggestionsAdapter;->storeInIconCache(Ljava/lang/String;Landroid/graphics/drawable/Drawable;)V
    :try_end_0
    .catch Ljava/lang/NumberFormatException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Landroid/content/res/Resources$NotFoundException; {:try_start_0 .. :try_end_0} :catch_1

    #@49
    goto :goto_0

    #@4a
    .line 502
    .end local v0    # "drawable":Landroid/graphics/drawable/Drawable;
    .end local v1    # "drawableUri":Ljava/lang/String;
    .end local v3    # "resourceId":I
    :catch_0
    move-exception v2

    #@4b
    .line 504
    .local v2, "nfe":Ljava/lang/NumberFormatException;
    invoke-direct {p0, p1}, Landroid/support/v7/widget/SuggestionsAdapter;->checkIconCache(Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    #@4e
    move-result-object v0

    #@4f
    .line 505
    .restart local v0    # "drawable":Landroid/graphics/drawable/Drawable;
    if-nez v0, :cond_1

    #@51
    .line 508
    invoke-static {p1}, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;

    #@54
    move-result-object v4

    #@55
    .line 509
    .local v4, "uri":Landroid/net/Uri;
    invoke-direct {p0, v4}, Landroid/support/v7/widget/SuggestionsAdapter;->getDrawable(Landroid/net/Uri;)Landroid/graphics/drawable/Drawable;

    #@58
    move-result-object v0

    #@59
    .line 510
    invoke-direct {p0, p1, v0}, Landroid/support/v7/widget/SuggestionsAdapter;->storeInIconCache(Ljava/lang/String;Landroid/graphics/drawable/Drawable;)V

    #@5c
    goto :goto_0

    #@5d
    .line 512
    .end local v0    # "drawable":Landroid/graphics/drawable/Drawable;
    .end local v2    # "nfe":Ljava/lang/NumberFormatException;
    .end local v4    # "uri":Landroid/net/Uri;
    :catch_1
    move-exception v2

    #@5e
    .line 514
    .local v2, "nfe":Landroid/content/res/Resources$NotFoundException;
    const-string v6, "SuggestionsAdapter"

    #@60
    new-instance v7, Ljava/lang/StringBuilder;

    #@62
    invoke-direct {v7}, Ljava/lang/StringBuilder;-><init>()V

    #@65
    const-string v8, "Icon resource not found: "

    #@67
    invoke-virtual {v7, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@6a
    move-result-object v7

    #@6b
    invoke-virtual {v7, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@6e
    move-result-object v7

    #@6f
    invoke-virtual {v7}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@72
    move-result-object v7

    #@73
    invoke-static {v6, v7}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@76
    move-object v0, v5

    #@77
    .line 515
    goto :goto_0
.end method

.method private getIcon1(Landroid/database/Cursor;)Landroid/graphics/drawable/Drawable;
    .locals 4
    .param p1, "cursor"    # Landroid/database/Cursor;

    #@0
    .prologue
    .line 361
    iget v2, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mIconName1Col:I

    #@2
    const/4 v3, -0x1

    #@3
    if-ne v2, v3, :cond_1

    #@5
    .line 362
    const/4 v0, 0x0

    #@6
    .line 369
    :cond_0
    :goto_0
    return-object v0

    #@7
    .line 364
    :cond_1
    iget v2, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mIconName1Col:I

    #@9
    invoke-interface {p1, v2}, Landroid/database/Cursor;->getString(I)Ljava/lang/String;

    #@c
    move-result-object v1

    #@d
    .line 365
    .local v1, "value":Ljava/lang/String;
    invoke-direct {p0, v1}, Landroid/support/v7/widget/SuggestionsAdapter;->getDrawableFromResourceValue(Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    #@10
    move-result-object v0

    #@11
    .line 366
    .local v0, "drawable":Landroid/graphics/drawable/Drawable;
    if-nez v0, :cond_0

    #@13
    .line 369
    invoke-direct {p0, p1}, Landroid/support/v7/widget/SuggestionsAdapter;->getDefaultIcon1(Landroid/database/Cursor;)Landroid/graphics/drawable/Drawable;

    #@16
    move-result-object v0

    #@17
    goto :goto_0
.end method

.method private getIcon2(Landroid/database/Cursor;)Landroid/graphics/drawable/Drawable;
    .locals 3
    .param p1, "cursor"    # Landroid/database/Cursor;

    #@0
    .prologue
    .line 373
    iget v1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mIconName2Col:I

    #@2
    const/4 v2, -0x1

    #@3
    if-ne v1, v2, :cond_0

    #@5
    .line 374
    const/4 v1, 0x0

    #@6
    .line 377
    :goto_0
    return-object v1

    #@7
    .line 376
    :cond_0
    iget v1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mIconName2Col:I

    #@9
    invoke-interface {p1, v1}, Landroid/database/Cursor;->getString(I)Ljava/lang/String;

    #@c
    move-result-object v0

    #@d
    .line 377
    .local v0, "value":Ljava/lang/String;
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SuggestionsAdapter;->getDrawableFromResourceValue(Ljava/lang/String;)Landroid/graphics/drawable/Drawable;

    #@10
    move-result-object v1

    #@11
    goto :goto_0
.end method

.method private static getStringOrNull(Landroid/database/Cursor;I)Ljava/lang/String;
    .locals 4
    .param p0, "cursor"    # Landroid/database/Cursor;
    .param p1, "col"    # I

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 657
    const/4 v2, -0x1

    #@2
    if-ne p1, v2, :cond_0

    #@4
    .line 666
    :goto_0
    return-object v1

    #@5
    .line 661
    :cond_0
    :try_start_0
    invoke-interface {p0, p1}, Landroid/database/Cursor;->getString(I)Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@8
    move-result-object v1

    #@9
    goto :goto_0

    #@a
    .line 662
    :catch_0
    move-exception v0

    #@b
    .line 663
    .local v0, "e":Ljava/lang/Exception;
    const-string v2, "SuggestionsAdapter"

    #@d
    const-string v3, "unexpected error retrieving valid column from cursor, did the remote process die?"

    #@f
    invoke-static {v2, v3, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@12
    goto :goto_0
.end method

.method private setViewDrawable(Landroid/widget/ImageView;Landroid/graphics/drawable/Drawable;I)V
    .locals 2
    .param p1, "v"    # Landroid/widget/ImageView;
    .param p2, "drawable"    # Landroid/graphics/drawable/Drawable;
    .param p3, "nullVisibility"    # I

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 387
    invoke-virtual {p1, p2}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@4
    .line 389
    if-nez p2, :cond_0

    #@6
    .line 390
    invoke-virtual {p1, p3}, Landroid/widget/ImageView;->setVisibility(I)V

    #@9
    .line 402
    :goto_0
    return-void

    #@a
    .line 392
    :cond_0
    invoke-virtual {p1, v1}, Landroid/widget/ImageView;->setVisibility(I)V

    #@d
    .line 399
    invoke-virtual {p2, v1, v1}, Landroid/graphics/drawable/Drawable;->setVisible(ZZ)Z

    #@10
    .line 400
    const/4 v0, 0x1

    #@11
    invoke-virtual {p2, v0, v1}, Landroid/graphics/drawable/Drawable;->setVisible(ZZ)Z

    #@14
    goto :goto_0
.end method

.method private setViewText(Landroid/widget/TextView;Ljava/lang/CharSequence;)V
    .locals 1
    .param p1, "v"    # Landroid/widget/TextView;
    .param p2, "text"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 351
    invoke-virtual {p1, p2}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@3
    .line 353
    invoke-static {p2}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@6
    move-result v0

    #@7
    if-eqz v0, :cond_0

    #@9
    .line 354
    const/16 v0, 0x8

    #@b
    invoke-virtual {p1, v0}, Landroid/widget/TextView;->setVisibility(I)V

    #@e
    .line 358
    :goto_0
    return-void

    #@f
    .line 356
    :cond_0
    const/4 v0, 0x0

    #@10
    invoke-virtual {p1, v0}, Landroid/widget/TextView;->setVisibility(I)V

    #@13
    goto :goto_0
.end method

.method private storeInIconCache(Ljava/lang/String;Landroid/graphics/drawable/Drawable;)V
    .locals 2
    .param p1, "resourceUri"    # Ljava/lang/String;
    .param p2, "drawable"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 568
    if-eqz p2, :cond_0

    #@2
    .line 569
    iget-object v0, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mOutsideDrawablesCache:Ljava/util/WeakHashMap;

    #@4
    invoke-virtual {p2}, Landroid/graphics/drawable/Drawable;->getConstantState()Landroid/graphics/drawable/Drawable$ConstantState;

    #@7
    move-result-object v1

    #@8
    invoke-virtual {v0, p1, v1}, Ljava/util/WeakHashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@b
    .line 571
    :cond_0
    return-void
.end method

.method private updateSpinnerState(Landroid/database/Cursor;)V
    .locals 2
    .param p1, "cursor"    # Landroid/database/Cursor;

    #@0
    .prologue
    .line 195
    if-eqz p1, :cond_1

    #@2
    invoke-interface {p1}, Landroid/database/Cursor;->getExtras()Landroid/os/Bundle;

    #@5
    move-result-object v0

    #@6
    .line 203
    .local v0, "extras":Landroid/os/Bundle;
    :goto_0
    if-eqz v0, :cond_0

    #@8
    const-string v1, "in_progress"

    #@a
    invoke-virtual {v0, v1}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;)Z

    #@d
    move-result v1

    #@e
    if-eqz v1, :cond_0

    #@10
    .line 208
    :cond_0
    return-void

    #@11
    .line 195
    .end local v0    # "extras":Landroid/os/Bundle;
    :cond_1
    const/4 v0, 0x0

    #@12
    goto :goto_0
.end method


# virtual methods
.method public bindView(Landroid/view/View;Landroid/content/Context;Landroid/database/Cursor;)V
    .locals 11
    .param p1, "view"    # Landroid/view/View;
    .param p2, "context"    # Landroid/content/Context;
    .param p3, "cursor"    # Landroid/database/Cursor;

    #@0
    .prologue
    const/16 v10, 0x8

    #@2
    const/4 v9, 0x2

    #@3
    const/4 v8, 0x0

    #@4
    const/4 v7, 0x1

    #@5
    .line 275
    invoke-virtual {p1}, Landroid/view/View;->getTag()Ljava/lang/Object;

    #@8
    move-result-object v3

    #@9
    check-cast v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;

    #@b
    .line 277
    .local v3, "views":Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;
    const/4 v0, 0x0

    #@c
    .line 278
    .local v0, "flags":I
    iget v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mFlagsCol:I

    #@e
    const/4 v5, -0x1

    #@f
    if-eq v4, v5, :cond_0

    #@11
    .line 279
    iget v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mFlagsCol:I

    #@13
    invoke-interface {p3, v4}, Landroid/database/Cursor;->getInt(I)I

    #@16
    move-result v0

    #@17
    .line 281
    :cond_0
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mText1:Landroid/widget/TextView;

    #@19
    if-eqz v4, :cond_1

    #@1b
    .line 282
    iget v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mText1Col:I

    #@1d
    invoke-static {p3, v4}, Landroid/support/v7/widget/SuggestionsAdapter;->getStringOrNull(Landroid/database/Cursor;I)Ljava/lang/String;

    #@20
    move-result-object v1

    #@21
    .line 283
    .local v1, "text1":Ljava/lang/String;
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mText1:Landroid/widget/TextView;

    #@23
    invoke-direct {p0, v4, v1}, Landroid/support/v7/widget/SuggestionsAdapter;->setViewText(Landroid/widget/TextView;Ljava/lang/CharSequence;)V

    #@26
    .line 285
    .end local v1    # "text1":Ljava/lang/String;
    :cond_1
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mText2:Landroid/widget/TextView;

    #@28
    if-eqz v4, :cond_3

    #@2a
    .line 287
    iget v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mText2UrlCol:I

    #@2c
    invoke-static {p3, v4}, Landroid/support/v7/widget/SuggestionsAdapter;->getStringOrNull(Landroid/database/Cursor;I)Ljava/lang/String;

    #@2f
    move-result-object v2

    #@30
    .line 288
    .local v2, "text2":Ljava/lang/CharSequence;
    if-eqz v2, :cond_7

    #@32
    .line 289
    invoke-direct {p0, v2}, Landroid/support/v7/widget/SuggestionsAdapter;->formatUrl(Ljava/lang/CharSequence;)Ljava/lang/CharSequence;

    #@35
    move-result-object v2

    #@36
    .line 296
    :goto_0
    invoke-static {v2}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@39
    move-result v4

    #@3a
    if-eqz v4, :cond_8

    #@3c
    .line 297
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mText1:Landroid/widget/TextView;

    #@3e
    if-eqz v4, :cond_2

    #@40
    .line 298
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mText1:Landroid/widget/TextView;

    #@42
    invoke-virtual {v4, v8}, Landroid/widget/TextView;->setSingleLine(Z)V

    #@45
    .line 299
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mText1:Landroid/widget/TextView;

    #@47
    invoke-virtual {v4, v9}, Landroid/widget/TextView;->setMaxLines(I)V

    #@4a
    .line 307
    :cond_2
    :goto_1
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mText2:Landroid/widget/TextView;

    #@4c
    invoke-direct {p0, v4, v2}, Landroid/support/v7/widget/SuggestionsAdapter;->setViewText(Landroid/widget/TextView;Ljava/lang/CharSequence;)V

    #@4f
    .line 310
    .end local v2    # "text2":Ljava/lang/CharSequence;
    :cond_3
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mIcon1:Landroid/widget/ImageView;

    #@51
    if-eqz v4, :cond_4

    #@53
    .line 311
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mIcon1:Landroid/widget/ImageView;

    #@55
    invoke-direct {p0, p3}, Landroid/support/v7/widget/SuggestionsAdapter;->getIcon1(Landroid/database/Cursor;)Landroid/graphics/drawable/Drawable;

    #@58
    move-result-object v5

    #@59
    const/4 v6, 0x4

    #@5a
    invoke-direct {p0, v4, v5, v6}, Landroid/support/v7/widget/SuggestionsAdapter;->setViewDrawable(Landroid/widget/ImageView;Landroid/graphics/drawable/Drawable;I)V

    #@5d
    .line 313
    :cond_4
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mIcon2:Landroid/widget/ImageView;

    #@5f
    if-eqz v4, :cond_5

    #@61
    .line 314
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mIcon2:Landroid/widget/ImageView;

    #@63
    invoke-direct {p0, p3}, Landroid/support/v7/widget/SuggestionsAdapter;->getIcon2(Landroid/database/Cursor;)Landroid/graphics/drawable/Drawable;

    #@66
    move-result-object v5

    #@67
    invoke-direct {p0, v4, v5, v10}, Landroid/support/v7/widget/SuggestionsAdapter;->setViewDrawable(Landroid/widget/ImageView;Landroid/graphics/drawable/Drawable;I)V

    #@6a
    .line 316
    :cond_5
    iget v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mQueryRefinement:I

    #@6c
    if-eq v4, v9, :cond_6

    #@6e
    iget v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mQueryRefinement:I

    #@70
    if-ne v4, v7, :cond_9

    #@72
    and-int/lit8 v4, v0, 0x1

    #@74
    if-eqz v4, :cond_9

    #@76
    .line 319
    :cond_6
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mIconRefine:Landroid/widget/ImageView;

    #@78
    invoke-virtual {v4, v8}, Landroid/widget/ImageView;->setVisibility(I)V

    #@7b
    .line 320
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mIconRefine:Landroid/widget/ImageView;

    #@7d
    iget-object v5, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mText1:Landroid/widget/TextView;

    #@7f
    invoke-virtual {v5}, Landroid/widget/TextView;->getText()Ljava/lang/CharSequence;

    #@82
    move-result-object v5

    #@83
    invoke-virtual {v4, v5}, Landroid/widget/ImageView;->setTag(Ljava/lang/Object;)V

    #@86
    .line 321
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mIconRefine:Landroid/widget/ImageView;

    #@88
    invoke-virtual {v4, p0}, Landroid/widget/ImageView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    #@8b
    .line 325
    :goto_2
    return-void

    #@8c
    .line 291
    .restart local v2    # "text2":Ljava/lang/CharSequence;
    :cond_7
    iget v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mText2Col:I

    #@8e
    invoke-static {p3, v4}, Landroid/support/v7/widget/SuggestionsAdapter;->getStringOrNull(Landroid/database/Cursor;I)Ljava/lang/String;

    #@91
    move-result-object v2

    #@92
    goto :goto_0

    #@93
    .line 302
    :cond_8
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mText1:Landroid/widget/TextView;

    #@95
    if-eqz v4, :cond_2

    #@97
    .line 303
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mText1:Landroid/widget/TextView;

    #@99
    invoke-virtual {v4, v7}, Landroid/widget/TextView;->setSingleLine(Z)V

    #@9c
    .line 304
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mText1:Landroid/widget/TextView;

    #@9e
    invoke-virtual {v4, v7}, Landroid/widget/TextView;->setMaxLines(I)V

    #@a1
    goto :goto_1

    #@a2
    .line 323
    .end local v2    # "text2":Ljava/lang/CharSequence;
    :cond_9
    iget-object v4, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mIconRefine:Landroid/widget/ImageView;

    #@a4
    invoke-virtual {v4, v10}, Landroid/widget/ImageView;->setVisibility(I)V

    #@a7
    goto :goto_2
.end method

.method public changeCursor(Landroid/database/Cursor;)V
    .locals 3
    .param p1, "c"    # Landroid/database/Cursor;

    #@0
    .prologue
    .line 217
    iget-boolean v1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mClosed:Z

    #@2
    if-eqz v1, :cond_1

    #@4
    .line 218
    const-string v1, "SuggestionsAdapter"

    #@6
    const-string v2, "Tried to change cursor after adapter was closed."

    #@8
    invoke-static {v1, v2}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@b
    .line 219
    if-eqz p1, :cond_0

    #@d
    invoke-interface {p1}, Landroid/database/Cursor;->close()V

    #@10
    .line 237
    :cond_0
    :goto_0
    return-void

    #@11
    .line 224
    :cond_1
    :try_start_0
    invoke-super {p0, p1}, Landroid/support/v4/widget/ResourceCursorAdapter;->changeCursor(Landroid/database/Cursor;)V

    #@14
    .line 226
    if-eqz p1, :cond_0

    #@16
    .line 227
    const-string v1, "suggest_text_1"

    #@18
    invoke-interface {p1, v1}, Landroid/database/Cursor;->getColumnIndex(Ljava/lang/String;)I

    #@1b
    move-result v1

    #@1c
    iput v1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mText1Col:I

    #@1e
    .line 228
    const-string v1, "suggest_text_2"

    #@20
    invoke-interface {p1, v1}, Landroid/database/Cursor;->getColumnIndex(Ljava/lang/String;)I

    #@23
    move-result v1

    #@24
    iput v1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mText2Col:I

    #@26
    .line 229
    const-string v1, "suggest_text_2_url"

    #@28
    invoke-interface {p1, v1}, Landroid/database/Cursor;->getColumnIndex(Ljava/lang/String;)I

    #@2b
    move-result v1

    #@2c
    iput v1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mText2UrlCol:I

    #@2e
    .line 230
    const-string v1, "suggest_icon_1"

    #@30
    invoke-interface {p1, v1}, Landroid/database/Cursor;->getColumnIndex(Ljava/lang/String;)I

    #@33
    move-result v1

    #@34
    iput v1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mIconName1Col:I

    #@36
    .line 231
    const-string v1, "suggest_icon_2"

    #@38
    invoke-interface {p1, v1}, Landroid/database/Cursor;->getColumnIndex(Ljava/lang/String;)I

    #@3b
    move-result v1

    #@3c
    iput v1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mIconName2Col:I

    #@3e
    .line 232
    const-string v1, "suggest_flags"

    #@40
    invoke-interface {p1, v1}, Landroid/database/Cursor;->getColumnIndex(Ljava/lang/String;)I

    #@43
    move-result v1

    #@44
    iput v1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mFlagsCol:I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@46
    goto :goto_0

    #@47
    .line 234
    :catch_0
    move-exception v0

    #@48
    .line 235
    .local v0, "e":Ljava/lang/Exception;
    const-string v1, "SuggestionsAdapter"

    #@4a
    const-string v2, "error changing cursor and caching columns"

    #@4c
    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@4f
    goto :goto_0
.end method

.method public close()V
    .locals 1

    #@0
    .prologue
    .line 174
    const/4 v0, 0x0

    #@1
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/SuggestionsAdapter;->changeCursor(Landroid/database/Cursor;)V

    #@4
    .line 175
    const/4 v0, 0x1

    #@5
    iput-boolean v0, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mClosed:Z

    #@7
    .line 176
    return-void
.end method

.method public convertToString(Landroid/database/Cursor;)Ljava/lang/CharSequence;
    .locals 5
    .param p1, "cursor"    # Landroid/database/Cursor;

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 414
    if-nez p1, :cond_1

    #@3
    move-object v1, v3

    #@4
    .line 437
    :cond_0
    :goto_0
    return-object v1

    #@5
    .line 418
    :cond_1
    const-string v4, "suggest_intent_query"

    #@7
    invoke-static {p1, v4}, Landroid/support/v7/widget/SuggestionsAdapter;->getColumnString(Landroid/database/Cursor;Ljava/lang/String;)Ljava/lang/String;

    #@a
    move-result-object v1

    #@b
    .line 419
    .local v1, "query":Ljava/lang/String;
    if-nez v1, :cond_0

    #@d
    .line 423
    iget-object v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mSearchable:Landroid/app/SearchableInfo;

    #@f
    invoke-virtual {v4}, Landroid/app/SearchableInfo;->shouldRewriteQueryFromData()Z

    #@12
    move-result v4

    #@13
    if-eqz v4, :cond_2

    #@15
    .line 424
    const-string v4, "suggest_intent_data"

    #@17
    invoke-static {p1, v4}, Landroid/support/v7/widget/SuggestionsAdapter;->getColumnString(Landroid/database/Cursor;Ljava/lang/String;)Ljava/lang/String;

    #@1a
    move-result-object v0

    #@1b
    .line 425
    .local v0, "data":Ljava/lang/String;
    if-eqz v0, :cond_2

    #@1d
    move-object v1, v0

    #@1e
    .line 426
    goto :goto_0

    #@1f
    .line 430
    .end local v0    # "data":Ljava/lang/String;
    :cond_2
    iget-object v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mSearchable:Landroid/app/SearchableInfo;

    #@21
    invoke-virtual {v4}, Landroid/app/SearchableInfo;->shouldRewriteQueryFromText()Z

    #@24
    move-result v4

    #@25
    if-eqz v4, :cond_3

    #@27
    .line 431
    const-string v4, "suggest_text_1"

    #@29
    invoke-static {p1, v4}, Landroid/support/v7/widget/SuggestionsAdapter;->getColumnString(Landroid/database/Cursor;Ljava/lang/String;)Ljava/lang/String;

    #@2c
    move-result-object v2

    #@2d
    .line 432
    .local v2, "text1":Ljava/lang/String;
    if-eqz v2, :cond_3

    #@2f
    move-object v1, v2

    #@30
    .line 433
    goto :goto_0

    #@31
    .end local v2    # "text1":Ljava/lang/String;
    :cond_3
    move-object v1, v3

    #@32
    .line 437
    goto :goto_0
.end method

.method getDrawableFromResourceUri(Landroid/net/Uri;)Landroid/graphics/drawable/Drawable;
    .locals 10
    .param p1, "uri"    # Landroid/net/Uri;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/FileNotFoundException;
        }
    .end annotation

    #@0
    .prologue
    const/4 v9, 0x1

    #@1
    const/4 v8, 0x0

    #@2
    .line 675
    invoke-virtual {p1}, Landroid/net/Uri;->getAuthority()Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    .line 677
    .local v0, "authority":Ljava/lang/String;
    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@9
    move-result v7

    #@a
    if-eqz v7, :cond_0

    #@c
    .line 678
    new-instance v7, Ljava/io/FileNotFoundException;

    #@e
    new-instance v8, Ljava/lang/StringBuilder;

    #@10
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@13
    const-string v9, "No authority: "

    #@15
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@18
    move-result-object v8

    #@19
    invoke-virtual {v8, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v8

    #@1d
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@20
    move-result-object v8

    #@21
    invoke-direct {v7, v8}, Ljava/io/FileNotFoundException;-><init>(Ljava/lang/String;)V

    #@24
    throw v7

    #@25
    .line 681
    :cond_0
    :try_start_0
    iget-object v7, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mContext:Landroid/content/Context;

    #@27
    invoke-virtual {v7}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    #@2a
    move-result-object v7

    #@2b
    invoke-virtual {v7, v0}, Landroid/content/pm/PackageManager;->getResourcesForApplication(Ljava/lang/String;)Landroid/content/res/Resources;
    :try_end_0
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    #@2e
    move-result-object v6

    #@2f
    .line 686
    .local v6, "r":Landroid/content/res/Resources;
    invoke-virtual {p1}, Landroid/net/Uri;->getPathSegments()Ljava/util/List;

    #@32
    move-result-object v5

    #@33
    .line 687
    .local v5, "path":Ljava/util/List;, "Ljava/util/List<Ljava/lang/String;>;"
    if-nez v5, :cond_1

    #@35
    .line 688
    new-instance v7, Ljava/io/FileNotFoundException;

    #@37
    new-instance v8, Ljava/lang/StringBuilder;

    #@39
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@3c
    const-string v9, "No path: "

    #@3e
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@41
    move-result-object v8

    #@42
    invoke-virtual {v8, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@45
    move-result-object v8

    #@46
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@49
    move-result-object v8

    #@4a
    invoke-direct {v7, v8}, Ljava/io/FileNotFoundException;-><init>(Ljava/lang/String;)V

    #@4d
    throw v7

    #@4e
    .line 682
    .end local v5    # "path":Ljava/util/List;, "Ljava/util/List<Ljava/lang/String;>;"
    .end local v6    # "r":Landroid/content/res/Resources;
    :catch_0
    move-exception v2

    #@4f
    .line 683
    .local v2, "ex":Landroid/content/pm/PackageManager$NameNotFoundException;
    new-instance v7, Ljava/io/FileNotFoundException;

    #@51
    new-instance v8, Ljava/lang/StringBuilder;

    #@53
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@56
    const-string v9, "No package found for authority: "

    #@58
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@5b
    move-result-object v8

    #@5c
    invoke-virtual {v8, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@5f
    move-result-object v8

    #@60
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@63
    move-result-object v8

    #@64
    invoke-direct {v7, v8}, Ljava/io/FileNotFoundException;-><init>(Ljava/lang/String;)V

    #@67
    throw v7

    #@68
    .line 690
    .end local v2    # "ex":Landroid/content/pm/PackageManager$NameNotFoundException;
    .restart local v5    # "path":Ljava/util/List;, "Ljava/util/List<Ljava/lang/String;>;"
    .restart local v6    # "r":Landroid/content/res/Resources;
    :cond_1
    invoke-interface {v5}, Ljava/util/List;->size()I

    #@6b
    move-result v4

    #@6c
    .line 692
    .local v4, "len":I
    if-ne v4, v9, :cond_2

    #@6e
    .line 694
    const/4 v7, 0x0

    #@6f
    :try_start_1
    invoke-interface {v5, v7}, Ljava/util/List;->get(I)Ljava/lang/Object;

    #@72
    move-result-object v7

    #@73
    check-cast v7, Ljava/lang/String;

    #@75
    invoke-static {v7}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I
    :try_end_1
    .catch Ljava/lang/NumberFormatException; {:try_start_1 .. :try_end_1} :catch_1

    #@78
    move-result v3

    #@79
    .line 703
    .local v3, "id":I
    :goto_0
    if-nez v3, :cond_4

    #@7b
    .line 704
    new-instance v7, Ljava/io/FileNotFoundException;

    #@7d
    new-instance v8, Ljava/lang/StringBuilder;

    #@7f
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@82
    const-string v9, "No resource found for: "

    #@84
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@87
    move-result-object v8

    #@88
    invoke-virtual {v8, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@8b
    move-result-object v8

    #@8c
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@8f
    move-result-object v8

    #@90
    invoke-direct {v7, v8}, Ljava/io/FileNotFoundException;-><init>(Ljava/lang/String;)V

    #@93
    throw v7

    #@94
    .line 695
    .end local v3    # "id":I
    :catch_1
    move-exception v1

    #@95
    .line 696
    .local v1, "e":Ljava/lang/NumberFormatException;
    new-instance v7, Ljava/io/FileNotFoundException;

    #@97
    new-instance v8, Ljava/lang/StringBuilder;

    #@99
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@9c
    const-string v9, "Single path segment is not a resource ID: "

    #@9e
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@a1
    move-result-object v8

    #@a2
    invoke-virtual {v8, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@a5
    move-result-object v8

    #@a6
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@a9
    move-result-object v8

    #@aa
    invoke-direct {v7, v8}, Ljava/io/FileNotFoundException;-><init>(Ljava/lang/String;)V

    #@ad
    throw v7

    #@ae
    .line 698
    .end local v1    # "e":Ljava/lang/NumberFormatException;
    :cond_2
    const/4 v7, 0x2

    #@af
    if-ne v4, v7, :cond_3

    #@b1
    .line 699
    invoke-interface {v5, v9}, Ljava/util/List;->get(I)Ljava/lang/Object;

    #@b4
    move-result-object v7

    #@b5
    check-cast v7, Ljava/lang/String;

    #@b7
    invoke-interface {v5, v8}, Ljava/util/List;->get(I)Ljava/lang/Object;

    #@ba
    move-result-object v8

    #@bb
    check-cast v8, Ljava/lang/String;

    #@bd
    invoke-virtual {v6, v7, v8, v0}, Landroid/content/res/Resources;->getIdentifier(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I

    #@c0
    move-result v3

    #@c1
    .restart local v3    # "id":I
    goto :goto_0

    #@c2
    .line 701
    .end local v3    # "id":I
    :cond_3
    new-instance v7, Ljava/io/FileNotFoundException;

    #@c4
    new-instance v8, Ljava/lang/StringBuilder;

    #@c6
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@c9
    const-string v9, "More than two path segments: "

    #@cb
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@ce
    move-result-object v8

    #@cf
    invoke-virtual {v8, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@d2
    move-result-object v8

    #@d3
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@d6
    move-result-object v8

    #@d7
    invoke-direct {v7, v8}, Ljava/io/FileNotFoundException;-><init>(Ljava/lang/String;)V

    #@da
    throw v7

    #@db
    .line 706
    .restart local v3    # "id":I
    :cond_4
    invoke-virtual {v6, v3}, Landroid/content/res/Resources;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@de
    move-result-object v7

    #@df
    return-object v7
.end method

.method public getQueryRefinement()I
    .locals 1

    #@0
    .prologue
    .line 126
    iget v0, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mQueryRefinement:I

    #@2
    return v0
.end method

.method getSearchManagerSuggestions(Landroid/app/SearchableInfo;Ljava/lang/String;I)Landroid/database/Cursor;
    .locals 9
    .param p1, "searchable"    # Landroid/app/SearchableInfo;
    .param p2, "query"    # Ljava/lang/String;
    .param p3, "limit"    # I

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 713
    if-nez p1, :cond_1

    #@3
    .line 754
    :cond_0
    :goto_0
    return-object v2

    #@4
    .line 717
    :cond_1
    invoke-virtual {p1}, Landroid/app/SearchableInfo;->getSuggestAuthority()Ljava/lang/String;

    #@7
    move-result-object v6

    #@8
    .line 718
    .local v6, "authority":Ljava/lang/String;
    if-eqz v6, :cond_0

    #@a
    .line 722
    new-instance v0, Landroid/net/Uri$Builder;

    #@c
    invoke-direct {v0}, Landroid/net/Uri$Builder;-><init>()V

    #@f
    const-string v5, "content"

    #@11
    invoke-virtual {v0, v5}, Landroid/net/Uri$Builder;->scheme(Ljava/lang/String;)Landroid/net/Uri$Builder;

    #@14
    move-result-object v0

    #@15
    invoke-virtual {v0, v6}, Landroid/net/Uri$Builder;->authority(Ljava/lang/String;)Landroid/net/Uri$Builder;

    #@18
    move-result-object v0

    #@19
    const-string v5, ""

    #@1b
    invoke-virtual {v0, v5}, Landroid/net/Uri$Builder;->query(Ljava/lang/String;)Landroid/net/Uri$Builder;

    #@1e
    move-result-object v0

    #@1f
    const-string v5, ""

    #@21
    invoke-virtual {v0, v5}, Landroid/net/Uri$Builder;->fragment(Ljava/lang/String;)Landroid/net/Uri$Builder;

    #@24
    move-result-object v8

    #@25
    .line 729
    .local v8, "uriBuilder":Landroid/net/Uri$Builder;
    invoke-virtual {p1}, Landroid/app/SearchableInfo;->getSuggestPath()Ljava/lang/String;

    #@28
    move-result-object v7

    #@29
    .line 730
    .local v7, "contentPath":Ljava/lang/String;
    if-eqz v7, :cond_2

    #@2b
    .line 731
    invoke-virtual {v8, v7}, Landroid/net/Uri$Builder;->appendEncodedPath(Ljava/lang/String;)Landroid/net/Uri$Builder;

    #@2e
    .line 735
    :cond_2
    const-string v0, "search_suggest_query"

    #@30
    invoke-virtual {v8, v0}, Landroid/net/Uri$Builder;->appendPath(Ljava/lang/String;)Landroid/net/Uri$Builder;

    #@33
    .line 738
    invoke-virtual {p1}, Landroid/app/SearchableInfo;->getSuggestSelection()Ljava/lang/String;

    #@36
    move-result-object v3

    #@37
    .line 740
    .local v3, "selection":Ljava/lang/String;
    const/4 v4, 0x0

    #@38
    .line 741
    .local v4, "selArgs":[Ljava/lang/String;
    if-eqz v3, :cond_4

    #@3a
    .line 742
    const/4 v0, 0x1

    #@3b
    new-array v4, v0, [Ljava/lang/String;

    #@3d
    .end local v4    # "selArgs":[Ljava/lang/String;
    const/4 v0, 0x0

    #@3e
    aput-object p2, v4, v0

    #@40
    .line 747
    .restart local v4    # "selArgs":[Ljava/lang/String;
    :goto_1
    if-lez p3, :cond_3

    #@42
    .line 748
    const-string v0, "limit"

    #@44
    invoke-static {p3}, Ljava/lang/String;->valueOf(I)Ljava/lang/String;

    #@47
    move-result-object v5

    #@48
    invoke-virtual {v8, v0, v5}, Landroid/net/Uri$Builder;->appendQueryParameter(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri$Builder;

    #@4b
    .line 751
    :cond_3
    invoke-virtual {v8}, Landroid/net/Uri$Builder;->build()Landroid/net/Uri;

    #@4e
    move-result-object v1

    #@4f
    .line 754
    .local v1, "uri":Landroid/net/Uri;
    iget-object v0, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mContext:Landroid/content/Context;

    #@51
    invoke-virtual {v0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    #@54
    move-result-object v0

    #@55
    move-object v5, v2

    #@56
    invoke-virtual/range {v0 .. v5}, Landroid/content/ContentResolver;->query(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;

    #@59
    move-result-object v2

    #@5a
    goto :goto_0

    #@5b
    .line 744
    .end local v1    # "uri":Landroid/net/Uri;
    :cond_4
    invoke-virtual {v8, p2}, Landroid/net/Uri$Builder;->appendPath(Ljava/lang/String;)Landroid/net/Uri$Builder;

    #@5e
    goto :goto_1
.end method

.method public getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 6
    .param p1, "position"    # I
    .param p2, "convertView"    # Landroid/view/View;
    .param p3, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 449
    :try_start_0
    invoke-super {p0, p1, p2, p3}, Landroid/support/v4/widget/ResourceCursorAdapter;->getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;
    :try_end_0
    .catch Ljava/lang/RuntimeException; {:try_start_0 .. :try_end_0} :catch_0

    #@3
    move-result-object v2

    #@4
    .line 459
    :cond_0
    :goto_0
    return-object v2

    #@5
    .line 450
    :catch_0
    move-exception v0

    #@6
    .line 451
    .local v0, "e":Ljava/lang/RuntimeException;
    const-string v4, "SuggestionsAdapter"

    #@8
    const-string v5, "Search suggestions cursor threw exception."

    #@a
    invoke-static {v4, v5, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@d
    .line 453
    iget-object v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mContext:Landroid/content/Context;

    #@f
    iget-object v5, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mCursor:Landroid/database/Cursor;

    #@11
    invoke-virtual {p0, v4, v5, p3}, Landroid/support/v7/widget/SuggestionsAdapter;->newView(Landroid/content/Context;Landroid/database/Cursor;Landroid/view/ViewGroup;)Landroid/view/View;

    #@14
    move-result-object v2

    #@15
    .line 454
    .local v2, "v":Landroid/view/View;
    if-eqz v2, :cond_0

    #@17
    .line 455
    invoke-virtual {v2}, Landroid/view/View;->getTag()Ljava/lang/Object;

    #@1a
    move-result-object v3

    #@1b
    check-cast v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;

    #@1d
    .line 456
    .local v3, "views":Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;
    iget-object v1, v3, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;->mText1:Landroid/widget/TextView;

    #@1f
    .line 457
    .local v1, "tv":Landroid/widget/TextView;
    invoke-virtual {v0}, Ljava/lang/RuntimeException;->toString()Ljava/lang/String;

    #@22
    move-result-object v4

    #@23
    invoke-virtual {v1, v4}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@26
    goto :goto_0
.end method

.method public hasStableIds()Z
    .locals 1

    #@0
    .prologue
    .line 135
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public newView(Landroid/content/Context;Landroid/database/Cursor;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "cursor"    # Landroid/database/Cursor;
    .param p3, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 244
    invoke-super {p0, p1, p2, p3}, Landroid/support/v4/widget/ResourceCursorAdapter;->newView(Landroid/content/Context;Landroid/database/Cursor;Landroid/view/ViewGroup;)Landroid/view/View;

    #@3
    move-result-object v1

    #@4
    .line 245
    .local v1, "v":Landroid/view/View;
    new-instance v2, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;

    #@6
    invoke-direct {v2, v1}, Landroid/support/v7/widget/SuggestionsAdapter$ChildViewCache;-><init>(Landroid/view/View;)V

    #@9
    invoke-virtual {v1, v2}, Landroid/view/View;->setTag(Ljava/lang/Object;)V

    #@c
    .line 248
    sget v2, Landroid/support/v7/appcompat/R$id;->edit_query:I

    #@e
    invoke-virtual {v1, v2}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@11
    move-result-object v0

    #@12
    check-cast v0, Landroid/widget/ImageView;

    #@14
    .line 249
    .local v0, "iconRefine":Landroid/widget/ImageView;
    iget v2, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mCommitIconResId:I

    #@16
    invoke-virtual {v0, v2}, Landroid/widget/ImageView;->setImageResource(I)V

    #@19
    .line 250
    return-object v1
.end method

.method public notifyDataSetChanged()V
    .locals 1

    #@0
    .prologue
    .line 181
    invoke-super {p0}, Landroid/support/v4/widget/ResourceCursorAdapter;->notifyDataSetChanged()V

    #@3
    .line 183
    invoke-virtual {p0}, Landroid/support/v7/widget/SuggestionsAdapter;->getCursor()Landroid/database/Cursor;

    #@6
    move-result-object v0

    #@7
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SuggestionsAdapter;->updateSpinnerState(Landroid/database/Cursor;)V

    #@a
    .line 184
    return-void
.end method

.method public notifyDataSetInvalidated()V
    .locals 1

    #@0
    .prologue
    .line 189
    invoke-super {p0}, Landroid/support/v4/widget/ResourceCursorAdapter;->notifyDataSetInvalidated()V

    #@3
    .line 191
    invoke-virtual {p0}, Landroid/support/v7/widget/SuggestionsAdapter;->getCursor()Landroid/database/Cursor;

    #@6
    move-result-object v0

    #@7
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SuggestionsAdapter;->updateSpinnerState(Landroid/database/Cursor;)V

    #@a
    .line 192
    return-void
.end method

.method public onClick(Landroid/view/View;)V
    .locals 2
    .param p1, "v"    # Landroid/view/View;

    #@0
    .prologue
    .line 328
    invoke-virtual {p1}, Landroid/view/View;->getTag()Ljava/lang/Object;

    #@3
    move-result-object v0

    #@4
    .line 329
    .local v0, "tag":Ljava/lang/Object;
    instance-of v1, v0, Ljava/lang/CharSequence;

    #@6
    if-eqz v1, :cond_0

    #@8
    .line 330
    iget-object v1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mSearchView:Landroid/support/v7/widget/SearchView;

    #@a
    check-cast v0, Ljava/lang/CharSequence;

    #@c
    .end local v0    # "tag":Ljava/lang/Object;
    invoke-virtual {v1, v0}, Landroid/support/v7/widget/SearchView;->onQueryRefine(Ljava/lang/CharSequence;)V

    #@f
    .line 332
    :cond_0
    return-void
.end method

.method public runQueryOnBackgroundThread(Ljava/lang/CharSequence;)Landroid/database/Cursor;
    .locals 6
    .param p1, "constraint"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 146
    if-nez p1, :cond_1

    #@3
    const-string v2, ""

    #@5
    .line 151
    .local v2, "query":Ljava/lang/String;
    :goto_0
    const/4 v0, 0x0

    #@6
    .line 152
    .local v0, "cursor":Landroid/database/Cursor;
    iget-object v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mSearchView:Landroid/support/v7/widget/SearchView;

    #@8
    invoke-virtual {v4}, Landroid/support/v7/widget/SearchView;->getVisibility()I

    #@b
    move-result v4

    #@c
    if-nez v4, :cond_0

    #@e
    iget-object v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mSearchView:Landroid/support/v7/widget/SearchView;

    #@10
    invoke-virtual {v4}, Landroid/support/v7/widget/SearchView;->getWindowVisibility()I

    #@13
    move-result v4

    #@14
    if-eqz v4, :cond_2

    #@16
    .line 169
    :cond_0
    :goto_1
    return-object v3

    #@17
    .line 146
    .end local v0    # "cursor":Landroid/database/Cursor;
    .end local v2    # "query":Ljava/lang/String;
    :cond_1
    invoke-interface {p1}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    #@1a
    move-result-object v2

    #@1b
    goto :goto_0

    #@1c
    .line 157
    .restart local v0    # "cursor":Landroid/database/Cursor;
    .restart local v2    # "query":Ljava/lang/String;
    :cond_2
    :try_start_0
    iget-object v4, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mSearchable:Landroid/app/SearchableInfo;

    #@1e
    const/16 v5, 0x32

    #@20
    invoke-virtual {p0, v4, v2, v5}, Landroid/support/v7/widget/SuggestionsAdapter;->getSearchManagerSuggestions(Landroid/app/SearchableInfo;Ljava/lang/String;I)Landroid/database/Cursor;

    #@23
    move-result-object v0

    #@24
    .line 160
    if-eqz v0, :cond_0

    #@26
    .line 161
    invoke-interface {v0}, Landroid/database/Cursor;->getCount()I
    :try_end_0
    .catch Ljava/lang/RuntimeException; {:try_start_0 .. :try_end_0} :catch_0

    #@29
    move-object v3, v0

    #@2a
    .line 162
    goto :goto_1

    #@2b
    .line 164
    :catch_0
    move-exception v1

    #@2c
    .line 165
    .local v1, "e":Ljava/lang/RuntimeException;
    const-string v4, "SuggestionsAdapter"

    #@2e
    const-string v5, "Search suggestions query threw an exception."

    #@30
    invoke-static {v4, v5, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@33
    goto :goto_1
.end method

.method public setQueryRefinement(I)V
    .locals 0
    .param p1, "refineWhat"    # I

    #@0
    .prologue
    .line 118
    iput p1, p0, Landroid/support/v7/widget/SuggestionsAdapter;->mQueryRefinement:I

    #@2
    .line 119
    return-void
.end method
