.class Landroid/support/v4/os/EnvironmentCompatKitKat;
.super Ljava/lang/Object;
.source "EnvironmentCompatKitKat.java"


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

.method public static getStorageState(Ljava/io/File;)Ljava/lang/String;
    .locals 1
    .param p0, "path"    # Ljava/io/File;

    #@0
    .prologue
    .line 25
    invoke-static {p0}, Landroid/os/Environment;->getStorageState(Ljava/io/File;)Ljava/lang/String;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method
