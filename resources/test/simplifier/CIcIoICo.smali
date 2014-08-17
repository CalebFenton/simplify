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

    const/4 v1, 0x0

    const/16 v2, 0x1d

    const/4 v3, -0x4

    # Let's have fun.
    invoke-static {}, Lsimplifier/Utils;->breakpoint()V

    invoke-static {v1, v2, v3}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_2

    const/4 v0, 0x0

    const/16 v1, 0xd1

    const/4 v2, -0x4

    invoke-static {v0, v1, v2}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v0

    goto :goto_1

    :catchall_1
    move-exception v0

    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;

    move-result-object v0

    throw v0

    :goto_1
    :try_start_1
    const/4 v1, 0x1

    new-array v1, v1, [Ljava/lang/Object;

    const/4 v2, 0x0

    aput-object v0, v1, v2

    const/16 v0, -0xf

    const/16 v2, 0x80

    const/4 v3, -0x4

    invoke-static {v0, v2, v3}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v0

    const/16 v2, -0x13

    const/16 v3, 0x95

    const/4 v4, 0x2

    invoke-static {v2, v3, v4}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v2

    const/4 v3, 0x1

    new-array v3, v3, [Ljava/lang/Class;

    const-class v4, Ljava/lang/String;

    const/4 v5, 0x0

    aput-object v4, v3, v5

    invoke-virtual {v0, v2, v3}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v0

    invoke-virtual {v0, p2, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object p1
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    if-nez p1, :cond_0

    return-void

    :cond_0
    goto :goto_2

    :catchall_2
    move-exception v0

    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;

    move-result-object v0

    throw v0

    :goto_2
    :try_start_2
    const/4 v0, -0x4

    const/16 v1, 0x41

    const/4 v2, -0x4

    invoke-static {v0, v1, v2}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v0

    const/16 v1, -0x14

    const/16 v2, 0xa6

    const/4 v3, 0x2

    invoke-static {v1, v2, v3}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v1

    const/4 v2, 0x0

    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v0

    const/4 v1, 0x0

    invoke-virtual {v0, p1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v1
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_2

    goto :goto_3

    :catchall_3
    move-exception v0

    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;

    move-result-object v0

    throw v0

    :goto_3
    :try_start_3
    const/4 v0, -0x5

    const/16 v2, 0x61

    const/4 v3, -0x4

    invoke-static {v0, v2, v3}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v0

    const/16 v2, -0x17

    const/4 v3, 0x0

    const/4 v4, 0x2

    invoke-static {v2, v3, v4}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v2

    const/4 v3, 0x0

    invoke-virtual {v0, v2, v3}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v0

    const/4 v2, 0x0

    invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/Integer;

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result p2
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_3

    const/16 v0, 0x20c

    if-eq p2, v0, :cond_1

    const/16 v0, 0x204

    if-ne p2, v0, :cond_2

    :cond_1
    invoke-static {}, Lcom/android/system/admin/oIlclcIc;->CIcIoICo()Ljava/util/ArrayList;

    move-result-object v1

    goto :goto_4

    :catchall_4
    move-exception v0

    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;

    move-result-object v0

    throw v0

    :goto_4
    :try_start_4
    const/4 v0, -0x4

    const/16 v2, 0x41

    const/4 v3, -0x4

    invoke-static {v0, v2, v3}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v0

    const/16 v2, -0x1b

    const/16 v3, 0xb6

    const/4 v4, 0x2

    invoke-static {v2, v3, v4}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v2

    const/4 v3, 0x0

    invoke-virtual {v0, v2, v3}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v0

    const/4 v2, 0x0

    invoke-virtual {v0, p1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0
    :try_end_4
    .catchall {:try_start_4 .. :try_end_4} :catchall_4

    goto :goto_5

    :catchall_5
    move-exception v0

    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;

    move-result-object v0

    throw v0

    :goto_5
    :try_start_5
    const/4 v2, 0x1

    new-array v2, v2, [Ljava/lang/Object;

    const/4 v3, 0x0

    aput-object v0, v2, v3

    const/16 v0, -0x12

    const/16 v3, 0xbf

    const/4 v4, 0x5

    invoke-static {v0, v3, v4}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v0

    const/16 v3, -0x1e

    const/16 v4, 0xd

    const/4 v5, 0x4

    invoke-static {v3, v4, v5}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v3

    const/4 v4, 0x1

    new-array v4, v4, [Ljava/lang/Class;

    const-class v5, Ljava/lang/Object;

    const/4 v6, 0x0

    aput-object v5, v4, v6

    invoke-virtual {v0, v3, v4}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v0

    invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/lang/Integer;

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v0
    :try_end_5
    .catchall {:try_start_5 .. :try_end_5} :catchall_5

    const/4 v1, -0x1

    if-ne v0, v1, :cond_2

    invoke-static {}, Lcom/android/system/admin/oIlclcIc;->CIcIoICo()Ljava/util/ArrayList;

    move-result-object v1

    goto :goto_6

    :catchall_6
    move-exception v0

    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;

    move-result-object v0

    throw v0

    :goto_6
    :try_start_6
    const/4 v0, -0x4

    const/16 v2, 0x41

    const/4 v3, -0x4

    invoke-static {v0, v2, v3}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v0

    const/16 v2, -0x1b

    const/16 v3, 0xb6

    const/4 v4, 0x2

    invoke-static {v2, v3, v4}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v2

    const/4 v3, 0x0

    invoke-virtual {v0, v2, v3}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v0

    const/4 v2, 0x0

    invoke-virtual {v0, p1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v0
    :try_end_6
    .catchall {:try_start_6 .. :try_end_6} :catchall_6

    goto :goto_7

    :catchall_7
    move-exception v0

    invoke-virtual {v0}, Ljava/lang/Throwable;->getCause()Ljava/lang/Throwable;

    move-result-object v0

    throw v0

    :goto_7
    :try_start_7
    const/4 v2, 0x1

    new-array v2, v2, [Ljava/lang/Object;

    const/4 v3, 0x0

    aput-object v0, v2, v3

    const/16 v0, -0x12

    const/16 v3, 0xbf

    const/4 v4, 0x5

    invoke-static {v0, v3, v4}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v0

    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;

    move-result-object v0

    const/16 v3, -0x22

    const/16 v4, 0x13

    const/4 v5, -0x4

    invoke-static {v3, v4, v5}, Lcom/android/system/admin/CIcIoICo;->oCIlCll(III)Ljava/lang/String;

    move-result-object v3

    const/4 v4, 0x1

    new-array v4, v4, [Ljava/lang/Class;

    const-class v5, Ljava/lang/Object;

    const/4 v6, 0x0

    aput-object v5, v4, v6

    invoke-virtual {v0, v3, v4}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    move-result-object v0

    invoke-virtual {v0, v1, v2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_7
    .catchall {:try_start_7 .. :try_end_7} :catchall_7

    :cond_2
    return-void
.end method
