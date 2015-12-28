.class Landroid/support/v4/app/ActivityOptionsCompat21;
.super Ljava/lang/Object;
.source "ActivityOptionsCompat21.java"


# instance fields
.field private final mActivityOptions:Landroid/app/ActivityOptions;


# direct methods
.method private constructor <init>(Landroid/app/ActivityOptions;)V
    .locals 0
    .param p1, "activityOptions"    # Landroid/app/ActivityOptions;

    #@0
    .prologue
    .line 49
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 50
    iput-object p1, p0, Landroid/support/v4/app/ActivityOptionsCompat21;->mActivityOptions:Landroid/app/ActivityOptions;

    #@5
    .line 51
    return-void
.end method

.method public static makeSceneTransitionAnimation(Landroid/app/Activity;Landroid/view/View;Ljava/lang/String;)Landroid/support/v4/app/ActivityOptionsCompat21;
    .locals 2
    .param p0, "activity"    # Landroid/app/Activity;
    .param p1, "sharedElement"    # Landroid/view/View;
    .param p2, "sharedElementName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 31
    new-instance v0, Landroid/support/v4/app/ActivityOptionsCompat21;

    #@2
    invoke-static {p0, p1, p2}, Landroid/app/ActivityOptions;->makeSceneTransitionAnimation(Landroid/app/Activity;Landroid/view/View;Ljava/lang/String;)Landroid/app/ActivityOptions;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1}, Landroid/support/v4/app/ActivityOptionsCompat21;-><init>(Landroid/app/ActivityOptions;)V

    #@9
    return-object v0
.end method

.method public static makeSceneTransitionAnimation(Landroid/app/Activity;[Landroid/view/View;[Ljava/lang/String;)Landroid/support/v4/app/ActivityOptionsCompat21;
    .locals 4
    .param p0, "activity"    # Landroid/app/Activity;
    .param p1, "sharedElements"    # [Landroid/view/View;
    .param p2, "sharedElementNames"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 38
    const/4 v1, 0x0

    #@1
    .line 39
    .local v1, "pairs":[Landroid/util/Pair;
    if-eqz p1, :cond_0

    #@3
    .line 40
    array-length v2, p1

    #@4
    new-array v1, v2, [Landroid/util/Pair;

    #@6
    .line 41
    const/4 v0, 0x0

    #@7
    .local v0, "i":I
    :goto_0
    array-length v2, v1

    #@8
    if-ge v0, v2, :cond_0

    #@a
    .line 42
    aget-object v2, p1, v0

    #@c
    aget-object v3, p2, v0

    #@e
    invoke-static {v2, v3}, Landroid/util/Pair;->create(Ljava/lang/Object;Ljava/lang/Object;)Landroid/util/Pair;

    #@11
    move-result-object v2

    #@12
    aput-object v2, v1, v0

    #@14
    .line 41
    add-int/lit8 v0, v0, 0x1

    #@16
    goto :goto_0

    #@17
    .line 45
    .end local v0    # "i":I
    :cond_0
    new-instance v2, Landroid/support/v4/app/ActivityOptionsCompat21;

    #@19
    invoke-static {p0, v1}, Landroid/app/ActivityOptions;->makeSceneTransitionAnimation(Landroid/app/Activity;[Landroid/util/Pair;)Landroid/app/ActivityOptions;

    #@1c
    move-result-object v3

    #@1d
    invoke-direct {v2, v3}, Landroid/support/v4/app/ActivityOptionsCompat21;-><init>(Landroid/app/ActivityOptions;)V

    #@20
    return-object v2
.end method


# virtual methods
.method public toBundle()Landroid/os/Bundle;
    .locals 1

    #@0
    .prologue
    .line 54
    iget-object v0, p0, Landroid/support/v4/app/ActivityOptionsCompat21;->mActivityOptions:Landroid/app/ActivityOptions;

    #@2
    invoke-virtual {v0}, Landroid/app/ActivityOptions;->toBundle()Landroid/os/Bundle;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public update(Landroid/support/v4/app/ActivityOptionsCompat21;)V
    .locals 2
    .param p1, "otherOptions"    # Landroid/support/v4/app/ActivityOptionsCompat21;

    #@0
    .prologue
    .line 58
    iget-object v0, p0, Landroid/support/v4/app/ActivityOptionsCompat21;->mActivityOptions:Landroid/app/ActivityOptions;

    #@2
    iget-object v1, p1, Landroid/support/v4/app/ActivityOptionsCompat21;->mActivityOptions:Landroid/app/ActivityOptions;

    #@4
    invoke-virtual {v0, v1}, Landroid/app/ActivityOptions;->update(Landroid/app/ActivityOptions;)V

    #@7
    .line 59
    return-void
.end method
