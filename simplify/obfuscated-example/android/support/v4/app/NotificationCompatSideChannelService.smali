.class public abstract Landroid/support/v4/app/NotificationCompatSideChannelService;
.super Landroid/app/Service;
.source "NotificationCompatSideChannelService.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/NotificationCompatSideChannelService$1;,
        Landroid/support/v4/app/NotificationCompatSideChannelService$NotificationSideChannelStub;
    }
.end annotation


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 44
    invoke-direct {p0}, Landroid/app/Service;-><init>()V

    #@3
    .line 73
    return-void
.end method

.method static synthetic access$100(Landroid/support/v4/app/NotificationCompatSideChannelService;ILjava/lang/String;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/app/NotificationCompatSideChannelService;
    .param p1, "x1"    # I
    .param p2, "x2"    # Ljava/lang/String;

    #@0
    .prologue
    .line 44
    invoke-direct {p0, p1, p2}, Landroid/support/v4/app/NotificationCompatSideChannelService;->checkPermission(ILjava/lang/String;)V

    #@3
    return-void
.end method

.method private checkPermission(ILjava/lang/String;)V
    .locals 7
    .param p1, "callingUid"    # I
    .param p2, "packageName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 110
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatSideChannelService;->getPackageManager()Landroid/content/pm/PackageManager;

    #@3
    move-result-object v4

    #@4
    invoke-virtual {v4, p1}, Landroid/content/pm/PackageManager;->getPackagesForUid(I)[Ljava/lang/String;

    #@7
    move-result-object v0

    #@8
    .local v0, "arr$":[Ljava/lang/String;
    array-length v2, v0

    #@9
    .local v2, "len$":I
    const/4 v1, 0x0

    #@a
    .local v1, "i$":I
    :goto_0
    if-ge v1, v2, :cond_1

    #@c
    aget-object v3, v0, v1

    #@e
    .line 111
    .local v3, "validPackage":Ljava/lang/String;
    invoke-virtual {v3, p2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@11
    move-result v4

    #@12
    if-eqz v4, :cond_0

    #@14
    .line 112
    return-void

    #@15
    .line 110
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@17
    goto :goto_0

    #@18
    .line 115
    .end local v3    # "validPackage":Ljava/lang/String;
    :cond_1
    new-instance v4, Ljava/lang/SecurityException;

    #@1a
    new-instance v5, Ljava/lang/StringBuilder;

    #@1c
    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    #@1f
    const-string v6, "NotificationSideChannelService: Uid "

    #@21
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@24
    move-result-object v5

    #@25
    invoke-virtual {v5, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@28
    move-result-object v5

    #@29
    const-string v6, " is not authorized for package "

    #@2b
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2e
    move-result-object v5

    #@2f
    invoke-virtual {v5, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@32
    move-result-object v5

    #@33
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@36
    move-result-object v5

    #@37
    invoke-direct {v4, v5}, Ljava/lang/SecurityException;-><init>(Ljava/lang/String;)V

    #@3a
    throw v4
.end method


# virtual methods
.method public abstract cancel(Ljava/lang/String;ILjava/lang/String;)V
.end method

.method public abstract cancelAll(Ljava/lang/String;)V
.end method

.method public abstract notify(Ljava/lang/String;ILjava/lang/String;Landroid/app/Notification;)V
.end method

.method public onBind(Landroid/content/Intent;)Landroid/os/IBinder;
    .locals 3
    .param p1, "intent"    # Landroid/content/Intent;

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 47
    invoke-virtual {p1}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    #@4
    move-result-object v1

    #@5
    const-string v2, "android.support.BIND_NOTIFICATION_SIDE_CHANNEL"

    #@7
    invoke-virtual {v1, v2}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@a
    move-result v1

    #@b
    if-eqz v1, :cond_0

    #@d
    .line 50
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@f
    const/16 v2, 0x13

    #@11
    if-le v1, v2, :cond_1

    #@13
    .line 55
    :cond_0
    :goto_0
    return-object v0

    #@14
    .line 53
    :cond_1
    new-instance v1, Landroid/support/v4/app/NotificationCompatSideChannelService$NotificationSideChannelStub;

    #@16
    invoke-direct {v1, p0, v0}, Landroid/support/v4/app/NotificationCompatSideChannelService$NotificationSideChannelStub;-><init>(Landroid/support/v4/app/NotificationCompatSideChannelService;Landroid/support/v4/app/NotificationCompatSideChannelService$1;)V

    #@19
    move-object v0, v1

    #@1a
    goto :goto_0
.end method
