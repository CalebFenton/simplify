.class public Ljmy/$$$5$;
.super Landroid/content/BroadcastReceiver;
.source "jmy/$$$5$.j"


# static fields
.field public static $SS:Z

.field public static Ill1:Ljava/lang/Boolean;


# instance fields
.field private $55:Ljava/lang/String;

.field private IlIl:Landroid/content/SharedPreferences;

.field private S$$:Landroid/os/Handler;


# direct methods
.method constructor <clinit>()V
    .locals 2

    new-instance v0, Ljava/lang/Boolean;

    const/4 v1, 0x1

    invoke-direct {v0, v1}, Ljava/lang/Boolean;-><init>(Z)V

    sput-object v0, Ljmy/$$$5$;->Ill1:Ljava/lang/Boolean;

    return-void
.end method

# virtual methods
.method public GCGQO(Ljava/lang/String;I)V
    .locals 19

    const/16 v1, 0xb

    new-array v5, v1, [J

    const/16 v1, 0x8

    new-array v6, v1, [I

    new-instance v7, Ljava/simplify.util/HashMap;

    invoke-direct {v7}, Ljava/simplify.util/HashMap;-><init>()V

    const-string v3, "\uf54f\ue650\u4c1e\u5d78"

    const-string v4, "\uf4e1\ue5db\u4bb2\u5d0c\u27a9"

    invoke-virtual {v3}, Ljava/lang/String;->length()I

    move-result v1

    const/4 v2, 0x0

    aput v1, v6, v2

    const/4 v2, 0x0

    div-int/lit8 v8, v1, 0x21

    const/4 v9, 0x4

    rem-int/lit8 v1, v1, 0x21

    aput v1, v6, v9

    mul-int/lit8 v8, v8, 0x21

    aput v8, v6, v2

    const/4 v2, 0x0

    add-int/2addr v1, v8

    aput v1, v6, v2

    new-array v8, v1, [C

    const/4 v2, 0x0

    const/4 v1, 0x0

    const/4 v9, 0x4

    const/4 v10, 0x0

    aput v10, v6, v9

    :goto_0
    invoke-virtual {v3}, Ljava/lang/String;->length()I

    move-result v9

    const/4 v10, 0x1

    aput v9, v6, v10

    div-int/lit8 v10, v9, 0x21

    rem-int/lit8 v9, v9, 0x21

    if-lt v2, v10, :cond_2

    if-ne v2, v10, :cond_0

    const/4 v1, 0x4

    aget v1, v6, v1

    if-lt v1, v9, :cond_2

    :cond_0
    new-instance v9, Ljava/lang/String;

    invoke-direct {v9, v8}, Ljava/lang/String;-><init>([C)V

    const-string v8, "\u135b\u0cf9\uf86b\u49bc\uf4fa\u6e15\ua904\u19d0\ub494\ued2c\ub799\u87f1\u521f\u8a2e\u2430\u9405\ucdb7\u454f"

    const/4 v1, 0x2

    invoke-virtual {v8}, Ljava/lang/String;->length()I

    move-result v2

    aput v2, v6, v1

    const/4 v1, 0x0

    int-to-long v2, v2

    aput-wide v2, v5, v1

    const/4 v1, 0x0

    const-wide/16 v10, 0x180f

    mul-long/2addr v2, v10

    aput-wide v2, v5, v1

    const/4 v1, 0x0

    const-wide/16 v10, 0x1ee2

    add-long/2addr v2, v10

    aput-wide v2, v5, v1

    const/4 v1, 0x1

    const-wide/16 v10, 0x1ee2

    sub-long/2addr v2, v10

    aput-wide v2, v5, v1

    const/4 v1, 0x1

    const-wide/16 v10, 0x180f

    div-long/2addr v2, v10

    aput-wide v2, v5, v1

    const/4 v1, 0x2

    long-to-int v2, v2

    aput v2, v6, v1

    new-array v10, v2, [C

    const/4 v1, 0x0

    const-wide/16 v2, 0x0

    aput-wide v2, v5, v1

    const-wide/16 v1, 0x1ee2

    const/4 v3, 0x0

    const-wide/16 v11, 0x1ee2

    aput-wide v11, v5, v3

    const-wide/32 v3, 0x1cb6bb9

    :goto_1
    const/16 v11, 0xa

    invoke-virtual {v8}, Ljava/lang/String;->length()I

    move-result v12

    int-to-long v12, v12

    const-wide/16 v14, 0x180f

    mul-long/2addr v12, v14

    const-wide/16 v14, 0x21c1

    add-long/2addr v12, v14

    const/16 v14, 0x9

    const-wide/16 v15, 0x1ee2

    sub-long v15, v1, v15

    aput-wide v15, v5, v14

    const/16 v14, 0x9

    const-wide/16 v17, 0x180f

    div-long v15, v15, v17

    aput-wide v15, v5, v14

    const/4 v14, 0x2

    long-to-int v15, v15

    aput v15, v6, v14

    const-wide/16 v14, 0x21c1

    sub-long/2addr v12, v14

    aput-wide v12, v5, v11

    const/16 v11, 0xa

    const-wide/16 v14, 0x180f

    div-long/2addr v12, v14

    aput-wide v12, v5, v11

    const/4 v11, 0x2

    aget v11, v6, v11

    long-to-int v12, v12

    if-lt v11, v12, :cond_3

    new-instance v1, Ljava/lang/String;

    invoke-direct {v1, v10}, Ljava/lang/String;-><init>([C)V

    move-object/from16 v0, p1

    if-ne v0, v1, :cond_4

    :cond_1
    :goto_2
    return-void

    :cond_2
    const/4 v9, 0x0

    mul-int/lit8 v10, v2, 0x21

    aput v10, v6, v9

    const/4 v9, 0x0

    add-int/2addr v10, v1

    aput v10, v6, v9

    const/high16 v9, 0x10000

    invoke-virtual {v3, v10}, Ljava/lang/String;->charAt(I)C

    move-result v10

    invoke-virtual {v4}, Ljava/lang/String;->length()I

    move-result v11

    const/4 v12, 0x1

    aput v11, v6, v12

    const/4 v12, 0x1

    div-int/lit8 v13, v11, 0x21

    rem-int/lit8 v11, v11, 0x21

    const/4 v14, 0x0

    mul-int/lit8 v15, v2, 0x21

    aput v15, v6, v14

    const/4 v14, 0x0

    add-int/2addr v1, v15

    aput v1, v6, v14

    mul-int/lit8 v13, v13, 0x21

    aput v13, v6, v12

    const/4 v12, 0x1

    add-int/2addr v11, v13

    aput v11, v6, v12

    rem-int/2addr v1, v11

    const/4 v11, 0x1

    aput v1, v6, v11

    const/4 v11, 0x1

    div-int/lit8 v12, v1, 0x21

    rem-int/lit8 v1, v1, 0x21

    mul-int/lit8 v12, v12, 0x21

    aput v12, v6, v11

    const/4 v11, 0x1

    add-int/2addr v1, v12

    aput v1, v6, v11

    invoke-virtual {v4, v1}, Ljava/lang/String;->charAt(I)C

    move-result v1

    add-int/2addr v9, v10

    const/4 v10, 0x1

    aput v9, v6, v10

    const/4 v10, 0x1

    div-int/lit8 v11, v9, 0x21

    rem-int/lit8 v9, v9, 0x21

    const/4 v12, 0x5

    div-int/lit8 v13, v1, 0x21

    aput v13, v6, v12

    const/4 v12, 0x6

    rem-int/lit8 v1, v1, 0x21

    aput v1, v6, v12

    sub-int v1, v9, v1

    sub-int v9, v11, v13

    div-int/lit8 v11, v1, 0x21

    add-int/2addr v9, v11

    mul-int/lit8 v9, v9, 0x21

    aput v9, v6, v10

    const/4 v10, 0x1

    rem-int/lit8 v1, v1, 0x21

    add-int/2addr v1, v9

    aput v1, v6, v10

    const/high16 v9, 0x10000

    rem-int/2addr v1, v9

    const/4 v9, 0x1

    aput v1, v6, v9

    const/4 v9, 0x1

    div-int/lit8 v10, v1, 0x21

    rem-int/lit8 v1, v1, 0x21

    mul-int/lit8 v10, v10, 0x21

    aput v10, v6, v9

    const/4 v9, 0x1

    add-int/2addr v1, v10

    aput v1, v6, v9

    const/4 v9, 0x0

    mul-int/lit8 v10, v2, 0x21

    aput v10, v6, v9

    const/4 v9, 0x4

    aget v9, v6, v9

    const/4 v11, 0x0

    add-int/2addr v10, v9

    aput v10, v6, v11

    int-to-char v1, v1

    aput-char v1, v8, v10

    const/4 v1, 0x7

    const/4 v10, 0x0

    aput v10, v6, v1

    add-int/lit8 v1, v9, 0x1

    const/4 v9, 0x4

    aput v1, v6, v9

    add-int/lit8 v2, v2, 0x0

    div-int/lit8 v9, v1, 0x21

    add-int/2addr v2, v9

    rem-int/lit8 v1, v1, 0x21

    const/4 v9, 0x4

    aput v1, v6, v9

    goto/16 :goto_0

    :cond_3
    const/4 v11, 0x2

    const-wide/16 v12, 0x1ee2

    sub-long/2addr v1, v12

    const-wide/16 v12, 0x180f

    div-long/2addr v1, v12

    long-to-int v1, v1

    aput v1, v6, v11

    const/4 v2, 0x2

    const/high16 v11, 0x10000

    invoke-virtual {v8, v1}, Ljava/lang/String;->charAt(I)C

    move-result v1

    add-int/2addr v1, v11

    int-to-long v11, v1

    const-wide/16 v13, 0x180f

    mul-long/2addr v11, v13

    const-wide/16 v13, 0x21c1

    add-long/2addr v11, v13

    const-wide/16 v13, 0x21c1

    sub-long/2addr v11, v13

    aput-wide v11, v5, v2

    const/4 v1, 0x2

    const-wide/16 v13, 0x180f

    div-long/2addr v11, v13

    aput-wide v11, v5, v1

    const/4 v1, 0x6

    long-to-int v2, v11

    const/4 v11, 0x3

    const-wide/16 v12, 0xd51

    sub-long v12, v3, v12

    const-wide/16 v14, 0x180f

    div-long/2addr v12, v14

    long-to-int v12, v12

    aput v12, v6, v11

    sub-int/2addr v2, v12

    int-to-long v11, v2

    const-wide/16 v13, 0x180f

    mul-long/2addr v11, v13

    const-wide/16 v13, 0x21c1

    add-long/2addr v11, v13

    const/4 v2, 0x3

    const-wide/16 v13, 0xd51

    sub-long/2addr v3, v13

    aput-wide v3, v5, v2

    const/4 v2, 0x3

    const-wide/16 v13, 0x180f

    div-long/2addr v3, v13

    aput-wide v3, v5, v2

    const/4 v2, 0x3

    long-to-int v3, v3

    aput v3, v6, v2

    const/4 v2, 0x3

    mul-int/lit16 v3, v3, 0x7261

    aput v3, v6, v2

    const/4 v2, 0x5

    int-to-long v3, v3

    const-wide/16 v13, 0x180f

    mul-long/2addr v3, v13

    const-wide/16 v13, 0xd51

    add-long/2addr v3, v13

    const/4 v13, 0x4

    const-wide/32 v14, 0x30fe0bb

    aput-wide v14, v5, v13

    const/4 v13, 0x4

    const-wide/32 v14, 0x30fee0c

    aput-wide v14, v5, v13

    const-wide/16 v13, 0xd51

    sub-long/2addr v3, v13

    aput-wide v3, v5, v2

    const/4 v2, 0x5

    const-wide/16 v13, 0x180f

    div-long/2addr v3, v13

    aput-wide v3, v5, v2

    long-to-int v2, v3

    const/4 v3, 0x3

    aput v2, v6, v3

    add-int/lit16 v2, v2, 0x2095

    const/4 v3, 0x3

    aput v2, v6, v3

    const/4 v3, 0x3

    int-to-long v13, v2

    const-wide/16 v15, 0x180f

    mul-long/2addr v13, v15

    const-wide/16 v15, 0xd51

    add-long/2addr v13, v15

    const-wide/16 v15, 0xd51

    sub-long/2addr v13, v15

    const-wide/16 v15, 0x180f

    div-long/2addr v13, v15

    long-to-int v2, v13

    aput v2, v6, v3

    const/4 v3, 0x3

    const/high16 v4, 0x10000

    rem-int/2addr v2, v4

    aput v2, v6, v3

    int-to-long v2, v2

    const-wide/16 v13, 0x180f

    mul-long/2addr v2, v13

    const-wide/16 v13, 0xd51

    add-long v3, v2, v13

    const-wide/16 v13, 0x21c1

    sub-long/2addr v11, v13

    aput-wide v11, v5, v1

    const/4 v1, 0x6

    const-wide/16 v13, 0x180f

    div-long/2addr v11, v13

    aput-wide v11, v5, v1

    const/4 v1, 0x7

    const/4 v2, 0x0

    aget-wide v13, v5, v2

    const-wide/16 v15, 0x1ee2

    sub-long/2addr v13, v15

    aput-wide v13, v5, v1

    const/4 v1, 0x7

    const-wide/16 v15, 0x180f

    div-long/2addr v13, v15

    aput-wide v13, v5, v1

    const/4 v1, 0x2

    long-to-int v2, v13

    aput v2, v6, v1

    long-to-int v1, v11

    const/high16 v11, 0x10000

    rem-int/2addr v1, v11

    int-to-long v11, v1

    const-wide/16 v13, 0x180f

    mul-long/2addr v11, v13

    const-wide/16 v13, 0x21c1

    add-long/2addr v11, v13

    const-wide/16 v13, 0x21c1

    sub-long/2addr v11, v13

    const-wide/16 v13, 0x180f

    div-long/2addr v11, v13

    long-to-int v1, v11

    int-to-char v1, v1

    aput-char v1, v10, v2

    const/16 v1, 0x8

    const-wide/16 v11, 0x180f

    aput-wide v11, v5, v1

    const/16 v1, 0x8

    const-wide/16 v11, 0x36f1

    aput-wide v11, v5, v1

    const/4 v1, 0x0

    aget-wide v1, v5, v1

    const-wide/16 v11, 0x1ee2

    sub-long/2addr v1, v11

    const-wide/16 v11, 0x180f

    div-long/2addr v1, v11

    long-to-int v1, v1

    const/4 v2, 0x2

    aput v1, v6, v2

    add-int/lit8 v1, v1, 0x1

    const/4 v2, 0x2

    aput v1, v6, v2

    const/4 v2, 0x0

    int-to-long v11, v1

    aput-wide v11, v5, v2

    const/4 v1, 0x0

    const-wide/16 v13, 0x180f

    mul-long/2addr v11, v13

    aput-wide v11, v5, v1

    const-wide/16 v1, 0x1ee2

    add-long/2addr v1, v11

    const/4 v11, 0x0

    aput-wide v1, v5, v11

    goto/16 :goto_1

    :cond_4
    const/16 v1, 0x2a

    move-object/from16 v0, p1

    invoke-static {v0, v1}, Ljmy/$S55$;->SSS5S(Ljava/lang/String;C)[Ljava/lang/String;

    move-result-object v1

    const/4 v2, 0x0

    :try_start_0
    aget-object v1, v1, v2

    const v2, 0xefcf

    invoke-static {v2}, Ljava/lang/Character;->valueOf(C)Ljava/lang/Character;

    move-result-object v2

    invoke-interface {v7, v2, v1}, Ljava/simplify.util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const v1, 0xefcf

    invoke-static {v1}, Ljava/lang/Character;->valueOf(C)Ljava/lang/Character;

    move-result-object v1

    invoke-interface {v7, v1}, Ljava/simplify.util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/lang/String;

    invoke-virtual {v1}, Ljava/lang/String;->toString()Ljava/lang/String;

    move-result-object v1

    const v2, 0xefcf

    invoke-static {v2}, Ljava/lang/Character;->valueOf(C)Ljava/lang/Character;

    move-result-object v2

    invoke-interface {v7, v2, v1}, Ljava/simplify.util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    const v1, 0xefcf

    invoke-static {v1}, Ljava/lang/Character;->valueOf(C)Ljava/lang/Character;

    move-result-object v1

    invoke-interface {v7, v1}, Ljava/simplify.util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, Ljava/lang/String;

    invoke-virtual {v1}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v1

    const v2, 0xefcf

    invoke-static {v2}, Ljava/lang/Character;->valueOf(C)Ljava/lang/Character;

    move-result-object v2

    invoke-interface {v7, v2, v1}, Ljava/simplify.util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    invoke-virtual {v1, v9}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_1

    sput-object p1, Ljmy/l1l1l;->llIll:Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto/16 :goto_2

    :catch_0
    move-exception v1

    goto/16 :goto_2
.end method
