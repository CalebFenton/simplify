.class public Landroid/support/v4/content/FileProvider;
.super Landroid/content/ContentProvider;
.source "FileProvider.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/content/FileProvider$SimplePathStrategy;,
        Landroid/support/v4/content/FileProvider$PathStrategy;
    }
.end annotation


# static fields
.field private static final ATTR_NAME:Ljava/lang/String; = "name"

.field private static final ATTR_PATH:Ljava/lang/String; = "path"

.field private static final COLUMNS:[Ljava/lang/String;

.field private static final DEVICE_ROOT:Ljava/io/File;

.field private static final META_DATA_FILE_PROVIDER_PATHS:Ljava/lang/String; = "android.support.FILE_PROVIDER_PATHS"

.field private static final TAG_CACHE_PATH:Ljava/lang/String; = "cache-path"

.field private static final TAG_EXTERNAL:Ljava/lang/String; = "external-path"

.field private static final TAG_FILES_PATH:Ljava/lang/String; = "files-path"

.field private static final TAG_ROOT_PATH:Ljava/lang/String; = "root-path"

.field private static sCache:Ljava/util/HashMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/HashMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/support/v4/content/FileProvider$PathStrategy;",
            ">;"
        }
    .end annotation
.end field


# instance fields
.field private mStrategy:Landroid/support/v4/content/FileProvider$PathStrategy;


# direct methods
.method static constructor <clinit>()V
    .locals 3

    #@0
    .prologue
    .line 303
    const/4 v0, 0x2

    #@1
    new-array v0, v0, [Ljava/lang/String;

    #@3
    const/4 v1, 0x0

    #@4
    const-string v2, "_display_name"

    #@6
    aput-object v2, v0, v1

    #@8
    const/4 v1, 0x1

    #@9
    const-string v2, "_size"

    #@b
    aput-object v2, v0, v1

    #@d
    sput-object v0, Landroid/support/v4/content/FileProvider;->COLUMNS:[Ljava/lang/String;

    #@f
    .line 317
    new-instance v0, Ljava/io/File;

    #@11
    const-string v1, "/"

    #@13
    invoke-direct {v0, v1}, Ljava/io/File;-><init>(Ljava/lang/String;)V

    #@16
    sput-object v0, Landroid/support/v4/content/FileProvider;->DEVICE_ROOT:Ljava/io/File;

    #@18
    .line 320
    new-instance v0, Ljava/util/HashMap;

    #@1a
    invoke-direct {v0}, Ljava/util/HashMap;-><init>()V

    #@1d
    sput-object v0, Landroid/support/v4/content/FileProvider;->sCache:Ljava/util/HashMap;

    #@1f
    return-void
.end method

.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 302
    invoke-direct {p0}, Landroid/content/ContentProvider;-><init>()V

    #@3
    .line 630
    return-void
.end method

.method private static varargs buildPath(Ljava/io/File;[Ljava/lang/String;)Ljava/io/File;
    .locals 6
    .param p0, "base"    # Ljava/io/File;
    .param p1, "segments"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 753
    move-object v1, p0

    #@1
    .line 754
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
    if-ge v3, v4, :cond_0

    #@7
    aget-object v5, v0, v3

    #@9
    .line 755
    .local v5, "segment":Ljava/lang/String;
    if-eqz v5, :cond_1

    #@b
    .line 756
    new-instance v1, Ljava/io/File;

    #@d
    invoke-direct {v1, v2, v5}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V

    #@10
    .line 754
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
    .line 759
    .end local v5    # "segment":Ljava/lang/String;
    :cond_0
    return-object v2

    #@15
    .restart local v5    # "segment":Ljava/lang/String;
    :cond_1
    move-object v1, v2

    #@16
    .end local v2    # "cur":Ljava/io/File;
    .restart local v1    # "cur":Ljava/io/File;
    goto :goto_1
.end method

.method private static copyOf([Ljava/lang/Object;I)[Ljava/lang/Object;
    .locals 2
    .param p0, "original"    # [Ljava/lang/Object;
    .param p1, "newLength"    # I

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 769
    new-array v0, p1, [Ljava/lang/Object;

    #@3
    .line 770
    .local v0, "result":[Ljava/lang/Object;
    invoke-static {p0, v1, v0, v1, p1}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@6
    .line 771
    return-object v0
.end method

.method private static copyOf([Ljava/lang/String;I)[Ljava/lang/String;
    .locals 2
    .param p0, "original"    # [Ljava/lang/String;
    .param p1, "newLength"    # I

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 763
    new-array v0, p1, [Ljava/lang/String;

    #@3
    .line 764
    .local v0, "result":[Ljava/lang/String;
    invoke-static {p0, v1, v0, v1, p1}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@6
    .line 765
    return-object v0
.end method

.method private static getPathStrategy(Landroid/content/Context;Ljava/lang/String;)Landroid/support/v4/content/FileProvider$PathStrategy;
    .locals 5
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "authority"    # Ljava/lang/String;

    #@0
    .prologue
    .line 530
    sget-object v3, Landroid/support/v4/content/FileProvider;->sCache:Ljava/util/HashMap;

    #@2
    monitor-enter v3

    #@3
    .line 531
    :try_start_0
    sget-object v2, Landroid/support/v4/content/FileProvider;->sCache:Ljava/util/HashMap;

    #@5
    invoke-virtual {v2, p1}, Ljava/util/HashMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@8
    move-result-object v1

    #@9
    check-cast v1, Landroid/support/v4/content/FileProvider$PathStrategy;
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@b
    .line 532
    .local v1, "strat":Landroid/support/v4/content/FileProvider$PathStrategy;
    if-nez v1, :cond_0

    #@d
    .line 534
    :try_start_1
    invoke-static {p0, p1}, Landroid/support/v4/content/FileProvider;->parsePathStrategy(Landroid/content/Context;Ljava/lang/String;)Landroid/support/v4/content/FileProvider$PathStrategy;
    :try_end_1
    .catch Ljava/io/IOException; {:try_start_1 .. :try_end_1} :catch_0
    .catch Lorg/xmlpull/v1/XmlPullParserException; {:try_start_1 .. :try_end_1} :catch_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@10
    move-result-object v1

    #@11
    .line 542
    :try_start_2
    sget-object v2, Landroid/support/v4/content/FileProvider;->sCache:Ljava/util/HashMap;

    #@13
    invoke-virtual {v2, p1, v1}, Ljava/util/HashMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@16
    .line 544
    :cond_0
    monitor-exit v3

    #@17
    .line 545
    return-object v1

    #@18
    .line 535
    :catch_0
    move-exception v0

    #@19
    .line 536
    .local v0, "e":Ljava/io/IOException;
    new-instance v2, Ljava/lang/IllegalArgumentException;

    #@1b
    const-string v4, "Failed to parse android.support.FILE_PROVIDER_PATHS meta-data"

    #@1d
    invoke-direct {v2, v4, v0}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;Ljava/lang/Throwable;)V

    #@20
    throw v2

    #@21
    .line 544
    .end local v0    # "e":Ljava/io/IOException;
    .end local v1    # "strat":Landroid/support/v4/content/FileProvider$PathStrategy;
    :catchall_0
    move-exception v2

    #@22
    monitor-exit v3
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    #@23
    throw v2

    #@24
    .line 538
    .restart local v1    # "strat":Landroid/support/v4/content/FileProvider$PathStrategy;
    :catch_1
    move-exception v0

    #@25
    .line 539
    .local v0, "e":Lorg/xmlpull/v1/XmlPullParserException;
    :try_start_3
    new-instance v2, Ljava/lang/IllegalArgumentException;

    #@27
    const-string v4, "Failed to parse android.support.FILE_PROVIDER_PATHS meta-data"

    #@29
    invoke-direct {v2, v4, v0}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;Ljava/lang/Throwable;)V

    #@2c
    throw v2
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_0
.end method

.method public static getUriForFile(Landroid/content/Context;Ljava/lang/String;Ljava/io/File;)Landroid/net/Uri;
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "authority"    # Ljava/lang/String;
    .param p2, "file"    # Ljava/io/File;

    #@0
    .prologue
    .line 376
    invoke-static {p0, p1}, Landroid/support/v4/content/FileProvider;->getPathStrategy(Landroid/content/Context;Ljava/lang/String;)Landroid/support/v4/content/FileProvider$PathStrategy;

    #@3
    move-result-object v0

    #@4
    .line 377
    .local v0, "strategy":Landroid/support/v4/content/FileProvider$PathStrategy;
    invoke-interface {v0, p2}, Landroid/support/v4/content/FileProvider$PathStrategy;->getUriForFile(Ljava/io/File;)Landroid/net/Uri;

    #@7
    move-result-object v1

    #@8
    return-object v1
.end method

.method private static modeToMode(Ljava/lang/String;)I
    .locals 4
    .param p0, "mode"    # Ljava/lang/String;

    #@0
    .prologue
    .line 729
    const-string v1, "r"

    #@2
    invoke-virtual {v1, p0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@5
    move-result v1

    #@6
    if-eqz v1, :cond_0

    #@8
    .line 730
    const/high16 v0, 0x10000000

    #@a
    .line 749
    .local v0, "modeBits":I
    :goto_0
    return v0

    #@b
    .line 731
    .end local v0    # "modeBits":I
    :cond_0
    const-string v1, "w"

    #@d
    invoke-virtual {v1, p0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@10
    move-result v1

    #@11
    if-nez v1, :cond_1

    #@13
    const-string v1, "wt"

    #@15
    invoke-virtual {v1, p0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@18
    move-result v1

    #@19
    if-eqz v1, :cond_2

    #@1b
    .line 732
    :cond_1
    const/high16 v0, 0x2c000000

    #@1d
    .restart local v0    # "modeBits":I
    goto :goto_0

    #@1e
    .line 735
    .end local v0    # "modeBits":I
    :cond_2
    const-string v1, "wa"

    #@20
    invoke-virtual {v1, p0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@23
    move-result v1

    #@24
    if-eqz v1, :cond_3

    #@26
    .line 736
    const/high16 v0, 0x2a000000

    #@28
    .restart local v0    # "modeBits":I
    goto :goto_0

    #@29
    .line 739
    .end local v0    # "modeBits":I
    :cond_3
    const-string v1, "rw"

    #@2b
    invoke-virtual {v1, p0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@2e
    move-result v1

    #@2f
    if-eqz v1, :cond_4

    #@31
    .line 740
    const/high16 v0, 0x38000000

    #@33
    .restart local v0    # "modeBits":I
    goto :goto_0

    #@34
    .line 742
    .end local v0    # "modeBits":I
    :cond_4
    const-string v1, "rwt"

    #@36
    invoke-virtual {v1, p0}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@39
    move-result v1

    #@3a
    if-eqz v1, :cond_5

    #@3c
    .line 743
    const/high16 v0, 0x3c000000    # 0.0078125f

    #@3e
    .restart local v0    # "modeBits":I
    goto :goto_0

    #@3f
    .line 747
    .end local v0    # "modeBits":I
    :cond_5
    new-instance v1, Ljava/lang/IllegalArgumentException;

    #@41
    new-instance v2, Ljava/lang/StringBuilder;

    #@43
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@46
    const-string v3, "Invalid mode: "

    #@48
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@4b
    move-result-object v2

    #@4c
    invoke-virtual {v2, p0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@4f
    move-result-object v2

    #@50
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@53
    move-result-object v2

    #@54
    invoke-direct {v1, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@57
    throw v1
.end method

.method private static parsePathStrategy(Landroid/content/Context;Ljava/lang/String;)Landroid/support/v4/content/FileProvider$PathStrategy;
    .locals 13
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "authority"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;,
            Lorg/xmlpull/v1/XmlPullParserException;
        }
    .end annotation

    #@0
    .prologue
    const/4 v12, 0x0

    #@1
    const/4 v11, 0x0

    #@2
    const/4 v10, 0x1

    #@3
    .line 556
    new-instance v4, Landroid/support/v4/content/FileProvider$SimplePathStrategy;

    #@5
    invoke-direct {v4, p1}, Landroid/support/v4/content/FileProvider$SimplePathStrategy;-><init>(Ljava/lang/String;)V

    #@8
    .line 558
    .local v4, "strat":Landroid/support/v4/content/FileProvider$SimplePathStrategy;
    invoke-virtual {p0}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    #@b
    move-result-object v8

    #@c
    const/16 v9, 0x80

    #@e
    invoke-virtual {v8, p1, v9}, Landroid/content/pm/PackageManager;->resolveContentProvider(Ljava/lang/String;I)Landroid/content/pm/ProviderInfo;

    #@11
    move-result-object v1

    #@12
    .line 560
    .local v1, "info":Landroid/content/pm/ProviderInfo;
    invoke-virtual {p0}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    #@15
    move-result-object v8

    #@16
    const-string v9, "android.support.FILE_PROVIDER_PATHS"

    #@18
    invoke-virtual {v1, v8, v9}, Landroid/content/pm/ProviderInfo;->loadXmlMetaData(Landroid/content/pm/PackageManager;Ljava/lang/String;)Landroid/content/res/XmlResourceParser;

    #@1b
    move-result-object v0

    #@1c
    .line 562
    .local v0, "in":Landroid/content/res/XmlResourceParser;
    if-nez v0, :cond_0

    #@1e
    .line 563
    new-instance v8, Ljava/lang/IllegalArgumentException;

    #@20
    const-string v9, "Missing android.support.FILE_PROVIDER_PATHS meta-data"

    #@22
    invoke-direct {v8, v9}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@25
    throw v8

    #@26
    .line 568
    :cond_0
    :goto_0
    invoke-interface {v0}, Landroid/content/res/XmlResourceParser;->next()I

    #@29
    move-result v7

    #@2a
    .local v7, "type":I
    if-eq v7, v10, :cond_5

    #@2c
    .line 569
    const/4 v8, 0x2

    #@2d
    if-ne v7, v8, :cond_0

    #@2f
    .line 570
    invoke-interface {v0}, Landroid/content/res/XmlResourceParser;->getName()Ljava/lang/String;

    #@32
    move-result-object v5

    #@33
    .line 572
    .local v5, "tag":Ljava/lang/String;
    const-string v8, "name"

    #@35
    invoke-interface {v0, v12, v8}, Landroid/content/res/XmlResourceParser;->getAttributeValue(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    #@38
    move-result-object v2

    #@39
    .line 573
    .local v2, "name":Ljava/lang/String;
    const-string v8, "path"

    #@3b
    invoke-interface {v0, v12, v8}, Landroid/content/res/XmlResourceParser;->getAttributeValue(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    #@3e
    move-result-object v3

    #@3f
    .line 575
    .local v3, "path":Ljava/lang/String;
    const/4 v6, 0x0

    #@40
    .line 576
    .local v6, "target":Ljava/io/File;
    const-string v8, "root-path"

    #@42
    invoke-virtual {v8, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@45
    move-result v8

    #@46
    if-eqz v8, :cond_2

    #@48
    .line 577
    sget-object v8, Landroid/support/v4/content/FileProvider;->DEVICE_ROOT:Ljava/io/File;

    #@4a
    new-array v9, v10, [Ljava/lang/String;

    #@4c
    aput-object v3, v9, v11

    #@4e
    invoke-static {v8, v9}, Landroid/support/v4/content/FileProvider;->buildPath(Ljava/io/File;[Ljava/lang/String;)Ljava/io/File;

    #@51
    move-result-object v6

    #@52
    .line 586
    :cond_1
    :goto_1
    if-eqz v6, :cond_0

    #@54
    .line 587
    invoke-virtual {v4, v2, v6}, Landroid/support/v4/content/FileProvider$SimplePathStrategy;->addRoot(Ljava/lang/String;Ljava/io/File;)V

    #@57
    goto :goto_0

    #@58
    .line 578
    :cond_2
    const-string v8, "files-path"

    #@5a
    invoke-virtual {v8, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@5d
    move-result v8

    #@5e
    if-eqz v8, :cond_3

    #@60
    .line 579
    invoke-virtual {p0}, Landroid/content/Context;->getFilesDir()Ljava/io/File;

    #@63
    move-result-object v8

    #@64
    new-array v9, v10, [Ljava/lang/String;

    #@66
    aput-object v3, v9, v11

    #@68
    invoke-static {v8, v9}, Landroid/support/v4/content/FileProvider;->buildPath(Ljava/io/File;[Ljava/lang/String;)Ljava/io/File;

    #@6b
    move-result-object v6

    #@6c
    goto :goto_1

    #@6d
    .line 580
    :cond_3
    const-string v8, "cache-path"

    #@6f
    invoke-virtual {v8, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@72
    move-result v8

    #@73
    if-eqz v8, :cond_4

    #@75
    .line 581
    invoke-virtual {p0}, Landroid/content/Context;->getCacheDir()Ljava/io/File;

    #@78
    move-result-object v8

    #@79
    new-array v9, v10, [Ljava/lang/String;

    #@7b
    aput-object v3, v9, v11

    #@7d
    invoke-static {v8, v9}, Landroid/support/v4/content/FileProvider;->buildPath(Ljava/io/File;[Ljava/lang/String;)Ljava/io/File;

    #@80
    move-result-object v6

    #@81
    goto :goto_1

    #@82
    .line 582
    :cond_4
    const-string v8, "external-path"

    #@84
    invoke-virtual {v8, v5}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@87
    move-result v8

    #@88
    if-eqz v8, :cond_1

    #@8a
    .line 583
    invoke-static {}, Landroid/os/Environment;->getExternalStorageDirectory()Ljava/io/File;

    #@8d
    move-result-object v8

    #@8e
    new-array v9, v10, [Ljava/lang/String;

    #@90
    aput-object v3, v9, v11

    #@92
    invoke-static {v8, v9}, Landroid/support/v4/content/FileProvider;->buildPath(Ljava/io/File;[Ljava/lang/String;)Ljava/io/File;

    #@95
    move-result-object v6

    #@96
    goto :goto_1

    #@97
    .line 592
    .end local v2    # "name":Ljava/lang/String;
    .end local v3    # "path":Ljava/lang/String;
    .end local v5    # "tag":Ljava/lang/String;
    .end local v6    # "target":Ljava/io/File;
    :cond_5
    return-object v4
.end method


# virtual methods
.method public attachInfo(Landroid/content/Context;Landroid/content/pm/ProviderInfo;)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "info"    # Landroid/content/pm/ProviderInfo;

    #@0
    .prologue
    .line 342
    invoke-super {p0, p1, p2}, Landroid/content/ContentProvider;->attachInfo(Landroid/content/Context;Landroid/content/pm/ProviderInfo;)V

    #@3
    .line 345
    iget-boolean v0, p2, Landroid/content/pm/ProviderInfo;->exported:Z

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 346
    new-instance v0, Ljava/lang/SecurityException;

    #@9
    const-string v1, "Provider must not be exported"

    #@b
    invoke-direct {v0, v1}, Ljava/lang/SecurityException;-><init>(Ljava/lang/String;)V

    #@e
    throw v0

    #@f
    .line 348
    :cond_0
    iget-boolean v0, p2, Landroid/content/pm/ProviderInfo;->grantUriPermissions:Z

    #@11
    if-nez v0, :cond_1

    #@13
    .line 349
    new-instance v0, Ljava/lang/SecurityException;

    #@15
    const-string v1, "Provider must grant uri permissions"

    #@17
    invoke-direct {v0, v1}, Ljava/lang/SecurityException;-><init>(Ljava/lang/String;)V

    #@1a
    throw v0

    #@1b
    .line 352
    :cond_1
    iget-object v0, p2, Landroid/content/pm/ProviderInfo;->authority:Ljava/lang/String;

    #@1d
    invoke-static {p1, v0}, Landroid/support/v4/content/FileProvider;->getPathStrategy(Landroid/content/Context;Ljava/lang/String;)Landroid/support/v4/content/FileProvider$PathStrategy;

    #@20
    move-result-object v0

    #@21
    iput-object v0, p0, Landroid/support/v4/content/FileProvider;->mStrategy:Landroid/support/v4/content/FileProvider$PathStrategy;

    #@23
    .line 353
    return-void
.end method

.method public delete(Landroid/net/Uri;Ljava/lang/String;[Ljava/lang/String;)I
    .locals 2
    .param p1, "uri"    # Landroid/net/Uri;
    .param p2, "selection"    # Ljava/lang/String;
    .param p3, "selectionArgs"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 497
    iget-object v1, p0, Landroid/support/v4/content/FileProvider;->mStrategy:Landroid/support/v4/content/FileProvider$PathStrategy;

    #@2
    invoke-interface {v1, p1}, Landroid/support/v4/content/FileProvider$PathStrategy;->getFileForUri(Landroid/net/Uri;)Ljava/io/File;

    #@5
    move-result-object v0

    #@6
    .line 498
    .local v0, "file":Ljava/io/File;
    invoke-virtual {v0}, Ljava/io/File;->delete()Z

    #@9
    move-result v1

    #@a
    if-eqz v1, :cond_0

    #@c
    const/4 v1, 0x1

    #@d
    :goto_0
    return v1

    #@e
    :cond_0
    const/4 v1, 0x0

    #@f
    goto :goto_0
.end method

.method public getType(Landroid/net/Uri;)Ljava/lang/String;
    .locals 6
    .param p1, "uri"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 451
    iget-object v4, p0, Landroid/support/v4/content/FileProvider;->mStrategy:Landroid/support/v4/content/FileProvider$PathStrategy;

    #@2
    invoke-interface {v4, p1}, Landroid/support/v4/content/FileProvider$PathStrategy;->getFileForUri(Landroid/net/Uri;)Ljava/io/File;

    #@5
    move-result-object v1

    #@6
    .line 453
    .local v1, "file":Ljava/io/File;
    invoke-virtual {v1}, Ljava/io/File;->getName()Ljava/lang/String;

    #@9
    move-result-object v4

    #@a
    const/16 v5, 0x2e

    #@c
    invoke-virtual {v4, v5}, Ljava/lang/String;->lastIndexOf(I)I

    #@f
    move-result v2

    #@10
    .line 454
    .local v2, "lastDot":I
    if-ltz v2, :cond_0

    #@12
    .line 455
    invoke-virtual {v1}, Ljava/io/File;->getName()Ljava/lang/String;

    #@15
    move-result-object v4

    #@16
    add-int/lit8 v5, v2, 0x1

    #@18
    invoke-virtual {v4, v5}, Ljava/lang/String;->substring(I)Ljava/lang/String;

    #@1b
    move-result-object v0

    #@1c
    .line 456
    .local v0, "extension":Ljava/lang/String;
    invoke-static {}, Landroid/webkit/MimeTypeMap;->getSingleton()Landroid/webkit/MimeTypeMap;

    #@1f
    move-result-object v4

    #@20
    invoke-virtual {v4, v0}, Landroid/webkit/MimeTypeMap;->getMimeTypeFromExtension(Ljava/lang/String;)Ljava/lang/String;

    #@23
    move-result-object v3

    #@24
    .line 457
    .local v3, "mime":Ljava/lang/String;
    if-eqz v3, :cond_0

    #@26
    .line 462
    .end local v0    # "extension":Ljava/lang/String;
    .end local v3    # "mime":Ljava/lang/String;
    :goto_0
    return-object v3

    #@27
    :cond_0
    const-string v3, "application/octet-stream"

    #@29
    goto :goto_0
.end method

.method public insert(Landroid/net/Uri;Landroid/content/ContentValues;)Landroid/net/Uri;
    .locals 2
    .param p1, "uri"    # Landroid/net/Uri;
    .param p2, "values"    # Landroid/content/ContentValues;

    #@0
    .prologue
    .line 471
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "No external inserts"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method

.method public onCreate()Z
    .locals 1

    #@0
    .prologue
    .line 330
    const/4 v0, 0x1

    #@1
    return v0
.end method

.method public openFile(Landroid/net/Uri;Ljava/lang/String;)Landroid/os/ParcelFileDescriptor;
    .locals 3
    .param p1, "uri"    # Landroid/net/Uri;
    .param p2, "mode"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/FileNotFoundException;
        }
    .end annotation

    #@0
    .prologue
    .line 519
    iget-object v2, p0, Landroid/support/v4/content/FileProvider;->mStrategy:Landroid/support/v4/content/FileProvider$PathStrategy;

    #@2
    invoke-interface {v2, p1}, Landroid/support/v4/content/FileProvider$PathStrategy;->getFileForUri(Landroid/net/Uri;)Ljava/io/File;

    #@5
    move-result-object v0

    #@6
    .line 520
    .local v0, "file":Ljava/io/File;
    invoke-static {p2}, Landroid/support/v4/content/FileProvider;->modeToMode(Ljava/lang/String;)I

    #@9
    move-result v1

    #@a
    .line 521
    .local v1, "fileMode":I
    invoke-static {v0, v1}, Landroid/os/ParcelFileDescriptor;->open(Ljava/io/File;I)Landroid/os/ParcelFileDescriptor;

    #@d
    move-result-object v2

    #@e
    return-object v2
.end method

.method public query(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    .locals 12
    .param p1, "uri"    # Landroid/net/Uri;
    .param p2, "projection"    # [Ljava/lang/String;
    .param p3, "selection"    # Ljava/lang/String;
    .param p4, "selectionArgs"    # [Ljava/lang/String;
    .param p5, "sortOrder"    # Ljava/lang/String;

    #@0
    .prologue
    .line 412
    iget-object v10, p0, Landroid/support/v4/content/FileProvider;->mStrategy:Landroid/support/v4/content/FileProvider$PathStrategy;

    #@2
    invoke-interface {v10, p1}, Landroid/support/v4/content/FileProvider$PathStrategy;->getFileForUri(Landroid/net/Uri;)Ljava/io/File;

    #@5
    move-result-object v4

    #@6
    .line 414
    .local v4, "file":Ljava/io/File;
    if-nez p2, :cond_0

    #@8
    .line 415
    sget-object p2, Landroid/support/v4/content/FileProvider;->COLUMNS:[Ljava/lang/String;

    #@a
    .line 418
    :cond_0
    array-length v10, p2

    #@b
    new-array v2, v10, [Ljava/lang/String;

    #@d
    .line 419
    .local v2, "cols":[Ljava/lang/String;
    array-length v10, p2

    #@e
    new-array v9, v10, [Ljava/lang/Object;

    #@10
    .line 420
    .local v9, "values":[Ljava/lang/Object;
    const/4 v5, 0x0

    #@11
    .line 421
    .local v5, "i":I
    move-object v0, p2

    #@12
    .local v0, "arr$":[Ljava/lang/String;
    array-length v8, v0

    #@13
    .local v8, "len$":I
    const/4 v7, 0x0

    #@14
    .local v7, "i$":I
    move v6, v5

    #@15
    .end local v5    # "i":I
    .local v6, "i":I
    :goto_0
    if-ge v7, v8, :cond_2

    #@17
    aget-object v1, v0, v7

    #@19
    .line 422
    .local v1, "col":Ljava/lang/String;
    const-string v10, "_display_name"

    #@1b
    invoke-virtual {v10, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@1e
    move-result v10

    #@1f
    if-eqz v10, :cond_1

    #@21
    .line 423
    const-string v10, "_display_name"

    #@23
    aput-object v10, v2, v6

    #@25
    .line 424
    add-int/lit8 v5, v6, 0x1

    #@27
    .end local v6    # "i":I
    .restart local v5    # "i":I
    invoke-virtual {v4}, Ljava/io/File;->getName()Ljava/lang/String;

    #@2a
    move-result-object v10

    #@2b
    aput-object v10, v9, v6

    #@2d
    .line 421
    :goto_1
    add-int/lit8 v7, v7, 0x1

    #@2f
    move v6, v5

    #@30
    .end local v5    # "i":I
    .restart local v6    # "i":I
    goto :goto_0

    #@31
    .line 425
    :cond_1
    const-string v10, "_size"

    #@33
    invoke-virtual {v10, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@36
    move-result v10

    #@37
    if-eqz v10, :cond_3

    #@39
    .line 426
    const-string v10, "_size"

    #@3b
    aput-object v10, v2, v6

    #@3d
    .line 427
    add-int/lit8 v5, v6, 0x1

    #@3f
    .end local v6    # "i":I
    .restart local v5    # "i":I
    invoke-virtual {v4}, Ljava/io/File;->length()J

    #@42
    move-result-wide v10

    #@43
    invoke-static {v10, v11}, Ljava/lang/Long;->valueOf(J)Ljava/lang/Long;

    #@46
    move-result-object v10

    #@47
    aput-object v10, v9, v6

    #@49
    goto :goto_1

    #@4a
    .line 431
    .end local v1    # "col":Ljava/lang/String;
    .end local v5    # "i":I
    .restart local v6    # "i":I
    :cond_2
    invoke-static {v2, v6}, Landroid/support/v4/content/FileProvider;->copyOf([Ljava/lang/String;I)[Ljava/lang/String;

    #@4d
    move-result-object v2

    #@4e
    .line 432
    invoke-static {v9, v6}, Landroid/support/v4/content/FileProvider;->copyOf([Ljava/lang/Object;I)[Ljava/lang/Object;

    #@51
    move-result-object v9

    #@52
    .line 434
    new-instance v3, Landroid/database/MatrixCursor;

    #@54
    const/4 v10, 0x1

    #@55
    invoke-direct {v3, v2, v10}, Landroid/database/MatrixCursor;-><init>([Ljava/lang/String;I)V

    #@58
    .line 435
    .local v3, "cursor":Landroid/database/MatrixCursor;
    invoke-virtual {v3, v9}, Landroid/database/MatrixCursor;->addRow([Ljava/lang/Object;)V

    #@5b
    .line 436
    return-object v3

    #@5c
    .end local v3    # "cursor":Landroid/database/MatrixCursor;
    .restart local v1    # "col":Ljava/lang/String;
    :cond_3
    move v5, v6

    #@5d
    .end local v6    # "i":I
    .restart local v5    # "i":I
    goto :goto_1
.end method

.method public update(Landroid/net/Uri;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    .locals 2
    .param p1, "uri"    # Landroid/net/Uri;
    .param p2, "values"    # Landroid/content/ContentValues;
    .param p3, "selection"    # Ljava/lang/String;
    .param p4, "selectionArgs"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 480
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@2
    const-string v1, "No external updates"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/UnsupportedOperationException;-><init>(Ljava/lang/String;)V

    #@7
    throw v0
.end method
