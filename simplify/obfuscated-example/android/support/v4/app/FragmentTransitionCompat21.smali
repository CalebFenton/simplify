.class Landroid/support/v4/app/FragmentTransitionCompat21;
.super Ljava/lang/Object;
.source "FragmentTransitionCompat21.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;,
        Landroid/support/v4/app/FragmentTransitionCompat21$ViewRetriever;
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 34
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 327
    return-void
.end method

.method static synthetic access$000(Ljava/util/ArrayList;Landroid/view/View;)V
    .locals 0
    .param p0, "x0"    # Ljava/util/ArrayList;
    .param p1, "x1"    # Landroid/view/View;

    #@0
    .prologue
    .line 34
    invoke-static {p0, p1}, Landroid/support/v4/app/FragmentTransitionCompat21;->captureTransitioningViews(Ljava/util/ArrayList;Landroid/view/View;)V

    #@3
    return-void
.end method

.method static synthetic access$100(Landroid/view/View;)Landroid/graphics/Rect;
    .locals 1
    .param p0, "x0"    # Landroid/view/View;

    #@0
    .prologue
    .line 34
    invoke-static {p0}, Landroid/support/v4/app/FragmentTransitionCompat21;->getBoundsOnScreen(Landroid/view/View;)Landroid/graphics/Rect;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static addTargets(Ljava/lang/Object;Ljava/util/ArrayList;)V
    .locals 4
    .param p0, "transitionObject"    # Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Object;",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 316
    .local p1, "views":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    move-object v2, p0

    #@1
    check-cast v2, Landroid/transition/Transition;

    #@3
    .line 317
    .local v2, "transition":Landroid/transition/Transition;
    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    #@6
    move-result v1

    #@7
    .line 318
    .local v1, "numViews":I
    const/4 v0, 0x0

    #@8
    .local v0, "i":I
    :goto_0
    if-ge v0, v1, :cond_0

    #@a
    .line 319
    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@d
    move-result-object v3

    #@e
    check-cast v3, Landroid/view/View;

    #@10
    invoke-virtual {v2, v3}, Landroid/transition/Transition;->addTarget(Landroid/view/View;)Landroid/transition/Transition;

    #@13
    .line 318
    add-int/lit8 v0, v0, 0x1

    #@15
    goto :goto_0

    #@16
    .line 321
    :cond_0
    return-void
.end method

.method public static addTransitionTargets(Ljava/lang/Object;Ljava/lang/Object;Landroid/view/View;Landroid/support/v4/app/FragmentTransitionCompat21$ViewRetriever;Landroid/view/View;Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;Ljava/util/Map;Ljava/util/ArrayList;Ljava/util/Map;Ljava/util/ArrayList;)V
    .locals 10
    .param p0, "enterTransitionObject"    # Ljava/lang/Object;
    .param p1, "sharedElementTransitionObject"    # Ljava/lang/Object;
    .param p2, "container"    # Landroid/view/View;
    .param p3, "inFragment"    # Landroid/support/v4/app/FragmentTransitionCompat21$ViewRetriever;
    .param p4, "nonExistentView"    # Landroid/view/View;
    .param p5, "epicenterView"    # Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Object;",
            "Ljava/lang/Object;",
            "Landroid/view/View;",
            "Landroid/support/v4/app/FragmentTransitionCompat21$ViewRetriever;",
            "Landroid/view/View;",
            "Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;",
            "Ljava/util/Map",
            "<",
            "Ljava/lang/String;",
            "Ljava/lang/String;",
            ">;",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;",
            "Ljava/util/Map",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 101
    .local p6, "nameOverrides":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Ljava/lang/String;>;"
    .local p7, "enteringViews":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    .local p8, "renamedViews":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Landroid/view/View;>;"
    .local p9, "sharedElementTargets":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    if-nez p0, :cond_0

    #@2
    if-eqz p1, :cond_4

    #@4
    :cond_0
    move-object v6, p0

    #@5
    .line 102
    check-cast v6, Landroid/transition/Transition;

    #@7
    .line 103
    .local v6, "enterTransition":Landroid/transition/Transition;
    if-eqz v6, :cond_1

    #@9
    .line 104
    invoke-virtual {v6, p4}, Landroid/transition/Transition;->addTarget(Landroid/view/View;)Landroid/transition/Transition;

    #@c
    .line 106
    :cond_1
    if-eqz p1, :cond_2

    #@e
    move-object v8, p1

    #@f
    .line 107
    check-cast v8, Landroid/transition/Transition;

    #@11
    .line 108
    .local v8, "sharedElementTransition":Landroid/transition/Transition;
    move-object/from16 v0, p9

    #@13
    invoke-static {v8, v0}, Landroid/support/v4/app/FragmentTransitionCompat21;->addTargets(Ljava/lang/Object;Ljava/util/ArrayList;)V

    #@16
    .line 111
    .end local v8    # "sharedElementTransition":Landroid/transition/Transition;
    :cond_2
    if-eqz p3, :cond_3

    #@18
    .line 112
    invoke-virtual {p2}, Landroid/view/View;->getViewTreeObserver()Landroid/view/ViewTreeObserver;

    #@1b
    move-result-object v9

    #@1c
    new-instance v1, Landroid/support/v4/app/FragmentTransitionCompat21$2;

    #@1e
    move-object v2, p2

    #@1f
    move-object v3, p3

    #@20
    move-object/from16 v4, p6

    #@22
    move-object/from16 v5, p8

    #@24
    move-object/from16 v7, p7

    #@26
    invoke-direct/range {v1 .. v7}, Landroid/support/v4/app/FragmentTransitionCompat21$2;-><init>(Landroid/view/View;Landroid/support/v4/app/FragmentTransitionCompat21$ViewRetriever;Ljava/util/Map;Ljava/util/Map;Landroid/transition/Transition;Ljava/util/ArrayList;)V

    #@29
    invoke-virtual {v9, v1}, Landroid/view/ViewTreeObserver;->addOnPreDrawListener(Landroid/view/ViewTreeObserver$OnPreDrawListener;)V

    #@2c
    .line 140
    :cond_3
    invoke-static {v6, p5}, Landroid/support/v4/app/FragmentTransitionCompat21;->setSharedElementEpicenter(Landroid/transition/Transition;Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;)V

    #@2f
    .line 142
    .end local v6    # "enterTransition":Landroid/transition/Transition;
    :cond_4
    return-void
.end method

.method public static beginDelayedTransition(Landroid/view/ViewGroup;Ljava/lang/Object;)V
    .locals 1
    .param p0, "sceneRoot"    # Landroid/view/ViewGroup;
    .param p1, "transitionObject"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 68
    move-object v0, p1

    #@1
    check-cast v0, Landroid/transition/Transition;

    #@3
    .line 69
    .local v0, "transition":Landroid/transition/Transition;
    invoke-static {p0, v0}, Landroid/transition/TransitionManager;->beginDelayedTransition(Landroid/view/ViewGroup;Landroid/transition/Transition;)V

    #@6
    .line 70
    return-void
.end method

.method public static captureExitingViews(Ljava/lang/Object;Landroid/view/View;Ljava/util/ArrayList;Ljava/util/Map;)Ljava/lang/Object;
    .locals 1
    .param p0, "exitTransition"    # Ljava/lang/Object;
    .param p1, "root"    # Landroid/view/View;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Object;",
            "Landroid/view/View;",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;",
            "Ljava/util/Map",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;)",
            "Ljava/lang/Object;"
        }
    .end annotation

    #@0
    .prologue
    .line 48
    .local p2, "viewList":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    .local p3, "namedViews":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Landroid/view/View;>;"
    if-eqz p0, :cond_1

    #@2
    .line 49
    invoke-static {p2, p1}, Landroid/support/v4/app/FragmentTransitionCompat21;->captureTransitioningViews(Ljava/util/ArrayList;Landroid/view/View;)V

    #@5
    .line 50
    if-eqz p3, :cond_0

    #@7
    .line 51
    invoke-interface {p3}, Ljava/util/Map;->values()Ljava/util/Collection;

    #@a
    move-result-object v0

    #@b
    invoke-virtual {p2, v0}, Ljava/util/ArrayList;->removeAll(Ljava/util/Collection;)Z

    #@e
    .line 53
    :cond_0
    invoke-virtual {p2}, Ljava/util/ArrayList;->isEmpty()Z

    #@11
    move-result v0

    #@12
    if-eqz v0, :cond_2

    #@14
    .line 54
    const/4 p0, 0x0

    #@15
    .line 59
    .end local p0    # "exitTransition":Ljava/lang/Object;
    :cond_1
    :goto_0
    return-object p0

    #@16
    .restart local p0    # "exitTransition":Ljava/lang/Object;
    :cond_2
    move-object v0, p0

    #@17
    .line 56
    check-cast v0, Landroid/transition/Transition;

    #@19
    invoke-static {v0, p2}, Landroid/support/v4/app/FragmentTransitionCompat21;->addTargets(Ljava/lang/Object;Ljava/util/ArrayList;)V

    #@1c
    goto :goto_0
.end method

.method private static captureTransitioningViews(Ljava/util/ArrayList;Landroid/view/View;)V
    .locals 5
    .param p1, "view"    # Landroid/view/View;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;",
            "Landroid/view/View;",
            ")V"
        }
    .end annotation

    #@0
    .prologue
    .line 231
    .local p0, "transitioningViews":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    invoke-virtual {p1}, Landroid/view/View;->getVisibility()I

    #@3
    move-result v4

    #@4
    if-nez v4, :cond_0

    #@6
    .line 232
    instance-of v4, p1, Landroid/view/ViewGroup;

    #@8
    if-eqz v4, :cond_2

    #@a
    move-object v3, p1

    #@b
    .line 233
    check-cast v3, Landroid/view/ViewGroup;

    #@d
    .line 234
    .local v3, "viewGroup":Landroid/view/ViewGroup;
    invoke-virtual {v3}, Landroid/view/ViewGroup;->isTransitionGroup()Z

    #@10
    move-result v4

    #@11
    if-eqz v4, :cond_1

    #@13
    .line 235
    invoke-virtual {p0, v3}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@16
    .line 247
    .end local v3    # "viewGroup":Landroid/view/ViewGroup;
    :cond_0
    :goto_0
    return-void

    #@17
    .line 237
    .restart local v3    # "viewGroup":Landroid/view/ViewGroup;
    :cond_1
    invoke-virtual {v3}, Landroid/view/ViewGroup;->getChildCount()I

    #@1a
    move-result v1

    #@1b
    .line 238
    .local v1, "count":I
    const/4 v2, 0x0

    #@1c
    .local v2, "i":I
    :goto_1
    if-ge v2, v1, :cond_0

    #@1e
    .line 239
    invoke-virtual {v3, v2}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    #@21
    move-result-object v0

    #@22
    .line 240
    .local v0, "child":Landroid/view/View;
    invoke-static {p0, v0}, Landroid/support/v4/app/FragmentTransitionCompat21;->captureTransitioningViews(Ljava/util/ArrayList;Landroid/view/View;)V

    #@25
    .line 238
    add-int/lit8 v2, v2, 0x1

    #@27
    goto :goto_1

    #@28
    .line 244
    .end local v0    # "child":Landroid/view/View;
    .end local v1    # "count":I
    .end local v2    # "i":I
    .end local v3    # "viewGroup":Landroid/view/ViewGroup;
    :cond_2
    invoke-virtual {p0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@2b
    goto :goto_0
.end method

.method public static cleanupTransitions(Landroid/view/View;Landroid/view/View;Ljava/lang/Object;Ljava/util/ArrayList;Ljava/lang/Object;Ljava/util/ArrayList;Ljava/lang/Object;Ljava/util/ArrayList;Ljava/lang/Object;Ljava/util/ArrayList;Ljava/util/Map;)V
    .locals 13
    .param p0, "sceneRoot"    # Landroid/view/View;
    .param p1, "nonExistentView"    # Landroid/view/View;
    .param p2, "enterTransitionObject"    # Ljava/lang/Object;
    .param p4, "exitTransitionObject"    # Ljava/lang/Object;
    .param p6, "sharedElementTransitionObject"    # Ljava/lang/Object;
    .param p8, "overallTransitionObject"    # Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/view/View;",
            "Landroid/view/View;",
            "Ljava/lang/Object;",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;",
            "Ljava/lang/Object;",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;",
            "Ljava/lang/Object;",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;",
            "Ljava/lang/Object;",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;",
            "Ljava/util/Map",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 272
    .local p3, "enteringViews":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    .local p5, "exitingViews":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    .local p7, "sharedElementTargets":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    .local p9, "hiddenViews":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    .local p10, "renamedViews":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Landroid/view/View;>;"
    move-object v2, p2

    #@1
    check-cast v2, Landroid/transition/Transition;

    #@3
    .local v2, "enterTransition":Landroid/transition/Transition;
    move-object/from16 v5, p4

    #@5
    .line 273
    check-cast v5, Landroid/transition/Transition;

    #@7
    .local v5, "exitTransition":Landroid/transition/Transition;
    move-object/from16 v7, p6

    #@9
    .line 274
    check-cast v7, Landroid/transition/Transition;

    #@b
    .local v7, "sharedElementTransition":Landroid/transition/Transition;
    move-object/from16 v11, p8

    #@d
    .line 275
    check-cast v11, Landroid/transition/Transition;

    #@f
    .line 276
    .local v11, "overallTransition":Landroid/transition/Transition;
    if-eqz v11, :cond_0

    #@11
    .line 277
    invoke-virtual {p0}, Landroid/view/View;->getViewTreeObserver()Landroid/view/ViewTreeObserver;

    #@14
    move-result-object v12

    #@15
    new-instance v0, Landroid/support/v4/app/FragmentTransitionCompat21$4;

    #@17
    move-object v1, p0

    #@18
    move-object v3, p1

    #@19
    move-object/from16 v4, p3

    #@1b
    move-object/from16 v6, p5

    #@1d
    move-object/from16 v8, p7

    #@1f
    move-object/from16 v9, p10

    #@21
    move-object/from16 v10, p9

    #@23
    invoke-direct/range {v0 .. v11}, Landroid/support/v4/app/FragmentTransitionCompat21$4;-><init>(Landroid/view/View;Landroid/transition/Transition;Landroid/view/View;Ljava/util/ArrayList;Landroid/transition/Transition;Ljava/util/ArrayList;Landroid/transition/Transition;Ljava/util/ArrayList;Ljava/util/Map;Ljava/util/ArrayList;Landroid/transition/Transition;)V

    #@26
    invoke-virtual {v12, v0}, Landroid/view/ViewTreeObserver;->addOnPreDrawListener(Landroid/view/ViewTreeObserver$OnPreDrawListener;)V

    #@29
    .line 305
    :cond_0
    return-void
.end method

.method public static cloneTransition(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 0
    .param p0, "transition"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 40
    if-eqz p0, :cond_0

    #@2
    .line 41
    check-cast p0, Landroid/transition/Transition;

    #@4
    .end local p0    # "transition":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/transition/Transition;->clone()Landroid/transition/Transition;

    #@7
    move-result-object p0

    #@8
    .line 43
    :cond_0
    return-object p0
.end method

.method public static excludeTarget(Ljava/lang/Object;Landroid/view/View;Z)V
    .locals 1
    .param p0, "transitionObject"    # Ljava/lang/Object;
    .param p1, "view"    # Landroid/view/View;
    .param p2, "exclude"    # Z

    #@0
    .prologue
    .line 63
    move-object v0, p0

    #@1
    check-cast v0, Landroid/transition/Transition;

    #@3
    .line 64
    .local v0, "transition":Landroid/transition/Transition;
    invoke-virtual {v0, p1, p2}, Landroid/transition/Transition;->excludeTarget(Landroid/view/View;Z)Landroid/transition/Transition;

    #@6
    .line 65
    return-void
.end method

.method public static findNamedViews(Ljava/util/Map;Landroid/view/View;)V
    .locals 6
    .param p1, "view"    # Landroid/view/View;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/Map",
            "<",
            "Ljava/lang/String;",
            "Landroid/view/View;",
            ">;",
            "Landroid/view/View;",
            ")V"
        }
    .end annotation

    #@0
    .prologue
    .line 250
    .local p0, "namedViews":Ljava/util/Map;, "Ljava/util/Map<Ljava/lang/String;Landroid/view/View;>;"
    invoke-virtual {p1}, Landroid/view/View;->getVisibility()I

    #@3
    move-result v5

    #@4
    if-nez v5, :cond_1

    #@6
    .line 251
    invoke-virtual {p1}, Landroid/view/View;->getTransitionName()Ljava/lang/String;

    #@9
    move-result-object v3

    #@a
    .line 252
    .local v3, "transitionName":Ljava/lang/String;
    if-eqz v3, :cond_0

    #@c
    .line 253
    invoke-interface {p0, v3, p1}, Ljava/util/Map;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@f
    .line 255
    :cond_0
    instance-of v5, p1, Landroid/view/ViewGroup;

    #@11
    if-eqz v5, :cond_1

    #@13
    move-object v4, p1

    #@14
    .line 256
    check-cast v4, Landroid/view/ViewGroup;

    #@16
    .line 257
    .local v4, "viewGroup":Landroid/view/ViewGroup;
    invoke-virtual {v4}, Landroid/view/ViewGroup;->getChildCount()I

    #@19
    move-result v1

    #@1a
    .line 258
    .local v1, "count":I
    const/4 v2, 0x0

    #@1b
    .local v2, "i":I
    :goto_0
    if-ge v2, v1, :cond_1

    #@1d
    .line 259
    invoke-virtual {v4, v2}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    #@20
    move-result-object v0

    #@21
    .line 260
    .local v0, "child":Landroid/view/View;
    invoke-static {p0, v0}, Landroid/support/v4/app/FragmentTransitionCompat21;->findNamedViews(Ljava/util/Map;Landroid/view/View;)V

    #@24
    .line 258
    add-int/lit8 v2, v2, 0x1

    #@26
    goto :goto_0

    #@27
    .line 264
    .end local v0    # "child":Landroid/view/View;
    .end local v1    # "count":I
    .end local v2    # "i":I
    .end local v3    # "transitionName":Ljava/lang/String;
    .end local v4    # "viewGroup":Landroid/view/ViewGroup;
    :cond_1
    return-void
.end method

.method private static getBoundsOnScreen(Landroid/view/View;)Landroid/graphics/Rect;
    .locals 7
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v6, 0x1

    #@1
    const/4 v4, 0x0

    #@2
    .line 222
    new-instance v0, Landroid/graphics/Rect;

    #@4
    invoke-direct {v0}, Landroid/graphics/Rect;-><init>()V

    #@7
    .line 223
    .local v0, "epicenter":Landroid/graphics/Rect;
    const/4 v2, 0x2

    #@8
    new-array v1, v2, [I

    #@a
    .line 224
    .local v1, "loc":[I
    invoke-virtual {p0, v1}, Landroid/view/View;->getLocationOnScreen([I)V

    #@d
    .line 226
    aget v2, v1, v4

    #@f
    aget v3, v1, v6

    #@11
    aget v4, v1, v4

    #@13
    invoke-virtual {p0}, Landroid/view/View;->getWidth()I

    #@16
    move-result v5

    #@17
    add-int/2addr v4, v5

    #@18
    aget v5, v1, v6

    #@1a
    invoke-virtual {p0}, Landroid/view/View;->getHeight()I

    #@1d
    move-result v6

    #@1e
    add-int/2addr v5, v6

    #@1f
    invoke-virtual {v0, v2, v3, v4, v5}, Landroid/graphics/Rect;->set(IIII)V

    #@22
    .line 227
    return-object v0
.end method

.method public static getTransitionName(Landroid/view/View;)Ljava/lang/String;
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 36
    invoke-virtual {p0}, Landroid/view/View;->getTransitionName()Ljava/lang/String;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static mergeTransitions(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Z)Ljava/lang/Object;
    .locals 10
    .param p0, "enterTransitionObject"    # Ljava/lang/Object;
    .param p1, "exitTransitionObject"    # Ljava/lang/Object;
    .param p2, "sharedElementTransitionObject"    # Ljava/lang/Object;
    .param p3, "allowOverlap"    # Z

    #@0
    .prologue
    .line 147
    const/4 v2, 0x1

    #@1
    .local v2, "overlap":Z
    move-object v0, p0

    #@2
    .line 148
    check-cast v0, Landroid/transition/Transition;

    #@4
    .local v0, "enterTransition":Landroid/transition/Transition;
    move-object v1, p1

    #@5
    .line 149
    check-cast v1, Landroid/transition/Transition;

    #@7
    .local v1, "exitTransition":Landroid/transition/Transition;
    move-object v3, p2

    #@8
    .line 150
    check-cast v3, Landroid/transition/Transition;

    #@a
    .line 152
    .local v3, "sharedElementTransition":Landroid/transition/Transition;
    if-eqz v0, :cond_0

    #@c
    if-eqz v1, :cond_0

    #@e
    .line 153
    move v2, p3

    #@f
    .line 161
    :cond_0
    if-eqz v2, :cond_4

    #@11
    .line 163
    new-instance v7, Landroid/transition/TransitionSet;

    #@13
    invoke-direct {v7}, Landroid/transition/TransitionSet;-><init>()V

    #@16
    .line 164
    .local v7, "transitionSet":Landroid/transition/TransitionSet;
    if-eqz v0, :cond_1

    #@18
    .line 165
    invoke-virtual {v7, v0}, Landroid/transition/TransitionSet;->addTransition(Landroid/transition/Transition;)Landroid/transition/TransitionSet;

    #@1b
    .line 167
    :cond_1
    if-eqz v1, :cond_2

    #@1d
    .line 168
    invoke-virtual {v7, v1}, Landroid/transition/TransitionSet;->addTransition(Landroid/transition/Transition;)Landroid/transition/TransitionSet;

    #@20
    .line 170
    :cond_2
    if-eqz v3, :cond_3

    #@22
    .line 171
    invoke-virtual {v7, v3}, Landroid/transition/TransitionSet;->addTransition(Landroid/transition/Transition;)Landroid/transition/TransitionSet;

    #@25
    .line 173
    :cond_3
    move-object v6, v7

    #@26
    .line 199
    .end local v7    # "transitionSet":Landroid/transition/TransitionSet;
    .local v6, "transition":Landroid/transition/Transition;
    :goto_0
    return-object v6

    #@27
    .line 177
    .end local v6    # "transition":Landroid/transition/Transition;
    :cond_4
    const/4 v4, 0x0

    #@28
    .line 178
    .local v4, "staggered":Landroid/transition/Transition;
    if-eqz v1, :cond_7

    #@2a
    if-eqz v0, :cond_7

    #@2c
    .line 179
    new-instance v8, Landroid/transition/TransitionSet;

    #@2e
    invoke-direct {v8}, Landroid/transition/TransitionSet;-><init>()V

    #@31
    invoke-virtual {v8, v1}, Landroid/transition/TransitionSet;->addTransition(Landroid/transition/Transition;)Landroid/transition/TransitionSet;

    #@34
    move-result-object v8

    #@35
    invoke-virtual {v8, v0}, Landroid/transition/TransitionSet;->addTransition(Landroid/transition/Transition;)Landroid/transition/TransitionSet;

    #@38
    move-result-object v8

    #@39
    const/4 v9, 0x1

    #@3a
    invoke-virtual {v8, v9}, Landroid/transition/TransitionSet;->setOrdering(I)Landroid/transition/TransitionSet;

    #@3d
    move-result-object v4

    #@3e
    .line 188
    :cond_5
    :goto_1
    if-eqz v3, :cond_9

    #@40
    .line 189
    new-instance v5, Landroid/transition/TransitionSet;

    #@42
    invoke-direct {v5}, Landroid/transition/TransitionSet;-><init>()V

    #@45
    .line 190
    .local v5, "together":Landroid/transition/TransitionSet;
    if-eqz v4, :cond_6

    #@47
    .line 191
    invoke-virtual {v5, v4}, Landroid/transition/TransitionSet;->addTransition(Landroid/transition/Transition;)Landroid/transition/TransitionSet;

    #@4a
    .line 193
    :cond_6
    invoke-virtual {v5, v3}, Landroid/transition/TransitionSet;->addTransition(Landroid/transition/Transition;)Landroid/transition/TransitionSet;

    #@4d
    .line 194
    move-object v6, v5

    #@4e
    .line 195
    .restart local v6    # "transition":Landroid/transition/Transition;
    goto :goto_0

    #@4f
    .line 183
    .end local v5    # "together":Landroid/transition/TransitionSet;
    .end local v6    # "transition":Landroid/transition/Transition;
    :cond_7
    if-eqz v1, :cond_8

    #@51
    .line 184
    move-object v4, v1

    #@52
    goto :goto_1

    #@53
    .line 185
    :cond_8
    if-eqz v0, :cond_5

    #@55
    .line 186
    move-object v4, v0

    #@56
    goto :goto_1

    #@57
    .line 196
    :cond_9
    move-object v6, v4

    #@58
    .restart local v6    # "transition":Landroid/transition/Transition;
    goto :goto_0
.end method

.method public static removeTargets(Ljava/lang/Object;Ljava/util/ArrayList;)V
    .locals 4
    .param p0, "transitionObject"    # Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Object;",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 308
    .local p1, "views":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/view/View;>;"
    move-object v2, p0

    #@1
    check-cast v2, Landroid/transition/Transition;

    #@3
    .line 309
    .local v2, "transition":Landroid/transition/Transition;
    invoke-virtual {p1}, Ljava/util/ArrayList;->size()I

    #@6
    move-result v1

    #@7
    .line 310
    .local v1, "numViews":I
    const/4 v0, 0x0

    #@8
    .local v0, "i":I
    :goto_0
    if-ge v0, v1, :cond_0

    #@a
    .line 311
    invoke-virtual {p1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@d
    move-result-object v3

    #@e
    check-cast v3, Landroid/view/View;

    #@10
    invoke-virtual {v2, v3}, Landroid/transition/Transition;->removeTarget(Landroid/view/View;)Landroid/transition/Transition;

    #@13
    .line 310
    add-int/lit8 v0, v0, 0x1

    #@15
    goto :goto_0

    #@16
    .line 313
    :cond_0
    return-void
.end method

.method public static setEpicenter(Ljava/lang/Object;Landroid/view/View;)V
    .locals 3
    .param p0, "transitionObject"    # Ljava/lang/Object;
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 73
    move-object v1, p0

    #@1
    check-cast v1, Landroid/transition/Transition;

    #@3
    .line 74
    .local v1, "transition":Landroid/transition/Transition;
    invoke-static {p1}, Landroid/support/v4/app/FragmentTransitionCompat21;->getBoundsOnScreen(Landroid/view/View;)Landroid/graphics/Rect;

    #@6
    move-result-object v0

    #@7
    .line 76
    .local v0, "epicenter":Landroid/graphics/Rect;
    new-instance v2, Landroid/support/v4/app/FragmentTransitionCompat21$1;

    #@9
    invoke-direct {v2, v0}, Landroid/support/v4/app/FragmentTransitionCompat21$1;-><init>(Landroid/graphics/Rect;)V

    #@c
    invoke-virtual {v1, v2}, Landroid/transition/Transition;->setEpicenterCallback(Landroid/transition/Transition$EpicenterCallback;)V

    #@f
    .line 82
    return-void
.end method

.method private static setSharedElementEpicenter(Landroid/transition/Transition;Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;)V
    .locals 1
    .param p0, "transition"    # Landroid/transition/Transition;
    .param p1, "epicenterView"    # Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;

    #@0
    .prologue
    .line 206
    if-eqz p0, :cond_0

    #@2
    .line 207
    new-instance v0, Landroid/support/v4/app/FragmentTransitionCompat21$3;

    #@4
    invoke-direct {v0, p1}, Landroid/support/v4/app/FragmentTransitionCompat21$3;-><init>(Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;)V

    #@7
    invoke-virtual {p0, v0}, Landroid/transition/Transition;->setEpicenterCallback(Landroid/transition/Transition$EpicenterCallback;)V

    #@a
    .line 219
    :cond_0
    return-void
.end method
