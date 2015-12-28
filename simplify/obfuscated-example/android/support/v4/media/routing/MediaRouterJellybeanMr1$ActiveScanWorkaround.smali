.class public final Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;
.super Ljava/lang/Object;
.source "MediaRouterJellybeanMr1.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/routing/MediaRouterJellybeanMr1;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "ActiveScanWorkaround"
.end annotation


# static fields
.field private static final WIFI_DISPLAY_SCAN_INTERVAL:I = 0x3a98


# instance fields
.field private mActivelyScanningWifiDisplays:Z

.field private final mDisplayManager:Landroid/hardware/display/DisplayManager;

.field private final mHandler:Landroid/os/Handler;

.field private mScanWifiDisplaysMethod:Ljava/lang/reflect/Method;


# direct methods
.method public constructor <init>(Landroid/content/Context;Landroid/os/Handler;)V
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "handler"    # Landroid/os/Handler;

    #@0
    .prologue
    .line 67
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 68
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@5
    const/16 v1, 0x11

    #@7
    if-eq v0, v1, :cond_0

    #@9
    .line 69
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@b
    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@e
    throw v0

    #@f
    .line 72
    :cond_0
    const-string v0, "display"

    #@11
    invoke-virtual {p1, v0}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@14
    move-result-object v0

    #@15
    check-cast v0, Landroid/hardware/display/DisplayManager;

    #@17
    iput-object v0, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mDisplayManager:Landroid/hardware/display/DisplayManager;

    #@19
    .line 73
    iput-object p2, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mHandler:Landroid/os/Handler;

    #@1b
    .line 75
    :try_start_0
    const-class v0, Landroid/hardware/display/DisplayManager;

    #@1d
    const-string v1, "scanWifiDisplays"

    #@1f
    const/4 v2, 0x0

    #@20
    new-array v2, v2, [Ljava/lang/Class;

    #@22
    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@25
    move-result-object v0

    #@26
    iput-object v0, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mScanWifiDisplaysMethod:Ljava/lang/reflect/Method;
    :try_end_0
    .catch Ljava/lang/NoSuchMethodException; {:try_start_0 .. :try_end_0} :catch_0

    #@28
    .line 78
    :goto_0
    return-void

    #@29
    .line 76
    :catch_0
    move-exception v0

    #@2a
    goto :goto_0
.end method


# virtual methods
.method public run()V
    .locals 4

    #@0
    .prologue
    .line 108
    iget-boolean v1, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mActivelyScanningWifiDisplays:Z

    #@2
    if-eqz v1, :cond_0

    #@4
    .line 110
    :try_start_0
    iget-object v1, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mScanWifiDisplaysMethod:Ljava/lang/reflect/Method;

    #@6
    iget-object v2, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mDisplayManager:Landroid/hardware/display/DisplayManager;

    #@8
    const/4 v3, 0x0

    #@9
    new-array v3, v3, [Ljava/lang/Object;

    #@b
    invoke-virtual {v1, v2, v3}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/IllegalAccessException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/lang/reflect/InvocationTargetException; {:try_start_0 .. :try_end_0} :catch_1

    #@e
    .line 116
    :goto_0
    iget-object v1, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mHandler:Landroid/os/Handler;

    #@10
    const-wide/16 v2, 0x3a98

    #@12
    invoke-virtual {v1, p0, v2, v3}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    #@15
    .line 118
    :cond_0
    return-void

    #@16
    .line 111
    :catch_0
    move-exception v0

    #@17
    .line 112
    .local v0, "ex":Ljava/lang/IllegalAccessException;
    const-string v1, "MediaRouterJellybeanMr1"

    #@19
    const-string v2, "Cannot scan for wifi displays."

    #@1b
    invoke-static {v1, v2, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@1e
    goto :goto_0

    #@1f
    .line 113
    .end local v0    # "ex":Ljava/lang/IllegalAccessException;
    :catch_1
    move-exception v0

    #@20
    .line 114
    .local v0, "ex":Ljava/lang/reflect/InvocationTargetException;
    const-string v1, "MediaRouterJellybeanMr1"

    #@22
    const-string v2, "Cannot scan for wifi displays."

    #@24
    invoke-static {v1, v2, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@27
    goto :goto_0
.end method

.method public setActiveScanRouteTypes(I)V
    .locals 2
    .param p1, "routeTypes"    # I

    #@0
    .prologue
    .line 87
    and-int/lit8 v0, p1, 0x2

    #@2
    if-eqz v0, :cond_2

    #@4
    .line 88
    iget-boolean v0, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mActivelyScanningWifiDisplays:Z

    #@6
    if-nez v0, :cond_0

    #@8
    .line 89
    iget-object v0, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mScanWifiDisplaysMethod:Ljava/lang/reflect/Method;

    #@a
    if-eqz v0, :cond_1

    #@c
    .line 90
    const/4 v0, 0x1

    #@d
    iput-boolean v0, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mActivelyScanningWifiDisplays:Z

    #@f
    .line 91
    iget-object v0, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mHandler:Landroid/os/Handler;

    #@11
    invoke-virtual {v0, p0}, Landroid/os/Handler;->post(Ljava/lang/Runnable;)Z

    #@14
    .line 104
    :cond_0
    :goto_0
    return-void

    #@15
    .line 93
    :cond_1
    const-string v0, "MediaRouterJellybeanMr1"

    #@17
    const-string v1, "Cannot scan for wifi displays because the DisplayManager.scanWifiDisplays() method is not available on this device."

    #@19
    invoke-static {v0, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@1c
    goto :goto_0

    #@1d
    .line 99
    :cond_2
    iget-boolean v0, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mActivelyScanningWifiDisplays:Z

    #@1f
    if-eqz v0, :cond_0

    #@21
    .line 100
    const/4 v0, 0x0

    #@22
    iput-boolean v0, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mActivelyScanningWifiDisplays:Z

    #@24
    .line 101
    iget-object v0, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;->mHandler:Landroid/os/Handler;

    #@26
    invoke-virtual {v0, p0}, Landroid/os/Handler;->removeCallbacks(Ljava/lang/Runnable;)V

    #@29
    goto :goto_0
.end method
