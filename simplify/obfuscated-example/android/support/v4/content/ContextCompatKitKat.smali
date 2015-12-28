.class Landroid/support/v4/content/ContextCompatKitKat;
.super Ljava/lang/Object;
.source "ContextCompatKitKat.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 23
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getExternalCacheDirs(Landroid/content/Context;)[Ljava/io/File;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 25
    invoke-virtual {p0}, Landroid/content/Context;->getExternalCacheDirs()[Ljava/io/File;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static getExternalFilesDirs(Landroid/content/Context;Ljava/lang/String;)[Ljava/io/File;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "type"    # Ljava/lang/String;

    #@0
    .prologue
    .line 29
    invoke-virtual {p0, p1}, Landroid/content/Context;->getExternalFilesDirs(Ljava/lang/String;)[Ljava/io/File;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static getObbDirs(Landroid/content/Context;)[Ljava/io/File;
    .locals 1
    .param p0, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 33
    invoke-virtual {p0}, Landroid/content/Context;->getObbDirs()[Ljava/io/File;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method
