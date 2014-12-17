.class public final Landroid/util/JsonReader;
.super Ljava/lang/Object;
.source "JsonReader.java"

# interfaces
.implements Ljava/io/Closeable;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/util/JsonReader$1;
    }
.end annotation


# static fields
.field private static final FALSE:Ljava/lang/String; = "false"

.field private static final TRUE:Ljava/lang/String; = "true"


# instance fields
.field private final buffer:[C

.field private bufferStartColumn:I

.field private bufferStartLine:I

.field private final in:Ljava/io/Reader;

.field private lenient:Z

.field private limit:I

.field private name:Ljava/lang/String;

.field private pos:I

.field private skipping:Z

.field private final stack:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List",
            "<",
            "Landroid/util/JsonScope;",
            ">;"
        }
    .end annotation
.end field

.field private final stringPool:Llibcore/internal/StringPool;

.field private token:Landroid/util/JsonToken;

.field private value:Ljava/lang/String;

.field private valueLength:I

.field private valuePos:I


# direct methods
.method public constructor <init>(Ljava/io/Reader;)V
    .registers 5
    .param p1, "in"    # Ljava/io/Reader;

    .prologue
    const/4 v2, 0x1

    const/4 v1, 0x0

    .line 233
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 181
    new-instance v0, Llibcore/internal/StringPool;

    invoke-direct {v0}, Llibcore/internal/StringPool;-><init>()V

    iput-object v0, p0, Landroid/util/JsonReader;->stringPool:Llibcore/internal/StringPool;

    .line 187
    iput-boolean v1, p0, Landroid/util/JsonReader;->lenient:Z

    .line 195
    const/16 v0, 0x400

    new-array v0, v0, [C

    iput-object v0, p0, Landroid/util/JsonReader;->buffer:[C

    .line 196
    iput v1, p0, Landroid/util/JsonReader;->pos:I

    .line 197
    iput v1, p0, Landroid/util/JsonReader;->limit:I

    .line 202
    iput v2, p0, Landroid/util/JsonReader;->bufferStartLine:I

    .line 203
    iput v2, p0, Landroid/util/JsonReader;->bufferStartColumn:I

    .line 205
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Landroid/util/JsonReader;->stack:Ljava/util/List;

    .line 207
    sget-object v0, Landroid/util/JsonScope;->EMPTY_DOCUMENT:Landroid/util/JsonScope;

    invoke-direct {p0, v0}, Landroid/util/JsonReader;->push(Landroid/util/JsonScope;)V

    .line 228
    iput-boolean v1, p0, Landroid/util/JsonReader;->skipping:Z

    .line 234
    if-nez p1, :cond_34

    .line 235
    new-instance v0, Ljava/lang/NullPointerException;

    const-string v1, "in == null"

    invoke-direct {v0, v1}, Ljava/lang/NullPointerException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 237
    :cond_34
    iput-object p1, p0, Landroid/util/JsonReader;->in:Ljava/io/Reader;

    .line 238
    return-void
.end method

.method private advance()Landroid/util/JsonToken;
    .registers 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    const/4 v1, 0x0

    .line 371
    invoke-virtual {p0}, Landroid/util/JsonReader;->peek()Landroid/util/JsonToken;

    .line 373
    iget-object v0, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    .line 374
    .local v0, "result":Landroid/util/JsonToken;
    iput-object v1, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    .line 375
    iput-object v1, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    .line 376
    iput-object v1, p0, Landroid/util/JsonReader;->name:Ljava/lang/String;

    .line 377
    return-object v0
.end method

.method private checkLenient()V
    .registers 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 834
    iget-boolean v0, p0, Landroid/util/JsonReader;->lenient:Z

    if-nez v0, :cond_b

    .line 835
    const-string v0, "Use JsonReader.setLenient(true) to accept malformed JSON"

    invoke-direct {p0, v0}, Landroid/util/JsonReader;->syntaxError(Ljava/lang/String;)Ljava/io/IOException;

    move-result-object v0

    throw v0

    .line 837
    :cond_b
    return-void
.end method

.method private decodeLiteral()Landroid/util/JsonToken;
    .registers 9
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    const/16 v7, 0x55

    const/16 v6, 0x45

    const/4 v5, 0x4

    const/16 v4, 0x6c

    const/16 v3, 0x4c

    .line 1066
    iget v0, p0, Landroid/util/JsonReader;->valuePos:I

    const/4 v1, -0x1

    if-ne v0, v1, :cond_11

    .line 1068
    sget-object v0, Landroid/util/JsonToken;->STRING:Landroid/util/JsonToken;

    .line 1093
    :goto_10
    return-object v0

    .line 1069
    :cond_11
    iget v0, p0, Landroid/util/JsonReader;->valueLength:I

    if-ne v0, v5, :cond_6f

    const/16 v0, 0x6e

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    aget-char v1, v1, v2

    if-eq v0, v1, :cond_29

    const/16 v0, 0x4e

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    aget-char v1, v1, v2

    if-ne v0, v1, :cond_6f

    :cond_29
    const/16 v0, 0x75

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v2, v2, 0x1

    aget-char v1, v1, v2

    if-eq v0, v1, :cond_3f

    iget-object v0, p0, Landroid/util/JsonReader;->buffer:[C

    iget v1, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v1, v1, 0x1

    aget-char v0, v0, v1

    if-ne v7, v0, :cond_6f

    :cond_3f
    iget-object v0, p0, Landroid/util/JsonReader;->buffer:[C

    iget v1, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v1, v1, 0x2

    aget-char v0, v0, v1

    if-eq v4, v0, :cond_53

    iget-object v0, p0, Landroid/util/JsonReader;->buffer:[C

    iget v1, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v1, v1, 0x2

    aget-char v0, v0, v1

    if-ne v3, v0, :cond_6f

    :cond_53
    iget-object v0, p0, Landroid/util/JsonReader;->buffer:[C

    iget v1, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v1, v1, 0x3

    aget-char v0, v0, v1

    if-eq v4, v0, :cond_67

    iget-object v0, p0, Landroid/util/JsonReader;->buffer:[C

    iget v1, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v1, v1, 0x3

    aget-char v0, v0, v1

    if-ne v3, v0, :cond_6f

    .line 1074
    :cond_67
    const-string/jumbo v0, "null"

    iput-object v0, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    .line 1075
    sget-object v0, Landroid/util/JsonToken;->NULL:Landroid/util/JsonToken;

    goto :goto_10

    .line 1076
    :cond_6f
    iget v0, p0, Landroid/util/JsonReader;->valueLength:I

    if-ne v0, v5, :cond_d4

    const/16 v0, 0x74

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    aget-char v1, v1, v2

    if-eq v0, v1, :cond_87

    const/16 v0, 0x54

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    aget-char v1, v1, v2

    if-ne v0, v1, :cond_d4

    :cond_87
    const/16 v0, 0x72

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v2, v2, 0x1

    aget-char v1, v1, v2

    if-eq v0, v1, :cond_9f

    const/16 v0, 0x52

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v2, v2, 0x1

    aget-char v1, v1, v2

    if-ne v0, v1, :cond_d4

    :cond_9f
    const/16 v0, 0x75

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v2, v2, 0x2

    aget-char v1, v1, v2

    if-eq v0, v1, :cond_b5

    iget-object v0, p0, Landroid/util/JsonReader;->buffer:[C

    iget v1, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v1, v1, 0x2

    aget-char v0, v0, v1

    if-ne v7, v0, :cond_d4

    :cond_b5
    const/16 v0, 0x65

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v2, v2, 0x3

    aget-char v1, v1, v2

    if-eq v0, v1, :cond_cb

    iget-object v0, p0, Landroid/util/JsonReader;->buffer:[C

    iget v1, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v1, v1, 0x3

    aget-char v0, v0, v1

    if-ne v6, v0, :cond_d4

    .line 1081
    :cond_cb
    const-string/jumbo v0, "true"

    iput-object v0, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    .line 1082
    sget-object v0, Landroid/util/JsonToken;->BOOLEAN:Landroid/util/JsonToken;

    goto/16 :goto_10

    .line 1083
    :cond_d4
    iget v0, p0, Landroid/util/JsonReader;->valueLength:I

    const/4 v1, 0x5

    if-ne v0, v1, :cond_14f

    const/16 v0, 0x66

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    aget-char v1, v1, v2

    if-eq v0, v1, :cond_ed

    const/16 v0, 0x46

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    aget-char v1, v1, v2

    if-ne v0, v1, :cond_14f

    :cond_ed
    const/16 v0, 0x61

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v2, v2, 0x1

    aget-char v1, v1, v2

    if-eq v0, v1, :cond_105

    const/16 v0, 0x41

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v2, v2, 0x1

    aget-char v1, v1, v2

    if-ne v0, v1, :cond_14f

    :cond_105
    iget-object v0, p0, Landroid/util/JsonReader;->buffer:[C

    iget v1, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v1, v1, 0x2

    aget-char v0, v0, v1

    if-eq v4, v0, :cond_119

    iget-object v0, p0, Landroid/util/JsonReader;->buffer:[C

    iget v1, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v1, v1, 0x2

    aget-char v0, v0, v1

    if-ne v3, v0, :cond_14f

    :cond_119
    const/16 v0, 0x73

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v2, v2, 0x3

    aget-char v1, v1, v2

    if-eq v0, v1, :cond_131

    const/16 v0, 0x53

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v2, v2, 0x3

    aget-char v1, v1, v2

    if-ne v0, v1, :cond_14f

    :cond_131
    const/16 v0, 0x65

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v2, v2, 0x4

    aget-char v1, v1, v2

    if-eq v0, v1, :cond_147

    iget-object v0, p0, Landroid/util/JsonReader;->buffer:[C

    iget v1, p0, Landroid/util/JsonReader;->valuePos:I

    add-int/lit8 v1, v1, 0x4

    aget-char v0, v0, v1

    if-ne v6, v0, :cond_14f

    .line 1089
    :cond_147
    const-string v0, "false"

    iput-object v0, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    .line 1090
    sget-object v0, Landroid/util/JsonToken;->BOOLEAN:Landroid/util/JsonToken;

    goto/16 :goto_10

    .line 1092
    :cond_14f
    iget-object v0, p0, Landroid/util/JsonReader;->stringPool:Llibcore/internal/StringPool;

    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->valuePos:I

    iget v3, p0, Landroid/util/JsonReader;->valueLength:I

    invoke-virtual {v0, v1, v2, v3}, Llibcore/internal/StringPool;->get([CII)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    .line 1093
    iget-object v0, p0, Landroid/util/JsonReader;->buffer:[C

    iget v1, p0, Landroid/util/JsonReader;->valuePos:I

    iget v2, p0, Landroid/util/JsonReader;->valueLength:I

    invoke-direct {p0, v0, v1, v2}, Landroid/util/JsonReader;->decodeNumber([CII)Landroid/util/JsonToken;

    move-result-object v0

    goto/16 :goto_10
.end method

.method private decodeNumber([CII)Landroid/util/JsonToken;
    .registers 10
    .param p1, "chars"    # [C
    .param p2, "offset"    # I
    .param p3, "length"    # I

    .prologue
    const/16 v5, 0x2d

    const/16 v4, 0x39

    const/16 v3, 0x30

    .line 1104
    move v1, p2

    .line 1105
    .local v1, "i":I
    aget-char v0, p1, v1

    .line 1107
    .local v0, "c":I
    if-ne v0, v5, :cond_f

    .line 1108
    add-int/lit8 v1, v1, 0x1

    aget-char v0, p1, v1

    .line 1111
    :cond_f
    if-ne v0, v3, :cond_26

    .line 1112
    add-int/lit8 v1, v1, 0x1

    aget-char v0, p1, v1

    .line 1122
    :cond_15
    const/16 v2, 0x2e

    if-ne v0, v2, :cond_3c

    .line 1123
    add-int/lit8 v1, v1, 0x1

    aget-char v0, p1, v1

    .line 1124
    :goto_1d
    if-lt v0, v3, :cond_3c

    if-gt v0, v4, :cond_3c

    .line 1125
    add-int/lit8 v1, v1, 0x1

    aget-char v0, p1, v1

    goto :goto_1d

    .line 1113
    :cond_26
    const/16 v2, 0x31

    if-lt v0, v2, :cond_39

    if-gt v0, v4, :cond_39

    .line 1114
    add-int/lit8 v1, v1, 0x1

    aget-char v0, p1, v1

    .line 1115
    :goto_30
    if-lt v0, v3, :cond_15

    if-gt v0, v4, :cond_15

    .line 1116
    add-int/lit8 v1, v1, 0x1

    aget-char v0, p1, v1

    goto :goto_30

    .line 1119
    :cond_39
    sget-object v2, Landroid/util/JsonToken;->STRING:Landroid/util/JsonToken;

    .line 1147
    :goto_3b
    return-object v2

    .line 1129
    :cond_3c
    const/16 v2, 0x65

    if-eq v0, v2, :cond_44

    const/16 v2, 0x45

    if-ne v0, v2, :cond_66

    .line 1130
    :cond_44
    add-int/lit8 v1, v1, 0x1

    aget-char v0, p1, v1

    .line 1131
    const/16 v2, 0x2b

    if-eq v0, v2, :cond_4e

    if-ne v0, v5, :cond_52

    .line 1132
    :cond_4e
    add-int/lit8 v1, v1, 0x1

    aget-char v0, p1, v1

    .line 1134
    :cond_52
    if-lt v0, v3, :cond_63

    if-gt v0, v4, :cond_63

    .line 1135
    add-int/lit8 v1, v1, 0x1

    aget-char v0, p1, v1

    .line 1136
    :goto_5a
    if-lt v0, v3, :cond_66

    if-gt v0, v4, :cond_66

    .line 1137
    add-int/lit8 v1, v1, 0x1

    aget-char v0, p1, v1

    goto :goto_5a

    .line 1140
    :cond_63
    sget-object v2, Landroid/util/JsonToken;->STRING:Landroid/util/JsonToken;

    goto :goto_3b

    .line 1144
    :cond_66
    add-int v2, p2, p3

    if-ne v1, v2, :cond_6d

    .line 1145
    sget-object v2, Landroid/util/JsonToken;->NUMBER:Landroid/util/JsonToken;

    goto :goto_3b

    .line 1147
    :cond_6d
    sget-object v2, Landroid/util/JsonToken;->STRING:Landroid/util/JsonToken;

    goto :goto_3b
.end method

.method private expect(Landroid/util/JsonToken;)V
    .registers 5
    .param p1, "expected"    # Landroid/util/JsonToken;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 308
    invoke-virtual {p0}, Landroid/util/JsonReader;->peek()Landroid/util/JsonToken;

    .line 309
    iget-object v0, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    if-eq v0, p1, :cond_2e

    .line 310
    new-instance v0, Ljava/lang/IllegalStateException;

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "Expected "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, " but was "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {p0}, Landroid/util/JsonReader;->peek()Landroid/util/JsonToken;

    move-result-object v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 312
    :cond_2e
    invoke-direct {p0}, Landroid/util/JsonReader;->advance()Landroid/util/JsonToken;

    .line 313
    return-void
.end method

.method private fillBuffer(I)Z
    .registers 11
    .param p1, "minimum"    # I
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    const/4 v2, 0x1

    const/4 v3, 0x0

    .line 721
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_3
    iget v4, p0, Landroid/util/JsonReader;->pos:I

    if-ge v0, v4, :cond_21

    .line 722
    iget-object v4, p0, Landroid/util/JsonReader;->buffer:[C

    aget-char v4, v4, v0

    const/16 v5, 0xa

    if-ne v4, v5, :cond_1a

    .line 723
    iget v4, p0, Landroid/util/JsonReader;->bufferStartLine:I

    add-int/lit8 v4, v4, 0x1

    iput v4, p0, Landroid/util/JsonReader;->bufferStartLine:I

    .line 724
    iput v2, p0, Landroid/util/JsonReader;->bufferStartColumn:I

    .line 721
    :goto_17
    add-int/lit8 v0, v0, 0x1

    goto :goto_3

    .line 726
    :cond_1a
    iget v4, p0, Landroid/util/JsonReader;->bufferStartColumn:I

    add-int/lit8 v4, v4, 0x1

    iput v4, p0, Landroid/util/JsonReader;->bufferStartColumn:I

    goto :goto_17

    .line 730
    :cond_21
    iget v4, p0, Landroid/util/JsonReader;->limit:I

    iget v5, p0, Landroid/util/JsonReader;->pos:I

    if-eq v4, v5, :cond_79

    .line 731
    iget v4, p0, Landroid/util/JsonReader;->limit:I

    iget v5, p0, Landroid/util/JsonReader;->pos:I

    sub-int/2addr v4, v5

    iput v4, p0, Landroid/util/JsonReader;->limit:I

    .line 732
    iget-object v4, p0, Landroid/util/JsonReader;->buffer:[C

    iget v5, p0, Landroid/util/JsonReader;->pos:I

    iget-object v6, p0, Landroid/util/JsonReader;->buffer:[C

    iget v7, p0, Landroid/util/JsonReader;->limit:I

    invoke-static {v4, v5, v6, v3, v7}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    .line 737
    :goto_39
    iput v3, p0, Landroid/util/JsonReader;->pos:I

    .line 739
    :cond_3b
    iget-object v4, p0, Landroid/util/JsonReader;->in:Ljava/io/Reader;

    iget-object v5, p0, Landroid/util/JsonReader;->buffer:[C

    iget v6, p0, Landroid/util/JsonReader;->limit:I

    iget-object v7, p0, Landroid/util/JsonReader;->buffer:[C

    array-length v7, v7

    iget v8, p0, Landroid/util/JsonReader;->limit:I

    sub-int/2addr v7, v8

    invoke-virtual {v4, v5, v6, v7}, Ljava/io/Reader;->read([CII)I

    move-result v1

    .local v1, "total":I
    const/4 v4, -0x1

    if-eq v1, v4, :cond_7c

    .line 740
    iget v4, p0, Landroid/util/JsonReader;->limit:I

    add-int/2addr v4, v1

    iput v4, p0, Landroid/util/JsonReader;->limit:I

    .line 743
    iget v4, p0, Landroid/util/JsonReader;->bufferStartLine:I

    if-ne v4, v2, :cond_74

    iget v4, p0, Landroid/util/JsonReader;->bufferStartColumn:I

    if-ne v4, v2, :cond_74

    iget v4, p0, Landroid/util/JsonReader;->limit:I

    if-lez v4, :cond_74

    iget-object v4, p0, Landroid/util/JsonReader;->buffer:[C

    aget-char v4, v4, v3

    const v5, 0xfeff

    if-ne v4, v5, :cond_74

    .line 745
    iget v4, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v4, v4, 0x1

    iput v4, p0, Landroid/util/JsonReader;->pos:I

    .line 746
    iget v4, p0, Landroid/util/JsonReader;->bufferStartColumn:I

    add-int/lit8 v4, v4, -0x1

    iput v4, p0, Landroid/util/JsonReader;->bufferStartColumn:I

    .line 749
    :cond_74
    iget v4, p0, Landroid/util/JsonReader;->limit:I

    if-lt v4, p1, :cond_3b

    .line 753
    :goto_78
    return v2

    .line 734
    .end local v1    # "total":I
    :cond_79
    iput v3, p0, Landroid/util/JsonReader;->limit:I

    goto :goto_39

    .restart local v1    # "total":I
    :cond_7c
    move v2, v3

    .line 753
    goto :goto_78
.end method

.method private getColumnNumber()I
    .registers 5

    .prologue
    .line 767
    iget v1, p0, Landroid/util/JsonReader;->bufferStartColumn:I

    .line 768
    .local v1, "result":I
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_3
    iget v2, p0, Landroid/util/JsonReader;->pos:I

    if-ge v0, v2, :cond_16

    .line 769
    iget-object v2, p0, Landroid/util/JsonReader;->buffer:[C

    aget-char v2, v2, v0

    const/16 v3, 0xa

    if-ne v2, v3, :cond_13

    .line 770
    const/4 v1, 0x1

    .line 768
    :goto_10
    add-int/lit8 v0, v0, 0x1

    goto :goto_3

    .line 772
    :cond_13
    add-int/lit8 v1, v1, 0x1

    goto :goto_10

    .line 775
    :cond_16
    return v1
.end method

.method private getLineNumber()I
    .registers 5

    .prologue
    .line 757
    iget v1, p0, Landroid/util/JsonReader;->bufferStartLine:I

    .line 758
    .local v1, "result":I
    const/4 v0, 0x0

    .local v0, "i":I
    :goto_3
    iget v2, p0, Landroid/util/JsonReader;->pos:I

    if-ge v0, v2, :cond_14

    .line 759
    iget-object v2, p0, Landroid/util/JsonReader;->buffer:[C

    aget-char v2, v2, v0

    const/16 v3, 0xa

    if-ne v2, v3, :cond_11

    .line 760
    add-int/lit8 v1, v1, 0x1

    .line 758
    :cond_11
    add-int/lit8 v0, v0, 0x1

    goto :goto_3

    .line 763
    :cond_14
    return v1
.end method

.method private getSnippet()Ljava/lang/CharSequence;
    .registers 7

    .prologue
    const/16 v5, 0x14

    .line 1161
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    .line 1162
    .local v2, "snippet":Ljava/lang/StringBuilder;
    iget v3, p0, Landroid/util/JsonReader;->pos:I

    invoke-static {v3, v5}, Ljava/lang/Math;->min(II)I

    move-result v1

    .line 1163
    .local v1, "beforePos":I
    iget-object v3, p0, Landroid/util/JsonReader;->buffer:[C

    iget v4, p0, Landroid/util/JsonReader;->pos:I

    sub-int/2addr v4, v1

    invoke-virtual {v2, v3, v4, v1}, Ljava/lang/StringBuilder;->append([CII)Ljava/lang/StringBuilder;

    .line 1164
    iget v3, p0, Landroid/util/JsonReader;->limit:I

    iget v4, p0, Landroid/util/JsonReader;->pos:I

    sub-int/2addr v3, v4

    invoke-static {v3, v5}, Ljava/lang/Math;->min(II)I

    move-result v0

    .line 1165
    .local v0, "afterPos":I
    iget-object v3, p0, Landroid/util/JsonReader;->buffer:[C

    iget v4, p0, Landroid/util/JsonReader;->pos:I

    invoke-virtual {v2, v3, v4, v0}, Ljava/lang/StringBuilder;->append([CII)Ljava/lang/StringBuilder;

    .line 1166
    return-object v2
.end method

.method private nextInArray(Z)Landroid/util/JsonToken;
    .registers 3
    .param p1, "firstElement"    # Z
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 583
    if-eqz p1, :cond_19

    .line 584
    sget-object v0, Landroid/util/JsonScope;->NONEMPTY_ARRAY:Landroid/util/JsonScope;

    invoke-direct {p0, v0}, Landroid/util/JsonReader;->replaceTop(Landroid/util/JsonScope;)V

    .line 600
    :goto_7
    :sswitch_7
    invoke-direct {p0}, Landroid/util/JsonReader;->nextNonWhitespace()I

    move-result v0

    sparse-switch v0, :sswitch_data_50

    .line 615
    iget v0, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v0, v0, -0x1

    iput v0, p0, Landroid/util/JsonReader;->pos:I

    .line 616
    invoke-direct {p0}, Landroid/util/JsonReader;->nextValue()Landroid/util/JsonToken;

    move-result-object v0

    :goto_18
    return-object v0

    .line 587
    :cond_19
    invoke-direct {p0}, Landroid/util/JsonReader;->nextNonWhitespace()I

    move-result v0

    sparse-switch v0, :sswitch_data_5e

    .line 596
    const-string v0, "Unterminated array"

    invoke-direct {p0, v0}, Landroid/util/JsonReader;->syntaxError(Ljava/lang/String;)Ljava/io/IOException;

    move-result-object v0

    throw v0

    .line 589
    :sswitch_27
    invoke-direct {p0}, Landroid/util/JsonReader;->pop()Landroid/util/JsonScope;

    .line 590
    sget-object v0, Landroid/util/JsonToken;->END_ARRAY:Landroid/util/JsonToken;

    iput-object v0, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    goto :goto_18

    .line 592
    :sswitch_2f
    invoke-direct {p0}, Landroid/util/JsonReader;->checkLenient()V

    goto :goto_7

    .line 602
    :sswitch_33
    if-eqz p1, :cond_3d

    .line 603
    invoke-direct {p0}, Landroid/util/JsonReader;->pop()Landroid/util/JsonScope;

    .line 604
    sget-object v0, Landroid/util/JsonToken;->END_ARRAY:Landroid/util/JsonToken;

    iput-object v0, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    goto :goto_18

    .line 610
    :cond_3d
    :sswitch_3d
    invoke-direct {p0}, Landroid/util/JsonReader;->checkLenient()V

    .line 611
    iget v0, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v0, v0, -0x1

    iput v0, p0, Landroid/util/JsonReader;->pos:I

    .line 612
    const-string/jumbo v0, "null"

    iput-object v0, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    .line 613
    sget-object v0, Landroid/util/JsonToken;->NULL:Landroid/util/JsonToken;

    iput-object v0, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    goto :goto_18

    .line 600
    :sswitch_data_50
    .sparse-switch
        0x2c -> :sswitch_3d
        0x3b -> :sswitch_3d
        0x5d -> :sswitch_33
    .end sparse-switch

    .line 587
    :sswitch_data_5e
    .sparse-switch
        0x2c -> :sswitch_7
        0x3b -> :sswitch_2f
        0x5d -> :sswitch_27
    .end sparse-switch
.end method

.method private nextInObject(Z)Landroid/util/JsonToken;
    .registers 4
    .param p1, "firstElement"    # Z
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 626
    if-eqz p1, :cond_3d

    .line 628
    invoke-direct {p0}, Landroid/util/JsonReader;->nextNonWhitespace()I

    move-result v1

    packed-switch v1, :pswitch_data_68

    .line 633
    iget v1, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v1, v1, -0x1

    iput v1, p0, Landroid/util/JsonReader;->pos:I

    .line 649
    :sswitch_f
    invoke-direct {p0}, Landroid/util/JsonReader;->nextNonWhitespace()I

    move-result v0

    .line 650
    .local v0, "quote":I
    sparse-switch v0, :sswitch_data_6e

    .line 657
    invoke-direct {p0}, Landroid/util/JsonReader;->checkLenient()V

    .line 658
    iget v1, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v1, v1, -0x1

    iput v1, p0, Landroid/util/JsonReader;->pos:I

    .line 659
    const/4 v1, 0x0

    invoke-direct {p0, v1}, Landroid/util/JsonReader;->nextLiteral(Z)Ljava/lang/String;

    move-result-object v1

    iput-object v1, p0, Landroid/util/JsonReader;->name:Ljava/lang/String;

    .line 660
    iget-object v1, p0, Landroid/util/JsonReader;->name:Ljava/lang/String;

    invoke-virtual {v1}, Ljava/lang/String;->isEmpty()Z

    move-result v1

    if-eqz v1, :cond_5d

    .line 661
    const-string v1, "Expected name"

    invoke-direct {p0, v1}, Landroid/util/JsonReader;->syntaxError(Ljava/lang/String;)Ljava/io/IOException;

    move-result-object v1

    throw v1

    .line 630
    .end local v0    # "quote":I
    :pswitch_35
    invoke-direct {p0}, Landroid/util/JsonReader;->pop()Landroid/util/JsonScope;

    .line 631
    sget-object v1, Landroid/util/JsonToken;->END_OBJECT:Landroid/util/JsonToken;

    iput-object v1, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    .line 666
    :goto_3c
    return-object v1

    .line 636
    :cond_3d
    invoke-direct {p0}, Landroid/util/JsonReader;->nextNonWhitespace()I

    move-result v1

    sparse-switch v1, :sswitch_data_78

    .line 644
    const-string v1, "Unterminated object"

    invoke-direct {p0, v1}, Landroid/util/JsonReader;->syntaxError(Ljava/lang/String;)Ljava/io/IOException;

    move-result-object v1

    throw v1

    .line 638
    :sswitch_4b
    invoke-direct {p0}, Landroid/util/JsonReader;->pop()Landroid/util/JsonScope;

    .line 639
    sget-object v1, Landroid/util/JsonToken;->END_OBJECT:Landroid/util/JsonToken;

    iput-object v1, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    goto :goto_3c

    .line 652
    .restart local v0    # "quote":I
    :sswitch_53
    invoke-direct {p0}, Landroid/util/JsonReader;->checkLenient()V

    .line 654
    :sswitch_56
    int-to-char v1, v0

    invoke-direct {p0, v1}, Landroid/util/JsonReader;->nextString(C)Ljava/lang/String;

    move-result-object v1

    iput-object v1, p0, Landroid/util/JsonReader;->name:Ljava/lang/String;

    .line 665
    :cond_5d
    sget-object v1, Landroid/util/JsonScope;->DANGLING_NAME:Landroid/util/JsonScope;

    invoke-direct {p0, v1}, Landroid/util/JsonReader;->replaceTop(Landroid/util/JsonScope;)V

    .line 666
    sget-object v1, Landroid/util/JsonToken;->NAME:Landroid/util/JsonToken;

    iput-object v1, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    goto :goto_3c

    .line 628
    nop

    :pswitch_data_68
    .packed-switch 0x7d
        :pswitch_35
    .end packed-switch

    .line 650
    :sswitch_data_6e
    .sparse-switch
        0x22 -> :sswitch_56
        0x27 -> :sswitch_53
    .end sparse-switch

    .line 636
    :sswitch_data_78
    .sparse-switch
        0x2c -> :sswitch_f
        0x3b -> :sswitch_f
        0x7d -> :sswitch_4b
    .end sparse-switch
.end method

.method private nextLiteral(Z)Ljava/lang/String;
    .registers 8
    .param p1, "assignOffsetsOnly"    # Z
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    const/4 v5, 0x0

    .line 922
    const/4 v0, 0x0

    .line 923
    .local v0, "builder":Ljava/lang/StringBuilder;
    const/4 v3, -0x1

    iput v3, p0, Landroid/util/JsonReader;->valuePos:I

    .line 924
    iput v5, p0, Landroid/util/JsonReader;->valueLength:I

    .line 925
    const/4 v1, 0x0

    .line 929
    .local v1, "i":I
    :cond_8
    :goto_8
    iget v3, p0, Landroid/util/JsonReader;->pos:I

    add-int/2addr v3, v1

    iget v4, p0, Landroid/util/JsonReader;->limit:I

    if-ge v3, v4, :cond_33

    .line 930
    iget-object v3, p0, Landroid/util/JsonReader;->buffer:[C

    iget v4, p0, Landroid/util/JsonReader;->pos:I

    add-int/2addr v4, v1

    aget-char v3, v3, v4

    sparse-switch v3, :sswitch_data_8a

    .line 929
    add-int/lit8 v1, v1, 0x1

    goto :goto_8

    .line 936
    :sswitch_1c
    invoke-direct {p0}, Landroid/util/JsonReader;->checkLenient()V

    .line 980
    :goto_1f
    :sswitch_1f
    if-eqz p1, :cond_68

    if-nez v0, :cond_68

    .line 981
    iget v3, p0, Landroid/util/JsonReader;->pos:I

    iput v3, p0, Landroid/util/JsonReader;->valuePos:I

    .line 982
    const/4 v2, 0x0

    .line 991
    .local v2, "result":Ljava/lang/String;
    :goto_28
    iget v3, p0, Landroid/util/JsonReader;->valueLength:I

    add-int/2addr v3, v1

    iput v3, p0, Landroid/util/JsonReader;->valueLength:I

    .line 992
    iget v3, p0, Landroid/util/JsonReader;->pos:I

    add-int/2addr v3, v1

    iput v3, p0, Landroid/util/JsonReader;->pos:I

    .line 993
    return-object v2

    .line 957
    .end local v2    # "result":Ljava/lang/String;
    :cond_33
    iget-object v3, p0, Landroid/util/JsonReader;->buffer:[C

    array-length v3, v3

    if-ge v1, v3, :cond_47

    .line 958
    add-int/lit8 v3, v1, 0x1

    invoke-direct {p0, v3}, Landroid/util/JsonReader;->fillBuffer(I)Z

    move-result v3

    if-nez v3, :cond_8

    .line 961
    iget-object v3, p0, Landroid/util/JsonReader;->buffer:[C

    iget v4, p0, Landroid/util/JsonReader;->limit:I

    aput-char v5, v3, v4

    goto :goto_1f

    .line 967
    :cond_47
    if-nez v0, :cond_4e

    .line 968
    new-instance v0, Ljava/lang/StringBuilder;

    .end local v0    # "builder":Ljava/lang/StringBuilder;
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    .line 970
    .restart local v0    # "builder":Ljava/lang/StringBuilder;
    :cond_4e
    iget-object v3, p0, Landroid/util/JsonReader;->buffer:[C

    iget v4, p0, Landroid/util/JsonReader;->pos:I

    invoke-virtual {v0, v3, v4, v1}, Ljava/lang/StringBuilder;->append([CII)Ljava/lang/StringBuilder;

    .line 971
    iget v3, p0, Landroid/util/JsonReader;->valueLength:I

    add-int/2addr v3, v1

    iput v3, p0, Landroid/util/JsonReader;->valueLength:I

    .line 972
    iget v3, p0, Landroid/util/JsonReader;->pos:I

    add-int/2addr v3, v1

    iput v3, p0, Landroid/util/JsonReader;->pos:I

    .line 973
    const/4 v1, 0x0

    .line 974
    const/4 v3, 0x1

    invoke-direct {p0, v3}, Landroid/util/JsonReader;->fillBuffer(I)Z

    move-result v3

    if-nez v3, :cond_8

    goto :goto_1f

    .line 983
    :cond_68
    iget-boolean v3, p0, Landroid/util/JsonReader;->skipping:Z

    if-eqz v3, :cond_70

    .line 984
    const-string/jumbo v2, "skipped!"

    .restart local v2    # "result":Ljava/lang/String;
    goto :goto_28

    .line 985
    .end local v2    # "result":Ljava/lang/String;
    :cond_70
    if-nez v0, :cond_7d

    .line 986
    iget-object v3, p0, Landroid/util/JsonReader;->stringPool:Llibcore/internal/StringPool;

    iget-object v4, p0, Landroid/util/JsonReader;->buffer:[C

    iget v5, p0, Landroid/util/JsonReader;->pos:I

    invoke-virtual {v3, v4, v5, v1}, Llibcore/internal/StringPool;->get([CII)Ljava/lang/String;

    move-result-object v2

    .restart local v2    # "result":Ljava/lang/String;
    goto :goto_28

    .line 988
    .end local v2    # "result":Ljava/lang/String;
    :cond_7d
    iget-object v3, p0, Landroid/util/JsonReader;->buffer:[C

    iget v4, p0, Landroid/util/JsonReader;->pos:I

    invoke-virtual {v0, v3, v4, v1}, Ljava/lang/StringBuilder;->append([CII)Ljava/lang/StringBuilder;

    .line 989
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    .restart local v2    # "result":Ljava/lang/String;
    goto :goto_28

    .line 930
    nop

    :sswitch_data_8a
    .sparse-switch
        0x9 -> :sswitch_1f
        0xa -> :sswitch_1f
        0xc -> :sswitch_1f
        0xd -> :sswitch_1f
        0x20 -> :sswitch_1f
        0x23 -> :sswitch_1c
        0x2c -> :sswitch_1f
        0x2f -> :sswitch_1c
        0x3a -> :sswitch_1f
        0x3b -> :sswitch_1c
        0x3d -> :sswitch_1c
        0x5b -> :sswitch_1f
        0x5c -> :sswitch_1c
        0x5d -> :sswitch_1f
        0x7b -> :sswitch_1f
        0x7d -> :sswitch_1f
    .end sparse-switch
.end method

.method private nextNonWhitespace()I
    .registers 7
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    const/4 v5, 0x1

    .line 779
    :goto_1
    :sswitch_1
    iget v2, p0, Landroid/util/JsonReader;->pos:I

    iget v3, p0, Landroid/util/JsonReader;->limit:I

    if-lt v2, v3, :cond_d

    invoke-direct {p0, v5}, Landroid/util/JsonReader;->fillBuffer(I)Z

    move-result v2

    if-eqz v2, :cond_61

    .line 780
    :cond_d
    iget-object v2, p0, Landroid/util/JsonReader;->buffer:[C

    iget v3, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v4, v3, 0x1

    iput v4, p0, Landroid/util/JsonReader;->pos:I

    aget-char v0, v2, v3

    .line 781
    .local v0, "c":I
    sparse-switch v0, :sswitch_data_6a

    .line 826
    :cond_1a
    :goto_1a
    return v0

    .line 789
    :sswitch_1b
    iget v2, p0, Landroid/util/JsonReader;->pos:I

    iget v3, p0, Landroid/util/JsonReader;->limit:I

    if-ne v2, v3, :cond_27

    invoke-direct {p0, v5}, Landroid/util/JsonReader;->fillBuffer(I)Z

    move-result v2

    if-eqz v2, :cond_1a

    .line 793
    :cond_27
    invoke-direct {p0}, Landroid/util/JsonReader;->checkLenient()V

    .line 794
    iget-object v2, p0, Landroid/util/JsonReader;->buffer:[C

    iget v3, p0, Landroid/util/JsonReader;->pos:I

    aget-char v1, v2, v3

    .line 795
    .local v1, "peek":C
    sparse-switch v1, :sswitch_data_84

    goto :goto_1a

    .line 798
    :sswitch_34
    iget v2, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v2, v2, 0x1

    iput v2, p0, Landroid/util/JsonReader;->pos:I

    .line 799
    const-string v2, "*/"

    invoke-direct {p0, v2}, Landroid/util/JsonReader;->skipTo(Ljava/lang/String;)Z

    move-result v2

    if-nez v2, :cond_49

    .line 800
    const-string v2, "Unterminated comment"

    invoke-direct {p0, v2}, Landroid/util/JsonReader;->syntaxError(Ljava/lang/String;)Ljava/io/IOException;

    move-result-object v2

    throw v2

    .line 802
    :cond_49
    iget v2, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v2, v2, 0x2

    iput v2, p0, Landroid/util/JsonReader;->pos:I

    goto :goto_1

    .line 807
    :sswitch_50
    iget v2, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v2, v2, 0x1

    iput v2, p0, Landroid/util/JsonReader;->pos:I

    .line 808
    invoke-direct {p0}, Landroid/util/JsonReader;->skipToEndOfLine()V

    goto :goto_1

    .line 821
    .end local v1    # "peek":C
    :sswitch_5a
    invoke-direct {p0}, Landroid/util/JsonReader;->checkLenient()V

    .line 822
    invoke-direct {p0}, Landroid/util/JsonReader;->skipToEndOfLine()V

    goto :goto_1

    .line 830
    .end local v0    # "c":I
    :cond_61
    new-instance v2, Ljava/io/EOFException;

    const-string v3, "End of input"

    invoke-direct {v2, v3}, Ljava/io/EOFException;-><init>(Ljava/lang/String;)V

    throw v2

    .line 781
    nop

    :sswitch_data_6a
    .sparse-switch
        0x9 -> :sswitch_1
        0xa -> :sswitch_1
        0xd -> :sswitch_1
        0x20 -> :sswitch_1
        0x23 -> :sswitch_5a
        0x2f -> :sswitch_1b
    .end sparse-switch

    .line 795
    :sswitch_data_84
    .sparse-switch
        0x2a -> :sswitch_34
        0x2f -> :sswitch_50
    .end sparse-switch
.end method

.method private nextString(C)Ljava/lang/String;
    .registers 8
    .param p1, "quote"    # C
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 877
    const/4 v0, 0x0

    .line 880
    .local v0, "builder":Ljava/lang/StringBuilder;
    :cond_1
    iget v2, p0, Landroid/util/JsonReader;->pos:I

    .line 881
    .local v2, "start":I
    :cond_3
    :goto_3
    iget v3, p0, Landroid/util/JsonReader;->pos:I

    iget v4, p0, Landroid/util/JsonReader;->limit:I

    if-ge v3, v4, :cond_5b

    .line 882
    iget-object v3, p0, Landroid/util/JsonReader;->buffer:[C

    iget v4, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v5, v4, 0x1

    iput v5, p0, Landroid/util/JsonReader;->pos:I

    aget-char v1, v3, v4

    .line 884
    .local v1, "c":I
    if-ne v1, p1, :cond_3c

    .line 885
    iget-boolean v3, p0, Landroid/util/JsonReader;->skipping:Z

    if-eqz v3, :cond_1d

    .line 886
    const-string/jumbo v3, "skipped!"

    .line 891
    :goto_1c
    return-object v3

    .line 887
    :cond_1d
    if-nez v0, :cond_2d

    .line 888
    iget-object v3, p0, Landroid/util/JsonReader;->stringPool:Llibcore/internal/StringPool;

    iget-object v4, p0, Landroid/util/JsonReader;->buffer:[C

    iget v5, p0, Landroid/util/JsonReader;->pos:I

    sub-int/2addr v5, v2

    add-int/lit8 v5, v5, -0x1

    invoke-virtual {v3, v4, v2, v5}, Llibcore/internal/StringPool;->get([CII)Ljava/lang/String;

    move-result-object v3

    goto :goto_1c

    .line 890
    :cond_2d
    iget-object v3, p0, Landroid/util/JsonReader;->buffer:[C

    iget v4, p0, Landroid/util/JsonReader;->pos:I

    sub-int/2addr v4, v2

    add-int/lit8 v4, v4, -0x1

    invoke-virtual {v0, v3, v2, v4}, Ljava/lang/StringBuilder;->append([CII)Ljava/lang/StringBuilder;

    .line 891
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    goto :goto_1c

    .line 894
    :cond_3c
    const/16 v3, 0x5c

    if-ne v1, v3, :cond_3

    .line 895
    if-nez v0, :cond_47

    .line 896
    new-instance v0, Ljava/lang/StringBuilder;

    .end local v0    # "builder":Ljava/lang/StringBuilder;
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    .line 898
    .restart local v0    # "builder":Ljava/lang/StringBuilder;
    :cond_47
    iget-object v3, p0, Landroid/util/JsonReader;->buffer:[C

    iget v4, p0, Landroid/util/JsonReader;->pos:I

    sub-int/2addr v4, v2

    add-int/lit8 v4, v4, -0x1

    invoke-virtual {v0, v3, v2, v4}, Ljava/lang/StringBuilder;->append([CII)Ljava/lang/StringBuilder;

    .line 899
    invoke-direct {p0}, Landroid/util/JsonReader;->readEscapeCharacter()C

    move-result v3

    invoke-virtual {v0, v3}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    .line 900
    iget v2, p0, Landroid/util/JsonReader;->pos:I

    goto :goto_3

    .line 904
    .end local v1    # "c":I
    :cond_5b
    if-nez v0, :cond_62

    .line 905
    new-instance v0, Ljava/lang/StringBuilder;

    .end local v0    # "builder":Ljava/lang/StringBuilder;
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    .line 907
    .restart local v0    # "builder":Ljava/lang/StringBuilder;
    :cond_62
    iget-object v3, p0, Landroid/util/JsonReader;->buffer:[C

    iget v4, p0, Landroid/util/JsonReader;->pos:I

    sub-int/2addr v4, v2

    invoke-virtual {v0, v3, v2, v4}, Ljava/lang/StringBuilder;->append([CII)Ljava/lang/StringBuilder;

    .line 908
    const/4 v3, 0x1

    invoke-direct {p0, v3}, Landroid/util/JsonReader;->fillBuffer(I)Z

    move-result v3

    if-nez v3, :cond_1

    .line 910
    const-string v3, "Unterminated string"

    invoke-direct {p0, v3}, Landroid/util/JsonReader;->syntaxError(Ljava/lang/String;)Ljava/io/IOException;

    move-result-object v3

    throw v3
.end method

.method private nextValue()Landroid/util/JsonToken;
    .registers 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 692
    invoke-direct {p0}, Landroid/util/JsonReader;->nextNonWhitespace()I

    move-result v0

    .line 693
    .local v0, "c":I
    sparse-switch v0, :sswitch_data_36

    .line 709
    iget v1, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v1, v1, -0x1

    iput v1, p0, Landroid/util/JsonReader;->pos:I

    .line 710
    invoke-direct {p0}, Landroid/util/JsonReader;->readLiteral()Landroid/util/JsonToken;

    move-result-object v1

    :goto_11
    return-object v1

    .line 695
    :sswitch_12
    sget-object v1, Landroid/util/JsonScope;->EMPTY_OBJECT:Landroid/util/JsonScope;

    invoke-direct {p0, v1}, Landroid/util/JsonReader;->push(Landroid/util/JsonScope;)V

    .line 696
    sget-object v1, Landroid/util/JsonToken;->BEGIN_OBJECT:Landroid/util/JsonToken;

    iput-object v1, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    goto :goto_11

    .line 699
    :sswitch_1c
    sget-object v1, Landroid/util/JsonScope;->EMPTY_ARRAY:Landroid/util/JsonScope;

    invoke-direct {p0, v1}, Landroid/util/JsonReader;->push(Landroid/util/JsonScope;)V

    .line 700
    sget-object v1, Landroid/util/JsonToken;->BEGIN_ARRAY:Landroid/util/JsonToken;

    iput-object v1, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    goto :goto_11

    .line 703
    :sswitch_26
    invoke-direct {p0}, Landroid/util/JsonReader;->checkLenient()V

    .line 705
    :sswitch_29
    int-to-char v1, v0

    invoke-direct {p0, v1}, Landroid/util/JsonReader;->nextString(C)Ljava/lang/String;

    move-result-object v1

    iput-object v1, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    .line 706
    sget-object v1, Landroid/util/JsonToken;->STRING:Landroid/util/JsonToken;

    iput-object v1, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    goto :goto_11

    .line 693
    nop

    :sswitch_data_36
    .sparse-switch
        0x22 -> :sswitch_29
        0x27 -> :sswitch_26
        0x5b -> :sswitch_1c
        0x7b -> :sswitch_12
    .end sparse-switch
.end method

.method private objectValue()Landroid/util/JsonToken;
    .registers 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 674
    invoke-direct {p0}, Landroid/util/JsonReader;->nextNonWhitespace()I

    move-result v0

    packed-switch v0, :pswitch_data_38

    .line 684
    :pswitch_7
    const-string v0, "Expected \':\'"

    invoke-direct {p0, v0}, Landroid/util/JsonReader;->syntaxError(Ljava/lang/String;)Ljava/io/IOException;

    move-result-object v0

    throw v0

    .line 678
    :pswitch_e
    invoke-direct {p0}, Landroid/util/JsonReader;->checkLenient()V

    .line 679
    iget v0, p0, Landroid/util/JsonReader;->pos:I

    iget v1, p0, Landroid/util/JsonReader;->limit:I

    if-lt v0, v1, :cond_1e

    const/4 v0, 0x1

    invoke-direct {p0, v0}, Landroid/util/JsonReader;->fillBuffer(I)Z

    move-result v0

    if-eqz v0, :cond_2e

    :cond_1e
    iget-object v0, p0, Landroid/util/JsonReader;->buffer:[C

    iget v1, p0, Landroid/util/JsonReader;->pos:I

    aget-char v0, v0, v1

    const/16 v1, 0x3e

    if-ne v0, v1, :cond_2e

    .line 680
    iget v0, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v0, v0, 0x1

    iput v0, p0, Landroid/util/JsonReader;->pos:I

    .line 687
    :cond_2e
    :pswitch_2e
    sget-object v0, Landroid/util/JsonScope;->NONEMPTY_OBJECT:Landroid/util/JsonScope;

    invoke-direct {p0, v0}, Landroid/util/JsonReader;->replaceTop(Landroid/util/JsonScope;)V

    .line 688
    invoke-direct {p0}, Landroid/util/JsonReader;->nextValue()Landroid/util/JsonToken;

    move-result-object v0

    return-object v0

    .line 674
    :pswitch_data_38
    .packed-switch 0x3a
        :pswitch_2e
        :pswitch_7
        :pswitch_7
        :pswitch_e
    .end packed-switch
.end method

.method private peekStack()Landroid/util/JsonScope;
    .registers 3

    .prologue
    .line 564
    iget-object v0, p0, Landroid/util/JsonReader;->stack:Ljava/util/List;

    iget-object v1, p0, Landroid/util/JsonReader;->stack:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    add-int/lit8 v1, v1, -0x1

    invoke-interface {v0, v1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/util/JsonScope;

    return-object v0
.end method

.method private pop()Landroid/util/JsonScope;
    .registers 3

    .prologue
    .line 568
    iget-object v0, p0, Landroid/util/JsonReader;->stack:Ljava/util/List;

    iget-object v1, p0, Landroid/util/JsonReader;->stack:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    add-int/lit8 v1, v1, -0x1

    invoke-interface {v0, v1}, Ljava/util/List;->remove(I)Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Landroid/util/JsonScope;

    return-object v0
.end method

.method private push(Landroid/util/JsonScope;)V
    .registers 3
    .param p1, "newTop"    # Landroid/util/JsonScope;

    .prologue
    .line 572
    iget-object v0, p0, Landroid/util/JsonReader;->stack:Ljava/util/List;

    invoke-interface {v0, p1}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 573
    return-void
.end method

.method private readEscapeCharacter()C
    .registers 7
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    const/4 v5, 0x4

    .line 1010
    iget v2, p0, Landroid/util/JsonReader;->pos:I

    iget v3, p0, Landroid/util/JsonReader;->limit:I

    if-ne v2, v3, :cond_15

    const/4 v2, 0x1

    invoke-direct {p0, v2}, Landroid/util/JsonReader;->fillBuffer(I)Z

    move-result v2

    if-nez v2, :cond_15

    .line 1011
    const-string v2, "Unterminated escape sequence"

    invoke-direct {p0, v2}, Landroid/util/JsonReader;->syntaxError(Ljava/lang/String;)Ljava/io/IOException;

    move-result-object v2

    throw v2

    .line 1014
    :cond_15
    iget-object v2, p0, Landroid/util/JsonReader;->buffer:[C

    iget v3, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v4, v3, 0x1

    iput v4, p0, Landroid/util/JsonReader;->pos:I

    aget-char v0, v2, v3

    .line 1015
    .local v0, "escaped":C
    sparse-switch v0, :sswitch_data_60

    .line 1043
    .end local v0    # "escaped":C
    :goto_22
    return v0

    .line 1017
    .restart local v0    # "escaped":C
    :sswitch_23
    iget v2, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v2, v2, 0x4

    iget v3, p0, Landroid/util/JsonReader;->limit:I

    if-le v2, v3, :cond_38

    invoke-direct {p0, v5}, Landroid/util/JsonReader;->fillBuffer(I)Z

    move-result v2

    if-nez v2, :cond_38

    .line 1018
    const-string v2, "Unterminated escape sequence"

    invoke-direct {p0, v2}, Landroid/util/JsonReader;->syntaxError(Ljava/lang/String;)Ljava/io/IOException;

    move-result-object v2

    throw v2

    .line 1020
    :cond_38
    iget-object v2, p0, Landroid/util/JsonReader;->stringPool:Llibcore/internal/StringPool;

    iget-object v3, p0, Landroid/util/JsonReader;->buffer:[C

    iget v4, p0, Landroid/util/JsonReader;->pos:I

    invoke-virtual {v2, v3, v4, v5}, Llibcore/internal/StringPool;->get([CII)Ljava/lang/String;

    move-result-object v1

    .line 1021
    .local v1, "hex":Ljava/lang/String;
    iget v2, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v2, v2, 0x4

    iput v2, p0, Landroid/util/JsonReader;->pos:I

    .line 1022
    const/16 v2, 0x10

    invoke-static {v1, v2}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;I)I

    move-result v2

    int-to-char v0, v2

    goto :goto_22

    .line 1025
    .end local v1    # "hex":Ljava/lang/String;
    :sswitch_50
    const/16 v0, 0x9

    goto :goto_22

    .line 1028
    :sswitch_53
    const/16 v0, 0x8

    goto :goto_22

    .line 1031
    :sswitch_56
    const/16 v0, 0xa

    goto :goto_22

    .line 1034
    :sswitch_59
    const/16 v0, 0xd

    goto :goto_22

    .line 1037
    :sswitch_5c
    const/16 v0, 0xc

    goto :goto_22

    .line 1015
    nop

    :sswitch_data_60
    .sparse-switch
        0x62 -> :sswitch_53
        0x66 -> :sswitch_5c
        0x6e -> :sswitch_56
        0x72 -> :sswitch_59
        0x74 -> :sswitch_50
        0x75 -> :sswitch_23
    .end sparse-switch
.end method

.method private readLiteral()Landroid/util/JsonToken;
    .registers 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 1051
    const/4 v0, 0x1

    invoke-direct {p0, v0}, Landroid/util/JsonReader;->nextLiteral(Z)Ljava/lang/String;

    move-result-object v0

    iput-object v0, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    .line 1052
    iget v0, p0, Landroid/util/JsonReader;->valueLength:I

    if-nez v0, :cond_12

    .line 1053
    const-string v0, "Expected literal value"

    invoke-direct {p0, v0}, Landroid/util/JsonReader;->syntaxError(Ljava/lang/String;)Ljava/io/IOException;

    move-result-object v0

    throw v0

    .line 1055
    :cond_12
    invoke-direct {p0}, Landroid/util/JsonReader;->decodeLiteral()Landroid/util/JsonToken;

    move-result-object v0

    iput-object v0, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    .line 1056
    iget-object v0, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v1, Landroid/util/JsonToken;->STRING:Landroid/util/JsonToken;

    if-ne v0, v1, :cond_21

    .line 1057
    invoke-direct {p0}, Landroid/util/JsonReader;->checkLenient()V

    .line 1059
    :cond_21
    iget-object v0, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    return-object v0
.end method

.method private replaceTop(Landroid/util/JsonScope;)V
    .registers 4
    .param p1, "newTop"    # Landroid/util/JsonScope;

    .prologue
    .line 579
    iget-object v0, p0, Landroid/util/JsonReader;->stack:Ljava/util/List;

    iget-object v1, p0, Landroid/util/JsonReader;->stack:Ljava/util/List;

    invoke-interface {v1}, Ljava/util/List;->size()I

    move-result v1

    add-int/lit8 v1, v1, -0x1

    invoke-interface {v0, v1, p1}, Ljava/util/List;->set(ILjava/lang/Object;)Ljava/lang/Object;

    .line 580
    return-void
.end method

.method private skipTo(Ljava/lang/String;)Z
    .registers 5
    .param p1, "toFind"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 855
    :goto_0
    iget v1, p0, Landroid/util/JsonReader;->pos:I

    invoke-virtual {p1}, Ljava/lang/String;->length()I

    move-result v2

    add-int/2addr v1, v2

    iget v2, p0, Landroid/util/JsonReader;->limit:I

    if-le v1, v2, :cond_15

    invoke-virtual {p1}, Ljava/lang/String;->length()I

    move-result v1

    invoke-direct {p0, v1}, Landroid/util/JsonReader;->fillBuffer(I)Z

    move-result v1

    if-eqz v1, :cond_35

    .line 856
    :cond_15
    const/4 v0, 0x0

    .local v0, "c":I
    :goto_16
    invoke-virtual {p1}, Ljava/lang/String;->length()I

    move-result v1

    if-ge v0, v1, :cond_33

    .line 857
    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->pos:I

    add-int/2addr v2, v0

    aget-char v1, v1, v2

    invoke-virtual {p1, v0}, Ljava/lang/String;->charAt(I)C

    move-result v2

    if-eq v1, v2, :cond_30

    .line 855
    iget v1, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v1, v1, 0x1

    iput v1, p0, Landroid/util/JsonReader;->pos:I

    goto :goto_0

    .line 856
    :cond_30
    add-int/lit8 v0, v0, 0x1

    goto :goto_16

    .line 861
    :cond_33
    const/4 v1, 0x1

    .line 863
    .end local v0    # "c":I
    :goto_34
    return v1

    :cond_35
    const/4 v1, 0x0

    goto :goto_34
.end method

.method private skipToEndOfLine()V
    .registers 5
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 845
    :cond_0
    iget v1, p0, Landroid/util/JsonReader;->pos:I

    iget v2, p0, Landroid/util/JsonReader;->limit:I

    if-lt v1, v2, :cond_d

    const/4 v1, 0x1

    invoke-direct {p0, v1}, Landroid/util/JsonReader;->fillBuffer(I)Z

    move-result v1

    if-eqz v1, :cond_1f

    .line 846
    :cond_d
    iget-object v1, p0, Landroid/util/JsonReader;->buffer:[C

    iget v2, p0, Landroid/util/JsonReader;->pos:I

    add-int/lit8 v3, v2, 0x1

    iput v3, p0, Landroid/util/JsonReader;->pos:I

    aget-char v0, v1, v2

    .line 847
    .local v0, "c":C
    const/16 v1, 0xd

    if-eq v0, v1, :cond_1f

    const/16 v1, 0xa

    if-ne v0, v1, :cond_0

    .line 851
    .end local v0    # "c":C
    :cond_1f
    return-void
.end method

.method private syntaxError(Ljava/lang/String;)Ljava/io/IOException;
    .registers 5
    .param p1, "message"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 1156
    new-instance v0, Landroid/util/MalformedJsonException;

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v1, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, " at line "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-direct {p0}, Landroid/util/JsonReader;->getLineNumber()I

    move-result v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    const-string v2, " column "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-direct {p0}, Landroid/util/JsonReader;->getColumnNumber()I

    move-result v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Landroid/util/MalformedJsonException;-><init>(Ljava/lang/String;)V

    throw v0
.end method


# virtual methods
.method public beginArray()V
    .registers 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 277
    sget-object v0, Landroid/util/JsonToken;->BEGIN_ARRAY:Landroid/util/JsonToken;

    invoke-direct {p0, v0}, Landroid/util/JsonReader;->expect(Landroid/util/JsonToken;)V

    .line 278
    return-void
.end method

.method public beginObject()V
    .registers 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 293
    sget-object v0, Landroid/util/JsonToken;->BEGIN_OBJECT:Landroid/util/JsonToken;

    invoke-direct {p0, v0}, Landroid/util/JsonReader;->expect(Landroid/util/JsonToken;)V

    .line 294
    return-void
.end method

.method public close()V
    .registers 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    const/4 v0, 0x0

    .line 534
    iput-object v0, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    .line 535
    iput-object v0, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    .line 536
    iget-object v0, p0, Landroid/util/JsonReader;->stack:Ljava/util/List;

    invoke-interface {v0}, Ljava/util/List;->clear()V

    .line 537
    iget-object v0, p0, Landroid/util/JsonReader;->stack:Ljava/util/List;

    sget-object v1, Landroid/util/JsonScope;->CLOSED:Landroid/util/JsonScope;

    invoke-interface {v0, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    .line 538
    iget-object v0, p0, Landroid/util/JsonReader;->in:Ljava/io/Reader;

    invoke-virtual {v0}, Ljava/io/Reader;->close()V

    .line 539
    return-void
.end method

.method public endArray()V
    .registers 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 285
    sget-object v0, Landroid/util/JsonToken;->END_ARRAY:Landroid/util/JsonToken;

    invoke-direct {p0, v0}, Landroid/util/JsonReader;->expect(Landroid/util/JsonToken;)V

    .line 286
    return-void
.end method

.method public endObject()V
    .registers 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 301
    sget-object v0, Landroid/util/JsonToken;->END_OBJECT:Landroid/util/JsonToken;

    invoke-direct {p0, v0}, Landroid/util/JsonReader;->expect(Landroid/util/JsonToken;)V

    .line 302
    return-void
.end method

.method public hasNext()Z
    .registers 3
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 319
    invoke-virtual {p0}, Landroid/util/JsonReader;->peek()Landroid/util/JsonToken;

    .line 320
    iget-object v0, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v1, Landroid/util/JsonToken;->END_OBJECT:Landroid/util/JsonToken;

    if-eq v0, v1, :cond_11

    iget-object v0, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v1, Landroid/util/JsonToken;->END_ARRAY:Landroid/util/JsonToken;

    if-eq v0, v1, :cond_11

    const/4 v0, 0x1

    :goto_10
    return v0

    :cond_11
    const/4 v0, 0x0

    goto :goto_10
.end method

.method public isLenient()Z
    .registers 2

    .prologue
    .line 269
    iget-boolean v0, p0, Landroid/util/JsonReader;->lenient:Z

    return v0
.end method

.method public nextBoolean()Z
    .registers 5
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 424
    invoke-virtual {p0}, Landroid/util/JsonReader;->peek()Landroid/util/JsonToken;

    .line 425
    iget-object v1, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v2, Landroid/util/JsonToken;->BOOLEAN:Landroid/util/JsonToken;

    if-eq v1, v2, :cond_24

    .line 426
    new-instance v1, Ljava/lang/IllegalStateException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "Expected a boolean but was "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    iget-object v3, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    throw v1

    .line 429
    :cond_24
    iget-object v1, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    const-string/jumbo v2, "true"

    if-ne v1, v2, :cond_30

    const/4 v0, 0x1

    .line 430
    .local v0, "result":Z
    :goto_2c
    invoke-direct {p0}, Landroid/util/JsonReader;->advance()Landroid/util/JsonToken;

    .line 431
    return v0

    .line 429
    .end local v0    # "result":Z
    :cond_30
    const/4 v0, 0x0

    goto :goto_2c
.end method

.method public nextDouble()D
    .registers 6
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 458
    invoke-virtual {p0}, Landroid/util/JsonReader;->peek()Landroid/util/JsonToken;

    .line 459
    iget-object v2, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v3, Landroid/util/JsonToken;->STRING:Landroid/util/JsonToken;

    if-eq v2, v3, :cond_2a

    iget-object v2, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v3, Landroid/util/JsonToken;->NUMBER:Landroid/util/JsonToken;

    if-eq v2, v3, :cond_2a

    .line 460
    new-instance v2, Ljava/lang/IllegalStateException;

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string v4, "Expected a double but was "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    iget-object v4, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-direct {v2, v3}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    throw v2

    .line 463
    :cond_2a
    iget-object v2, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    invoke-static {v2}, Ljava/lang/Double;->parseDouble(Ljava/lang/String;)D

    move-result-wide v0

    .line 464
    .local v0, "result":D
    invoke-direct {p0}, Landroid/util/JsonReader;->advance()Landroid/util/JsonToken;

    .line 465
    return-wide v0
.end method

.method public nextInt()I
    .registers 8
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 510
    invoke-virtual {p0}, Landroid/util/JsonReader;->peek()Landroid/util/JsonToken;

    .line 511
    iget-object v4, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v5, Landroid/util/JsonToken;->STRING:Landroid/util/JsonToken;

    if-eq v4, v5, :cond_2a

    iget-object v4, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v5, Landroid/util/JsonToken;->NUMBER:Landroid/util/JsonToken;

    if-eq v4, v5, :cond_2a

    .line 512
    new-instance v4, Ljava/lang/IllegalStateException;

    new-instance v5, Ljava/lang/StringBuilder;

    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    const-string v6, "Expected an int but was "

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v5

    iget-object v6, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v5

    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    invoke-direct {v4, v5}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    throw v4

    .line 517
    :cond_2a
    :try_start_2a
    iget-object v4, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    invoke-static {v4}, Ljava/lang/Integer;->parseInt(Ljava/lang/String;)I
    :try_end_2f
    .catch Ljava/lang/NumberFormatException; {:try_start_2a .. :try_end_2f} :catch_34

    move-result v3

    .line 526
    .local v3, "result":I
    :cond_30
    invoke-direct {p0}, Landroid/util/JsonReader;->advance()Landroid/util/JsonToken;

    .line 527
    return v3

    .line 518
    .end local v3    # "result":I
    :catch_34
    move-exception v2

    .line 519
    .local v2, "ignored":Ljava/lang/NumberFormatException;
    iget-object v4, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    invoke-static {v4}, Ljava/lang/Double;->parseDouble(Ljava/lang/String;)D

    move-result-wide v0

    .line 520
    .local v0, "asDouble":D
    double-to-int v3, v0

    .line 521
    .restart local v3    # "result":I
    int-to-double v4, v3

    cmpl-double v4, v4, v0

    if-eqz v4, :cond_30

    .line 522
    new-instance v4, Ljava/lang/NumberFormatException;

    iget-object v5, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    invoke-direct {v4, v5}, Ljava/lang/NumberFormatException;-><init>(Ljava/lang/String;)V

    throw v4
.end method

.method public nextLong()J
    .registers 9
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 479
    invoke-virtual {p0}, Landroid/util/JsonReader;->peek()Landroid/util/JsonToken;

    .line 480
    iget-object v5, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v6, Landroid/util/JsonToken;->STRING:Landroid/util/JsonToken;

    if-eq v5, v6, :cond_2a

    iget-object v5, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v6, Landroid/util/JsonToken;->NUMBER:Landroid/util/JsonToken;

    if-eq v5, v6, :cond_2a

    .line 481
    new-instance v5, Ljava/lang/IllegalStateException;

    new-instance v6, Ljava/lang/StringBuilder;

    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    const-string v7, "Expected a long but was "

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v6

    iget-object v7, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v6

    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v6

    invoke-direct {v5, v6}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    throw v5

    .line 486
    :cond_2a
    :try_start_2a
    iget-object v5, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    invoke-static {v5}, Ljava/lang/Long;->parseLong(Ljava/lang/String;)J
    :try_end_2f
    .catch Ljava/lang/NumberFormatException; {:try_start_2a .. :try_end_2f} :catch_34

    move-result-wide v3

    .line 495
    .local v3, "result":J
    :cond_30
    invoke-direct {p0}, Landroid/util/JsonReader;->advance()Landroid/util/JsonToken;

    .line 496
    return-wide v3

    .line 487
    .end local v3    # "result":J
    :catch_34
    move-exception v2

    .line 488
    .local v2, "ignored":Ljava/lang/NumberFormatException;
    iget-object v5, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    invoke-static {v5}, Ljava/lang/Double;->parseDouble(Ljava/lang/String;)D

    move-result-wide v0

    .line 489
    .local v0, "asDouble":D
    double-to-long v3, v0

    .line 490
    .restart local v3    # "result":J
    long-to-double v5, v3

    cmpl-double v5, v5, v0

    if-eqz v5, :cond_30

    .line 491
    new-instance v5, Ljava/lang/NumberFormatException;

    iget-object v6, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    invoke-direct {v5, v6}, Ljava/lang/NumberFormatException;-><init>(Ljava/lang/String;)V

    throw v5
.end method

.method public nextName()Ljava/lang/String;
    .registers 5
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 388
    invoke-virtual {p0}, Landroid/util/JsonReader;->peek()Landroid/util/JsonToken;

    .line 389
    iget-object v1, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v2, Landroid/util/JsonToken;->NAME:Landroid/util/JsonToken;

    if-eq v1, v2, :cond_26

    .line 390
    new-instance v1, Ljava/lang/IllegalStateException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "Expected a name but was "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {p0}, Landroid/util/JsonReader;->peek()Landroid/util/JsonToken;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    throw v1

    .line 392
    :cond_26
    iget-object v0, p0, Landroid/util/JsonReader;->name:Ljava/lang/String;

    .line 393
    .local v0, "result":Ljava/lang/String;
    invoke-direct {p0}, Landroid/util/JsonReader;->advance()Landroid/util/JsonToken;

    .line 394
    return-object v0
.end method

.method public nextNull()V
    .registers 4
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 442
    invoke-virtual {p0}, Landroid/util/JsonReader;->peek()Landroid/util/JsonToken;

    .line 443
    iget-object v0, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v1, Landroid/util/JsonToken;->NULL:Landroid/util/JsonToken;

    if-eq v0, v1, :cond_24

    .line 444
    new-instance v0, Ljava/lang/IllegalStateException;

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    const-string v2, "Expected null but was "

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    iget-object v2, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    throw v0

    .line 447
    :cond_24
    invoke-direct {p0}, Landroid/util/JsonReader;->advance()Landroid/util/JsonToken;

    .line 448
    return-void
.end method

.method public nextString()Ljava/lang/String;
    .registers 5
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 406
    invoke-virtual {p0}, Landroid/util/JsonReader;->peek()Landroid/util/JsonToken;

    .line 407
    iget-object v1, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v2, Landroid/util/JsonToken;->STRING:Landroid/util/JsonToken;

    if-eq v1, v2, :cond_2c

    iget-object v1, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v2, Landroid/util/JsonToken;->NUMBER:Landroid/util/JsonToken;

    if-eq v1, v2, :cond_2c

    .line 408
    new-instance v1, Ljava/lang/IllegalStateException;

    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    const-string v3, "Expected a string but was "

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {p0}, Landroid/util/JsonReader;->peek()Landroid/util/JsonToken;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v2

    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    throw v1

    .line 411
    :cond_2c
    iget-object v0, p0, Landroid/util/JsonReader;->value:Ljava/lang/String;

    .line 412
    .local v0, "result":Ljava/lang/String;
    invoke-direct {p0}, Landroid/util/JsonReader;->advance()Landroid/util/JsonToken;

    .line 413
    return-object v0
.end method

.method public peek()Landroid/util/JsonToken;
    .registers 8
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    const/4 v6, 0x1

    const/4 v5, 0x0

    .line 327
    iget-object v3, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    if-eqz v3, :cond_9

    .line 328
    iget-object v1, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    .line 358
    :cond_8
    :goto_8
    return-object v1

    .line 331
    :cond_9
    sget-object v3, Landroid/util/JsonReader$1;->$SwitchMap$android$util$JsonScope:[I

    invoke-direct {p0}, Landroid/util/JsonReader;->peekStack()Landroid/util/JsonScope;

    move-result-object v4

    invoke-virtual {v4}, Landroid/util/JsonScope;->ordinal()I

    move-result v4

    aget v3, v3, v4

    packed-switch v3, :pswitch_data_8a

    .line 363
    new-instance v3, Ljava/lang/AssertionError;

    invoke-direct {v3}, Ljava/lang/AssertionError;-><init>()V

    throw v3

    .line 333
    :pswitch_1e
    sget-object v3, Landroid/util/JsonScope;->NONEMPTY_DOCUMENT:Landroid/util/JsonScope;

    invoke-direct {p0, v3}, Landroid/util/JsonReader;->replaceTop(Landroid/util/JsonScope;)V

    .line 334
    invoke-direct {p0}, Landroid/util/JsonReader;->nextValue()Landroid/util/JsonToken;

    move-result-object v1

    .line 335
    .local v1, "firstToken":Landroid/util/JsonToken;
    iget-boolean v3, p0, Landroid/util/JsonReader;->lenient:Z

    if-nez v3, :cond_8

    iget-object v3, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v4, Landroid/util/JsonToken;->BEGIN_ARRAY:Landroid/util/JsonToken;

    if-eq v3, v4, :cond_8

    iget-object v3, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    sget-object v4, Landroid/util/JsonToken;->BEGIN_OBJECT:Landroid/util/JsonToken;

    if-eq v3, v4, :cond_8

    .line 336
    new-instance v3, Ljava/io/IOException;

    new-instance v4, Ljava/lang/StringBuilder;

    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    const-string v5, "Expected JSON document to start with \'[\' or \'{\' but was "

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v4

    iget-object v5, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-direct {v3, v4}, Ljava/io/IOException;-><init>(Ljava/lang/String;)V

    throw v3

    .line 341
    .end local v1    # "firstToken":Landroid/util/JsonToken;
    :pswitch_52
    invoke-direct {p0, v6}, Landroid/util/JsonReader;->nextInArray(Z)Landroid/util/JsonToken;

    move-result-object v1

    goto :goto_8

    .line 343
    :pswitch_57
    invoke-direct {p0, v5}, Landroid/util/JsonReader;->nextInArray(Z)Landroid/util/JsonToken;

    move-result-object v1

    goto :goto_8

    .line 345
    :pswitch_5c
    invoke-direct {p0, v6}, Landroid/util/JsonReader;->nextInObject(Z)Landroid/util/JsonToken;

    move-result-object v1

    goto :goto_8

    .line 347
    :pswitch_61
    invoke-direct {p0}, Landroid/util/JsonReader;->objectValue()Landroid/util/JsonToken;

    move-result-object v1

    goto :goto_8

    .line 349
    :pswitch_66
    invoke-direct {p0, v5}, Landroid/util/JsonReader;->nextInObject(Z)Landroid/util/JsonToken;

    move-result-object v1

    goto :goto_8

    .line 352
    :pswitch_6b
    :try_start_6b
    invoke-direct {p0}, Landroid/util/JsonReader;->nextValue()Landroid/util/JsonToken;

    move-result-object v2

    .line 353
    .local v2, "token":Landroid/util/JsonToken;
    iget-boolean v3, p0, Landroid/util/JsonReader;->lenient:Z

    if-eqz v3, :cond_75

    move-object v1, v2

    .line 354
    goto :goto_8

    .line 356
    :cond_75
    const-string v3, "Expected EOF"

    invoke-direct {p0, v3}, Landroid/util/JsonReader;->syntaxError(Ljava/lang/String;)Ljava/io/IOException;

    move-result-object v3

    throw v3
    :try_end_7c
    .catch Ljava/io/EOFException; {:try_start_6b .. :try_end_7c} :catch_7c

    .line 357
    .end local v2    # "token":Landroid/util/JsonToken;
    :catch_7c
    move-exception v0

    .line 358
    .local v0, "e":Ljava/io/EOFException;
    sget-object v1, Landroid/util/JsonToken;->END_DOCUMENT:Landroid/util/JsonToken;

    iput-object v1, p0, Landroid/util/JsonReader;->token:Landroid/util/JsonToken;

    goto :goto_8

    .line 361
    .end local v0    # "e":Ljava/io/EOFException;
    :pswitch_82
    new-instance v3, Ljava/lang/IllegalStateException;

    const-string v4, "JsonReader is closed"

    invoke-direct {v3, v4}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    throw v3

    .line 331
    :pswitch_data_8a
    .packed-switch 0x1
        :pswitch_1e
        :pswitch_52
        :pswitch_57
        :pswitch_5c
        :pswitch_61
        :pswitch_66
        :pswitch_6b
        :pswitch_82
    .end packed-switch
.end method

.method public setLenient(Z)V
    .registers 2
    .param p1, "lenient"    # Z

    .prologue
    .line 262
    iput-boolean p1, p0, Landroid/util/JsonReader;->lenient:Z

    .line 263
    return-void
.end method

.method public skipValue()V
    .registers 5
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    const/4 v3, 0x0

    .line 547
    const/4 v2, 0x1

    iput-boolean v2, p0, Landroid/util/JsonReader;->skipping:Z

    .line 549
    const/4 v0, 0x0

    .line 551
    .local v0, "count":I
    :cond_5
    :try_start_5
    invoke-direct {p0}, Landroid/util/JsonReader;->advance()Landroid/util/JsonToken;

    move-result-object v1

    .line 552
    .local v1, "token":Landroid/util/JsonToken;
    sget-object v2, Landroid/util/JsonToken;->BEGIN_ARRAY:Landroid/util/JsonToken;

    if-eq v1, v2, :cond_11

    sget-object v2, Landroid/util/JsonToken;->BEGIN_OBJECT:Landroid/util/JsonToken;
    :try_end_f
    .catchall {:try_start_5 .. :try_end_f} :catchall_23

    if-ne v1, v2, :cond_18

    .line 553
    :cond_11
    add-int/lit8 v0, v0, 0x1

    .line 557
    :cond_13
    :goto_13
    if-nez v0, :cond_5

    .line 559
    iput-boolean v3, p0, Landroid/util/JsonReader;->skipping:Z

    .line 561
    return-void

    .line 554
    :cond_18
    :try_start_18
    sget-object v2, Landroid/util/JsonToken;->END_ARRAY:Landroid/util/JsonToken;

    if-eq v1, v2, :cond_20

    sget-object v2, Landroid/util/JsonToken;->END_OBJECT:Landroid/util/JsonToken;
    :try_end_1e
    .catchall {:try_start_18 .. :try_end_1e} :catchall_23

    if-ne v1, v2, :cond_13

    .line 555
    :cond_20
    add-int/lit8 v0, v0, -0x1

    goto :goto_13

    .line 559
    .end local v1    # "token":Landroid/util/JsonToken;
    :catchall_23
    move-exception v2

    iput-boolean v3, p0, Landroid/util/JsonReader;->skipping:Z

    throw v2
.end method

.method public toString()Ljava/lang/String;
    .registers 3

    .prologue
    .line 997
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    const-string v1, " near "

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-direct {p0}, Landroid/util/JsonReader;->getSnippet()Ljava/lang/CharSequence;

    move-result-object v1

    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    move-result-object v0

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v0

    return-object v0
.end method
