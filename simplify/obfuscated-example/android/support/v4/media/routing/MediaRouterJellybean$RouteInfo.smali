.class public final Landroid/support/v4/media/routing/MediaRouterJellybean$RouteInfo;
.super Ljava/lang/Object;
.source "MediaRouterJellybean.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/routing/MediaRouterJellybean;
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
    .line 114
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getCategory(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 128
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteInfo;->getCategory()Landroid/media/MediaRouter$RouteCategory;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getGroup(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 172
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteInfo;->getGroup()Landroid/media/MediaRouter$RouteGroup;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getIconDrawable(Ljava/lang/Object;)Landroid/graphics/drawable/Drawable;
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 132
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteInfo;->getIconDrawable()Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getName(Ljava/lang/Object;Landroid/content/Context;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 116
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaRouter$RouteInfo;->getName(Landroid/content/Context;)Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getPlaybackStream(Ljava/lang/Object;)I
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 140
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteInfo;->getPlaybackStream()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getPlaybackType(Ljava/lang/Object;)I
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 136
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteInfo;->getPlaybackType()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getStatus(Ljava/lang/Object;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 120
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteInfo;->getStatus()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getSupportedTypes(Ljava/lang/Object;)I
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 124
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteInfo;->getSupportedTypes()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getTag(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 156
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteInfo;->getTag()Ljava/lang/Object;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getVolume(Ljava/lang/Object;)I
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 144
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteInfo;->getVolume()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getVolumeHandling(Ljava/lang/Object;)I
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 152
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteInfo;->getVolumeHandling()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getVolumeMax(Ljava/lang/Object;)I
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 148
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteInfo;->getVolumeMax()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isGroup(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 176
    instance-of v0, p0, Landroid/media/MediaRouter$RouteGroup;

    #@2
    return v0
.end method

.method public static requestSetVolume(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "routeObj"    # Ljava/lang/Object;
    .param p1, "volume"    # I

    #@0
    .prologue
    .line 164
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaRouter$RouteInfo;->requestSetVolume(I)V

    #@5
    .line 165
    return-void
.end method

.method public static requestUpdateVolume(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "routeObj"    # Ljava/lang/Object;
    .param p1, "direction"    # I

    #@0
    .prologue
    .line 168
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaRouter$RouteInfo;->requestUpdateVolume(I)V

    #@5
    .line 169
    return-void
.end method

.method public static setTag(Ljava/lang/Object;Ljava/lang/Object;)V
    .locals 0
    .param p0, "routeObj"    # Ljava/lang/Object;
    .param p1, "tag"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 160
    check-cast p0, Landroid/media/MediaRouter$RouteInfo;

    #@2
    .end local p0    # "routeObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaRouter$RouteInfo;->setTag(Ljava/lang/Object;)V

    #@5
    .line 161
    return-void
.end method
