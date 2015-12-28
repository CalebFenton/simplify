.class public Landroid/support/v4/app/ActivityOptionsCompat;
.super Ljava/lang/Object;
.source "ActivityOptionsCompat.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;,
        Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImplJB;
    }
.end annotation


# direct methods
.method protected constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 220
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 221
    return-void
.end method

.method public static makeCustomAnimation(Landroid/content/Context;II)Landroid/support/v4/app/ActivityOptionsCompat;
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "enterResId"    # I
    .param p2, "exitResId"    # I

    #@0
    .prologue
    .line 47
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x10

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 48
    new-instance v0, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImplJB;

    #@8
    invoke-static {p0, p1, p2}, Landroid/support/v4/app/ActivityOptionsCompatJB;->makeCustomAnimation(Landroid/content/Context;II)Landroid/support/v4/app/ActivityOptionsCompatJB;

    #@b
    move-result-object v1

    #@c
    invoke-direct {v0, v1}, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImplJB;-><init>(Landroid/support/v4/app/ActivityOptionsCompatJB;)V

    #@f
    .line 51
    :goto_0
    return-object v0

    #@10
    :cond_0
    new-instance v0, Landroid/support/v4/app/ActivityOptionsCompat;

    #@12
    invoke-direct {v0}, Landroid/support/v4/app/ActivityOptionsCompat;-><init>()V

    #@15
    goto :goto_0
.end method

.method public static makeScaleUpAnimation(Landroid/view/View;IIII)Landroid/support/v4/app/ActivityOptionsCompat;
    .locals 2
    .param p0, "source"    # Landroid/view/View;
    .param p1, "startX"    # I
    .param p2, "startY"    # I
    .param p3, "startWidth"    # I
    .param p4, "startHeight"    # I

    #@0
    .prologue
    .line 76
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x10

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 77
    new-instance v0, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImplJB;

    #@8
    invoke-static {p0, p1, p2, p3, p4}, Landroid/support/v4/app/ActivityOptionsCompatJB;->makeScaleUpAnimation(Landroid/view/View;IIII)Landroid/support/v4/app/ActivityOptionsCompatJB;

    #@b
    move-result-object v1

    #@c
    invoke-direct {v0, v1}, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImplJB;-><init>(Landroid/support/v4/app/ActivityOptionsCompatJB;)V

    #@f
    .line 81
    :goto_0
    return-object v0

    #@10
    :cond_0
    new-instance v0, Landroid/support/v4/app/ActivityOptionsCompat;

    #@12
    invoke-direct {v0}, Landroid/support/v4/app/ActivityOptionsCompat;-><init>()V

    #@15
    goto :goto_0
.end method

.method public static makeSceneTransitionAnimation(Landroid/app/Activity;Landroid/view/View;Ljava/lang/String;)Landroid/support/v4/app/ActivityOptionsCompat;
    .locals 2
    .param p0, "activity"    # Landroid/app/Activity;
    .param p1, "sharedElement"    # Landroid/view/View;
    .param p2, "sharedElementName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 133
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x15

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 134
    new-instance v0, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;

    #@8
    invoke-static {p0, p1, p2}, Landroid/support/v4/app/ActivityOptionsCompat21;->makeSceneTransitionAnimation(Landroid/app/Activity;Landroid/view/View;Ljava/lang/String;)Landroid/support/v4/app/ActivityOptionsCompat21;

    #@b
    move-result-object v1

    #@c
    invoke-direct {v0, v1}, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;-><init>(Landroid/support/v4/app/ActivityOptionsCompat21;)V

    #@f
    .line 138
    :goto_0
    return-object v0

    #@10
    :cond_0
    new-instance v0, Landroid/support/v4/app/ActivityOptionsCompat;

    #@12
    invoke-direct {v0}, Landroid/support/v4/app/ActivityOptionsCompat;-><init>()V

    #@15
    goto :goto_0
.end method

.method public static varargs makeSceneTransitionAnimation(Landroid/app/Activity;[Landroid/support/v4/util/Pair;)Landroid/support/v4/app/ActivityOptionsCompat;
    .locals 5
    .param p0, "activity"    # Landroid/app/Activity;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/app/Activity;",
            "[",
            "Landroid/support/v4/util/Pair",
            "<",
            "Landroid/view/View;",
            "Ljava/lang/String;",
            ">;)",
            "Landroid/support/v4/app/ActivityOptionsCompat;"
        }
    .end annotation

    #@0
    .prologue
    .line 160
    .local p1, "sharedElements":[Landroid/support/v4/util/Pair;, "[Landroid/support/v4/util/Pair<Landroid/view/View;Ljava/lang/String;>;"
    sget v3, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v4, 0x15

    #@4
    if-lt v3, v4, :cond_1

    #@6
    .line 161
    const/4 v2, 0x0

    #@7
    .line 162
    .local v2, "views":[Landroid/view/View;
    const/4 v1, 0x0

    #@8
    .line 163
    .local v1, "names":[Ljava/lang/String;
    if-eqz p1, :cond_0

    #@a
    .line 164
    array-length v3, p1

    #@b
    new-array v2, v3, [Landroid/view/View;

    #@d
    .line 165
    array-length v3, p1

    #@e
    new-array v1, v3, [Ljava/lang/String;

    #@10
    .line 166
    const/4 v0, 0x0

    #@11
    .local v0, "i":I
    :goto_0
    array-length v3, p1

    #@12
    if-ge v0, v3, :cond_0

    #@14
    .line 167
    aget-object v3, p1, v0

    #@16
    iget-object v3, v3, Landroid/support/v4/util/Pair;->first:Ljava/lang/Object;

    #@18
    check-cast v3, Landroid/view/View;

    #@1a
    aput-object v3, v2, v0

    #@1c
    .line 168
    aget-object v3, p1, v0

    #@1e
    iget-object v3, v3, Landroid/support/v4/util/Pair;->second:Ljava/lang/Object;

    #@20
    check-cast v3, Ljava/lang/String;

    #@22
    aput-object v3, v1, v0

    #@24
    .line 166
    add-int/lit8 v0, v0, 0x1

    #@26
    goto :goto_0

    #@27
    .line 171
    .end local v0    # "i":I
    :cond_0
    new-instance v3, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;

    #@29
    invoke-static {p0, v2, v1}, Landroid/support/v4/app/ActivityOptionsCompat21;->makeSceneTransitionAnimation(Landroid/app/Activity;[Landroid/view/View;[Ljava/lang/String;)Landroid/support/v4/app/ActivityOptionsCompat21;

    #@2c
    move-result-object v4

    #@2d
    invoke-direct {v3, v4}, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;-><init>(Landroid/support/v4/app/ActivityOptionsCompat21;)V

    #@30
    .line 174
    .end local v1    # "names":[Ljava/lang/String;
    .end local v2    # "views":[Landroid/view/View;
    :goto_1
    return-object v3

    #@31
    :cond_1
    new-instance v3, Landroid/support/v4/app/ActivityOptionsCompat;

    #@33
    invoke-direct {v3}, Landroid/support/v4/app/ActivityOptionsCompat;-><init>()V

    #@36
    goto :goto_1
.end method

.method public static makeThumbnailScaleUpAnimation(Landroid/view/View;Landroid/graphics/Bitmap;II)Landroid/support/v4/app/ActivityOptionsCompat;
    .locals 2
    .param p0, "source"    # Landroid/view/View;
    .param p1, "thumbnail"    # Landroid/graphics/Bitmap;
    .param p2, "startX"    # I
    .param p3, "startY"    # I

    #@0
    .prologue
    .line 105
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x10

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 106
    new-instance v0, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImplJB;

    #@8
    invoke-static {p0, p1, p2, p3}, Landroid/support/v4/app/ActivityOptionsCompatJB;->makeThumbnailScaleUpAnimation(Landroid/view/View;Landroid/graphics/Bitmap;II)Landroid/support/v4/app/ActivityOptionsCompatJB;

    #@b
    move-result-object v1

    #@c
    invoke-direct {v0, v1}, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImplJB;-><init>(Landroid/support/v4/app/ActivityOptionsCompatJB;)V

    #@f
    .line 110
    :goto_0
    return-object v0

    #@10
    :cond_0
    new-instance v0, Landroid/support/v4/app/ActivityOptionsCompat;

    #@12
    invoke-direct {v0}, Landroid/support/v4/app/ActivityOptionsCompat;-><init>()V

    #@15
    goto :goto_0
.end method


# virtual methods
.method public toBundle()Landroid/os/Bundle;
    .locals 1

    #@0
    .prologue
    .line 231
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public update(Landroid/support/v4/app/ActivityOptionsCompat;)V
    .locals 0
    .param p1, "otherOptions"    # Landroid/support/v4/app/ActivityOptionsCompat;

    #@0
    .prologue
    .line 241
    return-void
.end method
