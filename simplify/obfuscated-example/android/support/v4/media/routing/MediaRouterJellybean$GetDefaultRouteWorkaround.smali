.class public final Landroid/support/v4/media/routing/MediaRouterJellybean$GetDefaultRouteWorkaround;
.super Ljava/lang/Object;
.source "MediaRouterJellybean.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/routing/MediaRouterJellybean;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "GetDefaultRouteWorkaround"
.end annotation


# instance fields
.field private mGetSystemAudioRouteMethod:Ljava/lang/reflect/Method;


# direct methods
.method public constructor <init>()V
    .locals 3

    #@0
    .prologue
    .line 335
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 336
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@5
    const/16 v1, 0x10

    #@7
    if-lt v0, v1, :cond_0

    #@9
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@b
    const/16 v1, 0x11

    #@d
    if-le v0, v1, :cond_1

    #@f
    .line 337
    :cond_0
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@11
    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@14
    throw v0

    #@15
    .line 340
    :cond_1
    :try_start_0
    const-class v0, Landroid/media/MediaRouter;

    #@17
    const-string v1, "getSystemAudioRoute"

    #@19
    const/4 v2, 0x0

    #@1a
    new-array v2, v2, [Ljava/lang/Class;

    #@1c
    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@1f
    move-result-object v0

    #@20
    iput-object v0, p0, Landroid/support/v4/media/routing/MediaRouterJellybean$GetDefaultRouteWorkaround;->mGetSystemAudioRouteMethod:Ljava/lang/reflect/Method;
    :try_end_0
    .catch Ljava/lang/NoSuchMethodException; {:try_start_0 .. :try_end_0} :catch_0

    #@22
    .line 344
    :goto_0
    return-void

    #@23
    .line 342
    :catch_0
    move-exception v0

    #@24
    goto :goto_0
.end method


# virtual methods
.method public getDefaultRoute(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 4
    .param p1, "routerObj"    # Ljava/lang/Object;

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 347
    move-object v0, p1

    #@2
    check-cast v0, Landroid/media/MediaRouter;

    #@4
    .line 349
    .local v0, "router":Landroid/media/MediaRouter;
    iget-object v1, p0, Landroid/support/v4/media/routing/MediaRouterJellybean$GetDefaultRouteWorkaround;->mGetSystemAudioRouteMethod:Ljava/lang/reflect/Method;

    #@6
    if-eqz v1, :cond_0

    #@8
    .line 351
    :try_start_0
    iget-object v1, p0, Landroid/support/v4/media/routing/MediaRouterJellybean$GetDefaultRouteWorkaround;->mGetSystemAudioRouteMethod:Ljava/lang/reflect/Method;

    #@a
    const/4 v2, 0x0

    #@b
    new-array v2, v2, [Ljava/lang/Object;

    #@d
    invoke-virtual {v1, v0, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/IllegalAccessException; {:try_start_0 .. :try_end_0} :catch_1
    .catch Ljava/lang/reflect/InvocationTargetException; {:try_start_0 .. :try_end_0} :catch_0

    #@10
    move-result-object v1

    #@11
    .line 359
    :goto_0
    return-object v1

    #@12
    .line 353
    :catch_0
    move-exception v1

    #@13
    .line 359
    :cond_0
    :goto_1
    invoke-virtual {v0, v3}, Landroid/media/MediaRouter;->getRouteAt(I)Landroid/media/MediaRouter$RouteInfo;

    #@16
    move-result-object v1

    #@17
    goto :goto_0

    #@18
    .line 352
    :catch_1
    move-exception v1

    #@19
    goto :goto_1
.end method
