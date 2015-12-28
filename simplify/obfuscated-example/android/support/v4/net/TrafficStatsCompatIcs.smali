.class Landroid/support/v4/net/TrafficStatsCompatIcs;
.super Ljava/lang/Object;
.source "TrafficStatsCompatIcs.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 27
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static clearThreadStatsTag()V
    .locals 0

    #@0
    .prologue
    .line 29
    invoke-static {}, Landroid/net/TrafficStats;->clearThreadStatsTag()V

    #@3
    .line 30
    return-void
.end method

.method public static getThreadStatsTag()I
    .locals 1

    #@0
    .prologue
    .line 33
    invoke-static {}, Landroid/net/TrafficStats;->getThreadStatsTag()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static incrementOperationCount(I)V
    .locals 0
    .param p0, "operationCount"    # I

    #@0
    .prologue
    .line 37
    invoke-static {p0}, Landroid/net/TrafficStats;->incrementOperationCount(I)V

    #@3
    .line 38
    return-void
.end method

.method public static incrementOperationCount(II)V
    .locals 0
    .param p0, "tag"    # I
    .param p1, "operationCount"    # I

    #@0
    .prologue
    .line 41
    invoke-static {p0, p1}, Landroid/net/TrafficStats;->incrementOperationCount(II)V

    #@3
    .line 42
    return-void
.end method

.method public static setThreadStatsTag(I)V
    .locals 0
    .param p0, "tag"    # I

    #@0
    .prologue
    .line 45
    invoke-static {p0}, Landroid/net/TrafficStats;->setThreadStatsTag(I)V

    #@3
    .line 46
    return-void
.end method

.method public static tagSocket(Ljava/net/Socket;)V
    .locals 0
    .param p0, "socket"    # Ljava/net/Socket;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/net/SocketException;
        }
    .end annotation

    #@0
    .prologue
    .line 49
    invoke-static {p0}, Landroid/net/TrafficStats;->tagSocket(Ljava/net/Socket;)V

    #@3
    .line 50
    return-void
.end method

.method public static untagSocket(Ljava/net/Socket;)V
    .locals 0
    .param p0, "socket"    # Ljava/net/Socket;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/net/SocketException;
        }
    .end annotation

    #@0
    .prologue
    .line 53
    invoke-static {p0}, Landroid/net/TrafficStats;->untagSocket(Ljava/net/Socket;)V

    #@3
    .line 54
    return-void
.end method
