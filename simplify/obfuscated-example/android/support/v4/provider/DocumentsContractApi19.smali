.class Landroid/support/v4/provider/DocumentsContractApi19;
.super Ljava/lang/Object;
.source "DocumentsContractApi19.java"


# static fields
.field private static final TAG:Ljava/lang/String; = "DocumentFile"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 29
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static canRead(Landroid/content/Context;Landroid/net/Uri;)Z
    .locals 3
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v0, 0x0

    #@2
    .line 76
    invoke-virtual {p0, p1, v1}, Landroid/content/Context;->checkCallingOrSelfUriPermission(Landroid/net/Uri;I)I

    #@5
    move-result v2

    #@6
    if-eqz v2, :cond_1

    #@8
    .line 86
    :cond_0
    :goto_0
    return v0

    #@9
    .line 82
    :cond_1
    invoke-static {p0, p1}, Landroid/support/v4/provider/DocumentsContractApi19;->getRawType(Landroid/content/Context;Landroid/net/Uri;)Ljava/lang/String;

    #@c
    move-result-object v2

    #@d
    invoke-static {v2}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@10
    move-result v2

    #@11
    if-nez v2, :cond_0

    #@13
    move v0, v1

    #@14
    .line 86
    goto :goto_0
.end method

.method public static canWrite(Landroid/content/Context;Landroid/net/Uri;)Z
    .locals 5
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 91
    const/4 v4, 0x2

    #@3
    invoke-virtual {p0, p1, v4}, Landroid/content/Context;->checkCallingOrSelfUriPermission(Landroid/net/Uri;I)I

    #@6
    move-result v4

    #@7
    if-eqz v4, :cond_1

    #@9
    .line 119
    :cond_0
    :goto_0
    return v2

    #@a
    .line 96
    :cond_1
    invoke-static {p0, p1}, Landroid/support/v4/provider/DocumentsContractApi19;->getRawType(Landroid/content/Context;Landroid/net/Uri;)Ljava/lang/String;

    #@d
    move-result-object v1

    #@e
    .line 97
    .local v1, "type":Ljava/lang/String;
    const-string v4, "flags"

    #@10
    invoke-static {p0, p1, v4, v2}, Landroid/support/v4/provider/DocumentsContractApi19;->queryForInt(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;I)I

    #@13
    move-result v0

    #@14
    .line 100
    .local v0, "flags":I
    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@17
    move-result v4

    #@18
    if-nez v4, :cond_0

    #@1a
    .line 105
    and-int/lit8 v4, v0, 0x4

    #@1c
    if-eqz v4, :cond_2

    #@1e
    move v2, v3

    #@1f
    .line 106
    goto :goto_0

    #@20
    .line 109
    :cond_2
    const-string v4, "vnd.android.document/directory"

    #@22
    invoke-virtual {v4, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@25
    move-result v4

    #@26
    if-eqz v4, :cond_3

    #@28
    and-int/lit8 v4, v0, 0x8

    #@2a
    if-eqz v4, :cond_3

    #@2c
    move v2, v3

    #@2d
    .line 112
    goto :goto_0

    #@2e
    .line 113
    :cond_3
    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@31
    move-result v4

    #@32
    if-nez v4, :cond_0

    #@34
    and-int/lit8 v4, v0, 0x2

    #@36
    if-eqz v4, :cond_0

    #@38
    move v2, v3

    #@39
    .line 116
    goto :goto_0
.end method

.method private static closeQuietly(Ljava/lang/AutoCloseable;)V
    .locals 2
    .param p0, "closeable"    # Ljava/lang/AutoCloseable;

    #@0
    .prologue
    .line 188
    if-eqz p0, :cond_0

    #@2
    .line 190
    :try_start_0
    invoke-interface {p0}, Ljava/lang/AutoCloseable;->close()V
    :try_end_0
    .catch Ljava/lang/RuntimeException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_1

    #@5
    .line 196
    :cond_0
    :goto_0
    return-void

    #@6
    .line 191
    :catch_0
    move-exception v0

    #@7
    .line 192
    .local v0, "rethrown":Ljava/lang/RuntimeException;
    throw v0

    #@8
    .line 193
    .end local v0    # "rethrown":Ljava/lang/RuntimeException;
    :catch_1
    move-exception v1

    #@9
    goto :goto_0
.end method

.method public static delete(Landroid/content/Context;Landroid/net/Uri;)Z
    .locals 1
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 123
    invoke-virtual {p0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    #@3
    move-result-object v0

    #@4
    invoke-static {v0, p1}, Landroid/provider/DocumentsContract;->deleteDocument(Landroid/content/ContentResolver;Landroid/net/Uri;)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public static exists(Landroid/content/Context;Landroid/net/Uri;)Z
    .locals 10
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;

    #@0
    .prologue
    const/4 v8, 0x1

    #@1
    const/4 v9, 0x0

    #@2
    .line 127
    invoke-virtual {p0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    #@5
    move-result-object v0

    #@6
    .line 129
    .local v0, "resolver":Landroid/content/ContentResolver;
    const/4 v6, 0x0

    #@7
    .line 131
    .local v6, "c":Landroid/database/Cursor;
    const/4 v1, 0x1

    #@8
    :try_start_0
    new-array v2, v1, [Ljava/lang/String;

    #@a
    const/4 v1, 0x0

    #@b
    const-string v3, "document_id"

    #@d
    aput-object v3, v2, v1

    #@f
    const/4 v3, 0x0

    #@10
    const/4 v4, 0x0

    #@11
    const/4 v5, 0x0

    #@12
    move-object v1, p1

    #@13
    invoke-virtual/range {v0 .. v5}, Landroid/content/ContentResolver;->query(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;

    #@16
    move-result-object v6

    #@17
    .line 133
    invoke-interface {v6}, Landroid/database/Cursor;->getCount()I
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@1a
    move-result v1

    #@1b
    if-lez v1, :cond_0

    #@1d
    move v1, v8

    #@1e
    .line 138
    :goto_0
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi19;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@21
    :goto_1
    return v1

    #@22
    :cond_0
    move v1, v9

    #@23
    .line 133
    goto :goto_0

    #@24
    .line 134
    :catch_0
    move-exception v7

    #@25
    .line 135
    .local v7, "e":Ljava/lang/Exception;
    :try_start_1
    const-string v1, "DocumentFile"

    #@27
    new-instance v2, Ljava/lang/StringBuilder;

    #@29
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@2c
    const-string v3, "Failed query: "

    #@2e
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@31
    move-result-object v2

    #@32
    invoke-virtual {v2, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@35
    move-result-object v2

    #@36
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@39
    move-result-object v2

    #@3a
    invoke-static {v1, v2}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@3d
    .line 138
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi19;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@40
    move v1, v9

    #@41
    goto :goto_1

    #@42
    .end local v7    # "e":Ljava/lang/Exception;
    :catchall_0
    move-exception v1

    #@43
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi19;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@46
    throw v1
.end method

.method public static getName(Landroid/content/Context;Landroid/net/Uri;)Ljava/lang/String;
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 37
    const-string v0, "_display_name"

    #@2
    const/4 v1, 0x0

    #@3
    invoke-static {p0, p1, v0, v1}, Landroid/support/v4/provider/DocumentsContractApi19;->queryForString(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    #@6
    move-result-object v0

    #@7
    return-object v0
.end method

.method private static getRawType(Landroid/content/Context;Landroid/net/Uri;)Ljava/lang/String;
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 41
    const-string v0, "mime_type"

    #@2
    const/4 v1, 0x0

    #@3
    invoke-static {p0, p1, v0, v1}, Landroid/support/v4/provider/DocumentsContractApi19;->queryForString(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    #@6
    move-result-object v0

    #@7
    return-object v0
.end method

.method public static getType(Landroid/content/Context;Landroid/net/Uri;)Ljava/lang/String;
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 45
    invoke-static {p0, p1}, Landroid/support/v4/provider/DocumentsContractApi19;->getRawType(Landroid/content/Context;Landroid/net/Uri;)Ljava/lang/String;

    #@3
    move-result-object v0

    #@4
    .line 46
    .local v0, "rawType":Ljava/lang/String;
    const-string v1, "vnd.android.document/directory"

    #@6
    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@9
    move-result v1

    #@a
    if-eqz v1, :cond_0

    #@c
    .line 47
    const/4 v0, 0x0

    #@d
    .line 49
    .end local v0    # "rawType":Ljava/lang/String;
    :cond_0
    return-object v0
.end method

.method public static isDirectory(Landroid/content/Context;Landroid/net/Uri;)Z
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 54
    const-string v0, "vnd.android.document/directory"

    #@2
    invoke-static {p0, p1}, Landroid/support/v4/provider/DocumentsContractApi19;->getRawType(Landroid/content/Context;Landroid/net/Uri;)Ljava/lang/String;

    #@5
    move-result-object v1

    #@6
    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@9
    move-result v0

    #@a
    return v0
.end method

.method public static isDocumentUri(Landroid/content/Context;Landroid/net/Uri;)Z
    .locals 1
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 33
    invoke-static {p0, p1}, Landroid/provider/DocumentsContract;->isDocumentUri(Landroid/content/Context;Landroid/net/Uri;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static isFile(Landroid/content/Context;Landroid/net/Uri;)Z
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 58
    invoke-static {p0, p1}, Landroid/support/v4/provider/DocumentsContractApi19;->getRawType(Landroid/content/Context;Landroid/net/Uri;)Ljava/lang/String;

    #@3
    move-result-object v0

    #@4
    .line 59
    .local v0, "type":Ljava/lang/String;
    const-string v1, "vnd.android.document/directory"

    #@6
    invoke-virtual {v1, v0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@9
    move-result v1

    #@a
    if-nez v1, :cond_0

    #@c
    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@f
    move-result v1

    #@10
    if-eqz v1, :cond_1

    #@12
    .line 60
    :cond_0
    const/4 v1, 0x0

    #@13
    .line 62
    :goto_0
    return v1

    #@14
    :cond_1
    const/4 v1, 0x1

    #@15
    goto :goto_0
.end method

.method public static lastModified(Landroid/content/Context;Landroid/net/Uri;)J
    .locals 4
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 67
    const-string v0, "last_modified"

    #@2
    const-wide/16 v2, 0x0

    #@4
    invoke-static {p0, p1, v0, v2, v3}, Landroid/support/v4/provider/DocumentsContractApi19;->queryForLong(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;J)J

    #@7
    move-result-wide v0

    #@8
    return-wide v0
.end method

.method public static length(Landroid/content/Context;Landroid/net/Uri;)J
    .locals 4
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 71
    const-string v0, "_size"

    #@2
    const-wide/16 v2, 0x0

    #@4
    invoke-static {p0, p1, v0, v2, v3}, Landroid/support/v4/provider/DocumentsContractApi19;->queryForLong(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;J)J

    #@7
    move-result-wide v0

    #@8
    return-wide v0
.end method

.method private static queryForInt(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;I)I
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;
    .param p2, "column"    # Ljava/lang/String;
    .param p3, "defaultValue"    # I

    #@0
    .prologue
    .line 164
    int-to-long v0, p3

    #@1
    invoke-static {p0, p1, p2, v0, v1}, Landroid/support/v4/provider/DocumentsContractApi19;->queryForLong(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;J)J

    #@4
    move-result-wide v0

    #@5
    long-to-int v0, v0

    #@6
    return v0
.end method

.method private static queryForLong(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;J)J
    .locals 9
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;
    .param p2, "column"    # Ljava/lang/String;
    .param p3, "defaultValue"    # J

    #@0
    .prologue
    .line 169
    invoke-virtual {p0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    #@3
    move-result-object v0

    #@4
    .line 171
    .local v0, "resolver":Landroid/content/ContentResolver;
    const/4 v6, 0x0

    #@5
    .line 173
    .local v6, "c":Landroid/database/Cursor;
    const/4 v1, 0x1

    #@6
    :try_start_0
    new-array v2, v1, [Ljava/lang/String;

    #@8
    const/4 v1, 0x0

    #@9
    aput-object p2, v2, v1

    #@b
    const/4 v3, 0x0

    #@c
    const/4 v4, 0x0

    #@d
    const/4 v5, 0x0

    #@e
    move-object v1, p1

    #@f
    invoke-virtual/range {v0 .. v5}, Landroid/content/ContentResolver;->query(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;

    #@12
    move-result-object v6

    #@13
    .line 174
    invoke-interface {v6}, Landroid/database/Cursor;->moveToFirst()Z

    #@16
    move-result v1

    #@17
    if-eqz v1, :cond_0

    #@19
    const/4 v1, 0x0

    #@1a
    invoke-interface {v6, v1}, Landroid/database/Cursor;->isNull(I)Z

    #@1d
    move-result v1

    #@1e
    if-nez v1, :cond_0

    #@20
    .line 175
    const/4 v1, 0x0

    #@21
    invoke-interface {v6, v1}, Landroid/database/Cursor;->getLong(I)J
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@24
    move-result-wide p3

    #@25
    .line 183
    .end local p3    # "defaultValue":J
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi19;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@28
    :goto_0
    return-wide p3

    #@29
    .restart local p3    # "defaultValue":J
    :cond_0
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi19;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@2c
    goto :goto_0

    #@2d
    .line 179
    :catch_0
    move-exception v7

    #@2e
    .line 180
    .local v7, "e":Ljava/lang/Exception;
    :try_start_1
    const-string v1, "DocumentFile"

    #@30
    new-instance v2, Ljava/lang/StringBuilder;

    #@32
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@35
    const-string v3, "Failed query: "

    #@37
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@3a
    move-result-object v2

    #@3b
    invoke-virtual {v2, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@3e
    move-result-object v2

    #@3f
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@42
    move-result-object v2

    #@43
    invoke-static {v1, v2}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@46
    .line 183
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi19;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@49
    goto :goto_0

    #@4a
    .end local v7    # "e":Ljava/lang/Exception;
    :catchall_0
    move-exception v1

    #@4b
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi19;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@4e
    throw v1
.end method

.method private static queryForString(Landroid/content/Context;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    .locals 8
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "self"    # Landroid/net/Uri;
    .param p2, "column"    # Ljava/lang/String;
    .param p3, "defaultValue"    # Ljava/lang/String;

    #@0
    .prologue
    .line 144
    invoke-virtual {p0}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    #@3
    move-result-object v0

    #@4
    .line 146
    .local v0, "resolver":Landroid/content/ContentResolver;
    const/4 v6, 0x0

    #@5
    .line 148
    .local v6, "c":Landroid/database/Cursor;
    const/4 v1, 0x1

    #@6
    :try_start_0
    new-array v2, v1, [Ljava/lang/String;

    #@8
    const/4 v1, 0x0

    #@9
    aput-object p2, v2, v1

    #@b
    const/4 v3, 0x0

    #@c
    const/4 v4, 0x0

    #@d
    const/4 v5, 0x0

    #@e
    move-object v1, p1

    #@f
    invoke-virtual/range {v0 .. v5}, Landroid/content/ContentResolver;->query(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;

    #@12
    move-result-object v6

    #@13
    .line 149
    invoke-interface {v6}, Landroid/database/Cursor;->moveToFirst()Z

    #@16
    move-result v1

    #@17
    if-eqz v1, :cond_0

    #@19
    const/4 v1, 0x0

    #@1a
    invoke-interface {v6, v1}, Landroid/database/Cursor;->isNull(I)Z

    #@1d
    move-result v1

    #@1e
    if-nez v1, :cond_0

    #@20
    .line 150
    const/4 v1, 0x0

    #@21
    invoke-interface {v6, v1}, Landroid/database/Cursor;->getString(I)Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@24
    move-result-object p3

    #@25
    .line 158
    .end local p3    # "defaultValue":Ljava/lang/String;
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi19;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@28
    :goto_0
    return-object p3

    #@29
    .restart local p3    # "defaultValue":Ljava/lang/String;
    :cond_0
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi19;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@2c
    goto :goto_0

    #@2d
    .line 154
    :catch_0
    move-exception v7

    #@2e
    .line 155
    .local v7, "e":Ljava/lang/Exception;
    :try_start_1
    const-string v1, "DocumentFile"

    #@30
    new-instance v2, Ljava/lang/StringBuilder;

    #@32
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@35
    const-string v3, "Failed query: "

    #@37
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@3a
    move-result-object v2

    #@3b
    invoke-virtual {v2, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@3e
    move-result-object v2

    #@3f
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@42
    move-result-object v2

    #@43
    invoke-static {v1, v2}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@46
    .line 158
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi19;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@49
    goto :goto_0

    #@4a
    .end local v7    # "e":Ljava/lang/Exception;
    :catchall_0
    move-exception v1

    #@4b
    invoke-static {v6}, Landroid/support/v4/provider/DocumentsContractApi19;->closeQuietly(Ljava/lang/AutoCloseable;)V

    #@4e
    throw v1
.end method
