.class public Landroid/support/v4/content/ContextCompat;
.super Ljava/lang/Object;
.source "ContextCompat.java"


# static fields
.field private static final DIR_ANDROID:Ljava/lang/String; = "Android"

.field private static final DIR_CACHE:Ljava/lang/String; = "cache"

.field private static final DIR_DATA:Ljava/lang/String; = "data"

.field private static final DIR_FILES:Ljava/lang/String; = "files"

.field private static final DIR_OBB:Ljava/lang/String; = "obb"

.field private static final TAG:Ljava/lang/String; = "ContextCompat"


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 36
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method private static varargs buildPath(Ljava/io/File;[Ljava/lang/String;)Ljava/io/File;
    .locals 6
    .param p0, "base"    # Ljava/io/File;
    .param p1, "segments"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 294
    move-object v1, p0

    #@1
    .line 295
    .local v1, "cur":Ljava/io/File;
    move-object v0, p1

    #@2
    .local v0, "arr$":[Ljava/lang/String;
    array-length v4, v0

    #@3
    .local v4, "len$":I
    const/4 v3, 0x0

    #@4
    .local v3, "i$":I
    move-object v2, v1

    #@5
    .end local v1    # "cur":Ljava/io/File;
    .local v2, "cur":Ljava/io/File;
    :goto_0
    if-ge v3, v4, :cond_1

    #@7
    aget-object v5, v0, v3

    #@9
    .line 296
    .local v5, "segment":Ljava/lang/String;
    if-nez v2, :cond_0

    #@b
    .line 297
    new-instance v1, Ljava/io/File;

    #@d
    invoke-direct {v1, v5}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    #@10
    .line 295
    .end local v2    # "cur":Ljava/io/File;
    .restart local v1    # "cur":Ljava/io/File;
    :goto_1
    add-int/lit8 v3, v3, 0x1

    #@12
    move-object v2, v1

    #@13
    .end local v1    # "cur":Ljava/io/File;
    .restart local v2    # "cur":Ljava/io/File;
    goto :goto_0

    #@14
    .line 298
    :cond_0
    if-eqz v5, :cond_2

    #@16
    .line 299
    new-instance v1, Ljava/io/File;

    #@18
    invoke-direct {v1, v2, v5}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V

    #@1b
    .end local v2    # "cur":Ljava/io/File;
    .restart local v1    # "cur":Ljava/io/File;
    goto :goto_1

    #@1c
    .line 302
    .end local v1    # "cur":Ljava/io/File;
    .end local v5    # "segment":Ljava/lang/String;
    .restart local v2    # "cur":Ljava/io/File;
    :cond_1
    return-object v2

    #@1d
    .restart local v5    # "segment":Ljava/lang/String;
    :cond_2
    move-object v1, v2

    #@1e
    .end local v2    # "cur":Ljava/io/File;
    .restart local v1    # "cur":Ljava/io/File;
    goto :goto_1
.end method

.method private static declared-synchronized createFilesDir(Ljava/io/File;)Ljava/io/File;
    .locals 4
    .param p0, "file"    # Ljava/io/File;

    #@0
    .prologue
    .line 377
    const-class v1, Landroid/support/v4/content/ContextCompat;

    #@2
    monitor-enter v1

    #@3
    :try_start_0
    invoke-virtual {p0}, Ljava/io/File;->exists()Z

    #@6
    move-result v0

    #@7
    if-nez v0, :cond_0

    #@9
    .line 378
    invoke-virtual {p0}, Ljava/io/File;->mkdirs()Z

    #@c
    move-result v0

    #@d
    if-nez v0, :cond_0

    #@f
    .line 379
    invoke-virtual {p0}, Ljava/io/File;->exists()Z
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@12
    move-result v0

    #@13
    if-eqz v0, :cond_1

    #@15
    .line 387
    .end local p0    # "file":Ljava/io/File;
    :cond_0
    :goto_0
    monitor-exit v1

    #@16
    return-object p0

    #@17
    .line 383
    .restart local p0    # "file":Ljava/io/File;
    :cond_1
    :try_start_1
    const-string v0, "ContextCompat"

    #@19
    new-instance v2, Ljava/lang/StringBuilder;

    #@1b
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@1e
    const-string v3, "Unable to create files subdir "

    #@20
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@23
    move-result-object v2

    #@24
    invoke-virtual {p0}, Ljava/io/File;->getPath()Ljava/lang/String;

    #@27
    move-result-object v3

    #@28
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2b
    move-result-object v2

    #@2c
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@2f
    move-result-object v2

    #@30
    invoke-static {v0, v2}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@33
    .line 384
    const/4 p0, 0x0

    #@34
    goto :goto_0

    #@35
    .line 377
    :catchall_0
    move-exception v0

    #@36
    monitor-exit v1

    #@37
    throw v0
.end method

.method public static final getDrawable(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "id"    # I

    #@0
    .prologue
    .line 317
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    .line 318
    .local v0, "version":I
    const/16 v1, 0x15

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 319
    invoke-static {p0, p1}, Landroid/support/v4/content/ContextCompatApi21;->getDrawable(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;

    #@9
    move-result-object v1

    #@a
    .line 321
    :goto_0
    return-object v1

    #@b
    :cond_0
    invoke-virtual {p0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@e
    move-result-object v1

    #@f
    invoke-virtual {v1, p1}, Landroid/content/res/Resources;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@12
    move-result-object v1

    #@13
    goto :goto_0
.end method

.method public static getExternalCacheDirs(Landroid/content/Context;)[Ljava/io/File;
    .locals 8
    .param p0, "context"    # Landroid/content/Context;

    #@0
    .prologue
    const/4 v7, 0x1

    #@1
    const/4 v6, 0x0

    #@2
    .line 278
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@4
    .line 279
    .local v1, "version":I
    const/16 v2, 0x13

    #@6
    if-lt v1, v2, :cond_0

    #@8
    .line 280
    invoke-static {p0}, Landroid/support/v4/content/ContextCompatKitKat;->getExternalCacheDirs(Landroid/content/Context;)[Ljava/io/File;

    #@b
    move-result-object v2

    #@c
    .line 289
    :goto_0
    return-object v2

    #@d
    .line 283
    :cond_0
    const/16 v2, 0x8

    #@f
    if-lt v1, v2, :cond_1

    #@11
    .line 284
    invoke-static {p0}, Landroid/support/v4/content/ContextCompatFroyo;->getExternalCacheDir(Landroid/content/Context;)Ljava/io/File;

    #@14
    move-result-object v0

    #@15
    .line 289
    .local v0, "single":Ljava/io/File;
    :goto_1
    new-array v2, v7, [Ljava/io/File;

    #@17
    aput-object v0, v2, v6

    #@19
    goto :goto_0

    #@1a
    .line 286
    .end local v0    # "single":Ljava/io/File;
    :cond_1
    invoke-static {}, Landroid/os/Environment;->getExternalStorageDirectory()Ljava/io/File;

    #@1d
    move-result-object v2

    #@1e
    const/4 v3, 0x4

    #@1f
    new-array v3, v3, [Ljava/lang/String;

    #@21
    const-string v4, "Android"

    #@23
    aput-object v4, v3, v6

    #@25
    const-string v4, "data"

    #@27
    aput-object v4, v3, v7

    #@29
    const/4 v4, 0x2

    #@2a
    invoke-virtual {p0}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    #@2d
    move-result-object v5

    #@2e
    aput-object v5, v3, v4

    #@30
    const/4 v4, 0x3

    #@31
    const-string v5, "cache"

    #@33
    aput-object v5, v3, v4

    #@35
    invoke-static {v2, v3}, Landroid/support/v4/content/ContextCompat;->buildPath(Ljava/io/File;[Ljava/lang/String;)Ljava/io/File;

    #@38
    move-result-object v0

    #@39
    .restart local v0    # "single":Ljava/io/File;
    goto :goto_1
.end method

.method public static getExternalFilesDirs(Landroid/content/Context;Ljava/lang/String;)[Ljava/io/File;
    .locals 8
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "type"    # Ljava/lang/String;

    #@0
    .prologue
    const/4 v7, 0x1

    #@1
    const/4 v6, 0x0

    #@2
    .line 218
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@4
    .line 219
    .local v1, "version":I
    const/16 v2, 0x13

    #@6
    if-lt v1, v2, :cond_0

    #@8
    .line 220
    invoke-static {p0, p1}, Landroid/support/v4/content/ContextCompatKitKat;->getExternalFilesDirs(Landroid/content/Context;Ljava/lang/String;)[Ljava/io/File;

    #@b
    move-result-object v2

    #@c
    .line 229
    :goto_0
    return-object v2

    #@d
    .line 223
    :cond_0
    const/16 v2, 0x8

    #@f
    if-lt v1, v2, :cond_1

    #@11
    .line 224
    invoke-static {p0, p1}, Landroid/support/v4/content/ContextCompatFroyo;->getExternalFilesDir(Landroid/content/Context;Ljava/lang/String;)Ljava/io/File;

    #@14
    move-result-object v0

    #@15
    .line 229
    .local v0, "single":Ljava/io/File;
    :goto_1
    new-array v2, v7, [Ljava/io/File;

    #@17
    aput-object v0, v2, v6

    #@19
    goto :goto_0

    #@1a
    .line 226
    .end local v0    # "single":Ljava/io/File;
    :cond_1
    invoke-static {}, Landroid/os/Environment;->getExternalStorageDirectory()Ljava/io/File;

    #@1d
    move-result-object v2

    #@1e
    const/4 v3, 0x5

    #@1f
    new-array v3, v3, [Ljava/lang/String;

    #@21
    const-string v4, "Android"

    #@23
    aput-object v4, v3, v6

    #@25
    const-string v4, "data"

    #@27
    aput-object v4, v3, v7

    #@29
    const/4 v4, 0x2

    #@2a
    invoke-virtual {p0}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    #@2d
    move-result-object v5

    #@2e
    aput-object v5, v3, v4

    #@30
    const/4 v4, 0x3

    #@31
    const-string v5, "files"

    #@33
    aput-object v5, v3, v4

    #@35
    const/4 v4, 0x4

    #@36
    aput-object p1, v3, v4

    #@38
    invoke-static {v2, v3}, Landroid/support/v4/content/ContextCompat;->buildPath(Ljava/io/File;[Ljava/lang/String;)Ljava/io/File;

    #@3b
    move-result-object v0

    #@3c
    .restart local v0    # "single":Ljava/io/File;
    goto :goto_1
.end method

.method public static getObbDirs(Landroid/content/Context;)[Ljava/io/File;
    .locals 8
    .param p0, "context"    # Landroid/content/Context;

    #@0
    .prologue
    const/4 v7, 0x1

    #@1
    const/4 v6, 0x0

    #@2
    .line 158
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@4
    .line 159
    .local v1, "version":I
    const/16 v2, 0x13

    #@6
    if-lt v1, v2, :cond_0

    #@8
    .line 160
    invoke-static {p0}, Landroid/support/v4/content/ContextCompatKitKat;->getObbDirs(Landroid/content/Context;)[Ljava/io/File;

    #@b
    move-result-object v2

    #@c
    .line 169
    :goto_0
    return-object v2

    #@d
    .line 163
    :cond_0
    const/16 v2, 0xb

    #@f
    if-lt v1, v2, :cond_1

    #@11
    .line 164
    invoke-static {p0}, Landroid/support/v4/content/ContextCompatHoneycomb;->getObbDir(Landroid/content/Context;)Ljava/io/File;

    #@14
    move-result-object v0

    #@15
    .line 169
    .local v0, "single":Ljava/io/File;
    :goto_1
    new-array v2, v7, [Ljava/io/File;

    #@17
    aput-object v0, v2, v6

    #@19
    goto :goto_0

    #@1a
    .line 166
    .end local v0    # "single":Ljava/io/File;
    :cond_1
    invoke-static {}, Landroid/os/Environment;->getExternalStorageDirectory()Ljava/io/File;

    #@1d
    move-result-object v2

    #@1e
    const/4 v3, 0x3

    #@1f
    new-array v3, v3, [Ljava/lang/String;

    #@21
    const-string v4, "Android"

    #@23
    aput-object v4, v3, v6

    #@25
    const-string v4, "obb"

    #@27
    aput-object v4, v3, v7

    #@29
    const/4 v4, 0x2

    #@2a
    invoke-virtual {p0}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    #@2d
    move-result-object v5

    #@2e
    aput-object v5, v3, v4

    #@30
    invoke-static {v2, v3}, Landroid/support/v4/content/ContextCompat;->buildPath(Ljava/io/File;[Ljava/lang/String;)Ljava/io/File;

    #@33
    move-result-object v0

    #@34
    .restart local v0    # "single":Ljava/io/File;
    goto :goto_1
.end method

.method public static startActivities(Landroid/content/Context;[Landroid/content/Intent;)Z
    .locals 1
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "intents"    # [Landroid/content/Intent;

    #@0
    .prologue
    .line 71
    const/4 v0, 0x0

    #@1
    invoke-static {p0, p1, v0}, Landroid/support/v4/content/ContextCompat;->startActivities(Landroid/content/Context;[Landroid/content/Intent;Landroid/os/Bundle;)Z

    #@4
    move-result v0

    #@5
    return v0
.end method

.method public static startActivities(Landroid/content/Context;[Landroid/content/Intent;Landroid/os/Bundle;)Z
    .locals 3
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "intents"    # [Landroid/content/Intent;
    .param p2, "options"    # Landroid/os/Bundle;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 103
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@3
    .line 104
    .local v0, "version":I
    const/16 v2, 0x10

    #@5
    if-lt v0, v2, :cond_0

    #@7
    .line 105
    invoke-static {p0, p1, p2}, Landroid/support/v4/content/ContextCompatJellybean;->startActivities(Landroid/content/Context;[Landroid/content/Intent;Landroid/os/Bundle;)V

    #@a
    .line 111
    :goto_0
    return v1

    #@b
    .line 107
    :cond_0
    const/16 v2, 0xb

    #@d
    if-lt v0, v2, :cond_1

    #@f
    .line 108
    invoke-static {p0, p1}, Landroid/support/v4/content/ContextCompatHoneycomb;->startActivities(Landroid/content/Context;[Landroid/content/Intent;)V

    #@12
    goto :goto_0

    #@13
    .line 111
    :cond_1
    const/4 v1, 0x0

    #@14
    goto :goto_0
.end method


# virtual methods
.method public final getCodeCacheDir(Landroid/content/Context;)Ljava/io/File;
    .locals 5
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 367
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    .line 368
    .local v1, "version":I
    const/16 v2, 0x15

    #@4
    if-lt v1, v2, :cond_0

    #@6
    .line 369
    invoke-static {p1}, Landroid/support/v4/content/ContextCompatApi21;->getCodeCacheDir(Landroid/content/Context;)Ljava/io/File;

    #@9
    move-result-object v2

    #@a
    .line 372
    :goto_0
    return-object v2

    #@b
    .line 371
    :cond_0
    invoke-virtual {p1}, Landroid/content/Context;->getApplicationInfo()Landroid/content/pm/ApplicationInfo;

    #@e
    move-result-object v0

    #@f
    .line 372
    .local v0, "appInfo":Landroid/content/pm/ApplicationInfo;
    new-instance v2, Ljava/io/File;

    #@11
    iget-object v3, v0, Landroid/content/pm/ApplicationInfo;->dataDir:Ljava/lang/String;

    #@13
    const-string v4, "code_cache"

    #@15
    invoke-direct {v2, v3, v4}, Ljava/io/File;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    #@18
    invoke-static {v2}, Landroid/support/v4/content/ContextCompat;->createFilesDir(Ljava/io/File;)Ljava/io/File;

    #@1b
    move-result-object v2

    #@1c
    goto :goto_0
.end method

.method public final getNoBackupFilesDir(Landroid/content/Context;)Ljava/io/File;
    .locals 5
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 342
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    .line 343
    .local v1, "version":I
    const/16 v2, 0x15

    #@4
    if-lt v1, v2, :cond_0

    #@6
    .line 344
    invoke-static {p1}, Landroid/support/v4/content/ContextCompatApi21;->getNoBackupFilesDir(Landroid/content/Context;)Ljava/io/File;

    #@9
    move-result-object v2

    #@a
    .line 347
    :goto_0
    return-object v2

    #@b
    .line 346
    :cond_0
    invoke-virtual {p1}, Landroid/content/Context;->getApplicationInfo()Landroid/content/pm/ApplicationInfo;

    #@e
    move-result-object v0

    #@f
    .line 347
    .local v0, "appInfo":Landroid/content/pm/ApplicationInfo;
    new-instance v2, Ljava/io/File;

    #@11
    iget-object v3, v0, Landroid/content/pm/ApplicationInfo;->dataDir:Ljava/lang/String;

    #@13
    const-string v4, "no_backup"

    #@15
    invoke-direct {v2, v3, v4}, Ljava/io/File;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    #@18
    invoke-static {v2}, Landroid/support/v4/content/ContextCompat;->createFilesDir(Ljava/io/File;)Ljava/io/File;

    #@1b
    move-result-object v2

    #@1c
    goto :goto_0
.end method
