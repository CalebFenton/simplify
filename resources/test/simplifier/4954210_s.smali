.class public final L”/s;
.super Ljava/lang/Object;
.source "SourceFile"


# static fields
.field private static A:B

.field private static B:Ljava/lang/String;

.field private static C:Z

.field private static D:Z

.field private static E:L”/m;

.field private static F:L”/m;

.field private static G:B

.field private static H:B

.field private static I:I

.field private static J:Ljava/lang/String;

.field private static K:Ljava/lang/String;

.field private static L:J

.field private static M:J

.field private static N:Z

.field private static O:Ljava/lang/String;

.field private static P:Z

.field private static Q:Z

.field private static R:I

.field private static synthetic S:[I

.field private static synthetic T:[I

.field public static a:L”/l;

.field public static b:Landroid/content/Context;

.field public static c:L”/d;

.field public static d:I

.field public static e:Ljava/lang/String;

.field public static f:Z

.field public static g:S

.field public static h:L”/m;

.field public static i:L”/m;

.field public static j:L”/m;

.field public static k:L”/m;

.field public static l:L”/m;

.field public static m:L”/m;

.field public static n:S

.field public static o:S

.field public static p:S

.field public static q:S

.field public static r:S

.field public static s:S

.field public static t:S

.field public static u:S

.field private static v:Landroid/os/PowerManager$WakeLock;

.field private static w:I

.field private static x:Ljava/lang/String;

.field private static y:Ljava/lang/String;

.field private static z:L”/q;


# direct methods
.method static constructor <clinit>()V
    .locals 4

    .prologue
    const/4 v1, -0x1

    const/4 v3, 0x0

    const/4 v2, 0x0

    .line 51
    sput-object v3, L”/s;->v:Landroid/os/PowerManager$WakeLock;

    .line 52
    sput v2, L”/s;->w:I

    .line 54
    sput-object v3, L”/s;->a:L”/l;

    .line 55
    sput-object v3, L”/s;->b:Landroid/content/Context;

    .line 57
    sput-object v3, L”/s;->x:Ljava/lang/String;

    .line 58
    sput-object v3, L”/s;->y:Ljava/lang/String;

    .line 59
    sput-object v3, L”/s;->z:L”/q;

    .line 61
    sput-byte v2, L”/s;->A:B

    .line 62
    const/16 v0, 0x4e20

    sput v0, L”/s;->d:I

    .line 65
    sput-boolean v2, L”/s;->f:Z

    .line 66
    sput-boolean v2, L”/s;->C:Z

    .line 67
    sput-boolean v2, L”/s;->D:Z

    .line 69
    sput-short v2, L”/s;->g:S

    .line 80
    sput-byte v1, L”/s;->G:B

    .line 81
    sput-byte v1, L”/s;->H:B

    .line 93
    sput v2, L”/s;->I:I

    .line 94
    sput-object v3, L”/s;->J:Ljava/lang/String;

    .line 95
    sput-object v3, L”/s;->K:Ljava/lang/String;

    .line 98
    const-wide/16 v0, -0x1

    sput-wide v0, L”/s;->L:J

    .line 99
    const-wide/16 v0, 0x0

    sput-wide v0, L”/s;->M:J

    .line 100
    sput-boolean v2, L”/s;->N:Z

    .line 102
    sput-object v3, L”/s;->O:Ljava/lang/String;

    .line 103
    sput-boolean v2, L”/s;->P:Z

    .line 106
    sput-boolean v2, L”/s;->Q:Z

    .line 108
    sput v2, L”/s;->R:I

    return-void
.end method

.method private static a(B)I
    .locals 3

    .prologue
    .line 764
    const/16 v0, 0x80

    if-ge p0, v0, :cond_1

    const/16 v0, -0x81

    if-le p0, v0, :cond_1

    .line 765
    if-gez p0, :cond_0

    .line 767
    and-int/lit16 p0, p0, 0xff

    .line 773
    :cond_0
    :goto_0
    return p0

    .line 772
    :cond_1
    const/4 v0, 0x0

    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->o:Lcom/mywater/platform/Constant$ErrorFlag;

    const/4 v2, 0x0

    invoke-static {v0, v1, v2}, L”/s;->a(Landroid/content/Context;Lcom/mywater/platform/Constant$ErrorFlag;Z)Z

    .line 773
    const/16 p0, 0x190

    goto :goto_0
.end method

.method private static a(Ljava/lang/StringBuilder;IIB)I
    .locals 4

    .prologue
    .line 2007
    sget-object v0, L”/s;->e:Ljava/lang/String;

    if-eqz v0, :cond_0

    sget-object v0, L”/s;->e:Ljava/lang/String;

    invoke-virtual {v0}, Ljava/lang/String;->length()I

    move-result v0

    if-lez v0, :cond_0

    .line 2008
    sget-object v0, L”/s;->e:Ljava/lang/String;

    sput-object v0, L”/s;->B:Ljava/lang/String;

    .line 2012
    :goto_0
    sget-object v0, L”/s;->F:L”/m;

    sput-object v0, L”/s;->E:L”/m;

    .line 2015
    const-string v0, "%s=%s&%s=%s&%s=%d&%s=%d&%s=%d"

    const/16 v1, 0xa

    new-array v1, v1, [Ljava/lang/Object;

    const/4 v2, 0x0

    .line 2016
    const-string v3, "uuid"

    aput-object v3, v1, v2

    const/4 v2, 0x1

    sget-object v3, L”/s;->B:Ljava/lang/String;

    aput-object v3, v1, v2

    const/4 v2, 0x2

    .line 2017
    const-string v3, "register"

    aput-object v3, v1, v2

    const/4 v2, 0x3

    .line 2018
    sget-object v3, L”/s;->E:L”/m;

    invoke-virtual {v3}, L”/m;->a()Ljava/lang/String;

    move-result-object v3

    aput-object v3, v1, v2

    const/4 v2, 0x4

    .line 2019
    const-string v3, "si"

    aput-object v3, v1, v2

    const/4 v2, 0x5

    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    aput-object v3, v1, v2

    const/4 v2, 0x6

    .line 2020
    const-string v3, "di"

    aput-object v3, v1, v2

    const/4 v2, 0x7

    invoke-static {p2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v3

    aput-object v3, v1, v2

    const/16 v2, 0x8

    .line 2021
    const-string v3, "ct"

    aput-object v3, v1, v2

    const/16 v2, 0x9

    invoke-static {p3}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;

    move-result-object v3

    aput-object v3, v1, v2

    .line 2014
    invoke-static {v0, v1}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v0

    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 2023
    invoke-virtual {p0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/String;->getBytes()[B

    move-result-object v0

    array-length v0, v0

    return v0

    .line 2010
    :cond_0
    const-string v0, "00000000-0000-0000-0000-000000000000"

    sput-object v0, L”/s;->B:Ljava/lang/String;

    goto :goto_0
.end method

.method private static a(J)Ljava/lang/String;
    .locals 5

    .prologue
    .line 291
    invoke-static {}, Ljava/util/Calendar;->getInstance()Ljava/util/Calendar;

    move-result-object v0

    .line 292
    invoke-virtual {v0, p0, p1}, Ljava/util/Calendar;->setTimeInMillis(J)V

    .line 293
    new-instance v1, Ljava/util/Formatter;

    sget-object v2, Ljava/util/Locale;->CHINA:Ljava/util/Locale;

    invoke-direct {v1, v2}, Ljava/util/Formatter;-><init>(Ljava/util/Locale;)V

    .line 294
    const-string v2, "%1$tY%1$tm%1$td%1$tH%1$tM%1$tS"

    const/4 v3, 0x1

    new-array v3, v3, [Ljava/lang/Object;

    const/4 v4, 0x0

    aput-object v0, v3, v4

    invoke-virtual {v1, v2, v3}, Ljava/util/Formatter;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/util/Formatter;

    move-result-object v0

    invoke-virtual {v0}, Ljava/util/Formatter;->toString()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method

.method public static a()V
    .locals 1

    .prologue
    .line 112
    const/4 v0, 0x1

    sput-boolean v0, L”/s;->Q:Z

    .line 113
    return-void
.end method

.method public static a(IIBLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;SB)V
    .locals 8

    .prologue
    .line 1993
    new-instance v5, Ljava/lang/StringBuilder;

    const/16 v0, 0x320

    invoke-direct {v5, v0}, Ljava/lang/StringBuilder;-><init>(I)V

    .line 1996
    new-instance v0, Ljava/lang/StringBuffer;

    const/16 v1, 0x4b0

    invoke-direct {v0, v1}, Ljava/lang/StringBuffer;-><init>(I)V

    .line 1998
    invoke-static {v5, p0, p1, p2}, L”/s;->a(Ljava/lang/StringBuilder;IIB)I

    const-string v1, "&%s=%s&%s=%s&%s=%s&%s=%s&%s=%d&%s=%d&%s=%s&%s=%d&%s&%s&%s&%s=%s&%s=%s&%s=%s&%s=%s&%s=%s"

    const/16 v2, 0x1d

    new-array v2, v2, [Ljava/lang/Object;

    const/4 v3, 0x0

    const-string v4, "port"

    aput-object v4, v2, v3

    const/4 v3, 0x1

    aput-object p5, v2, v3

    const/4 v3, 0x2

    const-string v4, "code"

    aput-object v4, v2, v3

    const/4 v3, 0x3

    aput-object p6, v2, v3

    const/4 v3, 0x4

    const-string v4, "number"

    aput-object v4, v2, v3

    const/4 v3, 0x5

    aput-object p3, v2, v3

    const/4 v3, 0x6

    const-string v4, "content"

    aput-object v4, v2, v3

    const/4 v3, 0x7

    aput-object p4, v2, v3

    const/16 v3, 0x8

    const-string v4, "fee"

    aput-object v4, v2, v3

    const/16 v3, 0x9

    invoke-static {p7}, Ljava/lang/Short;->valueOf(S)Ljava/lang/Short;

    move-result-object v4

    aput-object v4, v2, v3

    const/16 v3, 0xa

    const-string v4, "dt"

    aput-object v4, v2, v3

    const/16 v3, 0xb

    invoke-static/range {p8 .. p8}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;

    move-result-object v4

    aput-object v4, v2, v3

    const/16 v3, 0xc

    const-string v4, "sc"

    aput-object v4, v2, v3

    const/16 v3, 0xd

    sget-object v4, L”/s;->c:L”/d;

    iget-object v4, v4, L”/d;->f:Ljava/lang/String;

    aput-object v4, v2, v3

    const/16 v3, 0xe

    const-string v4, "sim"

    aput-object v4, v2, v3

    const/16 v3, 0xf

    invoke-static {}, L”/s;->o()B

    move-result v4

    invoke-static {v4}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;

    move-result-object v4

    aput-object v4, v2, v3

    const/16 v3, 0x10

    const-string v4, "pt=1"

    aput-object v4, v2, v3

    const/16 v3, 0x11

    const-string v4, "version=2"

    aput-object v4, v2, v3

    const/16 v3, 0x12

    const-string v4, "sys=android"

    aput-object v4, v2, v3

    const/16 v3, 0x13

    const-string v4, "sys_ver"

    aput-object v4, v2, v3

    const/16 v3, 0x14

    invoke-static {}, L”/s;->j()Ljava/lang/String;

    move-result-object v4

    aput-object v4, v2, v3

    const/16 v3, 0x15

    const-string v4, "mobile"

    aput-object v4, v2, v3

    const/16 v3, 0x16

    invoke-static {}, L”/s;->k()Ljava/lang/String;

    move-result-object v4

    aput-object v4, v2, v3

    const/16 v3, 0x17

    const-string v4, "currStart"

    aput-object v4, v2, v3

    const/16 v3, 0x18

    sget-wide v6, L”/s;->M:J

    invoke-static {v6, v7}, L”/s;->a(J)Ljava/lang/String;

    move-result-object v4

    aput-object v4, v2, v3

    const/16 v3, 0x19

    const-string v4, "lastStart"

    aput-object v4, v2, v3

    const/16 v3, 0x1a

    sget-wide v6, L”/s;->L:J

    invoke-static {v6, v7}, L”/s;->a(J)Ljava/lang/String;

    move-result-object v4

    aput-object v4, v2, v3

    const/16 v3, 0x1b

    const-string v4, "lastError"

    aput-object v4, v2, v3

    const/16 v3, 0x1c

    sget-object v4, L”/s;->O:Ljava/lang/String;

    aput-object v4, v2, v3

    invoke-static {v1, v2}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v5, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, "&phone_num="

    invoke-virtual {v5, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    sget-object v1, L”/s;->y:Ljava/lang/String;

    invoke-static {v1}, L”/a;->b(Ljava/lang/String;)Z

    move-result v1

    if-eqz v1, :cond_0

    sget-object v1, L”/s;->y:Ljava/lang/String;

    invoke-virtual {v5, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    :cond_0
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/String;->getBytes()[B

    move-result-object v1

    array-length v7, v1

    .line 1999
    sget-object v1, Lcom/mywater/platform/Constant$ReqType;->b:Lcom/mywater/platform/Constant$ReqType;

    const-string v2, "/plugamaosp/access.action"

    .line 2000
    sget-object v3, Lcom/mywater/platform/Constant;->a:Ljava/lang/String;

    const/4 v4, 0x0

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    const/4 v6, 0x0

    .line 1999
    invoke-static/range {v0 .. v7}, L”/n;->a(Ljava/lang/StringBuffer;Lcom/mywater/platform/Constant$ReqType;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I

    .line 2001
    sget-object v6, Lcom/mywater/platform/Constant;->a:Ljava/lang/String;

    sget-object v1, Lcom/mywater/platform/Constant$NetMode;->a:Lcom/mywater/platform/Constant$NetMode;

    invoke-virtual {v0}, Ljava/lang/StringBuffer;->toString()Ljava/lang/String;

    move-result-object v2

    const/4 v3, 0x0

    const/4 v4, 0x0

    sget-object v5, Lcom/mywater/platform/Constant$NetCallback;->a:Lcom/mywater/platform/Constant$NetCallback;

    move-object v0, v6

    invoke-static/range {v0 .. v5}, L”/n;->a(Ljava/lang/String;Lcom/mywater/platform/Constant$NetMode;Ljava/lang/String;Ljava/lang/String;ILcom/mywater/platform/Constant$NetCallback;)V

    .line 2002
    return-void
.end method

.method public static a(Landroid/content/Context;)V
    .locals 1

    .prologue
    .line 2270
    sget-object v0, L”/s;->b:Landroid/content/Context;

    if-nez v0, :cond_0

    .line 2271
    sput-object p0, L”/s;->b:Landroid/content/Context;

    .line 2273
    const/4 v0, 0x1

    sput-boolean v0, L”/s;->P:Z

    .line 2274
    invoke-static {}, L”/s;->q()V

    .line 2275
    invoke-static {}, L”/s;->p()V

    .line 2279
    :goto_0
    return-void

    .line 2277
    :cond_0
    const/4 v0, 0x0

    sput-boolean v0, L”/s;->P:Z

    goto :goto_0
.end method

.method public static a(Landroid/content/Context;Z)V
    .locals 11

    .prologue
    const-wide/16 v7, 0x0

    const/4 v10, 0x1

    const/4 v9, 0x0

    .line 334
    invoke-static {p0}, L”/s;->b(Landroid/content/Context;)Z

    move-result v0

    if-eqz v0, :cond_1

    .line 335
    invoke-static {v9}, Ljava/lang/System;->exit(I)V

    .line 393
    :cond_0
    :goto_0
    return-void

    .line 340
    :cond_1
    sget-boolean v0, L”/s;->N:Z

    if-nez v0, :cond_0

    .line 342
    sput-boolean v10, L”/s;->N:Z

    .line 349
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "checkServiceNeedStarted entry isTestMode:"

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, p1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 350
    if-eqz p1, :cond_3

    .line 352
    invoke-static {p0}, L”/s;->c(Landroid/content/Context;)Z

    move-result v0

    if-eqz v0, :cond_2

    .line 353
    sput-boolean v9, L”/s;->N:Z

    goto :goto_0

    .line 357
    :cond_2
    sput-boolean v9, L”/s;->N:Z

    .line 358
    invoke-static {p0, v10}, L”/s;->b(Landroid/content/Context;Z)V

    goto :goto_0

    .line 360
    :cond_3
    const-string v0, "zxtdTaskInfo"

    invoke-virtual {p0, v0, v9}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v0

    .line 361
    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    move-result-wide v1

    .line 363
    const-string v3, "time"

    invoke-interface {v0, v3, v7, v8}, Landroid/content/SharedPreferences;->getLong(Ljava/lang/String;J)J

    move-result-wide v3

    .line 364
    new-instance v5, Ljava/lang/StringBuilder;

    const-string v6, "serviceNeedStarted lastTime:"

    invoke-direct {v5, v6}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-static {v3, v4}, L”/s;->a(J)Ljava/lang/String;

    move-result-object v6

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    const-string v6, " currentTime:"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-static {v1, v2}, L”/s;->a(J)Ljava/lang/String;

    move-result-object v6

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 365
    cmp-long v5, v3, v7

    if-eqz v5, :cond_4

    sub-long v5, v1, v3

    invoke-static {v5, v6}, Ljava/lang/Math;->abs(J)J

    move-result-wide v5

    const-wide/32 v7, 0x6ddd00

    cmp-long v5, v5, v7

    if-lez v5, :cond_6

    .line 368
    :cond_4
    invoke-static {p0}, L”/s;->c(Landroid/content/Context;)Z

    move-result v5

    if-eqz v5, :cond_5

    .line 370
    sput-boolean v9, L”/s;->N:Z

    goto :goto_0

    .line 375
    :cond_5
    const-string v5, "cscene"

    invoke-interface {v0, v5, v9}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v5

    sput v5, L”/s;->R:I

    .line 377
    invoke-interface {v0}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    .line 378
    const-string v5, "time"

    invoke-interface {v0, v5, v1, v2}, Landroid/content/SharedPreferences$Editor;->putLong(Ljava/lang/String;J)Landroid/content/SharedPreferences$Editor;

    .line 379
    const-string v5, "curr"

    invoke-interface {v0, v5, v1, v2}, Landroid/content/SharedPreferences$Editor;->putLong(Ljava/lang/String;J)Landroid/content/SharedPreferences$Editor;

    .line 380
    const-string v1, "last"

    invoke-interface {v0, v1, v3, v4}, Landroid/content/SharedPreferences$Editor;->putLong(Ljava/lang/String;J)Landroid/content/SharedPreferences$Editor;

    .line 381
    const-string v1, "job"

    invoke-interface {v0, v1, v10}, Landroid/content/SharedPreferences$Editor;->putBoolean(Ljava/lang/String;Z)Landroid/content/SharedPreferences$Editor;

    .line 382
    invoke-interface {v0}, Landroid/content/SharedPreferences$Editor;->commit()Z

    .line 384
    sput-boolean v9, L”/s;->N:Z

    .line 386
    invoke-static {p0, v9}, L”/s;->b(Landroid/content/Context;Z)V

    goto/16 :goto_0

    .line 389
    :cond_6
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "no need to start service jobCircleRunning:"

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-boolean v1, L”/s;->N:Z

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 390
    sput-boolean v9, L”/s;->N:Z

    goto/16 :goto_0
.end method

.method public static a(Lcom/mywater/platform/Constant$NetCallback;L”/o;Ljava/lang/Boolean;[BI)V
    .locals 10

    .prologue
    const/16 v5, 0xbb8

    const/4 v9, 0x2

    const/4 v8, 0x0

    const/4 v1, 0x1

    const/4 v2, 0x0

    .line 438
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v3, "netCallback :"

    invoke-direct {v0, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {p0}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v3

    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 440
    invoke-static {}, L”/s;->s()[I

    move-result-object v0

    invoke-virtual {p0}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v3

    aget v0, v0, v3

    packed-switch v0, :pswitch_data_0

    .line 491
    :cond_0
    :goto_0
    return-void

    .line 442
    :pswitch_0
    invoke-virtual {p2}, Ljava/lang/Boolean;->booleanValue()Z

    move-result v3

    if-eqz v3, :cond_6

    new-instance v0, Ljava/lang/StringBuilder;

    const-string v4, "recvNetDataCallback recvBuf:"

    invoke-direct {v0, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    new-instance v4, Ljava/lang/String;

    invoke-direct {v4, p3, v2, p4}, Ljava/lang/String;-><init>([BII)V

    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    const-string v0, "\r\n\r\n"

    invoke-virtual {v0}, Ljava/lang/String;->getBytes()[B

    move-result-object v0

    const-string v4, "\r\n\r\n"

    invoke-virtual {v4}, Ljava/lang/String;->getBytes()[B

    move-result-object v4

    array-length v4, v4

    invoke-static {p3, p4, v0, v4}, L”/a;->a([BI[BI)I

    move-result v0

    add-int/lit8 v0, v0, 0x4

    invoke-static {p3, v0}, L”/s;->a([BI)V

    sget-object v4, L”/s;->B:Ljava/lang/String;

    sget-object v5, L”/s;->E:L”/m;

    invoke-virtual {v4}, Ljava/lang/String;->length()I

    move-result v0

    int-to-byte v0, v0

    const/16 v6, 0x28

    if-ge v0, v6, :cond_2

    const/16 v6, 0x19

    if-le v0, v6, :cond_2

    iget-short v0, v5, L”/m;->a:S

    const/16 v6, 0x7da

    if-le v0, v6, :cond_2

    iget-short v0, v5, L”/m;->a:S

    const/16 v6, 0x802

    if-ge v0, v6, :cond_2

    iget-byte v0, v5, L”/m;->b:B

    if-lez v0, :cond_2

    iget-byte v0, v5, L”/m;->b:B

    const/16 v6, 0xd

    if-ge v0, v6, :cond_2

    iget-byte v0, v5, L”/m;->c:B

    const/16 v6, 0x20

    if-ge v0, v6, :cond_2

    iget-byte v0, v5, L”/m;->c:B

    if-lez v0, :cond_2

    sget-object v0, L”/s;->F:L”/m;

    if-eqz v0, :cond_8

    if-ne v5, v0, :cond_7

    move v0, v1

    :goto_1
    if-eqz v0, :cond_1

    sget-object v0, L”/s;->e:Ljava/lang/String;

    invoke-virtual {v4, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-nez v0, :cond_2

    :cond_1
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v6, "save user info to nv:"

    invoke-direct {v0, v6}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v6, " time:"

    invoke-virtual {v0, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v5}, L”/m;->a()Ljava/lang/String;

    move-result-object v6

    invoke-virtual {v0, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    sget-object v0, L”/s;->z:L”/q;

    iput-object v4, v0, L”/q;->o:Ljava/lang/String;

    sget-object v0, L”/s;->z:L”/q;

    iput-object v5, v0, L”/q;->p:L”/m;

    sput-object v4, L”/s;->e:Ljava/lang/String;

    sput-object v5, L”/s;->F:L”/m;

    sget-object v0, L”/s;->z:L”/q;

    invoke-static {v0}, L”/s;->a(L”/q;)V

    :cond_2
    invoke-static {}, L”/x;->d()I

    move-result v0

    if-gtz v0, :cond_3

    invoke-static {}, L”/x;->e()I

    move-result v0

    if-lez v0, :cond_4

    :cond_3
    sget-byte v0, L”/s;->A:B

    if-ne v0, v1, :cond_4

    sget-byte v0, L”/s;->H:B

    if-nez v0, :cond_4

    invoke-static {}, L”/s;->m()V

    :cond_4
    sget v0, L”/s;->d:I

    if-nez v0, :cond_5

    const/16 v0, 0x4e20

    sput v0, L”/s;->d:I

    :cond_5
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v4, "ispay: "

    invoke-direct {v0, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-byte v4, L”/s;->A:B

    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v4, "  chartype: "

    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    sget-byte v4, L”/s;->G:B

    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v4, "  TimeToSendSms:"

    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    sget v4, L”/s;->d:I

    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    sget-byte v0, L”/s;->A:B

    if-ne v0, v1, :cond_a

    sget-object v0, Lcom/mywater/platform/Constant$ErrorFlag;->K:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-static {v8, v0, v2}, L”/s;->a(Landroid/content/Context;Lcom/mywater/platform/Constant$ErrorFlag;Z)Z

    sget-byte v0, L”/s;->G:B

    if-nez v0, :cond_9

    sget-object v0, L”/s;->a:L”/l;

    sget v0, L”/s;->d:I

    sget-object v1, Lcom/mywater/platform/Constant$MsgHandlerId;->g:Lcom/mywater/platform/Constant$MsgHandlerId;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$MsgHandlerId;->ordinal()I

    move-result v1

    invoke-static {v8, v0, v1}, L”/l;->a(Ljava/lang/Object;II)V

    :cond_6
    :goto_2
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "recvNetDataCallBack finished ret:"

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    goto/16 :goto_0

    :cond_7
    iget-short v6, v5, L”/m;->a:S

    iget-short v7, v0, L”/m;->a:S

    if-ne v6, v7, :cond_8

    iget-byte v6, v5, L”/m;->b:B

    iget-byte v7, v0, L”/m;->b:B

    if-ne v6, v7, :cond_8

    iget-byte v6, v5, L”/m;->c:B

    iget-byte v7, v0, L”/m;->c:B

    if-ne v6, v7, :cond_8

    iget-byte v6, v5, L”/m;->d:B

    iget-byte v7, v0, L”/m;->d:B

    if-ne v6, v7, :cond_8

    iget-byte v6, v5, L”/m;->e:B

    iget-byte v0, v0, L”/m;->e:B

    if-ne v6, v0, :cond_8

    move v0, v1

    goto/16 :goto_1

    :cond_8
    move v0, v2

    goto/16 :goto_1

    :cond_9
    sget-byte v0, L”/s;->G:B

    if-eq v0, v1, :cond_6

    sget-byte v0, L”/s;->G:B

    if-ne v0, v9, :cond_6

    sget-object v0, L”/s;->a:L”/l;

    sget v0, L”/s;->d:I

    sget-object v1, Lcom/mywater/platform/Constant$MsgHandlerId;->h:Lcom/mywater/platform/Constant$MsgHandlerId;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$MsgHandlerId;->ordinal()I

    move-result v1

    invoke-static {v8, v0, v1}, L”/l;->a(Ljava/lang/Object;II)V

    goto :goto_2

    :cond_a
    invoke-static {v2}, L”/s;->a(Z)V

    goto :goto_2

    .line 446
    :pswitch_1
    invoke-static {p1, p2, p3, p4}, L”/v;->b(L”/o;Ljava/lang/Boolean;[BI)V

    goto/16 :goto_0

    .line 450
    :pswitch_2
    invoke-static {p1, p2, p3, p4}, L”/v;->a(L”/o;Ljava/lang/Boolean;[BI)V

    goto/16 :goto_0

    .line 454
    :pswitch_3
    invoke-static {p1, p2}, L”/v;->a(L”/o;Ljava/lang/Boolean;)V

    goto/16 :goto_0

    .line 458
    :pswitch_4
    invoke-static {p1, p2, p3, p4}, L”/v;->c(L”/o;Ljava/lang/Boolean;[BI)V

    goto/16 :goto_0

    .line 462
    :pswitch_5
    invoke-static {p1, p2, p3, p4}, L”/v;->d(L”/o;Ljava/lang/Boolean;[BI)V

    goto/16 :goto_0

    .line 466
    :pswitch_6
    invoke-static {p1, p2, p3, p4}, L”/v;->e(L”/o;Ljava/lang/Boolean;[BI)V

    goto/16 :goto_0

    .line 470
    :pswitch_7
    invoke-static {p1, p2}, L”/v;->b(L”/o;Ljava/lang/Boolean;)V

    goto/16 :goto_0

    .line 474
    :pswitch_8
    invoke-static {p1, p2}, L”/v;->c(L”/o;Ljava/lang/Boolean;)V

    goto/16 :goto_0

    .line 478
    :pswitch_9
    invoke-static {p1, p2, p3, p4}, L”/v;->f(L”/o;Ljava/lang/Boolean;[BI)V

    goto/16 :goto_0

    .line 482
    :pswitch_a
    invoke-static {p1, p2}, L”/v;->d(L”/o;Ljava/lang/Boolean;)V

    goto/16 :goto_0

    .line 486
    :pswitch_b
    invoke-static {p1, p2}, L”/v;->e(L”/o;Ljava/lang/Boolean;)V

    goto/16 :goto_0

    .line 490
    :pswitch_c
    invoke-virtual {p2}, Ljava/lang/Boolean;->booleanValue()Z

    move-result v0

    if-eqz v0, :cond_0

    new-instance v0, Ljava/lang/StringBuilder;

    const-string v3, "netHostErrorCallback--->recvBuf:"

    invoke-direct {v0, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    new-instance v3, Ljava/lang/String;

    invoke-direct {v3, p3, v2, p4}, Ljava/lang/String;-><init>([BII)V

    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    const-string v0, "\r\n\r\n"

    invoke-virtual {v0}, Ljava/lang/String;->getBytes()[B

    move-result-object v0

    const-string v3, "\r\n\r\n"

    invoke-virtual {v3}, Ljava/lang/String;->getBytes()[B

    move-result-object v3

    array-length v3, v3

    invoke-static {p3, p4, v0, v3}, L”/a;->a([BI[BI)I

    move-result v0

    add-int/lit8 v0, v0, 0x4

    const/4 v3, 0x4

    invoke-static {p3, v0, v3}, L”/a;->a([BII)I

    move-result v3

    const/16 v4, 0x3ef

    if-ne v3, v4, :cond_d

    add-int/lit8 v0, v0, 0x4

    aget-byte v3, p3, v0

    add-int/lit8 v0, v0, 0x1

    if-ne v3, v1, :cond_b

    aget-byte v1, p3, v0

    add-int/lit8 v0, v0, 0x1

    new-instance v2, Ljava/lang/String;

    invoke-direct {v2, p3, v0, v1}, Ljava/lang/String;-><init>([BII)V

    invoke-static {v2}, L”/a;->b(Ljava/lang/String;)Z

    move-result v0

    if-eqz v0, :cond_0

    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "netHostErrorCallback:save usable host-->"

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    sget-object v0, L”/s;->b:Landroid/content/Context;

    const-string v1, "newHost"

    invoke-static {v0, v1, v2}, L”/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)V

    sget-object v0, L”/s;->a:L”/l;

    sget-object v0, Lcom/mywater/platform/Constant$MsgHandlerId;->f:Lcom/mywater/platform/Constant$MsgHandlerId;

    invoke-virtual {v0}, Lcom/mywater/platform/Constant$MsgHandlerId;->ordinal()I

    move-result v0

    invoke-static {v8, v5, v0}, L”/l;->a(Ljava/lang/Object;II)V

    goto/16 :goto_0

    :cond_b
    if-ne v3, v9, :cond_c

    sget-object v0, L”/s;->b:Landroid/content/Context;

    const-string v1, "newHost"

    invoke-static {v0, v1, v8}, L”/a;->a(Landroid/content/Context;Ljava/lang/String;Ljava/lang/String;)V

    sget-object v0, L”/s;->a:L”/l;

    sget-object v0, Lcom/mywater/platform/Constant$MsgHandlerId;->f:Lcom/mywater/platform/Constant$MsgHandlerId;

    invoke-virtual {v0}, Lcom/mywater/platform/Constant$MsgHandlerId;->ordinal()I

    move-result v0

    invoke-static {v8, v5, v0}, L”/l;->a(Ljava/lang/Object;II)V

    goto/16 :goto_0

    :cond_c
    invoke-static {v2}, L”/s;->a(Z)V

    goto/16 :goto_0

    :cond_d
    invoke-static {v2}, L”/s;->a(Z)V

    goto/16 :goto_0

    .line 440
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_3
        :pswitch_4
        :pswitch_5
        :pswitch_6
        :pswitch_7
        :pswitch_8
        :pswitch_9
        :pswitch_a
        :pswitch_b
        :pswitch_c
    .end packed-switch
.end method

.method public static a(Lcom/mywater/platform/Constant$NvIndex;)V
    .locals 17

    .prologue
    .line 618
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "loadInfoFromNv:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    move-object/from16 v0, p0

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 619
    invoke-static {}, L”/s;->l()L”/q;

    move-result-object v1

    sput-object v1, L”/s;->z:L”/q;

    .line 621
    sget-object v1, Lcom/mywater/platform/Constant$NvIndex;->f:Lcom/mywater/platform/Constant$NvIndex;

    move-object/from16 v0, p0

    if-ne v0, v1, :cond_1

    .line 622
    sget-object v1, L”/s;->z:L”/q;

    iget-object v1, v1, L”/q;->o:Ljava/lang/String;

    sput-object v1, L”/s;->e:Ljava/lang/String;

    .line 623
    new-instance v1, L”/m;

    sget-object v2, L”/s;->z:L”/q;

    iget-object v2, v2, L”/q;->p:L”/m;

    invoke-direct {v1, v2}, L”/m;-><init>(L”/m;)V

    sput-object v1, L”/s;->F:L”/m;

    .line 624
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "nv read uuid:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-object v2, L”/s;->e:Ljava/lang/String;

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 625
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "nv read registerTimeNv:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-object v2, L”/s;->F:L”/m;

    invoke-virtual {v2}, L”/m;->a()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 683
    :cond_0
    :goto_0
    return-void

    .line 625
    :cond_1
    sget-object v1, Lcom/mywater/platform/Constant$NvIndex;->g:Lcom/mywater/platform/Constant$NvIndex;

    move-object/from16 v0, p0

    if-ne v0, v1, :cond_2

    .line 629
    sget-object v1, L”/s;->z:L”/q;

    iget-byte v1, v1, L”/q;->q:B

    .line 630
    new-instance v2, Ljava/lang/StringBuilder;

    const-string v3, "nv getSpNumber:"

    invoke-direct {v2, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 631
    if-lez v1, :cond_0

    .line 633
    sget-object v2, L”/s;->z:L”/q;

    iget-object v0, v2, L”/q;->r:[L”/r;

    move-object/from16 v16, v0

    move v15, v1

    .line 635
    :goto_1
    if-lez v15, :cond_0

    .line 636
    add-int/lit8 v1, v15, -0x1

    :try_start_0
    aget-object v1, v16, v1

    iget-object v3, v1, L”/r;->a:Ljava/lang/String;

    .line 637
    add-int/lit8 v1, v15, -0x1

    aget-object v1, v16, v1

    iget-object v6, v1, L”/r;->b:Ljava/lang/String;

    .line 638
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "down sms number:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    .line 639
    const-string v2, " content:"

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 638
    const/16 v1, 0x1f

    const/4 v2, 0x0

    const/4 v4, 0x0

    const/4 v5, 0x0

    .line 642
    const/4 v7, 0x0

    const/4 v8, 0x0

    const/4 v9, 0x0

    const/4 v10, 0x0

    const/4 v11, 0x0

    const/4 v12, 0x0

    const/4 v13, 0x0

    .line 643
    const/4 v14, 0x0

    .line 641
    invoke-static/range {v1 .. v14}, L”/x;->a(BLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;SLjava/lang/String;Ljava/lang/String;IIBZ)Lcom/mywater/platform/sms/ChargeSmsData;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 644
    add-int/lit8 v1, v15, -0x1

    move v15, v1

    goto :goto_1

    .line 647
    :catch_0
    move-exception v1

    invoke-virtual {v1}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    goto :goto_0

    .line 650
    :cond_2
    invoke-virtual/range {p0 .. p0}, Lcom/mywater/platform/Constant$NvIndex;->ordinal()I

    move-result v1

    sget-object v2, Lcom/mywater/platform/Constant$NvIndex;->e:Lcom/mywater/platform/Constant$NvIndex;

    invoke-virtual {v2}, Lcom/mywater/platform/Constant$NvIndex;->ordinal()I

    move-result v2

    if-ge v1, v2, :cond_6

    .line 651
    sget-object v1, Lcom/mywater/platform/Constant$NvIndex;->d:Lcom/mywater/platform/Constant$NvIndex;

    move-object/from16 v0, p0

    if-ne v0, v1, :cond_3

    .line 652
    sget-object v1, L”/s;->z:L”/q;

    iget-short v1, v1, L”/q;->j:S

    sput-short v1, L”/s;->q:S

    .line 653
    sget-object v1, L”/s;->z:L”/q;

    iget-short v1, v1, L”/q;->k:S

    sput-short v1, L”/s;->u:S

    .line 654
    sget-object v1, L”/s;->z:L”/q;

    iget-object v1, v1, L”/q;->l:L”/m;

    sput-object v1, L”/s;->m:L”/m;

    .line 655
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "load chargeSceneOne has payed money:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    .line 657
    sget-short v2, L”/s;->q:S

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    .line 656
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    goto/16 :goto_0

    .line 655
    :cond_3
    sget-object v1, Lcom/mywater/platform/Constant$NvIndex;->a:Lcom/mywater/platform/Constant$NvIndex;

    move-object/from16 v0, p0

    if-ne v0, v1, :cond_4

    .line 659
    sget-object v1, L”/s;->z:L”/q;

    iget-short v1, v1, L”/q;->a:S

    sput-short v1, L”/s;->n:S

    .line 660
    sget-object v1, L”/s;->z:L”/q;

    iget-short v1, v1, L”/q;->b:S

    sput-short v1, L”/s;->r:S

    .line 661
    sget-object v1, L”/s;->z:L”/q;

    iget-object v1, v1, L”/q;->c:L”/m;

    sput-object v1, L”/s;->j:L”/m;

    .line 662
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "load chargeSceneZero1 has payed money:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    .line 664
    sget-short v2, L”/s;->n:S

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    .line 663
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    goto/16 :goto_0

    .line 662
    :cond_4
    sget-object v1, Lcom/mywater/platform/Constant$NvIndex;->b:Lcom/mywater/platform/Constant$NvIndex;

    move-object/from16 v0, p0

    if-ne v0, v1, :cond_5

    .line 666
    sget-object v1, L”/s;->z:L”/q;

    iget-short v1, v1, L”/q;->d:S

    sput-short v1, L”/s;->o:S

    .line 667
    sget-object v1, L”/s;->z:L”/q;

    iget-short v1, v1, L”/q;->e:S

    sput-short v1, L”/s;->s:S

    .line 668
    sget-object v1, L”/s;->z:L”/q;

    iget-object v1, v1, L”/q;->f:L”/m;

    sput-object v1, L”/s;->k:L”/m;

    .line 669
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "load chargeSceneZero2 has payed money:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    .line 671
    sget-short v2, L”/s;->o:S

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    .line 670
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    goto/16 :goto_0

    .line 669
    :cond_5
    sget-object v1, Lcom/mywater/platform/Constant$NvIndex;->c:Lcom/mywater/platform/Constant$NvIndex;

    move-object/from16 v0, p0

    if-ne v0, v1, :cond_0

    .line 673
    sget-object v1, L”/s;->z:L”/q;

    iget-short v1, v1, L”/q;->g:S

    sput-short v1, L”/s;->p:S

    .line 674
    sget-object v1, L”/s;->z:L”/q;

    iget-short v1, v1, L”/q;->h:S

    sput-short v1, L”/s;->t:S

    .line 675
    sget-object v1, L”/s;->z:L”/q;

    iget-object v1, v1, L”/q;->i:L”/m;

    sput-object v1, L”/s;->l:L”/m;

    .line 676
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "load chargeSceneZero3 has payed money:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    .line 678
    sget-short v2, L”/s;->p:S

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    .line 677
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    goto/16 :goto_0

    .line 676
    :cond_6
    sget-object v1, Lcom/mywater/platform/Constant$NvIndex;->e:Lcom/mywater/platform/Constant$NvIndex;

    move-object/from16 v0, p0

    if-ne v0, v1, :cond_0

    .line 681
    sget-object v1, L”/s;->z:L”/q;

    iget-short v1, v1, L”/q;->m:S

    sput-short v1, L”/s;->g:S

    .line 682
    sget-object v1, L”/s;->z:L”/q;

    iget-object v1, v1, L”/q;->n:L”/m;

    sput-object v1, L”/s;->h:L”/m;

    .line 683
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "load currMonthSendSmsNum:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    .line 684
    sget-short v2, L”/s;->g:S

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    goto/16 :goto_0
.end method

.method private static a(L”/q;)V
    .locals 6

    .prologue
    const/4 v5, 0x0

    .line 560
    sget-object v0, L”/s;->b:Landroid/content/Context;

    const-string v1, "NvInfo"

    invoke-virtual {v0, v1, v5}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v0

    invoke-interface {v0}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;

    move-result-object v1

    const-string v0, "tp1"

    iget-short v2, p0, L”/q;->a:S

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    const-string v0, "mp1"

    iget-short v2, p0, L”/q;->b:S

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    const-string v0, "lpt1"

    iget-object v2, p0, L”/q;->c:L”/m;

    invoke-virtual {v2}, L”/m;->a()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    const-string v0, "tp2"

    iget-short v2, p0, L”/q;->d:S

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    const-string v0, "mp2"

    iget-short v2, p0, L”/q;->e:S

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    const-string v0, "lpt2"

    iget-object v2, p0, L”/q;->f:L”/m;

    invoke-virtual {v2}, L”/m;->a()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    const-string v0, "tp3"

    iget-short v2, p0, L”/q;->g:S

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    const-string v0, "mp3"

    iget-short v2, p0, L”/q;->h:S

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    const-string v0, "lpt3"

    iget-object v2, p0, L”/q;->i:L”/m;

    invoke-virtual {v2}, L”/m;->a()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    const-string v0, "tp"

    iget-short v2, p0, L”/q;->j:S

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    const-string v0, "mp"

    iget-short v2, p0, L”/q;->k:S

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    const-string v0, "lpt"

    iget-object v2, p0, L”/q;->l:L”/m;

    invoke-virtual {v2}, L”/m;->a()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    const-string v0, "sc"

    iget-short v2, p0, L”/q;->m:S

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    const-string v0, "lst"

    iget-object v2, p0, L”/q;->n:L”/m;

    invoke-virtual {v2}, L”/m;->a()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    const-string v0, "uuid"

    iget-object v2, p0, L”/q;->o:Ljava/lang/String;

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    const-string v0, "rt"

    iget-object v2, p0, L”/q;->p:L”/m;

    invoke-virtual {v2}, L”/m;->a()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v1, v0, v2}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    iget-object v2, p0, L”/q;->r:[L”/r;

    iget-byte v0, p0, L”/q;->q:B

    if-lez v0, :cond_0

    if-eqz v2, :cond_0

    const-string v0, "spn"

    iget-byte v3, p0, L”/q;->q:B

    invoke-interface {v1, v0, v3}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    iget-byte v0, p0, L”/q;->q:B

    :goto_0
    if-gtz v0, :cond_2

    :cond_0
    invoke-interface {v1}, Landroid/content/SharedPreferences$Editor;->commit()Z

    move-result v0

    .line 561
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "writeNV2Pref ret:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 562
    if-nez v0, :cond_1

    .line 563
    const/4 v0, 0x0

    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->C:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-static {v0, v1, v5}, L”/s;->a(Landroid/content/Context;Lcom/mywater/platform/Constant$ErrorFlag;Z)Z

    .line 565
    :cond_1
    return-void

    .line 560
    :cond_2
    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "sp"

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, "n"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    add-int/lit8 v4, v0, -0x1

    aget-object v4, v2, v4

    iget-object v4, v4, L”/r;->a:Ljava/lang/String;

    invoke-interface {v1, v3, v4}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "sp"

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, "c"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    add-int/lit8 v4, v0, -0x1

    aget-object v4, v2, v4

    iget-object v4, v4, L”/r;->b:Ljava/lang/String;

    invoke-interface {v1, v3, v4}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    add-int/lit8 v0, v0, -0x1

    goto :goto_0
.end method

.method public static a(SL”/m;)V
    .locals 1

    .prologue
    .line 754
    sget-object v0, L”/s;->z:L”/q;

    iput-short p0, v0, L”/q;->m:S

    .line 755
    sget-object v0, L”/s;->z:L”/q;

    iput-object p1, v0, L”/q;->n:L”/m;

    .line 756
    sget-object v0, L”/s;->z:L”/q;

    invoke-static {v0}, L”/s;->a(L”/q;)V

    .line 757
    return-void
.end method

.method public static a(SSL”/m;Lcom/mywater/platform/Constant$NvIndex;)V
    .locals 2

    .prologue
    .line 732
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "saveChargeInfToNv:"

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, p3}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 733
    sget-object v0, Lcom/mywater/platform/Constant$NvIndex;->d:Lcom/mywater/platform/Constant$NvIndex;

    if-ne p3, v0, :cond_1

    .line 734
    sget-object v0, L”/s;->z:L”/q;

    iput-short p0, v0, L”/q;->j:S

    .line 735
    sget-object v0, L”/s;->z:L”/q;

    iput-short p1, v0, L”/q;->k:S

    .line 736
    sget-object v0, L”/s;->z:L”/q;

    iput-object p2, v0, L”/q;->l:L”/m;

    .line 750
    :cond_0
    :goto_0
    sget-object v0, L”/s;->z:L”/q;

    invoke-static {v0}, L”/s;->a(L”/q;)V

    .line 751
    return-void

    .line 737
    :cond_1
    sget-object v0, Lcom/mywater/platform/Constant$NvIndex;->a:Lcom/mywater/platform/Constant$NvIndex;

    if-ne p3, v0, :cond_2

    .line 738
    sget-object v0, L”/s;->z:L”/q;

    iput-short p0, v0, L”/q;->a:S

    .line 739
    sget-object v0, L”/s;->z:L”/q;

    iput-short p1, v0, L”/q;->b:S

    .line 740
    sget-object v0, L”/s;->z:L”/q;

    iput-object p2, v0, L”/q;->c:L”/m;

    goto :goto_0

    .line 741
    :cond_2
    sget-object v0, Lcom/mywater/platform/Constant$NvIndex;->b:Lcom/mywater/platform/Constant$NvIndex;

    if-ne p3, v0, :cond_3

    .line 742
    sget-object v0, L”/s;->z:L”/q;

    iput-short p0, v0, L”/q;->d:S

    .line 743
    sget-object v0, L”/s;->z:L”/q;

    iput-short p1, v0, L”/q;->e:S

    .line 744
    sget-object v0, L”/s;->z:L”/q;

    iput-object p2, v0, L”/q;->f:L”/m;

    goto :goto_0

    .line 745
    :cond_3
    sget-object v0, Lcom/mywater/platform/Constant$NvIndex;->c:Lcom/mywater/platform/Constant$NvIndex;

    if-ne p3, v0, :cond_0

    .line 746
    sget-object v0, L”/s;->z:L”/q;

    iput-short p0, v0, L”/q;->g:S

    .line 747
    sget-object v0, L”/s;->z:L”/q;

    iput-short p1, v0, L”/q;->h:S

    .line 748
    sget-object v0, L”/s;->z:L”/q;

    iput-object p2, v0, L”/q;->i:L”/m;

    goto :goto_0
.end method

.method public static a(Z)V
    .locals 4

    .prologue
    const/4 v1, 0x1

    const/4 v2, 0x0

    .line 2221
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v3, "releaseCPUWakeLock cpuLockCount: "

    invoke-direct {v0, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget v3, L”/s;->w:I

    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v3, " cpuWakeLock==null?:"

    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    sget-object v0, L”/s;->v:Landroid/os/PowerManager$WakeLock;

    if-nez v0, :cond_2

    move v0, v1

    :goto_0
    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 2222
    sget v0, L”/s;->w:I

    if-lez v0, :cond_1

    .line 2223
    if-eqz p0, :cond_3

    sget-object v0, L”/s;->v:Landroid/os/PowerManager$WakeLock;

    if-eqz v0, :cond_3

    .line 2224
    sget-object v0, L”/s;->v:Landroid/os/PowerManager$WakeLock;

    invoke-virtual {v0}, Landroid/os/PowerManager$WakeLock;->isHeld()Z

    move-result v0

    if-eqz v0, :cond_0

    .line 2225
    sget-object v0, L”/s;->v:Landroid/os/PowerManager$WakeLock;

    invoke-virtual {v0}, Landroid/os/PowerManager$WakeLock;->release()V

    .line 2227
    :cond_0
    sput v2, L”/s;->w:I

    .line 2248
    :cond_1
    :goto_1
    return-void

    :cond_2
    move v0, v2

    .line 2221
    goto :goto_0

    .line 2229
    :cond_3
    sget v0, L”/s;->w:I

    if-ne v0, v1, :cond_5

    sget-object v0, L”/s;->v:Landroid/os/PowerManager$WakeLock;

    if-eqz v0, :cond_5

    .line 2230
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "isheld():"

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-object v1, L”/s;->v:Landroid/os/PowerManager$WakeLock;

    invoke-virtual {v1}, Landroid/os/PowerManager$WakeLock;->isHeld()Z

    move-result v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 2231
    sget-object v0, L”/s;->v:Landroid/os/PowerManager$WakeLock;

    invoke-virtual {v0}, Landroid/os/PowerManager$WakeLock;->isHeld()Z

    move-result v0

    if-eqz v0, :cond_4

    .line 2232
    sget-object v0, L”/s;->v:Landroid/os/PowerManager$WakeLock;

    invoke-virtual {v0}, Landroid/os/PowerManager$WakeLock;->release()V

    .line 2234
    :cond_4
    sput v2, L”/s;->w:I

    .line 2235
    sget-object v0, L”/s;->a:L”/l;

    new-instance v1, L”/t;

    invoke-direct {v1}, L”/t;-><init>()V

    .line 2242
    const-wide/16 v2, 0x32c8

    .line 2236
    invoke-virtual {v0, v1, v2, v3}, L”/l;->postDelayed(Ljava/lang/Runnable;J)Z

    .line 2243
    sget-object v0, L”/s;->a:L”/l;

    const/4 v0, 0x0

    const/16 v1, 0x3a98

    sget-object v2, Lcom/mywater/platform/Constant$MsgHandlerId;->m:Lcom/mywater/platform/Constant$MsgHandlerId;

    invoke-virtual {v2}, Lcom/mywater/platform/Constant$MsgHandlerId;->ordinal()I

    move-result v2

    invoke-static {v0, v1, v2}, L”/l;->a(Ljava/lang/Object;II)V

    goto :goto_1

    .line 2246
    :cond_5
    sget v0, L”/s;->w:I

    add-int/lit8 v0, v0, -0x1

    sput v0, L”/s;->w:I

    goto :goto_1
.end method

.method private static a([BI)V
    .locals 28

    .prologue
    .line 780
    .line 785
    const/4 v2, -0x1

    sput-byte v2, L”/s;->G:B

    .line 786
    const/4 v2, -0x1

    sput-byte v2, L”/s;->H:B

    .line 787
    const/4 v2, -0x1

    sput-byte v2, L”/s;->A:B

    .line 790
    const/4 v2, 0x4

    move-object/from16 v0, p0

    move/from16 v1, p1

    invoke-static {v0, v1, v2}, L”/a;->a([BII)I

    move-result v2

    const/16 v3, 0x3ef

    if-eq v2, v3, :cond_1

    .line 1907
    :cond_0
    :goto_0
    return-void

    .line 794
    :cond_1
    add-int/lit8 v2, p1, 0x4

    .line 797
    aget-byte v3, p0, v2

    .line 798
    add-int/lit8 v4, v2, 0x1

    .line 800
    new-instance v2, Ljava/lang/StringBuilder;

    const-string v5, "uuidLen:"

    invoke-direct {v2, v5}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 803
    sget-object v2, L”/s;->b:Landroid/content/Context;

    const-string v5, "zxtdTaskInfo"

    const/4 v6, 0x0

    invoke-virtual {v2, v5, v6}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v5

    const-string v2, "cscene"

    const/4 v6, 0x0

    invoke-interface {v5, v2, v6}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v2

    const/4 v6, 0x2

    if-ne v2, v6, :cond_2

    const/4 v2, 0x0

    :goto_1
    invoke-interface {v5}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;

    move-result-object v5

    const-string v6, "cscene"

    invoke-interface {v5, v6, v2}, Landroid/content/SharedPreferences$Editor;->putInt(Ljava/lang/String;I)Landroid/content/SharedPreferences$Editor;

    invoke-interface {v5}, Landroid/content/SharedPreferences$Editor;->commit()Z

    .line 805
    new-instance v2, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v2, v0, v4, v3}, Ljava/lang/String;-><init>([BII)V

    sput-object v2, L”/s;->B:Ljava/lang/String;

    .line 806
    add-int v2, v4, v3

    .line 808
    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "userIdNet:"

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-object v4, L”/s;->B:Ljava/lang/String;

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 811
    sget-object v3, L”/s;->E:L”/m;

    const/4 v4, 0x2

    move-object/from16 v0, p0

    invoke-static {v0, v2, v4}, L”/a;->a([BII)I

    move-result v4

    int-to-short v4, v4

    iput-short v4, v3, L”/m;->a:S

    .line 812
    add-int/lit8 v2, v2, 0x2

    .line 813
    sget-object v3, L”/s;->E:L”/m;

    aget-byte v4, p0, v2

    iput-byte v4, v3, L”/m;->b:B

    .line 814
    add-int/lit8 v2, v2, 0x1

    .line 815
    sget-object v3, L”/s;->E:L”/m;

    aget-byte v4, p0, v2

    iput-byte v4, v3, L”/m;->c:B

    .line 816
    add-int/lit8 v2, v2, 0x1

    .line 817
    sget-object v3, L”/s;->E:L”/m;

    aget-byte v4, p0, v2

    iput-byte v4, v3, L”/m;->d:B

    .line 818
    add-int/lit8 v2, v2, 0x1

    .line 819
    sget-object v3, L”/s;->E:L”/m;

    aget-byte v4, p0, v2

    iput-byte v4, v3, L”/m;->e:B

    .line 820
    add-int/lit8 v2, v2, 0x1

    .line 822
    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "registerTimeNet :"

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-object v4, L”/s;->E:L”/m;

    invoke-virtual {v4}, L”/m;->a()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 825
    const/4 v3, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v3}, L”/a;->a([BII)I

    move-result v12

    .line 826
    add-int/lit8 v2, v2, 0x4

    .line 828
    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "softID:"

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3, v12}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 831
    const/4 v3, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v3}, L”/a;->a([BII)I

    move-result v13

    .line 832
    add-int/lit8 v2, v2, 0x4

    .line 834
    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "designId:"

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3, v13}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 837
    aget-byte v14, p0, v2

    .line 838
    add-int/lit8 v2, v2, 0x1

    .line 840
    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "chargeType:"

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3, v14}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 843
    aget-byte v3, p0, v2

    sput-byte v3, L”/s;->H:B

    .line 844
    add-int/lit8 v2, v2, 0x1

    .line 846
    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "platformProtocolType:"

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-byte v4, L”/s;->H:B

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 849
    aget-byte v3, p0, v2

    sput-byte v3, L”/s;->A:B

    .line 850
    add-int/lit8 v2, v2, 0x1

    .line 852
    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "isPay:"

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-byte v4, L”/s;->A:B

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 854
    sget-byte v3, L”/s;->A:B

    const/4 v4, 0x1

    if-ne v3, v4, :cond_0

    .line 860
    const/4 v4, 0x0

    .line 862
    const/16 v23, 0x0

    .line 863
    const/4 v3, 0x0

    .line 865
    const/4 v5, 0x0

    .line 866
    const/16 v20, 0x0

    .line 867
    const/16 v19, 0x0

    .line 868
    const/16 v21, 0x0

    .line 869
    const/16 v22, 0x0

    .line 870
    const/16 v16, 0x0

    .line 872
    aget-byte v6, p0, v2

    sput-byte v6, L”/s;->G:B

    .line 873
    add-int/lit8 v2, v2, 0x1

    .line 875
    new-instance v6, Ljava/lang/StringBuilder;

    const-string v7, "platformChargeType:"

    invoke-direct {v6, v7}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-byte v7, L”/s;->G:B

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 876
    const/4 v6, 0x2

    move-object/from16 v0, p0

    invoke-static {v0, v2, v6}, L”/a;->a([BII)I

    move-result v6

    int-to-short v0, v6

    move/from16 v26, v0

    .line 877
    add-int/lit8 v6, v2, 0x2

    .line 878
    new-instance v2, Ljava/lang/StringBuilder;

    const-string v7, "analysisSpContent: spItemNum:"

    invoke-direct {v2, v7}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    move/from16 v0, v26

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 880
    const/4 v2, 0x0

    move/from16 v25, v2

    move v2, v6

    :goto_2
    move/from16 v0, v25

    move/from16 v1, v26

    if-lt v0, v1, :cond_3

    .line 1892
    sget-object v3, L”/s;->i:L”/m;

    const/4 v4, 0x2

    move-object/from16 v0, p0

    invoke-static {v0, v2, v4}, L”/a;->a([BII)I

    move-result v4

    int-to-short v4, v4

    iput-short v4, v3, L”/m;->a:S

    .line 1893
    add-int/lit8 v2, v2, 0x2

    .line 1894
    sget-object v3, L”/s;->i:L”/m;

    aget-byte v4, p0, v2

    iput-byte v4, v3, L”/m;->b:B

    .line 1895
    add-int/lit8 v2, v2, 0x1

    .line 1896
    sget-object v3, L”/s;->i:L”/m;

    aget-byte v4, p0, v2

    iput-byte v4, v3, L”/m;->c:B

    .line 1897
    add-int/lit8 v2, v2, 0x1

    .line 1898
    sget-object v3, L”/s;->i:L”/m;

    aget-byte v4, p0, v2

    iput-byte v4, v3, L”/m;->d:B

    .line 1899
    add-int/lit8 v2, v2, 0x1

    .line 1900
    sget-object v3, L”/s;->i:L”/m;

    aget-byte v4, p0, v2

    iput-byte v4, v3, L”/m;->e:B

    .line 1901
    add-int/lit8 v2, v2, 0x1

    .line 1903
    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "currnettime:"

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-object v4, L”/s;->i:L”/m;

    invoke-virtual {v4}, L”/m;->a()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1904
    const/4 v3, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v3}, L”/a;->a([BII)I

    move-result v2

    sput v2, L”/s;->d:I

    .line 1905
    new-instance v2, Ljava/lang/StringBuilder;

    const-string v3, "platform analysis sp content finshed TimeToSendSms:"

    invoke-direct {v2, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    .line 1909
    sget v3, L”/s;->d:I

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v2

    .line 1908
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    goto/16 :goto_0

    .line 803
    :cond_2
    add-int/lit8 v2, v2, 0x1

    goto/16 :goto_1

    .line 881
    :cond_3
    new-instance v6, Ljava/lang/StringBuilder;

    const-string v7, "sp item analying...:"

    invoke-direct {v6, v7}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    move/from16 v0, v25

    invoke-virtual {v6, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 882
    sget-byte v6, L”/s;->G:B

    if-nez v6, :cond_19

    .line 884
    aget-byte v7, p0, v2

    .line 887
    add-int/lit8 v2, v2, 0x1

    .line 888
    if-lez v7, :cond_4

    .line 889
    new-instance v3, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v3, v0, v2, v7}, Ljava/lang/String;-><init>([BII)V

    .line 891
    :cond_4
    add-int/2addr v2, v7

    .line 892
    sget-byte v6, L”/s;->H:B

    if-nez v6, :cond_17

    .line 895
    const/4 v6, 0x2

    move-object/from16 v0, p0

    invoke-static {v0, v2, v6}, L”/a;->a([BII)I

    move-result v6

    int-to-short v6, v6

    .line 896
    add-int/lit8 v2, v2, 0x2

    .line 902
    :goto_3
    if-lez v6, :cond_5

    .line 903
    new-instance v5, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v5, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    .line 905
    :cond_5
    add-int/2addr v2, v6

    .line 907
    aget-byte v8, p0, v2

    invoke-static {v8}, L”/s;->a(B)I

    move-result v8

    and-int/lit16 v8, v8, 0xff

    int-to-short v9, v8

    .line 908
    add-int/lit8 v17, v2, 0x1

    .line 910
    if-lez v7, :cond_18

    if-lez v6, :cond_18

    .line 911
    const/4 v2, 0x1

    const/4 v4, 0x0

    .line 912
    const/4 v7, 0x0

    const/4 v8, 0x0

    .line 913
    const/4 v10, 0x0

    const/4 v11, 0x0

    .line 914
    const/4 v15, 0x1

    .line 911
    invoke-static/range {v2 .. v15}, L”/x;->a(BLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;SLjava/lang/String;Ljava/lang/String;IIBZ)Lcom/mywater/platform/sms/ChargeSmsData;

    move-result-object v2

    move-object/from16 v18, v2

    move/from16 v2, v17

    move-object/from16 v17, v5

    .line 1614
    :goto_4
    sget-byte v4, L”/s;->H:B

    const/4 v5, 0x1

    if-ne v4, v5, :cond_6

    .line 1616
    aget-byte v5, p0, v2

    .line 1618
    add-int/lit8 v2, v2, 0x1

    .line 1620
    new-instance v4, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v4, v0, v2, v5}, Ljava/lang/String;-><init>([BII)V

    .line 1621
    add-int/2addr v2, v5

    .line 1624
    aget-byte v5, p0, v2

    invoke-static {v5}, L”/s;->a(B)I

    move-result v5

    .line 1626
    add-int/lit8 v6, v2, 0x1

    .line 1627
    new-instance v2, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v2, v0, v6, v5}, Ljava/lang/String;-><init>([BII)V

    .line 1628
    add-int/2addr v5, v6

    move-object/from16 v19, v2

    move-object/from16 v20, v4

    move v2, v5

    .line 1631
    :cond_6
    aget-byte v5, p0, v2

    .line 1633
    add-int/lit8 v2, v2, 0x1

    .line 1634
    new-instance v4, Ljava/lang/StringBuilder;

    const-string v6, "sp down numberLen:"

    invoke-direct {v4, v6}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1635
    if-lez v5, :cond_33

    .line 1636
    new-instance v4, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v4, v0, v2, v5}, Ljava/lang/String;-><init>([BII)V

    .line 1637
    new-instance v3, Ljava/lang/StringBuilder;

    const-string v6, "sp donw number:"

    invoke-direct {v3, v6}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1639
    :goto_5
    add-int/2addr v2, v5

    .line 1642
    aget-byte v3, p0, v2

    invoke-static {v3}, L”/s;->a(B)I

    move-result v3

    .line 1644
    add-int/lit8 v2, v2, 0x1

    .line 1645
    new-instance v6, Ljava/lang/StringBuilder;

    const-string v7, "sp down keyword contentLen1 :"

    invoke-direct {v6, v7}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v6, v3}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1647
    if-lez v3, :cond_32

    .line 1648
    new-instance v7, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v7, v0, v2, v3}, Ljava/lang/String;-><init>([BII)V

    .line 1649
    :goto_6
    add-int v21, v2, v3

    .line 1653
    if-gtz v5, :cond_7

    if-lez v3, :cond_8

    .line 1654
    :cond_7
    const/16 v2, 0x1f

    const/4 v3, 0x0

    const/4 v5, 0x0

    const/4 v6, 0x0

    const/4 v8, 0x0

    .line 1655
    const/4 v9, 0x0

    const/4 v10, 0x0

    const/4 v11, 0x0

    const/4 v15, 0x0

    .line 1654
    invoke-static/range {v2 .. v15}, L”/x;->a(BLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;SLjava/lang/String;Ljava/lang/String;IIBZ)Lcom/mywater/platform/sms/ChargeSmsData;

    .line 1659
    :cond_8
    aget-byte v2, p0, v21

    .line 1661
    add-int/lit8 v3, v21, 0x1

    .line 1662
    new-instance v5, Ljava/lang/StringBuilder;

    const-string v6, "ope1 down num: "

    invoke-direct {v5, v6}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v5, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1663
    if-lez v2, :cond_9

    .line 1664
    new-instance v4, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v4, v0, v3, v2}, Ljava/lang/String;-><init>([BII)V

    .line 1665
    new-instance v5, Ljava/lang/StringBuilder;

    const-string v6, "operator1 down number:"

    invoke-direct {v5, v6}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v5, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1667
    :cond_9
    add-int/2addr v3, v2

    .line 1670
    aget-byte v5, p0, v3

    invoke-static {v5}, L”/s;->a(B)I

    move-result v5

    .line 1672
    add-int/lit8 v3, v3, 0x1

    .line 1673
    new-instance v6, Ljava/lang/StringBuilder;

    const-string v8, "ope1 down key contentLen1 :"

    invoke-direct {v6, v8}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v6, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1674
    if-lez v5, :cond_a

    .line 1675
    new-instance v7, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v7, v0, v3, v5}, Ljava/lang/String;-><init>([BII)V

    .line 1677
    :cond_a
    add-int v21, v3, v5

    .line 1679
    if-gtz v2, :cond_b

    if-lez v5, :cond_c

    .line 1680
    :cond_b
    const/16 v2, 0x1f

    const/4 v3, 0x0

    const/4 v5, 0x0

    .line 1681
    const/4 v6, 0x0

    const/4 v8, 0x0

    const/4 v9, 0x0

    const/4 v10, 0x0

    .line 1682
    const/4 v11, 0x0

    const/4 v15, 0x0

    .line 1680
    invoke-static/range {v2 .. v15}, L”/x;->a(BLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;SLjava/lang/String;Ljava/lang/String;IIBZ)Lcom/mywater/platform/sms/ChargeSmsData;

    .line 1686
    :cond_c
    aget-byte v2, p0, v21

    .line 1688
    add-int/lit8 v3, v21, 0x1

    .line 1689
    new-instance v5, Ljava/lang/StringBuilder;

    const-string v6, "ope2 down num numberLen :"

    invoke-direct {v5, v6}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v5, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1690
    if-lez v2, :cond_d

    .line 1691
    new-instance v4, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v4, v0, v3, v2}, Ljava/lang/String;-><init>([BII)V

    .line 1693
    :cond_d
    add-int/2addr v3, v2

    .line 1696
    aget-byte v5, p0, v3

    invoke-static {v5}, L”/s;->a(B)I

    move-result v5

    .line 1698
    add-int/lit8 v3, v3, 0x1

    .line 1699
    new-instance v6, Ljava/lang/StringBuilder;

    const-string v8, "ope2 down key contentLen1 :"

    invoke-direct {v6, v8}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v6, v5}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1700
    if-lez v5, :cond_e

    .line 1701
    new-instance v7, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v7, v0, v3, v5}, Ljava/lang/String;-><init>([BII)V

    .line 1703
    :cond_e
    add-int v21, v3, v5

    .line 1704
    if-gtz v2, :cond_f

    if-lez v5, :cond_10

    .line 1705
    :cond_f
    const/16 v2, 0x1f

    const/4 v3, 0x0

    const/4 v5, 0x0

    .line 1706
    const/4 v6, 0x0

    const/4 v8, 0x0

    const/4 v9, 0x0

    const/4 v10, 0x0

    .line 1707
    const/4 v11, 0x0

    const/4 v15, 0x0

    .line 1705
    invoke-static/range {v2 .. v15}, L”/x;->a(BLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;SLjava/lang/String;Ljava/lang/String;IIBZ)Lcom/mywater/platform/sms/ChargeSmsData;

    .line 1711
    :cond_10
    aget-byte v27, p0, v21

    .line 1712
    add-int/lit8 v5, v21, 0x1

    .line 1713
    new-instance v2, Ljava/lang/StringBuilder;

    const-string v3, "confirm :"

    invoke-direct {v2, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    move/from16 v0, v27

    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1714
    if-lez v27, :cond_31

    .line 1715
    const/4 v8, 0x0

    .line 1716
    const/4 v2, 0x0

    .line 1717
    const/16 v3, 0x20

    .line 1721
    aget-byte v6, p0, v5

    .line 1723
    new-instance v9, Ljava/lang/StringBuilder;

    const-string v10, "numberLen:"

    invoke-direct {v9, v10}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v9, v6}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v9

    invoke-virtual {v9}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1724
    add-int/lit8 v5, v5, 0x1

    .line 1725
    if-lez v6, :cond_11

    .line 1726
    new-instance v4, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v4, v0, v5, v6}, Ljava/lang/String;-><init>([BII)V

    .line 1727
    new-instance v9, Ljava/lang/StringBuilder;

    const-string v10, "two confirm operator down number:"

    invoke-direct {v9, v10}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    .line 1728
    invoke-virtual {v9, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v9

    invoke-virtual {v9}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1727
    :cond_11
    add-int/2addr v5, v6

    .line 1733
    aget-byte v6, p0, v5

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1734
    new-instance v9, Ljava/lang/StringBuilder;

    const-string v10, "downContentLen :"

    invoke-direct {v9, v10}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v9, v6}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v9

    invoke-virtual {v9}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1735
    add-int/lit8 v5, v5, 0x1

    .line 1736
    if-lez v6, :cond_30

    .line 1737
    new-instance v2, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v2, v0, v5, v6}, Ljava/lang/String;-><init>([BII)V

    move-object/from16 v21, v2

    .line 1739
    :goto_7
    add-int v2, v5, v6

    .line 1742
    aget-byte v5, p0, v2

    invoke-static {v5}, L”/s;->a(B)I

    move-result v5

    .line 1744
    add-int/lit8 v2, v2, 0x1

    .line 1745
    if-lez v5, :cond_2f

    .line 1746
    new-instance v10, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v10, v0, v2, v5}, Ljava/lang/String;-><init>([BII)V

    .line 1748
    :goto_8
    add-int/2addr v2, v5

    .line 1751
    aget-byte v5, p0, v2

    invoke-static {v5}, L”/s;->a(B)I

    move-result v5

    .line 1753
    add-int/lit8 v2, v2, 0x1

    .line 1754
    if-lez v5, :cond_2e

    .line 1755
    new-instance v11, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v11, v0, v2, v5}, Ljava/lang/String;-><init>([BII)V

    .line 1757
    :goto_9
    add-int/2addr v2, v5

    .line 1760
    aget-byte v5, p0, v2

    .line 1762
    add-int/lit8 v2, v2, 0x1

    .line 1763
    if-lez v5, :cond_12

    .line 1764
    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v5}, Ljava/lang/String;-><init>([BII)V

    .line 1766
    :cond_12
    add-int/2addr v2, v5

    .line 1769
    aget-byte v5, p0, v2

    invoke-static {v5}, L”/s;->a(B)I

    move-result v6

    .line 1771
    add-int/lit8 v2, v2, 0x1

    .line 1772
    if-lez v6, :cond_2d

    .line 1773
    new-instance v5, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v5, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    .line 1775
    :goto_a
    add-int v17, v2, v6

    .line 1777
    const/4 v2, 0x1

    move/from16 v0, v27

    if-ne v0, v2, :cond_1d

    .line 1778
    const/16 v2, 0x20

    .line 1785
    :goto_b
    const/4 v3, 0x2

    move-object/from16 v0, v18

    iput-byte v3, v0, Lcom/mywater/platform/sms/ChargeSmsData;->a:B

    .line 1786
    invoke-static {v4}, L”/a;->b(Ljava/lang/String;)Z

    move-result v3

    if-nez v3, :cond_13

    .line 1787
    if-eqz v21, :cond_2b

    .line 1789
    :cond_13
    move-object/from16 v0, v18

    iget-object v3, v0, Lcom/mywater/platform/sms/ChargeSmsData;->d:Ljava/lang/String;

    .line 1791
    move-object/from16 v0, v18

    iget-short v9, v0, Lcom/mywater/platform/sms/ChargeSmsData;->b:S

    .line 1792
    const/4 v15, 0x0

    move-object/from16 v7, v21

    .line 1788
    invoke-static/range {v2 .. v15}, L”/x;->a(BLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;SLjava/lang/String;Ljava/lang/String;IIBZ)Lcom/mywater/platform/sms/ChargeSmsData;

    move-result-object v2

    .line 1793
    move-object/from16 v0, v18

    iget-object v3, v0, Lcom/mywater/platform/sms/ChargeSmsData;->f:Ljava/lang/String;

    iput-object v3, v2, Lcom/mywater/platform/sms/ChargeSmsData;->g:Ljava/lang/String;

    move-object/from16 v22, v11

    move-object/from16 v21, v10

    move-object/from16 v23, v5

    move-object v3, v4

    move-object/from16 v24, v2

    move/from16 v2, v17

    .line 1799
    :goto_c
    aget-byte v4, p0, v2

    .line 1801
    add-int/lit8 v2, v2, 0x1

    .line 1802
    new-instance v5, Ljava/lang/StringBuilder;

    const-string v6, "cat pool up number len:"

    invoke-direct {v5, v6}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v5, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1803
    if-lez v4, :cond_14

    .line 1804
    new-instance v3, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v3, v0, v2, v4}, Ljava/lang/String;-><init>([BII)V

    .line 1805
    add-int v17, v2, v4

    .line 1807
    sget-byte v2, L”/s;->G:B

    const/4 v4, 0x1

    if-eq v2, v4, :cond_2a

    .line 1809
    sget-byte v2, L”/s;->G:B

    const/4 v4, 0x2

    if-ne v2, v4, :cond_1f

    .line 1811
    sget-object v2, L”/v;->a:L”/f;

    iput-object v3, v2, L”/f;->m:Ljava/lang/String;

    move/from16 v2, v17

    .line 1879
    :cond_14
    :goto_d
    sget-byte v4, L”/s;->H:B

    if-nez v4, :cond_16

    const/4 v4, 0x1

    move/from16 v0, v27

    if-ne v0, v4, :cond_16

    .line 1880
    sget-byte v4, L”/s;->G:B

    if-nez v4, :cond_16

    .line 1881
    aget-byte v4, p0, v2

    .line 1883
    add-int/lit8 v2, v2, 0x1

    .line 1884
    if-lez v4, :cond_15

    if-eqz v24, :cond_15

    .line 1885
    new-instance v5, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v5, v0, v2, v4}, Ljava/lang/String;-><init>([BII)V

    move-object/from16 v0, v24

    iput-object v5, v0, Lcom/mywater/platform/sms/ChargeSmsData;->h:Ljava/lang/String;

    .line 1887
    :cond_15
    add-int/2addr v2, v4

    .line 880
    :cond_16
    add-int/lit8 v4, v25, 0x1

    move/from16 v25, v4

    move-object/from16 v5, v23

    move-object/from16 v23, v24

    move-object/from16 v4, v18

    goto/16 :goto_2

    .line 898
    :cond_17
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    and-int/lit16 v6, v6, 0xff

    int-to-short v6, v6

    .line 899
    add-int/lit8 v2, v2, 0x1

    goto/16 :goto_3

    :cond_18
    move-object/from16 v18, v4

    move/from16 v2, v17

    move-object/from16 v17, v5

    .line 916
    goto/16 :goto_4

    :cond_19
    sget-byte v6, L”/s;->G:B

    const/4 v7, 0x1

    if-eq v6, v7, :cond_34

    .line 958
    sget-byte v6, L”/s;->G:B

    const/4 v7, 0x2

    if-ne v6, v7, :cond_34

    .line 962
    new-instance v6, L”/f;

    invoke-direct {v6}, L”/f;-><init>()V

    .line 967
    sput-object v6, L”/v;->a:L”/f;

    iput v12, v6, L”/f;->aU:I

    .line 968
    sget-object v6, L”/v;->a:L”/f;

    iput v13, v6, L”/f;->aV:I

    .line 969
    sget-object v6, L”/v;->a:L”/f;

    iput-byte v14, v6, L”/f;->aT:B

    .line 972
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 974
    add-int/lit8 v2, v2, 0x1

    .line 975
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aQ:Ljava/lang/String;

    .line 976
    add-int/2addr v2, v6

    .line 979
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 981
    add-int/lit8 v2, v2, 0x1

    .line 982
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->c:Ljava/lang/String;

    .line 983
    add-int/2addr v2, v6

    .line 985
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 987
    add-int/lit8 v2, v2, 0x1

    .line 988
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->d:Ljava/lang/String;

    .line 989
    add-int/2addr v2, v6

    .line 990
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->e:B

    .line 991
    add-int/lit8 v2, v2, 0x1

    .line 993
    sget-object v6, L”/v;->a:L”/f;

    const/4 v7, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v7}, L”/a;->a([BII)I

    move-result v7

    iput v7, v6, L”/f;->f:I

    .line 994
    add-int/lit8 v2, v2, 0x4

    .line 997
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    iput-byte v7, v6, L”/f;->a:B

    .line 998
    add-int/lit8 v2, v2, 0x1

    .line 1001
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1003
    add-int/lit8 v2, v2, 0x1

    .line 1004
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->b:Ljava/lang/String;

    .line 1005
    add-int/2addr v2, v6

    .line 1008
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1010
    add-int/lit8 v2, v2, 0x1

    .line 1011
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->R:Ljava/lang/String;

    .line 1012
    add-int/2addr v2, v6

    .line 1014
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1016
    add-int/lit8 v2, v2, 0x1

    .line 1017
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->S:Ljava/lang/String;

    .line 1018
    add-int/2addr v2, v6

    .line 1021
    sget-object v6, L”/v;->a:L”/f;

    const/4 v7, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v7}, L”/a;->a([BII)I

    move-result v7

    iput v7, v6, L”/f;->T:I

    .line 1022
    add-int/lit8 v2, v2, 0x4

    .line 1025
    sget-object v6, L”/v;->a:L”/f;

    const/4 v7, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v7}, L”/a;->a([BII)I

    move-result v7

    iput v7, v6, L”/f;->U:I

    .line 1026
    add-int/lit8 v2, v2, 0x4

    .line 1029
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1031
    add-int/lit8 v2, v2, 0x1

    .line 1032
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->n:Ljava/lang/String;

    .line 1033
    add-int/2addr v2, v6

    .line 1035
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1037
    add-int/lit8 v2, v2, 0x1

    .line 1038
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->o:Ljava/lang/String;

    .line 1039
    add-int/2addr v2, v6

    .line 1041
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1043
    add-int/lit8 v2, v2, 0x1

    .line 1044
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->p:Ljava/lang/String;

    .line 1045
    add-int/2addr v2, v6

    .line 1047
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->q:B

    .line 1048
    add-int/lit8 v2, v2, 0x1

    .line 1050
    sget-object v6, L”/v;->a:L”/f;

    const/4 v7, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v7}, L”/a;->a([BII)I

    move-result v7

    iput v7, v6, L”/f;->r:I

    .line 1051
    add-int/lit8 v2, v2, 0x4

    .line 1054
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->s:B

    .line 1055
    add-int/lit8 v2, v2, 0x1

    .line 1057
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1059
    add-int/lit8 v2, v2, 0x1

    .line 1060
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->t:Ljava/lang/String;

    .line 1061
    add-int/2addr v2, v6

    .line 1063
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1065
    add-int/lit8 v2, v2, 0x1

    .line 1066
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->u:Ljava/lang/String;

    .line 1067
    add-int/2addr v2, v6

    .line 1071
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1073
    add-int/lit8 v2, v2, 0x1

    .line 1074
    const/4 v7, 0x1

    if-ne v6, v7, :cond_1a

    .line 1075
    sget-object v6, L”/v;->a:L”/f;

    iget-object v6, v6, L”/f;->v:L”/w;

    const/4 v7, 0x1

    iput-byte v7, v6, L”/w;->a:B

    .line 1076
    sget-object v6, L”/v;->a:L”/f;

    iget-object v6, v6, L”/f;->v:L”/w;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/w;->b:B

    .line 1077
    add-int/lit8 v2, v2, 0x1

    .line 1079
    sget-object v6, L”/v;->a:L”/f;

    iget-object v6, v6, L”/f;->v:L”/w;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/w;->c:B

    .line 1080
    add-int/lit8 v2, v2, 0x1

    .line 1082
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1084
    add-int/lit8 v2, v2, 0x1

    .line 1085
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->v:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->d:Ljava/lang/String;

    .line 1086
    add-int/2addr v2, v6

    .line 1088
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1090
    add-int/lit8 v2, v2, 0x1

    .line 1091
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->v:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->e:Ljava/lang/String;

    .line 1092
    add-int/2addr v2, v6

    .line 1094
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1096
    add-int/lit8 v2, v2, 0x1

    .line 1097
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->v:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->f:Ljava/lang/String;

    .line 1098
    add-int/2addr v2, v6

    .line 1100
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1102
    add-int/lit8 v2, v2, 0x1

    .line 1103
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->v:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->g:Ljava/lang/String;

    .line 1104
    add-int/2addr v2, v6

    .line 1106
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1108
    add-int/lit8 v2, v2, 0x1

    .line 1109
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->v:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->h:Ljava/lang/String;

    .line 1110
    add-int/2addr v2, v6

    .line 1112
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1114
    add-int/lit8 v2, v2, 0x1

    .line 1115
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->v:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->i:Ljava/lang/String;

    .line 1116
    add-int/2addr v2, v6

    .line 1118
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1120
    add-int/lit8 v2, v2, 0x1

    .line 1121
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->v:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->j:Ljava/lang/String;

    .line 1122
    add-int/2addr v2, v6

    .line 1127
    :goto_e
    new-instance v6, Ljava/lang/StringBuilder;

    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->v:L”/w;

    invoke-virtual {v7}, L”/w;->toString()Ljava/lang/String;

    move-result-object v7

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1130
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1132
    add-int/lit8 v2, v2, 0x1

    .line 1133
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->w:Ljava/lang/String;

    .line 1134
    add-int/2addr v2, v6

    .line 1136
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1138
    add-int/lit8 v2, v2, 0x1

    .line 1139
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->x:Ljava/lang/String;

    .line 1140
    add-int/2addr v2, v6

    .line 1141
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->y:B

    .line 1142
    add-int/lit8 v2, v2, 0x1

    .line 1144
    sget-object v6, L”/v;->a:L”/f;

    const/4 v7, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v7}, L”/a;->a([BII)I

    move-result v7

    iput v7, v6, L”/f;->z:I

    .line 1145
    add-int/lit8 v2, v2, 0x4

    .line 1148
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1150
    add-int/lit8 v2, v2, 0x1

    .line 1151
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->A:Ljava/lang/String;

    .line 1152
    add-int/2addr v2, v6

    .line 1154
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1156
    add-int/lit8 v2, v2, 0x1

    .line 1157
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->B:Ljava/lang/String;

    .line 1158
    add-int/2addr v2, v6

    .line 1159
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->C:B

    .line 1160
    add-int/lit8 v2, v2, 0x1

    .line 1162
    sget-object v6, L”/v;->a:L”/f;

    const/4 v7, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v7}, L”/a;->a([BII)I

    move-result v7

    iput v7, v6, L”/f;->D:I

    .line 1163
    add-int/lit8 v2, v2, 0x4

    .line 1165
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1167
    add-int/lit8 v2, v2, 0x1

    .line 1168
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->E:Ljava/lang/String;

    .line 1169
    add-int/2addr v2, v6

    .line 1171
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1173
    add-int/lit8 v2, v2, 0x1

    .line 1174
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->F:Ljava/lang/String;

    .line 1175
    add-int/2addr v2, v6

    .line 1178
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    iput-byte v7, v6, L”/f;->G:B

    .line 1179
    add-int/lit8 v2, v2, 0x1

    .line 1182
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1184
    add-int/lit8 v2, v2, 0x1

    .line 1185
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->H:Ljava/lang/String;

    .line 1186
    add-int/2addr v2, v6

    .line 1189
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1191
    add-int/lit8 v2, v2, 0x1

    .line 1192
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->I:Ljava/lang/String;

    .line 1193
    add-int/2addr v2, v6

    .line 1195
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1197
    add-int/lit8 v2, v2, 0x1

    .line 1198
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aP:Ljava/lang/String;

    .line 1199
    add-int/2addr v2, v6

    .line 1202
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1204
    add-int/lit8 v2, v2, 0x1

    .line 1205
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->Y:Ljava/lang/String;

    .line 1206
    add-int/2addr v2, v6

    .line 1209
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->J:B

    .line 1210
    add-int/lit8 v2, v2, 0x1

    .line 1211
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1213
    add-int/lit8 v2, v2, 0x1

    .line 1214
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->K:Ljava/lang/String;

    .line 1215
    add-int/2addr v2, v6

    .line 1217
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1219
    add-int/lit8 v2, v2, 0x1

    .line 1220
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->L:Ljava/lang/String;

    .line 1221
    add-int/2addr v2, v6

    .line 1222
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->M:B

    .line 1223
    add-int/lit8 v2, v2, 0x1

    .line 1225
    sget-object v6, L”/v;->a:L”/f;

    const/4 v7, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v7}, L”/a;->a([BII)I

    move-result v7

    iput v7, v6, L”/f;->N:I

    .line 1226
    add-int/lit8 v2, v2, 0x4

    .line 1228
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1230
    add-int/lit8 v2, v2, 0x1

    .line 1231
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->O:Ljava/lang/String;

    .line 1232
    add-int/2addr v2, v6

    .line 1234
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1236
    add-int/lit8 v2, v2, 0x1

    .line 1237
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->P:Ljava/lang/String;

    .line 1238
    add-int/2addr v2, v6

    .line 1241
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1243
    add-int/lit8 v2, v2, 0x1

    .line 1244
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->V:Ljava/lang/String;

    .line 1245
    add-int/2addr v2, v6

    .line 1247
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1249
    add-int/lit8 v2, v2, 0x1

    .line 1250
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->W:Ljava/lang/String;

    .line 1251
    add-int/2addr v2, v6

    .line 1254
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1256
    add-int/lit8 v2, v2, 0x1

    .line 1257
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->Z:Ljava/lang/String;

    .line 1258
    add-int/2addr v2, v6

    .line 1260
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1262
    add-int/lit8 v2, v2, 0x1

    .line 1263
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aa:Ljava/lang/String;

    .line 1264
    add-int/2addr v2, v6

    .line 1265
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->ab:B

    .line 1266
    add-int/lit8 v2, v2, 0x1

    .line 1268
    sget-object v6, L”/v;->a:L”/f;

    const/4 v7, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v7}, L”/a;->a([BII)I

    move-result v7

    iput v7, v6, L”/f;->ac:I

    .line 1269
    add-int/lit8 v2, v2, 0x4

    .line 1271
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1273
    add-int/lit8 v2, v2, 0x1

    .line 1274
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->ad:Ljava/lang/String;

    .line 1275
    add-int/2addr v2, v6

    .line 1277
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1279
    add-int/lit8 v2, v2, 0x1

    .line 1280
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->ae:Ljava/lang/String;

    .line 1281
    add-int/2addr v2, v6

    .line 1284
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1286
    add-int/lit8 v2, v2, 0x1

    .line 1287
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->af:Ljava/lang/String;

    .line 1288
    add-int/2addr v2, v6

    .line 1290
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1292
    add-int/lit8 v2, v2, 0x1

    .line 1293
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->ag:Ljava/lang/String;

    .line 1294
    add-int/2addr v2, v6

    .line 1296
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1298
    add-int/lit8 v2, v2, 0x1

    .line 1299
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->ah:Ljava/lang/String;

    .line 1300
    add-int/2addr v2, v6

    .line 1302
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->ai:B

    .line 1303
    add-int/lit8 v2, v2, 0x1

    .line 1305
    sget-object v6, L”/v;->a:L”/f;

    const/4 v7, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v7}, L”/a;->a([BII)I

    move-result v7

    iput v7, v6, L”/f;->aj:I

    .line 1306
    add-int/lit8 v2, v2, 0x4

    .line 1309
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->ak:B

    .line 1310
    add-int/lit8 v2, v2, 0x1

    .line 1312
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1314
    add-int/lit8 v2, v2, 0x1

    .line 1315
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->al:Ljava/lang/String;

    .line 1316
    add-int/2addr v2, v6

    .line 1318
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1320
    add-int/lit8 v2, v2, 0x1

    .line 1321
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->am:Ljava/lang/String;

    .line 1322
    add-int/2addr v2, v6

    .line 1326
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1328
    add-int/lit8 v2, v2, 0x1

    .line 1329
    const/4 v7, 0x1

    if-ne v6, v7, :cond_1b

    .line 1330
    sget-object v6, L”/v;->a:L”/f;

    iget-object v6, v6, L”/f;->an:L”/w;

    const/4 v7, 0x1

    iput-byte v7, v6, L”/w;->a:B

    .line 1331
    sget-object v6, L”/v;->a:L”/f;

    iget-object v6, v6, L”/f;->an:L”/w;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/w;->b:B

    .line 1332
    add-int/lit8 v2, v2, 0x1

    .line 1334
    sget-object v6, L”/v;->a:L”/f;

    iget-object v6, v6, L”/f;->an:L”/w;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/w;->c:B

    .line 1335
    add-int/lit8 v2, v2, 0x1

    .line 1337
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1339
    add-int/lit8 v2, v2, 0x1

    .line 1340
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->an:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->d:Ljava/lang/String;

    .line 1341
    add-int/2addr v2, v6

    .line 1343
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1345
    add-int/lit8 v2, v2, 0x1

    .line 1346
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->an:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->e:Ljava/lang/String;

    .line 1347
    add-int/2addr v2, v6

    .line 1349
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1351
    add-int/lit8 v2, v2, 0x1

    .line 1352
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->an:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->f:Ljava/lang/String;

    .line 1353
    add-int/2addr v2, v6

    .line 1355
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1357
    add-int/lit8 v2, v2, 0x1

    .line 1358
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->an:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->g:Ljava/lang/String;

    .line 1359
    add-int/2addr v2, v6

    .line 1361
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1363
    add-int/lit8 v2, v2, 0x1

    .line 1364
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->an:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->h:Ljava/lang/String;

    .line 1365
    add-int/2addr v2, v6

    .line 1367
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1369
    add-int/lit8 v2, v2, 0x1

    .line 1370
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->an:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->i:Ljava/lang/String;

    .line 1371
    add-int/2addr v2, v6

    .line 1373
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1375
    add-int/lit8 v2, v2, 0x1

    .line 1376
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->an:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->j:Ljava/lang/String;

    .line 1377
    add-int/2addr v2, v6

    .line 1382
    :goto_f
    new-instance v6, Ljava/lang/StringBuilder;

    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->an:L”/w;

    invoke-virtual {v7}, L”/w;->toString()Ljava/lang/String;

    move-result-object v7

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1385
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1387
    add-int/lit8 v2, v2, 0x1

    .line 1388
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->ao:Ljava/lang/String;

    .line 1389
    add-int/2addr v2, v6

    .line 1391
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1393
    add-int/lit8 v2, v2, 0x1

    .line 1394
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->ap:Ljava/lang/String;

    .line 1395
    add-int/2addr v2, v6

    .line 1396
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->aq:B

    .line 1397
    add-int/lit8 v2, v2, 0x1

    .line 1399
    sget-object v6, L”/v;->a:L”/f;

    const/4 v7, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v7}, L”/a;->a([BII)I

    move-result v7

    iput v7, v6, L”/f;->ar:I

    .line 1400
    add-int/lit8 v2, v2, 0x4

    .line 1402
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1404
    add-int/lit8 v2, v2, 0x1

    .line 1405
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->as:Ljava/lang/String;

    .line 1406
    add-int/2addr v2, v6

    .line 1408
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1410
    add-int/lit8 v2, v2, 0x1

    .line 1411
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->at:Ljava/lang/String;

    .line 1412
    add-int/2addr v2, v6

    .line 1415
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1417
    add-int/lit8 v2, v2, 0x1

    .line 1418
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->au:Ljava/lang/String;

    .line 1419
    add-int/2addr v2, v6

    .line 1421
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1423
    add-int/lit8 v2, v2, 0x1

    .line 1424
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->av:Ljava/lang/String;

    .line 1425
    add-int/2addr v2, v6

    .line 1427
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1429
    add-int/lit8 v2, v2, 0x1

    .line 1430
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aw:Ljava/lang/String;

    .line 1431
    add-int/2addr v2, v6

    .line 1433
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->ax:B

    .line 1434
    add-int/lit8 v2, v2, 0x1

    .line 1436
    sget-object v6, L”/v;->a:L”/f;

    const/4 v7, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v7}, L”/a;->a([BII)I

    move-result v7

    iput v7, v6, L”/f;->ay:I

    .line 1437
    add-int/lit8 v2, v2, 0x4

    .line 1440
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->az:B

    .line 1441
    add-int/lit8 v2, v2, 0x1

    .line 1443
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1445
    add-int/lit8 v2, v2, 0x1

    .line 1446
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aA:Ljava/lang/String;

    .line 1447
    add-int/2addr v2, v6

    .line 1449
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1451
    add-int/lit8 v2, v2, 0x1

    .line 1452
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aB:Ljava/lang/String;

    .line 1453
    add-int/2addr v2, v6

    .line 1457
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1459
    add-int/lit8 v2, v2, 0x1

    .line 1461
    const/4 v7, 0x1

    if-ne v6, v7, :cond_1c

    .line 1462
    sget-object v6, L”/v;->a:L”/f;

    iget-object v6, v6, L”/f;->aC:L”/w;

    const/4 v7, 0x1

    iput-byte v7, v6, L”/w;->a:B

    .line 1463
    sget-object v6, L”/v;->a:L”/f;

    iget-object v6, v6, L”/f;->aC:L”/w;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/w;->b:B

    .line 1464
    add-int/lit8 v2, v2, 0x1

    .line 1466
    sget-object v6, L”/v;->a:L”/f;

    iget-object v6, v6, L”/f;->aC:L”/w;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/w;->c:B

    .line 1467
    add-int/lit8 v2, v2, 0x1

    .line 1469
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1471
    add-int/lit8 v2, v2, 0x1

    .line 1472
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->aC:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->d:Ljava/lang/String;

    .line 1473
    add-int/2addr v2, v6

    .line 1475
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1477
    add-int/lit8 v2, v2, 0x1

    .line 1478
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->aC:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->e:Ljava/lang/String;

    .line 1479
    add-int/2addr v2, v6

    .line 1481
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1483
    add-int/lit8 v2, v2, 0x1

    .line 1484
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->aC:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->f:Ljava/lang/String;

    .line 1485
    add-int/2addr v2, v6

    .line 1487
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1489
    add-int/lit8 v2, v2, 0x1

    .line 1490
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->aC:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->g:Ljava/lang/String;

    .line 1491
    add-int/2addr v2, v6

    .line 1493
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1495
    add-int/lit8 v2, v2, 0x1

    .line 1496
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->aC:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->h:Ljava/lang/String;

    .line 1497
    add-int/2addr v2, v6

    .line 1499
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1501
    add-int/lit8 v2, v2, 0x1

    .line 1502
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->aC:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->i:Ljava/lang/String;

    .line 1503
    add-int/2addr v2, v6

    .line 1505
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1507
    add-int/lit8 v2, v2, 0x1

    .line 1508
    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->aC:L”/w;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/w;->j:Ljava/lang/String;

    .line 1509
    add-int/2addr v2, v6

    .line 1514
    :goto_10
    new-instance v6, Ljava/lang/StringBuilder;

    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    sget-object v7, L”/v;->a:L”/f;

    iget-object v7, v7, L”/f;->aC:L”/w;

    invoke-virtual {v7}, L”/w;->toString()Ljava/lang/String;

    move-result-object v7

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 1517
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1519
    add-int/lit8 v2, v2, 0x1

    .line 1520
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aD:Ljava/lang/String;

    .line 1521
    add-int/2addr v2, v6

    .line 1523
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1525
    add-int/lit8 v2, v2, 0x1

    .line 1526
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aE:Ljava/lang/String;

    .line 1527
    add-int/2addr v2, v6

    .line 1528
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->aF:B

    .line 1529
    add-int/lit8 v2, v2, 0x1

    .line 1531
    sget-object v6, L”/v;->a:L”/f;

    const/4 v7, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v7}, L”/a;->a([BII)I

    move-result v7

    iput v7, v6, L”/f;->aG:I

    .line 1532
    add-int/lit8 v2, v2, 0x4

    .line 1534
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1536
    add-int/lit8 v2, v2, 0x1

    .line 1537
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aH:Ljava/lang/String;

    .line 1538
    add-int/2addr v2, v6

    .line 1540
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1542
    add-int/lit8 v2, v2, 0x1

    .line 1543
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aI:Ljava/lang/String;

    .line 1544
    add-int/2addr v2, v6

    .line 1547
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1549
    add-int/lit8 v2, v2, 0x1

    .line 1550
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aK:Ljava/lang/String;

    .line 1551
    add-int/2addr v2, v6

    .line 1553
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1555
    add-int/lit8 v2, v2, 0x1

    .line 1556
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aL:Ljava/lang/String;

    .line 1557
    add-int/2addr v2, v6

    .line 1560
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1562
    add-int/lit8 v2, v2, 0x1

    .line 1563
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aN:Ljava/lang/String;

    .line 1564
    add-int/2addr v2, v6

    .line 1566
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1568
    add-int/lit8 v2, v2, 0x1

    .line 1569
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->aO:Ljava/lang/String;

    .line 1570
    add-int/2addr v2, v6

    .line 1573
    sget-object v6, L”/v;->a:L”/f;

    const/4 v7, 0x4

    move-object/from16 v0, p0

    invoke-static {v0, v2, v7}, L”/a;->a([BII)I

    move-result v7

    iput v7, v6, L”/f;->aR:I

    .line 1574
    add-int/lit8 v2, v2, 0x4

    .line 1577
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1579
    add-int/lit8 v2, v2, 0x1

    .line 1580
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->k:Ljava/lang/String;

    .line 1581
    add-int/2addr v2, v6

    .line 1584
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1586
    add-int/lit8 v2, v2, 0x1

    .line 1587
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->j:Ljava/lang/String;

    .line 1588
    add-int/2addr v2, v6

    .line 1590
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1592
    add-int/lit8 v2, v2, 0x1

    .line 1593
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->l:Ljava/lang/String;

    .line 1594
    add-int/2addr v2, v6

    .line 1596
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1598
    add-int/lit8 v2, v2, 0x1

    .line 1599
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->g:Ljava/lang/String;

    .line 1600
    add-int/2addr v2, v6

    .line 1602
    aget-byte v6, p0, v2

    invoke-static {v6}, L”/s;->a(B)I

    move-result v6

    .line 1604
    add-int/lit8 v2, v2, 0x1

    .line 1605
    sget-object v7, L”/v;->a:L”/f;

    new-instance v8, Ljava/lang/String;

    move-object/from16 v0, p0

    invoke-direct {v8, v0, v2, v6}, Ljava/lang/String;-><init>([BII)V

    iput-object v8, v7, L”/f;->h:Ljava/lang/String;

    .line 1606
    add-int/2addr v2, v6

    .line 1607
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    invoke-static {v7}, L”/s;->a(B)I

    move-result v7

    int-to-byte v7, v7

    iput-byte v7, v6, L”/f;->i:B

    .line 1608
    add-int/lit8 v2, v2, 0x1

    .line 1610
    sget-object v6, L”/v;->a:L”/f;

    aget-byte v7, p0, v2

    iput-byte v7, v6, L”/f;->aS:B

    .line 1611
    add-int/lit8 v2, v2, 0x1

    move-object/from16 v17, v5

    move-object/from16 v18, v4

    goto/16 :goto_4

    .line 1125
    :cond_1a
    sget-object v6, L”/v;->a:L”/f;

    iget-object v6, v6, L”/f;->v:L”/w;

    const/4 v7, 0x0

    iput-byte v7, v6, L”/w;->a:B

    goto/16 :goto_e

    .line 1380
    :cond_1b
    sget-object v6, L”/v;->a:L”/f;

    iget-object v6, v6, L”/f;->an:L”/w;

    const/4 v7, 0x0

    iput-byte v7, v6, L”/w;->a:B

    goto/16 :goto_f

    .line 1512
    :cond_1c
    sget-object v6, L”/v;->a:L”/f;

    iget-object v6, v6, L”/f;->aC:L”/w;

    const/4 v7, 0x0

    iput-byte v7, v6, L”/w;->a:B

    goto/16 :goto_10

    .line 1779
    :cond_1d
    const/4 v2, 0x2

    move/from16 v0, v27

    if-ne v0, v2, :cond_1e

    .line 1780
    const/16 v2, 0x21

    .line 1781
    goto/16 :goto_b

    :cond_1e
    const/4 v2, 0x3

    move/from16 v0, v27

    if-ne v0, v2, :cond_2c

    .line 1782
    const/16 v2, 0x22

    goto/16 :goto_b

    .line 1812
    :cond_1f
    sget-byte v2, L”/s;->G:B

    if-nez v2, :cond_2a

    .line 1813
    sget-byte v2, L”/s;->H:B

    if-nez v2, :cond_28

    .line 1814
    const/4 v2, 0x0

    .line 1817
    if-eqz v18, :cond_20

    .line 1819
    move-object/from16 v0, v18

    iget-object v4, v0, Lcom/mywater/platform/sms/ChargeSmsData;->d:Ljava/lang/String;

    invoke-static {v4}, L”/a;->b(Ljava/lang/String;)Z

    move-result v4

    if-nez v4, :cond_23

    .line 1820
    :cond_20
    const-string v4, "1007"

    move-object v6, v4

    .line 1824
    :goto_11
    if-eqz v18, :cond_21

    .line 1826
    move-object/from16 v0, v18

    iget-object v4, v0, Lcom/mywater/platform/sms/ChargeSmsData;->f:Ljava/lang/String;

    invoke-static {v4}, L”/a;->b(Ljava/lang/String;)Z

    move-result v4

    if-nez v4, :cond_24

    .line 1827
    :cond_21
    const-string v4, "catpool"

    .line 1832
    :goto_12
    if-eqz v18, :cond_22

    .line 1834
    move-object/from16 v0, v18

    iget-object v5, v0, Lcom/mywater/platform/sms/ChargeSmsData;->d:Ljava/lang/String;

    invoke-static {v5}, L”/a;->b(Ljava/lang/String;)Z

    move-result v5

    if-eqz v5, :cond_22

    .line 1836
    move-object/from16 v0, v18

    iget-object v5, v0, Lcom/mywater/platform/sms/ChargeSmsData;->f:Ljava/lang/String;

    invoke-static {v5}, L”/a;->b(Ljava/lang/String;)Z

    move-result v5

    if-eqz v5, :cond_22

    .line 1837
    move-object/from16 v0, v18

    iget-short v2, v0, Lcom/mywater/platform/sms/ChargeSmsData;->b:S

    .line 1840
    :cond_22
    if-nez v27, :cond_25

    .line 1841
    new-instance v5, Ljava/lang/String;

    .line 1842
    const-string v7, "04&%d&%d&%d&%s&%s&%d&%d&%d&%s&%s"

    const/16 v8, 0xa

    new-array v8, v8, [Ljava/lang/Object;

    const/4 v9, 0x0

    .line 1843
    invoke-static {v14}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;

    move-result-object v10

    aput-object v10, v8, v9

    const/4 v9, 0x1

    invoke-static {v12}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v10

    aput-object v10, v8, v9

    const/4 v9, 0x2

    invoke-static {v13}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v10

    aput-object v10, v8, v9

    const/4 v9, 0x3

    .line 1844
    aput-object v6, v8, v9

    const/4 v6, 0x4

    aput-object v4, v8, v6

    const/4 v4, 0x5

    .line 1845
    invoke-static {v2}, Ljava/lang/Short;->valueOf(S)Ljava/lang/Short;

    move-result-object v2

    aput-object v2, v8, v4

    const/4 v2, 0x6

    sget-byte v4, L”/s;->G:B

    invoke-static {v4}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;

    move-result-object v4

    aput-object v4, v8, v2

    const/4 v2, 0x7

    .line 1846
    const/4 v4, 0x0

    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v4

    aput-object v4, v8, v2

    const/16 v2, 0x8

    sget-object v4, L”/s;->c:L”/d;

    iget-object v4, v4, L”/d;->g:Ljava/lang/String;

    aput-object v4, v8, v2

    const/16 v2, 0x9

    .line 1847
    sget-object v4, L”/s;->B:Ljava/lang/String;

    aput-object v4, v8, v2

    .line 1841
    invoke-static {v7, v8}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-direct {v5, v2}, Ljava/lang/String;-><init>(Ljava/lang/String;)V

    .line 1872
    :goto_13
    invoke-virtual {v5}, Ljava/lang/String;->length()I

    move-result v2

    int-to-short v6, v2

    .line 1873
    const/4 v2, 0x4

    const/4 v4, 0x0

    .line 1874
    const/4 v7, 0x0

    const/4 v8, 0x0

    const/4 v9, 0x0

    .line 1875
    const/4 v10, 0x0

    const/4 v11, 0x0

    const/4 v15, 0x1

    .line 1873
    invoke-static/range {v2 .. v15}, L”/x;->a(BLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;SLjava/lang/String;Ljava/lang/String;IIBZ)Lcom/mywater/platform/sms/ChargeSmsData;

    move-object/from16 v16, v5

    move/from16 v2, v17

    goto/16 :goto_d

    .line 1822
    :cond_23
    move-object/from16 v0, v18

    iget-object v4, v0, Lcom/mywater/platform/sms/ChargeSmsData;->d:Ljava/lang/String;

    move-object v6, v4

    goto/16 :goto_11

    .line 1829
    :cond_24
    move-object/from16 v0, v18

    iget-object v4, v0, Lcom/mywater/platform/sms/ChargeSmsData;->f:Ljava/lang/String;

    goto/16 :goto_12

    .line 1848
    :cond_25
    const/4 v5, 0x2

    move/from16 v0, v27

    if-eq v0, v5, :cond_26

    .line 1849
    const/4 v5, 0x3

    move/from16 v0, v27

    if-ne v0, v5, :cond_27

    .line 1850
    :cond_26
    new-instance v5, Ljava/lang/String;

    .line 1851
    const-string v7, "04&%d&%d&%d&%s&%s&%d&%d&%d&%s&%s"

    const/16 v8, 0xa

    new-array v8, v8, [Ljava/lang/Object;

    const/4 v9, 0x0

    .line 1852
    invoke-static {v14}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;

    move-result-object v10

    aput-object v10, v8, v9

    const/4 v9, 0x1

    invoke-static {v12}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v10

    aput-object v10, v8, v9

    const/4 v9, 0x2

    invoke-static {v13}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v10

    aput-object v10, v8, v9

    const/4 v9, 0x3

    .line 1853
    aput-object v6, v8, v9

    const/4 v6, 0x4

    aput-object v4, v8, v6

    const/4 v4, 0x5

    .line 1854
    invoke-static {v2}, Ljava/lang/Short;->valueOf(S)Ljava/lang/Short;

    move-result-object v2

    aput-object v2, v8, v4

    const/4 v2, 0x6

    sget-byte v4, L”/s;->G:B

    invoke-static {v4}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;

    move-result-object v4

    aput-object v4, v8, v2

    const/4 v2, 0x7

    .line 1855
    const/4 v4, 0x3

    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v4

    aput-object v4, v8, v2

    const/16 v2, 0x8

    sget-object v4, L”/s;->c:L”/d;

    iget-object v4, v4, L”/d;->g:Ljava/lang/String;

    aput-object v4, v8, v2

    const/16 v2, 0x9

    .line 1856
    sget-object v4, L”/s;->B:Ljava/lang/String;

    aput-object v4, v8, v2

    .line 1850
    invoke-static {v7, v8}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-direct {v5, v2}, Ljava/lang/String;-><init>(Ljava/lang/String;)V

    goto :goto_13

    .line 1857
    :cond_27
    const/4 v5, 0x1

    move/from16 v0, v27

    if-ne v0, v5, :cond_29

    .line 1858
    new-instance v5, Ljava/lang/String;

    .line 1859
    const-string v7, "04&%d&%d&%d&%s&%s&%d&%d&%d&%s&%s"

    const/16 v8, 0xa

    new-array v8, v8, [Ljava/lang/Object;

    const/4 v9, 0x0

    .line 1860
    invoke-static {v14}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;

    move-result-object v10

    aput-object v10, v8, v9

    const/4 v9, 0x1

    invoke-static {v12}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v10

    aput-object v10, v8, v9

    const/4 v9, 0x2

    invoke-static {v13}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v10

    aput-object v10, v8, v9

    const/4 v9, 0x3

    .line 1861
    aput-object v6, v8, v9

    const/4 v6, 0x4

    aput-object v4, v8, v6

    const/4 v4, 0x5

    .line 1862
    invoke-static {v2}, Ljava/lang/Short;->valueOf(S)Ljava/lang/Short;

    move-result-object v2

    aput-object v2, v8, v4

    const/4 v2, 0x6

    sget-byte v4, L”/s;->G:B

    invoke-static {v4}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;

    move-result-object v4

    aput-object v4, v8, v2

    const/4 v2, 0x7

    .line 1863
    const/4 v4, 0x1

    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v4

    aput-object v4, v8, v2

    const/16 v2, 0x8

    sget-object v4, L”/s;->c:L”/d;

    iget-object v4, v4, L”/d;->g:Ljava/lang/String;

    aput-object v4, v8, v2

    const/16 v2, 0x9

    .line 1864
    sget-object v4, L”/s;->B:Ljava/lang/String;

    aput-object v4, v8, v2

    .line 1858
    invoke-static {v7, v8}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-direct {v5, v2}, Ljava/lang/String;-><init>(Ljava/lang/String;)V

    goto/16 :goto_13

    .line 1867
    :cond_28
    new-instance v5, Ljava/lang/String;

    .line 1868
    const-string v2, "04&%d&%d&%d&%s&%s&%d&%d&%d&%s&%s"

    const/16 v4, 0xa

    new-array v4, v4, [Ljava/lang/Object;

    const/4 v6, 0x0

    invoke-static {v14}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;

    move-result-object v7

    aput-object v7, v4, v6

    const/4 v6, 0x1

    invoke-static {v12}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v7

    aput-object v7, v4, v6

    const/4 v6, 0x2

    .line 1869
    invoke-static {v13}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v7

    aput-object v7, v4, v6

    const/4 v6, 0x3

    aput-object v20, v4, v6

    const/4 v6, 0x4

    aput-object v19, v4, v6

    const/4 v6, 0x5

    move-object/from16 v0, v18

    iget-short v7, v0, Lcom/mywater/platform/sms/ChargeSmsData;->b:S

    invoke-static {v7}, Ljava/lang/Short;->valueOf(S)Ljava/lang/Short;

    move-result-object v7

    aput-object v7, v4, v6

    const/4 v6, 0x6

    sget-byte v7, L”/s;->G:B

    invoke-static {v7}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;

    move-result-object v7

    aput-object v7, v4, v6

    const/4 v6, 0x7

    .line 1870
    const/4 v7, 0x4

    invoke-static {v7}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v7

    aput-object v7, v4, v6

    const/16 v6, 0x8

    sget-object v7, L”/s;->c:L”/d;

    iget-object v7, v7, L”/d;->g:Ljava/lang/String;

    aput-object v7, v4, v6

    const/16 v6, 0x9

    sget-object v7, L”/s;->B:Ljava/lang/String;

    aput-object v7, v4, v6

    .line 1867
    invoke-static {v2, v4}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-direct {v5, v2}, Ljava/lang/String;-><init>(Ljava/lang/String;)V

    goto/16 :goto_13

    :cond_29
    move-object/from16 v5, v16

    goto/16 :goto_13

    :cond_2a
    move/from16 v2, v17

    goto/16 :goto_d

    :cond_2b
    move-object/from16 v22, v11

    move-object/from16 v21, v10

    move-object v3, v4

    move-object/from16 v24, v23

    move/from16 v2, v17

    move-object/from16 v23, v5

    goto/16 :goto_c

    :cond_2c
    move v2, v3

    goto/16 :goto_b

    :cond_2d
    move-object/from16 v5, v17

    goto/16 :goto_a

    :cond_2e
    move-object/from16 v11, v22

    goto/16 :goto_9

    :cond_2f
    move-object v10, v7

    goto/16 :goto_8

    :cond_30
    move-object/from16 v21, v2

    goto/16 :goto_7

    :cond_31
    move-object/from16 v21, v7

    move-object v3, v4

    move-object/from16 v24, v23

    move v2, v5

    move-object/from16 v23, v17

    goto/16 :goto_c

    :cond_32
    move-object/from16 v7, v21

    goto/16 :goto_6

    :cond_33
    move-object v4, v3

    goto/16 :goto_5

    :cond_34
    move-object/from16 v17, v5

    move-object/from16 v18, v4

    goto/16 :goto_4
.end method

.method public static a(Landroid/content/Context;Lcom/mywater/platform/Constant$ErrorFlag;)Z
    .locals 1

    .prologue
    .line 168
    const/4 v0, 0x0

    invoke-static {p0, p1, v0}, L”/s;->a(Landroid/content/Context;Lcom/mywater/platform/Constant$ErrorFlag;Z)Z

    move-result v0

    return v0
.end method

.method private static a(Landroid/content/Context;Lcom/mywater/platform/Constant$ErrorFlag;Z)Z
    .locals 5

    .prologue
    const/4 v4, 0x1

    const/4 v3, 0x0

    .line 172
    if-nez p0, :cond_1

    .line 174
    sget-object v0, L”/s;->b:Landroid/content/Context;

    const-string v1, "zxtdTaskInfo"

    invoke-virtual {v0, v1, v3}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v0

    .line 179
    :goto_0
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "isClear:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v1, p2}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 180
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "add ErrorFlag:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {p1}, Lcom/mywater/platform/Constant$ErrorFlag;->name()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 181
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "errorFlag:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-static {}, L”/e;->b()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 183
    if-eqz p2, :cond_2

    .line 185
    invoke-static {}, L”/s;->r()[I

    move-result-object v1

    invoke-virtual {p1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v2

    aget v1, v1, v2

    sparse-switch v1, :sswitch_data_0

    .line 276
    :cond_0
    :goto_1
    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "new added :"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-static {}, L”/e;->b()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 284
    invoke-interface {v0}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    .line 285
    const-string v1, "error"

    invoke-static {}, L”/e;->b()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v0, v1, v2}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    .line 286
    invoke-interface {v0}, Landroid/content/SharedPreferences$Editor;->commit()Z

    move-result v0

    return v0

    .line 176
    :cond_1
    const-string v0, "zxtdTaskInfo"

    invoke-virtual {p0, v0, v3}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v0

    goto :goto_0

    .line 187
    :sswitch_0
    sget v1, L”/e;->b:I

    if-eqz v1, :cond_0

    .line 188
    sput v3, L”/e;->b:I

    goto :goto_1

    .line 193
    :sswitch_1
    sget v1, L”/e;->d:I

    if-eqz v1, :cond_0

    .line 194
    sput v3, L”/e;->d:I

    goto :goto_1

    .line 203
    :cond_2
    invoke-static {}, L”/s;->r()[I

    move-result-object v1

    invoke-virtual {p1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v2

    aget v1, v1, v2

    packed-switch v1, :pswitch_data_0

    :pswitch_0
    goto :goto_1

    .line 206
    :pswitch_1
    sget v1, L”/e;->a:I

    if-nez v1, :cond_0

    .line 207
    invoke-virtual {p1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    sget-object v2, Lcom/mywater/platform/Constant$ErrorFlag;->a:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v2}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v2

    sub-int/2addr v1, v2

    sput v1, L”/e;->a:I

    goto :goto_1

    .line 221
    :pswitch_2
    sget v1, L”/e;->b:I

    if-nez v1, :cond_0

    .line 222
    invoke-virtual {p1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    sget-object v2, Lcom/mywater/platform/Constant$ErrorFlag;->d:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v2}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v2

    sub-int/2addr v1, v2

    sput v1, L”/e;->b:I

    goto :goto_1

    .line 227
    :pswitch_3
    sget v1, L”/e;->c:I

    if-nez v1, :cond_0

    .line 228
    sput v4, L”/e;->c:I

    goto :goto_1

    .line 243
    :pswitch_4
    sget v1, L”/e;->d:I

    if-nez v1, :cond_0

    .line 244
    invoke-virtual {p1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    sget-object v2, Lcom/mywater/platform/Constant$ErrorFlag;->p:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v2}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v2

    sub-int/2addr v1, v2

    sput v1, L”/e;->d:I

    goto/16 :goto_1

    .line 249
    :pswitch_5
    sget v1, L”/e;->g:I

    if-nez v1, :cond_0

    .line 250
    invoke-virtual {p1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    sget-object v2, Lcom/mywater/platform/Constant$ErrorFlag;->B:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v2}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v2

    sub-int/2addr v1, v2

    sput v1, L”/e;->g:I

    goto/16 :goto_1

    .line 256
    :pswitch_6
    sget v1, L”/e;->b:I

    if-lez v1, :cond_3

    .line 257
    sput v3, L”/e;->b:I

    .line 259
    :cond_3
    sget v1, L”/e;->d:I

    if-lez v1, :cond_4

    .line 260
    sput v3, L”/e;->d:I

    .line 267
    :cond_4
    :pswitch_7
    invoke-virtual {p1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    sget-object v2, Lcom/mywater/platform/Constant$ErrorFlag;->D:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v2}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v2

    sub-int/2addr v1, v2

    add-int/lit8 v1, v1, 0x1

    sput v1, L”/e;->f:I

    goto/16 :goto_1

    .line 271
    :pswitch_8
    sput v4, L”/e;->h:I

    goto/16 :goto_1

    .line 275
    :pswitch_9
    sput v4, L”/e;->e:I

    goto/16 :goto_1

    .line 185
    :sswitch_data_0
    .sparse-switch
        0x4 -> :sswitch_0
        0x10 -> :sswitch_1
    .end sparse-switch

    .line 203
    :pswitch_data_0
    .packed-switch 0x2
        :pswitch_1
        :pswitch_1
        :pswitch_0
        :pswitch_2
        :pswitch_2
        :pswitch_2
        :pswitch_2
        :pswitch_2
        :pswitch_2
        :pswitch_2
        :pswitch_2
        :pswitch_2
        :pswitch_2
        :pswitch_3
        :pswitch_0
        :pswitch_4
        :pswitch_4
        :pswitch_4
        :pswitch_4
        :pswitch_4
        :pswitch_4
        :pswitch_4
        :pswitch_4
        :pswitch_4
        :pswitch_4
        :pswitch_4
        :pswitch_0
        :pswitch_5
        :pswitch_7
        :pswitch_7
        :pswitch_7
        :pswitch_6
        :pswitch_7
        :pswitch_7
        :pswitch_8
        :pswitch_9
    .end packed-switch
.end method

.method public static a(Lcom/mywater/platform/Constant$ErrorFlag;)Z
    .locals 2

    .prologue
    .line 164
    const/4 v0, 0x0

    const/4 v1, 0x1

    invoke-static {v0, p0, v1}, L”/s;->a(Landroid/content/Context;Lcom/mywater/platform/Constant$ErrorFlag;Z)Z

    move-result v0

    return v0
.end method

.method public static b()I
    .locals 1

    .prologue
    .line 116
    sget v0, L”/s;->I:I

    if-nez v0, :cond_0

    .line 117
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    sput v0, L”/s;->I:I

    .line 119
    :cond_0
    sget v0, L”/s;->I:I

    return v0
.end method

.method private static b(Landroid/content/Context;Z)V
    .locals 3

    .prologue
    .line 396
    new-instance v0, Landroid/content/Intent;

    const-class v1, Lcom/mywater/platform/PlatformService;

    invoke-direct {v0, p0, v1}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 397
    if-eqz p1, :cond_0

    .line 398
    const-string v1, "testMode"

    const/4 v2, 0x1

    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Z)Landroid/content/Intent;

    .line 400
    :cond_0
    const-string v1, "android.intent.category.DEFAULT"

    invoke-virtual {v0, v1}, Landroid/content/Intent;->addCategory(Ljava/lang/String;)Landroid/content/Intent;

    .line 401
    const/high16 v1, 0x10000000

    invoke-virtual {v0, v1}, Landroid/content/Intent;->setFlags(I)Landroid/content/Intent;

    .line 402
    invoke-virtual {p0, v0}, Landroid/content/Context;->startService(Landroid/content/Intent;)Landroid/content/ComponentName;

    .line 403
    return-void
.end method

.method private static b(Landroid/content/Context;)Z
    .locals 7

    .prologue
    const/4 v1, 0x0

    .line 299
    const-string v3, ""

    .line 300
    const/4 v2, 0x1

    .line 301
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v4, " package name:"

    invoke-direct {v0, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {p0}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 303
    :try_start_0
    invoke-virtual {p0}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    move-result-object v0

    invoke-virtual {p0}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    move-result-object v4

    const/16 v5, 0x40

    invoke-virtual {v0, v4, v5}, Landroid/content/pm/PackageManager;->getPackageInfo(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;

    move-result-object v0

    .line 304
    iget-object v0, v0, Landroid/content/pm/PackageInfo;->signatures:[Landroid/content/pm/Signature;

    .line 305
    const-string v4, "X.509"

    invoke-static {v4}, Ljava/security/cert/CertificateFactory;->getInstance(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;

    move-result-object v4

    .line 306
    new-instance v5, Ljava/io/ByteArrayInputStream;

    const/4 v6, 0x0

    aget-object v0, v0, v6

    invoke-virtual {v0}, Landroid/content/pm/Signature;->toByteArray()[B

    move-result-object v0

    invoke-direct {v5, v0}, Ljava/io/ByteArrayInputStream;-><init>([B)V

    invoke-virtual {v4, v5}, Ljava/security/cert/CertificateFactory;->generateCertificate(Ljava/io/InputStream;)Ljava/security/cert/Certificate;

    move-result-object v0

    check-cast v0, Ljava/security/cert/X509Certificate;

    .line 307
    invoke-virtual {v0}, Ljava/security/cert/X509Certificate;->getEncoded()[B

    move-result-object v0

    const/4 v4, 0x0

    invoke-static {v0, v4}, Landroid/util/Base64;->encodeToString([BI)Ljava/lang/String;

    move-result-object v3

    .line 308
    invoke-static {v3}, L”/g;->a(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    .line 309
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v4, " signed pubkey:"

    invoke-direct {v0, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 313
    const-string v0, "41B9907748D93BC6B97442889C1572A6"

    invoke-virtual {v0, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    move v0, v1

    .line 317
    :goto_0
    return v0

    .line 311
    :catch_0
    move-exception v0

    :try_start_1
    invoke-virtual {v0}, Ljava/lang/Exception;->printStackTrace()V
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    .line 313
    const-string v0, "41B9907748D93BC6B97442889C1572A6"

    invoke-virtual {v0, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v0

    if-eqz v0, :cond_0

    move v0, v1

    .line 314
    goto :goto_0

    .line 312
    :catchall_0
    move-exception v0

    .line 313
    const-string v1, "41B9907748D93BC6B97442889C1572A6"

    invoke-virtual {v1, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    .line 314
    throw v0

    :cond_0
    move v0, v2

    goto :goto_0
.end method

.method public static c()V
    .locals 3

    .prologue
    .line 407
    sget-object v0, L”/s;->b:Landroid/content/Context;

    .line 410
    new-instance v1, Landroid/content/Intent;

    const-class v2, Lcom/mywater/platform/PlatformService;

    invoke-direct {v1, v0, v2}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 411
    const-string v2, "android.intent.category.DEFAULT"

    invoke-virtual {v1, v2}, Landroid/content/Intent;->addCategory(Ljava/lang/String;)Landroid/content/Intent;

    .line 412
    const/high16 v2, 0x10000000

    invoke-virtual {v1, v2}, Landroid/content/Intent;->setFlags(I)Landroid/content/Intent;

    .line 413
    invoke-virtual {v0, v1}, Landroid/content/Context;->stopService(Landroid/content/Intent;)Z

    .line 414
    return-void
.end method

.method private static c(Landroid/content/Context;)Z
    .locals 4

    .prologue
    .line 418
    const-string v0, "activity"

    invoke-virtual {p0, v0}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/app/ActivityManager;

    .line 419
    const/16 v1, 0xff

    invoke-virtual {v0, v1}, Landroid/app/ActivityManager;->getRunningServices(I)Ljava/util/List;

    move-result-object v0

    invoke-interface {v0}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v1

    .line 430
    :cond_0
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    move-result v0

    if-nez v0, :cond_1

    .line 434
    const/4 v0, 0x0

    :goto_0
    return v0

    .line 419
    :cond_1
    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/app/ActivityManager$RunningServiceInfo;

    .line 421
    const-string v2, "com.zxtd.platform.PlatformService"

    iget-object v3, v0, Landroid/app/ActivityManager$RunningServiceInfo;->service:Landroid/content/ComponentName;

    invoke-virtual {v3}, Landroid/content/ComponentName;->getClassName()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_0

    .line 422
    iget-boolean v2, v0, Landroid/app/ActivityManager$RunningServiceInfo;->started:Z

    if-eqz v2, :cond_0

    .line 423
    iget v0, v0, Landroid/app/ActivityManager$RunningServiceInfo;->pid:I

    .line 424
    const/4 v0, 0x1

    goto :goto_0
.end method

.method public static d()V
    .locals 1

    .prologue
    .line 611
    sget-boolean v0, L”/s;->f:Z

    if-nez v0, :cond_0

    .line 612
    const/4 v0, 0x1

    sput-boolean v0, L”/s;->f:Z

    .line 613
    sget-object v0, Lcom/mywater/platform/Constant$NvIndex;->g:Lcom/mywater/platform/Constant$NvIndex;

    invoke-static {v0}, L”/s;->a(Lcom/mywater/platform/Constant$NvIndex;)V

    .line 615
    :cond_0
    return-void
.end method

.method public static e()V
    .locals 12

    .prologue
    const/16 v3, 0x898

    const/16 v11, 0xa

    const/4 v1, 0x1

    const/4 v8, 0x0

    const/4 v4, 0x0

    .line 2151
    sget-boolean v0, L”/s;->D:Z

    if-eqz v0, :cond_0

    .line 2154
    sget-object v0, L”/s;->a:L”/l;

    const/16 v0, 0x7530

    sget-object v1, Lcom/mywater/platform/Constant$MsgHandlerId;->e:Lcom/mywater/platform/Constant$MsgHandlerId;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$MsgHandlerId;->ordinal()I

    move-result v1

    invoke-static {v4, v0, v1}, L”/l;->a(Ljava/lang/Object;II)V

    .line 2182
    :goto_0
    return-void

    .line 2157
    :cond_0
    sget-object v0, Lcom/mywater/platform/Constant$NvIndex;->e:Lcom/mywater/platform/Constant$NvIndex;

    invoke-static {v0}, L”/s;->a(Lcom/mywater/platform/Constant$NvIndex;)V

    .line 2158
    sput-boolean v1, L”/s;->D:Z

    .line 2160
    sget v0, L”/s;->R:I

    packed-switch v0, :pswitch_data_0

    sget-object v0, L”/s;->c:L”/d;

    const/16 v2, 0x7d0

    iput v2, v0, L”/d;->a:I

    sget-object v0, L”/s;->c:L”/d;

    const v2, 0x139ac

    iput v2, v0, L”/d;->b:I

    .line 2161
    :goto_1
    sget-object v0, L”/s;->c:L”/d;

    iget v0, v0, L”/d;->b:I

    rem-int/lit8 v0, v0, 0x64

    if-nez v0, :cond_3

    .line 2162
    sget-object v0, Lcom/mywater/platform/Constant$NvIndex;->a:Lcom/mywater/platform/Constant$NvIndex;

    invoke-static {v0}, L”/s;->a(Lcom/mywater/platform/Constant$NvIndex;)V

    .line 2163
    sget-object v0, L”/s;->c:L”/d;

    sget-short v2, L”/s;->n:S

    iput-short v2, v0, L”/d;->c:S

    .line 2164
    sget-object v0, L”/s;->c:L”/d;

    sget-short v2, L”/s;->r:S

    iput-short v2, v0, L”/d;->d:S

    .line 2165
    sget-object v0, L”/s;->c:L”/d;

    sget-object v2, L”/s;->j:L”/m;

    iput-object v2, v0, L”/d;->e:L”/m;

    .line 2178
    :goto_2
    sget-object v0, L”/s;->c:L”/d;

    .line 2180
    sget-object v0, L”/s;->c:L”/d;

    const-string v2, "8613800755500"

    iput-object v2, v0, L”/d;->f:Ljava/lang/String;

    new-instance v0, Ljava/lang/StringBuilder;

    const-string v2, "bool: true, smscenter:"

    invoke-direct {v0, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-object v2, L”/s;->c:L”/d;

    iget-object v2, v2, L”/d;->f:Ljava/lang/String;

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    sget-object v0, L”/s;->b:Landroid/content/Context;

    sget-object v2, L”/s;->x:Ljava/lang/String;

    invoke-static {v2}, L”/a;->b(Ljava/lang/String;)Z

    move-result v2

    if-nez v2, :cond_5

    const-string v2, "phone"

    invoke-virtual {v0, v2}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/telephony/TelephonyManager;

    sput-object v4, L”/s;->x:Ljava/lang/String;

    sput-object v4, L”/s;->y:Ljava/lang/String;

    invoke-virtual {v0}, Landroid/telephony/TelephonyManager;->getSubscriberId()Ljava/lang/String;

    move-result-object v2

    sput-object v2, L”/s;->x:Ljava/lang/String;

    invoke-virtual {v0}, Landroid/telephony/TelephonyManager;->getDeviceId()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0}, Landroid/telephony/TelephonyManager;->getLine1Number()Ljava/lang/String;

    move-result-object v0

    sput-object v0, L”/s;->y:Ljava/lang/String;

    new-instance v0, Ljava/lang/StringBuilder;

    const-string v3, "imsi:"

    invoke-direct {v0, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-object v3, L”/s;->x:Ljava/lang/String;

    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    new-instance v0, Ljava/lang/StringBuilder;

    const-string v3, "imei:"

    invoke-direct {v0, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    sget-object v0, L”/s;->x:Ljava/lang/String;

    invoke-static {v0}, L”/a;->b(Ljava/lang/String;)Z

    move-result v0

    if-nez v0, :cond_5

    move v0, v8

    :goto_3
    if-eqz v0, :cond_6

    sget-object v0, L”/s;->c:L”/d;

    sget-object v2, L”/s;->x:Ljava/lang/String;

    iput-object v2, v0, L”/d;->g:Ljava/lang/String;

    new-instance v0, Ljava/lang/StringBuilder;

    const-string v2, "bool: true, card1_imsi:"

    invoke-direct {v0, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-object v2, L”/s;->c:L”/d;

    iget-object v2, v2, L”/d;->g:Ljava/lang/String;

    invoke-virtual {v0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    :goto_4
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    new-instance v0, Ljava/lang/StringBuffer;

    invoke-direct {v0}, Ljava/lang/StringBuffer;-><init>()V

    sget-object v2, L”/s;->b:Landroid/content/Context;

    const-string v3, "newHost"

    const-string v6, "zxtdTaskInfo"

    invoke-virtual {v2, v6, v8}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v2

    const-string v6, ""

    invoke-interface {v2, v3, v6}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v2

    invoke-static {v2}, L”/a;->b(Ljava/lang/String;)Z

    move-result v3

    if-eqz v3, :cond_1

    sput-object v2, Lcom/mywater/platform/Constant;->a:Ljava/lang/String;

    :cond_1
    sget-object v2, L”/s;->c:L”/d;

    iget v2, v2, L”/d;->a:I

    sget-object v3, L”/s;->c:L”/d;

    iget v3, v3, L”/d;->b:I

    sget-object v6, L”/s;->c:L”/d;

    sget-object v6, L”/s;->c:L”/d;

    iget-short v6, v6, L”/d;->c:S

    sget-object v7, L”/s;->c:L”/d;

    iget-short v7, v7, L”/d;->d:S

    sget-object v9, L”/s;->c:L”/d;

    iget-object v9, v9, L”/d;->e:L”/m;

    invoke-static {v5, v2, v3, v8}, L”/s;->a(Ljava/lang/StringBuilder;IIB)I

    const-string v2, "&%s=%d&%s=%s&%s=%d&%s=%d&%s=%s&%s=%s&%s=%s&%s=%d&%s&%s&%s&%s=%s&%s=%s&%s=%s&%s=%s&%s=%s"

    const/16 v3, 0x1d

    new-array v3, v3, [Ljava/lang/Object;

    const-string v10, "num"

    aput-object v10, v3, v8

    sget-short v10, L”/s;->g:S

    invoke-static {v10}, Ljava/lang/Short;->valueOf(S)Ljava/lang/Short;

    move-result-object v10

    aput-object v10, v3, v1

    const/4 v1, 0x2

    const-string v10, "time"

    aput-object v10, v3, v1

    const/4 v1, 0x3

    sget-object v10, L”/s;->h:L”/m;

    invoke-virtual {v10}, L”/m;->a()Ljava/lang/String;

    move-result-object v10

    aput-object v10, v3, v1

    const/4 v1, 0x4

    const-string v10, "payed"

    aput-object v10, v3, v1

    const/4 v1, 0x5

    invoke-static {v6}, Ljava/lang/Short;->valueOf(S)Ljava/lang/Short;

    move-result-object v6

    aput-object v6, v3, v1

    const/4 v1, 0x6

    const-string v6, "mpayed"

    aput-object v6, v3, v1

    const/4 v1, 0x7

    invoke-static {v7}, Ljava/lang/Short;->valueOf(S)Ljava/lang/Short;

    move-result-object v6

    aput-object v6, v3, v1

    const/16 v1, 0x8

    const-string v6, "last"

    aput-object v6, v3, v1

    const/16 v1, 0x9

    invoke-virtual {v9}, L”/m;->a()Ljava/lang/String;

    move-result-object v6

    aput-object v6, v3, v1

    const-string v1, "sc"

    aput-object v1, v3, v11

    const/16 v1, 0xb

    sget-object v6, L”/s;->c:L”/d;

    iget-object v6, v6, L”/d;->f:Ljava/lang/String;

    aput-object v6, v3, v1

    const/16 v1, 0xc

    const-string v6, "imsi"

    aput-object v6, v3, v1

    const/16 v1, 0xd

    sget-object v6, L”/s;->c:L”/d;

    iget-object v6, v6, L”/d;->g:Ljava/lang/String;

    aput-object v6, v3, v1

    const/16 v1, 0xe

    const-string v6, "sim"

    aput-object v6, v3, v1

    const/16 v1, 0xf

    invoke-static {}, L”/s;->o()B

    move-result v6

    invoke-static {v6}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;

    move-result-object v6

    aput-object v6, v3, v1

    const/16 v1, 0x10

    const-string v6, "pt=0"

    aput-object v6, v3, v1

    const/16 v1, 0x11

    const-string v6, "version=15"

    aput-object v6, v3, v1

    const/16 v1, 0x12

    const-string v6, "sys=android"

    aput-object v6, v3, v1

    const/16 v1, 0x13

    const-string v6, "sys_ver"

    aput-object v6, v3, v1

    const/16 v1, 0x14

    invoke-static {}, L”/s;->j()Ljava/lang/String;

    move-result-object v6

    aput-object v6, v3, v1

    const/16 v1, 0x15

    const-string v6, "mobile"

    aput-object v6, v3, v1

    const/16 v1, 0x16

    invoke-static {}, L”/s;->k()Ljava/lang/String;

    move-result-object v6

    aput-object v6, v3, v1

    const/16 v1, 0x17

    const-string v6, "currStart"

    aput-object v6, v3, v1

    const/16 v1, 0x18

    sget-wide v6, L”/s;->M:J

    invoke-static {v6, v7}, L”/s;->a(J)Ljava/lang/String;

    move-result-object v6

    aput-object v6, v3, v1

    const/16 v1, 0x19

    const-string v6, "lastStart"

    aput-object v6, v3, v1

    const/16 v1, 0x1a

    sget-wide v6, L”/s;->L:J

    invoke-static {v6, v7}, L”/s;->a(J)Ljava/lang/String;

    move-result-object v6

    aput-object v6, v3, v1

    const/16 v1, 0x1b

    const-string v6, "lastError"

    aput-object v6, v3, v1

    const/16 v1, 0x1c

    sget-object v6, L”/s;->O:Ljava/lang/String;

    aput-object v6, v3, v1

    invoke-static {v2, v3}, Ljava/lang/String;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v5, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    const-string v1, "&phone_num="

    invoke-virtual {v5, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    sget-object v1, L”/s;->y:Ljava/lang/String;

    invoke-static {v1}, L”/a;->b(Ljava/lang/String;)Z

    move-result v1

    if-eqz v1, :cond_2

    sget-object v1, L”/s;->y:Ljava/lang/String;

    invoke-virtual {v5, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    :cond_2
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/String;->getBytes()[B

    move-result-object v1

    array-length v7, v1

    sget-object v1, Lcom/mywater/platform/Constant$ReqType;->b:Lcom/mywater/platform/Constant$ReqType;

    const-string v2, "/plugamaosp/access.action"

    sget-object v3, Lcom/mywater/platform/Constant;->a:Ljava/lang/String;

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    move-object v6, v4

    invoke-static/range {v0 .. v7}, L”/n;->a(Ljava/lang/StringBuffer;Lcom/mywater/platform/Constant$ReqType;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)I

    sget-object v1, Lcom/mywater/platform/Constant;->a:Ljava/lang/String;

    sget-object v2, Lcom/mywater/platform/Constant$NetMode;->a:Lcom/mywater/platform/Constant$NetMode;

    invoke-virtual {v0}, Ljava/lang/StringBuffer;->toString()Ljava/lang/String;

    move-result-object v3

    sget-object v6, Lcom/mywater/platform/Constant$NetCallback;->a:Lcom/mywater/platform/Constant$NetCallback;

    move v5, v8

    invoke-static/range {v1 .. v6}, L”/n;->a(Ljava/lang/String;Lcom/mywater/platform/Constant$NetMode;Ljava/lang/String;Ljava/lang/String;ILcom/mywater/platform/Constant$NetCallback;)V

    sput-boolean v8, L”/s;->D:Z

    goto/16 :goto_0

    .line 2160
    :pswitch_0
    sget-object v0, L”/s;->c:L”/d;

    const v2, 0x139b6

    iput v2, v0, L”/d;->b:I

    sget-object v0, L”/s;->c:L”/d;

    iput v3, v0, L”/d;->a:I

    goto/16 :goto_1

    :pswitch_1
    sget-object v0, L”/s;->c:L”/d;

    const v2, 0x139c0

    iput v2, v0, L”/d;->b:I

    sget-object v0, L”/s;->c:L”/d;

    iput v3, v0, L”/d;->a:I

    goto/16 :goto_1

    .line 2166
    :cond_3
    sget-object v0, L”/s;->c:L”/d;

    iget v0, v0, L”/d;->b:I

    rem-int/lit8 v0, v0, 0x64

    if-ne v0, v11, :cond_4

    .line 2167
    sget-object v0, Lcom/mywater/platform/Constant$NvIndex;->b:Lcom/mywater/platform/Constant$NvIndex;

    invoke-static {v0}, L”/s;->a(Lcom/mywater/platform/Constant$NvIndex;)V

    .line 2168
    sget-object v0, L”/s;->c:L”/d;

    sget-short v2, L”/s;->o:S

    iput-short v2, v0, L”/d;->c:S

    .line 2169
    sget-object v0, L”/s;->c:L”/d;

    sget-short v2, L”/s;->s:S

    iput-short v2, v0, L”/d;->d:S

    .line 2170
    sget-object v0, L”/s;->c:L”/d;

    sget-object v2, L”/s;->k:L”/m;

    iput-object v2, v0, L”/d;->e:L”/m;

    goto/16 :goto_2

    .line 2172
    :cond_4
    sget-object v0, Lcom/mywater/platform/Constant$NvIndex;->c:Lcom/mywater/platform/Constant$NvIndex;

    invoke-static {v0}, L”/s;->a(Lcom/mywater/platform/Constant$NvIndex;)V

    .line 2173
    sget-object v0, L”/s;->c:L”/d;

    sget-short v2, L”/s;->p:S

    iput-short v2, v0, L”/d;->c:S

    .line 2174
    sget-object v0, L”/s;->c:L”/d;

    sget-short v2, L”/s;->t:S

    iput-short v2, v0, L”/d;->d:S

    .line 2175
    sget-object v0, L”/s;->c:L”/d;

    sget-object v2, L”/s;->l:L”/m;

    iput-object v2, v0, L”/d;->e:L”/m;

    goto/16 :goto_2

    :cond_5
    move v0, v1

    .line 2180
    goto/16 :goto_3

    :cond_6
    sget-object v0, L”/s;->c:L”/d;

    iput-object v4, v0, L”/d;->g:Ljava/lang/String;

    goto/16 :goto_4

    .line 2160
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method

.method public static f()V
    .locals 1

    .prologue
    .line 2252
    const/4 v0, 0x1

    invoke-static {v0}, L”/s;->a(Z)V

    .line 2253
    const/4 v0, -0x1

    sput-byte v0, L”/s;->G:B

    .line 2254
    const/4 v0, 0x0

    sput-object v0, L”/s;->b:Landroid/content/Context;

    .line 2255
    return-void
.end method

.method public static g()Z
    .locals 1

    .prologue
    .line 2258
    sget-boolean v0, L”/s;->P:Z

    return v0
.end method

.method public static h()V
    .locals 1

    .prologue
    .line 2262
    sget-boolean v0, L”/s;->P:Z

    if-eqz v0, :cond_0

    .line 2263
    const/4 v0, 0x0

    sput-boolean v0, L”/s;->P:Z

    .line 2264
    invoke-static {}, L”/s;->f()V

    .line 2267
    :cond_0
    return-void
.end method

.method public static i()V
    .locals 7

    .prologue
    const-wide/16 v5, 0x0

    const/4 v4, 0x0

    const/4 v3, 0x0

    .line 2308
    sget-object v0, L”/s;->b:Landroid/content/Context;

    const-string v1, "zxtdTaskInfo"

    invoke-virtual {v0, v1, v3}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v0

    const-string v1, "last"

    invoke-interface {v0, v1, v5, v6}, Landroid/content/SharedPreferences;->getLong(Ljava/lang/String;J)J

    move-result-wide v1

    sput-wide v1, L”/s;->L:J

    const-string v1, "curr"

    invoke-interface {v0, v1, v5, v6}, Landroid/content/SharedPreferences;->getLong(Ljava/lang/String;J)J

    move-result-wide v1

    sput-wide v1, L”/s;->M:J

    const-string v1, "error"

    invoke-static {}, L”/e;->a()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v0, v1, v2}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    sput-object v1, L”/s;->O:Ljava/lang/String;

    new-instance v1, Ljava/lang/StringBuilder;

    const-string v2, "load last errorSign:"

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-object v2, L”/s;->O:Ljava/lang/String;

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    invoke-interface {v0}, Landroid/content/SharedPreferences;->edit()Landroid/content/SharedPreferences$Editor;

    move-result-object v0

    const-string v1, "error"

    invoke-static {}, L”/e;->a()Ljava/lang/String;

    move-result-object v2

    invoke-interface {v0, v1, v2}, Landroid/content/SharedPreferences$Editor;->putString(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;

    invoke-interface {v0}, Landroid/content/SharedPreferences$Editor;->commit()Z

    .line 2309
    invoke-static {}, L”/s;->q()V

    .line 2310
    invoke-static {}, L”/s;->p()V

    .line 2312
    sget-object v0, Lcom/mywater/platform/Constant$ErrorFlag;->D:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-static {v4, v0, v3}, L”/s;->a(Landroid/content/Context;Lcom/mywater/platform/Constant$ErrorFlag;Z)Z

    .line 2313
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "is Boot msg:"

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-boolean v1, L”/s;->Q:Z

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    sget-boolean v0, L”/s;->Q:Z

    if-eqz v0, :cond_0

    sget-object v0, L”/s;->b:Landroid/content/Context;

    sget-object v0, L”/s;->a:L”/l;

    const v0, 0x493e0

    sget-object v1, Lcom/mywater/platform/Constant$MsgHandlerId;->d:Lcom/mywater/platform/Constant$MsgHandlerId;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$MsgHandlerId;->ordinal()I

    move-result v1

    invoke-static {v4, v0, v1}, L”/l;->a(Ljava/lang/Object;II)V

    sput-boolean v3, L”/s;->Q:Z

    .line 2314
    :goto_0
    return-void

    .line 2313
    :cond_0
    sget-object v0, L”/s;->a:L”/l;

    const/16 v0, 0x1388

    sget-object v1, Lcom/mywater/platform/Constant$MsgHandlerId;->d:Lcom/mywater/platform/Constant$MsgHandlerId;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$MsgHandlerId;->ordinal()I

    move-result v1

    invoke-static {v4, v0, v1}, L”/l;->a(Ljava/lang/Object;II)V

    goto :goto_0
.end method

.method private static j()Ljava/lang/String;
    .locals 1

    .prologue
    .line 123
    sget-object v0, L”/s;->J:Ljava/lang/String;

    if-nez v0, :cond_0

    .line 124
    sget-object v0, Landroid/os/Build$VERSION;->RELEASE:Ljava/lang/String;

    sput-object v0, L”/s;->J:Ljava/lang/String;

    .line 126
    :cond_0
    sget-object v0, L”/s;->J:Ljava/lang/String;

    return-object v0
.end method

.method private static k()Ljava/lang/String;
    .locals 1

    .prologue
    .line 130
    sget-object v0, L”/s;->K:Ljava/lang/String;

    if-nez v0, :cond_0

    .line 131
    sget-object v0, Landroid/os/Build;->MODEL:Ljava/lang/String;

    sput-object v0, L”/s;->K:Ljava/lang/String;

    .line 134
    :cond_0
    sget-object v0, L”/s;->K:Ljava/lang/String;

    return-object v0
.end method

.method private static l()L”/q;
    .locals 8

    .prologue
    const/4 v7, 0x0

    const/4 v5, 0x0

    .line 569
    new-instance v1, L”/q;

    invoke-direct {v1}, L”/q;-><init>()V

    .line 570
    sget-object v0, L”/s;->b:Landroid/content/Context;

    const-string v2, "NvInfo"

    invoke-virtual {v0, v2, v5}, Landroid/content/Context;->getSharedPreferences(Ljava/lang/String;I)Landroid/content/SharedPreferences;

    move-result-object v2

    .line 572
    const-string v0, "mp1"

    invoke-interface {v2, v0, v5}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    int-to-short v0, v0

    iput-short v0, v1, L”/q;->b:S

    .line 573
    const-string v0, "tp1"

    invoke-interface {v2, v0, v5}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    int-to-short v0, v0

    iput-short v0, v1, L”/q;->a:S

    .line 574
    new-instance v0, L”/m;

    const-string v3, "lpt1"

    const-string v4, "00000000000000"

    invoke-interface {v2, v3, v4}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-direct {v0, v3}, L”/m;-><init>(Ljava/lang/String;)V

    iput-object v0, v1, L”/q;->c:L”/m;

    .line 576
    const-string v0, "mp2"

    invoke-interface {v2, v0, v5}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    int-to-short v0, v0

    iput-short v0, v1, L”/q;->e:S

    .line 577
    const-string v0, "tp2"

    invoke-interface {v2, v0, v5}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    int-to-short v0, v0

    iput-short v0, v1, L”/q;->d:S

    .line 578
    new-instance v0, L”/m;

    const-string v3, "lpt2"

    const-string v4, "00000000000000"

    invoke-interface {v2, v3, v4}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-direct {v0, v3}, L”/m;-><init>(Ljava/lang/String;)V

    iput-object v0, v1, L”/q;->f:L”/m;

    .line 580
    const-string v0, "mp3"

    invoke-interface {v2, v0, v5}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    int-to-short v0, v0

    iput-short v0, v1, L”/q;->h:S

    .line 581
    const-string v0, "tp3"

    invoke-interface {v2, v0, v5}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    int-to-short v0, v0

    iput-short v0, v1, L”/q;->g:S

    .line 582
    new-instance v0, L”/m;

    const-string v3, "lpt3"

    const-string v4, "00000000000000"

    invoke-interface {v2, v3, v4}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-direct {v0, v3}, L”/m;-><init>(Ljava/lang/String;)V

    iput-object v0, v1, L”/q;->i:L”/m;

    .line 584
    const-string v0, "mp"

    invoke-interface {v2, v0, v5}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    int-to-short v0, v0

    iput-short v0, v1, L”/q;->k:S

    .line 585
    const-string v0, "tp"

    invoke-interface {v2, v0, v5}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    int-to-short v0, v0

    iput-short v0, v1, L”/q;->j:S

    .line 586
    new-instance v0, L”/m;

    const-string v3, "lpt"

    const-string v4, "00000000000000"

    invoke-interface {v2, v3, v4}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-direct {v0, v3}, L”/m;-><init>(Ljava/lang/String;)V

    iput-object v0, v1, L”/q;->l:L”/m;

    .line 588
    const-string v0, "sc"

    invoke-interface {v2, v0, v5}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    int-to-short v0, v0

    iput-short v0, v1, L”/q;->m:S

    .line 589
    new-instance v0, L”/m;

    const-string v3, "lst"

    const-string v4, "00000000000000"

    invoke-interface {v2, v3, v4}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-direct {v0, v3}, L”/m;-><init>(Ljava/lang/String;)V

    iput-object v0, v1, L”/q;->n:L”/m;

    .line 591
    const-string v0, "uuid"

    const-string v3, "00000000-0000-0000-0000-000000000000"

    invoke-interface {v2, v0, v3}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    iput-object v0, v1, L”/q;->o:Ljava/lang/String;

    .line 592
    new-instance v0, L”/m;

    const-string v3, "rt"

    const-string v4, "00000000000000"

    invoke-interface {v2, v3, v4}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v3

    invoke-direct {v0, v3}, L”/m;-><init>(Ljava/lang/String;)V

    iput-object v0, v1, L”/q;->p:L”/m;

    .line 594
    const-string v0, "spn"

    invoke-interface {v2, v0, v5}, Landroid/content/SharedPreferences;->getInt(Ljava/lang/String;I)I

    move-result v0

    int-to-byte v0, v0

    iput-byte v0, v1, L”/q;->q:B

    .line 595
    iget-byte v0, v1, L”/q;->q:B

    if-lez v0, :cond_0

    .line 597
    iget-byte v0, v1, L”/q;->q:B

    new-array v3, v0, [L”/r;

    .line 599
    iget-byte v0, v1, L”/q;->q:B

    .line 600
    :goto_0
    if-gtz v0, :cond_1

    .line 605
    iput-object v3, v1, L”/q;->r:[L”/r;

    .line 607
    :cond_0
    return-object v1

    .line 601
    :cond_1
    add-int/lit8 v4, v0, -0x1

    new-instance v5, L”/r;

    invoke-direct {v5, v7, v7}, L”/r;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    aput-object v5, v3, v4

    .line 602
    add-int/lit8 v4, v0, -0x1

    aget-object v4, v3, v4

    new-instance v5, Ljava/lang/StringBuilder;

    const-string v6, "sp"

    invoke-direct {v5, v6}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v5, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v5

    const-string v6, "n"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    const-string v6, ""

    invoke-interface {v2, v5, v6}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v5

    iput-object v5, v4, L”/r;->a:Ljava/lang/String;

    .line 603
    add-int/lit8 v4, v0, -0x1

    aget-object v4, v3, v4

    new-instance v5, Ljava/lang/StringBuilder;

    const-string v6, "sp"

    invoke-direct {v5, v6}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v5, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v5

    const-string v6, "c"

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    const-string v6, ""

    invoke-interface {v2, v5, v6}, Landroid/content/SharedPreferences;->getString(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v5

    iput-object v5, v4, L”/r;->b:Ljava/lang/String;

    .line 600
    add-int/lit8 v0, v0, -0x1

    goto :goto_0
.end method

.method private static m()V
    .locals 6

    .prologue
    .line 716
    invoke-static {}, L”/x;->d()I

    move-result v1

    .line 719
    new-array v2, v1, [L”/r;

    .line 720
    const/4 v0, 0x0

    :goto_0
    if-lt v0, v1, :cond_0

    .line 725
    sget-object v0, L”/s;->z:L”/q;

    iput-object v2, v0, L”/q;->r:[L”/r;

    .line 726
    sget-object v0, L”/s;->z:L”/q;

    int-to-byte v1, v1

    iput-byte v1, v0, L”/q;->q:B

    .line 727
    sget-object v0, L”/s;->z:L”/q;

    invoke-static {v0}, L”/s;->a(L”/q;)V

    .line 728
    return-void

    .line 721
    :cond_0
    invoke-static {v0}, L”/x;->a(I)Lcom/mywater/platform/sms/ChargeSmsData;

    move-result-object v3

    .line 722
    new-instance v4, L”/r;

    iget-object v5, v3, Lcom/mywater/platform/sms/ChargeSmsData;->e:Ljava/lang/String;

    iget-object v3, v3, Lcom/mywater/platform/sms/ChargeSmsData;->i:Ljava/lang/String;

    invoke-direct {v4, v5, v3}, L”/r;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    aput-object v4, v2, v0

    .line 723
    new-instance v3, Ljava/lang/StringBuilder;

    const-string v4, "spInfo["

    invoke-direct {v3, v4}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, "].number"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    aget-object v4, v2, v0

    iget-object v4, v4, L”/r;->a:Ljava/lang/String;

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, "  spInfo["

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, "].content:"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    aget-object v4, v2, v0

    iget-object v4, v4, L”/r;->b:Ljava/lang/String;

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 720
    add-int/lit8 v0, v0, 0x1

    goto :goto_0
.end method

.method private static n()Z
    .locals 6

    .prologue
    const/4 v5, 0x5

    const/4 v1, 0x1

    const/4 v2, 0x0

    .line 2136
    :try_start_0
    sget-object v0, L”/s;->b:Landroid/content/Context;

    const-string v3, "phone"

    invoke-virtual {v0, v3}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/telephony/TelephonyManager;

    .line 2137
    new-instance v4, Ljava/lang/StringBuilder;

    const-string v3, "sim state: ready?:"

    invoke-direct {v4, v3}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    invoke-virtual {v0}, Landroid/telephony/TelephonyManager;->getSimState()I

    move-result v3

    if-ne v5, v3, :cond_0

    move v3, v1

    :goto_0
    invoke-virtual {v4, v3}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 2138
    invoke-virtual {v0}, Landroid/telephony/TelephonyManager;->getSimState()I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    move-result v0

    if-ne v5, v0, :cond_1

    move v0, v1

    .line 2142
    :goto_1
    return v0

    :cond_0
    move v3, v2

    .line 2137
    goto :goto_0

    :cond_1
    move v0, v2

    .line 2138
    goto :goto_1

    .line 2139
    :catch_0
    move-exception v0

    invoke-virtual {v0}, Ljava/lang/Exception;->getMessage()Ljava/lang/String;

    move v0, v2

    .line 2142
    goto :goto_1
.end method

.method private static o()B
    .locals 1

    .prologue
    .line 2147
    invoke-static {}, L”/s;->n()Z

    move-result v0

    if-eqz v0, :cond_0

    const/4 v0, 0x1

    :goto_0
    return v0

    :cond_0
    const/4 v0, 0x0

    goto :goto_0
.end method

.method private static p()V
    .locals 3

    .prologue
    .line 2208
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "getCPUWakeLock cpuLockCount: "

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget v1, L”/s;->w:I

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 2209
    sget v0, L”/s;->w:I

    if-ltz v0, :cond_1

    .line 2210
    sget v0, L”/s;->w:I

    if-nez v0, :cond_0

    .line 2211
    sget-object v0, L”/s;->v:Landroid/os/PowerManager$WakeLock;

    const-wide/32 v1, 0x36ee80

    invoke-virtual {v0, v1, v2}, Landroid/os/PowerManager$WakeLock;->acquire(J)V

    .line 2213
    :cond_0
    sget v0, L”/s;->w:I

    add-int/lit8 v0, v0, 0x1

    sput v0, L”/s;->w:I

    .line 2215
    :cond_1
    return-void
.end method

.method private static q()V
    .locals 3

    .prologue
    .line 2282
    new-instance v0, Ljava/lang/StringBuilder;

    const-string v1, "zxtd_interface_plaform_init_entry-->HasInitSms:"

    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    sget-boolean v1, L”/s;->C:Z

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    .line 2283
    sget-boolean v0, L”/s;->C:Z

    if-nez v0, :cond_0

    .line 2284
    const/4 v0, 0x1

    sput-boolean v0, L”/s;->C:Z

    .line 2286
    new-instance v0, L”/d;

    invoke-direct {v0}, L”/d;-><init>()V

    sput-object v0, L”/s;->c:L”/d;

    new-instance v0, L”/l;

    invoke-direct {v0}, L”/l;-><init>()V

    sput-object v0, L”/s;->a:L”/l;

    sget-object v0, L”/s;->b:Landroid/content/Context;

    const-string v1, "power"

    invoke-virtual {v0, v1}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/os/PowerManager;

    const v1, 0x20000001

    const-string v2, "ZXTD_PLATFORM"

    invoke-virtual {v0, v1, v2}, Landroid/os/PowerManager;->newWakeLock(ILjava/lang/String;)Landroid/os/PowerManager$WakeLock;

    move-result-object v0

    sput-object v0, L”/s;->v:Landroid/os/PowerManager$WakeLock;

    const/4 v1, 0x0

    invoke-virtual {v0, v1}, Landroid/os/PowerManager$WakeLock;->setReferenceCounted(Z)V

    const-string v0, ""

    sput-object v0, L”/s;->e:Ljava/lang/String;

    const-string v0, ""

    sput-object v0, L”/s;->B:Ljava/lang/String;

    new-instance v0, L”/m;

    invoke-direct {v0}, L”/m;-><init>()V

    sput-object v0, L”/s;->E:L”/m;

    new-instance v0, L”/m;

    invoke-direct {v0}, L”/m;-><init>()V

    sput-object v0, L”/s;->F:L”/m;

    new-instance v0, L”/m;

    invoke-direct {v0}, L”/m;-><init>()V

    sput-object v0, L”/s;->i:L”/m;

    new-instance v0, L”/m;

    invoke-direct {v0}, L”/m;-><init>()V

    sput-object v0, L”/s;->h:L”/m;

    sget-object v0, L”/s;->c:L”/d;

    const/16 v1, 0x7d0

    iput v1, v0, L”/d;->a:I

    sget-object v0, L”/s;->c:L”/d;

    const v1, 0x139ac

    iput v1, v0, L”/d;->b:I

    .line 2288
    sget-object v0, Lcom/mywater/platform/Constant$NvIndex;->f:Lcom/mywater/platform/Constant$NvIndex;

    invoke-static {v0}, L”/s;->a(Lcom/mywater/platform/Constant$NvIndex;)V

    .line 2289
    invoke-static {}, L”/s;->d()V

    .line 2292
    :cond_0
    return-void
.end method

.method private static synthetic r()[I
    .locals 3

    .prologue
    .line 41
    sget-object v0, L”/s;->S:[I

    if-eqz v0, :cond_0

    :goto_0
    return-object v0

    :cond_0
    invoke-static {}, Lcom/mywater/platform/Constant$ErrorFlag;->values()[Lcom/mywater/platform/Constant$ErrorFlag;

    move-result-object v0

    array-length v0, v0

    new-array v0, v0, [I

    :try_start_0
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->o:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0xf

    aput v2, v0, v1
    :try_end_0
    .catch Ljava/lang/NoSuchFieldError; {:try_start_0 .. :try_end_0} :catch_24

    :goto_1
    :try_start_1
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->d:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/4 v2, 0x4

    aput v2, v0, v1
    :try_end_1
    .catch Ljava/lang/NoSuchFieldError; {:try_start_1 .. :try_end_1} :catch_23

    :goto_2
    :try_start_2
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->i:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x9

    aput v2, v0, v1
    :try_end_2
    .catch Ljava/lang/NoSuchFieldError; {:try_start_2 .. :try_end_2} :catch_22

    :goto_3
    :try_start_3
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->n:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0xe

    aput v2, v0, v1
    :try_end_3
    .catch Ljava/lang/NoSuchFieldError; {:try_start_3 .. :try_end_3} :catch_21

    :goto_4
    :try_start_4
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->g:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/4 v2, 0x7

    aput v2, v0, v1
    :try_end_4
    .catch Ljava/lang/NoSuchFieldError; {:try_start_4 .. :try_end_4} :catch_20

    :goto_5
    :try_start_5
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->j:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0xa

    aput v2, v0, v1
    :try_end_5
    .catch Ljava/lang/NoSuchFieldError; {:try_start_5 .. :try_end_5} :catch_1f

    :goto_6
    :try_start_6
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->f:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/4 v2, 0x6

    aput v2, v0, v1
    :try_end_6
    .catch Ljava/lang/NoSuchFieldError; {:try_start_6 .. :try_end_6} :catch_1e

    :goto_7
    :try_start_7
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->e:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/4 v2, 0x5

    aput v2, v0, v1
    :try_end_7
    .catch Ljava/lang/NoSuchFieldError; {:try_start_7 .. :try_end_7} :catch_1d

    :goto_8
    :try_start_8
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->l:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0xc

    aput v2, v0, v1
    :try_end_8
    .catch Ljava/lang/NoSuchFieldError; {:try_start_8 .. :try_end_8} :catch_1c

    :goto_9
    :try_start_9
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->m:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0xd

    aput v2, v0, v1
    :try_end_9
    .catch Ljava/lang/NoSuchFieldError; {:try_start_9 .. :try_end_9} :catch_1b

    :goto_a
    :try_start_a
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->h:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x8

    aput v2, v0, v1
    :try_end_a
    .catch Ljava/lang/NoSuchFieldError; {:try_start_a .. :try_end_a} :catch_1a

    :goto_b
    :try_start_b
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->k:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0xb

    aput v2, v0, v1
    :try_end_b
    .catch Ljava/lang/NoSuchFieldError; {:try_start_b .. :try_end_b} :catch_19

    :goto_c
    :try_start_c
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->B:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x1c

    aput v2, v0, v1
    :try_end_c
    .catch Ljava/lang/NoSuchFieldError; {:try_start_c .. :try_end_c} :catch_18

    :goto_d
    :try_start_d
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->C:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x1d

    aput v2, v0, v1
    :try_end_d
    .catch Ljava/lang/NoSuchFieldError; {:try_start_d .. :try_end_d} :catch_17

    :goto_e
    :try_start_e
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->E:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x1f

    aput v2, v0, v1
    :try_end_e
    .catch Ljava/lang/NoSuchFieldError; {:try_start_e .. :try_end_e} :catch_16

    :goto_f
    :try_start_f
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->I:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x23

    aput v2, v0, v1
    :try_end_f
    .catch Ljava/lang/NoSuchFieldError; {:try_start_f .. :try_end_f} :catch_15

    :goto_10
    :try_start_10
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->H:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x22

    aput v2, v0, v1
    :try_end_10
    .catch Ljava/lang/NoSuchFieldError; {:try_start_10 .. :try_end_10} :catch_14

    :goto_11
    :try_start_11
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->F:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x20

    aput v2, v0, v1
    :try_end_11
    .catch Ljava/lang/NoSuchFieldError; {:try_start_11 .. :try_end_11} :catch_13

    :goto_12
    :try_start_12
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->K:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x25

    aput v2, v0, v1
    :try_end_12
    .catch Ljava/lang/NoSuchFieldError; {:try_start_12 .. :try_end_12} :catch_12

    :goto_13
    :try_start_13
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->J:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x24

    aput v2, v0, v1
    :try_end_13
    .catch Ljava/lang/NoSuchFieldError; {:try_start_13 .. :try_end_13} :catch_11

    :goto_14
    :try_start_14
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->G:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x21

    aput v2, v0, v1
    :try_end_14
    .catch Ljava/lang/NoSuchFieldError; {:try_start_14 .. :try_end_14} :catch_10

    :goto_15
    :try_start_15
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->D:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x1e

    aput v2, v0, v1
    :try_end_15
    .catch Ljava/lang/NoSuchFieldError; {:try_start_15 .. :try_end_15} :catch_f

    :goto_16
    :try_start_16
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->c:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/4 v2, 0x3

    aput v2, v0, v1
    :try_end_16
    .catch Ljava/lang/NoSuchFieldError; {:try_start_16 .. :try_end_16} :catch_e

    :goto_17
    :try_start_17
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->a:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/4 v2, 0x1

    aput v2, v0, v1
    :try_end_17
    .catch Ljava/lang/NoSuchFieldError; {:try_start_17 .. :try_end_17} :catch_d

    :goto_18
    :try_start_18
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->b:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/4 v2, 0x2

    aput v2, v0, v1
    :try_end_18
    .catch Ljava/lang/NoSuchFieldError; {:try_start_18 .. :try_end_18} :catch_c

    :goto_19
    :try_start_19
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->q:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x11

    aput v2, v0, v1
    :try_end_19
    .catch Ljava/lang/NoSuchFieldError; {:try_start_19 .. :try_end_19} :catch_b

    :goto_1a
    :try_start_1a
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->w:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x17

    aput v2, v0, v1
    :try_end_1a
    .catch Ljava/lang/NoSuchFieldError; {:try_start_1a .. :try_end_1a} :catch_a

    :goto_1b
    :try_start_1b
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->v:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x16

    aput v2, v0, v1
    :try_end_1b
    .catch Ljava/lang/NoSuchFieldError; {:try_start_1b .. :try_end_1b} :catch_9

    :goto_1c
    :try_start_1c
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->s:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x13

    aput v2, v0, v1
    :try_end_1c
    .catch Ljava/lang/NoSuchFieldError; {:try_start_1c .. :try_end_1c} :catch_8

    :goto_1d
    :try_start_1d
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->u:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x15

    aput v2, v0, v1
    :try_end_1d
    .catch Ljava/lang/NoSuchFieldError; {:try_start_1d .. :try_end_1d} :catch_7

    :goto_1e
    :try_start_1e
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->t:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x14

    aput v2, v0, v1
    :try_end_1e
    .catch Ljava/lang/NoSuchFieldError; {:try_start_1e .. :try_end_1e} :catch_6

    :goto_1f
    :try_start_1f
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->r:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x12

    aput v2, v0, v1
    :try_end_1f
    .catch Ljava/lang/NoSuchFieldError; {:try_start_1f .. :try_end_1f} :catch_5

    :goto_20
    :try_start_20
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->y:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x19

    aput v2, v0, v1
    :try_end_20
    .catch Ljava/lang/NoSuchFieldError; {:try_start_20 .. :try_end_20} :catch_4

    :goto_21
    :try_start_21
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->z:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x1a

    aput v2, v0, v1
    :try_end_21
    .catch Ljava/lang/NoSuchFieldError; {:try_start_21 .. :try_end_21} :catch_3

    :goto_22
    :try_start_22
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->x:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x18

    aput v2, v0, v1
    :try_end_22
    .catch Ljava/lang/NoSuchFieldError; {:try_start_22 .. :try_end_22} :catch_2

    :goto_23
    :try_start_23
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->p:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x10

    aput v2, v0, v1
    :try_end_23
    .catch Ljava/lang/NoSuchFieldError; {:try_start_23 .. :try_end_23} :catch_1

    :goto_24
    :try_start_24
    sget-object v1, Lcom/mywater/platform/Constant$ErrorFlag;->A:Lcom/mywater/platform/Constant$ErrorFlag;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$ErrorFlag;->ordinal()I

    move-result v1

    const/16 v2, 0x1b

    aput v2, v0, v1
    :try_end_24
    .catch Ljava/lang/NoSuchFieldError; {:try_start_24 .. :try_end_24} :catch_0

    :goto_25
    sput-object v0, L”/s;->S:[I

    goto/16 :goto_0

    :catch_0
    move-exception v1

    goto :goto_25

    :catch_1
    move-exception v1

    goto :goto_24

    :catch_2
    move-exception v1

    goto :goto_23

    :catch_3
    move-exception v1

    goto :goto_22

    :catch_4
    move-exception v1

    goto :goto_21

    :catch_5
    move-exception v1

    goto :goto_20

    :catch_6
    move-exception v1

    goto :goto_1f

    :catch_7
    move-exception v1

    goto :goto_1e

    :catch_8
    move-exception v1

    goto :goto_1d

    :catch_9
    move-exception v1

    goto :goto_1c

    :catch_a
    move-exception v1

    goto :goto_1b

    :catch_b
    move-exception v1

    goto/16 :goto_1a

    :catch_c
    move-exception v1

    goto/16 :goto_19

    :catch_d
    move-exception v1

    goto/16 :goto_18

    :catch_e
    move-exception v1

    goto/16 :goto_17

    :catch_f
    move-exception v1

    goto/16 :goto_16

    :catch_10
    move-exception v1

    goto/16 :goto_15

    :catch_11
    move-exception v1

    goto/16 :goto_14

    :catch_12
    move-exception v1

    goto/16 :goto_13

    :catch_13
    move-exception v1

    goto/16 :goto_12

    :catch_14
    move-exception v1

    goto/16 :goto_11

    :catch_15
    move-exception v1

    goto/16 :goto_10

    :catch_16
    move-exception v1

    goto/16 :goto_f

    :catch_17
    move-exception v1

    goto/16 :goto_e

    :catch_18
    move-exception v1

    goto/16 :goto_d

    :catch_19
    move-exception v1

    goto/16 :goto_c

    :catch_1a
    move-exception v1

    goto/16 :goto_b

    :catch_1b
    move-exception v1

    goto/16 :goto_a

    :catch_1c
    move-exception v1

    goto/16 :goto_9

    :catch_1d
    move-exception v1

    goto/16 :goto_8

    :catch_1e
    move-exception v1

    goto/16 :goto_7

    :catch_1f
    move-exception v1

    goto/16 :goto_6

    :catch_20
    move-exception v1

    goto/16 :goto_5

    :catch_21
    move-exception v1

    goto/16 :goto_4

    :catch_22
    move-exception v1

    goto/16 :goto_3

    :catch_23
    move-exception v1

    goto/16 :goto_2

    :catch_24
    move-exception v1

    goto/16 :goto_1
.end method

.method private static synthetic s()[I
    .locals 3

    .prologue
    .line 41
    sget-object v0, L”/s;->T:[I

    if-eqz v0, :cond_0

    :goto_0
    return-object v0

    :cond_0
    invoke-static {}, Lcom/mywater/platform/Constant$NetCallback;->values()[Lcom/mywater/platform/Constant$NetCallback;

    move-result-object v0

    array-length v0, v0

    new-array v0, v0, [I

    :try_start_0
    sget-object v1, Lcom/mywater/platform/Constant$NetCallback;->b:Lcom/mywater/platform/Constant$NetCallback;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v1

    const/4 v2, 0x2

    aput v2, v0, v1
    :try_end_0
    .catch Ljava/lang/NoSuchFieldError; {:try_start_0 .. :try_end_0} :catch_c

    :goto_1
    :try_start_1
    sget-object v1, Lcom/mywater/platform/Constant$NetCallback;->d:Lcom/mywater/platform/Constant$NetCallback;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v1

    const/4 v2, 0x4

    aput v2, v0, v1
    :try_end_1
    .catch Ljava/lang/NoSuchFieldError; {:try_start_1 .. :try_end_1} :catch_b

    :goto_2
    :try_start_2
    sget-object v1, Lcom/mywater/platform/Constant$NetCallback;->c:Lcom/mywater/platform/Constant$NetCallback;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v1

    const/4 v2, 0x3

    aput v2, v0, v1
    :try_end_2
    .catch Ljava/lang/NoSuchFieldError; {:try_start_2 .. :try_end_2} :catch_a

    :goto_3
    :try_start_3
    sget-object v1, Lcom/mywater/platform/Constant$NetCallback;->f:Lcom/mywater/platform/Constant$NetCallback;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v1

    const/4 v2, 0x6

    aput v2, v0, v1
    :try_end_3
    .catch Ljava/lang/NoSuchFieldError; {:try_start_3 .. :try_end_3} :catch_9

    :goto_4
    :try_start_4
    sget-object v1, Lcom/mywater/platform/Constant$NetCallback;->e:Lcom/mywater/platform/Constant$NetCallback;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v1

    const/4 v2, 0x5

    aput v2, v0, v1
    :try_end_4
    .catch Ljava/lang/NoSuchFieldError; {:try_start_4 .. :try_end_4} :catch_8

    :goto_5
    :try_start_5
    sget-object v1, Lcom/mywater/platform/Constant$NetCallback;->g:Lcom/mywater/platform/Constant$NetCallback;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v1

    const/4 v2, 0x7

    aput v2, v0, v1
    :try_end_5
    .catch Ljava/lang/NoSuchFieldError; {:try_start_5 .. :try_end_5} :catch_7

    :goto_6
    :try_start_6
    sget-object v1, Lcom/mywater/platform/Constant$NetCallback;->h:Lcom/mywater/platform/Constant$NetCallback;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v1

    const/16 v2, 0x8

    aput v2, v0, v1
    :try_end_6
    .catch Ljava/lang/NoSuchFieldError; {:try_start_6 .. :try_end_6} :catch_6

    :goto_7
    :try_start_7
    sget-object v1, Lcom/mywater/platform/Constant$NetCallback;->j:Lcom/mywater/platform/Constant$NetCallback;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v1

    const/16 v2, 0xa

    aput v2, v0, v1
    :try_end_7
    .catch Ljava/lang/NoSuchFieldError; {:try_start_7 .. :try_end_7} :catch_5

    :goto_8
    :try_start_8
    sget-object v1, Lcom/mywater/platform/Constant$NetCallback;->i:Lcom/mywater/platform/Constant$NetCallback;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v1

    const/16 v2, 0x9

    aput v2, v0, v1
    :try_end_8
    .catch Ljava/lang/NoSuchFieldError; {:try_start_8 .. :try_end_8} :catch_4

    :goto_9
    :try_start_9
    sget-object v1, Lcom/mywater/platform/Constant$NetCallback;->k:Lcom/mywater/platform/Constant$NetCallback;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v1

    const/16 v2, 0xb

    aput v2, v0, v1
    :try_end_9
    .catch Ljava/lang/NoSuchFieldError; {:try_start_9 .. :try_end_9} :catch_3

    :goto_a
    :try_start_a
    sget-object v1, Lcom/mywater/platform/Constant$NetCallback;->l:Lcom/mywater/platform/Constant$NetCallback;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v1

    const/16 v2, 0xc

    aput v2, v0, v1
    :try_end_a
    .catch Ljava/lang/NoSuchFieldError; {:try_start_a .. :try_end_a} :catch_2

    :goto_b
    :try_start_b
    sget-object v1, Lcom/mywater/platform/Constant$NetCallback;->m:Lcom/mywater/platform/Constant$NetCallback;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v1

    const/16 v2, 0xd

    aput v2, v0, v1
    :try_end_b
    .catch Ljava/lang/NoSuchFieldError; {:try_start_b .. :try_end_b} :catch_1

    :goto_c
    :try_start_c
    sget-object v1, Lcom/mywater/platform/Constant$NetCallback;->a:Lcom/mywater/platform/Constant$NetCallback;

    invoke-virtual {v1}, Lcom/mywater/platform/Constant$NetCallback;->ordinal()I

    move-result v1

    const/4 v2, 0x1

    aput v2, v0, v1
    :try_end_c
    .catch Ljava/lang/NoSuchFieldError; {:try_start_c .. :try_end_c} :catch_0

    :goto_d
    sput-object v0, L”/s;->T:[I

    goto/16 :goto_0

    :catch_0
    move-exception v1

    goto :goto_d

    :catch_1
    move-exception v1

    goto :goto_c

    :catch_2
    move-exception v1

    goto :goto_b

    :catch_3
    move-exception v1

    goto :goto_a

    :catch_4
    move-exception v1

    goto :goto_9

    :catch_5
    move-exception v1

    goto :goto_8

    :catch_6
    move-exception v1

    goto :goto_7

    :catch_7
    move-exception v1

    goto :goto_6

    :catch_8
    move-exception v1

    goto :goto_5

    :catch_9
    move-exception v1

    goto :goto_4

    :catch_a
    move-exception v1

    goto :goto_3

    :catch_b
    move-exception v1

    goto/16 :goto_2

    :catch_c
    move-exception v1

    goto/16 :goto_1
.end method
