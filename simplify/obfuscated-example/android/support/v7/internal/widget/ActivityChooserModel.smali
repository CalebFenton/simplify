.class public Landroid/support/v7/internal/widget/ActivityChooserModel;
.super Landroid/database/DataSetObservable;
.source "ActivityChooserModel.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/widget/ActivityChooserModel$1;,
        Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;,
        Landroid/support/v7/internal/widget/ActivityChooserModel$DefaultSorter;,
        Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;,
        Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;,
        Landroid/support/v7/internal/widget/ActivityChooserModel$OnChooseActivityListener;,
        Landroid/support/v7/internal/widget/ActivityChooserModel$ActivitySorter;,
        Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityChooserModelClient;
    }
.end annotation


# static fields
.field private static final ATTRIBUTE_ACTIVITY:Ljava/lang/String; = "activity"

.field private static final ATTRIBUTE_TIME:Ljava/lang/String; = "time"

.field private static final ATTRIBUTE_WEIGHT:Ljava/lang/String; = "weight"

.field private static final DEBUG:Z = false

.field private static final DEFAULT_ACTIVITY_INFLATION:I = 0x5

.field private static final DEFAULT_HISTORICAL_RECORD_WEIGHT:F = 1.0f

.field public static final DEFAULT_HISTORY_FILE_NAME:Ljava/lang/String; = "activity_choser_model_history.xml"

.field public static final DEFAULT_HISTORY_MAX_LENGTH:I = 0x32

.field private static final HISTORY_FILE_EXTENSION:Ljava/lang/String; = ".xml"

.field private static final INVALID_INDEX:I = -0x1

.field private static final LOG_TAG:Ljava/lang/String;

.field private static final TAG_HISTORICAL_RECORD:Ljava/lang/String; = "historical-record"

.field private static final TAG_HISTORICAL_RECORDS:Ljava/lang/String; = "historical-records"

.field private static final sDataModelRegistry:Ljava/util/Map;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/Map",
            "<",
            "Ljava/lang/String;",
            "Landroid/support/v7/internal/widget/ActivityChooserModel;",
            ">;"
        }
    .end annotation
.end field

.field private static final sRegistryLock:Ljava/lang/Object;


# instance fields
.field private final mActivities:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List",
            "<",
            "Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;",
            ">;"
        }
    .end annotation
.end field

.field private mActivityChoserModelPolicy:Landroid/support/v7/internal/widget/ActivityChooserModel$OnChooseActivityListener;

.field private mActivitySorter:Landroid/support/v7/internal/widget/ActivityChooserModel$ActivitySorter;

.field private mCanReadHistoricalData:Z

.field private final mContext:Landroid/content/Context;

.field private final mHistoricalRecords:Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/List",
            "<",
            "Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;",
            ">;"
        }
    .end annotation
.end field

.field private mHistoricalRecordsChanged:Z

.field private final mHistoryFileName:Ljava/lang/String;

.field private mHistoryMaxSize:I

.field private final mInstanceLock:Ljava/lang/Object;

.field private mIntent:Landroid/content/Intent;

.field private mReadShareHistoryCalled:Z

.field private mReloadActivities:Z


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 161
    const-class v0, Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@2
    invoke-virtual {v0}, Ljava/lang/Class;->getSimpleName()Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    sput-object v0, Landroid/support/v7/internal/widget/ActivityChooserModel;->LOG_TAG:Ljava/lang/String;

    #@8
    .line 222
    new-instance v0, Ljava/lang/Object;

    #@a
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    #@d
    sput-object v0, Landroid/support/v7/internal/widget/ActivityChooserModel;->sRegistryLock:Ljava/lang/Object;

    #@f
    .line 227
    new-instance v0, Ljava/util/HashMap;

    #@11
    invoke-direct {v0}, Ljava/util/HashMap;-><init>()V

    #@14
    sput-object v0, Landroid/support/v7/internal/widget/ActivityChooserModel;->sDataModelRegistry:Ljava/util/Map;

    #@16
    return-void
.end method

.method private constructor <init>(Landroid/content/Context;Ljava/lang/String;)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "historyFileName"    # Ljava/lang/String;

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 350
    invoke-direct {p0}, Landroid/database/DataSetObservable;-><init>()V

    #@5
    .line 233
    new-instance v0, Ljava/lang/Object;

    #@7
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    #@a
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@c
    .line 238
    new-instance v0, Ljava/util/ArrayList;

    #@e
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@11
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivities:Ljava/util/List;

    #@13
    .line 243
    new-instance v0, Ljava/util/ArrayList;

    #@15
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@18
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecords:Ljava/util/List;

    #@1a
    .line 263
    new-instance v0, Landroid/support/v7/internal/widget/ActivityChooserModel$DefaultSorter;

    #@1c
    const/4 v1, 0x0

    #@1d
    invoke-direct {v0, p0, v1}, Landroid/support/v7/internal/widget/ActivityChooserModel$DefaultSorter;-><init>(Landroid/support/v7/internal/widget/ActivityChooserModel;Landroid/support/v7/internal/widget/ActivityChooserModel$1;)V

    #@20
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivitySorter:Landroid/support/v7/internal/widget/ActivityChooserModel$ActivitySorter;

    #@22
    .line 268
    const/16 v0, 0x32

    #@24
    iput v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryMaxSize:I

    #@26
    .line 278
    iput-boolean v3, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mCanReadHistoricalData:Z

    #@28
    .line 289
    iput-boolean v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mReadShareHistoryCalled:Z

    #@2a
    .line 297
    iput-boolean v3, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecordsChanged:Z

    #@2c
    .line 302
    iput-boolean v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mReloadActivities:Z

    #@2e
    .line 351
    invoke-virtual {p1}, Landroid/content/Context;->getApplicationContext()Landroid/content/Context;

    #@31
    move-result-object v0

    #@32
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mContext:Landroid/content/Context;

    #@34
    .line 352
    invoke-static {p2}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@37
    move-result v0

    #@38
    if-nez v0, :cond_0

    #@3a
    const-string v0, ".xml"

    #@3c
    invoke-virtual {p2, v0}, Ljava/lang/String;->endsWith(Ljava/lang/String;)Z

    #@3f
    move-result v0

    #@40
    if-nez v0, :cond_0

    #@42
    .line 354
    new-instance v0, Ljava/lang/StringBuilder;

    #@44
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    #@47
    invoke-virtual {v0, p2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@4a
    move-result-object v0

    #@4b
    const-string v1, ".xml"

    #@4d
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@50
    move-result-object v0

    #@51
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@54
    move-result-object v0

    #@55
    iput-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryFileName:Ljava/lang/String;

    #@57
    .line 358
    :goto_0
    return-void

    #@58
    .line 356
    :cond_0
    iput-object p2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryFileName:Ljava/lang/String;

    #@5a
    goto :goto_0
.end method

.method static synthetic access$200(Landroid/support/v7/internal/widget/ActivityChooserModel;)Landroid/content/Context;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@0
    .prologue
    .line 94
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mContext:Landroid/content/Context;

    #@2
    return-object v0
.end method

.method static synthetic access$300()Ljava/lang/String;
    .locals 1

    #@0
    .prologue
    .line 94
    sget-object v0, Landroid/support/v7/internal/widget/ActivityChooserModel;->LOG_TAG:Ljava/lang/String;

    #@2
    return-object v0
.end method

.method static synthetic access$400(Landroid/support/v7/internal/widget/ActivityChooserModel;)Ljava/lang/String;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@0
    .prologue
    .line 94
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryFileName:Ljava/lang/String;

    #@2
    return-object v0
.end method

.method static synthetic access$502(Landroid/support/v7/internal/widget/ActivityChooserModel;Z)Z
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/internal/widget/ActivityChooserModel;
    .param p1, "x1"    # Z

    #@0
    .prologue
    .line 94
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mCanReadHistoricalData:Z

    #@2
    return p1
.end method

.method private addHisoricalRecord(Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;)Z
    .locals 2
    .param p1, "historicalRecord"    # Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;

    #@0
    .prologue
    .line 731
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecords:Ljava/util/List;

    #@2
    invoke-interface {v1, p1}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    #@5
    move-result v0

    #@6
    .line 732
    .local v0, "added":Z
    if-eqz v0, :cond_0

    #@8
    .line 733
    const/4 v1, 0x1

    #@9
    iput-boolean v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecordsChanged:Z

    #@b
    .line 734
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->pruneExcessiveHistoricalRecordsIfNeeded()V

    #@e
    .line 735
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->persistHistoricalDataIfNeeded()V

    #@11
    .line 736
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->sortActivitiesIfNeeded()Z

    #@14
    .line 737
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->notifyChanged()V

    #@17
    .line 739
    :cond_0
    return v0
.end method

.method private ensureConsistentState()V
    .locals 2

    #@0
    .prologue
    .line 658
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->loadActivitiesIfNeeded()Z

    #@3
    move-result v0

    #@4
    .line 659
    .local v0, "stateChanged":Z
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->readHistoricalDataIfNeeded()Z

    #@7
    move-result v1

    #@8
    or-int/2addr v0, v1

    #@9
    .line 660
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->pruneExcessiveHistoricalRecordsIfNeeded()V

    #@c
    .line 661
    if-eqz v0, :cond_0

    #@e
    .line 662
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->sortActivitiesIfNeeded()Z

    #@11
    .line 663
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->notifyChanged()V

    #@14
    .line 665
    :cond_0
    return-void
.end method

.method public static get(Landroid/content/Context;Ljava/lang/String;)Landroid/support/v7/internal/widget/ActivityChooserModel;
    .locals 3
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "historyFileName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 334
    sget-object v2, Landroid/support/v7/internal/widget/ActivityChooserModel;->sRegistryLock:Ljava/lang/Object;

    #@2
    monitor-enter v2

    #@3
    .line 335
    :try_start_0
    sget-object v1, Landroid/support/v7/internal/widget/ActivityChooserModel;->sDataModelRegistry:Ljava/util/Map;

    #@5
    invoke-interface {v1, p1}, Ljava/util/Map;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@8
    move-result-object v0

    #@9
    check-cast v0, Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@b
    .line 336
    .local v0, "dataModel":Landroid/support/v7/internal/widget/ActivityChooserModel;
    if-nez v0, :cond_0

    #@d
    .line 337
    new-instance v0, Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@f
    .end local v0    # "dataModel":Landroid/support/v7/internal/widget/ActivityChooserModel;
    invoke-direct {v0, p0, p1}, Landroid/support/v7/internal/widget/ActivityChooserModel;-><init>(Landroid/content/Context;Ljava/lang/String;)V

    #@12
    .line 338
    .restart local v0    # "dataModel":Landroid/support/v7/internal/widget/ActivityChooserModel;
    sget-object v1, Landroid/support/v7/internal/widget/ActivityChooserModel;->sDataModelRegistry:Ljava/util/Map;

    #@14
    invoke-interface {v1, p1, v0}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@17
    .line 340
    :cond_0
    monitor-exit v2

    #@18
    return-object v0

    #@19
    .line 341
    .end local v0    # "dataModel":Landroid/support/v7/internal/widget/ActivityChooserModel;
    :catchall_0
    move-exception v1

    #@1a
    monitor-exit v2
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@1b
    throw v1
.end method

.method private loadActivitiesIfNeeded()Z
    .locals 7

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    .line 691
    iget-boolean v5, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mReloadActivities:Z

    #@3
    if-eqz v5, :cond_1

    #@5
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mIntent:Landroid/content/Intent;

    #@7
    if-eqz v5, :cond_1

    #@9
    .line 692
    iput-boolean v4, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mReloadActivities:Z

    #@b
    .line 693
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivities:Ljava/util/List;

    #@d
    invoke-interface {v5}, Ljava/util/List;->clear()V

    #@10
    .line 694
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mContext:Landroid/content/Context;

    #@12
    invoke-virtual {v5}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    #@15
    move-result-object v5

    #@16
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mIntent:Landroid/content/Intent;

    #@18
    invoke-virtual {v5, v6, v4}, Landroid/content/pm/PackageManager;->queryIntentActivities(Landroid/content/Intent;I)Ljava/util/List;

    #@1b
    move-result-object v3

    #@1c
    .line 696
    .local v3, "resolveInfos":Ljava/util/List;, "Ljava/util/List<Landroid/content/pm/ResolveInfo;>;"
    invoke-interface {v3}, Ljava/util/List;->size()I

    #@1f
    move-result v2

    #@20
    .line 697
    .local v2, "resolveInfoCount":I
    const/4 v0, 0x0

    #@21
    .local v0, "i":I
    :goto_0
    if-ge v0, v2, :cond_0

    #@23
    .line 698
    invoke-interface {v3, v0}, Ljava/util/List;->get(I)Ljava/lang/Object;

    #@26
    move-result-object v1

    #@27
    check-cast v1, Landroid/content/pm/ResolveInfo;

    #@29
    .line 699
    .local v1, "resolveInfo":Landroid/content/pm/ResolveInfo;
    iget-object v4, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivities:Ljava/util/List;

    #@2b
    new-instance v5, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;

    #@2d
    invoke-direct {v5, p0, v1}, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;-><init>(Landroid/support/v7/internal/widget/ActivityChooserModel;Landroid/content/pm/ResolveInfo;)V

    #@30
    invoke-interface {v4, v5}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    #@33
    .line 697
    add-int/lit8 v0, v0, 0x1

    #@35
    goto :goto_0

    #@36
    .line 701
    .end local v1    # "resolveInfo":Landroid/content/pm/ResolveInfo;
    :cond_0
    const/4 v4, 0x1

    #@37
    .line 703
    .end local v0    # "i":I
    .end local v2    # "resolveInfoCount":I
    .end local v3    # "resolveInfos":Ljava/util/List;, "Ljava/util/List<Landroid/content/pm/ResolveInfo;>;"
    :cond_1
    return v4
.end method

.method private persistHistoricalDataIfNeeded()V
    .locals 4

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 570
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mReadShareHistoryCalled:Z

    #@3
    if-nez v0, :cond_0

    #@5
    .line 571
    new-instance v0, Ljava/lang/IllegalStateException;

    #@7
    const-string v1, "No preceding call to #readHistoricalData"

    #@9
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@c
    throw v0

    #@d
    .line 573
    :cond_0
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecordsChanged:Z

    #@f
    if-nez v0, :cond_2

    #@11
    .line 581
    :cond_1
    :goto_0
    return-void

    #@12
    .line 576
    :cond_2
    iput-boolean v3, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecordsChanged:Z

    #@14
    .line 577
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryFileName:Ljava/lang/String;

    #@16
    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@19
    move-result v0

    #@1a
    if-nez v0, :cond_1

    #@1c
    .line 578
    new-instance v0, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;

    #@1e
    const/4 v1, 0x0

    #@1f
    invoke-direct {v0, p0, v1}, Landroid/support/v7/internal/widget/ActivityChooserModel$PersistHistoryAsyncTask;-><init>(Landroid/support/v7/internal/widget/ActivityChooserModel;Landroid/support/v7/internal/widget/ActivityChooserModel$1;)V

    #@22
    const/4 v1, 0x2

    #@23
    new-array v1, v1, [Ljava/lang/Object;

    #@25
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecords:Ljava/util/List;

    #@27
    aput-object v2, v1, v3

    #@29
    const/4 v2, 0x1

    #@2a
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryFileName:Ljava/lang/String;

    #@2c
    aput-object v3, v1, v2

    #@2e
    invoke-static {v0, v1}, Landroid/support/v4/os/AsyncTaskCompat;->executeParallel(Landroid/os/AsyncTask;[Ljava/lang/Object;)Landroid/os/AsyncTask;

    #@31
    goto :goto_0
.end method

.method private pruneExcessiveHistoricalRecordsIfNeeded()V
    .locals 4

    #@0
    .prologue
    .line 746
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecords:Ljava/util/List;

    #@2
    invoke-interface {v2}, Ljava/util/List;->size()I

    #@5
    move-result v2

    #@6
    iget v3, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryMaxSize:I

    #@8
    sub-int v1, v2, v3

    #@a
    .line 747
    .local v1, "pruneCount":I
    if-gtz v1, :cond_1

    #@c
    .line 757
    :cond_0
    return-void

    #@d
    .line 750
    :cond_1
    const/4 v2, 0x1

    #@e
    iput-boolean v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecordsChanged:Z

    #@10
    .line 751
    const/4 v0, 0x0

    #@11
    .local v0, "i":I
    :goto_0
    if-ge v0, v1, :cond_0

    #@13
    .line 752
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecords:Ljava/util/List;

    #@15
    const/4 v3, 0x0

    #@16
    invoke-interface {v2, v3}, Ljava/util/List;->remove(I)Ljava/lang/Object;

    #@19
    move-result-object v2

    #@1a
    check-cast v2, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;

    #@1c
    .line 751
    add-int/lit8 v0, v0, 0x1

    #@1e
    goto :goto_0
.end method

.method private readHistoricalDataIfNeeded()Z
    .locals 3

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    const/4 v1, 0x0

    #@2
    .line 714
    iget-boolean v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mCanReadHistoricalData:Z

    #@4
    if-eqz v2, :cond_0

    #@6
    iget-boolean v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecordsChanged:Z

    #@8
    if-eqz v2, :cond_0

    #@a
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryFileName:Ljava/lang/String;

    #@c
    invoke-static {v2}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@f
    move-result v2

    #@10
    if-nez v2, :cond_0

    #@12
    .line 716
    iput-boolean v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mCanReadHistoricalData:Z

    #@14
    .line 717
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mReadShareHistoryCalled:Z

    #@16
    .line 718
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->readHistoricalDataImpl()V

    #@19
    .line 721
    :goto_0
    return v0

    #@1a
    :cond_0
    move v0, v1

    #@1b
    goto :goto_0
.end method

.method private readHistoricalDataImpl()V
    .locals 18

    #@0
    .prologue
    .line 964
    const/4 v3, 0x0

    #@1
    .line 966
    .local v3, "fis":Ljava/io/FileInputStream;
    :try_start_0
    move-object/from16 v0, p0

    #@3
    iget-object v15, v0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mContext:Landroid/content/Context;

    #@5
    move-object/from16 v0, p0

    #@7
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryFileName:Ljava/lang/String;

    #@9
    move-object/from16 v16, v0

    #@b
    invoke-virtual/range {v15 .. v16}, Landroid/content/Context;->openFileInput(Ljava/lang/String;)Ljava/io/FileInputStream;
    :try_end_0
    .catch Ljava/io/FileNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    #@e
    move-result-object v3

    #@f
    .line 974
    :try_start_1
    invoke-static {}, Landroid/util/Xml;->newPullParser()Lorg/xmlpull/v1/XmlPullParser;

    #@12
    move-result-object v8

    #@13
    .line 975
    .local v8, "parser":Lorg/xmlpull/v1/XmlPullParser;
    const/4 v15, 0x0

    #@14
    invoke-interface {v8, v3, v15}, Lorg/xmlpull/v1/XmlPullParser;->setInput(Ljava/io/InputStream;Ljava/lang/String;)V

    #@17
    .line 977
    const/4 v12, 0x0

    #@18
    .line 978
    .local v12, "type":I
    :goto_0
    const/4 v15, 0x1

    #@19
    if-eq v12, v15, :cond_1

    #@1b
    const/4 v15, 0x2

    #@1c
    if-eq v12, v15, :cond_1

    #@1e
    .line 979
    invoke-interface {v8}, Lorg/xmlpull/v1/XmlPullParser;->next()I

    #@21
    move-result v12

    #@22
    goto :goto_0

    #@23
    .line 967
    .end local v8    # "parser":Lorg/xmlpull/v1/XmlPullParser;
    .end local v12    # "type":I
    :catch_0
    move-exception v4

    #@24
    .line 1032
    :cond_0
    :goto_1
    return-void

    #@25
    .line 982
    .restart local v8    # "parser":Lorg/xmlpull/v1/XmlPullParser;
    .restart local v12    # "type":I
    :cond_1
    const-string v15, "historical-records"

    #@27
    invoke-interface {v8}, Lorg/xmlpull/v1/XmlPullParser;->getName()Ljava/lang/String;

    #@2a
    move-result-object v16

    #@2b
    invoke-virtual/range {v15 .. v16}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@2e
    move-result v15

    #@2f
    if-nez v15, :cond_2

    #@31
    .line 983
    new-instance v15, Lorg/xmlpull/v1/XmlPullParserException;

    #@33
    const-string v16, "Share records file does not start with historical-records tag."

    #@35
    invoke-direct/range {v15 .. v16}, Lorg/xmlpull/v1/XmlPullParserException;-><init>(Ljava/lang/String;)V

    #@38
    throw v15
    :try_end_1
    .catch Lorg/xmlpull/v1/XmlPullParserException; {:try_start_1 .. :try_end_1} :catch_1
    .catch Ljava/io/IOException; {:try_start_1 .. :try_end_1} :catch_4
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@39
    .line 1019
    .end local v8    # "parser":Lorg/xmlpull/v1/XmlPullParser;
    .end local v12    # "type":I
    :catch_1
    move-exception v14

    #@3a
    .line 1020
    .local v14, "xppe":Lorg/xmlpull/v1/XmlPullParserException;
    :try_start_2
    sget-object v15, Landroid/support/v7/internal/widget/ActivityChooserModel;->LOG_TAG:Ljava/lang/String;

    #@3c
    new-instance v16, Ljava/lang/StringBuilder;

    #@3e
    invoke-direct/range {v16 .. v16}, Ljava/lang/StringBuilder;-><init>()V

    #@41
    const-string v17, "Error reading historical recrod file: "

    #@43
    invoke-virtual/range {v16 .. v17}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@46
    move-result-object v16

    #@47
    move-object/from16 v0, p0

    #@49
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryFileName:Ljava/lang/String;

    #@4b
    move-object/from16 v17, v0

    #@4d
    invoke-virtual/range {v16 .. v17}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@50
    move-result-object v16

    #@51
    invoke-virtual/range {v16 .. v16}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@54
    move-result-object v16

    #@55
    move-object/from16 v0, v16

    #@57
    invoke-static {v15, v0, v14}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    #@5a
    .line 1024
    if-eqz v3, :cond_0

    #@5c
    .line 1026
    :try_start_3
    invoke-virtual {v3}, Ljava/io/FileInputStream;->close()V
    :try_end_3
    .catch Ljava/io/IOException; {:try_start_3 .. :try_end_3} :catch_2

    #@5f
    goto :goto_1

    #@60
    .line 1027
    :catch_2
    move-exception v15

    #@61
    goto :goto_1

    #@62
    .line 987
    .end local v14    # "xppe":Lorg/xmlpull/v1/XmlPullParserException;
    .restart local v8    # "parser":Lorg/xmlpull/v1/XmlPullParser;
    .restart local v12    # "type":I
    :cond_2
    :try_start_4
    move-object/from16 v0, p0

    #@64
    iget-object v5, v0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecords:Ljava/util/List;

    #@66
    .line 988
    .local v5, "historicalRecords":Ljava/util/List;, "Ljava/util/List<Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;>;"
    invoke-interface {v5}, Ljava/util/List;->clear()V

    #@69
    .line 991
    :cond_3
    :goto_2
    invoke-interface {v8}, Lorg/xmlpull/v1/XmlPullParser;->next()I
    :try_end_4
    .catch Lorg/xmlpull/v1/XmlPullParserException; {:try_start_4 .. :try_end_4} :catch_1
    .catch Ljava/io/IOException; {:try_start_4 .. :try_end_4} :catch_4
    .catchall {:try_start_4 .. :try_end_4} :catchall_0

    #@6c
    move-result v12

    #@6d
    .line 992
    const/4 v15, 0x1

    #@6e
    if-ne v12, v15, :cond_4

    #@70
    .line 1024
    if-eqz v3, :cond_0

    #@72
    .line 1026
    :try_start_5
    invoke-virtual {v3}, Ljava/io/FileInputStream;->close()V
    :try_end_5
    .catch Ljava/io/IOException; {:try_start_5 .. :try_end_5} :catch_3

    #@75
    goto :goto_1

    #@76
    .line 1027
    :catch_3
    move-exception v15

    #@77
    goto :goto_1

    #@78
    .line 995
    :cond_4
    const/4 v15, 0x3

    #@79
    if-eq v12, v15, :cond_3

    #@7b
    const/4 v15, 0x4

    #@7c
    if-eq v12, v15, :cond_3

    #@7e
    .line 998
    :try_start_6
    invoke-interface {v8}, Lorg/xmlpull/v1/XmlPullParser;->getName()Ljava/lang/String;

    #@81
    move-result-object v7

    #@82
    .line 999
    .local v7, "nodeName":Ljava/lang/String;
    const-string v15, "historical-record"

    #@84
    invoke-virtual {v15, v7}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@87
    move-result v15

    #@88
    if-nez v15, :cond_5

    #@8a
    .line 1000
    new-instance v15, Lorg/xmlpull/v1/XmlPullParserException;

    #@8c
    const-string v16, "Share records file not well-formed."

    #@8e
    invoke-direct/range {v15 .. v16}, Lorg/xmlpull/v1/XmlPullParserException;-><init>(Ljava/lang/String;)V

    #@91
    throw v15
    :try_end_6
    .catch Lorg/xmlpull/v1/XmlPullParserException; {:try_start_6 .. :try_end_6} :catch_1
    .catch Ljava/io/IOException; {:try_start_6 .. :try_end_6} :catch_4
    .catchall {:try_start_6 .. :try_end_6} :catchall_0

    #@92
    .line 1021
    .end local v5    # "historicalRecords":Ljava/util/List;, "Ljava/util/List<Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;>;"
    .end local v7    # "nodeName":Ljava/lang/String;
    .end local v8    # "parser":Lorg/xmlpull/v1/XmlPullParser;
    .end local v12    # "type":I
    :catch_4
    move-exception v6

    #@93
    .line 1022
    .local v6, "ioe":Ljava/io/IOException;
    :try_start_7
    sget-object v15, Landroid/support/v7/internal/widget/ActivityChooserModel;->LOG_TAG:Ljava/lang/String;

    #@95
    new-instance v16, Ljava/lang/StringBuilder;

    #@97
    invoke-direct/range {v16 .. v16}, Ljava/lang/StringBuilder;-><init>()V

    #@9a
    const-string v17, "Error reading historical recrod file: "

    #@9c
    invoke-virtual/range {v16 .. v17}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@9f
    move-result-object v16

    #@a0
    move-object/from16 v0, p0

    #@a2
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryFileName:Ljava/lang/String;

    #@a4
    move-object/from16 v17, v0

    #@a6
    invoke-virtual/range {v16 .. v17}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@a9
    move-result-object v16

    #@aa
    invoke-virtual/range {v16 .. v16}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@ad
    move-result-object v16

    #@ae
    move-object/from16 v0, v16

    #@b0
    invoke-static {v15, v0, v6}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    :try_end_7
    .catchall {:try_start_7 .. :try_end_7} :catchall_0

    #@b3
    .line 1024
    if-eqz v3, :cond_0

    #@b5
    .line 1026
    :try_start_8
    invoke-virtual {v3}, Ljava/io/FileInputStream;->close()V
    :try_end_8
    .catch Ljava/io/IOException; {:try_start_8 .. :try_end_8} :catch_5

    #@b8
    goto/16 :goto_1

    #@ba
    .line 1027
    :catch_5
    move-exception v15

    #@bb
    goto/16 :goto_1

    #@bd
    .line 1003
    .end local v6    # "ioe":Ljava/io/IOException;
    .restart local v5    # "historicalRecords":Ljava/util/List;, "Ljava/util/List<Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;>;"
    .restart local v7    # "nodeName":Ljava/lang/String;
    .restart local v8    # "parser":Lorg/xmlpull/v1/XmlPullParser;
    .restart local v12    # "type":I
    :cond_5
    const/4 v15, 0x0

    #@be
    :try_start_9
    const-string v16, "activity"

    #@c0
    move-object/from16 v0, v16

    #@c2
    invoke-interface {v8, v15, v0}, Lorg/xmlpull/v1/XmlPullParser;->getAttributeValue(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    #@c5
    move-result-object v2

    #@c6
    .line 1004
    .local v2, "activity":Ljava/lang/String;
    const/4 v15, 0x0

    #@c7
    const-string v16, "time"

    #@c9
    move-object/from16 v0, v16

    #@cb
    invoke-interface {v8, v15, v0}, Lorg/xmlpull/v1/XmlPullParser;->getAttributeValue(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    #@ce
    move-result-object v15

    #@cf
    invoke-static {v15}, Ljava/lang/Long;->parseLong(Ljava/lang/String;)J

    #@d2
    move-result-wide v10

    #@d3
    .line 1006
    .local v10, "time":J
    const/4 v15, 0x0

    #@d4
    const-string v16, "weight"

    #@d6
    move-object/from16 v0, v16

    #@d8
    invoke-interface {v8, v15, v0}, Lorg/xmlpull/v1/XmlPullParser;->getAttributeValue(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    #@db
    move-result-object v15

    #@dc
    invoke-static {v15}, Ljava/lang/Float;->parseFloat(Ljava/lang/String;)F

    #@df
    move-result v13

    #@e0
    .line 1008
    .local v13, "weight":F
    new-instance v9, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;

    #@e2
    invoke-direct {v9, v2, v10, v11, v13}, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;-><init>(Ljava/lang/String;JF)V

    #@e5
    .line 1009
    .local v9, "readRecord":Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;
    invoke-interface {v5, v9}, Ljava/util/List;->add(Ljava/lang/Object;)Z
    :try_end_9
    .catch Lorg/xmlpull/v1/XmlPullParserException; {:try_start_9 .. :try_end_9} :catch_1
    .catch Ljava/io/IOException; {:try_start_9 .. :try_end_9} :catch_4
    .catchall {:try_start_9 .. :try_end_9} :catchall_0

    #@e8
    goto :goto_2

    #@e9
    .line 1024
    .end local v2    # "activity":Ljava/lang/String;
    .end local v5    # "historicalRecords":Ljava/util/List;, "Ljava/util/List<Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;>;"
    .end local v7    # "nodeName":Ljava/lang/String;
    .end local v8    # "parser":Lorg/xmlpull/v1/XmlPullParser;
    .end local v9    # "readRecord":Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;
    .end local v10    # "time":J
    .end local v12    # "type":I
    .end local v13    # "weight":F
    :catchall_0
    move-exception v15

    #@ea
    if-eqz v3, :cond_6

    #@ec
    .line 1026
    :try_start_a
    invoke-virtual {v3}, Ljava/io/FileInputStream;->close()V
    :try_end_a
    .catch Ljava/io/IOException; {:try_start_a .. :try_end_a} :catch_6

    #@ef
    .line 1029
    :cond_6
    :goto_3
    throw v15

    #@f0
    .line 1027
    :catch_6
    move-exception v16

    #@f1
    goto :goto_3
.end method

.method private sortActivitiesIfNeeded()Z
    .locals 4

    #@0
    .prologue
    .line 675
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivitySorter:Landroid/support/v7/internal/widget/ActivityChooserModel$ActivitySorter;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mIntent:Landroid/content/Intent;

    #@6
    if-eqz v0, :cond_0

    #@8
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivities:Ljava/util/List;

    #@a
    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    #@d
    move-result v0

    #@e
    if-nez v0, :cond_0

    #@10
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecords:Ljava/util/List;

    #@12
    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    #@15
    move-result v0

    #@16
    if-nez v0, :cond_0

    #@18
    .line 677
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivitySorter:Landroid/support/v7/internal/widget/ActivityChooserModel$ActivitySorter;

    #@1a
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mIntent:Landroid/content/Intent;

    #@1c
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivities:Ljava/util/List;

    #@1e
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecords:Ljava/util/List;

    #@20
    invoke-static {v3}, Ljava/util/Collections;->unmodifiableList(Ljava/util/List;)Ljava/util/List;

    #@23
    move-result-object v3

    #@24
    invoke-interface {v0, v1, v2, v3}, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivitySorter;->sort(Landroid/content/Intent;Ljava/util/List;Ljava/util/List;)V

    #@27
    .line 679
    const/4 v0, 0x1

    #@28
    .line 681
    :goto_0
    return v0

    #@29
    :cond_0
    const/4 v0, 0x0

    #@2a
    goto :goto_0
.end method


# virtual methods
.method public chooseActivity(I)Landroid/content/Intent;
    .locals 10
    .param p1, "index"    # I

    #@0
    .prologue
    const/4 v6, 0x0

    #@1
    .line 461
    iget-object v7, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@3
    monitor-enter v7

    #@4
    .line 462
    :try_start_0
    iget-object v8, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mIntent:Landroid/content/Intent;

    #@6
    if-nez v8, :cond_0

    #@8
    .line 463
    monitor-exit v7

    #@9
    move-object v0, v6

    #@a
    .line 491
    :goto_0
    return-object v0

    #@b
    .line 466
    :cond_0
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->ensureConsistentState()V

    #@e
    .line 468
    iget-object v8, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivities:Ljava/util/List;

    #@10
    invoke-interface {v8, p1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    #@13
    move-result-object v2

    #@14
    check-cast v2, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;

    #@16
    .line 470
    .local v2, "chosenActivity":Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;
    new-instance v3, Landroid/content/ComponentName;

    #@18
    iget-object v8, v2, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->resolveInfo:Landroid/content/pm/ResolveInfo;

    #@1a
    iget-object v8, v8, Landroid/content/pm/ResolveInfo;->activityInfo:Landroid/content/pm/ActivityInfo;

    #@1c
    iget-object v8, v8, Landroid/content/pm/ActivityInfo;->packageName:Ljava/lang/String;

    #@1e
    iget-object v9, v2, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->resolveInfo:Landroid/content/pm/ResolveInfo;

    #@20
    iget-object v9, v9, Landroid/content/pm/ResolveInfo;->activityInfo:Landroid/content/pm/ActivityInfo;

    #@22
    iget-object v9, v9, Landroid/content/pm/ActivityInfo;->name:Ljava/lang/String;

    #@24
    invoke-direct {v3, v8, v9}, Landroid/content/ComponentName;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    #@27
    .line 474
    .local v3, "chosenName":Landroid/content/ComponentName;
    new-instance v0, Landroid/content/Intent;

    #@29
    iget-object v8, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mIntent:Landroid/content/Intent;

    #@2b
    invoke-direct {v0, v8}, Landroid/content/Intent;-><init>(Landroid/content/Intent;)V

    #@2e
    .line 475
    .local v0, "choiceIntent":Landroid/content/Intent;
    invoke-virtual {v0, v3}, Landroid/content/Intent;->setComponent(Landroid/content/ComponentName;)Landroid/content/Intent;

    #@31
    .line 477
    iget-object v8, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivityChoserModelPolicy:Landroid/support/v7/internal/widget/ActivityChooserModel$OnChooseActivityListener;

    #@33
    if-eqz v8, :cond_1

    #@35
    .line 479
    new-instance v1, Landroid/content/Intent;

    #@37
    invoke-direct {v1, v0}, Landroid/content/Intent;-><init>(Landroid/content/Intent;)V

    #@3a
    .line 480
    .local v1, "choiceIntentCopy":Landroid/content/Intent;
    iget-object v8, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivityChoserModelPolicy:Landroid/support/v7/internal/widget/ActivityChooserModel$OnChooseActivityListener;

    #@3c
    invoke-interface {v8, p0, v1}, Landroid/support/v7/internal/widget/ActivityChooserModel$OnChooseActivityListener;->onChooseActivity(Landroid/support/v7/internal/widget/ActivityChooserModel;Landroid/content/Intent;)Z

    #@3f
    move-result v4

    #@40
    .line 482
    .local v4, "handled":Z
    if-eqz v4, :cond_1

    #@42
    .line 483
    monitor-exit v7

    #@43
    move-object v0, v6

    #@44
    goto :goto_0

    #@45
    .line 487
    .end local v1    # "choiceIntentCopy":Landroid/content/Intent;
    .end local v4    # "handled":Z
    :cond_1
    new-instance v5, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;

    #@47
    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    #@4a
    move-result-wide v8

    #@4b
    const/high16 v6, 0x3f800000    # 1.0f

    #@4d
    invoke-direct {v5, v3, v8, v9, v6}, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;-><init>(Landroid/content/ComponentName;JF)V

    #@50
    .line 489
    .local v5, "historicalRecord":Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;
    invoke-direct {p0, v5}, Landroid/support/v7/internal/widget/ActivityChooserModel;->addHisoricalRecord(Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;)Z

    #@53
    .line 491
    monitor-exit v7

    #@54
    goto :goto_0

    #@55
    .line 492
    .end local v0    # "choiceIntent":Landroid/content/Intent;
    .end local v2    # "chosenActivity":Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;
    .end local v3    # "chosenName":Landroid/content/ComponentName;
    .end local v5    # "historicalRecord":Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;
    :catchall_0
    move-exception v6

    #@56
    monitor-exit v7
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@57
    throw v6
.end method

.method public getActivity(I)Landroid/content/pm/ResolveInfo;
    .locals 2
    .param p1, "index"    # I

    #@0
    .prologue
    .line 414
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@2
    monitor-enter v1

    #@3
    .line 415
    :try_start_0
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->ensureConsistentState()V

    #@6
    .line 416
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivities:Ljava/util/List;

    #@8
    invoke-interface {v0, p1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    #@b
    move-result-object v0

    #@c
    check-cast v0, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;

    #@e
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->resolveInfo:Landroid/content/pm/ResolveInfo;

    #@10
    monitor-exit v1

    #@11
    return-object v0

    #@12
    .line 417
    :catchall_0
    move-exception v0

    #@13
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@14
    throw v0
.end method

.method public getActivityCount()I
    .locals 2

    #@0
    .prologue
    .line 399
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@2
    monitor-enter v1

    #@3
    .line 400
    :try_start_0
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->ensureConsistentState()V

    #@6
    .line 401
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivities:Ljava/util/List;

    #@8
    invoke-interface {v0}, Ljava/util/List;->size()I

    #@b
    move-result v0

    #@c
    monitor-exit v1

    #@d
    return v0

    #@e
    .line 402
    :catchall_0
    move-exception v0

    #@f
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@10
    throw v0
.end method

.method public getActivityIndex(Landroid/content/pm/ResolveInfo;)I
    .locals 6
    .param p1, "activity"    # Landroid/content/pm/ResolveInfo;

    #@0
    .prologue
    .line 428
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@2
    monitor-enter v5

    #@3
    .line 429
    :try_start_0
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->ensureConsistentState()V

    #@6
    .line 430
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivities:Ljava/util/List;

    #@8
    .line 431
    .local v0, "activities":Ljava/util/List;, "Ljava/util/List<Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;>;"
    invoke-interface {v0}, Ljava/util/List;->size()I

    #@b
    move-result v1

    #@c
    .line 432
    .local v1, "activityCount":I
    const/4 v3, 0x0

    #@d
    .local v3, "i":I
    :goto_0
    if-ge v3, v1, :cond_1

    #@f
    .line 433
    invoke-interface {v0, v3}, Ljava/util/List;->get(I)Ljava/lang/Object;

    #@12
    move-result-object v2

    #@13
    check-cast v2, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;

    #@15
    .line 434
    .local v2, "currentActivity":Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;
    iget-object v4, v2, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->resolveInfo:Landroid/content/pm/ResolveInfo;

    #@17
    if-ne v4, p1, :cond_0

    #@19
    .line 435
    monitor-exit v5

    #@1a
    .line 438
    .end local v2    # "currentActivity":Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;
    .end local v3    # "i":I
    :goto_1
    return v3

    #@1b
    .line 432
    .restart local v2    # "currentActivity":Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;
    .restart local v3    # "i":I
    :cond_0
    add-int/lit8 v3, v3, 0x1

    #@1d
    goto :goto_0

    #@1e
    .line 438
    .end local v2    # "currentActivity":Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;
    :cond_1
    const/4 v3, -0x1

    #@1f
    monitor-exit v5

    #@20
    goto :goto_1

    #@21
    .line 439
    .end local v0    # "activities":Ljava/util/List;, "Ljava/util/List<Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;>;"
    .end local v1    # "activityCount":I
    .end local v3    # "i":I
    :catchall_0
    move-exception v4

    #@22
    monitor-exit v5
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@23
    throw v4
.end method

.method public getDefaultActivity()Landroid/content/pm/ResolveInfo;
    .locals 3

    #@0
    .prologue
    .line 516
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@2
    monitor-enter v1

    #@3
    .line 517
    :try_start_0
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->ensureConsistentState()V

    #@6
    .line 518
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivities:Ljava/util/List;

    #@8
    invoke-interface {v0}, Ljava/util/List;->isEmpty()Z

    #@b
    move-result v0

    #@c
    if-nez v0, :cond_0

    #@e
    .line 519
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivities:Ljava/util/List;

    #@10
    const/4 v2, 0x0

    #@11
    invoke-interface {v0, v2}, Ljava/util/List;->get(I)Ljava/lang/Object;

    #@14
    move-result-object v0

    #@15
    check-cast v0, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;

    #@17
    iget-object v0, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->resolveInfo:Landroid/content/pm/ResolveInfo;

    #@19
    monitor-exit v1

    #@1a
    .line 522
    :goto_0
    return-object v0

    #@1b
    .line 521
    :cond_0
    monitor-exit v1

    #@1c
    .line 522
    const/4 v0, 0x0

    #@1d
    goto :goto_0

    #@1e
    .line 521
    :catchall_0
    move-exception v0

    #@1f
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@20
    throw v0
.end method

.method public getHistoryMaxSize()I
    .locals 2

    #@0
    .prologue
    .line 634
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@2
    monitor-enter v1

    #@3
    .line 635
    :try_start_0
    iget v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryMaxSize:I

    #@5
    monitor-exit v1

    #@6
    return v0

    #@7
    .line 636
    :catchall_0
    move-exception v0

    #@8
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@9
    throw v0
.end method

.method public getHistorySize()I
    .locals 2

    #@0
    .prologue
    .line 645
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@2
    monitor-enter v1

    #@3
    .line 646
    :try_start_0
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->ensureConsistentState()V

    #@6
    .line 647
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoricalRecords:Ljava/util/List;

    #@8
    invoke-interface {v0}, Ljava/util/List;->size()I

    #@b
    move-result v0

    #@c
    monitor-exit v1

    #@d
    return v0

    #@e
    .line 648
    :catchall_0
    move-exception v0

    #@f
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@10
    throw v0
.end method

.method public getIntent()Landroid/content/Intent;
    .locals 2

    #@0
    .prologue
    .line 386
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@2
    monitor-enter v1

    #@3
    .line 387
    :try_start_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mIntent:Landroid/content/Intent;

    #@5
    monitor-exit v1

    #@6
    return-object v0

    #@7
    .line 388
    :catchall_0
    move-exception v0

    #@8
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@9
    throw v0
.end method

.method public setActivitySorter(Landroid/support/v7/internal/widget/ActivityChooserModel$ActivitySorter;)V
    .locals 2
    .param p1, "activitySorter"    # Landroid/support/v7/internal/widget/ActivityChooserModel$ActivitySorter;

    #@0
    .prologue
    .line 591
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@2
    monitor-enter v1

    #@3
    .line 592
    :try_start_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivitySorter:Landroid/support/v7/internal/widget/ActivityChooserModel$ActivitySorter;

    #@5
    if-ne v0, p1, :cond_0

    #@7
    .line 593
    monitor-exit v1

    #@8
    .line 600
    :goto_0
    return-void

    #@9
    .line 595
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivitySorter:Landroid/support/v7/internal/widget/ActivityChooserModel$ActivitySorter;

    #@b
    .line 596
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->sortActivitiesIfNeeded()Z

    #@e
    move-result v0

    #@f
    if-eqz v0, :cond_1

    #@11
    .line 597
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->notifyChanged()V

    #@14
    .line 599
    :cond_1
    monitor-exit v1

    #@15
    goto :goto_0

    #@16
    :catchall_0
    move-exception v0

    #@17
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@18
    throw v0
.end method

.method public setDefaultActivity(I)V
    .locals 10
    .param p1, "index"    # I

    #@0
    .prologue
    .line 536
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@2
    monitor-enter v6

    #@3
    .line 537
    :try_start_0
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->ensureConsistentState()V

    #@6
    .line 539
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivities:Ljava/util/List;

    #@8
    invoke-interface {v5, p1}, Ljava/util/List;->get(I)Ljava/lang/Object;

    #@b
    move-result-object v2

    #@c
    check-cast v2, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;

    #@e
    .line 540
    .local v2, "newDefaultActivity":Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;
    iget-object v5, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivities:Ljava/util/List;

    #@10
    const/4 v7, 0x0

    #@11
    invoke-interface {v5, v7}, Ljava/util/List;->get(I)Ljava/lang/Object;

    #@14
    move-result-object v3

    #@15
    check-cast v3, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;

    #@17
    .line 543
    .local v3, "oldDefaultActivity":Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;
    if-eqz v3, :cond_0

    #@19
    .line 545
    iget v5, v3, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->weight:F

    #@1b
    iget v7, v2, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->weight:F

    #@1d
    sub-float/2addr v5, v7

    #@1e
    const/high16 v7, 0x40a00000    # 5.0f

    #@20
    add-float v4, v5, v7

    #@22
    .line 551
    .local v4, "weight":F
    :goto_0
    new-instance v0, Landroid/content/ComponentName;

    #@24
    iget-object v5, v2, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->resolveInfo:Landroid/content/pm/ResolveInfo;

    #@26
    iget-object v5, v5, Landroid/content/pm/ResolveInfo;->activityInfo:Landroid/content/pm/ActivityInfo;

    #@28
    iget-object v5, v5, Landroid/content/pm/ActivityInfo;->packageName:Ljava/lang/String;

    #@2a
    iget-object v7, v2, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->resolveInfo:Landroid/content/pm/ResolveInfo;

    #@2c
    iget-object v7, v7, Landroid/content/pm/ResolveInfo;->activityInfo:Landroid/content/pm/ActivityInfo;

    #@2e
    iget-object v7, v7, Landroid/content/pm/ActivityInfo;->name:Ljava/lang/String;

    #@30
    invoke-direct {v0, v5, v7}, Landroid/content/ComponentName;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    #@33
    .line 554
    .local v0, "defaultName":Landroid/content/ComponentName;
    new-instance v1, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;

    #@35
    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J

    #@38
    move-result-wide v8

    #@39
    invoke-direct {v1, v0, v8, v9, v4}, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;-><init>(Landroid/content/ComponentName;JF)V

    #@3c
    .line 556
    .local v1, "historicalRecord":Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;
    invoke-direct {p0, v1}, Landroid/support/v7/internal/widget/ActivityChooserModel;->addHisoricalRecord(Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;)Z

    #@3f
    .line 557
    monitor-exit v6

    #@40
    .line 558
    return-void

    #@41
    .line 548
    .end local v0    # "defaultName":Landroid/content/ComponentName;
    .end local v1    # "historicalRecord":Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;
    .end local v4    # "weight":F
    :cond_0
    const/high16 v4, 0x3f800000    # 1.0f

    #@43
    .restart local v4    # "weight":F
    goto :goto_0

    #@44
    .line 557
    .end local v2    # "newDefaultActivity":Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;
    .end local v3    # "oldDefaultActivity":Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;
    .end local v4    # "weight":F
    :catchall_0
    move-exception v5

    #@45
    monitor-exit v6
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@46
    throw v5
.end method

.method public setHistoryMaxSize(I)V
    .locals 2
    .param p1, "historyMaxSize"    # I

    #@0
    .prologue
    .line 616
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@2
    monitor-enter v1

    #@3
    .line 617
    :try_start_0
    iget v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryMaxSize:I

    #@5
    if-ne v0, p1, :cond_0

    #@7
    .line 618
    monitor-exit v1

    #@8
    .line 626
    :goto_0
    return-void

    #@9
    .line 620
    :cond_0
    iput p1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mHistoryMaxSize:I

    #@b
    .line 621
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->pruneExcessiveHistoricalRecordsIfNeeded()V

    #@e
    .line 622
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->sortActivitiesIfNeeded()Z

    #@11
    move-result v0

    #@12
    if-eqz v0, :cond_1

    #@14
    .line 623
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->notifyChanged()V

    #@17
    .line 625
    :cond_1
    monitor-exit v1

    #@18
    goto :goto_0

    #@19
    :catchall_0
    move-exception v0

    #@1a
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@1b
    throw v0
.end method

.method public setIntent(Landroid/content/Intent;)V
    .locals 2
    .param p1, "intent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 370
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@2
    monitor-enter v1

    #@3
    .line 371
    :try_start_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mIntent:Landroid/content/Intent;

    #@5
    if-ne v0, p1, :cond_0

    #@7
    .line 372
    monitor-exit v1

    #@8
    .line 378
    :goto_0
    return-void

    #@9
    .line 374
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mIntent:Landroid/content/Intent;

    #@b
    .line 375
    const/4 v0, 0x1

    #@c
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mReloadActivities:Z

    #@e
    .line 376
    invoke-direct {p0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->ensureConsistentState()V

    #@11
    .line 377
    monitor-exit v1

    #@12
    goto :goto_0

    #@13
    :catchall_0
    move-exception v0

    #@14
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@15
    throw v0
.end method

.method public setOnChooseActivityListener(Landroid/support/v7/internal/widget/ActivityChooserModel$OnChooseActivityListener;)V
    .locals 2
    .param p1, "listener"    # Landroid/support/v7/internal/widget/ActivityChooserModel$OnChooseActivityListener;

    #@0
    .prologue
    .line 501
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mInstanceLock:Ljava/lang/Object;

    #@2
    monitor-enter v1

    #@3
    .line 502
    :try_start_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel;->mActivityChoserModelPolicy:Landroid/support/v7/internal/widget/ActivityChooserModel$OnChooseActivityListener;

    #@5
    .line 503
    monitor-exit v1

    #@6
    .line 504
    return-void

    #@7
    .line 503
    :catchall_0
    move-exception v0

    #@8
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@9
    throw v0
.end method
