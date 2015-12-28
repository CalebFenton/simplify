.class Landroid/support/v4/app/NotificationCompatApi21;
.super Ljava/lang/Object;
.source "NotificationCompatApi21.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/NotificationCompatApi21$Builder;
    }
.end annotation


# static fields
.field public static final CATEGORY_ALARM:Ljava/lang/String; = "alarm"

.field public static final CATEGORY_CALL:Ljava/lang/String; = "call"

.field public static final CATEGORY_EMAIL:Ljava/lang/String; = "email"

.field public static final CATEGORY_ERROR:Ljava/lang/String; = "err"

.field public static final CATEGORY_EVENT:Ljava/lang/String; = "event"

.field public static final CATEGORY_MESSAGE:Ljava/lang/String; = "msg"

.field public static final CATEGORY_PROGRESS:Ljava/lang/String; = "progress"

.field public static final CATEGORY_PROMO:Ljava/lang/String; = "promo"

.field public static final CATEGORY_RECOMMENDATION:Ljava/lang/String; = "recommendation"

.field public static final CATEGORY_SERVICE:Ljava/lang/String; = "service"

.field public static final CATEGORY_SOCIAL:Ljava/lang/String; = "social"

.field public static final CATEGORY_STATUS:Ljava/lang/String; = "status"

.field public static final CATEGORY_SYSTEM:Ljava/lang/String; = "sys"

.field public static final CATEGORY_TRANSPORT:Ljava/lang/String; = "transport"

.field private static final KEY_AUTHOR:Ljava/lang/String; = "author"

.field private static final KEY_MESSAGES:Ljava/lang/String; = "messages"

.field private static final KEY_ON_READ:Ljava/lang/String; = "on_read"

.field private static final KEY_ON_REPLY:Ljava/lang/String; = "on_reply"

.field private static final KEY_PARTICIPANTS:Ljava/lang/String; = "participants"

.field private static final KEY_REMOTE_INPUT:Ljava/lang/String; = "remote_input"

.field private static final KEY_TEXT:Ljava/lang/String; = "text"

.field private static final KEY_TIMESTAMP:Ljava/lang/String; = "timestamp"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 29
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 55
    return-void
.end method

.method private static fromCompatRemoteInput(Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;)Landroid/app/RemoteInput;
    .locals 2
    .param p0, "src"    # Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@0
    .prologue
    .line 205
    new-instance v0, Landroid/app/RemoteInput$Builder;

    #@2
    invoke-virtual {p0}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getResultKey()Ljava/lang/String;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1}, Landroid/app/RemoteInput$Builder;-><init>(Ljava/lang/String;)V

    #@9
    invoke-virtual {p0}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getLabel()Ljava/lang/CharSequence;

    #@c
    move-result-object v1

    #@d
    invoke-virtual {v0, v1}, Landroid/app/RemoteInput$Builder;->setLabel(Ljava/lang/CharSequence;)Landroid/app/RemoteInput$Builder;

    #@10
    move-result-object v0

    #@11
    invoke-virtual {p0}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getChoices()[Ljava/lang/CharSequence;

    #@14
    move-result-object v1

    #@15
    invoke-virtual {v0, v1}, Landroid/app/RemoteInput$Builder;->setChoices([Ljava/lang/CharSequence;)Landroid/app/RemoteInput$Builder;

    #@18
    move-result-object v0

    #@19
    invoke-virtual {p0}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getAllowFreeFormInput()Z

    #@1c
    move-result v1

    #@1d
    invoke-virtual {v0, v1}, Landroid/app/RemoteInput$Builder;->setAllowFreeFormInput(Z)Landroid/app/RemoteInput$Builder;

    #@20
    move-result-object v0

    #@21
    invoke-virtual {p0}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getExtras()Landroid/os/Bundle;

    #@24
    move-result-object v1

    #@25
    invoke-virtual {v0, v1}, Landroid/app/RemoteInput$Builder;->addExtras(Landroid/os/Bundle;)Landroid/app/RemoteInput$Builder;

    #@28
    move-result-object v0

    #@29
    invoke-virtual {v0}, Landroid/app/RemoteInput$Builder;->build()Landroid/app/RemoteInput;

    #@2c
    move-result-object v0

    #@2d
    return-object v0
.end method

.method static getBundleForUnreadConversation(Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;)Landroid/os/Bundle;
    .locals 10
    .param p0, "uc"    # Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;

    #@0
    .prologue
    .line 128
    if-nez p0, :cond_0

    #@2
    .line 129
    const/4 v1, 0x0

    #@3
    .line 152
    :goto_0
    return-object v1

    #@4
    .line 131
    :cond_0
    new-instance v1, Landroid/os/Bundle;

    #@6
    invoke-direct {v1}, Landroid/os/Bundle;-><init>()V

    #@9
    .line 132
    .local v1, "b":Landroid/os/Bundle;
    const/4 v0, 0x0

    #@a
    .line 133
    .local v0, "author":Ljava/lang/String;
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;->getParticipants()[Ljava/lang/String;

    #@d
    move-result-object v6

    #@e
    if-eqz v6, :cond_1

    #@10
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;->getParticipants()[Ljava/lang/String;

    #@13
    move-result-object v6

    #@14
    array-length v6, v6

    #@15
    const/4 v7, 0x1

    #@16
    if-le v6, v7, :cond_1

    #@18
    .line 134
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;->getParticipants()[Ljava/lang/String;

    #@1b
    move-result-object v6

    #@1c
    const/4 v7, 0x0

    #@1d
    aget-object v0, v6, v7

    #@1f
    .line 136
    :cond_1
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;->getMessages()[Ljava/lang/String;

    #@22
    move-result-object v6

    #@23
    array-length v6, v6

    #@24
    new-array v4, v6, [Landroid/os/Parcelable;

    #@26
    .line 137
    .local v4, "messages":[Landroid/os/Parcelable;
    const/4 v2, 0x0

    #@27
    .local v2, "i":I
    :goto_1
    array-length v6, v4

    #@28
    if-ge v2, v6, :cond_2

    #@2a
    .line 138
    new-instance v3, Landroid/os/Bundle;

    #@2c
    invoke-direct {v3}, Landroid/os/Bundle;-><init>()V

    #@2f
    .line 139
    .local v3, "m":Landroid/os/Bundle;
    const-string v6, "text"

    #@31
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;->getMessages()[Ljava/lang/String;

    #@34
    move-result-object v7

    #@35
    aget-object v7, v7, v2

    #@37
    invoke-virtual {v3, v6, v7}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    #@3a
    .line 140
    const-string v6, "author"

    #@3c
    invoke-virtual {v3, v6, v0}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    #@3f
    .line 141
    aput-object v3, v4, v2

    #@41
    .line 137
    add-int/lit8 v2, v2, 0x1

    #@43
    goto :goto_1

    #@44
    .line 143
    .end local v3    # "m":Landroid/os/Bundle;
    :cond_2
    const-string v6, "messages"

    #@46
    invoke-virtual {v1, v6, v4}, Landroid/os/Bundle;->putParcelableArray(Ljava/lang/String;[Landroid/os/Parcelable;)V

    #@49
    .line 144
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;->getRemoteInput()Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@4c
    move-result-object v5

    #@4d
    .line 145
    .local v5, "remoteInput":Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    if-eqz v5, :cond_3

    #@4f
    .line 146
    const-string v6, "remote_input"

    #@51
    invoke-static {v5}, Landroid/support/v4/app/NotificationCompatApi21;->fromCompatRemoteInput(Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;)Landroid/app/RemoteInput;

    #@54
    move-result-object v7

    #@55
    invoke-virtual {v1, v6, v7}, Landroid/os/Bundle;->putParcelable(Ljava/lang/String;Landroid/os/Parcelable;)V

    #@58
    .line 148
    :cond_3
    const-string v6, "on_reply"

    #@5a
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;->getReplyPendingIntent()Landroid/app/PendingIntent;

    #@5d
    move-result-object v7

    #@5e
    invoke-virtual {v1, v6, v7}, Landroid/os/Bundle;->putParcelable(Ljava/lang/String;Landroid/os/Parcelable;)V

    #@61
    .line 149
    const-string v6, "on_read"

    #@63
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;->getReadPendingIntent()Landroid/app/PendingIntent;

    #@66
    move-result-object v7

    #@67
    invoke-virtual {v1, v6, v7}, Landroid/os/Bundle;->putParcelable(Ljava/lang/String;Landroid/os/Parcelable;)V

    #@6a
    .line 150
    const-string v6, "participants"

    #@6c
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;->getParticipants()[Ljava/lang/String;

    #@6f
    move-result-object v7

    #@70
    invoke-virtual {v1, v6, v7}, Landroid/os/Bundle;->putStringArray(Ljava/lang/String;[Ljava/lang/String;)V

    #@73
    .line 151
    const-string v6, "timestamp"

    #@75
    invoke-virtual {p0}, Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;->getLatestTimestamp()J

    #@78
    move-result-wide v8

    #@79
    invoke-virtual {v1, v6, v8, v9}, Landroid/os/Bundle;->putLong(Ljava/lang/String;J)V

    #@7c
    goto :goto_0
.end method

.method public static getCategory(Landroid/app/Notification;)Ljava/lang/String;
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 124
    iget-object v0, p0, Landroid/app/Notification;->category:Ljava/lang/String;

    #@2
    return-object v0
.end method

.method static getUnreadConversationFromBundle(Landroid/os/Bundle;Landroid/support/v4/app/NotificationCompatBase$UnreadConversation$Factory;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;
    .locals 13
    .param p0, "b"    # Landroid/os/Bundle;
    .param p1, "factory"    # Landroid/support/v4/app/NotificationCompatBase$UnreadConversation$Factory;
    .param p2, "remoteInputFactory"    # Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 158
    if-nez p0, :cond_1

    #@3
    .line 195
    :cond_0
    :goto_0
    return-object v2

    #@4
    .line 161
    :cond_1
    const-string v0, "messages"

    #@6
    invoke-virtual {p0, v0}, Landroid/os/Bundle;->getParcelableArray(Ljava/lang/String;)[Landroid/os/Parcelable;

    #@9
    move-result-object v9

    #@a
    .line 162
    .local v9, "parcelableMessages":[Landroid/os/Parcelable;
    const/4 v1, 0x0

    #@b
    .line 163
    .local v1, "messages":[Ljava/lang/String;
    if-eqz v9, :cond_3

    #@d
    .line 164
    array-length v0, v9

    #@e
    new-array v12, v0, [Ljava/lang/String;

    #@10
    .line 165
    .local v12, "tmp":[Ljava/lang/String;
    const/4 v11, 0x1

    #@11
    .line 166
    .local v11, "success":Z
    const/4 v8, 0x0

    #@12
    .local v8, "i":I
    :goto_1
    array-length v0, v12

    #@13
    if-ge v8, v0, :cond_2

    #@15
    .line 167
    aget-object v0, v9, v8

    #@17
    instance-of v0, v0, Landroid/os/Bundle;

    #@19
    if-nez v0, :cond_5

    #@1b
    .line 168
    const/4 v11, 0x0

    #@1c
    .line 177
    :cond_2
    :goto_2
    if-eqz v11, :cond_0

    #@1e
    .line 178
    move-object v1, v12

    #@1f
    .line 184
    .end local v8    # "i":I
    .end local v11    # "success":Z
    .end local v12    # "tmp":[Ljava/lang/String;
    :cond_3
    const-string v0, "on_read"

    #@21
    invoke-virtual {p0, v0}, Landroid/os/Bundle;->getParcelable(Ljava/lang/String;)Landroid/os/Parcelable;

    #@24
    move-result-object v4

    #@25
    check-cast v4, Landroid/app/PendingIntent;

    #@27
    .line 185
    .local v4, "onRead":Landroid/app/PendingIntent;
    const-string v0, "on_reply"

    #@29
    invoke-virtual {p0, v0}, Landroid/os/Bundle;->getParcelable(Ljava/lang/String;)Landroid/os/Parcelable;

    #@2c
    move-result-object v3

    #@2d
    check-cast v3, Landroid/app/PendingIntent;

    #@2f
    .line 187
    .local v3, "onReply":Landroid/app/PendingIntent;
    const-string v0, "remote_input"

    #@31
    invoke-virtual {p0, v0}, Landroid/os/Bundle;->getParcelable(Ljava/lang/String;)Landroid/os/Parcelable;

    #@34
    move-result-object v10

    #@35
    check-cast v10, Landroid/app/RemoteInput;

    #@37
    .line 189
    .local v10, "remoteInput":Landroid/app/RemoteInput;
    const-string v0, "participants"

    #@39
    invoke-virtual {p0, v0}, Landroid/os/Bundle;->getStringArray(Ljava/lang/String;)[Ljava/lang/String;

    #@3c
    move-result-object v5

    #@3d
    .line 190
    .local v5, "participants":[Ljava/lang/String;
    if-eqz v5, :cond_0

    #@3f
    array-length v0, v5

    #@40
    const/4 v6, 0x1

    #@41
    if-ne v0, v6, :cond_0

    #@43
    .line 195
    if-eqz v10, :cond_4

    #@45
    invoke-static {v10, p2}, Landroid/support/v4/app/NotificationCompatApi21;->toCompatRemoteInput(Landroid/app/RemoteInput;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@48
    move-result-object v2

    #@49
    :cond_4
    const-string v0, "timestamp"

    #@4b
    invoke-virtual {p0, v0}, Landroid/os/Bundle;->getLong(Ljava/lang/String;)J

    #@4e
    move-result-wide v6

    #@4f
    move-object v0, p1

    #@50
    invoke-interface/range {v0 .. v7}, Landroid/support/v4/app/NotificationCompatBase$UnreadConversation$Factory;->build([Ljava/lang/String;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;Landroid/app/PendingIntent;Landroid/app/PendingIntent;[Ljava/lang/String;J)Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;

    #@53
    move-result-object v2

    #@54
    goto :goto_0

    #@55
    .line 171
    .end local v3    # "onReply":Landroid/app/PendingIntent;
    .end local v4    # "onRead":Landroid/app/PendingIntent;
    .end local v5    # "participants":[Ljava/lang/String;
    .end local v10    # "remoteInput":Landroid/app/RemoteInput;
    .restart local v8    # "i":I
    .restart local v11    # "success":Z
    .restart local v12    # "tmp":[Ljava/lang/String;
    :cond_5
    aget-object v0, v9, v8

    #@57
    check-cast v0, Landroid/os/Bundle;

    #@59
    const-string v6, "text"

    #@5b
    invoke-virtual {v0, v6}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    #@5e
    move-result-object v0

    #@5f
    aput-object v0, v12, v8

    #@61
    .line 172
    aget-object v0, v12, v8

    #@63
    if-nez v0, :cond_6

    #@65
    .line 173
    const/4 v11, 0x0

    #@66
    .line 174
    goto :goto_2

    #@67
    .line 166
    :cond_6
    add-int/lit8 v8, v8, 0x1

    #@69
    goto :goto_1
.end method

.method private static toCompatRemoteInput(Landroid/app/RemoteInput;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    .locals 6
    .param p0, "remoteInput"    # Landroid/app/RemoteInput;
    .param p1, "factory"    # Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;

    #@0
    .prologue
    .line 216
    invoke-virtual {p0}, Landroid/app/RemoteInput;->getResultKey()Ljava/lang/String;

    #@3
    move-result-object v1

    #@4
    invoke-virtual {p0}, Landroid/app/RemoteInput;->getLabel()Ljava/lang/CharSequence;

    #@7
    move-result-object v2

    #@8
    invoke-virtual {p0}, Landroid/app/RemoteInput;->getChoices()[Ljava/lang/CharSequence;

    #@b
    move-result-object v3

    #@c
    invoke-virtual {p0}, Landroid/app/RemoteInput;->getAllowFreeFormInput()Z

    #@f
    move-result v4

    #@10
    invoke-virtual {p0}, Landroid/app/RemoteInput;->getExtras()Landroid/os/Bundle;

    #@13
    move-result-object v5

    #@14
    move-object v0, p1

    #@15
    invoke-interface/range {v0 .. v5}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;->build(Ljava/lang/String;Ljava/lang/CharSequence;[Ljava/lang/CharSequence;ZLandroid/os/Bundle;)Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@18
    move-result-object v0

    #@19
    return-object v0
.end method
