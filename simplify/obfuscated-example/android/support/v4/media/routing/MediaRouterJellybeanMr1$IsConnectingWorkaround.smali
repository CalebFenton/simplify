.class public final Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$IsConnectingWorkaround;
.super Ljava/lang/Object;
.source "MediaRouterJellybeanMr1.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/routing/MediaRouterJellybeanMr1;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "IsConnectingWorkaround"
.end annotation


# instance fields
.field private mGetStatusCodeMethod:Ljava/lang/reflect/Method;

.field private mStatusConnecting:I


# direct methods
.method public constructor <init>()V
    .locals 4

    #@0
    .prologue
    .line 129
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 130
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@5
    const/16 v2, 0x11

    #@7
    if-eq v1, v2, :cond_0

    #@9
    .line 131
    new-instance v1, Ljava/lang/UnsupportedOperationException;

    #@b
    invoke-direct {v1}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@e
    throw v1

    #@f
    .line 135
    :cond_0
    :try_start_0
    const-class v1, Landroid/media/MediaRouter$RouteInfo;

    #@11
    const-string v2, "STATUS_CONNECTING"

    #@13
    invoke-virtual {v1, v2}, Ljava/lang/Class;->getField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    #@16
    move-result-object v0

    #@17
    .line 137
    .local v0, "statusConnectingField":Ljava/lang/reflect/Field;
    const/4 v1, 0x0

    #@18
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->getInt(Ljava/lang/Object;)I

    #@1b
    move-result v1

    #@1c
    iput v1, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$IsConnectingWorkaround;->mStatusConnecting:I

    #@1e
    .line 138
    const-class v1, Landroid/media/MediaRouter$RouteInfo;

    #@20
    const-string v2, "getStatusCode"

    #@22
    const/4 v3, 0x0

    #@23
    new-array v3, v3, [Ljava/lang/Class;

    #@25
    invoke-virtual {v1, v2, v3}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@28
    move-result-object v1

    #@29
    iput-object v1, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$IsConnectingWorkaround;->mGetStatusCodeMethod:Ljava/lang/reflect/Method;
    :try_end_0
    .catch Ljava/lang/NoSuchFieldException; {:try_start_0 .. :try_end_0} :catch_2
    .catch Ljava/lang/NoSuchMethodException; {:try_start_0 .. :try_end_0} :catch_1
    .catch Ljava/lang/IllegalAccessException; {:try_start_0 .. :try_end_0} :catch_0

    #@2b
    .line 144
    .end local v0    # "statusConnectingField":Ljava/lang/reflect/Field;
    :goto_0
    return-void

    #@2c
    .line 142
    :catch_0
    move-exception v1

    #@2d
    goto :goto_0

    #@2e
    .line 141
    :catch_1
    move-exception v1

    #@2f
    goto :goto_0

    #@30
    .line 140
    :catch_2
    move-exception v1

    #@31
    goto :goto_0
.end method


# virtual methods
.method public isConnecting(Ljava/lang/Object;)Z
    .locals 5
    .param p1, "routeObj"    # Ljava/lang/Object;

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 147
    move-object v0, p1

    #@2
    check-cast v0, Landroid/media/MediaRouter$RouteInfo;

    #@4
    .line 150
    .local v0, "route":Landroid/media/MediaRouter$RouteInfo;
    iget-object v2, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$IsConnectingWorkaround;->mGetStatusCodeMethod:Ljava/lang/reflect/Method;

    #@6
    if-eqz v2, :cond_1

    #@8
    .line 152
    :try_start_0
    iget-object v2, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$IsConnectingWorkaround;->mGetStatusCodeMethod:Ljava/lang/reflect/Method;

    #@a
    const/4 v4, 0x0

    #@b
    new-array v4, v4, [Ljava/lang/Object;

    #@d
    invoke-virtual {v2, v0, v4}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    #@10
    move-result-object v2

    #@11
    check-cast v2, Ljava/lang/Integer;

    #@13
    invoke-virtual {v2}, Ljava/lang/Integer;->intValue()I

    #@16
    move-result v1

    #@17
    .line 153
    .local v1, "statusCode":I
    iget v2, p0, Landroid/support/v4/media/routing/MediaRouterJellybeanMr1$IsConnectingWorkaround;->mStatusConnecting:I
    :try_end_0
    .catch Ljava/lang/IllegalAccessException; {:try_start_0 .. :try_end_0} :catch_1
    .catch Ljava/lang/reflect/InvocationTargetException; {:try_start_0 .. :try_end_0} :catch_0

    #@19
    if-ne v1, v2, :cond_0

    #@1b
    const/4 v2, 0x1

    #@1c
    .line 160
    .end local v1    # "statusCode":I
    :goto_0
    return v2

    #@1d
    .restart local v1    # "statusCode":I
    :cond_0
    move v2, v3

    #@1e
    .line 153
    goto :goto_0

    #@1f
    .line 155
    .end local v1    # "statusCode":I
    :catch_0
    move-exception v2

    #@20
    :cond_1
    :goto_1
    move v2, v3

    #@21
    .line 160
    goto :goto_0

    #@22
    .line 154
    :catch_1
    move-exception v2

    #@23
    goto :goto_1
.end method
