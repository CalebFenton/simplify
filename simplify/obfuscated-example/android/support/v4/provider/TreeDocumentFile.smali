.class Landroid/support/v4/provider/TreeDocumentFile;
.super Landroid/support/v4/provider/DocumentFile;
.source "TreeDocumentFile.java"


# instance fields
.field private mContext:Landroid/content/Context;

.field private mUri:Landroid/net/Uri;


# direct methods
.method constructor <init>(Landroid/support/v4/provider/DocumentFile;Landroid/content/Context;Landroid/net/Uri;)V
    .locals 0
    .param p1, "parent"    # Landroid/support/v4/provider/DocumentFile;
    .param p2, "context"    # Landroid/content/Context;
    .param p3, "uri"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 27
    invoke-direct {p0, p1}, Landroid/support/v4/provider/DocumentFile;-><init>(Landroid/support/v4/provider/DocumentFile;)V

    #@3
    .line 28
    iput-object p2, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@5
    .line 29
    iput-object p3, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@7
    .line 30
    return-void
.end method


# virtual methods
.method public canRead()Z
    .locals 2

    #@0
    .prologue
    .line 81
    iget-object v0, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v1, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v0, v1}, Landroid/support/v4/provider/DocumentsContractApi19;->canRead(Landroid/content/Context;Landroid/net/Uri;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public canWrite()Z
    .locals 2

    #@0
    .prologue
    .line 86
    iget-object v0, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v1, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v0, v1}, Landroid/support/v4/provider/DocumentsContractApi19;->canWrite(Landroid/content/Context;Landroid/net/Uri;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public createDirectory(Ljava/lang/String;)Landroid/support/v4/provider/DocumentFile;
    .locals 3
    .param p1, "displayName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 40
    iget-object v1, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v2, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v1, v2, p1}, Landroid/support/v4/provider/DocumentsContractApi21;->createDirectory(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;

    #@7
    move-result-object v0

    #@8
    .line 41
    .local v0, "result":Landroid/net/Uri;
    if-eqz v0, :cond_0

    #@a
    new-instance v1, Landroid/support/v4/provider/TreeDocumentFile;

    #@c
    iget-object v2, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@e
    invoke-direct {v1, p0, v2, v0}, Landroid/support/v4/provider/TreeDocumentFile;-><init>(Landroid/support/v4/provider/DocumentFile;Landroid/content/Context;Landroid/net/Uri;)V

    #@11
    :goto_0
    return-object v1

    #@12
    :cond_0
    const/4 v1, 0x0

    #@13
    goto :goto_0
.end method

.method public createFile(Ljava/lang/String;Ljava/lang/String;)Landroid/support/v4/provider/DocumentFile;
    .locals 3
    .param p1, "mimeType"    # Ljava/lang/String;
    .param p2, "displayName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 34
    iget-object v1, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v2, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v1, v2, p1, p2}, Landroid/support/v4/provider/DocumentsContractApi21;->createFile(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri;

    #@7
    move-result-object v0

    #@8
    .line 35
    .local v0, "result":Landroid/net/Uri;
    if-eqz v0, :cond_0

    #@a
    new-instance v1, Landroid/support/v4/provider/TreeDocumentFile;

    #@c
    iget-object v2, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@e
    invoke-direct {v1, p0, v2, v0}, Landroid/support/v4/provider/TreeDocumentFile;-><init>(Landroid/support/v4/provider/DocumentFile;Landroid/content/Context;Landroid/net/Uri;)V

    #@11
    :goto_0
    return-object v1

    #@12
    :cond_0
    const/4 v1, 0x0

    #@13
    goto :goto_0
.end method

.method public delete()Z
    .locals 2

    #@0
    .prologue
    .line 91
    iget-object v0, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v1, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v0, v1}, Landroid/support/v4/provider/DocumentsContractApi19;->delete(Landroid/content/Context;Landroid/net/Uri;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public exists()Z
    .locals 2

    #@0
    .prologue
    .line 96
    iget-object v0, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v1, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v0, v1}, Landroid/support/v4/provider/DocumentsContractApi19;->exists(Landroid/content/Context;Landroid/net/Uri;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public getName()Ljava/lang/String;
    .locals 2

    #@0
    .prologue
    .line 51
    iget-object v0, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v1, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v0, v1}, Landroid/support/v4/provider/DocumentsContractApi19;->getName(Landroid/content/Context;Landroid/net/Uri;)Ljava/lang/String;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public getType()Ljava/lang/String;
    .locals 2

    #@0
    .prologue
    .line 56
    iget-object v0, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v1, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v0, v1}, Landroid/support/v4/provider/DocumentsContractApi19;->getType(Landroid/content/Context;Landroid/net/Uri;)Ljava/lang/String;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public getUri()Landroid/net/Uri;
    .locals 1

    #@0
    .prologue
    .line 46
    iget-object v0, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@2
    return-object v0
.end method

.method public isDirectory()Z
    .locals 2

    #@0
    .prologue
    .line 61
    iget-object v0, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v1, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v0, v1}, Landroid/support/v4/provider/DocumentsContractApi19;->isDirectory(Landroid/content/Context;Landroid/net/Uri;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public isFile()Z
    .locals 2

    #@0
    .prologue
    .line 66
    iget-object v0, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v1, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v0, v1}, Landroid/support/v4/provider/DocumentsContractApi19;->isFile(Landroid/content/Context;Landroid/net/Uri;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public lastModified()J
    .locals 2

    #@0
    .prologue
    .line 71
    iget-object v0, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v1, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v0, v1}, Landroid/support/v4/provider/DocumentsContractApi19;->lastModified(Landroid/content/Context;Landroid/net/Uri;)J

    #@7
    move-result-wide v0

    #@8
    return-wide v0
.end method

.method public length()J
    .locals 2

    #@0
    .prologue
    .line 76
    iget-object v0, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v1, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v0, v1}, Landroid/support/v4/provider/DocumentsContractApi19;->length(Landroid/content/Context;Landroid/net/Uri;)J

    #@7
    move-result-wide v0

    #@8
    return-wide v0
.end method

.method public listFiles()[Landroid/support/v4/provider/DocumentFile;
    .locals 6

    #@0
    .prologue
    .line 101
    iget-object v3, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v4, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v3, v4}, Landroid/support/v4/provider/DocumentsContractApi21;->listFiles(Landroid/content/Context;Landroid/net/Uri;)[Landroid/net/Uri;

    #@7
    move-result-object v1

    #@8
    .line 102
    .local v1, "result":[Landroid/net/Uri;
    array-length v3, v1

    #@9
    new-array v2, v3, [Landroid/support/v4/provider/DocumentFile;

    #@b
    .line 103
    .local v2, "resultFiles":[Landroid/support/v4/provider/DocumentFile;
    const/4 v0, 0x0

    #@c
    .local v0, "i":I
    :goto_0
    array-length v3, v1

    #@d
    if-ge v0, v3, :cond_0

    #@f
    .line 104
    new-instance v3, Landroid/support/v4/provider/TreeDocumentFile;

    #@11
    iget-object v4, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@13
    aget-object v5, v1, v0

    #@15
    invoke-direct {v3, p0, v4, v5}, Landroid/support/v4/provider/TreeDocumentFile;-><init>(Landroid/support/v4/provider/DocumentFile;Landroid/content/Context;Landroid/net/Uri;)V

    #@18
    aput-object v3, v2, v0

    #@1a
    .line 103
    add-int/lit8 v0, v0, 0x1

    #@1c
    goto :goto_0

    #@1d
    .line 106
    :cond_0
    return-object v2
.end method

.method public renameTo(Ljava/lang/String;)Z
    .locals 3
    .param p1, "displayName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 111
    iget-object v1, p0, Landroid/support/v4/provider/TreeDocumentFile;->mContext:Landroid/content/Context;

    #@2
    iget-object v2, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@4
    invoke-static {v1, v2, p1}, Landroid/support/v4/provider/DocumentsContractApi21;->renameTo(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;

    #@7
    move-result-object v0

    #@8
    .line 112
    .local v0, "result":Landroid/net/Uri;
    if-eqz v0, :cond_0

    #@a
    .line 113
    iput-object v0, p0, Landroid/support/v4/provider/TreeDocumentFile;->mUri:Landroid/net/Uri;

    #@c
    .line 114
    const/4 v1, 0x1

    #@d
    .line 116
    :goto_0
    return v1

    #@e
    :cond_0
    const/4 v1, 0x0

    #@f
    goto :goto_0
.end method
