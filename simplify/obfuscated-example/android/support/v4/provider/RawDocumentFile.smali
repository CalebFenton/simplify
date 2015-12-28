.class Landroid/support/v4/provider/RawDocumentFile;
.super Landroid/support/v4/provider/DocumentFile;
.source "RawDocumentFile.java"


# instance fields
.field private mFile:Ljava/io/File;


# direct methods
.method constructor <init>(Landroid/support/v4/provider/DocumentFile;Ljava/io/File;)V
    .locals 0
    .param p1, "parent"    # Landroid/support/v4/provider/DocumentFile;
    .param p2, "file"    # Ljava/io/File;

    #@0
    .prologue
    .line 31
    invoke-direct {p0, p1}, Landroid/support/v4/provider/DocumentFile;-><init>(Landroid/support/v4/provider/DocumentFile;)V

    #@3
    .line 32
    iput-object p2, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@5
    .line 33
    return-void
.end method

.method private static deleteContents(Ljava/io/File;)Z
    .locals 9
    .param p0, "dir"    # Ljava/io/File;

    #@0
    .prologue
    .line 159
    invoke-virtual {p0}, Ljava/io/File;->listFiles()[Ljava/io/File;

    #@3
    move-result-object v2

    #@4
    .line 160
    .local v2, "files":[Ljava/io/File;
    const/4 v5, 0x1

    #@5
    .line 161
    .local v5, "success":Z
    if-eqz v2, :cond_2

    #@7
    .line 162
    move-object v0, v2

    #@8
    .local v0, "arr$":[Ljava/io/File;
    array-length v4, v0

    #@9
    .local v4, "len$":I
    const/4 v3, 0x0

    #@a
    .local v3, "i$":I
    :goto_0
    if-ge v3, v4, :cond_2

    #@c
    aget-object v1, v0, v3

    #@e
    .line 163
    .local v1, "file":Ljava/io/File;
    invoke-virtual {v1}, Ljava/io/File;->isDirectory()Z

    #@11
    move-result v6

    #@12
    if-eqz v6, :cond_0

    #@14
    .line 164
    invoke-static {v1}, Landroid/support/v4/provider/RawDocumentFile;->deleteContents(Ljava/io/File;)Z

    #@17
    move-result v6

    #@18
    and-int/2addr v5, v6

    #@19
    .line 166
    :cond_0
    invoke-virtual {v1}, Ljava/io/File;->delete()Z

    #@1c
    move-result v6

    #@1d
    if-nez v6, :cond_1

    #@1f
    .line 167
    const-string v6, "DocumentFile"

    #@21
    new-instance v7, Ljava/lang/StringBuilder;

    #@23
    invoke-direct {v7}, Ljava/lang/StringBuilder;-><init>()V

    #@26
    const-string v8, "Failed to delete "

    #@28
    invoke-virtual {v7, v8}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2b
    move-result-object v7

    #@2c
    invoke-virtual {v7, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@2f
    move-result-object v7

    #@30
    invoke-virtual {v7}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@33
    move-result-object v7

    #@34
    invoke-static {v6, v7}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@37
    .line 168
    const/4 v5, 0x0

    #@38
    .line 162
    :cond_1
    add-int/lit8 v3, v3, 0x1

    #@3a
    goto :goto_0

    #@3b
    .line 172
    .end local v0    # "arr$":[Ljava/io/File;
    .end local v1    # "file":Ljava/io/File;
    .end local v3    # "i$":I
    .end local v4    # "len$":I
    :cond_2
    return v5
.end method

.method private static getTypeForName(Ljava/lang/String;)Ljava/lang/String;
    .locals 4
    .param p0, "name"    # Ljava/lang/String;

    #@0
    .prologue
    .line 146
    const/16 v3, 0x2e

    #@2
    invoke-virtual {p0, v3}, Ljava/lang/String;->lastIndexOf(I)I

    #@5
    move-result v1

    #@6
    .line 147
    .local v1, "lastDot":I
    if-ltz v1, :cond_0

    #@8
    .line 148
    add-int/lit8 v3, v1, 0x1

    #@a
    invoke-virtual {p0, v3}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    #@d
    move-result-object v3

    #@e
    invoke-virtual {v3}, Ljava/lang/String;->toLowerCase()Ljava/lang/String;

    #@11
    move-result-object v0

    #@12
    .line 149
    .local v0, "extension":Ljava/lang/String;
    invoke-static {}, Landroid/webkit/MimeTypeMap;->getSingleton()Landroid/webkit/MimeTypeMap;

    #@15
    move-result-object v3

    #@16
    invoke-virtual {v3, v0}, Landroid/webkit/MimeTypeMap;->getMimeTypeFromExtension(Ljava/lang/String;)Ljava/lang/String;

    #@19
    move-result-object v2

    #@1a
    .line 150
    .local v2, "mime":Ljava/lang/String;
    if-eqz v2, :cond_0

    #@1c
    .line 155
    .end local v0    # "extension":Ljava/lang/String;
    .end local v2    # "mime":Ljava/lang/String;
    :goto_0
    return-object v2

    #@1d
    :cond_0
    const-string v2, "application/octet-stream"

    #@1f
    goto :goto_0
.end method


# virtual methods
.method public canRead()Z
    .locals 1

    #@0
    .prologue
    .line 103
    iget-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@2
    invoke-virtual {v0}, Ljava/io/File;->canRead()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public canWrite()Z
    .locals 1

    #@0
    .prologue
    .line 108
    iget-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@2
    invoke-virtual {v0}, Ljava/io/File;->canWrite()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public createDirectory(Ljava/lang/String;)Landroid/support/v4/provider/DocumentFile;
    .locals 2
    .param p1, "displayName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 54
    new-instance v0, Ljava/io/File;

    #@2
    iget-object v1, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@4
    invoke-direct {v0, v1, p1}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V

    #@7
    .line 55
    .local v0, "target":Ljava/io/File;
    invoke-virtual {v0}, Ljava/io/File;->isDirectory()Z

    #@a
    move-result v1

    #@b
    if-nez v1, :cond_0

    #@d
    invoke-virtual {v0}, Ljava/io/File;->mkdir()Z

    #@10
    move-result v1

    #@11
    if-eqz v1, :cond_1

    #@13
    .line 56
    :cond_0
    new-instance v1, Landroid/support/v4/provider/RawDocumentFile;

    #@15
    invoke-direct {v1, p0, v0}, Landroid/support/v4/provider/RawDocumentFile;-><init>(Landroid/support/v4/provider/DocumentFile;Ljava/io/File;)V

    #@18
    .line 58
    :goto_0
    return-object v1

    #@19
    :cond_1
    const/4 v1, 0x0

    #@1a
    goto :goto_0
.end method

.method public createFile(Ljava/lang/String;Ljava/lang/String;)Landroid/support/v4/provider/DocumentFile;
    .locals 6
    .param p1, "mimeType"    # Ljava/lang/String;
    .param p2, "displayName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 38
    invoke-static {}, Landroid/webkit/MimeTypeMap;->getSingleton()Landroid/webkit/MimeTypeMap;

    #@3
    move-result-object v3

    #@4
    invoke-virtual {v3, p1}, Landroid/webkit/MimeTypeMap;->getExtensionFromMimeType(Ljava/lang/String;)Ljava/lang/String;

    #@7
    move-result-object v1

    #@8
    .line 39
    .local v1, "extension":Ljava/lang/String;
    if-eqz v1, :cond_0

    #@a
    .line 40
    new-instance v3, Ljava/lang/StringBuilder;

    #@c
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@f
    invoke-virtual {v3, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v3

    #@13
    const-string v4, "."

    #@15
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@18
    move-result-object v3

    #@19
    invoke-virtual {v3, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v3

    #@1d
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@20
    move-result-object p2

    #@21
    .line 42
    :cond_0
    new-instance v2, Ljava/io/File;

    #@23
    iget-object v3, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@25
    invoke-direct {v2, v3, p2}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V

    #@28
    .line 44
    .local v2, "target":Ljava/io/File;
    :try_start_0
    invoke-virtual {v2}, Ljava/io/File;->createNewFile()Z

    #@2b
    .line 45
    new-instance v3, Landroid/support/v4/provider/RawDocumentFile;

    #@2d
    invoke-direct {v3, p0, v2}, Landroid/support/v4/provider/RawDocumentFile;-><init>(Landroid/support/v4/provider/DocumentFile;Ljava/io/File;)V
    :try_end_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_0

    #@30
    .line 48
    :goto_0
    return-object v3

    #@31
    .line 46
    :catch_0
    move-exception v0

    #@32
    .line 47
    .local v0, "e":Ljava/io/IOException;
    const-string v3, "DocumentFile"

    #@34
    new-instance v4, Ljava/lang/StringBuilder;

    #@36
    invoke-direct {v4}, Ljava/lang/StringBuilder;-><init>()V

    #@39
    const-string v5, "Failed to createFile: "

    #@3b
    invoke-virtual {v4, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@3e
    move-result-object v4

    #@3f
    invoke-virtual {v4, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@42
    move-result-object v4

    #@43
    invoke-virtual {v4}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@46
    move-result-object v4

    #@47
    invoke-static {v3, v4}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@4a
    .line 48
    const/4 v3, 0x0

    #@4b
    goto :goto_0
.end method

.method public delete()Z
    .locals 1

    #@0
    .prologue
    .line 113
    iget-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@2
    invoke-static {v0}, Landroid/support/v4/provider/RawDocumentFile;->deleteContents(Ljava/io/File;)Z

    #@5
    .line 114
    iget-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@7
    invoke-virtual {v0}, Ljava/io/File;->delete()Z

    #@a
    move-result v0

    #@b
    return v0
.end method

.method public exists()Z
    .locals 1

    #@0
    .prologue
    .line 119
    iget-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@2
    invoke-virtual {v0}, Ljava/io/File;->exists()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getName()Ljava/lang/String;
    .locals 1

    #@0
    .prologue
    .line 69
    iget-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@2
    invoke-virtual {v0}, Ljava/io/File;->getName()Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getType()Ljava/lang/String;
    .locals 1

    #@0
    .prologue
    .line 74
    iget-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@2
    invoke-virtual {v0}, Ljava/io/File;->isDirectory()Z

    #@5
    move-result v0

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 75
    const/4 v0, 0x0

    #@9
    .line 77
    :goto_0
    return-object v0

    #@a
    :cond_0
    iget-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@c
    invoke-virtual {v0}, Ljava/io/File;->getName()Ljava/lang/String;

    #@f
    move-result-object v0

    #@10
    invoke-static {v0}, Landroid/support/v4/provider/RawDocumentFile;->getTypeForName(Ljava/lang/String;)Ljava/lang/String;

    #@13
    move-result-object v0

    #@14
    goto :goto_0
.end method

.method public getUri()Landroid/net/Uri;
    .locals 1

    #@0
    .prologue
    .line 64
    iget-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@2
    invoke-static {v0}, Landroid/net/Uri;->fromFile(Ljava/io/File;)Landroid/net/Uri;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public isDirectory()Z
    .locals 1

    #@0
    .prologue
    .line 83
    iget-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@2
    invoke-virtual {v0}, Ljava/io/File;->isDirectory()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public isFile()Z
    .locals 1

    #@0
    .prologue
    .line 88
    iget-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@2
    invoke-virtual {v0}, Ljava/io/File;->isFile()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public lastModified()J
    .locals 2

    #@0
    .prologue
    .line 93
    iget-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@2
    invoke-virtual {v0}, Ljava/io/File;->lastModified()J

    #@5
    move-result-wide v0

    #@6
    return-wide v0
.end method

.method public length()J
    .locals 2

    #@0
    .prologue
    .line 98
    iget-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@2
    invoke-virtual {v0}, Ljava/io/File;->length()J

    #@5
    move-result-wide v0

    #@6
    return-wide v0
.end method

.method public listFiles()[Landroid/support/v4/provider/DocumentFile;
    .locals 7

    #@0
    .prologue
    .line 124
    new-instance v5, Ljava/util/ArrayList;

    #@2
    invoke-direct {v5}, Ljava/util/ArrayList;-><init>()V

    #@5
    .line 125
    .local v5, "results":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v4/provider/DocumentFile;>;"
    iget-object v6, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@7
    invoke-virtual {v6}, Ljava/io/File;->listFiles()[Ljava/io/File;

    #@a
    move-result-object v2

    #@b
    .line 126
    .local v2, "files":[Ljava/io/File;
    if-eqz v2, :cond_0

    #@d
    .line 127
    move-object v0, v2

    #@e
    .local v0, "arr$":[Ljava/io/File;
    array-length v4, v0

    #@f
    .local v4, "len$":I
    const/4 v3, 0x0

    #@10
    .local v3, "i$":I
    :goto_0
    if-ge v3, v4, :cond_0

    #@12
    aget-object v1, v0, v3

    #@14
    .line 128
    .local v1, "file":Ljava/io/File;
    new-instance v6, Landroid/support/v4/provider/RawDocumentFile;

    #@16
    invoke-direct {v6, p0, v1}, Landroid/support/v4/provider/RawDocumentFile;-><init>(Landroid/support/v4/provider/DocumentFile;Ljava/io/File;)V

    #@19
    invoke-virtual {v5, v6}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@1c
    .line 127
    add-int/lit8 v3, v3, 0x1

    #@1e
    goto :goto_0

    #@1f
    .line 131
    .end local v0    # "arr$":[Ljava/io/File;
    .end local v1    # "file":Ljava/io/File;
    .end local v3    # "i$":I
    .end local v4    # "len$":I
    :cond_0
    invoke-virtual {v5}, Ljava/util/ArrayList;->size()I

    #@22
    move-result v6

    #@23
    new-array v6, v6, [Landroid/support/v4/provider/DocumentFile;

    #@25
    invoke-virtual {v5, v6}, Ljava/util/ArrayList;->toArray([Ljava/lang/Object;)[Ljava/lang/Object;

    #@28
    move-result-object v6

    #@29
    check-cast v6, [Landroid/support/v4/provider/DocumentFile;

    #@2b
    return-object v6
.end method

.method public renameTo(Ljava/lang/String;)Z
    .locals 2
    .param p1, "displayName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 136
    new-instance v0, Ljava/io/File;

    #@2
    iget-object v1, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@4
    invoke-virtual {v1}, Ljava/io/File;->getParentFile()Ljava/io/File;

    #@7
    move-result-object v1

    #@8
    invoke-direct {v0, v1, p1}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V

    #@b
    .line 137
    .local v0, "target":Ljava/io/File;
    iget-object v1, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@d
    invoke-virtual {v1, v0}, Ljava/io/File;->renameTo(Ljava/io/File;)Z

    #@10
    move-result v1

    #@11
    if-eqz v1, :cond_0

    #@13
    .line 138
    iput-object v0, p0, Landroid/support/v4/provider/RawDocumentFile;->mFile:Ljava/io/File;

    #@15
    .line 139
    const/4 v1, 0x1

    #@16
    .line 141
    :goto_0
    return v1

    #@17
    :cond_0
    const/4 v1, 0x0

    #@18
    goto :goto_0
.end method
