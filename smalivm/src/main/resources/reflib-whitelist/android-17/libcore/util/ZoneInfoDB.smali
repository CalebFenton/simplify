.class public final Llibcore/util/ZoneInfoDB;
.super Ljava/lang/Object;
.source "ZoneInfoDB.java"


# static fields
.field private static final ALL_ZONE_DATA:Llibcore/io/MemoryMappedFile;

.field private static final INDEX_FILE_NAME:Ljava/lang/String;

.field private static final LOCK:Ljava/lang/Object;

.field private static final VERSION:Ljava/lang/String;

.field private static final ZONE_DIRECTORY_NAME:Ljava/lang/String;

.field private static final ZONE_FILE_NAME:Ljava/lang/String;

.field private static byteOffsets:[I

.field private static ids:[Ljava/lang/String;

.field private static rawUtcOffsets:[I


# direct methods
.method static constructor <clinit>()V
    .registers 2

    .prologue
    .line 50
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    const-string v1, "ANDROID_ROOT"

    invoke-static {v1}, Ljava/lang/System;->getenv(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, "/usr/share/zoneinfo/"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    sput-object v0, Llibcore/util/ZoneInfoDB;->ZONE_DIRECTORY_NAME:Ljava/lang/String;

    .line 56
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    sget-object v1, Llibcore/util/ZoneInfoDB;->ZONE_DIRECTORY_NAME:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, "zoneinfo.dat"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    sput-object v0, Llibcore/util/ZoneInfoDB;->ZONE_FILE_NAME:Ljava/lang/String;

    .line 62
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    sget-object v1, Llibcore/util/ZoneInfoDB;->ZONE_DIRECTORY_NAME:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, "zoneinfo.idx"

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    sput-object v0, Llibcore/util/ZoneInfoDB;->INDEX_FILE_NAME:Ljava/lang/String;

    .line 64
    new-instance v0, Ljava/lang/Object;

    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    sput-object v0, Llibcore/util/ZoneInfoDB;->LOCK:Ljava/lang/Object;

    .line 66
    invoke-static {}, Llibcore/util/ZoneInfoDB;->readVersion()Ljava/lang/String;

    move-result-object v0

    sput-object v0, Llibcore/util/ZoneInfoDB;->VERSION:Ljava/lang/String;

    .line 75
    invoke-static {}, Llibcore/util/ZoneInfoDB;->mapData()Llibcore/io/MemoryMappedFile;

    move-result-object v0

    sput-object v0, Llibcore/util/ZoneInfoDB;->ALL_ZONE_DATA:Llibcore/io/MemoryMappedFile;

    .line 87
    invoke-static {}, Llibcore/util/ZoneInfoDB;->readIndex()V

    .line 88
    return-void
.end method

.method private constructor <init>()V
    .registers 1

    .prologue
    .line 90
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 91
    return-void
.end method

.method public static getAvailableIDs()[Ljava/lang/String;
    .registers 1

    .prologue
    .line 233
    sget-object v0, Llibcore/util/ZoneInfoDB;->ids:[Ljava/lang/String;

    invoke-virtual {v0}, [Ljava/lang/String;->clone()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, [Ljava/lang/String;

    return-object v0
.end method

.method public static getAvailableIDs(I)[Ljava/lang/String;
    .registers 5
    .param p0, "rawOffset"    # I

    .prologue
    .line 237
    new-instance v2, Ljava/util/ArrayList;

    invoke-direct {v2}, Ljava/util/ArrayList;-><init>()V

    .line 238
    .local v2, "matches":Ljava/util/List;, "Ljava/util/List<Ljava/lang/String;>;"
    const/4 v1, 0x0

    .local v1, "i":I
    sget-object v3, Llibcore/util/ZoneInfoDB;->rawUtcOffsets:[I

    array-length v0, v3

    .local v0, "end":I
    :goto_9
    if-ge v1, v0, :cond_1b

    .line 239
    sget-object v3, Llibcore/util/ZoneInfoDB;->rawUtcOffsets:[I

    aget v3, v3, v1

    if-ne v3, p0, :cond_18

    .line 240
    sget-object v3, Llibcore/util/ZoneInfoDB;->ids:[Ljava/lang/String;

    aget-object v3, v3, v1

    invoke-interface {v2, v3}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 238
    :cond_18
    add-int/lit8 v1, v1, 0x1

    goto :goto_9

    .line 243
    :cond_1b
    invoke-interface {v2}, Ljava/util/List;->size()I

    move-result v3

    new-array v3, v3, [Ljava/lang/String;

    invoke-interface {v2, v3}, Ljava/util/List;->toArray([Ljava/lang/Object;)[Ljava/lang/Object;

    move-result-object v3

    check-cast v3, [Ljava/lang/String;

    return-object v3
.end method

.method public static getSystemDefault()Ljava/util/TimeZone;
    .registers 4

    .prologue
    .line 247
    sget-object v3, Llibcore/util/ZoneInfoDB;->LOCK:Ljava/lang/Object;

    monitor-enter v3

    .line 248
    :try_start_3
    invoke-static {}, Lorg/apache/harmony/luni/internal/util/TimezoneGetter;->getInstance()Lorg/apache/harmony/luni/internal/util/TimezoneGetter;

    move-result-object v0

    .line 249
    .local v0, "tzGetter":Lorg/apache/harmony/luni/internal/util/TimezoneGetter;
    if-eqz v0, :cond_23

    invoke-virtual {v0}, Lorg/apache/harmony/luni/internal/util/TimezoneGetter;->getId()Ljava/lang/String;

    move-result-object v1

    .line 250
    .local v1, "zoneName":Ljava/lang/String;
    :goto_d
    if-eqz v1, :cond_13

    .line 251
    invoke-virtual {v1}, Ljava/lang/String;->trim()Ljava/lang/String;

    move-result-object v1

    .line 253
    :cond_13
    if-eqz v1, :cond_1b

    invoke-virtual {v1}, Ljava/lang/String;->isEmpty()Z

    move-result v2

    if-eqz v2, :cond_1d

    .line 256
    :cond_1b
    const-string v1, "localtime"

    .line 258
    :cond_1d
    invoke-static {v1}, Ljava/util/TimeZone;->getTimeZone(Ljava/lang/String;)Ljava/util/TimeZone;

    move-result-object v2

    monitor-exit v3

    return-object v2

    .line 249
    .end local v1    # "zoneName":Ljava/lang/String;
    :cond_23
    const/4 v1, 0x0

    goto :goto_d

    .line 259
    :catchall_25
    move-exception v2

    monitor-exit v3
    :try_end_27
    .catchall {:try_start_3 .. :try_end_27} :catchall_25

    throw v2
.end method

.method public static getTimeZone(Ljava/lang/String;)Ljava/util/TimeZone;
    .registers 3
    .param p0, "id"    # Ljava/lang/String;

    .prologue
    const/4 v1, 0x0

    .line 263
    if-nez p0, :cond_4

    .line 269
    :goto_3
    return-object v1

    .line 267
    :cond_4
    :try_start_4
    invoke-static {p0}, Llibcore/util/ZoneInfoDB;->makeTimeZone(Ljava/lang/String;)Ljava/util/TimeZone;
    :try_end_7
    .catch Ljava/io/IOException; {:try_start_4 .. :try_end_7} :catch_9

    move-result-object v1

    goto :goto_3

    .line 268
    :catch_9
    move-exception v0

    .line 269
    .local v0, "ignored":Ljava/io/IOException;
    goto :goto_3
.end method

.method public static getVersion()Ljava/lang/String;
    .registers 1

    .prologue
    .line 274
    sget-object v0, Llibcore/util/ZoneInfoDB;->VERSION:Ljava/lang/String;

    return-object v0
.end method

.method private static makeTimeZone(Ljava/lang/String;)Ljava/util/TimeZone;
    .registers 14
    .param p0, "id"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    const/4 v0, 0x0

    const/4 v12, 0x0

    .line 185
    sget-object v1, Llibcore/util/ZoneInfoDB;->ids:[Ljava/lang/String;

    invoke-static {v1, p0}, Ljava/util/Arrays;->binarySearch([Ljava/lang/Object;Ljava/lang/Object;)I

    move-result v8

    .line 186
    .local v8, "index":I
    if-gez v8, :cond_b

    .line 229
    :cond_a
    :goto_a
    return-object v0

    .line 190
    :cond_b
    sget-object v1, Llibcore/util/ZoneInfoDB;->ALL_ZONE_DATA:Llibcore/io/MemoryMappedFile;

    invoke-virtual {v1}, Llibcore/io/MemoryMappedFile;->bigEndianIterator()Llibcore/io/BufferIterator;

    move-result-object v6

    .line 191
    .local v6, "data":Llibcore/io/BufferIterator;
    sget-object v1, Llibcore/util/ZoneInfoDB;->byteOffsets:[I

    aget v1, v1, v8

    invoke-virtual {v6, v1}, Llibcore/io/BufferIterator;->skip(I)V

    .line 195
    invoke-virtual {v6}, Llibcore/io/BufferIterator;->readInt()I

    move-result v1

    const v11, 0x545a6966

    if-ne v1, v11, :cond_a

    .line 200
    const/16 v0, 0x1c

    invoke-virtual {v6, v0}, Llibcore/io/BufferIterator;->skip(I)V

    .line 203
    invoke-virtual {v6}, Llibcore/io/BufferIterator;->readInt()I

    move-result v9

    .line 204
    .local v9, "tzh_timecnt":I
    invoke-virtual {v6}, Llibcore/io/BufferIterator;->readInt()I

    move-result v10

    .line 206
    .local v10, "tzh_typecnt":I
    const/4 v0, 0x4

    invoke-virtual {v6, v0}, Llibcore/io/BufferIterator;->skip(I)V

    .line 208
    new-array v2, v9, [I

    .line 209
    .local v2, "transitions":[I
    array-length v0, v2

    invoke-virtual {v6, v2, v12, v0}, Llibcore/io/BufferIterator;->readIntArray([III)V

    .line 211
    new-array v3, v9, [B

    .line 212
    .local v3, "type":[B
    array-length v0, v3

    invoke-virtual {v6, v3, v12, v0}, Llibcore/io/BufferIterator;->readByteArray([BII)V

    .line 214
    new-array v4, v10, [I

    .line 215
    .local v4, "gmtOffsets":[I
    new-array v5, v10, [B

    .line 216
    .local v5, "isDsts":[B
    const/4 v7, 0x0

    .local v7, "i":I
    :goto_43
    if-ge v7, v10, :cond_58

    .line 217
    invoke-virtual {v6}, Llibcore/io/BufferIterator;->readInt()I

    move-result v0

    aput v0, v4, v7

    .line 218
    invoke-virtual {v6}, Llibcore/io/BufferIterator;->readByte()B

    move-result v0

    aput-byte v0, v5, v7

    .line 226
    const/4 v0, 0x1

    invoke-virtual {v6, v0}, Llibcore/io/BufferIterator;->skip(I)V

    .line 216
    add-int/lit8 v7, v7, 0x1

    goto :goto_43

    .line 229
    :cond_58
    new-instance v0, Llibcore/util/ZoneInfo;

    move-object v1, p0

    invoke-direct/range {v0 .. v5}, Llibcore/util/ZoneInfo;-><init>(Ljava/lang/String;[I[B[I[B)V

    goto :goto_a
.end method

.method private static mapData()Llibcore/io/MemoryMappedFile;
    .registers 2

    .prologue
    .line 107
    :try_start_0
    sget-object v1, Llibcore/util/ZoneInfoDB;->ZONE_FILE_NAME:Ljava/lang/String;

    invoke-static {v1}, Llibcore/io/MemoryMappedFile;->mmapRO(Ljava/lang/String;)Llibcore/io/MemoryMappedFile;
    :try_end_5
    .catch Llibcore/io/ErrnoException; {:try_start_0 .. :try_end_5} :catch_7

    move-result-object v1

    return-object v1

    .line 108
    :catch_7
    move-exception v0

    .line 109
    .local v0, "errnoException":Llibcore/io/ErrnoException;
    new-instance v1, Ljava/lang/AssertionError;

    invoke-direct {v1, v0}, Ljava/lang/AssertionError;-><init>(Ljava/lang/Object;)V

    throw v1
.end method

.method private static readIndex()V
    .registers 3

    .prologue
    .line 124
    const/4 v1, 0x0

    .line 126
    .local v1, "mappedFile":Llibcore/io/MemoryMappedFile;
    :try_start_1
    sget-object v2, Llibcore/util/ZoneInfoDB;->INDEX_FILE_NAME:Ljava/lang/String;

    invoke-static {v2}, Llibcore/io/MemoryMappedFile;->mmapRO(Ljava/lang/String;)Llibcore/io/MemoryMappedFile;

    move-result-object v1

    .line 127
    invoke-static {v1}, Llibcore/util/ZoneInfoDB;->readIndex(Llibcore/io/MemoryMappedFile;)V
    :try_end_a
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_a} :catch_e
    .catchall {:try_start_1 .. :try_end_a} :catchall_15

    .line 131
    invoke-static {v1}, Llibcore/io/IoUtils;->closeQuietly(Ljava/lang/AutoCloseable;)V

    .line 133
    return-void

    .line 128
    :catch_e
    move-exception v0

    .line 129
    .local v0, "ex":Ljava/lang/Exception;
    :try_start_f
    new-instance v2, Ljava/lang/AssertionError;

    invoke-direct {v2, v0}, Ljava/lang/AssertionError;-><init>(Ljava/lang/Object;)V

    throw v2
    :try_end_15
    .catchall {:try_start_f .. :try_end_15} :catchall_15

    .line 131
    .end local v0    # "ex":Ljava/lang/Exception;
    :catchall_15
    move-exception v2

    invoke-static {v1}, Llibcore/io/IoUtils;->closeQuietly(Ljava/lang/AutoCloseable;)V

    throw v2
.end method

.method private static readIndex(Llibcore/io/MemoryMappedFile;)V
    .registers 19
    .param p0, "mappedFile"    # Llibcore/io/MemoryMappedFile;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Llibcore/io/ErrnoException;,
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 136
    invoke-virtual/range {p0 .. p0}, Llibcore/io/MemoryMappedFile;->bigEndianIterator()Llibcore/io/BufferIterator;

    move-result-object v10

    .line 139
    .local v10, "it":Llibcore/io/BufferIterator;
    const/16 v2, 0x28

    .line 141
    .local v2, "SIZEOF_TZNAME":I
    const/4 v1, 0x4

    .line 143
    .local v1, "SIZEOF_TZINT":I
    const/16 v15, 0x28

    new-array v5, v15, [B

    .line 144
    .local v5, "idBytes":[B
    invoke-virtual/range {p0 .. p0}, Llibcore/io/MemoryMappedFile;->size()J

    move-result-wide v15

    long-to-int v15, v15

    div-int/lit8 v14, v15, 0x34

    .line 146
    .local v14, "numEntries":I
    mul-int/lit8 v15, v14, 0x28

    new-array v6, v15, [C

    .line 147
    .local v6, "idChars":[C
    new-array v7, v14, [I

    .line 148
    .local v7, "idEnd":[I
    const/4 v8, 0x0

    .line 150
    .local v8, "idOffset":I
    new-array v15, v14, [I

    sput-object v15, Llibcore/util/ZoneInfoDB;->byteOffsets:[I

    .line 151
    new-array v15, v14, [I

    sput-object v15, Llibcore/util/ZoneInfoDB;->rawUtcOffsets:[I

    .line 153
    const/4 v4, 0x0

    .local v4, "i":I
    :goto_22
    if-ge v4, v14, :cond_69

    .line 154
    const/4 v15, 0x0

    array-length v0, v5

    move/from16 v16, v0

    move/from16 v0, v16

    invoke-virtual {v10, v5, v15, v0}, Llibcore/io/BufferIterator;->readByteArray([BII)V

    .line 155
    sget-object v15, Llibcore/util/ZoneInfoDB;->byteOffsets:[I

    invoke-virtual {v10}, Llibcore/io/BufferIterator;->readInt()I

    move-result v16

    aput v16, v15, v4

    .line 156
    invoke-virtual {v10}, Llibcore/io/BufferIterator;->readInt()I

    move-result v13

    .line 157
    .local v13, "length":I
    const/16 v15, 0x2c

    if-ge v13, v15, :cond_45

    .line 158
    new-instance v15, Ljava/lang/AssertionError;

    const-string v16, "length in index file < sizeof(tzhead)"

    invoke-direct/range {v15 .. v16}, Ljava/lang/AssertionError;-><init>(Ljava/lang/Object;)V

    throw v15

    .line 160
    :cond_45
    sget-object v15, Llibcore/util/ZoneInfoDB;->rawUtcOffsets:[I

    invoke-virtual {v10}, Llibcore/io/BufferIterator;->readInt()I

    move-result v16

    aput v16, v15, v4

    .line 163
    array-length v12, v5

    .line 164
    .local v12, "len":I
    const/4 v11, 0x0

    .local v11, "j":I
    move v9, v8

    .end local v8    # "idOffset":I
    .local v9, "idOffset":I
    :goto_50
    if-ge v11, v12, :cond_56

    .line 165
    aget-byte v15, v5, v11

    if-nez v15, :cond_5c

    .line 171
    :cond_56
    aput v9, v7, v4

    .line 153
    add-int/lit8 v4, v4, 0x1

    move v8, v9

    .end local v9    # "idOffset":I
    .restart local v8    # "idOffset":I
    goto :goto_22

    .line 168
    .end local v8    # "idOffset":I
    .restart local v9    # "idOffset":I
    :cond_5c
    add-int/lit8 v8, v9, 0x1

    .end local v9    # "idOffset":I
    .restart local v8    # "idOffset":I
    aget-byte v15, v5, v11

    and-int/lit16 v15, v15, 0xff

    int-to-char v15, v15

    aput-char v15, v6, v9

    .line 164
    add-int/lit8 v11, v11, 0x1

    move v9, v8

    .end local v8    # "idOffset":I
    .restart local v9    # "idOffset":I
    goto :goto_50

    .line 176
    .end local v9    # "idOffset":I
    .end local v11    # "j":I
    .end local v12    # "len":I
    .end local v13    # "length":I
    .restart local v8    # "idOffset":I
    :cond_69
    new-instance v3, Ljava/lang/String;

    const/4 v15, 0x0

    invoke-direct {v3, v6, v15, v8}, Ljava/lang/String;-><init>([CII)V

    .line 177
    .local v3, "allIds":Ljava/lang/String;
    new-array v15, v14, [Ljava/lang/String;

    sput-object v15, Llibcore/util/ZoneInfoDB;->ids:[Ljava/lang/String;

    .line 178
    const/4 v4, 0x0

    :goto_74
    if-ge v4, v14, :cond_8d

    .line 179
    sget-object v16, Llibcore/util/ZoneInfoDB;->ids:[Ljava/lang/String;

    if-nez v4, :cond_88

    const/4 v15, 0x0

    :goto_7b
    aget v17, v7, v4

    move/from16 v0, v17

    invoke-virtual {v3, v15, v0}, Ljava/lang/String;->substring(II)Ljava/lang/String;

    move-result-object v15

    aput-object v15, v16, v4

    .line 178
    add-int/lit8 v4, v4, 0x1

    goto :goto_74

    .line 179
    :cond_88
    add-int/lit8 v15, v4, -0x1

    aget v15, v7, v15

    goto :goto_7b

    .line 181
    :cond_8d
    return-void
.end method

.method private static readVersion()Ljava/lang/String;
    .registers 6

    .prologue
    .line 98
    :try_start_0
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    sget-object v3, Llibcore/util/ZoneInfoDB;->ZONE_DIRECTORY_NAME:Ljava/lang/String;

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    const-string v3, "zoneinfo.version"

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-static {v2}, Llibcore/io/IoUtils;->readFileAsByteArray(Ljava/lang/String;)[B

    move-result-object v0

    .line 99
    .local v0, "bytes":[B
    new-instance v2, Ljava/lang/String;

    const/4 v3, 0x0

    array-length v4, v0

    sget-object v5, Ljava/nio/charset/Charsets;->ISO_8859_1:Ljava/nio/charset/Charset;

    invoke-direct {v2, v0, v3, v4, v5}, Ljava/lang/String;-><init>([BIILjava/nio/charset/Charset;)V

    invoke-virtual {v2}, Ljava/lang/String;->trim()Ljava/lang/String;
    :try_end_25
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_25} :catch_27

    move-result-object v2

    return-object v2

    .line 100
    :catch_27
    move-exception v1

    .line 101
    .local v1, "ex":Ljava/io/IOException;
    new-instance v2, Ljava/lang/RuntimeException;

    invoke-direct {v2, v1}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/Throwable;)V

    throw v2
.end method
