.class interface abstract Landroid/support/v4/net/TrafficStatsCompat$TrafficStatsCompatImpl;
.super Ljava/lang/Object;
.source "TrafficStatsCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/net/TrafficStatsCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x608
    name = "TrafficStatsCompatImpl"
.end annotation


# virtual methods
.method public abstract clearThreadStatsTag()V
.end method

.method public abstract getThreadStatsTag()I
.end method

.method public abstract incrementOperationCount(I)V
.end method

.method public abstract incrementOperationCount(II)V
.end method

.method public abstract setThreadStatsTag(I)V
.end method

.method public abstract tagSocket(Ljava/net/Socket;)V
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/net/SocketException;
        }
    .end annotation
.end method

.method public abstract untagSocket(Ljava/net/Socket;)V
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/net/SocketException;
        }
    .end annotation
.end method
