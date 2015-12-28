.class public Landroid/support/v4/app/NotificationCompat;
.super Ljava/lang/Object;
.source "NotificationCompat.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/NotificationCompat$1;,
        Landroid/support/v4/app/NotificationCompat$CarExtender;,
        Landroid/support/v4/app/NotificationCompat$WearableExtender;,
        Landroid/support/v4/app/NotificationCompat$Extender;,
        Landroid/support/v4/app/NotificationCompat$Action;,
        Landroid/support/v4/app/NotificationCompat$InboxStyle;,
        Landroid/support/v4/app/NotificationCompat$BigTextStyle;,
        Landroid/support/v4/app/NotificationCompat$BigPictureStyle;,
        Landroid/support/v4/app/NotificationCompat$Style;,
        Landroid/support/v4/app/NotificationCompat$Builder;,
        Landroid/support/v4/app/NotificationCompat$NotificationCompatImplApi21;,
        Landroid/support/v4/app/NotificationCompat$NotificationCompatImplApi20;,
        Landroid/support/v4/app/NotificationCompat$NotificationCompatImplKitKat;,
        Landroid/support/v4/app/NotificationCompat$NotificationCompatImplJellybean;,
        Landroid/support/v4/app/NotificationCompat$NotificationCompatImplIceCreamSandwich;,
        Landroid/support/v4/app/NotificationCompat$NotificationCompatImplHoneycomb;,
        Landroid/support/v4/app/NotificationCompat$NotificationCompatImplGingerbread;,
        Landroid/support/v4/app/NotificationCompat$NotificationCompatImplBase;,
        Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;
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

.field public static final COLOR_DEFAULT:I = 0x0

.field public static final DEFAULT_ALL:I = -0x1

.field public static final DEFAULT_LIGHTS:I = 0x4

.field public static final DEFAULT_SOUND:I = 0x1

.field public static final DEFAULT_VIBRATE:I = 0x2

.field public static final EXTRA_BACKGROUND_IMAGE_URI:Ljava/lang/String; = "android.backgroundImageUri"

.field public static final EXTRA_BIG_TEXT:Ljava/lang/String; = "android.bigText"

.field public static final EXTRA_COMPACT_ACTIONS:Ljava/lang/String; = "android.compactActions"

.field public static final EXTRA_INFO_TEXT:Ljava/lang/String; = "android.infoText"

.field public static final EXTRA_LARGE_ICON:Ljava/lang/String; = "android.largeIcon"

.field public static final EXTRA_LARGE_ICON_BIG:Ljava/lang/String; = "android.largeIcon.big"

.field public static final EXTRA_MEDIA_SESSION:Ljava/lang/String; = "android.mediaSession"

.field public static final EXTRA_PEOPLE:Ljava/lang/String; = "android.people"

.field public static final EXTRA_PICTURE:Ljava/lang/String; = "android.picture"

.field public static final EXTRA_PROGRESS:Ljava/lang/String; = "android.progress"

.field public static final EXTRA_PROGRESS_INDETERMINATE:Ljava/lang/String; = "android.progressIndeterminate"

.field public static final EXTRA_PROGRESS_MAX:Ljava/lang/String; = "android.progressMax"

.field public static final EXTRA_SHOW_CHRONOMETER:Ljava/lang/String; = "android.showChronometer"

.field public static final EXTRA_SHOW_WHEN:Ljava/lang/String; = "android.showWhen"

.field public static final EXTRA_SMALL_ICON:Ljava/lang/String; = "android.icon"

.field public static final EXTRA_SUB_TEXT:Ljava/lang/String; = "android.subText"

.field public static final EXTRA_SUMMARY_TEXT:Ljava/lang/String; = "android.summaryText"

.field public static final EXTRA_TEMPLATE:Ljava/lang/String; = "android.template"

.field public static final EXTRA_TEXT:Ljava/lang/String; = "android.text"

.field public static final EXTRA_TEXT_LINES:Ljava/lang/String; = "android.textLines"

.field public static final EXTRA_TITLE:Ljava/lang/String; = "android.title"

.field public static final EXTRA_TITLE_BIG:Ljava/lang/String; = "android.title.big"

.field public static final FLAG_AUTO_CANCEL:I = 0x10

.field public static final FLAG_FOREGROUND_SERVICE:I = 0x40

.field public static final FLAG_GROUP_SUMMARY:I = 0x200

.field public static final FLAG_HIGH_PRIORITY:I = 0x80

.field public static final FLAG_INSISTENT:I = 0x4

.field public static final FLAG_LOCAL_ONLY:I = 0x100

.field public static final FLAG_NO_CLEAR:I = 0x20

.field public static final FLAG_ONGOING_EVENT:I = 0x2

.field public static final FLAG_ONLY_ALERT_ONCE:I = 0x8

.field public static final FLAG_SHOW_LIGHTS:I = 0x1

.field private static final IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

.field public static final PRIORITY_DEFAULT:I = 0x0

.field public static final PRIORITY_HIGH:I = 0x1

.field public static final PRIORITY_LOW:I = -0x1

.field public static final PRIORITY_MAX:I = 0x2

.field public static final PRIORITY_MIN:I = -0x2

.field public static final STREAM_DEFAULT:I = -0x1

.field public static final VISIBILITY_PRIVATE:I = 0x0

.field public static final VISIBILITY_PUBLIC:I = 0x1

.field public static final VISIBILITY_SECRET:I = -0x1


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    .line 815
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x15

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 816
    new-instance v0, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplApi21;

    #@8
    invoke-direct {v0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplApi21;-><init>()V

    #@b
    sput-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@d
    .line 832
    :goto_0
    return-void

    #@e
    .line 817
    :cond_0
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@10
    const/16 v1, 0x14

    #@12
    if-lt v0, v1, :cond_1

    #@14
    .line 818
    new-instance v0, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplApi20;

    #@16
    invoke-direct {v0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplApi20;-><init>()V

    #@19
    sput-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@1b
    goto :goto_0

    #@1c
    .line 819
    :cond_1
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@1e
    const/16 v1, 0x13

    #@20
    if-lt v0, v1, :cond_2

    #@22
    .line 820
    new-instance v0, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplKitKat;

    #@24
    invoke-direct {v0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplKitKat;-><init>()V

    #@27
    sput-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@29
    goto :goto_0

    #@2a
    .line 821
    :cond_2
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2c
    const/16 v1, 0x10

    #@2e
    if-lt v0, v1, :cond_3

    #@30
    .line 822
    new-instance v0, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplJellybean;

    #@32
    invoke-direct {v0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplJellybean;-><init>()V

    #@35
    sput-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@37
    goto :goto_0

    #@38
    .line 823
    :cond_3
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@3a
    const/16 v1, 0xe

    #@3c
    if-lt v0, v1, :cond_4

    #@3e
    .line 824
    new-instance v0, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplIceCreamSandwich;

    #@40
    invoke-direct {v0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplIceCreamSandwich;-><init>()V

    #@43
    sput-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@45
    goto :goto_0

    #@46
    .line 825
    :cond_4
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@48
    const/16 v1, 0xb

    #@4a
    if-lt v0, v1, :cond_5

    #@4c
    .line 826
    new-instance v0, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplHoneycomb;

    #@4e
    invoke-direct {v0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplHoneycomb;-><init>()V

    #@51
    sput-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@53
    goto :goto_0

    #@54
    .line 827
    :cond_5
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@56
    const/16 v1, 0x9

    #@58
    if-lt v0, v1, :cond_6

    #@5a
    .line 828
    new-instance v0, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplGingerbread;

    #@5c
    invoke-direct {v0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplGingerbread;-><init>()V

    #@5f
    sput-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@61
    goto :goto_0

    #@62
    .line 830
    :cond_6
    new-instance v0, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplBase;

    #@64
    invoke-direct {v0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImplBase;-><init>()V

    #@67
    sput-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@69
    goto :goto_0
.end method

.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 41
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 2837
    return-void
.end method

.method static synthetic access$000(Landroid/support/v4/app/NotificationBuilderWithActions;Ljava/util/ArrayList;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/app/NotificationBuilderWithActions;
    .param p1, "x1"    # Ljava/util/ArrayList;

    #@0
    .prologue
    .line 41
    invoke-static {p0, p1}, Landroid/support/v4/app/NotificationCompat;->addActionsToBuilder(Landroid/support/v4/app/NotificationBuilderWithActions;Ljava/util/ArrayList;)V

    #@3
    return-void
.end method

.method static synthetic access$100(Landroid/support/v4/app/NotificationBuilderWithBuilderAccessor;Landroid/support/v4/app/NotificationCompat$Style;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/app/NotificationBuilderWithBuilderAccessor;
    .param p1, "x1"    # Landroid/support/v4/app/NotificationCompat$Style;

    #@0
    .prologue
    .line 41
    invoke-static {p0, p1}, Landroid/support/v4/app/NotificationCompat;->addStyleToBuilderJellybean(Landroid/support/v4/app/NotificationBuilderWithBuilderAccessor;Landroid/support/v4/app/NotificationCompat$Style;)V

    #@3
    return-void
.end method

.method static synthetic access$200()Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;
    .locals 1

    #@0
    .prologue
    .line 41
    sget-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@2
    return-object v0
.end method

.method static synthetic access$500(Landroid/os/Bundle;Ljava/lang/String;)[Landroid/app/Notification;
    .locals 1
    .param p0, "x0"    # Landroid/os/Bundle;
    .param p1, "x1"    # Ljava/lang/String;

    #@0
    .prologue
    .line 41
    invoke-static {p0, p1}, Landroid/support/v4/app/NotificationCompat;->getNotificationArrayFromBundle(Landroid/os/Bundle;Ljava/lang/String;)[Landroid/app/Notification;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method private static addActionsToBuilder(Landroid/support/v4/app/NotificationBuilderWithActions;Ljava/util/ArrayList;)V
    .locals 3
    .param p0, "builder"    # Landroid/support/v4/app/NotificationBuilderWithActions;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v4/app/NotificationBuilderWithActions;",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v4/app/NotificationCompat$Action;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 779
    .local p1, "actions":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v4/app/NotificationCompat$Action;>;"
    invoke-virtual {p1}, Ljava/util/ArrayList;->iterator()Ljava/util/Iterator;

    #@3
    move-result-object v1

    #@4
    .local v1, "i$":Ljava/util/Iterator;
    :goto_0
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    #@7
    move-result v2

    #@8
    if-eqz v2, :cond_0

    #@a
    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@d
    move-result-object v0

    #@e
    check-cast v0, Landroid/support/v4/app/NotificationCompat$Action;

    #@10
    .line 780
    .local v0, "action":Landroid/support/v4/app/NotificationCompat$Action;
    invoke-interface {p0, v0}, Landroid/support/v4/app/NotificationBuilderWithActions;->addAction(Landroid/support/v4/app/NotificationCompatBase$Action;)V

    #@13
    goto :goto_0

    #@14
    .line 782
    .end local v0    # "action":Landroid/support/v4/app/NotificationCompat$Action;
    :cond_0
    return-void
.end method

.method private static addStyleToBuilderJellybean(Landroid/support/v4/app/NotificationBuilderWithBuilderAccessor;Landroid/support/v4/app/NotificationCompat$Style;)V
    .locals 10
    .param p0, "builder"    # Landroid/support/v4/app/NotificationBuilderWithBuilderAccessor;
    .param p1, "style"    # Landroid/support/v4/app/NotificationCompat$Style;

    #@0
    .prologue
    .line 786
    if-eqz p1, :cond_0

    #@2
    .line 787
    instance-of v0, p1, Landroid/support/v4/app/NotificationCompat$BigTextStyle;

    #@4
    if-eqz v0, :cond_1

    #@6
    move-object v8, p1

    #@7
    .line 788
    check-cast v8, Landroid/support/v4/app/NotificationCompat$BigTextStyle;

    #@9
    .line 789
    .local v8, "bigTextStyle":Landroid/support/v4/app/NotificationCompat$BigTextStyle;
    iget-object v0, v8, Landroid/support/v4/app/NotificationCompat$BigTextStyle;->mBigContentTitle:Ljava/lang/CharSequence;

    #@b
    iget-boolean v1, v8, Landroid/support/v4/app/NotificationCompat$BigTextStyle;->mSummaryTextSet:Z

    #@d
    iget-object v2, v8, Landroid/support/v4/app/NotificationCompat$BigTextStyle;->mSummaryText:Ljava/lang/CharSequence;

    #@f
    iget-object v3, v8, Landroid/support/v4/app/NotificationCompat$BigTextStyle;->mBigText:Ljava/lang/CharSequence;

    #@11
    invoke-static {p0, v0, v1, v2, v3}, Landroid/support/v4/app/NotificationCompatJellybean;->addBigTextStyle(Landroid/support/v4/app/NotificationBuilderWithBuilderAccessor;Ljava/lang/CharSequence;ZLjava/lang/CharSequence;Ljava/lang/CharSequence;)V

    #@14
    .line 812
    .end local v8    # "bigTextStyle":Landroid/support/v4/app/NotificationCompat$BigTextStyle;
    :cond_0
    :goto_0
    return-void

    #@15
    .line 794
    :cond_1
    instance-of v0, p1, Landroid/support/v4/app/NotificationCompat$InboxStyle;

    #@17
    if-eqz v0, :cond_2

    #@19
    move-object v9, p1

    #@1a
    .line 795
    check-cast v9, Landroid/support/v4/app/NotificationCompat$InboxStyle;

    #@1c
    .line 796
    .local v9, "inboxStyle":Landroid/support/v4/app/NotificationCompat$InboxStyle;
    iget-object v0, v9, Landroid/support/v4/app/NotificationCompat$InboxStyle;->mBigContentTitle:Ljava/lang/CharSequence;

    #@1e
    iget-boolean v1, v9, Landroid/support/v4/app/NotificationCompat$InboxStyle;->mSummaryTextSet:Z

    #@20
    iget-object v2, v9, Landroid/support/v4/app/NotificationCompat$InboxStyle;->mSummaryText:Ljava/lang/CharSequence;

    #@22
    iget-object v3, v9, Landroid/support/v4/app/NotificationCompat$InboxStyle;->mTexts:Ljava/util/ArrayList;

    #@24
    invoke-static {p0, v0, v1, v2, v3}, Landroid/support/v4/app/NotificationCompatJellybean;->addInboxStyle(Landroid/support/v4/app/NotificationBuilderWithBuilderAccessor;Ljava/lang/CharSequence;ZLjava/lang/CharSequence;Ljava/util/ArrayList;)V

    #@27
    goto :goto_0

    #@28
    .line 801
    .end local v9    # "inboxStyle":Landroid/support/v4/app/NotificationCompat$InboxStyle;
    :cond_2
    instance-of v0, p1, Landroid/support/v4/app/NotificationCompat$BigPictureStyle;

    #@2a
    if-eqz v0, :cond_0

    #@2c
    move-object v7, p1

    #@2d
    .line 802
    check-cast v7, Landroid/support/v4/app/NotificationCompat$BigPictureStyle;

    #@2f
    .line 803
    .local v7, "bigPictureStyle":Landroid/support/v4/app/NotificationCompat$BigPictureStyle;
    iget-object v1, v7, Landroid/support/v4/app/NotificationCompat$BigPictureStyle;->mBigContentTitle:Ljava/lang/CharSequence;

    #@31
    iget-boolean v2, v7, Landroid/support/v4/app/NotificationCompat$BigPictureStyle;->mSummaryTextSet:Z

    #@33
    iget-object v3, v7, Landroid/support/v4/app/NotificationCompat$BigPictureStyle;->mSummaryText:Ljava/lang/CharSequence;

    #@35
    iget-object v4, v7, Landroid/support/v4/app/NotificationCompat$BigPictureStyle;->mPicture:Landroid/graphics/Bitmap;

    #@37
    iget-object v5, v7, Landroid/support/v4/app/NotificationCompat$BigPictureStyle;->mBigLargeIcon:Landroid/graphics/Bitmap;

    #@39
    iget-boolean v6, v7, Landroid/support/v4/app/NotificationCompat$BigPictureStyle;->mBigLargeIconSet:Z

    #@3b
    move-object v0, p0

    #@3c
    invoke-static/range {v0 .. v6}, Landroid/support/v4/app/NotificationCompatJellybean;->addBigPictureStyle(Landroid/support/v4/app/NotificationBuilderWithBuilderAccessor;Ljava/lang/CharSequence;ZLjava/lang/CharSequence;Landroid/graphics/Bitmap;Landroid/graphics/Bitmap;Z)V

    #@3f
    goto :goto_0
.end method

.method public static getAction(Landroid/app/Notification;I)Landroid/support/v4/app/NotificationCompat$Action;
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;
    .param p1, "actionIndex"    # I

    #@0
    .prologue
    .line 3204
    sget-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@2
    invoke-interface {v0, p0, p1}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;->getAction(Landroid/app/Notification;I)Landroid/support/v4/app/NotificationCompat$Action;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getActionCount(Landroid/app/Notification;)I
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 3194
    sget-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@2
    invoke-interface {v0, p0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;->getActionCount(Landroid/app/Notification;)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getCategory(Landroid/app/Notification;)Ljava/lang/String;
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 3213
    sget-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@2
    invoke-interface {v0, p0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;->getCategory(Landroid/app/Notification;)Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getExtras(Landroid/app/Notification;)Landroid/os/Bundle;
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 3186
    sget-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@2
    invoke-interface {v0, p0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;->getExtras(Landroid/app/Notification;)Landroid/os/Bundle;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getGroup(Landroid/app/Notification;)Ljava/lang/String;
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 3231
    sget-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@2
    invoke-interface {v0, p0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;->getGroup(Landroid/app/Notification;)Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getLocalOnly(Landroid/app/Notification;)Z
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 3223
    sget-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@2
    invoke-interface {v0, p0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;->getLocalOnly(Landroid/app/Notification;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method private static getNotificationArrayFromBundle(Landroid/os/Bundle;Ljava/lang/String;)[Landroid/app/Notification;
    .locals 4
    .param p0, "bundle"    # Landroid/os/Bundle;
    .param p1, "key"    # Ljava/lang/String;

    #@0
    .prologue
    .line 3168
    invoke-virtual {p0, p1}, Landroid/os/Bundle;->getParcelableArray(Ljava/lang/String;)[Landroid/os/Parcelable;

    #@3
    move-result-object v0

    #@4
    .line 3169
    .local v0, "array":[Landroid/os/Parcelable;
    instance-of v3, v0, [Landroid/app/Notification;

    #@6
    if-nez v3, :cond_0

    #@8
    if-nez v0, :cond_1

    #@a
    .line 3170
    :cond_0
    check-cast v0, [Landroid/app/Notification;

    #@c
    .end local v0    # "array":[Landroid/os/Parcelable;
    check-cast v0, [Landroid/app/Notification;

    #@e
    move-object v2, v0

    #@f
    .line 3177
    :goto_0
    return-object v2

    #@10
    .line 3172
    .restart local v0    # "array":[Landroid/os/Parcelable;
    :cond_1
    array-length v3, v0

    #@11
    new-array v2, v3, [Landroid/app/Notification;

    #@13
    .line 3173
    .local v2, "typedArray":[Landroid/app/Notification;
    const/4 v1, 0x0

    #@14
    .local v1, "i":I
    :goto_1
    array-length v3, v0

    #@15
    if-ge v1, v3, :cond_2

    #@17
    .line 3174
    aget-object v3, v0, v1

    #@19
    check-cast v3, Landroid/app/Notification;

    #@1b
    aput-object v3, v2, v1

    #@1d
    .line 3173
    add-int/lit8 v1, v1, 0x1

    #@1f
    goto :goto_1

    #@20
    .line 3176
    :cond_2
    invoke-virtual {p0, p1, v2}, Landroid/os/Bundle;->putParcelableArray(Ljava/lang/String;[Landroid/os/Parcelable;)V

    #@23
    goto :goto_0
.end method

.method public static getSortKey(Landroid/app/Notification;)Ljava/lang/String;
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 3257
    sget-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@2
    invoke-interface {v0, p0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;->getSortKey(Landroid/app/Notification;)Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static isGroupSummary(Landroid/app/Notification;)Z
    .locals 1
    .param p0, "notif"    # Landroid/app/Notification;

    #@0
    .prologue
    .line 3241
    sget-object v0, Landroid/support/v4/app/NotificationCompat;->IMPL:Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;

    #@2
    invoke-interface {v0, p0}, Landroid/support/v4/app/NotificationCompat$NotificationCompatImpl;->isGroupSummary(Landroid/app/Notification;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method
