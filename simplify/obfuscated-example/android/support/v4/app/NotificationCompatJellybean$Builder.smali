.class public Landroid/support/v4/app/NotificationCompatJellybean$Builder;
.super Ljava/lang/Object;
.source "NotificationCompatJellybean.java"

# interfaces
.implements Landroid/support/v4/app/NotificationBuilderWithActions;
.implements Landroid/support/v4/app/NotificationBuilderWithBuilderAccessor;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/NotificationCompatJellybean;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "Builder"
.end annotation


# instance fields
.field private b:Landroid/app/Notification$Builder;

.field private mActionExtrasList:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List",
            "<",
            "Landroid/os/Bundle;",
            ">;"
        }
    .end annotation
.end field

.field private final mExtras:Landroid/os/Bundle;


# direct methods
.method public constructor <init>(Landroid/content/Context;Landroid/app/Notification;Ljava/lang/CharSequence;Ljava/lang/CharSequence;Ljava/lang/CharSequence;Landroid/widget/RemoteViews;ILandroid/app/PendingIntent;Landroid/app/PendingIntent;Landroid/graphics/Bitmap;IIZZILjava/lang/CharSequence;ZLandroid/os/Bundle;Ljava/lang/String;ZLjava/lang/String;)V
    .locals 8
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "n"    # Landroid/app/Notification;
    .param p3, "contentTitle"    # Ljava/lang/CharSequence;
    .param p4, "contentText"    # Ljava/lang/CharSequence;
    .param p5, "contentInfo"    # Ljava/lang/CharSequence;
    .param p6, "tickerView"    # Landroid/widget/RemoteViews;
    .param p7, "number"    # I
    .param p8, "contentIntent"    # Landroid/app/PendingIntent;
    .param p9, "fullScreenIntent"    # Landroid/app/PendingIntent;
    .param p10, "largeIcon"    # Landroid/graphics/Bitmap;
    .param p11, "progressMax"    # I
    .param p12, "progress"    # I
    .param p13, "progressIndeterminate"    # Z
    .param p14, "useChronometer"    # Z
    .param p15, "priority"    # I
    .param p16, "subText"    # Ljava/lang/CharSequence;
    .param p17, "localOnly"    # Z
    .param p18, "extras"    # Landroid/os/Bundle;
    .param p19, "groupKey"    # Ljava/lang/String;
    .param p20, "groupSummary"    # Z
    .param p21, "sortKey"    # Ljava/lang/String;

    #@0
    .prologue
    .line 76
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 68
    new-instance v4, Ljava/util/ArrayList;

    #@5
    invoke-direct {v4}, Ljava/util/ArrayList;-><init>()V

    #@8
    iput-object v4, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->mActionExtrasList:Ljava/util/List;

    #@a
    .line 77
    new-instance v4, Landroid/app/Notification$Builder;

    #@c
    invoke-direct {v4, p1}, Landroid/app/Notification$Builder;-><init>(Landroid/content/Context;)V

    #@f
    iget-wide v6, p2, Landroid/app/Notification;->when:J

    #@11
    invoke-virtual {v4, v6, v7}, Landroid/app/Notification$Builder;->setWhen(J)Landroid/app/Notification$Builder;

    #@14
    move-result-object v4

    #@15
    iget v5, p2, Landroid/app/Notification;->icon:I

    #@17
    iget v6, p2, Landroid/app/Notification;->iconLevel:I

    #@19
    invoke-virtual {v4, v5, v6}, Landroid/app/Notification$Builder;->setSmallIcon(II)Landroid/app/Notification$Builder;

    #@1c
    move-result-object v4

    #@1d
    iget-object v5, p2, Landroid/app/Notification;->contentView:Landroid/widget/RemoteViews;

    #@1f
    invoke-virtual {v4, v5}, Landroid/app/Notification$Builder;->setContent(Landroid/widget/RemoteViews;)Landroid/app/Notification$Builder;

    #@22
    move-result-object v4

    #@23
    iget-object v5, p2, Landroid/app/Notification;->tickerText:Ljava/lang/CharSequence;

    #@25
    invoke-virtual {v4, v5, p6}, Landroid/app/Notification$Builder;->setTicker(Ljava/lang/CharSequence;Landroid/widget/RemoteViews;)Landroid/app/Notification$Builder;

    #@28
    move-result-object v4

    #@29
    iget-object v5, p2, Landroid/app/Notification;->sound:Landroid/net/Uri;

    #@2b
    iget v6, p2, Landroid/app/Notification;->audioStreamType:I

    #@2d
    invoke-virtual {v4, v5, v6}, Landroid/app/Notification$Builder;->setSound(Landroid/net/Uri;I)Landroid/app/Notification$Builder;

    #@30
    move-result-object v4

    #@31
    iget-object v5, p2, Landroid/app/Notification;->vibrate:[J

    #@33
    invoke-virtual {v4, v5}, Landroid/app/Notification$Builder;->setVibrate([J)Landroid/app/Notification$Builder;

    #@36
    move-result-object v4

    #@37
    iget v5, p2, Landroid/app/Notification;->ledARGB:I

    #@39
    iget v6, p2, Landroid/app/Notification;->ledOnMS:I

    #@3b
    iget v7, p2, Landroid/app/Notification;->ledOffMS:I

    #@3d
    invoke-virtual {v4, v5, v6, v7}, Landroid/app/Notification$Builder;->setLights(III)Landroid/app/Notification$Builder;

    #@40
    move-result-object v5

    #@41
    iget v4, p2, Landroid/app/Notification;->flags:I

    #@43
    and-int/lit8 v4, v4, 0x2

    #@45
    if-eqz v4, :cond_4

    #@47
    const/4 v4, 0x1

    #@48
    :goto_0
    invoke-virtual {v5, v4}, Landroid/app/Notification$Builder;->setOngoing(Z)Landroid/app/Notification$Builder;

    #@4b
    move-result-object v5

    #@4c
    iget v4, p2, Landroid/app/Notification;->flags:I

    #@4e
    and-int/lit8 v4, v4, 0x8

    #@50
    if-eqz v4, :cond_5

    #@52
    const/4 v4, 0x1

    #@53
    :goto_1
    invoke-virtual {v5, v4}, Landroid/app/Notification$Builder;->setOnlyAlertOnce(Z)Landroid/app/Notification$Builder;

    #@56
    move-result-object v5

    #@57
    iget v4, p2, Landroid/app/Notification;->flags:I

    #@59
    and-int/lit8 v4, v4, 0x10

    #@5b
    if-eqz v4, :cond_6

    #@5d
    const/4 v4, 0x1

    #@5e
    :goto_2
    invoke-virtual {v5, v4}, Landroid/app/Notification$Builder;->setAutoCancel(Z)Landroid/app/Notification$Builder;

    #@61
    move-result-object v4

    #@62
    iget v5, p2, Landroid/app/Notification;->defaults:I

    #@64
    invoke-virtual {v4, v5}, Landroid/app/Notification$Builder;->setDefaults(I)Landroid/app/Notification$Builder;

    #@67
    move-result-object v4

    #@68
    invoke-virtual {v4, p3}, Landroid/app/Notification$Builder;->setContentTitle(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@6b
    move-result-object v4

    #@6c
    invoke-virtual {v4, p4}, Landroid/app/Notification$Builder;->setContentText(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@6f
    move-result-object v4

    #@70
    move-object/from16 v0, p16

    #@72
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setSubText(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@75
    move-result-object v4

    #@76
    invoke-virtual {v4, p5}, Landroid/app/Notification$Builder;->setContentInfo(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@79
    move-result-object v4

    #@7a
    move-object/from16 v0, p8

    #@7c
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setContentIntent(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;

    #@7f
    move-result-object v4

    #@80
    iget-object v5, p2, Landroid/app/Notification;->deleteIntent:Landroid/app/PendingIntent;

    #@82
    invoke-virtual {v4, v5}, Landroid/app/Notification$Builder;->setDeleteIntent(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;

    #@85
    move-result-object v5

    #@86
    iget v4, p2, Landroid/app/Notification;->flags:I

    #@88
    and-int/lit16 v4, v4, 0x80

    #@8a
    if-eqz v4, :cond_7

    #@8c
    const/4 v4, 0x1

    #@8d
    :goto_3
    move-object/from16 v0, p9

    #@8f
    invoke-virtual {v5, v0, v4}, Landroid/app/Notification$Builder;->setFullScreenIntent(Landroid/app/PendingIntent;Z)Landroid/app/Notification$Builder;

    #@92
    move-result-object v4

    #@93
    move-object/from16 v0, p10

    #@95
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setLargeIcon(Landroid/graphics/Bitmap;)Landroid/app/Notification$Builder;

    #@98
    move-result-object v4

    #@99
    invoke-virtual {v4, p7}, Landroid/app/Notification$Builder;->setNumber(I)Landroid/app/Notification$Builder;

    #@9c
    move-result-object v4

    #@9d
    move/from16 v0, p14

    #@9f
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setUsesChronometer(Z)Landroid/app/Notification$Builder;

    #@a2
    move-result-object v4

    #@a3
    move/from16 v0, p15

    #@a5
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setPriority(I)Landroid/app/Notification$Builder;

    #@a8
    move-result-object v4

    #@a9
    move/from16 v0, p11

    #@ab
    move/from16 v1, p12

    #@ad
    move/from16 v2, p13

    #@af
    invoke-virtual {v4, v0, v1, v2}, Landroid/app/Notification$Builder;->setProgress(IIZ)Landroid/app/Notification$Builder;

    #@b2
    move-result-object v4

    #@b3
    iput-object v4, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->b:Landroid/app/Notification$Builder;

    #@b5
    .line 102
    new-instance v4, Landroid/os/Bundle;

    #@b7
    invoke-direct {v4}, Landroid/os/Bundle;-><init>()V

    #@ba
    iput-object v4, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->mExtras:Landroid/os/Bundle;

    #@bc
    .line 103
    if-eqz p18, :cond_0

    #@be
    .line 104
    iget-object v4, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->mExtras:Landroid/os/Bundle;

    #@c0
    move-object/from16 v0, p18

    #@c2
    invoke-virtual {v4, v0}, Landroid/os/Bundle;->putAll(Landroid/os/Bundle;)V

    #@c5
    .line 106
    :cond_0
    if-eqz p17, :cond_1

    #@c7
    .line 107
    iget-object v4, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->mExtras:Landroid/os/Bundle;

    #@c9
    const-string v5, "android.support.localOnly"

    #@cb
    const/4 v6, 0x1

    #@cc
    invoke-virtual {v4, v5, v6}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    #@cf
    .line 109
    :cond_1
    if-eqz p19, :cond_2

    #@d1
    .line 110
    iget-object v4, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->mExtras:Landroid/os/Bundle;

    #@d3
    const-string v5, "android.support.groupKey"

    #@d5
    move-object/from16 v0, p19

    #@d7
    invoke-virtual {v4, v5, v0}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    #@da
    .line 111
    if-eqz p20, :cond_8

    #@dc
    .line 112
    iget-object v4, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->mExtras:Landroid/os/Bundle;

    #@de
    const-string v5, "android.support.isGroupSummary"

    #@e0
    const/4 v6, 0x1

    #@e1
    invoke-virtual {v4, v5, v6}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    #@e4
    .line 117
    :cond_2
    :goto_4
    if-eqz p21, :cond_3

    #@e6
    .line 118
    iget-object v4, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->mExtras:Landroid/os/Bundle;

    #@e8
    const-string v5, "android.support.sortKey"

    #@ea
    move-object/from16 v0, p21

    #@ec
    invoke-virtual {v4, v5, v0}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    #@ef
    .line 120
    :cond_3
    return-void

    #@f0
    .line 77
    :cond_4
    const/4 v4, 0x0

    #@f1
    goto/16 :goto_0

    #@f3
    :cond_5
    const/4 v4, 0x0

    #@f4
    goto/16 :goto_1

    #@f6
    :cond_6
    const/4 v4, 0x0

    #@f7
    goto/16 :goto_2

    #@f9
    :cond_7
    const/4 v4, 0x0

    #@fa
    goto :goto_3

    #@fb
    .line 114
    :cond_8
    iget-object v4, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->mExtras:Landroid/os/Bundle;

    #@fd
    const-string v5, "android.support.useSideChannel"

    #@ff
    const/4 v6, 0x1

    #@100
    invoke-virtual {v4, v5, v6}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    #@103
    goto :goto_4
.end method


# virtual methods
.method public addAction(Landroid/support/v4/app/NotificationCompatBase$Action;)V
    .locals 2
    .param p1, "action"    # Landroid/support/v4/app/NotificationCompatBase$Action;

    #@0
    .prologue
    .line 124
    iget-object v0, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->mActionExtrasList:Ljava/util/List;

    #@2
    iget-object v1, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->b:Landroid/app/Notification$Builder;

    #@4
    invoke-static {v1, p1}, Landroid/support/v4/app/NotificationCompatJellybean;->writeActionAndGetExtras(Landroid/app/Notification$Builder;Landroid/support/v4/app/NotificationCompatBase$Action;)Landroid/os/Bundle;

    #@7
    move-result-object v1

    #@8
    invoke-interface {v0, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    #@b
    .line 125
    return-void
.end method

.method public build()Landroid/app/Notification;
    .locals 8

    #@0
    .prologue
    .line 133
    iget-object v6, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->b:Landroid/app/Notification$Builder;

    #@2
    invoke-virtual {v6}, Landroid/app/Notification$Builder;->build()Landroid/app/Notification;

    #@5
    move-result-object v5

    #@6
    .line 136
    .local v5, "notif":Landroid/app/Notification;
    invoke-static {v5}, Landroid/support/v4/app/NotificationCompatJellybean;->getExtras(Landroid/app/Notification;)Landroid/os/Bundle;

    #@9
    move-result-object v1

    #@a
    .line 137
    .local v1, "extras":Landroid/os/Bundle;
    new-instance v4, Landroid/os/Bundle;

    #@c
    iget-object v6, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->mExtras:Landroid/os/Bundle;

    #@e
    invoke-direct {v4, v6}, Landroid/os/Bundle;-><init>(Landroid/os/Bundle;)V

    #@11
    .line 138
    .local v4, "mergeBundle":Landroid/os/Bundle;
    iget-object v6, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->mExtras:Landroid/os/Bundle;

    #@13
    invoke-virtual {v6}, Landroid/os/Bundle;->keySet()Ljava/util/Set;

    #@16
    move-result-object v6

    #@17
    invoke-interface {v6}, Ljava/util/Set;->iterator()Ljava/util/Iterator;

    #@1a
    move-result-object v2

    #@1b
    .local v2, "i$":Ljava/util/Iterator;
    :cond_0
    :goto_0
    invoke-interface {v2}, Ljava/util/Iterator;->hasNext()Z

    #@1e
    move-result v6

    #@1f
    if-eqz v6, :cond_1

    #@21
    invoke-interface {v2}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@24
    move-result-object v3

    #@25
    check-cast v3, Ljava/lang/String;

    #@27
    .line 139
    .local v3, "key":Ljava/lang/String;
    invoke-virtual {v1, v3}, Landroid/os/Bundle;->containsKey(Ljava/lang/String;)Z

    #@2a
    move-result v6

    #@2b
    if-eqz v6, :cond_0

    #@2d
    .line 140
    invoke-virtual {v4, v3}, Landroid/os/Bundle;->remove(Ljava/lang/String;)V

    #@30
    goto :goto_0

    #@31
    .line 143
    .end local v3    # "key":Ljava/lang/String;
    :cond_1
    invoke-virtual {v1, v4}, Landroid/os/Bundle;->putAll(Landroid/os/Bundle;)V

    #@34
    .line 144
    iget-object v6, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->mActionExtrasList:Ljava/util/List;

    #@36
    invoke-static {v6}, Landroid/support/v4/app/NotificationCompatJellybean;->buildActionExtrasMap(Ljava/util/List;)Landroid/util/SparseArray;

    #@39
    move-result-object v0

    #@3a
    .line 145
    .local v0, "actionExtrasMap":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Bundle;>;"
    if-eqz v0, :cond_2

    #@3c
    .line 147
    invoke-static {v5}, Landroid/support/v4/app/NotificationCompatJellybean;->getExtras(Landroid/app/Notification;)Landroid/os/Bundle;

    #@3f
    move-result-object v6

    #@40
    const-string v7, "android.support.actionExtras"

    #@42
    invoke-virtual {v6, v7, v0}, Landroid/os/Bundle;->putSparseParcelableArray(Ljava/lang/String;Landroid/util/SparseArray;)V

    #@45
    .line 149
    :cond_2
    return-object v5
.end method

.method public getBuilder()Landroid/app/Notification$Builder;
    .locals 1

    #@0
    .prologue
    .line 129
    iget-object v0, p0, Landroid/support/v4/app/NotificationCompatJellybean$Builder;->b:Landroid/app/Notification$Builder;

    #@2
    return-object v0
.end method
