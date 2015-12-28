.class interface abstract Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;
.super Ljava/lang/Object;
.source "NotificationCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/NotificationCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x608
    name = "NotificationCompatImpl"
.end annotation


# virtual methods
.method public abstract build(Landroid/support/v4/app/NotificationCompat$Builder;)Landroid/app/Notification;
.end method

.method public abstract getAction(Landroid/app/Notification;I)Landroid/support/v4/app/NotificationCompat$Action;
.end method

.method public abstract getActionCount(Landroid/app/Notification;)I
.end method

.method public abstract getActionsFromParcelableArrayList(Ljava/util/ArrayList;)[Landroid/support/v4/app/NotificationCompat$Action;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/os/Parcelable;",
            ">;)[",
            "Landroid/support/v4/app/NotificationCompat$Action;"
        }
    .end annotation
.end method

.method public abstract getBundleForUnreadConversation(Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;)Landroid/os/Bundle;
.end method

.method public abstract getCategory(Landroid/app/Notification;)Ljava/lang/String;
.end method

.method public abstract getExtras(Landroid/app/Notification;)Landroid/os/Bundle;
.end method

.method public abstract getGroup(Landroid/app/Notification;)Ljava/lang/String;
.end method

.method public abstract getLocalOnly(Landroid/app/Notification;)Z
.end method

.method public abstract getParcelableArrayListForActions([Landroid/support/v4/app/NotificationCompat$Action;)Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "([",
            "Landroid/support/v4/app/NotificationCompat$Action;",
            ")",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/os/Parcelable;",
            ">;"
        }
    .end annotation
.end method

.method public abstract getSortKey(Landroid/app/Notification;)Ljava/lang/String;
.end method

.method public abstract getUnreadConversationFromBundle(Landroid/os/Bundle;Landroid/support/v4/app/NotificationCompatBase$UnreadConversation$Factory;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;
.end method

.method public abstract isGroupSummary(Landroid/app/Notification;)Z
.end method
