.class Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb;
.super Ljava/lang/Object;
.source "ActionBarDrawerToggleHoneycomb.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;
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
    .line 45
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
    sput-object v0, Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb;->THEME_ATTRS:[I

    #@b
    return-void
.end method

.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 42
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 98
    return-void
.end method

.method public static getThemeUpIndicator(Landroid/app/Activity;)Landroid/graphics/drawable/Drawable;
    .locals 3
    .param p0, "activity"    # Landroid/app/Activity;

    #@0
    .prologue
    .line 92
    sget-object v2, Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb;->THEME_ATTRS:[I

    #@2
    invoke-virtual {p0, v2}, Landroid/app/Activity;->obtainStyledAttributes([I)Landroid/content/res/TypedArray;

    #@5
    move-result-object v0

    #@6
    .line 93
    .local v0, "a":Landroid/content/res/TypedArray;
    const/4 v2, 0x0

    #@7
    invoke-virtual {v0, v2}, Landroid/content/res/TypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@a
    move-result-object v1

    #@b
    .line 94
    .local v1, "result":Landroid/graphics/drawable/Drawable;
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@e
    .line 95
    return-object v1
.end method

.method public static setActionBarDescription(Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;Landroid/app/Activity;I)Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;
    .locals 6
    .param p0, "info"    # Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "contentDescRes"    # I

    #@0
    .prologue
    .line 72
    if-nez p0, :cond_0

    #@2
    .line 73
    new-instance p0, Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;

    #@4
    .end local p0    # "info":Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;-><init>(Landroid/app/Activity;)V

    #@7
    .line 75
    .restart local p0    # "info":Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;
    :cond_0
    iget-object v2, p0, Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->setHomeAsUpIndicator:Ljava/lang/reflect/Method;

    #@9
    if-eqz v2, :cond_1

    #@b
    .line 77
    :try_start_0
    invoke-virtual {p1}, Landroid/app/Activity;->getActionBar()Landroid/app/ActionBar;

    #@e
    move-result-object v0

    #@f
    .line 78
    .local v0, "actionBar":Landroid/app/ActionBar;
    iget-object v2, p0, Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->setHomeActionContentDescription:Ljava/lang/reflect/Method;

    #@11
    const/4 v3, 0x1

    #@12
    new-array v3, v3, [Ljava/lang/Object;

    #@14
    const/4 v4, 0x0

    #@15
    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@18
    move-result-object v5

    #@19
    aput-object v5, v3, v4

    #@1b
    invoke-virtual {v2, v0, v3}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    #@1e
    .line 79
    sget v2, Landroid/os/Build$VERSION;->SDK_INT:I

    #@20
    const/16 v3, 0x13

    #@22
    if-gt v2, v3, :cond_1

    #@24
    .line 82
    invoke-virtual {v0}, Landroid/app/ActionBar;->getSubtitle()Ljava/lang/CharSequence;

    #@27
    move-result-object v2

    #@28
    invoke-virtual {v0, v2}, Landroid/app/ActionBar;->setSubtitle(Ljava/lang/CharSequence;)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@2b
    .line 88
    .end local v0    # "actionBar":Landroid/app/ActionBar;
    :cond_1
    :goto_0
    return-object p0

    #@2c
    .line 84
    :catch_0
    move-exception v1

    #@2d
    .line 85
    .local v1, "e":Ljava/lang/Exception;
    const-string v2, "ActionBarDrawerToggleHoneycomb"

    #@2f
    const-string v3, "Couldn\'t set content description via JB-MR2 API"

    #@31
    invoke-static {v2, v3, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@34
    goto :goto_0
.end method

.method public static setActionBarUpIndicator(Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;Landroid/app/Activity;Landroid/graphics/drawable/Drawable;I)Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;
    .locals 6
    .param p0, "info"    # Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "drawable"    # Landroid/graphics/drawable/Drawable;
    .param p3, "contentDescRes"    # I

    #@0
    .prologue
    .line 52
    new-instance p0, Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;

    #@2
    .end local p0    # "info":Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;
    invoke-direct {p0, p1}, Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;-><init>(Landroid/app/Activity;)V

    #@5
    .line 54
    .restart local p0    # "info":Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;
    iget-object v2, p0, Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->setHomeAsUpIndicator:Ljava/lang/reflect/Method;

    #@7
    if-eqz v2, :cond_0

    #@9
    .line 56
    :try_start_0
    invoke-virtual {p1}, Landroid/app/Activity;->getActionBar()Landroid/app/ActionBar;

    #@c
    move-result-object v0

    #@d
    .line 57
    .local v0, "actionBar":Landroid/app/ActionBar;
    iget-object v2, p0, Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->setHomeAsUpIndicator:Ljava/lang/reflect/Method;

    #@f
    const/4 v3, 0x1

    #@10
    new-array v3, v3, [Ljava/lang/Object;

    #@12
    const/4 v4, 0x0

    #@13
    aput-object p2, v3, v4

    #@15
    invoke-virtual {v2, v0, v3}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    #@18
    .line 58
    iget-object v2, p0, Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->setHomeActionContentDescription:Ljava/lang/reflect/Method;

    #@1a
    const/4 v3, 0x1

    #@1b
    new-array v3, v3, [Ljava/lang/Object;

    #@1d
    const/4 v4, 0x0

    #@1e
    invoke-static {p3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@21
    move-result-object v5

    #@22
    aput-object v5, v3, v4

    #@24
    invoke-virtual {v2, v0, v3}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@27
    .line 67
    .end local v0    # "actionBar":Landroid/app/ActionBar;
    :goto_0
    return-object p0

    #@28
    .line 59
    :catch_0
    move-exception v1

    #@29
    .line 60
    .local v1, "e":Ljava/lang/Exception;
    const-string v2, "ActionBarDrawerToggleHoneycomb"

    #@2b
    const-string v3, "Couldn\'t set home-as-up indicator via JB-MR2 API"

    #@2d
    invoke-static {v2, v3, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@30
    goto :goto_0

    #@31
    .line 62
    .end local v1    # "e":Ljava/lang/Exception;
    :cond_0
    iget-object v2, p0, Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->upIndicatorView:Landroid/widget/ImageView;

    #@33
    if-eqz v2, :cond_1

    #@35
    .line 63
    iget-object v2, p0, Landroid/support/v7/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->upIndicatorView:Landroid/widget/ImageView;

    #@37
    invoke-virtual {v2, p2}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@3a
    goto :goto_0

    #@3b
    .line 65
    :cond_1
    const-string v2, "ActionBarDrawerToggleHoneycomb"

    #@3d
    const-string v3, "Couldn\'t set home-as-up indicator"

    #@3f
    invoke-static {v2, v3}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@42
    goto :goto_0
.end method
