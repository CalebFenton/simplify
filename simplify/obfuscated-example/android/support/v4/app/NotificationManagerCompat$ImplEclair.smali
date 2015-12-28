.class Landroid/support/v4/app/NotificationManagerCompat$ImplEclair;
.super Landroid/support/v4/app/NotificationManagerCompat$ImplBase;
.source "NotificationManagerCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/NotificationManagerCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "ImplEclair"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 142
    invoke-direct {p0}, Landroid/support/v4/app/NotificationManagerCompat$ImplBase;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public cancelNotification(Landroid/app/NotificationManager;Ljava/lang/String;I)V
    .locals 0
    .param p1, "notificationManager"    # Landroid/app/NotificationManager;
    .param p2, "tag"    # Ljava/lang/String;
    .param p3, "id"    # I

    #@0
    .prologue
    .line 146
    invoke-static {p1, p2, p3}, Landroid/support/v4/app/NotificationManagerCompatEclair;->cancelNotification(Landroid/app/NotificationManager;Ljava/lang/String;I)V

    #@3
    .line 147
    return-void
.end method

.method public postNotification(Landroid/app/NotificationManager;Ljava/lang/String;ILandroid/app/Notification;)V
    .locals 0
    .param p1, "notificationManager"    # Landroid/app/NotificationManager;
    .param p2, "tag"    # Ljava/lang/String;
    .param p3, "id"    # I
    .param p4, "notification"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 152
    invoke-static {p1, p2, p3, p4}, Landroid/support/v4/app/NotificationManagerCompatEclair;->postNotification(Landroid/app/NotificationManager;Ljava/lang/String;ILandroid/app/Notification;)V

    #@3
    .line 154
    return-void
.end method
