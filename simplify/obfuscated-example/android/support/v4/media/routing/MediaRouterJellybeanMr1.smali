.class Landroid/support/v4/media/routing/MediaRouterJellybeanMr1;
.super Landroid/support/v4/media/routing/MediaRouterJellybean;
.source "MediaRouterJellybeanMr1.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$CallbackProxy;,
        Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$IsConnectingWorkaround;,
        Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$ActiveScanWorkaround;,
        Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$Callback;,
        Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$RouteInfo;
    }
.end annotation


# static fields
.field private static final TAG:Ljava/lang/String; = "MediaRouterJellybeanMr1"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 30
    invoke-direct {p0}, Landroid/support/v4/media/routing/MediaRouterJellybean;-><init>()V

    #@3
    .line 164
    return-void
.end method

.method public static createCallback(Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$Callback;)Ljava/lang/Object;
    .locals 1
    .param p0, "callback"    # Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$Callback;

    #@0
    .prologue
    .line 34
    new-instance v0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$CallbackProxy;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$CallbackProxy;-><init>(Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$Callback;)V

    #@5
    return-object v0
.end method
