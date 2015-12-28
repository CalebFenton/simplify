.class Landroid/support/v4/view/ViewCompatEclairMr1;
.super Ljava/lang/Object;
.source "ViewCompatEclairMr1.java"


# static fields
.field public static final TAG:Ljava/lang/String; = "ViewCompat"

.field private static sChildrenDrawingOrderMethod:Ljava/lang/reflect/Method;


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 27
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static isOpaque(Landroid/view/View;)Z
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 33
    invoke-virtual {p0}, Landroid/view/View;->isOpaque()Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static setChildrenDrawingOrderEnabled(Landroid/view/ViewGroup;Z)V
    .locals 7
    .param p0, "viewGroup"    # Landroid/view/ViewGroup;
    .param p1, "enabled"    # Z

    #@0
    .prologue
    const/4 v6, 0x1

    #@1
    .line 37
    sget-object v1, Landroid/support/v4/view/ViewCompatEclairMr1;->sChildrenDrawingOrderMethod:Ljava/lang/reflect/Method;

    #@3
    if-nez v1, :cond_0

    #@5
    .line 39
    :try_start_0
    const-class v1, Landroid/view/ViewGroup;

    #@7
    const-string v2, "setChildrenDrawingOrderEnabled"

    #@9
    const/4 v3, 0x1

    #@a
    new-array v3, v3, [Ljava/lang/Class;

    #@c
    const/4 v4, 0x0

    #@d
    sget-object v5, Ljava/lang/Boolean;->TYPE:Ljava/lang/Class;

    #@f
    aput-object v5, v3, v4

    #@11
    invoke-virtual {v1, v2, v3}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@14
    move-result-object v1

    #@15
    sput-object v1, Landroid/support/v4/view/ViewCompatEclairMr1;->sChildrenDrawingOrderMethod:Ljava/lang/reflect/Method;
    :try_end_0
    .catch Ljava/lang/NoSuchMethodException; {:try_start_0 .. :try_end_0} :catch_0

    #@17
    .line 44
    :goto_0
    sget-object v1, Landroid/support/v4/view/ViewCompatEclairMr1;->sChildrenDrawingOrderMethod:Ljava/lang/reflect/Method;

    #@19
    invoke-virtual {v1, v6}, Ljava/lang/reflect/Method;->setAccessible(Z)V

    #@1c
    .line 47
    :cond_0
    :try_start_1
    sget-object v1, Landroid/support/v4/view/ViewCompatEclairMr1;->sChildrenDrawingOrderMethod:Ljava/lang/reflect/Method;

    #@1e
    const/4 v2, 0x1

    #@1f
    new-array v2, v2, [Ljava/lang/Object;

    #@21
    const/4 v3, 0x0

    #@22
    invoke-static {p1}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    #@25
    move-result-object v4

    #@26
    aput-object v4, v2, v3

    #@28
    invoke-virtual {v1, p0, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_1
    .catch Ljava/lang/IllegalAccessException; {:try_start_1 .. :try_end_1} :catch_1
    .catch Ljava/lang/IllegalArgumentException; {:try_start_1 .. :try_end_1} :catch_2
    .catch Ljava/lang/reflect/InvocationTargetException; {:try_start_1 .. :try_end_1} :catch_3

    #@2b
    .line 55
    :goto_1
    return-void

    #@2c
    .line 41
    :catch_0
    move-exception v0

    #@2d
    .line 42
    .local v0, "e":Ljava/lang/NoSuchMethodException;
    const-string v1, "ViewCompat"

    #@2f
    const-string v2, "Unable to find childrenDrawingOrderEnabled"

    #@31
    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@34
    goto :goto_0

    #@35
    .line 48
    .end local v0    # "e":Ljava/lang/NoSuchMethodException;
    :catch_1
    move-exception v0

    #@36
    .line 49
    .local v0, "e":Ljava/lang/IllegalAccessException;
    const-string v1, "ViewCompat"

    #@38
    const-string v2, "Unable to invoke childrenDrawingOrderEnabled"

    #@3a
    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@3d
    goto :goto_1

    #@3e
    .line 50
    .end local v0    # "e":Ljava/lang/IllegalAccessException;
    :catch_2
    move-exception v0

    #@3f
    .line 51
    .local v0, "e":Ljava/lang/IllegalArgumentException;
    const-string v1, "ViewCompat"

    #@41
    const-string v2, "Unable to invoke childrenDrawingOrderEnabled"

    #@43
    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@46
    goto :goto_1

    #@47
    .line 52
    .end local v0    # "e":Ljava/lang/IllegalArgumentException;
    :catch_3
    move-exception v0

    #@48
    .line 53
    .local v0, "e":Ljava/lang/reflect/InvocationTargetException;
    const-string v1, "ViewCompat"

    #@4a
    const-string v2, "Unable to invoke childrenDrawingOrderEnabled"

    #@4c
    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@4f
    goto :goto_1
.end method
