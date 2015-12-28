.class Landroid/support/v4/app/NotificationManagerCompatEclair;
.super Ljava/lang/Object;
.source "NotificationManagerCompatEclair.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method static cancelNotification(Landroid/app/NotificationManager;Ljava/lang/String;I)V
    .locals 0
    .param p0, "notificationManager"    # Landroid/app/NotificationManager;
    .param p1, "tag"    # Ljava/lang/String;
    .param p2, "id"    # I

    #@0
    .prologue
    .line 25
    invoke-virtual {p0, p1, p2}, Landroid/app/NotificationManager;->cancel(Ljava/lang/String;I)V

    #@3
    .line 26
    return-void
.end method

.method public static postNotification(Landroid/app/NotificationManager;Ljava/lang/String;ILandroid/app/Notification;)V
    .locals 0
    .param p0, "notificationManager"    # Landroid/app/NotificationManager;
    .param p1, "tag"    # Ljava/lang/String;
    .param p2, "id"    # I
    .param p3, "notification"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 30
    invoke-virtual {p0, p1, p2, p3}, Landroid/app/NotificationManager;->notify(Ljava/lang/String;ILandroid/app/Notification;)V

    #@3
    .line 31
    return-void
.end method
