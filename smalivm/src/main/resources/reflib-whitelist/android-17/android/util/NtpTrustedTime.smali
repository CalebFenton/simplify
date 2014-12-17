.class public Landroid/util/NtpTrustedTime;
.super Ljava/lang/Object;
.source "NtpTrustedTime.java"

# interfaces
.implements Landroid/util/TrustedTime;


# static fields
.field private static final LOGD:Z = false

.field private static final TAG:Ljava/lang/String; = "NtpTrustedTime"

.field private static sSingleton:Landroid/util/NtpTrustedTime;


# instance fields
.field private mCachedNtpCertainty:J

.field private mCachedNtpElapsedRealtime:J

.field private mCachedNtpTime:J

.field private mHasCache:Z

.field private final mServer:Ljava/lang/String;

.field private final mTimeout:J


# direct methods
.method private constructor <init>(Ljava/lang/String;J)V
    .registers 4
    .param p1, "server"    # Ljava/lang/String;
    .param p2, "timeout"    # J

    .prologue
    .line 46
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 48
    iput-object p1, p0, Landroid/util/NtpTrustedTime;->mServer:Ljava/lang/String;

    .line 49
    iput-wide p2, p0, Landroid/util/NtpTrustedTime;->mTimeout:J

    .line 50
    return-void
.end method

.method public static declared-synchronized getInstance(Landroid/content/Context;)Landroid/util/NtpTrustedTime;
    .registers 12
    .param p0, "context"    # Landroid/content/Context;

    .prologue
    .line 53
    const-class v10, Landroid/util/NtpTrustedTime;

    monitor-enter v10

    :try_start_3
    sget-object v9, Landroid/util/NtpTrustedTime;->sSingleton:Landroid/util/NtpTrustedTime;

    if-nez v9, :cond_36

    .line 54
    invoke-virtual {p0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    move-result-object v3

    .line 55
    .local v3, "res":Landroid/content/res/Resources;
    invoke-virtual {p0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    move-result-object v4

    .line 57
    .local v4, "resolver":Landroid/content/ContentResolver;
    const v9, 0x1040023

    invoke-virtual {v3, v9}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    move-result-object v0

    .line 59
    .local v0, "defaultServer":Ljava/lang/String;
    const v9, 0x10e002a

    invoke-virtual {v3, v9}, Landroid/content/res/Resources;->getInteger(I)I

    move-result v9

    int-to-long v1, v9

    .line 62
    .local v1, "defaultTimeout":J
    const-string/jumbo v9, "ntp_server"

    invoke-static {v4, v9}, Landroid/provider/Settings$Secure;->getString(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v5

    .line 64
    .local v5, "secureServer":Ljava/lang/String;
    const-string/jumbo v9, "ntp_timeout"

    invoke-static {v4, v9, v1, v2}, Landroid/provider/Settings$Secure;->getLong(Landroid/content/ContentResolver;Ljava/lang/String;J)J

    move-result-wide v7

    .line 67
    .local v7, "timeout":J
    if-eqz v5, :cond_3a

    move-object v6, v5

    .line 68
    .local v6, "server":Ljava/lang/String;
    :goto_2f
    new-instance v9, Landroid/util/NtpTrustedTime;

    invoke-direct {v9, v6, v7, v8}, Landroid/util/NtpTrustedTime;-><init>(Ljava/lang/String;J)V

    sput-object v9, Landroid/util/NtpTrustedTime;->sSingleton:Landroid/util/NtpTrustedTime;

    .line 71
    .end local v0    # "defaultServer":Ljava/lang/String;
    .end local v1    # "defaultTimeout":J
    .end local v3    # "res":Landroid/content/res/Resources;
    .end local v4    # "resolver":Landroid/content/ContentResolver;
    .end local v5    # "secureServer":Ljava/lang/String;
    .end local v6    # "server":Ljava/lang/String;
    .end local v7    # "timeout":J
    :cond_36
    sget-object v9, Landroid/util/NtpTrustedTime;->sSingleton:Landroid/util/NtpTrustedTime;
    :try_end_38
    .catchall {:try_start_3 .. :try_end_38} :catchall_3c

    monitor-exit v10

    return-object v9

    .restart local v0    # "defaultServer":Ljava/lang/String;
    .restart local v1    # "defaultTimeout":J
    .restart local v3    # "res":Landroid/content/res/Resources;
    .restart local v4    # "resolver":Landroid/content/ContentResolver;
    .restart local v5    # "secureServer":Ljava/lang/String;
    .restart local v7    # "timeout":J
    :cond_3a
    move-object v6, v0

    .line 67
    goto :goto_2f

    .line 53
    .end local v0    # "defaultServer":Ljava/lang/String;
    .end local v1    # "defaultTimeout":J
    .end local v3    # "res":Landroid/content/res/Resources;
    .end local v4    # "resolver":Landroid/content/ContentResolver;
    .end local v5    # "secureServer":Ljava/lang/String;
    .end local v7    # "timeout":J
    :catchall_3c
    move-exception v9

    monitor-exit v10

    throw v9
.end method


# virtual methods
.method public currentTimeMillis()J
    .registers 5

    .prologue
    .line 119
    iget-boolean v0, p0, Landroid/util/NtpTrustedTime;->mHasCache:Z

    if-nez v0, :cond_c

    .line 120
    new-instance v0, Ljava/lang/IllegalStateException;

    const-string v1, "Missing authoritative time source"

    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 126
    :cond_c
    iget-wide v0, p0, Landroid/util/NtpTrustedTime;->mCachedNtpTime:J

    invoke-virtual {p0}, Landroid/util/NtpTrustedTime;->getCacheAge()J

    move-result-wide v2

    add-long/2addr v0, v2

    return-wide v0
.end method

.method public forceRefresh()Z
    .registers 8

    .prologue
    const/4 v2, 0x1

    const/4 v1, 0x0

    .line 76
    iget-object v3, p0, Landroid/util/NtpTrustedTime;->mServer:Ljava/lang/String;

    if-nez v3, :cond_7

    .line 90
    :cond_6
    :goto_6
    return v1

    .line 82
    :cond_7
    new-instance v0, Landroid/net/SntpClient;

    invoke-direct {v0}, Landroid/net/SntpClient;-><init>()V

    .line 83
    .local v0, "client":Landroid/net/SntpClient;
    iget-object v3, p0, Landroid/util/NtpTrustedTime;->mServer:Ljava/lang/String;

    iget-wide v4, p0, Landroid/util/NtpTrustedTime;->mTimeout:J

    long-to-int v4, v4

    invoke-virtual {v0, v3, v4}, Landroid/net/SntpClient;->requestTime(Ljava/lang/String;I)Z

    move-result v3

    if-eqz v3, :cond_6

    .line 84
    iput-boolean v2, p0, Landroid/util/NtpTrustedTime;->mHasCache:Z

    .line 85
    invoke-virtual {v0}, Landroid/net/SntpClient;->getNtpTime()J

    move-result-wide v3

    iput-wide v3, p0, Landroid/util/NtpTrustedTime;->mCachedNtpTime:J

    .line 86
    invoke-virtual {v0}, Landroid/net/SntpClient;->getNtpTimeReference()J

    move-result-wide v3

    iput-wide v3, p0, Landroid/util/NtpTrustedTime;->mCachedNtpElapsedRealtime:J

    .line 87
    invoke-virtual {v0}, Landroid/net/SntpClient;->getRoundTripTime()J

    move-result-wide v3

    const-wide/16 v5, 0x2

    div-long/2addr v3, v5

    iput-wide v3, p0, Landroid/util/NtpTrustedTime;->mCachedNtpCertainty:J

    move v1, v2

    .line 88
    goto :goto_6
.end method

.method public getCacheAge()J
    .registers 5

    .prologue
    .line 101
    iget-boolean v0, p0, Landroid/util/NtpTrustedTime;->mHasCache:Z

    if-eqz v0, :cond_c

    .line 102
    invoke-static {}, Landroid/os/SystemClock;->elapsedRealtime()J

    move-result-wide v0

    iget-wide v2, p0, Landroid/util/NtpTrustedTime;->mCachedNtpElapsedRealtime:J

    sub-long/2addr v0, v2

    .line 104
    :goto_b
    return-wide v0

    :cond_c
    const-wide v0, 0x7fffffffffffffffL

    goto :goto_b
.end method

.method public getCacheCertainty()J
    .registers 3

    .prologue
    .line 110
    iget-boolean v0, p0, Landroid/util/NtpTrustedTime;->mHasCache:Z

    if-eqz v0, :cond_7

    .line 111
    iget-wide v0, p0, Landroid/util/NtpTrustedTime;->mCachedNtpCertainty:J

    .line 113
    :goto_6
    return-wide v0

    :cond_7
    const-wide v0, 0x7fffffffffffffffL

    goto :goto_6
.end method

.method public getCachedNtpTime()J
    .registers 3

    .prologue
    .line 131
    iget-wide v0, p0, Landroid/util/NtpTrustedTime;->mCachedNtpTime:J

    return-wide v0
.end method

.method public getCachedNtpTimeReference()J
    .registers 3

    .prologue
    .line 135
    iget-wide v0, p0, Landroid/util/NtpTrustedTime;->mCachedNtpElapsedRealtime:J

    return-wide v0
.end method

.method public hasCache()Z
    .registers 2

    .prologue
    .line 96
    iget-boolean v0, p0, Landroid/util/NtpTrustedTime;->mHasCache:Z

    return v0
.end method
