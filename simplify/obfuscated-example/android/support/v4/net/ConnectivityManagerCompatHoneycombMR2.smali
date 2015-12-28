.class Landroid/support/v4/net/ConnectivityManagerCompatHoneycombMR2;
.super Ljava/lang/Object;
.source "ConnectivityManagerCompatHoneycombMR2.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 35
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static isActiveNetworkMetered(Landroid/net/ConnectivityManager;)Z
    .locals 3
    .param p0, "cm"    # Landroid/net/ConnectivityManager;

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    .line 37
    invoke-virtual {p0}, Landroid/net/ConnectivityManager;->getActiveNetworkInfo()Landroid/net/NetworkInfo;

    #@4
    move-result-object v0

    #@5
    .line 38
    .local v0, "info":Landroid/net/NetworkInfo;
    if-nez v0, :cond_0

    #@7
    .line 58
    :goto_0
    :pswitch_0
    return v2

    #@8
    .line 43
    :cond_0
    invoke-virtual {v0}, Landroid/net/NetworkInfo;->getType()I

    #@b
    move-result v1

    #@c
    .line 44
    .local v1, "type":I
    packed-switch v1, :pswitch_data_0

    #@f
    :pswitch_1
    goto :goto_0

    #@10
    .line 55
    :pswitch_2
    const/4 v2, 0x0

    #@11
    goto :goto_0

    #@12
    .line 44
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_2
        :pswitch_0
        :pswitch_0
        :pswitch_0
        :pswitch_0
        :pswitch_0
        :pswitch_2
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method
