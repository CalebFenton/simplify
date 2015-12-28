.class Landroid/support/v4/app/NotificationCompatApi20;
.super Ljava/lang/Object;
.source "NotificationCompatApi20.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/NotificationCompatApi20$Builder;
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

.method public static addAction(Landroid/app/Notification$Builder;Landroid/support/v4/app/NotificationCompatBase$Action;)V
    .locals 8
    .param p0, "b"    # Landroid/app/Notification$Builder;
    .param p1, "action"    # Landroid/support/v4/app/NotificationCompatBase$Action;

    #@0
    .prologue
    .line 101
    new-instance v0, Landroid/app/Notification$Action$Builder;

    #@2
    invoke-virtual {p1}, Landroid/support/v4/app/NotificationCompatBase$Action;->getIcon()I

    #@5
    move-result v5

    #@6
    invoke-virtual {p1}, Landroid/support/v4/app/NotificationCompatBase$Action;->getTitle()Ljava/lang/CharSequence;

    #@9
    move-result-object v6

    #@a
    invoke-virtual {p1}, Landroid/support/v4/app/NotificationCompatBase$Action;->getActionIntent()Landroid/app/PendingIntent;

    #@d
    move-result-object v7

    #@e
    invoke-direct {v0, v5, v6, v7}, Landroid/app/Notification$Action$Builder;-><init>(ILjava/lang/CharSequence;Landroid/app/PendingIntent;)V

    #@11
    .line 103
    .local v0, "actionBuilder":Landroid/app/Notification$Action$Builder;
    invoke-virtual {p1}, Landroid/support/v4/app/NotificationCompatBase$Action;->getRemoteInputs()[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@14
    move-result-object v5

    #@15
    if-eqz v5, :cond_0

    #@17
    .line 104
    invoke-virtual {p1}, Landroid/support/v4/app/NotificationCompatBase$Action;->getRemoteInputs()[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@1a
    move-result-object v5

    #@1b
    invoke-static {v5}, Landroid/support/v4/app/RemoteInputCompatApi20;->fromCompat([Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;)[Landroid/app/RemoteInput;

    #@1e
    move-result-object v1

    #@1f
    .local v1, "arr$":[Landroid/app/RemoteInput;
    array-length v3, v1

    #@20
    .local v3, "len$":I
    const/4 v2, 0x0

    #@21
    .local v2, "i$":I
    :goto_0
    if-ge v2, v3, :cond_0

    #@23
    aget-object v4, v1, v2

    #@25
    .line 106
    .local v4, "remoteInput":Landroid/app/RemoteInput;
    invoke-virtual {v0, v4}, Landroid/app/Notification$Action$Builder;->addRemoteInput(Landroid/app/RemoteInput;)Landroid/app/Notification$Action$Builder;

    #@28
    .line 104
    add-int/lit8 v2, v2, 0x1

    #@2a
    goto :goto_0

    #@2b
    .line 109
    .end local v1    # "arr$":[Landroid/app/RemoteInput;
    .end local v2    # "i$":I
    .end local v3    # "len$":I
    .end local v4    # "remoteInput":Landroid/app/RemoteInput;
    :cond_0
    invoke-virtual {p1}, Landroid/support/v4/app/NotificationCompatBase$Action;->getExtras()Landroid/os/Bundle;

    #@2e
    move-result-object v5

    #@2f
    if-eqz v5, :cond_1

    #@31
    .line 110
    invoke-virtual {p1}, Landroid/support/v4/app/NotificationCompatBase$Action;->getExtras()Landroid/os/Bundle;

    #@34
    move-result-object v5

    #@35
    invoke-virtual {v0, v5}, Landroid/app/Notification$Action$Builder;->addExtras(Landroid/os/Bundle;)Landroid/app/Notification$Action$Builder;

    #@38
    .line 112
    :cond_1
    invoke-virtual {v0}, Landroid/app/Notification$Action$Builder;->build()Landroid/app/Notification$Action;

    #@3b
    move-result-object v5

    #@3c
    invoke-virtual {p0, v5}, Landroid/app/Notification$Builder;->addAction(Landroid/app/Notification$Action;)Landroid/app/Notification$Builder;

    #@3f
    .line 113
    return-void
.end method

.method public static getAction(Landroid/app/Notification;ILandroid/support/v4/app/NotificationCompatBase$Action$Factory;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)Landroid/support/v4/app/NotificationCompatBase$Action;
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;
    .param p1, "actionIndex"    # I
    .param p2, "actionFactory"    # Landroid/support/v4/app/NotificationCompatBase$Action$Factory;
    .param p3, "remoteInputFactory"    # Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;

    #@0
    .prologue
    .line 118
    iget-object v0, p0, Landroid/app/Notification;->actions:[Landroid/app/Notification$Action;

    #@2
    aget-object v0, v0, p1

    #@4
    invoke-static {v0, p2, p3}, Landroid/support/v4/app/NotificationCompatApi20;->getActionCompatFromAction(Landroid/app/Notification$Action;Landroid/support/v4/app/NotificationCompatBase$Action$Factory;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)Landroid/support/v4/app/NotificationCompatBase$Action;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method private static getActionCompatFromAction(Landroid/app/Notification$Action;Landroid/support/v4/app/NotificationCompatBase$Action$Factory;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)Landroid/support/v4/app/NotificationCompatBase$Action;
    .locals 6
    .param p0, "action"    # Landroid/app/Notification$Action;
    .param p1, "actionFactory"    # Landroid/support/v4/app/NotificationCompatBase$Action$Factory;
    .param p2, "remoteInputFactory"    # Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;

    #@0
    .prologue
    .line 124
    invoke-virtual {p0}, Landroid/app/Notification$Action;->getRemoteInputs()[Landroid/app/RemoteInput;

    #@3
    move-result-object v0

    #@4
    invoke-static {v0, p2}, Landroid/support/v4/app/RemoteInputCompatApi20;->toCompat([Landroid/app/RemoteInput;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@7
    move-result-object v5

    #@8
    .line 126
    .local v5, "remoteInputs":[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    iget v1, p0, Landroid/app/Notification$Action;->icon:I

    #@a
    iget-object v2, p0, Landroid/app/Notification$Action;->title:Ljava/lang/CharSequence;

    #@c
    iget-object v3, p0, Landroid/app/Notification$Action;->actionIntent:Landroid/app/PendingIntent;

    #@e
    invoke-virtual {p0}, Landroid/app/Notification$Action;->getExtras()Landroid/os/Bundle;

    #@11
    move-result-object v4

    #@12
    move-object v0, p1

    #@13
    invoke-interface/range {v0 .. v5}, Landroid/support/v4/app/NotificationCompatBase$Action$Factory;->build(ILjava/lang/CharSequence;Landroid/app/PendingIntent;Landroid/os/Bundle;[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;)Landroid/support/v4/app/NotificationCompatBase$Action;

    #@16
    move-result-object v0

    #@17
    return-object v0
.end method

.method private static getActionFromActionCompat(Landroid/support/v4/app/NotificationCompatBase$Action;)Landroid/app/Notification$Action;
    .locals 11
    .param p0, "actionCompat"    # Landroid/support/v4/app/NotificationCompatBase$Action;

    #@0
    .prologue
    .line 132
    new-instance v7, Landroid/app/Notification$Action$Builder;

    #@2
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$Action;->getIcon()I

    #@5
    move-result v8

    #@6
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$Action;->getTitle()Ljava/lang/CharSequence;

    #@9
    move-result-object v9

    #@a
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$Action;->getActionIntent()Landroid/app/PendingIntent;

    #@d
    move-result-object v10

    #@e
    invoke-direct {v7, v8, v9, v10}, Landroid/app/Notification$Action$Builder;-><init>(ILjava/lang/CharSequence;Landroid/app/PendingIntent;)V

    #@11
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$Action;->getExtras()Landroid/os/Bundle;

    #@14
    move-result-object v8

    #@15
    invoke-virtual {v7, v8}, Landroid/app/Notification$Action$Builder;->addExtras(Landroid/os/Bundle;)Landroid/app/Notification$Action$Builder;

    #@18
    move-result-object v0

    #@19
    .line 135
    .local v0, "actionBuilder":Landroid/app/Notification$Action$Builder;
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$Action;->getRemoteInputs()[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@1c
    move-result-object v5

    #@1d
    .line 136
    .local v5, "remoteInputCompats":[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    if-eqz v5, :cond_0

    #@1f
    .line 137
    invoke-static {v5}, Landroid/support/v4/app/RemoteInputCompatApi20;->fromCompat([Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;)[Landroid/app/RemoteInput;

    #@22
    move-result-object v6

    #@23
    .line 138
    .local v6, "remoteInputs":[Landroid/app/RemoteInput;
    move-object v1, v6

    #@24
    .local v1, "arr$":[Landroid/app/RemoteInput;
    array-length v3, v1

    #@25
    .local v3, "len$":I
    const/4 v2, 0x0

    #@26
    .local v2, "i$":I
    :goto_0
    if-ge v2, v3, :cond_0

    #@28
    aget-object v4, v1, v2

    #@2a
    .line 139
    .local v4, "remoteInput":Landroid/app/RemoteInput;
    invoke-virtual {v0, v4}, Landroid/app/Notification$Action$Builder;->addRemoteInput(Landroid/app/RemoteInput;)Landroid/app/Notification$Action$Builder;

    #@2d
    .line 138
    add-int/lit8 v2, v2, 0x1

    #@2f
    goto :goto_0

    #@30
    .line 142
    .end local v1    # "arr$":[Landroid/app/RemoteInput;
    .end local v2    # "i$":I
    .end local v3    # "len$":I
    .end local v4    # "remoteInput":Landroid/app/RemoteInput;
    .end local v6    # "remoteInputs":[Landroid/app/RemoteInput;
    :cond_0
    invoke-virtual {v0}, Landroid/app/Notification$Action$Builder;->build()Landroid/app/Notification$Action;

    #@33
    move-result-object v7

    #@34
    return-object v7
.end method

.method public static getActionsFromParcelableArrayList(Ljava/util/ArrayList;Landroid/support/v4/app/NotificationCompatBase$Action$Factory;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)[Landroid/support/v4/app/NotificationCompatBase$Action;
    .locals 4
    .param p1, "actionFactory"    # Landroid/support/v4/app/NotificationCompatBase$Action$Factory;
    .param p2, "remoteInputFactory"    # Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/os/Parcelable;",
            ">;",
            "Landroid/support/v4/app/NotificationCompatBase$Action$Factory;",
            "Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;",
            ")[",
            "Landroid/support/v4/app/NotificationCompatBase$Action;"
        }
    .end annotation

    #@0
    .prologue
    .line 155
    .local p0, "parcelables":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/os/Parcelable;>;"
    if-nez p0, :cond_1

    #@2
    .line 156
    const/4 v1, 0x0

    #@3
    .line 163
    :cond_0
    return-object v1

    #@4
    .line 158
    :cond_1
    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    #@7
    move-result v3

    #@8
    invoke-interface {p1, v3}, Landroid/support/v4/app/NotificationCompatBase$Action$Factory;->newArray(I)[Landroid/support/v4/app/NotificationCompatBase$Action;

    #@b
    move-result-object v1

    #@c
    .line 159
    .local v1, "actions":[Landroid/support/v4/app/NotificationCompatBase$Action;
    const/4 v2, 0x0

    #@d
    .local v2, "i":I
    :goto_0
    array-length v3, v1

    #@e
    if-ge v2, v3, :cond_0

    #@10
    .line 160
    invoke-virtual {p0, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@13
    move-result-object v0

    #@14
    check-cast v0, Landroid/app/Notification$Action;

    #@16
    .line 161
    .local v0, "action":Landroid/app/Notification$Action;
    invoke-static {v0, p1, p2}, Landroid/support/v4/app/NotificationCompatApi20;->getActionCompatFromAction(Landroid/app/Notification$Action;Landroid/support/v4/app/NotificationCompatBase$Action$Factory;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)Landroid/support/v4/app/NotificationCompatBase$Action;

    #@19
    move-result-object v3

    #@1a
    aput-object v3, v1, v2

    #@1c
    .line 159
    add-int/lit8 v2, v2, 0x1

    #@1e
    goto :goto_0
.end method

.method public static getGroup(Landroid/app/Notification;)Ljava/lang/String;
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 188
    invoke-virtual {p0}, Landroid/app/Notification;->getGroup()Ljava/lang/String;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static getLocalOnly(Landroid/app/Notification;)Z
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 184
    iget v0, p0, Landroid/app/Notification;->flags:I

    #@2
    and-int/lit16 v0, v0, 0x100

    #@4
    if-eqz v0, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method public static getParcelableArrayListForActions([Landroid/support/v4/app/NotificationCompatBase$Action;)Ljava/util/ArrayList;
    .locals 6
    .param p0, "actions"    # [Landroid/support/v4/app/NotificationCompatBase$Action;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "([",
            "Landroid/support/v4/app/NotificationCompatBase$Action;",
            ")",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/os/Parcelable;",
            ">;"
        }
    .end annotation

    #@0
    .prologue
    .line 173
    if-nez p0, :cond_1

    #@2
    .line 174
    const/4 v4, 0x0

    #@3
    .line 180
    :cond_0
    return-object v4

    #@4
    .line 176
    :cond_1
    new-instance v4, Ljava/util/ArrayList;

    #@6
    array-length v5, p0

    #@7
    invoke-direct {v4, v5}, Ljava/util/ArrayList;-><init>(I)V

    #@a
    .line 177
    .local v4, "parcelables":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/os/Parcelable;>;"
    move-object v1, p0

    #@b
    .local v1, "arr$":[Landroid/support/v4/app/NotificationCompatBase$Action;
    array-length v3, v1

    #@c
    .local v3, "len$":I
    const/4 v2, 0x0

    #@d
    .local v2, "i$":I
    :goto_0
    if-ge v2, v3, :cond_0

    #@f
    aget-object v0, v1, v2

    #@11
    .line 178
    .local v0, "action":Landroid/support/v4/app/NotificationCompatBase$Action;
    invoke-static {v0}, Landroid/support/v4/app/NotificationCompatApi20;->getActionFromActionCompat(Landroid/support/v4/app/NotificationCompatBase$Action;)Landroid/app/Notification$Action;

    #@14
    move-result-object v5

    #@15
    invoke-virtual {v4, v5}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@18
    .line 177
    add-int/lit8 v2, v2, 0x1

    #@1a
    goto :goto_0
.end method

.method public static getSortKey(Landroid/app/Notification;)Ljava/lang/String;
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 196
    invoke-virtual {p0}, Landroid/app/Notification;->getSortKey()Ljava/lang/String;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static isGroupSummary(Landroid/app/Notification;)Z
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 192
    iget v0, p0, Landroid/app/Notification;->flags:I

    #@2
    and-int/lit16 v0, v0, 0x200

    #@4
    if-eqz v0, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method
