.class public final Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;
.super Ljava/lang/Object;
.source "ActivityChooserModel.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/ActivityChooserModel;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "HistoricalRecord"
.end annotation


# instance fields
.field public final activity:Landroid/content/ComponentName;

.field public final time:J

.field public final weight:F


# direct methods
.method public constructor <init>(Landroid/content/ComponentName;JF)V
    .locals 0
    .param p1, "activityName"    # Landroid/content/ComponentName;
    .param p2, "time"    # J
    .param p4, "weight"    # F

    #@0
    .prologue
    .line 797
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 798
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->activity:Landroid/content/ComponentName;

    #@5
    .line 799
    iput-wide p2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->time:J

    #@7
    .line 800
    iput p4, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->weight:F

    #@9
    .line 801
    return-void
.end method

.method public constructor <init>(Ljava/lang/String;JF)V
    .locals 2
    .param p1, "activityName"    # Ljava/lang/String;
    .param p2, "time"    # J
    .param p4, "weight"    # F

    #@0
    .prologue
    .line 787
    invoke-static {p1}, Landroid/content/ComponentName;->unflattenFromString(Ljava/lang/String;)Landroid/content/ComponentName;

    #@3
    move-result-object v0

    #@4
    invoke-direct {p0, v0, p2, p3, p4}, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;-><init>(Landroid/content/ComponentName;JF)V

    #@7
    .line 788
    return-void
.end method


# virtual methods
.method public equals(Ljava/lang/Object;)Z
    .locals 8
    .param p1, "obj"    # Ljava/lang/Object;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 815
    if-ne p0, p1, :cond_1

    #@4
    .line 838
    :cond_0
    :goto_0
    return v1

    #@5
    .line 818
    :cond_1
    if-nez p1, :cond_2

    #@7
    move v1, v2

    #@8
    .line 819
    goto :goto_0

    #@9
    .line 821
    :cond_2
    invoke-virtual {p0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@c
    move-result-object v3

    #@d
    invoke-virtual {p1}, Ljava/lang/Object;->getClass()Ljava/lang/Class;

    #@10
    move-result-object v4

    #@11
    if-eq v3, v4, :cond_3

    #@13
    move v1, v2

    #@14
    .line 822
    goto :goto_0

    #@15
    :cond_3
    move-object v0, p1

    #@16
    .line 824
    check-cast v0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;

    #@18
    .line 825
    .local v0, "other":Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->activity:Landroid/content/ComponentName;

    #@1a
    if-nez v3, :cond_4

    #@1c
    .line 826
    iget-object v3, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->activity:Landroid/content/ComponentName;

    #@1e
    if-eqz v3, :cond_5

    #@20
    move v1, v2

    #@21
    .line 827
    goto :goto_0

    #@22
    .line 829
    :cond_4
    iget-object v3, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->activity:Landroid/content/ComponentName;

    #@24
    iget-object v4, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->activity:Landroid/content/ComponentName;

    #@26
    invoke-virtual {v3, v4}, Landroid/content/ComponentName;->equals(Ljava/lang/Object;)Z

    #@29
    move-result v3

    #@2a
    if-nez v3, :cond_5

    #@2c
    move v1, v2

    #@2d
    .line 830
    goto :goto_0

    #@2e
    .line 832
    :cond_5
    iget-wide v4, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->time:J

    #@30
    iget-wide v6, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->time:J

    #@32
    cmp-long v3, v4, v6

    #@34
    if-eqz v3, :cond_6

    #@36
    move v1, v2

    #@37
    .line 833
    goto :goto_0

    #@38
    .line 835
    :cond_6
    iget v3, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->weight:F

    #@3a
    invoke-static {v3}, Ljava/lang/Float;->floatToIntBits(F)I

    #@3d
    move-result v3

    #@3e
    iget v4, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->weight:F

    #@40
    invoke-static {v4}, Ljava/lang/Float;->floatToIntBits(F)I

    #@43
    move-result v4

    #@44
    if-eq v3, v4, :cond_0

    #@46
    move v1, v2

    #@47
    .line 836
    goto :goto_0
.end method

.method public hashCode()I
    .locals 8

    #@0
    .prologue
    .line 805
    const/16 v0, 0x1f

    #@2
    .line 806
    .local v0, "prime":I
    const/4 v1, 0x1

    #@3
    .line 807
    .local v1, "result":I
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->activity:Landroid/content/ComponentName;

    #@5
    if-nez v2, :cond_0

    #@7
    const/4 v2, 0x0

    #@8
    :goto_0
    add-int/lit8 v1, v2, 0x1f

    #@a
    .line 808
    mul-int/lit8 v2, v1, 0x1f

    #@c
    iget-wide v4, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->time:J

    #@e
    iget-wide v6, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->time:J

    #@10
    const/16 v3, 0x20

    #@12
    ushr-long/2addr v6, v3

    #@13
    xor-long/2addr v4, v6

    #@14
    long-to-int v3, v4

    #@15
    add-int v1, v2, v3

    #@17
    .line 809
    mul-int/lit8 v2, v1, 0x1f

    #@19
    iget v3, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->weight:F

    #@1b
    invoke-static {v3}, Ljava/lang/Float;->floatToIntBits(F)I

    #@1e
    move-result v3

    #@1f
    add-int v1, v2, v3

    #@21
    .line 810
    return v1

    #@22
    .line 807
    :cond_0
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->activity:Landroid/content/ComponentName;

    #@24
    invoke-virtual {v2}, Landroid/content/ComponentName;->hashCode()I

    #@27
    move-result v2

    #@28
    goto :goto_0
.end method

.method public toString()Ljava/lang/String;
    .locals 6

    #@0
    .prologue
    .line 843
    new-instance v0, Ljava/lang/StringBuilder;

    #@2
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    #@5
    .line 844
    .local v0, "builder":Ljava/lang/StringBuilder;
    const-string v1, "["

    #@7
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@a
    .line 845
    const-string v1, "; activity:"

    #@c
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@f
    move-result-object v1

    #@10
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->activity:Landroid/content/ComponentName;

    #@12
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@15
    .line 846
    const-string v1, "; time:"

    #@17
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1a
    move-result-object v1

    #@1b
    iget-wide v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->time:J

    #@1d
    invoke-virtual {v1, v2, v3}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

    #@20
    .line 847
    const-string v1, "; weight:"

    #@22
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@25
    move-result-object v1

    #@26
    new-instance v2, Ljava/math/BigDecimal;

    #@28
    iget v3, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$HistoricalRecord;->weight:F

    #@2a
    float-to-double v4, v3

    #@2b
    invoke-direct {v2, v4, v5}, Ljava/math/BigDecimal;-><init>(D)V

    #@2e
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@31
    .line 848
    const-string v1, "]"

    #@33
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@36
    .line 849
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@39
    move-result-object v1

    #@3a
    return-object v1
.end method
