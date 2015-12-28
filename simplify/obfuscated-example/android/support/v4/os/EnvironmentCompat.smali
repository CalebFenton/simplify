.class public Landroid/support/v4/os/EnvironmentCompat;
.super Ljava/lang/Object;
.source "EnvironmentCompat.java"


# static fields
.field public static final MEDIA_UNKNOWN:Ljava/lang/String; = "unknown"

.field private static final TAG:Ljava/lang/String; = "EnvironmentCompat"


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 30
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getStorageState(Ljava/io/File;)Ljava/lang/String;
    .locals 7
    .param p0, "path"    # Ljava/io/File;

    #@0
    .prologue
    .line 56
    sget v3, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    .line 57
    .local v3, "version":I
    const/16 v4, 0x13

    #@4
    if-lt v3, v4, :cond_0

    #@6
    .line 58
    invoke-static {p0}, Landroid/support/v4/os/EnvironmentCompatKitKat;->getStorageState(Ljava/io/File;)Ljava/lang/String;

    #@9
    move-result-object v4

    #@a
    .line 73
    :goto_0
    return-object v4

    #@b
    .line 62
    :cond_0
    :try_start_0
    invoke-virtual {p0}, Ljava/io/File;->getCanonicalPath()Ljava/lang/String;

    #@e
    move-result-object v1

    #@f
    .line 63
    .local v1, "canonicalPath":Ljava/lang/String;
    invoke-static {}, Landroid/os/Environment;->getExternalStorageDirectory()Ljava/io/File;

    #@12
    move-result-object v4

    #@13
    invoke-virtual {v4}, Ljava/io/File;->getCanonicalPath()Ljava/lang/String;

    #@16
    move-result-object v0

    #@17
    .line 66
    .local v0, "canonicalExternal":Ljava/lang/String;
    invoke-virtual {v1, v0}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    #@1a
    move-result v4

    #@1b
    if-eqz v4, :cond_1

    #@1d
    .line 67
    invoke-static {}, Landroid/os/Environment;->getExternalStorageState()Ljava/lang/String;
    :try_end_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_0

    #@20
    move-result-object v4

    #@21
    goto :goto_0

    #@22
    .line 69
    .end local v0    # "canonicalExternal":Ljava/lang/String;
    .end local v1    # "canonicalPath":Ljava/lang/String;
    :catch_0
    move-exception v2

    #@23
    .line 70
    .local v2, "e":Ljava/io/IOException;
    const-string v4, "EnvironmentCompat"

    #@25
    new-instance v5, Ljava/lang/StringBuilder;

    #@27
    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    #@2a
    const-string v6, "Failed to resolve canonical path: "

    #@2c
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2f
    move-result-object v5

    #@30
    invoke-virtual {v5, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@33
    move-result-object v5

    #@34
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@37
    move-result-object v5

    #@38
    invoke-static {v4, v5}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@3b
    .line 73
    .end local v2    # "e":Ljava/io/IOException;
    :cond_1
    const-string v4, "unknown"

    #@3d
    goto :goto_0
.end method
