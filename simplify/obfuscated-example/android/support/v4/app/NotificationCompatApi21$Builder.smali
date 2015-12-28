.class public Landroid/support/v4/app/NotificationCompatApi21$Builder;
.super Ljava/lang/Object;
.source "NotificationCompatApi21.java"

# interfaces
.implements Landroid/support/v4/app/NotificationBuilderWithActions;
.implements Landroid/support/v4/app/NotificationBuilderWithBuilderAccessor;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/NotificationCompatApi21;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "Builder"
.end annotation


# instance fields
.field private b:Landroid/app/Notification$Builder;


# direct methods
.method public constructor <init>(Landroid/content/Context;Landroid/app/Notification;Ljava/lang/CharSequence;Ljava/lang/CharSequence;Ljava/lang/CharSequence;Landroid/widget/RemoteViews;ILandroid/app/PendingIntent;Landroid/app/PendingIntent;Landroid/graphics/Bitmap;IIZZZILjava/lang/CharSequence;ZLjava/lang/String;Ljava/util/ArrayList;Landroid/os/Bundle;IILandroid/app/Notification;Ljava/lang/String;ZLjava/lang/String;)V
    .locals 10
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
    .param p19, "category"    # Ljava/lang/String;
    .param p21, "extras"    # Landroid/os/Bundle;
    .param p22, "color"    # I
    .param p23, "visibility"    # I
    .param p24, "publicVersion"    # Landroid/app/Notification;
    .param p25, "groupKey"    # Ljava/lang/String;
    .param p26, "groupSummary"    # Z
    .param p27, "sortKey"    # Ljava/lang/String;
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
            "Ljava/lang/String;",
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;",
            "Landroid/os/Bundle;",
            "II",
            "Landroid/app/Notification;",
            "Ljava/lang/String;",
            "Z",
            "Ljava/lang/String;",
            ")V"
        }
    .end annotation

    #@0
    .prologue
    .line 67
    .local p20, "people":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Ljava/lang/String;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 68
    new-instance v6, Landroid/app/Notification$Builder;

    #@5
    invoke-direct {v6, p1}, Landroid/app/Notification$Builder;-><init>(Landroid/content/Context;)V

    #@8
    iget-wide v8, p2, Landroid/app/Notification;->when:J

    #@a
    invoke-virtual {v6, v8, v9}, Landroid/app/Notification$Builder;->setWhen(J)Landroid/app/Notification$Builder;

    #@d
    move-result-object v6

    #@e
    move/from16 v0, p14

    #@10
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setShowWhen(Z)Landroid/app/Notification$Builder;

    #@13
    move-result-object v6

    #@14
    iget v7, p2, Landroid/app/Notification;->icon:I

    #@16
    iget v8, p2, Landroid/app/Notification;->iconLevel:I

    #@18
    invoke-virtual {v6, v7, v8}, Landroid/app/Notification$Builder;->setSmallIcon(II)Landroid/app/Notification$Builder;

    #@1b
    move-result-object v6

    #@1c
    iget-object v7, p2, Landroid/app/Notification;->contentView:Landroid/widget/RemoteViews;

    #@1e
    invoke-virtual {v6, v7}, Landroid/app/Notification$Builder;->setContent(Landroid/widget/RemoteViews;)Landroid/app/Notification$Builder;

    #@21
    move-result-object v6

    #@22
    iget-object v7, p2, Landroid/app/Notification;->tickerText:Ljava/lang/CharSequence;

    #@24
    move-object/from16 v0, p6

    #@26
    invoke-virtual {v6, v7, v0}, Landroid/app/Notification$Builder;->setTicker(Ljava/lang/CharSequence;Landroid/widget/RemoteViews;)Landroid/app/Notification$Builder;

    #@29
    move-result-object v6

    #@2a
    iget-object v7, p2, Landroid/app/Notification;->sound:Landroid/net/Uri;

    #@2c
    iget v8, p2, Landroid/app/Notification;->audioStreamType:I

    #@2e
    invoke-virtual {v6, v7, v8}, Landroid/app/Notification$Builder;->setSound(Landroid/net/Uri;I)Landroid/app/Notification$Builder;

    #@31
    move-result-object v6

    #@32
    iget-object v7, p2, Landroid/app/Notification;->vibrate:[J

    #@34
    invoke-virtual {v6, v7}, Landroid/app/Notification$Builder;->setVibrate([J)Landroid/app/Notification$Builder;

    #@37
    move-result-object v6

    #@38
    iget v7, p2, Landroid/app/Notification;->ledARGB:I

    #@3a
    iget v8, p2, Landroid/app/Notification;->ledOnMS:I

    #@3c
    iget v9, p2, Landroid/app/Notification;->ledOffMS:I

    #@3e
    invoke-virtual {v6, v7, v8, v9}, Landroid/app/Notification$Builder;->setLights(III)Landroid/app/Notification$Builder;

    #@41
    move-result-object v7

    #@42
    iget v6, p2, Landroid/app/Notification;->flags:I

    #@44
    and-int/lit8 v6, v6, 0x2

    #@46
    if-eqz v6, :cond_0

    #@48
    const/4 v6, 0x1

    #@49
    :goto_0
    invoke-virtual {v7, v6}, Landroid/app/Notification$Builder;->setOngoing(Z)Landroid/app/Notification$Builder;

    #@4c
    move-result-object v7

    #@4d
    iget v6, p2, Landroid/app/Notification;->flags:I

    #@4f
    and-int/lit8 v6, v6, 0x8

    #@51
    if-eqz v6, :cond_1

    #@53
    const/4 v6, 0x1

    #@54
    :goto_1
    invoke-virtual {v7, v6}, Landroid/app/Notification$Builder;->setOnlyAlertOnce(Z)Landroid/app/Notification$Builder;

    #@57
    move-result-object v7

    #@58
    iget v6, p2, Landroid/app/Notification;->flags:I

    #@5a
    and-int/lit8 v6, v6, 0x10

    #@5c
    if-eqz v6, :cond_2

    #@5e
    const/4 v6, 0x1

    #@5f
    :goto_2
    invoke-virtual {v7, v6}, Landroid/app/Notification$Builder;->setAutoCancel(Z)Landroid/app/Notification$Builder;

    #@62
    move-result-object v6

    #@63
    iget v7, p2, Landroid/app/Notification;->defaults:I

    #@65
    invoke-virtual {v6, v7}, Landroid/app/Notification$Builder;->setDefaults(I)Landroid/app/Notification$Builder;

    #@68
    move-result-object v6

    #@69
    invoke-virtual {v6, p3}, Landroid/app/Notification$Builder;->setContentTitle(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@6c
    move-result-object v6

    #@6d
    invoke-virtual {v6, p4}, Landroid/app/Notification$Builder;->setContentText(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@70
    move-result-object v6

    #@71
    move-object/from16 v0, p17

    #@73
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setSubText(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@76
    move-result-object v6

    #@77
    invoke-virtual {v6, p5}, Landroid/app/Notification$Builder;->setContentInfo(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@7a
    move-result-object v6

    #@7b
    move-object/from16 v0, p8

    #@7d
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setContentIntent(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;

    #@80
    move-result-object v6

    #@81
    iget-object v7, p2, Landroid/app/Notification;->deleteIntent:Landroid/app/PendingIntent;

    #@83
    invoke-virtual {v6, v7}, Landroid/app/Notification$Builder;->setDeleteIntent(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;

    #@86
    move-result-object v7

    #@87
    iget v6, p2, Landroid/app/Notification;->flags:I

    #@89
    and-int/lit16 v6, v6, 0x80

    #@8b
    if-eqz v6, :cond_3

    #@8d
    const/4 v6, 0x1

    #@8e
    :goto_3
    move-object/from16 v0, p9

    #@90
    invoke-virtual {v7, v0, v6}, Landroid/app/Notification$Builder;->setFullScreenIntent(Landroid/app/PendingIntent;Z)Landroid/app/Notification$Builder;

    #@93
    move-result-object v6

    #@94
    move-object/from16 v0, p10

    #@96
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setLargeIcon(Landroid/graphics/Bitmap;)Landroid/app/Notification$Builder;

    #@99
    move-result-object v6

    #@9a
    move/from16 v0, p7

    #@9c
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setNumber(I)Landroid/app/Notification$Builder;

    #@9f
    move-result-object v6

    #@a0
    move/from16 v0, p15

    #@a2
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setUsesChronometer(Z)Landroid/app/Notification$Builder;

    #@a5
    move-result-object v6

    #@a6
    move/from16 v0, p16

    #@a8
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setPriority(I)Landroid/app/Notification$Builder;

    #@ab
    move-result-object v6

    #@ac
    move/from16 v0, p11

    #@ae
    move/from16 v1, p12

    #@b0
    move/from16 v2, p13

    #@b2
    invoke-virtual {v6, v0, v1, v2}, Landroid/app/Notification$Builder;->setProgress(IIZ)Landroid/app/Notification$Builder;

    #@b5
    move-result-object v6

    #@b6
    move/from16 v0, p18

    #@b8
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setLocalOnly(Z)Landroid/app/Notification$Builder;

    #@bb
    move-result-object v6

    #@bc
    move-object/from16 v0, p21

    #@be
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setExtras(Landroid/os/Bundle;)Landroid/app/Notification$Builder;

    #@c1
    move-result-object v6

    #@c2
    move-object/from16 v0, p25

    #@c4
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setGroup(Ljava/lang/String;)Landroid/app/Notification$Builder;

    #@c7
    move-result-object v6

    #@c8
    move/from16 v0, p26

    #@ca
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setGroupSummary(Z)Landroid/app/Notification$Builder;

    #@cd
    move-result-object v6

    #@ce
    move-object/from16 v0, p27

    #@d0
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setSortKey(Ljava/lang/String;)Landroid/app/Notification$Builder;

    #@d3
    move-result-object v6

    #@d4
    move-object/from16 v0, p19

    #@d6
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setCategory(Ljava/lang/String;)Landroid/app/Notification$Builder;

    #@d9
    move-result-object v6

    #@da
    move/from16 v0, p22

    #@dc
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setColor(I)Landroid/app/Notification$Builder;

    #@df
    move-result-object v6

    #@e0
    move/from16 v0, p23

    #@e2
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setVisibility(I)Landroid/app/Notification$Builder;

    #@e5
    move-result-object v6

    #@e6
    move-object/from16 v0, p24

    #@e8
    invoke-virtual {v6, v0}, Landroid/app/Notification$Builder;->setPublicVersion(Landroid/app/Notification;)Landroid/app/Notification$Builder;

    #@eb
    move-result-object v6

    #@ec
    iput-object v6, p0, Landroid/support/v4/app/NotificationCompatApi21$Builder;->b:Landroid/app/Notification$Builder;

    #@ee
    .line 103
    invoke-virtual/range {p20 .. p20}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    #@f1
    move-result-object v4

    #@f2
    .local v4, "i$":Ljava/util/Iterator;
    :goto_4
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    #@f5
    move-result v6

    #@f6
    if-eqz v6, :cond_4

    #@f8
    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@fb
    move-result-object v5

    #@fc
    check-cast v5, Ljava/lang/String;

    #@fe
    .line 104
    .local v5, "person":Ljava/lang/String;
    iget-object v6, p0, Landroid/support/v4/app/NotificationCompatApi21$Builder;->b:Landroid/app/Notification$Builder;

    #@100
    invoke-virtual {v6, v5}, Landroid/app/Notification$Builder;->addPerson(Ljava/lang/String;)Landroid/app/Notification$Builder;

    #@103
    goto :goto_4

    #@104
    .line 68
    .end local v4    # "i$":Ljava/util/Iterator;
    .end local v5    # "person":Ljava/lang/String;
    :cond_0
    const/4 v6, 0x0

    #@105
    goto/16 :goto_0

    #@107
    :cond_1
    const/4 v6, 0x0

    #@108
    goto/16 :goto_1

    #@10a
    :cond_2
    const/4 v6, 0x0

    #@10b
    goto/16 :goto_2

    #@10d
    :cond_3
    const/4 v6, 0x0

    #@10e
    goto :goto_3

    #@10f
    .line 106
    .restart local v4    # "i$":Ljava/util/Iterator;
    :cond_4
    return-void
.end method


# virtual methods
.method public addAction(Landroid/support/v4/app/NotificationCompatBase$Action;)V
    .locals 1
    .param p1, "action"    # Landroid/support/v4/app/NotificationCompatBase$Action;

    #@0
    .prologue
    .line 110
    iget-object v0, p0, Landroid/support/v4/app/NotificationCompatApi21$Builder;->b:Landroid/app/Notification$Builder;

    #@2
    invoke-static {v0, p1}, Landroid/support/v4/app/NotificationCompatApi20;->addAction(Landroid/app/Notification$Builder;Landroid/support/v4/app/NotificationCompatBase$Action;)V

    #@5
    .line 111
    return-void
.end method

.method public build()Landroid/app/Notification;
    .locals 1

    #@0
    .prologue
    .line 119
    iget-object v0, p0, Landroid/support/v4/app/NotificationCompatApi21$Builder;->b:Landroid/app/Notification$Builder;

    #@2
    invoke-virtual {v0}, Landroid/app/Notification$Builder;->build()Landroid/app/Notification;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getBuilder()Landroid/app/Notification$Builder;
    .locals 1

    #@0
    .prologue
    .line 115
    iget-object v0, p0, Landroid/support/v4/app/NotificationCompatApi21$Builder;->b:Landroid/app/Notification$Builder;

    #@2
    return-object v0
.end method
