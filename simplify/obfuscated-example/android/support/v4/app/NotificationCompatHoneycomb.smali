.class Landroid/support/v4/app/NotificationCompatHoneycomb;
.super Ljava/lang/Object;
.source "NotificationCompatHoneycomb.java"


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

.method static add(Landroid/content/Context;Landroid/app/Notification;Ljava/lang/CharSequence;Ljava/lang/CharSequence;Ljava/lang/CharSequence;Landroid/widget/RemoteViews;ILandroid/app/PendingIntent;Landroid/app/PendingIntent;Landroid/graphics/Bitmap;)Landroid/app/Notification;
    .locals 5
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

    #@0
    .prologue
    .line 30
    new-instance v1, Landroid/app/Notification$Builder;

    #@2
    invoke-direct {v1, p0}, Landroid/app/Notification$Builder;-><init>(Landroid/content/Context;)V

    #@5
    iget-wide v2, p1, Landroid/app/Notification;->when:J

    #@7
    invoke-virtual {v1, v2, v3}, Landroid/app/Notification$Builder;->setWhen(J)Landroid/app/Notification$Builder;

    #@a
    move-result-object v1

    #@b
    iget v2, p1, Landroid/app/Notification;->icon:I

    #@d
    iget v3, p1, Landroid/app/Notification;->iconLevel:I

    #@f
    invoke-virtual {v1, v2, v3}, Landroid/app/Notification$Builder;->setSmallIcon(II)Landroid/app/Notification$Builder;

    #@12
    move-result-object v1

    #@13
    iget-object v2, p1, Landroid/app/Notification;->contentView:Landroid/widget/RemoteViews;

    #@15
    invoke-virtual {v1, v2}, Landroid/app/Notification$Builder;->setContent(Landroid/widget/RemoteViews;)Landroid/app/Notification$Builder;

    #@18
    move-result-object v1

    #@19
    iget-object v2, p1, Landroid/app/Notification;->tickerText:Ljava/lang/CharSequence;

    #@1b
    invoke-virtual {v1, v2, p5}, Landroid/app/Notification$Builder;->setTicker(Ljava/lang/CharSequence;Landroid/widget/RemoteViews;)Landroid/app/Notification$Builder;

    #@1e
    move-result-object v1

    #@1f
    iget-object v2, p1, Landroid/app/Notification;->sound:Landroid/net/Uri;

    #@21
    iget v3, p1, Landroid/app/Notification;->audioStreamType:I

    #@23
    invoke-virtual {v1, v2, v3}, Landroid/app/Notification$Builder;->setSound(Landroid/net/Uri;I)Landroid/app/Notification$Builder;

    #@26
    move-result-object v1

    #@27
    iget-object v2, p1, Landroid/app/Notification;->vibrate:[J

    #@29
    invoke-virtual {v1, v2}, Landroid/app/Notification$Builder;->setVibrate([J)Landroid/app/Notification$Builder;

    #@2c
    move-result-object v1

    #@2d
    iget v2, p1, Landroid/app/Notification;->ledARGB:I

    #@2f
    iget v3, p1, Landroid/app/Notification;->ledOnMS:I

    #@31
    iget v4, p1, Landroid/app/Notification;->ledOffMS:I

    #@33
    invoke-virtual {v1, v2, v3, v4}, Landroid/app/Notification$Builder;->setLights(III)Landroid/app/Notification$Builder;

    #@36
    move-result-object v2

    #@37
    iget v1, p1, Landroid/app/Notification;->flags:I

    #@39
    and-int/lit8 v1, v1, 0x2

    #@3b
    if-eqz v1, :cond_0

    #@3d
    const/4 v1, 0x1

    #@3e
    :goto_0
    invoke-virtual {v2, v1}, Landroid/app/Notification$Builder;->setOngoing(Z)Landroid/app/Notification$Builder;

    #@41
    move-result-object v2

    #@42
    iget v1, p1, Landroid/app/Notification;->flags:I

    #@44
    and-int/lit8 v1, v1, 0x8

    #@46
    if-eqz v1, :cond_1

    #@48
    const/4 v1, 0x1

    #@49
    :goto_1
    invoke-virtual {v2, v1}, Landroid/app/Notification$Builder;->setOnlyAlertOnce(Z)Landroid/app/Notification$Builder;

    #@4c
    move-result-object v2

    #@4d
    iget v1, p1, Landroid/app/Notification;->flags:I

    #@4f
    and-int/lit8 v1, v1, 0x10

    #@51
    if-eqz v1, :cond_2

    #@53
    const/4 v1, 0x1

    #@54
    :goto_2
    invoke-virtual {v2, v1}, Landroid/app/Notification$Builder;->setAutoCancel(Z)Landroid/app/Notification$Builder;

    #@57
    move-result-object v1

    #@58
    iget v2, p1, Landroid/app/Notification;->defaults:I

    #@5a
    invoke-virtual {v1, v2}, Landroid/app/Notification$Builder;->setDefaults(I)Landroid/app/Notification$Builder;

    #@5d
    move-result-object v1

    #@5e
    invoke-virtual {v1, p2}, Landroid/app/Notification$Builder;->setContentTitle(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@61
    move-result-object v1

    #@62
    invoke-virtual {v1, p3}, Landroid/app/Notification$Builder;->setContentText(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@65
    move-result-object v1

    #@66
    invoke-virtual {v1, p4}, Landroid/app/Notification$Builder;->setContentInfo(Ljava/lang/CharSequence;)Landroid/app/Notification$Builder;

    #@69
    move-result-object v1

    #@6a
    invoke-virtual {v1, p7}, Landroid/app/Notification$Builder;->setContentIntent(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;

    #@6d
    move-result-object v1

    #@6e
    iget-object v2, p1, Landroid/app/Notification;->deleteIntent:Landroid/app/PendingIntent;

    #@70
    invoke-virtual {v1, v2}, Landroid/app/Notification$Builder;->setDeleteIntent(Landroid/app/PendingIntent;)Landroid/app/Notification$Builder;

    #@73
    move-result-object v2

    #@74
    iget v1, p1, Landroid/app/Notification;->flags:I

    #@76
    and-int/lit16 v1, v1, 0x80

    #@78
    if-eqz v1, :cond_3

    #@7a
    const/4 v1, 0x1

    #@7b
    :goto_3
    invoke-virtual {v2, p8, v1}, Landroid/app/Notification$Builder;->setFullScreenIntent(Landroid/app/PendingIntent;Z)Landroid/app/Notification$Builder;

    #@7e
    move-result-object v1

    #@7f
    invoke-virtual {v1, p9}, Landroid/app/Notification$Builder;->setLargeIcon(Landroid/graphics/Bitmap;)Landroid/app/Notification$Builder;

    #@82
    move-result-object v1

    #@83
    invoke-virtual {v1, p6}, Landroid/app/Notification$Builder;->setNumber(I)Landroid/app/Notification$Builder;

    #@86
    move-result-object v0

    #@87
    .line 52
    .local v0, "b":Landroid/app/Notification$Builder;
    invoke-virtual {v0}, Landroid/app/Notification$Builder;->getNotification()Landroid/app/Notification;

    #@8a
    move-result-object v1

    #@8b
    return-object v1

    #@8c
    .line 30
    .end local v0    # "b":Landroid/app/Notification$Builder;
    :cond_0
    const/4 v1, 0x0

    #@8d
    goto :goto_0

    #@8e
    :cond_1
    const/4 v1, 0x0

    #@8f
    goto :goto_1

    #@90
    :cond_2
    const/4 v1, 0x0

    #@91
    goto :goto_2

    #@92
    :cond_3
    const/4 v1, 0x0

    #@93
    goto :goto_3
.end method
