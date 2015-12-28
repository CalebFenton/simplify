.class public final Landroid/support/v4/app/NotificationCompat$CarExtender;
.super Ljava/lang/Object;
.source "NotificationCompat.java"

# interfaces
.implements Landroid/support/v4/app/NotificationCompat$Extender;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/NotificationCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "CarExtender"
.end annotation

.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/NotificationCompat$CarExtender$UnreadConversation;
    }
.end annotation


# static fields
.field private static final EXTRA_CAR_EXTENDER:Ljava/lang/String; = "android.car.EXTENSIONS"

.field private static final EXTRA_COLOR:Ljava/lang/String; = "app_color"

.field private static final EXTRA_CONVERSATION:Ljava/lang/String; = "car_conversation"

.field private static final EXTRA_LARGE_ICON:Ljava/lang/String; = "large_icon"

.field private static final TAG:Ljava/lang/String; = "CarExtender"


# instance fields
.field private mColor:I

.field private mLargeIcon:Landroid/graphics/Bitmap;

.field private mUnreadConversation:Landroid/support/v4/app/NotificationCompat$CarExtender$UnreadConversation;


# direct methods
.method public constructor <init>()V
    .locals 1

    #@0
    .prologue
    .line 2852
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 2847
    const/4 v0, 0x0

    #@4
    iput v0, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mColor:I

    #@6
    .line 2853
    return-void
.end method

.method public constructor <init>(Landroid/app/Notification;)V
    .locals 5
    .param p1, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    .line 2860
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@4
    .line 2847
    iput v4, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mColor:I

    #@6
    .line 2861
    sget v2, Landroid/os/Build$VERSION;->SDK_INT:I

    #@8
    const/16 v3, 0x15

    #@a
    if-ge v2, v3, :cond_1

    #@c
    .line 2875
    :cond_0
    :goto_0
    return-void

    #@d
    .line 2865
    :cond_1
    invoke-static {p1}, Landroid/support/v4/app/NotificationCompat;->getExtras(Landroid/app/Notification;)Landroid/os/Bundle;

    #@10
    move-result-object v2

    #@11
    if-nez v2, :cond_2

    #@13
    const/4 v1, 0x0

    #@14
    .line 2867
    .local v1, "carBundle":Landroid/os/Bundle;
    :goto_1
    if-eqz v1, :cond_0

    #@16
    .line 2868
    const-string v2, "large_icon"

    #@18
    invoke-virtual {v1, v2}, Landroid/os/Bundle;->getParcelable(Ljava/lang/String;)Landroid/os/Parcelable;

    #@1b
    move-result-object v2

    #@1c
    check-cast v2, Landroid/graphics/Bitmap;

    #@1e
    iput-object v2, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mLargeIcon:Landroid/graphics/Bitmap;

    #@20
    .line 2869
    const-string v2, "app_color"

    #@22
    invoke-virtual {v1, v2, v4}, Landroid/os/Bundle;->getInt(Ljava/lang/String;I)I

    #@25
    move-result v2

    #@26
    iput v2, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mColor:I

    #@28
    .line 2871
    const-string v2, "car_conversation"

    #@2a
    invoke-virtual {v1, v2}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    #@2d
    move-result-object v0

    #@2e
    .line 2872
    .local v0, "b":Landroid/os/Bundle;
    # getter for: Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;
    invoke-static {}, Landroid/support/v4/app/NotificationCompat;->access$200()Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@31
    move-result-object v2

    #@32
    sget-object v3, Landroid/support/v4/app/NotificationCompat$CarExtender$UnreadConversation;->FACTORY:Landroid/support/v4/app/NotificationCompatBase$UnreadConversation$Factory;

    #@34
    sget-object v4, Landroid/support/v4/app/RemoteInput;->FACTORY:Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;

    #@36
    invoke-interface {v2, v0, v3, v4}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;->getUnreadConversationFromBundle(Landroid/os/Bundle;Landroid/support/v4/app/NotificationCompatBase$UnreadConversation$Factory;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;

    #@39
    move-result-object v2

    #@3a
    check-cast v2, Landroid/support/v4/app/NotificationCompat$CarExtender$UnreadConversation;

    #@3c
    iput-object v2, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mUnreadConversation:Landroid/support/v4/app/NotificationCompat$CarExtender$UnreadConversation;

    #@3e
    goto :goto_0

    #@3f
    .line 2865
    .end local v0    # "b":Landroid/os/Bundle;
    .end local v1    # "carBundle":Landroid/os/Bundle;
    :cond_2
    invoke-static {p1}, Landroid/support/v4/app/NotificationCompat;->getExtras(Landroid/app/Notification;)Landroid/os/Bundle;

    #@42
    move-result-object v2

    #@43
    const-string v3, "android.car.EXTENSIONS"

    #@45
    invoke-virtual {v2, v3}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    #@48
    move-result-object v1

    #@49
    goto :goto_1
.end method


# virtual methods
.method public extend(Landroid/support/v4/app/NotificationCompat$Builder;)Landroid/support/v4/app/NotificationCompat$Builder;
    .locals 4
    .param p1, "builder"    # Landroid/support/v4/app/NotificationCompat$Builder;

    #@0
    .prologue
    .line 2884
    sget v2, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v3, 0x15

    #@4
    if-ge v2, v3, :cond_0

    #@6
    .line 2903
    :goto_0
    return-object p1

    #@7
    .line 2888
    :cond_0
    new-instance v1, Landroid/os/Bundle;

    #@9
    invoke-direct {v1}, Landroid/os/Bundle;-><init>()V

    #@c
    .line 2890
    .local v1, "carExtensions":Landroid/os/Bundle;
    iget-object v2, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mLargeIcon:Landroid/graphics/Bitmap;

    #@e
    if-eqz v2, :cond_1

    #@10
    .line 2891
    const-string v2, "large_icon"

    #@12
    iget-object v3, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mLargeIcon:Landroid/graphics/Bitmap;

    #@14
    invoke-virtual {v1, v2, v3}, Landroid/os/Bundle;->putParcelable(Ljava/lang/String;Landroid/os/Parcelable;)V

    #@17
    .line 2893
    :cond_1
    iget v2, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mColor:I

    #@19
    if-eqz v2, :cond_2

    #@1b
    .line 2894
    const-string v2, "app_color"

    #@1d
    iget v3, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mColor:I

    #@1f
    invoke-virtual {v1, v2, v3}, Landroid/os/Bundle;->putInt(Ljava/lang/String;I)V

    #@22
    .line 2897
    :cond_2
    iget-object v2, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mUnreadConversation:Landroid/support/v4/app/NotificationCompat$CarExtender$UnreadConversation;

    #@24
    if-eqz v2, :cond_3

    #@26
    .line 2898
    # getter for: Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;
    invoke-static {}, Landroid/support/v4/app/NotificationCompat;->access$200()Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@29
    move-result-object v2

    #@2a
    iget-object v3, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mUnreadConversation:Landroid/support/v4/app/NotificationCompat$CarExtender$UnreadConversation;

    #@2c
    invoke-interface {v2, v3}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;->getBundleForUnreadConversation(Landroid/support/v4/app/NotificationCompatBase$UnreadConversation;)Landroid/os/Bundle;

    #@2f
    move-result-object v0

    #@30
    .line 2899
    .local v0, "b":Landroid/os/Bundle;
    const-string v2, "car_conversation"

    #@32
    invoke-virtual {v1, v2, v0}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    #@35
    .line 2902
    .end local v0    # "b":Landroid/os/Bundle;
    :cond_3
    invoke-virtual {p1}, Landroid/support/v4/app/NotificationCompat$Builder;->getExtras()Landroid/os/Bundle;

    #@38
    move-result-object v2

    #@39
    const-string v3, "android.car.EXTENSIONS"

    #@3b
    invoke-virtual {v2, v3, v1}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    #@3e
    goto :goto_0
.end method

.method public getColor()I
    .locals 1

    #@0
    .prologue
    .line 2925
    iget v0, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mColor:I

    #@2
    return v0
.end method

.method public getLargeIcon()Landroid/graphics/Bitmap;
    .locals 1

    #@0
    .prologue
    .line 2949
    iget-object v0, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mLargeIcon:Landroid/graphics/Bitmap;

    #@2
    return-object v0
.end method

.method public getUnreadConversation()Landroid/support/v4/app/NotificationCompat$CarExtender$UnreadConversation;
    .locals 1

    #@0
    .prologue
    .line 2968
    iget-object v0, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mUnreadConversation:Landroid/support/v4/app/NotificationCompat$CarExtender$UnreadConversation;

    #@2
    return-object v0
.end method

.method public setColor(I)Landroid/support/v4/app/NotificationCompat$CarExtender;
    .locals 0
    .param p1, "color"    # I

    #@0
    .prologue
    .line 2915
    iput p1, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mColor:I

    #@2
    .line 2916
    return-object p0
.end method

.method public setLargeIcon(Landroid/graphics/Bitmap;)Landroid/support/v4/app/NotificationCompat$CarExtender;
    .locals 0
    .param p1, "largeIcon"    # Landroid/graphics/Bitmap;

    #@0
    .prologue
    .line 2938
    iput-object p1, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mLargeIcon:Landroid/graphics/Bitmap;

    #@2
    .line 2939
    return-object p0
.end method

.method public setUnreadConversation(Landroid/support/v4/app/NotificationCompat$CarExtender$UnreadConversation;)Landroid/support/v4/app/NotificationCompat$CarExtender;
    .locals 0
    .param p1, "unreadConversation"    # Landroid/support/v4/app/NotificationCompat$CarExtender$UnreadConversation;

    #@0
    .prologue
    .line 2959
    iput-object p1, p0, Landroid/support/v4/app/NotificationCompat$CarExtender;->mUnreadConversation:Landroid/support/v4/app/NotificationCompat$CarExtender$UnreadConversation;

    #@2
    .line 2960
    return-object p0
.end method
