.class Landroid/support/v4/app/NotificationCompatIceCreamSandwich;
.super Ljava/lang/Object;
.source "NotificationCompatIceCreamSandwich.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 25
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method static add(Landroid/content/Context;Landroid/app/Notification;Ljava/lang/CharSequence;Ljava/lang/CharSequence;Ljava/lang/CharSequence;Landroid/widget/RemoteViews;ILandroid/app/PendingIntent;Landroid/app/PendingIntent;Landroid/graphics/Bitmap;IIZ)Landroid/app/Notification;
    .locals 9
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "n"    # Landroid/app/Notification;
    .param p2, "contentTitle"    # Ljava/lang/CharSequence;
    .param p3, "contentText"    # Ljava/lang/CharSequence;
    .param p4, "contentInfo"    # Ljava/lang/CharSequence;
    .param p5, "tickerView"    # Landroid/widget/RemoteViews;
    .param p6, "number"    # I
    .param p7, "contentIntent"    # Landroid/app/PendingIntent;
    .param p8, "fullScreenIntent"    # Landroid/app/PendingIntent;
    .param p9, "largeIcon"    # Landroid/graphics/Bitmap;
    .param p10, "progressMax"    # I
    .param p11, "progress"    # I
    .param p12, "progressIndeterminate"    # Z

    #@0
    .prologue
    .line 31
    new-instance v5, Landroid/app/Notification$Builder;

    #@2
    invoke-direct {v5, p0}, Landroid/app/Notification$Builder;-><init>(Landroid/content/Context;)V

    #@5
    iget-wide v6, p1, Landroid/app/Notification;->when:J

    #@7
    invoke-virtual {v5, v6, v7}, Landroid/app/Notification$Builder;->setWhen(J)Landroid/app/Notification$Builder;

    #@a
    move-result-object v5

    #@b
    iget v6, p1, Landroid/app/Notification;->icon:I

    #@d
    iget v7, p1, Landroid/app/Notification;->iconLevel:I

    #@f
    invoke-virtual {v5, v6, v7}, Landroid/app/Notification$Builder;->setSmallIcon(II)Landroid/app/Notification$Builder;

    #@12
    move-result-object v5

    #@13
    iget-object v6, p1, Landroid/app/Notification;->contentView:Landroid/widget/RemoteViews;

    #@15
    invoke-virtual {v5, v6}, Landroid/app/Notification$Builder;->setContent(Landroid/widget/RemoteViews;)Landroid/app/Notification$Builder;

    #@18
    move-result-object v5

    #@19
    iget-object v6, p1, Landroid/app/Notification;->tickerText:Ljava/lang/CharSequence;

    #@1b
    invoke-virtual {v5, v6, p5}, Landroid/app/Notification$Builder;->setTicker(Ljava/lang/CharSequence;Landroid/widget/RemoteViews;)Landroid/app/Notification$Builder;

    #@1e
    move-result-object v5

    #@1f
    iget-object v6, p1, Landroid/app/Notification;->sound:Landroid/net/Uri;

    #@21
    iget v7, p1, Landroid/app/Notification;->audioStreamType:I

    #@23
    invoke-virtual {v5, v6, v7}, Landroid/app/Notification$Builder;->setSound(Landroid/net/Uri;I)Landroid/app/Notification$Builder;

    #@26
    move-result-object v5

    #@27
    iget-object v6, p1, Landroid/app/Notification;->vibrate:[J

    #@29
    invoke-virtual {v5, v6}, Landroid/app/Notification$Builder;->setVibrate([J)Landroid/app/Notification$Builder;

    #@2c
    move-result-object v5

    #@2d
    iget v6, p1, Landroid/app/Notification;->ledARGB:I

    #@2f
    iget v7, p1, Landroid/app/Notification;->ledOnMS:I

    #@31
    iget v8, p1, Landroid/app/Notification;->ledOffMS:I

    #@33
    invoke-virtual {v5, v6, v7, v8}, Landroid/app/Notification$Builder;->setLights(III)Landroid/app/Notification$Builder;

    #@36
    move-result-object v6

    #@37
    iget v5, p1, Landroid/app/Notification;->flags:I

    #@39
    and-int/lit8 v5, v5, 0x2

    #@3b
    if-eqz v5, :cond_0

    #@3d
    const/4 v5, 0x1

    #@3e
    :goto_0
    invoke-virtual {v6, v5}, Landroid/app/Notification$Builder;->setOngoing(Z)Landroid/app/Notification$Builder;

    #@41
    move-result-object v6

    #@42
    iget v5, p1, Landroid/app/Notification;->flags:I

    #@44
    and-int/lit8 v5, v5, 0x8

    #@46
    if-eqz v5, :cond_1

    #@48
    const/4 v5, 0x1

    #@49
    :goto_1
    invoke-virtual {v6, v5}, Landroid/app/Notification$Builder;->setOnlyAlertOnce(Z)Landroid/app/Notification$Builder;

    #@4c
    move-result-object v6

    #@4d
    iget v5, p1, Landroid/app/Notification;->flags:I

    #@4f
    and-int/lit8 v5, v5, 0x10

    #@51
    if-eqz v5, :cond_2

    #@53
    const/4 v5, 0x1

    #@54
    :goto_2
    invoke-virtual {v6, v5}, Landroid/app/Notification$Builder;->setAutoCancel(Z)Landroid/app/Notification$Builder;

    #@57
    move-result-object v5

    #@58
    iget v6, p1, Landroid/app/Notification;->defaults:I

    #@5a
    invoke-virtual {v5, v6}, Landroid/app/Notification$Builder;->setDefaults(I)Landroid/app/Notification$Builder;

    #@5d
    move-result-object v5

    #@5e
    invoke-virtual {v5, p2}, Landroid/app/Notification$Builder;->setContentTitle(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@61
    move-result-object v5

    #@62
    invoke-virtual {v5, p3}, Landroid/app/Notification$Builder;->setContentText(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@65
    move-result-object v5

    #@66
    invoke-virtual {v5, p4}, Landroid/app/Notification$Builder;->setContentInfo(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@69
    move-result-object v5

    #@6a
    move-object/from16 v0, p7

    #@6c
    invoke-virtual {v5, v0}, Landroid/app/Notification$Builder;->setContentIntent(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;

    #@6f
    move-result-object v5

    #@70
    iget-object v6, p1, Landroid/app/Notification;->deleteIntent:Landroid/app/PendingIntent;

    #@72
    invoke-virtual {v5, v6}, Landroid/app/Notification$Builder;->setDeleteIntent(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;

    #@75
    move-result-object v6

    #@76
    iget v5, p1, Landroid/app/Notification;->flags:I

    #@78
    and-int/lit16 v5, v5, 0x80

    #@7a
    if-eqz v5, :cond_3

    #@7c
    const/4 v5, 0x1

    #@7d
    :goto_3
    move-object/from16 v0, p8

    #@7f
    invoke-virtual {v6, v0, v5}, Landroid/app/Notification$Builder;->setFullScreenIntent(Landroid/app/PendingIntent;Z)Landroid/app/Notification$Builder;

    #@82
    move-result-object v5

    #@83
    move-object/from16 v0, p9

    #@85
    invoke-virtual {v5, v0}, Landroid/app/Notification$Builder;->setLargeIcon(Landroid/graphics/Bitmap;)Landroid/app/Notification$Builder;

    #@88
    move-result-object v5

    #@89
    invoke-virtual {v5, p6}, Landroid/app/Notification$Builder;->setNumber(I)Landroid/app/Notification$Builder;

    #@8c
    move-result-object v5

    #@8d
    move/from16 v0, p10

    #@8f
    move/from16 v1, p11

    #@91
    move/from16 v2, p12

    #@93
    invoke-virtual {v5, v0, v1, v2}, Landroid/app/Notification$Builder;->setProgress(IIZ)Landroid/app/Notification$Builder;

    #@96
    move-result-object v4

    #@97
    .line 54
    .local v4, "b":Landroid/app/Notification$Builder;
    invoke-virtual {v4}, Landroid/app/Notification$Builder;->getNotification()Landroid/app/Notification;

    #@9a
    move-result-object v5

    #@9b
    return-object v5

    #@9c
    .line 31
    .end local v4    # "b":Landroid/app/Notification$Builder;
    :cond_0
    const/4 v5, 0x0

    #@9d
    goto :goto_0

    #@9e
    :cond_1
    const/4 v5, 0x0

    #@9f
    goto :goto_1

    #@a0
    :cond_2
    const/4 v5, 0x0

    #@a1
    goto :goto_2

    #@a2
    :cond_3
    const/4 v5, 0x0

    #@a3
    goto :goto_3
.end method
