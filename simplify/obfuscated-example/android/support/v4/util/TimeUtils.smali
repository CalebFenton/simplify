.class public Landroid/support/v4/util/TimeUtils;
.super Ljava/lang/Object;
.source "TimeUtils.java"


# static fields
.field public static final HUNDRED_DAY_FIELD_LEN:I = 0x13

.field private static final SECONDS_PER_DAY:I = 0x15180

.field private static final SECONDS_PER_HOUR:I = 0xe10

.field private static final SECONDS_PER_MINUTE:I = 0x3c

.field private static sFormatStr:[C

.field private static final sFormatSync:Ljava/lang/Object;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 35
    new-instance v0, Ljava/lang/Object;

    #@2
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    #@5
    sput-object v0, Landroid/support/v4/util/TimeUtils;->sFormatSync:Ljava/lang/Object;

    #@7
    .line 36
    const/16 v0, 0x18

    #@9
    new-array v0, v0, [C

    #@b
    sput-object v0, Landroid/support/v4/util/TimeUtils;->sFormatStr:[C

    #@d
    return-void
.end method

.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 27
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method private static accumField(IIZI)I
    .locals 1
    .param p0, "amt"    # I
    .param p1, "suffix"    # I
    .param p2, "always"    # Z
    .param p3, "zeropad"    # I

    #@0
    .prologue
    .line 39
    const/16 v0, 0x63

    #@2
    if-gt p0, v0, :cond_0

    #@4
    if-eqz p2, :cond_1

    #@6
    const/4 v0, 0x3

    #@7
    if-lt p3, v0, :cond_1

    #@9
    .line 40
    :cond_0
    add-int/lit8 v0, p1, 0x3

    #@b
    .line 48
    :goto_0
    return v0

    #@c
    .line 42
    :cond_1
    const/16 v0, 0x9

    #@e
    if-gt p0, v0, :cond_2

    #@10
    if-eqz p2, :cond_3

    #@12
    const/4 v0, 0x2

    #@13
    if-lt p3, v0, :cond_3

    #@15
    .line 43
    :cond_2
    add-int/lit8 v0, p1, 0x2

    #@17
    goto :goto_0

    #@18
    .line 45
    :cond_3
    if-nez p2, :cond_4

    #@1a
    if-lez p0, :cond_5

    #@1c
    .line 46
    :cond_4
    add-int/lit8 v0, p1, 0x1

    #@1e
    goto :goto_0

    #@1f
    .line 48
    :cond_5
    const/4 v0, 0x0

    #@20
    goto :goto_0
.end method

.method public static formatDuration(JJLjava/io/PrintWriter;)V
    .locals 4
    .param p0, "time"    # J
    .param p2, "now"    # J
    .param p4, "pw"    # Ljava/io/PrintWriter;

    #@0
    .prologue
    .line 169
    const-wide/16 v0, 0x0

    #@2
    cmp-long v0, p0, v0

    #@4
    if-nez v0, :cond_0

    #@6
    .line 170
    const-string v0, "--"

    #@8
    invoke-virtual {p4, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@b
    .line 174
    :goto_0
    return-void

    #@c
    .line 173
    :cond_0
    sub-long v0, p0, p2

    #@e
    const/4 v2, 0x0

    #@f
    invoke-static {v0, v1, p4, v2}, Landroid/support/v4/util/TimeUtils;->formatDuration(JLjava/io/PrintWriter;I)V

    #@12
    goto :goto_0
.end method

.method public static formatDuration(JLjava/io/PrintWriter;)V
    .locals 2
    .param p0, "duration"    # J
    .param p2, "pw"    # Ljava/io/PrintWriter;

    #@0
    .prologue
    .line 164
    const/4 v0, 0x0

    #@1
    invoke-static {p0, p1, p2, v0}, Landroid/support/v4/util/TimeUtils;->formatDuration(JLjava/io/PrintWriter;I)V

    #@4
    .line 165
    return-void
.end method

.method public static formatDuration(JLjava/io/PrintWriter;I)V
    .locals 6
    .param p0, "duration"    # J
    .param p2, "pw"    # Ljava/io/PrintWriter;
    .param p3, "fieldLen"    # I

    #@0
    .prologue
    .line 156
    sget-object v2, Landroid/support/v4/util/TimeUtils;->sFormatSync:Ljava/lang/Object;

    #@2
    monitor-enter v2

    #@3
    .line 157
    :try_start_0
    invoke-static {p0, p1, p3}, Landroid/support/v4/util/TimeUtils;->formatDurationLocked(JI)I

    #@6
    move-result v0

    #@7
    .line 158
    .local v0, "len":I
    new-instance v1, Ljava/lang/String;

    #@9
    sget-object v3, Landroid/support/v4/util/TimeUtils;->sFormatStr:[C

    #@b
    const/4 v4, 0x0

    #@c
    invoke-direct {v1, v3, v4, v0}, Ljava/lang/String;-><init>([CII)V

    #@f
    invoke-virtual {p2, v1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@12
    .line 159
    monitor-exit v2

    #@13
    .line 160
    return-void

    #@14
    .line 159
    .end local v0    # "len":I
    :catchall_0
    move-exception v1

    #@15
    monitor-exit v2
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@16
    throw v1
.end method

.method public static formatDuration(JLjava/lang/StringBuilder;)V
    .locals 4
    .param p0, "duration"    # J
    .param p2, "builder"    # Ljava/lang/StringBuilder;

    #@0
    .prologue
    .line 148
    sget-object v2, Landroid/support/v4/util/TimeUtils;->sFormatSync:Ljava/lang/Object;

    #@2
    monitor-enter v2

    #@3
    .line 149
    const/4 v1, 0x0

    #@4
    :try_start_0
    invoke-static {p0, p1, v1}, Landroid/support/v4/util/TimeUtils;->formatDurationLocked(JI)I

    #@7
    move-result v0

    #@8
    .line 150
    .local v0, "len":I
    sget-object v1, Landroid/support/v4/util/TimeUtils;->sFormatStr:[C

    #@a
    const/4 v3, 0x0

    #@b
    invoke-virtual {p2, v1, v3, v0}, Ljava/lang/StringBuilder;->append([CII)Ljava/lang/StringBuilder;

    #@e
    .line 151
    monitor-exit v2

    #@f
    .line 152
    return-void

    #@10
    .line 151
    .end local v0    # "len":I
    :catchall_0
    move-exception v1

    #@11
    monitor-exit v2
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@12
    throw v1
.end method

.method private static formatDurationLocked(JI)I
    .locals 20
    .param p0, "duration"    # J
    .param p2, "fieldLen"    # I

    #@0
    .prologue
    .line 76
    sget-object v4, Landroid/support/v4/util/TimeUtils;->sFormatStr:[C

    #@2
    array-length v4, v4

    #@3
    move/from16 v0, p2

    #@5
    if-ge v4, v0, :cond_0

    #@7
    .line 77
    move/from16 v0, p2

    #@9
    new-array v4, v0, [C

    #@b
    sput-object v4, Landroid/support/v4/util/TimeUtils;->sFormatStr:[C

    #@d
    .line 80
    :cond_0
    sget-object v2, Landroid/support/v4/util/TimeUtils;->sFormatStr:[C

    #@f
    .line 82
    .local v2, "formatStr":[C
    const-wide/16 v6, 0x0

    #@11
    cmp-long v4, p0, v6

    #@13
    if-nez v4, :cond_2

    #@15
    .line 83
    const/4 v5, 0x0

    #@16
    .line 84
    .local v5, "pos":I
    add-int/lit8 p2, p2, -0x1

    #@18
    .line 85
    :goto_0
    move/from16 v0, p2

    #@1a
    if-ge v5, v0, :cond_1

    #@1c
    .line 86
    const/16 v4, 0x20

    #@1e
    aput-char v4, v2, v5

    #@20
    goto :goto_0

    #@21
    .line 88
    :cond_1
    const/16 v4, 0x30

    #@23
    aput-char v4, v2, v5

    #@25
    .line 89
    const/4 v4, 0x1

    #@26
    .line 143
    :goto_1
    return v4

    #@27
    .line 93
    .end local v5    # "pos":I
    :cond_2
    const-wide/16 v6, 0x0

    #@29
    cmp-long v4, p0, v6

    #@2b
    if-lez v4, :cond_6

    #@2d
    .line 94
    const/16 v16, 0x2b

    #@2f
    .line 100
    .local v16, "prefix":C
    :goto_2
    const-wide/16 v6, 0x3e8

    #@31
    rem-long v6, p0, v6

    #@33
    long-to-int v13, v6

    #@34
    .line 101
    .local v13, "millis":I
    const-wide/16 v6, 0x3e8

    #@36
    div-long v6, p0, v6

    #@38
    long-to-double v6, v6

    #@39
    invoke-static {v6, v7}, Ljava/lang/Math;->floor(D)D

    #@3c
    move-result-wide v6

    #@3d
    double-to-int v0, v6

    #@3e
    move/from16 v17, v0

    #@40
    .line 102
    .local v17, "seconds":I
    const/4 v3, 0x0

    #@41
    .local v3, "days":I
    const/4 v12, 0x0

    #@42
    .local v12, "hours":I
    const/4 v14, 0x0

    #@43
    .line 104
    .local v14, "minutes":I
    const v4, 0x15180

    #@46
    move/from16 v0, v17

    #@48
    if-le v0, v4, :cond_3

    #@4a
    .line 105
    const v4, 0x15180

    #@4d
    div-int v3, v17, v4

    #@4f
    .line 106
    const v4, 0x15180

    #@52
    mul-int/2addr v4, v3

    #@53
    sub-int v17, v17, v4

    #@55
    .line 108
    :cond_3
    const/16 v4, 0xe10

    #@57
    move/from16 v0, v17

    #@59
    if-le v0, v4, :cond_4

    #@5b
    .line 109
    move/from16 v0, v17

    #@5d
    div-int/lit16 v12, v0, 0xe10

    #@5f
    .line 110
    mul-int/lit16 v4, v12, 0xe10

    #@61
    sub-int v17, v17, v4

    #@63
    .line 112
    :cond_4
    const/16 v4, 0x3c

    #@65
    move/from16 v0, v17

    #@67
    if-le v0, v4, :cond_5

    #@69
    .line 113
    div-int/lit8 v14, v17, 0x3c

    #@6b
    .line 114
    mul-int/lit8 v4, v14, 0x3c

    #@6d
    sub-int v17, v17, v4

    #@6f
    .line 117
    :cond_5
    const/4 v5, 0x0

    #@70
    .line 119
    .restart local v5    # "pos":I
    if-eqz p2, :cond_b

    #@72
    .line 120
    const/4 v4, 0x1

    #@73
    const/4 v6, 0x0

    #@74
    const/4 v7, 0x0

    #@75
    invoke-static {v3, v4, v6, v7}, Landroid/support/v4/util/TimeUtils;->accumField(IIZI)I

    #@78
    move-result v15

    #@79
    .line 121
    .local v15, "myLen":I
    const/4 v6, 0x1

    #@7a
    if-lez v15, :cond_7

    #@7c
    const/4 v4, 0x1

    #@7d
    :goto_3
    const/4 v7, 0x2

    #@7e
    invoke-static {v12, v6, v4, v7}, Landroid/support/v4/util/TimeUtils;->accumField(IIZI)I

    #@81
    move-result v4

    #@82
    add-int/2addr v15, v4

    #@83
    .line 122
    const/4 v6, 0x1

    #@84
    if-lez v15, :cond_8

    #@86
    const/4 v4, 0x1

    #@87
    :goto_4
    const/4 v7, 0x2

    #@88
    invoke-static {v14, v6, v4, v7}, Landroid/support/v4/util/TimeUtils;->accumField(IIZI)I

    #@8b
    move-result v4

    #@8c
    add-int/2addr v15, v4

    #@8d
    .line 123
    const/4 v6, 0x1

    #@8e
    if-lez v15, :cond_9

    #@90
    const/4 v4, 0x1

    #@91
    :goto_5
    const/4 v7, 0x2

    #@92
    move/from16 v0, v17

    #@94
    invoke-static {v0, v6, v4, v7}, Landroid/support/v4/util/TimeUtils;->accumField(IIZI)I

    #@97
    move-result v4

    #@98
    add-int/2addr v15, v4

    #@99
    .line 124
    const/4 v6, 0x2

    #@9a
    const/4 v7, 0x1

    #@9b
    if-lez v15, :cond_a

    #@9d
    const/4 v4, 0x3

    #@9e
    :goto_6
    invoke-static {v13, v6, v7, v4}, Landroid/support/v4/util/TimeUtils;->accumField(IIZI)I

    #@a1
    move-result v4

    #@a2
    add-int/lit8 v4, v4, 0x1

    #@a4
    add-int/2addr v15, v4

    #@a5
    .line 125
    :goto_7
    move/from16 v0, p2

    #@a7
    if-ge v15, v0, :cond_b

    #@a9
    .line 126
    const/16 v4, 0x20

    #@ab
    aput-char v4, v2, v5

    #@ad
    .line 127
    add-int/lit8 v5, v5, 0x1

    #@af
    .line 128
    add-int/lit8 v15, v15, 0x1

    #@b1
    goto :goto_7

    #@b2
    .line 96
    .end local v3    # "days":I
    .end local v5    # "pos":I
    .end local v12    # "hours":I
    .end local v13    # "millis":I
    .end local v14    # "minutes":I
    .end local v15    # "myLen":I
    .end local v16    # "prefix":C
    .end local v17    # "seconds":I
    :cond_6
    const/16 v16, 0x2d

    #@b4
    .line 97
    .restart local v16    # "prefix":C
    move-wide/from16 v0, p0

    #@b6
    neg-long v0, v0

    #@b7
    move-wide/from16 p0, v0

    #@b9
    goto/16 :goto_2

    #@bb
    .line 121
    .restart local v3    # "days":I
    .restart local v5    # "pos":I
    .restart local v12    # "hours":I
    .restart local v13    # "millis":I
    .restart local v14    # "minutes":I
    .restart local v15    # "myLen":I
    .restart local v17    # "seconds":I
    :cond_7
    const/4 v4, 0x0

    #@bc
    goto :goto_3

    #@bd
    .line 122
    :cond_8
    const/4 v4, 0x0

    #@be
    goto :goto_4

    #@bf
    .line 123
    :cond_9
    const/4 v4, 0x0

    #@c0
    goto :goto_5

    #@c1
    .line 124
    :cond_a
    const/4 v4, 0x0

    #@c2
    goto :goto_6

    #@c3
    .line 132
    .end local v15    # "myLen":I
    :cond_b
    aput-char v16, v2, v5

    #@c5
    .line 133
    add-int/lit8 v5, v5, 0x1

    #@c7
    .line 135
    move/from16 v18, v5

    #@c9
    .line 136
    .local v18, "start":I
    if-eqz p2, :cond_c

    #@cb
    const/16 v19, 0x1

    #@cd
    .line 137
    .local v19, "zeropad":Z
    :goto_8
    const/16 v4, 0x64

    #@cf
    const/4 v6, 0x0

    #@d0
    const/4 v7, 0x0

    #@d1
    invoke-static/range {v2 .. v7}, Landroid/support/v4/util/TimeUtils;->printField([CICIZI)I

    #@d4
    move-result v5

    #@d5
    .line 138
    const/16 v8, 0x68

    #@d7
    move/from16 v0, v18

    #@d9
    if-eq v5, v0, :cond_d

    #@db
    const/4 v10, 0x1

    #@dc
    :goto_9
    if-eqz v19, :cond_e

    #@de
    const/4 v11, 0x2

    #@df
    :goto_a
    move-object v6, v2

    #@e0
    move v7, v12

    #@e1
    move v9, v5

    #@e2
    invoke-static/range {v6 .. v11}, Landroid/support/v4/util/TimeUtils;->printField([CICIZI)I

    #@e5
    move-result v5

    #@e6
    .line 139
    const/16 v8, 0x6d

    #@e8
    move/from16 v0, v18

    #@ea
    if-eq v5, v0, :cond_f

    #@ec
    const/4 v10, 0x1

    #@ed
    :goto_b
    if-eqz v19, :cond_10

    #@ef
    const/4 v11, 0x2

    #@f0
    :goto_c
    move-object v6, v2

    #@f1
    move v7, v14

    #@f2
    move v9, v5

    #@f3
    invoke-static/range {v6 .. v11}, Landroid/support/v4/util/TimeUtils;->printField([CICIZI)I

    #@f6
    move-result v5

    #@f7
    .line 140
    const/16 v8, 0x73

    #@f9
    move/from16 v0, v18

    #@fb
    if-eq v5, v0, :cond_11

    #@fd
    const/4 v10, 0x1

    #@fe
    :goto_d
    if-eqz v19, :cond_12

    #@100
    const/4 v11, 0x2

    #@101
    :goto_e
    move-object v6, v2

    #@102
    move/from16 v7, v17

    #@104
    move v9, v5

    #@105
    invoke-static/range {v6 .. v11}, Landroid/support/v4/util/TimeUtils;->printField([CICIZI)I

    #@108
    move-result v5

    #@109
    .line 141
    const/16 v8, 0x6d

    #@10b
    const/4 v10, 0x1

    #@10c
    if-eqz v19, :cond_13

    #@10e
    move/from16 v0, v18

    #@110
    if-eq v5, v0, :cond_13

    #@112
    const/4 v11, 0x3

    #@113
    :goto_f
    move-object v6, v2

    #@114
    move v7, v13

    #@115
    move v9, v5

    #@116
    invoke-static/range {v6 .. v11}, Landroid/support/v4/util/TimeUtils;->printField([CICIZI)I

    #@119
    move-result v5

    #@11a
    .line 142
    const/16 v4, 0x73

    #@11c
    aput-char v4, v2, v5

    #@11e
    .line 143
    add-int/lit8 v4, v5, 0x1

    #@120
    goto/16 :goto_1

    #@122
    .line 136
    .end local v19    # "zeropad":Z
    :cond_c
    const/16 v19, 0x0

    #@124
    goto :goto_8

    #@125
    .line 138
    .restart local v19    # "zeropad":Z
    :cond_d
    const/4 v10, 0x0

    #@126
    goto :goto_9

    #@127
    :cond_e
    const/4 v11, 0x0

    #@128
    goto :goto_a

    #@129
    .line 139
    :cond_f
    const/4 v10, 0x0

    #@12a
    goto :goto_b

    #@12b
    :cond_10
    const/4 v11, 0x0

    #@12c
    goto :goto_c

    #@12d
    .line 140
    :cond_11
    const/4 v10, 0x0

    #@12e
    goto :goto_d

    #@12f
    :cond_12
    const/4 v11, 0x0

    #@130
    goto :goto_e

    #@131
    .line 141
    :cond_13
    const/4 v11, 0x0

    #@132
    goto :goto_f
.end method

.method private static printField([CICIZI)I
    .locals 3
    .param p0, "formatStr"    # [C
    .param p1, "amt"    # I
    .param p2, "suffix"    # C
    .param p3, "pos"    # I
    .param p4, "always"    # Z
    .param p5, "zeropad"    # I

    #@0
    .prologue
    .line 53
    if-nez p4, :cond_0

    #@2
    if-lez p1, :cond_7

    #@4
    .line 54
    :cond_0
    move v1, p3

    #@5
    .line 55
    .local v1, "startPos":I
    if-eqz p4, :cond_1

    #@7
    const/4 v2, 0x3

    #@8
    if-ge p5, v2, :cond_2

    #@a
    :cond_1
    const/16 v2, 0x63

    #@c
    if-le p1, v2, :cond_3

    #@e
    .line 56
    :cond_2
    div-int/lit8 v0, p1, 0x64

    #@10
    .line 57
    .local v0, "dig":I
    add-int/lit8 v2, v0, 0x30

    #@12
    int-to-char v2, v2

    #@13
    aput-char v2, p0, p3

    #@15
    .line 58
    add-int/lit8 p3, p3, 0x1

    #@17
    .line 59
    mul-int/lit8 v2, v0, 0x64

    #@19
    sub-int/2addr p1, v2

    #@1a
    .line 61
    .end local v0    # "dig":I
    :cond_3
    if-eqz p4, :cond_4

    #@1c
    const/4 v2, 0x2

    #@1d
    if-ge p5, v2, :cond_5

    #@1f
    :cond_4
    const/16 v2, 0x9

    #@21
    if-gt p1, v2, :cond_5

    #@23
    if-eq v1, p3, :cond_6

    #@25
    .line 62
    :cond_5
    div-int/lit8 v0, p1, 0xa

    #@27
    .line 63
    .restart local v0    # "dig":I
    add-int/lit8 v2, v0, 0x30

    #@29
    int-to-char v2, v2

    #@2a
    aput-char v2, p0, p3

    #@2c
    .line 64
    add-int/lit8 p3, p3, 0x1

    #@2e
    .line 65
    mul-int/lit8 v2, v0, 0xa

    #@30
    sub-int/2addr p1, v2

    #@31
    .line 67
    .end local v0    # "dig":I
    :cond_6
    add-int/lit8 v2, p1, 0x30

    #@33
    int-to-char v2, v2

    #@34
    aput-char v2, p0, p3

    #@36
    .line 68
    add-int/lit8 p3, p3, 0x1

    #@38
    .line 69
    aput-char p2, p0, p3

    #@3a
    .line 70
    add-int/lit8 p3, p3, 0x1

    #@3c
    .line 72
    .end local v1    # "startPos":I
    :cond_7
    return p3
.end method
