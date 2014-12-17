.class public Landroid/util/TimingLogger;
.super Ljava/lang/Object;
.source "TimingLogger.java"


# instance fields
.field private mDisabled:Z

.field private mLabel:Ljava/lang/String;

.field mSplitLabels:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field mSplits:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/Long;",
            ">;"
        }
    .end annotation
.end field

.field private mTag:Ljava/lang/String;


# direct methods
.method public constructor <init>(Ljava/lang/String;Ljava/lang/String;)V
    .registers 3
    .param p1, "tag"    # Ljava/lang/String;
    .param p2, "label"    # Ljava/lang/String;

    .prologue
    .line 76
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 77
    invoke-virtual {p0, p1, p2}, Landroid/util/TimingLogger;->reset(Ljava/lang/String;Ljava/lang/String;)V

    .line 78
    return-void
.end method


# virtual methods
.method public addSplit(Ljava/lang/String;)V
    .registers 6
    .param p1, "splitLabel"    # Ljava/lang/String;

    .prologue
    .line 123
    iget-boolean v2, p0, Landroid/util/TimingLogger;->mDisabled:Z

    if-eqz v2, :cond_5

    .line 127
    :goto_4
    return-void

    .line 124
    :cond_5
    invoke-static {}, Landroid/os/SystemClock;->elapsedRealtime()J

    move-result-wide v0

    .line 125
    .local v0, "now":J
    iget-object v2, p0, Landroid/util/TimingLogger;->mSplits:Ljava/util/ArrayList;

    invoke-static {v0, v1}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;

    move-result-object v3

    invoke-virtual {v2, v3}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    .line 126
    iget-object v2, p0, Landroid/util/TimingLogger;->mSplitLabels:Ljava/util/ArrayList;

    invoke-virtual {v2, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    goto :goto_4
.end method

.method public dumpToLog()V
    .registers 13

    .prologue
    .line 135
    iget-boolean v8, p0, Landroid/util/TimingLogger;->mDisabled:Z

    if-eqz v8, :cond_5

    .line 147
    :goto_4
    return-void

    .line 136
    :cond_5
    iget-object v8, p0, Landroid/util/TimingLogger;->mTag:Ljava/lang/String;

    new-instance v9, Ljava/lang/StringBuilder;

    invoke-direct {v9}, Ljava/lang/StringBuilder;-><init>()V

    iget-object v10, p0, Landroid/util/TimingLogger;->mLabel:Ljava/lang/String;

    invoke-virtual {v9, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v9

    const-string v10, ": begin"

    invoke-virtual {v9, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v9

    invoke-virtual {v9}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v9

    invoke-static {v8, v9}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 137
    iget-object v8, p0, Landroid/util/TimingLogger;->mSplits:Ljava/util/ArrayList;

    const/4 v9, 0x0

    invoke-virtual {v8, v9}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v8

    check-cast v8, Ljava/lang/Long;

    invoke-virtual {v8}, Ljava/lang/Long;->longValue()J

    move-result-wide v0

    .line 138
    .local v0, "first":J
    move-wide v3, v0

    .line 139
    .local v3, "now":J
    const/4 v2, 0x1

    .local v2, "i":I
    :goto_2e
    iget-object v8, p0, Landroid/util/TimingLogger;->mSplits:Ljava/util/ArrayList;

    invoke-virtual {v8}, Ljava/util/ArrayList;->size()I

    move-result v8

    if-ge v2, v8, :cond_85

    .line 140
    iget-object v8, p0, Landroid/util/TimingLogger;->mSplits:Ljava/util/ArrayList;

    invoke-virtual {v8, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v8

    check-cast v8, Ljava/lang/Long;

    invoke-virtual {v8}, Ljava/lang/Long;->longValue()J

    move-result-wide v3

    .line 141
    iget-object v8, p0, Landroid/util/TimingLogger;->mSplitLabels:Ljava/util/ArrayList;

    invoke-virtual {v8, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v7

    check-cast v7, Ljava/lang/String;

    .line 142
    .local v7, "splitLabel":Ljava/lang/String;
    iget-object v8, p0, Landroid/util/TimingLogger;->mSplits:Ljava/util/ArrayList;

    add-int/lit8 v9, v2, -0x1

    invoke-virtual {v8, v9}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    move-result-object v8

    check-cast v8, Ljava/lang/Long;

    invoke-virtual {v8}, Ljava/lang/Long;->longValue()J

    move-result-wide v5

    .line 144
    .local v5, "prev":J
    iget-object v8, p0, Landroid/util/TimingLogger;->mTag:Ljava/lang/String;

    new-instance v9, Ljava/lang/StringBuilder;

    invoke-direct {v9}, Ljava/lang/StringBuilder;-><init>()V

    iget-object v10, p0, Landroid/util/TimingLogger;->mLabel:Ljava/lang/String;

    invoke-virtual {v9, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v9

    const-string v10, ":      "

    invoke-virtual {v9, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v9

    sub-long v10, v3, v5

    invoke-virtual {v9, v10, v11}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

    move-result-object v9

    const-string v10, " ms, "

    invoke-virtual {v9, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v9

    invoke-virtual {v9, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v9

    invoke-virtual {v9}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v9

    invoke-static {v8, v9}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 139
    add-int/lit8 v2, v2, 0x1

    goto :goto_2e

    .line 146
    .end local v5    # "prev":J
    .end local v7    # "splitLabel":Ljava/lang/String;
    :cond_85
    iget-object v8, p0, Landroid/util/TimingLogger;->mTag:Ljava/lang/String;

    new-instance v9, Ljava/lang/StringBuilder;

    invoke-direct {v9}, Ljava/lang/StringBuilder;-><init>()V

    iget-object v10, p0, Landroid/util/TimingLogger;->mLabel:Ljava/lang/String;

    invoke-virtual {v9, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v9

    const-string v10, ": end, "

    invoke-virtual {v9, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v9

    sub-long v10, v3, v0

    invoke-virtual {v9, v10, v11}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

    move-result-object v9

    const-string v10, " ms"

    invoke-virtual {v9, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v9

    invoke-virtual {v9}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v9

    invoke-static {v8, v9}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    goto/16 :goto_4
.end method

.method public reset()V
    .registers 3

    .prologue
    .line 103
    iget-object v0, p0, Landroid/util/TimingLogger;->mTag:Ljava/lang/String;

    const/4 v1, 0x2

    invoke-static {v0, v1}, Landroid/util/Log;->isLoggable(Ljava/lang/String;I)Z

    move-result v0

    if-nez v0, :cond_11

    const/4 v0, 0x1

    :goto_a
    iput-boolean v0, p0, Landroid/util/TimingLogger;->mDisabled:Z

    .line 104
    iget-boolean v0, p0, Landroid/util/TimingLogger;->mDisabled:Z

    if-eqz v0, :cond_13

    .line 113
    :goto_10
    return-void

    .line 103
    :cond_11
    const/4 v0, 0x0

    goto :goto_a

    .line 105
    :cond_13
    iget-object v0, p0, Landroid/util/TimingLogger;->mSplits:Ljava/util/ArrayList;

    if-nez v0, :cond_2a

    .line 106
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Landroid/util/TimingLogger;->mSplits:Ljava/util/ArrayList;

    .line 107
    new-instance v0, Ljava/util/ArrayList;

    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    iput-object v0, p0, Landroid/util/TimingLogger;->mSplitLabels:Ljava/util/ArrayList;

    .line 112
    :goto_25
    const/4 v0, 0x0

    invoke-virtual {p0, v0}, Landroid/util/TimingLogger;->addSplit(Ljava/lang/String;)V

    goto :goto_10

    .line 109
    :cond_2a
    iget-object v0, p0, Landroid/util/TimingLogger;->mSplits:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->clear()V

    .line 110
    iget-object v0, p0, Landroid/util/TimingLogger;->mSplitLabels:Ljava/util/ArrayList;

    invoke-virtual {v0}, Ljava/util/ArrayList;->clear()V

    goto :goto_25
.end method

.method public reset(Ljava/lang/String;Ljava/lang/String;)V
    .registers 3
    .param p1, "tag"    # Ljava/lang/String;
    .param p2, "label"    # Ljava/lang/String;

    .prologue
    .line 89
    iput-object p1, p0, Landroid/util/TimingLogger;->mTag:Ljava/lang/String;

    .line 90
    iput-object p2, p0, Landroid/util/TimingLogger;->mLabel:Ljava/lang/String;

    .line 91
    invoke-virtual {p0}, Landroid/util/TimingLogger;->reset()V

    .line 92
    return-void
.end method
