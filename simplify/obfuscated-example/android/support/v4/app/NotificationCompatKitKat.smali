.class Landroid/support/v4/app/NotificationCompatKitKat;
.super Ljava/lang/Object;
.source "NotificationCompatKitKat.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/NotificationCompatKitKat$Builder;
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 30
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 31
    return-void
.end method

.method public static getAction(Landroid/app/Notification;ILandroid/support/v4/app/NotificationCompatBase$Action$Factory;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)Landroid/support/v4/app/NotificationCompatBase$Action;
    .locals 8
    .param p0, "notif"    # Landroid/app/Notification;
    .param p1, "actionIndex"    # I
    .param p2, "factory"    # Landroid/support/v4/app/NotificationCompatBase$Action$Factory;
    .param p3, "remoteInputFactory"    # Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;

    #@0
    .prologue
    .line 129
    iget-object v0, p0, Landroid/app/Notification;->actions:[Landroid/app/Notification$Action;

    #@2
    aget-object v6, v0, p1

    #@4
    .line 130
    .local v6, "action":Landroid/app/Notification$Action;
    const/4 v5, 0x0

    #@5
    .line 131
    .local v5, "actionExtras":Landroid/os/Bundle;
    iget-object v0, p0, Landroid/app/Notification;->extras:Landroid/os/Bundle;

    #@7
    const-string v1, "android.support.actionExtras"

    #@9
    invoke-virtual {v0, v1}, Landroid/os/Bundle;->getSparseParcelableArray(Ljava/lang/String;)Landroid/util/SparseArray;

    #@c
    move-result-object v7

    #@d
    .line 133
    .local v7, "actionExtrasMap":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Bundle;>;"
    if-eqz v7, :cond_0

    #@f
    .line 134
    invoke-virtual {v7, p1}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    #@12
    move-result-object v5

    #@13
    .end local v5    # "actionExtras":Landroid/os/Bundle;
    check-cast v5, Landroid/os/Bundle;

    #@15
    .line 136
    .restart local v5    # "actionExtras":Landroid/os/Bundle;
    :cond_0
    iget v2, v6, Landroid/app/Notification$Action;->icon:I

    #@17
    iget-object v3, v6, Landroid/app/Notification$Action;->title:Ljava/lang/CharSequence;

    #@19
    iget-object v4, v6, Landroid/app/Notification$Action;->actionIntent:Landroid/app/PendingIntent;

    #@1b
    move-object v0, p2

    #@1c
    move-object v1, p3

    #@1d
    invoke-static/range {v0 .. v5}, Landroid/support/v4/app/NotificationCompatJellybean;->readAction(Landroid/support/v4/app/NotificationCompatBase$Action$Factory;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;ILjava/lang/CharSequence;Landroid/app/PendingIntent;Landroid/os/Bundle;)Landroid/support/v4/app/NotificationCompatBase$Action;

    #@20
    move-result-object v0

    #@21
    return-object v0
.end method

.method public static getActionCount(Landroid/app/Notification;)I
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 123
    iget-object v0, p0, Landroid/app/Notification;->actions:[Landroid/app/Notification$Action;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/app/Notification;->actions:[Landroid/app/Notification$Action;

    #@6
    array-length v0, v0

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method public static getExtras(Landroid/app/Notification;)Landroid/os/Bundle;
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 119
    iget-object v0, p0, Landroid/app/Notification;->extras:Landroid/os/Bundle;

    #@2
    return-object v0
.end method

.method public static getGroup(Landroid/app/Notification;)Ljava/lang/String;
    .locals 2
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 145
    iget-object v0, p0, Landroid/app/Notification;->extras:Landroid/os/Bundle;

    #@2
    const-string v1, "android.support.groupKey"

    #@4
    invoke-virtual {v0, v1}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public static getLocalOnly(Landroid/app/Notification;)Z
    .locals 2
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 141
    iget-object v0, p0, Landroid/app/Notification;->extras:Landroid/os/Bundle;

    #@2
    const-string v1, "android.support.localOnly"

    #@4
    invoke-virtual {v0, v1}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public static getSortKey(Landroid/app/Notification;)Ljava/lang/String;
    .locals 2
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 153
    iget-object v0, p0, Landroid/app/Notification;->extras:Landroid/os/Bundle;

    #@2
    const-string v1, "android.support.sortKey"

    #@4
    invoke-virtual {v0, v1}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public static isGroupSummary(Landroid/app/Notification;)Z
    .locals 2
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 149
    iget-object v0, p0, Landroid/app/Notification;->extras:Landroid/os/Bundle;

    #@2
    const-string v1, "android.support.isGroupSummary"

    #@4
    invoke-virtual {v0, v1}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method
