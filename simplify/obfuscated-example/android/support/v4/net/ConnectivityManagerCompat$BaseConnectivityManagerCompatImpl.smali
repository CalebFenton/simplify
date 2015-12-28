.class Landroid/support/v4/net/ConnectivityManagerCompat$BaseConnectivityManagerCompatImpl;
.super Ljava/lang/Object;
.source "ConnectivityManagerCompat.java"

# interfaces
.implements Landroid/support/v4/net/ConnectivityManagerCompat$ConnectivityManagerCompatImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/net/ConnectivityManagerCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "BaseConnectivityManagerCompatImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 37
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public isActiveNetworkMetered(Landroid/net/ConnectivityManager;)Z
    .locals 3
    .param p1, "cm"    # Landroid/net/ConnectivityManager;

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    .line 40
    invoke-virtual {p1}, Landroid/net/ConnectivityManager;->getActiveNetworkInfo()Landroid/net/NetworkInfo;

    #@4
    move-result-object v0

    #@5
    .line 41
    .local v0, "info":Landroid/net/NetworkInfo;
    if-nez v0, :cond_0

    #@7
    .line 54
    :goto_0
    :pswitch_0
    return v2

    #@8
    .line 46
    :cond_0
    invoke-virtual {v0}, Landroid/net/NetworkInfo;->getType()I

    #@b
    move-result v1

    #@c
    .line 47
    .local v1, "type":I
    packed-switch v1, :pswitch_data_0

    #@f
    goto :goto_0

    #@10
    .line 51
    :pswitch_1
    const/4 v2, 0x0

    #@11
    goto :goto_0

    #@12
    .line 47
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method
