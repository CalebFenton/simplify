.class public Landroid/support/v4/util/LogWriter;
.super Ljava/io/Writer;
.source "LogWriter.java"


# instance fields
.field private mBuilder:Ljava/lang/StringBuilder;

.field private final mTag:Ljava/lang/String;


# direct methods
.method public constructor <init>(Ljava/lang/String;)V
    .locals 2
    .param p1, "tag"    # Ljava/lang/String;

    #@0
    .prologue
    .line 39
    invoke-direct {p0}, Ljava/io/Writer;-><init>()V

    #@3
    .line 31
    new-instance v0, Ljava/lang/StringBuilder;

    #@5
    const/16 v1, 0x80

    #@7
    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(I)V

    #@a
    iput-object v0, p0, Landroid/support/v4/util/LogWriter;->mBuilder:Ljava/lang/StringBuilder;

    #@c
    .line 40
    iput-object p1, p0, Landroid/support/v4/util/LogWriter;->mTag:Ljava/lang/String;

    #@e
    .line 41
    return-void
.end method

.method private flushBuilder()V
    .locals 3

    #@0
    .prologue
    .line 64
    iget-object v0, p0, Landroid/support/v4/util/LogWriter;->mBuilder:Ljava/lang/StringBuilder;

    #@2
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->length()I

    #@5
    move-result v0

    #@6
    if-lez v0, :cond_0

    #@8
    .line 65
    iget-object v0, p0, Landroid/support/v4/util/LogWriter;->mTag:Ljava/lang/String;

    #@a
    iget-object v1, p0, Landroid/support/v4/util/LogWriter;->mBuilder:Ljava/lang/StringBuilder;

    #@c
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@f
    move-result-object v1

    #@10
    invoke-static {v0, v1}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    #@13
    .line 66
    iget-object v0, p0, Landroid/support/v4/util/LogWriter;->mBuilder:Ljava/lang/StringBuilder;

    #@15
    const/4 v1, 0x0

    #@16
    iget-object v2, p0, Landroid/support/v4/util/LogWriter;->mBuilder:Ljava/lang/StringBuilder;

    #@18
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->length()I

    #@1b
    move-result v2

    #@1c
    invoke-virtual {v0, v1, v2}, Ljava/lang/StringBuilder;->delete(II)Ljava/lang/StringBuilder;

    #@1f
    .line 68
    :cond_0
    return-void
.end method


# virtual methods
.method public close()V
    .locals 0

    #@0
    .prologue
    .line 44
    invoke-direct {p0}, Landroid/support/v4/util/LogWriter;->flushBuilder()V

    #@3
    .line 45
    return-void
.end method

.method public flush()V
    .locals 0

    #@0
    .prologue
    .line 48
    invoke-direct {p0}, Landroid/support/v4/util/LogWriter;->flushBuilder()V

    #@3
    .line 49
    return-void
.end method

.method public write([CII)V
    .locals 3
    .param p1, "buf"    # [C
    .param p2, "offset"    # I
    .param p3, "count"    # I

    #@0
    .prologue
    .line 52
    const/4 v1, 0x0

    #@1
    .local v1, "i":I
    :goto_0
    if-ge v1, p3, :cond_1

    #@3
    .line 53
    add-int v2, p2, v1

    #@5
    aget-char v0, p1, v2

    #@7
    .line 54
    .local v0, "c":C
    const/16 v2, 0xa

    #@9
    if-ne v0, v2, :cond_0

    #@b
    .line 55
    invoke-direct {p0}, Landroid/support/v4/util/LogWriter;->flushBuilder()V

    #@e
    .line 52
    :goto_1
    add-int/lit8 v1, v1, 0x1

    #@10
    goto :goto_0

    #@11
    .line 58
    :cond_0
    iget-object v2, p0, Landroid/support/v4/util/LogWriter;->mBuilder:Ljava/lang/StringBuilder;

    #@13
    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@16
    goto :goto_1

    #@17
    .line 61
    .end local v0    # "c":C
    :cond_1
    return-void
.end method
