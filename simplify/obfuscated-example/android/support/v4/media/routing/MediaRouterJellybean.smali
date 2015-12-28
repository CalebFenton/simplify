.class Landroid/support/v4/media/routing/MediaRouterJellybean;
.super Ljava/lang/Object;
.source "MediaRouterJellybean.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/media/routing/MediaRouterJellybean$VolumeCallbackProxy;,
        Landroid/support/v4/media/routing/MediaRouterJellybean$CallbackProxy;,
        Landroid/support/v4/media/routing/MediaRouterJellybean$GetDefaultRouteWorkaround;,
        Landroid/support/v4/media/routing/MediaRouterJellybean$SelectRouteWorkaround;,
        Landroid/support/v4/media/routing/MediaRouterJellybean$VolumeCallback;,
        Landroid/support/v4/media/routing/MediaRouterJellybean$Callback;,
        Landroid/support/v4/media/routing/MediaRouterJellybean$RouteCategory;,
        Landroid/support/v4/media/routing/MediaRouterJellybean$UserRouteInfo;,
        Landroid/support/v4/media/routing/MediaRouterJellybean$RouteGroup;,
        Landroid/support/v4/media/routing/MediaRouterJellybean$RouteInfo;
    }
.end annotation


# static fields
.field public static final ALL_ROUTE_TYPES:I = 0x800003

.field public static final ROUTE_TYPE_LIVE_AUDIO:I = 0x1

.field public static final ROUTE_TYPE_LIVE_VIDEO:I = 0x2

.field public static final ROUTE_TYPE_USER:I = 0x800000

.field private static final TAG:Ljava/lang/String; = "MediaRouterJellybean"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 29
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 422
    return-void
.end method

.method public static addCallback(Ljava/lang/Object;ILjava/lang/Object;)V
    .locals 0
    .param p0, "routerObj"    # Ljava/lang/Object;
    .param p1, "types"    # I
    .param p2, "callbackObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 77
    check-cast p0, Landroid/media/MediaRouter;

    #@2
    .end local p0    # "routerObj":Ljava/lang/Object;
    check-cast p2, Landroid/media/MediaRouter$Callback;

    #@4
    .end local p2    # "callbackObj":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2}, Landroid/media/MediaRouter;->addCallback(ILandroid/media/MediaRouter$Callback;)V

    #@7
    .line 79
    return-void
.end method

.method public static addUserRoute(Ljava/lang/Object;Ljava/lang/Object;)V
    .locals 0
    .param p0, "routerObj"    # Ljava/lang/Object;
    .param p1, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 97
    check-cast p0, Landroid/media/MediaRouter;

    #@2
    .end local p0    # "routerObj":Ljava/lang/Object;
    check-cast p1, Landroid/media/MediaRouter$UserRouteInfo;

    #@4
    .end local p1    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaRouter;->addUserRoute(Landroid/media/MediaRouter$UserRouteInfo;)V

    #@7
    .line 99
    return-void
.end method

.method public static createCallback(Landroid/support/v4/media/routing/MediaRouterJellybean$Callback;)Ljava/lang/Object;
    .locals 1
    .param p0, "callback"    # Landroid/support/v4/media/routing/MediaRouterJellybean$Callback;

    #@0
    .prologue
    .line 107
    new-instance v0, Landroid/support/v4/media/routing/MediaRouterJellybean$CallbackProxy;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v4/media/routing/MediaRouterJellybean$CallbackProxy;-><init>(Landroid/support/v4/media/routing/MediaRouterJellybean$Callback;)V

    #@5
    return-object v0
.end method

.method public static createRouteCategory(Ljava/lang/Object;Ljava/lang/String;Z)Ljava/lang/Object;
    .locals 1
    .param p0, "routerObj"    # Ljava/lang/Object;
    .param p1, "name"    # Ljava/lang/String;
    .param p2, "isGroupable"    # Z

    #@0
    .prologue
    .line 88
    check-cast p0, Landroid/media/MediaRouter;

    #@2
    .end local p0    # "routerObj":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2}, Landroid/media/MediaRouter;->createRouteCategory(Ljava/lang/CharSequence;Z)Landroid/media/MediaRouter$RouteCategory;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static createUserRoute(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .param p0, "routerObj"    # Ljava/lang/Object;
    .param p1, "categoryObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 92
    check-cast p0, Landroid/media/MediaRouter;

    #@2
    .end local p0    # "routerObj":Ljava/lang/Object;
    check-cast p1, Landroid/media/MediaRouter$RouteCategory;

    #@4
    .end local p1    # "categoryObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaRouter;->createUserRoute(Landroid/media/MediaRouter$RouteCategory;)Landroid/media/MediaRouter$UserRouteInfo;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public static createVolumeCallback(Landroid/support/v4/media/routing/MediaRouterJellybean$VolumeCallback;)Ljava/lang/Object;
    .locals 1
    .param p0, "callback"    # Landroid/support/v4/media/routing/MediaRouterJellybean$VolumeCallback;

    #@0
    .prologue
    .line 111
    new-instance v0, Landroid/support/v4/media/routing/MediaRouterJellybean$VolumeCallbackProxy;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v4/media/routing/MediaRouterJellybean$VolumeCallbackProxy;-><init>(Landroid/support/v4/media/routing/MediaRouterJellybean$VolumeCallback;)V

    #@5
    return-object v0
.end method

.method public static getCategories(Ljava/lang/Object;)Ljava/util/List;
    .locals 5
    .param p0, "routerObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 58
    move-object v3, p0

    #@1
    check-cast v3, Landroid/media/MediaRouter;

    #@3
    .line 59
    .local v3, "router":Landroid/media/MediaRouter;
    invoke-virtual {v3}, Landroid/media/MediaRouter;->getCategoryCount()I

    #@6
    move-result v0

    #@7
    .line 60
    .local v0, "count":I
    new-instance v2, Ljava/util/ArrayList;

    #@9
    invoke-direct {v2, v0}, Ljava/util/ArrayList;-><init>(I)V

    #@c
    .line 61
    .local v2, "out":Ljava/util/List;
    const/4 v1, 0x0

    #@d
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_0

    #@f
    .line 62
    invoke-virtual {v3, v1}, Landroid/media/MediaRouter;->getCategoryAt(I)Landroid/media/MediaRouter$RouteCategory;

    #@12
    move-result-object v4

    #@13
    invoke-interface {v2, v4}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    #@16
    .line 61
    add-int/lit8 v1, v1, 0x1

    #@18
    goto :goto_0

    #@19
    .line 64
    :cond_0
    return-object v2
.end method

.method public static getMediaRouter(Landroid/content/Context;)Ljava/lang/Object;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 42
    const-string v0, "media_router"

    #@2
    invoke-virtual {p0, v0}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getRoutes(Ljava/lang/Object;)Ljava/util/List;
    .locals 5
    .param p0, "routerObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 47
    move-object v3, p0

    #@1
    check-cast v3, Landroid/media/MediaRouter;

    #@3
    .line 48
    .local v3, "router":Landroid/media/MediaRouter;
    invoke-virtual {v3}, Landroid/media/MediaRouter;->getRouteCount()I

    #@6
    move-result v0

    #@7
    .line 49
    .local v0, "count":I
    new-instance v2, Ljava/util/ArrayList;

    #@9
    invoke-direct {v2, v0}, Ljava/util/ArrayList;-><init>(I)V

    #@c
    .line 50
    .local v2, "out":Ljava/util/List;
    const/4 v1, 0x0

    #@d
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_0

    #@f
    .line 51
    invoke-virtual {v3, v1}, Landroid/media/MediaRouter;->getRouteAt(I)Landroid/media/MediaRouter$RouteInfo;

    #@12
    move-result-object v4

    #@13
    invoke-interface {v2, v4}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    #@16
    .line 50
    add-int/lit8 v1, v1, 0x1

    #@18
    goto :goto_0

    #@19
    .line 53
    :cond_0
    return-object v2
.end method

.method public static getSelectedRoute(Ljava/lang/Object;I)Ljava/lang/Object;
    .locals 1
    .param p0, "routerObj"    # Ljava/lang/Object;
    .param p1, "type"    # I

    #@0
    .prologue
    .line 68
    check-cast p0, Landroid/media/MediaRouter;

    #@2
    .end local p0    # "routerObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaRouter;->getSelectedRoute(I)Landroid/media/MediaRouter$RouteInfo;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static removeCallback(Ljava/lang/Object;Ljava/lang/Object;)V
    .locals 0
    .param p0, "routerObj"    # Ljava/lang/Object;
    .param p1, "callbackObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 82
    check-cast p0, Landroid/media/MediaRouter;

    #@2
    .end local p0    # "routerObj":Ljava/lang/Object;
    check-cast p1, Landroid/media/MediaRouter$Callback;

    #@4
    .end local p1    # "callbackObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaRouter;->removeCallback(Landroid/media/MediaRouter$Callback;)V

    #@7
    .line 84
    return-void
.end method

.method public static removeUserRoute(Ljava/lang/Object;Ljava/lang/Object;)V
    .locals 0
    .param p0, "routerObj"    # Ljava/lang/Object;
    .param p1, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 102
    check-cast p0, Landroid/media/MediaRouter;

    #@2
    .end local p0    # "routerObj":Ljava/lang/Object;
    check-cast p1, Landroid/media/MediaRouter$UserRouteInfo;

    #@4
    .end local p1    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaRouter;->removeUserRoute(Landroid/media/MediaRouter$UserRouteInfo;)V

    #@7
    .line 104
    return-void
.end method

.method public static selectRoute(Ljava/lang/Object;ILjava/lang/Object;)V
    .locals 0
    .param p0, "routerObj"    # Ljava/lang/Object;
    .param p1, "types"    # I
    .param p2, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 72
    check-cast p0, Landroid/media/MediaRouter;

    #@2
    .end local p0    # "routerObj":Ljava/lang/Object;
    check-cast p2, Landroid/media/MediaRouter$RouteInfo;

    #@4
    .end local p2    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2}, Landroid/media/MediaRouter;->selectRoute(ILandroid/media/MediaRouter$RouteInfo;)V

    #@7
    .line 74
    return-void
.end method
