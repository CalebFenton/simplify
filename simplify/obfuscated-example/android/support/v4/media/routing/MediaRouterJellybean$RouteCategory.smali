.class public final Landroid/support/v4/media/routing/MediaRouterJellybean$RouteCategory;
.super Ljava/lang/Object;
.source "MediaRouterJellybean.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/routing/MediaRouterJellybean;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "RouteCategory"
.end annotation


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 238
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getName(Ljava/lang/Object;Landroid/content/Context;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "categoryObj"    # Ljava/lang/Object;
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 240
    check-cast p0, Landroid/media/MediaRouter$RouteCategory;

    #@2
    .end local p0    # "categoryObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaRouter$RouteCategory;->getName(Landroid/content/Context;)Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getRoutes(Ljava/lang/Object;)Ljava/util/List;
    .locals 1
    .param p0, "categoryObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 245
    new-instance v0, Ljava/util/ArrayList;

    #@2
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@5
    .line 246
    .local v0, "out":Ljava/util/ArrayList;
    check-cast p0, Landroid/media/MediaRouter$RouteCategory;

    #@7
    .end local p0    # "categoryObj":Ljava/lang/Object;
    invoke-virtual {p0, v0}, Landroid/media/MediaRouter$RouteCategory;->getRoutes(Ljava/util/List;)Ljava/util/List;

    #@a
    .line 247
    return-object v0
.end method

.method public static getSupportedTypes(Ljava/lang/Object;)I
    .locals 1
    .param p0, "categoryObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 251
    check-cast p0, Landroid/media/MediaRouter$RouteCategory;

    #@2
    .end local p0    # "categoryObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteCategory;->getSupportedTypes()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isGroupable(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "categoryObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 255
    check-cast p0, Landroid/media/MediaRouter$RouteCategory;

    #@2
    .end local p0    # "categoryObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaRouter$RouteCategory;->isGroupable()Z

    #@5
    move-result v0

    #@6
    return v0
.end method
