.class public final Llibcore/util/ZoneInfo;
.super Ljava/util/TimeZone;
.source "ZoneInfo.java"


# static fields
.field private static final LEAP:[I

.field private static final MILLISECONDS_PER_400_YEARS:J = 0xb7af85d9c00L

.field private static final MILLISECONDS_PER_DAY:J = 0x5265c00L

.field private static final NORMAL:[I

.field private static final UNIX_OFFSET:J = 0x388a6f046000L


# instance fields
.field private final mEarliestRawOffset:I

.field private final mIsDsts:[B

.field private final mOffsets:[I

.field private mRawOffset:I

.field private final mTransitions:[I

.field private final mTypes:[B

.field private final mUseDst:Z


# direct methods
.method static constructor <clinit>()V
    .registers 2

    .prologue
    const/16 v1, 0xc

    .line 37
    new-array v0, v1, [I

    fill-array-data v0, :array_12

    sput-object v0, Llibcore/util/ZoneInfo;->NORMAL:[I

    .line 41
    new-array v0, v1, [I

    fill-array-data v0, :array_2e

    sput-object v0, Llibcore/util/ZoneInfo;->LEAP:[I

    return-void

    .line 37
    nop

    :array_12
    .array-data 4
        0x0
        0x1f
        0x3b
        0x5a
        0x78
        0x97
        0xb5
        0xd4
        0xf3
        0x111
        0x130
        0x14e
    .end array-data

    .line 41
    :array_2e
    .array-data 4
        0x0
        0x1f
        0x3c
        0x5b
        0x79
        0x98
        0xb6
        0xd5
        0xf4
        0x112
        0x131
        0x14f
    .end array-data
.end method

.method constructor <init>(Ljava/lang/String;[I[B[I[B)V
    .registers 20
    .param p1, "name"    # Ljava/lang/String;
    .param p2, "transitions"    # [I
    .param p3, "type"    # [B
    .param p4, "gmtOffsets"    # [I
    .param p5, "isDsts"    # [B

    .prologue
    .line 55
    invoke-direct {p0}, Ljava/util/TimeZone;-><init>()V

    .line 56
    move-object/from16 v0, p2

    iput-object v0, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    .line 57
    move-object/from16 v0, p3

    iput-object v0, p0, Llibcore/util/ZoneInfo;->mTypes:[B

    .line 58
    move-object/from16 v0, p5

    iput-object v0, p0, Llibcore/util/ZoneInfo;->mIsDsts:[B

    .line 59
    invoke-virtual {p0, p1}, Llibcore/util/ZoneInfo;->setID(Ljava/lang/String;)V

    .line 63
    iget-object v10, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    array-length v10, v10

    add-int/lit8 v6, v10, -0x1

    .local v6, "lastStd":I
    :goto_17
    if-ltz v6, :cond_25

    .line 64
    iget-object v10, p0, Llibcore/util/ZoneInfo;->mIsDsts:[B

    iget-object v11, p0, Llibcore/util/ZoneInfo;->mTypes:[B

    aget-byte v11, v11, v6

    and-int/lit16 v11, v11, 0xff

    aget-byte v10, v10, v11

    if-nez v10, :cond_67

    .line 68
    :cond_25
    if-gez v6, :cond_28

    .line 69
    const/4 v6, 0x0

    .line 71
    :cond_28
    iget-object v10, p0, Llibcore/util/ZoneInfo;->mTypes:[B

    array-length v10, v10

    if-lt v6, v10, :cond_6a

    .line 72
    const/4 v10, 0x0

    aget v10, p4, v10

    iput v10, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    .line 79
    :goto_32
    const/4 v4, -0x1

    .line 80
    .local v4, "firstStd":I
    const/4 v5, 0x0

    .local v5, "i":I
    :goto_34
    iget-object v10, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    array-length v10, v10

    if-ge v5, v10, :cond_46

    .line 81
    iget-object v10, p0, Llibcore/util/ZoneInfo;->mIsDsts:[B

    iget-object v11, p0, Llibcore/util/ZoneInfo;->mTypes:[B

    aget-byte v11, v11, v5

    and-int/lit16 v11, v11, 0xff

    aget-byte v10, v10, v11

    if-nez v10, :cond_75

    .line 82
    move v4, v5

    .line 86
    :cond_46
    const/4 v10, -0x1

    if-eq v4, v10, :cond_78

    iget-object v10, p0, Llibcore/util/ZoneInfo;->mTypes:[B

    aget-byte v10, v10, v4

    and-int/lit16 v10, v10, 0xff

    aget v3, p4, v10

    .line 90
    .local v3, "earliestRawOffset":I
    :goto_51
    move-object/from16 v0, p4

    iput-object v0, p0, Llibcore/util/ZoneInfo;->mOffsets:[I

    .line 91
    const/4 v5, 0x0

    :goto_56
    iget-object v10, p0, Llibcore/util/ZoneInfo;->mOffsets:[I

    array-length v10, v10

    if-ge v5, v10, :cond_7b

    .line 92
    iget-object v10, p0, Llibcore/util/ZoneInfo;->mOffsets:[I

    aget v11, v10, v5

    iget v12, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    sub-int/2addr v11, v12

    aput v11, v10, v5

    .line 91
    add-int/lit8 v5, v5, 0x1

    goto :goto_56

    .line 63
    .end local v3    # "earliestRawOffset":I
    .end local v4    # "firstStd":I
    .end local v5    # "i":I
    :cond_67
    add-int/lit8 v6, v6, -0x1

    goto :goto_17

    .line 74
    :cond_6a
    iget-object v10, p0, Llibcore/util/ZoneInfo;->mTypes:[B

    aget-byte v10, v10, v6

    and-int/lit16 v10, v10, 0xff

    aget v10, p4, v10

    iput v10, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    goto :goto_32

    .line 80
    .restart local v4    # "firstStd":I
    .restart local v5    # "i":I
    :cond_75
    add-int/lit8 v5, v5, 0x1

    goto :goto_34

    .line 86
    :cond_78
    iget v3, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    goto :goto_51

    .line 103
    .restart local v3    # "earliestRawOffset":I
    :cond_7b
    const/4 v9, 0x0

    .line 104
    .local v9, "usesDst":Z
    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    move-result-wide v10

    const-wide/16 v12, 0x3e8

    div-long v1, v10, v12

    .line 105
    .local v1, "currentUnixTime":J
    iget-object v10, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    array-length v10, v10

    if-lez v10, :cond_9c

    .line 107
    iget-object v10, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    iget-object v11, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    array-length v11, v11

    add-int/lit8 v11, v11, -0x1

    aget v10, v10, v11

    int-to-long v10, v10

    const-wide/16 v12, -0x1

    and-long v7, v10, v12

    .line 108
    .local v7, "latestScheduleTime":J
    cmp-long v10, v1, v7

    if-gez v10, :cond_9c

    .line 109
    const/4 v9, 0x1

    .line 112
    .end local v7    # "latestScheduleTime":J
    :cond_9c
    iput-boolean v9, p0, Llibcore/util/ZoneInfo;->mUseDst:Z

    .line 114
    iget v10, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    mul-int/lit16 v10, v10, 0x3e8

    iput v10, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    .line 115
    mul-int/lit16 v10, v3, 0x3e8

    iput v10, p0, Llibcore/util/ZoneInfo;->mEarliestRawOffset:I

    .line 116
    return-void
.end method

.method private static formatTime(ILjava/util/TimeZone;)Ljava/lang/String;
    .registers 9
    .param p0, "s"    # I
    .param p1, "tz"    # Ljava/util/TimeZone;

    .prologue
    .line 255
    new-instance v2, Ljava/text/SimpleDateFormat;

    const-string v3, "EEE MMM dd HH:mm:ss yyyy zzz"

    invoke-direct {v2, v3}, Ljava/text/SimpleDateFormat;-><init>(Ljava/lang/String;)V

    .line 256
    .local v2, "sdf":Ljava/text/SimpleDateFormat;
    invoke-virtual {v2, p1}, Ljava/text/SimpleDateFormat;->setTimeZone(Ljava/util/TimeZone;)V

    .line 257
    int-to-long v3, p0

    const-wide/16 v5, 0x3e8

    mul-long v0, v3, v5

    .line 258
    .local v0, "ms":J
    new-instance v3, Ljava/util/Date;

    invoke-direct {v3, v0, v1}, Ljava/util/Date;-><init>(J)V

    invoke-virtual {v2, v3}, Ljava/text/SimpleDateFormat;->format(Ljava/util/Date;)Ljava/lang/String;

    move-result-object v3

    return-object v3
.end method


# virtual methods
.method public equals(Ljava/lang/Object;)Z
    .registers 6
    .param p1, "obj"    # Ljava/lang/Object;

    .prologue
    const/4 v1, 0x0

    .line 212
    instance-of v2, p1, Llibcore/util/ZoneInfo;

    if-nez v2, :cond_6

    .line 216
    :cond_5
    :goto_5
    return v1

    :cond_6
    move-object v0, p1

    .line 215
    check-cast v0, Llibcore/util/ZoneInfo;

    .line 216
    .local v0, "other":Llibcore/util/ZoneInfo;
    invoke-virtual {p0}, Llibcore/util/ZoneInfo;->getID()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v0}, Llibcore/util/ZoneInfo;->getID()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    move-result v2

    if-eqz v2, :cond_5

    invoke-virtual {p0, v0}, Llibcore/util/ZoneInfo;->hasSameRules(Ljava/util/TimeZone;)Z

    move-result v2

    if-eqz v2, :cond_5

    const/4 v1, 0x1

    goto :goto_5
.end method

.method public getOffset(IIIIII)I
    .registers 15
    .param p1, "era"    # I
    .param p2, "year"    # I
    .param p3, "month"    # I
    .param p4, "day"    # I
    .param p5, "dayOfWeek"    # I
    .param p6, "millis"    # I

    .prologue
    .line 124
    div-int/lit16 v4, p2, 0x190

    int-to-long v4, v4

    const-wide v6, 0xb7af85d9c00L

    mul-long v0, v4, v6

    .line 125
    .local v0, "calc":J
    rem-int/lit16 p2, p2, 0x190

    .line 127
    int-to-long v4, p2

    const-wide v6, 0x757b12c00L

    mul-long/2addr v4, v6

    add-long/2addr v0, v4

    .line 128
    add-int/lit8 v4, p2, 0x3

    div-int/lit8 v4, v4, 0x4

    int-to-long v4, v4

    const-wide/32 v6, 0x5265c00

    mul-long/2addr v4, v6

    add-long/2addr v0, v4

    .line 130
    if-lez p2, :cond_2a

    .line 131
    add-int/lit8 v4, p2, -0x1

    div-int/lit8 v4, v4, 0x64

    int-to-long v4, v4

    const-wide/32 v6, 0x5265c00

    mul-long/2addr v4, v6

    sub-long/2addr v0, v4

    .line 134
    :cond_2a
    if-eqz p2, :cond_34

    rem-int/lit8 v4, p2, 0x4

    if-nez v4, :cond_5a

    rem-int/lit8 v4, p2, 0x64

    if-eqz v4, :cond_5a

    :cond_34
    const/4 v2, 0x1

    .line 135
    .local v2, "isLeap":Z
    :goto_35
    if-eqz v2, :cond_5c

    sget-object v3, Llibcore/util/ZoneInfo;->LEAP:[I

    .line 137
    .local v3, "mlen":[I
    :goto_39
    aget v4, v3, p3

    int-to-long v4, v4

    const-wide/32 v6, 0x5265c00

    mul-long/2addr v4, v6

    add-long/2addr v0, v4

    .line 138
    add-int/lit8 v4, p4, -0x1

    int-to-long v4, v4

    const-wide/32 v6, 0x5265c00

    mul-long/2addr v4, v6

    add-long/2addr v0, v4

    .line 139
    int-to-long v4, p6

    add-long/2addr v0, v4

    .line 141
    iget v4, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    int-to-long v4, v4

    sub-long/2addr v0, v4

    .line 142
    const-wide v4, 0x388a6f046000L

    sub-long/2addr v0, v4

    .line 144
    invoke-virtual {p0, v0, v1}, Llibcore/util/ZoneInfo;->getOffset(J)I

    move-result v4

    return v4

    .line 134
    .end local v2    # "isLeap":Z
    .end local v3    # "mlen":[I
    :cond_5a
    const/4 v2, 0x0

    goto :goto_35

    .line 135
    .restart local v2    # "isLeap":Z
    :cond_5c
    sget-object v3, Llibcore/util/ZoneInfo;->NORMAL:[I

    goto :goto_39
.end method

.method public getOffset(J)I
    .registers 8
    .param p1, "when"    # J

    .prologue
    .line 149
    const-wide/16 v2, 0x3e8

    div-long v2, p1, v2

    long-to-int v1, v2

    .line 150
    .local v1, "unix":I
    iget-object v2, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    invoke-static {v2, v1}, Ljava/util/Arrays;->binarySearch([II)I

    move-result v0

    .line 151
    .local v0, "transition":I
    if-gez v0, :cond_16

    .line 152
    xor-int/lit8 v2, v0, -0x1

    add-int/lit8 v0, v2, -0x1

    .line 153
    if-gez v0, :cond_16

    .line 157
    iget v2, p0, Llibcore/util/ZoneInfo;->mEarliestRawOffset:I

    .line 160
    :goto_15
    return v2

    :cond_16
    iget v2, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    iget-object v3, p0, Llibcore/util/ZoneInfo;->mOffsets:[I

    iget-object v4, p0, Llibcore/util/ZoneInfo;->mTypes:[B

    aget-byte v4, v4, v0

    and-int/lit16 v4, v4, 0xff

    aget v3, v3, v4

    mul-int/lit16 v3, v3, 0x3e8

    add-int/2addr v2, v3

    goto :goto_15
.end method

.method public getRawOffset()I
    .registers 2

    .prologue
    .line 181
    iget v0, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    return v0
.end method

.method public hasSameRules(Ljava/util/TimeZone;)Z
    .registers 7
    .param p1, "timeZone"    # Ljava/util/TimeZone;

    .prologue
    const/4 v1, 0x1

    const/4 v2, 0x0

    .line 193
    instance-of v3, p1, Llibcore/util/ZoneInfo;

    if-nez v3, :cond_7

    .line 203
    :cond_6
    :goto_6
    return v2

    :cond_7
    move-object v0, p1

    .line 196
    check-cast v0, Llibcore/util/ZoneInfo;

    .line 197
    .local v0, "other":Llibcore/util/ZoneInfo;
    iget-boolean v3, p0, Llibcore/util/ZoneInfo;->mUseDst:Z

    iget-boolean v4, v0, Llibcore/util/ZoneInfo;->mUseDst:Z

    if-ne v3, v4, :cond_6

    .line 200
    iget-boolean v3, p0, Llibcore/util/ZoneInfo;->mUseDst:Z

    if-nez v3, :cond_1e

    .line 201
    iget v3, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    iget v4, v0, Llibcore/util/ZoneInfo;->mRawOffset:I

    if-ne v3, v4, :cond_1c

    :goto_1a
    move v2, v1

    goto :goto_6

    :cond_1c
    move v1, v2

    goto :goto_1a

    .line 203
    :cond_1e
    iget v3, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    iget v4, v0, Llibcore/util/ZoneInfo;->mRawOffset:I

    if-ne v3, v4, :cond_4e

    iget-object v3, p0, Llibcore/util/ZoneInfo;->mOffsets:[I

    iget-object v4, v0, Llibcore/util/ZoneInfo;->mOffsets:[I

    invoke-static {v3, v4}, Ljava/util/Arrays;->equals([I[I)Z

    move-result v3

    if-eqz v3, :cond_4e

    iget-object v3, p0, Llibcore/util/ZoneInfo;->mIsDsts:[B

    iget-object v4, v0, Llibcore/util/ZoneInfo;->mIsDsts:[B

    invoke-static {v3, v4}, Ljava/util/Arrays;->equals([B[B)Z

    move-result v3

    if-eqz v3, :cond_4e

    iget-object v3, p0, Llibcore/util/ZoneInfo;->mTypes:[B

    iget-object v4, v0, Llibcore/util/ZoneInfo;->mTypes:[B

    invoke-static {v3, v4}, Ljava/util/Arrays;->equals([B[B)Z

    move-result v3

    if-eqz v3, :cond_4e

    iget-object v3, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    iget-object v4, v0, Llibcore/util/ZoneInfo;->mTransitions:[I

    invoke-static {v3, v4}, Ljava/util/Arrays;->equals([I[I)Z

    move-result v3

    if-eqz v3, :cond_4e

    :goto_4c
    move v2, v1

    goto :goto_6

    :cond_4e
    move v1, v2

    goto :goto_4c
.end method

.method public hashCode()I
    .registers 5

    .prologue
    .line 221
    const/16 v0, 0x1f

    .line 222
    .local v0, "prime":I
    const/4 v1, 0x1

    .line 223
    .local v1, "result":I
    invoke-virtual {p0}, Llibcore/util/ZoneInfo;->getID()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/String;->hashCode()I

    move-result v2

    add-int/lit8 v1, v2, 0x1f

    .line 224
    mul-int/lit8 v2, v1, 0x1f

    iget-object v3, p0, Llibcore/util/ZoneInfo;->mOffsets:[I

    invoke-static {v3}, Ljava/util/Arrays;->hashCode([I)I

    move-result v3

    add-int v1, v2, v3

    .line 225
    mul-int/lit8 v2, v1, 0x1f

    iget-object v3, p0, Llibcore/util/ZoneInfo;->mIsDsts:[B

    invoke-static {v3}, Ljava/util/Arrays;->hashCode([B)I

    move-result v3

    add-int v1, v2, v3

    .line 226
    mul-int/lit8 v2, v1, 0x1f

    iget v3, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    add-int v1, v2, v3

    .line 227
    mul-int/lit8 v2, v1, 0x1f

    iget-object v3, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    invoke-static {v3}, Ljava/util/Arrays;->hashCode([I)I

    move-result v3

    add-int v1, v2, v3

    .line 228
    mul-int/lit8 v2, v1, 0x1f

    iget-object v3, p0, Llibcore/util/ZoneInfo;->mTypes:[B

    invoke-static {v3}, Ljava/util/Arrays;->hashCode([B)I

    move-result v3

    add-int v1, v2, v3

    .line 229
    mul-int/lit8 v3, v1, 0x1f

    iget-boolean v2, p0, Llibcore/util/ZoneInfo;->mUseDst:Z

    if-eqz v2, :cond_46

    const/16 v2, 0x4cf

    :goto_43
    add-int v1, v3, v2

    .line 230
    return v1

    .line 229
    :cond_46
    const/16 v2, 0x4d5

    goto :goto_43
.end method

.method public inDaylightTime(Ljava/util/Date;)Z
    .registers 10
    .param p1, "time"    # Ljava/util/Date;

    .prologue
    const/4 v4, 0x1

    const/4 v5, 0x0

    .line 164
    invoke-virtual {p1}, Ljava/util/Date;->getTime()J

    move-result-wide v2

    .line 165
    .local v2, "when":J
    const-wide/16 v6, 0x3e8

    div-long v6, v2, v6

    long-to-int v1, v6

    .line 166
    .local v1, "unix":I
    iget-object v6, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    invoke-static {v6, v1}, Ljava/util/Arrays;->binarySearch([II)I

    move-result v0

    .line 167
    .local v0, "transition":I
    if-gez v0, :cond_1a

    .line 168
    xor-int/lit8 v6, v0, -0x1

    add-int/lit8 v0, v6, -0x1

    .line 169
    if-gez v0, :cond_1a

    .line 177
    :goto_19
    return v5

    :cond_1a
    iget-object v6, p0, Llibcore/util/ZoneInfo;->mIsDsts:[B

    iget-object v7, p0, Llibcore/util/ZoneInfo;->mTypes:[B

    aget-byte v7, v7, v0

    and-int/lit16 v7, v7, 0xff

    aget-byte v6, v6, v7

    if-ne v6, v4, :cond_28

    :goto_26
    move v5, v4

    goto :goto_19

    :cond_28
    move v4, v5

    goto :goto_26
.end method

.method public setRawOffset(I)V
    .registers 2
    .param p1, "off"    # I

    .prologue
    .line 185
    iput p1, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    .line 186
    return-void
.end method

.method public toString()Ljava/lang/String;
    .registers 13

    .prologue
    .line 235
    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    .line 237
    .local v5, "sb":Ljava/lang/StringBuilder;
    new-instance v8, Ljava/lang/StringBuilder;

    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v9

    invoke-virtual {v9}, Ljava/lang/Class;->getName()Ljava/lang/String;

    move-result-object v9

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v8

    const-string v9, "["

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v8

    invoke-virtual {p0}, Llibcore/util/ZoneInfo;->getID()Ljava/lang/String;

    move-result-object v9

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v8

    const-string v9, ",mRawOffset="

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v8

    iget v9, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v8

    const-string v9, ",mUseDst="

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v8

    iget-boolean v9, p0, Llibcore/util/ZoneInfo;->mUseDst:Z

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Z)Ljava/lang/StringBuilder;

    move-result-object v8

    const-string v9, "]"

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v8

    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v8

    invoke-virtual {v5, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 240
    const-string v8, "\n"

    invoke-virtual {v5, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 241
    new-instance v0, Ljava/util/Formatter;

    invoke-direct {v0, v5}, Ljava/util/Formatter;-><init>(Ljava/lang/Appendable;)V

    .line 242
    .local v0, "f":Ljava/util/Formatter;
    const/4 v2, 0x0

    .local v2, "i":I
    :goto_54
    iget-object v8, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    array-length v8, v8

    if-ge v2, v8, :cond_bb

    .line 243
    iget-object v8, p0, Llibcore/util/ZoneInfo;->mTypes:[B

    aget-byte v8, v8, v2

    and-int/lit16 v6, v8, 0xff

    .line 244
    .local v6, "type":I
    iget-object v8, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    aget v8, v8, v2

    const-string v9, "UTC"

    invoke-static {v9}, Ljava/util/TimeZone;->getTimeZone(Ljava/lang/String;)Ljava/util/TimeZone;

    move-result-object v9

    invoke-static {v8, v9}, Llibcore/util/ZoneInfo;->formatTime(ILjava/util/TimeZone;)Ljava/lang/String;

    move-result-object v7

    .line 245
    .local v7, "utcTime":Ljava/lang/String;
    iget-object v8, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    aget v8, v8, v2

    invoke-static {v8, p0}, Llibcore/util/ZoneInfo;->formatTime(ILjava/util/TimeZone;)Ljava/lang/String;

    move-result-object v3

    .line 246
    .local v3, "localTime":Ljava/lang/String;
    iget-object v8, p0, Llibcore/util/ZoneInfo;->mOffsets:[I

    aget v4, v8, v6

    .line 247
    .local v4, "offset":I
    iget v8, p0, Llibcore/util/ZoneInfo;->mRawOffset:I

    div-int/lit16 v8, v8, 0x3e8

    add-int v1, v8, v4

    .line 248
    .local v1, "gmtOffset":I
    const-string v8, "%4d : time=%11d %s = %s isDst=%d offset=%5d gmtOffset=%d\n"

    const/4 v9, 0x7

    new-array v9, v9, [Ljava/lang/Object;

    const/4 v10, 0x0

    invoke-static {v2}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v11

    aput-object v11, v9, v10

    const/4 v10, 0x1

    iget-object v11, p0, Llibcore/util/ZoneInfo;->mTransitions:[I

    aget v11, v11, v2

    invoke-static {v11}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v11

    aput-object v11, v9, v10

    const/4 v10, 0x2

    aput-object v7, v9, v10

    const/4 v10, 0x3

    aput-object v3, v9, v10

    const/4 v10, 0x4

    iget-object v11, p0, Llibcore/util/ZoneInfo;->mIsDsts:[B

    aget-byte v11, v11, v6

    invoke-static {v11}, Ljava/lang/Byte;->valueOf(B)Ljava/lang/Byte;

    move-result-object v11

    aput-object v11, v9, v10

    const/4 v10, 0x5

    invoke-static {v4}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v11

    aput-object v11, v9, v10

    const/4 v10, 0x6

    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    move-result-object v11

    aput-object v11, v9, v10

    invoke-virtual {v0, v8, v9}, Ljava/util/Formatter;->format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/util/Formatter;

    .line 242
    add-int/lit8 v2, v2, 0x1

    goto :goto_54

    .line 251
    .end local v1    # "gmtOffset":I
    .end local v3    # "localTime":Ljava/lang/String;
    .end local v4    # "offset":I
    .end local v6    # "type":I
    .end local v7    # "utcTime":Ljava/lang/String;
    :cond_bb
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v8

    return-object v8
.end method

.method public useDaylightTime()Z
    .registers 2

    .prologue
    .line 189
    iget-boolean v0, p0, Llibcore/util/ZoneInfo;->mUseDst:Z

    return v0
.end method
