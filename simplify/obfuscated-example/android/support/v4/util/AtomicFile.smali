.class public Landroid/support/v4/util/AtomicFile;
.super Ljava/lang/Object;
.source "AtomicFile.java"


# instance fields
.field private final mBackupName:Ljava/io/File;

.field private final mBaseName:Ljava/io/File;


# direct methods
.method public constructor <init>(Ljava/io/File;)V
    .locals 3
    .param p1, "baseName"    # Ljava/io/File;

    #@0
    .prologue
    .line 51
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 52
    iput-object p1, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@5
    .line 53
    new-instance v0, Ljava/io/File;

    #@7
    new-instance v1, Ljava/lang/StringBuilder;

    #@9
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@c
    invoke-virtual {p1}, Ljava/io/File;->getPath()Ljava/lang/String;

    #@f
    move-result-object v2

    #@10
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@13
    move-result-object v1

    #@14
    const-string v2, ".bak"

    #@16
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@19
    move-result-object v1

    #@1a
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1d
    move-result-object v1

    #@1e
    invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    #@21
    iput-object v0, p0, Landroid/support/v4/util/AtomicFile;->mBackupName:Ljava/io/File;

    #@23
    .line 54
    return-void
.end method

.method static sync(Ljava/io/FileOutputStream;)Z
    .locals 1
    .param p0, "stream"    # Ljava/io/FileOutputStream;

    #@0
    .prologue
    .line 205
    if-eqz p0, :cond_0

    #@2
    .line 206
    :try_start_0
    invoke-virtual {p0}, Ljava/io/FileOutputStream;->getFD()Ljava/io/FileDescriptor;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0}, Ljava/io/FileDescriptor;->sync()V
    :try_end_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_0

    #@9
    .line 208
    :cond_0
    const/4 v0, 0x1

    #@a
    .line 211
    :goto_0
    return v0

    #@b
    .line 209
    :catch_0
    move-exception v0

    #@c
    .line 211
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method


# virtual methods
.method public delete()V
    .locals 1

    #@0
    .prologue
    .line 68
    iget-object v0, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@2
    invoke-virtual {v0}, Ljava/io/File;->delete()Z

    #@5
    .line 69
    iget-object v0, p0, Landroid/support/v4/util/AtomicFile;->mBackupName:Ljava/io/File;

    #@7
    invoke-virtual {v0}, Ljava/io/File;->delete()Z

    #@a
    .line 70
    return-void
.end method

.method public failWrite(Ljava/io/FileOutputStream;)V
    .locals 3
    .param p1, "str"    # Ljava/io/FileOutputStream;

    #@0
    .prologue
    .line 139
    if-eqz p1, :cond_0

    #@2
    .line 140
    invoke-static {p1}, Landroid/support/v4/util/AtomicFile;->sync(Ljava/io/FileOutputStream;)Z

    #@5
    .line 142
    :try_start_0
    invoke-virtual {p1}, Ljava/io/FileOutputStream;->close()V

    #@8
    .line 143
    iget-object v1, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@a
    invoke-virtual {v1}, Ljava/io/File;->delete()Z

    #@d
    .line 144
    iget-object v1, p0, Landroid/support/v4/util/AtomicFile;->mBackupName:Ljava/io/File;

    #@f
    iget-object v2, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@11
    invoke-virtual {v1, v2}, Ljava/io/File;->renameTo(Ljava/io/File;)Z
    :try_end_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_0

    #@14
    .line 149
    :cond_0
    :goto_0
    return-void

    #@15
    .line 145
    :catch_0
    move-exception v0

    #@16
    .line 146
    .local v0, "e":Ljava/io/IOException;
    const-string v1, "AtomicFile"

    #@18
    const-string v2, "failWrite: Got exception:"

    #@1a
    invoke-static {v1, v2, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@1d
    goto :goto_0
.end method

.method public finishWrite(Ljava/io/FileOutputStream;)V
    .locals 3
    .param p1, "str"    # Ljava/io/FileOutputStream;

    #@0
    .prologue
    .line 122
    if-eqz p1, :cond_0

    #@2
    .line 123
    invoke-static {p1}, Landroid/support/v4/util/AtomicFile;->sync(Ljava/io/FileOutputStream;)Z

    #@5
    .line 125
    :try_start_0
    invoke-virtual {p1}, Ljava/io/FileOutputStream;->close()V

    #@8
    .line 126
    iget-object v1, p0, Landroid/support/v4/util/AtomicFile;->mBackupName:Ljava/io/File;

    #@a
    invoke-virtual {v1}, Ljava/io/File;->delete()Z
    :try_end_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_0

    #@d
    .line 131
    :cond_0
    :goto_0
    return-void

    #@e
    .line 127
    :catch_0
    move-exception v0

    #@f
    .line 128
    .local v0, "e":Ljava/io/IOException;
    const-string v1, "AtomicFile"

    #@11
    const-string v2, "finishWrite: Got exception:"

    #@13
    invoke-static {v1, v2, v0}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@16
    goto :goto_0
.end method

.method public getBaseFile()Ljava/io/File;
    .locals 1

    #@0
    .prologue
    .line 61
    iget-object v0, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@2
    return-object v0
.end method

.method public openRead()Ljava/io/FileInputStream;
    .locals 2
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/FileNotFoundException;
        }
    .end annotation

    #@0
    .prologue
    .line 164
    iget-object v0, p0, Landroid/support/v4/util/AtomicFile;->mBackupName:Ljava/io/File;

    #@2
    invoke-virtual {v0}, Ljava/io/File;->exists()Z

    #@5
    move-result v0

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 165
    iget-object v0, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@a
    invoke-virtual {v0}, Ljava/io/File;->delete()Z

    #@d
    .line 166
    iget-object v0, p0, Landroid/support/v4/util/AtomicFile;->mBackupName:Ljava/io/File;

    #@f
    iget-object v1, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@11
    invoke-virtual {v0, v1}, Ljava/io/File;->renameTo(Ljava/io/File;)Z

    #@14
    .line 168
    :cond_0
    new-instance v0, Ljava/io/FileInputStream;

    #@16
    iget-object v1, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@18
    invoke-direct {v0, v1}, Ljava/io/FileInputStream;-><init>(Ljava/io/File;)V

    #@1b
    return-object v0
.end method

.method public readFully()[B
    .locals 8
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    #@0
    .prologue
    .line 176
    invoke-virtual {p0}, Landroid/support/v4/util/AtomicFile;->openRead()Ljava/io/FileInputStream;

    #@3
    move-result-object v5

    #@4
    .line 178
    .local v5, "stream":Ljava/io/FileInputStream;
    const/4 v4, 0x0

    #@5
    .line 179
    .local v4, "pos":I
    :try_start_0
    invoke-virtual {v5}, Ljava/io/FileInputStream;->available()I

    #@8
    move-result v1

    #@9
    .line 180
    .local v1, "avail":I
    new-array v2, v1, [B

    #@b
    .line 182
    .local v2, "data":[B
    :cond_0
    :goto_0
    array-length v6, v2

    #@c
    sub-int/2addr v6, v4

    #@d
    invoke-virtual {v5, v2, v4, v6}, Ljava/io/FileInputStream;->read([BII)I
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@10
    move-result v0

    #@11
    .line 185
    .local v0, "amt":I
    if-gtz v0, :cond_1

    #@13
    .line 199
    invoke-virtual {v5}, Ljava/io/FileInputStream;->close()V

    #@16
    return-object v2

    #@17
    .line 190
    :cond_1
    add-int/2addr v4, v0

    #@18
    .line 191
    :try_start_1
    invoke-virtual {v5}, Ljava/io/FileInputStream;->available()I

    #@1b
    move-result v1

    #@1c
    .line 192
    array-length v6, v2

    #@1d
    sub-int/2addr v6, v4

    #@1e
    if-le v1, v6, :cond_0

    #@20
    .line 193
    add-int v6, v4, v1

    #@22
    new-array v3, v6, [B

    #@24
    .line 194
    .local v3, "newData":[B
    const/4 v6, 0x0

    #@25
    const/4 v7, 0x0

    #@26
    invoke-static {v2, v6, v3, v7, v4}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@29
    .line 195
    move-object v2, v3

    #@2a
    goto :goto_0

    #@2b
    .line 199
    .end local v0    # "amt":I
    .end local v1    # "avail":I
    .end local v2    # "data":[B
    .end local v3    # "newData":[B
    :catchall_0
    move-exception v6

    #@2c
    invoke-virtual {v5}, Ljava/io/FileInputStream;->close()V

    #@2f
    throw v6
.end method

.method public startWrite()Ljava/io/FileOutputStream;
    .locals 7
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    #@0
    .prologue
    .line 88
    iget-object v4, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@2
    invoke-virtual {v4}, Ljava/io/File;->exists()Z

    #@5
    move-result v4

    #@6
    if-eqz v4, :cond_0

    #@8
    .line 89
    iget-object v4, p0, Landroid/support/v4/util/AtomicFile;->mBackupName:Ljava/io/File;

    #@a
    invoke-virtual {v4}, Ljava/io/File;->exists()Z

    #@d
    move-result v4

    #@e
    if-nez v4, :cond_1

    #@10
    .line 90
    iget-object v4, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@12
    iget-object v5, p0, Landroid/support/v4/util/AtomicFile;->mBackupName:Ljava/io/File;

    #@14
    invoke-virtual {v4, v5}, Ljava/io/File;->renameTo(Ljava/io/File;)Z

    #@17
    move-result v4

    #@18
    if-nez v4, :cond_0

    #@1a
    .line 91
    const-string v4, "AtomicFile"

    #@1c
    new-instance v5, Ljava/lang/StringBuilder;

    #@1e
    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    #@21
    const-string v6, "Couldn\'t rename file "

    #@23
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@26
    move-result-object v5

    #@27
    iget-object v6, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@29
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@2c
    move-result-object v5

    #@2d
    const-string v6, " to backup file "

    #@2f
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@32
    move-result-object v5

    #@33
    iget-object v6, p0, Landroid/support/v4/util/AtomicFile;->mBackupName:Ljava/io/File;

    #@35
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@38
    move-result-object v5

    #@39
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@3c
    move-result-object v5

    #@3d
    invoke-static {v4, v5}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@40
    .line 98
    :cond_0
    :goto_0
    const/4 v3, 0x0

    #@41
    .line 100
    .local v3, "str":Ljava/io/FileOutputStream;
    :try_start_0
    new-instance v3, Ljava/io/FileOutputStream;

    #@43
    .end local v3    # "str":Ljava/io/FileOutputStream;
    iget-object v4, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@45
    invoke-direct {v3, v4}, Ljava/io/FileOutputStream;-><init>(Ljava/io/File;)V
    :try_end_0
    .catch Ljava/io/FileNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    #@48
    .line 112
    .restart local v3    # "str":Ljava/io/FileOutputStream;
    :goto_1
    return-object v3

    #@49
    .line 95
    .end local v3    # "str":Ljava/io/FileOutputStream;
    :cond_1
    iget-object v4, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@4b
    invoke-virtual {v4}, Ljava/io/File;->delete()Z

    #@4e
    goto :goto_0

    #@4f
    .line 101
    :catch_0
    move-exception v0

    #@50
    .line 102
    .local v0, "e":Ljava/io/FileNotFoundException;
    iget-object v4, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@52
    invoke-virtual {v4}, Ljava/io/File;->getParentFile()Ljava/io/File;

    #@55
    move-result-object v2

    #@56
    .line 103
    .local v2, "parent":Ljava/io/File;
    invoke-virtual {v2}, Ljava/io/File;->mkdir()Z

    #@59
    move-result v4

    #@5a
    if-nez v4, :cond_2

    #@5c
    .line 104
    new-instance v4, Ljava/io/IOException;

    #@5e
    new-instance v5, Ljava/lang/StringBuilder;

    #@60
    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    #@63
    const-string v6, "Couldn\'t create directory "

    #@65
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@68
    move-result-object v5

    #@69
    iget-object v6, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@6b
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@6e
    move-result-object v5

    #@6f
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@72
    move-result-object v5

    #@73
    invoke-direct {v4, v5}, Ljava/io/IOException;-><init>(Ljava/lang/String;)V

    #@76
    throw v4

    #@77
    .line 107
    :cond_2
    :try_start_1
    new-instance v3, Ljava/io/FileOutputStream;

    #@79
    iget-object v4, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@7b
    invoke-direct {v3, v4}, Ljava/io/FileOutputStream;-><init>(Ljava/io/File;)V
    :try_end_1
    .catch Ljava/io/FileNotFoundException; {:try_start_1 .. :try_end_1} :catch_1

    #@7e
    .restart local v3    # "str":Ljava/io/FileOutputStream;
    goto :goto_1

    #@7f
    .line 108
    .end local v3    # "str":Ljava/io/FileOutputStream;
    :catch_1
    move-exception v1

    #@80
    .line 109
    .local v1, "e2":Ljava/io/FileNotFoundException;
    new-instance v4, Ljava/io/IOException;

    #@82
    new-instance v5, Ljava/lang/StringBuilder;

    #@84
    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    #@87
    const-string v6, "Couldn\'t create "

    #@89
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@8c
    move-result-object v5

    #@8d
    iget-object v6, p0, Landroid/support/v4/util/AtomicFile;->mBaseName:Ljava/io/File;

    #@8f
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@92
    move-result-object v5

    #@93
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@96
    move-result-object v5

    #@97
    invoke-direct {v4, v5}, Ljava/io/IOException;-><init>(Ljava/lang/String;)V

    #@9a
    throw v4
.end method
