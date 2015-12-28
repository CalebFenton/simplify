.class public abstract Landroid/support/v4/content/WakefulBroadcastReceiver;
.super Landroid/content/BroadcastReceiver;
.source "WakefulBroadcastReceiver.java"


# static fields
.field private static final EXTRA_WAKE_LOCK_ID:Ljava/lang/String; = "android.support.content.wakelockid"

.field private static final mActiveWakeLocks:Landroid/util/SparseArray;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/util/SparseArray",
            "<",
            "Landroid/os/PowerManager$WakeLock;",
            ">;"
        }
    .end annotation
.end field

.field private static mNextId:I


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 62
    new-instance v0, Landroid/util/SparseArray;

    #@2
    invoke-direct {v0}, Landroid/util/SparseArray;-><init>()V

    #@5
    sput-object v0, Landroid/support/v4/content/WakefulBroadcastReceiver;->mActiveWakeLocks:Landroid/util/SparseArray;

    #@7
    .line 64
    const/4 v0, 0x1

    #@8
    sput v0, Landroid/support/v4/content/WakefulBroadcastReceiver;->mNextId:I

    #@a
    return-void
.end method

.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 59
    invoke-direct {p0}, Landroid/content/BroadcastReceiver;-><init>()V

    #@3
    return-void
.end method

.method public static completeWakefulIntent(Landroid/content/Intent;)Z
    .locals 7
    .param p0, "intent"    # Landroid/content/Intent;

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 113
    const-string v4, "android.support.content.wakelockid"

    #@4
    invoke-virtual {p0, v4, v2}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I

    #@7
    move-result v0

    #@8
    .line 114
    .local v0, "id":I
    if-nez v0, :cond_0

    #@a
    .line 131
    :goto_0
    return v2

    #@b
    .line 117
    :cond_0
    sget-object v4, Landroid/support/v4/content/WakefulBroadcastReceiver;->mActiveWakeLocks:Landroid/util/SparseArray;

    #@d
    monitor-enter v4

    #@e
    .line 118
    :try_start_0
    sget-object v2, Landroid/support/v4/content/WakefulBroadcastReceiver;->mActiveWakeLocks:Landroid/util/SparseArray;

    #@10
    invoke-virtual {v2, v0}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    #@13
    move-result-object v1

    #@14
    check-cast v1, Landroid/os/PowerManager$WakeLock;

    #@16
    .line 119
    .local v1, "wl":Landroid/os/PowerManager$WakeLock;
    if-eqz v1, :cond_1

    #@18
    .line 120
    invoke-virtual {v1}, Landroid/os/PowerManager$WakeLock;->release()V

    #@1b
    .line 121
    sget-object v2, Landroid/support/v4/content/WakefulBroadcastReceiver;->mActiveWakeLocks:Landroid/util/SparseArray;

    #@1d
    invoke-virtual {v2, v0}, Landroid/util/SparseArray;->remove(I)V

    #@20
    .line 122
    monitor-exit v4

    #@21
    move v2, v3

    #@22
    goto :goto_0

    #@23
    .line 130
    :cond_1
    const-string v2, "WakefulBroadcastReceiver"

    #@25
    new-instance v5, Ljava/lang/StringBuilder;

    #@27
    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    #@2a
    const-string v6, "No active wake lock id #"

    #@2c
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2f
    move-result-object v5

    #@30
    invoke-virtual {v5, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@33
    move-result-object v5

    #@34
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@37
    move-result-object v5

    #@38
    invoke-static {v2, v5}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@3b
    .line 131
    monitor-exit v4

    #@3c
    move v2, v3

    #@3d
    goto :goto_0

    #@3e
    .line 132
    .end local v1    # "wl":Landroid/os/PowerManager$WakeLock;
    :catchall_0
    move-exception v2

    #@3f
    monitor-exit v4
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@40
    throw v2
.end method

.method public static startWakefulService(Landroid/content/Context;Landroid/content/Intent;)Landroid/content/ComponentName;
    .locals 8
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "intent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 81
    sget-object v5, Landroid/support/v4/content/WakefulBroadcastReceiver;->mActiveWakeLocks:Landroid/util/SparseArray;

    #@2
    monitor-enter v5

    #@3
    .line 82
    :try_start_0
    sget v1, Landroid/support/v4/content/WakefulBroadcastReceiver;->mNextId:I

    #@5
    .line 83
    .local v1, "id":I
    sget v4, Landroid/support/v4/content/WakefulBroadcastReceiver;->mNextId:I

    #@7
    add-int/lit8 v4, v4, 0x1

    #@9
    sput v4, Landroid/support/v4/content/WakefulBroadcastReceiver;->mNextId:I

    #@b
    .line 84
    sget v4, Landroid/support/v4/content/WakefulBroadcastReceiver;->mNextId:I

    #@d
    if-gtz v4, :cond_0

    #@f
    .line 85
    const/4 v4, 0x1

    #@10
    sput v4, Landroid/support/v4/content/WakefulBroadcastReceiver;->mNextId:I

    #@12
    .line 88
    :cond_0
    const-string v4, "android.support.content.wakelockid"

    #@14
    invoke-virtual {p1, v4, v1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    #@17
    .line 89
    invoke-virtual {p0, p1}, Landroid/content/Context;->startService(Landroid/content/Intent;)Landroid/content/ComponentName;

    #@1a
    move-result-object v0

    #@1b
    .line 90
    .local v0, "comp":Landroid/content/ComponentName;
    if-nez v0, :cond_1

    #@1d
    .line 91
    const/4 v0, 0x0

    #@1e
    monitor-exit v5

    #@1f
    .line 100
    .end local v0    # "comp":Landroid/content/ComponentName;
    :goto_0
    return-object v0

    #@20
    .line 94
    .restart local v0    # "comp":Landroid/content/ComponentName;
    :cond_1
    const-string v4, "power"

    #@22
    invoke-virtual {p0, v4}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@25
    move-result-object v2

    #@26
    check-cast v2, Landroid/os/PowerManager;

    #@28
    .line 95
    .local v2, "pm":Landroid/os/PowerManager;
    const/4 v4, 0x1

    #@29
    new-instance v6, Ljava/lang/StringBuilder;

    #@2b
    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    #@2e
    const-string v7, "wake:"

    #@30
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@33
    move-result-object v6

    #@34
    invoke-virtual {v0}, Landroid/content/ComponentName;->flattenToShortString()Ljava/lang/String;

    #@37
    move-result-object v7

    #@38
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@3b
    move-result-object v6

    #@3c
    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@3f
    move-result-object v6

    #@40
    invoke-virtual {v2, v4, v6}, Landroid/os/PowerManager;->newWakeLock(ILjava/lang/String;)Landroid/os/PowerManager$WakeLock;

    #@43
    move-result-object v3

    #@44
    .line 97
    .local v3, "wl":Landroid/os/PowerManager$WakeLock;
    const/4 v4, 0x0

    #@45
    invoke-virtual {v3, v4}, Landroid/os/PowerManager$WakeLock;->setReferenceCounted(Z)V

    #@48
    .line 98
    const-wide/32 v6, 0xea60

    #@4b
    invoke-virtual {v3, v6, v7}, Landroid/os/PowerManager$WakeLock;->acquire(J)V

    #@4e
    .line 99
    sget-object v4, Landroid/support/v4/content/WakefulBroadcastReceiver;->mActiveWakeLocks:Landroid/util/SparseArray;

    #@50
    invoke-virtual {v4, v1, v3}, Landroid/util/SparseArray;->put(ILjava/lang/Object;)V

    #@53
    .line 100
    monitor-exit v5

    #@54
    goto :goto_0

    #@55
    .line 101
    .end local v0    # "comp":Landroid/content/ComponentName;
    .end local v1    # "id":I
    .end local v2    # "pm":Landroid/os/PowerManager;
    .end local v3    # "wl":Landroid/os/PowerManager$WakeLock;
    :catchall_0
    move-exception v4

    #@56
    monitor-exit v5
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@57
    throw v4
.end method
