.class public Landroid/support/v7/internal/widget/ViewUtils;
.super Ljava/lang/Object;
.source "ViewUtils.java"


# static fields
.field private static final TAG:Ljava/lang/String; = "ViewUtils"

.field private static sComputeFitSystemWindowsMethod:Ljava/lang/reflect/Method;


# direct methods
.method static constructor <clinit>()V
    .locals 6

    #@0
    .prologue
    .line 37
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v2, 0x12

    #@4
    if-lt v1, v2, :cond_0

    #@6
    .line 39
    :try_start_0
    const-class v1, Landroid/view/View;

    #@8
    const-string v2, "computeFitSystemWindows"

    #@a
    const/4 v3, 0x2

    #@b
    new-array v3, v3, [Ljava/lang/Class;

    #@d
    const/4 v4, 0x0

    #@e
    const-class v5, Landroid/graphics/Rect;

    #@10
    aput-object v5, v3, v4

    #@12
    const/4 v4, 0x1

    #@13
    const-class v5, Landroid/graphics/Rect;

    #@15
    aput-object v5, v3, v4

    #@17
    invoke-virtual {v1, v2, v3}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@1a
    move-result-object v1

    #@1b
    sput-object v1, Landroid/support/v7/internal/widget/ViewUtils;->sComputeFitSystemWindowsMethod:Ljava/lang/reflect/Method;

    #@1d
    .line 41
    sget-object v1, Landroid/support/v7/internal/widget/ViewUtils;->sComputeFitSystemWindowsMethod:Ljava/lang/reflect/Method;

    #@1f
    invoke-virtual {v1}, Ljava/lang/reflect/Method;->isAccessible()Z

    #@22
    move-result v1

    #@23
    if-nez v1, :cond_0

    #@25
    .line 42
    sget-object v1, Landroid/support/v7/internal/widget/ViewUtils;->sComputeFitSystemWindowsMethod:Ljava/lang/reflect/Method;

    #@27
    const/4 v2, 0x1

    #@28
    invoke-virtual {v1, v2}, Ljava/lang/reflect/Method;->setAccessible(Z)V
    :try_end_0
    .catch Ljava/lang/NoSuchMethodException; {:try_start_0 .. :try_end_0} :catch_0

    #@2b
    .line 48
    .local v0, "e":Ljava/lang/NoSuchMethodException;
    :cond_0
    :goto_0
    return-void

    #@2c
    .line 44
    .end local v0    # "e":Ljava/lang/NoSuchMethodException;
    :catch_0
    move-exception v0

    #@2d
    .line 45
    .restart local v0    # "e":Ljava/lang/NoSuchMethodException;
    const-string v1, "ViewUtils"

    #@2f
    const-string v2, "Could not find method computeFitSystemWindows. Oh well."

    #@31
    invoke-static {v1, v2}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    #@34
    goto :goto_0
.end method

.method private constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 50
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static combineMeasuredStates(II)I
    .locals 1
    .param p0, "curState"    # I
    .param p1, "newState"    # I

    #@0
    .prologue
    .line 65
    or-int v0, p0, p1

    #@2
    return v0
.end method

.method public static computeFitSystemWindows(Landroid/view/View;Landroid/graphics/Rect;Landroid/graphics/Rect;)V
    .locals 4
    .param p0, "view"    # Landroid/view/View;
    .param p1, "inoutInsets"    # Landroid/graphics/Rect;
    .param p2, "outLocalInsets"    # Landroid/graphics/Rect;

    #@0
    .prologue
    .line 73
    sget-object v1, Landroid/support/v7/internal/widget/ViewUtils;->sComputeFitSystemWindowsMethod:Ljava/lang/reflect/Method;

    #@2
    if-eqz v1, :cond_0

    #@4
    .line 75
    :try_start_0
    sget-object v1, Landroid/support/v7/internal/widget/ViewUtils;->sComputeFitSystemWindowsMethod:Ljava/lang/reflect/Method;

    #@6
    const/4 v2, 0x2

    #@7
    new-array v2, v2, [Ljava/lang/Object;

    #@9
    const/4 v3, 0x0

    #@a
    aput-object p1, v2, v3

    #@c
    const/4 v3, 0x1

    #@d
    aput-object p2, v2, v3

    #@f
    invoke-virtual {v1, p0, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@12
    .line 80
    :cond_0
    :goto_0
    return-void

    #@13
    .line 76
    :catch_0
    move-exception v0

    #@14
    .line 77
    .local v0, "e":Ljava/lang/Exception;
    const-string v1, "ViewUtils"

    #@16
    const-string v2, "Could not invoke computeFitSystemWindows"

    #@18
    invoke-static {v1, v2, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@1b
    goto :goto_0
.end method

.method public static isLayoutRtl(Landroid/view/View;)Z
    .locals 2
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 53
    invoke-static {p0}, Landroid/support/v4/view/ViewCompat;->getLayoutDirection(Landroid/view/View;)I

    #@4
    move-result v1

    #@5
    if-ne v1, v0, :cond_0

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method public static makeOptionalFitsSystemWindows(Landroid/view/View;)V
    .locals 5
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 87
    sget v2, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v3, 0x10

    #@4
    if-lt v2, v3, :cond_1

    #@6
    .line 91
    :try_start_0
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@9
    move-result-object v2

    #@a
    const-string v3, "makeOptionalFitsSystemWindows"

    #@c
    const/4 v4, 0x0

    #@d
    new-array v4, v4, [Ljava/lang/Class;

    #@f
    invoke-virtual {v2, v3, v4}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@12
    move-result-object v1

    #@13
    .line 92
    .local v1, "method":Ljava/lang/reflect/Method;
    invoke-virtual {v1}, Ljava/lang/reflect/Method;->isAccessible()Z

    #@16
    move-result v2

    #@17
    if-nez v2, :cond_0

    #@19
    .line 93
    const/4 v2, 0x1

    #@1a
    invoke-virtual {v1, v2}, Ljava/lang/reflect/Method;->setAccessible(Z)V

    #@1d
    .line 95
    :cond_0
    const/4 v2, 0x0

    #@1e
    new-array v2, v2, [Ljava/lang/Object;

    #@20
    invoke-virtual {v1, p0, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/NoSuchMethodException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/lang/reflect/InvocationTargetException; {:try_start_0 .. :try_end_0} :catch_1
    .catch Ljava/lang/IllegalAccessException; {:try_start_0 .. :try_end_0} :catch_2

    #@23
    .line 104
    .end local v1    # "method":Ljava/lang/reflect/Method;
    :cond_1
    :goto_0
    return-void

    #@24
    .line 96
    :catch_0
    move-exception v0

    #@25
    .line 97
    .local v0, "e":Ljava/lang/NoSuchMethodException;
    const-string v2, "ViewUtils"

    #@27
    const-string v3, "Could not find method makeOptionalFitsSystemWindows. Oh well..."

    #@29
    invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    #@2c
    goto :goto_0

    #@2d
    .line 98
    .end local v0    # "e":Ljava/lang/NoSuchMethodException;
    :catch_1
    move-exception v0

    #@2e
    .line 99
    .local v0, "e":Ljava/lang/reflect/InvocationTargetException;
    const-string v2, "ViewUtils"

    #@30
    const-string v3, "Could not invoke makeOptionalFitsSystemWindows"

    #@32
    invoke-static {v2, v3, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@35
    goto :goto_0

    #@36
    .line 100
    .end local v0    # "e":Ljava/lang/reflect/InvocationTargetException;
    :catch_2
    move-exception v0

    #@37
    .line 101
    .local v0, "e":Ljava/lang/IllegalAccessException;
    const-string v2, "ViewUtils"

    #@39
    const-string v3, "Could not invoke makeOptionalFitsSystemWindows"

    #@3b
    invoke-static {v2, v3, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@3e
    goto :goto_0
.end method
