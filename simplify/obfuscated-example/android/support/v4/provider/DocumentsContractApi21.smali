.class Landroid/support/v4/provider/DocumentsContractApi21;
.super Ljava/lang/Object;
.source "DocumentsContractApi21.java"


# static fields
.field private static final TAG:Ljava/lang/String; = "DocumentFile"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 28
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method private static closeQuietly(Ljava/lang/AutoCloseable;)V
    .locals 2
    .param p0, "closeable"    # Ljava/lang/AutoCloseable;

    #@0
    .prologue
    .line 76
    if-eqz p0, :cond_0

    #@2
    .line 78
    :try_start_0
    invoke-interface {p0}, Ljava/lang/AutoCloseable;->close()V
    :try_end_0
    .catch Ljava/lang/RuntimeException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    #@5
    .line 84
    :cond_0
    :goto_0
    return-void

    #@6
    .line 79
    :catch_0
    move-exception v0

    #@7
    .line 80
    .local v0, "rethrown":Ljava/lang/RuntimeException;
    throw v0

    #@8
    .line 81
    .end local v0    # "rethrown":Ljava/lang/RuntimeException;
    :catch_1
    move-exception v1

    #@9
    goto :goto_0
.end method

.method public static createDirectory(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;
    .param p2, "displayName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 38
    const-string v0, "vnd.android.document/directory"

    #@2
    invoke-static {p0, p1, v0, p2}, Landroid/support/v4/provider/DocumentsContractApi21;->createFile(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static createFile(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;
    .param p2, "mimeType"    # Ljava/lang/String;
    .param p3, "displayName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 33
    invoke-virtual {p0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    #@3
    move-result-object v0

    #@4
    invoke-static {v0, p1, p2, p3}, Landroid/provider/DocumentsContract;->createDocument(Landroid/content/ContentResolver;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public static listFiles(Landroid/content/Context;Landroid/net/Uri;)[Landroid/net/Uri;
    .locals 11
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 47
    invoke-virtual {p0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    #@3
    move-result-object v0

    #@4
    .line 48
    .local v0, "resolver":Landroid/content/ContentResolver;
    invoke-static {p1}, Landroid/provider/DocumentsContract;->getDocumentId(Landroid/net/Uri;)Ljava/lang/String;

    #@7
    move-result-object v2

    #@8
    invoke-static {p1, v2}, Landroid/provider/DocumentsContract;->buildChildDocumentsUriUsingTree(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;

    #@b
    move-result-object v1

    #@c
    .line 50
    .local v1, "childrenUri":Landroid/net/Uri;
    new-instance v10, Ljava/util/ArrayList;

    #@e
    invoke-direct {v10}, Ljava/util/ArrayList;-><init>()V

    #@11
    .line 52
    .local v10, "results":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/net/Uri;>;"
    const/4 v6, 0x0

    #@12
    .line 54
    .local v6, "c":Landroid/database/Cursor;
    const/4 v2, 0x1

    #@13
    :try_start_0
    new-array v2, v2, [Ljava/lang/String;

    #@15
    const/4 v3, 0x0

    #@16
    const-string v4, "document_id"

    #@18
    aput-object v4, v2, v3

    #@1a
    const/4 v3, 0x0

    #@1b
    const/4 v4, 0x0

    #@1c
    const/4 v5, 0x0

    #@1d
    invoke-virtual/range {v0 .. v5}, Landroid/content/ContentResolver;->query(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;

    #@20
    move-result-object v6

    #@21
    .line 56
    :goto_0
    invoke-interface {v6}, Landroid/database/Cursor;->moveToNext()Z

    #@24
    move-result v2

    #@25
    if-eqz v2, :cond_0

    #@27
    .line 57
    const/4 v2, 0x0

    #@28
    invoke-interface {v6, v2}, Landroid/database/Cursor;->getString(I)Ljava/lang/String;

    #@2b
    move-result-object v7

    #@2c
    .line 58
    .local v7, "documentId":Ljava/lang/String;
    invoke-static {p1, v7}, Landroid/provider/DocumentsContract;->buildDocumentUriUsingTree(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;

    #@2f
    move-result-object v8

    #@30
    .line 60
    .local v8, "documentUri":Landroid/net/Uri;
    invoke-virtual {v10, v8}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@33
    goto :goto_0

    #@34
    .line 62
    .end local v7    # "documentId":Ljava/lang/String;
    .end local v8    # "documentUri":Landroid/net/Uri;
    :catch_0
    move-exception v9

    #@35
    .line 63
    .local v9, "e":Ljava/lang/Exception;
    :try_start_1
    const-string v2, "DocumentFile"

    #@37
    new-instance v3, Ljava/lang/StringBuilder;

    #@39
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@3c
    const-string v4, "Failed query: "

    #@3e
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@41
    move-result-object v3

    #@42
    invoke-virtual {v3, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@45
    move-result-object v3

    #@46
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@49
    move-result-object v3

    #@4a
    invoke-static {v2, v3}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@4d
    .line 65
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi21;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@50
    .line 68
    .end local v9    # "e":Ljava/lang/Exception;
    :goto_1
    invoke-virtual {v10}, Ljava/util/ArrayList;->size()I

    #@53
    move-result v2

    #@54
    new-array v2, v2, [Landroid/net/Uri;

    #@56
    invoke-virtual {v10, v2}, Ljava/util/ArrayList;->toArray([Ljava/lang/Object;)[Ljava/lang/Object;

    #@59
    move-result-object v2

    #@5a
    check-cast v2, [Landroid/net/Uri;

    #@5c
    return-object v2

    #@5d
    .line 65
    :cond_0
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi21;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@60
    goto :goto_1

    #@61
    :catchall_0
    move-exception v2

    #@62
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi21;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@65
    throw v2
.end method

.method public static prepareTreeUri(Landroid/net/Uri;)Landroid/net/Uri;
    .locals 1
    .param p0, "treeUri"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 42
    invoke-static {p0}, Landroid/provider/DocumentsContract;->getTreeDocumentId(Landroid/net/Uri;)Ljava/lang/String;

    #@3
    move-result-object v0

    #@4
    invoke-static {p0, v0}, Landroid/provider/DocumentsContract;->buildDocumentUriUsingTree(Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public static renameTo(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;
    .param p2, "displayName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 72
    invoke-virtual {p0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    #@3
    move-result-object v0

    #@4
    invoke-static {v0, p1, p2}, Landroid/provider/DocumentsContract;->renameDocument(Landroid/content/ContentResolver;Landroid/net/Uri;Ljava/lang/String;)Landroid/net/Uri;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method
