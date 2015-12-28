.class Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb;
.super Ljava/lang/Object;
.source "ActionBarDrawerToggleHoneycomb.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;
    }
.end annotation


# static fields
.field private static final TAG:Ljava/lang/String; = "ActionBarDrawerToggleHoneycomb"

.field private static final THEME_ATTRS:[I


# direct methods
.method static constructor <clinit>()V
    .locals 3

    #@0
    .prologue
    .line 44
    const/4 v0, 0x1

    #@1
    new-array v0, v0, [I

    #@3
    const/4 v1, 0x0

    #@4
    const v2, 0x101030b

    #@7
    aput v2, v0, v1

    #@9
    sput-object v0, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb;->THEME_ATTRS:[I

    #@b
    return-void
.end method

.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 41
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 99
    return-void
.end method

.method public static getThemeUpIndicator(Landroid/app/Activity;)Landroid/graphics/drawable/Drawable;
    .locals 3
    .param p0, "activity"    # Landroid/app/Activity;

    #@0
    .prologue
    .line 93
    sget-object v2, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb;->THEME_ATTRS:[I

    #@2
    invoke-virtual {p0, v2}, Landroid/app/Activity;->obtainStyledAttributes([I)Landroid/content/res/TypedArray;

    #@5
    move-result-object v0

    #@6
    .line 94
    .local v0, "a":Landroid/content/res/TypedArray;
    const/4 v2, 0x0

    #@7
    invoke-virtual {v0, v2}, Landroid/content/res/TypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@a
    move-result-object v1

    #@b
    .line 95
    .local v1, "result":Landroid/graphics/drawable/Drawable;
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@e
    .line 96
    return-object v1
.end method

.method public static setActionBarDescription(Ljava/lang/Object;Landroid/app/Activity;I)Ljava/lang/Object;
    .locals 7
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "contentDescRes"    # I

    #@0
    .prologue
    .line 72
    if-nez p0, :cond_0

    #@2
    .line 73
    new-instance p0, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;

    #@4
    .end local p0    # "info":Ljava/lang/Object;
    invoke-direct {p0, p1}, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;-><init>(Landroid/app/Activity;)V

    #@7
    :cond_0
    move-object v2, p0

    #@8
    .line 75
    check-cast v2, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;

    #@a
    .line 76
    .local v2, "sii":Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;
    iget-object v3, v2, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->setHomeAsUpIndicator:Ljava/lang/reflect/Method;

    #@c
    if-eqz v3, :cond_1

    #@e
    .line 78
    :try_start_0
    invoke-virtual {p1}, Landroid/app/Activity;->getActionBar()Landroid/app/ActionBar;

    #@11
    move-result-object v0

    #@12
    .line 79
    .local v0, "actionBar":Landroid/app/ActionBar;
    iget-object v3, v2, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->setHomeActionContentDescription:Ljava/lang/reflect/Method;

    #@14
    const/4 v4, 0x1

    #@15
    new-array v4, v4, [Ljava/lang/Object;

    #@17
    const/4 v5, 0x0

    #@18
    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@1b
    move-result-object v6

    #@1c
    aput-object v6, v4, v5

    #@1e
    invoke-virtual {v3, v0, v4}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    #@21
    .line 80
    sget v3, Landroid/os/Build$VERSION;->SDK_INT:I

    #@23
    const/16 v4, 0x13

    #@25
    if-gt v3, v4, :cond_1

    #@27
    .line 83
    invoke-virtual {v0}, Landroid/app/ActionBar;->getSubtitle()Ljava/lang/CharSequence;

    #@2a
    move-result-object v3

    #@2b
    invoke-virtual {v0, v3}, Landroid/app/ActionBar;->setSubtitle(Ljava/lang/CharSequence;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@2e
    .line 89
    .end local v0    # "actionBar":Landroid/app/ActionBar;
    :cond_1
    :goto_0
    return-object p0

    #@2f
    .line 85
    :catch_0
    move-exception v1

    #@30
    .line 86
    .local v1, "e":Ljava/lang/Exception;
    const-string v3, "ActionBarDrawerToggleHoneycomb"

    #@32
    const-string v4, "Couldn\'t set content description via JB-MR2 API"

    #@34
    invoke-static {v3, v4, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@37
    goto :goto_0
.end method

.method public static setActionBarUpIndicator(Ljava/lang/Object;Landroid/app/Activity;Landroid/graphics/drawable/Drawable;I)Ljava/lang/Object;
    .locals 7
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "drawable"    # Landroid/graphics/drawable/Drawable;
    .param p3, "contentDescRes"    # I

    #@0
    .prologue
    .line 50
    if-nez p0, :cond_0

    #@2
    .line 51
    new-instance p0, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;

    #@4
    .end local p0    # "info":Ljava/lang/Object;
    invoke-direct {p0, p1}, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;-><init>(Landroid/app/Activity;)V

    #@7
    :cond_0
    move-object v2, p0

    #@8
    .line 53
    check-cast v2, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;

    #@a
    .line 54
    .local v2, "sii":Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;
    iget-object v3, v2, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->setHomeAsUpIndicator:Ljava/lang/reflect/Method;

    #@c
    if-eqz v3, :cond_1

    #@e
    .line 56
    :try_start_0
    invoke-virtual {p1}, Landroid/app/Activity;->getActionBar()Landroid/app/ActionBar;

    #@11
    move-result-object v0

    #@12
    .line 57
    .local v0, "actionBar":Landroid/app/ActionBar;
    iget-object v3, v2, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->setHomeAsUpIndicator:Ljava/lang/reflect/Method;

    #@14
    const/4 v4, 0x1

    #@15
    new-array v4, v4, [Ljava/lang/Object;

    #@17
    const/4 v5, 0x0

    #@18
    aput-object p2, v4, v5

    #@1a
    invoke-virtual {v3, v0, v4}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    #@1d
    .line 58
    iget-object v3, v2, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->setHomeActionContentDescription:Ljava/lang/reflect/Method;

    #@1f
    const/4 v4, 0x1

    #@20
    new-array v4, v4, [Ljava/lang/Object;

    #@22
    const/4 v5, 0x0

    #@23
    invoke-static {p3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@26
    move-result-object v6

    #@27
    aput-object v6, v4, v5

    #@29
    invoke-virtual {v3, v0, v4}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@2c
    .line 67
    .end local v0    # "actionBar":Landroid/app/ActionBar;
    :goto_0
    return-object p0

    #@2d
    .line 59
    :catch_0
    move-exception v1

    #@2e
    .line 60
    .local v1, "e":Ljava/lang/Exception;
    const-string v3, "ActionBarDrawerToggleHoneycomb"

    #@30
    const-string v4, "Couldn\'t set home-as-up indicator via JB-MR2 API"

    #@32
    invoke-static {v3, v4, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@35
    goto :goto_0

    #@36
    .line 62
    .end local v1    # "e":Ljava/lang/Exception;
    :cond_1
    iget-object v3, v2, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->upIndicatorView:Landroid/widget/ImageView;

    #@38
    if-eqz v3, :cond_2

    #@3a
    .line 63
    iget-object v3, v2, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->upIndicatorView:Landroid/widget/ImageView;

    #@3c
    invoke-virtual {v3, p2}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@3f
    goto :goto_0

    #@40
    .line 65
    :cond_2
    const-string v3, "ActionBarDrawerToggleHoneycomb"

    #@42
    const-string v4, "Couldn\'t set home-as-up indicator"

    #@44
    invoke-static {v3, v4}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@47
    goto :goto_0
.end method
