.class public Landroid/support/v4/app/NotificationCompatApi20$Builder;
.super Ljava/lang/Object;
.source "NotificationCompatApi20.java"

# interfaces
.implements Landroid/support/v4/app/NotificationBuilderWithActions;
.implements Landroid/support/v4/app/NotificationBuilderWithBuilderAccessor;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/NotificationCompatApi20;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "Builder"
.end annotation


# instance fields
.field private b:Landroid/app/Notification$Builder;

.field private mExtras:Landroid/os/Bundle;


# direct methods
.method public constructor <init>(Landroid/content/Context;Landroid/app/Notification;Ljava/lang/CharSequence;Ljava/lang/CharSequence;Ljava/lang/CharSequence;Landroid/widget/RemoteViews;ILandroid/app/PendingIntent;Landroid/app/PendingIntent;Landroid/graphics/Bitmap;IIZZZILjava/lang/CharSequence;ZLjava/util/ArrayList;Landroid/os/Bundle;Ljava/lang/String;ZLjava/lang/String;)V
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
    .param p14, "showWhen"    # Z
    .param p15, "useChronometer"    # Z
    .param p16, "priority"    # I
    .param p17, "subText"    # Ljava/lang/CharSequence;
    .param p18, "localOnly"    # Z
    .param p20, "extras"    # Landroid/os/Bundle;
    .param p21, "groupKey"    # Ljava/lang/String;
    .param p22, "groupSummary"    # Z
    .param p23, "sortKey"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/content/Context;",
            "Landroid/app/Notification;",
            "Ljava/lang/CharSequence;",
            "Ljava/lang/CharSequence;",
            "Ljava/lang/CharSequence;",
            "Landroid/widget/RemoteViews;",
            "I",
            "Landroid/app/PendingIntent;",
            "Landroid/app/PendingIntent;",
            "Landroid/graphics/Bitmap;",
            "IIZZZI",
            "Ljava/lang/CharSequence;",
            "Z",
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;",
            "Landroid/os/Bundle;",
            "Ljava/lang/String;",
            "Z",
            "Ljava/lang/String;",
            ")V"
        }
    .end annotation

    #@0
    .prologue
    .line 43
    .local p19, "people":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Ljava/lang/String;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 44
    new-instance v4, Landroid/app/Notification$Builder;

    #@5
    invoke-direct {v4, p1}, Landroid/app/Notification$Builder;-><init>(Landroid/content/Context;)V

    #@8
    iget-wide v6, p2, Landroid/app/Notification;->when:J

    #@a
    invoke-virtual {v4, v6, v7}, Landroid/app/Notification$Builder;->setWhen(J)Landroid/app/Notification$Builder;

    #@d
    move-result-object v4

    #@e
    move/from16 v0, p14

    #@10
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setShowWhen(Z)Landroid/app/Notification$Builder;

    #@13
    move-result-object v4

    #@14
    iget v5, p2, Landroid/app/Notification;->icon:I

    #@16
    iget v6, p2, Landroid/app/Notification;->iconLevel:I

    #@18
    invoke-virtual {v4, v5, v6}, Landroid/app/Notification$Builder;->setSmallIcon(II)Landroid/app/Notification$Builder;

    #@1b
    move-result-object v4

    #@1c
    iget-object v5, p2, Landroid/app/Notification;->contentView:Landroid/widget/RemoteViews;

    #@1e
    invoke-virtual {v4, v5}, Landroid/app/Notification$Builder;->setContent(Landroid/widget/RemoteViews;)Landroid/app/Notification$Builder;

    #@21
    move-result-object v4

    #@22
    iget-object v5, p2, Landroid/app/Notification;->tickerText:Ljava/lang/CharSequence;

    #@24
    invoke-virtual {v4, v5, p6}, Landroid/app/Notification$Builder;->setTicker(Ljava/lang/CharSequence;Landroid/widget/RemoteViews;)Landroid/app/Notification$Builder;

    #@27
    move-result-object v4

    #@28
    iget-object v5, p2, Landroid/app/Notification;->sound:Landroid/net/Uri;

    #@2a
    iget v6, p2, Landroid/app/Notification;->audioStreamType:I

    #@2c
    invoke-virtual {v4, v5, v6}, Landroid/app/Notification$Builder;->setSound(Landroid/net/Uri;I)Landroid/app/Notification$Builder;

    #@2f
    move-result-object v4

    #@30
    iget-object v5, p2, Landroid/app/Notification;->vibrate:[J

    #@32
    invoke-virtual {v4, v5}, Landroid/app/Notification$Builder;->setVibrate([J)Landroid/app/Notification$Builder;

    #@35
    move-result-object v4

    #@36
    iget v5, p2, Landroid/app/Notification;->ledARGB:I

    #@38
    iget v6, p2, Landroid/app/Notification;->ledOnMS:I

    #@3a
    iget v7, p2, Landroid/app/Notification;->ledOffMS:I

    #@3c
    invoke-virtual {v4, v5, v6, v7}, Landroid/app/Notification$Builder;->setLights(III)Landroid/app/Notification$Builder;

    #@3f
    move-result-object v5

    #@40
    iget v4, p2, Landroid/app/Notification;->flags:I

    #@42
    and-int/lit8 v4, v4, 0x2

    #@44
    if-eqz v4, :cond_2

    #@46
    const/4 v4, 0x1

    #@47
    :goto_0
    invoke-virtual {v5, v4}, Landroid/app/Notification$Builder;->setOngoing(Z)Landroid/app/Notification$Builder;

    #@4a
    move-result-object v5

    #@4b
    iget v4, p2, Landroid/app/Notification;->flags:I

    #@4d
    and-int/lit8 v4, v4, 0x8

    #@4f
    if-eqz v4, :cond_3

    #@51
    const/4 v4, 0x1

    #@52
    :goto_1
    invoke-virtual {v5, v4}, Landroid/app/Notification$Builder;->setOnlyAlertOnce(Z)Landroid/app/Notification$Builder;

    #@55
    move-result-object v5

    #@56
    iget v4, p2, Landroid/app/Notification;->flags:I

    #@58
    and-int/lit8 v4, v4, 0x10

    #@5a
    if-eqz v4, :cond_4

    #@5c
    const/4 v4, 0x1

    #@5d
    :goto_2
    invoke-virtual {v5, v4}, Landroid/app/Notification$Builder;->setAutoCancel(Z)Landroid/app/Notification$Builder;

    #@60
    move-result-object v4

    #@61
    iget v5, p2, Landroid/app/Notification;->defaults:I

    #@63
    invoke-virtual {v4, v5}, Landroid/app/Notification$Builder;->setDefaults(I)Landroid/app/Notification$Builder;

    #@66
    move-result-object v4

    #@67
    invoke-virtual {v4, p3}, Landroid/app/Notification$Builder;->setContentTitle(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@6a
    move-result-object v4

    #@6b
    invoke-virtual {v4, p4}, Landroid/app/Notification$Builder;->setContentText(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@6e
    move-result-object v4

    #@6f
    move-object/from16 v0, p17

    #@71
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setSubText(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@74
    move-result-object v4

    #@75
    invoke-virtual {v4, p5}, Landroid/app/Notification$Builder;->setContentInfo(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@78
    move-result-object v4

    #@79
    move-object/from16 v0, p8

    #@7b
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setContentIntent(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;

    #@7e
    move-result-object v4

    #@7f
    iget-object v5, p2, Landroid/app/Notification;->deleteIntent:Landroid/app/PendingIntent;

    #@81
    invoke-virtual {v4, v5}, Landroid/app/Notification$Builder;->setDeleteIntent(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;

    #@84
    move-result-object v5

    #@85
    iget v4, p2, Landroid/app/Notification;->flags:I

    #@87
    and-int/lit16 v4, v4, 0x80

    #@89
    if-eqz v4, :cond_5

    #@8b
    const/4 v4, 0x1

    #@8c
    :goto_3
    move-object/from16 v0, p9

    #@8e
    invoke-virtual {v5, v0, v4}, Landroid/app/Notification$Builder;->setFullScreenIntent(Landroid/app/PendingIntent;Z)Landroid/app/Notification$Builder;

    #@91
    move-result-object v4

    #@92
    move-object/from16 v0, p10

    #@94
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setLargeIcon(Landroid/graphics/Bitmap;)Landroid/app/Notification$Builder;

    #@97
    move-result-object v4

    #@98
    invoke-virtual {v4, p7}, Landroid/app/Notification$Builder;->setNumber(I)Landroid/app/Notification$Builder;

    #@9b
    move-result-object v4

    #@9c
    move/from16 v0, p15

    #@9e
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setUsesChronometer(Z)Landroid/app/Notification$Builder;

    #@a1
    move-result-object v4

    #@a2
    move/from16 v0, p16

    #@a4
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setPriority(I)Landroid/app/Notification$Builder;

    #@a7
    move-result-object v4

    #@a8
    move/from16 v0, p11

    #@aa
    move/from16 v1, p12

    #@ac
    move/from16 v2, p13

    #@ae
    invoke-virtual {v4, v0, v1, v2}, Landroid/app/Notification$Builder;->setProgress(IIZ)Landroid/app/Notification$Builder;

    #@b1
    move-result-object v4

    #@b2
    move/from16 v0, p18

    #@b4
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setLocalOnly(Z)Landroid/app/Notification$Builder;

    #@b7
    move-result-object v4

    #@b8
    move-object/from16 v0, p21

    #@ba
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setGroup(Ljava/lang/String;)Landroid/app/Notification$Builder;

    #@bd
    move-result-object v4

    #@be
    move/from16 v0, p22

    #@c0
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setGroupSummary(Z)Landroid/app/Notification$Builder;

    #@c3
    move-result-object v4

    #@c4
    move-object/from16 v0, p23

    #@c6
    invoke-virtual {v4, v0}, Landroid/app/Notification$Builder;->setSortKey(Ljava/lang/String;)Landroid/app/Notification$Builder;

    #@c9
    move-result-object v4

    #@ca
    iput-object v4, p0, Landroid/support/v4/app/NotificationCompatApi20$Builder;->b:Landroid/app/Notification$Builder;

    #@cc
    .line 74
    new-instance v4, Landroid/os/Bundle;

    #@ce
    invoke-direct {v4}, Landroid/os/Bundle;-><init>()V

    #@d1
    iput-object v4, p0, Landroid/support/v4/app/NotificationCompatApi20$Builder;->mExtras:Landroid/os/Bundle;

    #@d3
    .line 75
    if-eqz p20, :cond_0

    #@d5
    .line 76
    iget-object v4, p0, Landroid/support/v4/app/NotificationCompatApi20$Builder;->mExtras:Landroid/os/Bundle;

    #@d7
    move-object/from16 v0, p20

    #@d9
    invoke-virtual {v4, v0}, Landroid/os/Bundle;->putAll(Landroid/os/Bundle;)V

    #@dc
    .line 78
    :cond_0
    if-eqz p19, :cond_1

    #@de
    invoke-virtual/range {p19 .. p19}, Ljava/util/ArrayList;->isEmpty()Z

    #@e1
    move-result v4

    #@e2
    if-nez v4, :cond_1

    #@e4
    .line 79
    iget-object v5, p0, Landroid/support/v4/app/NotificationCompatApi20$Builder;->mExtras:Landroid/os/Bundle;

    #@e6
    const-string v6, "android.people"

    #@e8
    invoke-virtual/range {p19 .. p19}, Ljava/util/ArrayList;->size()I

    #@eb
    move-result v4

    #@ec
    new-array v4, v4, [Ljava/lang/String;

    #@ee
    move-object/from16 v0, p19

    #@f0
    invoke-virtual {v0, v4}, Ljava/util/ArrayList;->toArray([Ljava/lang/Object;)[Ljava/lang/Object;

    #@f3
    move-result-object v4

    #@f4
    check-cast v4, [Ljava/lang/String;

    #@f6
    invoke-virtual {v5, v6, v4}, Landroid/os/Bundle;->putStringArray(Ljava/lang/String;[Ljava/lang/String;)V

    #@f9
    .line 82
    :cond_1
    return-void

    #@fa
    .line 44
    :cond_2
    const/4 v4, 0x0

    #@fb
    goto/16 :goto_0

    #@fd
    :cond_3
    const/4 v4, 0x0

    #@fe
    goto/16 :goto_1

    #@100
    :cond_4
    const/4 v4, 0x0

    #@101
    goto/16 :goto_2

    #@103
    :cond_5
    const/4 v4, 0x0

    #@104
    goto :goto_3
.end method


# virtual methods
.method public addAction(Landroid/support/v4/app/NotificationCompatBase$Action;)V
    .locals 1
    .param p1, "action"    # Landroid/support/v4/app/NotificationCompatBase$Action;

    #@0
    .prologue
    .line 86
    iget-object v0, p0, Landroid/support/v4/app/NotificationCompatApi20$Builder;->b:Landroid/app/Notification$Builder;

    #@2
    invoke-static {v0, p1}, Landroid/support/v4/app/NotificationCompatApi20;->addAction(Landroid/app/Notification$Builder;Landroid/support/v4/app/NotificationCompatBase$Action;)V

    #@5
    .line 87
    return-void
.end method

.method public build()Landroid/app/Notification;
    .locals 2

    #@0
    .prologue
    .line 95
    iget-object v0, p0, Landroid/support/v4/app/NotificationCompatApi20$Builder;->b:Landroid/app/Notification$Builder;

    #@2
    iget-object v1, p0, Landroid/support/v4/app/NotificationCompatApi20$Builder;->mExtras:Landroid/os/Bundle;

    #@4
    invoke-virtual {v0, v1}, Landroid/app/Notification$Builder;->setExtras(Landroid/os/Bundle;)Landroid/app/Notification$Builder;

    #@7
    .line 96
    iget-object v0, p0, Landroid/support/v4/app/NotificationCompatApi20$Builder;->b:Landroid/app/Notification$Builder;

    #@9
    invoke-virtual {v0}, Landroid/app/Notification$Builder;->build()Landroid/app/Notification;

    #@c
    move-result-object v0

    #@d
    return-object v0
.end method

.method public getBuilder()Landroid/app/Notification$Builder;
    .locals 1

    #@0
    .prologue
    .line 91
    iget-object v0, p0, Landroid/support/v4/app/NotificationCompatApi20$Builder;->b:Landroid/app/Notification$Builder;

    #@2
    return-object v0
.end method
