.class public Landroid/security/KeyStore;
.super Ljava/lang/Object;
.source "KeyStore.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/security/KeyStore$State;
    }
.end annotation


# static fields
.field public static final KEY_NOT_FOUND:I = 0x7

.field public static final LOCKED:I = 0x2

.field public static final NO_ERROR:I = 0x1

.field public static final PERMISSION_DENIED:I = 0x6

.field public static final PROTOCOL_ERROR:I = 0x5

.field public static final SYSTEM_ERROR:I = 0x4

.field public static final UNDEFINED_ACTION:I = 0x9

.field public static final UNINITIALIZED:I = 0x3

.field public static final VALUE_CORRUPTED:I = 0x8

.field public static final WRONG_PASSWORD:I = 0xa

.field private static final sAddress:Landroid/net/LocalSocketAddress;


# instance fields
.field private mError:I


# direct methods
.method static constructor <clinit>()V
    .registers 3

    .prologue
    .line 51
    new-instance v0, Landroid/net/LocalSocketAddress;

    const-string v1, "keystore"

    sget-object v2, Landroid/net/LocalSocketAddress$Namespace;->RESERVED:Landroid/net/LocalSocketAddress$Namespace;

    invoke-direct {v0, v1, v2}, Landroid/net/LocalSocketAddress;-><init>(Ljava/lang/String;Landroid/net/LocalSocketAddress$Namespace;)V

    sput-object v0, Landroid/security/KeyStore;->sAddress:Landroid/net/LocalSocketAddress;

    return-void
.end method

.method private constructor <init>()V
    .registers 2

    .prologue
    .line 56
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 54
    const/4 v0, 0x1

    iput v0, p0, Landroid/security/KeyStore;->mError:I

    .line 56
    return-void
.end method

.method private contains([B)Z
    .registers 6
    .param p1, "key"    # [B

    .prologue
    const/4 v1, 0x0

    const/4 v0, 0x1

    .line 100
    const/16 v2, 0x65

    new-array v3, v0, [[B

    aput-object p1, v3, v1

    invoke-direct {p0, v2, v3}, Landroid/security/KeyStore;->execute(I[[B)Ljava/util/ArrayList;

    .line 101
    iget v2, p0, Landroid/security/KeyStore;->mError:I

    if-ne v2, v0, :cond_10

    :goto_f
    return v0

    :cond_10
    move v0, v1

    goto :goto_f
.end method

.method private delete([B)Z
    .registers 6
    .param p1, "key"    # [B

    .prologue
    const/4 v1, 0x0

    const/4 v0, 0x1

    .line 91
    const/16 v2, 0x64

    new-array v3, v0, [[B

    aput-object p1, v3, v1

    invoke-direct {p0, v2, v3}, Landroid/security/KeyStore;->execute(I[[B)Ljava/util/ArrayList;

    .line 92
    iget v2, p0, Landroid/security/KeyStore;->mError:I

    if-ne v2, v0, :cond_10

    :goto_f
    return v0

    :cond_10
    move v0, v1

    goto :goto_f
.end method

.method private varargs execute(I[[B)Ljava/util/ArrayList;
    .registers 16
    .param p1, "code"    # I
    .param p2, "parameters"    # [[B
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I[[B)",
            "Ljava/util/ArrayList",
            "<[B>;"
        }
    .end annotation

    .prologue
    .line 163
    const/4 v11, 0x5

    iput v11, p0, Landroid/security/KeyStore;->mError:I

    .line 165
    move-object v0, p2

    .local v0, "arr$":[[B
    array-length v5, v0

    .local v5, "len$":I
    const/4 v2, 0x0

    .local v2, "i$":I
    :goto_6
    if-ge v2, v5, :cond_17

    aget-object v7, v0, v2

    .line 166
    .local v7, "parameter":[B
    if-eqz v7, :cond_12

    array-length v11, v7

    const v12, 0xffff

    if-le v11, v12, :cond_14

    .line 167
    :cond_12
    const/4 v10, 0x0

    .line 219
    .end local v7    # "parameter":[B
    :goto_13
    return-object v10

    .line 165
    .restart local v7    # "parameter":[B
    :cond_14
    add-int/lit8 v2, v2, 0x1

    goto :goto_6

    .line 171
    .end local v7    # "parameter":[B
    :cond_17
    new-instance v8, Landroid/net/LocalSocket;

    invoke-direct {v8}, Landroid/net/LocalSocket;-><init>()V

    .line 173
    .local v8, "socket":Landroid/net/LocalSocket;
    :try_start_1c
    sget-object v11, Landroid/security/KeyStore;->sAddress:Landroid/net/LocalSocketAddress;

    invoke-virtual {v8, v11}, Landroid/net/LocalSocket;->connect(Landroid/net/LocalSocketAddress;)V

    .line 175
    invoke-virtual {v8}, Landroid/net/LocalSocket;->getOutputStream()Ljava/io/OutputStream;

    move-result-object v6

    .line 176
    .local v6, "out":Ljava/io/OutputStream;
    invoke-virtual {v6, p1}, Ljava/io/OutputStream;->write(I)V

    .line 177
    move-object v0, p2

    array-length v5, v0

    const/4 v2, 0x0

    :goto_2b
    if-ge v2, v5, :cond_3f

    aget-object v7, v0, v2

    .line 178
    .restart local v7    # "parameter":[B
    array-length v11, v7

    shr-int/lit8 v11, v11, 0x8

    invoke-virtual {v6, v11}, Ljava/io/OutputStream;->write(I)V

    .line 179
    array-length v11, v7

    invoke-virtual {v6, v11}, Ljava/io/OutputStream;->write(I)V

    .line 180
    invoke-virtual {v6, v7}, Ljava/io/OutputStream;->write([B)V

    .line 177
    add-int/lit8 v2, v2, 0x1

    goto :goto_2b

    .line 182
    .end local v7    # "parameter":[B
    :cond_3f
    invoke-virtual {v6}, Ljava/io/OutputStream;->flush()V

    .line 183
    invoke-virtual {v8}, Landroid/net/LocalSocket;->shutdownOutput()V

    .line 185
    invoke-virtual {v8}, Landroid/net/LocalSocket;->getInputStream()Ljava/io/InputStream;

    move-result-object v3

    .line 186
    .local v3, "in":Ljava/io/InputStream;
    invoke-virtual {v3}, Ljava/io/InputStream;->read()I

    move-result p1

    const/4 v11, 0x1

    if-eq p1, v11, :cond_5c

    .line 187
    const/4 v11, -0x1

    if-eq p1, v11, :cond_55

    .line 188
    iput p1, p0, Landroid/security/KeyStore;->mError:I
    :try_end_55
    .catch Ljava/io/IOException; {:try_start_1c .. :try_end_55} :catch_a0
    .catchall {:try_start_1c .. :try_end_55} :catchall_a7

    .line 190
    :cond_55
    const/4 v10, 0x0

    .line 216
    :try_start_56
    invoke-virtual {v8}, Landroid/net/LocalSocket;->close()V
    :try_end_59
    .catch Ljava/io/IOException; {:try_start_56 .. :try_end_59} :catch_5a

    goto :goto_13

    .line 217
    :catch_5a
    move-exception v11

    goto :goto_13

    .line 193
    :cond_5c
    :try_start_5c
    new-instance v10, Ljava/util/ArrayList;

    invoke-direct {v10}, Ljava/util/ArrayList;-><init>()V

    .line 196
    .local v10, "values":Ljava/util/ArrayList;, "Ljava/util/ArrayList<[B>;"
    :goto_61
    invoke-virtual {v3}, Ljava/io/InputStream;->read()I

    move-result v1

    .local v1, "i":I
    const/4 v11, -0x1

    if-ne v1, v11, :cond_71

    .line 210
    const/4 v11, 0x1

    iput v11, p0, Landroid/security/KeyStore;->mError:I
    :try_end_6b
    .catch Ljava/io/IOException; {:try_start_5c .. :try_end_6b} :catch_a0
    .catchall {:try_start_5c .. :try_end_6b} :catchall_a7

    .line 216
    :try_start_6b
    invoke-virtual {v8}, Landroid/net/LocalSocket;->close()V
    :try_end_6e
    .catch Ljava/io/IOException; {:try_start_6b .. :try_end_6e} :catch_6f

    goto :goto_13

    .line 217
    :catch_6f
    move-exception v11

    goto :goto_13

    .line 199
    :cond_71
    :try_start_71
    invoke-virtual {v3}, Ljava/io/InputStream;->read()I
    :try_end_74
    .catch Ljava/io/IOException; {:try_start_71 .. :try_end_74} :catch_a0
    .catchall {:try_start_71 .. :try_end_74} :catchall_a7

    move-result v4

    .local v4, "j":I
    const/4 v11, -0x1

    if-ne v4, v11, :cond_7f

    .line 200
    const/4 v10, 0x0

    .line 216
    .end local v10    # "values":Ljava/util/ArrayList;, "Ljava/util/ArrayList<[B>;"
    :try_start_79
    invoke-virtual {v8}, Landroid/net/LocalSocket;->close()V
    :try_end_7c
    .catch Ljava/io/IOException; {:try_start_79 .. :try_end_7c} :catch_7d

    goto :goto_13

    .line 217
    :catch_7d
    move-exception v11

    goto :goto_13

    .line 202
    .restart local v10    # "values":Ljava/util/ArrayList;, "Ljava/util/ArrayList<[B>;"
    :cond_7f
    shl-int/lit8 v11, v1, 0x8

    or-int/2addr v11, v4

    :try_start_82
    new-array v9, v11, [B

    .line 203
    .local v9, "value":[B
    const/4 v1, 0x0

    :goto_85
    array-length v11, v9

    if-ge v1, v11, :cond_9c

    .line 204
    array-length v11, v9

    sub-int/2addr v11, v1

    invoke-virtual {v3, v9, v1, v11}, Ljava/io/InputStream;->read([BII)I
    :try_end_8d
    .catch Ljava/io/IOException; {:try_start_82 .. :try_end_8d} :catch_a0
    .catchall {:try_start_82 .. :try_end_8d} :catchall_a7

    move-result v4

    const/4 v11, -0x1

    if-ne v4, v11, :cond_9a

    .line 205
    const/4 v10, 0x0

    .line 216
    .end local v10    # "values":Ljava/util/ArrayList;, "Ljava/util/ArrayList<[B>;"
    :try_start_92
    invoke-virtual {v8}, Landroid/net/LocalSocket;->close()V
    :try_end_95
    .catch Ljava/io/IOException; {:try_start_92 .. :try_end_95} :catch_97

    goto/16 :goto_13

    .line 217
    :catch_97
    move-exception v11

    goto/16 :goto_13

    .line 203
    .restart local v10    # "values":Ljava/util/ArrayList;, "Ljava/util/ArrayList<[B>;"
    :cond_9a
    add-int/2addr v1, v4

    goto :goto_85

    .line 208
    :cond_9c
    :try_start_9c
    invoke-virtual {v10, v9}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z
    :try_end_9f
    .catch Ljava/io/IOException; {:try_start_9c .. :try_end_9f} :catch_a0
    .catchall {:try_start_9c .. :try_end_9f} :catchall_a7

    goto :goto_61

    .line 212
    .end local v1    # "i":I
    .end local v3    # "in":Ljava/io/InputStream;
    .end local v4    # "j":I
    .end local v6    # "out":Ljava/io/OutputStream;
    .end local v9    # "value":[B
    .end local v10    # "values":Ljava/util/ArrayList;, "Ljava/util/ArrayList<[B>;"
    :catch_a0
    move-exception v11

    .line 216
    :try_start_a1
    invoke-virtual {v8}, Landroid/net/LocalSocket;->close()V
    :try_end_a4
    .catch Ljava/io/IOException; {:try_start_a1 .. :try_end_a4} :catch_ac

    .line 219
    :goto_a4
    const/4 v10, 0x0

    goto/16 :goto_13

    .line 215
    :catchall_a7
    move-exception v11

    .line 216
    :try_start_a8
    invoke-virtual {v8}, Landroid/net/LocalSocket;->close()V
    :try_end_ab
    .catch Ljava/io/IOException; {:try_start_a8 .. :try_end_ab} :catch_ae

    .line 217
    :goto_ab
    throw v11

    :catch_ac
    move-exception v11

    goto :goto_a4

    :catch_ae
    move-exception v12

    goto :goto_ab
.end method

.method private get([B)[B
    .registers 6
    .param p1, "key"    # [B

    .prologue
    const/4 v3, 0x0

    .line 73
    const/16 v1, 0x67

    const/4 v2, 0x1

    new-array v2, v2, [[B

    aput-object p1, v2, v3

    invoke-direct {p0, v1, v2}, Landroid/security/KeyStore;->execute(I[[B)Ljava/util/ArrayList;

    move-result-object v0

    .line 74
    .local v0, "values":Ljava/util/ArrayList;, "Ljava/util/ArrayList<[B>;"
    if-eqz v0, :cond_14

    invoke-virtual {v0}, Ljava/util/ArrayList;->isEmpty()Z

    move-result v1

    if-eqz v1, :cond_16

    :cond_14
    const/4 v1, 0x0

    :goto_15
    return-object v1

    :cond_16
    invoke-virtual {v0, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v1

    check-cast v1, [B

    goto :goto_15
.end method

.method private static getBytes(Ljava/lang/String;)[B
    .registers 2
    .param p0, "string"    # Ljava/lang/String;

    .prologue
    .line 223
    sget-object v0, Ljava/nio/charset/Charsets;->UTF_8:Ljava/nio/charset/Charset;

    invoke-virtual {p0, v0}, Ljava/lang/String;->getBytes(Ljava/nio/charset/Charset;)[B

    move-result-object v0

    return-object v0
.end method

.method public static getInstance()Landroid/security/KeyStore;
    .registers 1

    .prologue
    .line 59
    new-instance v0, Landroid/security/KeyStore;

    invoke-direct {v0}, Landroid/security/KeyStore;-><init>()V

    return-object v0
.end method

.method private password([B)Z
    .registers 6
    .param p1, "password"    # [B

    .prologue
    const/4 v1, 0x0

    const/4 v0, 0x1

    .line 131
    const/16 v2, 0x70

    new-array v3, v0, [[B

    aput-object p1, v3, v1

    invoke-direct {p0, v2, v3}, Landroid/security/KeyStore;->execute(I[[B)Ljava/util/ArrayList;

    .line 132
    iget v2, p0, Landroid/security/KeyStore;->mError:I

    if-ne v2, v0, :cond_10

    :goto_f
    return v0

    :cond_10
    move v0, v1

    goto :goto_f
.end method

.method private put([B[B)Z
    .registers 7
    .param p1, "key"    # [B
    .param p2, "value"    # [B

    .prologue
    const/4 v1, 0x0

    const/4 v0, 0x1

    .line 82
    const/16 v2, 0x69

    const/4 v3, 0x2

    new-array v3, v3, [[B

    aput-object p1, v3, v1

    aput-object p2, v3, v0

    invoke-direct {p0, v2, v3}, Landroid/security/KeyStore;->execute(I[[B)Ljava/util/ArrayList;

    .line 83
    iget v2, p0, Landroid/security/KeyStore;->mError:I

    if-ne v2, v0, :cond_13

    :goto_12
    return v0

    :cond_13
    move v0, v1

    goto :goto_12
.end method

.method private static toString([B)Ljava/lang/String;
    .registers 3
    .param p0, "bytes"    # [B

    .prologue
    .line 227
    new-instance v0, Ljava/lang/String;

    sget-object v1, Ljava/nio/charset/Charsets;->UTF_8:Ljava/nio/charset/Charset;

    invoke-direct {v0, p0, v1}, Ljava/lang/String;-><init>([BLjava/nio/charset/Charset;)V

    return-object v0
.end method

.method private unlock([B)Z
    .registers 6
    .param p1, "password"    # [B

    .prologue
    const/4 v1, 0x0

    const/4 v0, 0x1

    .line 145
    const/16 v2, 0x75

    new-array v3, v0, [[B

    aput-object p1, v3, v1

    invoke-direct {p0, v2, v3}, Landroid/security/KeyStore;->execute(I[[B)Ljava/util/ArrayList;

    .line 146
    iget v2, p0, Landroid/security/KeyStore;->mError:I

    if-ne v2, v0, :cond_10

    :goto_f
    return v0

    :cond_10
    move v0, v1

    goto :goto_f
.end method


# virtual methods
.method public contains(Ljava/lang/String;)Z
    .registers 3
    .param p1, "key"    # Ljava/lang/String;

    .prologue
    .line 105
    invoke-static {p1}, Landroid/security/KeyStore;->getBytes(Ljava/lang/String;)[B

    move-result-object v0

    invoke-direct {p0, v0}, Landroid/security/KeyStore;->contains([B)Z

    move-result v0

    return v0
.end method

.method public delete(Ljava/lang/String;)Z
    .registers 3
    .param p1, "key"    # Ljava/lang/String;

    .prologue
    .line 96
    invoke-static {p1}, Landroid/security/KeyStore;->getBytes(Ljava/lang/String;)[B

    move-result-object v0

    invoke-direct {p0, v0}, Landroid/security/KeyStore;->delete([B)Z

    move-result v0

    return v0
.end method

.method public get(Ljava/lang/String;)[B
    .registers 3
    .param p1, "key"    # Ljava/lang/String;

    .prologue
    .line 78
    invoke-static {p1}, Landroid/security/KeyStore;->getBytes(Ljava/lang/String;)[B

    move-result-object v0

    invoke-direct {p0, v0}, Landroid/security/KeyStore;->get([B)[B

    move-result-object v0

    return-object v0
.end method

.method public getLastError()I
    .registers 2

    .prologue
    .line 159
    iget v0, p0, Landroid/security/KeyStore;->mError:I

    return v0
.end method

.method public isEmpty()Z
    .registers 4

    .prologue
    const/4 v0, 0x0

    .line 154
    const/16 v1, 0x7a

    new-array v2, v0, [[B

    invoke-direct {p0, v1, v2}, Landroid/security/KeyStore;->execute(I[[B)Ljava/util/ArrayList;

    .line 155
    iget v1, p0, Landroid/security/KeyStore;->mError:I

    const/4 v2, 0x7

    if-ne v1, v2, :cond_e

    const/4 v0, 0x1

    :cond_e
    return v0
.end method

.method public lock()Z
    .registers 5

    .prologue
    const/4 v0, 0x1

    const/4 v1, 0x0

    .line 140
    const/16 v2, 0x6c

    new-array v3, v1, [[B

    invoke-direct {p0, v2, v3}, Landroid/security/KeyStore;->execute(I[[B)Ljava/util/ArrayList;

    .line 141
    iget v2, p0, Landroid/security/KeyStore;->mError:I

    if-ne v2, v0, :cond_e

    :goto_d
    return v0

    :cond_e
    move v0, v1

    goto :goto_d
.end method

.method public password(Ljava/lang/String;)Z
    .registers 3
    .param p1, "password"    # Ljava/lang/String;

    .prologue
    .line 136
    invoke-static {p1}, Landroid/security/KeyStore;->getBytes(Ljava/lang/String;)[B

    move-result-object v0

    invoke-direct {p0, v0}, Landroid/security/KeyStore;->password([B)Z

    move-result v0

    return v0
.end method

.method public put(Ljava/lang/String;[B)Z
    .registers 4
    .param p1, "key"    # Ljava/lang/String;
    .param p2, "value"    # [B

    .prologue
    .line 87
    invoke-static {p1}, Landroid/security/KeyStore;->getBytes(Ljava/lang/String;)[B

    move-result-object v0

    invoke-direct {p0, v0, p2}, Landroid/security/KeyStore;->put([B[B)Z

    move-result v0

    return v0
.end method

.method public reset()Z
    .registers 5

    .prologue
    const/4 v0, 0x1

    const/4 v1, 0x0

    .line 126
    const/16 v2, 0x72

    new-array v3, v1, [[B

    invoke-direct {p0, v2, v3}, Landroid/security/KeyStore;->execute(I[[B)Ljava/util/ArrayList;

    .line 127
    iget v2, p0, Landroid/security/KeyStore;->mError:I

    if-ne v2, v0, :cond_e

    :goto_d
    return v0

    :cond_e
    move v0, v1

    goto :goto_d
.end method

.method public saw(Ljava/lang/String;)[Ljava/lang/String;
    .registers 6
    .param p1, "prefix"    # Ljava/lang/String;

    .prologue
    .line 114
    invoke-static {p1}, Landroid/security/KeyStore;->getBytes(Ljava/lang/String;)[B

    move-result-object v3

    invoke-virtual {p0, v3}, Landroid/security/KeyStore;->saw([B)[[B

    move-result-object v2

    .line 115
    .local v2, "values":[[B
    if-nez v2, :cond_c

    .line 116
    const/4 v1, 0x0

    .line 122
    :cond_b
    return-object v1

    .line 118
    :cond_c
    array-length v3, v2

    new-array v1, v3, [Ljava/lang/String;

    .line 119
    .local v1, "strings":[Ljava/lang/String;
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_10
    array-length v3, v2

    if-ge v0, v3, :cond_b

    .line 120
    aget-object v3, v2, v0

    invoke-static {v3}, Landroid/security/KeyStore;->toString([B)Ljava/lang/String;

    move-result-object v3

    aput-object v3, v1, v0

    .line 119
    add-int/lit8 v0, v0, 0x1

    goto :goto_10
.end method

.method public saw([B)[[B
    .registers 6
    .param p1, "prefix"    # [B

    .prologue
    .line 109
    const/16 v1, 0x73

    const/4 v2, 0x1

    new-array v2, v2, [[B

    const/4 v3, 0x0

    aput-object p1, v2, v3

    invoke-direct {p0, v1, v2}, Landroid/security/KeyStore;->execute(I[[B)Ljava/util/ArrayList;

    move-result-object v0

    .line 110
    .local v0, "values":Ljava/util/ArrayList;, "Ljava/util/ArrayList<[B>;"
    if-nez v0, :cond_12

    const/4 v1, 0x0

    check-cast v1, [[B

    :goto_11
    return-object v1

    :cond_12
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    move-result v1

    new-array v1, v1, [[B

    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->toArray([Ljava/lang/Object;)[Ljava/lang/Object;

    move-result-object v1

    check-cast v1, [[B

    goto :goto_11
.end method

.method public state()Landroid/security/KeyStore$State;
    .registers 3

    .prologue
    .line 63
    const/16 v0, 0x74

    const/4 v1, 0x0

    new-array v1, v1, [[B

    invoke-direct {p0, v0, v1}, Landroid/security/KeyStore;->execute(I[[B)Ljava/util/ArrayList;

    .line 64
    iget v0, p0, Landroid/security/KeyStore;->mError:I

    packed-switch v0, :pswitch_data_1e

    .line 68
    new-instance v0, Ljava/lang/AssertionError;

    iget v1, p0, Landroid/security/KeyStore;->mError:I

    invoke-direct {v0, v1}, Ljava/lang/AssertionError;-><init>(I)V

    throw v0

    .line 65
    :pswitch_15
    sget-object v0, Landroid/security/KeyStore$State;->UNLOCKED:Landroid/security/KeyStore$State;

    .line 67
    :goto_17
    return-object v0

    .line 66
    :pswitch_18
    sget-object v0, Landroid/security/KeyStore$State;->LOCKED:Landroid/security/KeyStore$State;

    goto :goto_17

    .line 67
    :pswitch_1b
    sget-object v0, Landroid/security/KeyStore$State;->UNINITIALIZED:Landroid/security/KeyStore$State;

    goto :goto_17

    .line 64
    :pswitch_data_1e
    .packed-switch 0x1
        :pswitch_15
        :pswitch_18
        :pswitch_1b
    .end packed-switch
.end method

.method public unlock(Ljava/lang/String;)Z
    .registers 3
    .param p1, "password"    # Ljava/lang/String;

    .prologue
    .line 150
    invoke-static {p1}, Landroid/security/KeyStore;->getBytes(Ljava/lang/String;)[B

    move-result-object v0

    invoke-direct {p0, v0}, Landroid/security/KeyStore;->unlock([B)Z

    move-result v0

    return v0
.end method
