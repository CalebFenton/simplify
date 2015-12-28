.class public final Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;
.super Ljava/lang/Object;
.source "ActivityChooserModel.java"

# interfaces
.implements Ljava/lang/Comparable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/ActivityChooserModel;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x11
    name = "ActivityResolveInfo"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Object;",
        "Ljava/lang/Comparable",
        "<",
        "Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;",
        ">;"
    }
.end annotation


# instance fields
.field public final resolveInfo:Landroid/content/pm/ResolveInfo;

.field final synthetic this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;

.field public weight:F


# direct methods
.method public constructor <init>(Landroid/support/v7/internal/widget/ActivityChooserModel;Landroid/content/pm/ResolveInfo;)V
    .locals 0
    .param p2, "resolveInfo"    # Landroid/content/pm/ResolveInfo;

    #@0
    .prologue
    .line 873
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->this$0:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    .line 874
    iput-object p2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->resolveInfo:Landroid/content/pm/ResolveInfo;

    #@7
    .line 875
    return-void
.end method


# virtual methods
.method public compareTo(Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;)I
    .locals 2
    .param p1, "another"    # Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;

    #@0
    .prologue
    .line 901
    iget v0, p1, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->weight:F

    #@2
    invoke-static {v0}, Ljava/lang/Float;->floatToIntBits(F)I

    #@5
    move-result v0

    #@6
    iget v1, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->weight:F

    #@8
    invoke-static {v1}, Ljava/lang/Float;->floatToIntBits(F)I

    #@b
    move-result v1

    #@c
    sub-int/2addr v0, v1

    #@d
    return v0
.end method

.method public bridge synthetic compareTo(Ljava/lang/Object;)I
    .locals 1
    .param p1, "x0"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 856
    check-cast p1, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;

    #@2
    .end local p1    # "x0":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->compareTo(Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public equals(Ljava/lang/Object;)Z
    .locals 5
    .param p1, "obj"    # Ljava/lang/Object;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 884
    if-ne p0, p1, :cond_1

    #@4
    .line 897
    :cond_0
    :goto_0
    return v1

    #@5
    .line 887
    :cond_1
    if-nez p1, :cond_2

    #@7
    move v1, v2

    #@8
    .line 888
    goto :goto_0

    #@9
    .line 890
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
    .line 891
    goto :goto_0

    #@15
    :cond_3
    move-object v0, p1

    #@16
    .line 893
    check-cast v0, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;

    #@18
    .line 894
    .local v0, "other":Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;
    iget v3, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->weight:F

    #@1a
    invoke-static {v3}, Ljava/lang/Float;->floatToIntBits(F)I

    #@1d
    move-result v3

    #@1e
    iget v4, v0, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->weight:F

    #@20
    invoke-static {v4}, Ljava/lang/Float;->floatToIntBits(F)I

    #@23
    move-result v4

    #@24
    if-eq v3, v4, :cond_0

    #@26
    move v1, v2

    #@27
    .line 895
    goto :goto_0
.end method

.method public hashCode()I
    .locals 1

    #@0
    .prologue
    .line 879
    iget v0, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->weight:F

    #@2
    invoke-static {v0}, Ljava/lang/Float;->floatToIntBits(F)I

    #@5
    move-result v0

    #@6
    add-int/lit8 v0, v0, 0x1f

    #@8
    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 6

    #@0
    .prologue
    .line 906
    new-instance v0, Ljava/lang/StringBuilder;

    #@2
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    #@5
    .line 907
    .local v0, "builder":Ljava/lang/StringBuilder;
    const-string v1, "["

    #@7
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@a
    .line 908
    const-string v1, "resolveInfo:"

    #@c
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@f
    move-result-object v1

    #@10
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->resolveInfo:Landroid/content/pm/ResolveInfo;

    #@12
    invoke-virtual {v2}, Landroid/content/pm/ResolveInfo;->toString()Ljava/lang/String;

    #@15
    move-result-object v2

    #@16
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@19
    .line 909
    const-string v1, "; weight:"

    #@1b
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1e
    move-result-object v1

    #@1f
    new-instance v2, Ljava/math/BigDecimal;

    #@21
    iget v3, p0, Landroid/support/v7/internal/widget/ActivityChooserModel$ActivityResolveInfo;->weight:F

    #@23
    float-to-double v4, v3

    #@24
    invoke-direct {v2, v4, v5}, Ljava/math/BigDecimal;-><init>(D)V

    #@27
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@2a
    .line 910
    const-string v1, "]"

    #@2c
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2f
    .line 911
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@32
    move-result-object v1

    #@33
    return-object v1
.end method
