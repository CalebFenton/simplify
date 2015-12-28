.class public final Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$RouteInfo;
.super Ljava/lang/Object;
.source "MediaRouterJellybeanMr1.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/routing/MediaRouterJellybeanMr1;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "RouteInfo"
.end annotation


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 37
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getPresentationDisplay(Ljava/lang/Object;)Landroid/view/Display;
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 43
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteInfo;->getPresentationDisplay()Landroid/view/Display;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static isEnabled(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 39
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteInfo;->isEnabled()Z

    #@5
    move-result v0

    #@6
    return v0
.end method
